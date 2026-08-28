# GAIA (محلّل معرّفات تطبيق GS1) — دليل المطوّر

## المحتويات

1. [نظرة عامة](#نظرة-عامة)
2. [عن GS1 والمواصفات العامة](#عن-gs1-والمواصفات-العامة)
3. [معرّفات تطبيق GS1](#معرفات-تطبيق-gs1)
4. [البداية السريعة](#البداية-السريعة)
5. [مسار التحليل](#مسار-التحليل)
   - [المرحلة التمهيدية — مُعدِّلات المدخل](#المرحلة-التمهيدية--معدلات-المدخل)
   - [المرحلة 0 — معرّف الارتباط](#المرحلة-0--معرف-الارتباط)
   - [المرحلة 1 — توجيه المدخل](#المرحلة-1--توجيه-المدخل)
   - [المرحلة 2 — البنية النحوية](#المرحلة-2--البنية-النحوية)
   - [المرحلة 3 — المحتوى](#المرحلة-3--المحتوى)
   - [المرحلة 4 — التفسير](#المرحلة-4--التفسير)
6. [ضبط التحليل (`ParseConfig`)](#ضبط-التحليل-parseconfig)
   - [الخيارات](#الخيارات)
   - [الرسايل والتسميات المترجمة](#الرسايل-والتسميات-المترجمة)
   - [تنسيق التاريخ](#تنسيق-التاريخ)
7. [مُعدِّلات المدخل](#معدلات-المدخل)
   - [المُعدِّلات المدمجة](#المعدلات-المدمجة)
   - [إزاي تكتب مُعدِّل](#إزاي-تكتب-معدل)
   - [تسجيل المُعدِّلات](#تسجيل-المعدلات)
   - [إزاي تشوف المُعدِّل عمل إيه](#إزاي-تشوف-المعدل-عمل-إيه)
   - [معالجة إخفاق المُعدِّل](#معالجة-إخفاق-المعدل)
8. [أوضاع التحليل](#أوضاع-التحليل)
   - [وضع DATA_CARRIER](#وضع-data_carrier)
   - [وضع SYNTAX](#وضع-syntax)
   - [وضع CONTENT](#وضع-content)
   - [وضع INTERPRETATION (الافتراضي)](#وضع-interpretation-الافتراضي)
9. [معرّف الارتباط](#معرف-الارتباط)
10. [GS1 Digital Link](#gs1-digital-link)
11. [الشغل بالنتايج](#الشغل-بالنتايج)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry وDataCarrierType](#datacarrierentry-وdatacarriertype)
12. [مرجع الأخطاء](#مرجع-الأخطاء)
13. [الأمان مع الخيوط المتعددة](#الأمان-مع-الخيوط-المتعددة)
14. [الملحق أ — ثوابت نصوص المعرّفات](#الملحق-أ--ثوابت-نصوص-المعرفات)
    - [التعريف والترقيم التسلسلي](#التعريف-والترقيم-التسلسلي)
    - [التواريخ والأوقات](#التواريخ-والأوقات)
    - [الكمية والقياس — قياس متغيّر (المتري)](#الكمية-والقياس--قياس-متغير-المتري)
    - [الكمية والقياس — قياس متغيّر (الإمبراطوري / الأمريكي)](#الكمية-والقياس--قياس-متغير-الإمبراطوري--الأمريكي)
    - [التسعير والمبالغ النقدية](#التسعير-والمبالغ-النقدية)
    - [الموقع والشحن](#الموقع-والشحن)
    - [سمات المنتج والتتبّع](#سمات-المنتج-والتتبع)
    - [أرقام السداد الصحية الوطنية (NHRN)](#أرقام-السداد-الصحية-الوطنية-nhrn)
    - [الرعاية الصحية وGMN وHIDRI وCPID وبيانات الأشخاص](#الرعاية-الصحية-وgmn-وhidri-وcpid-وبيانات-الأشخاص)
    - [الاستخدام الداخلي / استخدام الشركة](#الاستخدام-الداخلي--استخدام-الشركة)
15. [الملحق ب — ثوابت مفاتيح التفسير](#الملحق-ب--ثوابت-مفاتيح-التفسير)
    - [التاريخ والوقت](#التاريخ-والوقت)
    - [تاريخ الحصاد](#تاريخ-الحصاد)
    - [بادئة شركة GS1](#بادئة-شركة-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [البلد (ISO 3166)](#البلد-iso-3166)
    - [العملة (ISO 4217)](#العملة-iso-4217)
    - [درجة الحرارة](#درجة-الحرارة)
    - [الجنس (ISO 5218)](#الجنس-iso-5218)
    - [الأنواع المائية (FAO)](#الأنواع-المائية-fao)
    - [رقم مخزون الناتو (NSN)](#رقم-مخزون-الناتو-nsn)
    - [منتجات اللفائف](#منتجات-اللفائف)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [معرّفات SIM (EID / ICCID)](#معرفات-sim-eid--iccid)
    - [مرجع الشهادة](#مرجع-الشهادة)
    - [GS1 UIC](#gs1-uic)
    - [تسلسل ولادة الرضيع](#تسلسل-ولادة-الرضيع)
    - [رقم الطراز العالمي (GMN)](#رقم-الطراز-العالمي-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [القيم العشرية وقيم القياس](#القيم-العشرية-وقيم-القياس)
    - [الإحداثيات الجغرافية](#الإحداثيات-الجغرافية)
    - [طريقة الإنتاج](#طريقة-الإنتاج)
    - [نوع وسيط AIDC](#نوع-وسيط-aidc)
    - [القطعة من الإجمالي](#القطعة-من-الإجمالي)
    - [تقسيمات المكوّنات](#تقسيمات-المكونات)
    - [متفرّقات](#متفرقات)

---

## نظرة عامة

`GaiaParser` ده نقطة الدخول اللي بتحلّل سلاسل عناصر معرّفات تطبيق GS1 (AI). بيقبل الخرج الخام بتاع الماسح بأي شكل من الأشكال دي، وبيرجّع `ParseResult` منظّم فيه كل معرّف تطبيق اتحلّ، وأخطاء التحقق، ولو حبيت كمان تفسيرات يقدر الإنسان يقراها:

- سلسلة عناصر معرّفات تطبيق بسيطة: `0109506000134352`
- سلسلة عناصر قبلها معرّف ترميز AIM: `]C10109506000134352`
- عنوان GS1 Digital Link: `https://example.com/01/09506000134352`
- أي حاجة من دول، ولو حبيت تحطّ قدّامها معرّف ارتباط من تمن أرقام: `12345678~0109506000134352`

**كلاس نقطة الدخول:** `tools.pantheum.gaia.GaiaParser`

> **لسه أول مرة تشتغل بـ Gaia؟** ابدأ بـ **[البداية السريعة مع GaiaParser](GaiaParser-QuickStart-EgyptianArabic.md)** — عشر دقايق بس بتمرّ بيك على الاعتماديات، وأول تحليل، وكام حاجة بتوقّع الناس كتير. الدليل ده هو المرجع الكامل.

> الاتجاه العكسي — إنك *تبني* سلاسل عناصر صحيحة وعناوين Digital Link من أزواج معرّف/قيمة — بيتكلّم عنه **[GaiaBuilder — دليل المطوّر](GaiaBuilder-EgyptianArabic.md)**.

---

## عن GS1 والمواصفات العامة

**GS1** دي منظمة عالمية مش هادفة للربح، بتطوّر معايير مفتوحة للتعريف وتبادل البيانات في سلاسل الإمداد وبتحافظ عليها. معاييرها بتتستعمل في التجزئة والرعاية الصحية والخدمات اللوجستية وخدمات الأغذية وصناعات تانية كتير، وبتغطّي كل حاجة من الباركود اللي على تغليف المنتجات لحد تتبّع جرعات الأدوية بالرقم التسلسلي.

المرجع المعتمد لكل حاجة بينفّذها المحلّل ده هو **المواصفات العامة لـ GS1** — وثيقة واحدة بتحدّد:

- كل رموز معرّفات التطبيق (AI)، وعناوين بياناتها وتنسيقاتها وقواعد التحقق منها
- قواعد بناء سلاسل عناصر معرّفات التطبيق وترميزها
- متطلبات أنظمة ترميز الباركود وتوزيع معرّفات ترميز AIM
- خوارزميات رقم التحقق وحرف التحقق
- تفسير السنة اللي من رقمين (قاعدة النافذة المتحرّكة)
- مواصفات Data Matrix وQR Code وGS1-128 وGS1 DataBar وغيرها من النواقل

المواصفات العامة لـ GS1 بتتحدّث كل سنة. الإصدار الحالي والمواد المساعدة موجودين هنا:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA بينفّذ **الإصدار 26.0 (معتمد، يناير 2026)** من المواصفات العامة لـ GS1.

عناوين GS1 Digital Link بيحكمها معيار مرافق اسمه **GS1 Digital Link: URI Syntax**، وهو اللي بيحدّد مفاتيح التعريف الأساسية، وترتيب مقيّدات المفاتيح، وترميز سمات البيانات اللي المحلّل بيطبّقه على مدخلات Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA بينفّذ **الإصدار 1.7.0 (معتمد، أغسطس 2026)** من معيار GS1 Digital Link: URI Syntax.

الإشارات للأقسام في الوثيقة دي كلها بتشاور على المواصفات العامة لـ GS1 (زي «Table 7-5» و«section 7.12»)، ما عدا أرقام أقسام Digital Link (زي «§4.9» و«§4.12») اللي بتشاور على معيار GS1 Digital Link: URI Syntax.

---

## معرّفات تطبيق GS1

**معرّف تطبيق GS1 (AI)** ده بادئة رقمية قصيّرة — من رقمين لأربعة أرقام — بتحدّد معنى البيانات اللي جاية وراها على طول وتنسيقها. معرّفات التطبيق متعرّفة في المواصفات العامة لـ GS1 وبتغطّي مدى واسع من بيانات سلسلة الإمداد: معرّفات المنتجات، والتواريخ، والكميات، وأرقام التشغيلات، والأرقام التسلسلية، والقياسات، والعناوين، وحاجات تانية كتير.

### تركيب عنصر معرّف التطبيق

كل عنصر معرّف تطبيق متكوّن من جزئين:

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

رمز معرّف التطبيق بيبقى رقمي دايمًا. وقيمة البيانات بتيجي وراه على طول، من غير أي فاصل بين الرمز والقيمة.

### معرّفات ثابتة الطول ومعرّفات متغيّرة الطول

معرّفات التطبيق بتتقسم لنوعين:

<div dir="rtl">

| النوع | بيشتغل إزاي | مثال |
|---|---|---|
| **ثابت الطول** | عدد محارف محدّد بالظبط، وبيتقرا كامل دايمًا | المعرّف `01` (GTIN) — أربعتاشر رقم دايمًا |
| **متغيّر الطول** | من محرف واحد لحد حدّ أقصى؛ بينتهي بفاصل GS أو بنهاية المدخل | المعرّف `10` (التشغيلة/الدفعة) — من 1 لـ 20 محرف أبجدي رقمي |

</div>

إن المعرّف يبقى ثابت ولا متغيّر، ده بيتحدّد بس من تعريفه في مواصفة GS1 — والمحلّل عمره ما بيخمّن.

### سلاسل العناصر اللي فيها كذا معرّف

تقدر توصّل كذا معرّف تطبيق في سلسلة عناصر واحدة. المعرّفات ثابتة الطول تقدر توصّلها على طول، علشان المحلّل عارف بالظبط هيقرا كام محرف. لكن المعرّفات متغيّرة الطول لازم تنتهي بـ **محرف GS** (ASCII `0x1D`، واللي بيتسمّى كمان FNC1 في أنظمة ترميز الباركود) كل ما يبقى وراها معرّف تاني، علشان المحلّل يعرف قيمة بتخلص فين ورمز المعرّف اللي بعدها بيبدأ فين.

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

في نصوص Java الحرفية، اكتب محرف GS بهروب اليونيكود `""`.

### معرّفات التطبيق المشهورة

<div dir="rtl">

| AI | عنوان البيانات | التنسيق | مثال للقيمة |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34، منطقة نقدية واحدة) |
| `710` | NHRN PZN | X..20 | `12345678` |

</div>

> **الرقم الرابع** في معرّف قياس أو سعر من أربعة أرقام بيرمّز عدد المنازل العشرية الضمنية — يعني `3103` ده الوزن الصافي بالكيلوجرام بتلات منازل عشرية (`001500` = 1.500 كجم)، لكن `3102` هيقرا نفس الأرقام دي على إنها 15.00 كجم. عمود «التنسيق» فوق بيوري تنسيق *البيانات*؛ إنما `getFormatString()` الكامل لكل معرّف بيشمل المعرّف نفسه كمان (زي `N4+N6` للمعرّف `3103`).

### التفسير اللي الإنسان يقدر يقراه (HRI)

الشكل المتعارف عليه للقراية بيحطّ كل رمز معرّف بين قوسين قبل قيمته على طول، وبيسيب مسافة بين العناصر:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

فاصل GS مش بيبان في HRI. والشكل ده بينتجه `GS1AIObject.toHriString()`.

### رموز المعرّفات اللي من أربعة أرقام

في معرّفات بتستعمل أربعة أرقام بدل رقمين. أول رقمين بيحدّدوا عيلة المعرّف؛ والرقم التالت و/أو الرابع بيشيل معنى زيادة (زي مكان الفاصلة العشرية الضمنية في معرّفات القياس). المحلّل بيطلّع رمز المعرّف الكامل من سلسلة العناصر لوحده — واللي بينادي بيتعامل دايمًا مع الرمز الكامل (يعني `"3102"` مش `"31"` لوحدها).

---

## البداية السريعة

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

> **فاصل GS:** المعرّفات متغيّرة الطول جوّه سلسلة فيها كذا معرّف لازم تتفصل بمحرف GS (ASCII `0x1D`). استعمل `""` في نصوص Java الحرفية.

---

## مسار التحليل

### المرحلة التمهيدية — مُعدِّلات المدخل

لو `ParseConfig` شايل أي **مُعدِّلات مدخل**، دي بتشتغل قبل أي حاجة تانية — قبل ما يتشال معرّف الارتباط، وقبل كشف الناقل، وقبل ما ندخل مسار GS1 أصلًا. كل مُعدِّل بيعيد كتابة المدخل الخام للمُعدِّل اللي بعده، وكل المراحل اللي تحت بتشتغل على خرج السلسلة دي.

مفيش مُعدِّلات مضبوطة افتراضيًا، فالمرحلة التمهيدية دي مش بتعمل حاجة غير لو انت اخترت تشغّلها. شوف [مُعدِّلات المدخل](#معدلات-المدخل).

---

### المرحلة 0 — معرّف الارتباط

قبل أي معالجة لـ GS1، `GaiaParser` بيبصّ يشوف المدخل بيبدأ بـ **بادئة معرّف ارتباط** اختيارية ولا لأ: تمن أرقام عشرية ASCII بالظبط وبعدها علامة التلدة (`~`)، يعني كده `12345678~`.

لو البادئة موجودة بتتشال وتتخزّن كـ `CorrelationInfo` في الـ `ParseResult` اللي بيرجع. وكل المراحل اللي بعد كده بتشتغل على الحمولة بعد ما اتشالت منها. ولو مفيش بادئة، المدخل بيعدّي زي ما هو.

للتفاصيل شوف [معرّف الارتباط](#معرف-الارتباط).

---

### المرحلة 1 — توجيه المدخل

بعد ما يتشال معرّف الارتباط، `GaiaParser` بيبصّ يشوف المدخل (بعد الشيل) بيبدأ بـ **معرّف ترميز AIM** ولا لأ: بادئة من تلات محارف شكلها `]` + حرف ASCII + رقم ASCII (زي `]C1` لـ GS1-128، و`]d2` لـ GS1 DataMatrix، و`]e0` لـ GS1 DataBar / GS1 Composite).

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

لو الناقل مش قادر يشيل معرّفات تطبيق GS1 (زي باركود بريدي مثلًا)، التحليل بيقف على طول بخطأ `GE-D002`.

---

### المرحلة 2 — البنية النحوية

دي بتشتغل دايمًا. ومتكوّنة من خطوتين فرعيتين:

**2أ. التقطيع لرموز (`AISyntaxParser`)**
- بيقرا طول رمز المعرّف من أول محرفين بجدول بادئات GS1 (الجدول 7-5 في المواصفات العامة لـ GS1).
- المعرّفات ثابتة الطول بتقرا عدد بايتات محدّد بالظبط من المدخل.
- المعرّفات متغيّرة الطول بتتقرا لحد محرف GS أو لحد نهاية المدخل.
- المعرّفات اللي فيها كذا مكوّن بتتقسّم كتلة قيمها لمقاطع لكل مكوّن.

**2ب. التحقق البنيوي (`SyntaxValidator`)**
- بيدوّر على المعرّفات المتكرّرة (`GE-S004`).
- بيتأكّد من الاعتماديات المطلوبة، يعني المعرّف `02` مثلًا محتاج المعرّف `37` (`GE-S005`).
- بيتأكّد من الاقترانات الممنوعة بين المعرّفات (`GE-S006`).

أخطاء المرحلة دي مستواها `SYNTAX_ERROR` (للتقطيع) أو `INTEGRITY_ERROR` (للبنية). ولو في **أي** خطأ — من التقطيع أو من البنية — المسار بيقف ومرحلتَي المحتوى والتفسير بتتتخطّى.

---

### المرحلة 3 — المحتوى

دي بتشتغل بس لو المرحلة 2 ما طلّعتش أي خطأ (لا من التقطيع ولا من البنية). ولكل عنصر مسار زي ده (كل خطوة بتشتغل بس لو اللي قبلها ما طلّعتش أخطاء):

<div dir="rtl">

| الخطوة | المدقّق | رموز الأخطاء |
|---|---|---|
| فحص التعبير النمطي | `RegexValidator` | `GE-C001` |
| مجموعة محارف المكوّن وتنسيقه | `ComponentValidator` | `GE-C005` + رموز التنسيق لكل حالة (`GE-C054`–`GE-C115`) |
| رقم التحقق / حرف التحقق | `CheckDigitCharacterValidator` | `GE-C003`، `GE-C004` |
| تحقق دلالي مخصّص | `ContentValidatorRegistry` | رموز المحتوى لكل حالة (`GE-C116`–`GE-C170`) |

</div>

أخطاء المرحلة دي مستواها `FORMAT_ERROR` أو `DATA_ERROR`، ما عدا استثناء واحد: فحوص بادئة شركة
GS1 على المعرّفات اللي فيها مفاتيح GS1 دي إرشادية بس ومستواها `WARNING` (شوف
[مرجع الأخطاء](#مرجع-الأخطاء))، يعني بادئة شركة مش معروفة مش هتخلّي النتيجة غلط
لوحدها.

---

### المرحلة 4 — التفسير

دي بتشتغل بس في وضع `INTERPRETATION`، وبس لما ما يكونش في أي عنصر شايل خطأ من أي مرحلة قبل كده. `InterpretationEngine` بيغني كل عنصر ببيانات وصفية ليها تسميات:

- تواريخ متعاد تنسيقها بشكل `dd/mm/yyyy`
- تفكيك رقم التحقق في GTIN والبحث عن بادئة شركة GS1
- أسامي البلاد حسب ISO 3166
- أسامي العملات ورموزها حسب ISO 4217
- المبالغ العشرية بعد فكّ ترميزها
- أجزاء التفسير اللي الإنسان يقدر يقراه (HRI)

والنتايج بتتربط بكل `GS1AIObjectElement` على شكل مدخلات `GS1AIInterpretation`.

---

## ضبط التحليل (`ParseConfig`)

`GaiaParser` بيوفّر نقطتين دخول بالظبط:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` بيشتغل بـ **الضبط الافتراضي**: وضع `INTERPRETATION`، وتواريخ صغيرة الترتيب (`dd/mm/yyyy`) بفاصل `/` وسنة من أربعة أرقام، ورسايل أخطاء **بالإنجليزي**. وعلشان تغيّر أي حاجة من دي — بما فيها وضع التحليل — ابنِ `ParseConfig` بالباني الانسيابي بتاعه واستعمل النسخة اللي بتاخد وسيطين.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

وتعدادات الخيارات كلها موجودة في `GaiaConstants`.

### الخيارات

<div dir="rtl">

| دالة الباني | التعداد (`GaiaConstants`) | الافتراضي | بيعمل إيه |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | عمق المسار — شوف [أوضاع التحليل](#أوضاع-التحليل). |
| `language(...)`      | `Language`      | `ENGLISH`        | لغة رسايل الأخطاء وتسميات التفسير **ووصوف المعرّفات**. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | ترتيب مكوّنات التاريخ: `LITTLE` (`dd/mm/yyyy`)، `MIDDLE` (`mm/dd/yyyy`)، `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | المحرف اللي بين مكوّنات التاريخ: `SLASH` (`/`)، `HYPHEN` (`-`)، `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) أو `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) أو `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | بيتخطّى فحص «بيحتاج» البنيوي (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | بيتخطّى فحص «بيمنع» البنيوي (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / اسم كلاس | مفيش | كود بيعيد كتابة المدخل الخام قبل التحليل — [مُعدِّلين مدمجين](#المعدلات-المدمجة) وكمان أي حاجة انت كاتبها. شوف [مُعدِّلات المدخل](#معدلات-المدخل). |

</div>

خيارات التاريخ الأربعة دي بتأثّر بس على نصوص التواريخ المنسّقة اللي بتطلّعها مُغنيات التفسير (في وضع `INTERPRETATION`)؛ ومش بتغيّر التحقق. وتقدر تسيب قيم الباني — أي خيار ما اتحدّدش (أو اتبعتله `null`) هيفضل على قيمته الافتراضية.

### الرسايل والتسميات المترجمة

`language(...)` بيختار لغة **تلات** أنواع من النصوص اللي الإنسان يقراها: رسايل الأخطاء، وتسميات التفسير (`getLabel()` بتاعة كل `GS1AIInterpretation`)، ووصوف المعرّفات (`getDescription()` بتاعة كل `GS1AIObjectElement`).

`GaiaConstants.Language` بيعرّف **35 لغة** بتغطّي أكتر لغات العالم انتشارًا: الإنجليزية، والفرنسية، والإسبانية، والألمانية، والإيطالية، والبرتغالية، والهولندية، والبولندية، والروسية، والأوكرانية، والتشيكية، والسويدية، والصينية، واليابانية، والكورية، والعربية، والإندونيسية، والهندية، والتركية، والبنغالية، والأردية، والفيتنامية، والبيدجن النيجيرية، والعربية المصرية، والماراثية، والتيلوجية، والتاميلية، والكانتونية، والصينية الوو، والتاجالوجية، والفارسية، والهوساوية، والبنجابية، والجاوية، والسواحيلية.

حالة الترجمة (زي ما بتتشحن):
- **تسميات التفسير** — مترجمة لكل اللغات.
- **رسايل الأخطاء** — مترجمة لكل اللغات.
- **وصوف المعرّفات** — مترجمة لكل اللغات ما عدا الإنجليزية. الإنجليزية مش فهرس لوحدها: بتتقرا على طول من حقل `description` في مدخلة المعرّف جوّه `gs1-application-identifiers.jsonld`، وده اللي كل وصف معرّف بيرجعله في الآخر.

البيدجن النيجيرية (`NIGERIAN_PIDGIN`)، وهي كريولية أساسها الإنجليزية، بتعيد استعمال النص الإنجليزي عن قصد في تسميات التفسير ورسايل الأخطاء. ووصوف المعرّفات دي الاستثناء بتاع الاستثناء ده: هي مترجمة بصيغة بيدجن حقيقية بدل ما تعيد استعمال الإنجليزية، علشان فهارس وصوف المعرّفات اتعملت بمعزل عن فهارس التسميات والرسايل. ويُستحسن إن اللي بيتكلّموا اللغة كلغة أم يراجعوا الترجمات الآلية قبل ما تعتمد عليها في بيئة التشغيل.

أي رسالة أو تسمية أو وصف مش موجودة في فهرس لغة معيّنة بترجع للإنجليزية. واللغات اللي بتتكتب من اليمين للشمال (العربية والأردية والعربية المصرية والفارسية) متخزّنة صح كنصوص؛ إنما عرضها من اليمين للشمال ده شغل طبقة العرض.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

وتسميات التفسير بتتترجم بنفس الطريقة (القيم ما بتتغيّرش — التسميات بس):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

ووصوف المعرّفات بتتترجم بنفس الطريقة (اللي ما بيتترجمش بس هو `getTitle()`، زي `"GTIN"`):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### تنسيق التاريخ

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## مُعدِّلات المدخل

**مُعدِّل المدخل** ده كود بيعيد كتابة نص المدخل الخام قبل ما Gaia يحلّله. المُعدِّلات موجودة علشان المدخل اللي بيوصل وهو باظ أصلًا — ماسح بيحطّ مكان فاصل GS محرف يتطبع، أو وسيط برمجي بيلفّ الحمولة في بادئة خاصة بالمورّد، أو نظام مضيف بيحوّل كل حاجة لحروف كبيرة. فبدل ما تعالج كل نص قبل التحليل في كل مكان بتنادي منه (وتغلط غلطة صغيّرة في واحد منهم)، سجّل التطبيع مرة واحدة على `ParseConfig` وسيب المحلّل هو اللي يطبّقه.

المُعدِّلات بتشتغل في أول `GaiaParser.parse(...)` خالص — قبل ما يتشال معرّف الارتباط، وقبل كشف معرّف ترميز AIM، وقبل مسار GS1. وكل اللي بعد كده مش بيشوف غير النص بعد إعادة الكتابة. **ومفيش حاجة مضبوطة افتراضيًا**، حتى [المُعدِّلين المدمجين](#المعدلات-المدمجة) — انت اللي بتشغّلهم لكل `ParseConfig` لوحدها.

**الواجهة:** `tools.pantheum.gaia.modifier.ModifierInterface`

### المُعدِّلات المدمجة

في نواة الـ jar جايين مُعدِّلين، في `tools.pantheum.gaia.modifier.custom`. وهما بيغطّوا أكتر طريقتين بتوصل بيهم حمولة GS1 وهي باظت — أقواس HRI المطبوعة اللي بتتحسب بيانات، والمسافات الزيادة — فالحالات الشائعة مش محتاجة كلاس مخصّص:

<div dir="rtl">

| الكلاس | `getName()` | بيعمل إيه |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | بيشيل أقواس HRI اللي حوالين كل معرّف (`(01)…(10)…`) وبيرجّع فاصل FNC1 اللي كانت بتدلّ عليه. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | بيشيل كل مسافة (`0x20`) من سلسلة عناصر المعرّفات. |

</div>

الاتنين دول تطبيقين عاديين لـ `ModifierInterface` من غير أي وضع خاص — بيتسجّلوا ويتترتّبوا ويتبلّغ عنهم ويفشلوا بالظبط زي اللي انت كاتبه:

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

الاتنين من غير حالة وآمنين مع الخيوط المتعددة، يعني نسخة واحدة ممكن يستعملها الكل؛ والاتنين كمان ممكن تناديهم باسم الكلاس الكامل في الإعدادات اللي بتتبني على ملفات ضبط (شوف [تسجيل المُعدِّلات](#تسجيل-المعدلات)).

#### `ModifierRemoveAIBrackets`

التفسير اللي الإنسان يقراه في GS1 بيطبع كل معرّف بين قوسين — `(01)09521234543213(10)ABC123` — وده عرف طباعي بحت. وأي ماسح أو وسيط برمجي مضبوط علشان يطلّع HRI بيمرّر الأقواس دي كأنها بيانات، والمقطِّع مش عارف يعمل بيها إيه.

وشيل الأقواس ده نص الشغل بس. في HRI، القوس الفاتح بتاع المعرّف *اللي بعده* هو اللي بيعلّم نهاية القيمة اللي قبله، فبالشكل ده المعرّف متغيّر الطول مش محتاج FNC1. وشيل الأقواس من غير تفكير هيخلّي الحدّ ده يختفي:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

علشان كده المُعدِّل **بيرجّع يحطّ FNC1 عند كل حدّ يكون المعرّف اللي قبله متغيّر الطول**، وبيرجّع بالظبط اللي كانت الأقواس بترمّزه:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

الطول بيتدوّر عليه في `AiDefinitionRegistry` بتاعة المحلّل نفسه، فكل معرّف متغيّر الطول بيتعالج بدل قايمة متثبّتة في الكود. وتلات حالات بتتساب عن قصد من غير مساس: قيمة خلصانة أصلًا بـ FNC1 (مصدر بيطلّع العرفين الاتنين مش هياخد فاصل تاني)، ورمز بين قوسين مش معرّف معروف (المعرّف المجهول ما بيقولش حاجة عن طوله)، وآخر معرّف في النص.

وإعادة الكتابة دي **متماثلة القوى** — لو شغّلتها على خرجها هي نفسها مش هيتغيّر حاجة — فهي آمنة على تدفّق مخلوط جزء بس من مدخلاته بين قوسين.

> **حدّ.** القوسين `(` و`)` دول نفسهم محارف بيانات صحيحة في GS1، والنمط المستعمل هنا مش أكتر من `\((\d{2,4})\)`. فلو صدفة في قيمة فيها رقم من رقمين لأربعة أرقام بين قوسين، هيتشال منه القوسين كمان. طبّق ده بس على مصدر بيستعمل عرف أقواس HRI، مش على مصدر عنده قيم بين قوسين بجدّ.

#### `ModifierRemoveSpaces`

في ماسحات ووسائط برمجية ومسارات طباعة ملصقات بتحطّ مسافات زيادة في سلسلة عناصر كانت سليمة — علشان تملّي حقل ثابت العرض، أو تفصل مجموعات علشان تتقرا، أو تلفّ قيمة طويلة. والمقطِّع بيحسب كل مسافة منهم بيانات، فبتبوظ القيمة اللي هي جوّاها، ولو المعرّف متغيّر الطول بيتزحزح كل اللي بعده.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

اللي بيتشال بس هو ASCII `0x20`. والمسافات البيضا التانية بتفضل مكانها — يعني التاب مثلًا بره مجموعة محارف GS1 القابلة للترميز، فالمحلّل بيبلّغ عنه كـ `GE-S008` بدل ما يكنسه في سكوت.

> **حدّ.** المسافة (`0x20`) جزء من مجموعة محارف GS1 الثابتة، يعني ممكن رقم تشغيلة/دفعة أو رقم قطعة عند العميل يكون فيه مسافة بشكل مشروع. والمُعدِّل مش قادر يفرّق بين المسافة الزيادة والمسافة الحقيقية؛ فطبّقه بس على مصدر انت عارف إنه مش بيستعمل مسافات جوّه قيم معرّفاته.

#### البادئات بتتخطّى، مش بتتعاد كتابتها

المُعدِّلات بتشتغل والمحلّل لسه ما شالش حاجة، فالمدخل الخام ممكن لسه يكون شايل معرّف ارتباط ومعرّف ترميز AIM ومؤشّر ECI. والمُعدِّلين المدمجين الاتنين بيلاقوا بداية سلسلة عناصر المعرّفات بمنطق `CorrelationIdParser` و`DataCarrierParser` بتاع المحلّل نفسه، وبيعيدوا الكتابة من هناك ورايح بس، وبعدين بيوصّلوا الناتج بالبادئة اللي **ما اتلمستش**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

أما نواقل EAN/UPC اللي قيمتها بتتملّي لحد GTIN-14 (`isRequiresGtinPadding()`) فبتتخطّى بالكامل — حمولتها قيمة باركود رقمية خام من غير أي بنية معرّفات، فلا الأقواس ولا المسافات ممكن يبقى ليها معنى هناك.

#### الترتيب: المسافات قبل الأقواس

لما تستعمل الاتنين، **سجّل `ModifierRemoveSpaces` الأول**. مطابقة الأقواس حسّاسة للمكان: `( 01 )` المفتوحة بمسافات مش هتطابق `\((\d{2,4})\)`، فالأقواس هتفضل والفاصل اللي كانت بتدلّ عليه مش هيرجع أبدًا.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### إزاي تكتب مُعدِّل

اكتب مُعدِّلك انت لما ولا واحد من المدمجين يظبط معاك — الواجهة فيها دالة واحدة بس.

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

ولو إعادة الكتابة معتمدة على ضبط التحليل، تجاهد النسخة اللي بتاخد وسيطين بدل كده:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

الاتفاق:

<div dir="rtl">

| القاعدة | التفصيل |
|---|---|
| من غير حالة وآمن مع الخيوط المتعددة | بيتخزّن نسخة واحدة لكل كلاس وبتتشارك في كل عمليات التحليل. |
| باني عام من غير وسائط | مطلوب بس لما المُعدِّل يتنادى باسم الكلاس. |
| اتعامل مع `null` والمدخل الفاضي | المحلّل مش بيرشّحهم قبل ما السلسلة تشتغل. |
| ترجّع `null` معناها «مفيش تغيير» | القيمة اللي قبلها بتتمرّر زي ما هي. رجّع `input` من غير تغيير لما المُعدِّل ما يبقاش منطبق. |
| ترجّع من غير تغيير أحسن من إنك ترمي استثناء | المُعدِّل اللي بيرمي استثناء بيلغي التحليل — شوف [معالجة الإخفاق](#معالجة-إخفاق-المعدل). |
| `getName()` | تجاهدها علشان تتحكّم في الاسم اللي هيتبلّغ عنه في `ModifierInfo`؛ والافتراضي هو اسم الكلاس البسيط. |

</div>

### تسجيل المُعدِّلات

المُعدِّلات بتشتغل بالترتيب اللي بتضيفها بيه، وكل واحد بياخد خرج اللي قبله. سجّلهم بنسخة كائن، أو باسم الكلاس الكامل، أو بقايمة من أي واحد فيهم:

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

[المُعدِّلين المدمجين](#المعدلات-المدمجة) بيتسمّوا بنفس الطريقة اللي بتسمّي بيها بتوعك — **باسم كامل دايمًا**. مفيش ليهم اسم مختصر ولا بحث بأسماء مستعارة؛ `ModifierRegistry` بيحلّ كل مُعدِّل، سواء جاي مع المكتبة أو لأ، باسم الكلاس الكامل.

الأسامي بيحلّها `ModifierRegistry`، اللي بيعمل نسخة واحدة من كل كلاس بالباني اللي من غير وسائط ويخزّنها لأي ضبط بعد كده بيسمّي نفس الكلاس. والحلّ بيحصل **وقت بناء الضبط**، فأي اسم مش هيتلاقي، أو مش بينفّذ `ModifierInterface`، أو مش هينفع تتعمل منه نسخة، هيرمي `IllegalArgumentException` هناك على طول — مش في سكوت وقت التحليل. وأي مُعدِّل مش هينفع يتبني بالانعكاس (يعني مثلًا شايل اعتمادية محقونة) تقدر تسجّله من بدري علشان يفضل ينفع تناديه بالاسم:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### إزاي تشوف المُعدِّل عمل إيه

لما المُعدِّلات تكون مضبوطة، `ParseResult.getPayload()` بيوري المدخل **بعد التعديل**. أما الأصلي فمحفوظ في `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` بيبلّغ عن `getName()` بتاعة كل مُعدِّل، واللي افتراضها اسم الكلاس البسيط، بس المُعدِّلين المدمجين بيجاهدوها — فسلسلة من الاتنين دول بتبلّغ عن أسامي العرض مش أسامي الكلاسات:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

و`getModifierInfo()` بترجّع `null` لما ما يكونش في أي مُعدِّلات مضبوطة. ولو المُعدِّلات اشتغلت بس كلها رجّعت المدخل زي ما هو، المعلومة بتفضل موجودة و`isModified()` بتبقى `false` — واللي بيتحطّ في `getAppliedModifiers()` هو بس المُعدِّلات اللي غيّرت المدخل فعلًا.

### معالجة إخفاق المُعدِّل

المُعدِّل اللي بيرمي استثناء بيلغي التحليل. والاستثناء بيتلفّ في `GaiaModifierException` بيسمّي المُعدِّل اللي عمل المشكلة، والنتيجة بتشيل خطأ داخلي `GE-I001` ورسالته فيها الاسم ده؛ و`getPayload()` بيبلّغ عن المدخل من غير تعديل. والتحليل عن قصد **مش** بيكمّل بنص مكتوب نص كتابة — خطوة تطبيع فشلت في سكوت هتطلّع نتايج شكلها سليم بس هي متحلّلة من مدخل غلط.

---

## أوضاع التحليل

كل وضع اسمه على أعمق [مرحلة في المسار](#مسار-التحليل) بيشغّلها؛ وكل مرحلة قبلها بتشتغل برضه.

<div dir="rtl">

| الوضع | بيشتغل لحد | بيجاوب على إيه |
|---|---|---|
| `DATA_CARRIER` | المرحلة 1 (توجيه المدخل) | أنهي نظام ترميز شال ده؟ |
| `SYNTAX` | المرحلة 2 (البنية النحوية) | رموز المعرّفات وأطوالها سليمة؟ |
| `CONTENT` | المرحلة 3 (المحتوى) | القيم دي بيانات GS1 صحيحة؟ |
| `INTERPRETATION` | المرحلة 4 (التفسير) | القيم معناها إيه؟ |

</div>

### وضع DATA_CARRIER

بيقف بعد المرحلة 1 — بيتحقّق من معرّف ترميز AIM ويحدّد نظام الترميز، بس مش بيدخل مسار تحليل المعرّفات. مفيد لتحديد نظام الترميز والتوجيه من غير تكلفة التحقق الكامل.

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

**استعمله لما:** تطبيقك يكون محتاج يعرف نوع الباركود قبل ما يقرّر يعالج الحمولة إزاي — زي إنك توجّه أنظمة الترميز الأحادية البُعد والثنائية لمعالجات مختلفة. وللتوجيه ده، استعمل [`DataCarrierType`](#datacarrierentry-وdatacarriertype) المصنَّف (`getDataCarrier().getDataCarrierType()`) بدل ما تطابق `getName()` كنص.

---

### وضع SYNTAX

بيقف بعد المرحلة 2. مفيد للفرز البنيوي المبدئي من غير تكلفة التحقق من المحتوى.

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

**استعمله لما:** تحبّ تتأكّد إن رموز المعرّفات وأطوال البيانات سليمة قبل ما تدخل في التحقق الكامل، أو لما تكون بتمسح كميات كبيرة وأخطاء المحتوى فيها نادرة.

---

### وضع CONTENT

بيقف بعد المرحلة 3.

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

> أغلب المعرّفات مش بتقدر تقف لوحدها: المعرّفات `10` (BATCH/LOT) و`17` (USE BY or EXPIRY)
> و`21` (SERIAL) كل واحد فيهم *بيحتاج* مفتاح تعريف زي المعرّف `01` في نفس سلسلة العناصر،
> يعني لو شيلت GTIN من المثال اللي فوق هيفشل في المرحلة 2 بـ `GE-S005` ومش هيوصل أصلًا
> للتحقق من المحتوى. اضبط `skipRequiresCheck(true)` على `ParseConfig` علشان تحلّل أجزاء
> شايل عن قصد معرّفاتها المرافقة.

**استعمله لما:** تكون محتاج تعرف إن القيمة الممسوحة متوافقة تمامًا مع GS1 قبل ما تستعملها في إجراء شغل، من غير ما تدفع تكلفة الإغناء بالتفسير.

---

### وضع INTERPRETATION (الافتراضي)

بيشغّل المسار كامل لحد المرحلة 4. وده الافتراضي لما تنادي `parse(String)` من غير وسيط وضع. وما بيغنيش غير العناصر اللي عدّت التحقق من المحتوى نضيف.

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

**مثال للخرج:**
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

**مثال لمبلغ نقدي (المعرّف 3932 — سعر برمز عملة ISO):**
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

**استعمله لما:** تكون بتبني طبقات عرض، أو أدوات تدقيق ملصقات، أو أي واجهة محتاجة تفكّك قيم المعرّفات بشكل قريب للناس.

---

## معرّف الارتباط

في مسارات شغل بتحطّ معرّف ارتباط خاص بيها من تمن أرقام قدّام مدخل GS1 الخام، علشان تقدر تربط أحداث المسح بجلسة أو معاملة. والشكل بتاعه كده:

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

علامة التلدة (`~`) هي الفاصل. وهي **مش** جزء من محتوى GS1 — بتتشال قبل ما أي تحليل لـ GS1 يبدأ.

### قواعد الكشف

البادئة بتتكشف لما المدخل يبدأ بتمن أرقام عشرية ASCII بالظبط (`0`–`9`) وبعدهم على طول `~`. ولو المحرف التاسع مش `~`، أو واحد من أول تمن محارف مش رقم، المدخل بيتعامل كمحتوى GS1 عادي من غير بادئة ارتباط.

### إزاي توصل لمعرّف الارتباط

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

### لما يتجمع مع معرّف ترميز AIM

بادئة الارتباط ممكن تيجي قبل معرّف ترميز AIM. والمحلّل بيتعامل مع ده من غير ما تحسّ:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**كلاس التنفيذ:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** بيرمّز قيمة معرّف تطبيق واحدة أو أكتر جوّه تركيب عنوان HTTP(S) على طول، فبيدّي المنتجات المادية معرّفات ينفع تتحلّ عن طريق الويب. وGAIA بينفّذ *GS1 Digital Link Standard: URI Syntax* (الإصدار 1.7.0) للعناوين **غير المضغوطة**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` بيعرف عناوين Digital Link لوحده — أي مدخل بيبدأ بـ `http://` أو `https://` بيتوجّه لـ `GS1DLParser`، اللي بيشغّل نفس مرحلتَي المحتوى والتفسير اللي بيشغّلهم مسار سلسلة العناصر.

### تركيب العنوان وأدوار المعرّفات

كل معرّف في عنوان Digital Link بياخد واحد من تلات أدوار، وبيبان على كل `GS1AIObjectElement` عن طريق `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

<div dir="rtl">

| الدور | المكان | مثال |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | أول زوج `/ai/value` في المسار (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | أزواج المسار اللي بعده، مترتّبة حسب المفتاح الأساسي (§4.9) | `/10/ABC`، `/21/SER` |
| `DATA_ATTRIBUTE` | معاملات الاستعلام اللي مفاتيحها كلها أرقام (§4.10) | `?17=271231` |

</div>

القواعد البنيوية المفروضة (`DLPathRules`):
- مفتاح تعريف أساسي **واحد** بالظبط في المسار؛ وأي مفاتيح زيادة لازم تترمّز كسمات بيانات في الاستعلام.
- مقيّدات المفاتيح لازم المفتاح الأساسي يقبلها ولازم تيجي بالترتيب المحدّد. والمقيّدات الاختيارية ممكن تتشال، بس اللي *موجود* منها لازم برضه يمشي بالترتيب الثابت — شوف [ترتيب المقيّدات](#ترتيب-المقيدات).
- ممكن تيجي قبل المفتاح الأساسي مقاطع مسار مخصّصة أيًا كانت (زي `/products/au/01/...`)؛ هاتها بـ `getDigitalLinkInfo().getCustomPathStem()`.
- مفاتيح الاستعلام اللي مش رقمية (`linkType`، و`context`، ومعاملات التوسعة زي `23P`) بتتجاهل؛ أما المفاتيح اللي كلها أرقام فلازم تكون معرّفات صحيحة معلّمة بـ `validAsDataAttribute`.
- محارف القيم المرمّزة بعلامة النسبة بيتفكّ ترميزها؛ والمعرّفين `(03)` و`(8014)` ممنوعين.

المفاتيح الأساسية وتسلسلات المقيّدات اللي بتقبلها **مستمدّة من البيانات** في تعريفات المعرّفات — من العلَم `gs1DigitalLinkPrimaryKey` والسمة `gs1DigitalLinkQualifiers` — مش متثبّتة في الكود.

وأي مخالفة بنيوية، أو مدخل مش عنوان أصلًا، بيطلّع خطأ بنيوي في Digital Link (`GE-L001`–`GE-L014`، رمز لكل حالة). أما البيانات الوصفية للعنوان بعد تفكيكه (`scheme` و`domain` و`path` و`customPathStem` و`query`، و`java.net.URL`) فمتاحة عن طريق `getDigitalLinkInfo()` حتى لو في أخطاء بنيوية.

### ترتيب المقيّدات

لكل مفتاح أساسي، `gs1DigitalLinkQualifiers` بيسرد تسلسل **مترتّب** واحد أو أكتر من المقيّدات. وجوّه التسلسل، المعرّف اللي بين قوسين معقوفين **اختياري**، واللي من غير قوسين **مطلوب** — وده نفس ترميز `[cpv-comp]` في صيغة ABNF بتاعة القسم §4.9. وتسلسلات المفتاح الأساسي الواحد دي بدايل بتستبعد بعضها.

يعني GTIN (`01`) مثلًا بيعرّف تسلسلين:

<div dir="rtl">

| المسار | التسلسل | المعنى |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV وLOT وSER — كل واحد اختياري، بس بالترتيب ده مثبّت |
| upui-path | `235` | TPX (مطلوب)؛ GTIN + TPX = UPUI |

</div>

يعني `/01/09506000134352/10/LOT-ABC/21/SER` صحيح (LOT قبل SER، وCPV اتشال)، و`/01/.../21/SER/10/LOT-ABC` **مرفوض** (الترتيب باظ)، و`/01/09506000134352/235/2ABC456` ده upui-path. وفحص الترتيب ده مطابقة متتالية جزئية بتحافظ على الترتيب، فالمعرّفات الاختيارية ممكن تتخطّى بس ترتيبها عمره ما يتغيّر.

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

**كلاس التنفيذ:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## الشغل بالنتايج

### ParseResult

النتيجة العليا اللي `GaiaParser.parse()` بيرجّعها.

<div dir="rtl">

| الدالة | بترجّع | الوصف |
|---|---|---|
| `isValid()` | `boolean` | `true` لو مفيش أخطاء على أي مستوى. والتحذيرات مش بتأثّر على الصلاحية. وبتبقى `true` دايمًا لما `getAiObject()` تساوي `null`. |
| `getPayload()` | `String` | نص المدخل بعد ما تتشال بادئة الارتباط — وبعد ما تكون [مُعدِّلات المدخل](#معدلات-المدخل) عادت كتابته. |
| `getPayloadContent()` | `String` | الحمولة بعد ما يتشال منها معرّف ترميز AIM وبادئة ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`، أو `GS1_DIGITAL_LINK`، أو `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (ناقل بيانات اترفض علشان مش GS1، زي ناقل Code 39 بمعرّف `]A0`)، أو `UNABLE_TO_DETERMINE_CONTENT` (لما `aiObject` تساوي `null`، زي في وضع `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | عمق المسار المضبوط (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | أعمق مرحلة التحليل وصلها فعلًا — شوف تحت. |
| `isParseComplete()` | `boolean` | `true` لو التحليل وصل للعمق المطلوب (`achieved == requested`). ومستقلة عن `isValid()`. |
| `getAiObject()` | `GS1AIObject` | كل المعرّفات اللي اتحلّت. وبتساوي `null` في وضع `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | كل الأخطاء اللي مش WARNING (مستوى الكائن + كل مستويات العناصر). |
| `getWarnings()` | `List<GaiaError>` | كل تنبيهات WARNING (مستوى الكائن + كل مستويات العناصر). |
| `hasWarnings()` | `boolean` | `true` لو اتطلّع أي تنبيه WARNING. |
| `getIssues()` | `List<GaiaError>` | الأخطاء والتحذيرات مع بعض. |
| `hasDataCarrier()` | `boolean` | `true` لو اتعرف على معرّف ترميز AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | البيانات الوصفية لنظام الترميز، أو `null` لو ما اتحدّدش ناقل. |
| `hasEci()` | `boolean` | `true` لو اتشال مؤشّر ECI من الحمولة. |
| `getEci()` | `EciEntry` | البيانات الوصفية لترميز ECI، أو `null`. |
| `hasCorrelationId()` | `boolean` | `true` لو كان في بادئة ارتباط `DDDDDDDD~` في المدخل الأصلي. |
| `getCorrelationInfo()` | `CorrelationInfo` | معرّف الارتباط المستخرج، أو `null` لو ما كانش موجود. |
| `isInputModified()` | `boolean` | `true` لو [مُعدِّل مدخل](#معدلات-المدخل) غيّر المدخل. |
| `getModifierInfo()` | `ModifierInfo` | سلسلة المُعدِّلات عملت إيه — `getOriginalInput()`، و`getModifiedInput()`، و`getAppliedModifiers()`. وبتساوي `null` لو ما اتضبطش أي مُعدِّلات. |
| `getTiming()` | `ProcessingTiming` | توقيت التحليل بساعة الحيط — `getStartTime()` (`Instant`)، و`getProcessingTime()` (`Duration`)، و`getProcessingTimeMillis()` (`long`)، و`getCompletionTime()`. وبتساوي `null` لو مش `GaiaParser` اللي طلّعها. |
| `getVersion()` | `String` | إصدار المكتبة اللي طلّع النتيجة. |

</div>

#### الوضع المطلوب مقابل الوضع اللي اتحقّق

المسار بيطلع سلّم **SYNTAX → CONTENT → INTERPRETATION** وبيقف بدري لو في أخطاء، فالوضع اللي *اتحقّق* فعلًا ممكن يبقى أضحل من الوضع *المطلوب*. و`getAchievedParseMode()` بيبلّغك وصل لحد فين:

<div dir="rtl">

| المطلوب | اللي بيحصل | اللي اتحقّق | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | خطأ **نحوي / بنيوي** بيوقّف التحليل بعد التقطيع | `SYNTAX` | `false` |
| `INTERPRETATION` | خطأ **محتوى** (تنسيق أو رقم تحقق غلط) بيمنع الإغناء | `CONTENT` | `false` |
| `CONTENT` | مرحلة المحتوى بتشتغل لآخرها دايمًا (الأخطاء بتتعلّم مش بتوقّف) | `CONTENT` | `true` |
| أي واحد (مدخل نضيف) | المسار بيوصل للعمق المطلوب | = المطلوب | `true` |
| `DATA_CARRIER` | الناقل اتحقّق منه؛ ومحتوى المعرّفات ما اتحلّلش | `DATA_CARRIER` | `true` |
| أي واحد | ناقل البيانات اترفض قبل تحليل المعرّفات (زي ناقل `]A0` اللي مش GS1) | `SYNTAX` | `false` |

</div>

`isParseComplete()` مستقلة عن `isValid()`: يعني تحليل `CONTENT` لـ GTIN رقم تحققه غلط ده **مكتمل** (مرحلة المحتوى اشتغلت) بس **مش صحيح** (رقم التحقق فشل). استعمل `isParseComplete()` علشان تسأل «المسار اشتغل لحد العمق اللي طلبته؟» و`isValid()` علشان تسأل «البيانات سليمة؟».

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

مجموعة عناصر المعرّفات اللي اتحلّت.

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getAis()` | كل نسخ `GS1AIObjectElement` بترتيب المدخل. |
| `get(String aiCode)` | أول عنصر مطابق لرمز المعرّف اللي بعتّه، أو `null`. |
| `contains(String aiCode)` | `true` لو في معرّف بالرمز ده. |
| `size()` | عدد المعرّفات اللي اتحلّت. |
| `isValid()` | `true` لو مفيش أخطاء على مستوى الكائن ومفيش عنصر فيه أخطاء. |
| `toHriString()` | نص HRI، زي `(01)09506000134352 (17)261231`. |
| `toElementString()` | سلسلة العناصر الخام — من غير أقواس، وبـ FNC1 بعد كل عنصر متغيّر الطول — زي `010950600013435210LOT-ABC<GS>17271231`. وبترجّع `null` لو `isValid()` كانت `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` لما `hasDigitalLink()` تكون صح، وغير كده `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` لو المدخل كان عنوان GS1 Digital Link شايل مفتاح تعريف أساسي. أما عنوان سليم من غير مفتاح أساسي فبيفضل يوفّر `getDigitalLinkInfo()` بس بيرجّع `false` هنا. |
| `getCanonicalDigitalLink()` | عنوان GS1 Digital Link المعياري (§4.12) على `https://id.gs1.org` — المفتاح الأساسي والمقيّدات كمقاطع مسار، وسمات البيانات كمعاملات استعلام مترتّبة بمفتاح المعرّف — أو `null` لو مفيش مفتاح أساسي. |
| `getDigitalLinkInfo()` | البيانات الوصفية لتفكيك العنوان (`getUri()`، `getUrl()`، `scheme`، `domain`، `path`، `getCustomPathStem()`، `query`)، أو `null` لو مش Digital Link. |
| `getAllErrors()` | أخطاء مستوى الكائن + كل أخطاء العناصر (ما عدا WARNING). |
| `getAllWarnings()` | تحذيرات مستوى الكائن + كل تحذيرات العناصر. |
| `getAllIssues()` | كل ده مع بعض. |

</div>

---

### GS1AIObjectElement

نسخة واحدة من معرّف اتحلّ.

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getAi()` | رمز المعرّف، زي `"01"` و`"3102"`. |
| `getTitle()` | عنوان بيانات GS1، زي `"GTIN"` و`"BATCH/LOT"`. |
| `getDescription()` | وصف GS1 الكامل للمعرّف، **مترجم للغة التحليل** (بالإنجليزي مثلًا `"Global Trade Item Number (GTIN)"`). وبيرجع للنص الإنجليزي في تعريف المعرّف لو ما كانش مترجم. |
| `getFormatString()` | واصف التنسيق اللي بيغطّي المعرّف *وبياناته*، زي `"N2+N14"` للمعرّف `01`، و`"N2+X..20"` للمعرّف `10`، و`"N4+N3+N..15"` للمعرّف `3932`. |
| `getValue()` | قيمة البيانات الخام المستخرجة من سلسلة العناصر. |
| `isFixedLength()` | `true` لو طول بيانات المعرّف ثابت. |
| `getPosition()` | إزاحة المحرف في المدخل الأصلي، من الصفر. |
| `getGS1ComponentValues()` | شرايح القيم لكل مكوّن (للمعرّفات اللي فيها كذا مكوّن). |
| `getErrors()` | أخطاء مستوى العنصر اللي مش WARNING. |
| `getWarnings()` | تنبيهات WARNING على مستوى العنصر. |
| `getIssues()` | أخطاء العنصر وتحذيراته مع بعض. |
| `hasErrors()` | `true` لو في أي خطأ مش WARNING مربوط. |
| `hasWarnings()` | `true` لو في أي تنبيه WARNING مربوط. |
| `getInterpretations()` | مدخلات `GS1AIInterpretation` (بتتملّي في وضع INTERPRETATION). |
| `getInterpretation(String type)` | أول تفسير مطابق لمفتاح النوع من `GS1Constants_Enricher` اللي بعتّه، أو `null`. |
| `getDigitalLinkAIType()` | دور العنصر في Digital Link (`PRIMARY_IDENTIFICATION_KEY`، `KEY_QUALIFIER`، `DATA_ATTRIBUTE`)، أو `null` لمدخلات سلاسل العناصر. |
| `hasDigitalLinkAIType()` | `true` لو اتحدّد دور Digital Link. |

</div>

---

### GaiaError

خطأ تحقق أو تنبيه مش بيتغيّر.

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getId()` | معرّف الفهرس، زي `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`، أو `INTEGRITY_ERROR`، أو `FORMAT_ERROR`، أو `DATA_ERROR`، أو `WARNING`. |
| `getStage()` | `DATA_CARRIER`، أو `DIGITAL_LINK`، أو `SYNTAX`، أو `CONTENT`، أو `INTERNAL`. |
| `getCode()` | رمز قصيّر تقراه الآلة. |
| `getAi()` | رمز المعرّف اللي عمل الخطأ، أو `null` لأخطاء مستوى الكائن. |
| `getMessage()` | رسالة يقراها الإنسان وقيمها متحطّة جوّاها. |
| `getPosition()` | إزاحة المحرف في المدخل الأصلي، من الصفر. |

</div>

---

### GS1AIInterpretation

جزء تفسير واحد ليه تسمية، بيتربط بـ `GS1AIObjectElement` في وضع `INTERPRETATION`.

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getType()` | مفتاح النوع اللي تقراه الآلة، زي `"DATE_VALUE"` و`"GS1_COMPANY_PREFIX"`. وثابت في كل اللغات. |
| `getLabel()` | تسمية يقراها الإنسان، **مترجمة للغة التحليل** (بالإنجليزي مثلًا `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | القيمة المستخرجة أو المُغناة، زي `"31/12/2026"` و`"9506000"`. ومش بتتترجم. |

</div>

---

### DataCarrierEntry وDataCarrierType

لما المدخل يكون شايل معرّف ترميز AIM، `ParseResult.getDataCarrier()` بيرجّع `DataCarrierEntry` بيوصف الرمز اللي شال البيانات. والمدخلة دي هي سجلّ الفهرس المحدّد لمعرّف ترميز AIM اللي طابق؛ أما `DataCarrierType` فهو التعداد بتاع وقت الترجمة اللي هي تابعة له.

#### DataCarrierEntry

البيانات الوصفية لمعرّف ترميز AIM واحد اتعرف عليه (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getAimCodeId()` | معرّف ترميز AIM اللي طابق، زي `"]C1"`. |
| `getName()` | الاسم اللي يقراه الإنسان للرمز المحدّد، زي `"GS1-128 / ISBT 128"` و`"EAN-8"`. |
| `getDescription()` | وصف أطول للناقل. |
| `getType()` | النوع البنيوي للناقل كنص (بيعكس `getDataCarrierType().getCategory()`). |
| `getStandard()` | معيار نظام الترميز، لو مسجّل. |
| `getDataCarrierType()` | قيمة `DataCarrierType` المصنَّفة للمدخلة دي — استعملها في التوجيه البرمجي. |
| `isGs1Capable()` | `true` لو الناقل يقدر يشيل بيانات GS1 (سلاسل عناصر معرّفات و/أو Digital Link). |
| `isGs1AICapable()` | `true` لو الناقل يقدر يشيل سلاسل عناصر معرّفات GS1. |
| `isGs1DigitalLinkCapable()` | `true` لو الناقل يقدر يشيل عنوان GS1 Digital Link. |
| `isEciCapable()` | `true` لو الناقل بيدعم مؤشّر ECI. |
| `isRequiresGtinPadding()` | `true` لنواقل EAN/UPC/ITF اللي قيمتها الرقمية بتتملّي لحد GTIN-14 قبل تحليل المعرّفات. |

</div>

#### DataCarrierType

تعداد وقت ترجمة لأنواع نواقل البيانات، مفتاحه معرّف ترميز AIM المخصّص في ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). والمحرف اللي بعد `]` (*محرف الرمز*) بيختار العيلة؛ وأغلب العيل بتتطابق مع ثابت واحد بيغطّي كل المُعدِّلات (`ITF` بيغطّي `]I0`–`]I2`؛ و`EAN_UPC` بيغطّي EAN-13 وUPC-A وUPC-E وEAN-8). ولما GS1 تحجز مُعدِّل لبيانات المعرّفات، الصورة دي بيبقى ليها ثابت لوحدها — `GS1_128` (`]C1`)، و`GS1_DATA_MATRIX` (`]d2`)، و`GS1_QR_CODE` (`]Q3`)، و`GS1_DOT_CODE` (`]J1`) — منفصلة عن نظيرها العادي. ولما ما يكونش في معرّف ترميز AIM، أو يكون بيسمّي ناقل مش معروف، النوع بيبقى `UNKNOWN`.

<div dir="rtl">

| الدالة | الوصف |
|---|---|
| `getCategory()` | الفئة العريضة `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`، أو `STACKED_LINEAR`، أو `TWO_D`، أو `POSTAL`، أو `OCR`، أو `OTHER`. |
| `getCodeChar()` | محرف رمز AIM اللي بيحدّد العيلة، زي `"Q"` لـ QR Code؛ و`null` للنوع `UNKNOWN`. |
| `getDisplayName()` | الاسم اللي يقراه الإنسان لـ *النوع* (ممكن يبقى أعمّ من `DataCarrierEntry.getName()` — زي `"EAN-13 / UPC-A / UPC-E / EAN-8"` مقابل `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` للثوابت اللي بتدلّ دايمًا على بيانات معرّفات GS1: الأربع صور المحجوزة لـ GS1 (`GS1_128` و`GS1_DATA_MATRIX` و`GS1_QR_CODE` و`GS1_DOT_CODE`) وكمان `GS1_DATABAR`، اللي هو GS1 بطبيعته علشان كل مُعدِّل `]e` هو GS1 DataBar. وهي أضيق من `DataCarrierEntry.isGs1AICapable()` — علشان `QR_CODE` العادي برضه ممكن يشيل بيانات معرّفات GS1. |
| `static forAimCodeId(String)` | بيحلّ النوع من معرّف ترميز AIM على طول (`"]Q3"` → `GS1_QR_CODE`؛ و`"]Q9"` → `QR_CODE`)؛ وبيرجّع `UNKNOWN` لمعرّف مش موجود أو باظ أو مش معروف. |

</div>

التوجيه بالنوع مش بالاسم — زي إنك تفصل الرموز الأحادية البُعد (Code-128) عن الثنائية (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` بيغطّي رموز المصفوفات والنقط بس؛ أما النواقل الأحادية البُعد المكدّسة (`PDF417`
و`CODE_16K` و`CODABLOCK` و`CODE_49`) فهي `STACKED_LINEAR`، حتى لو الناس بتسمّيها باركود
«ثنائي البُعد» على طول. وعلشان تتعامل مع الاتنين كمجموعة واحدة — يعني مثلًا علشان تقرّر
إنت محتاج مُصوِّر ولا ماسح ليزر — افحص الفئتين.

> حلّ النوع محتاج معرّف ترميز AIM يكون موجود في المسح؛ ومن غيره `getDataCarrier()` بتساوي `null` والنوع بيبقى `UNKNOWN`. اضبط الماسح علشان يبعت بادئة معرّف ترميز AIM.

---

## مرجع الأخطاء

<div dir="rtl">

| الرمز | المستوى | المرحلة | المعنى |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | بادئة معرّف مش معروفة — مش قادر يحدّد طول البيانات |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | المدخل قصيّر أوي على إنه يتقرا منه رمز معرّف كامل |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | قيمة مقطوعة — محارف أقلّ من اللي المعرّف محتاجه |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | معرّف تطبيق متكرّر في سلسلة العناصر |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | اعتمادية معرّف مطلوبة ناقصة |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | اقتران معرّفات ممنوع — معرّفين ما ينفعش يبقوا مع بعض |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | إخفاق مش متوقّع في التقطيع |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | محرف بره مجموعة محارف GS1 القابلة للترميز في سلسلة العناصر |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | فاصل FNC1 المطلوب ناقص بعد معرّف متغيّر الطول |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | بيانات زيادة بعد الحدود القصوى لكل المكوّنات |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | فاصل FNC1 بعد معرّف ثابت الطول في نص النص |
| `GE-W002` | WARNING | SYNTAX | FNC1 زيادة في آخر سلسلة العناصر (تنبيه بس) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | مخالفات بنيوية في عنوان Digital Link — رمز لكل حالة (عنوان باظ، أو مخطّط، أو مضيف، أو ترتيب مقيّدات، أو معرّف ممنوع، أو مفيش مفتاح أساسي (`GE-L013`)، أو مفاتيح أساسية كتير (`GE-L014`)، …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | القيمة مش مطابقة للنمط النظامي بتاع المعرّف |
| `GE-C003` | DATA_ERROR | CONTENT | التحقق من رقم التحقق فشل |
| `GE-C004` | DATA_ERROR | CONTENT | التحقق من زوج حرفَي التحقق فشل |
| `GE-C005` | FORMAT_ERROR | CONTENT | قيمة المكوّن فيها محرف بره مجموعة المحارف المسموحة |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | إخفاقات تنسيق المكوّنات — رمز لكل حالة تدقيق (شوف `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | إخفاقات التحقق الدلالي المخصّص — رمز لكل حالة تدقيق (شوف `content/validator/`). **استثناءات:** فحوص بادئة شركة GS1 الأربعتاشر اللي تحت مستواها `WARNING`، و`GE-C168` (رمز بلد رقمي مش معروف حسب ISO 3166-1) مستواه `FORMAT_ERROR`. |
| فحوص بادئة شركة GS1 | WARNING | CONTENT | المفتاح مش بيبدأ ببادئة شركة GS1 معروفة، في المعرّفات اللي فيها مفاتيح GS1 — `GE-C122` (CPID)، و`GE-C129` (GCN)، و`GE-C131` (GDTI)، و`GE-C132` (GIAI)، و`GE-C133` (GINC)، و`GE-C135` (GLN)، و`GE-C137` (GMN)، و`GE-C140` (GRAI)، و`GE-C142` (GSIN)، و`GE-C144` (GSRN)، و`GE-C146` (GTIN)، و`GE-C148` (HIDRI)، و`GE-C153` (ITIP)، و`GE-C165` (SSCC). تنبيه بس — مش بيأثّر على الصلاحية. |
| `GE-C169` | DATA_ERROR | CONTENT | رقم تحقق IMEI (Luhn) فشل في المعرّف 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | رقم تحقق EID (Luhn) فشل في المعرّف 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | معرّف ترميز AIM مش معروف |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | الناقل اتحدّد بس هو مش بيدعم لا سلاسل عناصر معرّفات GS1 ولا عناوين Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | خطأ داخلي مش متوقّع |

</div>

> **عيب معروف في عرض الرسايل.** قوالب الفهرس بتحطّ القيم المُدرَجة بين علامتين تنصيص
> مضاعفتين على طريقة MessageFormat (`''{value}''`)، بس `ErrorRegistry` بيدرج القيم بـ
> `String.replace` العادية، فالمضاعفة بتفضل لحد `getMessage()` — يعني دلوقتي هتشوف
> `value ''09506000134351''` في المكان اللي نصوص الرسايل المقتبسة في الدليل ده بتوري فيه
> `value '09506000134351'`. وده بيمسّ كل رسالة بتقتبس قيمة في فهارس الخمسة وتلاتين لغة
> كلها. ما تحلّلش رسايل الأخطاء؛ طابق على `getId()` / `getCode()`.

---

## الأمان مع الخيوط المتعددة

`GaiaParser` آمن مع الخيوط المتعددة أول ما يتبني. ونسخة واحدة تقدر تتشارك بين الخيوط وتتستعمل بالتوازي. والنمط الموصى بيه إنك تعمل نسخة واحدة أول ما التطبيق يبدأ وتعيد استعمالها:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

و`ParseConfig` مش بيتغيّر وآمن للمشاركة بنفس الدرجة. والالتزام الوحيد بالأمان مع الخيوط اللي المكتبة مش قادرة تفرضه بدالك هو في [مُعدِّلات المدخل](#معدلات-المدخل): علشان بتتخزّن نسخة واحدة من كل مُعدِّل وبتتشارك في كل عمليات التحليل المتوازية، فلازم التطبيقات تكون من غير حالة.

---

## الملحق أ — ثوابت نصوص المعرّفات

`GS1Constants_AICodes` (في الحزمة `tools.pantheum.gaia.gs1.constants`) بيعلن ثابت من نوع `String` لكل معرّف تطبيق بيعرفه GAIA. استعمل الثوابت دي بدل ما تثبّت نصوص رموز المعرّفات في الكود:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

وكل ثابت شايل الصورة النصية لرمز المعرّف (زي `AI_01_GTIN = "01"`).

### التعريف والترقيم التسلسلي

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | الرمز التسلسلي لحاوية الشحن (SSCC). |
| `01` | `AI_01_GTIN` | الرقم العالمي للصنف التجاري (GTIN). |
| `02` | `AI_02_CONTENT` | الرقم العالمي للصنف التجاري (GTIN) للأصناف التجارية المحتواة. |
| `03` | `AI_03_MTO_GTIN` | تعريف الصنف التجاري المصنوع حسب الطلب (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | رقم التشغيلة أو الدفعة. |
| `20` | `AI_20_VARIANT` | الشكل الداخلي للمنتج. |
| `21` | `AI_21_SERIAL` | الرقم التسلسلي. |
| `22` | `AI_22_CPV` | الشكل الاستهلاكي للمنتج. |
| `235` | `AI_235_TPX` | امتداد تسلسلي للرقم العالمي للصنف التجاري (GTIN) يتحكم فيه طرف ثالث (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | تعريف إضافي للمنتج يحدده الشركة المصنّعة. |
| `241` | `AI_241_CUST_PART_NO` | رقم القطعة الخاص بالعميل. |
| `242` | `AI_242_MTO_VARIANT` | رقم التغيير الخاص بالتصنيع حسب الطلب. |
| `243` | `AI_243_PCN` | رقم مكوّن التغليف. |
| `250` | `AI_250_SECONDARY_SERIAL` | الرقم التسلسلي الثانوي. |
| `251` | `AI_251_REF_TO_SOURCE` | مرجع الكيان المصدر. |
| `253` | `AI_253_GDTI` | المعرّف العالمي لنوع المستند (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | مكوّن الامتداد للرقم العالمي للموقع (GLN). |
| `255` | `AI_255_GCN` | الرقم العالمي للكوبون (GCN). |
| `30` | `AI_30_VAR_COUNT` | عدد متغيّر من الأصناف (صنف بقياس متغيّر). |
| `37` | `AI_37_COUNT` | عدد الأصناف التجارية أو أجزائها داخل الوحدة اللوجستية. |

</div>

### التواريخ والأوقات

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | تاريخ الإنتاج (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | تاريخ الاستحقاق (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | تاريخ التعبئة والتغليف (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | تاريخ انتهاء الصلاحية المثلى (YYMMDD). |
| `16` | `AI_16_SELL_BY` | تاريخ انتهاء البيع (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | تاريخ انتهاء الصلاحية (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | تاريخ ووقت التسليم بحد أدنى (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | تاريخ ووقت التسليم بحد أقصى (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | تاريخ الإصدار (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | تاريخ ووقت انتهاء الصلاحية (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | تاريخ أول تجميد (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | تاريخ الحصاد (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | تاريخ يجب الفحص قبله (YYMMDD[hhmm]). |

</div>

### الكمية والقياس — قياس متغيّر (المتري)

عيل المعرّفات اللي من أربعة أرقام `310n`–`369n` بترمّز الكميات المتغيّرة القياس. الرقم التالت بيختار نوع القياس؛ و**الرقم الرابع** (`n`، من 0 لـ 5) هو عدد المنازل العشرية الضمنية — يعني `AI_3102_NET_WEIGHT_KG` معناه الوزن الصافي بالكيلوجرام بمنزلتين عشريتين.

<div dir="rtl">

| العيلة | نمط الثابت (`n` = رقم المنازل العشرية) | الوصف |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | الوزن الصافي، بالكيلوجرام (صنف بقياس متغيّر). |
| `311n` | `AI_311n_LENGTH_M` | الطول أو البعد الأول، بالمتر (صنف بقياس متغيّر). |
| `312n` | `AI_312n_WIDTH_M` | العرض أو القطر أو البعد الثاني، بالمتر (صنف بقياس متغيّر). |
| `313n` | `AI_313n_HEIGHT_M` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالمتر (صنف بقياس متغيّر). |
| `314n` | `AI_314n_AREA_M` | المساحة، بالمتر المربع (صنف بقياس متغيّر). |
| `315n` | `AI_315n_NET_VOLUME_L` | الحجم الصافي، باللتر (صنف بقياس متغيّر). |
| `316n` | `AI_316n_NET_VOLUME_M` | الحجم الصافي، بالمتر المكعب (صنف بقياس متغيّر). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | الوزن اللوجستي، بالكيلوجرام. |
| `331n` | `AI_331n_LENGTH_M_LOG` | الطول أو البعد الأول، بالمتر. |
| `332n` | `AI_332n_WIDTH_M_LOG` | العرض أو القطر أو البعد الثاني، بالمتر. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالمتر. |
| `334n` | `AI_334n_AREA_M_LOG` | المساحة، بالمتر المربع. |
| `335n` | `AI_335n_VOLUME_L_LOG` | الحجم اللوجستي، باللتر. |
| `336n` | `AI_336n_VOLUME_M_LOG` | الحجم اللوجستي، بالمتر المكعب. |
| `337n` | `AI_337n_KG_PER_M` | كيلوجرام لكل متر مربع. |

</div>

### الكمية والقياس — قياس متغيّر (الإمبراطوري / الأمريكي)

<div dir="rtl">

| العيلة | نمط الثابت (`n` = رقم المنازل العشرية) | الوصف |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | الوزن الصافي، بالرطل (صنف بقياس متغيّر). |
| `321n` | `AI_321n_LENGTH_IN` | الطول أو البعد الأول، بالبوصة (صنف بقياس متغيّر). |
| `322n` | `AI_322n_LENGTH_FT` | الطول أو البعد الأول، بالقدم (صنف بقياس متغيّر). |
| `323n` | `AI_323n_LENGTH_YD` | الطول أو البعد الأول، بالياردة (صنف بقياس متغيّر). |
| `324n` | `AI_324n_WIDTH_IN` | العرض أو القطر أو البعد الثاني، بالبوصة (صنف بقياس متغيّر). |
| `325n` | `AI_325n_WIDTH_FT` | العرض أو القطر أو البعد الثاني، بالقدم (صنف بقياس متغيّر). |
| `326n` | `AI_326n_WIDTH_YD` | العرض أو القطر أو البعد الثاني، بالياردة (صنف بقياس متغيّر). |
| `327n` | `AI_327n_HEIGHT_IN` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالبوصة (صنف بقياس متغيّر). |
| `328n` | `AI_328n_HEIGHT_FT` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالقدم (صنف بقياس متغيّر). |
| `329n` | `AI_329n_HEIGHT_YD` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالياردة (صنف بقياس متغيّر). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | الوزن اللوجستي، بالرطل. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | الطول أو البعد الأول، بالبوصة. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | الطول أو البعد الأول، بالقدم. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | الطول أو البعد الأول، بالياردة. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | العرض أو القطر أو البعد الثاني، بالبوصة. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | العرض أو القطر أو البعد الثاني، بالقدم. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | العرض أو القطر أو البعد الثاني، بالياردة. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالبوصة. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالقدم. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | العمق أو السُّمك أو الارتفاع أو البعد الثالث، بالياردة. |
| `350n` | `AI_350n_AREA_IN` | المساحة، بالبوصة المربعة (صنف بقياس متغيّر). |
| `351n` | `AI_351n_AREA_FT` | المساحة، بالقدم المربع (صنف بقياس متغيّر). |
| `352n` | `AI_352n_AREA_YD` | المساحة، بالياردة المربعة (صنف بقياس متغيّر). |
| `353n` | `AI_353n_AREA_IN_LOG` | المساحة، بالبوصة المربعة. |
| `354n` | `AI_354n_AREA_FT_LOG` | المساحة، بالقدم المربع. |
| `355n` | `AI_355n_AREA_YD_LOG` | المساحة، بالياردة المربعة. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | الوزن الصافي، بأونصة تروي (صنف بقياس متغيّر). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | الوزن الصافي (أو الحجم)، بالأونصة (صنف بقياس متغيّر). |
| `360n` | `AI_360n_NET_VOLUME_QT` | الحجم الصافي، بالكوارت (صنف بقياس متغيّر). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | الحجم الصافي، بالجالون الأمريكي (صنف بقياس متغيّر). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | الحجم اللوجستي، بالكوارت. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | الحجم اللوجستي، بالجالون الأمريكي. |
| `364n` | `AI_364n_NET_VOLUME_IN` | الحجم الصافي، بالبوصة المكعبة (صنف بقياس متغيّر). |
| `365n` | `AI_365n_NET_VOLUME_FT` | الحجم الصافي، بالقدم المكعب (صنف بقياس متغيّر). |
| `366n` | `AI_366n_NET_VOLUME_YD` | الحجم الصافي، بالياردة المكعبة (صنف بقياس متغيّر). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | الحجم اللوجستي، بالبوصة المكعبة. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | الحجم اللوجستي، بالقدم المكعب. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | الحجم اللوجستي، بالياردة المكعبة. |

</div>

### التسعير والمبالغ النقدية

الرقم الرابع (`n`) بيرمّز عدد المنازل العشرية الضمنية. والمدى المسموح
بيختلف من عيلة للتانية — شوف عمود `n`.

<div dir="rtl">

| العيلة | نمط الثابت (`n` = رقم المنازل العشرية) | `n` | الوصف |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | المبلغ المستحق الدفع أو قيمة الكوبون بالعملة المحلية. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | المبلغ المستحق الدفع مع رمز العملة وفق معيار ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | المبلغ المستحق الدفع، في منطقة عملة واحدة (صنف بقياس متغيّر). |
| `393n` | `AI_393n_PRICE` | 0–9 | المبلغ المستحق الدفع مع رمز العملة وفق معيار ISO (صنف بقياس متغيّر). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | نسبة الخصم المئوية للكوبون. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | المبلغ المستحق الدفع لكل وحدة قياس، في منطقة عملة واحدة (صنف بقياس متغيّر). |

</div>

### الموقع والشحن

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | رقم أمر الشراء الخاص بالعميل. |
| `401` | `AI_401_GINC` | الرقم العالمي لتعريف الشحنة (GINC). |
| `402` | `AI_402_GSIN` | الرقم العالمي لتعريف الشحنة (GSIN). |
| `403` | `AI_403_ROUTE` | رمز التوجيه. |
| `410` | `AI_410_SHIP_TO_LOC` | الرقم العالمي للموقع (GLN) لجهة الشحن/التسليم إليها. |
| `411` | `AI_411_BILL_TO` | الرقم العالمي للموقع (GLN) للجهة الموجَّهة إليها الفاتورة. |
| `412` | `AI_412_PURCHASE_FROM` | الرقم العالمي للموقع (GLN) لجهة الشراء. |
| `413` | `AI_413_SHIP_FOR_LOC` | الرقم العالمي للموقع (GLN) لجهة إعادة التوجيه للشحن/التسليم. |
| `414` | `AI_414_LOC_NO` | تعريف الموقع الفعلي - الرقم العالمي للموقع (GLN). |
| `415` | `AI_415_PAY_TO` | الرقم العالمي للموقع (GLN) للجهة المصدرة للفاتورة. |
| `416` | `AI_416_PROD_SERV_LOC` | الرقم العالمي للموقع (GLN) لموقع الإنتاج أو الخدمة. |
| `417` | `AI_417_PARTY` | الرقم العالمي للموقع (GLN) للطرف المعني. |
| `420` | `AI_420_SHIP_TO_POST` | الرمز البريدي لجهة الشحن/التسليم ضمن هيئة بريدية واحدة. |
| `421` | `AI_421_SHIP_TO_POST` | الرمز البريدي لجهة الشحن/التسليم مع رمز البلد وفق ISO. |
| `422` | `AI_422_ORIGIN` | بلد منشأ الصنف التجاري. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | بلد المعالجة الأولية. |
| `424` | `AI_424_COUNTRY_PROCESS` | بلد التصنيع/المعالجة. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | بلد الفك/التفكيك. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | البلد الذي يغطي سلسلة العمليات بالكامل. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | التقسيم الإداري لبلد المنشأ. |
| `4300` | `AI_4300_SHIP_TO_COMP` | اسم شركة الشحن/التسليم. |
| `4301` | `AI_4301_SHIP_TO_NAME` | جهة الاتصال للشحن/التسليم. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | عنوان الشحن/التسليم - السطر 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | عنوان الشحن/التسليم - السطر 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | ضاحية الشحن/التسليم. |
| `4305` | `AI_4305_SHIP_TO_LOC` | منطقة الشحن/التسليم. |
| `4306` | `AI_4306_SHIP_TO_REG` | منطقة/إقليم الشحن/التسليم. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | رمز بلد الشحن/التسليم. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | رقم هاتف الشحن/التسليم. |
| `4309` | `AI_4309_SHIP_TO_GEO` | الموقع الجغرافي لجهة الشحن/التسليم. |
| `4310` | `AI_4310_RTN_TO_COMP` | اسم شركة الإرجاع. |
| `4311` | `AI_4311_RTN_TO_NAME` | جهة الاتصال للإرجاع. |
| `4312` | `AI_4312_RTN_TO_ADD1` | عنوان الإرجاع - السطر 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | عنوان الإرجاع - السطر 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | الضاحية الخاصة بالإرجاع. |
| `4315` | `AI_4315_RTN_TO_LOC` | منطقة الإرجاع. |
| `4316` | `AI_4316_RTN_TO_REG` | منطقة/إقليم الإرجاع. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | رمز بلد الإرجاع. |
| `4318` | `AI_4318_RTN_TO_POST` | الرمز البريدي للإرجاع. |
| `4319` | `AI_4319_RTN_TO_PHONE` | رقم هاتف الإرجاع. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | وصف رمز الخدمة. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | إشارة البضائع الخطرة. |
| `4322` | `AI_4322_AUTH_LEAVE` | إذن ترك الشحنة دون توقيع. |
| `4323` | `AI_4323_SIG_REQUIRED` | إشارة طلب التوقيع. |
| `4330` | `AI_4330_MAX_TEMP_F` | أقصى درجة حرارة بالفهرنهايت (بجزء من مئة من الدرجة). |
| `4331` | `AI_4331_MAX_TEMP_C` | أقصى درجة حرارة بالمئوية (بجزء من مئة من الدرجة). |
| `4332` | `AI_4332_MIN_TEMP_F` | أدنى درجة حرارة بالفهرنهايت (بجزء من مئة من الدرجة). |
| `4333` | `AI_4333_MIN_TEMP_C` | أدنى درجة حرارة بالمئوية (بجزء من مئة من الدرجة). |

</div>

### سمات المنتج والتتبّع

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | رقم مخزون الناتو (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | تصنيف UN/ECE لذبائح ولحوم اللحوم المقطعة. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | الفعالية النشطة. |
| `7005` | `AI_7005_CATCH_AREA` | منطقة الصيد. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | الأنواع لأغراض الصيد. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | نوع معدات الصيد. |
| `7010` | `AI_7010_PROD_METHOD` | طريقة الإنتاج. |
| `7020` | `AI_7020_REFURB_LOT` | معرّف دفعة إعادة التأهيل. |
| `7021` | `AI_7021_FUNC_STAT` | الحالة الوظيفية. |
| `7022` | `AI_7022_REV_STAT` | حالة المراجعة. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | المعرّف العالمي للأصل الفردي (GIAI) لمجموعة تجميع. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | رقم المُصنِّع مع رمز بلد ISO من تلات أرقام (10 خانات). |
| `7040` | `AI_7040_UIC_EXT` | رمز GS1 UIC مع الامتداد 1 ومؤشر المستورد. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | نوع وحدة الشحن وفق UN/CEFACT. |

</div>

### أرقام السداد الصحية الوطنية (NHRN)

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - PZN ألمانيا. |
| `711` | `AI_711_NHRN_CIP` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - CIP فرنسا. |
| `712` | `AI_712_NHRN_CN` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - CN إسبانيا. |
| `713` | `AI_713_NHRN_DRN` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - DRN البرازيل. |
| `714` | `AI_714_NHRN_AIM` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - AIM البرتغال. |
| `715` | `AI_715_NHRN_NDC` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - NDC الولايات المتحدة الأمريكية. |
| `716` | `AI_716_NHRN_AIC` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - AIC إيطاليا. |
| `717` | `AI_717_NHRN_SRN` | الرقم الوطني لسداد الرعاية الصحية (NHRN) - رقم السجل الصحي لكوستاريكا. |

</div>

### الرعاية الصحية وGMN وHIDRI وCPID وبيانات الأشخاص

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | مرجع الشهادة (10 خانات). |
| `7240` | `AI_7240_PROTOCOL` | معرّف البروتوكول. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | نوع وسيط الالتقاط الآلي للبيانات (AIDC). |
| `7242` | `AI_7242_VCN` | رقم التحكم بالإصدار (VCN). |
| `7250` | `AI_7250_DOB` | تاريخ الميلاد (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | تاريخ ووقت الميلاد (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | الجنس البيولوجي. |
| `7253` | `AI_7253_FAMILY_NAME` | اسم عائلة الشخص. |
| `7254` | `AI_7254_GIVEN_NAME` | الاسم الشخصي (الاسم الأول). |
| `7255` | `AI_7255_SUFFIX` | لاحقة اسم الشخص. |
| `7256` | `AI_7256_FULL_NAME` | الاسم الكامل للشخص. |
| `7257` | `AI_7257_PERSON_ADDR` | عنوان الشخص. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | ترتيب ولادة الطفل. |
| `7259` | `AI_7259_BABY` | اسم عائلة الطفل. |
| `8001` | `AI_8001_DIMENSIONS` | المنتجات الملفوفة (العرض، الطول، قطر اللب، الاتجاه، الوصلات). |
| `8002` | `AI_8002_CMT_NO` | معرّف رقم الهاتف المحمول. |
| `8003` | `AI_8003_GRAI` | المعرّف العالمي للأصل القابل للإرجاع (GRAI). |
| `8004` | `AI_8004_GIAI` | المعرّف العالمي للأصل الفردي (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | السعر لكل وحدة قياس. |
| `8006` | `AI_8006_ITIP` | تعريف قطعة فردية من الصنف التجاري (ITIP). |
| `8007` | `AI_8007_IBAN` | الرقم الدولي لحساب البنك (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | تاريخ ووقت الإنتاج (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | مؤشر مستشعر قابل للقراءة الضوئية. |
| `8010` | `AI_8010_CPID` | معرّف المكوّن/الجزء (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | الرقم التسلسلي لمعرّف المكوّن/الجزء (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | إصدار البرنامج. |
| `8013` | `AI_8013_GMN` | الرقم العالمي للطراز (GMN). |
| `8014` | `AI_8014_MUDI` | معرّف تسجيل الجهاز الفردي بشكل عالٍ (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | الرقم العالمي لعلاقة الخدمة (GSRN) لتحديد العلاقة بين المؤسسة المقدِّمة للخدمة ومزوّد الخدمة. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | الرقم العالمي لعلاقة الخدمة (GSRN) لتحديد العلاقة بين المؤسسة المقدِّمة للخدمة ومستلم الخدمة. |
| `8019` | `AI_8019_SRIN` | رقم مثيل علاقة الخدمة (SRIN). |
| `8020` | `AI_8020_REF_NO` | الرقم المرجعي لإيصال الدفع. |
| `8026` | `AI_8026_ITIP_CONTENT` | تعريف قطع الصنف التجاري (ITIP) الموجودة داخل الوحدة اللوجستية. |
| `8030` | `AI_8030_DIGSIG` | التوقيع الرقمي (DigSig). |
| `8040` | `AI_8040_IMEI` | الهوية الدولية للمعدات المتنقلة (IMEI). |
| `8041` | `AI_8041_IMEI2` | الهوية الدولية للمعدات المتنقلة 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | رقم شريحة الاتصال المدمجة (eSIM). |
| `8043` | `AI_8043_PSIM` | رقم شريحة الاتصال الفعلية (SIM). |
| `8110` | `AI_8110` | رمز تعريف الكوبون للاستخدام في أمريكا الشمالية. |
| `8111` | `AI_8111_POINTS` | نقاط الولاء الخاصة بالكوبون. |
| `8112` | `AI_8112` | رمز تعريف كوبون ملف العروض الموجبة للاستخدام في أمريكا الشمالية. |
| `8200` | `AI_8200_PRODUCT_URL` | رابط التغليف الموسّع (URL). |

</div>

### الاستخدام الداخلي / استخدام الشركة

<div dir="rtl">

| AI | الثابت | الوصف |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | معلومات متفق عليها بين الشركاء التجاريين. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | معلومات داخلية للشركة (9 خانات). |

</div>

---

## الملحق ب — ثوابت مفاتيح التفسير

لما `GaiaParser.parse()` تتنادى بالوضع `ParseMode.INTERPRETATION`، كل `GS1AIObjectElement` ممكن يشيل قايمة من كائنات `GS1AIInterpretation` بتطلّعها مُغنيات متخصّصة بالمجال. استعمل ثوابت `GS1Constants_Enricher` (في الحزمة `tools.pantheum.gaia.gs1.constants`) كمفاتيح علشان تدوّر على قيم تفسير معيّنة:

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

تسميات العرض **مش** ثوابت — هي في الفهارس المترجمة تحت `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`، ومفتاحها ثابت النوع. و`GS1AIInterpretation.getLabel()` بترجّع التسمية بلغة التحليل (شوف [الرسايل والتسميات المترجمة](#الرسايل-والتسميات-المترجمة))، وبترجع للإنجليزية لما فهرس يكون ناقص المفتاح. وعمود «تسمية العرض» اللي تحت بيسرد النص بالعربية المصرية؛ أما مفاتيح الأنواع نفسها فثابتة في كل اللغات، فطابق دايمًا على المفتاح، عمرك ما تطابق على التسمية.

### التاريخ والوقت

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `DATE_VALUE` | التاريخ | معرّفات التواريخ (11–17، 7003، 7006، 7011، وغيرها) |
| `DATE_FORMAT` | تنسيق التاريخ | معرّفات التواريخ |
| `TIME_VALUE` | الوقت | المعرّفات اللي شايلة وقت (7003، 7011، 8008، وغيرها) |
| `TIME_FORMAT` | تنسيق الوقت | المعرّفات اللي شايلة وقت |
| `DATETIME_VALUE` | التاريخ والوقت | معرّفات التاريخ والوقت |
| `DATETIME_FORMAT` | تنسيق التاريخ والوقت | معرّفات التاريخ والوقت |

</div>

### تاريخ الحصاد

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | تاريخ بدء الحصاد | AI 7007 |
| `HARVEST_END_DATE` | تاريخ انتهاء الحصاد | AI 7007 (نهاية المدى الاختيارية) |
| `HARVEST_DATE_RANGE` | نطاق تاريخ الحصاد | AI 7007 |

</div>

### بادئة شركة GS1

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | بادئة شركة GS1 | معرّفات GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | رمز عضو GS1 | معرّفات GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | منظمة عضو GS1 | معرّفات GTIN / GLN / SSCC |

</div>

### GTIN

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `GTIN_TYPE` | نوع GTIN | AI 01، 02 |
| `GTIN_NATIVE` | GTIN | AI 01، 02 |
| `PACKAGING_LEVEL` | مستوى التعبئة | AI 01 |
| `GTIN_CHECK_DIGIT` | رقم التحقق | AI 01، 02 |

</div>

### SSCC

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | رقم الامتداد | AI 00 |
| `SSCC_SERIAL_REFERENCE` | المرجع التسلسلي | AI 00 |
| `SSCC_CHECK_DIGIT` | رقم التحقق | AI 00 |

</div>

### البلد (ISO 3166)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | رمز البلد (رقمي) | معرّفات البلد الواحد (422، 424–426، 4307، 4317، 421، 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | رمز البلد (حرفي-2) | معرّفات البلاد برمز من حرفين |
| `COUNTRY_NAME` | اسم البلد | معرّفات البلد الواحد |
| `COUNTRY_LIST` | البلدان | AI 423 — كل الأسامي موصولة، زي `Australia, New Zealand` |

</div>

المعرّف 423 (بلد المعالجة الأولى) ممكن يشيل لحد خمس بلاد، فبيطلّع **زوج مرقّم لكل بلد** —
`COUNTRY_CODE_NUMERIC_1` و`COUNTRY_NAME_1` و`COUNTRY_CODE_NUMERIC_2` و`COUNTRY_NAME_2` …
— وبعدهم ملخّص `COUNTRY_LIST` واحد. ابنِ المفاتيح دي من الثابتين
`COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` مع ترتيب بيبدأ من 1، أو ببساطة لفّ
على `getInterpretations()`؛ أما المفتاحين `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` من غير
لاحقة فـ **مش** بيتطلّعوا للمعرّف 423.

### العملة (ISO 4217)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | رمز العملة | معرّفات المبالغ اللي معاها عملة (391n، 393n) |
| `CURRENCY_ALPHA` | رمز العملة الحرفي | معرّفات المبالغ اللي معاها عملة |
| `CURRENCY_NAME` | اسم العملة | معرّفات المبالغ اللي معاها عملة |

</div>

### درجة الحرارة

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `TEMPERATURE` | درجة الحرارة | AI 4330–4333 |
| `TEMPERATURE_UNIT` | وحدة درجة الحرارة | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | درجة الحرارة (منسّقة) | AI 4330–4333 |

</div>

### الجنس (ISO 5218)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `SEX_CODE` | رمز الجنس | AI 7252 |
| `SEX_DESCRIPTION` | وصف الجنس | AI 7252 |

</div>

### الأنواع المائية (FAO)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `SPECIES_CODE` | رمز النوع | AI 7008 |
| `SPECIES_SCIENTIFIC` | الاسم العلمي | AI 7008 |
| `SPECIES_ENGLISH` | الاسم الشائع | AI 7008 |
| `SPECIES_FAMILY` | الفصيلة | AI 7008 |
| `SPECIES_ORDER` | الرتبة | AI 7008 |

</div>

### رقم مخزون الناتو (NSN)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `NSN_FSG` | مجموعة الإمداد | AI 7001 |
| `NSN_FSG_NAME` | اسم مجموعة الإمداد | AI 7001 |
| `NSN_FSCG` | فئة الإمداد | AI 7001 |
| `NSN_FSCG_NAME` | اسم فئة الإمداد | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | رمز البلد | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | البلد | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | رمز البلد ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | فئة NCS | AI 7001 |
| `NSN_NIIN` | رقم الصنف الوطني | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

</div>

### منتجات اللفائف

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | عرض اللفة (mm) | AI 8001 |
| `ROLL_LENGTH` | طول اللفة (m) | AI 8001 |
| `CORE_DIAMETER` | قطر اللب (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | رمز اتجاه اللف | AI 8001 |
| `WINDING_DIRECTION` | اتجاه اللف | AI 8001 |
| `SPLICES` | الوصلات | AI 8001 |

</div>

### IBAN

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | رمز البلد | AI 8007 |
| `IBAN_COUNTRY_NAME` | البلد | AI 8007 |
| `IBAN_CHECK_DIGITS` | أرقام التحقق | AI 8007 |
| `IBAN_CHECK_VALID` | التحقق | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

</div>

### IMEI

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040، 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040، 8041 |
| `IMEI_SERIAL` | الرقم التسلسلي | AI 8040، 8041 |
| `IMEI_CHECK_DIGIT` | رقم التحقق | AI 8040، 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040، 8041 |
| `IMEI_RBI_NAME` | جهة الإصدار | AI 8040، 8041 |

</div>

الخمستاشر رقم بيتفكّكوا لـ `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`، وRBI هو أول
رقمين من TAC — يعني `IMEI_RBI` بادئة لـ `IMEI_TAC` مش حقل لوحده. و`IMEI_FORMATTED` بيوري
تجميع العرض القياسي بتاع GSMA `AA-BBBBBB-CCCCCC-D` (زي `49-015420-323751-8`)، اللي بيقسّم
TAC عند حدّ RBI؛ أما التجميع القديم `6-2-6-1`، اللي كان بيقطع عند بداية رمز التجميع النهائي
الملغي، فمش بيتطلّع.

و`IMEI_RBI_NAME` بيحلّ الـ RBI لاسم الجهة اللي خصّصته عن طريق `ImeiRbiData`، وبيتضاف
**في الآخر وبس لو الرمز مدرج هناك**. والجدول ده بيغطّي تلات مجموعات:

- **جهات لسه بتخصّص** — `01` CTIA/PTCRB، و`35` TÜV SÜD BABT، و`86` TAF، وكمان `99`
  Global Hexadecimal Administrator و`98` (محجوز).
- **مديات اختبارية** — `00` و`02`–`09`، وبتدلّ على أرقام IMEI اختبارية مش تخصيص حقيقي.
  اسأل عنها بـ `ImeiRbiData.isTestCode(code)`.
- **جهات ما بقتش بتخصّص** — جهات تاريخية زي `49` (BZT/BAPT، ألمانيا) و`44` (BABT،
  المملكة المتحدة) و`91` (MSAI، الهند). اسأل عنها بـ
  `ImeiRbiData.isNoLongerAllocating(code)`. والأجهزة اللي شايلة الرموز دي عادية ولسه شغّالة؛
  اللي وقف بس هو التخصيص الجديد، يعني دي معلومة للإبلاغ، مش إشارة صلاحية خالص.

وغياب `IMEI_RBI_NAME` معناه «الـ RBI ده مش في جدولنا»، **مش** معناه «رقم IMEI مش صحيح»:
علشان الجدول متجمّع من قايمة RBI منشورة مش من GSMA على طول، فممكن يتأخّر عن الجهات اللي
اتعيّنت حديثًا. ما تستنتجش أي حكم تحقّق من غيابه؛ الـ RBI مش حرف تحقق. والكود اللي بيلفّ
على قايمة التفسيرات لازم يتحمّل غيابه بدل ما يفهرس بالمكان.

### معرّفات SIM (EID / ICCID)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042، 8043 |
| `SIM_MII_NAME` | فئة الصناعة | AI 8042 |
| `EID_BODY` | متن EID | AI 8042 |
| `EID_CHECK_DIGIT` | رقم التحقق | AI 8042 |
| `ICCID_BODY` | متن ICCID | AI 8043 |
| `ICCID_EXTENSION` | الامتداد | AI 8043 |

</div>

`SIM_MII` شايل **أول** رقمين (`89`)، وهو الزوج اللي ITU-T E.118 بيخصّصه للاتصالات. أما
ISO/IEC 7812 نفسه فبيعرّف MII على إنها **أول رقم بس**، علشان كده `SIM_MII_NAME` بيحلّ الفئة
من الرقم `8` الأول عن طريق `Iso7812Data` — فبيطلّع "Healthcare, telecommunications and
other future industry assignments". يعني هو ثابت لأي EID سليم؛ وبيتبلّغ عنه علشان التتبّع
للمعيار مش كمميّز. و`Iso7812Data.nameForCode(digit)` بتاخد رقم واحد بس، أما
`nameForIdentifier(prefix)` فبتقبل بادئة أطول وبتقرا أول رقم فيها.

و`SIM_MII_NAME` بيطلّعه بس المُغني `EidEnricher` (المعرّف 8042). أما `IccidEnricher`
(المعرّف 8043) فبيوري `SIM_MII` من غير الفئة.

### مرجع الشهادة

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | الرقم التسلسلي | AI 7230–7239 |
| `CERT_SCHEME_CODE` | رمز نظام الاعتماد | AI 7230–7239 |
| `CERT_SCHEME_NAME` | نظام الاعتماد | AI 7230–7239 |
| `CERT_REFERENCE` | مرجع الشهادة | AI 7230–7239 |

</div>

### GS1 UIC

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `UIC_CODE` | رمز UIC | AI 7040 |
| `UIC_EXTENSION_1` | الامتداد 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | مؤشر المستورد | AI 7040 |

</div>

### تسلسل ولادة الرضيع

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | موضع الولادة | AI 7258 |
| `BIRTH_TOTAL` | إجمالي الولادات | AI 7258 |
| `BIRTH_SEQUENCE` | تسلسل الولادة | AI 7258 |

</div>

### رقم الطراز العالمي (GMN)

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | مرجع الطراز | AI 8013 |
| `GMN_CHECK_PAIR` | زوج التحقق | AI 8013 |

</div>

### HIDRI

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | مرجع الجهاز | AI 8014 |
| `HIDRI_CHECK_PAIR` | زوج التحقق | AI 8014 |

</div>

### CPID

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | مرجع المكوّن والجزء | AI 8010–8011 |

</div>

### القيم العشرية وقيم القياس

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | قيمة عشرية | معرّفات رقمية بمنازل عشرية ضمنية (31xx–36xx) |
| `DECIMAL_AMOUNT` | المبلغ | معرّفات الأسعار (390n–395n) |
| `DECIMAL_PERCENTAGE` | النسبة المئوية | AI 394n |
| `DECIMAL_PLACES` | المنازل العشرية | مع `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | تنسيق النسبة المئوية | AI 394n |
| `ISO_UNIT_CODE` | رمز وحدة ISO | معرّفات القياس |
| `ISO_UNIT_NAME` | اسم وحدة ISO | معرّفات القياس |
| `MONETARY_AMOUNT` | المبلغ النقدي | معرّفات الأسعار |
| `MONETARY_AMOUNT_DISPLAY` | المبلغ النقدي (منسّق) | معرّفات الأسعار |

</div>

### الإحداثيات الجغرافية

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `LATITUDE` | خط العرض | AI 4309 |
| `LONGITUDE` | خط الطول | AI 4309 |
| `GEO_COORDINATES` | الإحداثيات الجغرافية | AI 4309 |
| `LATITUDE_DMS` | خط العرض (DMS) | AI 4309 |
| `LONGITUDE_DMS` | خط الطول (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | الإحداثيات الجغرافية (DMS) | AI 4309 |

</div>

### طريقة الإنتاج

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | رمز طريقة الإنتاج | AI 7010 |
| `PRODUCTION_METHOD` | طريقة الإنتاج | AI 7010 |

</div>

### نوع وسيط AIDC

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | رمز نوع وسيط AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | نوع وسيط AIDC | AI 7241 |

</div>

### القطعة من الإجمالي

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | رقم القطعة | AI 8006 |
| `PIECE_TOTAL` | إجمالي القطع | AI 8006 |
| `PIECE_OF_TOTAL` | القطعة من الإجمالي | AI 8006 |

</div>

### تقسيمات المكوّنات

مفاتيح بتطلّعها تقسيمات المكوّنات التصريحية في `content/ai-content.json` مش أي مُغني مكتوب
بـ Java — وكلها بتوري الأجزاء المسمّاة من قيمة معرّف مركّبة. وعلى عكس كل مفتاح تاني في
الملحق ده، **دول ما لهمش ثوابت في `GS1Constants_Enricher`**: طابق على النص الحرفي، أو اقرا
النوع من `GS1AIInterpretation.getType()`.

<div dir="rtl">

| مفتاح النوع | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | رقم التحقق | AI 253، 255، 402، 410–417، 8003، 8017، 8018 |
| `SERIAL_NUMBER` | الرقم التسلسلي | AI 253، 255، 8003 |
| `POSTAL_CODE` | الرمز البريدي | AI 421 |
| `PROCESSOR_ID` | معرّف المُعالِج | AI 7030–7039 |

</div>

وخلّي بالك إن `CHECK_DIGIT` هنا هو مفتاح تقسيم المكوّنات العام، وهو غير المفاتيح الخاصة
بالمُغنيات `GTIN_CHECK_DIGIT` و`SSCC_CHECK_DIGIT` و`IMEI_CHECK_DIGIT` و`EID_CHECK_DIGIT`
اللي فوق.

### متفرّقات

<div dir="rtl">

| ثابت المفتاح | تسمية العرض | بيطلّعها |
|--------------|---------------|-------------|
| `FLAG_VALUE` | القيمة | معرّفات منطقية / علَمية (4321–4323) |
| `DECODED_TEXT` | نص مفكوك | معرّفات النص الحر |

</div>
