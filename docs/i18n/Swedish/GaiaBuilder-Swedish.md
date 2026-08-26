# GaiaBuilder — Utvecklarhandbok

## Innehåll

1. [Översikt](#översikt)
2. [Om GS1 och General Specifications](#om-gs1-och-general-specifications)
3. [Snabbstart](#snabbstart)
4. [Så fungerar det](#så-fungerar-det)
5. [Att bygga elementsträngar](#att-bygga-elementsträngar)
   - [Attribut-AI behöver sin identifieringsnyckel](#attribut-ai-behöver-sin-identifieringsnyckel)
6. [Att bygga Digital Link-URI:er](#att-bygga-digital-link-urier)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validering och fel](#validering-och-fel)
   - [Byggmetoder som kastar undantag](#byggmetoder-som-kastar-undantag)
   - [tryBuild\*-metoder utan undantag](#trybuild-metoder-utan-undantag)
   - [Felmeddelandenas språk](#felmeddelandenas-språk)
   - [BuildResult](#buildresult)
9. [Kontrollsiffror](#kontrollsiffror)
10. [Trådsäkerhet](#trådsäkerhet)
11. [API-översikt](#api-översikt)

---

## Översikt

`GaiaBuilder` är motstycket till [`GaiaParser`](GaiaParser-Swedish.md): den gör en uppsättning par av applikationsidentifierare (AI) och värde till en välformad GS1-**elementsträng** eller en välformad **GS1 Digital Link-URI**. Du lämnar AI:erna och deras fullständiga datavärden; byggaren fogar samman dem, validerar resultatet med samma maskineri som `GaiaParser` använder och avger utdata.

Eftersom byggaren validerar genom att *tolka sitt eget föreslagna utdata* går allt den returnerar med säkerhet att läsa tillbaka felfritt med `GaiaParser` — de två kan aldrig gå isär om vad som är välformat.

**Ingångsklass:** `tools.pantheum.gaia.GaiaBuilder`

---

## Om GS1 och General Specifications

**GS1** är en världsomspännande ideell organisation som utvecklar och förvaltar öppna standarder för identifiering och datautbyte i leveranskedjor. Standarderna används inom detaljhandel, hälso- och sjukvård, logistik, restaurangnäring och många andra branscher — från streckkoder på konsumentförpackningar till serialiserad spårning av läkemedelsdoser.

Den auktoritativa källan för allt som den här byggaren förverkligar är dokumentet **GS1 General Specifications** — ett enda dokument som fastställer:

- Alla koder för applikationsidentifierare (AI), deras datarubriker, format och valideringsregler
- Syntaxreglerna för att sätta samman och koda AI-elementsträngar
- Kraven på streckkodssymbologier och tilldelningen av AIM-symbologiidentifierare
- Algoritmerna för kontrollsiffra och kontrolltecken
- Uttydningen av tvåsiffriga årtal (regeln om glidande fönster)
- Specifikationerna för Data Matrix, QR Code, GS1-128, GS1 DataBar och övriga databärare

GS1 General Specifications uppdateras varje år. Gällande utgåva och tillhörande material finns på:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA förverkligar **utgåva 26.0 (fastställd i januari 2026)** av GS1 General Specifications.

GS1 Digital Link-URI:er styrs av en kompletterande standard, **GS1 Digital Link: URI Syntax**, som fastställer de primära identifieringsnycklarna, ordningen på nyckelkvalificerare och kodningen av dataattribut som byggaren tillämpar när den avger Digital Link-URI:er:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA förverkligar **utgåva 1.7.0 (fastställd i augusti 2026)** av standarden GS1 Digital Link: URI Syntax.

Hänvisningar till avsnitt i hela detta dokument avser GS1 General Specifications (till exempel ”Table 7-5”, ”section 7.12”), med undantag för Digital Link-avsnittsnummer (till exempel ”§4.9”, ”§4.12”), som hänvisar till standarden GS1 Digital Link: URI Syntax.

---

## Snabbstart

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

Föredra konstanterna i `GS1Constants_AICodes` framför råa AI-strängar (se [bilaga A i tolkhandboken](GaiaParser-Swedish.md#bilaga-a--ai-strängkonstanter)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Så fungerar det

Varje bygge går samma väg:

1. **Sammanfogning** — AI/värde-paren fogas samman till en föreslagen elementsträng. Efter varje AI som *kräver en avgränsare* och som inte är sista elementet infogas en FNC1-gruppavgränsare (`0x1D`). AI med i förväg bestämd längd (GTIN, datum, mått med fast längd) får ingen avgränsare; alla andra får det. (Okända AI når aldrig det här momentet — `ai(...)` avvisar dem omedelbart; se [Att bygga elementsträngar](#att-bygga-elementsträngar).)
2. **Validering** — förslaget tolkas i läget `CONTENT` med `GaiaParser`. Varje värde prövas mot sitt AI:s format och kontrollsiffra, och strukturreglerna (obligatoriska eller uteslutande AI-par) upprätthålls. Är tolkningen inte giltig misslyckas bygget.
3. **Avgivning** —
   - För en elementsträng returneras det validerade objektets `toElementString()`.
   - För en Digital Link tilldelas varje element sin DL-roll (primär nyckel, nyckelkvalificerare eller dataattribut), kvalificerarföljden valideras, URI:n avges, och den avgivna URI:n **tolkas på nytt för att bekräfta att den läses tillbaka som en giltig Digital Link** — en skyddande kontroll av strängsammanfogningen och procentkodningen. Misslyckas den rundturen kastas ett `GaiaBuilderException`.

Detta återger återuppbyggnadslogiken i `DLSyntaxParser`, så avgränsarnas placering och valideringen stämmer helt med vad tolken väntar sig.

---

## Att bygga elementsträngar

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI:t** valideras omedelbart: `ai(...)` kastar `IllegalArgumentException` om det inte är en känd GS1-applikationsidentifierare. (Byggaren fogar samman AI och värde före tolkningen, så ett okänt eller alltför långt AI som `"99999"` måste fångas här — annars skulle det tyst delas om i lexem som ett annat AI.) **Värdet** valideras däremot senare, vid bygget.
- Värdena måste vara **fullständiga**, inbegripet eventuell kontrollsiffra. Byggaren beräknar inte och lägger inte till kontrollsiffror åt dig — se [Kontrollsiffror](#kontrollsiffror).
- AI:erna avges i den ordning du lägger till dem. Byggaren infogar FNC1-avgränsarna där GS1-syntaxen kräver dem; lägg inte till dem själv.
- Ett bygge **helt utan AI** kastar `GaiaBuilderException("No AIs supplied")` med en tom `getErrors()`-lista — det enda misslyckande som inte bär något `GaiaError`.
- Ett AI vars värde bryter mot sin format- eller kontrollsifferregel får bygget att misslyckas.

### Attribut-AI behöver sin identifieringsnyckel

De flesta AI är *attribut* som GS1 General Specifications kräver ska följas av en identifieringsnyckel, och byggaren upprätthåller det: den validerar genom hela syntaxsteget, utan möjlighet att avstå. Ett parti eller ett serienummer för sig utgör **ingen** giltig elementsträng:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identifieringsnycklarna (GTIN `01`, SSCC `00`, GLN `414`, …) och AI:erna för företagsinternt bruk (`90`–`99`) får med full rätt stå ensamma. Allt annat behöver sin följeslagare.

> `GaiaParser` kan ombes hoppa över den här kontrollen med `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` erbjuder avsiktligt ingen motsvarighet — den är till för att avge standardenligt utdata. Vill du foga samman en avsiktligt ofullständig elementsträng, sätt ihop den själv och tolka den med kontrollen avstängd.

---

## Att bygga Digital Link-URI:er

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

En giltig Digital Link kräver exakt en **primär identifieringsnyckel** (till exempel GTIN `01`, GLN `414`, SSCC `00`). Byggaren placerar in varje angivet AI:

| Roll | Avges som | Exempel |
|------|-------------|---------|
| Primär identifieringsnyckel | Sökvägsavsnitt efter domänen eller prefixet | `/01/09506000134352` |
| Nyckelkvalificerare (CPV `22`, parti `10`, serienummer `21`, …) | Följande sökvägsavsnitt, i **kanonisk ordning enligt §4.9** (inte i den ordning du lade till dem) | `/10/LOT-ABC` |
| Dataattribut (allt övrigt) | Frågeparametrar, **sorterade lexikalt på AI-nyckel** (§4.12) | `?17=271231` |

Eftersom kvalificerarna ordnas om vid avgivningen är det ingen olägenhet att lämna dem i godtycklig ordning: `ai("21", …)` före `ai("10", …)` ger ändå `/10/LOT/21/SER`. Endast själva *uppsättningen* måste vara tillåten för den primära nyckeln.

Värdena procentkodas både i sökvägen och i frågan.

Bygget **misslyckas** (kastar `GaiaBuilderException`, eller returnerar ett misslyckat `BuildResult`) när:

- det **inte** finns någon primär identifieringsnyckel bland AI:erna;
- det finns **fler än en** primär identifieringsnyckel;
- ett AI är **förbjudet** i Digital Link (`03`, `8014`);
- **nyckelkvalificerarföljden** är otillåten för den valda primära nyckeln (till exempel en kvalificerare som inte hör till den nyckeln, eller kvalificerare utanför den tillåtna ordningen).

---

## BuilderDigitalLinkConfig

Skicka med ett `BuilderDigitalLinkConfig` för att styra schema, domän, sökvägsprefix, extra frågeparametrar och fragment:

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

| Byggarmetod | Ändamål | Standard |
|----------------|---------|---------|
| `scheme(String)` | URI-schema; måste vara `http` eller `https` | `https` |
| `domain(String)` | Auktoritet — värd eller `värd:port` | `id.gs1.org` |
| `pathPrefix(String)` | Sökvägsavsnitt före den första primära nyckeln; inledande och avslutande snedstreck normaliseras | *(inget)* |
| `baseUrl(String)` | Genväg som delar upp en webbadress i `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Extra frågeparameter, tillagd **efter** AI-dataattributen, i insättningsordning; procentkodad | — |
| `fragment(String)` | Webbadressens fragment (utan inledande `#`); procentkodat | *(inget)* |

`build()` validerar inställningarna omedelbart: ett schema som inte är `http(s)` eller en tom domän kastar `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (aliaset `defaultConfig()`) är standardvärdet `https://id.gs1.org` utan tillägg — precis det som `buildDigitalLinkUri()` utan argument använder och som `GS1AIObject.getCanonicalDigitalLink()` avger.
- `baseUrl("http://id.example.org:8080/r")` → schema `http`, domän `id.example.org:8080`, sökvägsprefix `/r`.
- Extra frågeparametrar följer alltid efter de attribut som härletts ur AI:erna, så den kanoniska AI-ordningen (§4.12) bevaras.

`BuilderDigitalLinkConfig` är oföränderlig; återanvänd samma instans fritt.

---

## Validering och fel

### Byggmetoder som kastar undantag

`buildElementString()`, `buildDigitalLinkUri()` och `buildDigitalLinkUri(BuilderDigitalLinkConfig)` kastar **`GaiaBuilderException`** (ett okontrollerat `RuntimeException`) när AI:erna inte kan bilda ett välformat utdata:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Vid **innehållsfel** (fel kontrollsiffra, formatavvikelse, saknat eller uteslutet AI) bär `getErrors()` tolkens `GaiaError`-objekt — samma objekt som [beskrivs i tolkhandboken](GaiaParser-Swedish.md#gaiaerror).
- Vid **strukturella Digital Link-fel** (ingen primär nyckel, fler än en primär nyckel, förbjudet AI, otillåten nyckelkvalificerarföljd) bär `getErrors()` ett enda `GaiaError` (koden `GE-L008`, `GE-L012`, `GE-L013` eller `GE-L014`) lokaliserat till byggarens språk.

### tryBuild\*-metoder utan undantag

Kommer indata från användaren och är ett misslyckande ett väntat och avhjälpbart utfall, använd varianterna `tryBuild*` i stället för flödesstyrning med undantag. De returnerar ett [`BuildResult`](#buildresult) i stället för att kasta:

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

| Med undantag | Utan undantag |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Varje `tryBuild*`-metod delar samma valideringskärna med sin kastande tvilling; det är bara sättet att meddela misslyckandet som skiljer.

### Felmeddelandenas språk

Felen från innehållsvalideringen hämtas ur den lokaliserade felkatalogen. Anropa `language(...)` för att välja språk på meddelandena i de `GaiaError`-objekt som `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` bär; standard är engelska:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Det är samma inställning `GaiaConstants.Language` som `GaiaParser` tar emot via `ParseConfig`, så byggaren och tolken lokaliseras likadant.

Både `GaiaError`-meddelandena om **innehåll** och de **strukturella Digital Link-felen** (ingen primär nyckel, fler än en primär nyckel, förbjudet AI, otillåten nyckelkvalificerarföljd) lokaliseras via den gemensamma felkatalogen — de senare med koderna `GE-L008`, `GE-L012`, `GE-L013` och `GE-L014`.

### BuildResult

`BuildResult` (i paketet `tools.pantheum.gaia.result`) är en oföränderlig värdetyp som beskriver utfallet av ett `tryBuild*`-anrop:

| Metod | Vid lyckat bygge | Vid misslyckande |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | den avgivna strängen | `null` |
| `getMessage()` | `null` | beskrivning av misslyckandet |
| `getErrors()` | tom lista | valideringsfelen (samma som hos `GaiaBuilderException.getErrors()`) |

---

## Kontrollsiffror

Byggaren validerar kontrollsiffror men **beräknar** dem inte — värdena måste redan innehålla sin. För att beräkna en, använd `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` tillämpar GS1:s gängse modulo-10-algoritm på de angivna siffrorna och returnerar kontrollsiffran `0–9`, eller `-1` om indata är null, tomt eller inte numeriskt.

---

## Trådsäkerhet

`GaiaBuilder` är **inte** trådsäker och är avsedd för engångsbruk: anropa `create()`, lägg till AI:erna, bygg en gång. Skapa en ny byggare för varje utdata; dela inte en mellan trådar.

`BuilderDigitalLinkConfig` (och de `BuildResult` den avger) är oföränderliga och får delas fritt — bygg en konfiguration en gång vid start och återanvänd den i många byggare.

---

## API-översikt

### `GaiaBuilder`

| Metod | Beskrivning |
|--------|-------------|
| `static GaiaBuilder create()` | Påbörjar en ny, tom byggare. |
| `GaiaBuilder ai(String ai, String value)` | Lägger till ett AI och dess fullständiga värde. Kastar `IllegalArgumentException` om något av argumenten är `null`, eller om `ai` inte är en känd GS1-applikationsidentifierare. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Anger språket för innehållsvalideringens felmeddelanden (standard engelska). `null` hoppas över. |
| `String buildElementString()` | Avger en GS1-elementsträng. Kastar `GaiaBuilderException` vid misslyckande. |
| `String buildDigitalLinkUri()` | Avger en kanonisk Digital Link-URI. Kastar `GaiaBuilderException` vid misslyckande. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Avger en Digital Link-URI enligt `config`. Kastar `GaiaBuilderException` vid misslyckande. |
| `BuildResult tryBuildElementString()` | Bygge av elementsträng utan undantag. |
| `BuildResult tryBuildDigitalLinkUri()` | Bygge av kanonisk Digital Link utan undantag. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Bygge av Digital Link enligt `config`, utan undantag. |

### `BuilderDigitalLinkConfig`

| Medlem | Beskrivning |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Standardvärdet `https://id.gs1.org`. |
| `static Builder builder()` | En ny konfigurationsbyggare. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Fastställt schema, fastställd auktoritet och fastställt sökvägsprefix. |
| `getExtraQueryParams()` | Extra frågeparametrar, i insättningsordning. |
| `getFragment()` | Fragment, eller `null`. |

### `GaiaBuilderException`

| Medlem | Beskrivning |
|--------|-------------|
| `getErrors()` | De `GaiaError` som orsakade misslyckandet — tolkens fel vid ett innehållsfel, eller ett enda strukturellt Digital Link-fel (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Aldrig `null`. |

### `BuildResult`

| Medlem | Beskrivning |
|--------|-------------|
| `isSuccess()` | Om bygget lyckades. |
| `getValue()` | Det avgivna utdata vid lyckat bygge; `null` vid misslyckande. |
| `getMessage()` | Beskrivning av misslyckandet vid misslyckande; `null` vid lyckat bygge. |
| `getErrors()` | Valideringsfelen vid misslyckande; tom lista vid lyckat bygge. Aldrig `null`. |
| `getTiming()` | Byggets `ProcessingTiming` (starttid, behandlingstid), eller `null`. |

---

Se även: **[GaiaParser — Utvecklarhandbok](GaiaParser-Swedish.md)** för tolkningssidan, AI-elementmodellen, felöversikten och bilagorna med AI- och tolkningskonstanter.
