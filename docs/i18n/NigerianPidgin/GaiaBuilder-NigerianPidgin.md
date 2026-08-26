# GaiaBuilder — Developer Guide

## Wetin Dey Inside

1. [Overview](#overview)
2. [About GS1 and di General Specifications](#about-gs1-and-di-general-specifications)
3. [Quick Start](#quick-start)
4. [How e take dey work](#how-e-take-dey-work)
5. [How to build element string](#how-to-build-element-string)
   - [Attribute AI need dia own identification key](#attribute-ai-need-dia-own-identification-key)
6. [How to build Digital Link URI](#how-to-build-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validation and errors](#validation-and-errors)
   - [Di build methods wey dey throw](#di-build-methods-wey-dey-throw)
   - [Di tryBuild\* methods wey no dey throw](#di-trybuild-methods-wey-no-dey-throw)
   - [Di language of error messages](#di-language-of-error-messages)
   - [BuildResult](#buildresult)
9. [Check digits](#check-digits)
10. [Thread Safety](#thread-safety)
11. [API Reference](#api-reference)

---

## Overview

`GaiaBuilder` na di opposite number of [`GaiaParser`](GaiaParser-NigerianPidgin.md): e dey turn collection of Application Identifier (AI) and value pairs into GS1 **element string** wey correct, or into **GS1 Digital Link URI**. You go supply di AI wit dia complete data values; di builder go assemble dem, validate di result wit di same engine wey `GaiaParser` dey use, then e go render di output.

Because di builder dey validate by *parsing im own candidate output*, everything wey e return dey guaranteed to parse clean through `GaiaParser` — di two of dem no fit ever disagree on wetin correct.

**Entry-point class:** `tools.pantheum.gaia.GaiaBuilder`

---

## About GS1 and di General Specifications

**GS1** na global non-profit organisation wey dey develop and maintain open standards for supply-chain identification and data exchange. Dem dey use im standards for retail, healthcare, logistics, foodservice, and plenty other industries — everything from di product barcode wey dey consumer packaging, reach di serialised tracking of pharmaceutical doses.

Di authoritative reference for everything wey dis builder dey implement na di **GS1 General Specifications** — one single document wey define:

- Every Application Identifier (AI) code, dia data titles, formats, and validation rules
- Di syntax rules for how to build and encode AI element string
- Barcode symbology requirements and AIM Code ID assignments
- Check digit and check character algorithms
- How dem dey resolve two-digit year (di sliding-window rule)
- Data Matrix, QR Code, GS1-128, GS1 DataBar, and other carrier specifications

Dem dey update di GS1 General Specifications every year. Di current edition and di materials wey follow am dey here:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA dey implement **Release 26.0 (Ratified, Jan 2026)** of di GS1 General Specifications.

Na one companion standard, **GS1 Digital Link: URI Syntax**, dey govern GS1 Digital Link URI. Na im define di primary identification keys, di order wey key qualifiers go follow, and di data-attribute encoding wey di builder dey apply when e dey render Digital Link URI:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA dey implement **Release 1.7.0 (Ratified, Aug 2026)** of di GS1 Digital Link: URI Syntax standard.

Di section references wey dey dis document dey point to di GS1 General Specifications (like "Table 7-5", "section 7.12"), except di Digital Link section numbers (like "§4.9", "§4.12") wey dey point to di GS1 Digital Link: URI Syntax standard.

---

## Quick Start

```java
import tools.pantheum.gaia.GaiaBuilder;

// Element string
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .ai("17", "271231")
        .buildElementString();
// 0109506000134352 10 LOT-ABC <GS> 17 271231   (GS = FNC1 group separator, 0x1D)

// GS1 Digital Link URI (canonical, on https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .ai("17", "271231")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
```

Use di `GS1Constants_AICodes` constants instead of raw AI literal (see [Appendix A for di parser guide](GaiaParser-NigerianPidgin.md#appendix-a--ai-string-constants)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## How e take dey work

Every build dey follow di same path:

1. **Assembly** — dem dey join di AI/value pairs into candidate element string. Dem dey insert FNC1 group separator (`0x1D`) after every AI wey *need separator* and wey no be di last element. AI wey dia length don already dey known (GTIN, dates, fixed-length measures) no dey collect separator; di others dey collect. (AI wey dem no sabi no dey ever reach dis step — `ai(...)` dey reject dem sharp-sharp; see [How to build element string](#how-to-build-element-string).)
2. **Validation** — `GaiaParser` dey parse di candidate for `CONTENT` mode. Dem dey check every value against di format and di check digit of im AI, and dem dey enforce di structural rules (di AI pairings wey required and di ones wey dem exclude). If di parse no valid, di build go fail.
3. **Rendering** —
   - For element string, na di `toElementString()` of di object wey dem validate dem dey return.
   - For Digital Link, dem dey assign every element im DL role (primary key, key qualifier, or data attribute), dem dey validate di key-qualifier sequence, dem dey emit di URI, then **dem dey re-parse di URI wey dem emit to confirm say e go come back as valid Digital Link** — na safety check for di string-assembly and percent-encoding step. If e no come back, dem go throw `GaiaBuilderException`.

Dis one dey mirror di reconstruction logic wey dey `DLSyntaxParser`, so di way dem dey place separator and di validation match exactly wetin di parser dey expect.

---

## How to build element string

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- Dem dey validate di **AI** sharp-sharp: `ai(...)` dey throw `IllegalArgumentException` if e no be GS1 Application Identifier wey dem sabi. (Di builder dey join di AI wit di value before parsing, so AI wey dem no sabi or wey too long like `"99999"` gats catch here — if no be so, dem for silently re-tokenise am into different AI.) Na later, for build time, dem dey validate di **value**.
- Value must dey **complete**, including any check digit. Di builder no dey calculate check digit and e no dey append am for you — see [Check digits](#check-digits).
- Dem dey emit di AI for di order wey you add dem. Di builder dey insert FNC1 separator where di GS1 structure require am; no add dem by yourself.
- If you build **without any AI at all**, e go throw `GaiaBuilderException("No AIs supplied")` wit empty `getErrors()` list — na di only failure wey no dey carry any `GaiaError`.
- Any AI wey im value fail im format rule or im check digit go make di whole build fail.

### Attribute AI need dia own identification key

Most AI na *attributes* wey di GS1 General Specifications require make identification key follow dem, and di builder dey enforce am — e dey validate through di full syntax stage, and no way dey to escape am. Batch/lot or serial number wey stand alone **no** be valid element string:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identification keys (GTIN `01`, SSCC `00`, GLN `414`, …) and company-internal AI (`90`–`99`) fit stand alone, and e correct completely. Everything else need companion.

> You fit tell `GaiaParser` make e skip dis check, wit `ParseConfig.skipRequiresCheck(true)`; but `GaiaBuilder` deliberately no dey expose anything like dat — im purpose na to emit output wey follow di standard. If you wan assemble element string wey you deliberately leave incomplete, assemble am by yourself and parse am wit di check off.

---

## How to build Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Valid Digital Link require exactly one **primary identification key** (like GTIN `01`, GLN `414`, SSCC `00`). Di builder dey classify every AI wey you supply:

| Role | How dem dey render am | Example |
|------|-------------|---------|
| Primary identification key | Path segment wey come after di domain/prefix | `/01/09506000134352` |
| Key qualifier (CPV `22`, batch `10`, serial `21`, …) | Di path segments wey follow, **for di canonical §4.9 order** (no be di order wey you add dem) | `/10/LOT-ABC` |
| Data attribute (everything else) | Query parameters, **sorted lexically by AI key** (§4.12) | `?17=271231` |

Because dem dey reorder di qualifiers when dem dey render, e no be problem if you supply dem out of order — `ai("21", …)` before `ai("10", …)` still dey render as `/10/LOT/21/SER`. Na only di *set* of dem di primary key gats admit.

Dem dey percent-encode di values for both di path and di query.

Di build go fail (e go throw `GaiaBuilderException`, or e go return `BuildResult` wey fail) when:

- **no** primary identification key dey among di AI;
- **more than one** primary identification key dey;
- one AI **dey banned** for Digital Link (`03`, `8014`);
- di **key-qualifier sequence** no valid for di primary key wey dem choose (qualifier wey no dey follow dat key, or qualifiers wey comot from di order wey dem allow).

---

## BuilderDigitalLinkConfig

Pass `BuilderDigitalLinkConfig` if you wan control di scheme, di domain, di path prefix, extra query parameters, and di fragment:

```java
import tools.pantheum.gaia.config.BuilderDigitalLinkConfig;

BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder()
        .baseUrl("https://example.com/resolver")   // sets scheme, domain, and path prefix at once
        .addQueryParam("context", "retail")        // appended after the AI data attributes
        .fragment("section-2")
        .build();

String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildDigitalLinkUri(cfg);
// https://example.com/resolver/01/09506000134352?context=retail#section-2
```

| Builder method | Wetin e dey do | Default |
|----------------|---------|---------|
| `scheme(String)` | Di URI scheme; e gats be `http` or `https` | `https` |
| `domain(String)` | Di resolver authority — host or `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Path segments wey come before di first primary key; dem dey normalise di slash for both ends | *(none)* |
| `baseUrl(String)` | Convenience wey dey split one URL into `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Extra query parameter, wey dem dey append **after** di AI data attributes, for di order wey you insert dem; e dey percent-encoded | — |
| `fragment(String)` | Di URI fragment (no `#` for front); e dey percent-encoded | *(none)* |

`build()` dey validate di configuration sharp-sharp: scheme wey no be `http(s)`, or domain wey empty, go throw `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) na di default `https://id.gs1.org` wit nothing extra — exactly wetin `buildDigitalLinkUri()` wey no take argument dey use, and wetin `GS1AIObject.getCanonicalDigitalLink()` dey produce.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, path prefix `/r`.
- Extra query parameters dey always come after di attributes wey derive from AI, so di canonical AI order (§4.12) dey preserved.

`BuilderDigitalLinkConfig` no fit change; reuse one single instance as you like.

---

## Validation and errors

### Di build methods wey dey throw

`buildElementString()`, `buildDigitalLinkUri()` and `buildDigitalLinkUri(BuilderDigitalLinkConfig)` dey throw **`GaiaBuilderException`** (unchecked `RuntimeException`) when di AI no fit form output wey correct:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- For **content** failures (bad check digit, format wey no match, AI wey missing/banned), `getErrors()` dey carry di parser own `GaiaError` objects — di same objects wey [di parser guide document](GaiaParser-NigerianPidgin.md#gaiaerror).
- For **Digital Link structure** failures (no primary key, plenty primary keys, AI wey dem ban, key-qualifier sequence wey no valid), `getErrors()` dey carry one single `GaiaError` (code `GE-L008`, `GE-L012`, `GE-L013` or `GE-L014`) wey dem localize to di builder language.

### Di tryBuild\* methods wey no dey throw

When di input dey come from user and failure na something wey you expect and fit handle, use di `tryBuild*` variants instead of make you dey control flow wit exception. Dem dey return [`BuildResult`](#buildresult) instead of throwing:

```java
BuildResult r = GaiaBuilder.create()
        .ai("01", userValue)
        .tryBuildElementString();

if (r.isSuccess()) {
    use(r.getValue());
} else {
    report(r.getMessage(), r.getErrors());
}
```

| Dey throw | No dey throw |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Every `tryBuild*` method dey share di same validation core wit im twin wey dey throw; na only di failure boundary different.

### Di language of error messages

Content validation errors dey come from di error catalogue wey dem localize. Call `language(...)` to pick di language of di `GaiaError` messages wey `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` dey carry; di default na English:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Na di same `GaiaConstants.Language` setting wey `GaiaParser` dey accept through `ParseConfig`, so di builder and di parser dey localize di same way.

Di `GaiaError` messages for both **content** failures and **Digital Link structure** failures (no primary key, plenty primary keys, AI wey dem ban, key-qualifier sequence wey no valid) — di two of dem dey localized through di shared error catalogue; di last one dey use di codes `GE-L008`, `GE-L012`, `GE-L013` and `GE-L014`.

### BuildResult

`BuildResult` (package `tools.pantheum.gaia.result`) na value type wey no fit change, wey dey describe di outcome of one `tryBuild*` call:

| Method | When e succeed | When e fail |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | Di string wey dem render | `null` |
| `getMessage()` | `null` | Description of di failure |
| `getErrors()` | Empty list | Di validation errors (same as di ones for `GaiaBuilderException.getErrors()`) |

---

## Check digits

Di builder dey validate check digit but e **no** dey calculate am — your values gats already carry dia check digit. To calculate one, use `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` dey apply di standard GS1 modulo-10 algorithm to di digits wey you give, and e dey return check digit from `0` reach `9`, or `-1` if di input na `null`, empty, or e no be number.

---

## Thread Safety

`GaiaBuilder` **no** dey safe for thread, and dem design am for single use: call `create()`, add di AI, build once. Construct new builder for every output; no share one builder across threads.

`BuilderDigitalLinkConfig` (and di `BuildResult` outputs wey e dey produce) no fit change, and you fit share dem as you like — build one config for startup and reuse am across plenty builders.

---

## API Reference

### `GaiaBuilder`

| Method | Description |
|--------|-------------|
| `static GaiaBuilder create()` | E dey start new builder wey empty. |
| `GaiaBuilder ai(String ai, String value)` | E dey append one AI and im complete value. E dey throw `IllegalArgumentException` if any of dem na `null`, or if `ai` no be GS1 Application Identifier wey dem sabi. |
| `GaiaBuilder language(GaiaConstants.Language language)` | E dey set di language of di content validation error messages (default na English). Dem dey ignore `null`. |
| `String buildElementString()` | E dey render GS1 element string. E dey throw `GaiaBuilderException` when e fail. |
| `String buildDigitalLinkUri()` | E dey render canonical Digital Link URI. E dey throw `GaiaBuilderException` when e fail. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | E dey render Digital Link URI according to `config`. E dey throw `GaiaBuilderException` when e fail. |
| `BuildResult tryBuildElementString()` | Element string build wey no dey throw. |
| `BuildResult tryBuildDigitalLinkUri()` | Canonical Digital Link build wey no dey throw. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Digital Link build according to `config`, wey no dey throw. |

### `BuilderDigitalLinkConfig`

| Member | Description |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Di default `https://id.gs1.org`. |
| `static Builder builder()` | New config builder. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Di scheme, di resolver authority and di path prefix wey dem resolve. |
| `getExtraQueryParams()` | Di extra query parameters, for di order wey dem insert dem. |
| `getFragment()` | Di fragment, or `null`. |

### `GaiaBuilderException`

| Member | Description |
|--------|-------------|
| `getErrors()` | Di `GaiaError` objects wey cause di failure — parser errors for content failure, or one single Digital Link structural error (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). E never dey `null`. |

### `BuildResult`

| Member | Description |
|--------|-------------|
| `isSuccess()` | Whether di build succeed. |
| `getValue()` | Di output wey dem render when e succeed; `null` when e fail. |
| `getMessage()` | Description of di failure when e fail; `null` when e succeed. |
| `getErrors()` | Di validation errors when e fail; empty when e succeed. E never dey `null`. |
| `getTiming()` | Di `ProcessingTiming` for di build operation (start time, processing duration), or `null`. |

---

See also: **[GaiaParser — Developer Guide](GaiaParser-NigerianPidgin.md)** for di parsing side, di AI element model, di error reference, and di appendices of AI and interpretation constants.
