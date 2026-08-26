# GaiaParser — Schnelleinstieg

Verwandeln Sie die Nutzdaten eines GS1-Strichcodes in etwa zehn Minuten in strukturierte, validierte,
menschenlesbare Daten. Dies ist der kurze Weg; das **[GaiaParser-Entwicklerhandbuch](GaiaParser-German.md)** ist die
vollständige Referenz, und **[GaiaBuilder](GaiaBuilder-German.md)** behandelt die Gegenrichtung
(das Erzeugen von Elementketten und Digital-Link-URIs).

## Inhalt

1. [Gaia zu Ihrem Projekt hinzufügen](#1-gaia-zu-ihrem-projekt-hinzufügen)
2. [Etwas parsen](#2-etwas-parsen)
3. [Das Ergebnis lesen](#3-das-ergebnis-lesen)
4. [Ein fehlgeschlagenes Parsen behandeln](#4-ein-fehlgeschlagenes-parsen-behandeln)
5. [Zwei Dinge, die Ihnen zum Verhängnis werden](#5-zwei-dinge-die-ihnen-zum-verhängnis-werden)
6. [Scanner-Präfixe und Digital Links funktionieren einfach](#6-scanner-präfixe-und-digital-links-funktionieren-einfach)
7. [Weniger Arbeit leisten: Parse-Modi](#7-weniger-arbeit-leisten-parse-modi)
8. [Sprache und Datumsformat ändern](#8-sprache-und-datumsformat-ändern)
9. [Unsaubere Eingaben bereinigen](#9-unsaubere-eingaben-bereinigen)
10. [Wie es weitergeht](#10-wie-es-weitergeht)

---

## 1. Gaia zu Ihrem Projekt hinzufügen

Gaia wird nicht auf Maven Central veröffentlicht; bauen Sie den Kern daher einmal und installieren Sie ihn in Ihr
lokales Repository:

```bash
cd gaia && mvn install
```

Binden Sie ihn anschließend ein:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Das ist die gesamte Abhängigkeitsliste, die Sie schreiben müssen. Das JAR ist schlank, sodass Gaias einzige
Abhängigkeit mit Compile-Scope — `com.fasterxml.jackson.core:jackson-databind` — transitiv
mitkommt; legt Ihr Build bereits eine Jackson-Version fest, gewinnt diese, und Gaia verwendet sie.
Gaia zielt auf **Java 11**, und dasselbe JAR läuft unverändert auf jeder späteren JVM.

> Die Testsuite des Kerns zu überspringen (`mvn install -DskipTests`) macht aus einigen Minuten wenige
> Sekunden, solange Sie sich einarbeiten.

---

## 2. Etwas parsen

Eine Klasse, keine Konfiguration:

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

`parse(String)` durchläuft die **vollständige** Kette: Syntax, Inhaltsvalidierung und Interpretation.
Das ist der richtige Standard — schränken Sie ihn später ein, wenn Sie einen messbaren Grund dafür haben.

---

## 3. Das Ergebnis lesen

`ParseResult.getAiObject()` enthält die aufgelösten AI. Greifen Sie auf ein bestimmtes über seinen Code zu,
nicht über die Position:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Jedes Element trägt eine Liste von **Interpretationen** — die decodierte Bedeutung hinter den rohen Ziffern,
erzeugt von der Interpretationsstufe:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` ist lokalisiert und für die Anzeige gedacht. Um einen Wert im Code zu *lesen*, schlagen Sie ihn
stattdessen über seinen stabilen Typschlüssel nach:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Verschiedene AI erzeugen verschiedene Schlüssel — eine GTIN liefert ihre Basisnummer, ihren GTIN-Typ und ihre
Prüfziffer; ein Preis liefert Währung und Dezimalbetrag. Die vollständige Liste steht in
[Anhang B](GaiaParser-German.md#anhang-b--konstanten-der-interpretationsschlüssel), und die Konstanten liegen
in `GS1Constants_Enricher`. Nicht jedes AI hat Interpretationen: Eine Freitext-Charge hat
nichts abzuleiten, ihre Liste bleibt also leer.

---

## 4. Ein fehlgeschlagenes Parsen behandeln

Ungültige Nutzdaten sind ein normales Ergebnis, keine Ausnahme — `parse` wirft bei fehlerhaften
GS1-Daten nie:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Verzweigen Sie über `getId()`, nie über die Meldung.** Meldungen sind lokalisiert, ihr Wortlaut ist
kein Vertrag — und sie tragen derzeit einen bekannten Mangel bei den Anführungszeichen (das verdoppelte `''` oben),
vermerkt in der [Fehlerreferenz](GaiaParser-German.md#fehlerreferenz).

Zwei verschiedene Fragen, zwei verschiedene Methoden:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Ein Parse-Vorgang steigt nicht weiter ab, sobald eine Stufe scheitert; eine falsche Prüfziffer liefert Ihnen
also Validierungsfehler, aber keine Interpretationen.

### Warnungen machen ein Ergebnis nicht ungültig

Manche Prüfungen sind reine Hinweise. Eine unbekannte GS1-Basisnummer wird gemeldet, die Nutzdaten sind
aber weiterhin strukturell einwandfrei:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Verwenden Sie `getIssues()`, wenn Sie beides wollen. Muss Ihr Ablauf unbekannte Basisnummern zurückweisen, prüfen Sie
`getWarnings()` ausdrücklich — `isValid()` nimmt Ihnen das nicht ab.

---

## 5. Zwei Dinge, die Ihnen zum Verhängnis werden

### Das GS-Trennzeichen, und warum es wegzulassen schlimmer ist als ein Fehler

Ein AI mit variabler Länge reicht bis zu einem **GS-Zeichen** (ASCII `0x1D`, in Strichcode-Symbologien
FNC1 genannt) oder bis zum Ende der Zeichenkette. Folgt ihm ein weiteres AI, ist dieses Trennzeichen
zwingend:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Lassen Sie es weg, erhalten Sie **keinen** Fehler — Sie erhalten eine selbstbewusst falsche Antwort:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` ist `X..20`, verschluckt also berechtigterweise `LOT-ABC21SN-98765`, und der Parser hat keine
Möglichkeit zu erkennen, dass das nicht beabsichtigt war. Nachgelagert lässt sich das nicht mehr retten; setzen Sie das
Trennzeichen daher schon an der Quelle richtig: Lesen Sie Scanner-Bytes als **ISO-8859-1**, damit `0x1D` überlebt, und schreiben Sie
`""` in Java-Zeichenkettenliteralen. AI mit fester Länge (`01`, `17`, `3103`) brauchen kein Trennzeichen —
der Parser kennt ihre Länge.

### Die meisten AI können nicht allein stehen

Charge, Seriennummer, Verfallsdatum und Verwandte sind *Attribute*: Die GS1 General Specifications
verlangen, dass sie zusammen mit einem Identifikationsschlüssel auftreten, und Gaia setzt das durch.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Fügen Sie die GTIN hinzu, und es geht durch. Müssen Sie tatsächlich ein Fragment parsen — einen Unit-Test, einen
Teilscan —, schalten Sie die Prüfung ab:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Scanner-Präfixe und Digital Links funktionieren einfach

Sie müssen Gaia nicht mitteilen, welche Gestalt die Eingabe hat — es erkennt alle vier Formen. Geben Sie ihm
schlicht das, was der Scanner geliefert hat.

**Ein AIM-Symbologiekennungspräfix** bestimmt die Symbologie und wird automatisch entfernt:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Ein GS1-Digital-Link-URI** durchläuft dieselbe Validierung und Anreicherung:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Da beide Formen im selben `GS1AIObject` landen, muss der Code, der einen Scan verarbeitet, nicht wissen,
welche davon eingetroffen ist — und `toElementString()` / `getCanonicalDigitalLink()`
wandeln zwischen ihnen um.

Ein **8-stelliges Korrelationspräfix** (`12345678~…`) wird ebenfalls entfernt und in
`getCorrelationInfo()` aufbewahrt, falls Ihre Verarbeitungskette eines verwendet.

---

## 7. Weniger Arbeit leisten: Parse-Modi

Der Standard erledigt alles. Verlangen Sie weniger, wenn Sie nur einen Teil der Antwort brauchen:

| Modus | Beantwortet | Aufwand |
|---|---|---|
| `DATA_CARRIER` | Welche Symbologie ist das? | Am günstigsten — gar kein AI-Parsen, `getAiObject()` ist `null` |
| `SYNTAX` | Sind die AI-Codes und Längen wohlgeformt? | Keine Prüfziffern, keine Interpretationen |
| `CONTENT` | Sind das gültige GS1-Daten? | Vollständige Validierung, keine Interpretationen |
| `INTERPRETATION` | Was bedeutet das? | **Standard** — alles |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Greifen Sie zu `CONTENT`, wenn Sie in großen Mengen validieren und die Aufschlüsselung nie anzeigen, und zu
`DATA_CARRIER`, wenn Sie einen Scan lediglich an den richtigen Handler weiterleiten müssen.

---

## 8. Sprache und Datumsformat ändern

Fehlermeldungen, Interpretationsbeschriftungen und AI-Beschreibungen sind in **35 Sprachen**
übersetzt; Datumsangaben werden so dargestellt, wie Sie es wünschen. All das steckt in einer unveränderlichen `ParseConfig`:

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

Werte werden nie lokalisiert — nur Beschriftungen, Beschreibungen und Meldungen —, sodass `"2026-12-31"` und
`"09506000134352"` in jeder Sprache dasselbe bedeuten. Bauen Sie die Konfiguration einmal beim Start
und teilen Sie sie; sie ist unveränderlich.

---

## 9. Unsaubere Eingaben bereinigen

Gibt Ihre Quelle gedruckte HRI-Klammern oder versprengte Leerzeichen aus, so liefert der Kern zwei
**Eingabemodifikatoren** mit, die die Nutzdaten vor dem Parsen reparieren:

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

Standardmäßig ist nichts aktiviert, und beide haben Vorbehalte — Leerzeichen und Klammern sind zulässige
GS1-Datenzeichen; wenden Sie sie also nur auf eine Quelle an, die Sie kennen. Siehe
[Eingebaute Modifikatoren](GaiaParser-German.md#eingebaute-modifikatoren); dort steht auch, warum das Entfernen der Klammern
das von ihnen implizierte Trennzeichen wiederherstellen muss.

---

## 10. Wie es weitergeht

- **[GaiaParser-Entwicklerhandbuch](GaiaParser-German.md)** — die Verarbeitungskette im Detail, das vollständige
  Ergebnismodell, sämtliche Fehlercodes sowie die Anhänge zu AI und Interpretationsschlüsseln.
- **[GaiaBuilder-Entwicklerhandbuch](GaiaBuilder-German.md)** — Elementketten und Digital-Link-URIs aus
  AI/Wert-Paaren bauen.
- **[Gaia-API-HTTP-Referenz](../../gaia-api-reference.md)** — dieselbe Maschinerie über HTTP, falls Sie
  die Bibliothek lieber nicht einbetten möchten.
- **[ai-codes.txt](../../ai-codes.txt)** — eine flache Liste `(AI) TITEL` zum schnellen Nachschlagen.

### Die Fünf-Zeilen-Fassung

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
