# GAIA（GS1 Application Identifiers Analyser）— 開発者ガイド

## 目次

1. [概要](#概要)
2. [GS1 と General Specifications について](#gs1-と-general-specifications-について)
3. [GS1 アプリケーション識別子](#gs1-アプリケーション識別子)
4. [クイックスタート](#クイックスタート)
5. [解析パイプライン](#解析パイプライン)
   - [前段 — 入力モディファイア](#前段--入力モディファイア)
   - [段 0 — 相関 ID](#段-0--相関-id)
   - [段 1 — 入力の振り分け](#段-1--入力の振り分け)
   - [段 2 — 構文](#段-2--構文)
   - [段 3 — 内容](#段-3--内容)
   - [段 4 — 解釈](#段-4--解釈)
6. [解析の設定（`ParseConfig`）](#解析の設定parseconfig)
   - [オプション](#オプション)
   - [ローカライズされたメッセージとラベル](#ローカライズされたメッセージとラベル)
   - [日付の整形](#日付の整形)
7. [入力モディファイア](#入力モディファイア)
   - [組込みモディファイア](#組込みモディファイア)
   - [モディファイアを書く](#モディファイアを書く)
   - [モディファイアの登録](#モディファイアの登録)
   - [モディファイアが何をしたかを調べる](#モディファイアが何をしたかを調べる)
   - [モディファイアの失敗時の扱い](#モディファイアの失敗時の扱い)
8. [解析モード](#解析モード)
   - [DATA_CARRIER モード](#data_carrier-モード)
   - [SYNTAX モード](#syntax-モード)
   - [CONTENT モード](#content-モード)
   - [INTERPRETATION モード（既定）](#interpretation-モード既定)
9. [相関 ID](#相関-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [結果を扱う](#結果を扱う)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry と DataCarrierType](#datacarrierentry-と-datacarriertype)
12. [エラーリファレンス](#エラーリファレンス)
13. [スレッド安全性](#スレッド安全性)
14. [付録 A — AI 文字列定数](#付録-a--ai-文字列定数)
    - [識別とシリアル化](#識別とシリアル化)
    - [日付と時刻](#日付と時刻)
    - [数量と計量 — 変量計量（メートル法）](#数量と計量--変量計量メートル法)
    - [数量と計量 — 変量計量（ヤード・ポンド法／米国）](#数量と計量--変量計量ヤードポンド法米国)
    - [価格と金額](#価格と金額)
    - [場所と出荷](#場所と出荷)
    - [商品の属性とトレーサビリティ](#商品の属性とトレーサビリティ)
    - [各国の医療償還番号（NHRN）](#各国の医療償還番号nhrn)
    - [ヘルスケア、GMN、HIDRI、CPID、個人データ](#ヘルスケアgmnhidricpid個人データ)
    - [社内用途](#社内用途)
15. [付録 B — 解釈キーの定数](#付録-b--解釈キーの定数)
    - [日付と時刻](#日付と時刻)
    - [収穫日](#収穫日)
    - [GS1 事業者コード](#gs1-事業者コード)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [国（ISO 3166）](#国iso-3166)
    - [通貨（ISO 4217）](#通貨iso-4217)
    - [温度](#温度)
    - [性別（ISO 5218）](#性別iso-5218)
    - [水生生物種（FAO）](#水生生物種fao)
    - [NATO 在庫番号（NSN）](#nato-在庫番号nsn)
    - [ロール製品](#ロール製品)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM の識別子（EID / ICCID）](#sim-の識別子eid--iccid)
    - [認証番号](#認証番号)
    - [GS1 UIC](#gs1-uic)
    - [新生児の出生順](#新生児の出生順)
    - [グローバル型式番号（GMN）](#グローバル型式番号gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [小数値と計量値](#小数値と計量値)
    - [地理座標](#地理座標)
    - [生産方法](#生産方法)
    - [AIDC メディア種別](#aidc-メディア種別)
    - [全体のうちの何個目か](#全体のうちの何個目か)
    - [コンポーネントの分解](#コンポーネントの分解)
    - [その他](#その他)

---

## 概要

`GaiaParser` は、GS1 アプリケーション識別子（AI）のエレメントストリングを解析するための入口です。スキャナの生出力を次のいずれかの形式で受け取り、解決されたすべての AI、検証エラー、および（任意で）人が読める解釈を含む構造化された `ParseResult` を返します。

- 素の AI エレメントストリング：`0109506000134352`
- AIM シンボル体系識別子を前置したエレメントストリング：`]C10109506000134352`
- GS1 Digital Link URI：`https://example.com/01/09506000134352`
- 上記のいずれかに、8 桁の相関 ID を任意で前置したもの：`12345678~0109506000134352`

**入口クラス：** `tools.pantheum.gaia.GaiaParser`

> **Gaia は初めてですか。** まずは **[GaiaParser クイックスタート](GaiaParser-QuickStart-Japanese.md)** をご覧ください。依存関係、最初の解析、そしてつまずきやすい数点を、十分ほどで押さえられます。本ガイドは完全なリファレンスです。

> 逆方向の操作——AI と値の組から適格なエレメントストリングや Digital Link URI を *構築* すること——については **[GaiaBuilder — 開発者ガイド](GaiaBuilder-Japanese.md)** をご覧ください。

---

## GS1 と General Specifications について

**GS1** は、サプライチェーンにおける識別とデータ交換のためのオープンな標準を策定・維持する国際的な非営利団体です。その標準は小売、ヘルスケア、物流、フードサービスをはじめ数多くの業界で用いられ、消費者向け包装の商品バーコードから医薬品用量のシリアル管理による追跡まで広く及びます。

本パーサーが実装するすべての事柄について、典拠となるのは **GS1 General Specifications** です。この単一の文書が次の各項目を定めています。

- すべてのアプリケーション識別子（AI）コード、そのデータ名称、書式、検証規則
- AI エレメントストリングを組み立て符号化するための構文規則
- バーコードのシンボル体系要件と AIM シンボル体系識別子の割当て
- チェックデジットおよびチェックキャラクタのアルゴリズム
- 2 桁年の解決（スライディングウィンドウ規則）
- Data Matrix、QR Code、GS1-128、GS1 DataBar その他のデータキャリアの仕様

GS1 General Specifications は毎年更新されます。現行版と関連資料は次の場所で入手できます。

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA は GS1 General Specifications の **リリース 26.0（2026 年 1 月承認）** を実装しています。

GS1 Digital Link URI は関連標準 **GS1 Digital Link: URI Syntax** に従います。この標準は、主識別キー、キー修飾子の順序、およびパーサーが Digital Link 入力に適用するデータ属性の符号化方法を定めています。

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA は GS1 Digital Link: URI Syntax 標準の **リリース 1.7.0（2026 年 8 月承認）** を実装しています。

本書全体を通じて、節への参照は GS1 General Specifications を指します（たとえば「Table 7-5」「section 7.12」）。ただし Digital Link の節番号（たとえば「§4.9」「§4.12」）は例外で、GS1 Digital Link: URI Syntax 標準を指します。

---

## GS1 アプリケーション識別子

**GS1 アプリケーション識別子（AI）** とは、2 桁から 4 桁の短い数字の接頭辞であり、その直後に続くデータの意味と書式を定めるものです。AI は GS1 General Specifications で定義され、商品識別、日付、数量、ロット番号、シリアル番号、計量値、URL など、サプライチェーンの多様なデータを網羅します。

### AI エレメントの構成

各 AI エレメントは 2 つの部分から成ります。

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

AI コードは常に数字です。データ値はその直後に続き、コードと値の間に区切り文字は入りません。

### 固定長 AI と可変長 AI

AI は 2 つの区分に分かれます。

| 種別 | 挙動 | 例 |
|---|---|---|
| **固定長** | 文字数が厳密に定まり、常に全体が読み取られる | AI `01`（GTIN）——常に 14 桁 |
| **可変長** | 1 文字から上限まで。GS 区切り文字または入力の終端で終わる | AI `10`（ロット）——英数字 1〜20 文字 |

AI が固定長か可変長かは、GS1 仕様におけるその定義のみで決まります。パーサーが推測することはありません。

### 複数の AI を含むエレメントストリング

複数の AI を 1 本のエレメントストリングに連結できます。固定長 AI は、読み取るべき文字数をパーサーが常に正確に把握しているため、そのまま連結できます。可変長 AI は、後ろに別の AI が続く場合、必ず **GS 文字**（ASCII `0x1D`、バーコードのシンボル体系では FNC1 とも呼ばれます）で終端しなければなりません。そうしてはじめて、値がどこで終わり次の AI コードがどこから始まるのかをパーサーが判別できます。

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

Java の文字列リテラルでは、GS 文字を Unicode エスケープ `""` として書きます。

### よく使われる AI

| AI | データ名称 | 書式 | 値の例 |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6（YYMMDD） | `261231` |
| `17` | USE BY or EXPIRY | N6（YYMMDD） | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500`（= 1.500 kg） |
| `3922` | PRICE | N..15 | `91234`（= 912.34、単一通貨圏） |
| `710` | NHRN PZN | X..20 | `12345678` |

> 4 桁の計量系・価格系 AI の **4 桁目** は、暗黙の小数位の桁数を符号化します。`3103` は小数 3 桁のキログラム単位の正味重量（`001500` = 1.500 kg）を表し、`3102` なら同じ数字を 15.00 kg と読みます。上表の `書式` 列が示すのは *データ* の書式です。各 AI の完全な `getFormatString()` には AI 自体も含まれます（たとえば `3103` なら `N4+N6`）。

### 人が読める解釈（HRI）

慣用の可読形式では、各 AI コードを丸括弧で囲んでその値の直前に置き、エレメント間を空白で区切ります。

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS 区切り文字は HRI には現れません。この書式は `GS1AIObject.toHriString()` が生成します。

### 4 桁の AI コード

一部の AI は 2 桁ではなく 4 桁を用います。先頭 2 桁が AI のファミリーを示し、3 桁目・4 桁目が追加の意味（計量系 AI における暗黙の小数点位置など）を担います。パーサーは完全な AI コードをエレメントストリングから自動的に判別します。呼び出し側は常に完全なコードを扱ってください（たとえば `"31"` ではなく `"3102"`）。

---

## クイックスタート

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

> **GS 区切り文字：** 複数の AI を含むストリングでは、可変長 AI を GS 文字（ASCII `0x1D`）で区切らなければなりません。Java の文字列リテラルでは `""` を使ってください。

---

## 解析パイプライン

### 前段 — 入力モディファイア

`ParseConfig` に **入力モディファイア** が設定されている場合、それらは何よりも先に実行されます。相関 ID の除去より前、データキャリアの判別より前、GS1 パイプラインに入る前です。各モディファイアは次のモディファイアのために生入力を書き換え、以降のすべての段はこの連鎖の出力に対して働きます。

既定ではモディファイアは一つも設定されていないため、明示的に有効化しない限りこの前段は何もしません。[入力モディファイア](#入力モディファイア)を参照してください。

---

### 段 0 — 相関 ID

GS1 の処理に入る前に、`GaiaParser` は入力が任意の **相関 ID 接頭辞** で始まっているかを確認します。これは 10 進 ASCII 数字ちょうど 8 桁の後にチルダ（`~`）が続く形、たとえば `12345678~` です。

接頭辞が存在すれば、それを取り除き、返される `ParseResult` に `CorrelationInfo` として格納します。以降のすべての段は、取り除いた後のペイロードに対して働きます。接頭辞がなければ、入力はそのまま通過します。

詳しくは[相関 ID](#相関-id)をご覧ください。

---

### 段 1 — 入力の振り分け

相関 ID を取り除いた後、`GaiaParser` は（取り除き済みの）入力が **AIM シンボル体系識別子** で始まっているかを確認します。これは `]` + ASCII 英字 + ASCII 数字 という 3 文字の接頭辞です（たとえば GS1-128 なら `]C1`、GS1 DataMatrix なら `]d2`、GS1 DataBar / GS1 Composite なら `]e0`）。

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

データキャリアが GS1 AI に対応していない場合（郵便バーコードなど）、解析はただちにエラー `GE-D002` で停止します。

---

### 段 2 — 構文

常に実行されます。二つの下位手順から成ります。

**2a. トークン化（`AISyntaxParser`）**
- GS1 の接頭辞表（GS1 General Specifications 表 7-5）を用いて、先頭 2 文字から AI コードの長さを読み取ります。
- 固定長 AI は、入力から厳密なバイト数を読み取ります。
- 可変長 AI は、GS 文字または入力の終端まで読み取ります。
- 複数コンポーネントの AI では、値のかたまりをコンポーネントごとの区間に切り分けます。

**2b. 構造検証（`SyntaxValidator`）**
- AI の重複を検出します（`GE-S004`）。
- 必須の AI 依存関係を確認します。たとえば AI `02` は AI `37` を必要とします（`GE-S005`）。
- 併存できない AI の組合せを確認します（`GE-S006`）。

この段のエラーはレベル `SYNTAX_ERROR`（トークン化）または `INTEGRITY_ERROR`（構造）を持ちます。トークン化・構造のいずれであれ **エラーが一つでも** あれば、パイプラインは停止し、内容段と解釈段は飛ばされます。

---

### 段 3 — 内容

段 2 がエラーを一つも生じなかった場合にのみ実行されます（トークン化・構造のいずれからも）。エレメントごとのパイプラインは次のとおりです（各手順は直前の手順がエラーを生じなかった場合にのみ実行されます）。

| 手順 | バリデータ | エラーコード |
|---|---|---|
| 正規表現による確認 | `RegexValidator` | `GE-C001` |
| コンポーネントの文字集合と書式 | `ComponentValidator` | `GE-C005` ＋ 条件ごとの書式コード（`GE-C054`–`GE-C115`） |
| チェックデジット／チェックキャラクタ | `CheckDigitCharacterValidator` | `GE-C003`、`GE-C004` |
| 独自の意味検証 | `ContentValidatorRegistry` | 条件ごとの内容コード（`GE-C116`–`GE-C170`） |

この段のエラーはレベル `FORMAT_ERROR` または `DATA_ERROR` を持ちますが、一つだけ例外があります。
GS1 キーを持つ AI に対する GS1 事業者コードの確認は参考情報にとどまり、レベルは `WARNING` です（[エラーリファレンス](#エラーリファレンス)を参照）。
したがって、未知の事業者コードそれ自体によって結果が
無効になることはありません。

---

### 段 4 — 解釈

`INTERPRETATION` モードでのみ、かつ、いずれのエレメントも先行段のエラーを持たない場合にのみ実行されます。`InterpretationEngine` が各エレメントにラベル付きのメタデータを付加します。

- `dd/mm/yyyy` に整形し直した日付
- GTIN チェックデジットの分解と GS1 事業者コードの照会
- ISO 3166 の国名
- ISO 4217 の通貨名と通貨記号
- 復号した小数金額
- HRI（人が読める解釈）の断片

結果は各 `GS1AIObjectElement` に `GS1AIInterpretation` エントリとして付加されます。

---

## 解析の設定（`ParseConfig`）

`GaiaParser` が公開する入口はちょうど二つです。

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` は **既定の設定** で動作します。`INTERPRETATION` モード、リトルエンディアンの日付（`dd/mm/yyyy`）に区切り文字 `/` と 4 桁の年、そして **英語** のエラーメッセージです。これらのいずれかを——解析モードを含めて——変えたい場合は、流れるようなビルダーで `ParseConfig` を組み立て、引数 2 個のオーバーロードを使ってください。

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

オプションの列挙型はすべて `GaiaConstants` にあります。

### オプション

| ビルダーのメソッド | 列挙型（`GaiaConstants`） | 既定値 | 効果 |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | パイプラインの深さ——[解析モード](#解析モード)を参照。 |
| `language(...)`      | `Language`      | `ENGLISH`        | エラーメッセージ、解釈ラベル、**および** AI 説明の言語。 |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | 日付要素の順序：`LITTLE`（`dd/mm/yyyy`）、`MIDDLE`（`mm/dd/yyyy`）、`BIG`（`yyyy/mm/dd`）。 |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | 日付要素の間の文字：`SLASH`（`/`）、`HYPHEN`（`-`）、`PERIOD`（`.`）。 |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT`（`12`）または `THREE_LETTER`（`DEC`）。 |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT`（`2026`）または `TWO_DIGIT`（`26`）。 |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | 構造上の「必要とする」確認（`GE-S005`）を飛ばします。 |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | 構造上の「排除する」確認（`GE-S006`）を飛ばします。 |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / クラス名 | なし | 解析前に生入力を書き換えるコード——二つの[組込みモディファイア](#組込みモディファイア)と、ご自身で書いたもの。[入力モディファイア](#入力モディファイア)を参照。 |

日付に関する四つのオプションは、解釈エンリッチャーが生成する整形済み日付文字列にのみ影響し（`INTERPRETATION` モードにおいて）、検証を変えることはありません。ビルダーの値は省略できます。設定しなかった（あるいは `null` を渡した）オプションは既定値のままです。

### ローカライズされたメッセージとラベル

`language(...)` は、人が読む **三種類** のテキストの言語を選びます。エラーメッセージ、解釈ラベル（各 `GS1AIInterpretation` の `getLabel()`）、そして AI 説明（各 `GS1AIObjectElement` の `getDescription()`）です。

`GaiaConstants.Language` は **35 の言語** を定義し、世界で最も話者の多い言語を網羅しています。英語、フランス語、スペイン語、ドイツ語、イタリア語、ポルトガル語、オランダ語、ポーランド語、ロシア語、ウクライナ語、チェコ語、スウェーデン語、中国語、日本語、韓国語、アラビア語、インドネシア語、ヒンディー語、トルコ語、ベンガル語、ウルドゥー語、ベトナム語、ナイジェリア・ピジン、エジプト・アラビア語、マラーティー語、テルグ語、タミル語、広東語、呉語、タガログ語、ペルシア語、ハウサ語、パンジャーブ語、ジャワ語、スワヒリ語です。

翻訳の状況（同梱時点）：
- **解釈ラベル** —— すべての言語に翻訳済み。
- **エラーメッセージ** —— すべての言語に翻訳済み。
- **AI 説明** —— 英語を除くすべての言語に翻訳済み。英語は独立したカタログを持ちません。`gs1-application-identifiers.jsonld` にある当該 AI エントリの `description` フィールドから直接読み取られ、あらゆる AI 説明は最終的にここへ立ち返ります。

ナイジェリア・ピジン（`NIGERIAN_PIDGIN`）は英語を基層とするクレオール語であり、解釈ラベルとエラーメッセージについては意図して英語のテキストをそのまま用いています。AI 説明はこの例外のさらに例外です。AI 説明のカタログはラベル・メッセージのカタログとは別に作成されたため、英語を流用せず本来のピジンに翻訳されています。機械翻訳は、本番で頼りにする前に母語話者による確認を受けてください。

ある言語のカタログに欠けているメッセージ・ラベル・説明は、いずれも英語に立ち返ります。右から左に書く言語（アラビア語、ウルドゥー語、エジプト・アラビア語、ペルシア語）は文字列として正しく格納されています。右から左に描画するのは表示層の役目です。

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

解釈ラベルも同じようにローカライズされます（値そのものは変わらず、ラベルだけが変わります）。

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI 説明も同じようにローカライズされます（ローカライズされないのは `getTitle()`、たとえば `"GTIN"` だけです）。

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### 日付の整形

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## 入力モディファイア

**入力モディファイア** とは、Gaia が解析する前に生の入力文字列を書き換えるコードです。モディファイアは、既に壊れた形で届く入力のために用意されています。GS 区切り文字を印字可能なプレースホルダに置き換えるスキャナ、ペイロードをベンダ独自の接頭辞で包むミドルウェア、何もかもを大文字に変えてしまうホストシステムなどです。呼び出し箇所ごとに文字列を前処理して回り（そのどれか一つで微妙に取り違え）るのではなく、この正規化を `ParseConfig` に一度だけ宣言し、パーサーに適用させてください。

モディファイアは `GaiaParser.parse(...)` のまさに冒頭で実行されます。相関 ID の除去より前、AIM シンボル体系識別子の判別より前、GS1 パイプラインより前です。以降の処理はすべて、書き換え後の文字列しか目にしません。**既定では何も設定されておらず**、二つの[組込みモディファイア](#組込みモディファイア)も例外ではありません。`ParseConfig` ごとに明示的に有効化してください。

**インタフェース：** `tools.pantheum.gaia.modifier.ModifierInterface`

### 組込みモディファイア

コア jar には `tools.pantheum.gaia.modifier.custom` に二つのモディファイアが同梱されています。GS1 ペイロードが壊れて届く最も一般的な二つの形——データとして扱われてしまった印字済みの HRI 丸括弧と、余分な空白——を扱うため、よくある場合には独自クラスを書く必要がありません。

| クラス | `getName()` | はたらき |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | 各 AI を囲む HRI 丸括弧（`(01)…(10)…`）を取り除き、それが含意していた FNC1 区切り文字を復元します。 |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI エレメントストリングからすべての空白（`0x20`）を取り除きます。 |

これらは特別な地位を持たない普通の `ModifierInterface` 実装であり、登録・順序付け・報告・失敗のいずれも、ご自身が書いたものとまったく同じように扱われます。

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

どちらも状態を持たずスレッドセーフなので、単一のインスタンスを共有できます。また、設定駆動の構成のために、どちらも完全修飾クラス名で指定できます（[モディファイアの登録](#モディファイアの登録)を参照）。

#### `ModifierRemoveAIBrackets`

GS1 の人が読める解釈は、各 AI を丸括弧に入れて印字します——`(01)09521234543213(10)ABC123`——が、これは純然たる印字上の約束事にすぎません。HRI を出力するよう設定されたスキャナやミドルウェアは、その括弧をデータとしてそのまま送り出し、トークン化はそれをどう扱えばよいのか見当もつきません。

括弧を取り除くだけでは仕事の半分です。HRI では *次の* AI の開き括弧こそが直前の値の終わりを示すため、括弧付きの形では可変長 AI に FNC1 は要りません。何も考えずに括弧を取り除けば、その境界は消えてしまいます。

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

そこでこのモディファイアは、**直前の AI が可変長である境界すべてに FNC1 を挿し戻し**、括弧が符号化していたものを正確に復元します。

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

長さはパーサー自身の `AiDefinitionRegistry` から引くため、決め打ちの一覧ではなく、可変長 AI のすべてが扱われます。三つの場合はあえて手を触れません。すでに FNC1 で終わっている値（両方の流儀を出力する送出元に二つ目の区切り文字を足さないため）、既知の AI ではない括弧付きコード（未知の AI は自身の長さについて何も語りません）、そしてストリング内の最後の AI です。

この書き換えは **冪等** です——自身の出力に対してもう一度走らせても何も変わりません——ので、一部の入力だけが括弧付きである混在した流れに対しても安全です。

> **限界。** `(` と `)` はそれ自体が正当な GS1 データ文字であり、用いるパターンは `\((\d{2,4})\)` にすぎません。たまたま 2〜4 桁の数字を丸括弧で囲んで含む値も、同様に括弧を外されてしまいます。HRI の括弧の約束事を用いる送出元に限って適用し、本物の括弧を含む値には用いないでください。

#### `ModifierRemoveSpaces`

一部のスキャナ、ミドルウェア、ラベル印字の工程は、そうでなければ適格なエレメントストリングに余分な空白を挿し込みます。固定幅の欄を埋めるため、読みやすい単位に区切るため、あるいは長い値を折り返すためです。トークン化はそのひとつひとつをデータとして扱うので、空白が入り込んだ値は壊れ、可変長 AI ではそれ以降のすべてがずれてしまいます。

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

取り除かれるのは ASCII `0x20` だけです。ほかの空白文字はそのまま残ります。たとえばタブは GS1 の符号化可能文字集合の外にあるため、パーサーはこれを黙って掃き捨てるのではなく `GE-S008` として報告します。

> **限界。** 空白（`0x20`）は GS1 の不変文字集合に属するため、ロット番号や得意先品番が正当に空白を含むことはあり得ます。モディファイアには余分な空白と本物の空白との区別がつきません。AI の値の内部で空白を使わないと分かっている送出元に限って適用してください。

#### 接頭辞は書き換えず、読み飛ばす

モディファイアはパーサーが何も取り除いていない段階で実行されるため、生入力にはまだ相関 ID、AIM シンボル体系識別子、ECI 指示子が付いている可能性があります。二つの組込みモディファイアはいずれも、パーサー自身の `CorrelationIdParser` と `DataCarrierParser` のロジックを使って AI エレメントストリングの開始位置を突き止め、そこから先だけを書き換えて、結果を **手つかずの** 接頭辞につなぎ戻します。

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

値が GTIN-14 まで詰められる EAN/UPC キャリア（`isRequiresGtinPadding()`）は丸ごと読み飛ばされます。そのペイロードは AI 構造を持たない純粋な数字のバーコード値であり、そこでは括弧も空白も意味を持ち得ないからです。

#### 順序：括弧より先に空白

両方を使うときは、**`ModifierRemoveSpaces` を先に登録してください**。括弧の照合は位置に依存します。空白の入った `( 01 )` は `\((\d{2,4})\)` に一致しないため、括弧は生き残り、それが含意していた区切り文字は永久に復元されません。

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### モディファイアを書く

組込みのどちらも合わない場合はご自身で書いてください。インタフェースはメソッド一つです。

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

書き換えが解析の設定に依存する場合は、代わりに引数 2 個のオーバーロードをオーバーライドしてください。

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

規約：

| 規則 | 内容 |
|---|---|
| 状態を持たずスレッドセーフであること | クラスごとにインスタンス 1 個がキャッシュされ、すべての解析で共有されます。 |
| 公開の引数なしコンストラクタ | モディファイアをクラス名で指定する場合にのみ必要です。 |
| `null` 入力と空入力を扱うこと | パーサーは連鎖を実行する前にそれらを取り除いたりしません。 |
| `null` を返すと「変更なし」の意味 | 直前の値がそのまま引き継がれます。当てはまらない場合は `input` をそのまま返してください。 |
| 例外を投げるより、そのまま返すほうがよい | 例外を投げるモディファイアは解析を中断させます——[失敗時の扱い](#モディファイアの失敗時の扱い)を参照。 |
| `getName()` | `ModifierInfo` に報告される名前を決めるにはこれをオーバーライドします。既定はクラスの単純名です。 |

### モディファイアの登録

モディファイアは追加した順に実行され、各々が直前の出力を受け取ります。インスタンスで、完全修飾クラス名で、あるいはそのいずれかのリストで登録してください。

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

[組込みモディファイア](#組込みモディファイア)の指定の仕方はご自身のものと変わりません——**常に完全修飾名** です。これらに短縮名や別名での検索はありません。同梱かどうかにかかわらず、`ModifierRegistry` はすべてのモディファイアを完全なクラス名で解決します。

名前を解決するのは `ModifierRegistry` です。各クラスを引数なしコンストラクタで一度だけ生成し、そのインスタンスをキャッシュして、同じクラスを指す後続のあらゆる設定に使い回します。解決は **設定を組み立てる時点** で行われるため、見つからない名前、`ModifierInterface` を実装していない名前、生成できない名前は、その場で `IllegalArgumentException` を投げます——解析時になって黙って問題になることはありません。リフレクションで生成できないモディファイア（依存性を注入されているものなど）は、あらかじめ登録しておけば名前で指定できるままにできます。

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### モディファイアが何をしたかを調べる

モディファイアが設定されている場合、`ParseResult.getPayload()` は **書き換え後** の入力を返します。元の入力は `ModifierInfo` に保たれます。

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` は各モディファイアの `getName()` を報告します。既定ではクラスの単純名ですが、二つの組込みモディファイアはこれをオーバーライドしているため、両者から成る連鎖はクラス名ではなく表示名を報告します。

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

モディファイアが一つも設定されていない場合、`getModifierInfo()` は `null` を返します。モディファイアは走ったものの、いずれも入力をそのまま返した場合、情報は存在し `isModified()` は `false` になります。`getAppliedModifiers()` に並ぶのは、実際に入力を変えたモディファイアだけです。

### モディファイアの失敗時の扱い

例外を投げたモディファイアは解析を中断させます。その例外は、問題のモディファイア名を示す `GaiaModifierException` に包まれ、結果にはその名前をメッセージに含む内部エラー `GE-I001` が付きます。`getPayload()` は書き換えられていない入力を返します。解析が、書き換えの途中で止まった文字列を抱えたまま続くことは **意図的にありません**。黙って失敗した正規化の工程は、一見正当に見えて実は誤った入力から得られた結果を生んでしまうからです。

---

## 解析モード

各モードは、そのモードが実行する最も深い[パイプラインの段](#解析パイプライン)を示します。それ以前の段もすべて実行されます。

| モード | どこまで実行するか | 答える問い |
|---|---|---|
| `DATA_CARRIER` | 段 1（入力の振り分け） | これを運んできたのはどのシンボル体系か。 |
| `SYNTAX` | 段 2（構文） | AI コードと長さは適格か。 |
| `CONTENT` | 段 3（内容） | 値は正当な GS1 データか。 |
| `INTERPRETATION` | 段 4（解釈） | 値は何を意味するのか。 |

### DATA_CARRIER モード

段 1 の後で止まります。AIM シンボル体系識別子を検証してシンボル体系を判別しますが、AI の解析パイプラインには入りません。完全な検証の負担を負わずにシンボル体系を見分け、処理を振り分けたい場合に向いています。

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

**こんなときに：** ペイロードの処理方法を決める前に、バーコードの種別を見分ける必要があるとき。たとえば 1D と 2D のシンボル体系で別々のハンドラに振り分ける場合です。その振り分けには、`getName()` の文字列照合ではなく、型付きの [`DataCarrierType`](#datacarrierentry-と-datacarriertype)（`getDataCarrier().getDataCarrierType()`）を用いてください。

---

### SYNTAX モード

段 2 の後で止まります。内容検証の負担を負わずに構造面のふるい分けをしたい場合に向いています。

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

**こんなときに：** 完全な検証に踏み切る前に AI コードとデータ長が適格であることを確かめたいとき、あるいは内容エラーがまれな大量処理の場面です。

---

### CONTENT モード

段 3 の後で止まります。

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

> ほとんどの AI は単独では成り立ちません。AI `10`（BATCH/LOT）、`17`（USE BY or EXPIRY）、`21`
> （SERIAL）はいずれも、同じエレメントストリング内に AI `01` のような識別キーを *必要とします*。
> 上の例で GTIN を省くと、内容の検証に至るより前、段 2 の時点で `GE-S005` により
> 失敗します。付随する AI をあえて省いた断片を解析したい場合は、
> `ParseConfig` に `skipRequiresCheck(true)` を設定してください。

**こんなときに：** 読み取った値を業務処理に用いる前に、それが GS1 に完全に適合しているかを、解釈による付加の負担なしに確かめたいときです。

---

### INTERPRETATION モード（既定）

段 4 まで、パイプライン全体を実行します。モード引数なしで `parse(String)` を呼んだときの既定です。内容検証を問題なく通過したエレメントだけが付加の対象になります。

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

**出力例：**
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

**金額の例（AI 3932 — ISO 通貨コード付きの価格）：**
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

**こんなときに：** 表示層、ラベル検証ツール、あるいは AI の値を人に分かる形に分解して示す必要のある、あらゆる画面を作るときです。

---

## 相関 ID

業務の流れによっては、走査の出来事をセッションや取引に結び付けられるよう、生の GS1 入力の前に独自の 8 桁の相関 ID を置くことがあります。書式は次のとおりです。

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

チルダ（`~`）が区切り文字です。これは GS1 の内容には **含まれません**。GS1 の解析が始まる前に取り除かれます。

### 判別の規則

入力が 10 進 ASCII 数字（`0`–`9`）ちょうど 8 桁で始まり、その直後に `~` が続くとき、この接頭辞と判別されます。9 文字目が `~` でない場合、あるいは先頭 8 文字のいずれかが数字でない場合、その入力は相関接頭辞のない通常の GS1 内容として扱われます。

### 相関 ID を取り出す

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

### AIM シンボル体系識別子との併用

相関接頭辞は AIM シンボル体系識別子の前に置けます。パーサーはこれを透過的に扱います。

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**実装クラス：** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** は、一つ以上の AI 値を HTTP(S) URL の構造そのものに符号化し、物理的な商品の識別子をウェブ上で解決できるようにします。GAIA は **非圧縮** の URI について *GS1 Digital Link Standard: URI Syntax*（リリース 1.7.0）を実装しています。

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` は Digital Link URI を自動で判別します。`http://` または `https://` で始まる入力はすべて `GS1DLParser` に回され、エレメントストリングのパイプラインと同じ内容段・解釈段が実行されます。

### URI の構造と AI の役割

Digital Link URI 内の各 AI は三つの役割のいずれかを担い、それは各 `GS1AIObjectElement` の `getDigitalLinkAIType()`（`GS1Constants.DigitalLinkAIType`）で得られます。

| 役割 | 位置 | 例 |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | パス中の最初の `/ai/値` の組（§4.3） | `/01/09506000134352` |
| `KEY_QUALIFIER` | それに続くパスの組。主キーごとに定められた順序による（§4.9） | `/10/ABC`、`/21/SER` |
| `DATA_ATTRIBUTE` | キーがすべて数字のクエリパラメータ（§4.10） | `?17=271231` |

適用される構造上の規則（`DLPathRules`）：
- パス中の主識別キーはちょうど **一つ**。追加のキーはクエリのデータ属性として符号化しなければなりません。
- キー修飾子は主キーが許容するものでなければならず、定められた順序で現れる必要があります。任意の修飾子は省けますが、*現れる* 修飾子はやはり定められた順序に従わなければなりません——[修飾子の順序](#修飾子の順序)を参照。
- 主キーの前には任意の独自パスセグメントを置けます（たとえば `/products/au/01/...`）。`getDigitalLinkInfo().getCustomPathStem()` で取得できます。
- 数字以外のクエリキー（`linkType`、`context`、`23P` のような拡張パラメータ）は無視されます。すべて数字のキーは `validAsDataAttribute` が付いた正当な AI でなければなりません。
- パーセント符号化された値の文字は復号されます。AI `(03)` と `(8014)` は認められません。

主キーと、それが許容する修飾子の並びは、決め打ちではなく AI 定義から **データ駆動** で導かれます——`gs1DigitalLinkPrimaryKey` フラグと `gs1DigitalLinkQualifiers` 属性によります。

構造上の違反、および URL でない入力は、いずれも Digital Link の構造エラー（`GE-L001`–`GE-L014`、条件ごとに 1 コード）を生じます。構造エラーがある場合でも、分解された URL のメタデータ（`scheme`、`domain`、`path`、`customPathStem`、`query`、および `java.net.URL`）は `getDigitalLinkInfo()` から得られます。

### 修飾子の順序

主キーごとに、`gs1DigitalLinkQualifiers` が一つ以上の **順序付き** 修飾子列を挙げています。列の中では、角括弧で囲まれた AI が **任意**、囲まれていない AI が **必須** です。これは §4.9 の ABNF における `[cpv-comp]` の書き方に対応します。一つの主キーに属する複数の列は、互いに排他的な選択肢です。

たとえば GTIN（`01`）は二つの列を定めています。

| パス | 列 | 意味 |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV、LOT、SER——各々任意だが、この定まった順序で |
| upui-path | `235` | TPX（必須）。GTIN + TPX = UPUI |

したがって `/01/09506000134352/10/LOT-ABC/21/SER` は正当（LOT が SER より前、CPV は省略）、`/01/.../21/SER/10/LOT-ABC` は **拒否** され（順序が違う）、`/01/09506000134352/235/2ABC456` は upui-path にあたります。順序の確認は順序を保った部分列の照合であるため、任意の AI は飛ばせても、並べ替えることは決してできません。

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

**実装クラス：** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## 結果を扱う

### ParseResult

`GaiaParser.parse()` が返す最上位の結果です。

| メソッド | 返す型 | 説明 |
|---|---|---|
| `isValid()` | `boolean` | どのレベルにもエラーがなければ `true`。警告は有効性に影響しません。`getAiObject()` が `null` のときは常に `true`。 |
| `getPayload()` | `String` | 相関接頭辞を取り除いた後の入力文字列。[入力モディファイア](#入力モディファイア)が書き換えていれば、その後の文字列。 |
| `getPayloadContent()` | `String` | AIM シンボル体系識別子と ECI 接頭辞を取り除いたペイロード。 |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`、`GS1_DIGITAL_LINK`、`DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL`（GS1 でないとして拒否されたデータキャリア。たとえば Code 39 キャリア `]A0`）、または `UNABLE_TO_DETERMINE_CONTENT`（`aiObject` が `null` のとき。たとえば `DATA_CARRIER` モード）。 |
| `getRequestedParseMode()` | `ParseMode` | 設定されたパイプラインの深さ（`ParseConfig.getRequestedParseMode()`）。 |
| `getAchievedParseMode()` | `ParseMode` | 解析が実際に到達した最も深い段——下記参照。 |
| `isParseComplete()` | `boolean` | 解析が要求した深さに到達していれば `true`（`achieved == requested`）。`isValid()` とは独立です。 |
| `getAiObject()` | `GS1AIObject` | 解決されたすべての AI。`DATA_CARRIER` モードでは `null`。 |
| `getErrors()` | `List<GaiaError>` | WARNING 以外のレベルのエラーすべて（オブジェクト単位およびすべてのエレメント単位）。 |
| `getWarnings()` | `List<GaiaError>` | WARNING レベルの注意すべて（オブジェクト単位およびすべてのエレメント単位）。 |
| `hasWarnings()` | `boolean` | WARNING レベルの注意が出ていれば `true`。 |
| `getIssues()` | `List<GaiaError>` | エラーと警告をまとめたもの。 |
| `hasDataCarrier()` | `boolean` | AIM シンボル体系識別子が判別できていれば `true`。 |
| `getDataCarrier()` | `DataCarrierEntry` | シンボル体系のメタデータ。キャリアが判別できなかった場合は `null`。 |
| `hasEci()` | `boolean` | ペイロードから ECI 指示子を取り除いていれば `true`。 |
| `getEci()` | `EciEntry` | ECI 符号化のメタデータ、または `null`。 |
| `hasCorrelationId()` | `boolean` | 元の入力に `DDDDDDDD~` の相関接頭辞があれば `true`。 |
| `getCorrelationInfo()` | `CorrelationInfo` | 取り出された相関 ID。なかった場合は `null`。 |
| `isInputModified()` | `boolean` | [入力モディファイア](#入力モディファイア)が入力を変えていれば `true`。 |
| `getModifierInfo()` | `ModifierInfo` | モディファイアの連鎖が行ったこと——`getOriginalInput()`、`getModifiedInput()`、`getAppliedModifiers()`。モディファイアが設定されていなければ `null`。 |
| `getTiming()` | `ProcessingTiming` | 解析の実時間計測——`getStartTime()`（`Instant`）、`getProcessingTime()`（`Duration`）、`getProcessingTimeMillis()`（`long`）、`getCompletionTime()`。`GaiaParser` が生成した結果でなければ `null`。 |
| `getVersion()` | `String` | この結果を生成したライブラリのバージョン。 |

#### 要求した解析モードと到達した解析モード

パイプラインは **SYNTAX → CONTENT → INTERPRETATION** という段梯子を進み、エラーがあれば早めに止まります。そのため実際に *到達した* モードは、*要求した* モードより浅くなることがあります。どこまで進んだかは `getAchievedParseMode()` が示します。

| 要求 | 起きること | 到達 | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | **構文または構造** のエラーがトークン化の後で解析を止める | `SYNTAX` | `false` |
| `INTERPRETATION` | **内容** のエラー（書式やチェックデジットの誤り）が付加を妨げる | `CONTENT` | `false` |
| `CONTENT` | 内容段は常に最後まで実行される（エラーは記録されるが致命的ではない） | `CONTENT` | `true` |
| いずれでも（エラーのない入力） | パイプラインが要求した深さに到達する | = 要求 | `true` |
| `DATA_CARRIER` | キャリアを検証。AI の内容は解析しない | `DATA_CARRIER` | `true` |
| いずれでも | AI の解析より前にデータキャリアが拒否される（たとえば GS1 でない `]A0` キャリア） | `SYNTAX` | `false` |

`isParseComplete()` は `isValid()` とは独立です。チェックデジットの誤った GTIN を `CONTENT` で解析した場合、その結果は **完了している**（内容段は実行された）と同時に **無効** です（チェックデジットが合わない）。「パイプラインは頼んだ深さまで進んだか」を問うには `isParseComplete()` を、「データは適格か」を問うには `isValid()` を使ってください。

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

解決された AI エレメントの集合です。

| メソッド | 説明 |
|---|---|
| `getAis()` | 入力順に並んだすべての `GS1AIObjectElement` インスタンス。 |
| `get(String aiCode)` | 指定した AI コードに一致する最初のエレメント、または `null`。 |
| `contains(String aiCode)` | そのコードの AI があれば `true`。 |
| `size()` | 解決された AI の個数。 |
| `isValid()` | オブジェクト単位のエラーがなく、どのエレメントにもエラーがなければ `true`。 |
| `toHriString()` | HRI 文字列。たとえば `(01)09506000134352 (17)261231`。 |
| `toElementString()` | 生のエレメントストリング——括弧なしで、可変長エレメントごとに FNC1 を付す——たとえば `010950600013435210LOT-ABC<GS>17271231`。`isValid()` が `false` の場合は `null` を返します。 |
| `getContentType()` | `hasDigitalLink()` が真なら `GS1_DIGITAL_LINK`、そうでなければ `GS1_APPLICATION_IDENTIFIERS`。 |
| `hasDigitalLink()` | 入力が主識別キーを持つ GS1 Digital Link URI であれば `true`。主キーのない適格な URL でも `getDigitalLinkInfo()` は得られますが、ここでは `false` を返します。 |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` 上の正準な GS1 Digital Link URI（§4.12）——主キーと修飾子をパスセグメントに、データ属性を AI キー順に並べたクエリパラメータに——主キーがなければ `null`。 |
| `getDigitalLinkInfo()` | URI を分解したメタデータ（`getUri()`、`getUrl()`、`scheme`、`domain`、`path`、`getCustomPathStem()`、`query`）。Digital Link でなければ `null`。 |
| `getAllErrors()` | オブジェクト単位のエラー ＋ すべてのエレメントのエラー（WARNING を除く）。 |
| `getAllWarnings()` | オブジェクト単位の警告 ＋ すべてのエレメントの警告。 |
| `getAllIssues()` | 以上をまとめたもの。 |

---

### GS1AIObjectElement

解決された AI ひとつ分の実体です。

| メソッド | 説明 |
|---|---|
| `getAi()` | AI コード。たとえば `"01"`、`"3102"`。 |
| `getTitle()` | GS1 のデータ名称。たとえば `"GTIN"`、`"BATCH/LOT"`。 |
| `getDescription()` | 当該 AI の完全な GS1 説明。**解析言語にローカライズ済み**（英語ならたとえば `"Global Trade Item Number (GTIN)"`）。未翻訳の場合は AI 定義の英語テキストに立ち返ります。 |
| `getFormatString()` | AI *と* そのデータの両方を含む書式記述子。たとえば AI `01` なら `"N2+N14"`、AI `10` なら `"N2+X..20"`、AI `3932` なら `"N4+N3+N..15"`。 |
| `getValue()` | エレメントストリングから取り出した生のデータ値。 |
| `isFixedLength()` | 当該 AI のデータ長が固定なら `true`。 |
| `getPosition()` | 元の入力における文字位置（0 から数えます）。 |
| `getGS1ComponentValues()` | コンポーネントごとに切り分けた値（複数コンポーネントの AI の場合）。 |
| `getErrors()` | エレメント単位の、WARNING 以外のエラー。 |
| `getWarnings()` | エレメント単位の WARNING レベルの注意。 |
| `getIssues()` | 当該エレメントのエラーと警告をまとめたもの。 |
| `hasErrors()` | WARNING 以外のエラーが付いていれば `true`。 |
| `hasWarnings()` | WARNING レベルの注意が付いていれば `true`。 |
| `getInterpretations()` | `GS1AIInterpretation` のエントリ（INTERPRETATION モードで埋められます）。 |
| `getInterpretation(String type)` | 指定した `GS1Constants_Enricher` の型キーに一致する最初の解釈、または `null`。 |
| `getDigitalLinkAIType()` | 当該エレメントの Digital Link における役割（`PRIMARY_IDENTIFICATION_KEY`、`KEY_QUALIFIER`、`DATA_ATTRIBUTE`）。エレメントストリング形式の入力では `null`。 |
| `hasDigitalLinkAIType()` | Digital Link の役割が割り当てられていれば `true`。 |

---

### GaiaError

不変の検証エラーまたは注意です。

| メソッド | 説明 |
|---|---|
| `getId()` | カタログの識別子。たとえば `"GE-C003"`。 |
| `getLevel()` | `SYNTAX_ERROR`、`INTEGRITY_ERROR`、`FORMAT_ERROR`、`DATA_ERROR`、`WARNING` のいずれか。 |
| `getStage()` | `DATA_CARRIER`、`DIGITAL_LINK`、`SYNTAX`、`CONTENT`、`INTERNAL` のいずれか。 |
| `getCode()` | 機械可読の短いコード。 |
| `getAi()` | エラーの原因となった AI コード。オブジェクト単位のエラーでは `null`。 |
| `getMessage()` | 値を差し込んだ、人が読めるメッセージ。 |
| `getPosition()` | 元の入力における文字位置（0 から数えます）。 |

---

### GS1AIInterpretation

ラベル付きの解釈の断片ひとつです。`INTERPRETATION` モードで `GS1AIObjectElement` に付加されます。

| メソッド | 説明 |
|---|---|
| `getType()` | 機械可読の型キー。たとえば `"DATE_VALUE"`、`"GS1_COMPANY_PREFIX"`。言語をまたいで一定です。 |
| `getLabel()` | 人が読めるラベル。**解析言語にローカライズ済み**（英語ならたとえば `"Date"` / `"GS1 company prefix"`）。 |
| `getValue()` | 取り出された値、または付加された値。たとえば `"31/12/2026"`、`"9506000"`。ローカライズされません。 |

---

### DataCarrierEntry と DataCarrierType

入力が AIM シンボル体系識別子を伴う場合、`ParseResult.getDataCarrier()` は、データを運んだシンボルを説明する `DataCarrierEntry` を返します。このエントリは、判別された AIM 識別子に対応するレジストリの具体的なレコードであり、`DataCarrierType` はそれが属する、コンパイル時に定まった列挙型です。

#### DataCarrierEntry

判別された AIM シンボル体系識別子ひとつ分のメタデータです（`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`）。

| メソッド | 説明 |
|---|---|
| `getAimCodeId()` | 判別された AIM シンボル体系識別子。たとえば `"]C1"`。 |
| `getName()` | 具体的なシンボルの、人が読める名称。たとえば `"GS1-128 / ISBT 128"`、`"EAN-8"`。 |
| `getDescription()` | 当該キャリアのより詳しい説明。 |
| `getType()` | キャリアの構造上の種別を文字列で表したもの（`getDataCarrierType().getCategory()` と対応します）。 |
| `getStandard()` | 記録がある場合の、シンボル体系の標準。 |
| `getDataCarrierType()` | このエントリに対応する型付きの `DataCarrierType`——プログラムによる振り分けにはこちらを用いてください。 |
| `isGs1Capable()` | キャリアが GS1 データ（AI エレメントストリングや Digital Link）を収められるなら `true`。 |
| `isGs1AICapable()` | キャリアが GS1 の AI エレメントストリングを収められるなら `true`。 |
| `isGs1DigitalLinkCapable()` | キャリアが GS1 Digital Link URI を収められるなら `true`。 |
| `isEciCapable()` | キャリアが ECI 指示子に対応していれば `true`。 |
| `isRequiresGtinPadding()` | AI 解析の前に数値を GTIN-14 まで詰める EAN/UPC/ITF キャリアで `true`。 |

#### DataCarrierType

データキャリア種別の、コンパイル時に定まった列挙型です。ISO/IEC 15424 で割り当てられた AIM シンボル体系識別子を鍵とします（`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`）。`]` の次の文字（*コード文字*）が系統を選びます。ほとんどの系統は、修飾子の別を問わず一つの定数に対応します（`ITF` は `]I0`–`]I2` を、`EAN_UPC` は EAN-13、UPC-A、UPC-E、EAN-8 を含みます）。GS1 が AI データのために修飾子を確保している場合、その変種は独立した定数になります——`GS1_128`（`]C1`）、`GS1_DATA_MATRIX`（`]d2`）、`GS1_QR_CODE`（`]Q3`）、`GS1_DOT_CODE`（`]J1`）——通常の対応物とは区別されます。AIM 識別子がない場合、あるいはそれが未知のキャリアを指す場合、種別は `UNKNOWN` です。

| メソッド | 説明 |
|---|---|
| `getCategory()` | 大分類 `GaiaConstants.DataCarrierTypeCategory`：`LINEAR`、`STACKED_LINEAR`、`TWO_D`、`POSTAL`、`OCR`、`OTHER`。 |
| `getCodeChar()` | 系統を示す AIM のコード文字。たとえば QR Code なら `"Q"`。`UNKNOWN` では `null`。 |
| `getDisplayName()` | *種別* の人が読める名称（`DataCarrierEntry.getName()` より広い場合があります——たとえば `"EAN-8"` に対して `"EAN-13 / UPC-A / UPC-E / EAN-8"`）。 |
| `isGs1DataCarrier()` | 常に GS1 の AI データを表す定数について `true`：GS1 が確保した四つの変種（`GS1_128`、`GS1_DATA_MATRIX`、`GS1_QR_CODE`、`GS1_DOT_CODE`）に加え、`]e` の修飾子はいずれも GS1 DataBar を表すため本質的に GS1 である `GS1_DATABAR`。`DataCarrierEntry.isGs1AICapable()` より狭く、通常の `QR_CODE` でも GS1 の AI データを運べます。 |
| `static forAimCodeId(String)` | AIM 識別子から直接に種別を求めます（`"]Q3"` → `GS1_QR_CODE`、`"]Q9"` → `QR_CODE`）。識別子がない、形式が不正、または未知の場合は `UNKNOWN` を返します。 |

名称ではなく種別で振り分ける例——1D シンボル（Code 128）と 2D シンボル（QR / Data Matrix）を分ける場合：

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` に含まれるのはマトリックス型とドット型のシンボルだけです。積層型の 1D キャリア（`PDF417`、
`CODE_16K`、`CODABLOCK`、`CODE_49`）は、世間では「2 次元」バーコードと呼ばれることが多いものの、
`STACKED_LINEAR` に属します。両者を一つのまとまりとして扱いたい場合——たとえばレーザースキャナではなく
イメージャが要るかどうかを判断する場合——は、二つの分類のいずれかに当たるかを調べてください。

> 種別の判別には、読み取りデータに AIM シンボル体系識別子が含まれている必要があります。それがなければ `getDataCarrier()` は `null` となり、種別は `UNKNOWN` になります。AIM 識別子の接頭辞を送出するようスキャナを設定してください。

---

## エラーリファレンス

| コード | レベル | 段 | 意味 |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | AI の接頭辞が未知——データ長を判別できない |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | 入力が短すぎて完全な AI コードを読めない |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | 値が途中で切れている——当該 AI が要する文字数に足りない |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | エレメントストリング内にアプリケーション識別子の重複 |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | 必須の AI 依存関係が欠けている |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | 併存できない AI の組合せ——同時に現れてはならない二つの AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | トークン化で予期せぬ失敗 |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | エレメントストリング内に GS1 の符号化可能文字集合外の文字 |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | 可変長 AI の後に必須の FNC1 区切り文字がない |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | すべてのコンポーネントの上限を超える余剰データ |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | 固定長 AI の後、途中の位置に FNC1 区切り文字 |
| `GE-W002` | WARNING | SYNTAX | エレメントストリング末尾の FNC1（参考情報のみ） |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI の構造違反——条件ごとに 1 コード（URI の形式不正、スキーム、ホスト、修飾子の順序、禁止された AI、主キーなし（`GE-L013`）、主キーが複数（`GE-L014`）など） |
| `GE-C001` | FORMAT_ERROR | CONTENT | 値が当該 AI の正規表現を満たさない |
| `GE-C003` | DATA_ERROR | CONTENT | チェックデジットの検証に失敗 |
| `GE-C004` | DATA_ERROR | CONTENT | チェックキャラクタ対の検証に失敗 |
| `GE-C005` | FORMAT_ERROR | CONTENT | コンポーネントの値に、許容される文字集合外の文字が含まれる |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | コンポーネント書式の不適合——検証条件ごとに 1 コード（`componentformat/` を参照） |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | 独自の意味検証の不適合——検証条件ごとに 1 コード（`content/validator/` を参照）。**例外：** 下に挙げる 14 件の GS1 事業者コード確認はレベル `WARNING`、`GE-C168`（ISO 3166-1 の数字国コードが未知）はレベル `FORMAT_ERROR` です。 |
| GS1 事業者コードの確認 | WARNING | CONTENT | GS1 キーを持つ AI において、キーが既知の GS1 事業者コードで始まっていない——`GE-C122`（CPID）、`GE-C129`（GCN）、`GE-C131`（GDTI）、`GE-C132`（GIAI）、`GE-C133`（GINC）、`GE-C135`（GLN）、`GE-C137`（GMN）、`GE-C140`（GRAI）、`GE-C142`（GSIN）、`GE-C144`（GSRN）、`GE-C146`（GTIN）、`GE-C148`（HIDRI）、`GE-C153`（ITIP）、`GE-C165`（SSCC）。参考情報のみで、有効性には影響しません。 |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040（IMEI）/ 8041（IMEI2）の IMEI チェックデジット（Luhn）が不適合 |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042（ESIM）の EID チェックデジット（Luhn）が不適合 |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | AIM シンボル体系識別子が未知 |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | キャリアは判別できたが、GS1 の AI エレメントストリングにも Digital Link URI にも対応していない |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | 予期せぬ内部エラー |

> **メッセージ表示における既知の不具合。** カタログのひな型は、差し込む値を
> MessageFormat 流に二重のアポストロフィで囲んでいますが（`''{value}''`）、
> `ErrorRegistry` は素の `String.replace` で差し込むため、この重複が
> `getMessage()` にまで残ります。本ガイドが引用しているメッセージ文では
> `value '09506000134351'` と書かれていますが、実際には現状 `value ''09506000134351''` と表示されます。
> 値を引用符で囲むメッセージはすべて、35 の言語カタログのいずれにおいても影響を受けます。
> エラーメッセージを解析しないでください。`getId()` / `getCode()` で照合してください。

---

## スレッド安全性

`GaiaParser` は生成後スレッドセーフです。単一のインスタンスをスレッド間で共有し、並行して使えます。推奨される方法は、アプリケーションの起動時にインスタンスを一つ生成し、それを使い回すことです。

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` は不変であり、同じく安心して共有できます。ライブラリが肩代わりできない唯一のスレッド安全性の義務は[入力モディファイア](#入力モディファイア)にあります。各モディファイアはインスタンス 1 個がキャッシュされ、並行するすべての解析で共有されるため、実装は状態を持たないものでなければなりません。

---

## 付録 A — AI 文字列定数

`GS1Constants_AICodes`（パッケージ `tools.pantheum.gaia.gs1.constants`）は、GAIA が認識するすべてのアプリケーション識別子について `String` 定数を宣言しています。AI コードを文字列で直に書く代わりに、これらの定数をお使いください。

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

各定数は AI コードの文字列表現を保持します（たとえば `AI_01_GTIN = "01"`）。

### 識別とシリアル化

| AI | 定数 | 説明 |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | 出荷容器連続番号 (SSCC). |
| `01` | `AI_01_GTIN` | 商品識別コード (GTIN). |
| `02` | `AI_02_CONTENT` | 内容物である取引単位の商品識別コード (GTIN). |
| `03` | `AI_03_MTO_GTIN` | 受注生産（MtO）取引単位の識別 (GTIN). |
| `10` | `AI_10_BATCH_LOT` | バッチ番号またはロット番号. |
| `20` | `AI_20_VARIANT` | 社内製品バリアント. |
| `21` | `AI_21_SERIAL` | シリアル番号. |
| `22` | `AI_22_CPV` | 消費者向け製品バリアント. |
| `235` | `AI_235_TPX` | 第三者管理によるシリアル化された商品識別コード (GTIN) の拡張 (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | 製造業者が割り当てた追加の製品識別. |
| `241` | `AI_241_CUST_PART_NO` | 顧客部品番号. |
| `242` | `AI_242_MTO_VARIANT` | 受注生産バリエーション番号. |
| `243` | `AI_243_PCN` | 包装コンポーネント番号. |
| `250` | `AI_250_SECONDARY_SERIAL` | 副シリアル番号. |
| `251` | `AI_251_REF_TO_SOURCE` | 発生元エンティティへの参照. |
| `253` | `AI_253_GDTI` | 文書種別識別コード (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | ロケーションコード (GLN) 拡張コンポーネント. |
| `255` | `AI_255_GCN` | クーポン識別コード (GCN). |
| `30` | `AI_30_VAR_COUNT` | 品目の変量数量（変量取引単位）. |
| `37` | `AI_37_COUNT` | 物流単位に含まれる取引単位または取引単位ピースの数量. |

### 日付と時刻

| AI | 定数 | 説明 |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | 製造日 (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | 支払期日 (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | 包装日 (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | 賞味期限 (YYMMDD). |
| `16` | `AI_16_SELL_BY` | 販売期限 (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | 有効期限 (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | 配達可能開始日時 (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | 配達期限日時 (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | リリース日 (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | 有効期限日時 (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | 最初の冷凍日 (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | 収穫日 (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | 検査期限日 (YYMMDD[hhmm]). |

### 数量と計量 — 変量計量（メートル法）

4 桁の AI ファミリー `310n`–`369n` は変量計量の数量を符号化します。3 桁目が計量の種別を選び、**4 桁目**（`n`、0–5）が暗黙の小数位の桁数です。たとえば `AI_3102_NET_WEIGHT_KG` は小数 2 桁のキログラム単位の正味重量を意味します。

| ファミリー | 定数のかたち（`n` = 小数位の桁） | 説明 |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | 正味重量、キログラム（変量取引単位）. |
| `311n` | `AI_311n_LENGTH_M` | 長さまたは第一寸法、メートル（変量取引単位）. |
| `312n` | `AI_312n_WIDTH_M` | 幅、直径、または第二寸法、メートル（変量取引単位）. |
| `313n` | `AI_313n_HEIGHT_M` | 奥行き、厚さ、高さ、または第三寸法、メートル（変量取引単位）. |
| `314n` | `AI_314n_AREA_M` | 面積、平方メートル（変量取引単位）. |
| `315n` | `AI_315n_NET_VOLUME_L` | 正味容積、リットル（変量取引単位）. |
| `316n` | `AI_316n_NET_VOLUME_M` | 正味容積、立方メートル（変量取引単位）. |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | 物流重量、キログラム. |
| `331n` | `AI_331n_LENGTH_M_LOG` | 長さまたは第一寸法、メートル. |
| `332n` | `AI_332n_WIDTH_M_LOG` | 幅、直径、または第二寸法、メートル. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | 奥行き、厚さ、高さ、または第三寸法、メートル. |
| `334n` | `AI_334n_AREA_M_LOG` | 面積、平方メートル. |
| `335n` | `AI_335n_VOLUME_L_LOG` | 物流容積、リットル. |
| `336n` | `AI_336n_VOLUME_M_LOG` | 物流容積、立方メートル. |
| `337n` | `AI_337n_KG_PER_M` | 1平方メートルあたりのキログラム. |

### 数量と計量 — 変量計量（ヤード・ポンド法／米国）

| ファミリー | 定数のかたち（`n` = 小数位の桁） | 説明 |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | 正味重量、ポンド（変量取引単位）. |
| `321n` | `AI_321n_LENGTH_IN` | 長さまたは第一寸法、インチ（変量取引単位）. |
| `322n` | `AI_322n_LENGTH_FT` | 長さまたは第一寸法、フィート（変量取引単位）. |
| `323n` | `AI_323n_LENGTH_YD` | 長さまたは第一寸法、ヤード（変量取引単位）. |
| `324n` | `AI_324n_WIDTH_IN` | 幅、直径、または第二寸法、インチ（変量取引単位）. |
| `325n` | `AI_325n_WIDTH_FT` | 幅、直径、または第二寸法、フィート（変量取引単位）. |
| `326n` | `AI_326n_WIDTH_YD` | 幅、直径、または第二寸法、ヤード（変量取引単位）. |
| `327n` | `AI_327n_HEIGHT_IN` | 奥行き、厚さ、高さ、または第三寸法、インチ（変量取引単位）. |
| `328n` | `AI_328n_HEIGHT_FT` | 奥行き、厚さ、高さ、または第三寸法、フィート（変量取引単位）. |
| `329n` | `AI_329n_HEIGHT_YD` | 奥行き、厚さ、高さ、または第三寸法、ヤード（変量取引単位）. |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | 物流重量、ポンド. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | 長さまたは第一寸法、インチ. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | 長さまたは第一寸法、フィート. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | 長さまたは第一寸法、ヤード. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | 幅、直径、または第二寸法、インチ. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | 幅、直径、または第二寸法、フィート. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | 幅、直径、または第二寸法、ヤード. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | 奥行き、厚さ、高さ、または第三寸法、インチ. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | 奥行き、厚さ、高さ、または第三寸法、フィート. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | 奥行き、厚さ、高さ、または第三寸法、ヤード. |
| `350n` | `AI_350n_AREA_IN` | 面積、平方インチ（変量取引単位）. |
| `351n` | `AI_351n_AREA_FT` | 面積、平方フィート（変量取引単位）. |
| `352n` | `AI_352n_AREA_YD` | 面積、平方ヤード（変量取引単位）. |
| `353n` | `AI_353n_AREA_IN_LOG` | 面積、平方インチ. |
| `354n` | `AI_354n_AREA_FT_LOG` | 面積、平方フィート. |
| `355n` | `AI_355n_AREA_YD_LOG` | 面積、平方ヤード. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | 正味重量、トロイオンス（変量取引単位）. |
| `357n` | `AI_357n_NET_VOLUME_OZ` | 正味重量（または容積）、オンス（変量取引単位）. |
| `360n` | `AI_360n_NET_VOLUME_QT` | 正味容積、クォート（変量取引単位）. |
| `361n` | `AI_361n_NET_VOLUME_GAL` | 正味容積、米ガロン（変量取引単位）. |
| `362n` | `AI_362n_VOLUME_QT_LOG` | 物流容積、クォート. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | 物流容積、米ガロン. |
| `364n` | `AI_364n_NET_VOLUME_IN` | 正味容積、立方インチ（変量取引単位）. |
| `365n` | `AI_365n_NET_VOLUME_FT` | 正味容積、立方フィート（変量取引単位）. |
| `366n` | `AI_366n_NET_VOLUME_YD` | 正味容積、立方ヤード（変量取引単位）. |
| `367n` | `AI_367n_VOLUME_IN_LOG` | 物流容積、立方インチ. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | 物流容積、立方フィート. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | 物流容積、立方ヤード. |

### 価格と金額

4 桁目（`n`）が暗黙の小数位の桁数を符号化します。許される範囲は
ファミリーごとに異なります——`n` の列をご覧ください。

| ファミリー | 定数のかたち（`n` = 小数位の桁） | `n` | 説明 |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | 支払対象金額またはクーポン額面、現地通貨. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | ISO通貨コードによる支払対象金額. |
| `392n` | `AI_392n_PRICE` | 0–9 | 支払対象金額、単一通貨圏（変量取引単位）. |
| `393n` | `AI_393n_PRICE` | 0–9 | ISO通貨コードによる支払対象金額（変量取引単位）. |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | クーポンの割引率. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | 計量単位あたりの支払対象金額、単一通貨圏（変量取引単位）. |

### 場所と出荷

| AI | 定数 | 説明 |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | 顧客発注番号. |
| `401` | `AI_401_GINC` | 混載貨物識別コード (GINC). |
| `402` | `AI_402_GSIN` | 出荷識別コード (GSIN). |
| `403` | `AI_403_ROUTE` | ルーティングコード. |
| `410` | `AI_410_SHIP_TO_LOC` | 出荷先／配送先ロケーションコード (GLN). |
| `411` | `AI_411_BILL_TO` | 請求先ロケーションコード (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | 購入元ロケーションコード (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | 転送先ロケーションコード (GLN)（〜のために出荷／配送）. |
| `414` | `AI_414_LOC_NO` | 物理的ロケーションの識別 - ロケーションコード (GLN). |
| `415` | `AI_415_PAY_TO` | 請求当事者のロケーションコード (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | 製造またはサービス提供場所のロケーションコード (GLN). |
| `417` | `AI_417_PARTY` | 当事者ロケーションコード (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | 単一の郵便当局内における出荷先／配送先郵便番号. |
| `421` | `AI_421_SHIP_TO_POST` | ISO国コード付き出荷先／配送先郵便番号. |
| `422` | `AI_422_ORIGIN` | 取引単位の原産国. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | 一次加工国. |
| `424` | `AI_424_COUNTRY_PROCESS` | 加工国. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | 解体国. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | 全工程を網羅する国. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | 原産地の国内区分. |
| `4300` | `AI_4300_SHIP_TO_COMP` | 配送先会社名. |
| `4301` | `AI_4301_SHIP_TO_NAME` | 配送先担当者. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | 配送先住所1行目. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | 配送先住所2行目. |
| `4304` | `AI_4304_SHIP_TO_SUB` | 配送先郊外地区. |
| `4305` | `AI_4305_SHIP_TO_LOC` | 配送先地域. |
| `4306` | `AI_4306_SHIP_TO_REG` | 配送先地方. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | 配送先国コード. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | 配送先電話番号. |
| `4309` | `AI_4309_SHIP_TO_GEO` | 配送先GEOロケーション. |
| `4310` | `AI_4310_RTN_TO_COMP` | 返品先会社名. |
| `4311` | `AI_4311_RTN_TO_NAME` | 返品先担当者. |
| `4312` | `AI_4312_RTN_TO_ADD1` | 返品先住所1行目. |
| `4313` | `AI_4313_RTN_TO_ADD2` | 返品先住所2行目. |
| `4314` | `AI_4314_RTN_TO_SUB` | 返品先郊外地区. |
| `4315` | `AI_4315_RTN_TO_LOC` | 返品先地域. |
| `4316` | `AI_4316_RTN_TO_REG` | 返品先地方. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | 返品先国コード. |
| `4318` | `AI_4318_RTN_TO_POST` | 返品先郵便番号. |
| `4319` | `AI_4319_RTN_TO_PHONE` | 返品先電話番号. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | サービスコードの説明. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | 危険物フラグ. |
| `4322` | `AI_4322_AUTH_LEAVE` | 置き配許可. |
| `4323` | `AI_4323_SIG_REQUIRED` | 署名要求フラグ. |
| `4330` | `AI_4330_MAX_TEMP_F` | 華氏での最高温度（度の100分の1単位で表示）. |
| `4331` | `AI_4331_MAX_TEMP_C` | 摂氏での最高温度（度の100分の1単位で表示）. |
| `4332` | `AI_4332_MIN_TEMP_F` | 華氏での最低温度（度の100分の1単位で表示）. |
| `4333` | `AI_4333_MIN_TEMP_C` | 摂氏での最低温度（度の100分の1単位で表示）. |

### 商品の属性とトレーサビリティ

| AI | 定数 | 説明 |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO在庫番号 (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECEによる食肉枝肉・部分肉分類. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | 有効力価. |
| `7005` | `AI_7005_CATCH_AREA` | 漁獲海域. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | 漁業目的の魚種. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | 漁具の種類. |
| `7010` | `AI_7010_PROD_METHOD` | 生産方法. |
| `7020` | `AI_7020_REFURB_LOT` | 再生ロットID. |
| `7021` | `AI_7021_FUNC_STAT` | 機能ステータス. |
| `7022` | `AI_7022_REV_STAT` | 改訂ステータス. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | 組立品の個別資産識別コード (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | 加工者番号。3 桁の ISO 国コード付き（10 枠）。. |
| `7040` | `AI_7040_UIC_EXT` | 拡張子1およびインポーター指標付きGS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACTによる貨物ユニット種別. |

### 各国の医療償還番号（NHRN）

| AI | 定数 | 説明 |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | 各国医療償還番号 (NHRN) - ドイツ PZN. |
| `711` | `AI_711_NHRN_CIP` | 各国医療償還番号 (NHRN) - フランス CIP. |
| `712` | `AI_712_NHRN_CN` | 各国医療償還番号 (NHRN) - スペイン CN. |
| `713` | `AI_713_NHRN_DRN` | 各国医療償還番号 (NHRN) - ブラジル DRN. |
| `714` | `AI_714_NHRN_AIM` | 各国医療償還番号 (NHRN) - ポルトガル AIM. |
| `715` | `AI_715_NHRN_NDC` | 各国医療償還番号 (NHRN) - アメリカ合衆国 NDC. |
| `716` | `AI_716_NHRN_AIC` | 各国医療償還番号 (NHRN) - イタリア AIC. |
| `717` | `AI_717_NHRN_SRN` | 各国医療償還番号 (NHRN) - コスタリカ 衛生登録番号. |

### ヘルスケア、GMN、HIDRI、CPID、個人データ

| AI | 定数 | 説明 |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | 認証番号（10 枠）。. |
| `7240` | `AI_7240_PROTOCOL` | プロトコルID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDCメディアタイプ. |
| `7242` | `AI_7242_VCN` | バージョン管理番号 (VCN). |
| `7250` | `AI_7250_DOB` | 生年月日 (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | 生年月日および出生時刻 (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | 生物学的性別. |
| `7253` | `AI_7253_FAMILY_NAME` | 氏（姓）. |
| `7254` | `AI_7254_GIVEN_NAME` | 名. |
| `7255` | `AI_7255_SUFFIX` | 氏名のサフィックス. |
| `7256` | `AI_7256_FULL_NAME` | 氏名（フルネーム）. |
| `7257` | `AI_7257_PERSON_ADDR` | 個人の住所. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | 出生順位（複数出生時）. |
| `7259` | `AI_7259_BABY` | 氏（姓）の対象となる新生児. |
| `8001` | `AI_8001_DIMENSIONS` | ロール製品（幅、長さ、コア径、巻き方向、継ぎ目数）. |
| `8002` | `AI_8002_CMT_NO` | 携帯電話識別番号. |
| `8003` | `AI_8003_GRAI` | 回収可能資産識別コード (GRAI). |
| `8004` | `AI_8004_GIAI` | 個別資産識別コード (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | 計量単位あたりの価格. |
| `8006` | `AI_8006_ITIP` | 個々の取引単位ピースの識別 (ITIP). |
| `8007` | `AI_8007_IBAN` | 国際銀行口座番号 (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | 製造日時 (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | 光学読取可能センサーインジケーター. |
| `8010` | `AI_8010_CPID` | コンポーネント／部品識別子 (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | コンポーネント／部品識別子のシリアル番号 (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | ソフトウェアバージョン. |
| `8013` | `AI_8013_GMN` | モデル識別コード (GMN). |
| `8014` | `AI_8014_MUDI` | 高度個別化機器登録識別子 (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | サービスを提供する組織とサービス提供者との関係を識別するサービス関係識別コード (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | サービスを提供する組織とサービス受給者との関係を識別するサービス関係識別コード (GSRN). |
| `8019` | `AI_8019_SRIN` | サービス関係インスタンス番号 (SRIN). |
| `8020` | `AI_8020_REF_NO` | 振込用紙参照番号. |
| `8026` | `AI_8026_ITIP_CONTENT` | 物流単位に含まれる取引単位ピース (ITIP) の識別. |
| `8030` | `AI_8030_DIGSIG` | デジタル署名 (DigSig). |
| `8040` | `AI_8040_IMEI` | 国際移動体装置識別番号 (IMEI). |
| `8041` | `AI_8041_IMEI2` | 国際移動体装置識別番号2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | 組込みSIM番号. |
| `8043` | `AI_8043_PSIM` | 物理SIM番号. |
| `8110` | `AI_8110` | 北米で使用するクーポンコード識別. |
| `8111` | `AI_8111_POINTS` | クーポンのロイヤルティポイント. |
| `8112` | `AI_8112` | 北米で使用するポジティブオファーファイルクーポンコード識別. |
| `8200` | `AI_8200_PRODUCT_URL` | 拡張パッケージングURL. |

### 社内用途

| AI | 定数 | 説明 |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | 取引先間で相互に合意した情報. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | 社内情報（9 枠）。. |

---

## 付録 B — 解釈キーの定数

`GaiaParser.parse()` を `ParseMode.INTERPRETATION` で呼ぶと、各 `GS1AIObjectElement` は分野別のエンリッチャーが生成した `GS1AIInterpretation` オブジェクトの一覧を持つことがあります。特定の解釈値を引くには、`GS1Constants_Enricher`（パッケージ `tools.pantheum.gaia.gs1.constants`）の定数をキーとしてお使いください。

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

表示ラベルは定数では **ありません**。`gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` にあるローカライズ済みカタログに、型定数をキーとして収められています。`GS1AIInterpretation.getLabel()` は解析言語に対応するラベルを返し（[ローカライズされたメッセージとラベル](#ローカライズされたメッセージとラベル)を参照）、カタログにそのキーがない場合は英語に立ち返ります。下表の「表示ラベル」列に示すのは、カタログに同梱されているとおりの日本語テキストです。型キー自体は言語をまたいで一定ですので、照合には常にキーを用い、ラベルを用いないでください。

### 日付と時刻

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `DATE_VALUE` | 日付 | 日付系の AI（11–17、7003、7006、7011 など） |
| `DATE_FORMAT` | 日付フォーマット | 日付系の AI |
| `TIME_VALUE` | 時刻 | 時刻を伴う AI（7003、7011、8008 など） |
| `TIME_FORMAT` | 時刻フォーマット | 時刻を伴う AI |
| `DATETIME_VALUE` | 日時 | 日付と時刻の AI |
| `DATETIME_FORMAT` | 日時フォーマット | 日付と時刻の AI |

### 収穫日

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | 収穫開始日 | AI 7007 |
| `HARVEST_END_DATE` | 収穫終了日 | AI 7007（任意の期間終了日） |
| `HARVEST_DATE_RANGE` | 収穫日範囲 | AI 7007 |

### GS1 事業者コード

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 事業者コード | GTIN / GLN / SSCC 系の AI |
| `GS1_MEMBER_CODE` | GS1 メンバーコード | GTIN / GLN / SSCC 系の AI |
| `GS1_MEMBER_NAME` | GS1 加盟組織 | GTIN / GLN / SSCC 系の AI |

### GTIN

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN タイプ | AI 01、02 |
| `GTIN_NATIVE` | GTIN | AI 01、02 |
| `PACKAGING_LEVEL` | 包装レベル | AI 01 |
| `GTIN_CHECK_DIGIT` | チェックデジット | AI 01、02 |

### SSCC

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | 拡張桁 | AI 00 |
| `SSCC_SERIAL_REFERENCE` | シリアル参照番号 | AI 00 |
| `SSCC_CHECK_DIGIT` | チェックデジット | AI 00 |

### 国（ISO 3166）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | 国コード（数値） | 単一国の AI（422、424–426、4307、4317、421、7030–7039） |
| `COUNTRY_CODE_ALPHA2` | 国コード（アルファ2） | 2 文字国コード系の AI |
| `COUNTRY_NAME` | 国名 | 単一国の AI |
| `COUNTRY_LIST` | 国 | AI 423 —— すべての名称を連結。たとえば `Australia, New Zealand` |

AI 423（一次加工国）は最大五か国を保持できるため、国ごとに
**番号付きの組** を出力します——`COUNTRY_CODE_NUMERIC_1`、`COUNTRY_NAME_1`、
`COUNTRY_CODE_NUMERIC_2`、`COUNTRY_NAME_2`、……——その後に取りまとめの
`COUNTRY_LIST` が一つ続きます。これらのキーは定数 `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` に 1 から数えた添字を付けて組み立てるか、単に `getInterpretations()` をたどってください。
接尾辞のない `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` は AI 423 では **出力されません**。

### 通貨（ISO 4217）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | 通貨コード | 通貨付きの金額系 AI（391n、393n） |
| `CURRENCY_ALPHA` | 通貨アルファコード | 通貨付きの金額系 AI |
| `CURRENCY_NAME` | 通貨名 | 通貨付きの金額系 AI |

### 温度

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `TEMPERATURE` | 温度 | AI 4330–4333 |
| `TEMPERATURE_UNIT` | 温度単位 | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | 温度（整形済み） | AI 4330–4333 |

### 性別（ISO 5218）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `SEX_CODE` | 性別コード | AI 7252 |
| `SEX_DESCRIPTION` | 性別の説明 | AI 7252 |

### 水生生物種（FAO）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `SPECIES_CODE` | 種コード | AI 7008 |
| `SPECIES_SCIENTIFIC` | 学名 | AI 7008 |
| `SPECIES_ENGLISH` | 一般名 | AI 7008 |
| `SPECIES_FAMILY` | 科 | AI 7008 |
| `SPECIES_ORDER` | 目 | AI 7008 |

### NATO 在庫番号（NSN）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `NSN_FSG` | 補給群 | AI 7001 |
| `NSN_FSG_NAME` | 補給群名 | AI 7001 |
| `NSN_FSCG` | 供給クラス | AI 7001 |
| `NSN_FSCG_NAME` | 補給分類名 | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | 国コード | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | 国 | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO国コード | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS区分 | AI 7001 |
| `NSN_NIIN` | 国家品目番号 | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### ロール製品

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | ロール幅 (mm) | AI 8001 |
| `ROLL_LENGTH` | ロール長 (m) | AI 8001 |
| `CORE_DIAMETER` | コア径 (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | 巻き方向コード | AI 8001 |
| `WINDING_DIRECTION` | 巻き方向 | AI 8001 |
| `SPLICES` | スプライス数 | AI 8001 |

### IBAN

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | 国コード | AI 8007 |
| `IBAN_COUNTRY_NAME` | 国 | AI 8007 |
| `IBAN_CHECK_DIGITS` | チェックデジット | AI 8007 |
| `IBAN_CHECK_VALID` | チェック | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040、8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040、8041 |
| `IMEI_SERIAL` | シリアル番号 | AI 8040、8041 |
| `IMEI_CHECK_DIGIT` | チェックデジット | AI 8040、8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040、8041 |
| `IMEI_RBI_NAME` | 割当機関 | AI 8040、8041 |

15 桁は `[ TAC（8 桁）][ シリアル番号（6 桁）][ Luhn チェックデジット（1 桁）]` に分解され、
RBI は TAC の先頭 2 桁にあたります——つまり `IMEI_RBI` は `IMEI_TAC` の接頭部であって、
別の区間ではありません。`IMEI_FORMATTED` は GSMA 標準の表示用の区切り方
`AA-BBBBBB-CCCCCC-D`（たとえば `49-015420-323751-8`）を返します。これは TAC を RBI の
境目で切ります。廃止された Final Assembly Code の始まる位置で切っていた旧来の
`6-2-6-1` の区切り方は出力されません。

`IMEI_RBI_NAME` は `ImeiRbiData` を介して RBI を割当機関の名称に対応づけ、
**常に最後に、しかもそのコードが表に載っている場合にのみ** 付加されます。この表は三つの区分を扱います。

- **現在も割当てを行っている** —— `01` CTIA/PTCRB、`35` TÜV SÜD BABT、`86` TAF、加えて `99`
  Global Hexadecimal Administrator と `98`（予約）。
- **試験用の範囲** —— `00` および `02`–`09`。実際の割当てではなく試験用 IMEI であることを示します。
  `ImeiRbiData.isTestCode(code)` で調べられます。
- **割当てを終えている** —— `49`（BZT/BAPT、ドイツ）、`44`
  （BABT、英国）、`91`（MSAI、インド）といった往時の機関。`ImeiRbiData.isNoLongerAllocating(code)` で調べられます。
  これらのコードを持つ端末はごく普通のもので、現に使われ続けています。終わったのは新規の割当てだけであり、
  したがってこれは報告のための情報であって、有効性の徴ではまったくありません。

`IMEI_RBI_NAME` がないことは「この RBI は当方の表にない」という意味であって、**「IMEI が無効」ではありません**。
この表は GSMA から直接ではなく公開された RBI の一覧をもとに編んだものであり、
新たに指定された機関に後れを取ることがあります。それがないことから有効性の判断を導かないでください。
RBI はチェックキャラクタではありません。解釈の一覧をたどるコードもまた、
位置で参照するのではなく、これがないことを許容しなければなりません。

### SIM の識別子（EID / ICCID）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042、8043 |
| `SIM_MII_NAME` | 業種区分 | AI 8042 |
| `EID_BODY` | EID 本体 | AI 8042 |
| `EID_CHECK_DIGIT` | チェックデジット | AI 8042 |
| `ICCID_BODY` | ICCID 本体 | AI 8043 |
| `ICCID_EXTENSION` | 拡張 | AI 8043 |

`SIM_MII` は先頭の **2 桁**（`89`）を保持します。ITU-T E.118 が電気通信に
割り当てている組合せです。一方 ISO/IEC 7812 自体は MII を **先頭 1 桁のみ** と定めているため、
`SIM_MII_NAME` はその先頭の `8` から `Iso7812Data` を用いて区分を求め、
「Healthcare, telecommunications and other future industry assignments」を返します。したがって適格な
EID ではこの値は一定です。標準への対応づけを追えるように報告しているのであって、
何かを区別するためのものではありません。`Iso7812Data.nameForCode(digit)` は 1 桁だけを取り、
`nameForIdentifier(prefix)` はより長い接頭辞を受け取ってその先頭の桁を読みます。

`SIM_MII_NAME` を出力するのは `EidEnricher`（AI 8042）だけです。`IccidEnricher`（AI 8043）は
区分なしで `SIM_MII` を示します。

### 認証番号

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | シーケンス番号 | AI 7230–7239 |
| `CERT_SCHEME_CODE` | 認証スキームコード | AI 7230–7239 |
| `CERT_SCHEME_NAME` | 認証スキーム | AI 7230–7239 |
| `CERT_REFERENCE` | 認証参照番号 | AI 7230–7239 |

### GS1 UIC

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC コード | AI 7040 |
| `UIC_EXTENSION_1` | 拡張 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | 輸入者インデックス | AI 7040 |

### 新生児の出生順

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | 出生順位（位置） | AI 7258 |
| `BIRTH_TOTAL` | 出生総数 | AI 7258 |
| `BIRTH_SEQUENCE` | 出生順序 | AI 7258 |

### グローバル型式番号（GMN）

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | モデル参照番号 | AI 8013 |
| `GMN_CHECK_PAIR` | チェックペア | AI 8013 |

### HIDRI

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | デバイス参照番号 | AI 8014 |
| `HIDRI_CHECK_PAIR` | チェックペア | AI 8014 |

### CPID

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | コンポーネント・部品参照番号 | AI 8010–8011 |

### 小数値と計量値

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | 小数値 | 暗黙の小数位を持つ数値系の AI（31xx–36xx） |
| `DECIMAL_AMOUNT` | 金額 | 価格系の AI（390n–395n） |
| `DECIMAL_PERCENTAGE` | パーセンテージ | AI 394n |
| `DECIMAL_PLACES` | 小数点以下の桁数 | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` と併せて |
| `PERCENTAGE_FORMAT` | パーセンテージ形式 | AI 394n |
| `ISO_UNIT_CODE` | ISO 単位コード | 計量系の AI |
| `ISO_UNIT_NAME` | ISO 単位名 | 計量系の AI |
| `MONETARY_AMOUNT` | 金額（通貨） | 価格系の AI |
| `MONETARY_AMOUNT_DISPLAY` | 金額（整形済み） | 価格系の AI |

### 地理座標

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `LATITUDE` | 緯度 | AI 4309 |
| `LONGITUDE` | 経度 | AI 4309 |
| `GEO_COORDINATES` | 地理座標 | AI 4309 |
| `LATITUDE_DMS` | 緯度 (DMS) | AI 4309 |
| `LONGITUDE_DMS` | 経度 (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | 地理座標 (DMS) | AI 4309 |

### 生産方法

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | 生産方法コード | AI 7010 |
| `PRODUCTION_METHOD` | 生産方法 | AI 7010 |

### AIDC メディア種別

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC メディアタイプコード | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC メディアタイプ | AI 7241 |

### 全体のうちの何個目か

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | ピース番号 | AI 8006 |
| `PIECE_TOTAL` | 総ピース数 | AI 8006 |
| `PIECE_OF_TOTAL` | 総数中のピース | AI 8006 |

### コンポーネントの分解

これらのキーは Java のエンリッチャーではなく、`content/ai-content.json` の宣言的な
コンポーネント分解によって出力され、複合的な AI 値の名前付きの部分を示します。本付録の
ほかのキーとは違い、これらには **`GS1Constants_Enricher` に対応する定数がありません**。
文字列そのものと照合するか、`GS1AIInterpretation.getType()` から型を読み取ってください。

| 型キー | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | チェックデジット | AI 253、255、402、410–417、8003、8017、8018 |
| `SERIAL_NUMBER` | シリアル番号 | AI 253、255、8003 |
| `POSTAL_CODE` | 郵便番号 | AI 421 |
| `PROCESSOR_ID` | 加工者識別子 | AI 7030–7039 |

ここでの `CHECK_DIGIT` はコンポーネント分解の汎用キーであり、上に挙げた
エンリッチャー固有のキー `GTIN_CHECK_DIGIT`、`SSCC_CHECK_DIGIT`、`IMEI_CHECK_DIGIT`、
`EID_CHECK_DIGIT` とは別のものである点にご注意ください。

### その他

| キー定数 | 表示ラベル | 生成元 |
|--------------|---------------|-------------|
| `FLAG_VALUE` | 値 | 真偽値・フラグ系の AI（4321–4323） |
| `DECODED_TEXT` | デコードされたテキスト | 自由記述の AI |
