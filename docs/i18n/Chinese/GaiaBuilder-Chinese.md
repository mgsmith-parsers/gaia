# GaiaBuilder — 开发者指南

## 目录

1. [概述](#概述)
2. [关于 GS1 与 General Specifications](#关于-gs1-与-general-specifications)
3. [快速上手](#快速上手)
4. [工作原理](#工作原理)
5. [构建单元串](#构建单元串)
   - [属性类 AI 需要与之配套的标识键](#属性类-ai-需要与之配套的标识键)
6. [构建 Digital Link URI](#构建-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [校验与错误](#校验与错误)
   - [会抛出异常的构建方法](#会抛出异常的构建方法)
   - [不抛异常的 tryBuild\* 方法](#不抛异常的-trybuild-方法)
   - [错误消息的语言](#错误消息的语言)
   - [BuildResult](#buildresult)
9. [校验位](#校验位)
10. [线程安全](#线程安全)
11. [API 参考](#api-参考)

---

## 概述

`GaiaBuilder` 是 [`GaiaParser`](GaiaParser-Chinese.md) 的逆向操作：它把一组应用标识符（AI）与值构成的键值对，变成格式正确的 GS1 **单元串**或 **GS1 Digital Link URI**。你提供各个 AI 及其完整的数据值；构建器将它们组装起来，用与 `GaiaParser` 相同的机制校验结果，再输出成品。

由于构建器是通过*解析自己生成的候选输出*来完成校验的，它所返回的一切都必定能被 `GaiaParser` 无误地重新读回——二者对“何为格式正确”绝不会产生分歧。

**入口类：** `tools.pantheum.gaia.GaiaBuilder`

---

## 关于 GS1 与 General Specifications

**GS1** 是一家全球性非营利组织，负责制定并维护供应链标识与数据交换方面的开放标准。其标准广泛用于零售、医疗、物流、餐饮服务及其他众多行业，涵盖从消费品包装上的条码到药品剂量的序列化追溯。

本构建器所实现的一切，其权威依据是 **GS1 General Specifications**——一份统一的文件，规定了：

- 全部应用标识符（AI）代码及其数据名称、格式与校验规则
- 组成并编码 AI 单元串的语法规则
- 条码码制要求以及 AIM 码制标识符的分配
- 校验位与校验字符算法
- 两位年份的还原（滑动窗口规则）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 及其他数据载体的规范

GS1 General Specifications 每年更新。现行版本及配套资料可从下列地址获取：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 实现 GS1 General Specifications 的 **26.0 版（2026 年 1 月批准）**。

GS1 Digital Link URI 由配套标准 **GS1 Digital Link: URI Syntax** 规范，该标准规定了主标识键、键限定符的顺序，以及构建器生成 Digital Link URI 时所采用的数据属性编码方式：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 实现 GS1 Digital Link: URI Syntax 标准的 **1.7.0 版（2026 年 8 月批准）**。

本文中的章节引用均指 GS1 General Specifications（例如“Table 7-5”“section 7.12”），但 Digital Link 的章节号（例如“§4.9”“§4.12”）除外，它们指向 GS1 Digital Link: URI Syntax 标准。

---

## 快速上手

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

相较于裸写的 AI 字符串，请优先使用 `GS1Constants_AICodes` 中的常量（参见[解析器指南的附录 A](GaiaParser-Chinese.md#附录-a--ai-字符串常量)）：

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## 工作原理

每次构建都循同一条路径：

1. **组装** —— 各 AI/值对被拼接成一个候选单元串。凡*需要分隔符*且不是最后一个单元的 AI，其后都会插入一个 FNC1 组分隔符（`0x1D`）。长度预先确定的 AI（GTIN、日期、定长计量值）不加分隔符；其余则都要加。（无法识别的 AI 根本到不了这一步——`ai(...)` 会当即拒绝它们；参见[构建单元串](#构建单元串)。）
2. **校验** —— 候选串由 `GaiaParser` 以 `CONTENT` 模式解析。每个值都要与其 AI 的格式和校验位相核对，同时施行各项结构规则（必需或互斥的 AI 组合）。若解析结果无效，构建即告失败。
3. **生成** ——
   - 对单元串，返回已校验对象的 `toElementString()`。
   - 对 Digital Link，先为每个单元指派其 DL 角色（主键、键限定符或数据属性），校验键限定符序列，生成 URI，随后**再次解析该 URI，以确认它能作为有效的 Digital Link 原样读回**——这是对字符串组装与百分号编码的一道防御性检查。若这一往返不成立，则抛出 `GaiaBuilderException`。

这与 `DLSyntaxParser` 中的重建逻辑如出一辙，因此分隔符的摆放与校验方式，同解析器所预期的完全一致。

---

## 构建单元串

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** 会被即时校验：若它不是已知的 GS1 应用标识符，`ai(...)` 便抛出 `IllegalArgumentException`。（构建器在解析之前先把 AI 与值拼接起来，因此像 `"99999"` 这样无法识别或过长的 AI 必须在此截获——否则它会被无声地重新切分成另一个 AI。）**值**则要等到构建时才校验。
- 值必须**完整**，包括其校验位在内。构建器不会替你计算或补上校验位——参见[校验位](#校验位)。
- 各 AI 按你添加的顺序输出。GS1 语法要求何处需要 FNC1 分隔符，构建器便在何处插入；无须你自行添加。
- **完全不含 AI** 的构建会抛出 `GaiaBuilderException("No AIs supplied")`，且 `getErrors()` 列表为空——这是唯一一种不带任何 `GaiaError` 的失败。
- 某个 AI 的值若违反其格式或校验位规则，构建即告失败。

### 属性类 AI 需要与之配套的标识键

多数 AI 属于*属性*，GS1 General Specifications 要求它们必须与某个标识键同行，而构建器对此严格执行：它会走完整个语法阶段来校验，且无从规避。单凭一个批号或一个序列号，**并不**构成有效的单元串：

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

各类标识键（GTIN `01`、SSCC `00`、GLN `414` 等）以及企业内部自用的 AI（`90`–`99`）完全可以单独出现。此外的一切都需要与之配套的那一个。

> 可以通过 `ParseConfig.skipRequiresCheck(true)` 让 `GaiaParser` 跳过这项检查；`GaiaBuilder` 则有意不提供对应手段——它的职责是产出符合标准的输出。若要组装一个有意残缺的单元串，请自行拼接，再关闭该检查去解析它。

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

有效的 Digital Link 要求恰有一个**主标识键**（例如 GTIN `01`、GLN `414`、SSCC `00`）。构建器会为所提供的每个 AI 归类：

| 角色 | 生成形式 | 示例 |
|------|-------------|---------|
| 主标识键 | 域名或前缀之后的路径段 | `/01/09506000134352` |
| 键限定符（CPV `22`、批号 `10`、序列号 `21` 等） | 其后的路径段，按 **§4.9 的规范顺序**排列（而非你添加的顺序） | `/10/LOT-ABC` |
| 数据属性（其余一切） | 查询参数，**按 AI 键的字典序排序**（§4.12） | `?17=271231` |

由于限定符在生成时会被重新排序，因此不按次序提供它们并无妨碍：把 `ai("21", …)` 写在 `ai("10", …)` 之前，生成的仍是 `/10/LOT/21/SER`。只有这个*集合*本身必须为该主键所允许。

路径与查询中的值都会作百分号编码。

出现下列情形时，构建会**失败**（抛出 `GaiaBuilderException`，或返回失败的 `BuildResult`）：

- 各 AI 之中**没有**主标识键；
- 主标识键**多于一个**；
- 某个 AI 在 Digital Link 中被**禁用**（`03`、`8014`）；
- 对所选的主键而言，**键限定符序列**不合法（例如某限定符并不属于该键，或限定符超出了允许的顺序）。

---

## BuilderDigitalLinkConfig

传入 `BuilderDigitalLinkConfig` 即可控制方案、域名、路径前缀、额外的查询参数以及片段：

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

| 构建器方法 | 用途 | 默认值 |
|----------------|---------|---------|
| `scheme(String)` | URI 方案；必须为 `http` 或 `https` | `https` |
| `domain(String)` | 授权部分——主机或 `主机:端口` | `id.gs1.org` |
| `pathPrefix(String)` | 位于第一个主键之前的路径段；首尾的斜杠会被规范化 | *（无）* |
| `baseUrl(String)` | 便捷方法，将一个 URL 拆分为 `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | 额外的查询参数，按插入顺序追加在 AI 数据属性**之后**；作百分号编码 | — |
| `fragment(String)` | URL 片段（不含前导 `#`）；作百分号编码 | *（无）* |

`build()` 会即时校验配置：方案若不是 `http(s)`，或域名为空，都会抛出 `IllegalArgumentException`。

- `BuilderDigitalLinkConfig.canonical()`（别名 `defaultConfig()`）即不带任何附加项的默认值 `https://id.gs1.org`——这正是无参调用 `buildDigitalLinkUri()` 所用的配置，也正是 `GS1AIObject.getCanonicalDigitalLink()` 所生成的形式。
- `baseUrl("http://id.example.org:8080/r")` → 方案 `http`、域名 `id.example.org:8080`、路径前缀 `/r`。
- 额外的查询参数始终排在由 AI 导出的属性之后，因此 AI 的规范顺序（§4.12）得以保持。

`BuilderDigitalLinkConfig` 不可变；同一个实例可随意反复使用。

---

## 校验与错误

### 会抛出异常的构建方法

当各 AI 无法构成格式正确的输出时，`buildElementString()`、`buildDigitalLinkUri()` 与 `buildDigitalLinkUri(BuilderDigitalLinkConfig)` 会抛出 **`GaiaBuilderException`**（一个非受检的 `RuntimeException`）：

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- 对**内容**类失败（校验位有误、格式不符、AI 缺失或互斥），`getErrors()` 携带的是解析器的 `GaiaError` 对象——与[解析器指南中所述](GaiaParser-Chinese.md#gaiaerror)的是同一批对象。
- 对 **Digital Link 结构**类失败（无主键、主键多于一个、AI 被禁用、键限定符序列不合法），`getErrors()` 携带单个 `GaiaError`（代码为 `GE-L008`、`GE-L012`、`GE-L013` 或 `GE-L014`），并已本地化为构建器所设定的语言。

### 不抛异常的 tryBuild\* 方法

当输入来自用户、失败属于预料之中且可以补救时，请改用 `tryBuild*` 系列方法，而不要以异常来控制流程。它们返回 [`BuildResult`](#buildresult)，而不抛出异常：

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

| 抛出异常 | 不抛异常 |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

每个 `tryBuild*` 方法都与其抛出异常的孪生方法共用同一套校验内核；不同之处只在于失败的呈现方式。

### 错误消息的语言

内容校验的错误取自本地化的错误目录。调用 `language(...)` 即可选定 `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` 所携带的 `GaiaError` 消息语言；默认为英文：

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

这与 `GaiaParser` 通过 `ParseConfig` 所接受的 `GaiaConstants.Language` 设置完全相同，因此构建器与解析器的本地化方式一致。

无论是**内容**类的 `GaiaError` 消息，还是 **Digital Link 结构**类失败（无主键、主键多于一个、AI 被禁用、键限定符序列不合法），都经由同一份共享的错误目录本地化——后者使用代码 `GE-L008`、`GE-L012`、`GE-L013` 与 `GE-L014`。

### BuildResult

`BuildResult`（位于包 `tools.pantheum.gaia.result`）是一个不可变的值类型，用以描述 `tryBuild*` 调用的结果：

| 方法 | 成功时 | 失败时 |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | 生成的字符串 | `null` |
| `getMessage()` | `null` | 失败说明 |
| `getErrors()` | 空列表 | 各项校验错误（与 `GaiaBuilderException.getErrors()` 相同） |

---

## 校验位

构建器只校验而**不**计算校验位——各个值本身就必须已含校验位。若要计算，请使用 `GS1Utils.calculateCheckDigit`：

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` 对所给数字施以 GS1 标准的模 10 算法，返回 `0–9` 之间的校验位；若输入为 null、为空或非数字，则返回 `-1`。

---

## 线程安全

`GaiaBuilder` **并非**线程安全，且只适合一次性使用：调用 `create()`、添加各个 AI、构建一次。请为每份输出新建一个构建器；不要在多个线程间共享同一个。

`BuilderDigitalLinkConfig`（以及它所产出的 `BuildResult`）不可变，可随意共享——在启动时构建一份配置，即可在众多构建器中反复使用。

---

## API 参考

### `GaiaBuilder`

| 方法 | 说明 |
|--------|-------------|
| `static GaiaBuilder create()` | 新建一个空的构建器。 |
| `GaiaBuilder ai(String ai, String value)` | 添加一个 AI 及其完整的值。若两者之一为 `null`，或 `ai` 并非已知的 GS1 应用标识符，则抛出 `IllegalArgumentException`。 |
| `GaiaBuilder language(GaiaConstants.Language language)` | 设定内容校验错误消息的语言（默认为英文）。传入 `null` 时忽略。 |
| `String buildElementString()` | 生成 GS1 单元串。失败时抛出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri()` | 生成规范的 Digital Link URI。失败时抛出 `GaiaBuilderException`。 |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 依 `config` 生成 Digital Link URI。失败时抛出 `GaiaBuilderException`。 |
| `BuildResult tryBuildElementString()` | 不抛异常地构建单元串。 |
| `BuildResult tryBuildDigitalLinkUri()` | 不抛异常地构建规范的 Digital Link。 |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | 依 `config` 不抛异常地构建 Digital Link。 |

### `BuilderDigitalLinkConfig`

| 成员 | 说明 |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | 默认值 `https://id.gs1.org`。 |
| `static Builder builder()` | 新建一个配置构建器。 |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | 已确定的方案、授权部分与路径前缀。 |
| `getExtraQueryParams()` | 额外的查询参数，按插入顺序排列。 |
| `getFragment()` | 片段，或 `null`。 |

### `GaiaBuilderException`

| 成员 | 说明 |
|--------|-------------|
| `getErrors()` | 导致本次失败的各个 `GaiaError`——内容类失败时为解析器的错误，否则为单个 Digital Link 结构错误（`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`）。绝不为 `null`。 |

### `BuildResult`

| 成员 | 说明 |
|--------|-------------|
| `isSuccess()` | 本次构建是否成功。 |
| `getValue()` | 成功时为所生成的输出；失败时为 `null`。 |
| `getMessage()` | 失败时为失败说明；成功时为 `null`。 |
| `getErrors()` | 失败时为各项校验错误；成功时为空列表。绝不为 `null`。 |
| `getTiming()` | 本次构建的 `ProcessingTiming`（开始时间、处理时长），或 `null`。 |

---

另请参阅：**[GaiaParser — 开发者指南](GaiaParser-Chinese.md)**，其中讲述解析一侧的内容、AI 单元模型、错误参考，以及 AI 常量与解释常量的两个附录。
