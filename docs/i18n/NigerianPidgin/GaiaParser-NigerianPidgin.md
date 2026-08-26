# GAIA (GS1 Application Identifiers Analyser) — Developer Guide

## Wetin Dey Inside

1. [Overview](#overview)
2. [About GS1 and di General Specifications](#about-gs1-and-di-general-specifications)
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
   - [Messages and labels wey dem localize](#messages-and-labels-wey-dem-localize)
   - [Date formatting](#date-formatting)
7. [Input Modifiers](#input-modifiers)
   - [Built-in modifiers](#built-in-modifiers)
   - [How to write modifier](#how-to-write-modifier)
   - [Registering modifiers](#registering-modifiers)
   - [How to see wetin di modifier do](#how-to-see-wetin-di-modifier-do)
   - [How dem dey handle modifier failure](#how-dem-dey-handle-modifier-failure)
8. [Parse Modes](#parse-modes)
   - [DATA_CARRIER mode](#data_carrier-mode)
   - [SYNTAX mode](#syntax-mode)
   - [CONTENT mode](#content-mode)
   - [INTERPRETATION mode (di default)](#interpretation-mode-di-default)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [How to work wit di Results](#how-to-work-wit-di-results)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry and DataCarrierType](#datacarrierentry-and-datacarriertype)
12. [Error Reference](#error-reference)
13. [Thread Safety](#thread-safety)
14. [Appendix A — AI String Constants](#appendix-a--ai-string-constants)
    - [Identification and Serialisation](#identification-and-serialisation)
    - [Dates and Times](#dates-and-times)
    - [Quantity and Measure — Variable Measure (Metric)](#quantity-and-measure--variable-measure-metric)
    - [Quantity and Measure — Variable Measure (Imperial / US)](#quantity-and-measure--variable-measure-imperial--us)
    - [Pricing and Monetary Amounts](#pricing-and-monetary-amounts)
    - [Location and Shipping](#location-and-shipping)
    - [Product Attributes and Traceability](#product-attributes-and-traceability)
    - [National Healthcare Reimbursement Numbers (NHRN)](#national-healthcare-reimbursement-numbers-nhrn)
    - [Healthcare, GMN, HIDRI, CPID and Person Data](#healthcare-gmn-hidri-cpid-and-person-data)
    - [Internal / Company Use](#internal--company-use)
15. [Appendix B — Interpretation Key Constants](#appendix-b--interpretation-key-constants)
    - [Date and Time](#date-and-time)
    - [Harvest Date](#harvest-date)
    - [GS1 Company Prefix](#gs1-company-prefix)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Country (ISO 3166)](#country-iso-3166)
    - [Currency (ISO 4217)](#currency-iso-4217)
    - [Temperature](#temperature)
    - [Sex (ISO 5218)](#sex-iso-5218)
    - [Aquatic Species (FAO)](#aquatic-species-fao)
    - [NATO Stock Number (NSN)](#nato-stock-number-nsn)
    - [Roll Products](#roll-products)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM Identifiers (EID / ICCID)](#sim-identifiers-eid--iccid)
    - [Certification Reference](#certification-reference)
    - [GS1 UIC](#gs1-uic)
    - [Infant Birth Sequence](#infant-birth-sequence)
    - [Global Model Number (GMN)](#global-model-number-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Decimal and Measurement Values](#decimal-and-measurement-values)
    - [Geographic Coordinates](#geographic-coordinates)
    - [Production Method](#production-method)
    - [AIDC Media Type](#aidc-media-type)
    - [Piece of Total](#piece-of-total)
    - [Component Splits](#component-splits)
    - [Miscellaneous](#miscellaneous)

---

## Overview

`GaiaParser` na di entry point wey you dey use parse GS1 Application Identifier (AI) element string. E dey accept di raw output wey scanner give, for any of di forms wey dey below, and e go return one structured `ParseResult` wey carry every AI wey e resolve, di validation errors, and — if you want am — interpretations wey human being fit read:

- Plain AI element string: `0109506000134352`
- Element string wey get AIM Symbology Identifier for front: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Any of dem wey dey up, wit optional 8-digit correlation ID for front: `12345678~0109506000134352`

**Entry-point class:** `tools.pantheum.gaia.GaiaParser`

> **You just dey start wit Gaia?** Begin from **[GaiaParser Quick Start](GaiaParser-QuickStart-NigerianPidgin.md)** — ten minutes wey go carry you through di dependencies, your first parse, and small-small wahala wey dey trip people. Dis guide na di complete reference.

> Di opposite direction — to *build* element string and Digital Link URI wey correct, from AI/value pairs — na **[GaiaBuilder — Developer Guide](GaiaBuilder-NigerianPidgin.md)** dey cover am.

---

## About GS1 and di General Specifications

**GS1** na global non-profit organisation wey dey develop and maintain open standards for supply-chain identification and data exchange. Dem dey use im standards for retail, healthcare, logistics, foodservice, and plenty other industries — everything from di product barcode wey dey consumer packaging, reach di serialised tracking of pharmaceutical doses.

Di authoritative reference for everything wey dis parser dey implement na di **GS1 General Specifications** — one single document wey define:

- Every Application Identifier (AI) code, dia data titles, formats, and validation rules
- Di syntax rules for how to build and encode AI element string
- Barcode symbology requirements and AIM Code ID assignments
- Check digit and check character algorithms
- How dem dey resolve two-digit year (di sliding-window rule)
- Data Matrix, QR Code, GS1-128, GS1 DataBar, and other carrier specifications

Dem dey update di GS1 General Specifications every year. Di current edition and di materials wey follow am dey here:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA dey implement **Release 26.0 (Ratified, Jan 2026)** of di GS1 General Specifications.

Na one companion standard, **GS1 Digital Link: URI Syntax**, dey govern GS1 Digital Link URI. Na im define di primary identification keys, di order wey key qualifiers go follow, and di data-attribute encoding wey di parser dey apply to Digital Link input:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA dey implement **Release 1.7.0 (Ratified, Aug 2026)** of di GS1 Digital Link: URI Syntax standard.

Di section references wey dey dis document dey point to di GS1 General Specifications (like "Table 7-5", "section 7.12"), except di Digital Link section numbers (like "§4.9", "§4.12") wey dey point to di GS1 Digital Link: URI Syntax standard.

---

## GS1 Application Identifiers

**GS1 Application Identifier (AI)** na short numeric prefix — two reach four digits — wey dey identify di meaning and di format of di data wey follow am sharp-sharp. Na di GS1 General Specifications define di AIs, and dem cover plenty kind supply-chain data: product identifiers, dates, quantities, lot numbers, serial numbers, measurements, URLs, and plenty more.

### How AI element take dey structured

Every AI element get two parts:

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

Di AI code na numeric always. Di data value dey follow am sharp-sharp, and no delimiter dey between di code and di value.

### Fixed-length AI versus variable-length AI

AI dey fall into two categories:

| Type | How e dey behave | Example |
|---|---|---|
| **Fixed-length** | Exact number of characters, e dey consume dem complete every time | AI `01` (GTIN) — na 14 digits always |
| **Variable-length** | From 1 reach one maximum number; e go end for GS separator or for end of input | AI `10` (Batch/Lot) — 1 reach 20 alphanumeric characters |

Na only di definition wey dey di GS1 specification dey decide whether AI na fixed or variable — di parser no dey guess at all.

### Element string wey carry plenty AI

You fit join plenty AI put for one element string. Fixed-length AI fit join direct, because di parser sabi exactly how many characters e go consume. But variable-length AI must end wit di **GS character** (ASCII `0x1D`, wey dem dey also call FNC1 for barcode symbologies) any time another AI dey follow am, so dat di parser go sabi where one value end and where di next AI code start.

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

For Java string literals, write di GS character wit di Unicode escape `""`.

### AI wey people dey commonly use

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

> Di **fourth digit** of any 4-digit measure or price AI dey encode how many implied decimal places dey — `3103` na net weight for kg wit 3 decimals (`001500` = 1.500 kg), while `3102` go read di same digits as 15.00 kg. Di `Format` column wey dey up dey show di format of di *data*; but di full `getFormatString()` of each AI dey include di AI itself (like `N4+N6` for `3103`).

### Human Readable Interpretation (HRI)

Di conventional form wey person fit read dey wrap every AI code inside bracket, just before im value, and e dey put space between di elements:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Di GS separator no dey show for HRI. Na `GS1AIObject.toHriString()` dey produce dis format.

### AI code wey get four digits

Some AI dey use four digits instead of two. Di first two digits dey identify di AI family; di third and/or fourth digit dey carry extra meaning (like where di implied decimal point dey for measurement AI). Di parser dey resolve di full AI code from di element string by itself — di person wey dey call am dey always work wit di full code (like `"3102"`, no be just `"31"`).

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

> **GS separator:** Variable-length AI wey dey inside string wey carry plenty AI must get GS character (ASCII `0x1D`) wey separate dem. Use `""` for Java string literals.

---

## Parsing Pipeline

### Pre-stage — Input Modifiers

If di `ParseConfig` carry any **input modifier**, dem go run before anything else — before correlation stripping, before carrier detection, before di GS1 pipeline even start. Every modifier dey rewrite di raw input for di next one, and every stage wey dey below dey work on wetin di chain produce.

By default no modifier dey configured, so dis pre-stage no dey do anything unless you opt in. See [Input Modifiers](#input-modifiers).

---

### Stage 0 — Correlation ID

Before any GS1 processing at all, `GaiaParser` dey check whether di input start wit optional **correlation ID prefix**: exactly 8 ASCII decimal digits wey tilde (`~`) follow, like `12345678~`.

If di prefix dey, e go strip am comot and store am as `CorrelationInfo` for di `ParseResult` wey e return. Every stage wey follow dey work on di payload wey dem don strip. If no prefix dey, di input just dey pass through as e be.

See [Correlation ID](#correlation-id) for di full details.

---

### Stage 1 — Input Routing

After di correlation stripping, `GaiaParser` dey check whether di (stripped) input start wit **AIM Code ID**: three-character prefix wey get di form `]` + ASCII letter + ASCII digit (like `]C1` for GS1-128, `]d2` for GS1 DataMatrix, `]e0` for GS1 DataBar / GS1 Composite).

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

If di carrier no fit carry GS1 AI (like postal barcode), parsing go stop sharp-sharp wit `GE-D002` error.

---

### Stage 2 — Syntax

Dis one dey run every time. E get two sub-steps:

**2a. Tokenisation (`AISyntaxParser`)**
- E dey read di AI code length from di first two characters, using di GS1 prefix table (GS1 General Specifications Table 7-5).
- Fixed-length AI dey consume exact byte count from di input.
- Variable-length AI dey read reach GS character or reach end of input.
- For AI wey get plenty component, dem dey slice di value blob into per-component segments.

**2b. Structural validation (`SyntaxValidator`)**
- E dey check for AI wey repeat (`GE-S004`).
- E dey check di AI dependencies wey required, like say AI `02` need AI `37` (`GE-S005`).
- E dey check AI pairings wey dem exclude (`GE-S006`).

Error for dis stage get level `SYNTAX_ERROR` (tokeniser) or `INTEGRITY_ERROR` (structural). If **any** error dey — whether na tokeniser own or structural own — di pipeline go stop, and dem go skip di content and interpretation stages.

---

### Stage 3 — Content

Dis one dey run only if Stage 2 no produce any error at all (no tokeniser own, no structural own). Di per-element pipeline be dis (every step dey run only if di one before am no produce error):

| Step | Validator | Error Codes |
|---|---|---|
| Regex check | `RegexValidator` | `GE-C001` |
| Component charset + format | `ComponentValidator` | `GE-C005` + per-condition format codes (`GE-C054`–`GE-C115`) |
| Check digit / check character | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Custom semantic validation | `ContentValidatorRegistry` | per-condition content codes (`GE-C116`–`GE-C170`) |

Error for dis stage get level `FORMAT_ERROR` or `DATA_ERROR`, but one exception dey: di GS1
company-prefix checks wey dey run on di GS1-key AI na advisory, and dem carry level `WARNING`
(see [Error Reference](#error-reference)), so company prefix wey dem no recognise no fit
by itself make di result invalid.

---

### Stage 4 — Interpretation

Dis one dey run only for `INTERPRETATION` mode, and only if no element carry error from any stage before am. `InterpretationEngine` dey enrich every element wit metadata wey get label:

- Dates wey dem reformat as `dd/mm/yyyy`
- GTIN check digit breakdown and GS1 company prefix lookup
- ISO 3166 country names
- ISO 4217 currency names and symbols
- Decimal amounts wey dem decode
- HRI (Human Readable Interpretation) fragments

Dem dey attach di results as `GS1AIInterpretation` entries for every `GS1AIObjectElement`.

---

## Parse Configuration (`ParseConfig`)

`GaiaParser` dey expose exactly two entry points:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` dey run wit di **default configuration**: `INTERPRETATION` mode, little-endian dates (`dd/mm/yyyy`) wit `/` separator and four-digit year, and error messages for **English**. If you wan change any of dem — including di parse mode — build one `ParseConfig` wit im fluent builder, then use di overload wey take two arguments.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Na inside `GaiaConstants` all di option enums dey.

### Options

| Builder method | Enum (`GaiaConstants`) | Default | Wetin e dey do |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | How deep di pipeline go go — see [Parse Modes](#parse-modes). |
| `language(...)`      | `Language`      | `ENGLISH`        | Di language of error messages, interpretation labels, **and** AI descriptions. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Di order of date components: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Di character wey dey between date components: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) or `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) or `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | E dey skip di structural "requires" check (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | E dey skip di structural "excludes" check (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / class name | none | Code wey dey rewrite di raw input before parsing — di two [built-in modifiers](#built-in-modifiers) plus anything wey you write. See [Input Modifiers](#input-modifiers). |

Di four date options dey affect only di formatted date strings wey interpretation enrichers dey produce (for `INTERPRETATION` mode); dem no dey change validation at all. You fit leave out builder values — any option wey you no set (or wey you pass `null`) go keep im default.

### Messages and labels wey dem localize

`language(...)` dey pick di language for **three** kinds of text wey human being dey read: error messages, interpretation labels (di `getLabel()` of every `GS1AIInterpretation`), and AI descriptions (di `getDescription()` of every `GS1AIObjectElement`).

`GaiaConstants.Language` define **35 languages**, wey cover di languages wey plenty people for di world dey speak: English, French, Spanish, German, Italian, Portuguese, Dutch, Polish, Russian, Ukrainian, Czech, Swedish, Chinese, Japanese, Korean, Arabic, Indonesian, Hindi, Turkish, Bengali, Urdu, Vietnamese, Nigerian Pidgin, Egyptian Arabic, Marathi, Telugu, Tamil, Cantonese, Wu Chinese, Tagalog, Persian, Hausa, Punjabi, Javanese, and Swahili.

Di translation state (as dem ship am):
- **Interpretation labels** — dem don translate am for every language.
- **Error messages** — dem don translate am for every language.
- **AI descriptions** — dem don translate am for every language except English. English no be separate catalogue: dem dey read am direct from di `description` field of dat AI entry inside `gs1-application-identifiers.jsonld`, and na dia every AI description dey fall back to for last.

Nigerian Pidgin (`NIGERIAN_PIDGIN`), wey be creole wey stand on English, dey deliberately reuse di English text for interpretation labels and error messages. AI descriptions na di exception to dat exception: dem translate am into real Pidgin phrasing instead of reusing English, because dem produce di AI-description catalogues separate from di label/message catalogues. Make native speakers review machine translations before you trust dem for production.

Any message, label, or description wey no dey inside one language catalogue go fall back to English. Di right-to-left languages (Arabic, Urdu, Egyptian Arabic, Persian) dey stored correctly as strings; na di display layer get di work of rendering dem RTL.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Interpretation labels dey localize di same way (di values no dey change — na di labels only):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI descriptions dey localize di same way (na only `getTitle()`, like `"GTIN"`, wey no dey localized):

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

**Input modifier** na code wey dey rewrite di raw input string before Gaia parse am. Modifier dey exist because of input wey don already scatter before e reach — scanner wey dey put printable placeholder for di GS separator, middleware wey dey wrap di payload inside vendor prefix, host system wey dey turn everything to capital letter. Instead make you dey pre-process every string for every place wey you dey call am (and then get am small-small wrong for one of dem), register di normalisation once for di `ParseConfig` and make di parser apply am.

Modifier dey run for di very beginning of `GaiaParser.parse(...)` — before dem strip correlation ID, before dem detect AIM Code ID, before di GS1 pipeline. Everything wey dey downstream dey see only di string wey dem don rewrite. **Nothing dey configured by default**, including di two [built-in modifiers](#built-in-modifiers) — na you go opt in for each `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Built-in modifiers

Two modifiers dey ship inside di core jar, for `tools.pantheum.gaia.modifier.custom`. Dem cover di two ways wey GS1 payload dey most commonly arrive scatter-scatter — printed HRI brackets wey dem treat as data, and space wey no suppose dey — so di common cases no need any custom class:

| Class | `getName()` | Wetin e dey do |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | E dey strip di HRI brackets wey surround every AI (`(01)…(10)…`), and e dey restore di FNC1 separator wey dem imply. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | E dey remove every space (`0x20`) from di AI element string. |

Di two of dem na ordinary `ModifierInterface` implementations, dem no get any special status — dem dey register dem, order dem, report dem, and dem dey fail exactly like your own:

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

Di two of dem no dey hold state and dem safe for thread, so one single instance fit serve everybody; and you fit address di two of dem by dia fully-qualified class name for setup wey configuration file dey drive (see [Registering modifiers](#registering-modifiers)).

#### `ModifierRemoveAIBrackets`

Di GS1 human-readable interpretation dey print every AI inside bracket — `(01)09521234543213(10)ABC123` — na purely printing convention. Any scanner or middleware wey dem configure to emit di HRI go pass dose brackets through as data, and di tokeniser no sabi wetin e go do wit dem.

To strip di brackets na only half of di work. For HRI, na di opening `(` of di *next* AI dey mark where di previous value end, so for bracket form, variable-length AI no need FNC1. Remove di brackets anyhow and dat boundary go just disappear:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

So di modifier dey **put FNC1 back for every boundary wey di AI wey dey before am na variable-length**, and e dey restore exactly wetin di brackets bin encode:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

E dey look di length up inside di parser own `AiDefinitionRegistry`, so every variable-length AI dey handled, no be some list wey dem hard-code. Three cases dem deliberately no touch: value wey don already end wit FNC1 (source wey dey emit di two conventions no go collect second separator), bracketed code wey no be AI wey dem sabi (AI wey dem no know no talk anything about im own length), and di last AI wey dey di string.

Di rewrite na **idempotent** — run am on im own output and nothing go change — so e safe for mixed feed where na only some of di input get bracket.

> **Limitation.** `(` and `)` na valid GS1 data characters demselves, and di pattern na just `\((\d{2,4})\)`. If any value happen to carry two-to-four-digit number inside bracket, dem go unwrap dat one too. Apply dis one only to source wey dey use di HRI bracket convention, no be to source wey get genuine bracketed values.

#### `ModifierRemoveSpaces`

Some scanners, middleware, and label-print pipelines dey insert space wey no suppose dey inside element string wey for dey correct — to pad fixed-width field, to separate groups wey person go read, or to wrap long value. Di tokeniser dey treat every one of dem as data, e dey corrupt di value wey e sit inside, and for variable-length AI, e dey shift everything wey follow.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Na only ASCII `0x20` dem dey remove. Other whitespace dey remain for im place — tab, for example, dey outside di GS1 encodable set, so di parser dey report am as `GE-S008` instead of make e just sweep am comot quietly.

> **Limitation.** Space (`0x20`) na part of di GS1 invariant character set, so batch/lot or customer part number fit legitimately carry one inside. Di modifier no fit tell di difference between space wey no suppose dey and genuine one; apply am only to source wey you know say e no dey use space inside im AI values.

#### Prefix dem dey skip, dem no dey rewrite am

Modifier dey run before di parser strip anything, so di raw input fit still dey carry correlation ID, AIM Code ID, and ECI indicator. Di two built-ins dey locate where di AI element string start, using di parser own `CorrelationIdParser` and `DataCarrierParser` logic; dem dey rewrite only from dia forward, then dem dey splice di result back onto di prefix wey dem **never touch**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

EAN/UPC carriers wey dem pad dia value reach GTIN-14 (`isRequiresGtinPadding()`) — dem dey skip dem completely. Dia payload na raw numeric barcode value wey no get AI structure, so neither bracket nor space fit mean anything dia.

#### Ordering: space first, bracket after

When you dey use di two, **register `ModifierRemoveSpaces` first**. Bracket matching dey sensitive to position: `( 01 )` wey space don pad no dey match `\((\d{2,4})\)`, so di brackets go survive, and di separator wey dem imply no go ever come back.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### How to write modifier

Write your own when di two built-ins no fit do di work — di interface na one method.

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

If di rewrite depend on di parse configuration, override di overload wey take two arguments instead:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Di contract:

| Rule | Detail |
|---|---|
| No hold state, and safe for thread | Dem dey cache one instance per class, and every parse dey share am. |
| Public constructor wey no take argument | Na only when you dey reference di modifier by class name e dey required. |
| Handle `null` and empty input | Di parser no dey filter dem comot before di chain run. |
| Return `null` mean say "nothing change" | Di previous value dey carry forward. Return `input` as e be when di modifier no apply. |
| Better make you return am unchanged pass make you throw | Modifier wey throw dey abort di parse — see [Failure handling](#how-dem-dey-handle-modifier-failure). |
| `getName()` | Override am to control di name wey dem go report for `ModifierInfo`; di default na di simple class name. |

### Registering modifiers

Modifier dey run for di order wey you add dem, and each one dey collect di output of di one before am. Register dem by instance, by fully-qualified class name, or as list of either one:

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

Dem dey name di [built-in modifiers](#built-in-modifiers) di same way wey dem dey name your own — **fully qualified always**. No short name or alias lookup dey for dem; `ModifierRegistry` dey resolve every modifier, whether e ship wit di jar or no, by di full class name.

Na `ModifierRegistry` dey resolve di names. E dey instantiate every class once through im no-argument constructor, then e dey cache dat instance for every later config wey name di same class. Di resolution dey happen **when dem dey build di config**, so any name wey dem no fit find, wey no implement `ModifierInterface`, or wey dem no fit instantiate, go throw `IllegalArgumentException` right dia — no be quietly for parse time. Any modifier wey reflection no fit build (say e dey hold one dependency wey dem inject) — you fit pre-register am so dat e still dey addressable by name:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### How to see wetin di modifier do

When modifier dey configured, `ParseResult.getPayload()` dey reflect di input wey dem **modify**. Di original dey preserved for `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` dey report di `getName()` of every modifier. Im default na di simple class name, but di two built-ins dey override am — so chain of di two dey report di display names, no be di class names:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` dey return `null` when no modifier dey configured. When modifier run but every one of dem return di input as e be, di info still dey and `isModified()` na `false` — na only modifier wey actually change di input dey appear inside `getAppliedModifiers()`.

### How dem dey handle modifier failure

Modifier wey throw dey abort di parse. Dem dey wrap di exception inside `GaiaModifierException` wey name di modifier wey cause am, and di result dey carry `GE-I001` internal error whose message include dat name; `getPayload()` dey report di input wey dem never modify. Di parse deliberately **no** dey continue wit string wey dem rewrite half way — normalisation step wey fail quietly go produce results wey go look valid but wey dem parse from di wrong input.

---

## Parse Modes

Every mode carry di name of di deepest [pipeline stage](#parsing-pipeline) wey e dey run; every stage wey dey before am still dey run.

| Mode | E dey run reach | Wetin e dey answer |
|---|---|---|
| `DATA_CARRIER` | Stage 1 (input routing) | Na which symbology carry dis one? |
| `SYNTAX` | Stage 2 (syntax) | Di AI codes and lengths — dem correct? |
| `CONTENT` | Stage 3 (content) | Di values — na valid GS1 data? |
| `INTERPRETATION` | Stage 4 (interpretation) | Di values mean wetin? |

### DATA_CARRIER mode

E dey stop after Stage 1 — e dey validate di AIM Code ID and identify di symbology, but e no dey enter di AI parsing pipeline. E dey useful when you wan identify symbology and route am without di overhead of full validation.

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

**Use am when:** your application need to sabi di barcode type before e decide how e go process di payload — like say you dey route 1D and 2D symbologies to different handlers. For dat routing, use di typed [`DataCarrierType`](#datacarrierentry-and-datacarriertype) (`getDataCarrier().getDataCarrierType()`) instead of make you dey string-match `getName()`.

---

### SYNTAX mode

E dey stop after Stage 2. E dey useful for structural pre-screening without di cost of content validation.

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

**Use am when:** you wan check say di AI codes and di data lengths dey correct before you commit to full validation, or when you dey scan plenty volume where content error rare.

---

### CONTENT mode

E dey stop after Stage 3.

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

> Most AI no fit stand alone: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) and `21` (SERIAL)
> — each one of dem *require* identification key like AI `01` inside di same element string,
> so if you comot di GTIN wey dey up dia, e go fail for Stage 2 wit `GE-S005` instead of even
> reaching content validation at all. Set `skipRequiresCheck(true)` for di `ParseConfig` if
> you wan parse fragments wey deliberately no carry dia companion AI.

**Use am when:** you need sabi whether di value wey dem scan dey fully GS1-compliant before you use am inside business process, but you no want di overhead of interpretation enrichment.

---

### INTERPRETATION mode (di default)

E dey run di full pipeline reach Stage 4. Na di default when you call `parse(String)` without any mode argument. E dey enrich only elements wey pass content validation clean.

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

**Monetary amount example (AI 3932 — price wit ISO currency code):**
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

**Use am when:** you dey build display layer, label verification tool, or any UI wey need AI values wey dem break down for person to understand.

---

## Correlation ID

Some workflows dey put dia own 8-digit correlation identifier for di front of di raw GS1 input, so dat dem fit tie scan events back to session or transaction. Di format be dis:

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

Di `~` (tilde) na di separator. E **no** be part of di GS1 content — dem dey strip am comot before any GS1 parsing start.

### Detection rules

Dem dey detect di prefix when di input start wit exactly 8 ASCII decimal digits (`0`–`9`) wey `~` follow sharp-sharp. If di 9th character no be `~`, or if any of di first 8 characters no be digit, dem go treat di input as plain GS1 content wey no get correlation prefix.

### How to reach di correlation ID

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

### Wetin happen when e join wit AIM Code ID

Correlation prefix fit come before AIM Code ID. Di parser dey handle am without wahala:

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

**GS1 Digital Link** dey encode one or more AI value direct inside di structure of HTTP(S) URL, so dat physical product fit get identifier wey web fit resolve. GAIA dey implement di *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) for **uncompressed** URI.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` dey recognise Digital Link URI automatically — any input wey start wit `http://` or `https://` dey go to `GS1DLParser`, wey dey run di same content and interpretation stages as di element-string pipeline.

### URI structure and di roles wey AI dey play

Every AI inside Digital Link URI dey play one of three roles, and every `GS1AIObjectElement` dey expose am through `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Role | Where e dey | Example |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Di first `/ai/value` pair for di path (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Di path pairs wey follow, ordered according to di primary key (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Query parameters wey dia keys na all-numeric (§4.10) | `?17=271231` |

Di structural rules wey dem dey enforce (`DLPathRules`):
- Exactly **one** primary identification key for di path; any extra key must dey encoded as query data attribute.
- Di primary key must admit di key qualifiers, and dem must appear for di order wey dem prescribe. You fit leave out optional qualifier, but any one wey *dey* still gats follow di fixed order — see [Qualifier ordering](#qualifier-ordering).
- Custom path segments fit come before di primary key anyhow (like `/products/au/01/...`); collect dem through `getDigitalLinkInfo().getCustomPathStem()`.
- Query keys wey no be numeric (`linkType`, `context`, extension parameters like `23P`) dey ignored; all-numeric keys must be valid AI wey dem flag `validAsDataAttribute`.
- Dem dey decode percent-encoded value characters; AI `(03)` and `(8014)` no dey permitted.

Na **data** dey drive di primary keys and di qualifier sequences wey dem admit — from di AI definitions demselves, di `gs1DigitalLinkPrimaryKey` flag and di `gs1DigitalLinkQualifiers` attribute — no be hard-coded list.

Any structural violation, or input wey no be URL, dey produce Digital Link structural error (`GE-L001`–`GE-L014`, one code for every condition). Di URL metadata wey dem decompose (`scheme`, `domain`, `path`, `customPathStem`, `query`, and di `java.net.URL`) still dey available through `getDigitalLinkInfo()` even when structural error dey.

### Qualifier ordering

For every primary key, `gs1DigitalLinkQualifiers` dey list one or more qualifier sequence wey **get order**. Inside one sequence, AI wey square bracket wrap na **optional**, AI wey no get bracket na **required** — e dey mirror di `[cpv-comp]` notation of di §4.9 ABNF. Di sequences of one primary key na alternatives wey dey exclude each other.

GTIN (`01`), for example, define two sequences:

| Path | Sequence | Meaning |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — each one optional, but dis order fixed |
| upui-path | `235` | TPX (required); GTIN + TPX = UPUI |

So `/01/09506000134352/10/LOT-ABC/21/SER` dey valid (LOT before SER, CPV comot), `/01/.../21/SER/10/LOT-ABC` — dem go **reject** am (order scatter), and `/01/09506000134352/235/2ABC456` na di upui-path. Di ordering check na subsequence match wey dey preserve order, so you fit skip optional AI but you no fit ever reorder dem.

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

## How to work wit di Results

### ParseResult

Di top-level result wey `GaiaParser.parse()` dey return.

| Method | E dey return | Description |
|---|---|---|
| `isValid()` | `boolean` | `true` if no error dey for any level. Warning no dey affect validity. E dey always `true` when `getAiObject()` na `null`. |
| `getPayload()` | `String` | Di input string after dem strip di correlation prefix — and after any [input modifier](#input-modifiers) don rewrite am. |
| `getPayloadContent()` | `String` | Di payload after dem strip AIM Code ID and ECI prefix comot. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (data carrier wey dem reject because e no be GS1, like Code 39 `]A0` carrier), or `UNABLE_TO_DETERMINE_CONTENT` (when `aiObject` na `null`, like for `DATA_CARRIER` mode). |
| `getRequestedParseMode()` | `ParseMode` | Di pipeline depth wey dem configure (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Di deepest stage wey di parse actually reach — see below. |
| `isParseComplete()` | `boolean` | `true` if di parse reach di depth wey dem request (`achieved == requested`). E no depend on `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Every AI wey dem resolve. Na `null` for `DATA_CARRIER` mode. |
| `getErrors()` | `List<GaiaError>` | Every error wey no be WARNING (object-level + all element-level). |
| `getWarnings()` | `List<GaiaError>` | Every WARNING advisory (object-level + all element-level). |
| `hasWarnings()` | `boolean` | `true` if any WARNING advisory dey raised. |
| `getIssues()` | `List<GaiaError>` | Errors and warnings put together. |
| `hasDataCarrier()` | `boolean` | `true` if dem recognise one AIM Code ID. |
| `getDataCarrier()` | `DataCarrierEntry` | Di symbology metadata, or `null` if dem no identify any carrier. |
| `hasEci()` | `boolean` | `true` if dem strip ECI indicator comot from di payload. |
| `getEci()` | `EciEntry` | ECI encoding metadata, or `null`. |
| `hasCorrelationId()` | `boolean` | `true` if `DDDDDDDD~` correlation prefix bin dey inside di original input. |
| `getCorrelationInfo()` | `CorrelationInfo` | Di correlation ID wey dem extract, or `null` if none bin dey. |
| `isInputModified()` | `boolean` | `true` if [input modifier](#input-modifiers) change di input. |
| `getModifierInfo()` | `ModifierInfo` | Wetin di modifier chain do — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. Na `null` if no modifier dey configured. |
| `getTiming()` | `ProcessingTiming` | Di wall-clock timing of di parse — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. Na `null` if no be `GaiaParser` produce am. |
| `getVersion()` | `String` | Di library version wey produce di result. |

#### Requested versus achieved parse mode

Di pipeline dey climb di ladder **SYNTAX → CONTENT → INTERPRETATION**, and e dey stop early when error show, so di mode wey e actually *achieve* fit dey shallower pass di mode wey dem *request*. `getAchievedParseMode()` dey report how far e reach:

| Requested | Wetin happen | Achieved | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | **syntax / structural** error dey halt di parse after tokenisation | `SYNTAX` | `false` |
| `INTERPRETATION` | **content** error (bad format/check digit) dey block di enrichment | `CONTENT` | `false` |
| `CONTENT` | content dey always run reach di end (dem dey annotate errors, e no dey fatal) | `CONTENT` | `true` |
| any (clean input) | di pipeline reach di depth wey dem request | = requested | `true` |
| `DATA_CARRIER` | dem validate di carrier; no AI content dey parsed | `DATA_CARRIER` | `true` |
| any | dem reject di data carrier before AI parsing (like `]A0` carrier wey no be GS1) | `SYNTAX` | `false` |

`isParseComplete()` no depend on `isValid()`: `CONTENT` parse of GTIN wey get bad check digit na **complete** (di content stage run) but e **invalid** (di check digit fail). Use `isParseComplete()` to ask "di pipeline — e run reach di depth wey I ask for?" and use `isValid()` to ask "di data — e correct?".

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

Di collection of AI elements wey dem resolve.

| Method | Description |
|---|---|
| `getAis()` | Every `GS1AIObjectElement` instance, for di order wey dem dey inside di input. |
| `get(String aiCode)` | Di first element wey match di AI code wey you give, or `null`. |
| `contains(String aiCode)` | `true` if AI wey get dat code dey. |
| `size()` | How many AI dem resolve. |
| `isValid()` | `true` if no object-level error dey and no element get error. |
| `toHriString()` | HRI string, like `(01)09506000134352 (17)261231`. |
| `toElementString()` | Raw element string — no bracket, FNC1 dey after every variable-length element — like `010950600013435210LOT-ABC<GS>17271231`. E dey return `null` if `isValid()` na `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` when `hasDigitalLink()` na true, if no be so `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` if di input na GS1 Digital Link URI wey carry primary identification key. URL wey correct but wey no get primary key still dey expose `getDigitalLinkInfo()`, but e dey return `false` here. |
| `getCanonicalDigitalLink()` | Di canonical GS1 Digital Link URI (§4.12) for `https://id.gs1.org` — primary key and qualifiers as path segments, data attributes as query parameters wey dem sort by AI key — or `null` if no primary key dey. |
| `getDigitalLinkInfo()` | URI decomposition metadata (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), or `null` if e no be Digital Link. |
| `getAllErrors()` | Object-level + every element error (dose wey no be WARNING). |
| `getAllWarnings()` | Object-level + every element warning. |
| `getAllIssues()` | Everything put together. |

---

### GS1AIObjectElement

One single AI instance wey dem resolve.

| Method | Description |
|---|---|
| `getAi()` | AI code, like `"01"`, `"3102"`. |
| `getTitle()` | GS1 data title, like `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Di full GS1 description of di AI, **localized to di parse language** (for English na `"Global Trade Item Number (GTIN)"`). E dey fall back to di English text wey dey di AI definition if dem never translate am. |
| `getFormatString()` | Format descriptor wey cover di AI *and* im data, like `"N2+N14"` for AI `01`, `"N2+X..20"` for AI `10`, `"N4+N3+N..15"` for AI `3932`. |
| `getValue()` | Di raw data value wey dem extract from di element string. |
| `isFixedLength()` | `true` if di AI get fixed data length. |
| `getPosition()` | Di character offset (counting from zero) inside di original input. |
| `getGS1ComponentValues()` | Di value slices per component (for AI wey get plenty component). |
| `getErrors()` | Element-level errors wey no be WARNING. |
| `getWarnings()` | Element-level WARNING advisories. |
| `getIssues()` | Element-level errors and warnings put together. |
| `hasErrors()` | `true` if any error wey no be WARNING dey attached. |
| `hasWarnings()` | `true` if any WARNING advisory dey attached. |
| `getInterpretations()` | `GS1AIInterpretation` entries (dem dey populate am for INTERPRETATION mode). |
| `getInterpretation(String type)` | Di first interpretation wey match di `GS1Constants_Enricher` type key wey you give, or `null`. |
| `getDigitalLinkAIType()` | Di Digital Link role of di element (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), or `null` for element-string input. |
| `hasDigitalLinkAIType()` | `true` if dem don assign Digital Link role. |

---

### GaiaError

Validation error or advisory wey no fit change.

| Method | Description |
|---|---|
| `getId()` | Di catalogue identifier, like `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, or `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, or `INTERNAL`. |
| `getCode()` | Short code wey machine fit read. |
| `getAi()` | Di AI code wey cause di error, or `null` for object-level error. |
| `getMessage()` | Message wey human being fit read, wey dem don put di values inside. |
| `getPosition()` | Di character offset (counting from zero) inside di original input. |

---

### GS1AIInterpretation

One single interpretation fragment wey get label, wey dem attach to `GS1AIObjectElement` for `INTERPRETATION` mode.

| Method | Description |
|---|---|
| `getType()` | Type key wey machine fit read, like `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. E no dey change across languages. |
| `getLabel()` | Label wey human being fit read, **localized to di parse language** (for English na `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | Di value wey dem extract/enrich, like `"31/12/2026"`, `"9506000"`. Dem no dey localize am. |

---

### DataCarrierEntry and DataCarrierType

When di input carry AIM Code ID, `ParseResult.getDataCarrier()` dey return `DataCarrierEntry` wey dey describe di symbol wey carry di data. Di entry na di specific registry record for di AIM Code ID wey match; `DataCarrierType` na di compile-time enum wey e belong to.

#### DataCarrierEntry

Di metadata for one AIM Code ID wey dem recognise (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Method | Description |
|---|---|
| `getAimCodeId()` | Di AIM Code ID wey match, like `"]C1"`. |
| `getName()` | Di name wey human being fit read for dat specific symbol, like `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Longer description of di carrier. |
| `getType()` | Di structural type of di carrier as string (e dey mirror `getDataCarrierType().getCategory()`). |
| `getStandard()` | Di symbology standard, where dem record am. |
| `getDataCarrierType()` | Di typed `DataCarrierType` for dis entry — use dis one for routing inside code. |
| `isGs1Capable()` | `true` if di carrier fit hold GS1 data (AI element string and/or Digital Link). |
| `isGs1AICapable()` | `true` if di carrier fit hold GS1 AI element string. |
| `isGs1DigitalLinkCapable()` | `true` if di carrier fit hold GS1 Digital Link URI. |
| `isEciCapable()` | `true` if di carrier support ECI indicator. |
| `isRequiresGtinPadding()` | `true` for EAN/UPC/ITF carriers wey dem dey pad dia numeric value reach GTIN-14 before AI parsing. |

#### DataCarrierType

Compile-time enum of data-carrier types, keyed by di AIM Code ID wey ISO/IEC 15424 assign (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Di character wey come after `]` (di *code character*) dey select di family; most families dey map to one single constant wey cover every modifier (`ITF` dey cover `]I0`–`]I2`; `EAN_UPC` dey cover EAN-13, UPC-A, UPC-E and EAN-8). Where GS1 reserve one modifier for AI data, dat variant get im own constant — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — separate from di plain ones wey match dem. When no AIM Code ID dey, or when e name carrier wey dem no sabi, di type na `UNKNOWN`.

| Method | Description |
|---|---|
| `getCategory()` | Di broad `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, or `OTHER`. |
| `getCodeChar()` | Di AIM code character wey dey identify di family, like `"Q"` for QR Code; `null` for `UNKNOWN`. |
| `getDisplayName()` | Di name of di *type* wey human being fit read (e fit dey broader pass `DataCarrierEntry.getName()` — like `"EAN-13 / UPC-A / UPC-E / EAN-8"` versus `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` for constants wey dey always mean GS1 AI data: di four variants wey GS1 reserve (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) plus `GS1_DATABAR`, wey be GS1 by im own nature because every `]e` modifier na GS1 DataBar. E narrow pass `DataCarrierEntry.isGs1AICapable()` — plain `QR_CODE` fit still carry GS1 AI data. |
| `static forAimCodeId(String)` | E dey resolve type direct from AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); e dey return `UNKNOWN` for ID wey no dey, wey scatter, or wey dem no recognise. |

Routing by type instead of by name — like splitting linear (Code-128) from 2D (QR / Data Matrix) symbols:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` dey cover only di matrix and dot symbols; di stacked-linear carriers (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) na `STACKED_LINEAR`, even though people commonly dey call
dem "2D" barcode. If you wan treat di two as one group — say, to decide whether you need
imager instead of laser scanner — test for either category.

> To resolve type, di AIM Code ID must dey inside di scan; without am `getDataCarrier()` na `null` and di type na `UNKNOWN`. Configure di scanner make e dey transmit di AIM Code ID prefix.

---

## Error Reference

| Code | Level | Stage | Wetin e mean |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | AI prefix wey dem no sabi — dem no fit determine di data length |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Di input too short to read complete AI code |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Value wey dem cut — characters no reach wetin di AI require |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Application Identifier repeat inside di element string |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | AI dependency wey dem require no dey |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | AI pairing wey dem exclude — two AI wey no fit dey together |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Tokenisation failure wey dem no expect |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Character wey dey outside di GS1 encodable set dey di element string |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | FNC1 separator wey dem require no dey after variable-length AI |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Data remain after di maximum of every component |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | FNC1 separator dey after fixed-length AI for middle of di string |
| `GE-W002` | WARNING | SYNTAX | FNC1 remain for di end of di element string (advisory only) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI structural violations — one code for every condition (URI wey scatter, scheme, host, qualifier order, AI wey dem ban, no primary key (`GE-L013`), plenty primary keys (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Di value fail di regex pattern of di AI |
| `GE-C003` | DATA_ERROR | CONTENT | Check digit validation fail |
| `GE-C004` | DATA_ERROR | CONTENT | Check character pair validation fail |
| `GE-C005` | FORMAT_ERROR | CONTENT | Component value carry character wey dey outside di allowed character set |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Component-format failures — one code for every validator condition (see `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Custom semantic-validation failures — one code for every validator condition (see `content/validator/`). **Exceptions:** di 14 GS1 company-prefix checks wey dey listed below carry level `WARNING`, and `GE-C168` (ISO 3166-1 numeric country code wey dem no recognise) carry `FORMAT_ERROR`. |
| GS1 company-prefix checks | WARNING | CONTENT | Di key no start wit GS1 company prefix wey dem recognise, for di GS1-key AI — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Advisory only — e no dey affect validity. |
| `GE-C169` | DATA_ERROR | CONTENT | IMEI check digit (Luhn) fail for AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | EID check digit (Luhn) fail for AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | AIM Code ID wey dem no recognise |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Dem identify di carrier but e no support GS1 AI element string, e no support Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Internal error wey dem no expect |

> **One defect wey dem sabi for how message dey render.** Di catalogue templates dey quote di
> values wey dem interpolate wit doubled apostrophes for MessageFormat style (`''{value}''`),
> but `ErrorRegistry` dey interpolate wit plain `String.replace`, so di doubling dey survive
> reach `getMessage()` — for now you go see `value ''09506000134351''` for where di message
> text wey dis guide quote dey show `value '09506000134351'`. E dey affect every message wey
> dey quote value, for all 35 language catalogues. No parse error message; match on
> `getId()` / `getCode()`.

---

## Thread Safety

`GaiaParser` dey safe for thread once you don construct am. Plenty threads fit share one single instance and use am concurrently. Di pattern wey dem recommend na to construct one instance when di application dey start, then reuse am:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` no fit change and e safe to share di same way. Di only thread-safety obligation wey di library no fit enforce for you dey on [input modifiers](#input-modifiers): dem dey cache one single instance of every modifier and share am across every concurrent parse, so di implementations must no hold state.

---

## Appendix A — AI String Constants

`GS1Constants_AICodes` (package `tools.pantheum.gaia.gs1.constants`) dey declare one `String` constant for every Application Identifier wey GAIA recognise. Use dis constants instead of make you hard-code AI code literal inside your code:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Every constant dey carry di string form of im AI code (like `AI_01_GTIN = "01"`).

### Identification and Serialisation

| AI | Constant | Description |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Serial Shipping Container Code (SSCC). |
| `01` | `AI_01_GTIN` | Global Trade Item Number wey dem use identify product (GTIN). |
| `02` | `AI_02_CONTENT` | Global Trade Item Number (GTIN) of di trade items wey dey inside. |
| `03` | `AI_03_MTO_GTIN` | Identification for Made-to-Order (MtO) trade item (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Batch or lot number. |
| `20` | `AI_20_VARIANT` | Internal product variant. |
| `21` | `AI_21_SERIAL` | Serial number. |
| `22` | `AI_22_CPV` | Consumer product variant (di type wey dem sell for customer). |
| `235` | `AI_235_TPX` | Third Party Controlled, Serialised Extension of Global Trade Item Number (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Extra product ID number wey di maker give am. |
| `241` | `AI_241_CUST_PART_NO` | Customer own part number. |
| `242` | `AI_242_MTO_VARIANT` | Made-to-Order variation number. |
| `243` | `AI_243_PCN` | Packaging component number. |
| `250` | `AI_250_SECONDARY_SERIAL` | Secondary serial number. |
| `251` | `AI_251_REF_TO_SOURCE` | Reference to di source entity. |
| `253` | `AI_253_GDTI` | Global Document Type Identifier (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Extension part wey dem add to Global Location Number (GLN). |
| `255` | `AI_255_GCN` | Global Coupon Number (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variable count of items (product wey get variable measure). |
| `37` | `AI_37_COUNT` | How many trade items or pieces dey inside di logistic unit. |

### Dates and Times

| AI | Constant | Description |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Production date (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Due date (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Packaging date (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Best-before date (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Sell-by date (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Expiry date (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | No-earlier-than delivery date time (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | No-later-than delivery date time (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Release date (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Expiry date and time (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | First freeze date (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Harvest date (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Test-by date (YYMMDD[hhmm]). |

### Quantity and Measure — Variable Measure (Metric)

Di four-digit AI families `310n`–`369n` dey encode quantities wey get variable measure. Di third digit dey select di measure type; di **fourth digit** (`n`, 0–5) na how many implied decimal places dey — so `AI_3102_NET_WEIGHT_KG` mean net weight for kilogram wit 2 decimal places.

| Family | Constant pattern (`n` = decimal digit) | Description |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Net weight, kilogram (product wey get variable measure). |
| `311n` | `AI_311n_LENGTH_M` | Length or first size, metre (product wey get variable measure). |
| `312n` | `AI_312n_WIDTH_M` | Width, diameter, or second size, metre (product wey get variable measure). |
| `313n` | `AI_313n_HEIGHT_M` | Depth, thickness, height, or third size, metre (product wey get variable measure). |
| `314n` | `AI_314n_AREA_M` | Area, square metre (product wey get variable measure). |
| `315n` | `AI_315n_NET_VOLUME_L` | Net volume, litre (product wey get variable measure). |
| `316n` | `AI_316n_NET_VOLUME_M` | Net volume, cubic metre (product wey get variable measure). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistic weight, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Length or first size, metre. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Width, diameter, or second size, metre. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Depth, thickness, height, or third size, metre. |
| `334n` | `AI_334n_AREA_M_LOG` | Area, square metre. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistic volume, litre. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistic volume, cubic metre. |
| `337n` | `AI_337n_KG_PER_M` | Kilogram for each square metre. |

### Quantity and Measure — Variable Measure (Imperial / US)

| Family | Constant pattern (`n` = decimal digit) | Description |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Net weight, pound (product wey get variable measure). |
| `321n` | `AI_321n_LENGTH_IN` | Length or first size, inch (product wey get variable measure). |
| `322n` | `AI_322n_LENGTH_FT` | Length or first size, feet (product wey get variable measure). |
| `323n` | `AI_323n_LENGTH_YD` | Length or first size, yard (product wey get variable measure). |
| `324n` | `AI_324n_WIDTH_IN` | Width, diameter, or second size, inch (product wey get variable measure). |
| `325n` | `AI_325n_WIDTH_FT` | Width, diameter, or second size, feet (product wey get variable measure). |
| `326n` | `AI_326n_WIDTH_YD` | Width, diameter, or second size, yard (product wey get variable measure). |
| `327n` | `AI_327n_HEIGHT_IN` | Depth, thickness, height, or third size, inch (product wey get variable measure). |
| `328n` | `AI_328n_HEIGHT_FT` | Depth, thickness, height, or third size, feet (product wey get variable measure). |
| `329n` | `AI_329n_HEIGHT_YD` | Depth, thickness, height, or third size, yard (product wey get variable measure). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistic weight, pound. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Length or first size, inch. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Length or first size, feet. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Length or first size, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Width, diameter, or second size, inch. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Width, diameter, or second size, feet. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Width, diameter, or second size, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Depth, thickness, height, or third size, inch. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Depth, thickness, height, or third size, feet. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Depth, thickness, height, or third size, yard. |
| `350n` | `AI_350n_AREA_IN` | Area, square inch (product wey get variable measure). |
| `351n` | `AI_351n_AREA_FT` | Area, square feet (product wey get variable measure). |
| `352n` | `AI_352n_AREA_YD` | Area, square yard (product wey get variable measure). |
| `353n` | `AI_353n_AREA_IN_LOG` | Area, square inch. |
| `354n` | `AI_354n_AREA_FT_LOG` | Area, square feet. |
| `355n` | `AI_355n_AREA_YD_LOG` | Area, square yard. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Net weight, troy ounce (product wey get variable measure). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Net weight (or volume), ounce (product wey get variable measure). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Net volume, quart (product wey get variable measure). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Net volume, U.S. gallon (product wey get variable measure). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistic volume, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistic volume, U.S. gallon. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Net volume, cubic inch (product wey get variable measure). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Net volume, cubic feet (product wey get variable measure). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Net volume, cubic yard (product wey get variable measure). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistic volume, cubic inch. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistic volume, cubic feet. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistic volume, cubic yard. |

### Pricing and Monetary Amounts

Di fourth digit (`n`) dey encode how many implied decimal places dey. Di range wey dem
allow dey different from family to family — check di `n` column.

| Family | Constant pattern (`n` = decimal digit) | `n` | Description |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Amount wey dem go pay or coupon value, for local currency. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Amount wey dem go pay, wit ISO currency code. |
| `392n` | `AI_392n_PRICE` | 0–9 | Amount wey dem go pay, for one currency area (product wey get variable measure). |
| `393n` | `AI_393n_PRICE` | 0–9 | Amount wey dem go pay, wit ISO currency code (product wey get variable measure). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Percentage discount wey coupon give. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Amount wey person go pay for each unit, for one currency area (product wey get variable measure). |

### Location and Shipping

| AI | Constant | Description |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Customer purchase order number. |
| `401` | `AI_401_GINC` | Global Identification Number for Consignment (GINC). |
| `402` | `AI_402_GSIN` | Global Shipment Identification Number (GSIN). |
| `403` | `AI_403_ROUTE` | Routing code. |
| `410` | `AI_410_SHIP_TO_LOC` | Ship to / Deliver to Global Location Number (GLN). |
| `411` | `AI_411_BILL_TO` | Global Location Number (GLN) of di person wey go receive bill/invoice. |
| `412` | `AI_412_PURCHASE_FROM` | Global Location Number (GLN) of di place wey dem buy am from. |
| `413` | `AI_413_SHIP_FOR_LOC` | Ship for / Deliver for - Forward to Global Location Number (GLN). |
| `414` | `AI_414_LOC_NO` | Identification of physical location - Global Location Number (GLN). |
| `415` | `AI_415_PAY_TO` | Global Location Number (GLN) of di party wey send di invoice. |
| `416` | `AI_416_PROD_SERV_LOC` | Global Location Number (GLN) of di production or service location. |
| `417` | `AI_417_PARTY` | Global Location Number (GLN) of di party. |
| `420` | `AI_420_SHIP_TO_POST` | Ship to / Deliver to postal code wey belong to just one postal authority. |
| `421` | `AI_421_SHIP_TO_POST` | Ship to / Deliver to postal code wit ISO country code. |
| `422` | `AI_422_ORIGIN` | Country wia di product come from (origin). |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Country wia dem first process am. |
| `424` | `AI_424_COUNTRY_PROCESS` | Country wia dem process am. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Country wia dem dismantle am. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Country wey cover di whole process chain. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Region inside di country wia e come from. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Ship-to / Deliver-to company name. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Ship-to / Deliver-to contact person. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Ship-to / Deliver-to address line 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Ship-to / Deliver-to address line 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Ship-to / Deliver-to suburb. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Ship-to / Deliver-to locality (town/city). |
| `4306` | `AI_4306_SHIP_TO_REG` | Ship-to / Deliver-to region. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Ship-to / Deliver-to country code. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Ship-to / Deliver-to telephone number. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Ship-to / Deliver-to GEO location (map location). |
| `4310` | `AI_4310_RTN_TO_COMP` | Return-to company name. |
| `4311` | `AI_4311_RTN_TO_NAME` | Return-to contact person. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Return-to address line 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Return-to address line 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Return-to suburb. |
| `4315` | `AI_4315_RTN_TO_LOC` | Return-to locality (town/city). |
| `4316` | `AI_4316_RTN_TO_REG` | Return-to region. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Return-to country code. |
| `4318` | `AI_4318_RTN_TO_POST` | Return-to postal code. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Return-to telephone number. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Service code description. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Sign wey show say na dangerous goods. |
| `4322` | `AI_4322_AUTH_LEAVE` | Permission to leave package (no need sign for am). |
| `4323` | `AI_4323_SIG_REQUIRED` | Sign wey show say dem need signature. |
| `4330` | `AI_4330_MAX_TEMP_F` | Highest temperature for Fahrenheit (dem show am for hundredth of degree). |
| `4331` | `AI_4331_MAX_TEMP_C` | Highest temperature for Celsius (dem show am for hundredth of degree). |
| `4332` | `AI_4332_MIN_TEMP_F` | Lowest temperature for Fahrenheit (dem show am for hundredth of degree). |
| `4333` | `AI_4333_MIN_TEMP_C` | Lowest temperature for Celsius (dem show am for hundredth of degree). |

### Product Attributes and Traceability

| AI | Constant | Description |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO Stock Number (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE classification for meat carcasses and cuts. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | How strong di active ingredient be (potency). |
| `7005` | `AI_7005_CATCH_AREA` | Catch area (wia dem catch di fish). |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Fish species (for fishery purpose). |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Type of fishing gear/equipment. |
| `7010` | `AI_7010_PROD_METHOD` | How dem produce am (production method). |
| `7020` | `AI_7020_REFURB_LOT` | Refurbishment lot ID (di batch wey dem repair/renew). |
| `7021` | `AI_7021_FUNC_STAT` | Functional status (if e still dey work). |
| `7022` | `AI_7022_REV_STAT` | Revision status. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Global Individual Asset Identifier (GIAI) of an assembly (group of parts wey join together). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Number of di processor wit ISO country code wey get three digits (10 slots). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC wit Extension 1 and Importer index. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT freight unit type. |

### National Healthcare Reimbursement Numbers (NHRN)

| AI | Constant | Description |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | National Healthcare Reimbursement Number (NHRN) - Germany PZN. |
| `711` | `AI_711_NHRN_CIP` | National Healthcare Reimbursement Number (NHRN) - France CIP. |
| `712` | `AI_712_NHRN_CN` | National Healthcare Reimbursement Number (NHRN) - Spain CN. |
| `713` | `AI_713_NHRN_DRN` | National Healthcare Reimbursement Number (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | National Healthcare Reimbursement Number (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | National Healthcare Reimbursement Number (NHRN) - United States of America NDC. |
| `716` | `AI_716_NHRN_AIC` | National Healthcare Reimbursement Number (NHRN) - Italy AIC. |
| `717` | `AI_717_NHRN_SRN` | National Healthcare Reimbursement Number (NHRN) - Costa Rica Sanitary Register Number. |

### Healthcare, GMN, HIDRI, CPID and Person Data

| AI | Constant | Description |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Certification Reference (10 slots). |
| `7240` | `AI_7240_PROTOCOL` | Protocol ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Type of AIDC media (how dem carry di code). |
| `7242` | `AI_7242_VCN` | Version Control Number (VCN). |
| `7250` | `AI_7250_DOB` | Date of birth (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Date and time wey dem born am (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biological sex. |
| `7253` | `AI_7253_FAMILY_NAME` | Person family name (surname). |
| `7254` | `AI_7254_GIVEN_NAME` | Person first name (given name). |
| `7255` | `AI_7255_SUFFIX` | Person name suffix (like Jr., Sr.). |
| `7256` | `AI_7256_FULL_NAME` | Person full name. |
| `7257` | `AI_7257_PERSON_ADDR` | Person address. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Baby birth order (wich number pikin be for multiple birth). |
| `7259` | `AI_7259_BABY` | Baby family name (surname). |
| `8001` | `AI_8001_DIMENSIONS` | Roll products (width, length, core diameter, direction, splices). |
| `8002` | `AI_8002_CMT_NO` | Mobile phone number identifier. |
| `8003` | `AI_8003_GRAI` | Global Returnable Asset Identifier (GRAI). |
| `8004` | `AI_8004_GIAI` | Global Individual Asset Identifier (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Price for each unit of measure. |
| `8006` | `AI_8006_ITIP` | Identification for individual trade item piece (ITIP). |
| `8007` | `AI_8007_IBAN` | International Bank Account Number (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Date and time wey dem produce am (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optically Readable Sensor Indicator (sensor wey scanner fit read). |
| `8010` | `AI_8010_CPID` | Component/Part Identifier (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Serial number for Component/Part Identifier (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Software version. |
| `8013` | `AI_8013_GMN` | Global Model Number (GMN). |
| `8014` | `AI_8014_MUDI` | Highly Individualised Device Registration Identifier (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Global Service Relation Number (GSRN) wey show relationship between organisation wey dey offer service and di service provider. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Global Service Relation Number (GSRN) wey show relationship between organisation wey dey offer service and di person wey dey receive di service. |
| `8019` | `AI_8019_SRIN` | Service Relation Instance Number (SRIN). |
| `8020` | `AI_8020_REF_NO` | Payment slip reference number. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identification for pieces of trade item (ITIP) wey dey inside logistic unit. |
| `8030` | `AI_8030_DIGSIG` | Digital Signature (DigSig). |
| `8040` | `AI_8040_IMEI` | International Mobile Equipment Identity (IMEI). |
| `8041` | `AI_8041_IMEI2` | International Mobile Equipment Identity 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Embedded SIM number (eSIM). |
| `8043` | `AI_8043_PSIM` | Physical SIM number. |
| `8110` | `AI_8110` | Coupon code wey dem dey use for North America. |
| `8111` | `AI_8111_POINTS` | Loyalty points wey coupon carry. |
| `8112` | `AI_8112` | Positive offer file coupon code identification for use for North America. |
| `8200` | `AI_8200_PRODUCT_URL` | Extended Packaging URL (link wey get more package info). |

### Internal / Company Use

| AI | Constant | Description |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Information wey trading partners agree together. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Information wey dey internal to di company (9 slots). |

---

## Appendix B — Interpretation Key Constants

When dem call `GaiaParser.parse()` wit `ParseMode.INTERPRETATION`, every `GS1AIObjectElement` fit carry list of `GS1AIInterpretation` objects wey domain-specific enrichers produce. Use di `GS1Constants_Enricher` constants (package `tools.pantheum.gaia.gs1.constants`) as keys to look up specific interpretation values:

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

Di display labels **no** be constants — dem dey inside di localized catalogues for `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, keyed by di type constant. `GS1AIInterpretation.getLabel()` dey return di label for di parse language (see [Messages and labels wey dem localize](#messages-and-labels-wey-dem-localize)), and e dey fall back to English when one catalogue no get di key. Di Display label column wey dey below dey list di Nigerian Pidgin text — but note say di Pidgin catalogue deliberately dey reuse di English label, so wetin you go see na English. Di type keys demselves no dey change across languages, so match on di key always, never on di label.

### Date and Time

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `DATE_VALUE` | Date | Date AI (11–17, 7003, 7006, 7011, and so on) |
| `DATE_FORMAT` | Date format | Date AI |
| `TIME_VALUE` | Time | AI wey carry time (7003, 7011, 8008, and so on) |
| `TIME_FORMAT` | Time format | AI wey carry time |
| `DATETIME_VALUE` | Date and time | Date+time AI |
| `DATETIME_FORMAT` | Date and time format | Date+time AI |

### Harvest Date

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Harvest start date | AI 7007 |
| `HARVEST_END_DATE` | Harvest end date | AI 7007 (di range end wey optional) |
| `HARVEST_DATE_RANGE` | Harvest date range | AI 7007 |

### GS1 Company Prefix

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 company prefix | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 member code | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 member organisation | GTIN / GLN / SSCC AI |

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
| `COUNTRY_CODE_NUMERIC` | Country code (numeric) | Single-country AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Country code (alpha-2) | Alpha-2 country AI |
| `COUNTRY_NAME` | Country name | Single-country AI |
| `COUNTRY_LIST` | Countries | AI 423 — every name join together, like `Australia, New Zealand` |

AI 423 (country of initial processing) fit carry reach five countries, so e dey emit
**one numbered pair for every country** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — then one single `COUNTRY_LIST` summary go
follow. Build dis keys from di `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX`
constants wit ordinal wey start from 1, or just walk through `getInterpretations()`; di
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` keys wey no get suffix — dem **no** dey emit dem
for AI 423.

### Currency (ISO 4217)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Currency code | Amount AI wey get currency (391n, 393n) |
| `CURRENCY_ALPHA` | Currency alpha code | Amount AI wey get currency |
| `CURRENCY_NAME` | Currency name | Amount AI wey get currency |

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

### Aquatic Species (FAO)

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

### Roll Products

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

Di fifteen digits dey break down into `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`, and
di RBI na di first two digits of di TAC — so `IMEI_RBI` na prefix of `IMEI_TAC`, e no be
separate field. `IMEI_FORMATTED` dey show di standard GSMA display grouping
`AA-BBBBBB-CCCCCC-D` (like `49-015420-323751-8`), wey dey split di TAC for di RBI boundary; di
old `6-2-6-1` grouping, wey bin dey split where di final assembly code wey dem don abolish bin
dey start, no dey emitted.

`IMEI_RBI_NAME` dey resolve di RBI into di name of di body wey allocate am, through
`ImeiRbiData`, and dem dey attach am **last, and only if di code dey listed dia**. Dat table
dey cover three groups:

- **Bodies wey still dey allocate** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, plus `99`
  Global Hexadecimal Administrator and `98` (reserved).
- **Test ranges** — `00` and `02`–`09`, wey dey indicate test IMEI, no be real allocation.
  Ask about dem wit `ImeiRbiData.isTestCode(code)`.
- **Bodies wey no dey allocate again** — historical bodies like `49` (BZT/BAPT, Germany), `44`
  (BABT, United Kingdom) and `91` (MSAI, India). Ask about dem wit
  `ImeiRbiData.isNoLongerAllocating(code)`. Device wey carry dis codes dey normal and dem
  still dey for service; na only new allocation stop, so dis one na information to report, e
  no be signal about validity at all.

If `IMEI_RBI_NAME` no dey, e mean say "dis RBI no dey our table", e **no** mean say "di IMEI no
valid": dem compile di table from RBI list wey dem publish, no be direct from GSMA, so e fit
lag behind bodies wey dem just appoint. No draw any validation conclusion from di fact say e no
dey; RBI no be check character. Code wey dey walk through di interpretation list suppose fit
survive when e no dey, instead of indexing by position.

### SIM Identifiers (EID / ICCID)

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Industry category | AI 8042 |
| `EID_BODY` | EID body | AI 8042 |
| `EID_CHECK_DIGIT` | Check digit | AI 8042 |
| `ICCID_BODY` | ICCID body | AI 8043 |
| `ICCID_EXTENSION` | Extension | AI 8043 |

`SIM_MII` dey carry di **first two** digits (`89`), wey be di pair wey ITU-T E.118 assign to
telecommunications. But ISO/IEC 7812 itself dey define di MII as **only di first digit**, so
`SIM_MII_NAME` dey resolve di category from di leading `8` digit through `Iso7812Data` —
producing "Healthcare, telecommunications and other future industry assignments". Because of
dat, e dey constant for every EID wey correct; dem dey report am for traceability back to di
standard, e no be discriminator. `Iso7812Data.nameForCode(digit)` dey take single digit, while
`nameForIdentifier(prefix)` dey accept longer prefix and dey read im first digit.

Na only `EidEnricher` (AI 8042) dey emit `SIM_MII_NAME`. `IccidEnricher` (AI 8043) dey show
`SIM_MII` but e no dey show di category.

### Certification Reference

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

### Infant Birth Sequence

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

### Decimal and Measurement Values

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Decimal value | Numeric AI wey get implied decimal places (31xx–36xx) |
| `DECIMAL_AMOUNT` | Amount | Pricing AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | Percentage | AI 394n |
| `DECIMAL_PLACES` | Decimal places | Together wit `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Percentage format | AI 394n |
| `ISO_UNIT_CODE` | ISO unit code | Measurement AI |
| `ISO_UNIT_NAME` | ISO unit name | Measurement AI |
| `MONETARY_AMOUNT` | Monetary amount | Pricing AI |
| `MONETARY_AMOUNT_DISPLAY` | Monetary amount (formatted) | Pricing AI |

### Geographic Coordinates

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `LATITUDE` | Latitude | AI 4309 |
| `LONGITUDE` | Longitude | AI 4309 |
| `GEO_COORDINATES` | Geo coordinates | AI 4309 |
| `LATITUDE_DMS` | Latitude (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitude (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Geo coordinates (DMS) | AI 4309 |

### Production Method

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Production method code | AI 7010 |
| `PRODUCTION_METHOD` | Production method | AI 7010 |

### AIDC Media Type

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC media type code | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC media type | AI 7241 |

### Piece of Total

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Piece number | AI 8006 |
| `PIECE_TOTAL` | Total pieces | AI 8006 |
| `PIECE_OF_TOTAL` | Piece of total | AI 8006 |

### Component Splits

Keys wey di declarative component splits inside `content/ai-content.json` dey emit, no be any
enricher wey dem write for Java — all of dem dey show di named parts of one composite AI value.
Unlike every other key inside dis appendix, **dis ones no get constant inside
`GS1Constants_Enricher`**: match di string literal, or read di type from
`GS1AIInterpretation.getType()`.

| Type key | Display label | Produced by |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Check digit | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Serial number | AI 253, 255, 8003 |
| `POSTAL_CODE` | Postal code | AI 421 |
| `PROCESSOR_ID` | Processor identifier | AI 7030–7039 |

Note say di `CHECK_DIGIT` wey dey here na di general component-split key, and e different from
di enricher-specific keys `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` and
`EID_CHECK_DIGIT` wey dey up dia.

### Miscellaneous

| Key constant | Display label | Produced by |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Value | Boolean / flag AI (4321–4323) |
| `DECODED_TEXT` | Decoded text | Free-text AI |
