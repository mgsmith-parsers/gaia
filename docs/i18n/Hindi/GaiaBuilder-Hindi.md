# GaiaBuilder — डेवलपर गाइड

## विषय-सूची

1. [संक्षिप्त परिचय](#सकषपत-परचय)
2. [GS1 और General Specifications के बारे में](#gs1-और-general-specifications-क-बर-म)
3. [क्विक स्टार्ट](#कवक-सटरट)
4. [यह कैसे काम करता है](#यह-कस-कम-करत-ह)
5. [एलिमेंट स्ट्रिंग बनाना](#एलमट-सटरग-बनन)
   - [विशेषता-AI को अपनी पहचान-कुंजी चाहिए](#वशषत-ai-क-अपन-पहचन-कज-चहए)
6. [Digital Link URI बनाना](#digital-link-uri-बनन)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [सत्यापन और त्रुटियाँ](#सतयपन-और-तरटय)
   - [अपवाद फेंकने वाली निर्माण-विधियाँ](#अपवद-फकन-वल-नरमण-वधय)
   - [अपवाद न फेंकने वाली tryBuild\* विधियाँ](#अपवद-न-फकन-वल-trybuild-वधय)
   - [त्रुटि-संदेशों की भाषा](#तरट-सदश-क-भष)
   - [BuildResult](#buildresult)
9. [जाँच अंक](#जच-अक)
10. [थ्रेड सुरक्षा](#थरड-सरकष)
11. [API संदर्भ](#api-सदरभ)

---

## संक्षिप्त परिचय

`GaiaBuilder` [`GaiaParser`](GaiaParser-Hindi.md) का उल्टा समकक्ष है: यह एप्लिकेशन आइडेंटिफ़ायर (AI) और मान के युग्मों के संग्रह को एक सुगठित GS1 **एलिमेंट स्ट्रिंग** या **GS1 Digital Link URI** में बदल देता है। आप AI और उनके पूरे डेटा मान देते हैं; बिल्डर उन्हें जोड़ता है, `GaiaParser` वाले उसी इंजन से परिणाम सत्यापित करता है, और फिर आउटपुट प्रस्तुत करता है।

चूँकि बिल्डर *अपने ही प्रस्तावित आउटपुट को पार्स करके* सत्यापन करता है, इसलिए जो कुछ भी वह लौटाता है उसका `GaiaParser` से साफ़-साफ़ पार्स होना सुनिश्चित है — दोनों इस बात पर कभी असहमत नहीं हो सकते कि सुगठित क्या है।

**प्रवेश-बिंदु क्लास:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 और General Specifications के बारे में

**GS1** एक वैश्विक गैर-लाभकारी संस्था है जो आपूर्ति-शृंखला की पहचान और डेटा-विनिमय के लिए खुले मानक विकसित करती और उनका रखरखाव करती है। इसके मानक खुदरा, स्वास्थ्य-सेवा, लॉजिस्टिक्स, खाद्य-सेवा और कई अन्य उद्योगों में प्रयुक्त होते हैं, और उपभोक्ता पैकेजिंग पर लगे उत्पाद बारकोड से लेकर दवा की खुराकों की क्रम-संख्या आधारित ट्रैकिंग तक सब कुछ समेटते हैं।

यह बिल्डर जो कुछ भी लागू करता है, उसका प्रामाणिक संदर्भ **GS1 General Specifications** है — एक ही दस्तावेज़ जो निम्न को परिभाषित करता है:

- सभी एप्लिकेशन आइडेंटिफ़ायर (AI) कोड, उनके डेटा शीर्षक, प्रारूप और सत्यापन नियम
- AI एलिमेंट स्ट्रिंग बनाने और एन्कोड करने के वाक्य-रचना नियम
- बारकोड सिम्बोलॉजी की आवश्यकताएँ और AIM कोड ID के आवंटन
- जाँच अंक और जाँच वर्ण के एल्गोरिदम
- दो-अंकीय वर्ष का निर्धारण (खिसकती-खिड़की नियम)
- Data Matrix, QR Code, GS1-128, GS1 DataBar और अन्य कैरियर विनिर्देश

GS1 General Specifications हर साल अद्यतन होते हैं। वर्तमान संस्करण और सहायक सामग्री यहाँ उपलब्ध है:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications का **रिलीज़ 26.0 (अनुमोदित, जन 2026)** लागू करता है।

GS1 Digital Link URI एक सहयोगी मानक, **GS1 Digital Link: URI Syntax**, द्वारा शासित हैं, जो प्राथमिक पहचान कुंजियाँ, कुंजी-योग्यकों का क्रम, और डेटा-विशेषताओं की एन्कोडिंग परिभाषित करता है — Digital Link URI प्रस्तुत करते समय बिल्डर इन्हीं को लागू करता है:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax मानक का **रिलीज़ 1.7.0 (अनुमोदित, अग 2026)** लागू करता है।

इस दस्तावेज़ में अनुभागों के संदर्भ GS1 General Specifications की ओर संकेत करते हैं (जैसे "Table 7-5", "section 7.12"), केवल Digital Link के अनुभाग-क्रमांक (जैसे "§4.9", "§4.12") को छोड़कर, जो GS1 Digital Link: URI Syntax मानक की ओर संकेत करते हैं।

---

## क्विक स्टार्ट

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

कच्चे AI लिटरल के बजाय `GS1Constants_AICodes` स्थिरांकों को प्राथमिकता दीजिए (देखें [पार्सर गाइड का परिशिष्ट अ](GaiaParser-Hindi.md#परशषट-अ--ai-सटरग-सथरक)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## यह कैसे काम करता है

हर निर्माण एक ही मार्ग से गुज़रता है:

1. **संयोजन** — AI/मान युग्म जुड़कर एक प्रस्तावित एलिमेंट स्ट्रिंग बनाते हैं। FNC1 समूह विभाजक (`0x1D`) हर उस AI के बाद डाला जाता है जिसे *विभाजक चाहिए* और जो अंतिम एलिमेंट न हो। जिन AI की लंबाई पहले से निर्धारित है (GTIN, तिथियाँ, नियत-लंबाई माप) उन्हें विभाजक नहीं मिलता; बाक़ी को मिलता है। (अज्ञात AI इस पग तक पहुँचते ही नहीं — `ai(...)` उन्हें तुरंत अस्वीकार कर देता है; देखें [एलिमेंट स्ट्रिंग बनाना](#एलमट-सटरग-बनन)।)
2. **सत्यापन** — प्रस्ताव को `GaiaParser` द्वारा `CONTENT` मोड में पार्स किया जाता है। हर मान को उसके AI के प्रारूप और जाँच अंक के विरुद्ध जाँचा जाता है, और संरचनात्मक नियम (अनिवार्य और वर्जित AI युग्म) लागू किए जाते हैं। यदि पार्स वैध न हो, तो निर्माण विफल हो जाता है।
3. **प्रस्तुति** —
   - एलिमेंट स्ट्रिंग के लिए, सत्यापित ऑब्जेक्ट का `toElementString()` लौटाया जाता है।
   - Digital Link के लिए, हर एलिमेंट को उसकी DL भूमिका दी जाती है (प्राथमिक कुंजी, कुंजी-योग्यक, या डेटा-विशेषता), कुंजी-योग्यक अनुक्रम सत्यापित होता है, URI उत्सर्जित होता है, और फिर **उत्सर्जित URI को फिर से पार्स करके यह सुनिश्चित किया जाता है कि वह वैध Digital Link के रूप में वापस लौटता है** — यह स्ट्रिंग-संयोजन और प्रतिशत-एन्कोडिंग पग के लिए एक सुरक्षा-जाँच है। यदि वह वापस न लौटे, तो `GaiaBuilderException` फेंका जाता है।

यह `DLSyntaxParser` के पुनर्निर्माण तर्क का ही प्रतिबिंब है, जिससे विभाजकों का स्थान और सत्यापन ठीक वही रहते हैं जिनकी पार्सर अपेक्षा करता है।

---

## एलिमेंट स्ट्रिंग बनाना

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** तुरंत सत्यापित होता है: यदि वह कोई ज्ञात GS1 एप्लिकेशन आइडेंटिफ़ायर न हो तो `ai(...)` `IllegalArgumentException` फेंकता है। (बिल्डर पार्सिंग से पहले AI को मान के साथ जोड़ देता है, इसलिए `"99999"` जैसे अज्ञात या अत्यधिक लंबे AI को यहीं पकड़ना ज़रूरी है — अन्यथा उसे चुपचाप किसी भिन्न AI में पुनः टोकनीकृत कर दिया जाएगा।) **मान** का सत्यापन बाद में, निर्माण के समय होता है।
- मान **पूर्ण** होने चाहिए, जाँच अंक सहित। बिल्डर आपके लिए जाँच अंक न तो गणना करता है, न जोड़ता है — देखें [जाँच अंक](#जच-अक)।
- AI उसी क्रम में उत्सर्जित होते हैं जिसमें आप उन्हें जोड़ते हैं। जहाँ GS1 की संरचना अपेक्षा करती है वहाँ बिल्डर FNC1 विभाजक डाल देता है; उन्हें स्वयं मत जोड़िए।
- **बिना किसी AI के** निर्माण `GaiaBuilderException("No AIs supplied")` फेंकता है और `getErrors()` रिक्त रहता है — यही एकमात्र विफलता है जो कोई `GaiaError` नहीं वहन करती।
- जिस AI का मान उसके प्रारूप या जाँच अंक के नियम पर विफल हो, वह निर्माण को विफल कर देता है।

### विशेषता-AI को अपनी पहचान-कुंजी चाहिए

अधिकांश AI *विशेषताएँ* हैं जिनके साथ GS1 General Specifications किसी पहचान-कुंजी का होना अनिवार्य करते हैं, और बिल्डर इसे लागू करता है — वह पूरे वाक्य-रचना चरण से सत्यापन करता है, जिसका कोई विकल्प नहीं है। अकेला बैच/लॉट या क्रम संख्या वैध एलिमेंट स्ट्रिंग **नहीं** है:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

पहचान-कुंजियाँ (GTIN `01`, SSCC `00`, GLN `414`, …) और कंपनी-आंतरिक AI (`90`–`99`) पूर्णतः वैध रूप से अकेले खड़े हो सकते हैं। बाक़ी सबको अपना सहचर चाहिए।

> `GaiaParser` को `ParseConfig.skipRequiresCheck(true)` से यह जाँच छोड़ने को कहा जा सकता है; `GaiaBuilder` जान-बूझकर इसका कोई समकक्ष नहीं देता — उसका उद्देश्य मानक-अनुरूप आउटपुट देना है। जान-बूझकर अधूरी एलिमेंट स्ट्रिंग जोड़नी हो तो उसे स्वयं जोड़िए और जाँच बंद करके पार्स कीजिए।

---

## Digital Link URI बनाना

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

वैध Digital Link के लिए ठीक एक **प्राथमिक पहचान कुंजी** आवश्यक है (जैसे GTIN `01`, GLN `414`, SSCC `00`)। बिल्डर आपके दिए हर AI को वर्गीकृत करता है:

| भूमिका | कैसे प्रस्तुत होता है | उदाहरण |
|------|-------------|---------|
| प्राथमिक पहचान कुंजी | डोमेन/उपसर्ग के बाद पथ-खंड | `/01/09506000134352` |
| कुंजी-योग्यक (CPV `22`, बैच `10`, क्रम संख्या `21`, …) | उसके बाद के पथ-खंड, **§4.9 के विहित क्रम में** (आपके जोड़ने के क्रम में नहीं) | `/10/LOT-ABC` |
| डेटा-विशेषता (बाक़ी सब) | क्वेरी पैरामीटर, **AI कुंजी से कोशानुक्रम में क्रमित** (§4.12) | `?17=271231` |

चूँकि योग्यक प्रस्तुति के समय पुनः क्रमित हो जाते हैं, इसलिए उन्हें क्रम से बाहर देने में कोई हानि नहीं — `ai("10", …)` से पहले `ai("21", …)` देने पर भी प्रस्तुति `/10/LOT/21/SER` ही होगी। प्राथमिक कुंजी को केवल उनका *समुच्चय* स्वीकार्य होना चाहिए।

पथ और क्वेरी, दोनों में मान प्रतिशत-एन्कोडेड होते हैं।

निर्माण तब विफल होता है (`GaiaBuilderException` फेंकता है, या विफल `BuildResult` लौटाता है) जब:

- AI में कोई प्राथमिक पहचान कुंजी **नहीं** है;
- **एक से अधिक** प्राथमिक पहचान कुंजियाँ हैं;
- कोई AI Digital Link में **वर्जित** है (`03`, `8014`);
- चुनी गई प्राथमिक कुंजी के लिए **कुंजी-योग्यक अनुक्रम** अवैध है (कोई ऐसा योग्यक जो उस कुंजी के साथ नहीं आता, या योग्यक अपने अनुमत क्रम से बाहर)।

---

## BuilderDigitalLinkConfig

स्कीम, डोमेन, पथ-उपसर्ग, अतिरिक्त क्वेरी पैरामीटर और फ़्रैगमेंट नियंत्रित करने के लिए `BuilderDigitalLinkConfig` दीजिए:

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

| बिल्डर विधि | प्रयोजन | डिफ़ॉल्ट |
|----------------|---------|---------|
| `scheme(String)` | URI स्कीम; `http` या `https` होनी चाहिए | `https` |
| `domain(String)` | रिज़ॉल्वर प्राधिकार — होस्ट या `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | पहली प्राथमिक कुंजी से पहले के पथ-खंड; दोनों सिरों की तिरछी रेखाएँ सामान्यीकृत होती हैं | *(कोई नहीं)* |
| `baseUrl(String)` | सुविधा जो किसी URL को `scheme` + `domain` + `pathPrefix` में विभाजित कर देती है | — |
| `addQueryParam(String, String)` | अतिरिक्त क्वेरी पैरामीटर, AI डेटा-विशेषताओं के **बाद** जोड़ा जाता है, प्रविष्टि-क्रम में; प्रतिशत-एन्कोडेड | — |
| `fragment(String)` | URI फ़्रैगमेंट (आगे `#` के बिना); प्रतिशत-एन्कोडेड | *(कोई नहीं)* |

`build()` विन्यास को तुरंत सत्यापित करता है: `http(s)` से भिन्न स्कीम या रिक्त डोमेन `IllegalArgumentException` फेंकता है।

- `BuilderDigitalLinkConfig.canonical()` (उपनाम `defaultConfig()`) बिना किसी अतिरिक्त के डिफ़ॉल्ट `https://id.gs1.org` है — ठीक वही जो बिना तर्क वाला `buildDigitalLinkUri()` प्रयोग करता है, और वही जो `GS1AIObject.getCanonicalDigitalLink()` बनाता है।
- `baseUrl("http://id.example.org:8080/r")` → स्कीम `http`, डोमेन `id.example.org:8080`, पथ-उपसर्ग `/r`।
- अतिरिक्त क्वेरी पैरामीटर सदैव AI से व्युत्पन्न विशेषताओं के बाद आते हैं, जिससे विहित AI क्रम (§4.12) सुरक्षित रहता है।

`BuilderDigitalLinkConfig` अपरिवर्तनीय है; एक ही उदाहरण का निःसंकोच पुनःप्रयोग कीजिए।

---

## सत्यापन और त्रुटियाँ

### अपवाद फेंकने वाली निर्माण-विधियाँ

`buildElementString()`, `buildDigitalLinkUri()` और `buildDigitalLinkUri(BuilderDigitalLinkConfig)` तब **`GaiaBuilderException`** (एक अनियंत्रित `RuntimeException`) फेंकती हैं जब दिए गए AI कोई सुगठित आउटपुट नहीं बना सकते:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **सामग्री** विफलताओं (ग़लत जाँच अंक, प्रारूप-असंगति, अनुपस्थित/वर्जित AI) के लिए `getErrors()` पार्सर की `GaiaError` वस्तुएँ वहन करता है — वही वस्तुएँ जो [पार्सर गाइड में प्रलेखित](GaiaParser-Hindi.md#gaiaerror) हैं।
- **Digital Link संरचना** विफलताओं (प्राथमिक कुंजी अनुपस्थित, एक से अधिक प्राथमिक कुंजियाँ, वर्जित AI, अवैध कुंजी-योग्यक अनुक्रम) के लिए `getErrors()` बिल्डर की भाषा में स्थानीयकृत एक ही `GaiaError` वहन करता है (कोड `GE-L008`, `GE-L012`, `GE-L013` या `GE-L014`)।

### अपवाद न फेंकने वाली tryBuild\* विधियाँ

जब इनपुट उपयोगकर्ता से आता हो और विफलता एक अपेक्षित, सँभालने योग्य परिणाम हो, तब अपवादों से नियंत्रण-प्रवाह चलाने के बजाय `tryBuild*` रूप प्रयोग कीजिए। ये फेंकने के बजाय [`BuildResult`](#buildresult) लौटाते हैं:

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

| फेंकने वाली | न फेंकने वाली |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

हर `tryBuild*` विधि अपने फेंकने वाले जुड़वाँ के साथ वही सत्यापन-केंद्र साझा करती है; केवल विफलता की सीमा भिन्न है।

### त्रुटि-संदेशों की भाषा

सामग्री-सत्यापन की त्रुटियाँ स्थानीयकृत त्रुटि-सूची से ली जाती हैं। `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` द्वारा वहन किए गए `GaiaError` संदेशों की भाषा चुनने के लिए `language(...)` बुलाइए; डिफ़ॉल्ट अंग्रेज़ी है:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

यह वही `GaiaConstants.Language` सेटिंग है जिसे `GaiaParser` `ParseConfig` के माध्यम से स्वीकार करता है, इसलिए बिल्डर और पार्सर एक ही तरह स्थानीयकरण करते हैं।

**सामग्री** और **Digital Link संरचना** (प्राथमिक कुंजी अनुपस्थित, एक से अधिक प्राथमिक कुंजियाँ, वर्जित AI, अवैध कुंजी-योग्यक अनुक्रम), दोनों की `GaiaError` संदेश साझा त्रुटि-सूची से स्थानीयकृत होती हैं — बाद वाली कोड `GE-L008`, `GE-L012`, `GE-L013` और `GE-L014` प्रयोग करती है।

### BuildResult

`BuildResult` (पैकेज `tools.pantheum.gaia.result`) एक अपरिवर्तनीय मान-प्रकार है जो किसी `tryBuild*` कॉल का परिणाम बताता है:

| विधि | सफलता पर | विफलता पर |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | प्रस्तुत की गई स्ट्रिंग | `null` |
| `getMessage()` | `null` | विफलता का विवरण |
| `getErrors()` | रिक्त सूची | सत्यापन त्रुटियाँ (वही जो `GaiaBuilderException.getErrors()` पर) |

---

## जाँच अंक

बिल्डर जाँच अंकों का सत्यापन करता है पर उनकी गणना **नहीं** करता — आपके मानों में जाँच अंक पहले से होना चाहिए। किसी की गणना करने के लिए `GS1Utils.calculateCheckDigit` प्रयोग कीजिए:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` दिए गए अंकों पर मानक GS1 मॉड्यूलो-10 एल्गोरिदम लागू करता है और `0`–`9` का जाँच अंक लौटाता है, या इनपुट के `null`, रिक्त या ग़ैर-संख्यात्मक होने पर `-1`।

---

## थ्रेड सुरक्षा

`GaiaBuilder` थ्रेड-सुरक्षित **नहीं** है और एक बार के प्रयोग के लिए बना है: `create()` बुलाइए, AI जोड़िए, एक बार निर्माण कीजिए। हर आउटपुट के लिए नया बिल्डर बनाइए; एक ही बिल्डर को कई थ्रेडों में साझा मत कीजिए।

`BuilderDigitalLinkConfig` (और उसके `BuildResult` आउटपुट) अपरिवर्तनीय हैं और निःसंकोच साझा किए जा सकते हैं — आरंभ में एक विन्यास बनाइए और अनेक बिल्डरों में उसका पुनःप्रयोग कीजिए।

---

## API संदर्भ

### `GaiaBuilder`

| विधि | विवरण |
|--------|-------------|
| `static GaiaBuilder create()` | नया, रिक्त बिल्डर आरंभ करता है। |
| `GaiaBuilder ai(String ai, String value)` | एक AI और उसका पूरा मान जोड़ता है। यदि इनमें से कोई `null` हो, या `ai` कोई ज्ञात GS1 एप्लिकेशन आइडेंटिफ़ायर न हो, तो `IllegalArgumentException` फेंकता है। |
| `GaiaBuilder language(GaiaConstants.Language language)` | सामग्री-सत्यापन त्रुटि-संदेशों की भाषा निर्धारित करता है (डिफ़ॉल्ट अंग्रेज़ी)। `null` उपेक्षित रहता है। |
| `String buildElementString()` | GS1 एलिमेंट स्ट्रिंग प्रस्तुत करता है। विफलता पर `GaiaBuilderException` फेंकता है। |
| `String buildDigitalLinkUri()` | विहित Digital Link URI प्रस्तुत करता है। विफलता पर `GaiaBuilderException` फेंकता है। |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` के अनुसार Digital Link URI प्रस्तुत करता है। विफलता पर `GaiaBuilderException` फेंकता है। |
| `BuildResult tryBuildElementString()` | अपवाद न फेंकने वाला एलिमेंट स्ट्रिंग निर्माण। |
| `BuildResult tryBuildDigitalLinkUri()` | अपवाद न फेंकने वाला विहित Digital Link निर्माण। |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` के अनुसार अपवाद न फेंकने वाला Digital Link निर्माण। |

### `BuilderDigitalLinkConfig`

| सदस्य | विवरण |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | डिफ़ॉल्ट `https://id.gs1.org`। |
| `static Builder builder()` | नया विन्यास बिल्डर। |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | हल की गई स्कीम, रिज़ॉल्वर प्राधिकार और पथ-उपसर्ग। |
| `getExtraQueryParams()` | अतिरिक्त क्वेरी पैरामीटर, प्रविष्टि-क्रम में। |
| `getFragment()` | फ़्रैगमेंट, या `null`। |

### `GaiaBuilderException`

| सदस्य | विवरण |
|--------|-------------|
| `getErrors()` | विफलता उत्पन्न करने वाली `GaiaError` वस्तुएँ — सामग्री विफलता के लिए पार्सर त्रुटियाँ, या एक अकेली Digital Link संरचनात्मक त्रुटि (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`)। कभी `null` नहीं। |

### `BuildResult`

| सदस्य | विवरण |
|--------|-------------|
| `isSuccess()` | निर्माण सफल हुआ या नहीं। |
| `getValue()` | सफलता पर प्रस्तुत आउटपुट; विफलता पर `null`। |
| `getMessage()` | विफलता पर विफलता-विवरण; सफलता पर `null`। |
| `getErrors()` | विफलता पर सत्यापन त्रुटियाँ; सफलता पर रिक्त। कभी `null` नहीं। |
| `getTiming()` | निर्माण-संक्रिया का `ProcessingTiming` (आरंभ समय, प्रसंस्करण अवधि), या `null`। |

---

यह भी देखें: पार्सिंग पक्ष, AI एलिमेंट मॉडल, त्रुटि संदर्भ, तथा AI और व्याख्या स्थिरांकों के परिशिष्टों के लिए **[GaiaParser — डेवलपर गाइड](GaiaParser-Hindi.md)**।
