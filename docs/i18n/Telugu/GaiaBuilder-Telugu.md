# GaiaBuilder — డెవలపర్ మార్గదర్శి

## విషయ సూచిక

1. [స్థూల పరిచయం](#సథల-పరచయ)
2. [GS1 మరియు General Specifications గురించి](#gs1-మరయ-general-specifications-గరచ)
3. [శీఘ్ర ప్రారంభం](#శఘర-పరరభ)
4. [ఇది ఎలా పని చేస్తుంది](#ఇద-ఎల-పన-చసతద)
5. [ఎలిమెంట్ స్ట్రింగ్‌లు నిర్మించడం](#ఎలమట-సటరగల-నరమచడ)
   - [లక్షణ-AIలకు వాటి గుర్తింపు కీ కావాలి](#లకషణ-aiలక-వట-గరతప-క-కవల)
6. [Digital Link URIలు నిర్మించడం](#digital-link-uriల-నరమచడ)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [ధ్రువీకరణ, లోపాలు](#ధరవకరణ-లపల)
   - [మినహాయింపు విసిరే నిర్మాణ పద్ధతులు](#మనహయప-వసర-నరమణ-పదధతల)
   - [మినహాయింపు విసరని tryBuild\* పద్ధతులు](#మనహయప-వసరన-trybuild-పదధతల)
   - [లోప సందేశాల భాష](#లప-సదశల-భష)
   - [BuildResult](#buildresult)
9. [తనిఖీ అంకెలు](#తనఖ-అకల)
10. [థ్రెడ్ భద్రత](#థరడ-భదరత)
11. [API సూచిక](#api-సచక)

---

## స్థూల పరిచయం

`GaiaBuilder` అనేది [`GaiaParser`](GaiaParser-Telugu.md)కి వ్యతిరేక దిశ జోడీ: అప్లికేషన్ ఐడెంటిఫైయర్ (AI), విలువ జతల సమాహారాన్ని ఇది చక్కని GS1 **ఎలిమెంట్ స్ట్రింగ్**గా లేదా **GS1 Digital Link URI**గా మారుస్తుంది. మీరు AIలనూ వాటి పూర్తి డేటా విలువలనూ ఇస్తారు; బిల్డర్ వాటిని కలిపి, `GaiaParser` వాడే అదే యంత్రంతో ఫలితాన్ని ధ్రువీకరించి, ఆపై అవుట్‌పుట్‌ను అందిస్తుంది.

బిల్డర్ *తన సొంత ప్రతిపాదిత అవుట్‌పుట్‌నే పార్స్ చేసి* ధ్రువీకరిస్తుంది కాబట్టి, అది తిరిగి ఇచ్చే ప్రతిదీ `GaiaParser` ద్వారా నిర్దోషంగా పార్స్ అవుతుందని హామీ — ఏది సరైనదో అన్న విషయంలో ఈ రెండూ ఎప్పటికీ విభేదించలేవు.

**ప్రవేశ బిందువు తరగతి:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 మరియు General Specifications గురించి

**GS1** అనేది సరఫరా-గొలుసు గుర్తింపు, డేటా మార్పిడి కోసం బహిరంగ ప్రమాణాలను రూపొందించి నిర్వహించే ప్రపంచవ్యాప్త లాభాపేక్ష రహిత సంస్థ. దీని ప్రమాణాలు రిటైల్, ఆరోగ్య సంరక్షణ, రవాణా-నిర్వహణ, ఆహార సేవలు మరియు మరెన్నో పరిశ్రమల్లో వాడుకలో ఉన్నాయి; వినియోగదారు ప్యాకేజింగ్‌పై ఉండే ఉత్పత్తి బార్‌కోడ్ నుండి ఔషధ మోతాదుల సీరియల్ ఆధారిత ట్రాకింగ్ వరకు అన్నీ వీటి పరిధిలోకి వస్తాయి.

ఈ బిల్డర్ అమలు చేసే ప్రతిదానికీ అధికారిక ఆధారం **GS1 General Specifications** — ఈ ఒక్క పత్రమే కింది వాటిని నిర్వచిస్తుంది:

- అన్ని అప్లికేషన్ ఐడెంటిఫైయర్ (AI) కోడ్‌లు, వాటి డేటా శీర్షికలు, ఆకృతులు, ధ్రువీకరణ నియమాలు
- AI ఎలిమెంట్ స్ట్రింగ్‌లను నిర్మించడానికి, ఎన్‌కోడ్ చేయడానికి వాక్యనిర్మాణ నియమాలు
- బార్‌కోడ్ సింబాలజీ అవసరాలు మరియు AIM కోడ్ ఐడీల కేటాయింపు
- తనిఖీ అంకె, తనిఖీ అక్షరాల అల్గారిథమ్‌లు
- రెండంకెల సంవత్సర నిర్ధారణ (జారే కిటికీ నియమం)
- Data Matrix, QR Code, GS1-128, GS1 DataBar మరియు ఇతర వాహకాల వివరాలు

GS1 General Specifications ప్రతి సంవత్సరం నవీకరించబడతాయి. ప్రస్తుత ముద్రణ, సహాయక సామగ్రి ఇక్కడ లభ్యం:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications యొక్క **విడుదల 26.0 (ఆమోదితం, జనవరి 2026)**ను అమలు చేస్తుంది.

GS1 Digital Link URIలు **GS1 Digital Link: URI Syntax** అనే సహచర ప్రమాణం పరిధిలోకి వస్తాయి. ప్రాథమిక గుర్తింపు కీలు, కీ-నిర్దేశకాల క్రమం, డేటా లక్షణాల ఎన్‌కోడింగ్‌ను అదే నిర్వచిస్తుంది — Digital Link URIలను అందించేటప్పుడు బిల్డర్ వీటినే వర్తింపజేస్తుంది:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax ప్రమాణం యొక్క **విడుదల 1.7.0 (ఆమోదితం, ఆగస్టు 2026)**ను అమలు చేస్తుంది.

ఈ పత్రంలోని విభాగ ప్రస్తావనలు GS1 General Specificationsను సూచిస్తాయి (ఉదా. "Table 7-5", "section 7.12"), కేవలం Digital Link విభాగ సంఖ్యలు (ఉదా. "§4.9", "§4.12") మినహాయింపు; అవి GS1 Digital Link: URI Syntax ప్రమాణాన్ని సూచిస్తాయి.

---

## శీఘ్ర ప్రారంభం

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

ముడి AI లిటరల్‌లకు బదులు `GS1Constants_AICodes` స్థిరాంకాలను వాడండి (చూడండి [పార్సర్ మార్గదర్శిలోని అనుబంధం అ](GaiaParser-Telugu.md#అనబధ-అ--ai-సటరగ-సథరకల)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## ఇది ఎలా పని చేస్తుంది

ప్రతి నిర్మాణమూ ఒకే దారిలో సాగుతుంది:

1. **కలపడం** — AI/విలువ జతలు కలిసి ఒక ప్రతిపాదిత ఎలిమెంట్ స్ట్రింగ్ అవుతాయి. *విభాజకం అవసరమైన*, చివరి ఎలిమెంట్ కాని ప్రతి AI తర్వాతా FNC1 గుంపు విభాజకం (`0x1D`) చొప్పించబడుతుంది. పొడవు ముందే నిర్ణీతమైన AIలకు (GTIN, తేదీలు, స్థిర-పొడవు కొలతలు) విభాజకం రాదు; మిగతావాటికి వస్తుంది. (తెలియని AIలు ఈ అడుగు దాకా చేరనే చేరవు — `ai(...)` వాటిని వెంటనే తిరస్కరిస్తుంది; చూడండి [ఎలిమెంట్ స్ట్రింగ్‌లు నిర్మించడం](#ఎలమట-సటరగల-నరమచడ).)
2. **ధ్రువీకరణ** — ప్రతిపాదిత స్ట్రింగ్‌ను `GaiaParser` `CONTENT` పద్ధతిలో పార్స్ చేస్తుంది. ప్రతి విలువా దాని AI ఆకృతికీ తనిఖీ అంకెకూ సరిచూడబడుతుంది, నిర్మాణ నియమాలు (తప్పనిసరి, నిషేధిత AI జతలు) అమలవుతాయి. పార్సింగ్ చెల్లనిదైతే నిర్మాణం విఫలమవుతుంది.
3. **అందించడం** —
   - ఎలిమెంట్ స్ట్రింగ్‌కు, ధ్రువీకరించిన వస్తువు యొక్క `toElementString()` తిరిగి ఇవ్వబడుతుంది.
   - Digital Linkకు, ప్రతి ఎలిమెంట్‌కూ దాని DL పాత్ర కేటాయించబడుతుంది (ప్రాథమిక కీ, కీ-నిర్దేశకం, లేదా డేటా లక్షణం), కీ-నిర్దేశక వరుస ధ్రువీకరించబడుతుంది, URI తయారవుతుంది, ఆపై **తయారైన URIని మళ్ళీ పార్స్ చేసి అది చెల్లుబాటయ్యే Digital Linkగానే తిరిగి వస్తుందా అని నిర్ధారించుకుంటారు** — స్ట్రింగ్ కలపడం, శాతం-ఎన్‌కోడింగ్ అడుగుకు ఇది భద్రతా పరిశీలన. అలా తిరిగి రాకపోతే `GaiaBuilderException` విసరబడుతుంది.

ఇది `DLSyntaxParser`లోని పునర్నిర్మాణ తర్కానికే ప్రతిబింబం, కాబట్టి విభాజకం స్థానం, ధ్రువీకరణ పార్సర్ ఆశించినట్లే సరిగ్గా ఉంటాయి.

---

## ఎలిమెంట్ స్ట్రింగ్‌లు నిర్మించడం

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** వెంటనే ధ్రువీకరించబడుతుంది: అది తెలిసిన GS1 అప్లికేషన్ ఐడెంటిఫైయర్ కాకపోతే `ai(...)` `IllegalArgumentException` విసురుతుంది. (బిల్డర్ పార్సింగ్‌కు ముందే AIని విలువతో కలుపుతుంది, కాబట్టి `"99999"` వంటి తెలియని లేదా మరీ పొడవైన AIని ఇక్కడే పట్టుకోవాలి — లేకపోతే అది నిశ్శబ్దంగా వేరే AIగా మళ్ళీ టోకనీకరించబడేది.) **విలువ** మాత్రం తర్వాత, నిర్మాణ సమయంలో ధ్రువీకరించబడుతుంది.
- విలువలు తనిఖీ అంకెతో సహా **పూర్తిగా** ఉండాలి. బిల్డర్ మీ కోసం తనిఖీ అంకెను లెక్కించదు, జోడించదు కూడా — చూడండి [తనిఖీ అంకెలు](#తనఖ-అకల).
- AIలు మీరు జోడించిన క్రమంలోనే వస్తాయి. GS1 నిర్మాణం కోరిన చోట బిల్డరే FNC1 విభాజకాలను పెడుతుంది; వాటిని మీరు జోడించవద్దు.
- **ఏ AI లేకుండా** నిర్మిస్తే ఖాళీ `getErrors()` జాబితాతో `GaiaBuilderException("No AIs supplied")` విసరబడుతుంది — ఏ `GaiaError`నూ మోయని ఏకైక వైఫల్యం ఇదే.
- తన ఆకృతి నియమంలో గానీ తనిఖీ అంకెలో గానీ విలువ విఫలమైన AI, మొత్తం నిర్మాణాన్నే విఫలం చేస్తుంది.

### లక్షణ-AIలకు వాటి గుర్తింపు కీ కావాలి

చాలా AIలు *లక్షణాలే*; వాటితో పాటు గుర్తింపు కీ ఉండాలని GS1 General Specifications కోరుతాయి, బిల్డర్ దాన్ని అమలు చేస్తుంది — పూర్తి వాక్యనిర్మాణ దశ ద్వారానే ధ్రువీకరిస్తుంది, తప్పించుకునే దారి లేదు. ఒంటరి బ్యాచ్/లాట్ లేదా సీరియల్ నంబర్ చెల్లుబాటయ్యే ఎలిమెంట్ స్ట్రింగ్ **కాదు**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

గుర్తింపు కీలు (GTIN `01`, SSCC `00`, GLN `414`, …), కంపెనీ-అంతర్గత AIలు (`90`–`99`) పూర్తి న్యాయంగా ఒంటరిగా నిలబడగలవు. మిగతా అన్నిటికీ తోడు కావాలి.

> ఈ పరిశీలనను దాటవేయమని `GaiaParser`కు `ParseConfig.skipRequiresCheck(true)`తో చెప్పవచ్చు; కానీ `GaiaBuilder` ఉద్దేశపూర్వకంగానే దానికి సమానమైనది ఇవ్వదు — ప్రమాణానికి అనుగుణమైన అవుట్‌పుట్ ఇవ్వడమే దాని లక్ష్యం. ఉద్దేశపూర్వకంగా అసంపూర్ణమైన ఎలిమెంట్ స్ట్రింగ్‌ను కలపాలంటే, దాన్ని మీరే కలిపి, పరిశీలన ఆపివేసి పార్స్ చేయండి.

---

## Digital Link URIలు నిర్మించడం

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

చెల్లుబాటయ్యే Digital Linkకు సరిగ్గా ఒకే ఒక **ప్రాథమిక గుర్తింపు కీ** కావాలి (ఉదా. GTIN `01`, GLN `414`, SSCC `00`). మీరిచ్చిన ప్రతి AInీ బిల్డర్ వర్గీకరిస్తుంది:

| పాత్ర | ఎలా అందించబడుతుంది | ఉదాహరణ |
|------|-------------|---------|
| ప్రాథమిక గుర్తింపు కీ | డొమైన్/ఉపసర్గం తర్వాతి మార్గ ముక్క | `/01/09506000134352` |
| కీ-నిర్దేశకం (CPV `22`, బ్యాచ్ `10`, సీరియల్ `21`, …) | ఆ తర్వాతి మార్గ ముక్కలు, **§4.9 ప్రామాణిక క్రమంలో** (మీరు జోడించిన క్రమంలో కాదు) | `/10/LOT-ABC` |
| డేటా లక్షణం (మిగతావన్నీ) | ప్రశ్న పరామితులు, **AI కీ ప్రకారం నిఘంటు క్రమంలో** (§4.12) | `?17=271231` |

అందించేటప్పుడు నిర్దేశకాలు మళ్ళీ క్రమబద్ధమవుతాయి కాబట్టి, వాటిని క్రమం తప్పి ఇచ్చినా ఇబ్బంది లేదు — `ai("10", …)` కంటే ముందు `ai("21", …)` ఇచ్చినా `/10/LOT/21/SER` గానే అందించబడుతుంది. ప్రాథమిక కీకి ఆమోదయోగ్యంగా ఉండాల్సింది వాటి *సమూహమే*.

మార్గంలోనూ ప్రశ్నలోనూ ఉన్న విలువలు శాతం-ఎన్‌కోడ్ చేయబడతాయి.

కింది సందర్భాల్లో నిర్మాణం విఫలమవుతుంది (`GaiaBuilderException` విసురుతుంది, లేదా విఫలమైన `BuildResult` తిరిగి ఇస్తుంది):

- AIలలో ప్రాథమిక గుర్తింపు కీ **ఏదీ** లేకపోతే;
- **ఒకటికి మించి** ప్రాథమిక గుర్తింపు కీలు ఉంటే;
- ఏదైనా AI Digital Linkలో **నిషేధితమైతే** (`03`, `8014`);
- ఎంచుకున్న ప్రాథమిక కీకి **కీ-నిర్దేశక వరుస** చెల్లనిదైతే (ఆ కీ వెంట రాని నిర్దేశకం, లేదా అనుమతించిన క్రమం బయట ఉన్న నిర్దేశకాలు).

---

## BuilderDigitalLinkConfig

పథకం, డొమైన్, మార్గ ఉపసర్గం, అదనపు ప్రశ్న పరామితులు, ముక్క — వీటిని నియంత్రించడానికి `BuilderDigitalLinkConfig` ఇవ్వండి:

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

| బిల్డర్ పద్ధతి | ప్రయోజనం | డిఫాల్ట్ |
|----------------|---------|---------|
| `scheme(String)` | URI పథకం; `http` లేదా `https` అయి ఉండాలి | `https` |
| `domain(String)` | పరిష్కర్త అధికారం — హోస్ట్ లేదా `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | మొదటి ప్రాథమిక కీకి ముందటి మార్గ ముక్కలు; రెండు చివరల వాలుగీతలు ప్రమాణీకరించబడతాయి | *(ఏమీ లేదు)* |
| `baseUrl(String)` | ఒక URLను `scheme` + `domain` + `pathPrefix`గా విడగొట్టే సౌకర్యం | — |
| `addQueryParam(String, String)` | అదనపు ప్రశ్న పరామితి; AI డేటా లక్షణాల **తర్వాత**, చేర్చిన క్రమంలో వస్తుంది; శాతం-ఎన్‌కోడ్ చేయబడుతుంది | — |
| `fragment(String)` | URI ముక్క (మొదట `#` లేకుండా); శాతం-ఎన్‌కోడ్ చేయబడుతుంది | *(ఏమీ లేదు)* |

`build()` అమరికను వెంటనే ధ్రువీకరిస్తుంది: `http(s)` కాని పథకం లేదా ఖాళీ డొమైన్ `IllegalArgumentException` విసురుతుంది.

- `BuilderDigitalLinkConfig.canonical()` (మారుపేరు `defaultConfig()`) అంటే ఏ అదనపు లేకుండా డిఫాల్ట్ `https://id.gs1.org` — పరామితి లేని `buildDigitalLinkUri()` వాడేది సరిగ్గా అదే, `GS1AIObject.getCanonicalDigitalLink()` తయారుచేసేదీ అదే.
- `baseUrl("http://id.example.org:8080/r")` → పథకం `http`, డొమైన్ `id.example.org:8080`, మార్గ ఉపసర్గం `/r`.
- అదనపు ప్రశ్న పరామితులు ఎప్పుడూ AI నుండి వచ్చిన లక్షణాల తర్వాతే వస్తాయి, కాబట్టి ప్రామాణిక AI క్రమం (§4.12) నిలిచి ఉంటుంది.

`BuilderDigitalLinkConfig` మార్పులేనిది; ఒకే ప్రతిని నిరభ్యంతరంగా మళ్ళీ మళ్ళీ వాడండి.

---

## ధ్రువీకరణ, లోపాలు

### మినహాయింపు విసిరే నిర్మాణ పద్ధతులు

AIలు చక్కని అవుట్‌పుట్‌ను ఏర్పరచలేనప్పుడు `buildElementString()`, `buildDigitalLinkUri()`, `buildDigitalLinkUri(BuilderDigitalLinkConfig)` అనేవి **`GaiaBuilderException`** (పరిశీలించని `RuntimeException`) విసురుతాయి:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **విషయ** వైఫల్యాల్లో (తప్పు తనిఖీ అంకె, ఆకృతి సరిపోలకపోవడం, లేని/నిషేధిత AI), `getErrors()` పార్సర్ యొక్క `GaiaError` వస్తువులనే మోస్తుంది — [పార్సర్ మార్గదర్శిలో నమోదైన](GaiaParser-Telugu.md#gaiaerror) అవే వస్తువులు.
- **Digital Link నిర్మాణ** వైఫల్యాల్లో (ప్రాథమిక కీ లేదు, అనేక ప్రాథమిక కీలు, నిషేధిత AI, చెల్లని కీ-నిర్దేశక వరుస), `getErrors()` బిల్డర్ భాషలోకి స్థానికీకరించిన ఒకే ఒక `GaiaError`ను మోస్తుంది (కోడ్ `GE-L008`, `GE-L012`, `GE-L013` లేదా `GE-L014`).

### మినహాయింపు విసరని tryBuild\* పద్ధతులు

ఇన్‌పుట్ వాడుకరి నుండి వస్తున్నప్పుడు, వైఫల్యం ఊహించదగిన, చూసుకోగల ఫలితమైనప్పుడు — మినహాయింపులతో నియంత్రణ ప్రవాహాన్ని నడపడానికి బదులు `tryBuild*` రూపాలను వాడండి. అవి విసరకుండా [`BuildResult`](#buildresult)ను తిరిగి ఇస్తాయి:

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

| విసురుతుంది | విసరదు |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

ప్రతి `tryBuild*` పద్ధతీ తన విసిరే జోడీతో అదే ధ్రువీకరణ కేంద్రాన్ని పంచుకుంటుంది; తేడా కేవలం వైఫల్య సరిహద్దులోనే.

### లోప సందేశాల భాష

విషయ ధ్రువీకరణ లోపాలు స్థానికీకరించిన లోప జాబితా నుండే వస్తాయి. `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` మోసే `GaiaError` సందేశాల భాషను ఎంచుకోవడానికి `language(...)`ను పిలవండి; డిఫాల్ట్ భాష ఇంగ్లిష్:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

`GaiaParser` `ParseConfig` ద్వారా స్వీకరించే అదే `GaiaConstants.Language` అమరిక ఇది, కాబట్టి బిల్డర్, పార్సర్ ఒకేలా స్థానికీకరిస్తాయి.

**విషయ** వైఫల్యాలకూ **Digital Link నిర్మాణ** వైఫల్యాలకూ (ప్రాథమిక కీ లేదు, అనేక ప్రాథమిక కీలు, నిషేధిత AI, చెల్లని కీ-నిర్దేశక వరుస) — ఈ రెండింటి `GaiaError` సందేశాలూ ఉమ్మడి లోప జాబితా ద్వారానే స్థానికీకరించబడతాయి; రెండోది `GE-L008`, `GE-L012`, `GE-L013`, `GE-L014` కోడ్‌లను వాడుతుంది.

### BuildResult

`BuildResult` (పాకేజీ `tools.pantheum.gaia.result`) అనేది `tryBuild*` పిలుపు ఫలితాన్ని వర్ణించే మార్పులేని విలువ-రకం:

| పద్ధతి | విజయమైనప్పుడు | విఫలమైనప్పుడు |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | అందించిన స్ట్రింగ్ | `null` |
| `getMessage()` | `null` | వైఫల్య వివరణ |
| `getErrors()` | ఖాళీ జాబితా | ధ్రువీకరణ లోపాలు (`GaiaBuilderException.getErrors()`లోనివే) |

---

## తనిఖీ అంకెలు

బిల్డర్ తనిఖీ అంకెలను ధ్రువీకరిస్తుంది కానీ వాటిని **లెక్కించదు** — మీ విలువల్లో తనిఖీ అంకె ముందే ఉండాలి. ఒకదాన్ని లెక్కించడానికి `GS1Utils.calculateCheckDigit` వాడండి:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` ఇచ్చిన అంకెలపై ప్రామాణిక GS1 మాడ్యులో-10 అల్గారిథమ్‌ను వర్తింపజేసి `0` నుండి `9` వరకూ తనిఖీ అంకెను తిరిగి ఇస్తుంది; ఇన్‌పుట్ `null`, ఖాళీ లేదా సంఖ్యేతరమైతే `-1` ఇస్తుంది.

---

## థ్రెడ్ భద్రత

`GaiaBuilder` థ్రెడ్-సురక్షితం **కాదు**, అది ఒకసారి వాడటానికే రూపొందించబడింది: `create()` పిలవండి, AIలు జోడించండి, ఒకేసారి నిర్మించండి. ప్రతి అవుట్‌పుట్‌కూ కొత్త బిల్డర్‌ను తయారుచేయండి; ఒకే బిల్డర్‌ను అనేక థ్రెడ్‌లలో పంచుకోవద్దు.

`BuilderDigitalLinkConfig` (మరియు దాని `BuildResult` అవుట్‌పుట్‌లు) మార్పులేనివి, నిరభ్యంతరంగా పంచుకోవచ్చు — మొదట్లో ఒకే అమరికను నిర్మించి, దాన్ని అనేక బిల్డర్లలో మళ్ళీ వాడండి.

---

## API సూచిక

### `GaiaBuilder`

| పద్ధతి | వివరణ |
|--------|-------------|
| `static GaiaBuilder create()` | కొత్త, ఖాళీ బిల్డర్‌ను మొదలుపెడుతుంది. |
| `GaiaBuilder ai(String ai, String value)` | ఒక AInీ దాని పూర్తి విలువనూ జోడిస్తుంది. ఈ రెండింటిలో ఏదైనా `null` అయితే, లేదా `ai` తెలిసిన GS1 అప్లికేషన్ ఐడెంటిఫైయర్ కాకపోతే `IllegalArgumentException` విసురుతుంది. |
| `GaiaBuilder language(GaiaConstants.Language language)` | విషయ ధ్రువీకరణ లోప సందేశాల భాషను నిర్ణయిస్తుంది (డిఫాల్ట్ ఇంగ్లిష్). `null` పట్టించుకోబడదు. |
| `String buildElementString()` | GS1 ఎలిమెంట్ స్ట్రింగ్‌ను అందిస్తుంది. విఫలమైతే `GaiaBuilderException` విసురుతుంది. |
| `String buildDigitalLinkUri()` | ప్రామాణిక Digital Link URIని అందిస్తుంది. విఫలమైతే `GaiaBuilderException` విసురుతుంది. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` ప్రకారం Digital Link URIని అందిస్తుంది. విఫలమైతే `GaiaBuilderException` విసురుతుంది. |
| `BuildResult tryBuildElementString()` | మినహాయింపు విసరని ఎలిమెంట్ స్ట్రింగ్ నిర్మాణం. |
| `BuildResult tryBuildDigitalLinkUri()` | మినహాయింపు విసరని ప్రామాణిక Digital Link నిర్మాణం. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` ప్రకారం మినహాయింపు విసరని Digital Link నిర్మాణం. |

### `BuilderDigitalLinkConfig`

| సభ్యం | వివరణ |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | డిఫాల్ట్ `https://id.gs1.org`. |
| `static Builder builder()` | కొత్త అమరిక బిల్డర్. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | పరిష్కరించిన పథకం, పరిష్కర్త అధికారం, మార్గ ఉపసర్గం. |
| `getExtraQueryParams()` | అదనపు ప్రశ్న పరామితులు, చేర్చిన క్రమంలో. |
| `getFragment()` | ముక్క, లేదా `null`. |

### `GaiaBuilderException`

| సభ్యం | వివరణ |
|--------|-------------|
| `getErrors()` | వైఫల్యానికి కారణమైన `GaiaError` వస్తువులు — విషయ వైఫల్యంలో పార్సర్ లోపాలు, లేదా ఒకే ఒక Digital Link నిర్మాణ లోపం (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). ఎప్పుడూ `null` కాదు. |

### `BuildResult`

| సభ్యం | వివరణ |
|--------|-------------|
| `isSuccess()` | నిర్మాణం విజయవంతమైందా. |
| `getValue()` | విజయమైనప్పుడు అందించిన అవుట్‌పుట్; విఫలమైతే `null`. |
| `getMessage()` | విఫలమైనప్పుడు వైఫల్య వివరణ; విజయమైతే `null`. |
| `getErrors()` | విఫలమైనప్పుడు ధ్రువీకరణ లోపాలు; విజయమైతే ఖాళీ. ఎప్పుడూ `null` కాదు. |
| `getTiming()` | నిర్మాణ కార్యానికి `ProcessingTiming` (మొదలైన సమయం, ప్రాసెసింగ్ వ్యవధి), లేదా `null`. |

---

ఇవి కూడా చూడండి: పార్సింగ్ వైపు, AI ఎలిమెంట్ నమూనా, లోప సూచిక, మరియు AI, వ్యాఖ్యాన స్థిరాంకాల అనుబంధాల కోసం **[GaiaParser — డెవలపర్ మార్గదర్శి](GaiaParser-Telugu.md)**.
