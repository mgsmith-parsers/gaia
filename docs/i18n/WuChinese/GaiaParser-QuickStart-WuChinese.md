# GaiaParser — 快速入门

大约十分钟，就把一个 GS1 条码有效载荷解析成结构化、已验证、供人阅读个数据。迭个是短路径；
**[GaiaParser 开发者指南](GaiaParser-WuChinese.md)** 才是完整参考，而
**[GaiaBuilder](GaiaBuilder-WuChinese.md)** 就涵盖反过来个方向（构建元素串搭 Digital Link
URI）。

## 目录

1. [把 Gaia 加进侬个项目](#1-把-gaia-加进侬个项目)
2. [解析眼物事](#2-解析眼物事)
3. [读结果](#3-读结果)
4. [处理失败个解析](#4-处理失败个解析)
5. [两桩会咬着侬个事体](#5-两桩会咬着侬个事体)
6. [扫描器前缀搭 Digital Link 自动搞定](#6-扫描器前缀搭-digital-link-自动搞定)
7. [做少眼事体：解析模式](#7-做少眼事体解析模式)
8. [改语言搭日期格式](#8-改语言搭日期格式)
9. [清理龌龊个输入](#9-清理龌龊个输入)
10. [接下来去啥地方](#10-接下来去啥地方)

---

## 1. 把 Gaia 加进侬个项目

Gaia 呒没发布到 Maven Central，所以要把核心构建一趟，然后安装到侬个本地仓库：

```bash
cd gaia && mvn install
```

接下来就声明依赖：

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

侬要写个依赖清单就搿眼多。搿个 jar 是薄个，所以 Gaia 唯一搿个编译范围个依赖
——`com.fasterxml.jackson.core:jackson-databind`——会经传递依赖带落来；假使侬个
构建已经钉牢仔某个 Jackson 版本，噉就以侬搿个为准，Gaia 跟牢用。
Gaia 以 **Java 11** 为目标，同一个 jar 辣后头每一个 JVM 浪侪好原封勿动噉跑。

> 刚刚开始上手个辰光，跳过核心个测试套件（`mvn install -DskipTests`）好把几分钟
> 变成几秒。

---

## 2. 解析眼物事

一个类，零配置：

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

`parse(String)` 会跑 **足** 整条流程：语法、内容验证搭解释。
迭个是对个默认——假使侬量出来有理由收窄伊，迟眼再收窄。

---

## 3. 读结果

已解析个 AI 存辣 `ParseResult.getAiObject()` 里向。要拿某一个个辰光，用代码而勿要用位置：

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

每个元素侪带牢一份 **解释** 清单——就是搿眼原始数字背后解码出来个意义，
由解释阶段所产生：

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` 是已本地化个，是用来显示个。想辣代码里向 *读* 一个值，就改用伊
稳定个类型键来查：

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

勿同个 AI 会产生勿同个键——GTIN 拨得出伊个公司前缀、GTIN 类型搭校验位；
价格就拨得出货币搭小数金额。完整清单辣
[附录 B](GaiaParser-WuChinese.md#附录-b--解释键常量)，而搿眼常量就住辣
`GS1Constants_Enricher` 里向。勿是每个 AI 侪有解释：一个自由文本个批次／批号
根本呒没物事好推导，所以伊搿张清单是空个。

---

## 4. 处理失败个解析

一个无效个有效载荷是正常结果，勿是异常——`parse` 对牢坏个 GS1 数据永远勿会抛异常：

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**辣 `getId()` 浪分支，千万勿要用消息。** 搿眼消息是已本地化个，伊拉个措辞
勿是契约——而且伊拉现在还带牢一个已知个引号缺陷（上头搿个双 `''`），
迭点辣 [错误参考](GaiaParser-WuChinese.md#错误参考) 有讲。

两条勿同个问题，两个勿同个方法：

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

一个阶段一失败，解析就停牢勿再往下跑，所以校验位错脱就是讲侬会拿到验证
错误，勿过呒没解释。

### 警告勿会叫一个结果变成无效

有眼检查是建议性质个。一个认勿出个 GS1 公司前缀会被报出来，勿过搿个有效载荷
辣结构浪照样是健全个：

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

想两样侪拿个辰光就用 `getIssues()`。假使侬个工作流程一定要拒绝未知前缀，就要明确噉
检查 `getWarnings()`——`isValid()` 勿会替侬做搿桩事体。

---

## 5. 两桩会咬着侬个事体

### GS 分隔符，还有为啥省脱伊比出错还要坏

一个可变长度个 AI 会一径跑到碰着 **GS 字符**（ASCII `0x1D`，辣条码码制里向叫
FNC1）或者字符串末尾为止。伊后头还跟牢另一个 AI 个辰光，迭个分隔符是
强制个：

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

省脱伊，侬 **勿会** 收到错误——侬会收到一个交关笃定勿过是错个答案：

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` 是 `X..20`，所以伊完全合法噉吞脱 `LOT-ABC21SN-98765`，而解析器根本
呒没办法晓得搿个勿是侬想要个结果。下游呒没啥物事救得转，所以要辣源头
就把分隔符弄对：把扫描器个字节以 **ISO-8859-1** 读取，噉 `0x1D` 就保得牢；
辣 Java 字符串字面量里向就写 `""`。固定长度个 AI（`01`、`17`、`3103`）勿用分隔符——
解析器晓得伊拉个长度。

### 大部分 AI 勿好单独存在

批次／批号、序列号、有效期搭伊拉搿班侪是 *属性*：GS1 General Specifications
要求伊拉要搭一个标识键一道出现，而 Gaia 也会强制执行。

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

加转个 GTIN 落去就过得了。假使侬真个要解析一个片段——比方讲一个单元测试、一趟
局部扫描——就把迭项检查关脱：

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. 扫描器前缀搭 Digital Link 自动搞定

侬勿用告诉 Gaia 搿个输入是啥形状——四种形式伊侪检测得着。扫描器拨啥拨侬，
侬就喂啥拨伊。

**AIM 码制标识符前缀** 标明码制，会被自动剥除：

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** 会跑同一套验证搭增强：

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

因为两种形式侪落到同一个 `GS1AIObject` 搭浪，所以消费扫描结果个代码
勿用去管到底进来个是啥一种——而 `toElementString()`／`getCanonicalDigitalLink()`
还好辣两者当中互转。

一个 **8 位数个关联前缀**（`12345678~…`）也会被剥除，并保留辣
`getCorrelationInfo()` 浪，假使侬搿条流程有用个闲话。

---

## 7. 做少眼事体：解析模式

默认会做光所有物事。侬光光需要答案个一部分个辰光，就要少眼：

| 模式 | 答得出啥 | 成本 |
|---|---|---|
| `DATA_CARRIER` | 迭个是啥个码制？ | 最便宜——完全勿做 AI 解析，`getAiObject()` 是 `null` |
| `SYNTAX` | AI 代码搭长度个格式对勿对？ | 呒没校验位，呒没解释 |
| `CONTENT` | 迭个是勿是合法个 GS1 数据？ | 完整验证，呒没解释 |
| `INTERPRETATION` | 伊是啥意思？ | **默认**——所有物事 |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

侬要大批量验证而又永远勿会显示搿个拆解个辰光就用 `CONTENT`；
侬光光要把一个扫描结果分流到对个处理器个辰光就用 `DATA_CARRIER`。

---

## 8. 改语言搭日期格式

错误消息、解释标签搭 AI 描述侪翻译成仔 **35 种语言**；日期就侬想哪能显示侪好。
搿眼全部侪辣同一个不可变个 `ParseConfig` 里向：

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

搿眼值永远勿会本地化——光光标签、描述搭消息会——所以 `"2026-12-31"` 搭
`"09506000134352"` 辣每种语言里向意思侪一样。辣启动个辰光做一趟配置
然后共用伊；伊是不可变个。

---

## 9. 清理龌龊个输入

假使侬个来源会送出印刷用个 HRI 括号或者多余个空格，核心附带仔两个 **输入
修改器**，好辣解析之前把有效载荷修转好：

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

默认啥侪勿会启用，而且两个侪带牢注意事项——空格搭括号本身就是合法个 GS1
数据字符，所以只好把伊拉用辣侬确晓得个来源浪。请看
[内置修改器](GaiaParser-WuChinese.md#内置修改器)，搿搭也解释仔为啥剥除括号
之后一定要还原搿眼括号所隐含个分隔符。

---

## 10. 接下来去啥地方

- **[GaiaParser 开发者指南](GaiaParser-WuChinese.md)** — 流程个详细内容、完整个结果
  模型、每一个错误代码，搭 AI 及解释键个附录。
- **[GaiaBuilder 开发者指南](GaiaBuilder-WuChinese.md)** — 从 AI／值对构建元素串搭
  Digital Link URI。
- **[Gaia API HTTP 参考](../../gaia-api-reference.md)** — 同一个引擎经 HTTP 提供，
  假使侬勿想把搿个库嵌进去个闲话。
- **[ai-codes.txt](../../ai-codes.txt)** — 一份方便快查个平面 `(AI) TITLE` 清单。

### 五行版

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
