# GaiaParser — Mwanzo wa Haraka

Changanua payload ya msimbo pau wa GS1 kuwa data iliyopangwa, iliyothibitishwa, na inayosomeka
na binadamu ndani ya takriban dakika kumi. Hii ndiyo njia fupi; **[Mwongozo wa GaiaParser kwa
Msanidi](GaiaParser-Swahili.md)** ndio marejeleo kamili, na **[GaiaBuilder](GaiaBuilder-Swahili.md)** hushughulikia
mwelekeo wa kinyume (kujenga mifuatano ya vipengele na Digital Link URI).

## Yaliyomo

1. [1. Ongeza Gaia kwenye mradi wako](#1-ongeza-gaia-kwenye-mradi-wako)
2. [2. Changanua kitu](#2-changanua-kitu)
3. [3. Soma matokeo](#3-soma-matokeo)
4. [4. Shughulikia uchanganuzi ulioshindwa](#4-shughulikia-uchanganuzi-ulioshindwa)
5. [5. Mambo mawili yatakayokuuma](#5-mambo-mawili-yatakayokuuma)
6. [6. Viambishi awali vya kichanganuzi na Digital Link hufanya kazi vyenyewe](#6-viambishi-awali-vya-kichanganuzi-na-digital-link-hufanya-kazi-vyenyewe)
7. [7. Fanya kazi kidogo: hali za uchanganuzi](#7-fanya-kazi-kidogo-hali-za-uchanganuzi)
8. [8. Badilisha lugha na umbizo la tarehe](#8-badilisha-lugha-na-umbizo-la-tarehe)
9. [9. Safisha ingizo lililochafuka](#9-safisha-ingizo-lililochafuka)
10. [10. Wapi kwenda baadaye](#10-wapi-kwenda-baadaye)

---

## 1. Ongeza Gaia kwenye mradi wako

Gaia haijachapishwa kwenye Maven Central, hivyo jenga core mara moja kisha uisakinishe kwenye
hifadhi yako ya karibu:

```bash
cd gaia && mvn install
```

Kisha itegemee:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Hiyo ndiyo orodha nzima ya utegemezi unayohitaji kuandika. Jar ni nyembamba, hivyo utegemezi
pekee wa Gaia katika kiwango cha compile — `com.fasterxml.jackson.core:jackson-databind` —
huja kwa njia ya mpito; ikiwa ujenzi wako tayari umebainisha toleo la Jackson, hilo ndilo
litakaloshinda na Gaia italitumia. Gaia inalenga **Java 11**, na jar ileile hukimbia bila
mabadiliko kwenye kila JVM ya baadaye.

> Kuruka seti ya majaribio ya core (`mvn install -DskipTests`) hugeuza dakika kadhaa kuwa
> sekunde chache unapoanza.

---

## 2. Changanua kitu

Klasi moja, bila usanidi:

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

`parse(String)` hukimbiza mtiririko **kamili**: sintaksia, uthibitishaji wa maudhui, na tafsiri.
Hilo ndilo chaguo-msingi sahihi — lipunguze baadaye ukipata sababu uliyoipima.

---

## 3. Soma matokeo

`ParseResult.getAiObject()` hushikilia AI zilizotambuliwa. Chukua moja mahususi kwa msimbo
badala ya kwa nafasi:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Kila kipengele hubeba orodha ya **tafsiri** — maana iliyofumbuliwa nyuma ya tarakimu ghafi,
iliyozalishwa na hatua ya tafsiri:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` imetafsiriwa na imekusudiwa kwa uonyeshaji. Ili *kusoma* thamani ndani ya msimbo,
itafute badala yake kwa ufunguo wake wa aina usiobadilika:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI tofauti huzalisha funguo tofauti — GTIN hutoa kiambishi awali cha kampuni yake, aina ya
GTIN na tarakimu ya ukaguzi; bei hutoa sarafu na kiasi cha desimali. Orodha kamili iko katika
[Kiambatisho B](GaiaParser-Swahili.md#kiambatisho-b--konstanti-za-funguo-za-tafsiri), na konstanti zinaishi
katika `GS1Constants_Enricher`. Si kila AI ina tafsiri: kundi/lot ya maandishi huru haina
chochote cha kutolewa, hivyo orodha yake ni tupu.

---

## 4. Shughulikia uchanganuzi ulioshindwa

Payload isiyo halali ni matokeo ya kawaida, si exception — `parse` haitupi kamwe kwa sababu ya
data mbovu za GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Gawa kwenye `getId()`, kamwe si kwenye ujumbe.** Ujumbe umetafsiriwa na maneno yake si
mkataba — na kwa sasa unabeba kasoro inayojulikana ya alama za nukuu (`''` mbili hapo juu),
iliyotajwa katika [Marejeleo ya Hitilafu](GaiaParser-Swahili.md#marejeleo-ya-hitilafu).

Maswali mawili tofauti, mbinu mbili tofauti:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Uchanganuzi huacha kushuka mara hatua inaposhindwa, hivyo tarakimu ya ukaguzi mbovu inamaanisha
utapata hitilafu za uthibitishaji lakini hakuna tafsiri.

### Maonyo hayafanyi matokeo yasiwe halali

Baadhi ya ukaguzi ni ushauri tu. Kiambishi awali cha kampuni GS1 kisichotambuliwa huripotiwa,
lakini payload bado ni imara kimuundo:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Tumia `getIssues()` unapotaka vyote viwili. Ikiwa mtiririko wako wa kazi lazima ukatae viambishi
awali visivyojulikana, kagua `getWarnings()` waziwazi — `isValid()` haitakufanyia hilo.

---

## 5. Mambo mawili yatakayokuuma

### Kitenganishi cha GS, na kwa nini kukiacha ni baya kuliko hitilafu

AI yenye urefu unaobadilika hukimbia hadi **herufi ya GS** (ASCII `0x1D`, iitwayo FNC1 katika
simbolojia za msimbo pau) au mwisho wa mfuatano. AI nyingine inapoifuata, kitenganishi hicho ni
cha lazima:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Kiache na **hutapata** hitilafu — utapata jibu lisilo sahihi lililotolewa kwa uhakika kamili:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` ni `X..20`, hivyo kihalali huimeza `LOT-ABC21SN-98765` na parser haina njia ya kujua
kuwa hilo halikukusudiwa. Hakuna kitu kinachofuata kinachoweza kurekebisha hili, hivyo rekebisha
kitenganishi kwenye chanzo chenyewe: soma baiti za kichanganuzi kama **ISO-8859-1** ili `0x1D`
ibaki, na andika `""` katika literali za string za Java. AI zenye urefu usiobadilika (`01`,
`17`, `3103`) hazihitaji kitenganishi — parser hujua urefu wake.

### AI nyingi haziwezi kusimama peke yake

Kundi/lot, mfululizo, tarehe ya mwisho wa matumizi na wenzao ni *sifa*: GS1 General
Specifications huhitaji zisafiri pamoja na ufunguo wa utambulisho, na Gaia hutekeleza hilo.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Ongeza GTIN nayo hupita. Ikiwa kweli unahitaji kuchanganua kipande — jaribio la kitengo, uchanganuzi
wa sehemu — zima ukaguzi:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Viambishi awali vya kichanganuzi na Digital Link hufanya kazi vyenyewe

Huna haja ya kuiambia Gaia umbo la ingizo — hutambua maumbo yote manne. Ipe tu chochote
kichanganuzi kilichokupa.

**Kiambishi awali cha AIM Code ID** hutambulisha simbolojia na huondolewa kiotomatiki:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** hupitia uthibitishaji na uboreshaji uleule:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Kwa kuwa maumbo yote mawili hufikia `GS1AIObject` ileile, msimbo unaotumia matokeo ya
uchanganuzi hauhitaji kujali ni lipi lililofika — na `toElementString()` /
`getCanonicalDigitalLink()` hubadilisha kati yake.

**Kiambishi awali cha correlation cha tarakimu 8** (`12345678~…`) nacho huondolewa na
kuhifadhiwa kwenye `getCorrelationInfo()`, ikiwa mtiririko wako unakitumia.

---

## 7. Fanya kazi kidogo: hali za uchanganuzi

Chaguo-msingi hufanya kila kitu. Omba kidogo pale unapohitaji sehemu tu ya jibu:

| Hali | Hujibu | Gharama |
|---|---|---|
| `DATA_CARRIER` | Hii ni simbolojia gani? | Rahisi zaidi — hakuna uchanganuzi wa AI kabisa, `getAiObject()` ni `null` |
| `SYNTAX` | Misimbo ya AI na urefu wake vimeundwa vizuri? | Hakuna tarakimu ya ukaguzi, hakuna tafsiri |
| `CONTENT` | Hizi ni data halali za GS1? | Uthibitishaji kamili, hakuna tafsiri |
| `INTERPRETATION` | Ina maana gani? | **Chaguo-msingi** — kila kitu |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Chagua `CONTENT` unapothibitisha kwa wingi na kamwe huonyeshi uchambuzi, na `DATA_CARRIER`
unapohitaji tu kuelekeza uchanganuzi kwa mshughulikiaji sahihi.

---

## 8. Badilisha lugha na umbizo la tarehe

Ujumbe wa hitilafu, lebo za tafsiri na maelezo ya AI vimetafsiriwa katika **lugha 35**; tarehe
huonyeshwa upendavyo. Vyote viko katika `ParseConfig` moja isiyobadilika:

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

Thamani hazitafsiriwi kamwe — ni lebo, maelezo na ujumbe pekee — hivyo `"2026-12-31"` na
`"09506000134352"` vina maana ileile katika kila lugha. Jenga usanidi mara moja wakati wa
kuanza kisha uushiriki; hauubadiliki.

---

## 9. Safisha ingizo lililochafuka

Ikiwa chanzo chako hutoa mabano ya HRI yaliyochapishwa au nafasi za ziada, kuna **input
modifier** mbili zinazokuja na core na hurekebisha payload kabla ya uchanganuzi:

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

Hakuna kilichowashwa kama chaguo-msingi, na zote mbili zina tahadhari — nafasi na mabano ni
herufi halali za data za GS1, hivyo zitumie tu kwa chanzo unachokijua. Angalia
[Modifier za ndani](GaiaParser-Swahili.md#modifier-za-ndani), inayoeleza pia kwa nini kuondoa mabano
lazima kurudishe kitenganishi ambacho mabano yalikimaanisha.

---

## 10. Wapi kwenda baadaye

- **[Mwongozo wa GaiaParser kwa Msanidi](GaiaParser-Swahili.md)** — mtiririko kwa kina, muundo kamili
  wa matokeo, kila msimbo wa hitilafu, na viambatisho vya AI na funguo za tafsiri.
- **[Mwongozo wa GaiaBuilder kwa Msanidi](GaiaBuilder-Swahili.md)** — jenga mifuatano ya vipengele na
  Digital Link URI kutoka jozi za AI na thamani.
- **[Marejeleo ya HTTP ya Gaia API](../../gaia-api-reference.md)** — injini ileile kupitia HTTP,
  ikiwa hupendi kuweka maktaba ndani.
- **[ai-codes.txt](../../ai-codes.txt)** — orodha tambarare ya `(AI) TITLE` kwa utafutaji wa haraka.

### Toleo la mistari mitano

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
