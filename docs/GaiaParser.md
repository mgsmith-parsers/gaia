# GAIA (GS1 Application Identifiers Analyser) — Developer Guide

## Table of Contents

1. [Overview](#overview)
2. [About GS1 and the General Specifications](#about-gs1-and-the-general-specifications)
3. [GS1 Application Identifiers](#gs1-application-identifiers)
4. [Quick Start](#quick-start)
5. [Parsing Pipeline](#parsing-pipeline)
   - [Pre-stage — Input Modifiers](#pre-stage--input-modifiers)
   - [Stage 0 — Correlation ID](#stage-0--correlation-id)
   - [Stage 1 — Input Routing](#stage-1--input-routing)
   - [Stage 2 — Syntax](#stage-2--syntax)
   - [Stage 3 — Content](#stage-3--content)
   - [Stage 4 — Interpretation](#stage-4--interpretation)
6. [Parse Configuration (`ParseConfig`)](#parse-configuration-parseconfig)
   - [Options](#options)
   - [Localized messages and labels](#localized-messages-and-labels)
   - [Date formatting](#date-formatting)
7. [Input Modifiers](#input-modifiers)
   - [Built-in modifiers](#built-in-modifiers)
   - [Writing a modifier](#writing-a-modifier)
   - [Registering modifiers](#registering-modifiers)
   - [Inspecting what a modifier did](#inspecting-what-a-modifier-did)
   - [Failure handling](#modifier-failure-handling)
8. [Parse Modes](#parse-modes)
   - [DATA_CARRIER](#data_carrier-mode)
   - [SYNTAX](#syntax-mode)
   - [CONTENT](#content-mode)
   - [INTERPRETATION](#interpretation-mode-default)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Working with Results](#working-with-results)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry and DataCarrierType](#datacarrierentry-and-datacarriertype)
12. [Error Reference](#error-reference)
13. [Thread Safety](#thread-safety)
14. [Appendix A — AI String Constants](#appendix-a--ai-string-constants)
    - [Identification and serialisation](#identification-and-serialisation)
    - [Dates and times](#dates-and-times)
    - [Quantity and measure — variable measure (metric)](#quantity-and-measure--variable-measure-metric)
    - [Quantity and measure — variable measure (imperial / US)](#quantity-and-measure--variable-measure-imperial--us)
    - [Pricing and monetary amounts](#pricing-and-monetary-amounts)
    - [Location and shipping](#location-and-shipping)
    - [Product attributes and traceability](#product-attributes-and-traceability)
    - [National Healthcare Reimbursement Numbers (NHRN)](#national-healthcare-reimbursement-numbers-nhrn)
    - [Healthcare, GMN, HIDRI, CPID, person data](#healthcare-gmn-hidri-cpid-person-data)
    - [Internal / company use](#internal--company-use)
15. [Appendix B — Interpretation Key Constants](#appendix-b--interpretation-key-constants)
    - [Date and time](#date-and-time)
    - [Harvest date](#harvest-date)
    - [GS1 Company Prefix](#gs1-company-prefix)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Country (ISO 3166)](#country-iso-3166)
    - [Currency (ISO 4217)](#currency-iso-4217)
    - [Temperature](#temperature)
    - [Sex (ISO 5218)](#sex-iso-5218)
    - [Aquatic species (FAO)](#aquatic-species-fao)
    - [NATO Stock Number (NSN)](#nato-stock-number-nsn)
    - [Roll products](#roll-products)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM identifiers (EID / ICCID)](#sim-identifiers-eid--iccid)
    - [Certification reference](#certification-reference)
    - [GS1 UIC](#gs1-uic)
    - [Baby birth sequence](#baby-birth-sequence)
    - [Global Model Number (GMN)](#global-model-number-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Decimal and measurement values](#decimal-and-measurement-values)
    - [Geo coordinates](#geo-coordinates)
    - [Production method](#production-method)
    - [AIDC media type](#aidc-media-type)
    - [Piece of total](#piece-of-total)
    - [Component splits](#component-splits)
    - [Miscellaneous](#miscellaneous)

---

## Overview

`GaiaParser` is the entry point for parsing GS1 Application Identifier (AI) element strings. It accepts raw scanner output in any of the following forms and returns a structured `ParseResult` containing all resolved AIs, validation errors, and (optionally) human-readable interpretations:

- Plain AI element string: `0109506000134352`
- Element string with AIM Code ID prefix: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Any of the above optionally prefixed by an 8-digit correlation ID: `12345678~0109506000134352`

**Entry point class:** `tools.pantheum.gaia.GaiaParser`

> **New to Gaia?** Start with the **[GaiaParser Quick Start](GaiaParser-QuickStart.md)** — dependency, first parse, and the handful of things that trip people up, in about ten minutes. This guide is the complete reference.

> For the inverse operation — *constructing* well-formed element strings and Digital Link URIs from AI/value pairs — see the **[GaiaBuilder — Developer Guide](GaiaBuilder.md)**.

---

## About GS1 and the General Specifications

**GS1** is a global not-for-profit organisation that develops and maintains open standards for supply-chain identification and data exchange. Its standards are used in retail, healthcare, logistics, foodservice, and many other industries, covering everything from product barcodes on consumer packaging to serialised tracking of pharmaceutical doses.

The authoritative reference for everything this parser implements is the **GS1 General Specifications** — a single document that defines:

- All Application Identifier (AI) codes, their data titles, formats, and validation rules
- The syntax rules for constructing and encoding AI element strings
- Barcode symbology requirements and AIM Code ID assignments
- Check digit and check character algorithms
- Two-digit year resolution (the sliding-window rule)
- Data Matrix, QR Code, GS1-128, GS1 DataBar, and other carrier specifications

The GS1 General Specifications are updated annually. The current edition and supporting resources are available at:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implements **Release 26.0 (Ratified, Jan 2026)** of the GS1 General Specifications.

GS1 Digital Link URIs are governed by a companion standard, **GS1 Digital Link: URI Syntax**, which defines the primary identification keys, key-qualifier ordering, and data-attribute encoding the parser applies to Digital Link inputs:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implements **Release 1.7.0 (Ratified, Aug 2026)** of the GS1 Digital Link: URI Syntax standard.

Section references throughout this document refer to the GS1 General Specifications (e.g. "Table 7-5", "section 7.12"), except Digital Link section numbers (e.g. "§4.9", "§4.12"), which refer to the GS1 Digital Link: URI Syntax standard.

---

## GS1 Application Identifiers

A **GS1 Application Identifier (AI)** is a short numeric prefix — two to four digits — that identifies the meaning and format of the data that immediately follows it. AIs are defined in the GS1 General Specifications and cover a wide range of supply-chain data: product identifiers, dates, quantities, lot numbers, serial numbers, measurements, URLs, and more.

### Structure of an AI element

Each AI element consists of two parts:

```
┌─────────────┬──────────────────────────────────┐
│  AI code    │  Data value                      │
│  (2–4 digits)│                                  │
└─────────────┴──────────────────────────────────┘

Example:
  01  09506000134352
  ^^  ^^^^^^^^^^^^^^
  AI  GTIN-14 value (14 digits, fixed length)
```

The AI code is always numeric. The data value immediately follows, with no delimiter between the code and the value.

### Fixed-length vs variable-length AIs

AIs fall into two categories:

| Type | Behaviour | Example |
|---|---|---|
| **Fixed-length** | Exact number of characters, always consumed in full | AI `01` (GTIN) — always 14 digits |
| **Variable-length** | 1 up to a maximum number of characters; terminated by a GS separator or end of input | AI `10` (Batch/Lot) — 1 to 20 alphanumeric characters |

Whether an AI is fixed or variable is determined solely by its definition in the GS1 specification — the parser never guesses.

### Multi-AI element strings

Multiple AIs can be concatenated into a single element string. Fixed-length AIs can be concatenated directly because the parser always knows exactly how many characters to consume. Variable-length AIs must be terminated by the **GS character** (ASCII `0x1D`, also known as FNC1 in barcode symbologies) whenever another AI follows them, so the parser knows where one value ends and the next AI code begins.

```
Fixed-length AIs — no separator needed:

  0109506000134352  17261231
  ^^^^^^^^^^^^^^^^  ^^^^^^^^
  (01) GTIN-14      (17) Expiry date YYMMDD (also fixed)


Variable-length AI followed by another AI — GS separator required:

  10LOT-001 <GS> 21SN-98765
  ^^^^^^^^^       ^^^^^^^^^^
  (10) Batch/Lot  (21) Serial number
         ↑
     ASCII 0x1D


Mixed — variable before fixed:

  10LOT-001 <GS> 0109506000134352
  ^^^^^^^^^       ^^^^^^^^^^^^^^^^
  (10) Batch/Lot  (01) GTIN-14
```

In Java string literals, write the GS character with the Unicode escape `"\u001D"`.

### Common AIs

| AI | Data title | Format | Example value |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, single monetary area) |
| `710` | NHRN PZN | X..20 | `12345678` |

> The **fourth digit** of a 4-digit measure or price AI encodes the number of implied decimal places — `3103` is net weight in kg with 3 decimals (`001500` = 1.500 kg), while `3102` would read the same digits as 15.00 kg. The `Format` column above shows the *data* format; each AI's full `getFormatString()` includes the AI itself (e.g. `N4+N6` for `3103`).

### Human Readable Interpretation (HRI)

The conventional human-readable form wraps each AI code in parentheses immediately before its value, with a space between elements:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

The GS separator is not shown in HRI. `GS1AIObject.toHriString()` produces this format.

### Four-digit AI codes

Some AIs use four digits rather than two. The first two digits identify the AI family; the third and/or fourth digits carry additional semantics (such as the implied decimal point position for measurement AIs). The parser resolves the full AI code from the element string automatically — callers always work with the full code (e.g. `"3102"`, not just `"31"`).

---

## Quick Start

```java
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.GaiaConstants.ParseMode;
import tools.pantheum.gaia.result.ParseResult;

GaiaParser parser = new GaiaParser();

// Default parse (INTERPRETATION mode)
ParseResult response = parser.parse("01095060001343521726123110LOT-001");

if (response.isValid()) {
    System.out.println(response.getAiObject().toHriString());
    // → (01)09506000134352 (17)261231 (10)LOT-001
} else {
    response.getErrors().forEach(e -> System.out.println(e.getMessage()));
}
```

> **GS separator:** Variable-length AIs within a multi-AI string must be delimited by the GS character (ASCII `0x1D`). Use `"\u001D"` in Java string literals.

---

## Parsing Pipeline

### Pre-stage — Input Modifiers

If the `ParseConfig` carries any **input modifiers**, they run before anything else — before correlation stripping, before carrier detection, before the GS1 pipeline is entered. Each modifier rewrites the raw input for the next one, and every stage below operates on the chain's output.

No modifiers are configured by default, so this pre-stage is a no-op unless you opt in. See [Input Modifiers](#input-modifiers).

---

### Stage 0 — Correlation ID

Before any GS1 processing, `GaiaParser` checks whether the input starts with an optional **correlation ID prefix**: exactly 8 ASCII decimal digits followed by a tilde (`~`), e.g. `12345678~`.

If the prefix is present it is stripped and stored as a `CorrelationInfo` on the returned `ParseResult`. All subsequent stages operate on the stripped payload. If no prefix is present, the input passes through unchanged.

See [Correlation ID](#correlation-id) for details.

---

### Stage 1 — Input Routing

After correlation stripping, `GaiaParser` checks whether the (stripped) input begins with an **AIM Code ID**: a three-character prefix of the form `]` + ASCII letter + ASCII digit (e.g. `]C1` for GS1-128, `]d2` for GS1 DataMatrix, `]e0` for GS1 DataBar / GS1 Composite).

```
Input
  │
  ├─ input modifiers configured? ──YES──► run chain in order ──► ModifierInfo stored
  │
  ├─ starts with DDDDDDDD~ ──► strip correlation prefix ──► CorrelationInfo stored
  │
  ├─ starts with AIM Code ID? ──YES──► DataCarrierParser
  │                                         │
  │                                    Validate carrier
  │                                    Strip prefix + ECI
  │                                    Pad GTIN if needed
  │                                         │
  │                                    GS1Parser
  │                                    (see below)
  │
  ├─ starts with http:// or https:// ──YES──► GS1DLParser
  │
  └─ otherwise ───────────────────────────► GS1AIParser
```

If the carrier is not GS1 AI-capable (e.g. a postal barcode), parsing stops immediately with a `GE-D002` error.

---

### Stage 2 — Syntax

Runs unconditionally. Consists of two sub-steps:

**2a. Tokenisation (`AISyntaxParser`)**
- Reads the AI code length from the first two characters using the GS1 prefix table (GS1 General Specifications Table 7-5).
- Fixed-length AIs consume an exact byte count from the input.
- Variable-length AIs are read until a GS character or end of input.
- Multi-component AIs have their value blob sliced into per-component segments.

**2b. Structural validation (`SyntaxValidator`)**
- Checks for duplicate AIs (`GE-S004`).
- Checks required AI dependencies, e.g. AI `02` requires AI `37` (`GE-S005`).
- Checks excluded AI pairings (`GE-S006`).

Errors at this stage have level `SYNTAX_ERROR` (tokeniser) or `INTEGRITY_ERROR` (structural). If **any** error is present — tokeniser or structural — the pipeline stops and the content and interpretation stages are skipped.

---

### Stage 3 — Content

Runs only when Stage 2 produced no errors (neither tokeniser nor structural). Per-element pipeline (each step only runs if the previous produced no errors):

| Step | Validator | Error Codes |
|---|---|---|
| Regex check | `RegexValidator` | `GE-C001` |
| Component charset + format | `ComponentValidator` | `GE-C005` + per-condition format codes (`GE-C054`–`GE-C115`) |
| Check digit / check character | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Custom semantic validation | `ContentValidatorRegistry` | per-condition content codes (`GE-C116`–`GE-C170`) |

Errors at this stage have level `FORMAT_ERROR` or `DATA_ERROR`, with one exception: the GS1
company-prefix checks on the GS1-key AIs are advisory and carry level `WARNING` (see the
[Error Reference](#error-reference)), so an unrecognised company prefix does not by itself
make the result invalid.

---

### Stage 4 — Interpretation

Runs only in `INTERPRETATION` mode and only when no element carries an error from any prior stage. The `InterpretationEngine` enriches each element with labelled metadata:

- Dates reformatted as `dd/mm/yyyy`
- GTIN check digit decomposition and GS1 company prefix lookup
- ISO 3166 country names
- ISO 4217 currency names and symbols
- Decoded decimal amounts
- HRI (Human Readable Interpretation) fragments

Results are attached as `GS1AIInterpretation` entries on each `GS1AIObjectElement`.

---

## Parse Configuration (`ParseConfig`)

`GaiaParser` exposes exactly two entry points:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` runs with the **default configuration**: `INTERPRETATION` mode, little-endian dates (`dd/mm/yyyy`) with a `/` separator and a four-digit year, and **English** error messages. To change any of these — including the parse mode — build a `ParseConfig` with its fluent builder and use the two-argument overload.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

The option enums all live in `GaiaConstants`.

### Options

| Builder method | Enum (`GaiaConstants`) | Default | Effect |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Pipeline depth — see [Parse Modes](#parse-modes). |
| `language(...)`      | `Language`      | `ENGLISH`        | Language of error messages, interpretation labels, **and** AI descriptions. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Date component order: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Character between date components: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) or `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) or `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Skips the structural "requires" check (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Skips the structural "excludes" check (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / class name | none | Code that rewrites the raw input before parsing — two [built-in modifiers](#built-in-modifiers) plus anything you write. See [Input Modifiers](#input-modifiers). |

The four date options affect only the formatted date strings produced by interpretation enrichers (in `INTERPRETATION` mode); they do not change validation. Builder values may be omitted — any option left unset (or passed `null`) keeps its default.

### Localized messages and labels

`language(...)` selects the language for **three** kinds of human-readable text: error messages, interpretation labels (the `getLabel()` of each `GS1AIInterpretation`), and AI descriptions (the `getDescription()` of each `GS1AIObjectElement`).

**35 languages** are defined by `GaiaConstants.Language`, covering the world's most-spoken languages: English, French, Spanish, German, Italian, Portuguese, Dutch, Polish, Russian, Ukrainian, Czech, Swedish, Chinese, Japanese, Korean, Arabic, Indonesian, Hindi, Turkish, Bengali, Urdu, Vietnamese, Nigerian Pidgin, Egyptian Arabic, Marathi, Telugu, Tamil, Cantonese, Wu Chinese, Tagalog, Persian, Hausa, Punjabi, Javanese, and Swahili.

Translation state (as shipped):
- **Interpretation labels** — translated for all languages.
- **Error messages** — translated for all languages.
- **AI descriptions** — translated for all languages except English. English is not a separate catalogue: it is read directly from the `description` field of the AI's entry in `gs1-application-identifiers.jsonld`, which every AI description ultimately falls back to.

Nigerian Pidgin (`NIGERIAN_PIDGIN`), an English-based creole, intentionally reuses the English text for interpretation labels and error messages. AI descriptions are the exception to that exception: they are translated into genuine Pidgin phrasing rather than reusing English, since the AI-description catalogues were produced independently of the label/message catalogues. Machine translations should be reviewed by native speakers before relying on them in production.

Any message, label, or description missing from a language's catalogue falls back to English. Right-to-left languages (Arabic, Urdu, Egyptian Arabic, Persian) are stored correctly as strings; rendering them RTL is the display layer's responsibility.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Interpretation labels localize the same way (the values are unchanged — only the labels):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI descriptions localize the same way (only `getTitle()`, e.g. `"GTIN"`, is not localized):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Date formatting

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Input Modifiers

An **input modifier** is code that rewrites the raw input string before Gaia parses it. Modifiers exist for the input that arrives already mangled — a scanner that substitutes a printable placeholder for the GS separator, middleware that wraps the payload in a vendor prefix, a host system that upper-cases everything. Rather than pre-processing every string at each call site (and getting it subtly wrong in one of them), register the normalisation once on the `ParseConfig` and let the parser apply it.

Modifiers run at the very start of `GaiaParser.parse(...)` — before correlation-ID stripping, before AIM Code ID detection, before the GS1 pipeline. Everything downstream sees only the rewritten string. **Nothing is configured by default**, including the two [built-in modifiers](#built-in-modifiers) — you opt in per `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Built-in modifiers

Two modifiers ship in the core jar, in `tools.pantheum.gaia.modifier.custom`. They cover the two ways a GS1 payload most often arrives mangled — printed HRI parentheses treated as data, and spurious spaces — so the common cases need no custom class:

| Class | `getName()` | What it does |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Strips the HRI parentheses around each AI (`(01)…(10)…`) and restores the FNC1 separator they implied. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Removes every space (`0x20`) from the AI element string. |

They are ordinary `ModifierInterface` implementations with no special status — registered, ordered, reported, and failed exactly like your own:

```java
import tools.pantheum.gaia.modifier.custom.ModifierRemoveAIBrackets;
import tools.pantheum.gaia.modifier.custom.ModifierRemoveSpaces;

ParseConfig config = ParseConfig.builder()
        .modifier(new ModifierRemoveSpaces())        // spaces first — see the ordering note
        .modifier(new ModifierRemoveAIBrackets())
        .build();

ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123 ( 17 ) 251231", config);
r.getPayload();   // 010952123454321310ABC123<GS>17251231
r.isValid();      // true
```

Both are stateless and thread-safe, so a single instance can be shared, and both are addressable by fully-qualified class name for configuration-driven setups (see [Registering modifiers](#registering-modifiers)).

#### `ModifierRemoveAIBrackets`

The GS1 human-readable interpretation prints every AI in parentheses — `(01)09521234543213(10)ABC123` — purely as a printing convention. A scanner or middleware configured to emit the HRI passes those brackets through as data, and the tokeniser has no idea what to do with them.

Stripping the brackets is only half the job. In HRI, the opening `(` of the *next* AI is what marks the end of the previous value, so a variable-length AI needs no FNC1 in bracketed form. Remove the brackets naively and that boundary disappears:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

So the modifier **re-inserts an FNC1 at each boundary whose preceding AI is variable-length**, restoring exactly what the brackets encoded:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Length is looked up in the parser's own `AiDefinitionRegistry`, so every variable-length AI is handled rather than a hard-coded list. Three cases are deliberately left alone: a value that already ends in FNC1 (a source emitting both conventions gets no second separator), a bracketed code that is not a known AI (an unknown AI says nothing about its own length), and the final AI in the string.

The rewrite is **idempotent** — running it over its own output changes nothing — so it is safe on a mixed feed where only some inputs are bracketed.

> **Limitation.** `(` and `)` are themselves valid GS1 data characters, and the pattern is just `\((\d{2,4})\)`. A value that happens to contain a parenthesised two-to-four-digit number would also have it unwrapped. Apply this only to a source that uses the HRI bracket convention rather than genuine parenthesised values.

#### `ModifierRemoveSpaces`

Some scanners, middleware, and label-print pipelines insert spurious spaces into an otherwise well-formed element string — padding a fixed-width field, separating human-readable groups, or wrapping a long value. The tokeniser treats each one as data, corrupting the value it sits in and, for a variable-length AI, shifting everything after it.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Only ASCII `0x20` is removed. Other whitespace is left in place — a tab, for instance, is outside the GS1 encodable set, so the parser reports it as `GE-S008` rather than having it silently swept away.

> **Limitation.** Space (`0x20`) is part of the GS1 invariant character set, so a batch/lot or customer part number may legitimately contain one. The modifier cannot tell a spurious space from a genuine one; apply it only to a source known not to use spaces inside its AI values.

#### Prefixes are skipped, not rewritten

Modifiers run before the parser has stripped anything, so the raw input may still carry a correlation ID, an AIM Code ID, and an ECI indicator. Both built-ins locate the start of the AI element string using the parser's own `CorrelationIdParser` and `DataCarrierParser` logic, rewrite only from there onward, and splice the result back onto the **untouched** prefix:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

EAN/UPC carriers whose value is padded to GTIN-14 (`isRequiresGtinPadding()`) are skipped entirely — their payload is a raw numeric barcode value with no AI structure, so neither brackets nor spaces can be meaningful there.

#### Ordering: spaces before brackets

When both are used, **register `ModifierRemoveSpaces` first**. Bracket matching is position-sensitive: a padded `( 01 )` does not match `\((\d{2,4})\)`, so the brackets survive and the separator they implied is never restored.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Writing a modifier

Write your own when neither built-in fits — the interface is one method.

```java
package com.example.gaia;

import tools.pantheum.gaia.modifier.ModifierInterface;

/** Substitutes the printable {GS} placeholder back to the real separator (ASCII 0x1D). */
public final class GsPlaceholderModifier implements ModifierInterface {

    @Override
    public String modify(String input) {
        return input == null ? null : input.replace("{GS}", "\u001D");
    }
}
```

Override the two-argument overload instead when the rewrite depends on the parse configuration:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contract:

| Rule | Detail |
|---|---|
| Stateless and thread-safe | One instance is cached per class and shared across every parse. |
| Public no-argument constructor | Required only when the modifier is referenced by class name. |
| Handle `null` and empty input | The parser does not filter those out before the chain runs. |
| `null` return means "no change" | The previous value is carried forward. Return `input` unchanged when the modifier does not apply. |
| Prefer returning unchanged over throwing | A throwing modifier aborts the parse — see [Failure handling](#modifier-failure-handling). |
| `getName()` | Override to control the name reported on `ModifierInfo`; defaults to the simple class name. |

### Registering modifiers

Modifiers run in the order they are added, each fed the previous one's output. Register them by instance, by fully-qualified class name, or as a list of either:

```java
ParseConfig config = ParseConfig.builder()
        .modifier(new GsPlaceholderModifier())                          // by instance
        .modifierClass("com.example.gaia.StripVendorWrapperModifier")   // by class name
        .build();

// Or from external configuration — a list of fully-qualified class names, in execution order
ParseConfig fromConfig = ParseConfig.builder()
        .modifierClasses(List.of("tools.pantheum.gaia.modifier.custom.ModifierRemoveSpaces",
                                 "tools.pantheum.gaia.modifier.custom.ModifierRemoveAIBrackets",
                                 "com.example.gaia.StripVendorWrapperModifier"))
        .build();

ParseResult result = parser.parse("SCAN:10LOT-A{GS}17271231", config);
```

The [built-in modifiers](#built-in-modifiers) are named the same way as your own — **always fully qualified**. There is no short-name or alias lookup for them; `ModifierRegistry` resolves every modifier, shipped or not, by full class name.

Names are resolved by `ModifierRegistry`, which instantiates each class once via its no-argument constructor and caches the instance for every later config naming the same class. Resolution happens **when the config is built**, so a name that cannot be found, does not implement `ModifierInterface`, or cannot be instantiated throws `IllegalArgumentException` there — not silently at parse time. A modifier that cannot be built reflectively (one holding an injected dependency, say) can be pre-registered so it is still addressable by name:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Inspecting what a modifier did

When modifiers are configured, `ParseResult.getPayload()` reflects the **modified** input. The original is preserved on `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` reports each modifier's `getName()`, which defaults to the simple class name but is overridden by both built-ins — so a chain of the two reports the display names, not the class names:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` returns `null` when no modifiers were configured. When modifiers ran but every one returned the input unchanged, the info is present and `isModified()` is `false` — only modifiers that actually changed the input are listed in `getAppliedModifiers()`.

### Modifier failure handling

A modifier that throws aborts the parse. The exception is wrapped in `GaiaModifierException` naming the offending modifier, and the result carries a `GE-I001` internal error whose message includes that name; `getPayload()` reports the unmodified input. The parse deliberately does **not** continue with a half-rewritten string — a normalisation step that failed silently would produce results that look valid but were parsed from the wrong input.

---

## Parse Modes

Each mode names the deepest [pipeline stage](#parsing-pipeline) it runs; every stage before it still runs.

| Mode | Runs through | Answers |
|---|---|---|
| `DATA_CARRIER` | Stage 1 (input routing) | Which symbology carried this? |
| `SYNTAX` | Stage 2 (syntax) | Are the AI codes and lengths well-formed? |
| `CONTENT` | Stage 3 (content) | Are the values valid GS1 data? |
| `INTERPRETATION` | Stage 4 (interpretation) | What do the values mean? |

### DATA_CARRIER Mode

Stops after Stage 1 — validates the AIM Code ID and identifies the symbology, but does not enter the AI parsing pipeline. Useful for symbology identification and routing without full validation overhead.

```java
// GS1-128 prefixed input (]C1 = GS1-128 / ISBT 128)
ParseResult response = parser.parse(
    "]C10109506000134352",
    ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

System.out.println(response.hasDataCarrier());       // true
System.out.println(response.getDataCarrier().getName());
// → GS1-128 / ISBT 128
System.out.println(response.getDataCarrier().getAimCodeId());
// → ]C1
System.out.println(response.getDataCarrier().isGs1AICapable());
// → true
System.out.println(response.getDataCarrier().getDataCarrierType());
// → GS1_128   (typed symbology — see DataCarrierEntry and DataCarrierType)
System.out.println(response.getAiObject());          // null — AI parsing not performed

// Unrecognised AIM Code ID
ParseResult unknown = parser.parse("]Z9somedata",
    ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());
System.out.println(unknown.isValid());               // false
unknown.getErrors().forEach(e ->
    System.out.println("[" + e.getId() + "] " + e.getMessage()));
// → [GE-D001] AIM Code ID ']Z9' is not a recognised data carrier
```

**Use when:** Your application needs to identify the barcode type before deciding how to process the payload — for example, routing to different handlers for 1D vs 2D symbologies. For that routing, prefer the typed [`DataCarrierType`](#datacarrierentry-and-datacarriertype) (`getDataCarrier().getDataCarrierType()`) over string-matching `getName()`.

---

### SYNTAX Mode

Stops after Stage 2. Useful for structural pre-screening without the cost of content validation.

```java
ParseResult response = parser.parse(
    "0109506000134352",
    ParseConfig.builder().requestedParseMode(ParseMode.SYNTAX).build());

// Tells you: is the AI structure valid?
// Does NOT tell you: is the GTIN check digit correct?
System.out.println(response.isValid()); // true — syntax is fine

for (GS1AIObjectElement e : response.getAiObject().getAis()) {
    System.out.println("(" + e.getAi() + ") " + e.getTitle() + " = " + e.getValue());
}
// → (01) GTIN = 09506000134352
```

**Use when:** You want to check that the AI codes and data lengths are well-formed before committing to full validation, or when scanning high volumes where content errors are rare.

---

### CONTENT Mode

Stops after Stage 3.

```java
// Valid input
ParseResult response = parser.parse("01095060001343521726123110LOT-001",
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(response.isValid());              // true
System.out.println(response.getAiObject().toHriString());
// → (01)09506000134352 (17)261231 (10)LOT-001

// Invalid GTIN check digit
ParseResult bad = parser.parse("0109506000134351",
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(bad.isValid());                   // false
bad.getErrors().forEach(e ->
    System.out.println("[" + e.getId() + "] " + e.getMessage()));
// → [GE-C003] Check digit validation failed for AI (01) value '09506000134351'

// Variable-length AI followed by another AI — GS separator required
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult multi = parser.parse(input,
    ParseConfig.builder().requestedParseMode(ParseMode.CONTENT).build());

System.out.println(multi.isValid());                 // true
multi.getAiObject().getAis().forEach(e ->
    System.out.printf("(%s) %s%n", e.getAi(), e.getValue()));
// → (01) 09506000134352
// → (10) LOT-ABC
// → (21) SN-98765
```

> Most AIs may not stand alone: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) and `21`
> (SERIAL) each *require* an identification key such as AI `01` in the same element
> string, so omitting the GTIN above would fail Stage 2 with `GE-S005` rather than
> reaching content validation at all. Set `skipRequiresCheck(true)` on the
> `ParseConfig` to parse fragments that deliberately omit their companion AIs.

**Use when:** You need to know whether a scanned value is fully GS1-compliant before using it in a business process, without the overhead of interpretation enrichment.

---

### INTERPRETATION Mode (default)

Runs the full pipeline through Stage 4. The default when calling `parse(String)` with no mode argument. Only enriches elements that have passed content validation cleanly.

```java
// GTIN with expiry date and batch/lot
String input = "0109506000134352" + "17261231" + "10LOT-001";
ParseResult response = parser.parse(input,
    ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());

System.out.println(response.isValid());              // true
System.out.println(response.getAiObject().toHriString());
// → (01)09506000134352 (17)261231 (10)LOT-001

for (GS1AIObjectElement element : response.getAiObject().getAis()) {
    System.out.println("AI " + element.getAi() + " — " + element.getTitle());
    for (GS1AIInterpretation interp : element.getInterpretations()) {
        System.out.printf("  %-25s : %s%n", interp.getLabel(), interp.getValue());
    }
}
```

**Example output:**
```
AI 01 — GTIN
  GS1 member code            : 950
  GS1 member organisation    : GS1 Global Office
  GTIN type                  : GTIN-13
  GTIN                       : 9506000134352
  Check digit                : 2

AI 17 — USE BY or EXPIRY
  Date                       : 31/12/2026
  Date format                : dd/mm/yyyy

AI 10 — BATCH/LOT
  (no interpretations — a free-text lot number carries no derivable metadata)
```

**Monetary amount example (AI 3932 — price with ISO currency code):**
```java
// AI 3932 requires a variable-measure AI in the same element string — here AI 3103.
ParseResult price = parser.parse("]d2" + "0109506000134352" + "3103001500" + "3932036002953",
    ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());

GS1AIObjectElement ai = price.getAiObject().get("3932");
ai.getInterpretations().forEach(i ->
    System.out.printf("%-28s : %s%n", i.getLabel(), i.getValue()));
// Currency code                : 036
// Currency alpha code          : AUD
// Currency name                : Australian Dollar
// Amount                       : 29.53
// Decimal places               : 2
// Monetary amount              : AUD 29.53
// Monetary amount (formatted)  : A$29.53
```

**Use when:** Building display layers, label verification tools, or any UI that needs human-friendly breakdowns of AI values.

---

## Correlation ID

Some workflows prepend a proprietary 8-digit correlation identifier to the raw GS1 input so that scan events can be tied back to a session or transaction. The format is:

```
DDDDDDDD~<GS1 content>

Example:
  12345678~0109506000134352
  ^^^^^^^^
  8-digit correlation ID
          ^
          tilde separator
           ^^^^^^^^^^^^^^^^
           GS1 element string (passed to the normal pipeline)
```

The `~` (tilde) is the separator. It is **not** part of the GS1 content — it is stripped before any GS1 parsing begins.

### Detection rules

The prefix is detected when the input starts with exactly 8 ASCII decimal digits (`0`–`9`) immediately followed by `~`. If the 9th character is not `~`, or any of the first 8 characters is not a digit, the input is treated as plain GS1 content with no correlation prefix.

### Accessing the correlation ID

```java
ParseResult response = parser.parse("12345678~0109506000134352");

System.out.println(response.hasCorrelationId());           // true
System.out.println(response.getCorrelationInfo().getId()); // "12345678"
System.out.println(response.getPayload());                 // "0109506000134352"

// Without a prefix — hasCorrelationId() is false
ParseResult plain = parser.parse("0109506000134352");
System.out.println(plain.hasCorrelationId());              // false
System.out.println(plain.getCorrelationInfo());            // null
```

### Combining with AIM Code ID

A correlation prefix can appear before an AIM Code ID. The parser handles this transparently:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Implementation class:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

A **GS1 Digital Link** encodes one or more AI values directly in the structure of an HTTP(S) URL, enabling web-resolvable identifiers for physical products. GAIA implements the *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) for **uncompressed** URIs.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` recognises Digital Link URIs automatically — any input beginning with `http://` or `https://` is routed to `GS1DLParser`, which runs the same content and interpretation stages as the element-string pipeline.

### URI structure and AI roles

Each AI in a Digital Link URI plays one of three roles, exposed on each `GS1AIObjectElement` via `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Role | Location | Example |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | First `/ai/value` pair of the path (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Subsequent path pairs, ordered per primary key (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Query parameters with all-numeric keys (§4.10) | `?17=271231` |

Structural rules enforced (`DLPathRules`):
- Exactly **one** primary identification key in the path; additional keys must be encoded as query data attributes.
- Key qualifiers must be admitted by the primary key and appear in the prescribed order. Optional qualifiers may be omitted, but any that *are* present must still follow the fixed order — see [Qualifier ordering](#qualifier-ordering).
- Arbitrary custom path segments may precede the primary key (e.g. `/products/au/01/...`); retrieve them via `getDigitalLinkInfo().getCustomPathStem()`.
- Query keys that are non-numeric (`linkType`, `context`, extension parameters like `23P`) are ignored; all-numeric keys must be valid AIs flagged `validAsDataAttribute`.
- Percent-encoded value characters are decoded; AIs `(03)` and `(8014)` are not permitted.

The primary keys and their admissible qualifier sequences are **data-driven** from the AI definitions — the `gs1DigitalLinkPrimaryKey` flag and the `gs1DigitalLinkQualifiers` attribute — rather than hard-coded.

Any structural violation, or a non-URL input, produces a Digital Link structural error (`GE-L001`–`GE-L014`, one code per condition). The decomposed URL metadata (`scheme`, `domain`, `path`, `customPathStem`, `query`, and the `java.net.URL`) is available via `getDigitalLinkInfo()` even when structural errors are present.

### Qualifier ordering

For each primary key, `gs1DigitalLinkQualifiers` lists one or more **ordered** qualifier sequences. Within a sequence an AI wrapped in square brackets is **optional**, an unbracketed AI is **required** — mirroring the `[cpv-comp]` notation of the §4.9 ABNF. The sequences for one primary key are mutually exclusive alternatives.

GTIN (`01`), for example, defines two sequences:

| Path | Sequence | Meaning |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — each optional, but fixed in this order |
| upui-path | `235` | TPX (required); GTIN + TPX = UPUI |

So `/01/09506000134352/10/LOT-ABC/21/SER` is valid (LOT before SER, CPV omitted), `/01/.../21/SER/10/LOT-ABC` is **rejected** (out of order), and `/01/09506000134352/235/2ABC456` is the upui-path. The ordering check is an order-preserving subsequence match, so optional AIs can be skipped but never reordered.

```java
ParseResult resp = parser.parse(
    "https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

resp.getAiObject().hasDigitalLink();        // true
resp.getContentType();                       // GS1_DIGITAL_LINK
resp.getAiObject().get("01").getDigitalLinkAIType();  // PRIMARY_IDENTIFICATION_KEY
resp.getAiObject().get("17").getDigitalLinkAIType();  // DATA_ATTRIBUTE

// Canonical form on id.gs1.org (data attributes become query parameters)
resp.getAiObject().getCanonicalDigitalLink();
//   https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231

// Any custom path stem before the primary key (empty here)
resp.getAiObject().getDigitalLinkInfo().getCustomPathStem();  // ""

// Convert to the equivalent raw element string (FNC1-separated)
resp.getAiObject().toElementString();       // 010950600013435210LOT-ABC<GS>17271231
```

**Implementation class:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Working with Results

### ParseResult

The top-level result returned by `GaiaParser.parse()`.

| Method | Returns | Description |
|---|---|---|
| `isValid()` | `boolean` | `true` if no errors at any level. Warnings do not affect validity. Always `true` when `getAiObject()` is `null`. |
| `getPayload()` | `String` | The input string after correlation prefix stripping — and after any [input modifiers](#input-modifiers) rewrote it. |
| `getPayloadContent()` | `String` | The payload with AIM Code ID and ECI prefix stripped. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (a data carrier rejected as non-GS1, e.g. a Code 39 `]A0` carrier), or `UNABLE_TO_DETERMINE_CONTENT` (when `aiObject` is `null`, e.g. `DATA_CARRIER` mode). |
| `getRequestedParseMode()` | `ParseMode` | The configured pipeline depth (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | The deepest stage the parse actually reached — see below. |
| `isParseComplete()` | `boolean` | `true` if the parse reached the requested depth (`achieved == requested`). Independent of `isValid()`. |
| `getAiObject()` | `GS1AIObject` | All resolved AIs. `null` in `DATA_CARRIER` mode. |
| `getErrors()` | `List<GaiaError>` | All non-WARNING errors (object-level + all element-level). |
| `getWarnings()` | `List<GaiaError>` | All WARNING advisories (object-level + all element-level). |
| `hasWarnings()` | `boolean` | `true` if any WARNING advisories were raised. |
| `getIssues()` | `List<GaiaError>` | Errors and warnings combined. |
| `hasDataCarrier()` | `boolean` | `true` if an AIM Code ID was recognised. |
| `getDataCarrier()` | `DataCarrierEntry` | Symbology metadata, or `null` if no carrier identified. |
| `hasEci()` | `boolean` | `true` if an ECI indicator was stripped from the payload. |
| `getEci()` | `EciEntry` | ECI encoding metadata, or `null`. |
| `hasCorrelationId()` | `boolean` | `true` if an `DDDDDDDD~` correlation prefix was present in the original input. |
| `getCorrelationInfo()` | `CorrelationInfo` | The extracted correlation ID, or `null` if none was present. |
| `isInputModified()` | `boolean` | `true` if an [input modifier](#input-modifiers) changed the input. |
| `getModifierInfo()` | `ModifierInfo` | What the modifier chain did — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` if no modifiers were configured. |
| `getTiming()` | `ProcessingTiming` | Wall-clock timing of the parse — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` if not produced by `GaiaParser`. |
| `getVersion()` | `String` | The library version that produced the result. |

#### Requested vs achieved parse mode

The pipeline runs the ladder **SYNTAX → CONTENT → INTERPRETATION** and stops early on errors, so the mode actually *achieved* can be shallower than the mode *requested*. `getAchievedParseMode()` reports how far it got:

| Requested | What happens | Achieved | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | a **syntax / structural** error halts the parse after tokenisation | `SYNTAX` | `false` |
| `INTERPRETATION` | a **content** error (bad format/check digit) blocks enrichment | `CONTENT` | `false` |
| `CONTENT` | content always runs to completion (errors are annotated, not fatal) | `CONTENT` | `true` |
| any (clean input) | the pipeline reaches the requested depth | = requested | `true` |
| `DATA_CARRIER` | carrier validated; no AI content parsed | `DATA_CARRIER` | `true` |
| any | the data carrier is rejected before AI parsing (e.g. a non-GS1 `]A0` carrier) | `SYNTAX` | `false` |

`isParseComplete()` is independent of `isValid()`: a `CONTENT` parse of a GTIN with a bad check digit is **complete** (it ran the content stage) yet **invalid** (the check digit failed). Use `isParseComplete()` to ask "did the pipeline run as deep as I asked?" and `isValid()` to ask "is the data well-formed?".

```java
ParseResult r = parser.parse("0109506000134350",          // bad check digit
        ParseConfig.builder().requestedParseMode(ParseMode.INTERPRETATION).build());
r.getRequestedParseMode();  // INTERPRETATION
r.getAchievedParseMode();   // CONTENT  — enrichment was skipped because of the content error
r.isParseComplete();        // false
r.isValid();                // false
```

---

### GS1AIObject

The collection of resolved AI elements.

| Method | Description |
|---|---|
| `getAis()` | All `GS1AIObjectElement` instances in input order. |
| `get(String aiCode)` | First element matching the given AI code, or `null`. |
| `contains(String aiCode)` | `true` if an AI with that code is present. |
| `size()` | Number of resolved AIs. |
| `isValid()` | `true` if no object-level errors and no element has errors. |
| `toHriString()` | HRI string, e.g. `(01)09506000134352 (17)261231`. |
| `toElementString()` | Raw element string — no brackets, FNC1 after each variable-length element — e.g. `010950600013435210LOT-ABC<GS>17271231`. Returns `null` if `isValid()` is `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` when `hasDigitalLink()` is true, otherwise `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` if the input was a GS1 Digital Link URI carrying a primary identification key. A well-formed URL with no primary key still exposes `getDigitalLinkInfo()` but returns `false` here. |
| `getCanonicalDigitalLink()` | The canonical GS1 Digital Link URI (§4.12) on `https://id.gs1.org` — primary key and qualifiers as path segments, data attributes as query parameters sorted by AI key — or `null` if no primary key is present. |
| `getDigitalLinkInfo()` | URI decomposition metadata (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), or `null` if not a Digital Link. |
| `getAllErrors()` | Object-level + all element errors (non-WARNING). |
| `getAllWarnings()` | Object-level + all element warnings. |
| `getAllIssues()` | Everything combined. |

---

### GS1AIObjectElement

A single resolved AI instance.

| Method | Description |
|---|---|
| `getAi()` | AI code, e.g. `"01"`, `"3102"`. |
| `getTitle()` | GS1 data title, e.g. `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Full GS1 description of the AI, **localized to the parse language** (e.g. `"Global Trade Item Number (GTIN)"` in English). Falls back to the English text from the AI definition if untranslated. |
| `getFormatString()` | Format descriptor covering the AI *and* its data, e.g. `"N2+N14"` for AI `01`, `"N2+X..20"` for AI `10`, `"N4+N3+N..15"` for AI `3932`. |
| `getValue()` | Raw data value extracted from the element string. |
| `isFixedLength()` | `true` if the AI has a fixed data length. |
| `getPosition()` | Zero-based character offset in the original input. |
| `getGS1ComponentValues()` | Per-component value slices (for multi-component AIs). |
| `getErrors()` | Element-level non-WARNING errors. |
| `getWarnings()` | Element-level WARNING advisories. |
| `getIssues()` | Element-level errors and warnings combined. |
| `hasErrors()` | `true` if any non-WARNING errors are attached. |
| `hasWarnings()` | `true` if any WARNING advisories are attached. |
| `getInterpretations()` | `GS1AIInterpretation` entries (populated in INTERPRETATION mode). |
| `getInterpretation(String type)` | First interpretation matching the given `GS1Constants_Enricher` type key, or `null`. |
| `getDigitalLinkAIType()` | The element's Digital Link role (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), or `null` for element-string inputs. |
| `hasDigitalLinkAIType()` | `true` if a Digital Link role has been assigned. |

---

### GaiaError

An immutable validation error or advisory.

| Method | Description |
|---|---|
| `getId()` | Catalogue identifier, e.g. `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, or `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, or `INTERNAL`. |
| `getCode()` | Machine-readable short code. |
| `getAi()` | AI code that caused the error, or `null` for object-level errors. |
| `getMessage()` | Human-readable interpolated message. |
| `getPosition()` | Zero-based character offset in the original input. |

---

### GS1AIInterpretation

A single labelled interpretation fragment, attached to a `GS1AIObjectElement` in `INTERPRETATION` mode.

| Method | Description |
|---|---|
| `getType()` | Machine-readable type key, e.g. `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Stable across languages. |
| `getLabel()` | Human-readable label, **localized to the parse language** (e.g. `"Date"` / `"GS1 company prefix"` in English). |
| `getValue()` | Extracted/enriched value, e.g. `"31/12/2026"`, `"9506000"`. Not localized. |

---

### DataCarrierEntry and DataCarrierType

When the input carries an AIM Code ID, `ParseResult.getDataCarrier()` returns a `DataCarrierEntry` describing the symbol that carried the data. The entry is the specific registry record for the matched AIM Code ID; `DataCarrierType` is the compile-time enum it belongs to.

#### DataCarrierEntry

The metadata for one recognised AIM Code ID (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Method | Description |
|---|---|
| `getAimCodeId()` | The AIM Code ID that matched, e.g. `"]C1"`. |
| `getName()` | Human-readable name of the specific symbol, e.g. `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Longer description of the carrier. |
| `getType()` | The carrier's structural type as a string (mirrors `getDataCarrierType().getCategory()`). |
| `getStandard()` | The symbology standard, where recorded. |
| `getDataCarrierType()` | The typed `DataCarrierType` for this entry — prefer this for programmatic routing. |
| `isGs1Capable()` | `true` if the carrier can hold GS1 data (AI element strings and/or Digital Link). |
| `isGs1AICapable()` | `true` if the carrier can hold GS1 AI element strings. |
| `isGs1DigitalLinkCapable()` | `true` if the carrier can hold a GS1 Digital Link URI. |
| `isEciCapable()` | `true` if the carrier supports an ECI indicator. |
| `isRequiresGtinPadding()` | `true` for EAN/UPC/ITF carriers whose numeric value is padded to GTIN-14 before AI parsing. |

#### DataCarrierType

A compile-time enum of data-carrier types, keyed by the AIM Code ID assigned in ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). The character after `]` (the *code character*) selects the family; most families map to a single constant covering every modifier (`ITF` covers `]I0`–`]I2`; `EAN_UPC` covers EAN-13, UPC-A, UPC-E and EAN-8). Where GS1 reserves a modifier for AI data, that variant is its own constant — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — distinct from their plain counterparts. When no AIM Code ID is present, or it names an unknown carrier, the type is `UNKNOWN`.

| Method | Description |
|---|---|
| `getCategory()` | The broad `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, or `OTHER`. |
| `getCodeChar()` | The AIM code character identifying the family, e.g. `"Q"` for QR Code; `null` for `UNKNOWN`. |
| `getDisplayName()` | Human-readable name of the *type* (may be broader than `DataCarrierEntry.getName()` — e.g. `"EAN-13 / UPC-A / UPC-E / EAN-8"` vs `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` for constants that always denote GS1 AI data: the four GS1-reserved variants (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) plus `GS1_DATABAR`, which is inherently GS1 since every `]e` modifier is GS1 DataBar. Narrower than `DataCarrierEntry.isGs1AICapable()` — a plain `QR_CODE` can still carry GS1 AI data. |
| `static forAimCodeId(String)` | Resolves a type directly from an AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); returns `UNKNOWN` for an absent, malformed or unrecognised ID. |

Routing by type rather than by name — e.g. splitting linear (Code-128) from 2D (QR / Data Matrix) symbols:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` covers the matrix and dot symbols only; the stacked-linear carriers (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) are `STACKED_LINEAR`, even though they are commonly
called "2D" barcodes. To treat both as one group — say, to decide whether an imager rather
than a laser scanner is needed — test for either category.

> Type resolution needs the AIM Code ID to be present in the scan; without it `getDataCarrier()` is `null` and the type is `UNKNOWN`. Configure the scanner to transmit the AIM Code ID prefix.

---

## Error Reference

| Code | Level | Stage | Meaning |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Unknown AI prefix — cannot determine data length |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Input too short to read a complete AI code |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Truncated value — fewer characters than the AI requires |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Duplicate Application Identifier in the element string |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Required AI dependency missing |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Excluded AI pairing — two AIs that cannot co-occur |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Unexpected tokenisation failure |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Character outside the GS1 encodable set in the element string |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Required FNC1 separator missing after a variable-length AI |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Trailing data beyond all component maximums |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | FNC1 separator after a fixed-length AI in mid-string position |
| `GE-W002` | WARNING | SYNTAX | Trailing FNC1 at end of element string (advisory only) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI structural violations — one code per condition (malformed URI, scheme, host, qualifier order, banned AI, no primary key (`GE-L013`), multiple primary keys (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Value fails the AI's regex pattern |
| `GE-C003` | DATA_ERROR | CONTENT | Check digit validation failure |
| `GE-C004` | DATA_ERROR | CONTENT | Check character pair validation failure |
| `GE-C005` | FORMAT_ERROR | CONTENT | Component value contains a character outside the allowed character set |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Component-format failures — one code per validator condition (see `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Custom semantic-validation failures — one code per validator condition (see `content/validator/`). **Exceptions:** the 14 GS1 company-prefix checks listed below carry level `WARNING`, and `GE-C168` (unrecognised ISO 3166-1 numeric country code) carries `FORMAT_ERROR`. |
| GS1 company-prefix checks | WARNING | CONTENT | Key does not begin with a recognised GS1 company prefix, on the GS1-key AIs — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Advisory only — does not affect validity. |
| `GE-C169` | DATA_ERROR | CONTENT | IMEI check digit (Luhn) failure on AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | EID check digit (Luhn) failure on AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Unrecognised AIM Code ID |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Carrier identified but supports neither GS1 AI element strings nor Digital Link URIs |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Unexpected internal error |

> **Known defect in message rendering.** The catalogue templates quote interpolated
> values with MessageFormat-style doubled apostrophes (`''{value}''`), but
> `ErrorRegistry` interpolates with plain `String.replace`, so the doubling survives into
> `getMessage()` — you will currently see `value ''09506000134351''` where the message
> texts quoted in this guide show `value '09506000134351'`. It affects every
> value-quoting message in all 35 language catalogues. Do not parse error messages;
> match on `getId()` / `getCode()`.

---

## Thread Safety

`GaiaParser` is thread-safe once constructed. A single instance may be shared across threads and used concurrently. The recommended pattern is to construct one instance at application startup and reuse it:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` is immutable and equally safe to share. The one thread-safety obligation the library cannot enforce for you is on [input modifiers](#input-modifiers): a single instance of each modifier is cached and shared across every concurrent parse, so implementations must be stateless.

---

## Appendix A — AI String Constants

`GS1Constants_AICodes` (in package `tools.pantheum.gaia.gs1.constants`) declares a `String` constant for every Application Identifier GAIA recognises. Use these constants rather than hard-coding raw AI code strings:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Each constant holds the string form of the AI code (e.g. `AI_01_GTIN = "01"`).

### Identification and serialisation

| AI | Constant | Description |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Serial Shipping Container Code (SSCC). |
| `01` | `AI_01_GTIN` | Global Trade Item Number (GTIN). |
| `02` | `AI_02_CONTENT` | GTIN of contained trade items. |
| `03` | `AI_03_MTO_GTIN` | Made-to-Order (MtO) trade item GTIN. |
| `10` | `AI_10_BATCH_LOT` | Batch or lot number. |
| `20` | `AI_20_VARIANT` | Internal product variant. |
| `21` | `AI_21_SERIAL` | Serial number. |
| `22` | `AI_22_CPV` | Consumer product variant. |
| `235` | `AI_235_TPX` | Third-party controlled serialised GTIN extension (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Additional product identification (manufacturer-assigned). |
| `241` | `AI_241_CUST_PART_NO` | Customer part number. |
| `242` | `AI_242_MTO_VARIANT` | Made-to-Order variation number. |
| `243` | `AI_243_PCN` | Packaging component number. |
| `250` | `AI_250_SECONDARY_SERIAL` | Secondary serial number. |
| `251` | `AI_251_REF_TO_SOURCE` | Reference to source entity. |
| `253` | `AI_253_GDTI` | Global Document Type Identifier (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | GLN extension component. |
| `255` | `AI_255_GCN` | Global Coupon Number (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variable count of items. |
| `37` | `AI_37_COUNT` | Count of trade items or pieces in a logistic unit. |

### Dates and times

| AI | Constant | Description |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Production date (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Due date (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Packaging date (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Best before date (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Sell-by date (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Expiration date (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Not-before delivery date/time (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Not-after delivery date/time (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Release date (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Expiration date and time (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | First freeze date (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Harvest date (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Test-by date (YYMMDD[hhmm]). |

### Quantity and measure — variable measure (metric)

The 4-digit AI families `310n`–`369n` encode variable-measure quantities. The third digit selects the measure type; the **fourth digit** (`n`, 0–5) is the number of implied decimal places — e.g. `AI_3102_NET_WEIGHT_KG` means net weight in kg with 2 decimal places.

| Family | Constant pattern (`n` = decimal-place digit) | Description |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Net weight, kilograms. |
| `311n` | `AI_311n_LENGTH_M` | Length / first dimension, metres. |
| `312n` | `AI_312n_WIDTH_M` | Width / second dimension, metres. |
| `313n` | `AI_313n_HEIGHT_M` | Depth / height / third dimension, metres. |
| `314n` | `AI_314n_AREA_M` | Area, square metres. |
| `315n` | `AI_315n_NET_VOLUME_L` | Net volume, litres. |
| `316n` | `AI_316n_NET_VOLUME_M` | Net volume, cubic metres. |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistic weight, kilograms. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Logistic length, metres. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Logistic width, metres. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Logistic depth / height, metres. |
| `334n` | `AI_334n_AREA_M_LOG` | Logistic area, square metres. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistic volume, litres. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistic volume, cubic metres. |
| `337n` | `AI_337n_KG_PER_M` | Kilograms per square metre. |

### Quantity and measure — variable measure (imperial / US)

| Family | Constant pattern (`n` = decimal-place digit) | Description |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Net weight, pounds. |
| `321n` | `AI_321n_LENGTH_IN` | Length / first dimension, inches. |
| `322n` | `AI_322n_LENGTH_FT` | Length / first dimension, feet. |
| `323n` | `AI_323n_LENGTH_YD` | Length / first dimension, yards. |
| `324n` | `AI_324n_WIDTH_IN` | Width / second dimension, inches. |
| `325n` | `AI_325n_WIDTH_FT` | Width / second dimension, feet. |
| `326n` | `AI_326n_WIDTH_YD` | Width / second dimension, yards. |
| `327n` | `AI_327n_HEIGHT_IN` | Depth / height, inches. |
| `328n` | `AI_328n_HEIGHT_FT` | Depth / height, feet. |
| `329n` | `AI_329n_HEIGHT_YD` | Depth / height, yards. |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistic weight, pounds. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Logistic length, inches. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Logistic length, feet. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Logistic length, yards. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Logistic width, inches. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Logistic width, feet. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Logistic width, yards. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Logistic depth / height, inches. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Logistic depth / height, feet. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Logistic depth / height, yards. |
| `350n` | `AI_350n_AREA_IN` | Area, square inches (variable measure). |
| `351n` | `AI_351n_AREA_FT` | Area, square feet (variable measure). |
| `352n` | `AI_352n_AREA_YD` | Area, square yards (variable measure). |
| `353n` | `AI_353n_AREA_IN_LOG` | Logistic area, square inches. |
| `354n` | `AI_354n_AREA_FT_LOG` | Logistic area, square feet. |
| `355n` | `AI_355n_AREA_YD_LOG` | Logistic area, square yards. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Net weight, troy ounces. |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Net weight / volume, ounces. |
| `360n` | `AI_360n_NET_VOLUME_QT` | Net volume, quarts. |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Net volume, gallons (US). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistic volume, quarts. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistic volume, gallons (US). |
| `364n` | `AI_364n_NET_VOLUME_IN` | Net volume, cubic inches. |
| `365n` | `AI_365n_NET_VOLUME_FT` | Net volume, cubic feet. |
| `366n` | `AI_366n_NET_VOLUME_YD` | Net volume, cubic yards. |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistic volume, cubic inches. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistic volume, cubic feet. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistic volume, cubic yards. |

### Pricing and monetary amounts

The fourth digit (`n`) encodes the number of implied decimal places. Its permitted range
differs per family — see the `n` column.

| Family | Constant pattern (`n` = decimal-place digit) | `n` | Description |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Amount payable / coupon value, local currency. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Amount payable with ISO currency code. |
| `392n` | `AI_392n_PRICE` | 0–9 | Amount payable, single monetary area (variable measure). |
| `393n` | `AI_393n_PRICE` | 0–9 | Amount payable with ISO currency code (variable measure). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Percentage discount of a coupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Amount payable per unit of measure, single monetary area. |

### Location and shipping

| AI | Constant | Description |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Customer purchase order number. |
| `401` | `AI_401_GINC` | Global Identification Number for Consignment (GINC). |
| `402` | `AI_402_GSIN` | Global Shipment Identification Number (GSIN). |
| `403` | `AI_403_ROUTE` | Routing code. |
| `410` | `AI_410_SHIP_TO_LOC` | Ship-to / Deliver-to GLN. |
| `411` | `AI_411_BILL_TO` | Bill-to / Invoice-to GLN. |
| `412` | `AI_412_PURCHASE_FROM` | Purchased-from GLN. |
| `413` | `AI_413_SHIP_FOR_LOC` | Ship-for / Forward-to GLN. |
| `414` | `AI_414_LOC_NO` | Physical location GLN. |
| `415` | `AI_415_PAY_TO` | Invoicing party GLN. |
| `416` | `AI_416_PROD_SERV_LOC` | Production or service location GLN. |
| `417` | `AI_417_PARTY` | Party GLN. |
| `420` | `AI_420_SHIP_TO_POST` | Ship-to postal code (single postal authority). |
| `421` | `AI_421_SHIP_TO_POST` | Ship-to postal code with ISO country code. |
| `422` | `AI_422_ORIGIN` | Country of origin. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Country of initial processing. |
| `424` | `AI_424_COUNTRY_PROCESS` | Country of processing. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Country of disassembly. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Country — full process chain. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Country subdivision of origin. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Ship-to company name. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Ship-to contact. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Ship-to address line 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Ship-to address line 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Ship-to suburb. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Ship-to locality. |
| `4306` | `AI_4306_SHIP_TO_REG` | Ship-to region. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Ship-to country code. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Ship-to telephone number. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Ship-to GEO location. |
| `4310` | `AI_4310_RTN_TO_COMP` | Return-to company name. |
| `4311` | `AI_4311_RTN_TO_NAME` | Return-to contact. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Return-to address line 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Return-to address line 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Return-to suburb. |
| `4315` | `AI_4315_RTN_TO_LOC` | Return-to locality. |
| `4316` | `AI_4316_RTN_TO_REG` | Return-to region. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Return-to country code. |
| `4318` | `AI_4318_RTN_TO_POST` | Return-to postal code. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Return-to telephone number. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Service code description. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Dangerous goods flag. |
| `4322` | `AI_4322_AUTH_LEAVE` | Authority to leave. |
| `4323` | `AI_4323_SIG_REQUIRED` | Signature required flag. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maximum temperature, Fahrenheit (hundredths of degrees). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maximum temperature, Celsius (hundredths of degrees). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimum temperature, Fahrenheit (hundredths of degrees). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimum temperature, Celsius (hundredths of degrees). |

### Product attributes and traceability

| AI | Constant | Description |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO Stock Number (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE meat carcass and cuts classification. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Active potency. |
| `7005` | `AI_7005_CATCH_AREA` | Catch area. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Species for fishery purposes. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Fishing gear type. |
| `7010` | `AI_7010_PROD_METHOD` | Production method. |
| `7020` | `AI_7020_REFURB_LOT` | Refurbishment lot ID. |
| `7021` | `AI_7021_FUNC_STAT` | Functional status. |
| `7022` | `AI_7022_REV_STAT` | Revision status. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | GIAI of an assembly. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Number of processor with three-digit ISO country code (10 slots). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC with Extension 1 and Importer index. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT freight unit type. |

### National Healthcare Reimbursement Numbers (NHRN)

| AI | Constant | Description |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Germany PZN. |
| `711` | `AI_711_NHRN_CIP` | France CIP. |
| `712` | `AI_712_NHRN_CN` | Spain CN. |
| `713` | `AI_713_NHRN_DRN` | Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | USA NDC. |
| `716` | `AI_716_NHRN_AIC` | Italy AIC. |
| `717` | `AI_717_NHRN_SRN` | Costa Rica Sanitary Register Number. |

### Healthcare, GMN, HIDRI, CPID, person data

| AI | Constant | Description |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Certification Reference (10 slots). |
| `7240` | `AI_7240_PROTOCOL` | Protocol ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC media type. |
| `7242` | `AI_7242_VCN` | Version Control Number (VCN). |
| `7250` | `AI_7250_DOB` | Date of birth (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Date and time of birth (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biological sex. |
| `7253` | `AI_7253_FAMILY_NAME` | Family name. |
| `7254` | `AI_7254_GIVEN_NAME` | Given name. |
| `7255` | `AI_7255_SUFFIX` | Name suffix. |
| `7256` | `AI_7256_FULL_NAME` | Full name. |
| `7257` | `AI_7257_PERSON_ADDR` | Address of person. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Baby birth sequence. |
| `7259` | `AI_7259_BABY` | Baby of family name. |
| `8001` | `AI_8001_DIMENSIONS` | Roll products — width, length, core diameter, direction, splices. |
| `8002` | `AI_8002_CMT_NO` | Cellular mobile telephone identifier. |
| `8003` | `AI_8003_GRAI` | Global Returnable Asset Identifier (GRAI). |
| `8004` | `AI_8004_GIAI` | Global Individual Asset Identifier (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Price per unit of measure. |
| `8006` | `AI_8006_ITIP` | Individual trade item piece (ITIP). |
| `8007` | `AI_8007_IBAN` | International Bank Account Number (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Date and time of production (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optically Readable Sensor Indicator. |
| `8010` | `AI_8010_CPID` | Component / Part Identifier (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | CPID serial number. |
| `8012` | `AI_8012_VERSION` | Software version. |
| `8013` | `AI_8013_GMN` | Global Model Number (GMN). |
| `8014` | `AI_8014_MUDI` | Highly Individualised Device Registration Identifier (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | GSRN — service provider. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | GSRN — service recipient. |
| `8019` | `AI_8019_SRIN` | Service Relation Instance Number (SRIN). |
| `8020` | `AI_8020_REF_NO` | Payment slip reference number. |
| `8026` | `AI_8026_ITIP_CONTENT` | ITIP contained in a logistic unit. |
| `8030` | `AI_8030_DIGSIG` | Digital Signature (DigSig). |
| `8040` | `AI_8040_IMEI` | International Mobile Equipment Identity (IMEI). |
| `8041` | `AI_8041_IMEI2` | IMEI2. |
| `8042` | `AI_8042_ESIM` | Embedded SIM number. |
| `8043` | `AI_8043_PSIM` | Physical SIM number. |
| `8110` | `AI_8110` | Coupon code identification (North America). |
| `8111` | `AI_8111_POINTS` | Loyalty points of a coupon. |
| `8112` | `AI_8112` | Positive offer file coupon code (North America). |
| `8200` | `AI_8200_PRODUCT_URL` | Extended Packaging URL. |

### Internal / company use

| AI | Constant | Description |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Mutually agreed trading partner information. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Company internal information (9 slots). |

---

## Appendix B — Interpretation Key Constants

When `GaiaParser.parse()` is called with `ParseMode.INTERPRETATION`, each `GS1AIObjectElement` may carry a list of `GS1AIInterpretation` objects produced by domain-specific enrichers. Use the constants from `GS1Constants_Enricher` (in package `tools.pantheum.gaia.gs1.constants`) as keys to look up specific interpretation values:

```java
GS1AIObjectElement el = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Look up a single interpretation by type constant
GS1AIInterpretation fmt = el.getInterpretation(GS1Constants_Enricher.GTIN_TYPE);
if (fmt != null) System.out.println("GTIN type: " + fmt.getValue());

// Or iterate all interpretations
for (GS1AIInterpretation interp : el.getInterpretations()) {
    System.out.println(interp.getType() + " = " + interp.getValue());
}
```

Display labels are **not** constants — they live in the localized catalogues under `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, keyed by the type constant. `GS1AIInterpretation.getLabel()` returns the label for the parse language (see [Localized messages and labels](#localized-messages-and-labels)), falling back to English when a catalogue omits the key. The "Display label" column below lists the English text; the type keys themselves are stable across languages, so match on the key, never on the label.

### Date and time

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `DATE_VALUE` | Date | Date AIs (11–17, 7003, 7006, 7011, etc.) |
| `DATE_FORMAT` | Date format | Date AIs |
| `TIME_VALUE` | Time | Time-bearing AIs (7003, 7011, 8008, etc.) |
| `TIME_FORMAT` | Time format | Time-bearing AIs |
| `DATETIME_VALUE` | Date and time | Date+time AIs |
| `DATETIME_FORMAT` | Date and time format | Date+time AIs |

### Harvest date

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Harvest start date | AI 7007 |
| `HARVEST_END_DATE` | Harvest end date | AI 7007 (optional range end) |
| `HARVEST_DATE_RANGE` | Harvest date range | AI 7007 |

### GS1 Company Prefix

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 company prefix | GTIN / GLN / SSCC AIs |
| `GS1_MEMBER_CODE` | GS1 member code | GTIN / GLN / SSCC AIs |
| `GS1_MEMBER_NAME` | GS1 member organisation | GTIN / GLN / SSCC AIs |

### GTIN

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN type | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Packaging level | AI 01 |
| `GTIN_CHECK_DIGIT` | Check digit | AI 01, 02 |

### SSCC

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Extension digit | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Serial reference | AI 00 |
| `SSCC_CHECK_DIGIT` | Check digit | AI 00 |

### Country (ISO 3166)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Country code (numeric) | Single-country AIs (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Country code (alpha-2) | Alpha-2 country AIs |
| `COUNTRY_NAME` | Country name | Single-country AIs |
| `COUNTRY_LIST` | Countries | AI 423 — all names joined, e.g. `Australia, New Zealand` |

AI 423 (country of initial processing) can carry up to five countries, so it emits one
**numbered pair per country** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — followed by the single `COUNTRY_LIST`
summary. Build these keys from the `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX`
constants plus the 1-based index, or simply iterate `getInterpretations()`; the
unsuffixed `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` keys are **not** emitted for AI 423.

### Currency (ISO 4217)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Currency code | Amount AIs with currency (391n, 393n) |
| `CURRENCY_ALPHA` | Currency alpha code | Amount AIs with currency |
| `CURRENCY_NAME` | Currency name | Amount AIs with currency |

### Temperature

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperature | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Temperature unit | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperature formatted | AI 4330–4333 |

### Sex (ISO 5218)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `SEX_CODE` | Sex code | AI 7252 |
| `SEX_DESCRIPTION` | Sex description | AI 7252 |

### Aquatic species (FAO)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Species code | AI 7008 |
| `SPECIES_SCIENTIFIC` | Scientific name | AI 7008 |
| `SPECIES_ENGLISH` | Common name | AI 7008 |
| `SPECIES_FAMILY` | Family | AI 7008 |
| `SPECIES_ORDER` | Order | AI 7008 |

### NATO Stock Number (NSN)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `NSN_FSG` | Supply group | AI 7001 |
| `NSN_FSG_NAME` | Supply group name | AI 7001 |
| `NSN_FSCG` | Supply class | AI 7001 |
| `NSN_FSCG_NAME` | Supply class name | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Country code | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Country | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO country code | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS category | AI 7001 |
| `NSN_NIIN` | National Item Identification Number | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Roll products

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Roll width (mm) | AI 8001 |
| `ROLL_LENGTH` | Roll length (m) | AI 8001 |
| `CORE_DIAMETER` | Core diameter (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Winding direction code | AI 8001 |
| `WINDING_DIRECTION` | Winding direction | AI 8001 |
| `SPLICES` | Splices | AI 8001 |

### IBAN

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Country code | AI 8007 |
| `IBAN_COUNTRY_NAME` | Country | AI 8007 |
| `IBAN_CHECK_DIGITS` | Check digits | AI 8007 |
| `IBAN_CHECK_VALID` | Check | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Serial number | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Check digit | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Reporting body | AI 8040, 8041 |

The 15 digits decompose as `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`, with the
RBI being the leading 2 digits of the TAC — so `IMEI_RBI` is a prefix of `IMEI_TAC`, not
a separate span. `IMEI_FORMATTED` renders the standard GSMA display grouping
`AA-BBBBBB-CCCCCC-D` (e.g. `49-015420-323751-8`), which splits the TAC at the RBI
boundary; the legacy `6-2-6-1` grouping, which cuts where the discontinued Final Assembly
Code used to begin, is not emitted.

`IMEI_RBI_NAME` resolves the RBI to the allocating body's name via `ImeiRbiData`, and is
**appended last and only when the code is listed there**. That table covers three groups:

- **Currently allocating** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, plus `99`
  Global Hexadecimal Administrator and `98` (reserved).
- **Test ranges** — `00` and `02`–`09`, marking test IMEIs rather than a real allocation.
  Query with `ImeiRbiData.isTestCode(code)`.
- **No longer allocating** — historic bodies such as `49` (BZT/BAPT, Germany), `44`
  (BABT, UK) or `91` (MSAI, India). Query with `ImeiRbiData.isNoLongerAllocating(code)`.
  Devices carrying these codes are ordinary and remain in service; only new allocation
  has stopped, so this is reporting information, never a validity signal.

A missing `IMEI_RBI_NAME` means "this RBI is not in our table", **not** "invalid IMEI":
the table is compiled from a published RBI listing rather than the GSMA directly, so it
can lag newly appointed bodies. Do not derive any validation outcome from its absence;
the RBI is not a check character. Code that walks the interpretation list must also
tolerate its absence rather than indexing positionally.

### SIM identifiers (EID / ICCID)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Industry category | AI 8042 |
| `EID_BODY` | EID body | AI 8042 |
| `EID_CHECK_DIGIT` | Check digit | AI 8042 |
| `ICCID_BODY` | ICCID body | AI 8043 |
| `ICCID_EXTENSION` | Extension | AI 8043 |

`SIM_MII` carries the leading **two** digits (`89`), the pair ITU-T E.118 assigns to
telecommunications. ISO/IEC 7812 itself defines the MII as the **first digit only**, so
`SIM_MII_NAME` resolves the category from that leading `8` via `Iso7812Data` — yielding
"Healthcare, telecommunications and other future industry assignments". For a well-formed
EID this is therefore constant; it is reported for traceability to the standard, not as a
discriminator. `Iso7812Data.nameForCode(digit)` takes a bare digit,
`nameForIdentifier(prefix)` accepts a longer prefix and reads its leading digit.

`SIM_MII_NAME` is emitted by `EidEnricher` (AI 8042) only. `IccidEnricher` (AI 8043)
surfaces `SIM_MII` without the category.

### Certification reference

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Sequence number | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Certification scheme code | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Certification scheme | AI 7230–7239 |
| `CERT_REFERENCE` | Certification reference | AI 7230–7239 |

### GS1 UIC

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC code | AI 7040 |
| `UIC_EXTENSION_1` | Extension 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Importer index | AI 7040 |

### Baby birth sequence

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Birth position | AI 7258 |
| `BIRTH_TOTAL` | Total births | AI 7258 |
| `BIRTH_SEQUENCE` | Birth sequence | AI 7258 |

### Global Model Number (GMN)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Model reference | AI 8013 |
| `GMN_CHECK_PAIR` | Check pair | AI 8013 |

### HIDRI

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Device reference | AI 8014 |
| `HIDRI_CHECK_PAIR` | Check pair | AI 8014 |

### CPID

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Component & Part reference | AI 8010–8011 |

### Decimal and measurement values

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Decimal value | Numeric AIs with implied decimal places (31xx–36xx) |
| `DECIMAL_AMOUNT` | Amount | Pricing AIs (390n–395n) |
| `DECIMAL_PERCENTAGE` | Percentage | AI 394n |
| `DECIMAL_PLACES` | Decimal places | Alongside `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Percentage format | AI 394n |
| `ISO_UNIT_CODE` | ISO unit code | Measurement AIs |
| `ISO_UNIT_NAME` | ISO unit name | Measurement AIs |
| `MONETARY_AMOUNT` | Monetary amount | Pricing AIs |
| `MONETARY_AMOUNT_DISPLAY` | Monetary amount (formatted) | Pricing AIs |

### Geo coordinates

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `LATITUDE` | Latitude | AI 4309 |
| `LONGITUDE` | Longitude | AI 4309 |
| `GEO_COORDINATES` | Geo coordinates | AI 4309 |
| `LATITUDE_DMS` | Latitude (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitude (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Geo coordinates (DMS) | AI 4309 |

### Production method

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Production method code | AI 7010 |
| `PRODUCTION_METHOD` | Production method | AI 7010 |

### AIDC media type

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC media type code | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC media type | AI 7241 |

### Piece of total

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Piece number | AI 8006 |
| `PIECE_TOTAL` | Total pieces | AI 8006 |
| `PIECE_OF_TOTAL` | Piece of total | AI 8006 |

### Component splits

Keys emitted by the declarative component splits in `content/ai-content.json` rather than
by a Java enricher — they surface the named parts of a composite AI value. Unlike every
other key in this appendix, these have **no constant in `GS1Constants_Enricher`**: match
the literal string, or read the type off `GS1AIInterpretation.getType()`.

| Type key | Display label | Produced by |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Check digit | AIs 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Serial number | AIs 253, 255, 8003 |
| `POSTAL_CODE` | Postal code | AI 421 |
| `PROCESSOR_ID` | Processor identifier | AIs 7030–7039 |

Note `CHECK_DIGIT` here is the generic component-split key, distinct from the
enricher-specific `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` and
`EID_CHECK_DIGIT` keys listed above.

### Miscellaneous

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Value | Boolean / flag AIs (4321–4323) |
| `DECODED_TEXT` | Decoded text | Free-text AIs |
