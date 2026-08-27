# GAIA (GS1 Application Identifiers Analyser) — विकसक मार्गदर्शक

## अनुक्रमणिका

1. [थोडक्यात ओळख](#थडकयत-ओळख)
2. [GS1 आणि General Specifications विषयी](#gs1-आण-general-specifications-वषय)
3. [GS1 अ‍ॅप्लिकेशन आयडेंटिफायर](#gs1-अपलकशन-आयडटफयर)
4. [झटपट सुरुवात](#झटपट-सरवत)
5. [पार्सिंग पाइपलाइन](#परसग-पइपलइन)
   - [पूर्वटप्पा — इनपुट मॉडिफायर](#परवटपप--इनपट-मडफयर)
   - [टप्पा ० — सहसंबंध आयडी](#टपप-०--सहसबध-आयड)
   - [टप्पा १ — इनपुटची दिशा ठरवणे](#टपप-१--इनपटच-दश-ठरवण)
   - [टप्पा २ — वाक्यरचना](#टपप-२--वकयरचन)
   - [टप्पा ३ — आशय](#टपप-३--आशय)
   - [टप्पा ४ — विवेचन](#टपप-४--ववचन)
6. [पार्स रचना (`ParseConfig`)](#परस-रचन-parseconfig)
   - [पर्याय](#परयय)
   - [स्थानिकीकृत संदेश आणि लेबले](#सथनककत-सदश-आण-लबल)
   - [दिनांकाचे स्वरूपन](#दनकच-सवरपन)
7. [इनपुट मॉडिफायर](#इनपट-मडफयर)
   - [अंगभूत मॉडिफायर](#अगभत-मडफयर)
   - [मॉडिफायर लिहिणे](#मडफयर-लहण)
   - [मॉडिफायर नोंदवणे](#मडफयर-नदवण)
   - [मॉडिफायरने काय केले ते पाहणे](#मडफयरन-कय-कल-त-पहण)
   - [मॉडिफायरचे अपयश हाताळणे](#मडफयरच-अपयश-हतळण)
8. [पार्स पद्धती](#परस-पदधत)
   - [DATA_CARRIER पद्धत](#data_carrier-पदधत)
   - [SYNTAX पद्धत](#syntax-पदधत)
   - [CONTENT पद्धत](#content-पदधत)
   - [INTERPRETATION पद्धत (पूर्वनिर्धारित)](#interpretation-पदधत-परवनरधरत)
9. [सहसंबंध आयडी](#सहसबध-आयड)
10. [GS1 Digital Link](#gs1-digital-link)
11. [निकालांसह काम करणे](#नकलसह-कम-करण)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry आणि DataCarrierType](#datacarrierentry-आण-datacarriertype)
12. [चूक संदर्भ](#चक-सदरभ)
13. [थ्रेड सुरक्षा](#थरड-सरकष)
14. [परिशिष्ट अ — AI स्ट्रिंग स्थिरांक](#परशषट-अ--ai-सटरग-सथरक)
    - [ओळख आणि अनुक्रमांकन](#ओळख-आण-अनकरमकन)
    - [दिनांक आणि वेळा](#दनक-आण-वळ)
    - [प्रमाण आणि माप — परिवर्तनीय माप (मेट्रिक)](#परमण-आण-मप--परवरतनय-मप-मटरक)
    - [प्रमाण आणि माप — परिवर्तनीय माप (इंपीरियल / अमेरिकी)](#परमण-आण-मप--परवरतनय-मप-इपरयल--अमरक)
    - [किंमत आणि आर्थिक रकमा](#कमत-आण-आरथक-रकम)
    - [स्थान आणि पाठवणी](#सथन-आण-पठवण)
    - [उत्पादनाचे गुणधर्म आणि मागोवा](#उतपदनच-गणधरम-आण-मगव)
    - [राष्ट्रीय आरोग्य परतफेड क्रमांक (NHRN)](#रषटरय-आरगय-परतफड-करमक-nhrn)
    - [आरोग्यसेवा, GMN, HIDRI, CPID आणि व्यक्तिविषयक माहिती](#आरगयसव-gmn-hidri-cpid-आण-वयकतवषयक-महत)
    - [अंतर्गत / कंपनी वापर](#अतरगत--कपन-वपर)
15. [परिशिष्ट ब — विवेचन किल्ली स्थिरांक](#परशषट-ब--ववचन-कलल-सथरक)
    - [दिनांक आणि वेळ](#दनक-आण-वळ)
    - [कापणीची तारीख](#कपणच-तरख)
    - [GS1 कंपनी उपसर्ग](#gs1-कपन-उपसरग)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [देश (ISO 3166)](#दश-iso-3166)
    - [चलन (ISO 4217)](#चलन-iso-4217)
    - [तापमान](#तपमन)
    - [लिंग (ISO 5218)](#लग-iso-5218)
    - [जलचर प्रजाती (FAO)](#जलचर-परजत-fao)
    - [नाटो साठा क्रमांक (NSN)](#नट-सठ-करमक-nsn)
    - [रोल उत्पादने](#रल-उतपदन)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM ओळखचिन्हे (EID / ICCID)](#sim-ओळखचनह-eid--iccid)
    - [प्रमाणन संदर्भ](#परमणन-सदरभ)
    - [GS1 UIC](#gs1-uic)
    - [अर्भकाचा जन्मक्रम](#अरभकच-जनमकरम)
    - [जागतिक मॉडेल क्रमांक (GMN)](#जगतक-मडल-करमक-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [दशांश आणि मापन मूल्ये](#दशश-आण-मपन-मलय)
    - [भौगोलिक निर्देशांक](#भगलक-नरदशक)
    - [उत्पादन पद्धत](#उतपदन-पदधत)
    - [AIDC माध्यम प्रकार](#aidc-मधयम-परकर)
    - [एकूणातील तुकडा](#एकणतल-तकड)
    - [घटक विभाजने](#घटक-वभजन)
    - [संकीर्ण](#सकरण)

---

## थोडक्यात ओळख

`GaiaParser` हा GS1 अ‍ॅप्लिकेशन आयडेंटिफायर (AI) घटक स्ट्रिंग पार्स करण्याचा प्रवेशबिंदू आहे. स्कॅनरचे कच्चे आउटपुट तो खालीलपैकी कोणत्याही स्वरूपात स्वीकारतो आणि एक संरचित `ParseResult` परत करतो, ज्यात सोडवलेले सर्व AI, पडताळणीच्या चुका, आणि हवे असल्यास माणसाला वाचता येतील अशी विवेचनेही असतात:

- साधी AI घटक स्ट्रिंग: `0109506000134352`
- AIM सिम्बॉलॉजी आयडेंटिफायरचा उपसर्ग असलेली घटक स्ट्रिंग: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- यांपैकी काहीही, ऐच्छिकरीत्या ८-अंकी सहसंबंध आयडीच्या उपसर्गासह: `12345678~0109506000134352`

**प्रवेशबिंदू वर्ग:** `tools.pantheum.gaia.GaiaParser`

> **Gaia नवीन आहे का?** **[GaiaParser झटपट सुरुवात](GaiaParser-QuickStart-Marathi.md)** पासून सुरू करा — दहा मिनिटांत अवलंबित्वे, पहिले पार्सिंग, आणि नेहमी अडखळायला लावणाऱ्या काही गोष्टी. हे मार्गदर्शक म्हणजे संपूर्ण संदर्भ.

> याच्या उलट दिशा — AI/मूल्य जोड्यांपासून वैध घटक स्ट्रिंग आणि Digital Link URI *तयार करणे* — **[GaiaBuilder — विकसक मार्गदर्शक](GaiaBuilder-Marathi.md)** मध्ये मांडली आहे.

---

## GS1 आणि General Specifications विषयी

**GS1** ही एक जागतिक ना-नफा संस्था आहे, जी पुरवठा-साखळीतील ओळख आणि माहितीच्या देवाणघेवाणीसाठी खुली मानके विकसित करते व त्यांची देखभाल करते. तिची मानके किरकोळ विक्री, आरोग्यसेवा, वाहतूक-व्यवस्थापन, अन्नसेवा आणि इतर अनेक उद्योगांत वापरली जातात; ग्राहक पॅकेजिंगवरील उत्पादन बारकोडपासून ते औषधांच्या मात्रांच्या अनुक्रमांकाधारित मागोव्यापर्यंत सर्व काही त्यात येते.

हा पार्सर जे काही अंमलात आणतो त्याचा अधिकृत संदर्भ म्हणजे **GS1 General Specifications** — एकच दस्तऐवज, जो पुढील गोष्टी निश्चित करतो:

- सर्व अ‍ॅप्लिकेशन आयडेंटिफायर (AI) कोड, त्यांची माहिती-शीर्षके, स्वरूपे आणि पडताळणीचे नियम
- AI घटक स्ट्रिंग रचण्याचे आणि एन्कोड करण्याचे वाक्यरचना-नियम
- बारकोड सिम्बॉलॉजीच्या आवश्यकता आणि AIM कोड आयडीचे वाटप
- तपासणी अंक व तपासणी अक्षराचे अल्गोरिदम
- दोन-अंकी वर्षाचे निर्धारण (सरकत्या खिडकीचा नियम)
- Data Matrix, QR Code, GS1-128, GS1 DataBar आणि इतर वाहकांची तपशीलवार मांडणी

GS1 General Specifications दरवर्षी अद्ययावत होतात. सध्याची आवृत्ती आणि पूरक साहित्य येथे उपलब्ध आहे:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA हे GS1 General Specifications ची **आवृत्ती २६.० (मंजूर, जानेवारी २०२६)** अंमलात आणते.

GS1 Digital Link URI हे **GS1 Digital Link: URI Syntax** या सोबतीच्या मानकाच्या अधीन आहेत. तेच प्राथमिक ओळख-किल्ल्या, किल्ली-विशेषकांचा क्रम, आणि माहिती-गुणधर्मांचे एन्कोडिंग निश्चित करते — पार्सर Digital Link इनपुटवर हेच लागू करतो:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA हे GS1 Digital Link: URI Syntax मानकाची **आवृत्ती १.७.० (मंजूर, ऑगस्ट २०२६)** अंमलात आणते.

या दस्तऐवजातील विभागांचे संदर्भ GS1 General Specifications कडे निर्देश करतात (उदा. "Table 7-5", "section 7.12"), फक्त Digital Link चे विभाग-क्रमांक (उदा. "§4.9", "§4.12") याला अपवाद आहेत; ते GS1 Digital Link: URI Syntax मानकाकडे निर्देश करतात.

---

## GS1 अ‍ॅप्लिकेशन आयडेंटिफायर

**GS1 अ‍ॅप्लिकेशन आयडेंटिफायर (AI)** म्हणजे दोन ते चार अंकांचा छोटा संख्यात्मक उपसर्ग, जो त्याच्या लगेच पुढे येणाऱ्या माहितीचा अर्थ आणि स्वरूप ठरवतो. AI हे GS1 General Specifications मध्ये व्याख्यायित आहेत आणि पुरवठा-साखळीतील माहितीचा विस्तृत पट व्यापतात: उत्पादन ओळखचिन्हे, दिनांक, प्रमाणे, लॉट क्रमांक, अनुक्रमांक, मापे, URL, आणि आणखी बरेच काही.

### AI घटकाची रचना

प्रत्येक AI घटक दोन भागांचा बनलेला असतो:

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

AI कोड नेहमी संख्यात्मक असतो. माहितीचे मूल्य त्याच्या लगेच पुढे येते, आणि कोड व मूल्य यांच्यामध्ये कोणताही विभाजक नसतो.

### निश्चित-लांबीचे विरुद्ध परिवर्तनीय-लांबीचे AI

AI दोन प्रकारांत मोडतात:

| प्रकार | वर्तन | उदाहरण |
|---|---|---|
| **निश्चित-लांबी** | अक्षरांची नेमकी संख्या, नेहमी पूर्ण वाचली जाते | AI `01` (GTIN) — नेहमी १४ अंक |
| **परिवर्तनीय-लांबी** | १ पासून कमाल संख्येपर्यंत; GS विभाजकावर किंवा इनपुटच्या शेवटी संपते | AI `10` (बॅच/लॉट) — १ ते २० अक्षरसंख्यात्मक अक्षरे |

AI निश्चित आहे की परिवर्तनीय, हे केवळ GS1 तपशिलातील त्याच्या व्याख्येवरून ठरते — पार्सर कधीही अंदाज बांधत नाही.

### अनेक AI असलेल्या घटक स्ट्रिंग

अनेक AI एकाच घटक स्ट्रिंगमध्ये जोडता येतात. निश्चित-लांबीचे AI थेट जोडता येतात, कारण पार्सरला किती अक्षरे वाचायची हे नेमके ठाऊक असते. मात्र परिवर्तनीय-लांबीच्या AI च्या पुढे दुसरा AI येत असेल, तेव्हा त्यांचा शेवट **GS अक्षराने** (ASCII `0x1D`, ज्याला बारकोड सिम्बॉलॉजीमध्ये FNC1 असेही म्हणतात) करणे अनिवार्य आहे, म्हणजे एक मूल्य कुठे संपते आणि पुढील AI कोड कुठे सुरू होतो हे पार्सरला कळेल.

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

Java स्ट्रिंग लिटरलमध्ये GS अक्षर युनिकोड एस्केप `""` ने लिहा.

### नेहमी वापरले जाणारे AI

| AI | माहिती-शीर्षक | स्वरूप | उदाहरण मूल्य |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, एकल चलन-क्षेत्र) |
| `710` | NHRN PZN | X..20 | `12345678` |

> ४-अंकी माप किंवा किंमत AI मधील **चौथा अंक** अध्याहृत दशांश स्थानांची संख्या एन्कोड करतो — `3103` म्हणजे ३ दशांशांसह किलोग्रॅममधील निव्वळ वजन (`001500` = 1.500 kg), तर `3102` तेच अंक 15.00 kg असे वाचेल. वरील `स्वरूप` स्तंभ *माहितीचे* स्वरूप दाखवतो; प्रत्येक AI चा पूर्ण `getFormatString()` मात्र स्वतः AI लाही समाविष्ट करतो (उदा. `3103` साठी `N4+N6`).

### माणसाला वाचता येणारे विवेचन (HRI)

रूढ वाचनीय स्वरूपात प्रत्येक AI कोड त्याच्या मूल्याच्या अगदी आधी कंसात गुंडाळला जातो, आणि घटकांमध्ये एक जागा सोडली जाते:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS विभाजक HRI मध्ये दिसत नाही. हे स्वरूप `GS1AIObject.toHriString()` तयार करते.

### चार अंकांचे AI कोड

काही AI दोनऐवजी चार अंक वापरतात. पहिले दोन अंक AI कुळ ओळखतात; तिसरा आणि/किंवा चौथा अंक अधिकचा अर्थ वाहतो (जसे मापन AI मधील अध्याहृत दशांश बिंदूचे स्थान). पार्सर घटक स्ट्रिंगमधून पूर्ण AI कोड आपोआप सोडवतो — कॉल करणारा नेहमी पूर्ण कोडशीच काम करतो (उदा. `"3102"`, नुसते `"31"` नव्हे).

---

## झटपट सुरुवात

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

> **GS विभाजक:** अनेक AI असलेल्या स्ट्रिंगमधील परिवर्तनीय-लांबीचे AI GS अक्षराने (ASCII `0x1D`) वेगळे करणे अनिवार्य आहे. Java स्ट्रिंग लिटरलमध्ये `""` वापरा.

---

## पार्सिंग पाइपलाइन

### पूर्वटप्पा — इनपुट मॉडिफायर

`ParseConfig` मध्ये कोणतेही **इनपुट मॉडिफायर** असतील, तर ते इतर सर्व गोष्टींच्या आधी चालतात — सहसंबंध उपसर्ग काढण्याआधी, वाहक ओळखण्याआधी, GS1 पाइपलाइनमध्ये शिरण्याआधी. प्रत्येक मॉडिफायर पुढच्यासाठी कच्चे इनपुट पुन्हा लिहितो, आणि खालील सर्व टप्पे याच साखळीच्या आउटपुटवर काम करतात.

पूर्वनिर्धारितपणे एकही मॉडिफायर रचलेला नसतो, त्यामुळे तुम्ही स्वतः निवडल्याशिवाय हा पूर्वटप्पा काहीच करत नाही. पाहा [इनपुट मॉडिफायर](#इनपट-मडफयर).

---

### टप्पा ० — सहसंबंध आयडी

कोणत्याही GS1 प्रक्रियेआधी `GaiaParser` तपासतो की इनपुट ऐच्छिक **सहसंबंध आयडी उपसर्गाने** सुरू होते का: नेमके ८ ASCII दशांश अंक आणि त्यांच्यापुढे टिल्ड (`~`), उदा. `12345678~`.

उपसर्ग असल्यास तो काढून घेतला जातो आणि परत केलेल्या `ParseResult` वर `CorrelationInfo` म्हणून साठवला जातो. पुढील सर्व टप्पे उपसर्ग काढलेल्या पेलोडवर काम करतात. उपसर्ग नसल्यास इनपुट जसेच्या तसे पुढे जाते.

तपशिलांसाठी पाहा [सहसंबंध आयडी](#सहसबध-आयड).

---

### टप्पा १ — इनपुटची दिशा ठरवणे

सहसंबंध उपसर्ग काढल्यानंतर `GaiaParser` तपासतो की (उपसर्ग काढलेले) इनपुट **AIM कोड आयडी** ने सुरू होते का: `]` + ASCII अक्षर + ASCII अंक अशा रूपाचा तीन-अक्षरी उपसर्ग (उदा. GS1-128 साठी `]C1`, GS1 DataMatrix साठी `]d2`, GS1 DataBar / GS1 Composite साठी `]e0`).

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

वाहक GS1 AI वाहून नेण्यास सक्षम नसेल (उदा. टपाल बारकोड), तर पार्सिंग तत्काळ `GE-D002` चुकीसह थांबते.

---

### टप्पा २ — वाक्यरचना

हा नेहमीच चालतो. यात दोन उपपायऱ्या आहेत:

**२अ. टोकनीकरण (`AISyntaxParser`)**
- GS1 उपसर्ग तक्त्याच्या (GS1 General Specifications Table 7-5) साहाय्याने पहिल्या दोन अक्षरांतून AI कोडची लांबी वाचतो.
- निश्चित-लांबीचे AI इनपुटमधून नेमक्या संख्येने बाइट वाचतात.
- परिवर्तनीय-लांबीचे AI GS अक्षरापर्यंत किंवा इनपुटच्या शेवटापर्यंत वाचले जातात.
- अनेक घटक असलेल्या AI चा मूल्य-गठ्ठा प्रत्येक घटकानुसार तुकड्यांत कापला जातो.

**२ब. संरचनात्मक पडताळणी (`SyntaxValidator`)**
- पुनरावृत्त AI तपासतो (`GE-S004`).
- आवश्यक AI अवलंबित्वे तपासतो; उदा. AI `02` ला AI `37` लागतो (`GE-S005`).
- वर्जित AI जोड्या तपासतो (`GE-S006`).

या टप्प्यातील चुकांची पातळी `SYNTAX_ERROR` (टोकनायझर) किंवा `INTEGRITY_ERROR` (संरचनात्मक) असते. **कोणतीही एक** चूक असली — टोकनायझरची असो वा संरचनात्मक — तरी पाइपलाइन थांबते आणि आशय व विवेचनाचे टप्पे वगळले जातात.

---

### टप्पा ३ — आशय

टप्पा २ ने कोणतीही चूक दिली नसेल तरच हा चालतो (टोकनायझरची नाही आणि संरचनात्मकही नाही). प्रत्येक घटकासाठीची पाइपलाइन अशी (प्रत्येक पायरी आधीच्या पायरीत चूक नसेल तरच चालते):

| पायरी | पडताळणीकर्ता | चूक कोड |
|---|---|---|
| रेगेक्स तपासणी | `RegexValidator` | `GE-C001` |
| घटकाचा अक्षरसंच + स्वरूप | `ComponentValidator` | `GE-C005` + प्रत्येक अटीसाठी स्वरूप कोड (`GE-C054`–`GE-C115`) |
| तपासणी अंक / तपासणी अक्षर | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| विशेष अर्थविषयक पडताळणी | `ContentValidatorRegistry` | प्रत्येक अटीसाठी आशय कोड (`GE-C116`–`GE-C170`) |

या टप्प्यातील चुकांची पातळी `FORMAT_ERROR` किंवा `DATA_ERROR` असते, एक अपवाद वगळता: GS1-किल्ली
असलेल्या AI वरील GS1 कंपनी उपसर्गाच्या तपासण्या केवळ सूचनावजा आहेत आणि त्यांची पातळी
`WARNING` असते (पाहा [चूक संदर्भ](#चक-सदरभ)); त्यामुळे ओळखता न आलेला कंपनी उपसर्ग
स्वतःहून निकाल अवैध ठरवत नाही.

---

### टप्पा ४ — विवेचन

हा केवळ `INTERPRETATION` पद्धतीत चालतो, आणि तेव्हाच जेव्हा कोणत्याही घटकावर आधीच्या कोणत्याही टप्प्यातील चूक नसेल. `InterpretationEngine` प्रत्येक घटक लेबल असलेल्या मेटाडेटाने समृद्ध करतो:

- `dd/mm/yyyy` अशा स्वरूपात पुन्हा मांडलेले दिनांक
- GTIN तपासणी अंकाचे पृथक्करण आणि GS1 कंपनी उपसर्गाचा शोध
- ISO 3166 देशांची नावे
- ISO 4217 चलनांची नावे आणि चिन्हे
- उलगडलेल्या दशांश रकमा
- HRI (माणसाला वाचता येणारे विवेचन) चे तुकडे

निकाल प्रत्येक `GS1AIObjectElement` वर `GS1AIInterpretation` नोंदी म्हणून जोडले जातात.

---

## पार्स रचना (`ParseConfig`)

`GaiaParser` नेमके दोनच प्रवेशबिंदू उपलब्ध करतो:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` **पूर्वनिर्धारित रचनेसह** चालतो: `INTERPRETATION` पद्धत, `/` विभाजक व चार-अंकी वर्षासह लहान-टोकाचे दिनांक (`dd/mm/yyyy`), आणि **इंग्रजी** चूक-संदेश. यांपैकी काहीही बदलण्यासाठी — पार्स पद्धतीसह — प्रवाही बिल्डरने `ParseConfig` तयार करा आणि दोन-कारकांचे रूप वापरा.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

पर्यायांचे सर्व enum `GaiaConstants` मध्ये आहेत.

### पर्याय

| बिल्डर पद्धत | Enum (`GaiaConstants`) | पूर्वनिर्धारित | परिणाम |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | पाइपलाइनची खोली — पाहा [पार्स पद्धती](#परस-पदधत). |
| `language(...)`      | `Language`      | `ENGLISH`        | चूक-संदेश, विवेचन लेबले **आणि** AI वर्णनांची भाषा. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | दिनांक घटकांचा क्रम: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | दिनांक घटकांमधील अक्षर: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) किंवा `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) किंवा `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | संरचनात्मक "आवश्यक आहे" तपासणी (`GE-S005`) वगळतो. |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | संरचनात्मक "वर्जित आहे" तपासणी (`GE-S006`) वगळतो. |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / वर्गनाव | काहीही नाही | पार्सिंगआधी कच्चे इनपुट पुन्हा लिहिणारा कोड — दोन [अंगभूत मॉडिफायर](#अगभत-मडफयर) आणि तुम्ही लिहिलेले कोणतेही. पाहा [इनपुट मॉडिफायर](#इनपट-मडफयर). |

दिनांकाचे हे चार पर्याय केवळ विवेचन-समृद्धकांनी तयार केलेल्या स्वरूपित दिनांक स्ट्रिंगवरच परिणाम करतात (`INTERPRETATION` पद्धतीत); ते पडताळणी बदलत नाहीत. बिल्डरची मूल्ये वगळता येतात — जो पर्याय ठरवला नाही (किंवा ज्याला `null` दिले) तो आपले पूर्वनिर्धारित मूल्यच राखतो.

### स्थानिकीकृत संदेश आणि लेबले

`language(...)` माणसाला वाचता येणाऱ्या **तीन** प्रकारच्या मजकुराची भाषा निवडतो: चूक-संदेश, विवेचन लेबले (प्रत्येक `GS1AIInterpretation` चे `getLabel()`), आणि AI वर्णने (प्रत्येक `GS1AIObjectElement` चे `getDescription()`).

`GaiaConstants.Language` मध्ये **३५ भाषा** व्याख्यायित आहेत, ज्या जगातील सर्वाधिक बोलल्या जाणाऱ्या भाषा व्यापतात: इंग्रजी, फ्रेंच, स्पॅनिश, जर्मन, इटालियन, पोर्तुगीज, डच, पोलिश, रशियन, युक्रेनियन, झेक, स्वीडिश, चिनी, जपानी, कोरियन, अरबी, इंडोनेशियन, हिंदी, तुर्की, बंगाली, उर्दू, व्हिएतनामी, नायजेरियन पिजिन, इजिप्शियन अरबी, मराठी, तेलुगू, तमिळ, कँटोनीज, वू चिनी, तागालोग, फार्सी, हौसा, पंजाबी, जावानीज आणि स्वाहिली.

अनुवादाची स्थिती (जशी वितरित होते):
- **विवेचन लेबले** — सर्व भाषांसाठी अनुवादित.
- **चूक-संदेश** — सर्व भाषांसाठी अनुवादित.
- **AI वर्णने** — इंग्रजी वगळून सर्व भाषांसाठी अनुवादित. इंग्रजीची वेगळी यादी नाही: ती थेट `gs1-application-identifiers.jsonld` मधील त्या AI च्या नोंदीतील `description` क्षेत्रातून वाचली जाते, आणि अखेरीस प्रत्येक AI वर्णन तिथेच परत येते.

नायजेरियन पिजिन (`NIGERIAN_PIDGIN`) ही इंग्रजीवर आधारित क्रिओल भाषा विवेचन लेबले आणि चूक-संदेशांसाठी मुद्दामच इंग्रजी मजकूरच पुन्हा वापरते. AI वर्णने या अपवादाचा अपवाद आहेत: ती इंग्रजी पुन्हा वापरण्याऐवजी खऱ्या पिजिन शैलीत अनुवादित आहेत, कारण AI-वर्णनांच्या याद्या लेबल/संदेश याद्यांपासून स्वतंत्रपणे तयार झाल्या. उत्पादन वातावरणात विसंबण्याआधी यंत्र-अनुवादांची मातृभाषकांकडून तपासणी करून घेणे उचित.

एखाद्या भाषेच्या यादीत नसलेला कोणताही संदेश, लेबल किंवा वर्णन इंग्रजीवर परत जातो. उजवीकडून डावीकडे लिहिल्या जाणाऱ्या भाषा (अरबी, उर्दू, इजिप्शियन अरबी, फार्सी) स्ट्रिंग म्हणून योग्यरीत्या साठवलेल्या आहेत; त्या उजवीकडून डावीकडे दाखवणे ही प्रदर्शन-स्तराची जबाबदारी.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

विवेचन लेबलेही याच पद्धतीने स्थानिकीकृत होतात (मूल्ये तशीच राहतात — फक्त लेबले बदलतात):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI वर्णनेही याच पद्धतीने स्थानिकीकृत होतात (फक्त `getTitle()`, उदा. `"GTIN"`, स्थानिकीकृत होत नाही):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### दिनांकाचे स्वरूपन

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## इनपुट मॉडिफायर

**इनपुट मॉडिफायर** म्हणजे Gaia पार्स करण्याआधी कच्ची इनपुट स्ट्रिंग पुन्हा लिहिणारा कोड. जे इनपुट आधीच बिघडलेल्या अवस्थेत येते, त्यासाठी मॉडिफायर असतात — GS विभाजकाच्या जागी छापता येणारे अक्षर टाकणारा स्कॅनर, पेलोडला विक्रेत्याच्या खास उपसर्गात गुंडाळणारे मिडलवेअर, सर्व काही मोठ्या अक्षरांत बदलणारी होस्ट प्रणाली. प्रत्येक कॉलच्या ठिकाणी प्रत्येक स्ट्रिंग आधी हाताळत बसण्याऐवजी (आणि त्यांपैकी एखाद्यात सूक्ष्म चूक करण्याऐवजी), प्रमाणीकरण एकदाच `ParseConfig` वर नोंदवा आणि ते लागू करण्याचे काम पार्सरवर सोपवा.

मॉडिफायर `GaiaParser.parse(...)` च्या अगदी सुरुवातीला चालतात — सहसंबंध आयडी काढण्याआधी, AIM कोड आयडी ओळखण्याआधी, GS1 पाइपलाइनआधी. त्यानंतरच्या सर्व गोष्टींना केवळ पुन्हा लिहिलेली स्ट्रिंगच दिसते. दोन्ही [अंगभूत मॉडिफायर](#अगभत-मडफयर) धरून **पूर्वनिर्धारितपणे काहीही रचलेले नसते** — प्रत्येक `ParseConfig` साठी तुम्ही स्वतः ते निवडता.

**इंटरफेस:** `tools.pantheum.gaia.modifier.ModifierInterface`

### अंगभूत मॉडिफायर

कोअर jar मध्ये `tools.pantheum.gaia.modifier.custom` अंतर्गत दोन मॉडिफायर येतात. GS1 पेलोड सर्वात वारंवार ज्या दोन प्रकारे बिघडून येतो ते हे दोघे हाताळतात — माहिती समजल्या जाणाऱ्या छापील HRI कंस, आणि नको असलेल्या जागा — त्यामुळे नेहमीच्या प्रसंगांसाठी खास वर्ग लिहावा लागत नाही:

| वर्ग | `getName()` | काय करतो |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | प्रत्येक AI भोवतीचे HRI कंस (`(01)…(10)…`) काढून टाकतो आणि ते ज्या FNC1 विभाजकाचा निर्देश करत होते तो पुन्हा आणतो. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI घटक स्ट्रिंगमधून प्रत्येक जागा (`0x20`) काढून टाकतो. |

हे दोघेही कोणताही विशेष दर्जा नसलेले साधे `ModifierInterface` अंमलबजावणी आहेत — तुमच्या स्वतःच्या मॉडिफायरप्रमाणेच ते नोंदवले जातात, क्रम लावले जातात, अहवालात येतात आणि अपयशी ठरतात:

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

दोघेही अवस्थारहित आणि थ्रेड-सुरक्षित आहेत, त्यामुळे एकच प्रत सर्वांनी वापरता येते; आणि रचना-फाइलवर आधारित मांडणीसाठी दोघांनाही पूर्ण वर्गनावाने बोलावता येते (पाहा [मॉडिफायर नोंदवणे](#मडफयर-नदवण)).

#### `ModifierRemoveAIBrackets`

GS1 चे माणसाला वाचता येणारे विवेचन प्रत्येक AI कंसात छापते — `(01)09521234543213(10)ABC123` — ही निव्वळ छपाईची प्रथा आहे. HRI बाहेर टाकण्यासाठी रचलेला स्कॅनर किंवा मिडलवेअर ते कंस माहिती म्हणूनच पुढे पाठवतो, आणि टोकनायझरला त्यांचे काय करावे हे कळत नाही.

कंस काढणे हे कामाचे निम्मेच आहे. HRI मध्ये *पुढच्या* AI चा उघडणारा `(` हाच आधीच्या मूल्याचा शेवट दाखवतो, त्यामुळे कंसातील रूपात परिवर्तनीय-लांबीच्या AI ला FNC1 लागत नाही. कंस बिनदिक्कत काढून टाकले, तर ती सीमाच नाहीशी होते:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

म्हणून हा मॉडिफायर **ज्या प्रत्येक सीमेवर आधीचा AI परिवर्तनीय-लांबीचा असेल तिथे पुन्हा FNC1 घालतो**, आणि कंस जे एन्कोड करत होते तेच अगदी तसेच परत आणतो:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

लांबी पार्सरच्याच `AiDefinitionRegistry` मध्ये शोधली जाते, त्यामुळे कोडात ठोकून बसवलेल्या यादीऐवजी प्रत्येक परिवर्तनीय-लांबीचा AI हाताळला जातो. तीन प्रसंग मुद्दाम तसेच सोडले जातात: आधीच FNC1 वर संपणारे मूल्य (दोन्ही प्रथा बाहेर टाकणाऱ्या स्रोताला दुसरा विभाजक मिळत नाही), कंसातील असा कोड जो ओळखीचा AI नाही (अनोळखी AI आपल्या लांबीबद्दल काहीच सांगत नाही), आणि स्ट्रिंगमधला शेवटचा AI.

हे पुनर्लेखन **एकघाती** आहे — स्वतःच्याच आउटपुटवर चालवले तरी काही बदलत नाही — त्यामुळे ज्यात फक्त काही इनपुट कंसात असतात अशा मिश्र प्रवाहावरही ते सुरक्षित आहे.

> **मर्यादा.** `(` आणि `)` ही स्वतःच वैध GS1 माहिती-अक्षरे आहेत, आणि इथला नमुना केवळ `\((\d{2,4})\)` इतकाच आहे. एखाद्या मूल्यात योगायोगाने कंसातील दोन-ते-चार अंकी संख्या असेल, तर तिचेही कंस निघून जातील. हे केवळ HRI कंस-प्रथा वापरणाऱ्या स्रोतावरच लावा, खरोखर कंसातील मूल्ये असणाऱ्या स्रोतावर नको.

#### `ModifierRemoveSpaces`

काही स्कॅनर, मिडलवेअर आणि लेबल-छपाईच्या प्रणाली एरवी व्यवस्थित असलेल्या घटक स्ट्रिंगमध्ये नको असलेल्या जागा घुसवतात — निश्चित रुंदीचे क्षेत्र भरण्यासाठी, वाचनीय गट वेगळे करण्यासाठी, किंवा लांब मूल्य गुंडाळण्यासाठी. टोकनायझर त्यांपैकी प्रत्येक जागा माहिती मानतो, त्यामुळे ज्या मूल्यात ती बसली आहे ते बिघडते, आणि परिवर्तनीय-लांबीच्या AI मध्ये तिच्या पुढचे सर्व काही सरकते.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

फक्त ASCII `0x20` काढला जातो. इतर पांढऱ्या जागा जागच्या जागी राहतात — उदाहरणार्थ टॅब GS1 च्या एन्कोड करण्यायोग्य संचाबाहेर आहे, त्यामुळे पार्सर तो गुपचूप झाडून टाकण्याऐवजी `GE-S008` म्हणून कळवतो.

> **मर्यादा.** जागा (`0x20`) ही GS1 च्या अपरिवर्तनीय अक्षरसंचाचा भाग आहे, त्यामुळे एखाद्या बॅच/लॉट किंवा ग्राहकाच्या भाग-क्रमांकात रास्तपणे जागा असू शकते. नको असलेली जागा आणि खरी जागा यांतला फरक मॉडिफायरला कळत नाही; तो केवळ ज्या स्रोताबद्दल खात्री आहे की तो AI मूल्यांत जागा वापरत नाही, अशाच स्रोतावर लावा.

#### उपसर्ग वगळले जातात, पुन्हा लिहिले जात नाहीत

पार्सरने अजून काहीच काढलेले नसताना मॉडिफायर चालतात, त्यामुळे कच्च्या इनपुटमध्ये अजूनही सहसंबंध आयडी, AIM कोड आयडी आणि ECI निर्देशक असू शकतो. दोन्ही अंगभूत मॉडिफायर पार्सरच्याच `CorrelationIdParser` व `DataCarrierParser` तर्काने AI घटक स्ट्रिंगची सुरुवात शोधतात, तिथूनच पुढे पुन्हा लिहितात, आणि निकाल **न शिवलेल्या** उपसर्गाला पुन्हा जोडतात:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

ज्या EAN/UPC वाहकांचे मूल्य GTIN-14 पर्यंत भरले जाते (`isRequiresGtinPadding()`) ते पूर्णपणे वगळले जातात — त्यांचा पेलोड म्हणजे कोणतीही AI रचना नसलेले कच्चे संख्यात्मक बारकोड मूल्य, त्यामुळे तिथे कंस किंवा जागा यांना अर्थच उरत नाही.

#### क्रम: कंसांआधी जागा

दोन्ही वापरत असाल, तर **आधी `ModifierRemoveSpaces` नोंदवा**. कंस जुळवणे स्थानावर अवलंबून असते: जागांनी पसरलेला `( 01 )` हा `\((\d{2,4})\)` शी जुळत नाही, त्यामुळे कंस तसेच राहतात आणि ते ज्या विभाजकाचा निर्देश करत होते तो कधीच परत येत नाही.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### मॉडिफायर लिहिणे

दोन्ही अंगभूत मॉडिफायरपैकी एकही बसत नसेल तेव्हा स्वतःचा लिहा — इंटरफेसमध्ये एकच पद्धत आहे.

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

पुनर्लेखन पार्स रचनेवर अवलंबून असेल, तेव्हा त्याऐवजी दोन-कारकांचे रूप अधिलिखित करा:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

करार:

| नियम | तपशील |
|---|---|
| अवस्थारहित आणि थ्रेड-सुरक्षित | प्रत्येक वर्गाची एक प्रत साठवली जाते आणि प्रत्येक पार्समध्ये वाटून वापरली जाते. |
| कारकरहित सार्वजनिक कन्स्ट्रक्टर | मॉडिफायरला वर्गनावाने बोलावले जाईल तेव्हाच आवश्यक. |
| `null` आणि रिकामे इनपुट हाताळा | साखळी चालण्याआधी पार्सर ते गाळून टाकत नाही. |
| `null` परत करणे म्हणजे "बदल नाही" | आधीचे मूल्यच पुढे नेले जाते. मॉडिफायर लागू होत नसेल तेव्हा `input` जसाच्या तसा परत करा. |
| अपवाद फेकण्यापेक्षा जसाच्या तसा परत करणे बरे | अपवाद फेकणारा मॉडिफायर पार्सिंग रद्द करतो — पाहा [अपयश हाताळणी](#मडफयरच-अपयश-हतळण). |
| `getName()` | `ModifierInfo` वर कळवले जाणारे नाव ठरवण्यासाठी अधिलिखित करा; पूर्वनिर्धारित म्हणजे साधे वर्गनाव. |

### मॉडिफायर नोंदवणे

मॉडिफायर ज्या क्रमाने जोडता त्याच क्रमाने चालतात, आणि प्रत्येकाला आधीच्याचे आउटपुट मिळते. त्यांची नोंदणी प्रतीने, पूर्ण वर्गनावाने, किंवा यांपैकी कशाच्याही यादीने करा:

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

[अंगभूत मॉडिफायर](#अगभत-मडफयर) यांनाही तुमच्याच मॉडिफायरप्रमाणे नावे दिली जातात — **नेहमी पूर्ण नावाने**. त्यांच्यासाठी लघुनाव किंवा उपनाव-शोध नाही; `ModifierRegistry` प्रत्येक मॉडिफायर, तो सोबत आलेला असो वा नसो, पूर्ण वर्गनावानेच सोडवतो.

नावे `ModifierRegistry` सोडवतो; प्रत्येक वर्गाची एक प्रत त्याच्या कारकरहित कन्स्ट्रक्टरने एकदाच तयार करतो आणि तोच वर्ग नमूद करणाऱ्या पुढील प्रत्येक रचनेसाठी ती प्रत साठवून ठेवतो. हे सोडवणे **रचना तयार होताना** घडते, त्यामुळे जे नाव सापडत नाही, जे `ModifierInterface` अंमलात आणत नाही, किंवा ज्याची प्रत तयार होऊ शकत नाही, ते तिथेच `IllegalArgumentException` फेकते — पार्सच्या वेळी गुपचूप नाही. जो मॉडिफायर परावर्तनाने तयार होऊ शकत नाही (समजा त्यात एखादे अंतःक्षेपित अवलंबित्व आहे), तो आधीच नोंदवून ठेवता येतो म्हणजे नावाने बोलावणे शक्य राहते:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### मॉडिफायरने काय केले ते पाहणे

मॉडिफायर रचलेले असतील, तेव्हा `ParseResult.getPayload()` हे **बदललेले** इनपुट दाखवते. मूळ इनपुट `ModifierInfo` वर जपून ठेवले जाते:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` प्रत्येक मॉडिफायरचे `getName()` कळवते; त्याचे पूर्वनिर्धारित मूल्य साधे वर्गनाव असले तरी दोन्ही अंगभूत मॉडिफायर ते अधिलिखित करतात — म्हणून या दोघांची साखळी वर्गनावांऐवजी प्रदर्शन-नावे कळवते:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

एकही मॉडिफायर रचलेला नसेल तेव्हा `getModifierInfo()` `null` परत करते. मॉडिफायर चालले पण प्रत्येकाने इनपुट जसेच्या तसे परत केले, तर माहिती उपलब्ध असते आणि `isModified()` हे `false` असते — `getAppliedModifiers()` मध्ये केवळ ज्यांनी खरोखर इनपुट बदलले तेच मॉडिफायर येतात.

### मॉडिफायरचे अपयश हाताळणे

अपवाद फेकणारा मॉडिफायर पार्सिंग रद्द करतो. तो अपवाद दोषी मॉडिफायरचे नाव सांगणाऱ्या `GaiaModifierException` मध्ये गुंडाळला जातो, आणि निकालात `GE-I001` ही अंतर्गत चूक येते, जिच्या संदेशात तेच नाव असते; `getPayload()` न बदललेले इनपुट कळवते. अर्धवट पुन्हा लिहिलेल्या स्ट्रिंगसह पार्सिंग मुद्दामच **पुढे जात नाही** — गुपचूप अपयशी ठरलेली प्रमाणीकरणाची पायरी असे निकाल देईल जे दिसायला वैध वाटतील पण चुकीच्या इनपुटमधून पार्स झालेले असतील.

---

## पार्स पद्धती

प्रत्येक पद्धतीचे नाव ती चालवत असलेल्या सर्वात खोल [पाइपलाइन टप्प्यावरून](#परसग-पइपलइन) दिले आहे; त्याआधीचा प्रत्येक टप्पा तरीही चालतो.

| पद्धत | कुठपर्यंत चालते | कशाचे उत्तर देते |
|---|---|---|
| `DATA_CARRIER` | टप्पा १ (इनपुटची दिशा) | हे कोणत्या सिम्बॉलॉजीने वाहून आणले? |
| `SYNTAX` | टप्पा २ (वाक्यरचना) | AI कोड आणि लांब्या सुव्यवस्थित आहेत का? |
| `CONTENT` | टप्पा ३ (आशय) | मूल्ये वैध GS1 माहिती आहेत का? |
| `INTERPRETATION` | टप्पा ४ (विवेचन) | मूल्यांचा अर्थ काय? |

### DATA_CARRIER पद्धत

टप्पा १ नंतर थांबते — AIM कोड आयडीची पडताळणी करते आणि सिम्बॉलॉजी ओळखते, पण AI पार्सिंग पाइपलाइनमध्ये शिरत नाही. पूर्ण पडताळणीच्या खर्चाशिवाय सिम्बॉलॉजी ओळखण्यासाठी आणि दिशा ठरवण्यासाठी उपयुक्त.

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

**कधी वापरावी:** पेलोड कसा हाताळायचा हे ठरवण्याआधी तुमच्या अनुप्रयोगाला बारकोडचा प्रकार ओळखायचा असेल तेव्हा — उदा. 1D आणि 2D सिम्बॉलॉजी वेगवेगळ्या हँडलरकडे पाठवण्यासाठी. त्या दिशा-निवडीसाठी `getName()` ची स्ट्रिंग जुळवण्याऐवजी प्रकारयुक्त [`DataCarrierType`](#datacarrierentry-आण-datacarriertype) (`getDataCarrier().getDataCarrierType()`) वापरा.

---

### SYNTAX पद्धत

टप्पा २ नंतर थांबते. आशय-पडताळणीच्या खर्चाशिवाय संरचनात्मक पूर्वछाननीसाठी उपयुक्त.

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

**कधी वापरावी:** पूर्ण पडताळणीत उतरण्याआधी AI कोड आणि माहितीच्या लांब्या सुव्यवस्थित आहेत का हे तपासायचे असेल, किंवा जिथे आशयाच्या चुका विरळ आहेत अशा मोठ्या प्रमाणातील स्कॅनिंगमध्ये.

---

### CONTENT पद्धत

टप्पा ३ नंतर थांबते.

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

> बहुतेक AI एकटे उभे राहू शकत नाहीत: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) आणि
> `21` (SERIAL) या प्रत्येकाला त्याच घटक स्ट्रिंगमध्ये AI `01` सारखी ओळख-किल्ली *लागते*,
> त्यामुळे वरील उदाहरणातून GTIN काढून टाकल्यास आशय-पडताळणीपर्यंत पोहोचण्याआधीच टप्पा २ मध्ये
> `GE-S005` सह अपयश येईल. जे तुकडे मुद्दामच आपले सोबती AI वगळतात, ते पार्स करण्यासाठी
> `ParseConfig` वर `skipRequiresCheck(true)` ठेवा.

**कधी वापरावी:** स्कॅन केलेले मूल्य व्यावसायिक प्रक्रियेत वापरण्याआधी ते पूर्णपणे GS1-सुसंगत आहे का हे जाणून घ्यायचे असेल, पण विवेचन-समृद्धीचा भार नको असेल तेव्हा.

---

### INTERPRETATION पद्धत (पूर्वनिर्धारित)

टप्पा ४ पर्यंत संपूर्ण पाइपलाइन चालवते. पद्धत-कारकाविना `parse(String)` बोलावल्यास हीच पूर्वनिर्धारित असते. आशय-पडताळणी निर्दोषपणे उत्तीर्ण झालेले घटकच ती समृद्ध करते.

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

**उदाहरण आउटपुट:**
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

**आर्थिक रकमेचे उदाहरण (AI 3932 — ISO चलन कोडसह किंमत):**
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

**कधी वापरावी:** प्रदर्शन-स्तर, लेबल-पडताळणीची साधने, किंवा AI मूल्यांचे माणसाला सोयीचे पृथक्करण हवे असणारे कोणतेही UI बनवताना.

---

## सहसंबंध आयडी

काही कार्यप्रवाह कच्च्या GS1 इनपुटच्या आधी आपला स्वतःचा ८-अंकी सहसंबंध ओळखक्रमांक जोडतात, म्हणजे स्कॅनच्या घटना एखाद्या सत्राशी किंवा व्यवहाराशी जोडता येतील. त्याचे स्वरूप असे:

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

`~` (टिल्ड) हा विभाजक आहे. तो GS1 आशयाचा भाग **नाही** — कोणतेही GS1 पार्सिंग सुरू होण्याआधीच तो काढून टाकला जातो.

### ओळखण्याचे नियम

इनपुट नेमक्या ८ ASCII दशांश अंकांनी (`0`–`9`) सुरू होत असेल आणि लगेच पुढे `~` असेल, तेव्हाच उपसर्ग ओळखला जातो. नववे अक्षर `~` नसेल, किंवा पहिल्या ८ अक्षरांपैकी एखादे अंक नसेल, तर इनपुट सहसंबंध उपसर्गाविना साधा GS1 आशय मानला जातो.

### सहसंबंध आयडीपर्यंत पोहोचणे

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

### AIM कोड आयडीसोबत एकत्र

सहसंबंध उपसर्ग AIM कोड आयडीच्या आधी येऊ शकतो. पार्सर हे सहजपणे हाताळतो:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**अंमलबजावणी वर्ग:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** एक किंवा अधिक AI मूल्ये थेट HTTP(S) URL च्या रचनेतच एन्कोड करतो, ज्यामुळे प्रत्यक्ष वस्तूंना वेबवरून सोडवता येणारी ओळखचिन्हे मिळतात. GAIA **असंकुचित** URI साठी *GS1 Digital Link Standard: URI Syntax* (आवृत्ती १.७.०) अंमलात आणतो.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` Digital Link URI आपोआप ओळखतो — `http://` किंवा `https://` ने सुरू होणारे प्रत्येक इनपुट `GS1DLParser` कडे पाठवले जाते, जो घटक-स्ट्रिंग पाइपलाइनसारखेच आशय व विवेचनाचे टप्पे चालवतो.

### URI ची रचना आणि AI च्या भूमिका

Digital Link URI मधील प्रत्येक AI तीनपैकी एक भूमिका बजावतो, जी प्रत्येक `GS1AIObjectElement` वर `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) द्वारे मिळते:

| भूमिका | स्थान | उदाहरण |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | मार्गातील पहिली `/ai/value` जोडी (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | त्यानंतरच्या मार्ग-जोड्या, प्राथमिक किल्लीनुसार क्रमबद्ध (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | पूर्णपणे संख्यात्मक किल्ल्या असलेले क्वेरी पॅरामीटर (§4.10) | `?17=271231` |

लागू केले जाणारे संरचनात्मक नियम (`DLPathRules`):
- मार्गात नेमकी **एकच** प्राथमिक ओळख-किल्ली; अतिरिक्त किल्ल्या क्वेरी माहिती-गुणधर्म म्हणून एन्कोड कराव्याच लागतात.
- किल्ली-विशेषक प्राथमिक किल्लीला मान्य असावे लागतात आणि ठरलेल्या क्रमानेच यावे लागतात. ऐच्छिक विशेषक वगळता येतात, पण जे *आहेत* त्यांना तरीही ठरलेला क्रम पाळावाच लागतो — पाहा [विशेषकांचा क्रम](#वशषकच-करम).
- प्राथमिक किल्लीच्या आधी कोणतेही खास मार्ग-तुकडे येऊ शकतात (उदा. `/products/au/01/...`); ते `getDigitalLinkInfo().getCustomPathStem()` ने मिळवा.
- असंख्यात्मक क्वेरी किल्ल्या (`linkType`, `context`, `23P` सारखे विस्तार-पॅरामीटर) दुर्लक्षित होतात; पूर्णपणे संख्यात्मक किल्ल्या `validAsDataAttribute` ने खुणावलेले वैध AI असाव्या लागतात.
- टक्केवारी-एन्कोड केलेली मूल्य-अक्षरे उलगडली जातात; AI `(03)` आणि `(8014)` यांना परवानगी नाही.

प्राथमिक किल्ल्या आणि त्यांना मान्य असलेले विशेषक-क्रम कोडात ठोकून बसवलेले नसून AI व्याख्यांमधून **माहिती-आधारित** येतात — `gs1DigitalLinkPrimaryKey` खूण आणि `gs1DigitalLinkQualifiers` गुणधर्म यांतून.

कोणताही संरचनात्मक भंग, किंवा URL नसलेले इनपुट, Digital Link संरचनात्मक चूक निर्माण करते (`GE-L001`–`GE-L014`, प्रत्येक अटीसाठी एक कोड). विभागलेला URL मेटाडेटा (`scheme`, `domain`, `path`, `customPathStem`, `query`, आणि `java.net.URL`) संरचनात्मक चुका असतानाही `getDigitalLinkInfo()` द्वारे उपलब्ध राहतो.

### विशेषकांचा क्रम

प्रत्येक प्राथमिक किल्लीसाठी `gs1DigitalLinkQualifiers` एक किंवा अधिक **क्रमबद्ध** विशेषक-क्रम नोंदवते. एका क्रमामध्ये चौकोनी कंसात गुंडाळलेला AI **ऐच्छिक** असतो, तर कंसाविनाचा AI **अनिवार्य** — हे §4.9 च्या ABNF मधील `[cpv-comp]` संकेतनाचेच प्रतिबिंब आहे. एका प्राथमिक किल्लीचे क्रम हे परस्परांना वगळणारे पर्याय असतात.

उदाहरणार्थ, GTIN (`01`) दोन क्रम व्याख्यायित करतो:

| मार्ग | क्रम | अर्थ |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — प्रत्येक ऐच्छिक, पण याच क्रमाने निश्चित |
| upui-path | `235` | TPX (अनिवार्य); GTIN + TPX = UPUI |

म्हणून `/01/09506000134352/10/LOT-ABC/21/SER` वैध आहे (SER च्या आधी LOT, CPV वगळला), `/01/.../21/SER/10/LOT-ABC` **नाकारला** जातो (क्रम चुकला), आणि `/01/09506000134352/235/2ABC456` हा upui-path आहे. क्रम-तपासणी ही क्रम राखणारी उपक्रम-जुळवणी आहे, त्यामुळे ऐच्छिक AI वगळता येतात पण त्यांचा क्रम कधीही बदलता येत नाही.

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

**अंमलबजावणी वर्ग:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## निकालांसह काम करणे

### ParseResult

`GaiaParser.parse()` परत करत असलेला सर्वोच्च-स्तरीय निकाल.

| पद्धत | काय परत करते | वर्णन |
|---|---|---|
| `isValid()` | `boolean` | कोणत्याही पातळीवर चूक नसेल तर `true`. इशाऱ्यांचा वैधतेवर परिणाम होत नाही. `getAiObject()` `null` असताना नेहमी `true`. |
| `getPayload()` | `String` | सहसंबंध उपसर्ग काढल्यानंतरची — आणि कोणत्याही [इनपुट मॉडिफायरने](#इनपट-मडफयर) ती पुन्हा लिहिल्यानंतरची — इनपुट स्ट्रिंग. |
| `getPayloadContent()` | `String` | AIM कोड आयडी आणि ECI उपसर्ग काढल्यानंतरचा पेलोड. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (GS1 नसल्यामुळे नाकारलेला डेटा वाहक, उदा. Code 39 चा `]A0` वाहक), किंवा `UNABLE_TO_DETERMINE_CONTENT` (`aiObject` `null` असताना, उदा. `DATA_CARRIER` पद्धतीत). |
| `getRequestedParseMode()` | `ParseMode` | रचलेली पाइपलाइन खोली (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | पार्सिंग प्रत्यक्षात ज्या सर्वात खोल टप्प्यापर्यंत पोहोचले तो — खाली पाहा. |
| `isParseComplete()` | `boolean` | पार्सिंग मागितलेल्या खोलीपर्यंत पोहोचले तर `true` (`achieved == requested`). `isValid()` पासून स्वतंत्र. |
| `getAiObject()` | `GS1AIObject` | सोडवलेले सर्व AI. `DATA_CARRIER` पद्धतीत `null`. |
| `getErrors()` | `List<GaiaError>` | WARNING नसलेल्या सर्व चुका (ऑब्जेक्ट-पातळी + सर्व घटक-पातळी). |
| `getWarnings()` | `List<GaiaError>` | सर्व WARNING सूचना (ऑब्जेक्ट-पातळी + सर्व घटक-पातळी). |
| `hasWarnings()` | `boolean` | कोणतीही WARNING सूचना उठली असेल तर `true`. |
| `getIssues()` | `List<GaiaError>` | चुका आणि इशारे एकत्र. |
| `hasDataCarrier()` | `boolean` | AIM कोड आयडी ओळखला गेला असेल तर `true`. |
| `getDataCarrier()` | `DataCarrierEntry` | सिम्बॉलॉजीचा मेटाडेटा, किंवा वाहक ओळखला नसेल तर `null`. |
| `hasEci()` | `boolean` | पेलोडमधून ECI निर्देशक काढला असेल तर `true`. |
| `getEci()` | `EciEntry` | ECI एन्कोडिंगचा मेटाडेटा, किंवा `null`. |
| `hasCorrelationId()` | `boolean` | मूळ इनपुटमध्ये `DDDDDDDD~` सहसंबंध उपसर्ग असेल तर `true`. |
| `getCorrelationInfo()` | `CorrelationInfo` | काढलेला सहसंबंध आयडी, किंवा नसेल तर `null`. |
| `isInputModified()` | `boolean` | [इनपुट मॉडिफायरने](#इनपट-मडफयर) इनपुट बदलले असेल तर `true`. |
| `getModifierInfo()` | `ModifierInfo` | मॉडिफायर साखळीने काय केले — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. एकही मॉडिफायर रचलेला नसेल तर `null`. |
| `getTiming()` | `ProcessingTiming` | पार्सचा प्रत्यक्ष घड्याळी वेळ — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `GaiaParser` ने तयार केला नसेल तर `null`. |
| `getVersion()` | `String` | हा निकाल तयार करणारी लायब्ररी आवृत्ती. |

#### मागितलेली विरुद्ध गाठलेली पार्स पद्धत

पाइपलाइन **SYNTAX → CONTENT → INTERPRETATION** ही शिडी चढते आणि चुका आल्यास आधीच थांबते, त्यामुळे प्रत्यक्षात *गाठलेली* पद्धत *मागितलेल्या* पद्धतीपेक्षा उथळ असू शकते. `getAchievedParseMode()` ती कुठवर पोहोचली ते सांगते:

| मागितलेली | काय घडते | गाठलेली | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | **वाक्यरचनात्मक / संरचनात्मक** चूक टोकनीकरणानंतर पार्सिंग थांबवते | `SYNTAX` | `false` |
| `INTERPRETATION` | **आशयाची** चूक (चुकीचे स्वरूप/तपासणी अंक) समृद्धी अडवते | `CONTENT` | `false` |
| `CONTENT` | आशयाचा टप्पा नेहमीच पूर्ण चालतो (चुका नोंदवल्या जातात, प्राणघातक नसतात) | `CONTENT` | `true` |
| कोणतीही (स्वच्छ इनपुट) | पाइपलाइन मागितलेल्या खोलीपर्यंत पोहोचते | = मागितलेली | `true` |
| `DATA_CARRIER` | वाहकाची पडताळणी होते; AI आशय पार्स होत नाही | `DATA_CARRIER` | `true` |
| कोणतीही | AI पार्सिंगआधीच डेटा वाहक नाकारला जातो (उदा. GS1 नसलेला `]A0` वाहक) | `SYNTAX` | `false` |

`isParseComplete()` हे `isValid()` पासून स्वतंत्र आहे: चुकीच्या तपासणी अंकाच्या GTIN चे `CONTENT` पार्सिंग **पूर्ण** आहे (आशयाचा टप्पा चालला) पण **अवैध** आहे (तपासणी अंक अपयशी ठरला). "पाइपलाइन मी मागितली तितकी खोल चालली का?" हे विचारण्यासाठी `isParseComplete()` आणि "माहिती सुव्यवस्थित आहे का?" हे विचारण्यासाठी `isValid()` वापरा.

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

सोडवलेल्या AI घटकांचा संग्रह.

| पद्धत | वर्णन |
|---|---|
| `getAis()` | सर्व `GS1AIObjectElement` प्रती, इनपुटच्या क्रमाने. |
| `get(String aiCode)` | दिलेल्या AI कोडशी जुळणारा पहिला घटक, किंवा `null`. |
| `contains(String aiCode)` | त्या कोडचा AI असेल तर `true`. |
| `size()` | सोडवलेल्या AI ची संख्या. |
| `isValid()` | ऑब्जेक्ट-पातळीवर चूक नसेल आणि कोणत्याही घटकावर चूक नसेल तर `true`. |
| `toHriString()` | HRI स्ट्रिंग, उदा. `(01)09506000134352 (17)261231`. |
| `toElementString()` | कच्ची घटक स्ट्रिंग — कंसाविना, प्रत्येक परिवर्तनीय-लांबीच्या घटकानंतर FNC1 सह — उदा. `010950600013435210LOT-ABC<GS>17271231`. `isValid()` `false` असेल तर `null` परत करते. |
| `getContentType()` | `hasDigitalLink()` खरे असेल तेव्हा `GS1_DIGITAL_LINK`, अन्यथा `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | इनपुट प्राथमिक ओळख-किल्ली वाहणारा GS1 Digital Link URI असेल तर `true`. प्राथमिक किल्ली नसलेला सुव्यवस्थित URL तरीही `getDigitalLinkInfo()` देतो, पण इथे `false` परत करतो. |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` वरील प्रमाण GS1 Digital Link URI (§4.12) — प्राथमिक किल्ली आणि विशेषक मार्ग-तुकडे म्हणून, माहिती-गुणधर्म AI किल्लीनुसार क्रमबद्ध क्वेरी पॅरामीटर म्हणून — किंवा प्राथमिक किल्ली नसेल तर `null`. |
| `getDigitalLinkInfo()` | URI विभाजनाचा मेटाडेटा (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), किंवा Digital Link नसेल तर `null`. |
| `getAllErrors()` | ऑब्जेक्ट-पातळी + सर्व घटक चुका (WARNING वगळून). |
| `getAllWarnings()` | ऑब्जेक्ट-पातळी + सर्व घटक इशारे. |
| `getAllIssues()` | सर्व काही एकत्र. |

---

### GS1AIObjectElement

सोडवलेली एकच AI प्रत.

| पद्धत | वर्णन |
|---|---|
| `getAi()` | AI कोड, उदा. `"01"`, `"3102"`. |
| `getTitle()` | GS1 माहिती-शीर्षक, उदा. `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | AI चे संपूर्ण GS1 वर्णन, **पार्स भाषेत स्थानिकीकृत** (इंग्रजीत उदा. `"Global Trade Item Number (GTIN)"`). अनुवादित नसल्यास AI व्याख्येतील इंग्रजी मजकुरावर परत जाते. |
| `getFormatString()` | AI *आणि* त्याची माहिती दोन्ही व्यापणारा स्वरूप-वर्णक, उदा. AI `01` साठी `"N2+N14"`, AI `10` साठी `"N2+X..20"`, AI `3932` साठी `"N4+N3+N..15"`. |
| `getValue()` | घटक स्ट्रिंगमधून काढलेले कच्चे माहिती-मूल्य. |
| `isFixedLength()` | AI ची माहिती-लांबी निश्चित असेल तर `true`. |
| `getPosition()` | मूळ इनपुटमधील शून्यापासून मोजलेले अक्षर-स्थान. |
| `getGS1ComponentValues()` | प्रत्येक घटकानुसार मूल्य-तुकडे (अनेक घटक असलेल्या AI साठी). |
| `getErrors()` | घटक-पातळीवरील WARNING नसलेल्या चुका. |
| `getWarnings()` | घटक-पातळीवरील WARNING सूचना. |
| `getIssues()` | घटक-पातळीवरील चुका आणि इशारे एकत्र. |
| `hasErrors()` | WARNING नसलेली कोणतीही चूक जोडलेली असेल तर `true`. |
| `hasWarnings()` | कोणतीही WARNING सूचना जोडलेली असेल तर `true`. |
| `getInterpretations()` | `GS1AIInterpretation` नोंदी (INTERPRETATION पद्धतीत भरल्या जातात). |
| `getInterpretation(String type)` | दिलेल्या `GS1Constants_Enricher` प्रकार-किल्लीशी जुळणारे पहिले विवेचन, किंवा `null`. |
| `getDigitalLinkAIType()` | घटकाची Digital Link भूमिका (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), किंवा घटक-स्ट्रिंग इनपुटसाठी `null`. |
| `hasDigitalLinkAIType()` | Digital Link भूमिका नेमली असेल तर `true`. |

---

### GaiaError

न बदलणारी पडताळणी-चूक किंवा सूचना.

| पद्धत | वर्णन |
|---|---|
| `getId()` | यादीतील ओळखक्रमांक, उदा. `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, किंवा `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, किंवा `INTERNAL`. |
| `getCode()` | यंत्राला वाचता येणारा लघु कोड. |
| `getAi()` | चूक निर्माण करणारा AI कोड, किंवा ऑब्जेक्ट-पातळीच्या चुकांसाठी `null`. |
| `getMessage()` | मूल्ये भरून तयार झालेला, माणसाला वाचता येणारा संदेश. |
| `getPosition()` | मूळ इनपुटमधील शून्यापासून मोजलेले अक्षर-स्थान. |

---

### GS1AIInterpretation

`INTERPRETATION` पद्धतीत `GS1AIObjectElement` ला जोडलेला, लेबल असलेला एकच विवेचन-तुकडा.

| पद्धत | वर्णन |
|---|---|
| `getType()` | यंत्राला वाचता येणारी प्रकार-किल्ली, उदा. `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. सर्व भाषांत तीच राहते. |
| `getLabel()` | माणसाला वाचता येणारे लेबल, **पार्स भाषेत स्थानिकीकृत** (इंग्रजीत उदा. `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | काढलेले/समृद्ध केलेले मूल्य, उदा. `"31/12/2026"`, `"9506000"`. स्थानिकीकृत होत नाही. |

---

### DataCarrierEntry आणि DataCarrierType

इनपुटमध्ये AIM कोड आयडी असेल, तेव्हा `ParseResult.getDataCarrier()` एक `DataCarrierEntry` परत करते, जे माहिती वाहून आणणाऱ्या चिन्हाचे वर्णन करते. ही नोंद म्हणजे जुळलेल्या AIM कोड आयडीसाठीची नेमकी नोंदवही-नोंद; तर `DataCarrierType` म्हणजे ती ज्याचा भाग आहे तो संकलन-वेळचा enum.

#### DataCarrierEntry

ओळखल्या गेलेल्या एका AIM कोड आयडीचा मेटाडेटा (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| पद्धत | वर्णन |
|---|---|
| `getAimCodeId()` | जुळलेला AIM कोड आयडी, उदा. `"]C1"`. |
| `getName()` | त्या विशिष्ट चिन्हाचे माणसाला वाचता येणारे नाव, उदा. `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | वाहकाचे अधिक विस्तृत वर्णन. |
| `getType()` | वाहकाचा संरचनात्मक प्रकार स्ट्रिंग म्हणून (`getDataCarrierType().getCategory()` चे प्रतिबिंब). |
| `getStandard()` | नोंदवलेले असल्यास सिम्बॉलॉजीचे मानक. |
| `getDataCarrierType()` | या नोंदीसाठीचा प्रकारयुक्त `DataCarrierType` — कोडमधील दिशा-निवडीसाठी हाच वापरा. |
| `isGs1Capable()` | वाहक GS1 माहिती धारण करू शकत असेल तर `true` (AI घटक स्ट्रिंग आणि/किंवा Digital Link). |
| `isGs1AICapable()` | वाहक GS1 AI घटक स्ट्रिंग धारण करू शकत असेल तर `true`. |
| `isGs1DigitalLinkCapable()` | वाहक GS1 Digital Link URI धारण करू शकत असेल तर `true`. |
| `isEciCapable()` | वाहक ECI निर्देशकाला पाठिंबा देत असेल तर `true`. |
| `isRequiresGtinPadding()` | ज्या EAN/UPC/ITF वाहकांचे संख्यात्मक मूल्य AI पार्सिंगआधी GTIN-14 पर्यंत भरले जाते त्यांच्यासाठी `true`. |

#### DataCarrierType

डेटा-वाहक प्रकारांचा संकलन-वेळचा enum, ज्याची किल्ली ISO/IEC 15424 मध्ये नेमलेला AIM कोड आयडी आहे (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). `]` नंतरचे अक्षर (*कोड अक्षर*) कुळ निवडते; बहुतेक कुळे एकाच स्थिरांकावर मॅप होतात, जो सर्व उपप्रकार व्यापतो (`ITF` मध्ये `]I0`–`]I2` येतात; `EAN_UPC` मध्ये EAN-13, UPC-A, UPC-E आणि EAN-8). जिथे GS1 एखादा उपप्रकार AI माहितीसाठी राखून ठेवतो, तिथे तो प्रकार स्वतंत्र स्थिरांक असतो — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — त्यांच्या साध्या समकक्षांपासून वेगळा. AIM कोड आयडी नसेल, किंवा तो अनोळखी वाहकाचे नाव घेत असेल, तेव्हा प्रकार `UNKNOWN` असतो.

| पद्धत | वर्णन |
|---|---|
| `getCategory()` | व्यापक वर्ग `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, किंवा `OTHER`. |
| `getCodeChar()` | कुळ ओळखणारे AIM कोड अक्षर, उदा. QR Code साठी `"Q"`; `UNKNOWN` साठी `null`. |
| `getDisplayName()` | *प्रकाराचे* माणसाला वाचता येणारे नाव (`DataCarrierEntry.getName()` पेक्षा व्यापक असू शकते — उदा. `"EAN-13 / UPC-A / UPC-E / EAN-8"` विरुद्ध `"EAN-8"`). |
| `isGs1DataCarrier()` | नेहमीच GS1 AI माहिती दर्शवणाऱ्या स्थिरांकांसाठी `true`: GS1 साठी राखीव असलेले चार प्रकार (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) आणि त्याशिवाय `GS1_DATABAR`, जो स्वभावतःच GS1 आहे कारण प्रत्येक `]e` उपप्रकार GS1 DataBar आहे. हे `DataCarrierEntry.isGs1AICapable()` पेक्षा संकुचित आहे — साधा `QR_CODE` सुद्धा GS1 AI माहिती वाहू शकतो. |
| `static forAimCodeId(String)` | AIM कोड आयडीवरून थेट प्रकार सोडवतो (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); नसलेल्या, बिघडलेल्या किंवा न ओळखलेल्या आयडीसाठी `UNKNOWN` परत करतो. |

नावाऐवजी प्रकारानुसार दिशा ठरवणे — उदा. रेषीय (Code-128) चिन्हे 2D (QR / Data Matrix) पासून वेगळी करणे:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` मध्ये केवळ मॅट्रिक्स आणि ठिपक्यांची चिन्हे येतात; एकावर एक रचलेले रेषीय वाहक (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) हे `STACKED_LINEAR` आहेत, जरी त्यांना सर्रास "2D"
बारकोड म्हटले जाते. दोन्हींना एकच गट मानायचे असेल — म्हणजे लेझर स्कॅनरऐवजी इमेजर लागेल का
हे ठरवण्यासाठी — तर दोन्ही वर्ग तपासा.

> प्रकार सोडवण्यासाठी स्कॅनमध्ये AIM कोड आयडी असणे आवश्यक आहे; त्याविना `getDataCarrier()` हे `null` आणि प्रकार `UNKNOWN` असतो. स्कॅनर AIM कोड आयडी उपसर्ग पाठवेल अशी रचना करा.

---

## चूक संदर्भ

| कोड | पातळी | टप्पा | अर्थ |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | अनोळखी AI उपसर्ग — माहितीची लांबी ठरवता येत नाही |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | पूर्ण AI कोड वाचण्यासाठी इनपुट फारच लहान |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | तुटलेले मूल्य — AI ला लागणाऱ्यापेक्षा कमी अक्षरे |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | घटक स्ट्रिंगमध्ये पुनरावृत्त अ‍ॅप्लिकेशन आयडेंटिफायर |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | आवश्यक AI अवलंबित्व अनुपस्थित |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | वर्जित AI जोडी — एकत्र येऊ न शकणारे दोन AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | टोकनीकरणातील अनपेक्षित अपयश |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | घटक स्ट्रिंगमध्ये GS1 च्या एन्कोड करण्यायोग्य संचाबाहेरचे अक्षर |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | परिवर्तनीय-लांबीच्या AI नंतर आवश्यक FNC1 विभाजक अनुपस्थित |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | सर्व घटकांच्या कमाल मर्यादेपलीकडे उरलेली माहिती |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | स्ट्रिंगच्या मध्यभागी निश्चित-लांबीच्या AI नंतर FNC1 विभाजक |
| `GE-W002` | WARNING | SYNTAX | घटक स्ट्रिंगच्या शेवटी उरलेला FNC1 (केवळ सूचना) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI चे संरचनात्मक भंग — प्रत्येक अटीसाठी एक कोड (बिघडलेला URI, योजना, होस्ट, विशेषकांचा क्रम, वर्जित AI, प्राथमिक किल्ली नाही (`GE-L013`), अनेक प्राथमिक किल्ल्या (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | मूल्य AI च्या रेगेक्स नमुन्याशी जुळत नाही |
| `GE-C003` | DATA_ERROR | CONTENT | तपासणी अंकाची पडताळणी अपयशी |
| `GE-C004` | DATA_ERROR | CONTENT | तपासणी अक्षर-जोडीची पडताळणी अपयशी |
| `GE-C005` | FORMAT_ERROR | CONTENT | घटक-मूल्यात परवानगी असलेल्या अक्षरसंचाबाहेरचे अक्षर |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | घटक-स्वरूपाची अपयशे — प्रत्येक पडताळणी-अटीसाठी एक कोड (पाहा `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | विशेष अर्थविषयक पडताळणीची अपयशे — प्रत्येक पडताळणी-अटीसाठी एक कोड (पाहा `content/validator/`). **अपवाद:** खाली दिलेल्या १४ GS1 कंपनी उपसर्ग तपासण्यांची पातळी `WARNING` आहे, आणि `GE-C168` (न ओळखलेला ISO 3166-1 संख्यात्मक देश कोड) याची पातळी `FORMAT_ERROR` आहे. |
| GS1 कंपनी उपसर्ग तपासण्या | WARNING | CONTENT | GS1-किल्ली असलेल्या AI मध्ये किल्ली ओळखीच्या GS1 कंपनी उपसर्गाने सुरू होत नाही — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). केवळ सूचना — वैधतेवर परिणाम नाही. |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2) वर IMEI तपासणी अंक (Luhn) अपयशी |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM) वर EID तपासणी अंक (Luhn) अपयशी |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | न ओळखलेला AIM कोड आयडी |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | वाहक ओळखला, पण तो GS1 AI घटक स्ट्रिंग किंवा Digital Link URI यांपैकी कशालाही पाठिंबा देत नाही |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | अनपेक्षित अंतर्गत चूक |

> **संदेश दाखवण्यातील एक ज्ञात त्रुटी.** यादीतील नमुने भरलेली मूल्ये MessageFormat शैलीच्या
> दुहेरी अवतरणचिन्हांत (`''{value}''`) लिहितात, पण `ErrorRegistry` मूल्ये साध्या
> `String.replace` ने भरतो, त्यामुळे ते दुहेरीपण `getMessage()` पर्यंत टिकून राहते — या
> मार्गदर्शकात उद्धृत केलेल्या संदेशांत जिथे `value '09506000134351'` दिसते तिथे सध्या
> तुम्हाला `value ''09506000134351''` दिसेल. ३५ भाषांच्या सर्व याद्यांतील मूल्य उद्धृत
> करणाऱ्या प्रत्येक संदेशावर याचा परिणाम होतो. चूक-संदेश पार्स करू नका; `getId()` /
> `getCode()` वर जुळवा.

---

## थ्रेड सुरक्षा

`GaiaParser` एकदा तयार झाल्यावर थ्रेड-सुरक्षित असतो. एकच प्रत अनेक थ्रेडमध्ये वाटून आणि एकाच वेळी वापरता येते. शिफारस केलेली रीत म्हणजे अनुप्रयोग सुरू होताना एकच प्रत तयार करून तीच पुन्हा वापरणे:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` न बदलणारा आहे आणि वाटून वापरण्यास तितकाच सुरक्षित. थ्रेड-सुरक्षेची जी एकमेव जबाबदारी लायब्ररी तुमच्यासाठी पार पाडू शकत नाही ती [इनपुट मॉडिफायरची](#इनपट-मडफयर) आहे: प्रत्येक मॉडिफायरची एकच प्रत साठवून एकाच वेळी चालणाऱ्या सर्व पार्समध्ये वाटली जाते, त्यामुळे अंमलबजावणी अवस्थारहितच असायला हवी.

---

## परिशिष्ट अ — AI स्ट्रिंग स्थिरांक

`GS1Constants_AICodes` (पॅकेज `tools.pantheum.gaia.gs1.constants`) GAIA ओळखत असलेल्या प्रत्येक अ‍ॅप्लिकेशन आयडेंटिफायरसाठी एक `String` स्थिरांक घोषित करते. कोडमध्ये AI कोडचे लिटरल ठोकून बसवण्याऐवजी हे स्थिरांक वापरा:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

प्रत्येक स्थिरांक आपल्या AI कोडचे स्ट्रिंग रूप वाहतो (उदा. `AI_01_GTIN = "01"`).

### ओळख आणि अनुक्रमांकन

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | अनुक्रमिक शिपिंग कंटेनर कोड (SSCC). |
| `01` | `AI_01_GTIN` | जागतिक व्यापार वस्तू क्रमांक (GTIN). |
| `02` | `AI_02_CONTENT` | आतील व्यापार वस्तूंचा जागतिक व्यापार वस्तू क्रमांक (GTIN). |
| `03` | `AI_03_MTO_GTIN` | मागणीनुसार तयार (MtO) व्यापार वस्तूची ओळख (GTIN). |
| `10` | `AI_10_BATCH_LOT` | बॅच किंवा लॉट क्रमांक. |
| `20` | `AI_20_VARIANT` | अंतर्गत उत्पादन प्रकार. |
| `21` | `AI_21_SERIAL` | अनुक्रमांक. |
| `22` | `AI_22_CPV` | ग्राहक उत्पादन प्रकार. |
| `235` | `AI_235_TPX` | तृतीय पक्षाद्वारे नियंत्रित, जागतिक व्यापार वस्तू क्रमांकाचा (GTIN) अनुक्रमांकित विस्तार (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | उत्पादकाने दिलेली अतिरिक्त उत्पादन ओळख. |
| `241` | `AI_241_CUST_PART_NO` | ग्राहकाचा भाग क्रमांक. |
| `242` | `AI_242_MTO_VARIANT` | मागणीनुसार तयार उत्पादनाचा फरक क्रमांक. |
| `243` | `AI_243_PCN` | पॅकेजिंग घटक क्रमांक. |
| `250` | `AI_250_SECONDARY_SERIAL` | दुय्यम अनुक्रमांक. |
| `251` | `AI_251_REF_TO_SOURCE` | स्रोत घटकाचा संदर्भ. |
| `253` | `AI_253_GDTI` | जागतिक दस्तऐवज प्रकार ओळख क्रमांक (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | जागतिक स्थान क्रमांक (GLN) विस्तार घटक. |
| `255` | `AI_255_GCN` | जागतिक कूपन क्रमांक (GCN). |
| `30` | `AI_30_VAR_COUNT` | वस्तूंची परिवर्तनीय संख्या (परिवर्तनीय मापाची व्यापार वस्तू). |
| `37` | `AI_37_COUNT` | लॉजिस्टिक एककामध्ये असलेल्या व्यापार वस्तू किंवा तुकड्यांची संख्या. |

### दिनांक आणि वेळा

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | उत्पादन तारीख (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | देय तारीख (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | पॅकेजिंग तारीख (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | उत्तम वापर मुदत तारीख (YYMMDD). |
| `16` | `AI_16_SELL_BY` | विक्री मुदत तारीख (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | कालबाह्यता तारीख (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | यापूर्वी वितरण होऊ नये अशी तारीख-वेळ (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | यानंतर वितरण होऊ नये अशी तारीख-वेळ (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | प्रकाशन तारीख (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | कालबाह्यता तारीख आणि वेळ (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | प्रथम गोठवण्याची तारीख (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | कापणी तारीख (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | चाचणी मुदत तारीख (YYMMDD[hhmm]). |

### प्रमाण आणि माप — परिवर्तनीय माप (मेट्रिक)

`310n`–`369n` ही चार-अंकी AI कुळे परिवर्तनीय मापाची प्रमाणे एन्कोड करतात. तिसरा अंक मापाचा प्रकार निवडतो; आणि **चौथा अंक** (`n`, ०–५) म्हणजे अध्याहृत दशांश स्थानांची संख्या — म्हणजे `AI_3102_NET_WEIGHT_KG` याचा अर्थ २ दशांश स्थानांसह किलोग्रॅममधील निव्वळ वजन.

| कुळ | स्थिरांकाचा नमुना (`n` = दशांश अंक) | वर्णन |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | निव्वळ वजन, किलोग्रॅम (परिवर्तनीय मापाची व्यापार वस्तू). |
| `311n` | `AI_311n_LENGTH_M` | लांबी किंवा पहिले परिमाण, मीटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `312n` | `AI_312n_WIDTH_M` | रुंदी, व्यास किंवा दुसरे परिमाण, मीटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `313n` | `AI_313n_HEIGHT_M` | खोली, जाडी, उंची किंवा तिसरे परिमाण, मीटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `314n` | `AI_314n_AREA_M` | क्षेत्रफळ, चौरस मीटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `315n` | `AI_315n_NET_VOLUME_L` | निव्वळ घनफळ, लिटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `316n` | `AI_316n_NET_VOLUME_M` | निव्वळ घनफळ, घन मीटर (परिवर्तनीय मापाची व्यापार वस्तू). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | लॉजिस्टिक वजन, किलोग्रॅम. |
| `331n` | `AI_331n_LENGTH_M_LOG` | लांबी किंवा पहिले परिमाण, मीटर. |
| `332n` | `AI_332n_WIDTH_M_LOG` | रुंदी, व्यास किंवा दुसरे परिमाण, मीटर. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | खोली, जाडी, उंची किंवा तिसरे परिमाण, मीटर. |
| `334n` | `AI_334n_AREA_M_LOG` | क्षेत्रफळ, चौरस मीटर. |
| `335n` | `AI_335n_VOLUME_L_LOG` | लॉजिस्टिक घनफळ, लिटर. |
| `336n` | `AI_336n_VOLUME_M_LOG` | लॉजिस्टिक घनफळ, घन मीटर. |
| `337n` | `AI_337n_KG_PER_M` | किलोग्रॅम प्रति चौरस मीटर. |

### प्रमाण आणि माप — परिवर्तनीय माप (इंपीरियल / अमेरिकी)

| कुळ | स्थिरांकाचा नमुना (`n` = दशांश अंक) | वर्णन |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | निव्वळ वजन, पौंड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `321n` | `AI_321n_LENGTH_IN` | लांबी किंवा पहिले परिमाण, इंच (परिवर्तनीय मापाची व्यापार वस्तू). |
| `322n` | `AI_322n_LENGTH_FT` | लांबी किंवा पहिले परिमाण, फूट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `323n` | `AI_323n_LENGTH_YD` | लांबी किंवा पहिले परिमाण, यार्ड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `324n` | `AI_324n_WIDTH_IN` | रुंदी, व्यास किंवा दुसरे परिमाण, इंच (परिवर्तनीय मापाची व्यापार वस्तू). |
| `325n` | `AI_325n_WIDTH_FT` | रुंदी, व्यास किंवा दुसरे परिमाण, फूट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `326n` | `AI_326n_WIDTH_YD` | रुंदी, व्यास किंवा दुसरे परिमाण, यार्ड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `327n` | `AI_327n_HEIGHT_IN` | खोली, जाडी, उंची किंवा तिसरे परिमाण, इंच (परिवर्तनीय मापाची व्यापार वस्तू). |
| `328n` | `AI_328n_HEIGHT_FT` | खोली, जाडी, उंची किंवा तिसरे परिमाण, फूट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `329n` | `AI_329n_HEIGHT_YD` | खोली, जाडी, उंची किंवा तिसरे परिमाण, यार्ड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | लॉजिस्टिक वजन, पौंड. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | लांबी किंवा पहिले परिमाण, इंच. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | लांबी किंवा पहिले परिमाण, फूट. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | लांबी किंवा पहिले परिमाण, यार्ड. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | रुंदी, व्यास किंवा दुसरे परिमाण, इंच. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | रुंदी, व्यास किंवा दुसरे परिमाण, फूट. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | रुंदी, व्यास किंवा दुसरे परिमाण, यार्ड. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | खोली, जाडी, उंची किंवा तिसरे परिमाण, इंच. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | खोली, जाडी, उंची किंवा तिसरे परिमाण, फूट. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | खोली, जाडी, उंची किंवा तिसरे परिमाण, यार्ड. |
| `350n` | `AI_350n_AREA_IN` | क्षेत्रफळ, चौरस इंच (परिवर्तनीय मापाची व्यापार वस्तू). |
| `351n` | `AI_351n_AREA_FT` | क्षेत्रफळ, चौरस फूट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `352n` | `AI_352n_AREA_YD` | क्षेत्रफळ, चौरस यार्ड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `353n` | `AI_353n_AREA_IN_LOG` | क्षेत्रफळ, चौरस इंच. |
| `354n` | `AI_354n_AREA_FT_LOG` | क्षेत्रफळ, चौरस फूट. |
| `355n` | `AI_355n_AREA_YD_LOG` | क्षेत्रफळ, चौरस यार्ड. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | निव्वळ वजन, ट्रॉय औंस (परिवर्तनीय मापाची व्यापार वस्तू). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | निव्वळ वजन (किंवा घनफळ), औंस (परिवर्तनीय मापाची व्यापार वस्तू). |
| `360n` | `AI_360n_NET_VOLUME_QT` | निव्वळ घनफळ, क्वार्ट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | निव्वळ घनफळ, यू.एस. गॅलन (परिवर्तनीय मापाची व्यापार वस्तू). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | लॉजिस्टिक घनफळ, क्वार्ट. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | लॉजिस्टिक घनफळ, यू.एस. गॅलन. |
| `364n` | `AI_364n_NET_VOLUME_IN` | निव्वळ घनफळ, घन इंच (परिवर्तनीय मापाची व्यापार वस्तू). |
| `365n` | `AI_365n_NET_VOLUME_FT` | निव्वळ घनफळ, घन फूट (परिवर्तनीय मापाची व्यापार वस्तू). |
| `366n` | `AI_366n_NET_VOLUME_YD` | निव्वळ घनफळ, घन यार्ड (परिवर्तनीय मापाची व्यापार वस्तू). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | लॉजिस्टिक घनफळ, घन इंच. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | लॉजिस्टिक घनफळ, घन फूट. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | लॉजिस्टिक घनफळ, घन यार्ड. |

### किंमत आणि आर्थिक रकमा

चौथा अंक (`n`) अध्याहृत दशांश स्थानांची संख्या एन्कोड करतो. परवानगी असलेली श्रेणी
कुळानुसार बदलते — `n` स्तंभ पाहा.

| कुळ | स्थिरांकाचा नमुना (`n` = दशांश अंक) | `n` | वर्णन |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | लागू देय रक्कम किंवा कूपन मूल्य, स्थानिक चलनात. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | ISO चलन कोडसह लागू देय रक्कम. |
| `392n` | `AI_392n_PRICE` | 0–9 | लागू देय रक्कम, एकल चलन क्षेत्र (परिवर्तनीय मापाची व्यापार वस्तू). |
| `393n` | `AI_393n_PRICE` | 0–9 | ISO चलन कोडसह लागू देय रक्कम (परिवर्तनीय मापाची व्यापार वस्तू). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | कूपनवरील टक्केवारी सूट. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | प्रति मापन एकक देय रक्कम, एकल चलन क्षेत्र (परिवर्तनीय मापाची व्यापार वस्तू). |

### स्थान आणि पाठवणी

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | ग्राहकाचा खरेदी आदेश क्रमांक. |
| `401` | `AI_401_GINC` | मालवाहतुकीसाठी जागतिक ओळख क्रमांक (GINC). |
| `402` | `AI_402_GSIN` | जागतिक शिपमेंट ओळख क्रमांक (GSIN). |
| `403` | `AI_403_ROUTE` | मार्गनिर्देशन कोड. |
| `410` | `AI_410_SHIP_TO_LOC` | पाठवण्याच्या/वितरणाच्या ठिकाणाचा जागतिक स्थान क्रमांक (GLN). |
| `411` | `AI_411_BILL_TO` | बिल/इनव्हॉइस प्राप्तकर्त्याचा जागतिक स्थान क्रमांक (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | ज्याकडून खरेदी केली त्याचा जागतिक स्थान क्रमांक (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | पुढे पाठवण्यासाठीचा जागतिक स्थान क्रमांक (GLN) - शिप फॉर/डिलिव्हर फॉर. |
| `414` | `AI_414_LOC_NO` | प्रत्यक्ष स्थानाची ओळख - जागतिक स्थान क्रमांक (GLN). |
| `415` | `AI_415_PAY_TO` | इनव्हॉइस जारी करणाऱ्या पक्षाचा जागतिक स्थान क्रमांक (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | उत्पादन किंवा सेवा स्थानाचा जागतिक स्थान क्रमांक (GLN). |
| `417` | `AI_417_PARTY` | पक्षाचा जागतिक स्थान क्रमांक (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | एका टपाल प्राधिकरणांतर्गत पाठवण्याच्या/वितरणाच्या ठिकाणाचा टपाल कोड. |
| `421` | `AI_421_SHIP_TO_POST` | ISO देश कोडसह पाठवण्याच्या/वितरणाच्या ठिकाणाचा टपाल कोड. |
| `422` | `AI_422_ORIGIN` | व्यापार वस्तूचा उगम देश. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | प्रारंभिक प्रक्रिया देश. |
| `424` | `AI_424_COUNTRY_PROCESS` | प्रक्रिया देश. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | विघटन देश. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | संपूर्ण प्रक्रिया साखळी व्यापणारा देश. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | उगम देशातील उपविभाग. |
| `4300` | `AI_4300_SHIP_TO_COMP` | पाठवण्याच्या/वितरणाच्या कंपनीचे नाव. |
| `4301` | `AI_4301_SHIP_TO_NAME` | पाठवण्याच्या/वितरणाच्या संपर्क व्यक्ती. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | पाठवण्याचा/वितरणाचा पत्ता - ओळ 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | पाठवण्याचा/वितरणाचा पत्ता - ओळ 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | पाठवण्याचे/वितरणाचे उपनगर. |
| `4305` | `AI_4305_SHIP_TO_LOC` | पाठवण्याचे/वितरणाचे स्थान. |
| `4306` | `AI_4306_SHIP_TO_REG` | पाठवण्याचा/वितरणाचा प्रदेश. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | पाठवण्याच्या/वितरणाच्या देशाचा कोड. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | पाठवण्यासाठी/वितरणासाठी दूरध्वनी क्रमांक. |
| `4309` | `AI_4309_SHIP_TO_GEO` | पाठवण्याच्या/वितरणाच्या भौगोलिक स्थानाचे स्थान. |
| `4310` | `AI_4310_RTN_TO_COMP` | परत करण्याच्या कंपनीचे नाव. |
| `4311` | `AI_4311_RTN_TO_NAME` | परत करण्यासाठी संपर्क व्यक्ती. |
| `4312` | `AI_4312_RTN_TO_ADD1` | परत करण्याचा पत्ता - ओळ 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | परत करण्याचा पत्ता - ओळ 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | परत करण्याचे उपनगर. |
| `4315` | `AI_4315_RTN_TO_LOC` | परत करण्याचे स्थान. |
| `4316` | `AI_4316_RTN_TO_REG` | परत करण्याचा प्रदेश. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | परत करण्याच्या देशाचा कोड. |
| `4318` | `AI_4318_RTN_TO_POST` | परत करण्याचा टपाल कोड. |
| `4319` | `AI_4319_RTN_TO_PHONE` | परत करण्यासाठी दूरध्वनी क्रमांक. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | सेवा कोड वर्णन. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | धोकादायक माल चिन्ह. |
| `4322` | `AI_4322_AUTH_LEAVE` | स्वाक्षरीशिवाय माल सोडण्याची परवानगी. |
| `4323` | `AI_4323_SIG_REQUIRED` | स्वाक्षरी आवश्यक चिन्ह. |
| `4330` | `AI_4330_MAX_TEMP_F` | फॅरनहाइटमधील कमाल तापमान (अंशाच्या शतांशात). |
| `4331` | `AI_4331_MAX_TEMP_C` | सेल्सिअसमधील कमाल तापमान (अंशाच्या शतांशात). |
| `4332` | `AI_4332_MIN_TEMP_F` | फॅरनहाइटमधील किमान तापमान (अंशाच्या शतांशात). |
| `4333` | `AI_4333_MIN_TEMP_C` | सेल्सिअसमधील किमान तापमान (अंशाच्या शतांशात). |

### उत्पादनाचे गुणधर्म आणि मागोवा

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | नाटो साठा क्रमांक (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE मांस शव आणि तुकडे वर्गीकरण. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | सक्रिय क्षमता. |
| `7005` | `AI_7005_CATCH_AREA` | मासेमारी क्षेत्र. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | मत्स्यव्यवसायाच्या उद्देशासाठी प्रजाती. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | मासेमारी उपकरण प्रकार. |
| `7010` | `AI_7010_PROD_METHOD` | उत्पादन पद्धत. |
| `7020` | `AI_7020_REFURB_LOT` | नूतनीकरण लॉट ओळख क्रमांक. |
| `7021` | `AI_7021_FUNC_STAT` | कार्यात्मक स्थिती. |
| `7022` | `AI_7022_REV_STAT` | सुधारणा स्थिती. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | असेंब्लीचा जागतिक वैयक्तिक मालमत्ता ओळख क्रमांक (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | तीन-अंकी ISO देश कोडसह प्रक्रियाकर्त्याचा क्रमांक (१० जागा). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC विस्तार 1 आणि आयातदार निर्देशांकासह. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT मालवाहतूक एकक प्रकार. |

### राष्ट्रीय आरोग्य परतफेड क्रमांक (NHRN)

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - जर्मनी PZN. |
| `711` | `AI_711_NHRN_CIP` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - फ्रान्स CIP. |
| `712` | `AI_712_NHRN_CN` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - स्पेन CN. |
| `713` | `AI_713_NHRN_DRN` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - ब्राझील DRN. |
| `714` | `AI_714_NHRN_AIM` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - पोर्तुगाल AIM. |
| `715` | `AI_715_NHRN_NDC` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - अमेरिका NDC. |
| `716` | `AI_716_NHRN_AIC` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - इटली AIC. |
| `717` | `AI_717_NHRN_SRN` | राष्ट्रीय आरोग्यसेवा परतफेड क्रमांक (NHRN) - कोस्टा रिका स्वच्छता नोंदणी क्रमांक. |

### आरोग्यसेवा, GMN, HIDRI, CPID आणि व्यक्तिविषयक माहिती

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | प्रमाणन संदर्भ (१० जागा). |
| `7240` | `AI_7240_PROTOCOL` | प्रोटोकॉल ओळख क्रमांक. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC माध्यम प्रकार. |
| `7242` | `AI_7242_VCN` | आवृत्ती नियंत्रण क्रमांक (VCN). |
| `7250` | `AI_7250_DOB` | जन्म तारीख (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | जन्म तारीख आणि वेळ (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | जैविक लिंग. |
| `7253` | `AI_7253_FAMILY_NAME` | व्यक्तीचे आडनाव. |
| `7254` | `AI_7254_GIVEN_NAME` | व्यक्तीचे पहिले नाव. |
| `7255` | `AI_7255_SUFFIX` | व्यक्तीच्या नावाचा प्रत्यय. |
| `7256` | `AI_7256_FULL_NAME` | व्यक्तीचे पूर्ण नाव. |
| `7257` | `AI_7257_PERSON_ADDR` | व्यक्तीचा पत्ता. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | बाळाचा जन्म क्रम. |
| `7259` | `AI_7259_BABY` | बाळाचे आडनाव. |
| `8001` | `AI_8001_DIMENSIONS` | रोल उत्पादने (रुंदी, लांबी, गाभा व्यास, दिशा, जोड). |
| `8002` | `AI_8002_CMT_NO` | मोबाईल दूरध्वनी ओळख क्रमांक. |
| `8003` | `AI_8003_GRAI` | जागतिक परतयोग्य मालमत्ता ओळख क्रमांक (GRAI). |
| `8004` | `AI_8004_GIAI` | जागतिक वैयक्तिक मालमत्ता ओळख क्रमांक (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | प्रति मापन एकक किंमत. |
| `8006` | `AI_8006_ITIP` | वैयक्तिक व्यापार वस्तू तुकड्याची ओळख (ITIP). |
| `8007` | `AI_8007_IBAN` | आंतरराष्ट्रीय बँक खाते क्रमांक (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | उत्पादन तारीख आणि वेळ (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | प्रकाशीय वाचनीय सेन्सर निर्देशक. |
| `8010` | `AI_8010_CPID` | घटक/भाग ओळख क्रमांक (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | घटक/भाग ओळख क्रमांकाचा अनुक्रमांक (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | सॉफ्टवेअर आवृत्ती. |
| `8013` | `AI_8013_GMN` | जागतिक मॉडेल क्रमांक (GMN). |
| `8014` | `AI_8014_MUDI` | अति-वैयक्तिकृत उपकरण नोंदणी ओळख क्रमांक (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | सेवा देणारी संस्था आणि सेवा पुरवठादार यांच्यातील संबंध ओळखण्यासाठी जागतिक सेवा संबंध क्रमांक (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | सेवा देणारी संस्था आणि सेवा प्राप्तकर्ता यांच्यातील संबंध ओळखण्यासाठी जागतिक सेवा संबंध क्रमांक (GSRN). |
| `8019` | `AI_8019_SRIN` | सेवा संबंध घटना क्रमांक (SRIN). |
| `8020` | `AI_8020_REF_NO` | देयक पावती संदर्भ क्रमांक. |
| `8026` | `AI_8026_ITIP_CONTENT` | लॉजिस्टिक एककामध्ये असलेल्या व्यापार वस्तू तुकड्यांची (ITIP) ओळख. |
| `8030` | `AI_8030_DIGSIG` | डिजिटल स्वाक्षरी (DigSig). |
| `8040` | `AI_8040_IMEI` | आंतरराष्ट्रीय मोबाईल उपकरण ओळख क्रमांक (IMEI). |
| `8041` | `AI_8041_IMEI2` | आंतरराष्ट्रीय मोबाईल उपकरण ओळख क्रमांक 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | एम्बेडेड सिम क्रमांक. |
| `8043` | `AI_8043_PSIM` | प्रत्यक्ष सिम क्रमांक. |
| `8110` | `AI_8110` | उत्तर अमेरिकेत वापरण्यासाठी कूपन कोड ओळख. |
| `8111` | `AI_8111_POINTS` | कूपनचे लॉयल्टी गुण. |
| `8112` | `AI_8112` | उत्तर अमेरिकेत वापरण्यासाठी सकारात्मक ऑफर फाइल कूपन कोड ओळख. |
| `8200` | `AI_8200_PRODUCT_URL` | विस्तारित पॅकेजिंग URL. |

### अंतर्गत / कंपनी वापर

| AI | स्थिरांक | वर्णन |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | व्यापारी भागीदारांमध्ये परस्पर मान्य केलेली माहिती. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | कंपनीची अंतर्गत माहिती (९ जागा). |

---

## परिशिष्ट ब — विवेचन किल्ली स्थिरांक

`GaiaParser.parse()` ला `ParseMode.INTERPRETATION` सह बोलावले असता, प्रत्येक `GS1AIObjectElement` क्षेत्र-विशिष्ट समृद्धकांनी तयार केलेल्या `GS1AIInterpretation` ऑब्जेक्टची यादी वाहू शकतो. विशिष्ट विवेचन-मूल्ये शोधण्यासाठी `GS1Constants_Enricher` स्थिरांक (पॅकेज `tools.pantheum.gaia.gs1.constants`) किल्ल्या म्हणून वापरा:

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

प्रदर्शन-लेबले स्थिरांक **नाहीत** — ती `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` अंतर्गत स्थानिकीकृत याद्यांत असतात, आणि त्यांची किल्ली प्रकार-स्थिरांक असते. `GS1AIInterpretation.getLabel()` लेबल पार्स भाषेत परत करते (पाहा [स्थानिकीकृत संदेश आणि लेबले](#सथनककत-सदश-आण-लबल)), आणि एखाद्या यादीत किल्ली नसेल तेव्हा इंग्रजीवर परत जाते. खालील प्रदर्शन-लेबल स्तंभ मराठी मजकूर देतो; प्रकार-किल्ल्या मात्र सर्व भाषांत तशाच राहतात, म्हणून नेहमी किल्लीवर जुळवा, कधीही लेबलावर नाही.

### दिनांक आणि वेळ

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `DATE_VALUE` | दिनांक | दिनांक AI (11–17, 7003, 7006, 7011, इत्यादी) |
| `DATE_FORMAT` | दिनांक स्वरूप | दिनांक AI |
| `TIME_VALUE` | वेळ | वेळ वाहणारे AI (7003, 7011, 8008, इत्यादी) |
| `TIME_FORMAT` | वेळ स्वरूप | वेळ वाहणारे AI |
| `DATETIME_VALUE` | दिनांक व वेळ | दिनांक+वेळ AI |
| `DATETIME_FORMAT` | दिनांक व वेळ स्वरूप | दिनांक+वेळ AI |

### कापणीची तारीख

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | कापणी सुरुवात दिनांक | AI 7007 |
| `HARVEST_END_DATE` | कापणी समाप्ती दिनांक | AI 7007 (ऐच्छिक श्रेणी-अंत) |
| `HARVEST_DATE_RANGE` | कापणी दिनांक श्रेणी | AI 7007 |

### GS1 कंपनी उपसर्ग

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 कंपनी उपसर्ग | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 सदस्य कोड | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 सदस्य संस्था | GTIN / GLN / SSCC AI |

### GTIN

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN प्रकार | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | पॅकेजिंग स्तर | AI 01 |
| `GTIN_CHECK_DIGIT` | तपासणी अंक | AI 01, 02 |

### SSCC

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | विस्तार अंक | AI 00 |
| `SSCC_SERIAL_REFERENCE` | अनुक्रम संदर्भ | AI 00 |
| `SSCC_CHECK_DIGIT` | तपासणी अंक | AI 00 |

### देश (ISO 3166)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | देश कोड (संख्यात्मक) | एकल-देश AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | देश कोड (अल्फा-2) | अल्फा-२ देश AI |
| `COUNTRY_NAME` | देशाचे नाव | एकल-देश AI |
| `COUNTRY_LIST` | देश | AI 423 — सर्व नावे जोडून, उदा. `Australia, New Zealand` |

AI 423 (प्रारंभिक प्रक्रियेचा देश) पाच देशांपर्यंत वाहू शकतो, म्हणून तो **प्रत्येक देशासाठी
एक क्रमांकित जोडी** बाहेर टाकतो — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — आणि त्यानंतर एकच `COUNTRY_LIST` सारांश.
या किल्ल्या `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` स्थिरांकांपासून १ पासून
सुरू होणाऱ्या क्रमांकासह तयार करा, किंवा सरळ `getInterpretations()` वर फिरा; प्रत्यय
नसलेल्या `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` किल्ल्या AI 423 साठी बाहेर **टाकल्या जात
नाहीत**.

### चलन (ISO 4217)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | चलन कोड | चलनासह रक्कम AI (391n, 393n) |
| `CURRENCY_ALPHA` | चलन अक्षर कोड | चलनासह रक्कम AI |
| `CURRENCY_NAME` | चलनाचे नाव | चलनासह रक्कम AI |

### तापमान

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `TEMPERATURE` | तापमान | AI 4330–4333 |
| `TEMPERATURE_UNIT` | तापमान एकक | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | तापमान (स्वरूपित) | AI 4330–4333 |

### लिंग (ISO 5218)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `SEX_CODE` | लिंग कोड | AI 7252 |
| `SEX_DESCRIPTION` | लिंग वर्णन | AI 7252 |

### जलचर प्रजाती (FAO)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `SPECIES_CODE` | प्रजाती कोड | AI 7008 |
| `SPECIES_SCIENTIFIC` | शास्त्रीय नाव | AI 7008 |
| `SPECIES_ENGLISH` | सामान्य नाव | AI 7008 |
| `SPECIES_FAMILY` | कुळ | AI 7008 |
| `SPECIES_ORDER` | गण | AI 7008 |

### नाटो साठा क्रमांक (NSN)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `NSN_FSG` | पुरवठा गट | AI 7001 |
| `NSN_FSG_NAME` | पुरवठा गटाचे नाव | AI 7001 |
| `NSN_FSCG` | पुरवठा वर्ग | AI 7001 |
| `NSN_FSCG_NAME` | पुरवठा वर्गाचे नाव | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | देश कोड | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | देश | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO देश कोड | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS श्रेणी | AI 7001 |
| `NSN_NIIN` | राष्ट्रीय वस्तू क्रमांक | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### रोल उत्पादने

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | रोल रुंदी (mm) | AI 8001 |
| `ROLL_LENGTH` | रोल लांबी (m) | AI 8001 |
| `CORE_DIAMETER` | गाभा व्यास (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | गुंडाळण्याची दिशा कोड | AI 8001 |
| `WINDING_DIRECTION` | गुंडाळण्याची दिशा | AI 8001 |
| `SPLICES` | जोड | AI 8001 |

### IBAN

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | देश कोड | AI 8007 |
| `IBAN_COUNTRY_NAME` | देश | AI 8007 |
| `IBAN_CHECK_DIGITS` | तपासणी अंक | AI 8007 |
| `IBAN_CHECK_VALID` | तपासणी | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | अनुक्रमांक | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | तपासणी अंक | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | जारी करणारी संस्था | AI 8040, 8041 |

ते पंधरा अंक `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]` असे विभागले जातात, आणि
RBI म्हणजे TAC चे पहिले दोन अंक — म्हणजे `IMEI_RBI` हे `IMEI_TAC` चे उपसर्ग आहे, वेगळे क्षेत्र
नव्हे. `IMEI_FORMATTED` हे GSMA चे प्रमाण प्रदर्शन-गटन `AA-BBBBBB-CCCCCC-D` दाखवते (उदा.
`49-015420-323751-8`), जे TAC ला RBI च्या सीमेवर तोडते; जुने `6-2-6-1` गटन, जे आता रद्द
झालेला अंतिम-जुळणी कोड जिथे सुरू होत असे तिथे तोडत असे, ते बाहेर टाकले जात नाही.

`IMEI_RBI_NAME` हे `ImeiRbiData` द्वारे RBI चे वाटप करणाऱ्या संस्थेच्या नावात सोडवते, आणि ते
**सर्वात शेवटी आणि तो कोड तिथे नोंदलेला असेल तरच** जोडले जाते. तो तक्ता तीन गट व्यापतो:

- **अजूनही वाटप करणाऱ्या संस्था** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, आणि
  त्याशिवाय `99` Global Hexadecimal Administrator व `98` (राखीव).
- **चाचणी श्रेण्या** — `00` आणि `02`–`09`, जे खऱ्या वाटपाऐवजी चाचणी IMEI दर्शवतात.
  त्यांच्याबद्दल `ImeiRbiData.isTestCode(code)` ने विचारा.
- **आता वाटप न करणाऱ्या संस्था** — `49` (BZT/BAPT, जर्मनी), `44` (BABT, युनायटेड किंग्डम)
  आणि `91` (MSAI, भारत) यांसारख्या ऐतिहासिक संस्था. त्यांच्याबद्दल
  `ImeiRbiData.isNoLongerAllocating(code)` ने विचारा. हे कोड वाहणारी उपकरणे सामान्य आहेत आणि
  अजूनही वापरात आहेत; फक्त नवीन वाटप थांबले आहे, त्यामुळे ही नोंदवण्याजोगी माहिती आहे,
  वैधतेचा संकेत मुळीच नाही.

`IMEI_RBI_NAME` नसणे म्हणजे "हा RBI आमच्या तक्त्यात नाही", **हे नव्हे** की "IMEI अवैध आहे":
तो तक्ता थेट GSMA कडून नव्हे तर प्रकाशित RBI यादीतून संकलित केलेला आहे, त्यामुळे तो नव्याने
नेमलेल्या संस्थांच्या मागे राहू शकतो. त्याच्या अनुपस्थितीवरून कोणताही पडताळणी-निष्कर्ष काढू
नका; RBI हे तपासणी अक्षर नाही. विवेचनांच्या यादीवर फिरणाऱ्या कोडने स्थानानुसार निर्देशांक
लावण्याऐवजी त्याची अनुपस्थिती सहन करता आली पाहिजे.

### SIM ओळखचिन्हे (EID / ICCID)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | उद्योग श्रेणी | AI 8042 |
| `EID_BODY` | EID मुख्य भाग | AI 8042 |
| `EID_CHECK_DIGIT` | तपासणी अंक | AI 8042 |
| `ICCID_BODY` | ICCID मुख्य भाग | AI 8043 |
| `ICCID_EXTENSION` | विस्तार | AI 8043 |

`SIM_MII` पहिले **दोन** अंक (`89`) वाहते; हीच ती जोडी जी ITU-T E.118 दूरसंचारासाठी नेमते.
मात्र ISO/IEC 7812 स्वतः MII ची व्याख्या **केवळ पहिला अंक** अशी करते, म्हणून
`SIM_MII_NAME` हा वर्ग सुरुवातीच्या `8` अंकावरून `Iso7812Data` द्वारे सोडवतो — आणि
"Healthcare, telecommunications and other future industry assignments" असे मिळते. त्यामुळे
प्रत्येक सुव्यवस्थित EID साठी ते तेच राहते; ते मानकापर्यंतच्या मागोव्यासाठी नोंदवले जाते,
भेद दाखवणारे चिन्ह म्हणून नव्हे. `Iso7812Data.nameForCode(digit)` एकच अंक घेते, तर
`nameForIdentifier(prefix)` अधिक लांब उपसर्ग स्वीकारते आणि त्याचा पहिला अंक वाचते.

`SIM_MII_NAME` केवळ `EidEnricher` (AI 8042) बाहेर टाकतो. `IccidEnricher` (AI 8043)
`SIM_MII` दाखवतो, पण वर्ग नाही.

### प्रमाणन संदर्भ

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | अनुक्रमांक | AI 7230–7239 |
| `CERT_SCHEME_CODE` | प्रमाणन योजना कोड | AI 7230–7239 |
| `CERT_SCHEME_NAME` | प्रमाणन योजना | AI 7230–7239 |
| `CERT_REFERENCE` | प्रमाणन संदर्भ | AI 7230–7239 |

### GS1 UIC

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC कोड | AI 7040 |
| `UIC_EXTENSION_1` | विस्तार 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | आयातदार निर्देशांक | AI 7040 |

### अर्भकाचा जन्मक्रम

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | जन्म स्थान | AI 7258 |
| `BIRTH_TOTAL` | एकूण जन्म | AI 7258 |
| `BIRTH_SEQUENCE` | जन्म क्रम | AI 7258 |

### जागतिक मॉडेल क्रमांक (GMN)

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | मॉडेल संदर्भ | AI 8013 |
| `GMN_CHECK_PAIR` | तपासणी जोडी | AI 8013 |

### HIDRI

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | उपकरण संदर्भ | AI 8014 |
| `HIDRI_CHECK_PAIR` | तपासणी जोडी | AI 8014 |

### CPID

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | घटक व भाग संदर्भ | AI 8010–8011 |

### दशांश आणि मापन मूल्ये

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | दशांश मूल्य | अध्याहृत दशांश स्थाने असलेले संख्यात्मक AI (31xx–36xx) |
| `DECIMAL_AMOUNT` | रक्कम | किंमत AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | टक्केवारी | AI 394n |
| `DECIMAL_PLACES` | दशांश स्थान | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` सोबत |
| `PERCENTAGE_FORMAT` | टक्केवारी स्वरूप | AI 394n |
| `ISO_UNIT_CODE` | ISO एकक कोड | मापन AI |
| `ISO_UNIT_NAME` | ISO एकक नाव | मापन AI |
| `MONETARY_AMOUNT` | आर्थिक रक्कम | किंमत AI |
| `MONETARY_AMOUNT_DISPLAY` | आर्थिक रक्कम (स्वरूपित) | किंमत AI |

### भौगोलिक निर्देशांक

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `LATITUDE` | अक्षांश | AI 4309 |
| `LONGITUDE` | रेखांश | AI 4309 |
| `GEO_COORDINATES` | भौगोलिक निर्देशांक | AI 4309 |
| `LATITUDE_DMS` | अक्षांश (DMS) | AI 4309 |
| `LONGITUDE_DMS` | रेखांश (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | भौगोलिक निर्देशांक (DMS) | AI 4309 |

### उत्पादन पद्धत

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | उत्पादन पद्धत कोड | AI 7010 |
| `PRODUCTION_METHOD` | उत्पादन पद्धत | AI 7010 |

### AIDC माध्यम प्रकार

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC माध्यम प्रकार कोड | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC माध्यम प्रकार | AI 7241 |

### एकूणातील तुकडा

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | नग क्रमांक | AI 8006 |
| `PIECE_TOTAL` | एकूण नग | AI 8006 |
| `PIECE_OF_TOTAL` | एकूणातील नग | AI 8006 |

### घटक विभाजने

या किल्ल्या Java मध्ये लिहिलेल्या समृद्धकाकडून नव्हे, तर `content/ai-content.json` मधील
घोषणात्मक घटक-विभाजनांकडून येतात — त्या सर्व मिश्र AI मूल्याचे नामांकित भाग दाखवतात. या
परिशिष्टातील इतर प्रत्येक किल्लीच्या उलट, **यांच्यासाठी `GS1Constants_Enricher` मध्ये स्थिरांक
नाही**: स्ट्रिंग लिटरलवर जुळवा, किंवा प्रकार `GS1AIInterpretation.getType()` मधून वाचा.

| प्रकार किल्ली | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | तपासणी अंक | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | अनुक्रमांक | AI 253, 255, 8003 |
| `POSTAL_CODE` | टपाल कोड | AI 421 |
| `PROCESSOR_ID` | प्रक्रियाकर्ता ओळखकर्ता | AI 7030–7039 |

लक्षात घ्या की इथला `CHECK_DIGIT` ही सर्वसाधारण घटक-विभाजनाची किल्ली आहे, आणि ती वर दिलेल्या
समृद्धक-विशिष्ट `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` व
`EID_CHECK_DIGIT` या किल्ल्यांपेक्षा वेगळी आहे.

### संकीर्ण

| किल्ली स्थिरांक | प्रदर्शन-लेबल | कोणी तयार केले |
|--------------|---------------|-------------|
| `FLAG_VALUE` | मूल्य | बूलियन / ध्वज AI (4321–4323) |
| `DECODED_TEXT` | डीकोड केलेला मजकूर | मुक्त-मजकूर AI |
