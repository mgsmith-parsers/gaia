# GaiaBuilder — دليل المطوّر

## المحتويات

1. [نظرة عامة](#نظرة-عامة)
2. [عن GS1 والمواصفات العامة](#عن-gs1-والمواصفات-العامة)
3. [البداية السريعة](#البداية-السريعة)
4. [بيشتغل إزاي](#بيشتغل-إزاي)
5. [بناء سلاسل العناصر](#بناء-سلاسل-العناصر)
   - [معرّفات السمات محتاجة مفتاح تعريفها](#معرفات-السمات-محتاجة-مفتاح-تعريفها)
6. [بناء عناوين Digital Link](#بناء-عناوين-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [التحقق والأخطاء](#التحقق-والأخطاء)
   - [دوال الإنشاء اللي بترمي استثناءات](#دوال-الإنشاء-اللي-بترمي-استثناءات)
   - [دوال tryBuild\* اللي ما بترميش استثناءات](#دوال-trybuild-اللي-ما-بترميش-استثناءات)
   - [لغة رسايل الأخطاء](#لغة-رسايل-الأخطاء)
   - [BuildResult](#buildresult)
9. [أرقام التحقق](#أرقام-التحقق)
10. [الأمان مع الخيوط المتعددة](#الأمان-مع-الخيوط-المتعددة)
11. [مرجع الواجهة البرمجية](#مرجع-الواجهة-البرمجية)

---

## نظرة عامة

`GaiaBuilder` ده النظير العكسي لـ [`GaiaParser`](GaiaParser-EgyptianArabic.md): بياخد مجموعة من أزواج معرّف التطبيق (AI) وقيمته ويحوّلها لـ **سلسلة عناصر** GS1 سليمة أو لـ **عنوان GS1 Digital Link**. انت بتديله المعرّفات وقيم بياناتها الكاملة؛ والمُنشئ بيوصّلها، ويتحقّق من الناتج بنفس المحرّك اللي `GaiaParser` بيستعمله، وبعدين يعرض الخرج.

وعلشان المُنشئ بيتحقّق عن طريق إنه *يحلّل ناتجه المرشّح بنفسه*، فأي حاجة بيرجّعها مضمون إنها هتتحلّل نضيف بـ `GaiaParser` — يعني مستحيل الاتنين يختلفوا على إيه اللي سليم.

**كلاس نقطة الدخول:** `tools.pantheum.gaia.GaiaBuilder`

---

## عن GS1 والمواصفات العامة

**GS1** دي منظمة عالمية مش هادفة للربح، بتطوّر معايير مفتوحة للتعريف وتبادل البيانات في سلاسل الإمداد وبتحافظ عليها. معاييرها بتتستعمل في التجزئة والرعاية الصحية والخدمات اللوجستية وخدمات الأغذية وصناعات تانية كتير، وبتغطّي كل حاجة من الباركود اللي على تغليف المنتجات لحد تتبّع جرعات الأدوية بالرقم التسلسلي.

المرجع المعتمد لكل حاجة بينفّذها المُنشئ ده هو **المواصفات العامة لـ GS1** — وثيقة واحدة بتحدّد:

- كل رموز معرّفات التطبيق (AI)، وعناوين بياناتها وتنسيقاتها وقواعد التحقق منها
- قواعد بناء سلاسل عناصر معرّفات التطبيق وترميزها
- متطلبات أنظمة ترميز الباركود وتوزيع معرّفات ترميز AIM
- خوارزميات رقم التحقق وحرف التحقق
- تفسير السنة اللي من رقمين (قاعدة النافذة المتحرّكة)
- مواصفات Data Matrix وQR Code وGS1-128 وGS1 DataBar وغيرها من النواقل

المواصفات العامة لـ GS1 بتتحدّث كل سنة. الإصدار الحالي والمواد المساعدة موجودين هنا:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA بينفّذ **الإصدار 26.0 (معتمد، يناير 2026)** من المواصفات العامة لـ GS1.

عناوين GS1 Digital Link بيحكمها معيار مرافق اسمه **GS1 Digital Link: URI Syntax**، وهو اللي بيحدّد مفاتيح التعريف الأساسية، وترتيب مقيّدات المفاتيح، وترميز سمات البيانات اللي المُنشئ بيطبّقه وهو بيعرض عناوين Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA بينفّذ **الإصدار 1.7.0 (معتمد، أغسطس 2026)** من معيار GS1 Digital Link: URI Syntax.

الإشارات للأقسام في الوثيقة دي كلها بتشاور على المواصفات العامة لـ GS1 (زي «Table 7-5» و«section 7.12»)، ما عدا أرقام أقسام Digital Link (زي «§4.9» و«§4.12») اللي بتشاور على معيار GS1 Digital Link: URI Syntax.

---

## البداية السريعة

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

استعمل ثوابت `GS1Constants_AICodes` بدل نصوص المعرّفات الخام (شوف [الملحق أ في دليل المحلّل](GaiaParser-EgyptianArabic.md#الملحق-أ--ثوابت-نصوص-المعرفات)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## بيشتغل إزاي

كل عملية إنشاء بتمشي في نفس المسار:

1. **التوصيل** — أزواج المعرّف/القيمة بتتوصّل في سلسلة عناصر مرشّحة. وفاصل مجموعة FNC1 (`0x1D`) بيتحطّ بعد كل معرّف *محتاج فاصل* ومش آخر عنصر. أما المعرّفات اللي طولها محدّد من الأول (GTIN والتواريخ والقياسات ثابتة الطول) فما بتاخدش فاصل؛ وغيرها بياخد. (المعرّفات المش معروفة أصلًا ما بتوصلش للخطوة دي — `ai(...)` بيرفضها على طول؛ شوف [بناء سلاسل العناصر](#بناء-سلاسل-العناصر).)
2. **التحقق** — المرشّح بيتحلّل في وضع `CONTENT` بـ `GaiaParser`. وكل قيمة بتتفحص قدّام تنسيق معرّفها ورقم تحققه، والقواعد البنيوية (الاقترانات المطلوبة والممنوعة بين المعرّفات) بتتفرض. ولو التحليل مش صحيح، الإنشاء بيفشل.
3. **العرض** —
   - لسلسلة العناصر، بترجع `toElementString()` بتاعة الكائن اللي اتحقّق منه.
   - لـ Digital Link، كل عنصر بياخد دوره في DL (مفتاح أساسي، أو مقيّد مفتاح، أو سمة بيانات)، وتسلسل مقيّدات المفاتيح بيتحقّق منه، والعنوان بيتطلّع، وبعدين **العنوان اللي اتطلّع بيتحلّل تاني علشان نتأكّد إنه بيرجع Digital Link صحيح** — وده فحص أمان لخطوة توصيل النص والترميز بعلامة النسبة. ولو ما رجعش، بيترمي `GaiaBuilderException`.

وده بيعكس منطق إعادة البناء في `DLSyntaxParser`، فمكان الفواصل والتحقق بيبقوا بالظبط زي ما المحلّل متوقّع.

---

## بناء سلاسل العناصر

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **المعرّف** بيتحقّق منه على طول: `ai(...)` بترمي `IllegalArgumentException` لو مش معرّف تطبيق GS1 معروف. (المُنشئ بيوصّل المعرّف بالقيمة قبل التحليل، فمعرّف مش معروف أو طويل زيادة زي `"99999"` لازم يتمسك هنا — وإلا كان هيتقطّع في سكوت لمعرّف تاني.) أما **القيمة** فبتتحقّق بعد كده، وقت الإنشاء.
- القيم لازم تكون **كاملة**، بما فيها رقم التحقق. والمُنشئ لا بيحسب أرقام التحقق ولا بيضيفها بدالك — شوف [أرقام التحقق](#أرقام-التحقق).
- المعرّفات بتتطلّع بالترتيب اللي انت بتضيفها بيه. والمُنشئ بيحطّ فواصل FNC1 في الأماكن اللي بنية GS1 بتطلبها؛ ما تضيفهاش انت.
- الإنشاء **من غير أي معرّفات** بيرمي `GaiaBuilderException("No AIs supplied")` وقايمة `getErrors()` فاضية — وده الإخفاق الوحيد اللي ما بيشيلش أي `GaiaError`.
- وأي معرّف قيمته فشلت في قاعدة تنسيقه أو رقم تحققه بيخلّي الإنشاء كله يفشل.

### معرّفات السمات محتاجة مفتاح تعريفها

أغلب المعرّفات دي *سمات* المواصفات العامة لـ GS1 بتطلب إنها تيجي مع مفتاح تعريف، والمُنشئ بيفرض ده — بيتحقّق عن طريق مرحلة البنية النحوية كاملة، ومفيش طريقة تلفّ حواليها. يعني رقم تشغيلة/دفعة أو رقم تسلسلي لوحده **مش** سلسلة عناصر صحيحة:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

أما مفاتيح التعريف (GTIN `01`، وSSCC `00`، وGLN `414`، …) والمعرّفات الداخلية للشركات (`90`–`99`) فبتقف لوحدها بشكل مشروع تمامًا. وكل اللي غير كده محتاج مرافق.

> تقدر تقول لـ `GaiaParser` إنه يتخطّى الفحص ده بـ `ParseConfig.skipRequiresCheck(true)`؛ إنما `GaiaBuilder` عن قصد ما بيوفّرش حاجة زيها — علشان هدفه يطلّع ناتج متوافق مع المعايير. ولو عايز توصّل سلسلة عناصر ناقصة عن قصد، وصّلها بنفسك وحلّلها والفحص مقفول.

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

Digital Link الصحيح محتاج مفتاح **تعريف أساسي** واحد بالظبط (زي GTIN `01`، وGLN `414`، وSSCC `00`). والمُنشئ بيصنّف كل معرّف بتديهوله:

<div dir="rtl">

| الدور | بيتعرض إزاي | مثال |
|------|-------------|---------|
| مفتاح التعريف الأساسي | مقطع مسار بعد النطاق/البادئة | `/01/09506000134352` |
| مقيّد المفتاح (CPV `22`، والتشغيلة `10`، والتسلسلي `21`، …) | مقاطع المسار اللي بعده، **بالترتيب المعياري في §4.9** (مش بترتيب إضافتك) | `/10/LOT-ABC` |
| سمة البيانات (كل اللي غير كده) | معاملات استعلام، **مترتّبة معجميًا بمفتاح المعرّف** (§4.12) | `?17=271231` |

</div>

وعلشان المقيّدات بيتعاد ترتيبها وقت العرض، مفيش مشكلة لو ديتهم مش بالترتيب — يعني `ai("21", …)` قبل `ai("10", …)` هتتعرض برضه `/10/LOT/21/SER`. اللي لازم يكون مقبول للمفتاح الأساسي هو *المجموعة* بس.

والقيم في المسار والاستعلام الاتنين بتترمّز بعلامة النسبة.

والإنشاء بيفشل (بيرمي `GaiaBuilderException`، أو بيرجّع `BuildResult` فاشل) لما:

- ما يكونش في **أي** مفتاح تعريف أساسي بين المعرّفات؛
- يكون في **أكتر من** مفتاح تعريف أساسي واحد؛
- يكون في معرّف **ممنوع** في Digital Link (`03`، `8014`)؛
- يكون **تسلسل مقيّدات المفاتيح** مش صحيح للمفتاح الأساسي المختار (مقيّد مش بيجي مع المفتاح ده، أو مقيّدات بره ترتيبها المسموح).

---

## BuilderDigitalLinkConfig

ابعت `BuilderDigitalLinkConfig` علشان تتحكّم في المخطّط والنطاق وبادئة المسار ومعاملات الاستعلام الزيادة والجزء:

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
| `scheme(String)` | مخطّط العنوان؛ لازم يكون `http` أو `https` | `https` |
| `domain(String)` | جهة الحلّ — المضيف أو `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | مقاطع المسار قبل أول مفتاح أساسي؛ والشرطات المايلة على الطرفين بتتظبّط | *(مفيش)* |
| `baseUrl(String)` | تسهيل بيقسّم عنوان لـ `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | معامل استعلام زيادة، بيتحطّ **بعد** سمات بيانات المعرّفات، بترتيب الإدراج؛ ومترمّز بعلامة النسبة | — |
| `fragment(String)` | جزء العنوان (من غير `#` في أوله)؛ مترمّز بعلامة النسبة | *(مفيش)* |

</div>

و`build()` بيتحقّق من الضبط على طول: مخطّط مش `http(s)` أو نطاق فاضي بيرمي `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (واسمه المستعار `defaultConfig()`) ده الافتراضي `https://id.gs1.org` من غير أي إضافات — يعني بالظبط اللي `buildDigitalLinkUri()` من غير وسيط بتستعمله، واللي `GS1AIObject.getCanonicalDigitalLink()` بتطلّعه.
- `baseUrl("http://id.example.org:8080/r")` → المخطّط `http`، والنطاق `id.example.org:8080`، وبادئة المسار `/r`.
- ومعاملات الاستعلام الزيادة بتيجي دايمًا بعد السمات المستمدّة من المعرّفات، علشان ترتيب المعرّفات المعياري (§4.12) يفضل زي ما هو.

و`BuilderDigitalLinkConfig` مش بيتغيّر؛ فاستعمل نسخة واحدة براحتك.

---

## التحقق والأخطاء

### دوال الإنشاء اللي بترمي استثناءات

`buildElementString()` و`buildDigitalLinkUri()` و`buildDigitalLinkUri(BuilderDigitalLinkConfig)` بترمي **`GaiaBuilderException`** (وهو `RuntimeException` غير متحقّق منه) لما المعرّفات ما تعرفش تكوّن ناتج سليم:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- في إخفاقات **المحتوى** (رقم تحقق غلط، أو تنسيق مش مطابق، أو معرّف ناقص/ممنوع)، `getErrors()` بتشيل كائنات `GaiaError` بتاعة المحلّل — نفس الكائنات [الموثّقة في دليل المحلّل](GaiaParser-EgyptianArabic.md#gaiaerror).
- وفي إخفاقات **بنية Digital Link** (مفيش مفتاح أساسي، أو مفاتيح أساسية كتير، أو معرّف ممنوع، أو تسلسل مقيّدات مفاتيح مش صحيح)، `getErrors()` بتشيل كائن `GaiaError` واحد (بالرمز `GE-L008` أو `GE-L012` أو `GE-L013` أو `GE-L014`) مترجم للغة المُنشئ.

### دوال tryBuild\* اللي ما بترميش استثناءات

لما المدخل يكون جاي من المستخدم والإخفاق يكون نتيجة متوقّعة تقدر تتعامل معاها، استعمل صيغ `tryBuild*` بدل ما تتحكّم في التدفّق بالاستثناءات. دي بترجّع [`BuildResult`](#buildresult) بدل ما ترمي:

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

| بترمي | ما بترميش |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

</div>

وكل دالة `tryBuild*` بتشارك نواة التحقق نفسها مع توأمها اللي بيرمي؛ الفرق بس في حدّ الإخفاق.

### لغة رسايل الأخطاء

أخطاء التحقق من المحتوى بتيجي من فهرس الأخطاء المترجم. نادِ `language(...)` علشان تختار لغة رسايل `GaiaError` اللي `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` بتشيلها؛ والافتراضي الإنجليزية:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

وده نفس إعداد `GaiaConstants.Language` اللي `GaiaParser` بيقبله عن طريق `ParseConfig`، فالمُنشئ والمحلّل بيترجموا بنفس الطريقة.

ورسايل `GaiaError` بتاعة إخفاقات **المحتوى** و**بنية Digital Link** (مفيش مفتاح أساسي، أو مفاتيح أساسية كتير، أو معرّف ممنوع، أو تسلسل مقيّدات مفاتيح مش صحيح) — الاتنين بيتترجموا عن طريق فهرس الأخطاء المشترك؛ والتانية بتستعمل الرموز `GE-L008` و`GE-L012` و`GE-L013` و`GE-L014`.

### BuildResult

`BuildResult` (في الحزمة `tools.pantheum.gaia.result`) ده نوع قيمة مش بيتغيّر بيوصف نتيجة استدعاء `tryBuild*`:

<div dir="rtl">

| الدالة | لما تنجح | لما تفشل |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | النص المعروض | `null` |
| `getMessage()` | `null` | وصف الإخفاق |
| `getErrors()` | قايمة فاضية | أخطاء التحقق (نفسها اللي في `GaiaBuilderException.getErrors()`) |

</div>

---

## أرقام التحقق

المُنشئ بيتحقّق من أرقام التحقق بس **ما بيحسبهاش** — لازم قيمك تكون شايلة رقم تحققها أصلًا. وعلشان تحسب واحد، استعمل `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` بتطبّق خوارزمية GS1 القياسية بنظام 10 التقايسي على الأرقام اللي بعتّها وبترجّع رقم تحقق من `0` لـ `9`، أو `-1` لو المدخل `null` أو فاضي أو مش رقمي.

---

## الأمان مع الخيوط المتعددة

`GaiaBuilder` **مش** آمن مع الخيوط المتعددة وهو معمول للاستعمال مرة واحدة: نادِ `create()`، وضيف المعرّفات، وابني مرة واحدة. اعمل مُنشئ جديد لكل ناتج؛ وما تشاركش واحد بين الخيوط.

أما `BuilderDigitalLinkConfig` (ومخرجاته من نوع `BuildResult`) فمش بتتغيّر وممكن تتشارك براحتك — ابني ضبط واحد أول ما تبدأ وأعد استعماله في مُنشئات كتير.

---

## مرجع الواجهة البرمجية

### `GaiaBuilder`

<div dir="rtl">

| الدالة | الوصف |
|--------|-------------|
| `static GaiaBuilder create()` | بتبدأ مُنشئ جديد فاضي. |
| `GaiaBuilder ai(String ai, String value)` | بتضيف معرّف وقيمته الكاملة. وبترمي `IllegalArgumentException` لو أي واحد فيهم `null`، أو لو `ai` مش معرّف تطبيق GS1 معروف. |
| `GaiaBuilder language(GaiaConstants.Language language)` | بتضبط لغة رسايل أخطاء التحقق من المحتوى (والافتراضي الإنجليزية). و`null` بتتجاهل. |
| `String buildElementString()` | بتعرض سلسلة عناصر GS1. وبترمي `GaiaBuilderException` لما تفشل. |
| `String buildDigitalLinkUri()` | بتعرض عنوان Digital Link معياري. وبترمي `GaiaBuilderException` لما تفشل. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | بتعرض عنوان Digital Link حسب `config`. وبترمي `GaiaBuilderException` لما تفشل. |
| `BuildResult tryBuildElementString()` | إنشاء سلسلة عناصر من غير رمي استثناء. |
| `BuildResult tryBuildDigitalLinkUri()` | إنشاء Digital Link معياري من غير رمي استثناء. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | إنشاء Digital Link حسب `config` من غير رمي استثناء. |

</div>

### `BuilderDigitalLinkConfig`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | الافتراضي `https://id.gs1.org`. |
| `static Builder builder()` | باني ضبط جديد. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | المخطّط وجهة الحلّ وبادئة المسار بعد ما اتحلّوا. |
| `getExtraQueryParams()` | معاملات الاستعلام الزيادة، بترتيب الإدراج. |
| `getFragment()` | الجزء، أو `null`. |

</div>

### `GaiaBuilderException`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `getErrors()` | كائنات `GaiaError` اللي عملت الإخفاق — أخطاء المحلّل في إخفاق المحتوى، أو خطأ بنيوي واحد في Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). وعمرها ما بتبقى `null`. |

</div>

### `BuildResult`

<div dir="rtl">

| العضو | الوصف |
|--------|-------------|
| `isSuccess()` | الإنشاء نجح ولا لأ. |
| `getValue()` | الناتج المعروض لما ينجح؛ و`null` لما يفشل. |
| `getMessage()` | وصف الإخفاق لما يفشل؛ و`null` لما ينجح. |
| `getErrors()` | أخطاء التحقق لما يفشل؛ وفاضية لما ينجح. وعمرها ما بتبقى `null`. |
| `getTiming()` | `ProcessingTiming` بتاعة عملية الإنشاء (وقت البدء ومدة المعالجة)، أو `null`. |

</div>

---

شوف كمان: **[GaiaParser — دليل المطوّر](GaiaParser-EgyptianArabic.md)** لجانب التحليل، ونموذج عناصر المعرّفات، ومرجع الأخطاء، وملحقَي ثوابت المعرّفات والتفسير.
