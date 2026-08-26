# GaiaParser — البداية السريعة

حوّل حمولة باركود GS1 لبيانات منظّمة ومتحقّق منها ويقدر الإنسان يقراها، في حوالي عشر
دقايق. ده الطريق القصير؛ أما **[دليل مطوّر GaiaParser](GaiaParser-EgyptianArabic.md)** فهو المرجع
الكامل، و**[GaiaBuilder](GaiaBuilder-EgyptianArabic.md)** بيتكلّم عن الاتجاه العكسي (بناء سلاسل العناصر
وعناوين Digital Link).

## المحتويات

1. [ضيف Gaia لمشروعك](#1-ضيف-gaia-لمشروعك)
2. [حلّل حاجة](#2-حلل-حاجة)
3. [اقرا النتيجة](#3-اقرا-النتيجة)
4. [اتعامل مع تحليل فشل](#4-اتعامل-مع-تحليل-فشل)
5. [حاجتين هيوقّعوك](#5-حاجتين-هيوقعوك)
6. [بادئات الماسح وعناوين Digital Link بتشتغل لوحدها](#6-بادئات-الماسح-وعناوين-digital-link-بتشتغل-لوحدها)
7. [اشتغل أقلّ: أوضاع التحليل](#7-اشتغل-أقل-أوضاع-التحليل)
8. [غيّر اللغة وتنسيق التاريخ](#8-غير-اللغة-وتنسيق-التاريخ)
9. [نضّف المدخل المبعثر](#9-نضف-المدخل-المبعثر)
10. [رايح فين بعد كده](#10-رايح-فين-بعد-كده)

---

## 1. ضيف Gaia لمشروعك

Gaia مش منشور على Maven Central، فابني النواة مرة واحدة وثبّتها في مستودعك المحلي:

```bash
cd gaia && mvn install
```

وبعدين اعتمد عليه:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

دي كل الاعتماديات اللي هتكتبها. والـ jar خفيف، فاعتمادية Gaia الوحيدة في نطاق الترجمة —
`com.fasterxml.jackson.core:jackson-databind` — بتيجي بالوراثة؛ ولو البناء بتاعك مثبّت
إصدار من Jackson أصلًا، التثبيت ده هو اللي هيغلب وهو اللي Gaia هيستعمله. وGaia بيستهدف
**Java 11**، ونفس الـ jar بيشتغل من غير تغيير على كل إصدارات JVM اللي بعده.

> إنك تتخطّى مجموعة اختبارات النواة (`mvn install -DskipTests`) بيحوّل كام دقيقة لكام
> تانية وانت لسه في الأول.

---

## 2. حلّل حاجة

كلاس واحد، من غير ضبط:

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

`parse(String)` بيشغّل المسار **كامل**: البنية النحوية، والتحقق من المحتوى، والتفسير. وده
الافتراضي الصح — ضيّقه بعدين لما يبقى عندك سبب قِسته.

---

## 3. اقرا النتيجة

`ParseResult.getAiObject()` شايل المعرّفات اللي اتحلّت. هات معرّف معيّن برمزه مش بمكانه:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

وكل عنصر شايل قايمة **تفسير** — المعنى المفكوك ورا الأرقام الخام، واللي بتطلّعه مرحلة
التفسير:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` مترجمة ومقصودة للعرض. أما علشان *تقرا* قيمة في الكود، دوّر عليها بمفتاح نوعها
الثابت بدل كده:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

والمعرّفات المختلفة بتطلّع مفاتيح مختلفة — GTIN بيدّي بادئة شركته ونوع GTIN ورقم التحقق؛
والسعر بيدّي العملة والمبلغ العشري. والقايمة الكاملة في
[الملحق ب](GaiaParser-EgyptianArabic.md#الملحق-ب--ثوابت-مفاتيح-التفسير)، والثوابت موجودة في
`GS1Constants_Enricher`. ومش كل معرّف ليه تفسيرات: رقم التشغيلة/الدفعة نصّه حر ومفيش حاجة
تتستنتج منه، فقايمته فاضية.

---

## 4. اتعامل مع تحليل فشل

الحمولة المش صحيحة نتيجة عادية مش استثناء — `parse` عمرها ما بترمي علشان بيانات GS1 غلط:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**فرّع على `getId()`، عمرك ما تفرّع على الرسالة.** الرسايل مترجمة وصياغتها مش اتفاق —
وكمان هي دلوقتي شايلة عيب معروف في الاقتباس (المضاعفة `''` اللي فوق)، ومذكور في
[مرجع الأخطاء](GaiaParser-EgyptianArabic.md#مرجع-الأخطاء).

سؤالين مختلفين، ودالّتين مختلفتين:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

التحليل بيبطّل ينزل أول ما مرحلة تفشل، يعني رقم تحقق غلط معناه إنك هتاخد أخطاء تحقق بس
من غير أي تفسيرات.

### التحذيرات مش بتخلّي النتيجة غلط

في فحوص إرشادية بس. بادئة شركة GS1 مش معروفة بيتبلّغ عنها، بس الحمولة بتفضل سليمة:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

استعمل `getIssues()` لما تعوز الاتنين مع بعض. ولو مسار شغلك لازم يرفض البادئات المش معروفة،
افحص `getWarnings()` صراحةً — `isValid()` مش هتعمل كده بدالك.

---

## 5. حاجتين هيوقّعوك

### فاصل GS، وليه إغفاله أوحش من الخطأ

المعرّف متغيّر الطول بيمتدّ لحد **محرف GS** (ASCII `0x1D`، واللي بيتسمّى FNC1 في أنظمة
ترميز الباركود) أو لحد نهاية النص. ولما يبقى وراه معرّف تاني، الفاصل ده إجباري:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

ولو أغفلته **مش** هتاخد خطأ — هتاخد إجابة غلط بكل ثقة:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

المعرّف `10` هو `X..20`، فإنه يبلع `LOT-ABC21SN-98765` كله ده منطقي، ومفيش أي طريقة
المحلّل يعرف بيها إن ده مش اللي انت قصده. ومفيش حاجة بعد كده تقدر ترجّع ده، فظبّط الفاصل
من المصدر: اقرا بايتات الماسح بترميز **ISO-8859-1** علشان `0x1D` يفضل، واكتب `""` في نصوص
Java الحرفية. أما المعرّفات ثابتة الطول (`01` و`17` و`3103`) فمش محتاجة فاصل — المحلّل
عارف طولها.

### أغلب المعرّفات مش بتقف لوحدها

التشغيلة/الدفعة، والرقم التسلسلي، وتاريخ انتهاء الصلاحية وأشباههم دول *سمات*: المواصفات
العامة لـ GS1 بتطلب إنهم يجوا مع مفتاح تعريف، وGaia بيفرض ده.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

ضيف GTIN وهيعدّي. ولو محتاج بجدّ تحلّل جزء — اختبار وحدة، أو مسح جزئي — اقفل الفحص:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. بادئات الماسح وعناوين Digital Link بتشتغل لوحدها

مش محتاج تقول لـ Gaia المدخل بأي شكل — هو بيكشف الأربع أشكال كلهم. ابعتله اللي الماسح
بتاعك ادّاهولك على طول.

**بادئة معرّف ترميز AIM** بتحدّد نظام الترميز وبتتشال لوحدها:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

و**عنوان GS1 Digital Link** بيعدّي بنفس التحقق والإغناء:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

وعلشان الشكلين الاتنين بيوصلوا لنفس `GS1AIObject`، الكود اللي بياخد نتيجة المسح مش محتاج
يهتمّ أنهي واحد فيهم جه — و`toElementString()` / `getCanonicalDigitalLink()` بيحوّلوا
الواحد للتاني.

و**بادئة الارتباط من تمن أرقام** (`12345678~…`) بتتشال برضه وبتتحفظ في
`getCorrelationInfo()`، لو مسارك بيستعملها.

---

## 7. اشتغل أقلّ: أوضاع التحليل

الافتراضي بيعمل كل حاجة. اطلب أقلّ لما تكون محتاج جزء بس من الإجابة:

| الوضع | بيجاوب على إيه | التكلفة |
|---|---|---|
| `DATA_CARRIER` | ده أنهي نظام ترميز؟ | أرخص واحد — من غير أي تحليل للمعرّفات، و`getAiObject()` بتساوي `null` |
| `SYNTAX` | رموز المعرّفات وأطوالها سليمة؟ | من غير أرقام تحقق ومن غير تفسيرات |
| `CONTENT` | دي بيانات GS1 صحيحة؟ | تحقق كامل، من غير تفسير |
| `INTERPRETATION` | ده معناه إيه؟ | **الافتراضي** — كل حاجة |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

اختار `CONTENT` لما تكون بتتحقّق من كميات كبيرة وعمرك ما بتعرض التفكيك، و`DATA_CARRIER`
لما يكون كل اللي محتاجه هو إنك توجّه المسح للمعالج الصح.

---

## 8. غيّر اللغة وتنسيق التاريخ

رسايل الأخطاء وتسميات التفسير ووصوف المعرّفات مترجمة لـ **35 لغة**؛ والتواريخ ممكن
تتعرض زي ما تحبّ. وكل ده في `ParseConfig` واحد مش بيتغيّر:

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

القيم عمرها ما بتتترجم — التسميات والوصوف والرسايل بس — يعني `"2026-12-31"` و
`"09506000134352"` معناهم واحد في كل اللغات. ابني الضبط مرة واحدة أول ما تبدأ وشاركه؛
هو مش بيتغيّر.

---

## 9. نضّف المدخل المبعثر

لو مصدرك بيطلّع أقواس HRI مطبوعة أو مسافات شاردة، في النواة **مُعدِّلين مدخل** بيظبّطوا
الحمولة قبل التحليل:

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

مفيش حاجة شغّالة افتراضيًا، والاتنين ليهم تحفّظات — المسافة والقوسين محارف بيانات مشروعة
في GS1، فطبّقهم بس على مصدر انت عارفه. شوف
[المُعدِّلين المدمجين](GaiaParser-EgyptianArabic.md#المعدلات-المدمجة)، وفيه كمان شرح ليه شيل الأقواس
لازم يرجّع الفاصل اللي كانت بتدلّ عليه.

---

## 10. رايح فين بعد كده

- **[دليل مطوّر GaiaParser](GaiaParser-EgyptianArabic.md)** — المسار بالتفصيل، ونموذج النتيجة الكامل،
  وكل رمز خطأ، وملحقَي المعرّفات ومفاتيح التفسير.
- **[دليل مطوّر GaiaBuilder](GaiaBuilder-EgyptianArabic.md)** — ابني سلاسل عناصر وعناوين Digital Link من
  أزواج معرّف/قيمة.
- **[مرجع Gaia API عبر HTTP](../../gaia-api-reference.md)** — نفس المحرّك عن طريق HTTP، لو مش
  عايز تضمّن المكتبة.
- **[ai-codes.txt](../../ai-codes.txt)** — قايمة مسطّحة بصيغة `(AI) TITLE` للبحث السريع.

### النسخة من خمس سطور

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
