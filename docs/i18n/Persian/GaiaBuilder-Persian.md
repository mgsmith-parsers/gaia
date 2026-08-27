# GaiaBuilder — راهنمای توسعه‌دهنده

## فهرست مطالب

1. [نگاه کلی](#نگاه-کلی)
2. [دربارهٔ GS1 و General Specifications](#درباره-gs1-و-general-specifications)
3. [شروع سریع](#شروع-سریع)
4. [چگونه کار می‌کند](#چگونه-کار-میکند)
5. [ساخت رشته‌های عنصر](#ساخت-رشتههای-عنصر)
   - [شناسه‌های صفتی به کلید شناسایی خود نیاز دارند](#شناسههای-صفتی-به-کلید-شناسایی-خود-نیاز-دارند)
6. [ساخت نشانی‌های Digital Link](#ساخت-نشانیهای-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [اعتبارسنجی و خطاها](#اعتبارسنجی-و-خطاها)
   - [متدهای ساختی که استثنا پرتاب می‌کنند](#متدهای-ساختی-که-استثنا-پرتاب-میکنند)
   - [متدهای tryBuild\* که استثنا پرتاب نمی‌کنند](#متدهای-trybuild-که-استثنا-پرتاب-نمیکنند)
   - [زبان پیام‌های خطا](#زبان-پیامهای-خطا)
   - [BuildResult](#buildresult)
9. [ارقام کنترل](#ارقام-کنترل)
10. [امنیت در برابر نخ‌ها](#امنیت-در-برابر-نخها)
11. [مرجع API](#مرجع-api)

---

## نگاه کلی

`GaiaBuilder` همتای وارونهٔ [`GaiaParser`](GaiaParser-Persian.md) است: مجموعه‌ای از جفت‌های شناسهٔ کاربرد (AI) و مقدار را به یک **رشتهٔ عنصر** درست‌ساختِ GS1 یا به یک **نشانی GS1 Digital Link** بدل می‌کند. شما شناسه‌ها را همراه با مقادیر دادهٔ کاملشان می‌دهید؛ سازنده آن‌ها را به هم می‌پیوندد، نتیجه را با همان موتوری که `GaiaParser` به کار می‌برد اعتبارسنجی می‌کند، و سپس خروجی را برمی‌سازد.

از آنجا که سازنده با *تجزیهٔ خروجی نامزدِ خودش* اعتبارسنجی می‌کند، هر چه بازمی‌گرداند به‌یقین از راه `GaiaParser` بی‌عیب تجزیه خواهد شد — این دو هرگز نمی‌توانند بر سر آنکه چه چیزی درست‌ساخت است اختلاف کنند.

**کلاس نقطهٔ ورود:** `tools.pantheum.gaia.GaiaBuilder`

---

## دربارهٔ GS1 و General Specifications

**GS1** سازمانی جهانی و غیرانتفاعی است که استانداردهای باز را برای شناسایی و تبادل داده در زنجیرهٔ تأمین تدوین و نگهداری می‌کند. استانداردهای آن در خرده‌فروشی، بهداشت و درمان، لجستیک، خدمات غذایی و بسیاری صنایع دیگر به کار می‌رود و همه چیز را در بر می‌گیرد؛ از بارکد کالا روی بسته‌بندی مصرف‌کننده تا ردیابی سریالی دوزهای دارویی.

مرجع معتبر برای هر آنچه این سازنده پیاده می‌کند، **GS1 General Specifications** است — سندی یگانه که موارد زیر را تعریف می‌کند:

- همهٔ کدهای شناسهٔ کاربرد (AI)، عنوان‌های داده، قالب‌ها و قواعد اعتبارسنجی آن‌ها
- قواعد نحوی ساخت و رمزگذاری رشته‌های عنصر AI
- الزامات نمادشناسی بارکد و تخصیص شناسه‌های کد AIM
- الگوریتم‌های رقم کنترل و نویسهٔ کنترل
- تعیین سالِ دورقمی (قاعدهٔ پنجرهٔ لغزان)
- مشخصات Data Matrix، QR Code، GS1-128، GS1 DataBar و دیگر حامل‌ها

GS1 General Specifications هر سال به‌روز می‌شود. ویرایش کنونی و منابع پشتیبان در این نشانی در دسترس است:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA **نسخهٔ ۲۶٫۰ (تصویب‌شده، ژانویهٔ ۲۰۲۶)** از GS1 General Specifications را پیاده می‌کند.

نشانی‌های GS1 Digital Link تابع استانداردی همراه به نام **GS1 Digital Link: URI Syntax** هستند که کلیدهای شناسایی اصلی، ترتیب مقیدکننده‌های کلید و رمزگذاری صفت‌های داده را تعریف می‌کند — همان چیزی که سازنده هنگام برساختنِ نشانی‌های Digital Link اعمال می‌کند:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA **نسخهٔ ۱٫۷٫۰ (تصویب‌شده، اوت ۲۰۲۶)** از استاندارد GS1 Digital Link: URI Syntax را پیاده می‌کند.

ارجاع‌های بخشی در سراسر این سند به GS1 General Specifications اشاره دارند (مانند «Table 7-5» و «section 7.12»)، مگر شماره‌بخش‌های Digital Link (مانند «§4.9» و «§4.12») که به استاندارد GS1 Digital Link: URI Syntax اشاره می‌کنند.

---

## شروع سریع

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

به‌جای رشته‌های خامِ شناسه، ثابت‌های `GS1Constants_AICodes` را برگزینید (نگاه کنید به [پیوست الف در راهنمای تجزیه‌گر](GaiaParser-Persian.md#پیوست-الف--ثابتهای-رشتهای-شناسهها)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## چگونه کار می‌کند

هر ساخت از همان مسیر می‌گذرد:

۱. **پیوستن** — جفت‌های شناسه/مقدار به هم می‌پیوندند و یک رشتهٔ عنصر نامزد می‌سازند. جداکنندهٔ گروهیِ FNC1 (`0x1D`) پس از هر شناسه‌ای گذاشته می‌شود که *به جداکننده نیاز دارد* و آخرین عنصر نیست. شناسه‌هایی که طولشان از پیش معین است (GTIN، تاریخ‌ها، اندازه‌گیری‌های طول‌ثابت) جداکننده نمی‌گیرند؛ دیگران می‌گیرند. (شناسه‌های ناشناخته هرگز به این گام نمی‌رسند — `ai(...)` بی‌درنگ آن‌ها را پس می‌زند؛ نگاه کنید به [ساخت رشته‌های عنصر](#ساخت-رشتههای-عنصر).)
۲. **اعتبارسنجی** — نامزد در حالت `CONTENT` با `GaiaParser` تجزیه می‌شود. هر مقدار در برابر قالب و رقم کنترلِ شناسهٔ خود سنجیده می‌شود و قواعد ساختاری (جفت‌های الزامی و ناسازگارِ شناسه‌ها) اعمال می‌گردند. اگر تجزیه معتبر نباشد، ساخت شکست می‌خورد.
۳. **برساختن** —
   - برای رشتهٔ عنصر، مقدار `toElementString()` شیءِ اعتبارسنجی‌شده بازگردانده می‌شود.
   - برای Digital Link، به هر عنصر نقش DL آن گمارده می‌شود (کلید اصلی، مقیدکنندهٔ کلید، یا صفت داده)، رشتهٔ مقیدکننده‌های کلید اعتبارسنجی می‌شود، نشانی بیرون داده می‌شود، و سپس **نشانی بیرون‌داده‌شده دوباره تجزیه می‌شود تا مطمئن شویم همچون Digital Link‌ای معتبر بازمی‌گردد** — این یک بررسی ایمنی برای گام پیوستن رشته و درصدرمزگذاری است. اگر بازنگردد، `GaiaBuilderException` پرتاب می‌شود.

این کار منطق بازسازیِ `DLSyntaxParser` را بازمی‌تاباند، پس جای جداکننده‌ها و اعتبارسنجی دقیقاً همان می‌شود که تجزیه‌گر انتظار دارد.

---

## ساخت رشته‌های عنصر

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **شناسه** بی‌درنگ اعتبارسنجی می‌شود: اگر شناسهٔ کاربردِ شناخته‌شدهٔ GS1 نباشد، `ai(...)` یک `IllegalArgumentException` پرتاب می‌کند. (سازنده شناسه را پیش از تجزیه به مقدار می‌پیوندد، پس شناسهٔ ناشناخته یا بیش‌ازحد بلندی مانند `"99999"` باید همین‌جا گرفته شود — وگرنه بی‌صدا به شناسه‌ای دیگر بازنشانه‌گذاری می‌شد.) اما **مقدار** دیرتر، هنگام ساخت، اعتبارسنجی می‌شود.
- مقادیر باید **کامل** باشند، از جمله رقم کنترل. سازنده رقم کنترل را نه محاسبه می‌کند و نه به‌جای شما می‌افزاید — نگاه کنید به [ارقام کنترل](#ارقام-کنترل).
- شناسه‌ها به همان ترتیبی که می‌افزایید بیرون داده می‌شوند. سازنده جداکننده‌های FNC1 را در جایی که ساختار GS1 می‌طلبد می‌گذارد؛ خودتان آن‌ها را نیفزایید.
- ساختن **بدون هیچ شناسه‌ای** یک `GaiaBuilderException("No AIs supplied")` با فهرست تهیِ `getErrors()` پرتاب می‌کند — تنها شکستی که هیچ `GaiaError`‌ای با خود ندارد.
- شناسه‌ای که مقدارش در قاعدهٔ قالب یا رقم کنترلش شکست بخورد، همهٔ ساخت را ناکام می‌گذارد.

### شناسه‌های صفتی به کلید شناسایی خود نیاز دارند

بیشتر شناسه‌ها *صفت*‌اند که GS1 General Specifications همراهی کلید شناسایی را برایشان الزامی می‌داند، و سازنده این را اعمال می‌کند — از سراسر مرحلهٔ نحو اعتبارسنجی می‌کند و هیچ راه گریزی نیست. یک بچ/لات یا شمارهٔ سریالِ تنها، رشتهٔ عنصر معتبری **نیست**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

کلیدهای شناسایی (GTIN `01`، SSCC `00`، GLN `414`، …) و شناسه‌های درون‌شرکتی (`90`–`99`) کاملاً به‌حق می‌توانند تنها بایستند. هر چیز دیگری به همراهی نیاز دارد.

> می‌توان به `GaiaParser` گفت این بررسی را با `ParseConfig.skipRequiresCheck(true)` رد کند؛ اما `GaiaBuilder` آگاهانه معادلی برای آن ندارد — هدفش برساختنِ خروجیِ سازگار با استاندارد است. برای پیوستن رشتهٔ عنصری که آگاهانه ناقص است، خودتان آن را بپیوندید و با بررسیِ خاموش تجزیه‌اش کنید.

---

## ساخت نشانی‌های Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Digital Link معتبر دقیقاً به یک **کلید شناسایی اصلی** نیاز دارد (مانند GTIN `01`، GLN `414`، SSCC `00`). سازنده هر شناسه‌ای را که می‌دهید رده‌بندی می‌کند:

| نقش | چگونه برساخته می‌شود | نمونه |
|------|-------------|---------|
| کلید شناسایی اصلی | بخش مسیر پس از دامنه/پیشوند | `/01/09506000134352` |
| مقیدکنندهٔ کلید (CPV `22`، بچ `10`، سریال `21`، …) | بخش‌های بعدی مسیر، **به ترتیب متعارف §4.9** (نه به ترتیبی که افزوده‌اید) | `/10/LOT-ABC` |
| صفت داده (هر چیز دیگر) | پارامترهای پرس‌وجو، **مرتب به ترتیب واژه‌نامه‌ای بر پایهٔ کلید شناسه** (§4.12) | `?17=271231` |

از آنجا که مقیدکننده‌ها هنگام برساختن دوباره مرتب می‌شوند، دادنِ آن‌ها بی‌ترتیب زیانی ندارد — `ai("21", …)` پیش از `ai("10", …)` باز هم `/10/LOT/21/SER` برساخته می‌شود. تنها *مجموعهٔ* آن‌هاست که باید مورد پذیرش کلید اصلی باشد.

مقادیر هم در مسیر و هم در پرس‌وجو درصدرمزگذاری می‌شوند.

ساخت شکست می‌خورد (`GaiaBuilderException` پرتاب می‌کند، یا `BuildResult`ی ناکام بازمی‌گرداند) هنگامی که:

- **هیچ** کلید شناسایی اصلی در میان شناسه‌ها نباشد؛
- **بیش از یک** کلید شناسایی اصلی باشد؛
- شناسه‌ای در Digital Link **ممنوع** باشد (`03`، `8014`)؛
- **رشتهٔ مقیدکننده‌های کلید** برای کلید اصلیِ برگزیده نامعتبر باشد (مقیدکننده‌ای که در پی آن کلید نمی‌آید، یا مقیدکننده‌هایی بیرون از ترتیب مجازشان).

---

## BuilderDigitalLinkConfig

برای مهار طرح، دامنه، پیشوند مسیر، پارامترهای پرس‌وجوی افزوده و پاره، یک `BuilderDigitalLinkConfig` بدهید:

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

| متد سازنده | هدف | پیش‌فرض |
|----------------|---------|---------|
| `scheme(String)` | طرح نشانی؛ باید `http` یا `https` باشد | `https` |
| `domain(String)` | مرجع بازیاب — میزبان یا `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | بخش‌های مسیر پیش از نخستین کلید اصلی؛ ممیزهای دو سر هنجار می‌شوند | *(هیچ)* |
| `baseUrl(String)` | آسان‌کننده‌ای که یک نشانی را به `scheme` + `domain` + `pathPrefix` می‌شکند | — |
| `addQueryParam(String, String)` | پارامتر پرس‌وجوی افزوده که **پس از** صفت‌های دادهٔ شناسه‌ها و به ترتیب افزودن می‌آید؛ درصدرمزگذاری می‌شود | — |
| `fragment(String)` | پارهٔ نشانی (بی‌`#` در آغاز)؛ درصدرمزگذاری می‌شود | *(هیچ)* |

`build()` پیکربندی را بی‌درنگ اعتبارسنجی می‌کند: طرحی جز `http(s)` یا دامنه‌ای تهی `IllegalArgumentException` پرتاب می‌کند.

- `BuilderDigitalLinkConfig.canonical()` (نام دیگرش `defaultConfig()`) همان پیش‌فرضِ `https://id.gs1.org` بی هیچ افزوده‌ای است — دقیقاً همان که `buildDigitalLinkUri()` بی‌آرگومان به کار می‌برد و همان که `GS1AIObject.getCanonicalDigitalLink()` می‌سازد.
- `baseUrl("http://id.example.org:8080/r")` → طرح `http`، دامنهٔ `id.example.org:8080`، پیشوند مسیر `/r`.
- پارامترهای پرس‌وجوی افزوده همیشه پس از صفت‌های برآمده از شناسه‌ها می‌آیند، پس ترتیب متعارف شناسه‌ها (§4.12) پابرجا می‌ماند.

`BuilderDigitalLinkConfig` دگرگون‌ناپذیر است؛ یک نمونهٔ یگانه را بی‌پروا بارها به کار ببرید.

---

## اعتبارسنجی و خطاها

### متدهای ساختی که استثنا پرتاب می‌کنند

`buildElementString()`، `buildDigitalLinkUri()` و `buildDigitalLinkUri(BuilderDigitalLinkConfig)` هنگامی که شناسه‌ها نتوانند خروجی درست‌ساختی بسازند، **`GaiaBuilderException`** (یک `RuntimeException` بررسی‌نشده) پرتاب می‌کنند:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- در شکست‌های **محتوا** (رقم کنترل نادرست، ناهمخوانی قالب، شناسهٔ نبوده/ممنوع)، `getErrors()` شیءهای `GaiaError` خودِ تجزیه‌گر را در بر دارد — همان شیءهایی که [در راهنمای تجزیه‌گر مستند شده‌اند](GaiaParser-Persian.md#gaiaerror).
- در شکست‌های **ساختار Digital Link** (نبود کلید اصلی، چند کلید اصلی، شناسهٔ ممنوع، رشتهٔ نامعتبرِ مقیدکننده‌های کلید)، `getErrors()` یک `GaiaError` یگانه را در بر دارد (کد `GE-L008`، `GE-L012`، `GE-L013` یا `GE-L014`) که به زبان سازنده بومی شده است.

### متدهای tryBuild\* که استثنا پرتاب نمی‌کنند

هنگامی که ورودی از کاربر می‌آید و شکست پیامدی چشم‌داشتنی و رسیدگی‌پذیر است، به‌جای مهار جریان با استثنا، از گونه‌های `tryBuild*` بهره بگیرید. آن‌ها به‌جای پرتاب، یک [`BuildResult`](#buildresult) بازمی‌گردانند:

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

| پرتاب می‌کند | پرتاب نمی‌کند |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

هر متد `tryBuild*` همان هستهٔ اعتبارسنجی را با همزادِ پرتاب‌کنندهٔ خود در میان می‌گذارد؛ تنها مرز رسیدگی به شکست فرق دارد.

### زبان پیام‌های خطا

خطاهای اعتبارسنجی محتوا از فهرست خطاهای بومی‌شده برداشته می‌شوند. برای برگزیدن زبانِ پیام‌های `GaiaError` که `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` با خود دارند، `language(...)` را فرابخوانید؛ پیش‌فرض انگلیسی است:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

این همان تنظیم `GaiaConstants.Language` است که `GaiaParser` از راه `ParseConfig` می‌پذیرد، پس سازنده و تجزیه‌گر به یک شیوه بومی می‌کنند.

پیام‌های `GaiaError` برای شکست‌های **محتوا** و شکست‌های **ساختار Digital Link** (نبود کلید اصلی، چند کلید اصلی، شناسهٔ ممنوع، رشتهٔ نامعتبرِ مقیدکننده‌های کلید) هر دو از راه فهرست خطاهای مشترک بومی می‌شوند — دستهٔ دوم کدهای `GE-L008`، `GE-L012`، `GE-L013` و `GE-L014` را به کار می‌برد.

### BuildResult

`BuildResult` (بستهٔ `tools.pantheum.gaia.result`) گونه‌ای مقدارِ دگرگون‌ناپذیر است که برایند یک فراخوانی `tryBuild*` را توصیف می‌کند:

| متد | در کامیابی | در شکست |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | رشتهٔ برساخته | `null` |
| `getMessage()` | `null` | شرح شکست |
| `getErrors()` | فهرست تهی | خطاهای اعتبارسنجی (همان‌ها که در `GaiaBuilderException.getErrors()` هستند) |

---

## ارقام کنترل

سازنده ارقام کنترل را اعتبارسنجی می‌کند اما آن‌ها را **محاسبه نمی‌کند** — مقادیر شما باید از پیش رقم کنترلشان را داشته باشند. برای محاسبهٔ یکی، از `GS1Utils.calculateCheckDigit` بهره بگیرید:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` الگوریتم استانداردِ پیمانهٔ ۱۰ در GS1 را بر ارقام داده‌شده اعمال می‌کند و رقم کنترلی از `0` تا `9` بازمی‌گرداند، یا `-1` اگر ورودی `null`، تهی یا غیرعددی باشد.

---

## امنیت در برابر نخ‌ها

`GaiaBuilder` امن برای نخ‌ها **نیست** و برای یک‌بار مصرف ساخته شده است: `create()` را فرابخوانید، شناسه‌ها را بیفزایید، یک بار بسازید. برای هر خروجی سازنده‌ای تازه بسازید؛ یک سازنده را میان نخ‌ها به اشتراک نگذارید.

`BuilderDigitalLinkConfig` (و خروجی‌های `BuildResult` آن) دگرگون‌ناپذیرند و می‌توان بی‌پروا به اشتراکشان گذاشت — در آغاز یک پیکربندی بسازید و آن را در سازنده‌های بسیار دوباره به کار ببرید.

---

## مرجع API

### `GaiaBuilder`

| متد | شرح |
|--------|-------------|
| `static GaiaBuilder create()` | سازنده‌ای تازه و تهی می‌آغازد. |
| `GaiaBuilder ai(String ai, String value)` | یک شناسه را همراه با مقدار کاملش می‌افزاید. اگر یکی از آن دو `null` باشد، یا `ai` شناسهٔ کاربردِ شناخته‌شدهٔ GS1 نباشد، `IllegalArgumentException` پرتاب می‌کند. |
| `GaiaBuilder language(GaiaConstants.Language language)` | زبان پیام‌های خطای اعتبارسنجی محتوا را تنظیم می‌کند (پیش‌فرض انگلیسی). مقدار `null` نادیده گرفته می‌شود. |
| `String buildElementString()` | یک رشتهٔ عنصر GS1 برمی‌سازد. در شکست `GaiaBuilderException` پرتاب می‌کند. |
| `String buildDigitalLinkUri()` | یک نشانی متعارف Digital Link برمی‌سازد. در شکست `GaiaBuilderException` پرتاب می‌کند. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | یک نشانی Digital Link بر پایهٔ `config` برمی‌سازد. در شکست `GaiaBuilderException` پرتاب می‌کند. |
| `BuildResult tryBuildElementString()` | ساخت رشتهٔ عنصر بی‌پرتاب استثنا. |
| `BuildResult tryBuildDigitalLinkUri()` | ساخت Digital Link متعارف بی‌پرتاب استثنا. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | ساخت Digital Link بر پایهٔ `config` بی‌پرتاب استثنا. |

### `BuilderDigitalLinkConfig`

| عضو | شرح |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | پیش‌فرضِ `https://id.gs1.org`. |
| `static Builder builder()` | سازنده‌ای تازه برای پیکربندی. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | طرح، مرجع بازیاب و پیشوند مسیر پس از بازیابی. |
| `getExtraQueryParams()` | پارامترهای پرس‌وجوی افزوده، به ترتیب افزودن. |
| `getFragment()` | پاره، یا `null`. |

### `GaiaBuilderException`

| عضو | شرح |
|--------|-------------|
| `getErrors()` | شیءهای `GaiaError` که شکست را پدید آوردند — خطاهای تجزیه‌گر در شکست محتوا، یا یک خطای ساختاریِ یگانهٔ Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). هرگز `null` نیست. |

### `BuildResult`

| عضو | شرح |
|--------|-------------|
| `isSuccess()` | اینکه ساخت کامیاب بود یا نه. |
| `getValue()` | خروجی برساخته در کامیابی؛ در شکست `null`. |
| `getMessage()` | شرح شکست در شکست؛ در کامیابی `null`. |
| `getErrors()` | خطاهای اعتبارسنجی در شکست؛ در کامیابی تهی. هرگز `null` نیست. |
| `getTiming()` | مقدار `ProcessingTiming` برای عملیات ساخت (زمان آغاز، مدت پردازش)، یا `null`. |

---

همچنین ببینید: **[GaiaParser — راهنمای توسعه‌دهنده](GaiaParser-Persian.md)** برای سمت تجزیه، مدل عنصر شناسه، مرجع خطاها، و پیوست‌های ثابت‌های شناسه و تفسیر.
