# GaiaParser — Быстрый старт

Превратите полезную нагрузку штрихового кода GS1 в структурированные, проверенные и понятные человеку данные
примерно за десять минут. Это короткий путь; **[руководство разработчика GaiaParser](GaiaParser-Russian.md)** служит
полным справочником, а **[GaiaBuilder](GaiaBuilder-Russian.md)** описывает обратное направление
(построение строк элементов и URI Digital Link).

## Содержание

1. [Добавление Gaia в проект](#1-добавление-gaia-в-проект)
2. [Первый разбор](#2-первый-разбор)
3. [Чтение результата](#3-чтение-результата)
4. [Обработка неудачного разбора](#4-обработка-неудачного-разбора)
5. [Две вещи, на которых вы обожжётесь](#5-две-вещи-на-которых-вы-обожжётесь)
6. [Префиксы сканеров и Digital Link работают сразу](#6-префиксы-сканеров-и-digital-link-работают-сразу)
7. [Меньше работы: режимы разбора](#7-меньше-работы-режимы-разбора)
8. [Смена языка и формата даты](#8-смена-языка-и-формата-даты)
9. [Приведение неаккуратного ввода в порядок](#9-приведение-неаккуратного-ввода-в-порядок)
10. [Что дальше](#10-что-дальше)

---

## 1. Добавление Gaia в проект

Gaia не публикуется в Maven Central, поэтому соберите основной модуль один раз и установите его в свой
локальный репозиторий:

```bash
cd gaia && mvn install
```

Затем объявите зависимость:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Это весь перечень зависимостей, который вам нужно написать. Jar-файл лёгкий, поэтому единственная
зависимость Gaia с областью компиляции — `com.fasterxml.jackson.core:jackson-databind` — приходит
транзитивно; если ваша сборка уже фиксирует версию Jackson, побеждает она, и Gaia пользуется именно ею.
Gaia рассчитана на **Java 11**, и тот же jar-файл работает без изменений на любой более поздней JVM.

> Пропуск набора тестов основного модуля (`mvn install -DskipTests`) превращает несколько минут в несколько
> секунд, пока вы только осваиваетесь.

---

## 2. Первый разбор

Один класс, никакой настройки:

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

`parse(String)` выполняет **весь** конвейер: синтаксис, проверку содержимого и интерпретацию.
Это верное значение по умолчанию — сузьте его позже, если измерения дадут вам к тому повод.

---

## 3. Чтение результата

`ParseResult.getAiObject()` содержит распознанные AI. Обращайтесь к нужному по его коду, а не
по положению:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Каждый элемент несёт список **интерпретаций** — расшифрованный смысл, скрытый за необработанными цифрами
и полученный на этапе интерпретации:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` локализуется и предназначен для отображения. Чтобы *прочитать* значение в коде, находите его
лучше по ключу типа, который неизменен:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Разные AI дают разные ключи: GTIN возвращает свой префикс компании, тип GTIN и контрольную
цифру; цена — валюту и десятичную сумму. Полный перечень приведён в
[приложении B](GaiaParser-Russian.md#приложение-b--константы-ключей-интерпретации), а константы находятся
в `GS1Constants_Enricher`. Интерпретации есть не у каждого AI: из номера партии в произвольном тексте
вывести нечего, поэтому его список остаётся пустым.

---

## 4. Обработка неудачного разбора

Недействительная полезная нагрузка — обычный исход, а не исключение: `parse` никогда не выбрасывает исключений
из-за неверных данных GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Ветвитесь по `getId()`, а не по тексту сообщения.** Сообщения локализуются, и их формулировка
договором не является — к тому же сейчас они несут известный дефект кавычек (удвоенные `''` выше),
отмеченный в [справочнике ошибок](GaiaParser-Russian.md#справочник-ошибок).

Два разных вопроса — два разных метода:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Разбор перестаёт углубляться, как только этап даёт сбой, поэтому неверная контрольная цифра даёт вам
ошибки проверки, но никаких интерпретаций.

### Предупреждения не делают результат недействительным

Некоторые проверки носят справочный характер. О нераспознанном префиксе компании GS1 сообщается, но полезная нагрузка
остаётся структурно исправной:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Пользуйтесь `getIssues()`, когда нужны и те и другие. Если ваш процесс обязан отклонять неизвестные префиксы, проверяйте
`getWarnings()` явно — `isValid()` этого за вас не сделает.

---

## 5. Две вещи, на которых вы обожжётесь

### Разделитель GS и почему его пропуск хуже ошибки

AI переменной длины тянется до **знака GS** (ASCII `0x1D`, в символиках штриховых кодов
называемого FNC1) либо до конца строки. Когда за ним следует другой AI, этот разделитель
обязателен:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Пропустите его — и вы получите **не** ошибку, а уверенно поданный неверный ответ:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` имеет формат `X..20`, поэтому он вполне законно проглатывает `LOT-ABC21SN-98765`, и у анализатора нет
никакой возможности узнать, что задумывалось иное. Ниже по цепочке этого уже не исправить, поэтому позаботьтесь
о верном разделителе прямо в источнике: считывайте байты сканера как **ISO-8859-1**, чтобы `0x1D` уцелел, и пишите
`""` в строковых литералах Java. Идентификаторы AI постоянной длины (`01`, `17`, `3103`) в разделителе не нуждаются —
анализатор знает их длину.

### Большинство AI не может стоять само по себе

Партия, серийный номер, срок годности и им подобные — это *атрибуты*: GS1 General Specifications
требуют, чтобы они следовали вместе с ключом идентификации, и Gaia за этим следит.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Добавьте GTIN — и разбор пройдёт. Если вам действительно нужно разобрать фрагмент — модульный тест,
частичное считывание, — отключите эту проверку:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Префиксы сканеров и Digital Link работают сразу

Вам не нужно сообщать Gaia, какого вида ввод, — она распознаёт все четыре. Просто передайте ей
то, что выдал сканер.

**Префикс идентификатора символики AIM** определяет символику и удаляется автоматически:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**URI GS1 Digital Link** проходит ту же проверку и то же обогащение:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Поскольку обе формы попадают в один и тот же `GS1AIObject`, коду, потребляющему считанные данные, не нужно
задумываться, какая из них пришла, — а `toElementString()` / `getCanonicalDigitalLink()`
преобразуют одну в другую.

**Восьмизначный префикс корреляции** (`12345678~…`) также удаляется и сохраняется в
`getCorrelationInfo()`, если ваш конвейер им пользуется.

---

## 7. Меньше работы: режимы разбора

Значение по умолчанию делает всё. Просите меньше, когда нужна лишь часть ответа:

| Режим | Отвечает на вопрос | Стоимость |
|---|---|---|
| `DATA_CARRIER` | Что это за символика? | Самая низкая — разбора AI нет вовсе, `getAiObject()` равно `null` |
| `SYNTAX` | Правильно ли построены коды AI и длины? | Без контрольных цифр и без интерпретаций |
| `CONTENT` | Действительны ли эти данные GS1? | Полная проверка, без интерпретаций |
| `INTERPRETATION` | Что это означает? | **По умолчанию** — всё |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Прибегайте к `CONTENT`, когда проверяете большие объёмы и никогда не показываете разбор, и к
`DATA_CARRIER`, когда нужно лишь направить считанные данные нужному обработчику.

---

## 8. Смена языка и формата даты

Сообщения об ошибках, метки интерпретации и описания AI переведены на **35 языков**;
даты отображаются так, как вам угодно. Всё это умещается в один неизменяемый `ParseConfig`:

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

Значения не локализуются никогда — локализуются только метки, описания и сообщения, — поэтому `"2026-12-31"` и
`"09506000134352"` означают одно и то же на любом языке. Постройте конфигурацию один раз при запуске
и используйте её совместно; она неизменяема.

---

## 9. Приведение неаккуратного ввода в порядок

Если ваш источник выдаёт напечатанные скобки HRI или случайные пробелы, в основном модуле есть два
**модификатора ввода**, которые исправляют полезную нагрузку до разбора:

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

По умолчанию не включено ничего, и у обоих есть оговорки: пробел и скобки — допустимые знаки
данных GS1, поэтому применяйте их только к источнику, который вам знаком. См.
[Встроенные модификаторы](GaiaParser-Russian.md#встроенные-модификаторы), где объясняется и то, почему удаление
скобок обязано восстановить разделитель, который они подразумевали.

---

## 10. Что дальше

- **[Руководство разработчика GaiaParser](GaiaParser-Russian.md)** — конвейер обработки в подробностях, полная модель
  результата, все коды ошибок, а также приложения об AI и ключах интерпретации.
- **[Руководство разработчика GaiaBuilder](GaiaBuilder-Russian.md)** — построение строк элементов и URI Digital Link
  из пар AI/значение.
- **[Справочник HTTP API Gaia](../../gaia-api-reference.md)** — тот же механизм по HTTP, если вы
  предпочитаете не встраивать библиотеку.
- **[ai-codes.txt](../../ai-codes.txt)** — простой перечень `(AI) НАЗВАНИЕ` для быстрого поиска.

### Версия в пять строк

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
