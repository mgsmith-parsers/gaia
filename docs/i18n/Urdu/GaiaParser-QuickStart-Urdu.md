# GaiaParser — فوری آغاز

تقریباً دس منٹ میں کسی GS1 بارکوڈ پے لوڈ کو منظم، توثیق شدہ، انسان کے پڑھنے کے قابل ڈیٹا میں
بدل دیجیے۔ یہ مختصر راستہ ہے؛ **[GaiaParser ڈیویلپر گائیڈ](GaiaParser-Urdu.md)** مکمل حوالہ جاتی
دستاویز ہے، اور **[GaiaBuilder](GaiaBuilder-Urdu.md)** الٹی سمت (ایلیمنٹ سٹرنگ اور Digital Link
URI بنانا) پر بات کرتا ہے۔

## فہرست

1. [اپنے منصوبے میں Gaia شامل کیجیے](#1-اپنے-منصوبے-میں-gaia-شامل-کیجیے)
2. [کچھ پارس کیجیے](#2-کچھ-پارس-کیجیے)
3. [نتیجہ پڑھیے](#3-نتیجہ-پڑھیے)
4. [ناکام پارسنگ سنبھالیے](#4-ناکام-پارسنگ-سنبھالیے)
5. [دو چیزیں جو آپ کو ٹھوکر دیں گی](#5-دو-چیزیں-جو-آپ-کو-ٹھوکر-دیں-گی)
6. [اسکینر کے سابقے اور Digital Link خود بخود چل جاتے ہیں](#6-اسکینر-کے-سابقے-اور-digital-link-خود-بخود-چل-جاتے-ہیں)
7. [کم کام کیجیے: پارس وضعیں](#7-کم-کام-کیجیے-پارس-وضعیں)
8. [زبان اور تاریخ کی شکل بدلیے](#8-زبان-اور-تاریخ-کی-شکل-بدلیے)
9. [بےترتیب مواد صاف کیجیے](#9-بےترتیب-مواد-صاف-کیجیے)
10. [اب آگے کہاں](#10-اب-آگے-کہاں)

---

## 1. اپنے منصوبے میں Gaia شامل کیجیے

Gaia، Maven Central پر شائع نہیں ہوا، اس لیے بنیادی حصہ ایک بار بنا کر اپنی مقامی ریپازٹری
میں نصب کیجیے:

```bash
cd gaia && mvn install
```

پھر اس پر انحصار کیجیے:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

آپ کو بس اتنا ہی انحصار لکھنا ہے۔ jar ہلکا ہے: Gaia کا واحد تالیفی دائرے کا انحصار —
`com.fasterxml.jackson.core:jackson-databind` — منتقل ہو کر خود آ جاتا ہے؛ اور اگر آپ کا بلڈ
پہلے ہی کوئی Jackson ورژن طے کیے ہوئے ہے، تو وہی طے شدگی غالب رہتی ہے اور Gaia اسی کو
استعمال کرتا ہے۔ Gaia **Java 11** کو ہدف بناتا ہے، اور وہی jar بعد کے ہر JVM ریلیز پر بغیر
تبدیلی چلتا ہے۔

> شروع میں بنیادی جانچوں کا مجموعہ چھوڑ دینا (`mvn install -DskipTests`) کئی منٹوں کو چند
> سیکنڈوں میں بدل دیتا ہے۔

---

## 2. کچھ پارس کیجیے

ایک ہی کلاس، کوئی ترتیب نہیں:

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

`parse(String)` **پوری** پائپ لائن چلاتا ہے: نحو، مواد کی توثیق، تعبیر۔ یہی درست طے شدہ روش
ہے — جب آپ کے پاس اسے تنگ کرنے کی کوئی ماپی ہوئی وجہ ہو، تب تنگ کیجیے۔

---

## 3. نتیجہ پڑھیے

`ParseResult.getAiObject()` حل شدہ AI رکھتا ہے۔ کوئی مخصوص AI مقام کے بجائے اس کے کوڈ سے
لیجیے:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

ہر ایلیمنٹ ایک **تعبیری** فہرست اٹھاتا ہے — خام ہندسوں کے پیچھے کھلا ہوا مطلب، جو تعبیر کا
مرحلہ پیدا کرتا ہے:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` مقامی زبان میں ہے اور نمائش کے لیے ہے۔ کوڈ کے اندر کوئی قدر *پڑھنے* کے لیے اسے
اس کے بجائے اس کی ناقابلِ تبدیل قسمی کلید سے تلاش کیجیے:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

مختلف AI مختلف کلیدیں پیدا کرتے ہیں — GTIN اپنا کمپنی سابقہ، GTIN قسم اور چیک ہندسہ دیتا ہے؛
قیمت کرنسی اور اعشاری رقم دیتی ہے۔ مکمل فہرست
[ضمیمہ ب](GaiaParser-Urdu.md#ضمیمہ-ب--تعبیری-کلیدی-مستقلات) میں ہے، اور مستقلات
`GS1Constants_Enricher` میں ہیں۔ ہر AI کی تعبیر نہیں ہوتی: بیچ/لاٹ نمبر آزاد متن ہے جس سے
کچھ اخذ کرنے کو نہیں، اس لیے اس کی فہرست خالی رہتی ہے۔

---

## 4. ناکام پارسنگ سنبھالیے

ناقص پے لوڈ ایک عام نتیجہ ہے، استثنا نہیں — خراب GS1 ڈیٹا کے لیے `parse` کبھی استثنا نہیں
پھینکتا:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` پر شاخ بنائیے، کبھی پیغام پر نہیں۔** پیغامات مقامی زبان میں ہوتے ہیں اور ان کے
الفاظ کوئی معاہدہ نہیں — نیز وہ اس وقت ایک معلوم واوین کا نقص بھی اٹھاتے ہیں (اوپر کا `''`
دوہراپن)، جو [خامیوں کے حوالے](GaiaParser-Urdu.md#خامیوں-کا-حوالہ) میں درج ہے۔

دو مختلف سوال، دو مختلف طریقے:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

جیسے ہی کوئی مرحلہ ناکام ہوتا ہے پارسنگ مزید نیچے نہیں اترتی، چنانچہ غلط چیک ہندسے کا مطلب ہے
کہ آپ کو توثیق کی خامیاں ملیں گی مگر کوئی تعبیر نہیں۔

### تنبیہات نتیجے کو ناقص نہیں بناتیں

بعض جانچیں مشورتی ہیں۔ کوئی غیر شناختہ GS1 کمپنی سابقہ رپورٹ ہوتا ہے، مگر پے لوڈ پھر بھی
خوش ساخت رہتا ہے:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

جب آپ کو دونوں ایک ساتھ چاہئیں تو `getIssues()` استعمال کیجیے۔ اگر آپ کے ورک فلو میں غیر
شناختہ سابقوں کو مسترد کرنا لازم ہو، تو `getWarnings()` صراحتاً جانچیے — `isValid()` آپ کی
طرف سے یہ نہیں کرے گا۔

---

## 5. دو چیزیں جو آپ کو ٹھوکر دیں گی

### GS جداکار، اور اسے چھوڑ دینا خامی سے بھی برا کیوں ہے

متغیر طوالت کا AI کسی **GS حرف** (ASCII `0x1D`، جسے بارکوڈ سمبولوجیوں میں FNC1 کہا جاتا ہے)
تک چلتا ہے، یا سٹرنگ کے اختتام تک۔ جب اس کے بعد کوئی اور AI آئے، تو یہ جداکار
لازمی ہے:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

اسے چھوڑ دیجیے تو آپ کو خامی **نہیں** ملے گی — پورے اعتماد کے ساتھ ایک غلط جواب ملے گا:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` ایک `X..20` ہے، اس لیے پورے `LOT-ABC21SN-98765` کو نگل جانا معقول ہی ہے، اور پارسر
کے پاس یہ جاننے کا کوئی ذریعہ نہیں کہ آپ کا مقصد یہ نہیں تھا۔ بعد میں کوئی چیز اسے واپس نہیں
لا سکتی، چنانچہ جداکار کو منبع ہی پر درست کیجیے: `0x1D` کے بچے رہنے کے لیے اسکینر کے بائٹ
**ISO-8859-1** کے طور پر پڑھیے، اور Java سٹرنگ لٹرل میں `""` لکھیے۔ مقررہ طوالت کے AI
(`01`، `17`، `3103`) کو جداکار درکار نہیں — پارسر ان کی طوالت جانتا ہے۔

### بیشتر AI اکیلے نہیں کھڑے ہوتے

بیچ/لاٹ، سیریل نمبر، میعاد ختم ہونے کی تاریخ اور ایسی ہی چیزیں *خصوصیات* ہیں: GS1 General
Specifications تقاضا کرتی ہیں کہ ان کے ساتھ کوئی شناختی کلید ہو، اور Gaia اسے نافذ کرتا ہے۔

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

ایک GTIN شامل کیجیے، بات بن جائے گی۔ اگر آپ کو واقعی کوئی ٹکڑا پارس کرنا ہو — کوئی یونٹ
ٹیسٹ، کوئی جزوی اسکین — تو جانچ بند کر دیجیے:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. اسکینر کے سابقے اور Digital Link خود بخود چل جاتے ہیں

آپ کو Gaia کو یہ بتانے کی ضرورت نہیں کہ مواد کس شکل میں ہے — وہ چاروں شکلیں پہچان لیتا ہے۔
آپ کے اسکینر نے جو دیا ہے وہی سیدھا آگے بڑھا دیجیے۔

**AIM سمبولوجی آئیڈینٹیفائر کا سابقہ** سمبولوجی متعین کرتا ہے اور خود بخود ہٹ جاتا ہے:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** بھی اسی توثیق اور مالا مالی سے گزرتا ہے:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

چونکہ دونوں شکلیں ایک ہی `GS1AIObject` تک پہنچتی ہیں، اس لیے اسکین استعمال کرنے والے کوڈ کو
اس کی پروا کرنے کی ضرورت نہیں کہ کون سی آئی — اور `toElementString()` /
`getCanonicalDigitalLink()` ایک کو دوسری میں بدل بھی دیتے ہیں۔

**8 ہندسوں کا ارتباطی سابقہ** (`12345678~…`) بھی، اگر آپ کا بہاؤ اسے استعمال کرتا ہو، اسی طرح
ہٹا کر `getCorrelationInfo()` میں محفوظ کر لیا جاتا ہے۔

---

## 7. کم کام کیجیے: پارس وضعیں

طے شدہ وضع سب کچھ کرتی ہے۔ جب آپ کو جواب کا صرف ایک حصہ درکار ہو تو کم مانگیے:

| وضع | کس کا جواب دیتی ہے | لاگت |
|---|---|---|
| `DATA_CARRIER` | یہ کون سی سمبولوجی ہے؟ | سب سے سستی — کوئی AI پارسنگ نہیں، `getAiObject()` `null` |
| `SYNTAX` | کیا AI کوڈ اور طوالتیں خوش ساخت ہیں؟ | نہ چیک ہندسے، نہ تعبیریں |
| `CONTENT` | کیا یہ درست GS1 ڈیٹا ہے؟ | مکمل توثیق، تعبیر کے بغیر |
| `INTERPRETATION` | اس کا مطلب کیا ہے؟ | **طے شدہ** — سب کچھ |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

جب آپ بڑی تعداد میں توثیق کر رہے ہوں اور تجزیہ کبھی دکھاتے نہ ہوں تو `CONTENT` چنیے، اور جب
صرف اسکین کو درست ہینڈلر تک بھیجنا ہو تو `DATA_CARRIER`۔

---

## 8. زبان اور تاریخ کی شکل بدلیے

خامی پیغامات، تعبیری عنوانات اور AI تفصیلات **35 زبانوں** میں ترجمہ شدہ ہیں؛ تاریخیں آپ کی
پسند کے مطابق دکھائی جا سکتی ہیں۔ یہ سب ایک ہی ناقابلِ تبدیل `ParseConfig` میں سما جاتا ہے:

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

قدریں کبھی مقامی نہیں کی جاتیں — صرف عنوانات، تفصیلات اور پیغامات — چنانچہ `"2026-12-31"` اور
`"09506000134352"` ہر زبان میں ایک ہی مطلب رکھتے ہیں۔ ترتیب آغاز پر ایک بار بنا کر مشترک
کیجیے؛ یہ ناقابلِ تبدیل ہے۔

---

## 9. بےترتیب مواد صاف کیجیے

اگر آپ کا منبع چھپے ہوئے HRI قوسین یا بھٹکے ہوئے وقفے بھیجتا ہے، تو بنیادی حصے میں دو
**ان پٹ موڈیفائر** موجود ہیں جو پارسنگ سے پہلے پے لوڈ درست کر دیتے ہیں:

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

بطورِ طے شدہ کوئی بھی فعال نہیں، اور دونوں کے ساتھ تنبیہات ہیں — وقفے اور قوسین دونوں درست
GS1 ڈیٹا حروف ہیں، اس لیے انہیں صرف اُسی منبع پر لگائیے جسے آپ جانتے ہوں۔ دیکھیے
[اندرونی موڈیفائر](GaiaParser-Urdu.md#اندرونی-موڈیفائر)، جہاں یہ بھی بیان ہے کہ قوسین ہٹانے کے
بعد اُس جداکار کو بحال کرنا کیوں ضروری ہے جس کی طرف وہ اشارہ کر رہے تھے۔

---

## 10. اب آگے کہاں

- **[GaiaParser ڈیویلپر گائیڈ](GaiaParser-Urdu.md)** — پائپ لائن کی تفصیل، مکمل نتیجہ ماڈل، ہر
  خامی کوڈ، اور AI و تعبیری کلیدوں کے ضمیمے۔
- **[GaiaBuilder ڈیویلپر گائیڈ](GaiaBuilder-Urdu.md)** — AI/قدر کے جوڑوں سے ایلیمنٹ سٹرنگ اور
  Digital Link URI بنائیے۔
- **[Gaia API HTTP حوالہ](../../gaia-api-reference.md)** — اگر آپ لائبریری شامل کرنا نہ چاہیں تو
  وہی انجن HTTP کے ذریعے۔
- **[ai-codes.txt](../../ai-codes.txt)** — تیز تلاش کے لیے `(AI) TITLE` کی ایک سپاٹ فہرست۔

### پانچ سطروں والا نسخہ

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
