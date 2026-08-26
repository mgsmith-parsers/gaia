# GaiaParser — Szybki start

Zamień ładunek kodu kreskowego GS1 w ustrukturyzowane, sprawdzone i czytelne dla człowieka dane
w mniej więcej dziesięć minut. To droga na skróty; **[podręcznik programisty GaiaParser](GaiaParser-Polish.md)** stanowi
pełne odniesienie, a **[GaiaBuilder](GaiaBuilder-Polish.md)** opisuje kierunek odwrotny
(budowanie ciągów elementów i identyfikatorów URI Digital Link).

## Spis treści

1. [Dodanie Gai do projektu](#1-dodanie-gai-do-projektu)
2. [Pierwsza analiza](#2-pierwsza-analiza)
3. [Odczytanie wyniku](#3-odczytanie-wyniku)
4. [Obsługa nieudanej analizy](#4-obsługa-nieudanej-analizy)
5. [Dwie rzeczy, które dadzą ci się we znaki](#5-dwie-rzeczy-które-dadzą-ci-się-we-znaki)
6. [Przedrostki czytników i Digital Link działają od razu](#6-przedrostki-czytników-i-digital-link-działają-od-razu)
7. [Mniej pracy: tryby analizy](#7-mniej-pracy-tryby-analizy)
8. [Zmiana języka i formatu daty](#8-zmiana-języka-i-formatu-daty)
9. [Porządkowanie nieuporządkowanych danych wejściowych](#9-porządkowanie-nieuporządkowanych-danych-wejściowych)
10. [Co dalej](#10-co-dalej)

---

## 1. Dodanie Gai do projektu

Gaia nie jest publikowana w Maven Central, zbuduj więc moduł główny raz i zainstaluj go w swoim
repozytorium lokalnym:

```bash
cd gaia && mvn install
```

Następnie zadeklaruj zależność:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

To cała lista zależności, jaką musisz zapisać. Plik jar jest lekki, więc jedyna zależność
Gai o zasięgu kompilacji — `com.fasterxml.jackson.core:jackson-databind` — pojawia się
przechodnio; jeżeli twoja kompilacja już ustala wersję Jacksona, to ona wygrywa i to jej Gaia użyje.
Gaia jest przeznaczona dla **Javy 11**, a ten sam plik jar działa bez zmian na każdej późniejszej maszynie JVM.

> Pominięcie zestawu testów modułu głównego (`mvn install -DskipTests`) zamienia kilka minut w kilka
> sekund, dopóki dopiero się wdrażasz.

---

## 2. Pierwsza analiza

Jedna klasa, żadnej konfiguracji:

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

`parse(String)` wykonuje **cały** potok: składnię, walidację treści i interpretację.
To właściwe ustawienie domyślne — zawęź je później, jeżeli pomiary dadzą ci ku temu powód.

---

## 3. Odczytanie wyniku

`ParseResult.getAiObject()` zawiera rozpoznane identyfikatory AI. Sięgaj po konkretny po jego kodzie,
a nie po położeniu:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Każdy element niesie listę **interpretacji** — odczytane znaczenie kryjące się za surowymi cyframi,
wytworzone przez etap interpretacji:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` jest zlokalizowane i przeznaczone do wyświetlania. Aby *odczytać* wartość w kodzie, znajdź ją
raczej po jej kluczu typu, który jest niezmienny:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Różne identyfikatory AI dają różne klucze: GTIN zwraca swój prefiks firmy, typ GTIN i cyfrę
kontrolną; cena zwraca walutę i kwotę dziesiętną. Pełną listę zawiera
[dodatek B](GaiaParser-Polish.md#dodatek-b--stałe-kluczy-interpretacji), a stałe znajdują się
w `GS1Constants_Enricher`. Nie każdy AI ma interpretacje: numer partii jako tekst swobodny nie
pozwala niczego wywnioskować, więc jego lista pozostaje pusta.

---

## 4. Obsługa nieudanej analizy

Niepoprawny ładunek to zwykły wynik, a nie wyjątek — `parse` nigdy nie zgłasza wyjątku dla błędnych
danych GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Rozgałęziaj się po `getId()`, nigdy po treści komunikatu.** Komunikaty są zlokalizowane, a ich brzmienie
nie stanowi umowy — niosą też obecnie znaną usterkę cudzysłowów (podwojone `''` powyżej),
odnotowaną w [wykazie błędów](GaiaParser-Polish.md#wykaz-błędów).

Dwa różne pytania, dwie różne metody:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Analiza przestaje schodzić niżej, gdy tylko jakiś etap zawiedzie, więc błędna cyfra kontrolna daje ci
błędy walidacji, ale żadnych interpretacji.

### Ostrzeżenia nie unieważniają wyniku

Niektóre kontrole mają charakter wyłącznie informacyjny. Nierozpoznany prefiks firmy GS1 zostaje zgłoszony, lecz ładunek
pozostaje strukturalnie w porządku:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Użyj `getIssues()`, gdy potrzebujesz jednych i drugich. Jeżeli twój proces musi odrzucać nieznane prefiksy, sprawdzaj
`getWarnings()` jawnie — `isValid()` nie zrobi tego za ciebie.

---

## 5. Dwie rzeczy, które dadzą ci się we znaki

### Separator GS i dlaczego jego pominięcie jest gorsze niż błąd

AI o zmiennej długości ciągnie się aż do **znaku GS** (ASCII `0x1D`, w symbolikach kodów kreskowych
nazywanego FNC1) albo do końca łańcucha. Gdy następuje po nim kolejny AI, separator ten jest
obowiązkowy:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Pomiń go, a **nie** dostaniesz błędu — dostaniesz odpowiedź błędną, lecz podaną z pełnym przekonaniem:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` ma format `X..20`, więc całkiem zasadnie połyka `LOT-ABC21SN-98765`, a analizator nie ma
żadnej możliwości stwierdzić, że nie o to chodziło. Nic dalej w łańcuchu tego nie naprawi, zadbaj więc
o poprawny separator już u źródła: odczytuj bajty z czytnika jako **ISO-8859-1**, aby `0x1D` przetrwał, i zapisuj
`""` w literałach łańcuchowych Javy. Identyfikatory AI o stałej długości (`01`, `17`, `3103`) nie potrzebują separatora —
analizator zna ich długość.

### Większość AI nie może występować samodzielnie

Partia, numer seryjny, termin przydatności i im podobne to *atrybuty*: dokument GS1 General Specifications
wymaga, aby podróżowały wraz z kluczem identyfikacyjnym, a Gaia tego pilnuje.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Dodaj GTIN, a analiza się powiedzie. Jeżeli naprawdę musisz przeanalizować fragment — test jednostkowy,
częściowy odczyt — wyłącz tę kontrolę:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Przedrostki czytników i Digital Link działają od razu

Nie musisz mówić Gai, jaką postać mają dane wejściowe — rozpoznaje wszystkie cztery. Podaj jej
po prostu to, co przekazał czytnik.

**Przedrostek identyfikatora symboliki AIM** wskazuje symbolikę i zostaje usunięty automatycznie:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Identyfikator URI GS1 Digital Link** przechodzi tę samą walidację i to samo wzbogacanie:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Ponieważ obie postacie trafiają do tego samego obiektu `GS1AIObject`, kod przetwarzający odczyt nie musi
się przejmować tym, która z nich nadeszła — a `toElementString()` / `getCanonicalDigitalLink()`
przechodzą z jednej w drugą.

Również **ośmiocyfrowy przedrostek korelacji** (`12345678~…`) zostaje usunięty i zachowany w
`getCorrelationInfo()`, jeżeli twój potok z niego korzysta.

---

## 7. Mniej pracy: tryby analizy

Ustawienie domyślne robi wszystko. Proś o mniej, gdy potrzebujesz tylko części odpowiedzi:

| Tryb | Odpowiada na pytanie | Koszt |
|---|---|---|
| `DATA_CARRIER` | Jaka to symbolika? | Najniższy — żadnej analizy AI, `getAiObject()` ma wartość `null` |
| `SYNTAX` | Czy kody AI i długości są poprawnie zbudowane? | Bez cyfr kontrolnych, bez interpretacji |
| `CONTENT` | Czy to poprawne dane GS1? | Pełna walidacja, bez interpretacji |
| `INTERPRETATION` | Co to znaczy? | **Domyślnie** — wszystko |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Sięgaj po `CONTENT`, gdy sprawdzasz poprawność masowo i nigdy nie pokazujesz rozbioru, a po
`DATA_CARRIER`, gdy musisz jedynie skierować odczyt do właściwej procedury obsługi.

---

## 8. Zmiana języka i formatu daty

Komunikaty o błędach, etykiety interpretacji i opisy AI przetłumaczono na **35 języków**;
daty wyświetlają się tak, jak zechcesz. Wszystko to mieści się w jednym niezmiennym obiekcie `ParseConfig`:

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

Wartości nigdy nie podlegają lokalizacji — podlegają jej tylko etykiety, opisy i komunikaty — więc `"2026-12-31"` i
`"09506000134352"` znaczą to samo w każdym języku. Zbuduj konfigurację raz przy starcie
i współdziel ją; jest niezmienna.

---

## 9. Porządkowanie nieuporządkowanych danych wejściowych

Jeżeli twoje źródło wysyła wydrukowane nawiasy HRI albo pojedyncze spacje, moduł główny zawiera dwa
**modyfikatory wejścia**, które naprawiają ładunek przed analizą:

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

Domyślnie nic nie jest włączone, a oba mają swoje zastrzeżenia: spacja i nawiasy są dopuszczalnymi
znakami danych GS1, stosuj je więc wyłącznie do źródła, które znasz. Zobacz
[Wbudowane modyfikatory](GaiaParser-Polish.md#wbudowane-modyfikatory), gdzie wyjaśniono również, dlaczego usunięcie
nawiasów musi przywrócić separator, który był w nich zawarty.

---

## 10. Co dalej

- **[Podręcznik programisty GaiaParser](GaiaParser-Polish.md)** — potok przetwarzania w szczegółach, pełny model
  wyniku, wszystkie kody błędów oraz dodatki o AI i kluczach interpretacji.
- **[Podręcznik programisty GaiaBuilder](GaiaBuilder-Polish.md)** — budowanie ciągów elementów i identyfikatorów URI
  Digital Link z par AI/wartość.
- **[Dokumentacja HTTP API Gaia](../../gaia-api-reference.md)** — ten sam mechanizm przez HTTP, jeżeli
  wolisz nie osadzać biblioteki.
- **[ai-codes.txt](../../ai-codes.txt)** — płaska lista `(AI) TYTUŁ` do szybkiego sprawdzenia.

### Wersja w pięciu wierszach

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
