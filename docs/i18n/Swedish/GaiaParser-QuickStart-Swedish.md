# GaiaParser — Snabbstart

Gör nyttolasten i en GS1-streckkod till strukturerade, validerade och mänskligt läsbara data
på ungefär tio minuter. Det här är den korta vägen; **[utvecklarhandboken för GaiaParser](GaiaParser-Swedish.md)** är
den fullständiga referensen, och **[GaiaBuilder](GaiaBuilder-Swedish.md)** behandlar motsatt riktning
(att bygga elementsträngar och Digital Link-URI:er).

## Innehåll

1. [Att lägga till Gaia i projektet](#1-att-lägga-till-gaia-i-projektet)
2. [Att tolka något](#2-att-tolka-något)
3. [Att läsa resultatet](#3-att-läsa-resultatet)
4. [Att hantera en misslyckad tolkning](#4-att-hantera-en-misslyckad-tolkning)
5. [Två saker som kommer att ställa till det](#5-två-saker-som-kommer-att-ställa-till-det)
6. [Läsarprefix och Digital Link fungerar direkt](#6-läsarprefix-och-digital-link-fungerar-direkt)
7. [Mindre arbete: tolkningslägena](#7-mindre-arbete-tolkningslägena)
8. [Att byta språk och datumformat](#8-att-byta-språk-och-datumformat)
9. [Att städa upp stökigt indata](#9-att-städa-upp-stökigt-indata)
10. [Vidare härifrån](#10-vidare-härifrån)

---

## 1. Att lägga till Gaia i projektet

Gaia publiceras inte på Maven Central, så bygg huvudmodulen en gång och installera den i ditt
lokala arkiv:

```bash
cd gaia && mvn install
```

Ange därefter beroendet:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Det är hela beroendelistan du behöver skriva. Jar-filen är lätt, så Gaias enda
beroende med kompileringsomfång — `com.fasterxml.jackson.core:jackson-databind` — följer med
transitivt; låser ditt bygge redan en Jackson-version vinner den, och det är den Gaia använder.
Gaia riktar sig mot **Java 11**, och samma jar-fil kör oförändrad på varje senare JVM.

> Att hoppa över huvudmodulens testsvit (`mvn install -DskipTests`) gör några minuter till ett par
> sekunder medan du håller på att komma in i biblioteket.

---

## 2. Att tolka något

En enda klass, ingen konfiguration:

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

`parse(String)` kör **hela** kedjan: syntax, innehållsvalidering och tolkning.
Det är rätt standardval — snäva in det senare om mätningar ger dig skäl till det.

---

## 3. Att läsa resultatet

`ParseResult.getAiObject()` innehåller de igenkända AI:erna. Hämta ett bestämt via dess kod och inte
via dess läge:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Varje element bär en lista med **tolkningar** — den avkodade innebörden bakom de råa siffrorna,
skapad i tolkningssteget:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` lokaliseras och är avsedd att visas. Vill du *läsa* ett värde i kod, slå hellre upp det
via dess typnyckel, som är oföränderlig:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Olika AI ger olika nycklar: ett GTIN ger sitt företagsprefix, sin GTIN-typ och sin kontrollsiffra;
ett pris ger valuta och decimalbelopp. Den fullständiga listan finns i
[bilaga B](GaiaParser-Swedish.md#bilaga-b--konstanter-för-tolkningsnycklar), och konstanterna ligger
i `GS1Constants_Enricher`. Alla AI har inte tolkningar: ur ett partinummer i fri text
går ingenting att härleda, så dess lista förblir tom.

---

## 4. Att hantera en misslyckad tolkning

En ogiltig nyttolast är ett normalt utfall, inte ett undantag — `parse` kastar aldrig undantag för felaktiga
GS1-data:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Förgrena på `getId()`, aldrig på meddelandetexten.** Meddelandena lokaliseras och deras ordalydelse
är inget åtagande — dessutom bär de just nu en känd brist i citattecknen (de dubblerade `''` ovan),
noterad i [felöversikten](GaiaParser-Swedish.md#felöversikt).

Två skilda frågor, två skilda metoder:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

En tolkning slutar gå djupare så snart ett steg misslyckas, så en felaktig kontrollsiffra ger dig
valideringsfel men inga tolkningar.

### Varningar gör inte ett resultat ogiltigt

Vissa kontroller är enbart upplysande. Ett okänt GS1-företagsprefix rapporteras, men nyttolasten
är alltjämt strukturellt riktig:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Använd `getIssues()` när du vill ha båda. Måste ditt flöde avvisa okända prefix, läs
`getWarnings()` uttryckligen — `isValid()` gör det inte åt dig.

---

## 5. Två saker som kommer att ställa till det

### GS-avgränsaren, och varför det är värre att utelämna den än att få ett fel

Ett AI med variabel längd sträcker sig fram till ett **GS-tecken** (ASCII `0x1D`, i streckkodssymbologier
kallat FNC1) eller till strängens slut. Följer ett annat AI efter är den avgränsaren
obligatorisk:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Utelämna den så får du **inget** fel — du får ett självsäkert felaktigt svar:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` har formatet `X..20` och sväljer alltså med full rätt `LOT-ABC21SN-98765`, och tolken har ingen
möjlighet att veta att det inte var meningen. Längre fram i kedjan går det inte att rätta till, så se
till att avgränsaren blir rätt redan vid källan: läs läsarens byte som **ISO-8859-1** så att `0x1D` överlever, och skriv
`""` i Javas stränglitteraler. AI med fast längd (`01`, `17`, `3103`) behöver ingen avgränsare —
tolken känner deras längd.

### De flesta AI får inte stå ensamma

Parti, serienummer, sista förbrukningsdag och deras likar är *attribut*: GS1 General Specifications
kräver att de färdas tillsammans med en identifieringsnyckel, och Gaia upprätthåller det.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Lägg till GTIN:et så går det igenom. Behöver du verkligen tolka ett fragment — ett enhetstest, en
ofullständig avläsning — stäng av kontrollen:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Läsarprefix och Digital Link fungerar direkt

Du behöver inte tala om för Gaia vilken form indata har — den känner igen alla fyra. Ge den helt enkelt
det som läsaren lämnade ifrån sig.

**Ett prefix med AIM-symbologiidentifierare** bestämmer symbologin och tas bort automatiskt:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**En GS1 Digital Link-URI** går genom samma validering och samma berikning:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Eftersom båda formerna hamnar i samma `GS1AIObject` behöver koden som tar emot en avläsning inte
bry sig om vilken av dem som kom — och `toElementString()` / `getCanonicalDigitalLink()`
växlar mellan dem.

Även ett **åttasiffrigt korrelationsprefix** (`12345678~…`) tas bort och bevaras i
`getCorrelationInfo()`, om din behandlingskedja använder ett sådant.

---

## 7. Mindre arbete: tolkningslägena

Standardvalet gör allt. Be om mindre när du bara behöver en del av svaret:

| Läge | Svarar på frågan | Kostnad |
|---|---|---|
| `DATA_CARRIER` | Vilken symbologi är detta? | Lägst — ingen AI-tolkning alls, `getAiObject()` är `null` |
| `SYNTAX` | Är AI-koderna och längderna välformade? | Inga kontrollsiffror, inga tolkningar |
| `CONTENT` | Är detta giltiga GS1-data? | Fullständig validering, inga tolkningar |
| `INTERPRETATION` | Vad betyder det? | **Standard** — allt |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Ta till `CONTENT` när du validerar i stor mängd och aldrig visar uppdelningen, och
`DATA_CARRIER` när du bara behöver dirigera en avläsning till rätt hanterare.

---

## 8. Att byta språk och datumformat

Felmeddelanden, tolkningsetiketter och AI-beskrivningar är översatta till **35 språk**;
datum visas som du vill. Allt detta ryms i ett enda oföränderligt `ParseConfig`:

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

Värdena lokaliseras aldrig — bara etiketter, beskrivningar och meddelanden gör det — så `"2026-12-31"` och
`"09506000134352"` betyder detsamma på alla språk. Bygg konfigurationen en gång vid start
och dela den; den är oföränderlig.

---

## 9. Att städa upp stökigt indata

Sänder din källa utskrivna HRI-parenteser eller lösa blanksteg finns det två
**indatamodifierare** i huvudmodulen som reparerar nyttolasten före tolkningen:

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

Ingenting är påslaget som standard, och båda har sina förbehåll: blanksteg och parenteser är tillåtna
GS1-datatecken, så tillämpa dem enbart på en källa du känner. Se
[Inbyggda modifierare](GaiaParser-Swedish.md#inbyggda-modifierare), där det också förklaras varför borttagningen av
parenteser måste återställa den avgränsare de stod för.

---

## 10. Vidare härifrån

- **[Utvecklarhandbok för GaiaParser](GaiaParser-Swedish.md)** — behandlingskedjan i detalj, hela
  resultatmodellen, samtliga felkoder och bilagorna om AI och tolkningsnycklar.
- **[Utvecklarhandbok för GaiaBuilder](GaiaBuilder-Swedish.md)** — att bygga elementsträngar och Digital Link-URI:er
  ur AI/värde-par.
- **[HTTP-referens för Gaias API](../../gaia-api-reference.md)** — samma maskineri över HTTP, om du
  hellre slipper bädda in biblioteket.
- **[ai-codes.txt](../../ai-codes.txt)** — en enkel förteckning `(AI) RUBRIK` för snabb uppslagning.

### Versionen på fem rader

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
