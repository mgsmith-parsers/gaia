# GaiaBuilder — 开发者指南

## 目录

1. [概览](#概览)
2. [关于 GS1 搭 General Specifications](#关于-gs1-搭-general-specifications)
3. [快速入门](#快速入门)
4. [伊哪能运作](#伊哪能运作)
5. [构建元素串](#构建元素串)
   - [属性类 AI 需要伊拉个标识键](#属性类-ai-需要伊拉个标识键)
6. [构建 Digital Link URI](#构建-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [验证搭错误](#验证搭错误)
   - [会抛异常个构建方法](#会抛异常个构建方法)
   - [勿会抛异常个 tryBuild\* 方法](#勿会抛异常个-trybuild-方法)
   - [错误消息个语言](#错误消息个语言)
   - [BuildResult](#buildresult)
9. [校验位](#校验位)
10. [线程安全](#线程安全)
11. [API 参考](#api-参考)

---

## 概览

`GaiaBuilder` 是 [`GaiaParser`](GaiaParser-WuChinese.md) 个反过来：伊把一组应用标识符（AI）／值对，变成一条格式正确个 GS1 **元素串** 或者 **GS1 Digital Link URI**。侬提供 AI 搭伊拉完整个数据值；构建器把伊拉拼辣一道，用 `GaiaParser` 所用个同一个引擎验证结果，然后输出。

因为构建器是靠 *解析伊自家个候选输出* 来验证个，所以伊返回个任何物事，侪保证好顺顺当当噉经 `GaiaParser` 解析转来——两者永远勿会辣「啥物事才算格式正确」搿桩事体浪有分歧。

**入口类：** `tools.pantheum.gaia.GaiaBuilder`

---

## 关于 GS1 搭 General Specifications

**GS1** 是一个全球性个非营利组织，负责制定搭维护供应链标识搭数据交换个开放标准。伊个标准用辣零售、医疗、物流、餐饮服务搭交关别样行业，涵盖从消费品包装浪个产品条码，一直到药品剂量个序列号追踪。

迭个构建器所实现个一切，权威依据侪是 **GS1 General Specifications**——一份文件就定义仔下头全部：

- 所有应用标识符（AI）代码、伊拉个数据标题、格式搭验证规则
- 构建搭编码 AI 元素串个语法规则
- 条码码制个要求搭 AIM 码制标识符个分配
- 校验位搭校验字符个算法
- 两位数年份个判定（滑动窗口规则）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 搭别样载体个规格

GS1 General Specifications 每年侪会更新。最新版本搭相关资源辣迭搭寻得着：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 实现仔 GS1 General Specifications 个 **第 26.0 版（已通过，2026 年 1 月）**。

GS1 Digital Link URI 由一份配套标准 **GS1 Digital Link: URI Syntax** 规范，伊定义仔构建器输出 Digital Link URI 辰光所应用个主标识键、键限定符排序，搭数据属性编码：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 实现仔 GS1 Digital Link: URI Syntax 标准个 **第 1.7.0 版（已通过，2026 年 8 月）**。

本文件通篇个章节引用侪是指 GS1 General Specifications（比方讲「Table 7-5」、「section 7.12」），只有 Digital Link 个章节编号（比方讲「§4.9」、「§4.12」）例外，搿眼是指 GS1 Digital Link: URI Syntax 标准。

---

## 快速入门

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

比起用原始 AI 字符串，用 `GS1Constants_AICodes` 常量要好眼（请看 [解析器指南个附录 A](GaiaParser-WuChinese.md#附录-a--ai-字符串常量)）：

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## 伊哪能运作

每趟构建侪跑同一条路：

1. **拼起来** — AI／值对会串连成一条候选元素串。凡是 *需要分隔符* 而又勿是最后一个元素个 AI，后头侪会插入一个 FNC1 组分隔符（`0x1D`）。预定长度个 AI（GTIN、日期、固定长度个计量值）勿加分隔符；其余全部侪加。（未识别个 AI 根本跑勿到迭一步——`ai(...)` 会立刻拒绝伊拉；请看 [构建元素串](#构建元素串)。）
2. **验证** — 候选串会经 `GaiaParser` 以 `CONTENT` 模式解析。每个值侪按伊搿个 AI 个格式搭校验位检查，而结构规则（必需／互斥个 AI 配对）也会强制执行。假使解析结果勿合法，构建就失败。
3. **输出** —
   - 元素串就返回已验证对象个 `toElementString()`。
   - Digital Link 就替每个元素指派伊个 DL 角色（主键、键限定符，或者数据属性），验证键限定符序列，输出搿个 URI，然后把输出个 URI **再解析一遍，确认伊好作为一个合法个 Digital Link 完成来回转换**——迭个是针对字符串组装搭百分号编码步骤个防御性检查。假使来回转换勿成功，就会抛出 `GaiaBuilderException`。

迭个做法搭 `DLSyntaxParser` 里向个重建逻辑一致，所以分隔符个摆位搭验证，侪搭解析器所预期个一模一样。

---

## 构建元素串

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** 会立刻验证：假使伊勿是一个已识别个 GS1 应用标识符，`ai(...)` 就会抛出 `IllegalArgumentException`。（构建器辣解析之前会把 AI 搭值串辣一道，所以像 `"99999"` 噉个未识别或者忒长个 AI 一定要辣迭搭捉牢——否则伊就会闷声勿响被重新分词成另一个 AI。）**值** 就迟眼再验证，就是辣构建个辰光。
- 搿眼值一定要 **完整**，包括任何校验位。构建器勿会替侬算或者加校验位——请看 [校验位](#校验位)。
- AI 会按侬加进去个次序输出。构建器会辣 GS1 语法要求个位置插入 FNC1 分隔符；侬勿用自家加分隔符。
- **一个 AI 侪呒没加** 就构建，会抛出 `GaiaBuilderException("No AIs supplied")`，而 `getErrors()` 是一个空清单——迭个是唯一一种勿带 `GaiaError` 个失败。
- 任何值勿符合伊个格式或者校验位规则个 AI，侪会叫构建失败。

### 属性类 AI 需要伊拉个标识键

大部分 AI 侪是 *属性*，GS1 General Specifications 要求伊拉要有一个标识键陪牢，而构建器也会强制执行迭点——伊会跑足整个语法阶段做验证，呒没任何退出个办法。一个孤零零个批次／批号或者序列号，**勿是** 一条合法个元素串：

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

标识键（GTIN `01`、SSCC `00`、GLN `414`、……）搭公司内部用个 AI（`90`–`99`）就完全合法噉好单独存在。其余全部侪需要伊拉个伴随 AI。

> 好用 `ParseConfig.skipRequiresCheck(true)` 叫 `GaiaParser` 跳过迭项检查；`GaiaBuilder` 就特为呒没提供对等个做法——伊是为仔输出符合标准个结果而造个。想拼一条特为勿完整个元素串，就自家串起来，然后辣关脱迭项检查个情况下解析。

---

## 构建 Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

一个合法个 Digital Link 需要正好一个 **主标识键**（比方讲 GTIN `01`、GLN `414`、SSCC `00`）。构建器会替每个所提供个 AI 分类：

| 角色 | 输出形式 | 例子 |
|------|-------------|---------|
| 主标识键 | 域名／前缀后头个路径段 | `/01/09506000134352` |
| 键限定符（CPV `22`、批次 `10`、序列号 `21`、……） | 后头个路径段，按 **规范 §4.9 次序**（勿是侬加进去个次序） | `/10/LOT-ABC` |
| 数据属性（其余全部） | 查询参数，**按 AI 键个字典序排列**（§4.12） | `?17=271231` |

因为限定符辣输出个辰光会重新排序，所以就算侬勿按次序提供也呒没问题——`ai("21", …)` 跑辣 `ai("10", …)` 前头，照样输出 `/10/LOT/21/SER`。光光搿个 *集合* 需要是主键所允许个。

路径搭查询里向个值侪会经百分号编码。

辣下头搿眼情况，构建会 **失败**（抛出 `GaiaBuilderException`，或者返回一个失败个 `BuildResult`）：

- 搿眼 AI 里向 **一个** 主标识键侪呒没；
- 有 **超过一个** 主标识键；
- 有 AI 辣 Digital Link 里向是 **被禁用** 个（`03`、`8014`）；
- 对所拣个主键来讲，**键限定符序列** 勿合法（比方讲某个限定符勿属于搿个键，或者限定符超出仔所容许个次序）。

---

## BuilderDigitalLinkConfig

传进一个 `BuilderDigitalLinkConfig` 就好控制 scheme、域名、路径前缀、额外查询参数搭片段：

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

| 构建器方法 | 用途 | 默认 |
|----------------|---------|---------|
| `scheme(String)` | URI scheme；一定要是 `http` 或者 `https` | `https` |
| `domain(String)` | 授权部分——主机或者 `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | 头一个主键之前个路径段；前后个斜杠会被规范化 | *（呒没）* |
| `baseUrl(String)` | 方便个写法，把一个 URL 拆成 `scheme` ＋ `domain` ＋ `pathPrefix` | — |
| `addQueryParam(String, String)` | 额外个查询参数，排辣 AI 数据属性 **后头**，按插入次序；会经百分号编码 | — |
| `fragment(String)` | URL 片段（勿包括开头个 `#`）；会经百分号编码 | *（呒没）* |

`build()` 会立刻验证配置：非 `http(s)` 个 scheme 或者空白个域名侪会抛出 `IllegalArgumentException`。

- `BuilderDigitalLinkConfig.canonical()`（别名 `defaultConfig()`）就是 `https://id.gs1.org` 个默认值，呒没任何额外物事——就是呒没参数搿个 `buildDigitalLinkUri()` 所用个物事，也是 `GS1AIObject.getCanonicalDigitalLink()` 所产生个物事。
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`、域名 `id.example.org:8080`、路径前缀 `/r`。
- 额外个查询参数永远排辣由 AI 派生个属性后头，所以规范个 AI 次序（§4.12）保得牢。

`BuilderDigitalLinkConfig` 是不可变个；同一个实例好放心重复使用。

---

## 验证搭错误

### 会抛异常个构建方法

搿眼 AI 拼勿出格式正确个结果个辰光，`buildElementString()`、`buildDigitalLinkUri()` 搭 `buildDigitalLinkUri(BuilderDigitalLinkConfig)` 会抛出 **`GaiaBuilderException`**（一个非受检个 `RuntimeException`）：

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- 对于 **内容** 浪个失败（校验位错、格式勿对、缺少／互斥个 AI），`getErrors()` 带牢个是解析器个 `GaiaError`——搭 [解析器指南里向所记录个](GaiaParser-WuChinese.md#gaiaerror) 是同一批对象。
- 对于 **Digital Link 结构** 浪个失败（呒没主键、超过一个主键、被禁用个 AI、勿合法个键限定符序列），`getErrors()` 带牢个是单独一个已按构建器语言本地化个 `GaiaError`（代码 `GE-L008`、`GE-L012`、`GE-L013` 或者 `GE-L014`）。

### 勿会抛异常个 tryBuild\* 方法

输入是由用户提供，而失败是一个好预料、好恢复个结果个辰光，就用 `tryBuild*` 搿眼变体，勿要用异常来做流程控制。伊拉勿抛异常，而是返回一个 [`BuildResult`](#buildresult)：

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

| 会抛异常 | 勿会抛异常 |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

每个 `tryBuild*` 方法侪搭伊会抛异常个孪生方法共用同一个验证核心；勿一样个光光是失败个边界。

### 错误消息个语言

内容验证个错误是从本地化错误目录提取个。调用 `language(...)` 就好拣 `GaiaBuilderException.getErrors()`／`BuildResult.getErrors()` 所带 `GaiaError` 消息个语言；默认是英文：

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

迭个搭 `GaiaParser` 经 `ParseConfig` 接受个 `GaiaConstants.Language` 配置是同一样物事，所以构建器搭解析器个本地化行为一模一样。

**内容** 个 `GaiaError` 消息，搭 **Digital Link 结构** 浪个失败（呒没主键、超过一个主键、被禁用个 AI、勿合法个键限定符序列），两者侪是经共用个错误目录本地化——后者用个代码是 `GE-L008`、`GE-L012`、`GE-L013` 搭 `GE-L014`。

### BuildResult

`BuildResult`（辣 `tools.pantheum.gaia.result` 包里向）是一个不可变个值类型，用来描述 `tryBuild*` 调用个结果：

| 方法 | 成功辰光 | 失败辰光 |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | 输出个字符串 | `null` |
| `getMessage()` | `null` | 失败描述 |
| `getErrors()` | 空清单 | 验证错误（搭 `GaiaBuilderException.getErrors()` 一样） |

---

## 校验位

构建器会验证校验位，勿过 **勿会** 计算伊拉——搿眼值本身一定要已经包含校验位。想算一个出来，就用 `GS1Utils.calculateCheckDigit`：

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` 对所提供个数字应用标准 GS1 模 10 算法，返回 `0–9` 当中个校验位；假使输入是 null、空白或者非数字，就返回 `-1`。

---

## 线程安全

`GaiaBuilder` **勿是** 线程安全个，伊是设计来用一趟个：调用 `create()`、加 AI、构建一趟。每个输出侪开一个新个构建器；勿要把同一个辣几条线程当中共用。

`BuilderDigitalLinkConfig`（搭伊输出个 `BuildResult`）是不可变个，好放心共用——辣启动个辰光做一趟配置，然后辣交关多构建器当中重复使用。

---

## API 参考

### `GaiaBuilder`

| 方法 | 描述 |
|--------|-------------|
| `static GaiaBuilder create()` | 开一个新个空构建器。 |
| `GaiaBuilder ai(String ai, String value)` | 加进一个 AI 搭伊完整个值。假使两者随便哪一个是 `null`，或者 `ai` 勿是一个已识别个 GS1 应用标识符，就抛出 `IllegalArgumentException`。 |
| `GaiaBuilder language(GaiaConstants.Language language)` | 设置内容验证错误消息个语言（默认英文）。`null` 会被忽略。 |
| `String buildElementString()` | 输出一条 GS1 元素串。失败就抛出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri()` | 输出一个规范个 Digital Link URI。失败就抛出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 按 `config` 输出一个 Digital Link URI。失败就抛出 `GaiaBuilderException`。 |
| `BuildResult tryBuildElementString()` | 勿抛异常个元素串构建。 |
| `BuildResult tryBuildDigitalLinkUri()` | 勿抛异常个规范 Digital Link 构建。 |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 按 `config` 个、勿抛异常个 Digital Link 构建。 |

### `BuilderDigitalLinkConfig`

| 成员 | 描述 |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | `https://id.gs1.org` 个默认值。 |
| `static Builder builder()` | 一个新个配置构建器。 |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | 已解析个 scheme、授权部分搭路径前缀。 |
| `getExtraQueryParams()` | 额外个查询参数，按插入次序。 |
| `getFragment()` | 片段，或者 `null`。 |

### `GaiaBuilderException`

| 成员 | 描述 |
|--------|-------------|
| `getErrors()` | 引起失败个 `GaiaError`——内容失败就是解析器个错误，或者单独一个 Digital Link 结构错误（`GE-L008`／`GE-L012`／`GE-L013`／`GE-L014`）。永远勿会是 `null`。 |

### `BuildResult`

| 成员 | 描述 |
|--------|-------------|
| `isSuccess()` | 构建有呒没成功。 |
| `getValue()` | 成功辰光个输出结果；失败辰光是 `null`。 |
| `getMessage()` | 失败辰光个失败描述；成功辰光是 `null`。 |
| `getErrors()` | 失败辰光个验证错误；成功辰光是空。永远勿会是 `null`。 |
| `getTiming()` | 迭趟构建个 `ProcessingTiming`（开始时间、处理时长），或者 `null`。 |

---

另外好看：**[GaiaParser — 开发者指南](GaiaParser-WuChinese.md)**，里向有解析搿一边个内容、AI 元素模型、错误参考，搭 AI／解释常量个附录。
