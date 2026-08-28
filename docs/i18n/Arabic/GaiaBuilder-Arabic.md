# GaiaBuilder — دليل المطوّر

## المحتويات

1. [نظرة عامة](#نظرة-عامة)
2. [عن GS1 والمواصفات العامة](#عن-gs1-والمواصفات-العامة)
3. [بداية سريعة](#بداية-سريعة)
4. [كيف يعمل](#كيف-يعمل)
5. [بناء سلاسل العناصر](#بناء-سلاسل-العناصر)
   - [معرّفات السمات تحتاج إلى مفتاح تعريفها](#معرفات-السمات-تحتاج-إلى-مفتاح-تعريفها)
6. [بناء عناوين Digital Link](#بناء-عناوين-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [التحقق والأخطاء](#التحقق-والأخطاء)
   - [دوال الإنشاء التي ترمي استثناءات](#دوال-الإنشاء-التي-ترمي-استثناءات)
   - [دوال tryBuild\* التي لا ترمي استثناءات](#دوال-trybuild-التي-لا-ترمي-استثناءات)
   - [لغة رسائل الأخطاء](#لغة-رسائل-الأخطاء)
   - [BuildResult](#buildresult)
9. [أرقام التحقق](#أرقام-التحقق)
10. [الأمان مع الخيوط المتعددة](#الأمان-مع-الخيوط-المتعددة)
11. [مرجع الواجهة البرمجية](#مرجع-الواجهة-البرمجية)

---

## نظرة عامة

`GaiaBuilder` هو نظير [`GaiaParser`](GaiaParser-Arabic.md) المعاكس: فهو يحوّل مجموعة من أزواج معرّف التطبيق (AI) وقيمته إلى **سلسلة عناصر** GS1 سليمة البنية أو **عنوان GS1 Digital Link**. أنت تزوّده بالمعرّفات وقيم بياناتها الكاملة؛ فيجمّعها المُنشئ، ويتحقّق من الناتج بالمحرّك نفسه الذي يستخدمه `GaiaParser`، ثم يعرض المخرجات.

ولأن المُنشئ يتحقّق بـ*تحليل ناتجه المرشَّح بنفسه*، فإن كل ما يعيده مضمونٌ أن يُحلَّل بلا أخطاء عبر `GaiaParser` — فلا يمكن أن يختلف الاثنان أبدًا على ما هو سليم البنية.

**صنف نقطة الدخول:** `tools.pantheum.gaia.GaiaBuilder`

---

## عن GS1 والمواصفات العامة

**GS1** منظمة عالمية غير ربحية تضع المعايير المفتوحة للتعريف وتبادل البيانات في سلاسل الإمداد وتتولّى صيانتها. تُستخدم معاييرها في التجزئة والرعاية الصحية والخدمات اللوجستية وخدمات الأغذية وصناعات كثيرة غيرها، وتغطي كل شيء من الباركود على أغلفة المنتجات الاستهلاكية إلى التتبّع التسلسلي لجرعات الأدوية.

المرجع المعتمد لكل ما يطبّقه هذا المُنشئ هو **المواصفات العامة لـ GS1** — وثيقة واحدة تحدّد:

- جميع رموز معرّفات التطبيق (AI) وعناوين بياناتها وتنسيقاتها وقواعد التحقق منها
- قواعد بناء سلاسل عناصر معرّفات التطبيق وترميزها
- متطلبات أنظمة ترميز الباركود وتخصيص معرّفات ترميز AIM
- خوارزميات رقم التحقق وحرف التحقق
- تفسير السنة ذات الخانتين (قاعدة النافذة المنزلقة)
- مواصفات Data Matrix وQR Code وGS1-128 وGS1 DataBar وغيرها من نواقل البيانات

تُحدَّث المواصفات العامة لـ GS1 سنويًا. الإصدار الحالي والموارد المساندة متاحة على:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

يطبّق GAIA **الإصدار 26.0 (المعتمد، يناير 2026)** من المواصفات العامة لـ GS1.

تحكم عناوين GS1 Digital Link معيارٌ رفيق هو **GS1 Digital Link: URI Syntax**، وهو الذي يحدّد مفاتيح التعريف الأساسية، وترتيب مُقيِّدات المفاتيح، وترميز سمات البيانات الذي يطبّقه المُنشئ عند عرض عناوين Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

يطبّق GAIA **الإصدار 1.7.0 (المعتمد، أغسطس 2026)** من معيار GS1 Digital Link: URI Syntax.

الإحالات إلى الأقسام في هذه الوثيقة تشير إلى المواصفات العامة لـ GS1 (مثل «Table 7-5» و«section 7.12»)، باستثناء أرقام أقسام Digital Link (مثل «§4.9» و«§4.12») فهي تشير إلى معيار GS1 Digital Link: URI Syntax.

---

## بداية سريعة

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

فضّل ثوابت `GS1Constants_AICodes` على نصوص المعرّفات الخام (انظر [الملحق أ في دليل المحلّل](GaiaParser-Arabic.md#الملحق-أ--ثوابت-نصوص-المعرفات)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## كيف يعمل

كل عملية إنشاء تسلك المسار نفسه:

1. **التجميع** — تُوصَل أزواج المعرّف/القيمة في سلسلة عناصر مرشَّحة. ويُدرَج فاصل مجموعة FNC1 (`0x1D`) بعد كل معرّف *يستلزم فاصلًا* ولا يكون العنصر الأخير. أما المعرّفات ذات الطول المحدّد سلفًا (GTIN والتواريخ والقياسات ثابتة الطول) فلا تأخذ فاصلًا؛ وسواها يأخذه. (المعرّفات غير المعروفة لا تبلغ هذه الخطوة أصلًا — إذ يرفضها `ai(...)` فورًا؛ انظر [بناء سلاسل العناصر](#بناء-سلاسل-العناصر).)
2. **التحقق** — يُحلَّل المرشَّح في وضع `CONTENT` عبر `GaiaParser`. وتُفحص كل قيمة مقابل تنسيق معرّفها ورقم تحققه، وتُفرض القواعد البنيوية (الاقترانات المطلوبة والممنوعة بين المعرّفات). فإن لم يكن التحليل صالحًا، أخفق الإنشاء.
3. **العرض** —
   - لسلسلة العناصر، تُعاد `toElementString()` للكائن الذي جرى التحقق منه.
   - لـ Digital Link، يُسنَد لكل عنصر دوره في DL (مفتاح أساسي، أو مُقيِّد مفتاح، أو سمة بيانات)، ويُتحقَّق من تسلسل مُقيِّدات المفاتيح، ويُخرَج العنوان، ثم **يُعاد تحليل العنوان المُخرَج للتأكد من أنه يعود صحيحًا بوصفه Digital Link صالحًا** — وهو فحص وقائي لخطوة تجميع النص والترميز بعلامة النسبة المئوية. فإن لم يعُد كما هو، رُمي `GaiaBuilderException`.

وهذا يعكس منطق إعادة البناء في `DLSyntaxParser`، فيكون موضع الفواصل والتحقق مطابقين تمامًا لما يتوقعه المحلّل.

---

## بناء سلاسل العناصر

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- يُتحقَّق من **المعرّف** فورًا: إذ ترمي `ai(...)` استثناء `IllegalArgumentException` إن لم يكن معرّف تطبيق GS1 معروفًا. (فالمُنشئ يصل المعرّف بالقيمة قبل التحليل، فلا بدّ من التقاط معرّف مجهول أو مفرط الطول مثل `"99999"` هنا — وإلا أُعيد تقطيعه في صمت إلى معرّف مختلف.) أما **القيمة** فيُتحقَّق منها لاحقًا، وقت الإنشاء.
- يجب أن تكون القيم **كاملة**، بما فيها أي رقم تحقق. فالمُنشئ لا يحسب أرقام التحقق ولا يُلحقها نيابةً عنك — انظر [أرقام التحقق](#أرقام-التحقق).
- تُخرَج المعرّفات بالترتيب الذي تضيفها به. ويُدرج المُنشئ فواصل FNC1 حيث تستلزمها بنية GS1؛ فلا تضف الفواصل بنفسك.
- الإنشاء **بلا أي معرّفات** يرمي `GaiaBuilderException("No AIs supplied")` بقائمة `getErrors()` فارغة — وهو الإخفاق الوحيد الذي لا يحمل أي `GaiaError`.
- ومعرّفٌ تُخفق قيمته في قاعدة تنسيقه أو رقم تحققه يجعل الإنشاء يُخفق.

### معرّفات السمات تحتاج إلى مفتاح تعريفها

أغلب المعرّفات *سمات* تستلزم المواصفات العامة لـ GS1 أن تصحب مفتاح تعريف، والمُنشئ يفرض ذلك — إذ يتحقّق عبر مرحلة البنية النحوية كاملة، بلا سبيل إلى الخروج عنها. فرقم دفعة/تشغيلة أو رقم تسلسلي بمفرده **ليس** سلسلة عناصر صالحة:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

أما مفاتيح التعريف (GTIN `01`، وSSCC `00`، وGLN `414`، …) والمعرّفات الداخلية للشركات (`90`–`99`) فتقوم بذاتها بوجه مشروع تمامًا. وكل ما عداها يحتاج إلى مرافقه.

> يمكن إخبار `GaiaParser` بتخطّي هذا الفحص عبر `ParseConfig.skipRequiresCheck(true)`؛ أما `GaiaBuilder` فلا يعرض ما يعادله عمدًا — إذ غايته إخراج ناتج مطابق للمعايير. ولتجميع سلسلة عناصر ناقصة عمدًا، صِلها بنفسك وحلّلها والفحص معطَّل.

---

## بناء عناوين Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

يستلزم Digital Link الصالح مفتاح **تعريف أساسيًا** واحدًا بالضبط (مثل GTIN `01`، وGLN `414`، وSSCC `00`). ويصنّف المُنشئ كل معرّف تزوّده به:

<div dir="rtl">

| الدور | كيف يُعرَض | مثال |
|------|-------------|---------|
| مفتاح التعريف الأساسي | مقطع مسار بعد النطاق/البادئة | `/01/09506000134352` |
| مُقيِّد المفتاح (CPV `22`، الدفعة `10`، التسلسلي `21`، …) | مقاطع المسار التالية، **بالترتيب المعياري في §4.9** (لا بترتيب إضافتك لها) | `/10/LOT-ABC` |
| سمة البيانات (كل ما عدا ذلك) | معاملات استعلام، **مرتّبة معجميًا بمفتاح المعرّف** (§4.12) | `?17=271231` |

</div>

ولأن المُقيِّدات يُعاد ترتيبها عند الإخراج، فلا ضير في تزويدها خارج التسلسل — فـ `ai("21", …)` قبل `ai("10", …)` تُعرَض مع ذلك `/10/LOT/21/SER`. وما يجب أن يكون مقبولًا للمفتاح الأساسي هو *المجموعة* وحدها.

والقيم في المسار والاستعلام كليهما مرمّزة بعلامة النسبة المئوية.

ويُخفق الإنشاء (فيرمي `GaiaBuilderException`، أو يعيد `BuildResult` مُخفقًا) حين:

- **لا** يوجد مفتاح تعريف أساسي بين المعرّفات؛
- يوجد **أكثر من** مفتاح تعريف أساسي واحد؛
- يكون أحد المعرّفات **ممنوعًا** في Digital Link (`03`، `8014`)؛
- يكون **تسلسل مُقيِّدات المفاتيح** غير صالح للمفتاح الأساسي المختار (كمُقيِّد لا يتبع ذلك المفتاح، أو مُقيِّدات خارج ترتيبها المسموح).

---

## BuilderDigitalLinkConfig

مرّر `BuilderDigitalLinkConfig` للتحكّم في المخطّط والنطاق وبادئة المسار ومعاملات الاستعلام الإضافية والجزء:

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

<div dir="rtl">

| دالة الباني | الغرض | الافتراضي |
|----------------|---------|---------|
| `scheme(String)` | مخطّط العنوان؛ ويجب أن يكون `http` أو `https` | `https` |
| `domain(String)` | جهة الإسناد — المضيف أو `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | مقاطع المسار قبل أول مفتاح أساسي؛ وتُطبَّع الشرطات المائلة في الطرفين | *(لا شيء)* |
| `baseUrl(String)` | تيسير يقسم عنوانًا إلى `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | معامل استعلام إضافي، يُلحق **بعد** سمات بيانات المعرّفات، بترتيب الإدراج؛ ومرمّز بعلامة النسبة المئوية | — |
| `fragment(String)` | جزء العنوان (بلا `#` في أوله)؛ مرمّز بعلامة النسبة المئوية | *(لا شيء)* |

</div>

وتتحقّق `build()` من الضبط فورًا: فمخطّط ليس `http(s)` أو نطاق فارغ يرمي `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (واسمها المستعار `defaultConfig()`) هي الافتراضي `https://id.gs1.org` بلا إضافات — وهي بالضبط ما تستخدمه `buildDigitalLinkUri()` بلا وسيط، وما تنتجه `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → المخطّط `http`، والنطاق `id.example.org:8080`، وبادئة المسار `/r`.
- ومعاملات الاستعلام الإضافية تأتي دائمًا بعد السمات المستمدّة من المعرّفات، فيُحفظ ترتيب المعرّفات المعياري (§4.12).

و`BuilderDigitalLinkConfig` غير قابل للتغيير؛ فأعد استخدام نسخة واحدة بحرية.

---

## التحقق والأخطاء

### دوال الإنشاء التي ترمي استثناءات

ترمي `buildElementString()` و`buildDigitalLinkUri()` و`buildDigitalLinkUri(BuilderDigitalLinkConfig)` استثناء **`GaiaBuilderException`** (وهو `RuntimeException` غير مُتحقَّق منه) حين يتعذّر على المعرّفات تكوين ناتج سليم البنية:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- في إخفاقات **المحتوى** (رقم تحقق خاطئ، أو عدم تطابق تنسيق، أو معرّف مفقود/ممنوع)، تحمل `getErrors()` كائنات `GaiaError` الخاصة بالمحلّل — وهي الكائنات نفسها [الموثّقة في دليل المحلّل](GaiaParser-Arabic.md#gaiaerror).
- وفي إخفاقات **بنية Digital Link** (غياب المفتاح الأساسي، أو تعدّد المفاتيح الأساسية، أو معرّف ممنوع، أو تسلسل مُقيِّدات مفاتيح غير صالح)، تحمل `getErrors()` كائن `GaiaError` واحدًا (بالرمز `GE-L008` أو `GE-L012` أو `GE-L013` أو `GE-L014`) مُترجَمًا إلى لغة المُنشئ.

### دوال tryBuild\* التي لا ترمي استثناءات

حين يأتي الإدخال من المستخدم ويكون الإخفاق نتيجة متوقّعة يمكن تداركها، استخدم صيغ `tryBuild*` بدل التحكّم في التدفّق بالاستثناءات. فهي تعيد [`BuildResult`](#buildresult) بدل أن ترمي:

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

<div dir="rtl">

| ترمي استثناءً | لا ترمي استثناءً |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

</div>

وتتشارك كل دالة `tryBuild*` نواة التحقق نفسها مع توأمها الرامي للاستثناء؛ ولا يختلف إلا حدّ الإخفاق.

### لغة رسائل الأخطاء

تُستمدّ أخطاء التحقق من المحتوى من فهرس الأخطاء المُترجَم. استدعِ `language(...)` لاختيار لغة رسائل `GaiaError` التي تحملها `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`؛ وافتراضها الإنجليزية:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

وهذا هو إعداد `GaiaConstants.Language` نفسه الذي يقبله `GaiaParser` عبر `ParseConfig`، فيترجم المُنشئ والمحلّل بالطريقة نفسها.

ورسائل `GaiaError` الخاصة بـ**المحتوى** وإخفاقات **بنية Digital Link** (غياب المفتاح الأساسي، أو تعدّد المفاتيح الأساسية، أو معرّف ممنوع، أو تسلسل مُقيِّدات مفاتيح غير صالح) كلاهما مُترجَم عبر فهرس الأخطاء المشترك — والأخيرة تستخدم الرموز `GE-L008` و`GE-L012` و`GE-L013` و`GE-L014`.

### BuildResult

`BuildResult` (في الحزمة `tools.pantheum.gaia.result`) نوع قيمة غير قابل للتغيير يصف نتيجة استدعاء `tryBuild*`:

<div dir="rtl">

| الدالة | عند النجاح | عند الإخفاق |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | النص المعروض | `null` |
| `getMessage()` | `null` | وصف الإخفاق |
| `getErrors()` | قائمة فارغة | أخطاء التحقق (نفسها في `GaiaBuilderException.getErrors()`) |

</div>

---

## أرقام التحقق

يتحقّق المُنشئ من أرقام التحقق لكنه **لا** يحسبها — فيجب أن تتضمّن القيم رقم تحققها أصلًا. ولحساب واحد، استخدم `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

تطبّق `calculateCheckDigit(String)` خوارزمية GS1 القياسية بنظام 10 التقايسي على الخانات المعطاة وتعيد رقم التحقق من `0` إلى `9`، أو `-1` إن كان الإدخال `null` أو فارغًا أو غير رقمي.

---

## الأمان مع الخيوط المتعددة

`GaiaBuilder` **ليس** آمنًا مع الخيوط المتعددة وهو مُعدّ للاستخدام مرة واحدة: استدعِ `create()`، وأضف المعرّفات، وأنشئ مرة واحدة. أنشئ مُنشئًا جديدًا لكل ناتج؛ ولا تشارك واحدًا بين الخيوط.

أما `BuilderDigitalLinkConfig` (ومخرجاته من نوع `BuildResult`) فغير قابلة للتغيير ويجوز مشاركتها بحرية — ابنِ ضبطًا واحدًا عند بدء التشغيل وأعد استخدامه عبر مُنشئات كثيرة.

---

## مرجع الواجهة البرمجية

### `GaiaBuilder`

<div dir="rtl">

| الدالة | الوصف |
|--------|-------------|
| `static GaiaBuilder create()` | يبدأ مُنشئًا جديدًا فارغًا. |
| `GaiaBuilder ai(String ai, String value)` | يُلحق معرّفًا وقيمته الكاملة. ويرمي `IllegalArgumentException` إن كان أيٌّ منهما `null`، أو إن لم يكن `ai` معرّف تطبيق GS1 معروفًا. |
| `GaiaBuilder language(GaiaConstants.Language language)` | يضبط لغة رسائل أخطاء التحقق من المحتوى (والافتراضي الإنجليزية). و`null` تُتجاهَل. |
| `String buildElementString()` | يعرض سلسلة عناصر GS1. ويرمي `GaiaBuilderException` عند الإخفاق. |
| `String buildDigitalLinkUri()` | يعرض عنوان Digital Link معياريًا. ويرمي `GaiaBuilderException` عند الإخفاق. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | يعرض عنوان Digital Link وفق `config`. ويرمي `GaiaBuilderException` عند الإخفاق. |
| `BuildResult tryBuildElementString()` | إنشاء سلسلة عناصر بلا رمي استثناء. |
| `BuildResult tryBuildDigitalLinkUri()` | إنشاء Digital Link معياري بلا رمي استثناء. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | إنشاء Digital Link وفق `config` بلا رمي استثناء. |

</div>

### `BuilderDigitalLinkConfig`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | الافتراضي `https://id.gs1.org`. |
| `static Builder builder()` | بانٍ جديد للضبط. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | المخطّط وجهة الإسناد وبادئة المسار بعد حلّها. |
| `getExtraQueryParams()` | معاملات الاستعلام الإضافية، بترتيب الإدراج. |
| `getFragment()` | الجزء، أو `null`. |

</div>

### `GaiaBuilderException`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `getErrors()` | كائنات `GaiaError` التي سبّبت الإخفاق — أخطاء المحلّل في إخفاق المحتوى، أو خطأ بنيوي واحد في Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). ولا تكون `null` أبدًا. |

</div>

### `BuildResult`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `isSuccess()` | هل نجح الإنشاء. |
| `getValue()` | الناتج المعروض عند النجاح؛ و`null` عند الإخفاق. |
| `getMessage()` | وصف الإخفاق عند الإخفاق؛ و`null` عند النجاح. |
| `getErrors()` | أخطاء التحقق عند الإخفاق؛ وفارغة عند النجاح. ولا تكون `null` أبدًا. |
| `getTiming()` | `ProcessingTiming` لعملية الإنشاء (وقت البدء ومدة المعالجة)، أو `null`. |

</div>

---

انظر أيضًا: **[GaiaParser — دليل المطوّر](GaiaParser-Arabic.md)** لجانب التحليل، ونموذج عناصر المعرّفات، ومرجع الأخطاء، وملحقَي ثوابت المعرّفات والتفسير.
