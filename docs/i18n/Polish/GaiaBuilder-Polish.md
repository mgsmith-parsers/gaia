# GaiaBuilder — Podręcznik programisty

## Spis treści

1. [Przegląd](#przegląd)
2. [O GS1 i dokumencie General Specifications](#o-gs1-i-dokumencie-general-specifications)
3. [Szybki start](#szybki-start)
4. [Zasada działania](#zasada-działania)
5. [Budowanie ciągów elementów](#budowanie-ciągów-elementów)
   - [Atrybutowe AI wymagają swojego klucza identyfikacyjnego](#atrybutowe-ai-wymagają-swojego-klucza-identyfikacyjnego)
6. [Budowanie identyfikatorów URI Digital Link](#budowanie-identyfikatorów-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Walidacja i błędy](#walidacja-i-błędy)
   - [Metody budowania zgłaszające wyjątki](#metody-budowania-zgłaszające-wyjątki)
   - [Metody tryBuild\* bez wyjątków](#metody-trybuild-bez-wyjątków)
   - [Język komunikatów o błędach](#język-komunikatów-o-błędach)
   - [BuildResult](#buildresult)
9. [Cyfry kontrolne](#cyfry-kontrolne)
10. [Bezpieczeństwo wątkowe](#bezpieczeństwo-wątkowe)
11. [Dokumentacja API](#dokumentacja-api)

---

## Przegląd

`GaiaBuilder` jest odwrotnością [`GaiaParser`](GaiaParser-Polish.md): zamienia zestaw par identyfikator zastosowania (AI) / wartość w poprawnie zbudowany **ciąg elementów** GS1 albo **identyfikator URI GS1 Digital Link**. Ty dostarczasz identyfikatory AI i ich pełne wartości danych; builder je składa, sprawdza wynik tym samym mechanizmem, z którego korzysta `GaiaParser`, i wytwarza dane wyjściowe.

Ponieważ builder sprawdza poprawność, *analizując własne kandydujące dane wyjściowe*, wszystko, co zwraca, na pewno da się bezbłędnie odczytać z powrotem przez `GaiaParser` — oba nigdy nie mogą się różnić co do tego, co jest poprawnie zbudowane.

**Klasa wejściowa:** `tools.pantheum.gaia.GaiaBuilder`

---

## O GS1 i dokumencie General Specifications

**GS1** to ogólnoświatowa organizacja non-profit, która opracowuje i utrzymuje otwarte standardy identyfikacji i wymiany danych w łańcuchach dostaw. Jej standardy stosuje się w handlu detalicznym, ochronie zdrowia, logistyce, gastronomii i wielu innych branżach — od kodów kreskowych na opakowaniach konsumenckich po serializowane śledzenie dawek farmaceutycznych.

Miarodajnym źródłem dla wszystkiego, co realizuje ten builder, jest dokument **GS1 General Specifications** — jeden dokument, który określa:

- Wszystkie kody identyfikatorów zastosowania (AI), ich tytuły danych, formaty i reguły walidacji
- Reguły składni budowania i kodowania ciągów elementów AI
- Wymagania symbolik kodów kreskowych i przydział identyfikatorów symboliki AIM
- Algorytmy cyfry kontrolnej i znaku kontrolnego
- Rozwijanie dwucyfrowych oznaczeń roku (reguła przesuwnego okna)
- Specyfikacje Data Matrix, QR Code, GS1-128, GS1 DataBar i pozostałych nośników danych

Dokument GS1 General Specifications jest aktualizowany co roku. Obowiązujące wydanie i materiały towarzyszące są dostępne pod adresem:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA realizuje **wydanie 26.0 (zatwierdzone w styczniu 2026 r.)** dokumentu GS1 General Specifications.

Identyfikatory URI GS1 Digital Link podlegają odrębnemu standardowi **GS1 Digital Link: URI Syntax**, który określa podstawowe klucze identyfikacyjne, kolejność kwalifikatorów klucza oraz kodowanie atrybutów danych stosowane przez builder przy wytwarzaniu identyfikatorów URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA realizuje **wydanie 1.7.0 (zatwierdzone w sierpniu 2026 r.)** standardu GS1 Digital Link: URI Syntax.

Odwołania do punktów w całym niniejszym dokumencie dotyczą dokumentu GS1 General Specifications (na przykład „Table 7-5”, „section 7.12”), z wyjątkiem numerów punktów Digital Link (na przykład „§4.9”, „§4.12”), które odsyłają do standardu GS1 Digital Link: URI Syntax.

---

## Szybki start

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

Zamiast surowych łańcuchów AI używaj stałych `GS1Constants_AICodes` (zobacz [dodatek A podręcznika analizatora](GaiaParser-Polish.md#dodatek-a--stałe-łańcuchowe-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Zasada działania

Każde budowanie przebiega tą samą drogą:

1. **Składanie** — pary AI/wartość są łączone w kandydujący ciąg elementów. Po każdym AI, który *wymaga separatora* i nie jest ostatnim elementem, wstawiany jest separator grupy FNC1 (`0x1D`). Identyfikatory AI o z góry określonej długości (GTIN, daty, miary o stałej długości) nie otrzymują separatora; wszystkie pozostałe — tak. (Nierozpoznane AI nigdy nie docierają do tego kroku — `ai(...)` odrzuca je natychmiast; zobacz [Budowanie ciągów elementów](#budowanie-ciągów-elementów).)
2. **Walidacja** — kandydat jest analizowany w trybie `CONTENT` przez `GaiaParser`. Każda wartość jest porównywana z formatem i cyfrą kontrolną swojego AI, egzekwowane są też reguły strukturalne (wymagane lub wykluczające się pary AI). Jeżeli analiza nie jest poprawna, budowanie kończy się niepowodzeniem.
3. **Wytworzenie** —
   - Dla ciągu elementów zwracany jest `toElementString()` sprawdzonego obiektu.
   - Dla Digital Link każdy element otrzymuje swoją rolę DL (klucz podstawowy, kwalifikator klucza albo atrybut danych), sprawdzana jest sekwencja kwalifikatorów klucza, wytwarzany jest identyfikator URI, a następnie **jest on ponownie analizowany, aby potwierdzić, że da się go odczytać z powrotem jako poprawny Digital Link** — to zabezpieczająca kontrola składania łańcucha i kodowania procentowego. Jeżeli taki obieg się nie powiedzie, zgłaszany jest wyjątek `GaiaBuilderException`.

Odwzorowuje to logikę odtwarzania z `DLSyntaxParser`, dzięki czemu rozmieszczenie separatorów i walidacja są identyczne z tym, czego oczekuje analizator.

---

## Budowanie ciągów elementów

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** jest sprawdzany natychmiast: `ai(...)` zgłasza `IllegalArgumentException`, jeżeli nie jest to rozpoznany identyfikator zastosowania GS1. (Builder łączy AI z wartością przed analizą, więc nierozpoznany lub zbyt długi AI, taki jak `"99999"`, musi zostać przechwycony tutaj — inaczej zostałby po cichu podzielony na tokeny jako inny AI.) **Wartość** sprawdzana jest później, w chwili budowania.
- Wartości muszą być **kompletne**, łącznie z ewentualną cyfrą kontrolną. Builder nie oblicza ani nie dodaje cyfr kontrolnych za ciebie — zobacz [Cyfry kontrolne](#cyfry-kontrolne).
- Identyfikatory AI wytwarzane są w kolejności ich dodawania. Builder wstawia separatory FNC1 tam, gdzie wymaga tego składnia GS1; sam ich nie dodawaj.
- Budowanie **bez żadnego AI** zgłasza `GaiaBuilderException("No AIs supplied")` z pustą listą `getErrors()` — to jedyne niepowodzenie, które nie niesie żadnego `GaiaError`.
- AI, którego wartość narusza regułę formatu lub cyfry kontrolnej, powoduje niepowodzenie budowania.

### Atrybutowe AI wymagają swojego klucza identyfikacyjnego

Większość identyfikatorów AI to *atrybuty*, którym dokument GS1 General Specifications nakazuje towarzyszyć kluczowi identyfikacyjnemu, a builder tego pilnuje: sprawdza poprawność przez pełny etap składni, bez możliwości rezygnacji. Sama partia albo sam numer seryjny **nie** stanowią poprawnego ciągu elementów:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Klucze identyfikacyjne (GTIN `01`, SSCC `00`, GLN `414`, …) oraz AI do użytku wewnętrznego firmy (`90`–`99`) mogą występować samodzielnie całkiem zasadnie. Wszystko pozostałe potrzebuje swojego towarzysza.

> `GaiaParser` można poprosić o pominięcie tej kontroli przez `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` celowo nie udostępnia odpowiednika — ma wytwarzać dane wyjściowe zgodne ze standardem. Aby złożyć celowo niepełny ciąg elementów, połącz go samodzielnie i przeanalizuj z wyłączoną kontrolą.

---

## Budowanie identyfikatorów URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Poprawny Digital Link wymaga dokładnie jednego **podstawowego klucza identyfikacyjnego** (na przykład GTIN `01`, GLN `414`, SSCC `00`). Builder klasyfikuje każdy dostarczony AI:

| Rola | Wytwarzana jako | Przykład |
|------|-------------|---------|
| Podstawowy klucz identyfikacyjny | Segment ścieżki po domenie lub przedrostku | `/01/09506000134352` |
| Kwalifikator klucza (CPV `22`, partia `10`, numer seryjny `21`, …) | Kolejne segmenty ścieżki, w **kanonicznej kolejności z §4.9** (a nie w kolejności dodawania) | `/10/LOT-ABC` |
| Atrybut danych (cała reszta) | Parametry zapytania, **posortowane leksykalnie wg klucza AI** (§4.12) | `?17=271231` |

Ponieważ kwalifikatory są porządkowane na nowo przy wytwarzaniu, podanie ich w dowolnej kolejności nie stanowi problemu: `ai("21", …)` przed `ai("10", …)` nadal daje `/10/LOT/21/SER`. Dopuszczalny dla klucza podstawowego musi być jedynie sam *zbiór*.

Wartości są kodowane procentowo zarówno w ścieżce, jak i w zapytaniu.

Budowanie **kończy się niepowodzeniem** (zgłasza `GaiaBuilderException` albo zwraca nieudany `BuildResult`), gdy:

- wśród identyfikatorów AI **nie ma** podstawowego klucza identyfikacyjnego;
- podstawowych kluczy identyfikacyjnych jest **więcej niż jeden**;
- jakiś AI jest **zabroniony** w Digital Link (`03`, `8014`);
- **sekwencja kwalifikatorów klucza** jest niepoprawna dla wybranego klucza podstawowego (na przykład kwalifikator nienależący do tego klucza albo kwalifikatory poza dozwoloną kolejnością).

---

## BuilderDigitalLinkConfig

Przekaż `BuilderDigitalLinkConfig`, aby sterować schematem, domeną, przedrostkiem ścieżki, dodatkowymi parametrami zapytania i fragmentem:

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

| Metoda konstruktora | Przeznaczenie | Domyślnie |
|----------------|---------|---------|
| `scheme(String)` | Schemat URI; musi być `http` albo `https` | `https` |
| `domain(String)` | Autorytet — host albo `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Segmenty ścieżki przed pierwszym kluczem podstawowym; ukośniki na początku i końcu są normalizowane | *(brak)* |
| `baseUrl(String)` | Skrót rozkładający adres URL na `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Dodatkowy parametr zapytania, dołączany **po** atrybutach danych AI, w kolejności wstawiania; kodowany procentowo | — |
| `fragment(String)` | Fragment adresu URL (bez wiodącego `#`); kodowany procentowo | *(brak)* |

`build()` sprawdza konfigurację natychmiast: schemat inny niż `http(s)` albo pusta domena zgłaszają `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) to domyślne `https://id.gs1.org` bez dodatków — dokładnie to, czego używa `buildDigitalLinkUri()` bez argumentów i co wytwarza `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → schemat `http`, domena `id.example.org:8080`, przedrostek ścieżki `/r`.
- Dodatkowe parametry zapytania zawsze następują po atrybutach wyprowadzonych z AI, dzięki czemu zachowana zostaje kanoniczna kolejność AI (§4.12).

`BuilderDigitalLinkConfig` jest niezmienny; jedną instancję można dowolnie wykorzystywać wielokrotnie.

---

## Walidacja i błędy

### Metody budowania zgłaszające wyjątki

`buildElementString()`, `buildDigitalLinkUri()` i `buildDigitalLinkUri(BuilderDigitalLinkConfig)` zgłaszają **`GaiaBuilderException`** (niekontrolowany `RuntimeException`), gdy z identyfikatorów AI nie da się zbudować poprawnych danych wyjściowych:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Przy niepowodzeniach **treści** (zła cyfra kontrolna, niezgodny format, brakujący lub wykluczony AI) `getErrors()` niesie obiekty `GaiaError` analizatora — te same, które [opisano w podręczniku analizatora](GaiaParser-Polish.md#gaiaerror).
- Przy niepowodzeniach **strukturalnych Digital Link** (brak klucza podstawowego, więcej niż jeden klucz podstawowy, zabroniony AI, niepoprawna sekwencja kwalifikatorów klucza) `getErrors()` niesie pojedynczy `GaiaError` (kod `GE-L008`, `GE-L012`, `GE-L013` albo `GE-L014`) zlokalizowany do języka buildera.

### Metody tryBuild\* bez wyjątków

Gdy dane wejściowe pochodzą od użytkownika, a niepowodzenie jest oczekiwanym i naprawialnym wynikiem, korzystaj z odmian `tryBuild*` zamiast sterowania przepływem przez wyjątki. Zwracają one [`BuildResult`](#buildresult), zamiast zgłaszać wyjątek:

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

| Z wyjątkiem | Bez wyjątku |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Każda metoda `tryBuild*` dzieli ten sam rdzeń walidacji ze swoją zgłaszającą wyjątki bliźniaczką; różni się wyłącznie sposób sygnalizowania niepowodzenia.

### Język komunikatów o błędach

Błędy walidacji treści pochodzą ze zlokalizowanego katalogu błędów. Wywołaj `language(...)`, aby wybrać język komunikatów obiektów `GaiaError` niesionych przez `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; domyślnie jest to angielski:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

To to samo ustawienie `GaiaConstants.Language`, które `GaiaParser` przyjmuje przez `ParseConfig`, więc builder i analizator lokalizują się identycznie.

Zarówno komunikaty `GaiaError` dotyczące **treści**, jak i niepowodzenia **strukturalne Digital Link** (brak klucza podstawowego, więcej niż jeden klucz podstawowy, zabroniony AI, niepoprawna sekwencja kwalifikatorów klucza) lokalizowane są przez wspólny katalog błędów — te ostatnie za pomocą kodów `GE-L008`, `GE-L012`, `GE-L013` i `GE-L014`.

### BuildResult

`BuildResult` (w pakiecie `tools.pantheum.gaia.result`) to niezmienny typ wartościowy opisujący wynik wywołania `tryBuild*`:

| Metoda | Przy powodzeniu | Przy niepowodzeniu |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | wytworzony łańcuch | `null` |
| `getMessage()` | `null` | opis niepowodzenia |
| `getErrors()` | pusta lista | błędy walidacji (te same co w `GaiaBuilderException.getErrors()`) |

---

## Cyfry kontrolne

Builder sprawdza cyfry kontrolne, ale ich **nie** oblicza — wartości muszą już zawierać własną. Aby obliczyć cyfrę kontrolną, użyj `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` stosuje standardowy algorytm GS1 modulo 10 do podanych cyfr i zwraca cyfrę kontrolną `0–9` albo `-1`, jeżeli dane wejściowe są puste, mają wartość null lub nie są liczbowe.

---

## Bezpieczeństwo wątkowe

`GaiaBuilder` **nie** jest bezpieczny wątkowo i przeznaczono go do jednorazowego użycia: wywołaj `create()`, dodaj identyfikatory AI, zbuduj raz. Twórz nowy builder dla każdego wyniku; nie współdziel jednego między wątkami.

`BuilderDigitalLinkConfig` (oraz wytwarzane przez niego obiekty `BuildResult`) są niezmienne i można je swobodnie współdzielić — zbuduj konfigurację raz przy starcie i wykorzystuj ją w wielu builderach.

---

## Dokumentacja API

### `GaiaBuilder`

| Metoda | Opis |
|--------|-------------|
| `static GaiaBuilder create()` | Rozpoczyna nowy, pusty builder. |
| `GaiaBuilder ai(String ai, String value)` | Dodaje AI wraz z jego pełną wartością. Zgłasza `IllegalArgumentException`, jeżeli którykolwiek z argumentów ma wartość `null` albo jeżeli `ai` nie jest rozpoznanym identyfikatorem zastosowania GS1. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Ustawia język komunikatów o błędach walidacji treści (domyślnie angielski). `null` jest pomijany. |
| `String buildElementString()` | Wytwarza ciąg elementów GS1. Przy niepowodzeniu zgłasza `GaiaBuilderException`. |
| `String buildDigitalLinkUri()` | Wytwarza kanoniczny identyfikator URI Digital Link. Przy niepowodzeniu zgłasza `GaiaBuilderException`. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Wytwarza identyfikator URI Digital Link zgodnie z `config`. Przy niepowodzeniu zgłasza `GaiaBuilderException`. |
| `BuildResult tryBuildElementString()` | Budowanie ciągu elementów bez wyjątków. |
| `BuildResult tryBuildDigitalLinkUri()` | Budowanie kanonicznego Digital Link bez wyjątków. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Budowanie Digital Link zgodnie z `config`, bez wyjątków. |

### `BuilderDigitalLinkConfig`

| Składowa | Opis |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Domyślne `https://id.gs1.org`. |
| `static Builder builder()` | Nowy konstruktor konfiguracji. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Ustalony schemat, autorytet i przedrostek ścieżki. |
| `getExtraQueryParams()` | Dodatkowe parametry zapytania, w kolejności wstawiania. |
| `getFragment()` | Fragment albo `null`. |

### `GaiaBuilderException`

| Składowa | Opis |
|--------|-------------|
| `getErrors()` | Obiekty `GaiaError`, które spowodowały niepowodzenie — błędy analizatora przy niepowodzeniu treści albo pojedynczy strukturalny błąd Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nigdy `null`. |

### `BuildResult`

| Składowa | Opis |
|--------|-------------|
| `isSuccess()` | Czy budowanie się powiodło. |
| `getValue()` | Wytworzone dane wyjściowe przy powodzeniu; `null` przy niepowodzeniu. |
| `getMessage()` | Opis niepowodzenia przy niepowodzeniu; `null` przy powodzeniu. |
| `getErrors()` | Błędy walidacji przy niepowodzeniu; pusta lista przy powodzeniu. Nigdy `null`. |
| `getTiming()` | Obiekt `ProcessingTiming` budowania (czas rozpoczęcia, czas trwania przetwarzania) albo `null`. |

---

Zobacz także: **[GaiaParser — Podręcznik programisty](GaiaParser-Polish.md)**, opisujący stronę analizy, model elementu AI, wykaz błędów oraz dodatki ze stałymi AI i interpretacji.
