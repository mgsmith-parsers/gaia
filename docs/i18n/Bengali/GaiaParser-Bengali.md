# GAIA (GS1 Application Identifiers Analyser) — ডেভেলপার গাইড

## সূচিপত্র

1. [সংক্ষিপ্ত পরিচিতি](#সকষপত-পরচত)
2. [GS1 এবং General Specifications সম্পর্কে](#gs1-এব-general-specifications-সমপরক)
3. [GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার](#gs1-অযপলকশন-আইডনটফযর)
4. [কুইক স্টার্ট](#কইক-সটরট)
5. [পার্সিং পাইপলাইন](#পরস-পইপলইন)
   - [পূর্ব-পর্যায় — ইনপুট মডিফায়ার](#পরব-পরযয--ইনপট-মডফযর)
   - [পর্যায় ০ — সহসম্বন্ধ আইডি](#পরযয-০--সহসমবনধ-আইড)
   - [পর্যায় ১ — ইনপুট রাউটিং](#পরযয-১--ইনপট-রউট)
   - [পর্যায় ২ — বাক্যরীতি](#পরযয-২--বকযরত)
   - [পর্যায় ৩ — বিষয়বস্তু](#পরযয-৩--বষযবসত)
   - [পর্যায় ৪ — ব্যাখ্যা](#পরযয-৪--বযখয)
6. [পার্স বিন্যাস (`ParseConfig`)](#পরস-বনযস-parseconfig)
   - [বিকল্পসমূহ](#বকলপসমহ)
   - [স্থানীয়কৃত বার্তা ও লেবেল](#সথনযকত-বরত-ও-লবল)
   - [তারিখ বিন্যাসকরণ](#তরখ-বনযসকরণ)
7. [ইনপুট মডিফায়ার](#ইনপট-মডফযর)
   - [অন্তর্নির্মিত মডিফায়ার](#অনতরনরমত-মডফযর)
   - [একটি মডিফায়ার লেখা](#একট-মডফযর-লখ)
   - [মডিফায়ার নিবন্ধন](#মডফযর-নবনধন)
   - [মডিফায়ার কী করেছে তা পরখ করা](#মডফযর-ক-করছ-ত-পরখ-কর)
   - [মডিফায়ারের ব্যর্থতা সামলানো](#মডফযরর-বযরথত-সমলন)
8. [পার্স মোড](#পরস-মড)
   - [DATA_CARRIER মোড](#data_carrier-মড)
   - [SYNTAX মোড](#syntax-মড)
   - [CONTENT মোড](#content-মড)
   - [INTERPRETATION মোড (ডিফল্ট)](#interpretation-মড-ডফলট)
9. [সহসম্বন্ধ আইডি](#সহসমবনধ-আইড)
10. [GS1 Digital Link](#gs1-digital-link)
11. [ফলাফল নিয়ে কাজ করা](#ফলফল-নয-কজ-কর)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry ও DataCarrierType](#datacarrierentry-ও-datacarriertype)
12. [ত্রুটি রেফারেন্স](#তরট-রফরনস)
13. [থ্রেড নিরাপত্তা](#থরড-নরপতত)
14. [পরিশিষ্ট ক — AI স্ট্রিং ধ্রুবক](#পরশষট-ক--ai-সটর-ধরবক)
    - [শনাক্তকরণ ও ক্রমায়ন](#শনকতকরণ-ও-করমযন)
    - [তারিখ ও সময়](#তরখ-ও-সময)
    - [পরিমাণ ও পরিমাপ — পরিবর্তনশীল পরিমাপ (মেট্রিক)](#পরমণ-ও-পরমপ--পরবরতনশল-পরমপ-মটরক)
    - [পরিমাণ ও পরিমাপ — পরিবর্তনশীল পরিমাপ (ইম্পেরিয়াল / মার্কিন)](#পরমণ-ও-পরমপ--পরবরতনশল-পরমপ-ইমপরযল--মরকন)
    - [মূল্য নির্ধারণ ও আর্থিক পরিমাণ](#মলয-নরধরণ-ও-আরথক-পরমণ)
    - [অবস্থান ও চালান](#অবসথন-ও-চলন)
    - [পণ্যের বৈশিষ্ট্য ও অনুসরণযোগ্যতা](#পণযর-বশষটয-ও-অনসরণযগযত)
    - [জাতীয় স্বাস্থ্য পরিশোধ নম্বর (NHRN)](#জতয-সবসথয-পরশধ-নমবর-nhrn)
    - [স্বাস্থ্যসেবা, GMN, HIDRI, CPID ও ব্যক্তি-সংক্রান্ত ডেটা](#সবসথযসব-gmn-hidri-cpid-ও-বযকত-সকরনত-ডট)
    - [অভ্যন্তরীণ / কোম্পানির ব্যবহার](#অভযনতরণ--কমপনর-বযবহর)
15. [পরিশিষ্ট খ — ব্যাখ্যা কী ধ্রুবক](#পরশষট-খ--বযখয-ক-ধরবক)
    - [তারিখ ও সময়](#তরখ-ও-সময)
    - [ফসল তোলার তারিখ](#ফসল-তলর-তরখ)
    - [GS1 কোম্পানি প্রিফিক্স](#gs1-কমপন-পরফকস)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [দেশ (ISO 3166)](#দশ-iso-3166)
    - [মুদ্রা (ISO 4217)](#মদর-iso-4217)
    - [তাপমাত্রা](#তপমতর)
    - [লিঙ্গ (ISO 5218)](#লঙগ-iso-5218)
    - [জলজ প্রজাতি (FAO)](#জলজ-পরজত-fao)
    - [NATO স্টক নম্বর (NSN)](#nato-সটক-নমবর-nsn)
    - [রোল পণ্য](#রল-পণয)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM শনাক্তকারী (EID / ICCID)](#sim-শনকতকর-eid--iccid)
    - [সার্টিফিকেশন রেফারেন্স](#সরটফকশন-রফরনস)
    - [GS1 UIC](#gs1-uic)
    - [শিশুর জন্মক্রম](#শশর-জনমকরম)
    - [গ্লোবাল মডেল নম্বর (GMN)](#গলবল-মডল-নমবর-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [দশমিক ও পরিমাপের মান](#দশমক-ও-পরমপর-মন)
    - [ভৌগোলিক স্থানাঙ্ক](#ভগলক-সথনঙক)
    - [উৎপাদন পদ্ধতি](#উৎপদন-পদধত)
    - [AIDC মাধ্যমের ধরন](#aidc-মধযমর-ধরন)
    - [মোটের মধ্যে টুকরা](#মটর-মধয-টকর)
    - [উপাদান বিভাজন](#উপদন-বভজন)
    - [বিবিধ](#ববধ)

---

## সংক্ষিপ্ত পরিচিতি

`GaiaParser` হল GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার (AI) এলিমেন্ট স্ট্রিং পার্স করার প্রবেশবিন্দু। এটি স্ক্যানারের কাঁচা আউটপুট নিচের যেকোনো রূপে গ্রহণ করে এবং একটি কাঠামোবদ্ধ `ParseResult` ফেরত দেয়, যাতে থাকে সমাধান করা সব AI, যাচাইয়ের ত্রুটি, এবং ঐচ্ছিকভাবে মানুষের পাঠযোগ্য ব্যাখ্যা:

- সাধারণ AI এলিমেন্ট স্ট্রিং: `0109506000134352`
- AIM সিম্বোলজি আইডেন্টিফায়ার দিয়ে উপসর্গযুক্ত এলিমেন্ট স্ট্রিং: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- উপরের যেকোনোটি, ঐচ্ছিকভাবে ৮-অঙ্কের সহসম্বন্ধ আইডি দিয়ে উপসর্গযুক্ত: `12345678~0109506000134352`

**প্রবেশবিন্দু ক্লাস:** `tools.pantheum.gaia.GaiaParser`

> **Gaia-তে নতুন?** **[GaiaParser কুইক স্টার্ট](GaiaParser-QuickStart-Bengali.md)** দিয়ে শুরু করুন — দশ মিনিটে নির্ভরতা, প্রথম পার্সিং এবং সবচেয়ে পরিচিত কয়েকটি ফাঁদ। এই গাইডটি হল সম্পূর্ণ রেফারেন্স।

> এর বিপরীত দিক — AI/মান জোড়া থেকে বৈধ এলিমেন্ট স্ট্রিং ও Digital Link URI *তৈরি করা* — **[GaiaBuilder — ডেভেলপার গাইড](GaiaBuilder-Bengali.md)**-এ আলোচিত হয়েছে।

---

## GS1 এবং General Specifications সম্পর্কে

**GS1** একটি বৈশ্বিক অলাভজনক সংস্থা, যা সরবরাহ-শৃঙ্খলের শনাক্তকরণ ও তথ্য বিনিময়ের জন্য উন্মুক্ত মানদণ্ড তৈরি ও রক্ষণাবেক্ষণ করে। এর মানদণ্ড খুচরা বিক্রয়, স্বাস্থ্যসেবা, লজিস্টিকস, খাদ্যসেবা এবং আরও বহু শিল্পে ব্যবহৃত হয়; ভোক্তা প্যাকেজিংয়ের পণ্য বারকোড থেকে শুরু করে ওষুধের ডোজের ক্রমিক নম্বরভিত্তিক ট্র্যাকিং পর্যন্ত সবকিছু এর আওতাভুক্ত।

এই পার্সার যা কিছু বাস্তবায়ন করে তার প্রামাণিক উৎস হল **GS1 General Specifications** — একটিমাত্র নথি, যা নির্ধারণ করে:

- সমস্ত অ্যাপ্লিকেশন আইডেন্টিফায়ার (AI) কোড, তাদের ডেটা শিরোনাম, বিন্যাস ও যাচাইয়ের নিয়ম
- AI এলিমেন্ট স্ট্রিং গঠন ও এনকোড করার বাক্যরীতির নিয়ম
- বারকোড সিম্বোলজির প্রয়োজনীয়তা এবং AIM কোড আইডি বরাদ্দ
- চেক ডিজিট ও চেক ক্যারেক্টারের অ্যালগরিদম
- দুই-অঙ্কের বছর নির্ধারণ (সরণশীল-জানালা নিয়ম)
- Data Matrix, QR Code, GS1-128, GS1 DataBar এবং অন্যান্য ক্যারিয়ারের বিবরণ

GS1 General Specifications প্রতি বছর হালনাগাদ হয়। বর্তমান সংস্করণ ও সহায়ক উপকরণ এখানে পাওয়া যায়:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications-এর **রিলিজ ২৬.০ (অনুমোদিত, জানু ২০২৬)** বাস্তবায়ন করে।

GS1 Digital Link URI পরিচালিত হয় একটি সহযোগী মানদণ্ড, **GS1 Digital Link: URI Syntax** দ্বারা, যা প্রাথমিক শনাক্তকরণ কী, কী-যোগ্যকের ক্রম এবং ডেটা বৈশিষ্ট্যের এনকোডিং নির্ধারণ করে — পার্সার Digital Link ইনপুটে এগুলোই প্রয়োগ করে:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax মানদণ্ডের **রিলিজ ১.৭.০ (অনুমোদিত, আগ ২০২৬)** বাস্তবায়ন করে।

এই নথিজুড়ে অনুচ্ছেদের উল্লেখ GS1 General Specifications-কে নির্দেশ করে (যেমন "Table 7-5", "section 7.12"), কেবল Digital Link-এর অনুচ্ছেদ-সংখ্যা (যেমন "§4.9", "§4.12") ব্যতিক্রম, যা GS1 Digital Link: URI Syntax মানদণ্ডকে নির্দেশ করে।

---

## GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার

**GS1 অ্যাপ্লিকেশন আইডেন্টিফায়ার (AI)** হল একটি ছোট সাংখ্যিক উপসর্গ — দুই থেকে চার অঙ্ক — যা ঠিক তার পরে আসা ডেটার অর্থ ও বিন্যাস নির্ধারণ করে। AI-গুলো GS1 General Specifications-এ সংজ্ঞায়িত এবং সরবরাহ-শৃঙ্খলের বিস্তৃত পরিসরের ডেটা আওতাভুক্ত করে: পণ্য শনাক্তকারী, তারিখ, পরিমাণ, লট নম্বর, ক্রম নম্বর, পরিমাপ, URL এবং আরও অনেক কিছু।

### একটি AI এলিমেন্টের গঠন

প্রতিটি AI এলিমেন্ট দুটি অংশ নিয়ে গঠিত:

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

AI কোড সবসময় সাংখ্যিক। ডেটা মান ঠিক তার পরেই আসে, কোড ও মানের মাঝে কোনো বিভাজক থাকে না।

### নির্দিষ্ট-দৈর্ঘ্য বনাম পরিবর্তনশীল-দৈর্ঘ্যের AI

AI দুটি শ্রেণিতে ভাগ হয়:

| ধরন | আচরণ | উদাহরণ |
|---|---|---|
| **নির্দিষ্ট-দৈর্ঘ্য** | ঠিক নির্দিষ্টসংখ্যক অক্ষর, সবসময় সম্পূর্ণ পড়া হয় | AI `01` (GTIN) — সবসময় ১৪ অঙ্ক |
| **পরিবর্তনশীল-দৈর্ঘ্য** | ১ থেকে সর্বোচ্চ সংখ্যা পর্যন্ত; একটি GS বিভাজক বা ইনপুটের শেষে সমাপ্ত | AI `10` (ব্যাচ/লট) — ১ থেকে ২০ অক্ষরসংখ্যিক অক্ষর |

কোনো AI নির্দিষ্ট না পরিবর্তনশীল, তা কেবল GS1 বিবরণে দেওয়া তার সংজ্ঞা দিয়েই নির্ধারিত হয় — পার্সার কখনো অনুমান করে না।

### বহু-AI এলিমেন্ট স্ট্রিং

একাধিক AI একটিমাত্র এলিমেন্ট স্ট্রিংয়ে জোড়া যায়। নির্দিষ্ট-দৈর্ঘ্যের AI সরাসরি জোড়া যায়, কারণ পার্সার সবসময় ঠিক জানে কতটি অক্ষর পড়তে হবে। পরিবর্তনশীল-দৈর্ঘ্যের AI-এর পরে যখনই আরেকটি AI আসে, তখন সেটিকে অবশ্যই **GS অক্ষর** (ASCII `0x1D`, বারকোড সিম্বোলজিতে যা FNC1 নামেও পরিচিত) দিয়ে শেষ করতে হবে, যাতে পার্সার বুঝতে পারে একটি মান কোথায় শেষ হয় ও পরের AI কোড কোথায় শুরু।

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

Java স্ট্রিং লিটারেলে GS অক্ষরটি ইউনিকোড এস্কেপ `""` দিয়ে লিখুন।

### প্রচলিত AI

| AI | ডেটা শিরোনাম | বিন্যাস | মানের উদাহরণ |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, একক মুদ্রা-অঞ্চল) |
| `710` | NHRN PZN | X..20 | `12345678` |

> ৪-অঙ্কের পরিমাপ বা মূল্য AI-এর **চতুর্থ অঙ্কটি** নিহিত দশমিক স্থানের সংখ্যা এনকোড করে — `3103` মানে ৩ দশমিকসহ কিলোগ্রামে নিট ওজন (`001500` = 1.500 kg), আর `3102` একই অঙ্কগুলোকে 15.00 kg হিসেবে পড়বে। উপরের `বিন্যাস` স্তম্ভ *ডেটার* বিন্যাস দেখায়; প্রতিটি AI-এর পূর্ণ `getFormatString()`-এ AI নিজেও অন্তর্ভুক্ত থাকে (যেমন `3103`-এর জন্য `N4+N6`)।

### মানুষের পাঠযোগ্য ব্যাখ্যা (HRI)

প্রচলিত পাঠযোগ্য রূপে প্রতিটি AI কোডকে তার মানের ঠিক আগে বন্ধনীতে মুড়ে দেওয়া হয়, আর এলিমেন্টগুলোর মাঝে একটি ফাঁকা স্থান রাখা হয়:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS বিভাজক HRI-তে দেখানো হয় না। এই বিন্যাসটি `GS1AIObject.toHriString()` তৈরি করে।

### চার-অঙ্কের AI কোড

কিছু AI দুইয়ের বদলে চার অঙ্ক ব্যবহার করে। প্রথম দুই অঙ্ক AI পরিবার চিহ্নিত করে; তৃতীয় ও/অথবা চতুর্থ অঙ্ক বাড়তি অর্থ বহন করে (যেমন পরিমাপ AI-তে নিহিত দশমিক বিন্দুর অবস্থান)। পার্সার এলিমেন্ট স্ট্রিং থেকে পূর্ণ AI কোড আপনা-আপনি বের করে নেয় — আহ্বানকারী সবসময় পূর্ণ কোড নিয়েই কাজ করে (যেমন `"3102"`, শুধু `"31"` নয়)।

---

## কুইক স্টার্ট

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

> **GS বিভাজক:** বহু-AI স্ট্রিংয়ের ভেতরে পরিবর্তনশীল-দৈর্ঘ্যের AI অবশ্যই GS অক্ষর (ASCII `0x1D`) দিয়ে সীমাবদ্ধ করতে হবে। Java স্ট্রিং লিটারেলে `""` ব্যবহার করুন।

---

## পার্সিং পাইপলাইন

### পূর্ব-পর্যায় — ইনপুট মডিফায়ার

`ParseConfig`-এ যদি কোনো **ইনপুট মডিফায়ার** থাকে, সেগুলো সবার আগে চলে — সহসম্বন্ধ উপসর্গ ছাঁটার আগে, ক্যারিয়ার শনাক্তকরণের আগে, GS1 পাইপলাইনে ঢোকার আগে। প্রতিটি মডিফায়ার পরেরটির জন্য কাঁচা ইনপুট নতুন করে লেখে, আর নিচের সব পর্যায় এই শৃঙ্খলের আউটপুটের উপর কাজ করে।

ডিফল্টে কোনো মডিফায়ার বিন্যস্ত থাকে না, তাই আপনি নিজে বেছে না নেওয়া পর্যন্ত এই পূর্ব-পর্যায় কিছুই করে না। দেখুন [ইনপুট মডিফায়ার](#ইনপট-মডফযর)।

---

### পর্যায় ০ — সহসম্বন্ধ আইডি

যেকোনো GS1 প্রক্রিয়াকরণের আগে `GaiaParser` পরীক্ষা করে ইনপুটটি কোনো ঐচ্ছিক **সহসম্বন্ধ আইডি উপসর্গ** দিয়ে শুরু হয়েছে কি না: ঠিক ৮টি ASCII দশমিক অঙ্ক এবং তার পরে একটি টিল্ড (`~`), যেমন `12345678~`।

উপসর্গ থাকলে সেটি ছেঁটে ফেলা হয় এবং ফেরত দেওয়া `ParseResult`-এ `CorrelationInfo` হিসেবে সংরক্ষণ করা হয়। পরবর্তী সব পর্যায় ছাঁটা পেলোডের উপর কাজ করে। উপসর্গ না থাকলে ইনপুট অপরিবর্তিতভাবেই এগিয়ে যায়।

বিস্তারিত জানতে দেখুন [সহসম্বন্ধ আইডি](#সহসমবনধ-আইড)।

---

### পর্যায় ১ — ইনপুট রাউটিং

সহসম্বন্ধ উপসর্গ ছাঁটার পরে `GaiaParser` পরীক্ষা করে (ছাঁটা) ইনপুটটি কোনো **AIM কোড আইডি** দিয়ে শুরু হয়েছে কি না: `]` + ASCII অক্ষর + ASCII অঙ্ক আকারের তিন-অক্ষরের উপসর্গ (যেমন GS1-128-এর জন্য `]C1`, GS1 DataMatrix-এর জন্য `]d2`, GS1 DataBar / GS1 Composite-এর জন্য `]e0`)।

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

ক্যারিয়ারটি GS1 AI ধারণ করতে না পারলে (যেমন কোনো ডাক বারকোড), পার্সিং সঙ্গে সঙ্গেই `GE-D002` ত্রুটি নিয়ে থেমে যায়।

---

### পর্যায় ২ — বাক্যরীতি

এটি সবসময় চলে। দুটি উপ-ধাপে গঠিত:

**২ক. টোকেনায়ন (`AISyntaxParser`)**
- GS1 উপসর্গ সারণি (GS1 General Specifications Table 7-5) কাজে লাগিয়ে প্রথম দুই অক্ষর থেকে AI কোডের দৈর্ঘ্য পড়ে।
- নির্দিষ্ট-দৈর্ঘ্যের AI ইনপুট থেকে ঠিক নির্দিষ্টসংখ্যক বাইট পড়ে।
- পরিবর্তনশীল-দৈর্ঘ্যের AI একটি GS অক্ষর বা ইনপুটের শেষ পর্যন্ত পড়া হয়।
- বহু-উপাদান AI-এর মান-খণ্ডকে উপাদান অনুযায়ী অংশে কাটা হয়।

**২খ. কাঠামোগত যাচাই (`SyntaxValidator`)**
- পুনরাবৃত্ত AI পরীক্ষা করে (`GE-S004`)।
- আবশ্যিক AI নির্ভরতা পরীক্ষা করে; যেমন AI `02`-এর জন্য AI `37` দরকার (`GE-S005`)।
- বর্জিত AI জোড়া পরীক্ষা করে (`GE-S006`)।

এই পর্যায়ের ত্রুটিগুলো `SYNTAX_ERROR` (টোকেনায়ক) বা `INTEGRITY_ERROR` (কাঠামোগত) স্তরের। **যেকোনো একটি** ত্রুটি থাকলেই — টোকেনায়কের হোক বা কাঠামোগত — পাইপলাইন থেমে যায় এবং বিষয়বস্তু ও ব্যাখ্যার পর্যায় এড়িয়ে যাওয়া হয়।

---

### পর্যায় ৩ — বিষয়বস্তু

কেবল তখনই চলে যখন পর্যায় ২ কোনো ত্রুটি তৈরি করেনি (টোকেনায়কেরও নয়, কাঠামোগতও নয়)। এলিমেন্টভিত্তিক পাইপলাইন (প্রতিটি ধাপ কেবল তখনই চলে যখন আগেরটিতে কোনো ত্রুটি হয়নি):

| ধাপ | যাচাইকারী | ত্রুটি কোড |
|---|---|---|
| রেগেক্স পরীক্ষা | `RegexValidator` | `GE-C001` |
| উপাদানের অক্ষরসেট + বিন্যাস | `ComponentValidator` | `GE-C005` + শর্তভিত্তিক বিন্যাস কোড (`GE-C054`–`GE-C115`) |
| চেক ডিজিট / চেক ক্যারেক্টার | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| স্বনির্ধারিত অর্থগত যাচাই | `ContentValidatorRegistry` | শর্তভিত্তিক বিষয়বস্তু কোড (`GE-C116`–`GE-C170`) |

এই পর্যায়ের ত্রুটিগুলো `FORMAT_ERROR` বা `DATA_ERROR` স্তরের, একটিমাত্র ব্যতিক্রম বাদে: GS1-কী
যুক্ত AI-তে GS1 কোম্পানি প্রিফিক্সের পরীক্ষাগুলো পরামর্শমূলক এবং `WARNING` স্তর বহন করে (দেখুন
[ত্রুটি রেফারেন্স](#তরট-রফরনস)), তাই অপরিচিত কোনো কোম্পানি প্রিফিক্স নিজে থেকেই
ফলাফলকে অবৈধ করে না।

---

### পর্যায় ৪ — ব্যাখ্যা

কেবল `INTERPRETATION` মোডে চলে, আর তখনই যখন কোনো এলিমেন্ট আগের কোনো পর্যায়ের ত্রুটি বহন করে না। `InterpretationEngine` প্রতিটি এলিমেন্টকে লেবেলযুক্ত মেটাডেটা দিয়ে সমৃদ্ধ করে:

- `dd/mm/yyyy` রূপে পুনর্বিন্যস্ত তারিখ
- GTIN চেক ডিজিটের বিশ্লেষণ ও GS1 কোম্পানি প্রিফিক্স অনুসন্ধান
- ISO 3166 দেশের নাম
- ISO 4217 মুদ্রার নাম ও প্রতীক
- ডিকোড করা দশমিক পরিমাণ
- HRI (মানুষের পাঠযোগ্য ব্যাখ্যা) অংশ

ফলাফলগুলো প্রতিটি `GS1AIObjectElement`-এ `GS1AIInterpretation` এন্ট্রি হিসেবে যুক্ত হয়।

---

## পার্স বিন্যাস (`ParseConfig`)

`GaiaParser` ঠিক দুটি প্রবেশবিন্দু উন্মুক্ত করে:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` চলে **ডিফল্ট বিন্যাসে**: `INTERPRETATION` মোড, `/` বিভাজক ও চার-অঙ্কের বছরসহ লিটল-এন্ডিয়ান তারিখ (`dd/mm/yyyy`), এবং **ইংরেজি** ত্রুটি বার্তা। এর যেকোনোটি বদলাতে — পার্স মোডসহ — সাবলীল বিল্ডার দিয়ে একটি `ParseConfig` গড়ুন এবং দুই-আর্গুমেন্টের রূপটি ব্যবহার করুন।

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

বিকল্পের সব enum `GaiaConstants`-এ রয়েছে।

### বিকল্পসমূহ

| বিল্ডার পদ্ধতি | Enum (`GaiaConstants`) | ডিফল্ট | প্রভাব |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | পাইপলাইনের গভীরতা — দেখুন [পার্স মোড](#পরস-মড)। |
| `language(...)`      | `Language`      | `ENGLISH`        | ত্রুটি বার্তা, ব্যাখ্যা লেবেল **এবং** AI বিবরণের ভাষা। |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | তারিখের উপাদানের ক্রম: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`)। |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | তারিখের উপাদানগুলোর মাঝের অক্ষর: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`)। |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) বা `THREE_LETTER` (`DEC`)। |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) বা `TWO_DIGIT` (`26`)। |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | কাঠামোগত "প্রয়োজন" পরীক্ষা (`GE-S005`) এড়িয়ে যায়। |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | কাঠামোগত "বর্জন" পরীক্ষা (`GE-S006`) এড়িয়ে যায়। |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / ক্লাসের নাম | কিছু নয় | পার্সিংয়ের আগে কাঁচা ইনপুট নতুন করে লেখে এমন কোড — দুটি [অন্তর্নির্মিত মডিফায়ার](#অনতরনরমত-মডফযর) এবং আপনার লেখা যেকোনোটি। দেখুন [ইনপুট মডিফায়ার](#ইনপট-মডফযর)। |

তারিখের চারটি বিকল্প কেবল ব্যাখ্যা-সমৃদ্ধকদের তৈরি বিন্যস্ত তারিখ-স্ট্রিংকেই প্রভাবিত করে (`INTERPRETATION` মোডে); যাচাই বদলায় না। বিল্ডারের মান বাদ দেওয়া যায় — যে বিকল্প নির্ধারণ করা হয়নি (বা যাকে `null` দেওয়া হয়েছে) সেটি নিজের ডিফল্ট মানই ধরে রাখে।

### স্থানীয়কৃত বার্তা ও লেবেল

`language(...)` মানুষের পাঠযোগ্য **তিন** ধরনের লেখার ভাষা বেছে নেয়: ত্রুটি বার্তা, ব্যাখ্যা লেবেল (প্রতিটি `GS1AIInterpretation`-এর `getLabel()`), এবং AI বিবরণ (প্রতিটি `GS1AIObjectElement`-এর `getDescription()`)।

`GaiaConstants.Language`-এ **৩৫টি ভাষা** সংজ্ঞায়িত, যা বিশ্বের সবচেয়ে বেশি কথিত ভাষাগুলো আওতাভুক্ত করে: ইংরেজি, ফরাসি, স্প্যানিশ, জার্মান, ইতালীয়, পর্তুগিজ, ওলন্দাজ, পোলিশ, রুশ, ইউক্রেনীয়, চেক, সুইডিশ, চীনা, জাপানি, কোরীয়, আরবি, ইন্দোনেশীয়, হিন্দি, তুর্কি, বাংলা, উর্দু, ভিয়েতনামি, নাইজেরীয় পিজিন, মিশরীয় আরবি, মারাঠি, তেলুগু, তামিল, ক্যান্টনীয়, উ চীনা, তাগালোগ, ফারসি, হাউসা, পাঞ্জাবি, জাভানি ও সোয়াহিলি।

অনুবাদের অবস্থা (যেভাবে বিতরণ করা হয়েছে):
- **ব্যাখ্যা লেবেল** — সব ভাষার জন্য অনূদিত।
- **ত্রুটি বার্তা** — সব ভাষার জন্য অনূদিত।
- **AI বিবরণ** — ইংরেজি বাদে সব ভাষার জন্য অনূদিত। ইংরেজি আলাদা কোনো তালিকা নয়: এটি সরাসরি `gs1-application-identifiers.jsonld`-এ সেই AI-এর এন্ট্রির `description` ক্ষেত্র থেকে পড়া হয়, আর শেষ পর্যন্ত প্রতিটি AI বিবরণ এখানেই ফিরে আসে।

নাইজেরীয় পিজিন (`NIGERIAN_PIDGIN`), একটি ইংরেজি-ভিত্তিক ক্রেওল, ব্যাখ্যা লেবেল ও ত্রুটি বার্তার জন্য ইচ্ছাকৃতভাবেই ইংরেজি লেখাই পুনরায় ব্যবহার করে। AI বিবরণ এই ব্যতিক্রমেরই ব্যতিক্রম: ইংরেজি পুনরায় ব্যবহার না করে সেগুলো প্রকৃত পিজিন ভঙ্গিতে অনূদিত, কারণ AI-বিবরণের তালিকাগুলো লেবেল/বার্তার তালিকা থেকে স্বাধীনভাবে তৈরি হয়েছিল। উৎপাদন পরিবেশে ভরসা করার আগে যন্ত্র-অনুবাদগুলো মাতৃভাষীদের দিয়ে পর্যালোচনা করিয়ে নেওয়া উচিত।

কোনো ভাষার তালিকায় অনুপস্থিত যেকোনো বার্তা, লেবেল বা বিবরণ ইংরেজিতে ফিরে যায়। ডান-থেকে-বাঁয়ে লেখা ভাষাগুলো (আরবি, উর্দু, মিশরীয় আরবি, ফারসি) স্ট্রিং হিসেবে সঠিকভাবেই সংরক্ষিত; সেগুলো ডান-থেকে-বাঁয়ে প্রদর্শন করা প্রদর্শন-স্তরের দায়িত্ব।

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

ব্যাখ্যা লেবেলও একইভাবে স্থানীয়কৃত হয় (মান অপরিবর্তিত থাকে — কেবল লেবেল বদলায়):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI বিবরণও একইভাবে স্থানীয়কৃত হয় (কেবল `getTitle()`, যেমন `"GTIN"`, স্থানীয়কৃত হয় না):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### তারিখ বিন্যাসকরণ

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## ইনপুট মডিফায়ার

**ইনপুট মডিফায়ার** হল সেই কোড, যা Gaia পার্স করার আগে কাঁচা ইনপুট স্ট্রিংটিকে নতুন করে লেখে। মডিফায়ার আছে সেই ইনপুটের জন্য, যা আগে থেকেই বিকৃত হয়ে আসে — এমন স্ক্যানার যা GS বিভাজকের বদলে কোনো মুদ্রণযোগ্য অক্ষর বসায়, এমন মিডলওয়্যার যা পেলোডকে বিক্রেতা-নির্দিষ্ট উপসর্গে মুড়ে দেয়, এমন হোস্ট সিস্টেম যা সবকিছু বড় হাতের অক্ষরে বদলে ফেলে। প্রতিটি আহ্বানস্থলে প্রতিটি স্ট্রিং আগেভাগে সামলানোর (আর তার কোনো একটিতে সূক্ষ্ম ভুল করে বসার) বদলে, নিয়মিতকরণটি একবারই `ParseConfig`-এ নিবন্ধন করুন এবং সেটি প্রয়োগের ভার পার্সারের উপর ছেড়ে দিন।

মডিফায়ার চলে `GaiaParser.parse(...)`-এর একেবারে শুরুতে — সহসম্বন্ধ আইডি ছাঁটার আগে, AIM কোড আইডি শনাক্তের আগে, GS1 পাইপলাইনের আগে। এরপরের সবকিছু কেবল নতুন করে লেখা স্ট্রিংটিই দেখে। দুটি [অন্তর্নির্মিত মডিফায়ার](#অনতরনরমত-মডফযর) সহ **ডিফল্টে কিছুই বিন্যস্ত থাকে না** — প্রতিটি `ParseConfig`-এ আপনি নিজেই তা বেছে নেন।

**ইন্টারফেস:** `tools.pantheum.gaia.modifier.ModifierInterface`

### অন্তর্নির্মিত মডিফায়ার

কোর jar-এ `tools.pantheum.gaia.modifier.custom`-এর ভেতরে দুটি মডিফায়ার আসে। GS1 পেলোড সবচেয়ে বেশি যে দুই উপায়ে বিকৃত হয়ে পৌঁছায় সেগুলোই এরা সামলায় — ডেটা ভেবে নেওয়া মুদ্রিত HRI বন্ধনী, এবং অবাঞ্ছিত ফাঁকা স্থান — ফলে সাধারণ পরিস্থিতিগুলোর জন্য আলাদা ক্লাস লেখার দরকার পড়ে না:

| ক্লাস | `getName()` | যা করে |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | প্রতিটি AI-এর চারপাশের HRI বন্ধনী (`(01)…(10)…`) ছেঁটে ফেলে এবং সেগুলো যে FNC1 বিভাজক নির্দেশ করত তা ফিরিয়ে আনে। |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI এলিমেন্ট স্ট্রিং থেকে প্রতিটি ফাঁকা স্থান (`0x20`) সরিয়ে দেয়। |

এরা কোনো বিশেষ মর্যাদাবিহীন সাধারণ `ModifierInterface` বাস্তবায়ন — ঠিক আপনার নিজের লেখাগুলোর মতোই নিবন্ধিত হয়, ক্রমে সাজে, প্রতিবেদিত হয় এবং ব্যর্থ হয়:

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

দুটিই অবস্থাহীন ও থ্রেড-নিরাপদ, তাই একটিমাত্র দৃষ্টান্ত ভাগ করে নেওয়া যায়; আর বিন্যাস-ফাইলভিত্তিক ব্যবস্থার জন্য দুটিকেই পূর্ণ-যোগ্য ক্লাসের নাম দিয়ে নির্দেশ করা যায় (দেখুন [মডিফায়ার নিবন্ধন](#মডফযর-নবনধন))।

#### `ModifierRemoveAIBrackets`

GS1-এর মানুষের পাঠযোগ্য ব্যাখ্যা প্রতিটি AI-কে বন্ধনীর ভেতরে ছাপে — `(01)09521234543213(10)ABC123` — এটি নিছকই একটি মুদ্রণরীতি। HRI নির্গত করার জন্য বিন্যস্ত কোনো স্ক্যানার বা মিডলওয়্যার সেই বন্ধনীগুলো ডেটা হিসেবেই পাঠিয়ে দেয়, আর টোকেনায়কের কোনো ধারণাই থাকে না সেগুলো নিয়ে কী করবে।

বন্ধনী ছেঁটে ফেলাই কাজের অর্ধেক মাত্র। HRI-তে *পরবর্তী* AI-এর খোলা `(` বন্ধনীটিই আগের মানের শেষ চিহ্নিত করে, তাই বন্ধনীযুক্ত রূপে পরিবর্তনশীল-দৈর্ঘ্যের AI-এর FNC1 লাগে না। বন্ধনী নির্বিচারে সরিয়ে ফেলুন, আর সেই সীমানাটি উবে যায়:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

তাই মডিফায়ারটি **সেই প্রতিটি সীমানায় নতুন করে একটি FNC1 বসায় যেখানে আগের AI পরিবর্তনশীল-দৈর্ঘ্যের**, আর বন্ধনীগুলো ঠিক যা এনকোড করছিল তা-ই ফিরিয়ে আনে:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

দৈর্ঘ্য খোঁজা হয় পার্সারের নিজস্ব `AiDefinitionRegistry`-তে, ফলে কোনো হার্ড-কোড করা তালিকার বদলে প্রতিটি পরিবর্তনশীল-দৈর্ঘ্যের AI সামলানো হয়। তিনটি ক্ষেত্র ইচ্ছাকৃতভাবেই অস্পর্শ থাকে: যে মান আগে থেকেই FNC1-এ শেষ হয় (দুই রীতিই নির্গত করা উৎস দ্বিতীয় বিভাজক পায় না), যে বন্ধনীবদ্ধ কোড কোনো পরিচিত AI নয় (অজানা AI নিজের দৈর্ঘ্য সম্পর্কে কিছুই বলে না), আর স্ট্রিংয়ের শেষ AI।

এই পুনর্লিখন **অভেদসম** — নিজের আউটপুটের উপর চালালে কিছুই বদলায় না — তাই যে মিশ্র প্রবাহে কেবল কিছু ইনপুট বন্ধনীযুক্ত, সেখানেও এটি নিরাপদ।

> **সীমাবদ্ধতা।** `(` ও `)` নিজেরাই বৈধ GS1 ডেটা অক্ষর, আর ব্যবহৃত প্যাটার্নটি কেবল `\((\d{2,4})\)`। কোনো মানের ভেতরে দৈবক্রমে বন্ধনীবদ্ধ দুই-থেকে-চার অঙ্কের সংখ্যা থাকলে তারও বন্ধনী খুলে যাবে। এটি কেবল সেই উৎসে প্রয়োগ করুন যেটি HRI বন্ধনী-রীতি ব্যবহার করে, প্রকৃত বন্ধনীবদ্ধ মান ব্যবহার করে এমন উৎসে নয়।

#### `ModifierRemoveSpaces`

কিছু স্ক্যানার, মিডলওয়্যার ও লেবেল-মুদ্রণ ব্যবস্থা অন্যথায় সুগঠিত এলিমেন্ট স্ট্রিংয়ে অবাঞ্ছিত ফাঁকা স্থান ঢুকিয়ে দেয় — কোনো নির্দিষ্ট-প্রস্থের ক্ষেত্র ভরাট করতে, পাঠযোগ্য দলগুলো আলাদা করতে, বা লম্বা মান মোড়াতে। টোকেনায়ক প্রতিটি ফাঁকাকেই ডেটা গণ্য করে, ফলে যে মানের ভেতরে সেটি বসে আছে তা নষ্ট হয়, আর পরিবর্তনশীল-দৈর্ঘ্যের AI হলে তার পরের সবকিছু সরে যায়।

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

কেবল ASCII `0x20` সরানো হয়। অন্যান্য শূন্যস্থান-অক্ষর যেখানে আছে সেখানেই থাকে — যেমন ট্যাব GS1-এর এনকোডযোগ্য সেটের বাইরে, তাই পার্সার সেটিকে নীরবে ঝেঁটিয়ে ফেলার বদলে `GE-S008` হিসেবে প্রতিবেদন করে।

> **সীমাবদ্ধতা।** ফাঁকা স্থান (`0x20`) GS1-এর অপরিবর্ত অক্ষরসেটের অংশ, তাই কোনো ব্যাচ/লট বা গ্রাহকের যন্ত্রাংশ-নম্বরে বৈধভাবেই ফাঁকা থাকতে পারে। মডিফায়ারটি অবাঞ্ছিত ফাঁকা আর প্রকৃত ফাঁকার পার্থক্য করতে পারে না; এটি কেবল সেই উৎসে প্রয়োগ করুন যার AI মানের ভেতরে ফাঁকা স্থান ব্যবহার না করার বিষয়টি জানা।

#### উপসর্গ নতুন করে লেখা হয় না, এড়িয়ে যাওয়া হয়

মডিফায়ার চলে তখন, যখন পার্সার এখনো কিছুই ছাঁটেনি; তাই কাঁচা ইনপুটে তখনো সহসম্বন্ধ আইডি, AIM কোড আইডি ও ECI নির্দেশক থাকতে পারে। দুটি অন্তর্নির্মিত মডিফায়ারই পার্সারের নিজস্ব `CorrelationIdParser` ও `DataCarrierParser` যুক্তি দিয়ে AI এলিমেন্ট স্ট্রিংয়ের শুরু খুঁজে নেয়, কেবল সেখান থেকেই নতুন করে লেখে, আর ফলাফলটিকে **অস্পর্শ** উপসর্গের সঙ্গে ফিরিয়ে জোড়া লাগায়:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

যেসব EAN/UPC ক্যারিয়ারের মান GTIN-14 পর্যন্ত ভরাট করা হয় (`isRequiresGtinPadding()`), সেগুলো পুরোপুরি এড়িয়ে যাওয়া হয় — তাদের পেলোড হল AI কাঠামোবিহীন কাঁচা সাংখ্যিক বারকোড মান, তাই সেখানে বন্ধনী বা ফাঁকা স্থান কোনোটিই অর্থবহ হতে পারে না।

#### ক্রম: বন্ধনীর আগে ফাঁকা স্থান

দুটিই ব্যবহার করলে **আগে `ModifierRemoveSpaces` নিবন্ধন করুন**। বন্ধনী মেলানো অবস্থান-সংবেদনশীল: ফাঁকা দিয়ে ছড়ানো `( 01 )` `\((\d{2,4})\)`-এর সঙ্গে মেলে না, ফলে বন্ধনীগুলো টিকে যায় আর সেগুলো যে বিভাজক নির্দেশ করত তা কখনোই ফিরে আসে না।

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### একটি মডিফায়ার লেখা

দুটি অন্তর্নির্মিত মডিফায়ারের কোনোটিই না মিললে নিজেরটি লিখুন — ইন্টারফেসে পদ্ধতি মাত্র একটি।

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

পুনর্লিখন যখন পার্স বিন্যাসের উপর নির্ভর করে, তখন এর বদলে দুই-আর্গুমেন্টের রূপটি ওভাররাইড করুন:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

চুক্তি:

| নিয়ম | বিবরণ |
|---|---|
| অবস্থাহীন ও থ্রেড-নিরাপদ | প্রতিটি ক্লাসের একটি দৃষ্টান্ত ক্যাশে রাখা হয় এবং প্রতিটি পার্সে ভাগ করে নেওয়া হয়। |
| আর্গুমেন্টবিহীন সর্বজনীন কনস্ট্রাক্টর | কেবল তখনই দরকার যখন মডিফায়ারকে ক্লাসের নাম দিয়ে নির্দেশ করা হয়। |
| `null` ও ফাঁকা ইনপুট সামলান | শৃঙ্খল চলার আগে পার্সার সেগুলো ছেঁকে বাদ দেয় না। |
| `null` ফেরত মানে "কোনো পরিবর্তন নেই" | আগের মানটিই এগিয়ে যায়। মডিফায়ার প্রযোজ্য না হলে `input` অপরিবর্তিতভাবে ফেরত দিন। |
| ব্যতিক্রম ছোড়ার বদলে অপরিবর্তিত ফেরত দেওয়াই শ্রেয় | ব্যতিক্রম ছোড়া মডিফায়ার পার্সিং বাতিল করে দেয় — দেখুন [ব্যর্থতা সামলানো](#মডফযরর-বযরথত-সমলন)। |
| `getName()` | `ModifierInfo`-তে প্রতিবেদিত নাম নিয়ন্ত্রণ করতে ওভাররাইড করুন; ডিফল্ট হল সরল ক্লাসের নাম। |

### মডিফায়ার নিবন্ধন

মডিফায়ার যে ক্রমে যোগ করা হয় সেই ক্রমেই চলে, আর প্রত্যেকে আগেরটির আউটপুট পায়। এগুলো দৃষ্টান্ত দিয়ে, পূর্ণ-যোগ্য ক্লাসের নাম দিয়ে, বা যেকোনোটির তালিকা দিয়ে নিবন্ধন করুন:

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

[অন্তর্নির্মিত মডিফায়ার](#অনতরনরমত-মডফযর)গুলোরও নাম দেওয়া হয় আপনার নিজেরগুলোর মতোই — **সবসময় পূর্ণ-যোগ্য রূপে**। এদের জন্য কোনো সংক্ষিপ্ত নাম বা উপনাম-অনুসন্ধান নেই; `ModifierRegistry` প্রতিটি মডিফায়ারকেই, সঙ্গে আসুক বা না আসুক, পূর্ণ ক্লাসের নাম দিয়ে সমাধান করে।

নাম সমাধান করে `ModifierRegistry`, যা প্রতিটি ক্লাসের একটি দৃষ্টান্ত তার আর্গুমেন্টবিহীন কনস্ট্রাক্টর দিয়ে একবার তৈরি করে এবং একই ক্লাসের নামধারী পরবর্তী প্রতিটি বিন্যাসের জন্য সেটিই ক্যাশে রাখে। সমাধান ঘটে **বিন্যাস গড়ার সময়ে**, তাই যে নাম খুঁজে পাওয়া যায় না, যা `ModifierInterface` বাস্তবায়ন করে না, বা যার দৃষ্টান্ত তৈরি করা যায় না, সেটি সেখানেই `IllegalArgumentException` ছোড়ে — পার্সের সময়ে নীরবে নয়। যে মডিফায়ার রিফ্লেকশন দিয়ে গড়া যায় না (ধরুন যেটি কোনো ইনজেক্ট করা নির্ভরতা ধরে রাখে), সেটি আগেভাগে নিবন্ধন করে রাখলে নাম দিয়েই নির্দেশযোগ্য থাকে:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### মডিফায়ার কী করেছে তা পরখ করা

মডিফায়ার বিন্যস্ত থাকলে `ParseResult.getPayload()` **পরিবর্তিত** ইনপুটই প্রতিফলিত করে। মূলটি `ModifierInfo`-তে সংরক্ষিত থাকে:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` প্রতিটি মডিফায়ারের `getName()` প্রতিবেদন করে, যার ডিফল্ট সরল ক্লাসের নাম হলেও দুটি অন্তর্নির্মিত মডিফায়ারই তা ওভাররাইড করে — তাই এ দুটির শৃঙ্খল ক্লাসের নামের বদলে প্রদর্শনের নাম প্রতিবেদন করে:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

কোনো মডিফায়ার বিন্যস্ত না থাকলে `getModifierInfo()` `null` ফেরত দেয়। মডিফায়ার চললেও যদি প্রত্যেকেই ইনপুট অপরিবর্তিতভাবে ফেরত দেয়, তবে তথ্যটি উপস্থিত থাকে আর `isModified()` হয় `false` — `getAppliedModifiers()`-এ কেবল সেসব মডিফায়ারই তালিকাভুক্ত হয় যারা সত্যিই ইনপুট বদলেছে।

### মডিফায়ারের ব্যর্থতা সামলানো

ব্যতিক্রম ছোড়া মডিফায়ার পার্সিং বাতিল করে দেয়। ব্যতিক্রমটি দোষী মডিফায়ারের নামধারী একটি `GaiaModifierException`-এ মোড়ানো হয়, আর ফলাফল একটি `GE-I001` অভ্যন্তরীণ ত্রুটি বহন করে যার বার্তায় সেই নামটি থাকে; `getPayload()` অপরিবর্তিত ইনপুট প্রতিবেদন করে। পার্সিং ইচ্ছাকৃতভাবেই অর্ধ-পুনর্লিখিত স্ট্রিং নিয়ে **এগোয় না** — নীরবে ব্যর্থ হওয়া কোনো নিয়মিতকরণ ধাপ এমন ফলাফল তৈরি করত যা দেখতে বৈধ, কিন্তু ভুল ইনপুট থেকে পার্স করা।

---

## পার্স মোড

প্রতিটি মোডের নাম সে যে সবচেয়ে গভীর [পাইপলাইন পর্যায়](#পরস-পইপলইন) চালায় তার নামে; তার আগের প্রতিটি পর্যায় তবুও চলে।

| মোড | কত দূর চলে | কীসের উত্তর দেয় |
|---|---|---|
| `DATA_CARRIER` | পর্যায় ১ (ইনপুট রাউটিং) | কোন সিম্বোলজি এটি বহন করেছে? |
| `SYNTAX` | পর্যায় ২ (বাক্যরীতি) | AI কোড ও দৈর্ঘ্য কি সুগঠিত? |
| `CONTENT` | পর্যায় ৩ (বিষয়বস্তু) | মানগুলো কি বৈধ GS1 ডেটা? |
| `INTERPRETATION` | পর্যায় ৪ (ব্যাখ্যা) | মানগুলোর অর্থ কী? |

### DATA_CARRIER মোড

পর্যায় ১-এর পরে থেমে যায় — AIM কোড আইডি যাচাই করে ও সিম্বোলজি শনাক্ত করে, কিন্তু AI পার্সিং পাইপলাইনে ঢোকে না। পূর্ণ যাচাইয়ের বাড়তি ভার ছাড়াই সিম্বোলজি শনাক্তকরণ ও রাউটিংয়ের জন্য কাজে লাগে।

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

**কখন ব্যবহার করবেন:** পেলোড কীভাবে প্রক্রিয়া করবেন তা ঠিক করার আগে আপনার অ্যাপ্লিকেশনের যখন বারকোডের ধরন জানা দরকার — যেমন 1D বনাম 2D সিম্বোলজিকে আলাদা হ্যান্ডলারে পাঠানো। সেই রাউটিংয়ের জন্য `getName()`-এর স্ট্রিং মিলানোর বদলে টাইপযুক্ত [`DataCarrierType`](#datacarrierentry-ও-datacarriertype) (`getDataCarrier().getDataCarrierType()`) ব্যবহার করুন।

---

### SYNTAX মোড

পর্যায় ২-এর পরে থেমে যায়। বিষয়বস্তু যাচাইয়ের খরচ ছাড়াই কাঠামোগত প্রাক-বাছাইয়ের জন্য কাজে লাগে।

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

**কখন ব্যবহার করবেন:** পূর্ণ যাচাইয়ে নামার আগে যখন আপনি নিশ্চিত হতে চান AI কোড ও ডেটার দৈর্ঘ্য সুগঠিত কি না, অথবা যখন বিপুল পরিমাণে স্ক্যান করছেন যেখানে বিষয়বস্তুর ত্রুটি বিরল।

---

### CONTENT মোড

পর্যায় ৩-এর পরে থেমে যায়।

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

> অধিকাংশ AI একা দাঁড়াতে পারে না: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) ও
> `21` (SERIAL) — প্রত্যেকেরই একই এলিমেন্ট স্ট্রিংয়ে AI `01`-এর মতো একটি শনাক্তকরণ কী
> *প্রয়োজন*; তাই উপরের GTIN বাদ দিলে বিষয়বস্তু যাচাই পর্যন্ত পৌঁছানোর আগেই পর্যায় ২-এ
> `GE-S005` নিয়ে ব্যর্থ হবে। যেসব অংশ ইচ্ছাকৃতভাবে তাদের সঙ্গী AI ছাড়াই থাকে, সেগুলো
> পার্স করতে `ParseConfig`-এ `skipRequiresCheck(true)` নির্ধারণ করুন।

**কখন ব্যবহার করবেন:** কোনো স্ক্যান করা মান ব্যবসায়িক প্রক্রিয়ায় ব্যবহারের আগে সেটি পুরোপুরি GS1-সঙ্গত কি না জানা দরকার, অথচ ব্যাখ্যা-সমৃদ্ধকরণের বাড়তি ভার চান না।

---

### INTERPRETATION মোড (ডিফল্ট)

পর্যায় ৪ পর্যন্ত পুরো পাইপলাইন চালায়। মোড-আর্গুমেন্ট ছাড়া `parse(String)` ডাকলে এটিই ডিফল্ট। কেবল সেসব এলিমেন্টকেই সমৃদ্ধ করে যারা বিষয়বস্তু যাচাই নির্ঝঞ্ঝাটে উতরেছে।

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

**উদাহরণ আউটপুট:**
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

**আর্থিক পরিমাণের উদাহরণ (AI 3932 — ISO মুদ্রা কোডসহ মূল্য):**
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

**কখন ব্যবহার করবেন:** প্রদর্শন-স্তর, লেবেল যাচাইয়ের সরঞ্জাম, বা এমন যেকোনো UI গড়ার সময় যার AI মানের মানুষ-বান্ধব বিশ্লেষণ দরকার।

---

## সহসম্বন্ধ আইডি

কিছু কর্মপ্রবাহ কাঁচা GS1 ইনপুটের আগে একটি নিজস্ব ৮-অঙ্কের সহসম্বন্ধ শনাক্তকারী জুড়ে দেয়, যাতে স্ক্যান-ঘটনাগুলো কোনো সেশন বা লেনদেনের সঙ্গে বাঁধা যায়। এর বিন্যাস হল:

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

`~` (টিল্ড) হল বিভাজক। এটি GS1 বিষয়বস্তুর অংশ **নয়** — কোনো GS1 পার্সিং শুরু হওয়ার আগেই এটি ছেঁটে ফেলা হয়।

### শনাক্তকরণের নিয়ম

উপসর্গটি তখনই শনাক্ত হয় যখন ইনপুট ঠিক ৮টি ASCII দশমিক অঙ্ক (`0`–`9`) দিয়ে শুরু হয় এবং তার ঠিক পরেই `~` থাকে। নবম অক্ষরটি `~` না হলে, কিংবা প্রথম ৮টি অক্ষরের কোনোটি অঙ্ক না হলে, ইনপুটকে সহসম্বন্ধ উপসর্গবিহীন সাদামাটা GS1 বিষয়বস্তু হিসেবেই গণ্য করা হয়।

### সহসম্বন্ধ আইডিতে পৌঁছানো

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

### AIM কোড আইডির সঙ্গে মিলিয়ে

সহসম্বন্ধ উপসর্গ AIM কোড আইডির আগে আসতে পারে। পার্সার এটি নিঃশব্দেই সামলে নেয়:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**বাস্তবায়ন ক্লাস:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

একটি **GS1 Digital Link** এক বা একাধিক AI মানকে সরাসরি কোনো HTTP(S) URL-এর কাঠামোর ভেতরেই এনকোড করে, ফলে ভৌত পণ্যের জন্য ওয়েব-সমাধানযোগ্য শনাক্তকারী সম্ভব হয়। GAIA **অসংকুচিত** URI-এর জন্য *GS1 Digital Link Standard: URI Syntax* (রিলিজ ১.৭.০) বাস্তবায়ন করে।

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` Digital Link URI আপনা-আপনি চিনে নেয় — `http://` বা `https://` দিয়ে শুরু হওয়া প্রতিটি ইনপুট `GS1DLParser`-এ পাঠানো হয়, যা এলিমেন্ট-স্ট্রিং পাইপলাইনের মতোই একই বিষয়বস্তু ও ব্যাখ্যার পর্যায় চালায়।

### URI-এর কাঠামো ও AI-এর ভূমিকা

Digital Link URI-তে প্রতিটি AI তিনটি ভূমিকার একটি পালন করে, যা প্রতিটি `GS1AIObjectElement`-এ `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) দিয়ে পাওয়া যায়:

| ভূমিকা | অবস্থান | উদাহরণ |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | পথের প্রথম `/ai/value` জোড়া (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | তার পরের পথ-জোড়াগুলো, প্রাথমিক কী অনুযায়ী ক্রমবদ্ধ (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | সম্পূর্ণ সাংখ্যিক কী-যুক্ত কোয়েরি প্যারামিটার (§4.10) | `?17=271231` |

প্রয়োগ করা কাঠামোগত নিয়ম (`DLPathRules`):
- পথে ঠিক **একটি** প্রাথমিক শনাক্তকরণ কী; বাড়তি কী অবশ্যই কোয়েরি ডেটা বৈশিষ্ট্য হিসেবে এনকোড করতে হবে।
- কী-যোগ্যক প্রাথমিক কী দ্বারা স্বীকৃত হতে হবে এবং নির্ধারিত ক্রমে আসতে হবে। ঐচ্ছিক যোগ্যক বাদ দেওয়া যায়, তবে যেগুলো *আছে* সেগুলোকে তবুও নির্দিষ্ট ক্রম মেনে চলতে হবে — দেখুন [যোগ্যকের ক্রম](#যগযকর-করম)।
- প্রাথমিক কী-এর আগে যেকোনো স্বনির্ধারিত পথ-খণ্ড থাকতে পারে (যেমন `/products/au/01/...`); সেগুলো `getDigitalLinkInfo().getCustomPathStem()` দিয়ে পান।
- অ-সাংখ্যিক কোয়েরি কী (`linkType`, `context`, `23P`-এর মতো বর্ধিতাংশ প্যারামিটার) উপেক্ষিত হয়; সম্পূর্ণ সাংখ্যিক কী অবশ্যই `validAsDataAttribute` চিহ্নিত বৈধ AI হতে হবে।
- শতকরা-এনকোড করা মান-অক্ষর ডিকোড করা হয়; AI `(03)` ও `(8014)` অনুমোদিত নয়।

প্রাথমিক কী ও তাদের স্বীকৃত যোগ্যক-অনুক্রম হার্ড-কোড করা নয়, বরং AI সংজ্ঞা থেকে **ডেটা-চালিত** — `gs1DigitalLinkPrimaryKey` পতাকা ও `gs1DigitalLinkQualifiers` বৈশিষ্ট্য থেকে।

যেকোনো কাঠামোগত লঙ্ঘন, বা URL নয় এমন ইনপুট, একটি Digital Link কাঠামোগত ত্রুটি তৈরি করে (`GE-L001`–`GE-L014`, প্রতিটি শর্তের জন্য একটি কোড)। বিশ্লিষ্ট URL মেটাডেটা (`scheme`, `domain`, `path`, `customPathStem`, `query`, এবং `java.net.URL`) কাঠামোগত ত্রুটি থাকলেও `getDigitalLinkInfo()` দিয়ে পাওয়া যায়।

### যোগ্যকের ক্রম

প্রতিটি প্রাথমিক কী-এর জন্য `gs1DigitalLinkQualifiers` এক বা একাধিক **ক্রমবদ্ধ** যোগ্যক-অনুক্রম তালিকাভুক্ত করে। কোনো অনুক্রমের ভেতরে তৃতীয় বন্ধনীতে মোড়া AI **ঐচ্ছিক**, আর বন্ধনীবিহীন AI **আবশ্যিক** — এটি §4.9-এর ABNF-এর `[cpv-comp]` চিহ্নপদ্ধতিরই প্রতিফলন। একটিমাত্র প্রাথমিক কী-এর অনুক্রমগুলো পারস্পরিক বর্জনশীল বিকল্প।

উদাহরণস্বরূপ, GTIN (`01`) দুটি অনুক্রম সংজ্ঞায়িত করে:

| পথ | অনুক্রম | অর্থ |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — প্রত্যেকটি ঐচ্ছিক, তবে এই ক্রমেই নির্দিষ্ট |
| upui-path | `235` | TPX (আবশ্যিক); GTIN + TPX = UPUI |

সুতরাং `/01/09506000134352/10/LOT-ABC/21/SER` বৈধ (SER-এর আগে LOT, CPV বাদ), `/01/.../21/SER/10/LOT-ABC` **প্রত্যাখ্যাত** (ক্রমভঙ্গ), আর `/01/09506000134352/235/2ABC456` হল upui-path। ক্রম-পরীক্ষা একটি ক্রম-রক্ষাকারী উপ-অনুক্রম মিলানো, তাই ঐচ্ছিক AI এড়িয়ে যাওয়া গেলেও কখনোই তাদের ক্রম বদলানো যায় না।

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

**বাস্তবায়ন ক্লাস:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## ফলাফল নিয়ে কাজ করা

### ParseResult

`GaiaParser.parse()` যে সর্বোচ্চ-স্তরের ফলাফল ফেরত দেয়।

| পদ্ধতি | যা ফেরত দেয় | বিবরণ |
|---|---|---|
| `isValid()` | `boolean` | কোনো স্তরেই ত্রুটি না থাকলে `true`। সতর্কতা বৈধতাকে প্রভাবিত করে না। `getAiObject()` `null` হলে সবসময় `true`। |
| `getPayload()` | `String` | সহসম্বন্ধ উপসর্গ ছাঁটার পরে — এবং কোনো [ইনপুট মডিফায়ার](#ইনপট-মডফযর) সেটি নতুন করে লেখার পরে — ইনপুট স্ট্রিং। |
| `getPayloadContent()` | `String` | AIM কোড আইডি ও ECI উপসর্গ ছাঁটার পরের পেলোড। |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (GS1 নয় বলে প্রত্যাখ্যাত ডেটা ক্যারিয়ার, যেমন Code 39-এর `]A0` ক্যারিয়ার), বা `UNABLE_TO_DETERMINE_CONTENT` (যখন `aiObject` `null`, যেমন `DATA_CARRIER` মোডে)। |
| `getRequestedParseMode()` | `ParseMode` | বিন্যস্ত পাইপলাইনের গভীরতা (`ParseConfig.getRequestedParseMode()`)। |
| `getAchievedParseMode()` | `ParseMode` | পার্সিং আসলে যে সবচেয়ে গভীর পর্যায়ে পৌঁছেছে — নিচে দেখুন। |
| `isParseComplete()` | `boolean` | পার্সিং অনুরোধ করা গভীরতায় পৌঁছালে `true` (`achieved == requested`)। `isValid()` থেকে স্বতন্ত্র। |
| `getAiObject()` | `GS1AIObject` | সমাধান করা সব AI। `DATA_CARRIER` মোডে `null`। |
| `getErrors()` | `List<GaiaError>` | WARNING নয় এমন সব ত্রুটি (অবজেক্ট-স্তর + সব এলিমেন্ট-স্তর)। |
| `getWarnings()` | `List<GaiaError>` | সব WARNING পরামর্শ (অবজেক্ট-স্তর + সব এলিমেন্ট-স্তর)। |
| `hasWarnings()` | `boolean` | কোনো WARNING পরামর্শ উঠে থাকলে `true`। |
| `getIssues()` | `List<GaiaError>` | ত্রুটি ও সতর্কতা একত্রে। |
| `hasDataCarrier()` | `boolean` | কোনো AIM কোড আইডি চেনা গেলে `true`। |
| `getDataCarrier()` | `DataCarrierEntry` | সিম্বোলজির মেটাডেটা, বা কোনো ক্যারিয়ার শনাক্ত না হলে `null`। |
| `hasEci()` | `boolean` | পেলোড থেকে কোনো ECI নির্দেশক ছাঁটা হলে `true`। |
| `getEci()` | `EciEntry` | ECI এনকোডিংয়ের মেটাডেটা, বা `null`। |
| `hasCorrelationId()` | `boolean` | মূল ইনপুটে `DDDDDDDD~` সহসম্বন্ধ উপসর্গ থাকলে `true`। |
| `getCorrelationInfo()` | `CorrelationInfo` | নিষ্কাশিত সহসম্বন্ধ আইডি, বা না থাকলে `null`। |
| `isInputModified()` | `boolean` | কোনো [ইনপুট মডিফায়ার](#ইনপট-মডফযর) ইনপুট বদলালে `true`। |
| `getModifierInfo()` | `ModifierInfo` | মডিফায়ার শৃঙ্খল যা করেছে — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`। কোনো মডিফায়ার বিন্যস্ত না থাকলে `null`। |
| `getTiming()` | `ProcessingTiming` | পার্সের দেয়াল-ঘড়ির সময় — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`। `GaiaParser` তৈরি না করলে `null`। |
| `getVersion()` | `String` | যে লাইব্রেরি সংস্করণ এই ফলাফল তৈরি করেছে। |

#### অনুরোধ করা বনাম অর্জিত পার্স মোড

পাইপলাইন **SYNTAX → CONTENT → INTERPRETATION** সিঁড়ি বেয়ে ওঠে এবং ত্রুটিতে আগেভাগেই থেমে যায়, তাই আসলে *অর্জিত* মোড *অনুরোধ করা* মোডের চেয়ে অগভীর হতে পারে। `getAchievedParseMode()` জানায় কতদূর পৌঁছানো গেছে:

| অনুরোধ করা | যা ঘটে | অর্জিত | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | কোনো **বাক্যরীতিগত / কাঠামোগত** ত্রুটি টোকেনায়নের পরে পার্সিং থামিয়ে দেয় | `SYNTAX` | `false` |
| `INTERPRETATION` | কোনো **বিষয়বস্তু** ত্রুটি (ভুল বিন্যাস/চেক ডিজিট) সমৃদ্ধকরণ আটকে দেয় | `CONTENT` | `false` |
| `CONTENT` | বিষয়বস্তু পর্যায় সবসময় শেষ পর্যন্ত চলে (ত্রুটি টুকে রাখা হয়, প্রাণঘাতী নয়) | `CONTENT` | `true` |
| যেকোনোটি (নির্মল ইনপুট) | পাইপলাইন অনুরোধ করা গভীরতায় পৌঁছায় | = অনুরোধ করা | `true` |
| `DATA_CARRIER` | ক্যারিয়ার যাচাই হয়; AI বিষয়বস্তু পার্স হয় না | `DATA_CARRIER` | `true` |
| যেকোনোটি | AI পার্সিংয়ের আগেই ডেটা ক্যারিয়ার প্রত্যাখ্যাত (যেমন GS1 নয় এমন `]A0` ক্যারিয়ার) | `SYNTAX` | `false` |

`isParseComplete()` `isValid()` থেকে স্বতন্ত্র: ভুল চেক ডিজিটওয়ালা কোনো GTIN-এর `CONTENT` পার্সিং **সম্পূর্ণ** (বিষয়বস্তু পর্যায় চলেছে), তবু **অবৈধ** (চেক ডিজিট ব্যর্থ হয়েছে)। "পাইপলাইন কি আমার চাওয়া গভীরতা পর্যন্ত চলেছে?" জিজ্ঞাসা করতে `isParseComplete()` আর "ডেটা কি সুগঠিত?" জিজ্ঞাসা করতে `isValid()` ব্যবহার করুন।

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

সমাধান করা AI এলিমেন্টগুলোর সংগ্রহ।

| পদ্ধতি | বিবরণ |
|---|---|
| `getAis()` | সব `GS1AIObjectElement` দৃষ্টান্ত, ইনপুটের ক্রমে। |
| `get(String aiCode)` | দেওয়া AI কোডের সঙ্গে মেলা প্রথম এলিমেন্ট, বা `null`। |
| `contains(String aiCode)` | সেই কোডের AI থাকলে `true`। |
| `size()` | সমাধান করা AI-এর সংখ্যা। |
| `isValid()` | অবজেক্ট-স্তরে কোনো ত্রুটি না থাকলে এবং কোনো এলিমেন্টে ত্রুটি না থাকলে `true`। |
| `toHriString()` | HRI স্ট্রিং, যেমন `(01)09506000134352 (17)261231`। |
| `toElementString()` | কাঁচা এলিমেন্ট স্ট্রিং — বন্ধনী ছাড়া, প্রতিটি পরিবর্তনশীল-দৈর্ঘ্যের এলিমেন্টের পরে FNC1 সহ — যেমন `010950600013435210LOT-ABC<GS>17271231`। `isValid()` `false` হলে `null` ফেরত দেয়। |
| `getContentType()` | `hasDigitalLink()` সত্য হলে `GS1_DIGITAL_LINK`, নইলে `GS1_APPLICATION_IDENTIFIERS`। |
| `hasDigitalLink()` | ইনপুট যদি প্রাথমিক শনাক্তকরণ কী বহনকারী কোনো GS1 Digital Link URI হয় তবে `true`। প্রাথমিক কী ছাড়া সুগঠিত কোনো URL তবু `getDigitalLinkInfo()` দেয়, কিন্তু এখানে `false` ফেরত দেয়। |
| `getCanonicalDigitalLink()` | `https://id.gs1.org`-এ আদর্শ GS1 Digital Link URI (§4.12) — প্রাথমিক কী ও যোগ্যক পথ-খণ্ড হিসেবে, ডেটা বৈশিষ্ট্য AI কী অনুসারে সাজানো কোয়েরি প্যারামিটার হিসেবে — বা প্রাথমিক কী না থাকলে `null`। |
| `getDigitalLinkInfo()` | URI বিশ্লেষণের মেটাডেটা (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), বা Digital Link না হলে `null`। |
| `getAllErrors()` | অবজেক্ট-স্তর + সব এলিমেন্ট ত্রুটি (WARNING নয় এমন)। |
| `getAllWarnings()` | অবজেক্ট-স্তর + সব এলিমেন্ট সতর্কতা। |
| `getAllIssues()` | সবকিছু একত্রে। |

---

### GS1AIObjectElement

সমাধান করা একটিমাত্র AI দৃষ্টান্ত।

| পদ্ধতি | বিবরণ |
|---|---|
| `getAi()` | AI কোড, যেমন `"01"`, `"3102"`। |
| `getTitle()` | GS1 ডেটা শিরোনাম, যেমন `"GTIN"`, `"BATCH/LOT"`। |
| `getDescription()` | AI-এর পূর্ণ GS1 বিবরণ, **পার্স ভাষায় স্থানীয়কৃত** (ইংরেজিতে যেমন `"Global Trade Item Number (GTIN)"`)। অনূদিত না থাকলে AI সংজ্ঞার ইংরেজি লেখায় ফিরে যায়। |
| `getFormatString()` | AI *ও* তার ডেটা — দুটোকেই আওতাভুক্ত করা বিন্যাস-বর্ণনা, যেমন AI `01`-এর জন্য `"N2+N14"`, AI `10`-এর জন্য `"N2+X..20"`, AI `3932`-এর জন্য `"N4+N3+N..15"`। |
| `getValue()` | এলিমেন্ট স্ট্রিং থেকে নিষ্কাশিত কাঁচা ডেটা মান। |
| `isFixedLength()` | AI-এর ডেটার দৈর্ঘ্য নির্দিষ্ট হলে `true`। |
| `getPosition()` | মূল ইনপুটে শূন্য থেকে গোনা অক্ষরের অবস্থান। |
| `getGS1ComponentValues()` | উপাদানভিত্তিক মান-খণ্ড (বহু-উপাদান AI-এর জন্য)। |
| `getErrors()` | এলিমেন্ট-স্তরে WARNING নয় এমন ত্রুটি। |
| `getWarnings()` | এলিমেন্ট-স্তরে WARNING পরামর্শ। |
| `getIssues()` | এলিমেন্ট-স্তরের ত্রুটি ও সতর্কতা একত্রে। |
| `hasErrors()` | WARNING নয় এমন কোনো ত্রুটি যুক্ত থাকলে `true`। |
| `hasWarnings()` | কোনো WARNING পরামর্শ যুক্ত থাকলে `true`। |
| `getInterpretations()` | `GS1AIInterpretation` এন্ট্রি (INTERPRETATION মোডে পূরণ হয়)। |
| `getInterpretation(String type)` | দেওয়া `GS1Constants_Enricher` প্রকার-কী-এর সঙ্গে মেলা প্রথম ব্যাখ্যা, বা `null`। |
| `getDigitalLinkAIType()` | এলিমেন্টের Digital Link ভূমিকা (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), বা এলিমেন্ট-স্ট্রিং ইনপুটের ক্ষেত্রে `null`। |
| `hasDigitalLinkAIType()` | কোনো Digital Link ভূমিকা বরাদ্দ হলে `true`। |

---

### GaiaError

একটি অপরিবর্তনীয় যাচাই-ত্রুটি বা পরামর্শ।

| পদ্ধতি | বিবরণ |
|---|---|
| `getId()` | তালিকার শনাক্তকারী, যেমন `"GE-C003"`। |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, বা `WARNING`। |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, বা `INTERNAL`। |
| `getCode()` | যন্ত্র-পাঠযোগ্য সংক্ষিপ্ত কোড। |
| `getAi()` | যে AI কোড ত্রুটি ঘটিয়েছে, বা অবজেক্ট-স্তরের ত্রুটির ক্ষেত্রে `null`। |
| `getMessage()` | মান বসানো, মানুষের পাঠযোগ্য বার্তা। |
| `getPosition()` | মূল ইনপুটে শূন্য থেকে গোনা অক্ষরের অবস্থান। |

---

### GS1AIInterpretation

`INTERPRETATION` মোডে কোনো `GS1AIObjectElement`-এর সঙ্গে যুক্ত একটিমাত্র লেবেলযুক্ত ব্যাখ্যা-অংশ।

| পদ্ধতি | বিবরণ |
|---|---|
| `getType()` | যন্ত্র-পাঠযোগ্য প্রকার-কী, যেমন `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`। সব ভাষায় অপরিবর্তিত। |
| `getLabel()` | মানুষের পাঠযোগ্য লেবেল, **পার্স ভাষায় স্থানীয়কৃত** (ইংরেজিতে যেমন `"Date"` / `"GS1 company prefix"`)। |
| `getValue()` | নিষ্কাশিত/সমৃদ্ধ করা মান, যেমন `"31/12/2026"`, `"9506000"`। স্থানীয়কৃত নয়। |

---

### DataCarrierEntry ও DataCarrierType

ইনপুট যখন কোনো AIM কোড আইডি বহন করে, তখন `ParseResult.getDataCarrier()` একটি `DataCarrierEntry` ফেরত দেয়, যা ডেটা বহনকারী প্রতীকটির বর্ণনা দেয়। এই এন্ট্রিটি মিলে যাওয়া AIM কোড আইডির নির্দিষ্ট রেজিস্ট্রি রেকর্ড; আর `DataCarrierType` হল সেই কম্পাইল-সময়ের enum যার অন্তর্গত এটি।

#### DataCarrierEntry

চেনা একটিমাত্র AIM কোড আইডির মেটাডেটা (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`)।

| পদ্ধতি | বিবরণ |
|---|---|
| `getAimCodeId()` | যে AIM কোড আইডি মিলেছে, যেমন `"]C1"`। |
| `getName()` | নির্দিষ্ট প্রতীকটির মানুষের পাঠযোগ্য নাম, যেমন `"GS1-128 / ISBT 128"`, `"EAN-8"`। |
| `getDescription()` | ক্যারিয়ারের দীর্ঘতর বিবরণ। |
| `getType()` | ক্যারিয়ারের কাঠামোগত প্রকার, স্ট্রিং হিসেবে (`getDataCarrierType().getCategory()`-এর প্রতিফলন)। |
| `getStandard()` | সিম্বোলজির মানদণ্ড, যেখানে নথিভুক্ত আছে। |
| `getDataCarrierType()` | এই এন্ট্রির জন্য টাইপযুক্ত `DataCarrierType` — প্রোগ্রামভিত্তিক রাউটিংয়ে এটিই ব্যবহার করুন। |
| `isGs1Capable()` | ক্যারিয়ার GS1 ডেটা ধারণ করতে পারলে `true` (AI এলিমেন্ট স্ট্রিং ও/অথবা Digital Link)। |
| `isGs1AICapable()` | ক্যারিয়ার GS1 AI এলিমেন্ট স্ট্রিং ধারণ করতে পারলে `true`। |
| `isGs1DigitalLinkCapable()` | ক্যারিয়ার কোনো GS1 Digital Link URI ধারণ করতে পারলে `true`। |
| `isEciCapable()` | ক্যারিয়ার ECI নির্দেশক সমর্থন করলে `true`। |
| `isRequiresGtinPadding()` | সেসব EAN/UPC/ITF ক্যারিয়ারের জন্য `true`, যাদের সাংখ্যিক মান AI পার্সিংয়ের আগে GTIN-14 পর্যন্ত ভরাট করা হয়। |

#### DataCarrierType

ডেটা-ক্যারিয়ারের প্রকারগুলোর কম্পাইল-সময়ের enum, যার কী হল ISO/IEC 15424-এ বরাদ্দ AIM কোড আইডি (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`)। `]`-এর পরের অক্ষরটি (*কোড অক্ষর*) পরিবার বেছে নেয়; অধিকাংশ পরিবার এমন একটিমাত্র ধ্রুবকে ম্যাপ করে যা প্রতিটি সংশোধক আওতাভুক্ত করে (`ITF`-এর মধ্যে `]I0`–`]I2` পড়ে; `EAN_UPC`-এর মধ্যে EAN-13, UPC-A, UPC-E ও EAN-8)। GS1 যেখানে কোনো সংশোধক AI ডেটার জন্য সংরক্ষিত রাখে, সেখানে সেই রূপটি নিজেই আলাদা ধ্রুবক — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — তাদের সাধারণ প্রতিরূপ থেকে পৃথক। কোনো AIM কোড আইডি না থাকলে, বা সেটি অজানা কোনো ক্যারিয়ারের নাম করলে, প্রকারটি হয় `UNKNOWN`।

| পদ্ধতি | বিবরণ |
|---|---|
| `getCategory()` | বিস্তৃত শ্রেণি `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, বা `OTHER`। |
| `getCodeChar()` | পরিবার চিহ্নিতকারী AIM কোড অক্ষর, যেমন QR Code-এর জন্য `"Q"`; `UNKNOWN`-এর জন্য `null`। |
| `getDisplayName()` | *প্রকারটির* মানুষের পাঠযোগ্য নাম (`DataCarrierEntry.getName()`-এর চেয়ে বিস্তৃততর হতে পারে — যেমন `"EAN-13 / UPC-A / UPC-E / EAN-8"` বনাম `"EAN-8"`)। |
| `isGs1DataCarrier()` | সেসব ধ্রুবকের জন্য `true` যারা সবসময়ই GS1 AI ডেটা নির্দেশ করে: GS1-সংরক্ষিত চারটি রূপ (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) এবং সেই সঙ্গে `GS1_DATABAR`, যা স্বভাবতই GS1, কারণ প্রতিটি `]e` সংশোধকই GS1 DataBar। এটি `DataCarrierEntry.isGs1AICapable()`-এর চেয়ে সংকীর্ণ — সাধারণ কোনো `QR_CODE`-ও GS1 AI ডেটা বহন করতে পারে। |
| `static forAimCodeId(String)` | সরাসরি কোনো AIM কোড আইডি থেকে প্রকার সমাধান করে (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); অনুপস্থিত, বিকৃত বা অপরিচিত আইডির জন্য `UNKNOWN` ফেরত দেয়। |

নামের বদলে প্রকার ধরে রাউটিং — যেমন রৈখিক (Code-128) প্রতীককে 2D (QR / Data Matrix) থেকে আলাদা করা:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D`-এর মধ্যে কেবল ম্যাট্রিক্স ও ডট প্রতীকই পড়ে; স্তরায়িত-রৈখিক ক্যারিয়ারগুলো (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) `STACKED_LINEAR`, যদিও সেগুলোকে সচরাচর "2D" বারকোড
বলা হয়। দুটিকেই এক দল হিসেবে ধরতে — ধরুন লেজার স্ক্যানারের বদলে ইমেজার লাগবে কি না তা
ঠিক করতে — দুটি শ্রেণিই পরীক্ষা করুন।

> প্রকার সমাধানের জন্য স্ক্যানে AIM কোড আইডি থাকা দরকার; সেটি ছাড়া `getDataCarrier()` `null` আর প্রকার `UNKNOWN`। স্ক্যানারকে AIM কোড আইডি উপসর্গ পাঠানোর জন্য বিন্যস্ত করুন।

---

## ত্রুটি রেফারেন্স

| কোড | স্তর | পর্যায় | অর্থ |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | অজানা AI উপসর্গ — ডেটার দৈর্ঘ্য নির্ধারণ করা যাচ্ছে না |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | পূর্ণ একটি AI কোড পড়ার পক্ষে ইনপুট অত্যন্ত ছোট |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | কাটা মান — AI-এর প্রয়োজনের চেয়ে কম অক্ষর |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | এলিমেন্ট স্ট্রিংয়ে পুনরাবৃত্ত অ্যাপ্লিকেশন আইডেন্টিফায়ার |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | আবশ্যিক AI নির্ভরতা অনুপস্থিত |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | বর্জিত AI জোড়া — এমন দুটি AI যারা একসঙ্গে থাকতে পারে না |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | অপ্রত্যাশিত টোকেনায়ন ব্যর্থতা |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | এলিমেন্ট স্ট্রিংয়ে GS1-এর এনকোডযোগ্য সেটের বাইরের অক্ষর |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | পরিবর্তনশীল-দৈর্ঘ্যের AI-এর পরে আবশ্যিক FNC1 বিভাজক অনুপস্থিত |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | সব উপাদানের সর্বোচ্চ সীমার পরেও উদ্বৃত্ত ডেটা |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | স্ট্রিংয়ের মাঝখানে নির্দিষ্ট-দৈর্ঘ্যের AI-এর পরে FNC1 বিভাজক |
| `GE-W002` | WARNING | SYNTAX | এলিমেন্ট স্ট্রিংয়ের শেষে উদ্বৃত্ত FNC1 (কেবল পরামর্শ) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI-এর কাঠামোগত লঙ্ঘন — প্রতিটি শর্তের জন্য একটি কোড (বিকৃত URI, স্কিম, হোস্ট, যোগ্যকের ক্রম, নিষিদ্ধ AI, প্রাথমিক কী নেই (`GE-L013`), একাধিক প্রাথমিক কী (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | মান AI-এর রেগেক্স প্যাটার্নে উতরায় না |
| `GE-C003` | DATA_ERROR | CONTENT | চেক ডিজিট যাচাই ব্যর্থ |
| `GE-C004` | DATA_ERROR | CONTENT | চেক ক্যারেক্টার জোড়ার যাচাই ব্যর্থ |
| `GE-C005` | FORMAT_ERROR | CONTENT | উপাদানের মানে অনুমোদিত অক্ষরসেটের বাইরের অক্ষর |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | উপাদান-বিন্যাসের ব্যর্থতা — প্রতিটি যাচাইকারী-শর্তের জন্য একটি কোড (দেখুন `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | স্বনির্ধারিত অর্থগত যাচাইয়ের ব্যর্থতা — প্রতিটি যাচাইকারী-শর্তের জন্য একটি কোড (দেখুন `content/validator/`)। **ব্যতিক্রম:** নিচে তালিকাভুক্ত ১৪টি GS1 কোম্পানি প্রিফিক্স পরীক্ষা `WARNING` স্তর বহন করে, আর `GE-C168` (অপরিচিত ISO 3166-1 সাংখ্যিক দেশ কোড) `FORMAT_ERROR` স্তর বহন করে। |
| GS1 কোম্পানি প্রিফিক্স পরীক্ষা | WARNING | CONTENT | GS1-কী যুক্ত AI-তে কী কোনো পরিচিত GS1 কোম্পানি প্রিফিক্স দিয়ে শুরু হয় না — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC)। কেবল পরামর্শ — বৈধতাকে প্রভাবিত করে না। |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2)-তে IMEI চেক ডিজিট (Luhn) ব্যর্থ |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM)-এ EID চেক ডিজিট (Luhn) ব্যর্থ |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | অপরিচিত AIM কোড আইডি |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | ক্যারিয়ার শনাক্ত হয়েছে, কিন্তু সেটি GS1 AI এলিমেন্ট স্ট্রিং বা Digital Link URI কোনোটিই সমর্থন করে না |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | অপ্রত্যাশিত অভ্যন্তরীণ ত্রুটি |

> **বার্তা উপস্থাপনায় জানা ত্রুটি।** তালিকার টেমপ্লেটগুলো বসানো মানকে MessageFormat-ধাঁচের
> দ্বিগুণ ঊর্ধ্বকমা (`''{value}''`) দিয়ে উদ্ধৃত করে, কিন্তু `ErrorRegistry` মান বসায় সাধারণ
> `String.replace` দিয়ে, ফলে সেই দ্বিগুণতা `getMessage()` পর্যন্ত টিকে যায় — এই গাইডে
> উদ্ধৃত বার্তায় যেখানে `value '09506000134351'` দেখানো হয়, সেখানে আপাতত আপনি
> `value ''09506000134351''` দেখবেন। ৩৫টি ভাষার তালিকার প্রতিটি মান-উদ্ধৃতকারী বার্তাই
> এতে আক্রান্ত। ত্রুটি বার্তা পার্স করবেন না; `getId()` / `getCode()` ধরে মেলান।

---

## থ্রেড নিরাপত্তা

`GaiaParser` একবার গড়া হয়ে গেলে থ্রেড-নিরাপদ। একটিমাত্র দৃষ্টান্ত একাধিক থ্রেডে ভাগ করে নেওয়া যায় এবং যুগপৎভাবে ব্যবহার করা যায়। সুপারিশ করা রীতি হল অ্যাপ্লিকেশন শুরুর সময়ে একটিমাত্র দৃষ্টান্ত গড়ে সেটিই বারবার ব্যবহার করা:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` অপরিবর্তনীয় এবং ভাগ করে নেওয়ার ক্ষেত্রে সমানভাবেই নিরাপদ। থ্রেড-নিরাপত্তার একমাত্র যে দায়িত্ব লাইব্রেরি আপনার হয়ে পালন করতে পারে না তা হল [ইনপুট মডিফায়ার](#ইনপট-মডফযর)-এর ক্ষেত্রে: প্রতিটি মডিফায়ারের একটিমাত্র দৃষ্টান্ত ক্যাশে রেখে যুগপৎ চলা প্রতিটি পার্সে ভাগ করে নেওয়া হয়, তাই বাস্তবায়নগুলোকে অবশ্যই অবস্থাহীন হতে হবে।

---

## পরিশিষ্ট ক — AI স্ট্রিং ধ্রুবক

`GS1Constants_AICodes` (প্যাকেজ `tools.pantheum.gaia.gs1.constants`) GAIA-র চেনা প্রতিটি অ্যাপ্লিকেশন আইডেন্টিফায়ারের জন্য একটি `String` ধ্রুবক ঘোষণা করে। কোডে AI কোডের লিটারেল বসানোর বদলে এই ধ্রুবকগুলো ব্যবহার করুন:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

প্রতিটি ধ্রুবক তার AI কোডের স্ট্রিং রূপ ধারণ করে (যেমন `AI_01_GTIN = "01"`)।

### শনাক্তকরণ ও ক্রমায়ন

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | সিরিয়াল শিপিং কন্টেইনার কোড (SSCC). |
| `01` | `AI_01_GTIN` | গ্লোবাল ট্রেড আইটেম নম্বর (GTIN). |
| `02` | `AI_02_CONTENT` | অন্তর্ভুক্ত ট্রেড আইটেমসমূহের গ্লোবাল ট্রেড আইটেম নম্বর (GTIN). |
| `03` | `AI_03_MTO_GTIN` | মেড-টু-অর্ডার (MtO) ট্রেড আইটেমের শনাক্তকরণ (GTIN). |
| `10` | `AI_10_BATCH_LOT` | ব্যাচ বা লট নম্বর. |
| `20` | `AI_20_VARIANT` | অভ্যন্তরীণ পণ্য ভ্যারিয়েন্ট. |
| `21` | `AI_21_SERIAL` | ক্রম নম্বর (সিরিয়াল নম্বর). |
| `22` | `AI_22_CPV` | ভোক্তা পণ্যের ভ্যারিয়েন্ট. |
| `235` | `AI_235_TPX` | থার্ড পার্টি নিয়ন্ত্রিত, গ্লোবাল ট্রেড আইটেম নম্বরের (GTIN) সিরিয়ালাইজড এক্সটেনশন (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | প্রস্তুতকারক কর্তৃক নির্ধারিত অতিরিক্ত পণ্য শনাক্তকরণ. |
| `241` | `AI_241_CUST_PART_NO` | গ্রাহক পার্ট নম্বর. |
| `242` | `AI_242_MTO_VARIANT` | মেড-টু-অর্ডার ভ্যারিয়েশন নম্বর. |
| `243` | `AI_243_PCN` | প্যাকেজিং কম্পোনেন্ট নম্বর. |
| `250` | `AI_250_SECONDARY_SERIAL` | দ্বিতীয় ক্রম নম্বর. |
| `251` | `AI_251_REF_TO_SOURCE` | উৎস সত্তার রেফারেন্স. |
| `253` | `AI_253_GDTI` | গ্লোবাল ডকুমেন্ট টাইপ শনাক্তকারী (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | গ্লোবাল লোকেশন নম্বর (GLN) এক্সটেনশন উপাদান. |
| `255` | `AI_255_GCN` | গ্লোবাল কুপন নম্বর (GCN). |
| `30` | `AI_30_VAR_COUNT` | আইটেমের পরিবর্তনশীল সংখ্যা (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `37` | `AI_37_COUNT` | একটি লজিস্টিক ইউনিটে অন্তর্ভুক্ত ট্রেড আইটেম বা ট্রেড আইটেম টুকরার সংখ্যা. |

### তারিখ ও সময়

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | উৎপাদনের তারিখ (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | নির্ধারিত তারিখ (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | প্যাকেজিং তারিখ (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | সর্বোত্তম ব্যবহারের তারিখ (YYMMDD). |
| `16` | `AI_16_SELL_BY` | বিক্রয়ের শেষ তারিখ (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | মেয়াদ উত্তীর্ণের তারিখ (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | ডেলিভারির সর্বনিম্ন প্রারম্ভিক তারিখ-সময় (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | ডেলিভারির সর্বশেষ গ্রহণযোগ্য তারিখ-সময় (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | রিলিজ তারিখ (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | মেয়াদ উত্তীর্ণের তারিখ ও সময় (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | প্রথম হিমায়িতকরণের তারিখ (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | ফসল সংগ্রহের তারিখ (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | পরীক্ষার তারিখ (YYMMDD[hhmm]). |

### পরিমাণ ও পরিমাপ — পরিবর্তনশীল পরিমাপ (মেট্রিক)

চার-অঙ্কের AI পরিবার `310n`–`369n` পরিবর্তনশীল-পরিমাপের পরিমাণ এনকোড করে। তৃতীয় অঙ্কটি পরিমাপের ধরন বেছে নেয়; **চতুর্থ অঙ্কটি** (`n`, ০–৫) হল নিহিত দশমিক স্থানের সংখ্যা — অর্থাৎ `AI_3102_NET_WEIGHT_KG` মানে ২ দশমিক স্থানসহ কিলোগ্রামে নিট ওজন।

| পরিবার | ধ্রুবকের নকশা (`n` = দশমিক অঙ্ক) | বিবরণ |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | নিট ওজন, কিলোগ্রাম (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `311n` | `AI_311n_LENGTH_M` | দৈর্ঘ্য বা প্রথম মাত্রা, মিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `312n` | `AI_312n_WIDTH_M` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, মিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `313n` | `AI_313n_HEIGHT_M` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, মিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `314n` | `AI_314n_AREA_M` | ক্ষেত্রফল, বর্গমিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `315n` | `AI_315n_NET_VOLUME_L` | নিট আয়তন, লিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `316n` | `AI_316n_NET_VOLUME_M` | নিট আয়তন, ঘনমিটার (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | লজিস্টিক ওজন, কিলোগ্রাম. |
| `331n` | `AI_331n_LENGTH_M_LOG` | দৈর্ঘ্য বা প্রথম মাত্রা, মিটার. |
| `332n` | `AI_332n_WIDTH_M_LOG` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, মিটার. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, মিটার. |
| `334n` | `AI_334n_AREA_M_LOG` | ক্ষেত্রফল, বর্গমিটার. |
| `335n` | `AI_335n_VOLUME_L_LOG` | লজিস্টিক আয়তন, লিটার. |
| `336n` | `AI_336n_VOLUME_M_LOG` | লজিস্টিক আয়তন, ঘনমিটার. |
| `337n` | `AI_337n_KG_PER_M` | প্রতি বর্গমিটারে কিলোগ্রাম. |

### পরিমাণ ও পরিমাপ — পরিবর্তনশীল পরিমাপ (ইম্পেরিয়াল / মার্কিন)

| পরিবার | ধ্রুবকের নকশা (`n` = দশমিক অঙ্ক) | বিবরণ |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | নিট ওজন, পাউন্ড (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `321n` | `AI_321n_LENGTH_IN` | দৈর্ঘ্য বা প্রথম মাত্রা, ইঞ্চি (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `322n` | `AI_322n_LENGTH_FT` | দৈর্ঘ্য বা প্রথম মাত্রা, ফুট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `323n` | `AI_323n_LENGTH_YD` | দৈর্ঘ্য বা প্রথম মাত্রা, গজ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `324n` | `AI_324n_WIDTH_IN` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, ইঞ্চি (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `325n` | `AI_325n_WIDTH_FT` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, ফুট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `326n` | `AI_326n_WIDTH_YD` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, গজ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `327n` | `AI_327n_HEIGHT_IN` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, ইঞ্চি (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `328n` | `AI_328n_HEIGHT_FT` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, ফুট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `329n` | `AI_329n_HEIGHT_YD` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, গজ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | লজিস্টিক ওজন, পাউন্ড. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | দৈর্ঘ্য বা প্রথম মাত্রা, ইঞ্চি. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | দৈর্ঘ্য বা প্রথম মাত্রা, ফুট. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | দৈর্ঘ্য বা প্রথম মাত্রা, গজ. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, ইঞ্চি. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, ফুট. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | প্রস্থ, ব্যাস, বা দ্বিতীয় মাত্রা, গজ. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, ইঞ্চি. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, ফুট. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | গভীরতা, পুরুত্ব, উচ্চতা, বা তৃতীয় মাত্রা, গজ. |
| `350n` | `AI_350n_AREA_IN` | ক্ষেত্রফল, বর্গ ইঞ্চি (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `351n` | `AI_351n_AREA_FT` | ক্ষেত্রফল, বর্গফুট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `352n` | `AI_352n_AREA_YD` | ক্ষেত্রফল, বর্গ গজ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `353n` | `AI_353n_AREA_IN_LOG` | ক্ষেত্রফল, বর্গ ইঞ্চি. |
| `354n` | `AI_354n_AREA_FT_LOG` | ক্ষেত্রফল, বর্গফুট. |
| `355n` | `AI_355n_AREA_YD_LOG` | ক্ষেত্রফল, বর্গ গজ. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | নিট ওজন, ট্রয় আউন্স (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | নিট ওজন (বা আয়তন), আউন্স (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `360n` | `AI_360n_NET_VOLUME_QT` | নিট আয়তন, কোয়ার্ট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | নিট আয়তন, ইউএস গ্যালন (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | লজিস্টিক আয়তন, কোয়ার্ট. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | লজিস্টিক আয়তন, ইউএস গ্যালন. |
| `364n` | `AI_364n_NET_VOLUME_IN` | নিট আয়তন, ঘন ইঞ্চি (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `365n` | `AI_365n_NET_VOLUME_FT` | নিট আয়তন, ঘনফুট (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `366n` | `AI_366n_NET_VOLUME_YD` | নিট আয়তন, ঘন গজ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | লজিস্টিক আয়তন, ঘন ইঞ্চি. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | লজিস্টিক আয়তন, ঘনফুট. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | লজিস্টিক আয়তন, ঘন গজ. |

### মূল্য নির্ধারণ ও আর্থিক পরিমাণ

চতুর্থ অঙ্কটি (`n`) নিহিত দশমিক স্থানের সংখ্যা এনকোড করে। অনুমোদিত পরিসর
পরিবারভেদে আলাদা — `n` স্তম্ভটি দেখুন।

| পরিবার | ধ্রুবকের নকশা (`n` = দশমিক অঙ্ক) | `n` | বিবরণ |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | প্রযোজ্য প্রদেয় পরিমাণ বা কুপন মূল্য, স্থানীয় মুদ্রা. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | আইএসও মুদ্রা কোডসহ প্রযোজ্য প্রদেয় পরিমাণ. |
| `392n` | `AI_392n_PRICE` | 0–9 | প্রযোজ্য প্রদেয় পরিমাণ, একক মুদ্রা অঞ্চল (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `393n` | `AI_393n_PRICE` | 0–9 | আইএসও মুদ্রা কোডসহ প্রযোজ্য প্রদেয় পরিমাণ (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | একটি কুপনের শতাংশ ছাড়. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | প্রতি একক পরিমাপে প্রদেয় পরিমাণ, একক মুদ্রা অঞ্চল (পরিবর্তনশীল পরিমাপ ট্রেড আইটেম). |

### অবস্থান ও চালান

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | গ্রাহকের ক্রয় আদেশ নম্বর. |
| `401` | `AI_401_GINC` | চালানের জন্য গ্লোবাল শনাক্তকরণ নম্বর (GINC). |
| `402` | `AI_402_GSIN` | গ্লোবাল শিপমেন্ট শনাক্তকরণ নম্বর (GSIN). |
| `403` | `AI_403_ROUTE` | রাউটিং কোড. |
| `410` | `AI_410_SHIP_TO_LOC` | শিপ টু / ডেলিভার টু গ্লোবাল লোকেশন নম্বর (GLN). |
| `411` | `AI_411_BILL_TO` | বিল টু / ইনভয়েস টু গ্লোবাল লোকেশন নম্বর (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | ক্রয়কৃত উৎস গ্লোবাল লোকেশন নম্বর (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | শিপ ফর / ডেলিভার ফর - ফরওয়ার্ড টু গ্লোবাল লোকেশন নম্বর (GLN). |
| `414` | `AI_414_LOC_NO` | একটি ভৌত অবস্থানের শনাক্তকরণ - গ্লোবাল লোকেশন নম্বর (GLN). |
| `415` | `AI_415_PAY_TO` | ইনভয়েসকারী পক্ষের গ্লোবাল লোকেশন নম্বর (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | উৎপাদন বা পরিষেবা স্থানের গ্লোবাল লোকেশন নম্বর (GLN). |
| `417` | `AI_417_PARTY` | পার্টি গ্লোবাল লোকেশন নম্বর (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | একক পোস্টাল কর্তৃপক্ষের মধ্যে শিপ টু / ডেলিভার টু পোস্টাল কোড. |
| `421` | `AI_421_SHIP_TO_POST` | আইএসও দেশ কোডসহ শিপ টু / ডেলিভার টু পোস্টাল কোড. |
| `422` | `AI_422_ORIGIN` | ট্রেড আইটেমের উৎপত্তির দেশ. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | প্রাথমিক প্রক্রিয়াকরণের দেশ. |
| `424` | `AI_424_COUNTRY_PROCESS` | প্রক্রিয়াকরণের দেশ. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | বিচ্ছিন্নকরণের দেশ. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | সম্পূর্ণ প্রক্রিয়া শৃঙ্খল কভারকারী দেশ. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | উৎপত্তির দেশ উপবিভাগ. |
| `4300` | `AI_4300_SHIP_TO_COMP` | শিপ-টু / ডেলিভার-টু কোম্পানির নাম. |
| `4301` | `AI_4301_SHIP_TO_NAME` | শিপ-টু / ডেলিভার-টু যোগাযোগ. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | শিপ-টু / ডেলিভার-টু ঠিকানা লাইন ১. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | শিপ-টু / ডেলিভার-টু ঠিকানা লাইন ২. |
| `4304` | `AI_4304_SHIP_TO_SUB` | শিপ-টু / ডেলিভার-টু উপশহর. |
| `4305` | `AI_4305_SHIP_TO_LOC` | শিপ-টু / ডেলিভার-টু এলাকা. |
| `4306` | `AI_4306_SHIP_TO_REG` | শিপ-টু / ডেলিভার-টু অঞ্চল. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | শিপ-টু / ডেলিভার-টু দেশের কোড. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | শিপ-টু / ডেলিভার-টু টেলিফোন নম্বর. |
| `4309` | `AI_4309_SHIP_TO_GEO` | শিপ-টু / ডেলিভার-টু জিও লোকেশন. |
| `4310` | `AI_4310_RTN_TO_COMP` | রিটার্ন-টু কোম্পানির নাম. |
| `4311` | `AI_4311_RTN_TO_NAME` | রিটার্ন-টু যোগাযোগ. |
| `4312` | `AI_4312_RTN_TO_ADD1` | রিটার্ন-টু ঠিকানা লাইন ১. |
| `4313` | `AI_4313_RTN_TO_ADD2` | রিটার্ন-টু ঠিকানা লাইন ২. |
| `4314` | `AI_4314_RTN_TO_SUB` | রিটার্ন-টু উপশহর. |
| `4315` | `AI_4315_RTN_TO_LOC` | রিটার্ন-টু এলাকা. |
| `4316` | `AI_4316_RTN_TO_REG` | রিটার্ন-টু অঞ্চল. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | রিটার্ন-টু দেশের কোড. |
| `4318` | `AI_4318_RTN_TO_POST` | রিটার্ন-টু পোস্টাল কোড. |
| `4319` | `AI_4319_RTN_TO_PHONE` | রিটার্ন-টু টেলিফোন নম্বর. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | সার্ভিস কোড বিবরণ. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | বিপজ্জনক পণ্যের ফ্ল্যাগ. |
| `4322` | `AI_4322_AUTH_LEAVE` | ছেড়ে যাওয়ার অনুমতি (ডেলিভারি অনুমোদন). |
| `4323` | `AI_4323_SIG_REQUIRED` | স্বাক্ষর প্রয়োজনীয় ফ্ল্যাগ. |
| `4330` | `AI_4330_MAX_TEMP_F` | ফারেনহাইটে সর্বোচ্চ তাপমাত্রা (ডিগ্রির শতভাগে প্রকাশিত). |
| `4331` | `AI_4331_MAX_TEMP_C` | সেলসিয়াসে সর্বোচ্চ তাপমাত্রা (ডিগ্রির শতভাগে প্রকাশিত). |
| `4332` | `AI_4332_MIN_TEMP_F` | ফারেনহাইটে সর্বনিম্ন তাপমাত্রা (ডিগ্রির শতভাগে প্রকাশিত). |
| `4333` | `AI_4333_MIN_TEMP_C` | সেলসিয়াসে সর্বনিম্ন তাপমাত্রা (ডিগ্রির শতভাগে প্রকাশিত). |

### পণ্যের বৈশিষ্ট্য ও অনুসরণযোগ্যতা

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | ন্যাটো স্টক নম্বর (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | ইউএন/ইসিই মাংস দেহাবশেষ ও কর্তন শ্রেণীবিভাগ. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | সক্রিয় শক্তি (পোটেন্সি). |
| `7005` | `AI_7005_CATCH_AREA` | আহরণ এলাকা (মৎস্য শিকার এলাকা). |
| `7008` | `AI_7008_AQUATIC_SPECIES` | মৎস্য শিকারের উদ্দেশ্যে প্রজাতি. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | মৎস্য শিকারের সরঞ্জামের ধরন. |
| `7010` | `AI_7010_PROD_METHOD` | উৎপাদন পদ্ধতি. |
| `7020` | `AI_7020_REFURB_LOT` | পুনর্নবীকরণ লট আইডি. |
| `7021` | `AI_7021_FUNC_STAT` | কার্যকরী অবস্থা. |
| `7022` | `AI_7022_REV_STAT` | সংশোধন অবস্থা. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | একটি অ্যাসেম্বলির গ্লোবাল ব্যক্তিগত সম্পদ শনাক্তকারী (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | তিন-অঙ্কের ISO দেশ কোডসহ প্রক্রিয়াকারীর নম্বর (১০টি স্থান)।. |
| `7040` | `AI_7040_UIC_EXT` | এক্সটেনশন ১ ও ইম্পোর্টার ইনডেক্সসহ GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | ইউএন/সেফ্যাক্ট ফ্রেইট ইউনিট প্রকার. |

### জাতীয় স্বাস্থ্য পরিশোধ নম্বর (NHRN)

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - জার্মানি PZN. |
| `711` | `AI_711_NHRN_CIP` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - ফ্রান্স CIP. |
| `712` | `AI_712_NHRN_CN` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - স্পেন CN. |
| `713` | `AI_713_NHRN_DRN` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - ব্রাজিল DRN. |
| `714` | `AI_714_NHRN_AIM` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - পর্তুগাল AIM. |
| `715` | `AI_715_NHRN_NDC` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - মার্কিন যুক্তরাষ্ট্র NDC. |
| `716` | `AI_716_NHRN_AIC` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - ইতালি AIC. |
| `717` | `AI_717_NHRN_SRN` | জাতীয় স্বাস্থ্যসেবা প্রতিদান নম্বর (NHRN) - কোস্টা রিকা স্যানিটারি রেজিস্টার নম্বর. |

### স্বাস্থ্যসেবা, GMN, HIDRI, CPID ও ব্যক্তি-সংক্রান্ত ডেটা

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | সার্টিফিকেশন রেফারেন্স (১০টি স্থান)।. |
| `7240` | `AI_7240_PROTOCOL` | প্রোটোকল আইডি. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | এআইডিসি মিডিয়া প্রকার. |
| `7242` | `AI_7242_VCN` | ভার্সন কন্ট্রোল নম্বর (VCN). |
| `7250` | `AI_7250_DOB` | জন্ম তারিখ (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | জন্মের তারিখ ও সময় (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | জৈবিক লিঙ্গ. |
| `7253` | `AI_7253_FAMILY_NAME` | ব্যক্তির পদবি (পারিবারিক নাম). |
| `7254` | `AI_7254_GIVEN_NAME` | ব্যক্তির প্রথম নাম. |
| `7255` | `AI_7255_SUFFIX` | ব্যক্তির নামের প্রত্যয় (সাফিক্স). |
| `7256` | `AI_7256_FULL_NAME` | ব্যক্তির পূর্ণ নাম. |
| `7257` | `AI_7257_PERSON_ADDR` | ব্যক্তির ঠিকানা. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | শিশু জন্ম ক্রম. |
| `7259` | `AI_7259_BABY` | পারিবারিক পদবি অনুযায়ী শিশু ক্রম. |
| `8001` | `AI_8001_DIMENSIONS` | রোল পণ্য (প্রস্থ, দৈর্ঘ্য, কোর ব্যাস, দিক, স্প্লাইস). |
| `8002` | `AI_8002_CMT_NO` | সেলুলার মোবাইল টেলিফোন শনাক্তকারী. |
| `8003` | `AI_8003_GRAI` | গ্লোবাল রিটার্নেবল সম্পদ শনাক্তকারী (GRAI). |
| `8004` | `AI_8004_GIAI` | গ্লোবাল ব্যক্তিগত সম্পদ শনাক্তকারী (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | প্রতি একক পরিমাপে মূল্য. |
| `8006` | `AI_8006_ITIP` | একটি স্বতন্ত্র ট্রেড আইটেম টুকরার শনাক্তকরণ (ITIP). |
| `8007` | `AI_8007_IBAN` | ইন্টারন্যাশনাল ব্যাংক অ্যাকাউন্ট নম্বর (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | উৎপাদনের তারিখ ও সময় (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | অপটিক্যালি রিডেবল সেন্সর ইন্ডিকেটর. |
| `8010` | `AI_8010_CPID` | কম্পোনেন্ট/পার্ট শনাক্তকারী (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | কম্পোনেন্ট/পার্ট শনাক্তকারী ক্রম নম্বর (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | সফটওয়্যার সংস্করণ. |
| `8013` | `AI_8013_GMN` | গ্লোবাল মডেল নম্বর (GMN). |
| `8014` | `AI_8014_MUDI` | অতিমাত্রায় স্বতন্ত্রীকৃত ডিভাইস নিবন্ধন শনাক্তকারী (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | পরিষেবা প্রদানকারী প্রতিষ্ঠান ও পরিষেবা সরবরাহকারীর মধ্যে সম্পর্ক শনাক্ত করতে গ্লোবাল সার্ভিস রিলেশন নম্বর (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | পরিষেবা প্রদানকারী প্রতিষ্ঠান ও পরিষেবা গ্রহীতার মধ্যে সম্পর্ক শনাক্ত করতে গ্লোবাল সার্ভিস রিলেশন নম্বর (GSRN). |
| `8019` | `AI_8019_SRIN` | সার্ভিস রিলেশন ইনস্ট্যান্স নম্বর (SRIN). |
| `8020` | `AI_8020_REF_NO` | পেমেন্ট স্লিপ রেফারেন্স নম্বর. |
| `8026` | `AI_8026_ITIP_CONTENT` | একটি লজিস্টিক ইউনিটে অন্তর্ভুক্ত ট্রেড আইটেমের টুকরাসমূহের শনাক্তকরণ (ITIP). |
| `8030` | `AI_8030_DIGSIG` | ডিজিটাল স্বাক্ষর (DigSig). |
| `8040` | `AI_8040_IMEI` | ইন্টারন্যাশনাল মোবাইল ইকুইপমেন্ট আইডেন্টিটি (IMEI). |
| `8041` | `AI_8041_IMEI2` | ইন্টারন্যাশনাল মোবাইল ইকুইপমেন্ট আইডেন্টিটি ২ (IMEI2). |
| `8042` | `AI_8042_ESIM` | এম্বেডেড সিম নম্বর. |
| `8043` | `AI_8043_PSIM` | ফিজিক্যাল সিম নম্বর. |
| `8110` | `AI_8110` | উত্তর আমেরিকায় ব্যবহারের জন্য কুপন কোড শনাক্তকরণ. |
| `8111` | `AI_8111_POINTS` | একটি কুপনের লয়্যালটি পয়েন্ট. |
| `8112` | `AI_8112` | উত্তর আমেরিকায় ব্যবহারের জন্য পজিটিভ অফার ফাইল কুপন কোড শনাক্তকরণ. |
| `8200` | `AI_8200_PRODUCT_URL` | বর্ধিত প্যাকেজিং ইউআরএল. |

### অভ্যন্তরীণ / কোম্পানির ব্যবহার

| AI | ধ্রুবক | বিবরণ |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | বাণিজ্য অংশীদারদের মধ্যে পারস্পরিকভাবে সম্মত তথ্য. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | কোম্পানির অভ্যন্তরীণ তথ্য (৯টি স্থান)।. |

---

## পরিশিষ্ট খ — ব্যাখ্যা কী ধ্রুবক

`GaiaParser.parse()` যখন `ParseMode.INTERPRETATION` দিয়ে ডাকা হয়, তখন প্রতিটি `GS1AIObjectElement` ক্ষেত্র-নির্দিষ্ট সমৃদ্ধকদের তৈরি `GS1AIInterpretation` অবজেক্টের একটি তালিকা বহন করতে পারে। নির্দিষ্ট ব্যাখ্যা-মান খুঁজতে `GS1Constants_Enricher` ধ্রুবকগুলো (প্যাকেজ `tools.pantheum.gaia.gs1.constants`) কী হিসেবে ব্যবহার করুন:

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

প্রদর্শনের লেবেল ধ্রুবক **নয়** — সেগুলো `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`-এর স্থানীয়কৃত তালিকায় থাকে, যাদের কী হল প্রকার-ধ্রুবক। `GS1AIInterpretation.getLabel()` লেবেলটি পার্স ভাষায় ফেরত দেয় (দেখুন [স্থানীয়কৃত বার্তা ও লেবেল](#সথনযকত-বরত-ও-লবল)), আর কোনো তালিকায় কী অনুপস্থিত থাকলে ইংরেজিতে ফিরে যায়। নিচের প্রদর্শনের লেবেল স্তম্ভটি বাংলা লেখা তালিকাভুক্ত করে; প্রকার-কীগুলো নিজে সব ভাষাতেই অপরিবর্তিত, তাই সবসময় কী ধরে মেলান, কখনোই লেবেল ধরে নয়।

### তারিখ ও সময়

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `DATE_VALUE` | তারিখ | তারিখ AI (11–17, 7003, 7006, 7011 ইত্যাদি) |
| `DATE_FORMAT` | তারিখের বিন্যাস | তারিখ AI |
| `TIME_VALUE` | সময় | সময়-বহনকারী AI (7003, 7011, 8008 ইত্যাদি) |
| `TIME_FORMAT` | সময়ের বিন্যাস | সময়-বহনকারী AI |
| `DATETIME_VALUE` | তারিখ ও সময় | তারিখ+সময় AI |
| `DATETIME_FORMAT` | তারিখ ও সময়ের বিন্যাস | তারিখ+সময় AI |

### ফসল তোলার তারিখ

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | ফসল কাটার শুরুর তারিখ | AI 7007 |
| `HARVEST_END_DATE` | ফসল কাটার শেষ তারিখ | AI 7007 (ঐচ্ছিক পরিসর-শেষ) |
| `HARVEST_DATE_RANGE` | ফসল কাটার তারিখ পরিসর | AI 7007 |

### GS1 কোম্পানি প্রিফিক্স

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 কোম্পানি প্রিফিক্স | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 সদস্য কোড | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 সদস্য সংস্থা | GTIN / GLN / SSCC AI |

### GTIN

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN প্রকার | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | প্যাকেজিং স্তর | AI 01 |
| `GTIN_CHECK_DIGIT` | চেক ডিজিট | AI 01, 02 |

### SSCC

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | এক্সটেনশন ডিজিট | AI 00 |
| `SSCC_SERIAL_REFERENCE` | সিরিয়াল রেফারেন্স | AI 00 |
| `SSCC_CHECK_DIGIT` | চেক ডিজিট | AI 00 |

### দেশ (ISO 3166)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | দেশ কোড (সংখ্যাসূচক) | একক-দেশ AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | দেশ কোড (আলফা-২) | আলফা-২ দেশ AI |
| `COUNTRY_NAME` | দেশের নাম | একক-দেশ AI |
| `COUNTRY_LIST` | দেশসমূহ | AI 423 — সব নাম জোড়া, যেমন `Australia, New Zealand` |

AI 423 (প্রাথমিক প্রক্রিয়াকরণের দেশ) পাঁচটি পর্যন্ত দেশ বহন করতে পারে, তাই এটি
**প্রতিটি দেশের জন্য একটি করে ক্রমাঙ্কিত জোড়া** নির্গত করে — `COUNTRY_CODE_NUMERIC_1`,
`COUNTRY_NAME_1`, `COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — আর তার পরে একটিমাত্র
`COUNTRY_LIST` সারসংক্ষেপ। এই কী-গুলো `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` ধ্রুবক থেকে ১ থেকে শুরু হওয়া ক্রমসংখ্যা জুড়ে গড়ুন, অথবা কেবল
`getInterpretations()` ধরে চলুন; প্রত্যয়বিহীন `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME`
কী AI 423-এর জন্য **নির্গত হয় না**।

### মুদ্রা (ISO 4217)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | মুদ্রা কোড | মুদ্রাসহ পরিমাণ AI (391n, 393n) |
| `CURRENCY_ALPHA` | মুদ্রার বর্ণানুক্রমিক কোড | মুদ্রাসহ পরিমাণ AI |
| `CURRENCY_NAME` | মুদ্রার নাম | মুদ্রাসহ পরিমাণ AI |

### তাপমাত্রা

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `TEMPERATURE` | তাপমাত্রা | AI 4330–4333 |
| `TEMPERATURE_UNIT` | তাপমাত্রার একক | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | তাপমাত্রা (ফরম্যাট করা) | AI 4330–4333 |

### লিঙ্গ (ISO 5218)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `SEX_CODE` | লিঙ্গ কোড | AI 7252 |
| `SEX_DESCRIPTION` | লিঙ্গ বিবরণ | AI 7252 |

### জলজ প্রজাতি (FAO)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `SPECIES_CODE` | প্রজাতি কোড | AI 7008 |
| `SPECIES_SCIENTIFIC` | বৈজ্ঞানিক নাম | AI 7008 |
| `SPECIES_ENGLISH` | প্রচলিত নাম | AI 7008 |
| `SPECIES_FAMILY` | গোত্র | AI 7008 |
| `SPECIES_ORDER` | বর্গ | AI 7008 |

### NATO স্টক নম্বর (NSN)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `NSN_FSG` | সরবরাহ গোষ্ঠী | AI 7001 |
| `NSN_FSG_NAME` | সরবরাহ গোষ্ঠীর নাম | AI 7001 |
| `NSN_FSCG` | সরবরাহ শ্রেণি | AI 7001 |
| `NSN_FSCG_NAME` | সরবরাহ শ্রেণির নাম | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | দেশ কোড | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | দেশ | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO দেশ কোড | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS বিভাগ | AI 7001 |
| `NSN_NIIN` | জাতীয় আইটেম নম্বর | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### রোল পণ্য

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | রোলের প্রস্থ (mm) | AI 8001 |
| `ROLL_LENGTH` | রোলের দৈর্ঘ্য (m) | AI 8001 |
| `CORE_DIAMETER` | কোর ব্যাস (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | গুটানোর দিক কোড | AI 8001 |
| `WINDING_DIRECTION` | গুটানোর দিক | AI 8001 |
| `SPLICES` | সংযোগস্থল | AI 8001 |

### IBAN

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | দেশ কোড | AI 8007 |
| `IBAN_COUNTRY_NAME` | দেশ | AI 8007 |
| `IBAN_CHECK_DIGITS` | চেক ডিজিট | AI 8007 |
| `IBAN_CHECK_VALID` | যাচাই | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | ক্রমিক নম্বর | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | চেক ডিজিট | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | ইস্যুকারী সংস্থা | AI 8040, 8041 |

পনেরোটি অঙ্ক `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]` রূপে বিশ্লিষ্ট হয়, আর RBI
হল TAC-এর প্রথম দুই অঙ্ক — অর্থাৎ `IMEI_RBI` আলাদা কোনো ক্ষেত্র নয়, `IMEI_TAC`-এর উপসর্গ।
`IMEI_FORMATTED` GSMA-র আদর্শ প্রদর্শন-বিন্যাস `AA-BBBBBB-CCCCCC-D` দেখায় (যেমন
`49-015420-323751-8`), যা TAC-কে RBI সীমানায় ভাগ করে; পুরোনো `6-2-6-1` বিন্যাস, যা এখন
বিলুপ্ত চূড়ান্ত-সংযোজন কোডের শুরুতে ভাগ করত, নির্গত হয় না।

`IMEI_RBI_NAME` `ImeiRbiData`-র মাধ্যমে RBI-কে বরাদ্দকারী সংস্থার নামে সমাধান করে, আর এটি
**সবার শেষে এবং কেবল তখনই** যুক্ত হয় যখন কোডটি সেখানে তালিকাভুক্ত থাকে। ওই সারণি তিনটি
দল আওতাভুক্ত করে:

- **এখনো বরাদ্দকারী সংস্থা** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, এবং সেই
  সঙ্গে `99` Global Hexadecimal Administrator ও `98` (সংরক্ষিত)।
- **পরীক্ষামূলক পরিসর** — `00` এবং `02`–`09`; এগুলো প্রকৃত বরাদ্দ নয়, পরীক্ষামূলক IMEI
  নির্দেশ করে। এগুলো `ImeiRbiData.isTestCode(code)` দিয়ে জিজ্ঞাসা করুন।
- **আর বরাদ্দ করে না এমন সংস্থা** — যেমন `49` (BZT/BAPT, জার্মানি), `44` (BABT, যুক্তরাজ্য)
  ও `91` (MSAI, ভারত)-এর মতো ঐতিহাসিক সংস্থা। এগুলো `ImeiRbiData.isNoLongerAllocating(code)`
  দিয়ে জিজ্ঞাসা করুন। এই কোড বহনকারী যন্ত্রগুলো স্বাভাবিক এবং এখনো সচল; কেবল নতুন বরাদ্দই
  থেমে গেছে, তাই এটি প্রতিবেদনযোগ্য তথ্য, বৈধতার ইঙ্গিত মোটেই নয়।

`IMEI_RBI_NAME`-এর অনুপস্থিতির মানে "এই RBI আমাদের সারণিতে নেই", **এই নয়** যে "IMEI অবৈধ":
সারণিটি সরাসরি GSMA থেকে নয়, একটি প্রকাশিত RBI তালিকা থেকে সংকলিত, তাই সদ্য নিযুক্ত
সংস্থাগুলোর চেয়ে তা পিছিয়ে থাকতে পারে। এর অনুপস্থিতি থেকে কোনো যাচাই-সিদ্ধান্ত টানবেন না;
RBI কোনো চেক ক্যারেক্টার নয়। ব্যাখ্যার তালিকা ধরে চলা কোড অবস্থান ধরে সূচি করার বদলে এর
অনুপস্থিতি সহ্য করতে সক্ষম হওয়া উচিত।

### SIM শনাক্তকারী (EID / ICCID)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | শিল্প বিভাগ | AI 8042 |
| `EID_BODY` | EID মূল অংশ | AI 8042 |
| `EID_CHECK_DIGIT` | চেক ডিজিট | AI 8042 |
| `ICCID_BODY` | ICCID মূল অংশ | AI 8043 |
| `ICCID_EXTENSION` | এক্সটেনশন | AI 8043 |

`SIM_MII` প্রথম **দুটি** অঙ্ক (`89`) ধারণ করে — এই জোড়াটিই ITU-T E.118 টেলিযোগাযোগের
জন্য বরাদ্দ করে। ISO/IEC 7812 নিজে MII-কে **কেবল প্রথম অঙ্ক** হিসেবে সংজ্ঞায়িত করে, তাই
`SIM_MII_NAME` শ্রেণিটি `Iso7812Data`-র মাধ্যমে শুরুর `8` অঙ্ক থেকে সমাধান করে — যার ফলে
পাওয়া যায় "Healthcare, telecommunications and other future industry assignments"। ফলে
প্রতিটি সুগঠিত EID-এর জন্য এটি একই থাকে; মানদণ্ড পর্যন্ত অনুসরণযোগ্যতার জন্যই এটি
প্রতিবেদন করা হয়, কোনো পার্থক্যসূচক হিসেবে নয়।
`Iso7812Data.nameForCode(digit)` একটিমাত্র অঙ্ক নেয়, আর `nameForIdentifier(prefix)`
দীর্ঘতর উপসর্গ গ্রহণ করে ও তার প্রথম অঙ্কটি পড়ে।

`SIM_MII_NAME` কেবল `EidEnricher` (AI 8042) নির্গত করে। `IccidEnricher` (AI 8043)
`SIM_MII` দেখায়, কিন্তু শ্রেণিটি নয়।

### সার্টিফিকেশন রেফারেন্স

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | ক্রমিক সংখ্যা | AI 7230–7239 |
| `CERT_SCHEME_CODE` | সার্টিফিকেশন স্কিম কোড | AI 7230–7239 |
| `CERT_SCHEME_NAME` | সার্টিফিকেশন স্কিম | AI 7230–7239 |
| `CERT_REFERENCE` | সার্টিফিকেশন রেফারেন্স | AI 7230–7239 |

### GS1 UIC

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC কোড | AI 7040 |
| `UIC_EXTENSION_1` | এক্সটেনশন ১ | AI 7040 |
| `UIC_IMPORTER_INDEX` | আমদানিকারক সূচক | AI 7040 |

### শিশুর জন্মক্রম

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | জন্ম অবস্থান | AI 7258 |
| `BIRTH_TOTAL` | মোট জন্ম | AI 7258 |
| `BIRTH_SEQUENCE` | জন্ম ক্রম | AI 7258 |

### গ্লোবাল মডেল নম্বর (GMN)

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | মডেল রেফারেন্স | AI 8013 |
| `GMN_CHECK_PAIR` | চেক জোড়া | AI 8013 |

### HIDRI

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | ডিভাইস রেফারেন্স | AI 8014 |
| `HIDRI_CHECK_PAIR` | চেক জোড়া | AI 8014 |

### CPID

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | উপাদান ও যন্ত্রাংশ রেফারেন্স | AI 8010–8011 |

### দশমিক ও পরিমাপের মান

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | দশমিক মান | নিহিত দশমিক স্থানযুক্ত সাংখ্যিক AI (31xx–36xx) |
| `DECIMAL_AMOUNT` | পরিমাণ | মূল্য AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | শতাংশ | AI 394n |
| `DECIMAL_PLACES` | দশমিক স্থান | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE`-এর সঙ্গে |
| `PERCENTAGE_FORMAT` | শতাংশ বিন্যাস | AI 394n |
| `ISO_UNIT_CODE` | ISO একক কোড | পরিমাপ AI |
| `ISO_UNIT_NAME` | ISO এককের নাম | পরিমাপ AI |
| `MONETARY_AMOUNT` | আর্থিক পরিমাণ | মূল্য AI |
| `MONETARY_AMOUNT_DISPLAY` | আর্থিক পরিমাণ (ফরম্যাট করা) | মূল্য AI |

### ভৌগোলিক স্থানাঙ্ক

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `LATITUDE` | অক্ষাংশ | AI 4309 |
| `LONGITUDE` | দ্রাঘিমাংশ | AI 4309 |
| `GEO_COORDINATES` | ভৌগোলিক স্থানাঙ্ক | AI 4309 |
| `LATITUDE_DMS` | অক্ষাংশ (DMS) | AI 4309 |
| `LONGITUDE_DMS` | দ্রাঘিমাংশ (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | ভৌগোলিক স্থানাঙ্ক (DMS) | AI 4309 |

### উৎপাদন পদ্ধতি

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | উৎপাদন পদ্ধতি কোড | AI 7010 |
| `PRODUCTION_METHOD` | উৎপাদন পদ্ধতি | AI 7010 |

### AIDC মাধ্যমের ধরন

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC মিডিয়া টাইপ কোড | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC মিডিয়া টাইপ | AI 7241 |

### মোটের মধ্যে টুকরা

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | টুকরা নম্বর | AI 8006 |
| `PIECE_TOTAL` | মোট টুকরা | AI 8006 |
| `PIECE_OF_TOTAL` | মোটের মধ্যে টুকরা | AI 8006 |

### উপাদান বিভাজন

এই কী-গুলো Java-য় লেখা কোনো সমৃদ্ধক নয়, বরং `content/ai-content.json`-এর ঘোষণামূলক
উপাদান-বিভাজন নির্গত করে — সবগুলোই কোনো যৌগিক AI মানের নামাঙ্কিত অংশ দেখায়। এই পরিশিষ্টের
অন্য প্রতিটি কী-এর বিপরীতে, **এগুলোর জন্য `GS1Constants_Enricher`-এ কোনো ধ্রুবক নেই**:
স্ট্রিং লিটারেল ধরে মেলান, অথবা প্রকারটি `GS1AIInterpretation.getType()` থেকে পড়ুন।

| প্রকার কী | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | চেক ডিজিট | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | ক্রমিক নম্বর | AI 253, 255, 8003 |
| `POSTAL_CODE` | পোস্টাল কোড | AI 421 |
| `PROCESSOR_ID` | প্রসেসর শনাক্তকারী | AI 7030–7039 |

লক্ষ করুন, এখানকার `CHECK_DIGIT` হল সাধারণ উপাদান-বিভাজন কী, যা উপরের সমৃদ্ধক-নির্দিষ্ট
`GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` ও `EID_CHECK_DIGIT` কী থেকে
পৃথক।

### বিবিধ

| কী ধ্রুবক | প্রদর্শনের লেবেল | যার তৈরি |
|--------------|---------------|-------------|
| `FLAG_VALUE` | মান | বুলিয়ান / পতাকা AI (4321–4323) |
| `DECODED_TEXT` | ডিকোড করা টেক্সট | মুক্ত-পাঠ AI |
