# GAIA (محلّل معرّفات تطبيق GS1) — دليل المطوّر

## المحتويات

1. [نظرة عامة](#نظرة-عامة)
2. [عن GS1 والمواصفات العامة](#عن-gs1-والمواصفات-العامة)
3. [معرّفات تطبيق GS1](#معرفات-تطبيق-gs1)
4. [بداية سريعة](#بداية-سريعة)
5. [مسار التحليل](#مسار-التحليل)
   - [المرحلة التمهيدية — مُعدِّلات الإدخال](#المرحلة-التمهيدية--معدلات-الإدخال)
   - [المرحلة 0 — معرّف الارتباط](#المرحلة-0--معرف-الارتباط)
   - [المرحلة 1 — توجيه الإدخال](#المرحلة-1--توجيه-الإدخال)
   - [المرحلة 2 — البنية النحوية](#المرحلة-2--البنية-النحوية)
   - [المرحلة 3 — المحتوى](#المرحلة-3--المحتوى)
   - [المرحلة 4 — التفسير](#المرحلة-4--التفسير)
6. [ضبط التحليل (`ParseConfig`)](#ضبط-التحليل-parseconfig)
   - [الخيارات](#الخيارات)
   - [الرسائل والتسميات المُترجَمة](#الرسائل-والتسميات-المترجمة)
   - [تنسيق التاريخ](#تنسيق-التاريخ)
7. [مُعدِّلات الإدخال](#معدلات-الإدخال)
   - [المُعدِّلان المدمجان](#المعدلان-المدمجان)
   - [كتابة مُعدِّل](#كتابة-معدل)
   - [تسجيل المُعدِّلات](#تسجيل-المعدلات)
   - [تفقّد ما فعله المُعدِّل](#تفقد-ما-فعله-المعدل)
   - [معالجة إخفاق المُعدِّل](#معالجة-إخفاق-المعدل)
8. [أوضاع التحليل](#أوضاع-التحليل)
   - [وضع DATA_CARRIER](#وضع-data_carrier)
   - [وضع SYNTAX](#وضع-syntax)
   - [وضع CONTENT](#وضع-content)
   - [وضع INTERPRETATION (الافتراضي)](#وضع-interpretation-الافتراضي)
9. [معرّف الارتباط](#معرف-الارتباط)
10. [GS1 Digital Link](#gs1-digital-link)
11. [التعامل مع النتائج](#التعامل-مع-النتائج)
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

`GaiaParser` هو نقطة الدخول لتحليل سلاسل عناصر معرّفات تطبيق GS1 (AI). يقبل المخرجات الخام من الماسح الضوئي بأي من الصيغ التالية، ويعيد `ParseResult` منظَّمًا يحوي كل معرّف تطبيق جرى حلّه، وأخطاء التحقق، واختياريًا تفسيرات مقروءة للبشر:

- سلسلة عناصر معرّفات تطبيق بسيطة: `0109506000134352`
- سلسلة عناصر مسبوقة بمعرّف ترميز AIM: `]C10109506000134352`
- عنوان GS1 Digital Link: `https://example.com/01/09506000134352`
- أيٌّ مما سبق مسبوقًا اختياريًا بمعرّف ارتباط من ثماني خانات: `12345678~0109506000134352`

**صنف نقطة الدخول:** `tools.pantheum.gaia.GaiaParser`

> **أوّل عهدك بـ Gaia؟** ابدأ بـ **[بداية سريعة مع GaiaParser](GaiaParser-QuickStart-Arabic.md)** — عشر دقائق تمرّ بك على الاعتماديات، وأول تحليل، وأشهر ما يُعثِر المبتدئ. هذا الدليل هو المرجع الكامل.

> أما الاتجاه المعاكس — *إنشاء* سلاسل عناصر صحيحة وعناوين Digital Link من أزواج معرّف/قيمة — فيتناوله **[GaiaBuilder — دليل المطوّر](GaiaBuilder-Arabic.md)**.

---

## عن GS1 والمواصفات العامة

**GS1** منظمة عالمية غير ربحية تضع المعايير المفتوحة للتعريف وتبادل البيانات في سلاسل الإمداد وتتولّى صيانتها. تُستخدم معاييرها في التجزئة والرعاية الصحية والخدمات اللوجستية وخدمات الأغذية وصناعات كثيرة غيرها، وتغطي كل شيء من الباركود على أغلفة المنتجات الاستهلاكية إلى التتبّع التسلسلي لجرعات الأدوية.

المرجع المعتمد لكل ما يطبّقه هذا المحلّل هو **المواصفات العامة لـ GS1** — وثيقة واحدة تحدّد:

- جميع رموز معرّفات التطبيق (AI) وعناوين بياناتها وتنسيقاتها وقواعد التحقق منها
- قواعد بناء سلاسل عناصر معرّفات التطبيق وترميزها
- متطلبات أنظمة ترميز الباركود وتخصيص معرّفات ترميز AIM
- خوارزميات رقم التحقق وحرف التحقق
- تفسير السنة ذات الخانتين (قاعدة النافذة المنزلقة)
- مواصفات Data Matrix وQR Code وGS1-128 وGS1 DataBar وغيرها من نواقل البيانات

تُحدَّث المواصفات العامة لـ GS1 سنويًا. الإصدار الحالي والموارد المساندة متاحة على:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

يطبّق GAIA **الإصدار 26.0 (المعتمد، يناير 2026)** من المواصفات العامة لـ GS1.

تحكم عناوين GS1 Digital Link معيارٌ رفيق هو **GS1 Digital Link: URI Syntax**، وهو الذي يحدّد مفاتيح التعريف الأساسية، وترتيب مُقيِّدات المفاتيح، وترميز سمات البيانات الذي يطبّقه المحلّل على مدخلات Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

يطبّق GAIA **الإصدار 1.7.0 (المعتمد، أغسطس 2026)** من معيار GS1 Digital Link: URI Syntax.

الإحالات إلى الأقسام في هذه الوثيقة تشير إلى المواصفات العامة لـ GS1 (مثل «Table 7-5» و«section 7.12»)، باستثناء أرقام أقسام Digital Link (مثل «§4.9» و«§4.12») فهي تشير إلى معيار GS1 Digital Link: URI Syntax.

---

## معرّفات تطبيق GS1

**معرّف تطبيق GS1 (AI)** هو بادئة رقمية قصيرة — من خانتين إلى أربع خانات — تحدّد معنى البيانات التي تليها مباشرة وتنسيقها. معرّفات التطبيق معرَّفة في المواصفات العامة لـ GS1 وتغطي مدى واسعًا من بيانات سلسلة الإمداد: معرّفات المنتجات، والتواريخ، والكميات، وأرقام الدفعات، والأرقام التسلسلية، والقياسات، والعناوين، وغير ذلك.

### بنية عنصر معرّف التطبيق

يتكوّن كل عنصر من جزأين:

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

رمز معرّف التطبيق رقمي دائمًا. وتليه قيمة البيانات مباشرة، بلا أي فاصل بين الرمز والقيمة.

### معرّفات ثابتة الطول ومعرّفات متغيرة الطول

تنقسم معرّفات التطبيق إلى فئتين:

| النوع | السلوك | مثال |
|---|---|---|
| **ثابت الطول** | عدد محدّد من المحارف، يُستهلك كاملًا دائمًا | المعرّف `01` (GTIN) — أربع عشرة خانة دائمًا |
| **متغير الطول** | من محرف واحد إلى حدّ أقصى؛ ينتهي بفاصل GS أو بنهاية الإدخال | المعرّف `10` (الدفعة/التشغيلة) — من 1 إلى 20 محرفًا أبجديًا رقميًا |

كون المعرّف ثابتًا أو متغيرًا يتحدّد حصرًا بتعريفه في مواصفة GS1 — والمحلّل لا يخمّن أبدًا.

### سلاسل العناصر متعددة المعرّفات

يمكن وصل عدة معرّفات تطبيق في سلسلة عناصر واحدة. المعرّفات ثابتة الطول يمكن وصلها مباشرة لأن المحلّل يعرف دائمًا كم محرفًا عليه أن يستهلك بالضبط. أما المعرّفات متغيرة الطول فيجب أن تنتهي بـ **محرف GS** (ASCII `0x1D`، ويُعرف أيضًا بـ FNC1 في أنظمة ترميز الباركود) كلما تلاها معرّف آخر، حتى يعرف المحلّل أين تنتهي قيمة ويبدأ رمز المعرّف التالي.

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

### معرّفات التطبيق الشائعة

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

> **الخانة الرابعة** في معرّف قياس أو سعر من أربع خانات ترمّز عدد المنازل العشرية الضمنية — فـ `3103` هو الوزن الصافي بالكيلوغرام بثلاث منازل عشرية (`001500` = 1.500 كغ)، بينما `3102` يقرأ الخانات نفسها على أنها 15.00 كغ. عمود «التنسيق» أعلاه يبيّن تنسيق *البيانات*؛ أما `getFormatString()` الكامل لكل معرّف فيشمل المعرّف نفسه (مثل `N4+N6` للمعرّف `3103`).

### التفسير المقروء للبشر (HRI)

الصيغة المتعارف عليها للقراءة البشرية تضع كل رمز معرّف بين قوسين قبل قيمته مباشرة، مع مسافة بين العناصر:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

فاصل GS لا يظهر في HRI. وهذه الصيغة ينتجها `GS1AIObject.toHriString()`.

### رموز المعرّفات ذات الأربع خانات

بعض المعرّفات تستخدم أربع خانات بدل خانتين. الخانتان الأوليان تحدّدان عائلة المعرّف، والخانة الثالثة و/أو الرابعة تحمل دلالة إضافية (كموضع الفاصلة العشرية الضمنية في معرّفات القياس). يحلّ المحلّل رمز المعرّف الكامل من سلسلة العناصر تلقائيًا — والمستدعي يتعامل دائمًا مع الرمز الكامل (مثل `"3102"`، لا `"31"` وحدها).

---

## بداية سريعة

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

> **فاصل GS:** المعرّفات متغيرة الطول داخل سلسلة متعددة المعرّفات يجب فصلها بمحرف GS (ASCII `0x1D`). استخدم `""` في نصوص Java الحرفية.

---

## مسار التحليل

### المرحلة التمهيدية — مُعدِّلات الإدخال

إذا حمل `ParseConfig` أي **مُعدِّلات إدخال**، فإنها تعمل قبل كل شيء آخر — قبل نزع معرّف الارتباط، وقبل كشف ناقل البيانات، وقبل الدخول إلى مسار GS1. كل مُعدِّل يعيد كتابة الإدخال الخام للمُعدِّل الذي يليه، وكل المراحل أدناه تعمل على ناتج هذه السلسلة.

لا مُعدِّلات مضبوطة افتراضيًا، فهذه المرحلة التمهيدية لا تفعل شيئًا ما لم تختر تفعيلها. انظر [مُعدِّلات الإدخال](#معدلات-الإدخال).

---

### المرحلة 0 — معرّف الارتباط

قبل أي معالجة لـ GS1، يتحقّق `GaiaParser` مما إذا كان الإدخال يبدأ ببادئة **معرّف ارتباط** اختيارية: ثماني خانات عشرية ASCII بالضبط تليها علامة التلدة (`~`)، مثل `12345678~`.

إن وُجدت البادئة نُزعت وحُفظت بوصفها `CorrelationInfo` في `ParseResult` المُعاد. وكل المراحل التالية تعمل على الحمولة بعد النزع. وإن لم توجد بادئة مرّ الإدخال كما هو.

انظر [معرّف الارتباط](#معرف-الارتباط) للتفاصيل.

---

### المرحلة 1 — توجيه الإدخال

بعد نزع معرّف الارتباط، يتحقّق `GaiaParser` مما إذا كان الإدخال (بعد النزع) يبدأ بـ **معرّف ترميز AIM**: بادئة من ثلاثة محارف على صورة `]` + حرف ASCII + خانة ASCII (مثل `]C1` لـ GS1-128، و`]d2` لـ GS1 DataMatrix، و`]e0` لـ GS1 DataBar / GS1 Composite).

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

إذا كان الناقل غير قادر على حمل معرّفات تطبيق GS1 (كباركود بريدي مثلًا)، يتوقف التحليل فورًا بالخطأ `GE-D002`.

---

### المرحلة 2 — البنية النحوية

تعمل دائمًا. وتتألف من خطوتين فرعيتين:

**2أ. التقطيع إلى رموز (`AISyntaxParser`)**
- يقرأ طول رمز المعرّف من المحرفين الأولين باستخدام جدول بادئات GS1 (الجدول 7-5 من المواصفات العامة لـ GS1).
- المعرّفات ثابتة الطول تستهلك عددًا محدّدًا من البايتات من الإدخال.
- المعرّفات متغيرة الطول تُقرأ حتى محرف GS أو نهاية الإدخال.
- المعرّفات متعددة المكوّنات تُقسَّم كتلة قيمها إلى مقاطع لكل مكوّن.

**2ب. التحقق البنيوي (`SyntaxValidator`)**
- يبحث عن المعرّفات المكرّرة (`GE-S004`).
- يتحقّق من الاعتماديات المطلوبة، فالمعرّف `02` مثلًا يستلزم المعرّف `37` (`GE-S005`).
- يتحقّق من الاقترانات الممنوعة بين المعرّفات (`GE-S006`).

أخطاء هذه المرحلة مستواها `SYNTAX_ERROR` (للتقطيع) أو `INTEGRITY_ERROR` (للبنية). وإن وُجد **أي** خطأ — من التقطيع أو من البنية — توقّف المسار وتُتخطّى مرحلتا المحتوى والتفسير.

---

### المرحلة 3 — المحتوى

تعمل فقط إن لم تُنتج المرحلة 2 أي خطأ (لا من التقطيع ولا من البنية). ولكل عنصر مسارٌ كالتالي (كل خطوة تعمل فقط إن لم تُنتج سابقتها أخطاء):

| الخطوة | المدقّق | رموز الأخطاء |
|---|---|---|
| فحص التعبير النمطي | `RegexValidator` | `GE-C001` |
| مجموعة محارف المكوّن وتنسيقه | `ComponentValidator` | `GE-C005` + رموز التنسيق لكل حالة (`GE-C054`–`GE-C115`) |
| رقم التحقق / حرف التحقق | `CheckDigitCharacterValidator` | `GE-C003`، `GE-C004` |
| تحقق دلالي مخصّص | `ContentValidatorRegistry` | رموز المحتوى لكل حالة (`GE-C116`–`GE-C170`) |

أخطاء هذه المرحلة مستواها `FORMAT_ERROR` أو `DATA_ERROR`، باستثناء واحد: فحوص بادئة شركة GS1
على المعرّفات ذات مفاتيح GS1 إرشادية ومستواها `WARNING` (انظر
[مرجع الأخطاء](#مرجع-الأخطاء))، فبادئة شركة غير معروفة لا تجعل النتيجة
غير صالحة بذاتها.

---

### المرحلة 4 — التفسير

تعمل في وضع `INTERPRETATION` فقط، وفقط حين لا يحمل أي عنصر خطأً من أي مرحلة سابقة. يُثري `InterpretationEngine` كل عنصر ببيانات وصفية مُسمّاة:

- تواريخ مُعاد تنسيقها على صورة `dd/mm/yyyy`
- تفكيك رقم التحقق في GTIN والبحث عن بادئة شركة GS1
- أسماء البلدان وفق ISO 3166
- أسماء العملات ورموزها وفق ISO 4217
- المبالغ العشرية بعد فكّ ترميزها
- أجزاء التفسير المقروء للبشر (HRI)

وتُرفَق النتائج بوصفها مُدخلات `GS1AIInterpretation` على كل `GS1AIObjectElement`.

---

## ضبط التحليل (`ParseConfig`)

يعرض `GaiaParser` نقطتَي دخول لا ثالث لهما:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

يعمل `parse(String)` بـ **الضبط الافتراضي**: وضع `INTERPRETATION`، وتواريخ صغيرة الترتيب (`dd/mm/yyyy`) بفاصل `/` وسنة من أربع خانات، ورسائل أخطاء **بالإنجليزية**. ولتغيير أي من ذلك — بما فيه وضع التحليل — ابنِ `ParseConfig` ببانيه الانسيابي واستخدم النسخة ذات الوسيطين.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

وتعدادات الخيارات كلها موجودة في `GaiaConstants`.

### الخيارات

| دالة الباني | التعداد (`GaiaConstants`) | الافتراضي | الأثر |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | عمق المسار — انظر [أوضاع التحليل](#أوضاع-التحليل). |
| `language(...)`      | `Language`      | `ENGLISH`        | لغة رسائل الأخطاء وتسميات التفسير **ووصوف المعرّفات**. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | ترتيب مكوّنات التاريخ: `LITTLE` (`dd/mm/yyyy`)، `MIDDLE` (`mm/dd/yyyy`)، `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | المحرف الفاصل بين مكوّنات التاريخ: `SLASH` (`/`)، `HYPHEN` (`-`)، `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) أو `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) أو `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | يتخطّى فحص «يستلزم» البنيوي (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | يتخطّى فحص «يمنع» البنيوي (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / اسم صنف | لا شيء | شيفرة تعيد كتابة الإدخال الخام قبل التحليل — [مُعدِّلان مدمجان](#المعدلان-المدمجان) إضافة إلى ما تكتبه أنت. انظر [مُعدِّلات الإدخال](#معدلات-الإدخال). |

خيارات التاريخ الأربعة تؤثر فقط في نصوص التواريخ المنسّقة التي تنتجها مُثريات التفسير (في وضع `INTERPRETATION`)؛ ولا تغيّر التحقق. ويجوز إغفال قيم الباني — فأي خيار تُرك دون ضبط (أو مُرّر إليه `null`) يبقى على قيمته الافتراضية.

### الرسائل والتسميات المُترجَمة

يختار `language(...)` لغةَ **ثلاثة** أنواع من النصوص المقروءة للبشر: رسائل الأخطاء، وتسميات التفسير (`getLabel()` في كل `GS1AIInterpretation`)، ووصوف المعرّفات (`getDescription()` في كل `GS1AIObjectElement`).

يعرّف `GaiaConstants.Language` **35 لغة** تغطي أكثر لغات العالم انتشارًا: الإنجليزية، والفرنسية، والإسبانية، والألمانية، والإيطالية، والبرتغالية، والهولندية، والبولندية، والروسية، والأوكرانية، والتشيكية، والسويدية، والصينية، واليابانية، والكورية، والعربية، والإندونيسية، والهندية، والتركية، والبنغالية، والأردية، والفيتنامية، والبيدجن النيجيرية، والعربية المصرية، والماراثية، والتيلوغوية، والتاميلية، والكانتونية، والصينية الوو، والتغالوغية، والفارسية، والهوساوية، والبنجابية، والجاوية، والسواحيلية.

حالة الترجمة (كما تُشحن):
- **تسميات التفسير** — مترجمة لجميع اللغات.
- **رسائل الأخطاء** — مترجمة لجميع اللغات.
- **وصوف المعرّفات** — مترجمة لجميع اللغات عدا الإنجليزية. فالإنجليزية ليست فهرسًا منفصلًا: تُقرأ مباشرة من حقل `description` في مُدخلة المعرّف داخل `gs1-application-identifiers.jsonld`، وهو ما يرجع إليه كل وصف معرّف في نهاية المطاف.

البيدجن النيجيرية (`NIGERIAN_PIDGIN`)، وهي كريولية أساسها الإنجليزية، تعيد استخدام النص الإنجليزي عمدًا في تسميات التفسير ورسائل الأخطاء. ووصوف المعرّفات هي الاستثناء من هذا الاستثناء: فهي مترجمة بصياغة بيدجن حقيقية بدل إعادة استخدام الإنجليزية، لأن فهارس وصوف المعرّفات أُنتجت باستقلال عن فهارس التسميات والرسائل. ويُستحسن أن يراجع الناطقون الأصليون الترجماتِ الآلية قبل الاعتماد عليها في بيئة التشغيل.

أي رسالة أو تسمية أو وصف يغيب عن فهرس لغةٍ ما يرجع إلى الإنجليزية. واللغات التي تُكتب من اليمين إلى اليسار (العربية والأردية والعربية المصرية والفارسية) مخزّنة بصورة صحيحة كنصوص؛ أما عرضها من اليمين إلى اليسار فمسؤولية طبقة العرض.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

وتسميات التفسير تُترجَم بالطريقة نفسها (القيم لا تتغيّر — التسميات وحدها):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

ووصوف المعرّفات تُترجَم بالطريقة نفسها (وحده `getTitle()`، مثل `"GTIN"`، لا يُترجَم):

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

## مُعدِّلات الإدخال

**مُعدِّل الإدخال** شيفرة تعيد كتابة نص الإدخال الخام قبل أن يحلّله Gaia. وُجدت المُعدِّلات من أجل الإدخال الذي يصل مشوّهًا أصلًا — ماسحٌ يستبدل بفاصل GS محرفًا قابلًا للطباعة، أو وسيطٌ برمجي يلفّ الحمولة ببادئة خاصة بالمورّد، أو نظامٌ مضيف يحوّل كل شيء إلى حروف كبيرة. فبدل معالجة كل نص مسبقًا عند كل موضع استدعاء (ثم إصابته بخلل خفيّ في أحدها)، سجّل التطبيع مرة واحدة على `ParseConfig` ودع المحلّل يطبّقه.

تعمل المُعدِّلات في مستهلّ `GaiaParser.parse(...)` تمامًا — قبل نزع معرّف الارتباط، وقبل كشف معرّف ترميز AIM، وقبل مسار GS1. وكل ما يليها لا يرى إلا النص بعد إعادة الكتابة. **ولا شيء مضبوط افتراضيًا**، بما في ذلك [المُعدِّلان المدمجان](#المعدلان-المدمجان) — فالتفعيل يكون لكل `ParseConfig` على حدة.

**الواجهة:** `tools.pantheum.gaia.modifier.ModifierInterface`

### المُعدِّلان المدمجان

يأتي في نواة الـ jar مُعدِّلان، في `tools.pantheum.gaia.modifier.custom`. وهما يغطيان أشيع صورتين تصل بهما حمولة GS1 مشوّهة — أقواس HRI المطبوعة تُعامَل بوصفها بيانات، والمسافات الدخيلة — فلا تحتاج الحالات الشائعة إلى صنف مخصّص:

| الصنف | `getName()` | ما يفعله |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | ينزع أقواس HRI حول كل معرّف (`(01)…(10)…`) ويعيد فاصل FNC1 الذي كانت تتضمّنه. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | يزيل كل مسافة (`0x20`) من سلسلة عناصر المعرّفات. |

وهما تطبيقان عاديان لـ `ModifierInterface` بلا أي وضع خاص — تُسجَّل وتُرتَّب ويُبلَّغ عنها وتفشل تمامًا كما يحدث لما تكتبه أنت:

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

كلاهما عديم الحالة وآمن للاستخدام من خيوط متعددة، فيمكن مشاركة نسخة واحدة منه، وكلاهما قابل للعنونة باسم الصنف الكامل في الإعدادات المبنية على ملفات ضبط (انظر [تسجيل المُعدِّلات](#تسجيل-المعدلات)).

#### `ModifierRemoveAIBrackets`

التفسير المقروء للبشر في GS1 يطبع كل معرّف بين قوسين — `(01)09521234543213(10)ABC123` — على سبيل العرف الطباعي المحض. وماسحٌ أو وسيطٌ برمجي مضبوط على إخراج HRI يمرّر هذه الأقواس بوصفها بيانات، فلا يدري المقطِّع ما يصنع بها.

ونزع الأقواس نصف العمل فحسب. ففي HRI، القوس الفاتح للمعرّف *التالي* هو ما يحدّد نهاية القيمة السابقة، فلا يحتاج المعرّف متغير الطول إلى FNC1 في الصيغة المقوّسة. وإذا نزعت الأقواس ببساطة اختفى ذلك الحدّ:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

لذلك **يعيد المُعدِّل إدراج FNC1 عند كل حدٍّ يسبقه معرّف متغير الطول**، فيستعيد بالضبط ما كانت الأقواس ترمّزه:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

يُبحث عن الطول في `AiDefinitionRegistry` الخاص بالمحلّل نفسه، فتُعالَج كل المعرّفات متغيرة الطول بدل قائمة مثبَّتة في الشيفرة. وثلاث حالات تُترك عمدًا دون مساس: قيمة تنتهي أصلًا بـ FNC1 (فمصدرٌ يخرج بالعرفين لا يحصل على فاصل ثانٍ)، ورمز مقوّس ليس معرّفًا معروفًا (فالمعرّف المجهول لا يقول شيئًا عن طوله)، والمعرّف الأخير في النص.

وإعادة الكتابة **مُتماثلة القوى** — تشغيلها على ناتجها لا يغيّر شيئًا — فهي آمنة على تدفّق مختلط لا تكون فيه الأقواس إلا في بعض المدخلات.

> **حدّ.** القوسان `(` و`)` هما نفساهما محرفا بيانات صالحان في GS1، والنمط المستخدم ليس إلا `\((\d{2,4})\)`. فقيمةٌ تصادف أن تحوي عددًا من خانتين إلى أربع بين قوسين ستُنزع أقواسها أيضًا. طبّق هذا فقط على مصدر يستعمل عرف أقواس HRI، لا على مصدر يستعمل قيمًا مقوّسة حقيقية.

#### `ModifierRemoveSpaces`

بعض الماسحات والوسائط البرمجية ومسارات طباعة الملصقات تُقحم مسافات دخيلة في سلسلة عناصر سليمة البنية — لحشو حقل ثابت العرض، أو لفصل مجموعات مقروءة للبشر، أو للفّ قيمة طويلة. والمقطِّع يعامل كل مسافة بوصفها بيانات، فتفسد القيمة التي هي فيها، وتُزيح كل ما بعدها إن كان المعرّف متغير الطول.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

لا يُزال إلا محرف ASCII `0x20`. وتُترك المسافات البيضاء الأخرى في مكانها — فالجدولة مثلًا خارج مجموعة محارف GS1 القابلة للترميز، فيبلّغ عنها المحلّل بالخطأ `GE-S008` بدل أن تُكنَس في صمت.

> **حدّ.** المسافة (`0x20`) جزء من مجموعة محارف GS1 الثابتة، فقد يحوي رقم دفعة/تشغيلة أو رقم قطعة لدى العميل مسافةً بوجه مشروع. والمُعدِّل لا يميّز المسافة الدخيلة من الأصيلة؛ فطبّقه فقط على مصدر تعلم أنه لا يستعمل المسافات داخل قيم معرّفاته.

#### البادئات تُتخطّى ولا يُعاد كتابتها

تعمل المُعدِّلات قبل أن ينزع المحلّل شيئًا، فقد يظلّ الإدخال الخام حاملًا معرّف ارتباط ومعرّف ترميز AIM ومؤشّر ECI. وكلا المُعدِّلين المدمجين يحدّد بداية سلسلة عناصر المعرّفات بمنطق `CorrelationIdParser` و`DataCarrierParser` الخاص بالمحلّل نفسه، ثم يعيد الكتابة من ذلك الموضع فصاعدًا فحسب، ويصل الناتج بالبادئة **دون أن يمسّها**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

أما نواقل EAN/UPC التي تُحشى قيمتها إلى GTIN-14 (`isRequiresGtinPadding()`) فتُتخطّى كليًا — إذ حمولتها قيمة باركود رقمية خام بلا بنية معرّفات، فلا يمكن للأقواس ولا للمسافات أن تحمل معنى فيها.

#### الترتيب: المسافات قبل الأقواس

عند استخدامهما معًا، **سجّل `ModifierRemoveSpaces` أولًا**. فمطابقة الأقواس حسّاسة للموضع: `( 01 )` المحشوّة بالمسافات لا تطابق `\((\d{2,4})\)`، فتبقى الأقواس ولا يُستعاد الفاصل الذي كانت تتضمّنه أبدًا.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### كتابة مُعدِّل

اكتب مُعدِّلك الخاص حين لا يناسبك أيٌّ من المدمجَين — فالواجهة دالة واحدة.

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

وتجاوز النسخة ذات الوسيطين بدلًا من ذلك حين تتوقف إعادة الكتابة على ضبط التحليل:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

العقد:

| القاعدة | التفصيل |
|---|---|
| عديم الحالة وآمن مع الخيوط المتعددة | تُخزَّن نسخة واحدة لكل صنف وتُشارَك في كل عمليات التحليل. |
| بانٍ عام بلا وسائط | مطلوب فقط حين يُشار إلى المُعدِّل باسم الصنف. |
| تعامل مع `null` والإدخال الفارغ | فالمحلّل لا يرشّحهما قبل تشغيل السلسلة. |
| إعادة `null` تعني «لا تغيير» | فتُمرَّر القيمة السابقة كما هي. أعِد `input` دون تغيير حين لا ينطبق المُعدِّل. |
| فضّل الإعادة دون تغيير على رمي استثناء | فالمُعدِّل الذي يرمي استثناءً يُجهض التحليل — انظر [معالجة الإخفاق](#معالجة-إخفاق-المعدل). |
| `getName()` | تجاوزها للتحكّم في الاسم المُبلَّغ عنه في `ModifierInfo`؛ وافتراضها اسم الصنف المبسّط. |

### تسجيل المُعدِّلات

تعمل المُعدِّلات بالترتيب الذي تُضاف به، ويتلقّى كلٌّ منها ناتج سابقه. سجّلها بنسخة كائن، أو باسم الصنف الكامل، أو بقائمة من أيٍّ منهما:

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

يُسمّى [المُعدِّلان المدمجان](#المعدلان-المدمجان) بالطريقة نفسها التي تسمّي بها مُعدِّلاتك — **باسم كامل دائمًا**. فليس لهما اسم مختصر ولا بحث بالأسماء المستعارة؛ إذ يحلّ `ModifierRegistry` كل مُعدِّل، مشحونًا كان أم لا، باسم صنفه الكامل.

يحلّ `ModifierRegistry` الأسماء، فينشئ نسخة واحدة من كل صنف عبر بانيه بلا وسائط ويحتفظ بها لكل ضبط لاحق يسمّي الصنف نفسه. ويقع الحلّ **عند بناء الضبط**، فاسمٌ لا يُعثر عليه، أو لا يطبّق `ModifierInterface`، أو يتعذّر إنشاء نسخة منه، يرمي `IllegalArgumentException` هناك — لا في صمت وقت التحليل. أما مُعدِّل لا يمكن بناؤه بالانعكاس (كأن يحمل اعتمادية محقونة) فيمكن تسجيله مسبقًا ليبقى قابلًا للعنونة بالاسم:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### تفقّد ما فعله المُعدِّل

حين تكون المُعدِّلات مضبوطة، يعكس `ParseResult.getPayload()` الإدخال **بعد التعديل**. أما الأصل فمحفوظ في `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

يُبلّغ `getAppliedModifiers()` عن `getName()` لكل مُعدِّل، وافتراضها اسم الصنف المبسّط، لكن المُعدِّلين المدمجين يتجاوزانها — فسلسلة منهما تُبلّغ عن أسماء العرض لا أسماء الأصناف:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

ويعيد `getModifierInfo()` القيمة `null` حين لا تكون أي مُعدِّلات مضبوطة. وإن عملت المُعدِّلات لكن أعادت كلها الإدخال دون تغيير، كانت المعلومة موجودة و`isModified()` تساوي `false` — فلا يُدرَج في `getAppliedModifiers()` إلا ما غيّر الإدخال فعلًا.

### معالجة إخفاق المُعدِّل

المُعدِّل الذي يرمي استثناءً يُجهض التحليل. ويُلَفّ الاستثناء في `GaiaModifierException` يسمّي المُعدِّل المخالف، وتحمل النتيجة خطأً داخليًا `GE-I001` تتضمّن رسالته ذلك الاسم؛ ويُبلّغ `getPayload()` عن الإدخال غير المعدَّل. والتحليل **لا** يمضي عمدًا بنصٍّ أُعيدت كتابته نصف إعادة — فخطوة تطبيع أخفقت في صمت تنتج نتائج تبدو صالحة لكنها حُلّلت من إدخال خاطئ.

---

## أوضاع التحليل

يسمّي كل وضع أعمق [مرحلة في المسار](#مسار-التحليل) يشغّلها؛ وكل مرحلة قبلها تعمل أيضًا.

| الوضع | يعمل حتى | يجيب عن |
|---|---|---|
| `DATA_CARRIER` | المرحلة 1 (توجيه الإدخال) | أي نظام ترميز حمل هذا؟ |
| `SYNTAX` | المرحلة 2 (البنية النحوية) | هل رموز المعرّفات وأطوالها سليمة البنية؟ |
| `CONTENT` | المرحلة 3 (المحتوى) | هل القيم بيانات GS1 صالحة؟ |
| `INTERPRETATION` | المرحلة 4 (التفسير) | ماذا تعني القيم؟ |

### وضع DATA_CARRIER

يتوقف بعد المرحلة 1 — يتحقّق من معرّف ترميز AIM ويحدّد نظام الترميز، لكنه لا يدخل مسار تحليل المعرّفات. مفيد لتحديد نظام الترميز والتوجيه دون كلفة التحقق الكامل.

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

**استخدمه حين:** يحتاج تطبيقك إلى تحديد نوع الباركود قبل أن يقرّر كيف يعالج الحمولة — كالتوجيه إلى معالجات مختلفة للترميزات الأحادية البُعد مقابل الثنائية. ولذلك التوجيه، فضّل [`DataCarrierType`](#datacarrierentry-وdatacarriertype) المُصنَّف (`getDataCarrier().getDataCarrierType()`) على مطابقة `getName()` نصيًا.

---

### وضع SYNTAX

يتوقف بعد المرحلة 2. مفيد للفرز البنيوي المسبق دون كلفة التحقق من المحتوى.

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

**استخدمه حين:** تريد التأكد من سلامة بنية رموز المعرّفات وأطوال البيانات قبل الالتزام بالتحقق الكامل، أو حين تمسح كميات كبيرة تندر فيها أخطاء المحتوى.

---

### وضع CONTENT

يتوقف بعد المرحلة 3.

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

> أغلب المعرّفات لا تقوم بذاتها: فالمعرّفات `10` (BATCH/LOT) و`17` (USE BY or EXPIRY)
> و`21` (SERIAL) *يستلزم* كلٌّ منها مفتاح تعريف كالمعرّف `01` في سلسلة العناصر
> نفسها، فإغفال GTIN أعلاه يُخفق في المرحلة 2 بالخطأ `GE-S005` ولا يبلغ التحقق من
> المحتوى أصلًا. اضبط `skipRequiresCheck(true)` على `ParseConfig` لتحليل الأجزاء
> التي تُغفل معرّفاتها المرافقة عمدًا.

**استخدمه حين:** تحتاج إلى معرفة ما إذا كانت القيمة الممسوحة مطابقة تمامًا لـ GS1 قبل استعمالها في إجراء عمل، دون كلفة إثراء التفسير.

---

### وضع INTERPRETATION (الافتراضي)

يشغّل المسار كاملًا حتى المرحلة 4. وهو الافتراضي عند استدعاء `parse(String)` بلا وسيط وضع. ولا يُثري إلا العناصر التي اجتازت التحقق من المحتوى بلا أخطاء.

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

**مثال للمخرجات:**
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

**استخدمه حين:** تبني طبقات عرض، أو أدوات تدقيق ملصقات، أو أي واجهة تحتاج إلى تفكيك قيم المعرّفات بصورة مألوفة للبشر.

---

## معرّف الارتباط

تُلحق بعض سير العمل معرّف ارتباط خاصًا من ثماني خانات ببداية إدخال GS1 الخام، ليتسنّى ربط أحداث المسح بجلسة أو معاملة. والصيغة هي:

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

علامة التلدة (`~`) هي الفاصل. وهي **ليست** جزءًا من محتوى GS1 — إذ تُنزع قبل بدء أي تحليل لـ GS1.

### قواعد الكشف

تُكتشف البادئة حين يبدأ الإدخال بثماني خانات عشرية ASCII بالضبط (`0`–`9`) تليها مباشرة `~`. فإن لم يكن المحرف التاسع `~`، أو لم يكن أحد المحارف الثمانية الأولى خانة رقمية، عُومل الإدخال بوصفه محتوى GS1 عاديًا بلا بادئة ارتباط.

### الوصول إلى معرّف الارتباط

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

### الجمع مع معرّف ترميز AIM

قد تسبق بادئة الارتباط معرّفَ ترميز AIM. والمحلّل يعالج ذلك بلا عناء منك:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**صنف التطبيق:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

يرمّز **GS1 Digital Link** قيمة معرّف تطبيق واحدة أو أكثر داخل بنية عنوان HTTP(S) مباشرة، فيتيح معرّفات للمنتجات المادية قابلة للحلّ عبر الويب. ويطبّق GAIA معيار *GS1 Digital Link Standard: URI Syntax* (الإصدار 1.7.0) للعناوين **غير المضغوطة**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

يتعرّف `GaiaParser` على عناوين Digital Link تلقائيًا — فأي إدخال يبدأ بـ `http://` أو `https://` يُوجَّه إلى `GS1DLParser`، الذي يشغّل مرحلتَي المحتوى والتفسير نفسيهما اللتين يشغّلهما مسار سلسلة العناصر.

### بنية العنوان وأدوار المعرّفات

يؤدي كل معرّف في عنوان Digital Link أحد ثلاثة أدوار، تُعرَض على كل `GS1AIObjectElement` عبر `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| الدور | الموضع | مثال |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | أول زوج `/ai/value` في المسار (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | أزواج المسار التالية، مرتّبة بحسب المفتاح الأساسي (§4.9) | `/10/ABC`، `/21/SER` |
| `DATA_ATTRIBUTE` | معاملات الاستعلام ذات المفاتيح الرقمية بالكامل (§4.10) | `?17=271231` |

القواعد البنيوية المفروضة (`DLPathRules`):
- مفتاح تعريف أساسي **واحد** بالضبط في المسار؛ وأي مفاتيح إضافية يجب ترميزها بوصفها سمات بيانات في الاستعلام.
- يجب أن يقبل المفتاح الأساسي مُقيِّدات المفاتيح وأن تظهر بالترتيب المقرّر. ويجوز إغفال المُقيِّدات الاختيارية، لكن ما يوجد منها يجب أن يتّبع الترتيب الثابت — انظر [ترتيب المُقيِّدات](#ترتيب-المقيدات).
- يجوز أن تسبق المفتاحَ الأساسي مقاطعُ مسار مخصّصة كيفما كانت (مثل `/products/au/01/...`)؛ استرجعها عبر `getDigitalLinkInfo().getCustomPathStem()`.
- مفاتيح الاستعلام غير الرقمية (`linkType` و`context` ومعاملات الامتداد مثل `23P`) تُتجاهَل؛ أما المفاتيح الرقمية بالكامل فيجب أن تكون معرّفات صالحة موسومة بـ `validAsDataAttribute`.
- محارف القيم المرمّزة بعلامة النسبة المئوية تُفكّ؛ والمعرّفان `(03)` و`(8014)` غير مسموح بهما.

المفاتيح الأساسية وتسلسلات المُقيِّدات المقبولة لها **مُستمدّة من البيانات** في تعريفات المعرّفات — من العلَم `gs1DigitalLinkPrimaryKey` والسمة `gs1DigitalLinkQualifiers` — لا مثبَّتة في الشيفرة.

وأي مخالفة بنيوية، أو إدخال ليس عنوانًا، تُنتج خطأً بنيويًا في Digital Link (`GE-L001`–`GE-L014`، رمز لكل حالة). أما البيانات الوصفية المفكّكة للعنوان (`scheme` و`domain` و`path` و`customPathStem` و`query`، و`java.net.URL`) فمتاحة عبر `getDigitalLinkInfo()` حتى مع وجود أخطاء بنيوية.

### ترتيب المُقيِّدات

لكل مفتاح أساسي، يسرد `gs1DigitalLinkQualifiers` تسلسلًا **مرتّبًا** واحدًا أو أكثر من المُقيِّدات. وداخل التسلسل، المعرّف الموضوع بين قوسين معقوفين **اختياري**، وغير الموضوع بينهما **مطلوب** — على مثال ترميز `[cpv-comp]` في صيغة ABNF بالقسم §4.9. وتسلسلات المفتاح الأساسي الواحد بدائل يستبعد بعضها بعضًا.

فـ GTIN (`01`) مثلًا يعرّف تسلسلين:

| المسار | التسلسل | المعنى |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV وLOT وSER — كلٌّ اختياري، لكن بهذا الترتيب الثابت |
| upui-path | `235` | TPX (مطلوب)؛ GTIN + TPX = UPUI |

فـ `/01/09506000134352/10/LOT-ABC/21/SER` صالح (LOT قبل SER، وCPV مُغفَل)، و`/01/.../21/SER/10/LOT-ABC` **مرفوض** (خارج الترتيب)، و`/01/09506000134352/235/2ABC456` هو upui-path. وفحص الترتيب مطابقةُ متتاليةٍ جزئية تحفظ الترتيب، فيمكن تخطّي المعرّفات الاختيارية دون إعادة ترتيبها أبدًا.

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

**صنف التطبيق:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## التعامل مع النتائج

### ParseResult

النتيجة العليا التي يعيدها `GaiaParser.parse()`.

| الدالة | تعيد | الوصف |
|---|---|---|
| `isValid()` | `boolean` | `true` إن لم توجد أخطاء على أي مستوى. والتحذيرات لا تؤثر في الصلاحية. وهي `true` دائمًا حين يكون `getAiObject()` مساويًا لـ `null`. |
| `getPayload()` | `String` | نص الإدخال بعد نزع بادئة الارتباط — وبعد أن تكون [مُعدِّلات الإدخال](#معدلات-الإدخال) قد أعادت كتابته. |
| `getPayloadContent()` | `String` | الحمولة بعد نزع معرّف ترميز AIM وبادئة ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`، أو `GS1_DIGITAL_LINK`، أو `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (ناقل بيانات رُفض لأنه ليس GS1، كناقل Code 39 بمعرّف `]A0`)، أو `UNABLE_TO_DETERMINE_CONTENT` (حين يكون `aiObject` مساويًا لـ `null`، كما في وضع `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | عمق المسار المضبوط (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | أعمق مرحلة بلغها التحليل فعلًا — انظر أدناه. |
| `isParseComplete()` | `boolean` | `true` إن بلغ التحليل العمق المطلوب (`achieved == requested`). مستقلة عن `isValid()`. |
| `getAiObject()` | `GS1AIObject` | كل المعرّفات التي جرى حلّها. وتساوي `null` في وضع `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | كل الأخطاء التي ليست WARNING (على مستوى الكائن + كل مستويات العناصر). |
| `getWarnings()` | `List<GaiaError>` | كل التنبيهات من نوع WARNING (على مستوى الكائن + كل مستويات العناصر). |
| `hasWarnings()` | `boolean` | `true` إن أُثير أي تنبيه من نوع WARNING. |
| `getIssues()` | `List<GaiaError>` | الأخطاء والتحذيرات مجتمعةً. |
| `hasDataCarrier()` | `boolean` | `true` إن جرى التعرّف على معرّف ترميز AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | البيانات الوصفية لنظام الترميز، أو `null` إن لم يُحدَّد ناقل. |
| `hasEci()` | `boolean` | `true` إن نُزع مؤشّر ECI من الحمولة. |
| `getEci()` | `EciEntry` | البيانات الوصفية لترميز ECI، أو `null`. |
| `hasCorrelationId()` | `boolean` | `true` إن وُجدت بادئة ارتباط `DDDDDDDD~` في الإدخال الأصلي. |
| `getCorrelationInfo()` | `CorrelationInfo` | معرّف الارتباط المستخرَج، أو `null` إن لم يوجد. |
| `isInputModified()` | `boolean` | `true` إن غيّر [مُعدِّل إدخال](#معدلات-الإدخال) الإدخالَ. |
| `getModifierInfo()` | `ModifierInfo` | ما فعلته سلسلة المُعدِّلات — `getOriginalInput()` و`getModifiedInput()` و`getAppliedModifiers()`. وتساوي `null` إن لم تُضبط أي مُعدِّلات. |
| `getTiming()` | `ProcessingTiming` | توقيت التحليل بالساعة الجدارية — `getStartTime()` (`Instant`)، و`getProcessingTime()` (`Duration`)، و`getProcessingTimeMillis()` (`long`)، و`getCompletionTime()`. وتساوي `null` إن لم ينتجها `GaiaParser`. |
| `getVersion()` | `String` | إصدار المكتبة الذي أنتج النتيجة. |

#### الوضع المطلوب مقابل الوضع المُحقَّق

يصعد المسار سلّم **SYNTAX → CONTENT → INTERPRETATION** ويتوقف مبكرًا عند الأخطاء، فقد يكون الوضع *المُحقَّق* فعلًا أضحل من الوضع *المطلوب*. ويُبلّغ `getAchievedParseMode()` عن المدى الذي بلغه:

| المطلوب | ما يحدث | المُحقَّق | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | خطأ **نحوي / بنيوي** يوقف التحليل بعد التقطيع | `SYNTAX` | `false` |
| `INTERPRETATION` | خطأ **محتوى** (تنسيق أو رقم تحقق خاطئ) يمنع الإثراء | `CONTENT` | `false` |
| `CONTENT` | مرحلة المحتوى تعمل حتى نهايتها دائمًا (فالأخطاء تُوسَم ولا تكون قاتلة) | `CONTENT` | `true` |
| أي وضع (بإدخال سليم) | يبلغ المسار العمق المطلوب | = المطلوب | `true` |
| `DATA_CARRIER` | يُتحقَّق من الناقل؛ ولا يُحلَّل محتوى المعرّفات | `DATA_CARRIER` | `true` |
| أي وضع | يُرفض ناقل البيانات قبل تحليل المعرّفات (كناقل `]A0` غير التابع لـ GS1) | `SYNTAX` | `false` |

`isParseComplete()` مستقلة عن `isValid()`: فتحليل `CONTENT` لـ GTIN برقم تحقق خاطئ **مكتمل** (إذ شغّل مرحلة المحتوى) لكنه **غير صالح** (إذ أخفق رقم التحقق). استخدم `isParseComplete()` لتسأل «هل عمل المسار إلى العمق الذي طلبته؟» و`isValid()` لتسأل «هل البيانات سليمة البنية؟».

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

مجموعة عناصر المعرّفات التي جرى حلّها.

| الدالة | الوصف |
|---|---|
| `getAis()` | كل نسخ `GS1AIObjectElement` بترتيب الإدخال. |
| `get(String aiCode)` | أول عنصر يطابق رمز المعرّف المعطى، أو `null`. |
| `contains(String aiCode)` | `true` إن وُجد معرّف بذلك الرمز. |
| `size()` | عدد المعرّفات التي جرى حلّها. |
| `isValid()` | `true` إن لم توجد أخطاء على مستوى الكائن ولم يحمل أي عنصر أخطاء. |
| `toHriString()` | نص HRI، مثل `(01)09506000134352 (17)261231`. |
| `toElementString()` | سلسلة العناصر الخام — بلا أقواس، وبـ FNC1 بعد كل عنصر متغير الطول — مثل `010950600013435210LOT-ABC<GS>17271231`. وتعيد `null` إن كانت `isValid()` تساوي `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` حين تكون `hasDigitalLink()` صحيحة، وإلا `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` إن كان الإدخال عنوان GS1 Digital Link يحمل مفتاح تعريف أساسيًا. أما عنوان سليم البنية بلا مفتاح أساسي فيظلّ يعرض `getDigitalLinkInfo()` لكنه يعيد `false` هنا. |
| `getCanonicalDigitalLink()` | عنوان GS1 Digital Link المعياري (§4.12) على `https://id.gs1.org` — المفتاح الأساسي والمُقيِّدات مقاطعَ مسار، وسمات البيانات معاملاتِ استعلام مرتّبة بمفتاح المعرّف — أو `null` إن لم يوجد مفتاح أساسي. |
| `getDigitalLinkInfo()` | البيانات الوصفية لتفكيك العنوان (`getUri()`، `getUrl()`، `scheme`، `domain`، `path`، `getCustomPathStem()`، `query`)، أو `null` إن لم يكن Digital Link. |
| `getAllErrors()` | أخطاء مستوى الكائن + كل أخطاء العناصر (عدا WARNING). |
| `getAllWarnings()` | تحذيرات مستوى الكائن + كل تحذيرات العناصر. |
| `getAllIssues()` | كل ذلك مجتمعًا. |

---

### GS1AIObjectElement

نسخة واحدة من معرّف جرى حلّه.

| الدالة | الوصف |
|---|---|
| `getAi()` | رمز المعرّف، مثل `"01"` و`"3102"`. |
| `getTitle()` | عنوان بيانات GS1، مثل `"GTIN"` و`"BATCH/LOT"`. |
| `getDescription()` | وصف GS1 الكامل للمعرّف، **مُترجَمًا إلى لغة التحليل** (مثل `"Global Trade Item Number (GTIN)"` بالإنجليزية). ويرجع إلى النص الإنجليزي من تعريف المعرّف إن لم يكن مترجمًا. |
| `getFormatString()` | واصف التنسيق الشامل للمعرّف *وبياناته*، مثل `"N2+N14"` للمعرّف `01`، و`"N2+X..20"` للمعرّف `10`، و`"N4+N3+N..15"` للمعرّف `3932`. |
| `getValue()` | قيمة البيانات الخام المستخرَجة من سلسلة العناصر. |
| `isFixedLength()` | `true` إن كان طول بيانات المعرّف ثابتًا. |
| `getPosition()` | إزاحة المحرف في الإدخال الأصلي، بدءًا من الصفر. |
| `getGS1ComponentValues()` | شرائح القيم لكل مكوّن (في المعرّفات متعددة المكوّنات). |
| `getErrors()` | أخطاء مستوى العنصر التي ليست WARNING. |
| `getWarnings()` | تنبيهات WARNING على مستوى العنصر. |
| `getIssues()` | أخطاء العنصر وتحذيراته مجتمعةً. |
| `hasErrors()` | `true` إن أُرفق أي خطأ ليس WARNING. |
| `hasWarnings()` | `true` إن أُرفق أي تنبيه WARNING. |
| `getInterpretations()` | مُدخلات `GS1AIInterpretation` (تُملأ في وضع INTERPRETATION). |
| `getInterpretation(String type)` | أول تفسير يطابق مفتاح النوع المعطى من `GS1Constants_Enricher`، أو `null`. |
| `getDigitalLinkAIType()` | دور العنصر في Digital Link (`PRIMARY_IDENTIFICATION_KEY`، `KEY_QUALIFIER`، `DATA_ATTRIBUTE`)، أو `null` لمدخلات سلاسل العناصر. |
| `hasDigitalLinkAIType()` | `true` إن أُسند دور Digital Link. |

---

### GaiaError

خطأ تحقق أو تنبيه غير قابل للتغيير.

| الدالة | الوصف |
|---|---|
| `getId()` | معرّف الفهرس، مثل `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`، أو `INTEGRITY_ERROR`، أو `FORMAT_ERROR`، أو `DATA_ERROR`، أو `WARNING`. |
| `getStage()` | `DATA_CARRIER`، أو `DIGITAL_LINK`، أو `SYNTAX`، أو `CONTENT`، أو `INTERNAL`. |
| `getCode()` | رمز قصير مقروء للآلة. |
| `getAi()` | رمز المعرّف الذي سبّب الخطأ، أو `null` لأخطاء مستوى الكائن. |
| `getMessage()` | رسالة مقروءة للبشر بعد إدراج القيم فيها. |
| `getPosition()` | إزاحة المحرف في الإدخال الأصلي، بدءًا من الصفر. |

---

### GS1AIInterpretation

جزء تفسير واحد مُسمّى، يُرفَق بـ `GS1AIObjectElement` في وضع `INTERPRETATION`.

| الدالة | الوصف |
|---|---|
| `getType()` | مفتاح النوع المقروء للآلة، مثل `"DATE_VALUE"` و`"GS1_COMPANY_PREFIX"`. ثابت عبر اللغات. |
| `getLabel()` | تسمية مقروءة للبشر، **مُترجَمة إلى لغة التحليل** (مثل `"Date"` / `"GS1 company prefix"` بالإنجليزية). |
| `getValue()` | القيمة المستخرَجة أو المُثراة، مثل `"31/12/2026"` و`"9506000"`. غير مُترجَمة. |

---

### DataCarrierEntry وDataCarrierType

حين يحمل الإدخال معرّف ترميز AIM، يعيد `ParseResult.getDataCarrier()` كائن `DataCarrierEntry` يصف الرمز الذي حمل البيانات. والمُدخلة هي سجلّ الفهرس المحدّد لمعرّف ترميز AIM المطابق؛ أما `DataCarrierType` فهو التعداد — وقت الترجمة — الذي تنتمي إليه.

#### DataCarrierEntry

البيانات الوصفية لمعرّف ترميز AIM واحد جرى التعرّف عليه (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| الدالة | الوصف |
|---|---|
| `getAimCodeId()` | معرّف ترميز AIM المطابق، مثل `"]C1"`. |
| `getName()` | الاسم المقروء للبشر للرمز المحدّد، مثل `"GS1-128 / ISBT 128"` و`"EAN-8"`. |
| `getDescription()` | وصف أطول للناقل. |
| `getType()` | النوع البنيوي للناقل نصًا (يعكس `getDataCarrierType().getCategory()`). |
| `getStandard()` | معيار نظام الترميز، حيث يكون مسجَّلًا. |
| `getDataCarrierType()` | قيمة `DataCarrierType` المُصنَّفة لهذه المُدخلة — فضّلها للتوجيه البرمجي. |
| `isGs1Capable()` | `true` إن كان الناقل قادرًا على حمل بيانات GS1 (سلاسل عناصر معرّفات و/أو Digital Link). |
| `isGs1AICapable()` | `true` إن كان الناقل قادرًا على حمل سلاسل عناصر معرّفات GS1. |
| `isGs1DigitalLinkCapable()` | `true` إن كان الناقل قادرًا على حمل عنوان GS1 Digital Link. |
| `isEciCapable()` | `true` إن كان الناقل يدعم مؤشّر ECI. |
| `isRequiresGtinPadding()` | `true` لنواقل EAN/UPC/ITF التي تُحشى قيمتها الرقمية إلى GTIN-14 قبل تحليل المعرّفات. |

#### DataCarrierType

تعداد — وقت الترجمة — لأنواع نواقل البيانات، مفتاحه معرّف ترميز AIM المخصَّص في ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). والمحرف التالي لـ `]` (*محرف الرمز*) يختار العائلة؛ ومعظم العائلات تقابل ثابتًا واحدًا يغطي كل المُعدِّلات (فـ `ITF` يغطي `]I0`–`]I2`، و`EAN_UPC` يغطي EAN-13 وUPC-A وUPC-E وEAN-8). وحيث تحجز GS1 مُعدِّلًا لبيانات المعرّفات، يكون ذلك النوع ثابتًا مستقلًا — `GS1_128` (`]C1`)، و`GS1_DATA_MATRIX` (`]d2`)، و`GS1_QR_CODE` (`]Q3`)، و`GS1_DOT_CODE` (`]J1`) — متمايزًا عن نظيره العادي. وحين لا يوجد معرّف ترميز AIM، أو يسمّي ناقلًا مجهولًا، يكون النوع `UNKNOWN`.

| الدالة | الوصف |
|---|---|
| `getCategory()` | الفئة العريضة `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`، أو `STACKED_LINEAR`، أو `TWO_D`، أو `POSTAL`، أو `OCR`، أو `OTHER`. |
| `getCodeChar()` | محرف رمز AIM الذي يحدّد العائلة، مثل `"Q"` لـ QR Code؛ و`null` للنوع `UNKNOWN`. |
| `getDisplayName()` | الاسم المقروء للبشر لـ *النوع* (وقد يكون أعمّ من `DataCarrierEntry.getName()` — مثل `"EAN-13 / UPC-A / UPC-E / EAN-8"` مقابل `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` للثوابت التي تدلّ دائمًا على بيانات معرّفات GS1: الأنواع الأربعة المحجوزة لـ GS1 (`GS1_128` و`GS1_DATA_MATRIX` و`GS1_QR_CODE` و`GS1_DOT_CODE`) إضافة إلى `GS1_DATABAR`، وهو تابع لـ GS1 بطبيعته لأن كل مُعدِّل `]e` هو GS1 DataBar. وهي أضيق من `DataCarrierEntry.isGs1AICapable()` — فـ `QR_CODE` العادي يظلّ قادرًا على حمل بيانات معرّفات GS1. |
| `static forAimCodeId(String)` | يحلّ النوع من معرّف ترميز AIM مباشرة (`"]Q3"` → `GS1_QR_CODE`؛ و`"]Q9"` → `QR_CODE`)؛ ويعيد `UNKNOWN` لمعرّف غائب أو مشوّه أو غير معروف. |

التوجيه بالنوع لا بالاسم — كفصل الرموز الأحادية البُعد (Code-128) عن الثنائية (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

يغطي `TWO_D` رموز المصفوفات والنقاط فحسب؛ أما النواقل الأحادية البُعد المكدّسة (`PDF417`
و`CODE_16K` و`CODABLOCK` و`CODE_49`) فهي `STACKED_LINEAR`، وإن شاع تسميتها باركودات
«ثنائية البُعد». ولمعاملة النوعين مجموعةً واحدة — كأن تقرّر ما إذا كنت تحتاج إلى مُصوِّر
لا إلى ماسح ليزري — افحص أيًّا من الفئتين.

> يستلزم حلّ النوع وجود معرّف ترميز AIM في المسح؛ فبدونه تكون `getDataCarrier()` مساوية لـ `null` ويكون النوع `UNKNOWN`. اضبط الماسح على إرسال بادئة معرّف ترميز AIM.

---

## مرجع الأخطاء

| الرمز | المستوى | المرحلة | المعنى |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | بادئة معرّف مجهولة — يتعذّر تحديد طول البيانات |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | الإدخال أقصر من أن يُقرأ منه رمز معرّف كامل |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | قيمة مبتورة — محارف أقل مما يستلزمه المعرّف |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | معرّف تطبيق مكرّر في سلسلة العناصر |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | اعتمادية معرّف مطلوبة مفقودة |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | اقتران معرّفات ممنوع — معرّفان لا يجتمعان |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | إخفاق غير متوقّع في التقطيع |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | محرف خارج مجموعة محارف GS1 القابلة للترميز في سلسلة العناصر |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | فاصل FNC1 المطلوب مفقود بعد معرّف متغير الطول |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | بيانات زائدة بعد بلوغ الحدود القصوى لكل المكوّنات |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | فاصل FNC1 بعد معرّف ثابت الطول في موضع وسط النص |
| `GE-W002` | WARNING | SYNTAX | فاصل FNC1 زائد في نهاية سلسلة العناصر (تنبيه فقط) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | مخالفات بنيوية في عنوان Digital Link — رمز لكل حالة (عنوان مشوّه، أو مخطّط، أو مضيف، أو ترتيب مُقيِّدات، أو معرّف ممنوع، أو غياب مفتاح أساسي (`GE-L013`)، أو تعدّد المفاتيح الأساسية (`GE-L014`)، …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | القيمة لا تطابق النمط النظامي للمعرّف |
| `GE-C003` | DATA_ERROR | CONTENT | إخفاق التحقق من رقم التحقق |
| `GE-C004` | DATA_ERROR | CONTENT | إخفاق التحقق من زوج حرفَي التحقق |
| `GE-C005` | FORMAT_ERROR | CONTENT | قيمة المكوّن تحوي محرفًا خارج مجموعة المحارف المسموحة |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | إخفاقات تنسيق المكوّنات — رمز لكل حالة تدقيق (انظر `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | إخفاقات التحقق الدلالي المخصّص — رمز لكل حالة تدقيق (انظر `content/validator/`). **استثناءات:** فحوص بادئة شركة GS1 الأربعة عشر المذكورة أدناه مستواها `WARNING`، و`GE-C168` (رمز بلد رقمي غير معروف وفق ISO 3166-1) مستواه `FORMAT_ERROR`. |
| فحوص بادئة شركة GS1 | WARNING | CONTENT | المفتاح لا يبدأ ببادئة شركة GS1 معروفة، في المعرّفات ذات مفاتيح GS1 — `GE-C122` (CPID)، و`GE-C129` (GCN)، و`GE-C131` (GDTI)، و`GE-C132` (GIAI)، و`GE-C133` (GINC)، و`GE-C135` (GLN)، و`GE-C137` (GMN)، و`GE-C140` (GRAI)، و`GE-C142` (GSIN)، و`GE-C144` (GSRN)، و`GE-C146` (GTIN)، و`GE-C148` (HIDRI)، و`GE-C153` (ITIP)، و`GE-C165` (SSCC). تنبيه فقط — لا يؤثر في الصلاحية. |
| `GE-C169` | DATA_ERROR | CONTENT | إخفاق رقم التحقق (Luhn) لـ IMEI في المعرّف 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | إخفاق رقم التحقق (Luhn) لـ EID في المعرّف 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | معرّف ترميز AIM غير معروف |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | جرى تحديد الناقل لكنه لا يدعم سلاسل عناصر معرّفات GS1 ولا عناوين Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | خطأ داخلي غير متوقّع |

> **خلل معروف في عرض الرسائل.** قوالب الفهرس تضع القيم المُدرَجة بين علامتَي اقتباس
> مضاعفتين على طريقة MessageFormat (`''{value}''`)، لكن `ErrorRegistry` يُدرج القيم
> بـ `String.replace` العادية، فتبقى المضاعفة إلى `getMessage()` — فسترى حاليًا
> `value ''09506000134351''` حيث تُظهر نصوص الرسائل المقتبسة في هذا الدليل
> `value '09506000134351'`. ويطال ذلك كل رسالة تقتبس قيمة في فهارس اللغات الخمس
> والثلاثين جميعًا. لا تحلّل رسائل الأخطاء؛ بل طابِق على `getId()` / `getCode()`.

---

## الأمان مع الخيوط المتعددة

`GaiaParser` آمن للاستخدام من خيوط متعددة بمجرد إنشائه. ويمكن مشاركة نسخة واحدة بين الخيوط واستخدامها بالتوازي. والنمط الموصى به هو إنشاء نسخة واحدة عند بدء التطبيق وإعادة استخدامها:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

و`ParseConfig` غير قابل للتغيير وآمن للمشاركة بالقدر نفسه. والالتزام الوحيد بالأمان مع الخيوط الذي لا تستطيع المكتبة فرضه نيابةً عنك هو في [مُعدِّلات الإدخال](#معدلات-الإدخال): إذ تُخزَّن نسخة واحدة من كل مُعدِّل وتُشارَك في كل عمليات التحليل المتوازية، فيجب أن تكون التطبيقات عديمة الحالة.

---

## الملحق أ — ثوابت نصوص المعرّفات

يعلن `GS1Constants_AICodes` (في الحزمة `tools.pantheum.gaia.gs1.constants`) ثابتًا من نوع `String` لكل معرّف تطبيق يتعرّف عليه GAIA. استخدم هذه الثوابت بدل تثبيت نصوص رموز المعرّفات في الشيفرة:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

ويحمل كل ثابت الصورة النصية لرمز المعرّف (مثل `AI_01_GTIN = "01"`).

### التعريف والترقيم التسلسلي

| AI | الثابت | الوصف |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | رمز حاوية الشحن التسلسلي (SSCC). |
| `01` | `AI_01_GTIN` | رقم الصنف التجاري العالمي (GTIN). |
| `02` | `AI_02_CONTENT` | رقم الصنف التجاري العالمي (GTIN) للأصناف التجارية المحتواة. |
| `03` | `AI_03_MTO_GTIN` | تعريف الصنف التجاري المصنوع حسب الطلب (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | رقم الدفعة أو التشغيلة. |
| `20` | `AI_20_VARIANT` | متغير المنتج الداخلي. |
| `21` | `AI_21_SERIAL` | الرقم التسلسلي. |
| `22` | `AI_22_CPV` | متغير المنتج الاستهلاكي. |
| `235` | `AI_235_TPX` | امتداد مسلسل خاضع لتحكم طرف ثالث لرقم الصنف التجاري العالمي (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | تعريف إضافي للمنتج يحدده المصنّع. |
| `241` | `AI_241_CUST_PART_NO` | رقم القطعة لدى العميل. |
| `242` | `AI_242_MTO_VARIANT` | رقم تغيير الصنع حسب الطلب. |
| `243` | `AI_243_PCN` | رقم مكوّن التعبئة. |
| `250` | `AI_250_SECONDARY_SERIAL` | الرقم التسلسلي الثانوي. |
| `251` | `AI_251_REF_TO_SOURCE` | مرجع الكيان المصدر. |
| `253` | `AI_253_GDTI` | معرّف نوع المستند العالمي (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | مكوّن امتداد رقم الموقع العالمي (GLN). |
| `255` | `AI_255_GCN` | رقم القسيمة العالمي (GCN). |
| `30` | `AI_30_VAR_COUNT` | عدد متغير من الأصناف (صنف تجاري متغير القياس). |
| `37` | `AI_37_COUNT` | عدد الأصناف التجارية أو أجزاء الصنف التجاري ضمن الوحدة اللوجستية. |

### التواريخ والأوقات

| AI | الثابت | الوصف |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | تاريخ الإنتاج (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | تاريخ الاستحقاق (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | تاريخ التعبئة (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | تاريخ الاستخدام الأفضل قبل (YYMMDD). |
| `16` | `AI_16_SELL_BY` | تاريخ انتهاء البيع (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | تاريخ انتهاء الصلاحية (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | تاريخ ووقت عدم التسليم قبله (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | تاريخ ووقت عدم التسليم بعده (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | تاريخ الإصدار (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | تاريخ ووقت انتهاء الصلاحية (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | تاريخ التجميد الأول (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | تاريخ الحصاد (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | تاريخ الاختبار بحلول (YYMMDD[hhmm]). |

### الكمية والقياس — قياس متغيّر (المتري)

عائلات المعرّفات ذات الأربع خانات `310n`–`369n` ترمّز الكميات متغيّرة القياس. الخانة الثالثة تختار نوع القياس؛ و**الخانة الرابعة** (`n`، من 0 إلى 5) هي عدد المنازل العشرية الضمنية — فـ `AI_3102_NET_WEIGHT_KG` مثلًا يعني الوزن الصافي بالكيلوغرام بمنزلتين عشريتين.

| العائلة | نمط الثابت (`n` = خانة المنازل العشرية) | الوصف |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | الوزن الصافي، كيلوغرام (صنف تجاري متغير القياس). |
| `311n` | `AI_311n_LENGTH_M` | الطول أو البُعد الأول، متر (صنف تجاري متغير القياس). |
| `312n` | `AI_312n_WIDTH_M` | العرض أو القطر أو البُعد الثاني، متر (صنف تجاري متغير القياس). |
| `313n` | `AI_313n_HEIGHT_M` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، متر (صنف تجاري متغير القياس). |
| `314n` | `AI_314n_AREA_M` | المساحة، متر مربع (صنف تجاري متغير القياس). |
| `315n` | `AI_315n_NET_VOLUME_L` | الحجم الصافي، لتر (صنف تجاري متغير القياس). |
| `316n` | `AI_316n_NET_VOLUME_M` | الحجم الصافي، متر مكعب (صنف تجاري متغير القياس). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | الوزن اللوجستي، كيلوغرام. |
| `331n` | `AI_331n_LENGTH_M_LOG` | الطول أو البُعد الأول، متر. |
| `332n` | `AI_332n_WIDTH_M_LOG` | العرض أو القطر أو البُعد الثاني، متر. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، متر. |
| `334n` | `AI_334n_AREA_M_LOG` | المساحة، متر مربع. |
| `335n` | `AI_335n_VOLUME_L_LOG` | الحجم اللوجستي، لتر. |
| `336n` | `AI_336n_VOLUME_M_LOG` | الحجم اللوجستي، متر مكعب. |
| `337n` | `AI_337n_KG_PER_M` | كيلوغرام لكل متر مربع. |

### الكمية والقياس — قياس متغيّر (الإمبراطوري / الأمريكي)

| العائلة | نمط الثابت (`n` = خانة المنازل العشرية) | الوصف |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | الوزن الصافي، رطل (صنف تجاري متغير القياس). |
| `321n` | `AI_321n_LENGTH_IN` | الطول أو البُعد الأول، بوصة (صنف تجاري متغير القياس). |
| `322n` | `AI_322n_LENGTH_FT` | الطول أو البُعد الأول، قدم (صنف تجاري متغير القياس). |
| `323n` | `AI_323n_LENGTH_YD` | الطول أو البُعد الأول، ياردة (صنف تجاري متغير القياس). |
| `324n` | `AI_324n_WIDTH_IN` | العرض أو القطر أو البُعد الثاني، بوصة (صنف تجاري متغير القياس). |
| `325n` | `AI_325n_WIDTH_FT` | العرض أو القطر أو البُعد الثاني، قدم (صنف تجاري متغير القياس). |
| `326n` | `AI_326n_WIDTH_YD` | العرض أو القطر أو البُعد الثاني، ياردة (صنف تجاري متغير القياس). |
| `327n` | `AI_327n_HEIGHT_IN` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، بوصة (صنف تجاري متغير القياس). |
| `328n` | `AI_328n_HEIGHT_FT` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، قدم (صنف تجاري متغير القياس). |
| `329n` | `AI_329n_HEIGHT_YD` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، ياردة (صنف تجاري متغير القياس). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | الوزن اللوجستي، رطل. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | الطول أو البُعد الأول، بوصة. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | الطول أو البُعد الأول، قدم. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | الطول أو البُعد الأول، ياردة. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | العرض أو القطر أو البُعد الثاني، بوصة. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | العرض أو القطر أو البُعد الثاني، قدم. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | العرض أو القطر أو البُعد الثاني، ياردة. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، بوصة. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، قدم. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | العمق أو السُّمك أو الارتفاع أو البُعد الثالث، ياردة. |
| `350n` | `AI_350n_AREA_IN` | المساحة، بوصة مربعة (صنف تجاري متغير القياس). |
| `351n` | `AI_351n_AREA_FT` | المساحة، قدم مربع (صنف تجاري متغير القياس). |
| `352n` | `AI_352n_AREA_YD` | المساحة، ياردة مربعة (صنف تجاري متغير القياس). |
| `353n` | `AI_353n_AREA_IN_LOG` | المساحة، بوصة مربعة. |
| `354n` | `AI_354n_AREA_FT_LOG` | المساحة، قدم مربع. |
| `355n` | `AI_355n_AREA_YD_LOG` | المساحة، ياردة مربعة. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | الوزن الصافي، أونصة تروي (صنف تجاري متغير القياس). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | الوزن الصافي (أو الحجم)، أونصة (صنف تجاري متغير القياس). |
| `360n` | `AI_360n_NET_VOLUME_QT` | الحجم الصافي، كوارت (صنف تجاري متغير القياس). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | الحجم الصافي، غالون أمريكي (صنف تجاري متغير القياس). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | الحجم اللوجستي، كوارت. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | الحجم اللوجستي، غالون أمريكي. |
| `364n` | `AI_364n_NET_VOLUME_IN` | الحجم الصافي، بوصة مكعبة (صنف تجاري متغير القياس). |
| `365n` | `AI_365n_NET_VOLUME_FT` | الحجم الصافي، قدم مكعب (صنف تجاري متغير القياس). |
| `366n` | `AI_366n_NET_VOLUME_YD` | الحجم الصافي، ياردة مكعبة (صنف تجاري متغير القياس). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | الحجم اللوجستي، بوصة مكعبة. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | الحجم اللوجستي، قدم مكعب. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | الحجم اللوجستي، ياردة مكعبة. |

### التسعير والمبالغ النقدية

الخانة الرابعة (`n`) ترمّز عدد المنازل العشرية الضمنية. ومداها المسموح
يختلف بحسب العائلة — انظر عمود `n`.

| العائلة | نمط الثابت (`n` = خانة المنازل العشرية) | `n` | الوصف |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | المبلغ المستحق الدفع أو قيمة القسيمة، بالعملة المحلية. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | المبلغ المستحق الدفع برمز العملة وفق ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | المبلغ المستحق الدفع، منطقة نقدية واحدة (صنف تجاري متغير القياس). |
| `393n` | `AI_393n_PRICE` | 0–9 | المبلغ المستحق الدفع برمز العملة وفق ISO (صنف تجاري متغير القياس). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | نسبة الخصم للقسيمة. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | المبلغ المستحق الدفع لكل وحدة قياس، منطقة نقدية واحدة (صنف تجاري متغير القياس). |

### الموقع والشحن

| AI | الثابت | الوصف |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | رقم أمر الشراء الخاص بالعميل. |
| `401` | `AI_401_GINC` | رقم التعريف العالمي للشحنة (GINC). |
| `402` | `AI_402_GSIN` | رقم تعريف الشحنة العالمي (GSIN). |
| `403` | `AI_403_ROUTE` | رمز التوجيه. |
| `410` | `AI_410_SHIP_TO_LOC` | رقم الموقع العالمي (GLN) للشحن إلى / التسليم إلى. |
| `411` | `AI_411_BILL_TO` | رقم الموقع العالمي (GLN) للفوترة إلى. |
| `412` | `AI_412_PURCHASE_FROM` | رقم الموقع العالمي (GLN) للشراء من. |
| `413` | `AI_413_SHIP_FOR_LOC` | رقم الموقع العالمي (GLN) للشحن من أجل / التسليم من أجل - إعادة التوجيه إلى. |
| `414` | `AI_414_LOC_NO` | تعريف الموقع الفعلي - رقم الموقع العالمي (GLN). |
| `415` | `AI_415_PAY_TO` | رقم الموقع العالمي (GLN) للجهة المصدرة للفاتورة. |
| `416` | `AI_416_PROD_SERV_LOC` | رقم الموقع العالمي (GLN) لموقع الإنتاج أو الخدمة. |
| `417` | `AI_417_PARTY` | رقم الموقع العالمي (GLN) للطرف. |
| `420` | `AI_420_SHIP_TO_POST` | الرمز البريدي للشحن إلى / التسليم إلى ضمن هيئة بريدية واحدة. |
| `421` | `AI_421_SHIP_TO_POST` | الرمز البريدي للشحن إلى / التسليم إلى مع رمز الدولة وفق ISO. |
| `422` | `AI_422_ORIGIN` | بلد منشأ الصنف التجاري. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | بلد المعالجة الأولية. |
| `424` | `AI_424_COUNTRY_PROCESS` | بلد المعالجة. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | بلد التفكيك. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | البلد المُغطّي لسلسلة العمليات الكاملة. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | التقسيم الفرعي لبلد المنشأ. |
| `4300` | `AI_4300_SHIP_TO_COMP` | اسم شركة الشحن إلى / التسليم إلى. |
| `4301` | `AI_4301_SHIP_TO_NAME` | جهة اتصال الشحن إلى / التسليم إلى. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | سطر عنوان 1 للشحن إلى / التسليم إلى. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | سطر عنوان 2 للشحن إلى / التسليم إلى. |
| `4304` | `AI_4304_SHIP_TO_SUB` | ضاحية الشحن إلى / التسليم إلى. |
| `4305` | `AI_4305_SHIP_TO_LOC` | منطقة الشحن إلى / التسليم إلى. |
| `4306` | `AI_4306_SHIP_TO_REG` | إقليم الشحن إلى / التسليم إلى. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | رمز بلد الشحن إلى / التسليم إلى. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | رقم هاتف الشحن إلى / التسليم إلى. |
| `4309` | `AI_4309_SHIP_TO_GEO` | الموقع الجغرافي (GEO) للشحن إلى / التسليم إلى. |
| `4310` | `AI_4310_RTN_TO_COMP` | اسم شركة الإرجاع إلى. |
| `4311` | `AI_4311_RTN_TO_NAME` | جهة اتصال الإرجاع إلى. |
| `4312` | `AI_4312_RTN_TO_ADD1` | سطر عنوان 1 للإرجاع إلى. |
| `4313` | `AI_4313_RTN_TO_ADD2` | سطر عنوان 2 للإرجاع إلى. |
| `4314` | `AI_4314_RTN_TO_SUB` | ضاحية الإرجاع إلى. |
| `4315` | `AI_4315_RTN_TO_LOC` | منطقة الإرجاع إلى. |
| `4316` | `AI_4316_RTN_TO_REG` | إقليم الإرجاع إلى. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | رمز بلد الإرجاع إلى. |
| `4318` | `AI_4318_RTN_TO_POST` | الرمز البريدي للإرجاع إلى. |
| `4319` | `AI_4319_RTN_TO_PHONE` | رقم هاتف الإرجاع إلى. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | وصف رمز الخدمة. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | علامة البضائع الخطرة. |
| `4322` | `AI_4322_AUTH_LEAVE` | إذن الترك دون توقيع. |
| `4323` | `AI_4323_SIG_REQUIRED` | علامة طلب التوقيع. |
| `4330` | `AI_4330_MAX_TEMP_F` | الحد الأقصى لدرجة الحرارة بالفهرنهايت (معبّرًا عنها بأجزاء من مئة الدرجة). |
| `4331` | `AI_4331_MAX_TEMP_C` | الحد الأقصى لدرجة الحرارة بالمئوية (معبّرًا عنها بأجزاء من مئة الدرجة). |
| `4332` | `AI_4332_MIN_TEMP_F` | الحد الأدنى لدرجة الحرارة بالفهرنهايت (معبّرًا عنها بأجزاء من مئة الدرجة). |
| `4333` | `AI_4333_MIN_TEMP_C` | الحد الأدنى لدرجة الحرارة بالمئوية (معبّرًا عنها بأجزاء من مئة الدرجة). |

### سمات المنتج والتتبّع

| AI | الثابت | الوصف |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | رقم مخزون الناتو (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | تصنيف UN/ECE لذبائح وقطع اللحوم. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | الفعالية الفاعلة. |
| `7005` | `AI_7005_CATCH_AREA` | منطقة الصيد. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | الأنواع لأغراض مصايد الأسماك. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | نوع أداة الصيد. |
| `7010` | `AI_7010_PROD_METHOD` | طريقة الإنتاج. |
| `7020` | `AI_7020_REFURB_LOT` | معرّف دفعة إعادة التجديد. |
| `7021` | `AI_7021_FUNC_STAT` | الحالة الوظيفية. |
| `7022` | `AI_7022_REV_STAT` | حالة المراجعة. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | معرّف الأصل الفردي العالمي (GIAI) للتجميع. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | رقم المُصنِّع مع رمز بلد ISO من ثلاث خانات (10 خانات متاحة). |
| `7040` | `AI_7040_UIC_EXT` | UIC الخاص بـ GS1 مع الامتداد 1 ومؤشر المستورد. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | نوع وحدة الشحن وفق UN/CEFACT. |

### أرقام السداد الصحية الوطنية (NHRN)

| AI | الثابت | الوصف |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - ألمانيا PZN. |
| `711` | `AI_711_NHRN_CIP` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - فرنسا CIP. |
| `712` | `AI_712_NHRN_CN` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - إسبانيا CN. |
| `713` | `AI_713_NHRN_DRN` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - البرازيل DRN. |
| `714` | `AI_714_NHRN_AIM` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - البرتغال AIM. |
| `715` | `AI_715_NHRN_NDC` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - الولايات المتحدة الأمريكية NDC. |
| `716` | `AI_716_NHRN_AIC` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - إيطاليا AIC. |
| `717` | `AI_717_NHRN_SRN` | الرقم الوطني لتعويضات الرعاية الصحية (NHRN) - رقم السجل الصحي لكوستاريكا. |

### الرعاية الصحية وGMN وHIDRI وCPID وبيانات الأشخاص

| AI | الثابت | الوصف |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | مرجع الشهادة (10 خانات متاحة). |
| `7240` | `AI_7240_PROTOCOL` | معرّف البروتوكول. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | نوع وسائط AIDC. |
| `7242` | `AI_7242_VCN` | رقم التحكم بالإصدار (VCN). |
| `7250` | `AI_7250_DOB` | تاريخ الميلاد (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | تاريخ ووقت الميلاد (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | الجنس البيولوجي. |
| `7253` | `AI_7253_FAMILY_NAME` | اسم العائلة للشخص. |
| `7254` | `AI_7254_GIVEN_NAME` | الاسم الأول للشخص. |
| `7255` | `AI_7255_SUFFIX` | لاحقة اسم الشخص. |
| `7256` | `AI_7256_FULL_NAME` | الاسم الكامل للشخص. |
| `7257` | `AI_7257_PERSON_ADDR` | عنوان الشخص. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | تسلسل ولادة الطفل. |
| `7259` | `AI_7259_BABY` | طفل يحمل اسم العائلة. |
| `8001` | `AI_8001_DIMENSIONS` | المنتجات الملفوفة (العرض، الطول، قطر اللب، الاتجاه، عدد الوصلات). |
| `8002` | `AI_8002_CMT_NO` | معرّف الهاتف المحمول الخلوي. |
| `8003` | `AI_8003_GRAI` | معرّف الأصل القابل للإرجاع العالمي (GRAI). |
| `8004` | `AI_8004_GIAI` | معرّف الأصل الفردي العالمي (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | السعر لكل وحدة قياس. |
| `8006` | `AI_8006_ITIP` | تعريف قطعة فردية من الصنف التجاري (ITIP). |
| `8007` | `AI_8007_IBAN` | رقم الحساب المصرفي الدولي (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | تاريخ ووقت الإنتاج (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | مؤشر المستشعر القابل للقراءة ضوئيًا. |
| `8010` | `AI_8010_CPID` | معرّف المكوّن/القطعة (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | الرقم التسلسلي لمعرّف المكوّن/القطعة (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | إصدار البرمجيات. |
| `8013` | `AI_8013_GMN` | رقم الطراز العالمي (GMN). |
| `8014` | `AI_8014_MUDI` | معرّف تسجيل الجهاز الفردي المتقدم (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | رقم علاقة الخدمة العالمي (GSRN) لتحديد العلاقة بين المؤسسة المقدّمة للخدمات ومزوّد الخدمات. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | رقم علاقة الخدمة العالمي (GSRN) لتحديد العلاقة بين المؤسسة المقدّمة للخدمات ومتلقّي الخدمات. |
| `8019` | `AI_8019_SRIN` | رقم مثيل علاقة الخدمة (SRIN). |
| `8020` | `AI_8020_REF_NO` | الرقم المرجعي لقسيمة الدفع. |
| `8026` | `AI_8026_ITIP_CONTENT` | تعريف قطع الصنف التجاري (ITIP) الموجودة ضمن الوحدة اللوجستية. |
| `8030` | `AI_8030_DIGSIG` | التوقيع الرقمي (DigSig). |
| `8040` | `AI_8040_IMEI` | الهوية الدولية لمعدات الجوال (IMEI). |
| `8041` | `AI_8041_IMEI2` | الهوية الدولية لمعدات الجوال 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | رقم شريحة SIM المدمجة. |
| `8043` | `AI_8043_PSIM` | رقم شريحة SIM المادية. |
| `8110` | `AI_8110` | تعريف رمز القسيمة للاستخدام في أمريكا الشمالية. |
| `8111` | `AI_8111_POINTS` | نقاط الولاء للقسيمة. |
| `8112` | `AI_8112` | تعريف رمز قسيمة ملف العروض الإيجابية للاستخدام في أمريكا الشمالية. |
| `8200` | `AI_8200_PRODUCT_URL` | عنوان URL للتعبئة الموسّعة. |

### الاستخدام الداخلي / استخدام الشركة

| AI | الثابت | الوصف |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | معلومات متفق عليها بين الشركاء التجاريين. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | معلومات داخلية للشركة (9 خانات متاحة). |

---

## الملحق ب — ثوابت مفاتيح التفسير

حين يُستدعى `GaiaParser.parse()` بالوضع `ParseMode.INTERPRETATION`، قد يحمل كل `GS1AIObjectElement` قائمة من كائنات `GS1AIInterpretation` تنتجها مُثريات متخصّصة بالمجال. استخدم ثوابت `GS1Constants_Enricher` (في الحزمة `tools.pantheum.gaia.gs1.constants`) مفاتيحَ للبحث عن قيم تفسير بعينها:

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

تسميات العرض **ليست** ثوابت — فهي في الفهارس المُترجَمة تحت `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`، ومفتاحها ثابت النوع. وتعيد `GS1AIInterpretation.getLabel()` التسمية بلغة التحليل (انظر [الرسائل والتسميات المُترجَمة](#الرسائل-والتسميات-المترجمة))، وترجع إلى الإنجليزية حين يُغفل فهرسٌ ما المفتاحَ. وعمود «تسمية العرض» أدناه يسرد النص العربي؛ أما مفاتيح الأنواع نفسها فثابتة عبر اللغات، فطابِق على المفتاح لا على التسمية أبدًا.

### التاريخ والوقت

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `DATE_VALUE` | التاريخ | معرّفات التواريخ (11–17، 7003، 7006، 7011، وغيرها) |
| `DATE_FORMAT` | تنسيق التاريخ | معرّفات التواريخ |
| `TIME_VALUE` | الوقت | المعرّفات الحاملة للوقت (7003، 7011، 8008، وغيرها) |
| `TIME_FORMAT` | تنسيق الوقت | المعرّفات الحاملة للوقت |
| `DATETIME_VALUE` | التاريخ والوقت | معرّفات التاريخ والوقت |
| `DATETIME_FORMAT` | تنسيق التاريخ والوقت | معرّفات التاريخ والوقت |

### تاريخ الحصاد

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | تاريخ بدء الحصاد | AI 7007 |
| `HARVEST_END_DATE` | تاريخ انتهاء الحصاد | AI 7007 (نهاية المدى الاختيارية) |
| `HARVEST_DATE_RANGE` | نطاق تاريخ الحصاد | AI 7007 |

### بادئة شركة GS1

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | بادئة شركة GS1 | معرّفات GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | رمز عضو GS1 | معرّفات GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | منظمة عضو GS1 | معرّفات GTIN / GLN / SSCC |

### GTIN

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `GTIN_TYPE` | نوع GTIN | AI 01، 02 |
| `GTIN_NATIVE` | GTIN | AI 01، 02 |
| `PACKAGING_LEVEL` | مستوى التعبئة | AI 01 |
| `GTIN_CHECK_DIGIT` | رقم التحقق | AI 01، 02 |

### SSCC

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | رقم الامتداد | AI 00 |
| `SSCC_SERIAL_REFERENCE` | المرجع التسلسلي | AI 00 |
| `SSCC_CHECK_DIGIT` | رقم التحقق | AI 00 |

### البلد (ISO 3166)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | رمز البلد (رقمي) | معرّفات البلد الواحد (422، 424–426، 4307، 4317، 421، 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | رمز البلد (حرفي-2) | معرّفات البلدان برمز من حرفين |
| `COUNTRY_NAME` | اسم البلد | معرّفات البلد الواحد |
| `COUNTRY_LIST` | البلدان | AI 423 — كل الأسماء موصولة، مثل `Australia, New Zealand` |

المعرّف 423 (بلد المعالجة الأولى) يمكن أن يحمل حتى خمسة بلدان، فيُخرج
**زوجًا مرقّمًا لكل بلد** — `COUNTRY_CODE_NUMERIC_1` و`COUNTRY_NAME_1`
و`COUNTRY_CODE_NUMERIC_2` و`COUNTRY_NAME_2` … — يتلوها ملخّص `COUNTRY_LIST`
الواحد. ابنِ هذه المفاتيح من الثابتين `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` مع الترتيب بدءًا من 1، أو تنقّل ببساطة في
`getInterpretations()`؛ فالمفتاحان `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` بلا لاحقة
**لا يُخرَجان** للمعرّف 423.

### العملة (ISO 4217)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | رمز العملة | معرّفات المبالغ مع العملة (391n، 393n) |
| `CURRENCY_ALPHA` | رمز العملة الحرفي | معرّفات المبالغ مع العملة |
| `CURRENCY_NAME` | اسم العملة | معرّفات المبالغ مع العملة |

### درجة الحرارة

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `TEMPERATURE` | درجة الحرارة | AI 4330–4333 |
| `TEMPERATURE_UNIT` | وحدة درجة الحرارة | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | درجة الحرارة (منسّقة) | AI 4330–4333 |

### الجنس (ISO 5218)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `SEX_CODE` | رمز الجنس | AI 7252 |
| `SEX_DESCRIPTION` | وصف الجنس | AI 7252 |

### الأنواع المائية (FAO)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `SPECIES_CODE` | رمز النوع | AI 7008 |
| `SPECIES_SCIENTIFIC` | الاسم العلمي | AI 7008 |
| `SPECIES_ENGLISH` | الاسم الشائع | AI 7008 |
| `SPECIES_FAMILY` | الفصيلة | AI 7008 |
| `SPECIES_ORDER` | الرتبة | AI 7008 |

### رقم مخزون الناتو (NSN)

| ثابت المفتاح | تسمية العرض | ينتجها |
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

### منتجات اللفائف

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | عرض اللفة (mm) | AI 8001 |
| `ROLL_LENGTH` | طول اللفة (m) | AI 8001 |
| `CORE_DIAMETER` | قطر اللب (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | رمز اتجاه اللف | AI 8001 |
| `WINDING_DIRECTION` | اتجاه اللف | AI 8001 |
| `SPLICES` | الوصلات | AI 8001 |

### IBAN

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | رمز البلد | AI 8007 |
| `IBAN_COUNTRY_NAME` | البلد | AI 8007 |
| `IBAN_CHECK_DIGITS` | أرقام التحقق | AI 8007 |
| `IBAN_CHECK_VALID` | التحقق | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040، 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040، 8041 |
| `IMEI_SERIAL` | الرقم التسلسلي | AI 8040، 8041 |
| `IMEI_CHECK_DIGIT` | رقم التحقق | AI 8040، 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040، 8041 |
| `IMEI_RBI_NAME` | جهة الإصدار | AI 8040، 8041 |

تتفكّك الخانات الخمس عشرة إلى `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`، وتكون
RBI هي الخانتين الأوليين من TAC — فـ `IMEI_RBI` بادئة لـ `IMEI_TAC` لا مقطعًا مستقلًا.
ويعرض `IMEI_FORMATTED` تجميع العرض القياسي لدى GSMA `AA-BBBBBB-CCCCCC-D` (مثل
`49-015420-323751-8`)، وهو يقسم TAC عند حدّ RBI؛ أما التجميع القديم `6-2-6-1`، الذي
يقطع حيث كان يبدأ رمز التجميع النهائي الملغى، فلا يُخرَج.

ويحلّ `IMEI_RBI_NAME` الـ RBI إلى اسم الجهة المخصِّصة عبر `ImeiRbiData`، ويُلحق
**أخيرًا وفقط حين يكون الرمز مدرجًا هناك**. ويغطي ذلك الجدول ثلاث مجموعات:

- **جهات تخصّص حاليًا** — `01` CTIA/PTCRB، و`35` TÜV SÜD BABT، و`86` TAF، إضافة إلى `99`
  Global Hexadecimal Administrator و`98` (محجوز).
- **مديات اختبارية** — `00` و`02`–`09`، وتدلّ على أرقام IMEI اختبارية لا على تخصيص حقيقي.
  استعلم عنها بـ `ImeiRbiData.isTestCode(code)`.
- **جهات لم تعد تخصّص** — جهات تاريخية مثل `49` (BZT/BAPT، ألمانيا) و`44`
  (BABT، المملكة المتحدة) و`91` (MSAI، الهند). استعلم عنها بـ `ImeiRbiData.isNoLongerAllocating(code)`.
  والأجهزة الحاملة لهذه الرموز عادية وما زالت في الخدمة؛ فالتخصيص الجديد وحده هو الذي
  توقّف، ومن ثمّ فهذه معلومة للإبلاغ لا إشارةَ صلاحية البتة.

وغياب `IMEI_RBI_NAME` يعني «هذا الـ RBI ليس في جدولنا»، **لا** «رقم IMEI غير صالح»:
فالجدول مُجمَّع من قائمة RBI منشورة لا من GSMA مباشرة، فقد يتأخر عن الجهات المعيَّنة
حديثًا. لا تستنتج أي حكم تحقّق من غيابه؛ فالـ RBI ليس حرف تحقق. وعلى الشيفرة التي
تتنقّل في قائمة التفسيرات أن تحتمل غيابه بدل الفهرسة بالموضع.

### معرّفات SIM (EID / ICCID)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042، 8043 |
| `SIM_MII_NAME` | فئة الصناعة | AI 8042 |
| `EID_BODY` | متن EID | AI 8042 |
| `EID_CHECK_DIGIT` | رقم التحقق | AI 8042 |
| `ICCID_BODY` | متن ICCID | AI 8043 |
| `ICCID_EXTENSION` | الامتداد | AI 8043 |

يحمل `SIM_MII` الخانتين **الأوليين** (`89`)، وهما الزوج الذي يخصّصه ITU-T E.118
للاتصالات. أما ISO/IEC 7812 نفسه فيعرّف MII بأنها **الخانة الأولى وحدها**، ولذا
يحلّ `SIM_MII_NAME` الفئةَ من الخانة `8` الأولى عبر `Iso7812Data` — فينتج
"Healthcare, telecommunications and other future industry assignments". ومن ثمّ فهو ثابت
لأي EID سليم البنية؛ ويُبلَّغ عنه للتتبّع إلى المعيار لا بوصفه مميِّزًا.
وتأخذ `Iso7812Data.nameForCode(digit)` خانة مجرّدة، بينما تقبل
`nameForIdentifier(prefix)` بادئة أطول وتقرأ خانتها الأولى.

ويُخرِج `SIM_MII_NAME` مُثري `EidEnricher` (المعرّف 8042) وحده. أما `IccidEnricher`
(المعرّف 8043) فيُظهر `SIM_MII` بلا الفئة.

### مرجع الشهادة

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | الرقم التسلسلي | AI 7230–7239 |
| `CERT_SCHEME_CODE` | رمز نظام الاعتماد | AI 7230–7239 |
| `CERT_SCHEME_NAME` | نظام الاعتماد | AI 7230–7239 |
| `CERT_REFERENCE` | مرجع الشهادة | AI 7230–7239 |

### GS1 UIC

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `UIC_CODE` | رمز UIC | AI 7040 |
| `UIC_EXTENSION_1` | الامتداد 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | مؤشر المستورد | AI 7040 |

### تسلسل ولادة الرضيع

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | موضع الولادة | AI 7258 |
| `BIRTH_TOTAL` | إجمالي الولادات | AI 7258 |
| `BIRTH_SEQUENCE` | تسلسل الولادة | AI 7258 |

### رقم الطراز العالمي (GMN)

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | مرجع الطراز | AI 8013 |
| `GMN_CHECK_PAIR` | زوج التحقق | AI 8013 |

### HIDRI

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | مرجع الجهاز | AI 8014 |
| `HIDRI_CHECK_PAIR` | زوج التحقق | AI 8014 |

### CPID

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | مرجع المكوّن والجزء | AI 8010–8011 |

### القيم العشرية وقيم القياس

| ثابت المفتاح | تسمية العرض | ينتجها |
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

### الإحداثيات الجغرافية

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `LATITUDE` | خط العرض | AI 4309 |
| `LONGITUDE` | خط الطول | AI 4309 |
| `GEO_COORDINATES` | الإحداثيات الجغرافية | AI 4309 |
| `LATITUDE_DMS` | خط العرض (DMS) | AI 4309 |
| `LONGITUDE_DMS` | خط الطول (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | الإحداثيات الجغرافية (DMS) | AI 4309 |

### طريقة الإنتاج

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | رمز طريقة الإنتاج | AI 7010 |
| `PRODUCTION_METHOD` | طريقة الإنتاج | AI 7010 |

### نوع وسيط AIDC

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | رمز نوع وسيط AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | نوع وسيط AIDC | AI 7241 |

### القطعة من الإجمالي

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | رقم القطعة | AI 8006 |
| `PIECE_TOTAL` | إجمالي القطع | AI 8006 |
| `PIECE_OF_TOTAL` | القطعة من الإجمالي | AI 8006 |

### تقسيمات المكوّنات

مفاتيح تُخرجها تقسيماتُ المكوّنات التصريحية في `content/ai-content.json` لا مُثرٍ مكتوب
بلغة Java — وهي تُظهر الأجزاء المسمّاة من قيمة معرّف مركّبة. وخلافًا لكل مفتاح آخر في
هذا الملحق، **ليس لهذه ثوابت في `GS1Constants_Enricher`**: طابِق النص الحرفي، أو اقرأ
النوع من `GS1AIInterpretation.getType()`.

| مفتاح النوع | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | رقم التحقق | AI 253، 255، 402، 410–417، 8003، 8017، 8018 |
| `SERIAL_NUMBER` | الرقم التسلسلي | AI 253، 255، 8003 |
| `POSTAL_CODE` | الرمز البريدي | AI 421 |
| `PROCESSOR_ID` | معرّف المُعالِج | AI 7030–7039 |

ولاحظ أن `CHECK_DIGIT` هنا هو مفتاح تقسيم المكوّنات العام، وهو متمايز عن المفاتيح
الخاصة بالمُثريات `GTIN_CHECK_DIGIT` و`SSCC_CHECK_DIGIT` و`IMEI_CHECK_DIGIT`
و`EID_CHECK_DIGIT` المذكورة أعلاه.

### متفرّقات

| ثابت المفتاح | تسمية العرض | ينتجها |
|--------------|---------------|-------------|
| `FLAG_VALUE` | القيمة | معرّفات منطقية / علَمية (4321–4323) |
| `DECODED_TEXT` | نص مفكوك | معرّفات النص الحر |
