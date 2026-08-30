# GAIA

**GS1 Application Identifier Analyzer** — a Java library that parses and builds the encoded data
inside GS1 data carriers.

## What are GS1 Application Identifiers?

An Application Identifier is a short numeric prefix — two to four digits — that declares
what the value after it means. `01` introduces a 14-digit GTIN, `10` a batch number, `17`
an expiry date, and so on across 541 of them.

Because every value announces its own meaning, one barcode can carry several unrelated
fields in a single unbroken string, and anything reading it can pick out the parts it
cares about without knowing what the product is:

| | |
|---|---|
| `01` | an Application Identifier — a 14-digit GTIN follows |
| `09506000134352` | the GTIN |
| `10` | an Application Identifier — a batch number follows |
| `LOT-ABC` | the batch number |

Concatenated, that is `010950600013435210LOT-ABC` — one string, two fields, no separators
needed because `01` is defined as fixed-length. GAIA reads strings like this, splits them
into typed values, and checks each one against the rules GS1 defines for that AI.

## Supported

| | Support |
|---|---|
| **GS1 Application Identifiers** | All **541** AIs defined in GS1 General Specifications Release 26.0 — parsed, validated and built |
| **GS1 Digital Link** | URI Syntax Release 1.7.0 — parsed and built, AIs in the path and in the query string |

### GS1 data carriers

Scanners prefix their output with an AIM Code ID identifying the symbology it came from.
GAIA recognises these as GS1 carriers and reads them accordingly:

| Carrier | AIM Code ID | Type | Application Identifiers | Digital Link |
|---|---|---|---|---|
| GS1-128 | `]C1` | Linear | ✓ | |
| GS1 DataBar / GS1 Composite | `]e0` `]e1` `]e2` | Linear | ✓ | |
| GS1 DataMatrix | `]d2` | 2D | ✓ | ✓ |
| GS1 DotCode | `]J1` | 2D | ✓ | ✓ |
| GS1 QR Code | `]Q3` | 2D | ✓ | ✓ |
| EAN-13 / UPC-A / UPC-E | `]E0` … `]E3` | Linear | Converted to AI `(01)` | |
| EAN-8 | `]E4` | Linear | Converted to AI `(01)` | |
| ITF-14 | `]I0` … `]I2` | Linear | Converted to AI `(01)` | |

The first five carry Application Identifiers directly; the three retail and logistics
symbols carry a bare GTIN and nothing else. The registry behind this table holds 137 AIM
Code ID entries across 37 symbology families, so a scan from a non-GS1 symbology — Code
39, PDF417, MaxiCode and the rest — is reported as the symbology it actually is rather
than failing as malformed GS1 data.

## Install

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Parse an element string or URI

```java
ParseResult result = new GaiaParser().parse("010950600013435210LOT-ABC");

System.out.println(result.isValid());        // true

result.getAiObject().getAis().forEach(e ->
    System.out.println("(" + e.getAi() + ") " + e.getTitle() + " = " + e.getValue()));
```

```
(01) GTIN = 09506000134352
(10) BATCH/LOT = LOT-ABC
```

The same call takes a GS1 Digital Link URI — same method, no flag to set:

```java
ParseResult result = new GaiaParser()
        .parse("https://id.acme.com/01/09506000134352/10/LOT-ABC?17=271231");
```

```
(01) GTIN = 09506000134352
(10) BATCH/LOT = LOT-ABC
(17) USE BY or EXPIRY = 271231
```

AIs in the path and AIs in the query string come back the same way, so what you do with
the result does not depend on which form the data arrived in.

GAIA works out the input shape for you. As well as these two it accepts raw scanner
output with an AIM Code ID prefix, and a few other forms.

## Build an element string or URI

```java
String element = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .buildElementString();
```

```
010950600013435210LOT-ABC
```

The same values as a Digital Link URI, on your own domain:

```java
BuilderDigitalLinkConfig config = BuilderDigitalLinkConfig.builder()
        .domain("id.acme.com")
        .build();

String uri = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("10", "LOT-ABC")
        .buildDigitalLinkUri(config);
```

```
https://id.acme.com/01/09506000134352/10/LOT-ABC
```

Omit the config and GAIA uses the GS1 canonical domain, `id.gs1.org`.

## Catch bad data

```java
ParseResult result = new GaiaParser().parse("0109506000134353");

System.out.println(result.isValid());        // false

result.getErrors().forEach(e ->
    System.out.println(e.getId() + ": " + e.getMessage()));
```

```
GE-C003: Check digit validation failed for AI (01) value '09506000134353'
```

## Multi-language support

Error messages, data titles and value interpretations are translated into 35 languages.
Pick one per call:

```java
ParseConfig config = ParseConfig.builder()
        .language(GaiaConstants.Language.FRENCH)
        .build();

new GaiaParser().parse("0109506000134352", config);
// (01) Numéro international d'article commercial (GTIN)
```

The developer guides are translated too:

| Language | | Quick start | Parser guide | Builder guide |
|---|---|---|---|---|
| English | English | [Quick start](docs/GaiaParser-QuickStart.md) | [Parser](docs/GaiaParser.md) | [Builder](docs/GaiaBuilder.md) |
| Arabic | العربية | [Quick start](docs/i18n/Arabic/GaiaParser-QuickStart-Arabic.md) | [Parser](docs/i18n/Arabic/GaiaParser-Arabic.md) | [Builder](docs/i18n/Arabic/GaiaBuilder-Arabic.md) |
| Bengali | বাংলা | [Quick start](docs/i18n/Bengali/GaiaParser-QuickStart-Bengali.md) | [Parser](docs/i18n/Bengali/GaiaParser-Bengali.md) | [Builder](docs/i18n/Bengali/GaiaBuilder-Bengali.md) |
| Cantonese (Yue) | 粵語 | [Quick start](docs/i18n/Cantonese/GaiaParser-QuickStart-Cantonese.md) | [Parser](docs/i18n/Cantonese/GaiaParser-Cantonese.md) | [Builder](docs/i18n/Cantonese/GaiaBuilder-Cantonese.md) |
| Chinese (Simplified) | 简体中文 | [Quick start](docs/i18n/Chinese/GaiaParser-QuickStart-Chinese.md) | [Parser](docs/i18n/Chinese/GaiaParser-Chinese.md) | [Builder](docs/i18n/Chinese/GaiaBuilder-Chinese.md) |
| Czech | Čeština | [Quick start](docs/i18n/Czech/GaiaParser-QuickStart-Czech.md) | [Parser](docs/i18n/Czech/GaiaParser-Czech.md) | [Builder](docs/i18n/Czech/GaiaBuilder-Czech.md) |
| Dutch | Nederlands | [Quick start](docs/i18n/Dutch/GaiaParser-QuickStart-Dutch.md) | [Parser](docs/i18n/Dutch/GaiaParser-Dutch.md) | [Builder](docs/i18n/Dutch/GaiaBuilder-Dutch.md) |
| Egyptian Arabic | العربية المصرية | [Quick start](docs/i18n/EgyptianArabic/GaiaParser-QuickStart-EgyptianArabic.md) | [Parser](docs/i18n/EgyptianArabic/GaiaParser-EgyptianArabic.md) | [Builder](docs/i18n/EgyptianArabic/GaiaBuilder-EgyptianArabic.md) |
| French | Français | [Quick start](docs/i18n/French/GaiaParser-QuickStart-French.md) | [Parser](docs/i18n/French/GaiaParser-French.md) | [Builder](docs/i18n/French/GaiaBuilder-French.md) |
| German | Deutsch | [Quick start](docs/i18n/German/GaiaParser-QuickStart-German.md) | [Parser](docs/i18n/German/GaiaParser-German.md) | [Builder](docs/i18n/German/GaiaBuilder-German.md) |
| Hausa | Hausa | [Quick start](docs/i18n/Hausa/GaiaParser-QuickStart-Hausa.md) | [Parser](docs/i18n/Hausa/GaiaParser-Hausa.md) | [Builder](docs/i18n/Hausa/GaiaBuilder-Hausa.md) |
| Hindi | हिन्दी | [Quick start](docs/i18n/Hindi/GaiaParser-QuickStart-Hindi.md) | [Parser](docs/i18n/Hindi/GaiaParser-Hindi.md) | [Builder](docs/i18n/Hindi/GaiaBuilder-Hindi.md) |
| Indonesian | Bahasa Indonesia | [Quick start](docs/i18n/Indonesian/GaiaParser-QuickStart-Indonesian.md) | [Parser](docs/i18n/Indonesian/GaiaParser-Indonesian.md) | [Builder](docs/i18n/Indonesian/GaiaBuilder-Indonesian.md) |
| Italian | Italiano | [Quick start](docs/i18n/Italian/GaiaParser-QuickStart-Italian.md) | [Parser](docs/i18n/Italian/GaiaParser-Italian.md) | [Builder](docs/i18n/Italian/GaiaBuilder-Italian.md) |
| Japanese | 日本語 | [Quick start](docs/i18n/Japanese/GaiaParser-QuickStart-Japanese.md) | [Parser](docs/i18n/Japanese/GaiaParser-Japanese.md) | [Builder](docs/i18n/Japanese/GaiaBuilder-Japanese.md) |
| Javanese | Basa Jawa | [Quick start](docs/i18n/Javanese/GaiaParser-QuickStart-Javanese.md) | [Parser](docs/i18n/Javanese/GaiaParser-Javanese.md) | [Builder](docs/i18n/Javanese/GaiaBuilder-Javanese.md) |
| Korean | 한국어 | [Quick start](docs/i18n/Korean/GaiaParser-QuickStart-Korean.md) | [Parser](docs/i18n/Korean/GaiaParser-Korean.md) | [Builder](docs/i18n/Korean/GaiaBuilder-Korean.md) |
| Marathi | मराठी | [Quick start](docs/i18n/Marathi/GaiaParser-QuickStart-Marathi.md) | [Parser](docs/i18n/Marathi/GaiaParser-Marathi.md) | [Builder](docs/i18n/Marathi/GaiaBuilder-Marathi.md) |
| Nigerian Pidgin | Naijá | [Quick start](docs/i18n/NigerianPidgin/GaiaParser-QuickStart-NigerianPidgin.md) | [Parser](docs/i18n/NigerianPidgin/GaiaParser-NigerianPidgin.md) | [Builder](docs/i18n/NigerianPidgin/GaiaBuilder-NigerianPidgin.md) |
| Persian (Farsi) | فارسی | [Quick start](docs/i18n/Persian/GaiaParser-QuickStart-Persian.md) | [Parser](docs/i18n/Persian/GaiaParser-Persian.md) | [Builder](docs/i18n/Persian/GaiaBuilder-Persian.md) |
| Polish | Polski | [Quick start](docs/i18n/Polish/GaiaParser-QuickStart-Polish.md) | [Parser](docs/i18n/Polish/GaiaParser-Polish.md) | [Builder](docs/i18n/Polish/GaiaBuilder-Polish.md) |
| Portuguese | Português | [Quick start](docs/i18n/Portuguese/GaiaParser-QuickStart-Portuguese.md) | [Parser](docs/i18n/Portuguese/GaiaParser-Portuguese.md) | [Builder](docs/i18n/Portuguese/GaiaBuilder-Portuguese.md) |
| Punjabi | ਪੰਜਾਬੀ | [Quick start](docs/i18n/Punjabi/GaiaParser-QuickStart-Punjabi.md) | [Parser](docs/i18n/Punjabi/GaiaParser-Punjabi.md) | [Builder](docs/i18n/Punjabi/GaiaBuilder-Punjabi.md) |
| Russian | Русский | [Quick start](docs/i18n/Russian/GaiaParser-QuickStart-Russian.md) | [Parser](docs/i18n/Russian/GaiaParser-Russian.md) | [Builder](docs/i18n/Russian/GaiaBuilder-Russian.md) |
| Spanish | Español | [Quick start](docs/i18n/Spanish/GaiaParser-QuickStart-Spanish.md) | [Parser](docs/i18n/Spanish/GaiaParser-Spanish.md) | [Builder](docs/i18n/Spanish/GaiaBuilder-Spanish.md) |
| Swahili | Kiswahili | [Quick start](docs/i18n/Swahili/GaiaParser-QuickStart-Swahili.md) | [Parser](docs/i18n/Swahili/GaiaParser-Swahili.md) | [Builder](docs/i18n/Swahili/GaiaBuilder-Swahili.md) |
| Swedish | Svenska | [Quick start](docs/i18n/Swedish/GaiaParser-QuickStart-Swedish.md) | [Parser](docs/i18n/Swedish/GaiaParser-Swedish.md) | [Builder](docs/i18n/Swedish/GaiaBuilder-Swedish.md) |
| Tagalog (Filipino) | Tagalog | [Quick start](docs/i18n/Tagalog/GaiaParser-QuickStart-Tagalog.md) | [Parser](docs/i18n/Tagalog/GaiaParser-Tagalog.md) | [Builder](docs/i18n/Tagalog/GaiaBuilder-Tagalog.md) |
| Tamil | தமிழ் | [Quick start](docs/i18n/Tamil/GaiaParser-QuickStart-Tamil.md) | [Parser](docs/i18n/Tamil/GaiaParser-Tamil.md) | [Builder](docs/i18n/Tamil/GaiaBuilder-Tamil.md) |
| Telugu | తెలుగు | [Quick start](docs/i18n/Telugu/GaiaParser-QuickStart-Telugu.md) | [Parser](docs/i18n/Telugu/GaiaParser-Telugu.md) | [Builder](docs/i18n/Telugu/GaiaBuilder-Telugu.md) |
| Turkish | Türkçe | [Quick start](docs/i18n/Turkish/GaiaParser-QuickStart-Turkish.md) | [Parser](docs/i18n/Turkish/GaiaParser-Turkish.md) | [Builder](docs/i18n/Turkish/GaiaBuilder-Turkish.md) |
| Ukrainian | Українська | [Quick start](docs/i18n/Ukrainian/GaiaParser-QuickStart-Ukrainian.md) | [Parser](docs/i18n/Ukrainian/GaiaParser-Ukrainian.md) | [Builder](docs/i18n/Ukrainian/GaiaBuilder-Ukrainian.md) |
| Urdu | اردو | [Quick start](docs/i18n/Urdu/GaiaParser-QuickStart-Urdu.md) | [Parser](docs/i18n/Urdu/GaiaParser-Urdu.md) | [Builder](docs/i18n/Urdu/GaiaBuilder-Urdu.md) |
| Vietnamese | Tiếng Việt | [Quick start](docs/i18n/Vietnamese/GaiaParser-QuickStart-Vietnamese.md) | [Parser](docs/i18n/Vietnamese/GaiaParser-Vietnamese.md) | [Builder](docs/i18n/Vietnamese/GaiaBuilder-Vietnamese.md) |
| Wu Chinese | 吳語 | [Quick start](docs/i18n/WuChinese/GaiaParser-QuickStart-WuChinese.md) | [Parser](docs/i18n/WuChinese/GaiaParser-WuChinese.md) | [Builder](docs/i18n/WuChinese/GaiaBuilder-WuChinese.md) |

Language not listed? Request a new one through
[Issues → Feature Request](https://github.com/mgsmith-parsers/gaia/issues/new).

## Requirements

Java 11 or later. The library runs unchanged on every later JVM.

## Reporting issues

Bugs and feature requests go to
[GitHub Issues](https://github.com/mgsmith-parsers/gaia/issues). Include the input string,
what you expected, what GAIA returned — and, most importantly, a reference to the standard, with its release number and section.

## Licence

[Apache License 2.0](LICENSE) — this covers GAIA's own source code and documentation only.

## GS1 standards

All GS1 standards, specifications and identification keys are owned by GS1 and remain the
property of GS1. This includes the GS1 General Specifications, GS1 Digital Link, the
Application Identifiers, and identification keys such as GTIN, SSCC, GLN, GRAI and GIAI.
"GS1" and the identification key names are trademarks of GS1 AISBL.

GAIA is an independent implementation of those published standards. It is not affiliated
with, endorsed by, or certified by GS1. The Apache licence above applies to this library,
not to the standards it implements — for the standards themselves, and for permission to
use them, refer to [gs1.org](https://www.gs1.org/standards).

Using GS1 identification keys in your own products generally requires membership of a GS1
Member Organisation. This library does not grant any such right.
