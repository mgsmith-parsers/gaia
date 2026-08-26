# GAIA (GS1 Application Identifiers Analyser) — Utvecklarhandbok

## Innehåll

1. [Översikt](#översikt)
2. [Om GS1 och General Specifications](#om-gs1-och-general-specifications)
3. [GS1-applikationsidentifierare](#gs1-applikationsidentifierare)
4. [Snabbstart](#snabbstart)
5. [Tolkningens behandlingskedja](#tolkningens-behandlingskedja)
   - [Förberedande steg — indatamodifierare](#förberedande-steg--indatamodifierare)
   - [Steg 0 — korrelations-ID](#steg-0--korrelations-id)
   - [Steg 1 — dirigering av indata](#steg-1--dirigering-av-indata)
   - [Steg 2 — syntax](#steg-2--syntax)
   - [Steg 3 — innehåll](#steg-3--innehåll)
   - [Steg 4 — tolkning](#steg-4--tolkning)
6. [Tolkningsinställningar (`ParseConfig`)](#tolkningsinställningar-parseconfig)
   - [Inställningar](#inställningar)
   - [Lokaliserade meddelanden och etiketter](#lokaliserade-meddelanden-och-etiketter)
   - [Datumformatering](#datumformatering)
7. [Indatamodifierare](#indatamodifierare)
   - [Inbyggda modifierare](#inbyggda-modifierare)
   - [Att skriva en modifierare](#att-skriva-en-modifierare)
   - [Registrera modifierare](#registrera-modifierare)
   - [Att se efter vad en modifierare gjorde](#att-se-efter-vad-en-modifierare-gjorde)
   - [Felhantering hos en modifierare](#felhantering-hos-en-modifierare)
8. [Tolkningslägen](#tolkningslägen)
   - [Läget DATA_CARRIER](#läget-data_carrier)
   - [Läget SYNTAX](#läget-syntax)
   - [Läget CONTENT](#läget-content)
   - [Läget INTERPRETATION (standard)](#läget-interpretation-standard)
9. [Korrelations-ID](#korrelations-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Att arbeta med resultaten](#att-arbeta-med-resultaten)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry och DataCarrierType](#datacarrierentry-och-datacarriertype)
12. [Felöversikt](#felöversikt)
13. [Trådsäkerhet](#trådsäkerhet)
14. [Bilaga A — AI-strängkonstanter](#bilaga-a--ai-strängkonstanter)
    - [Identifiering och serialisering](#identifiering-och-serialisering)
    - [Datum och tider](#datum-och-tider)
    - [Mängd och mått — variabelt mått (metriskt)](#mängd-och-mått--variabelt-mått-metriskt)
    - [Mängd och mått — variabelt mått (brittiskt/amerikanskt)](#mängd-och-mått--variabelt-mått-brittisktamerikanskt)
    - [Priser och penningbelopp](#priser-och-penningbelopp)
    - [Plats och försändelse](#plats-och-försändelse)
    - [Produktegenskaper och spårbarhet](#produktegenskaper-och-spårbarhet)
    - [Nationella ersättningsnummer inom hälso- och sjukvård (NHRN)](#nationella-ersättningsnummer-inom-hälso--och-sjukvård-nhrn)
    - [Hälso- och sjukvård, GMN, HIDRI, CPID, personuppgifter](#hälso--och-sjukvård-gmn-hidri-cpid-personuppgifter)
    - [Internt bruk / företagsbruk](#internt-bruk--företagsbruk)
15. [Bilaga B — konstanter för tolkningsnycklar](#bilaga-b--konstanter-för-tolkningsnycklar)
    - [Datum och tid](#datum-och-tid)
    - [Skördedatum](#skördedatum)
    - [GS1-företagsprefix](#gs1-företagsprefix)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Land (ISO 3166)](#land-iso-3166)
    - [Valuta (ISO 4217)](#valuta-iso-4217)
    - [Temperatur](#temperatur)
    - [Kön (ISO 5218)](#kön-iso-5218)
    - [Vattenlevande arter (FAO)](#vattenlevande-arter-fao)
    - [Natos förrådsbeteckning (NSN)](#natos-förrådsbeteckning-nsn)
    - [Rullprodukter](#rullprodukter)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM-identifierare (EID / ICCID)](#sim-identifierare-eid--iccid)
    - [Certifieringsreferens](#certifieringsreferens)
    - [GS1 UIC](#gs1-uic)
    - [Den nyföddes födelseordning](#den-nyföddes-födelseordning)
    - [Globalt modellnummer (GMN)](#globalt-modellnummer-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Decimal- och måttvärden](#decimal--och-måttvärden)
    - [Geografiska koordinater](#geografiska-koordinater)
    - [Produktionsmetod](#produktionsmetod)
    - [AIDC-medietyp](#aidc-medietyp)
    - [Del av totalt antal](#del-av-totalt-antal)
    - [Uppdelning i komponenter](#uppdelning-i-komponenter)
    - [Övrigt](#övrigt)

---

## Översikt

`GaiaParser` är ingångspunkten för tolkning av elementsträngar med GS1-applikationsidentifierare (AI). Den tar emot rå utdata från en läsare i någon av följande former och returnerar ett strukturerat `ParseResult` som innehåller alla igenkända AI, valideringsfel och — om så önskas — mänskligt läsbara tolkningar:

- Enkel AI-elementsträng: `0109506000134352`
- Elementsträng föregången av en AIM-symbologiidentifierare: `]C10109506000134352`
- GS1 Digital Link-URI: `https://example.com/01/09506000134352`
- Någon av ovanstående former, eventuellt med ett åttasiffrigt korrelations-ID först: `12345678~0109506000134352`

**Ingångsklass:** `tools.pantheum.gaia.GaiaParser`

> **Ny med Gaia?** Börja med **[snabbstarten för GaiaParser](GaiaParser-QuickStart-Swedish.md)** — beroendet, en första tolkning och den handfull fallgropar som alla möter, på ungefär tio minuter. Den här handboken är den fullständiga referensen.

> Den omvända operationen — att *bygga* välformade elementsträngar och Digital Link-URI:er från AI/värde-par — beskrivs i **[GaiaBuilder — Utvecklarhandbok](GaiaBuilder-Swedish.md)**.

---

## Om GS1 och General Specifications

**GS1** är en världsomspännande ideell organisation som utvecklar och förvaltar öppna standarder för identifiering och datautbyte i leveranskedjor. Standarderna används inom detaljhandel, hälso- och sjukvård, logistik, restaurangnäring och många andra branscher — från streckkoder på konsumentförpackningar till serialiserad spårning av läkemedelsdoser.

Den auktoritativa källan för allt som den här tolken förverkligar är dokumentet **GS1 General Specifications** — ett enda dokument som fastställer:

- Alla koder för applikationsidentifierare (AI), deras datarubriker, format och valideringsregler
- Syntaxreglerna för att sätta samman och koda AI-elementsträngar
- Kraven på streckkodssymbologier och tilldelningen av AIM-symbologiidentifierare
- Algoritmerna för kontrollsiffra och kontrolltecken
- Uttydningen av tvåsiffriga årtal (regeln om glidande fönster)
- Specifikationerna för Data Matrix, QR Code, GS1-128, GS1 DataBar och övriga databärare

GS1 General Specifications uppdateras varje år. Gällande utgåva och tillhörande material finns på:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA förverkligar **utgåva 26.0 (fastställd i januari 2026)** av GS1 General Specifications.

GS1 Digital Link-URI:er styrs av en kompletterande standard, **GS1 Digital Link: URI Syntax**, som fastställer de primära identifieringsnycklarna, ordningen på nyckelkvalificerare och kodningen av dataattribut som tolken tillämpar på indata av typen Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA förverkligar **utgåva 1.7.0 (fastställd i augusti 2026)** av standarden GS1 Digital Link: URI Syntax.

Hänvisningar till avsnitt i hela detta dokument avser GS1 General Specifications (till exempel ”Table 7-5”, ”section 7.12”), med undantag för Digital Link-avsnittsnummer (till exempel ”§4.9”, ”§4.12”), som hänvisar till standarden GS1 Digital Link: URI Syntax.

---

## GS1-applikationsidentifierare

En **GS1-applikationsidentifierare (AI)** är ett kort numeriskt prefix — två till fyra siffror — som bestämmer betydelsen och formatet hos de data som följer omedelbart efter. AI:er fastställs i GS1 General Specifications och täcker ett brett fält av data i leveranskedjan: produktidentifierare, datum, kvantiteter, partinummer, serienummer, mått, URL:er och mycket annat.

### Ett AI-elements uppbyggnad

Varje AI-element består av två delar:

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

AI-koden är alltid numerisk. Datavärdet följer omedelbart efter, utan någon avgränsare mellan kod och värde.

### AI med fast och med variabel längd

AI:er delas in i två grupper:

| Typ | Beteende | Exempel |
|---|---|---|
| **Fast längd** | Exakt antal tecken, läses alltid i sin helhet | AI `01` (GTIN) — alltid 14 siffror |
| **Variabel längd** | Från 1 tecken upp till ett maximum; avslutas med en GS-avgränsare eller av indataslutet | AI `10` (parti) — 1 till 20 alfanumeriska tecken |

Om ett AI har fast eller variabel längd följer uteslutande av dess definition i GS1-specifikationen — tolken gissar aldrig.

### Elementsträngar med flera AI

Flera AI kan fogas samman till en enda elementsträng. AI med fast längd kan fogas samman direkt, eftersom tolken alltid vet exakt hur många tecken som ska läsas. AI med variabel längd måste avslutas med **GS-tecknet** (ASCII `0x1D`, i streckkodssymbologier även kallat FNC1) varje gång ett annat AI följer efter, så att tolken vet var ett värde slutar och nästa AI-kod börjar.

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

I Javas stränglitteraler skrivs GS-tecknet med Unicode-eskapsekvensen `""`.

### Vanliga AI

| AI | Datarubrik | Format | Exempelvärde |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (ÅÅMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (ÅÅMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, enhetligt valutaområde) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Fjärde siffran** i ett fyrsiffrigt AI för mått eller pris kodar antalet underförstådda decimaler: `3103` är nettovikt i kg med 3 decimaler (`001500` = 1,500 kg), medan `3102` skulle läsa samma siffror som 15,00 kg. Kolumnen `Format` ovan visar formatet på *data*; varje AI:s fullständiga `getFormatString()` omfattar även AI:t självt (till exempel `N4+N6` för `3103`).

### Mänskligt läsbar tolkning (HRI)

Den gängse läsbara formen sätter varje AI-kod inom parentes omedelbart före dess värde, med ett mellanslag mellan elementen:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS-avgränsaren visas inte i HRI. Det här formatet skapas av `GS1AIObject.toHriString()`.

### Fyrsiffriga AI-koder

Vissa AI har fyra siffror i stället för två. De två första anger AI-familjen; den tredje och/eller fjärde bär ytterligare innebörd (till exempel läget för det underförstådda decimaltecknet i mått-AI). Tolken härleder själv den fullständiga AI-koden ur elementsträngen — anropande kod arbetar alltid med den fullständiga koden (till exempel `"3102"`, inte bara `"31"`).

---

## Snabbstart

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

> **GS-avgränsare:** i en sträng med flera AI måste AI med variabel längd avgränsas med GS-tecknet (ASCII `0x1D`). Använd `""` i Javas stränglitteraler.

---

## Tolkningens behandlingskedja

### Förberedande steg — indatamodifierare

Innehåller `ParseConfig` några **indatamodifierare** körs de före allt annat: före borttagningen av korrelations-ID:t, före identifieringen av databäraren och före inträdet i GS1-kedjan. Varje modifierare skriver om rådata åt nästa, och alla steg nedan arbetar med kedjans utdata.

Som standard är ingen modifierare konfigurerad, så det här förberedande steget gör ingenting förrän du uttryckligen aktiverar det. Se [Indatamodifierare](#indatamodifierare).

---

### Steg 0 — korrelations-ID

Före all GS1-behandling kontrollerar `GaiaParser` om indata inleds med ett valfritt **korrelations-ID-prefix**: exakt 8 decimala ASCII-siffror följda av ett tilde (`~`), till exempel `12345678~`.

Finns prefixet tas det bort och bevaras som `CorrelationInfo` i det returnerade `ParseResult`. Alla följande steg arbetar med den så rensade nyttolasten. Saknas prefixet passerar indata oförändrat.

Se [Korrelations-ID](#korrelations-id) för närmare uppgifter.

---

### Steg 1 — dirigering av indata

Efter borttagningen av korrelationen kontrollerar `GaiaParser` om det (rensade) indata inleds med en **AIM-symbologiidentifierare**: ett prefix på tre tecken av formen `]` + ASCII-bokstav + ASCII-siffra (till exempel `]C1` för GS1-128, `]d2` för GS1 DataMatrix, `]e0` för GS1 DataBar / GS1 Composite).

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

Stöder databäraren inte GS1-AI (till exempel en postal streckkod) avbryts tolkningen omedelbart med felet `GE-D002`.

---

### Steg 2 — syntax

Körs alltid. Består av två deloperationer:

**2a. Uppdelning i lexem (`AISyntaxParser`)**
- Läser AI-kodens längd ur de två första tecknen med hjälp av GS1:s prefixtabell (GS1 General Specifications, tabell 7-5).
- AI med fast längd läser ett exakt antal byte ur indata.
- AI med variabel längd läses fram till ett GS-tecken eller till indataslutet.
- Hos AI med flera komponenter delas värdeblocket i avsnitt, ett per komponent.

**2b. Strukturvalidering (`SyntaxValidator`)**
- Upptäcker upprepade AI (`GE-S004`).
- Kontrollerar obligatoriska beroenden mellan AI, till exempel AI `02` som kräver AI `37` (`GE-S005`).
- Kontrollerar uteslutande AI-par (`GE-S006`).

Fel i det här steget har nivån `SYNTAX_ERROR` (uppdelning i lexem) eller `INTEGRITY_ERROR` (struktur). Finns **ett enda** fel — från uppdelningen eller från strukturen — stannar kedjan, och stegen för innehåll och tolkning hoppas över.

---

### Steg 3 — innehåll

Körs endast om steg 2 inte gav några fel (varken från uppdelningen i lexem eller från strukturen). Kedjan tillämpas på varje element (varje moment körs bara om det föregående inte gav fel):

| Moment | Validerare | Felkoder |
|---|---|---|
| Kontroll med reguljärt uttryck | `RegexValidator` | `GE-C001` |
| Teckenuppsättning och format för komponenter | `ComponentValidator` | `GE-C005` + formatkoder per villkor (`GE-C054`–`GE-C115`) |
| Kontrollsiffra / kontrolltecken | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Egen betydelsevalidering | `ContentValidatorRegistry` | innehållskoder per villkor (`GE-C116`–`GE-C170`) |

Fel i det här steget har nivån `FORMAT_ERROR` eller `DATA_ERROR`, med ett undantag:
kontrollerna av GS1-företagsprefixet hos AI som bär en GS1-nyckel är enbart upplysande och har nivån `WARNING` (se
[Felöversikt](#felöversikt)), så ett okänt företagsprefix gör inte i sig
resultatet ogiltigt.

---

### Steg 4 — tolkning

Körs endast i läget `INTERPRETATION` och bara när inget element bär ett fel från ett tidigare steg. `InterpretationEngine` berikar varje element med märkta metadata:

- Datum omformaterade som `dd/mm/åååå`
- Uppdelning av GTIN:s kontrollsiffra och uppslagning av GS1-företagsprefixet
- Landsnamn enligt ISO 3166
- Valutanamn och valutasymboler enligt ISO 4217
- Avkodade decimalbelopp
- HRI-delar (mänskligt läsbar tolkning)

Resultaten fogas som `GS1AIInterpretation`-poster till varje `GS1AIObjectElement`.

---

## Tolkningsinställningar (`ParseConfig`)

`GaiaParser` erbjuder exakt två ingångspunkter:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` körs med **standardinställningarna**: läget `INTERPRETATION`, datum i stigande ordning (`dd/mm/åååå`) med `/` som avgränsare och fyrsiffrigt årtal, samt felmeddelanden på **engelska**. Vill du ändra något av detta — inbegripet tolkningsläget — bygg ett `ParseConfig` med dess flytande byggare och använd varianten med två argument.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Alla uppräkningar av inställningar ligger i `GaiaConstants`.

### Inställningar

| Byggarmetod | Uppräkning (`GaiaConstants`) | Standard | Verkan |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Kedjans djup — se [Tolkningslägen](#tolkningslägen). |
| `language(...)`      | `Language`      | `ENGLISH`        | Språk för felmeddelanden, tolkningsetiketter **och** AI-beskrivningar. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Ordningen på datumets delar: `LITTLE` (`dd/mm/åååå`), `MIDDLE` (`mm/dd/åååå`), `BIG` (`åååå/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Tecken mellan datumets delar: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) eller `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) eller `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Hoppar över strukturkontrollen ”kräver” (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Hoppar över strukturkontrollen ”utesluter” (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / klassnamn | ingen | Kod som skriver om rådata före tolkningen — två [inbyggda modifierare](#inbyggda-modifierare) plus allt du skriver själv. Se [Indatamodifierare](#indatamodifierare). |

De fyra datuminställningarna påverkar endast de formaterade datumsträngar som tolkningsberikarna skapar (i läget `INTERPRETATION`); valideringen ändras inte. Byggarvärden får utelämnas — varje inställning som inte anges (eller som ges `null`) behåller sitt standardvärde.

### Lokaliserade meddelanden och etiketter

`language(...)` väljer språk för **tre** slags mänskligt läsbar text: felmeddelanden, tolkningsetiketter (`getLabel()` hos varje `GS1AIInterpretation`) och AI-beskrivningar (`getDescription()` hos varje `GS1AIObjectElement`).

`GaiaConstants.Language` fastställer **35 språk** som täcker världens mest talade språk: engelska, franska, spanska, tyska, italienska, portugisiska, nederländska, polska, ryska, ukrainska, tjeckiska, svenska, kinesiska, japanska, koreanska, arabiska, indonesiska, hindi, turkiska, bengali, urdu, vietnamesiska, nigeriansk pidgin, egyptisk arabiska, marathi, telugu, tamil, kantonesiska, wu, tagalog, persiska, hausa, punjabi, javanesiska och swahili.

Översättningarnas läge (som de levereras):
- **Tolkningsetiketter** — översatta till samtliga språk.
- **Felmeddelanden** — översatta till samtliga språk.
- **AI-beskrivningar** — översatta till samtliga språk utom engelska. Engelskan utgör ingen egen katalog: den läses direkt ur fältet `description` i AI:ts post i `gs1-application-identifiers.jsonld`, som varje AI-beskrivning ytterst faller tillbaka på.

Nigeriansk pidgin (`NIGERIAN_PIDGIN`), ett kreolspråk med engelsk grund, återanvänder avsiktligt den engelska texten för tolkningsetiketter och felmeddelanden. AI-beskrivningarna är undantaget från det undantaget: de är översatta till äkta pidgin i stället för att överta engelskan, eftersom katalogerna med AI-beskrivningar tagits fram oberoende av katalogerna med etiketter och meddelanden. Maskinöversättningar bör granskas av modersmålstalare innan man förlitar sig på dem i drift.

Varje meddelande, etikett eller beskrivning som saknas i ett språks katalog ersätts med den engelska. Språk som skrivs från höger till vänster (arabiska, urdu, egyptisk arabiska, persiska) lagras korrekt som strängar; att återge dem från höger till vänster är presentationsskiktets uppgift.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Tolkningsetiketterna lokaliseras på samma sätt (värdena är oförändrade — bara etiketterna ändras):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI-beskrivningarna lokaliseras på samma sätt (endast `getTitle()`, till exempel `"GTIN"`, lokaliseras inte):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Datumformatering

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Indatamodifierare

En **indatamodifierare** är kod som skriver om den råa indatasträngen innan Gaia tolkar den. Modifierare finns till för indata som kommer in redan förvanskade: en läsare som byter ut GS-avgränsaren mot en utskrivbar platshållare, en mellanprogramvara som lindar in nyttolasten i ett eget prefix, ett värdsystem som gör om allt till versaler. I stället för att förbehandla varje sträng på varje anropsställe (och göra det omärkligt fel på ett av dem) anger du normaliseringen en enda gång i `ParseConfig` och låter tolken tillämpa den.

Modifierarna körs allra först i `GaiaParser.parse(...)` — före borttagningen av korrelations-ID:t, före identifieringen av AIM-symbologiidentifieraren och före GS1-kedjan. Allt som följer ser bara den omskrivna strängen. **Som standard är ingenting konfigurerat**, inte heller de två [inbyggda modifierarna](#inbyggda-modifierare) — du aktiverar dem uttryckligen i varje `ParseConfig`.

**Gränssnitt:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Inbyggda modifierare

Huvud-jar-filen innehåller två modifierare i paketet `tools.pantheum.gaia.modifier.custom`. De täcker de två vanligaste sätten på vilka en GS1-nyttolast kommer in förvanskad — utskrivna HRI-parenteser som tas för data, och överflödiga blanksteg — så att de vanliga fallen inte kräver någon egen klass:

| Klass | `getName()` | Vad den gör |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Tar bort HRI-parenteserna kring varje AI (`(01)…(10)…`) och återställer den FNC1-avgränsare de stod för. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Tar bort alla blanksteg (`0x20`) ur AI-elementsträngen. |

De är vanliga implementationer av `ModifierInterface` utan någon särställning — de registreras, ordnas, rapporteras och misslyckas precis som dina egna:

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

Båda saknar tillstånd och är trådsäkra, så en enda instans kan delas, och båda kan anges med sitt fullständiga klassnamn vid konfigurationsstyrd uppsättning (se [Registrera modifierare](#registrera-modifierare)).

#### `ModifierRemoveAIBrackets`

GS1:s mänskligt läsbara tolkning skriver ut varje AI inom parentes — `(01)09521234543213(10)ABC123` — enbart som tryckkonvention. En läsare eller mellanprogramvara som ställts in på att sända HRI skickar vidare de parenteserna som data, och uppdelningen i lexem vet inte alls vad den ska göra med dem.

Att ta bort parenteserna är bara halva arbetet. I HRI är det den inledande parentesen hos *nästa* AI som markerar slutet på föregående värde, så i parentesform behöver ett AI med variabel längd inget FNC1. Ta bort parenteserna utan eftertanke och den gränsen försvinner:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Därför **sätter modifieraren tillbaka ett FNC1 vid varje gräns vars föregående AI har variabel längd**, och återställer därmed exakt det parenteserna kodade:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Längden slås upp i tolkens eget register `AiDefinitionRegistry`, så alla AI med variabel längd hanteras i stället för en handskriven lista. Tre fall lämnas avsiktligt orörda: ett värde som redan slutar på FNC1 (en källa som sänder båda konventionerna får ingen andra avgränsare), en kod inom parentes som inte är ett känt AI (ett okänt AI säger ingenting om sin egen längd), och det sista AI:t i strängen.

Omskrivningen är **idempotent** — att tillämpa den på sitt eget resultat ändrar ingenting — och är därför trygg i ett blandat flöde där bara en del av indata har parenteser.

> **Begränsning.** `(` och `)` är i sig giltiga GS1-datatecken, och mönstret stannar vid `\((\d{2,4})\)`. Ett värde som råkar innehålla ett två- till fyrsiffrigt tal inom parentes skulle också bli av med dem. Tillämpa detta enbart på en källa som använder HRI:s parenteskonvention, inte på värden med verkliga parenteser.

#### `ModifierRemoveSpaces`

Vissa läsare, mellanprogramvaror och etikettryckskedjor lägger in överflödiga blanksteg i en i övrigt välformad elementsträng: för att fylla ut ett fält med fast bredd, skilja läsbara grupper åt eller bryta ett långt värde. Uppdelningen i lexem tar vart och ett av dem för data, vilket fördärvar värdet de står i och, hos ett AI med variabel längd, förskjuter allt som följer.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Endast ASCII `0x20` tas bort. Övriga blanktecken lämnas kvar — en tabb ligger till exempel utanför GS1:s kodbara teckenuppsättning, så tolken rapporterar den som `GE-S008` i stället för att tyst sopa bort den.

> **Begränsning.** Blanksteget (`0x20`) hör till GS1:s oföränderliga teckenuppsättning, så ett partinummer eller ett kundartikelnummer kan med full rätt innehålla ett. Modifieraren kan inte skilja ett överflödigt blanksteg från ett äkta; tillämpa den enbart på en källa som du vet inte använder blanksteg inuti sina AI-värden.

#### Prefix hoppas över, de skrivs inte om

Modifierarna körs innan tolken har tagit bort något, så rådata kan ännu bära ett korrelations-ID, en AIM-symbologiidentifierare och en ECI-indikator. Båda de inbyggda modifierarna hittar början på AI-elementsträngen med tolkens egen logik ur `CorrelationIdParser` och `DataCarrierParser`, skriver om först därifrån och fogar resultatet tillbaka till det **orörda** prefixet:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

EAN/UPC-bärare vars värde fylls ut till GTIN-14 (`isRequiresGtinPadding()`) hoppas över helt: deras nyttolast är ett rent numeriskt streckkodsvärde utan AI-struktur, där varken parenteser eller blanksteg kan betyda något.

#### Ordning: blanksteg före parenteser

När båda används, **registrera `ModifierRemoveSpaces` först**. Igenkänningen av parenteser beror på läget: ett `( 01 )` med blanksteg matchar inte `\((\d{2,4})\)`, så parenteserna blir kvar och den avgränsare de stod för återställs aldrig.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Att skriva en modifierare

Skriv en egen när ingen av de inbyggda passar — gränssnittet består av en enda metod.

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

Åsidosätt i stället varianten med två argument när omskrivningen beror på tolkningsinställningarna:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Kontrakt:

| Regel | Närmare |
|---|---|
| Utan tillstånd och trådsäker | Per klass mellanlagras en enda instans som delas av alla tolkningar. |
| Publik konstruktor utan argument | Krävs endast när modifieraren anges med klassnamn. |
| Hantera indata `null` och tomt indata | Tolken filtrerar inte bort dem innan kedjan körs. |
| Att returnera `null` betyder ”ingen ändring” | Det föregående värdet behålls. Returnera `input` oförändrat när modifieraren inte är tillämplig. |
| Hellre returnera indata oförändrat än kasta ett undantag | En modifierare som kastar ett undantag avbryter tolkningen — se [Felhantering](#felhantering-hos-en-modifierare). |
| `getName()` | Åsidosätt den för att styra namnet som rapporteras i `ModifierInfo`; standard är klassens enkla namn. |

### Registrera modifierare

Modifierarna körs i den ordning de läggs till, och var och en får den föregåendes utdata. Registrera dem som instanser, med fullständigt klassnamn eller som en lista av endera:

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

De [inbyggda modifierarna](#inbyggda-modifierare) anges på samma sätt som dina egna — **alltid med fullständigt namn**. Det finns för dem varken uppslagning på kortnamn eller alias; `ModifierRegistry` löser upp varje modifierare, levererad eller inte, med det fullständiga klassnamnet.

Namnen löser `ModifierRegistry` upp: den skapar varje klass en gång via dess konstruktor utan argument och mellanlagrar instansen åt varje senare konfiguration som nämner samma klass. Uppslagningen sker **när konfigurationen byggs**, så ett namn som inte går att hitta, som inte implementerar `ModifierInterface` eller som inte går att instansiera kastar `IllegalArgumentException` just där — inte tyst först vid tolkningen. En modifierare som inte kan byggas med reflektion (till exempel en som bär ett inmatat beroende) kan registreras i förväg så att den ändå går att nå med sitt namn:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Att se efter vad en modifierare gjorde

När modifierare är konfigurerade återger `ParseResult.getPayload()` det **ändrade** indata. Originalet bevaras i `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` rapporterar varje modifierares `getName()`, som normalt är klassens enkla namn men som båda de inbyggda modifierarna åsidosätter — en kedja av de två rapporterar alltså visningsnamnen, inte klassnamnen:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` returnerar `null` när ingen modifierare konfigurerats. Har modifierare körts men var och en returnerat indata oförändrat finns uppgifterna där och `isModified()` är `false` — i `getAppliedModifiers()` räknas endast de modifierare upp som verkligen ändrade indata.

### Felhantering hos en modifierare

En modifierare som kastar ett undantag avbryter tolkningen. Undantaget lindas in i ett `GaiaModifierException` som namnger den skyldiga modifieraren, och resultatet bär ett internt fel `GE-I001` vars meddelande upprepar det namnet; `getPayload()` rapporterar det oförändrade indata. Tolkningen fortsätter avsiktligt **inte** med en halvt omskriven sträng: ett normaliseringssteg som misslyckats tyst skulle ge resultat som ser giltiga ut men härrör ur fel indata.

---

## Tolkningslägen

Varje läge anger det djupaste [steget i kedjan](#tolkningens-behandlingskedja) som det utför; alla föregående steg körs också.

| Läge | Går till | Svarar på frågan |
|---|---|---|
| `DATA_CARRIER` | Steg 1 (dirigering av indata) | Vilken symbologi bar detta? |
| `SYNTAX` | Steg 2 (syntax) | Är AI-koderna och längderna välformade? |
| `CONTENT` | Steg 3 (innehåll) | Är värdena giltiga GS1-data? |
| `INTERPRETATION` | Steg 4 (tolkning) | Vad betyder värdena? |

### Läget DATA_CARRIER

Stannar efter steg 1 — kontrollerar AIM-symbologiidentifieraren och bestämmer symbologin, men går inte in i AI-tolkningskedjan. Användbart för att känna igen symbologin och dirigera vidare utan kostnaden för en fullständig validering.

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

**Använd när:** din tillämpning behöver bestämma streckkodstypen innan den avgör hur nyttolasten ska behandlas — till exempel för att dirigera till olika hanterare för 1D- respektive 2D-symbologier. Föredra för sådan dirigering den typade [`DataCarrierType`](#datacarrierentry-och-datacarriertype) (`getDataCarrier().getDataCarrierType()`) framför strängjämförelse mot `getName()`.

---

### Läget SYNTAX

Stannar efter steg 2. Användbart för en strukturell förhandsgallring utan kostnaden för innehållsvalidering.

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

**Använd när:** du vill kontrollera att AI-koderna och datalängderna är välformade innan du ger dig i kast med en fullständig validering, eller när du behandlar stora volymer där innehållsfel är sällsynta.

---

### Läget CONTENT

Stannar efter steg 3.

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

> De flesta AI får inte stå ensamma: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) och `21`
> (SERIAL) *kräver* var och en en identifieringsnyckel såsom AI `01` i samma
> elementsträng; att utelämna GTIN ovan skulle misslyckas redan i steg 2 med `GE-S005`, utan att
> alls nå fram till innehållsvalideringen. Sätt `skipRequiresCheck(true)` i
> `ParseConfig` för att tolka fragment som avsiktligt utelämnar sina följeslagande AI.

**Använd när:** du behöver veta om ett avläst värde är helt GS1-förenligt innan du använder det i en verksamhetsprocess — utan omkostnaden för tolkningsberikning.

---

### Läget INTERPRETATION (standard)

Utför hela kedjan fram till och med steg 4. Det är standardläget vid anrop av `parse(String)` utan lägesargument. Endast element som felfritt passerat innehållsvalideringen berikas.

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

**Exempelutdata:**
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

**Exempel med penningbelopp (AI 3932 — pris med ISO-valutakod):**
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

**Använd när:** du bygger presentationsskikt, verktyg för etikettkontroll eller vilket gränssnitt som helst som behöver en läsbar uppdelning av AI-värdena.

---

## Korrelations-ID

Vissa arbetsflöden sätter ett eget åttasiffrigt korrelations-ID före de råa GS1-indata, så att avläsningshändelser kan knytas till en session eller en transaktion. Formatet är:

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

Tildet (`~`) är avgränsaren. Det hör **inte** till GS1-innehållet — det tas bort innan någon GS1-tolkning börjar.

### Regler för igenkänning

Prefixet känns igen när indata inleds med exakt 8 decimala ASCII-siffror (`0`–`9`) omedelbart följda av `~`. Är det nionde tecknet inte `~`, eller är något av de 8 första tecknen inte en siffra, behandlas indata som vanligt GS1-innehåll utan korrelationsprefix.

### Att komma åt korrelations-ID:t

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

### Kombination med en AIM-symbologiidentifierare

Ett korrelationsprefix får stå före en AIM-symbologiidentifierare. Tolken hanterar det fallet genomskinligt:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Implementationsklass:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

En **GS1 Digital Link** kodar ett eller flera AI-värden direkt i uppbyggnaden av en HTTP(S)-webbadress och möjliggör därmed identifierare för fysiska produkter som kan slås upp på webben. GAIA förverkligar standarden *GS1 Digital Link Standard: URI Syntax* (utgåva 1.7.0) för **okomprimerade** URI:er.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` känner igen Digital Link-URI:er av sig själv: varje indata som inleds med `http://` eller `https://` dirigeras till `GS1DLParser`, som utför samma steg för innehåll och tolkning som elementsträngskedjan.

### URI:ns uppbyggnad och AI:ernas roller

Varje AI i en Digital Link-URI har en av tre roller, tillgänglig på varje `GS1AIObjectElement` via `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Roll | Plats | Exempel |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Första paret `/ai/värde` i sökvägen (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Följande par i sökvägen, ordnade efter den primära nyckeln (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Frågeparametrar med helt numeriska nycklar (§4.10) | `?17=271231` |

Strukturregler som upprätthålls (`DLPathRules`):
- Exakt **en** primär identifieringsnyckel i sökvägen; ytterligare nycklar måste kodas som dataattribut i frågan.
- Nyckelkvalificerare måste tillåtas av den primära nyckeln och stå i föreskriven ordning. Valfria kvalificerare får utelämnas, men de som *finns med* måste hålla den fastställda ordningen — se [Kvalificerarnas ordning](#kvalificerarnas-ordning).
- Före den primära nyckeln får godtyckliga egna sökvägsavsnitt stå (till exempel `/products/au/01/...`); hämta dem med `getDigitalLinkInfo().getCustomPathStem()`.
- Icke-numeriska frågenycklar (`linkType`, `context`, utökningsparametrar som `23P`) hoppas över; helt numeriska nycklar måste vara giltiga AI märkta med `validAsDataAttribute`.
- Procentkodade värdetecken avkodas; AI `(03)` och `(8014)` är inte tillåtna.

De primära nycklarna och deras tillåtna kvalificerarföljder är **datastyrda** ur AI-definitionerna — genom flaggan `gs1DigitalLinkPrimaryKey` och attributet `gs1DigitalLinkQualifiers` — i stället för fast inskrivna.

Varje strukturbrott, liksom indata som inte är en webbadress, ger ett strukturellt Digital Link-fel (`GE-L001`–`GE-L014`, en kod per villkor). De uppdelade metadata om webbadressen (`scheme`, `domain`, `path`, `customPathStem`, `query` och objektet `java.net.URL`) är fortsatt tillgängliga via `getDigitalLinkInfo()` även vid strukturfel.

### Kvalificerarnas ordning

För varje primär nyckel räknar `gs1DigitalLinkQualifiers` upp en eller flera **ordnade** kvalificerarföljder. Inom en följd är ett AI inom hakparentes **valfritt** och ett AI utan parentes **obligatoriskt**, efter mönster av skrivsättet `[cpv-comp]` i ABNF i §4.9. Följderna för en primär nyckel är ömsesidigt uteslutande alternativ.

GTIN (`01`) fastställer till exempel två följder:

| Sökväg | Följd | Betydelse |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — var för sig valfria, men i den här fasta ordningen |
| upui-path | `235` | TPX (obligatorisk); GTIN + TPX = UPUI |

Alltså är `/01/09506000134352/10/LOT-ABC/21/SER` giltig (LOT före SER, CPV utelämnad), `/01/.../21/SER/10/LOT-ABC` **avvisas** (fel ordning) och `/01/09506000134352/235/2ABC456` hör till upui-path. Ordningskontrollen är en delföljdsmatchning som bevarar ordningen: valfria AI får alltså hoppas över, men aldrig kastas om.

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

**Implementationsklass:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Att arbeta med resultaten

### ParseResult

Det översta resultatet som `GaiaParser.parse()` returnerar.

| Metod | Returnerar | Beskrivning |
|---|---|---|
| `isValid()` | `boolean` | `true` om det inte finns fel på någon nivå. Varningar påverkar inte giltigheten. Alltid `true` när `getAiObject()` är `null`. |
| `getPayload()` | `String` | Indatasträngen efter att korrelationsprefixet tagits bort — och efter att eventuella [indatamodifierare](#indatamodifierare) skrivit om den. |
| `getPayloadContent()` | `String` | Nyttolasten utan AIM-symbologiidentifierare och utan ECI-prefix. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (en databärare som avvisats som icke-GS1, till exempel en Code 39-bärare `]A0`) eller `UNABLE_TO_DETERMINE_CONTENT` (när `aiObject` är `null`, till exempel i läget `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Det inställda kedjedjupet (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Det djupaste steg som tolkningen verkligen nådde — se nedan. |
| `isParseComplete()` | `boolean` | `true` om tolkningen nådde det begärda djupet (`achieved == requested`). Oberoende av `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Alla igenkända AI. `null` i läget `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Alla fel av annan nivå än WARNING (på objektnivå och på samtliga elements nivå). |
| `getWarnings()` | `List<GaiaError>` | Alla upplysningar av nivån WARNING (på objektnivå och på samtliga elements nivå). |
| `hasWarnings()` | `boolean` | `true` om upplysningar av nivån WARNING lämnats. |
| `getIssues()` | `List<GaiaError>` | Fel och varningar tillsammans. |
| `hasDataCarrier()` | `boolean` | `true` om en AIM-symbologiidentifierare känts igen. |
| `getDataCarrier()` | `DataCarrierEntry` | Symbologimetadata, eller `null` om ingen bärare bestämts. |
| `hasEci()` | `boolean` | `true` om en ECI-indikator tagits bort ur nyttolasten. |
| `getEci()` | `EciEntry` | Metadata om ECI-kodningen, eller `null`. |
| `hasCorrelationId()` | `boolean` | `true` om det ursprungliga indata bar ett korrelationsprefix `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | Det utvunna korrelations-ID:t, eller `null` om inget fanns. |
| `isInputModified()` | `boolean` | `true` om en [indatamodifierare](#indatamodifierare) ändrat indata. |
| `getModifierInfo()` | `ModifierInfo` | Vad modifierarkedjan gjorde — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` om ingen modifierare konfigurerats. |
| `getTiming()` | `ProcessingTiming` | Faktisk tidmätning av tolkningen — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` om resultatet inte skapats av `GaiaParser`. |
| `getVersion()` | `String` | Den biblioteksversion som skapade resultatet. |

#### Begärt kontra uppnått tolkningsläge

Kedjan går uppför stegen **SYNTAX → CONTENT → INTERPRETATION** och stannar i förtid vid fel, så det verkligen *uppnådda* läget kan bli grundare än det *begärda*. `getAchievedParseMode()` visar hur långt den kom:

| Begärt | Vad som händer | Uppnått | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | ett **syntax- eller strukturfel** stoppar tolkningen efter uppdelningen i lexem | `SYNTAX` | `false` |
| `INTERPRETATION` | ett **innehållsfel** (fel format eller fel kontrollsiffra) hindrar berikningen | `CONTENT` | `false` |
| `CONTENT` | innehållssteget går alltid i mål (fel antecknas, de är inte ödesdigra) | `CONTENT` | `true` |
| vilket som helst (felfritt indata) | kedjan når det begärda djupet | = begärt | `true` |
| `DATA_CARRIER` | bäraren kontrollerad; inget AI-innehåll tolkat | `DATA_CARRIER` | `true` |
| vilket som helst | databäraren avvisas före AI-tolkningen (till exempel en icke-GS1-bärare `]A0`) | `SYNTAX` | `false` |

`isParseComplete()` är oberoende av `isValid()`: en `CONTENT`-tolkning av ett GTIN med fel kontrollsiffra är **fullständig** (innehållssteget kördes) och samtidigt **ogiltig** (kontrollsiffran stämde inte). Fråga med `isParseComplete()` ”gick kedjan så djupt som jag bad om?” och med `isValid()` ”är data välformade?”.

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

Samlingen av igenkända AI-element.

| Metod | Beskrivning |
|---|---|
| `getAis()` | Alla instanser av `GS1AIObjectElement` i indataordning. |
| `get(String aiCode)` | Första elementet som motsvarar den angivna AI-koden, eller `null`. |
| `contains(String aiCode)` | `true` om ett AI med den koden finns. |
| `size()` | Antalet igenkända AI. |
| `isValid()` | `true` om det inte finns fel på objektnivå och inget element bär fel. |
| `toHriString()` | HRI-sträng, till exempel `(01)09506000134352 (17)261231`. |
| `toElementString()` | Rå elementsträng — utan parenteser, med ett FNC1 efter varje element med variabel längd — till exempel `010950600013435210LOT-ABC<GS>17271231`. Returnerar `null` om `isValid()` är `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` när `hasDigitalLink()` är sant, annars `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` om indata var en GS1 Digital Link-URI med en primär identifieringsnyckel. En välformad webbadress utan primär nyckel erbjuder ändå `getDigitalLinkInfo()`, men returnerar `false` här. |
| `getCanonicalDigitalLink()` | Den kanoniska GS1 Digital Link-URI:n (§4.12) på `https://id.gs1.org` — primär nyckel och kvalificerare som sökvägsavsnitt, dataattribut som frågeparametrar sorterade på AI-nyckel — eller `null` om ingen primär nyckel finns. |
| `getDigitalLinkInfo()` | Metadata om URI-uppdelningen (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), eller `null` om det inte är en Digital Link. |
| `getAllErrors()` | Fel på objektnivå + alla elementfel (andra än WARNING). |
| `getAllWarnings()` | Varningar på objektnivå + alla elementvarningar. |
| `getAllIssues()` | Allt tillsammans. |

---

### GS1AIObjectElement

En enskild igenkänd AI-instans.

| Metod | Beskrivning |
|---|---|
| `getAi()` | AI-kod, till exempel `"01"`, `"3102"`. |
| `getTitle()` | GS1-datarubrik, till exempel `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Fullständig GS1-beskrivning av AI:t, **lokaliserad till tolkningsspråket** (till exempel `"Global Trade Item Number (GTIN)"` på engelska). Faller tillbaka på den engelska texten ur AI-definitionen om översättning saknas. |
| `getFormatString()` | Formatbeskrivare som omfattar AI:t *och* dess data, till exempel `"N2+N14"` för AI `01`, `"N2+X..20"` för AI `10`, `"N4+N3+N..15"` för AI `3932`. |
| `getValue()` | Rått datavärde utvunnet ur elementsträngen. |
| `isFixedLength()` | `true` om AI:t har fast datalängd. |
| `getPosition()` | Teckenförskjutning (från noll) i det ursprungliga indata. |
| `getGS1ComponentValues()` | Värdedelar per komponent (för AI med flera komponenter). |
| `getErrors()` | Fel på elementnivå, andra än WARNING. |
| `getWarnings()` | Upplysningar av nivån WARNING för elementet. |
| `getIssues()` | Elementets fel och varningar tillsammans. |
| `hasErrors()` | `true` om fel andra än WARNING är fogade till elementet. |
| `hasWarnings()` | `true` om upplysningar av nivån WARNING är fogade till elementet. |
| `getInterpretations()` | `GS1AIInterpretation`-poster (fylls i läget INTERPRETATION). |
| `getInterpretation(String type)` | Första tolkningen som motsvarar den angivna typnyckeln ur `GS1Constants_Enricher`, eller `null`. |
| `getDigitalLinkAIType()` | Elementets Digital Link-roll (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), eller `null` för indata i form av elementsträng. |
| `hasDigitalLinkAIType()` | `true` om en Digital Link-roll tilldelats. |

---

### GaiaError

Ett oföränderligt valideringsfel eller en upplysning.

| Metod | Beskrivning |
|---|---|
| `getId()` | Katalogidentifierare, till exempel `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` eller `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` eller `INTERNAL`. |
| `getCode()` | Kort maskinläsbar kod. |
| `getAi()` | AI-koden som orsakade felet, eller `null` vid fel på objektnivå. |
| `getMessage()` | Läsbart meddelande med insatta värden. |
| `getPosition()` | Teckenförskjutning (från noll) i det ursprungliga indata. |

---

### GS1AIInterpretation

En enskild märkt tolkningsdel, fogad till ett `GS1AIObjectElement` i läget `INTERPRETATION`.

| Metod | Beskrivning |
|---|---|
| `getType()` | Maskinläsbar typnyckel, till exempel `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Densamma på alla språk. |
| `getLabel()` | Mänskligt läsbar etikett, **lokaliserad till tolkningsspråket** (till exempel `"Date"` / `"GS1 company prefix"` på engelska). |
| `getValue()` | Utvunnet eller berikat värde, till exempel `"31/12/2026"`, `"9506000"`. Lokaliseras inte. |

---

### DataCarrierEntry och DataCarrierType

Bär indata en AIM-symbologiidentifierare returnerar `ParseResult.getDataCarrier()` ett `DataCarrierEntry` som beskriver den symbol som burit data. Posten är den bestämda registerposten för den igenkända AIM-identifieraren; `DataCarrierType` är den vid kompileringen kända uppräkning som posten hör till.

#### DataCarrierEntry

Metadata för en igenkänd AIM-symbologiidentifierare (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metod | Beskrivning |
|---|---|
| `getAimCodeId()` | Den igenkända AIM-symbologiidentifieraren, till exempel `"]C1"`. |
| `getName()` | Läsbart namn på den bestämda symbolen, till exempel `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Utförligare beskrivning av bäraren. |
| `getType()` | Bärarens strukturella typ som sträng (återger `getDataCarrierType().getCategory()`). |
| `getStandard()` | Symbologistandarden, där den är noterad. |
| `getDataCarrierType()` | Den typade `DataCarrierType` för den här posten — att föredra vid programmässig dirigering. |
| `isGs1Capable()` | `true` om bäraren kan innehålla GS1-data (AI-elementsträngar och/eller Digital Link). |
| `isGs1AICapable()` | `true` om bäraren kan innehålla GS1-AI-elementsträngar. |
| `isGs1DigitalLinkCapable()` | `true` om bäraren kan innehålla en GS1 Digital Link-URI. |
| `isEciCapable()` | `true` om bäraren stöder en ECI-indikator. |
| `isRequiresGtinPadding()` | `true` för EAN/UPC/ITF-bärare vars numeriska värde fylls ut till GTIN-14 före AI-tolkningen. |

#### DataCarrierType

En vid kompileringen känd uppräkning av databärartyper, indexerad med den AIM-symbologiidentifierare som tilldelats i ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Tecknet efter `]` (*kodtecknet*) väljer familjen; de flesta familjer motsvaras av en enda konstant som täcker alla modifierare (`ITF` täcker `]I0`–`]I2`; `EAN_UPC` täcker EAN-13, UPC-A, UPC-E och EAN-8). Där GS1 avsätter en modifierare för AI-data utgör den varianten en egen konstant — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — skild från sin vanliga motsvarighet. Saknas AIM-identifierare, eller pekar den ut en okänd bärare, blir typen `UNKNOWN`.

| Metod | Beskrivning |
|---|---|
| `getCategory()` | Den övergripande kategorin `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` eller `OTHER`. |
| `getCodeChar()` | Det AIM-kodtecken som anger familjen, till exempel `"Q"` för QR Code; `null` vid `UNKNOWN`. |
| `getDisplayName()` | Läsbart namn på *typen* (kan vara vidare än `DataCarrierEntry.getName()` — till exempel `"EAN-13 / UPC-A / UPC-E / EAN-8"` mot `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` för de konstanter som alltid betecknar GS1-AI-data: de fyra av GS1 avsatta varianterna (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) samt `GS1_DATABAR`, som till sin natur hör till GS1 eftersom varje `]e`-modifierare betecknar en GS1 DataBar. Snävare än `DataCarrierEntry.isGs1AICapable()` — även en vanlig `QR_CODE` kan bära GS1-AI-data. |
| `static forAimCodeId(String)` | Bestämmer typen direkt ur en AIM-identifierare (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); returnerar `UNKNOWN` vid saknad, felformad eller okänd identifierare. |

Dirigering efter typ i stället för efter namn — till exempel för att skilja linjära symboler (Code 128) från tvådimensionella (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` täcker enbart matris- och punktsymbolerna; de staplade linjära bärarna (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) hör till `STACKED_LINEAR`, trots att de i dagligt tal kallas
”2D”-streckkoder. Vill du behandla båda som en enda grupp — till exempel för att avgöra
om en bildläsare behövs i stället för en laserläsare — pröva tillhörighet till endera kategorin.

> Typbestämningen förutsätter att AIM-symbologiidentifieraren finns med i avläsningen; utan den är `getDataCarrier()` `null` och typen `UNKNOWN`. Ställ in läsaren så att den sänder med AIM-identifierarens prefix.

---

## Felöversikt

| Kod | Nivå | Steg | Betydelse |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Okänt AI-prefix — datalängden går inte att bestämma |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Indata för kort för att läsa en fullständig AI-kod |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Avkortat värde — färre tecken än AI:t kräver |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Upprepad applikationsidentifierare i elementsträngen |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Obligatoriskt AI-beroende saknas |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Uteslutande AI-par — två AI som inte kan förekomma tillsammans |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Oväntat fel vid uppdelningen i lexem |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Tecken utanför GS1:s kodbara uppsättning i elementsträngen |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Obligatorisk FNC1-avgränsare saknas efter ett AI med variabel längd |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Överskjutande data utöver samtliga komponenters maximum |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | FNC1-avgränsare efter ett AI med fast längd i mellanliggande läge |
| `GE-W002` | WARNING | SYNTAX | FNC1 sist i elementsträngen (enbart upplysande) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Strukturbrott i en Digital Link-URI — en kod per villkor (felformad URI, schema, värd, kvalificerarordning, förbjudet AI, ingen primär nyckel (`GE-L013`), flera primära nycklar (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Värdet uppfyller inte AI:ts reguljära uttryck |
| `GE-C003` | DATA_ERROR | CONTENT | Kontrollsiffervalideringen misslyckades |
| `GE-C004` | DATA_ERROR | CONTENT | Valideringen av kontrolltecknens par misslyckades |
| `GE-C005` | FORMAT_ERROR | CONTENT | Ett komponentvärde innehåller ett tecken utanför den tillåtna uppsättningen |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Formatfel hos komponenter — en kod per valideringsvillkor (se `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Fel i den egna betydelsevalideringen — en kod per valideringsvillkor (se `content/validator/`). **Undantag:** de 14 kontrollerna av GS1-företagsprefixet nedan har nivån `WARNING`, och `GE-C168` (okänd numerisk landskod enligt ISO 3166-1) har `FORMAT_ERROR`. |
| Kontroller av GS1-företagsprefixet | WARNING | CONTENT | Nyckeln inleds inte med ett känt GS1-företagsprefix, hos de AI som bär en GS1-nyckel — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Enbart upplysande — påverkar inte giltigheten. |
| `GE-C169` | DATA_ERROR | CONTENT | IMEI-kontrollsiffran (Luhn) misslyckades hos AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | EID-kontrollsiffran (Luhn) misslyckades hos AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Okänd AIM-symbologiidentifierare |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Bäraren bestämd, men den stöder varken GS1-AI-elementsträngar eller Digital Link-URI:er |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Oväntat internt fel |

> **Känd brist i meddelandeutskriften.** Katalogmallarna omger insatta värden
> med dubblerade apostrofer efter MessageFormats mönster (`''{value}''`), men
> `ErrorRegistry` sätter in värdena med ett enkelt `String.replace`, så dubbleringen överlever ända till
> `getMessage()` — just nu ser du `value ''09506000134351''` där de meddelandetexter
> som citeras i den här handboken visar `value '09506000134351'`. Det gäller varje meddelande
> som omger ett värde med citattecken, i samtliga 35 språkkataloger. Tolka inte felmeddelanden;
> jämför `getId()` / `getCode()`.

---

## Trådsäkerhet

`GaiaParser` är trådsäker när den väl skapats. En enda instans får delas mellan trådar och användas samtidigt. Det rekommenderade mönstret är att skapa en instans när tillämpningen startar och återanvända den:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` är oföränderlig och lika trygg att dela. Den enda skyldighet i fråga om trådsäkerhet som biblioteket inte kan ta åt dig gäller [indatamodifierarna](#indatamodifierare): av varje modifierare mellanlagras en enda instans som delas av alla samtidiga tolkningar, och implementationerna måste därför sakna tillstånd.

---

## Bilaga A — AI-strängkonstanter

`GS1Constants_AICodes` (i paketet `tools.pantheum.gaia.gs1.constants`) deklarerar en konstant av typen `String` för varje applikationsidentifierare som GAIA känner igen. Använd dessa konstanter i stället för att skriva in AI-koder som strängar:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Varje konstant innehåller AI-kodens textform (till exempel `AI_01_GTIN = "01"`).

### Identifiering och serialisering

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Serienummer för transportenhet (SSCC). |
| `01` | `AI_01_GTIN` | Globalt artikelnummer (GTIN). |
| `02` | `AI_02_CONTENT` | Globalt artikelnummer (GTIN) för ingående handelsenheter. |
| `03` | `AI_03_MTO_GTIN` | Identifiering av en tillverkad-på-beställning-artikel (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Parti- eller batchnummer. |
| `20` | `AI_20_VARIANT` | Intern produktvariant. |
| `21` | `AI_21_SERIAL` | Serienummer. |
| `22` | `AI_22_CPV` | Konsumentproduktvariant. |
| `235` | `AI_235_TPX` | Tredjepartskontrollerad, serialiserad utökning av globalt artikelnummer (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Ytterligare produktidentifiering tilldelad av tillverkaren. |
| `241` | `AI_241_CUST_PART_NO` | Kundens artikelnummer. |
| `242` | `AI_242_MTO_VARIANT` | Variationsnummer för tillverkad-på-beställning. |
| `243` | `AI_243_PCN` | Förpackningskomponentnummer. |
| `250` | `AI_250_SECONDARY_SERIAL` | Sekundärt serienummer. |
| `251` | `AI_251_REF_TO_SOURCE` | Referens till källenhet. |
| `253` | `AI_253_GDTI` | Globalt dokumenttypsidentifierare (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Tilläggskomponent för globalt lokaliseringsnummer (GLN). |
| `255` | `AI_255_GCN` | Globalt kupongnummer (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variabelt antal enheter (artikel med variabel mängd). |
| `37` | `AI_37_COUNT` | Antal handelsenheter eller delar av handelsenheter i en logistisk enhet. |

### Datum och tider

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Tillverkningsdatum (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Förfallodatum (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Förpackningsdatum (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Bäst före-datum (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Sista försäljningsdag (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Utgångsdatum (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Leverans tidigast datum och tid (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Leverans senast datum och tid (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Frisläppandedatum (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Utgångsdatum och -tid (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Datum för första infrysning (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Skörde-/fångstdatum (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Testas senast datum (YYMMDD[hhmm]). |

### Mängd och mått — variabelt mått (metriskt)

De fyrsiffriga AI-familjerna `310n`–`369n` kodar mängder med variabelt mått. Tredje siffran väljer måttslag; **fjärde siffran** (`n`, 0–5) är antalet underförstådda decimaler — `AI_3102_NET_WEIGHT_KG` betyder alltså nettovikt i kg med 2 decimaler.

| Familj | Konstantmönster (`n` = decimalsiffra) | Beskrivning |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Nettovikt, kilogram (artikel med variabel mängd). |
| `311n` | `AI_311n_LENGTH_M` | Längd eller första dimensionen, meter (artikel med variabel mängd). |
| `312n` | `AI_312n_WIDTH_M` | Bredd, diameter eller andra dimensionen, meter (artikel med variabel mängd). |
| `313n` | `AI_313n_HEIGHT_M` | Djup, tjocklek, höjd eller tredje dimensionen, meter (artikel med variabel mängd). |
| `314n` | `AI_314n_AREA_M` | Area, kvadratmeter (artikel med variabel mängd). |
| `315n` | `AI_315n_NET_VOLUME_L` | Nettovolym, liter (artikel med variabel mängd). |
| `316n` | `AI_316n_NET_VOLUME_M` | Nettovolym, kubikmeter (artikel med variabel mängd). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistisk vikt, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Längd eller första dimensionen, meter. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Bredd, diameter eller andra dimensionen, meter. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Djup, tjocklek, höjd eller tredje dimensionen, meter. |
| `334n` | `AI_334n_AREA_M_LOG` | Area, kvadratmeter. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistisk volym, liter. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistisk volym, kubikmeter. |
| `337n` | `AI_337n_KG_PER_M` | Kilogram per kvadratmeter. |

### Mängd och mått — variabelt mått (brittiskt/amerikanskt)

| Familj | Konstantmönster (`n` = decimalsiffra) | Beskrivning |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Nettovikt, pund (artikel med variabel mängd). |
| `321n` | `AI_321n_LENGTH_IN` | Längd eller första dimensionen, tum (artikel med variabel mängd). |
| `322n` | `AI_322n_LENGTH_FT` | Längd eller första dimensionen, fot (artikel med variabel mängd). |
| `323n` | `AI_323n_LENGTH_YD` | Längd eller första dimensionen, yard (artikel med variabel mängd). |
| `324n` | `AI_324n_WIDTH_IN` | Bredd, diameter eller andra dimensionen, tum (artikel med variabel mängd). |
| `325n` | `AI_325n_WIDTH_FT` | Bredd, diameter eller andra dimensionen, fot (artikel med variabel mängd). |
| `326n` | `AI_326n_WIDTH_YD` | Bredd, diameter eller andra dimensionen, yard (artikel med variabel mängd). |
| `327n` | `AI_327n_HEIGHT_IN` | Djup, tjocklek, höjd eller tredje dimensionen, tum (artikel med variabel mängd). |
| `328n` | `AI_328n_HEIGHT_FT` | Djup, tjocklek, höjd eller tredje dimensionen, fot (artikel med variabel mängd). |
| `329n` | `AI_329n_HEIGHT_YD` | Djup, tjocklek, höjd eller tredje dimensionen, yard (artikel med variabel mängd). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistisk vikt, pund. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Längd eller första dimensionen, tum. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Längd eller första dimensionen, fot. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Längd eller första dimensionen, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Bredd, diameter eller andra dimensionen, tum. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Bredd, diameter eller andra dimensionen, fot. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Bredd, diameter eller andra dimensionen, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Djup, tjocklek, höjd eller tredje dimensionen, tum. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Djup, tjocklek, höjd eller tredje dimensionen, fot. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Djup, tjocklek, höjd eller tredje dimensionen, yard. |
| `350n` | `AI_350n_AREA_IN` | Area, kvadrattum (artikel med variabel mängd). |
| `351n` | `AI_351n_AREA_FT` | Area, kvadratfot (artikel med variabel mängd). |
| `352n` | `AI_352n_AREA_YD` | Area, kvadratyard (artikel med variabel mängd). |
| `353n` | `AI_353n_AREA_IN_LOG` | Area, kvadrattum. |
| `354n` | `AI_354n_AREA_FT_LOG` | Area, kvadratfot. |
| `355n` | `AI_355n_AREA_YD_LOG` | Area, kvadratyard. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Nettovikt, troy ounce (artikel med variabel mängd). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Nettovikt (eller volym), uns (artikel med variabel mängd). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Nettovolym, quarts (artikel med variabel mängd). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Nettovolym, US gallon (artikel med variabel mängd). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistisk volym, quarts. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistisk volym, US gallon. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Nettovolym, kubiktum (artikel med variabel mängd). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Nettovolym, kubikfot (artikel med variabel mängd). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Nettovolym, kubikyard (artikel med variabel mängd). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistisk volym, kubiktum. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistisk volym, kubikfot. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistisk volym, kubikyard. |

### Priser och penningbelopp

Fjärde siffran (`n`) kodar antalet underförstådda decimaler. Dess tillåtna intervall
skiljer sig mellan familjerna — se kolumnen `n`.

| Familj | Konstantmönster (`n` = decimalsiffra) | `n` | Beskrivning |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Belopp att betala eller kupongvärde, lokal valuta. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Belopp att betala med ISO-valutakod. |
| `392n` | `AI_392n_PRICE` | 0–9 | Belopp att betala, enskilt valutaområde (artikel med variabel mängd). |
| `393n` | `AI_393n_PRICE` | 0–9 | Belopp att betala med ISO-valutakod (artikel med variabel mängd). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Procentuell rabatt för en kupong. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Att betala per måttenhet, enskilt valutaområde (artikel med variabel mängd). |

### Plats och försändelse

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Kundens beställningsnummer. |
| `401` | `AI_401_GINC` | Globalt identifieringsnummer för sändning (GINC). |
| `402` | `AI_402_GSIN` | Globalt sändningsidentifieringsnummer (GSIN). |
| `403` | `AI_403_ROUTE` | Dirigeringskod. |
| `410` | `AI_410_SHIP_TO_LOC` | Globalt lokaliseringsnummer (GLN) för leveransadress. |
| `411` | `AI_411_BILL_TO` | Globalt lokaliseringsnummer (GLN) för faktureringsmottagare. |
| `412` | `AI_412_PURCHASE_FROM` | Globalt lokaliseringsnummer (GLN) för inköpsställe. |
| `413` | `AI_413_SHIP_FOR_LOC` | Globalt lokaliseringsnummer (GLN) för vidarebefordran av försändelse. |
| `414` | `AI_414_LOC_NO` | Identifiering av en fysisk plats - globalt lokaliseringsnummer (GLN). |
| `415` | `AI_415_PAY_TO` | Globalt lokaliseringsnummer (GLN) för den fakturerande parten. |
| `416` | `AI_416_PROD_SERV_LOC` | Globalt lokaliseringsnummer (GLN) för produktions- eller serviceplatsen. |
| `417` | `AI_417_PARTY` | Globalt lokaliseringsnummer (GLN) för part. |
| `420` | `AI_420_SHIP_TO_POST` | Postnummer för leverans inom en enskild postmyndighet. |
| `421` | `AI_421_SHIP_TO_POST` | Postnummer för leverans med ISO-landskod. |
| `422` | `AI_422_ORIGIN` | Ursprungsland för handelsenheten. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Land för första bearbetning. |
| `424` | `AI_424_COUNTRY_PROCESS` | Land för bearbetning. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Land för demontering. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Land som täcker hela processkedjan. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Administrativ region i ursprungslandet. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Företagsnamn för leverans. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Kontaktperson för leverans. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Leveransadress, rad 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Leveransadress, rad 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Stadsdel för leverans. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Ort för leverans. |
| `4306` | `AI_4306_SHIP_TO_REG` | Region för leverans. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Landskod för leverans. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Telefonnummer för leverans. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Geografisk position för leverans. |
| `4310` | `AI_4310_RTN_TO_COMP` | Företagsnamn för retur. |
| `4311` | `AI_4311_RTN_TO_NAME` | Kontaktperson för retur. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Returadress, rad 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Returadress, rad 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Stadsdel för retur. |
| `4315` | `AI_4315_RTN_TO_LOC` | Ort för retur. |
| `4316` | `AI_4316_RTN_TO_REG` | Region för retur. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Landskod för retur. |
| `4318` | `AI_4318_RTN_TO_POST` | Postnummer för retur. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Telefonnummer för retur. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Beskrivning av tjänstekod. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Flagga för farligt gods. |
| `4322` | `AI_4322_AUTH_LEAVE` | Tillstånd att lämna utan underskrift. |
| `4323` | `AI_4323_SIG_REQUIRED` | Flagga för signaturkrav. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maximitemperatur i Fahrenheit (uttryckt i hundradels grader). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maximitemperatur i Celsius (uttryckt i hundradels grader). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimitemperatur i Fahrenheit (uttryckt i hundradels grader). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimitemperatur i Celsius (uttryckt i hundradels grader). |

### Produktegenskaper och spårbarhet

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO lagernummer (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE klassificering av slaktkroppar och styckningsdelar. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktiv styrka (potens). |
| `7005` | `AI_7005_CATCH_AREA` | Fångstområde. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Art för fiskeriändamål. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Typ av fiskeredskap. |
| `7010` | `AI_7010_PROD_METHOD` | Produktionsmetod. |
| `7020` | `AI_7020_REFURB_LOT` | Parti-ID för renovering. |
| `7021` | `AI_7021_FUNC_STAT` | Funktionsstatus. |
| `7022` | `AI_7022_REV_STAT` | Revisionsstatus. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Globalt individuellt tillgångsidentifierare (GIAI) för en enhet. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Bearbetarens nummer med tresiffrig ISO-landskod (10 platser). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC med tillägg 1 och importörsindex. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT fraktenhetstyp. |

### Nationella ersättningsnummer inom hälso- och sjukvård (NHRN)

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nationellt nummer för sjukvårdsersättning (NHRN) - Tyskland PZN. |
| `711` | `AI_711_NHRN_CIP` | Nationellt nummer för sjukvårdsersättning (NHRN) - Frankrike CIP. |
| `712` | `AI_712_NHRN_CN` | Nationellt nummer för sjukvårdsersättning (NHRN) - Spanien CN. |
| `713` | `AI_713_NHRN_DRN` | Nationellt nummer för sjukvårdsersättning (NHRN) - Brasilien DRN. |
| `714` | `AI_714_NHRN_AIM` | Nationellt nummer för sjukvårdsersättning (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Nationellt nummer för sjukvårdsersättning (NHRN) - USA NDC. |
| `716` | `AI_716_NHRN_AIC` | Nationellt nummer för sjukvårdsersättning (NHRN) - Italien AIC. |
| `717` | `AI_717_NHRN_SRN` | Nationellt nummer för sjukvårdsersättning (NHRN) - Costa Rica, sanitärt registernummer. |

### Hälso- och sjukvård, GMN, HIDRI, CPID, personuppgifter

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Certifieringsreferens (10 platser). |
| `7240` | `AI_7240_PROTOCOL` | Protokoll-ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC-medietyp. |
| `7242` | `AI_7242_VCN` | Versionskontrollnummer (VCN). |
| `7250` | `AI_7250_DOB` | Födelsedatum (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Datum och tid för födsel (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biologiskt kön. |
| `7253` | `AI_7253_FAMILY_NAME` | Personens efternamn. |
| `7254` | `AI_7254_GIVEN_NAME` | Personens förnamn. |
| `7255` | `AI_7255_SUFFIX` | Personens namntillägg (suffix). |
| `7256` | `AI_7256_FULL_NAME` | Personens fullständiga namn. |
| `7257` | `AI_7257_PERSON_ADDR` | Personens adress. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Barnets födelseordning. |
| `7259` | `AI_7259_BABY` | Barnets efternamn. |
| `8001` | `AI_8001_DIMENSIONS` | Rullprodukter (bredd, längd, kärndiameter, riktning, skarvar). |
| `8002` | `AI_8002_CMT_NO` | Identifierare för mobiltelefon. |
| `8003` | `AI_8003_GRAI` | Globalt identifierare för returtillgång (GRAI). |
| `8004` | `AI_8004_GIAI` | Globalt individuellt tillgångsidentifierare (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Pris per måttenhet. |
| `8006` | `AI_8006_ITIP` | Identifiering av en enskild del av en handelsenhet (ITIP). |
| `8007` | `AI_8007_IBAN` | Internationellt bankkontonummer (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Datum och tid för tillverkning (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optiskt läsbar sensorindikator. |
| `8010` | `AI_8010_CPID` | Komponent-/delidentifierare (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Serienummer för komponent-/delidentifierare (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Programvaruversion. |
| `8013` | `AI_8013_GMN` | Globalt modellnummer (GMN). |
| `8014` | `AI_8014_MUDI` | Registreringsidentifierare för höggradigt individualiserad enhet (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Globalt tjänsterelationsnummer (GSRN) för att identifiera relationen mellan en organisation som erbjuder tjänster och tjänsteleverantören. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Globalt tjänsterelationsnummer (GSRN) för att identifiera relationen mellan en organisation som erbjuder tjänster och tjänstemottagaren. |
| `8019` | `AI_8019_SRIN` | Instansnummer för tjänsterelation (SRIN). |
| `8020` | `AI_8020_REF_NO` | Referensnummer för inbetalningsavi. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identifiering av delar av en handelsenhet (ITIP) som ingår i en logistisk enhet. |
| `8030` | `AI_8030_DIGSIG` | Digital signatur (DigSig). |
| `8040` | `AI_8040_IMEI` | Internationell mobilutrustningsidentitet (IMEI). |
| `8041` | `AI_8041_IMEI2` | Internationell mobilutrustningsidentitet 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nummer för inbyggt SIM-kort (eSIM). |
| `8043` | `AI_8043_PSIM` | Nummer för fysiskt SIM-kort. |
| `8110` | `AI_8110` | Kupongkodsidentifiering för användning i Nordamerika. |
| `8111` | `AI_8111_POINTS` | Lojalitetspoäng för en kupong. |
| `8112` | `AI_8112` | Kupongkodsidentifiering för positiv erbjudandefil för användning i Nordamerika. |
| `8200` | `AI_8200_PRODUCT_URL` | URL för utökad förpackning. |

### Internt bruk / företagsbruk

| AI | Konstant | Beskrivning |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Information som ömsesidigt överenskommits mellan handelspartner. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Företagsintern information (9 platser). |

---

## Bilaga B — konstanter för tolkningsnycklar

När `GaiaParser.parse()` anropas med `ParseMode.INTERPRETATION` kan varje `GS1AIObjectElement` bära en lista med `GS1AIInterpretation`-objekt som skapats av områdesinriktade berikare. Använd konstanterna ur `GS1Constants_Enricher` (i paketet `tools.pantheum.gaia.gs1.constants`) som nycklar för att slå upp bestämda tolkningsvärden:

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

Visningsetiketterna är **inte** konstanter — de ligger i de lokaliserade katalogerna under `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` och indexeras med typkonstanten. `GS1AIInterpretation.getLabel()` returnerar etiketten för tolkningsspråket (se [Lokaliserade meddelanden och etiketter](#lokaliserade-meddelanden-och-etiketter)) och faller tillbaka på engelska när en katalog utelämnar nyckeln. Kolumnen ”Visningsetikett” nedan anger den svenska texten så som den levereras i katalogen; typnycklarna själva är desamma på alla språk — jämför därför alltid nyckeln, aldrig etiketten.

### Datum och tid

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `DATE_VALUE` | Datum | Datum-AI (11–17, 7003, 7006, 7011 m.fl.) |
| `DATE_FORMAT` | Datumformat | Datum-AI |
| `TIME_VALUE` | Tid | AI som bär tid (7003, 7011, 8008 m.fl.) |
| `TIME_FORMAT` | Tidsformat | AI som bär tid |
| `DATETIME_VALUE` | Datum och tid | Datum- och tids-AI |
| `DATETIME_FORMAT` | Datum- och tidsformat | Datum- och tids-AI |

### Skördedatum

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Startdatum för skörd | AI 7007 |
| `HARVEST_END_DATE` | Slutdatum för skörd | AI 7007 (valfritt intervallslut) |
| `HARVEST_DATE_RANGE` | Datumintervall för skörd | AI 7007 |

### GS1-företagsprefix

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1-företagsprefix | GTIN- / GLN- / SSCC-AI |
| `GS1_MEMBER_CODE` | GS1-medlemskod | GTIN- / GLN- / SSCC-AI |
| `GS1_MEMBER_NAME` | GS1-medlemsorganisation | GTIN- / GLN- / SSCC-AI |

### GTIN

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN-typ | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Förpackningsnivå | AI 01 |
| `GTIN_CHECK_DIGIT` | Kontrollsiffra | AI 01, 02 |

### SSCC

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Utökningssiffra | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Seriereferens | AI 00 |
| `SSCC_CHECK_DIGIT` | Kontrollsiffra | AI 00 |

### Land (ISO 3166)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Landskod (numerisk) | AI för ett enda land (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Landskod (alfa-2) | Alfa-2-lands-AI |
| `COUNTRY_NAME` | Landsnamn | AI för ett enda land |
| `COUNTRY_LIST` | Länder | AI 423 — alla namn sammanfogade, till exempel `Australia, New Zealand` |

AI 423 (land för första bearbetning) kan bära upp till fem länder och avger därför ett
**numrerat par per land** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — följt av den enda sammanfattningen
`COUNTRY_LIST`. Sätt samman nycklarna av konstanterna `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` och indexet räknat från 1, eller gå helt enkelt igenom `getInterpretations()`; nycklarna
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` utan suffix avges **inte** för AI 423.

### Valuta (ISO 4217)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Valutakod | Belopps-AI med valuta (391n, 393n) |
| `CURRENCY_ALPHA` | Alfabetisk valutakod | Belopps-AI med valuta |
| `CURRENCY_NAME` | Valutanamn | Belopps-AI med valuta |

### Temperatur

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatur | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Temperaturenhet | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatur (formaterad) | AI 4330–4333 |

### Kön (ISO 5218)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `SEX_CODE` | Könskod | AI 7252 |
| `SEX_DESCRIPTION` | Könsbeskrivning | AI 7252 |

### Vattenlevande arter (FAO)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Artkod | AI 7008 |
| `SPECIES_SCIENTIFIC` | Vetenskapligt namn | AI 7008 |
| `SPECIES_ENGLISH` | Vanligt namn | AI 7008 |
| `SPECIES_FAMILY` | Familj | AI 7008 |
| `SPECIES_ORDER` | Ordning | AI 7008 |

### Natos förrådsbeteckning (NSN)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `NSN_FSG` | Försörjningsgrupp | AI 7001 |
| `NSN_FSG_NAME` | Försörjningsgruppens namn | AI 7001 |
| `NSN_FSCG` | Försörjningsklass | AI 7001 |
| `NSN_FSCG_NAME` | Försörjningsklassens namn | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Landskod | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Land | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO-landskod | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS-kategori | AI 7001 |
| `NSN_NIIN` | Nationellt artikelnummer | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Rullprodukter

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Rullbredd (mm) | AI 8001 |
| `ROLL_LENGTH` | Rullängd (m) | AI 8001 |
| `CORE_DIAMETER` | Kärndiameter (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Kod för lindningsriktning | AI 8001 |
| `WINDING_DIRECTION` | Lindningsriktning | AI 8001 |
| `SPLICES` | Skarvar | AI 8001 |

### IBAN

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Landskod | AI 8007 |
| `IBAN_COUNTRY_NAME` | Land | AI 8007 |
| `IBAN_CHECK_DIGITS` | Kontrollsiffror | AI 8007 |
| `IBAN_CHECK_VALID` | Kontroll | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Serienummer | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Kontrollsiffra | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Utfärdande organ | AI 8040, 8041 |

De femton siffrorna delas upp som `[ TAC (8) ][ serienummer (6) ][ Luhn-kontrollsiffra (1) ]`, där
RBI utgörs av TAC:ens två första siffror — `IMEI_RBI` är alltså ett prefix till `IMEI_TAC` och inte
ett eget avsnitt. `IMEI_FORMATTED` återger GSMA:s gängse visningsgruppering
`AA-BBBBBB-CCCCCC-D` (till exempel `49-015420-323751-8`), som delar TAC:en vid RBI-gränsen;
den äldre grupperingen `6-2-6-1`, som skar där den avskaffade Final Assembly
Code började, avges inte.

`IMEI_RBI_NAME` slår upp RBI:n som namnet på det tilldelande organet via `ImeiRbiData` och
**läggs till sist och endast när koden finns upptagen där**. Den tabellen omfattar tre grupper:

- **Tilldelar för närvarande** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF samt `99`
  Global Hexadecimal Administrator och `98` (reserverad).
- **Testintervall** — `00` och `02`–`09`, som anger test-IMEI i stället för en verklig tilldelning.
  Fråga efter dem med `ImeiRbiData.isTestCode(code)`.
- **Tilldelar inte längre** — historiska organ såsom `49` (BZT/BAPT, Tyskland), `44`
  (BABT, Storbritannien) eller `91` (MSAI, Indien). Fråga efter dem med `ImeiRbiData.isNoLongerAllocating(code)`.
  Enheter med dessa koder är helt vanliga och används alltjämt; endast tilldelningen av nya koder
  har upphört, så detta är en uppgift för rapportering, aldrig ett tecken på giltighet.

Att `IMEI_RBI_NAME` saknas betyder ”den här RBI:n finns inte i vår tabell”, **inte** ”ogiltigt IMEI”:
tabellen är sammanställd ur en publicerad RBI-förteckning och inte hämtad direkt från GSMA, och kan
därför släpa efter nyligen utsedda organ. Dra ingen slutsats om giltighet ur att den saknas;
RBI:n är inget kontrolltecken. Kod som går igenom tolkningslistan måste likaså
tåla att den saknas i stället för att gå på position.

### SIM-identifierare (EID / ICCID)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Branschkategori | AI 8042 |
| `EID_BODY` | EID-huvuddel | AI 8042 |
| `EID_CHECK_DIGIT` | Kontrollsiffra | AI 8042 |
| `ICCID_BODY` | ICCID-huvuddel | AI 8043 |
| `ICCID_EXTENSION` | Utökning | AI 8043 |

`SIM_MII` bär de **två** inledande siffrorna (`89`) — det par som ITU-T E.118 tilldelar
telekommunikation. ISO/IEC 7812 själv bestämmer MII som **enbart den första siffran**, och därför
härleder `SIM_MII_NAME` kategorin ur den inledande `8` via `Iso7812Data`, vilket ger
”Healthcare, telecommunications and other future industry assignments”. För ett välformat
EID är värdet därmed konstant; det anges för spårbarhet mot standarden, inte som
särskiljande kännetecken. `Iso7812Data.nameForCode(digit)` tar en ensam siffra och
`nameForIdentifier(prefix)` tar ett längre prefix och läser dess inledande siffra.

`SIM_MII_NAME` avges endast av `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
visar `SIM_MII` utan kategorin.

### Certifieringsreferens

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Sekvensnummer | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Kod för certifieringsschema | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Certifieringsschema | AI 7230–7239 |
| `CERT_REFERENCE` | Certifieringsreferens | AI 7230–7239 |

### GS1 UIC

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC-kod | AI 7040 |
| `UIC_EXTENSION_1` | Utökning 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Importörindex | AI 7040 |

### Den nyföddes födelseordning

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Födelseposition | AI 7258 |
| `BIRTH_TOTAL` | Totalt antal födslar | AI 7258 |
| `BIRTH_SEQUENCE` | Födelsesekvens | AI 7258 |

### Globalt modellnummer (GMN)

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Modellreferens | AI 8013 |
| `GMN_CHECK_PAIR` | Kontrollpar | AI 8013 |

### HIDRI

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Enhetsreferens | AI 8014 |
| `HIDRI_CHECK_PAIR` | Kontrollpar | AI 8014 |

### CPID

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Komponent- och delreferens | AI 8010–8011 |

### Decimal- och måttvärden

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Decimalvärde | Numeriska AI med underförstådda decimaler (31xx–36xx) |
| `DECIMAL_AMOUNT` | Belopp | Pris-AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | Procent | AI 394n |
| `DECIMAL_PLACES` | Decimaler | Tillsammans med `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Procentformat | AI 394n |
| `ISO_UNIT_CODE` | ISO-enhetskod | Mått-AI |
| `ISO_UNIT_NAME` | ISO-enhetsnamn | Mått-AI |
| `MONETARY_AMOUNT` | Penningbelopp | Pris-AI |
| `MONETARY_AMOUNT_DISPLAY` | Penningbelopp (formaterat) | Pris-AI |

### Geografiska koordinater

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `LATITUDE` | Latitud | AI 4309 |
| `LONGITUDE` | Longitud | AI 4309 |
| `GEO_COORDINATES` | Geokoordinater | AI 4309 |
| `LATITUDE_DMS` | Latitud (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitud (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Geokoordinater (DMS) | AI 4309 |

### Produktionsmetod

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Kod för produktionsmetod | AI 7010 |
| `PRODUCTION_METHOD` | Produktionsmetod | AI 7010 |

### AIDC-medietyp

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC-medietypkod | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC-medietyp | AI 7241 |

### Del av totalt antal

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Stycknummer | AI 8006 |
| `PIECE_TOTAL` | Totalt antal stycken | AI 8006 |
| `PIECE_OF_TOTAL` | Stycke av totalt | AI 8006 |

### Uppdelning i komponenter

Nycklar som avges av de deklarativa komponentuppdelningarna i `content/ai-content.json` och inte
av en berikare i Java: de blottlägger de namngivna delarna av ett sammansatt AI-värde. Till skillnad från alla
övriga nycklar i den här bilagan har de **ingen konstant i `GS1Constants_Enricher`**: jämför
den ordagranna strängen, eller läs typen via `GS1AIInterpretation.getType()`.

| Typnyckel | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Kontrollsiffra | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Serienummer | AI 253, 255, 8003 |
| `POSTAL_CODE` | Postnummer | AI 421 |
| `PROCESSOR_ID` | Bearbetar-ID | AI 7030–7039 |

Observera att `CHECK_DIGIT` här är den allmänna nyckeln för komponentuppdelning, skild från de
berikarspecifika nycklarna `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` och
`EID_CHECK_DIGIT` som räknats upp ovan.

### Övrigt

| Nyckelkonstant | Visningsetikett | Skapas av |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Värde | Booleska AI / flagg-AI (4321–4323) |
| `DECODED_TEXT` | Avkodad text | Fritext-AI |
