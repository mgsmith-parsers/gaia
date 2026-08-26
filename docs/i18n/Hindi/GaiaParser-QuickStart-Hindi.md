# GaiaParser — क्विक स्टार्ट

GS1 बारकोड पेलोड को लगभग दस मिनट में संरचित, सत्यापित, मनुष्य-पठनीय डेटा में बदलिए।
यह छोटा रास्ता है; **[GaiaParser डेवलपर गाइड](GaiaParser-Hindi.md)** पूरा संदर्भ-दस्तावेज़ है,
और **[GaiaBuilder](GaiaBuilder-Hindi.md)** उलटी दिशा (एलिमेंट स्ट्रिंग और Digital Link URI
बनाना) को समेटता है।

## विषय-सूची

1. [अपनी परियोजना में Gaia जोड़िए](#1-अपन-परयजन-म-gaia-जडए)
2. [कुछ पार्स कीजिए](#2-कछ-परस-कजए)
3. [परिणाम पढ़िए](#3-परणम-पढए)
4. [विफल पार्सिंग सँभालिए](#4-वफल-परसग-सभलए)
5. [दो बातें जो आपको ठोकर खिलाएँगी](#5-द-बत-ज-आपक-ठकर-खलएग)
6. [स्कैनर उपसर्ग और Digital Link अपने-आप चलते हैं](#6-सकनर-उपसरग-और-digital-link-अपन-आप-चलत-ह)
7. [कम काम कीजिए: पार्स मोड](#7-कम-कम-कजए-परस-मड)
8. [भाषा और तिथि-प्रारूप बदलिए](#8-भष-और-तथ-पररप-बदलए)
9. [गंदे इनपुट को साफ़ कीजिए](#9-गद-इनपट-क-सफ-कजए)
10. [आगे कहाँ जाएँ](#10-आग-कह-जए)

---

## 1. अपनी परियोजना में Gaia जोड़िए

Gaia Maven Central पर प्रकाशित नहीं है, इसलिए कोर को एक बार बनाइए और अपनी स्थानीय
रिपॉज़िटरी में स्थापित कीजिए:

```bash
cd gaia && mvn install
```

फिर उस पर निर्भरता रखिए:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

आपको बस इतनी ही निर्भरता लिखनी है। jar पतला है: Gaia की एकमात्र संकलन-क्षेत्र निर्भरता —
`com.fasterxml.jackson.core:jackson-databind` — सकर्मक रूप से आ जाती है; और यदि आपका बिल्ड
पहले से कोई Jackson संस्करण निर्धारित करता है, तो वही निर्धारण जीतता है और Gaia उसी का
प्रयोग करता है। Gaia **Java 11** को लक्षित करता है, और वही jar हर बाद के JVM रिलीज़ पर
बिना बदलाव चलता है।

> आरंभ करते समय कोर परीक्षण-समूह छोड़ देना (`mvn install -DskipTests`) कुछ मिनटों को कुछ
> सेकंडों में बदल देता है।

---

## 2. कुछ पार्स कीजिए

एक क्लास, कोई विन्यास नहीं:

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

`parse(String)` **पूरी** पाइपलाइन चलाता है: वाक्य-रचना, सामग्री-सत्यापन, व्याख्या। यही सही
डिफ़ॉल्ट है — जब आप इसका कोई कारण माप लें, तब इसे संकुचित कीजिए।

---

## 3. परिणाम पढ़िए

`ParseResult.getAiObject()` में हल किए गए AI होते हैं। किसी विशिष्ट AI को उसके स्थान से
नहीं, कोड से लीजिए:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

हर एलिमेंट **व्याख्याओं** की एक सूची वहन करता है — कच्चे अंकों के पीछे का खुला हुआ अर्थ,
जिसे व्याख्या-चरण बनाता है:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` स्थानीयकृत है और प्रदर्शन के लिए है। कोड में कोई मान *पढ़ने* के लिए उसे उसकी
स्थिर प्रकार-कुंजी से खोजिए:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

भिन्न AI भिन्न कुंजियाँ बनाते हैं — GTIN अपना कंपनी उपसर्ग, GTIN प्रकार और जाँच अंक देता
है; मूल्य अपनी मुद्रा और दशमलव राशि देता है। पूरी सूची
[परिशिष्ट ब](GaiaParser-Hindi.md#परशषट-ब--वयखय-कज-सथरक) में है, और स्थिरांक
`GS1Constants_Enricher` में। हर AI की व्याख्याएँ नहीं होतीं: बैच/लॉट संख्या मुक्त पाठ है
जिससे कुछ व्युत्पन्न नहीं किया जा सकता, इसलिए उसकी सूची रिक्त रहती है।

---

## 4. विफल पार्सिंग सँभालिए

अवैध पेलोड एक सामान्य परिणाम है, अपवाद नहीं — ख़राब GS1 डेटा के लिए `parse` कभी अपवाद
नहीं फेंकता:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` पर शाखा बनाइए, संदेश पर कभी नहीं।** संदेश स्थानीयकृत हैं और उनकी शब्दावली कोई
अनुबंध नहीं — साथ ही उनमें इस समय एक ज्ञात उद्धरण-दोष भी है (ऊपर दिखा `''` दोहरापन), जो
[त्रुटि संदर्भ](GaiaParser-Hindi.md#तरट-सदरभ) में अभिलिखित है।

दो भिन्न प्रश्न, दो भिन्न विधियाँ:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

कोई चरण विफल होते ही पार्सिंग नीचे उतरना बंद कर देती है, इसलिए ग़लत जाँच अंक का अर्थ है कि
आपको सत्यापन-त्रुटियाँ मिलेंगी पर कोई व्याख्या नहीं।

### चेतावनियाँ परिणाम को अवैध नहीं बनातीं

कुछ जाँचें सलाहकारी हैं। अपरिचित GS1 कंपनी उपसर्ग प्रतिवेदित तो होता है, पर पेलोड सुगठित
ही रहता है:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

जब आपको दोनों चाहिए हों तब `getIssues()` प्रयोग कीजिए। यदि आपके कार्य-प्रवाह में अपरिचित
उपसर्ग अस्वीकार करने ही हों, तो `getWarnings()` की स्पष्ट जाँच कीजिए — `isValid()` यह आपके
लिए नहीं करेगा।

---

## 5. दो बातें जो आपको ठोकर खिलाएँगी

### GS विभाजक, और उसे छोड़ देना त्रुटि से भी बुरा क्यों है

परिवर्तनीय-लंबाई वाला AI **GS वर्ण** (ASCII `0x1D`, बारकोड सिम्बोलॉजी में इसे FNC1 कहते
हैं) तक या स्ट्रिंग के अंत तक चलता है। जब उसके बाद कोई और AI आता हो, तब वह विभाजक
अनिवार्य है:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

उसे छोड़ दीजिए तो आपको त्रुटि **नहीं** मिलती — पूरे आत्मविश्वास के साथ ग़लत उत्तर मिलता है:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` `X..20` है, इसलिए पूरे `LOT-ABC21SN-98765` को निगल लेना उचित ही है, और पार्सर के
पास यह जानने का कोई उपाय नहीं कि आपका आशय यह न था। आगे कुछ भी इसे वापस नहीं ला सकता,
इसलिए विभाजक को स्रोत पर ही ठीक कीजिए: स्कैनर के बाइट **ISO-8859-1** के रूप में पढ़िए
ताकि `0x1D` बचा रहे, और Java स्ट्रिंग लिटरल में `""` लिखिए। नियत-लंबाई वाले AI
(`01`, `17`, `3103`) को विभाजक नहीं चाहिए — पार्सर उनकी लंबाई जानता है।

### अधिकांश AI अकेले खड़े नहीं होते

बैच/लॉट, क्रम संख्या, समाप्ति तिथि और इन जैसे सब *विशेषताएँ* हैं: GS1 General
Specifications अपेक्षा करते हैं कि इनके साथ कोई पहचान-कुंजी हो, और Gaia इसे लागू करता है।

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

GTIN जोड़िए, और यह पार हो जाता है। यदि आपको वाक़ई कोई अंश पार्स करना हो — कोई इकाई परीक्षण,
कोई आंशिक स्कैन — तो जाँच बंद कर दीजिए:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. स्कैनर उपसर्ग और Digital Link अपने-आप चलते हैं

आपको Gaia को यह बताने की ज़रूरत नहीं कि इनपुट किस रूप में है — वह चारों रूप पहचान लेता है।
आपका स्कैनर जो भी दे, वही आगे बढ़ा दीजिए।

**AIM सिम्बोलॉजी आइडेंटिफ़ायर उपसर्ग** सिम्बोलॉजी निर्धारित करता है और स्वतः हट जाता है:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** उसी सत्यापन और समृद्धीकरण से गुज़रता है:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

चूँकि दोनों रूप एक ही `GS1AIObject` पर पहुँचते हैं, इसलिए स्कैन का उपभोग करने वाले कोड को
इसकी चिंता नहीं करनी पड़ती कि कौन-सा रूप आया — और `toElementString()` /
`getCanonicalDigitalLink()` एक को दूसरे में बदल देते हैं।

**8-अंकीय सहसंबंध उपसर्ग** (`12345678~…`) भी हट जाता है और `getCorrelationInfo()` में सुरक्षित
रहता है, यदि आपका प्रवाह उसका प्रयोग करता हो।

---

## 7. कम काम कीजिए: पार्स मोड

डिफ़ॉल्ट सब कुछ करता है। जब आपको उत्तर का केवल एक हिस्सा चाहिए, तब कम माँगिए:

| मोड | किसका उत्तर देता है | लागत |
|---|---|---|
| `DATA_CARRIER` | यह कौन-सी सिम्बोलॉजी है? | सबसे सस्ता — कोई AI पार्सिंग नहीं, `getAiObject()` `null` |
| `SYNTAX` | क्या AI कोड और लंबाइयाँ सुगठित हैं? | कोई जाँच अंक नहीं, कोई व्याख्या नहीं |
| `CONTENT` | क्या यह वैध GS1 डेटा है? | पूर्ण सत्यापन, बिना व्याख्या |
| `INTERPRETATION` | इसका अर्थ क्या है? | **डिफ़ॉल्ट** — सब कुछ |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

जब आप बड़ी मात्रा में सत्यापन कर रहे हों और विश्लेषण कभी दिखाते ही न हों तब `CONTENT`
चुनिए, और जब आपको केवल स्कैन को सही हैंडलर तक भेजना हो तब `DATA_CARRIER`।

---

## 8. भाषा और तिथि-प्रारूप बदलिए

त्रुटि-संदेश, व्याख्या-लेबल और AI विवरण **35 भाषाओं** में अनूदित हैं; तिथियाँ आपकी पसंद के
रूप में दिखाई जा सकती हैं। यह सब एक ही अपरिवर्तनीय `ParseConfig` में समाया है:

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

मान कभी स्थानीयकृत नहीं होते — केवल लेबल, विवरण और संदेश — इसलिए `"2026-12-31"` और
`"09506000134352"` का अर्थ हर भाषा में एक ही रहता है। विन्यास आरंभ में एक बार बनाइए और साझा
कीजिए; वह अपरिवर्तनीय है।

---

## 9. गंदे इनपुट को साफ़ कीजिए

यदि आपका स्रोत मुद्रित HRI कोष्ठक या इधर-उधर बिखरी रिक्तियाँ भेजता है, तो कोर में दो
**इनपुट मॉडिफ़ायर** हैं जो पार्सिंग से पहले पेलोड ठीक कर देते हैं:

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

डिफ़ॉल्ट रूप से कुछ भी सक्रिय नहीं है, और दोनों के साथ चेतावनियाँ जुड़ी हैं — रिक्तियाँ और
कोष्ठक दोनों वैध GS1 डेटा वर्ण हैं, इसलिए इन्हें केवल उसी स्रोत पर लगाइए जिसे आप जानते
हों। देखें [अंतर्निहित मॉडिफ़ायर](GaiaParser-Hindi.md#अतरनहत-मडफयर), जो यह भी समझाता है कि
कोष्ठक हटाने पर उनसे निहित विभाजक को बहाल करना क्यों आवश्यक है।

---

## 10. आगे कहाँ जाएँ

- **[GaiaParser डेवलपर गाइड](GaiaParser-Hindi.md)** — पाइपलाइन का विस्तार, पूरा परिणाम-मॉडल,
  हर त्रुटि कोड, और AI तथा व्याख्या-कुंजियों के परिशिष्ट।
- **[GaiaBuilder डेवलपर गाइड](GaiaBuilder-Hindi.md)** — AI/मान युग्मों से एलिमेंट स्ट्रिंग और
  Digital Link URI बनाइए।
- **[Gaia API HTTP संदर्भ](../../gaia-api-reference.md)** — वही इंजन HTTP के माध्यम से, यदि आप
  लाइब्रेरी अंतःस्थापित न करना चाहें।
- **[ai-codes.txt](../../ai-codes.txt)** — त्वरित खोज के लिए `(AI) TITLE` की सपाट सूची।

### पाँच-पंक्ति संस्करण

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
