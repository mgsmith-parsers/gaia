# GAIA（GS1 Application Identifiers Analyser）— 開發者指南

## 目錄

1. [概覽](#概覽)
2. [關於 GS1 同 General Specifications](#關於-gs1-同-general-specifications)
3. [GS1 應用識別碼](#gs1-應用識別碼)
4. [快速入門](#快速入門)
5. [解析流程](#解析流程)
   - [前置階段 — 輸入修改器](#前置階段--輸入修改器)
   - [階段 0 — 關聯 ID](#階段-0--關聯-id)
   - [階段 1 — 輸入分流](#階段-1--輸入分流)
   - [階段 2 — 語法](#階段-2--語法)
   - [階段 3 — 內容](#階段-3--內容)
   - [階段 4 — 詮釋](#階段-4--詮釋)
6. [解析設定（`ParseConfig`）](#解析設定parseconfig)
   - [選項](#選項)
   - [本地化嘅訊息同標籤](#本地化嘅訊息同標籤)
   - [日期格式化](#日期格式化)
7. [輸入修改器](#輸入修改器)
   - [內建修改器](#內建修改器)
   - [寫一個修改器](#寫一個修改器)
   - [登記修改器](#登記修改器)
   - [查看修改器做過啲乜](#查看修改器做過啲乜)
   - [修改器嘅失敗處理](#修改器嘅失敗處理)
8. [解析模式](#解析模式)
   - [DATA_CARRIER 模式](#data_carrier-模式)
   - [SYNTAX 模式](#syntax-模式)
   - [CONTENT 模式](#content-模式)
   - [INTERPRETATION 模式（預設）](#interpretation-模式預設)
9. [關聯 ID](#關聯-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [處理結果](#處理結果)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry 同 DataCarrierType](#datacarrierentry-同-datacarriertype)
12. [錯誤參考](#錯誤參考)
13. [執行緒安全](#執行緒安全)
14. [附錄 A — AI 字串常數](#附錄-a--ai-字串常數)
    - [識別同序列化](#識別同序列化)
    - [日期同時間](#日期同時間)
    - [數量同量度 — 可變計量（公制）](#數量同量度--可變計量公制)
    - [數量同量度 — 可變計量（英制／美制）](#數量同量度--可變計量英制美制)
    - [價格同貨幣金額](#價格同貨幣金額)
    - [位置同運送](#位置同運送)
    - [產品屬性同可追溯性](#產品屬性同可追溯性)
    - [國家醫療保健報銷編號（NHRN）](#國家醫療保健報銷編號nhrn)
    - [醫療保健、GMN、HIDRI、CPID、個人資料](#醫療保健gmnhidricpid個人資料)
    - [內部／公司用途](#內部公司用途)
15. [附錄 B — 詮釋鍵常數](#附錄-b--詮釋鍵常數)
    - [日期同時間](#日期同時間)
    - [採收日期](#採收日期)
    - [GS1 公司前綴](#gs1-公司前綴)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [國家（ISO 3166）](#國家iso-3166)
    - [貨幣（ISO 4217）](#貨幣iso-4217)
    - [溫度](#溫度)
    - [性別（ISO 5218）](#性別iso-5218)
    - [水產物種（FAO）](#水產物種fao)
    - [北約庫存編號（NSN）](#北約庫存編號nsn)
    - [卷裝產品](#卷裝產品)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM 識別碼（EID／ICCID）](#sim-識別碼eidiccid)
    - [認證參考編號](#認證參考編號)
    - [GS1 UIC](#gs1-uic)
    - [嬰兒出生次序](#嬰兒出生次序)
    - [全球型號編號（GMN）](#全球型號編號gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [小數同量度值](#小數同量度值)
    - [地理座標](#地理座標)
    - [生產方式](#生產方式)
    - [AIDC 媒體類型](#aidc-媒體類型)
    - [總數中嘅件數](#總數中嘅件數)
    - [組件拆分](#組件拆分)
    - [雜項](#雜項)

---

## 概覽

`GaiaParser` 係解析 GS1 應用識別碼（AI）元素字串嘅入口。佢接受掃描器嘅原始輸出，可以係下面任何一種形式，然後回傳一個結構化嘅 `ParseResult`，入面包含所有已解析嘅 AI、驗證錯誤，以及（可選嘅）供人閱讀嘅詮釋：

- 純 AI 元素字串：`0109506000134352`
- 帶 AIM 碼制識別碼前綴嘅元素字串：`]C10109506000134352`
- GS1 Digital Link URI：`https://example.com/01/09506000134352`
- 以上任何一種，前面仲可以加一個 8 位數嘅關聯 ID：`12345678~0109506000134352`

**入口類別：** `tools.pantheum.gaia.GaiaParser`

> **初次接觸 Gaia？** 由 **[GaiaParser 快速入門](GaiaParser-QuickStart-Cantonese.md)** 開始——依賴項、第一次解析，同埋幾個最容易令人跌倒嘅地方，大約十分鐘睇得晒。本指南就係完整參考。

> 至於相反嘅操作——由 AI／值對 *建構* 格式正確嘅元素字串同 Digital Link URI——請睇 **[GaiaBuilder — 開發者指南](GaiaBuilder-Cantonese.md)**。

---

## 關於 GS1 同 General Specifications

**GS1** 係一個全球性嘅非牟利組織，負責制定同維護供應鏈識別同資料交換嘅開放標準。佢嘅標準用喺零售、醫療、物流、餐飲服務同好多其他行業，涵蓋由消費品包裝上面嘅產品條碼，一直到藥物劑量嘅序號追蹤。

呢個解析器所實作嘅一切，權威依據都係 **GS1 General Specifications**——一份文件就定義咗以下全部：

- 所有應用識別碼（AI）代碼、佢哋嘅資料標題、格式同驗證規則
- 建構同編碼 AI 元素字串嘅語法規則
- 條碼碼制嘅要求同 AIM 碼制識別碼嘅分配
- 校驗碼同校驗字元嘅演算法
- 兩位數年份嘅判定（滑動視窗規則）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 同其他載體嘅規格

GS1 General Specifications 每年都會更新。最新版本同相關資源可以喺呢度搵到：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 實作咗 GS1 General Specifications 嘅 **第 26.0 版（已通過，2026 年 1 月）**。

GS1 Digital Link URI 由一份配套標準 **GS1 Digital Link: URI Syntax** 規範，佢定義咗解析器處理 Digital Link 輸入時所套用嘅主要識別鍵、鍵限定詞排序，同資料屬性編碼：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 實作咗 GS1 Digital Link: URI Syntax 標準嘅 **第 1.7.0 版（已通過，2026 年 8 月）**。

本文件通篇嘅章節引用都係指 GS1 General Specifications（例如「Table 7-5」、「section 7.12」），只有 Digital Link 嘅章節編號（例如「§4.9」、「§4.12」）例外，嗰啲係指 GS1 Digital Link: URI Syntax 標準。

---

## GS1 應用識別碼

**GS1 應用識別碼（AI）** 係一個短嘅數字前綴——兩到四位數——用嚟標明緊接住佢後面嗰段資料嘅意義同格式。AI 喺 GS1 General Specifications 入面定義，涵蓋各式各樣嘅供應鏈資料：產品識別碼、日期、數量、批號、序號、量度值、URL 等等。

### AI 元素嘅結構

每個 AI 元素由兩部分組成：

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

AI 代碼一定係數字。資料值緊接住佢，代碼同值之間冇任何分隔符。

### 固定長度 AI 對可變長度 AI

AI 分為兩類：

| 類型 | 行為 | 例子 |
|---|---|---|
| **固定長度** | 字元數目固定，永遠整段讀完 | AI `01`（GTIN）——永遠 14 位數 |
| **可變長度** | 由 1 個字元到某個上限；遇到 GS 分隔符或者輸入結尾就結束 | AI `10`（批次／批號）——1 到 20 個英數字元 |

一個 AI 究竟係固定定係可變，完全由佢喺 GS1 規格入面嘅定義決定——解析器唔會估。

### 多 AI 元素字串

多個 AI 可以串埋一齊組成單一嘅元素字串。固定長度嘅 AI 可以直接串連，因為解析器永遠都知道要讀幾多個字元。可變長度嘅 AI 只要後面仲有另一個 AI，就必須用 **GS 字元**（ASCII `0x1D`，喺條碼碼制入面又叫 FNC1）作結尾，噉解析器先至知道一個值喺邊度完、下一個 AI 代碼喺邊度開始。

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

喺 Java 字串常值入面，用 Unicode 跳脫序列 `"\u001D"` 嚟寫 GS 字元。

### 常見嘅 AI

| AI | 資料標題 | 格式 | 範例值 |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6（YYMMDD） | `261231` |
| `17` | USE BY or EXPIRY | N6（YYMMDD） | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500`（＝ 1.500 kg） |
| `3922` | PRICE | N..15 | `91234`（＝ 912.34，單一貨幣區） |
| `710` | NHRN PZN | X..20 | `12345678` |

> 四位數嘅量度或者價格 AI，佢嘅 **第四位數** 編碼咗隱含小數位嘅數目——`3103` 係以公斤計、帶 3 個小數位嘅淨重（`001500` ＝ 1.500 kg），而 `3102` 就會將同一串數字讀成 15.00 kg。上面個 `格式` 欄顯示嘅係 *資料* 嘅格式；每個 AI 完整嘅 `getFormatString()` 仲會包埋 AI 本身（例如 `3103` 就係 `N4+N6`）。

### 供人閱讀嘅詮釋（HRI）

慣用嘅人類可讀形式，係喺每個 AI 代碼嘅值前面用括號將代碼括住，元素之間隔一個空格：

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS 分隔符唔會喺 HRI 入面顯示。`GS1AIObject.toHriString()` 就會產生呢個格式。

### 四位數嘅 AI 代碼

有啲 AI 用四位數而唔係兩位。頭兩位數標明 AI 族系；第三同／或第四位數承載額外嘅語意（例如量度類 AI 嘅隱含小數點位置）。解析器會自動由元素字串解析出完整嘅 AI 代碼——呼叫方永遠都係用完整代碼（例如 `"3102"`，唔係淨係 `"31"`）。

---

## 快速入門

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

> **GS 分隔符：** 喺多 AI 字串入面，可變長度嘅 AI 必須用 GS 字元（ASCII `0x1D`）分隔。喺 Java 字串常值入面用 `"\u001D"`。

---

## 解析流程

### 前置階段 — 輸入修改器

如果 `ParseConfig` 帶有任何 **輸入修改器**，佢哋會喺所有嘢之前行——喺剝除關聯前綴之前、喺偵測載體之前、喺入到 GS1 流程之前。每個修改器都會為下一個修改器改寫原始輸入，而下面所有階段都係處理成條鏈嘅輸出。

預設係唔會設定任何修改器嘅，所以除非你主動選用，否則呢個前置階段等於乜都冇做。請睇 [輸入修改器](#輸入修改器)。

---

### 階段 0 — 關聯 ID

喺任何 GS1 處理之前，`GaiaParser` 會檢查輸入係咪以一個可選嘅 **關聯 ID 前綴** 開頭：啱啱好 8 個 ASCII 十進位數字，後面跟住一個波浪號（`~`），例如 `12345678~`。

如果有呢個前綴，佢就會被剝除，並且以 `CorrelationInfo` 嘅形式存喺回傳嘅 `ParseResult` 上面。之後所有階段都係處理剝除咗前綴嘅酬載。如果冇呢個前綴，輸入就原封不動噉傳落去。

詳情請睇 [關聯 ID](#關聯-id)。

---

### 階段 1 — 輸入分流

剝除關聯前綴之後，`GaiaParser` 會檢查（已剝除嘅）輸入係咪以 **AIM 碼制識別碼** 開頭：一個三字元前綴，形式為 `]` ＋ ASCII 字母 ＋ ASCII 數字（例如 GS1-128 係 `]C1`，GS1 DataMatrix 係 `]d2`，GS1 DataBar／GS1 Composite 係 `]e0`）。

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

如果個載體唔支援 GS1 AI（例如郵政條碼），解析就會即刻停低，並發出一個 `GE-D002` 錯誤。

---

### 階段 2 — 語法

無條件執行。由兩個子步驟組成：

**2a. 分詞（`AISyntaxParser`）**
- 用 GS1 前綴表（GS1 General Specifications Table 7-5）由頭兩個字元讀出 AI 代碼嘅長度。
- 固定長度嘅 AI 由輸入讀取準確嘅位元組數目。
- 可變長度嘅 AI 一路讀到 GS 字元或者輸入結尾為止。
- 多組件嘅 AI 會將佢個值切成逐個組件嘅片段。

**2b. 結構驗證（`SyntaxValidator`）**
- 檢查有冇重複嘅 AI（`GE-S004`）。
- 檢查必要嘅 AI 相依關係，例如 AI `02` 需要 AI `37`（`GE-S005`）。
- 檢查互斥嘅 AI 配對（`GE-S006`）。

呢個階段嘅錯誤，等級係 `SYNTAX_ERROR`（分詞器）或者 `INTEGRITY_ERROR`（結構）。只要有 **任何** 錯誤——無論係分詞器定結構嘅——整條流程就會停低，內容同詮釋階段都會跳過。

---

### 階段 3 — 內容

只有喺階段 2 冇產生任何錯誤（分詞器同結構兩方面都冇）嘅時候先至會行。逐個元素嘅流程如下（每一步只有喺上一步冇出錯先至會行）：

| 步驟 | 驗證器 | 錯誤代碼 |
|---|---|---|
| 正規表達式檢查 | `RegexValidator` | `GE-C001` |
| 組件字元集＋格式 | `ComponentValidator` | `GE-C005` ＋ 逐條件嘅格式代碼（`GE-C054`–`GE-C115`） |
| 校驗碼／校驗字元 | `CheckDigitCharacterValidator` | `GE-C003`、`GE-C004` |
| 自訂語意驗證 | `ContentValidatorRegistry` | 逐條件嘅內容代碼（`GE-C116`–`GE-C170`） |

呢個階段嘅錯誤，等級係 `FORMAT_ERROR` 或者 `DATA_ERROR`，只有一個例外：GS1 鍵類
AI 上面嘅 GS1 公司前綴檢查係建議性質，等級為 `WARNING`（請睇
[錯誤參考](#錯誤參考)），所以一個未能辨識嘅公司前綴，本身唔會令個結果變成無效。

---

### 階段 4 — 詮釋

只有喺 `INTERPRETATION` 模式，而且冇任何元素帶住之前階段嘅錯誤嗰陣先至會行。`InterpretationEngine` 會為每個元素加上有標籤嘅中繼資料：

- 日期重新格式化為 `dd/mm/yyyy`
- GTIN 校驗碼分解同 GS1 公司前綴查找
- ISO 3166 國家名稱
- ISO 4217 貨幣名稱同符號
- 已解碼嘅小數金額
- HRI（供人閱讀嘅詮釋）片段

結果會以 `GS1AIInterpretation` 條目嘅形式附加喺每個 `GS1AIObjectElement` 上面。

---

## 解析設定（`ParseConfig`）

`GaiaParser` 淨係開放咗兩個入口：

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` 會用 **預設設定** 執行：`INTERPRETATION` 模式、小端序日期（`dd/mm/yyyy`）配 `/` 分隔符同四位數年份，以及 **英文** 錯誤訊息。想改動任何一項——包括解析模式——就用流暢式建構器整一個 `ParseConfig`，再用兩個參數嗰個多載版本。

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

所有選項嘅列舉型別都喺 `GaiaConstants` 入面。

### 選項

| 建構器方法 | 列舉（`GaiaConstants`） | 預設 | 作用 |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | 流程深度——請睇 [解析模式](#解析模式)。 |
| `language(...)`      | `Language`      | `ENGLISH`        | 錯誤訊息、詮釋標籤，**同埋** AI 描述嘅語言。 |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | 日期組件次序：`LITTLE`（`dd/mm/yyyy`）、`MIDDLE`（`mm/dd/yyyy`）、`BIG`（`yyyy/mm/dd`）。 |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | 日期組件之間嘅字元：`SLASH`（`/`）、`HYPHEN`（`-`）、`PERIOD`（`.`）。 |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT`（`12`）或者 `THREE_LETTER`（`DEC`）。 |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT`（`2026`）或者 `TWO_DIGIT`（`26`）。 |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | 跳過結構上嘅「需要」檢查（`GE-S005`）。 |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | 跳過結構上嘅「互斥」檢查（`GE-S006`）。 |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` ／ 類別名稱 | 冇 | 喺解析之前改寫原始輸入嘅程式碼——兩個 [內建修改器](#內建修改器) 加上你自己寫嘅。請睇 [輸入修改器](#輸入修改器)。 |

嗰四個日期選項只會影響詮釋增益器所產生嘅格式化日期字串（喺 `INTERPRETATION` 模式下）；佢哋唔會改變驗證。建構器嘅值可以唔填——任何未設定（或者傳 `null`）嘅選項都會保持預設值。

### 本地化嘅訊息同標籤

`language(...)` 為 **三種** 供人閱讀嘅文字選擇語言：錯誤訊息、詮釋標籤（每個 `GS1AIInterpretation` 嘅 `getLabel()`），以及 AI 描述（每個 `GS1AIObjectElement` 嘅 `getDescription()`）。

`GaiaConstants.Language` 定義咗 **35 種語言**，涵蓋全世界使用人數最多嘅語言：英文、法文、西班牙文、德文、意大利文、葡萄牙文、荷蘭文、波蘭文、俄文、烏克蘭文、捷克文、瑞典文、中文、日文、韓文、阿拉伯文、印尼文、印地文、土耳其文、孟加拉文、烏爾都文、越南文、尼日利亞皮欽語、埃及阿拉伯文、馬拉地文、泰盧固文、泰米爾文、粵語、吳語、他加祿文、波斯文、豪薩文、旁遮普文、爪哇文同斯瓦希里文。

翻譯狀態（隨附版本）：
- **詮釋標籤** — 所有語言都已翻譯。
- **錯誤訊息** — 所有語言都已翻譯。
- **AI 描述** — 除英文外所有語言都已翻譯。英文並唔係一份獨立嘅目錄：佢直接由 `gs1-application-identifiers.jsonld` 入面該 AI 條目嘅 `description` 欄位讀取，而所有 AI 描述最終都會退回到呢度。

尼日利亞皮欽語（`NIGERIAN_PIDGIN`）係一種以英語為基礎嘅克里奧爾語，佢刻意喺詮釋標籤同錯誤訊息上面沿用英文文字。AI 描述就係呢個例外裏面嘅例外：佢哋譯成咗真正嘅皮欽語措辭，而唔係沿用英文，因為 AI 描述目錄係獨立於標籤／訊息目錄製作嘅。機器翻譯喺投入生產環境之前，應該搵母語使用者審閱。

任何喺某種語言目錄入面搵唔到嘅訊息、標籤或者描述，都會退回英文。由右至左書寫嘅語言（阿拉伯文、烏爾都文、埃及阿拉伯文、波斯文）以字串形式正確儲存；至於點樣以 RTL 呈現，就係顯示層嘅責任。

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

詮釋標籤都係一樣噉本地化（值唔會變——淨係標籤變）：

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI 描述都係一樣噉本地化（只有 `getTitle()`，例如 `"GTIN"`，唔會本地化）：

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### 日期格式化

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## 輸入修改器

**輸入修改器** 係喺 Gaia 解析之前改寫原始輸入字串嘅程式碼。修改器係為咗應付一啲入嚟嗰陣已經走樣嘅輸入——例如掃描器將 GS 分隔符換成咗可列印字元、中介軟體將酬載包咗一層供應商專用嘅前綴、主機系統將所有嘢轉成大寫。與其喺每個呼叫點都要將每條字串先清理一次（噉樣總有一處會出啲細微錯誤），不如將正規化喺 `ParseConfig` 上面登記一次，然後將套用嘅工作交返畀解析器。

修改器喺 `GaiaParser.parse(...)` 最開頭就行——喺剝除關聯 ID 之前、喺辨識 AIM 碼制識別碼之前、喺入到 GS1 流程之前。由呢度往後嘅一切，見到嘅都淨係改寫咗嘅字串。包括兩個[內建修改器](#內建修改器)喺內，**預設乜都唔會設定**——每個 `ParseConfig` 都由你自己揀。

**介面：** `tools.pantheum.gaia.modifier.ModifierInterface`

### 內建修改器

核心 jar 喺 `tools.pantheum.gaia.modifier.custom` 之下附帶兩個修改器。GS1 酬載最常見嘅兩種走樣方式，佢哋啱啱好都處理到——被當成資料嘅印刷 HRI 括號，同埋多餘嘅空格——所以一般情況都唔使自己寫類別：

| 類別 | `getName()` | 佢做啲乜 |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | 剝除每個 AI 周圍嘅 HRI 括號（`(01)…(10)…`），並且還原返嗰啲括號本身所隱含嘅 FNC1 分隔符。 |
| `ModifierRemoveSpaces` | `Remove Space Characters` | 由 AI 元素字串入面移除所有空格（`0x20`）。 |

呢兩個都係冇特殊地位嘅普通 `ModifierInterface` 實作——佢哋嘅登記、排序、報告同失敗方式，同你自己寫嘅完全一樣：

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

兩者都係無狀態同執行緒安全嘅，所以同一個實例可以大家共用；而且為方便以設定檔為本嘅部署，兩者都可以用完整類別名稱嚟指名（請睇 [登記修改器](#登記修改器)）。

#### `ModifierRemoveAIBrackets`

GS1 嘅供人閱讀詮釋會將每個 AI 用括號印出嚟——`(01)09521234543213(10)ABC123`——純粹係一個排印慣例。任何設定咗傳送 HRI 嘅掃描器或者中介軟體，都會將嗰啲括號當成資料一樣送落去，而分詞器根本唔知拎佢哋點算好。

單單剝除括號只做咗一半嘢。喺 HRI 入面，*下一個* AI 嘅開括號 `(` 就係前一個值結束嘅唯一標記，所以喺括號形式之下，可變長度嘅 AI 根本唔需要 FNC1。如果就噉盲摙剝走括號，嗰個邊界就會冇咗：

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

正因如此，呢個修改器會 **喺每一個前一個 AI 屬可變長度嘅邊界上重新插入 FNC1**，準確噉還原返嗰啲括號本來所編碼嘅轉折：

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

長度係由解析器自己嘅 `AiDefinitionRegistry` 查返嚟嘅，所以佢處理到每一個可變長度嘅 AI，而唔係靠一份寫死喺程式碼入面嘅清單。有三種情況係刻意唔郁嘅：本身已經以 FNC1 結尾嘅值（兩種慣例都送過嚟嘅來源唔會攞到第二個分隔符）、唔屬於任何已知 AI 嘅括號代碼（未知嘅 AI 講唔到自己嘅長度），以及字串入面最後嗰個 AI。

呢個改寫係 **冪等** 嘅——攞佢自己嘅輸出再行多次，乜都唔會變——所以喺只有部分輸入帶括號嘅混合流程上面用都好安全。

> **限制。** `(` 同 `)` 本身就係合法嘅 GS1 資料字元，而呢度用嘅樣式淨係 `\((\d{2,4})\)`。如果某個值啱啱好含有一個用括號括住嘅兩至四位數字，佢嘅括號都會一併被剝走。只可以將佢用喺採用 HRI 括號慣例嘅來源上面，唔好用喺真係含有括號值嘅來源。

#### `ModifierRemoveSpaces`

有啲掃描器、中介軟體同標籤列印系統，會喺本來正常嘅元素字串入面加插多餘嘅空格——為咗填滿一個固定寬度嘅欄位、為咗分開易讀嘅組別，又或者為咗將一個長值摺行。分詞器會將每個空格都當成資料，令佢身處嗰個值走樣；而如果係可變長度嘅 AI，佢後面嘅所有嘢都會跟住移位。

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

只有 ASCII `0x20` 會被移除。其他空白字元原封不動——例如定位字元本來就唔喺 GS1 可編碼字元集之內，所以解析器會將佢報成 `GE-S008`，而唔係靜靜雞吞咗佢。

> **限制。** 空格（`0x20`）係 GS1 不變字元集嘅一部分，所以一個批次／批號或者客戶零件編號，合法噉都可以含有空格。修改器分唔到邊個係多餘空格、邊個係真實空格；只可以將佢用喺你確知唔會喺 AI 值入面用空格嘅來源上面。

#### 前綴會被略過，唔會被改寫

修改器行嗰陣，解析器仲未剝除過任何嘢，所以原始輸入可能仲帶住關聯 ID、AIM 碼制識別碼同 ECI 指示符。兩個內建修改器都係用解析器自己嗰套 `CorrelationIdParser` 同 `DataCarrierParser` 邏輯嚟搵出 AI 元素字串嘅起點，由嗰度先至開始改寫，然後將結果同 **原封不動** 嘅前綴重新駁返埋：

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

至於嗰啲值會被填充到 GTIN-14 嘅 EAN/UPC 載體（`isRequiresGtinPadding()`），佢哋會被整個略過——佢哋嘅酬載係冇任何 AI 結構嘅原始數字條碼值，所以喺嗰度括號同空格都唔可能有任何意義。

#### 次序：先空格，後括號

兩個都用嗰陣，要 **先登記 `ModifierRemoveSpaces`**。括號嘅比對係對位置敏感嘅：夾雜空格嘅 `( 01 )` 唔會夾到 `\((\d{2,4})\)`，於是括號會留低，而佢哋所隱含嘅分隔符就永遠唔會返嚟。

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### 寫一個修改器

如果兩個內建修改器都唔啱用，就自己寫一個——個介面淨係得一個方法。

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

如果個改寫要視乎解析設定，就改為覆寫兩個參數嗰個版本：

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

契約如下：

| 規則 | 詳情 |
|---|---|
| 無狀態同執行緒安全 | 每個類別只會快取一個實例，喺所有解析之間共用。 |
| 公開嘅無參數建構子 | 只有喺用類別名稱嚟指名修改器嗰陣先至需要。 |
| 要處理 `null` 同空輸入 | 解析器唔會喺成條鏈行之前先幫你過濾呢啲。 |
| 回傳 `null` 即係「冇改動」 | 前一個值會繼續傳落去。當修改器唔適用嗰陣，原樣回傳 `input`。 |
| 寧願原樣回傳，都好過拋例外 | 拋例外嘅修改器會令成個解析中止——請睇 [失敗處理](#修改器嘅失敗處理)。 |
| `getName()` | 覆寫佢就可以控制 `ModifierInfo` 上面所報嘅名；預設係簡單類別名稱。 |

### 登記修改器

修改器按照你加入嘅次序執行，每一個都收到上一個嘅輸出。可以用實例、用完整類別名稱，又或者用兩者混合嘅清單嚟登記：

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

[內建修改器](#內建修改器)嘅指名方式同你自己嗰啲一模一樣——**永遠都係用完整名稱**。佢哋冇短名，亦都冇別名查找；`ModifierRegistry` 對每一個修改器，無論係隨附定係自訂，都係用完整類別名稱嚟解析。

名稱由 `ModifierRegistry` 解析；佢會用每個類別嘅無參數建構子將佢實例化一次，然後為之後每一份指名同一個類別嘅設定快取住同一個實例。呢個解析係喺 **建立設定嗰陣** 發生嘅，所以搵唔到嘅名、冇實作 `ModifierInterface` 嘅類別，或者無法實例化嘅類別，都會即刻喺嗰度拋出 `IllegalArgumentException`——而唔係喺解析嗰陣靜靜雞出事。至於用反射整唔出嚟嘅修改器（例如佢帶住一個注入嘅相依項），可以預先登記，噉就依然可以用名稱嚟指名：

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### 查看修改器做過啲乜

有設定修改器嗰陣，`ParseResult.getPayload()` 顯示嘅係 **改寫咗** 之後嘅輸入。原本嗰個會保留喺 `ModifierInfo` 上面：

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` 報嘅係每個修改器嘅 `getName()`，佢預設係簡單類別名稱，但係兩個內建修改器都覆寫咗佢——所以呢兩個組成嘅鏈會顯示展示名稱，而唔係類別名稱：

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

冇設定任何修改器嗰陣，`getModifierInfo()` 回傳 `null`。如果修改器行過，但係每一個都原樣回傳咗輸入，噉個資訊物件依然存在，不過 `isModified()` 係 `false`——`getAppliedModifiers()` 入面淨係包含真正改動過輸入嘅修改器。

### 修改器嘅失敗處理

拋例外嘅修改器會令成個解析中止。嗰個例外會被包成一個 `GaiaModifierException`，入面指明係邊個修改器出事，而個結果會帶住一個 `GE-I001` 內部錯誤，訊息入面亦有同一個名；`getPayload()` 顯示嘅係未經修改嘅輸入。解析 **唔會** 帶住一條改寫到一半嘅字串繼續行落去，呢個係刻意嘅——一個靜靜雞失敗咗嘅正規化步驟，會產生一啲睇落合法、但其實係由錯誤輸入解析出嚟嘅結果。

---

## 解析模式

每個模式嘅名，都係取自佢所執行嘅最深嗰個[流程階段](#解析流程)；之前嘅每一個階段照樣都會行。

| 模式 | 行到邊 | 答到咩問題 |
|---|---|---|
| `DATA_CARRIER` | 階段 1（輸入分流） | 呢個係邊種碼制送嚟嘅？ |
| `SYNTAX` | 階段 2（語法） | AI 代碼同長度嘅格式啱唔啱？ |
| `CONTENT` | 階段 3（內容） | 啲值係咪合法嘅 GS1 資料？ |
| `INTERPRETATION` | 階段 4（詮釋） | 啲值即係咩意思？ |

### DATA_CARRIER 模式

喺階段 1 之後就停。佢會驗證 AIM 碼制識別碼同辨識碼制，但係唔會入到 AI 解析流程。適合喺唔想承受完整驗證成本嘅情況下辨識碼制同分流。

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

**幾時用：** 當你嘅應用程式喺決定點樣處理酬載之前，需要知道條碼係咩類型嗰陣——例如將 1D 同 2D 碼制送去唔同嘅處理器。做呢種分流嗰陣，用有型別嘅 [`DataCarrierType`](#datacarrierentry-同-datacarriertype)（`getDataCarrier().getDataCarrierType()`），唔好喺 `getName()` 上面夾字串。

---

### SYNTAX 模式

喺階段 2 之後就停。適合喺唔想承受內容驗證成本嘅情況下做結構篩查。

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

**幾時用：** 當你想喺投入完整驗證之前，先確認 AI 代碼同資料長度啱唔啱嗰陣；又或者做大批量掃描，而內容錯誤好少見嗰陣。

---

### CONTENT 模式

喺階段 3 之後就停。

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

> 大部分 AI 都唔可以獨立存在：AI `10`（BATCH/LOT）、`17`（USE BY or EXPIRY）同
> `21`（SERIAL）——每一個都 *需要* 喺同一條元素字串入面有一個好似 AI `01` 噉嘅識別鍵；
> 所以將上面例子入面嘅 GTIN 攞走，喺仲未去到內容驗證之前，就已經會喺階段 2
> 以 `GE-S005` 失敗。如果啲片段係刻意冇埋佢哋嘅伴隨 AI 嘅，就喺 `ParseConfig` 上面
> 設定 `skipRequiresCheck(true)` 嚟解析佢哋。

**幾時用：** 當你喺將一個掃描到嘅值用落業務流程之前，需要知道佢係咪完全符合 GS1 規範，但係又唔想承受詮釋增益嘅成本嗰陣。

---

### INTERPRETATION 模式（預設）

行足成條流程直到階段 4。呼叫冇模式參數嘅 `parse(String)` 嗰陣，用嘅就係呢個預設。佢淨係會為順利通過內容驗證嘅元素做增益。

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

**範例輸出：**
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

**貨幣金額嘅例子（AI 3932 — 帶 ISO 貨幣代碼嘅價格）：**
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

**幾時用：** 起緊展示層、標籤檢查工具，又或者任何需要將 AI 值以人性化方式拆解嘅 UI 嗰陣。

---

## 關聯 ID

有啲工作流程會喺原始 GS1 輸入前面加一個專用嘅 8 位數關聯識別碼，噉就可以將掃描事件同某個工作階段或者交易對返。格式係噉：

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

`~`（波浪號）係分隔符。佢 **唔係** GS1 內容嘅一部分——喺任何 GS1 解析開始之前，佢就已經被剝走。

### 偵測規則

當輸入係以啱啱好 8 個 ASCII 十進位數字（`0`–`9`）開頭，並且緊接住一個 `~` 嗰陣，就會偵測到呢個前綴。如果第 9 個字元唔係 `~`，又或者頭 8 個字元入面有任何一個唔係數字，噉個輸入就會被當成冇關聯前綴嘅純 GS1 內容。

### 攞關聯 ID

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

### 同 AIM 碼制識別碼一齊用

關聯前綴可以出現喺 AIM 碼制識別碼之前。解析器會透明噉處理呢種情況：

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**實作類別：** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** 直接將一個或者多個 AI 值編碼入 HTTP(S) URL 嘅結構入面，令實體產品有一個可以喺網上解析嘅識別碼。GAIA 為 **未壓縮** 嘅 URI 實作咗 *GS1 Digital Link Standard: URI Syntax*（第 1.7.0 版）。

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` 會自動辨識 Digital Link URI——任何以 `http://` 或者 `https://` 開頭嘅輸入，都會被送去 `GS1DLParser`，佢會行同元素字串流程一模一樣嘅內容同詮釋階段。

### URI 結構同 AI 嘅角色

Digital Link URI 入面每個 AI 都擔當三種角色之一，透過每個 `GS1AIObjectElement` 嘅 `getDigitalLinkAIType()`（`GS1Constants.DigitalLinkAIType`）攞到：

| 角色 | 位置 | 例子 |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | 路徑上第一對 `/ai/value`（§4.3） | `/01/09506000134352` |
| `KEY_QUALIFIER` | 之後嘅路徑對，按主要鍵所訂嘅次序（§4.9） | `/10/ABC`、`/21/SER` |
| `DATA_ATTRIBUTE` | 鍵名全數字嘅查詢參數（§4.10） | `?17=271231` |

所強制執行嘅結構規則（`DLPathRules`）：
- 路徑入面啱啱好要有 **一個** 主要識別鍵；額外嘅鍵必須編碼成查詢資料屬性。
- 鍵限定詞必須係主要鍵所允許嘅，而且要按規定次序出現。可選嘅限定詞可以略去，但係凡係 *有出現* 嘅，都仍然要遵守固定次序——請睇 [限定詞次序](#限定詞次序)。
- 主要鍵之前可以有任意嘅自訂路徑區段（例如 `/products/au/01/...`）；用 `getDigitalLinkInfo().getCustomPathStem()` 攞返佢哋。
- 非數字嘅查詢鍵（`linkType`、`context`，同 `23P` 之類嘅擴充參數）會被忽略；全數字嘅鍵必須係標記咗 `validAsDataAttribute` 嘅合法 AI。
- 百分號編碼嘅值字元會被解碼；AI `(03)` 同 `(8014)` 唔准使用。

主要鍵同佢哋所允許嘅限定詞序列係 **由資料驅動** 嘅，源自 AI 定義入面嘅 `gs1DigitalLinkPrimaryKey` 旗標同 `gs1DigitalLinkQualifiers` 屬性，而唔係寫死喺程式碼度。

任何結構上嘅違規，又或者非 URL 嘅輸入，都會產生一個 Digital Link 結構錯誤（`GE-L001`–`GE-L014`，每種情況一個代碼）。即使有結構錯誤，經拆解嘅 URL 中繼資料（`scheme`、`domain`、`path`、`customPathStem`、`query`，同埋 `java.net.URL`）依然可以透過 `getDigitalLinkInfo()` 攞到。

### 限定詞次序

對每個主要鍵，`gs1DigitalLinkQualifiers` 會列出一個或者多個 **有次序** 嘅限定詞序列。喺一個序列入面，用方括號括住嘅 AI 係 **可選** 嘅，冇括號嘅 AI 就係 **必需** 嘅——同 §4.9 ABNF 嘅 `[cpv-comp]` 記法一致。同一個主要鍵嘅多個序列，係互相排斥嘅替代方案。

例如 GTIN（`01`）就定義咗兩個序列：

| 路徑 | 序列 | 意義 |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV、LOT、SER——每個都可選，但係次序固定係噉 |
| upui-path | `235` | TPX（必需）；GTIN ＋ TPX ＝ UPUI |

所以 `/01/09506000134352/10/LOT-ABC/21/SER` 係合法嘅（LOT 喺 SER 之前，CPV 略去咗），`/01/.../21/SER/10/LOT-ABC` 就會 **被拒絕**（次序錯咗），而 `/01/09506000134352/235/2ABC456` 就係 upui-path。次序檢查係一種保序嘅子序列比對，所以可選嘅 AI 可以跳過，但係永遠都唔可以調亂次序。

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

**實作類別：** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## 處理結果

### ParseResult

`GaiaParser.parse()` 回傳嘅最上層結果。

| 方法 | 回傳 | 描述 |
|---|---|---|
| `isValid()` | `boolean` | 如果任何等級都冇錯誤就係 `true`。警告唔會影響有效性。當 `getAiObject()` 係 `null` 嗰陣永遠都係 `true`。 |
| `getPayload()` | `String` | 剝除關聯前綴之後——以及經任何[輸入修改器](#輸入修改器)改寫之後——嘅輸入字串。 |
| `getPayloadContent()` | `String` | 剝除咗 AIM 碼制識別碼同 ECI 前綴嘅酬載。 |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`、`GS1_DIGITAL_LINK`、`DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL`（被判定為非 GS1 而拒絕嘅資料載體，例如 Code 39 嘅 `]A0` 載體），或者 `UNABLE_TO_DETERMINE_CONTENT`（當 `aiObject` 係 `null` 嗰陣，例如 `DATA_CARRIER` 模式）。 |
| `getRequestedParseMode()` | `ParseMode` | 所設定嘅流程深度（`ParseConfig.getRequestedParseMode()`）。 |
| `getAchievedParseMode()` | `ParseMode` | 呢次解析實際去到嘅最深階段——見下文。 |
| `isParseComplete()` | `boolean` | 如果解析去到所要求嘅深度就係 `true`（`achieved == requested`）。同 `isValid()` 無關。 |
| `getAiObject()` | `GS1AIObject` | 所有已解析嘅 AI。喺 `DATA_CARRIER` 模式係 `null`。 |
| `getErrors()` | `List<GaiaError>` | 所有非 WARNING 嘅錯誤（物件層 ＋ 所有元素層）。 |
| `getWarnings()` | `List<GaiaError>` | 所有 WARNING 建議（物件層 ＋ 所有元素層）。 |
| `hasWarnings()` | `boolean` | 如果有發出任何 WARNING 建議就係 `true`。 |
| `getIssues()` | `List<GaiaError>` | 錯誤同警告合埋一齊。 |
| `hasDataCarrier()` | `boolean` | 如果辨識到 AIM 碼制識別碼就係 `true`。 |
| `getDataCarrier()` | `DataCarrierEntry` | 碼制中繼資料；如果冇辨識到載體就係 `null`。 |
| `hasEci()` | `boolean` | 如果由酬載剝除咗一個 ECI 指示符就係 `true`。 |
| `getEci()` | `EciEntry` | ECI 編碼中繼資料，或者 `null`。 |
| `hasCorrelationId()` | `boolean` | 如果原始輸入入面有 `DDDDDDDD~` 關聯前綴就係 `true`。 |
| `getCorrelationInfo()` | `CorrelationInfo` | 抽取出嚟嘅關聯 ID；如果冇就係 `null`。 |
| `isInputModified()` | `boolean` | 如果有[輸入修改器](#輸入修改器)改動過輸入就係 `true`。 |
| `getModifierInfo()` | `ModifierInfo` | 修改器鏈做過啲乜——`getOriginalInput()`、`getModifiedInput()`、`getAppliedModifiers()`。如果冇設定修改器就係 `null`。 |
| `getTiming()` | `ProcessingTiming` | 呢次解析嘅實際耗時——`getStartTime()`（`Instant`）、`getProcessingTime()`（`Duration`）、`getProcessingTimeMillis()`（`long`）、`getCompletionTime()`。如果唔係由 `GaiaParser` 產生就係 `null`。 |
| `getVersion()` | `String` | 產生呢個結果嘅程式庫版本。 |

#### 所要求嘅解析模式對實際達到嘅解析模式

成條流程係行 **SYNTAX → CONTENT → INTERPRETATION** 呢條階梯，一遇到錯誤就提早停低，所以實際 *達到* 嘅模式可以淺過 *所要求* 嘅模式。`getAchievedParseMode()` 會報返佢行到幾遠：

| 所要求 | 發生咩事 | 實際達到 | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | 一個 **語法／結構** 錯誤令解析喺分詞之後停低 | `SYNTAX` | `false` |
| `INTERPRETATION` | 一個 **內容** 錯誤（格式／校驗碼有問題）阻止咗增益 | `CONTENT` | `false` |
| `CONTENT` | 內容階段永遠都會行完（錯誤只會標註，唔會致命） | `CONTENT` | `true` |
| 任何（輸入乾淨） | 流程去到所要求嘅深度 | ＝ 所要求 | `true` |
| `DATA_CARRIER` | 載體已驗證；冇解析 AI 內容 | `DATA_CARRIER` | `true` |
| 任何 | 資料載體喺 AI 解析之前就被拒絕（例如非 GS1 嘅 `]A0` 載體） | `SYNTAX` | `false` |

`isParseComplete()` 同 `isValid()` 無關：一個校驗碼錯咗嘅 GTIN，用 `CONTENT` 解析係 **完成咗** 嘅（佢行完咗內容階段），但係 **無效** 嘅（校驗碼失敗）。想問「條流程有冇行到我要求嘅深度？」就用 `isParseComplete()`，想問「啲資料格式啱唔啱？」就用 `isValid()`。

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

已解析嘅 AI 元素集合。

| 方法 | 描述 |
|---|---|
| `getAis()` | 按輸入次序排列嘅所有 `GS1AIObjectElement` 實例。 |
| `get(String aiCode)` | 符合所給 AI 代碼嘅第一個元素，或者 `null`。 |
| `contains(String aiCode)` | 如果存在該代碼嘅 AI 就係 `true`。 |
| `size()` | 已解析嘅 AI 數目。 |
| `isValid()` | 如果冇物件層錯誤而且冇任何元素有錯誤就係 `true`。 |
| `toHriString()` | HRI 字串，例如 `(01)09506000134352 (17)261231`。 |
| `toElementString()` | 原始元素字串——冇括號，每個可變長度元素之後有 FNC1——例如 `010950600013435210LOT-ABC<GS>17271231`。如果 `isValid()` 係 `false` 就回傳 `null`。 |
| `getContentType()` | 當 `hasDigitalLink()` 為真嗰陣係 `GS1_DIGITAL_LINK`，否則係 `GS1_APPLICATION_IDENTIFIERS`。 |
| `hasDigitalLink()` | 如果輸入係一個帶主要識別鍵嘅 GS1 Digital Link URI 就係 `true`。一個格式正確但冇主要鍵嘅 URL，依然攞到 `getDigitalLinkInfo()`，但係呢度會回傳 `false`。 |
| `getCanonicalDigitalLink()` | 喺 `https://id.gs1.org` 上面嘅標準 GS1 Digital Link URI（§4.12）——主要鍵同限定詞作路徑區段，資料屬性作查詢參數並按 AI 鍵排序——如果冇主要鍵就係 `null`。 |
| `getDigitalLinkInfo()` | URI 拆解嘅中繼資料（`getUri()`、`getUrl()`、`scheme`、`domain`、`path`、`getCustomPathStem()`、`query`）；如果唔係 Digital Link 就係 `null`。 |
| `getAllErrors()` | 物件層 ＋ 所有元素錯誤（非 WARNING）。 |
| `getAllWarnings()` | 物件層 ＋ 所有元素警告。 |
| `getAllIssues()` | 全部合埋一齊。 |

---

### GS1AIObjectElement

單一個已解析嘅 AI 實例。

| 方法 | 描述 |
|---|---|
| `getAi()` | AI 代碼，例如 `"01"`、`"3102"`。 |
| `getTitle()` | GS1 資料標題，例如 `"GTIN"`、`"BATCH/LOT"`。 |
| `getDescription()` | 該 AI 完整嘅 GS1 描述，**已按解析語言本地化**（例如英文係 `"Global Trade Item Number (GTIN)"`）。如果未翻譯就退回 AI 定義入面嘅英文文字。 |
| `getFormatString()` | 涵蓋該 AI *同埋* 佢資料嘅格式描述符，例如 AI `01` 係 `"N2+N14"`、AI `10` 係 `"N2+X..20"`、AI `3932` 係 `"N4+N3+N..15"`。 |
| `getValue()` | 由元素字串抽取出嚟嘅原始資料值。 |
| `isFixedLength()` | 如果該 AI 嘅資料長度固定就係 `true`。 |
| `getPosition()` | 喺原始輸入入面由零開始計嘅字元偏移量。 |
| `getGS1ComponentValues()` | 逐個組件嘅值切片（用於多組件 AI）。 |
| `getErrors()` | 元素層嘅非 WARNING 錯誤。 |
| `getWarnings()` | 元素層嘅 WARNING 建議。 |
| `getIssues()` | 元素層嘅錯誤同警告合埋一齊。 |
| `hasErrors()` | 如果附有任何非 WARNING 錯誤就係 `true`。 |
| `hasWarnings()` | 如果附有任何 WARNING 建議就係 `true`。 |
| `getInterpretations()` | `GS1AIInterpretation` 條目（喺 INTERPRETATION 模式先至會填充）。 |
| `getInterpretation(String type)` | 符合所給 `GS1Constants_Enricher` 型別鍵嘅第一個詮釋，或者 `null`。 |
| `getDigitalLinkAIType()` | 該元素嘅 Digital Link 角色（`PRIMARY_IDENTIFICATION_KEY`、`KEY_QUALIFIER`、`DATA_ATTRIBUTE`）；元素字串輸入就係 `null`。 |
| `hasDigitalLinkAIType()` | 如果已指派 Digital Link 角色就係 `true`。 |

---

### GaiaError

一個不可變嘅驗證錯誤或者建議。

| 方法 | 描述 |
|---|---|
| `getId()` | 目錄識別碼，例如 `"GE-C003"`。 |
| `getLevel()` | `SYNTAX_ERROR`、`INTEGRITY_ERROR`、`FORMAT_ERROR`、`DATA_ERROR`，或者 `WARNING`。 |
| `getStage()` | `DATA_CARRIER`、`DIGITAL_LINK`、`SYNTAX`、`CONTENT`，或者 `INTERNAL`。 |
| `getCode()` | 機器可讀嘅短代碼。 |
| `getAi()` | 引致該錯誤嘅 AI 代碼；物件層錯誤就係 `null`。 |
| `getMessage()` | 供人閱讀、已填入值嘅訊息。 |
| `getPosition()` | 喺原始輸入入面由零開始計嘅字元偏移量。 |

---

### GS1AIInterpretation

單一個有標籤嘅詮釋片段，喺 `INTERPRETATION` 模式下附加喺 `GS1AIObjectElement` 上面。

| 方法 | 描述 |
|---|---|
| `getType()` | 機器可讀嘅型別鍵，例如 `"DATE_VALUE"`、`"GS1_COMPANY_PREFIX"`。喺所有語言都保持不變。 |
| `getLabel()` | 供人閱讀嘅標籤，**已按解析語言本地化**（例如英文係 `"Date"`／`"GS1 company prefix"`）。 |
| `getValue()` | 抽取／增益後嘅值，例如 `"31/12/2026"`、`"9506000"`。唔會本地化。 |

---

### DataCarrierEntry 同 DataCarrierType

當輸入帶有 AIM 碼制識別碼嗰陣，`ParseResult.getDataCarrier()` 會回傳一個 `DataCarrierEntry`，描述承載咗啲資料嘅嗰個符號。呢個條目係所夾到嘅 AIM 碼制識別碼嘅具體登記紀錄；`DataCarrierType` 就係佢所屬嘅編譯期列舉。

#### DataCarrierEntry

一個已辨識 AIM 碼制識別碼嘅中繼資料（`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`）。

| 方法 | 描述 |
|---|---|
| `getAimCodeId()` | 夾到嘅 AIM 碼制識別碼，例如 `"]C1"`。 |
| `getName()` | 該具體符號嘅人類可讀名稱，例如 `"GS1-128 / ISBT 128"`、`"EAN-8"`。 |
| `getDescription()` | 該載體較詳細嘅描述。 |
| `getType()` | 該載體嘅結構類型，以字串表示（同 `getDataCarrierType().getCategory()` 一致）。 |
| `getStandard()` | 碼制標準，如有紀錄。 |
| `getDataCarrierType()` | 呢個條目所對應、有型別嘅 `DataCarrierType`——做程式分流嗰陣應該用呢個。 |
| `isGs1Capable()` | 如果該載體可以承載 GS1 資料（AI 元素字串同／或 Digital Link）就係 `true`。 |
| `isGs1AICapable()` | 如果該載體可以承載 GS1 AI 元素字串就係 `true`。 |
| `isGs1DigitalLinkCapable()` | 如果該載體可以承載 GS1 Digital Link URI 就係 `true`。 |
| `isEciCapable()` | 如果該載體支援 ECI 指示符就係 `true`。 |
| `isRequiresGtinPadding()` | 對於數字值會喺 AI 解析之前被填充到 GTIN-14 嘅 EAN/UPC/ITF 載體，就係 `true`。 |

#### DataCarrierType

一個編譯期嘅資料載體類型列舉，以 ISO/IEC 15424 所指派嘅 AIM 碼制識別碼為鍵（`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`）。`]` 之後嗰個字元（即 *碼制字元*）決定族系；大部分族系都對應一個涵蓋所有修飾詞嘅常數（`ITF` 涵蓋 `]I0`–`]I2`；`EAN_UPC` 涵蓋 EAN-13、UPC-A、UPC-E 同 EAN-8）。凡係 GS1 為 AI 資料保留咗修飾詞嘅，該變體都自成一個常數——`GS1_128`（`]C1`）、`GS1_DATA_MATRIX`（`]d2`）、`GS1_QR_CODE`（`]Q3`）、`GS1_DOT_CODE`（`]J1`）——同佢哋嘅普通對應物有分別。當冇 AIM 碼制識別碼，又或者佢指嘅係一個未知載體嗰陣，型別就係 `UNKNOWN`。

| 方法 | 描述 |
|---|---|
| `getCategory()` | 概括嘅 `GaiaConstants.DataCarrierTypeCategory`：`LINEAR`、`STACKED_LINEAR`、`TWO_D`、`POSTAL`、`OCR`，或者 `OTHER`。 |
| `getCodeChar()` | 標明族系嘅 AIM 碼制字元，例如 QR Code 係 `"Q"`；`UNKNOWN` 就係 `null`。 |
| `getDisplayName()` | *型別* 嘅人類可讀名稱（可能闊過 `DataCarrierEntry.getName()`——例如 `"EAN-13 / UPC-A / UPC-E / EAN-8"` 對 `"EAN-8"`）。 |
| `isGs1DataCarrier()` | 對於永遠代表 GS1 AI 資料嘅常數就係 `true`：四個 GS1 保留變體（`GS1_128`、`GS1_DATA_MATRIX`、`GS1_QR_CODE`、`GS1_DOT_CODE`），再加 `GS1_DATABAR`，後者本質上就係 GS1，因為每個 `]e` 修飾詞都係 GS1 DataBar。呢個窄過 `DataCarrierEntry.isGs1AICapable()`——一個普通嘅 `QR_CODE` 一樣可以承載 GS1 AI 資料。 |
| `static forAimCodeId(String)` | 直接由 AIM 碼制識別碼解析出型別（`"]Q3"` → `GS1_QR_CODE`；`"]Q9"` → `QR_CODE`）；如果識別碼缺失、格式錯誤或者未能辨識，就回傳 `UNKNOWN`。 |

按型別而唔係按名稱嚟分流——例如將線性（Code-128）同 2D（QR／Data Matrix）符號分開：

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` 淨係涵蓋矩陣同點陣符號；堆疊式線性載體（`PDF417`、
`CODE_16K`、`CODABLOCK`、`CODE_49`）屬於 `STACKED_LINEAR`，雖然佢哋通常都被人叫做
「2D」條碼。想將兩者當成同一組嚟處理——譬如要決定係咪需要影像式掃描器而唔係鐳射掃描器
——就檢查係咪屬於呢兩個類別之一。

> 解析型別需要掃描結果入面有 AIM 碼制識別碼；冇咗佢，`getDataCarrier()` 就係 `null`，型別亦都係 `UNKNOWN`。請將掃描器設定成會傳送 AIM 碼制識別碼前綴。

---

## 錯誤參考

| 代碼 | 等級 | 階段 | 意義 |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | 未知嘅 AI 前綴——無法判定資料長度 |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | 輸入太短，讀唔到一個完整嘅 AI 代碼 |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | 值被截斷——字元數少過該 AI 所需 |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | 元素字串入面有重複嘅應用識別碼 |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | 缺少必要嘅 AI 相依項 |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | 互斥嘅 AI 配對——兩個唔可以同時出現嘅 AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | 非預期嘅分詞失敗 |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | 元素字串入面有 GS1 可編碼字元集以外嘅字元 |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | 可變長度 AI 之後缺少必要嘅 FNC1 分隔符 |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | 超出所有組件上限之後仲有剩餘資料 |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | 喺字串中間，固定長度 AI 之後出現 FNC1 分隔符 |
| `GE-W002` | WARNING | SYNTAX | 元素字串結尾有多餘嘅 FNC1（僅屬建議） |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI 嘅結構違規——每種情況一個代碼（URI 格式錯誤、scheme、host、限定詞次序、禁用 AI、冇主要鍵（`GE-L013`）、多個主要鍵（`GE-L014`）……） |
| `GE-C001` | FORMAT_ERROR | CONTENT | 值唔符合該 AI 嘅正規表達式樣式 |
| `GE-C003` | DATA_ERROR | CONTENT | 校驗碼驗證失敗 |
| `GE-C004` | DATA_ERROR | CONTENT | 校驗字元對驗證失敗 |
| `GE-C005` | FORMAT_ERROR | CONTENT | 組件值含有所允許字元集以外嘅字元 |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | 組件格式失敗——每個驗證條件一個代碼（請睇 `componentformat/`） |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | 自訂語意驗證失敗——每個驗證條件一個代碼（請睇 `content/validator/`）。**例外：** 下列 14 項 GS1 公司前綴檢查嘅等級係 `WARNING`，而 `GE-C168`（未能辨識嘅 ISO 3166-1 數字國家代碼）嘅等級係 `FORMAT_ERROR`。 |
| GS1 公司前綴檢查 | WARNING | CONTENT | 喺 GS1 鍵類 AI 上面，個鍵並唔係以一個已辨識嘅 GS1 公司前綴開頭——`GE-C122`（CPID）、`GE-C129`（GCN）、`GE-C131`（GDTI）、`GE-C132`（GIAI）、`GE-C133`（GINC）、`GE-C135`（GLN）、`GE-C137`（GMN）、`GE-C140`（GRAI）、`GE-C142`（GSIN）、`GE-C144`（GSRN）、`GE-C146`（GTIN）、`GE-C148`（HIDRI）、`GE-C153`（ITIP）、`GE-C165`（SSCC）。僅屬建議——唔會影響有效性。 |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040（IMEI）／8041（IMEI2）嘅 IMEI 校驗碼（Luhn）失敗 |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042（ESIM）嘅 EID 校驗碼（Luhn）失敗 |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | 未能辨識嘅 AIM 碼制識別碼 |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | 已辨識到載體，但係佢既唔支援 GS1 AI 元素字串，亦都唔支援 Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | 非預期嘅內部錯誤 |

> **訊息呈現上嘅已知缺陷。** 目錄嘅範本用 MessageFormat 風格嘅雙重單引號
> （`''{value}''`）嚟引住填入嘅值，但係 `ErrorRegistry` 係用普通嘅 `String.replace`
> 做填入，所以嗰重雙重引號會一路帶到入 `getMessage()`——本指南所引嘅訊息文字寫
> `value '09506000134351'` 嗰度，你而家實際會見到 `value ''09506000134351''`。
> 全部 35 種語言目錄入面每一條會引用值嘅訊息都受影響。唔好去解析錯誤訊息；
> 請喺 `getId()`／`getCode()` 上面做比對。

---

## 執行緒安全

`GaiaParser` 一經建構就係執行緒安全嘅。同一個實例可以喺多條執行緒之間共用同並行使用。建議嘅做法係喺應用程式啟動嗰陣建構一個實例，然後重複使用：

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` 係不可變嘅，同樣可以安全共用。程式庫唯一冇辦法幫你保證嘅執行緒安全責任，就係喺[輸入修改器](#輸入修改器)上面：每個修改器只會快取一個實例，喺所有並行解析之間共用，所以啲實作必須係無狀態嘅。

---

## 附錄 A — AI 字串常數

`GS1Constants_AICodes`（喺 `tools.pantheum.gaia.gs1.constants` 套件入面）為 GAIA 所辨識嘅每一個應用識別碼都宣告咗一個 `String` 常數。請用呢啲常數，唔好將原始 AI 代碼字串寫死喺程式碼度：

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

每個常數存住嘅係該 AI 代碼嘅字串形式（例如 `AI_01_GTIN = "01"`）。

### 識別同序列化

| AI | 常數 | 描述 |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | 系列貨運包裝箱代碼 (SSCC). |
| `01` | `AI_01_GTIN` | 全球貿易項目編號 (GTIN). |
| `02` | `AI_02_CONTENT` | 內含貿易項目嘅全球貿易項目編號 (GTIN). |
| `03` | `AI_03_MTO_GTIN` | 按單訂造 (MtO) 貿易項目識別碼 (GTIN). |
| `10` | `AI_10_BATCH_LOT` | 批次或批號. |
| `20` | `AI_20_VARIANT` | 內部產品變體. |
| `21` | `AI_21_SERIAL` | 序號. |
| `22` | `AI_22_CPV` | 消費者產品變體. |
| `235` | `AI_235_TPX` | 第三方控制嘅全球貿易項目編號序列化擴展 (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | 製造商指定嘅附加產品識別碼. |
| `241` | `AI_241_CUST_PART_NO` | 客戶零件編號. |
| `242` | `AI_242_MTO_VARIANT` | 按單訂造變化編號. |
| `243` | `AI_243_PCN` | 包裝組件編號. |
| `250` | `AI_250_SECONDARY_SERIAL` | 次要序號. |
| `251` | `AI_251_REF_TO_SOURCE` | 來源實體參考. |
| `253` | `AI_253_GDTI` | 全球文件類型識別碼 (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | 全球位置碼 (GLN) 延伸部分. |
| `255` | `AI_255_GCN` | 全球優惠券編號 (GCN). |
| `30` | `AI_30_VAR_COUNT` | 可變物品數量（可變計量貿易項目）. |
| `37` | `AI_37_COUNT` | 物流單元內所含貿易項目或貿易項目件數嘅數量. |

### 日期同時間

| AI | 常數 | 描述 |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | 生產日期 (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | 到期日 (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | 包裝日期 (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | 最佳食用日期 (YYMMDD). |
| `16` | `AI_16_SELL_BY` | 售賣期限 (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | 有效期至 (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | 最早送貨日期時間 (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | 最遲送貨日期時間 (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | 發行日期 (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | 有效期至日期同時間 (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | 首次冷凍日期 (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | 採收日期 (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | 測試期限 (YYMMDD[hhmm]). |

### 數量同量度 — 可變計量（公制）

四位數嘅 AI 族系 `310n`–`369n` 編碼可變計量嘅數量。第三位數決定量度類型；**第四位數**（`n`，0–5）就係隱含小數位嘅數目——例如 `AI_3102_NET_WEIGHT_KG` 即係以公斤計、帶 2 個小數位嘅淨重。

| 族系 | 常數樣式（`n` ＝ 小數位數字） | 描述 |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | 淨重，公斤（可變計量貿易項目）. |
| `311n` | `AI_311n_LENGTH_M` | 長度或第一維度，米（可變計量貿易項目）. |
| `312n` | `AI_312n_WIDTH_M` | 闊度、直徑或第二維度，米（可變計量貿易項目）. |
| `313n` | `AI_313n_HEIGHT_M` | 深度、厚度、高度或第三維度，米（可變計量貿易項目）. |
| `314n` | `AI_314n_AREA_M` | 面積，平方米（可變計量貿易項目）. |
| `315n` | `AI_315n_NET_VOLUME_L` | 淨體積，公升（可變計量貿易項目）. |
| `316n` | `AI_316n_NET_VOLUME_M` | 淨體積，立方米（可變計量貿易項目）. |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | 物流重量，公斤. |
| `331n` | `AI_331n_LENGTH_M_LOG` | 長度或第一維度，米. |
| `332n` | `AI_332n_WIDTH_M_LOG` | 闊度、直徑或第二維度，米. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | 深度、厚度、高度或第三維度，米. |
| `334n` | `AI_334n_AREA_M_LOG` | 面積，平方米. |
| `335n` | `AI_335n_VOLUME_L_LOG` | 物流體積，公升. |
| `336n` | `AI_336n_VOLUME_M_LOG` | 物流體積，立方米. |
| `337n` | `AI_337n_KG_PER_M` | 每平方米公斤數. |

### 數量同量度 — 可變計量（英制／美制）

| 族系 | 常數樣式（`n` ＝ 小數位數字） | 描述 |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | 淨重，磅（可變計量貿易項目）. |
| `321n` | `AI_321n_LENGTH_IN` | 長度或第一維度，吋（可變計量貿易項目）. |
| `322n` | `AI_322n_LENGTH_FT` | 長度或第一維度，呎（可變計量貿易項目）. |
| `323n` | `AI_323n_LENGTH_YD` | 長度或第一維度，碼（可變計量貿易項目）. |
| `324n` | `AI_324n_WIDTH_IN` | 闊度、直徑或第二維度，吋（可變計量貿易項目）. |
| `325n` | `AI_325n_WIDTH_FT` | 闊度、直徑或第二維度，呎（可變計量貿易項目）. |
| `326n` | `AI_326n_WIDTH_YD` | 闊度、直徑或第二維度，碼（可變計量貿易項目）. |
| `327n` | `AI_327n_HEIGHT_IN` | 深度、厚度、高度或第三維度，吋（可變計量貿易項目）. |
| `328n` | `AI_328n_HEIGHT_FT` | 深度、厚度、高度或第三維度，呎（可變計量貿易項目）. |
| `329n` | `AI_329n_HEIGHT_YD` | 深度、厚度、高度或第三維度，碼（可變計量貿易項目）. |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | 物流重量，磅. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | 長度或第一維度，吋. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | 長度或第一維度，呎. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | 長度或第一維度，碼. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | 闊度、直徑或第二維度，吋. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | 闊度、直徑或第二維度，呎. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | 闊度、直徑或第二維度，碼. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | 深度、厚度、高度或第三維度，吋. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | 深度、厚度、高度或第三維度，呎. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | 深度、厚度、高度或第三維度，碼. |
| `350n` | `AI_350n_AREA_IN` | 面積，平方吋（可變計量貿易項目）. |
| `351n` | `AI_351n_AREA_FT` | 面積，平方呎（可變計量貿易項目）. |
| `352n` | `AI_352n_AREA_YD` | 面積，平方碼（可變計量貿易項目）. |
| `353n` | `AI_353n_AREA_IN_LOG` | 面積，平方吋. |
| `354n` | `AI_354n_AREA_FT_LOG` | 面積，平方呎. |
| `355n` | `AI_355n_AREA_YD_LOG` | 面積，平方碼. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | 淨重，金衡安士（可變計量貿易項目）. |
| `357n` | `AI_357n_NET_VOLUME_OZ` | 淨重（或體積），安士（可變計量貿易項目）. |
| `360n` | `AI_360n_NET_VOLUME_QT` | 淨體積，夸脫（可變計量貿易項目）. |
| `361n` | `AI_361n_NET_VOLUME_GAL` | 淨體積，加侖（美制）（可變計量貿易項目）. |
| `362n` | `AI_362n_VOLUME_QT_LOG` | 物流體積，夸脫. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | 物流體積，加侖（美制）. |
| `364n` | `AI_364n_NET_VOLUME_IN` | 淨體積，立方吋（可變計量貿易項目）. |
| `365n` | `AI_365n_NET_VOLUME_FT` | 淨體積，立方呎（可變計量貿易項目）. |
| `366n` | `AI_366n_NET_VOLUME_YD` | 淨體積，立方碼（可變計量貿易項目）. |
| `367n` | `AI_367n_VOLUME_IN_LOG` | 物流體積，立方吋. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | 物流體積，立方呎. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | 物流體積，立方碼. |

### 價格同貨幣金額

第四位數（`n`）編碼隱含小數位嘅數目。佢嘅容許範圍每個族系都唔同——請睇 `n` 欄。

| 族系 | 常數樣式（`n` ＝ 小數位數字） | `n` | 描述 |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | 適用應付金額或優惠券面值，當地貨幣. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | 適用應付金額（附ISO貨幣代碼）. |
| `392n` | `AI_392n_PRICE` | 0–9 | 適用應付金額，單一貨幣地區（可變計量貿易項目）. |
| `393n` | `AI_393n_PRICE` | 0–9 | 適用應付金額（附ISO貨幣代碼）（可變計量貿易項目）. |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | 優惠券折扣百分比. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | 每計量單位應付金額，單一貨幣地區（可變計量貿易項目）. |

### 位置同運送

| AI | 常數 | 描述 |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | 客戶採購訂單編號. |
| `401` | `AI_401_GINC` | 全球託運識別編號 (GINC). |
| `402` | `AI_402_GSIN` | 全球裝運識別編號 (GSIN). |
| `403` | `AI_403_ROUTE` | 路由代碼. |
| `410` | `AI_410_SHIP_TO_LOC` | 收貨方 / 送貨方全球位置碼 (GLN). |
| `411` | `AI_411_BILL_TO` | 收費方 / 發票方全球位置碼 (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | 採購來源全球位置碼 (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | 代運 / 代送 - 轉交全球位置碼 (GLN). |
| `414` | `AI_414_LOC_NO` | 實體地點識別 - 全球位置碼 (GLN). |
| `415` | `AI_415_PAY_TO` | 開票方嘅全球位置碼 (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | 生產或服務地點嘅全球位置碼 (GLN). |
| `417` | `AI_417_PARTY` | 有關方全球位置碼 (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | 收貨方 / 送貨方郵政編碼（單一郵政機構範圍內）. |
| `421` | `AI_421_SHIP_TO_POST` | 收貨方 / 送貨方郵政編碼（附ISO國家代碼）. |
| `422` | `AI_422_ORIGIN` | 貿易項目原產國. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | 初步加工國家. |
| `424` | `AI_424_COUNTRY_PROCESS` | 加工國家. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | 拆解國家. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | 涵蓋整個加工鏈嘅國家. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | 原產地國家細分區域. |
| `4300` | `AI_4300_SHIP_TO_COMP` | 收貨方 / 送貨方公司名稱. |
| `4301` | `AI_4301_SHIP_TO_NAME` | 收貨方 / 送貨方聯絡人. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | 收貨方 / 送貨方地址第一行. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | 收貨方 / 送貨方地址第二行. |
| `4304` | `AI_4304_SHIP_TO_SUB` | 收貨方 / 送貨方市郊. |
| `4305` | `AI_4305_SHIP_TO_LOC` | 收貨方 / 送貨方地區. |
| `4306` | `AI_4306_SHIP_TO_REG` | 收貨方 / 送貨方地域. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | 收貨方 / 送貨方國家代碼. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | 收貨方 / 送貨方電話號碼. |
| `4309` | `AI_4309_SHIP_TO_GEO` | 收貨方 / 送貨方地理位置. |
| `4310` | `AI_4310_RTN_TO_COMP` | 退回公司名稱. |
| `4311` | `AI_4311_RTN_TO_NAME` | 退回聯絡人. |
| `4312` | `AI_4312_RTN_TO_ADD1` | 退回地址第一行. |
| `4313` | `AI_4313_RTN_TO_ADD2` | 退回地址第二行. |
| `4314` | `AI_4314_RTN_TO_SUB` | 退回市郊. |
| `4315` | `AI_4315_RTN_TO_LOC` | 退回地區. |
| `4316` | `AI_4316_RTN_TO_REG` | 退回地域. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | 退回國家代碼. |
| `4318` | `AI_4318_RTN_TO_POST` | 退回郵政編碼. |
| `4319` | `AI_4319_RTN_TO_PHONE` | 退回電話號碼. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | 服務代碼描述. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | 危險品標記. |
| `4322` | `AI_4322_AUTH_LEAVE` | 放行授權. |
| `4323` | `AI_4323_SIG_REQUIRED` | 需要簽名標記. |
| `4330` | `AI_4330_MAX_TEMP_F` | 最高溫度（華氏，以百分之一度表示）. |
| `4331` | `AI_4331_MAX_TEMP_C` | 最高溫度（攝氏，以百分之一度表示）. |
| `4332` | `AI_4332_MIN_TEMP_F` | 最低溫度（華氏，以百分之一度表示）. |
| `4333` | `AI_4333_MIN_TEMP_C` | 最低溫度（攝氏，以百分之一度表示）. |

### 產品屬性同可追溯性

| AI | 常數 | 描述 |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | 北約庫存編號 (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE肉類胴體同分割部位分類. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | 有效效力. |
| `7005` | `AI_7005_CATCH_AREA` | 捕撈區域. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | 漁業用途物種. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | 捕魚工具類型. |
| `7010` | `AI_7010_PROD_METHOD` | 生產方法. |
| `7020` | `AI_7020_REFURB_LOT` | 翻新批次識別碼. |
| `7021` | `AI_7021_FUNC_STAT` | 功能狀態. |
| `7022` | `AI_7022_REV_STAT` | 修訂狀態. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | 組裝件嘅全球單項資產識別碼 (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | 加工者編號，附三位數 ISO 國家代碼（10 個位）。. |
| `7040` | `AI_7040_UIC_EXT` | 附延伸碼1同進口商索引嘅GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT貨運單位類型. |

### 國家醫療保健報銷編號（NHRN）

| AI | 常數 | 描述 |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | 國家醫療保健報銷編號 (NHRN) - 德國 PZN. |
| `711` | `AI_711_NHRN_CIP` | 國家醫療保健報銷編號 (NHRN) - 法國 CIP. |
| `712` | `AI_712_NHRN_CN` | 國家醫療保健報銷編號 (NHRN) - 西班牙 CN. |
| `713` | `AI_713_NHRN_DRN` | 國家醫療保健報銷編號 (NHRN) - 巴西 DRN. |
| `714` | `AI_714_NHRN_AIM` | 國家醫療保健報銷編號 (NHRN) - 葡萄牙 AIM. |
| `715` | `AI_715_NHRN_NDC` | 國家醫療保健報銷編號 (NHRN) - 美利堅合眾國 NDC. |
| `716` | `AI_716_NHRN_AIC` | 國家醫療保健報銷編號 (NHRN) - 意大利 AIC. |
| `717` | `AI_717_NHRN_SRN` | 國家醫療保健報銷編號 (NHRN) - 哥斯達黎加衛生註冊編號. |

### 醫療保健、GMN、HIDRI、CPID、個人資料

| AI | 常數 | 描述 |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | 認證參考編號（10 個位）。. |
| `7240` | `AI_7240_PROTOCOL` | 協議識別碼. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC 媒體類型. |
| `7242` | `AI_7242_VCN` | 版本控制編號 (VCN). |
| `7250` | `AI_7250_DOB` | 出生日期 (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | 出生日期同時間 (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | 生理性別. |
| `7253` | `AI_7253_FAMILY_NAME` | 人士姓氏. |
| `7254` | `AI_7254_GIVEN_NAME` | 人士名字. |
| `7255` | `AI_7255_SUFFIX` | 人士姓名後綴. |
| `7256` | `AI_7256_FULL_NAME` | 人士全名. |
| `7257` | `AI_7257_PERSON_ADDR` | 個人地址. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | 嬰兒出生順序. |
| `7259` | `AI_7259_BABY` | 嬰兒姓氏. |
| `8001` | `AI_8001_DIMENSIONS` | 卷裝產品（闊度、長度、芯徑、方向、接駁）. |
| `8002` | `AI_8002_CMT_NO` | 流動電話識別碼. |
| `8003` | `AI_8003_GRAI` | 全球可回收資產識別碼 (GRAI). |
| `8004` | `AI_8004_GIAI` | 全球單項資產識別碼 (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | 每計量單位價格. |
| `8006` | `AI_8006_ITIP` | 個別貿易項目件識別 (ITIP). |
| `8007` | `AI_8007_IBAN` | 國際銀行帳戶號碼 (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | 生產日期同時間 (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | 光學可讀感應指示器. |
| `8010` | `AI_8010_CPID` | 組件/零件識別碼 (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | 組件/零件識別碼序號 (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | 軟件版本. |
| `8013` | `AI_8013_GMN` | 全球型號編號 (GMN). |
| `8014` | `AI_8014_MUDI` | 高度個人化裝置註冊識別碼 (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | 用嚟識別提供服務嘅機構同服務提供者之間關係嘅全球服務關係編號 (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | 用嚟識別提供服務嘅機構同服務接收者之間關係嘅全球服務關係編號 (GSRN). |
| `8019` | `AI_8019_SRIN` | 服務關係實例編號 (SRIN). |
| `8020` | `AI_8020_REF_NO` | 付款單參考編號. |
| `8026` | `AI_8026_ITIP_CONTENT` | 物流單元內所含貿易項目件數識別 (ITIP). |
| `8030` | `AI_8030_DIGSIG` | 數碼簽名 (DigSig). |
| `8040` | `AI_8040_IMEI` | 國際移動設備識別碼 (IMEI). |
| `8041` | `AI_8041_IMEI2` | 國際移動設備識別碼2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | 內置SIM卡號碼. |
| `8043` | `AI_8043_PSIM` | 實體SIM卡號碼. |
| `8110` | `AI_8110` | 用於北美洲嘅優惠券代碼識別. |
| `8111` | `AI_8111_POINTS` | 優惠券嘅忠誠積分. |
| `8112` | `AI_8112` | 用於北美洲嘅正面優惠檔案優惠券代碼識別. |
| `8200` | `AI_8200_PRODUCT_URL` | 擴展包裝URL. |

### 內部／公司用途

| AI | 常數 | 描述 |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | 貿易夥伴之間互相同意嘅資訊. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | 公司內部資訊（9 個位）。. |

---

## 附錄 B — 詮釋鍵常數

當 `GaiaParser.parse()` 以 `ParseMode.INTERPRETATION` 呼叫嗰陣，每個 `GS1AIObjectElement` 都可能帶住一串由領域專用增益器所產生嘅 `GS1AIInterpretation` 物件。請用 `GS1Constants_Enricher`（喺 `tools.pantheum.gaia.gs1.constants` 套件入面）嘅常數做鍵，嚟查找具體嘅詮釋值：

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

展示標籤 **唔係** 常數——佢哋住喺 `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` 之下嘅本地化目錄入面，以型別常數為鍵。`GS1AIInterpretation.getLabel()` 回傳解析語言嘅標籤（請睇 [本地化嘅訊息同標籤](#本地化嘅訊息同標籤)），如果某份目錄缺咗個鍵就退回英文。下面「展示標籤」一欄列出嘅，就係粵語目錄實際會輸出嘅文字——請留意，隨附嘅粵語目錄入面，詮釋標籤同錯誤訊息主要用簡體字，而 AI 描述（附錄 A）就用繁體字，所以兩個附錄嘅字體會唔一致；呢度照樣顯示程式庫實際會印出嚟嘅嘢。型別鍵本身喺所有語言都保持不變，所以請喺鍵上面做比對，千祈唔好用標籤。

### 日期同時間

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `DATE_VALUE` | 日期 | 日期類 AI（11–17、7003、7006、7011 等） |
| `DATE_FORMAT` | 日期格式 | 日期類 AI |
| `TIME_VALUE` | 时间 | 帶時間嘅 AI（7003、7011、8008 等） |
| `TIME_FORMAT` | 时间格式 | 帶時間嘅 AI |
| `DATETIME_VALUE` | 日期时间 | 日期＋時間類 AI |
| `DATETIME_FORMAT` | 日期时间格式 | 日期＋時間類 AI |

### 採收日期

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | 收获开始日期 | AI 7007 |
| `HARVEST_END_DATE` | 收获结束日期 | AI 7007（可選嘅範圍結尾） |
| `HARVEST_DATE_RANGE` | 收获日期范围 | AI 7007 |

### GS1 公司前綴

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 公司前缀 | GTIN／GLN／SSCC 類 AI |
| `GS1_MEMBER_CODE` | GS1 成员代码 | GTIN／GLN／SSCC 類 AI |
| `GS1_MEMBER_NAME` | GS1 成员组织 | GTIN／GLN／SSCC 類 AI |

### GTIN

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN 类型 | AI 01、02 |
| `GTIN_NATIVE` | GTIN | AI 01、02 |
| `PACKAGING_LEVEL` | 包装层级 | AI 01 |
| `GTIN_CHECK_DIGIT` | 校验位 | AI 01、02 |

### SSCC

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | 扩展位 | AI 00 |
| `SSCC_SERIAL_REFERENCE` | 序列参考号 | AI 00 |
| `SSCC_CHECK_DIGIT` | 校验位 | AI 00 |

### 國家（ISO 3166）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | 国家代码（数字） | 單一國家類 AI（422、424–426、4307、4317、421、7030–7039） |
| `COUNTRY_CODE_ALPHA2` | 国家代码（字母-2） | Alpha-2 國家類 AI |
| `COUNTRY_NAME` | 国家名称 | 單一國家類 AI |
| `COUNTRY_LIST` | 国家 | AI 423 — 所有名稱連埋一齊，例如 `Australia, New Zealand` |

AI 423（初次加工國家）最多可以承載五個國家，所以佢會 **每個國家發出一對有編號嘅鍵**
——`COUNTRY_CODE_NUMERIC_1`、`COUNTRY_NAME_1`、
`COUNTRY_CODE_NUMERIC_2`、`COUNTRY_NAME_2`、……——之後再跟住單一個 `COUNTRY_LIST`
摘要。可以用 `COUNTRY_CODE_NUMERIC_PREFIX`／`COUNTRY_NAME_PREFIX`
常數加上由 1 開始嘅索引嚟砌呢啲鍵，或者索性行一次 `getInterpretations()`；至於冇後綴嘅
`COUNTRY_CODE_NUMERIC`／`COUNTRY_NAME` 鍵，AI 423 係 **唔會** 發出嘅。

### 貨幣（ISO 4217）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | 货币代码 | 帶貨幣嘅金額類 AI（391n、393n） |
| `CURRENCY_ALPHA` | 货币字母代码 | 帶貨幣嘅金額類 AI |
| `CURRENCY_NAME` | 货币名称 | 帶貨幣嘅金額類 AI |

### 溫度

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `TEMPERATURE` | 温度 | AI 4330–4333 |
| `TEMPERATURE_UNIT` | 温度单位 | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | 温度（已格式化） | AI 4330–4333 |

### 性別（ISO 5218）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `SEX_CODE` | 性别代码 | AI 7252 |
| `SEX_DESCRIPTION` | 性别说明 | AI 7252 |

### 水產物種（FAO）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `SPECIES_CODE` | 物种代码 | AI 7008 |
| `SPECIES_SCIENTIFIC` | 学名 | AI 7008 |
| `SPECIES_ENGLISH` | 通用名称 | AI 7008 |
| `SPECIES_FAMILY` | 科 | AI 7008 |
| `SPECIES_ORDER` | 目 | AI 7008 |

### 北約庫存編號（NSN）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `NSN_FSG` | 供应组 | AI 7001 |
| `NSN_FSG_NAME` | 供应组名称 | AI 7001 |
| `NSN_FSCG` | 供应类别 | AI 7001 |
| `NSN_FSCG_NAME` | 供应类别名称 | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | 国家代码 | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | 国家 | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO国家代码 | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS类别 | AI 7001 |
| `NSN_NIIN` | 国家物品编号 | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### 卷裝產品

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | 卷宽 (mm) | AI 8001 |
| `ROLL_LENGTH` | 卷长 (m) | AI 8001 |
| `CORE_DIAMETER` | 芯径 (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | 卷绕方向代码 | AI 8001 |
| `WINDING_DIRECTION` | 卷绕方向 | AI 8001 |
| `SPLICES` | 拼接数 | AI 8001 |

### IBAN

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | 国家代码 | AI 8007 |
| `IBAN_COUNTRY_NAME` | 国家 | AI 8007 |
| `IBAN_CHECK_DIGITS` | 校验位 | AI 8007 |
| `IBAN_CHECK_VALID` | 校验 | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040、8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040、8041 |
| `IMEI_SERIAL` | 序列号 | AI 8040、8041 |
| `IMEI_CHECK_DIGIT` | 校验位 | AI 8040、8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040、8041 |
| `IMEI_RBI_NAME` | 分配机构 | AI 8040、8041 |

呢 15 位數拆解為 `[ TAC (8) ][ 序號 (6) ][ Luhn 校驗碼 (1) ]`，而
RBI 就係 TAC 開頭嘅 2 位數——所以 `IMEI_RBI` 係 `IMEI_TAC` 嘅前綴，唔係一段
獨立嘅區間。`IMEI_FORMATTED` 呈現嘅係 GSMA 標準嘅展示分組
`AA-BBBBBB-CCCCCC-D`（例如 `49-015420-323751-8`），佢喺 RBI 邊界處切開 TAC；
至於舊有嘅 `6-2-6-1` 分組，即係喺已停用嘅 Final Assembly Code 起點處切嗰種，
就唔會發出。

`IMEI_RBI_NAME` 透過 `ImeiRbiData` 將 RBI 解析成發配機構嘅名稱，而佢
**永遠排喺最後，並且只有喺該代碼有列喺嗰度嗰陣先至會出現**。嗰張表涵蓋三組：

- **現時仍然發配嘅** — `01` CTIA/PTCRB、`35` TÜV SÜD BABT、`86` TAF，再加 `99`
  Global Hexadecimal Administrator 同 `98`（保留）。
- **測試範圍** — `00` 同 `02`–`09`，標示嘅係測試用 IMEI 而唔係真實發配。
  可以用 `ImeiRbiData.isTestCode(code)` 查詢。
- **已停止發配嘅** — 歷史上嘅機構，例如 `49`（BZT/BAPT，德國）、`44`
  （BABT，英國）或者 `91`（MSAI，印度）。可以用 `ImeiRbiData.isNoLongerAllocating(code)` 查詢。
  帶住呢啲代碼嘅裝置好普通，而且仍然喺服役中；停咗嘅淨係新嘅發配，
  所以呢個純粹係報告性質嘅資訊，絕對唔係有效性訊號。

冇咗 `IMEI_RBI_NAME` 嘅意思係「呢個 RBI 唔喺我哋張表入面」，**唔係**「IMEI 無效」：
嗰張表係由一份已公布嘅 RBI 清單編製而成，唔係直接由 GSMA 嚟，所以佢有可能
落後於新委任嘅機構。千祈唔好由佢缺席推導出任何驗證結論；
RBI 唔係一個校驗字元。任何行過詮釋清單嘅程式碼，都必須容得落佢唔喺度，
而唔係靠位置索引嚟攞值。

### SIM 識別碼（EID／ICCID）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042、8043 |
| `SIM_MII_NAME` | 行业类别 | AI 8042 |
| `EID_BODY` | EID 主体 | AI 8042 |
| `EID_CHECK_DIGIT` | 校验位 | AI 8042 |
| `ICCID_BODY` | ICCID 主体 | AI 8043 |
| `ICCID_EXTENSION` | 扩展 | AI 8043 |

`SIM_MII` 承載嘅係開頭 **兩** 位數（`89`），即係 ITU-T E.118 指派畀
電訊業嗰一對。ISO/IEC 7812 本身將 MII 定義為 **只有第一位數**，所以
`SIM_MII_NAME` 係由開頭嗰個 `8` 經 `Iso7812Data` 解析出類別——得出
「Healthcare, telecommunications and other future industry assignments」。所以對一個格式正確嘅
EID 嚟講，呢個值係固定不變嘅；佢係為咗對應返標準而報告，並唔係用嚟做
區分。`Iso7812Data.nameForCode(digit)` 收一個單一數字，
`nameForIdentifier(prefix)` 就收一個較長嘅前綴，然後讀佢開頭嗰位數。

`SIM_MII_NAME` 淨係由 `EidEnricher`（AI 8042）發出。`IccidEnricher`（AI 8043）
會提供 `SIM_MII`，但係唔會有個類別。

### 認證參考編號

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | 序列号 | AI 7230–7239 |
| `CERT_SCHEME_CODE` | 认证方案代码 | AI 7230–7239 |
| `CERT_SCHEME_NAME` | 认证方案 | AI 7230–7239 |
| `CERT_REFERENCE` | 认证参考号 | AI 7230–7239 |

### GS1 UIC

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC 代码 | AI 7040 |
| `UIC_EXTENSION_1` | 扩展 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | 进口商索引 | AI 7040 |

### 嬰兒出生次序

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | 出生位置 | AI 7258 |
| `BIRTH_TOTAL` | 出生总数 | AI 7258 |
| `BIRTH_SEQUENCE` | 出生顺序 | AI 7258 |

### 全球型號編號（GMN）

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | 型号参考 | AI 8013 |
| `GMN_CHECK_PAIR` | 校验对 | AI 8013 |

### HIDRI

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | 设备参考号 | AI 8014 |
| `HIDRI_CHECK_PAIR` | 校验对 | AI 8014 |

### CPID

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | 组件与部件参考号 | AI 8010–8011 |

### 小數同量度值

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | 小数值 | 帶隱含小數位嘅數字類 AI（31xx–36xx） |
| `DECIMAL_AMOUNT` | 金额 | 價格類 AI（390n–395n） |
| `DECIMAL_PERCENTAGE` | 百分比 | AI 394n |
| `DECIMAL_PLACES` | 小数位数 | 同 `DECIMAL_VALUE`／`DECIMAL_AMOUNT`／`DECIMAL_PERCENTAGE` 一齊出現 |
| `PERCENTAGE_FORMAT` | 百分比格式 | AI 394n |
| `ISO_UNIT_CODE` | ISO 单位代码 | 量度類 AI |
| `ISO_UNIT_NAME` | ISO 单位名称 | 量度類 AI |
| `MONETARY_AMOUNT` | 货币金额 | 價格類 AI |
| `MONETARY_AMOUNT_DISPLAY` | 货币金额（已格式化） | 價格類 AI |

### 地理座標

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `LATITUDE` | 纬度 | AI 4309 |
| `LONGITUDE` | 经度 | AI 4309 |
| `GEO_COORDINATES` | 地理坐标 | AI 4309 |
| `LATITUDE_DMS` | 纬度 (DMS) | AI 4309 |
| `LONGITUDE_DMS` | 经度 (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | 地理坐标 (DMS) | AI 4309 |

### 生產方式

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | 生产方法代码 | AI 7010 |
| `PRODUCTION_METHOD` | 生产方法 | AI 7010 |

### AIDC 媒體類型

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC 媒介类型代码 | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC 媒介类型 | AI 7241 |

### 總數中嘅件數

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | 件号 | AI 8006 |
| `PIECE_TOTAL` | 总件数 | AI 8006 |
| `PIECE_OF_TOTAL` | 总数中的件 | AI 8006 |

### 組件拆分

呢啲鍵並唔係由 Java 增益器發出，而係由 `content/ai-content.json` 入面嘅宣告式組件拆分
所發出——佢哋將一個複合 AI 值嘅具名部分呈現出嚟。同呢個附錄入面其他所有鍵唔同，
佢哋 **喺 `GS1Constants_Enricher` 入面冇常數**：
請夾字面字串，又或者由 `GS1AIInterpretation.getType()` 讀出型別。

| 型別鍵 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | 校验位 | AI 253、255、402、410–417、8003、8017、8018 |
| `SERIAL_NUMBER` | 序列号 | AI 253、255、8003 |
| `POSTAL_CODE` | 邮政编码 | AI 421 |
| `PROCESSOR_ID` | 加工者标识 | AI 7030–7039 |

請留意呢度嘅 `CHECK_DIGIT` 係通用嘅組件拆分鍵，同上面列出嘅增益器專用
`GTIN_CHECK_DIGIT`、`SSCC_CHECK_DIGIT`、`IMEI_CHECK_DIGIT` 同 `EID_CHECK_DIGIT`
鍵係唔同嘅。

### 雜項

| 鍵常數 | 展示標籤 | 由邊度產生 |
|--------------|---------------|-------------|
| `FLAG_VALUE` | 值 | 布林／旗標類 AI（4321–4323） |
| `DECODED_TEXT` | 解码文本 | 自由文字類 AI |
