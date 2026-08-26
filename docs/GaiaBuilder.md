# GaiaBuilder — Developer Guide

## Table of Contents

1. [Overview](#overview)
2. [About GS1 and the General Specifications](#about-gs1-and-the-general-specifications)
3. [Quick Start](#quick-start)
4. [How It Works](#how-it-works)
5. [Building Element Strings](#building-element-strings)
   - [Attribute AIs need their identification key](#attribute-ais-need-their-identification-key)
6. [Building Digital Link URIs](#building-digital-link-uris)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validation and Errors](#validation-and-errors)
   - [Throwing build methods](#throwing-build-methods)
   - [Non-throwing tryBuild\* methods](#non-throwing-trybuild-methods)
   - [Error-message language](#error-message-language)
   - [BuildResult](#buildresult)
9. [Check Digits](#check-digits)
10. [Thread Safety](#thread-safety)
11. [API Reference](#api-reference)

---

## Overview

`GaiaBuilder` is the inverse of [`GaiaParser`](GaiaParser.md): it turns a set of Application Identifier (AI) / value pairs into a well-formed GS1 **element string** or **GS1 Digital Link URI**. You supply the AIs and their complete data values; the builder assembles them, validates the result through the same engine `GaiaParser` uses, and renders the output.

Because the builder validates by *parsing its own candidate output*, anything it returns is guaranteed to parse cleanly back through `GaiaParser` — the two can never disagree about what is well-formed.

**Entry point class:** `tools.pantheum.gaia.GaiaBuilder`

---

## About GS1 and the General Specifications

**GS1** is a global not-for-profit organisation that develops and maintains open standards for supply-chain identification and data exchange. Its standards are used in retail, healthcare, logistics, foodservice, and many other industries, covering everything from product barcodes on consumer packaging to serialised tracking of pharmaceutical doses.

The authoritative reference for everything this builder implements is the **GS1 General Specifications** — a single document that defines:

- All Application Identifier (AI) codes, their data titles, formats, and validation rules
- The syntax rules for constructing and encoding AI element strings
- Barcode symbology requirements and AIM Code ID assignments
- Check digit and check character algorithms
- Two-digit year resolution (the sliding-window rule)
- Data Matrix, QR Code, GS1-128, GS1 DataBar, and other carrier specifications

The GS1 General Specifications are updated annually. The current edition and supporting resources are available at:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implements **Release 26.0 (Ratified, Jan 2026)** of the GS1 General Specifications.

GS1 Digital Link URIs are governed by a companion standard, **GS1 Digital Link: URI Syntax**, which defines the primary identification keys, key-qualifier ordering, and data-attribute encoding the builder applies when rendering Digital Link URIs:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implements **Release 1.7.0 (Ratified, Aug 2026)** of the GS1 Digital Link: URI Syntax standard.

Section references throughout this document refer to the GS1 General Specifications (e.g. "Table 7-5", "section 7.12"), except Digital Link section numbers (e.g. "§4.9", "§4.12"), which refer to the GS1 Digital Link: URI Syntax standard.

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

Prefer the `GS1Constants_AICodes` constants over raw AI strings (see [Appendix A of the parser guide](GaiaParser.md#appendix-a--ai-string-constants)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## How It Works

Every build follows the same path:

1. **Assemble** — the AI/value pairs are concatenated into a candidate element string. An FNC1 group separator (`0x1D`) is inserted after every AI that *requires a separator* and is not the last element. Predefined-length AIs (GTIN, dates, fixed-length measures) take no separator; all others do. (Unrecognised AIs never reach this step — `ai(...)` rejects them eagerly; see [Building Element Strings](#building-element-strings).)
2. **Validate** — the candidate is parsed in `CONTENT` mode through `GaiaParser`. Each value is checked against its AI's format and check digit, and structural rules (required/excluded AI pairings) are enforced. If the parse is not valid, the build fails.
3. **Render** —
   - For an element string, the validated object's `toElementString()` is returned.
   - For a Digital Link, each element is assigned its DL role (primary key, key qualifier, or data attribute), the key-qualifier sequence is validated, the URI is emitted, and the emitted URI is **re-parsed to confirm it round-trips as a valid Digital Link** — a defensive check on the string-assembly and percent-encoding step. If it does not round-trip, a `GaiaBuilderException` is thrown.

This mirrors the reconstruction logic in `DLSyntaxParser`, so the separator placement and validation are identical to what the parser expects.

---

## Building Element Strings

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- The **AI** is validated eagerly: `ai(...)` throws `IllegalArgumentException` if it is not a recognised GS1 Application Identifier. (The builder concatenates AI and value before parsing, so an unrecognised or over-long AI such as `"99999"` must be caught here — otherwise it would be silently re-tokenised into a different AI.) The **value** is validated later, at build time.
- Values must be **complete**, including any check digit. The builder does not compute or append check digits for you — see [Check Digits](#check-digits).
- AIs are emitted in the order you add them. The builder inserts FNC1 separators where the GS1 syntax requires them; you do not add separators yourself.
- Building with **no AIs at all** throws `GaiaBuilderException("No AIs supplied")` with an empty `getErrors()` list — the only failure that carries no `GaiaError`.
- An AI whose value fails its format or check-digit rule causes the build to fail.

### Attribute AIs need their identification key

Most AIs are *attributes* that the GS1 General Specifications require to accompany an identification key, and the builder enforces that — it validates through the full syntax stage, with no way to opt out. A batch/lot or serial on its own is **not** a valid element string:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identification keys (GTIN `01`, SSCC `00`, GLN `414`, …) and the company-internal AIs (`90`–`99`) stand alone quite legitimately. Everything else needs its companion.

> `GaiaParser` can be told to skip this check with `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` deliberately exposes no equivalent — it is meant to emit standards-conformant output. To assemble a deliberately partial element string, concatenate it yourself and parse it with the check disabled.

---

## Building Digital Link URIs

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

A valid Digital Link requires exactly one **primary identification key** (e.g. GTIN `01`, GLN `414`, SSCC `00`). The builder classifies each supplied AI:

| Role | Rendered as | Example |
|------|-------------|---------|
| Primary identification key | Path segment after the domain/prefix | `/01/09506000134352` |
| Key qualifier (CPV `22`, batch `10`, serial `21`, …) | Subsequent path segments, in the **canonical §4.9 order** (not the order you added them) | `/10/LOT-ABC` |
| Data attribute (everything else) | Query parameters, **sorted lexically by AI key** (§4.12) | `?17=271231` |

Because qualifiers are reordered on emit, supplying them out of sequence is fine — `ai("21", …)` before `ai("10", …)` still renders `/10/LOT/21/SER`. Only the *set* has to be admissible for the primary key.

Values in both the path and the query are percent-encoded.

The build **fails** (throws `GaiaBuilderException`, or returns a failed `BuildResult`) when:

- there is **no** primary identification key among the AIs;
- there is **more than one** primary identification key;
- an AI is **banned** in Digital Links (`03`, `8014`);
- the **key-qualifier sequence** is invalid for the chosen primary key (e.g. a qualifier that does not belong to that key, or qualifiers out of their permitted order).

---

## BuilderDigitalLinkConfig

Pass a `BuilderDigitalLinkConfig` to control the scheme, domain, path prefix, extra query parameters, and fragment:

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

| Builder method | Purpose | Default |
|----------------|---------|---------|
| `scheme(String)` | URI scheme; must be `http` or `https` | `https` |
| `domain(String)` | Authority — host or `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Path segments before the first primary key; leading/trailing slashes normalised | *(none)* |
| `baseUrl(String)` | Convenience that splits a URL into `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Extra query parameter, appended **after** the AI data attributes, in insertion order; percent-encoded | — |
| `fragment(String)` | URL fragment (without the leading `#`); percent-encoded | *(none)* |

`build()` validates the configuration eagerly: a non-`http(s)` scheme or a blank domain throws `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) is the `https://id.gs1.org` default with no extras — exactly what `buildDigitalLinkUri()` with no argument uses, and what `GS1AIObject.getCanonicalDigitalLink()` produces.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, path prefix `/r`.
- Extra query parameters always follow the AI-derived attributes, so the canonical AI ordering (§4.12) is preserved.

`BuilderDigitalLinkConfig` is immutable; reuse one instance freely.

---

## Validation and Errors

### Throwing build methods

`buildElementString()`, `buildDigitalLinkUri()`, and `buildDigitalLinkUri(BuilderDigitalLinkConfig)` throw **`GaiaBuilderException`** (an unchecked `RuntimeException`) when the AIs cannot form well-formed output:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- For **content** failures (bad check digit, format mismatch, missing/excluded AI), `getErrors()` carries the parser's `GaiaError`s — the same objects [documented in the parser guide](GaiaParser.md#gaiaerror).
- For **Digital Link structural** failures (no primary key, more than one primary key, banned AI, invalid key-qualifier sequence), `getErrors()` carries a single `GaiaError` (code `GE-L008`, `GE-L012`, `GE-L013`, or `GE-L014`) localized to the builder's language.

### Non-throwing tryBuild\* methods

When the input is user-supplied and failure is an expected, recoverable outcome, use the `tryBuild*` variants instead of exception control flow. They return a [`BuildResult`](#buildresult) rather than throwing:

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

| Throwing | Non-throwing |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Each `tryBuild*` method shares the same validation core as its throwing twin; only the failure boundary differs.

### Error-message language

Content-validation errors are drawn from the localized error catalogue. Call `language(...)` to choose the language of the `GaiaError` messages carried by `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; it defaults to English:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

This is the same `GaiaConstants.Language` setting `GaiaParser` accepts via `ParseConfig`, so the builder and parser localize identically.

Both the **content** `GaiaError` messages and the **Digital Link structural** failures (no primary key, more than one primary key, banned AI, invalid key-qualifier sequence) are localized via the shared error catalogue — the latter using codes `GE-L008`, `GE-L012`, `GE-L013`, and `GE-L014`.

### BuildResult

`BuildResult` (in package `tools.pantheum.gaia.result`) is an immutable value type describing the outcome of a `tryBuild*` call:

| Method | On success | On failure |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | the rendered string | `null` |
| `getMessage()` | `null` | failure description |
| `getErrors()` | empty list | the validation errors (same as `GaiaBuilderException.getErrors()`) |

---

## Check Digits

The builder validates check digits but does **not** compute them — values must already include their check digit. To compute one, use `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` applies the standard GS1 modulo-10 algorithm to the supplied digits and returns the check digit `0–9`, or `-1` if the input is null, empty, or non-numeric.

---

## Thread Safety

`GaiaBuilder` is **not** thread-safe and is intended for single use: call `create()`, add AIs, build once. Create a new builder per output; do not share one across threads.

`BuilderDigitalLinkConfig` (and its `BuildResult` outputs) are immutable and may be shared freely — build a config once at startup and reuse it across many builders.

---

## API Reference

### `GaiaBuilder`

| Method | Description |
|--------|-------------|
| `static GaiaBuilder create()` | Starts a new, empty builder. |
| `GaiaBuilder ai(String ai, String value)` | Appends an AI and its complete value. Throws `IllegalArgumentException` if either is `null`, or if `ai` is not a recognised GS1 Application Identifier. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Sets the language of content-validation error messages (default English). `null` is ignored. |
| `String buildElementString()` | Renders a GS1 element string. Throws `GaiaBuilderException` on failure. |
| `String buildDigitalLinkUri()` | Renders a canonical Digital Link URI. Throws `GaiaBuilderException` on failure. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Renders a Digital Link URI under `config`. Throws `GaiaBuilderException` on failure. |
| `BuildResult tryBuildElementString()` | Non-throwing element-string build. |
| `BuildResult tryBuildDigitalLinkUri()` | Non-throwing canonical Digital Link build. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Non-throwing Digital Link build under `config`. |

### `BuilderDigitalLinkConfig`

| Member | Description |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | The `https://id.gs1.org` default. |
| `static Builder builder()` | A new config builder. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Resolved scheme, authority, and path prefix. |
| `getExtraQueryParams()` | Extra query parameters, in insertion order. |
| `getFragment()` | Fragment, or `null`. |

### `GaiaBuilderException`

| Member | Description |
|--------|-------------|
| `getErrors()` | The `GaiaError`s that caused the failure — the parser's errors for a content failure, or a single Digital Link structural error (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Never `null`. |

### `BuildResult`

| Member | Description |
|--------|-------------|
| `isSuccess()` | Whether the build succeeded. |
| `getValue()` | Rendered output on success; `null` on failure. |
| `getMessage()` | Failure description on failure; `null` on success. |
| `getErrors()` | Validation errors on failure; empty on success. Never `null`. |
| `getTiming()` | `ProcessingTiming` for the build (start time, processing duration), or `null`. |

---

See also: **[GaiaParser — Developer Guide](GaiaParser.md)** for the parsing side, the AI element model, error reference, and AI/interpretation constant appendices.
