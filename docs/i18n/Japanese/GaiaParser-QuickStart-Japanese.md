# GaiaParser — クイックスタート

GS1 バーコードのペイロードを、構造化され検証された、人が読めるデータに変えるまで
およそ十分。ここは近道です。**[GaiaParser 開発者ガイド](GaiaParser-Japanese.md)** が
完全なリファレンスであり、**[GaiaBuilder](GaiaBuilder-Japanese.md)** は逆方向
（エレメントストリングと Digital Link URI の組み立て）を扱います。

## 目次

1. [Gaia をプロジェクトに加える](#1-gaia-をプロジェクトに加える)
2. [まず解析してみる](#2-まず解析してみる)
3. [結果を読む](#3-結果を読む)
4. [解析が失敗したときの扱い](#4-解析が失敗したときの扱い)
5. [つまずきやすい二点](#5-つまずきやすい二点)
6. [スキャナの接頭辞も Digital Link もそのまま通る](#6-スキャナの接頭辞も-digital-link-もそのまま通る)
7. [仕事を減らす：解析モード](#7-仕事を減らす解析モード)
8. [言語と日付書式を変える](#8-言語と日付書式を変える)
9. [乱れた入力を整える](#9-乱れた入力を整える)
10. [この先の道しるべ](#10-この先の道しるべ)

---

## 1. Gaia をプロジェクトに加える

Gaia は Maven Central に公開されていません。コアを一度ビルドし、ご自身の
ローカルリポジトリにインストールしてください。

```bash
cd gaia && mvn install
```

そのうえで依存関係として宣言します。

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

書く必要のある依存関係はこれだけです。jar は薄く、Gaia が持つ唯一の
コンパイルスコープの依存——`com.fasterxml.jackson.core:jackson-databind`——は
推移的に入ってきます。ビルドで既に Jackson のバージョンを固定していれば、そちらが優先され、Gaia もそれを使います。
Gaia は **Java 11** を対象としており、同じ jar がそれ以降のどの JVM でもそのまま動きます。

> 慣れるまでの間は、コアのテスト一式を飛ばす（`mvn install -DskipTests`）と、数分が数
> 秒になります。

---

## 2. まず解析してみる

クラス一つ、設定なし。

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

`parse(String)` は **すべて** の工程を通します。構文、内容検証、そして解釈です。
これが適切な既定です。狭める理由が実測から出てきたら、そのとき絞ってください。

---

## 3. 結果を読む

`ParseResult.getAiObject()` に、解決された AI が入っています。特定の一つを取り出すときは、位置ではなく
コードで指してください。

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

各エレメントは **解釈** の一覧を持ちます。生の数字の背後にある、復号された意味であり、
解釈段が生成したものです。

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` はローカライズされており、表示のためのものです。コードのなかで値を *読む* には、
代わりに、変わることのない型キーで引いてください。

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI が違えば出るキーも違います。GTIN なら事業者コード、GTIN タイプ、チェックデジット。
価格なら通貨と小数の金額です。一覧はすべて
[付録 B](GaiaParser-Japanese.md#付録-b--解釈キーの定数) にあり、定数は
`GS1Constants_Enricher` にあります。すべての AI に解釈があるわけではありません。自由記述のロット番号からは
導けるものが何もないため、その一覧は空のままです。

---

## 4. 解析が失敗したときの扱い

不正なペイロードは通常の結果であって、例外ではありません。GS1 データに不備があっても
`parse` が例外を投げることは決してありません。

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**分岐は `getId()` で行い、メッセージでは決して行わないでください。** メッセージはローカライズされており、その文言は
取り決めではありません。しかも現状、引用符に既知の不具合があります（上の二重の `''`）。
これは[エラーリファレンス](GaiaParser-Japanese.md#エラーリファレンス)に記してあります。

問いが二つあれば、メソッドも二つです。

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

ある段が失敗すると解析はそれ以上深くへ進まないため、チェックデジットに誤りがあると
検証エラーは得られても、解釈は一つも得られません。

### 警告は結果を無効にしない

一部の確認は参考情報にとどまります。未知の GS1 事業者コードは報告されますが、ペイロードは
構造上なお健全です。

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

両方が要るときは `getIssues()` を使ってください。未知のコードを退けなければならない業務であれば、
`getWarnings()` を明示的に確かめてください。`isValid()` が代わりにやってはくれません。

---

## 5. つまずきやすい二点

### GS 区切り文字と、それを省くことがエラーより厄介な理由

可変長 AI は **GS 文字**（ASCII `0x1D`、バーコードのシンボル体系では
FNC1 と呼ばれます）か、文字列の終わりまで続きます。後ろに別の AI が続く場合、この区切り文字は
必須です。

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

省くと、エラーには **なりません**——自信たっぷりに誤った答えが返ってきます。

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` は `X..20` なので、`LOT-ABC21SN-98765` を丸ごと呑み込むのは筋が通っており、それが意図と違うことを
パーサーが知るすべはありません。後段でこれを取り戻すことはできません。ですから区切り文字は
発生源で正しくしてください。スキャナのバイト列は `0x1D` が生き残るよう **ISO-8859-1** として読み、Java の文字列リテラルでは
`""` と書きます。固定長 AI（`01`、`17`、`3103`）に区切り文字は要りません——
パーサーがその長さを知っているからです。

### ほとんどの AI は単独では成り立たない

ロット、シリアル番号、使用期限といったものは *属性* です。GS1 General Specifications は
それらが識別キーとともに運ばれることを求めており、Gaia はそれを守らせます。

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

GTIN を足せば通ります。断片をどうしても解析したい場合——単体テストや
部分的な読み取りなど——は、この確認を切ってください。

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. スキャナの接頭辞も Digital Link もそのまま通る

入力がどの形かを Gaia に教える必要はありません——四つの形すべてを判別します。スキャナが返したものを
そのまま渡してください。

**AIM シンボル体系識別子の接頭辞** はシンボル体系を定め、自動で取り除かれます。

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** も同じ検証と付加を通ります。

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

どちらの形も同じ `GS1AIObject` に行き着くため、読み取り結果を受け取る側のコードは
どちらが来たのかを気にする必要がありません。`toElementString()` / `getCanonicalDigitalLink()` が
両者を相互に変換します。

**8 桁の相関接頭辞**（`12345678~…`）も、処理の流れで使っているなら同じく取り除かれ、
`getCorrelationInfo()` に保たれます。

---

## 7. 仕事を減らす：解析モード

既定はすべてを行います。答えの一部だけでよいなら、求める量を減らしてください。

| モード | 答える問い | コスト |
|---|---|---|
| `DATA_CARRIER` | これはどのシンボル体系か。 | 最小——AI の解析は一切行わず、`getAiObject()` は `null` |
| `SYNTAX` | AI コードと長さは適格か。 | チェックデジットも解釈もなし |
| `CONTENT` | これは正当な GS1 データか。 | 完全な検証、ただし解釈なし |
| `INTERPRETATION` | 何を意味するのか。 | **既定**——すべて |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

大量に検証していて分解結果を表示しないなら `CONTENT` を、読み取り結果を適切なハンドラへ
振り分けるだけなら `DATA_CARRIER` を選んでください。

---

## 8. 言語と日付書式を変える

エラーメッセージ、解釈ラベル、AI 説明は **35 の言語** に翻訳されています。
日付はお好みの形で表せます。それらはすべて、不変の `ParseConfig` 一つに収まります。

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

値がローカライズされることは決してありません——されるのはラベル、説明、メッセージだけです——ので、`"2026-12-31"` も
`"09506000134352"` もどの言語でも同じ意味です。設定は起動時に一度組み立てて
共有してください。不変です。

---

## 9. 乱れた入力を整える

送出元が印字済みの HRI 丸括弧やまばらな空白を出してくる場合、コアには二つの
**入力モディファイア** があり、解析の前にペイロードを直します。

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

既定では何も有効になっておらず、どちらにも但し書きがあります。空白も丸括弧も正当な
GS1 データ文字ですから、素性の分かっている送出元にのみ適用してください。
[組込みモディファイア](GaiaParser-Japanese.md#組込みモディファイア)を参照。括弧を取り除いたあと、それが含意していた
区切り文字をなぜ戻さなければならないのかも、そこで説明しています。

---

## 10. この先の道しるべ

- **[GaiaParser 開発者ガイド](GaiaParser-Japanese.md)** —— パイプラインの詳細、結果モデルの全体像、
  すべてのエラーコード、そして AI と解釈キーの付録。
- **[GaiaBuilder 開発者ガイド](GaiaBuilder-Japanese.md)** —— AI と値の組からエレメントストリングと Digital Link
  URI を組み立てる。
- **[Gaia API HTTP リファレンス](../../gaia-api-reference.md)** —— 同じ仕組みを HTTP 越しに。
  ライブラリを組み込みたくない場合に。
- **[ai-codes.txt](../../ai-codes.txt)** —— `(AI) 名称` の平たい一覧。手早く引くために。

### 五行版

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
