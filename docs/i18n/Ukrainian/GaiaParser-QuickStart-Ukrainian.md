# GaiaParser — Швидкий старт

Перетворіть корисне навантаження штрихового коду GS1 на структуровані, перевірені та зрозумілі людині дані
приблизно за десять хвилин. Це короткий шлях; **[посібник розробника GaiaParser](GaiaParser-Ukrainian.md)** є
повним довідником, а **[GaiaBuilder](GaiaBuilder-Ukrainian.md)** описує зворотний напрям
(побудову рядків елементів та URI Digital Link).

## Зміст

1. [Додавання Gaia до проєкту](#1-додавання-gaia-до-проєкту)
2. [Перший розбір](#2-перший-розбір)
3. [Читання результату](#3-читання-результату)
4. [Обробка невдалого розбору](#4-обробка-невдалого-розбору)
5. [Дві речі, на яких ви обпечетеся](#5-дві-речі-на-яких-ви-обпечетеся)
6. [Префікси сканерів і Digital Link працюють одразу](#6-префікси-сканерів-і-digital-link-працюють-одразу)
7. [Менше роботи: режими розбору](#7-менше-роботи-режими-розбору)
8. [Зміна мови та формату дати](#8-зміна-мови-та-формату-дати)
9. [Упорядкування неохайного введення](#9-упорядкування-неохайного-введення)
10. [Що далі](#10-що-далі)

---

## 1. Додавання Gaia до проєкту

Gaia не публікується в Maven Central, тож зберіть основний модуль один раз і встановіть його у свій
локальний репозиторій:

```bash
cd gaia && mvn install
```

Далі оголосіть залежність:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Це весь перелік залежностей, який вам потрібно написати. Jar-файл легкий, тож єдина
залежність Gaia з областю компіляції — `com.fasterxml.jackson.core:jackson-databind` — приходить
транзитивно; якщо ваше збирання вже фіксує версію Jackson, перемагає вона, і саме її Gaia використовує.
Gaia розрахована на **Java 11**, і той самий jar-файл працює без змін на будь-якій пізнішій JVM.

> Пропуск набору тестів основного модуля (`mvn install -DskipTests`) перетворює кілька хвилин на кілька
> секунд, доки ви тільки опановуєте бібліотеку.

---

## 2. Перший розбір

Один клас, жодних налаштувань:

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

`parse(String)` виконує **весь** конвеєр: синтаксис, перевірку вмісту й тлумачення.
Це правильне типове значення — звузьте його згодом, якщо вимірювання дадуть вам на те підставу.

---

## 3. Читання результату

`ParseResult.getAiObject()` містить розпізнані AI. Звертайтеся до потрібного за його кодом, а не
за положенням:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Кожен елемент несе список **тлумачень** — розшифрований зміст, схований за необробленими цифрами
й одержаний на етапі тлумачення:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` локалізується й призначений для показу. Щоб *прочитати* значення в коді, знаходьте його
краще за ключем типу, який незмінний:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Різні AI дають різні ключі: GTIN повертає свій префікс компанії, тип GTIN і контрольну
цифру; ціна — валюту й десяткову суму. Повний перелік наведено в
[додатку B](GaiaParser-Ukrainian.md#додаток-b--сталі-ключів-тлумачення), а сталі розміщено
в `GS1Constants_Enricher`. Тлумачення є не в кожного AI: з номера партії у довільному тексті
вивести нічого, тож його список лишається порожнім.

---

## 4. Обробка невдалого розбору

Недійсне корисне навантаження — звичайний результат, а не виняток: `parse` ніколи не викидає винятків
через хибні дані GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Розгалужуйтеся за `getId()`, а не за текстом повідомлення.** Повідомлення локалізуються, і їхнє формулювання
не є договором — до того ж зараз вони несуть відомий дефект лапок (подвоєні `''` вище),
зазначений у [довіднику помилок](GaiaParser-Ukrainian.md#довідник-помилок).

Два різні питання — два різні методи:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Розбір перестає заглиблюватися, щойно етап дає збій, тож хибна контрольна цифра дає вам
помилки перевірки, але жодних тлумачень.

### Попередження не роблять результат недійсним

Деякі перевірки мають довідковий характер. Про нерозпізнаний префікс компанії GS1 повідомляють, але корисне навантаження
лишається структурно справним:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Користуйтеся `getIssues()`, коли потрібні й ті, й ті. Якщо ваш процес зобов'язаний відхиляти невідомі префікси, перевіряйте
`getWarnings()` явно — `isValid()` цього за вас не зробить.

---

## 5. Дві речі, на яких ви обпечетеся

### Роздільник GS і чому його пропуск гірший за помилку

AI змінної довжини тягнеться до **знака GS** (ASCII `0x1D`, у символіках штрихових кодів
його називають FNC1) або до кінця рядка. Коли за ним іде інший AI, цей роздільник
обов'язковий:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Пропустіть його — і ви отримаєте **не** помилку, а впевнено подану хибну відповідь:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` має формат `X..20`, тож він цілком законно ковтає `LOT-ABC21SN-98765`, а в аналізатора немає
жодної змоги дізнатися, що задумувалося інше. Далі по ланцюжку цього вже не виправити, тож подбайте
про правильний роздільник просто в джерелі: зчитуйте байти сканера як **ISO-8859-1**, щоб `0x1D` уцілів, і пишіть
`""` у рядкових літералах Java. Ідентифікатори AI сталої довжини (`01`, `17`, `3103`) роздільника не потребують —
аналізатор знає їхню довжину.

### Більшість AI не може стояти окремо

Партія, серійний номер, термін придатності та подібні до них — це *атрибути*: GS1 General Specifications
вимагають, щоб вони йшли разом із ключем ідентифікації, і Gaia цього дотримується.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Додайте GTIN — і розбір мине. Якщо вам справді потрібно розібрати фрагмент — модульний тест,
часткове зчитування, — вимкніть цю перевірку:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Префікси сканерів і Digital Link працюють одразу

Вам не потрібно повідомляти Gaia, якого вигляду введення, — вона розпізнає всі чотири. Просто передайте їй
те, що видав сканер.

**Префікс ідентифікатора символіки AIM** визначає символіку й вилучається автоматично:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**URI GS1 Digital Link** проходить ту саму перевірку й те саме збагачення:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Оскільки обидва вигляди потрапляють в один і той самий `GS1AIObject`, коду, що споживає зчитані дані, не потрібно
перейматися, який із них надійшов, — а `toElementString()` / `getCanonicalDigitalLink()`
перетворюють один на інший.

**Восьмизначний префікс кореляції** (`12345678~…`) також вилучається й зберігається в
`getCorrelationInfo()`, якщо ваш конвеєр ним користується.

---

## 7. Менше роботи: режими розбору

Типове значення робить усе. Просіть менше, коли потрібна лише частина відповіді:

| Режим | Відповідає на питання | Вартість |
|---|---|---|
| `DATA_CARRIER` | Що це за символіка? | Найнижча — розбору AI немає взагалі, `getAiObject()` дорівнює `null` |
| `SYNTAX` | Чи правильно побудовано коди AI та довжини? | Без контрольних цифр і без тлумачень |
| `CONTENT` | Чи дійсні це дані GS1? | Повна перевірка, без тлумачень |
| `INTERPRETATION` | Що це означає? | **Типово** — усе |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Удавайтеся до `CONTENT`, коли перевіряєте великі обсяги й ніколи не показуєте розбір, і до
`DATA_CARRIER`, коли потрібно лише спрямувати зчитані дані до потрібного обробника.

---

## 8. Зміна мови та формату дати

Повідомлення про помилки, позначки тлумачення та описи AI перекладено **35 мовами**;
дати відображаються так, як вам заманеться. Усе це вміщується в один незмінний `ParseConfig`:

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

Значення не локалізуються ніколи — локалізуються лише позначки, описи й повідомлення, — тож `"2026-12-31"` та
`"09506000134352"` означають те саме будь-якою мовою. Побудуйте конфігурацію один раз під час запуску
й використовуйте її спільно; вона незмінна.

---

## 9. Упорядкування неохайного введення

Якщо ваше джерело видає надруковані дужки HRI або поодинокі пробіли, в основному модулі є два
**модифікатори введення**, які виправляють корисне навантаження до розбору:

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

Типово не ввімкнено нічого, і в обох є застереження: пробіл і дужки — дозволені знаки
даних GS1, тож застосовуйте їх лише до джерела, яке вам знайоме. Див.
[Вбудовані модифікатори](GaiaParser-Ukrainian.md#вбудовані-модифікатори), де пояснено й те, чому вилучення
дужок зобов'язане відновити роздільник, який вони мали на увазі.

---

## 10. Що далі

- **[Посібник розробника GaiaParser](GaiaParser-Ukrainian.md)** — конвеєр обробки в подробицях, повна модель
  результату, усі коди помилок, а також додатки про AI та ключі тлумачення.
- **[Посібник розробника GaiaBuilder](GaiaBuilder-Ukrainian.md)** — побудова рядків елементів та URI Digital Link
  із пар AI/значення.
- **[Довідник HTTP API Gaia](../../gaia-api-reference.md)** — той самий механізм через HTTP, якщо ви
  волієте не вбудовувати бібліотеку.
- **[ai-codes.txt](../../ai-codes.txt)** — простий перелік `(AI) НАЗВА` для швидкого пошуку.

### Версія в п'ять рядків

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
