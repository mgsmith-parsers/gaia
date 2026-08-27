# GAIA（GS1 Application Identifiers Analyser）— 开发者指南

## 目录

1. [概览](#概览)
2. [关于 GS1 搭 General Specifications](#关于-gs1-搭-general-specifications)
3. [GS1 应用标识符](#gs1-应用标识符)
4. [快速入门](#快速入门)
5. [解析流程](#解析流程)
   - [前置阶段 — 输入修改器](#前置阶段--输入修改器)
   - [阶段 0 — 关联 ID](#阶段-0--关联-id)
   - [阶段 1 — 输入分流](#阶段-1--输入分流)
   - [阶段 2 — 语法](#阶段-2--语法)
   - [阶段 3 — 内容](#阶段-3--内容)
   - [阶段 4 — 解释](#阶段-4--解释)
6. [解析配置（`ParseConfig`）](#解析配置parseconfig)
   - [选项](#选项)
   - [本地化个消息搭标签](#本地化个消息搭标签)
   - [日期格式化](#日期格式化)
7. [输入修改器](#输入修改器)
   - [内置修改器](#内置修改器)
   - [写一个修改器](#写一个修改器)
   - [登记修改器](#登记修改器)
   - [看修改器做过啥](#看修改器做过啥)
   - [修改器个失败处理](#修改器个失败处理)
8. [解析模式](#解析模式)
   - [DATA_CARRIER 模式](#data_carrier-模式)
   - [SYNTAX 模式](#syntax-模式)
   - [CONTENT 模式](#content-模式)
   - [INTERPRETATION 模式（默认）](#interpretation-模式默认)
9. [关联 ID](#关联-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [处理结果](#处理结果)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry 搭 DataCarrierType](#datacarrierentry-搭-datacarriertype)
12. [错误参考](#错误参考)
13. [线程安全](#线程安全)
14. [附录 A — AI 字符串常量](#附录-a--ai-字符串常量)
    - [标识搭序列化](#标识搭序列化)
    - [日期搭时间](#日期搭时间)
    - [数量搭计量 — 可变计量（公制）](#数量搭计量--可变计量公制)
    - [数量搭计量 — 可变计量（英制／美制）](#数量搭计量--可变计量英制美制)
    - [价格搭货币金额](#价格搭货币金额)
    - [位置搭运输](#位置搭运输)
    - [产品属性搭可追溯性](#产品属性搭可追溯性)
    - [国家医疗保健报销编号（NHRN）](#国家医疗保健报销编号nhrn)
    - [医疗保健、GMN、HIDRI、CPID、个人数据](#医疗保健gmnhidricpid个人数据)
    - [内部／公司用途](#内部公司用途)
15. [附录 B — 解释键常量](#附录-b--解释键常量)
    - [日期搭时间](#日期搭时间)
    - [采收日期](#采收日期)
    - [GS1 公司前缀](#gs1-公司前缀)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [国家（ISO 3166）](#国家iso-3166)
    - [货币（ISO 4217）](#货币iso-4217)
    - [温度](#温度)
    - [性别（ISO 5218）](#性别iso-5218)
    - [水产物种（FAO）](#水产物种fao)
    - [北约库存编号（NSN）](#北约库存编号nsn)
    - [卷装产品](#卷装产品)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM 标识符（EID／ICCID）](#sim-标识符eidiccid)
    - [认证参考编号](#认证参考编号)
    - [GS1 UIC](#gs1-uic)
    - [婴儿出生次序](#婴儿出生次序)
    - [全球型号编号（GMN）](#全球型号编号gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [小数搭计量值](#小数搭计量值)
    - [地理坐标](#地理坐标)
    - [生产方式](#生产方式)
    - [AIDC 媒体类型](#aidc-媒体类型)
    - [总数当中个件数](#总数当中个件数)
    - [组件拆分](#组件拆分)
    - [杂项](#杂项)

---

## 概览

`GaiaParser` 是解析 GS1 应用标识符（AI）元素串个入口。伊接受扫描器个原始输出，好是下头随便哪一种形式，然后返回一个结构化个 `ParseResult`，里向包含所有已解析个 AI、验证错误，搭仔（可选个）供人阅读个解释：

- 纯 AI 元素串：`0109506000134352`
- 带 AIM 码制标识符前缀个元素串：`]C10109506000134352`
- GS1 Digital Link URI：`https://example.com/01/09506000134352`
- 上头随便哪一种，前头还好加一个 8 位数个关联 ID：`12345678~0109506000134352`

**入口类：** `tools.pantheum.gaia.GaiaParser`

> **头一趟碰着 Gaia？** 从 **[GaiaParser 快速入门](GaiaParser-QuickStart-WuChinese.md)** 开始——依赖项、头一趟解析，还有几个交关容易绊着人个地方，大约十分钟就看得完。迭本指南是完整参考。

> 至于反过来个操作——从 AI／值对 *构建* 格式正确个元素串搭 Digital Link URI——请看 **[GaiaBuilder — 开发者指南](GaiaBuilder-WuChinese.md)**。

---

## 关于 GS1 搭 General Specifications

**GS1** 是一个全球性个非营利组织，负责制定搭维护供应链标识搭数据交换个开放标准。伊个标准用辣零售、医疗、物流、餐饮服务搭交关别样行业，涵盖从消费品包装浪个产品条码，一直到药品剂量个序列号追踪。

迭个解析器所实现个一切，权威依据侪是 **GS1 General Specifications**——一份文件就定义仔下头全部：

- 所有应用标识符（AI）代码、伊拉个数据标题、格式搭验证规则
- 构建搭编码 AI 元素串个语法规则
- 条码码制个要求搭 AIM 码制标识符个分配
- 校验位搭校验字符个算法
- 两位数年份个判定（滑动窗口规则）
- Data Matrix、QR Code、GS1-128、GS1 DataBar 搭别样载体个规格

GS1 General Specifications 每年侪会更新。最新版本搭相关资源辣迭搭寻得着：

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA 实现仔 GS1 General Specifications 个 **第 26.0 版（已通过，2026 年 1 月）**。

GS1 Digital Link URI 由一份配套标准 **GS1 Digital Link: URI Syntax** 规范，伊定义仔解析器处理 Digital Link 输入辰光所应用个主标识键、键限定符排序，搭数据属性编码：

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA 实现仔 GS1 Digital Link: URI Syntax 标准个 **第 1.7.0 版（已通过，2026 年 8 月）**。

本文件通篇个章节引用侪是指 GS1 General Specifications（比方讲「Table 7-5」、「section 7.12」），只有 Digital Link 个章节编号（比方讲「§4.9」、「§4.12」）例外，搿眼是指 GS1 Digital Link: URI Syntax 标准。

---

## GS1 应用标识符

**GS1 应用标识符（AI）** 是一个短个数字前缀——两到四位数——用来标明紧跟辣伊后头搿段数据个意义搭格式。AI 辣 GS1 General Specifications 里向定义，涵盖各式各样个供应链数据：产品标识符、日期、数量、批号、序列号、计量值、URL 等等。

### AI 元素个结构

每个 AI 元素由两部分组成：

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

AI 代码一定是数字。数据值紧跟辣伊后头，代码搭值当中呒没任何分隔符。

### 固定长度 AI 对可变长度 AI

AI 分成两类：

| 类型 | 行为 | 例子 |
|---|---|---|
| **固定长度** | 字符数目固定，永远整段读完 | AI `01`（GTIN）——永远 14 位数 |
| **可变长度** | 从 1 个字符到某个上限；碰着 GS 分隔符或者输入末尾就结束 | AI `10`（批次／批号）——1 到 20 个字母数字字符 |

一个 AI 到底是固定还是可变，完全由伊辣 GS1 规范里向个定义决定——解析器勿会去猜。

### 多 AI 元素串

几个 AI 好串辣一道组成单条元素串。固定长度个 AI 好直接串连，因为解析器永远晓得要读几化个字符。可变长度个 AI 只要后头还有另一个 AI，就一定要用 **GS 字符**（ASCII `0x1D`，辣条码码制里向又叫 FNC1）收尾，噉解析器才晓得一个值辣啥地方完、下一个 AI 代码辣啥地方开始。

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

辣 Java 字符串字面量里向，用 Unicode 转义 `""` 来写 GS 字符。

### 常见个 AI

| AI | 数据标题 | 格式 | 示例值 |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6（YYMMDD） | `261231` |
| `17` | USE BY or EXPIRY | N6（YYMMDD） | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500`（＝ 1.500 kg） |
| `3922` | PRICE | N..15 | `91234`（＝ 912.34，单一货币区） |
| `710` | NHRN PZN | X..20 | `12345678` |

> 四位数个计量或者价格 AI，伊个 **第四位数** 编码仔隐含小数位个数目——`3103` 是以公斤计、带 3 位小数个净重（`001500` ＝ 1.500 kg），而 `3102` 就会把同一串数字读成 15.00 kg。上头搿个 `格式` 列显示个是 *数据* 个格式；每个 AI 完整个 `getFormatString()` 还包括 AI 本身（比方讲 `3103` 就是 `N4+N6`）。

### 供人阅读个解释（HRI）

惯用个人类可读形式，是辣每个 AI 代码个值前头用括号把代码括牢，元素当中隔一个空格：

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS 分隔符勿会辣 HRI 里向显示。`GS1AIObject.toHriString()` 就产生迭个格式。

### 四位数个 AI 代码

有眼 AI 用四位数而勿是两位。头两位数标明 AI 族系；第三搭／或第四位数承载额外个语义（比方讲计量类 AI 个隐含小数点位置）。解析器会自动从元素串解析出完整个 AI 代码——调用方永远侪是用完整代码（比方讲 `"3102"`，勿是光光 `"31"`）。

---

## 快速入门

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

> **GS 分隔符：** 辣多 AI 串里向，可变长度个 AI 一定要用 GS 字符（ASCII `0x1D`）分隔。辣 Java 字符串字面量里向用 `""`。

---

## 解析流程

### 前置阶段 — 输入修改器

假使 `ParseConfig` 带仔啥个 **输入修改器**，伊拉会辣所有物事之前跑——辣剥除关联前缀之前、辣检测载体之前、辣进入 GS1 流程之前。每个修改器侪替下一个修改器改写原始输入，而下头所有阶段处理个侪是搿条链个输出。

默认勿会配置任何修改器，所以除非侬自家选用，否则迭个前置阶段等于啥侪呒没做。请看 [输入修改器](#输入修改器)。

---

### 阶段 0 — 关联 ID

辣任何 GS1 处理之前，`GaiaParser` 会检查输入是勿是以一个可选个 **关联 ID 前缀** 开头：正好 8 个 ASCII 十进制数字，后头跟一个波浪号（`~`），比方讲 `12345678~`。

假使有迭个前缀，伊就会被剥除，并且以 `CorrelationInfo` 个形式存辣返回个 `ParseResult` 浪。后头所有阶段处理个侪是剥除仔前缀个有效载荷。假使呒没迭个前缀，输入就原封勿动噉传落去。

详情请看 [关联 ID](#关联-id)。

---

### 阶段 1 — 输入分流

剥除关联前缀之后，`GaiaParser` 会检查（已剥除个）输入是勿是以 **AIM 码制标识符** 开头：一个三字符前缀，形式为 `]` ＋ ASCII 字母 ＋ ASCII 数字（比方讲 GS1-128 是 `]C1`，GS1 DataMatrix 是 `]d2`，GS1 DataBar／GS1 Composite 是 `]e0`）。

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

假使搿个载体勿支持 GS1 AI（比方讲邮政条码），解析就立刻停落来，并发出一个 `GE-D002` 错误。

---

### 阶段 2 — 语法

无条件执行。由两个子步骤组成：

**2a. 分词（`AISyntaxParser`）**
- 用 GS1 前缀表（GS1 General Specifications Table 7-5）从头两个字符读出 AI 代码个长度。
- 固定长度个 AI 从输入读取准确个字节数目。
- 可变长度个 AI 一径读到 GS 字符或者输入末尾为止。
- 多组件个 AI 会把伊个值切成逐个组件个片段。

**2b. 结构验证（`SyntaxValidator`）**
- 检查有呒没重复个 AI（`GE-S004`）。
- 检查必需个 AI 依赖关系，比方讲 AI `02` 需要 AI `37`（`GE-S005`）。
- 检查互斥个 AI 配对（`GE-S006`）。

迭个阶段个错误，级别是 `SYNTAX_ERROR`（分词器）或者 `INTEGRITY_ERROR`（结构）。只要有 **任何** 错误——勿管是分词器还是结构个——整条流程就停落来，内容搭解释阶段侪跳过。

---

### 阶段 3 — 内容

只有辣阶段 2 呒没产生任何错误（分词器搭结构两方面侪呒没）个辰光才会跑。逐个元素个流程如下（每一步只有辣上一步呒没出错才会跑）：

| 步骤 | 验证器 | 错误代码 |
|---|---|---|
| 正则表达式检查 | `RegexValidator` | `GE-C001` |
| 组件字符集＋格式 | `ComponentValidator` | `GE-C005` ＋ 逐条件个格式代码（`GE-C054`–`GE-C115`） |
| 校验位／校验字符 | `CheckDigitCharacterValidator` | `GE-C003`、`GE-C004` |
| 自定义语义验证 | `ContentValidatorRegistry` | 逐条件个内容代码（`GE-C116`–`GE-C170`） |

迭个阶段个错误，级别是 `FORMAT_ERROR` 或者 `DATA_ERROR`，只有一个例外：GS1 键类
AI 浪个 GS1 公司前缀检查是建议性质，级别为 `WARNING`（请看
[错误参考](#错误参考)），所以一个认勿出个公司前缀，本身勿会叫结果变成无效。

---

### 阶段 4 — 解释

只有辣 `INTERPRETATION` 模式，而且呒没任何元素带牢前头阶段个错误个辰光才会跑。`InterpretationEngine` 会替每个元素加浪带标签个元数据：

- 日期重新格式化为 `dd/mm/yyyy`
- GTIN 校验位分解搭 GS1 公司前缀查找
- ISO 3166 国家名称
- ISO 4217 货币名称搭符号
- 已解码个小数金额
- HRI（供人阅读个解释）片段

结果会以 `GS1AIInterpretation` 条目个形式附加辣每个 `GS1AIObjectElement` 浪。

---

## 解析配置（`ParseConfig`）

`GaiaParser` 光光开放仔两个入口：

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` 用 **默认配置** 执行：`INTERPRETATION` 模式、小端序日期（`dd/mm/yyyy`）配 `/` 分隔符搭四位数年份，还有 **英文** 错误消息。想改动随便哪一项——包括解析模式——就用流式构建器做一个 `ParseConfig`，再用两个参数搿个重载版本。

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

所有选项个枚举类型侪辣 `GaiaConstants` 里向。

### 选项

| 构建器方法 | 枚举（`GaiaConstants`） | 默认 | 作用 |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | 流程深度——请看 [解析模式](#解析模式)。 |
| `language(...)`      | `Language`      | `ENGLISH`        | 错误消息、解释标签，**搭仔** AI 描述个语言。 |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | 日期组件次序：`LITTLE`（`dd/mm/yyyy`）、`MIDDLE`（`mm/dd/yyyy`）、`BIG`（`yyyy/mm/dd`）。 |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | 日期组件当中个字符：`SLASH`（`/`）、`HYPHEN`（`-`）、`PERIOD`（`.`）。 |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT`（`12`）或者 `THREE_LETTER`（`DEC`）。 |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT`（`2026`）或者 `TWO_DIGIT`（`26`）。 |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | 跳过结构浪个「需要」检查（`GE-S005`）。 |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | 跳过结构浪个「互斥」检查（`GE-S006`）。 |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` ／ 类名 | 呒没 | 辣解析之前改写原始输入个代码——两个 [内置修改器](#内置修改器) 加浪侬自家写个。请看 [输入修改器](#输入修改器)。 |

搿四个日期选项只影响解释增强器所产生个格式化日期字符串（辣 `INTERPRETATION` 模式下头）；伊拉勿会改变验证。构建器个值好勿填——任何呒没设置（或者传 `null`）个选项侪保持默认值。

### 本地化个消息搭标签

`language(...)` 替 **三种** 供人阅读个文本选择语言：错误消息、解释标签（每个 `GS1AIInterpretation` 个 `getLabel()`），还有 AI 描述（每个 `GS1AIObjectElement` 个 `getDescription()`）。

`GaiaConstants.Language` 定义仔 **35 种语言**，涵盖全世界用个人最多个语言：英语、法语、西班牙语、德语、意大利语、葡萄牙语、荷兰语、波兰语、俄语、乌克兰语、捷克语、瑞典语、汉语、日语、朝鲜语、阿拉伯语、印尼语、印地语、土耳其语、孟加拉语、乌尔都语、越南语、尼日利亚皮钦语、埃及阿拉伯语、马拉地语、泰卢固语、泰米尔语、粤语、吴语、他加禄语、波斯语、豪萨语、旁遮普语、爪哇语搭斯瓦希里语。

翻译状态（随附版本）：
- **解释标签** — 所有语言侪已翻译。
- **错误消息** — 所有语言侪已翻译。
- **AI 描述** — 除仔英语之外所有语言侪已翻译。英语勿是一份独立个目录：伊直接从 `gs1-application-identifiers.jsonld` 里向该 AI 条目个 `description` 字段读取，而所有 AI 描述最后侪会退回到迭搭。

尼日利亚皮钦语（`NIGERIAN_PIDGIN`）是一种以英语为基础个克里奥尔语，伊辣解释标签搭错误消息浪特为沿用英文文本。AI 描述就是迭个例外里向个例外：伊拉翻译成仔真正个皮钦语措辞，而勿是沿用英文，因为 AI 描述目录是独立于标签／消息目录制作个。机器翻译辣投入生产环境之前，应该寻母语使用者审阅。

任何辣某种语言目录里向寻勿着个消息、标签或者描述，侪会退回英文。从右到左书写个语言（阿拉伯语、乌尔都语、埃及阿拉伯语、波斯语）以字符串形式正确存储；至于哪能以 RTL 呈现，搿是显示层个责任。

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

解释标签也是一样噉本地化（值勿变——光光标签变）：

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI 描述也是一样噉本地化（只有 `getTitle()`，比方讲 `"GTIN"`，勿会本地化）：

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

## 输入修改器

**输入修改器** 是辣 Gaia 解析之前改写原始输入字符串个代码。修改器是为仔应付搿眼进来辰光已经走样个输入——比方讲扫描器把 GS 分隔符换成仔可打印字符、中间件把有效载荷包仔一层供应商专用个前缀、主机系统把啥侪转成大写。与其辣每个调用点侪要把每条字符串先清理一遍（噉总归有一处会出眼细微差错），倒勿如把规范化辣 `ParseConfig` 浪登记一趟，然后把应用个事体交拨解析器。

修改器辣 `GaiaParser.parse(...)` 最开头就跑——辣剥除关联 ID 之前、辣识别 AIM 码制标识符之前、辣进入 GS1 流程之前。从迭搭往后个一切，看得着个光光是改写过个字符串。包括两个[内置修改器](#内置修改器)辣内，**默认啥侪勿会配置**——每个 `ParseConfig` 侪由侬自家拣。

**接口：** `tools.pantheum.gaia.modifier.ModifierInterface`

### 内置修改器

核心 jar 辣 `tools.pantheum.gaia.modifier.custom` 下头附带两个修改器。GS1 有效载荷最常见个两种走样方式，伊拉正好侪处理得着——被当成数据个印刷 HRI 括号，搭多余个空格——所以一般情况侪勿用自家写类：

| 类 | `getName()` | 伊做啥 |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | 剥除每个 AI 周围个 HRI 括号（`(01)…(10)…`），并且还原搿眼括号本身所隐含个 FNC1 分隔符。 |
| `ModifierRemoveSpaces` | `Remove Space Characters` | 从 AI 元素串里向移除所有空格（`0x20`）。 |

搿两个侪是呒没特殊地位个普通 `ModifierInterface` 实现——伊拉个登记、排序、报告搭失败方式，搭侬自家写个完全一样：

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

两个侪是无状态搭线程安全个，所以同一个实例好大家共用；而且为仔方便以配置文件为本个部署，两个侪好用完整类名来指名（请看 [登记修改器](#登记修改器)）。

#### `ModifierRemoveAIBrackets`

GS1 个供人阅读解释会把每个 AI 用括号打印出来——`(01)09521234543213(10)ABC123`——纯粹是一个排印惯例。任何配置成传送 HRI 个扫描器或者中间件，侪会把搿眼括号当成数据一样送落去，而分词器根本勿晓得拿伊拉哪能办。

光剥除括号只做仔一半事体。辣 HRI 里向，*下一个* AI 个开括号 `(` 就是前一个值结束个唯一标记，所以辣括号形式下头，可变长度个 AI 根本勿需要 FNC1。假使就噉硬剥括号，搿个边界就呒没脱：

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

正因为噉，迭个修改器会 **辣每一个前一个 AI 属可变长度个边界浪重新插入 FNC1**，准确噉还原搿眼括号本来所编码个转折：

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

长度是从解析器自家个 `AiDefinitionRegistry` 查出来个，所以伊处理得着每一个可变长度个 AI，而勿是靠一份写死辣代码里向个清单。有三种情况是特为勿动个：本身已经以 FNC1 收尾个值（两种惯例侪送过来个来源勿会拿到第二个分隔符）、勿属于任何已知 AI 个括号代码（未知个 AI 讲勿出自家个长度），还有字符串里向最后搿个 AI。

迭个改写是 **幂等** 个——拿伊自家个输出再跑一遍，啥侪勿会变——所以辣只有部分输入带括号个混合流程浪用也交关安全。

> **限制。** `(` 搭 `)` 本身就是合法个 GS1 数据字符，而迭搭用个模式光光是 `\((\d{2,4})\)`。假使某个值正好含有一个用括号括牢个两到四位数字，伊个括号也会一道被剥脱。只好把伊用辣采用 HRI 括号惯例个来源浪，勿要用辣真个含有括号值个来源浪。

#### `ModifierRemoveSpaces`

有眼扫描器、中间件搭标签打印系统，会辣本来正常个元素串里向加插多余个空格——为仔填满一个固定宽度个字段、为仔分开好读个组别，或者为仔把一个长值折行。分词器会把每个空格侪当成数据，叫伊身处搿个值走样；而假使是可变长度个 AI，伊后头个所有物事侪会跟牢移位。

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

只有 ASCII `0x20` 会被移除。别样空白字符原封勿动——比方讲制表符本身就勿辣 GS1 可编码字符集之内，所以解析器会把伊报成 `GE-S008`，而勿是闷声勿响吞脱伊。

> **限制。** 空格（`0x20`）是 GS1 不变字符集个一部分，所以一个批次／批号或者客户零件编号，合法噉也好含有空格。修改器分勿清啥地方是多余空格、啥地方是真实空格；只好把伊用辣侬确晓得勿会辣 AI 值里向用空格个来源浪。

#### 前缀会被跳过，勿会被改写

修改器跑个辰光，解析器还呒没剥除过任何物事，所以原始输入可能还带牢关联 ID、AIM 码制标识符搭 ECI 指示符。两个内置修改器侪是用解析器自家搿套 `CorrelationIdParser` 搭 `DataCarrierParser` 逻辑来寻出 AI 元素串个起点，从搿搭才开始改写，然后把结果搭 **原封勿动** 个前缀重新接转来：

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

至于搿眼值会被填充到 GTIN-14 个 EAN/UPC 载体（`isRequiresGtinPadding()`），伊拉会被整个跳过——伊拉个有效载荷是呒没任何 AI 结构个原始数字条码值，所以辣搿搭括号搭空格侪勿可能有啥意义。

#### 次序：先空格，后括号

两个侪用个辰光，要 **先登记 `ModifierRemoveSpaces`**。括号个匹配是对位置敏感个：夹仔空格个 `( 01 )` 勿会匹配着 `\((\d{2,4})\)`，于是括号会留落来，而伊拉所隐含个分隔符就永远勿会转来。

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### 写一个修改器

假使两个内置修改器侪勿适用，就自家写一个——搿个接口光光有一个方法。

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

假使搿个改写要看解析配置，就改成覆写两个参数搿个版本：

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

契约如下：

| 规则 | 详情 |
|---|---|
| 无状态搭线程安全 | 每个类只缓存一个实例，辣所有解析当中共用。 |
| 公开个无参构造函数 | 只有辣用类名来指名修改器个辰光才需要。 |
| 要处理 `null` 搭空输入 | 解析器勿会辣整条链跑之前先替侬过滤搿眼。 |
| 返回 `null` 就是「呒没改动」 | 前一个值会一径传落去。修改器勿适用个辰光，原样返回 `input`。 |
| 宁可原样返回，也勿要抛异常 | 抛异常个修改器会叫整个解析中止——请看 [失败处理](#修改器个失败处理)。 |
| `getName()` | 覆写伊就好控制 `ModifierInfo` 浪所报个名字；默认是简单类名。 |

### 登记修改器

修改器按照侬加进去个次序执行，每一个侪收到上一个个输出。好用实例、用完整类名，或者用两者混合个清单来登记：

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

[内置修改器](#内置修改器)个指名方式搭侬自家搿眼一模一样——**永远侪用完整名称**。伊拉呒没短名，也呒没别名查找；`ModifierRegistry` 对每一个修改器，勿管是随附还是自定义，侪是用完整类名来解析。

名称由 `ModifierRegistry` 解析；伊会用每个类个无参构造函数把伊实例化一趟，然后替后头每一份指名同一个类个配置缓存牢同一个实例。搿个解析是辣 **创建配置个辰光** 发生个，所以寻勿着个名称、呒没实现 `ModifierInterface` 个类，或者无法实例化个类，侪会立刻辣搿搭抛出 `IllegalArgumentException`——而勿是辣解析个辰光闷声勿响出事体。至于用反射造勿出来个修改器（比方讲伊带牢一个注入个依赖项），好预先登记，噉就照样好用名称来指名：

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### 看修改器做过啥

有配置修改器个辰光，`ParseResult.getPayload()` 显示个是 **改写过** 之后个输入。原来搿个会保留辣 `ModifierInfo` 浪：

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` 报个是每个修改器个 `getName()`，伊默认是简单类名，勿过两个内置修改器侪覆写仔伊——所以搿两个组成个链会显示展示名称，而勿是类名：

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

呒没配置任何修改器个辰光，`getModifierInfo()` 返回 `null`。假使修改器跑过，勿过每一个侪原样返回仔输入，噉搿个信息对象还辣，勿过 `isModified()` 是 `false`——`getAppliedModifiers()` 里向光光包含真个改动过输入个修改器。

### 修改器个失败处理

抛异常个修改器会叫整个解析中止。搿个异常会被包成一个 `GaiaModifierException`，里向指明是啥个修改器出事体，而结果会带牢一个 `GE-I001` 内部错误，消息里向也有同一个名字；`getPayload()` 显示个是呒没修改过个输入。解析 **勿会** 带牢一条改写仔一半个字符串继续跑落去，搿是特为个——一个闷声勿响失败脱个规范化步骤，会产生搿眼看起来合法、其实是从错误输入解析出来个结果。

---

## 解析模式

每个模式个名字，侪是取自伊所执行个最深搿个[流程阶段](#解析流程)；前头个每一个阶段照样侪会跑。

| 模式 | 跑到啥地方 | 答得出啥问题 |
|---|---|---|
| `DATA_CARRIER` | 阶段 1（输入分流） | 迭个是啥个码制送过来个？ |
| `SYNTAX` | 阶段 2（语法） | AI 代码搭长度个格式对勿对？ |
| `CONTENT` | 阶段 3（内容） | 搿眼值是勿是合法个 GS1 数据？ |
| `INTERPRETATION` | 阶段 4（解释） | 搿眼值是啥意思？ |

### DATA_CARRIER 模式

辣阶段 1 之后就停。伊会验证 AIM 码制标识符搭识别码制，勿过勿会进入 AI 解析流程。适合辣勿想承受完整验证成本个情况下识别码制搭分流。

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

**啥辰光用：** 侬个应用程序辣决定哪能处理有效载荷之前，需要晓得条码是啥类型个辰光——比方讲把 1D 搭 2D 码制送到勿同个处理器。做搿种分流个辰光，用带类型个 [`DataCarrierType`](#datacarrierentry-搭-datacarriertype)（`getDataCarrier().getDataCarrierType()`），勿要辣 `getName()` 浪匹配字符串。

---

### SYNTAX 模式

辣阶段 2 之后就停。适合辣勿想承受内容验证成本个情况下做结构筛查。

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

**啥辰光用：** 侬想辣投入完整验证之前，先确认 AI 代码搭数据长度对勿对个辰光；或者做大批量扫描，而内容错误交关少见个辰光。

---

### CONTENT 模式

辣阶段 3 之后就停。

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

> 大部分 AI 侪勿好单独存在：AI `10`（BATCH/LOT）、`17`（USE BY or EXPIRY）搭
> `21`（SERIAL）——每一个侪 *需要* 辣同一条元素串里向有一个像 AI `01` 噉个标识键；
> 所以把上头例子里向个 GTIN 拿脱，还呒没到内容验证，就已经会辣阶段 2
> 以 `GE-S005` 失败。假使搿眼片段是特为呒没伊拉个伴随 AI 个，就辣 `ParseConfig` 浪
> 设置 `skipRequiresCheck(true)` 来解析伊拉。

**啥辰光用：** 侬辣把一个扫到个值用到业务流程之前，需要晓得伊是勿是完全符合 GS1 规范，勿过又勿想承受解释增强个成本个辰光。

---

### INTERPRETATION 模式（默认）

跑足整条流程直到阶段 4。调用呒没模式参数个 `parse(String)` 个辰光，用个就是迭个默认。伊光光会替顺利通过内容验证个元素做增强。

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

**示例输出：**
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

**货币金额个例子（AI 3932 — 带 ISO 货币代码个价格）：**
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

**啥辰光用：** 辣造展示层、标签检查工具，或者任何需要把 AI 值以人性化方式拆开个 UI 个辰光。

---

## 关联 ID

有眼工作流程会辣原始 GS1 输入前头加一个专用个 8 位数关联标识符，噉就好把扫描事件搭某个会话或者交易对转来。格式是噉个：

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

`~`（波浪号）是分隔符。伊 **勿是** GS1 内容个一部分——辣任何 GS1 解析开始之前，伊就已经被剥脱。

### 检测规则

输入以正好 8 个 ASCII 十进制数字（`0`–`9`）开头，并且紧跟一个 `~` 个辰光，就会检测着迭个前缀。假使第 9 个字符勿是 `~`，或者头 8 个字符里向有随便哪一个勿是数字，噉搿个输入就会被当成呒没关联前缀个纯 GS1 内容。

### 拿关联 ID

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

### 搭 AIM 码制标识符一道用

关联前缀好出现辣 AIM 码制标识符之前。解析器会透明噉处理搿种情况：

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

**GS1 Digital Link** 直接把一个或者几个 AI 值编码进 HTTP(S) URL 个结构里向，叫实体产品有一个好辣网浪解析个标识符。GAIA 替 **未压缩** 个 URI 实现仔 *GS1 Digital Link Standard: URI Syntax*（第 1.7.0 版）。

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` 会自动识别 Digital Link URI——任何以 `http://` 或者 `https://` 开头个输入，侪会被送到 `GS1DLParser`，伊会跑搭元素串流程一模一样个内容搭解释阶段。

### URI 结构搭 AI 个角色

Digital Link URI 里向每个 AI 侪担当三种角色当中个一种，通过每个 `GS1AIObjectElement` 个 `getDigitalLinkAIType()`（`GS1Constants.DigitalLinkAIType`）拿得着：

| 角色 | 位置 | 例子 |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | 路径浪头一对 `/ai/value`（§4.3） | `/01/09506000134352` |
| `KEY_QUALIFIER` | 后头个路径对，按主键所定个次序（§4.9） | `/10/ABC`、`/21/SER` |
| `DATA_ATTRIBUTE` | 键名全数字个查询参数（§4.10） | `?17=271231` |

所强制执行个结构规则（`DLPathRules`）：
- 路径里向正好要有 **一个** 主标识键；额外个键一定要编码成查询数据属性。
- 键限定符一定要是主键所允许个，而且要按规定次序出现。可选个限定符好省脱，勿过凡是 *有出现* 个，侪照样要遵守固定次序——请看 [限定符次序](#限定符次序)。
- 主键之前好有任意个自定义路径段（比方讲 `/products/au/01/...`）；用 `getDigitalLinkInfo().getCustomPathStem()` 拿转来。
- 非数字个查询键（`linkType`、`context`，搭 `23P` 之类个扩展参数）会被忽略；全数字个键一定要是标记仔 `validAsDataAttribute` 个合法 AI。
- 百分号编码个值字符会被解码；AI `(03)` 搭 `(8014)` 勿许用。

主键搭伊拉所允许个限定符序列是 **由数据驱动** 个，来自 AI 定义里向个 `gs1DigitalLinkPrimaryKey` 标志搭 `gs1DigitalLinkQualifiers` 属性，而勿是写死辣代码里向。

任何结构浪个违规，或者非 URL 个输入，侪会产生一个 Digital Link 结构错误（`GE-L001`–`GE-L014`，每种情况一个代码）。就算有结构错误，拆开个 URL 元数据（`scheme`、`domain`、`path`、`customPathStem`、`query`，搭 `java.net.URL`）照样好通过 `getDigitalLinkInfo()` 拿着。

### 限定符次序

对每个主键，`gs1DigitalLinkQualifiers` 会列出一个或者几个 **有次序** 个限定符序列。辣一个序列里向，用方括号括牢个 AI 是 **可选** 个，呒没括号个 AI 就是 **必需** 个——搭 §4.9 ABNF 个 `[cpv-comp]` 记法一致。同一个主键个几个序列，是互相排斥个替代方案。

比方讲 GTIN（`01`）就定义仔两个序列：

| 路径 | 序列 | 意义 |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV、LOT、SER——每个侪可选，勿过次序固定就是噉 |
| upui-path | `235` | TPX（必需）；GTIN ＋ TPX ＝ UPUI |

所以 `/01/09506000134352/10/LOT-ABC/21/SER` 是合法个（LOT 辣 SER 前头，CPV 省脱仔），`/01/.../21/SER/10/LOT-ABC` 就会 **被拒绝**（次序弄错仔），而 `/01/09506000134352/235/2ABC456` 就是 upui-path。次序检查是一种保序个子序列匹配，所以可选个 AI 好跳过，勿过永远勿好调换次序。

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

## 处理结果

### ParseResult

`GaiaParser.parse()` 返回个最上层结果。

| 方法 | 返回 | 描述 |
|---|---|---|
| `isValid()` | `boolean` | 假使任何级别侪呒没错误就是 `true`。警告勿影响有效性。`getAiObject()` 是 `null` 个辰光永远是 `true`。 |
| `getPayload()` | `String` | 剥除关联前缀之后——搭经过任何[输入修改器](#输入修改器)改写之后——个输入字符串。 |
| `getPayloadContent()` | `String` | 剥除仔 AIM 码制标识符搭 ECI 前缀个有效载荷。 |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`、`GS1_DIGITAL_LINK`、`DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL`（被判定为非 GS1 而拒绝个数据载体，比方讲 Code 39 个 `]A0` 载体），或者 `UNABLE_TO_DETERMINE_CONTENT`（`aiObject` 是 `null` 个辰光，比方讲 `DATA_CARRIER` 模式）。 |
| `getRequestedParseMode()` | `ParseMode` | 所配置个流程深度（`ParseConfig.getRequestedParseMode()`）。 |
| `getAchievedParseMode()` | `ParseMode` | 迭趟解析实际到个最深阶段——看下头。 |
| `isParseComplete()` | `boolean` | 假使解析到仔所要求个深度就是 `true`（`achieved == requested`）。搭 `isValid()` 呒没关系。 |
| `getAiObject()` | `GS1AIObject` | 所有已解析个 AI。辣 `DATA_CARRIER` 模式是 `null`。 |
| `getErrors()` | `List<GaiaError>` | 所有非 WARNING 个错误（对象层 ＋ 所有元素层）。 |
| `getWarnings()` | `List<GaiaError>` | 所有 WARNING 建议（对象层 ＋ 所有元素层）。 |
| `hasWarnings()` | `boolean` | 假使有发出任何 WARNING 建议就是 `true`。 |
| `getIssues()` | `List<GaiaError>` | 错误搭警告合辣一道。 |
| `hasDataCarrier()` | `boolean` | 假使识别着 AIM 码制标识符就是 `true`。 |
| `getDataCarrier()` | `DataCarrierEntry` | 码制元数据；假使呒没识别着载体就是 `null`。 |
| `hasEci()` | `boolean` | 假使从有效载荷剥除仔一个 ECI 指示符就是 `true`。 |
| `getEci()` | `EciEntry` | ECI 编码元数据，或者 `null`。 |
| `hasCorrelationId()` | `boolean` | 假使原始输入里向有 `DDDDDDDD~` 关联前缀就是 `true`。 |
| `getCorrelationInfo()` | `CorrelationInfo` | 抽出来个关联 ID；假使呒没就是 `null`。 |
| `isInputModified()` | `boolean` | 假使有[输入修改器](#输入修改器)改动过输入就是 `true`。 |
| `getModifierInfo()` | `ModifierInfo` | 修改器链做过啥——`getOriginalInput()`、`getModifiedInput()`、`getAppliedModifiers()`。假使呒没配置修改器就是 `null`。 |
| `getTiming()` | `ProcessingTiming` | 迭趟解析个实际耗时——`getStartTime()`（`Instant`）、`getProcessingTime()`（`Duration`）、`getProcessingTimeMillis()`（`long`）、`getCompletionTime()`。假使勿是由 `GaiaParser` 产生就是 `null`。 |
| `getVersion()` | `String` | 产生迭个结果个库版本。 |

#### 所要求个解析模式对实际达到个解析模式

整条流程是跑 **SYNTAX → CONTENT → INTERPRETATION** 搿条阶梯，一碰着错误就提前停落来，所以实际 *达到* 个模式好比 *所要求* 个模式浅。`getAchievedParseMode()` 会报伊跑到几远：

| 所要求 | 出啥事体 | 实际达到 | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | 一个 **语法／结构** 错误叫解析辣分词之后停落来 | `SYNTAX` | `false` |
| `INTERPRETATION` | 一个 **内容** 错误（格式／校验位有问题）挡牢仔增强 | `CONTENT` | `false` |
| `CONTENT` | 内容阶段永远侪会跑完（错误只标注，勿会致命） | `CONTENT` | `true` |
| 随便哪一个（输入干净） | 流程到仔所要求个深度 | ＝ 所要求 | `true` |
| `DATA_CARRIER` | 载体已验证；呒没解析 AI 内容 | `DATA_CARRIER` | `true` |
| 随便哪一个 | 数据载体辣 AI 解析之前就被拒绝（比方讲非 GS1 个 `]A0` 载体） | `SYNTAX` | `false` |

`isParseComplete()` 搭 `isValid()` 呒没关系：一个校验位错脱个 GTIN，用 `CONTENT` 解析是 **完成仔** 个（伊跑完仔内容阶段），勿过是 **无效** 个（校验位失败）。想问「搿条流程有呒没跑到我要求个深度？」就用 `isParseComplete()`，想问「搿眼数据格式对勿对？」就用 `isValid()`。

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

已解析个 AI 元素集合。

| 方法 | 描述 |
|---|---|
| `getAis()` | 按输入次序排个所有 `GS1AIObjectElement` 实例。 |
| `get(String aiCode)` | 符合所给 AI 代码个头一个元素，或者 `null`。 |
| `contains(String aiCode)` | 假使存在该代码个 AI 就是 `true`。 |
| `size()` | 已解析个 AI 数目。 |
| `isValid()` | 假使呒没对象层错误而且呒没任何元素有错误就是 `true`。 |
| `toHriString()` | HRI 字符串，比方讲 `(01)09506000134352 (17)261231`。 |
| `toElementString()` | 原始元素串——呒没括号，每个可变长度元素后头有 FNC1——比方讲 `010950600013435210LOT-ABC<GS>17271231`。假使 `isValid()` 是 `false` 就返回 `null`。 |
| `getContentType()` | `hasDigitalLink()` 为真个辰光是 `GS1_DIGITAL_LINK`，否则是 `GS1_APPLICATION_IDENTIFIERS`。 |
| `hasDigitalLink()` | 假使输入是一个带主标识键个 GS1 Digital Link URI 就是 `true`。一个格式正确勿过呒没主键个 URL，照样拿得着 `getDigitalLinkInfo()`，勿过迭搭返回 `false`。 |
| `getCanonicalDigitalLink()` | 辣 `https://id.gs1.org` 浪个规范 GS1 Digital Link URI（§4.12）——主键搭限定符做路径段，数据属性做查询参数并按 AI 键排序——假使呒没主键就是 `null`。 |
| `getDigitalLinkInfo()` | URI 拆解个元数据（`getUri()`、`getUrl()`、`scheme`、`domain`、`path`、`getCustomPathStem()`、`query`）；假使勿是 Digital Link 就是 `null`。 |
| `getAllErrors()` | 对象层 ＋ 所有元素错误（非 WARNING）。 |
| `getAllWarnings()` | 对象层 ＋ 所有元素警告。 |
| `getAllIssues()` | 全部合辣一道。 |

---

### GS1AIObjectElement

单独一个已解析个 AI 实例。

| 方法 | 描述 |
|---|---|
| `getAi()` | AI 代码，比方讲 `"01"`、`"3102"`。 |
| `getTitle()` | GS1 数据标题，比方讲 `"GTIN"`、`"BATCH/LOT"`。 |
| `getDescription()` | 该 AI 完整个 GS1 描述，**已按解析语言本地化**（比方讲英语是 `"Global Trade Item Number (GTIN)"`）。假使还呒没翻译就退回 AI 定义里向个英文文本。 |
| `getFormatString()` | 涵盖该 AI *搭仔* 伊数据个格式描述符，比方讲 AI `01` 是 `"N2+N14"`、AI `10` 是 `"N2+X..20"`、AI `3932` 是 `"N4+N3+N..15"`。 |
| `getValue()` | 从元素串抽出来个原始数据值。 |
| `isFixedLength()` | 假使该 AI 个数据长度固定就是 `true`。 |
| `getPosition()` | 辣原始输入里向从零开始算个字符偏移量。 |
| `getGS1ComponentValues()` | 逐个组件个值切片（用辣多组件 AI）。 |
| `getErrors()` | 元素层个非 WARNING 错误。 |
| `getWarnings()` | 元素层个 WARNING 建议。 |
| `getIssues()` | 元素层个错误搭警告合辣一道。 |
| `hasErrors()` | 假使附有任何非 WARNING 错误就是 `true`。 |
| `hasWarnings()` | 假使附有任何 WARNING 建议就是 `true`。 |
| `getInterpretations()` | `GS1AIInterpretation` 条目（辣 INTERPRETATION 模式才会填充）。 |
| `getInterpretation(String type)` | 符合所给 `GS1Constants_Enricher` 类型键个头一个解释，或者 `null`。 |
| `getDigitalLinkAIType()` | 该元素个 Digital Link 角色（`PRIMARY_IDENTIFICATION_KEY`、`KEY_QUALIFIER`、`DATA_ATTRIBUTE`）；元素串输入就是 `null`。 |
| `hasDigitalLinkAIType()` | 假使已指派 Digital Link 角色就是 `true`。 |

---

### GaiaError

一个不可变个验证错误或者建议。

| 方法 | 描述 |
|---|---|
| `getId()` | 目录标识符，比方讲 `"GE-C003"`。 |
| `getLevel()` | `SYNTAX_ERROR`、`INTEGRITY_ERROR`、`FORMAT_ERROR`、`DATA_ERROR`，或者 `WARNING`。 |
| `getStage()` | `DATA_CARRIER`、`DIGITAL_LINK`、`SYNTAX`、`CONTENT`，或者 `INTERNAL`。 |
| `getCode()` | 机器可读个短代码。 |
| `getAi()` | 引起该错误个 AI 代码；对象层错误就是 `null`。 |
| `getMessage()` | 供人阅读、已填入值个消息。 |
| `getPosition()` | 辣原始输入里向从零开始算个字符偏移量。 |

---

### GS1AIInterpretation

单独一个带标签个解释片段，辣 `INTERPRETATION` 模式下头附加辣 `GS1AIObjectElement` 浪。

| 方法 | 描述 |
|---|---|
| `getType()` | 机器可读个类型键，比方讲 `"DATE_VALUE"`、`"GS1_COMPANY_PREFIX"`。辣所有语言侪保持勿变。 |
| `getLabel()` | 供人阅读个标签，**已按解析语言本地化**（比方讲英语是 `"Date"`／`"GS1 company prefix"`）。 |
| `getValue()` | 抽取／增强后个值，比方讲 `"31/12/2026"`、`"9506000"`。勿会本地化。 |

---

### DataCarrierEntry 搭 DataCarrierType

输入带有 AIM 码制标识符个辰光，`ParseResult.getDataCarrier()` 会返回一个 `DataCarrierEntry`，描述承载仔搿眼数据个搿个符号。迭个条目是所匹配着个 AIM 码制标识符个具体注册记录；`DataCarrierType` 就是伊所属个编译期枚举。

#### DataCarrierEntry

一个已识别 AIM 码制标识符个元数据（`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`）。

| 方法 | 描述 |
|---|---|
| `getAimCodeId()` | 匹配着个 AIM 码制标识符，比方讲 `"]C1"`。 |
| `getName()` | 该具体符号个人类可读名称，比方讲 `"GS1-128 / ISBT 128"`、`"EAN-8"`。 |
| `getDescription()` | 该载体比较详细个描述。 |
| `getType()` | 该载体个结构类型，以字符串表示（搭 `getDataCarrierType().getCategory()` 一致）。 |
| `getStandard()` | 码制标准，有记录个闲话。 |
| `getDataCarrierType()` | 迭个条目所对应个、带类型个 `DataCarrierType`——做程序分流个辰光应该用迭个。 |
| `isGs1Capable()` | 假使该载体好承载 GS1 数据（AI 元素串搭／或 Digital Link）就是 `true`。 |
| `isGs1AICapable()` | 假使该载体好承载 GS1 AI 元素串就是 `true`。 |
| `isGs1DigitalLinkCapable()` | 假使该载体好承载 GS1 Digital Link URI 就是 `true`。 |
| `isEciCapable()` | 假使该载体支持 ECI 指示符就是 `true`。 |
| `isRequiresGtinPadding()` | 对于数字值会辣 AI 解析之前被填充到 GTIN-14 个 EAN/UPC/ITF 载体，就是 `true`。 |

#### DataCarrierType

一个编译期个数据载体类型枚举，以 ISO/IEC 15424 所指派个 AIM 码制标识符为键（`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`）。`]` 后头搿个字符（就是 *码制字符*）决定族系；大部分族系侪对应一个涵盖所有修饰符个常量（`ITF` 涵盖 `]I0`–`]I2`；`EAN_UPC` 涵盖 EAN-13、UPC-A、UPC-E 搭 EAN-8）。凡是 GS1 替 AI 数据保留仔修饰符个，该变体侪自成一个常量——`GS1_128`（`]C1`）、`GS1_DATA_MATRIX`（`]d2`）、`GS1_QR_CODE`（`]Q3`）、`GS1_DOT_CODE`（`]J1`）——搭伊拉个普通对应物勿一样。呒没 AIM 码制标识符，或者伊指个是一个未知载体个辰光，类型就是 `UNKNOWN`。

| 方法 | 描述 |
|---|---|
| `getCategory()` | 概括个 `GaiaConstants.DataCarrierTypeCategory`：`LINEAR`、`STACKED_LINEAR`、`TWO_D`、`POSTAL`、`OCR`，或者 `OTHER`。 |
| `getCodeChar()` | 标明族系个 AIM 码制字符，比方讲 QR Code 是 `"Q"`；`UNKNOWN` 就是 `null`。 |
| `getDisplayName()` | *类型* 个人类可读名称（好比 `DataCarrierEntry.getName()` 阔——比方讲 `"EAN-13 / UPC-A / UPC-E / EAN-8"` 对 `"EAN-8"`）。 |
| `isGs1DataCarrier()` | 对于永远代表 GS1 AI 数据个常量就是 `true`：四个 GS1 保留变体（`GS1_128`、`GS1_DATA_MATRIX`、`GS1_QR_CODE`、`GS1_DOT_CODE`），再加 `GS1_DATABAR`，后者本质浪就是 GS1，因为每个 `]e` 修饰符侪是 GS1 DataBar。迭个比 `DataCarrierEntry.isGs1AICapable()` 窄——一个普通个 `QR_CODE` 一样好承载 GS1 AI 数据。 |
| `static forAimCodeId(String)` | 直接从 AIM 码制标识符解析出类型（`"]Q3"` → `GS1_QR_CODE`；`"]Q9"` → `QR_CODE`）；假使标识符缺失、格式勿对或者认勿出，就返回 `UNKNOWN`。 |

按类型而勿是按名称来分流——比方讲把线性（Code-128）搭 2D（QR／Data Matrix）符号分开：

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` 光光涵盖矩阵搭点阵符号；堆叠式线性载体（`PDF417`、
`CODE_16K`、`CODABLOCK`、`CODE_49`）属于 `STACKED_LINEAR`，虽然伊拉通常侪被人叫做
「2D」条码。想把两者当成同一组来处理——比方讲要决定是勿是需要影像式扫描器而勿是激光扫描器
——就检查是勿是属于搿两个类别当中个一个。

> 解析类型需要扫描结果里向有 AIM 码制标识符；呒没伊，`getDataCarrier()` 就是 `null`，类型也是 `UNKNOWN`。请把扫描器配置成会传送 AIM 码制标识符前缀。

---

## 错误参考

| 代码 | 级别 | 阶段 | 意义 |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | 未知个 AI 前缀——无法判定数据长度 |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | 输入忒短，读勿出一个完整个 AI 代码 |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | 值被截断——字符数少过该 AI 所需 |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | 元素串里向有重复个应用标识符 |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | 缺少必需个 AI 依赖项 |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | 互斥个 AI 配对——两个勿好同时出现个 AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | 非预期个分词失败 |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | 元素串里向有 GS1 可编码字符集之外个字符 |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | 可变长度 AI 后头缺少必需个 FNC1 分隔符 |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | 超出所有组件上限之后还有剩余数据 |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | 辣字符串中间，固定长度 AI 后头出现 FNC1 分隔符 |
| `GE-W002` | WARNING | SYNTAX | 元素串末尾有多余个 FNC1（光光是建议） |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI 个结构违规——每种情况一个代码（URI 格式勿对、scheme、host、限定符次序、禁用 AI、呒没主键（`GE-L013`）、几个主键（`GE-L014`）……） |
| `GE-C001` | FORMAT_ERROR | CONTENT | 值勿符合该 AI 个正则表达式模式 |
| `GE-C003` | DATA_ERROR | CONTENT | 校验位验证失败 |
| `GE-C004` | DATA_ERROR | CONTENT | 校验字符对验证失败 |
| `GE-C005` | FORMAT_ERROR | CONTENT | 组件值含有所允许字符集之外个字符 |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | 组件格式失败——每个验证条件一个代码（请看 `componentformat/`） |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | 自定义语义验证失败——每个验证条件一个代码（请看 `content/validator/`）。**例外：** 下头列个 14 项 GS1 公司前缀检查个级别是 `WARNING`，而 `GE-C168`（认勿出个 ISO 3166-1 数字国家代码）个级别是 `FORMAT_ERROR`。 |
| GS1 公司前缀检查 | WARNING | CONTENT | 辣 GS1 键类 AI 浪，搿个键并勿是以一个认得出个 GS1 公司前缀开头——`GE-C122`（CPID）、`GE-C129`（GCN）、`GE-C131`（GDTI）、`GE-C132`（GIAI）、`GE-C133`（GINC）、`GE-C135`（GLN）、`GE-C137`（GMN）、`GE-C140`（GRAI）、`GE-C142`（GSIN）、`GE-C144`（GSRN）、`GE-C146`（GTIN）、`GE-C148`（HIDRI）、`GE-C153`（ITIP）、`GE-C165`（SSCC）。光光是建议——勿影响有效性。 |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040（IMEI）／8041（IMEI2）个 IMEI 校验位（Luhn）失败 |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042（ESIM）个 EID 校验位（Luhn）失败 |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | 认勿出个 AIM 码制标识符 |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | 已识别着载体，勿过伊既勿支持 GS1 AI 元素串，也勿支持 Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | 非预期个内部错误 |

> **消息呈现浪个已知缺陷。** 目录个模板用 MessageFormat 风格个双单引号
> （`''{value}''`）来引牢填入个值，勿过 `ErrorRegistry` 是用普通个 `String.replace`
> 做填入，所以搿重双引号会一径带到 `getMessage()` 里向——本指南所引个消息文本写
> `value '09506000134351'` 个地方，侬现在实际会看着 `value ''09506000134351''`。
> 全部 35 种语言目录里向每一条会引用值个消息侪受影响。勿要去解析错误消息；
> 请辣 `getId()`／`getCode()` 浪做匹配。

---

## 线程安全

`GaiaParser` 一经构造就是线程安全个。同一个实例好辣几条线程当中共用搭并发使用。建议个做法是辣应用程序启动个辰光构造一个实例，然后重复使用：

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` 是不可变个，同样好安全共用。库唯一呒没办法替侬保证个线程安全责任，就是辣[输入修改器](#输入修改器)浪：每个修改器只缓存一个实例，辣所有并发解析当中共用，所以搿眼实现一定要是无状态个。

---

## 附录 A — AI 字符串常量

`GS1Constants_AICodes`（辣 `tools.pantheum.gaia.gs1.constants` 包里向）替 GAIA 所识别个每一个应用标识符侪声明仔一个 `String` 常量。请用搿眼常量，勿要把原始 AI 代码字符串写死辣代码里向：

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

每个常量存个是该 AI 代码个字符串形式（比方讲 `AI_01_GTIN = "01"`）。

### 标识搭序列化

| AI | 常量 | 描述 |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | 系列货运包装箱代码 (SSCC). |
| `01` | `AI_01_GTIN` | 全球贸易项目编号 (GTIN). |
| `02` | `AI_02_CONTENT` | 内含贸易项目个全球贸易项目编号 (GTIN). |
| `03` | `AI_03_MTO_GTIN` | 定制订造 (MtO) 贸易项目识别码 (GTIN). |
| `10` | `AI_10_BATCH_LOT` | 批次或批号. |
| `20` | `AI_20_VARIANT` | 内部产品变体. |
| `21` | `AI_21_SERIAL` | 序列号. |
| `22` | `AI_22_CPV` | 消费者产品变体. |
| `235` | `AI_235_TPX` | 第三方控制个全球贸易项目编号序列化扩展 (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | 制造商指定个附加产品识别码. |
| `241` | `AI_241_CUST_PART_NO` | 客户部件编号. |
| `242` | `AI_242_MTO_VARIANT` | 定制订造变化编号. |
| `243` | `AI_243_PCN` | 包装组件编号. |
| `250` | `AI_250_SECONDARY_SERIAL` | 次要序列号. |
| `251` | `AI_251_REF_TO_SOURCE` | 来源实体参考. |
| `253` | `AI_253_GDTI` | 全球文件类型识别码 (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | 全球位置码 (GLN) 扩展部分. |
| `255` | `AI_255_GCN` | 全球优惠券编号 (GCN). |
| `30` | `AI_30_VAR_COUNT` | 可变物品数量（可变计量贸易项目）. |
| `37` | `AI_37_COUNT` | 物流单元内所含贸易项目或贸易项目件数个数量. |

### 日期搭时间

| AI | 常量 | 描述 |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | 生产日期 (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | 到期日 (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | 包装日期 (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | 最佳食用日期 (YYMMDD). |
| `16` | `AI_16_SELL_BY` | 销售期限 (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | 有效期至 (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | 最早送货日期辰光 (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | 最迟送货日期辰光 (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | 发行日期 (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | 有效期至日期搭辰光 (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | 首次冷冻日期 (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | 采收日期 (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | 测试期限 (YYMMDD[hhmm]). |

### 数量搭计量 — 可变计量（公制）

四位数个 AI 族系 `310n`–`369n` 编码可变计量个数量。第三位数决定计量类型；**第四位数**（`n`，0–5）就是隐含小数位个数目——比方讲 `AI_3102_NET_WEIGHT_KG` 就是以公斤计、带 2 位小数个净重。

| 族系 | 常量模式（`n` ＝ 小数位数字） | 描述 |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | 净重，公斤（可变计量贸易项目）. |
| `311n` | `AI_311n_LENGTH_M` | 长度或者第一维度，米（可变计量贸易项目）. |
| `312n` | `AI_312n_WIDTH_M` | 宽度、直径或者第二维度，米（可变计量贸易项目）. |
| `313n` | `AI_313n_HEIGHT_M` | 深度、厚度、高度或者第三维度，米（可变计量贸易项目）. |
| `314n` | `AI_314n_AREA_M` | 面积，平方米（可变计量贸易项目）. |
| `315n` | `AI_315n_NET_VOLUME_L` | 净体积，升（可变计量贸易项目）. |
| `316n` | `AI_316n_NET_VOLUME_M` | 净体积，立方米（可变计量贸易项目）. |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | 物流重量，公斤. |
| `331n` | `AI_331n_LENGTH_M_LOG` | 长度或者第一维度，米. |
| `332n` | `AI_332n_WIDTH_M_LOG` | 宽度、直径或者第二维度，米. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | 深度、厚度、高度或者第三维度，米. |
| `334n` | `AI_334n_AREA_M_LOG` | 面积，平方米. |
| `335n` | `AI_335n_VOLUME_L_LOG` | 物流体积，升. |
| `336n` | `AI_336n_VOLUME_M_LOG` | 物流体积，立方米. |
| `337n` | `AI_337n_KG_PER_M` | 每平方米公斤数. |

### 数量搭计量 — 可变计量（英制／美制）

| 族系 | 常量模式（`n` ＝ 小数位数字） | 描述 |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | 净重，磅（可变计量贸易项目）. |
| `321n` | `AI_321n_LENGTH_IN` | 长度或者第一维度，英寸（可变计量贸易项目）. |
| `322n` | `AI_322n_LENGTH_FT` | 长度或者第一维度，英尺（可变计量贸易项目）. |
| `323n` | `AI_323n_LENGTH_YD` | 长度或者第一维度，码（可变计量贸易项目）. |
| `324n` | `AI_324n_WIDTH_IN` | 宽度、直径或者第二维度，英寸（可变计量贸易项目）. |
| `325n` | `AI_325n_WIDTH_FT` | 宽度、直径或者第二维度，英尺（可变计量贸易项目）. |
| `326n` | `AI_326n_WIDTH_YD` | 宽度、直径或者第二维度，码（可变计量贸易项目）. |
| `327n` | `AI_327n_HEIGHT_IN` | 深度、厚度、高度或者第三维度，英寸（可变计量贸易项目）. |
| `328n` | `AI_328n_HEIGHT_FT` | 深度、厚度、高度或者第三维度，英尺（可变计量贸易项目）. |
| `329n` | `AI_329n_HEIGHT_YD` | 深度、厚度、高度或者第三维度，码（可变计量贸易项目）. |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | 物流重量，磅. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | 长度或者第一维度，英寸. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | 长度或者第一维度，英尺. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | 长度或者第一维度，码. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | 宽度、直径或者第二维度，英寸. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | 宽度、直径或者第二维度，英尺. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | 宽度、直径或者第二维度，码. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | 深度、厚度、高度或者第三维度，英寸. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | 深度、厚度、高度或者第三维度，英尺. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | 深度、厚度、高度或者第三维度，码. |
| `350n` | `AI_350n_AREA_IN` | 面积，平方英寸（可变计量贸易项目）. |
| `351n` | `AI_351n_AREA_FT` | 面积，平方英尺（可变计量贸易项目）. |
| `352n` | `AI_352n_AREA_YD` | 面积，平方码（可变计量贸易项目）. |
| `353n` | `AI_353n_AREA_IN_LOG` | 面积，平方英寸. |
| `354n` | `AI_354n_AREA_FT_LOG` | 面积，平方英尺. |
| `355n` | `AI_355n_AREA_YD_LOG` | 面积，平方码. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | 净重，金衡盎司（可变计量贸易项目）. |
| `357n` | `AI_357n_NET_VOLUME_OZ` | 净重（或体积），盎司（可变计量贸易项目）. |
| `360n` | `AI_360n_NET_VOLUME_QT` | 净体积，夸脱（可变计量贸易项目）. |
| `361n` | `AI_361n_NET_VOLUME_GAL` | 净体积，加仑（美制）（可变计量贸易项目）. |
| `362n` | `AI_362n_VOLUME_QT_LOG` | 物流体积，夸脱. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | 物流体积，加仑（美制）. |
| `364n` | `AI_364n_NET_VOLUME_IN` | 净体积，立方英寸（可变计量贸易项目）. |
| `365n` | `AI_365n_NET_VOLUME_FT` | 净体积，立方英尺（可变计量贸易项目）. |
| `366n` | `AI_366n_NET_VOLUME_YD` | 净体积，立方码（可变计量贸易项目）. |
| `367n` | `AI_367n_VOLUME_IN_LOG` | 物流体积，立方英寸. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | 物流体积，立方英尺. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | 物流体积，立方码. |

### 价格搭货币金额

第四位数（`n`）编码隐含小数位个数目。伊个容许范围每个族系侪勿一样——请看 `n` 列。

| 族系 | 常量模式（`n` ＝ 小数位数字） | `n` | 描述 |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | 适用应付金额或优惠券面值，当地货币. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | 适用应付金额（附ISO货币代码）. |
| `392n` | `AI_392n_PRICE` | 0–9 | 适用应付金额，单一货币地区（可变计量贸易项目）. |
| `393n` | `AI_393n_PRICE` | 0–9 | 适用应付金额（附ISO货币代码）（可变计量贸易项目）. |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | 优惠券折扣百分比. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | 每计量单位应付金额，单一货币地区（可变计量贸易项目）. |

### 位置搭运输

| AI | 常量 | 描述 |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | 客户采购订单编号. |
| `401` | `AI_401_GINC` | 全球托运识别编号 (GINC). |
| `402` | `AI_402_GSIN` | 全球装运识别编号 (GSIN). |
| `403` | `AI_403_ROUTE` | 路由代码. |
| `410` | `AI_410_SHIP_TO_LOC` | 收货方 / 送货方全球位置码 (GLN). |
| `411` | `AI_411_BILL_TO` | 收费方 / 发票方全球位置码 (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | 采购来源全球位置码 (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | 代运 / 代送 - 转交全球位置码 (GLN). |
| `414` | `AI_414_LOC_NO` | 实体地点识别 - 全球位置码 (GLN). |
| `415` | `AI_415_PAY_TO` | 开票方个全球位置码 (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | 生产或服务地点个全球位置码 (GLN). |
| `417` | `AI_417_PARTY` | 相关方全球位置码 (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | 收货方 / 送货方邮政编码（单一邮政机构范围内）. |
| `421` | `AI_421_SHIP_TO_POST` | 收货方 / 送货方邮政编码（附ISO国家代码）. |
| `422` | `AI_422_ORIGIN` | 贸易项目原产国. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | 初步加工国家. |
| `424` | `AI_424_COUNTRY_PROCESS` | 加工国家. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | 拆解国家. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | 涵盖整个加工链个国家. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | 原产地国家细分区域. |
| `4300` | `AI_4300_SHIP_TO_COMP` | 收货方 / 送货方公司名称. |
| `4301` | `AI_4301_SHIP_TO_NAME` | 收货方 / 送货方联系人. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | 收货方 / 送货方地址第一行. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | 收货方 / 送货方地址第二行. |
| `4304` | `AI_4304_SHIP_TO_SUB` | 收货方 / 送货方郊区. |
| `4305` | `AI_4305_SHIP_TO_LOC` | 收货方 / 送货方地区. |
| `4306` | `AI_4306_SHIP_TO_REG` | 收货方 / 送货方地域. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | 收货方 / 送货方国家代码. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | 收货方 / 送货方电话号码. |
| `4309` | `AI_4309_SHIP_TO_GEO` | 收货方 / 送货方地理位置. |
| `4310` | `AI_4310_RTN_TO_COMP` | 退回公司名称. |
| `4311` | `AI_4311_RTN_TO_NAME` | 退回联系人. |
| `4312` | `AI_4312_RTN_TO_ADD1` | 退回地址第一行. |
| `4313` | `AI_4313_RTN_TO_ADD2` | 退回地址第二行. |
| `4314` | `AI_4314_RTN_TO_SUB` | 退回郊区. |
| `4315` | `AI_4315_RTN_TO_LOC` | 退回地区. |
| `4316` | `AI_4316_RTN_TO_REG` | 退回地域. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | 退回国家代码. |
| `4318` | `AI_4318_RTN_TO_POST` | 退回邮政编码. |
| `4319` | `AI_4319_RTN_TO_PHONE` | 退回电话号码. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | 服务代码描述. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | 危险品标记. |
| `4322` | `AI_4322_AUTH_LEAVE` | 放行授权. |
| `4323` | `AI_4323_SIG_REQUIRED` | 需要签名标记. |
| `4330` | `AI_4330_MAX_TEMP_F` | 最高温度（华氏，以百分之一度表示）. |
| `4331` | `AI_4331_MAX_TEMP_C` | 最高温度（摄氏，以百分之一度表示）. |
| `4332` | `AI_4332_MIN_TEMP_F` | 最低温度（华氏，以百分之一度表示）. |
| `4333` | `AI_4333_MIN_TEMP_C` | 最低温度（摄氏，以百分之一度表示）. |

### 产品属性搭可追溯性

| AI | 常量 | 描述 |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | 北约库存编号 (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE肉类胴体搭分割部位分类. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | 有效效力. |
| `7005` | `AI_7005_CATCH_AREA` | 捕捞区域. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | 渔业用途物种. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | 捕鱼工具类型. |
| `7010` | `AI_7010_PROD_METHOD` | 生产方法. |
| `7020` | `AI_7020_REFURB_LOT` | 翻新批次识别码. |
| `7021` | `AI_7021_FUNC_STAT` | 功能状态. |
| `7022` | `AI_7022_REV_STAT` | 修订状态. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | 装配件个全球单项资产识别码 (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | 加工者编号，附三位数 ISO 国家代码（10 个位）。. |
| `7040` | `AI_7040_UIC_EXT` | 带延伸码1搭进口商索引个GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT货运单位类型. |

### 国家医疗保健报销编号（NHRN）

| AI | 常量 | 描述 |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | 国家医疗保健报销编号 (NHRN) - 德国 PZN. |
| `711` | `AI_711_NHRN_CIP` | 国家医疗保健报销编号 (NHRN) - 法国 CIP. |
| `712` | `AI_712_NHRN_CN` | 国家医疗保健报销编号 (NHRN) - 西班牙 CN. |
| `713` | `AI_713_NHRN_DRN` | 国家医疗保健报销编号 (NHRN) - 巴西 DRN. |
| `714` | `AI_714_NHRN_AIM` | 国家医疗保健报销编号 (NHRN) - 葡萄牙 AIM. |
| `715` | `AI_715_NHRN_NDC` | 国家医疗保健报销编号 (NHRN) - 美利坚合众国 NDC. |
| `716` | `AI_716_NHRN_AIC` | 国家医疗保健报销编号 (NHRN) - 意大利 AIC. |
| `717` | `AI_717_NHRN_SRN` | 国家医疗保健报销编号 (NHRN) - 哥斯达黎加卫生注册编号. |

### 医疗保健、GMN、HIDRI、CPID、个人数据

| AI | 常量 | 描述 |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | 认证参考编号（10 个位）。. |
| `7240` | `AI_7240_PROTOCOL` | 协议识别码. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC 媒体类型. |
| `7242` | `AI_7242_VCN` | 版本控制编号 (VCN). |
| `7250` | `AI_7250_DOB` | 出生日期 (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | 出生日期搭辰光 (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | 生理性别. |
| `7253` | `AI_7253_FAMILY_NAME` | 个人姓氏. |
| `7254` | `AI_7254_GIVEN_NAME` | 个人名字. |
| `7255` | `AI_7255_SUFFIX` | 个人姓名后缀. |
| `7256` | `AI_7256_FULL_NAME` | 个人全名. |
| `7257` | `AI_7257_PERSON_ADDR` | 个人地址. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | 婴儿出生顺序. |
| `7259` | `AI_7259_BABY` | 婴儿姓氏. |
| `8001` | `AI_8001_DIMENSIONS` | 卷装产品（宽度、长度、芯径、方向、接头）. |
| `8002` | `AI_8002_CMT_NO` | 移动电话识别码. |
| `8003` | `AI_8003_GRAI` | 全球可回收资产识别码 (GRAI). |
| `8004` | `AI_8004_GIAI` | 全球单项资产识别码 (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | 每计量单位价格. |
| `8006` | `AI_8006_ITIP` | 单件贸易项目识别 (ITIP). |
| `8007` | `AI_8007_IBAN` | 国际银行账户号码 (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | 生产日期搭辰光 (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | 光学可读传感器指示器. |
| `8010` | `AI_8010_CPID` | 组件/部件识别码 (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | 组件/部件识别码序列号 (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | 软件版本. |
| `8013` | `AI_8013_GMN` | 全球型号编号 (GMN). |
| `8014` | `AI_8014_MUDI` | 高度个性化设备注册识别码 (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | 用来识别提供服务个机构搭服务提供者之间关系个全球服务关系编号 (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | 用来识别提供服务个机构搭服务接收者之间关系个全球服务关系编号 (GSRN). |
| `8019` | `AI_8019_SRIN` | 服务关系实例编号 (SRIN). |
| `8020` | `AI_8020_REF_NO` | 付款单参考编号. |
| `8026` | `AI_8026_ITIP_CONTENT` | 物流单元内所含贸易项目件数识别 (ITIP). |
| `8030` | `AI_8030_DIGSIG` | 数字签名 (DigSig). |
| `8040` | `AI_8040_IMEI` | 国际移动设备识别码 (IMEI). |
| `8041` | `AI_8041_IMEI2` | 国际移动设备识别码2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | 内嵌SIM卡号码. |
| `8043` | `AI_8043_PSIM` | 实体SIM卡号码. |
| `8110` | `AI_8110` | 用于北美洲个优惠券代码识别. |
| `8111` | `AI_8111_POINTS` | 优惠券个忠诚积分. |
| `8112` | `AI_8112` | 用于北美洲个正面优惠文件优惠券代码识别. |
| `8200` | `AI_8200_PRODUCT_URL` | 扩展包装URL. |

### 内部／公司用途

| AI | 常量 | 描述 |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | 贸易伙伴之间互相同意个信息. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | 公司内部信息（9 个位）。. |

---

## 附录 B — 解释键常量

`GaiaParser.parse()` 用 `ParseMode.INTERPRETATION` 调用个辰光，每个 `GS1AIObjectElement` 侪可能带牢一串由领域专用增强器所产生个 `GS1AIInterpretation` 对象。请用 `GS1Constants_Enricher`（辣 `tools.pantheum.gaia.gs1.constants` 包里向）个常量做键，来查找具体个解释值：

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

展示标签 **勿是** 常量——伊拉住辣 `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` 下头个本地化目录里向，以类型常量为键。`GS1AIInterpretation.getLabel()` 返回解析语言个标签（请看 [本地化个消息搭标签](#本地化个消息搭标签)），假使某份目录缺仔搿个键就退回英文。下头「展示标签」一列列出个，就是吴语目录里向个文本；类型键本身辣所有语言侪保持勿变，所以请辣键浪做匹配，千万勿要用标签。

### 日期搭时间

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `DATE_VALUE` | 日期 | 日期类 AI（11–17、7003、7006、7011 等） |
| `DATE_FORMAT` | 日期格式 | 日期类 AI |
| `TIME_VALUE` | 时间 | 带时间个 AI（7003、7011、8008 等） |
| `TIME_FORMAT` | 时间格式 | 带时间个 AI |
| `DATETIME_VALUE` | 日期时间 | 日期＋时间类 AI |
| `DATETIME_FORMAT` | 日期时间格式 | 日期＋时间类 AI |

### 采收日期

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | 收获开始日期 | AI 7007 |
| `HARVEST_END_DATE` | 收获结束日期 | AI 7007（可选个范围末尾） |
| `HARVEST_DATE_RANGE` | 收获日期范围 | AI 7007 |

### GS1 公司前缀

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 公司前缀 | GTIN／GLN／SSCC 类 AI |
| `GS1_MEMBER_CODE` | GS1 成员代码 | GTIN／GLN／SSCC 类 AI |
| `GS1_MEMBER_NAME` | GS1 成员组织 | GTIN／GLN／SSCC 类 AI |

### GTIN

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN 类型 | AI 01、02 |
| `GTIN_NATIVE` | GTIN | AI 01、02 |
| `PACKAGING_LEVEL` | 包装层级 | AI 01 |
| `GTIN_CHECK_DIGIT` | 校验位 | AI 01、02 |

### SSCC

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | 扩展位 | AI 00 |
| `SSCC_SERIAL_REFERENCE` | 序列参考号 | AI 00 |
| `SSCC_CHECK_DIGIT` | 校验位 | AI 00 |

### 国家（ISO 3166）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | 国家代码（数字） | 单一国家类 AI（422、424–426、4307、4317、421、7030–7039） |
| `COUNTRY_CODE_ALPHA2` | 国家代码（字母-2） | Alpha-2 国家类 AI |
| `COUNTRY_NAME` | 国家名称 | 单一国家类 AI |
| `COUNTRY_LIST` | 国家 | AI 423 — 所有名称连辣一道，比方讲 `Australia, New Zealand` |

AI 423（初次加工国家）最多好承载五个国家，所以伊会 **每个国家发出一对带编号个键**
——`COUNTRY_CODE_NUMERIC_1`、`COUNTRY_NAME_1`、
`COUNTRY_CODE_NUMERIC_2`、`COUNTRY_NAME_2`、……——后头再跟一个 `COUNTRY_LIST`
汇总。好用 `COUNTRY_CODE_NUMERIC_PREFIX`／`COUNTRY_NAME_PREFIX`
常量加浪从 1 开始个索引来拼搿眼键，或者干脆跑一遍 `getInterpretations()`；至于呒没后缀个
`COUNTRY_CODE_NUMERIC`／`COUNTRY_NAME` 键，AI 423 是 **勿会** 发出个。

### 货币（ISO 4217）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | 货币代码 | 带货币个金额类 AI（391n、393n） |
| `CURRENCY_ALPHA` | 货币字母代码 | 带货币个金额类 AI |
| `CURRENCY_NAME` | 货币名称 | 带货币个金额类 AI |

### 温度

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `TEMPERATURE` | 温度 | AI 4330–4333 |
| `TEMPERATURE_UNIT` | 温度单位 | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | 温度（已格式化） | AI 4330–4333 |

### 性别（ISO 5218）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `SEX_CODE` | 性别代码 | AI 7252 |
| `SEX_DESCRIPTION` | 性别说明 | AI 7252 |

### 水产物种（FAO）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `SPECIES_CODE` | 物种代码 | AI 7008 |
| `SPECIES_SCIENTIFIC` | 学名 | AI 7008 |
| `SPECIES_ENGLISH` | 通用名称 | AI 7008 |
| `SPECIES_FAMILY` | 科 | AI 7008 |
| `SPECIES_ORDER` | 目 | AI 7008 |

### 北约库存编号（NSN）

| 键常量 | 展示标签 | 从啥地方产生 |
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

### 卷装产品

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | 卷宽 (mm) | AI 8001 |
| `ROLL_LENGTH` | 卷长 (m) | AI 8001 |
| `CORE_DIAMETER` | 芯径 (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | 卷绕方向代码 | AI 8001 |
| `WINDING_DIRECTION` | 卷绕方向 | AI 8001 |
| `SPLICES` | 拼接数 | AI 8001 |

### IBAN

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | 国家代码 | AI 8007 |
| `IBAN_COUNTRY_NAME` | 国家 | AI 8007 |
| `IBAN_CHECK_DIGITS` | 校验位 | AI 8007 |
| `IBAN_CHECK_VALID` | 校验 | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040、8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040、8041 |
| `IMEI_SERIAL` | 序列号 | AI 8040、8041 |
| `IMEI_CHECK_DIGIT` | 校验位 | AI 8040、8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040、8041 |
| `IMEI_RBI_NAME` | 分配机构 | AI 8040、8041 |

搿 15 位数拆成 `[ TAC (8) ][ 序列号 (6) ][ Luhn 校验位 (1) ]`，而
RBI 就是 TAC 开头个 2 位数——所以 `IMEI_RBI` 是 `IMEI_TAC` 个前缀，勿是一段
独立个区间。`IMEI_FORMATTED` 呈现个是 GSMA 标准个展示分组
`AA-BBBBBB-CCCCCC-D`（比方讲 `49-015420-323751-8`），伊辣 RBI 边界搭浪切开 TAC；
至于老早个 `6-2-6-1` 分组，就是辣已停用个 Final Assembly Code 起点搭浪切搿种，
勿会发出。

`IMEI_RBI_NAME` 通过 `ImeiRbiData` 把 RBI 解析成分配机构个名称，而伊
**永远排辣最后，并且只有辣该代码有列辣搿搭个辰光才会出现**。搿张表涵盖三组：

- **现在还辣分配个** — `01` CTIA/PTCRB、`35` TÜV SÜD BABT、`86` TAF，再加 `99`
  Global Hexadecimal Administrator 搭 `98`（保留）。
- **测试范围** — `00` 搭 `02`–`09`，标示个是测试用 IMEI 而勿是真实分配。
  好用 `ImeiRbiData.isTestCode(code)` 查询。
- **已经勿分配个** — 历史浪个机构，比方讲 `49`（BZT/BAPT，德国）、`44`
  （BABT，英国）或者 `91`（MSAI，印度）。好用 `ImeiRbiData.isNoLongerAllocating(code)` 查询。
  带牢搿眼代码个设备交关普通，而且还辣服役当中；停脱个光光是新个分配，
  所以迭个纯粹是报告性质个信息，绝对勿是有效性信号。

呒没 `IMEI_RBI_NAME` 个意思是「迭个 RBI 勿辣阿拉搿张表里向」，**勿是**「IMEI 无效」：
搿张表是从一份已公布个 RBI 清单编制而成，勿是直接从 GSMA 来个，所以伊有可能
落后于新任命个机构。千万勿要从伊缺席推出任何验证结论；
RBI 勿是一个校验字符。任何跑过解释清单个代码，侪一定要容得落伊勿辣，
而勿是靠位置索引来拿值。

### SIM 标识符（EID／ICCID）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042、8043 |
| `SIM_MII_NAME` | 行业类别 | AI 8042 |
| `EID_BODY` | EID 主体 | AI 8042 |
| `EID_CHECK_DIGIT` | 校验位 | AI 8042 |
| `ICCID_BODY` | ICCID 主体 | AI 8043 |
| `ICCID_EXTENSION` | 扩展 | AI 8043 |

`SIM_MII` 承载个是开头 **两** 位数（`89`），就是 ITU-T E.118 指派拨
电信业搿一对。ISO/IEC 7812 本身把 MII 定义成 **只有第一位数**，所以
`SIM_MII_NAME` 是从开头搿个 `8` 经 `Iso7812Data` 解析出类别——得出
「Healthcare, telecommunications and other future industry assignments」。所以对一个格式正确个
EID 来讲，迭个值是固定勿变个；伊是为仔对应转标准而报告，并勿是用来做
区分。`Iso7812Data.nameForCode(digit)` 收一个单个数字，
`nameForIdentifier(prefix)` 就收一个比较长个前缀，然后读伊开头搿位数。

`SIM_MII_NAME` 光光由 `EidEnricher`（AI 8042）发出。`IccidEnricher`（AI 8043）
会提供 `SIM_MII`，勿过呒没搿个类别。

### 认证参考编号

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | 序列号 | AI 7230–7239 |
| `CERT_SCHEME_CODE` | 认证方案代码 | AI 7230–7239 |
| `CERT_SCHEME_NAME` | 认证方案 | AI 7230–7239 |
| `CERT_REFERENCE` | 认证参考号 | AI 7230–7239 |

### GS1 UIC

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC 代码 | AI 7040 |
| `UIC_EXTENSION_1` | 扩展 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | 进口商索引 | AI 7040 |

### 婴儿出生次序

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | 出生位置 | AI 7258 |
| `BIRTH_TOTAL` | 出生总数 | AI 7258 |
| `BIRTH_SEQUENCE` | 出生顺序 | AI 7258 |

### 全球型号编号（GMN）

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | 型号参考 | AI 8013 |
| `GMN_CHECK_PAIR` | 校验对 | AI 8013 |

### HIDRI

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | 设备参考号 | AI 8014 |
| `HIDRI_CHECK_PAIR` | 校验对 | AI 8014 |

### CPID

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | 组件与部件参考号 | AI 8010–8011 |

### 小数搭计量值

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | 小数值 | 带隐含小数位个数字类 AI（31xx–36xx） |
| `DECIMAL_AMOUNT` | 金额 | 价格类 AI（390n–395n） |
| `DECIMAL_PERCENTAGE` | 百分比 | AI 394n |
| `DECIMAL_PLACES` | 小数位数 | 搭 `DECIMAL_VALUE`／`DECIMAL_AMOUNT`／`DECIMAL_PERCENTAGE` 一道出现 |
| `PERCENTAGE_FORMAT` | 百分比格式 | AI 394n |
| `ISO_UNIT_CODE` | ISO 单位代码 | 计量类 AI |
| `ISO_UNIT_NAME` | ISO 单位名称 | 计量类 AI |
| `MONETARY_AMOUNT` | 货币金额 | 价格类 AI |
| `MONETARY_AMOUNT_DISPLAY` | 货币金额（已格式化） | 价格类 AI |

### 地理坐标

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `LATITUDE` | 纬度 | AI 4309 |
| `LONGITUDE` | 经度 | AI 4309 |
| `GEO_COORDINATES` | 地理坐标 | AI 4309 |
| `LATITUDE_DMS` | 纬度 (DMS) | AI 4309 |
| `LONGITUDE_DMS` | 经度 (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | 地理坐标 (DMS) | AI 4309 |

### 生产方式

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | 生产方法代码 | AI 7010 |
| `PRODUCTION_METHOD` | 生产方法 | AI 7010 |

### AIDC 媒体类型

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC 媒介类型代码 | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC 媒介类型 | AI 7241 |

### 总数当中个件数

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | 件号 | AI 8006 |
| `PIECE_TOTAL` | 总件数 | AI 8006 |
| `PIECE_OF_TOTAL` | 总数中的件 | AI 8006 |

### 组件拆分

搿眼键并勿是由 Java 增强器发出，而是由 `content/ai-content.json` 里向个声明式组件拆分
所发出个——伊拉把一个复合 AI 值个具名部分呈现出来。搭迭个附录里向别样所有键勿一样，
伊拉 **辣 `GS1Constants_Enricher` 里向呒没常量**：
请匹配字面字符串，或者从 `GS1AIInterpretation.getType()` 读出类型。

| 类型键 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | 校验位 | AI 253、255、402、410–417、8003、8017、8018 |
| `SERIAL_NUMBER` | 序列号 | AI 253、255、8003 |
| `POSTAL_CODE` | 邮政编码 | AI 421 |
| `PROCESSOR_ID` | 加工者标识 | AI 7030–7039 |

请注意迭搭个 `CHECK_DIGIT` 是通用个组件拆分键，搭上头列个增强器专用
`GTIN_CHECK_DIGIT`、`SSCC_CHECK_DIGIT`、`IMEI_CHECK_DIGIT` 搭 `EID_CHECK_DIGIT`
键是勿一样个。

### 杂项

| 键常量 | 展示标签 | 从啥地方产生 |
|--------------|---------------|-------------|
| `FLAG_VALUE` | 值 | 布尔／标志类 AI（4321–4323） |
| `DECODED_TEXT` | 解码文本 | 自由文本类 AI |
