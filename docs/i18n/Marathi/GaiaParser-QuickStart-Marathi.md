# GaiaParser — झटपट सुरुवात

GS1 बारकोड पेलोडचे रूपांतर सुमारे दहा मिनिटांत संरचित, पडताळलेल्या आणि माणसाला वाचता
येणाऱ्या माहितीत करा. हा जवळचा मार्ग आहे; **[GaiaParser विकसक
मार्गदर्शक](GaiaParser-Marathi.md)** हा संपूर्ण संदर्भ आहे, आणि **[GaiaBuilder](GaiaBuilder-Marathi.md)**
उलट दिशा हाताळतो (घटक स्ट्रिंग आणि Digital Link URI तयार करणे).

## अनुक्रमणिका

1. [तुमच्या प्रकल्पात Gaia जोडा](#1-तमचय-परकलपत-gaia-जड)
2. [काहीतरी पार्स करा](#2-कहतर-परस-कर)
3. [निकाल वाचा](#3-नकल-वच)
4. [अपयशी पार्सिंग हाताळा](#4-अपयश-परसग-हतळ)
5. [दोन गोष्टी ज्या तुम्हाला अडखळायला लावतील](#5-दन-गषट-जय-तमहल-अडखळयल-लवतल)
6. [स्कॅनर उपसर्ग आणि Digital Link आपोआप चालतात](#6-सकनर-उपसरग-आण-digital-link-आपआप-चलतत)
7. [कमी काम करा: पार्स पद्धती](#7-कम-कम-कर-परस-पदधत)
8. [भाषा आणि दिनांकाचे स्वरूप बदला](#8-भष-आण-दनकच-सवरप-बदल)
9. [विस्कळीत इनपुट स्वच्छ करा](#9-वसकळत-इनपट-सवचछ-कर)
10. [इथून पुढे कुठे](#10-इथन-पढ-कठ)

---

## 1. तुमच्या प्रकल्पात Gaia जोडा

Gaia हे Maven Central वर प्रकाशित नाही, म्हणून कोअर एकदाच बांधा आणि तुमच्या स्थानिक
भांडारात स्थापित करा:

```bash
cd gaia && mvn install
```

मग त्यावर अवलंबित्व द्या:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

एवढेच अवलंबित्व तुम्हाला लिहावे लागेल. jar हलके आहे: Gaia चे एकमेव संकलन-व्याप्तीचे
अवलंबित्व — `com.fasterxml.jackson.core:jackson-databind` — संक्रमणाने आपोआप येते; आणि
तुमच्या बांधणीने आधीच एखादी Jackson आवृत्ती निश्चित केली असेल, तर तीच निश्चिती जिंकते आणि
Gaia तीच वापरतो. Gaia **Java 11** ला लक्ष्य करतो, आणि तेच jar पुढील प्रत्येक JVM आवृत्तीवर
बदलाविना चालते.

> सुरुवातीच्या टप्प्यात कोअरची चाचणी-मालिका वगळणे (`mvn install -DskipTests`) कित्येक
> मिनिटांचे रूपांतर काही सेकंदांत करते.

---

## 2. काहीतरी पार्स करा

एकच वर्ग, कोणतीही रचना नाही:

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

`parse(String)` **संपूर्ण** पाइपलाइन चालवतो: वाक्यरचना, आशय-पडताळणी, विवेचन. हेच योग्य
पूर्वनिर्धारित आहे — ते संकुचित करण्याचे मोजून-मापून कारण मिळेल तेव्हाच संकुचित करा.

---

## 3. निकाल वाचा

`ParseResult.getAiObject()` सोडवलेले AI धरून ठेवतो. एखादा विशिष्ट AI त्याच्या स्थानावरून
नव्हे तर कोडवरून घ्या:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

प्रत्येक घटक एक **विवेचन** यादी वाहतो — कच्च्या अंकांमागचा उलगडलेला अर्थ, जो विवेचनाचा
टप्पा तयार करतो:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` स्थानिकीकृत असते आणि ती दाखवण्यासाठी आहे. पण कोडमध्ये एखादे मूल्य *वाचण्यासाठी*
त्याऐवजी त्याच्या स्थिर प्रकार-किल्लीने ते शोधा:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

वेगवेगळे AI वेगवेगळ्या किल्ल्या तयार करतात — GTIN आपला कंपनी उपसर्ग, GTIN प्रकार आणि तपासणी
अंक देतो; किंमत चलन आणि दशांश रक्कम देते. संपूर्ण यादी
[परिशिष्ट ब](GaiaParser-Marathi.md#परशषट-ब--ववचन-कलल-सथरक) मध्ये आहे, आणि
स्थिरांक `GS1Constants_Enricher` मध्ये आहेत. प्रत्येक AI ला विवेचन असतेच असे नाही: बॅच/लॉट
क्रमांक हा मुक्त मजकूर आहे ज्यातून काहीच निष्पन्न होत नाही, म्हणून त्याची यादी रिकामी असते.

---

## 4. अपयशी पार्सिंग हाताळा

अवैध पेलोड हा सामान्य निकाल आहे, अपवाद नव्हे — खराब GS1 माहितीसाठी `parse` कधीच अपवाद फेकत
नाही:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` वर शाखा करा, संदेशावर कधीही नाही.** संदेश स्थानिकीकृत असतात आणि त्यांची
शब्दरचना हा करार नाही — शिवाय सध्या त्यांत अवतरणचिन्हांची एक ज्ञात त्रुटी आहे (वरील दुहेरी
`''`), जी [चूक संदर्भात](GaiaParser-Marathi.md#चक-सदरभ) नोंदवली आहे.

दोन वेगळे प्रश्न, दोन वेगळ्या पद्धती:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

एखादा टप्पा अपयशी होताच पार्सिंग अधिक खोल जाणे थांबवते, त्यामुळे चुकीच्या तपासणी अंकाचा अर्थ
असा की तुम्हाला पडताळणीच्या चुका मिळतील पण एकही विवेचन नाही.

### इशाऱ्यांमुळे निकाल अवैध होत नाही

काही तपासण्या केवळ सूचनावजा आहेत. न ओळखलेला GS1 कंपनी उपसर्ग नोंदवला जातो, पण पेलोड तरीही
सुव्यवस्थितच राहतो:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

दोन्ही एकत्र हवे असतील तेव्हा `getIssues()` वापरा. तुमच्या कार्यप्रवाहात न ओळखलेले उपसर्ग
नाकारणे भाग असेल, तर `getWarnings()` स्पष्टपणे तपासा — `isValid()` ते तुमच्यासाठी करणार
नाही.

---

## 5. दोन गोष्टी ज्या तुम्हाला अडखळायला लावतील

### GS विभाजक, आणि तो वगळणे चुकीपेक्षाही वाईट का

परिवर्तनीय-लांबीचा AI **GS अक्षरापर्यंत** (ASCII `0x1D`, ज्याला बारकोड सिम्बॉलॉजीत FNC1
म्हणतात) किंवा स्ट्रिंगच्या शेवटापर्यंत चालतो. त्याच्या पुढे दुसरा AI येत असेल, तेव्हा तो
विभाजक अनिवार्य असतो:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

तो वगळल्यास तुम्हाला चूक **मिळणार नाही** — पूर्ण आत्मविश्वासाने दिलेले चुकीचे उत्तर मिळेल:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` हा `X..20` आहे, त्यामुळे संपूर्ण `LOT-ABC21SN-98765` गिळणे अगदी तर्कसंगत आहे, आणि
हे तुम्हाला अभिप्रेत नव्हते हे कळण्याचा पार्सरकडे कोणताही मार्ग नाही. पुढे जाऊन हे कोणीही
भरून काढू शकत नाही, म्हणून विभाजक मुळापाशीच बरोबर ठेवा: `0x1D` टिकावा म्हणून स्कॅनरचे बाइट
**ISO-8859-1** म्हणून वाचा, आणि Java स्ट्रिंग लिटरलमध्ये `""` लिहा. निश्चित-लांबीच्या AI
ना (`01`, `17`, `3103`) विभाजक लागत नाही — पार्सरला त्यांची लांबी ठाऊक असते.

### बहुतेक AI एकटे उभे राहत नाहीत

बॅच/लॉट, अनुक्रमांक, कालबाह्यता तारीख आणि तत्सम गोष्टी या *गुणधर्म* आहेत: GS1 General
Specifications मागणी करतात की त्यांच्यासोबत ओळख-किल्ली असावी, आणि Gaia ते लागू करतो.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

एक GTIN जोडा, म्हणजे ते उत्तीर्ण होईल. पण खरोखरच एखादा तुकडा पार्स करायचा असेल — एकक चाचणी,
अर्धवट स्कॅन — तर ती तपासणी बंद करा:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. स्कॅनर उपसर्ग आणि Digital Link आपोआप चालतात

इनपुट कोणत्या स्वरूपात आहे हे Gaia ला सांगावे लागत नाही — तो चारही स्वरूपे ओळखतो. तुमच्या
स्कॅनरने जे दिले असेल तेच थेट पुढे द्या.

**AIM सिम्बॉलॉजी आयडेंटिफायर उपसर्ग** सिम्बॉलॉजी ठरवतो आणि आपोआप काढून टाकला जातो:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** त्याच पडताळणी व समृद्धीतून जातो:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

दोन्ही स्वरूपे एकाच `GS1AIObject` पर्यंत पोहोचत असल्याने, स्कॅन वापरणाऱ्या कोडला कोणते आले
याची पर्वा करावी लागत नाही — आणि `toElementString()` / `getCanonicalDigitalLink()` एकाचे
दुसऱ्यात रूपांतर करतात.

**8-अंकी सहसंबंध उपसर्गही** (`12345678~…`) तुमचा प्रवाह तो वापरत असेल तर त्याच पद्धतीने
काढून `getCorrelationInfo()` मध्ये जपून ठेवला जातो.

---

## 7. कमी काम करा: पार्स पद्धती

पूर्वनिर्धारित पद्धत सर्व काही करते. उत्तराचा फक्त एक भाग हवा असेल तेव्हा कमी मागा:

| पद्धत | कशाचे उत्तर देते | खर्च |
|---|---|---|
| `DATA_CARRIER` | ही कोणती सिम्बॉलॉजी आहे? | सर्वात स्वस्त — AI पार्सिंग मुळीच नाही, `getAiObject()` हे `null` |
| `SYNTAX` | AI कोड आणि लांब्या सुव्यवस्थित आहेत का? | तपासणी अंक नाही, विवेचन नाही |
| `CONTENT` | ही वैध GS1 माहिती आहे का? | पूर्ण पडताळणी, विवेचनाविना |
| `INTERPRETATION` | याचा अर्थ काय? | **पूर्वनिर्धारित** — सर्व काही |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

मोठ्या प्रमाणात पडताळणी करत असाल आणि पृथक्करण कधीच दाखवत नसाल तेव्हा `CONTENT` निवडा, आणि
फक्त स्कॅन योग्य हँडलरकडे पाठवायचे असेल तेव्हा `DATA_CARRIER`.

---

## 8. भाषा आणि दिनांकाचे स्वरूप बदला

चूक-संदेश, विवेचन लेबले आणि AI वर्णने **35 भाषांत** अनुवादित आहेत; आणि दिनांक तुम्हाला हवे
तसे दाखवता येतात. हे सर्व एकाच न बदलणाऱ्या `ParseConfig` मध्ये सामावते:

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

मूल्ये कधीही स्थानिकीकृत होत नाहीत — केवळ लेबले, वर्णने आणि संदेश — त्यामुळे `"2026-12-31"`
आणि `"09506000134352"` यांचा अर्थ प्रत्येक भाषेत तोच राहतो. रचना सुरुवातीला एकदाच तयार करा
आणि ती वाटून वापरा; ती न बदलणारी आहे.

---

## 9. विस्कळीत इनपुट स्वच्छ करा

तुमचा स्रोत छापील HRI कंस किंवा भरकटलेल्या जागा बाहेर टाकत असेल, तर कोअरमध्ये दोन **इनपुट
मॉडिफायर** आहेत जे पार्सिंगआधी पेलोड नीट करतात:

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

पूर्वनिर्धारितपणे एकही सक्षम नाही, आणि दोघांच्याही स्वतःच्या मर्यादा आहेत — जागा आणि कंस
दोन्ही वैध GS1 माहिती-अक्षरे आहेत, म्हणून ते केवळ तुम्हाला माहीत असलेल्या स्रोतावरच लावा.
पाहा [अंगभूत मॉडिफायर](GaiaParser-Marathi.md#अगभत-मडफयर), जिथे कंस काढल्यावर ते ज्या
विभाजकाचा निर्देश करत होते तो का परत आणावा लागतो हेही स्पष्ट केले आहे.

---

## 10. इथून पुढे कुठे

- **[GaiaParser विकसक मार्गदर्शक](GaiaParser-Marathi.md)** — पाइपलाइनचा तपशील, संपूर्ण निकाल-प्रारूप,
  प्रत्येक चूक कोड, आणि AI व विवेचन किल्ल्यांची परिशिष्टे.
- **[GaiaBuilder विकसक मार्गदर्शक](GaiaBuilder-Marathi.md)** — AI/मूल्य जोड्यांपासून घटक स्ट्रिंग आणि
  Digital Link URI तयार करा.
- **[Gaia API HTTP संदर्भ](../../gaia-api-reference.md)** — लायब्ररी अंतर्भूत करायची नसेल, तर तेच
  इंजिन HTTP द्वारे.
- **[ai-codes.txt](../../ai-codes.txt)** — झटपट शोधासाठी `(AI) TITLE` ची सपाट यादी.

### पाच ओळींची आवृत्ती

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
