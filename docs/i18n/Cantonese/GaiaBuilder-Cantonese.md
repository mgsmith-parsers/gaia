# GaiaBuilder — 開發者指南

## 目錄

1. [概覽](#概覽)
2. [關於 GS1 同 General Specifications](#關於-gs1-同-general-specifications)
3. [快速入門](#快速入門)
4. [佢點樣運作](#佢點樣運作)
5. [建構元素字串](#建構元素字串)
   - [屬性類 AI 需要佢哋嘅識別鍵](#屬性類-ai-需要佢哋嘅識別鍵)
6. [建構 Digital Link URI](#建構-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [驗證同錯誤](#驗證同錯誤)
   - [會拋例外嘅建構方法](#會拋例外嘅建構方法)
   - [唔會拋例外嘅 tryBuild\* 方法](#唔會拋例外嘅-trybuild-方法)
   - [錯誤訊息嘅語言](#錯誤訊息嘅語言)
   - [BuildResult](#buildresult)
9. [校驗碼](#校驗碼)
10. [執行緒安全](#執行緒安全)
11. [API 參考](#api-參考)

---

## 概覽

`GaiaBuilder` 係 [`GaiaParser`](GaiaParser-Cantonese.md) 嘅相反：佢將一組應用識別碼（AI）／值對，變成一條格式正確嘅 GS1 **元素字串** 或者 **GS1 Digital Link URI**。你提供 AI 同佢哋完整嘅資料值；建構器將佢哋砌埋一齊，用 `GaiaParser` 所用嘅同一個引擎驗證結果，然後輸出。

因為建構器係靠 *解析佢自己嘅候選輸出* 嚟驗證，所以佢回傳嘅任何嘢，都保證可以順利噉經 `GaiaParser` 解析返轉頭——兩者永遠都唔會喺「乜嘢先算格式正確」呢件事上面有分歧。

**入口類別：** `tools.pantheum.gaia.GaiaBuilder`

---

## 關於 GS1 同 General Specifications

**GS1** 係一個全球性嘅非牟利組織，負責制定同維護供應鏈識別同資料交換嘅開放標準。佢嘅標準用喺零售、醫療、物流、餐飲服務同好多其他行業，涵蓋由消費品包裝上面嘅產品條碼，一直到藥物劑量嘅序號追蹤。

呢個建構器所實作嘅一切，權威依據都係 **GS1 General Specifications**——一份文件就定義咗以下全部：

- 所有應用識別碼（AI）代碼、佢哋嘅資料標題、格式同驗證規則
- 建構同編碼 AI 元素字串嘅語法規則
- 條碼碼制嘅要求同 AIM 碼制識別碼嘅分配
- 校驗碼同校驗字元嘅演算法
- 兩位數年份嘅判定（滑動視窗規則）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 同其他載體嘅規格

GS1 General Specifications 每年都會更新。最新版本同相關資源可以喺呢度搵到：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 實作咗 GS1 General Specifications 嘅 **第 26.0 版（已通過，2026 年 1 月）**。

GS1 Digital Link URI 由一份配套標準 **GS1 Digital Link: URI Syntax** 規範，佢定義咗建構器輸出 Digital Link URI 嗰陣所套用嘅主要識別鍵、鍵限定詞排序，同資料屬性編碼：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 實作咗 GS1 Digital Link: URI Syntax 標準嘅 **第 1.7.0 版（已通過，2026 年 8 月）**。

本文件通篇嘅章節引用都係指 GS1 General Specifications（例如「Table 7-5」、「section 7.12」），只有 Digital Link 嘅章節編號（例如「§4.9」、「§4.12」）例外，嗰啲係指 GS1 Digital Link: URI Syntax 標準。

---

## 快速入門

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

比起用原始 AI 字串，用 `GS1Constants_AICodes` 常數會好啲（請睇 [解析器指南嘅附錄 A](GaiaParser-Cantonese.md#附錄-a--ai-字串常數)）：

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## 佢點樣運作

每次建構都行同一條路：

1. **砌埋** — AI／值對會串連成一條候選元素字串。凡係 *需要分隔符* 而又唔係最後一個元素嘅 AI，後面都會插入一個 FNC1 群組分隔符（`0x1D`）。預定長度嘅 AI（GTIN、日期、固定長度嘅量度值）唔會加分隔符；其餘全部都會。（未辨識嘅 AI 根本行唔到呢一步——`ai(...)` 會即刻拒絕佢哋；請睇 [建構元素字串](#建構元素字串)。）
2. **驗證** — 候選字串會經 `GaiaParser` 以 `CONTENT` 模式解析。每個值都會按佢個 AI 嘅格式同校驗碼檢查，而結構規則（必要／互斥嘅 AI 配對）亦都會強制執行。如果解析結果唔合法，建構就失敗。
3. **輸出** —
   - 元素字串就回傳已驗證物件嘅 `toElementString()`。
   - Digital Link 就為每個元素指派佢嘅 DL 角色（主要鍵、鍵限定詞，或者資料屬性），驗證鍵限定詞序列，輸出個 URI，然後將輸出咗嘅 URI **再解析一次，確認佢可以作為一個合法嘅 Digital Link 完成來回轉換**——呢個係針對字串組裝同百分號編碼步驟嘅防禦性檢查。如果來回轉換唔成功，就會拋出 `GaiaBuilderException`。

呢個做法同 `DLSyntaxParser` 入面嘅重建邏輯一致，所以分隔符嘅擺位同驗證，都同解析器所預期嘅一模一樣。

---

## 建構元素字串

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** 會即刻驗證：如果佢唔係一個已辨識嘅 GS1 應用識別碼，`ai(...)` 就會拋出 `IllegalArgumentException`。（建構器喺解析之前會將 AI 同值串埋一齊，所以好似 `"99999"` 噉嘅未辨識或者過長 AI 必須喺呢度捉住——如果唔係，佢就會靜靜雞被重新分詞成另一個 AI。）**值** 就遲啲先驗證，即係喺建構嗰陣。
- 啲值必須 **完整**，包括任何校驗碼。建構器唔會幫你計或者加校驗碼——請睇 [校驗碼](#校驗碼)。
- AI 會按你加入嘅次序輸出。建構器會喺 GS1 語法要求嘅位置插入 FNC1 分隔符；你唔使自己加分隔符。
- **完全冇加任何 AI** 就建構，會拋出 `GaiaBuilderException("No AIs supplied")`，而 `getErrors()` 係一個空清單——呢個係唯一一種唔帶 `GaiaError` 嘅失敗。
- 任何值唔符合佢嘅格式或者校驗碼規則嘅 AI，都會令建構失敗。

### 屬性類 AI 需要佢哋嘅識別鍵

大部分 AI 都係 *屬性*，GS1 General Specifications 要求佢哋要有一個識別鍵陪住，而建構器亦都會強制執行呢點——佢會行足整個語法階段做驗證，冇任何退出嘅辦法。一個孤零零嘅批次／批號或者序號，**唔係** 一條合法嘅元素字串：

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

識別鍵（GTIN `01`、SSCC `00`、GLN `414`、……）同公司內部用嘅 AI（`90`–`99`）就完全合法噉可以獨立存在。其餘全部都需要佢哋嘅伴隨 AI。

> 可以用 `ParseConfig.skipRequiresCheck(true)` 叫 `GaiaParser` 跳過呢項檢查；`GaiaBuilder` 就刻意冇提供對等嘅做法——佢係為咗輸出符合標準嘅結果而設。想砌一條刻意唔完整嘅元素字串，就自己串起佢，然後喺關咗呢項檢查嘅情況下解析。

---

## 建構 Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

一個合法嘅 Digital Link 需要啱啱好一個 **主要識別鍵**（例如 GTIN `01`、GLN `414`、SSCC `00`）。建構器會為每個所提供嘅 AI 分類：

| 角色 | 輸出形式 | 例子 |
|------|-------------|---------|
| 主要識別鍵 | 網域／前綴之後嘅路徑區段 | `/01/09506000134352` |
| 鍵限定詞（CPV `22`、批次 `10`、序號 `21`、……） | 之後嘅路徑區段，按 **標準 §4.9 次序**（唔係你加入嘅次序） | `/10/LOT-ABC` |
| 資料屬性（其餘全部） | 查詢參數，**按 AI 鍵嘅字典序排列**（§4.12） | `?17=271231` |

因為限定詞喺輸出嗰陣會重新排序，所以就算你唔按次序提供都冇問題——`ai("21", …)` 行先過 `ai("10", …)`，一樣會輸出 `/10/LOT/21/SER`。淨係嗰個 *集合* 需要係主要鍵所允許嘅。

路徑同查詢入面嘅值都會經百分號編碼。

喺以下情況，建構會 **失敗**（拋出 `GaiaBuilderException`，或者回傳一個失敗嘅 `BuildResult`）：

- 啲 AI 入面 **完全冇** 主要識別鍵；
- 有 **多過一個** 主要識別鍵；
- 有 AI 喺 Digital Link 入面係 **被禁用** 嘅（`03`、`8014`）；
- 對所揀嘅主要鍵嚟講，**鍵限定詞序列** 唔合法（例如某個限定詞唔屬於嗰個鍵，或者限定詞超出咗所容許嘅次序）。

---

## BuilderDigitalLinkConfig

傳入一個 `BuilderDigitalLinkConfig` 就可以控制 scheme、網域、路徑前綴、額外查詢參數同片段：

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

| 建構器方法 | 用途 | 預設 |
|----------------|---------|---------|
| `scheme(String)` | URI scheme；必須係 `http` 或者 `https` | `https` |
| `domain(String)` | 授權部分——主機或者 `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | 第一個主要鍵之前嘅路徑區段；前後嘅斜線會被正規化 | *（冇）* |
| `baseUrl(String)` | 方便嘅寫法，將一個 URL 拆成 `scheme` ＋ `domain` ＋ `pathPrefix` | — |
| `addQueryParam(String, String)` | 額外嘅查詢參數，排喺 AI 資料屬性 **之後**，按插入次序；會經百分號編碼 | — |
| `fragment(String)` | URL 片段（唔包括開頭嘅 `#`）；會經百分號編碼 | *（冇）* |

`build()` 會即刻驗證設定：非 `http(s)` 嘅 scheme 或者空白嘅網域都會拋出 `IllegalArgumentException`。

- `BuilderDigitalLinkConfig.canonical()`（別名 `defaultConfig()`）就係 `https://id.gs1.org` 嘅預設值，冇任何額外嘢——即係冇參數嗰個 `buildDigitalLinkUri()` 所用嘅嘢，亦都係 `GS1AIObject.getCanonicalDigitalLink()` 所產生嘅嘢。
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`、網域 `id.example.org:8080`、路徑前綴 `/r`。
- 額外嘅查詢參數永遠都排喺由 AI 衍生嘅屬性之後，所以標準嘅 AI 次序（§4.12）得以保留。

`BuilderDigitalLinkConfig` 係不可變嘅；同一個實例可以放心重複使用。

---

## 驗證同錯誤

### 會拋例外嘅建構方法

當啲 AI 砌唔出格式正確嘅結果嗰陣，`buildElementString()`、`buildDigitalLinkUri()` 同 `buildDigitalLinkUri(BuilderDigitalLinkConfig)` 會拋出 **`GaiaBuilderException`**（一個非受檢嘅 `RuntimeException`）：

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- 對於 **內容** 上嘅失敗（校驗碼錯、格式唔夾、缺少／互斥嘅 AI），`getErrors()` 帶住嘅係解析器嘅 `GaiaError`——同 [解析器指南入面所記錄嘅](GaiaParser-Cantonese.md#gaiaerror) 係同一批物件。
- 對於 **Digital Link 結構** 上嘅失敗（冇主要鍵、多過一個主要鍵、被禁用嘅 AI、不合法嘅鍵限定詞序列），`getErrors()` 帶住嘅係單一個已按建構器語言本地化嘅 `GaiaError`（代碼 `GE-L008`、`GE-L012`、`GE-L013` 或者 `GE-L014`）。

### 唔會拋例外嘅 tryBuild\* 方法

當輸入係由使用者提供，而失敗係一個可預期、可以復原嘅結果嗰陣，就用 `tryBuild*` 呢啲變體，唔好用例外嚟做流程控制。佢哋唔會拋例外，而係回傳一個 [`BuildResult`](#buildresult)：

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

| 會拋例外 | 唔會拋例外 |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

每個 `tryBuild*` 方法都同佢會拋例外嘅孖生方法共用同一個驗證核心；唔同嘅淨係失敗嘅邊界。

### 錯誤訊息嘅語言

內容驗證嘅錯誤係由本地化錯誤目錄提取嘅。呼叫 `language(...)` 就可以揀 `GaiaBuilderException.getErrors()`／`BuildResult.getErrors()` 所帶 `GaiaError` 訊息嘅語言；預設係英文：

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

呢個同 `GaiaParser` 經 `ParseConfig` 接受嘅 `GaiaConstants.Language` 設定係同一樣嘢，所以建構器同解析器嘅本地化行為一模一樣。

**內容** 嘅 `GaiaError` 訊息，同 **Digital Link 結構** 上嘅失敗（冇主要鍵、多過一個主要鍵、被禁用嘅 AI、不合法嘅鍵限定詞序列），兩者都係經共用嘅錯誤目錄本地化——後者用嘅代碼係 `GE-L008`、`GE-L012`、`GE-L013` 同 `GE-L014`。

### BuildResult

`BuildResult`（喺 `tools.pantheum.gaia.result` 套件入面）係一個不可變嘅值型別，用嚟描述 `tryBuild*` 呼叫嘅結果：

| 方法 | 成功時 | 失敗時 |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | 輸出咗嘅字串 | `null` |
| `getMessage()` | `null` | 失敗描述 |
| `getErrors()` | 空清單 | 驗證錯誤（同 `GaiaBuilderException.getErrors()` 一樣） |

---

## 校驗碼

建構器會驗證校驗碼，但係 **唔會** 計算佢哋——啲值本身必須已經包含校驗碼。想計一個出嚟，就用 `GS1Utils.calculateCheckDigit`：

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` 對所提供嘅數字套用標準 GS1 模 10 演算法，回傳 `0–9` 之間嘅校驗碼；如果輸入係 null、空白或者非數字，就回傳 `-1`。

---

## 執行緒安全

`GaiaBuilder` **唔係** 執行緒安全嘅，佢係設計嚟用一次嘅：呼叫 `create()`、加 AI、建構一次。每個輸出都開一個新嘅建構器；唔好將同一個喺多條執行緒之間共用。

`BuilderDigitalLinkConfig`（同佢輸出嘅 `BuildResult`）係不可變嘅，可以放心共用——喺啟動嗰陣整一次設定，然後喺好多個建構器之間重複使用。

---

## API 參考

### `GaiaBuilder`

| 方法 | 描述 |
|--------|-------------|
| `static GaiaBuilder create()` | 開一個新嘅空建構器。 |
| `GaiaBuilder ai(String ai, String value)` | 加入一個 AI 同佢完整嘅值。如果兩者任何一個係 `null`，或者 `ai` 唔係一個已辨識嘅 GS1 應用識別碼，就拋出 `IllegalArgumentException`。 |
| `GaiaBuilder language(GaiaConstants.Language language)` | 設定內容驗證錯誤訊息嘅語言（預設英文）。`null` 會被忽略。 |
| `String buildElementString()` | 輸出一條 GS1 元素字串。失敗就拋出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri()` | 輸出一個標準嘅 Digital Link URI。失敗就拋出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 按 `config` 輸出一個 Digital Link URI。失敗就拋出 `GaiaBuilderException`。 |
| `BuildResult tryBuildElementString()` | 唔會拋例外嘅元素字串建構。 |
| `BuildResult tryBuildDigitalLinkUri()` | 唔會拋例外嘅標準 Digital Link 建構。 |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 按 `config` 嘅、唔會拋例外嘅 Digital Link 建構。 |

### `BuilderDigitalLinkConfig`

| 成員 | 描述 |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | `https://id.gs1.org` 嘅預設值。 |
| `static Builder builder()` | 一個新嘅設定建構器。 |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | 已解析嘅 scheme、授權部分同路徑前綴。 |
| `getExtraQueryParams()` | 額外嘅查詢參數，按插入次序。 |
| `getFragment()` | 片段，或者 `null`。 |

### `GaiaBuilderException`

| 成員 | 描述 |
|--------|-------------|
| `getErrors()` | 引致失敗嘅 `GaiaError`——內容失敗就係解析器嘅錯誤，或者單一個 Digital Link 結構錯誤（`GE-L008`／`GE-L012`／`GE-L013`／`GE-L014`）。永遠唔會係 `null`。 |

### `BuildResult`

| 成員 | 描述 |
|--------|-------------|
| `isSuccess()` | 建構有冇成功。 |
| `getValue()` | 成功時嘅輸出結果；失敗時係 `null`。 |
| `getMessage()` | 失敗時嘅失敗描述；成功時係 `null`。 |
| `getErrors()` | 失敗時嘅驗證錯誤；成功時係空。永遠唔會係 `null`。 |
| `getTiming()` | 呢次建構嘅 `ProcessingTiming`（開始時間、處理時長），或者 `null`。 |

---

另請參閱：**[GaiaParser — 開發者指南](GaiaParser-Cantonese.md)**，入面有解析嗰邊嘅內容、AI 元素模型、錯誤參考，同 AI／詮釋常數嘅附錄。
