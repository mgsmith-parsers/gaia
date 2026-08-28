# GaiaParser — شروع سریع

محمولهٔ یک بارکد GS1 را در حدود ده دقیقه به دادهٔ ساختاریافته، اعتبارسنجی‌شده و خوانا برای
انسان بدل کنید. این راهِ کوتاه است؛ **[راهنمای توسعه‌دهندهٔ GaiaParser](GaiaParser-Persian.md)**
مرجع کامل است، و **[GaiaBuilder](GaiaBuilder-Persian.md)** به جهت وارونه می‌پردازد (ساختن رشته‌های
عنصر و نشانی‌های Digital Link).

## فهرست مطالب

1. [Gaia را به پروژه‌تان بیفزایید](#۱-gaia-را-به-پروژهتان-بیفزایید)
2. [چیزی را تجزیه کنید](#۲-چیزی-را-تجزیه-کنید)
3. [نتیجه را بخوانید](#۳-نتیجه-را-بخوانید)
4. [تجزیه‌ای که شکست می‌خورد را رسیدگی کنید](#۴-تجزیهای-که-شکست-میخورد-را-رسیدگی-کنید)
5. [دو چیز که پایتان را می‌گیرد](#۵-دو-چیز-که-پایتان-را-میگیرد)
6. [پیشوندهای اسکنر و Digital Link خودبه‌خود کار می‌کنند](#۶-پیشوندهای-اسکنر-و-digital-link-خودبهخود-کار-میکنند)
7. [کمتر کار کنید: حالت‌های تجزیه](#۷-کمتر-کار-کنید-حالتهای-تجزیه)
8. [زبان و قالب تاریخ را دگرگون کنید](#۸-زبان-و-قالب-تاریخ-را-دگرگون-کنید)
9. [ورودی نامرتب را پاک کنید](#۹-ورودی-نامرتب-را-پاک-کنید)
10. [از اینجا به کجا](#۱۰-از-اینجا-به-کجا)

---

## ۱. Gaia را به پروژه‌تان بیفزایید

Gaia روی Maven Central منتشر نشده است، پس هسته را یک بار بسازید و در مخزن محلی خود نصب کنید:

```bash
cd gaia && mvn install
```

سپس به آن وابسته شوید:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

همین اندازه وابستگی است که باید بنویسید. جار سبک است: تنها وابستگیِ دامنهٔ ترجمهٔ Gaia —
یعنی `com.fasterxml.jackson.core:jackson-databind` — به‌صورت تراگذر می‌آید؛ و اگر ساخت شما
از پیش نسخه‌ای از Jackson را میخکوب کرده باشد، همان میخکوبی برنده است و Gaia همان را به کار
می‌برد. Gaia **Java 11** را هدف می‌گیرد و همان جار روی هر نسخهٔ بعدی JVM بی‌تغییر اجرا
می‌شود.

> رد کردن مجموعهٔ آزمونِ هسته (`mvn install -DskipTests`) در آغاز کار، چند دقیقه را به چند
> ثانیه بدل می‌کند.

---

## ۲. چیزی را تجزیه کنید

یک کلاس، بدون هیچ پیکربندی:

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

`parse(String)` خط پردازش را **به‌تمامی** اجرا می‌کند: نحو، اعتبارسنجی محتوا، تفسیر. این
پیش‌فرضِ درست است — هرگاه دلیلی سنجیده برای تنگ‌ کردنش یافتید، آن‌گاه تنگش کنید.

---

## ۳. نتیجه را بخوانید

`ParseResult.getAiObject()` شناسه‌های تفکیک‌شده را نگه می‌دارد. شناسهٔ مشخصی را با کدش
بگیرید، نه با جایگاهش:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

هر عنصر فهرستی از **تفسیر** با خود دارد — معنایی که از پس ارقام خام گشوده شده و مرحلهٔ تفسیر
آن را ساخته است:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` بومی شده و برای نمایش است. اما برای *خواندنِ* یک مقدار در کد، آن را با کلید
نوعِ پایدارش بجویید:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

شناسه‌های گوناگون کلیدهای گوناگون می‌سازند — GTIN پیشوند شرکت، نوع GTIN و رقم کنترلش را
می‌دهد؛ قیمت، ارز و مبلغ اعشاری را. فهرست کامل در
[پیوست ب](GaiaParser-Persian.md#پیوست-ب--ثابتهای-کلید-تفسیر) است و ثابت‌ها در
`GS1Constants_Enricher` جای دارند. هر شناسه‌ای تفسیر ندارد: شمارهٔ بچ/لات متن آزاد است و
چیزی برای استنتاج ندارد، پس فهرستش تهی است.

---

## ۴. تجزیه‌ای که شکست می‌خورد را رسیدگی کنید

محمولهٔ نامعتبر برایندی عادی است نه استثنا — `parse` هرگز به‌خاطر دادهٔ خرابِ GS1 استثنا
پرتاب نمی‌کند:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**بر `getId()` شاخه بزنید، هرگز بر پیام.** پیام‌ها بومی می‌شوند و واژه‌گزینی‌شان پیمانی
نیست — افزون بر این، هم‌اکنون نقصی شناخته‌شده در نقل‌قول دارند (همان دوتاییِ `''` در بالا)
که در [مرجع خطاها](GaiaParser-Persian.md#مرجع-خطاها) یادداشت شده است.

دو پرسش جداگانه، دو متد جداگانه:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

تجزیه همین که مرحله‌ای شکست بخورد از ژرف‌تر رفتن بازمی‌ماند، پس رقم کنترلِ نادرست یعنی
خطاهای اعتبارسنجی می‌گیرید اما هیچ تفسیری نه.

### هشدارها نتیجه را نامعتبر نمی‌کنند

برخی بررسی‌ها تنها توصیه‌ای‌اند. پیشوند شرکت GS1‌ای که شناخته نشود گزارش می‌شود، اما محموله
همچنان درست‌ساخت می‌ماند:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

هرگاه هر دو را با هم خواستید، از `getIssues()` بهره بگیرید. اگر جریان کاری شما ناگزیر باید
پیشوندهای ناشناخته را پس بزند، `getWarnings()` را صریحاً بررسی کنید — `isValid()` این کار
را به‌جای شما نخواهد کرد.

---

## ۵. دو چیز که پایتان را می‌گیرد

### جداکنندهٔ GS، و چرا وانهادنش از یک خطا بدتر است

شناسهٔ طول‌متغیر تا **نویسهٔ GS** (ASCII `0x1D`، که در نمادشناسی‌های بارکد FNC1 خوانده
می‌شود) یا تا پایان رشته پیش می‌رود. هنگامی که شناسهٔ دیگری پس از آن بیاید، آن جداکننده
الزامی است:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

اگر آن را وانهید، خطایی **نمی‌گیرید** — پاسخی نادرست می‌گیرید که با اطمینان کامل داده شده
است:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

شناسهٔ `10` از گونهٔ `X..20` است، پس بلعیدن تمامِ `LOT-ABC21SN-98765` کاملاً منطقی است و
تجزیه‌گر هیچ راهی ندارد که بداند مقصود شما این نبوده. هیچ چیز در پایین‌دست نمی‌تواند این را
بازگرداند، پس جداکننده را در سرچشمه درست کنید: بایت‌های اسکنر را با **ISO-8859-1** بخوانید
تا `0x1D` زنده بماند، و در رشته‌های ثابتِ Java بنویسید `""`. شناسه‌های طول‌ثابت (`01`، `17`،
`3103`) به جداکننده نیازی ندارند — تجزیه‌گر طولشان را می‌داند.

### بیشتر شناسه‌ها تنها نمی‌ایستند

بچ/لات، شمارهٔ سریال، تاریخ انقضا و مانند آن‌ها *صفت*‌اند: GS1 General Specifications
می‌خواهد که همراه یک کلید شناسایی بیایند، و Gaia این را اعمال می‌کند.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

یک GTIN بیفزایید تا بگذرد. اما اگر واقعاً باید پاره‌ای را تجزیه کنید — یک آزمون واحد، یک
پویش ناقص — آن بررسی را خاموش کنید:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## ۶. پیشوندهای اسکنر و Digital Link خودبه‌خود کار می‌کنند

لازم نیست به Gaia بگویید ورودی به چه شکلی است — هر چهار شکل را بازمی‌شناسد. هر چه اسکنرتان
داد، همان را بدهید.

**پیشوند شناسهٔ نمادشناسی AIM** نمادشناسی را تعیین می‌کند و خودبه‌خود جدا می‌شود:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**نشانی GS1 Digital Link** نیز از همان اعتبارسنجی و غنی‌سازی می‌گذرد:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

از آنجا که هر دو شکل به همان `GS1AIObject` می‌رسند، کدی که نتیجهٔ پویش را مصرف می‌کند نیازی
ندارد بداند کدام‌یک آمده است — و `toElementString()` / `getCanonicalDigitalLink()` یکی را
به دیگری بدل می‌کنند.

**پیشوند همبستگیِ هشت‌رقمی** (`12345678~…`) نیز اگر جریان شما آن را به کار برد، به همان
شیوه جدا و در `getCorrelationInfo()` نگه داشته می‌شود.

---

## ۷. کمتر کار کنید: حالت‌های تجزیه

پیش‌فرض همه‌چیز را انجام می‌دهد. هرگاه تنها بخشی از پاسخ را می‌خواهید، کمتر بخواهید:

<div dir="rtl">

| حالت | به چه پاسخ می‌دهد | هزینه |
|---|---|---|
| `DATA_CARRIER` | این کدام نمادشناسی است؟ | ارزان‌ترین — هیچ تجزیهٔ شناسه‌ای نه، و `getAiObject()` برابر `null` |
| `SYNTAX` | آیا کدها و طول‌های شناسه‌ها درست‌ساختند؟ | بی رقم کنترل، بی تفسیر |
| `CONTENT` | آیا این دادهٔ معتبر GS1 است؟ | اعتبارسنجی کامل، بی تفسیر |
| `INTERPRETATION` | این چه معنایی دارد؟ | **پیش‌فرض** — همه‌چیز |

</div>

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

هنگامی که حجم بالایی را اعتبارسنجی می‌کنید و هرگز تفکیک را نشان نمی‌دهید `CONTENT` را
برگزینید، و هنگامی که تنها باید نتیجهٔ پویش را به رسیدگی‌کنندهٔ درست بفرستید `DATA_CARRIER`
را.

---

## ۸. زبان و قالب تاریخ را دگرگون کنید

پیام‌های خطا، برچسب‌های تفسیر و شرح شناسه‌ها به **۳۵ زبان** ترجمه شده‌اند؛ و تاریخ‌ها را
هرگونه که بخواهید می‌توان نمایش داد. همهٔ این‌ها در یک `ParseConfig` دگرگون‌ناپذیر جای
می‌گیرند:

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

مقادیر هرگز بومی نمی‌شوند — تنها برچسب‌ها، شرح‌ها و پیام‌ها — پس `"2026-12-31"` و
`"09506000134352"` در هر زبانی همان معنا را دارند. پیکربندی را یک بار در آغاز بسازید و به
اشتراک بگذارید؛ دگرگون‌ناپذیر است.

---

## ۹. ورودی نامرتب را پاک کنید

اگر سرچشمهٔ شما پرانتزهای چاپ‌شدهٔ HRI یا فاصله‌های سرگردان بیرون می‌دهد، در هسته دو
**تغییردهندهٔ ورودی** هست که محموله را پیش از تجزیه سامان می‌دهد:

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

به‌طور پیش‌فرض هیچ‌کدام فعال نیستند، و هر دو هشدارهای خود را دارند — فاصله و پرانتز هر دو
نویسه‌های دادهٔ معتبر GS1 هستند، پس آن‌ها را تنها بر سرچشمه‌ای به کار ببرید که می‌شناسید.
نگاه کنید به [تغییردهنده‌های درون‌ساخت](GaiaParser-Persian.md#تغییردهندههای-درونساخت)، که آنجا نیز
توضیح داده شده چرا برداشتن پرانتزها باید جداکننده‌ای را که بر آن دلالت داشتند بازگرداند.

---

## ۱۰. از اینجا به کجا

- **[راهنمای توسعه‌دهندهٔ GaiaParser](GaiaParser-Persian.md)** — خط پردازش با جزئیات، مدل کاملِ
  نتیجه، همهٔ کدهای خطا، و پیوست‌های شناسه‌ها و کلیدهای تفسیر.
- **[راهنمای توسعه‌دهندهٔ GaiaBuilder](GaiaBuilder-Persian.md)** — ساختن رشته‌های عنصر و نشانی‌های
  Digital Link از جفت‌های شناسه/مقدار.
- **[مرجع HTTP در Gaia API](../../gaia-api-reference.md)** — همان موتور از راه HTTP، اگر ترجیح
  می‌دهید کتابخانه را جاسازی نکنید.
- **[ai-codes.txt](../../ai-codes.txt)** — فهرستی تخت با قالب `(AI) TITLE` برای جست‌وجوی شتابان.

### نسخهٔ پنج‌خطی

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
