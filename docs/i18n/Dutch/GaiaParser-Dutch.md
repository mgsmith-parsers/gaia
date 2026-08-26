# GAIA (GS1 Application Identifiers Analyser) — Ontwikkelaarshandleiding

## Inhoudsopgave

1. [Overzicht](#overzicht)
2. [Over GS1 en de General Specifications](#over-gs1-en-de-general-specifications)
3. [GS1-toepassingsidentificatoren](#gs1-toepassingsidentificatoren)
4. [Snelstart](#snelstart)
5. [Verwerkingsketen van de parser](#verwerkingsketen-van-de-parser)
   - [Voorfase — invoermodificatoren](#voorfase--invoermodificatoren)
   - [Fase 0 — correlatie-ID](#fase-0--correlatie-id)
   - [Fase 1 — invoerrouting](#fase-1--invoerrouting)
   - [Fase 2 — syntaxis](#fase-2--syntaxis)
   - [Fase 3 — inhoud](#fase-3--inhoud)
   - [Fase 4 — interpretatie](#fase-4--interpretatie)
6. [Parserconfiguratie (`ParseConfig`)](#parserconfiguratie-parseconfig)
   - [Opties](#opties)
   - [Gelokaliseerde meldingen en labels](#gelokaliseerde-meldingen-en-labels)
   - [Datumopmaak](#datumopmaak)
7. [Invoermodificatoren](#invoermodificatoren)
   - [Ingebouwde modificatoren](#ingebouwde-modificatoren)
   - [Een modificator schrijven](#een-modificator-schrijven)
   - [Modificatoren registreren](#modificatoren-registreren)
   - [Nagaan wat een modificator heeft gedaan](#nagaan-wat-een-modificator-heeft-gedaan)
   - [Foutafhandeling bij modificatoren](#foutafhandeling-bij-modificatoren)
8. [Parsemodi](#parsemodi)
   - [Modus DATA_CARRIER](#modus-data_carrier)
   - [Modus SYNTAX](#modus-syntax)
   - [Modus CONTENT](#modus-content)
   - [Modus INTERPRETATION (standaard)](#modus-interpretation-standaard)
9. [Correlatie-ID](#correlatie-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Werken met de resultaten](#werken-met-de-resultaten)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry en DataCarrierType](#datacarrierentry-en-datacarriertype)
12. [Foutenreferentie](#foutenreferentie)
13. [Threadveiligheid](#threadveiligheid)
14. [Bijlage A — AI-tekenreeksconstanten](#bijlage-a--ai-tekenreeksconstanten)
    - [Identificatie en serialisatie](#identificatie-en-serialisatie)
    - [Datums en tijden](#datums-en-tijden)
    - [Hoeveelheid en maat — variabele maat (metrisch)](#hoeveelheid-en-maat--variabele-maat-metrisch)
    - [Hoeveelheid en maat — variabele maat (imperiaal / VS)](#hoeveelheid-en-maat--variabele-maat-imperiaal--vs)
    - [Prijzen en geldbedragen](#prijzen-en-geldbedragen)
    - [Locatie en verzending](#locatie-en-verzending)
    - [Productkenmerken en traceerbaarheid](#productkenmerken-en-traceerbaarheid)
    - [Nationale vergoedingsnummers in de zorg (NHRN)](#nationale-vergoedingsnummers-in-de-zorg-nhrn)
    - [Zorg, GMN, HIDRI, CPID, persoonsgegevens](#zorg-gmn-hidri-cpid-persoonsgegevens)
    - [Intern / bedrijfsgebruik](#intern--bedrijfsgebruik)
15. [Bijlage B — constanten van de interpretatiesleutels](#bijlage-b--constanten-van-de-interpretatiesleutels)
    - [Datum en tijd](#datum-en-tijd)
    - [Oogstdatum](#oogstdatum)
    - [GS1-bedrijfsprefix](#gs1-bedrijfsprefix)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Land (ISO 3166)](#land-iso-3166)
    - [Valuta (ISO 4217)](#valuta-iso-4217)
    - [Temperatuur](#temperatuur)
    - [Geslacht (ISO 5218)](#geslacht-iso-5218)
    - [Waterdieren (FAO)](#waterdieren-fao)
    - [NAVO-voorraadnummer (NSN)](#navo-voorraadnummer-nsn)
    - [Rolproducten](#rolproducten)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM-identificaties (EID / ICCID)](#sim-identificaties-eid--iccid)
    - [Certificeringsreferentie](#certificeringsreferentie)
    - [GS1 UIC](#gs1-uic)
    - [Geboortevolgorde van de pasgeborene](#geboortevolgorde-van-de-pasgeborene)
    - [Wereldwijd modelnummer (GMN)](#wereldwijd-modelnummer-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Decimale en maatwaarden](#decimale-en-maatwaarden)
    - [Geocoördinaten](#geocoördinaten)
    - [Productiemethode](#productiemethode)
    - [AIDC-mediatype](#aidc-mediatype)
    - [Stuk van totaal](#stuk-van-totaal)
    - [Componentopsplitsingen](#componentopsplitsingen)
    - [Diversen](#diversen)

---

## Overzicht

`GaiaParser` is het startpunt voor het parseren van elementreeksen met GS1-toepassingsidentificatoren (AI). De parser aanvaardt de ruwe uitvoer van een scanner in elk van de volgende vormen en levert een gestructureerd `ParseResult` met alle herkende AI's, de validatiefouten en, desgewenst, de voor mensen leesbare interpretaties:

- Eenvoudige AI-elementreeks: `0109506000134352`
- Elementreeks voorafgegaan door een AIM-symbologie-identificatie: `]C10109506000134352`
- GS1 Digital Link-URI: `https://example.com/01/09506000134352`
- Elk van bovenstaande vormen, eventueel voorafgegaan door een correlatie-ID van 8 cijfers: `12345678~0109506000134352`

**Startklasse:** `tools.pantheum.gaia.GaiaParser`

> **Nieuw met Gaia?** Begin met de **[snelstartgids van GaiaParser](GaiaParser-QuickStart-Dutch.md)** — de dependency, een eerste parse en de handvol valkuilen die iedereen tegenkomt, in ongeveer tien minuten. Deze handleiding is de volledige referentie.

> Voor de omgekeerde bewerking — het *samenstellen* van welgevormde elementreeksen en Digital Link-URI's uit AI/waarde-paren — zie de **[GaiaBuilder — Ontwikkelaarshandleiding](GaiaBuilder-Dutch.md)**.

---

## Over GS1 en de General Specifications

**GS1** is een wereldwijde non-profitorganisatie die open standaarden ontwikkelt en onderhoudt voor identificatie en gegevensuitwisseling in toeleveringsketens. Haar standaarden worden gebruikt in de detailhandel, de zorg, de logistiek, de horeca en tal van andere sectoren, en bestrijken alles van productbarcodes op consumentenverpakkingen tot geserialiseerde tracering van farmaceutische doses.

Het gezaghebbende naslagwerk voor alles wat deze parser implementeert zijn de **GS1 General Specifications** — één enkel document dat het volgende vastlegt:

- Alle codes van toepassingsidentificatoren (AI), hun gegevenstitels, formaten en validatieregels
- De syntaxisregels voor het samenstellen en coderen van AI-elementreeksen
- De eisen aan barcodesymbologieën en de toewijzing van AIM-symbologie-identificaties
- De algoritmen voor controlecijfers en controletekens
- De interpretatie van jaartallen met twee cijfers (de schuivendvensterregel)
- De specificaties van Data Matrix, QR Code, GS1-128, GS1 DataBar en overige gegevensdragers

De GS1 General Specifications worden jaarlijks bijgewerkt. De geldende uitgave en de bijbehorende hulpmiddelen zijn beschikbaar op:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementeert **release 26.0 (bekrachtigd in januari 2026)** van de GS1 General Specifications.

GS1 Digital Link-URI's vallen onder een aanvullende standaard, **GS1 Digital Link: URI Syntax**, die de primaire identificatiesleutels, de volgorde van de sleutelkwalificatoren en de codering van de gegevensattributen vastlegt die de parser toepast op Digital Link-invoer:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementeert **release 1.7.0 (bekrachtigd in augustus 2026)** van de standaard GS1 Digital Link: URI Syntax.

Verwijzingen naar paragrafen in dit document betreffen de GS1 General Specifications (bijvoorbeeld «Table 7-5», «section 7.12»), met uitzondering van de Digital Link-paragraafnummers (bijvoorbeeld «§4.9», «§4.12»), die naar de standaard GS1 Digital Link: URI Syntax verwijzen.

---

## GS1-toepassingsidentificatoren

Een **GS1-toepassingsidentificatie (AI)** is een kort numeriek voorvoegsel — twee tot vier cijfers — dat de betekenis en het formaat bepaalt van de gegevens die er onmiddellijk op volgen. AI's zijn gedefinieerd in de GS1 General Specifications en bestrijken een breed scala aan gegevens uit de toeleveringsketen: productidentificaties, datums, hoeveelheden, partijnummers, serienummers, maten, URL's en meer.

### Opbouw van een AI-element

Elk AI-element bestaat uit twee delen:

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

De AI-code is altijd numeriek. De gegevenswaarde volgt er onmiddellijk op, zonder scheidingsteken tussen code en waarde.

### AI's met vaste en met variabele lengte

AI's vallen uiteen in twee categorieën:

| Type | Gedrag | Voorbeeld |
|---|---|---|
| **Vaste lengte** | Exact aantal tekens, wordt altijd volledig verbruikt | AI `01` (GTIN) — altijd 14 cijfers |
| **Variabele lengte** | Van 1 teken tot een maximum; eindigt met een GS-scheidingsteken of het einde van de invoer | AI `10` (partij) — 1 tot 20 alfanumerieke tekens |

Of een AI een vaste dan wel variabele lengte heeft, volgt uitsluitend uit zijn definitie in de GS1-specificatie — de parser gokt nooit.

### Elementreeksen met meerdere AI's

Meerdere AI's kunnen tot één elementreeks aaneengeschakeld worden. AI's met vaste lengte kunnen direct aaneengeschakeld worden, omdat de parser altijd precies weet hoeveel tekens hij moet verbruiken. AI's met variabele lengte moeten worden afgesloten met het **GS-teken** (ASCII `0x1D`, in barcodesymbologieën ook FNC1 genoemd) zodra er nog een AI op volgt, zodat de parser weet waar een waarde eindigt en de volgende AI-code begint.

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

Schrijf het GS-teken in Java-tekenreeksliteralen met de Unicode-escape `""`.

### Veelvoorkomende AI's

| AI | Gegevenstitel | Formaat | Voorbeeldwaarde |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (JJMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (JJMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, één valutagebied) |
| `710` | NHRN PZN | X..20 | `12345678` |

> Het **vierde cijfer** van een AI voor maat of prijs met 4 cijfers codeert het aantal impliciete decimalen: `3103` is het nettogewicht in kg met 3 decimalen (`001500` = 1,500 kg), terwijl `3102` dezelfde cijfers als 15,00 kg zou lezen. De kolom `Formaat` hierboven toont het formaat van de *gegevens*; de volledige `getFormatString()` van elke AI omvat de AI zelf (bijvoorbeeld `N4+N6` voor `3103`).

### Voor mensen leesbare interpretatie (HRI)

De gebruikelijke leesbare vorm plaatst elke AI-code tussen haakjes, direct vóór de bijbehorende waarde, met een spatie tussen de elementen:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Het GS-scheidingsteken verschijnt niet in de HRI. `GS1AIObject.toHriString()` levert dit formaat.

### AI-codes van vier cijfers

Sommige AI's gebruiken vier cijfers in plaats van twee. De eerste twee benoemen de AI-familie; het derde en/of vierde dragen extra semantiek (zoals de positie van de impliciete decimaalkomma bij maat-AI's). De parser leidt de volledige AI-code automatisch af uit de elementreeks — de aanroeper werkt altijd met de volledige code (bijvoorbeeld `"3102"`, niet enkel `"31"`).

---

## Snelstart

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

> **GS-scheidingsteken:** binnen een reeks met meerdere AI's moeten AI's met variabele lengte worden afgebakend met het GS-teken (ASCII `0x1D`). Gebruik `""` in Java-tekenreeksliteralen.

---

## Verwerkingsketen van de parser

### Voorfase — invoermodificatoren

Bevat de `ParseConfig` **invoermodificatoren**, dan draaien die vóór al het andere: vóór het verwijderen van de correlatie-ID, vóór de detectie van de gegevensdrager en vóór de GS1-keten wordt betreden. Elke modificator herschrijft de ruwe invoer voor de volgende, en alle onderstaande fasen werken op de uitvoer van de keten.

Standaard is er geen enkele modificator geconfigureerd, zodat deze voorfase niets doet zolang u ze niet uitdrukkelijk inschakelt. Zie [Invoermodificatoren](#invoermodificatoren).

---

### Fase 0 — correlatie-ID

Vóór enige GS1-verwerking controleert `GaiaParser` of de invoer begint met een optioneel **correlatie-ID-voorvoegsel**: precies 8 decimale ASCII-cijfers gevolgd door een tilde (`~`), bijvoorbeeld `12345678~`.

Is het voorvoegsel aanwezig, dan wordt het verwijderd en als `CorrelationInfo` bewaard in het teruggegeven `ParseResult`. Alle volgende fasen werken op de aldus ontdane payload. Ontbreekt het voorvoegsel, dan gaat de invoer ongewijzigd door.

Zie [Correlatie-ID](#correlatie-id) voor de details.

---

### Fase 1 — invoerrouting

Na het verwijderen van de correlatie controleert `GaiaParser` of de (ontdane) invoer begint met een **AIM-symbologie-identificatie**: een voorvoegsel van drie tekens in de vorm `]` + ASCII-letter + ASCII-cijfer (bijvoorbeeld `]C1` voor GS1-128, `]d2` voor GS1 DataMatrix, `]e0` voor GS1 DataBar / GS1 Composite).

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

Is de gegevensdrager niet geschikt voor GS1-AI's (bijvoorbeeld een postbarcode), dan stopt het parseren onmiddellijk met een fout `GE-D002`.

---

### Fase 2 — syntaxis

Draait altijd. Bestaat uit twee deelstappen:

**2a. Tokeniseren (`AISyntaxParser`)**
- Leest de lengte van de AI-code uit de eerste twee tekens aan de hand van de GS1-voorvoegseltabel (GS1 General Specifications, tabel 7-5).
- AI's met vaste lengte verbruiken een exact aantal bytes uit de invoer.
- AI's met variabele lengte worden gelezen tot een GS-teken of tot het einde van de invoer.
- Bij AI's met meerdere componenten wordt het waardeblok in segmenten per component gesneden.

**2b. Structuurvalidatie (`SyntaxValidator`)**
- Spoort dubbele AI's op (`GE-S004`).
- Controleert vereiste AI-afhankelijkheden, bijvoorbeeld AI `02` dat AI `37` vereist (`GE-S005`).
- Controleert uitgesloten AI-combinaties (`GE-S006`).

Fouten in deze fase hebben niveau `SYNTAX_ERROR` (tokeniseren) of `INTEGRITY_ERROR` (structuur). Is er **ook maar één** fout — uit het tokeniseren of uit de structuur — dan stopt de keten en worden de fasen inhoud en interpretatie overgeslagen.

---

### Fase 3 — inhoud

Draait alleen als fase 2 geen fouten opleverde (noch uit het tokeniseren, noch uit de structuur). Keten per element (elke stap draait alleen als de vorige geen fouten opleverde):

| Stap | Validator | Foutcodes |
|---|---|---|
| Controle met reguliere expressie | `RegexValidator` | `GE-C001` |
| Tekenset en formaat van de componenten | `ComponentValidator` | `GE-C005` + formaatcodes per voorwaarde (`GE-C054`–`GE-C115`) |
| Controlecijfer / controleteken | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Eigen semantische validatie | `ContentValidatorRegistry` | inhoudscodes per voorwaarde (`GE-C116`–`GE-C170`) |

Fouten in deze fase hebben niveau `FORMAT_ERROR` of `DATA_ERROR`, op één uitzondering na: de
controles op het GS1-bedrijfsprefix bij de AI's met een GS1-sleutel zijn louter adviserend en dragen niveau `WARNING` (zie de
[Foutenreferentie](#foutenreferentie)); een onbekend bedrijfsprefix maakt het resultaat dus
op zichzelf niet ongeldig.

---

### Fase 4 — interpretatie

Draait alleen in de modus `INTERPRETATION` en uitsluitend wanneer geen enkel element een fout uit een eerdere fase draagt. De `InterpretationEngine` verrijkt elk element met gelabelde metagegevens:

- Datums heropgemaakt als `dd/mm/jjjj`
- Ontleding van het GTIN-controlecijfer en opzoeking van het GS1-bedrijfsprefix
- Landnamen volgens ISO 3166
- Valutanamen en -symbolen volgens ISO 4217
- Gedecodeerde decimale bedragen
- HRI-fragmenten (voor mensen leesbare interpretatie)

De resultaten worden als `GS1AIInterpretation`-items aan elk `GS1AIObjectElement` gehangen.

---

## Parserconfiguratie (`ParseConfig`)

`GaiaParser` biedt precies twee startpunten:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` draait met de **standaardconfiguratie**: modus `INTERPRETATION`, datums in oplopende volgorde (`dd/mm/jjjj`) met `/` als scheidingsteken en een jaartal van vier cijfers, en **Engelse** foutmeldingen. Wilt u daar iets aan wijzigen — ook de parsemodus — bouw dan een `ParseConfig` met de vloeiende builder en gebruik de overload met twee argumenten.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Alle optie-enums staan in `GaiaConstants`.

### Opties

| Buildermethode | Enum (`GaiaConstants`) | Standaard | Effect |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Diepte van de keten — zie [Parsemodi](#parsemodi). |
| `language(...)`      | `Language`      | `ENGLISH`        | Taal van de foutmeldingen, de interpretatielabels **en** de AI-beschrijvingen. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Volgorde van de datumdelen: `LITTLE` (`dd/mm/jjjj`), `MIDDLE` (`mm/dd/jjjj`), `BIG` (`jjjj/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Teken tussen de datumdelen: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) of `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) of `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Slaat de structurele «vereist»-controle over (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Slaat de structurele «sluit uit»-controle over (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / klassenaam | geen | Code die de ruwe invoer herschrijft vóór het parseren — twee [ingebouwde modificatoren](#ingebouwde-modificatoren) plus alles wat u zelf schrijft. Zie [Invoermodificatoren](#invoermodificatoren). |

De vier datumopties werken alleen door in de opgemaakte datumreeksen die de interpretatieverrijkers produceren (in de modus `INTERPRETATION`); ze veranderen niets aan de validatie. Builderwaarden mogen worden weggelaten — elke optie die niet wordt ingesteld (of waaraan `null` wordt doorgegeven) behoudt haar standaardwaarde.

### Gelokaliseerde meldingen en labels

`language(...)` kiest de taal voor **drie** soorten voor mensen leesbare tekst: de foutmeldingen, de interpretatielabels (de `getLabel()` van elke `GS1AIInterpretation`) en de AI-beschrijvingen (de `getDescription()` van elk `GS1AIObjectElement`).

`GaiaConstants.Language` definieert **35 talen**, die de meest gesproken talen ter wereld bestrijken: Engels, Frans, Spaans, Duits, Italiaans, Portugees, Nederlands, Pools, Russisch, Oekraïens, Tsjechisch, Zweeds, Chinees, Japans, Koreaans, Arabisch, Indonesisch, Hindi, Turks, Bengaals, Urdu, Vietnamees, Nigeriaans Pidgin, Egyptisch Arabisch, Marathi, Telugu, Tamil, Kantonees, Wu, Tagalog, Perzisch, Hausa, Punjabi, Javaans en Swahili.

Stand van de vertalingen (zoals meegeleverd):
- **Interpretatielabels** — vertaald in alle talen.
- **Foutmeldingen** — vertaald in alle talen.
- **AI-beschrijvingen** — vertaald in alle talen behalve het Engels. Het Engels vormt geen aparte catalogus: het wordt rechtstreeks gelezen uit het veld `description` van de AI-vermelding in `gs1-application-identifiers.jsonld`, waarop elke AI-beschrijving uiteindelijk terugvalt.

Nigeriaans Pidgin (`NIGERIAN_PIDGIN`), een op het Engels gebaseerde creooltaal, hergebruikt bewust de Engelse tekst voor de interpretatielabels en de foutmeldingen. De AI-beschrijvingen vormen de uitzondering op die uitzondering: die zijn wél in echt Pidgin vertaald in plaats van het Engels over te nemen, omdat de catalogi met AI-beschrijvingen los van die met labels en meldingen tot stand zijn gekomen. Laat machinevertalingen door moedertaalsprekers nakijken voordat u er in productie op vertrouwt.

Elke melding, elk label en elke beschrijving die in de catalogus van een taal ontbreekt, valt terug op het Engels. Talen die van rechts naar links worden geschreven (Arabisch, Urdu, Egyptisch Arabisch, Perzisch) zijn correct als tekenreeksen opgeslagen; hun weergave van rechts naar links is een zaak van de presentatielaag.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

De interpretatielabels worden op dezelfde wijze gelokaliseerd (de waarden blijven ongewijzigd — alleen de labels):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

De AI-beschrijvingen worden op dezelfde wijze gelokaliseerd (alleen `getTitle()`, bijvoorbeeld `"GTIN"`, wordt niet gelokaliseerd):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Datumopmaak

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Invoermodificatoren

Een **invoermodificator** is code die de ruwe invoerreeks herschrijft voordat Gaia haar parseert. Modificatoren bestaan voor invoer die al vervormd binnenkomt: een scanner die het GS-scheidingsteken vervangt door een afdrukbare tijdelijke aanduiding, middleware die de payload in een leveranciersvoorvoegsel verpakt, een hostsysteem dat alles naar hoofdletters omzet. In plaats van elke reeks op elke aanroepplaats voor te bewerken (en het op één daarvan subtiel fout te doen), legt u de normalisatie één keer vast in de `ParseConfig` en laat u de parser haar toepassen.

Modificatoren draaien helemaal aan het begin van `GaiaParser.parse(...)`: vóór het verwijderen van de correlatie-ID, vóór de detectie van de AIM-symbologie-identificatie en vóór de GS1-keten. Alles verderop ziet enkel nog de herschreven reeks. **Standaard is er niets geconfigureerd**, ook de twee [ingebouwde modificatoren](#ingebouwde-modificatoren) niet — u schakelt ze uitdrukkelijk in per `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Ingebouwde modificatoren

De kern-jar bevat twee modificatoren, in `tools.pantheum.gaia.modifier.custom`. Ze dekken de twee manieren waarop een GS1-payload het vaakst vervormd binnenkomt — afgedrukte HRI-haakjes die als gegevens worden behandeld, en overtollige spaties — zodat de gangbare gevallen geen eigen klasse vergen:

| Klasse | `getName()` | Wat ze doet |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Verwijdert de HRI-haakjes rond elke AI (`(01)…(10)…`) en herstelt het FNC1-scheidingsteken dat ze impliceerden. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Verwijdert alle spaties (`0x20`) uit de AI-elementreeks. |

Het zijn gewone implementaties van `ModifierInterface`, zonder bijzondere status: ze worden geregistreerd, geordend, gerapporteerd en falen precies zoals de uwe:

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

Beide zijn toestandloos en threadveilig, zodat één instantie gedeeld kan worden, en beide zijn adresseerbaar via hun volledig gekwalificeerde klassenaam voor configuratiegestuurde opstellingen (zie [Modificatoren registreren](#modificatoren-registreren)).

#### `ModifierRemoveAIBrackets`

De voor mensen leesbare interpretatie van GS1 drukt elke AI tussen haakjes af — `(01)09521234543213(10)ABC123` — puur als drukconventie. Een scanner of middleware die is ingesteld om de HRI uit te sturen, geeft die haakjes als gegevens door, en de tokenisatie weet er niets mee aan te vangen.

De haakjes verwijderen is maar het halve werk. In de HRI markeert het openingshaakje van de *volgende* AI het einde van de vorige waarde, zodat een AI met variabele lengte in haakjesvorm geen FNC1 nodig heeft. Verwijdert u de haakjes zonder meer, dan verdwijnt die grens:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Daarom **voegt de modificator op elke grens waarvan de voorafgaande AI een variabele lengte heeft opnieuw een FNC1 in**, en herstelt zo precies wat de haakjes codeerden:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

De lengte wordt opgezocht in de eigen `AiDefinitionRegistry` van de parser, zodat elke AI met variabele lengte wordt behandeld in plaats van een handmatig ingevoerde lijst. Drie gevallen blijven bewust ongemoeid: een waarde die al op FNC1 eindigt (een bron die beide conventies uitstuurt krijgt geen tweede scheidingsteken), een code tussen haakjes die geen bekende AI is (een onbekende AI zegt niets over haar eigen lengte), en de laatste AI in de reeks.

Het herschrijven is **idempotent** — het opnieuw toepassen op de eigen uitvoer verandert niets — en is dus veilig bij een gemengde stroom waarin slechts een deel van de invoer haakjes draagt.

> **Beperking.** `(` en `)` zijn zelf geldige GS1-gegevenstekens, en het patroon is niet meer dan `\((\d{2,4})\)`. Een waarde die toevallig een getal van twee tot vier cijfers tussen haakjes bevat, zou eveneens van haar haakjes worden ontdaan. Pas dit alleen toe op een bron die de HRI-haakjesconventie gebruikt, niet op waarden met echte haakjes.

#### `ModifierRemoveSpaces`

Sommige scanners, middleware en etiketprintketens voegen overtollige spaties in een verder welgevormde elementreeks in: om een veld met vaste breedte op te vullen, om leesbare groepen te scheiden of om een lange waarde af te breken. De tokenisatie behandelt elk daarvan als gegevens, waardoor de waarde waarin de spatie staat bedorven raakt en, bij een AI met variabele lengte, alles wat erop volgt verschuift.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Alleen ASCII `0x20` wordt verwijderd. Andere witruimte blijft staan: een tab valt bijvoorbeeld buiten de GS1-codeerbare tekenset, zodat de parser die als `GE-S008` meldt in plaats van hem stilzwijgend weg te vegen.

> **Beperking.** De spatie (`0x20`) hoort bij de invariante tekenset van GS1, zodat een partijnummer of een klantartikelnummer er terecht een kan bevatten. De modificator kan een overtollige spatie niet van een echte onderscheiden; pas hem alleen toe op een bron waarvan u weet dat ze binnen haar AI-waarden geen spaties gebruikt.

#### Voorvoegsels worden overgeslagen, niet herschreven

Modificatoren draaien voordat de parser iets heeft verwijderd, zodat de ruwe invoer nog een correlatie-ID, een AIM-symbologie-identificatie en een ECI-indicator kan dragen. Beide ingebouwde modificatoren bepalen het begin van de AI-elementreeks met de eigen logica van `CorrelationIdParser` en `DataCarrierParser` van de parser, herschrijven pas vanaf dat punt en zetten het resultaat weer aan het **onaangeroerde** voorvoegsel:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

EAN/UPC-dragers waarvan de waarde tot GTIN-14 wordt aangevuld (`isRequiresGtinPadding()`) worden helemaal overgeslagen: hun payload is een zuiver numerieke barcodewaarde zonder AI-structuur, waarin haakjes noch spaties betekenis kunnen hebben.

#### Volgorde: eerst spaties, dan haakjes

Worden beide gebruikt, **registreer dan `ModifierRemoveSpaces` als eerste**. De haakjesherkenning is positiegevoelig: een `( 01 )` met spaties komt niet overeen met `\((\d{2,4})\)`, zodat de haakjes blijven staan en het scheidingsteken dat ze impliceerden nooit wordt hersteld.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Een modificator schrijven

Schrijf er zelf een wanneer geen van beide ingebouwde voldoet — de interface bestaat uit één methode.

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

Overschrijf in plaats daarvan de overload met twee argumenten wanneer het herschrijven van de parserconfiguratie afhangt:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contract:

| Regel | Detail |
|---|---|
| Toestandloos en threadveilig | Per klasse wordt één instantie in de cache bewaard en over alle parses gedeeld. |
| Publieke constructor zonder argumenten | Alleen vereist wanneer de modificator via de klassenaam wordt aangeduid. |
| `null`-invoer en lege invoer afhandelen | De parser filtert die niet weg voordat de keten draait. |
| `null` teruggeven betekent «geen wijziging» | De vorige waarde blijft behouden. Geef `input` ongewijzigd terug wanneer de modificator niet van toepassing is. |
| Liever ongewijzigd teruggeven dan een uitzondering werpen | Een modificator die een uitzondering werpt, breekt het parseren af — zie [Foutafhandeling](#foutafhandeling-bij-modificatoren). |
| `getName()` | Overschrijf deze om de naam te bepalen die in `ModifierInfo` wordt gemeld; standaard is dat de eenvoudige klassenaam. |

### Modificatoren registreren

Modificatoren draaien in de volgorde waarin ze worden toegevoegd, en elke krijgt de uitvoer van de vorige. Registreer ze per instantie, via de volledig gekwalificeerde klassenaam, of als een lijst van een van beide:

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

De [ingebouwde modificatoren](#ingebouwde-modificatoren) worden net zo aangeduid als de uwe — **altijd volledig gekwalificeerd**. Er bestaat voor hen geen opzoeking via korte naam of alias; `ModifierRegistry` herleidt elke modificator, meegeleverd of niet, via de volledige klassenaam.

De namen worden herleid door `ModifierRegistry`, die elke klasse één keer instantieert via haar constructor zonder argumenten en de instantie in de cache bewaart voor elke latere configuratie die dezelfde klasse noemt. De herleiding gebeurt **bij het bouwen van de configuratie**, zodat een naam die niet gevonden wordt, die `ModifierInterface` niet implementeert of die niet geïnstantieerd kan worden, daar een `IllegalArgumentException` werpt — en niet stilzwijgend pas tijdens het parseren. Een modificator die niet via reflectie gebouwd kan worden (bijvoorbeeld een met een geïnjecteerde dependency) kan vooraf worden geregistreerd, zodat hij toch via zijn naam adresseerbaar blijft:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Nagaan wat een modificator heeft gedaan

Zijn er modificatoren geconfigureerd, dan weerspiegelt `ParseResult.getPayload()` de **gewijzigde** invoer. Het origineel blijft bewaard in `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` meldt de `getName()` van elke modificator, standaard de eenvoudige klassenaam, maar beide ingebouwde modificatoren overschrijven die — een keten van de twee meldt dus de weergavenamen en niet de klassenamen:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` geeft `null` terug wanneer er geen modificator was geconfigureerd. Draaiden er wel modificatoren maar gaf elk de invoer ongewijzigd terug, dan is de informatie aanwezig en is `isModified()` gelijk aan `false` — in `getAppliedModifiers()` staan alleen de modificatoren die de invoer daadwerkelijk hebben gewijzigd.

### Foutafhandeling bij modificatoren

Een modificator die een uitzondering werpt, breekt het parseren af. De uitzondering wordt verpakt in een `GaiaModifierException` die de schuldige modificator noemt, en het resultaat draagt een interne fout `GE-I001` waarvan de melding die naam bevat; `getPayload()` meldt de ongewijzigde invoer. Het parseren gaat bewust **niet** verder met een half herschreven reeks: een normalisatiestap die stilzwijgend faalt, zou resultaten opleveren die geldig lijken maar uit de verkeerde invoer zijn afgeleid.

---

## Parsemodi

Elke modus noemt de diepste [fase van de keten](#verwerkingsketen-van-de-parser) die hij uitvoert; alle voorafgaande fasen draaien eveneens.

| Modus | Loopt tot | Beantwoordt de vraag |
|---|---|---|
| `DATA_CARRIER` | Fase 1 (invoerrouting) | Welke symbologie heeft dit gedragen? |
| `SYNTAX` | Fase 2 (syntaxis) | Zijn de AI-codes en lengtes welgevormd? |
| `CONTENT` | Fase 3 (inhoud) | Zijn de waarden geldige GS1-gegevens? |
| `INTERPRETATION` | Fase 4 (interpretatie) | Wat betekenen de waarden? |

### Modus DATA_CARRIER

Stopt na fase 1 — valideert de AIM-symbologie-identificatie en bepaalt de symbologie, maar betreedt de AI-parseketen niet. Handig om symbologieën te bepalen en verwerking te routeren zonder de kosten van een volledige validatie.

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

**Gebruik dit wanneer:** uw toepassing het barcodetype moet bepalen voordat ze beslist hoe de payload te verwerken — bijvoorbeeld om naar verschillende handlers te routeren voor 1D- versus 2D-symbologieën. Geef voor die routing de voorkeur aan het getypeerde [`DataCarrierType`](#datacarrierentry-en-datacarriertype) (`getDataCarrier().getDataCarrierType()`) boven een tekenreeksvergelijking op `getName()`.

---

### Modus SYNTAX

Stopt na fase 2. Handig voor een structurele voorselectie zonder de kosten van de inhoudsvalidatie.

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

**Gebruik dit wanneer:** u wilt nagaan of de AI-codes en gegevenslengtes welgevormd zijn voordat u zich aan een volledige validatie waagt, of wanneer u grote volumes verwerkt waarbij inhoudsfouten zeldzaam zijn.

---

### Modus CONTENT

Stopt na fase 3.

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

> De meeste AI's mogen niet alleen staan: de AI's `10` (BATCH/LOT), `17` (USE BY or EXPIRY) en `21`
> (SERIAL) *vereisen* elk een identificatiesleutel zoals AI `01` in dezelfde elementreeks;
> laat u de GTIN hierboven weg, dan zou dat al in fase 2 mislukken met `GE-S005`, zonder de
> inhoudsvalidatie ook maar te bereiken. Stel `skipRequiresCheck(true)` in op de
> `ParseConfig` om fragmenten te parseren die hun begeleidende AI's bewust weglaten.

**Gebruik dit wanneer:** u moet weten of een gescande waarde volledig GS1-conform is voordat u haar in een bedrijfsproces gebruikt, zonder de extra kosten van de interpretatieverrijking.

---

### Modus INTERPRETATION (standaard)

Voert de volledige keten uit tot en met fase 4. Dit is de standaard bij een aanroep van `parse(String)` zonder modusargument. Alleen elementen die de inhoudsvalidatie foutloos hebben doorstaan, worden verrijkt.

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

**Voorbeelduitvoer:**
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

**Voorbeeld met een geldbedrag (AI 3932 — prijs met ISO-valutacode):**
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

**Gebruik dit wanneer:** u presentatielagen, hulpmiddelen voor etiketcontrole of welke interface dan ook bouwt die een leesbare uitsplitsing van de AI-waarden nodig heeft.

---

## Correlatie-ID

Sommige workflows plaatsen vóór de ruwe GS1-invoer een eigen correlatie-identificatie van 8 cijfers, zodat scangebeurtenissen aan een sessie of transactie gekoppeld kunnen worden. Het formaat is:

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

De tilde (`~`) is het scheidingsteken. Die maakt **geen** deel uit van de GS1-inhoud — hij wordt verwijderd voordat enig GS1-parseren begint.

### Detectieregels

Het voorvoegsel wordt herkend wanneer de invoer begint met precies 8 decimale ASCII-cijfers (`0`–`9`), onmiddellijk gevolgd door `~`. Is het negende teken geen `~`, of is een van de eerste 8 tekens geen cijfer, dan wordt de invoer als gewone GS1-inhoud behandeld, zonder correlatievoorvoegsel.

### De correlatie-ID opvragen

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

### Combinatie met een AIM-symbologie-identificatie

Een correlatievoorvoegsel mag vóór een AIM-symbologie-identificatie staan. De parser gaat daar transparant mee om:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Implementatieklasse:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Een **GS1 Digital Link** codeert een of meer AI-waarden rechtstreeks in de structuur van een HTTP(S)-URL, waardoor identificaties van fysieke producten via het web opgelost kunnen worden. GAIA implementeert de *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) voor **niet-gecomprimeerde** URI's.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` herkent Digital Link-URI's automatisch: elke invoer die met `http://` of `https://` begint, wordt naar `GS1DLParser` geleid, die dezelfde fasen inhoud en interpretatie doorloopt als de keten voor elementreeksen.

### Opbouw van de URI en rollen van de AI's

Elke AI in een Digital Link-URI vervult een van drie rollen, beschikbaar op elk `GS1AIObjectElement` via `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Rol | Plaats | Voorbeeld |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Eerste paar `/ai/waarde` van het pad (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Volgende padparen, geordend volgens de primaire sleutel (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Queryparameters met volledig numerieke sleutels (§4.10) | `?17=271231` |

Afgedwongen structuurregels (`DLPathRules`):
- Precies **één** primaire identificatiesleutel in het pad; extra sleutels moeten als gegevensattributen in de query worden gecodeerd.
- Sleutelkwalificatoren moeten door de primaire sleutel worden toegelaten en in de voorgeschreven volgorde verschijnen. Optionele kwalificatoren mogen worden weggelaten, maar de kwalificatoren die er *wel* zijn, moeten de vaste volgorde aanhouden — zie [Volgorde van de kwalificatoren](#volgorde-van-de-kwalificatoren).
- Vóór de primaire sleutel mogen willekeurige eigen padsegmenten staan (bijvoorbeeld `/products/au/01/...`); haal ze op via `getDigitalLinkInfo().getCustomPathStem()`.
- Niet-numerieke querysleutels (`linkType`, `context`, uitbreidingsparameters zoals `23P`) worden genegeerd; volledig numerieke sleutels moeten geldige AI's zijn die als `validAsDataAttribute` zijn gemarkeerd.
- Procentgecodeerde waardetekens worden gedecodeerd; de AI's `(03)` en `(8014)` zijn niet toegestaan.

De primaire sleutels en hun toelaatbare reeksen kwalificatoren zijn **gegevensgestuurd** afgeleid uit de AI-definities — de vlag `gs1DigitalLinkPrimaryKey` en het attribuut `gs1DigitalLinkQualifiers` — in plaats van vast ingebouwd.

Elke structuurschending, en elke invoer die geen URL is, levert een structurele Digital Link-fout op (`GE-L001`–`GE-L014`, één code per voorwaarde). De ontlede URL-metagegevens (`scheme`, `domain`, `path`, `customPathStem`, `query` en het `java.net.URL`-object) blijven ook bij structurele fouten beschikbaar via `getDigitalLinkInfo()`.

### Volgorde van de kwalificatoren

Voor elke primaire sleutel somt `gs1DigitalLinkQualifiers` een of meer **geordende** reeksen kwalificatoren op. Binnen een reeks is een AI tussen vierkante haken **optioneel** en een AI zonder haken **verplicht**, naar het voorbeeld van de notatie `[cpv-comp]` uit de ABNF van §4.9. De reeksen van eenzelfde primaire sleutel zijn elkaar uitsluitende alternatieven.

De GTIN (`01`) bijvoorbeeld definieert twee reeksen:

| Pad | Reeks | Betekenis |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — elk optioneel, maar in deze vaste volgorde |
| upui-path | `235` | TPX (verplicht); GTIN + TPX = UPUI |

Zo is `/01/09506000134352/10/LOT-ABC/21/SER` geldig (LOT vóór SER, CPV weggelaten), wordt `/01/.../21/SER/10/LOT-ABC` **afgewezen** (verkeerde volgorde), en hoort `/01/09506000134352/235/2ABC456` bij de upui-path. De volgordecontrole is een deelreeksvergelijking die de volgorde behoudt: optionele AI's mogen dus worden overgeslagen, maar nooit omgewisseld.

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

**Implementatieklasse:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Werken met de resultaten

### ParseResult

Het resultaat op het hoogste niveau dat `GaiaParser.parse()` teruggeeft.

| Methode | Geeft terug | Beschrijving |
|---|---|---|
| `isValid()` | `boolean` | `true` als er op geen enkel niveau fouten zijn. Waarschuwingen doen niets af aan de geldigheid. Altijd `true` wanneer `getAiObject()` `null` is. |
| `getPayload()` | `String` | De invoerreeks na het verwijderen van het correlatievoorvoegsel — en nadat eventuele [invoermodificatoren](#invoermodificatoren) haar hebben herschreven. |
| `getPayloadContent()` | `String` | De payload zonder AIM-symbologie-identificatie en zonder ECI-voorvoegsel. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (een gegevensdrager die als niet-GS1 is afgewezen, bijvoorbeeld een Code 39-drager `]A0`) of `UNABLE_TO_DETERMINE_CONTENT` (wanneer `aiObject` `null` is, bijvoorbeeld in de modus `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | De geconfigureerde ketendiepte (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | De diepste fase die het parseren werkelijk heeft bereikt — zie hieronder. |
| `isParseComplete()` | `boolean` | `true` als het parseren de gevraagde diepte heeft bereikt (`achieved == requested`). Los van `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Alle herkende AI's. `null` in de modus `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Alle fouten van een ander niveau dan WARNING (op objectniveau en op alle elementniveaus). |
| `getWarnings()` | `List<GaiaError>` | Alle meldingen van niveau WARNING (op objectniveau en op alle elementniveaus). |
| `hasWarnings()` | `boolean` | `true` als er meldingen van niveau WARNING zijn gegeven. |
| `getIssues()` | `List<GaiaError>` | Fouten en waarschuwingen samen. |
| `hasDataCarrier()` | `boolean` | `true` als er een AIM-symbologie-identificatie is herkend. |
| `getDataCarrier()` | `DataCarrierEntry` | Symbologiemetagegevens, of `null` als er geen drager is bepaald. |
| `hasEci()` | `boolean` | `true` als er een ECI-indicator uit de payload is verwijderd. |
| `getEci()` | `EciEntry` | ECI-coderingsmetagegevens, of `null`. |
| `hasCorrelationId()` | `boolean` | `true` als in de oorspronkelijke invoer een correlatievoorvoegsel `DDDDDDDD~` aanwezig was. |
| `getCorrelationInfo()` | `CorrelationInfo` | De uitgelezen correlatie-ID, of `null` als die er niet was. |
| `isInputModified()` | `boolean` | `true` als een [invoermodificator](#invoermodificatoren) de invoer heeft gewijzigd. |
| `getModifierInfo()` | `ModifierInfo` | Wat de modificatorketen heeft gedaan — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` als er geen modificator was geconfigureerd. |
| `getTiming()` | `ProcessingTiming` | Werkelijke tijdmeting van het parseren — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` als het niet door `GaiaParser` is voortgebracht. |
| `getVersion()` | `String` | De versie van de bibliotheek die het resultaat heeft voortgebracht. |

#### Gevraagde tegenover bereikte parsemodus

De keten doorloopt de ladder **SYNTAX → CONTENT → INTERPRETATION** en stopt vroegtijdig bij fouten, zodat de werkelijk *bereikte* modus ondieper kan zijn dan de *gevraagde*. `getAchievedParseMode()` geeft aan hoe ver hij is gekomen:

| Gevraagd | Wat er gebeurt | Bereikt | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | een **syntaxis- of structuurfout** stopt het parseren na het tokeniseren | `SYNTAX` | `false` |
| `INTERPRETATION` | een **inhoudsfout** (verkeerd formaat / verkeerd controlecijfer) verhindert de verrijking | `CONTENT` | `false` |
| `CONTENT` | de inhoudsfase loopt altijd tot het einde (fouten worden aangetekend, niet fataal) | `CONTENT` | `true` |
| willekeurig (foutloze invoer) | de keten bereikt de gevraagde diepte | = gevraagd | `true` |
| `DATA_CARRIER` | drager gevalideerd; geen AI-inhoud geparseerd | `DATA_CARRIER` | `true` |
| willekeurig | de gegevensdrager wordt vóór het AI-parseren afgewezen (bijvoorbeeld een niet-GS1-drager `]A0`) | `SYNTAX` | `false` |

`isParseComplete()` staat los van `isValid()`: een `CONTENT`-parse van een GTIN met een verkeerd controlecijfer is **volledig** (de inhoudsfase is gedraaid) en tegelijk **ongeldig** (het controlecijfer klopte niet). Vraag met `isParseComplete()` «is de keten zo diep gegaan als ik heb gevraagd?» en met `isValid()` «zijn de gegevens welgevormd?».

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

De verzameling herkende AI-elementen.

| Methode | Beschrijving |
|---|---|
| `getAis()` | Alle `GS1AIObjectElement`-instanties, in de volgorde van de invoer. |
| `get(String aiCode)` | Eerste element dat overeenkomt met de opgegeven AI-code, of `null`. |
| `contains(String aiCode)` | `true` als er een AI met die code aanwezig is. |
| `size()` | Aantal herkende AI's. |
| `isValid()` | `true` als er geen fouten op objectniveau zijn en geen enkel element fouten draagt. |
| `toHriString()` | HRI-reeks, bijvoorbeeld `(01)09506000134352 (17)261231`. |
| `toElementString()` | Ruwe elementreeks — zonder haakjes, met een FNC1 na elk element met variabele lengte — bijvoorbeeld `010950600013435210LOT-ABC<GS>17271231`. Geeft `null` terug als `isValid()` `false` is. |
| `getContentType()` | `GS1_DIGITAL_LINK` wanneer `hasDigitalLink()` waar is, anders `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` als de invoer een GS1 Digital Link-URI met een primaire identificatiesleutel was. Een welgevormde URL zonder primaire sleutel biedt nog steeds `getDigitalLinkInfo()`, maar geeft hier `false` terug. |
| `getCanonicalDigitalLink()` | De canonieke GS1 Digital Link-URI (§4.12) op `https://id.gs1.org` — primaire sleutel en kwalificatoren als padsegmenten, gegevensattributen als queryparameters gesorteerd op AI-sleutel — of `null` als er geen primaire sleutel is. |
| `getDigitalLinkInfo()` | Metagegevens van de URI-ontleding (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), of `null` als het geen Digital Link is. |
| `getAllErrors()` | Fouten op objectniveau + alle elementfouten (anders dan WARNING). |
| `getAllWarnings()` | Waarschuwingen op objectniveau + alle elementwaarschuwingen. |
| `getAllIssues()` | Alles samen. |

---

### GS1AIObjectElement

Eén herkende AI-instantie.

| Methode | Beschrijving |
|---|---|
| `getAi()` | AI-code, bijvoorbeeld `"01"`, `"3102"`. |
| `getTitle()` | GS1-gegevenstitel, bijvoorbeeld `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Volledige GS1-beschrijving van de AI, **gelokaliseerd naar de parsetaal** (bijvoorbeeld `"Global Trade Item Number (GTIN)"` in het Engels). Valt terug op de Engelse tekst uit de AI-definitie als er geen vertaling is. |
| `getFormatString()` | Formaatbeschrijving die de AI *en* haar gegevens omvat, bijvoorbeeld `"N2+N14"` voor AI `01`, `"N2+X..20"` voor AI `10`, `"N4+N3+N..15"` voor AI `3932`. |
| `getValue()` | Ruwe gegevenswaarde, uit de elementreeks gehaald. |
| `isFixedLength()` | `true` als de AI een vaste gegevenslengte heeft. |
| `getPosition()` | Tekenpositie (nulgebaseerd) in de oorspronkelijke invoer. |
| `getGS1ComponentValues()` | Waardestukken per component (bij AI's met meerdere componenten). |
| `getErrors()` | Fouten op elementniveau, anders dan WARNING. |
| `getWarnings()` | Meldingen van niveau WARNING op het element. |
| `getIssues()` | Fouten en waarschuwingen van het element samen. |
| `hasErrors()` | `true` als er fouten anders dan WARNING zijn gekoppeld. |
| `hasWarnings()` | `true` als er meldingen van niveau WARNING zijn gekoppeld. |
| `getInterpretations()` | `GS1AIInterpretation`-items (gevuld in de modus INTERPRETATION). |
| `getInterpretation(String type)` | Eerste interpretatie die overeenkomt met de opgegeven typesleutel uit `GS1Constants_Enricher`, of `null`. |
| `getDigitalLinkAIType()` | De Digital Link-rol van het element (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), of `null` bij invoer in de vorm van een elementreeks. |
| `hasDigitalLinkAIType()` | `true` als er een Digital Link-rol is toegekend. |

---

### GaiaError

Een onveranderlijke validatiefout of melding.

| Methode | Beschrijving |
|---|---|
| `getId()` | Catalogusidentificatie, bijvoorbeeld `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` of `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` of `INTERNAL`. |
| `getCode()` | Korte, machineleesbare code. |
| `getAi()` | AI-code die de fout heeft veroorzaakt, of `null` bij fouten op objectniveau. |
| `getMessage()` | Leesbare melding, met ingevulde waarden. |
| `getPosition()` | Tekenpositie (nulgebaseerd) in de oorspronkelijke invoer. |

---

### GS1AIInterpretation

Eén gelabeld interpretatiefragment, in de modus `INTERPRETATION` aan een `GS1AIObjectElement` gehangen.

| Methode | Beschrijving |
|---|---|
| `getType()` | Machineleesbare typesleutel, bijvoorbeeld `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Gelijk in alle talen. |
| `getLabel()` | Voor mensen leesbaar label, **gelokaliseerd naar de parsetaal** (bijvoorbeeld `"Date"` / `"GS1 company prefix"` in het Engels). |
| `getValue()` | Uitgelezen of verrijkte waarde, bijvoorbeeld `"31/12/2026"`, `"9506000"`. Wordt niet gelokaliseerd. |

---

### DataCarrierEntry en DataCarrierType

Draagt de invoer een AIM-symbologie-identificatie, dan geeft `ParseResult.getDataCarrier()` een `DataCarrierEntry` terug die het symbool beschrijft dat de gegevens heeft gedragen. Die vermelding is het concrete registeritem voor de herkende AIM-identificatie; `DataCarrierType` is de bij compilatie bekende enum waartoe zij behoort.

#### DataCarrierEntry

De metagegevens van één herkende AIM-symbologie-identificatie (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Methode | Beschrijving |
|---|---|
| `getAimCodeId()` | De herkende AIM-symbologie-identificatie, bijvoorbeeld `"]C1"`. |
| `getName()` | Leesbare naam van het concrete symbool, bijvoorbeeld `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Uitvoeriger beschrijving van de drager. |
| `getType()` | Het structurele type van de drager als tekenreeks (weerspiegelt `getDataCarrierType().getCategory()`). |
| `getStandard()` | De symbologiestandaard, waar die is vastgelegd. |
| `getDataCarrierType()` | Het getypeerde `DataCarrierType` van deze vermelding — verkieslijk voor programmatische routing. |
| `isGs1Capable()` | `true` als de drager GS1-gegevens kan bevatten (AI-elementreeksen en/of Digital Link). |
| `isGs1AICapable()` | `true` als de drager GS1-AI-elementreeksen kan bevatten. |
| `isGs1DigitalLinkCapable()` | `true` als de drager een GS1 Digital Link-URI kan bevatten. |
| `isEciCapable()` | `true` als de drager een ECI-indicator ondersteunt. |
| `isRequiresGtinPadding()` | `true` voor EAN/UPC/ITF-dragers waarvan de numerieke waarde vóór het AI-parseren tot GTIN-14 wordt aangevuld. |

#### DataCarrierType

Een bij compilatie bekende enum van gegevensdragertypen, geïndexeerd op de AIM-symbologie-identificatie zoals toegekend in ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Het teken na `]` (het *codeteken*) kiest de familie; de meeste families vallen samen met één constante die alle modificatoren dekt (`ITF` dekt `]I0`–`]I2`; `EAN_UPC` dekt EAN-13, UPC-A, UPC-E en EAN-8). Waar GS1 een modificator voor AI-gegevens reserveert, vormt die variant een eigen constante — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — onderscheiden van hun gewone tegenhanger. Ontbreekt een AIM-identificatie, of duidt zij een onbekende drager aan, dan is het type `UNKNOWN`.

| Methode | Beschrijving |
|---|---|
| `getCategory()` | De ruime `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` of `OTHER`. |
| `getCodeChar()` | Het AIM-codeteken dat de familie aanduidt, bijvoorbeeld `"Q"` voor QR Code; `null` bij `UNKNOWN`. |
| `getDisplayName()` | Leesbare naam van het *type* (kan ruimer zijn dan `DataCarrierEntry.getName()` — bijvoorbeeld `"EAN-13 / UPC-A / UPC-E / EAN-8"` tegenover `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` voor de constanten die altijd GS1-AI-gegevens aanduiden: de vier door GS1 gereserveerde varianten (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) plus `GS1_DATABAR`, dat van nature GS1 is omdat elke `]e`-modificator een GS1 DataBar aanduidt. Enger dan `DataCarrierEntry.isGs1AICapable()` — ook een gewone `QR_CODE` kan GS1-AI-gegevens dragen. |
| `static forAimCodeId(String)` | Leidt een type rechtstreeks af uit een AIM-identificatie (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); geeft `UNKNOWN` terug bij een ontbrekende, misvormde of onbekende identificatie. |

Routeren op type in plaats van op naam — bijvoorbeeld om lineaire symbolen (Code 128) van 2D-symbolen (QR / Data Matrix) te scheiden:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` dekt alleen de matrix- en puntsymbolen; de gestapeld-lineaire dragers (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) vallen onder `STACKED_LINEAR`, ook al worden ze
gewoonlijk «2D»-barcodes genoemd. Wilt u beide als één groep behandelen — bijvoorbeeld om te beslissen
of er een imager nodig is in plaats van een laserscanner — test dan op een van beide categorieën.

> Voor het bepalen van het type moet de AIM-symbologie-identificatie in de scan aanwezig zijn; zonder die identificatie is `getDataCarrier()` `null` en is het type `UNKNOWN`. Stel de scanner zo in dat hij het AIM-identificatievoorvoegsel meestuurt.

---

## Foutenreferentie

| Code | Niveau | Fase | Betekenis |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Onbekend AI-voorvoegsel — gegevenslengte niet te bepalen |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Invoer te kort om een volledige AI-code te lezen |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Afgekapte waarde — minder tekens dan de AI vereist |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Dubbele toepassingsidentificatie in de elementreeks |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Vereiste AI-afhankelijkheid ontbreekt |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Uitgesloten AI-combinatie — twee AI's die niet samen mogen voorkomen |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Onverwachte fout bij het tokeniseren |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Teken buiten de GS1-codeerbare tekenset in de elementreeks |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Vereist FNC1-scheidingsteken ontbreekt na een AI met variabele lengte |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Overtollige gegevens voorbij het maximum van alle componenten |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | FNC1-scheidingsteken na een AI met vaste lengte op een tussenliggende positie |
| `GE-W002` | WARNING | SYNTAX | FNC1 aan het einde van de elementreeks (louter een melding) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Structuurschendingen in een Digital Link-URI — één code per voorwaarde (misvormde URI, schema, host, volgorde van de kwalificatoren, verboden AI, geen primaire sleutel (`GE-L013`), meerdere primaire sleutels (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | De waarde voldoet niet aan de reguliere expressie van de AI |
| `GE-C003` | DATA_ERROR | CONTENT | Validatie van het controlecijfer mislukt |
| `GE-C004` | DATA_ERROR | CONTENT | Validatie van het paar controletekens mislukt |
| `GE-C005` | FORMAT_ERROR | CONTENT | De waarde van een component bevat een teken buiten de toegestane tekenset |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Formaatfouten in componenten — één code per validatievoorwaarde (zie `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Fouten in de eigen semantische validatie — één code per validatievoorwaarde (zie `content/validator/`). **Uitzonderingen:** de 14 hieronder genoemde controles op het GS1-bedrijfsprefix dragen niveau `WARNING`, en `GE-C168` (onbekende numerieke landcode volgens ISO 3166-1) draagt `FORMAT_ERROR`. |
| Controles op het GS1-bedrijfsprefix | WARNING | CONTENT | De sleutel begint niet met een bekend GS1-bedrijfsprefix, bij de AI's met een GS1-sleutel — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Louter een melding — zonder invloed op de geldigheid. |
| `GE-C169` | DATA_ERROR | CONTENT | IMEI-controlecijfer (Luhn) mislukt bij AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | EID-controlecijfer (Luhn) mislukt bij AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Onbekende AIM-symbologie-identificatie |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Drager herkend, maar ondersteunt noch GS1-AI-elementreeksen noch Digital Link-URI's |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Onverwachte interne fout |

> **Bekend gebrek in de weergave van meldingen.** De catalogussjablonen zetten ingevulde
> waarden tussen verdubbelde apostroffen naar het model van MessageFormat (`''{value}''`), maar
> `ErrorRegistry` vult in met een eenvoudige `String.replace`, zodat de verdubbeling tot in
> `getMessage()` blijft staan — u ziet momenteel `value ''09506000134351''` waar de in deze
> handleiding aangehaalde meldingsteksten `value '09506000134351'` tonen. Het treft elke melding
> die een waarde aanhaalt, in alle 35 taalcatalogi. Ontleed foutmeldingen niet;
> vergelijk op `getId()` / `getCode()`.

---

## Threadveiligheid

`GaiaParser` is threadveilig zodra hij is aangemaakt. Eén instantie mag over threads heen worden gedeeld en gelijktijdig worden gebruikt. Het aanbevolen patroon is één instantie aan te maken bij het opstarten van de toepassing en die te hergebruiken:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` is onveranderlijk en even veilig om te delen. De enige verplichting inzake threadveiligheid die de bibliotheek u niet uit handen kan nemen, betreft de [invoermodificatoren](#invoermodificatoren): van elke modificator wordt één instantie in de cache bewaard en over alle gelijktijdige parses gedeeld, zodat implementaties toestandloos moeten zijn.

---

## Bijlage A — AI-tekenreeksconstanten

`GS1Constants_AICodes` (in het package `tools.pantheum.gaia.gs1.constants`) declareert een `String`-constante voor elke toepassingsidentificatie die GAIA herkent. Gebruik deze constanten in plaats van AI-codes als tekenreeks hard in te typen:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Elke constante bevat de tekstvorm van de AI-code (bijvoorbeeld `AI_01_GTIN = "01"`).

### Identificatie en serialisatie

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Serienummer verzendcontainer (SSCC). |
| `01` | `AI_01_GTIN` | Wereldwijd Artikelnummer (GTIN). |
| `02` | `AI_02_CONTENT` | Wereldwijd Artikelnummer (GTIN) van de bevatte handelseenheden. |
| `03` | `AI_03_MTO_GTIN` | Identificatie van een op bestelling gemaakte handelseenheid (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Partij- of lotnummer. |
| `20` | `AI_20_VARIANT` | Interne productvariant. |
| `21` | `AI_21_SERIAL` | Serienummer. |
| `22` | `AI_22_CPV` | Consumentenproductvariant. |
| `235` | `AI_235_TPX` | Door derden beheerde, geserialiseerde uitbreiding van het Wereldwijd Artikelnummer (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Aanvullende productidentificatie toegekend door de fabrikant. |
| `241` | `AI_241_CUST_PART_NO` | Onderdeelnummer van de klant. |
| `242` | `AI_242_MTO_VARIANT` | Variatienummer op bestelling gemaakt. |
| `243` | `AI_243_PCN` | Verpakkingscomponentnummer. |
| `250` | `AI_250_SECONDARY_SERIAL` | Secundair serienummer. |
| `251` | `AI_251_REF_TO_SOURCE` | Referentie naar bronentiteit. |
| `253` | `AI_253_GDTI` | Globale Documenttype-identificatie (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Uitbreidingscomponent van het Wereldwijd Locatienummer (GLN). |
| `255` | `AI_255_GCN` | Wereldwijd couponnummer (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variabel aantal artikelen (handelseenheid met variabele maat). |
| `37` | `AI_37_COUNT` | Aantal handelseenheden of stuks van handelseenheden in een logistieke eenheid. |

### Datums en tijden

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Productiedatum (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Vervaldatum (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Verpakkingsdatum (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Minimale houdbaarheidsdatum (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Uiterste verkoopdatum (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Uiterste gebruiksdatum (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Datum en tijd niet vóór levering (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Datum en tijd niet na levering (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Vrijgavedatum (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Vervaldatum en -tijd (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Datum van eerste invriezing (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Oogstdatum (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Testen-voor-datum (YYMMDD[hhmm]). |

### Hoeveelheid en maat — variabele maat (metrisch)

De AI-families van 4 cijfers `310n`–`369n` coderen hoeveelheden met variabele maat. Het derde cijfer kiest het maattype; het **vierde cijfer** (`n`, 0–5) is het aantal impliciete decimalen — zo betekent `AI_3102_NET_WEIGHT_KG` nettogewicht in kg met 2 decimalen.

| Familie | Constantepatroon (`n` = decimalencijfer) | Beschrijving |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Nettogewicht, kilogram (handelseenheid met variabele maat). |
| `311n` | `AI_311n_LENGTH_M` | Lengte of eerste afmeting, meter (handelseenheid met variabele maat). |
| `312n` | `AI_312n_WIDTH_M` | Breedte, diameter of tweede afmeting, meter (handelseenheid met variabele maat). |
| `313n` | `AI_313n_HEIGHT_M` | Diepte, dikte, hoogte of derde afmeting, meter (handelseenheid met variabele maat). |
| `314n` | `AI_314n_AREA_M` | Oppervlakte, vierkante meter (handelseenheid met variabele maat). |
| `315n` | `AI_315n_NET_VOLUME_L` | Nettovolume, liter (handelseenheid met variabele maat). |
| `316n` | `AI_316n_NET_VOLUME_M` | Nettovolume, kubieke meter (handelseenheid met variabele maat). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistiek gewicht, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Lengte of eerste afmeting, meter. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Breedte, diameter of tweede afmeting, meter. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Diepte, dikte, hoogte of derde afmeting, meter. |
| `334n` | `AI_334n_AREA_M_LOG` | Oppervlakte, vierkante meter. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistiek volume, liter. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistiek volume, kubieke meter. |
| `337n` | `AI_337n_KG_PER_M` | Kilogram per vierkante meter. |

### Hoeveelheid en maat — variabele maat (imperiaal / VS)

| Familie | Constantepatroon (`n` = decimalencijfer) | Beschrijving |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Nettogewicht, pond (handelseenheid met variabele maat). |
| `321n` | `AI_321n_LENGTH_IN` | Lengte of eerste afmeting, inch (handelseenheid met variabele maat). |
| `322n` | `AI_322n_LENGTH_FT` | Lengte of eerste afmeting, voet (handelseenheid met variabele maat). |
| `323n` | `AI_323n_LENGTH_YD` | Lengte of eerste afmeting, yard (handelseenheid met variabele maat). |
| `324n` | `AI_324n_WIDTH_IN` | Breedte, diameter of tweede afmeting, inch (handelseenheid met variabele maat). |
| `325n` | `AI_325n_WIDTH_FT` | Breedte, diameter of tweede afmeting, voet (handelseenheid met variabele maat). |
| `326n` | `AI_326n_WIDTH_YD` | Breedte, diameter of tweede afmeting, yard (handelseenheid met variabele maat). |
| `327n` | `AI_327n_HEIGHT_IN` | Diepte, dikte, hoogte of derde afmeting, inch (handelseenheid met variabele maat). |
| `328n` | `AI_328n_HEIGHT_FT` | Diepte, dikte, hoogte of derde afmeting, voet (handelseenheid met variabele maat). |
| `329n` | `AI_329n_HEIGHT_YD` | Diepte, dikte, hoogte of derde afmeting, yard (handelseenheid met variabele maat). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistiek gewicht, pond. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Lengte of eerste afmeting, inch. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Lengte of eerste afmeting, voet. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Lengte of eerste afmeting, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Breedte, diameter of tweede afmeting, inch. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Breedte, diameter of tweede afmeting, voet. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Breedte, diameter of tweede afmeting, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Diepte, dikte, hoogte of derde afmeting, inch. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Diepte, dikte, hoogte of derde afmeting, voet. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Diepte, dikte, hoogte of derde afmeting, yard. |
| `350n` | `AI_350n_AREA_IN` | Oppervlakte, vierkante inch (handelseenheid met variabele maat). |
| `351n` | `AI_351n_AREA_FT` | Oppervlakte, vierkante voet (handelseenheid met variabele maat). |
| `352n` | `AI_352n_AREA_YD` | Oppervlakte, vierkante yard (handelseenheid met variabele maat). |
| `353n` | `AI_353n_AREA_IN_LOG` | Oppervlakte, vierkante inch. |
| `354n` | `AI_354n_AREA_FT_LOG` | Oppervlakte, vierkante voet. |
| `355n` | `AI_355n_AREA_YD_LOG` | Oppervlakte, vierkante yard. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Nettogewicht, troy ounce (handelseenheid met variabele maat). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Nettogewicht (of volume), ounce (handelseenheid met variabele maat). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Nettovolume, quart (handelseenheid met variabele maat). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Nettovolume, Amerikaanse gallon (handelseenheid met variabele maat). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistiek volume, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistiek volume, Amerikaanse gallon. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Nettovolume, kubieke inch (handelseenheid met variabele maat). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Nettovolume, kubieke voet (handelseenheid met variabele maat). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Nettovolume, kubieke yard (handelseenheid met variabele maat). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistiek volume, kubieke inch. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistiek volume, kubieke voet. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistiek volume, kubieke yard. |

### Prijzen en geldbedragen

Het vierde cijfer (`n`) codeert het aantal impliciete decimalen. Het toegestane bereik
verschilt per familie — zie de kolom `n`.

| Familie | Constantepatroon (`n` = decimalencijfer) | `n` | Beschrijving |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Toepasselijk verschuldigd bedrag of couponwaarde, lokale valuta. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Toepasselijk verschuldigd bedrag met ISO-valutacode. |
| `392n` | `AI_392n_PRICE` | 0–9 | Toepasselijk verschuldigd bedrag, één monetair gebied (handelseenheid met variabele maat). |
| `393n` | `AI_393n_PRICE` | 0–9 | Toepasselijk verschuldigd bedrag met ISO-valutacode (handelseenheid met variabele maat). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Kortingspercentage van een coupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Verschuldigd bedrag per meeteenheid, één monetair gebied (handelseenheid met variabele maat). |

### Locatie en verzending

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Inkoopordernummer van de klant. |
| `401` | `AI_401_GINC` | Globaal identificatienummer voor zending (GINC). |
| `402` | `AI_402_GSIN` | Globaal identificatienummer voor verzending (GSIN). |
| `403` | `AI_403_ROUTE` | Routeringscode. |
| `410` | `AI_410_SHIP_TO_LOC` | Wereldwijd Locatienummer (GLN) van verzenden naar / afleveren aan. |
| `411` | `AI_411_BILL_TO` | Wereldwijd Locatienummer (GLN) van factureren aan. |
| `412` | `AI_412_PURCHASE_FROM` | Wereldwijd Locatienummer (GLN) van gekocht bij. |
| `413` | `AI_413_SHIP_FOR_LOC` | Wereldwijd Locatienummer (GLN) van verzenden voor / afleveren voor - doorsturen naar. |
| `414` | `AI_414_LOC_NO` | Identificatie van een fysieke locatie - Wereldwijd Locatienummer (GLN). |
| `415` | `AI_415_PAY_TO` | Wereldwijd Locatienummer (GLN) van de factureringspartij. |
| `416` | `AI_416_PROD_SERV_LOC` | Wereldwijd Locatienummer (GLN) van de productie- of servicelocatie. |
| `417` | `AI_417_PARTY` | Wereldwijd Locatienummer (GLN) van de partij. |
| `420` | `AI_420_SHIP_TO_POST` | Postcode van verzenden naar / afleveren aan binnen één postale autoriteit. |
| `421` | `AI_421_SHIP_TO_POST` | Postcode van verzenden naar / afleveren aan met ISO-landcode. |
| `422` | `AI_422_ORIGIN` | Land van oorsprong van een handelseenheid. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Land van eerste verwerking. |
| `424` | `AI_424_COUNTRY_PROCESS` | Land van verwerking. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Land van demontage. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Land dat de volledige procesketen omvat. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Landsonderdeel van oorsprong. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Bedrijfsnaam van verzenden naar / afleveren aan. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Contactpersoon van verzenden naar / afleveren aan. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Adresregel 1 van verzenden naar / afleveren aan. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Adresregel 2 van verzenden naar / afleveren aan. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Wijk van verzenden naar / afleveren aan. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Plaats van verzenden naar / afleveren aan. |
| `4306` | `AI_4306_SHIP_TO_REG` | Regio van verzenden naar / afleveren aan. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Landcode van verzenden naar / afleveren aan. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Telefoonnummer van verzenden naar / afleveren aan. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Geolocatie van verzenden naar / afleveren aan. |
| `4310` | `AI_4310_RTN_TO_COMP` | Bedrijfsnaam van retouradres. |
| `4311` | `AI_4311_RTN_TO_NAME` | Contactpersoon van retouradres. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Adresregel 1 van retouradres. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Adresregel 2 van retouradres. |
| `4314` | `AI_4314_RTN_TO_SUB` | Wijk van retouradres. |
| `4315` | `AI_4315_RTN_TO_LOC` | Plaats van retouradres. |
| `4316` | `AI_4316_RTN_TO_REG` | Regio van retouradres. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Landcode van retouradres. |
| `4318` | `AI_4318_RTN_TO_POST` | Postcode van retouradres. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Telefoonnummer van retouradres. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Beschrijving van de servicecode. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Indicator gevaarlijke goederen. |
| `4322` | `AI_4322_AUTH_LEAVE` | Toestemming om achter te laten. |
| `4323` | `AI_4323_SIG_REQUIRED` | Indicator handtekening vereist. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maximumtemperatuur in Fahrenheit (uitgedrukt in honderdsten van graden). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maximumtemperatuur in Celsius (uitgedrukt in honderdsten van graden). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimumtemperatuur in Fahrenheit (uitgedrukt in honderdsten van graden). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimumtemperatuur in Celsius (uitgedrukt in honderdsten van graden). |

### Productkenmerken en traceerbaarheid

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NAVO-voorraadnummer (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE-classificatie van vleeskarkassen en -delen. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Actieve potentie. |
| `7005` | `AI_7005_CATCH_AREA` | Vangstgebied. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Soort voor visserijdoeleinden. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Type vistuig. |
| `7010` | `AI_7010_PROD_METHOD` | Productiemethode. |
| `7020` | `AI_7020_REFURB_LOT` | Identificatie van de opknapbeurt. |
| `7021` | `AI_7021_FUNC_STAT` | Functionele status. |
| `7022` | `AI_7022_REV_STAT` | Revisiestatus. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Globale Individuele Bedrijfsmiddel-identificatie (GIAI) van een assemblage. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Nummer van de verwerker, met ISO-landcode van drie cijfers (10 plaatsen). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC met uitbreiding 1 en importeursindex. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT-vrachteenheidstype. |

### Nationale vergoedingsnummers in de zorg (NHRN)

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Duitsland PZN. |
| `711` | `AI_711_NHRN_CIP` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Frankrijk CIP. |
| `712` | `AI_712_NHRN_CN` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Spanje CN. |
| `713` | `AI_713_NHRN_DRN` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Brazilië DRN. |
| `714` | `AI_714_NHRN_AIM` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Verenigde Staten van Amerika NDC. |
| `716` | `AI_716_NHRN_AIC` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Italië AIC. |
| `717` | `AI_717_NHRN_SRN` | Nationaal nummer voor gezondheidszorgvergoeding (NHRN) - Costa Rica, nummer sanitair register. |

### Zorg, GMN, HIDRI, CPID, persoonsgegevens

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Certificeringsreferentie (10 plaatsen). |
| `7240` | `AI_7240_PROTOCOL` | Protocol-identificatie. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC-mediatype. |
| `7242` | `AI_7242_VCN` | Versiebeheernummer (VCN). |
| `7250` | `AI_7250_DOB` | Geboortedatum (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Geboortedatum en -tijd (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biologisch geslacht. |
| `7253` | `AI_7253_FAMILY_NAME` | Achternaam van de persoon. |
| `7254` | `AI_7254_GIVEN_NAME` | Voornaam van de persoon. |
| `7255` | `AI_7255_SUFFIX` | Naamsuffix van de persoon. |
| `7256` | `AI_7256_FULL_NAME` | Volledige naam van de persoon. |
| `7257` | `AI_7257_PERSON_ADDR` | Adres van de persoon. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Geboortevolgorde van de baby. |
| `7259` | `AI_7259_BABY` | Achternaam van de baby. |
| `8001` | `AI_8001_DIMENSIONS` | Rolproducten (breedte, lengte, kerndiameter, richting, verbindingen). |
| `8002` | `AI_8002_CMT_NO` | Identificatie van mobiele telefoon. |
| `8003` | `AI_8003_GRAI` | Globale Identificatie van Retourneerbaar Bedrijfsmiddel (GRAI). |
| `8004` | `AI_8004_GIAI` | Globale Individuele Bedrijfsmiddel-identificatie (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Prijs per meeteenheid. |
| `8006` | `AI_8006_ITIP` | Identificatie van een individueel stuk van een handelseenheid (ITIP). |
| `8007` | `AI_8007_IBAN` | Internationaal Bankrekeningnummer (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Productiedatum en -tijd (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optisch leesbare sensorindicator. |
| `8010` | `AI_8010_CPID` | Component-/onderdeelidentificatie (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Serienummer van component-/onderdeelidentificatie (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Softwareversie. |
| `8013` | `AI_8013_GMN` | Globaal modelnummer (GMN). |
| `8014` | `AI_8014_MUDI` | Identificatie voor registratie van sterk geïndividualiseerde hulpmiddelen (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Globaal Servicerelatienummer (GSRN) ter identificatie van de relatie tussen een organisatie die diensten aanbiedt en de dienstverlener. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Globaal Servicerelatienummer (GSRN) ter identificatie van de relatie tussen een organisatie die diensten aanbiedt en de ontvanger van de diensten. |
| `8019` | `AI_8019_SRIN` | Servicerelatie-instantienummer (SRIN). |
| `8020` | `AI_8020_REF_NO` | Referentienummer betalingsformulier. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identificatie van stukken van een handelseenheid (ITIP) in een logistieke eenheid. |
| `8030` | `AI_8030_DIGSIG` | Digitale handtekening (DigSig). |
| `8040` | `AI_8040_IMEI` | Internationaal identiteitsnummer mobiel toestel (IMEI). |
| `8041` | `AI_8041_IMEI2` | Internationaal identiteitsnummer mobiel toestel 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nummer van ingebouwde SIM. |
| `8043` | `AI_8043_PSIM` | Nummer van fysieke SIM. |
| `8110` | `AI_8110` | Identificatie van couponcode voor gebruik in Noord-Amerika. |
| `8111` | `AI_8111_POINTS` | Loyaliteitspunten van een coupon. |
| `8112` | `AI_8112` | Identificatie van couponcode voor positief-aanbodbestand voor gebruik in Noord-Amerika. |
| `8200` | `AI_8200_PRODUCT_URL` | URL uitgebreide verpakking. |

### Intern / bedrijfsgebruik

| AI | Constante | Beschrijving |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informatie onderling overeengekomen tussen handelspartners. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Bedrijfsinterne informatie (9 plaatsen). |

---

## Bijlage B — constanten van de interpretatiesleutels

Wordt `GaiaParser.parse()` aangeroepen met `ParseMode.INTERPRETATION`, dan kan elk `GS1AIObjectElement` een lijst `GS1AIInterpretation`-objecten dragen die door domeinspecifieke verrijkers zijn voortgebracht. Gebruik de constanten uit `GS1Constants_Enricher` (in het package `tools.pantheum.gaia.gs1.constants`) als sleutels om bepaalde interpretatiewaarden op te zoeken:

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

Weergavelabels zijn **geen** constanten: ze staan in de gelokaliseerde catalogi onder `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, geïndexeerd op de typeconstante. `GS1AIInterpretation.getLabel()` geeft het label voor de parsetaal terug (zie [Gelokaliseerde meldingen en labels](#gelokaliseerde-meldingen-en-labels)) en valt terug op het Engels wanneer een catalogus de sleutel weglaat. De kolom «Weergavelabel» hieronder toont de Nederlandse tekst zoals die in de catalogus wordt meegeleverd; de typesleutels zelf zijn in alle talen gelijk — vergelijk dus altijd op de sleutel en nooit op het label.

### Datum en tijd

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `DATE_VALUE` | Datum | Datum-AI's (11–17, 7003, 7006, 7011 enz.) |
| `DATE_FORMAT` | Datumnotatie | Datum-AI's |
| `TIME_VALUE` | Tijd | AI's met een tijdsaanduiding (7003, 7011, 8008 enz.) |
| `TIME_FORMAT` | Tijdnotatie | AI's met een tijdsaanduiding |
| `DATETIME_VALUE` | Datum en tijd | Datum-en-tijd-AI's |
| `DATETIME_FORMAT` | Datum- en tijdnotatie | Datum-en-tijd-AI's |

### Oogstdatum

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Startdatum oogst | AI 7007 |
| `HARVEST_END_DATE` | Einddatum oogst | AI 7007 (optioneel einde van het bereik) |
| `HARVEST_DATE_RANGE` | Oogstdatumbereik | AI 7007 |

### GS1-bedrijfsprefix

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1-bedrijfsprefix | GTIN- / GLN- / SSCC-AI's |
| `GS1_MEMBER_CODE` | GS1-lidcode | GTIN- / GLN- / SSCC-AI's |
| `GS1_MEMBER_NAME` | GS1-lidorganisatie | GTIN- / GLN- / SSCC-AI's |

### GTIN

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN-type | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Verpakkingsniveau | AI 01 |
| `GTIN_CHECK_DIGIT` | Controlecijfer | AI 01, 02 |

### SSCC

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Uitbreidingscijfer | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Seriereferentie | AI 00 |
| `SSCC_CHECK_DIGIT` | Controlecijfer | AI 00 |

### Land (ISO 3166)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Landcode (numeriek) | Eénland-AI's (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Landcode (alpha-2) | Alfa-2-land-AI's |
| `COUNTRY_NAME` | Landnaam | Eénland-AI's |
| `COUNTRY_LIST` | Landen | AI 423 — alle namen samengevoegd, bijvoorbeeld `Australia, New Zealand` |

AI 423 (land van eerste verwerking) kan tot vijf landen dragen en geeft daarom per land een
**genummerd paar** uit — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — gevolgd door de enkele samenvatting
`COUNTRY_LIST`. Stel deze sleutels samen uit de constanten `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` en de index vanaf 1, of doorloop eenvoudigweg `getInterpretations()`; de
sleutels `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` zonder achtervoegsel worden voor AI 423 **niet** uitgegeven.

### Valuta (ISO 4217)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Valutacode | Bedrag-AI's met valuta (391n, 393n) |
| `CURRENCY_ALPHA` | Alfabetische valutacode | Bedrag-AI's met valuta |
| `CURRENCY_NAME` | Valutanaam | Bedrag-AI's met valuta |

### Temperatuur

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatuur | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Temperatuureenheid | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatuur (opgemaakt) | AI 4330–4333 |

### Geslacht (ISO 5218)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `SEX_CODE` | Geslachtscode | AI 7252 |
| `SEX_DESCRIPTION` | Geslachtsbeschrijving | AI 7252 |

### Waterdieren (FAO)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Soortcode | AI 7008 |
| `SPECIES_SCIENTIFIC` | Wetenschappelijke naam | AI 7008 |
| `SPECIES_ENGLISH` | Algemene naam | AI 7008 |
| `SPECIES_FAMILY` | Familie | AI 7008 |
| `SPECIES_ORDER` | Orde | AI 7008 |

### NAVO-voorraadnummer (NSN)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `NSN_FSG` | Voorzieningsgroep | AI 7001 |
| `NSN_FSG_NAME` | Naam voorzieningsgroep | AI 7001 |
| `NSN_FSCG` | Voorzieningsklasse | AI 7001 |
| `NSN_FSCG_NAME` | Naam voorzieningsklasse | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Landcode | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Land | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO-landcode | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS-categorie | AI 7001 |
| `NSN_NIIN` | Nationaal artikelnummer | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Rolproducten

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Rolbreedte (mm) | AI 8001 |
| `ROLL_LENGTH` | Rollengte (m) | AI 8001 |
| `CORE_DIAMETER` | Kerndiameter (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Code wikkelrichting | AI 8001 |
| `WINDING_DIRECTION` | Wikkelrichting | AI 8001 |
| `SPLICES` | Verbindingen | AI 8001 |

### IBAN

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Landcode | AI 8007 |
| `IBAN_COUNTRY_NAME` | Land | AI 8007 |
| `IBAN_CHECK_DIGITS` | Controlecijfers | AI 8007 |
| `IBAN_CHECK_VALID` | Controle | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Serienummer | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Controlecijfer | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Uitgevende instantie | AI 8040, 8041 |

De 15 cijfers vallen uiteen in `[ TAC (8) ][ serienummer (6) ][ Luhn-controlecijfer (1) ]`, waarbij de
RBI de eerste 2 cijfers van de TAC zijn — `IMEI_RBI` is dus een voorvoegsel van `IMEI_TAC` en geen
afzonderlijk stuk. `IMEI_FORMATTED` geeft de gebruikelijke GSMA-weergavegroepering
`AA-BBBBBB-CCCCCC-D` (bijvoorbeeld `49-015420-323751-8`), die de TAC op de RBI-grens
splitst; de oude groepering `6-2-6-1`, die knipte waar de afgeschafte Final Assembly
Code begon, wordt niet uitgegeven.

`IMEI_RBI_NAME` herleidt de RBI via `ImeiRbiData` tot de naam van de toewijzende instantie en wordt
**als laatste toegevoegd, en alleen wanneer de code daar vermeld staat**. Die tabel omvat drie groepen:

- **Momenteel toewijzend** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, plus `99`
  Global Hexadecimal Administrator en `98` (gereserveerd).
- **Testbereiken** — `00` en `02`–`09`, die test-IMEI's aanduiden in plaats van een echte toewijzing.
  Bevraag ze met `ImeiRbiData.isTestCode(code)`.
- **Niet langer toewijzend** — historische instanties zoals `49` (BZT/BAPT, Duitsland), `44`
  (BABT, Verenigd Koninkrijk) of `91` (MSAI, India). Bevraag ze met `ImeiRbiData.isNoLongerAllocating(code)`.
  Apparaten met deze codes zijn doodgewoon en blijven in gebruik; alleen de toewijzing van nieuwe codes
  is gestaakt, zodat dit rapportage-informatie is en nooit een signaal over geldigheid.

Ontbreekt `IMEI_RBI_NAME`, dan betekent dat «deze RBI staat niet in onze tabel», **niet** «ongeldige IMEI»:
de tabel is samengesteld uit een gepubliceerde RBI-lijst en niet rechtstreeks van de GSMA, en kan dus
achterlopen op recent aangewezen instanties. Leid uit het ontbreken ervan geen validatie-uitkomst af;
de RBI is geen controleteken. Code die de interpretatielijst doorloopt, moet het ontbreken ervan
eveneens verdragen in plaats van op positie te indexeren.

### SIM-identificaties (EID / ICCID)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Branchecategorie | AI 8042 |
| `EID_BODY` | EID-hoofddeel | AI 8042 |
| `EID_CHECK_DIGIT` | Controlecijfer | AI 8042 |
| `ICCID_BODY` | ICCID-hoofddeel | AI 8043 |
| `ICCID_EXTENSION` | Uitbreiding | AI 8043 |

`SIM_MII` draagt de **twee** eerste cijfers (`89`), het paar dat ITU-T E.118 aan de
telecommunicatie toewijst. ISO/IEC 7812 zelf definieert de MII als **enkel het eerste cijfer**, zodat
`SIM_MII_NAME` de categorie uit die eerste `8` afleidt via `Iso7812Data` — wat
«Healthcare, telecommunications and other future industry assignments» oplevert. Voor een welgevormde
EID is die waarde dus constant; ze wordt gemeld ter herleidbaarheid naar de standaard, niet als
onderscheidend criterium. `Iso7812Data.nameForCode(digit)` neemt één enkel cijfer,
`nameForIdentifier(prefix)` aanvaardt een langer voorvoegsel en leest daarvan het eerste cijfer.

`SIM_MII_NAME` wordt alleen uitgegeven door `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
toont `SIM_MII` zonder de categorie.

### Certificeringsreferentie

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Volgnummer | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Code certificeringsschema | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Certificeringsschema | AI 7230–7239 |
| `CERT_REFERENCE` | Certificeringsreferentie | AI 7230–7239 |

### GS1 UIC

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC-code | AI 7040 |
| `UIC_EXTENSION_1` | Uitbreiding 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Importeursindex | AI 7040 |

### Geboortevolgorde van de pasgeborene

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Geboortepositie | AI 7258 |
| `BIRTH_TOTAL` | Totaal aantal geboorten | AI 7258 |
| `BIRTH_SEQUENCE` | Geboortevolgorde | AI 7258 |

### Wereldwijd modelnummer (GMN)

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Modelreferentie | AI 8013 |
| `GMN_CHECK_PAIR` | Controlepaar | AI 8013 |

### HIDRI

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Apparaatreferentie | AI 8014 |
| `HIDRI_CHECK_PAIR` | Controlepaar | AI 8014 |

### CPID

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Component- en onderdeelreferentie | AI 8010–8011 |

### Decimale en maatwaarden

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Decimale waarde | Numerieke AI's met impliciete decimalen (31xx–36xx) |
| `DECIMAL_AMOUNT` | Bedrag | Prijs-AI's (390n–395n) |
| `DECIMAL_PERCENTAGE` | Percentage | AI 394n |
| `DECIMAL_PLACES` | Decimalen | Samen met `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Percentagenotatie | AI 394n |
| `ISO_UNIT_CODE` | ISO-eenheidscode | Maat-AI's |
| `ISO_UNIT_NAME` | ISO-eenheidsnaam | Maat-AI's |
| `MONETARY_AMOUNT` | Geldbedrag | Prijs-AI's |
| `MONETARY_AMOUNT_DISPLAY` | Geldbedrag (opgemaakt) | Prijs-AI's |

### Geocoördinaten

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `LATITUDE` | Breedtegraad | AI 4309 |
| `LONGITUDE` | Lengtegraad | AI 4309 |
| `GEO_COORDINATES` | Geocoördinaten | AI 4309 |
| `LATITUDE_DMS` | Breedtegraad (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Lengtegraad (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Geocoördinaten (DMS) | AI 4309 |

### Productiemethode

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Code productiemethode | AI 7010 |
| `PRODUCTION_METHOD` | Productiemethode | AI 7010 |

### AIDC-mediatype

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC-mediatypecode | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC-mediatype | AI 7241 |

### Stuk van totaal

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Stuknummer | AI 8006 |
| `PIECE_TOTAL` | Totaal aantal stuks | AI 8006 |
| `PIECE_OF_TOTAL` | Stuk van totaal | AI 8006 |

### Componentopsplitsingen

Sleutels die worden uitgegeven door de declaratieve componentopsplitsingen in `content/ai-content.json` en niet
door een Java-verrijker — ze brengen de benoemde delen van een samengestelde AI-waarde naar boven. Anders dan alle
overige sleutels in deze bijlage hebben deze **geen constante in `GS1Constants_Enricher`**: vergelijk
de letterlijke tekenreeks, of lees het type af via `GS1AIInterpretation.getType()`.

| Typesleutel | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Controlecijfer | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Serienummer | AI 253, 255, 8003 |
| `POSTAL_CODE` | Postcode | AI 421 |
| `PROCESSOR_ID` | Verwerker-identificatie | AI 7030–7039 |

Merk op dat `CHECK_DIGIT` hier de algemene sleutel van de componentopsplitsing is, onderscheiden van de
verrijkerspecifieke sleutels `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` en
`EID_CHECK_DIGIT` die hierboven zijn opgesomd.

### Diversen

| Sleutelconstante | Weergavelabel | Voortgebracht door |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Waarde | Booleaanse AI's / vlag-AI's (4321–4323) |
| `DECODED_TEXT` | Gedecodeerde tekst | Vrijetekst-AI's |
