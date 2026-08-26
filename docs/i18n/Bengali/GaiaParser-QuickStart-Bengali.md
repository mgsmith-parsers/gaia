# GaiaParser — কুইক স্টার্ট

দশ মিনিটের মতো সময়ে একটি GS1 বারকোড পেলোডকে কাঠামোবদ্ধ, যাচাইকৃত, মানুষের পাঠযোগ্য ডেটায়
পরিণত করুন। এটি সংক্ষিপ্ত পথ; **[GaiaParser ডেভেলপার গাইড](GaiaParser-Bengali.md)** হল সম্পূর্ণ
রেফারেন্স, আর **[GaiaBuilder](GaiaBuilder-Bengali.md)** বিপরীত দিকটি (এলিমেন্ট স্ট্রিং ও Digital
Link URI তৈরি) আলোচনা করে।

## সূচিপত্র

1. [আপনার প্রকল্পে Gaia যোগ করুন](#১-আপনর-পরকলপ-gaia-যগ-করন)
2. [কিছু একটা পার্স করুন](#২-কছ-একট-পরস-করন)
3. [ফলাফল পড়ুন](#৩-ফলফল-পডন)
4. [ব্যর্থ পার্সিং সামলান](#৪-বযরথ-পরস-সমলন)
5. [দুটি বিষয় যা আপনাকে হোঁচট খাওয়াবে](#৫-দট-বষয-য-আপনক-হচট-খওযব)
6. [স্ক্যানার উপসর্গ ও Digital Link এমনিতেই চলে](#৬-সকযনর-উপসরগ-ও-digital-link-এমনতই-চল)
7. [কম কাজ করুন: পার্স মোড](#৭-কম-কজ-করন-পরস-মড)
8. [ভাষা ও তারিখের বিন্যাস বদলান](#৮-ভষ-ও-তরখর-বনযস-বদলন)
9. [এলোমেলো ইনপুট পরিষ্কার করুন](#৯-এলমল-ইনপট-পরষকর-করন)
10. [এরপর কোথায়](#১০-এরপর-কথয)

---

## ১. আপনার প্রকল্পে Gaia যোগ করুন

Gaia Maven Central-এ প্রকাশিত নয়, তাই কোরটি একবার গড়ে আপনার স্থানীয় রিপোজিটরিতে ইনস্টল
করুন:

```bash
cd gaia && mvn install
```

তারপর এর উপর নির্ভরতা দিন:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

আপনাকে এটুকু নির্ভরতাই লিখতে হবে। jar-টি হালকা: Gaia-র একমাত্র কম্পাইল-পরিসরের নির্ভরতা —
`com.fasterxml.jackson.core:jackson-databind` — সংক্রমণশীলভাবেই আসে; আর আপনার বিল্ড যদি
আগে থেকেই কোনো Jackson সংস্করণ স্থির করে রাখে, সেই স্থিরীকরণই বহাল থাকে এবং Gaia সেটিই
ব্যবহার করে। Gaia **Java 11** লক্ষ্য করে, আর একই jar পরবর্তী প্রতিটি JVM রিলিজে অপরিবর্তিতভাবে
চলে।

> শুরুর দিকে কোর পরীক্ষার সেট এড়িয়ে যাওয়া (`mvn install -DskipTests`) কয়েক মিনিটকে কয়েক
> সেকেন্ডে নামিয়ে আনে।

---

## ২. কিছু একটা পার্স করুন

একটিমাত্র ক্লাস, কোনো বিন্যাস নেই:

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

`parse(String)` **পুরো** পাইপলাইনটাই চালায়: বাক্যরীতি, বিষয়বস্তু যাচাই, ব্যাখ্যা। এটাই সঠিক
ডিফল্ট — যখন এর জন্য মাপা কোনো কারণ পাবেন, তখনই সংকুচিত করুন।

---

## ৩. ফলাফল পড়ুন

`ParseResult.getAiObject()` সমাধান করা AI-গুলো ধরে রাখে। নির্দিষ্ট কোনো AI অবস্থান ধরে নয়,
কোড ধরে নিন:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

প্রতিটি এলিমেন্ট একটি **ব্যাখ্যা** তালিকা বহন করে — কাঁচা অঙ্কের পেছনের উন্মোচিত অর্থ, যা
ব্যাখ্যার পর্যায় তৈরি করে:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` স্থানীয়কৃত এবং প্রদর্শনের জন্য। কোডের ভেতরে কোনো মান *পড়তে* হলে সেটিকে বরং
তার অপরিবর্তনীয় প্রকার-কী ধরে খুঁজুন:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

আলাদা AI আলাদা কী তৈরি করে — GTIN দেয় তার কোম্পানি প্রিফিক্স, GTIN প্রকার ও চেক ডিজিট; মূল্য
দেয় মুদ্রা ও দশমিক পরিমাণ। পূর্ণ তালিকা আছে
[পরিশিষ্ট খ](GaiaParser-Bengali.md#পরশষট-খ--বযখয-ক-ধরবক)-এ, আর ধ্রুবকগুলো আছে
`GS1Constants_Enricher`-এ। প্রতিটি AI-এর ব্যাখ্যা থাকে না: ব্যাচ/লট নম্বর হল মুক্ত পাঠ, যা
থেকে উদ্ভাবনযোগ্য কিছু নেই, তাই তার তালিকা খালি।

---

## ৪. ব্যর্থ পার্সিং সামলান

অবৈধ পেলোড একটি স্বাভাবিক ফলাফল, ব্যতিক্রম নয় — খারাপ GS1 ডেটার জন্য `parse` কখনোই ব্যতিক্রম
ছোড়ে না:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` ধরে শাখা করুন, কখনোই বার্তা ধরে নয়।** বার্তাগুলো স্থানীয়কৃত আর তাদের শব্দচয়ন
কোনো চুক্তি নয় — তাছাড়া সেগুলো এই মুহূর্তে একটি জানা উদ্ধৃতি-ত্রুটিও বহন করে (উপরের `''`
দ্বিগুণতা), যা [ত্রুটি রেফারেন্স](GaiaParser-Bengali.md#তরট-রফরনস)-এ নথিভুক্ত।

দুটি ভিন্ন প্রশ্ন, দুটি ভিন্ন পদ্ধতি:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

কোনো পর্যায় ব্যর্থ হওয়া মাত্রই পার্সিং আর নিচে নামে না, তাই ভুল চেক ডিজিটের মানে হল আপনি
যাচাইয়ের ত্রুটি পাবেন কিন্তু কোনো ব্যাখ্যা পাবেন না।

### সতর্কতা ফলাফলকে অবৈধ করে না

কিছু পরীক্ষা পরামর্শমূলক। অপরিচিত কোনো GS1 কোম্পানি প্রিফিক্স প্রতিবেদিত হয়, তবু পেলোডটি
সুগঠিতই থাকে:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

দুটোই একসঙ্গে চাইলে `getIssues()` ব্যবহার করুন। আপনার কর্মপ্রবাহে যদি অপরিচিত প্রিফিক্স
প্রত্যাখ্যান করাই বাধ্যতামূলক হয়, তবে স্পষ্টভাবে `getWarnings()` পরীক্ষা করুন —
`isValid()` আপনার হয়ে তা করবে না।

---

## ৫. দুটি বিষয় যা আপনাকে হোঁচট খাওয়াবে

### GS বিভাজক, আর সেটি বাদ দেওয়া কেন ত্রুটির চেয়েও খারাপ

পরিবর্তনশীল-দৈর্ঘ্যের একটি AI চলে একটি **GS অক্ষর** (ASCII `0x1D`, বারকোড সিম্বোলজিতে যাকে
FNC1 বলা হয়) পর্যন্ত, নয়তো স্ট্রিংয়ের শেষ পর্যন্ত। এর পরে আরেকটি AI এলে সেই বিভাজকটি
বাধ্যতামূলক:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

সেটি বাদ দিলে আপনি ত্রুটি পাবেন **না** — পাবেন পূর্ণ আত্মবিশ্বাসে দেওয়া একটি ভুল উত্তর:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` হল `X..20`, তাই গোটা `LOT-ABC21SN-98765` গিলে ফেলাই যুক্তিসঙ্গত, আর এটি যে আপনার
উদ্দেশ্য ছিল না তা জানার কোনো উপায়ই পার্সারের নেই। পরে আর কিছুই এটি ফিরিয়ে আনতে পারে না,
তাই বিভাজকটি উৎসেই ঠিক করুন: `0x1D` যাতে টিকে থাকে সেজন্য স্ক্যানারের বাইট **ISO-8859-1**
হিসেবে পড়ুন, আর Java স্ট্রিং লিটারেলে `""` লিখুন। নির্দিষ্ট-দৈর্ঘ্যের AI-এর (`01`, `17`,
`3103`) বিভাজক লাগে না — পার্সার তাদের দৈর্ঘ্য জানে।

### অধিকাংশ AI একা দাঁড়ায় না

ব্যাচ/লট, ক্রম নম্বর, মেয়াদ উত্তীর্ণের তারিখ ও এ ধরনের সবকিছু *বৈশিষ্ট্য*: GS1 General
Specifications দাবি করে এগুলোর সঙ্গে একটি শনাক্তকরণ কী থাকবে, আর Gaia তা প্রয়োগ করে।

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

একটি GTIN যোগ করুন, তাহলেই উতরে যাবে। সত্যিই যদি কোনো অংশ পার্স করতে হয় — কোনো ইউনিট
পরীক্ষা, আংশিক কোনো স্ক্যান — তবে পরীক্ষাটি বন্ধ করে দিন:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## ৬. স্ক্যানার উপসর্গ ও Digital Link এমনিতেই চলে

ইনপুট কোন রূপে আছে তা Gaia-কে বলে দিতে হয় না — চারটি রূপই সে শনাক্ত করে। আপনার স্ক্যানার
যা দিয়েছে তা-ই সোজা পাঠিয়ে দিন।

**AIM সিম্বোলজি আইডেন্টিফায়ার উপসর্গ** সিম্বোলজি নির্ধারণ করে এবং আপনা-আপনিই ছেঁটে যায়:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** একই যাচাই ও সমৃদ্ধকরণের ভেতর দিয়েই যায়:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

যেহেতু দুটি রূপই একই `GS1AIObject`-এ গিয়ে পৌঁছায়, তাই স্ক্যান ব্যবহারকারী কোডকে কোনটি এসেছে
তা নিয়ে ভাবতে হয় না — আর `toElementString()` / `getCanonicalDigitalLink()` একটিকে অন্যটিতে
রূপান্তরও করে দেয়।

**৮-অঙ্কের সহসম্বন্ধ উপসর্গও** (`12345678~…`), আপনার প্রবাহ সেটি ব্যবহার করলে, একইভাবে ছেঁটে
`getCorrelationInfo()`-তে রাখা হয়।

---

## ৭. কম কাজ করুন: পার্স মোড

ডিফল্ট সবকিছুই করে। উত্তরের কেবল একটি অংশ দরকার হলে কম চান:

| মোড | কীসের উত্তর দেয় | খরচ |
|---|---|---|
| `DATA_CARRIER` | এটি কোন সিম্বোলজি? | সবচেয়ে সস্তা — কোনো AI পার্সিংই নয়, `getAiObject()` `null` |
| `SYNTAX` | AI কোড ও দৈর্ঘ্য কি সুগঠিত? | চেক ডিজিট নেই, ব্যাখ্যা নেই |
| `CONTENT` | এটি কি বৈধ GS1 ডেটা? | পূর্ণ যাচাই, ব্যাখ্যা ছাড়া |
| `INTERPRETATION` | এর অর্থ কী? | **ডিফল্ট** — সবকিছু |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

বিপুল পরিমাণে যাচাই করছেন অথচ বিশ্লেষণ কখনোই দেখান না — তখন `CONTENT` বেছে নিন; আর কেবল
স্ক্যানটিকে সঠিক হ্যান্ডলারে পাঠানোই দরকার হলে `DATA_CARRIER`।

---

## ৮. ভাষা ও তারিখের বিন্যাস বদলান

ত্রুটি বার্তা, ব্যাখ্যা লেবেল ও AI বিবরণ **৩৫টি ভাষায়** অনূদিত; তারিখ আপনার পছন্দমতো
দেখানো যায়। এসবই একটিমাত্র অপরিবর্তনীয় `ParseConfig`-এ ধরা:

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

মান কখনোই স্থানীয়কৃত হয় না — কেবল লেবেল, বিবরণ ও বার্তাই হয় — তাই `"2026-12-31"` আর
`"09506000134352"` প্রতিটি ভাষাতেই একই অর্থ বহন করে। বিন্যাসটি শুরুতে একবার গড়ে ভাগ করে
নিন; এটি অপরিবর্তনীয়।

---

## ৯. এলোমেলো ইনপুট পরিষ্কার করুন

আপনার উৎস যদি মুদ্রিত HRI বন্ধনী বা এদিক-সেদিক ছড়ানো ফাঁকা স্থান পাঠায়, তবে কোরে দুটি
**ইনপুট মডিফায়ার** আছে যারা পার্সিংয়ের আগেই পেলোড ঠিক করে দেয়:

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

ডিফল্টে কোনোটিই সক্রিয় নয়, আর দুটিরই সতর্কবার্তা আছে — ফাঁকা স্থান ও বন্ধনী দুটোই বৈধ GS1
ডেটা অক্ষর, তাই কেবল সেই উৎসেই প্রয়োগ করুন যাকে আপনি চেনেন। দেখুন
[অন্তর্নির্মিত মডিফায়ার](GaiaParser-Bengali.md#অনতরনরমত-মডফযর), যেখানে এ-ও ব্যাখ্যা করা আছে
বন্ধনী ছাঁটার পর সেগুলো যে বিভাজক নির্দেশ করত তা কেন ফিরিয়ে আনতেই হয়।

---

## ১০. এরপর কোথায়

- **[GaiaParser ডেভেলপার গাইড](GaiaParser-Bengali.md)** — পাইপলাইনের বিস্তারিত, পূর্ণ ফলাফল-মডেল,
  প্রতিটি ত্রুটি কোড, আর AI ও ব্যাখ্যা-কী-এর পরিশিষ্ট।
- **[GaiaBuilder ডেভেলপার গাইড](GaiaBuilder-Bengali.md)** — AI/মান জোড়া থেকে এলিমেন্ট স্ট্রিং ও
  Digital Link URI গড়ুন।
- **[Gaia API HTTP রেফারেন্স](../../gaia-api-reference.md)** — লাইব্রেরি এমবেড না করতে চাইলে একই
  ইঞ্জিন HTTP-র মাধ্যমে।
- **[ai-codes.txt](../../ai-codes.txt)** — দ্রুত খোঁজার জন্য `(AI) TITLE`-এর একটি সমতল তালিকা।

### পাঁচ লাইনের সংস্করণ

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
