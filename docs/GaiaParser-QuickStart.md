# GaiaParser — Quick Start

Parse a GS1 barcode payload into structured, validated, human-readable data in about ten
minutes. This is the short path; the **[GaiaParser Developer Guide](GaiaParser.md)** is the
complete reference, and **[GaiaBuilder](GaiaBuilder.md)** covers the inverse direction
(constructing element strings and Digital Link URIs).

## Contents

1. [Add Gaia to your project](#1-add-gaia-to-your-project)
2. [Parse something](#2-parse-something)
3. [Read the result](#3-read-the-result)
4. [Handle a failed parse](#4-handle-a-failed-parse)
5. [Two things that will bite you](#5-two-things-that-will-bite-you)
6. [Scanner prefixes and Digital Links just work](#6-scanner-prefixes-and-digital-links-just-work)
7. [Do less work: parse modes](#7-do-less-work-parse-modes)
8. [Change the language and date format](#8-change-the-language-and-date-format)
9. [Clean up messy input](#9-clean-up-messy-input)
10. [Where to go next](#10-where-to-go-next)

---

## 1. Add Gaia to your project

Gaia is not published to Maven Central, so build the core once and install it into your
local repository:

```bash
cd gaia && mvn install
```

Then depend on it:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

That is the whole dependency list you have to write. The jar is thin, so Gaia's one
compile-scope dependency — `com.fasterxml.jackson.core:jackson-databind` — arrives
transitively; if your build already pins a Jackson version, that pin wins and Gaia uses it.
Gaia targets **Java 11**, and the same jar runs unchanged on every later JVM.

> Skipping the core's test suite (`mvn install -DskipTests`) turns a few minutes into a few
> seconds while you are getting started.

---

## 2. Parse something

One class, no configuration:

```java
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.result.ParseResult;

public class Hello {

    // Reuse one parser: it is thread-safe and does its dataset loading once.
    private static final GaiaParser PARSER = new GaiaParser();

    public static void main(String[] args) {
        ParseResult result = PARSER.parse("01095060001343521726123110LOT-001");

        System.out.println("valid : " + result.isValid());
        System.out.println("HRI   : " + result.getAiObject().toHriString());

        for (GS1AIObjectElement e : result.getAiObject().getAis()) {
            System.out.printf("(%s) %-18s = %s%n", e.getAi(), e.getTitle(), e.getValue());
        }
    }
}
```

```
valid : true
HRI   : (01)09506000134352 (17)261231 (10)LOT-001
(01) GTIN               = 09506000134352
(17) USE BY or EXPIRY   = 261231
(10) BATCH/LOT          = LOT-001
```

`parse(String)` runs the **full** pipeline: syntax, content validation, and interpretation.
That is the right default — narrow it later if you measure a reason to.

---

## 3. Read the result

`ParseResult.getAiObject()` holds the resolved AIs. Reach for a specific one by code rather
than by position:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Each element carries an **interpretation** list — the decoded meaning behind the raw digits,
produced by the interpretation stage:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` is localized and meant for display. To *read* a value in code, look it up by
its stable type key instead:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Different AIs produce different keys — a GTIN yields its company prefix, GTIN type and check
digit; a price yields currency and decimal amount. The full list is
[Appendix B](GaiaParser.md#appendix-b--interpretation-key-constants), and the constants live
in `GS1Constants_Enricher`. Not every AI has interpretations: a free-text batch/lot has
nothing to derive, so its list is empty.

---

## 4. Handle a failed parse

An invalid payload is a normal outcome, not an exception — `parse` never throws for bad GS1
data:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Branch on `getId()`, never on the message.** Messages are localized and their wording is
not a contract — and they currently carry a known quoting defect (the doubled `''` above),
noted in the [Error Reference](GaiaParser.md#error-reference).

Two different questions, two different methods:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

A parse stops descending once a stage fails, so a bad check digit means you get validation
errors but no interpretations.

### Warnings do not make a result invalid

Some checks are advisory. An unrecognised GS1 company prefix is reported, but the payload is
still structurally sound:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Use `getIssues()` when you want both. If your workflow must reject unknown prefixes, check
`getWarnings()` explicitly — `isValid()` will not do it for you.

---

## 5. Two things that will bite you

### The GS separator, and why omitting it is worse than an error

A variable-length AI runs until a **GS character** (ASCII `0x1D`, called FNC1 in barcode
symbologies) or the end of the string. When another AI follows one, that separator is
mandatory:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Leave it out and you do **not** get an error — you get a confidently wrong answer:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` is `X..20`, so it legitimately swallows `LOT-ABC21SN-98765` and the parser has no
way to know that was not intended. Nothing downstream can recover this, so get the separator
right at the source: read scanner bytes as **ISO-8859-1** so `0x1D` survives, and write
`"\u001D"` in Java string literals. Fixed-length AIs (`01`, `17`, `3103`) need no separator —
the parser knows their length.

### Most AIs cannot stand alone

Batch/lot, serial, expiry and friends are *attributes*: the GS1 General Specifications
require them to travel with an identification key, and Gaia enforces it.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Add the GTIN and it passes. If you genuinely need to parse a fragment — a unit test, a
partial scan — switch the check off:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Scanner prefixes and Digital Links just work

You do not have to tell Gaia what shape the input is — it detects all four forms. Feed it
whatever the scanner gave you.

**An AIM Code ID prefix** identifies the symbology and is stripped automatically:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**A GS1 Digital Link URI** runs through the same validation and enrichment:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Because both forms land in the same `GS1AIObject`, the code that consumes a scan does not
need to care which one arrived — and `toElementString()` / `getCanonicalDigitalLink()`
convert between them.

An **8-digit correlation prefix** (`12345678~…`) is also stripped and preserved on
`getCorrelationInfo()`, if your pipeline uses one.

---

## 7. Do less work: parse modes

The default does everything. Ask for less when you only need part of the answer:

| Mode | Answers | Cost |
|---|---|---|
| `DATA_CARRIER` | Which symbology is this? | Cheapest — no AI parsing at all, `getAiObject()` is `null` |
| `SYNTAX` | Are the AI codes and lengths well-formed? | No check digits, no interpretations |
| `CONTENT` | Is this valid GS1 data? | Full validation, no interpretations |
| `INTERPRETATION` | What does it mean? | **Default** — everything |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Reach for `CONTENT` when you are validating at volume and never display the breakdown, and
for `DATA_CARRIER` when you only need to route a scan to the right handler.

---

## 8. Change the language and date format

Error messages, interpretation labels and AI descriptions are translated into **35
languages**; dates render however you like. All of it is one immutable `ParseConfig`:

```java
ParseConfig config = ParseConfig.builder()
        .language(Language.FRENCH)
        .dateEndian(DateEndian.BIG)          // yyyy/mm/dd
        .dateSeparator(DateSeparator.HYPHEN) // yyyy-mm-dd
        .build();

ParseResult r = PARSER.parse("01095060001343521726123110LOT-001", config);

r.getAiObject().get("17").getDescription();
// "Date limite d'utilisation (AAMMJJ)"
```

```
Date                     2026-12-31
Format de date           yyyy-mm-dd
```

Values are never localized — only labels, descriptions and messages — so `"2026-12-31"` and
`"09506000134352"` mean the same thing in every language. Build the config once at startup
and share it; it is immutable.

---

## 9. Clean up messy input

If your source emits printed HRI parentheses or stray spaces, two **input modifiers** ship
in the core and repair the payload before parsing:

```java
ParseConfig config = ParseConfig.builder()
        .modifier(new ModifierRemoveSpaces())        // register spaces first
        .modifier(new ModifierRemoveAIBrackets())
        .build();

ParseResult r = PARSER.parse("(01) 09506000134352 (17) 261231 (10) LOT-001", config);

r.isValid();                                     // true
r.getPayload();                                  // 01095060001343521726123110LOT-001
r.getModifierInfo().getAppliedModifiers();       // [Remove Space Characters, Remove Brackets Around AI]
```

Nothing is enabled by default, and both carry caveats — space and parentheses are legal GS1
data characters, so apply them only to a source you know. See
[Built-in modifiers](GaiaParser.md#built-in-modifiers), which also explains why bracket
removal has to reinstate the separator the brackets implied.

---

## 10. Where to go next

- **[GaiaParser Developer Guide](GaiaParser.md)** — the pipeline in detail, the full result
  model, every error code, and the AI and interpretation-key appendices.
- **[GaiaBuilder Developer Guide](GaiaBuilder.md)** — build element strings and Digital Link
  URIs from AI/value pairs.
- **[Gaia API HTTP Reference](gaia-api-reference.md)** — the same engine over HTTP, if you
  would rather not embed the library.
- **[ai-codes.txt](ai-codes.txt)** — a flat `(AI) TITLE` listing for quick lookup.

### The five-line version

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
