# GaiaParser — శీఘ్ర ప్రారంభం

GS1 బార్‌కోడ్ పేలోడ్‌ను సుమారు పది నిమిషాల్లో నిర్మిత, ధ్రువీకరించిన, మనిషి చదవగల డేటాగా
మార్చండి. ఇది దగ్గరి దారి; **[GaiaParser డెవలపర్ మార్గదర్శి](GaiaParser-Telugu.md)** పూర్తి
సూచిక, మరియు **[GaiaBuilder](GaiaBuilder-Telugu.md)** వ్యతిరేక దిశను (ఎలిమెంట్ స్ట్రింగ్‌లు,
Digital Link URIలు నిర్మించడం) చూసుకుంటుంది.

## విషయ సూచిక

1. [మీ ప్రాజెక్ట్‌కు Gaiaను జోడించండి](#1-మ-పరజకటక-gaiaన-జడచడ)
2. [ఏదైనా పార్స్ చేయండి](#2-ఏదన-పరస-చయడ)
3. [ఫలితాన్ని చదవండి](#3-ఫలతనన-చదవడ)
4. [విఫలమైన పార్సింగ్‌ను చూసుకోండి](#4-వఫలమన-పరసగన-చసకడ)
5. [మిమ్మల్ని తడబడేలా చేసే రెండు విషయాలు](#5-మమమలన-తడబడల-చస-రడ-వషయల)
6. [స్కానర్ ఉపసర్గాలు, Digital Link వాటంతట అవే పని చేస్తాయి](#6-సకనర-ఉపసరగల-digital-link-వటతట-అవ-పన-చసతయ)
7. [తక్కువ పని చేయండి: పార్స్ పద్ధతులు](#7-తకకవ-పన-చయడ-పరస-పదధతల)
8. [భాషనూ తేదీ ఆకృతినీ మార్చండి](#8-భషన-తద-ఆకతన-మరచడ)
9. [చిందరవందర ఇన్‌పుట్‌ను శుభ్రం చేయండి](#9-చదరవదర-ఇనపటన-శభర-చయడ)
10. [ఇక్కడి నుండి ఎక్కడికి](#10-ఇకకడ-నడ-ఎకకడక)

---

## 1. మీ ప్రాజెక్ట్‌కు Gaiaను జోడించండి

Gaia Maven Centralలో ప్రచురితం కాలేదు, కాబట్టి కోర్‌ను ఒకసారి నిర్మించి మీ స్థానిక
రిపాజిటరీలో స్థాపించండి:

```bash
cd gaia && mvn install
```

ఆ తర్వాత దానిపై ఆధారపడండి:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

మీరు రాయాల్సిన ఆధారం అంతే. jar తేలికైనది: Gaia యొక్క ఏకైక కంపైల్-పరిధి ఆధారం —
`com.fasterxml.jackson.core:jackson-databind` — సంక్రమణ ద్వారానే వస్తుంది; మీ బిల్డ్ ఇప్పటికే
ఏదైనా Jackson విడుదలను నిర్ణయించి ఉంటే, ఆ నిర్ణయమే గెలుస్తుంది, Gaia దాన్నే వాడుతుంది. Gaia
**Java 11**ను లక్ష్యంగా చేసుకుంటుంది, అదే jar తర్వాతి ప్రతి JVM విడుదలలోనూ మార్పు లేకుండా
నడుస్తుంది.

> మొదట్లో కోర్ పరీక్షల సముదాయాన్ని దాటవేయడం (`mvn install -DskipTests`) కొన్ని నిమిషాలను
> కొన్ని క్షణాలుగా మారుస్తుంది.

---

## 2. ఏదైనా పార్స్ చేయండి

ఒకే తరగతి, ఎలాంటి అమరికా లేదు:

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

`parse(String)` **పూర్తి** పైప్‌లైన్‌ను నడుపుతుంది: వాక్యనిర్మాణం, విషయ ధ్రువీకరణ, వ్యాఖ్యానం.
ఇదే సరైన డిఫాల్ట్ — దాన్ని కుదించడానికి కొలిచిన కారణం దొరికినప్పుడే కుదించండి.

---

## 3. ఫలితాన్ని చదవండి

`ParseResult.getAiObject()` పరిష్కరించిన AIలను పట్టి ఉంచుతుంది. ఒక నిర్దిష్ట AIని దాని
స్థానం బట్టి కాక కోడ్ బట్టి తీసుకోండి:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

ప్రతి ఎలిమెంటూ ఒక **వ్యాఖ్యాన** జాబితాను మోస్తుంది — ముడి అంకెల వెనుక ఉన్న విప్పిన అర్థం,
దీన్ని వ్యాఖ్యాన దశ తయారుచేస్తుంది:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` స్థానికీకరించబడింది, అది ప్రదర్శన కోసమే. కానీ కోడ్‌లో ఒక విలువను *చదవడానికి*
బదులుగా దాని స్థిర రకం-కీతో వెతకండి:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

వేర్వేరు AIలు వేర్వేరు కీలను ఇస్తాయి — GTIN తన కంపెనీ ఉపసర్గాన్ని, GTIN రకాన్ని, తనిఖీ
అంకెను ఇస్తుంది; ధర కరెన్సీని, దశాంశ మొత్తాన్ని ఇస్తుంది. పూర్తి జాబితా
[అనుబంధం ఆ](GaiaParser-Telugu.md#అనబధ-ఆ--వయఖయన-క-సథరకల)లో ఉంది, స్థిరాంకాలు
`GS1Constants_Enricher`లో ఉన్నాయి. ప్రతి AIకీ వ్యాఖ్యానాలు ఉండవు: బ్యాచ్/లాట్ నంబర్ అనేది
స్వేచ్ఛా పాఠ్యం, దాని నుండి రాబట్టేదేమీ లేదు, కాబట్టి దాని జాబితా ఖాళీగా ఉంటుంది.

---

## 4. విఫలమైన పార్సింగ్‌ను చూసుకోండి

చెల్లని పేలోడ్ అనేది సాధారణ ఫలితమే తప్ప మినహాయింపు కాదు — చెడ్డ GS1 డేటా కోసం `parse`
ఎప్పుడూ మినహాయింపు విసరదు:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()`పై శాఖ చేయండి, సందేశంపై ఎప్పటికీ కాదు.** సందేశాలు స్థానికీకరించబడతాయి, వాటి
పదజాలం ఒప్పందం కాదు — పైగా ప్రస్తుతం వాటిలో తెలిసిన ఉద్ధరణ లోపం ఉంది (పైన కనిపించే జంట
`''`), అది [లోప సూచికలో](GaiaParser-Telugu.md#లప-సచక) నమోదైంది.

రెండు వేర్వేరు ప్రశ్నలు, రెండు వేర్వేరు పద్ధతులు:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

ఒక దశ విఫలమైన వెంటనే పార్సింగ్ మరింత లోతుకు వెళ్ళడం ఆపేస్తుంది, కాబట్టి తప్పు తనిఖీ అంకె
అంటే మీకు ధ్రువీకరణ లోపాలు వస్తాయి కానీ ఏ వ్యాఖ్యానమూ రాదు.

### హెచ్చరికల వల్ల ఫలితం చెల్లనిది కాదు

కొన్ని పరిశీలనలు సూచనాత్మకమే. గుర్తు పట్టని GS1 కంపెనీ ఉపసర్గం నివేదించబడుతుంది, కానీ
పేలోడ్ మాత్రం సరిగానే ఉంటుంది:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

రెండూ కలిపి కావాలంటే `getIssues()` వాడండి. గుర్తు పట్టని ఉపసర్గాలను తిరస్కరించడం మీ
పని-ప్రవాహానికి తప్పనిసరి అయితే, `getWarnings()`ను స్పష్టంగా పరిశీలించండి — `isValid()`
మీ తరఫున దాన్ని చేయదు.

---

## 5. మిమ్మల్ని తడబడేలా చేసే రెండు విషయాలు

### GS విభాజకం, దాన్ని వదిలేయడం లోపం కంటే ఎందుకు దారుణం

వేరియబుల్-పొడవు AI ఒక **GS అక్షరం** వరకూ (ASCII `0x1D`, బార్‌కోడ్ సింబాలజీల్లో దీన్ని FNC1
అంటారు) లేదా స్ట్రింగ్ ముగింపు వరకూ సాగుతుంది. దాని తర్వాత మరో AI వచ్చినప్పుడు ఆ విభాజకం
తప్పనిసరి:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

దాన్ని వదిలేస్తే మీకు లోపం **రాదు** — పూర్తి నమ్మకంతో ఇచ్చిన తప్పు జవాబు వస్తుంది:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` అనేది `X..20`, కాబట్టి మొత్తం `LOT-ABC21SN-98765`ను మింగేయడం సహేతుకమే; అది మీ ఉద్దేశం
కాదని తెలుసుకునే మార్గం పార్సర్‌కు లేదు. తర్వాత దీన్ని ఏదీ తిరిగి తేలేదు, కాబట్టి విభాజకాన్ని
మూలం దగ్గరే సరిచేయండి: `0x1D` నిలిచేలా స్కానర్ బైట్లను **ISO-8859-1**గా చదవండి, Java స్ట్రింగ్
లిటరల్‌లో `""` రాయండి. స్థిర-పొడవు AIలకు (`01`, `17`, `3103`) విభాజకం అక్కరలేదు — వాటి పొడవు
పార్సర్‌కు తెలుసు.

### చాలా AIలు ఒంటరిగా నిలబడవు

బ్యాచ్/లాట్, సీరియల్ నంబర్, గడువు తేదీ వంటివన్నీ *లక్షణాలే*: వాటితో పాటు గుర్తింపు కీ
ఉండాలని GS1 General Specifications కోరుతాయి, Gaia దాన్ని అమలు చేస్తుంది.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

ఒక GTIN జోడిస్తే అది గట్టెక్కుతుంది. నిజంగానే ఒక ముక్కను పార్స్ చేయాల్సి వస్తే — యూనిట్
పరీక్ష, పాక్షిక స్కాన్ — ఆ పరిశీలనను ఆపేయండి:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. స్కానర్ ఉపసర్గాలు, Digital Link వాటంతట అవే పని చేస్తాయి

ఇన్‌పుట్ ఏ రూపంలో ఉందో Gaiaకు చెప్పనవసరం లేదు — నాలుగు రూపాలనూ అది గుర్తిస్తుంది. మీ స్కానర్
ఇచ్చిన దాన్నే నేరుగా పంపండి.

**AIM సింబాలజీ ఐడెంటిఫైయర్ ఉపసర్గం** సింబాలజీని నిర్ణయిస్తుంది, అదే వాటంతట తీసేయబడుతుంది:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** కూడా అదే ధ్రువీకరణ, సుసంపన్నత గుండానే వెళుతుంది:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

రెండు రూపాలూ ఒకే `GS1AIObject`కే చేరతాయి కాబట్టి, స్కాన్‌ను వాడే కోడ్‌కు ఏది వచ్చిందనేది
పట్టించుకోనవసరం లేదు — `toElementString()` / `getCanonicalDigitalLink()` ఒకదాన్ని మరొకటిగా
మారుస్తాయి.

**8-అంకెల సహసంబంధ ఉపసర్గం** (`12345678~…`) కూడా, మీ ప్రవాహం దాన్ని వాడితే, అదే విధంగా
తీసేయబడి `getCorrelationInfo()`లో భద్రపరచబడుతుంది.

---

## 7. తక్కువ పని చేయండి: పార్స్ పద్ధతులు

డిఫాల్ట్ పద్ధతి అన్నీ చేస్తుంది. జవాబులో ఒక భాగమే కావాలనుకున్నప్పుడు తక్కువ అడగండి:

| పద్ధతి | దేనికి జవాబిస్తుంది | ఖర్చు |
|---|---|---|
| `DATA_CARRIER` | ఇది ఏ సింబాలజీ? | అత్యంత చౌక — AI పార్సింగ్ అస్సలు లేదు, `getAiObject()` `null` |
| `SYNTAX` | AI కోడ్‌లు, పొడవులు సరిగా ఉన్నాయా? | తనిఖీ అంకెలు లేవు, వ్యాఖ్యానాలు లేవు |
| `CONTENT` | ఇది చెల్లుబాటయ్యే GS1 డేటానా? | పూర్తి ధ్రువీకరణ, వ్యాఖ్యానం లేకుండా |
| `INTERPRETATION` | దీని అర్థమేమిటి? | **డిఫాల్ట్** — అన్నీ |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

పెద్ద పరిమాణంలో ధ్రువీకరిస్తూ విభజనను ఎప్పుడూ చూపనప్పుడు `CONTENT` ఎంచుకోండి, స్కాన్‌ను సరైన
హ్యాండ్లర్‌కు పంపడమే కావాలంటే `DATA_CARRIER`.

---

## 8. భాషనూ తేదీ ఆకృతినీ మార్చండి

లోప సందేశాలు, వ్యాఖ్యాన లేబుళ్ళు, AI వివరణలు **35 భాషల్లోకి** అనువదించబడ్డాయి; తేదీలను
మీకు నచ్చినట్లు చూపవచ్చు. ఇవన్నీ ఒకే మార్పులేని `ParseConfig`లో ఇమిడిపోతాయి:

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

విలువలు ఎప్పుడూ స్థానికీకరించబడవు — లేబుళ్ళు, వివరణలు, సందేశాలు మాత్రమే — కాబట్టి
`"2026-12-31"`, `"09506000134352"` ప్రతి భాషలోనూ ఒకే అర్థాన్నే ఇస్తాయి. అమరికను మొదట్లో
ఒకసారి నిర్మించి పంచుకోండి; అది మార్పులేనిది.

---

## 9. చిందరవందర ఇన్‌పుట్‌ను శుభ్రం చేయండి

మీ మూలం ముద్రిత HRI కుండలీకరణాలను గానీ దారి తప్పిన ఖాళీలను గానీ పంపుతుంటే, పార్సింగ్‌కు
ముందే పేలోడ్‌ను సరిచేసే రెండు **ఇన్‌పుట్ మాడిఫైయర్లు** కోర్‌లో ఉన్నాయి:

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

డిఫాల్ట్‌గా ఏదీ చేతనం కాలేదు, రెండింటికీ తమవైన హెచ్చరికలున్నాయి — ఖాళీ, కుండలీకరణం రెండూ
చెల్లుబాటయ్యే GS1 డేటా అక్షరాలే, కాబట్టి మీకు తెలిసిన మూలానికే వాటిని వర్తింపజేయండి. చూడండి
[అంతర్నిర్మిత మాడిఫైయర్లు](GaiaParser-Telugu.md#అతరనరమత-మడఫయరల) — కుండలీకరణాలు తీసేశాక అవి
సూచించిన విభాజకాన్ని ఎందుకు తిరిగి తేవాలో అక్కడే వివరించారు.

---

## 10. ఇక్కడి నుండి ఎక్కడికి

- **[GaiaParser డెవలపర్ మార్గదర్శి](GaiaParser-Telugu.md)** — పైప్‌లైన్ వివరాలు, పూర్తి ఫలిత
  నమూనా, ప్రతి లోప కోడ్, AI మరియు వ్యాఖ్యాన కీల అనుబంధాలు.
- **[GaiaBuilder డెవలపర్ మార్గదర్శి](GaiaBuilder-Telugu.md)** — AI/విలువ జతల నుండి ఎలిమెంట్
  స్ట్రింగ్‌లు, Digital Link URIలు నిర్మించండి.
- **[Gaia API HTTP సూచిక](../../gaia-api-reference.md)** — లైబ్రరీని పొందుపరచడం ఇష్టం లేకపోతే అదే
  యంత్రం HTTP ద్వారా.
- **[ai-codes.txt](../../ai-codes.txt)** — వేగంగా వెతకడానికి `(AI) TITLE` అనే చదునైన జాబితా.

### అయిదు పంక్తుల రూపం

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
