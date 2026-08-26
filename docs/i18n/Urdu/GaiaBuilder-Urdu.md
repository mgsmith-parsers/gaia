# GaiaBuilder — ڈیویلپر گائیڈ

## فہرست

1. [مجموعی جائزہ](#مجموعی-جائزہ)
2. [GS1 اور General Specifications کے بارے میں](#gs1-اور-general-specifications-کے-بارے-میں)
3. [فوری آغاز](#فوری-آغاز)
4. [یہ کیسے کام کرتا ہے](#یہ-کیسے-کام-کرتا-ہے)
5. [ایلیمنٹ سٹرنگ بنانا](#ایلیمنٹ-سٹرنگ-بنانا)
   - [خصوصیتی AI کو اپنی شناختی کلید درکار ہوتی ہے](#خصوصیتی-ai-کو-اپنی-شناختی-کلید-درکار-ہوتی-ہے)
6. [Digital Link URI بنانا](#digital-link-uri-بنانا)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [توثیق اور خامیاں](#توثیق-اور-خامیاں)
   - [وہ تعمیری طریقے جو استثنا پھینکتے ہیں](#وہ-تعمیری-طریقے-جو-استثنا-پھینکتے-ہیں)
   - [وہ tryBuild\* طریقے جو استثنا نہیں پھینکتے](#وہ-trybuild-طریقے-جو-استثنا-نہیں-پھینکتے)
   - [خامی پیغامات کی زبان](#خامی-پیغامات-کی-زبان)
   - [BuildResult](#buildresult)
9. [چیک ہندسے](#چیک-ہندسے)
10. [تھریڈ کی حفاظت](#تھریڈ-کی-حفاظت)
11. [API حوالہ](#api-حوالہ)

---

## مجموعی جائزہ

`GaiaBuilder`، [`GaiaParser`](GaiaParser-Urdu.md) کا الٹا ہم منصب ہے: یہ ایپلیکیشن آئیڈینٹیفائر (AI) اور قدر کے جوڑوں کے مجموعے کو ایک خوش ساخت GS1 **ایلیمنٹ سٹرنگ** یا ایک **GS1 Digital Link URI** میں بدل دیتا ہے۔ آپ AI اور ان کی مکمل ڈیٹا قدریں دیتے ہیں؛ بلڈر انہیں جوڑتا ہے، نتیجے کی توثیق اُسی انجن سے کرتا ہے جو `GaiaParser` استعمال کرتا ہے، اور پھر نتیجہ پیش کرتا ہے۔

چونکہ بلڈر توثیق *اپنے ہی مجوزہ نتیجے کو خود پارس کر کے* کرتا ہے، اس لیے جو کچھ بھی وہ واپس کرے وہ `GaiaParser` سے صاف صاف پارس ہو گا — یہ ممکن ہی نہیں کہ دونوں اس بارے میں اختلاف کریں کہ خوش ساخت کیا ہے۔

**داخلی نقطے کی کلاس:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 اور General Specifications کے بارے میں

**GS1** ایک عالمی غیر منافع بخش ادارہ ہے جو رسدی سلسلے کی شناخت اور تبادلۂ معلومات کے لیے کھلے معیارات وضع کرتا اور ان کی دیکھ بھال کرتا ہے۔ اس کے معیارات خردہ فروشی، صحتِ عامہ، ترسیل و رسد، غذائی خدمات اور کئی دیگر صنعتوں میں استعمال ہوتے ہیں، اور صارفی پیکنگ پر مصنوعات کے بارکوڈ سے لے کر ادویات کی خوراکوں کی سلسلہ وار نگرانی تک ہر چیز کو محیط ہیں۔

یہ بلڈر جو کچھ بھی نافذ کرتا ہے اُس کا مستند حوالہ **GS1 General Specifications** ہے — ایک ہی دستاویز جو درج ذیل کا تعین کرتی ہے:

- تمام ایپلیکیشن آئیڈینٹیفائر (AI) کوڈ، ان کے ڈیٹا عنوانات، اشکال اور توثیق کے اصول
- AI ایلیمنٹ سٹرنگ بنانے اور انکوڈ کرنے کے نحوی اصول
- بارکوڈ سمبولوجی کے تقاضے اور AIM کوڈ شناخت کی تفویض
- چیک ہندسے اور چیک حرف کے الگورتھم
- دو ہندسوں کے سال کا تعین (سرکتی کھڑکی کا اصول)
- Data Matrix، QR Code، GS1-128، GS1 DataBar اور دیگر کیریئروں کی تفصیلات

GS1 General Specifications ہر سال تجدید ہوتی ہیں۔ موجودہ اشاعت اور معاون مواد یہاں دستیاب ہے:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA، GS1 General Specifications کی **ریلیز 26.0 (منظور شدہ، جنوری 2026)** نافذ کرتا ہے۔

GS1 Digital Link URI ایک ہمراہی معیار، **GS1 Digital Link: URI Syntax**، کے تابع ہیں، جو بنیادی شناختی کلیدیں، کلیدی تخصیص کاروں کی ترتیب، اور ڈیٹا خصوصیات کی انکوڈنگ متعین کرتا ہے — بلڈر Digital Link URI پیش کرتے وقت یہی اصول لاگو کرتا ہے:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA، GS1 Digital Link: URI Syntax معیار کی **ریلیز 1.7.0 (منظور شدہ، اگست 2026)** نافذ کرتا ہے۔

اس دستاویز میں ابواب کے حوالے GS1 General Specifications کی طرف اشارہ کرتے ہیں (مثلاً "Table 7-5"، "section 7.12")، سوائے Digital Link کے باب نمبروں کے (مثلاً "§4.9"، "§4.12")، جو GS1 Digital Link: URI Syntax معیار کی طرف اشارہ کرتے ہیں۔

---

## فوری آغاز

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

خام AI لٹرل کے بجائے `GS1Constants_AICodes` مستقلات استعمال کیجیے (دیکھیے [پارسر گائیڈ کا ضمیمہ الف](GaiaParser-Urdu.md#ضمیمہ-الف--ai-سٹرنگ-مستقلات)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## یہ کیسے کام کرتا ہے

ہر تعمیر ایک ہی راستے سے گزرتی ہے:

1. **جوڑنا** — AI/قدر کے جوڑے جوڑ کر ایک مجوزہ ایلیمنٹ سٹرنگ بنائی جاتی ہے۔ FNC1 گروپ جداکار (`0x1D`) ہر اُس AI کے بعد ڈالا جاتا ہے جسے *جداکار درکار ہو* اور جو آخری ایلیمنٹ نہ ہو۔ جن AI کی طوالت پہلے سے طے شدہ ہے (GTIN، تاریخیں، مقررہ طوالت کی پیمائشیں) انہیں جداکار نہیں ملتا؛ باقیوں کو ملتا ہے۔ (نامعلوم AI اس قدم تک پہنچتے ہی نہیں — `ai(...)` انہیں فوراً مسترد کر دیتا ہے؛ دیکھیے [ایلیمنٹ سٹرنگ بنانا](#ایلیمنٹ-سٹرنگ-بنانا)۔)
2. **توثیق** — مجوزہ سٹرنگ کو `GaiaParser` `CONTENT` وضع میں پارس کرتا ہے۔ ہر قدر کو اس کے AI کی شکل اور چیک ہندسے کے مقابل جانچا جاتا ہے، اور ساختی اصول (لازمی اور ممنوع AI جوڑے) نافذ کیے جاتے ہیں۔ اگر پارس درست نہ ہو تو تعمیر ناکام ہو جاتی ہے۔
3. **پیشکش** —
   - ایلیمنٹ سٹرنگ کے لیے توثیق شدہ آبجیکٹ کا `toElementString()` واپس کیا جاتا ہے۔
   - Digital Link کے لیے ہر ایلیمنٹ کو اس کا DL کردار دیا جاتا ہے (بنیادی کلید، کلیدی تخصیص کار، یا ڈیٹا خصوصیت)، کلیدی تخصیص کار تسلسل کی توثیق ہوتی ہے، URI پیدا ہوتا ہے، پھر **پیدا شدہ URI کو دوبارہ پارس کر کے یقینی بنایا جاتا ہے کہ وہ ایک درست Digital Link کے طور پر واپس آتا ہے** — یہ سٹرنگ جوڑنے اور فیصد-انکوڈنگ کے قدم کے لیے ایک حفاظتی جانچ ہے۔ اگر واپس نہ آئے تو `GaiaBuilderException` پھینکا جاتا ہے۔

یہ `DLSyntaxParser` کی نئے سرے سے تعمیر کی منطق ہی کا عکس ہے، چنانچہ جداکار کا مقام اور توثیق بعینہٖ وہی رہتے ہیں جن کی پارسر کو توقع ہوتی ہے۔

---

## ایلیمنٹ سٹرنگ بنانا

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** کی توثیق فوراً ہوتی ہے: اگر وہ کوئی شناختہ GS1 ایپلیکیشن آئیڈینٹیفائر نہ ہو تو `ai(...)` ایک `IllegalArgumentException` پھینکتا ہے۔ (بلڈر AI کو پارسنگ سے پہلے ہی قدر کے ساتھ جوڑ دیتا ہے، اس لیے `"99999"` جیسے نامعلوم یا حد سے زیادہ طویل AI کا یہیں پکڑا جانا ضروری ہے — ورنہ وہ خاموشی سے کسی مختلف AI کے طور پر دوبارہ ٹوکن بن جاتا۔) **قدر** کی توثیق بعد میں، تعمیر کے وقت ہوتی ہے۔
- قدروں کا **مکمل** ہونا ضروری ہے، بشمول چیک ہندسے کے۔ بلڈر آپ کی طرف سے نہ چیک ہندسہ گنتا ہے نہ جوڑتا ہے — دیکھیے [چیک ہندسے](#چیک-ہندسے)۔
- AI اسی ترتیب سے نکلتے ہیں جس میں آپ انہیں شامل کریں۔ جہاں GS1 کی ساخت تقاضا کرتی ہے وہاں بلڈر خود FNC1 جداکار ڈال دیتا ہے؛ انہیں خود شامل نہ کیجیے۔
- **بغیر کسی AI کے** تعمیر کرنے پر خالی `getErrors()` فہرست کے ساتھ `GaiaBuilderException("No AIs supplied")` پھینکا جاتا ہے — یہی واحد ناکامی ہے جو کوئی `GaiaError` نہیں اٹھاتی۔
- وہ AI جس کی قدر اپنی شکل یا چیک ہندسے کے اصول پر پوری نہ اترے، پوری تعمیر ہی کو ناکام کر دیتا ہے۔

### خصوصیتی AI کو اپنی شناختی کلید درکار ہوتی ہے

بیشتر AI *خصوصیات* ہیں جن کے ساتھ GS1 General Specifications کسی شناختی کلید کا ہونا لازم قرار دیتی ہیں، اور بلڈر اسے نافذ کرتا ہے — یہ پورے نحوی مرحلے سے گزار کر توثیق کرتا ہے، اور اس کا کوئی راستہ نہیں۔ اکیلا بیچ/لاٹ یا سیریل نمبر درست ایلیمنٹ سٹرنگ **نہیں** ہے:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

شناختی کلیدیں (GTIN `01`، SSCC `00`، GLN `414`، …) اور کمپنی کے اندرونی AI (`90`–`99`) بالکل جائز طور پر اکیلے کھڑے ہو سکتے ہیں۔ باقی ہر چیز کو ایک ساتھی درکار ہے۔

> `GaiaParser` کو `ParseConfig.skipRequiresCheck(true)` کے ذریعے یہ جانچ چھوڑنے کو کہا جا سکتا ہے؛ `GaiaBuilder` جان بوجھ کر اس کا کوئی ہم پلہ نہیں دیتا — اس کا مقصد معیار کے مطابق نتیجہ پیدا کرنا ہے۔ جان بوجھ کر نامکمل کوئی ایلیمنٹ سٹرنگ جوڑنے کے لیے اسے خود جوڑیے اور جانچ بند کر کے پارس کیجیے۔

---

## Digital Link URI بنانا

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

درست Digital Link کے لیے ٹھیک ایک **بنیادی شناختی کلید** درکار ہے (مثلاً GTIN `01`، GLN `414`، SSCC `00`)۔ آپ کے دیے ہوئے ہر AI کو بلڈر درجہ بند کرتا ہے:

| کردار | کیسے پیش ہوتا ہے | مثال |
|------|-------------|---------|
| بنیادی شناختی کلید | ڈومین/سابقے کے بعد کا راستہ حصہ | `/01/09506000134352` |
| کلیدی تخصیص کار (CPV `22`، بیچ `10`، سیریل `21`، …) | اس کے بعد کے راستہ حصے، **§4.9 کی معیاری ترتیب میں** (آپ کے شامل کرنے کی ترتیب میں نہیں) | `/10/LOT-ABC` |
| ڈیٹا خصوصیت (باقی سب کچھ) | کوئری پیرامیٹر، **AI کلید کے مطابق لغوی ترتیب میں** (§4.12) | `?17=271231` |

چونکہ پیشکش کے وقت تخصیص کار ازسرِ نو مرتب ہو جاتے ہیں، انہیں بےترتیب دینے میں کوئی حرج نہیں — `ai("10", …)` سے پہلے `ai("21", …)` دینے پر بھی پیشکش `/10/LOT/21/SER` ہی ہو گی۔ بنیادی کلید کے ہاں صرف اُن کے *مجموعے* کا قابلِ قبول ہونا ضروری ہے۔

راستے اور کوئری، دونوں کی قدریں فیصد-انکوڈ ہوتی ہیں۔

تعمیر ناکام ہوتی ہے (`GaiaBuilderException` پھینکتی ہے، یا ناکام `BuildResult` واپس کرتی ہے) جب:

- AI میں **کوئی** بنیادی شناختی کلید نہ ہو؛
- **ایک سے زیادہ** بنیادی شناختی کلیدیں ہوں؛
- کوئی AI Digital Link میں **ممنوع** ہو (`03`، `8014`)؛
- منتخب بنیادی کلید کے لیے **کلیدی تخصیص کار تسلسل** غلط ہو (کوئی ایسا تخصیص کار جو اُس کلید کے ساتھ نہیں آتا، یا تخصیص کار اپنی اجازت یافتہ ترتیب سے باہر ہوں)۔

---

## BuilderDigitalLinkConfig

سکیم، ڈومین، راستے کے سابقے، اضافی کوئری پیرامیٹروں اور فریگمنٹ کو قابو کرنے کے لیے ایک `BuilderDigitalLinkConfig` دیجیے:

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

| بلڈر طریقہ | مقصد | طے شدہ |
|----------------|---------|---------|
| `scheme(String)` | URI سکیم؛ `http` یا `https` ہونا ضروری ہے | `https` |
| `domain(String)` | حل کنندہ اتھارٹی — میزبان یا `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | پہلی بنیادی کلید سے پہلے کے راستہ حصے؛ دونوں سروں کی سلیش معیاری بنا دی جاتی ہیں | *(کچھ نہیں)* |
| `baseUrl(String)` | سہولت جو کسی URL کو `scheme` + `domain` + `pathPrefix` میں توڑ دیتی ہے | — |
| `addQueryParam(String, String)` | اضافی کوئری پیرامیٹر، AI ڈیٹا خصوصیات کے **بعد**، شمولیت کی ترتیب میں لگتا ہے؛ فیصد-انکوڈ شدہ | — |
| `fragment(String)` | URI فریگمنٹ (شروع میں `#` کے بغیر)؛ فیصد-انکوڈ شدہ | *(کچھ نہیں)* |

`build()` ترتیب کی توثیق فوراً کرتا ہے: `http(s)` کے علاوہ کوئی سکیم یا خالی ڈومین `IllegalArgumentException` پھینکتا ہے۔

- `BuilderDigitalLinkConfig.canonical()` (عرف `defaultConfig()`) بغیر کسی اضافے کے طے شدہ `https://id.gs1.org` ہے — بعینہٖ وہی جو بغیر دلیل کے `buildDigitalLinkUri()` استعمال کرتا ہے، اور جو `GS1AIObject.getCanonicalDigitalLink()` پیدا کرتا ہے۔
- `baseUrl("http://id.example.org:8080/r")` → سکیم `http`، ڈومین `id.example.org:8080`، راستے کا سابقہ `/r`۔
- اضافی کوئری پیرامیٹر ہمیشہ AI سے اخذ شدہ خصوصیات کے بعد آتے ہیں، چنانچہ معیاری AI ترتیب (§4.12) برقرار رہتی ہے۔

`BuilderDigitalLinkConfig` ناقابلِ تبدیل ہے؛ ایک ہی نمونہ بےتکلف بار بار استعمال کیجیے۔

---

## توثیق اور خامیاں

### وہ تعمیری طریقے جو استثنا پھینکتے ہیں

جب AI کوئی خوش ساخت نتیجہ نہ بنا سکیں، تو `buildElementString()`، `buildDigitalLinkUri()` اور `buildDigitalLinkUri(BuilderDigitalLinkConfig)` ایک **`GaiaBuilderException`** (غیر جانچا گیا `RuntimeException`) پھینکتے ہیں:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **مواد** کی ناکامیوں میں (غلط چیک ہندسہ، شکل کی عدم مطابقت، غائب/ممنوع AI) `getErrors()` پارسر کے `GaiaError` آبجیکٹ اٹھاتا ہے — بعینہٖ وہی آبجیکٹ جو [پارسر گائیڈ میں درج ہیں](GaiaParser-Urdu.md#gaiaerror)۔
- **Digital Link ساخت** کی ناکامیوں میں (بنیادی کلید غائب، ایک سے زیادہ بنیادی کلیدیں، ممنوع AI، غلط کلیدی تخصیص کار تسلسل) `getErrors()` بلڈر کی زبان میں مقامی ایک ہی `GaiaError` اٹھاتا ہے (کوڈ `GE-L008`، `GE-L012`، `GE-L013` یا `GE-L014`)۔

### وہ tryBuild\* طریقے جو استثنا نہیں پھینکتے

جب مواد صارف سے آ رہا ہو اور ناکامی ایک متوقع، قابلِ انتظام نتیجہ ہو، تو استثناؤں سے کنٹرول فلو چلانے کے بجائے `tryBuild*` صورتیں استعمال کیجیے۔ یہ پھینکنے کے بجائے ایک [`BuildResult`](#buildresult) واپس کرتی ہیں:

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

| پھینکتا ہے | نہیں پھینکتا |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

ہر `tryBuild*` طریقہ اپنے پھینکنے والے جڑواں ہی کا توثیقی مرکز رکھتا ہے؛ فرق صرف ناکامی کی سرحد کا ہے۔

### خامی پیغامات کی زبان

مواد کی توثیق کی خامیاں مقامی خامی فہرست سے آتی ہیں۔ `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` جو `GaiaError` پیغامات اٹھاتے ہیں ان کی زبان منتخب کرنے کے لیے `language(...)` بلائیے؛ طے شدہ زبان انگریزی ہے:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

یہ وہی `GaiaConstants.Language` ترتیب ہے جو `GaiaParser` `ParseConfig` کے ذریعے قبول کرتا ہے، چنانچہ بلڈر اور پارسر ایک ہی طرح مقامی زبان میں ڈھالتے ہیں۔

**مواد** اور **Digital Link ساخت** (بنیادی کلید غائب، ایک سے زیادہ بنیادی کلیدیں، ممنوع AI، غلط کلیدی تخصیص کار تسلسل) — دونوں ناکامیوں کے `GaiaError` پیغامات مشترکہ خامی فہرست ہی کے ذریعے مقامی ہوتے ہیں؛ مؤخر الذکر `GE-L008`، `GE-L012`، `GE-L013` اور `GE-L014` کوڈ استعمال کرتی ہے۔

### BuildResult

`BuildResult` (پیکیج `tools.pantheum.gaia.result`) ایک ناقابلِ تبدیل قدری قسم ہے جو کسی `tryBuild*` بلاوے کا نتیجہ بیان کرتی ہے:

| طریقہ | کامیابی پر | ناکامی پر |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | پیش کی گئی سٹرنگ | `null` |
| `getMessage()` | `null` | ناکامی کی تفصیل |
| `getErrors()` | خالی فہرست | توثیق کی خامیاں (وہی جو `GaiaBuilderException.getErrors()` میں) |

---

## چیک ہندسے

بلڈر چیک ہندسوں کی توثیق کرتا ہے مگر انہیں **گنتا نہیں** — آپ کی قدروں میں چیک ہندسہ پہلے سے شامل ہونا چاہیے۔ کوئی ایک گننے کے لیے `GS1Utils.calculateCheckDigit` استعمال کیجیے:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` دیے گئے ہندسوں پر معیاری GS1 موڈیولو-10 الگورتھم لگاتا ہے اور `0`–`9` کے درمیان ایک چیک ہندسہ واپس کرتا ہے، یا اگر مواد `null`، خالی یا غیر عددی ہو تو `-1`۔

---

## تھریڈ کی حفاظت

`GaiaBuilder` تھریڈ-محفوظ **نہیں** ہے اور اسے ایک بار کے استعمال کے لیے بنایا گیا ہے: `create()` بلائیے، AI شامل کیجیے، ایک بار تعمیر کیجیے۔ ہر نتیجے کے لیے نیا بلڈر بنائیے؛ ایک ہی بلڈر کو کئی تھریڈوں میں مشترک نہ کیجیے۔

`BuilderDigitalLinkConfig` (اور اس کے `BuildResult` نتائج) ناقابلِ تبدیل ہیں اور بےتکلف مشترک کیے جا سکتے ہیں — آغاز پر ایک ترتیب بنائیے اور کئی بلڈروں میں اسی کو بار بار استعمال کیجیے۔

---

## API حوالہ

### `GaiaBuilder`

| طریقہ | تفصیل |
|--------|-------------|
| `static GaiaBuilder create()` | نیا، خالی بلڈر شروع کرتا ہے۔ |
| `GaiaBuilder ai(String ai, String value)` | ایک AI اور اس کی مکمل قدر شامل کرتا ہے۔ اگر دونوں میں سے کوئی `null` ہو، یا `ai` کوئی شناختہ GS1 ایپلیکیشن آئیڈینٹیفائر نہ ہو، تو `IllegalArgumentException` پھینکتا ہے۔ |
| `GaiaBuilder language(GaiaConstants.Language language)` | مواد کی توثیق کے خامی پیغامات کی زبان مقرر کرتا ہے (طے شدہ انگریزی)۔ `null` نظر انداز ہو جاتا ہے۔ |
| `String buildElementString()` | ایک GS1 ایلیمنٹ سٹرنگ پیش کرتا ہے۔ ناکامی پر `GaiaBuilderException` پھینکتا ہے۔ |
| `String buildDigitalLinkUri()` | ایک معیاری Digital Link URI پیش کرتا ہے۔ ناکامی پر `GaiaBuilderException` پھینکتا ہے۔ |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` کے مطابق ایک Digital Link URI پیش کرتا ہے۔ ناکامی پر `GaiaBuilderException` پھینکتا ہے۔ |
| `BuildResult tryBuildElementString()` | استثنا نہ پھینکنے والی ایلیمنٹ سٹرنگ تعمیر۔ |
| `BuildResult tryBuildDigitalLinkUri()` | استثنا نہ پھینکنے والی معیاری Digital Link تعمیر۔ |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` کے مطابق استثنا نہ پھینکنے والی Digital Link تعمیر۔ |

### `BuilderDigitalLinkConfig`

| رکن | تفصیل |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | طے شدہ `https://id.gs1.org`۔ |
| `static Builder builder()` | ترتیب کا نیا بلڈر۔ |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | حل شدہ سکیم، حل کنندہ اتھارٹی اور راستے کا سابقہ۔ |
| `getExtraQueryParams()` | اضافی کوئری پیرامیٹر، شمولیت کی ترتیب میں۔ |
| `getFragment()` | فریگمنٹ، یا `null`۔ |

### `GaiaBuilderException`

| رکن | تفصیل |
|--------|-------------|
| `getErrors()` | وہ `GaiaError` آبجیکٹ جنہوں نے ناکامی پیدا کی — مواد کی ناکامی میں پارسر کی خامیاں، یا ایک ہی Digital Link ساختی خامی (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`)۔ کبھی `null` نہیں۔ |

### `BuildResult`

| رکن | تفصیل |
|--------|-------------|
| `isSuccess()` | تعمیر کامیاب رہی یا نہیں۔ |
| `getValue()` | کامیابی پر پیش کیا گیا نتیجہ؛ ناکامی پر `null`۔ |
| `getMessage()` | ناکامی پر ناکامی کی تفصیل؛ کامیابی پر `null`۔ |
| `getErrors()` | ناکامی پر توثیق کی خامیاں؛ کامیابی پر خالی۔ کبھی `null` نہیں۔ |
| `getTiming()` | تعمیری عمل کا `ProcessingTiming` (آغاز کا وقت، کارروائی کا دورانیہ)، یا `null`۔ |

---

مزید دیکھیے: پارسنگ کا پہلو، AI ایلیمنٹ ماڈل، خامیوں کا حوالہ، اور AI و تعبیری مستقلات کے ضمیموں کے لیے **[GaiaParser — ڈیویلپر گائیڈ](GaiaParser-Urdu.md)**۔
