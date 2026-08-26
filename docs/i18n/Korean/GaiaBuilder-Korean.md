# GaiaBuilder — 개발자 안내서

## 목차

1. [개요](#개요)
2. [GS1과 General Specifications에 대하여](#gs1과-general-specifications에-대하여)
3. [빠른 시작](#빠른-시작)
4. [어떻게 움직이는가](#어떻게-움직이는가)
5. [요소 스트링 짓기](#요소-스트링-짓기)
   - [속성 AI에는 그 식별 키가 필요합니다](#속성-ai에는-그-식별-키가-필요합니다)
6. [Digital Link URI 짓기](#digital-link-uri-짓기)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [검증과 오류](#검증과-오류)
   - [예외를 던지는 빌드 메서드](#예외를-던지는-빌드-메서드)
   - [예외를 던지지 않는 tryBuild\* 메서드](#예외를-던지지-않는-trybuild-메서드)
   - [오류 메시지의 언어](#오류-메시지의-언어)
   - [BuildResult](#buildresult)
9. [검사 숫자](#검사-숫자)
10. [스레드 안전성](#스레드-안전성)
11. [API 참조](#api-참조)

---

## 개요

`GaiaBuilder`는 [`GaiaParser`](GaiaParser-Korean.md)의 반대편입니다. 응용 식별자(AI)와 값의 쌍들을 적격한 GS1 **요소 스트링** 또는 **GS1 Digital Link URI**로 만들어 냅니다. AI와 그 온전한 데이터 값을 건네면, 빌더가 그것들을 엮고 `GaiaParser`가 쓰는 것과 같은 엔진으로 결과를 검증한 다음 출력을 그려 냅니다.

빌더는 *자신이 만들어 낸 후보 출력을 스스로 구문 분석하여* 검증하므로, 그것이 돌려주는 것은 무엇이든 `GaiaParser`로 되짚어 깨끗이 구문 분석됨이 보장됩니다. 무엇이 적격한가를 두고 이 둘이 어긋나는 일은 있을 수 없습니다.

**진입 클래스:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1과 General Specifications에 대하여

**GS1**은 공급망의 식별과 데이터 교환에 관한 개방형 표준을 개발하고 유지하는 국제 비영리 기관입니다. 그 표준은 유통, 보건의료, 물류, 급식을 비롯한 여러 산업에서 쓰이며, 소비재 포장의 상품 바코드에서부터 의약품 낱개 단위의 일련번호 추적에 이르기까지 폭넓게 걸쳐 있습니다.

이 빌더가 구현하는 모든 사항의 전거는 **GS1 General Specifications**입니다. 이 단일 문서가 다음을 규정합니다.

- 모든 응용 식별자(AI) 코드와 그 데이터 명칭, 형식, 검증 규칙
- AI 요소 스트링을 구성하고 부호화하기 위한 구문 규칙
- 바코드 심볼로지 요건과 AIM 코드 ID의 배정
- 검사 숫자 및 검사 문자 알고리즘
- 두 자리 연도의 해석(이동 창 규칙)
- Data Matrix, QR Code, GS1-128, GS1 DataBar 등 데이터 캐리어의 사양

GS1 General Specifications는 해마다 개정됩니다. 현행판과 관련 자료는 다음에서 얻을 수 있습니다.

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA는 GS1 General Specifications의 **26.0판(2026년 1월 승인)**을 구현합니다.

GS1 Digital Link URI는 짝을 이루는 표준 **GS1 Digital Link: URI Syntax**의 규율을 받습니다. 이 표준은 빌더가 Digital Link URI를 그려 낼 때 적용하는 기본 식별 키, 키 한정자의 순서, 그리고 데이터 속성의 부호화 방식을 규정합니다.

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA는 GS1 Digital Link: URI Syntax 표준의 **1.7.0판(2026년 8월 승인)**을 구현합니다.

이 문서 전체에서 절 참조는 GS1 General Specifications를 가리킵니다(예: “Table 7-5”, “section 7.12”). 다만 Digital Link의 절 번호(예: “§4.9”, “§4.12”)는 예외로, GS1 Digital Link: URI Syntax 표준을 가리킵니다.

---

## 빠른 시작

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

원시 AI 문자열보다 `GS1Constants_AICodes` 상수를 쓰십시오([파서 안내서의 부록 A](GaiaParser-Korean.md#부록-a--ai-문자열-상수)를 보십시오).

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## 어떻게 움직이는가

어떤 빌드든 같은 길을 밟습니다.

1. **엮기** — AI와 값의 쌍들을 이어 붙여 후보 요소 스트링을 만듭니다. *구분자를 필요로 하는* AI 뒤에는, 그것이 마지막 요소가 아닌 한, FNC1 그룹 구분자(`0x1D`)를 넣습니다. 길이가 미리 정해진 AI(GTIN, 날짜, 고정 길이 계량값)에는 구분자가 붙지 않고, 그 밖의 것에는 붙습니다. (알려지지 않은 AI는 이 걸음에 이르지도 못합니다. `ai(...)`가 곧바로 물리칩니다. [요소 스트링 짓기](#요소-스트링-짓기)를 보십시오.)
2. **검증** — 후보를 `GaiaParser`로 `CONTENT` 방식에서 구문 분석합니다. 값마다 해당 AI의 형식과 검사 숫자를 살피고, 구조 규칙(필요로 하는/배제하는 AI 짝)을 강제합니다. 구문 분석이 유효하지 않으면 빌드는 실패합니다.
3. **그려 내기** —
   - 요소 스트링이라면 검증된 객체의 `toElementString()`을 돌려줍니다.
   - Digital Link라면 요소마다 DL 구실(기본 키, 키 한정자, 데이터 속성)을 배정하고, 키 한정자의 차례를 검증하고, URI를 내놓은 다음, 그 URI를 **다시 구문 분석하여 유효한 Digital Link로 되돌아오는지 확인합니다**. 문자열을 엮고 퍼센트 부호화하는 걸음에 대한 방어적 검사입니다. 되돌아오지 않으면 `GaiaBuilderException`을 던집니다.

이는 `DLSyntaxParser`의 재구성 논리를 그대로 옮긴 것이므로, 구분자를 놓는 자리와 검증이 파서가 기대하는 바와 똑같습니다.

---

## 요소 스트링 짓기

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI**는 곧바로 검증됩니다. 알려진 GS1 응용 식별자가 아니면 `ai(...)`가 `IllegalArgumentException`을 던집니다. (빌더는 구문 분석에 앞서 AI와 값을 이어 붙이므로, `"99999"`처럼 알려지지 않았거나 너무 긴 AI는 여기서 잡아야 합니다. 그러지 않으면 소리 없이 다른 AI로 토큰화되어 버립니다.) **값**은 나중에, 빌드할 때 검증됩니다.
- 값은 검사 숫자를 포함해 **온전해야** 합니다. 빌더가 검사 숫자를 대신 셈해 붙여 주지 않습니다 — [검사 숫자](#검사-숫자)를 보십시오.
- AI는 더한 순서대로 나옵니다. GS1 구문이 요구하는 자리에는 빌더가 FNC1 구분자를 넣어 줍니다. 손수 구분자를 더하지 마십시오.
- **AI를 하나도 주지 않고** 빌드하면 `getErrors()` 목록이 빈 채로 `GaiaBuilderException("No AIs supplied")`이 던져집니다. `GaiaError`를 하나도 싣지 않는 유일한 실패입니다.
- 값이 형식이나 검사 숫자 규칙에 어긋나는 AI가 있으면 빌드는 실패합니다.

### 속성 AI에는 그 식별 키가 필요합니다

대부분의 AI는 GS1 General Specifications가 식별 키와 함께 다닐 것을 요구하는 *속성*이며, 빌더는 이를 강제합니다. 구문 단계 전체를 거쳐 검증하며, 이를 끌 방법은 없습니다. 배치/로트나 일련번호만으로는 유효한 요소 스트링이 **되지 않습니다**.

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

식별 키(GTIN `01`, SSCC `00`, GLN `414`, …)와 회사 내부용 AI(`90`–`99`)는 얼마든지 홀로 설 수 있습니다. 그 밖의 것에는 모두 동반자가 필요합니다.

> `GaiaParser`에는 `ParseConfig.skipRequiresCheck(true)`로 이 검사를 건너뛰라고 이를 수 있으나, `GaiaBuilder`는 그에 맞먹는 것을 일부러 두지 않았습니다. 표준을 따르는 출력을 내놓는 것이 그 소임이기 때문입니다. 일부러 온전하지 않은 요소 스트링을 엮으려면, 손수 이어 붙인 다음 그 검사를 끄고 구문 분석하십시오.

---

## Digital Link URI 짓기

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

유효한 Digital Link에는 **기본 식별 키**가 정확히 하나 있어야 합니다(이를테면 GTIN `01`, GLN `414`, SSCC `00`). 빌더는 건네받은 AI를 저마다 갈래 지웁니다.

| 구실 | 어떻게 그려지는가 | 예 |
|------|-------------|---------|
| 기본 식별 키 | 도메인/접두어 뒤의 경로 조각 | `/01/09506000134352` |
| 키 한정자(CPV `22`, 배치 `10`, 일련번호 `21`, …) | 그 뒤의 경로 조각들. **§4.9의 정규 순서**로(더한 순서가 아니라) | `/10/LOT-ABC` |
| 데이터 속성(그 밖의 모든 것) | 질의 매개변수. **AI 키의 사전순으로 정렬**(§4.12) | `?17=271231` |

한정자는 내놓을 때 다시 정렬되므로 순서에 어긋나게 건네도 괜찮습니다. `ai("10", …)`보다 `ai("21", …)`를 먼저 써도 여전히 `/10/LOT/21/SER`로 그려집니다. 기본 키가 허용하는 것이어야 하는 것은 그 *집합*뿐입니다.

경로와 질의의 값은 모두 퍼센트 부호화됩니다.

다음의 경우 빌드가 **실패합니다**(`GaiaBuilderException`을 던지거나, 실패한 `BuildResult`를 돌려줍니다).

- AI 가운데 기본 식별 키가 **없을** 때
- 기본 식별 키가 **둘 이상**일 때
- Digital Link에서 **금지된** AI(`03`, `8014`)가 있을 때
- 고른 기본 키에 대해 **키 한정자의 차례**가 유효하지 않을 때(이를테면 그 키에 딸리지 않은 한정자가 있거나, 한정자가 허용되는 순서에서 벗어났을 때)

---

## BuilderDigitalLinkConfig

스킴, 도메인, 경로 접두어, 덧붙일 질의 매개변수, 그리고 조각을 정하려면 `BuilderDigitalLinkConfig`를 건네십시오.

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

| 빌더 메서드 | 쓰임새 | 기본값 |
|----------------|---------|---------|
| `scheme(String)` | URI 스킴. `http` 또는 `https`여야 합니다 | `https` |
| `domain(String)` | 권한부 — 호스트 또는 `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | 첫 기본 키 앞의 경로 조각. 앞뒤의 빗금은 정규화됩니다 | *(없음)* |
| `baseUrl(String)` | URL 하나를 `scheme` + `domain` + `pathPrefix`로 갈라 주는 편의 메서드 | — |
| `addQueryParam(String, String)` | 덧붙일 질의 매개변수. AI 데이터 속성 **뒤에** 넣은 순서대로 붙으며, 퍼센트 부호화됩니다 | — |
| `fragment(String)` | URL 조각(앞의 `#` 없이). 퍼센트 부호화됩니다 | *(없음)* |

`build()`는 설정을 곧바로 검증합니다. `http(s)`가 아닌 스킴이나 빈 도메인은 `IllegalArgumentException`을 던집니다.

- `BuilderDigitalLinkConfig.canonical()`(별칭 `defaultConfig()`)은 덧붙임 없는 `https://id.gs1.org` 기본값입니다. 인자 없는 `buildDigitalLinkUri()`가 쓰는 것이자 `GS1AIObject.getCanonicalDigitalLink()`가 만들어 내는 것과 똑같습니다.
- `baseUrl("http://id.example.org:8080/r")` → 스킴 `http`, 도메인 `id.example.org:8080`, 경로 접두어 `/r`.
- 덧붙인 질의 매개변수는 언제나 AI에서 나온 속성 뒤에 오므로, AI의 정규 순서(§4.12)가 그대로 지켜집니다.

`BuilderDigitalLinkConfig`는 바뀌지 않으므로 인스턴스 하나를 마음껏 다시 쓰십시오.

---

## 검증과 오류

### 예외를 던지는 빌드 메서드

`buildElementString()`, `buildDigitalLinkUri()`, `buildDigitalLinkUri(BuilderDigitalLinkConfig)`는 주어진 AI로 적격한 출력을 만들 수 없을 때 **`GaiaBuilderException`**(확인되지 않는 `RuntimeException`)을 던집니다.

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **내용** 실패(검사 숫자가 틀림, 형식이 어긋남, 필요한 AI가 빠짐, 배제되는 AI가 있음)에서는 `getErrors()`가 파서의 `GaiaError`들을 싣습니다. [파서 안내서에 적힌](GaiaParser-Korean.md#gaiaerror) 바로 그 객체들입니다.
- **Digital Link 구조** 실패(기본 키 없음, 기본 키가 둘 이상, 금지된 AI, 유효하지 않은 키 한정자 차례)에서는 `getErrors()`가 빌더의 언어로 지역화된 `GaiaError` 하나(코드 `GE-L008`, `GE-L012`, `GE-L013`, `GE-L014` 가운데 하나)를 싣습니다.

### 예외를 던지지 않는 tryBuild\* 메서드

입력이 사용자에게서 오고 실패가 으레 있을 수 있으며 되살릴 수 있는 결말이라면, 예외로 흐름을 다스리는 대신 `tryBuild*` 갈래를 쓰십시오. 이들은 예외를 던지지 않고 [`BuildResult`](#buildresult)를 돌려줍니다.

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

| 예외를 던지는 것 | 던지지 않는 것 |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

각 `tryBuild*` 메서드는 예외를 던지는 그 짝과 검증의 알맹이를 함께 씁니다. 다른 것은 실패를 알리는 방식뿐입니다.

### 오류 메시지의 언어

내용 검증 오류는 지역화된 오류 목록에서 가져옵니다. `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`가 싣는 `GaiaError` 메시지의 언어를 고르려면 `language(...)`를 부르십시오. 기본값은 영어입니다.

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

이는 `GaiaParser`가 `ParseConfig`로 받는 것과 같은 `GaiaConstants.Language` 설정이므로, 빌더와 파서는 똑같이 지역화됩니다.

**내용** `GaiaError` 메시지와 **Digital Link 구조** 실패(기본 키 없음, 기본 키가 둘 이상, 금지된 AI, 유효하지 않은 키 한정자 차례) 모두 함께 쓰는 오류 목록을 통해 지역화됩니다. 뒤의 것들은 코드 `GE-L008`, `GE-L012`, `GE-L013`, `GE-L014`를 씁니다.

### BuildResult

`BuildResult`(`tools.pantheum.gaia.result` 패키지)는 `tryBuild*` 호출의 결말을 서술하는, 바뀌지 않는 값 타입입니다.

| 메서드 | 성공했을 때 | 실패했을 때 |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | 그려 낸 문자열 | `null` |
| `getMessage()` | `null` | 실패에 대한 설명 |
| `getErrors()` | 빈 목록 | 검증 오류들(`GaiaBuilderException.getErrors()`와 같음) |

---

## 검사 숫자

빌더는 검사 숫자를 검증하되 셈해 내지는 **않습니다**. 값에는 이미 검사 숫자가 들어 있어야 합니다. 검사 숫자를 셈하려면 `GS1Utils.calculateCheckDigit`를 쓰십시오.

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)`은 건네받은 숫자들에 GS1의 표준 모듈로 10 알고리즘을 적용하여 `0`에서 `9` 사이의 검사 숫자를 돌려줍니다. 입력이 null이거나 비었거나 숫자가 아니면 `-1`을 돌려줍니다.

---

## 스레드 안전성

`GaiaBuilder`는 스레드에 안전하지 **않으며** 한 번 쓰고 버리도록 만들어졌습니다. `create()`를 부르고, AI를 더하고, 한 번 빌드하십시오. 출력마다 새 빌더를 만드시고, 하나를 여러 스레드가 나눠 쓰지 마십시오.

`BuilderDigitalLinkConfig`(그리고 그것이 내놓는 `BuildResult`)는 바뀌지 않으므로 마음껏 나눠 쓸 수 있습니다. 시작할 때 설정을 하나 지어 두고 여러 빌더에 걸쳐 다시 쓰십시오.

---

## API 참조

### `GaiaBuilder`

| 메서드 | 설명 |
|--------|-------------|
| `static GaiaBuilder create()` | 새 빈 빌더를 시작합니다. |
| `GaiaBuilder ai(String ai, String value)` | AI와 그 온전한 값을 덧붙입니다. 둘 가운데 하나가 `null`이거나 `ai`가 알려진 GS1 응용 식별자가 아니면 `IllegalArgumentException`을 던집니다. |
| `GaiaBuilder language(GaiaConstants.Language language)` | 내용 검증 오류 메시지의 언어를 정합니다(기본값 영어). `null`은 무시됩니다. |
| `String buildElementString()` | GS1 요소 스트링을 그려 냅니다. 실패하면 `GaiaBuilderException`을 던집니다. |
| `String buildDigitalLinkUri()` | 정규 Digital Link URI를 그려 냅니다. 실패하면 `GaiaBuilderException`을 던집니다. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config`에 따라 Digital Link URI를 그려 냅니다. 실패하면 `GaiaBuilderException`을 던집니다. |
| `BuildResult tryBuildElementString()` | 예외를 던지지 않는 요소 스트링 빌드. |
| `BuildResult tryBuildDigitalLinkUri()` | 예외를 던지지 않는 정규 Digital Link 빌드. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config`에 따른, 예외를 던지지 않는 Digital Link 빌드. |

### `BuilderDigitalLinkConfig`

| 구성원 | 설명 |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | `https://id.gs1.org` 기본값. |
| `static Builder builder()` | 새 설정 빌더. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | 풀어낸 스킴, 권한부, 경로 접두어. |
| `getExtraQueryParams()` | 덧붙인 질의 매개변수를 넣은 순서대로. |
| `getFragment()` | 조각, 또는 `null`. |

### `GaiaBuilderException`

| 구성원 | 설명 |
|--------|-------------|
| `getErrors()` | 실패를 일으킨 `GaiaError`들 — 내용 실패라면 파서의 오류들, Digital Link 구조 실패라면 오류 하나(`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). 결코 `null`이 아닙니다. |

### `BuildResult`

| 구성원 | 설명 |
|--------|-------------|
| `isSuccess()` | 빌드가 성공했는지 여부. |
| `getValue()` | 성공했을 때 그려 낸 출력. 실패했으면 `null`. |
| `getMessage()` | 실패했을 때 실패에 대한 설명. 성공했으면 `null`. |
| `getErrors()` | 실패했을 때 검증 오류들. 성공했으면 빈 목록. 결코 `null`이 아닙니다. |
| `getTiming()` | 그 빌드의 `ProcessingTiming`(시작 시각, 처리에 걸린 시간), 또는 `null`. |

---

함께 보십시오: 구문 분석 쪽과 AI 요소 모형, 오류 참조, AI 및 해석 상수 부록은 **[GaiaParser — 개발자 안내서](GaiaParser-Korean.md)**에 있습니다.
