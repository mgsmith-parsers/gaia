# GaiaParser — 快速入門

大約十分鐘，就將一個 GS1 條碼酬載解析成結構化、已驗證、供人閱讀嘅資料。呢個係短路徑；
**[GaiaParser 開發者指南](GaiaParser-Cantonese.md)** 先係完整參考，而
**[GaiaBuilder](GaiaBuilder-Cantonese.md)** 就涵蓋相反嘅方向（建構元素字串同 Digital Link
URI）。

## 目錄

1. [將 Gaia 加入你嘅專案](#1-將-gaia-加入你嘅專案)
2. [解析啲嘢](#2-解析啲嘢)
3. [讀個結果](#3-讀個結果)
4. [處理失敗嘅解析](#4-處理失敗嘅解析)
5. [兩件會咬親你嘅事](#5-兩件會咬親你嘅事)
6. [掃描器前綴同 Digital Link 自動搞掂](#6-掃描器前綴同-digital-link-自動搞掂)
7. [做少啲嘢：解析模式](#7-做少啲嘢解析模式)
8. [改語言同日期格式](#8-改語言同日期格式)
9. [清理污糟嘅輸入](#9-清理污糟嘅輸入)
10. [跟住去邊度](#10-跟住去邊度)

---

## 1. 將 Gaia 加入你嘅專案

Gaia 冇發布到 Maven Central，所以要將核心建構一次，然後安裝落你嘅本機儲存庫：

```bash
cd gaia && mvn install
```

跟住就宣告依賴：

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

你要寫嘅依賴清單就只有噉多。個 jar 係薄嘅，所以 Gaia 唯一嗰個編譯範圍嘅依賴
——`com.fasterxml.jackson.core:jackson-databind`——會經傳遞依賴帶埋落嚟；如果你個
建構已經釘死咗某個 Jackson 版本，噉就以你嗰個為準，Gaia 會跟住用。
Gaia 以 **Java 11** 為目標，同一個 jar 喺之後每一個 JVM 上面都可以原封不動噉行。

> 啱啱開始上手嗰陣，跳過核心嘅測試套件（`mvn install -DskipTests`）可以將幾分鐘
> 變成幾秒。

---

## 2. 解析啲嘢

一個類別，零設定：

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

`parse(String)` 會行 **足** 成條流程：語法、內容驗證同詮釋。
呢個係啱嘅預設——如果你量度到有理由收窄佢，遲啲再收窄。

---

## 3. 讀個結果

已解析嘅 AI 存喺 `ParseResult.getAiObject()` 入面。要攞某一個嗰陣，用代碼而唔好用位置：

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

每個元素都帶住一份 **詮釋** 清單——即係啲原始數字背後解碼出嚟嘅意義，
由詮釋階段所產生：

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` 係已本地化嘅，係用嚟顯示嘅。想喺程式碼入面 *讀* 一個值，就改為用佢
穩定嘅型別鍵嚟查：

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

唔同嘅 AI 會產生唔同嘅鍵——GTIN 會俾到佢嘅公司前綴、GTIN 類型同校驗碼；
價格就會俾到貨幣同小數金額。完整清單喺
[附錄 B](GaiaParser-Cantonese.md#附錄-b--詮釋鍵常數)，而啲常數就住喺
`GS1Constants_Enricher` 入面。唔係每個 AI 都有詮釋：一個自由文字嘅批次／批號
根本冇嘢可以推導，所以佢張清單係空嘅。

---

## 4. 處理失敗嘅解析

一個無效嘅酬載係正常結果，唔係例外——`parse` 對住壞嘅 GS1 資料永遠都唔會拋例外：

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**喺 `getId()` 上面分支，千祈唔好用訊息。** 啲訊息係已本地化嘅，佢哋嘅措辭
唔係契約——而且佢哋而家仲帶住一個已知嘅引號缺陷（上面嗰個雙重 `''`），
呢點喺 [錯誤參考](GaiaParser-Cantonese.md#錯誤參考) 有講。

兩條唔同嘅問題，兩個唔同嘅方法：

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

一個階段一失敗，解析就會停止再往下行，所以校驗碼錯咗即係話你會攞到驗證
錯誤，但係唔會有詮釋。

### 警告唔會令一個結果變成無效

有啲檢查係建議性質嘅。一個未能辨識嘅 GS1 公司前綴會被報告出嚟，但係個酬載
喺結構上依然係健全嘅：

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

想兩樣都攞嗰陣就用 `getIssues()`。如果你嘅工作流程必須拒絕未知前綴，就要明確噉
檢查 `getWarnings()`——`isValid()` 唔會幫你做呢件事。

---

## 5. 兩件會咬親你嘅事

### GS 分隔符，同埋點解略咗佢仲衰過出錯

一個可變長度嘅 AI 會一路行到遇上 **GS 字元**（ASCII `0x1D`，喺條碼碼制入面叫
FNC1）或者字串結尾為止。當佢後面仲跟住另一個 AI 嗰陣，呢個分隔符係
強制嘅：

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

略咗佢，你 **唔會** 收到錯誤——你會收到一個好肯定但係錯嘅答案：

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` 係 `X..20`，所以佢完全合法噉吞埋 `LOT-ABC21SN-98765`，而解析器根本
冇辦法知道嗰個唔係你想要嘅結果。下游冇任何嘢救得返呢個情況，所以要喺源頭
就將分隔符搞啱：將掃描器嘅位元組以 **ISO-8859-1** 讀取，噉 `0x1D` 就會保得住；
喺 Java 字串常值入面就寫 `""`。固定長度嘅 AI（`01`、`17`、`3103`）唔使分隔符——
解析器知道佢哋嘅長度。

### 大部分 AI 都唔可以獨立存在

批次／批號、序號、有效期同佢哋嗰班都係 *屬性*：GS1 General Specifications
要求佢哋要同一個識別鍵一齊出現，而 Gaia 亦都會強制執行。

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

加返個 GTIN 落去就過到。如果你真係要解析一個片段——譬如一個單元測試、一次
局部掃描——就將呢項檢查關咗：

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. 掃描器前綴同 Digital Link 自動搞掂

你唔使話畀 Gaia 聽個輸入係咩形狀——四種形式佢都偵測得到。掃描器俾咩你，
你就餵咩畀佢。

**AIM 碼制識別碼前綴** 標明碼制，會被自動剝除：

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** 會行同一套驗證同增益：

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

因為兩種形式都落到同一個 `GS1AIObject` 度，所以消費掃描結果嘅程式碼
唔使理究竟入嚟嘅係邊一種——而 `toElementString()`／`getCanonicalDigitalLink()`
仲可以喺兩者之間互轉。

一個 **8 位數嘅關聯前綴**（`12345678~…`）都會被剝除，並保留喺
`getCorrelationInfo()` 上面，如果你條流程有用嘅話。

---

## 7. 做少啲嘢：解析模式

預設會做晒所有嘢。當你淨係需要答案嘅一部分嗰陣，就要少啲：

| 模式 | 答到咩 | 成本 |
|---|---|---|
| `DATA_CARRIER` | 呢個係邊種碼制？ | 最平——完全唔做 AI 解析，`getAiObject()` 係 `null` |
| `SYNTAX` | AI 代碼同長度嘅格式啱唔啱？ | 冇校驗碼，冇詮釋 |
| `CONTENT` | 呢個係咪合法嘅 GS1 資料？ | 完整驗證，冇詮釋 |
| `INTERPRETATION` | 佢即係咩意思？ | **預設**——所有嘢 |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

當你要大批量驗證而又永遠唔會顯示個拆解嗰陣就用 `CONTENT`；
當你淨係要將一個掃描結果分流去啱嘅處理器嗰陣就用 `DATA_CARRIER`。

---

## 8. 改語言同日期格式

錯誤訊息、詮釋標籤同 AI 描述都譯咗做 **35 種語言**；日期就你想點顯示都得。
呢啲全部都喺同一個不可變嘅 `ParseConfig` 入面：

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

啲值永遠都唔會本地化——淨係標籤、描述同訊息會——所以 `"2026-12-31"` 同
`"09506000134352"` 喺每種語言入面意思都一樣。喺啟動嗰陣整一次設定
然後共用佢；佢係不可變嘅。

---

## 9. 清理污糟嘅輸入

如果你嘅來源會送出印刷用嘅 HRI 括號或者多餘嘅空格，核心附帶咗兩個 **輸入
修改器**，可以喺解析之前將酬載修返好：

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

預設乜都唔會啟用，而且兩者都帶住注意事項——空格同括號本身就係合法嘅 GS1
資料字元，所以只可以將佢哋用喺你確知嘅來源上面。請睇
[內建修改器](GaiaParser-Cantonese.md#內建修改器)，嗰度亦都解釋咗點解剝除括號
之後必須還原返嗰啲括號所隱含嘅分隔符。

---

## 10. 跟住去邊度

- **[GaiaParser 開發者指南](GaiaParser-Cantonese.md)** — 流程嘅詳細內容、完整嘅結果
  模型、每一個錯誤代碼，同 AI 及詮釋鍵嘅附錄。
- **[GaiaBuilder 開發者指南](GaiaBuilder-Cantonese.md)** — 由 AI／值對建構元素字串同
  Digital Link URI。
- **[Gaia API HTTP 參考](../../gaia-api-reference.md)** — 同一個引擎經 HTTP 提供，
  如果你唔想將個程式庫嵌入去嘅話。
- **[ai-codes.txt](../../ai-codes.txt)** — 一份方便快查嘅平面 `(AI) TITLE` 清單。

### 五行版

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
