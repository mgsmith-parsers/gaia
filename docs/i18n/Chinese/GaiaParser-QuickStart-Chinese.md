# GaiaParser — 快速入门

约十分钟，把 GS1 条码的有效载荷变成结构化、已校验、供人阅读的数据。
这里走的是捷径；**[GaiaParser 开发者指南](GaiaParser-Chinese.md)** 才是
完整参考，而 **[GaiaBuilder](GaiaBuilder-Chinese.md)** 讲的是相反方向
（构建单元串与 Digital Link URI）。

## 内容

1. [把 Gaia 加入你的项目](#1-把-gaia-加入你的项目)
2. [解析点什么](#2-解析点什么)
3. [读取结果](#3-读取结果)
4. [处理解析失败](#4-处理解析失败)
5. [两处最容易吃亏的地方](#5-两处最容易吃亏的地方)
6. [扫描器前缀与 Digital Link 开箱即用](#6-扫描器前缀与-digital-link-开箱即用)
7. [少做点事：解析模式](#7-少做点事解析模式)
8. [更改语言与日期格式](#8-更改语言与日期格式)
9. [整理杂乱的输入](#9-整理杂乱的输入)
10. [接下来读什么](#10-接下来读什么)

---

## 1. 把 Gaia 加入你的项目

Gaia 未发布到 Maven Central，因此请先构建核心模块一次，并安装到你的
本地仓库：

```bash
cd gaia && mvn install
```

然后声明依赖：

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

你需要写的依赖清单就这么多。该 jar 很轻，因此 Gaia 唯一一个
编译期依赖——`com.fasterxml.jackson.core:jackson-databind`——会
传递引入；若你的构建已锁定某个 Jackson 版本，则以你的为准，Gaia 便用它。
Gaia 面向 **Java 11**，同一个 jar 在其后的每个 JVM 上都可原样运行。

> 在你刚上手的阶段，跳过核心模块的测试套件（`mvn install -DskipTests`）可把几分钟
> 缩短为几秒。

---

## 2. 解析点什么

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

`parse(String)` 会跑完**整条**处理链：语法、内容校验与解释。
这是恰当的默认设置——日后若有实测理由，再去收窄它。

---

## 3. 读取结果

`ParseResult.getAiObject()` 中存放着已识别的各个 AI。取用特定的某个时，请按代码而不要按
位置：

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

每个单元都带有一份**解释**列表——即原始数字背后经解码的含义，
由解释阶段生成：

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` 已本地化，供显示之用。若要在代码中*读取*某个值，请改用它那
稳定不变的类型键来查找：

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

不同的 AI 会产生不同的键：GTIN 给出其公司前缀、GTIN 类型与校验位；
价格则给出货币与十进制金额。完整清单见
[附录 B](GaiaParser-Chinese.md#附录-b--解释键常量)，常量位于
`GS1Constants_Enricher`。并非每个 AI 都有解释：自由文本的批号无从
推导出任何东西，其列表因而为空。

---

## 4. 处理解析失败

无效的有效载荷是一种正常结果，而非异常——对格式有误的 GS1 数据，
`parse` 从不抛出异常：

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**请按 `getId()` 分支，切勿依据消息文本。** 消息是本地化的，其措辞
并非契约——而且它们目前还带有一处已知的引号缺陷（上文中成对的 `''`），
此事记录在[错误参考](GaiaParser-Chinese.md#错误参考)中。

两个不同的问题，对应两个不同的方法：

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

某个阶段一旦失败，解析便不再向下推进，因此校验位有误时你会得到
若干校验错误，却得不到任何解释。

### 警告不会使结果失效

有些检查只作提示之用。无法识别的 GS1 公司前缀会被报告出来，但有效载荷
在结构上依然完好：

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

两者都想要时，请用 `getIssues()`。若你的流程必须拒收未知前缀，则须显式检查
`getWarnings()`——`isValid()` 不会替你代劳。

---

## 5. 两处最容易吃亏的地方

### GS 分隔符：为何漏掉它比报错更糟

变长 AI 会一直延伸到 **GS 字符**（ASCII `0x1D`，在条码码制中
称作 FNC1）或字符串末尾为止。当其后还跟着另一个 AI 时，该分隔符
不可省略：

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

漏掉它，你**不会**得到错误——你会得到一个笃定却错误的答案：

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` 的格式是 `X..20`，因此它名正言顺地吞下了 `LOT-ABC21SN-98765`，而解析器
根本无从知道这并非本意。下游对此无从补救，所以务必在源头就把
分隔符弄对：把扫描器的字节按 **ISO-8859-1** 读取，好让 `0x1D` 得以留存；在 Java 字符串字面量中
写作 `""`。定长 AI（`01`、`17`、`3103`）无须分隔符——
解析器知道它们的长度。

### 多数 AI 不能单独出现

批号、序列号、有效期之类都属于*属性*：GS1 General Specifications
要求它们必须与某个标识键同行，而 Gaia 对此严格执行。

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

补上 GTIN 便可通过。若你确有解析片段的需要——单元测试、
部分扫描——请关闭该检查：

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. 扫描器前缀与 Digital Link 开箱即用

你无须告诉 Gaia 输入是什么形式——四种形式它都能识别。扫描器给你什么，
原样交给它便是。

**AIM 码制标识符前缀**用于确定码制，并会被自动剥离：

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** 会走同样的校验与增强流程：

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

由于两种形式最终都落入同一个 `GS1AIObject`，消费扫描结果的代码
无须理会来的是哪一种——而 `toElementString()` / `getCanonicalDigitalLink()`
可在两者之间相互转换。

若你的处理链用到 **8 位关联前缀**（`12345678~…`），它同样会被剥离并保存在
`getCorrelationInfo()` 中。

---

## 7. 少做点事：解析模式

默认模式什么都做。只需要答案的一部分时，就少要一些：

| 模式 | 回答的问题 | 开销 |
|---|---|---|
| `DATA_CARRIER` | 这是哪种码制？ | 最低——完全不解析 AI，`getAiObject()` 为 `null` |
| `SYNTAX` | AI 代码与长度是否格式正确？ | 不做校验位，不做解释 |
| `CONTENT` | 这是有效的 GS1 数据吗？ | 完整校验，不做解释 |
| `INTERPRETATION` | 它是什么含义？ | **默认**——全部都做 |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

大批量校验且从不展示拆解结果时，请选用 `CONTENT`；只需把扫描结果
分流到相应处理器时，则选用 `DATA_CARRIER`。

---

## 8. 更改语言与日期格式

错误消息、解释标签与 AI 描述已译成 **35 种语言**；
日期则可按你的意愿呈现。这一切都装在一个不可变的 `ParseConfig` 里：

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

值从不本地化——本地化的只有标签、描述与消息——因此 `"2026-12-31"` 与
`"09506000134352"` 在任何语言中含义都相同。请在启动时构建一次配置
并共享它；它是不可变的。

---

## 9. 整理杂乱的输入

若你的数据源会输出打印出来的 HRI 圆括号或零散的空格，核心模块中备有两个
**输入修饰器**，可在解析前把有效载荷修好：

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

默认不启用任何修饰器，且两者各有其保留条件：空格与圆括号都是合法的
GS1 数据字符，因此只应对你了解底细的数据源使用。参见
[内置修饰器](GaiaParser-Chinese.md#内置修饰器)，其中还说明了为何去掉括号之后
必须把它们所隐含的分隔符补回。

---

## 10. 接下来读什么

- **[GaiaParser 开发者指南](GaiaParser-Chinese.md)** —— 处理链的细节、完整的
  结果模型、全部错误代码，以及 AI 与解释键的两个附录。
- **[GaiaBuilder 开发者指南](GaiaBuilder-Chinese.md)** —— 由 AI/值对构建单元串与 Digital Link
  URI。
- **[Gaia API HTTP 参考](../../gaia-api-reference.md)** —— 同一套机制的 HTTP 形式，
  适合不愿嵌入该库的场合。
- **[ai-codes.txt](../../ai-codes.txt)** —— 一份 `(AI) 名称` 的扁平清单，便于速查。

### 五行版

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
