# GaiaBuilder — विकसक मार्गदर्शक

## अनुक्रमणिका

1. [थोडक्यात ओळख](#थडकयत-ओळख)
2. [GS1 आणि General Specifications विषयी](#gs1-आण-general-specifications-वषय)
3. [झटपट सुरुवात](#झटपट-सरवत)
4. [हे कसे चालते](#ह-कस-चलत)
5. [घटक स्ट्रिंग तयार करणे](#घटक-सटरग-तयर-करण)
   - [गुणधर्म-AI ला त्यांची ओळख-किल्ली लागते](#गणधरम-ai-ल-तयच-ओळख-कलल-लगत)
6. [Digital Link URI तयार करणे](#digital-link-uri-तयर-करण)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [पडताळणी आणि चुका](#पडतळण-आण-चक)
   - [अपवाद फेकणाऱ्या बांधणी-पद्धती](#अपवद-फकणऱय-बधण-पदधत)
   - [अपवाद न फेकणाऱ्या tryBuild\* पद्धती](#अपवद-न-फकणऱय-trybuild-पदधत)
   - [चूक-संदेशांची भाषा](#चक-सदशच-भष)
   - [BuildResult](#buildresult)
9. [तपासणी अंक](#तपसण-अक)
10. [थ्रेड सुरक्षा](#थरड-सरकष)
11. [API संदर्भ](#api-सदरभ)

---

## थोडक्यात ओळख

`GaiaBuilder` हा [`GaiaParser`](GaiaParser-Marathi.md) चा उलट दिशेचा जोडीदार आहे: तो अ‍ॅप्लिकेशन आयडेंटिफायर (AI) आणि मूल्य यांच्या जोड्यांच्या संग्रहाचे रूपांतर सुव्यवस्थित GS1 **घटक स्ट्रिंग** मध्ये किंवा **GS1 Digital Link URI** मध्ये करतो. तुम्ही AI आणि त्यांची संपूर्ण माहिती-मूल्ये देता; बिल्डर ती जोडतो, `GaiaParser` वापरत असलेल्या त्याच इंजिनने निकालाची पडताळणी करतो, आणि मग आउटपुट सादर करतो.

बिल्डर *स्वतःच्याच संभाव्य आउटपुटचे पार्सिंग करून* पडताळणी करत असल्याने, तो जे काही परत करतो ते `GaiaParser` मधून निर्दोषपणे पार्स होईल याची खात्री असते — सुव्यवस्थित काय आहे यावर या दोघांचे कधीच मतभेद होऊ शकत नाहीत.

**प्रवेशबिंदू वर्ग:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 आणि General Specifications विषयी

**GS1** ही एक जागतिक ना-नफा संस्था आहे, जी पुरवठा-साखळीतील ओळख आणि माहितीच्या देवाणघेवाणीसाठी खुली मानके विकसित करते व त्यांची देखभाल करते. तिची मानके किरकोळ विक्री, आरोग्यसेवा, वाहतूक-व्यवस्थापन, अन्नसेवा आणि इतर अनेक उद्योगांत वापरली जातात; ग्राहक पॅकेजिंगवरील उत्पादन बारकोडपासून ते औषधांच्या मात्रांच्या अनुक्रमांकाधारित मागोव्यापर्यंत सर्व काही त्यात येते.

हा बिल्डर जे काही अंमलात आणतो त्याचा अधिकृत संदर्भ म्हणजे **GS1 General Specifications** — एकच दस्तऐवज, जो पुढील गोष्टी निश्चित करतो:

- सर्व अ‍ॅप्लिकेशन आयडेंटिफायर (AI) कोड, त्यांची माहिती-शीर्षके, स्वरूपे आणि पडताळणीचे नियम
- AI घटक स्ट्रिंग रचण्याचे आणि एन्कोड करण्याचे वाक्यरचना-नियम
- बारकोड सिम्बॉलॉजीच्या आवश्यकता आणि AIM कोड आयडीचे वाटप
- तपासणी अंक व तपासणी अक्षराचे अल्गोरिदम
- दोन-अंकी वर्षाचे निर्धारण (सरकत्या खिडकीचा नियम)
- Data Matrix, QR Code, GS1-128, GS1 DataBar आणि इतर वाहकांची तपशीलवार मांडणी

GS1 General Specifications दरवर्षी अद्ययावत होतात. सध्याची आवृत्ती आणि पूरक साहित्य येथे उपलब्ध आहे:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA हे GS1 General Specifications ची **आवृत्ती २६.० (मंजूर, जानेवारी २०२६)** अंमलात आणते.

GS1 Digital Link URI हे **GS1 Digital Link: URI Syntax** या सोबतीच्या मानकाच्या अधीन आहेत. तेच प्राथमिक ओळख-किल्ल्या, किल्ली-विशेषकांचा क्रम, आणि माहिती-गुणधर्मांचे एन्कोडिंग निश्चित करते — बिल्डर Digital Link URI सादर करताना हेच लागू करतो:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA हे GS1 Digital Link: URI Syntax मानकाची **आवृत्ती १.७.० (मंजूर, ऑगस्ट २०२६)** अंमलात आणते.

या दस्तऐवजातील विभागांचे संदर्भ GS1 General Specifications कडे निर्देश करतात (उदा. "Table 7-5", "section 7.12"), फक्त Digital Link चे विभाग-क्रमांक (उदा. "§4.9", "§4.12") याला अपवाद आहेत; ते GS1 Digital Link: URI Syntax मानकाकडे निर्देश करतात.

---

## झटपट सुरुवात

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

कच्च्या AI लिटरलऐवजी `GS1Constants_AICodes` स्थिरांक वापरा (पाहा [पार्सर मार्गदर्शकातील परिशिष्ट अ](GaiaParser-Marathi.md#परशषट-अ--ai-सटरग-सथरक)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## हे कसे चालते

प्रत्येक बांधणी एकाच मार्गाने जाते:

१. **जोडणी** — AI/मूल्य जोड्या जोडून एक संभाव्य घटक स्ट्रिंग तयार होते. ज्या प्रत्येक AI ला *विभाजक लागतो* आणि जो शेवटचा घटक नाही, त्याच्यानंतर FNC1 गट-विभाजक (`0x1D`) घातला जातो. ज्यांची लांबी आधीच ठरलेली आहे असे AI (GTIN, दिनांक, निश्चित-लांबीची मापे) विभाजक घेत नाहीत; बाकीचे घेतात. (अनोळखी AI या पायरीपर्यंत पोहोचतच नाहीत — `ai(...)` त्यांना तत्काळ नाकारतो; पाहा [घटक स्ट्रिंग तयार करणे](#घटक-सटरग-तयर-करण).)
२. **पडताळणी** — संभाव्य स्ट्रिंग `GaiaParser` कडून `CONTENT` पद्धतीत पार्स केली जाते. प्रत्येक मूल्य त्याच्या AI च्या स्वरूपाशी आणि तपासणी अंकाशी तपासले जाते, आणि संरचनात्मक नियम (आवश्यक व वर्जित AI जोड्या) लागू केले जातात. पार्सिंग वैध नसेल, तर बांधणी अपयशी ठरते.
३. **सादरीकरण** —
   - घटक स्ट्रिंगसाठी, पडताळलेल्या ऑब्जेक्टचे `toElementString()` परत केले जाते.
   - Digital Link साठी, प्रत्येक घटकाला त्याची DL भूमिका नेमली जाते (प्राथमिक किल्ली, किल्ली-विशेषक, किंवा माहिती-गुणधर्म), किल्ली-विशेषकांचा क्रम पडताळला जातो, URI तयार होतो, आणि मग **तयार झालेला URI पुन्हा पार्स करून तो वैध Digital Link म्हणून परत येतो ना याची खात्री केली जाते** — ही स्ट्रिंग-जोडणी आणि टक्केवारी-एन्कोडिंगच्या पायरीसाठीची सुरक्षा-तपासणी आहे. तो परत आला नाही, तर `GaiaBuilderException` फेकला जातो.

हे `DLSyntaxParser` मधील पुनर्रचनेच्या तर्काचेच प्रतिबिंब आहे, त्यामुळे विभाजकाची जागा आणि पडताळणी अगदी पार्सरच्या अपेक्षेप्रमाणेच राहतात.

---

## घटक स्ट्रिंग तयार करणे

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** ची पडताळणी तत्काळ होते: तो ओळखीचा GS1 अ‍ॅप्लिकेशन आयडेंटिफायर नसेल तर `ai(...)` `IllegalArgumentException` फेकतो. (बिल्डर पार्सिंगआधीच AI मूल्याशी जोडतो, त्यामुळे `"99999"` सारखा अनोळखी किंवा अतिलांब AI इथेच पकडला जाणे गरजेचे आहे — नाहीतर तो गुपचूप वेगळ्याच AI मध्ये पुन्हा टोकनीकृत झाला असता.) **मूल्याची** पडताळणी मात्र नंतर, बांधणीच्या वेळी होते.
- मूल्ये **संपूर्ण** असावी लागतात, तपासणी अंकासह. बिल्डर तुमच्यासाठी तपासणी अंक ना मोजतो ना जोडतो — पाहा [तपासणी अंक](#तपसण-अक).
- AI तुम्ही जोडलेल्या क्रमानेच बाहेर येतात. GS1 च्या रचनेला जिथे आवश्यक असेल तिथे बिल्डर स्वतः FNC1 विभाजक घालतो; ते तुम्ही जोडू नका.
- **एकही AI न देता** बांधणी केल्यास रिकाम्या `getErrors()` यादीसह `GaiaBuilderException("No AIs supplied")` फेकला जातो — कोणताही `GaiaError` न वाहणारे हे एकमेव अपयश.
- ज्या AI चे मूल्य त्याच्या स्वरूप-नियमात किंवा तपासणी अंकात अपयशी ठरते, तो संपूर्ण बांधणीच अपयशी करतो.

### गुणधर्म-AI ला त्यांची ओळख-किल्ली लागते

बहुतेक AI हे *गुणधर्म* आहेत, ज्यांच्यासोबत ओळख-किल्ली असावी अशी GS1 General Specifications ची मागणी आहे, आणि बिल्डर ती लागू करतो — तो संपूर्ण वाक्यरचना टप्प्यातून पडताळणी करतो, आणि त्यातून सुटका नाही. एकटा बॅच/लॉट किंवा अनुक्रमांक ही वैध घटक स्ट्रिंग **नाही**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

ओळख-किल्ल्या (GTIN `01`, SSCC `00`, GLN `414`, …) आणि कंपनी-अंतर्गत AI (`90`–`99`) पूर्णपणे रास्तपणे एकटे उभे राहू शकतात. बाकी सर्वांना सोबती लागतो.

> `GaiaParser` ला ही तपासणी वगळायला `ParseConfig.skipRequiresCheck(true)` ने सांगता येते; पण `GaiaBuilder` मुद्दामच तसे काही देत नाही — मानकाशी सुसंगत आउटपुट देणे हाच त्याचा हेतू आहे. मुद्दाम अपूर्ण ठेवलेली घटक स्ट्रिंग जोडायची असेल, तर ती स्वतः जोडा आणि तपासणी बंद ठेवून पार्स करा.

---

## Digital Link URI तयार करणे

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

वैध Digital Link ला नेमकी एकच **प्राथमिक ओळख-किल्ली** लागते (उदा. GTIN `01`, GLN `414`, SSCC `00`). तुम्ही दिलेल्या प्रत्येक AI चे बिल्डर वर्गीकरण करतो:

| भूमिका | कशी सादर होते | उदाहरण |
|------|-------------|---------|
| प्राथमिक ओळख-किल्ली | डोमेन/उपसर्गानंतरचा मार्ग-तुकडा | `/01/09506000134352` |
| किल्ली-विशेषक (CPV `22`, बॅच `10`, अनुक्रमांक `21`, …) | त्यानंतरचे मार्ग-तुकडे, **§4.9 च्या प्रमाण क्रमाने** (तुम्ही जोडलेल्या क्रमाने नव्हे) | `/10/LOT-ABC` |
| माहिती-गुणधर्म (बाकी सर्व) | क्वेरी पॅरामीटर, **AI किल्लीनुसार शब्दकोश-क्रमाने** (§4.12) | `?17=271231` |

सादरीकरणाच्या वेळी विशेषक पुन्हा क्रमाने लावले जात असल्याने, ते क्रमाबाहेर दिले तरी बिघडत नाही — `ai("10", …)` च्या आधी `ai("21", …)` दिले तरी सादरीकरण `/10/LOT/21/SER` असेच होते. प्राथमिक किल्लीला केवळ त्यांचा *संच* मान्य असावा लागतो.

मार्ग आणि क्वेरी दोन्हींतील मूल्ये टक्केवारी-एन्कोड केली जातात.

बांधणी अपयशी ठरते (`GaiaBuilderException` फेकते, किंवा अपयशी `BuildResult` परत करते) जेव्हा:

- AI मध्ये **एकही** प्राथमिक ओळख-किल्ली नसेल;
- **एकापेक्षा अधिक** प्राथमिक ओळख-किल्ल्या असतील;
- एखादा AI Digital Link मध्ये **वर्जित** असेल (`03`, `8014`);
- निवडलेल्या प्राथमिक किल्लीसाठी **किल्ली-विशेषकांचा क्रम** अवैध असेल (त्या किल्लीसोबत न येणारा विशेषक, किंवा परवानगी असलेल्या क्रमाबाहेरचे विशेषक).

---

## BuilderDigitalLinkConfig

योजना, डोमेन, मार्ग-उपसर्ग, अतिरिक्त क्वेरी पॅरामीटर आणि तुकडा नियंत्रित करण्यासाठी `BuilderDigitalLinkConfig` द्या:

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

| बिल्डर पद्धत | उद्देश | पूर्वनिर्धारित |
|----------------|---------|---------|
| `scheme(String)` | URI योजना; `http` किंवा `https` असणे आवश्यक | `https` |
| `domain(String)` | सोडवणारा अधिकारी — होस्ट किंवा `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | पहिल्या प्राथमिक किल्लीआधीचे मार्ग-तुकडे; दोन्ही टोकांच्या तिरप्या रेषा प्रमाणित होतात | *(काही नाही)* |
| `baseUrl(String)` | एका URL चे `scheme` + `domain` + `pathPrefix` असे विभाजन करणारी सोय | — |
| `addQueryParam(String, String)` | अतिरिक्त क्वेरी पॅरामीटर, जो AI माहिती-गुणधर्मांच्या **नंतर**, जोडलेल्या क्रमाने येतो; टक्केवारी-एन्कोड केलेला | — |
| `fragment(String)` | URI तुकडा (सुरुवातीला `#` विना); टक्केवारी-एन्कोड केलेला | *(काही नाही)* |

`build()` रचनेची पडताळणी तत्काळ करते: `http(s)` नसलेली योजना किंवा रिकामे डोमेन `IllegalArgumentException` फेकते.

- `BuilderDigitalLinkConfig.canonical()` (उपनाव `defaultConfig()`) म्हणजे कोणत्याही जादाशिवाय पूर्वनिर्धारित `https://id.gs1.org` — कारकाविना `buildDigitalLinkUri()` नेमके तेच वापरते, आणि `GS1AIObject.getCanonicalDigitalLink()` तेच तयार करते.
- `baseUrl("http://id.example.org:8080/r")` → योजना `http`, डोमेन `id.example.org:8080`, मार्ग-उपसर्ग `/r`.
- अतिरिक्त क्वेरी पॅरामीटर नेहमी AI पासून आलेल्या गुणधर्मांनंतरच येतात, त्यामुळे प्रमाण AI क्रम (§4.12) टिकून राहतो.

`BuilderDigitalLinkConfig` न बदलणारा आहे; एकच प्रत बिनधास्त पुन्हा वापरा.

---

## पडताळणी आणि चुका

### अपवाद फेकणाऱ्या बांधणी-पद्धती

AI सुव्यवस्थित आउटपुट तयार करू शकत नसतील, तेव्हा `buildElementString()`, `buildDigitalLinkUri()` आणि `buildDigitalLinkUri(BuilderDigitalLinkConfig)` **`GaiaBuilderException`** (न तपासला जाणारा `RuntimeException`) फेकतात:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **आशयाच्या** अपयशांत (चुकीचा तपासणी अंक, स्वरूप न जुळणे, अनुपस्थित/वर्जित AI), `getErrors()` पार्सरचेच `GaiaError` ऑब्जेक्ट वाहते — तेच ऑब्जेक्ट जे [पार्सर मार्गदर्शकात नोंदवले आहेत](GaiaParser-Marathi.md#gaiaerror).
- **Digital Link रचनेच्या** अपयशांत (प्राथमिक किल्ली नाही, अनेक प्राथमिक किल्ल्या, वर्जित AI, अवैध किल्ली-विशेषक क्रम), `getErrors()` बिल्डरच्या भाषेत स्थानिकीकृत एकच `GaiaError` वाहते (कोड `GE-L008`, `GE-L012`, `GE-L013` किंवा `GE-L014`).

### अपवाद न फेकणाऱ्या tryBuild\* पद्धती

इनपुट वापरकर्त्याकडून येत असेल आणि अपयश हा अपेक्षित, हाताळता येण्याजोगा परिणाम असेल, तेव्हा अपवादांनी नियंत्रण-प्रवाह चालवण्याऐवजी `tryBuild*` रूपे वापरा. ती फेकण्याऐवजी [`BuildResult`](#buildresult) परत करतात:

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

| फेकते | फेकत नाही |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

प्रत्येक `tryBuild*` पद्धत आपल्या फेकणाऱ्या जुळ्या भावंडाशी तोच पडताळणी-गाभा वाटून घेते; फरक फक्त अपयशाच्या सीमेचा.

### चूक-संदेशांची भाषा

आशय-पडताळणीच्या चुका स्थानिकीकृत चूक-यादीतून येतात. `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` जे `GaiaError` संदेश वाहतात त्यांची भाषा निवडण्यासाठी `language(...)` बोलवा; पूर्वनिर्धारित भाषा इंग्रजी आहे:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

हीच ती `GaiaConstants.Language` सेटिंग आहे जी `GaiaParser` `ParseConfig` द्वारे स्वीकारतो, त्यामुळे बिल्डर आणि पार्सर एकाच पद्धतीने स्थानिकीकरण करतात.

**आशयाच्या** आणि **Digital Link रचनेच्या** (प्राथमिक किल्ली नाही, अनेक प्राथमिक किल्ल्या, वर्जित AI, अवैध किल्ली-विशेषक क्रम) — या दोन्ही अपयशांचे `GaiaError` संदेश समान चूक-यादीद्वारेच स्थानिकीकृत होतात; दुसरी `GE-L008`, `GE-L012`, `GE-L013` आणि `GE-L014` हे कोड वापरते.

### BuildResult

`BuildResult` (पॅकेज `tools.pantheum.gaia.result`) हा न बदलणारा मूल्य-प्रकार आहे, जो `tryBuild*` बोलावण्याचा परिणाम वर्णन करतो:

| पद्धत | यश आल्यावर | अपयश आल्यावर |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | सादर केलेली स्ट्रिंग | `null` |
| `getMessage()` | `null` | अपयशाचे वर्णन |
| `getErrors()` | रिकामी यादी | पडताळणीच्या चुका (`GaiaBuilderException.getErrors()` मधल्याच) |

---

## तपासणी अंक

बिल्डर तपासणी अंकांची पडताळणी करतो पण ते **मोजत नाही** — तुमच्या मूल्यांत तपासणी अंक आधीच असायला हवा. एखादा मोजण्यासाठी `GS1Utils.calculateCheckDigit` वापरा:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` दिलेल्या अंकांवर GS1 चा प्रमाण मोड्युलो-१० अल्गोरिदम लावते आणि `0` ते `9` यांतील तपासणी अंक परत करते, किंवा इनपुट `null`, रिकामे किंवा असंख्यात्मक असल्यास `-1`.

---

## थ्रेड सुरक्षा

`GaiaBuilder` थ्रेड-सुरक्षित **नाही** आणि तो एकदाच वापरण्यासाठी घडवलेला आहे: `create()` बोलवा, AI जोडा, एकदाच बांधणी करा. प्रत्येक आउटपुटसाठी नवा बिल्डर तयार करा; एकच बिल्डर अनेक थ्रेडमध्ये वाटून वापरू नका.

`BuilderDigitalLinkConfig` (आणि त्याचे `BuildResult` आउटपुट) न बदलणारे आहेत आणि बिनधास्त वाटून वापरता येतात — सुरुवातीला एकच रचना तयार करा आणि ती अनेक बिल्डरमध्ये पुन्हा वापरा.

---

## API संदर्भ

### `GaiaBuilder`

| पद्धत | वर्णन |
|--------|-------------|
| `static GaiaBuilder create()` | नवा, रिकामा बिल्डर सुरू करते. |
| `GaiaBuilder ai(String ai, String value)` | एक AI आणि त्याचे संपूर्ण मूल्य जोडते. यांपैकी एखादे `null` असल्यास, किंवा `ai` हा ओळखीचा GS1 अ‍ॅप्लिकेशन आयडेंटिफायर नसल्यास `IllegalArgumentException` फेकते. |
| `GaiaBuilder language(GaiaConstants.Language language)` | आशय-पडताळणीच्या चूक-संदेशांची भाषा ठरवते (पूर्वनिर्धारित इंग्रजी). `null` दुर्लक्षित होते. |
| `String buildElementString()` | GS1 घटक स्ट्रिंग सादर करते. अपयशी झाल्यास `GaiaBuilderException` फेकते. |
| `String buildDigitalLinkUri()` | प्रमाण Digital Link URI सादर करते. अपयशी झाल्यास `GaiaBuilderException` फेकते. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` नुसार Digital Link URI सादर करते. अपयशी झाल्यास `GaiaBuilderException` फेकते. |
| `BuildResult tryBuildElementString()` | अपवाद न फेकणारी घटक स्ट्रिंग बांधणी. |
| `BuildResult tryBuildDigitalLinkUri()` | अपवाद न फेकणारी प्रमाण Digital Link बांधणी. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` नुसार अपवाद न फेकणारी Digital Link बांधणी. |

### `BuilderDigitalLinkConfig`

| सदस्य | वर्णन |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | पूर्वनिर्धारित `https://id.gs1.org`. |
| `static Builder builder()` | नवा रचना-बिल्डर. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | सोडवलेली योजना, सोडवणारा अधिकारी आणि मार्ग-उपसर्ग. |
| `getExtraQueryParams()` | अतिरिक्त क्वेरी पॅरामीटर, जोडलेल्या क्रमाने. |
| `getFragment()` | तुकडा, किंवा `null`. |

### `GaiaBuilderException`

| सदस्य | वर्णन |
|--------|-------------|
| `getErrors()` | अपयशाला कारणीभूत ठरलेले `GaiaError` ऑब्जेक्ट — आशयाच्या अपयशात पार्सरच्या चुका, किंवा एकच Digital Link संरचनात्मक चूक (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). कधीही `null` नसते. |

### `BuildResult`

| सदस्य | वर्णन |
|--------|-------------|
| `isSuccess()` | बांधणी यशस्वी झाली का. |
| `getValue()` | यश आल्यावर सादर केलेले आउटपुट; अपयशी झाल्यास `null`. |
| `getMessage()` | अपयशी झाल्यास अपयशाचे वर्णन; यश आल्यावर `null`. |
| `getErrors()` | अपयशी झाल्यास पडताळणीच्या चुका; यश आल्यावर रिकामी. कधीही `null` नसते. |
| `getTiming()` | बांधणी-क्रियेचा `ProcessingTiming` (सुरुवातीची वेळ, प्रक्रियेचा कालावधी), किंवा `null`. |

---

हेही पाहा: पार्सिंगची बाजू, AI घटक प्रारूप, चूक संदर्भ, आणि AI व विवेचन स्थिरांकांची परिशिष्टे यांसाठी **[GaiaParser — विकसक मार्गदर्शक](GaiaParser-Marathi.md)**.
