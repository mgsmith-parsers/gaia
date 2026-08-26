# GaiaBuilder — 開発者ガイド

## 目次

1. [概要](#概要)
2. [GS1 と General Specifications について](#gs1-と-general-specifications-について)
3. [クイックスタート](#クイックスタート)
4. [しくみ](#しくみ)
5. [エレメントストリングを組み立てる](#エレメントストリングを組み立てる)
   - [属性系の AI には識別キーが要る](#属性系の-ai-には識別キーが要る)
6. [Digital Link URI を組み立てる](#digital-link-uri-を組み立てる)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [検証とエラー](#検証とエラー)
   - [例外を投げる構築メソッド](#例外を投げる構築メソッド)
   - [例外を投げない tryBuild\* メソッド](#例外を投げない-trybuild-メソッド)
   - [エラーメッセージの言語](#エラーメッセージの言語)
   - [BuildResult](#buildresult)
9. [チェックデジット](#チェックデジット)
10. [スレッド安全性](#スレッド安全性)
11. [API リファレンス](#api-リファレンス)

---

## 概要

`GaiaBuilder` は [`GaiaParser`](GaiaParser-Japanese.md) の逆にあたります。アプリケーション識別子（AI）と値の組を集めたものから、適格な GS1 **エレメントストリング** または **GS1 Digital Link URI** を作り出します。AI とその完全なデータ値を渡すと、ビルダーがそれらを組み立て、`GaiaParser` と同じ仕組みで結果を検証し、出力を返します。

ビルダーは *自ら作った候補の出力を解析して* 検証するため、返されるものはすべて `GaiaParser` で誤りなく読み戻せることが保証されます。何が適格かについて、この二つの見解が食い違うことはあり得ません。

**入口クラス：** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 と General Specifications について

**GS1** は、サプライチェーンにおける識別とデータ交換のためのオープンな標準を策定・維持する国際的な非営利団体です。その標準は小売、ヘルスケア、物流、フードサービスをはじめ数多くの業界で用いられ、消費者向け包装の商品バーコードから医薬品用量のシリアル管理による追跡まで広く及びます。

本ビルダーが実装するすべての事柄について、典拠となるのは **GS1 General Specifications** です。この単一の文書が次の各項目を定めています。

- すべてのアプリケーション識別子（AI）コード、そのデータ名称、書式、検証規則
- AI エレメントストリングを組み立て符号化するための構文規則
- バーコードのシンボル体系要件と AIM シンボル体系識別子の割当て
- チェックデジットおよびチェックキャラクタのアルゴリズム
- 2 桁年の解決（スライディングウィンドウ規則）
- Data Matrix、QR Code、GS1-128、GS1 DataBar その他のデータキャリアの仕様

GS1 General Specifications は毎年更新されます。現行版と関連資料は次の場所で入手できます。

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA は GS1 General Specifications の **リリース 26.0（2026 年 1 月承認）** を実装しています。

GS1 Digital Link URI は関連標準 **GS1 Digital Link: URI Syntax** に従います。この標準は、主識別キー、キー修飾子の順序、およびビルダーが Digital Link URI を生成する際に適用するデータ属性の符号化方法を定めています。

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA は GS1 Digital Link: URI Syntax 標準の **リリース 1.7.0（2026 年 8 月承認）** を実装しています。

本書全体を通じて、節への参照は GS1 General Specifications を指します（たとえば「Table 7-5」「section 7.12」）。ただし Digital Link の節番号（たとえば「§4.9」「§4.12」）は例外で、GS1 Digital Link: URI Syntax 標準を指します。

---

## クイックスタート

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

素の AI 文字列よりも `GS1Constants_AICodes` の定数をお使いください（[パーサーガイドの付録 A](GaiaParser-Japanese.md#付録-a--ai-文字列定数)を参照）。

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## しくみ

どの構築も同じ道筋をたどります。

1. **組み立て** —— AI と値の組を連結して、候補となるエレメントストリングを作ります。*区切り文字を要する* AI のうち最後のエレメントでないものの後には、FNC1 グループ区切り文字（`0x1D`）を挿入します。長さがあらかじめ定まっている AI（GTIN、日付、固定長の計量値）には区切り文字を付けません。それ以外にはすべて付けます。（未知の AI がこの段階に達することはありません——`ai(...)` がその場で退けます。[エレメントストリングを組み立てる](#エレメントストリングを組み立てる)を参照。）
2. **検証** —— 候補を `GaiaParser` で `CONTENT` モードとして解析します。各値をその AI の書式とチェックデジットに照らし、構造上の規則（必須または排他の AI 組合せ）を適用します。解析が正当でなければ、構築は失敗します。
3. **生成** ——
   - エレメントストリングの場合は、検証済みオブジェクトの `toElementString()` を返します。
   - Digital Link の場合は、各エレメントに DL の役割（主キー、キー修飾子、データ属性）を割り当て、キー修飾子の並びを検証し、URI を生成したうえで、その URI を **もう一度解析して、正当な Digital Link として読み戻せることを確かめます**——文字列の組み立てとパーセント符号化に対する備えの確認です。この往復が成り立たない場合は `GaiaBuilderException` を投げます。

これは `DLSyntaxParser` の復元ロジックをなぞったものであり、そのため区切り文字の置き方も検証も、パーサーが期待するところとまったく同じです。

---

## エレメントストリングを組み立てる

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** はその場で検証されます。既知の GS1 アプリケーション識別子でなければ、`ai(...)` は `IllegalArgumentException` を投げます。（ビルダーは解析の前に AI と値を連結するため、`"99999"` のような未知あるいは長すぎる AI はここで捕まえる必要があります——さもないと、黙って別の AI として切り直されてしまいます。）一方 **値** の検証は後、構築の時点で行われます。
- 値は、チェックデジットも含めて **完全** でなければなりません。ビルダーがチェックデジットを計算したり付け足したりすることはありません——[チェックデジット](#チェックデジット)を参照。
- AI は追加した順に出力されます。FNC1 区切り文字は、GS1 の構文が求める箇所にビルダーが挿入します。ご自身で付け足さないでください。
- **AI がまったくない** 状態での構築は、`getErrors()` が空のまま `GaiaBuilderException("No AIs supplied")` を投げます——`GaiaError` を一つも伴わない唯一の失敗です。
- 値が書式やチェックデジットの規則に反する AI があると、構築は失敗します。

### 属性系の AI には識別キーが要る

ほとんどの AI は *属性* であり、GS1 General Specifications はそれらが識別キーを伴うことを求めています。ビルダーはこれを守らせ、構文段を最後まで通して検証します。これを外す手立てはありません。ロット番号やシリアル番号だけでは、適格なエレメントストリングに **なりません**。

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

識別キー（GTIN `01`、SSCC `00`、GLN `414` など）と社内用途の AI（`90`–`99`）は、単独で立って何ら差し支えありません。それ以外はすべて、連れとなる AI を必要とします。

> `GaiaParser` には `ParseConfig.skipRequiresCheck(true)` でこの確認を飛ばすよう指示できますが、`GaiaBuilder` には意図してそれに当たる手立てがありません——標準に適合した出力を生むためのものだからです。あえて不完全なエレメントストリングを組みたい場合は、ご自身で連結し、この確認を切って解析してください。

---

## Digital Link URI を組み立てる

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

正当な Digital Link には、**主識別キー** がちょうど一つ必要です（たとえば GTIN `01`、GLN `414`、SSCC `00`）。ビルダーは渡された AI をそれぞれ次のように分類します。

| 役割 | 生成のされ方 | 例 |
|------|-------------|---------|
| 主識別キー | ドメインまたは接頭辞に続くパスセグメント | `/01/09506000134352` |
| キー修飾子（CPV `22`、ロット `10`、シリアル `21` など） | それに続くパスセグメント。**§4.9 の正準な順序** で（追加した順ではありません） | `/10/LOT-ABC` |
| データ属性（それ以外すべて） | クエリパラメータ。**AI キーの辞書順に並べます**（§4.12） | `?17=271231` |

修飾子は生成時に並べ替えられるため、順不同で渡してもかまいません。`ai("21", …)` を `ai("10", …)` より先に書いても、生成されるのはやはり `/10/LOT/21/SER` です。主キーに対して許容されている必要があるのは、*集合* としての中身だけです。

パスとクエリのいずれにおいても、値はパーセント符号化されます。

次の場合、構築は **失敗** します（`GaiaBuilderException` を投げるか、失敗した `BuildResult` を返します）。

- AI のなかに主識別キーが **一つもない**。
- 主識別キーが **二つ以上ある**。
- Digital Link で **禁じられた** AI がある（`03`、`8014`）。
- 選ばれた主キーに対して **キー修飾子の並び** が正当でない（その主キーに属さない修飾子、あるいは許された順序を外れた修飾子など）。

---

## BuilderDigitalLinkConfig

スキーム、ドメイン、パス接頭辞、追加のクエリパラメータ、フラグメントを制御するには `BuilderDigitalLinkConfig` を渡します。

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

| ビルダーのメソッド | 用途 | 既定値 |
|----------------|---------|---------|
| `scheme(String)` | URI のスキーム。`http` または `https` でなければなりません | `https` |
| `domain(String)` | オーソリティ——ホスト、または `ホスト:ポート` | `id.gs1.org` |
| `pathPrefix(String)` | 最初の主キーより前のパスセグメント。先頭と末尾のスラッシュは整えられます | *（なし）* |
| `baseUrl(String)` | URL を `scheme` ＋ `domain` ＋ `pathPrefix` に分ける簡便手段 | — |
| `addQueryParam(String, String)` | 追加のクエリパラメータ。AI のデータ属性の **後ろ** に、追加した順で付きます。パーセント符号化されます | — |
| `fragment(String)` | URL のフラグメント（先頭の `#` は不要）。パーセント符号化されます | *（なし）* |

`build()` は設定をその場で検証します。`http(s)` 以外のスキームや空のドメインは `IllegalArgumentException` を投げます。

- `BuilderDigitalLinkConfig.canonical()`（別名 `defaultConfig()`）は、付加物のない既定の `https://id.gs1.org` です——引数なしで `buildDigitalLinkUri()` を呼んだときに使われるもの、`GS1AIObject.getCanonicalDigitalLink()` が生成するものと、まさに同じです。
- `baseUrl("http://id.example.org:8080/r")` → スキーム `http`、ドメイン `id.example.org:8080`、パス接頭辞 `/r`。
- 追加のクエリパラメータは常に AI から導かれた属性の後に置かれるため、AI の正準な順序（§4.12）は保たれます。

`BuilderDigitalLinkConfig` は不変です。一つのインスタンスを自由に使い回してください。

---

## 検証とエラー

### 例外を投げる構築メソッド

AI から適格な出力を組めない場合、`buildElementString()`、`buildDigitalLinkUri()`、`buildDigitalLinkUri(BuilderDigitalLinkConfig)` は **`GaiaBuilderException`**（非検査例外の `RuntimeException`）を投げます。

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **内容** の失敗（チェックデジットの誤り、書式の不一致、AI の欠落や排他）では、`getErrors()` はパーサーの `GaiaError` を保持します——[パーサーガイドで説明している](GaiaParser-Japanese.md#gaiaerror)ものと同じオブジェクトです。
- **Digital Link の構造** に関する失敗（主キーなし、主キーが複数、禁じられた AI、キー修飾子の並びが不正）では、`getErrors()` はビルダーの言語にローカライズされた `GaiaError` を一つだけ保持します（コードは `GE-L008`、`GE-L012`、`GE-L013`、`GE-L014` のいずれか）。

### 例外を投げない tryBuild\* メソッド

入力が利用者から来るもので、失敗が想定内かつ回復可能な結果である場合は、例外による制御ではなく `tryBuild*` の各版をお使いください。例外を投げる代わりに [`BuildResult`](#buildresult) を返します。

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

| 例外を投げる | 例外を投げない |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

`tryBuild*` の各メソッドは、対をなす例外版とまったく同じ検証の中核を共有します。異なるのは失敗の伝え方だけです。

### エラーメッセージの言語

内容検証のエラーは、ローカライズされたエラーカタログから取られます。`GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` が保持する `GaiaError` のメッセージ言語を選ぶには `language(...)` を呼んでください。既定は英語です。

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

これは `GaiaParser` が `ParseConfig` を通じて受け取るのと同じ `GaiaConstants.Language` の設定であり、ビルダーとパーサーのローカライズのされ方は揃っています。

**内容** に関する `GaiaError` のメッセージも、**Digital Link の構造** に関する失敗（主キーなし、主キーが複数、禁じられた AI、キー修飾子の並びが不正）も、いずれも共通のエラーカタログを通じてローカライズされます。後者に用いられるコードは `GE-L008`、`GE-L012`、`GE-L013`、`GE-L014` です。

### BuildResult

`BuildResult`（パッケージ `tools.pantheum.gaia.result`）は、`tryBuild*` の呼び出し結果を表す不変の値型です。

| メソッド | 成功時 | 失敗時 |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | 生成された文字列 | `null` |
| `getMessage()` | `null` | 失敗の説明 |
| `getErrors()` | 空のリスト | 検証エラー（`GaiaBuilderException.getErrors()` と同じもの） |

---

## チェックデジット

ビルダーはチェックデジットを検証しますが、**計算はしません**。値には初めからチェックデジットが含まれていなければなりません。計算するには `GS1Utils.calculateCheckDigit` をお使いください。

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` は、渡された数字に GS1 標準のモジュロ 10 アルゴリズムを適用し、`0–9` のチェックデジットを返します。入力が null、空、または数字でない場合は `-1` を返します。

---

## スレッド安全性

`GaiaBuilder` はスレッドセーフでは **ありません**。使い捨てを想定しています。`create()` を呼び、AI を足し、一度だけ構築してください。出力ごとに新しいビルダーを作り、一つをスレッド間で共有しないでください。

`BuilderDigitalLinkConfig`（およびそれが生む `BuildResult`）は不変であり、自由に共有できます。起動時に設定を一度作れば、多数のビルダーで使い回せます。

---

## API リファレンス

### `GaiaBuilder`

| メソッド | 説明 |
|--------|-------------|
| `static GaiaBuilder create()` | 空のビルダーを新たに始めます。 |
| `GaiaBuilder ai(String ai, String value)` | AI とその完全な値を足します。いずれかが `null` の場合、または `ai` が既知の GS1 アプリケーション識別子でない場合は `IllegalArgumentException` を投げます。 |
| `GaiaBuilder language(GaiaConstants.Language language)` | 内容検証のエラーメッセージの言語を設定します（既定は英語）。`null` は無視されます。 |
| `String buildElementString()` | GS1 エレメントストリングを生成します。失敗時は `GaiaBuilderException` を投げます。 |
| `String buildDigitalLinkUri()` | 正準な Digital Link URI を生成します。失敗時は `GaiaBuilderException` を投げます。 |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` に従って Digital Link URI を生成します。失敗時は `GaiaBuilderException` を投げます。 |
| `BuildResult tryBuildElementString()` | 例外を投げないエレメントストリングの構築。 |
| `BuildResult tryBuildDigitalLinkUri()` | 例外を投げない正準な Digital Link の構築。 |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` に従う、例外を投げない Digital Link の構築。 |

### `BuilderDigitalLinkConfig`

| メンバー | 説明 |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | 既定の `https://id.gs1.org`。 |
| `static Builder builder()` | 設定用のビルダーを新たに作ります。 |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | 確定したスキーム、オーソリティ、パス接頭辞。 |
| `getExtraQueryParams()` | 追加のクエリパラメータ。追加した順。 |
| `getFragment()` | フラグメント、または `null`。 |

### `GaiaBuilderException`

| メンバー | 説明 |
|--------|-------------|
| `getErrors()` | 失敗の原因となった `GaiaError` 群——内容の失敗ではパーサーのエラー、そうでなければ Digital Link の構造エラー 1 件（`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`）。`null` になることはありません。 |

### `BuildResult`

| メンバー | 説明 |
|--------|-------------|
| `isSuccess()` | 構築が成功したかどうか。 |
| `getValue()` | 成功時は生成された出力、失敗時は `null`。 |
| `getMessage()` | 失敗時は失敗の説明、成功時は `null`。 |
| `getErrors()` | 失敗時は検証エラー、成功時は空。`null` になることはありません。 |
| `getTiming()` | 構築の `ProcessingTiming`（開始時刻、処理時間）、または `null`。 |

---

あわせてご覧ください：解析側、AI エレメントのモデル、エラーリファレンス、AI と解釈の定数一覧については **[GaiaParser — 開発者ガイド](GaiaParser-Japanese.md)**。
