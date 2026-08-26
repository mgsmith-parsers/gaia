# GAIA (GS1 ایپلیکیشن آئیڈینٹیفائر تجزیہ کار) — ڈیویلپر گائیڈ

## فہرست

1. [مجموعی جائزہ](#مجموعی-جائزہ)
2. [GS1 اور General Specifications کے بارے میں](#gs1-اور-general-specifications-کے-بارے-میں)
3. [GS1 ایپلیکیشن آئیڈینٹیفائر](#gs1-ایپلیکیشن-آئیڈینٹیفائر)
4. [فوری آغاز](#فوری-آغاز)
5. [پارسنگ پائپ لائن](#پارسنگ-پائپ-لائن)
   - [ماقبل مرحلہ — ان پٹ موڈیفائر](#ماقبل-مرحلہ--ان-پٹ-موڈیفائر)
   - [مرحلہ 0 — ارتباطی شناخت](#مرحلہ-0--ارتباطی-شناخت)
   - [مرحلہ 1 — مواد کی سمت بندی](#مرحلہ-1--مواد-کی-سمت-بندی)
   - [مرحلہ 2 — نحو](#مرحلہ-2--نحو)
   - [مرحلہ 3 — مواد](#مرحلہ-3--مواد)
   - [مرحلہ 4 — تعبیر](#مرحلہ-4--تعبیر)
6. [پارس کی ترتیب (`ParseConfig`)](#پارس-کی-ترتیب-parseconfig)
   - [اختیارات](#اختیارات)
   - [مقامی زبان میں پیغامات اور عنوانات](#مقامی-زبان-میں-پیغامات-اور-عنوانات)
   - [تاریخ کی ترتیب](#تاریخ-کی-ترتیب)
7. [ان پٹ موڈیفائر](#ان-پٹ-موڈیفائر)
   - [اندرونی موڈیفائر](#اندرونی-موڈیفائر)
   - [ایک موڈیفائر لکھنا](#ایک-موڈیفائر-لکھنا)
   - [موڈیفائر درج کرنا](#موڈیفائر-درج-کرنا)
   - [یہ دیکھنا کہ موڈیفائر نے کیا کیا](#یہ-دیکھنا-کہ-موڈیفائر-نے-کیا-کیا)
   - [موڈیفائر کی ناکامی کا انتظام](#موڈیفائر-کی-ناکامی-کا-انتظام)
8. [پارس وضعیں](#پارس-وضعیں)
   - [DATA_CARRIER وضع](#data_carrier-وضع)
   - [SYNTAX وضع](#syntax-وضع)
   - [CONTENT وضع](#content-وضع)
   - [INTERPRETATION وضع (طے شدہ)](#interpretation-وضع-طے-شدہ)
9. [ارتباطی شناخت](#ارتباطی-شناخت)
10. [GS1 Digital Link](#gs1-digital-link)
11. [نتائج کے ساتھ کام کرنا](#نتائج-کے-ساتھ-کام-کرنا)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry اور DataCarrierType](#datacarrierentry-اور-datacarriertype)
12. [خامیوں کا حوالہ](#خامیوں-کا-حوالہ)
13. [تھریڈ کی حفاظت](#تھریڈ-کی-حفاظت)
14. [ضمیمہ الف — AI سٹرنگ مستقلات](#ضمیمہ-الف--ai-سٹرنگ-مستقلات)
    - [شناخت اور سلسلہ بندی](#شناخت-اور-سلسلہ-بندی)
    - [تاریخیں اور اوقات](#تاریخیں-اور-اوقات)
    - [مقدار اور پیمائش — متغیر پیمائش (میٹرک)](#مقدار-اور-پیمائش--متغیر-پیمائش-میٹرک)
    - [مقدار اور پیمائش — متغیر پیمائش (امپیریل / امریکی)](#مقدار-اور-پیمائش--متغیر-پیمائش-امپیریل--امریکی)
    - [قیمتیں اور مالیاتی رقوم](#قیمتیں-اور-مالیاتی-رقوم)
    - [مقام اور ترسیل](#مقام-اور-ترسیل)
    - [مصنوعات کی خصوصیات اور قابلِ سراغ رسانی](#مصنوعات-کی-خصوصیات-اور-قابل-سراغ-رسانی)
    - [قومی صحت ادائیگی نمبر (NHRN)](#قومی-صحت-ادائیگی-نمبر-nhrn)
    - [صحتِ عامہ، GMN، HIDRI، CPID اور افراد کا ڈیٹا](#صحت-عامہ-gmn-hidri-cpid-اور-افراد-کا-ڈیٹا)
    - [اندرونی / کمپنی کا استعمال](#اندرونی--کمپنی-کا-استعمال)
15. [ضمیمہ ب — تعبیری کلیدی مستقلات](#ضمیمہ-ب--تعبیری-کلیدی-مستقلات)
    - [تاریخ اور وقت](#تاریخ-اور-وقت)
    - [فصل کاٹنے کی تاریخ](#فصل-کاٹنے-کی-تاریخ)
    - [GS1 کمپنی سابقہ](#gs1-کمپنی-سابقہ)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [ملک (ISO 3166)](#ملک-iso-3166)
    - [کرنسی (ISO 4217)](#کرنسی-iso-4217)
    - [درجۂ حرارت](#درجۂ-حرارت)
    - [جنس (ISO 5218)](#جنس-iso-5218)
    - [آبی انواع (FAO)](#آبی-انواع-fao)
    - [NATO سٹاک نمبر (NSN)](#nato-سٹاک-نمبر-nsn)
    - [رول مصنوعات](#رول-مصنوعات)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM شناخت کار (EID / ICCID)](#sim-شناخت-کار-eid--iccid)
    - [سرٹیفکیشن حوالہ](#سرٹیفکیشن-حوالہ)
    - [GS1 UIC](#gs1-uic)
    - [شیرخوار کی پیدائشی ترتیب](#شیرخوار-کی-پیدائشی-ترتیب)
    - [گلوبل ماڈل نمبر (GMN)](#گلوبل-ماڈل-نمبر-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [اعشاری اور پیمائشی قدریں](#اعشاری-اور-پیمائشی-قدریں)
    - [جغرافیائی متناسقات](#جغرافیائی-متناسقات)
    - [پیداوار کا طریقہ](#پیداوار-کا-طریقہ)
    - [AIDC ذریعے کی قسم](#aidc-ذریعے-کی-قسم)
    - [کل میں سے ٹکڑا](#کل-میں-سے-ٹکڑا)
    - [اجزا کی تقسیم](#اجزا-کی-تقسیم)
    - [متفرقات](#متفرقات)

---

## مجموعی جائزہ

`GaiaParser` وہ داخلی نقطہ ہے جہاں سے GS1 ایپلیکیشن آئیڈینٹیفائر (AI) ایلیمنٹ سٹرنگ پارس کی جاتی ہے۔ یہ اسکینر کا خام مواد درج ذیل میں سے کسی بھی شکل میں قبول کرتا ہے اور ایک منظم `ParseResult` واپس کرتا ہے، جس میں حل شدہ تمام AI، توثیق کی خامیاں، اور بطورِ اختیار انسان کے پڑھنے کے قابل تعبیریں شامل ہوتی ہیں:

- سادہ AI ایلیمنٹ سٹرنگ: `0109506000134352`
- AIM سمبولوجی آئیڈینٹیفائر کے سابقے کے ساتھ ایلیمنٹ سٹرنگ: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- ان میں سے کوئی بھی، بطورِ اختیار 8 ہندسوں کی ارتباطی شناخت کے سابقے کے ساتھ: `12345678~0109506000134352`

**داخلی نقطے کی کلاس:** `tools.pantheum.gaia.GaiaParser`

> **Gaia سے نئے متعارف ہو رہے ہیں؟** **[GaiaParser فوری آغاز](GaiaParser-QuickStart-Urdu.md)** سے شروع کیجیے — دس منٹ میں انحصارات، پہلی پارسنگ، اور چند مشہور ٹھوکریں۔ یہ گائیڈ مکمل حوالہ جاتی دستاویز ہے۔

> اس کی الٹی سمت — AI/قدر کے جوڑوں سے درست ایلیمنٹ سٹرنگ اور Digital Link URI *بنانا* — **[GaiaBuilder — ڈیویلپر گائیڈ](GaiaBuilder-Urdu.md)** میں زیرِ بحث ہے۔

---

## GS1 اور General Specifications کے بارے میں

**GS1** ایک عالمی غیر منافع بخش ادارہ ہے جو رسدی سلسلے کی شناخت اور تبادلۂ معلومات کے لیے کھلے معیارات وضع کرتا اور ان کی دیکھ بھال کرتا ہے۔ اس کے معیارات خردہ فروشی، صحتِ عامہ، ترسیل و رسد، غذائی خدمات اور کئی دیگر صنعتوں میں استعمال ہوتے ہیں، اور صارفی پیکنگ پر مصنوعات کے بارکوڈ سے لے کر ادویات کی خوراکوں کی سلسلہ وار نگرانی تک ہر چیز کو محیط ہیں۔

یہ پارسر جو کچھ بھی نافذ کرتا ہے اُس کا مستند حوالہ **GS1 General Specifications** ہے — ایک ہی دستاویز جو درج ذیل کا تعین کرتی ہے:

- تمام ایپلیکیشن آئیڈینٹیفائر (AI) کوڈ، ان کے ڈیٹا عنوانات، اشکال اور توثیق کے اصول
- AI ایلیمنٹ سٹرنگ بنانے اور انکوڈ کرنے کے نحوی اصول
- بارکوڈ سمبولوجی کے تقاضے اور AIM کوڈ شناخت کی تفویض
- چیک ہندسے اور چیک حرف کے الگورتھم
- دو ہندسوں کے سال کا تعین (سرکتی کھڑکی کا اصول)
- Data Matrix، QR Code، GS1-128، GS1 DataBar اور دیگر کیریئروں کی تفصیلات

GS1 General Specifications ہر سال تجدید ہوتی ہیں۔ موجودہ اشاعت اور معاون مواد یہاں دستیاب ہے:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA، GS1 General Specifications کی **ریلیز 26.0 (منظور شدہ، جنوری 2026)** نافذ کرتا ہے۔

GS1 Digital Link URI ایک ہمراہی معیار، **GS1 Digital Link: URI Syntax**، کے تابع ہیں، جو بنیادی شناختی کلیدیں، کلیدی تخصیص کاروں کی ترتیب، اور ڈیٹا خصوصیات کی انکوڈنگ متعین کرتا ہے — پارسر Digital Link کے مواد پر یہی اصول لاگو کرتا ہے:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA، GS1 Digital Link: URI Syntax معیار کی **ریلیز 1.7.0 (منظور شدہ، اگست 2026)** نافذ کرتا ہے۔

اس دستاویز میں ابواب کے حوالے GS1 General Specifications کی طرف اشارہ کرتے ہیں (مثلاً "Table 7-5"، "section 7.12")، سوائے Digital Link کے باب نمبروں کے (مثلاً "§4.9"، "§4.12")، جو GS1 Digital Link: URI Syntax معیار کی طرف اشارہ کرتے ہیں۔

---

## GS1 ایپلیکیشن آئیڈینٹیفائر

**GS1 ایپلیکیشن آئیڈینٹیفائر (AI)** ایک مختصر عددی سابقہ ہے — دو سے چار ہندسے — جو اپنے فوراً بعد آنے والے ڈیٹا کے معنی اور شکل کا تعین کرتا ہے۔ AI، GS1 General Specifications میں متعین ہیں اور رسدی سلسلے کے ڈیٹا کے وسیع دائرے کو محیط ہیں: مصنوعات کے شناخت کار، تاریخیں، مقداریں، لاٹ نمبر، سیریل نمبر، پیمائشیں، URL، اور اس کے علاوہ بھی بہت کچھ۔

### ایک AI ایلیمنٹ کی ساخت

ہر AI ایلیمنٹ دو حصوں پر مشتمل ہوتا ہے:

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

AI کوڈ ہمیشہ عددی ہوتا ہے۔ ڈیٹا کی قدر فوراً اس کے بعد آتی ہے، اور کوڈ و قدر کے درمیان کوئی جداکار نہیں ہوتا۔

### مقررہ طوالت بمقابلہ متغیر طوالت کے AI

AI دو زمروں میں بٹے ہوئے ہیں:

| قسم | طرزِ عمل | مثال |
|---|---|---|
| **مقررہ طوالت** | حروف کی ٹھیک ٹھیک تعداد، ہمیشہ پوری پڑھی جاتی ہے | AI `01` (GTIN) — ہمیشہ 14 ہندسے |
| **متغیر طوالت** | 1 سے زیادہ سے زیادہ تعداد تک؛ کسی GS جداکار یا مواد کے اختتام پر ختم | AI `10` (بیچ/لاٹ) — 1 سے 20 حرفی-عددی حروف |

کوئی AI مقررہ ہے یا متغیر، اس کا فیصلہ صرف GS1 تفصیلات میں دی گئی اس کی تعریف سے ہوتا ہے — پارسر کبھی اندازہ نہیں لگاتا۔

### کثیر-AI ایلیمنٹ سٹرنگ

کئی AI کو ایک ہی ایلیمنٹ سٹرنگ میں جوڑا جا سکتا ہے۔ مقررہ طوالت کے AI براہِ راست جوڑے جا سکتے ہیں، کیونکہ پارسر کو ہمیشہ معلوم ہوتا ہے کہ ٹھیک کتنے حروف پڑھنے ہیں۔ متغیر طوالت کے AI کے بعد جب بھی کوئی اور AI آئے، تو انہیں لازماً **GS حرف** (ASCII `0x1D`، جسے بارکوڈ سمبولوجیوں میں FNC1 بھی کہا جاتا ہے) پر ختم کرنا ضروری ہے، تاکہ پارسر جان سکے کہ ایک قدر کہاں ختم ہوتی ہے اور اگلا AI کوڈ کہاں شروع۔

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

Java سٹرنگ لٹرل میں GS حرف کو یونیکوڈ ایسکیپ `""` سے لکھیے۔

### عام AI

| AI | ڈیٹا عنوان | شکل | قدر کی مثال |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34، واحد مالیاتی خطہ) |
| `710` | NHRN PZN | X..20 | `12345678` |

> 4 ہندسوں والے پیمائشی یا قیمتی AI کا **چوتھا ہندسہ** مضمر اعشاری مقامات کی تعداد انکوڈ کرتا ہے — `3103` کا مطلب ہے 3 اعشاریوں کے ساتھ کلوگرام میں خالص وزن (`001500` = 1.500 kg)، جبکہ `3102` انہی ہندسوں کو 15.00 kg پڑھے گا۔ اوپر کا `شکل` کالم *ڈیٹا* کی شکل دکھاتا ہے؛ ہر AI کے مکمل `getFormatString()` میں خود AI بھی شامل ہوتا ہے (مثلاً `3103` کے لیے `N4+N6`)۔

### انسان کے پڑھنے کے قابل تعبیر (HRI)

روایتی قابلِ مطالعہ شکل میں ہر AI کوڈ کو اس کی قدر سے عین پہلے قوسین میں لپیٹا جاتا ہے، اور ایلیمنٹوں کے درمیان ایک وقفہ رکھا جاتا ہے:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS جداکار HRI میں نہیں دکھایا جاتا۔ یہ شکل `GS1AIObject.toHriString()` بناتا ہے۔

### چار ہندسوں کے AI کوڈ

بعض AI دو کے بجائے چار ہندسے استعمال کرتے ہیں۔ پہلے دو ہندسے AI خاندان کی نشاندہی کرتے ہیں؛ تیسرا اور/یا چوتھا ہندسہ اضافی معنی رکھتا ہے (جیسے پیمائشی AI میں مضمر اعشاریے کا مقام)۔ پارسر ایلیمنٹ سٹرنگ سے پورا AI کوڈ خود بخود اخذ کر لیتا ہے — بلانے والا ہمیشہ پورے کوڈ ہی کے ساتھ کام کرتا ہے (مثلاً `"3102"`، صرف `"31"` نہیں)۔

---

## فوری آغاز

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

> **GS جداکار:** کثیر-AI سٹرنگ کے اندر متغیر طوالت کے AI کو لازماً GS حرف (ASCII `0x1D`) سے محدود کرنا ضروری ہے۔ Java سٹرنگ لٹرل میں `""` استعمال کیجیے۔

---

## پارسنگ پائپ لائن

### ماقبل مرحلہ — ان پٹ موڈیفائر

اگر `ParseConfig` میں کوئی **ان پٹ موڈیفائر** موجود ہوں، تو وہ ہر چیز سے پہلے چلتے ہیں — ارتباطی سابقہ ہٹانے سے پہلے، کیریئر کی شناخت سے پہلے، GS1 پائپ لائن میں داخل ہونے سے پہلے۔ ہر موڈیفائر اگلے کے لیے خام مواد از سرِ نو لکھتا ہے، اور نیچے دیے گئے تمام مراحل اسی زنجیر کے نتیجے پر کام کرتے ہیں۔

بطورِ طے شدہ کوئی موڈیفائر متعین نہیں ہوتا، اس لیے جب تک آپ خود منتخب نہ کریں، یہ ماقبل مرحلہ کچھ نہیں کرتا۔ دیکھیے [ان پٹ موڈیفائر](#ان-پٹ-موڈیفائر)۔

---

### مرحلہ 0 — ارتباطی شناخت

کسی بھی GS1 عمل سے پہلے `GaiaParser` جانچتا ہے کہ آیا مواد کسی اختیاری **ارتباطی شناختی سابقے** سے شروع ہوتا ہے: ٹھیک 8 ASCII اعشاری ہندسے اور ان کے بعد ایک ٹلڈا (`~`)، مثلاً `12345678~`۔

اگر سابقہ موجود ہو تو اسے ہٹا کر واپس کیے جانے والے `ParseResult` پر بطور `CorrelationInfo` محفوظ کر لیا جاتا ہے۔ بعد کے تمام مراحل ہٹائے گئے پے لوڈ پر کام کرتے ہیں۔ اگر کوئی سابقہ نہ ہو تو مواد جوں کا توں گزر جاتا ہے۔

تفصیل کے لیے دیکھیے [ارتباطی شناخت](#ارتباطی-شناخت)۔

---

### مرحلہ 1 — مواد کی سمت بندی

ارتباطی سابقہ ہٹانے کے بعد `GaiaParser` جانچتا ہے کہ آیا (ہٹایا ہوا) مواد کسی **AIM کوڈ شناخت** سے شروع ہوتا ہے: `]` + ASCII حرف + ASCII ہندسہ کی شکل کا تین حرفی سابقہ (مثلاً GS1-128 کے لیے `]C1`، GS1 DataMatrix کے لیے `]d2`، GS1 DataBar / GS1 Composite کے لیے `]e0`)۔

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

اگر کیریئر GS1 AI اٹھانے کے قابل نہ ہو (مثلاً کوئی ڈاک بارکوڈ)، تو پارسنگ فوراً `GE-D002` خامی کے ساتھ رک جاتی ہے۔

---

### مرحلہ 2 — نحو

یہ ہمیشہ چلتا ہے۔ دو ذیلی قدموں پر مشتمل ہے:

**2الف. ٹوکن سازی (`AISyntaxParser`)**
- GS1 سابقہ جدول (GS1 General Specifications Table 7-5) کی مدد سے پہلے دو حروف سے AI کوڈ کی طوالت پڑھتا ہے۔
- مقررہ طوالت کے AI مواد سے بائٹوں کی ٹھیک ٹھیک تعداد پڑھتے ہیں۔
- متغیر طوالت کے AI کسی GS حرف یا مواد کے اختتام تک پڑھے جاتے ہیں۔
- کثیر-جزوی AI کے قدری حصے کو ہر جزو کے مطابق ٹکڑوں میں کاٹا جاتا ہے۔

**2ب. ساختی توثیق (`SyntaxValidator`)**
- مکرر AI کی جانچ (`GE-S004`)۔
- لازمی AI انحصارات کی جانچ، مثلاً AI `02` کے لیے AI `37` درکار ہے (`GE-S005`)۔
- ممنوع AI جوڑوں کی جانچ (`GE-S006`)۔

اس مرحلے کی خامیوں کا درجہ `SYNTAX_ERROR` (ٹوکن ساز) یا `INTEGRITY_ERROR` (ساختی) ہوتا ہے۔ اگر **کوئی ایک بھی** خامی موجود ہو — ٹوکن ساز کی ہو یا ساختی — تو پائپ لائن رک جاتی ہے اور مواد و تعبیر کے مراحل چھوڑ دیے جاتے ہیں۔

---

### مرحلہ 3 — مواد

یہ صرف اسی صورت چلتا ہے جب مرحلہ 2 نے کوئی خامی پیدا نہ کی ہو (نہ ٹوکن ساز کی، نہ ساختی)۔ ہر ایلیمنٹ کے لیے پائپ لائن (ہر قدم صرف اسی وقت چلتا ہے جب پچھلے میں کوئی خامی نہ آئی ہو):

| قدم | توثیق کار | خامی کے کوڈ |
|---|---|---|
| ریگیکس جانچ | `RegexValidator` | `GE-C001` |
| جزو کا حرفی مجموعہ + شکل | `ComponentValidator` | `GE-C005` + ہر شرط کے شکلی کوڈ (`GE-C054`–`GE-C115`) |
| چیک ہندسہ / چیک حرف | `CheckDigitCharacterValidator` | `GE-C003`، `GE-C004` |
| مخصوص معنوی توثیق | `ContentValidatorRegistry` | ہر شرط کے مواد کوڈ (`GE-C116`–`GE-C170`) |

اس مرحلے کی خامیوں کا درجہ `FORMAT_ERROR` یا `DATA_ERROR` ہوتا ہے، سوائے ایک استثنا کے: GS1 کلید
والے AI پر GS1 کمپنی سابقے کی جانچیں محض مشورتی ہیں اور `WARNING` درجہ رکھتی ہیں (دیکھیے
[خامیوں کا حوالہ](#خامیوں-کا-حوالہ))، چنانچہ کوئی غیر شناختہ کمپنی سابقہ بذاتِ خود نتیجے کو
ناقص نہیں بناتا۔

---

### مرحلہ 4 — تعبیر

یہ صرف `INTERPRETATION` وضع میں چلتا ہے، اور صرف اُس وقت جب کسی ایلیمنٹ پر پچھلے کسی مرحلے کی خامی نہ ہو۔ `InterpretationEngine` ہر ایلیمنٹ کو عنوان دار میٹا ڈیٹا سے مالا مال کرتا ہے:

- `dd/mm/yyyy` کی شکل میں ازسرِ نو ترتیب دی گئی تاریخیں
- GTIN چیک ہندسے کا تجزیہ اور GS1 کمپنی سابقے کی تلاش
- ISO 3166 ممالک کے نام
- ISO 4217 کرنسیوں کے نام اور علامتیں
- ضابطہ کشائی شدہ اعشاری رقوم
- HRI (انسان کے پڑھنے کے قابل تعبیر) کے ٹکڑے

نتائج ہر `GS1AIObjectElement` پر `GS1AIInterpretation` اندراجات کے طور پر منسلک ہوتے ہیں۔

---

## پارس کی ترتیب (`ParseConfig`)

`GaiaParser` ٹھیک دو داخلی نقطے مہیا کرتا ہے:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` **طے شدہ ترتیب** کے ساتھ چلتا ہے: `INTERPRETATION` وضع، `/` جداکار اور چار ہندسوں کے سال کے ساتھ چھوٹے-سرے کی تاریخیں (`dd/mm/yyyy`)، اور **انگریزی** خامی پیغامات۔ ان میں سے کسی کو بھی بدلنے کے لیے — بشمول پارس وضع — رواں بلڈر سے ایک `ParseConfig` بنائیے اور دو دلائل والی صورت استعمال کیجیے۔

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

اختیارات کے تمام enum `GaiaConstants` میں ہیں۔

### اختیارات

| بلڈر طریقہ | Enum (`GaiaConstants`) | طے شدہ | اثر |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | پائپ لائن کی گہرائی — دیکھیے [پارس وضعیں](#پارس-وضعیں)۔ |
| `language(...)`      | `Language`      | `ENGLISH`        | خامی پیغامات، تعبیری عنوانات **اور** AI تفصیلات کی زبان۔ |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | تاریخی اجزا کی ترتیب: `LITTLE` (`dd/mm/yyyy`)، `MIDDLE` (`mm/dd/yyyy`)، `BIG` (`yyyy/mm/dd`)۔ |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | تاریخی اجزا کے درمیان حرف: `SLASH` (`/`)، `HYPHEN` (`-`)، `PERIOD` (`.`)۔ |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) یا `THREE_LETTER` (`DEC`)۔ |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) یا `TWO_DIGIT` (`26`)۔ |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | ساختی "درکار ہے" جانچ (`GE-S005`) چھوڑ دیتا ہے۔ |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | ساختی "ممنوع ہے" جانچ (`GE-S006`) چھوڑ دیتا ہے۔ |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / کلاس کا نام | کوئی نہیں | وہ کوڈ جو پارسنگ سے پہلے خام مواد ازسرِ نو لکھتا ہے — دو [اندرونی موڈیفائر](#اندرونی-موڈیفائر) اور آپ کے لکھے ہوئے کوئی بھی۔ دیکھیے [ان پٹ موڈیفائر](#ان-پٹ-موڈیفائر)۔ |

تاریخ کے چاروں اختیارات صرف تعبیری مالا مال کرنے والوں کی بنائی ہوئی مرتب تاریخی سٹرنگ پر اثر ڈالتے ہیں (`INTERPRETATION` وضع میں)؛ یہ توثیق کو نہیں بدلتے۔ بلڈر کی قدریں چھوڑی جا سکتی ہیں — جو اختیار متعین نہ کیا جائے (یا جسے `null` دیا جائے) وہ اپنی طے شدہ قدر پر ہی رہتا ہے۔

### مقامی زبان میں پیغامات اور عنوانات

`language(...)` انسان کے پڑھنے کے قابل **تین** طرح کے متن کی زبان منتخب کرتا ہے: خامی پیغامات، تعبیری عنوانات (ہر `GS1AIInterpretation` کا `getLabel()`)، اور AI تفصیلات (ہر `GS1AIObjectElement` کا `getDescription()`)۔

`GaiaConstants.Language` میں **35 زبانیں** متعین ہیں، جو دنیا کی سب سے زیادہ بولی جانے والی زبانوں کو محیط ہیں: انگریزی، فرانسیسی، ہسپانوی، جرمن، اطالوی، پرتگالی، ولندیزی، پولش، روسی، یوکرینی، چیک، سویڈش، چینی، جاپانی، کوریائی، عربی، انڈونیشیائی، ہندی، ترکی، بنگالی، اردو، ویتنامی، نائجیرین پجن، مصری عربی، مراٹھی، تیلگو، تمل، کینٹونی، وو چینی، تگالوگ، فارسی، ہاؤسا، پنجابی، جاوی اور سواحلی۔

ترجمے کی صورتِ حال (جیسا کہ فراہم کیا گیا ہے):
- **تعبیری عنوانات** — تمام زبانوں کے لیے ترجمہ شدہ۔
- **خامی پیغامات** — تمام زبانوں کے لیے ترجمہ شدہ۔
- **AI تفصیلات** — انگریزی کے سوا تمام زبانوں کے لیے ترجمہ شدہ۔ انگریزی کوئی الگ فہرست نہیں ہے: وہ براہِ راست `gs1-application-identifiers.jsonld` میں اُس AI کے اندراج کے `description` خانے سے پڑھی جاتی ہے، اور بالآخر ہر AI تفصیل اسی پر لوٹتی ہے۔

نائجیرین پجن (`NIGERIAN_PIDGIN`)، جو انگریزی پر مبنی ایک کریول ہے، تعبیری عنوانات اور خامی پیغامات کے لیے جان بوجھ کر انگریزی متن ہی دہراتی ہے۔ AI تفصیلات اسی استثنا کا استثنا ہیں: وہ انگریزی دہرانے کے بجائے حقیقی پجن اسلوب میں ترجمہ شدہ ہیں، کیونکہ AI تفصیلات کی فہرستیں عنوانات/پیغامات کی فہرستوں سے الگ تیار ہوئی تھیں۔ مشینی ترجموں پر عملی ماحول میں بھروسا کرنے سے پہلے انہیں مادری زبان بولنے والوں سے نظرِ ثانی کرا لینا چاہیے۔

کسی زبان کی فہرست میں غیر موجود کوئی بھی پیغام، عنوان یا تفصیل انگریزی پر لوٹ جاتی ہے۔ دائیں سے بائیں لکھی جانے والی زبانیں (عربی، اردو، مصری عربی، فارسی) بطور سٹرنگ درست طور پر محفوظ ہیں؛ انہیں دائیں سے بائیں دکھانا نمائشی طبقے کی ذمہ داری ہے۔

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

تعبیری عنوانات بھی اسی طرح مقامی زبان میں ڈھلتے ہیں (قدریں بدستور رہتی ہیں — صرف عنوانات بدلتے ہیں):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI تفصیلات بھی اسی طرح مقامی زبان میں ڈھلتی ہیں (صرف `getTitle()`، مثلاً `"GTIN"`، مقامی نہیں ہوتا):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### تاریخ کی ترتیب

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## ان پٹ موڈیفائر

**ان پٹ موڈیفائر** وہ کوڈ ہے جو Gaia کے پارس کرنے سے پہلے خام ان پٹ سٹرنگ کو ازسرِ نو لکھتا ہے۔ موڈیفائر اُس مواد کے لیے ہیں جو پہلے ہی بگڑا ہوا پہنچتا ہے — کوئی اسکینر جو GS جداکار کی جگہ کوئی قابلِ طباعت حرف رکھ دیتا ہے، کوئی مڈل ویئر جو پے لوڈ کو کسی فروخت کار کے مخصوص سابقے میں لپیٹ دیتا ہے، کوئی میزبان نظام جو ہر چیز کو بڑے حروف میں بدل دیتا ہے۔ ہر بلاوے کی جگہ ہر سٹرنگ کو پہلے سے سنوارنے (اور ان میں سے کسی ایک میں باریک غلطی کر بیٹھنے) کے بجائے، معیار سازی کو ایک ہی بار `ParseConfig` پر درج کیجیے اور اس کے اطلاق کا کام پارسر پر چھوڑ دیجیے۔

موڈیفائر `GaiaParser.parse(...)` کے بالکل آغاز میں چلتے ہیں — ارتباطی شناخت ہٹانے سے پہلے، AIM کوڈ شناخت کی پہچان سے پہلے، GS1 پائپ لائن سے پہلے۔ اس کے بعد کی ہر چیز صرف ازسرِ نو لکھی ہوئی سٹرنگ ہی دیکھتی ہے۔ دونوں [اندرونی موڈیفائر](#اندرونی-موڈیفائر) سمیت **بطورِ طے شدہ کچھ بھی متعین نہیں ہوتا** — ہر `ParseConfig` پر آپ خود انتخاب کرتے ہیں۔

**انٹرفیس:** `tools.pantheum.gaia.modifier.ModifierInterface`

### اندرونی موڈیفائر

بنیادی jar میں `tools.pantheum.gaia.modifier.custom` کے اندر دو موڈیفائر آتے ہیں۔ یہ اُن دو طریقوں کو سنبھالتے ہیں جن سے GS1 پے لوڈ اکثر بگڑا ہوا پہنچتا ہے — چھپے ہوئے HRI قوسین جنہیں ڈیٹا سمجھ لیا جاتا ہے، اور بےجا وقفے — چنانچہ عام صورتوں کے لیے کوئی مخصوص کلاس لکھنے کی ضرورت نہیں پڑتی:

| کلاس | `getName()` | یہ کیا کرتا ہے |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | ہر AI کے گرد لگے HRI قوسین (`(01)…(10)…`) ہٹاتا ہے اور وہ FNC1 جداکار بحال کرتا ہے جس کی طرف وہ اشارہ کر رہے تھے۔ |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI ایلیمنٹ سٹرنگ سے ہر وقفہ (`0x20`) ہٹا دیتا ہے۔ |

یہ دونوں کسی خاص درجے کے بغیر عام `ModifierInterface` نفاذ ہیں — بالکل آپ کے اپنے لکھے ہوئے موڈیفائروں ہی کی طرح درج ہوتے، ترتیب پاتے، رپورٹ ہوتے اور ناکام ہوتے ہیں:

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

دونوں بےحالت اور تھریڈ-محفوظ ہیں، اس لیے ایک ہی نمونہ سب مل کر استعمال کر سکتے ہیں؛ اور دونوں کو ترتیبی فائل پر مبنی نظاموں کے لیے پورے کلاس نام سے پکارا جا سکتا ہے (دیکھیے [موڈیفائر درج کرنا](#موڈیفائر-درج-کرنا))۔

#### `ModifierRemoveAIBrackets`

GS1 کی انسان کے پڑھنے کے قابل تعبیر ہر AI کو قوسین میں چھاپتی ہے — `(01)09521234543213(10)ABC123` — یہ خالصتاً ایک طباعتی روایت ہے۔ HRI بھیجنے کے لیے متعین کوئی اسکینر یا مڈل ویئر ان قوسین کو ڈیٹا ہی کے طور پر آگے بڑھا دیتا ہے، اور ٹوکن ساز کو کچھ خبر نہیں ہوتی کہ ان کا کیا کرے۔

قوسین ہٹا دینا آدھا کام ہے۔ HRI میں *اگلے* AI کا کھلنے والا `(` ہی پچھلی قدر کے اختتام کی نشاندہی کرتا ہے، اس لیے قوسین والی شکل میں متغیر طوالت کے AI کو FNC1 کی ضرورت نہیں پڑتی۔ قوسین بےسوچے سمجھے ہٹا دیجیے اور وہ سرحد غائب ہو جاتی ہے:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

چنانچہ یہ موڈیفائر **ہر اُس سرحد پر دوبارہ ایک FNC1 ڈالتا ہے جہاں اس سے پہلے والا AI متغیر طوالت کا ہو**، اور بعینہٖ وہی بحال کر دیتا ہے جو قوسین انکوڈ کر رہے تھے:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

طوالت پارسر کی اپنی `AiDefinitionRegistry` میں تلاش کی جاتی ہے، چنانچہ کسی جامد فہرست کے بجائے متغیر طوالت کا ہر AI سنبھالا جاتا ہے۔ تین صورتیں جان بوجھ کر بےچھیڑے چھوڑ دی جاتی ہیں: وہ قدر جو پہلے ہی FNC1 پر ختم ہوتی ہو (دونوں روایتیں بھیجنے والے منبع کو دوسرا جداکار نہیں ملتا)، وہ قوسین والا کوڈ جو کوئی شناختہ AI نہ ہو (نامعلوم AI اپنی طوالت کے بارے میں کچھ نہیں بتاتا)، اور سٹرنگ کا آخری AI۔

یہ ازسرِ نو لکھائی **ہم اثر** ہے — اسے اسی کے اپنے نتیجے پر چلائیے تو کچھ نہیں بدلتا — اس لیے ایسے ملے جلے بہاؤ پر بھی محفوظ ہے جس میں صرف کچھ مواد قوسین والا ہو۔

> **حد۔** `(` اور `)` بذاتِ خود درست GS1 ڈیٹا حروف ہیں، اور یہاں استعمال ہونے والا نمونہ محض `\((\d{2,4})\)` ہے۔ اگر کسی قدر میں اتفاقاً قوسین کے اندر دو سے چار ہندسوں کا کوئی عدد ہو، تو اس کے قوسین بھی کھل جائیں گے۔ اسے صرف اُسی منبع پر لگائیے جو HRI قوسین کی روایت استعمال کرتا ہو، اُس پر نہیں جس میں حقیقتاً قوسین والی قدریں ہوں۔

#### `ModifierRemoveSpaces`

بعض اسکینر، مڈل ویئر اور لیبل چھاپنے کے نظام ایک بصورتِ دیگر خوش ساخت ایلیمنٹ سٹرنگ میں بےجا وقفے ڈال دیتے ہیں — کسی مقررہ چوڑائی کے خانے کو بھرنے، پڑھنے کے قابل گروہ الگ کرنے، یا کسی طویل قدر کو لپیٹنے کے لیے۔ ٹوکن ساز ہر وقفے کو ڈیٹا سمجھتا ہے، جس سے وہ قدر بگڑ جاتی ہے جس کے اندر وہ بیٹھا ہے، اور متغیر طوالت کے AI کی صورت میں اس کے بعد کی ہر چیز سرک جاتی ہے۔

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

صرف ASCII `0x20` ہٹایا جاتا ہے۔ باقی سفید فاصلے اپنی جگہ رہتے ہیں — مثلاً ٹیب GS1 کے قابلِ انکوڈ مجموعے سے باہر ہے، اس لیے پارسر اسے خاموشی سے بہا دینے کے بجائے `GE-S008` کے طور پر رپورٹ کرتا ہے۔

> **حد۔** وقفہ (`0x20`) GS1 کے غیر متبدل حرفی مجموعے کا حصہ ہے، اس لیے کسی بیچ/لاٹ یا گاہک کے پرزہ نمبر میں جائز طور پر بھی وقفہ ہو سکتا ہے۔ موڈیفائر بےجا وقفے اور اصلی وقفے میں فرق نہیں کر سکتا؛ اسے صرف اُسی منبع پر لگائیے جس کے بارے میں معلوم ہو کہ وہ اپنی AI قدروں کے اندر وقفے استعمال نہیں کرتا۔

#### سابقے ازسرِ نو نہیں لکھے جاتے، چھوڑ دیے جاتے ہیں

موڈیفائر اُس وقت چلتے ہیں جب پارسر نے ابھی کچھ بھی نہیں ہٹایا ہوتا، اس لیے خام مواد میں اب بھی ارتباطی شناخت، AIM کوڈ شناخت اور ECI اشاریہ ہو سکتا ہے۔ دونوں اندرونی موڈیفائر پارسر ہی کی `CorrelationIdParser` اور `DataCarrierParser` منطق سے AI ایلیمنٹ سٹرنگ کا آغاز ڈھونڈتے ہیں، صرف وہیں سے آگے ازسرِ نو لکھتے ہیں، اور نتیجے کو **بےچھیڑے** سابقے کے ساتھ دوبارہ جوڑ دیتے ہیں:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

وہ EAN/UPC کیریئر جن کی قدر GTIN-14 تک بھری جاتی ہے (`isRequiresGtinPadding()`)، مکمل طور پر چھوڑ دیے جاتے ہیں — ان کا پے لوڈ کسی AI ساخت کے بغیر خام عددی بارکوڈ قدر ہوتا ہے، چنانچہ وہاں نہ قوسین بامعنی ہو سکتے ہیں نہ وقفے۔

#### ترتیب: قوسین سے پہلے وقفے

جب دونوں استعمال ہوں، تو **پہلے `ModifierRemoveSpaces` درج کیجیے**۔ قوسین کی مطابقت مقام سے حساس ہے: وقفوں سے پھیلا ہوا `( 01 )` `\((\d{2,4})\)` سے میل نہیں کھاتا، چنانچہ قوسین باقی رہ جاتے ہیں اور وہ جداکار جس کی طرف وہ اشارہ کر رہے تھے کبھی بحال نہیں ہوتا۔

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### ایک موڈیفائر لکھنا

جب دونوں اندرونی موڈیفائروں میں سے کوئی بھی موزوں نہ ہو تو اپنا لکھیے — انٹرفیس میں طریقہ صرف ایک ہے۔

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

جب ازسرِ نو لکھائی پارس کی ترتیب پر منحصر ہو، تو اس کے بجائے دو دلائل والی صورت کو اوور رائیڈ کیجیے:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

معاہدہ:

| اصول | تفصیل |
|---|---|
| بےحالت اور تھریڈ-محفوظ | ہر کلاس کا ایک نمونہ محفوظ رکھا جاتا ہے اور ہر پارس میں مشترک ہوتا ہے۔ |
| بغیر دلیل کا عوامی کنسٹرکٹر | صرف اسی وقت درکار جب موڈیفائر کو کلاس کے نام سے پکارا جائے۔ |
| `null` اور خالی مواد سنبھالیے | زنجیر چلنے سے پہلے پارسر انہیں چھانتا نہیں۔ |
| `null` واپس کرنے کا مطلب ہے "کوئی تبدیلی نہیں" | پچھلی قدر ہی آگے بڑھا دی جاتی ہے۔ جب موڈیفائر کا اطلاق نہ ہو تو `input` کو بغیر تبدیلی واپس کیجیے۔ |
| استثنا پھینکنے کے بجائے بغیر تبدیلی واپس کرنا بہتر ہے | استثنا پھینکنے والا موڈیفائر پارسنگ منسوخ کر دیتا ہے — دیکھیے [ناکامی کا انتظام](#موڈیفائر-کی-ناکامی-کا-انتظام)۔ |
| `getName()` | `ModifierInfo` پر رپورٹ ہونے والے نام کو قابو کرنے کے لیے اوور رائیڈ کیجیے؛ طے شدہ قدر سادہ کلاس نام ہے۔ |

### موڈیفائر درج کرنا

موڈیفائر اسی ترتیب سے چلتے ہیں جس میں انہیں شامل کیا جائے، اور ہر ایک کو پچھلے کا نتیجہ ملتا ہے۔ انہیں نمونے سے، پورے کلاس نام سے، یا ان میں سے کسی کی فہرست سے درج کیجیے:

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

[اندرونی موڈیفائروں](#اندرونی-موڈیفائر) کو بھی وہی نام دیا جاتا ہے جو آپ کے اپنے موڈیفائروں کو — **ہمیشہ پورے نام سے**۔ ان کے لیے کوئی مختصر نام یا عرفی تلاش نہیں ہے؛ `ModifierRegistry` ہر موڈیفائر کو، خواہ وہ ساتھ آیا ہو یا نہ آیا ہو، پورے کلاس نام ہی سے حل کرتا ہے۔

نام `ModifierRegistry` حل کرتا ہے، جو ہر کلاس کا ایک نمونہ اس کے بغیر دلیل والے کنسٹرکٹر سے ایک بار بناتا ہے اور اسی کلاس کا نام لینے والی ہر بعد کی ترتیب کے لیے اسے محفوظ رکھتا ہے۔ حل **ترتیب بنتے وقت** ہوتا ہے، چنانچہ وہ نام جو نہ ملے، جو `ModifierInterface` نافذ نہ کرتا ہو، یا جس کا نمونہ نہ بن سکے، وہیں `IllegalArgumentException` پھینکتا ہے — پارس کے وقت خاموشی سے نہیں۔ وہ موڈیفائر جو انعکاس سے نہ بنایا جا سکے (مثلاً جو کوئی انجیکٹ شدہ انحصار رکھتا ہو)، پہلے سے درج کیا جا سکتا ہے تاکہ وہ نام سے پکارے جانے کے قابل رہے:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### یہ دیکھنا کہ موڈیفائر نے کیا کیا

جب موڈیفائر متعین ہوں، تو `ParseResult.getPayload()` **ترمیم شدہ** مواد ظاہر کرتا ہے۔ اصل مواد `ModifierInfo` پر محفوظ رہتا ہے:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` ہر موڈیفائر کا `getName()` رپورٹ کرتا ہے، جس کی طے شدہ قدر سادہ کلاس نام ہے مگر دونوں اندرونی موڈیفائر اسے اوور رائیڈ کرتے ہیں — چنانچہ ان دونوں کی زنجیر کلاس ناموں کے بجائے نمائشی نام رپورٹ کرتی ہے:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

جب کوئی موڈیفائر متعین نہ ہو تو `getModifierInfo()` `null` واپس کرتا ہے۔ اگر موڈیفائر چلے تو ہوں مگر ہر ایک نے مواد بغیر تبدیلی واپس کیا ہو، تو معلومات موجود رہتی ہیں اور `isModified()` `false` ہوتا ہے — `getAppliedModifiers()` میں صرف وہی موڈیفائر درج ہوتے ہیں جنہوں نے واقعی مواد بدلا ہو۔

### موڈیفائر کی ناکامی کا انتظام

استثنا پھینکنے والا موڈیفائر پارسنگ منسوخ کر دیتا ہے۔ وہ استثنا ایک `GaiaModifierException` میں لپیٹ دیا جاتا ہے جو قصوروار موڈیفائر کا نام بتاتا ہے، اور نتیجہ ایک `GE-I001` اندرونی خامی اٹھاتا ہے جس کے پیغام میں وہی نام شامل ہوتا ہے؛ `getPayload()` غیر ترمیم شدہ مواد رپورٹ کرتا ہے۔ پارسنگ جان بوجھ کر آدھی ازسرِ نو لکھی ہوئی سٹرنگ کے ساتھ آگے **نہیں** بڑھتی — خاموشی سے ناکام ہونے والا کوئی معیار سازی کا قدم ایسے نتائج دیتا جو بظاہر درست لگتے مگر غلط مواد سے پارس کیے گئے ہوتے۔

---

## پارس وضعیں

ہر وضع کا نام اُس گہرے ترین [پائپ لائن مرحلے](#پارسنگ-پائپ-لائن) پر ہے جسے وہ چلاتی ہے؛ اس سے پہلے کا ہر مرحلہ پھر بھی چلتا ہے۔

| وضع | کہاں تک چلتی ہے | کس کا جواب دیتی ہے |
|---|---|---|
| `DATA_CARRIER` | مرحلہ 1 (مواد کی سمت بندی) | یہ کس سمبولوجی نے اٹھایا؟ |
| `SYNTAX` | مرحلہ 2 (نحو) | کیا AI کوڈ اور طوالتیں خوش ساخت ہیں؟ |
| `CONTENT` | مرحلہ 3 (مواد) | کیا قدریں درست GS1 ڈیٹا ہیں؟ |
| `INTERPRETATION` | مرحلہ 4 (تعبیر) | قدروں کا مطلب کیا ہے؟ |

### DATA_CARRIER وضع

مرحلہ 1 کے بعد رک جاتی ہے — AIM کوڈ شناخت کی توثیق کرتی اور سمبولوجی پہچانتی ہے، مگر AI پارسنگ پائپ لائن میں داخل نہیں ہوتی۔ مکمل توثیق کے بوجھ کے بغیر سمبولوجی پہچاننے اور سمت بندی کے لیے مفید۔

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

**کب استعمال کریں:** جب آپ کی ایپلیکیشن کو پے لوڈ کے عمل کا طریقہ طے کرنے سے پہلے بارکوڈ کی قسم جاننا ہو — مثلاً 1D بمقابلہ 2D سمبولوجیوں کو مختلف ہینڈلروں تک بھیجنا۔ اس سمت بندی کے لیے `getName()` کا سٹرنگ ملانے کے بجائے قسم دار [`DataCarrierType`](#datacarrierentry-اور-datacarriertype) (`getDataCarrier().getDataCarrierType()`) استعمال کیجیے۔

---

### SYNTAX وضع

مرحلہ 2 کے بعد رک جاتی ہے۔ مواد کی توثیق کی لاگت کے بغیر ساختی ابتدائی چھانٹی کے لیے مفید۔

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

**کب استعمال کریں:** جب آپ مکمل توثیق میں اترنے سے پہلے یہ جانچنا چاہیں کہ AI کوڈ اور ڈیٹا کی طوالتیں خوش ساخت ہیں، یا جب آپ بڑی تعداد میں اسکین کر رہے ہوں جہاں مواد کی خامیاں شاذ ہوں۔

---

### CONTENT وضع

مرحلہ 3 کے بعد رک جاتی ہے۔

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

> بیشتر AI اکیلے کھڑے نہیں ہو سکتے: AI `10` (BATCH/LOT)، `17` (USE BY or EXPIRY) اور
> `21` (SERIAL) — ہر ایک کو اسی ایلیمنٹ سٹرنگ میں AI `01` جیسی کوئی شناختی کلید
> *درکار* ہوتی ہے، چنانچہ اوپر سے GTIN نکال دینے پر مواد کی توثیق تک پہنچنے سے پہلے ہی
> مرحلہ 2 میں `GE-S005` کے ساتھ ناکامی ہو گی۔ جو ٹکڑے جان بوجھ کر اپنے ساتھی AI کے بغیر
> ہوں، انہیں پارس کرنے کے لیے `ParseConfig` پر `skipRequiresCheck(true)` مقرر کیجیے۔

**کب استعمال کریں:** جب آپ کو کسی اسکین شدہ قدر کو کاروباری عمل میں لانے سے پہلے یہ جاننا ہو کہ وہ مکمل طور پر GS1 کے مطابق ہے یا نہیں، مگر تعبیری مالا مالی کا بوجھ نہ اٹھانا ہو۔

---

### INTERPRETATION وضع (طے شدہ)

مرحلہ 4 تک پوری پائپ لائن چلاتی ہے۔ بغیر وضع کی دلیل کے `parse(String)` بلانے پر یہی طے شدہ ہے۔ صرف اُن ایلیمنٹوں کو مالا مال کرتی ہے جو مواد کی توثیق صاف صاف عبور کر چکے ہوں۔

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

**نمونۂ نتیجہ:**
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

**مالیاتی رقم کی مثال (AI 3932 — ISO کرنسی کوڈ کے ساتھ قیمت):**
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

**کب استعمال کریں:** نمائشی طبقے، لیبل کی جانچ کے اوزار، یا کوئی بھی ایسا UI بناتے وقت جسے AI قدروں کا انسان دوست تجزیہ درکار ہو۔

---

## ارتباطی شناخت

بعض ورک فلو خام GS1 مواد سے پہلے ایک اپنی مخصوص 8 ہندسوں کی ارتباطی شناخت لگا دیتے ہیں، تاکہ اسکین کے واقعات کو کسی نشست یا لین دین سے جوڑا جا سکے۔ اس کی شکل یہ ہے:

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

`~` (ٹلڈا) جداکار ہے۔ یہ GS1 مواد کا حصہ **نہیں** ہے — کوئی بھی GS1 پارسنگ شروع ہونے سے پہلے ہی اسے ہٹا دیا جاتا ہے۔

### شناخت کے اصول

سابقہ اُس وقت پہچانا جاتا ہے جب مواد ٹھیک 8 ASCII اعشاری ہندسوں (`0`–`9`) سے شروع ہو اور فوراً بعد `~` ہو۔ اگر نواں حرف `~` نہ ہو، یا پہلے 8 حروف میں سے کوئی ہندسہ نہ ہو، تو مواد کو بغیر ارتباطی سابقے کے سادہ GS1 مواد سمجھا جاتا ہے۔

### ارتباطی شناخت تک رسائی

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

### AIM کوڈ شناخت کے ساتھ ملا کر

ارتباطی سابقہ AIM کوڈ شناخت سے پہلے آ سکتا ہے۔ پارسر اسے بے آواز سنبھال لیتا ہے:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**نفاذ کی کلاس:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

ایک **GS1 Digital Link** ایک یا زیادہ AI قدروں کو براہِ راست کسی HTTP(S) URL کی ساخت کے اندر انکوڈ کر دیتا ہے، جس سے طبعی مصنوعات کے لیے ویب سے حل ہونے والے شناخت کار ممکن ہوتے ہیں۔ GAIA **غیر مضغوط** URI کے لیے *GS1 Digital Link Standard: URI Syntax* (ریلیز 1.7.0) نافذ کرتا ہے۔

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` Digital Link URI کو خود بخود پہچان لیتا ہے — `http://` یا `https://` سے شروع ہونے والا ہر مواد `GS1DLParser` کو بھیجا جاتا ہے، جو ایلیمنٹ سٹرنگ پائپ لائن ہی جیسے مواد اور تعبیر کے مراحل چلاتا ہے۔

### URI کی ساخت اور AI کے کردار

Digital Link URI میں ہر AI تین کرداروں میں سے ایک ادا کرتا ہے، جو ہر `GS1AIObjectElement` پر `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) سے دستیاب ہوتا ہے:

| کردار | مقام | مثال |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | راستے کا پہلا `/ai/value` جوڑا (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | اس کے بعد کے راستہ جوڑے، بنیادی کلید کے مطابق مرتب (§4.9) | `/10/ABC`، `/21/SER` |
| `DATA_ATTRIBUTE` | مکمل عددی کلیدوں والے کوئری پیرامیٹر (§4.10) | `?17=271231` |

نافذ کیے جانے والے ساختی اصول (`DLPathRules`):
- راستے میں ٹھیک **ایک** بنیادی شناختی کلید؛ اضافی کلیدوں کو لازماً کوئری ڈیٹا خصوصیات کے طور پر انکوڈ کرنا ہو گا۔
- کلیدی تخصیص کار بنیادی کلید کے ہاں قابلِ قبول ہونے چاہئیں اور مقررہ ترتیب میں آنے چاہئیں۔ اختیاری تخصیص کار چھوڑے جا سکتے ہیں، مگر جو *موجود* ہوں انہیں پھر بھی طے شدہ ترتیب کی پابندی کرنی ہو گی — دیکھیے [تخصیص کاروں کی ترتیب](#تخصیص-کاروں-کی-ترتیب)۔
- بنیادی کلید سے پہلے کوئی بھی مخصوص راستہ حصے آ سکتے ہیں (مثلاً `/products/au/01/...`)؛ انہیں `getDigitalLinkInfo().getCustomPathStem()` سے حاصل کیجیے۔
- غیر عددی کوئری کلیدیں (`linkType`، `context`، `23P` جیسے توسیعی پیرامیٹر) نظر انداز کر دی جاتی ہیں؛ مکمل عددی کلیدوں کا `validAsDataAttribute` سے نشان زد درست AI ہونا ضروری ہے۔
- فیصد-انکوڈ شدہ قدری حروف کی ضابطہ کشائی کی جاتی ہے؛ AI `(03)` اور `(8014)` کی اجازت نہیں۔

بنیادی کلیدیں اور ان کے قابلِ قبول تخصیص کار تسلسل جامد نہیں بلکہ AI تعریفوں سے **ڈیٹا کی بنیاد پر** آتے ہیں — `gs1DigitalLinkPrimaryKey` نشان اور `gs1DigitalLinkQualifiers` خصوصیت سے۔

کوئی بھی ساختی خلاف ورزی، یا غیر URL مواد، ایک Digital Link ساختی خامی پیدا کرتا ہے (`GE-L001`–`GE-L014`، ہر شرط کے لیے ایک کوڈ)۔ تحلیل شدہ URL میٹا ڈیٹا (`scheme`، `domain`، `path`، `customPathStem`، `query`، اور `java.net.URL`) ساختی خامیوں کی موجودگی میں بھی `getDigitalLinkInfo()` سے دستیاب رہتا ہے۔

### تخصیص کاروں کی ترتیب

ہر بنیادی کلید کے لیے `gs1DigitalLinkQualifiers` ایک یا زیادہ **مرتب** تخصیص کار تسلسل درج کرتا ہے۔ کسی تسلسل کے اندر مربع قوسین میں لپٹا ہوا AI **اختیاری** ہے، اور بغیر قوسین والا AI **لازمی** — یہ §4.9 کے ABNF کی `[cpv-comp]` علامت کاری ہی کا عکس ہے۔ ایک ہی بنیادی کلید کے تسلسل باہم متبادل ہیں اور ایک دوسرے کو رد کرتے ہیں۔

مثلاً GTIN (`01`) دو تسلسل متعین کرتا ہے:

| راستہ | تسلسل | مطلب |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV، LOT، SER — ہر ایک اختیاری، مگر اسی ترتیب میں طے شدہ |
| upui-path | `235` | TPX (لازمی)؛ GTIN + TPX = UPUI |

چنانچہ `/01/09506000134352/10/LOT-ABC/21/SER` درست ہے (SER سے پہلے LOT، CPV چھوڑا گیا)، `/01/.../21/SER/10/LOT-ABC` **مسترد** ہے (ترتیب سے ہٹ کر)، اور `/01/09506000134352/235/2ABC456` upui-path ہے۔ ترتیب کی جانچ ایک ترتیب محفوظ رکھنے والی ذیلی تسلسل مطابقت ہے، چنانچہ اختیاری AI چھوڑے جا سکتے ہیں مگر ان کی ترتیب کبھی بدلی نہیں جا سکتی۔

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

**نفاذ کی کلاس:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## نتائج کے ساتھ کام کرنا

### ParseResult

`GaiaParser.parse()` کا واپس کیا ہوا اعلیٰ ترین درجے کا نتیجہ۔

| طریقہ | کیا واپس کرتا ہے | تفصیل |
|---|---|---|
| `isValid()` | `boolean` | اگر کسی بھی درجے پر کوئی خامی نہ ہو تو `true`۔ تنبیہات درستی پر اثر انداز نہیں ہوتیں۔ جب `getAiObject()` `null` ہو تو ہمیشہ `true`۔ |
| `getPayload()` | `String` | ارتباطی سابقہ ہٹانے کے بعد — اور کسی [ان پٹ موڈیفائر](#ان-پٹ-موڈیفائر) کے ازسرِ نو لکھنے کے بعد — کی ان پٹ سٹرنگ۔ |
| `getPayloadContent()` | `String` | AIM کوڈ شناخت اور ECI سابقہ ہٹانے کے بعد کا پے لوڈ۔ |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`، `GS1_DIGITAL_LINK`، `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (وہ ڈیٹا کیریئر جو GS1 نہ ہونے کی بنا پر مسترد ہوا، مثلاً Code 39 کا `]A0` کیریئر)، یا `UNABLE_TO_DETERMINE_CONTENT` (جب `aiObject` `null` ہو، مثلاً `DATA_CARRIER` وضع میں)۔ |
| `getRequestedParseMode()` | `ParseMode` | متعین کردہ پائپ لائن کی گہرائی (`ParseConfig.getRequestedParseMode()`)۔ |
| `getAchievedParseMode()` | `ParseMode` | وہ گہرا ترین مرحلہ جہاں پارسنگ واقعی پہنچی — نیچے دیکھیے۔ |
| `isParseComplete()` | `boolean` | اگر پارسنگ مطلوبہ گہرائی تک پہنچی ہو تو `true` (`achieved == requested`)۔ `isValid()` سے آزاد۔ |
| `getAiObject()` | `GS1AIObject` | حل شدہ تمام AI۔ `DATA_CARRIER` وضع میں `null`۔ |
| `getErrors()` | `List<GaiaError>` | تمام غیر-WARNING خامیاں (آبجیکٹ درجہ + تمام ایلیمنٹ درجہ)۔ |
| `getWarnings()` | `List<GaiaError>` | تمام WARNING مشورے (آبجیکٹ درجہ + تمام ایلیمنٹ درجہ)۔ |
| `hasWarnings()` | `boolean` | اگر کوئی WARNING مشورہ اٹھا ہو تو `true`۔ |
| `getIssues()` | `List<GaiaError>` | خامیاں اور تنبیہات، دونوں ملا کر۔ |
| `hasDataCarrier()` | `boolean` | اگر کوئی AIM کوڈ شناخت پہچانی گئی ہو تو `true`۔ |
| `getDataCarrier()` | `DataCarrierEntry` | سمبولوجی کا میٹا ڈیٹا، یا اگر کوئی کیریئر شناخت نہ ہوا ہو تو `null`۔ |
| `hasEci()` | `boolean` | اگر پے لوڈ سے کوئی ECI اشاریہ ہٹایا گیا ہو تو `true`۔ |
| `getEci()` | `EciEntry` | ECI انکوڈنگ کا میٹا ڈیٹا، یا `null`۔ |
| `hasCorrelationId()` | `boolean` | اگر اصل مواد میں `DDDDDDDD~` ارتباطی سابقہ موجود تھا تو `true`۔ |
| `getCorrelationInfo()` | `CorrelationInfo` | نکالی گئی ارتباطی شناخت، یا اگر کوئی نہ ہو تو `null`۔ |
| `isInputModified()` | `boolean` | اگر کسی [ان پٹ موڈیفائر](#ان-پٹ-موڈیفائر) نے مواد بدلا ہو تو `true`۔ |
| `getModifierInfo()` | `ModifierInfo` | موڈیفائر زنجیر نے کیا کیا — `getOriginalInput()`، `getModifiedInput()`، `getAppliedModifiers()`۔ اگر کوئی موڈیفائر متعین نہ ہو تو `null`۔ |
| `getTiming()` | `ProcessingTiming` | پارس کا دیواری گھڑی کا وقت — `getStartTime()` (`Instant`)، `getProcessingTime()` (`Duration`)، `getProcessingTimeMillis()` (`long`)، `getCompletionTime()`۔ اگر `GaiaParser` نے نہ بنایا ہو تو `null`۔ |
| `getVersion()` | `String` | وہ لائبریری ورژن جس نے یہ نتیجہ بنایا۔ |

#### مطلوبہ بمقابلہ حاصل شدہ پارس وضع

پائپ لائن **SYNTAX → CONTENT → INTERPRETATION** کی سیڑھی چڑھتی ہے اور خامیوں پر پہلے ہی رک جاتی ہے، اس لیے حقیقتاً *حاصل شدہ* وضع *مطلوبہ* وضع سے کم گہری ہو سکتی ہے۔ `getAchievedParseMode()` بتاتا ہے کہ وہ کہاں تک پہنچی:

| مطلوبہ | کیا ہوتا ہے | حاصل شدہ | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | کوئی **نحوی / ساختی** خامی ٹوکن سازی کے بعد پارسنگ روک دیتی ہے | `SYNTAX` | `false` |
| `INTERPRETATION` | کوئی **مواد** کی خامی (غلط شکل/چیک ہندسہ) مالا مالی روک دیتی ہے | `CONTENT` | `false` |
| `CONTENT` | مواد کا مرحلہ ہمیشہ آخر تک چلتا ہے (خامیاں درج ہوتی ہیں، جان لیوا نہیں) | `CONTENT` | `true` |
| کوئی بھی (صاف مواد) | پائپ لائن مطلوبہ گہرائی تک پہنچ جاتی ہے | = مطلوبہ | `true` |
| `DATA_CARRIER` | کیریئر کی توثیق ہوئی؛ AI مواد پارس نہیں ہوا | `DATA_CARRIER` | `true` |
| کوئی بھی | AI پارسنگ سے پہلے ہی ڈیٹا کیریئر مسترد (مثلاً غیر-GS1 `]A0` کیریئر) | `SYNTAX` | `false` |

`isParseComplete()` `isValid()` سے آزاد ہے: غلط چیک ہندسے والے کسی GTIN کی `CONTENT` پارسنگ **مکمل** ہے (مواد کا مرحلہ چلا) مگر **ناقص** ہے (چیک ہندسہ ناکام ہوا)۔ "کیا پائپ لائن اُتنی گہرائی تک چلی جتنی میں نے مانگی؟" پوچھنے کے لیے `isParseComplete()` اور "کیا ڈیٹا خوش ساخت ہے؟" پوچھنے کے لیے `isValid()` استعمال کیجیے۔

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

حل شدہ AI ایلیمنٹوں کا مجموعہ۔

| طریقہ | تفصیل |
|---|---|
| `getAis()` | تمام `GS1AIObjectElement` نمونے، مواد کی ترتیب میں۔ |
| `get(String aiCode)` | دیے گئے AI کوڈ سے میل کھاتا پہلا ایلیمنٹ، یا `null`۔ |
| `contains(String aiCode)` | اگر اُس کوڈ والا AI موجود ہو تو `true`۔ |
| `size()` | حل شدہ AI کی تعداد۔ |
| `isValid()` | اگر آبجیکٹ درجے پر کوئی خامی نہ ہو اور کسی ایلیمنٹ پر خامی نہ ہو تو `true`۔ |
| `toHriString()` | HRI سٹرنگ، مثلاً `(01)09506000134352 (17)261231`۔ |
| `toElementString()` | خام ایلیمنٹ سٹرنگ — بغیر قوسین کے، متغیر طوالت کے ہر ایلیمنٹ کے بعد FNC1 کے ساتھ — مثلاً `010950600013435210LOT-ABC<GS>17271231`۔ اگر `isValid()` `false` ہو تو `null` واپس کرتا ہے۔ |
| `getContentType()` | `hasDigitalLink()` درست ہو تو `GS1_DIGITAL_LINK`، ورنہ `GS1_APPLICATION_IDENTIFIERS`۔ |
| `hasDigitalLink()` | اگر مواد ایسا GS1 Digital Link URI تھا جس میں بنیادی شناختی کلید تھی تو `true`۔ بنیادی کلید کے بغیر خوش ساخت کوئی URL پھر بھی `getDigitalLinkInfo()` دیتا ہے، مگر یہاں `false` واپس کرتا ہے۔ |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` پر معیاری GS1 Digital Link URI (§4.12) — بنیادی کلید اور تخصیص کار راستہ حصوں کے طور پر، ڈیٹا خصوصیات AI کلید کے مطابق مرتب کوئری پیرامیٹروں کے طور پر — یا اگر کوئی بنیادی کلید نہ ہو تو `null`۔ |
| `getDigitalLinkInfo()` | URI تحلیل کا میٹا ڈیٹا (`getUri()`، `getUrl()`، `scheme`، `domain`، `path`، `getCustomPathStem()`، `query`)، یا Digital Link نہ ہونے پر `null`۔ |
| `getAllErrors()` | آبجیکٹ درجہ + تمام ایلیمنٹ خامیاں (غیر-WARNING)۔ |
| `getAllWarnings()` | آبجیکٹ درجہ + تمام ایلیمنٹ تنبیہات۔ |
| `getAllIssues()` | سب کچھ ملا کر۔ |

---

### GS1AIObjectElement

حل شدہ ایک AI نمونہ۔

| طریقہ | تفصیل |
|---|---|
| `getAi()` | AI کوڈ، مثلاً `"01"`، `"3102"`۔ |
| `getTitle()` | GS1 ڈیٹا عنوان، مثلاً `"GTIN"`، `"BATCH/LOT"`۔ |
| `getDescription()` | AI کی مکمل GS1 تفصیل، **پارس زبان میں مقامی** (انگریزی میں مثلاً `"Global Trade Item Number (GTIN)"`)۔ ترجمہ نہ ہونے پر AI تعریف کے انگریزی متن پر لوٹ جاتی ہے۔ |
| `getFormatString()` | AI *اور* اس کے ڈیٹا — دونوں کو محیط شکلی بیان، مثلاً AI `01` کے لیے `"N2+N14"`، AI `10` کے لیے `"N2+X..20"`، AI `3932` کے لیے `"N4+N3+N..15"`۔ |
| `getValue()` | ایلیمنٹ سٹرنگ سے نکالی گئی خام ڈیٹا قدر۔ |
| `isFixedLength()` | اگر AI کے ڈیٹا کی طوالت مقررہ ہو تو `true`۔ |
| `getPosition()` | اصل مواد میں صفر سے شروع ہونے والا حرفی مقام۔ |
| `getGS1ComponentValues()` | ہر جزو کے قدری ٹکڑے (کثیر-جزوی AI کے لیے)۔ |
| `getErrors()` | ایلیمنٹ درجے کی غیر-WARNING خامیاں۔ |
| `getWarnings()` | ایلیمنٹ درجے کے WARNING مشورے۔ |
| `getIssues()` | ایلیمنٹ درجے کی خامیاں اور تنبیہات، ملا کر۔ |
| `hasErrors()` | اگر کوئی غیر-WARNING خامی منسلک ہو تو `true`۔ |
| `hasWarnings()` | اگر کوئی WARNING مشورہ منسلک ہو تو `true`۔ |
| `getInterpretations()` | `GS1AIInterpretation` اندراجات (INTERPRETATION وضع میں بھرے جاتے ہیں)۔ |
| `getInterpretation(String type)` | دی گئی `GS1Constants_Enricher` قسمی کلید سے میل کھاتی پہلی تعبیر، یا `null`۔ |
| `getDigitalLinkAIType()` | ایلیمنٹ کا Digital Link کردار (`PRIMARY_IDENTIFICATION_KEY`، `KEY_QUALIFIER`، `DATA_ATTRIBUTE`)، یا ایلیمنٹ سٹرنگ مواد کے لیے `null`۔ |
| `hasDigitalLinkAIType()` | اگر کوئی Digital Link کردار تفویض ہوا ہو تو `true`۔ |

---

### GaiaError

ایک ناقابلِ تبدیل توثیقی خامی یا مشورہ۔

| طریقہ | تفصیل |
|---|---|
| `getId()` | فہرست کا شناخت کار، مثلاً `"GE-C003"`۔ |
| `getLevel()` | `SYNTAX_ERROR`، `INTEGRITY_ERROR`، `FORMAT_ERROR`، `DATA_ERROR`، یا `WARNING`۔ |
| `getStage()` | `DATA_CARRIER`، `DIGITAL_LINK`، `SYNTAX`، `CONTENT`، یا `INTERNAL`۔ |
| `getCode()` | مشین کے پڑھنے کے قابل مختصر کوڈ۔ |
| `getAi()` | وہ AI کوڈ جس نے خامی پیدا کی، یا آبجیکٹ درجے کی خامیوں کے لیے `null`۔ |
| `getMessage()` | قدریں بھرا ہوا، انسان کے پڑھنے کے قابل پیغام۔ |
| `getPosition()` | اصل مواد میں صفر سے شروع ہونے والا حرفی مقام۔ |

---

### GS1AIInterpretation

`INTERPRETATION` وضع میں کسی `GS1AIObjectElement` سے منسلک ایک عنوان دار تعبیری ٹکڑا۔

| طریقہ | تفصیل |
|---|---|
| `getType()` | مشین کے پڑھنے کے قابل قسمی کلید، مثلاً `"DATE_VALUE"`، `"GS1_COMPANY_PREFIX"`۔ تمام زبانوں میں یکساں۔ |
| `getLabel()` | انسان کے پڑھنے کے قابل عنوان، **پارس زبان میں مقامی** (انگریزی میں مثلاً `"Date"` / `"GS1 company prefix"`)۔ |
| `getValue()` | نکالی/مالا مال کی گئی قدر، مثلاً `"31/12/2026"`، `"9506000"`۔ مقامی نہیں کی جاتی۔ |

---

### DataCarrierEntry اور DataCarrierType

جب مواد کوئی AIM کوڈ شناخت اٹھائے ہو، تو `ParseResult.getDataCarrier()` ایک `DataCarrierEntry` واپس کرتا ہے جو اُس علامت کا بیان کرتا ہے جس نے ڈیٹا اٹھایا تھا۔ یہ اندراج میل کھانے والی AIM کوڈ شناخت کا مخصوص رجسٹری ریکارڈ ہے؛ جبکہ `DataCarrierType` وہ تالیفی وقت کا enum ہے جس سے اس کا تعلق ہے۔

#### DataCarrierEntry

پہچانی گئی ایک AIM کوڈ شناخت کا میٹا ڈیٹا (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`)۔

| طریقہ | تفصیل |
|---|---|
| `getAimCodeId()` | وہ AIM کوڈ شناخت جو میل کھائی، مثلاً `"]C1"`۔ |
| `getName()` | مخصوص علامت کا انسان کے پڑھنے کے قابل نام، مثلاً `"GS1-128 / ISBT 128"`، `"EAN-8"`۔ |
| `getDescription()` | کیریئر کی طویل تر تفصیل۔ |
| `getType()` | کیریئر کی ساختی قسم، بطور سٹرنگ (`getDataCarrierType().getCategory()` کا عکس)۔ |
| `getStandard()` | سمبولوجی کا معیار، جہاں درج ہو۔ |
| `getDataCarrierType()` | اس اندراج کے لیے قسم دار `DataCarrierType` — پروگرامی سمت بندی کے لیے یہی استعمال کیجیے۔ |
| `isGs1Capable()` | اگر کیریئر GS1 ڈیٹا اٹھا سکتا ہو تو `true` (AI ایلیمنٹ سٹرنگ اور/یا Digital Link)۔ |
| `isGs1AICapable()` | اگر کیریئر GS1 AI ایلیمنٹ سٹرنگ اٹھا سکتا ہو تو `true`۔ |
| `isGs1DigitalLinkCapable()` | اگر کیریئر کوئی GS1 Digital Link URI اٹھا سکتا ہو تو `true`۔ |
| `isEciCapable()` | اگر کیریئر ECI اشاریے کی حمایت کرتا ہو تو `true`۔ |
| `isRequiresGtinPadding()` | اُن EAN/UPC/ITF کیریئروں کے لیے `true` جن کی عددی قدر AI پارسنگ سے پہلے GTIN-14 تک بھری جاتی ہے۔ |

#### DataCarrierType

ڈیٹا کیریئر کی اقسام کا تالیفی وقت کا enum، جس کی کلید ISO/IEC 15424 میں تفویض کردہ AIM کوڈ شناخت ہے (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`)۔ `]` کے بعد کا حرف (*کوڈ حرف*) خاندان منتخب کرتا ہے؛ بیشتر خاندان ایک ہی مستقل پر منطبق ہوتے ہیں جو ہر ترمیم کار کو محیط ہوتا ہے (`ITF` میں `]I0`–`]I2` آتے ہیں؛ `EAN_UPC` میں EAN-13، UPC-A، UPC-E اور EAN-8)۔ جہاں GS1 کوئی ترمیم کار AI ڈیٹا کے لیے مخصوص رکھتا ہے، وہاں وہ صورت اپنا الگ مستقل ہے — `GS1_128` (`]C1`)، `GS1_DATA_MATRIX` (`]d2`)، `GS1_QR_CODE` (`]Q3`)، `GS1_DOT_CODE` (`]J1`) — اپنے سادہ ہم منصبوں سے الگ۔ جب کوئی AIM کوڈ شناخت موجود نہ ہو، یا وہ کسی نامعلوم کیریئر کا نام لے، تو قسم `UNKNOWN` ہوتی ہے۔

| طریقہ | تفصیل |
|---|---|
| `getCategory()` | وسیع زمرہ `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`، `STACKED_LINEAR`، `TWO_D`، `POSTAL`، `OCR`، یا `OTHER`۔ |
| `getCodeChar()` | خاندان کی نشاندہی کرنے والا AIM کوڈ حرف، مثلاً QR Code کے لیے `"Q"`؛ `UNKNOWN` کے لیے `null`۔ |
| `getDisplayName()` | *قسم* کا انسان کے پڑھنے کے قابل نام (`DataCarrierEntry.getName()` سے وسیع تر ہو سکتا ہے — مثلاً `"EAN-13 / UPC-A / UPC-E / EAN-8"` بمقابلہ `"EAN-8"`)۔ |
| `isGs1DataCarrier()` | اُن مستقلات کے لیے `true` جو ہمیشہ GS1 AI ڈیٹا ظاہر کرتے ہیں: GS1 کے لیے مخصوص چار صورتیں (`GS1_128`، `GS1_DATA_MATRIX`، `GS1_QR_CODE`، `GS1_DOT_CODE`) اور مزید `GS1_DATABAR`، جو بذاتِ خود GS1 ہے کیونکہ ہر `]e` ترمیم کار GS1 DataBar ہی ہے۔ یہ `DataCarrierEntry.isGs1AICapable()` سے تنگ تر ہے — کوئی سادہ `QR_CODE` بھی GS1 AI ڈیٹا اٹھا سکتا ہے۔ |
| `static forAimCodeId(String)` | قسم براہِ راست کسی AIM کوڈ شناخت سے حل کرتا ہے (`"]Q3"` → `GS1_QR_CODE`؛ `"]Q9"` → `QR_CODE`)؛ غیر موجود، بگڑی ہوئی یا غیر شناختہ شناخت کے لیے `UNKNOWN` واپس کرتا ہے۔ |

نام کے بجائے قسم کے مطابق سمت بندی — مثلاً خطی (Code-128) علامتوں کو 2D (QR / Data Matrix) سے الگ کرنا:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` میں صرف میٹرکس اور نقطہ علامتیں آتی ہیں؛ تہہ دار-خطی کیریئر (`PDF417`،
`CODE_16K`، `CODABLOCK`، `CODE_49`) `STACKED_LINEAR` ہیں، اگرچہ انہیں عام طور پر "2D"
بارکوڈ کہا جاتا ہے۔ دونوں کو ایک ہی گروہ سمجھنے کے لیے — مثلاً یہ طے کرنے کے لیے کہ لیزر
اسکینر کے بجائے امیجر درکار ہے یا نہیں — دونوں زمرے جانچیے۔

> قسم کے حل کے لیے اسکین میں AIM کوڈ شناخت کا موجود ہونا ضروری ہے؛ اس کے بغیر `getDataCarrier()` `null` اور قسم `UNKNOWN` ہوتی ہے۔ اسکینر کو AIM کوڈ شناخت کا سابقہ بھیجنے کے لیے متعین کیجیے۔

---

## خامیوں کا حوالہ

| کوڈ | درجہ | مرحلہ | مطلب |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | نامعلوم AI سابقہ — ڈیٹا کی طوالت متعین نہیں ہو سکتی |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | مکمل AI کوڈ پڑھنے کے لیے مواد بہت مختصر |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | کٹی ہوئی قدر — AI کی ضرورت سے کم حروف |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | ایلیمنٹ سٹرنگ میں مکرر ایپلیکیشن آئیڈینٹیفائر |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | لازمی AI انحصار غائب |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | ممنوع AI جوڑا — دو ایسے AI جو ساتھ نہیں آ سکتے |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | غیر متوقع ٹوکن سازی کی ناکامی |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | ایلیمنٹ سٹرنگ میں GS1 کے قابلِ انکوڈ مجموعے سے باہر کا حرف |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | متغیر طوالت کے AI کے بعد لازمی FNC1 جداکار غائب |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | تمام اجزا کی زیادہ سے زیادہ حدوں سے آگے بچا ہوا ڈیٹا |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | سٹرنگ کے بیچ میں مقررہ طوالت کے AI کے بعد FNC1 جداکار |
| `GE-W002` | WARNING | SYNTAX | ایلیمنٹ سٹرنگ کے آخر میں بچا ہوا FNC1 (محض مشورہ) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI کی ساختی خلاف ورزیاں — ہر شرط کے لیے ایک کوڈ (بگڑا ہوا URI، سکیم، میزبان، تخصیص کاروں کی ترتیب، ممنوع AI، کوئی بنیادی کلید نہیں (`GE-L013`)، ایک سے زیادہ بنیادی کلیدیں (`GE-L014`)، …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | قدر AI کے ریگیکس نمونے پر پوری نہیں اترتی |
| `GE-C003` | DATA_ERROR | CONTENT | چیک ہندسے کی توثیق ناکام |
| `GE-C004` | DATA_ERROR | CONTENT | چیک حرفی جوڑے کی توثیق ناکام |
| `GE-C005` | FORMAT_ERROR | CONTENT | جزو کی قدر میں اجازت یافتہ حرفی مجموعے سے باہر کا حرف |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | جزوی شکل کی ناکامیاں — ہر توثیق کار شرط کے لیے ایک کوڈ (دیکھیے `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | مخصوص معنوی توثیق کی ناکامیاں — ہر توثیق کار شرط کے لیے ایک کوڈ (دیکھیے `content/validator/`)۔ **استثنائات:** نیچے درج 14 GS1 کمپنی سابقہ جانچیں `WARNING` درجہ رکھتی ہیں، اور `GE-C168` (غیر شناختہ ISO 3166-1 عددی ملکی کوڈ) `FORMAT_ERROR` درجہ رکھتا ہے۔ |
| GS1 کمپنی سابقہ جانچیں | WARNING | CONTENT | GS1 کلید والے AI میں کلید کسی شناختہ GS1 کمپنی سابقے سے شروع نہیں ہوتی — `GE-C122` (CPID)، `GE-C129` (GCN)، `GE-C131` (GDTI)، `GE-C132` (GIAI)، `GE-C133` (GINC)، `GE-C135` (GLN)، `GE-C137` (GMN)، `GE-C140` (GRAI)، `GE-C142` (GSIN)، `GE-C144` (GSRN)، `GE-C146` (GTIN)، `GE-C148` (HIDRI)، `GE-C153` (ITIP)، `GE-C165` (SSCC)۔ محض مشورہ — درستی پر اثر انداز نہیں۔ |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2) پر IMEI چیک ہندسہ (Luhn) ناکام |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM) پر EID چیک ہندسہ (Luhn) ناکام |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | غیر شناختہ AIM کوڈ شناخت |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | کیریئر شناخت ہو گیا مگر وہ نہ GS1 AI ایلیمنٹ سٹرنگ کی حمایت کرتا ہے نہ Digital Link URI کی |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | غیر متوقع اندرونی خامی |

> **پیغام کی نمائش میں ایک معلوم نقص۔** فہرست کے سانچے بھری ہوئی قدروں کو MessageFormat
> طرز کے دوہرے واوین (`''{value}''`) میں لکھتے ہیں، مگر `ErrorRegistry` قدریں سادہ
> `String.replace` سے بھرتا ہے، چنانچہ یہ دوہراپن `getMessage()` تک باقی رہ جاتا ہے —
> اس گائیڈ میں نقل کیے گئے پیغامات جہاں `value '09506000134351'` دکھاتے ہیں، وہاں فی الحال
> آپ کو `value ''09506000134351''` نظر آئے گا۔ یہ 35 زبانوں کی فہرستوں میں قدر لکھنے والے
> ہر پیغام کو متاثر کرتا ہے۔ خامی پیغامات پارس نہ کیجیے؛ `getId()` / `getCode()` پر
> مطابقت کیجیے۔

---

## تھریڈ کی حفاظت

`GaiaParser` ایک بار بن جانے کے بعد تھریڈ-محفوظ ہے۔ ایک ہی نمونہ کئی تھریڈوں میں مشترک اور بیک وقت استعمال کیا جا سکتا ہے۔ تجویز کردہ طریقہ یہ ہے کہ ایپلیکیشن کے آغاز پر ایک نمونہ بنا کر اسی کو بار بار استعمال کیا جائے:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` ناقابلِ تبدیل ہے اور اشتراک کے لیے اتنا ہی محفوظ۔ تھریڈ کی حفاظت کی واحد ذمہ داری جو لائبریری آپ کی طرف سے نہیں نبھا سکتی وہ [ان پٹ موڈیفائروں](#ان-پٹ-موڈیفائر) پر ہے: ہر موڈیفائر کا ایک ہی نمونہ محفوظ رکھ کر بیک وقت چلنے والی ہر پارسنگ میں مشترک کیا جاتا ہے، اس لیے نفاذات کا بےحالت ہونا لازم ہے۔

---

## ضمیمہ الف — AI سٹرنگ مستقلات

`GS1Constants_AICodes` (پیکیج `tools.pantheum.gaia.gs1.constants`) GAIA کے پہچانے ہوئے ہر ایپلیکیشن آئیڈینٹیفائر کے لیے ایک `String` مستقل کا اعلان کرتا ہے۔ کوڈ میں AI کوڈ کے لٹرل جڑنے کے بجائے یہی مستقلات استعمال کیجیے:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

ہر مستقل اپنے AI کوڈ کی سٹرنگ صورت رکھتا ہے (مثلاً `AI_01_GTIN = "01"`)۔

### شناخت اور سلسلہ بندی

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | سیریل شپنگ کنٹینر کوڈ (SSCC). |
| `01` | `AI_01_GTIN` | گلوبل ٹریڈ آئٹم نمبر (GTIN). |
| `02` | `AI_02_CONTENT` | شامل تجارتی اشیاء کا گلوبل ٹریڈ آئٹم نمبر (GTIN). |
| `03` | `AI_03_MTO_GTIN` | میڈ ٹو آرڈر (MtO) تجارتی شے کی شناخت (GTIN). |
| `10` | `AI_10_BATCH_LOT` | بیچ یا لاٹ نمبر. |
| `20` | `AI_20_VARIANT` | اندرونی پروڈکٹ ویریئنٹ. |
| `21` | `AI_21_SERIAL` | سیریل نمبر. |
| `22` | `AI_22_CPV` | صارف پروڈکٹ ویریئنٹ. |
| `235` | `AI_235_TPX` | تھرڈ پارٹی کنٹرولڈ، گلوبل ٹریڈ آئٹم نمبر (GTIN) کی سیریلائزڈ ایکسٹینشن (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | مینوفیکچرر کی جانب سے تفویض کردہ اضافی پروڈکٹ شناخت. |
| `241` | `AI_241_CUST_PART_NO` | کسٹمر پارٹ نمبر. |
| `242` | `AI_242_MTO_VARIANT` | میڈ ٹو آرڈر ویریئیشن نمبر. |
| `243` | `AI_243_PCN` | پیکیجنگ جزو نمبر. |
| `250` | `AI_250_SECONDARY_SERIAL` | ثانوی سیریل نمبر. |
| `251` | `AI_251_REF_TO_SOURCE` | ماخذ ادارے کا حوالہ. |
| `253` | `AI_253_GDTI` | گلوبل دستاویز نوعیت شناخت کنندہ (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | گلوبل لوکیشن نمبر (GLN) ایکسٹینشن جزو. |
| `255` | `AI_255_GCN` | گلوبل کوپن نمبر (GCN). |
| `30` | `AI_30_VAR_COUNT` | اشیاء کی متغیر تعداد (متغیر پیمائش تجارتی شے). |
| `37` | `AI_37_COUNT` | لاجسٹک یونٹ میں شامل تجارتی اشیاء یا تجارتی شے کے ٹکڑوں کی تعداد. |

### تاریخیں اور اوقات

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | پیداوار کی تاریخ (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | مقررہ تاریخ (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | پیکیجنگ کی تاریخ (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | بہترین استعمال کی تاریخ (YYMMDD). |
| `16` | `AI_16_SELL_BY` | فروخت کی آخری تاریخ (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | میعاد ختم ہونے کی تاریخ (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | ڈیلیوری کی ابتدائی ترین تاریخ و وقت (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | ڈیلیوری کی آخری قابل قبول تاریخ و وقت (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | ریلیز کی تاریخ (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | میعاد ختم ہونے کی تاریخ اور وقت (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | پہلی منجمد کرنے کی تاریخ (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | کٹائی کی تاریخ (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | ٹیسٹ کی تاریخ (YYMMDD[hhmm]). |

### مقدار اور پیمائش — متغیر پیمائش (میٹرک)

چار ہندسوں کے AI خاندان `310n`–`369n` متغیر پیمائش کی مقداریں انکوڈ کرتے ہیں۔ تیسرا ہندسہ پیمائش کی قسم منتخب کرتا ہے؛ **چوتھا ہندسہ** (`n`، 0–5) مضمر اعشاری مقامات کی تعداد ہے — یعنی `AI_3102_NET_WEIGHT_KG` کا مطلب ہے 2 اعشاری مقامات کے ساتھ کلوگرام میں خالص وزن۔

| خاندان | مستقل کا نمونہ (`n` = اعشاری ہندسہ) | تفصیل |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | خالص وزن، کلوگرام (متغیر پیمائش تجارتی شے). |
| `311n` | `AI_311n_LENGTH_M` | لمبائی یا پہلی جہت، میٹر (متغیر پیمائش تجارتی شے). |
| `312n` | `AI_312n_WIDTH_M` | چوڑائی، قطر، یا دوسری جہت، میٹر (متغیر پیمائش تجارتی شے). |
| `313n` | `AI_313n_HEIGHT_M` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، میٹر (متغیر پیمائش تجارتی شے). |
| `314n` | `AI_314n_AREA_M` | رقبہ، مربع میٹر (متغیر پیمائش تجارتی شے). |
| `315n` | `AI_315n_NET_VOLUME_L` | خالص حجم، لیٹر (متغیر پیمائش تجارتی شے). |
| `316n` | `AI_316n_NET_VOLUME_M` | خالص حجم، مکعب میٹر (متغیر پیمائش تجارتی شے). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | لاجسٹک وزن، کلوگرام. |
| `331n` | `AI_331n_LENGTH_M_LOG` | لمبائی یا پہلی جہت، میٹر. |
| `332n` | `AI_332n_WIDTH_M_LOG` | چوڑائی، قطر، یا دوسری جہت، میٹر. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، میٹر. |
| `334n` | `AI_334n_AREA_M_LOG` | رقبہ، مربع میٹر. |
| `335n` | `AI_335n_VOLUME_L_LOG` | لاجسٹک حجم، لیٹر. |
| `336n` | `AI_336n_VOLUME_M_LOG` | لاجسٹک حجم، مکعب میٹر. |
| `337n` | `AI_337n_KG_PER_M` | کلوگرام فی مربع میٹر. |

### مقدار اور پیمائش — متغیر پیمائش (امپیریل / امریکی)

| خاندان | مستقل کا نمونہ (`n` = اعشاری ہندسہ) | تفصیل |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | خالص وزن، پاؤنڈ (متغیر پیمائش تجارتی شے). |
| `321n` | `AI_321n_LENGTH_IN` | لمبائی یا پہلی جہت، انچ (متغیر پیمائش تجارتی شے). |
| `322n` | `AI_322n_LENGTH_FT` | لمبائی یا پہلی جہت، فٹ (متغیر پیمائش تجارتی شے). |
| `323n` | `AI_323n_LENGTH_YD` | لمبائی یا پہلی جہت، گز (متغیر پیمائش تجارتی شے). |
| `324n` | `AI_324n_WIDTH_IN` | چوڑائی، قطر، یا دوسری جہت، انچ (متغیر پیمائش تجارتی شے). |
| `325n` | `AI_325n_WIDTH_FT` | چوڑائی، قطر، یا دوسری جہت، فٹ (متغیر پیمائش تجارتی شے). |
| `326n` | `AI_326n_WIDTH_YD` | چوڑائی، قطر، یا دوسری جہت، گز (متغیر پیمائش تجارتی شے). |
| `327n` | `AI_327n_HEIGHT_IN` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، انچ (متغیر پیمائش تجارتی شے). |
| `328n` | `AI_328n_HEIGHT_FT` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، فٹ (متغیر پیمائش تجارتی شے). |
| `329n` | `AI_329n_HEIGHT_YD` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، گز (متغیر پیمائش تجارتی شے). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | لاجسٹک وزن، پاؤنڈ. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | لمبائی یا پہلی جہت، انچ. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | لمبائی یا پہلی جہت، فٹ. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | لمبائی یا پہلی جہت، گز. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | چوڑائی، قطر، یا دوسری جہت، انچ. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | چوڑائی، قطر، یا دوسری جہت، فٹ. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | چوڑائی، قطر، یا دوسری جہت، گز. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، انچ. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، فٹ. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | گہرائی، موٹائی، اونچائی، یا تیسری جہت، گز. |
| `350n` | `AI_350n_AREA_IN` | رقبہ، مربع انچ (متغیر پیمائش تجارتی شے). |
| `351n` | `AI_351n_AREA_FT` | رقبہ، مربع فٹ (متغیر پیمائش تجارتی شے). |
| `352n` | `AI_352n_AREA_YD` | رقبہ، مربع گز (متغیر پیمائش تجارتی شے). |
| `353n` | `AI_353n_AREA_IN_LOG` | رقبہ، مربع انچ. |
| `354n` | `AI_354n_AREA_FT_LOG` | رقبہ، مربع فٹ. |
| `355n` | `AI_355n_AREA_YD_LOG` | رقبہ، مربع گز. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | خالص وزن، ٹرائے اونس (متغیر پیمائش تجارتی شے). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | خالص وزن (یا حجم)، اونس (متغیر پیمائش تجارتی شے). |
| `360n` | `AI_360n_NET_VOLUME_QT` | خالص حجم، کوارٹ (متغیر پیمائش تجارتی شے). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | خالص حجم، امریکی گیلن (متغیر پیمائش تجارتی شے). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | لاجسٹک حجم، کوارٹ. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | لاجسٹک حجم، امریکی گیلن. |
| `364n` | `AI_364n_NET_VOLUME_IN` | خالص حجم، مکعب انچ (متغیر پیمائش تجارتی شے). |
| `365n` | `AI_365n_NET_VOLUME_FT` | خالص حجم، مکعب فٹ (متغیر پیمائش تجارتی شے). |
| `366n` | `AI_366n_NET_VOLUME_YD` | خالص حجم، مکعب گز (متغیر پیمائش تجارتی شے). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | لاجسٹک حجم، مکعب انچ. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | لاجسٹک حجم، مکعب فٹ. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | لاجسٹک حجم، مکعب گز. |

### قیمتیں اور مالیاتی رقوم

چوتھا ہندسہ (`n`) مضمر اعشاری مقامات کی تعداد انکوڈ کرتا ہے۔ اجازت یافتہ حد
خاندان کے لحاظ سے مختلف ہے — `n` کالم دیکھیے۔

| خاندان | مستقل کا نمونہ (`n` = اعشاری ہندسہ) | `n` | تفصیل |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | قابل اطلاق قابل ادائیگی رقم یا کوپن ویلیو، مقامی کرنسی. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | آئی ایس او کرنسی کوڈ کے ساتھ قابل اطلاق قابل ادائیگی رقم. |
| `392n` | `AI_392n_PRICE` | 0–9 | قابل اطلاق قابل ادائیگی رقم، واحد کرنسی علاقہ (متغیر پیمائش تجارتی شے). |
| `393n` | `AI_393n_PRICE` | 0–9 | آئی ایس او کرنسی کوڈ کے ساتھ قابل اطلاق قابل ادائیگی رقم (متغیر پیمائش تجارتی شے). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | کوپن کا فیصد رعایت. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | فی یونٹ پیمائش قابل ادائیگی رقم، واحد کرنسی علاقہ (متغیر پیمائش تجارتی شے). |

### مقام اور ترسیل

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | کسٹمر خریداری آرڈر نمبر. |
| `401` | `AI_401_GINC` | کھیپ کے لیے گلوبل شناختی نمبر (GINC). |
| `402` | `AI_402_GSIN` | گلوبل شپمنٹ شناختی نمبر (GSIN). |
| `403` | `AI_403_ROUTE` | روٹنگ کوڈ. |
| `410` | `AI_410_SHIP_TO_LOC` | شپ ٹو / ڈیلیور ٹو گلوبل لوکیشن نمبر (GLN). |
| `411` | `AI_411_BILL_TO` | بل ٹو / انوائس ٹو گلوبل لوکیشن نمبر (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | خریداری ماخذ گلوبل لوکیشن نمبر (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | شپ فار / ڈیلیور فار - فارورڈ ٹو گلوبل لوکیشن نمبر (GLN). |
| `414` | `AI_414_LOC_NO` | ایک طبعی مقام کی شناخت - گلوبل لوکیشن نمبر (GLN). |
| `415` | `AI_415_PAY_TO` | انوائس کرنے والے فریق کا گلوبل لوکیشن نمبر (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | پیداوار یا سروس مقام کا گلوبل لوکیشن نمبر (GLN). |
| `417` | `AI_417_PARTY` | پارٹی گلوبل لوکیشن نمبر (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | واحد پوسٹل اتھارٹی کے اندر شپ ٹو / ڈیلیور ٹو پوسٹل کوڈ. |
| `421` | `AI_421_SHIP_TO_POST` | آئی ایس او ملکی کوڈ کے ساتھ شپ ٹو / ڈیلیور ٹو پوسٹل کوڈ. |
| `422` | `AI_422_ORIGIN` | تجارتی شے کے منشا کا ملک. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | ابتدائی پروسیسنگ کا ملک. |
| `424` | `AI_424_COUNTRY_PROCESS` | پروسیسنگ کا ملک. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | علیحدگی کا ملک. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | مکمل عمل زنجیر کا احاطہ کرنے والا ملک. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | منشا کا ملکی ذیلی حصہ. |
| `4300` | `AI_4300_SHIP_TO_COMP` | شپ ٹو / ڈیلیور ٹو کمپنی کا نام. |
| `4301` | `AI_4301_SHIP_TO_NAME` | شپ ٹو / ڈیلیور ٹو رابطہ. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | شپ ٹو / ڈیلیور ٹو پتہ سطر 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | شپ ٹو / ڈیلیور ٹو پتہ سطر 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | شپ ٹو / ڈیلیور ٹو مضافاتی علاقہ. |
| `4305` | `AI_4305_SHIP_TO_LOC` | شپ ٹو / ڈیلیور ٹو علاقہ. |
| `4306` | `AI_4306_SHIP_TO_REG` | شپ ٹو / ڈیلیور ٹو خطہ. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | شپ ٹو / ڈیلیور ٹو ملکی کوڈ. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | شپ ٹو / ڈیلیور ٹو ٹیلی فون نمبر. |
| `4309` | `AI_4309_SHIP_TO_GEO` | شپ ٹو / ڈیلیور ٹو جیو لوکیشن. |
| `4310` | `AI_4310_RTN_TO_COMP` | واپسی کمپنی کا نام. |
| `4311` | `AI_4311_RTN_TO_NAME` | واپسی رابطہ. |
| `4312` | `AI_4312_RTN_TO_ADD1` | واپسی پتہ سطر 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | واپسی پتہ سطر 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | واپسی مضافاتی علاقہ. |
| `4315` | `AI_4315_RTN_TO_LOC` | واپسی علاقہ. |
| `4316` | `AI_4316_RTN_TO_REG` | واپسی خطہ. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | واپسی ملکی کوڈ. |
| `4318` | `AI_4318_RTN_TO_POST` | واپسی پوسٹل کوڈ. |
| `4319` | `AI_4319_RTN_TO_PHONE` | واپسی ٹیلی فون نمبر. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | سروس کوڈ کی تفصیل. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | خطرناک اشیاء کا نشان (فلیگ). |
| `4322` | `AI_4322_AUTH_LEAVE` | چھوڑنے کا اختیار (ڈیلیوری اجازت). |
| `4323` | `AI_4323_SIG_REQUIRED` | دستخط درکار نشان (فلیگ). |
| `4330` | `AI_4330_MAX_TEMP_F` | فارن ہائیٹ میں زیادہ سے زیادہ درجہ حرارت (ڈگری کے سویں حصے میں ظاہر). |
| `4331` | `AI_4331_MAX_TEMP_C` | سیلسیس میں زیادہ سے زیادہ درجہ حرارت (ڈگری کے سویں حصے میں ظاہر). |
| `4332` | `AI_4332_MIN_TEMP_F` | فارن ہائیٹ میں کم از کم درجہ حرارت (ڈگری کے سویں حصے میں ظاہر). |
| `4333` | `AI_4333_MIN_TEMP_C` | سیلسیس میں کم از کم درجہ حرارت (ڈگری کے سویں حصے میں ظاہر). |

### مصنوعات کی خصوصیات اور قابلِ سراغ رسانی

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | نیٹو اسٹاک نمبر (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | یو این/ای سی ای گوشت لاش اور کٹس کی درجہ بندی. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | فعال طاقت (پوٹینسی). |
| `7005` | `AI_7005_CATCH_AREA` | پکڑ کا علاقہ (ماہی گیری علاقہ). |
| `7008` | `AI_7008_AQUATIC_SPECIES` | ماہی گیری کے مقاصد کے لیے انواع. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | ماہی گیری کے آلات کی قسم. |
| `7010` | `AI_7010_PROD_METHOD` | پیداوار کا طریقہ. |
| `7020` | `AI_7020_REFURB_LOT` | تجدید کاری لاٹ آئی ڈی. |
| `7021` | `AI_7021_FUNC_STAT` | فعالیت کی حیثیت. |
| `7022` | `AI_7022_REV_STAT` | نظرثانی کی حیثیت. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | اسمبلی کا گلوبل انفرادی اثاثہ شناخت کنندہ (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | تین ہندسوں کے ISO ملکی کوڈ کے ساتھ کارروائی کنندہ کا نمبر (10 خانے)۔. |
| `7040` | `AI_7040_UIC_EXT` | ایکسٹینشن 1 اور امپورٹر انڈیکس کے ساتھ GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | یو این/سیفیکٹ فریٹ یونٹ کی قسم. |

### قومی صحت ادائیگی نمبر (NHRN)

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - جرمنی PZN. |
| `711` | `AI_711_NHRN_CIP` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - فرانس CIP. |
| `712` | `AI_712_NHRN_CN` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - اسپین CN. |
| `713` | `AI_713_NHRN_DRN` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - برازیل DRN. |
| `714` | `AI_714_NHRN_AIM` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - پرتگال AIM. |
| `715` | `AI_715_NHRN_NDC` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - ریاستہائے متحدہ امریکہ NDC. |
| `716` | `AI_716_NHRN_AIC` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - اٹلی AIC. |
| `717` | `AI_717_NHRN_SRN` | قومی صحت کی دیکھ بھال معاوضہ نمبر (NHRN) - کوسٹاریکا سینیٹری رجسٹر نمبر. |

### صحتِ عامہ، GMN، HIDRI، CPID اور افراد کا ڈیٹا

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | سرٹیفکیشن حوالہ (10 خانے)۔. |
| `7240` | `AI_7240_PROTOCOL` | پروٹوکول آئی ڈی. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | اے آئی ڈی سی میڈیا کی قسم. |
| `7242` | `AI_7242_VCN` | ورژن کنٹرول نمبر (VCN). |
| `7250` | `AI_7250_DOB` | تاریخ پیدائش (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | پیدائش کی تاریخ اور وقت (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | حیاتیاتی جنس. |
| `7253` | `AI_7253_FAMILY_NAME` | شخص کا خاندانی نام. |
| `7254` | `AI_7254_GIVEN_NAME` | شخص کا پہلا نام. |
| `7255` | `AI_7255_SUFFIX` | شخص کے نام کا لاحقہ. |
| `7256` | `AI_7256_FULL_NAME` | شخص کا پورا نام. |
| `7257` | `AI_7257_PERSON_ADDR` | شخص کا پتہ. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | بچے کی پیدائش کی ترتیب. |
| `7259` | `AI_7259_BABY` | خاندانی نام کے مطابق بچے کی ترتیب. |
| `8001` | `AI_8001_DIMENSIONS` | رول پروڈکٹس (چوڑائی، لمبائی، کور قطر، سمت، اسپلائسز). |
| `8002` | `AI_8002_CMT_NO` | سیلولر موبائل ٹیلی فون شناخت کنندہ. |
| `8003` | `AI_8003_GRAI` | گلوبل قابل واپسی اثاثہ شناخت کنندہ (GRAI). |
| `8004` | `AI_8004_GIAI` | گلوبل انفرادی اثاثہ شناخت کنندہ (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | فی یونٹ پیمائش قیمت. |
| `8006` | `AI_8006_ITIP` | ایک انفرادی تجارتی شے کے ٹکڑے کی شناخت (ITIP). |
| `8007` | `AI_8007_IBAN` | انٹرنیشنل بینک اکاؤنٹ نمبر (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | پیداوار کی تاریخ اور وقت (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | آپٹیکلی ریڈایبل سینسر انڈیکیٹر. |
| `8010` | `AI_8010_CPID` | کمپوننٹ/پارٹ شناخت کنندہ (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | کمپوننٹ/پارٹ شناخت کنندہ سیریل نمبر (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | سافٹ ویئر ورژن. |
| `8013` | `AI_8013_GMN` | گلوبل ماڈل نمبر (GMN). |
| `8014` | `AI_8014_MUDI` | انتہائی انفرادی آلہ رجسٹریشن شناخت کنندہ (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | خدمات فراہم کرنے والے ادارے اور خدمات کے فراہم کنندہ کے درمیان تعلق کی شناخت کے لیے گلوبل سروس ریلیشن نمبر (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | خدمات فراہم کرنے والے ادارے اور خدمات کے وصول کنندہ کے درمیان تعلق کی شناخت کے لیے گلوبل سروس ریلیشن نمبر (GSRN). |
| `8019` | `AI_8019_SRIN` | سروس ریلیشن انسٹنس نمبر (SRIN). |
| `8020` | `AI_8020_REF_NO` | ادائیگی رسید حوالہ نمبر. |
| `8026` | `AI_8026_ITIP_CONTENT` | لاجسٹک یونٹ میں شامل تجارتی شے کے ٹکڑوں کی شناخت (ITIP). |
| `8030` | `AI_8030_DIGSIG` | ڈیجیٹل دستخط (DigSig). |
| `8040` | `AI_8040_IMEI` | انٹرنیشنل موبائل ایکوپمنٹ آئیڈینٹٹی (IMEI). |
| `8041` | `AI_8041_IMEI2` | انٹرنیشنل موبائل ایکوپمنٹ آئیڈینٹٹی 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | ایمبیڈڈ سم نمبر. |
| `8043` | `AI_8043_PSIM` | فزیکل سم نمبر. |
| `8110` | `AI_8110` | شمالی امریکہ میں استعمال کے لیے کوپن کوڈ شناخت. |
| `8111` | `AI_8111_POINTS` | کوپن کے لائلٹی پوائنٹس. |
| `8112` | `AI_8112` | شمالی امریکہ میں استعمال کے لیے پازیٹو آفر فائل کوپن کوڈ شناخت. |
| `8200` | `AI_8200_PRODUCT_URL` | توسیعی پیکیجنگ یو آر ایل. |

### اندرونی / کمپنی کا استعمال

| AI | مستقل | تفصیل |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | تجارتی شراکت داروں کے درمیان باہمی طور پر متفقہ معلومات. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | کمپنی کی اندرونی معلومات (9 خانے)۔. |

---

## ضمیمہ ب — تعبیری کلیدی مستقلات

جب `GaiaParser.parse()` کو `ParseMode.INTERPRETATION` کے ساتھ بلایا جائے، تو ہر `GS1AIObjectElement` میدان کے مخصوص مالا مال کرنے والوں کے بنائے ہوئے `GS1AIInterpretation` آبجیکٹوں کی ایک فہرست اٹھا سکتا ہے۔ مخصوص تعبیری قدریں تلاش کرنے کے لیے `GS1Constants_Enricher` مستقلات (پیکیج `tools.pantheum.gaia.gs1.constants`) بطور کلید استعمال کیجیے:

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

نمائشی عنوانات مستقلات **نہیں** ہیں — وہ `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` کی مقامی فہرستوں میں ہیں، جن کی کلید قسمی مستقل ہے۔ `GS1AIInterpretation.getLabel()` عنوان پارس زبان میں واپس کرتا ہے (دیکھیے [مقامی زبان میں پیغامات اور عنوانات](#مقامی-زبان-میں-پیغامات-اور-عنوانات))، اور جب کسی فہرست میں کلید نہ ہو تو انگریزی پر لوٹ جاتا ہے۔ نیچے کا نمائشی عنوان کالم اردو متن درج کرتا ہے؛ قسمی کلیدیں بذاتِ خود تمام زبانوں میں یکساں رہتی ہیں، اس لیے ہمیشہ کلید پر مطابقت کیجیے، کبھی عنوان پر نہیں۔

### تاریخ اور وقت

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `DATE_VALUE` | تاریخ | تاریخی AI (11–17، 7003، 7006، 7011 وغیرہ) |
| `DATE_FORMAT` | تاریخ کی شکل | تاریخی AI |
| `TIME_VALUE` | وقت | وقت اٹھانے والے AI (7003، 7011، 8008 وغیرہ) |
| `TIME_FORMAT` | وقت کی شکل | وقت اٹھانے والے AI |
| `DATETIME_VALUE` | تاریخ اور وقت | تاریخ+وقت AI |
| `DATETIME_FORMAT` | تاریخ اور وقت کی شکل | تاریخ+وقت AI |

### فصل کاٹنے کی تاریخ

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | کٹائی کی ابتدائی تاریخ | AI 7007 |
| `HARVEST_END_DATE` | کٹائی کی آخری تاریخ | AI 7007 (اختیاری حد کا اختتام) |
| `HARVEST_DATE_RANGE` | کٹائی کی تاریخ کی حد | AI 7007 |

### GS1 کمپنی سابقہ

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 کمپنی سابقہ | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 رکن کوڈ | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 رکن تنظیم | GTIN / GLN / SSCC AI |

### GTIN

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN قسم | AI 01، 02 |
| `GTIN_NATIVE` | GTIN | AI 01، 02 |
| `PACKAGING_LEVEL` | پیکجنگ سطح | AI 01 |
| `GTIN_CHECK_DIGIT` | چیک ہندسہ | AI 01، 02 |

### SSCC

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | توسیعی ہندسہ | AI 00 |
| `SSCC_SERIAL_REFERENCE` | سیریل حوالہ | AI 00 |
| `SSCC_CHECK_DIGIT` | چیک ہندسہ | AI 00 |

### ملک (ISO 3166)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | ملکی کوڈ (عددی) | واحد ملکی AI (422، 424–426، 4307، 4317، 421، 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | ملکی کوڈ (الفا-2) | الفا-2 ملکی AI |
| `COUNTRY_NAME` | ملک کا نام | واحد ملکی AI |
| `COUNTRY_LIST` | ممالک | AI 423 — تمام نام جوڑ کر، مثلاً `Australia, New Zealand` |

AI 423 (ابتدائی کارروائی کا ملک) پانچ تک ممالک اٹھا سکتا ہے، چنانچہ یہ **ہر ملک کے لیے
ایک شمار شدہ جوڑا** پیدا کرتا ہے — `COUNTRY_CODE_NUMERIC_1`، `COUNTRY_NAME_1`،
`COUNTRY_CODE_NUMERIC_2`، `COUNTRY_NAME_2` … — اور اس کے بعد ایک `COUNTRY_LIST` خلاصہ۔
ان کلیدوں کو `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` مستقلات سے 1 سے شروع
ہونے والے شمار کے ساتھ بنائیے، یا محض `getInterpretations()` پر چلیے؛ بغیر لاحقے کی
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` کلیدیں AI 423 کے لیے **پیدا نہیں ہوتیں**۔

### کرنسی (ISO 4217)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | کرنسی کوڈ | کرنسی کے ساتھ رقمی AI (391n، 393n) |
| `CURRENCY_ALPHA` | کرنسی کا حرفی کوڈ | کرنسی کے ساتھ رقمی AI |
| `CURRENCY_NAME` | کرنسی کا نام | کرنسی کے ساتھ رقمی AI |

### درجۂ حرارت

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `TEMPERATURE` | درجہ حرارت | AI 4330–4333 |
| `TEMPERATURE_UNIT` | درجہ حرارت کی اکائی | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | درجہ حرارت (فارمیٹ شدہ) | AI 4330–4333 |

### جنس (ISO 5218)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `SEX_CODE` | جنس کوڈ | AI 7252 |
| `SEX_DESCRIPTION` | جنس کی تفصیل | AI 7252 |

### آبی انواع (FAO)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `SPECIES_CODE` | نوع کوڈ | AI 7008 |
| `SPECIES_SCIENTIFIC` | سائنسی نام | AI 7008 |
| `SPECIES_ENGLISH` | عام نام | AI 7008 |
| `SPECIES_FAMILY` | خاندان | AI 7008 |
| `SPECIES_ORDER` | ترتیب | AI 7008 |

### NATO سٹاک نمبر (NSN)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `NSN_FSG` | سپلائی گروپ | AI 7001 |
| `NSN_FSG_NAME` | سپلائی گروپ کا نام | AI 7001 |
| `NSN_FSCG` | سپلائی کلاس | AI 7001 |
| `NSN_FSCG_NAME` | سپلائی درجے کا نام | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | ملکی کوڈ | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | ملک | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO ملکی کوڈ | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS زمرہ | AI 7001 |
| `NSN_NIIN` | قومی آئٹم نمبر | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### رول مصنوعات

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | رول کی چوڑائی (mm) | AI 8001 |
| `ROLL_LENGTH` | رول کی لمبائی (m) | AI 8001 |
| `CORE_DIAMETER` | کور قطر (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | لپیٹنے کی سمت کا کوڈ | AI 8001 |
| `WINDING_DIRECTION` | لپیٹنے کی سمت | AI 8001 |
| `SPLICES` | جوڑ | AI 8001 |

### IBAN

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | ملکی کوڈ | AI 8007 |
| `IBAN_COUNTRY_NAME` | ملک | AI 8007 |
| `IBAN_CHECK_DIGITS` | چیک ہندسے | AI 8007 |
| `IBAN_CHECK_VALID` | جانچ | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040، 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040، 8041 |
| `IMEI_SERIAL` | سیریل نمبر | AI 8040، 8041 |
| `IMEI_CHECK_DIGIT` | چیک ہندسہ | AI 8040، 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040، 8041 |
| `IMEI_RBI_NAME` | جاری کنندہ ادارہ | AI 8040، 8041 |

پندرہ ہندسے `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]` کی صورت میں تحلیل ہوتے ہیں،
اور RBI، TAC کے پہلے دو ہندسے ہیں — یعنی `IMEI_RBI` کوئی الگ خانہ نہیں بلکہ `IMEI_TAC` کا
سابقہ ہے۔ `IMEI_FORMATTED` GSMA کی معیاری نمائشی ترتیب `AA-BBBBBB-CCCCCC-D` دکھاتا ہے (مثلاً
`49-015420-323751-8`)، جو TAC کو RBI کی سرحد پر کاٹتی ہے؛ پرانی `6-2-6-1` ترتیب، جو وہاں
کاٹتی تھی جہاں اب متروک حتمی جوڑ کا کوڈ شروع ہوتا تھا، پیدا نہیں کی جاتی۔

`IMEI_RBI_NAME`، `ImeiRbiData` کے ذریعے RBI کو تفویض کنندہ ادارے کے نام میں حل کرتا ہے، اور
یہ **سب سے آخر میں اور صرف اُس وقت** منسلک ہوتا ہے جب کوڈ وہاں درج ہو۔ وہ جدول تین گروہوں کو
محیط ہے:

- **اب بھی تفویض کرنے والے ادارے** — `01` CTIA/PTCRB، `35` TÜV SÜD BABT، `86` TAF، اور مزید
  `99` Global Hexadecimal Administrator اور `98` (مخصوص)۔
- **آزمائشی حدیں** — `00` اور `02`–`09`؛ یہ کسی حقیقی تفویض کے بجائے آزمائشی IMEI ظاہر
  کرتی ہیں۔ ان کے بارے میں `ImeiRbiData.isTestCode(code)` سے پوچھیے۔
- **اب تفویض نہ کرنے والے ادارے** — تاریخی ادارے جیسے `49` (BZT/BAPT، جرمنی)، `44` (BABT،
  برطانیہ) اور `91` (MSAI، بھارت)۔ ان کے بارے میں `ImeiRbiData.isNoLongerAllocating(code)`
  سے پوچھیے۔ ان کوڈوں والے آلات معمول کے ہیں اور اب بھی زیرِ استعمال ہیں؛ صرف نئی تفویض
  رکی ہے، چنانچہ یہ رپورٹ کرنے کی معلومات ہے، درستی کا اشارہ ہرگز نہیں۔

`IMEI_RBI_NAME` کی غیر موجودگی کا مطلب ہے "یہ RBI ہمارے جدول میں نہیں"، یہ **نہیں** کہ
"IMEI ناقص ہے": جدول براہِ راست GSMA سے نہیں بلکہ ایک شائع شدہ RBI فہرست سے مرتب ہوا ہے،
اس لیے وہ نئے مقرر ہونے والے اداروں سے پیچھے رہ سکتا ہے۔ اس کی غیر موجودگی سے کوئی توثیقی
نتیجہ اخذ نہ کیجیے؛ RBI کوئی چیک حرف نہیں۔ تعبیروں کی فہرست پر چلنے والے کوڈ کو مقام کے
لحاظ سے اشاریہ بنانے کے بجائے اس کی غیر موجودگی برداشت کرنی چاہیے۔

### SIM شناخت کار (EID / ICCID)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042، 8043 |
| `SIM_MII_NAME` | صنعت کی قسم | AI 8042 |
| `EID_BODY` | EID باڈی | AI 8042 |
| `EID_CHECK_DIGIT` | چیک ہندسہ | AI 8042 |
| `ICCID_BODY` | ICCID باڈی | AI 8043 |
| `ICCID_EXTENSION` | توسیع | AI 8043 |

`SIM_MII` پہلے **دو** ہندسے (`89`) رکھتا ہے — یہی وہ جوڑا ہے جو ITU-T E.118 مواصلات کے لیے
مخصوص کرتا ہے۔ ISO/IEC 7812 بذاتِ خود MII کو **صرف پہلا ہندسہ** قرار دیتا ہے، اس لیے
`SIM_MII_NAME` زمرہ `Iso7812Data` کے ذریعے ابتدائی `8` ہندسے سے حل کرتا ہے — جس کا نتیجہ
"Healthcare, telecommunications and other future industry assignments" نکلتا ہے۔ چنانچہ یہ
ہر خوش ساخت EID کے لیے یکساں رہتا ہے؛ اسے معیار تک سراغ رسانی کے لیے رپورٹ کیا جاتا ہے،
کسی امتیاز کے طور پر نہیں۔ `Iso7812Data.nameForCode(digit)` ایک اکیلا ہندسہ لیتا ہے، جبکہ
`nameForIdentifier(prefix)` طویل تر سابقہ قبول کرتا ہے اور اس کا پہلا ہندسہ پڑھتا ہے۔

`SIM_MII_NAME` صرف `EidEnricher` (AI 8042) پیدا کرتا ہے۔ `IccidEnricher` (AI 8043)
`SIM_MII` دکھاتا ہے، مگر زمرہ نہیں۔

### سرٹیفکیشن حوالہ

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | سلسلہ نمبر | AI 7230–7239 |
| `CERT_SCHEME_CODE` | سرٹیفیکیشن اسکیم کوڈ | AI 7230–7239 |
| `CERT_SCHEME_NAME` | سرٹیفیکیشن اسکیم | AI 7230–7239 |
| `CERT_REFERENCE` | سرٹیفیکیشن حوالہ | AI 7230–7239 |

### GS1 UIC

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC کوڈ | AI 7040 |
| `UIC_EXTENSION_1` | توسیع 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | درآمد کنندہ اشاریہ | AI 7040 |

### شیرخوار کی پیدائشی ترتیب

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | پیدائش کی پوزیشن | AI 7258 |
| `BIRTH_TOTAL` | کل پیدائشیں | AI 7258 |
| `BIRTH_SEQUENCE` | پیدائش کی ترتیب | AI 7258 |

### گلوبل ماڈل نمبر (GMN)

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | ماڈل حوالہ | AI 8013 |
| `GMN_CHECK_PAIR` | چیک جوڑا | AI 8013 |

### HIDRI

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | آلہ حوالہ | AI 8014 |
| `HIDRI_CHECK_PAIR` | چیک جوڑا | AI 8014 |

### CPID

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | جزو اور پرزہ حوالہ | AI 8010–8011 |

### اعشاری اور پیمائشی قدریں

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | اعشاری قدر | مضمر اعشاری مقامات والے عددی AI (31xx–36xx) |
| `DECIMAL_AMOUNT` | رقم | قیمتی AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | فیصد | AI 394n |
| `DECIMAL_PLACES` | اعشاری مقامات | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` کے ساتھ |
| `PERCENTAGE_FORMAT` | فیصد کی شکل | AI 394n |
| `ISO_UNIT_CODE` | ISO یونٹ کوڈ | پیمائشی AI |
| `ISO_UNIT_NAME` | ISO یونٹ کا نام | پیمائشی AI |
| `MONETARY_AMOUNT` | مالیاتی رقم | قیمتی AI |
| `MONETARY_AMOUNT_DISPLAY` | مالیاتی رقم (فارمیٹ شدہ) | قیمتی AI |

### جغرافیائی متناسقات

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `LATITUDE` | عرض بلد | AI 4309 |
| `LONGITUDE` | طول بلد | AI 4309 |
| `GEO_COORDINATES` | جغرافیائی متناسقات | AI 4309 |
| `LATITUDE_DMS` | عرض بلد (DMS) | AI 4309 |
| `LONGITUDE_DMS` | طول بلد (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | جغرافیائی متناسقات (DMS) | AI 4309 |

### پیداوار کا طریقہ

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | پیداوار کے طریقے کا کوڈ | AI 7010 |
| `PRODUCTION_METHOD` | پیداوار کا طریقہ | AI 7010 |

### AIDC ذریعے کی قسم

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC میڈیا قسم کوڈ | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC میڈیا قسم | AI 7241 |

### کل میں سے ٹکڑا

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | ٹکڑا نمبر | AI 8006 |
| `PIECE_TOTAL` | کل ٹکڑے | AI 8006 |
| `PIECE_OF_TOTAL` | کل میں سے ٹکڑا | AI 8006 |

### اجزا کی تقسیم

یہ کلیدیں Java میں لکھے کسی مالا مال کرنے والے کے بجائے `content/ai-content.json` کی
اعلانیہ جزوی تقسیموں سے پیدا ہوتی ہیں — یہ سب کسی مرکب AI قدر کے نام دار حصے دکھاتی ہیں۔ اس
ضمیمے کی ہر دوسری کلید کے برعکس، **ان کے لیے `GS1Constants_Enricher` میں کوئی مستقل نہیں**:
سٹرنگ لٹرل پر مطابقت کیجیے، یا قسم `GS1AIInterpretation.getType()` سے پڑھیے۔

| قسمی کلید | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | چیک ہندسہ | AI 253، 255، 402، 410–417، 8003، 8017، 8018 |
| `SERIAL_NUMBER` | سیریل نمبر | AI 253، 255، 8003 |
| `POSTAL_CODE` | پوسٹل کوڈ | AI 421 |
| `PROCESSOR_ID` | پروسیسر شناخت کنندہ | AI 7030–7039 |

خیال رہے کہ یہاں کا `CHECK_DIGIT` عام جزوی تقسیم کی کلید ہے، جو اوپر دی گئی مالا مال کرنے
والے کی مخصوص کلیدوں `GTIN_CHECK_DIGIT`، `SSCC_CHECK_DIGIT`، `IMEI_CHECK_DIGIT` اور
`EID_CHECK_DIGIT` سے الگ ہے۔

### متفرقات

| کلیدی مستقل | نمائشی عنوان | کس کا بنایا ہوا |
|--------------|---------------|-------------|
| `FLAG_VALUE` | قدر | بولین / نشان AI (4321–4323) |
| `DECODED_TEXT` | ڈی کوڈ شدہ متن | آزاد متن AI |
