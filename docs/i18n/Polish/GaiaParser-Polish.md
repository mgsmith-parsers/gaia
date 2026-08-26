# GAIA (GS1 Application Identifiers Analyser) — Podręcznik programisty

## Spis treści

1. [Przegląd](#przegląd)
2. [O GS1 i dokumencie General Specifications](#o-gs1-i-dokumencie-general-specifications)
3. [Identyfikatory zastosowania GS1](#identyfikatory-zastosowania-gs1)
4. [Szybki start](#szybki-start)
5. [Potok przetwarzania analizy](#potok-przetwarzania-analizy)
   - [Etap wstępny — modyfikatory wejścia](#etap-wstępny--modyfikatory-wejścia)
   - [Etap 0 — identyfikator korelacji](#etap-0--identyfikator-korelacji)
   - [Etap 1 — kierowanie danych wejściowych](#etap-1--kierowanie-danych-wejściowych)
   - [Etap 2 — składnia](#etap-2--składnia)
   - [Etap 3 — treść](#etap-3--treść)
   - [Etap 4 — interpretacja](#etap-4--interpretacja)
6. [Konfiguracja analizy (`ParseConfig`)](#konfiguracja-analizy-parseconfig)
   - [Opcje](#opcje)
   - [Zlokalizowane komunikaty i etykiety](#zlokalizowane-komunikaty-i-etykiety)
   - [Formatowanie dat](#formatowanie-dat)
7. [Modyfikatory wejścia](#modyfikatory-wejścia)
   - [Wbudowane modyfikatory](#wbudowane-modyfikatory)
   - [Pisanie modyfikatora](#pisanie-modyfikatora)
   - [Rejestrowanie modyfikatorów](#rejestrowanie-modyfikatorów)
   - [Sprawdzanie, co zrobił modyfikator](#sprawdzanie-co-zrobił-modyfikator)
   - [Obsługa błędów modyfikatora](#obsługa-błędów-modyfikatora)
8. [Tryby analizy](#tryby-analizy)
   - [Tryb DATA_CARRIER](#tryb-data_carrier)
   - [Tryb SYNTAX](#tryb-syntax)
   - [Tryb CONTENT](#tryb-content)
   - [Tryb INTERPRETATION (domyślny)](#tryb-interpretation-domyślny)
9. [Identyfikator korelacji](#identyfikator-korelacji)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Praca z wynikami](#praca-z-wynikami)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry i DataCarrierType](#datacarrierentry-i-datacarriertype)
12. [Wykaz błędów](#wykaz-błędów)
13. [Bezpieczeństwo wątkowe](#bezpieczeństwo-wątkowe)
14. [Dodatek A — stałe łańcuchowe AI](#dodatek-a--stałe-łańcuchowe-ai)
    - [Identyfikacja i serializacja](#identyfikacja-i-serializacja)
    - [Daty i godziny](#daty-i-godziny)
    - [Ilość i miara — miara zmienna (metryczna)](#ilość-i-miara--miara-zmienna-metryczna)
    - [Ilość i miara — miara zmienna (imperialna / USA)](#ilość-i-miara--miara-zmienna-imperialna--usa)
    - [Ceny i kwoty pieniężne](#ceny-i-kwoty-pieniężne)
    - [Lokalizacja i wysyłka](#lokalizacja-i-wysyłka)
    - [Cechy produktu i identyfikowalność](#cechy-produktu-i-identyfikowalność)
    - [Krajowe numery refundacyjne w ochronie zdrowia (NHRN)](#krajowe-numery-refundacyjne-w-ochronie-zdrowia-nhrn)
    - [Ochrona zdrowia, GMN, HIDRI, CPID, dane osobowe](#ochrona-zdrowia-gmn-hidri-cpid-dane-osobowe)
    - [Użytek wewnętrzny / firmowy](#użytek-wewnętrzny--firmowy)
15. [Dodatek B — stałe kluczy interpretacji](#dodatek-b--stałe-kluczy-interpretacji)
    - [Data i godzina](#data-i-godzina)
    - [Data zbioru](#data-zbioru)
    - [Prefiks firmy GS1](#prefiks-firmy-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Kraj (ISO 3166)](#kraj-iso-3166)
    - [Waluta (ISO 4217)](#waluta-iso-4217)
    - [Temperatura](#temperatura)
    - [Płeć (ISO 5218)](#płeć-iso-5218)
    - [Gatunki wodne (FAO)](#gatunki-wodne-fao)
    - [Numer magazynowy NATO (NSN)](#numer-magazynowy-nato-nsn)
    - [Produkty w rolkach](#produkty-w-rolkach)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identyfikatory SIM (EID / ICCID)](#identyfikatory-sim-eid--iccid)
    - [Odniesienie certyfikacyjne](#odniesienie-certyfikacyjne)
    - [GS1 UIC](#gs1-uic)
    - [Kolejność urodzenia noworodka](#kolejność-urodzenia-noworodka)
    - [Globalny numer modelu (GMN)](#globalny-numer-modelu-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Wartości dziesiętne i miarowe](#wartości-dziesiętne-i-miarowe)
    - [Współrzędne geograficzne](#współrzędne-geograficzne)
    - [Metoda produkcji](#metoda-produkcji)
    - [Typ nośnika AIDC](#typ-nośnika-aidc)
    - [Sztuka z całości](#sztuka-z-całości)
    - [Podziały na składniki](#podziały-na-składniki)
    - [Różne](#różne)

---

## Przegląd

`GaiaParser` to punkt wejścia do analizy ciągów elementów z identyfikatorami zastosowania (AI) GS1. Przyjmuje surowe dane wyjściowe czytnika w dowolnej z poniższych postaci i zwraca ustrukturyzowany obiekt `ParseResult` zawierający wszystkie rozpoznane AI, błędy walidacji oraz — opcjonalnie — interpretacje czytelne dla człowieka:

- Zwykły ciąg elementów AI: `0109506000134352`
- Ciąg elementów poprzedzony identyfikatorem symboliki AIM: `]C10109506000134352`
- Identyfikator URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Dowolna z powyższych postaci, opcjonalnie poprzedzona ośmiocyfrowym identyfikatorem korelacji: `12345678~0109506000134352`

**Klasa wejściowa:** `tools.pantheum.gaia.GaiaParser`

> **Zaczynasz z Gaią?** Rozpocznij od **[przewodnika szybkiego startu GaiaParser](GaiaParser-QuickStart-Polish.md)** — zależność, pierwsza analiza i garść typowych pułapek, w około dziesięć minut. Niniejszy podręcznik stanowi pełne odniesienie.

> Operację odwrotną — *budowanie* poprawnych ciągów elementów i identyfikatorów URI Digital Link z par AI/wartość — opisuje **[GaiaBuilder — Podręcznik programisty](GaiaBuilder-Polish.md)**.

---

## O GS1 i dokumencie General Specifications

**GS1** to ogólnoświatowa organizacja non-profit, która opracowuje i utrzymuje otwarte standardy identyfikacji i wymiany danych w łańcuchach dostaw. Jej standardy stosuje się w handlu detalicznym, ochronie zdrowia, logistyce, gastronomii i wielu innych branżach — od kodów kreskowych na opakowaniach konsumenckich po serializowane śledzenie dawek farmaceutycznych.

Miarodajnym źródłem dla wszystkiego, co realizuje ten analizator, jest dokument **GS1 General Specifications** — jeden dokument, który określa:

- Wszystkie kody identyfikatorów zastosowania (AI), ich tytuły danych, formaty i reguły walidacji
- Reguły składni budowania i kodowania ciągów elementów AI
- Wymagania symbolik kodów kreskowych i przydział identyfikatorów symboliki AIM
- Algorytmy cyfry kontrolnej i znaku kontrolnego
- Rozwijanie dwucyfrowych oznaczeń roku (reguła przesuwnego okna)
- Specyfikacje Data Matrix, QR Code, GS1-128, GS1 DataBar i pozostałych nośników danych

Dokument GS1 General Specifications jest aktualizowany co roku. Obowiązujące wydanie i materiały towarzyszące są dostępne pod adresem:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA realizuje **wydanie 26.0 (zatwierdzone w styczniu 2026 r.)** dokumentu GS1 General Specifications.

Identyfikatory URI GS1 Digital Link podlegają odrębnemu standardowi **GS1 Digital Link: URI Syntax**, który określa podstawowe klucze identyfikacyjne, kolejność kwalifikatorów klucza oraz kodowanie atrybutów danych stosowane przez analizator do danych wejściowych typu Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA realizuje **wydanie 1.7.0 (zatwierdzone w sierpniu 2026 r.)** standardu GS1 Digital Link: URI Syntax.

Odwołania do punktów w całym niniejszym dokumencie dotyczą dokumentu GS1 General Specifications (na przykład „Table 7-5”, „section 7.12”), z wyjątkiem numerów punktów Digital Link (na przykład „§4.9”, „§4.12”), które odsyłają do standardu GS1 Digital Link: URI Syntax.

---

## Identyfikatory zastosowania GS1

**Identyfikator zastosowania (AI) GS1** to krótki przedrostek liczbowy — od dwóch do czterech cyfr — który określa znaczenie i format bezpośrednio po nim następujących danych. Identyfikatory AI zdefiniowano w dokumencie GS1 General Specifications; obejmują one szeroki zakres danych łańcucha dostaw: identyfikatory produktów, daty, ilości, numery partii, numery seryjne, miary, adresy URL i wiele innych.

### Budowa elementu AI

Każdy element AI składa się z dwóch części:

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

Kod AI jest zawsze liczbowy. Wartość danych następuje bezpośrednio po nim, bez żadnego separatora między kodem a wartością.

### AI o stałej i o zmiennej długości

Identyfikatory AI dzielą się na dwie kategorie:

| Rodzaj | Zachowanie | Przykład |
|---|---|---|
| **Stała długość** | Dokładna liczba znaków, zawsze pobierana w całości | AI `01` (GTIN) — zawsze 14 cyfr |
| **Zmienna długość** | Od 1 znaku do maksimum; kończy się separatorem GS albo końcem danych wejściowych | AI `10` (partia) — od 1 do 20 znaków alfanumerycznych |

To, czy AI ma stałą, czy zmienną długość, wynika wyłącznie z jego definicji w specyfikacji GS1 — analizator nigdy nie zgaduje.

### Ciągi elementów z wieloma AI

Wiele identyfikatorów AI można połączyć w jeden ciąg elementów. Identyfikatory AI o stałej długości można łączyć bezpośrednio, ponieważ analizator zawsze wie dokładnie, ile znaków pobrać. Identyfikatory AI o zmiennej długości należy zakończyć **znakiem GS** (ASCII `0x1D`, w symbolikach kodów kreskowych nazywanym również FNC1) zawsze wtedy, gdy następuje po nich kolejny AI — dzięki temu analizator wie, gdzie kończy się wartość, a gdzie zaczyna następny kod AI.

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

W literałach łańcuchowych Javy znak GS zapisuje się sekwencją ucieczki Unicode `""`.

### Często spotykane AI

| AI | Tytuł danych | Format | Przykładowa wartość |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (RRMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (RRMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, jednolity obszar walutowy) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Czwarta cyfra** czterocyfrowego AI miary lub ceny koduje liczbę domyślnych miejsc dziesiętnych: `3103` oznacza masę netto w kg z 3 miejscami dziesiętnymi (`001500` = 1,500 kg), podczas gdy `3102` odczytałoby te same cyfry jako 15,00 kg. Kolumna `Format` powyżej pokazuje format *danych*; pełny `getFormatString()` każdego AI obejmuje sam AI (na przykład `N4+N6` dla `3103`).

### Interpretacja czytelna dla człowieka (HRI)

Przyjęta postać czytelna umieszcza każdy kod AI w nawiasach, bezpośrednio przed jego wartością, ze spacją między elementami:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Separator GS nie pojawia się w HRI. Ten format wytwarza `GS1AIObject.toHriString()`.

### Czterocyfrowe kody AI

Niektóre identyfikatory AI mają cztery cyfry zamiast dwóch. Dwie pierwsze wskazują rodzinę AI; trzecia lub czwarta niosą dodatkowe znaczenie (na przykład położenie domyślnego przecinka dziesiętnego w AI miary). Analizator samodzielnie ustala pełny kod AI na podstawie ciągu elementów — kod wywołujący zawsze operuje pełnym kodem (na przykład `"3102"`, a nie samym `"31"`).

---

## Szybki start

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

> **Separator GS:** w ciągu z wieloma AI identyfikatory o zmiennej długości muszą być rozdzielone znakiem GS (ASCII `0x1D`). W literałach łańcuchowych Javy należy użyć `""`.

---

## Potok przetwarzania analizy

### Etap wstępny — modyfikatory wejścia

Jeżeli `ParseConfig` zawiera **modyfikatory wejścia**, uruchamiają się one przed wszystkim innym: przed usunięciem identyfikatora korelacji, przed wykryciem nośnika danych, przed wejściem w potok GS1. Każdy modyfikator przepisuje surowe dane wejściowe dla kolejnego, a wszystkie opisane niżej etapy działają na wyniku łańcucha.

Domyślnie nie skonfigurowano żadnego modyfikatora, więc ten etap wstępny nic nie robi, dopóki nie włączysz go jawnie. Zobacz [Modyfikatory wejścia](#modyfikatory-wejścia).

---

### Etap 0 — identyfikator korelacji

Przed jakimkolwiek przetwarzaniem GS1 `GaiaParser` sprawdza, czy dane wejściowe zaczynają się od opcjonalnego **przedrostka identyfikatora korelacji**: dokładnie 8 dziesiętnych cyfr ASCII, po których następuje tylda (`~`), na przykład `12345678~`.

Jeżeli przedrostek występuje, zostaje usunięty i zachowany jako `CorrelationInfo` w zwracanym obiekcie `ParseResult`. Wszystkie dalsze etapy działają na tak oczyszczonym ładunku. W razie braku przedrostka dane wejściowe przechodzą bez zmian.

Szczegóły opisano w części [Identyfikator korelacji](#identyfikator-korelacji).

---

### Etap 1 — kierowanie danych wejściowych

Po usunięciu korelacji `GaiaParser` sprawdza, czy (oczyszczone) dane wejściowe zaczynają się od **identyfikatora symboliki AIM**: trzyznakowego przedrostka w postaci `]` + litera ASCII + cyfra ASCII (na przykład `]C1` dla GS1-128, `]d2` dla GS1 DataMatrix, `]e0` dla GS1 DataBar / GS1 Composite).

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

Jeżeli nośnik danych nie obsługuje AI GS1 (na przykład kod pocztowy), analiza kończy się natychmiast błędem `GE-D002`.

---

### Etap 2 — składnia

Wykonywany zawsze. Składa się z dwóch kroków:

**2a. Podział na tokeny (`AISyntaxParser`)**
- Odczytuje długość kodu AI z dwóch pierwszych znaków, korzystając z tablicy przedrostków GS1 (GS1 General Specifications, tablica 7-5).
- Identyfikatory AI o stałej długości pobierają z danych wejściowych dokładną liczbę bajtów.
- Identyfikatory AI o zmiennej długości są czytane aż do znaku GS albo do końca danych wejściowych.
- W AI wieloskładnikowych blok wartości jest dzielony na segmenty, po jednym na składnik.

**2b. Walidacja strukturalna (`SyntaxValidator`)**
- Wykrywa zduplikowane AI (`GE-S004`).
- Sprawdza wymagane zależności między AI, na przykład AI `02`, który wymaga AI `37` (`GE-S005`).
- Sprawdza wykluczające się pary AI (`GE-S006`).

Błędy tego etapu mają poziom `SYNTAX_ERROR` (podział na tokeny) albo `INTEGRITY_ERROR` (struktura). Jeżeli wystąpi **choćby jeden** błąd — z podziału na tokeny lub ze struktury — potok zatrzymuje się, a etapy treści i interpretacji zostają pominięte.

---

### Etap 3 — treść

Wykonywany tylko wtedy, gdy etap 2 nie wytworzył błędów (ani z podziału na tokeny, ani ze struktury). Potok stosowany do każdego elementu (każdy krok wykonuje się tylko wtedy, gdy poprzedni nie wytworzył błędów):

| Krok | Walidator | Kody błędów |
|---|---|---|
| Sprawdzenie wyrażeniem regularnym | `RegexValidator` | `GE-C001` |
| Zestaw znaków i format składników | `ComponentValidator` | `GE-C005` + kody formatu wg warunku (`GE-C054`–`GE-C115`) |
| Cyfra kontrolna / znak kontrolny | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Własna walidacja semantyczna | `ContentValidatorRegistry` | kody treści wg warunku (`GE-C116`–`GE-C170`) |

Błędy tego etapu mają poziom `FORMAT_ERROR` albo `DATA_ERROR`, z jednym wyjątkiem:
kontrole prefiksu firmy GS1 w AI niosących klucz GS1 mają charakter wyłącznie informacyjny i poziom `WARNING` (zobacz
[Wykaz błędów](#wykaz-błędów)), zatem nierozpoznany prefiks firmy sam w sobie nie
unieważnia wyniku.

---

### Etap 4 — interpretacja

Wykonywany wyłącznie w trybie `INTERPRETATION` i tylko wtedy, gdy żaden element nie niesie błędu z wcześniejszego etapu. `InterpretationEngine` wzbogaca każdy element o opisane metadane:

- Daty przeformatowane jako `dd/mm/rrrr`
- Rozbiór cyfry kontrolnej GTIN i wyszukanie prefiksu firmy GS1
- Nazwy krajów wg ISO 3166
- Nazwy i symbole walut wg ISO 4217
- Zdekodowane kwoty dziesiętne
- Fragmenty HRI (interpretacji czytelnej dla człowieka)

Wyniki dołączane są jako wpisy `GS1AIInterpretation` do każdego `GS1AIObjectElement`.

---

## Konfiguracja analizy (`ParseConfig`)

`GaiaParser` udostępnia dokładnie dwa punkty wejścia:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` działa z **konfiguracją domyślną**: tryb `INTERPRETATION`, daty w porządku rosnącym (`dd/mm/rrrr`) z separatorem `/` i czterocyfrowym rokiem oraz komunikaty o błędach po **angielsku**. Aby zmienić którykolwiek z tych elementów — w tym tryb analizy — zbuduj `ParseConfig` przy użyciu jego płynnego konstruktora i skorzystaj z dwuargumentowej wersji metody.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Wszystkie wyliczenia opcji znajdują się w `GaiaConstants`.

### Opcje

| Metoda konstruktora | Wyliczenie (`GaiaConstants`) | Domyślnie | Działanie |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Głębokość potoku — zobacz [Tryby analizy](#tryby-analizy). |
| `language(...)`      | `Language`      | `ENGLISH`        | Język komunikatów o błędach, etykiet interpretacji **oraz** opisów AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Kolejność części daty: `LITTLE` (`dd/mm/rrrr`), `MIDDLE` (`mm/dd/rrrr`), `BIG` (`rrrr/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Znak między częściami daty: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) albo `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) albo `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Pomija strukturalną kontrolę „wymaga” (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Pomija strukturalną kontrolę „wyklucza” (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nazwa klasy | brak | Kod przepisujący surowe dane wejściowe przed analizą — dwa [wbudowane modyfikatory](#wbudowane-modyfikatory) oraz wszystko, co napiszesz sam. Zobacz [Modyfikatory wejścia](#modyfikatory-wejścia). |

Cztery opcje daty wpływają wyłącznie na sformatowane łańcuchy dat wytwarzane przez wzbogacacze interpretacji (w trybie `INTERPRETATION`); nie zmieniają walidacji. Wartości konstruktora można pominąć — każda nieustawiona opcja (lub taka, której przekazano `null`) zachowuje wartość domyślną.

### Zlokalizowane komunikaty i etykiety

`language(...)` wybiera język **trzech** rodzajów tekstu czytelnego dla człowieka: komunikatów o błędach, etykiet interpretacji (`getLabel()` każdego `GS1AIInterpretation`) oraz opisów AI (`getDescription()` każdego `GS1AIObjectElement`).

`GaiaConstants.Language` definiuje **35 języków** obejmujących najczęściej używane języki świata: angielski, francuski, hiszpański, niemiecki, włoski, portugalski, niderlandzki, polski, rosyjski, ukraiński, czeski, szwedzki, chiński, japoński, koreański, arabski, indonezyjski, hindi, turecki, bengalski, urdu, wietnamski, pidżyn nigeryjski, arabski egipski, marathi, telugu, tamilski, kantoński, wu, tagalski, perski, hausa, pendżabski, jawajski i suahili.

Stan tłumaczeń (w wersji dostarczanej):
- **Etykiety interpretacji** — przetłumaczone na wszystkie języki.
- **Komunikaty o błędach** — przetłumaczone na wszystkie języki.
- **Opisy AI** — przetłumaczone na wszystkie języki z wyjątkiem angielskiego. Angielski nie stanowi odrębnego katalogu: jest odczytywany wprost z pola `description` wpisu AI w pliku `gs1-application-identifiers.jsonld`, do którego ostatecznie sprowadza się każdy opis AI.

Pidżyn nigeryjski (`NIGERIAN_PIDGIN`), kreolski oparty na angielskim, świadomie wykorzystuje tekst angielski dla etykiet interpretacji i komunikatów o błędach. Opisy AI są wyjątkiem od tego wyjątku: przetłumaczono je na autentyczny pidżyn zamiast powielać angielski, ponieważ katalogi opisów AI powstały niezależnie od katalogów etykiet i komunikatów. Tłumaczenia maszynowe powinny zostać sprawdzone przez rodzimych użytkowników języka, zanim zaufa się im w środowisku produkcyjnym.

Każdy komunikat, etykieta lub opis brakujący w katalogu danego języka jest zastępowany wersją angielską. Języki pisane od prawej do lewej (arabski, urdu, arabski egipski, perski) są poprawnie przechowywane jako łańcuchy znaków; ich wyświetlenie od prawej do lewej należy do warstwy prezentacji.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Etykiety interpretacji lokalizowane są tak samo (wartości pozostają niezmienione — zmieniają się tylko etykiety):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Opisy AI lokalizowane są tak samo (nie lokalizuje się jedynie `getTitle()`, na przykład `"GTIN"`):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formatowanie dat

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Modyfikatory wejścia

**Modyfikator wejścia** to kod, który przepisuje surowy łańcuch wejściowy, zanim Gaia go przeanalizuje. Modyfikatory istnieją z myślą o danych, które przychodzą już zniekształcone: czytnik zastępujący separator GS drukowalnym symbolem zastępczym, oprogramowanie pośredniczące opakowujące ładunek we własny przedrostek, system nadrzędny zamieniający wszystko na wielkie litery. Zamiast wstępnie przetwarzać każdy łańcuch w każdym miejscu wywołania (i pomylić się subtelnie w jednym z nich), zadeklaruj normalizację raz w `ParseConfig` i pozwól, by zastosował ją analizator.

Modyfikatory uruchamiają się na samym początku `GaiaParser.parse(...)` — przed usunięciem identyfikatora korelacji, przed wykryciem identyfikatora symboliki AIM, przed potokiem GS1. Wszystko dalej widzi już wyłącznie przepisany łańcuch. **Domyślnie nic nie jest skonfigurowane**, również oba [wbudowane modyfikatory](#wbudowane-modyfikatory) — włączasz je jawnie w każdym `ParseConfig`.

**Interfejs:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Wbudowane modyfikatory

Główny plik jar zawiera dwa modyfikatory, w pakiecie `tools.pantheum.gaia.modifier.custom`. Obsługują dwa najczęstsze sposoby, w jakie ładunek GS1 przychodzi zniekształcony — wydrukowane nawiasy HRI traktowane jak dane oraz zbędne spacje — dzięki czemu typowe przypadki nie wymagają własnej klasy:

| Klasa | `getName()` | Co robi |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Usuwa nawiasy HRI wokół każdego AI (`(01)…(10)…`) i przywraca separator FNC1, który był w nich zawarty. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Usuwa wszystkie spacje (`0x20`) z ciągu elementów AI. |

Są to zwykłe implementacje `ModifierInterface`, bez żadnego szczególnego statusu — rejestruje się je, porządkuje, raportuje i unieważnia dokładnie tak jak twoje własne:

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

Oba są bezstanowe i bezpieczne wątkowo, więc można współdzielić jedną instancję, a do obu można się odwołać w pełni kwalifikowaną nazwą klasy w konfiguracjach zewnętrznych (zobacz [Rejestrowanie modyfikatorów](#rejestrowanie-modyfikatorów)).

#### `ModifierRemoveAIBrackets`

Interpretacja czytelna dla człowieka wg GS1 drukuje każdy AI w nawiasach — `(01)09521234543213(10)ABC123` — wyłącznie zgodnie z konwencją zapisu. Czytnik lub oprogramowanie pośredniczące ustawione na wysyłanie HRI przekazuje te nawiasy jako dane, a moduł podziału na tokeny zupełnie nie wie, co z nimi począć.

Usunięcie nawiasów to dopiero połowa pracy. W HRI to nawias otwierający *następnego* AI wyznacza koniec poprzedniej wartości, więc w postaci nawiasowej AI o zmiennej długości nie potrzebuje FNC1. Usuń nawiasy naiwnie, a granica ta zniknie:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Dlatego modyfikator **wstawia z powrotem FNC1 na każdej granicy, której poprzedzający AI ma zmienną długość**, odtwarzając dokładnie to, co kodowały nawiasy:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Długość odczytywana jest z własnego rejestru analizatora `AiDefinitionRegistry`, więc obsłużone zostają wszystkie AI o zmiennej długości, a nie lista wpisana na sztywno. Trzy przypadki celowo pozostają nietknięte: wartość, która już kończy się na FNC1 (źródło stosujące obie konwencje nie otrzyma drugiego separatora), kod w nawiasach niebędący znanym AI (nieznany AI nic nie mówi o swojej długości) oraz ostatni AI w ciągu.

Przepisanie jest **idempotentne** — ponowne zastosowanie go do własnego wyniku niczego nie zmienia — jest więc bezpieczne w strumieniu mieszanym, w którym tylko część danych ma nawiasy.

> **Ograniczenie.** `(` i `)` same w sobie są dopuszczalnymi znakami danych GS1, a wzorzec sprowadza się do `\((\d{2,4})\)`. Wartość, która przypadkiem zawiera dwu- lub czterocyfrową liczbę w nawiasach, również zostałaby ich pozbawiona. Stosuj to wyłącznie do źródła korzystającego z nawiasowej konwencji HRI, a nie do wartości z prawdziwymi nawiasami.

#### `ModifierRemoveSpaces`

Niektóre czytniki, systemy pośredniczące i linie druku etykiet wstawiają zbędne spacje do skądinąd poprawnego ciągu elementów: aby dopełnić pole o stałej szerokości, oddzielić czytelne grupy albo złamać długą wartość. Moduł podziału na tokeny traktuje każdą z nich jako dane, przez co psuje wartość, w której się znajduje, a w AI o zmiennej długości przesuwa wszystko, co następuje po niej.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Usuwany jest wyłącznie znak ASCII `0x20`. Pozostałe znaki odstępu pozostają na miejscu — tabulator na przykład leży poza zestawem znaków kodowalnych GS1, więc analizator zgłasza go jako `GE-S008`, zamiast po cichu go usuwać.

> **Ograniczenie.** Spacja (`0x20`) należy do niezmiennego zestawu znaków GS1, więc numer partii albo numer artykułu klienta może ją zawierać całkiem zasadnie. Modyfikator nie odróżni spacji zbędnej od prawdziwej; stosuj go wyłącznie do źródła, o którym wiesz, że nie używa spacji wewnątrz swoich wartości AI.

#### Przedrostki są pomijane, a nie przepisywane

Modyfikatory uruchamiają się, zanim analizator cokolwiek usunie, więc surowe dane wejściowe mogą jeszcze nieść identyfikator korelacji, identyfikator symboliki AIM oraz wskaźnik ECI. Oba wbudowane modyfikatory ustalają początek ciągu elementów AI za pomocą własnej logiki analizatora z `CorrelationIdParser` i `DataCarrierParser`, przepisują dopiero od tego miejsca i doklejają wynik z powrotem do **nienaruszonego** przedrostka:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Nośniki EAN/UPC, których wartość dopełnia się do GTIN-14 (`isRequiresGtinPadding()`), są pomijane w całości: ich ładunek to czysto liczbowa wartość kodu kreskowego bez struktury AI, w której ani nawiasy, ani spacje nie mogą mieć znaczenia.

#### Kolejność: najpierw spacje, potem nawiasy

Gdy używa się obu, **zarejestruj `ModifierRemoveSpaces` jako pierwszy**. Rozpoznawanie nawiasów zależy od położenia: `( 01 )` ze spacjami nie pasuje do `\((\d{2,4})\)`, więc nawiasy pozostają, a zawarty w nich separator nigdy nie zostaje przywrócony.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Pisanie modyfikatora

Napisz własny, gdy żaden z wbudowanych nie pasuje — interfejs to jedna metoda.

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

Gdy przepisanie zależy od konfiguracji analizy, nadpisz zamiast tego wersję dwuargumentową:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Kontrakt:

| Reguła | Szczegół |
|---|---|
| Bezstanowy i bezpieczny wątkowo | Dla każdej klasy buforowana jest jedna instancja, współdzielona przez wszystkie analizy. |
| Publiczny konstruktor bezargumentowy | Wymagany tylko wtedy, gdy modyfikator wskazuje się nazwą klasy. |
| Obsługa wejścia `null` i pustego | Analizator nie odfiltrowuje ich przed uruchomieniem łańcucha. |
| Zwrócenie `null` oznacza „brak zmiany” | Poprzednia wartość zostaje zachowana. Zwróć `input` bez zmian, gdy modyfikator nie ma zastosowania. |
| Lepiej zwrócić wejście bez zmian niż zgłosić wyjątek | Modyfikator zgłaszający wyjątek przerywa analizę — zobacz [Obsługa błędów](#obsługa-błędów-modyfikatora). |
| `getName()` | Nadpisz ją, aby ustalić nazwę raportowaną w `ModifierInfo`; domyślnie jest to prosta nazwa klasy. |

### Rejestrowanie modyfikatorów

Modyfikatory działają w kolejności dodawania, a każdy otrzymuje wynik poprzedniego. Rejestruj je jako instancje, przez w pełni kwalifikowaną nazwę klasy albo jako listę jednych lub drugich:

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

[Wbudowane modyfikatory](#wbudowane-modyfikatory) wskazuje się tak samo jak własne — **zawsze pełną nazwą**. Nie ma dla nich wyszukiwania po nazwie skróconej ani po aliasie; `ModifierRegistry` rozwiązuje każdy modyfikator, dostarczony czy nie, po pełnej nazwie klasy.

Nazwy rozwiązuje `ModifierRegistry`, który tworzy instancję każdej klasy jeden raz, korzystając z jej bezargumentowego konstruktora, i buforuje ją dla każdej późniejszej konfiguracji wskazującej tę samą klasę. Rozwiązanie następuje **w chwili budowania konfiguracji**, więc nazwa, której nie da się odnaleźć, która nie implementuje `ModifierInterface` albo której nie da się zainstancjonować, zgłasza tam `IllegalArgumentException` — a nie po cichu dopiero podczas analizy. Modyfikator, którego nie da się zbudować refleksyjnie (na przykład taki, który przechowuje wstrzykniętą zależność), można zarejestrować z góry, aby nadal był osiągalny po nazwie:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Sprawdzanie, co zrobił modyfikator

Gdy skonfigurowano modyfikatory, `ParseResult.getPayload()` odzwierciedla **zmienione** dane wejściowe. Oryginał zachowywany jest w `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` raportuje `getName()` każdego modyfikatora, którym domyślnie jest prosta nazwa klasy, ale oba wbudowane modyfikatory ją nadpisują — łańcuch złożony z tych dwóch raportuje zatem nazwy wyświetlane, a nie nazwy klas:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` zwraca `null`, gdy nie skonfigurowano żadnego modyfikatora. Jeżeli modyfikatory się wykonały, lecz każdy zwrócił wejście bez zmian, informacja jest obecna, a `isModified()` ma wartość `false` — w `getAppliedModifiers()` wymienione są wyłącznie modyfikatory, które faktycznie zmieniły dane wejściowe.

### Obsługa błędów modyfikatora

Modyfikator, który zgłosi wyjątek, przerywa analizę. Wyjątek zostaje opakowany w `GaiaModifierException` wskazujący winny modyfikator, a wynik niesie błąd wewnętrzny `GE-I001`, którego komunikat zawiera tę nazwę; `getPayload()` raportuje niezmienione dane wejściowe. Analiza celowo **nie** jest kontynuowana z na wpół przepisanym łańcuchem: krok normalizacji, który zawiódłby po cichu, dawałby wyniki wyglądające na poprawne, lecz uzyskane z niewłaściwych danych wejściowych.

---

## Tryby analizy

Każdy tryb wskazuje najgłębszy [etap potoku](#potok-przetwarzania-analizy), jaki wykonuje; wszystkie etapy wcześniejsze również się wykonują.

| Tryb | Sięga do | Odpowiada na pytanie |
|---|---|---|
| `DATA_CARRIER` | Etap 1 (kierowanie danych wejściowych) | Jaka symbolika przeniosła te dane? |
| `SYNTAX` | Etap 2 (składnia) | Czy kody AI i długości są poprawnie zbudowane? |
| `CONTENT` | Etap 3 (treść) | Czy wartości są poprawnymi danymi GS1? |
| `INTERPRETATION` | Etap 4 (interpretacja) | Co oznaczają te wartości? |

### Tryb DATA_CARRIER

Zatrzymuje się po etapie 1 — sprawdza identyfikator symboliki AIM i ustala symbolikę, ale nie wchodzi w potok analizy AI. Przydatny do rozpoznania symboliki i skierowania danych bez kosztu pełnej walidacji.

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

**Stosuj, gdy:** twoja aplikacja musi rozpoznać rodzaj kodu kreskowego, zanim zdecyduje, jak przetworzyć ładunek — na przykład aby skierować dane do różnych procedur obsługi dla symbolik 1D i 2D. Do takiego kierowania preferuj typ [`DataCarrierType`](#datacarrierentry-i-datacarriertype) (`getDataCarrier().getDataCarrierType()`) zamiast porównywania łańcuchów z `getName()`.

---

### Tryb SYNTAX

Zatrzymuje się po etapie 2. Przydatny do wstępnej selekcji strukturalnej bez kosztu walidacji treści.

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

**Stosuj, gdy:** chcesz sprawdzić, czy kody AI i długości danych są poprawnie zbudowane, zanim zdecydujesz się na pełną walidację, albo gdy przetwarzasz duże wolumeny, w których błędy treści zdarzają się rzadko.

---

### Tryb CONTENT

Zatrzymuje się po etapie 3.

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

> Większość identyfikatorów AI nie może występować samodzielnie: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) i `21`
> (SERIAL) *wymagają* każdy klucza identyfikacyjnego, takiego jak AI `01`, w tym samym ciągu
> elementów; pominięcie GTIN w powyższym przykładzie zakończyłoby się niepowodzeniem już na etapie 2 z kodem `GE-S005`, w ogóle
> nie docierając do walidacji treści. Ustaw `skipRequiresCheck(true)` w
> `ParseConfig`, aby analizować fragmenty celowo pomijające towarzyszące im AI.

**Stosuj, gdy:** musisz wiedzieć, czy odczytana wartość jest w pełni zgodna z GS1, zanim użyjesz jej w procesie biznesowym — bez narzutu wzbogacania interpretacyjnego.

---

### Tryb INTERPRETATION (domyślny)

Wykonuje cały potok aż do etapu 4. Jest to tryb domyślny przy wywołaniu `parse(String)` bez argumentu trybu. Wzbogacane są wyłącznie elementy, które bezbłędnie przeszły walidację treści.

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

**Przykładowy wynik:**
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

**Przykład kwoty pieniężnej (AI 3932 — cena z kodem waluty ISO):**
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

**Stosuj, gdy:** budujesz warstwy prezentacji, narzędzia weryfikacji etykiet albo dowolny interfejs wymagający czytelnego rozbioru wartości AI.

---

## Identyfikator korelacji

Niektóre procesy poprzedzają surowe dane GS1 własnym ośmiocyfrowym identyfikatorem korelacji, aby powiązać zdarzenia odczytu z sesją lub transakcją. Format jest następujący:

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

Tylda (`~`) jest separatorem. **Nie** stanowi części treści GS1 — zostaje usunięta, zanim rozpocznie się jakakolwiek analiza GS1.

### Reguły wykrywania

Przedrostek zostaje wykryty, gdy dane wejściowe zaczynają się od dokładnie 8 dziesiętnych cyfr ASCII (`0`–`9`), po których bezpośrednio następuje `~`. Jeżeli dziewiąty znak nie jest `~` albo któryś z 8 pierwszych znaków nie jest cyfrą, dane wejściowe traktuje się jak zwykłą treść GS1, bez przedrostka korelacji.

### Dostęp do identyfikatora korelacji

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

### Połączenie z identyfikatorem symboliki AIM

Przedrostek korelacji może poprzedzać identyfikator symboliki AIM. Analizator obsługuje ten przypadek w sposób przezroczysty:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Klasa implementacji:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** koduje jedną lub więcej wartości AI wprost w strukturze adresu URL HTTP(S), umożliwiając identyfikatory produktów fizycznych rozwiązywalne w sieci. GAIA realizuje standard *GS1 Digital Link Standard: URI Syntax* (wydanie 1.7.0) dla identyfikatorów URI **nieskompresowanych**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` rozpoznaje identyfikatory URI Digital Link automatycznie: każde dane wejściowe zaczynające się od `http://` lub `https://` trafiają do `GS1DLParser`, który wykonuje te same etapy treści i interpretacji co potok ciągów elementów.

### Budowa URI i role AI

Każdy AI w identyfikatorze URI Digital Link pełni jedną z trzech ról, udostępnianą w każdym `GS1AIObjectElement` przez `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Rola | Położenie | Przykład |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Pierwsza para `/ai/wartość` w ścieżce (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Kolejne pary w ścieżce, uporządkowane wg klucza podstawowego (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parametry zapytania o kluczach w całości liczbowych (§4.10) | `?17=271231` |

Egzekwowane reguły strukturalne (`DLPathRules`):
- Dokładnie **jeden** podstawowy klucz identyfikacyjny w ścieżce; dodatkowe klucze należy zakodować jako atrybuty danych w zapytaniu.
- Kwalifikatory klucza muszą być dopuszczone przez klucz podstawowy i występować w przepisanej kolejności. Kwalifikatory opcjonalne można pominąć, ale te, które *są* obecne, muszą zachować ustaloną kolejność — zobacz [Kolejność kwalifikatorów](#kolejność-kwalifikatorów).
- Przed kluczem podstawowym mogą występować dowolne własne segmenty ścieżki (na przykład `/products/au/01/...`); pobierz je przez `getDigitalLinkInfo().getCustomPathStem()`.
- Klucze zapytania niebędące liczbami (`linkType`, `context`, parametry rozszerzeń, takie jak `23P`) są pomijane; klucze w całości liczbowe muszą być poprawnymi AI oznaczonymi jako `validAsDataAttribute`.
- Znaki wartości zakodowane procentowo są dekodowane; identyfikatory AI `(03)` i `(8014)` są niedozwolone.

Klucze podstawowe i dopuszczalne dla nich sekwencje kwalifikatorów są **sterowane danymi** z definicji AI — flagą `gs1DigitalLinkPrimaryKey` i atrybutem `gs1DigitalLinkQualifiers` — zamiast być wpisane na sztywno.

Każde naruszenie struktury oraz dane wejściowe niebędące adresem URL dają strukturalny błąd Digital Link (`GE-L001`–`GE-L014`, po jednym kodzie na warunek). Rozłożone metadane adresu URL (`scheme`, `domain`, `path`, `customPathStem`, `query` oraz obiekt `java.net.URL`) pozostają dostępne przez `getDigitalLinkInfo()` nawet przy błędach strukturalnych.

### Kolejność kwalifikatorów

Dla każdego klucza podstawowego `gs1DigitalLinkQualifiers` wymienia jedną lub więcej **uporządkowanych** sekwencji kwalifikatorów. W obrębie sekwencji AI ujęty w nawiasy kwadratowe jest **opcjonalny**, a AI bez nawiasów — **obowiązkowy**, na wzór notacji `[cpv-comp]` z ABNF w §4.9. Sekwencje jednego klucza podstawowego stanowią wzajemnie wykluczające się warianty.

GTIN (`01`) definiuje na przykład dwie sekwencje:

| Ścieżka | Sekwencja | Znaczenie |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — każdy opcjonalny, lecz w tej ustalonej kolejności |
| upui-path | `235` | TPX (obowiązkowy); GTIN + TPX = UPUI |

Tak więc `/01/09506000134352/10/LOT-ABC/21/SER` jest poprawne (LOT przed SER, CPV pominięty), `/01/.../21/SER/10/LOT-ABC` zostaje **odrzucone** (zła kolejność), a `/01/09506000134352/235/2ABC456` należy do upui-path. Kontrola kolejności to dopasowanie podsekwencji zachowujące porządek: opcjonalne AI można zatem pominąć, ale nigdy nie wolno ich przestawić.

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

**Klasa implementacji:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Praca z wynikami

### ParseResult

Wynik najwyższego poziomu zwracany przez `GaiaParser.parse()`.

| Metoda | Zwraca | Opis |
|---|---|---|
| `isValid()` | `boolean` | `true`, jeżeli nie ma błędów na żadnym poziomie. Ostrzeżenia nie wpływają na poprawność. Zawsze `true`, gdy `getAiObject()` ma wartość `null`. |
| `getPayload()` | `String` | Łańcuch wejściowy po usunięciu przedrostka korelacji — oraz po ewentualnym przepisaniu przez [modyfikatory wejścia](#modyfikatory-wejścia). |
| `getPayloadContent()` | `String` | Ładunek pozbawiony identyfikatora symboliki AIM i przedrostka ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (nośnik danych odrzucony jako niezgodny z GS1, na przykład nośnik Code 39 `]A0`) albo `UNABLE_TO_DETERMINE_CONTENT` (gdy `aiObject` ma wartość `null`, na przykład w trybie `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Skonfigurowana głębokość potoku (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Najgłębszy etap faktycznie osiągnięty przez analizę — zobacz niżej. |
| `isParseComplete()` | `boolean` | `true`, jeżeli analiza osiągnęła żądaną głębokość (`achieved == requested`). Niezależne od `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Wszystkie rozpoznane AI. `null` w trybie `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Wszystkie błędy o poziomie innym niż WARNING (na poziomie obiektu i wszystkich elementów). |
| `getWarnings()` | `List<GaiaError>` | Wszystkie ostrzeżenia poziomu WARNING (na poziomie obiektu i wszystkich elementów). |
| `hasWarnings()` | `boolean` | `true`, jeżeli zgłoszono ostrzeżenia poziomu WARNING. |
| `getIssues()` | `List<GaiaError>` | Błędy i ostrzeżenia razem. |
| `hasDataCarrier()` | `boolean` | `true`, jeżeli rozpoznano identyfikator symboliki AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadane symboliki albo `null`, jeżeli nie ustalono nośnika. |
| `hasEci()` | `boolean` | `true`, jeżeli z ładunku usunięto wskaźnik ECI. |
| `getEci()` | `EciEntry` | Metadane kodowania ECI albo `null`. |
| `hasCorrelationId()` | `boolean` | `true`, jeżeli w pierwotnych danych wejściowych był przedrostek korelacji `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | Wydobyty identyfikator korelacji albo `null`, jeżeli go nie było. |
| `isInputModified()` | `boolean` | `true`, jeżeli [modyfikator wejścia](#modyfikatory-wejścia) zmienił dane wejściowe. |
| `getModifierInfo()` | `ModifierInfo` | Co zrobił łańcuch modyfikatorów — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null`, jeżeli nie skonfigurowano żadnego modyfikatora. |
| `getTiming()` | `ProcessingTiming` | Rzeczywisty pomiar czasu analizy — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null`, jeżeli wynik nie pochodzi z `GaiaParser`. |
| `getVersion()` | `String` | Wersja biblioteki, która wytworzyła wynik. |

#### Tryb analizy żądany a osiągnięty

Potok przechodzi drabinę **SYNTAX → CONTENT → INTERPRETATION** i zatrzymuje się przedwcześnie przy błędach, więc tryb faktycznie *osiągnięty* może być płytszy niż *żądany*. `getAchievedParseMode()` pokazuje, jak daleko dotarł:

| Żądany | Co się dzieje | Osiągnięty | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | błąd **składni lub struktury** zatrzymuje analizę po podziale na tokeny | `SYNTAX` | `false` |
| `INTERPRETATION` | błąd **treści** (zły format lub zła cyfra kontrolna) blokuje wzbogacanie | `CONTENT` | `false` |
| `CONTENT` | etap treści zawsze wykonuje się do końca (błędy są odnotowywane, nie są krytyczne) | `CONTENT` | `true` |
| dowolny (dane wejściowe bez błędów) | potok osiąga żądaną głębokość | = żądany | `true` |
| `DATA_CARRIER` | nośnik sprawdzony; nie analizowano treści AI | `DATA_CARRIER` | `true` |
| dowolny | nośnik danych zostaje odrzucony przed analizą AI (na przykład nośnik `]A0` niezgodny z GS1) | `SYNTAX` | `false` |

`isParseComplete()` jest niezależne od `isValid()`: analiza `CONTENT` GTIN-u z błędną cyfrą kontrolną jest **kompletna** (etap treści się wykonał), a zarazem **niepoprawna** (cyfra kontrolna się nie zgadza). Używaj `isParseComplete()`, aby zapytać „czy potok zszedł tak głęboko, jak prosiłem?”, a `isValid()` — „czy dane są poprawnie zbudowane?”.

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

Zbiór rozpoznanych elementów AI.

| Metoda | Opis |
|---|---|
| `getAis()` | Wszystkie instancje `GS1AIObjectElement`, w kolejności z danych wejściowych. |
| `get(String aiCode)` | Pierwszy element odpowiadający podanemu kodowi AI albo `null`. |
| `contains(String aiCode)` | `true`, jeżeli występuje AI o tym kodzie. |
| `size()` | Liczba rozpoznanych AI. |
| `isValid()` | `true`, jeżeli nie ma błędów na poziomie obiektu i żaden element nie niesie błędów. |
| `toHriString()` | Łańcuch HRI, na przykład `(01)09506000134352 (17)261231`. |
| `toElementString()` | Surowy ciąg elementów — bez nawiasów, z FNC1 po każdym elemencie o zmiennej długości — na przykład `010950600013435210LOT-ABC<GS>17271231`. Zwraca `null`, jeżeli `isValid()` ma wartość `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK`, gdy `hasDigitalLink()` jest prawdziwe, w przeciwnym razie `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true`, jeżeli dane wejściowe były identyfikatorem URI GS1 Digital Link z podstawowym kluczem identyfikacyjnym. Poprawnie zbudowany adres URL bez klucza podstawowego nadal udostępnia `getDigitalLinkInfo()`, ale tutaj zwraca `false`. |
| `getCanonicalDigitalLink()` | Kanoniczny identyfikator URI GS1 Digital Link (§4.12) w domenie `https://id.gs1.org` — klucz podstawowy i kwalifikatory jako segmenty ścieżki, atrybuty danych jako parametry zapytania posortowane wg klucza AI — albo `null`, jeżeli nie ma klucza podstawowego. |
| `getDigitalLinkInfo()` | Metadane rozbioru URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`) albo `null`, jeżeli to nie jest Digital Link. |
| `getAllErrors()` | Błędy na poziomie obiektu + wszystkie błędy elementów (inne niż WARNING). |
| `getAllWarnings()` | Ostrzeżenia na poziomie obiektu + wszystkie ostrzeżenia elementów. |
| `getAllIssues()` | Wszystko razem. |

---

### GS1AIObjectElement

Pojedyncza rozpoznana instancja AI.

| Metoda | Opis |
|---|---|
| `getAi()` | Kod AI, na przykład `"01"`, `"3102"`. |
| `getTitle()` | Tytuł danych GS1, na przykład `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Pełny opis AI wg GS1, **zlokalizowany do języka analizy** (na przykład `"Global Trade Item Number (GTIN)"` po angielsku). W razie braku tłumaczenia sięga po tekst angielski z definicji AI. |
| `getFormatString()` | Deskryptor formatu obejmujący AI *oraz* jego dane, na przykład `"N2+N14"` dla AI `01`, `"N2+X..20"` dla AI `10`, `"N4+N3+N..15"` dla AI `3932`. |
| `getValue()` | Surowa wartość danych wydobyta z ciągu elementów. |
| `isFixedLength()` | `true`, jeżeli AI ma stałą długość danych. |
| `getPosition()` | Przesunięcie znakowe (liczone od zera) w pierwotnych danych wejściowych. |
| `getGS1ComponentValues()` | Wycinki wartości wg składników (dla AI wieloskładnikowych). |
| `getErrors()` | Błędy na poziomie elementu, inne niż WARNING. |
| `getWarnings()` | Ostrzeżenia poziomu WARNING dla elementu. |
| `getIssues()` | Błędy i ostrzeżenia elementu razem. |
| `hasErrors()` | `true`, jeżeli dołączono błędy inne niż WARNING. |
| `hasWarnings()` | `true`, jeżeli dołączono ostrzeżenia poziomu WARNING. |
| `getInterpretations()` | Wpisy `GS1AIInterpretation` (wypełniane w trybie INTERPRETATION). |
| `getInterpretation(String type)` | Pierwsza interpretacja odpowiadająca podanemu kluczowi typu z `GS1Constants_Enricher` albo `null`. |
| `getDigitalLinkAIType()` | Rola elementu w Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`) albo `null` dla danych wejściowych w postaci ciągu elementów. |
| `hasDigitalLinkAIType()` | `true`, jeżeli przypisano rolę Digital Link. |

---

### GaiaError

Niezmienny błąd walidacji albo ostrzeżenie.

| Metoda | Opis |
|---|---|
| `getId()` | Identyfikator katalogowy, na przykład `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` albo `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` albo `INTERNAL`. |
| `getCode()` | Krótki kod czytelny maszynowo. |
| `getAi()` | Kod AI, który spowodował błąd, albo `null` dla błędów na poziomie obiektu. |
| `getMessage()` | Czytelny komunikat z wstawionymi wartościami. |
| `getPosition()` | Przesunięcie znakowe (liczone od zera) w pierwotnych danych wejściowych. |

---

### GS1AIInterpretation

Pojedynczy opisany fragment interpretacji, dołączany do `GS1AIObjectElement` w trybie `INTERPRETATION`.

| Metoda | Opis |
|---|---|
| `getType()` | Klucz typu czytelny maszynowo, na przykład `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Jednakowy we wszystkich językach. |
| `getLabel()` | Etykieta czytelna dla człowieka, **zlokalizowana do języka analizy** (na przykład `"Date"` / `"GS1 company prefix"` po angielsku). |
| `getValue()` | Wydobyta lub wzbogacona wartość, na przykład `"31/12/2026"`, `"9506000"`. Nie podlega lokalizacji. |

---

### DataCarrierEntry i DataCarrierType

Gdy dane wejściowe niosą identyfikator symboliki AIM, `ParseResult.getDataCarrier()` zwraca obiekt `DataCarrierEntry` opisujący symbol, który przeniósł dane. Wpis ten jest konkretnym rekordem rejestru dla rozpoznanego identyfikatora AIM; `DataCarrierType` to znane w czasie kompilacji wyliczenie, do którego wpis należy.

#### DataCarrierEntry

Metadane jednego rozpoznanego identyfikatora symboliki AIM (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metoda | Opis |
|---|---|
| `getAimCodeId()` | Rozpoznany identyfikator symboliki AIM, na przykład `"]C1"`. |
| `getName()` | Czytelna nazwa konkretnego symbolu, na przykład `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Obszerniejszy opis nośnika. |
| `getType()` | Strukturalny typ nośnika jako łańcuch znaków (odzwierciedla `getDataCarrierType().getCategory()`). |
| `getStandard()` | Standard symboliki, o ile go odnotowano. |
| `getDataCarrierType()` | Typowany `DataCarrierType` tego wpisu — preferowany do kierowania programowego. |
| `isGs1Capable()` | `true`, jeżeli nośnik może zawierać dane GS1 (ciągi elementów AI lub Digital Link). |
| `isGs1AICapable()` | `true`, jeżeli nośnik może zawierać ciągi elementów AI GS1. |
| `isGs1DigitalLinkCapable()` | `true`, jeżeli nośnik może zawierać identyfikator URI GS1 Digital Link. |
| `isEciCapable()` | `true`, jeżeli nośnik obsługuje wskaźnik ECI. |
| `isRequiresGtinPadding()` | `true` dla nośników EAN/UPC/ITF, których wartość liczbowa jest dopełniana do GTIN-14 przed analizą AI. |

#### DataCarrierType

Znane w czasie kompilacji wyliczenie typów nośników danych, indeksowane identyfikatorem symboliki AIM przypisanym w normie ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Znak po `]` (*znak kodu*) wybiera rodzinę; większość rodzin odpowiada jednej stałej obejmującej wszystkie modyfikatory (`ITF` obejmuje `]I0`–`]I2`; `EAN_UPC` obejmuje EAN-13, UPC-A, UPC-E i EAN-8). Tam, gdzie GS1 rezerwuje modyfikator dla danych AI, wariant ten stanowi własną stałą — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — odrębną od zwykłego odpowiednika. Gdy brakuje identyfikatora AIM albo wskazuje on nieznany nośnik, typem jest `UNKNOWN`.

| Metoda | Opis |
|---|---|
| `getCategory()` | Ogólna kategoria `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` albo `OTHER`. |
| `getCodeChar()` | Znak kodu AIM wskazujący rodzinę, na przykład `"Q"` dla QR Code; `null` dla `UNKNOWN`. |
| `getDisplayName()` | Czytelna nazwa *typu* (może być szersza niż `DataCarrierEntry.getName()` — na przykład `"EAN-13 / UPC-A / UPC-E / EAN-8"` wobec `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` dla stałych, które zawsze oznaczają dane AI GS1: czterech wariantów zarezerwowanych przez GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) oraz `GS1_DATABAR`, z natury zgodnego z GS1, ponieważ każdy modyfikator `]e` oznacza GS1 DataBar. Węższe niż `DataCarrierEntry.isGs1AICapable()` — zwykły `QR_CODE` również może nieść dane AI GS1. |
| `static forAimCodeId(String)` | Ustala typ wprost z identyfikatora AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); zwraca `UNKNOWN` dla identyfikatora brakującego, źle zbudowanego lub nierozpoznanego. |

Kierowanie według typu, a nie według nazwy — na przykład aby oddzielić symbole liniowe (Code 128) od symboli 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` obejmuje wyłącznie symbole macierzowe i punktowe; nośniki liniowe piętrowe (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) należą do `STACKED_LINEAR`, choć powszechnie nazywa się je
kodami kreskowymi „2D”. Aby potraktować jedne i drugie jako jedną grupę — na przykład by rozstrzygnąć,
czy potrzebny jest skaner obrazowy zamiast laserowego — sprawdź przynależność do którejkolwiek z dwóch kategorii.

> Ustalenie typu wymaga obecności identyfikatora symboliki AIM w odczycie; bez niego `getDataCarrier()` ma wartość `null`, a typem jest `UNKNOWN`. Skonfiguruj czytnik tak, aby przesyłał przedrostek identyfikatora AIM.

---

## Wykaz błędów

| Kod | Poziom | Etap | Znaczenie |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Nieznany przedrostek AI — nie można ustalić długości danych |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Dane wejściowe zbyt krótkie, aby odczytać pełny kod AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Wartość obcięta — mniej znaków, niż wymaga AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Zduplikowany identyfikator zastosowania w ciągu elementów |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Brak wymaganej zależności AI |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Wykluczająca się para AI — dwa AI, które nie mogą wystąpić razem |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Nieoczekiwany błąd podziału na tokeny |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Znak spoza zestawu kodowalnego GS1 w ciągu elementów |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Brak wymaganego separatora FNC1 po AI o zmiennej długości |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Dane nadmiarowe poza maksimum wszystkich składników |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separator FNC1 po AI o stałej długości w położeniu pośrednim |
| `GE-W002` | WARNING | SYNTAX | FNC1 na końcu ciągu elementów (wyłącznie informacyjnie) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Naruszenia struktury identyfikatora URI Digital Link — po jednym kodzie na warunek (źle zbudowany URI, schemat, host, kolejność kwalifikatorów, zabroniony AI, brak klucza podstawowego (`GE-L013`), wiele kluczy podstawowych (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Wartość nie spełnia wyrażenia regularnego danego AI |
| `GE-C003` | DATA_ERROR | CONTENT | Niepowodzenie walidacji cyfry kontrolnej |
| `GE-C004` | DATA_ERROR | CONTENT | Niepowodzenie walidacji pary znaków kontrolnych |
| `GE-C005` | FORMAT_ERROR | CONTENT | Wartość składnika zawiera znak spoza dozwolonego zestawu |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Niepowodzenia formatu składników — po jednym kodzie na warunek walidacji (zobacz `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Niepowodzenia własnej walidacji semantycznej — po jednym kodzie na warunek walidacji (zobacz `content/validator/`). **Wyjątki:** wymienione niżej 14 kontroli prefiksu firmy GS1 ma poziom `WARNING`, a `GE-C168` (nierozpoznany liczbowy kod kraju wg ISO 3166-1) ma `FORMAT_ERROR`. |
| Kontrole prefiksu firmy GS1 | WARNING | CONTENT | Klucz nie zaczyna się od rozpoznanego prefiksu firmy GS1, w AI niosących klucz GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Wyłącznie informacyjnie — bez wpływu na poprawność. |
| `GE-C169` | DATA_ERROR | CONTENT | Niepowodzenie cyfry kontrolnej IMEI (Luhn) w AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Niepowodzenie cyfry kontrolnej EID (Luhn) w AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Nierozpoznany identyfikator symboliki AIM |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Nośnik rozpoznany, lecz nieobsługujący ani ciągów elementów AI GS1, ani identyfikatorów URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Nieoczekiwany błąd wewnętrzny |

> **Znana usterka w wyświetlaniu komunikatów.** Szablony katalogu ujmują wstawiane wartości
> w podwojone apostrofy na wzór MessageFormat (`''{value}''`), lecz
> `ErrorRegistry` wstawia je zwykłym wywołaniem `String.replace`, więc podwojenie przechodzi aż do
> `getMessage()` — obecnie zobaczysz `value ''09506000134351''` tam, gdzie treści komunikatów
> cytowane w tym podręczniku pokazują `value '09506000134351'`. Dotyczy to każdego komunikatu
> cytującego wartość, we wszystkich 35 katalogach językowych. Nie analizuj komunikatów o błędach;
> porównuj `getId()` / `getCode()`.

---

## Bezpieczeństwo wątkowe

`GaiaParser` jest bezpieczny wątkowo po utworzeniu. Jedną instancję można współdzielić między wątkami i używać jej równolegle. Zalecany wzorzec to utworzenie jednej instancji przy starcie aplikacji i wielokrotne jej wykorzystanie:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` jest niezmienny i równie bezpieczny do współdzielenia. Jedyny obowiązek w zakresie bezpieczeństwa wątkowego, którego biblioteka nie może wypełnić za ciebie, dotyczy [modyfikatorów wejścia](#modyfikatory-wejścia): dla każdego modyfikatora buforowana jest jedna instancja współdzielona przez wszystkie równoległe analizy, dlatego implementacje muszą być bezstanowe.

---

## Dodatek A — stałe łańcuchowe AI

`GS1Constants_AICodes` (w pakiecie `tools.pantheum.gaia.gs1.constants`) deklaruje stałą typu `String` dla każdego identyfikatora zastosowania rozpoznawanego przez GAIA. Używaj tych stałych zamiast wpisywać kody AI jako łańcuchy znaków:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Każda stała zawiera tekstową postać kodu AI (na przykład `AI_01_GTIN = "01"`).

### Identyfikacja i serializacja

| AI | Stała | Opis |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Numer seryjny jednostki logistycznej (SSCC). |
| `01` | `AI_01_GTIN` | Globalny Numer Jednostki Handlowej (GTIN). |
| `02` | `AI_02_CONTENT` | Globalny Numer Jednostki Handlowej (GTIN) zawartych jednostek handlowych. |
| `03` | `AI_03_MTO_GTIN` | Identyfikacja jednostki handlowej wykonanej na zamówienie (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Numer partii. |
| `20` | `AI_20_VARIANT` | Wewnętrzny wariant produktu. |
| `21` | `AI_21_SERIAL` | Numer seryjny. |
| `22` | `AI_22_CPV` | Wariant produktu konsumenckiego. |
| `235` | `AI_235_TPX` | Kontrolowane przez stronę trzecią, serializowane rozszerzenie Globalnego Numeru Jednostki Handlowej (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Dodatkowa identyfikacja produktu nadana przez producenta. |
| `241` | `AI_241_CUST_PART_NO` | Numer części klienta. |
| `242` | `AI_242_MTO_VARIANT` | Numer wariantu wykonania na zamówienie. |
| `243` | `AI_243_PCN` | Numer komponentu opakowania. |
| `250` | `AI_250_SECONDARY_SERIAL` | Dodatkowy numer seryjny. |
| `251` | `AI_251_REF_TO_SOURCE` | Odniesienie do podmiotu źródłowego. |
| `253` | `AI_253_GDTI` | Globalny Identyfikator Typu Dokumentu (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Komponent rozszerzenia Globalnego Numeru Lokalizacyjnego (GLN). |
| `255` | `AI_255_GCN` | Globalny Numer Kuponu (GCN). |
| `30` | `AI_30_VAR_COUNT` | Zmienna liczba sztuk (jednostka handlowa o zmiennej mierze). |
| `37` | `AI_37_COUNT` | Liczba jednostek handlowych lub sztuk jednostek handlowych zawartych w jednostce logistycznej. |

### Daty i godziny

| AI | Stała | Opis |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Data produkcji (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Termin płatności (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Data pakowania (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Data minimalnej trwałości (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Sprzedać do (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Data ważności (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Data i godzina dostawy nie wcześniej niż (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Data i godzina dostawy nie później niż (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Data wydania (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Data i godzina ważności (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Data pierwszego zamrożenia (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Data zbioru (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Data ważności testu (YYMMDD[hhmm]). |

### Ilość i miara — miara zmienna (metryczna)

Czterocyfrowe rodziny AI `310n`–`369n` kodują ilości o zmiennej mierze. Trzecia cyfra wybiera rodzaj miary; **czwarta cyfra** (`n`, 0–5) to liczba domyślnych miejsc dziesiętnych — na przykład `AI_3102_NET_WEIGHT_KG` oznacza masę netto w kg z 2 miejscami dziesiętnymi.

| Rodzina | Wzorzec stałej (`n` = cyfra miejsc dziesiętnych) | Opis |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Masa netto, kilogramy (jednostka handlowa o zmiennej mierze). |
| `311n` | `AI_311n_LENGTH_M` | Długość lub pierwszy wymiar, metry (jednostka handlowa o zmiennej mierze). |
| `312n` | `AI_312n_WIDTH_M` | Szerokość, średnica lub drugi wymiar, metry (jednostka handlowa o zmiennej mierze). |
| `313n` | `AI_313n_HEIGHT_M` | Głębokość, grubość, wysokość lub trzeci wymiar, metry (jednostka handlowa o zmiennej mierze). |
| `314n` | `AI_314n_AREA_M` | Powierzchnia, metry kwadratowe (jednostka handlowa o zmiennej mierze). |
| `315n` | `AI_315n_NET_VOLUME_L` | Objętość netto, litry (jednostka handlowa o zmiennej mierze). |
| `316n` | `AI_316n_NET_VOLUME_M` | Objętość netto, metry sześcienne (jednostka handlowa o zmiennej mierze). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Masa logistyczna, kilogramy. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Długość lub pierwszy wymiar, metry. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Szerokość, średnica lub drugi wymiar, metry. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Głębokość, grubość, wysokość lub trzeci wymiar, metry. |
| `334n` | `AI_334n_AREA_M_LOG` | Powierzchnia, metry kwadratowe. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Objętość logistyczna, litry. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Objętość logistyczna, metry sześcienne. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramy na metr kwadratowy. |

### Ilość i miara — miara zmienna (imperialna / USA)

| Rodzina | Wzorzec stałej (`n` = cyfra miejsc dziesiętnych) | Opis |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Masa netto, funty (jednostka handlowa o zmiennej mierze). |
| `321n` | `AI_321n_LENGTH_IN` | Długość lub pierwszy wymiar, cale (jednostka handlowa o zmiennej mierze). |
| `322n` | `AI_322n_LENGTH_FT` | Długość lub pierwszy wymiar, stopy (jednostka handlowa o zmiennej mierze). |
| `323n` | `AI_323n_LENGTH_YD` | Długość lub pierwszy wymiar, jardy (jednostka handlowa o zmiennej mierze). |
| `324n` | `AI_324n_WIDTH_IN` | Szerokość, średnica lub drugi wymiar, cale (jednostka handlowa o zmiennej mierze). |
| `325n` | `AI_325n_WIDTH_FT` | Szerokość, średnica lub drugi wymiar, stopy (jednostka handlowa o zmiennej mierze). |
| `326n` | `AI_326n_WIDTH_YD` | Szerokość, średnica lub drugi wymiar, jardy (jednostka handlowa o zmiennej mierze). |
| `327n` | `AI_327n_HEIGHT_IN` | Głębokość, grubość, wysokość lub trzeci wymiar, cale (jednostka handlowa o zmiennej mierze). |
| `328n` | `AI_328n_HEIGHT_FT` | Głębokość, grubość, wysokość lub trzeci wymiar, stopy (jednostka handlowa o zmiennej mierze). |
| `329n` | `AI_329n_HEIGHT_YD` | Głębokość, grubość, wysokość lub trzeci wymiar, jardy (jednostka handlowa o zmiennej mierze). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Masa logistyczna, funty. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Długość lub pierwszy wymiar, cale. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Długość lub pierwszy wymiar, stopy. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Długość lub pierwszy wymiar, jardy. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Szerokość, średnica lub drugi wymiar, cale. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Szerokość, średnica lub drugi wymiar, stopy. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Szerokość, średnica lub drugi wymiar, jard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Głębokość, grubość, wysokość lub trzeci wymiar, cale. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Głębokość, grubość, wysokość lub trzeci wymiar, stopy. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Głębokość, grubość, wysokość lub trzeci wymiar, jardy. |
| `350n` | `AI_350n_AREA_IN` | Powierzchnia, cale kwadratowe (jednostka handlowa o zmiennej mierze). |
| `351n` | `AI_351n_AREA_FT` | Powierzchnia, stopy kwadratowe (jednostka handlowa o zmiennej mierze). |
| `352n` | `AI_352n_AREA_YD` | Powierzchnia, jardy kwadratowe (jednostka handlowa o zmiennej mierze). |
| `353n` | `AI_353n_AREA_IN_LOG` | Powierzchnia, cale kwadratowe. |
| `354n` | `AI_354n_AREA_FT_LOG` | Powierzchnia, stopy kwadratowe. |
| `355n` | `AI_355n_AREA_YD_LOG` | Powierzchnia, jardy kwadratowe. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Masa netto, uncje trojańskie (jednostka handlowa o zmiennej mierze). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Masa netto (lub objętość), uncje (jednostka handlowa o zmiennej mierze). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Objętość netto, kwarty (jednostka handlowa o zmiennej mierze). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Objętość netto, galony amerykańskie (jednostka handlowa o zmiennej mierze). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Objętość logistyczna, kwarty. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Objętość logistyczna, galony amerykańskie. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Objętość netto, cale sześcienne (jednostka handlowa o zmiennej mierze). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Objętość netto, stopy sześcienne (jednostka handlowa o zmiennej mierze). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Objętość netto, jardy sześcienne (jednostka handlowa o zmiennej mierze). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Objętość logistyczna, cale sześcienne. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Objętość logistyczna, stopy sześcienne. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Objętość logistyczna, jardy sześcienne. |

### Ceny i kwoty pieniężne

Czwarta cyfra (`n`) koduje liczbę domyślnych miejsc dziesiętnych. Jej dopuszczalny zakres
zależy od rodziny — zobacz kolumnę `n`.

| Rodzina | Wzorzec stałej (`n` = cyfra miejsc dziesiętnych) | `n` | Opis |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Należna kwota do zapłaty lub wartość kuponu, waluta lokalna. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Należna kwota do zapłaty z kodem waluty ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Należna kwota do zapłaty, jednolity obszar walutowy (jednostka handlowa o zmiennej mierze). |
| `393n` | `AI_393n_PRICE` | 0–9 | Należna kwota do zapłaty z kodem waluty ISO (jednostka handlowa o zmiennej mierze). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Procent zniżki kuponu. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Kwota do zapłaty za jednostkę miary, jednolity obszar walutowy (jednostka handlowa o zmiennej mierze). |

### Lokalizacja i wysyłka

| AI | Stała | Opis |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Numer zamówienia zakupu klienta. |
| `401` | `AI_401_GINC` | Globalny Numer Identyfikacyjny Przesyłki (GINC). |
| `402` | `AI_402_GSIN` | Globalny Numer Identyfikacyjny Wysyłki (GSIN). |
| `403` | `AI_403_ROUTE` | Kod trasowania. |
| `410` | `AI_410_SHIP_TO_LOC` | Globalny Numer Lokalizacyjny (GLN) miejsca wysyłki do / dostawy do. |
| `411` | `AI_411_BILL_TO` | Globalny Numer Lokalizacyjny (GLN) miejsca fakturowania. |
| `412` | `AI_412_PURCHASE_FROM` | Globalny Numer Lokalizacyjny (GLN) miejsca zakupu. |
| `413` | `AI_413_SHIP_FOR_LOC` | Globalny Numer Lokalizacyjny (GLN) wysyłki dla / dostawy dla - przekazanie do. |
| `414` | `AI_414_LOC_NO` | Identyfikacja lokalizacji fizycznej - Globalny Numer Lokalizacyjny (GLN). |
| `415` | `AI_415_PAY_TO` | Globalny Numer Lokalizacyjny (GLN) podmiotu wystawiającego fakturę. |
| `416` | `AI_416_PROD_SERV_LOC` | Globalny Numer Lokalizacyjny (GLN) miejsca produkcji lub świadczenia usługi. |
| `417` | `AI_417_PARTY` | Globalny Numer Lokalizacyjny (GLN) podmiotu. |
| `420` | `AI_420_SHIP_TO_POST` | Kod pocztowy miejsca wysyłki do / dostawy do w obrębie jednego urzędu pocztowego. |
| `421` | `AI_421_SHIP_TO_POST` | Kod pocztowy miejsca wysyłki do / dostawy do z kodem kraju ISO. |
| `422` | `AI_422_ORIGIN` | Kraj pochodzenia jednostki handlowej. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Kraj wstępnego przetworzenia. |
| `424` | `AI_424_COUNTRY_PROCESS` | Kraj przetworzenia. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Kraj demontażu. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Kraj obejmujący cały łańcuch procesów. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Podział administracyjny kraju pochodzenia. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Nazwa firmy miejsca wysyłki do / dostawy do. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Osoba kontaktowa miejsca wysyłki do / dostawy do. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Wiersz adresu 1 miejsca wysyłki do / dostawy do. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Wiersz adresu 2 miejsca wysyłki do / dostawy do. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Dzielnica miejsca wysyłki do / dostawy do. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Miejscowość miejsca wysyłki do / dostawy do. |
| `4306` | `AI_4306_SHIP_TO_REG` | Region miejsca wysyłki do / dostawy do. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Kod kraju miejsca wysyłki do / dostawy do. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Numer telefonu miejsca wysyłki do / dostawy do. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Lokalizacja geograficzna miejsca wysyłki do / dostawy do. |
| `4310` | `AI_4310_RTN_TO_COMP` | Nazwa firmy adresu zwrotnego. |
| `4311` | `AI_4311_RTN_TO_NAME` | Osoba kontaktowa adresu zwrotnego. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Wiersz adresu 1 adresu zwrotnego. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Wiersz adresu 2 adresu zwrotnego. |
| `4314` | `AI_4314_RTN_TO_SUB` | Dzielnica adresu zwrotnego. |
| `4315` | `AI_4315_RTN_TO_LOC` | Miejscowość adresu zwrotnego. |
| `4316` | `AI_4316_RTN_TO_REG` | Region adresu zwrotnego. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Kod kraju adresu zwrotnego. |
| `4318` | `AI_4318_RTN_TO_POST` | Kod pocztowy adresu zwrotnego. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Numer telefonu adresu zwrotnego. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Opis kodu usługi. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Flaga towarów niebezpiecznych. |
| `4322` | `AI_4322_AUTH_LEAVE` | Zgoda na pozostawienie przesyłki. |
| `4323` | `AI_4323_SIG_REQUIRED` | Flaga wymaganego podpisu. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maksymalna temperatura w stopniach Fahrenheita (wyrażona w setnych częściach stopnia). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maksymalna temperatura w stopniach Celsjusza (wyrażona w setnych częściach stopnia). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimalna temperatura w stopniach Fahrenheita (wyrażona w setnych częściach stopnia). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimalna temperatura w stopniach Celsjusza (wyrażona w setnych częściach stopnia). |

### Cechy produktu i identyfikowalność

| AI | Stała | Opis |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Numer Zaopatrzeniowy NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Klasyfikacja UN/EKG tusz i elementów mięsnych. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktywna moc. |
| `7005` | `AI_7005_CATCH_AREA` | Obszar połowu. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Gatunek do celów rybołówstwa. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Rodzaj narzędzia połowowego. |
| `7010` | `AI_7010_PROD_METHOD` | Metoda produkcji. |
| `7020` | `AI_7020_REFURB_LOT` | Identyfikator partii odnowienia. |
| `7021` | `AI_7021_FUNC_STAT` | Status funkcjonalny. |
| `7022` | `AI_7022_REV_STAT` | Status wersji. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Globalny Indywidualny Identyfikator Zasobu (GIAI) zespołu. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Numer przetwórcy z trzycyfrowym kodem kraju ISO (10 pozycji). |
| `7040` | `AI_7040_UIC_EXT` | UIC GS1 z rozszerzeniem 1 i indeksem importera. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Typ jednostki ładunkowej UN/CEFACT. |

### Krajowe numery refundacyjne w ochronie zdrowia (NHRN)

| AI | Stała | Opis |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Niemcy PZN. |
| `711` | `AI_711_NHRN_CIP` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Francja CIP. |
| `712` | `AI_712_NHRN_CN` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Hiszpania CN. |
| `713` | `AI_713_NHRN_DRN` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Brazylia DRN. |
| `714` | `AI_714_NHRN_AIM` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Portugalia AIM. |
| `715` | `AI_715_NHRN_NDC` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Stany Zjednoczone Ameryki NDC. |
| `716` | `AI_716_NHRN_AIC` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Włochy AIC. |
| `717` | `AI_717_NHRN_SRN` | Krajowy Numer Refundacji Opieki Zdrowotnej (NHRN) - Kostaryka, numer rejestru sanitarnego. |

### Ochrona zdrowia, GMN, HIDRI, CPID, dane osobowe

| AI | Stała | Opis |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Odniesienie certyfikacyjne (10 pozycji). |
| `7240` | `AI_7240_PROTOCOL` | Identyfikator protokołu. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Typ nośnika AIDC. |
| `7242` | `AI_7242_VCN` | Numer Kontroli Wersji (VCN). |
| `7250` | `AI_7250_DOB` | Data urodzenia (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Data i godzina urodzenia (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Płeć biologiczna. |
| `7253` | `AI_7253_FAMILY_NAME` | Nazwisko osoby. |
| `7254` | `AI_7254_GIVEN_NAME` | Imię osoby. |
| `7255` | `AI_7255_SUFFIX` | Sufiks nazwiska osoby. |
| `7256` | `AI_7256_FULL_NAME` | Pełne imię i nazwisko osoby. |
| `7257` | `AI_7257_PERSON_ADDR` | Adres osoby. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Kolejność urodzenia dziecka. |
| `7259` | `AI_7259_BABY` | Nazwisko rodowe dziecka. |
| `8001` | `AI_8001_DIMENSIONS` | Produkty w rolkach (szerokość, długość, średnica rdzenia, kierunek, łączenia). |
| `8002` | `AI_8002_CMT_NO` | Identyfikator telefonu komórkowego. |
| `8003` | `AI_8003_GRAI` | Globalny Identyfikator Zasobu Zwrotnego (GRAI). |
| `8004` | `AI_8004_GIAI` | Globalny Indywidualny Identyfikator Zasobu (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Cena za jednostkę miary. |
| `8006` | `AI_8006_ITIP` | Identyfikacja pojedynczej sztuki jednostki handlowej (ITIP). |
| `8007` | `AI_8007_IBAN` | Międzynarodowy Numer Rachunku Bankowego (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Data i godzina produkcji (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optycznie odczytywalny wskaźnik czujnika. |
| `8010` | `AI_8010_CPID` | Identyfikator komponentu/części (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Numer seryjny identyfikatora komponentu/części (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Wersja oprogramowania. |
| `8013` | `AI_8013_GMN` | Globalny Numer Modelu (GMN). |
| `8014` | `AI_8014_MUDI` | Identyfikator Rejestracyjny Wysoce Zindywidualizowanego Wyrobu (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Globalny Numer Relacji Usługowej (GSRN) do identyfikacji relacji między organizacją oferującą usługi a usługodawcą. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Globalny Numer Relacji Usługowej (GSRN) do identyfikacji relacji między organizacją oferującą usługi a odbiorcą usług. |
| `8019` | `AI_8019_SRIN` | Numer Instancji Relacji Usługowej (SRIN). |
| `8020` | `AI_8020_REF_NO` | Numer referencyjny dowodu wpłaty. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identyfikacja sztuk jednostki handlowej (ITIP) zawartych w jednostce logistycznej. |
| `8030` | `AI_8030_DIGSIG` | Podpis cyfrowy (DigSig). |
| `8040` | `AI_8040_IMEI` | Międzynarodowy Numer Identyfikacyjny Urządzenia Mobilnego (IMEI). |
| `8041` | `AI_8041_IMEI2` | Międzynarodowy Numer Identyfikacyjny Urządzenia Mobilnego 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Numer wbudowanej karty SIM. |
| `8043` | `AI_8043_PSIM` | Numer fizycznej karty SIM. |
| `8110` | `AI_8110` | Identyfikacja kodu kuponu do użytku w Ameryce Północnej. |
| `8111` | `AI_8111_POINTS` | Punkty lojalnościowe kuponu. |
| `8112` | `AI_8112` | Identyfikacja kodu kuponu z pliku ofert pozytywnych do użytku w Ameryce Północnej. |
| `8200` | `AI_8200_PRODUCT_URL` | Adres URL rozszerzonego opakowania. |

### Użytek wewnętrzny / firmowy

| AI | Stała | Opis |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informacje uzgodnione wzajemnie między partnerami handlowymi. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Informacja wewnętrzna firmy (9 pozycji). |

---

## Dodatek B — stałe kluczy interpretacji

Gdy `GaiaParser.parse()` wywoła się z `ParseMode.INTERPRETATION`, każdy `GS1AIObjectElement` może nieść listę obiektów `GS1AIInterpretation` wytworzonych przez wyspecjalizowane wzbogacacze. Używaj stałych z `GS1Constants_Enricher` (w pakiecie `tools.pantheum.gaia.gs1.constants`) jako kluczy do wyszukiwania konkretnych wartości interpretacji:

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

Etykiety wyświetlane **nie** są stałymi — znajdują się w zlokalizowanych katalogach w `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indeksowanych stałą typu. `GS1AIInterpretation.getLabel()` zwraca etykietę dla języka analizy (zobacz [Zlokalizowane komunikaty i etykiety](#zlokalizowane-komunikaty-i-etykiety)), sięgając po angielski, gdy katalog pomija dany klucz. Kolumna „Etykieta wyświetlana” poniżej podaje tekst polski w postaci dostarczanej w katalogu; same klucze typu są jednakowe we wszystkich językach — porównuj więc zawsze klucz, nigdy etykietę.

### Data i godzina

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `DATE_VALUE` | Data | AI daty (11–17, 7003, 7006, 7011 itd.) |
| `DATE_FORMAT` | Format daty | AI daty |
| `TIME_VALUE` | Godzina | AI niosące godzinę (7003, 7011, 8008 itd.) |
| `TIME_FORMAT` | Format godziny | AI niosące godzinę |
| `DATETIME_VALUE` | Data i godzina | AI daty i godziny |
| `DATETIME_FORMAT` | Format daty i godziny | AI daty i godziny |

### Data zbioru

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Data rozpoczęcia zbioru | AI 7007 |
| `HARVEST_END_DATE` | Data zakończenia zbioru | AI 7007 (opcjonalny koniec zakresu) |
| `HARVEST_DATE_RANGE` | Zakres dat zbioru | AI 7007 |

### Prefiks firmy GS1

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefiks firmy GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Kod członka GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organizacja członkowska GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Typ GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Poziom opakowania | AI 01 |
| `GTIN_CHECK_DIGIT` | Cyfra kontrolna | AI 01, 02 |

### SSCC

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Cyfra rozszerzenia | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Odniesienie seryjne | AI 00 |
| `SSCC_CHECK_DIGIT` | Cyfra kontrolna | AI 00 |

### Kraj (ISO 3166)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Kod kraju (numeryczny) | AI jednego kraju (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Kod kraju (alfa-2) | AI krajów w kodzie alfa-2 |
| `COUNTRY_NAME` | Nazwa kraju | AI jednego kraju |
| `COUNTRY_LIST` | Kraje | AI 423 — wszystkie nazwy połączone, na przykład `Australia, New Zealand` |

AI 423 (kraj pierwszego przetworzenia) może nieść do pięciu krajów, dlatego wytwarza
**numerowaną parę dla każdego kraju** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — a po nich jedno podsumowanie
`COUNTRY_LIST`. Buduj te klucze ze stałych `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` oraz indeksu liczonego od 1 albo po prostu przejdź `getInterpretations()`; klucze
`COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` bez przyrostka **nie** są wytwarzane dla AI 423.

### Waluta (ISO 4217)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Kod waluty | AI kwoty z walutą (391n, 393n) |
| `CURRENCY_ALPHA` | Alfabetyczny kod waluty | AI kwoty z walutą |
| `CURRENCY_NAME` | Nazwa waluty | AI kwoty z walutą |

### Temperatura

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatura | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Jednostka temperatury | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatura (sformatowana) | AI 4330–4333 |

### Płeć (ISO 5218)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `SEX_CODE` | Kod płci | AI 7252 |
| `SEX_DESCRIPTION` | Opis płci | AI 7252 |

### Gatunki wodne (FAO)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Kod gatunku | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nazwa naukowa | AI 7008 |
| `SPECIES_ENGLISH` | Nazwa zwyczajowa | AI 7008 |
| `SPECIES_FAMILY` | Rodzina | AI 7008 |
| `SPECIES_ORDER` | Rząd | AI 7008 |

### Numer magazynowy NATO (NSN)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `NSN_FSG` | Grupa zaopatrzenia | AI 7001 |
| `NSN_FSG_NAME` | Nazwa grupy zaopatrzenia | AI 7001 |
| `NSN_FSCG` | Klasa zaopatrzenia | AI 7001 |
| `NSN_FSCG_NAME` | Nazwa klasy zaopatrzenia | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Kod kraju | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Kraj | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Kod kraju ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategoria NCS | AI 7001 |
| `NSN_NIIN` | Krajowy numer pozycji | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Produkty w rolkach

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Szerokość rolki (mm) | AI 8001 |
| `ROLL_LENGTH` | Długość rolki (m) | AI 8001 |
| `CORE_DIAMETER` | Średnica rdzenia (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Kod kierunku nawijania | AI 8001 |
| `WINDING_DIRECTION` | Kierunek nawijania | AI 8001 |
| `SPLICES` | Łączenia | AI 8001 |

### IBAN

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Kod kraju | AI 8007 |
| `IBAN_COUNTRY_NAME` | Kraj | AI 8007 |
| `IBAN_CHECK_DIGITS` | Cyfry kontrolne | AI 8007 |
| `IBAN_CHECK_VALID` | Kontrola | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Numer seryjny | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Cyfra kontrolna | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Organ wydający | AI 8040, 8041 |

Piętnaście cyfr rozkłada się na `[ TAC (8) ][ numer seryjny (6) ][ cyfra kontrolna Luhna (1) ]`, przy czym
RBI to dwie pierwsze cyfry TAC — `IMEI_RBI` jest zatem przedrostkiem `IMEI_TAC`, a nie
osobnym odcinkiem. `IMEI_FORMATTED` przedstawia standardowe grupowanie wyświetlania GSMA
`AA-BBBBBB-CCCCCC-D` (na przykład `49-015420-323751-8`), które dzieli TAC na granicy
RBI; dawne grupowanie `6-2-6-1`, przecinające tam, gdzie zaczynał się wycofany Final Assembly
Code, nie jest wytwarzane.

`IMEI_RBI_NAME` odwzorowuje RBI na nazwę instytucji przydzielającej za pomocą `ImeiRbiData` i jest
**dołączany na końcu, wyłącznie gdy kod widnieje w tej tablicy**. Tablica ta obejmuje trzy grupy:

- **Obecnie przydzielające** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF oraz `99`
  Global Hexadecimal Administrator i `98` (zarezerwowany).
- **Zakresy testowe** — `00` oraz `02`–`09`, oznaczające testowe numery IMEI, a nie rzeczywisty przydział.
  Sprawdzisz je przez `ImeiRbiData.isTestCode(code)`.
- **Już nieprzydzielające** — instytucje historyczne, takie jak `49` (BZT/BAPT, Niemcy), `44`
  (BABT, Wielka Brytania) czy `91` (MSAI, Indie). Sprawdzisz je przez `ImeiRbiData.isNoLongerAllocating(code)`.
  Urządzenia z tymi kodami są zwyczajne i pozostają w użyciu; ustał jedynie przydział nowych kodów,
  jest to zatem informacja sprawozdawcza, nigdy sygnał o poprawności.

Brak `IMEI_RBI_NAME` oznacza „tego RBI nie ma w naszej tablicy”, a **nie** „niepoprawny IMEI”:
tablicę zestawiono z opublikowanego wykazu RBI, a nie bezpośrednio od GSMA, może więc
pozostawać w tyle za niedawno wyznaczonymi instytucjami. Nie wyciągaj z jej braku żadnych wniosków walidacyjnych;
RBI nie jest znakiem kontrolnym. Kod przechodzący listę interpretacji również musi
znieść jego brak, zamiast indeksować według położenia.

### Identyfikatory SIM (EID / ICCID)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategoria branży | AI 8042 |
| `EID_BODY` | Główna część EID | AI 8042 |
| `EID_CHECK_DIGIT` | Cyfra kontrolna | AI 8042 |
| `ICCID_BODY` | Główna część ICCID | AI 8043 |
| `ICCID_EXTENSION` | Rozszerzenie | AI 8043 |

`SIM_MII` niesie **dwie** pierwsze cyfry (`89`) — parę, którą ITU-T E.118 przypisuje
telekomunikacji. Sama norma ISO/IEC 7812 definiuje MII jako **wyłącznie pierwszą cyfrę**, dlatego
`SIM_MII_NAME` ustala kategorię z tej wiodącej `8` za pomocą `Iso7812Data`, co daje
„Healthcare, telecommunications and other future industry assignments”. Dla poprawnie zbudowanego
EID wartość ta jest więc stała; podaje się ją dla zachowania zgodności ze standardem, a nie jako
kryterium rozróżniające. `Iso7812Data.nameForCode(digit)` przyjmuje pojedynczą cyfrę,
a `nameForIdentifier(prefix)` przyjmuje dłuższy przedrostek i odczytuje jego pierwszą cyfrę.

`SIM_MII_NAME` wytwarza wyłącznie `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
udostępnia `SIM_MII` bez kategorii.

### Odniesienie certyfikacyjne

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Numer sekwencyjny | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Kod schematu certyfikacji | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Schemat certyfikacji | AI 7230–7239 |
| `CERT_REFERENCE` | Numer certyfikatu | AI 7230–7239 |

### GS1 UIC

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `UIC_CODE` | Kod UIC | AI 7040 |
| `UIC_EXTENSION_1` | Rozszerzenie 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Indeks importera | AI 7040 |

### Kolejność urodzenia noworodka

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Pozycja urodzenia | AI 7258 |
| `BIRTH_TOTAL` | Łączna liczba urodzeń | AI 7258 |
| `BIRTH_SEQUENCE` | Kolejność urodzenia | AI 7258 |

### Globalny numer modelu (GMN)

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Numer modelu | AI 8013 |
| `GMN_CHECK_PAIR` | Para kontrolna | AI 8013 |

### HIDRI

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Numer urządzenia | AI 8014 |
| `HIDRI_CHECK_PAIR` | Para kontrolna | AI 8014 |

### CPID

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Numer komponentu i części | AI 8010–8011 |

### Wartości dziesiętne i miarowe

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Wartość dziesiętna | AI liczbowe z domyślnymi miejscami dziesiętnymi (31xx–36xx) |
| `DECIMAL_AMOUNT` | Kwota | AI ceny (390n–395n) |
| `DECIMAL_PERCENTAGE` | Procent | AI 394n |
| `DECIMAL_PLACES` | Miejsca dziesiętne | Wraz z `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Format procentu | AI 394n |
| `ISO_UNIT_CODE` | Kod jednostki ISO | AI miary |
| `ISO_UNIT_NAME` | Nazwa jednostki ISO | AI miary |
| `MONETARY_AMOUNT` | Kwota pieniężna | AI ceny |
| `MONETARY_AMOUNT_DISPLAY` | Kwota pieniężna (sformatowana) | AI ceny |

### Współrzędne geograficzne

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `LATITUDE` | Szerokość geograficzna | AI 4309 |
| `LONGITUDE` | Długość geograficzna | AI 4309 |
| `GEO_COORDINATES` | Współrzędne geograficzne | AI 4309 |
| `LATITUDE_DMS` | Szerokość geograficzna (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Długość geograficzna (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Współrzędne geograficzne (DMS) | AI 4309 |

### Metoda produkcji

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Kod metody produkcji | AI 7010 |
| `PRODUCTION_METHOD` | Metoda produkcji | AI 7010 |

### Typ nośnika AIDC

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Kod typu nośnika AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Typ nośnika AIDC | AI 7241 |

### Sztuka z całości

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Numer sztuki | AI 8006 |
| `PIECE_TOTAL` | Łączna liczba sztuk | AI 8006 |
| `PIECE_OF_TOTAL` | Sztuka z całości | AI 8006 |

### Podziały na składniki

Klucze wytwarzane przez deklaratywne podziały na składniki z pliku `content/ai-content.json`, a nie
przez wzbogacacz w Javie — ujawniają nazwane części złożonej wartości AI. W odróżnieniu od wszystkich
pozostałych kluczy w tym dodatku **nie mają one stałej w `GS1Constants_Enricher`**: porównuj
dosłowny łańcuch znaków albo odczytaj typ przez `GS1AIInterpretation.getType()`.

| Klucz typu | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Cyfra kontrolna | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Numer seryjny | AI 253, 255, 8003 |
| `POSTAL_CODE` | Kod pocztowy | AI 421 |
| `PROCESSOR_ID` | Identyfikator przetwórcy | AI 7030–7039 |

Zwróć uwagę, że `CHECK_DIGIT` jest tutaj ogólnym kluczem podziału na składniki, odrębnym od
właściwych wzbogacaczom kluczy `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` i
`EID_CHECK_DIGIT` wymienionych powyżej.

### Różne

| Stała klucza | Etykieta wyświetlana | Wytwarzana przez |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Wartość | AI logiczne / znacznikowe (4321–4323) |
| `DECODED_TEXT` | Zdekodowany tekst | AI tekstu swobodnego |
