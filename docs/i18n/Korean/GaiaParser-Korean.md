# GAIA(GS1 Application Identifiers Analyser) — 개발자 안내서

## 목차

1. [개요](#개요)
2. [GS1과 General Specifications에 대하여](#gs1과-general-specifications에-대하여)
3. [GS1 응용 식별자](#gs1-응용-식별자)
4. [빠른 시작](#빠른-시작)
5. [구문 분석 파이프라인](#구문-분석-파이프라인)
   - [앞단계 — 입력 수정자](#앞단계--입력-수정자)
   - [단계 0 — 상관 ID](#단계-0--상관-id)
   - [단계 1 — 입력 갈래 나누기](#단계-1--입력-갈래-나누기)
   - [단계 2 — 구문](#단계-2--구문)
   - [단계 3 — 내용](#단계-3--내용)
   - [단계 4 — 해석](#단계-4--해석)
6. [구문 분석 설정(`ParseConfig`)](#구문-분석-설정parseconfig)
   - [설정값](#설정값)
   - [지역화된 메시지와 이름표](#지역화된-메시지와-이름표)
   - [날짜 꾸미기](#날짜-꾸미기)
7. [입력 수정자](#입력-수정자)
   - [내장 수정자](#내장-수정자)
   - [수정자 쓰기](#수정자-쓰기)
   - [수정자 등록하기](#수정자-등록하기)
   - [수정자가 무엇을 했는지 살펴보기](#수정자가-무엇을-했는지-살펴보기)
   - [수정자의 실패 처리](#수정자의-실패-처리)
8. [구문 분석 방식](#구문-분석-방식)
   - [DATA_CARRIER 방식](#data_carrier-방식)
   - [SYNTAX 방식](#syntax-방식)
   - [CONTENT 방식](#content-방식)
   - [INTERPRETATION 방식(기본값)](#interpretation-방식기본값)
9. [상관 ID](#상관-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [결과 다루기](#결과-다루기)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry와 DataCarrierType](#datacarrierentry와-datacarriertype)
12. [오류 참조](#오류-참조)
13. [스레드 안전성](#스레드-안전성)
14. [부록 A — AI 문자열 상수](#부록-a--ai-문자열-상수)
    - [식별과 일련번호](#식별과-일련번호)
    - [날짜와 시각](#날짜와-시각)
    - [수량과 계량 — 가변 계량(미터법)](#수량과-계량--가변-계량미터법)
    - [수량과 계량 — 가변 계량(야드파운드법 / 미국)](#수량과-계량--가변-계량야드파운드법--미국)
    - [가격과 금액](#가격과-금액)
    - [장소와 배송](#장소와-배송)
    - [상품 속성과 추적](#상품-속성과-추적)
    - [국가별 보건의료 상환 번호(NHRN)](#국가별-보건의료-상환-번호nhrn)
    - [보건의료, GMN, HIDRI, CPID, 사람에 관한 데이터](#보건의료-gmn-hidri-cpid-사람에-관한-데이터)
    - [내부 / 회사 용도](#내부--회사-용도)
15. [부록 B — 해석 키 상수](#부록-b--해석-키-상수)
    - [날짜와 시각](#날짜와-시각)
    - [수확일](#수확일)
    - [GS1 회사 접두어](#gs1-회사-접두어)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [국가(ISO 3166)](#국가iso-3166)
    - [통화(ISO 4217)](#통화iso-4217)
    - [온도](#온도)
    - [성별(ISO 5218)](#성별iso-5218)
    - [수산 어종(FAO)](#수산-어종fao)
    - [NATO 재고 번호(NSN)](#nato-재고-번호nsn)
    - [롤 제품](#롤-제품)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM 식별자(EID / ICCID)](#sim-식별자eid--iccid)
    - [인증 참조 번호](#인증-참조-번호)
    - [GS1 UIC](#gs1-uic)
    - [출생 순서](#출생-순서)
    - [국제 모델 번호(GMN)](#국제-모델-번호gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [소수와 계량값](#소수와-계량값)
    - [지리 좌표](#지리-좌표)
    - [생산 방식](#생산-방식)
    - [AIDC 매체 종류](#aidc-매체-종류)
    - [전체 가운데 몇째](#전체-가운데-몇째)
    - [구성 요소 나누기](#구성-요소-나누기)
    - [그 밖의 것들](#그-밖의-것들)

---

## 개요

`GaiaParser`는 GS1 응용 식별자(AI) 요소 스트링을 구문 분석하는 진입점입니다. 스캐너의 원시 출력을 다음 형태 가운데 어느 것으로든 받아들이며, 해석된 모든 AI와 검증 오류, 그리고 선택적으로 사람이 읽을 수 있는 해석까지 담은 구조화된 `ParseResult`를 돌려줍니다.

- 단순한 AI 요소 스트링: `0109506000134352`
- AIM 심볼로지 식별자를 앞에 둔 요소 스트링: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- 위 가운데 어느 것이든, 앞에 8자리 상관 ID를 선택적으로 붙인 것: `12345678~0109506000134352`

**진입 클래스:** `tools.pantheum.gaia.GaiaParser`

> **Gaia가 처음이신가요?** **[GaiaParser 빠른 시작](GaiaParser-QuickStart-Korean.md)**부터 보시기 바랍니다. 의존성, 첫 구문 분석, 그리고 흔히 걸려 넘어지는 몇 가지를 십 분쯤이면 짚을 수 있습니다. 이 안내서는 완전한 참고 문서입니다.

> 그 반대 방향의 작업 — AI와 값의 쌍으로부터 적격한 요소 스트링과 Digital Link URI를 *만들어 내는* 일 — 은 **[GaiaBuilder — 개발자 안내서](GaiaBuilder-Korean.md)**에서 다룹니다.

---

## GS1과 General Specifications에 대하여

**GS1**은 공급망의 식별과 데이터 교환에 관한 개방형 표준을 개발하고 유지하는 국제 비영리 기관입니다. 그 표준은 유통, 보건의료, 물류, 급식을 비롯한 여러 산업에서 쓰이며, 소비재 포장의 상품 바코드에서부터 의약품 낱개 단위의 일련번호 추적에 이르기까지 폭넓게 걸쳐 있습니다.

이 파서가 구현하는 모든 사항의 전거는 **GS1 General Specifications**입니다. 이 단일 문서가 다음을 규정합니다.

- 모든 응용 식별자(AI) 코드와 그 데이터 명칭, 형식, 검증 규칙
- AI 요소 스트링을 구성하고 부호화하기 위한 구문 규칙
- 바코드 심볼로지 요건과 AIM 심볼로지 식별자의 배정
- 검사 숫자 및 검사 문자 알고리즘
- 두 자리 연도의 해석(이동 창 규칙)
- Data Matrix, QR Code, GS1-128, GS1 DataBar 등 데이터 캐리어의 사양

GS1 General Specifications는 해마다 개정됩니다. 현행판과 관련 자료는 다음에서 얻을 수 있습니다.

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA는 GS1 General Specifications의 **26.0판(2026년 1월 승인)**을 구현합니다.

GS1 Digital Link URI는 짝을 이루는 표준 **GS1 Digital Link: URI Syntax**의 규율을 받습니다. 이 표준은 기본 식별 키, 키 한정자의 순서, 그리고 파서가 Digital Link 입력에 적용하는 데이터 속성의 부호화 방식을 규정합니다.

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA는 GS1 Digital Link: URI Syntax 표준의 **1.7.0판(2026년 8월 승인)**을 구현합니다.

이 문서 전체에서 절 참조는 GS1 General Specifications를 가리킵니다(예: “Table 7-5”, “section 7.12”). 다만 Digital Link의 절 번호(예: “§4.9”, “§4.12”)는 예외로, GS1 Digital Link: URI Syntax 표준을 가리킵니다.

---

## GS1 응용 식별자

**GS1 응용 식별자(AI)** 는 두 자리에서 네 자리에 이르는 짧은 숫자 접두어로, 바로 뒤에 오는 데이터의 뜻과 형식을 정합니다. AI는 GS1 General Specifications에 정의되어 있으며, 상품 식별자·날짜·수량·로트 번호·일련번호·계량값·URL 등 공급망 데이터를 폭넓게 아우릅니다.

### AI 요소의 구성

각 AI 요소는 두 부분으로 이루어집니다.

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

AI 코드는 언제나 숫자입니다. 데이터 값이 그 뒤에 곧바로 이어지며, 코드와 값 사이에는 어떠한 구분자도 없습니다.

### 고정 길이 AI와 가변 길이 AI

AI는 두 갈래로 나뉩니다.

| 종류 | 동작 | 예 |
|---|---|---|
| **고정 길이** | 문자 수가 정확히 정해져 있으며 언제나 그만큼 읽어 들입니다 | AI `01`(GTIN) — 언제나 14자리 |
| **가변 길이** | 1자에서 상한까지. GS 구분자 또는 입력의 끝에서 마칩니다 | AI `10`(로트) — 영숫자 1~20자 |

AI가 고정 길이인지 가변 길이인지는 오로지 GS1 규격의 정의에 따릅니다. 파서가 짐작하는 일은 없습니다.

### AI가 여럿 담긴 요소 스트링

여러 AI를 하나의 요소 스트링으로 이어 붙일 수 있습니다. 고정 길이 AI는 파서가 읽어야 할 문자 수를 언제나 정확히 알고 있으므로 그대로 이어 붙여도 됩니다. 가변 길이 AI는 뒤에 다른 AI가 이어질 때마다 반드시 **GS 문자**(ASCII `0x1D`, 바코드 심볼로지에서는 FNC1이라고도 합니다)로 마쳐야 합니다. 그래야 값이 어디서 끝나고 다음 AI 코드가 어디서 시작하는지를 파서가 가려낼 수 있습니다.

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

Java 문자열 리터럴에서는 GS 문자를 유니코드 이스케이프 `""`로 적습니다.

### 자주 쓰이는 AI

| AI | 데이터 명칭 | 형식 | 값의 예 |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6(YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6(YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500`(= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234`(= 912.34, 단일 통화권) |
| `710` | NHRN PZN | X..20 | `12345678` |

> 네 자리 계량·가격 AI의 **넷째 자리**는 묵시적 소수 자릿수를 나타냅니다. `3103`은 소수 세 자리의 킬로그램 단위 정미 중량(`001500` = 1.500 kg)이며, `3102`라면 같은 숫자를 15.00 kg으로 읽습니다. 위 표의 `형식` 열이 보여 주는 것은 *데이터*의 형식입니다. 각 AI의 온전한 `getFormatString()`에는 AI 자체도 들어갑니다(예컨대 `3103`이면 `N4+N6`).

### 사람이 읽을 수 있는 해석(HRI)

관행적인 가독 형식은 각 AI 코드를 괄호로 묶어 그 값 바로 앞에 두고, 요소와 요소 사이를 빈칸으로 띄웁니다.

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS 구분자는 HRI에 나타나지 않습니다. 이 형식은 `GS1AIObject.toHriString()`이 만들어 냅니다.

### 네 자리 AI 코드

일부 AI는 두 자리가 아니라 네 자리를 씁니다. 앞의 두 자리가 AI 계열을 나타내고, 셋째 자리와/또는 넷째 자리가 부가적인 의미를 지닙니다(계량 AI의 묵시적 소수점 위치 따위). 파서는 요소 스트링에서 온전한 AI 코드를 스스로 알아냅니다. 호출하는 쪽은 언제나 온전한 코드를 다룹니다(예컨대 `"31"`이 아니라 `"3102"`).

---

## 빠른 시작

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

> **GS 구분자:** AI가 여럿 담긴 스트링에서 가변 길이 AI는 반드시 GS 문자(ASCII `0x1D`)로 구분해야 합니다. Java 문자열 리터럴에서는 `""`를 쓰십시오.

---

## 구문 분석 파이프라인

### 앞단계 — 입력 수정자

`ParseConfig`에 **입력 수정자**가 담겨 있으면 그것들이 무엇보다 먼저 실행됩니다. 상관 ID를 떼어 내기 전, 데이터 캐리어를 가려내기 전, GS1 파이프라인에 들어서기도 전입니다. 각 수정자는 다음 수정자를 위해 원시 입력을 고쳐 쓰며, 아래의 모든 단계는 그 사슬의 출력을 대상으로 삼습니다.

기본값으로는 수정자가 하나도 설정되어 있지 않으므로, 명시적으로 켜기 전까지 이 앞단계는 아무 일도 하지 않습니다. [입력 수정자](#입력-수정자)를 보십시오.

---

### 단계 0 — 상관 ID

GS1 처리에 들어가기에 앞서 `GaiaParser`는 입력이 선택적인 **상관 ID 접두어**로 시작하는지 살핍니다. 10진 ASCII 숫자 정확히 8자리 뒤에 물결표(`~`)가 오는 형태로, 이를테면 `12345678~`입니다.

접두어가 있으면 그것을 떼어 내어, 돌려주는 `ParseResult`에 `CorrelationInfo`로 담아 둡니다. 이후의 모든 단계는 그렇게 떼어 낸 페이로드를 대상으로 삼습니다. 접두어가 없으면 입력은 그대로 지나갑니다.

자세한 내용은 [상관 ID](#상관-id)를 보십시오.

---

### 단계 1 — 입력 갈래 나누기

상관 ID를 떼어 낸 뒤, `GaiaParser`는 (떼어 낸) 입력이 **AIM 심볼로지 식별자**로 시작하는지 살핍니다. 이는 `]` + ASCII 영문자 + ASCII 숫자 꼴의 세 글자 접두어입니다(예컨대 GS1-128은 `]C1`, GS1 DataMatrix는 `]d2`, GS1 DataBar / GS1 Composite는 `]e0`).

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

데이터 캐리어가 GS1 AI를 담을 수 없는 것이면(예컨대 우편 바코드), 구문 분석은 곧바로 오류 `GE-D002`로 멎습니다.

---

### 단계 2 — 구문

언제나 실행됩니다. 두 하위 단계로 이루어집니다.

**2a. 토큰화(`AISyntaxParser`)**
- GS1 접두어 표(GS1 General Specifications 표 7-5)를 참고하여 앞의 두 글자에서 AI 코드의 길이를 읽어 냅니다.
- 고정 길이 AI는 입력에서 정확한 바이트 수만큼 읽어 들입니다.
- 가변 길이 AI는 GS 문자나 입력의 끝에 이를 때까지 읽습니다.
- 구성 요소가 여럿인 AI에서는 값 덩어리를 구성 요소별 구간으로 나눕니다.

**2b. 구조 검증(`SyntaxValidator`)**
- 중복된 AI를 찾아냅니다(`GE-S004`).
- 필수인 AI 의존 관계를 살핍니다. 이를테면 AI `02`는 AI `37`을 필요로 합니다(`GE-S005`).
- 함께 쓸 수 없는 AI 짝을 살핍니다(`GE-S006`).

이 단계의 오류는 등급이 `SYNTAX_ERROR`(토큰화) 또는 `INTEGRITY_ERROR`(구조)입니다. 토큰화든 구조든 **오류가 하나라도** 있으면 파이프라인은 멎고, 내용 단계와 해석 단계는 건너뜁니다.

---

### 단계 3 — 내용

단계 2가 오류를 하나도 내지 않은 경우에만 실행됩니다(토큰화와 구조 어느 쪽에서도). 요소마다 다음 흐름을 밟습니다(각 걸음은 바로 앞 걸음이 오류를 내지 않았을 때만 실행됩니다).

| 걸음 | 검증기 | 오류 코드 |
|---|---|---|
| 정규식 검사 | `RegexValidator` | `GE-C001` |
| 구성 요소의 문자 집합과 형식 | `ComponentValidator` | `GE-C005` + 조건별 형식 코드(`GE-C054`–`GE-C115`) |
| 검사 숫자 / 검사 문자 | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| 별도의 의미 검증 | `ContentValidatorRegistry` | 조건별 내용 코드(`GE-C116`–`GE-C170`) |

이 단계의 오류는 등급이 `FORMAT_ERROR` 또는 `DATA_ERROR`이나, 한 가지 예외가 있습니다.
GS1 키를 지닌 AI에 대한 GS1 회사 접두어 검사는 참고용일 뿐이며 등급은 `WARNING`입니다([오류 참조](#오류-참조)를 보십시오).
따라서 알려지지 않은 회사 접두어만으로 결과가
무효가 되지는 않습니다.

---

### 단계 4 — 해석

`INTERPRETATION` 방식에서만, 그리고 앞선 단계의 오류를 지닌 요소가 하나도 없을 때에만 실행됩니다. `InterpretationEngine`이 각 요소에 이름표가 붙은 메타데이터를 더합니다.

- `dd/mm/yyyy`로 다시 꾸민 날짜
- GTIN 검사 숫자의 분해와 GS1 회사 접두어 조회
- ISO 3166 국가명
- ISO 4217 통화명과 통화 기호
- 복호한 소수 금액
- HRI(사람이 읽을 수 있는 해석) 조각

그 결과는 각 `GS1AIObjectElement`에 `GS1AIInterpretation` 항목으로 붙습니다.

---

## 구문 분석 설정(`ParseConfig`)

`GaiaParser`가 내놓는 진입점은 정확히 둘입니다.

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)`은 **기본 설정**으로 돌아갑니다. `INTERPRETATION` 방식, 오름차순 날짜(`dd/mm/yyyy`)에 구분자 `/`와 네 자리 연도, 그리고 **영어** 오류 메시지입니다. 이 가운데 무엇이든 — 구문 분석 방식까지 포함해 — 바꾸려면, 이어 쓰기 방식의 빌더로 `ParseConfig`를 짓고 인자 두 개짜리 오버로드를 쓰십시오.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

설정값의 열거형은 모두 `GaiaConstants`에 있습니다.

### 설정값

| 빌더 메서드 | 열거형(`GaiaConstants`) | 기본값 | 효과 |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | 파이프라인의 깊이 — [구문 분석 방식](#구문-분석-방식)을 보십시오. |
| `language(...)`      | `Language`      | `ENGLISH`        | 오류 메시지, 해석 이름표, **그리고** AI 설명의 언어. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | 날짜 구성 요소의 순서: `LITTLE`(`dd/mm/yyyy`), `MIDDLE`(`mm/dd/yyyy`), `BIG`(`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | 날짜 구성 요소 사이의 문자: `SLASH`(`/`), `HYPHEN`(`-`), `PERIOD`(`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT`(`12`) 또는 `THREE_LETTER`(`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT`(`2026`) 또는 `TWO_DIGIT`(`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | 구조상의 “필요로 함” 검사(`GE-S005`)를 건너뜁니다. |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | 구조상의 “배제함” 검사(`GE-S006`)를 건너뜁니다. |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / 클래스 이름 | 없음 | 구문 분석에 앞서 원시 입력을 고쳐 쓰는 코드 — 두 [내장 수정자](#내장-수정자)와, 손수 쓰신 것들. [입력 수정자](#입력-수정자)를 보십시오. |

날짜에 관한 네 설정값은 해석 보강기가 만들어 내는 꾸며진 날짜 문자열에만 영향을 줄 뿐(`INTERPRETATION` 방식에서), 검증을 바꾸지는 않습니다. 빌더의 값은 빼도 됩니다. 지정하지 않은(또는 `null`을 넘긴) 설정값은 기본값 그대로입니다.

### 지역화된 메시지와 이름표

`language(...)`는 사람이 읽는 **세 가지** 텍스트의 언어를 고릅니다. 오류 메시지, 해석 이름표(각 `GS1AIInterpretation`의 `getLabel()`), 그리고 AI 설명(각 `GS1AIObjectElement`의 `getDescription()`)입니다.

`GaiaConstants.Language`는 세계에서 가장 많이 쓰이는 언어들을 아우르는 **35개 언어**를 정의합니다. 영어, 프랑스어, 스페인어, 독일어, 이탈리아어, 포르투갈어, 네덜란드어, 폴란드어, 러시아어, 우크라이나어, 체코어, 스웨덴어, 중국어, 일본어, 한국어, 아랍어, 인도네시아어, 힌디어, 튀르키예어, 벵골어, 우르두어, 베트남어, 나이지리아 피진어, 이집트 아랍어, 마라티어, 텔루구어, 타밀어, 광둥어, 우어, 타갈로그어, 페르시아어, 하우사어, 펀자브어, 자바어, 스와힐리어입니다.

번역 현황(배포 시점 기준):
- **해석 이름표** — 모든 언어로 번역되어 있습니다.
- **오류 메시지** — 모든 언어로 번역되어 있습니다.
- **AI 설명** — 영어를 뺀 모든 언어로 번역되어 있습니다. 영어는 별도의 목록을 이루지 않습니다. `gs1-application-identifiers.jsonld`에 있는 해당 AI 항목의 `description` 필드에서 곧바로 읽어 오며, 모든 AI 설명이 결국 여기로 되돌아옵니다.

나이지리아 피진어(`NIGERIAN_PIDGIN`)는 영어를 바탕으로 한 크리올어로, 해석 이름표와 오류 메시지에는 영어 텍스트를 일부러 그대로 씁니다. AI 설명은 그 예외의 예외입니다. AI 설명 목록은 이름표·메시지 목록과 별개로 만들어졌기에, 영어를 가져다 쓰지 않고 실제 피진어로 번역되어 있습니다. 기계 번역은 운영 환경에서 믿고 쓰기에 앞서 원어민의 검토를 거치는 것이 좋습니다.

어느 언어 목록에서든 빠져 있는 메시지·이름표·설명은 영어로 되돌아갑니다. 오른쪽에서 왼쪽으로 쓰는 언어(아랍어, 우르두어, 이집트 아랍어, 페르시아어)는 문자열로서 올바르게 저장되어 있습니다. 오른쪽에서 왼쪽으로 그려 내는 일은 표시 계층의 몫입니다.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

해석 이름표도 같은 방식으로 지역화됩니다(값 자체는 그대로이고 이름표만 바뀝니다).

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI 설명도 같은 방식으로 지역화됩니다(지역화되지 않는 것은 `getTitle()`, 이를테면 `"GTIN"`뿐입니다).

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### 날짜 꾸미기

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## 입력 수정자

**입력 수정자**란 Gaia가 구문 분석에 들어가기 전에 원시 입력 문자열을 고쳐 쓰는 코드입니다. 수정자는 이미 망가진 채 들어오는 입력을 위해 있습니다. GS 구분자를 인쇄 가능한 자리표로 바꿔 놓는 스캐너, 페이로드를 제조사 고유의 접두어로 감싸는 미들웨어, 무엇이든 대문자로 만들어 버리는 호스트 시스템 따위입니다. 호출하는 자리마다 문자열을 미리 손질하는(그러다 그 가운데 한 곳에서 미묘하게 어긋나는) 대신, 정규화를 `ParseConfig`에 한 번 등록해 두고 파서가 적용하도록 맡기십시오.

수정자는 `GaiaParser.parse(...)`의 맨 앞에서 실행됩니다. 상관 ID를 떼어 내기 전, AIM 코드 ID를 가려내기 전, GS1 파이프라인에 들어서기 전입니다. 그 뒤의 모든 것은 고쳐 쓰인 문자열만을 봅니다. 두 [내장 수정자](#내장-수정자)를 포함해 **기본값으로는 아무것도 설정되어 있지 않습니다**. `ParseConfig`마다 명시적으로 켜야 합니다.

**인터페이스:** `tools.pantheum.gaia.modifier.ModifierInterface`

### 내장 수정자

코어 jar에는 `tools.pantheum.gaia.modifier.custom`에 수정자가 둘 들어 있습니다. GS1 페이로드가 망가져 들어오는 가장 흔한 두 갈래 — 인쇄된 HRI 괄호가 데이터로 취급되는 것, 그리고 군더더기 빈칸 — 을 다루므로, 흔한 경우라면 손수 클래스를 쓸 일이 없습니다.

| 클래스 | `getName()` | 하는 일 |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | 각 AI를 감싼 HRI 괄호(`(01)…(10)…`)를 떼어 내고, 그 괄호가 함축하던 FNC1 구분자를 되살립니다. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI 요소 스트링에서 빈칸(`0x20`)을 모두 없앱니다. |

이 둘은 특별할 것 없는 평범한 `ModifierInterface` 구현체입니다. 등록되고, 순서가 매겨지고, 보고되고, 실패하는 방식이 손수 쓰신 것과 똑같습니다.

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

둘 다 상태를 지니지 않으며 스레드에 안전하므로 인스턴스 하나를 여럿이 나눠 쓸 수 있습니다. 또 둘 다 완전한 클래스 이름으로 지정할 수 있어 설정 파일로 꾸리는 환경에도 맞습니다([수정자 등록하기](#수정자-등록하기)를 보십시오).

#### `ModifierRemoveAIBrackets`

GS1의 사람이 읽을 수 있는 해석은 모든 AI를 괄호로 묶어 인쇄합니다 — `(01)09521234543213(10)ABC123`. 순전히 인쇄상의 관행입니다. HRI를 내보내도록 설정된 스캐너나 미들웨어는 그 괄호를 데이터로 흘려보내고, 토큰화기는 그것을 어찌해야 할지 알지 못합니다.

괄호를 떼어 내는 것만으로는 일의 절반밖에 되지 않습니다. HRI에서는 *다음* AI의 여는 `(`가 앞선 값의 끝을 나타내므로, 괄호 표기에서는 가변 길이 AI에 FNC1이 필요하지 않습니다. 아무 생각 없이 괄호만 걷어 내면 그 경계가 사라져 버립니다.

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

그래서 이 수정자는 **앞선 AI가 가변 길이인 모든 경계마다 FNC1을 다시 넣어**, 괄호가 부호화하고 있던 바를 고스란히 되살립니다.

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

길이는 파서 자신의 `AiDefinitionRegistry`에서 찾으므로, 미리 박아 넣은 목록이 아니라 모든 가변 길이 AI가 제대로 처리됩니다. 세 경우는 일부러 손대지 않습니다. 값이 이미 FNC1로 끝나는 경우(두 관행을 함께 내보내는 출처에 구분자를 두 번 넣지 않기 위해), 괄호에 담긴 코드가 알려진 AI가 아닌 경우(알 수 없는 AI는 그 길이에 대해 아무것도 말해 주지 않습니다), 그리고 문자열의 마지막 AI입니다.

이 고쳐 쓰기는 **멱등**합니다 — 자신의 출력에 다시 돌려도 아무것도 달라지지 않습니다 — 따라서 일부 입력만 괄호가 붙어 오는 뒤섞인 흐름에도 안전하게 쓸 수 있습니다.

> **한계.** `(`와 `)`는 그 자체로 적법한 GS1 데이터 문자이며, 이 수정자가 찾는 무늬는 그저 `\((\d{2,4})\)`일 뿐입니다. 어쩌다 괄호에 싸인 두 자리에서 네 자리 숫자를 값 안에 품고 있다면 그것 역시 벗겨집니다. 진짜로 괄호 친 값을 쓰는 출처가 아니라, HRI 괄호 관행을 쓰는 출처에만 적용하십시오.

#### `ModifierRemoveSpaces`

어떤 스캐너, 미들웨어, 라벨 인쇄 파이프라인은 멀쩡한 요소 스트링에 군더더기 빈칸을 끼워 넣습니다. 고정 폭 필드를 채우거나, 사람이 읽기 쉽도록 무리를 나누거나, 긴 값을 접기 위해서입니다. 토큰화기는 그 하나하나를 데이터로 다루므로, 그 빈칸이 들어앉은 값이 망가지고, 가변 길이 AI라면 그 뒤의 모든 것이 밀려납니다.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

없어지는 것은 ASCII `0x20`뿐입니다. 다른 공백 문자는 그대로 둡니다. 이를테면 탭은 GS1이 부호화할 수 있는 문자 집합 바깥에 있으므로, 슬그머니 쓸려 나가는 대신 파서가 `GE-S008`로 알려 줍니다.

> **한계.** 빈칸(`0x20`)은 GS1 불변 문자 집합에 속하므로, 배치/로트나 고객 부품 번호가 정당하게 빈칸을 품고 있을 수 있습니다. 이 수정자는 군더더기 빈칸과 진짜 빈칸을 가려내지 못합니다. AI 값 안에 빈칸을 쓰지 않는 것이 확실한 출처에만 적용하십시오.

#### 접두어는 고쳐 쓰지 않고 건너뜁니다

수정자는 파서가 아직 아무것도 떼어 내지 않은 시점에 실행되므로, 원시 입력에는 여전히 상관 ID와 AIM 코드 ID, ECI 지시자가 붙어 있을 수 있습니다. 두 내장 수정자는 파서 자신의 `CorrelationIdParser`와 `DataCarrierParser` 논리를 써서 AI 요소 스트링이 시작하는 자리를 찾아내고, 거기서부터만 고쳐 쓴 다음, 그 결과를 **손대지 않은** 접두어에 다시 이어 붙입니다.

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

값이 GTIN-14로 채워지는 EAN/UPC 캐리어(`isRequiresGtinPadding()`)는 통째로 건너뜁니다. 그 페이로드는 AI 구조가 없는 원시 숫자 바코드 값이므로, 괄호도 빈칸도 거기서는 아무 뜻이 될 수 없습니다.

#### 순서: 괄호보다 빈칸이 먼저

둘을 함께 쓸 때에는 **`ModifierRemoveSpaces`를 먼저 등록하십시오**. 괄호 맞추기는 자리에 민감합니다. 빈칸으로 벌어진 `( 01 )`은 `\((\d{2,4})\)`에 맞지 않으므로 괄호가 살아남고, 그것이 함축하던 구분자는 끝내 되살아나지 않습니다.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### 수정자 쓰기

내장 수정자 어느 쪽도 맞지 않을 때에는 손수 쓰십시오. 인터페이스에 메서드는 하나뿐입니다.

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

고쳐 쓰기가 구문 분석 설정에 따라 달라진다면, 대신 인자 두 개짜리 오버로드를 재정의하십시오.

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

지켜야 할 약속:

| 규칙 | 자세히 |
|---|---|
| 상태를 지니지 말고 스레드에 안전할 것 | 클래스마다 인스턴스 하나가 캐시되어 모든 구문 분석에 함께 쓰입니다. |
| 인자 없는 공개 생성자 | 수정자를 클래스 이름으로 지정할 때에만 필요합니다. |
| `null`과 빈 입력을 다룰 것 | 사슬이 돌기 전에 파서가 그런 것들을 걸러 내지 않습니다. |
| `null`을 돌려주면 “달라진 것 없음” | 앞선 값이 그대로 이어집니다. 수정자가 해당하지 않는 경우에는 `input`을 그대로 돌려주십시오. |
| 예외를 던지기보다 그대로 돌려주기를 | 예외를 던지는 수정자는 구문 분석을 중단시킵니다 — [실패 처리](#수정자의-실패-처리)를 보십시오. |
| `getName()` | `ModifierInfo`에 보고될 이름을 정하려면 재정의하십시오. 기본값은 단순 클래스 이름입니다. |

### 수정자 등록하기

수정자는 더해진 순서대로 실행되며, 저마다 앞선 것의 출력을 받습니다. 인스턴스로, 완전한 클래스 이름으로, 또는 그 어느 쪽의 목록으로 등록하십시오.

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

[내장 수정자](#내장-수정자)도 손수 쓰신 것과 똑같이 이름을 붙입니다 — **언제나 완전한 이름으로**. 내장 수정자를 위한 짧은 이름이나 별칭 조회 같은 것은 없습니다. `ModifierRegistry`는 함께 배포된 것이든 아니든 모든 수정자를 완전한 클래스 이름으로 찾습니다.

이름은 `ModifierRegistry`가 풀어냅니다. 각 클래스를 인자 없는 생성자로 한 번 만들어 두고, 같은 클래스를 지정하는 이후의 모든 설정에 그 인스턴스를 다시 씁니다. 이름을 푸는 일은 **설정을 지을 때** 일어나므로, 찾을 수 없거나 `ModifierInterface`를 구현하지 않았거나 인스턴스를 만들 수 없는 이름은 구문 분석 시점에 소리 없이가 아니라 바로 그 자리에서 `IllegalArgumentException`을 던집니다. 리플렉션으로 만들 수 없는 수정자(이를테면 주입된 의존성을 품은 것)는 미리 등록해 두면 이름으로도 지정할 수 있습니다.

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### 수정자가 무엇을 했는지 살펴보기

수정자가 설정되어 있으면 `ParseResult.getPayload()`는 **고쳐 쓰인** 입력을 나타냅니다. 원래 입력은 `ModifierInfo`에 남아 있습니다.

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()`는 각 수정자의 `getName()`을 알려 줍니다. 기본값은 단순 클래스 이름이나 두 내장 수정자는 이를 재정의해 두었으므로, 그 둘을 사슬로 이으면 클래스 이름이 아니라 표시용 이름이 나옵니다.

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

수정자가 하나도 설정되지 않았으면 `getModifierInfo()`는 `null`을 돌려줍니다. 수정자가 돌기는 했으나 모두가 입력을 그대로 돌려주었다면 정보는 있되 `isModified()`가 `false`입니다. `getAppliedModifiers()`에는 실제로 입력을 바꾼 수정자만 실립니다.

### 수정자의 실패 처리

예외를 던지는 수정자는 구문 분석을 중단시킵니다. 그 예외는 문제를 일으킨 수정자의 이름을 담은 `GaiaModifierException`으로 감싸이고, 결과에는 그 이름이 메시지에 들어간 `GE-I001` 내부 오류가 실립니다. `getPayload()`는 고쳐 쓰이지 않은 입력을 알려 줍니다. 구문 분석은 반쯤 고쳐 쓰인 문자열로 **일부러** 이어 가지 않습니다. 소리 없이 실패한 정규화 단계는 겉보기에는 멀쩡하되 엉뚱한 입력에서 나온 결과를 만들어 낼 테니까요.

---

## 구문 분석 방식

각 방식은 자신이 실행하는 가장 깊은 [파이프라인 단계](#구문-분석-파이프라인)의 이름을 딴 것이며, 그보다 앞선 단계는 모두 그대로 실행됩니다.

| 방식 | 어디까지 실행하는가 | 무엇에 답하는가 |
|---|---|---|
| `DATA_CARRIER` | 단계 1(입력 갈래 나누기) | 어떤 심볼로지가 이것을 실어 왔는가? |
| `SYNTAX` | 단계 2(구문) | AI 코드와 길이는 적격한가? |
| `CONTENT` | 단계 3(내용) | 값들은 유효한 GS1 데이터인가? |
| `INTERPRETATION` | 단계 4(해석) | 값들은 무엇을 뜻하는가? |

### DATA_CARRIER 방식

단계 1에서 멎습니다. AIM 코드 ID를 검증하고 심볼로지를 알아내되, AI 구문 분석 파이프라인에는 들어가지 않습니다. 온전한 검증에 드는 비용 없이 심볼로지를 알아내고 갈래를 나눌 때 쓸모가 있습니다.

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

**이럴 때 쓰십시오:** 페이로드를 어떻게 처리할지 정하기에 앞서 바코드의 종류를 알아내야 할 때 — 이를테면 1D와 2D 심볼로지를 서로 다른 처리기로 나눠 보낼 때입니다. 그렇게 갈래를 나눌 때에는 `getName()`을 문자열로 맞춰 보기보다, 유형이 지어진 [`DataCarrierType`](#datacarrierentry와-datacarriertype)(`getDataCarrier().getDataCarrierType()`)을 쓰십시오.

---

### SYNTAX 방식

단계 2에서 멎습니다. 내용 검증에 드는 비용 없이 구조를 미리 걸러 낼 때 쓸모가 있습니다.

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

**이럴 때 쓰십시오:** 온전한 검증에 들어서기 전에 AI 코드와 데이터 길이가 적격한지 확인하고 싶을 때, 또는 내용 오류가 드문 대량 스캔을 다룰 때입니다.

---

### CONTENT 방식

단계 3에서 멎습니다.

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

> 대부분의 AI는 홀로 설 수 없습니다. AI `10`(BATCH/LOT), `17`(USE BY or EXPIRY),
> `21`(SERIAL)은 저마다 같은 요소 스트링 안에 AI `01` 같은 식별 키를 *필요로* 합니다.
> 그러므로 위 예에서 GTIN을 빼면 내용 검증에 이르지도 못하고 단계 2에서 `GE-S005`로
> 실패합니다. 동반 AI를 일부러 뺀 조각을 구문 분석하려면 `ParseConfig`에
> `skipRequiresCheck(true)`를 지정하십시오.

**이럴 때 쓰십시오:** 스캔한 값을 업무 처리에 쓰기에 앞서 그것이 GS1을 온전히 따르는지 알아야 하되, 해석 보강의 비용은 치르고 싶지 않을 때입니다.

---

### INTERPRETATION 방식(기본값)

단계 4까지 파이프라인 전체를 실행합니다. 방식 인자 없이 `parse(String)`을 부를 때의 기본값입니다. 내용 검증을 깨끗이 통과한 요소만 보강합니다.

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

**출력의 예:**
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

**금액의 예(AI 3932 — ISO 통화 코드가 딸린 가격):**
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

**이럴 때 쓰십시오:** 표시 계층이나 라벨 검증 도구, 그 밖에 AI 값을 사람이 알아보기 좋게 풀어 보여야 하는 어떤 UI를 만들 때입니다.

---

## 상관 ID

어떤 업무 흐름은 스캔 사건을 세션이나 거래에 이어 붙일 수 있도록 원시 GS1 입력 앞에 여덟 자리짜리 고유 상관 식별자를 붙입니다. 그 형식은 다음과 같습니다.

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

`~`(물결표)가 구분자입니다. 이는 GS1 내용의 일부가 **아니며**, GS1 구문 분석이 시작되기에 앞서 떼어 냅니다.

### 알아내는 규칙

입력이 ASCII 10진 숫자(`0`–`9`) 정확히 여덟 자리로 시작하고 그 바로 뒤에 `~`가 오면 접두어로 봅니다. 아홉째 글자가 `~`가 아니거나 앞 여덟 글자 가운데 숫자가 아닌 것이 있으면, 그 입력은 상관 접두어가 없는 평범한 GS1 내용으로 다룹니다.

### 상관 ID 꺼내 보기

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

### AIM 코드 ID와 함께 쓰기

상관 접두어는 AIM 코드 ID 앞에 올 수 있습니다. 파서가 이를 알아서 다룹니다.

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**구현 클래스:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link**은 하나 이상의 AI 값을 HTTP(S) URL의 구조 안에 곧바로 부호화하여, 물리적 상품에 웹에서 풀어낼 수 있는 식별자를 붙입니다. GAIA는 **비압축** URI에 대해 *GS1 Digital Link Standard: URI Syntax*(1.7.0판)를 구현합니다.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser`는 Digital Link URI를 저절로 알아봅니다. `http://` 또는 `https://`로 시작하는 입력은 모두 `GS1DLParser`로 넘어가며, 거기서 요소 스트링 파이프라인과 똑같은 내용 단계와 해석 단계를 거칩니다.

### URI의 구조와 AI의 구실

Digital Link URI에 담긴 각 AI는 세 가지 구실 가운데 하나를 맡으며, 이는 각 `GS1AIObjectElement`의 `getDigitalLinkAIType()`(`GS1Constants.DigitalLinkAIType`)으로 드러납니다.

| 구실 | 자리 | 예 |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | 경로의 첫 `/ai/value` 쌍(§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | 그 뒤의 경로 쌍들. 기본 키에 따라 순서가 정해집니다(§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | 키가 모두 숫자인 질의 매개변수(§4.10) | `?17=271231` |

강제되는 구조 규칙(`DLPathRules`):
- 경로에는 기본 식별 키가 **하나만** 있어야 합니다. 그 밖의 키는 질의 데이터 속성으로 부호화해야 합니다.
- 키 한정자는 기본 키가 허용하는 것이어야 하고 정해진 순서로 나타나야 합니다. 선택적 한정자는 빼도 되지만, *있는* 것들은 여전히 정해진 순서를 따라야 합니다 — [한정자의 순서](#한정자의-순서)를 보십시오.
- 기본 키 앞에는 임의의 사용자 경로 조각이 올 수 있습니다(예: `/products/au/01/...`). 이는 `getDigitalLinkInfo().getCustomPathStem()`으로 꺼냅니다.
- 숫자가 아닌 질의 키(`linkType`, `context`, `23P` 같은 확장 매개변수)는 무시합니다. 모두 숫자인 키는 `validAsDataAttribute` 표시가 붙은 유효한 AI여야 합니다.
- 퍼센트 부호화된 값 문자는 복호합니다. AI `(03)`과 `(8014)`는 허용되지 않습니다.

기본 키와 그것이 허용하는 한정자 차례는 미리 박아 넣은 것이 아니라 AI 정의에서 **데이터로 이끌어 냅니다** — `gs1DigitalLinkPrimaryKey` 표시와 `gs1DigitalLinkQualifiers` 속성입니다.

구조를 어기거나 URL이 아닌 입력은 Digital Link 구조 오류(`GE-L001`–`GE-L014`, 조건마다 코드 하나)를 냅니다. 분해된 URL 메타데이터(`scheme`, `domain`, `path`, `customPathStem`, `query`, 그리고 `java.net.URL`)는 구조 오류가 있을 때에도 `getDigitalLinkInfo()`로 얻을 수 있습니다.

### 한정자의 순서

기본 키마다 `gs1DigitalLinkQualifiers`는 하나 이상의 **순서 지어진** 한정자 차례를 늘어놓습니다. 한 차례 안에서 대괄호에 싸인 AI는 **선택적**이고, 대괄호가 없는 AI는 **필수**입니다. §4.9 ABNF의 `[cpv-comp]` 표기를 그대로 옮긴 것입니다. 한 기본 키에 딸린 여러 차례는 서로 배타적인 갈래입니다.

이를테면 GTIN(`01`)은 두 차례를 정의합니다.

| 경로 | 차례 | 뜻 |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — 저마다 선택적이되 이 순서로 고정 |
| upui-path | `235` | TPX(필수). GTIN + TPX = UPUI |

그러므로 `/01/09506000134352/10/LOT-ABC/21/SER`은 유효하고(SER보다 LOT이 앞서고 CPV는 빠졌습니다), `/01/.../21/SER/10/LOT-ABC`는 **물리칩니다**(순서가 어긋났습니다). `/01/09506000134352/235/2ABC456`은 upui-path입니다. 순서 검사는 차례를 지키는 부분열 맞추기이므로, 선택적 AI는 건너뛸 수 있어도 순서를 바꿀 수는 없습니다.

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

**구현 클래스:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## 결과 다루기

### ParseResult

`GaiaParser.parse()`가 돌려주는 최상위 결과입니다.

| 메서드 | 돌려주는 것 | 설명 |
|---|---|---|
| `isValid()` | `boolean` | 어느 등급에서도 오류가 없으면 `true`. 경고는 유효성에 영향을 주지 않습니다. `getAiObject()`가 `null`일 때에는 언제나 `true`입니다. |
| `getPayload()` | `String` | 상관 접두어를 떼어 낸 뒤 — 그리고 [입력 수정자](#입력-수정자)가 고쳐 쓴 뒤 — 의 입력 문자열. |
| `getPayloadContent()` | `String` | AIM 코드 ID와 ECI 접두어까지 떼어 낸 페이로드. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL`(GS1이 아니라며 물리친 데이터 캐리어, 이를테면 Code 39의 `]A0` 캐리어), 또는 `UNABLE_TO_DETERMINE_CONTENT`(`aiObject`가 `null`일 때, 이를테면 `DATA_CARRIER` 방식). |
| `getRequestedParseMode()` | `ParseMode` | 설정된 파이프라인의 깊이(`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | 구문 분석이 실제로 이른 가장 깊은 단계 — 아래를 보십시오. |
| `isParseComplete()` | `boolean` | 구문 분석이 요청한 깊이에 이르렀으면 `true`(`achieved == requested`). `isValid()`와는 별개입니다. |
| `getAiObject()` | `GS1AIObject` | 풀어낸 모든 AI. `DATA_CARRIER` 방식에서는 `null`입니다. |
| `getErrors()` | `List<GaiaError>` | WARNING이 아닌 모든 오류(객체 등급 + 모든 요소 등급). |
| `getWarnings()` | `List<GaiaError>` | 모든 WARNING 알림(객체 등급 + 모든 요소 등급). |
| `hasWarnings()` | `boolean` | WARNING 알림이 하나라도 있으면 `true`. |
| `getIssues()` | `List<GaiaError>` | 오류와 경고를 합친 것. |
| `hasDataCarrier()` | `boolean` | AIM 코드 ID를 알아보았으면 `true`. |
| `getDataCarrier()` | `DataCarrierEntry` | 심볼로지 메타데이터. 캐리어를 알아내지 못했으면 `null`. |
| `hasEci()` | `boolean` | 페이로드에서 ECI 지시자를 떼어 냈으면 `true`. |
| `getEci()` | `EciEntry` | ECI 부호화 메타데이터, 또는 `null`. |
| `hasCorrelationId()` | `boolean` | 원래 입력에 `DDDDDDDD~` 상관 접두어가 있었으면 `true`. |
| `getCorrelationInfo()` | `CorrelationInfo` | 뽑아낸 상관 ID. 없었으면 `null`. |
| `isInputModified()` | `boolean` | [입력 수정자](#입력-수정자)가 입력을 바꾸었으면 `true`. |
| `getModifierInfo()` | `ModifierInfo` | 수정자 사슬이 한 일 — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. 수정자가 하나도 설정되지 않았으면 `null`. |
| `getTiming()` | `ProcessingTiming` | 구문 분석에 걸린 실제 시간 — `getStartTime()`(`Instant`), `getProcessingTime()`(`Duration`), `getProcessingTimeMillis()`(`long`), `getCompletionTime()`. `GaiaParser`가 만든 결과가 아니면 `null`. |
| `getVersion()` | `String` | 이 결과를 만들어 낸 라이브러리의 판 번호. |

#### 요청한 방식과 이룬 방식

파이프라인은 **SYNTAX → CONTENT → INTERPRETATION**의 사다리를 오르되 오류가 나면 일찍 멎으므로, 실제로 *이룬* 방식이 *요청한* 방식보다 얕을 수 있습니다. `getAchievedParseMode()`가 어디까지 갔는지 알려 줍니다.

| 요청한 것 | 무슨 일이 일어나는가 | 이룬 것 | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | **구문/구조** 오류가 토큰화 뒤에 구문 분석을 멈춰 세움 | `SYNTAX` | `false` |
| `INTERPRETATION` | **내용** 오류(형식이나 검사 숫자가 틀림)가 보강을 막음 | `CONTENT` | `false` |
| `CONTENT` | 내용 단계는 언제나 끝까지 실행됨(오류는 표시될 뿐 치명적이지 않음) | `CONTENT` | `true` |
| 아무것이나(깨끗한 입력) | 파이프라인이 요청한 깊이에 이름 | = 요청한 것 | `true` |
| `DATA_CARRIER` | 캐리어를 검증함. AI 내용은 구문 분석하지 않음 | `DATA_CARRIER` | `true` |
| 아무것이나 | AI 구문 분석에 앞서 데이터 캐리어가 물리쳐짐(이를테면 GS1이 아닌 `]A0` 캐리어) | `SYNTAX` | `false` |

`isParseComplete()`는 `isValid()`와 별개입니다. 검사 숫자가 틀린 GTIN을 `CONTENT`로 구문 분석하면 그것은 **완료된** 것이되(내용 단계를 실행했으므로) **무효**입니다(검사 숫자가 어긋났으므로). “파이프라인이 내가 청한 깊이까지 돌았는가?”를 물으려면 `isParseComplete()`를, “데이터가 적격한가?”를 물으려면 `isValid()`를 쓰십시오.

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

풀어낸 AI 요소들의 모음입니다.

| 메서드 | 설명 |
|---|---|
| `getAis()` | 모든 `GS1AIObjectElement` 인스턴스를 입력 순서대로. |
| `get(String aiCode)` | 주어진 AI 코드에 맞는 첫 요소, 또는 `null`. |
| `contains(String aiCode)` | 그 코드를 지닌 AI가 있으면 `true`. |
| `size()` | 풀어낸 AI의 개수. |
| `isValid()` | 객체 등급 오류가 없고 어느 요소에도 오류가 없으면 `true`. |
| `toHriString()` | HRI 문자열, 이를테면 `(01)09506000134352 (17)261231`. |
| `toElementString()` | 원시 요소 스트링 — 괄호 없이, 가변 길이 요소마다 뒤에 FNC1 — 이를테면 `010950600013435210LOT-ABC<GS>17271231`. `isValid()`가 `false`이면 `null`을 돌려줍니다. |
| `getContentType()` | `hasDigitalLink()`가 참이면 `GS1_DIGITAL_LINK`, 아니면 `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | 입력이 기본 식별 키를 실은 GS1 Digital Link URI였으면 `true`. 적격한 URL이되 기본 키가 없으면 `getDigitalLinkInfo()`는 여전히 얻을 수 있으나 여기서는 `false`를 돌려줍니다. |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` 위의 정규 GS1 Digital Link URI(§4.12) — 기본 키와 한정자는 경로 조각으로, 데이터 속성은 AI 키로 정렬된 질의 매개변수로. 기본 키가 없으면 `null`. |
| `getDigitalLinkInfo()` | URI 분해 메타데이터(`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`). Digital Link가 아니면 `null`. |
| `getAllErrors()` | 객체 등급 + 모든 요소의 오류(WARNING 제외). |
| `getAllWarnings()` | 객체 등급 + 모든 요소의 경고. |
| `getAllIssues()` | 모두 합친 것. |

---

### GS1AIObjectElement

풀어낸 AI 하나입니다.

| 메서드 | 설명 |
|---|---|
| `getAi()` | AI 코드, 이를테면 `"01"`, `"3102"`. |
| `getTitle()` | GS1 데이터 명칭, 이를테면 `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | AI의 온전한 GS1 설명. **구문 분석 언어로 지역화됩니다**(영어라면 이를테면 `"Global Trade Item Number (GTIN)"`). 번역되어 있지 않으면 AI 정의의 영어 텍스트로 되돌아갑니다. |
| `getFormatString()` | AI *와* 그 데이터를 아우르는 형식 서술. 이를테면 AI `01`이면 `"N2+N14"`, AI `10`이면 `"N2+X..20"`, AI `3932`면 `"N4+N3+N..15"`. |
| `getValue()` | 요소 스트링에서 뽑아낸 원시 데이터 값. |
| `isFixedLength()` | AI의 데이터 길이가 고정이면 `true`. |
| `getPosition()` | 원래 입력에서의 0부터 세는 문자 위치. |
| `getGS1ComponentValues()` | 구성 요소별 값 조각(구성 요소가 여럿인 AI의 경우). |
| `getErrors()` | 요소 등급의 WARNING 아닌 오류. |
| `getWarnings()` | 요소 등급의 WARNING 알림. |
| `getIssues()` | 요소 등급의 오류와 경고를 합친 것. |
| `hasErrors()` | WARNING 아닌 오류가 하나라도 붙어 있으면 `true`. |
| `hasWarnings()` | WARNING 알림이 하나라도 붙어 있으면 `true`. |
| `getInterpretations()` | `GS1AIInterpretation` 항목들(INTERPRETATION 방식에서 채워집니다). |
| `getInterpretation(String type)` | 주어진 `GS1Constants_Enricher` 유형 키에 맞는 첫 해석, 또는 `null`. |
| `getDigitalLinkAIType()` | 그 요소의 Digital Link 구실(`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`). 요소 스트링 입력이면 `null`. |
| `hasDigitalLinkAIType()` | Digital Link 구실이 배정되었으면 `true`. |

---

### GaiaError

바뀌지 않는 검증 오류 또는 알림입니다.

| 메서드 | 설명 |
|---|---|
| `getId()` | 목록상의 식별자, 이를테면 `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, 또는 `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, 또는 `INTERNAL`. |
| `getCode()` | 기계가 읽는 짧은 코드. |
| `getAi()` | 오류를 일으킨 AI 코드. 객체 등급 오류이면 `null`. |
| `getMessage()` | 사람이 읽는, 값이 채워진 메시지. |
| `getPosition()` | 원래 입력에서의 0부터 세는 문자 위치. |

---

### GS1AIInterpretation

`INTERPRETATION` 방식에서 `GS1AIObjectElement`에 붙는, 이름표가 달린 해석 조각 하나입니다.

| 메서드 | 설명 |
|---|---|
| `getType()` | 기계가 읽는 유형 키, 이를테면 `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. 언어가 달라도 그대로입니다. |
| `getLabel()` | 사람이 읽는 이름표. **구문 분석 언어로 지역화됩니다**(영어라면 이를테면 `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | 뽑아내거나 보강한 값, 이를테면 `"31/12/2026"`, `"9506000"`. 지역화되지 않습니다. |

---

### DataCarrierEntry와 DataCarrierType

입력이 AIM 코드 ID를 싣고 있으면 `ParseResult.getDataCarrier()`가 그 데이터를 실어 온 심볼을 서술하는 `DataCarrierEntry`를 돌려줍니다. 이 항목은 맞아떨어진 AIM 코드 ID에 딸린 구체적인 등록부 기록이며, `DataCarrierType`은 그것이 속한 컴파일 시점의 열거형입니다.

#### DataCarrierEntry

알아본 AIM 코드 ID 하나에 대한 메타데이터입니다(`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| 메서드 | 설명 |
|---|---|
| `getAimCodeId()` | 맞아떨어진 AIM 코드 ID, 이를테면 `"]C1"`. |
| `getName()` | 그 구체적인 심볼의 사람이 읽는 이름, 이를테면 `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | 캐리어에 대한 좀 더 긴 설명. |
| `getType()` | 캐리어의 구조적 유형을 문자열로(`getDataCarrierType().getCategory()`를 그대로 옮긴 것). |
| `getStandard()` | 기록되어 있는 경우, 그 심볼로지 표준. |
| `getDataCarrierType()` | 이 항목에 딸린 유형 지어진 `DataCarrierType` — 프로그램으로 갈래를 나눌 때에는 이것을 쓰십시오. |
| `isGs1Capable()` | 캐리어가 GS1 데이터(AI 요소 스트링 및/또는 Digital Link)를 담을 수 있으면 `true`. |
| `isGs1AICapable()` | 캐리어가 GS1 AI 요소 스트링을 담을 수 있으면 `true`. |
| `isGs1DigitalLinkCapable()` | 캐리어가 GS1 Digital Link URI를 담을 수 있으면 `true`. |
| `isEciCapable()` | 캐리어가 ECI 지시자를 받쳐 주면 `true`. |
| `isRequiresGtinPadding()` | AI 구문 분석에 앞서 숫자 값이 GTIN-14로 채워지는 EAN/UPC/ITF 캐리어이면 `true`. |

#### DataCarrierType

ISO/IEC 15424에서 배정한 AIM 코드 ID를 키로 삼는, 데이터 캐리어 유형의 컴파일 시점 열거형입니다(`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). `]` 다음 글자(*코드 문자*)가 계열을 정하며, 대부분의 계열은 모든 수식자를 아우르는 상수 하나로 이어집니다(`ITF`는 `]I0`–`]I2`를, `EAN_UPC`는 EAN-13, UPC-A, UPC-E, EAN-8을 아우릅니다). GS1이 AI 데이터를 위해 따로 잡아 둔 수식자는 그 갈래가 저마다 하나의 상수를 이룹니다 — `GS1_128`(`]C1`), `GS1_DATA_MATRIX`(`]d2`), `GS1_QR_CODE`(`]Q3`), `GS1_DOT_CODE`(`]J1`). 이는 수식자 없는 짝과 구별됩니다. AIM 코드 ID가 없거나 알 수 없는 캐리어를 가리키면 유형은 `UNKNOWN`입니다.

| 메서드 | 설명 |
|---|---|
| `getCategory()` | 넓은 갈래인 `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, 또는 `OTHER`. |
| `getCodeChar()` | 계열을 나타내는 AIM 코드 문자, 이를테면 QR Code이면 `"Q"`. `UNKNOWN`이면 `null`. |
| `getDisplayName()` | *유형*의 사람이 읽는 이름(`DataCarrierEntry.getName()`보다 넓을 수 있습니다 — 이를테면 `"EAN-8"`에 대해 `"EAN-13 / UPC-A / UPC-E / EAN-8"`). |
| `isGs1DataCarrier()` | 언제나 GS1 AI 데이터를 나타내는 상수이면 `true`. GS1이 따로 잡아 둔 네 갈래(`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`)에 더해, `]e` 수식자가 모두 GS1 DataBar이므로 본디 GS1인 `GS1_DATABAR`가 여기 듭니다. `DataCarrierEntry.isGs1AICapable()`보다 좁습니다 — 수식자 없는 `QR_CODE`도 GS1 AI 데이터를 실을 수 있습니다. |
| `static forAimCodeId(String)` | AIM 코드 ID에서 곧바로 유형을 풀어냅니다(`"]Q3"` → `GS1_QR_CODE`, `"]Q9"` → `QR_CODE`). 없거나 꼴이 어긋났거나 알 수 없는 ID이면 `UNKNOWN`을 돌려줍니다. |

이름이 아니라 유형으로 갈래 나누기 — 이를테면 선형(Code-128) 심볼과 2D(QR / Data Matrix) 심볼을 갈라내기:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D`가 아우르는 것은 행렬 심볼과 점 심볼뿐입니다. 층을 이룬 선형 캐리어(`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`)는 흔히 “2D” 바코드라 불리지만 `STACKED_LINEAR`입니다.
둘을 한 무리로 다루려면 — 이를테면 레이저 스캐너가 아니라 이미저가 필요한지 가리려면 —
두 갈래 어느 쪽인지를 함께 살피십시오.

> 유형을 풀어내려면 스캔에 AIM 코드 ID가 실려 있어야 합니다. 그것이 없으면 `getDataCarrier()`가 `null`이고 유형은 `UNKNOWN`입니다. 스캐너가 AIM 코드 ID 접두어를 함께 보내도록 설정하십시오.

---

## 오류 참조

| 코드 | 등급 | 단계 | 뜻 |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | 알 수 없는 AI 접두어 — 데이터 길이를 알아낼 수 없음 |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | 온전한 AI 코드를 읽기에 입력이 너무 짧음 |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | 잘린 값 — AI가 요구하는 것보다 문자가 적음 |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | 요소 스트링에 응용 식별자가 중복됨 |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | 필요로 하는 AI 의존 관계가 빠짐 |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | 배제되는 AI 짝 — 함께 나타날 수 없는 두 AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | 뜻밖의 토큰화 실패 |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | 요소 스트링에 GS1이 부호화할 수 있는 문자 집합 바깥의 문자가 있음 |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | 가변 길이 AI 뒤에 있어야 할 FNC1 구분자가 빠짐 |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | 모든 구성 요소의 최댓값을 넘어선 뒤에 데이터가 더 있음 |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | 문자열 중간에서 고정 길이 AI 뒤에 FNC1 구분자가 있음 |
| `GE-W002` | WARNING | SYNTAX | 요소 스트링 끝에 FNC1이 남아 있음(알림일 뿐) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI 구조 위반 — 조건마다 코드 하나(꼴이 어긋난 URI, 스킴, 호스트, 한정자 순서, 금지된 AI, 기본 키 없음(`GE-L013`), 기본 키가 여럿(`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | 값이 AI의 정규식 무늬에 맞지 않음 |
| `GE-C003` | DATA_ERROR | CONTENT | 검사 숫자 검증 실패 |
| `GE-C004` | DATA_ERROR | CONTENT | 검사 문자 쌍 검증 실패 |
| `GE-C005` | FORMAT_ERROR | CONTENT | 구성 요소 값에 허용되지 않는 문자 집합의 문자가 있음 |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | 구성 요소 형식 실패 — 검증기 조건마다 코드 하나(`componentformat/`를 보십시오) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | 별도의 의미 검증 실패 — 검증기 조건마다 코드 하나(`content/validator/`를 보십시오). **예외:** 아래에 늘어놓은 14가지 GS1 회사 접두어 검사는 등급이 `WARNING`이고, `GE-C168`(알 수 없는 ISO 3166-1 숫자 국가 코드)은 `FORMAT_ERROR`입니다. |
| GS1 회사 접두어 검사 | WARNING | CONTENT | GS1 키를 지닌 AI에서 키가 알려진 GS1 회사 접두어로 시작하지 않음 — `GE-C122`(CPID), `GE-C129`(GCN), `GE-C131`(GDTI), `GE-C132`(GIAI), `GE-C133`(GINC), `GE-C135`(GLN), `GE-C137`(GMN), `GE-C140`(GRAI), `GE-C142`(GSIN), `GE-C144`(GSRN), `GE-C146`(GTIN), `GE-C148`(HIDRI), `GE-C153`(ITIP), `GE-C165`(SSCC). 알림일 뿐이며 유효성에 영향을 주지 않습니다. |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040(IMEI) / 8041(IMEI2)의 IMEI 검사 숫자(Luhn) 실패 |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042(ESIM)의 EID 검사 숫자(Luhn) 실패 |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | 알아볼 수 없는 AIM 코드 ID |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | 캐리어는 알아냈으나 GS1 AI 요소 스트링도 Digital Link URI도 받쳐 주지 않음 |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | 뜻밖의 내부 오류 |

> **메시지를 그려 내는 데에 알려진 결함이 있습니다.** 목록의 틀은 채워 넣는 값을
> MessageFormat 방식으로 아포스트로피를 겹쳐 인용하나(`''{value}''`),
> `ErrorRegistry`는 평범한 `String.replace`로 값을 채우므로 그 겹침이
> `getMessage()`까지 살아남습니다. 이 안내서에 인용된 메시지 텍스트가
> `value '09506000134351'`로 보이는 자리에서 실제로는 `value ''09506000134351''`이
> 나옵니다. 35개 언어 목록 모두에서 값을 인용하는 모든 메시지가 여기 해당합니다.
> 오류 메시지를 구문 분석하지 마시고 `getId()` / `getCode()`로 맞춰 보십시오.

---

## 스레드 안전성

`GaiaParser`는 한 번 만들어지고 나면 스레드에 안전합니다. 인스턴스 하나를 여러 스레드가 나눠 쓰며 동시에 쓸 수 있습니다. 권하는 방식은 응용 프로그램이 시작할 때 인스턴스를 하나 만들어 두고 그것을 다시 쓰는 것입니다.

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig`는 바뀌지 않으며 나눠 쓰기에 똑같이 안전합니다. 라이브러리가 대신 지켜 줄 수 없는 단 하나의 스레드 안전성 의무는 [입력 수정자](#입력-수정자)에 있습니다. 수정자마다 인스턴스 하나가 캐시되어 동시에 도는 모든 구문 분석에 함께 쓰이므로, 구현체는 상태를 지니지 말아야 합니다.

---

## 부록 A — AI 문자열 상수

`GS1Constants_AICodes`(`tools.pantheum.gaia.gs1.constants` 패키지)는 GAIA가 알아보는 모든 응용 식별자마다 `String` 상수를 선언해 둡니다. 원시 AI 코드 문자열을 코드에 박아 넣지 말고 이 상수를 쓰십시오.

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

각 상수는 AI 코드의 문자열 형태를 담고 있습니다(예: `AI_01_GTIN = "01"`).

### 식별과 일련번호

| AI | 상수 | 설명 |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | 일련 배송 컨테이너 코드 (SSCC). |
| `01` | `AI_01_GTIN` | 국제거래단품식별코드 (GTIN). |
| `02` | `AI_02_CONTENT` | 내용물 거래 품목의 국제거래단품식별코드 (GTIN). |
| `03` | `AI_03_MTO_GTIN` | 주문 제작(MtO) 거래 품목의 식별 (GTIN). |
| `10` | `AI_10_BATCH_LOT` | 배치 또는 로트 번호. |
| `20` | `AI_20_VARIANT` | 내부 제품 변형. |
| `21` | `AI_21_SERIAL` | 일련번호. |
| `22` | `AI_22_CPV` | 소비자 제품 변형. |
| `235` | `AI_235_TPX` | 제3자가 관리하는, 일련화된 국제거래단품식별코드(GTIN) 확장 (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | 제조업체가 부여한 추가 제품 식별. |
| `241` | `AI_241_CUST_PART_NO` | 고객 부품 번호. |
| `242` | `AI_242_MTO_VARIANT` | 주문 제작 변형 번호. |
| `243` | `AI_243_PCN` | 포장 구성 요소 번호. |
| `250` | `AI_250_SECONDARY_SERIAL` | 보조 일련번호. |
| `251` | `AI_251_REF_TO_SOURCE` | 원본 엔터티 참조. |
| `253` | `AI_253_GDTI` | 국제문서유형식별코드 (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | 국제거래처식별코드(GLN) 확장 구성 요소. |
| `255` | `AI_255_GCN` | 국제쿠폰번호 (GCN). |
| `30` | `AI_30_VAR_COUNT` | 품목의 가변 수량 (가변 계량 거래 품목). |
| `37` | `AI_37_COUNT` | 물류 단위에 포함된 거래 품목 또는 거래 품목 조각의 수량. |

### 날짜와 시각

| AI | 상수 | 설명 |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | 생산일자 (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | 만기일 (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | 포장일자 (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | 품질유지기한 (YYMMDD). |
| `16` | `AI_16_SELL_BY` | 판매 기한일 (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | 유효기한 (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | 배송 가능 시작 날짜 및 시간 (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | 배송 마감 날짜 및 시간 (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | 출시일자 (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | 유효기한 날짜 및 시간 (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | 최초 냉동일자 (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | 수확일자 (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | 검사 기한일 (YYMMDD[hhmm]). |

### 수량과 계량 — 가변 계량(미터법)

네 자리 AI 계열 `310n`–`369n`은 가변 계량값을 부호화합니다. 셋째 자리가 계량의 종류를 정하고, **넷째 자리**(`n`, 0–5)가 묵시적 소수 자릿수입니다. 이를테면 `AI_3102_NET_WEIGHT_KG`는 소수 두 자리의 킬로그램 단위 정미 중량을 뜻합니다.

| 계열 | 상수의 꼴(`n` = 소수 자릿수) | 설명 |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | 정미 중량, 킬로그램 (가변 계량 거래 품목). |
| `311n` | `AI_311n_LENGTH_M` | 길이 또는 첫 번째 치수, 미터 (가변 계량 거래 품목). |
| `312n` | `AI_312n_WIDTH_M` | 너비, 지름 또는 두 번째 치수, 미터 (가변 계량 거래 품목). |
| `313n` | `AI_313n_HEIGHT_M` | 깊이, 두께, 높이 또는 세 번째 치수, 미터 (가변 계량 거래 품목). |
| `314n` | `AI_314n_AREA_M` | 면적, 제곱미터 (가변 계량 거래 품목). |
| `315n` | `AI_315n_NET_VOLUME_L` | 정미 용적, 리터 (가변 계량 거래 품목). |
| `316n` | `AI_316n_NET_VOLUME_M` | 정미 용적, 세제곱미터 (가변 계량 거래 품목). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | 물류 중량, 킬로그램. |
| `331n` | `AI_331n_LENGTH_M_LOG` | 길이 또는 첫 번째 치수, 미터. |
| `332n` | `AI_332n_WIDTH_M_LOG` | 너비, 지름 또는 두 번째 치수, 미터. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | 깊이, 두께, 높이 또는 세 번째 치수, 미터. |
| `334n` | `AI_334n_AREA_M_LOG` | 면적, 제곱미터. |
| `335n` | `AI_335n_VOLUME_L_LOG` | 물류 용적, 리터. |
| `336n` | `AI_336n_VOLUME_M_LOG` | 물류 용적, 세제곱미터. |
| `337n` | `AI_337n_KG_PER_M` | 제곱미터당 킬로그램. |

### 수량과 계량 — 가변 계량(야드파운드법 / 미국)

| 계열 | 상수의 꼴(`n` = 소수 자릿수) | 설명 |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | 정미 중량, 파운드 (가변 계량 거래 품목). |
| `321n` | `AI_321n_LENGTH_IN` | 길이 또는 첫 번째 치수, 인치 (가변 계량 거래 품목). |
| `322n` | `AI_322n_LENGTH_FT` | 길이 또는 첫 번째 치수, 피트 (가변 계량 거래 품목). |
| `323n` | `AI_323n_LENGTH_YD` | 길이 또는 첫 번째 치수, 야드 (가변 계량 거래 품목). |
| `324n` | `AI_324n_WIDTH_IN` | 너비, 지름 또는 두 번째 치수, 인치 (가변 계량 거래 품목). |
| `325n` | `AI_325n_WIDTH_FT` | 너비, 지름 또는 두 번째 치수, 피트 (가변 계량 거래 품목). |
| `326n` | `AI_326n_WIDTH_YD` | 너비, 지름 또는 두 번째 치수, 야드 (가변 계량 거래 품목). |
| `327n` | `AI_327n_HEIGHT_IN` | 깊이, 두께, 높이 또는 세 번째 치수, 인치 (가변 계량 거래 품목). |
| `328n` | `AI_328n_HEIGHT_FT` | 깊이, 두께, 높이 또는 세 번째 치수, 피트 (가변 계량 거래 품목). |
| `329n` | `AI_329n_HEIGHT_YD` | 깊이, 두께, 높이 또는 세 번째 치수, 야드 (가변 계량 거래 품목). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | 물류 중량, 파운드. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | 길이 또는 첫 번째 치수, 인치. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | 길이 또는 첫 번째 치수, 피트. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | 길이 또는 첫 번째 치수, 야드. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | 너비, 지름 또는 두 번째 치수, 인치. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | 너비, 지름 또는 두 번째 치수, 피트. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | 너비, 지름 또는 두 번째 치수, 야드. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | 깊이, 두께, 높이 또는 세 번째 치수, 인치. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | 깊이, 두께, 높이 또는 세 번째 치수, 피트. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | 깊이, 두께, 높이 또는 세 번째 치수, 야드. |
| `350n` | `AI_350n_AREA_IN` | 면적, 제곱인치 (가변 계량 거래 품목). |
| `351n` | `AI_351n_AREA_FT` | 면적, 제곱피트 (가변 계량 거래 품목). |
| `352n` | `AI_352n_AREA_YD` | 면적, 제곱야드 (가변 계량 거래 품목). |
| `353n` | `AI_353n_AREA_IN_LOG` | 면적, 제곱인치. |
| `354n` | `AI_354n_AREA_FT_LOG` | 면적, 제곱피트. |
| `355n` | `AI_355n_AREA_YD_LOG` | 면적, 제곱야드. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | 정미 중량, 트로이온스 (가변 계량 거래 품목). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | 정미 중량(또는 용적), 온스 (가변 계량 거래 품목). |
| `360n` | `AI_360n_NET_VOLUME_QT` | 정미 용적, 쿼트 (가변 계량 거래 품목). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | 정미 용적, 미국 갤런 (가변 계량 거래 품목). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | 물류 용적, 쿼트. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | 물류 용적, 미국 갤런. |
| `364n` | `AI_364n_NET_VOLUME_IN` | 정미 용적, 세제곱인치 (가변 계량 거래 품목). |
| `365n` | `AI_365n_NET_VOLUME_FT` | 정미 용적, 세제곱피트 (가변 계량 거래 품목). |
| `366n` | `AI_366n_NET_VOLUME_YD` | 정미 용적, 세제곱야드 (가변 계량 거래 품목). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | 물류 용적, 세제곱인치. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | 물류 용적, 세제곱피트. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | 물류 용적, 세제곱야드. |

### 가격과 금액

넷째 자리(`n`)가 묵시적 소수 자릿수를 부호화합니다. 허용되는 범위는 계열마다
다릅니다 — `n` 열을 보십시오.

| 계열 | 상수의 꼴(`n` = 소수 자릿수) | `n` | 설명 |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | 지불해야 할 금액 또는 쿠폰 가치, 현지 통화. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | ISO 통화 코드로 표시한 지불해야 할 금액. |
| `392n` | `AI_392n_PRICE` | 0–9 | 지불해야 할 금액, 단일 통화권 (가변 계량 거래 품목). |
| `393n` | `AI_393n_PRICE` | 0–9 | ISO 통화 코드로 표시한 지불해야 할 금액 (가변 계량 거래 품목). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | 쿠폰 할인율. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | 측정 단위당 지불해야 할 금액, 단일 통화권 (가변 계량 거래 품목). |

### 장소와 배송

| AI | 상수 | 설명 |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | 고객 발주 번호. |
| `401` | `AI_401_GINC` | 탁송화물 국제식별번호 (GINC). |
| `402` | `AI_402_GSIN` | 출하 국제식별번호 (GSIN). |
| `403` | `AI_403_ROUTE` | 라우팅 코드. |
| `410` | `AI_410_SHIP_TO_LOC` | 배송지/인도지 국제거래처식별코드 (GLN). |
| `411` | `AI_411_BILL_TO` | 청구지/송장 발행지 국제거래처식별코드 (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | 구매처 국제거래처식별코드 (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | 전달 대상 국제거래처식별코드 (GLN) (~를 위한 배송/인도). |
| `414` | `AI_414_LOC_NO` | 물리적 위치 식별 - 국제거래처식별코드 (GLN). |
| `415` | `AI_415_PAY_TO` | 청구 당사자의 국제거래처식별코드 (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | 생산 또는 서비스 제공 장소의 국제거래처식별코드 (GLN). |
| `417` | `AI_417_PARTY` | 당사자 국제거래처식별코드 (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | 단일 우편 관할 내 배송지/인도지 우편번호. |
| `421` | `AI_421_SHIP_TO_POST` | ISO 국가 코드가 포함된 배송지/인도지 우편번호. |
| `422` | `AI_422_ORIGIN` | 거래 품목의 원산지국. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | 최초 가공국. |
| `424` | `AI_424_COUNTRY_PROCESS` | 가공국. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | 분해국. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | 전체 공정 사슬을 포괄하는 국가. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | 원산지의 국가 하위 구역. |
| `4300` | `AI_4300_SHIP_TO_COMP` | 배송지/인도지 회사명. |
| `4301` | `AI_4301_SHIP_TO_NAME` | 배송지/인도지 담당자. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | 배송지/인도지 주소 1행. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | 배송지/인도지 주소 2행. |
| `4304` | `AI_4304_SHIP_TO_SUB` | 배송지/인도지 교외 지역. |
| `4305` | `AI_4305_SHIP_TO_LOC` | 배송지/인도지 지역. |
| `4306` | `AI_4306_SHIP_TO_REG` | 배송지/인도지 지방. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | 배송지/인도지 국가 코드. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | 배송지/인도지 전화번호. |
| `4309` | `AI_4309_SHIP_TO_GEO` | 배송지/인도지 지리적 위치(GEO). |
| `4310` | `AI_4310_RTN_TO_COMP` | 반품지 회사명. |
| `4311` | `AI_4311_RTN_TO_NAME` | 반품지 담당자. |
| `4312` | `AI_4312_RTN_TO_ADD1` | 반품지 주소 1행. |
| `4313` | `AI_4313_RTN_TO_ADD2` | 반품지 주소 2행. |
| `4314` | `AI_4314_RTN_TO_SUB` | 반품지 교외 지역. |
| `4315` | `AI_4315_RTN_TO_LOC` | 반품지 지역. |
| `4316` | `AI_4316_RTN_TO_REG` | 반품지 지방. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | 반품지 국가 코드. |
| `4318` | `AI_4318_RTN_TO_POST` | 반품지 우편번호. |
| `4319` | `AI_4319_RTN_TO_PHONE` | 반품지 전화번호. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | 서비스 코드 설명. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | 위험물 플래그. |
| `4322` | `AI_4322_AUTH_LEAVE` | 배송물 두고 가기 허가. |
| `4323` | `AI_4323_SIG_REQUIRED` | 서명 필요 플래그. |
| `4330` | `AI_4330_MAX_TEMP_F` | 화씨 최고 온도(도의 100분의 1 단위로 표시). |
| `4331` | `AI_4331_MAX_TEMP_C` | 섭씨 최고 온도(도의 100분의 1 단위로 표시). |
| `4332` | `AI_4332_MIN_TEMP_F` | 화씨 최저 온도(도의 100분의 1 단위로 표시). |
| `4333` | `AI_4333_MIN_TEMP_C` | 섭씨 최저 온도(도의 100분의 1 단위로 표시). |

### 상품 속성과 추적

| AI | 상수 | 설명 |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO 재고 번호 (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE 육류 도체 및 부위 분류. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | 유효 역가. |
| `7005` | `AI_7005_CATCH_AREA` | 어획 구역. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | 수산업 목적의 어종. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | 어구 유형. |
| `7010` | `AI_7010_PROD_METHOD` | 생산 방법. |
| `7020` | `AI_7020_REFURB_LOT` | 재정비 로트 ID. |
| `7021` | `AI_7021_FUNC_STAT` | 기능 상태. |
| `7022` | `AI_7022_REV_STAT` | 개정 상태. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | 조립품의 국제개별자산식별코드 (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | 세 자리 ISO 국가 코드가 딸린 가공자 번호(자리 10개). |
| `7040` | `AI_7040_UIC_EXT` | 확장자 1 및 수입업체 색인이 포함된 GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT 화물 단위 유형. |

### 국가별 보건의료 상환 번호(NHRN)

| AI | 상수 | 설명 |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | 국가별 의료 상환 번호 (NHRN) - 독일 PZN. |
| `711` | `AI_711_NHRN_CIP` | 국가별 의료 상환 번호 (NHRN) - 프랑스 CIP. |
| `712` | `AI_712_NHRN_CN` | 국가별 의료 상환 번호 (NHRN) - 스페인 CN. |
| `713` | `AI_713_NHRN_DRN` | 국가별 의료 상환 번호 (NHRN) - 브라질 DRN. |
| `714` | `AI_714_NHRN_AIM` | 국가별 의료 상환 번호 (NHRN) - 포르투갈 AIM. |
| `715` | `AI_715_NHRN_NDC` | 국가별 의료 상환 번호 (NHRN) - 미국 NDC. |
| `716` | `AI_716_NHRN_AIC` | 국가별 의료 상환 번호 (NHRN) - 이탈리아 AIC. |
| `717` | `AI_717_NHRN_SRN` | 국가별 의료 상환 번호 (NHRN) - 코스타리카 위생 등록 번호. |

### 보건의료, GMN, HIDRI, CPID, 사람에 관한 데이터

| AI | 상수 | 설명 |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | 인증 참조 번호(자리 10개). |
| `7240` | `AI_7240_PROTOCOL` | 프로토콜 ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC 매체 유형. |
| `7242` | `AI_7242_VCN` | 버전 관리 번호 (VCN). |
| `7250` | `AI_7250_DOB` | 생년월일 (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | 출생 날짜 및 시간 (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | 생물학적 성별. |
| `7253` | `AI_7253_FAMILY_NAME` | 성(姓). |
| `7254` | `AI_7254_GIVEN_NAME` | 이름. |
| `7255` | `AI_7255_SUFFIX` | 이름 접미사. |
| `7256` | `AI_7256_FULL_NAME` | 성명(전체 이름). |
| `7257` | `AI_7257_PERSON_ADDR` | 개인 주소. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | 출생 순서(다태아). |
| `7259` | `AI_7259_BABY` | 성(姓)의 대상 영아. |
| `8001` | `AI_8001_DIMENSIONS` | 롤 제품(너비, 길이, 코어 지름, 방향, 이음매 수). |
| `8002` | `AI_8002_CMT_NO` | 이동전화 식별번호. |
| `8003` | `AI_8003_GRAI` | 국제회수가능자산식별코드 (GRAI). |
| `8004` | `AI_8004_GIAI` | 국제개별자산식별코드 (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | 측정 단위당 가격. |
| `8006` | `AI_8006_ITIP` | 개별 거래 품목 조각의 식별 (ITIP). |
| `8007` | `AI_8007_IBAN` | 국제은행계좌번호 (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | 생산 날짜 및 시간 (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | 광학 판독 가능 센서 표시기. |
| `8010` | `AI_8010_CPID` | 구성요소/부품 식별자 (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | 구성요소/부품 식별자 일련번호 (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | 소프트웨어 버전. |
| `8013` | `AI_8013_GMN` | 국제모델번호 (GMN). |
| `8014` | `AI_8014_MUDI` | 고도 개별화 기기 등록 식별자 (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | 서비스를 제공하는 조직과 서비스 제공자 간의 관계를 식별하는 국제서비스관계번호 (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | 서비스를 제공하는 조직과 서비스 수혜자 간의 관계를 식별하는 국제서비스관계번호 (GSRN). |
| `8019` | `AI_8019_SRIN` | 서비스 관계 인스턴스 번호 (SRIN). |
| `8020` | `AI_8020_REF_NO` | 납부서 참조 번호. |
| `8026` | `AI_8026_ITIP_CONTENT` | 물류 단위에 포함된 거래 품목 조각(ITIP)의 식별. |
| `8030` | `AI_8030_DIGSIG` | 디지털 서명 (DigSig). |
| `8040` | `AI_8040_IMEI` | 국제단말기식별번호 (IMEI). |
| `8041` | `AI_8041_IMEI2` | 국제단말기식별번호 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | 내장 SIM 번호. |
| `8043` | `AI_8043_PSIM` | 물리적 SIM 번호. |
| `8110` | `AI_8110` | 북미에서 사용하는 쿠폰 코드 식별. |
| `8111` | `AI_8111_POINTS` | 쿠폰의 로열티 포인트. |
| `8112` | `AI_8112` | 북미에서 사용하는 포지티브 오퍼 파일 쿠폰 코드 식별. |
| `8200` | `AI_8200_PRODUCT_URL` | 확장 패키징 URL. |

### 내부 / 회사 용도

| AI | 상수 | 설명 |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | 거래처 간에 상호 합의된 정보. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | 회사 내부 정보(자리 9개). |

---

## 부록 B — 해석 키 상수

`GaiaParser.parse()`를 `ParseMode.INTERPRETATION`으로 부르면, 각 `GS1AIObjectElement`는 영역별 보강기가 만들어 낸 `GS1AIInterpretation` 객체의 목록을 지닐 수 있습니다. 특정한 해석 값을 찾을 때에는 `GS1Constants_Enricher`(`tools.pantheum.gaia.gs1.constants` 패키지)의 상수를 키로 쓰십시오.

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

표시 이름표는 상수가 **아닙니다**. 그것들은 `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`의 지역화된 목록에 유형 상수를 키로 하여 들어 있습니다. `GS1AIInterpretation.getLabel()`은 구문 분석 언어의 이름표를 돌려주며([지역화된 메시지와 이름표](#지역화된-메시지와-이름표)를 보십시오), 어느 목록에 그 키가 빠져 있으면 영어로 되돌아갑니다. 아래의 “표시 이름표” 열은 한국어 텍스트를 늘어놓은 것입니다. 유형 키 자체는 언어가 달라도 그대로이므로, 이름표가 아니라 반드시 키로 맞춰 보십시오.

### 날짜와 시각

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `DATE_VALUE` | 날짜 | 날짜 AI(11–17, 7003, 7006, 7011 등) |
| `DATE_FORMAT` | 날짜 형식 | 날짜 AI |
| `TIME_VALUE` | 시간 | 시각을 담은 AI(7003, 7011, 8008 등) |
| `TIME_FORMAT` | 시간 형식 | 시각을 담은 AI |
| `DATETIME_VALUE` | 날짜 및 시간 | 날짜와 시각 AI |
| `DATETIME_FORMAT` | 날짜 및 시간 형식 | 날짜와 시각 AI |

### 수확일

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | 수확 시작일 | AI 7007 |
| `HARVEST_END_DATE` | 수확 종료일 | AI 7007(선택적인 기간 끝) |
| `HARVEST_DATE_RANGE` | 수확 날짜 범위 | AI 7007 |

### GS1 회사 접두어

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 회사 접두어 | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_CODE` | GS1 회원 코드 | GTIN / GLN / SSCC AI |
| `GS1_MEMBER_NAME` | GS1 회원 기관 | GTIN / GLN / SSCC AI |

### GTIN

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN 유형 | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | 포장 단계 | AI 01 |
| `GTIN_CHECK_DIGIT` | 검사 숫자 | AI 01, 02 |

### SSCC

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | 확장 숫자 | AI 00 |
| `SSCC_SERIAL_REFERENCE` | 일련 참조 | AI 00 |
| `SSCC_CHECK_DIGIT` | 검사 숫자 | AI 00 |

### 국가(ISO 3166)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | 국가 코드 (숫자) | 단일 국가 AI(422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | 국가 코드 (알파-2) | 두 글자 국가 코드를 쓰는 AI |
| `COUNTRY_NAME` | 국가명 | 단일 국가 AI |
| `COUNTRY_LIST` | 국가 | AI 423 — 모든 이름을 이어 붙임, 이를테면 `Australia, New Zealand` |

AI 423(최초 가공 국가)은 나라를 다섯까지 실을 수 있으므로, **나라마다 번호가 붙은 쌍**을
내놓습니다 — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`, `COUNTRY_CODE_NUMERIC_2`,
`COUNTRY_NAME_2`, … — 그리고 그 뒤에 요약인 `COUNTRY_LIST` 하나가 옵니다. 이 키들은
`COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` 상수에 1부터 세는 번호를 붙여
짓거나, 그저 `getInterpretations()`를 훑으십시오. 번호가 붙지 않은
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` 키는 AI 423에서 **내놓지 않습니다**.

### 통화(ISO 4217)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | 통화 코드 | 통화가 딸린 금액 AI(391n, 393n) |
| `CURRENCY_ALPHA` | 통화 문자 코드 | 통화가 딸린 금액 AI |
| `CURRENCY_NAME` | 통화명 | 통화가 딸린 금액 AI |

### 온도

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `TEMPERATURE` | 온도 | AI 4330–4333 |
| `TEMPERATURE_UNIT` | 온도 단위 | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | 온도 (서식 적용) | AI 4330–4333 |

### 성별(ISO 5218)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `SEX_CODE` | 성별 코드 | AI 7252 |
| `SEX_DESCRIPTION` | 성별 설명 | AI 7252 |

### 수산 어종(FAO)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `SPECIES_CODE` | 종 코드 | AI 7008 |
| `SPECIES_SCIENTIFIC` | 학명 | AI 7008 |
| `SPECIES_ENGLISH` | 일반명 | AI 7008 |
| `SPECIES_FAMILY` | 과 | AI 7008 |
| `SPECIES_ORDER` | 목 | AI 7008 |

### NATO 재고 번호(NSN)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `NSN_FSG` | 보급 그룹 | AI 7001 |
| `NSN_FSG_NAME` | 보급 그룹명 | AI 7001 |
| `NSN_FSCG` | 보급 분류 | AI 7001 |
| `NSN_FSCG_NAME` | 보급 분류명 | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | 국가 코드 | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | 국가 | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO 국가 코드 | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS 분류 | AI 7001 |
| `NSN_NIIN` | 국가 품목 번호 | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### 롤 제품

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | 롤 너비 (mm) | AI 8001 |
| `ROLL_LENGTH` | 롤 길이 (m) | AI 8001 |
| `CORE_DIAMETER` | 코어 지름 (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | 권취 방향 코드 | AI 8001 |
| `WINDING_DIRECTION` | 권취 방향 | AI 8001 |
| `SPLICES` | 이음 수 | AI 8001 |

### IBAN

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | 국가 코드 | AI 8007 |
| `IBAN_COUNTRY_NAME` | 국가 | AI 8007 |
| `IBAN_CHECK_DIGITS` | 검사 숫자 | AI 8007 |
| `IBAN_CHECK_VALID` | 검사 | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | 일련번호 | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | 검사 숫자 | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | 발급 기관 | AI 8040, 8041 |

열다섯 자리는 `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`로 나뉘며, RBI는 TAC의
앞 두 자리입니다. 그러므로 `IMEI_RBI`는 따로 떨어진 구간이 아니라 `IMEI_TAC`의
접두어입니다. `IMEI_FORMATTED`는 GSMA의 표준 표시 묶음인 `AA-BBBBBB-CCCCCC-D`(이를테면
`49-015420-323751-8`)를 그려 내며, 이는 TAC를 RBI 경계에서 나눕니다. 이제는 쓰이지 않는
최종 조립 코드가 시작되던 자리에서 자르는 옛 `6-2-6-1` 묶음은 내놓지 않습니다.

`IMEI_RBI_NAME`은 `ImeiRbiData`를 통해 RBI를 배정 기관의 이름으로 풀어내며, **맨 마지막에,
그리고 그 코드가 거기 실려 있을 때에만** 덧붙습니다. 그 표는 세 무리를 아우릅니다.

- **지금도 배정하는 곳** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, 그리고 `99`
  Global Hexadecimal Administrator와 `98`(예약됨).
- **시험용 범위** — `00`과 `02`–`09`. 실제 배정이 아니라 시험용 IMEI임을 나타냅니다.
  `ImeiRbiData.isTestCode(code)`로 물어보십시오.
- **더는 배정하지 않는 곳** — `49`(BZT/BAPT, 독일), `44`(BABT, 영국), `91`(MSAI, 인도)
  같은 옛 기관들. `ImeiRbiData.isNoLongerAllocating(code)`로 물어보십시오.
  이 코드를 지닌 기기는 평범하며 여전히 쓰이고 있습니다. 새로 배정하는 일만 멎었을 뿐이니,
  이는 보고용 정보일 뿐 유효성을 알리는 신호가 결코 아닙니다.

`IMEI_RBI_NAME`이 없다는 것은 “이 RBI가 우리 표에 없다”는 뜻이지 “유효하지 않은 IMEI”라는
뜻이 **아닙니다**. 이 표는 GSMA에서 곧바로가 아니라 공표된 RBI 목록에서 엮은 것이므로,
새로 지정된 기관을 뒤늦게 반영할 수 있습니다. 그것이 없다고 해서 어떠한 검증 결과도
이끌어 내지 마십시오. RBI는 검사 문자가 아닙니다. 해석 목록을 훑는 코드도 자리로 세어
찾지 말고 그것이 없을 수 있음을 견뎌야 합니다.

### SIM 식별자(EID / ICCID)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | 산업 분류 | AI 8042 |
| `EID_BODY` | EID 본문 | AI 8042 |
| `EID_CHECK_DIGIT` | 검사 숫자 | AI 8042 |
| `ICCID_BODY` | ICCID 본문 | AI 8043 |
| `ICCID_EXTENSION` | 확장 | AI 8043 |

`SIM_MII`는 앞의 **두** 자리(`89`)를 담습니다. ITU-T E.118이 전기통신에 배정한 쌍입니다.
ISO/IEC 7812 자체는 MII를 **첫 한 자리만으로** 정의하므로, `SIM_MII_NAME`은 `Iso7812Data`를
통해 그 앞자리 `8`에서 갈래를 풀어내어 “Healthcare, telecommunications and other future
industry assignments”를 내놓습니다. 그러므로 적격한 EID라면 이 값은 늘 같습니다. 이는
가려내기 위한 것이 아니라 표준으로 되짚을 수 있도록 알려 주는 것입니다.
`Iso7812Data.nameForCode(digit)`는 낱 자리를 받고, `nameForIdentifier(prefix)`는 좀 더 긴
접두어를 받아 그 앞자리를 읽습니다.

`SIM_MII_NAME`은 `EidEnricher`(AI 8042)만 내놓습니다. `IccidEnricher`(AI 8043)는 갈래 이름
없이 `SIM_MII`만 드러냅니다.

### 인증 참조 번호

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | 일련번호 | AI 7230–7239 |
| `CERT_SCHEME_CODE` | 인증 체계 코드 | AI 7230–7239 |
| `CERT_SCHEME_NAME` | 인증 체계 | AI 7230–7239 |
| `CERT_REFERENCE` | 인증 참조 | AI 7230–7239 |

### GS1 UIC

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC 코드 | AI 7040 |
| `UIC_EXTENSION_1` | 확장 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | 수입자 색인 | AI 7040 |

### 출생 순서

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | 출생 위치 | AI 7258 |
| `BIRTH_TOTAL` | 총 출생 수 | AI 7258 |
| `BIRTH_SEQUENCE` | 출생 순서 | AI 7258 |

### 국제 모델 번호(GMN)

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | 모델 참조 | AI 8013 |
| `GMN_CHECK_PAIR` | 검사 쌍 | AI 8013 |

### HIDRI

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | 기기 참조 | AI 8014 |
| `HIDRI_CHECK_PAIR` | 검사 쌍 | AI 8014 |

### CPID

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | 구성요소 및 부품 참조 | AI 8010–8011 |

### 소수와 계량값

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | 소수 값 | 묵시적 소수 자릿수를 지닌 숫자 AI(31xx–36xx) |
| `DECIMAL_AMOUNT` | 금액 | 가격 AI(390n–395n) |
| `DECIMAL_PERCENTAGE` | 백분율 | AI 394n |
| `DECIMAL_PLACES` | 소수 자릿수 | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE`와 함께 |
| `PERCENTAGE_FORMAT` | 백분율 형식 | AI 394n |
| `ISO_UNIT_CODE` | ISO 단위 코드 | 계량 AI |
| `ISO_UNIT_NAME` | ISO 단위명 | 계량 AI |
| `MONETARY_AMOUNT` | 화폐 금액 | 가격 AI |
| `MONETARY_AMOUNT_DISPLAY` | 화폐 금액 (서식 적용) | 가격 AI |

### 지리 좌표

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `LATITUDE` | 위도 | AI 4309 |
| `LONGITUDE` | 경도 | AI 4309 |
| `GEO_COORDINATES` | 지리 좌표 | AI 4309 |
| `LATITUDE_DMS` | 위도 (DMS) | AI 4309 |
| `LONGITUDE_DMS` | 경도 (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | 지리 좌표 (DMS) | AI 4309 |

### 생산 방식

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | 생산 방법 코드 | AI 7010 |
| `PRODUCTION_METHOD` | 생산 방법 | AI 7010 |

### AIDC 매체 종류

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC 매체 유형 코드 | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC 매체 유형 | AI 7241 |

### 전체 가운데 몇째

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | 조각 번호 | AI 8006 |
| `PIECE_TOTAL` | 총 조각 수 | AI 8006 |
| `PIECE_OF_TOTAL` | 전체 중 조각 | AI 8006 |

### 구성 요소 나누기

Java 보강기가 아니라 `content/ai-content.json`의 선언적 구성 요소 나누기가 내놓는 키들로,
복합 AI 값의 이름 붙은 부분들을 드러냅니다. 이 부록의 다른 모든 키와 달리 이것들은
**`GS1Constants_Enricher`에 상수가 없습니다**. 문자열 그대로 맞춰 보거나
`GS1AIInterpretation.getType()`에서 유형을 읽으십시오.

| 유형 키 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | 검사 숫자 | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | 일련번호 | AI 253, 255, 8003 |
| `POSTAL_CODE` | 우편번호 | AI 421 |
| `PROCESSOR_ID` | 가공자 식별자 | AI 7030–7039 |

여기의 `CHECK_DIGIT`는 일반적인 구성 요소 나누기 키로, 위에 늘어놓은 보강기별 키인
`GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT`, `EID_CHECK_DIGIT`와는
다릅니다.

### 그 밖의 것들

| 키 상수 | 표시 이름표 | 만들어 내는 곳 |
|--------------|---------------|-------------|
| `FLAG_VALUE` | 값 | 참거짓 / 표시 AI(4321–4323) |
| `DECODED_TEXT` | 디코딩된 텍스트 | 자유 서술 AI |
