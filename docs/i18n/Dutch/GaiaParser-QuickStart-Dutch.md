# GaiaParser — Snelstart

Zet de payload van een GS1-barcode in ongeveer tien minuten om in gestructureerde, gevalideerde,
voor mensen leesbare gegevens. Dit is de korte weg; de **[GaiaParser-ontwikkelaarshandleiding](GaiaParser-Dutch.md)** is de
volledige referentie, en **[GaiaBuilder](GaiaBuilder-Dutch.md)** behandelt de omgekeerde richting
(het samenstellen van elementreeksen en Digital Link-URI's).

## Inhoud

1. [Gaia aan uw project toevoegen](#1-gaia-aan-uw-project-toevoegen)
2. [Iets parseren](#2-iets-parseren)
3. [Het resultaat lezen](#3-het-resultaat-lezen)
4. [Een mislukte parse afhandelen](#4-een-mislukte-parse-afhandelen)
5. [Twee dingen die u zullen opbreken](#5-twee-dingen-die-u-zullen-opbreken)
6. [Scannervoorvoegsels en Digital Links werken meteen](#6-scannervoorvoegsels-en-digital-links-werken-meteen)
7. [Minder werk doen: de parsemodi](#7-minder-werk-doen-de-parsemodi)
8. [Taal en datumnotatie wijzigen](#8-taal-en-datumnotatie-wijzigen)
9. [Rommelige invoer opschonen](#9-rommelige-invoer-opschonen)
10. [Hoe verder](#10-hoe-verder)

---

## 1. Gaia aan uw project toevoegen

Gaia wordt niet op Maven Central gepubliceerd; bouw de kern dus één keer en installeer die in uw
lokale repository:

```bash
cd gaia && mvn install
```

Neem hem daarna als dependency op:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Dat is de volledige lijst dependencies die u hoeft te schrijven. De jar is licht, zodat Gaia's enige
dependency met compile-scope — `com.fasterxml.jackson.core:jackson-databind` — transitief
meekomt; legt uw build al een Jackson-versie vast, dan wint die en gebruikt Gaia haar.
Gaia richt zich op **Java 11**, en dezelfde jar draait ongewijzigd op elke latere JVM.

> De testsuite van de kern overslaan (`mvn install -DskipTests`) maakt van enkele minuten een paar
> seconden, zolang u aan het inwerken bent.

---

## 2. Iets parseren

Eén klasse, geen configuratie:

```java
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.result.ParseResult;

public class Hello {

    // Reuse one parser: it is thread-safe and does its dataset loading once.
    private static final GaiaParser PARSER = new GaiaParser();

    public static void main(String[] args) {
        ParseResult result = PARSER.parse("01095060001343521726123110LOT-001");

        System.out.println("valid : " + result.isValid());
        System.out.println("HRI   : " + result.getAiObject().toHriString());

        for (GS1AIObjectElement e : result.getAiObject().getAis()) {
            System.out.printf("(%s) %-18s = %s%n", e.getAi(), e.getTitle(), e.getValue());
        }
    }
}
```

```
valid : true
HRI   : (01)09506000134352 (17)261231 (10)LOT-001
(01) GTIN               = 09506000134352
(17) USE BY or EXPIRY   = 261231
(10) BATCH/LOT          = LOT-001
```

`parse(String)` doorloopt de **volledige** keten: syntaxis, inhoudsvalidatie en interpretatie.
Dat is de juiste standaard — beperk hem later als u een gemeten reden hebt om dat te doen.

---

## 3. Het resultaat lezen

`ParseResult.getAiObject()` bevat de herkende AI's. Haal er een bepaalde uit via de code en niet
via de positie:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Elk element draagt een lijst **interpretaties**: de gedecodeerde betekenis achter de ruwe cijfers,
voortgebracht door de interpretatiefase:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` is gelokaliseerd en bedoeld om te tonen. Om een waarde in code te *lezen*, zoekt u haar
beter op via haar typesleutel, die vast ligt:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Verschillende AI's leveren verschillende sleutels op: een GTIN geeft zijn bedrijfsprefix, GTIN-type en
controlecijfer; een prijs geeft de valuta en het decimale bedrag. De volledige lijst staat in
[bijlage B](GaiaParser-Dutch.md#bijlage-b--constanten-van-de-interpretatiesleutels), en de constanten staan
in `GS1Constants_Enricher`. Niet elke AI heeft interpretaties: bij een partijnummer als vrije tekst
valt niets af te leiden, zodat de lijst leeg blijft.

---

## 4. Een mislukte parse afhandelen

Een ongeldige payload is een normale uitkomst, geen uitzondering — `parse` werpt nooit bij foutieve
GS1-gegevens:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Vertak op `getId()`, nooit op de melding.** Meldingen zijn gelokaliseerd en hun formulering
is geen contract — en ze dragen op dit moment een bekend gebrek in de aanhalingstekens (de verdubbelde `''` hierboven),
vermeld in de [foutenreferentie](GaiaParser-Dutch.md#foutenreferentie).

Twee verschillende vragen, twee verschillende methoden:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Een parse daalt niet verder af zodra een fase mislukt, zodat een verkeerd controlecijfer u wel
validatiefouten oplevert maar geen interpretaties.

### Waarschuwingen maken een resultaat niet ongeldig

Sommige controles zijn louter adviserend. Een onbekend GS1-bedrijfsprefix wordt gemeld, maar de payload
blijft structureel in orde:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Gebruik `getIssues()` wanneer u beide wilt. Moet uw workflow onbekende prefixen weigeren, raadpleeg dan
uitdrukkelijk `getWarnings()` — `isValid()` doet dat niet voor u.

---

## 5. Twee dingen die u zullen opbreken

### Het GS-scheidingsteken, en waarom het weglaten erger is dan een fout

Een AI met variabele lengte loopt door tot een **GS-teken** (ASCII `0x1D`, in barcodesymbologieën
FNC1 genoemd) of tot het einde van de reeks. Volgt er nog een AI op, dan is dat scheidingsteken
verplicht:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Laat u het weg, dan krijgt u **geen** fout — u krijgt een zelfverzekerd verkeerd antwoord:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` is `X..20` en slokt `LOT-ABC21SN-98765` dus volkomen terecht op, en de parser heeft geen
enkele manier om te weten dat dat niet de bedoeling was. Verderop valt daar niets meer aan te herstellen; zorg dus
al bij de bron dat het scheidingsteken klopt: lees de bytes van de scanner als **ISO-8859-1** zodat `0x1D` overleeft, en schrijf
`""` in Java-tekenreeksliteralen. AI's met vaste lengte (`01`, `17`, `3103`) hebben geen scheidingsteken nodig —
de parser kent hun lengte.

### De meeste AI's kunnen niet alleen staan

Partij, serienummer, houdbaarheidsdatum en verwanten zijn *attributen*: de GS1 General Specifications
vereisen dat ze samen met een identificatiesleutel reizen, en Gaia dwingt dat af.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Voeg de GTIN toe en het lukt. Moet u werkelijk een fragment parseren — een unittest, een
gedeeltelijke scan — schakel de controle dan uit:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Scannervoorvoegsels en Digital Links werken meteen

U hoeft Gaia niet te vertellen welke vorm de invoer heeft — hij herkent alle vier. Geef hem
gewoon wat de scanner heeft afgeleverd.

**Een AIM-symbologie-identificatievoorvoegsel** bepaalt de symbologie en wordt automatisch verwijderd:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Een GS1 Digital Link-URI** doorloopt dezelfde validatie en verrijking:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Doordat beide vormen in hetzelfde `GS1AIObject` belanden, hoeft de code die een scan verwerkt zich er niet
om te bekommeren welke van de twee binnenkwam — en `toElementString()` / `getCanonicalDigitalLink()`
zetten ze naar elkaar om.

Ook een **correlatievoorvoegsel van 8 cijfers** (`12345678~…`) wordt verwijderd en bewaard in
`getCorrelationInfo()`, als uw verwerkingsketen er een gebruikt.

---

## 7. Minder werk doen: de parsemodi

De standaard doet alles. Vraag om minder wanneer u maar een deel van het antwoord nodig hebt:

| Modus | Beantwoordt de vraag | Kosten |
|---|---|---|
| `DATA_CARRIER` | Om welke symbologie gaat het? | De laagste — helemaal geen AI-parsing, `getAiObject()` is `null` |
| `SYNTAX` | Zijn de AI-codes en lengtes welgevormd? | Geen controlecijfers, geen interpretaties |
| `CONTENT` | Zijn dit geldige GS1-gegevens? | Volledige validatie, geen interpretaties |
| `INTERPRETATION` | Wat betekent het? | **Standaard** — alles |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Grijp naar `CONTENT` wanneer u op grote schaal valideert en de uitsplitsing nooit toont, en naar
`DATA_CARRIER` wanneer u een scan alleen naar de juiste handler hoeft te routeren.

---

## 8. Taal en datumnotatie wijzigen

Foutmeldingen, interpretatielabels en AI-beschrijvingen zijn in **35 talen** vertaald;
datums worden weergegeven zoals u wilt. Dat alles past in één onveranderlijke `ParseConfig`:

```java
ParseConfig config = ParseConfig.builder()
        .language(Language.FRENCH)
        .dateEndian(DateEndian.BIG)          // yyyy/mm/dd
        .dateSeparator(DateSeparator.HYPHEN) // yyyy-mm-dd
        .build();

ParseResult r = PARSER.parse("01095060001343521726123110LOT-001", config);

r.getAiObject().get("17").getDescription();
// "Date limite d'utilisation (AAMMJJ)"
```

```
Date                     2026-12-31
Format de date           yyyy-mm-dd
```

Waarden worden nooit gelokaliseerd — alleen labels, beschrijvingen en meldingen — zodat `"2026-12-31"` en
`"09506000134352"` in elke taal hetzelfde betekenen. Bouw de configuratie één keer bij het opstarten
en deel haar; ze is onveranderlijk.

---

## 9. Rommelige invoer opschonen

Stuurt uw bron afgedrukte HRI-haakjes of losse spaties uit, dan bevat de kern twee
**invoermodificatoren** die de payload vóór het parseren herstellen:

```java
ParseConfig config = ParseConfig.builder()
        .modifier(new ModifierRemoveSpaces())        // register spaces first
        .modifier(new ModifierRemoveAIBrackets())
        .build();

ParseResult r = PARSER.parse("(01) 09506000134352 (17) 261231 (10) LOT-001", config);

r.isValid();                                     // true
r.getPayload();                                  // 01095060001343521726123110LOT-001
r.getModifierInfo().getAppliedModifiers();       // [Remove Space Characters, Remove Brackets Around AI]
```

Standaard staat er niets aan, en beide hebben hun kanttekeningen: spatie en haakjes zijn geoorloofde
GS1-gegevenstekens, dus pas ze alleen toe op een bron die u kent. Zie
[Ingebouwde modificatoren](GaiaParser-Dutch.md#ingebouwde-modificatoren), waar ook staat waarom het verwijderen van de
haakjes het scheidingsteken moet herstellen dat ze impliceerden.

---

## 10. Hoe verder

- **[GaiaParser-ontwikkelaarshandleiding](GaiaParser-Dutch.md)** — de verwerkingsketen in detail, het volledige
  resultaatmodel, alle foutcodes en de bijlagen over AI's en interpretatiesleutels.
- **[GaiaBuilder-ontwikkelaarshandleiding](GaiaBuilder-Dutch.md)** — elementreeksen en Digital Link-URI's bouwen
  uit AI/waarde-paren.
- **[Gaia API HTTP-referentie](../../gaia-api-reference.md)** — dezelfde machinerie via HTTP, als u
  de bibliotheek liever niet insluit.
- **[ai-codes.txt](../../ai-codes.txt)** — een platte lijst `(AI) TITEL` om snel iets op te zoeken.

### De versie in vijf regels

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
