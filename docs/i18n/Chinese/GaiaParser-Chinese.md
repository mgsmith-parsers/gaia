# GAIA（GS1 Application Identifiers Analyser）— 开发者指南

## 目录

1. [概述](#概述)
2. [关于 GS1 与 General Specifications](#关于-gs1-与-general-specifications)
3. [GS1 应用标识符](#gs1-应用标识符)
4. [快速上手](#快速上手)
5. [解析处理链](#解析处理链)
   - [前置阶段 — 输入修饰器](#前置阶段--输入修饰器)
   - [阶段 0 — 关联标识符](#阶段-0--关联标识符)
   - [阶段 1 — 输入路由](#阶段-1--输入路由)
   - [阶段 2 — 语法](#阶段-2--语法)
   - [阶段 3 — 内容](#阶段-3--内容)
   - [阶段 4 — 解释](#阶段-4--解释)
6. [解析配置（`ParseConfig`）](#解析配置parseconfig)
   - [选项](#选项)
   - [本地化的消息与标签](#本地化的消息与标签)
   - [日期格式化](#日期格式化)
7. [输入修饰器](#输入修饰器)
   - [内置修饰器](#内置修饰器)
   - [编写修饰器](#编写修饰器)
   - [注册修饰器](#注册修饰器)
   - [查看修饰器做了什么](#查看修饰器做了什么)
   - [修饰器的失败处理](#修饰器的失败处理)
8. [解析模式](#解析模式)
   - [DATA_CARRIER 模式](#data_carrier-模式)
   - [SYNTAX 模式](#syntax-模式)
   - [CONTENT 模式](#content-模式)
   - [INTERPRETATION 模式（默认）](#interpretation-模式默认)
9. [关联标识符](#关联标识符)
10. [GS1 Digital Link](#gs1-digital-link)
11. [使用解析结果](#使用解析结果)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry 与 DataCarrierType](#datacarrierentry-与-datacarriertype)
12. [错误参考](#错误参考)
13. [线程安全](#线程安全)
14. [附录 A — AI 字符串常量](#附录-a--ai-字符串常量)
    - [标识与序列化](#标识与序列化)
    - [日期与时间](#日期与时间)
    - [数量与计量 — 可变计量（公制）](#数量与计量--可变计量公制)
    - [数量与计量 — 可变计量（英制／美制）](#数量与计量--可变计量英制美制)
    - [价格与货币金额](#价格与货币金额)
    - [位置与运输](#位置与运输)
    - [产品属性与可追溯性](#产品属性与可追溯性)
    - [国家医疗报销编号（NHRN）](#国家医疗报销编号nhrn)
    - [医疗、GMN、HIDRI、CPID、人员数据](#医疗gmnhidricpid人员数据)
    - [内部／企业自用](#内部企业自用)
15. [附录 B — 解释键常量](#附录-b--解释键常量)
    - [日期与时间](#日期与时间)
    - [采收日期](#采收日期)
    - [GS1 公司前缀](#gs1-公司前缀)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [国家（ISO 3166）](#国家iso-3166)
    - [货币（ISO 4217）](#货币iso-4217)
    - [温度](#温度)
    - [性别（ISO 5218）](#性别iso-5218)
    - [水生物种（FAO）](#水生物种fao)
    - [北约存货编号（NSN）](#北约存货编号nsn)
    - [卷状产品](#卷状产品)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM 标识（EID / ICCID）](#sim-标识eid--iccid)
    - [认证参考号](#认证参考号)
    - [GS1 UIC](#gs1-uic)
    - [新生儿出生顺序](#新生儿出生顺序)
    - [全球型号代码（GMN）](#全球型号代码gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [十进制数值与计量值](#十进制数值与计量值)
    - [地理坐标](#地理坐标)
    - [生产方式](#生产方式)
    - [AIDC 载体类型](#aidc-载体类型)
    - [总数中的第几件](#总数中的第几件)
    - [分量拆分](#分量拆分)
    - [其他](#其他)

---

## 概述

`GaiaParser` 是解析 GS1 应用标识符（AI）单元串的入口。它接受扫描器的原始输出，形式可为下列任意一种，并返回结构化的 `ParseResult`，其中包含全部已识别的 AI、校验错误以及（可选的）供人阅读的解释：

- 纯 AI 单元串：`0109506000134352`
- 带 AIM 码制标识符前缀的单元串：`]C10109506000134352`
- GS1 Digital Link URI：`https://example.com/01/09506000134352`
- 上述任意一种，前面可再加 8 位关联标识符：`12345678~0109506000134352`

**入口类：** `tools.pantheum.gaia.GaiaParser`

> **初次接触 Gaia？** 请从 **[GaiaParser 快速入门](GaiaParser-QuickStart-Chinese.md)** 开始——依赖项、第一次解析，以及少数几个常见陷阱，约十分钟即可读完。本指南则是完整参考。

> 与之相反的操作——由 AI/值对 *构建* 格式正确的单元串和 Digital Link URI——请参阅 **[GaiaBuilder — 开发者指南](GaiaBuilder-Chinese.md)**。

---

## 关于 GS1 与 General Specifications

**GS1** 是一家全球性非营利组织，负责制定并维护供应链标识与数据交换方面的开放标准。其标准广泛用于零售、医疗、物流、餐饮服务及其他众多行业，涵盖从消费品包装上的条码到药品剂量的序列化追溯。

本解析器所实现的一切，其权威依据是 **GS1 General Specifications**——一份统一的文件，规定了：

- 全部应用标识符（AI）代码及其数据名称、格式与校验规则
- 组成并编码 AI 单元串的语法规则
- 条码码制要求以及 AIM 码制标识符的分配
- 校验位与校验字符算法
- 两位年份的还原（滑动窗口规则）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 及其他数据载体的规范

GS1 General Specifications 每年更新。现行版本及配套资料可从下列地址获取：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 实现 GS1 General Specifications 的 **26.0 版（2026 年 1 月批准）**。

GS1 Digital Link URI 由配套标准 **GS1 Digital Link: URI Syntax** 规范，该标准规定了主标识键、键限定符的顺序，以及解析器处理 Digital Link 输入时所采用的数据属性编码方式：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 实现 GS1 Digital Link: URI Syntax 标准的 **1.7.0 版（2026 年 8 月批准）**。

本文中的章节引用均指 GS1 General Specifications（例如“Table 7-5”“section 7.12”），但 Digital Link 的章节号（例如“§4.9”“§4.12”）除外，它们指向 GS1 Digital Link: URI Syntax 标准。

---

## GS1 应用标识符

**GS1 应用标识符（AI）** 是一个由两到四位数字构成的短前缀，用于确定紧随其后的数据的含义与格式。AI 在 GS1 General Specifications 中定义，涵盖供应链数据的广泛范围：产品标识、日期、数量、批号、序列号、计量值、URL 等等。

### AI 单元的构成

每个 AI 单元由两部分组成：

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

AI 代码始终为数字。数据值紧随其后，代码与值之间没有任何分隔符。

### 定长 AI 与变长 AI

AI 分为两类：

| 类型 | 行为 | 示例 |
|---|---|---|
| **定长** | 字符数固定，始终整体读取 | AI `01`（GTIN）——始终为 14 位数字 |
| **变长** | 由 1 个字符至某一上限；以 GS 分隔符或输入结束作为终止 | AI `10`（批号）——1 至 20 个字母数字字符 |

AI 属定长还是变长，完全取决于其在 GS1 规范中的定义——解析器从不猜测。

### 含多个 AI 的单元串

多个 AI 可以拼接成单个单元串。定长 AI 可以直接拼接，因为解析器始终确切知道应读取多少字符。变长 AI 后面只要还跟有另一个 AI，就必须以 **GS 字符**（ASCII `0x1D`，在条码码制中亦称 FNC1）终止，好让解析器知道一个值在哪里结束、下一个 AI 代码从哪里开始。

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

在 Java 字符串字面量中，GS 字符写作 Unicode 转义序列 `""`。

### 常用 AI

| AI | 数据名称 | 格式 | 值示例 |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6（YYMMDD） | `261231` |
| `17` | USE BY or EXPIRY | N6（YYMMDD） | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500`（= 1.500 千克） |
| `3922` | PRICE | N..15 | `91234`（= 912.34，单一货币区） |
| `710` | NHRN PZN | X..20 | `12345678` |

> 四位计量类或价格类 AI 的**第四位数字**表示隐含小数位数：`3103` 表示以千克计、带 3 位小数的净重（`001500` = 1.500 千克），而 `3102` 会把同样的数字读作 15.00 千克。上表的 `格式` 列给出的是*数据*的格式；每个 AI 完整的 `getFormatString()` 还包含 AI 本身（例如 `3103` 为 `N4+N6`）。

### 供人阅读的解释（HRI）

惯用的可读形式是把每个 AI 代码用圆括号括起，紧置于其值之前，各单元之间以空格分隔：

```
(01)09506000134352 (17)261231 (10)LOT-001
```

HRI 中不显示 GS 分隔符。该格式由 `GS1AIObject.toHriString()` 生成。

### 四位 AI 代码

有些 AI 使用四位而非两位数字。前两位标示 AI 族；第三位和／或第四位承载附加含义（例如计量类 AI 中隐含小数点的位置）。解析器会自行从单元串中确定完整的 AI 代码——调用方始终使用完整代码（例如 `"3102"`，而不只是 `"31"`）。

---

## 快速上手

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

> **GS 分隔符：** 在含多个 AI 的串中，变长 AI 之间必须以 GS 字符（ASCII `0x1D`）分隔。在 Java 字符串字面量中请使用 `""`。

---

## 解析处理链

### 前置阶段 — 输入修饰器

若 `ParseConfig` 中配置了**输入修饰器**，它们会先于其他一切运行：先于剥离关联标识符、先于识别数据载体、也先于进入 GS1 处理链。每个修饰器为下一个修饰器改写原始输入，下文各阶段都作用于整条修饰器链的输出。

默认不配置任何修饰器，因此在你显式启用之前，该前置阶段不做任何事。参见[输入修饰器](#输入修饰器)。

---

### 阶段 0 — 关联标识符

在任何 GS1 处理之前，`GaiaParser` 会检查输入是否以可选的**关联标识符前缀**开头：正好 8 位十进制 ASCII 数字，其后紧跟一个波浪号（`~`），例如 `12345678~`。

若该前缀存在，则将其剥离并作为 `CorrelationInfo` 存入返回的 `ParseResult`。其后所有阶段都作用于剥离后的有效载荷。若无该前缀，输入原样通过。

详见[关联标识符](#关联标识符)。

---

### 阶段 1 — 输入路由

剥离关联标识符之后，`GaiaParser` 会检查（已剥离的）输入是否以 **AIM 码制标识符**开头：形如 `]` + ASCII 字母 + ASCII 数字 的三字符前缀（例如 GS1-128 为 `]C1`，GS1 DataMatrix 为 `]d2`，GS1 DataBar / GS1 Composite 为 `]e0`）。

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

若该数据载体不支持 GS1 AI（例如邮政条码），解析立即以错误 `GE-D002` 终止。

---

### 阶段 2 — 语法

始终执行。由两个子步骤组成：

**2a. 切分为词元（`AISyntaxParser`）**
- 依据 GS1 前缀表（GS1 General Specifications 表 7-5），从前两个字符读出 AI 代码的长度。
- 定长 AI 从输入中读取确切的字节数。
- 变长 AI 一直读到 GS 字符或输入结束为止。
- 对多分量 AI，其值块被切分为若干片段，每个分量一段。

**2b. 结构校验（`SyntaxValidator`）**
- 检出重复的 AI（`GE-S004`）。
- 检查必需的 AI 依赖关系，例如 AI `02` 要求同时出现 AI `37`（`GE-S005`）。
- 检查互斥的 AI 组合（`GE-S006`）。

本阶段的错误级别为 `SYNTAX_ERROR`（切分）或 `INTEGRITY_ERROR`（结构）。只要存在**任何一个**错误——无论来自切分还是结构——处理链即行停止，内容阶段与解释阶段都会被跳过。

---

### 阶段 3 — 内容

仅当阶段 2 未产生任何错误（切分与结构均无）时才执行。对每个单元逐一应用下列处理链（每一步仅在前一步无错误时才执行）：

| 步骤 | 校验器 | 错误代码 |
|---|---|---|
| 正则表达式检查 | `RegexValidator` | `GE-C001` |
| 分量字符集与格式 | `ComponentValidator` | `GE-C005` + 按条件划分的格式代码（`GE-C054`–`GE-C115`） |
| 校验位／校验字符 | `CheckDigitCharacterValidator` | `GE-C003`、`GE-C004` |
| 自定义语义校验 | `ContentValidatorRegistry` | 按条件划分的内容代码（`GE-C116`–`GE-C170`） |

本阶段的错误级别为 `FORMAT_ERROR` 或 `DATA_ERROR`，仅有一处例外：
对带 GS1 键的 AI 所做的 GS1 公司前缀检查只作提示之用，级别为 `WARNING`（参见
[错误参考](#错误参考)），因此无法识别的公司前缀本身
并不会使结果失效。

---

### 阶段 4 — 解释

仅在 `INTERPRETATION` 模式下执行，且仅当没有任何单元带有先前阶段的错误时才执行。`InterpretationEngine` 会为每个单元附加带标签的元数据：

- 重新格式化为 `dd/mm/yyyy` 的日期
- GTIN 校验位的拆解，以及 GS1 公司前缀的查找
- ISO 3166 国家名称
- ISO 4217 货币名称与符号
- 解码后的十进制金额
- HRI（供人阅读的解释）片段

这些结果以 `GS1AIInterpretation` 条目的形式附加到每个 `GS1AIObjectElement` 上。

---

## 解析配置（`ParseConfig`）

`GaiaParser` 恰好提供两个入口：

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` 使用**默认配置**运行：`INTERPRETATION` 模式、日期采用小端顺序（`dd/mm/yyyy`）并以 `/` 分隔、四位年份，以及**英文**错误消息。若要更改其中任何一项——包括解析模式——请用 `ParseConfig` 的流式构建器构造配置，并使用双参数重载。

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

所有选项枚举都位于 `GaiaConstants` 中。

### 选项

| 构建器方法 | 枚举（`GaiaConstants`） | 默认值 | 作用 |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | 处理链深度——参见[解析模式](#解析模式)。 |
| `language(...)`      | `Language`      | `ENGLISH`        | 错误消息、解释标签**以及** AI 描述的语言。 |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | 日期各部分的顺序：`LITTLE`（`dd/mm/yyyy`）、`MIDDLE`（`mm/dd/yyyy`）、`BIG`（`yyyy/mm/dd`）。 |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | 日期各部分之间的字符：`SLASH`（`/`）、`HYPHEN`（`-`）、`PERIOD`（`.`）。 |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT`（`12`）或 `THREE_LETTER`（`DEC`）。 |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT`（`2026`）或 `TWO_DIGIT`（`26`）。 |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | 跳过结构性的“要求”检查（`GE-S005`）。 |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | 跳过结构性的“互斥”检查（`GE-S006`）。 |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / 类名 | 无 | 在解析前改写原始输入的代码——两个[内置修饰器](#内置修饰器)，以及你自行编写的任何修饰器。参见[输入修饰器](#输入修饰器)。 |

这四个日期选项只影响解释增强器所生成的格式化日期字符串（在 `INTERPRETATION` 模式下），并不改变校验行为。构建器的取值可以省略——任何未设置（或传入 `null`）的选项都保留其默认值。

### 本地化的消息与标签

`language(...)` 为**三**类供人阅读的文本选择语言：错误消息、解释标签（每个 `GS1AIInterpretation` 的 `getLabel()`），以及 AI 描述（每个 `GS1AIObjectElement` 的 `getDescription()`）。

`GaiaConstants.Language` 定义了 **35 种语言**，涵盖世界上使用人数最多的语言：英语、法语、西班牙语、德语、意大利语、葡萄牙语、荷兰语、波兰语、俄语、乌克兰语、捷克语、瑞典语、汉语、日语、朝鲜语、阿拉伯语、印度尼西亚语、印地语、土耳其语、孟加拉语、乌尔都语、越南语、尼日利亚皮钦语、埃及阿拉伯语、马拉地语、泰卢固语、泰米尔语、粤语、吴语、他加禄语、波斯语、豪萨语、旁遮普语、爪哇语和斯瓦希里语。

翻译现状（随发行版提供的状态）：
- **解释标签**——已译成全部语言。
- **错误消息**——已译成全部语言。
- **AI 描述**——除英语外已译成全部语言。英语并不单独成为一份目录：它直接读自 `gs1-application-identifiers.jsonld` 中该 AI 条目的 `description` 字段，而任何 AI 描述最终都会回落到这里。

尼日利亚皮钦语（`NIGERIAN_PIDGIN`）是一种以英语为基础的克里奥尔语，其解释标签与错误消息有意沿用英文文本。AI 描述则是这一例外中的例外：它们被译成了地道的皮钦语，而未沿用英文，因为 AI 描述目录的编制独立于标签与消息目录。在生产环境中依赖机器翻译之前，应请母语者审校。

某语言目录中缺失的任何消息、标签或描述都会回落为英文。从右向左书写的语言（阿拉伯语、乌尔都语、埃及阿拉伯语、波斯语）作为字符串已正确存储；按从右向左方式呈现是展示层的职责。

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

解释标签以同样方式本地化（值本身不变——变的只是标签）：

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI 描述也以同样方式本地化（唯有 `getTitle()`，例如 `"GTIN"`，不作本地化）：

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

## 输入修饰器

**输入修饰器**是在 Gaia 解析之前改写原始输入字符串的代码。设置修饰器，是为了应对那些送达时就已被破坏的输入：扫描器把 GS 分隔符替换成可打印的占位符、中间件把有效载荷包进厂商前缀、主机系统把一切都转成大写。与其在每个调用点分别预处理每个字符串（并在其中某一处出现细微差错），不如在 `ParseConfig` 中一次性声明这一规范化，交由解析器施行。

修饰器在 `GaiaParser.parse(...)` 的最开始运行——先于剥离关联标识符、先于识别 AIM 码制标识符、也先于 GS1 处理链。其后的一切只会看到改写后的字符串。**默认不配置任何内容**，两个[内置修饰器](#内置修饰器)也不例外——你需在各个 `ParseConfig` 中显式启用它们。

**接口：** `tools.pantheum.gaia.modifier.ModifierInterface`

### 内置修饰器

核心 jar 中随附两个修饰器，位于 `tools.pantheum.gaia.modifier.custom`。它们涵盖了 GS1 有效载荷最常见的两种受损形式——被当作数据的、打印出来的 HRI 圆括号，以及多余的空格——因此常见情形无须自行编写类：

| 类 | `getName()` | 作用 |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | 去掉每个 AI 两侧的 HRI 圆括号（`(01)…(10)…`），并还原它们所隐含的 FNC1 分隔符。 |
| `ModifierRemoveSpaces` | `Remove Space Characters` | 从 AI 单元串中删除全部空格（`0x20`）。 |

它们是普通的 `ModifierInterface` 实现，并无特殊地位——其注册、排序、报告与失败方式与你自己编写的完全一致：

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

两者都无状态且线程安全，因此可共享单个实例；两者也都可通过完全限定类名来指定，以便由配置驱动地装配（参见[注册修饰器](#注册修饰器)）。

#### `ModifierRemoveAIBrackets`

GS1 供人阅读的解释会把每个 AI 印在圆括号中——`(01)09521234543213(10)ABC123`——这纯粹是一种排印约定。被设置为输出 HRI 的扫描器或中间件会把这些括号当作数据一并传出，而切分模块对此完全无从措手。

去掉括号只完成了一半工作。在 HRI 中，正是*下一个* AI 的左括号标示了上一个值的终点，因此在带括号的形式里，变长 AI 并不需要 FNC1。若不假思索地删去括号，这条边界便随之消失：

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

因此该修饰器会**在每一处前一个 AI 为变长的边界上重新插入 FNC1**，从而准确还原括号原本所编码的内容：

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

长度取自解析器自身的 `AiDefinitionRegistry`，因此所有变长 AI 都能得到处理，而不必依赖一份硬编码的清单。有三种情形被有意保留原样：已经以 FNC1 结尾的值（同时输出两种约定的数据源不会再多得一个分隔符）、括号中并非已知 AI 的代码（未知 AI 无从说明自身长度），以及串中最后一个 AI。

这一改写是**幂等的**——对其自身输出再运行一次不会有任何变化——因此在只有部分输入带括号的混合数据流中使用是安全的。

> **局限。** `(` 与 `)` 本身就是合法的 GS1 数据字符，而所用模式仅为 `\((\d{2,4})\)`。若某个值恰好含有括在圆括号中的两位至四位数字，它同样会被去掉括号。请仅对采用 HRI 括号约定的数据源使用本修饰器，而不要用于含有真正括号的值。

#### `ModifierRemoveSpaces`

某些扫描器、中间件和标签打印链路会在本来格式正确的单元串中插入多余的空格：用以填充定宽字段、分隔便于阅读的分组，或折断过长的值。切分模块会把每一个这样的空格都当作数据，从而破坏它所在的那个值；若处在变长 AI 中，还会使其后的一切发生偏移。

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

只有 ASCII `0x20` 会被删除。其他空白字符则原样保留——例如制表符不属于 GS1 可编码字符集，因此解析器会将其报告为 `GE-S008`，而不是悄然抹去。

> **局限。** 空格（`0x20`）属于 GS1 不变字符集，因此批号或客户物料号中合理地含有空格是完全可能的。修饰器无法区分多余的空格与真正的空格；请仅对你确知其 AI 值内部不使用空格的数据源使用它。

#### 前缀会被跳过，而不会被改写

修饰器在解析器剥离任何内容之前运行，因此原始输入可能仍带有关联标识符、AIM 码制标识符和 ECI 指示符。两个内置修饰器都借助解析器自身 `CorrelationIdParser` 与 `DataCarrierParser` 的逻辑定位 AI 单元串的起点，仅从该处起改写，再把结果接回**未被触动的**前缀之后：

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

值需补齐为 GTIN-14 的 EAN/UPC 载体（`isRequiresGtinPadding()`）会被整体跳过：它们的有效载荷是纯数字的条码值，并无 AI 结构，其中括号与空格都不可能具有含义。

#### 顺序：先空格，后括号

两者同时使用时，**请先注册 `ModifierRemoveSpaces`**。括号识别对位置敏感：带空格的 `( 01 )` 无法匹配 `\((\d{2,4})\)`，于是括号得以留存，它们所隐含的分隔符也就永远不会被还原。

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### 编写修饰器

当两个内置修饰器都不合用时，可自行编写——该接口只有一个方法。

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

若改写取决于解析配置，则改为重写双参数的重载：

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

约定：

| 规则 | 说明 |
|---|---|
| 无状态且线程安全 | 每个类只缓存一个实例，为所有解析共用。 |
| 公开的无参构造函数 | 仅当以类名指定修饰器时才需要。 |
| 处理 `null` 输入与空输入 | 解析器不会在运行修饰器链之前把它们滤除。 |
| 返回 `null` 表示“未作改动” | 沿用前一个值。修饰器不适用时，请原样返回 `input`。 |
| 宁可原样返回输入，也不要抛出异常 | 抛出异常的修饰器会中止解析——参见[失败处理](#修饰器的失败处理)。 |
| `getName()` | 重写它可决定在 `ModifierInfo` 中报告的名称；默认为类的简单名称。 |

### 注册修饰器

修饰器按添加顺序运行，每一个都接收前一个的输出。可按实例注册、按完全限定类名注册，或以两者之一的列表注册：

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

[内置修饰器](#内置修饰器)的指定方式与你自己的完全相同——**始终使用完全限定名**。对它们并不存在简称或别名查找；无论是否随发行版提供，`ModifierRegistry` 一律按完整类名解析每个修饰器。

名称由 `ModifierRegistry` 解析：它通过无参构造函数为每个类创建一次实例，并将该实例缓存起来，供其后任何指定同一类的配置使用。解析发生在**构建配置之时**，因此找不到的名称、未实现 `ModifierInterface` 的名称，或无法实例化的名称，都会在那里抛出 `IllegalArgumentException`——而不会在解析时才无声地出问题。若某个修饰器无法通过反射构造（例如它持有注入的依赖），可以预先注册，使其仍可按名称访问：

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### 查看修饰器做了什么

配置了修饰器时，`ParseResult.getPayload()` 反映的是**改写后的**输入。原始输入保留在 `ModifierInfo` 中：

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` 报告每个修饰器的 `getName()`，其默认值为类的简单名称，但两个内置修饰器都重写了它——因此由这两者组成的链所报告的是显示名称，而非类名：

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

未配置任何修饰器时，`getModifierInfo()` 返回 `null`。若修饰器确已运行，但每一个都原样返回了输入，则该信息仍然存在，而 `isModified()` 为 `false`——`getAppliedModifiers()` 中只列出真正改动了输入的修饰器。

### 修饰器的失败处理

抛出异常的修饰器会中止解析。该异常被包装进 `GaiaModifierException`，其中指明出错的修饰器；结果则带有内部错误 `GE-I001`，其消息中包含该名称；`getPayload()` 报告未经改写的输入。解析有意**不会**带着改写到一半的字符串继续下去：一个无声失败的规范化步骤，会产出看似有效、实则源自错误输入的结果。

---

## 解析模式

每种模式指明它所执行的最深[处理链阶段](#解析处理链)；其前的各阶段同样会执行。

| 模式 | 执行至 | 回答的问题 |
|---|---|---|
| `DATA_CARRIER` | 阶段 1（输入路由） | 承载这些数据的是哪种码制？ |
| `SYNTAX` | 阶段 2（语法） | AI 代码与长度是否格式正确？ |
| `CONTENT` | 阶段 3（内容） | 这些值是否为有效的 GS1 数据？ |
| `INTERPRETATION` | 阶段 4（解释） | 这些值是什么含义？ |

### DATA_CARRIER 模式

在阶段 1 之后停止——校验 AIM 码制标识符并确定码制，但不进入 AI 解析链。适用于识别码制并据以分流，而无须承担完整校验的开销。

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

**适用场景：** 应用需要在决定如何处理有效载荷之前先确定条码类型——例如将一维与二维码制分别路由到不同的处理器。就这类路由而言，请优先使用带类型的 [`DataCarrierType`](#datacarrierentry-与-datacarriertype)（`getDataCarrier().getDataCarrierType()`），而不要对 `getName()` 作字符串匹配。

---

### SYNTAX 模式

在阶段 2 之后停止。适用于结构性的初筛，而无须承担内容校验的开销。

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

**适用场景：** 你希望在投入完整校验之前，先确认 AI 代码与数据长度格式正确；或者在处理大批量数据、而内容错误极少的场合。

---

### CONTENT 模式

在阶段 3 之后停止。

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

> 多数 AI 不得单独出现：AI `10`（BATCH/LOT）、`17`（USE BY or EXPIRY）与 `21`
> （SERIAL）各自都*要求*同一单元串中出现诸如 AI `01` 之类的标识键；
> 若省去上例中的 GTIN，解析会在阶段 2 即以 `GE-S005` 失败，
> 根本到不了内容校验。若要解析有意省略其配套 AI 的片段，
> 请在 `ParseConfig` 上设置 `skipRequiresCheck(true)`。

**适用场景：** 你需要在把扫描所得的值用于业务流程之前，确认它完全符合 GS1 规范，同时又不想承担解释增强的开销。

---

### INTERPRETATION 模式（默认）

执行整条处理链直至阶段 4。以不带模式参数的方式调用 `parse(String)` 时即为此模式。只有顺利通过内容校验的单元才会被增强。

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

**输出示例：**
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

**货币金额示例（AI 3932 — 带 ISO 货币代码的价格）：**
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

**适用场景：** 构建展示层、标签核验工具，或任何需要以易读方式拆解 AI 值的界面。

---

## 关联标识符

某些工作流会在原始 GS1 输入之前加上专有的 8 位关联标识符，以便把扫描事件同某个会话或事务关联起来。其格式为：

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

波浪号（`~`）是分隔符。它**不**属于 GS1 内容——在任何 GS1 解析开始之前即被剥离。

### 识别规则

当输入恰以 8 位十进制 ASCII 数字（`0`–`9`）开头、其后紧跟 `~` 时，即识别出该前缀。若第 9 个字符不是 `~`，或前 8 个字符中有任何一个不是数字，该输入便按不含关联前缀的普通 GS1 内容处理。

### 获取关联标识符

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

### 与 AIM 码制标识符组合

关联前缀可以位于 AIM 码制标识符之前。解析器对此的处理是透明的：

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**实现类：** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** 把一个或多个 AI 值直接编码进 HTTP(S) URL 的结构之中，从而使实体产品的标识可在网络上解析。GAIA 针对**未压缩**的 URI 实现了 *GS1 Digital Link Standard: URI Syntax*（1.7.0 版）。

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` 会自动识别 Digital Link URI：凡以 `http://` 或 `https://` 开头的输入都会转交 `GS1DLParser`，由它执行与单元串处理链相同的内容阶段与解释阶段。

### URI 的结构与 AI 的角色

Digital Link URI 中的每个 AI 都担任三种角色之一，可在各个 `GS1AIObjectElement` 上通过 `getDigitalLinkAIType()`（`GS1Constants.DigitalLinkAIType`）获取：

| 角色 | 位置 | 示例 |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | 路径中的第一对 `/ai/值`（§4.3） | `/01/09506000134352` |
| `KEY_QUALIFIER` | 其后的路径对，按主键规定的顺序排列（§4.9） | `/10/ABC`、`/21/SER` |
| `DATA_ATTRIBUTE` | 键为纯数字的查询参数（§4.10） | `?17=271231` |

强制执行的结构规则（`DLPathRules`）：
- 路径中恰有**一个**主标识键；其余的键必须编码为查询中的数据属性。
- 键限定符必须为该主键所允许，且须按规定顺序出现。可选限定符可以省略，但*确实出现*的限定符仍须遵循既定顺序——参见[限定符的顺序](#限定符的顺序)。
- 主键之前可以出现任意的自定义路径段（例如 `/products/au/01/...`）；可通过 `getDigitalLinkInfo().getCustomPathStem()` 取得。
- 非数字的查询键（`linkType`、`context`，以及诸如 `23P` 之类的扩展参数）会被忽略；纯数字的键必须是标有 `validAsDataAttribute` 的有效 AI。
- 百分号编码的值字符会被解码；不允许出现 AI `(03)` 与 `(8014)`。

主键及其允许的限定符序列由 AI 定义**以数据驱动**的方式得出——依据 `gs1DigitalLinkPrimaryKey` 标志与 `gs1DigitalLinkQualifiers` 属性——而非硬编码。

任何结构性违规，以及并非 URL 的输入，都会产生 Digital Link 结构错误（`GE-L001`–`GE-L014`，每种情形一个代码）。即便存在结构错误，拆解出的 URL 元数据（`scheme`、`domain`、`path`、`customPathStem`、`query` 以及 `java.net.URL` 对象）仍可通过 `getDigitalLinkInfo()` 获取。

### 限定符的顺序

对每个主键，`gs1DigitalLinkQualifiers` 列出一个或多个**有序的**限定符序列。序列之中，方括号内的 AI 为**可选**，不带方括号的 AI 为**必需**，与 §4.9 中 ABNF 的 `[cpv-comp]` 记法一致。同一主键的各个序列彼此互斥。

以 GTIN（`01`）为例，它定义了两个序列：

| 路径 | 序列 | 含义 |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV、LOT、SER——各自可选，但须按此既定顺序 |
| upui-path | `235` | TPX（必需）；GTIN + TPX = UPUI |

因此 `/01/09506000134352/10/LOT-ABC/21/SER` 有效（LOT 在 SER 之前，CPV 已省略），`/01/.../21/SER/10/LOT-ABC` 会被**拒绝**（顺序不对），而 `/01/09506000134352/235/2ABC456` 属于 upui-path。顺序检查是一种保序的子序列匹配：可选 AI 可以跳过，但绝不可调换次序。

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

**实现类：** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## 使用解析结果

### ParseResult

`GaiaParser.parse()` 返回的顶层结果。

| 方法 | 返回 | 说明 |
|---|---|---|
| `isValid()` | `boolean` | 各级别均无错误时为 `true`。警告不影响有效性。`getAiObject()` 为 `null` 时始终为 `true`。 |
| `getPayload()` | `String` | 剥离关联前缀之后的输入字符串——若有[输入修饰器](#输入修饰器)改写过，则为改写之后的结果。 |
| `getPayloadContent()` | `String` | 去掉 AIM 码制标识符与 ECI 前缀之后的有效载荷。 |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`、`GS1_DIGITAL_LINK`、`DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL`（被判定为非 GS1 而拒绝的数据载体，例如 Code 39 载体 `]A0`），或 `UNABLE_TO_DETERMINE_CONTENT`（当 `aiObject` 为 `null` 时，例如在 `DATA_CARRIER` 模式下）。 |
| `getRequestedParseMode()` | `ParseMode` | 所配置的处理链深度（`ParseConfig.getRequestedParseMode()`）。 |
| `getAchievedParseMode()` | `ParseMode` | 解析实际到达的最深阶段——见下文。 |
| `isParseComplete()` | `boolean` | 解析达到所请求的深度时为 `true`（`achieved == requested`）。与 `isValid()` 无关。 |
| `getAiObject()` | `GS1AIObject` | 全部已识别的 AI。在 `DATA_CARRIER` 模式下为 `null`。 |
| `getErrors()` | `List<GaiaError>` | 级别不为 WARNING 的全部错误（对象级与所有单元级）。 |
| `getWarnings()` | `List<GaiaError>` | 级别为 WARNING 的全部提示（对象级与所有单元级）。 |
| `hasWarnings()` | `boolean` | 若产生过 WARNING 级别的提示则为 `true`。 |
| `getIssues()` | `List<GaiaError>` | 错误与警告的合集。 |
| `hasDataCarrier()` | `boolean` | 若识别出 AIM 码制标识符则为 `true`。 |
| `getDataCarrier()` | `DataCarrierEntry` | 码制元数据；若未确定载体则为 `null`。 |
| `hasEci()` | `boolean` | 若从有效载荷中剥离了 ECI 指示符则为 `true`。 |
| `getEci()` | `EciEntry` | ECI 编码元数据，或 `null`。 |
| `hasCorrelationId()` | `boolean` | 若原始输入中带有 `DDDDDDDD~` 关联前缀则为 `true`。 |
| `getCorrelationInfo()` | `CorrelationInfo` | 提取出的关联标识符；若原本没有则为 `null`。 |
| `isInputModified()` | `boolean` | 若某个[输入修饰器](#输入修饰器)改动了输入则为 `true`。 |
| `getModifierInfo()` | `ModifierInfo` | 修饰器链所做的事——`getOriginalInput()`、`getModifiedInput()`、`getAppliedModifiers()`。未配置任何修饰器时为 `null`。 |
| `getTiming()` | `ProcessingTiming` | 解析的实际计时——`getStartTime()`（`Instant`）、`getProcessingTime()`（`Duration`）、`getProcessingTimeMillis()`（`long`）、`getCompletionTime()`。若结果并非由 `GaiaParser` 生成则为 `null`。 |
| `getVersion()` | `String` | 生成该结果的库版本。 |

#### 请求的解析模式与实际达到的解析模式

处理链沿 **SYNTAX → CONTENT → INTERPRETATION** 这一阶梯推进，遇错即提前停止，因此实际*达到*的模式可能浅于所*请求*的模式。`getAchievedParseMode()` 会告诉你它走到了哪里：

| 请求 | 发生的情况 | 达到 | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | **语法或结构**错误使解析在切分之后停止 | `SYNTAX` | `false` |
| `INTERPRETATION` | **内容**错误（格式或校验位有误）阻断了增强 | `CONTENT` | `false` |
| `CONTENT` | 内容阶段始终执行到底（错误被记录，但不致命） | `CONTENT` | `true` |
| 任意（输入无误） | 处理链达到所请求的深度 | = 所请求 | `true` |
| `DATA_CARRIER` | 已校验载体；未解析 AI 内容 | `DATA_CARRIER` | `true` |
| 任意 | 数据载体在 AI 解析之前即被拒绝（例如非 GS1 的 `]A0` 载体） | `SYNTAX` | `false` |

`isParseComplete()` 与 `isValid()` 彼此独立：对校验位有误的 GTIN 作 `CONTENT` 解析，其结果是**完整的**（内容阶段确已运行），同时又是**无效的**（校验位不符）。请用 `isParseComplete()` 询问“处理链是否按我的要求走到了那么深？”，用 `isValid()` 询问“数据格式是否正确？”。

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

已识别 AI 单元的集合。

| 方法 | 说明 |
|---|---|
| `getAis()` | 按输入顺序排列的全部 `GS1AIObjectElement` 实例。 |
| `get(String aiCode)` | 与给定 AI 代码相符的第一个单元，或 `null`。 |
| `contains(String aiCode)` | 若存在具有该代码的 AI 则为 `true`。 |
| `size()` | 已识别 AI 的数量。 |
| `isValid()` | 对象级无错误且没有任何单元带有错误时为 `true`。 |
| `toHriString()` | HRI 字符串，例如 `(01)09506000134352 (17)261231`。 |
| `toElementString()` | 原始单元串——不带括号，每个变长单元之后带一个 FNC1——例如 `010950600013435210LOT-ABC<GS>17271231`。若 `isValid()` 为 `false` 则返回 `null`。 |
| `getContentType()` | `hasDigitalLink()` 为真时返回 `GS1_DIGITAL_LINK`，否则返回 `GS1_APPLICATION_IDENTIFIERS`。 |
| `hasDigitalLink()` | 若输入是带有主标识键的 GS1 Digital Link URI 则为 `true`。格式正确但没有主键的 URL 仍会提供 `getDigitalLinkInfo()`，但此处返回 `false`。 |
| `getCanonicalDigitalLink()` | 位于 `https://id.gs1.org` 的规范 GS1 Digital Link URI（§4.12）——主键与限定符作为路径段，数据属性作为按 AI 键排序的查询参数——若没有主键则为 `null`。 |
| `getDigitalLinkInfo()` | URI 拆解后的元数据（`getUri()`、`getUrl()`、`scheme`、`domain`、`path`、`getCustomPathStem()`、`query`）；若并非 Digital Link 则为 `null`。 |
| `getAllErrors()` | 对象级错误 + 全部单元错误（WARNING 除外）。 |
| `getAllWarnings()` | 对象级警告 + 全部单元警告。 |
| `getAllIssues()` | 以上全部合在一起。 |

---

### GS1AIObjectElement

单个已识别的 AI 实例。

| 方法 | 说明 |
|---|---|
| `getAi()` | AI 代码，例如 `"01"`、`"3102"`。 |
| `getTitle()` | GS1 数据名称，例如 `"GTIN"`、`"BATCH/LOT"`。 |
| `getDescription()` | 该 AI 的完整 GS1 描述，**已本地化为解析语言**（例如英文的 `"Global Trade Item Number (GTIN)"`）。若无译文，则回落为 AI 定义中的英文文本。 |
| `getFormatString()` | 涵盖该 AI *及*其数据的格式描述符，例如 AI `01` 为 `"N2+N14"`、AI `10` 为 `"N2+X..20"`、AI `3932` 为 `"N4+N3+N..15"`。 |
| `getValue()` | 从单元串中提取的原始数据值。 |
| `isFixedLength()` | 若该 AI 的数据长度固定则为 `true`。 |
| `getPosition()` | 在原始输入中的字符偏移量（从零起算）。 |
| `getGS1ComponentValues()` | 按分量划分的值片段（用于多分量 AI）。 |
| `getErrors()` | 单元级的错误，WARNING 除外。 |
| `getWarnings()` | 该单元上 WARNING 级别的提示。 |
| `getIssues()` | 该单元的错误与警告合在一起。 |
| `hasErrors()` | 若附有 WARNING 之外的错误则为 `true`。 |
| `hasWarnings()` | 若附有 WARNING 级别的提示则为 `true`。 |
| `getInterpretations()` | `GS1AIInterpretation` 条目（在 INTERPRETATION 模式下填充）。 |
| `getInterpretation(String type)` | 与所给 `GS1Constants_Enricher` 类型键相符的第一条解释，或 `null`。 |
| `getDigitalLinkAIType()` | 该单元的 Digital Link 角色（`PRIMARY_IDENTIFICATION_KEY`、`KEY_QUALIFIER`、`DATA_ATTRIBUTE`）；对单元串形式的输入则为 `null`。 |
| `hasDigitalLinkAIType()` | 若已指派 Digital Link 角色则为 `true`。 |

---

### GaiaError

不可变的校验错误或提示。

| 方法 | 说明 |
|---|---|
| `getId()` | 目录标识符，例如 `"GE-C003"`。 |
| `getLevel()` | `SYNTAX_ERROR`、`INTEGRITY_ERROR`、`FORMAT_ERROR`、`DATA_ERROR` 或 `WARNING`。 |
| `getStage()` | `DATA_CARRIER`、`DIGITAL_LINK`、`SYNTAX`、`CONTENT` 或 `INTERNAL`。 |
| `getCode()` | 供程序使用的简短代码。 |
| `getAi()` | 引发该错误的 AI 代码；对象级错误则为 `null`。 |
| `getMessage()` | 已代入取值的可读消息。 |
| `getPosition()` | 在原始输入中的字符偏移量（从零起算）。 |

---

### GS1AIInterpretation

单条带标签的解释片段，在 `INTERPRETATION` 模式下附加到 `GS1AIObjectElement` 上。

| 方法 | 说明 |
|---|---|
| `getType()` | 供程序使用的类型键，例如 `"DATE_VALUE"`、`"GS1_COMPANY_PREFIX"`。各语言之间保持一致。 |
| `getLabel()` | 供人阅读的标签，**已本地化为解析语言**（例如英文的 `"Date"` / `"GS1 company prefix"`）。 |
| `getValue()` | 提取或增强后的值，例如 `"31/12/2026"`、`"9506000"`。不作本地化。 |

---

### DataCarrierEntry 与 DataCarrierType

当输入带有 AIM 码制标识符时，`ParseResult.getDataCarrier()` 会返回一个 `DataCarrierEntry`，用以描述承载这些数据的符号。该条目是与所识别的 AIM 标识符相对应的具体注册表记录；`DataCarrierType` 则是它所属的、在编译期即已确定的枚举。

#### DataCarrierEntry

一个已识别 AIM 码制标识符的元数据（`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`）。

| 方法 | 说明 |
|---|---|
| `getAimCodeId()` | 所识别的 AIM 码制标识符，例如 `"]C1"`。 |
| `getName()` | 具体符号的可读名称，例如 `"GS1-128 / ISBT 128"`、`"EAN-8"`。 |
| `getDescription()` | 对该载体更详尽的描述。 |
| `getType()` | 以字符串表示的载体结构类型（与 `getDataCarrierType().getCategory()` 一致）。 |
| `getStandard()` | 码制标准，若已记录。 |
| `getDataCarrierType()` | 该条目所对应的、带类型的 `DataCarrierType`——用于程序化分流时应优先选用。 |
| `isGs1Capable()` | 若该载体可容纳 GS1 数据（AI 单元串和／或 Digital Link）则为 `true`。 |
| `isGs1AICapable()` | 若该载体可容纳 GS1 AI 单元串则为 `true`。 |
| `isGs1DigitalLinkCapable()` | 若该载体可容纳 GS1 Digital Link URI 则为 `true`。 |
| `isEciCapable()` | 若该载体支持 ECI 指示符则为 `true`。 |
| `isRequiresGtinPadding()` | 对于其数值在 AI 解析前需补齐为 GTIN-14 的 EAN/UPC/ITF 载体，为 `true`。 |

#### DataCarrierType

数据载体类型的编译期枚举，以 ISO/IEC 15424 所分配的 AIM 码制标识符为索引（`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`）。`]` 之后的字符（*码字符*）决定所属族；多数族对应单个常量，涵盖其全部修饰位（`ITF` 涵盖 `]I0`–`]I2`；`EAN_UPC` 涵盖 EAN-13、UPC-A、UPC-E 与 EAN-8）。凡 GS1 为 AI 数据保留了某个修饰位者，该变体自成常量——`GS1_128`（`]C1`）、`GS1_DATA_MATRIX`（`]d2`）、`GS1_QR_CODE`（`]Q3`）、`GS1_DOT_CODE`（`]J1`）——与其普通对应项相区别。若不存在 AIM 标识符，或该标识符指向未知载体，则类型为 `UNKNOWN`。

| 方法 | 说明 |
|---|---|
| `getCategory()` | 大类 `GaiaConstants.DataCarrierTypeCategory`：`LINEAR`、`STACKED_LINEAR`、`TWO_D`、`POSTAL`、`OCR` 或 `OTHER`。 |
| `getCodeChar()` | 标示所属族的 AIM 码字符，例如 QR Code 为 `"Q"`；`UNKNOWN` 时为 `null`。 |
| `getDisplayName()` | *类型*的可读名称（可能比 `DataCarrierEntry.getName()` 宽泛——例如 `"EAN-13 / UPC-A / UPC-E / EAN-8"` 相对于 `"EAN-8"`）。 |
| `isGs1DataCarrier()` | 对于始终表示 GS1 AI 数据的常量为 `true`：GS1 保留的四个变体（`GS1_128`、`GS1_DATA_MATRIX`、`GS1_QR_CODE`、`GS1_DOT_CODE`），外加 `GS1_DATABAR`——由于任何 `]e` 修饰位都表示 GS1 DataBar，它本质上即属 GS1。其范围比 `DataCarrierEntry.isGs1AICapable()` 更窄——普通的 `QR_CODE` 同样可以承载 GS1 AI 数据。 |
| `static forAimCodeId(String)` | 直接由 AIM 标识符确定类型（`"]Q3"` → `GS1_QR_CODE`；`"]Q9"` → `QR_CODE`）；对于缺失、格式有误或无法识别的标识符，返回 `UNKNOWN`。 |

按类型而非按名称分流——例如把一维符号（Code 128）与二维符号（QR / Data Matrix）区分开：

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` 只涵盖矩阵式与点阵式符号；层叠式一维载体（`PDF417`、
`CODE_16K`、`CODABLOCK`、`CODE_49`）归入 `STACKED_LINEAR`，尽管它们通常也被称作
“二维”条码。若要把两者视作同一组处理——例如据以判断是否需要影像式扫描器而非激光扫描器——
请检测是否属于这两个类别中的任意一个。

> 确定类型需要扫描数据中带有 AIM 码制标识符；没有它时，`getDataCarrier()` 为 `null`，类型则为 `UNKNOWN`。请将扫描器设置为一并传出 AIM 标识符前缀。

---

## 错误参考

| 代码 | 级别 | 阶段 | 含义 |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | AI 前缀未知——无法确定数据长度 |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | 输入过短，不足以读出完整的 AI 代码 |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | 值被截断——字符数少于该 AI 的要求 |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | 单元串中出现重复的应用标识符 |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | 缺少必需的 AI 依赖项 |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | 互斥的 AI 组合——两个不能同时出现的 AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | 切分为词元时发生意外失败 |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | 单元串中出现 GS1 可编码字符集之外的字符 |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | 变长 AI 之后缺少必需的 FNC1 分隔符 |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | 超出全部分量上限的多余数据 |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | 定长 AI 之后在串中间位置出现 FNC1 分隔符 |
| `GE-W002` | WARNING | SYNTAX | 单元串末尾出现 FNC1（仅作提示） |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI 的结构性违规——每种情形一个代码（URI 格式有误、方案、主机、限定符顺序、禁用的 AI、无主键（`GE-L013`）、多个主键（`GE-L014`）等） |
| `GE-C001` | FORMAT_ERROR | CONTENT | 值不满足该 AI 的正则表达式 |
| `GE-C003` | DATA_ERROR | CONTENT | 校验位校验失败 |
| `GE-C004` | DATA_ERROR | CONTENT | 校验字符对校验失败 |
| `GE-C005` | FORMAT_ERROR | CONTENT | 某分量的值含有允许字符集之外的字符 |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | 分量格式失败——每个校验条件一个代码（参见 `componentformat/`） |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | 自定义语义校验失败——每个校验条件一个代码（参见 `content/validator/`）。**例外：** 下列 14 项 GS1 公司前缀检查的级别为 `WARNING`，而 `GE-C168`（无法识别的 ISO 3166-1 数字国家代码）的级别为 `FORMAT_ERROR`。 |
| GS1 公司前缀检查 | WARNING | CONTENT | 在带 GS1 键的 AI 上，键并非以已知的 GS1 公司前缀开头——`GE-C122`（CPID）、`GE-C129`（GCN）、`GE-C131`（GDTI）、`GE-C132`（GIAI）、`GE-C133`（GINC）、`GE-C135`（GLN）、`GE-C137`（GMN）、`GE-C140`（GRAI）、`GE-C142`（GSIN）、`GE-C144`（GSRN）、`GE-C146`（GTIN）、`GE-C148`（HIDRI）、`GE-C153`（ITIP）、`GE-C165`（SSCC）。仅作提示——不影响有效性。 |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040（IMEI）/ 8041（IMEI2）的 IMEI 校验位（Luhn）失败 |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042（ESIM）的 EID 校验位（Luhn）失败 |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | 无法识别的 AIM 码制标识符 |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | 已确定载体，但它既不支持 GS1 AI 单元串，也不支持 Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | 意外的内部错误 |

> **消息呈现方面的已知缺陷。** 目录模板按 MessageFormat 的写法，
> 用成对的双撇号包住代入的值（`''{value}''`），但
> `ErrorRegistry` 使用普通的 `String.replace` 进行代入，因此这一重复会一直保留到
> `getMessage()`——本指南中所引的消息文本写作 `value '09506000134351'`，
> 而你目前看到的会是 `value ''09506000134351''`。全部 35 份语言目录中，
> 凡引用取值的消息都受此影响。请勿解析错误消息；
> 应比对 `getId()` / `getCode()`。

---

## 线程安全

`GaiaParser` 一经构造即为线程安全。单个实例可在多个线程间共享并并发使用。推荐的做法是在应用启动时构造一个实例并反复使用：

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` 不可变，同样可安心共享。库唯一无法替你承担的线程安全义务在于[输入修饰器](#输入修饰器)：每个修饰器只缓存一个实例，供所有并发解析共用，因此其实现必须是无状态的。

---

## 附录 A — AI 字符串常量

`GS1Constants_AICodes`（位于包 `tools.pantheum.gaia.gs1.constants`）为 GAIA 所识别的每个应用标识符声明了一个 `String` 常量。请使用这些常量，而不要把 AI 代码作为字符串硬写在代码中：

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

每个常量保存的是 AI 代码的字符串形式（例如 `AI_01_GTIN = "01"`）。

### 标识与序列化

| AI | 常量 | 说明 |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | 系列货运包装箱代码 (SSCC). |
| `01` | `AI_01_GTIN` | 全球贸易项目代码 (GTIN). |
| `02` | `AI_02_CONTENT` | 内含贸易项目的全球贸易项目代码 (GTIN). |
| `03` | `AI_03_MTO_GTIN` | 按订单制造 (MtO) 贸易项目标识 (GTIN). |
| `10` | `AI_10_BATCH_LOT` | 批号. |
| `20` | `AI_20_VARIANT` | 内部产品变体. |
| `21` | `AI_21_SERIAL` | 序列号. |
| `22` | `AI_22_CPV` | 消费者产品变体. |
| `235` | `AI_235_TPX` | 第三方控制的全球贸易项目代码序列化扩展 (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | 制造商分配的附加产品标识. |
| `241` | `AI_241_CUST_PART_NO` | 客户零件号. |
| `242` | `AI_242_MTO_VARIANT` | 按订单制造变体编号. |
| `243` | `AI_243_PCN` | 包装组件编号. |
| `250` | `AI_250_SECONDARY_SERIAL` | 次要序列号. |
| `251` | `AI_251_REF_TO_SOURCE` | 源实体参考号. |
| `253` | `AI_253_GDTI` | 全球文档类型标识符 (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | 全球位置码 (GLN) 扩展组件. |
| `255` | `AI_255_GCN` | 全球优惠券代码 (GCN). |
| `30` | `AI_30_VAR_COUNT` | 可变数量（可变计量贸易项目）. |
| `37` | `AI_37_COUNT` | 物流单元内所含贸易项目或贸易项目件数. |

### 日期与时间

| AI | 常量 | 说明 |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | 生产日期 (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | 到期日 (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | 包装日期 (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | 最佳食用期限 (YYMMDD). |
| `16` | `AI_16_SELL_BY` | 销售截止日期 (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | 有效期至 (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | 最早交货日期和时间 (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | 最晚交货日期和时间 (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | 放行日期 (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | 有效期至（含时间）(YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | 首次冷冻日期 (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | 采收/捕捞日期 (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | 检测截止日期 (YYMMDD[hhmm]). |

### 数量与计量 — 可变计量（公制）

四位 AI 族 `310n`–`369n` 用于编码可变计量的数量。第三位选定计量种类；**第四位**（`n`，0–5）为隐含小数位数——例如 `AI_3102_NET_WEIGHT_KG` 表示以千克计、带 2 位小数的净重。

| 族 | 常量式样（`n` = 小数位数字） | 说明 |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | 净重，千克（可变计量贸易项目）. |
| `311n` | `AI_311n_LENGTH_M` | 长度或第一维度，米（可变计量贸易项目）. |
| `312n` | `AI_312n_WIDTH_M` | 宽度、直径或第二维度，米（可变计量贸易项目）. |
| `313n` | `AI_313n_HEIGHT_M` | 深度、厚度、高度或第三维度，米（可变计量贸易项目）. |
| `314n` | `AI_314n_AREA_M` | 面积，平方米（可变计量贸易项目）. |
| `315n` | `AI_315n_NET_VOLUME_L` | 净体积，升（可变计量贸易项目）. |
| `316n` | `AI_316n_NET_VOLUME_M` | 净体积，立方米（可变计量贸易项目）. |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | 物流重量，千克. |
| `331n` | `AI_331n_LENGTH_M_LOG` | 长度或第一维度，米. |
| `332n` | `AI_332n_WIDTH_M_LOG` | 宽度、直径或第二维度，米. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | 深度、厚度、高度或第三维度，米. |
| `334n` | `AI_334n_AREA_M_LOG` | 面积，平方米. |
| `335n` | `AI_335n_VOLUME_L_LOG` | 物流体积，升. |
| `336n` | `AI_336n_VOLUME_M_LOG` | 物流体积，立方米. |
| `337n` | `AI_337n_KG_PER_M` | 千克每平方米. |

### 数量与计量 — 可变计量（英制／美制）

| 族 | 常量式样（`n` = 小数位数字） | 说明 |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | 净重，磅（可变计量贸易项目）. |
| `321n` | `AI_321n_LENGTH_IN` | 长度或第一维度，英寸（可变计量贸易项目）. |
| `322n` | `AI_322n_LENGTH_FT` | 长度或第一维度，英尺（可变计量贸易项目）. |
| `323n` | `AI_323n_LENGTH_YD` | 长度或第一维度，码（可变计量贸易项目）. |
| `324n` | `AI_324n_WIDTH_IN` | 宽度、直径或第二维度，英寸（可变计量贸易项目）. |
| `325n` | `AI_325n_WIDTH_FT` | 宽度、直径或第二维度，英尺（可变计量贸易项目）. |
| `326n` | `AI_326n_WIDTH_YD` | 宽度、直径或第二维度，码（可变计量贸易项目）. |
| `327n` | `AI_327n_HEIGHT_IN` | 深度、厚度、高度或第三维度，英寸（可变计量贸易项目）. |
| `328n` | `AI_328n_HEIGHT_FT` | 深度、厚度、高度或第三维度，英尺（可变计量贸易项目）. |
| `329n` | `AI_329n_HEIGHT_YD` | 深度、厚度、高度或第三维度，码（可变计量贸易项目）. |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | 物流重量，磅. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | 长度或第一维度，英寸. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | 长度或第一维度，英尺. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | 长度或第一维度，码. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | 宽度、直径或第二维度，英寸. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | 宽度、直径或第二维度，英尺. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | 宽度、直径或第二维度，码. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | 深度、厚度、高度或第三维度，英寸. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | 深度、厚度、高度或第三维度，英尺. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | 深度、厚度、高度或第三维度，码. |
| `350n` | `AI_350n_AREA_IN` | 面积，平方英寸（可变计量贸易项目）. |
| `351n` | `AI_351n_AREA_FT` | 面积，平方英尺（可变计量贸易项目）. |
| `352n` | `AI_352n_AREA_YD` | 面积，平方码（可变计量贸易项目）. |
| `353n` | `AI_353n_AREA_IN_LOG` | 面积，平方英寸. |
| `354n` | `AI_354n_AREA_FT_LOG` | 面积，平方英尺. |
| `355n` | `AI_355n_AREA_YD_LOG` | 面积，平方码. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | 净重，金衡盎司（可变计量贸易项目）. |
| `357n` | `AI_357n_NET_VOLUME_OZ` | 净重（或净体积），盎司（可变计量贸易项目）. |
| `360n` | `AI_360n_NET_VOLUME_QT` | 净体积，夸脱（可变计量贸易项目）. |
| `361n` | `AI_361n_NET_VOLUME_GAL` | 净体积，美制加仑（可变计量贸易项目）. |
| `362n` | `AI_362n_VOLUME_QT_LOG` | 物流体积，夸脱. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | 物流体积，美制加仑. |
| `364n` | `AI_364n_NET_VOLUME_IN` | 净体积，立方英寸（可变计量贸易项目）. |
| `365n` | `AI_365n_NET_VOLUME_FT` | 净体积，立方英尺（可变计量贸易项目）. |
| `366n` | `AI_366n_NET_VOLUME_YD` | 净体积，立方码（可变计量贸易项目）. |
| `367n` | `AI_367n_VOLUME_IN_LOG` | 物流体积，立方英寸. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | 物流体积，立方英尺. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | 物流体积，立方码. |

### 价格与货币金额

第四位（`n`）用于编码隐含小数位数。其允许的取值范围
因族而异——参见 `n` 列。

| 族 | 常量式样（`n` = 小数位数字） | `n` | 说明 |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | 应付金额或优惠券面值，当地货币. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | 应付金额（含 ISO 货币代码）. |
| `392n` | `AI_392n_PRICE` | 0–9 | 应付金额，单一货币区（可变计量贸易项目）. |
| `393n` | `AI_393n_PRICE` | 0–9 | 应付金额（含 ISO 货币代码，可变计量贸易项目）. |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | 优惠券折扣百分比. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | 每计量单位应付金额，单一货币区（可变计量贸易项目）. |

### 位置与运输

| AI | 常量 | 说明 |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | 客户采购订单号. |
| `401` | `AI_401_GINC` | 全球托运货物标识代码 (GINC). |
| `402` | `AI_402_GSIN` | 全球发货标识代码 (GSIN). |
| `403` | `AI_403_ROUTE` | 路由代码. |
| `410` | `AI_410_SHIP_TO_LOC` | 收货全球位置码 (GLN). |
| `411` | `AI_411_BILL_TO` | 账单收件方全球位置码 (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | 采购来源全球位置码 (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | 转发全球位置码 (GLN). |
| `414` | `AI_414_LOC_NO` | 实体位置标识 - 全球位置码 (GLN). |
| `415` | `AI_415_PAY_TO` | 开票方全球位置码 (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | 生产或服务地点全球位置码 (GLN). |
| `417` | `AI_417_PARTY` | 相关方全球位置码 (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | 单一邮政管理机构范围内的收货邮政编码. |
| `421` | `AI_421_SHIP_TO_POST` | 收货邮政编码（含 ISO 国家代码）. |
| `422` | `AI_422_ORIGIN` | 贸易项目原产国. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | 初加工国. |
| `424` | `AI_424_COUNTRY_PROCESS` | 加工国. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | 拆解国. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | 覆盖全流程的国家. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | 原产国行政区划. |
| `4300` | `AI_4300_SHIP_TO_COMP` | 收货公司名称. |
| `4301` | `AI_4301_SHIP_TO_NAME` | 收货联系人. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | 收货地址第 1 行. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | 收货地址第 2 行. |
| `4304` | `AI_4304_SHIP_TO_SUB` | 收货郊区/城区. |
| `4305` | `AI_4305_SHIP_TO_LOC` | 收货所在地. |
| `4306` | `AI_4306_SHIP_TO_REG` | 收货地区. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | 收货国家代码. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | 收货电话号码. |
| `4309` | `AI_4309_SHIP_TO_GEO` | 收货地理位置. |
| `4310` | `AI_4310_RTN_TO_COMP` | 退货公司名称. |
| `4311` | `AI_4311_RTN_TO_NAME` | 退货联系人. |
| `4312` | `AI_4312_RTN_TO_ADD1` | 退货地址第 1 行. |
| `4313` | `AI_4313_RTN_TO_ADD2` | 退货地址第 2 行. |
| `4314` | `AI_4314_RTN_TO_SUB` | 退货郊区/城区. |
| `4315` | `AI_4315_RTN_TO_LOC` | 退货所在地. |
| `4316` | `AI_4316_RTN_TO_REG` | 退货地区. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | 退货国家代码. |
| `4318` | `AI_4318_RTN_TO_POST` | 退货邮政编码. |
| `4319` | `AI_4319_RTN_TO_PHONE` | 退货电话号码. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | 服务代码描述. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | 危险品标志. |
| `4322` | `AI_4322_AUTH_LEAVE` | 免签收放行授权. |
| `4323` | `AI_4323_SIG_REQUIRED` | 需要签名标志. |
| `4330` | `AI_4330_MAX_TEMP_F` | 最高温度（华氏度，以百分之一度表示）. |
| `4331` | `AI_4331_MAX_TEMP_C` | 最高温度（摄氏度，以百分之一度表示）. |
| `4332` | `AI_4332_MIN_TEMP_F` | 最低温度（华氏度，以百分之一度表示）. |
| `4333` | `AI_4333_MIN_TEMP_C` | 最低温度（摄氏度，以百分之一度表示）. |

### 产品属性与可追溯性

| AI | 常量 | 说明 |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | 北约储备编号 (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE 胴体和分割肉分类. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | 活性效价. |
| `7005` | `AI_7005_CATCH_AREA` | 捕捞区域. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | 渔业用途物种. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | 渔具类型. |
| `7010` | `AI_7010_PROD_METHOD` | 生产方法. |
| `7020` | `AI_7020_REFURB_LOT` | 翻新批次标识符. |
| `7021` | `AI_7021_FUNC_STAT` | 功能状态. |
| `7022` | `AI_7022_REV_STAT` | 版本状态. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | 组件的全球单个资产标识代码 (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | 加工方编号，附三位 ISO 国家代码（10 个位置）。. |
| `7040` | `AI_7040_UIC_EXT` | 带扩展位 1 和进口商索引的 GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT 货运单元类型. |

### 国家医疗报销编号（NHRN）

| AI | 常量 | 说明 |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | 国家医疗报销编码 (NHRN) - 德国 PZN. |
| `711` | `AI_711_NHRN_CIP` | 国家医疗报销编码 (NHRN) - 法国 CIP. |
| `712` | `AI_712_NHRN_CN` | 国家医疗报销编码 (NHRN) - 西班牙 CN. |
| `713` | `AI_713_NHRN_DRN` | 国家医疗报销编码 (NHRN) - 巴西 DRN. |
| `714` | `AI_714_NHRN_AIM` | 国家医疗报销编码 (NHRN) - 葡萄牙 AIM. |
| `715` | `AI_715_NHRN_NDC` | 国家医疗报销编码 (NHRN) - 美国 NDC. |
| `716` | `AI_716_NHRN_AIC` | 国家医疗报销编码 (NHRN) - 意大利 AIC. |
| `717` | `AI_717_NHRN_SRN` | 国家医疗报销编码 (NHRN) - 哥斯达黎加卫生注册号. |

### 医疗、GMN、HIDRI、CPID、人员数据

| AI | 常量 | 说明 |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | 认证参考号（10 个位置）。. |
| `7240` | `AI_7240_PROTOCOL` | 协议标识符. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC 介质类型. |
| `7242` | `AI_7242_VCN` | 版本控制编号 (VCN). |
| `7250` | `AI_7250_DOB` | 出生日期 (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | 出生日期和时间 (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | 生理性别. |
| `7253` | `AI_7253_FAMILY_NAME` | 个人姓氏. |
| `7254` | `AI_7254_GIVEN_NAME` | 个人名字. |
| `7255` | `AI_7255_SUFFIX` | 个人姓名后缀. |
| `7256` | `AI_7256_FULL_NAME` | 个人全名. |
| `7257` | `AI_7257_PERSON_ADDR` | 个人地址. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | 婴儿出生顺序. |
| `7259` | `AI_7259_BABY` | 婴儿姓氏. |
| `8001` | `AI_8001_DIMENSIONS` | 卷状产品（宽度、长度、芯径、方向、接头数）. |
| `8002` | `AI_8002_CMT_NO` | 移动电话标识符. |
| `8003` | `AI_8003_GRAI` | 全球可回收资产标识代码 (GRAI). |
| `8004` | `AI_8004_GIAI` | 全球单个资产标识代码 (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | 每计量单位价格. |
| `8006` | `AI_8006_ITIP` | 单个贸易项目件标识 (ITIP). |
| `8007` | `AI_8007_IBAN` | 国际银行账户号码 (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | 生产日期和时间 (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | 光学可读传感器指示器. |
| `8010` | `AI_8010_CPID` | 组件/部件标识符 (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | 组件/部件标识符序列号 (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | 软件版本. |
| `8013` | `AI_8013_GMN` | 全球型号代码 (GMN). |
| `8014` | `AI_8014_MUDI` | 高度个体化设备注册标识符 (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | 全球服务关系代码 (GSRN)，用于标识提供服务的组织与服务提供者之间的关系. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | 全球服务关系代码 (GSRN)，用于标识提供服务的组织与服务接收者之间的关系. |
| `8019` | `AI_8019_SRIN` | 服务关系实例编号 (SRIN). |
| `8020` | `AI_8020_REF_NO` | 付款单参考号. |
| `8026` | `AI_8026_ITIP_CONTENT` | 物流单元内所含贸易项目件 (ITIP) 标识. |
| `8030` | `AI_8030_DIGSIG` | 数字签名 (DigSig). |
| `8040` | `AI_8040_IMEI` | 国际移动设备识别码 (IMEI). |
| `8041` | `AI_8041_IMEI2` | 第二国际移动设备识别码 (IMEI2). |
| `8042` | `AI_8042_ESIM` | 嵌入式 SIM 卡号 (eSIM). |
| `8043` | `AI_8043_PSIM` | 实体 SIM 卡号. |
| `8110` | `AI_8110` | 用于北美的优惠券代码标识. |
| `8111` | `AI_8111_POINTS` | 优惠券积分. |
| `8112` | `AI_8112` | 用于北美的正向报价文件优惠券代码标识. |
| `8200` | `AI_8200_PRODUCT_URL` | 扩展包装 URL. |

### 内部／企业自用

| AI | 常量 | 说明 |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | 贸易伙伴间共同约定的信息. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | 企业内部信息（9 个位置）。. |

---

## 附录 B — 解释键常量

以 `ParseMode.INTERPRETATION` 调用 `GaiaParser.parse()` 时，每个 `GS1AIObjectElement` 都可能带有一份由各领域增强器生成的 `GS1AIInterpretation` 对象列表。请以 `GS1Constants_Enricher`（位于包 `tools.pantheum.gaia.gs1.constants`）中的常量作为键，查找特定的解释值：

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

显示标签**并非**常量——它们存放在 `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` 下的本地化目录中，以类型常量为索引。`GS1AIInterpretation.getLabel()` 返回解析语言所对应的标签（参见[本地化的消息与标签](#本地化的消息与标签)），当某份目录缺少该键时则回落为英文。下表“显示标签”一列给出的是目录中随发行版提供的中文文本；至于类型键本身，各语言之间保持一致——因此请始终比对键，而绝不要比对标签。

### 日期与时间

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `DATE_VALUE` | 日期 | 日期类 AI（11–17、7003、7006、7011 等） |
| `DATE_FORMAT` | 日期格式 | 日期类 AI |
| `TIME_VALUE` | 时间 | 含时间的 AI（7003、7011、8008 等） |
| `TIME_FORMAT` | 时间格式 | 含时间的 AI |
| `DATETIME_VALUE` | 日期时间 | 日期与时间类 AI |
| `DATETIME_FORMAT` | 日期时间格式 | 日期与时间类 AI |

### 采收日期

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | 收获开始日期 | AI 7007 |
| `HARVEST_END_DATE` | 收获结束日期 | AI 7007（可选的区间终点） |
| `HARVEST_DATE_RANGE` | 收获日期范围 | AI 7007 |

### GS1 公司前缀

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 公司前缀 | GTIN / GLN / SSCC 类 AI |
| `GS1_MEMBER_CODE` | GS1 成员代码 | GTIN / GLN / SSCC 类 AI |
| `GS1_MEMBER_NAME` | GS1 成员组织 | GTIN / GLN / SSCC 类 AI |

### GTIN

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN 类型 | AI 01、02 |
| `GTIN_NATIVE` | GTIN | AI 01、02 |
| `PACKAGING_LEVEL` | 包装层级 | AI 01 |
| `GTIN_CHECK_DIGIT` | 校验位 | AI 01、02 |

### SSCC

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | 扩展位 | AI 00 |
| `SSCC_SERIAL_REFERENCE` | 序列参考号 | AI 00 |
| `SSCC_CHECK_DIGIT` | 校验位 | AI 00 |

### 国家（ISO 3166）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | 国家代码（数字） | 单一国家类 AI（422、424–426、4307、4317、421、7030–7039） |
| `COUNTRY_CODE_ALPHA2` | 国家代码（字母-2） | 二字母国家代码类 AI |
| `COUNTRY_NAME` | 国家名称 | 单一国家类 AI |
| `COUNTRY_LIST` | 国家 | AI 423 —— 全部名称合并，例如 `Australia, New Zealand` |

AI 423（初加工国）最多可承载五个国家，因此它会为每个国家
输出一组**带编号的键对**——`COUNTRY_CODE_NUMERIC_1`、`COUNTRY_NAME_1`、
`COUNTRY_CODE_NUMERIC_2`、`COUNTRY_NAME_2`……——其后再跟一条汇总用的
`COUNTRY_LIST`。可由常量 `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` 加上从 1 起算的序号来拼出这些键，或者干脆遍历 `getInterpretations()`；
不带后缀的 `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` 键对 AI 423 **不会**输出。

### 货币（ISO 4217）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | 货币代码 | 带货币的金额类 AI（391n、393n） |
| `CURRENCY_ALPHA` | 货币字母代码 | 带货币的金额类 AI |
| `CURRENCY_NAME` | 货币名称 | 带货币的金额类 AI |

### 温度

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `TEMPERATURE` | 温度 | AI 4330–4333 |
| `TEMPERATURE_UNIT` | 温度单位 | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | 温度（已格式化） | AI 4330–4333 |

### 性别（ISO 5218）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `SEX_CODE` | 性别代码 | AI 7252 |
| `SEX_DESCRIPTION` | 性别说明 | AI 7252 |

### 水生物种（FAO）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `SPECIES_CODE` | 物种代码 | AI 7008 |
| `SPECIES_SCIENTIFIC` | 学名 | AI 7008 |
| `SPECIES_ENGLISH` | 通用名称 | AI 7008 |
| `SPECIES_FAMILY` | 科 | AI 7008 |
| `SPECIES_ORDER` | 目 | AI 7008 |

### 北约存货编号（NSN）

| 键常量 | 显示标签 | 生成者 |
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

### 卷状产品

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | 卷宽 (mm) | AI 8001 |
| `ROLL_LENGTH` | 卷长 (m) | AI 8001 |
| `CORE_DIAMETER` | 芯径 (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | 卷绕方向代码 | AI 8001 |
| `WINDING_DIRECTION` | 卷绕方向 | AI 8001 |
| `SPLICES` | 拼接数 | AI 8001 |

### IBAN

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | 国家代码 | AI 8007 |
| `IBAN_COUNTRY_NAME` | 国家 | AI 8007 |
| `IBAN_CHECK_DIGITS` | 校验位 | AI 8007 |
| `IBAN_CHECK_VALID` | 校验 | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040、8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040、8041 |
| `IMEI_SERIAL` | 序列号 | AI 8040、8041 |
| `IMEI_CHECK_DIGIT` | 校验位 | AI 8040、8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040、8041 |
| `IMEI_RBI_NAME` | 分配机构 | AI 8040、8041 |

这 15 位数字可拆解为 `[ TAC（8 位）][ 序列号（6 位）][ Luhn 校验位（1 位）]`，其中
RBI 即 TAC 的前 2 位——因此 `IMEI_RBI` 是 `IMEI_TAC` 的前缀，而非
另一段独立字段。`IMEI_FORMATTED` 给出 GSMA 标准的显示分组
`AA-BBBBBB-CCCCCC-D`（例如 `49-015420-323751-8`），它在 RBI 边界处切分
TAC；旧有的 `6-2-6-1` 分组在已废止的 Final Assembly Code 起始处切分，
现已不再输出。

`IMEI_RBI_NAME` 借助 `ImeiRbiData` 把 RBI 解析为分配机构的名称，且
**总是最后追加，并且仅在该代码列于表中时才追加**。该表涵盖三类：

- **目前仍在分配** —— `01` CTIA/PTCRB、`35` TÜV SÜD BABT、`86` TAF，另有 `99`
  Global Hexadecimal Administrator 与 `98`（保留）。
- **测试区段** —— `00` 以及 `02`–`09`，用以标示测试用 IMEI，而非真实分配。
  可用 `ImeiRbiData.isTestCode(code)` 查询。
- **已停止分配** —— 历史上的机构，如 `49`（BZT/BAPT，德国）、`44`
  （BABT，英国）或 `91`（MSAI，印度）。可用 `ImeiRbiData.isNoLongerAllocating(code)` 查询。
  带这些代码的设备属寻常之物，仍在使用；停止的只是新代码的分配，
  因此这是供报告参考的信息，绝非有效性的判据。

缺少 `IMEI_RBI_NAME` 意味着“该 RBI 不在我们的表中”，而**不是**“IMEI 无效”：
该表是依据公开的 RBI 名录汇编而成，并非直接取自 GSMA，因此
可能滞后于新近指定的机构。请勿据其缺失推断任何校验结论；
RBI 并不是校验字符。遍历解释列表的代码同样必须
容许它缺席，而不应按位置索引。

### SIM 标识（EID / ICCID）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042、8043 |
| `SIM_MII_NAME` | 行业类别 | AI 8042 |
| `EID_BODY` | EID 主体 | AI 8042 |
| `EID_CHECK_DIGIT` | 校验位 | AI 8042 |
| `ICCID_BODY` | ICCID 主体 | AI 8043 |
| `ICCID_EXTENSION` | 扩展 | AI 8043 |

`SIM_MII` 承载开头的**两**位数字（`89`），即 ITU-T E.118 分配给
电信业的那一对。而 ISO/IEC 7812 本身把 MII 定义为**仅第一位数字**，因此
`SIM_MII_NAME` 通过 `Iso7812Data` 由开头的 `8` 得出类别——结果为
“Healthcare, telecommunications and other future industry assignments”。对格式正确的
EID 而言，该值因此是恒定的；报告它是为了对照标准便于追溯，而非
用作区分依据。`Iso7812Data.nameForCode(digit)` 接受单个数字，
`nameForIdentifier(prefix)` 则接受较长的前缀并读取其首位数字。

`SIM_MII_NAME` 仅由 `EidEnricher`（AI 8042）输出。`IccidEnricher`（AI 8043）
只给出 `SIM_MII`，不含类别。

### 认证参考号

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | 序列号 | AI 7230–7239 |
| `CERT_SCHEME_CODE` | 认证方案代码 | AI 7230–7239 |
| `CERT_SCHEME_NAME` | 认证方案 | AI 7230–7239 |
| `CERT_REFERENCE` | 认证参考号 | AI 7230–7239 |

### GS1 UIC

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC 代码 | AI 7040 |
| `UIC_EXTENSION_1` | 扩展 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | 进口商索引 | AI 7040 |

### 新生儿出生顺序

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | 出生位置 | AI 7258 |
| `BIRTH_TOTAL` | 出生总数 | AI 7258 |
| `BIRTH_SEQUENCE` | 出生顺序 | AI 7258 |

### 全球型号代码（GMN）

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | 型号参考 | AI 8013 |
| `GMN_CHECK_PAIR` | 校验对 | AI 8013 |

### HIDRI

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | 设备参考号 | AI 8014 |
| `HIDRI_CHECK_PAIR` | 校验对 | AI 8014 |

### CPID

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | 组件与部件参考号 | AI 8010–8011 |

### 十进制数值与计量值

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | 小数值 | 带隐含小数位的数值类 AI（31xx–36xx） |
| `DECIMAL_AMOUNT` | 金额 | 价格类 AI（390n–395n） |
| `DECIMAL_PERCENTAGE` | 百分比 | AI 394n |
| `DECIMAL_PLACES` | 小数位数 | 与 `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` 一同输出 |
| `PERCENTAGE_FORMAT` | 百分比格式 | AI 394n |
| `ISO_UNIT_CODE` | ISO 单位代码 | 计量类 AI |
| `ISO_UNIT_NAME` | ISO 单位名称 | 计量类 AI |
| `MONETARY_AMOUNT` | 货币金额 | 价格类 AI |
| `MONETARY_AMOUNT_DISPLAY` | 货币金额（已格式化） | 价格类 AI |

### 地理坐标

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `LATITUDE` | 纬度 | AI 4309 |
| `LONGITUDE` | 经度 | AI 4309 |
| `GEO_COORDINATES` | 地理坐标 | AI 4309 |
| `LATITUDE_DMS` | 纬度 (DMS) | AI 4309 |
| `LONGITUDE_DMS` | 经度 (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | 地理坐标 (DMS) | AI 4309 |

### 生产方式

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | 生产方法代码 | AI 7010 |
| `PRODUCTION_METHOD` | 生产方法 | AI 7010 |

### AIDC 载体类型

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC 媒介类型代码 | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC 媒介类型 | AI 7241 |

### 总数中的第几件

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | 件号 | AI 8006 |
| `PIECE_TOTAL` | 总件数 | AI 8006 |
| `PIECE_OF_TOTAL` | 总数中的件 | AI 8006 |

### 分量拆分

这些键由 `content/ai-content.json` 中声明式的分量拆分输出，而非
由某个 Java 增强器生成——它们呈现复合 AI 值中各个具名的组成部分。与本附录中
其余各键不同，它们在 **`GS1Constants_Enricher` 中并无对应常量**：请直接比对
字面字符串，或通过 `GS1AIInterpretation.getType()` 读取其类型。

| 类型键 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | 校验位 | AI 253、255、402、410–417、8003、8017、8018 |
| `SERIAL_NUMBER` | 序列号 | AI 253、255、8003 |
| `POSTAL_CODE` | 邮政编码 | AI 421 |
| `PROCESSOR_ID` | 加工者标识 | AI 7030–7039 |

请注意，此处的 `CHECK_DIGIT` 是分量拆分所用的通用键，与上文所列
各增强器专有的 `GTIN_CHECK_DIGIT`、`SSCC_CHECK_DIGIT`、`IMEI_CHECK_DIGIT` 及
`EID_CHECK_DIGIT` 并不相同。

### 其他

| 键常量 | 显示标签 | 生成者 |
|--------------|---------------|-------------|
| `FLAG_VALUE` | 值 | 布尔／标志类 AI（4321–4323） |
| `DECODED_TEXT` | 解码文本 | 自由文本类 AI |
