# GaiaBuilder — Ontwikkelaarshandleiding

## Inhoudsopgave

1. [Overzicht](#overzicht)
2. [Over GS1 en de General Specifications](#over-gs1-en-de-general-specifications)
3. [Snelstart](#snelstart)
4. [Werking](#werking)
5. [Elementreeksen bouwen](#elementreeksen-bouwen)
   - [Attribuut-AI's hebben hun identificatiesleutel nodig](#attribuut-ais-hebben-hun-identificatiesleutel-nodig)
6. [Digital Link-URI's bouwen](#digital-link-uris-bouwen)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validatie en fouten](#validatie-en-fouten)
   - [Bouwmethoden die uitzonderingen werpen](#bouwmethoden-die-uitzonderingen-werpen)
   - [tryBuild\*-methoden zonder uitzonderingen](#trybuild-methoden-zonder-uitzonderingen)
   - [Taal van de foutmeldingen](#taal-van-de-foutmeldingen)
   - [BuildResult](#buildresult)
9. [Controlecijfers](#controlecijfers)
10. [Threadveiligheid](#threadveiligheid)
11. [API-referentie](#api-referentie)

---

## Overzicht

`GaiaBuilder` is de tegenhanger van [`GaiaParser`](GaiaParser-Dutch.md): hij maakt van een verzameling paren toepassingsidentificatie (AI) / waarde een welgevormde GS1-**elementreeks** of een welgevormde **GS1 Digital Link-URI**. U levert de AI's en hun volledige gegevenswaarden aan; de builder stelt ze samen, valideert het resultaat met dezelfde machinerie die `GaiaParser` gebruikt, en levert de uitvoer.

Doordat de builder valideert door *zijn eigen kandidaat-uitvoer te parseren*, is alles wat hij teruggeeft gegarandeerd foutloos terug te lezen met `GaiaParser` — de twee kunnen het nooit oneens zijn over wat welgevormd is.

**Startklasse:** `tools.pantheum.gaia.GaiaBuilder`

---

## Over GS1 en de General Specifications

**GS1** is een wereldwijde non-profitorganisatie die open standaarden ontwikkelt en onderhoudt voor identificatie en gegevensuitwisseling in toeleveringsketens. Haar standaarden worden gebruikt in de detailhandel, de zorg, de logistiek, de horeca en tal van andere sectoren, en bestrijken alles van productbarcodes op consumentenverpakkingen tot geserialiseerde tracering van farmaceutische doses.

Het gezaghebbende naslagwerk voor alles wat deze builder implementeert zijn de **GS1 General Specifications** — één enkel document dat het volgende vastlegt:

- Alle codes van toepassingsidentificatoren (AI), hun gegevenstitels, formaten en validatieregels
- De syntaxisregels voor het samenstellen en coderen van AI-elementreeksen
- De eisen aan barcodesymbologieën en de toewijzing van AIM-symbologie-identificaties
- De algoritmen voor controlecijfers en controletekens
- De interpretatie van jaartallen met twee cijfers (de schuivendvensterregel)
- De specificaties van Data Matrix, QR Code, GS1-128, GS1 DataBar en overige gegevensdragers

De GS1 General Specifications worden jaarlijks bijgewerkt. De geldende uitgave en de bijbehorende hulpmiddelen zijn beschikbaar op:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementeert **release 26.0 (bekrachtigd in januari 2026)** van de GS1 General Specifications.

GS1 Digital Link-URI's vallen onder een aanvullende standaard, **GS1 Digital Link: URI Syntax**, die de primaire identificatiesleutels, de volgorde van de sleutelkwalificatoren en de codering van de gegevensattributen vastlegt die de builder toepast bij het opstellen van Digital Link-URI's:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementeert **release 1.7.0 (bekrachtigd in augustus 2026)** van de standaard GS1 Digital Link: URI Syntax.

Verwijzingen naar paragrafen in dit document betreffen de GS1 General Specifications (bijvoorbeeld «Table 7-5», «section 7.12»), met uitzondering van de Digital Link-paragraafnummers (bijvoorbeeld «§4.9», «§4.12»), die naar de standaard GS1 Digital Link: URI Syntax verwijzen.

---

## Snelstart

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

Geef de voorkeur aan de constanten uit `GS1Constants_AICodes` boven ruwe AI-tekenreeksen (zie [bijlage A van de parserhandleiding](GaiaParser-Dutch.md#bijlage-a--ai-tekenreeksconstanten)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Werking

Elke bouwgang volgt hetzelfde pad:

1. **Samenstellen** — de AI/waarde-paren worden aaneengeschakeld tot een kandidaat-elementreeks. Na elke AI die *een scheidingsteken vereist* en niet het laatste element is, wordt een FNC1-groepsscheidingsteken (`0x1D`) ingevoegd. AI's met een vooraf bepaalde lengte (GTIN, datums, maten met vaste lengte) krijgen geen scheidingsteken; alle andere wel. (Onbekende AI's bereiken deze stap nooit — `ai(...)` wijst ze meteen af; zie [Elementreeksen bouwen](#elementreeksen-bouwen).)
2. **Valideren** — de kandidaat wordt in de modus `CONTENT` geparseerd door `GaiaParser`. Elke waarde wordt getoetst aan het formaat en het controlecijfer van haar AI, en de structuurregels (vereiste of uitgesloten AI-combinaties) worden afgedwongen. Is het parseren niet geldig, dan mislukt de bouwgang.
3. **Uitvoeren** —
   - Bij een elementreeks wordt de `toElementString()` van het gevalideerde object teruggegeven.
   - Bij een Digital Link krijgt elk element zijn DL-rol toegewezen (primaire sleutel, sleutelkwalificator of gegevensattribuut), wordt de reeks sleutelkwalificatoren gevalideerd, wordt de URI uitgegeven en wordt die uitgegeven URI **opnieuw geparseerd om te bevestigen dat hij als geldige Digital Link terug te lezen is** — een defensieve controle op het samenstellen van de reeks en op de procentcodering. Lukt dat teruglezen niet, dan wordt een `GaiaBuilderException` geworpen.

Dit volgt de reconstructielogica van `DLSyntaxParser`, zodat de plaatsing van de scheidingstekens en de validatie identiek zijn aan wat de parser verwacht.

---

## Elementreeksen bouwen

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- De **AI** wordt meteen gevalideerd: `ai(...)` werpt een `IllegalArgumentException` als het geen bekende GS1-toepassingsidentificatie is. (De builder schakelt AI en waarde aaneen vóór het parseren, zodat een onbekende of te lange AI zoals `"99999"` hier moet worden onderschept — anders zou hij stilzwijgend opnieuw tot een andere AI worden getokeniseerd.) De **waarde** wordt later gevalideerd, tijdens het bouwen.
- Waarden moeten **volledig** zijn, inclusief een eventueel controlecijfer. De builder berekent en voegt geen controlecijfers voor u toe — zie [Controlecijfers](#controlecijfers).
- De AI's worden uitgegeven in de volgorde waarin u ze toevoegt. De builder voegt de FNC1-scheidingstekens in waar de GS1-syntaxis dat vereist; u voegt ze zelf niet toe.
- Bouwen **zonder enige AI** werpt `GaiaBuilderException("No AIs supplied")` met een lege `getErrors()`-lijst — de enige mislukking zonder ook maar één `GaiaError`.
- Een AI waarvan de waarde haar formaat- of controlecijferregel schendt, laat de bouwgang mislukken.

### Attribuut-AI's hebben hun identificatiesleutel nodig

De meeste AI's zijn *attributen* waarvan de GS1 General Specifications eisen dat ze vergezeld gaan van een identificatiesleutel, en de builder dwingt dat af: hij valideert via de volledige syntaxisfase, zonder mogelijkheid om dat uit te schakelen. Een partij of een serienummer op zichzelf is **geen** geldige elementreeks:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identificatiesleutels (GTIN `01`, SSCC `00`, GLN `414`, …) en de bedrijfsinterne AI's (`90`–`99`) mogen volkomen terecht alleen staan. Al het overige heeft zijn metgezel nodig.

> `GaiaParser` kan worden gevraagd deze controle over te slaan met `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` biedt bewust geen equivalent — hij is bedoeld om standaardconforme uitvoer te leveren. Wilt u een bewust onvolledige elementreeks samenstellen, schakel haar dan zelf aaneen en parseer haar met de controle uitgeschakeld.

---

## Digital Link-URI's bouwen

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Een geldige Digital Link vereist precies één **primaire identificatiesleutel** (bijvoorbeeld GTIN `01`, GLN `414`, SSCC `00`). De builder deelt elke aangeleverde AI in:

| Rol | Uitgegeven als | Voorbeeld |
|------|-------------|---------|
| Primaire identificatiesleutel | Padsegment na het domein of het voorvoegsel | `/01/09506000134352` |
| Sleutelkwalificator (CPV `22`, partij `10`, serie `21`, …) | Volgende padsegmenten, in de **canonieke volgorde van §4.9** (niet in de volgorde waarin u ze toevoegde) | `/10/LOT-ABC` |
| Gegevensattribuut (al het overige) | Queryparameters, **lexicaal gesorteerd op AI-sleutel** (§4.12) | `?17=271231` |

Doordat de kwalificatoren bij het uitgeven worden herschikt, is het geen probleem ze in willekeurige volgorde aan te leveren: `ai("21", …)` vóór `ai("10", …)` levert nog steeds `/10/LOT/21/SER` op. Alleen de *verzameling* moet voor de primaire sleutel toelaatbaar zijn.

Waarden worden zowel in het pad als in de query procentgecodeerd.

De bouwgang **mislukt** (werpt `GaiaBuilderException`, of geeft een mislukt `BuildResult` terug) wanneer:

- er onder de AI's **geen** primaire identificatiesleutel is;
- er **meer dan één** primaire identificatiesleutel is;
- een AI in Digital Links **verboden** is (`03`, `8014`);
- de **reeks sleutelkwalificatoren** ongeldig is voor de gekozen primaire sleutel (bijvoorbeeld een kwalificator die niet bij die sleutel hoort, of kwalificatoren buiten hun toegestane volgorde).

---

## BuilderDigitalLinkConfig

Geef een `BuilderDigitalLinkConfig` mee om het schema, het domein, het padvoorvoegsel, extra queryparameters en het fragment te sturen:

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

| Buildermethode | Doel | Standaard |
|----------------|---------|---------|
| `scheme(String)` | URI-schema; moet `http` of `https` zijn | `https` |
| `domain(String)` | Autoriteit — host of `host:poort` | `id.gs1.org` |
| `pathPrefix(String)` | Padsegmenten vóór de eerste primaire sleutel; schuine strepen aan begin en einde worden genormaliseerd | *(geen)* |
| `baseUrl(String)` | Hulpmethode die een URL opsplitst in `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Extra queryparameter, toegevoegd **na** de AI-gegevensattributen, in volgorde van invoegen; procentgecodeerd | — |
| `fragment(String)` | URL-fragment (zonder de `#` ervoor); procentgecodeerd | *(geen)* |

`build()` valideert de configuratie meteen: een schema dat niet `http(s)` is of een leeg domein werpt `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) is de standaard `https://id.gs1.org` zonder extra's — precies wat `buildDigitalLinkUri()` zonder argument gebruikt en wat `GS1AIObject.getCanonicalDigitalLink()` oplevert.
- `baseUrl("http://id.example.org:8080/r")` → schema `http`, domein `id.example.org:8080`, padvoorvoegsel `/r`.
- Extra queryparameters volgen altijd op de uit de AI's afgeleide attributen, zodat de canonieke AI-volgorde (§4.12) behouden blijft.

`BuilderDigitalLinkConfig` is onveranderlijk; hergebruik één instantie naar believen.

---

## Validatie en fouten

### Bouwmethoden die uitzonderingen werpen

`buildElementString()`, `buildDigitalLinkUri()` en `buildDigitalLinkUri(BuilderDigitalLinkConfig)` werpen een **`GaiaBuilderException`** (een ongecontroleerde `RuntimeException`) wanneer de AI's geen welgevormde uitvoer kunnen vormen:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Bij **inhoudsfouten** (verkeerd controlecijfer, formaat dat niet klopt, ontbrekende of uitgesloten AI) draagt `getErrors()` de `GaiaError`s van de parser — dezelfde objecten die [in de parserhandleiding beschreven staan](GaiaParser-Dutch.md#gaiaerror).
- Bij **structurele Digital Link-fouten** (geen primaire sleutel, meer dan één primaire sleutel, verboden AI, ongeldige reeks sleutelkwalificatoren) draagt `getErrors()` één enkele `GaiaError` (code `GE-L008`, `GE-L012`, `GE-L013` of `GE-L014`), gelokaliseerd naar de taal van de builder.

### tryBuild\*-methoden zonder uitzonderingen

Komt de invoer van de gebruiker en is mislukken een verwachte, herstelbare uitkomst, gebruik dan de `tryBuild*`-varianten in plaats van een besturingsstroom op basis van uitzonderingen. Ze geven een [`BuildResult`](#buildresult) terug in plaats van te werpen:

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

| Met uitzondering | Zonder uitzondering |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Elke `tryBuild*`-methode deelt dezelfde validatiekern met haar werpende tweelingzus; alleen de grens waarop het mislukt verschilt.

### Taal van de foutmeldingen

Fouten uit de inhoudsvalidatie komen uit de gelokaliseerde foutencatalogus. Roep `language(...)` aan om de taal te kiezen van de `GaiaError`-meldingen die `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` dragen; standaard is dat Engels:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Het is dezelfde instelling `GaiaConstants.Language` die `GaiaParser` via `ParseConfig` aanvaardt, zodat builder en parser op identieke wijze lokaliseren.

Zowel de **inhoudelijke** `GaiaError`-meldingen als de **structurele Digital Link**-fouten (geen primaire sleutel, meer dan één primaire sleutel, verboden AI, ongeldige reeks sleutelkwalificatoren) worden via de gedeelde foutencatalogus gelokaliseerd — die laatste met de codes `GE-L008`, `GE-L012`, `GE-L013` en `GE-L014`.

### BuildResult

`BuildResult` (in het package `tools.pantheum.gaia.result`) is een onveranderlijk waardetype dat de uitkomst van een `tryBuild*`-aanroep beschrijft:

| Methode | Bij succes | Bij mislukking |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | de opgestelde tekenreeks | `null` |
| `getMessage()` | `null` | beschrijving van de mislukking |
| `getErrors()` | lege lijst | de validatiefouten (dezelfde als bij `GaiaBuilderException.getErrors()`) |

---

## Controlecijfers

De builder valideert controlecijfers maar berekent ze **niet**: de waarden moeten het hunne al bevatten. Om er een te berekenen, gebruikt u `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` past het gebruikelijke GS1-modulo-10-algoritme toe op de aangeleverde cijfers en geeft het controlecijfer `0–9` terug, of `-1` als de invoer null, leeg of niet-numeriek is.

---

## Threadveiligheid

`GaiaBuilder` is **niet** threadveilig en is bedoeld voor eenmalig gebruik: roep `create()` aan, voeg de AI's toe, bouw één keer. Maak per uitvoer een nieuwe builder; deel er geen over threads heen.

`BuilderDigitalLinkConfig` (en de `BuildResult`s die hij oplevert) zijn onveranderlijk en mogen vrij worden gedeeld — bouw één configuratie bij het opstarten en hergebruik die voor vele builders.

---

## API-referentie

### `GaiaBuilder`

| Methode | Beschrijving |
|--------|-------------|
| `static GaiaBuilder create()` | Start een nieuwe, lege builder. |
| `GaiaBuilder ai(String ai, String value)` | Voegt een AI en de bijbehorende volledige waarde toe. Werpt `IllegalArgumentException` als een van beide `null` is, of als `ai` geen bekende GS1-toepassingsidentificatie is. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Stelt de taal in van de foutmeldingen uit de inhoudsvalidatie (standaard Engels). `null` wordt genegeerd. |
| `String buildElementString()` | Levert een GS1-elementreeks. Werpt bij mislukking `GaiaBuilderException`. |
| `String buildDigitalLinkUri()` | Levert een canonieke Digital Link-URI. Werpt bij mislukking `GaiaBuilderException`. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Levert een Digital Link-URI volgens `config`. Werpt bij mislukking `GaiaBuilderException`. |
| `BuildResult tryBuildElementString()` | Bouwt een elementreeks zonder uitzonderingen. |
| `BuildResult tryBuildDigitalLinkUri()` | Bouwt een canonieke Digital Link zonder uitzonderingen. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Bouwt een Digital Link volgens `config`, zonder uitzonderingen. |

### `BuilderDigitalLinkConfig`

| Onderdeel | Beschrijving |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | De standaard `https://id.gs1.org`. |
| `static Builder builder()` | Een nieuwe configuratiebuilder. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Bepaald schema, bepaalde autoriteit en bepaald padvoorvoegsel. |
| `getExtraQueryParams()` | Extra queryparameters, in volgorde van invoegen. |
| `getFragment()` | Fragment, of `null`. |

### `GaiaBuilderException`

| Onderdeel | Beschrijving |
|--------|-------------|
| `getErrors()` | De `GaiaError`s die de mislukking hebben veroorzaakt — de fouten van de parser bij een inhoudsfout, of één enkele structurele Digital Link-fout (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nooit `null`. |

### `BuildResult`

| Onderdeel | Beschrijving |
|--------|-------------|
| `isSuccess()` | Of de bouwgang is geslaagd. |
| `getValue()` | De opgestelde uitvoer bij succes; `null` bij mislukking. |
| `getMessage()` | De beschrijving van de mislukking bij mislukking; `null` bij succes. |
| `getErrors()` | De validatiefouten bij mislukking; leeg bij succes. Nooit `null`. |
| `getTiming()` | De `ProcessingTiming` van de bouwgang (starttijd, verwerkingsduur), of `null`. |

---

Zie ook: **[GaiaParser — Ontwikkelaarshandleiding](GaiaParser-Dutch.md)** voor de parseerzijde, het AI-elementmodel, de foutenreferentie en de bijlagen met de AI- en interpretatieconstanten.
