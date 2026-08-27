# GaiaParser — விரைவுத் தொடக்கம்

GS1 பார்கோடு சுமையை ஏறத்தாழப் பத்து நிமிடங்களில் கட்டமைக்கப்பட்ட, சரிபார்க்கப்பட்ட, மனிதர்
படிக்கக்கூடிய தரவாக மாற்றுங்கள். இது குறுக்கு வழி; **[GaiaParser உருவாக்குநர்
வழிகாட்டி](GaiaParser-Tamil.md)** முழுமையான குறிப்பேடு; **[GaiaBuilder](GaiaBuilder-Tamil.md)** எதிர்த்
திசையைக் (உறுப்புச் சரங்களையும் Digital Link URI-களையும் கட்டமைத்தல்) கவனிக்கிறது.

## பொருளடக்கம்

1. [உங்கள் திட்டத்தில் Gaia-வைச் சேருங்கள்](#1-உஙகள-தடடததல-gaia-வச-சரஙகள)
2. [ஏதேனும் ஒன்றைப் பாகுபடுத்துங்கள்](#2-ஏதனம-ஒனறப-பகபடததஙகள)
3. [முடிவைப் படியுங்கள்](#3-மடவப-படயஙகள)
4. [தோல்வியுற்ற பாகுபடுத்தலைக் கையாளுங்கள்](#4-தலவயறற-பகபடததலக-கயளஙகள)
5. [உங்களைத் தடுமாற வைக்கும் இரு விஷயங்கள்](#5-உஙகளத-தடமற-வககம-இர-வஷயஙகள)
6. [ஸ்கேனர் முன்னொட்டுகளும் Digital Link-ம் தானாகவே இயங்குகின்றன](#6-ஸகனர-மனனடடகளம-digital-link-ம-தனகவ-இயஙககனறன)
7. [குறைவாக வேலை செய்யுங்கள்: பாகுபடுத்தல் முறைகள்](#7-கறவக-வல-சயயஙகள-பகபடததல-மறகள)
8. [மொழியையும் தேதி வடிவத்தையும் மாற்றுங்கள்](#8-மழயயம-தத-வடவததயம-மறறஙகள)
9. [கலைந்த உள்ளீட்டைச் சுத்தம் செய்யுங்கள்](#9-கலநத-உளளடடச-சததம-சயயஙகள)
10. [இங்கிருந்து எங்கே](#10-இஙகரநத-எஙக)

---

## 1. உங்கள் திட்டத்தில் Gaia-வைச் சேருங்கள்

Gaia Maven Central இல் வெளியிடப்படவில்லை; எனவே கோர்-ஐ ஒருமுறை கட்டி உங்கள் உள்ளூர்க்
களஞ்சியத்தில் நிறுவுங்கள்:

```bash
cd gaia && mvn install
```

பின்னர் அதைச் சார்ந்திருங்கள்:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

நீங்கள் எழுத வேண்டிய சார்பு இவ்வளவே. jar இலகுவானது: Gaia-வின் ஒரே தொகுப்பு-நோக்குச் சார்பு —
`com.fasterxml.jackson.core:jackson-databind` — கடத்தல் வழியாகவே வந்துவிடும்; உங்கள் கட்டமைப்பு
ஏற்கெனவே ஒரு Jackson பதிப்பை நிலைநிறுத்தியிருந்தால், அந்த நிலைநிறுத்தலே வெல்லும், Gaia
அதையே பயன்படுத்தும். Gaia **Java 11**-ஐக் குறியாகக் கொள்கிறது; அதே jar அடுத்துவரும் ஒவ்வொரு
JVM வெளியீட்டிலும் மாற்றமின்றி இயங்கும்.

> தொடக்கத்தில் கோர் சோதனைத் தொகுப்பைத் தவிர்ப்பது (`mvn install -DskipTests`) சில
> நிமிடங்களைச் சில நொடிகளாக மாற்றும்.

---

## 2. ஏதேனும் ஒன்றைப் பாகுபடுத்துங்கள்

ஒரே வகுப்பு, எந்த அமைப்பும் இல்லை:

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

`parse(String)` **முழுப்** பைப்லைனையும் இயக்குகிறது: தொடரமைப்பு, உள்ளடக்கச் சரிபார்ப்பு,
விளக்கம். இதுவே சரியான இயல்பு — அதைக் குறுக்குவதற்கு அளந்த காரணம் கிடைக்கும்போது குறுக்குங்கள்.

---

## 3. முடிவைப் படியுங்கள்

`ParseResult.getAiObject()` தீர்க்கப்பட்ட AI-களை வைத்திருக்கிறது. குறிப்பிட்ட ஓர் AI-ஐ அதன்
இடத்தை வைத்து அல்ல, குறியீட்டை வைத்துப் பெறுங்கள்:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

ஒவ்வோர் உறுப்பும் ஒரு **விளக்கப்** பட்டியலைச் சுமக்கிறது — மூல இலக்கங்களுக்குப் பின்னால்
விரித்துக் காட்டப்பட்ட பொருள்; இதை விளக்க நிலை உருவாக்குகிறது:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` உள்ளூர்மயமாக்கப்பட்டது, அது காட்சிக்கானது. ஆனால் நிரலுக்குள் ஒரு மதிப்பைப்
*படிக்க*, அதற்குப் பதிலாக அதன் மாறாத வகைச் சாவியால் தேடுங்கள்:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

வெவ்வேறு AI-கள் வெவ்வேறு சாவிகளை உருவாக்குகின்றன — GTIN தன் நிறுவன முன்னொட்டையும், GTIN
வகையையும், கட்டுப்பாட்டு இலக்கத்தையும் தருகிறது; விலை நாணயத்தையும் தசமத் தொகையையும்
தருகிறது. முழுப் பட்டியல் [இணைப்பு ஆ](GaiaParser-Tamil.md#இணபப-ஆ--வளககச-சவ-மறலகள)
இல் உள்ளது; மாறிலிகள் `GS1Constants_Enricher` இல் உள்ளன. ஒவ்வோர் AI-க்கும் விளக்கங்கள்
இருப்பதில்லை: தொகுதி/லாட் எண் என்பது தடையற்ற உரை, அதிலிருந்து பெறுவதற்கு ஒன்றுமில்லை; எனவே
அதன் பட்டியல் வெறுமையாகவே இருக்கும்.

---

## 4. தோல்வியுற்ற பாகுபடுத்தலைக் கையாளுங்கள்

செல்லாத சுமை என்பது இயல்பான முடிவே தவிர விதிவிலக்கு அல்ல — கெட்ட GS1 தரவுக்காக `parse`
ஒருபோதும் விதிவிலக்கு எறிவதில்லை:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()`-இல் கிளை பிரியுங்கள், செய்தியில் ஒருபோதும் அல்ல.** செய்திகள் உள்ளூர்மயமாக்கப்படுகின்றன,
அவற்றின் சொல்லாட்சி ஓர் ஒப்பந்தம் அல்ல — மேலும் தற்போது அவற்றில் அறியப்பட்ட ஒரு மேற்கோள்
குறை உள்ளது (மேலே தெரியும் இரட்டை `''`), அது
[பிழைக் குறிப்பேட்டில்](GaiaParser-Tamil.md#பழக-கறபபட) குறிக்கப்பட்டுள்ளது.

இரு வேறு கேள்விகள், இரு வேறு முறைகள்:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

ஒரு நிலை தோல்வியுற்றவுடன் பாகுபடுத்தல் ஆழம் செல்வதை நிறுத்திவிடும்; எனவே தவறான கட்டுப்பாட்டு
இலக்கம் என்றால் உங்களுக்குச் சரிபார்ப்புப் பிழைகள் கிடைக்கும், ஆனால் எந்த விளக்கமும் கிடைக்காது.

### எச்சரிக்கைகள் முடிவைச் செல்லாததாக்குவதில்லை

சில சரிபார்ப்புகள் அறிவுறுத்தல் மட்டுமே. அடையாளம் காணப்படாத GS1 நிறுவன முன்னொட்டு
அறிவிக்கப்படும், ஆனால் சுமை நன்கமைந்ததாகவே இருக்கும்:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

இரண்டையும் சேர்த்து வேண்டுமெனில் `getIssues()` பயன்படுத்துங்கள். அடையாளம் காணப்படாத
முன்னொட்டுகளை நிராகரிப்பது உங்கள் பணிப்பாய்வுக்குக் கட்டாயமெனில், `getWarnings()`-ஐத்
தெளிவாகச் சரிபாருங்கள் — `isValid()` உங்கள் சார்பாக அதைச் செய்யாது.

---

## 5. உங்களைத் தடுமாற வைக்கும் இரு விஷயங்கள்

### GS பிரிப்பான், அதை விட்டுவிடுவது பிழையைவிட ஏன் மோசம்

மாறு-நீள AI ஒரு **GS எழுத்து** வரை (ASCII `0x1D`; பார்கோடு குறியீட்டு முறைகளில் இது FNC1
எனப்படும்) அல்லது சரத்தின் முடிவு வரை நீளும். அதற்குப் பின் மற்றோர் AI வரும்போது அந்தப்
பிரிப்பான் கட்டாயம்:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

அதை விட்டுவிட்டால் உங்களுக்குப் பிழை **கிடைக்காது** — முழு நம்பிக்கையுடன் தரப்படும் தவறான
விடையே கிடைக்கும்:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` என்பது `X..20`; எனவே `LOT-ABC21SN-98765` முழுவதையும் விழுங்குவது நியாயமானதே, அது
உங்கள் நோக்கம் அல்ல என்பதை அறிய பாகுபடுத்திக்கு வழியேதும் இல்லை. பின்னால் எதுவும் இதை
மீட்டுத் தர முடியாது; எனவே பிரிப்பானை மூலத்திலேயே சரிசெய்யுங்கள்: `0x1D` தப்பிப்பிழைக்க
ஸ்கேனர் பைட்டுகளை **ISO-8859-1** ஆகப் படியுங்கள்; Java சரம் நேரெழுத்துகளில் `""` எழுதுங்கள்.
நிலையான-நீள AI-களுக்கு (`01`, `17`, `3103`) பிரிப்பான் தேவையில்லை — அவற்றின் நீளம்
பாகுபடுத்திக்குத் தெரியும்.

### பெரும்பாலான AI-கள் தனித்து நிற்பதில்லை

தொகுதி/லாட், வரிசை எண், காலாவதித் தேதி போன்றவை அனைத்தும் *பண்புகளே*: அவற்றுடன் ஓர்
அடையாளச் சாவி இருக்க வேண்டும் என GS1 General Specifications கோருகிறது, Gaia அதைச்
செயல்படுத்துகிறது.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

ஒரு GTIN சேர்த்தால் அது தேறிவிடும். உண்மையிலேயே ஒரு துண்டைப் பாகுபடுத்த வேண்டியிருந்தால் —
ஓர் அலகுச் சோதனை, ஒரு பகுதி ஸ்கேன் — அந்தச் சரிபார்ப்பை அணையுங்கள்:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. ஸ்கேனர் முன்னொட்டுகளும் Digital Link-ம் தானாகவே இயங்குகின்றன

உள்ளீடு எந்த வடிவத்தில் உள்ளது என Gaia-விடம் சொல்லத் தேவையில்லை — நான்கு வடிவங்களையும் அது
கண்டறிகிறது. உங்கள் ஸ்கேனர் தந்ததை அப்படியே அனுப்புங்கள்.

**AIM குறியீட்டு முறை அடையாளங்காட்டி முன்னொட்டு** குறியீட்டு முறையைத் தீர்மானிக்கிறது,
தானாகவே நீக்கப்படுகிறது:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI**-ம் அதே சரிபார்ப்பு, வளப்படுத்தல் வழியாகவே செல்கிறது:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

இரு வடிவங்களும் ஒரே `GS1AIObject`-ஐயே சென்றடைவதால், ஸ்கேனை உட்கொள்ளும் நிரல் எது வந்தது
என்பதைப் பற்றிக் கவலைப்பட வேண்டியதில்லை — `toElementString()` /
`getCanonicalDigitalLink()` ஒன்றை மற்றொன்றாக மாற்றுகின்றன.

**8-இலக்கத் தொடர்பு முன்னொட்டும்** (`12345678~…`), உங்கள் ஓட்டம் அதைப் பயன்படுத்தினால்,
அவ்வாறே நீக்கப்பட்டு `getCorrelationInfo()` இல் பாதுகாக்கப்படுகிறது.

---

## 7. குறைவாக வேலை செய்யுங்கள்: பாகுபடுத்தல் முறைகள்

இயல்பு முறை அனைத்தையும் செய்கிறது. விடையின் ஒரு பகுதி மட்டுமே தேவையெனில் குறைவாகக்
கேளுங்கள்:

| முறை | எதற்குப் பதிலளிக்கும் | செலவு |
|---|---|---|
| `DATA_CARRIER` | இது எந்தக் குறியீட்டு முறை? | மலிவானது — AI பாகுபடுத்தலே இல்லை, `getAiObject()` `null` |
| `SYNTAX` | AI குறியீடுகளும் நீளங்களும் சரியாக அமைந்துள்ளனவா? | கட்டுப்பாட்டு இலக்கங்கள் இல்லை, விளக்கங்கள் இல்லை |
| `CONTENT` | இது செல்லுபடியாகும் GS1 தரவா? | முழுச் சரிபார்ப்பு, விளக்கம் இன்றி |
| `INTERPRETATION` | இதன் பொருள் என்ன? | **இயல்பு** — அனைத்தும் |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

பெரும் அளவில் சரிபார்த்தும் பிரிப்பை ஒருபோதும் காட்டாதபோது `CONTENT`-ஐத் தேர்வு செய்யுங்கள்;
ஸ்கேனைச் சரியான கையாளிக்கு அனுப்புவது மட்டுமே தேவையெனில் `DATA_CARRIER`.

---

## 8. மொழியையும் தேதி வடிவத்தையும் மாற்றுங்கள்

பிழைச் செய்திகள், விளக்க லேபிள்கள், AI விளக்கங்கள் **35 மொழிகளில்** மொழிபெயர்க்கப்பட்டுள்ளன;
தேதிகளை நீங்கள் விரும்பியவாறு காட்டலாம். இவை அனைத்தும் ஒரே மாறாத `ParseConfig` இல்
அடங்கிவிடுகின்றன:

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

மதிப்புகள் ஒருபோதும் உள்ளூர்மயமாக்கப்படுவதில்லை — லேபிள்கள், விளக்கங்கள், செய்திகள்
மட்டுமே — எனவே `"2026-12-31"`-ம் `"09506000134352"`-ம் ஒவ்வொரு மொழியிலும் ஒரே பொருளையே
தரும். அமைப்பைத் தொடக்கத்தில் ஒருமுறை கட்டிப் பகிருங்கள்; அது மாறாதது.

---

## 9. கலைந்த உள்ளீட்டைச் சுத்தம் செய்யுங்கள்

உங்கள் மூலம் அச்சிடப்பட்ட HRI அடைப்புக்குறிகளையோ அலைபாயும் இடைவெளிகளையோ வெளியிட்டால்,
பாகுபடுத்தலுக்கு முன்பே சுமையைச் சரிசெய்யும் இரு **உள்ளீட்டு மாற்றிகள்** கோர்-இல் உள்ளன:

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

இயல்பாக எதுவும் இயக்கத்தில் இல்லை; இரண்டுக்கும் தமக்கே உரிய எச்சரிக்கைகள் உண்டு —
இடைவெளியும் அடைப்புக்குறியும் இரண்டுமே செல்லுபடியாகும் GS1 தரவு எழுத்துகள்; எனவே உங்களுக்குத்
தெரிந்த மூலத்திற்கே அவற்றைப் பயன்படுத்துங்கள். பார்க்க
[உள்ளமை மாற்றிகள்](GaiaParser-Tamil.md#உளளம-மறறகள) — அடைப்புக்குறிகளை நீக்கிய பின் அவை
குறித்த பிரிப்பானை ஏன் மீட்டுத் தர வேண்டும் என்பதும் அங்கே விளக்கப்பட்டுள்ளது.

---

## 10. இங்கிருந்து எங்கே

- **[GaiaParser உருவாக்குநர் வழிகாட்டி](GaiaParser-Tamil.md)** — பைப்லைன் விவரங்கள், முழு முடிவு
  மாதிரி, ஒவ்வொரு பிழைக் குறியீடு, மேலும் AI, விளக்கச் சாவிகளின் இணைப்புகள்.
- **[GaiaBuilder உருவாக்குநர் வழிகாட்டி](GaiaBuilder-Tamil.md)** — AI/மதிப்பு இணைகளிலிருந்து
  உறுப்புச் சரங்களையும் Digital Link URI-களையும் கட்டமையுங்கள்.
- **[Gaia API HTTP குறிப்பேடு](../../gaia-api-reference.md)** — நூலகத்தைப் பொதிக்க விரும்பாவிட்டால்
  அதே இயந்திரம் HTTP வழியாக.
- **[ai-codes.txt](../../ai-codes.txt)** — விரைவாகத் தேட `(AI) TITLE` எனும் தட்டையான பட்டியல்.

### ஐந்து வரி வடிவம்

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
