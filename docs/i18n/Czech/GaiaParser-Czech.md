# GAIA (GS1 Application Identifiers Analyser) — Příručka vývojáře

## Obsah

1. [Přehled](#přehled)
2. [O GS1 a dokumentu General Specifications](#o-gs1-a-dokumentu-general-specifications)
3. [Aplikační identifikátory GS1](#aplikační-identifikátory-gs1)
4. [Rychlý úvod](#rychlý-úvod)
5. [Zpracovatelský řetěz rozboru](#zpracovatelský-řetěz-rozboru)
   - [Předstupeň — modifikátory vstupu](#předstupeň--modifikátory-vstupu)
   - [Stupeň 0 — identifikátor korelace](#stupeň-0--identifikátor-korelace)
   - [Stupeň 1 — směrování vstupu](#stupeň-1--směrování-vstupu)
   - [Stupeň 2 — skladba](#stupeň-2--skladba)
   - [Stupeň 3 — obsah](#stupeň-3--obsah)
   - [Stupeň 4 — výklad](#stupeň-4--výklad)
6. [Nastavení rozboru (`ParseConfig`)](#nastavení-rozboru-parseconfig)
   - [Volby](#volby)
   - [Lokalizovaná hlášení a popisky](#lokalizovaná-hlášení-a-popisky)
   - [Formátování dat](#formátování-dat)
7. [Modifikátory vstupu](#modifikátory-vstupu)
   - [Vestavěné modifikátory](#vestavěné-modifikátory)
   - [Napsání modifikátoru](#napsání-modifikátoru)
   - [Zápis modifikátorů](#zápis-modifikátorů)
   - [Zjištění, co modifikátor udělal](#zjištění-co-modifikátor-udělal)
   - [Ošetření selhání modifikátoru](#ošetření-selhání-modifikátoru)
8. [Režimy rozboru](#režimy-rozboru)
   - [Režim DATA_CARRIER](#režim-data_carrier)
   - [Režim SYNTAX](#režim-syntax)
   - [Režim CONTENT](#režim-content)
   - [Režim INTERPRETATION (výchozí)](#režim-interpretation-výchozí)
9. [Identifikátor korelace](#identifikátor-korelace)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Práce s výsledky](#práce-s-výsledky)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry a DataCarrierType](#datacarrierentry-a-datacarriertype)
12. [Přehled chyb](#přehled-chyb)
13. [Bezpečnost pro vlákna](#bezpečnost-pro-vlákna)
14. [Příloha A — řetězcové konstanty AI](#příloha-a--řetězcové-konstanty-ai)
    - [Identifikace a sériové značení](#identifikace-a-sériové-značení)
    - [Data a časy](#data-a-časy)
    - [Množství a míra — proměnná míra (metrická)](#množství-a-míra--proměnná-míra-metrická)
    - [Množství a míra — proměnná míra (imperiální / USA)](#množství-a-míra--proměnná-míra-imperiální--usa)
    - [Ceny a peněžní částky](#ceny-a-peněžní-částky)
    - [Místo a odeslání](#místo-a-odeslání)
    - [Vlastnosti výrobku a sledovatelnost](#vlastnosti-výrobku-a-sledovatelnost)
    - [Národní čísla úhrad ve zdravotnictví (NHRN)](#národní-čísla-úhrad-ve-zdravotnictví-nhrn)
    - [Zdravotnictví, GMN, HIDRI, CPID, osobní údaje](#zdravotnictví-gmn-hidri-cpid-osobní-údaje)
    - [Vnitřní / firemní použití](#vnitřní--firemní-použití)
15. [Příloha B — konstanty klíčů výkladu](#příloha-b--konstanty-klíčů-výkladu)
    - [Datum a čas](#datum-a-čas)
    - [Datum sklizně](#datum-sklizně)
    - [Předčíslí společnosti GS1](#předčíslí-společnosti-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Země (ISO 3166)](#země-iso-3166)
    - [Měna (ISO 4217)](#měna-iso-4217)
    - [Teplota](#teplota)
    - [Pohlaví (ISO 5218)](#pohlaví-iso-5218)
    - [Vodní druhy (FAO)](#vodní-druhy-fao)
    - [Skladové číslo NATO (NSN)](#skladové-číslo-nato-nsn)
    - [Návinové výrobky](#návinové-výrobky)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identifikátory SIM (EID / ICCID)](#identifikátory-sim-eid--iccid)
    - [Odkaz na certifikaci](#odkaz-na-certifikaci)
    - [GS1 UIC](#gs1-uic)
    - [Pořadí narození novorozence](#pořadí-narození-novorozence)
    - [Globální číslo modelu (GMN)](#globální-číslo-modelu-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Desetinné a měrné hodnoty](#desetinné-a-měrné-hodnoty)
    - [Zeměpisné souřadnice](#zeměpisné-souřadnice)
    - [Způsob výroby](#způsob-výroby)
    - [Druh nosiče AIDC](#druh-nosiče-aidc)
    - [Kus z celku](#kus-z-celku)
    - [Rozklad na složky](#rozklad-na-složky)
    - [Různé](#různé)

---

## Přehled

`GaiaParser` je vstupní bod pro rozbor řetězců prvků s aplikačními identifikátory (AI) GS1. Přijímá surový výstup snímače v kterékoli z následujících podob a vrací strukturovaný `ParseResult` obsahující všechny rozpoznané AI, chyby ověření a volitelně i výklady srozumitelné člověku:

- Prostý řetězec prvků AI: `0109506000134352`
- Řetězec prvků s předponou identifikátoru symboliky AIM: `]C10109506000134352`
- URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Kterákoli z výše uvedených podob, případně s osmimístným identifikátorem korelace na začátku: `12345678~0109506000134352`

**Vstupní třída:** `tools.pantheum.gaia.GaiaParser`

> **Začínáte s Gaiou?** Začněte **[rychlým úvodem do GaiaParseru](GaiaParser-QuickStart-Czech.md)** — závislost, první rozbor a hrstka obvyklých nástrah zhruba za deset minut. Tato příručka je úplným referenčním textem.

> Opačnou operaci — *sestavení* správně utvořených řetězců prvků a URI Digital Link z dvojic AI/hodnota — popisuje **[GaiaBuilder — Příručka vývojáře](GaiaBuilder-Czech.md)**.

---

## O GS1 a dokumentu General Specifications

**GS1** je celosvětová nezisková organizace, která vytváří a spravuje otevřené standardy pro identifikaci a výměnu dat v dodavatelských řetězcích. Její standardy se používají v maloobchodu, zdravotnictví, logistice, stravovacích službách a mnoha dalších odvětvích — od čárových kódů na spotřebitelských obalech až po sériové sledování farmaceutických dávek.

Závazným pramenem pro vše, co tento analyzátor uskutečňuje, je dokument **GS1 General Specifications** — jediný dokument, který vymezuje:

- Všechny kódy aplikačních identifikátorů (AI), jejich datové názvy, formáty a pravidla ověřování
- Pravidla skladby pro sestavování a kódování řetězců prvků AI
- Požadavky na symboliky čárových kódů a přidělování identifikátorů symboliky AIM
- Algoritmy kontrolní číslice a kontrolního znaku
- Rozvinutí dvoumístného označení roku (pravidlo posuvného okna)
- Specifikace Data Matrix, QR Code, GS1-128, GS1 DataBar a dalších nosičů dat

Dokument GS1 General Specifications se aktualizuje každoročně. Platné vydání a doprovodné materiály jsou dostupné na adrese:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA uskutečňuje **vydání 26.0 (schválené v lednu 2026)** dokumentu GS1 General Specifications.

URI GS1 Digital Link se řídí samostatným standardem **GS1 Digital Link: URI Syntax**, který vymezuje primární identifikační klíče, pořadí kvalifikátorů klíče a kódování datových atributů, jež analyzátor uplatňuje na vstupy typu Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA uskutečňuje **vydání 1.7.0 (schválené v srpnu 2026)** standardu GS1 Digital Link: URI Syntax.

Odkazy na oddíly v celém tomto dokumentu se vztahují k GS1 General Specifications (například „Table 7-5“, „section 7.12“), s výjimkou čísel oddílů Digital Link (například „§4.9“, „§4.12“), která odkazují na standard GS1 Digital Link: URI Syntax.

---

## Aplikační identifikátory GS1

**Aplikační identifikátor (AI) GS1** je krátká číselná předpona o dvou až čtyřech číslicích, která určuje význam a formát dat bezprostředně za ní následujících. Identifikátory AI jsou vymezeny v GS1 General Specifications a pokrývají široký okruh dat dodavatelského řetězce: identifikátory výrobků, data, množství, čísla šarží, sériová čísla, míry, adresy URL a mnohé další.

### Stavba prvku AI

Každý prvek AI se skládá ze dvou částí:

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

Kód AI je vždy číselný. Hodnota dat následuje bezprostředně za ním, bez jakéhokoli oddělovače mezi kódem a hodnotou.

### AI pevné a proměnné délky

Identifikátory AI se dělí do dvou skupin:

| Druh | Chování | Příklad |
|---|---|---|
| **Pevná délka** | Přesný počet znaků, vždy načtený celý | AI `01` (GTIN) — vždy 14 číslic |
| **Proměnná délka** | Od 1 znaku po maximum; končí oddělovačem GS nebo koncem vstupu | AI `10` (šarže) — 1 až 20 alfanumerických znaků |

Zda má AI pevnou, či proměnnou délku, plyne výhradně z jeho vymezení ve specifikaci GS1 — analyzátor nikdy nehádá.

### Řetězce prvků s více AI

Několik AI lze spojit do jediného řetězce prvků. Identifikátory AI pevné délky lze spojovat přímo, protože analyzátor vždy přesně ví, kolik znaků načíst. Identifikátory AI proměnné délky je nutné zakončit **znakem GS** (ASCII `0x1D`, v symbolikách čárových kódů nazývaným též FNC1) pokaždé, když po nich následuje další AI — aby analyzátor věděl, kde hodnota končí a kde začíná další kód AI.

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

V řetězcových literálech Javy se znak GS zapisuje únikovou posloupností Unicode `""`.

### Časté AI

| AI | Datový název | Formát | Ukázková hodnota |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (RRMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (RRMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, jednotná měnová oblast) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Čtvrtá číslice** čtyřmístného AI míry nebo ceny kóduje počet předpokládaných desetinných míst: `3103` je čistá hmotnost v kg se 3 desetinnými místy (`001500` = 1,500 kg), zatímco `3102` by tytéž číslice četl jako 15,00 kg. Sloupec `Formát` výše ukazuje formát *dat*; úplný `getFormatString()` každého AI zahrnuje i AI samotný (například `N4+N6` pro `3103`).

### Výklad srozumitelný člověku (HRI)

Ustálená čitelná podoba uzavírá každý kód AI do závorek bezprostředně před jeho hodnotu, s mezerou mezi prvky:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Oddělovač GS se v HRI nezobrazuje. Tento formát vytváří `GS1AIObject.toHriString()`.

### Čtyřmístné kódy AI

Některé AI mají čtyři číslice namísto dvou. První dvě označují rodinu AI; třetí a/nebo čtvrtá nesou další význam (například polohu předpokládané desetinné čárky u AI míry). Analyzátor si úplný kód AI určí z řetězce prvků sám — volající kód vždy pracuje s úplným kódem (například `"3102"`, nikoli jen `"31"`).

---

## Rychlý úvod

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

> **Oddělovač GS:** v řetězci s několika AI musí být identifikátory proměnné délky odděleny znakem GS (ASCII `0x1D`). V řetězcových literálech Javy použijte `""`.

---

## Zpracovatelský řetěz rozboru

### Předstupeň — modifikátory vstupu

Obsahuje-li `ParseConfig` **modifikátory vstupu**, spustí se dříve než cokoli jiného: před odstraněním identifikátoru korelace, před rozpoznáním nosiče dat, ještě před vstupem do řetězu GS1. Každý modifikátor přepisuje surový vstup pro následující a všechny níže popsané stupně pracují s výstupem řetězce.

Ve výchozím nastavení není nastaven žádný modifikátor, takže tento předstupeň nic nedělá, dokud jej výslovně nezapnete. Viz [Modifikátory vstupu](#modifikátory-vstupu).

---

### Stupeň 0 — identifikátor korelace

Před jakýmkoli zpracováním GS1 `GaiaParser` ověří, zda vstup začíná nepovinnou **předponou identifikátoru korelace**: přesně 8 desítkových číslic ASCII, po nichž následuje vlnovka (`~`), například `12345678~`.

Je-li předpona přítomna, odstraní se a uloží jako `CorrelationInfo` ve vráceném `ParseResult`. Všechny další stupně pracují s takto očištěnými užitečnými daty. Chybí-li předpona, vstup projde beze změny.

Podrobnosti viz [Identifikátor korelace](#identifikátor-korelace).

---

### Stupeň 1 — směrování vstupu

Po odstranění korelace `GaiaParser` ověří, zda (očištěný) vstup začíná **identifikátorem symboliky AIM**: tříznakovou předponou tvaru `]` + písmeno ASCII + číslice ASCII (například `]C1` pro GS1-128, `]d2` pro GS1 DataMatrix, `]e0` pro GS1 DataBar / GS1 Composite).

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

Nepodporuje-li nosič dat AI GS1 (například poštovní čárový kód), rozbor se okamžitě zastaví s chybou `GE-D002`.

---

### Stupeň 2 — skladba

Provádí se vždy. Skládá se ze dvou dílčích kroků:

**2a. Rozdělení na lexémy (`AISyntaxParser`)**
- Načte délku kódu AI z prvních dvou znaků podle tabulky předpon GS1 (GS1 General Specifications, tabulka 7-5).
- Identifikátory AI pevné délky načtou ze vstupu přesný počet bajtů.
- Identifikátory AI proměnné délky se načítají až po znak GS nebo po konec vstupu.
- U vícesložkových AI se blok hodnoty rozdělí na úseky, po jednom na každou složku.

**2b. Ověření struktury (`SyntaxValidator`)**
- Odhalí opakované AI (`GE-S004`).
- Ověří povinné závislosti mezi AI, například AI `02`, který vyžaduje AI `37` (`GE-S005`).
- Ověří vzájemně se vylučující dvojice AI (`GE-S006`).

Chyby tohoto stupně mají úroveň `SYNTAX_ERROR` (rozdělení na lexémy) nebo `INTEGRITY_ERROR` (struktura). Objeví-li se **byť jediná** chyba — z rozdělení na lexémy či ze struktury —, řetěz se zastaví a stupně obsahu a výkladu se vynechají.

---

### Stupeň 3 — obsah

Provádí se pouze tehdy, nevzniknou-li na stupni 2 žádné chyby (ani z rozdělení na lexémy, ani ze struktury). Řetěz se uplatní na každý prvek (každý krok se provede jen tehdy, nevznikly-li v předchozím chyby):

| Krok | Validátor | Kódy chyb |
|---|---|---|
| Ověření regulárním výrazem | `RegexValidator` | `GE-C001` |
| Znaková sada a formát složek | `ComponentValidator` | `GE-C005` + kódy formátu podle podmínky (`GE-C054`–`GE-C115`) |
| Kontrolní číslice / kontrolní znak | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Vlastní významové ověření | `ContentValidatorRegistry` | kódy obsahu podle podmínky (`GE-C116`–`GE-C170`) |

Chyby tohoto stupně mají úroveň `FORMAT_ERROR` nebo `DATA_ERROR`, s jedinou výjimkou:
kontroly předčíslí společnosti GS1 u AI nesoucích klíč GS1 jsou pouze informativní a mají úroveň `WARNING` (viz
[Přehled chyb](#přehled-chyb)), takže nerozpoznané předčíslí společnosti samo o sobě
výsledek neplatným nečiní.

---

### Stupeň 4 — výklad

Provádí se pouze v režimu `INTERPRETATION` a jen tehdy, nenese-li žádný prvek chybu z dřívějšího stupně. `InterpretationEngine` obohatí každý prvek o označená metadata:

- Data přeformátovaná jako `dd/mm/rrrr`
- Rozklad kontrolní číslice GTIN a vyhledání předčíslí společnosti GS1
- Názvy zemí podle ISO 3166
- Názvy a značky měn podle ISO 4217
- Dekódované desetinné částky
- Části HRI (výkladu srozumitelného člověku)

Výsledky se připojují jako záznamy `GS1AIInterpretation` ke každému `GS1AIObjectElement`.

---

## Nastavení rozboru (`ParseConfig`)

`GaiaParser` nabízí právě dva vstupní body:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` běží s **výchozím nastavením**: režim `INTERPRETATION`, data ve vzestupném pořadí (`dd/mm/rrrr`) s oddělovačem `/` a čtyřmístným rokem a chybová hlášení v **angličtině**. Chcete-li kterékoli z toho změnit — včetně režimu rozboru —, sestavte `ParseConfig` jeho plynulým tvůrcem a použijte dvouargumentovou variantu.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Všechny výčty voleb jsou v `GaiaConstants`.

### Volby

| Metoda tvůrce | Výčet (`GaiaConstants`) | Výchozí | Účinek |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Hloubka řetězu — viz [Režimy rozboru](#režimy-rozboru). |
| `language(...)`      | `Language`      | `ENGLISH`        | Jazyk chybových hlášení, popisků výkladu **a** popisů AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Pořadí částí data: `LITTLE` (`dd/mm/rrrr`), `MIDDLE` (`mm/dd/rrrr`), `BIG` (`rrrr/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Znak mezi částmi data: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) nebo `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) nebo `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Vynechá strukturní kontrolu „vyžaduje“ (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Vynechá strukturní kontrolu „vylučuje“ (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / název třídy | žádný | Kód přepisující surový vstup před rozborem — dva [vestavěné modifikátory](#vestavěné-modifikátory) a vše, co napíšete sami. Viz [Modifikátory vstupu](#modifikátory-vstupu). |

Čtyři volby data působí pouze na naformátované řetězce dat, které vytvářejí obohacovače výkladu (v režimu `INTERPRETATION`); ověřování nemění. Hodnoty tvůrce lze vynechat — každá nenastavená volba (nebo ta, jíž se předá `null`) si ponechá výchozí hodnotu.

### Lokalizovaná hlášení a popisky

`language(...)` volí jazyk pro **tři** druhy textu srozumitelného člověku: chybová hlášení, popisky výkladu (`getLabel()` každého `GS1AIInterpretation`) a popisy AI (`getDescription()` každého `GS1AIObjectElement`).

`GaiaConstants.Language` vymezuje **35 jazyků** pokrývajících nejrozšířenější jazyky světa: angličtinu, francouzštinu, španělštinu, němčinu, italštinu, portugalštinu, nizozemštinu, polštinu, ruštinu, ukrajinštinu, češtinu, švédštinu, čínštinu, japonštinu, korejštinu, arabštinu, indonéštinu, hindštinu, turečtinu, bengálštinu, urdštinu, vietnamštinu, nigerijský pidžin, egyptskou arabštinu, maráthštinu, telugštinu, tamilštinu, kantonštinu, wu, tagalštinu, perštinu, hauštinu, paňdžábštinu, javánštinu a svahilštinu.

Stav překladů (v dodávané podobě):
- **Popisky výkladu** — přeloženy do všech jazyků.
- **Chybová hlášení** — přeložena do všech jazyků.
- **Popisy AI** — přeloženy do všech jazyků kromě angličtiny. Angličtina netvoří samostatný katalog: čte se přímo z pole `description` záznamu AI v souboru `gs1-application-identifiers.jsonld`, k němuž se každý popis AI nakonec uchyluje.

Nigerijský pidžin (`NIGERIAN_PIDGIN`), kreolština na anglickém základě, záměrně používá anglický text pro popisky výkladu a chybová hlášení. Popisy AI jsou výjimkou z této výjimky: jsou přeloženy do skutečného pidžinu, místo aby přebíraly angličtinu, protože katalogy popisů AI vznikly nezávisle na katalozích popisků a hlášení. Strojové překlady je vhodné nechat ověřit rodilými mluvčími, než se na ně v provozu spolehnete.

Každé hlášení, popisek či popis chybějící v katalogu daného jazyka se nahradí anglickou podobou. Jazyky psané zprava doleva (arabština, urdština, egyptská arabština, perština) jsou správně uloženy jako řetězce; jejich zobrazení zprava doleva je věcí prezentační vrstvy.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Popisky výkladu se lokalizují stejně (hodnoty zůstávají nezměněny — mění se pouze popisky):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Popisy AI se lokalizují stejně (nelokalizuje se pouze `getTitle()`, například `"GTIN"`):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formátování dat

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Modifikátory vstupu

**Modifikátor vstupu** je kód, který přepíše surový vstupní řetězec dříve, než jej Gaia rozebere. Modifikátory existují kvůli vstupům, které přicházejí již pokažené: snímač nahrazující oddělovač GS tisknutelným zástupným znakem, mezivrstva balící užitečná data do vlastní předpony, nadřazený systém převádějící vše na velká písmena. Namísto předzpracování každého řetězce na každém místě volání (a nenápadné chyby v jednom z nich) vyhlaste normalizaci jednou v `ParseConfig` a nechte ji uplatnit analyzátor.

Modifikátory se spouštějí hned na začátku `GaiaParser.parse(...)` — před odstraněním identifikátoru korelace, před rozpoznáním identifikátoru symboliky AIM, před řetězem GS1. Vše další už vidí jen přepsaný řetězec. **Ve výchozím nastavení není nastaveno nic**, ani oba [vestavěné modifikátory](#vestavěné-modifikátory) — zapínáte je výslovně v každém `ParseConfig`.

**Rozhraní:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Vestavěné modifikátory

Hlavní soubor jar obsahuje dva modifikátory v balíčku `tools.pantheum.gaia.modifier.custom`. Pokrývají dva nejčastější způsoby, jimiž užitečná data GS1 přicházejí pokažená — vytištěné závorky HRI pokládané za data a nadbytečné mezery —, takže běžné případy si vlastní třídu nevyžádají:

| Třída | `getName()` | Co dělá |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Odstraní závorky HRI kolem každého AI (`(01)…(10)…`) a obnoví oddělovač FNC1, který zastupovaly. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Odstraní všechny mezery (`0x20`) z řetězce prvků AI. |

Jsou to běžné implementace `ModifierInterface` bez jakéhokoli zvláštního postavení — zapisují se, řadí, hlásí a selhávají přesně jako ty vaše:

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

Oba jsou bezstavové a bezpečné pro vlákna, takže lze sdílet jedinou instanci, a na oba se lze odkázat plně určeným názvem třídy při nastavení z vnějšího zdroje (viz [Zápis modifikátorů](#zápis-modifikátorů)).

#### `ModifierRemoveAIBrackets`

Výklad GS1 srozumitelný člověku tiskne každý AI v závorkách — `(01)09521234543213(10)ABC123` — čistě z typografické úmluvy. Snímač nebo mezivrstva nastavená na vydávání HRI předá tyto závorky jako data a modul rozdělení na lexémy si s nimi neví rady.

Odstranit závorky je jen polovina práce. V HRI právě otevírací závorka *následujícího* AI vyznačuje konec předchozí hodnoty, takže v závorkové podobě AI proměnné délky žádný FNC1 nepotřebuje. Odstraňte závorky bez rozmyslu a tato hranice zmizí:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Modifikátor proto **znovu vkládá FNC1 na každé hranici, jejíž předcházející AI má proměnnou délku**, a obnovuje tak přesně to, co závorky kódovaly:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Délka se vyhledává ve vlastním rejstříku analyzátoru `AiDefinitionRegistry`, takže se ošetří všechny AI proměnné délky, a nikoli natvrdo zapsaný seznam. Tři případy zůstávají záměrně nedotčeny: hodnota, která již končí na FNC1 (zdroj vydávající obě úmluvy nedostane druhý oddělovač), kód v závorkách, který není známým AI (neznámý AI nic neříká o své vlastní délce), a poslední AI v řetězci.

Přepis je **idempotentní** — jeho opětovné uplatnění na vlastní výstup nic nezmění —, a je tedy bezpečný ve smíšeném proudu, v němž závorky nese jen část vstupů.

> **Omezení.** `(` a `)` jsou samy o sobě platnými datovými znaky GS1 a vzor se omezuje na `\((\d{2,4})\)`. Hodnota, která náhodou obsahuje dvou- až čtyřmístné číslo v závorkách, by o ně rovněž přišla. Uplatňujte to výhradně na zdroj používající závorkovou úmluvu HRI, nikoli na hodnoty se skutečnými závorkami.

#### `ModifierRemoveSpaces`

Některé snímače, mezivrstvy a linky tisku etiket vkládají do jinak správně utvořeného řetězce prvků nadbytečné mezery: aby doplnily pole pevné šířky, oddělily čitelné skupiny nebo zalomily dlouhou hodnotu. Modul rozdělení na lexémy každou z nich pokládá za data, čímž poškozuje hodnotu, v níž mezera stojí, a u AI proměnné délky posouvá vše následující.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Odstraňuje se pouze ASCII `0x20`. Ostatní bílé znaky zůstávají na místě — tabulátor například leží mimo kódovatelnou znakovou sadu GS1, takže jej analyzátor ohlásí jako `GE-S008`, místo aby jej mlčky odstranil.

> **Omezení.** Mezera (`0x20`) patří do neměnné znakové sady GS1, takže číslo šarže či zákaznické číslo výrobku ji zcela oprávněně obsahovat může. Modifikátor nadbytečnou mezeru od skutečné nerozezná; uplatňujte jej výhradně na zdroj, o němž víte, že uvnitř svých hodnot AI mezery nepoužívá.

#### Předpony se přeskakují, nikoli přepisují

Modifikátory se spouštějí dříve, než analyzátor cokoli odstranil, takže surový vstup může ještě nést identifikátor korelace, identifikátor symboliky AIM a ukazatel ECI. Oba vestavěné modifikátory zjistí začátek řetězce prvků AI pomocí vlastní logiky analyzátoru z `CorrelationIdParser` a `DataCarrierParser`, přepisují teprve od tohoto místa a výsledek připojí zpět k **nedotčené** předponě:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Nosiče EAN/UPC, jejichž hodnota se doplňuje na GTIN-14 (`isRequiresGtinPadding()`), se přeskakují úplně: jejich užitečná data jsou čistě číselnou hodnotou čárového kódu bez struktury AI, kde závorky ani mezery nemohou mít význam.

#### Pořadí: nejprve mezery, potom závorky

Používají-li se oba, **zapište `ModifierRemoveSpaces` jako první**. Rozpoznání závorek závisí na poloze: `( 01 )` s mezerami neodpovídá vzoru `\((\d{2,4})\)`, takže závorky přetrvají a oddělovač, který zastupovaly, se nikdy neobnoví.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Napsání modifikátoru

Napište si vlastní, nevyhovuje-li ani jeden z vestavěných — rozhraní tvoří jediná metoda.

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

Závisí-li přepis na nastavení rozboru, překryjte místo toho dvouargumentovou variantu:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Smlouva:

| Pravidlo | Podrobnost |
|---|---|
| Bezstavový a bezpečný pro vlákna | Pro každou třídu se ukládá do mezipaměti jediná instance sdílená všemi rozbory. |
| Veřejný bezparametrový konstruktor | Nutný pouze tehdy, uvádí-li se modifikátor názvem třídy. |
| Ošetřit vstup `null` a prázdný vstup | Analyzátor je před spuštěním řetězce neodfiltruje. |
| Návrat `null` znamená „beze změny“ | Zachová se předchozí hodnota. Vraťte `input` beze změny, není-li modifikátor použitelný. |
| Raději vrátit vstup beze změny než vyvolat výjimku | Modifikátor, který vyvolá výjimku, rozbor přeruší — viz [Ošetření selhání](#ošetření-selhání-modifikátoru). |
| `getName()` | Překryjte ji, chcete-li určit název hlášený v `ModifierInfo`; výchozí je prostý název třídy. |

### Zápis modifikátorů

Modifikátory se spouštějí v pořadí, v němž byly přidány, a každý dostane výstup předchozího. Zapisujte je jako instance, plně určeným názvem třídy nebo seznamem jednoho či druhého:

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

[Vestavěné modifikátory](#vestavěné-modifikátory) se uvádějí stejně jako ty vaše — **vždy úplným názvem**. Vyhledávání podle krátkého názvu ani podle přezdívky pro ně neexistuje; `ModifierRegistry` rozhoduje o každém modifikátoru, dodaném i vlastním, podle úplného názvu třídy.

Názvy rozhoduje `ModifierRegistry`: každou třídu jednou vytvoří jejím bezparametrovým konstruktorem a instanci uloží do mezipaměti pro každé pozdější nastavení, jež tutéž třídu jmenuje. K rozhodnutí dochází **při sestavování nastavení**, takže název, který nelze najít, který neuskutečňuje `ModifierInterface` nebo jehož instanci nelze vytvořit, vyvolá `IllegalArgumentException` právě tam — a nikoli mlčky až při rozboru. Modifikátor, který nelze sestavit odrazem (například takový, jenž nese vloženou závislost), lze zapsat předem, aby zůstal dostupný pod svým názvem:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Zjištění, co modifikátor udělal

Jsou-li nastaveny modifikátory, `ParseResult.getPayload()` odráží **změněný** vstup. Původní se uchovává v `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` hlásí `getName()` každého modifikátoru: výchozí je prostý název třídy, oba vestavěné modifikátory jej však překrývají — řetězec z těchto dvou tedy hlásí zobrazované názvy, nikoli názvy tříd:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` vrací `null`, nebyl-li nastaven žádný modifikátor. Proběhly-li modifikátory, ale každý vrátil vstup beze změny, údaje jsou k dispozici a `isModified()` má hodnotu `false` — v `getAppliedModifiers()` jsou uvedeny pouze modifikátory, které vstup skutečně změnily.

### Ošetření selhání modifikátoru

Modifikátor, který vyvolá výjimku, rozbor přeruší. Výjimka se zabalí do `GaiaModifierException` s uvedením viníka a výsledek nese vnitřní chybu `GE-I001`, v jejímž hlášení se tento název opakuje; `getPayload()` hlásí nezměněný vstup. Rozbor záměrně **nepokračuje** s napůl přepsaným řetězcem: normalizační krok, který by selhal mlčky, by dával výsledky vypadající platně, avšak získané z nesprávného vstupu.

---

## Režimy rozboru

Každý režim pojmenovává nejhlubší [stupeň řetězu](#zpracovatelský-řetěz-rozboru), který provádí; všechny předchozí stupně proběhnou také.

| Režim | Sahá po | Odpovídá na otázku |
|---|---|---|
| `DATA_CARRIER` | Stupeň 1 (směrování vstupu) | Která symbolika tato data nesla? |
| `SYNTAX` | Stupeň 2 (skladba) | Jsou kódy AI a délky správně utvořeny? |
| `CONTENT` | Stupeň 3 (obsah) | Jsou hodnoty platnými daty GS1? |
| `INTERPRETATION` | Stupeň 4 (výklad) | Co hodnoty znamenají? |

### Režim DATA_CARRIER

Zastaví se po stupni 1 — ověří identifikátor symboliky AIM a určí symboliku, do řetězu rozboru AI však nevstoupí. Hodí se k rozpoznání symboliky a nasměrování zpracování bez nákladů na úplné ověření.

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

**Použijte, když:** vaše aplikace potřebuje rozpoznat druh čárového kódu dříve, než rozhodne, jak užitečná data zpracovat — například aby data nasměrovala různým obslužným rutinám pro symboliky 1D a 2D. Pro takové směrování dejte přednost typovanému [`DataCarrierType`](#datacarrierentry-a-datacarriertype) (`getDataCarrier().getDataCarrierType()`) před porovnáváním řetězců s `getName()`.

---

### Režim SYNTAX

Zastaví se po stupni 2. Hodí se k předběžnému strukturnímu výběru bez nákladů na ověření obsahu.

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

**Použijte, když:** chcete ověřit, že kódy AI a délky dat jsou správně utvořeny, dříve než se pustíte do úplného ověřování, nebo když zpracováváte velké objemy, v nichž jsou chyby obsahu vzácné.

---

### Režim CONTENT

Zastaví se po stupni 3.

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

> Většina AI nesmí stát samostatně: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) a `21`
> (SERIAL) každý *vyžaduje* identifikační klíč, jako je AI `01`, v témže řetězci
> prvků; vynechání GTIN ve výše uvedeném příkladu by selhalo již na stupni 2 s kódem `GE-S005`, aniž by
> se vůbec dostalo k ověření obsahu. Nastavte `skipRequiresCheck(true)` v
> `ParseConfig`, chcete-li rozebírat úryvky, které své průvodní AI záměrně vynechávají.

**Použijte, když:** potřebujete vědět, zda je snímaná hodnota plně v souladu s GS1, dříve než ji použijete v obchodním procesu — bez režie obohacení výkladem.

---

### Režim INTERPRETATION (výchozí)

Provede celý řetěz až po stupeň 4. Je to výchozí režim při volání `parse(String)` bez argumentu režimu. Obohacují se pouze prvky, které bezchybně prošly ověřením obsahu.

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

**Ukázkový výstup:**
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

**Ukázka peněžní částky (AI 3932 — cena s kódem měny ISO):**
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

**Použijte, když:** stavíte prezentační vrstvy, nástroje pro kontrolu etiket nebo jakékoli rozhraní, které potřebuje čitelný rozklad hodnot AI.

---

## Identifikátor korelace

Některé pracovní postupy předsazují surovému vstupu GS1 vlastní osmimístný identifikátor korelace, aby bylo možné svázat události snímání s relací či transakcí. Formát je tento:

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

Vlnovka (`~`) je oddělovač. **Nepatří** k obsahu GS1 — odstraňuje se dříve, než jakýkoli rozbor GS1 začne.

### Pravidla rozpoznání

Předpona se rozpozná, začíná-li vstup přesně 8 desítkovými číslicemi ASCII (`0`–`9`), po nichž bezprostředně následuje `~`. Není-li devátý znak `~` nebo není-li některý z prvních 8 znaků číslicí, pokládá se vstup za běžný obsah GS1 bez předpony korelace.

### Přístup k identifikátoru korelace

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

### Spojení s identifikátorem symboliky AIM

Předpona korelace může stát před identifikátorem symboliky AIM. Analyzátor tento případ zvládá průhledně:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Třída uskutečnění:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** kóduje jednu či více hodnot AI přímo do stavby adresy URL HTTP(S), a umožňuje tak identifikátory fyzických výrobků řešitelné na webu. GAIA uskutečňuje standard *GS1 Digital Link Standard: URI Syntax* (vydání 1.7.0) pro **nekomprimované** URI.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` rozpoznává URI Digital Link samočinně: každý vstup začínající na `http://` nebo `https://` se nasměruje do `GS1DLParser`, který provede tytéž stupně obsahu a výkladu jako řetěz řetězců prvků.

### Stavba URI a úlohy AI

Každý AI v URI Digital Link plní jednu ze tří úloh, dostupnou u každého `GS1AIObjectElement` přes `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Úloha | Umístění | Příklad |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | První dvojice `/ai/hodnota` v cestě (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Další dvojice v cestě, seřazené podle primárního klíče (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parametry dotazu s výhradně číselnými klíči (§4.10) | `?17=271231` |

Vynucovaná strukturní pravidla (`DLPathRules`):
- Právě **jeden** primární identifikační klíč v cestě; další klíče je nutné zakódovat jako datové atributy v dotazu.
- Kvalifikátory klíče musí být primárním klíčem připuštěny a stát v předepsaném pořadí. Nepovinné kvalifikátory lze vynechat, ale ty, které *přítomny jsou*, musí předepsané pořadí dodržet — viz [Pořadí kvalifikátorů](#pořadí-kvalifikátorů).
- Před primárním klíčem mohou stát libovolné vlastní úseky cesty (například `/products/au/01/...`); získáte je přes `getDigitalLinkInfo().getCustomPathStem()`.
- Nečíselné klíče dotazu (`linkType`, `context`, parametry rozšíření jako `23P`) se přeskakují; výhradně číselné klíče musí být platnými AI označenými `validAsDataAttribute`.
- Znaky hodnot zakódované procenty se dekódují; AI `(03)` a `(8014)` nejsou dovoleny.

Primární klíče a jejich přípustné posloupnosti kvalifikátorů jsou **řízeny daty** z vymezení AI — příznakem `gs1DigitalLinkPrimaryKey` a atributem `gs1DigitalLinkQualifiers` —, a nikoli zapsány natvrdo.

Každé porušení stavby i vstup, který není adresou URL, vytvoří strukturní chybu Digital Link (`GE-L001`–`GE-L014`, jeden kód na podmínku). Rozložená metadata adresy URL (`scheme`, `domain`, `path`, `customPathStem`, `query` a objekt `java.net.URL`) zůstávají dostupná přes `getDigitalLinkInfo()` i při strukturních chybách.

### Pořadí kvalifikátorů

Pro každý primární klíč uvádí `gs1DigitalLinkQualifiers` jednu či více **seřazených** posloupností kvalifikátorů. V rámci posloupnosti je AI v hranatých závorkách **nepovinný** a AI bez závorek **povinný**, po vzoru zápisu `[cpv-comp]` z ABNF v §4.9. Posloupnosti jednoho primárního klíče jsou vzájemně se vylučující možnosti.

GTIN (`01`) například vymezuje dvě posloupnosti:

| Cesta | Posloupnost | Význam |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — každý nepovinný, avšak v tomto pevném pořadí |
| upui-path | `235` | TPX (povinný); GTIN + TPX = UPUI |

Takže `/01/09506000134352/10/LOT-ABC/21/SER` je platné (LOT před SER, CPV vynechán), `/01/.../21/SER/10/LOT-ABC` se **odmítne** (porušené pořadí) a `/01/09506000134352/235/2ABC456` patří k upui-path. Kontrola pořadí je porovnáním podposloupnosti se zachováním pořadí: nepovinné AI lze tedy vynechat, nikdy je však nelze přehodit.

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

**Třída uskutečnění:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Práce s výsledky

### ParseResult

Výsledek nejvyšší úrovně, který vrací `GaiaParser.parse()`.

| Metoda | Vrací | Popis |
|---|---|---|
| `isValid()` | `boolean` | `true`, nejsou-li chyby na žádné úrovni. Varování platnost neovlivňují. Vždy `true`, je-li `getAiObject()` roven `null`. |
| `getPayload()` | `String` | Vstupní řetězec po odstranění předpony korelace — a poté, co jej přepsaly [modifikátory vstupu](#modifikátory-vstupu), byly-li nějaké. |
| `getPayloadContent()` | `String` | Užitečná data bez identifikátoru symboliky AIM a bez předpony ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (nosič dat odmítnutý jako nepatřící ke GS1, například nosič Code 39 `]A0`) nebo `UNABLE_TO_DETERMINE_CONTENT` (je-li `aiObject` roven `null`, například v režimu `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Nastavená hloubka řetězu (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Nejhlubší stupeň, jehož rozbor skutečně dosáhl — viz níže. |
| `isParseComplete()` | `boolean` | `true`, dosáhl-li rozbor požadované hloubky (`achieved == requested`). Nezávislé na `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Všechny rozpoznané AI. `null` v režimu `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Všechny chyby jiné úrovně než WARNING (na úrovni objektu i všech prvků). |
| `getWarnings()` | `List<GaiaError>` | Všechna upozornění úrovně WARNING (na úrovni objektu i všech prvků). |
| `hasWarnings()` | `boolean` | `true`, byla-li vydána upozornění úrovně WARNING. |
| `getIssues()` | `List<GaiaError>` | Chyby a varování dohromady. |
| `hasDataCarrier()` | `boolean` | `true`, byl-li rozpoznán identifikátor symboliky AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata symboliky, nebo `null`, nebyl-li nosič určen. |
| `hasEci()` | `boolean` | `true`, byl-li z užitečných dat odstraněn ukazatel ECI. |
| `getEci()` | `EciEntry` | Metadata kódování ECI, nebo `null`. |
| `hasCorrelationId()` | `boolean` | `true`, byla-li v původním vstupu předpona korelace `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | Získaný identifikátor korelace, nebo `null`, nebyl-li žádný. |
| `isInputModified()` | `boolean` | `true`, změnil-li [modifikátor vstupu](#modifikátory-vstupu) vstup. |
| `getModifierInfo()` | `ModifierInfo` | Co řetězec modifikátorů udělal — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null`, nebyl-li nastaven žádný modifikátor. |
| `getTiming()` | `ProcessingTiming` | Skutečné měření času rozboru — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null`, nevytvořil-li výsledek `GaiaParser`. |
| `getVersion()` | `String` | Verze knihovny, která výsledek vytvořila. |

#### Požadovaný a dosažený režim rozboru

Řetěz prochází žebříkem **SYNTAX → CONTENT → INTERPRETATION** a při chybách se zastaví předčasně, takže skutečně *dosažený* režim může být mělčí než *požadovaný*. `getAchievedParseMode()` ukazuje, kam došel:

| Požadováno | Co se stane | Dosaženo | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | chyba **skladby nebo struktury** zastaví rozbor po rozdělení na lexémy | `SYNTAX` | `false` |
| `INTERPRETATION` | chyba **obsahu** (chybný formát či kontrolní číslice) zabrání obohacení | `CONTENT` | `false` |
| `CONTENT` | stupeň obsahu vždy proběhne až do konce (chyby se zaznamenávají, nejsou osudné) | `CONTENT` | `true` |
| kterýkoli (bezchybný vstup) | řetěz dosáhne požadované hloubky | = požadováno | `true` |
| `DATA_CARRIER` | nosič ověřen; obsah AI se nerozebíral | `DATA_CARRIER` | `true` |
| kterýkoli | nosič dat je odmítnut před rozborem AI (například nosič `]A0` nepatřící ke GS1) | `SYNTAX` | `false` |

`isParseComplete()` je nezávislé na `isValid()`: rozbor `CONTENT` u GTIN s chybnou kontrolní číslicí je **úplný** (stupeň obsahu proběhl) a zároveň **neplatný** (kontrolní číslice nesouhlasí). Ptejte se `isParseComplete()` „došel řetěz tak hluboko, jak jsem žádal?“ a `isValid()` „jsou data správně utvořena?“.

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

Soubor rozpoznaných prvků AI.

| Metoda | Popis |
|---|---|
| `getAis()` | Všechny instance `GS1AIObjectElement` v pořadí ze vstupu. |
| `get(String aiCode)` | První prvek odpovídající zadanému kódu AI, nebo `null`. |
| `contains(String aiCode)` | `true`, je-li přítomen AI s tímto kódem. |
| `size()` | Počet rozpoznaných AI. |
| `isValid()` | `true`, nejsou-li chyby na úrovni objektu a nenese-li chyby žádný prvek. |
| `toHriString()` | Řetězec HRI, například `(01)09506000134352 (17)261231`. |
| `toElementString()` | Surový řetězec prvků — bez závorek, s FNC1 za každým prvkem proměnné délky — například `010950600013435210LOT-ABC<GS>17271231`. Vrací `null`, je-li `isValid()` roven `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK`, je-li `hasDigitalLink()` pravdivé, jinak `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true`, byl-li vstupem URI GS1 Digital Link s primárním identifikačním klíčem. Správně utvořená adresa URL bez primárního klíče `getDigitalLinkInfo()` stále poskytuje, zde však vrací `false`. |
| `getCanonicalDigitalLink()` | Kanonický URI GS1 Digital Link (§4.12) v doméně `https://id.gs1.org` — primární klíč a kvalifikátory jako úseky cesty, datové atributy jako parametry dotazu seřazené podle klíče AI —, nebo `null`, není-li primární klíč přítomen. |
| `getDigitalLinkInfo()` | Metadata rozkladu URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), nebo `null`, nejde-li o Digital Link. |
| `getAllErrors()` | Chyby na úrovni objektu + všechny chyby prvků (jiné než WARNING). |
| `getAllWarnings()` | Varování na úrovni objektu + všechna varování prvků. |
| `getAllIssues()` | Vše dohromady. |

---

### GS1AIObjectElement

Jediná rozpoznaná instance AI.

| Metoda | Popis |
|---|---|
| `getAi()` | Kód AI, například `"01"`, `"3102"`. |
| `getTitle()` | Datový název GS1, například `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Úplný popis AI podle GS1, **lokalizovaný do jazyka rozboru** (například `"Global Trade Item Number (GTIN)"` v angličtině). Není-li překlad k dispozici, použije se anglický text z vymezení AI. |
| `getFormatString()` | Popisovač formátu zahrnující AI *i* jeho data, například `"N2+N14"` pro AI `01`, `"N2+X..20"` pro AI `10`, `"N4+N3+N..15"` pro AI `3932`. |
| `getValue()` | Surová hodnota dat získaná z řetězce prvků. |
| `isFixedLength()` | `true`, má-li AI pevnou délku dat. |
| `getPosition()` | Posun ve znacích (od nuly) v původním vstupu. |
| `getGS1ComponentValues()` | Výseky hodnoty po složkách (u vícesložkových AI). |
| `getErrors()` | Chyby na úrovni prvku, jiné než WARNING. |
| `getWarnings()` | Upozornění úrovně WARNING pro daný prvek. |
| `getIssues()` | Chyby a varování prvku dohromady. |
| `hasErrors()` | `true`, jsou-li připojeny chyby jiné než WARNING. |
| `hasWarnings()` | `true`, jsou-li připojena upozornění úrovně WARNING. |
| `getInterpretations()` | Záznamy `GS1AIInterpretation` (naplňují se v režimu INTERPRETATION). |
| `getInterpretation(String type)` | První výklad odpovídající zadanému klíči typu z `GS1Constants_Enricher`, nebo `null`. |
| `getDigitalLinkAIType()` | Úloha prvku v Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), nebo `null` u vstupu v podobě řetězce prvků. |
| `hasDigitalLinkAIType()` | `true`, byla-li úloha Digital Link přiřazena. |

---

### GaiaError

Neměnná chyba ověření nebo upozornění.

| Metoda | Popis |
|---|---|
| `getId()` | Identifikátor v katalogu, například `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` nebo `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` nebo `INTERNAL`. |
| `getCode()` | Krátký strojově čitelný kód. |
| `getAi()` | Kód AI, který chybu způsobil, nebo `null` u chyb na úrovni objektu. |
| `getMessage()` | Čitelné hlášení s doplněnými hodnotami. |
| `getPosition()` | Posun ve znacích (od nuly) v původním vstupu. |

---

### GS1AIInterpretation

Jediná označená část výkladu, připojovaná k `GS1AIObjectElement` v režimu `INTERPRETATION`.

| Metoda | Popis |
|---|---|
| `getType()` | Strojově čitelný klíč typu, například `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Ve všech jazycích stejný. |
| `getLabel()` | Popisek srozumitelný člověku, **lokalizovaný do jazyka rozboru** (například `"Date"` / `"GS1 company prefix"` v angličtině). |
| `getValue()` | Získaná či obohacená hodnota, například `"31/12/2026"`, `"9506000"`. Nelokalizuje se. |

---

### DataCarrierEntry a DataCarrierType

Nese-li vstup identifikátor symboliky AIM, vrací `ParseResult.getDataCarrier()` objekt `DataCarrierEntry` popisující symbol, který data přenesl. Tento záznam je konkrétním záznamem rejstříku pro rozpoznaný identifikátor AIM; `DataCarrierType` je výčet známý v době překladu, k němuž záznam náleží.

#### DataCarrierEntry

Metadata jednoho rozpoznaného identifikátoru symboliky AIM (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metoda | Popis |
|---|---|
| `getAimCodeId()` | Rozpoznaný identifikátor symboliky AIM, například `"]C1"`. |
| `getName()` | Čitelný název konkrétního symbolu, například `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Podrobnější popis nosiče. |
| `getType()` | Strukturní druh nosiče jako řetězec (odráží `getDataCarrierType().getCategory()`). |
| `getStandard()` | Standard symboliky, je-li zaznamenán. |
| `getDataCarrierType()` | Typovaný `DataCarrierType` tohoto záznamu — vhodnější pro programové směrování. |
| `isGs1Capable()` | `true`, může-li nosič obsahovat data GS1 (řetězce prvků AI či Digital Link). |
| `isGs1AICapable()` | `true`, může-li nosič obsahovat řetězce prvků AI GS1. |
| `isGs1DigitalLinkCapable()` | `true`, může-li nosič obsahovat URI GS1 Digital Link. |
| `isEciCapable()` | `true`, podporuje-li nosič ukazatel ECI. |
| `isRequiresGtinPadding()` | `true` u nosičů EAN/UPC/ITF, jejichž číselná hodnota se před rozborem AI doplňuje na GTIN-14. |

#### DataCarrierType

Výčet druhů nosičů dat známý v době překladu, indexovaný identifikátorem symboliky AIM přiděleným v ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Znak za `]` (*znak kódu*) volí rodinu; většině rodin odpovídá jediná konstanta pokrývající všechny modifikátory (`ITF` pokrývá `]I0`–`]I2`; `EAN_UPC` pokrývá EAN-13, UPC-A, UPC-E a EAN-8). Tam, kde GS1 vyhrazuje modifikátor pro data AI, tvoří tato obměna vlastní konstantu — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — odlišnou od běžného protějšku. Chybí-li identifikátor AIM nebo označuje-li neznámý nosič, je druhem `UNKNOWN`.

| Metoda | Popis |
|---|---|
| `getCategory()` | Obecná kategorie `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` nebo `OTHER`. |
| `getCodeChar()` | Znak kódu AIM označující rodinu, například `"Q"` pro QR Code; `null` u `UNKNOWN`. |
| `getDisplayName()` | Čitelný název *druhu* (může být širší než `DataCarrierEntry.getName()` — například `"EAN-13 / UPC-A / UPC-E / EAN-8"` oproti `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` u konstant, které vždy označují data AI GS1: čtyř obměn vyhrazených GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) a `GS1_DATABAR`, jenž ke GS1 patří ze své podstaty, neboť každý modifikátor `]e` označuje GS1 DataBar. Užší než `DataCarrierEntry.isGs1AICapable()` — i běžný `QR_CODE` může nést data AI GS1. |
| `static forAimCodeId(String)` | Určí druh přímo z identifikátoru AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); vrací `UNKNOWN` u chybějícího, chybně utvořeného či nerozpoznaného identifikátoru. |

Směrování podle druhu, a nikoli podle názvu — například k oddělení lineárních symbolů (Code 128) od dvourozměrných (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` pokrývá pouze maticové a bodové symboly; vrstvené lineární nosiče (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) patří k `STACKED_LINEAR`, ačkoli se jim běžně říká
„dvourozměrné“ čárové kódy. Chcete-li s oběma nakládat jako s jednou skupinou — například abyste rozhodli,
zda je namísto laserového snímače zapotřebí snímač obrazový —, ověřujte příslušnost ke kterékoli z obou kategorií.

> Určení druhu vyžaduje, aby byl identifikátor symboliky AIM ve snímaných datech přítomen; bez něj je `getDataCarrier()` roven `null` a druhem je `UNKNOWN`. Nastavte snímač tak, aby předponu identifikátoru AIM předával.

---

## Přehled chyb

| Kód | Úroveň | Stupeň | Význam |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Neznámá předpona AI — délku dat nelze určit |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Vstup je příliš krátký pro načtení úplného kódu AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Zkrácená hodnota — méně znaků, než AI vyžaduje |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Opakovaný aplikační identifikátor v řetězci prvků |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Chybí povinná závislost AI |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Vyloučená dvojice AI — dva AI, které nemohou stát vedle sebe |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Neočekávané selhání rozdělení na lexémy |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Znak mimo kódovatelnou sadu GS1 v řetězci prvků |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Chybí povinný oddělovač FNC1 za AI proměnné délky |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Nadbytečná data nad maximem všech složek |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Oddělovač FNC1 za AI pevné délky ve vnitřní poloze |
| `GE-W002` | WARNING | SYNTAX | FNC1 na konci řetězce prvků (pouze informativní) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Porušení stavby URI Digital Link — jeden kód na podmínku (chybně utvořený URI, schéma, uzel, pořadí kvalifikátorů, zakázaný AI, chybějící primární klíč (`GE-L013`), více primárních klíčů (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Hodnota nevyhovuje regulárnímu výrazu svého AI |
| `GE-C003` | DATA_ERROR | CONTENT | Selhání ověření kontrolní číslice |
| `GE-C004` | DATA_ERROR | CONTENT | Selhání ověření dvojice kontrolních znaků |
| `GE-C005` | FORMAT_ERROR | CONTENT | Hodnota složky obsahuje znak mimo povolenou sadu |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Selhání formátu složek — jeden kód na podmínku ověření (viz `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Selhání vlastního významového ověření — jeden kód na podmínku ověření (viz `content/validator/`). **Výjimky:** níže uvedených 14 kontrol předčíslí společnosti GS1 má úroveň `WARNING` a `GE-C168` (nerozpoznaný číselný kód země podle ISO 3166-1) má `FORMAT_ERROR`. |
| Kontroly předčíslí společnosti GS1 | WARNING | CONTENT | Klíč nezačíná rozpoznaným předčíslím společnosti GS1 — u AI nesoucích klíč GS1: `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Pouze informativní — platnost neovlivňuje. |
| `GE-C169` | DATA_ERROR | CONTENT | Selhání kontrolní číslice IMEI (Luhn) u AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Selhání kontrolní číslice EID (Luhn) u AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Nerozpoznaný identifikátor symboliky AIM |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Nosič určen, nepodporuje však ani řetězce prvků AI GS1, ani URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Neočekávaná vnitřní chyba |

> **Známá vada ve vypisování hlášení.** Předlohy katalogu uzavírají doplňované
> hodnoty do zdvojených apostrofů po vzoru MessageFormat (`''{value}''`), avšak
> `ErrorRegistry` doplňuje hodnoty prostým `String.replace`, takže zdvojení přežije až do
> `getMessage()` — nyní uvidíte `value ''09506000134351''` tam, kde texty hlášení
> uváděné v této příručce ukazují `value '09506000134351'`. Týká se to každého hlášení,
> jež hodnotu uzavírá do uvozovek, ve všech 35 jazykových katalozích. Chybová hlášení nerozebírejte;
> porovnávejte `getId()` / `getCode()`.

---

## Bezpečnost pro vlákna

`GaiaParser` je po vytvoření bezpečný pro vlákna. Jedinou instanci lze sdílet mezi vlákny a volat souběžně. Doporučeným postupem je vytvořit jednu instanci při spuštění aplikace a znovu ji používat:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` je neměnný a stejně bezpečný ke sdílení. Jedinou povinnost stran bezpečnosti pro vlákna, kterou za vás knihovna převzít nemůže, mají [modifikátory vstupu](#modifikátory-vstupu): pro každý modifikátor se ukládá do mezipaměti jediná instance sdílená všemi souběžnými rozbory, a implementace proto musí být bezstavové.

---

## Příloha A — řetězcové konstanty AI

`GS1Constants_AICodes` (v balíčku `tools.pantheum.gaia.gs1.constants`) vyhlašuje konstantu typu `String` pro každý aplikační identifikátor, který GAIA rozpoznává. Používejte tyto konstanty namísto zapisování kódů AI jako řetězců:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Každá konstanta obsahuje textovou podobu kódu AI (například `AI_01_GTIN = "01"`).

### Identifikace a sériové značení

| AI | Konstanta | Popis |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Sériový kód přepravní jednotky (SSCC). |
| `01` | `AI_01_GTIN` | Globální obchodní identifikační číslo (GTIN). |
| `02` | `AI_02_CONTENT` | Globální obchodní identifikační číslo (GTIN) obsažených obchodních jednotek. |
| `03` | `AI_03_MTO_GTIN` | Identifikace zboží vyrobeného na zakázku (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Číslo šarže nebo dávky. |
| `20` | `AI_20_VARIANT` | Interní varianta produktu. |
| `21` | `AI_21_SERIAL` | Sériové číslo. |
| `22` | `AI_22_CPV` | Spotřebitelská varianta produktu. |
| `235` | `AI_235_TPX` | Třetí stranou řízené sériové rozšíření globálního obchodního identifikačního čísla (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Dodatečná identifikace produktu přidělená výrobcem. |
| `241` | `AI_241_CUST_PART_NO` | Číslo dílu zákazníka. |
| `242` | `AI_242_MTO_VARIANT` | Číslo varianty zboží na zakázku. |
| `243` | `AI_243_PCN` | Číslo obalové komponenty. |
| `250` | `AI_250_SECONDARY_SERIAL` | Sekundární sériové číslo. |
| `251` | `AI_251_REF_TO_SOURCE` | Odkaz na zdrojový subjekt. |
| `253` | `AI_253_GDTI` | Globální identifikátor typu dokumentu (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Rozšiřující komponenta globálního lokalizačního čísla (GLN). |
| `255` | `AI_255_GCN` | Globální číslo kupónu (GCN). |
| `30` | `AI_30_VAR_COUNT` | Proměnlivý počet kusů (produkt s proměnlivým množstvím). |
| `37` | `AI_37_COUNT` | Počet obchodních jednotek nebo jejich částí v logistické jednotce. |

### Data a časy

| AI | Konstanta | Popis |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Datum výroby (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Datum splatnosti (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Datum balení (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Datum minimální trvanlivosti (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Datum prodeje do (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Datum spotřeby (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Datum a čas doručení nejdříve od (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Datum a čas doručení nejpozději do (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Datum vydání (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Datum a čas spotřeby (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Datum prvního zmrazení (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Datum sklizně/úlovku (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Datum testování do (YYMMDD[hhmm]). |

### Množství a míra — proměnná míra (metrická)

Čtyřmístné rodiny AI `310n`–`369n` kódují množství s proměnnou mírou. Třetí číslice volí druh míry; **čtvrtá číslice** (`n`, 0–5) je počet předpokládaných desetinných míst — `AI_3102_NET_WEIGHT_KG` tedy znamená čistou hmotnost v kg se 2 desetinnými místy.

| Rodina | Vzor konstanty (`n` = číslice desetinných míst) | Popis |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Čistá hmotnost, kilogramy (produkt s proměnlivým množstvím). |
| `311n` | `AI_311n_LENGTH_M` | Délka nebo první rozměr, metry (produkt s proměnlivým množstvím). |
| `312n` | `AI_312n_WIDTH_M` | Šířka, průměr nebo druhý rozměr, metry (produkt s proměnlivým množstvím). |
| `313n` | `AI_313n_HEIGHT_M` | Hloubka, tloušťka, výška nebo třetí rozměr, metry (produkt s proměnlivým množstvím). |
| `314n` | `AI_314n_AREA_M` | Plocha, čtvereční metry (produkt s proměnlivým množstvím). |
| `315n` | `AI_315n_NET_VOLUME_L` | Čistý objem, litry (produkt s proměnlivým množstvím). |
| `316n` | `AI_316n_NET_VOLUME_M` | Čistý objem, krychlové metry (produkt s proměnlivým množstvím). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistická hmotnost, kilogramy. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Délka nebo první rozměr, metry. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Šířka, průměr nebo druhý rozměr, metry. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Hloubka, tloušťka, výška nebo třetí rozměr, metry. |
| `334n` | `AI_334n_AREA_M_LOG` | Plocha, čtvereční metry. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistický objem, litry. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistický objem, krychlové metry. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramy na čtvereční metr. |

### Množství a míra — proměnná míra (imperiální / USA)

| Rodina | Vzor konstanty (`n` = číslice desetinných míst) | Popis |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Čistá hmotnost, libry (produkt s proměnlivým množstvím). |
| `321n` | `AI_321n_LENGTH_IN` | Délka nebo první rozměr, palce (produkt s proměnlivým množstvím). |
| `322n` | `AI_322n_LENGTH_FT` | Délka nebo první rozměr, stopy (produkt s proměnlivým množstvím). |
| `323n` | `AI_323n_LENGTH_YD` | Délka nebo první rozměr, yardy (produkt s proměnlivým množstvím). |
| `324n` | `AI_324n_WIDTH_IN` | Šířka, průměr nebo druhý rozměr, palce (produkt s proměnlivým množstvím). |
| `325n` | `AI_325n_WIDTH_FT` | Šířka, průměr nebo druhý rozměr, stopy (produkt s proměnlivým množstvím). |
| `326n` | `AI_326n_WIDTH_YD` | Šířka, průměr nebo druhý rozměr, yardy (produkt s proměnlivým množstvím). |
| `327n` | `AI_327n_HEIGHT_IN` | Hloubka, tloušťka, výška nebo třetí rozměr, palce (produkt s proměnlivým množstvím). |
| `328n` | `AI_328n_HEIGHT_FT` | Hloubka, tloušťka, výška nebo třetí rozměr, stopy (produkt s proměnlivým množstvím). |
| `329n` | `AI_329n_HEIGHT_YD` | Hloubka, tloušťka, výška nebo třetí rozměr, yardy (produkt s proměnlivým množstvím). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistická hmotnost, libry. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Délka nebo první rozměr, palce. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Délka nebo první rozměr, stopy. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Délka nebo první rozměr, yardy. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Šířka, průměr nebo druhý rozměr, palce. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Šířka, průměr nebo druhý rozměr, stopy. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Šířka, průměr nebo druhý rozměr, yardy. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Hloubka, tloušťka, výška nebo třetí rozměr, palce. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Hloubka, tloušťka, výška nebo třetí rozměr, stopy. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Hloubka, tloušťka, výška nebo třetí rozměr, yardy. |
| `350n` | `AI_350n_AREA_IN` | Plocha, čtvereční palce (produkt s proměnlivým množstvím). |
| `351n` | `AI_351n_AREA_FT` | Plocha, čtvereční stopy (produkt s proměnlivým množstvím). |
| `352n` | `AI_352n_AREA_YD` | Plocha, čtvereční yardy (produkt s proměnlivým množstvím). |
| `353n` | `AI_353n_AREA_IN_LOG` | Plocha, čtvereční palce. |
| `354n` | `AI_354n_AREA_FT_LOG` | Plocha, čtvereční stopy. |
| `355n` | `AI_355n_AREA_YD_LOG` | Plocha, čtvereční yardy. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Čistá hmotnost, trojské unce (produkt s proměnlivým množstvím). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Čistá hmotnost (nebo objem), unce (produkt s proměnlivým množstvím). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Čistý objem, kvarty (produkt s proměnlivým množstvím). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Čistý objem, americké galony (produkt s proměnlivým množstvím). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistický objem, kvarty. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistický objem, americké galony. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Čistý objem, krychlové palce (produkt s proměnlivým množstvím). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Čistý objem, krychlové stopy (produkt s proměnlivým množstvím). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Čistý objem, krychlové yardy (produkt s proměnlivým množstvím). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistický objem, krychlové palce. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistický objem, krychlové stopy. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistický objem, krychlové yardy. |

### Ceny a peněžní částky

Čtvrtá číslice (`n`) kóduje počet předpokládaných desetinných míst. Její přípustný rozsah
se u jednotlivých rodin liší — viz sloupec `n`.

| Rodina | Vzor konstanty (`n` = číslice desetinných míst) | `n` | Popis |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Splatná částka nebo hodnota kupónu, místní měna. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Splatná částka s ISO kódem měny. |
| `392n` | `AI_392n_PRICE` | 0–9 | Splatná částka, jednotná měnová oblast (produkt s proměnlivým množstvím). |
| `393n` | `AI_393n_PRICE` | 0–9 | Splatná částka s ISO kódem měny (produkt s proměnlivým množstvím). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Procentuální sleva kupónu. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Splatná částka za měrnou jednotku, jednotná měnová oblast (produkt s proměnlivým množstvím). |

### Místo a odeslání

| AI | Konstanta | Popis |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Číslo objednávky zákazníka. |
| `401` | `AI_401_GINC` | Globální identifikační číslo zásilky (GINC). |
| `402` | `AI_402_GSIN` | Globální identifikační číslo dodávky (GSIN). |
| `403` | `AI_403_ROUTE` | Směrovací kód. |
| `410` | `AI_410_SHIP_TO_LOC` | Globální lokalizační číslo (GLN) místa odeslání/doručení. |
| `411` | `AI_411_BILL_TO` | Globální lokalizační číslo (GLN) plátce / adresáta faktury. |
| `412` | `AI_412_PURCHASE_FROM` | Globální lokalizační číslo (GLN) prodejce (nakoupeno od). |
| `413` | `AI_413_SHIP_FOR_LOC` | Globální lokalizační číslo (GLN) pro přeposlání zásilky/dodávky. |
| `414` | `AI_414_LOC_NO` | Identifikace fyzického místa - globální lokalizační číslo (GLN). |
| `415` | `AI_415_PAY_TO` | Globální lokalizační číslo (GLN) fakturující strany. |
| `416` | `AI_416_PROD_SERV_LOC` | Globální lokalizační číslo (GLN) místa výroby nebo poskytování služby. |
| `417` | `AI_417_PARTY` | Globální lokalizační číslo (GLN) strany. |
| `420` | `AI_420_SHIP_TO_POST` | PSČ místa odeslání/doručení v rámci jednoho poštovního úřadu. |
| `421` | `AI_421_SHIP_TO_POST` | PSČ místa odeslání/doručení s ISO kódem země. |
| `422` | `AI_422_ORIGIN` | Země původu obchodní jednotky. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Země prvotního zpracování. |
| `424` | `AI_424_COUNTRY_PROCESS` | Země zpracování. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Země demontáže. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Země pokrývající celý výrobní řetězec. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Správní jednotka země původu. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Název společnosti místa odeslání/doručení. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Kontaktní osoba místa odeslání/doručení. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Adresa místa odeslání/doručení, řádek 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Adresa místa odeslání/doručení, řádek 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Městská část místa odeslání/doručení. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Obec místa odeslání/doručení. |
| `4306` | `AI_4306_SHIP_TO_REG` | Region místa odeslání/doručení. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Kód země místa odeslání/doručení. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Telefonní číslo místa odeslání/doručení. |
| `4309` | `AI_4309_SHIP_TO_GEO` | GEO poloha místa odeslání/doručení. |
| `4310` | `AI_4310_RTN_TO_COMP` | Název společnosti pro vrácení. |
| `4311` | `AI_4311_RTN_TO_NAME` | Kontaktní osoba pro vrácení. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Adresa pro vrácení, řádek 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Adresa pro vrácení, řádek 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Městská část pro vrácení. |
| `4315` | `AI_4315_RTN_TO_LOC` | Obec pro vrácení. |
| `4316` | `AI_4316_RTN_TO_REG` | Region pro vrácení. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Kód země pro vrácení. |
| `4318` | `AI_4318_RTN_TO_POST` | PSČ pro vrácení. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Telefonní číslo pro vrácení. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Popis kódu služby. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Příznak nebezpečného zboží. |
| `4322` | `AI_4322_AUTH_LEAVE` | Oprávnění ponechat zásilku bez podpisu. |
| `4323` | `AI_4323_SIG_REQUIRED` | Příznak požadavku na podpis. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maximální teplota ve stupních Fahrenheita (v setinách stupně). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maximální teplota ve stupních Celsia (v setinách stupně). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimální teplota ve stupních Fahrenheita (v setinách stupně). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimální teplota ve stupních Celsia (v setinách stupně). |

### Vlastnosti výrobku a sledovatelnost

| AI | Konstanta | Popis |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Skladové číslo NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Klasifikace jatečně upravených těl a masných dílů UN/ECE. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktivní účinnost (potence). |
| `7005` | `AI_7005_CATCH_AREA` | Oblast úlovku. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Druh pro rybářské účely. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Typ rybářského zařízení. |
| `7010` | `AI_7010_PROD_METHOD` | Způsob produkce. |
| `7020` | `AI_7020_REFURB_LOT` | ID šarže renovace. |
| `7021` | `AI_7021_FUNC_STAT` | Funkční stav. |
| `7022` | `AI_7022_REV_STAT` | Stav revize. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Globální identifikátor jednotlivého aktiva (GIAI) sestavy. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Číslo zpracovatele s třímístným kódem země ISO (10 pozic). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC s rozšířením 1 a indexem dovozce. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Typ přepravní jednotky UN/CEFACT. |

### Národní čísla úhrad ve zdravotnictví (NHRN)

| AI | Konstanta | Popis |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Národní číslo úhrady ve zdravotnictví (NHRN) - Německo PZN. |
| `711` | `AI_711_NHRN_CIP` | Národní číslo úhrady ve zdravotnictví (NHRN) - Francie CIP. |
| `712` | `AI_712_NHRN_CN` | Národní číslo úhrady ve zdravotnictví (NHRN) - Španělsko CN. |
| `713` | `AI_713_NHRN_DRN` | Národní číslo úhrady ve zdravotnictví (NHRN) - Brazílie DRN. |
| `714` | `AI_714_NHRN_AIM` | Národní číslo úhrady ve zdravotnictví (NHRN) - Portugalsko AIM. |
| `715` | `AI_715_NHRN_NDC` | Národní číslo úhrady ve zdravotnictví (NHRN) - USA NDC. |
| `716` | `AI_716_NHRN_AIC` | Národní číslo úhrady ve zdravotnictví (NHRN) - Itálie AIC. |
| `717` | `AI_717_NHRN_SRN` | Národní číslo úhrady ve zdravotnictví (NHRN) - Kostarika, číslo hygienického registru. |

### Zdravotnictví, GMN, HIDRI, CPID, osobní údaje

| AI | Konstanta | Popis |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Odkaz na certifikaci (10 pozic). |
| `7240` | `AI_7240_PROTOCOL` | ID protokolu. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Typ nosiče AIDC. |
| `7242` | `AI_7242_VCN` | Číslo správy verzí (VCN). |
| `7250` | `AI_7250_DOB` | Datum narození (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Datum a čas narození (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biologické pohlaví. |
| `7253` | `AI_7253_FAMILY_NAME` | Příjmení osoby. |
| `7254` | `AI_7254_GIVEN_NAME` | Křestní jméno osoby. |
| `7255` | `AI_7255_SUFFIX` | Přípona jména osoby. |
| `7256` | `AI_7256_FULL_NAME` | Celé jméno osoby. |
| `7257` | `AI_7257_PERSON_ADDR` | Adresa osoby. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Pořadí narození dítěte. |
| `7259` | `AI_7259_BABY` | Příjmení dítěte. |
| `8001` | `AI_8001_DIMENSIONS` | Návinové výrobky (šířka, délka, průměr jádra, směr, počet spojů). |
| `8002` | `AI_8002_CMT_NO` | Identifikátor mobilního telefonu. |
| `8003` | `AI_8003_GRAI` | Globální identifikátor vratného obalu/aktiva (GRAI). |
| `8004` | `AI_8004_GIAI` | Globální identifikátor jednotlivého aktiva (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Cena za měrnou jednotku. |
| `8006` | `AI_8006_ITIP` | Identifikace jednotlivého kusu obchodní jednotky (ITIP). |
| `8007` | `AI_8007_IBAN` | Mezinárodní číslo bankovního účtu (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Datum a čas výroby (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Opticky čitelný indikátor senzoru. |
| `8010` | `AI_8010_CPID` | Identifikátor komponenty/dílu (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Sériové číslo identifikátoru komponenty/dílu (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Verze softwaru. |
| `8013` | `AI_8013_GMN` | Globální číslo modelu (GMN). |
| `8014` | `AI_8014_MUDI` | Identifikátor registrace vysoce individualizovaného zařízení (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Globální číslo servisního vztahu (GSRN) k identifikaci vztahu mezi organizací poskytující služby a poskytovatelem služby. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Globální číslo servisního vztahu (GSRN) k identifikaci vztahu mezi organizací poskytující služby a příjemcem služby. |
| `8019` | `AI_8019_SRIN` | Číslo instance servisního vztahu (SRIN). |
| `8020` | `AI_8020_REF_NO` | Referenční číslo platebního dokladu. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identifikace kusů obchodní jednotky (ITIP) obsažených v logistické jednotce. |
| `8030` | `AI_8030_DIGSIG` | Digitální podpis (DigSig). |
| `8040` | `AI_8040_IMEI` | Mezinárodní identifikátor mobilního zařízení (IMEI). |
| `8041` | `AI_8041_IMEI2` | Druhý mezinárodní identifikátor mobilního zařízení (IMEI2). |
| `8042` | `AI_8042_ESIM` | Číslo vestavěné SIM karty (eSIM). |
| `8043` | `AI_8043_PSIM` | Číslo fyzické SIM karty. |
| `8110` | `AI_8110` | Identifikace kódu kupónu pro použití v Severní Americe. |
| `8111` | `AI_8111_POINTS` | Věrnostní body kupónu. |
| `8112` | `AI_8112` | Identifikace kódu kupónu z pozitivního souboru nabídek pro použití v Severní Americe. |
| `8200` | `AI_8200_PRODUCT_URL` | URL rozšířeného obalu. |

### Vnitřní / firemní použití

| AI | Konstanta | Popis |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informace vzájemně dohodnutá mezi obchodními partnery. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Vnitřní informace společnosti (9 pozic). |

---

## Příloha B — konstanty klíčů výkladu

Je-li `GaiaParser.parse()` volán s `ParseMode.INTERPRETATION`, může každý `GS1AIObjectElement` nést seznam objektů `GS1AIInterpretation` vytvořených oborově zaměřenými obohacovači. Používejte konstanty z `GS1Constants_Enricher` (v balíčku `tools.pantheum.gaia.gs1.constants`) jako klíče k vyhledání konkrétních hodnot výkladu:

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

Zobrazované popisky **nejsou** konstantami — leží v lokalizovaných katalozích v `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` a jsou indexovány konstantou typu. `GS1AIInterpretation.getLabel()` vrací popisek pro jazyk rozboru (viz [Lokalizovaná hlášení a popisky](#lokalizovaná-hlášení-a-popisky)) a uchyluje se k angličtině, vynechá-li katalog daný klíč. Sloupec „Zobrazovaný popisek“ níže uvádí český text v podobě, v jaké je v katalogu dodáván; samotné klíče typu jsou ve všech jazycích stejné — porovnávejte tedy vždy klíč, nikdy popisek.

### Datum a čas

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `DATE_VALUE` | Datum | AI data (11–17, 7003, 7006, 7011 atd.) |
| `DATE_FORMAT` | Formát data | AI data |
| `TIME_VALUE` | Čas | AI nesoucí čas (7003, 7011, 8008 atd.) |
| `TIME_FORMAT` | Formát času | AI nesoucí čas |
| `DATETIME_VALUE` | Datum a čas | AI data a času |
| `DATETIME_FORMAT` | Formát data a času | AI data a času |

### Datum sklizně

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Datum začátku sklizně | AI 7007 |
| `HARVEST_END_DATE` | Datum konce sklizně | AI 7007 (nepovinný konec rozsahu) |
| `HARVEST_DATE_RANGE` | Rozsah dat sklizně | AI 7007 |

### Předčíslí společnosti GS1

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Předčíslí společnosti GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Kód člena GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Členská organizace GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Typ GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Úroveň balení | AI 01 |
| `GTIN_CHECK_DIGIT` | Kontrolní číslice | AI 01, 02 |

### SSCC

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Rozšiřující číslice | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Sériová reference | AI 00 |
| `SSCC_CHECK_DIGIT` | Kontrolní číslice | AI 00 |

### Země (ISO 3166)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Kód země (číselný) | AI jediné země (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Kód země (alfa-2) | AI zemí v kódu alfa-2 |
| `COUNTRY_NAME` | Název země | AI jediné země |
| `COUNTRY_LIST` | Země | AI 423 — všechny názvy spojené, například `Australia, New Zealand` |

AI 423 (země prvního zpracování) může nést až pět zemí, a proto vydává
**číslovanou dvojici pro každou zemi** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — a po nich jediný souhrn
`COUNTRY_LIST`. Skládejte tyto klíče z konstant `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` a pořadového čísla počítaného od 1, nebo jednoduše projděte `getInterpretations()`; klíče
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` bez přípony se pro AI 423 **nevydávají**.

### Měna (ISO 4217)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Kód měny | AI částky s měnou (391n, 393n) |
| `CURRENCY_ALPHA` | Písmenný kód měny | AI částky s měnou |
| `CURRENCY_NAME` | Název měny | AI částky s měnou |

### Teplota

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `TEMPERATURE` | Teplota | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Jednotka teploty | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Teplota (formátovaná) | AI 4330–4333 |

### Pohlaví (ISO 5218)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `SEX_CODE` | Kód pohlaví | AI 7252 |
| `SEX_DESCRIPTION` | Popis pohlaví | AI 7252 |

### Vodní druhy (FAO)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Kód druhu | AI 7008 |
| `SPECIES_SCIENTIFIC` | Vědecký název | AI 7008 |
| `SPECIES_ENGLISH` | Obecný název | AI 7008 |
| `SPECIES_FAMILY` | Čeleď | AI 7008 |
| `SPECIES_ORDER` | Řád | AI 7008 |

### Skladové číslo NATO (NSN)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `NSN_FSG` | Zásobovací skupina | AI 7001 |
| `NSN_FSG_NAME` | Název zásobovací skupiny | AI 7001 |
| `NSN_FSCG` | Zásobovací třída | AI 7001 |
| `NSN_FSCG_NAME` | Název zásobovací třídy | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Kód země | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Země | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Kód země ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategorie NCS | AI 7001 |
| `NSN_NIIN` | Národní číslo položky | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Návinové výrobky

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Šířka role (mm) | AI 8001 |
| `ROLL_LENGTH` | Délka role (m) | AI 8001 |
| `CORE_DIAMETER` | Průměr jádra (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Kód směru navíjení | AI 8001 |
| `WINDING_DIRECTION` | Směr navíjení | AI 8001 |
| `SPLICES` | Spoje | AI 8001 |

### IBAN

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Kód země | AI 8007 |
| `IBAN_COUNTRY_NAME` | Země | AI 8007 |
| `IBAN_CHECK_DIGITS` | Kontrolní číslice | AI 8007 |
| `IBAN_CHECK_VALID` | Kontrola | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Sériové číslo | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Kontrolní číslice | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Vydávající orgán | AI 8040, 8041 |

Patnáct číslic se rozkládá na `[ TAC (8) ][ sériové číslo (6) ][ Luhnova kontrolní číslice (1) ]`, přičemž
RBI tvoří první 2 číslice TAC — `IMEI_RBI` je tedy předponou `IMEI_TAC`, a nikoli
samostatným úsekem. `IMEI_FORMATTED` podává obvyklé zobrazovací seskupení GSMA
`AA-BBBBBB-CCCCCC-D` (například `49-015420-323751-8`), které dělí TAC na hranici
RBI; dřívější seskupení `6-2-6-1`, jež řezalo tam, kde začínal zrušený Final Assembly
Code, se nevydává.

`IMEI_RBI_NAME` převádí RBI na název přidělujícího orgánu pomocí `ImeiRbiData` a
**připojuje se jako poslední a jen tehdy, je-li kód v tabulce uveden**. Tato tabulka zahrnuje tři skupiny:

- **Aktuálně přidělující** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF a dále `99`
  Global Hexadecimal Administrator a `98` (vyhrazeno).
- **Zkušební rozsahy** — `00` a `02`–`09`, které označují zkušební IMEI, nikoli skutečné přidělení.
  Dotazujte se na ně přes `ImeiRbiData.isTestCode(code)`.
- **Již nepřidělující** — historické orgány jako `49` (BZT/BAPT, Německo), `44`
  (BABT, Spojené království) či `91` (MSAI, Indie). Dotazujte se na ně přes `ImeiRbiData.isNoLongerAllocating(code)`.
  Zařízení s těmito kódy jsou běžná a zůstávají v provozu; skončilo pouze přidělování nových kódů,
  jde tedy o údaj pro výkaznictví, nikdy o známku platnosti.

Chybějící `IMEI_RBI_NAME` znamená „tento RBI v naší tabulce není“, a **nikoli** „neplatný IMEI“:
tabulka je sestavena ze zveřejněného seznamu RBI, a nikoli přímo od GSMA, může tedy
zaostávat za nedávno jmenovanými orgány. Z její nepřítomnosti nevyvozujte žádný závěr o platnosti;
RBI není kontrolním znakem. I kód procházející seznam výkladů musí
její nepřítomnost snést, místo aby přistupoval podle polohy.

### Identifikátory SIM (EID / ICCID)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategorie odvětví | AI 8042 |
| `EID_BODY` | Tělo EID | AI 8042 |
| `EID_CHECK_DIGIT` | Kontrolní číslice | AI 8042 |
| `ICCID_BODY` | Tělo ICCID | AI 8043 |
| `ICCID_EXTENSION` | Rozšíření | AI 8043 |

`SIM_MII` nese **dvě** úvodní číslice (`89`) — dvojici, kterou ITU-T E.118 vyhrazuje
telekomunikacím. Samotná norma ISO/IEC 7812 vymezuje MII jako **pouze první číslici**, a proto
`SIM_MII_NAME` určuje kategorii z této úvodní `8` pomocí `Iso7812Data`, což dává
„Healthcare, telecommunications and other future industry assignments“. U správně utvořeného
EID je tato hodnota tedy stálá; uvádí se kvůli sledovatelnosti vůči normě, nikoli jako
rozlišovací znak. `Iso7812Data.nameForCode(digit)` přijímá jedinou číslici a
`nameForIdentifier(prefix)` přijímá delší předponu a čte její úvodní číslici.

`SIM_MII_NAME` vydává pouze `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
poskytuje `SIM_MII` bez kategorie.

### Odkaz na certifikaci

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Pořadové číslo | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Kód certifikačního schématu | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Certifikační schéma | AI 7230–7239 |
| `CERT_REFERENCE` | Reference certifikace | AI 7230–7239 |

### GS1 UIC

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `UIC_CODE` | Kód UIC | AI 7040 |
| `UIC_EXTENSION_1` | Rozšíření 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Index dovozce | AI 7040 |

### Pořadí narození novorozence

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Pozice narození | AI 7258 |
| `BIRTH_TOTAL` | Celkem narození | AI 7258 |
| `BIRTH_SEQUENCE` | Pořadí narození | AI 7258 |

### Globální číslo modelu (GMN)

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Reference modelu | AI 8013 |
| `GMN_CHECK_PAIR` | Kontrolní pár | AI 8013 |

### HIDRI

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Reference zařízení | AI 8014 |
| `HIDRI_CHECK_PAIR` | Kontrolní pár | AI 8014 |

### CPID

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Reference komponenty a dílu | AI 8010–8011 |

### Desetinné a měrné hodnoty

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Desetinná hodnota | Číselné AI s předpokládanými desetinnými místy (31xx–36xx) |
| `DECIMAL_AMOUNT` | Částka | AI ceny (390n–395n) |
| `DECIMAL_PERCENTAGE` | Procento | AI 394n |
| `DECIMAL_PLACES` | Desetinná místa | Spolu s `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Formát procenta | AI 394n |
| `ISO_UNIT_CODE` | Kód jednotky ISO | AI míry |
| `ISO_UNIT_NAME` | Název jednotky ISO | AI míry |
| `MONETARY_AMOUNT` | Peněžní částka | AI ceny |
| `MONETARY_AMOUNT_DISPLAY` | Peněžní částka (formátovaná) | AI ceny |

### Zeměpisné souřadnice

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `LATITUDE` | Zeměpisná šířka | AI 4309 |
| `LONGITUDE` | Zeměpisná délka | AI 4309 |
| `GEO_COORDINATES` | Zeměpisné souřadnice | AI 4309 |
| `LATITUDE_DMS` | Zeměpisná šířka (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Zeměpisná délka (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Zeměpisné souřadnice (DMS) | AI 4309 |

### Způsob výroby

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Kód metody výroby | AI 7010 |
| `PRODUCTION_METHOD` | Metoda výroby | AI 7010 |

### Druh nosiče AIDC

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Kód typu média AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Typ média AIDC | AI 7241 |

### Kus z celku

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Číslo kusu | AI 8006 |
| `PIECE_TOTAL` | Celkem kusů | AI 8006 |
| `PIECE_OF_TOTAL` | Kus z celku | AI 8006 |

### Rozklad na složky

Klíče vydávané deklarativním rozkladem na složky ze souboru `content/ai-content.json`, a nikoli
obohacovačem v Javě: odhalují pojmenované části složené hodnoty AI. Na rozdíl od všech
ostatních klíčů v této příloze **nemají konstantu v `GS1Constants_Enricher`**: porovnávejte
doslovný řetězec nebo čtěte druh přes `GS1AIInterpretation.getType()`.

| Klíč typu | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Kontrolní číslice | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Sériové číslo | AI 253, 255, 8003 |
| `POSTAL_CODE` | PSČ | AI 421 |
| `PROCESSOR_ID` | Identifikátor zpracovatele | AI 7030–7039 |

Všimněte si, že `CHECK_DIGIT` je zde obecným klíčem rozkladu na složky, odlišným od
klíčů vlastních obohacovačům `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` a
`EID_CHECK_DIGIT` uvedených výše.

### Různé

| Konstanta klíče | Zobrazovaný popisek | Vytváří |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Hodnota | Logické AI / příznakové AI (4321–4323) |
| `DECODED_TEXT` | Dekódovaný text | AI volného textu |
