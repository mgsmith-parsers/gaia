# GaiaParser — بداية سريعة

حوّل حمولة باركود GS1 إلى بيانات منظَّمة ومُتحقَّق منها ومقروءة للبشر في نحو عشر
دقائق. هذا هو الطريق القصير؛ أما **[دليل مطوّر GaiaParser](GaiaParser-Arabic.md)** فهو
المرجع الكامل، و**[GaiaBuilder](GaiaBuilder-Arabic.md)** يتناول الاتجاه المعاكس
(بناء سلاسل العناصر وعناوين Digital Link).

## المحتويات

1. [أضف Gaia إلى مشروعك](#1-أضف-gaia-إلى-مشروعك)
2. [حلّل شيئًا](#2-حلل-شيئا)
3. [اقرأ النتيجة](#3-اقرأ-النتيجة)
4. [تعامل مع تحليل مُخفق](#4-تعامل-مع-تحليل-مخفق)
5. [أمران سيوقعانك](#5-أمران-سيوقعانك)
6. [بادئات الماسح وعناوين Digital Link تعمل بلا عناء](#6-بادئات-الماسح-وعناوين-digital-link-تعمل-بلا-عناء)
7. [اعمل أقلّ: أوضاع التحليل](#7-اعمل-أقل-أوضاع-التحليل)
8. [غيّر اللغة وتنسيق التاريخ](#8-غير-اللغة-وتنسيق-التاريخ)
9. [نظّف الإدخال الفوضوي](#9-نظف-الإدخال-الفوضوي)
10. [إلى أين بعد ذلك](#10-إلى-أين-بعد-ذلك)

---

## 1. أضف Gaia إلى مشروعك

Gaia ليس منشورًا على Maven Central، فابنِ النواة مرة واحدة وثبّتها في مستودعك
المحلي:

```bash
cd gaia && mvn install
```

ثم اعتمد عليه:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

هذه هي قائمة الاعتماديات كلها التي عليك كتابتها. والـ jar نحيف، فاعتمادية Gaia الوحيدة
في نطاق الترجمة — `com.fasterxml.jackson.core:jackson-databind` — تصل بالوراثة؛ وإن كان
بناؤك يثبّت إصدارًا من Jackson أصلًا، فذلك التثبيت هو الغالب ويستخدمه Gaia.
ويستهدف Gaia **Java 11**، والـ jar نفسه يعمل دون تغيير على كل إصدار لاحق من JVM.

> تخطّي مجموعة اختبارات النواة (`mvn install -DskipTests`) يحوّل دقائق معدودة إلى ثوانٍ
> معدودة وأنت في طور البدء.

---

## 2. حلّل شيئًا

صنف واحد، بلا ضبط:

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

تشغّل `parse(String)` المسار **كاملًا**: البنية النحوية، والتحقق من المحتوى، والتفسير.
وهو الافتراضي الصائب — ضيّقه لاحقًا إن قِست سببًا يدعو إلى ذلك.

---

## 3. اقرأ النتيجة

تحمل `ParseResult.getAiObject()` المعرّفات التي جرى حلّها. والتقط معرّفًا بعينه برمزه
لا بموضعه:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

ويحمل كل عنصر قائمة **تفسير** — المعنى المفكوك خلف الخانات الخام، وتنتجها مرحلة التفسير:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

و`getLabel()` مُترجَمة ومقصودة للعرض. أما لـ*قراءة* قيمة في الشيفرة، فابحث عنها
بمفتاح نوعها الثابت بدلًا من ذلك:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

والمعرّفات المختلفة تنتج مفاتيح مختلفة — فـ GTIN يعطي بادئة شركته ونوع GTIN ورقم
التحقق؛ والسعر يعطي العملة والمبلغ العشري. والقائمة الكاملة في
[الملحق ب](GaiaParser-Arabic.md#الملحق-ب--ثوابت-مفاتيح-التفسير)، والثوابت موجودة
في `GS1Constants_Enricher`. وليس لكل معرّف تفسيرات: فرقم دفعة/تشغيلة نصّه حر لا شيء
يُستنبط منه، فقائمته فارغة.

---

## 4. تعامل مع تحليل مُخفق

الحمولة غير الصالحة نتيجة عادية لا استثناء — فـ `parse` لا ترمي أبدًا من أجل بيانات
GS1 خاطئة:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**فرّع على `getId()`، لا على الرسالة أبدًا.** فالرسائل مُترجَمة وصياغتها ليست عقدًا —
كما أنها تحمل حاليًا خللًا معروفًا في الاقتباس (المضاعفة `''` أعلاه)، مذكورًا في
[مرجع الأخطاء](GaiaParser-Arabic.md#مرجع-الأخطاء).

سؤالان مختلفان، ودالّتان مختلفتان:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

يتوقف التحليل عن النزول بمجرد إخفاق مرحلة، فرقم تحقق خاطئ يعني أنك تحصل على أخطاء
تحقق لكن بلا تفسيرات.

### التحذيرات لا تجعل النتيجة غير صالحة

بعض الفحوص إرشادية. فبادئة شركة GS1 غير المعروفة يُبلَّغ عنها، لكن الحمولة تبقى سليمة
بنيويًا:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

استخدم `getIssues()` حين تريدهما معًا. وإن كان سير عملك يوجب رفض البادئات غير المعروفة،
فافحص `getWarnings()` صراحةً — فـ `isValid()` لن تفعل ذلك نيابةً عنك.

---

## 5. أمران سيوقعانك

### فاصل GS، ولماذا إغفاله أسوأ من خطأ

يمتدّ المعرّف متغير الطول حتى **محرف GS** (ASCII `0x1D`، ويُسمّى FNC1 في أنظمة ترميز
الباركود) أو حتى نهاية النص. وحين يتلوه معرّف آخر، يكون ذلك الفاصل
إلزاميًا:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

وإن أغفلته **لن** تحصل على خطأ — بل على إجابة خاطئة بكل ثقة:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

فالمعرّف `10` هو `X..20`، ومن ثمّ يبتلع `LOT-ABC21SN-98765` بوجه مشروع ولا سبيل للمحلّل
أن يعلم أن ذلك لم يكن مقصودًا. ولا شيء لاحقًا يستطيع استعادة هذا، فاضبط الفاصل عند
المصدر: اقرأ بايتات الماسح بترميز **ISO-8859-1** لينجو `0x1D`، واكتب
`""` في نصوص Java الحرفية. والمعرّفات ثابتة الطول (`01` و`17` و`3103`) لا
تحتاج فاصلًا — فالمحلّل يعرف طولها.

### أغلب المعرّفات لا تقوم بذاتها

الدفعة/التشغيلة والرقم التسلسلي وتاريخ الانتهاء وأمثالها *سمات*: فالمواصفات العامة لـ
GS1 تستلزم أن تصحب مفتاح تعريف، وGaia يفرض ذلك.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

أضف GTIN فيجتاز. وإن كنت تحتاج فعلًا إلى تحليل جزء — كاختبار وحدة، أو مسح
جزئي — فأوقف الفحص:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. بادئات الماسح وعناوين Digital Link تعمل بلا عناء

لست مضطرًا إلى إخبار Gaia بصيغة الإدخال — فهو يكشف الصيغ الأربع كلها. مرّر إليه
ما أعطاك إياه الماسح أيًّا كان.

**بادئة معرّف ترميز AIM** تحدّد نظام الترميز وتُنزع تلقائيًا:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**وعنوان GS1 Digital Link** يمرّ بالتحقق والإثراء نفسيهما:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

ولأن الصيغتين تنتهيان إلى `GS1AIObject` نفسه، فالشيفرة التي تستهلك المسح لا تحتاج إلى
الاكتراث بأيّهما وصل — و`toElementString()` / `getCanonicalDigitalLink()` تحوّلان
بينهما.

و**بادئة الارتباط من ثماني خانات** (`12345678~…`) تُنزع كذلك وتُحفظ في
`getCorrelationInfo()`، إن كان مسارك يستعملها.

---

## 7. اعمل أقلّ: أوضاع التحليل

الافتراضي يفعل كل شيء. اطلب أقلّ حين لا تحتاج إلا إلى جزء من الجواب:

<div dir="rtl">

| الوضع | يجيب عن | الكلفة |
|---|---|---|
| `DATA_CARRIER` | أي نظام ترميز هذا؟ | الأرخص — بلا أي تحليل للمعرّفات، و`getAiObject()` تساوي `null` |
| `SYNTAX` | هل رموز المعرّفات وأطوالها سليمة البنية؟ | بلا أرقام تحقق وبلا تفسيرات |
| `CONTENT` | هل هذه بيانات GS1 صالحة؟ | تحقق كامل، بلا تفسيرات |
| `INTERPRETATION` | ماذا يعني هذا؟ | **الافتراضي** — كل شيء |

</div>

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

اختر `CONTENT` حين تتحقّق بكميات كبيرة ولا تعرض التفكيك أبدًا، و`DATA_CARRIER` حين لا
تحتاج إلا إلى توجيه المسح إلى المعالج الصحيح.

---

## 8. غيّر اللغة وتنسيق التاريخ

رسائل الأخطاء وتسميات التفسير ووصوف المعرّفات مترجمة إلى **35 لغة**؛ والتواريخ تُعرض
كما تشاء. وكل ذلك في `ParseConfig` واحد غير قابل للتغيير:

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

القيم لا تُترجَم أبدًا — إنما التسميات والوصوف والرسائل وحدها — فـ `"2026-12-31"`
و`"09506000134352"` تعنيان الشيء نفسه في كل لغة. ابنِ الضبط مرة واحدة عند بدء التشغيل
وشاركه؛ فهو غير قابل للتغيير.

---

## 9. نظّف الإدخال الفوضوي

إن كان مصدرك يُخرج أقواس HRI المطبوعة أو مسافات شاردة، ففي النواة **مُعدِّلا إدخال**
يصلحان الحمولة قبل التحليل:

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

لا شيء مفعَّل افتراضيًا، ولكليهما تحفّظات — فالمسافة والقوسان محارف بيانات مشروعة في
GS1، فطبّقهما فقط على مصدر تعرفه. انظر
[المُعدِّلان المدمجان](GaiaParser-Arabic.md#المعدلان-المدمجان)، وفيه أيضًا شرحٌ لماذا يجب على
نزع الأقواس أن يعيد الفاصل الذي كانت تتضمّنه.

---

## 10. إلى أين بعد ذلك

- **[دليل مطوّر GaiaParser](GaiaParser-Arabic.md)** — المسار بالتفصيل، ونموذج النتيجة الكامل،
  وكل رمز خطأ، وملحقا المعرّفات ومفاتيح التفسير.
- **[دليل مطوّر GaiaBuilder](GaiaBuilder-Arabic.md)** — ابنِ سلاسل العناصر وعناوين Digital Link
  من أزواج المعرّف/القيمة.
- **[مرجع Gaia API عبر HTTP](../../gaia-api-reference.md)** — المحرّك نفسه عبر HTTP، إن كنت
  تفضّل ألّا تُضمّن المكتبة.
- **[ai-codes.txt](../../ai-codes.txt)** — قائمة مسطّحة بصيغة `(AI) TITLE` للبحث السريع.

### النسخة من خمسة أسطر

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
