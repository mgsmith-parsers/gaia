# GAIA (GS1 Application Identifiers Analyser) — डेवलपर गाइड

## विषय-सूची

1. [संक्षिप्त परिचय](#सकषपत-परचय)
2. [GS1 और General Specifications के बारे में](#gs1-और-general-specifications-क-बर-म)
3. [GS1 एप्लिकेशन आइडेंटिफ़ायर](#gs1-एपलकशन-आइडटफयर)
4. [क्विक स्टार्ट](#कवक-सटरट)
5. [पार्सिंग पाइपलाइन](#परसग-पइपलइन)
   - [पूर्व-चरण — इनपुट मॉडिफ़ायर](#परव-चरण--इनपट-मडफयर)
   - [चरण 0 — सहसंबंध ID](#चरण-0--सहसबध-id)
   - [चरण 1 — इनपुट मार्गनिर्धारण](#चरण-1--इनपट-मरगनरधरण)
   - [चरण 2 — वाक्य-रचना](#चरण-2--वकय-रचन)
   - [चरण 3 — सामग्री](#चरण-3--समगर)
   - [चरण 4 — व्याख्या](#चरण-4--वयखय)
6. [पार्स विन्यास (`ParseConfig`)](#परस-वनयस-parseconfig)
   - [विकल्प](#वकलप)
   - [स्थानीयकृत संदेश और लेबल](#सथनयकत-सदश-और-लबल)
   - [तिथि स्वरूपण](#तथ-सवरपण)
7. [इनपुट मॉडिफ़ायर](#इनपट-मडफयर)
   - [अंतर्निहित मॉडिफ़ायर](#अतरनहत-मडफयर)
   - [मॉडिफ़ायर लिखना](#मडफयर-लखन)
   - [मॉडिफ़ायर पंजीकृत करना](#मडफयर-पजकत-करन)
   - [मॉडिफ़ायर ने क्या किया, यह देखना](#मडफयर-न-कय-कय-यह-दखन)
   - [मॉडिफ़ायर की विफलता का प्रबंधन](#मडफयर-क-वफलत-क-परबधन)
8. [पार्स मोड](#परस-मड)
   - [DATA_CARRIER मोड](#data_carrier-मड)
   - [SYNTAX मोड](#syntax-मड)
   - [CONTENT मोड](#content-मड)
   - [INTERPRETATION मोड (डिफ़ॉल्ट)](#interpretation-मड-डफलट)
9. [सहसंबंध ID](#सहसबध-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [परिणामों के साथ काम करना](#परणम-क-सथ-कम-करन)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry और DataCarrierType](#datacarrierentry-और-datacarriertype)
12. [त्रुटि संदर्भ](#तरट-सदरभ)
13. [थ्रेड सुरक्षा](#थरड-सरकष)
14. [परिशिष्ट अ — AI स्ट्रिंग स्थिरांक](#परशषट-अ--ai-सटरग-सथरक)
    - [पहचान और क्रम-संख्यांकन](#पहचन-और-करम-सखयकन)
    - [तिथि और समय](#तथ-और-समय)
    - [मात्रा और माप — परिवर्तनीय माप (मीट्रिक)](#मतर-और-मप--परवरतनय-मप-मटरक)
    - [मात्रा और माप — परिवर्तनीय माप (इंपीरियल / अमेरिकी)](#मतर-और-मप--परवरतनय-मप-इपरयल--अमरक)
    - [मूल्य और मौद्रिक राशियाँ](#मलय-और-मदरक-रशय)
    - [स्थान और शिपिंग](#सथन-और-शपग)
    - [उत्पाद विशेषताएँ और अनुरेखणीयता](#उतपद-वशषतए-और-अनरखणयत)
    - [राष्ट्रीय स्वास्थ्य प्रतिपूर्ति संख्याएँ (NHRN)](#रषटरय-सवसथय-परतपरत-सखयए-nhrn)
    - [स्वास्थ्य-सेवा, GMN, HIDRI, CPID और व्यक्ति-संबंधी डेटा](#सवसथय-सव-gmn-hidri-cpid-और-वयकत-सबध-डट)
    - [आंतरिक / कंपनी उपयोग](#आतरक--कपन-उपयग)
15. [परिशिष्ट ब — व्याख्या कुंजी स्थिरांक](#परशषट-ब--वयखय-कज-सथरक)
    - [तिथि और समय](#तथ-और-समय)
    - [कटाई तिथि](#कटई-तथ)
    - [GS1 कंपनी उपसर्ग](#gs1-कपन-उपसरग)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [देश (ISO 3166)](#दश-iso-3166)
    - [मुद्रा (ISO 4217)](#मदर-iso-4217)
    - [तापमान](#तपमन)
    - [लिंग (ISO 5218)](#लग-iso-5218)
    - [जलीय प्रजातियाँ (FAO)](#जलय-परजतय-fao)
    - [NATO स्टॉक संख्या (NSN)](#nato-सटक-सखय-nsn)
    - [रोल उत्पाद](#रल-उतपद)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM पहचानकर्ता (EID / ICCID)](#sim-पहचनकरत-eid--iccid)
    - [प्रमाणन संदर्भ](#परमणन-सदरभ)
    - [GS1 UIC](#gs1-uic)
    - [शिशु जन्म-क्रम](#शश-जनम-करम)
    - [ग्लोबल मॉडल नंबर (GMN)](#गलबल-मडल-नबर-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [दशमलव और माप मान](#दशमलव-और-मप-मन)
    - [भौगोलिक निर्देशांक](#भगलक-नरदशक)
    - [उत्पादन विधि](#उतपदन-वध)
    - [AIC माध्यम प्रकार](#aic-मधयम-परकर)
    - [कुल में से टुकड़ा](#कल-म-स-टकड)
    - [घटक विभाजन](#घटक-वभजन)
    - [विविध](#ववध)

---

## संक्षिप्त परिचय

`GaiaParser` GS1 एप्लिकेशन आइडेंटिफ़ायर (AI) एलिमेंट स्ट्रिंग पार्स करने का प्रवेश-बिंदु है। यह स्कैनर के कच्चे आउटपुट को नीचे दिए किसी भी रूप में स्वीकार करता है और एक संरचित `ParseResult` लौटाता है, जिसमें सभी हल किए गए AI, सत्यापन त्रुटियाँ, और वैकल्पिक रूप से मनुष्य के पढ़ने योग्य व्याख्याएँ होती हैं:

- सादी AI एलिमेंट स्ट्रिंग: `0109506000134352`
- AIM सिम्बोलॉजी आइडेंटिफ़ायर से उपसर्गित एलिमेंट स्ट्रिंग: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- इनमें से कोई भी, वैकल्पिक रूप से 8-अंकीय सहसंबंध ID से उपसर्गित: `12345678~0109506000134352`

**प्रवेश-बिंदु क्लास:** `tools.pantheum.gaia.GaiaParser`

> **Gaia में नए हैं?** **[GaiaParser क्विक स्टार्ट](GaiaParser-QuickStart-Hindi.md)** से शुरू करें — दस मिनट में डिपेंडेंसी, पहली पार्सिंग, और सबसे आम अड़चनें। यह गाइड पूरा संदर्भ-दस्तावेज़ है।

> इसकी उलटी दिशा — AI/मान युग्मों से वैध एलिमेंट स्ट्रिंग और Digital Link URI *बनाना* — **[GaiaBuilder — डेवलपर गाइड](GaiaBuilder-Hindi.md)** में शामिल है।

---

## GS1 और General Specifications के बारे में

**GS1** एक वैश्विक गैर-लाभकारी संस्था है जो आपूर्ति-शृंखला की पहचान और डेटा-विनिमय के लिए खुले मानक विकसित करती और उनका रखरखाव करती है। इसके मानक खुदरा, स्वास्थ्य-सेवा, लॉजिस्टिक्स, खाद्य-सेवा और कई अन्य उद्योगों में प्रयुक्त होते हैं, और उपभोक्ता पैकेजिंग पर लगे उत्पाद बारकोड से लेकर दवा की खुराकों की क्रम-संख्या आधारित ट्रैकिंग तक सब कुछ समेटते हैं।

यह पार्सर जो कुछ भी लागू करता है, उसका प्रामाणिक संदर्भ **GS1 General Specifications** है — एक ही दस्तावेज़ जो निम्न को परिभाषित करता है:

- सभी एप्लिकेशन आइडेंटिफ़ायर (AI) कोड, उनके डेटा शीर्षक, प्रारूप और सत्यापन नियम
- AI एलिमेंट स्ट्रिंग बनाने और एन्कोड करने के वाक्य-रचना नियम
- बारकोड सिम्बोलॉजी की आवश्यकताएँ और AIM कोड ID के आवंटन
- जाँच अंक और जाँच वर्ण के एल्गोरिदम
- दो-अंकीय वर्ष का निर्धारण (खिसकती-खिड़की नियम)
- Data Matrix, QR Code, GS1-128, GS1 DataBar और अन्य कैरियर विनिर्देश

GS1 General Specifications हर साल अद्यतन होते हैं। वर्तमान संस्करण और सहायक सामग्री यहाँ उपलब्ध है:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications का **रिलीज़ 26.0 (अनुमोदित, जन 2026)** लागू करता है।

GS1 Digital Link URI एक सहयोगी मानक, **GS1 Digital Link: URI Syntax**, द्वारा शासित हैं, जो प्राथमिक पहचान कुंजियाँ, कुंजी-योग्यकों का क्रम, और डेटा-विशेषताओं की एन्कोडिंग परिभाषित करता है — पार्सर इन्हीं को Digital Link इनपुट पर लागू करता है:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax मानक का **रिलीज़ 1.7.0 (अनुमोदित, अग 2026)** लागू करता है।

इस दस्तावेज़ में अनुभागों के संदर्भ GS1 General Specifications की ओर संकेत करते हैं (जैसे "Table 7-5", "section 7.12"), केवल Digital Link के अनुभाग-क्रमांक (जैसे "§4.9", "§4.12") को छोड़कर, जो GS1 Digital Link: URI Syntax मानक की ओर संकेत करते हैं।

---

## GS1 एप्लिकेशन आइडेंटिफ़ायर

**GS1 एप्लिकेशन आइडेंटिफ़ायर (AI)** एक छोटा संख्यात्मक उपसर्ग है — दो से चार अंक — जो अपने ठीक बाद आने वाले डेटा का अर्थ और प्रारूप निर्धारित करता है। AI, GS1 General Specifications में परिभाषित हैं और आपूर्ति-शृंखला के डेटा की विस्तृत श्रेणी समेटते हैं: उत्पाद पहचानकर्ता, तिथियाँ, मात्राएँ, लॉट संख्याएँ, क्रम संख्याएँ, माप, URL, और भी बहुत कुछ।

### AI एलिमेंट की संरचना

हर AI एलिमेंट दो हिस्सों से बनता है:

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

AI कोड सदैव संख्यात्मक होता है। डेटा मान उसके ठीक बाद आता है, कोड और मान के बीच कोई सीमांकक नहीं होता।

### नियत-लंबाई बनाम परिवर्तनीय-लंबाई AI

AI दो श्रेणियों में बँटते हैं:

| प्रकार | व्यवहार | उदाहरण |
|---|---|---|
| **नियत-लंबाई** | वर्णों की ठीक-ठीक संख्या, सदैव पूरी पढ़ी जाती है | AI `01` (GTIN) — सदैव 14 अंक |
| **परिवर्तनीय-लंबाई** | 1 से अधिकतम संख्या तक; GS विभाजक या इनपुट के अंत पर समाप्त | AI `10` (बैच/लॉट) — 1 से 20 अल्फ़ान्यूमेरिक वर्ण |

कोई AI नियत है या परिवर्तनीय, यह केवल GS1 विनिर्देश में दी उसकी परिभाषा से तय होता है — पार्सर कभी अनुमान नहीं लगाता।

### बहु-AI एलिमेंट स्ट्रिंग

कई AI को एक ही एलिमेंट स्ट्रिंग में जोड़ा जा सकता है। नियत-लंबाई वाले AI सीधे जोड़े जा सकते हैं, क्योंकि पार्सर सदैव ठीक-ठीक जानता है कि कितने वर्ण पढ़ने हैं। परिवर्तनीय-लंबाई वाले AI को, जब भी उनके बाद कोई और AI आता हो, **GS वर्ण** (ASCII `0x1D`, बारकोड सिम्बोलॉजी में इसे FNC1 भी कहते हैं) से समाप्त करना अनिवार्य है, ताकि पार्सर जान सके कि एक मान कहाँ ख़त्म होता है और अगला AI कोड कहाँ शुरू।

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

Java स्ट्रिंग लिटरल में GS वर्ण को यूनिकोड एस्केप `""` से लिखें।

### प्रचलित AI

| AI | डेटा शीर्षक | प्रारूप | उदाहरण मान |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, एकल मुद्रा-क्षेत्र) |
| `710` | NHRN PZN | X..20 | `12345678` |

> 4-अंकीय माप या मूल्य AI का **चौथा अंक** निहित दशमलव स्थानों की संख्या एन्कोड करता है — `3103` का अर्थ है 3 दशमलव के साथ किलोग्राम में शुद्ध वज़न (`001500` = 1.500 kg), जबकि `3102` उन्हीं अंकों को 15.00 kg पढ़ेगा। ऊपर का `प्रारूप` स्तंभ *डेटा* का प्रारूप दिखाता है; प्रत्येक AI का पूरा `getFormatString()` स्वयं AI को भी सम्मिलित करता है (जैसे `3103` के लिए `N4+N6`)।

### मनुष्य के पढ़ने योग्य व्याख्या (HRI)

पारंपरिक पठनीय रूप में प्रत्येक AI कोड को उसके मान से ठीक पहले कोष्ठकों में लपेटा जाता है, और एलिमेंटों के बीच एक रिक्ति दी जाती है:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS विभाजक HRI में नहीं दिखता। यह प्रारूप `GS1AIObject.toHriString()` उत्पन्न करता है।

### चार-अंकीय AI कोड

कुछ AI दो के बजाय चार अंक प्रयोग करते हैं। पहले दो अंक AI कुल की पहचान करते हैं; तीसरा और/या चौथा अंक अतिरिक्त अर्थ रखता है (जैसे माप AI में निहित दशमलव बिंदु की स्थिति)। पार्सर एलिमेंट स्ट्रिंग से पूरा AI कोड स्वयं निकाल लेता है — कॉल करने वाला सदैव पूरे कोड के साथ काम करता है (जैसे `"3102"`, केवल `"31"` नहीं)।

---

## क्विक स्टार्ट

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

> **GS विभाजक:** बहु-AI स्ट्रिंग के भीतर परिवर्तनीय-लंबाई वाले AI को GS वर्ण (ASCII `0x1D`) से सीमांकित करना अनिवार्य है। Java स्ट्रिंग लिटरल में `""` का प्रयोग करें।

---

## पार्सिंग पाइपलाइन

### पूर्व-चरण — इनपुट मॉडिफ़ायर

यदि `ParseConfig` में कोई **इनपुट मॉडिफ़ायर** हों, तो वे सबसे पहले चलते हैं — सहसंबंध उपसर्ग हटाने से पहले, कैरियर पहचान से पहले, GS1 पाइपलाइन में प्रवेश से पहले। हर मॉडिफ़ायर अगले के लिए कच्चे इनपुट को फिर से लिखता है, और नीचे दिए सभी चरण इसी शृंखला के आउटपुट पर काम करते हैं।

डिफ़ॉल्ट रूप से कोई मॉडिफ़ायर विन्यस्त नहीं होता, इसलिए जब तक आप स्वयं न चुनें, यह पूर्व-चरण कुछ नहीं करता। देखें [इनपुट मॉडिफ़ायर](#इनपट-मडफयर)।

---

### चरण 0 — सहसंबंध ID

किसी भी GS1 प्रसंस्करण से पहले `GaiaParser` जाँचता है कि इनपुट किसी वैकल्पिक **सहसंबंध ID उपसर्ग** से शुरू होता है या नहीं: ठीक 8 ASCII दशमलव अंक और उनके बाद एक टिल्ड (`~`), जैसे `12345678~`।

यदि उपसर्ग मौजूद है तो उसे हटाकर लौटाए गए `ParseResult` पर `CorrelationInfo` के रूप में संग्रहीत कर लिया जाता है। आगे के सभी चरण उपसर्ग-रहित पेलोड पर काम करते हैं। यदि कोई उपसर्ग नहीं है, तो इनपुट ज्यों-का-त्यों आगे बढ़ जाता है।

विवरण के लिए देखें [सहसंबंध ID](#सहसबध-id)।

---

### चरण 1 — इनपुट मार्गनिर्धारण

सहसंबंध उपसर्ग हटाने के बाद `GaiaParser` जाँचता है कि (उपसर्ग-रहित) इनपुट किसी **AIM कोड ID** से शुरू होता है या नहीं: तीन-वर्णीय उपसर्ग जिसका रूप है `]` + ASCII अक्षर + ASCII अंक (जैसे GS1-128 के लिए `]C1`, GS1 DataMatrix के लिए `]d2`, GS1 DataBar / GS1 Composite के लिए `]e0`)।

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

यदि कैरियर GS1 AI धारण करने में सक्षम नहीं है (जैसे कोई डाक बारकोड), तो पार्सिंग तुरंत `GE-D002` त्रुटि के साथ रुक जाती है।

---

### चरण 2 — वाक्य-रचना

यह सदैव चलता है। इसके दो उप-चरण हैं:

**2क. टोकनीकरण (`AISyntaxParser`)**
- GS1 उपसर्ग तालिका (GS1 General Specifications Table 7-5) की सहायता से पहले दो वर्णों से AI कोड की लंबाई पढ़ता है।
- नियत-लंबाई वाले AI इनपुट से बाइटों की ठीक-ठीक संख्या पढ़ते हैं।
- परिवर्तनीय-लंबाई वाले AI GS वर्ण या इनपुट के अंत तक पढ़े जाते हैं।
- बहु-घटक AI के मान-खंड को प्रति-घटक टुकड़ों में काटा जाता है।

**2ख. संरचनात्मक सत्यापन (`SyntaxValidator`)**
- दोहराए गए AI की जाँच (`GE-S004`)।
- अनिवार्य AI निर्भरताओं की जाँच, जैसे AI `02` के लिए AI `37` आवश्यक है (`GE-S005`)।
- वर्जित AI युग्मों की जाँच (`GE-S006`)।

इस चरण की त्रुटियाँ `SYNTAX_ERROR` (टोकनाइज़र) या `INTEGRITY_ERROR` (संरचनात्मक) स्तर की होती हैं। यदि **कोई भी** त्रुटि मौजूद हो — टोकनाइज़र की या संरचनात्मक — तो पाइपलाइन रुक जाती है और सामग्री तथा व्याख्या के चरण छोड़ दिए जाते हैं।

---

### चरण 3 — सामग्री

यह तभी चलता है जब चरण 2 ने कोई त्रुटि उत्पन्न न की हो (न टोकनाइज़र की, न संरचनात्मक)। प्रति-एलिमेंट पाइपलाइन (हर पग तभी चलता है जब पिछले पग में कोई त्रुटि न आई हो):

| पग | सत्यापक | त्रुटि कोड |
|---|---|---|
| रेगेक्स जाँच | `RegexValidator` | `GE-C001` |
| घटक वर्ण-समुच्चय + प्रारूप | `ComponentValidator` | `GE-C005` + प्रति-शर्त प्रारूप कोड (`GE-C054`–`GE-C115`) |
| जाँच अंक / जाँच वर्ण | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| कस्टम शब्दार्थ सत्यापन | `ContentValidatorRegistry` | प्रति-शर्त सामग्री कोड (`GE-C116`–`GE-C170`) |

इस चरण की त्रुटियाँ `FORMAT_ERROR` या `DATA_ERROR` स्तर की होती हैं, एक अपवाद के साथ: GS1-कुंजी
वाले AI पर GS1 कंपनी उपसर्ग की जाँचें सलाहकारी हैं और `WARNING` स्तर रखती हैं (देखें
[त्रुटि संदर्भ](#तरट-सदरभ)), इसलिए कोई अपरिचित कंपनी उपसर्ग अपने-आप परिणाम को
अवैध नहीं बनाता।

---

### चरण 4 — व्याख्या

यह केवल `INTERPRETATION` मोड में चलता है और तभी जब किसी भी एलिमेंट पर पिछले किसी चरण की त्रुटि न हो। `InterpretationEngine` हर एलिमेंट को लेबल-युक्त मेटाडेटा से समृद्ध करता है:

- `dd/mm/yyyy` में पुनः स्वरूपित तिथियाँ
- GTIN जाँच अंक का विश्लेषण और GS1 कंपनी उपसर्ग की खोज
- ISO 3166 देश-नाम
- ISO 4217 मुद्रा-नाम और मुद्रा-चिह्न
- डिकोड की गई दशमलव राशियाँ
- HRI (मनुष्य के पढ़ने योग्य व्याख्या) के अंश

परिणाम हर `GS1AIObjectElement` पर `GS1AIInterpretation` प्रविष्टियों के रूप में जोड़े जाते हैं।

---

## पार्स विन्यास (`ParseConfig`)

`GaiaParser` ठीक दो प्रवेश-बिंदु उपलब्ध कराता है:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` **डिफ़ॉल्ट विन्यास** के साथ चलता है: `INTERPRETATION` मोड, `/` विभाजक और चार-अंकीय वर्ष के साथ लिटिल-एंडियन तिथियाँ (`dd/mm/yyyy`), और **अंग्रेज़ी** त्रुटि-संदेश। इनमें से किसी को भी बदलने के लिए — पार्स मोड सहित — प्रवाही बिल्डर से `ParseConfig` बनाइए और दो-तर्क वाला रूप प्रयोग कीजिए।

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

विकल्पों के सभी enum `GaiaConstants` में हैं।

### विकल्प

| बिल्डर विधि | Enum (`GaiaConstants`) | डिफ़ॉल्ट | प्रभाव |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | पाइपलाइन की गहराई — देखें [पार्स मोड](#परस-मड)। |
| `language(...)`      | `Language`      | `ENGLISH`        | त्रुटि-संदेशों, व्याख्या-लेबलों **और** AI विवरणों की भाषा। |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | तिथि-घटकों का क्रम: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`)। |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | तिथि-घटकों के बीच का वर्ण: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`)। |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) या `THREE_LETTER` (`DEC`)। |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) या `TWO_DIGIT` (`26`)। |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | संरचनात्मक "आवश्यक है" जाँच (`GE-S005`) छोड़ देता है। |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | संरचनात्मक "वर्जित है" जाँच (`GE-S006`) छोड़ देता है। |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / क्लास नाम | कोई नहीं | वह कोड जो पार्सिंग से पहले कच्चे इनपुट को फिर से लिखता है — दो [अंतर्निहित मॉडिफ़ायर](#अतरनहत-मडफयर) तथा आपके लिखे कोई भी। देखें [इनपुट मॉडिफ़ायर](#इनपट-मडफयर)। |

तिथि से जुड़े चारों विकल्प केवल व्याख्या-समृद्धकों द्वारा बनाई गई स्वरूपित तिथि-स्ट्रिंगों को प्रभावित करते हैं (`INTERPRETATION` मोड में); ये सत्यापन को नहीं बदलते। बिल्डर के मान छोड़े जा सकते हैं — जो विकल्प निर्धारित न किया जाए (या जिसे `null` दिया जाए) वह अपना डिफ़ॉल्ट बनाए रखता है।

### स्थानीयकृत संदेश और लेबल

`language(...)` मनुष्य के पढ़ने योग्य **तीन** प्रकार के पाठ की भाषा चुनता है: त्रुटि-संदेश, व्याख्या-लेबल (हर `GS1AIInterpretation` का `getLabel()`), और AI विवरण (हर `GS1AIObjectElement` का `getDescription()`)।

`GaiaConstants.Language` में **35 भाषाएँ** परिभाषित हैं, जो विश्व की सर्वाधिक बोली जाने वाली भाषाओं को समेटती हैं: अंग्रेज़ी, फ़्रांसीसी, स्पेनी, जर्मन, इतालवी, पुर्तगाली, डच, पोलिश, रूसी, यूक्रेनी, चेक, स्वीडिश, चीनी, जापानी, कोरियाई, अरबी, इंडोनेशियाई, हिन्दी, तुर्की, बांग्ला, उर्दू, वियतनामी, नाइजीरियाई पिजिन, मिस्री अरबी, मराठी, तेलुगु, तमिल, कैंटोनी, वू चीनी, तागालोग, फ़ारसी, हौसा, पंजाबी, जावानी और स्वाहिली।

अनुवाद की स्थिति (जैसी वितरित है):
- **व्याख्या-लेबल** — सभी भाषाओं के लिए अनूदित।
- **त्रुटि-संदेश** — सभी भाषाओं के लिए अनूदित।
- **AI विवरण** — अंग्रेज़ी को छोड़ सभी भाषाओं के लिए अनूदित। अंग्रेज़ी की कोई अलग सूची नहीं है: वह सीधे `gs1-application-identifiers.jsonld` में उस AI की प्रविष्टि के `description` क्षेत्र से पढ़ी जाती है, और अंततः हर AI विवरण इसी पर लौटता है।

नाइजीरियाई पिजिन (`NIGERIAN_PIDGIN`), जो अंग्रेज़ी-आधारित क्रिओल है, व्याख्या-लेबलों और त्रुटि-संदेशों के लिए जान-बूझकर अंग्रेज़ी पाठ ही दोहराती है। AI विवरण इस अपवाद का अपवाद हैं: वे अंग्रेज़ी दोहराने के बजाय असली पिजिन में अनूदित हैं, क्योंकि AI-विवरण सूचियाँ लेबल/संदेश सूचियों से स्वतंत्र रूप से बनाई गई थीं। मशीनी अनुवादों पर उत्पादन में भरोसा करने से पहले मातृभाषियों से उनकी समीक्षा करा लेनी चाहिए।

किसी भाषा की सूची में जो संदेश, लेबल या विवरण अनुपस्थित हो, वह अंग्रेज़ी पर लौट जाता है। दाएँ-से-बाएँ लिखी जाने वाली भाषाएँ (अरबी, उर्दू, मिस्री अरबी, फ़ारसी) स्ट्रिंग के रूप में सही ढंग से संग्रहीत हैं; उन्हें दाएँ-से-बाएँ प्रस्तुत करना प्रदर्शन-परत का दायित्व है।

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

व्याख्या-लेबल भी इसी तरह स्थानीयकृत होते हैं (मान अपरिवर्तित रहते हैं — केवल लेबल बदलते हैं):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI विवरण भी इसी तरह स्थानीयकृत होते हैं (केवल `getTitle()`, जैसे `"GTIN"`, स्थानीयकृत नहीं होता):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### तिथि स्वरूपण

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## इनपुट मॉडिफ़ायर

**इनपुट मॉडिफ़ायर** वह कोड है जो Gaia के पार्स करने से पहले कच्ची इनपुट स्ट्रिंग को फिर से लिखता है। मॉडिफ़ायर उस इनपुट के लिए हैं जो पहले से ही बिगड़ा हुआ आता है — कोई स्कैनर जो GS विभाजक की जगह कोई मुद्रणीय वर्ण रख देता है, कोई मिडलवेयर जो पेलोड को विक्रेता-विशिष्ट उपसर्ग में लपेट देता है, कोई होस्ट सिस्टम जो सब कुछ बड़े अक्षरों में बदल देता है। हर कॉल-स्थल पर हर स्ट्रिंग को पहले से संसाधित करने (और उनमें से किसी एक में सूक्ष्म चूक कर बैठने) के बजाय, सामान्यीकरण को `ParseConfig` पर एक ही बार पंजीकृत कीजिए और पार्सर को उसे लागू करने दीजिए।

मॉडिफ़ायर `GaiaParser.parse(...)` के बिलकुल आरंभ में चलते हैं — सहसंबंध ID हटाने से पहले, AIM कोड ID की पहचान से पहले, GS1 पाइपलाइन से पहले। आगे की हर चीज़ केवल पुनर्लिखित स्ट्रिंग ही देखती है। दोनों [अंतर्निहित मॉडिफ़ायर](#अतरनहत-मडफयर) सहित **डिफ़ॉल्ट रूप से कुछ भी विन्यस्त नहीं है** — आप प्रत्येक `ParseConfig` पर स्वयं चुनते हैं।

**इंटरफ़ेस:** `tools.pantheum.gaia.modifier.ModifierInterface`

### अंतर्निहित मॉडिफ़ायर

कोर jar में `tools.pantheum.gaia.modifier.custom` के भीतर दो मॉडिफ़ायर आते हैं। ये उन दो तरीक़ों को सँभालते हैं जिनसे GS1 पेलोड सबसे अधिक बार बिगड़ा हुआ पहुँचता है — मुद्रित HRI कोष्ठक जिन्हें डेटा मान लिया जाता है, और अनावश्यक रिक्तियाँ — इसलिए आम मामलों के लिए कोई कस्टम क्लास लिखने की ज़रूरत नहीं पड़ती:

| क्लास | `getName()` | यह क्या करता है |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | हर AI के चारों ओर लगे HRI कोष्ठक (`(01)…(10)…`) हटाता है और उनसे निहित FNC1 विभाजक बहाल करता है। |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI एलिमेंट स्ट्रिंग से हर रिक्ति (`0x20`) हटा देता है। |

ये दोनों सामान्य `ModifierInterface` कार्यान्वयन हैं, किसी विशेष दर्जे के बिना — इन्हें ठीक वैसे ही पंजीकृत किया जाता, क्रम दिया जाता, प्रतिवेदित किया जाता और विफल होने दिया जाता है जैसे आपके अपने लिखे मॉडिफ़ायर को:

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

दोनों अवस्था-रहित और थ्रेड-सुरक्षित हैं, इसलिए एक ही उदाहरण साझा किया जा सकता है, और दोनों को विन्यास-संचालित व्यवस्थाओं के लिए पूर्ण-योग्य क्लास नाम से संबोधित किया जा सकता है (देखें [मॉडिफ़ायर पंजीकृत करना](#मडफयर-पजकत-करन))।

#### `ModifierRemoveAIBrackets`

GS1 की मनुष्य-पठनीय व्याख्या हर AI को कोष्ठकों में छापती है — `(01)09521234543213(10)ABC123` — विशुद्ध रूप से एक मुद्रण-परंपरा के तौर पर। HRI उत्सर्जित करने के लिए विन्यस्त कोई स्कैनर या मिडलवेयर उन कोष्ठकों को डेटा की तरह आगे बढ़ा देता है, और टोकनाइज़र को पता ही नहीं होता कि उनका क्या करे।

केवल कोष्ठक हटाना आधा काम है। HRI में *अगले* AI का खुलने वाला `(` ही पिछले मान के अंत का चिह्न होता है, इसलिए कोष्ठक-रूप में परिवर्तनीय-लंबाई वाले AI को FNC1 की ज़रूरत नहीं पड़ती। कोष्ठक भोलेपन से हटा दीजिए और वह सीमा लुप्त हो जाती है:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

इसलिए यह मॉडिफ़ायर **हर उस सीमा पर FNC1 फिर से डालता है जहाँ पूर्ववर्ती AI परिवर्तनीय-लंबाई का हो**, और ठीक वही बहाल कर देता है जो कोष्ठक एन्कोड कर रहे थे:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

लंबाई पार्सर की अपनी `AiDefinitionRegistry` में खोजी जाती है, इसलिए किसी हार्ड-कोडेड सूची के बजाय हर परिवर्तनीय-लंबाई वाला AI सँभाला जाता है। तीन स्थितियाँ जान-बूझकर अछूती छोड़ दी जाती हैं: वह मान जो पहले से FNC1 पर समाप्त होता है (दोनों परंपराएँ उत्सर्जित करने वाले स्रोत को दूसरा विभाजक नहीं मिलता), वह कोष्ठकित कोड जो कोई ज्ञात AI नहीं है (अज्ञात AI अपनी लंबाई के बारे में कुछ नहीं कहता), और स्ट्रिंग का अंतिम AI।

यह पुनर्लेखन **वर्ग-सम** है — इसे इसके अपने आउटपुट पर चलाने से कुछ नहीं बदलता — इसलिए यह ऐसी मिली-जुली धारा पर भी सुरक्षित है जिसमें केवल कुछ इनपुट कोष्ठकित हों।

> **सीमा।** `(` और `)` स्वयं वैध GS1 डेटा वर्ण हैं, और यहाँ प्रयुक्त पैटर्न केवल `\((\d{2,4})\)` है। यदि किसी मान में संयोगवश कोष्ठकों में दो-से-चार अंकों की कोई संख्या हो, तो उसके भी कोष्ठक खुल जाएँगे। इसे केवल उसी स्रोत पर लगाइए जो HRI कोष्ठक-परंपरा का प्रयोग करता हो, न कि उस पर जिसमें सचमुच कोष्ठकित मान आते हों।

#### `ModifierRemoveSpaces`

कुछ स्कैनर, मिडलवेयर और लेबल-मुद्रण प्रणालियाँ अन्यथा सुगठित एलिमेंट स्ट्रिंग में अनावश्यक रिक्तियाँ डाल देती हैं — किसी नियत-चौड़ाई क्षेत्र को भरने के लिए, पठनीय समूह अलग करने के लिए, या किसी लंबे मान को लपेटने के लिए। टोकनाइज़र हर रिक्ति को डेटा मानता है, जिससे जिस मान में वह बैठी है वह बिगड़ जाता है और, परिवर्तनीय-लंबाई वाले AI की स्थिति में, उसके आगे का सब कुछ खिसक जाता है।

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

केवल ASCII `0x20` हटाया जाता है। अन्य श्वेत-रिक्तियाँ यथावत् रहती हैं — उदाहरण के लिए, टैब GS1 के एन्कोड-योग्य समुच्चय से बाहर है, इसलिए पार्सर उसे चुपचाप बुहार देने के बजाय `GE-S008` के रूप में प्रतिवेदित करता है।

> **सीमा।** रिक्ति (`0x20`) GS1 के अपरिवर्तनीय वर्ण-समुच्चय का अंग है, इसलिए किसी बैच/लॉट या ग्राहक पुर्ज़ा-संख्या में वैध रूप से भी रिक्ति हो सकती है। यह मॉडिफ़ायर अनावश्यक और असली रिक्ति में भेद नहीं कर सकता; इसे केवल उसी स्रोत पर लगाइए जिसके बारे में ज्ञात हो कि वह अपने AI मानों के भीतर रिक्तियाँ नहीं रखता।

#### उपसर्ग छोड़े जाते हैं, पुनर्लिखित नहीं होते

मॉडिफ़ायर तब चलते हैं जब पार्सर ने अभी कुछ भी नहीं हटाया होता, इसलिए कच्चा इनपुट अब भी सहसंबंध ID, AIM कोड ID और ECI सूचक धारण कर सकता है। दोनों अंतर्निहित मॉडिफ़ायर पार्सर के अपने `CorrelationIdParser` और `DataCarrierParser` तर्क से AI एलिमेंट स्ट्रिंग का आरंभ खोजते हैं, केवल वहीं से आगे पुनर्लेखन करते हैं, और परिणाम को **अछूते** उपसर्ग के साथ वापस जोड़ देते हैं:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

जिन EAN/UPC कैरियरों का मान GTIN-14 तक भरा जाता है (`isRequiresGtinPadding()`), वे पूरी तरह छोड़ दिए जाते हैं — उनका पेलोड बिना किसी AI संरचना का कच्चा संख्यात्मक बारकोड मान होता है, इसलिए वहाँ न कोष्ठक सार्थक हो सकते हैं और न रिक्तियाँ।

#### क्रम: कोष्ठकों से पहले रिक्तियाँ

जब दोनों प्रयुक्त हों, तो **`ModifierRemoveSpaces` को पहले पंजीकृत कीजिए**। कोष्ठक-मिलान स्थिति-संवेदी है: रिक्तियों से भरा `( 01 )` `\((\d{2,4})\)` से मेल नहीं खाता, इसलिए कोष्ठक बचे रह जाते हैं और उनसे निहित विभाजक कभी बहाल नहीं होता।

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### मॉडिफ़ायर लिखना

जब दोनों अंतर्निहित मॉडिफ़ायर में से कोई भी उपयुक्त न हो, तब अपना लिखिए — इंटरफ़ेस में केवल एक विधि है।

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

जब पुनर्लेखन पार्स विन्यास पर निर्भर हो, तब इसके बजाय दो-तर्क वाले रूप को अधिरोहित कीजिए:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

अनुबंध:

| नियम | विवरण |
|---|---|
| अवस्था-रहित और थ्रेड-सुरक्षित | हर क्लास का एक उदाहरण संचित रहता है और हर पार्स में साझा होता है। |
| तर्क-रहित सार्वजनिक कंस्ट्रक्टर | केवल तभी आवश्यक जब मॉडिफ़ायर को क्लास नाम से संदर्भित किया जाए। |
| `null` और रिक्त इनपुट सँभालें | शृंखला चलने से पहले पार्सर उन्हें छानता नहीं है। |
| `null` लौटाने का अर्थ है "कोई परिवर्तन नहीं" | पिछला मान आगे बढ़ा दिया जाता है। जब मॉडिफ़ायर लागू न होता हो तो `input` को अपरिवर्तित लौटाइए। |
| अपवाद फेंकने के बजाय अपरिवर्तित लौटाना बेहतर है | अपवाद फेंकने वाला मॉडिफ़ायर पार्सिंग रद्द कर देता है — देखें [विफलता प्रबंधन](#मडफयर-क-वफलत-क-परबधन)। |
| `getName()` | `ModifierInfo` पर प्रतिवेदित नाम नियंत्रित करने के लिए अधिरोहित कीजिए; डिफ़ॉल्ट सरल क्लास नाम है। |

### मॉडिफ़ायर पंजीकृत करना

मॉडिफ़ायर उसी क्रम में चलते हैं जिसमें वे जोड़े गए हों, और हर एक को पिछले का आउटपुट मिलता है। इन्हें उदाहरण से, पूर्ण-योग्य क्लास नाम से, या इनमें से किसी की सूची से पंजीकृत कीजिए:

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

[अंतर्निहित मॉडिफ़ायर](#अतरनहत-मडफयर) को भी वैसे ही नामित किया जाता है जैसे आपके अपने को — **सदैव पूर्ण-योग्य रूप में**। उनके लिए कोई संक्षिप्त नाम या उपनाम-खोज नहीं है; `ModifierRegistry` हर मॉडिफ़ायर को, वितरित हो या न हो, पूरे क्लास नाम से हल करता है।

नाम `ModifierRegistry` हल करता है, जो हर क्लास का एक उदाहरण उसके तर्क-रहित कंस्ट्रक्टर से एक बार बनाता है और उसी क्लास को नामित करने वाले हर बाद के विन्यास के लिए उसे संचित रखता है। यह हल **विन्यास बनते समय** होता है, इसलिए जो नाम न मिले, जो `ModifierInterface` लागू न करता हो, या जिसका उदाहरण न बन सके, वह वहीं `IllegalArgumentException` फेंकता है — पार्स के समय चुपचाप नहीं। जो मॉडिफ़ायर परावर्तन से नहीं बनाया जा सकता (मान लीजिए वह कोई अंतःक्षिप्त निर्भरता रखता हो), उसे पहले से पंजीकृत किया जा सकता है ताकि वह नाम से संबोधनीय बना रहे:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### मॉडिफ़ायर ने क्या किया, यह देखना

जब मॉडिफ़ायर विन्यस्त हों, तब `ParseResult.getPayload()` **संशोधित** इनपुट दर्शाता है। मूल इनपुट `ModifierInfo` पर सुरक्षित रहता है:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` हर मॉडिफ़ायर का `getName()` प्रतिवेदित करता है, जिसका डिफ़ॉल्ट सरल क्लास नाम है पर दोनों अंतर्निहित मॉडिफ़ायर उसे अधिरोहित करते हैं — इसलिए इन दोनों की शृंखला क्लास नामों के बजाय प्रदर्शन-नाम प्रतिवेदित करती है:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

जब कोई मॉडिफ़ायर विन्यस्त न हो तो `getModifierInfo()` `null` लौटाता है। जब मॉडिफ़ायर चले तो हों पर हर एक ने इनपुट अपरिवर्तित लौटाया हो, तब सूचना उपस्थित रहती है और `isModified()` `false` होता है — `getAppliedModifiers()` में केवल वे मॉडिफ़ायर सूचीबद्ध होते हैं जिन्होंने वास्तव में इनपुट बदला हो।

### मॉडिफ़ायर की विफलता का प्रबंधन

अपवाद फेंकने वाला मॉडिफ़ायर पार्सिंग रद्द कर देता है। वह अपवाद `GaiaModifierException` में लपेटा जाता है जो दोषी मॉडिफ़ायर का नाम देता है, और परिणाम एक आंतरिक त्रुटि `GE-I001` वहन करता है जिसके संदेश में वही नाम होता है; `getPayload()` असंशोधित इनपुट प्रतिवेदित करता है। पार्सिंग जान-बूझकर आधे-अधूरे पुनर्लिखित स्ट्रिंग के साथ आगे **नहीं** बढ़ती — चुपचाप विफल हुआ कोई सामान्यीकरण-पग ऐसे परिणाम देगा जो देखने में वैध लगेंगे पर ग़लत इनपुट से पार्स किए गए होंगे।

---

## पार्स मोड

हर मोड का नाम उस सबसे गहरे [पाइपलाइन चरण](#परसग-पइपलइन) पर है जिसे वह चलाता है; उससे पहले के सभी चरण फिर भी चलते हैं।

| मोड | कहाँ तक चलता है | किसका उत्तर देता है |
|---|---|---|
| `DATA_CARRIER` | चरण 1 (इनपुट मार्गनिर्धारण) | इसे कौन-सी सिम्बोलॉजी लाई? |
| `SYNTAX` | चरण 2 (वाक्य-रचना) | क्या AI कोड और लंबाइयाँ सुगठित हैं? |
| `CONTENT` | चरण 3 (सामग्री) | क्या मान वैध GS1 डेटा हैं? |
| `INTERPRETATION` | चरण 4 (व्याख्या) | मानों का अर्थ क्या है? |

### DATA_CARRIER मोड

चरण 1 के बाद रुक जाता है — AIM कोड ID सत्यापित करता है और सिम्बोलॉजी पहचानता है, पर AI पार्सिंग पाइपलाइन में प्रवेश नहीं करता। पूर्ण सत्यापन के भार के बिना सिम्बोलॉजी पहचानने और मार्ग तय करने के लिए उपयोगी।

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

**कब प्रयोग करें:** जब आपके अनुप्रयोग को पेलोड संसाधित करने का तरीक़ा तय करने से पहले बारकोड का प्रकार जानना हो — जैसे 1D बनाम 2D सिम्बोलॉजी को अलग-अलग हैंडलर तक भेजना। उस मार्गनिर्धारण के लिए `getName()` का स्ट्रिंग-मिलान करने के बजाय टाइप-युक्त [`DataCarrierType`](#datacarrierentry-और-datacarriertype) (`getDataCarrier().getDataCarrierType()`) प्रयोग कीजिए।

---

### SYNTAX मोड

चरण 2 के बाद रुक जाता है। सामग्री-सत्यापन की लागत के बिना संरचनात्मक पूर्व-छँटाई के लिए उपयोगी।

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

**कब प्रयोग करें:** जब आप पूर्ण सत्यापन में उतरने से पहले यह जाँचना चाहें कि AI कोड और डेटा-लंबाइयाँ सुगठित हैं, या जब आप बड़ी मात्रा में स्कैन कर रहे हों जहाँ सामग्री-त्रुटियाँ विरल हों।

---

### CONTENT मोड

चरण 3 के बाद रुक जाता है।

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

> अधिकांश AI अकेले खड़े नहीं हो सकते: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) और
> `21` (SERIAL), प्रत्येक को उसी एलिमेंट स्ट्रिंग में AI `01` जैसी कोई पहचान-कुंजी
> *आवश्यक* होती है, इसलिए ऊपर से GTIN हटा देने पर सामग्री-सत्यापन तक पहुँचने से पहले ही
> चरण 2 में `GE-S005` के साथ विफलता होगी। जो अंश जान-बूझकर अपने सहचर AI के बिना हों,
> उन्हें पार्स करने के लिए `ParseConfig` पर `skipRequiresCheck(true)` निर्धारित कीजिए।

**कब प्रयोग करें:** जब किसी स्कैन किए मान को व्यावसायिक प्रक्रिया में प्रयोग करने से पहले आपको यह जानना हो कि वह पूर्णतः GS1-अनुरूप है या नहीं, पर व्याख्या-समृद्धीकरण का भार न उठाना हो।

---

### INTERPRETATION मोड (डिफ़ॉल्ट)

चरण 4 तक पूरी पाइपलाइन चलाता है। बिना मोड-तर्क के `parse(String)` बुलाने पर यही डिफ़ॉल्ट है। केवल उन्हीं एलिमेंटों को समृद्ध करता है जो सामग्री-सत्यापन साफ़-साफ़ पार कर चुके हों।

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

**मौद्रिक राशि का उदाहरण (AI 3932 — ISO मुद्रा कोड सहित मूल्य):**
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

**कब प्रयोग करें:** प्रदर्शन-परतें, लेबल-सत्यापन उपकरण, या ऐसा कोई भी UI बनाते समय जिसे AI मानों का मनुष्य-अनुकूल विश्लेषण चाहिए।

---

## सहसंबंध ID

कुछ कार्य-प्रवाह कच्चे GS1 इनपुट के आगे एक स्वामित्व वाला 8-अंकीय सहसंबंध पहचानकर्ता जोड़ देते हैं, ताकि स्कैन-घटनाओं को किसी सत्र या लेनदेन से जोड़ा जा सके। इसका प्रारूप है:

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

`~` (टिल्ड) विभाजक है। यह GS1 सामग्री का अंग **नहीं** है — कोई भी GS1 पार्सिंग शुरू होने से पहले ही इसे हटा दिया जाता है।

### पहचान के नियम

उपसर्ग तब पहचाना जाता है जब इनपुट ठीक 8 ASCII दशमलव अंकों (`0`–`9`) से शुरू हो और उनके तुरंत बाद `~` हो। यदि नौवाँ वर्ण `~` न हो, या पहले 8 वर्णों में कोई अंक न हो, तो इनपुट को बिना सहसंबंध उपसर्ग वाली सादी GS1 सामग्री माना जाता है।

### सहसंबंध ID तक पहुँच

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

### AIM कोड ID के साथ संयोजन

सहसंबंध उपसर्ग AIM कोड ID से पहले आ सकता है। पार्सर इसे पारदर्शी ढंग से सँभाल लेता है:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**कार्यान्वयन क्लास:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** एक या अधिक AI मानों को सीधे किसी HTTP(S) URL की संरचना में एन्कोड कर देता है, जिससे भौतिक उत्पादों के लिए वेब से हल हो सकने वाले पहचानकर्ता संभव होते हैं। GAIA **असंपीड़ित** URI के लिए *GS1 Digital Link Standard: URI Syntax* (रिलीज़ 1.7.0) लागू करता है।

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` Digital Link URI को स्वतः पहचान लेता है — `http://` या `https://` से शुरू होने वाला हर इनपुट `GS1DLParser` को भेजा जाता है, जो एलिमेंट-स्ट्रिंग पाइपलाइन जैसे ही सामग्री और व्याख्या चरण चलाता है।

### URI की संरचना और AI की भूमिकाएँ

Digital Link URI में हर AI तीन भूमिकाओं में से एक निभाता है, जो हर `GS1AIObjectElement` पर `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) से उपलब्ध होती है:

| भूमिका | स्थान | उदाहरण |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | पथ का पहला `/ai/value` युग्म (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | उसके बाद के पथ-युग्म, प्राथमिक कुंजी के अनुसार क्रमित (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | पूर्णतः संख्यात्मक कुंजियों वाले क्वेरी पैरामीटर (§4.10) | `?17=271231` |

लागू किए जाने वाले संरचनात्मक नियम (`DLPathRules`):
- पथ में ठीक **एक** प्राथमिक पहचान कुंजी; अतिरिक्त कुंजियों को क्वेरी डेटा-विशेषताओं के रूप में एन्कोड करना अनिवार्य है।
- कुंजी-योग्यक प्राथमिक कुंजी द्वारा स्वीकार्य होने चाहिए और निर्धारित क्रम में आने चाहिए। वैकल्पिक योग्यक छोड़े जा सकते हैं, पर जो *उपस्थित* हों उन्हें निश्चित क्रम का पालन करना ही होगा — देखें [योग्यकों का क्रम](#यगयक-क-करम)।
- प्राथमिक कुंजी से पहले मनमाने कस्टम पथ-खंड आ सकते हैं (जैसे `/products/au/01/...`); इन्हें `getDigitalLinkInfo().getCustomPathStem()` से प्राप्त कीजिए।
- ग़ैर-संख्यात्मक क्वेरी कुंजियाँ (`linkType`, `context`, `23P` जैसे विस्तार-पैरामीटर) उपेक्षित रहती हैं; पूर्णतः संख्यात्मक कुंजियाँ `validAsDataAttribute` से चिह्नित वैध AI होनी चाहिए।
- प्रतिशत-एन्कोडेड मान-वर्ण डिकोड किए जाते हैं; AI `(03)` और `(8014)` की अनुमति नहीं है।

प्राथमिक कुंजियाँ और उनके स्वीकार्य योग्यक-अनुक्रम हार्ड-कोडेड नहीं, बल्कि AI परिभाषाओं से **डेटा-संचालित** हैं — `gs1DigitalLinkPrimaryKey` ध्वज और `gs1DigitalLinkQualifiers` विशेषता से।

किसी भी संरचनात्मक उल्लंघन, या ग़ैर-URL इनपुट, से Digital Link संरचनात्मक त्रुटि उत्पन्न होती है (`GE-L001`–`GE-L014`, प्रति शर्त एक कोड)। विघटित URL मेटाडेटा (`scheme`, `domain`, `path`, `customPathStem`, `query`, और `java.net.URL`) संरचनात्मक त्रुटियों की उपस्थिति में भी `getDigitalLinkInfo()` से उपलब्ध रहता है।

### योग्यकों का क्रम

प्रत्येक प्राथमिक कुंजी के लिए `gs1DigitalLinkQualifiers` एक या अधिक **क्रमित** योग्यक-अनुक्रम सूचीबद्ध करता है। किसी अनुक्रम के भीतर वर्ग-कोष्ठकों में लपेटा गया AI **वैकल्पिक** है, बिना कोष्ठक वाला AI **अनिवार्य** — यह §4.9 के ABNF की `[cpv-comp]` संकेत-पद्धति का ही प्रतिबिंब है। एक ही प्राथमिक कुंजी के अनुक्रम परस्पर अनन्य विकल्प हैं।

उदाहरण के लिए, GTIN (`01`) दो अनुक्रम परिभाषित करता है:

| पथ | अनुक्रम | अर्थ |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — प्रत्येक वैकल्पिक, पर इसी क्रम में निश्चित |
| upui-path | `235` | TPX (अनिवार्य); GTIN + TPX = UPUI |

अतः `/01/09506000134352/10/LOT-ABC/21/SER` वैध है (SER से पहले LOT, CPV छोड़ा गया), `/01/.../21/SER/10/LOT-ABC` **अस्वीकृत** है (क्रम भंग), और `/01/09506000134352/235/2ABC456` upui-path है। क्रम-जाँच एक क्रम-रक्षी उप-अनुक्रम मिलान है, इसलिए वैकल्पिक AI छोड़े जा सकते हैं पर उनका क्रम कभी बदला नहीं जा सकता।

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

**कार्यान्वयन क्लास:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## परिणामों के साथ काम करना

### ParseResult

`GaiaParser.parse()` द्वारा लौटाया गया शीर्ष-स्तरीय परिणाम।

| विधि | क्या लौटाती है | विवरण |
|---|---|---|
| `isValid()` | `boolean` | `true` यदि किसी भी स्तर पर कोई त्रुटि न हो। चेतावनियाँ वैधता को प्रभावित नहीं करतीं। जब `getAiObject()` `null` हो तब सदैव `true`। |
| `getPayload()` | `String` | सहसंबंध उपसर्ग हटाने के बाद — और किसी भी [इनपुट मॉडिफ़ायर](#इनपट-मडफयर) द्वारा पुनर्लेखन के बाद — की इनपुट स्ट्रिंग। |
| `getPayloadContent()` | `String` | AIM कोड ID और ECI उपसर्ग हटाने के बाद का पेलोड। |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (ग़ैर-GS1 मानकर अस्वीकृत डेटा कैरियर, जैसे Code 39 का `]A0` कैरियर), या `UNABLE_TO_DETERMINE_CONTENT` (जब `aiObject` `null` हो, जैसे `DATA_CARRIER` मोड में)। |
| `getRequestedParseMode()` | `ParseMode` | विन्यस्त पाइपलाइन गहराई (`ParseConfig.getRequestedParseMode()`)। |
| `getAchievedParseMode()` | `ParseMode` | पार्सिंग वास्तव में जिस सबसे गहरे चरण तक पहुँची — नीचे देखें। |
| `isParseComplete()` | `boolean` | `true` यदि पार्सिंग अनुरोधित गहराई तक पहुँची (`achieved == requested`)। `isValid()` से स्वतंत्र। |
| `getAiObject()` | `GS1AIObject` | सभी हल किए गए AI। `DATA_CARRIER` मोड में `null`। |
| `getErrors()` | `List<GaiaError>` | सभी ग़ैर-WARNING त्रुटियाँ (ऑब्जेक्ट-स्तर + सभी एलिमेंट-स्तर)। |
| `getWarnings()` | `List<GaiaError>` | सभी WARNING सलाहें (ऑब्जेक्ट-स्तर + सभी एलिमेंट-स्तर)। |
| `hasWarnings()` | `boolean` | `true` यदि कोई WARNING सलाह उठाई गई हो। |
| `getIssues()` | `List<GaiaError>` | त्रुटियाँ और चेतावनियाँ, दोनों मिलाकर। |
| `hasDataCarrier()` | `boolean` | `true` यदि कोई AIM कोड ID पहचाना गया हो। |
| `getDataCarrier()` | `DataCarrierEntry` | सिम्बोलॉजी मेटाडेटा, या `null` यदि कोई कैरियर न पहचाना गया हो। |
| `hasEci()` | `boolean` | `true` यदि पेलोड से कोई ECI सूचक हटाया गया हो। |
| `getEci()` | `EciEntry` | ECI एन्कोडिंग मेटाडेटा, या `null`। |
| `hasCorrelationId()` | `boolean` | `true` यदि मूल इनपुट में `DDDDDDDD~` सहसंबंध उपसर्ग उपस्थित था। |
| `getCorrelationInfo()` | `CorrelationInfo` | निकाला गया सहसंबंध ID, या `null` यदि कोई उपस्थित न था। |
| `isInputModified()` | `boolean` | `true` यदि किसी [इनपुट मॉडिफ़ायर](#इनपट-मडफयर) ने इनपुट बदला हो। |
| `getModifierInfo()` | `ModifierInfo` | मॉडिफ़ायर शृंखला ने क्या किया — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`। यदि कोई मॉडिफ़ायर विन्यस्त न हो तो `null`। |
| `getTiming()` | `ProcessingTiming` | पार्स का दीवार-घड़ी समय — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`। यदि `GaiaParser` ने न बनाया हो तो `null`। |
| `getVersion()` | `String` | वह लाइब्रेरी संस्करण जिसने यह परिणाम बनाया। |

#### अनुरोधित बनाम प्राप्त पार्स मोड

पाइपलाइन **SYNTAX → CONTENT → INTERPRETATION** की सीढ़ी चढ़ती है और त्रुटियों पर पहले ही रुक जाती है, इसलिए वास्तव में *प्राप्त* मोड *अनुरोधित* मोड से उथला हो सकता है। `getAchievedParseMode()` बताता है कि वह कहाँ तक पहुँची:

| अनुरोधित | क्या होता है | प्राप्त | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | कोई **वाक्य-रचनात्मक / संरचनात्मक** त्रुटि टोकनीकरण के बाद पार्सिंग रोक देती है | `SYNTAX` | `false` |
| `INTERPRETATION` | कोई **सामग्री** त्रुटि (ग़लत प्रारूप/जाँच अंक) समृद्धीकरण रोक देती है | `CONTENT` | `false` |
| `CONTENT` | सामग्री चरण सदैव पूरा चलता है (त्रुटियाँ अंकित होती हैं, प्राणघातक नहीं) | `CONTENT` | `true` |
| कोई भी (स्वच्छ इनपुट) | पाइपलाइन अनुरोधित गहराई तक पहुँच जाती है | = अनुरोधित | `true` |
| `DATA_CARRIER` | कैरियर सत्यापित; कोई AI सामग्री पार्स नहीं हुई | `DATA_CARRIER` | `true` |
| कोई भी | AI पार्सिंग से पहले ही डेटा कैरियर अस्वीकृत (जैसे ग़ैर-GS1 `]A0` कैरियर) | `SYNTAX` | `false` |

`isParseComplete()` `isValid()` से स्वतंत्र है: ग़लत जाँच अंक वाले GTIN की `CONTENT` पार्सिंग **पूर्ण** है (सामग्री चरण चला) पर **अवैध** है (जाँच अंक विफल हुआ)। "क्या पाइपलाइन उतनी गहराई तक चली जितनी मैंने माँगी?" पूछने के लिए `isParseComplete()` और "क्या डेटा सुगठित है?" पूछने के लिए `isValid()` प्रयोग कीजिए।

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

हल किए गए AI एलिमेंटों का संग्रह।

| विधि | विवरण |
|---|---|
| `getAis()` | सभी `GS1AIObjectElement` उदाहरण, इनपुट के क्रम में। |
| `get(String aiCode)` | दिए गए AI कोड से मेल खाता पहला एलिमेंट, या `null`। |
| `contains(String aiCode)` | `true` यदि उस कोड वाला AI उपस्थित हो। |
| `size()` | हल किए गए AI की संख्या। |
| `isValid()` | `true` यदि कोई ऑब्जेक्ट-स्तरीय त्रुटि न हो और किसी एलिमेंट पर त्रुटि न हो। |
| `toHriString()` | HRI स्ट्रिंग, जैसे `(01)09506000134352 (17)261231`। |
| `toElementString()` | कच्ची एलिमेंट स्ट्रिंग — बिना कोष्ठकों के, हर परिवर्तनीय-लंबाई एलिमेंट के बाद FNC1 सहित — जैसे `010950600013435210LOT-ABC<GS>17271231`। यदि `isValid()` `false` हो तो `null` लौटाती है। |
| `getContentType()` | `hasDigitalLink()` सत्य होने पर `GS1_DIGITAL_LINK`, अन्यथा `GS1_APPLICATION_IDENTIFIERS`। |
| `hasDigitalLink()` | `true` यदि इनपुट कोई ऐसा GS1 Digital Link URI था जिसमें प्राथमिक पहचान कुंजी थी। बिना प्राथमिक कुंजी वाला सुगठित URL `getDigitalLinkInfo()` तो देता है, पर यहाँ `false` लौटाता है। |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` पर विहित GS1 Digital Link URI (§4.12) — प्राथमिक कुंजी और योग्यक पथ-खंडों के रूप में, डेटा-विशेषताएँ AI कुंजी से क्रमित क्वेरी पैरामीटरों के रूप में — या यदि कोई प्राथमिक कुंजी न हो तो `null`। |
| `getDigitalLinkInfo()` | URI विघटन मेटाडेटा (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), या Digital Link न होने पर `null`। |
| `getAllErrors()` | ऑब्जेक्ट-स्तरीय + सभी एलिमेंट त्रुटियाँ (ग़ैर-WARNING)। |
| `getAllWarnings()` | ऑब्जेक्ट-स्तरीय + सभी एलिमेंट चेतावनियाँ। |
| `getAllIssues()` | सब कुछ मिलाकर। |

---

### GS1AIObjectElement

हल किया गया एक AI उदाहरण।

| विधि | विवरण |
|---|---|
| `getAi()` | AI कोड, जैसे `"01"`, `"3102"`। |
| `getTitle()` | GS1 डेटा शीर्षक, जैसे `"GTIN"`, `"BATCH/LOT"`। |
| `getDescription()` | AI का पूरा GS1 विवरण, **पार्स भाषा में स्थानीयकृत** (अंग्रेज़ी में जैसे `"Global Trade Item Number (GTIN)"`)। अनूदित न होने पर AI परिभाषा के अंग्रेज़ी पाठ पर लौट जाता है। |
| `getFormatString()` | प्रारूप-विवरणक जो AI *और* उसके डेटा दोनों को समेटता है, जैसे AI `01` के लिए `"N2+N14"`, AI `10` के लिए `"N2+X..20"`, AI `3932` के लिए `"N4+N3+N..15"`। |
| `getValue()` | एलिमेंट स्ट्रिंग से निकाला गया कच्चा डेटा मान। |
| `isFixedLength()` | `true` यदि AI की डेटा-लंबाई नियत हो। |
| `getPosition()` | मूल इनपुट में शून्य से आरंभ होने वाला वर्ण-स्थान। |
| `getGS1ComponentValues()` | प्रति-घटक मान-टुकड़े (बहु-घटक AI के लिए)। |
| `getErrors()` | एलिमेंट-स्तरीय ग़ैर-WARNING त्रुटियाँ। |
| `getWarnings()` | एलिमेंट-स्तरीय WARNING सलाहें। |
| `getIssues()` | एलिमेंट-स्तरीय त्रुटियाँ और चेतावनियाँ, मिलाकर। |
| `hasErrors()` | `true` यदि कोई ग़ैर-WARNING त्रुटि संलग्न हो। |
| `hasWarnings()` | `true` यदि कोई WARNING सलाह संलग्न हो। |
| `getInterpretations()` | `GS1AIInterpretation` प्रविष्टियाँ (INTERPRETATION मोड में भरी जाती हैं)। |
| `getInterpretation(String type)` | दी गई `GS1Constants_Enricher` प्रकार-कुंजी से मेल खाती पहली व्याख्या, या `null`। |
| `getDigitalLinkAIType()` | एलिमेंट की Digital Link भूमिका (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), या एलिमेंट-स्ट्रिंग इनपुट के लिए `null`। |
| `hasDigitalLinkAIType()` | `true` यदि कोई Digital Link भूमिका सौंपी गई हो। |

---

### GaiaError

अपरिवर्तनीय सत्यापन त्रुटि या सलाह।

| विधि | विवरण |
|---|---|
| `getId()` | सूची-पहचानकर्ता, जैसे `"GE-C003"`। |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, या `WARNING`। |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, या `INTERNAL`। |
| `getCode()` | मशीन-पठनीय संक्षिप्त कोड। |
| `getAi()` | वह AI कोड जिसने त्रुटि उत्पन्न की, या ऑब्जेक्ट-स्तरीय त्रुटियों के लिए `null`। |
| `getMessage()` | मान भरकर तैयार किया गया मनुष्य-पठनीय संदेश। |
| `getPosition()` | मूल इनपुट में शून्य से आरंभ होने वाला वर्ण-स्थान। |

---

### GS1AIInterpretation

एक लेबल-युक्त व्याख्या-अंश, जो `INTERPRETATION` मोड में `GS1AIObjectElement` से संलग्न होता है।

| विधि | विवरण |
|---|---|
| `getType()` | मशीन-पठनीय प्रकार-कुंजी, जैसे `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`। सभी भाषाओं में अपरिवर्तित। |
| `getLabel()` | मनुष्य-पठनीय लेबल, **पार्स भाषा में स्थानीयकृत** (अंग्रेज़ी में जैसे `"Date"` / `"GS1 company prefix"`)। |
| `getValue()` | निकाला/समृद्ध किया गया मान, जैसे `"31/12/2026"`, `"9506000"`। स्थानीयकृत नहीं। |

---

### DataCarrierEntry और DataCarrierType

जब इनपुट कोई AIM कोड ID वहन करता है, तब `ParseResult.getDataCarrier()` एक `DataCarrierEntry` लौटाता है जो उस प्रतीक का वर्णन करता है जिसने डेटा ढोया। यह प्रविष्टि मेल खाए AIM कोड ID के लिए विशिष्ट रजिस्ट्री अभिलेख है; `DataCarrierType` वह संकलन-समय enum है जिससे वह संबंधित है।

#### DataCarrierEntry

एक पहचाने गए AIM कोड ID का मेटाडेटा (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`)।

| विधि | विवरण |
|---|---|
| `getAimCodeId()` | वह AIM कोड ID जो मेल खाया, जैसे `"]C1"`। |
| `getName()` | उस विशिष्ट प्रतीक का मनुष्य-पठनीय नाम, जैसे `"GS1-128 / ISBT 128"`, `"EAN-8"`। |
| `getDescription()` | कैरियर का विस्तृत विवरण। |
| `getType()` | कैरियर का संरचनात्मक प्रकार, स्ट्रिंग के रूप में (`getDataCarrierType().getCategory()` का प्रतिबिंब)। |
| `getStandard()` | सिम्बोलॉजी मानक, जहाँ अभिलिखित हो। |
| `getDataCarrierType()` | इस प्रविष्टि के लिए टाइप-युक्त `DataCarrierType` — प्रोग्राम-आधारित मार्गनिर्धारण के लिए यही प्रयोग कीजिए। |
| `isGs1Capable()` | `true` यदि कैरियर GS1 डेटा धारण कर सकता हो (AI एलिमेंट स्ट्रिंग और/या Digital Link)। |
| `isGs1AICapable()` | `true` यदि कैरियर GS1 AI एलिमेंट स्ट्रिंग धारण कर सकता हो। |
| `isGs1DigitalLinkCapable()` | `true` यदि कैरियर GS1 Digital Link URI धारण कर सकता हो। |
| `isEciCapable()` | `true` यदि कैरियर ECI सूचक का समर्थन करता हो। |
| `isRequiresGtinPadding()` | `true` उन EAN/UPC/ITF कैरियरों के लिए जिनका संख्यात्मक मान AI पार्सिंग से पहले GTIN-14 तक भरा जाता है। |

#### DataCarrierType

डेटा-कैरियर प्रकारों का संकलन-समय enum, जिसकी कुंजी ISO/IEC 15424 में आवंटित AIM कोड ID है (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`)। `]` के बाद का वर्ण (*कोड वर्ण*) कुल चुनता है; अधिकांश कुल एक ही स्थिरांक पर मैप होते हैं जो हर संशोधक को समेटता है (`ITF` में `]I0`–`]I2` आते हैं; `EAN_UPC` में EAN-13, UPC-A, UPC-E और EAN-8)। जहाँ GS1 किसी संशोधक को AI डेटा के लिए आरक्षित रखता है, वहाँ वह प्रकार अपना अलग स्थिरांक है — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — अपने सादे समकक्षों से भिन्न। जब कोई AIM कोड ID उपस्थित न हो, या वह किसी अज्ञात कैरियर को नामित करे, तब प्रकार `UNKNOWN` होता है।

| विधि | विवरण |
|---|---|
| `getCategory()` | व्यापक श्रेणी `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, या `OTHER`। |
| `getCodeChar()` | कुल की पहचान कराने वाला AIM कोड वर्ण, जैसे QR Code के लिए `"Q"`; `UNKNOWN` के लिए `null`। |
| `getDisplayName()` | *प्रकार* का मनुष्य-पठनीय नाम (यह `DataCarrierEntry.getName()` से अधिक व्यापक हो सकता है — जैसे `"EAN-13 / UPC-A / UPC-E / EAN-8"` बनाम `"EAN-8"`)। |
| `isGs1DataCarrier()` | `true` उन स्थिरांकों के लिए जो सदैव GS1 AI डेटा का संकेत देते हैं: GS1 द्वारा आरक्षित चार प्रकार (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) और साथ में `GS1_DATABAR`, जो स्वभावतः GS1 है क्योंकि हर `]e` संशोधक GS1 DataBar ही होता है। यह `DataCarrierEntry.isGs1AICapable()` से संकीर्ण है — सादा `QR_CODE` भी GS1 AI डेटा ढो सकता है। |
| `static forAimCodeId(String)` | सीधे किसी AIM कोड ID से प्रकार हल करता है (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); अनुपस्थित, विकृत या अपरिचित ID के लिए `UNKNOWN` लौटाता है। |

नाम के बजाय प्रकार से मार्गनिर्धारण — जैसे रैखिक (Code-128) प्रतीकों को 2D (QR / Data Matrix) से अलग करना:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` में केवल मैट्रिक्स और डॉट प्रतीक आते हैं; स्तरित-रैखिक कैरियर (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) `STACKED_LINEAR` हैं, यद्यपि उन्हें आम तौर पर
"2D" बारकोड कहा जाता है। दोनों को एक ही समूह मानने के लिए — मान लीजिए यह तय करने के लिए
कि लेज़र स्कैनर के बजाय इमेजर चाहिए या नहीं — दोनों श्रेणियों की जाँच कीजिए।

> प्रकार का निर्धारण तभी संभव है जब स्कैन में AIM कोड ID उपस्थित हो; उसके बिना `getDataCarrier()` `null` होता है और प्रकार `UNKNOWN`। स्कैनर को AIM कोड ID उपसर्ग भेजने के लिए विन्यस्त कीजिए।

---

## त्रुटि संदर्भ

| कोड | स्तर | चरण | अर्थ |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | अज्ञात AI उपसर्ग — डेटा-लंबाई निर्धारित नहीं की जा सकती |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | पूरा AI कोड पढ़ने के लिए इनपुट बहुत छोटा है |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | कटा-फटा मान — AI की आवश्यकता से कम वर्ण |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | एलिमेंट स्ट्रिंग में दोहराया गया एप्लिकेशन आइडेंटिफ़ायर |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | अनिवार्य AI निर्भरता अनुपस्थित |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | वर्जित AI युग्म — दो AI जो साथ नहीं आ सकते |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | अप्रत्याशित टोकनीकरण विफलता |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | एलिमेंट स्ट्रिंग में GS1 एन्कोड-योग्य समुच्चय से बाहर का वर्ण |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | परिवर्तनीय-लंबाई AI के बाद अनिवार्य FNC1 विभाजक अनुपस्थित |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | सभी घटक-अधिकतमों से आगे शेष डेटा |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | स्ट्रिंग के बीच की स्थिति में नियत-लंबाई AI के बाद FNC1 विभाजक |
| `GE-W002` | WARNING | SYNTAX | एलिमेंट स्ट्रिंग के अंत में शेष FNC1 (केवल सलाह) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI के संरचनात्मक उल्लंघन — प्रति शर्त एक कोड (विकृत URI, स्कीम, होस्ट, योग्यक क्रम, वर्जित AI, कोई प्राथमिक कुंजी नहीं (`GE-L013`), एक से अधिक प्राथमिक कुंजियाँ (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | मान AI के रेगेक्स पैटर्न पर खरा नहीं उतरता |
| `GE-C003` | DATA_ERROR | CONTENT | जाँच अंक सत्यापन विफल |
| `GE-C004` | DATA_ERROR | CONTENT | जाँच वर्ण युग्म सत्यापन विफल |
| `GE-C005` | FORMAT_ERROR | CONTENT | घटक-मान में अनुमत वर्ण-समुच्चय से बाहर का वर्ण |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | घटक-प्रारूप विफलताएँ — प्रति सत्यापक-शर्त एक कोड (देखें `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | कस्टम शब्दार्थ-सत्यापन विफलताएँ — प्रति सत्यापक-शर्त एक कोड (देखें `content/validator/`)। **अपवाद:** नीचे सूचीबद्ध 14 GS1 कंपनी-उपसर्ग जाँचें `WARNING` स्तर की हैं, और `GE-C168` (अपरिचित ISO 3166-1 संख्यात्मक देश कोड) `FORMAT_ERROR` स्तर का है। |
| GS1 कंपनी-उपसर्ग जाँचें | WARNING | CONTENT | GS1-कुंजी वाले AI पर कुंजी किसी पहचाने गए GS1 कंपनी उपसर्ग से शुरू नहीं होती — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC)। केवल सलाह — वैधता को प्रभावित नहीं करती। |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2) पर IMEI जाँच अंक (Luhn) विफल |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM) पर EID जाँच अंक (Luhn) विफल |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | अपरिचित AIM कोड ID |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | कैरियर पहचाना गया पर वह न GS1 AI एलिमेंट स्ट्रिंग का समर्थन करता है, न Digital Link URI का |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | अप्रत्याशित आंतरिक त्रुटि |

> **संदेश-प्रस्तुति में ज्ञात दोष।** सूची-टेम्पलेट भरे जाने वाले मानों को MessageFormat-शैली
> के दोहरे अपॉस्ट्रॉफ़ी (`''{value}''`) में उद्धृत करते हैं, पर `ErrorRegistry` सादे
> `String.replace` से मान भरता है, इसलिए वह दोहरापन `getMessage()` तक बचा रह जाता है —
> इस समय आपको वहाँ `value ''09506000134351''` दिखेगा जहाँ इस गाइड में उद्धृत संदेश-पाठ
> `value '09506000134351'` दिखाते हैं। यह सभी 35 भाषा-सूचियों के हर मान-उद्धरण वाले संदेश
> को प्रभावित करता है। त्रुटि-संदेशों को पार्स मत कीजिए; `getId()` / `getCode()` पर
> मिलान कीजिए।

---

## थ्रेड सुरक्षा

`GaiaParser` एक बार बन जाने के बाद थ्रेड-सुरक्षित है। एक ही उदाहरण कई थ्रेडों में साझा किया जा सकता है और समवर्ती रूप से प्रयुक्त हो सकता है। अनुशंसित प्रतिमान यह है कि अनुप्रयोग के आरंभ में एक उदाहरण बनाइए और उसी का पुनःप्रयोग कीजिए:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` अपरिवर्तनीय है और उतना ही सुरक्षित रूप से साझा किया जा सकता है। थ्रेड-सुरक्षा का एकमात्र दायित्व जो लाइब्रेरी आपकी ओर से लागू नहीं कर सकती, वह [इनपुट मॉडिफ़ायर](#इनपट-मडफयर) पर है: हर मॉडिफ़ायर का एक ही उदाहरण संचित रहकर हर समवर्ती पार्स में साझा होता है, इसलिए कार्यान्वयनों का अवस्था-रहित होना अनिवार्य है।

---

## परिशिष्ट अ — AI स्ट्रिंग स्थिरांक

`GS1Constants_AICodes` (पैकेज `tools.pantheum.gaia.gs1.constants`) GAIA द्वारा पहचाने गए प्रत्येक एप्लिकेशन आइडेंटिफ़ायर के लिए एक `String` स्थिरांक घोषित करता है। कोड में AI कोड के लिटरल हार्ड-कोड करने के बजाय इन स्थिरांकों का प्रयोग कीजिए:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

हर स्थिरांक अपने AI कोड का स्ट्रिंग रूप धारण करता है (जैसे `AI_01_GTIN = "01"`)।

### पहचान और क्रम-संख्यांकन

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | सीरियल शिपिंग कंटेनर कोड (SSCC). |
| `01` | `AI_01_GTIN` | ग्लोबल ट्रेड आइटम नंबर (GTIN). |
| `02` | `AI_02_CONTENT` | निहित व्यापार वस्तुओं का ग्लोबल ट्रेड आइटम नंबर (GTIN). |
| `03` | `AI_03_MTO_GTIN` | मेड-टू-ऑर्डर (MtO) व्यापार वस्तु की पहचान (GTIN). |
| `10` | `AI_10_BATCH_LOT` | बैच या लॉट नंबर. |
| `20` | `AI_20_VARIANT` | आंतरिक उत्पाद संस्करण. |
| `21` | `AI_21_SERIAL` | क्रम संख्या (सीरियल नंबर). |
| `22` | `AI_22_CPV` | उपभोक्ता उत्पाद संस्करण (वेरिएंट). |
| `235` | `AI_235_TPX` | थर्ड पार्टी नियंत्रित, ग्लोबल ट्रेड आइटम नंबर (GTIN) का सीरियलाइज़्ड विस्तार (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | निर्माता द्वारा निर्धारित अतिरिक्त उत्पाद पहचान. |
| `241` | `AI_241_CUST_PART_NO` | ग्राहक पार्ट नंबर. |
| `242` | `AI_242_MTO_VARIANT` | मेड-टू-ऑर्डर विविधता संख्या. |
| `243` | `AI_243_PCN` | पैकेजिंग घटक संख्या. |
| `250` | `AI_250_SECONDARY_SERIAL` | द्वितीयक क्रम संख्या. |
| `251` | `AI_251_REF_TO_SOURCE` | स्रोत इकाई का संदर्भ. |
| `253` | `AI_253_GDTI` | ग्लोबल डॉक्यूमेंट टाइप पहचानकर्ता (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | ग्लोबल लोकेशन नंबर (GLN) विस्तार घटक. |
| `255` | `AI_255_GCN` | ग्लोबल कूपन नंबर (GCN). |
| `30` | `AI_30_VAR_COUNT` | वस्तुओं की परिवर्तनीय गणना (परिवर्तनीय माप व्यापार वस्तु). |
| `37` | `AI_37_COUNT` | लॉजिस्टिक इकाई में निहित व्यापार वस्तुओं या व्यापार वस्तु टुकड़ों की गणना. |

### तिथि और समय

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | उत्पादन तिथि (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | नियत तिथि (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | पैकेजिंग तिथि (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | सर्वोत्तम उपयोग तिथि (YYMMDD). |
| `16` | `AI_16_SELL_BY` | विक्रय तिथि (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | समाप्ति तिथि (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | डिलीवरी की न्यूनतम प्रारंभिक तिथि-समय (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | डिलीवरी की अंतिम स्वीकार्य तिथि-समय (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | रिलीज़ तिथि (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | समाप्ति तिथि और समय (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | प्रथम फ्रीज तिथि (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | कटाई तिथि (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | परीक्षण तिथि (YYMMDD[hhmm]). |

### मात्रा और माप — परिवर्तनीय माप (मीट्रिक)

चार-अंकीय AI कुल `310n`–`369n` परिवर्तनीय-माप मात्राएँ एन्कोड करते हैं। तीसरा अंक माप का प्रकार चुनता है; **चौथा अंक** (`n`, 0–5) निहित दशमलव स्थानों की संख्या है — इसलिए `AI_3102_NET_WEIGHT_KG` का अर्थ है 2 दशमलव स्थानों के साथ किलोग्राम में शुद्ध वज़न।

| कुल | स्थिरांक का प्रतिरूप (`n` = दशमलव अंक) | विवरण |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | शुद्ध वज़न, किलोग्राम (परिवर्तनीय माप व्यापार वस्तु). |
| `311n` | `AI_311n_LENGTH_M` | लंबाई या पहला आयाम, मीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `312n` | `AI_312n_WIDTH_M` | चौड़ाई, व्यास, या दूसरा आयाम, मीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `313n` | `AI_313n_HEIGHT_M` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, मीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `314n` | `AI_314n_AREA_M` | क्षेत्रफल, वर्ग मीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `315n` | `AI_315n_NET_VOLUME_L` | शुद्ध आयतन, लीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `316n` | `AI_316n_NET_VOLUME_M` | शुद्ध आयतन, घन मीटर (परिवर्तनीय माप व्यापार वस्तु). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | लॉजिस्टिक वज़न, किलोग्राम. |
| `331n` | `AI_331n_LENGTH_M_LOG` | लंबाई या पहला आयाम, मीटर. |
| `332n` | `AI_332n_WIDTH_M_LOG` | चौड़ाई, व्यास, या दूसरा आयाम, मीटर. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, मीटर. |
| `334n` | `AI_334n_AREA_M_LOG` | क्षेत्रफल, वर्ग मीटर. |
| `335n` | `AI_335n_VOLUME_L_LOG` | लॉजिस्टिक आयतन, लीटर. |
| `336n` | `AI_336n_VOLUME_M_LOG` | लॉजिस्टिक आयतन, घन मीटर. |
| `337n` | `AI_337n_KG_PER_M` | किलोग्राम प्रति वर्ग मीटर. |

### मात्रा और माप — परिवर्तनीय माप (इंपीरियल / अमेरिकी)

| कुल | स्थिरांक का प्रतिरूप (`n` = दशमलव अंक) | विवरण |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | शुद्ध वज़न, पाउंड (परिवर्तनीय माप व्यापार वस्तु). |
| `321n` | `AI_321n_LENGTH_IN` | लंबाई या पहला आयाम, इंच (परिवर्तनीय माप व्यापार वस्तु). |
| `322n` | `AI_322n_LENGTH_FT` | लंबाई या पहला आयाम, फ़ीट (परिवर्तनीय माप व्यापार वस्तु). |
| `323n` | `AI_323n_LENGTH_YD` | लंबाई या पहला आयाम, गज़ (परिवर्तनीय माप व्यापार वस्तु). |
| `324n` | `AI_324n_WIDTH_IN` | चौड़ाई, व्यास, या दूसरा आयाम, इंच (परिवर्तनीय माप व्यापार वस्तु). |
| `325n` | `AI_325n_WIDTH_FT` | चौड़ाई, व्यास, या दूसरा आयाम, फ़ीट (परिवर्तनीय माप व्यापार वस्तु). |
| `326n` | `AI_326n_WIDTH_YD` | चौड़ाई, व्यास, या दूसरा आयाम, गज़ (परिवर्तनीय माप व्यापार वस्तु). |
| `327n` | `AI_327n_HEIGHT_IN` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, इंच (परिवर्तनीय माप व्यापार वस्तु). |
| `328n` | `AI_328n_HEIGHT_FT` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, फ़ीट (परिवर्तनीय माप व्यापार वस्तु). |
| `329n` | `AI_329n_HEIGHT_YD` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, गज़ (परिवर्तनीय माप व्यापार वस्तु). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | लॉजिस्टिक वज़न, पाउंड. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | लंबाई या पहला आयाम, इंच. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | लंबाई या पहला आयाम, फ़ीट. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | लंबाई या पहला आयाम, गज़. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | चौड़ाई, व्यास, या दूसरा आयाम, इंच. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | चौड़ाई, व्यास, या दूसरा आयाम, फ़ीट. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | चौड़ाई, व्यास, या दूसरा आयाम, गज़. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, इंच. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, फ़ीट. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | गहराई, मोटाई, ऊँचाई, या तीसरा आयाम, गज़. |
| `350n` | `AI_350n_AREA_IN` | क्षेत्रफल, वर्ग इंच (परिवर्तनीय माप व्यापार वस्तु). |
| `351n` | `AI_351n_AREA_FT` | क्षेत्रफल, वर्ग फ़ीट (परिवर्तनीय माप व्यापार वस्तु). |
| `352n` | `AI_352n_AREA_YD` | क्षेत्रफल, वर्ग गज़ (परिवर्तनीय माप व्यापार वस्तु). |
| `353n` | `AI_353n_AREA_IN_LOG` | क्षेत्रफल, वर्ग इंच. |
| `354n` | `AI_354n_AREA_FT_LOG` | क्षेत्रफल, वर्ग फ़ीट. |
| `355n` | `AI_355n_AREA_YD_LOG` | क्षेत्रफल, वर्ग गज़. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | शुद्ध वज़न, ट्रॉय औंस (परिवर्तनीय माप व्यापार वस्तु). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | शुद्ध वज़न (या आयतन), औंस (परिवर्तनीय माप व्यापार वस्तु). |
| `360n` | `AI_360n_NET_VOLUME_QT` | शुद्ध आयतन, क्वार्ट (परिवर्तनीय माप व्यापार वस्तु). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | शुद्ध आयतन, यूएस गैलन (परिवर्तनीय माप व्यापार वस्तु). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | लॉजिस्टिक आयतन, क्वार्ट. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | लॉजिस्टिक आयतन, यूएस गैलन. |
| `364n` | `AI_364n_NET_VOLUME_IN` | शुद्ध आयतन, घन इंच (परिवर्तनीय माप व्यापार वस्तु). |
| `365n` | `AI_365n_NET_VOLUME_FT` | शुद्ध आयतन, घन फ़ीट (परिवर्तनीय माप व्यापार वस्तु). |
| `366n` | `AI_366n_NET_VOLUME_YD` | शुद्ध आयतन, घन गज़ (परिवर्तनीय माप व्यापार वस्तु). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | लॉजिस्टिक आयतन, घन इंच. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | लॉजिस्टिक आयतन, घन फ़ीट. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | लॉजिस्टिक आयतन, घन गज़. |

### मूल्य और मौद्रिक राशियाँ

चौथा अंक (`n`) निहित दशमलव स्थानों की संख्या एन्कोड करता है। अनुमत परास
कुल के अनुसार भिन्न होता है — `n` स्तंभ देखें।

| कुल | स्थिरांक का प्रतिरूप (`n` = दशमलव अंक) | `n` | विवरण |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | लागू देय राशि या कूपन मूल्य, स्थानीय मुद्रा. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | आईएसओ मुद्रा कोड सहित लागू देय राशि. |
| `392n` | `AI_392n_PRICE` | 0–9 | लागू देय राशि, एकल मौद्रिक क्षेत्र (परिवर्तनीय माप व्यापार वस्तु). |
| `393n` | `AI_393n_PRICE` | 0–9 | आईएसओ मुद्रा कोड सहित लागू देय राशि (परिवर्तनीय माप व्यापार वस्तु). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | कूपन का प्रतिशत छूट. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | प्रति इकाई माप देय राशि, एकल मौद्रिक क्षेत्र (परिवर्तनीय माप व्यापार वस्तु). |

### स्थान और शिपिंग

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | ग्राहक क्रय आदेश संख्या. |
| `401` | `AI_401_GINC` | कंसाइनमेंट के लिए ग्लोबल पहचान संख्या (GINC). |
| `402` | `AI_402_GSIN` | ग्लोबल शिपमेंट पहचान संख्या (GSIN). |
| `403` | `AI_403_ROUTE` | रूटिंग कोड. |
| `410` | `AI_410_SHIP_TO_LOC` | शिप टू / डिलीवर टू ग्लोबल लोकेशन नंबर (GLN). |
| `411` | `AI_411_BILL_TO` | बिल टू / इनवॉइस टू ग्लोबल लोकेशन नंबर (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | क्रय स्रोत ग्लोबल लोकेशन नंबर (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | शिप फॉर / डिलीवर फॉर - फॉरवर्ड टू ग्लोबल लोकेशन नंबर (GLN). |
| `414` | `AI_414_LOC_NO` | भौतिक स्थान की पहचान - ग्लोबल लोकेशन नंबर (GLN). |
| `415` | `AI_415_PAY_TO` | इनवॉइस करने वाले पक्ष का ग्लोबल लोकेशन नंबर (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | उत्पादन या सेवा स्थान का ग्लोबल लोकेशन नंबर (GLN). |
| `417` | `AI_417_PARTY` | पार्टी ग्लोबल लोकेशन नंबर (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | एकल डाक प्राधिकरण के भीतर शिप टू / डिलीवर टू डाक कोड. |
| `421` | `AI_421_SHIP_TO_POST` | आईएसओ देश कोड सहित शिप टू / डिलीवर टू डाक कोड. |
| `422` | `AI_422_ORIGIN` | व्यापार वस्तु की उत्पत्ति का देश. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | प्रारंभिक प्रसंस्करण का देश. |
| `424` | `AI_424_COUNTRY_PROCESS` | प्रसंस्करण का देश. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | पृथक्करण (डिससेंबली) का देश. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | संपूर्ण प्रक्रिया श्रृंखला को कवर करने वाला देश. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | उत्पत्ति का देश उपखंड. |
| `4300` | `AI_4300_SHIP_TO_COMP` | शिप-टू / डिलीवर-टू कंपनी का नाम. |
| `4301` | `AI_4301_SHIP_TO_NAME` | शिप-टू / डिलीवर-टू संपर्क. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | शिप-टू / डिलीवर-टू पता, पंक्ति 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | शिप-टू / डिलीवर-टू पता, पंक्ति 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | शिप-टू / डिलीवर-टू उपनगर. |
| `4305` | `AI_4305_SHIP_TO_LOC` | शिप-टू / डिलीवर-टू इलाका. |
| `4306` | `AI_4306_SHIP_TO_REG` | शिप-टू / डिलीवर-टू क्षेत्र. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | शिप-टू / डिलीवर-टू देश कोड. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | शिप-टू / डिलीवर-टू टेलीफोन नंबर. |
| `4309` | `AI_4309_SHIP_TO_GEO` | शिप-टू / डिलीवर-टू जीईओ लोकेशन. |
| `4310` | `AI_4310_RTN_TO_COMP` | वापसी कंपनी का नाम. |
| `4311` | `AI_4311_RTN_TO_NAME` | वापसी संपर्क. |
| `4312` | `AI_4312_RTN_TO_ADD1` | वापसी पता, पंक्ति 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | वापसी पता, पंक्ति 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | वापसी उपनगर. |
| `4315` | `AI_4315_RTN_TO_LOC` | वापसी इलाका. |
| `4316` | `AI_4316_RTN_TO_REG` | वापसी क्षेत्र. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | वापसी देश कोड. |
| `4318` | `AI_4318_RTN_TO_POST` | वापसी डाक कोड. |
| `4319` | `AI_4319_RTN_TO_PHONE` | वापसी टेलीफोन नंबर. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | सेवा कोड विवरण. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | खतरनाक वस्तु फ्लैग. |
| `4322` | `AI_4322_AUTH_LEAVE` | छोड़ने का अधिकार (डिलीवरी प्राधिकरण). |
| `4323` | `AI_4323_SIG_REQUIRED` | हस्ताक्षर आवश्यक फ्लैग. |
| `4330` | `AI_4330_MAX_TEMP_F` | फारेनहाइट में अधिकतम तापमान (डिग्री के सौवें हिस्से में व्यक्त). |
| `4331` | `AI_4331_MAX_TEMP_C` | सेल्सियस में अधिकतम तापमान (डिग्री के सौवें हिस्से में व्यक्त). |
| `4332` | `AI_4332_MIN_TEMP_F` | फारेनहाइट में न्यूनतम तापमान (डिग्री के सौवें हिस्से में व्यक्त). |
| `4333` | `AI_4333_MIN_TEMP_C` | सेल्सियस में न्यूनतम तापमान (डिग्री के सौवें हिस्से में व्यक्त). |

### उत्पाद विशेषताएँ और अनुरेखणीयता

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | नाटो स्टॉक नंबर (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | यूएन/ईसीई मांस शव और कटौती वर्गीकरण. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | सक्रिय शक्ति (पोटेंसी). |
| `7005` | `AI_7005_CATCH_AREA` | पकड़ क्षेत्र (मत्स्य क्षेत्र). |
| `7008` | `AI_7008_AQUATIC_SPECIES` | मत्स्य पालन प्रयोजन हेतु प्रजाति. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | मत्स्य पालन उपकरण प्रकार. |
| `7010` | `AI_7010_PROD_METHOD` | उत्पादन विधि. |
| `7020` | `AI_7020_REFURB_LOT` | नवीनीकरण (रिफर्बिशमेंट) लॉट आईडी. |
| `7021` | `AI_7021_FUNC_STAT` | कार्यात्मक स्थिति. |
| `7022` | `AI_7022_REV_STAT` | संशोधन स्थिति. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | असेंबली का ग्लोबल इंडिविजुअल एसेट पहचानकर्ता (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | तीन-अंकीय ISO देश कोड सहित प्रसंस्करणकर्ता संख्या (10 स्थान)।. |
| `7040` | `AI_7040_UIC_EXT` | विस्तार 1 और आयातक सूचकांक सहित GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | यूएन/सेफैक्ट माल इकाई प्रकार. |

### राष्ट्रीय स्वास्थ्य प्रतिपूर्ति संख्याएँ (NHRN)

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - जर्मनी PZN. |
| `711` | `AI_711_NHRN_CIP` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - फ्रांस CIP. |
| `712` | `AI_712_NHRN_CN` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - स्पेन CN. |
| `713` | `AI_713_NHRN_DRN` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - ब्राज़ील DRN. |
| `714` | `AI_714_NHRN_AIM` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - पुर्तगाल AIM. |
| `715` | `AI_715_NHRN_NDC` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - संयुक्त राज्य अमेरिका NDC. |
| `716` | `AI_716_NHRN_AIC` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - इटली AIC. |
| `717` | `AI_717_NHRN_SRN` | राष्ट्रीय स्वास्थ्य देखभाल प्रतिपूर्ति संख्या (NHRN) - कोस्टा रिका सैनिटरी रजिस्टर नंबर. |

### स्वास्थ्य-सेवा, GMN, HIDRI, CPID और व्यक्ति-संबंधी डेटा

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | प्रमाणन संदर्भ (10 स्थान)।. |
| `7240` | `AI_7240_PROTOCOL` | प्रोटोकॉल आईडी. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | एआईडीसी मीडिया प्रकार. |
| `7242` | `AI_7242_VCN` | वर्जन कंट्रोल नंबर (VCN). |
| `7250` | `AI_7250_DOB` | जन्म तिथि (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | जन्म की तिथि और समय (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | जैविक लिंग. |
| `7253` | `AI_7253_FAMILY_NAME` | व्यक्ति का उपनाम (परिवार का नाम). |
| `7254` | `AI_7254_GIVEN_NAME` | व्यक्ति का प्रथम नाम (गिवन नेम). |
| `7255` | `AI_7255_SUFFIX` | व्यक्ति के नाम का प्रत्यय (सफिक्स). |
| `7256` | `AI_7256_FULL_NAME` | व्यक्ति का पूरा नाम. |
| `7257` | `AI_7257_PERSON_ADDR` | व्यक्ति का पता. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | शिशु जन्म क्रम. |
| `7259` | `AI_7259_BABY` | पारिवारिक उपनाम अनुसार शिशु क्रम. |
| `8001` | `AI_8001_DIMENSIONS` | रोल उत्पाद (चौड़ाई, लंबाई, कोर व्यास, दिशा, स्प्लाइस). |
| `8002` | `AI_8002_CMT_NO` | सेल्युलर मोबाइल टेलीफ़ोन पहचानकर्ता. |
| `8003` | `AI_8003_GRAI` | ग्लोबल रिटर्नेबल एसेट पहचानकर्ता (GRAI). |
| `8004` | `AI_8004_GIAI` | ग्लोबल इंडिविजुअल एसेट पहचानकर्ता (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | प्रति इकाई माप मूल्य. |
| `8006` | `AI_8006_ITIP` | एक व्यक्तिगत व्यापार वस्तु टुकड़े की पहचान (ITIP). |
| `8007` | `AI_8007_IBAN` | इंटरनेशनल बैंक अकाउंट नंबर (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | उत्पादन की तिथि और समय (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | ऑप्टिकली रीडेबल सेंसर इंडिकेटर. |
| `8010` | `AI_8010_CPID` | कंपोनेंट/पार्ट पहचानकर्ता (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | कंपोनेंट/पार्ट पहचानकर्ता क्रम संख्या (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | सॉफ्टवेयर संस्करण. |
| `8013` | `AI_8013_GMN` | ग्लोबल मॉडल नंबर (GMN). |
| `8014` | `AI_8014_MUDI` | अत्यधिक व्यक्तिगत डिवाइस पंजीकरण पहचानकर्ता (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | सेवाएं प्रदान करने वाले संगठन और सेवा प्रदाता के बीच संबंध की पहचान हेतु ग्लोबल सर्विस रिलेशन नंबर (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | सेवाएं प्रदान करने वाले संगठन और सेवा प्राप्तकर्ता के बीच संबंध की पहचान हेतु ग्लोबल सर्विस रिलेशन नंबर (GSRN). |
| `8019` | `AI_8019_SRIN` | सर्विस रिलेशन इंस्टेंस नंबर (SRIN). |
| `8020` | `AI_8020_REF_NO` | भुगतान पर्ची संदर्भ संख्या. |
| `8026` | `AI_8026_ITIP_CONTENT` | लॉजिस्टिक इकाई में निहित व्यापार वस्तु के टुकड़ों की पहचान (ITIP). |
| `8030` | `AI_8030_DIGSIG` | डिजिटल हस्ताक्षर (DigSig). |
| `8040` | `AI_8040_IMEI` | इंटरनेशनल मोबाइल इक्विपमेंट आइडेंटिटी (IMEI). |
| `8041` | `AI_8041_IMEI2` | इंटरनेशनल मोबाइल इक्विपमेंट आइडेंटिटी 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | एम्बेडेड सिम नंबर. |
| `8043` | `AI_8043_PSIM` | भौतिक सिम नंबर. |
| `8110` | `AI_8110` | उत्तरी अमेरिका में उपयोग हेतु कूपन कोड पहचान. |
| `8111` | `AI_8111_POINTS` | कूपन के लॉयल्टी पॉइंट्स. |
| `8112` | `AI_8112` | उत्तरी अमेरिका में उपयोग हेतु पॉज़िटिव ऑफर फाइल कूपन कोड पहचान. |
| `8200` | `AI_8200_PRODUCT_URL` | विस्तारित पैकेजिंग यूआरएल. |

### आंतरिक / कंपनी उपयोग

| AI | स्थिरांक | विवरण |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | व्यापारिक भागीदारों के बीच परस्पर सहमत जानकारी. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | कंपनी की आंतरिक सूचना (9 स्थान)।. |

---

## परिशिष्ट ब — व्याख्या कुंजी स्थिरांक

जब `GaiaParser.parse()` को `ParseMode.INTERPRETATION` के साथ बुलाया जाता है, तब हर `GS1AIObjectElement` `GS1AIInterpretation` वस्तुओं की एक सूची वहन कर सकता है, जिन्हें क्षेत्र-विशिष्ट समृद्धक बनाते हैं। विशिष्ट व्याख्या-मान खोजने के लिए `GS1Constants_Enricher` स्थिरांकों (पैकेज `tools.pantheum.gaia.gs1.constants`) को कुंजी के रूप में प्रयोग कीजिए:

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

प्रदर्शन-लेबल स्थिरांक **नहीं** हैं — वे `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` की स्थानीयकृत सूचियों में हैं, जिनकी कुंजी प्रकार-स्थिरांक है। `GS1AIInterpretation.getLabel()` पार्स भाषा में लेबल लौटाता है (देखें [स्थानीयकृत संदेश और लेबल](#सथनयकत-सदश-और-लबल)), और जब किसी सूची में वह कुंजी न हो तो अंग्रेज़ी पर लौट जाता है। नीचे का प्रदर्शन-लेबल स्तंभ हिन्दी पाठ सूचीबद्ध करता है; प्रकार-कुंजियाँ स्वयं सभी भाषाओं में अपरिवर्तित रहती हैं, इसलिए सदैव कुंजी पर मिलान कीजिए, लेबल पर कभी नहीं।

### तिथि और समय

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `DATE_VALUE` | तिथि | तिथि AI (11–17, 7003, 7006, 7011, आदि) |
| `DATE_FORMAT` | तिथि प्रारूप | तिथि AI |
| `TIME_VALUE` | समय | समय धारण करने वाले AI (7003, 7011, 8008, आदि) |
| `TIME_FORMAT` | समय प्रारूप | समय धारण करने वाले AI |
| `DATETIME_VALUE` | तिथि और समय | तिथि+समय AI |
| `DATETIME_FORMAT` | तिथि और समय प्रारूप | तिथि+समय AI |

### कटाई तिथि

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | फ़सल आरंभ तिथि | AI 7007 |
| `HARVEST_END_DATE` | फ़सल समाप्ति तिथि | AI 7007 (वैकल्पिक परास-अंत) |
| `HARVEST_DATE_RANGE` | फ़सल तिथि सीमा | AI 7007 |

### GS1 कंपनी उपसर्ग

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 कंपनी उपसर्ग | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 सदस्य कोड | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 सदस्य संगठन | GTIN / GLN / SSCC AI |

### GTIN

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN प्रकार | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | पैकेजिंग स्तर | AI 01 |
| `GTIN_CHECK_DIGIT` | जाँच अंक | AI 01, 02 |

### SSCC

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | विस्तार अंक | AI 00 |
| `SSCC_SERIAL_REFERENCE` | क्रम संदर्भ | AI 00 |
| `SSCC_CHECK_DIGIT` | जाँच अंक | AI 00 |

### देश (ISO 3166)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | देश कोड (संख्यात्मक) | एकल-देश AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | देश कोड (अल्फा-2) | अल्फ़ा-2 देश AI |
| `COUNTRY_NAME` | देश का नाम | एकल-देश AI |
| `COUNTRY_LIST` | देश | AI 423 — सभी नाम जुड़े हुए, जैसे `Australia, New Zealand` |

AI 423 (प्रारंभिक प्रसंस्करण का देश) पाँच देशों तक वहन कर सकता है, इसलिए यह
**हर देश के लिए एक क्रमांकित युग्म** उत्सर्जित करता है — `COUNTRY_CODE_NUMERIC_1`,
`COUNTRY_NAME_1`, `COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — और उनके बाद एक
`COUNTRY_LIST` सारांश। इन कुंजियों को `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` स्थिरांकों से 1 से आरंभ होने वाले क्रमांक जोड़कर बनाइए, या
सीधे `getInterpretations()` पर चलिए; बिना प्रत्यय वाली `COUNTRY_CODE_NUMERIC` /
`COUNTRY_NAME` कुंजियाँ AI 423 के लिए **उत्सर्जित नहीं** होतीं।

### मुद्रा (ISO 4217)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | मुद्रा कोड | मुद्रा सहित राशि AI (391n, 393n) |
| `CURRENCY_ALPHA` | मुद्रा वर्णमाला कोड | मुद्रा सहित राशि AI |
| `CURRENCY_NAME` | मुद्रा का नाम | मुद्रा सहित राशि AI |

### तापमान

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `TEMPERATURE` | तापमान | AI 4330–4333 |
| `TEMPERATURE_UNIT` | तापमान इकाई | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | तापमान (स्वरूपित) | AI 4330–4333 |

### लिंग (ISO 5218)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `SEX_CODE` | लिंग कोड | AI 7252 |
| `SEX_DESCRIPTION` | लिंग विवरण | AI 7252 |

### जलीय प्रजातियाँ (FAO)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `SPECIES_CODE` | प्रजाति कोड | AI 7008 |
| `SPECIES_SCIENTIFIC` | वैज्ञानिक नाम | AI 7008 |
| `SPECIES_ENGLISH` | सामान्य नाम | AI 7008 |
| `SPECIES_FAMILY` | कुल | AI 7008 |
| `SPECIES_ORDER` | गण | AI 7008 |

### NATO स्टॉक संख्या (NSN)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `NSN_FSG` | आपूर्ति समूह | AI 7001 |
| `NSN_FSG_NAME` | आपूर्ति समूह नाम | AI 7001 |
| `NSN_FSCG` | आपूर्ति वर्ग | AI 7001 |
| `NSN_FSCG_NAME` | आपूर्ति श्रेणी नाम | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | देश कोड | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | देश | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO देश कोड | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS श्रेणी | AI 7001 |
| `NSN_NIIN` | राष्ट्रीय मद संख्या | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### रोल उत्पाद

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | रोल चौड़ाई (mm) | AI 8001 |
| `ROLL_LENGTH` | रोल लंबाई (m) | AI 8001 |
| `CORE_DIAMETER` | कोर व्यास (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | लपेटन दिशा कोड | AI 8001 |
| `WINDING_DIRECTION` | लपेटन दिशा | AI 8001 |
| `SPLICES` | जोड़ | AI 8001 |

### IBAN

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | देश कोड | AI 8007 |
| `IBAN_COUNTRY_NAME` | देश | AI 8007 |
| `IBAN_CHECK_DIGITS` | जाँच अंक | AI 8007 |
| `IBAN_CHECK_VALID` | जाँच | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | क्रम संख्या | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | जाँच अंक | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | जारीकर्ता संस्था | AI 8040, 8041 |

पंद्रह अंक `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]` में विघटित होते हैं, और
RBI, TAC के पहले दो अंक हैं — इसलिए `IMEI_RBI` `IMEI_TAC` का उपसर्ग है, कोई अलग क्षेत्र
नहीं। `IMEI_FORMATTED` GSMA का मानक प्रदर्शन-समूहन `AA-BBBBBB-CCCCCC-D` दिखाता है (जैसे
`49-015420-323751-8`), जो TAC को RBI सीमा पर विभाजित करता है; पुराना `6-2-6-1` समूहन, जो
वहाँ विभाजित करता था जहाँ अब समाप्त कर दिया गया अंतिम-संयोजन कोड आरंभ होता था, उत्सर्जित
नहीं होता।

`IMEI_RBI_NAME` `ImeiRbiData` के माध्यम से RBI को आवंटन-निकाय के नाम में हल करता है, और यह
**अंत में तथा केवल तभी** जोड़ा जाता है जब कोड वहाँ सूचीबद्ध हो। वह तालिका तीन समूह समेटती
है:

- **वर्तमान में आवंटन करने वाले निकाय** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF,
  और साथ में `99` Global Hexadecimal Administrator तथा `98` (आरक्षित)।
- **परीक्षण परास** — `00` और `02`–`09`, जो वास्तविक आवंटन नहीं बल्कि परीक्षण IMEI का संकेत
  देते हैं। इनकी पूछताछ `ImeiRbiData.isTestCode(code)` से कीजिए।
- **अब आवंटन न करने वाले निकाय** — ऐतिहासिक निकाय जैसे `49` (BZT/BAPT, जर्मनी), `44`
  (BABT, यूनाइटेड किंगडम), और `91` (MSAI, भारत)। इनकी पूछताछ
  `ImeiRbiData.isNoLongerAllocating(code)` से कीजिए। ये कोड धारण करने वाले उपकरण सामान्य
  हैं और अब भी सेवा में हैं; केवल नया आवंटन रुका है, इसलिए यह सूचना प्रतिवेदित करने के लिए
  है, वैधता का संकेत कदापि नहीं।

`IMEI_RBI_NAME` की अनुपस्थिति का अर्थ है "यह RBI हमारी तालिका में नहीं है", **यह नहीं** कि
"IMEI अवैध है": तालिका सीधे GSMA से नहीं, बल्कि एक प्रकाशित RBI सूची से संकलित है, इसलिए
वह नव-नियुक्त निकायों से पीछे रह सकती है। उसकी अनुपस्थिति से कोई सत्यापन-निर्णय मत निकालिए;
RBI कोई जाँच वर्ण नहीं है। व्याख्या-सूची पर चलने वाला कोड उसकी अनुपस्थिति सहन करने योग्य
होना चाहिए, स्थान के आधार पर अनुक्रमणन करने वाला नहीं।

### SIM पहचानकर्ता (EID / ICCID)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | उद्योग श्रेणी | AI 8042 |
| `EID_BODY` | EID मुख्य भाग | AI 8042 |
| `EID_CHECK_DIGIT` | जाँच अंक | AI 8042 |
| `ICCID_BODY` | ICCID मुख्य भाग | AI 8043 |
| `ICCID_EXTENSION` | विस्तार | AI 8043 |

`SIM_MII` पहले **दो** अंक (`89`) धारण करता है — यही वह युग्म है जो ITU-T E.118 दूरसंचार को
आवंटित करता है। ISO/IEC 7812 स्वयं MII को **केवल पहला अंक** परिभाषित करता है, इसलिए
`SIM_MII_NAME` श्रेणी को `Iso7812Data` के माध्यम से आरंभिक `8` से हल करता है — जिससे मिलता
है "Healthcare, telecommunications and other future industry assignments"। अतः यह हर सुगठित
EID के लिए एक-सा रहता है; इसे मानक तक अनुरेखणीयता के लिए प्रतिवेदित किया जाता है, किसी
विभेदक के रूप में नहीं।
`Iso7812Data.nameForCode(digit)` एक अकेला अंक लेता है, जबकि
`nameForIdentifier(prefix)` लंबा उपसर्ग स्वीकार करता है और उसका पहला अंक पढ़ता है।

`SIM_MII_NAME` केवल `EidEnricher` (AI 8042) उत्सर्जित करता है। `IccidEnricher`
(AI 8043) `SIM_MII` तो दिखाता है, पर श्रेणी नहीं।

### प्रमाणन संदर्भ

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | अनुक्रम संख्या | AI 7230–7239 |
| `CERT_SCHEME_CODE` | प्रमाणन योजना कोड | AI 7230–7239 |
| `CERT_SCHEME_NAME` | प्रमाणन योजना | AI 7230–7239 |
| `CERT_REFERENCE` | प्रमाणन संदर्भ | AI 7230–7239 |

### GS1 UIC

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC कोड | AI 7040 |
| `UIC_EXTENSION_1` | विस्तार 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | आयातक सूचकांक | AI 7040 |

### शिशु जन्म-क्रम

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | जन्म स्थिति | AI 7258 |
| `BIRTH_TOTAL` | कुल जन्म | AI 7258 |
| `BIRTH_SEQUENCE` | जन्म क्रम | AI 7258 |

### ग्लोबल मॉडल नंबर (GMN)

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | मॉडल संदर्भ | AI 8013 |
| `GMN_CHECK_PAIR` | जाँच युग्म | AI 8013 |

### HIDRI

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | उपकरण संदर्भ | AI 8014 |
| `HIDRI_CHECK_PAIR` | जाँच युग्म | AI 8014 |

### CPID

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | घटक और पुर्जा संदर्भ | AI 8010–8011 |

### दशमलव और माप मान

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | दशमलव मान | निहित दशमलव स्थानों वाले संख्यात्मक AI (31xx–36xx) |
| `DECIMAL_AMOUNT` | राशि | मूल्य AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | प्रतिशत | AI 394n |
| `DECIMAL_PLACES` | दशमलव स्थान | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` के साथ |
| `PERCENTAGE_FORMAT` | प्रतिशत प्रारूप | AI 394n |
| `ISO_UNIT_CODE` | ISO इकाई कोड | माप AI |
| `ISO_UNIT_NAME` | ISO इकाई नाम | माप AI |
| `MONETARY_AMOUNT` | मौद्रिक राशि | मूल्य AI |
| `MONETARY_AMOUNT_DISPLAY` | मौद्रिक राशि (स्वरूपित) | मूल्य AI |

### भौगोलिक निर्देशांक

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `LATITUDE` | अक्षांश | AI 4309 |
| `LONGITUDE` | देशांतर | AI 4309 |
| `GEO_COORDINATES` | भौगोलिक निर्देशांक | AI 4309 |
| `LATITUDE_DMS` | अक्षांश (DMS) | AI 4309 |
| `LONGITUDE_DMS` | देशांतर (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | भौगोलिक निर्देशांक (DMS) | AI 4309 |

### उत्पादन विधि

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | उत्पादन विधि कोड | AI 7010 |
| `PRODUCTION_METHOD` | उत्पादन विधि | AI 7010 |

### AIC माध्यम प्रकार

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC मीडिया प्रकार कोड | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC मीडिया प्रकार | AI 7241 |

### कुल में से टुकड़ा

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | टुकड़ा संख्या | AI 8006 |
| `PIECE_TOTAL` | कुल टुकड़े | AI 8006 |
| `PIECE_OF_TOTAL` | कुल में से टुकड़ा | AI 8006 |

### घटक विभाजन

ये वे कुंजियाँ हैं जिन्हें Java में लिखा कोई समृद्धक नहीं, बल्कि `content/ai-content.json`
में दिए घोषणात्मक घटक-विभाजन उत्सर्जित करते हैं — ये सब किसी संयुक्त AI मान के नामित हिस्से
दिखाती हैं। इस परिशिष्ट की हर अन्य कुंजी के विपरीत, **इनके लिए `GS1Constants_Enricher` में
कोई स्थिरांक नहीं है**: स्ट्रिंग लिटरल पर मिलान कीजिए, या प्रकार
`GS1AIInterpretation.getType()` से पढ़िए।

| प्रकार कुंजी | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | जाँच अंक | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | क्रम संख्या | AI 253, 255, 8003 |
| `POSTAL_CODE` | डाक कोड | AI 421 |
| `PROCESSOR_ID` | प्रसंस्करणकर्ता पहचानकर्ता | AI 7030–7039 |

ध्यान दीजिए कि यहाँ `CHECK_DIGIT` सामान्य घटक-विभाजन कुंजी है, जो ऊपर दी गई समृद्धक-विशिष्ट
कुंजियों `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` और `EID_CHECK_DIGIT`
से भिन्न है।

### विविध

| कुंजी स्थिरांक | प्रदर्शन लेबल | किसने बनाया |
|--------------|---------------|-------------|
| `FLAG_VALUE` | मान | बूलियन / ध्वज AI (4321–4323) |
| `DECODED_TEXT` | डिकोड किया गया पाठ | मुक्त-पाठ AI |
