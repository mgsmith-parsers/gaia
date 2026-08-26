# GaiaBuilder — Příručka vývojáře

## Obsah

1. [Přehled](#přehled)
2. [O GS1 a dokumentu General Specifications](#o-gs1-a-dokumentu-general-specifications)
3. [Rychlý úvod](#rychlý-úvod)
4. [Jak to pracuje](#jak-to-pracuje)
5. [Sestavování řetězců prvků](#sestavování-řetězců-prvků)
   - [Atributové AI potřebují svůj identifikační klíč](#atributové-ai-potřebují-svůj-identifikační-klíč)
6. [Sestavování URI Digital Link](#sestavování-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Ověřování a chyby](#ověřování-a-chyby)
   - [Metody sestavení vyvolávající výjimky](#metody-sestavení-vyvolávající-výjimky)
   - [Metody tryBuild\* bez výjimek](#metody-trybuild-bez-výjimek)
   - [Jazyk chybových hlášení](#jazyk-chybových-hlášení)
   - [BuildResult](#buildresult)
9. [Kontrolní číslice](#kontrolní-číslice)
10. [Bezpečnost pro vlákna](#bezpečnost-pro-vlákna)
11. [Přehled API](#přehled-api)

---

## Přehled

`GaiaBuilder` je opakem [`GaiaParseru`](GaiaParser-Czech.md): mění soubor dvojic aplikační identifikátor (AI) / hodnota ve správně utvořený **řetězec prvků** GS1 nebo **URI GS1 Digital Link**. Vy dodáte identifikátory AI a jejich úplné datové hodnoty; tvůrce je složí, ověří výsledek týmž strojem, jehož užívá `GaiaParser`, a vydá výstup.

Protože tvůrce ověřuje tak, že *rozebírá vlastní navrhovaný výstup*, vše, co vrátí, se zaručeně bezchybně přečte zpět `GaiaParserem` — tito dva se nikdy nerozejdou v tom, co pokládat za správně utvořené.

**Vstupní třída:** `tools.pantheum.gaia.GaiaBuilder`

---

## O GS1 a dokumentu General Specifications

**GS1** je celosvětová nezisková organizace, která vytváří a spravuje otevřené standardy pro identifikaci a výměnu dat v dodavatelských řetězcích. Její standardy se používají v maloobchodu, zdravotnictví, logistice, stravovacích službách a mnoha dalších odvětvích — od čárových kódů na spotřebitelských obalech až po sériové sledování farmaceutických dávek.

Závazným pramenem pro vše, co tento tvůrce uskutečňuje, je dokument **GS1 General Specifications** — jediný dokument, který vymezuje:

- Všechny kódy aplikačních identifikátorů (AI), jejich datové názvy, formáty a pravidla ověřování
- Pravidla skladby pro sestavování a kódování řetězců prvků AI
- Požadavky na symboliky čárových kódů a přidělování identifikátorů symboliky AIM
- Algoritmy kontrolní číslice a kontrolního znaku
- Rozvinutí dvoumístného označení roku (pravidlo posuvného okna)
- Specifikace Data Matrix, QR Code, GS1-128, GS1 DataBar a dalších nosičů dat

Dokument GS1 General Specifications se aktualizuje každoročně. Platné vydání a doprovodné materiály jsou dostupné na adrese:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA uskutečňuje **vydání 26.0 (schválené v lednu 2026)** dokumentu GS1 General Specifications.

URI GS1 Digital Link se řídí samostatným standardem **GS1 Digital Link: URI Syntax**, který vymezuje primární identifikační klíče, pořadí kvalifikátorů klíče a kódování datových atributů, jež tvůrce uplatňuje při vydávání URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA uskutečňuje **vydání 1.7.0 (schválené v srpnu 2026)** standardu GS1 Digital Link: URI Syntax.

Odkazy na oddíly v celém tomto dokumentu se vztahují k GS1 General Specifications (například „Table 7-5“, „section 7.12“), s výjimkou čísel oddílů Digital Link (například „§4.9“, „§4.12“), která odkazují na standard GS1 Digital Link: URI Syntax.

---

## Rychlý úvod

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

Dejte přednost konstantám `GS1Constants_AICodes` před surovými řetězci AI (viz [příloha A příručky k analyzátoru](GaiaParser-Czech.md#příloha-a--řetězcové-konstanty-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Jak to pracuje

Každé sestavení jde touž cestou:

1. **Složení** — dvojice AI/hodnota se spojí do navrhovaného řetězce prvků. Za každý AI, který *vyžaduje oddělovač* a není posledním prvkem, se vloží skupinový oddělovač FNC1 (`0x1D`). Identifikátory AI předem dané délky (GTIN, data, míry pevné délky) oddělovač nedostanou; všechny ostatní ano. (Nerozpoznané AI se k tomuto kroku vůbec nedostanou — `ai(...)` je ihned odmítne; viz [Sestavování řetězců prvků](#sestavování-řetězců-prvků).)
2. **Ověření** — návrh se rozebere v režimu `CONTENT` pomocí `GaiaParseru`. Každá hodnota se porovná s formátem a kontrolní číslicí svého AI a uplatní se strukturní pravidla (povinné či vylučující se dvojice AI). Není-li rozbor platný, sestavení selže.
3. **Vydání** —
   - U řetězce prvků se vrátí `toElementString()` ověřeného objektu.
   - U Digital Link se každému prvku přiřadí jeho úloha DL (primární klíč, kvalifikátor klíče či datový atribut), ověří se posloupnost kvalifikátorů klíče, vydá se URI a tento URI se **rozebere znovu, aby se potvrdilo, že se přečte zpět jako platný Digital Link** — jde o ochrannou kontrolu skládání řetězce a kódování procenty. Nepodaří-li se tento okruh, vyvolá se `GaiaBuilderException`.

Tím se napodobuje logika obnovy z `DLSyntaxParseru`, takže rozmístění oddělovačů i ověřování se zcela shodují s tím, co analyzátor očekává.

---

## Sestavování řetězců prvků

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** se ověřuje ihned: `ai(...)` vyvolá `IllegalArgumentException`, není-li to rozpoznaný aplikační identifikátor GS1. (Tvůrce spojí AI s hodnotou ještě před rozborem, takže nerozpoznaný či příliš dlouhý AI jako `"99999"` je nutné zachytit zde — jinak by se mlčky znovu rozdělil na lexémy jako jiný AI.) **Hodnota** se naproti tomu ověřuje později, při sestavování.
- Hodnoty musí být **úplné**, včetně případné kontrolní číslice. Tvůrce kontrolní číslice za vás nepočítá ani nedoplňuje — viz [Kontrolní číslice](#kontrolní-číslice).
- Identifikátory AI se vydávají v pořadí, v jakém je přidáte. Tvůrce vkládá oddělovače FNC1 tam, kde je skladba GS1 vyžaduje; sami je nedoplňujte.
- Sestavení **zcela bez AI** vyvolá `GaiaBuilderException("No AIs supplied")` s prázdným seznamem `getErrors()` — jediné selhání, které nenese žádný `GaiaError`.
- AI, jehož hodnota poruší pravidlo formátu či kontrolní číslice, sestavení zmaří.

### Atributové AI potřebují svůj identifikační klíč

Většina AI jsou *atributy*, jimž GS1 General Specifications ukládají doprovod identifikačním klíčem, a tvůrce to vymáhá: ověřuje úplným stupněm skladby, bez možnosti se toho zříci. Samotná šarže či samotné sériové číslo **netvoří** platný řetězec prvků:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identifikační klíče (GTIN `01`, SSCC `00`, GLN `414`, …) a AI pro vnitřní potřebu společnosti (`90`–`99`) mohou zcela oprávněně stát samostatně. Vše ostatní potřebuje svého průvodce.

> `GaiaParser` lze požádat, aby tuto kontrolu vynechal, pomocí `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` záměrně nic obdobného nenabízí — má vydávat výstup odpovídající standardům. Chcete-li složit záměrně neúplný řetězec prvků, spojte si jej sami a rozeberte jej s vypnutou kontrolou.

---

## Sestavování URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Platný Digital Link vyžaduje právě jeden **primární identifikační klíč** (například GTIN `01`, GLN `414`, SSCC `00`). Tvůrce každý dodaný AI zařadí:

| Úloha | Vydává se jako | Příklad |
|------|-------------|---------|
| Primární identifikační klíč | Úsek cesty za doménou či předponou | `/01/09506000134352` |
| Kvalifikátor klíče (CPV `22`, šarže `10`, sériové číslo `21`, …) | Další úseky cesty, v **kanonickém pořadí podle §4.9** (nikoli v pořadí přidání) | `/10/LOT-ABC` |
| Datový atribut (vše ostatní) | Parametry dotazu, **seřazené abecedně podle klíče AI** (§4.12) | `?17=271231` |

Protože se kvalifikátory při vydávání přerovnávají, není na závadu dodat je v libovolném pořadí: `ai("21", …)` před `ai("10", …)` dá přesto `/10/LOT/21/SER`. Přípustný pro primární klíč musí být jen samotný *soubor*.

Hodnoty se kódují procenty v cestě i v dotazu.

Sestavení **selže** (vyvolá `GaiaBuilderException`, případně vrátí neúspěšný `BuildResult`), když:

- mezi identifikátory AI **není** žádný primární identifikační klíč;
- primárních identifikačních klíčů je **více než jeden**;
- některý AI je v Digital Link **zakázán** (`03`, `8014`);
- **posloupnost kvalifikátorů klíče** je pro zvolený primární klíč nepřípustná (například kvalifikátor, jenž k tomuto klíči nepatří, nebo kvalifikátory mimo dovolené pořadí).

---

## BuilderDigitalLinkConfig

Předejte `BuilderDigitalLinkConfig`, chcete-li řídit schéma, doménu, předponu cesty, další parametry dotazu a fragment:

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

| Metoda tvůrce | Účel | Výchozí |
|----------------|---------|---------|
| `scheme(String)` | Schéma URI; musí být `http` nebo `https` | `https` |
| `domain(String)` | Autorita — uzel nebo `uzel:port` | `id.gs1.org` |
| `pathPrefix(String)` | Úseky cesty před prvním primárním klíčem; úvodní i koncová lomítka se sjednocují | *(žádná)* |
| `baseUrl(String)` | Zkratka rozkládající adresu URL na `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Další parametr dotazu, připojený **za** datové atributy AI, v pořadí vkládání; kódovaný procenty | — |
| `fragment(String)` | Fragment adresy URL (bez úvodního `#`); kódovaný procenty | *(žádný)* |

`build()` ověří nastavení ihned: schéma jiné než `http(s)` nebo prázdná doména vyvolají `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (přezdívka `defaultConfig()`) je výchozí `https://id.gs1.org` bez doplňků — přesně to, co používá `buildDigitalLinkUri()` bez argumentů a co vydává `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → schéma `http`, doména `id.example.org:8080`, předpona cesty `/r`.
- Další parametry dotazu jdou vždy až za atributy odvozenými z AI, takže se zachová kanonické pořadí AI (§4.12).

`BuilderDigitalLinkConfig` je neměnný; jedinou instanci lze volně používat opakovaně.

---

## Ověřování a chyby

### Metody sestavení vyvolávající výjimky

`buildElementString()`, `buildDigitalLinkUri()` a `buildDigitalLinkUri(BuilderDigitalLinkConfig)` vyvolají **`GaiaBuilderException`** (nekontrolovanou `RuntimeException`), nelze-li z identifikátorů AI utvořit správný výstup:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Při selháních **obsahu** (chybná kontrolní číslice, nesouhlasný formát, chybějící či vyloučený AI) nese `getErrors()` objekty `GaiaError` analyzátoru — tytéž, které [popisuje příručka k analyzátoru](GaiaParser-Czech.md#gaiaerror).
- Při **strukturních selháních Digital Link** (žádný primární klíč, více než jeden primární klíč, zakázaný AI, nepřípustná posloupnost kvalifikátorů klíče) nese `getErrors()` jediný `GaiaError` (kód `GE-L008`, `GE-L012`, `GE-L013` nebo `GE-L014`) lokalizovaný do jazyka tvůrce.

### Metody tryBuild\* bez výjimek

Pochází-li vstup od uživatele a je-li selhání očekávaným a napravitelným výsledkem, používejte obměny `tryBuild*` namísto řízení toku výjimkami. Vracejí [`BuildResult`](#buildresult) místo toho, aby vyvolaly výjimku:

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

| S výjimkou | Bez výjimky |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Každá metoda `tryBuild*` sdílí totéž ověřovací jádro se svým protějškem vyvolávajícím výjimky; liší se jen způsob, jak se selhání oznámí.

### Jazyk chybových hlášení

Chyby ověření obsahu pocházejí z lokalizovaného katalogu chyb. Zavolejte `language(...)`, chcete-li zvolit jazyk hlášení objektů `GaiaError`, které nesou `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; výchozí je angličtina:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Je to totéž nastavení `GaiaConstants.Language`, jaké `GaiaParser` přijímá přes `ParseConfig`, takže tvůrce i analyzátor se lokalizují shodně.

Jak hlášení `GaiaError` o **obsahu**, tak **strukturní selhání Digital Link** (žádný primární klíč, více než jeden primární klíč, zakázaný AI, nepřípustná posloupnost kvalifikátorů klíče) se lokalizují společným katalogem chyb — ta druhá pomocí kódů `GE-L008`, `GE-L012`, `GE-L013` a `GE-L014`.

### BuildResult

`BuildResult` (v balíčku `tools.pantheum.gaia.result`) je neměnný hodnotový typ popisující výsledek volání `tryBuild*`:

| Metoda | Při úspěchu | Při selhání |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | vydaný řetězec | `null` |
| `getMessage()` | `null` | popis selhání |
| `getErrors()` | prázdný seznam | chyby ověření (tytéž jako u `GaiaBuilderException.getErrors()`) |

---

## Kontrolní číslice

Tvůrce kontrolní číslice ověřuje, ale **nepočítá** je — hodnoty už tu svou obsahovat musí. Chcete-li ji vypočítat, použijte `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` uplatní na dodané číslice obvyklý algoritmus GS1 modulo 10 a vrátí kontrolní číslici `0–9`, nebo `-1`, je-li vstup null, prázdný či nečíselný.

---

## Bezpečnost pro vlákna

`GaiaBuilder` **není** bezpečný pro vlákna a je určen k jednorázovému použití: zavolejte `create()`, přidejte identifikátory AI, sestavte jednou. Pro každý výstup vytvořte nového tvůrce; nesdílejte jednoho mezi vlákny.

`BuilderDigitalLinkConfig` (a jím vydávané objekty `BuildResult`) jsou neměnné a lze je volně sdílet — sestavte nastavení jednou při spuštění a používejte je v mnoha tvůrcích.

---

## Přehled API

### `GaiaBuilder`

| Metoda | Popis |
|--------|-------------|
| `static GaiaBuilder create()` | Zahájí nového, prázdného tvůrce. |
| `GaiaBuilder ai(String ai, String value)` | Přidá AI a jeho úplnou hodnotu. Vyvolá `IllegalArgumentException`, je-li kterýkoli z argumentů `null` nebo není-li `ai` rozpoznaným aplikačním identifikátorem GS1. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Nastaví jazyk chybových hlášení ověřování obsahu (výchozí je angličtina). `null` se přeskočí. |
| `String buildElementString()` | Vydá řetězec prvků GS1. Při selhání vyvolá `GaiaBuilderException`. |
| `String buildDigitalLinkUri()` | Vydá kanonický URI Digital Link. Při selhání vyvolá `GaiaBuilderException`. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Vydá URI Digital Link podle `config`. Při selhání vyvolá `GaiaBuilderException`. |
| `BuildResult tryBuildElementString()` | Sestavení řetězce prvků bez výjimek. |
| `BuildResult tryBuildDigitalLinkUri()` | Sestavení kanonického Digital Link bez výjimek. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Sestavení Digital Link podle `config`, bez výjimek. |

### `BuilderDigitalLinkConfig`

| Člen | Popis |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Výchozí `https://id.gs1.org`. |
| `static Builder builder()` | Nový tvůrce nastavení. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Určené schéma, autorita a předpona cesty. |
| `getExtraQueryParams()` | Další parametry dotazu v pořadí vkládání. |
| `getFragment()` | Fragment, nebo `null`. |

### `GaiaBuilderException`

| Člen | Popis |
|--------|-------------|
| `getErrors()` | Objekty `GaiaError`, jež selhání způsobily — chyby analyzátoru při selhání obsahu, nebo jediná strukturní chyba Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nikdy `null`. |

### `BuildResult`

| Člen | Popis |
|--------|-------------|
| `isSuccess()` | Zda se sestavení zdařilo. |
| `getValue()` | Vydaný výstup při úspěchu; `null` při selhání. |
| `getMessage()` | Popis selhání při selhání; `null` při úspěchu. |
| `getErrors()` | Chyby ověření při selhání; prázdný seznam při úspěchu. Nikdy `null`. |
| `getTiming()` | Objekt `ProcessingTiming` pro sestavení (čas zahájení, doba zpracování), nebo `null`. |

---

Viz také: **[GaiaParser — Příručka vývojáře](GaiaParser-Czech.md)** — o straně rozboru, modelu prvku AI, přehledu chyb a přílohách s konstantami AI a výkladu.
