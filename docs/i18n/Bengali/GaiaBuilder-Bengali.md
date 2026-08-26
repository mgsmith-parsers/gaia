# GaiaBuilder — ডেভেলপার গাইড

## সূচিপত্র

1. [সংক্ষিপ্ত পরিচিতি](#সকষপত-পরচত)
2. [GS1 এবং General Specifications সম্পর্কে](#gs1-এব-general-specifications-সমপরক)
3. [কুইক স্টার্ট](#কইক-সটরট)
4. [এটি যেভাবে কাজ করে](#এট-যভব-কজ-কর)
5. [এলিমেন্ট স্ট্রিং তৈরি](#এলমনট-সটর-তর)
   - [বৈশিষ্ট্য-AI-এর নিজস্ব শনাক্তকরণ কী দরকার](#বশষটয-ai-এর-নজসব-শনকতকরণ-ক-দরকর)
6. [Digital Link URI তৈরি](#digital-link-uri-তর)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [যাচাই ও ত্রুটি](#যচই-ও-তরট)
   - [যেসব নির্মাণ-পদ্ধতি ব্যতিক্রম ছোড়ে](#যসব-নরমণ-পদধত-বযতকরম-ছড)
   - [যেসব tryBuild\* পদ্ধতি ব্যতিক্রম ছোড়ে না](#যসব-trybuild-পদধত-বযতকরম-ছড-ন)
   - [ত্রুটি বার্তার ভাষা](#তরট-বরতর-ভষ)
   - [BuildResult](#buildresult)
9. [চেক ডিজিট](#চক-ডজট)
10. [থ্রেড নিরাপত্তা](#থরড-নরপতত)
11. [API রেফারেন্স](#api-রফরনস)

---

## সংক্ষিপ্ত পরিচিতি

`GaiaBuilder` হল [`GaiaParser`](GaiaParser-Bengali.md)-এর বিপরীত প্রতিরূপ: এটি অ্যাপ্লিকেশন আইডেন্টিফায়ার (AI) ও মানের জোড়ার একটি সংগ্রহকে সুগঠিত GS1 **এলিমেন্ট স্ট্রিং** বা একটি **GS1 Digital Link URI**-তে রূপান্তরিত করে। আপনি AI ও তাদের পূর্ণ ডেটা মান দেন; বিল্ডার সেগুলো জুড়ে দেয়, `GaiaParser` যে ইঞ্জিন ব্যবহার করে ঠিক সেটি দিয়েই ফলাফল যাচাই করে, তারপর আউটপুট উপস্থাপন করে।

যেহেতু বিল্ডার যাচাই করে *নিজের প্রস্তাবিত আউটপুট নিজেই পার্স করে*, তাই এটি যা কিছু ফেরত দেয় তা `GaiaParser` দিয়ে নির্ঝঞ্ঝাটে পার্স হবেই — সুগঠিত কী, এ নিয়ে দুজনের মধ্যে কখনোই মতভেদ হতে পারে না।

**প্রবেশবিন্দু ক্লাস:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 এবং General Specifications সম্পর্কে

**GS1** একটি বৈশ্বিক অলাভজনক সংস্থা, যা সরবরাহ-শৃঙ্খলের শনাক্তকরণ ও তথ্য বিনিময়ের জন্য উন্মুক্ত মানদণ্ড তৈরি ও রক্ষণাবেক্ষণ করে। এর মানদণ্ড খুচরা বিক্রয়, স্বাস্থ্যসেবা, লজিস্টিকস, খাদ্যসেবা এবং আরও বহু শিল্পে ব্যবহৃত হয়; ভোক্তা প্যাকেজিংয়ের পণ্য বারকোড থেকে শুরু করে ওষুধের ডোজের ক্রমিক নম্বরভিত্তিক ট্র্যাকিং পর্যন্ত সবকিছু এর আওতাভুক্ত।

এই বিল্ডার যা কিছু বাস্তবায়ন করে তার প্রামাণিক উৎস হল **GS1 General Specifications** — একটিমাত্র নথি, যা নির্ধারণ করে:

- সমস্ত অ্যাপ্লিকেশন আইডেন্টিফায়ার (AI) কোড, তাদের ডেটা শিরোনাম, বিন্যাস ও যাচাইয়ের নিয়ম
- AI এলিমেন্ট স্ট্রিং গঠন ও এনকোড করার বাক্যরীতির নিয়ম
- বারকোড সিম্বোলজির প্রয়োজনীয়তা এবং AIM কোড আইডি বরাদ্দ
- চেক ডিজিট ও চেক ক্যারেক্টারের অ্যালগরিদম
- দুই-অঙ্কের বছর নির্ধারণ (সরণশীল-জানালা নিয়ম)
- Data Matrix, QR Code, GS1-128, GS1 DataBar এবং অন্যান্য ক্যারিয়ারের বিবরণ

GS1 General Specifications প্রতি বছর হালনাগাদ হয়। বর্তমান সংস্করণ ও সহায়ক উপকরণ এখানে পাওয়া যায়:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications-এর **রিলিজ ২৬.০ (অনুমোদিত, জানু ২০২৬)** বাস্তবায়ন করে।

GS1 Digital Link URI পরিচালিত হয় একটি সহযোগী মানদণ্ড, **GS1 Digital Link: URI Syntax** দ্বারা, যা প্রাথমিক শনাক্তকরণ কী, কী-যোগ্যকের ক্রম এবং ডেটা বৈশিষ্ট্যের এনকোডিং নির্ধারণ করে — Digital Link URI উপস্থাপনের সময় বিল্ডার এগুলোই প্রয়োগ করে:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax মানদণ্ডের **রিলিজ ১.৭.০ (অনুমোদিত, আগ ২০২৬)** বাস্তবায়ন করে।

এই নথিজুড়ে অনুচ্ছেদের উল্লেখ GS1 General Specifications-কে নির্দেশ করে (যেমন "Table 7-5", "section 7.12"), কেবল Digital Link-এর অনুচ্ছেদ-সংখ্যা (যেমন "§4.9", "§4.12") ব্যতিক্রম, যা GS1 Digital Link: URI Syntax মানদণ্ডকে নির্দেশ করে।

---

## কুইক স্টার্ট

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

কাঁচা AI লিটারেলের বদলে `GS1Constants_AICodes` ধ্রুবকগুলোই ব্যবহার করুন (দেখুন [পার্সার গাইডের পরিশিষ্ট ক](GaiaParser-Bengali.md#পরশষট-ক--ai-সটর-ধরবক)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## এটি যেভাবে কাজ করে

প্রতিটি নির্মাণ একই পথ ধরে চলে:

1. **সংযোজন** — AI/মান জোড়াগুলো জুড়ে একটি প্রস্তাবিত এলিমেন্ট স্ট্রিং তৈরি হয়। FNC1 গ্রুপ বিভাজক (`0x1D`) বসে সেই প্রতিটি AI-এর পরে যার *বিভাজক দরকার* এবং যেটি শেষ এলিমেন্ট নয়। যেসব AI-এর দৈর্ঘ্য আগে থেকেই নির্ধারিত (GTIN, তারিখ, নির্দিষ্ট-দৈর্ঘ্যের পরিমাপ) সেগুলো বিভাজক পায় না; বাকিরা পায়। (অজানা AI এই ধাপ পর্যন্ত পৌঁছায়ই না — `ai(...)` সঙ্গে সঙ্গেই সেগুলো প্রত্যাখ্যান করে; দেখুন [এলিমেন্ট স্ট্রিং তৈরি](#এলমনট-সটর-তর)।)
2. **যাচাই** — প্রস্তাবটি `GaiaParser` দিয়ে `CONTENT` মোডে পার্স করা হয়। প্রতিটি মান তার AI-এর বিন্যাস ও চেক ডিজিটের বিপরীতে পরীক্ষা করা হয়, আর কাঠামোগত নিয়ম (আবশ্যিক ও বর্জিত AI জোড়া) প্রয়োগ করা হয়। পার্স বৈধ না হলে নির্মাণ ব্যর্থ হয়।
3. **উপস্থাপনা** —
   - এলিমেন্ট স্ট্রিংয়ের জন্য যাচাই করা অবজেক্টের `toElementString()` ফেরত দেওয়া হয়।
   - Digital Link-এর জন্য প্রতিটি এলিমেন্টকে তার DL ভূমিকা দেওয়া হয় (প্রাথমিক কী, কী-যোগ্যক, বা ডেটা বৈশিষ্ট্য), কী-যোগ্যক অনুক্রম যাচাই হয়, URI নির্গত হয়, তারপর **নির্গত URI-টি আবার পার্স করে নিশ্চিত করা হয় যে সেটি বৈধ Digital Link হিসেবেই ফিরে আসে** — স্ট্রিং সংযোজন ও শতকরা-এনকোডিং ধাপের জন্য এটি একটি নিরাপত্তা-পরীক্ষা। ফিরে না এলে `GaiaBuilderException` ছোড়া হয়।

এটি `DLSyntaxParser`-এর পুনর্গঠন-যুক্তিরই প্রতিফলন, ফলে বিভাজকের অবস্থান ও যাচাই ঠিক তেমনই থাকে যেমনটি পার্সার প্রত্যাশা করে।

---

## এলিমেন্ট স্ট্রিং তৈরি

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** সঙ্গে সঙ্গেই যাচাই হয়: সেটি চেনা কোনো GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার না হলে `ai(...)` একটি `IllegalArgumentException` ছোড়ে। (বিল্ডার পার্সিংয়ের আগেই AI-কে মানের সঙ্গে জুড়ে দেয়, তাই `"99999"`-এর মতো অজানা বা অতিরিক্ত লম্বা AI এখানেই ধরা পড়া দরকার — নইলে সেটি নীরবেই ভিন্ন কোনো AI হিসেবে পুনরায় টোকেনায়িত হয়ে যেত।) **মান** যাচাই হয় পরে, নির্মাণের সময়ে।
- মান অবশ্যই **পূর্ণ** হতে হবে, চেক ডিজিটসহ। বিল্ডার আপনার হয়ে চেক ডিজিট গণনাও করে না, জুড়েও দেয় না — দেখুন [চেক ডিজিট](#চক-ডজট)।
- AI যে ক্রমে যোগ করেন সেই ক্রমেই নির্গত হয়। GS1-এর কাঠামো যেখানে দাবি করে সেখানে বিল্ডার নিজেই FNC1 বিভাজক বসিয়ে দেয়; সেগুলো নিজে যোগ করবেন না।
- **কোনো AI ছাড়াই** নির্মাণ করলে খালি `getErrors()` তালিকাসহ `GaiaBuilderException("No AIs supplied")` ছোড়া হয় — একমাত্র এই ব্যর্থতাই কোনো `GaiaError` বহন করে না।
- যে AI-এর মান তার বিন্যাস বা চেক ডিজিটের নিয়মে ব্যর্থ হয়, সেটি নির্মাণকেই ব্যর্থ করে দেয়।

### বৈশিষ্ট্য-AI-এর নিজস্ব শনাক্তকরণ কী দরকার

অধিকাংশ AI-ই *বৈশিষ্ট্য*, যাদের সঙ্গে GS1 General Specifications একটি শনাক্তকরণ কী থাকা আবশ্যিক করে, আর বিল্ডার সেটি প্রয়োগ করে — এটি পুরো বাক্যরীতি পর্যায়ের মধ্য দিয়েই যাচাই করে, এর কোনো ফাঁকফোকর নেই। একা একটি ব্যাচ/লট বা ক্রম নম্বর বৈধ এলিমেন্ট স্ট্রিং **নয়**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

শনাক্তকরণ কী (GTIN `01`, SSCC `00`, GLN `414`, …) এবং কোম্পানি-অভ্যন্তরীণ AI (`90`–`99`) পুরোপুরি বৈধভাবেই একা দাঁড়াতে পারে। বাকি সবকিছুরই একজন সঙ্গী দরকার।

> `GaiaParser`-কে `ParseConfig.skipRequiresCheck(true)` দিয়ে এই পরীক্ষা এড়িয়ে যেতে বলা যায়; `GaiaBuilder` ইচ্ছাকৃতভাবেই এর কোনো সমতুল্য দেয় না — এর উদ্দেশ্য মানদণ্ড-সঙ্গত আউটপুট তৈরি করা। ইচ্ছাকৃতভাবে অসম্পূর্ণ কোনো এলিমেন্ট স্ট্রিং জুড়তে চাইলে সেটি নিজেই জুড়ুন এবং পরীক্ষা বন্ধ রেখে পার্স করুন।

---

## Digital Link URI তৈরি

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

বৈধ Digital Link-এর জন্য ঠিক একটি **প্রাথমিক শনাক্তকরণ কী** দরকার (যেমন GTIN `01`, GLN `414`, SSCC `00`)। আপনার দেওয়া প্রতিটি AI-কে বিল্ডার শ্রেণিবদ্ধ করে:

| ভূমিকা | কীভাবে উপস্থাপিত হয় | উদাহরণ |
|------|-------------|---------|
| প্রাথমিক শনাক্তকরণ কী | ডোমেইন/উপসর্গের পরের পথ-খণ্ড | `/01/09506000134352` |
| কী-যোগ্যক (CPV `22`, ব্যাচ `10`, ক্রম নম্বর `21`, …) | পরবর্তী পথ-খণ্ডগুলো, **§4.9-এর আদর্শ ক্রমে** (আপনার যোগ করার ক্রমে নয়) | `/10/LOT-ABC` |
| ডেটা বৈশিষ্ট্য (বাকি সবকিছু) | কোয়েরি প্যারামিটার, **AI কী অনুসারে অভিধানক্রমে সাজানো** (§4.12) | `?17=271231` |

যেহেতু উপস্থাপনের সময় যোগ্যকগুলো নতুন করে সাজানো হয়, তাই সেগুলো ক্রমের বাইরে দিলেও ক্ষতি নেই — `ai("10", …)`-এর আগে `ai("21", …)` দিলেও উপস্থাপনা হবে `/10/LOT/21/SER`। প্রাথমিক কী-এর কাছে কেবল তাদের *সমষ্টিটিই* গ্রহণযোগ্য হতে হবে।

পথ ও কোয়েরি — উভয়েরই মান শতকরা-এনকোড করা হয়।

নির্মাণ ব্যর্থ হয় (`GaiaBuilderException` ছোড়ে, বা ব্যর্থ একটি `BuildResult` ফেরত দেয়) যখন:

- AI-গুলোর মধ্যে **কোনো** প্রাথমিক শনাক্তকরণ কী নেই;
- **একাধিক** প্রাথমিক শনাক্তকরণ কী আছে;
- কোনো AI Digital Link-এ **নিষিদ্ধ** (`03`, `8014`);
- বেছে নেওয়া প্রাথমিক কী-এর জন্য **কী-যোগ্যক অনুক্রম** অবৈধ (এমন কোনো যোগ্যক যা সেই কী-এর সঙ্গে আসে না, বা যোগ্যকগুলো তাদের অনুমোদিত ক্রমের বাইরে)।

---

## BuilderDigitalLinkConfig

স্কিম, ডোমেইন, পথ-উপসর্গ, বাড়তি কোয়েরি প্যারামিটার ও ফ্র্যাগমেন্ট নিয়ন্ত্রণ করতে একটি `BuilderDigitalLinkConfig` দিন:

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

| বিল্ডার পদ্ধতি | উদ্দেশ্য | ডিফল্ট |
|----------------|---------|---------|
| `scheme(String)` | URI স্কিম; অবশ্যই `http` বা `https` হতে হবে | `https` |
| `domain(String)` | রিজলভার কর্তৃপক্ষ — হোস্ট বা `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | প্রথম প্রাথমিক কী-এর আগের পথ-খণ্ড; দুই প্রান্তের স্ল্যাশ নিয়মিত করা হয় | *(কিছু নেই)* |
| `baseUrl(String)` | সুবিধা, যা একটি URL-কে `scheme` + `domain` + `pathPrefix`-এ ভেঙে দেয় | — |
| `addQueryParam(String, String)` | বাড়তি কোয়েরি প্যারামিটার, AI ডেটা বৈশিষ্ট্যের **পরে**, সন্নিবেশের ক্রমে যুক্ত হয়; শতকরা-এনকোড করা | — |
| `fragment(String)` | URI ফ্র্যাগমেন্ট (শুরুতে `#` ছাড়া); শতকরা-এনকোড করা | *(কিছু নেই)* |

`build()` বিন্যাসটি সঙ্গে সঙ্গেই যাচাই করে: `http(s)` নয় এমন স্কিম বা খালি ডোমেইন `IllegalArgumentException` ছোড়ে।

- `BuilderDigitalLinkConfig.canonical()` (উপনাম `defaultConfig()`) হল কোনো বাড়তি ছাড়াই ডিফল্ট `https://id.gs1.org` — আর্গুমেন্টবিহীন `buildDigitalLinkUri()` ঠিক এটিই ব্যবহার করে, আর `GS1AIObject.getCanonicalDigitalLink()` ঠিক এটিই তৈরি করে।
- `baseUrl("http://id.example.org:8080/r")` → স্কিম `http`, ডোমেইন `id.example.org:8080`, পথ-উপসর্গ `/r`।
- বাড়তি কোয়েরি প্যারামিটার সবসময় AI থেকে উদ্ভূত বৈশিষ্ট্যগুলোর পরেই আসে, ফলে আদর্শ AI ক্রম (§4.12) অক্ষুণ্ন থাকে।

`BuilderDigitalLinkConfig` অপরিবর্তনীয়; একটিমাত্র দৃষ্টান্ত নির্দ্বিধায় বারবার ব্যবহার করুন।

---

## যাচাই ও ত্রুটি

### যেসব নির্মাণ-পদ্ধতি ব্যতিক্রম ছোড়ে

AI-গুলো যখন সুগঠিত কোনো আউটপুট গঠন করতে পারে না, তখন `buildElementString()`, `buildDigitalLinkUri()` ও `buildDigitalLinkUri(BuilderDigitalLinkConfig)` একটি **`GaiaBuilderException`** (অপরীক্ষিত `RuntimeException`) ছোড়ে:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **বিষয়বস্তুর** ব্যর্থতায় (ভুল চেক ডিজিট, বিন্যাসের অমিল, অনুপস্থিত/নিষিদ্ধ AI) `getErrors()` পার্সারের `GaiaError` অবজেক্ট বহন করে — ঠিক সেই অবজেক্টগুলোই, যেগুলো [পার্সার গাইডে নথিভুক্ত](GaiaParser-Bengali.md#gaiaerror)।
- **Digital Link কাঠামোর** ব্যর্থতায় (প্রাথমিক কী নেই, একাধিক প্রাথমিক কী, নিষিদ্ধ AI, অবৈধ কী-যোগ্যক অনুক্রম) `getErrors()` বিল্ডারের ভাষায় স্থানীয়কৃত একটিমাত্র `GaiaError` বহন করে (কোড `GE-L008`, `GE-L012`, `GE-L013` বা `GE-L014`)।

### যেসব tryBuild\* পদ্ধতি ব্যতিক্রম ছোড়ে না

ইনপুট যখন ব্যবহারকারীর কাছ থেকে আসে আর ব্যর্থতা একটি প্রত্যাশিত, সামলানো-যোগ্য ফলাফল, তখন ব্যতিক্রম দিয়ে নিয়ন্ত্রণ-প্রবাহ চালানোর বদলে `tryBuild*` রূপগুলো ব্যবহার করুন। এগুলো ছোড়ার বদলে একটি [`BuildResult`](#buildresult) ফেরত দেয়:

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

| ছোড়ে | ছোড়ে না |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

প্রতিটি `tryBuild*` পদ্ধতি তার ছোড়া-যমজের সঙ্গে একই যাচাই-কেন্দ্র ভাগ করে নেয়; কেবল ব্যর্থতার সীমানাটুকুই আলাদা।

### ত্রুটি বার্তার ভাষা

বিষয়বস্তু যাচাইয়ের ত্রুটি আসে স্থানীয়কৃত ত্রুটি-তালিকা থেকে। `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` যেসব `GaiaError` বার্তা বহন করে, তাদের ভাষা বেছে নিতে `language(...)` ডাকুন; ডিফল্ট হল ইংরেজি:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

এটি সেই একই `GaiaConstants.Language` সেটিং যা `GaiaParser` `ParseConfig`-এর মাধ্যমে গ্রহণ করে, ফলে বিল্ডার ও পার্সার একইভাবেই স্থানীয়করণ করে।

**বিষয়বস্তু** ও **Digital Link কাঠামো** (প্রাথমিক কী নেই, একাধিক প্রাথমিক কী, নিষিদ্ধ AI, অবৈধ কী-যোগ্যক অনুক্রম) — উভয় ব্যর্থতারই `GaiaError` বার্তা অভিন্ন ত্রুটি-তালিকার মাধ্যমে স্থানীয়কৃত হয়; শেষোক্তটি `GE-L008`, `GE-L012`, `GE-L013` ও `GE-L014` কোড ব্যবহার করে।

### BuildResult

`BuildResult` (প্যাকেজ `tools.pantheum.gaia.result`) একটি অপরিবর্তনীয় মান-প্রকার, যা কোনো `tryBuild*` আহ্বানের ফলাফল বর্ণনা করে:

| পদ্ধতি | সফল হলে | ব্যর্থ হলে |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | উপস্থাপিত স্ট্রিং | `null` |
| `getMessage()` | `null` | ব্যর্থতার বিবরণ |
| `getErrors()` | খালি তালিকা | যাচাইয়ের ত্রুটি (`GaiaBuilderException.getErrors()`-এর মতোই) |

---

## চেক ডিজিট

বিল্ডার চেক ডিজিট যাচাই করে, কিন্তু **গণনা করে না** — আপনার মানে চেক ডিজিট আগে থেকেই থাকতে হবে। একটি গণনা করতে `GS1Utils.calculateCheckDigit` ব্যবহার করুন:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` দেওয়া অঙ্কগুলোর উপর আদর্শ GS1 মডুলো-১০ অ্যালগরিদম প্রয়োগ করে এবং `0`–`9`-এর মধ্যে একটি চেক ডিজিট ফেরত দেয়; ইনপুট `null`, খালি বা অ-সাংখ্যিক হলে `-1` ফেরত দেয়।

---

## থ্রেড নিরাপত্তা

`GaiaBuilder` থ্রেড-নিরাপদ **নয়** এবং এটি একবার ব্যবহারের জন্যই তৈরি: `create()` ডাকুন, AI যোগ করুন, একবার নির্মাণ করুন। প্রতিটি আউটপুটের জন্য নতুন বিল্ডার গড়ুন; একটিমাত্র বিল্ডার একাধিক থ্রেডে ভাগ করে নেবেন না।

`BuilderDigitalLinkConfig` (এবং তার `BuildResult` আউটপুট) অপরিবর্তনীয় এবং নির্দ্বিধায় ভাগ করে নেওয়া যায় — শুরুতে একটি বিন্যাস গড়ে বহু বিল্ডারে সেটিই বারবার ব্যবহার করুন।

---

## API রেফারেন্স

### `GaiaBuilder`

| পদ্ধতি | বিবরণ |
|--------|-------------|
| `static GaiaBuilder create()` | নতুন, খালি একটি বিল্ডার শুরু করে। |
| `GaiaBuilder ai(String ai, String value)` | একটি AI ও তার পূর্ণ মান যোগ করে। দুটির কোনোটি `null` হলে, বা `ai` চেনা কোনো GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার না হলে `IllegalArgumentException` ছোড়ে। |
| `GaiaBuilder language(GaiaConstants.Language language)` | বিষয়বস্তু যাচাইয়ের ত্রুটি বার্তার ভাষা নির্ধারণ করে (ডিফল্ট ইংরেজি)। `null` উপেক্ষিত হয়। |
| `String buildElementString()` | একটি GS1 এলিমেন্ট স্ট্রিং উপস্থাপন করে। ব্যর্থতায় `GaiaBuilderException` ছোড়ে। |
| `String buildDigitalLinkUri()` | একটি আদর্শ Digital Link URI উপস্থাপন করে। ব্যর্থতায় `GaiaBuilderException` ছোড়ে। |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` অনুসারে একটি Digital Link URI উপস্থাপন করে। ব্যর্থতায় `GaiaBuilderException` ছোড়ে। |
| `BuildResult tryBuildElementString()` | ব্যতিক্রম না ছোড়া এলিমেন্ট স্ট্রিং নির্মাণ। |
| `BuildResult tryBuildDigitalLinkUri()` | ব্যতিক্রম না ছোড়া আদর্শ Digital Link নির্মাণ। |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` অনুসারে ব্যতিক্রম না ছোড়া Digital Link নির্মাণ। |

### `BuilderDigitalLinkConfig`

| সদস্য | বিবরণ |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | ডিফল্ট `https://id.gs1.org`। |
| `static Builder builder()` | নতুন একটি বিন্যাস বিল্ডার। |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | সমাধান করা স্কিম, রিজলভার কর্তৃপক্ষ ও পথ-উপসর্গ। |
| `getExtraQueryParams()` | বাড়তি কোয়েরি প্যারামিটার, সন্নিবেশের ক্রমে। |
| `getFragment()` | ফ্র্যাগমেন্ট, বা `null`। |

### `GaiaBuilderException`

| সদস্য | বিবরণ |
|--------|-------------|
| `getErrors()` | যেসব `GaiaError` অবজেক্ট ব্যর্থতা ঘটিয়েছে — বিষয়বস্তুর ব্যর্থতায় পার্সারের ত্রুটি, বা একটিমাত্র Digital Link কাঠামোগত ত্রুটি (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`)। কখনোই `null` নয়। |

### `BuildResult`

| সদস্য | বিবরণ |
|--------|-------------|
| `isSuccess()` | নির্মাণ সফল হয়েছে কি না। |
| `getValue()` | সফল হলে উপস্থাপিত আউটপুট; ব্যর্থ হলে `null`। |
| `getMessage()` | ব্যর্থ হলে ব্যর্থতার বিবরণ; সফল হলে `null`। |
| `getErrors()` | ব্যর্থ হলে যাচাইয়ের ত্রুটি; সফল হলে খালি। কখনোই `null` নয়। |
| `getTiming()` | নির্মাণ-ক্রিয়ার `ProcessingTiming` (শুরুর সময়, প্রক্রিয়াকরণের সময়কাল), বা `null`। |

---

আরও দেখুন: পার্সিংয়ের দিক, AI এলিমেন্ট মডেল, ত্রুটি রেফারেন্স, এবং AI ও ব্যাখ্যা ধ্রুবকের পরিশিষ্টের জন্য **[GaiaParser — ডেভেলপার গাইড](GaiaParser-Bengali.md)**।
