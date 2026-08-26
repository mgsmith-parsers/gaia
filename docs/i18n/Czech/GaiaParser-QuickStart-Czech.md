# GaiaParser — Rychlý úvod

Proměňte užitečná data čárového kódu GS1 ve strukturovaná, ověřená a člověku srozumitelná data
zhruba za deset minut. Toto je krátká cesta; **[příručka vývojáře GaiaParseru](GaiaParser-Czech.md)** je
úplným referenčním textem a **[GaiaBuilder](GaiaBuilder-Czech.md)** popisuje opačný směr
(sestavování řetězců prvků a URI Digital Link).

## Obsah

1. [Přidání Gaii do projektu](#1-přidání-gaii-do-projektu)
2. [První rozbor](#2-první-rozbor)
3. [Čtení výsledku](#3-čtení-výsledku)
4. [Ošetření nezdařeného rozboru](#4-ošetření-nezdařeného-rozboru)
5. [Dvě věci, na kterých si spálíte prsty](#5-dvě-věci-na-kterých-si-spálíte-prsty)
6. [Předpony snímačů a Digital Link fungují rovnou](#6-předpony-snímačů-a-digital-link-fungují-rovnou)
7. [Méně práce: režimy rozboru](#7-méně-práce-režimy-rozboru)
8. [Změna jazyka a formátu data](#8-změna-jazyka-a-formátu-data)
9. [Úklid neupraveného vstupu](#9-úklid-neupraveného-vstupu)
10. [Kam dál](#10-kam-dál)

---

## 1. Přidání Gaii do projektu

Gaia se nezveřejňuje na Maven Central, sestavte tedy hlavní modul jednou a nainstalujte jej do svého
místního repozitáře:

```bash
cd gaia && mvn install
```

Poté vyhlaste závislost:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

To je celý seznam závislostí, který musíte napsat. Soubor jar je útlý, takže jediná
závislost Gaii s rozsahem překladu — `com.fasterxml.jackson.core:jackson-databind` — přichází
tranzitivně; určuje-li vaše sestavení už verzi Jacksonu, vítězí ta a Gaia ji použije.
Gaia míří na **Javu 11** a tentýž soubor jar běží beze změny na každém pozdějším JVM.

> Vynechání sady testů hlavního modulu (`mvn install -DskipTests`) promění několik minut v pár
> sekund, dokud se teprve rozkoukáváte.

---

## 2. První rozbor

Jediná třída, žádné nastavení:

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

`parse(String)` provádí **celý** řetěz: skladbu, ověření obsahu a výklad.
To je správné výchozí nastavení — zužte je později, dají-li vám k tomu měření důvod.

---

## 3. Čtení výsledku

`ParseResult.getAiObject()` obsahuje rozpoznané AI. Sahejte po konkrétním podle jeho kódu, a nikoli
podle polohy:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Každý prvek nese seznam **výkladů** — rozšifrovaný význam skrytý za surovými číslicemi,
získaný ve stupni výkladu:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` je lokalizovaný a určený k zobrazení. Chcete-li hodnotu v kódu *přečíst*, hledejte ji
raději podle klíče typu, který je neměnný:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Různé AI dávají různé klíče: GTIN vrátí své předčíslí společnosti, typ GTIN a kontrolní
číslici; cena měnu a desetinnou částku. Úplný seznam najdete v
[příloze B](GaiaParser-Czech.md#příloha-b--konstanty-klíčů-výkladu) a konstanty leží
v `GS1Constants_Enricher`. Výklady nemá každý AI: z čísla šarže ve volném textu
se nedá nic odvodit, takže jeho seznam zůstane prázdný.

---

## 4. Ošetření nezdařeného rozboru

Neplatná užitečná data jsou běžným výsledkem, nikoli výjimkou — `parse` u chybných dat GS1
výjimku nikdy nevyvolá:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Větvete podle `getId()`, nikdy podle textu hlášení.** Hlášení se lokalizují a jejich znění
není závazkem — navíc nyní nesou známou vadu uvozovek (zdvojené `''` výše),
zaznamenanou v [přehledu chyb](GaiaParser-Czech.md#přehled-chyb).

Dvě různé otázky, dvě různé metody:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Rozbor přestává klesat hlouběji, jakmile některý stupeň selže, takže chybná kontrolní číslice vám dá
chyby ověření, avšak žádné výklady.

### Varování výsledek neplatným nečiní

Některé kontroly jsou pouze informativní. Nerozpoznané předčíslí společnosti GS1 se ohlásí, užitečná data
však zůstávají strukturně v pořádku:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Používejte `getIssues()`, potřebujete-li obojí. Musí-li váš postup neznámá předčíslí odmítat, ověřujte
`getWarnings()` výslovně — `isValid()` to za vás neudělá.

---

## 5. Dvě věci, na kterých si spálíte prsty

### Oddělovač GS a proč je jeho vynechání horší než chyba

AI proměnné délky se táhne až ke **znaku GS** (ASCII `0x1D`, v symbolikách čárových kódů
zvanému FNC1) nebo ke konci řetězce. Následuje-li po něm další AI, je tento oddělovač
povinný:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Vynechte jej a **nedostanete** chybu — dostanete sebevědomě podanou nesprávnou odpověď:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` má formát `X..20`, takže zcela oprávněně spolkne `LOT-ABC21SN-98765`, a analyzátor nemá
jak zjistit, že to tak zamýšleno nebylo. Dále v řetězu to už nic nenapraví, postarejte se tedy
o správný oddělovač přímo u zdroje: čtěte bajty ze snímače jako **ISO-8859-1**, aby `0x1D` přežil, a pište
`""` v řetězcových literálech Javy. Identifikátory AI pevné délky (`01`, `17`, `3103`) oddělovač nepotřebují —
analyzátor jejich délku zná.

### Většina AI nesmí stát samostatně

Šarže, sériové číslo, datum spotřeby a jim podobné jsou *atributy*: GS1 General Specifications
vyžadují, aby putovaly spolu s identifikačním klíčem, a Gaia to vymáhá.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Přidejte GTIN a rozbor projde. Potřebujete-li opravdu rozebrat úryvek — jednotkový test,
částečné snímání —, tuto kontrolu vypněte:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Předpony snímačů a Digital Link fungují rovnou

Nemusíte Gaie sdělovat, jakou podobu vstup má — rozpozná všechny čtyři. Prostě jí předejte
to, co snímač vydal.

**Předpona identifikátoru symboliky AIM** určí symboliku a samočinně se odstraní:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**URI GS1 Digital Link** prochází týmž ověřováním a týmž obohacením:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Protože obě podoby končí v témže objektu `GS1AIObject`, kód zpracovávající snímaná data se nemusí
starat o to, která z nich přišla — a `toElementString()` / `getCanonicalDigitalLink()`
mezi nimi převádějí.

Také **osmimístná předpona korelace** (`12345678~…`) se odstraní a uchová v
`getCorrelationInfo()`, používá-li ji váš řetěz zpracování.

---

## 7. Méně práce: režimy rozboru

Výchozí nastavení dělá všechno. Žádejte méně, potřebujete-li jen část odpovědi:

| Režim | Odpovídá na otázku | Náklady |
|---|---|---|
| `DATA_CARRIER` | O jakou symboliku jde? | Nejnižší — žádný rozbor AI, `getAiObject()` je `null` |
| `SYNTAX` | Jsou kódy AI a délky správně utvořeny? | Bez kontrolních číslic a bez výkladů |
| `CONTENT` | Jsou to platná data GS1? | Úplné ověření, bez výkladů |
| `INTERPRETATION` | Co to znamená? | **Výchozí** — vše |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Sáhněte po `CONTENT`, ověřujete-li ve velkém a rozklad nikdy nezobrazujete, a po
`DATA_CARRIER`, potřebujete-li snímaná data jen nasměrovat ke správné obslužné rutině.

---

## 8. Změna jazyka a formátu data

Chybová hlášení, popisky výkladu a popisy AI jsou přeloženy do **35 jazyků**;
data se zobrazují, jak si přejete. To vše se vejde do jediného neměnného `ParseConfig`:

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

Hodnoty se nelokalizují nikdy — lokalizují se pouze popisky, popisy a hlášení —, takže `"2026-12-31"` a
`"09506000134352"` znamenají v každém jazyce totéž. Sestavte nastavení jednou při spuštění
a sdílejte je; je neměnné.

---

## 9. Úklid neupraveného vstupu

Vydává-li váš zdroj vytištěné závorky HRI nebo roztroušené mezery, obsahuje hlavní modul dva
**modifikátory vstupu**, které užitečná data před rozborem opraví:

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

Ve výchozím nastavení není zapnuto nic a oba mají své výhrady: mezera i závorky jsou přípustné
datové znaky GS1, uplatňujte je tedy jen na zdroj, který znáte. Viz
[Vestavěné modifikátory](GaiaParser-Czech.md#vestavěné-modifikátory), kde je vysvětleno i to, proč odstranění
závorek musí obnovit oddělovač, který zastupovaly.

---

## 10. Kam dál

- **[Příručka vývojáře GaiaParseru](GaiaParser-Czech.md)** — zpracovatelský řetěz podrobně, úplný model
  výsledku, všechny kódy chyb a přílohy o AI a klíčích výkladu.
- **[Příručka vývojáře GaiaBuilderu](GaiaBuilder-Czech.md)** — sestavování řetězců prvků a URI Digital Link
  z dvojic AI/hodnota.
- **[Přehled HTTP API Gaii](../../gaia-api-reference.md)** — týž stroj přes HTTP, dáváte-li přednost
  tomu, knihovnu nevkládat.
- **[ai-codes.txt](../../ai-codes.txt)** — prostý seznam `(AI) NÁZEV` k rychlému nahlédnutí.

### Verze na pět řádků

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
