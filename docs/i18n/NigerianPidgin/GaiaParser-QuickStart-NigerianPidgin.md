# GaiaParser — Quick Start

Turn GS1 barcode payload into data wey get structure, wey dem validate, and wey human being
fit read — for like ten minutes. Dis one na di short road; **[GaiaParser Developer
Guide](GaiaParser-NigerianPidgin.md)** na di complete reference, and **[GaiaBuilder](GaiaBuilder-NigerianPidgin.md)** dey
cover di opposite direction (building element string and Digital Link URI).

## Wetin Dey Inside

1. [Add Gaia to your project](#1-add-gaia-to-your-project)
2. [Parse something](#2-parse-something)
3. [Read di result](#3-read-di-result)
4. [Handle parse wey fail](#4-handle-parse-wey-fail)
5. [Two things wey go trip you](#5-two-things-wey-go-trip-you)
6. [Scanner prefix and Digital Link just dey work](#6-scanner-prefix-and-digital-link-just-dey-work)
7. [Do less work: di parse modes](#7-do-less-work-di-parse-modes)
8. [Change di language and di date format](#8-change-di-language-and-di-date-format)
9. [Clean up input wey scatter](#9-clean-up-input-wey-scatter)
10. [Where to go from here](#10-where-to-go-from-here)

---

## 1. Add Gaia to your project

Gaia no dey published for Maven Central, so build di core once and install am into your local
repository:

```bash
cd gaia && mvn install
```

Then depend on am:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Na dat one be di only dependency wey you go write. Di jar dey slim: di only compile-scope
dependency wey Gaia get — `com.fasterxml.jackson.core:jackson-databind` — dey come transitively;
and if your build don already pin one Jackson version, na dat pin go win and na am Gaia go use.
Gaia dey target **Java 11**, and di same jar dey run unchanged on every JVM release wey follow.

> To skip di core test suite (`mvn install -DskipTests`) dey turn several minutes into several
> seconds while you still dey start.

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

`parse(String)` dey run di **full** pipeline: syntax, content validation, interpretation. Na di
correct default — narrow am later when you get reason wey you don measure.

---

## 3. Read di result

`ParseResult.getAiObject()` dey hold di AI wey dem resolve. Grab specific AI by im code, no be
by im position:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Every element dey carry **interpretation** list — di meaning wey dem unpack from behind di raw
digits, wey di interpretation stage produce:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` don dey localized and na for display e dey. But to *read* value inside code, look
am up by im stable type key instead:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Different AI dey produce different keys — GTIN dey give im company prefix, im GTIN type, and im
check digit; price dey give currency and decimal amount. Di full list dey for
[Appendix B](GaiaParser-NigerianPidgin.md#appendix-b--interpretation-key-constants), and di constants dey
inside `GS1Constants_Enricher`. No be every AI get interpretation: batch/lot number na free
text wey nothing dey to derive from am, so im list dey empty.

---

## 4. Handle parse wey fail

Payload wey no valid na normal result, e no be exception — `parse` no dey ever throw because of
GS1 data wey bad:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Branch on `getId()`, never on di message.** Di messages dey localized and dia wording no be
contract — plus dem currently dey carry one quoting defect wey dem sabi (di doubled `''` wey
dey up dia), wey [Error Reference](GaiaParser-NigerianPidgin.md#error-reference) note.

Two different questions, two different methods:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Di parse dey stop going deeper once one stage fail, so check digit wey bad mean say you go
collect validation errors but no interpretation at all.

### Warning no dey make di result invalid

Some checks na advisory. GS1 company prefix wey dem no recognise dey reported, but di payload
still dey correct:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Use `getIssues()` when you want di two together. If your workflow gats reject prefix wey dem no
recognise, check `getWarnings()` explicitly — `isValid()` no go do am for you.

---

## 5. Two things wey go trip you

### Di GS separator, and why to leave am out worse pass error

Variable-length AI dey run reach di **GS character** (ASCII `0x1D`, wey dem dey call FNC1 for
barcode symbologies) or reach di end of di string. When another AI dey follow am, dat separator
na must:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

If you leave am out, you go **not** collect error — na answer wey wrong but wey full of
confidence you go collect:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` na `X..20`, so to swallow di whole `LOT-ABC21SN-98765` make sense well well, and di
parser no get any way to sabi say na no be wetin you mean. Nothing wey dey downstream fit
recover dis one, so make di separator correct from di source: read di scanner bytes as
**ISO-8859-1** so dat `0x1D` go survive, and write `""` for Java string literals. Fixed-length
AI (`01`, `17`, `3103`) no need separator — di parser sabi dia length.

### Most AI no dey stand alone

Batch/lot, serial number, expiry date and dia kind na *attributes*: di GS1 General
Specifications require say identification key must follow dem, and Gaia dey enforce am.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Add GTIN and e go pass. But if you really need to parse fragment — unit test, or partial scan —
turn di check off:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Scanner prefix and Digital Link just dey work

You no need tell Gaia which form di input dey — e dey detect di four forms. Just pass whatever
your scanner give you.

**AIM Symbology Identifier prefix** dey determine di symbology, and dem dey strip am
automatically:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** dey pass through di same validation and enrichment:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Because di two forms dey land for di same `GS1AIObject`, di code wey dey consume di scan no need
worry about which one come — and `toElementString()` / `getCanonicalDigitalLink()` dey convert
one into di other.

**Di 8-digit correlation prefix** (`12345678~…`) too dey stripped di same way and dey kept for
`getCorrelationInfo()`, if your flow dey use am.

---

## 7. Do less work: di parse modes

Di default dey do everything. Ask for less when na only part of di answer you need:

| Mode | Wetin e dey answer | Cost |
|---|---|---|
| `DATA_CARRIER` | Na which symbology be dis? | Cheapest — no AI parsing at all, `getAiObject()` na `null` |
| `SYNTAX` | Di AI codes and lengths — dem correct? | No check digit, no interpretation |
| `CONTENT` | Dis one na valid GS1 data? | Full validation, no interpretation |
| `INTERPRETATION` | E mean wetin? | **Di default** — everything |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Pick `CONTENT` when you dey validate plenty volume and you no dey ever display di breakdown,
and `DATA_CARRIER` when na only to route di scan to di correct handler you need.

---

## 8. Change di language and di date format

Dem don translate error messages, interpretation labels, and AI descriptions into **35
languages**; and you fit show dates di way wey you like. All of dem dey inside one single
`ParseConfig` wey no fit change:

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

Dem no dey ever localize values — na only labels, descriptions and messages — so `"2026-12-31"`
and `"09506000134352"` mean di same thing for every language. Build di config once for startup
and share am; e no fit change.

---

## 9. Clean up input wey scatter

If your source dey emit printed HRI brackets or space wey dey waka anyhow, di core get two
**input modifiers** wey dey fix di payload before parsing:

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

Nothing dey enabled by default, and di two get dia caveat — space and bracket na valid GS1 data
characters, so apply dem only to source wey you sabi. See
[Built-in modifiers](GaiaParser-NigerianPidgin.md#built-in-modifiers), wey also explain why to strip bracket
gats restore di separator wey dem imply.

---

## 10. Where to go from here

- **[GaiaParser Developer Guide](GaiaParser-NigerianPidgin.md)** — di pipeline for detail, di full result
  model, every error code, and di appendices of AI and interpretation keys.
- **[GaiaBuilder Developer Guide](GaiaBuilder-NigerianPidgin.md)** — build element string and Digital Link URI
  from AI/value pairs.
- **[Gaia API HTTP Reference](../../gaia-api-reference.md)** — di same engine but through HTTP, if
  you no wan embed di library.
- **[ai-codes.txt](../../ai-codes.txt)** — flat `(AI) TITLE` listing for quick lookup.

### Di five-line version

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
