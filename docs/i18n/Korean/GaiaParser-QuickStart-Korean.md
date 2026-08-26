# GaiaParser — 빠른 시작

GS1 바코드 페이로드를 구조화되고 검증된, 사람이 읽을 수 있는 데이터로 바꾸기까지
십 분 남짓. 여기는 지름길입니다. 온전한 참고 문서는
**[GaiaParser 개발자 안내서](GaiaParser-Korean.md)**이고, 그 반대 방향(요소 스트링과
Digital Link URI를 엮는 일)은 **[GaiaBuilder](GaiaBuilder-Korean.md)**가 다룹니다.

## 목차

1. [Gaia를 프로젝트에 더하기](#1-gaia를-프로젝트에-더하기)
2. [무언가 구문 분석하기](#2-무언가-구문-분석하기)
3. [결과 읽기](#3-결과-읽기)
4. [실패한 구문 분석 다루기](#4-실패한-구문-분석-다루기)
5. [발등을 찍을 두 가지](#5-발등을-찍을-두-가지)
6. [스캐너 접두어도 Digital Link도 그냥 됩니다](#6-스캐너-접두어도-digital-link도-그냥-됩니다)
7. [일을 덜기: 구문 분석 방식](#7-일을-덜기-구문-분석-방식)
8. [언어와 날짜 형식 바꾸기](#8-언어와-날짜-형식-바꾸기)
9. [어지러운 입력 손질하기](#9-어지러운-입력-손질하기)
10. [다음에 갈 곳](#10-다음에-갈-곳)

---

## 1. Gaia를 프로젝트에 더하기

Gaia는 Maven Central에 올려져 있지 않으므로, 코어를 한 번 빌드하여 로컬 저장소에
설치하십시오.

```bash
cd gaia && mvn install
```

그런 다음 의존성으로 두십시오.

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

손수 써야 할 의존성은 이것이 전부입니다. jar가 얇으므로 Gaia의 유일한 컴파일 범위
의존성인 `com.fasterxml.jackson.core:jackson-databind`가 딸려 옵니다. 빌드에서 이미
Jackson 판 번호를 못 박아 두셨다면 그쪽이 이기고 Gaia는 그것을 씁니다.
Gaia는 **Java 11**을 겨냥하며, 같은 jar가 그 뒤의 어떤 JVM에서도 그대로 돕니다.

> 시작하는 동안에는 코어의 테스트 묶음을 건너뛰면(`mvn install -DskipTests`) 몇 분이
> 몇 초로 줄어듭니다.

---

## 2. 무언가 구문 분석하기

클래스 하나, 설정은 없습니다.

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

`parse(String)`은 파이프라인 **전체**를 돌립니다. 구문, 내용 검증, 그리고 해석입니다.
그것이 옳은 기본값입니다. 좁힐 까닭을 재어 보고 나서 나중에 좁히십시오.

---

## 3. 결과 읽기

`ParseResult.getAiObject()`가 풀어낸 AI들을 지니고 있습니다. 특정한 것을 집을 때에는
자리가 아니라 코드로 집으십시오.

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

각 요소는 **해석** 목록을 지닙니다. 원시 숫자 뒤에 놓인, 해석 단계가 풀어낸 뜻입니다.

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()`은 지역화되며 보여 주기 위한 것입니다. 코드에서 값을 *읽으려면* 대신
바뀌지 않는 유형 키로 찾으십시오.

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI가 다르면 나오는 키도 다릅니다. GTIN은 회사 접두어와 GTIN 종류와 검사 숫자를 내놓고,
가격은 통화와 소수 금액을 내놓습니다. 온전한 목록은
[부록 B](GaiaParser-Korean.md#부록-b--해석-키-상수)에 있고, 상수는
`GS1Constants_Enricher`에 있습니다. 모든 AI에 해석이 있는 것은 아닙니다. 자유 서술인
배치/로트는 이끌어 낼 것이 없으므로 그 목록은 비어 있습니다.

---

## 4. 실패한 구문 분석 다루기

유효하지 않은 페이로드는 예외가 아니라 여느 결말입니다. `parse`는 잘못된 GS1 데이터를
두고 예외를 던지는 일이 없습니다.

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**메시지가 아니라 `getId()`로 갈래를 나누십시오.** 메시지는 지역화되며 그 표현은 약속이
아닙니다. 게다가 지금은 인용에 알려진 결함이 있습니다(위의 겹친 `''`). 이는
[오류 참조](GaiaParser-Korean.md#오류-참조)에 적어 두었습니다.

물음이 둘이면 메서드도 둘입니다.

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

어느 단계가 실패하면 구문 분석은 더 내려가기를 그만두므로, 검사 숫자가 틀리면 검증
오류는 얻되 해석은 얻지 못합니다.

### 경고가 결과를 무효로 만들지는 않습니다

어떤 검사는 알림일 뿐입니다. 알 수 없는 GS1 회사 접두어는 보고되지만, 페이로드는
구조상 여전히 멀쩡합니다.

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

둘 다 얻고 싶으면 `getIssues()`를 쓰십시오. 업무 흐름상 알 수 없는 접두어를 물리쳐야
한다면 `getWarnings()`를 명시적으로 살피십시오. `isValid()`가 대신해 주지 않습니다.

---

## 5. 발등을 찍을 두 가지

### GS 구분자, 그리고 그것을 빠뜨리는 일이 오류보다 나쁜 까닭

가변 길이 AI는 **GS 문자**(ASCII `0x1D`, 바코드 심볼로지에서는 FNC1이라고 합니다)나
문자열의 끝에 이를 때까지 이어집니다. 뒤에 다른 AI가 따라오면 이 구분자는
반드시 있어야 합니다.

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

빠뜨리면 오류가 나지 **않습니다**. 자신만만하게 틀린 답이 돌아옵니다.

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10`은 `X..20`이므로 `LOT-ABC21SN-98765`를 통째로 삼키는 것이 이치에 맞고, 그것이
뜻한 바가 아님을 파서가 알 길은 없습니다. 뒤에서 이를 되살릴 방법은 없으니 구분자는
발생하는 자리에서 바로잡으십시오. 스캐너의 바이트는 `0x1D`가 살아남도록
**ISO-8859-1**로 읽고, Java 문자열 리터럴에서는 `""`라고 적으십시오.
고정 길이 AI(`01`, `17`, `3103`)에는 구분자가 필요 없습니다. 파서가 그 길이를 알고
있으니까요.

### 대부분의 AI는 홀로 설 수 없습니다

배치/로트, 일련번호, 유효기한 따위는 *속성*입니다. GS1 General Specifications는 그것들이
식별 키와 함께 다닐 것을 요구하며, Gaia는 이를 강제합니다.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

GTIN을 더하면 통과합니다. 정말로 조각을 구문 분석해야 한다면 — 단위 테스트라든가
부분적으로 읽힌 것이라든가 — 그 검사를 끄십시오.

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. 스캐너 접두어도 Digital Link도 그냥 됩니다

입력이 어떤 꼴인지 Gaia에게 일러 줄 필요가 없습니다. 네 가지 꼴을 모두 알아봅니다.
스캐너가 준 것을 그대로 건네십시오.

**AIM 코드 ID 접두어**는 심볼로지를 알려 주며 저절로 떼어집니다.

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI**도 같은 검증과 보강을 거칩니다.

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

두 꼴 모두 같은 `GS1AIObject`에 이르므로, 스캔 결과를 받아 쓰는 코드는 어느 쪽이
왔는지 신경 쓸 필요가 없습니다. `toElementString()` / `getCanonicalDigitalLink()`가
둘 사이를 오갑니다.

**여덟 자리 상관 접두어**(`12345678~…`)도, 업무 흐름에서 쓰고 계시다면, 마찬가지로
떼어져 `getCorrelationInfo()`에 남습니다.

---

## 7. 일을 덜기: 구문 분석 방식

기본값은 모든 일을 합니다. 답의 일부만 있으면 되는 자리에서는 덜 청하십시오.

| 방식 | 무엇에 답하는가 | 값 |
|---|---|---|
| `DATA_CARRIER` | 이것은 어떤 심볼로지인가? | 가장 쌈 — AI 구문 분석을 아예 하지 않고 `getAiObject()`는 `null` |
| `SYNTAX` | AI 코드와 길이는 적격한가? | 검사 숫자도 해석도 없음 |
| `CONTENT` | 이것은 유효한 GS1 데이터인가? | 온전한 검증, 해석은 없음 |
| `INTERPRETATION` | 이것은 무엇을 뜻하는가? | **기본값** — 모든 것 |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

대량으로 검증하면서 풀어 놓은 내용을 보여 줄 일이 없다면 `CONTENT`를, 스캔 결과를 알맞은
처리기로 보내기만 하면 된다면 `DATA_CARRIER`를 집으십시오.

---

## 8. 언어와 날짜 형식 바꾸기

오류 메시지, 해석 이름표, AI 설명은 **35개 언어**로 번역되어 있습니다. 날짜는 원하시는
대로 그려집니다. 이 모두가 바뀌지 않는 `ParseConfig` 하나에 담깁니다.

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

값이 지역화되는 일은 결코 없습니다 — 지역화되는 것은 이름표와 설명과 메시지뿐입니다 —
그러므로 `"2026-12-31"`도 `"09506000134352"`도 어느 언어에서나 같은 뜻입니다. 설정은
시작할 때 한 번 지어 두고 나눠 쓰십시오. 바뀌지 않습니다.

---

## 9. 어지러운 입력 손질하기

보내오는 쪽이 인쇄된 HRI 괄호나 군더더기 빈칸을 내놓는다면, 코어에 담겨 오는 두
**입력 수정자**가 구문 분석에 앞서 페이로드를 고쳐 줍니다.

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

기본값으로는 아무것도 켜져 있지 않으며, 둘 다 단서가 붙습니다. 빈칸도 괄호도 적법한
GS1 데이터 문자이므로, 잘 아는 출처에만 적용하십시오.
[내장 수정자](GaiaParser-Korean.md#내장-수정자)를 보십시오. 괄호를 떼어 낸 뒤 그것이
함축하던 구분자를 왜 되살려야 하는지도 거기에 적어 두었습니다.

---

## 10. 다음에 갈 곳

- **[GaiaParser 개발자 안내서](GaiaParser-Korean.md)** — 파이프라인의 세부, 결과 모형 전체,
  모든 오류 코드, 그리고 AI와 해석 키의 부록.
- **[GaiaBuilder 개발자 안내서](GaiaBuilder-Korean.md)** — AI와 값의 쌍으로 요소 스트링과
  Digital Link URI 짓기.
- **[Gaia API HTTP 참조](../../gaia-api-reference.md)** — 라이브러리를 품고 싶지 않으시다면,
  같은 엔진을 HTTP 너머로.
- **[ai-codes.txt](../../ai-codes.txt)** — 빠르게 찾아보기 위한 `(AI) TITLE` 평면 목록.

### 다섯 줄짜리 판

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
