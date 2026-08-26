# GAIA (GS1 Application Identifiers Analyser) — Entwicklerhandbuch

## Inhaltsverzeichnis

1. [Überblick](#überblick)
2. [Über GS1 und die General Specifications](#über-gs1-und-die-general-specifications)
3. [GS1-Anwendungskennzeichen](#gs1-anwendungskennzeichen)
4. [Schnelleinstieg](#schnelleinstieg)
5. [Verarbeitungskette des Parsers](#verarbeitungskette-des-parsers)
   - [Vorstufe — Eingabemodifikatoren](#vorstufe--eingabemodifikatoren)
   - [Stufe 0 — Korrelations-ID](#stufe-0--korrelations-id)
   - [Stufe 1 — Eingabeweiterleitung](#stufe-1--eingabeweiterleitung)
   - [Stufe 2 — Syntax](#stufe-2--syntax)
   - [Stufe 3 — Inhalt](#stufe-3--inhalt)
   - [Stufe 4 — Interpretation](#stufe-4--interpretation)
6. [Parser-Konfiguration (`ParseConfig`)](#parser-konfiguration-parseconfig)
   - [Optionen](#optionen)
   - [Lokalisierte Meldungen und Beschriftungen](#lokalisierte-meldungen-und-beschriftungen)
   - [Datumsformatierung](#datumsformatierung)
7. [Eingabemodifikatoren](#eingabemodifikatoren)
   - [Eingebaute Modifikatoren](#eingebaute-modifikatoren)
   - [Einen Modifikator schreiben](#einen-modifikator-schreiben)
   - [Modifikatoren registrieren](#modifikatoren-registrieren)
   - [Nachvollziehen, was ein Modifikator getan hat](#nachvollziehen-was-ein-modifikator-getan-hat)
   - [Fehlerbehandlung bei Modifikatoren](#fehlerbehandlung-bei-modifikatoren)
8. [Parse-Modi](#parse-modi)
   - [Modus DATA_CARRIER](#modus-data_carrier)
   - [Modus SYNTAX](#modus-syntax)
   - [Modus CONTENT](#modus-content)
   - [Modus INTERPRETATION (Standard)](#modus-interpretation-standard)
9. [Korrelations-ID](#korrelations-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Mit Ergebnissen arbeiten](#mit-ergebnissen-arbeiten)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry und DataCarrierType](#datacarrierentry-und-datacarriertype)
12. [Fehlerreferenz](#fehlerreferenz)
13. [Thread-Sicherheit](#thread-sicherheit)
14. [Anhang A — AI-Zeichenkettenkonstanten](#anhang-a--ai-zeichenkettenkonstanten)
    - [Identifikation und Serialisierung](#identifikation-und-serialisierung)
    - [Datum und Uhrzeit](#datum-und-uhrzeit)
    - [Menge und Maß — variables Maß (metrisch)](#menge-und-maß--variables-maß-metrisch)
    - [Menge und Maß — variables Maß (imperial / USA)](#menge-und-maß--variables-maß-imperial--usa)
    - [Preise und Geldbeträge](#preise-und-geldbeträge)
    - [Ort und Versand](#ort-und-versand)
    - [Produktmerkmale und Rückverfolgbarkeit](#produktmerkmale-und-rückverfolgbarkeit)
    - [Nationale Erstattungsnummern im Gesundheitswesen (NHRN)](#nationale-erstattungsnummern-im-gesundheitswesen-nhrn)
    - [Gesundheitswesen, GMN, HIDRI, CPID, Personendaten](#gesundheitswesen-gmn-hidri-cpid-personendaten)
    - [Interne / betriebliche Verwendung](#interne--betriebliche-verwendung)
15. [Anhang B — Konstanten der Interpretationsschlüssel](#anhang-b--konstanten-der-interpretationsschlüssel)
    - [Datum und Uhrzeit](#datum-und-uhrzeit)
    - [Erntedatum](#erntedatum)
    - [GS1-Basisnummer](#gs1-basisnummer)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Land (ISO 3166)](#land-iso-3166)
    - [Währung (ISO 4217)](#währung-iso-4217)
    - [Temperatur](#temperatur)
    - [Geschlecht (ISO 5218)](#geschlecht-iso-5218)
    - [Wasserlebewesen (FAO)](#wasserlebewesen-fao)
    - [NATO-Versorgungsnummer (NSN)](#nato-versorgungsnummer-nsn)
    - [Rollenware](#rollenware)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM-Kennungen (EID / ICCID)](#sim-kennungen-eid--iccid)
    - [Zertifizierungsreferenz](#zertifizierungsreferenz)
    - [GS1 UIC](#gs1-uic)
    - [Geburtenreihenfolge des Neugeborenen](#geburtenreihenfolge-des-neugeborenen)
    - [Globale Modellnummer (GMN)](#globale-modellnummer-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Dezimal- und Maßwerte](#dezimal--und-maßwerte)
    - [Geokoordinaten](#geokoordinaten)
    - [Produktionsmethode](#produktionsmethode)
    - [AIDC-Medientyp](#aidc-medientyp)
    - [Stück von Gesamtzahl](#stück-von-gesamtzahl)
    - [Komponentenzerlegungen](#komponentenzerlegungen)
    - [Verschiedenes](#verschiedenes)

---

## Überblick

`GaiaParser` ist der Einstiegspunkt zum Parsen von Elementketten mit GS1-Anwendungskennzeichen (AI). Er nimmt die Rohausgabe eines Scanners in einer der folgenden Formen entgegen und liefert ein strukturiertes `ParseResult` mit allen aufgelösten AI, den Validierungsfehlern und – optional – den menschenlesbaren Interpretationen:

- Einfache AI-Elementkette: `0109506000134352`
- Elementkette mit AIM-Symbologiekennung als Präfix: `]C10109506000134352`
- GS1-Digital-Link-URI: `https://example.com/01/09506000134352`
- Jede der obigen Formen, optional mit einer 8-stelligen Korrelations-ID als Präfix: `12345678~0109506000134352`

**Einstiegsklasse:** `tools.pantheum.gaia.GaiaParser`

> **Neu bei Gaia?** Beginnen Sie mit dem **[GaiaParser-Schnelleinstieg](GaiaParser-QuickStart-German.md)** — Abhängigkeit, erster Parse-Vorgang und die wenigen typischen Stolperfallen, in etwa zehn Minuten. Dieses Handbuch ist die vollständige Referenz.

> Für die umgekehrte Richtung — das *Erzeugen* wohlgeformter Elementketten und Digital-Link-URIs aus AI/Wert-Paaren — siehe das **[GaiaBuilder — Entwicklerhandbuch](GaiaBuilder-German.md)**.

---

## Über GS1 und die General Specifications

**GS1** ist eine weltweit tätige gemeinnützige Organisation, die offene Standards für die Identifikation und den Datenaustausch in Lieferketten entwickelt und pflegt. Ihre Standards werden im Handel, im Gesundheitswesen, in der Logistik, in der Gastronomie und in vielen weiteren Branchen eingesetzt — von Produktstrichcodes auf Verbraucherverpackungen bis zur serialisierten Rückverfolgung pharmazeutischer Dosen.

Die maßgebliche Referenz für alles, was dieser Parser umsetzt, sind die **GS1 General Specifications** — ein einziges Dokument, das Folgendes festlegt:

- Alle Codes der Anwendungskennzeichen (AI), ihre Datentitel, Formate und Validierungsregeln
- Die Syntaxregeln zum Bilden und Codieren von AI-Elementketten
- Die Anforderungen an Strichcode-Symbologien und die Vergabe der AIM-Symbologiekennungen
- Die Algorithmen für Prüfziffern und Prüfzeichen
- Die Auflösung zweistelliger Jahreszahlen (die Schiebefensterregel)
- Die Spezifikationen für Data Matrix, QR Code, GS1-128, GS1 DataBar und weitere Datenträger

Die GS1 General Specifications werden jährlich aktualisiert. Die aktuelle Ausgabe und begleitende Materialien sind verfügbar unter:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA setzt **Release 26.0 (ratifiziert im Januar 2026)** der GS1 General Specifications um.

GS1-Digital-Link-URIs unterliegen einem begleitenden Standard, **GS1 Digital Link: URI Syntax**, der die primären Identifikationsschlüssel, die Reihenfolge der Schlüsselqualifizierer und die Codierung der Datenattribute festlegt, die der Parser auf Digital-Link-Eingaben anwendet:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA setzt **Release 1.7.0 (ratifiziert im August 2026)** des Standards GS1 Digital Link: URI Syntax um.

Abschnittsverweise in diesem Dokument beziehen sich auf die GS1 General Specifications (z. B. „Table 7-5“, „section 7.12“), mit Ausnahme der Digital-Link-Abschnittsnummern (z. B. „§4.9“, „§4.12“), die sich auf den Standard GS1 Digital Link: URI Syntax beziehen.

---

## GS1-Anwendungskennzeichen

Ein **GS1-Anwendungskennzeichen (AI)** ist ein kurzes numerisches Präfix — zwei bis vier Ziffern —, das Bedeutung und Format der unmittelbar folgenden Daten festlegt. AI sind in den GS1 General Specifications definiert und decken ein breites Spektrum an Lieferkettendaten ab: Produktkennungen, Datumsangaben, Mengen, Chargennummern, Seriennummern, Maßangaben, URLs und vieles mehr.

### Aufbau eines AI-Elements

Jedes AI-Element besteht aus zwei Teilen:

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

Der AI-Code ist stets numerisch. Der Datenwert folgt unmittelbar darauf, ohne Trennzeichen zwischen Code und Wert.

### AI mit fester und mit variabler Länge

AI fallen in zwei Kategorien:

| Typ | Verhalten | Beispiel |
|---|---|---|
| **Feste Länge** | Exakte Zeichenanzahl, wird stets vollständig verbraucht | AI `01` (GTIN) — immer 14 Ziffern |
| **Variable Länge** | Von 1 Zeichen bis zu einem Höchstwert; endet mit einem GS-Trennzeichen oder dem Ende der Eingabe | AI `10` (Charge) — 1 bis 20 alphanumerische Zeichen |

Ob ein AI fest oder variabel ist, ergibt sich ausschließlich aus seiner Definition in der GS1-Spezifikation — der Parser rät nie.

### Elementketten mit mehreren AI

Mehrere AI lassen sich zu einer einzigen Elementkette verketten. AI mit fester Länge können direkt aneinandergereiht werden, weil der Parser stets genau weiß, wie viele Zeichen zu verbrauchen sind. AI mit variabler Länge müssen mit dem **GS-Zeichen** (ASCII `0x1D`, in Strichcode-Symbologien auch FNC1 genannt) abgeschlossen werden, sobald ein weiteres AI folgt, damit der Parser erkennt, wo ein Wert endet und der nächste AI-Code beginnt.

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

In Java-Zeichenkettenliteralen schreiben Sie das GS-Zeichen als Unicode-Escape `""`.

### Häufige AI

| AI | Datentitel | Format | Beispielwert |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (JJMMTT) | `261231` |
| `17` | USE BY or EXPIRY | N6 (JJMMTT) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, einheitlicher Währungsraum) |
| `710` | NHRN PZN | X..20 | `12345678` |

> Die **vierte Ziffer** eines vierstelligen Maß- oder Preis-AI codiert die Anzahl der implizierten Nachkommastellen: `3103` ist das Nettogewicht in kg mit 3 Nachkommastellen (`001500` = 1,500 kg), während `3102` dieselben Ziffern als 15,00 kg läse. Die Spalte `Format` oben zeigt das Format der *Daten*; das vollständige `getFormatString()` eines AI schließt das AI selbst ein (z. B. `N4+N6` für `3103`).

### Menschenlesbare Interpretation (HRI)

Die übliche menschenlesbare Form setzt jeden AI-Code unmittelbar vor seinem Wert in Klammern, mit einem Leerzeichen zwischen den Elementen:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Das GS-Trennzeichen erscheint in der HRI nicht. `GS1AIObject.toHriString()` erzeugt dieses Format.

### Vierstellige AI-Codes

Manche AI verwenden vier statt zwei Ziffern. Die ersten beiden Ziffern bezeichnen die AI-Familie; die dritte und/oder vierte tragen zusätzliche Semantik (etwa die Position des implizierten Dezimaltrennzeichens bei Maß-AI). Der Parser löst den vollständigen AI-Code automatisch aus der Elementkette auf — Aufrufer arbeiten stets mit dem vollständigen Code (z. B. `"3102"`, nicht nur `"31"`).

---

## Schnelleinstieg

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

> **GS-Trennzeichen:** Innerhalb einer Kette mit mehreren AI müssen AI mit variabler Länge durch das GS-Zeichen (ASCII `0x1D`) abgegrenzt werden. Verwenden Sie in Java-Zeichenkettenliteralen `""`.

---

## Verarbeitungskette des Parsers

### Vorstufe — Eingabemodifikatoren

Enthält die `ParseConfig` **Eingabemodifikatoren**, so laufen diese vor allem anderen: vor dem Entfernen der Korrelations-ID, vor der Datenträgererkennung und vor dem Eintritt in die GS1-Kette. Jeder Modifikator schreibt die Roheingabe für den nächsten um, und alle nachfolgenden Stufen arbeiten auf dem Ergebnis der Kette.

Standardmäßig ist kein Modifikator konfiguriert; diese Vorstufe bleibt also wirkungslos, solange Sie sie nicht ausdrücklich aktivieren. Siehe [Eingabemodifikatoren](#eingabemodifikatoren).

---

### Stufe 0 — Korrelations-ID

Vor jeder GS1-Verarbeitung prüft `GaiaParser`, ob die Eingabe mit einem optionalen **Korrelations-ID-Präfix** beginnt: genau 8 dezimale ASCII-Ziffern, gefolgt von einer Tilde (`~`), z. B. `12345678~`.

Ist das Präfix vorhanden, wird es entfernt und als `CorrelationInfo` im zurückgegebenen `ParseResult` abgelegt. Alle weiteren Stufen arbeiten auf den so bereinigten Nutzdaten. Fehlt das Präfix, wird die Eingabe unverändert weitergereicht.

Einzelheiten siehe [Korrelations-ID](#korrelations-id).

---

### Stufe 1 — Eingabeweiterleitung

Nach dem Entfernen der Korrelation prüft `GaiaParser`, ob die (bereinigte) Eingabe mit einer **AIM-Symbologiekennung** beginnt: einem dreistelligen Präfix der Form `]` + ASCII-Buchstabe + ASCII-Ziffer (z. B. `]C1` für GS1-128, `]d2` für GS1 DataMatrix, `]e0` für GS1 DataBar / GS1 Composite).

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

Ist der Datenträger nicht GS1-AI-fähig (z. B. ein Post-Strichcode), bricht das Parsen sofort mit einem Fehler `GE-D002` ab.

---

### Stufe 2 — Syntax

Läuft immer. Sie besteht aus zwei Teilschritten:

**2a. Tokenisierung (`AISyntaxParser`)**
- Liest die Länge des AI-Codes aus den ersten beiden Zeichen anhand der GS1-Präfixtabelle (GS1 General Specifications, Tabelle 7-5).
- AI mit fester Länge verbrauchen eine exakte Byteanzahl aus der Eingabe.
- AI mit variabler Länge werden bis zu einem GS-Zeichen oder bis zum Ende der Eingabe gelesen.
- Bei AI mit mehreren Komponenten wird der Wertblock in Segmente je Komponente zerlegt.

**2b. Strukturvalidierung (`SyntaxValidator`)**
- Prüft auf doppelte AI (`GE-S004`).
- Prüft erforderliche AI-Abhängigkeiten, etwa AI `02`, das AI `37` voraussetzt (`GE-S005`).
- Prüft ausgeschlossene AI-Kombinationen (`GE-S006`).

Fehler dieser Stufe haben die Stufe `SYNTAX_ERROR` (Tokenisierung) oder `INTEGRITY_ERROR` (Struktur). Liegt **irgendein** Fehler vor — aus Tokenisierung oder Struktur —, bricht die Kette ab, und die Inhalts- und Interpretationsstufen entfallen.

---

### Stufe 3 — Inhalt

Läuft nur, wenn Stufe 2 keine Fehler erzeugt hat (weder aus der Tokenisierung noch aus der Struktur). Kette je Element (jeder Schritt läuft nur, wenn der vorherige fehlerfrei blieb):

| Schritt | Validator | Fehlercodes |
|---|---|---|
| Prüfung per regulärem Ausdruck | `RegexValidator` | `GE-C001` |
| Zeichensatz und Format der Komponenten | `ComponentValidator` | `GE-C005` + Formatcodes je Bedingung (`GE-C054`–`GE-C115`) |
| Prüfziffer / Prüfzeichen | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Eigene semantische Validierung | `ContentValidatorRegistry` | Inhaltscodes je Bedingung (`GE-C116`–`GE-C170`) |

Fehler dieser Stufe haben die Stufe `FORMAT_ERROR` oder `DATA_ERROR`, mit einer Ausnahme: die
Prüfungen des GS1-Basisnummernpräfixes bei den AI mit GS1-Schlüssel sind lediglich Hinweise und tragen die Stufe `WARNING` (siehe
[Fehlerreferenz](#fehlerreferenz)); ein nicht erkanntes Basisnummernpräfix macht das Ergebnis
also für sich genommen nicht ungültig.

---

### Stufe 4 — Interpretation

Läuft nur im Modus `INTERPRETATION` und nur, wenn kein Element einen Fehler aus einer früheren Stufe trägt. Die `InterpretationEngine` reichert jedes Element mit beschrifteten Metadaten an:

- Datumsangaben, neu formatiert als `tt/mm/jjjj`
- Zerlegung der GTIN-Prüfziffer und Nachschlagen des GS1-Basisnummernpräfixes
- Ländernamen nach ISO 3166
- Währungsnamen und -symbole nach ISO 4217
- Decodierte Dezimalbeträge
- HRI-Fragmente (menschenlesbare Interpretation)

Die Ergebnisse werden als `GS1AIInterpretation`-Einträge an jedes `GS1AIObjectElement` angehängt.

---

## Parser-Konfiguration (`ParseConfig`)

`GaiaParser` bietet genau zwei Einstiegspunkte:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` läuft mit der **Standardkonfiguration**: Modus `INTERPRETATION`, aufsteigende Datumsreihenfolge (`tt/mm/jjjj`) mit `/` als Trennzeichen und vierstelliger Jahreszahl sowie **englischen** Fehlermeldungen. Um daran etwas zu ändern — einschließlich des Parse-Modus —, bauen Sie eine `ParseConfig` mit ihrem Fluent-Builder und verwenden die zweiargumentige Überladung.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Sämtliche Options-Enums liegen in `GaiaConstants`.

### Optionen

| Builder-Methode | Enum (`GaiaConstants`) | Standard | Wirkung |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Tiefe der Kette — siehe [Parse-Modi](#parse-modi). |
| `language(...)`      | `Language`      | `ENGLISH`        | Sprache der Fehlermeldungen, der Interpretationsbeschriftungen **und** der AI-Beschreibungen. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Reihenfolge der Datumsbestandteile: `LITTLE` (`tt/mm/jjjj`), `MIDDLE` (`mm/tt/jjjj`), `BIG` (`jjjj/mm/tt`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Zeichen zwischen den Datumsbestandteilen: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) oder `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) oder `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Überspringt die strukturelle „erfordert“-Prüfung (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Überspringt die strukturelle „schließt aus“-Prüfung (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / Klassenname | keiner | Code, der die Roheingabe vor dem Parsen umschreibt — zwei [eingebaute Modifikatoren](#eingebaute-modifikatoren) sowie alles, was Sie selbst schreiben. Siehe [Eingabemodifikatoren](#eingabemodifikatoren). |

Die vier Datumsoptionen wirken sich nur auf die formatierten Datumszeichenketten aus, die die Interpretations-Anreicherer erzeugen (im Modus `INTERPRETATION`); die Validierung ändern sie nicht. Builder-Werte dürfen entfallen — jede nicht gesetzte Option (oder eine, der `null` übergeben wird) behält ihren Standardwert.

### Lokalisierte Meldungen und Beschriftungen

`language(...)` wählt die Sprache für **drei** Arten menschenlesbaren Texts: Fehlermeldungen, Interpretationsbeschriftungen (das `getLabel()` jeder `GS1AIInterpretation`) und AI-Beschreibungen (das `getDescription()` jedes `GS1AIObjectElement`).

`GaiaConstants.Language` definiert **35 Sprachen**, die die meistgesprochenen Sprachen der Welt abdecken: Englisch, Französisch, Spanisch, Deutsch, Italienisch, Portugiesisch, Niederländisch, Polnisch, Russisch, Ukrainisch, Tschechisch, Schwedisch, Chinesisch, Japanisch, Koreanisch, Arabisch, Indonesisch, Hindi, Türkisch, Bengalisch, Urdu, Vietnamesisch, Nigerianisches Pidgin, Ägyptisches Arabisch, Marathi, Telugu, Tamil, Kantonesisch, Wu, Tagalog, Persisch, Hausa, Punjabi, Javanisch und Suaheli.

Stand der Übersetzungen (im Auslieferungszustand):
- **Interpretationsbeschriftungen** — in alle Sprachen übersetzt.
- **Fehlermeldungen** — in alle Sprachen übersetzt.
- **AI-Beschreibungen** — in alle Sprachen außer Englisch übersetzt. Englisch bildet keinen eigenen Katalog: Es wird unmittelbar aus dem Feld `description` des AI-Eintrags in `gs1-application-identifiers.jsonld` gelesen, auf das jede AI-Beschreibung letztlich zurückfällt.

Nigerianisches Pidgin (`NIGERIAN_PIDGIN`), eine auf dem Englischen beruhende Kreolsprache, verwendet für Interpretationsbeschriftungen und Fehlermeldungen bewusst den englischen Text weiter. Die AI-Beschreibungen sind die Ausnahme von dieser Ausnahme: Sie sind in echtes Pidgin übersetzt statt das Englische zu übernehmen, da die AI-Beschreibungskataloge unabhängig von den Beschriftungs- und Meldungskatalogen entstanden sind. Maschinelle Übersetzungen sollten von Muttersprachlern geprüft werden, bevor man sich im Produktivbetrieb auf sie verlässt.

Jede Meldung, Beschriftung oder Beschreibung, die im Katalog einer Sprache fehlt, fällt auf das Englische zurück. Rechtsläufig geschriebene Sprachen (Arabisch, Urdu, Ägyptisches Arabisch, Persisch) sind als Zeichenketten korrekt gespeichert; ihre Darstellung von rechts nach links obliegt der Anzeigeschicht.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Interpretationsbeschriftungen werden auf dieselbe Weise lokalisiert (die Werte bleiben unverändert — nur die Beschriftungen):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI-Beschreibungen werden auf dieselbe Weise lokalisiert (nur `getTitle()`, z. B. `"GTIN"`, wird nicht lokalisiert):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Datumsformatierung

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Eingabemodifikatoren

Ein **Eingabemodifikator** ist Code, der die rohe Eingabezeichenkette umschreibt, bevor Gaia sie parst. Modifikatoren gibt es für Eingaben, die bereits verstümmelt eintreffen — ein Scanner, der das GS-Trennzeichen durch einen druckbaren Platzhalter ersetzt, eine Middleware, die die Nutzdaten in ein herstellerspezifisches Präfix hüllt, ein Hostsystem, das alles in Großbuchstaben wandelt. Statt jede Zeichenkette an jeder Aufrufstelle vorzuverarbeiten (und es an einer davon subtil falsch zu machen), hinterlegen Sie die Normalisierung einmal in der `ParseConfig` und lassen den Parser sie anwenden.

Modifikatoren laufen ganz am Anfang von `GaiaParser.parse(...)` — vor dem Entfernen der Korrelations-ID, vor der Erkennung der AIM-Symbologiekennung und vor der GS1-Kette. Alles Nachgelagerte sieht nur noch die umgeschriebene Zeichenkette. **Standardmäßig ist nichts konfiguriert**, auch nicht die beiden [eingebauten Modifikatoren](#eingebaute-modifikatoren) — Sie aktivieren sie ausdrücklich je `ParseConfig`.

**Schnittstelle:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Eingebaute Modifikatoren

Im Kern-JAR sind zwei Modifikatoren enthalten, in `tools.pantheum.gaia.modifier.custom`. Sie decken die beiden häufigsten Arten ab, in denen GS1-Nutzdaten verstümmelt eintreffen — gedruckte HRI-Klammern, die als Daten behandelt werden, und überzählige Leerzeichen —, sodass die geläufigen Fälle ohne eigene Klasse auskommen:

| Klasse | `getName()` | Wirkung |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Entfernt die HRI-Klammern um jedes AI (`(01)…(10)…`) und stellt das darin implizierte FNC1-Trennzeichen wieder her. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Entfernt sämtliche Leerzeichen (`0x20`) aus der AI-Elementkette. |

Es sind gewöhnliche `ModifierInterface`-Implementierungen ohne Sonderstellung — sie werden genauso registriert, geordnet, gemeldet und fehlgeschlagen wie Ihre eigenen:

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

Beide sind zustandslos und threadsicher, sodass eine einzige Instanz geteilt werden kann, und beide sind für konfigurationsgesteuerte Aufbauten über ihren voll qualifizierten Klassennamen ansprechbar (siehe [Modifikatoren registrieren](#modifikatoren-registrieren)).

#### `ModifierRemoveAIBrackets`

Die menschenlesbare Interpretation von GS1 druckt jedes AI in Klammern — `(01)09521234543213(10)ABC123` — rein als Druckkonvention. Ein Scanner oder eine Middleware, die auf Ausgabe der HRI eingestellt ist, reicht diese Klammern als Daten weiter, und die Tokenisierung weiß damit nichts anzufangen.

Die Klammern zu entfernen ist nur die halbe Arbeit. In der HRI markiert die öffnende Klammer des *nächsten* AI das Ende des vorherigen Werts; in geklammerter Form braucht ein AI mit variabler Länge daher kein FNC1. Entfernt man die Klammern naiv, verschwindet diese Grenze:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Deshalb **fügt der Modifikator an jeder Grenze, deren vorangehendes AI variable Länge hat, wieder ein FNC1 ein** und stellt damit genau das wieder her, was die Klammern codierten:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Die Länge wird in der parsereigenen `AiDefinitionRegistry` nachgeschlagen, sodass jedes AI mit variabler Länge behandelt wird statt einer fest verdrahteten Liste. Drei Fälle bleiben bewusst unangetastet: ein Wert, der bereits auf FNC1 endet (eine Quelle, die beide Konventionen ausgibt, erhält kein zweites Trennzeichen), ein geklammerter Code, der kein bekanntes AI ist (ein unbekanntes AI sagt nichts über seine eigene Länge aus), und das letzte AI der Kette.

Das Umschreiben ist **idempotent** — es erneut auf sein eigenes Ergebnis anzuwenden ändert nichts —, es ist daher bei einem gemischten Datenstrom unbedenklich, in dem nur ein Teil der Eingaben geklammert ist.

> **Einschränkung.** `(` und `)` sind selbst gültige GS1-Datenzeichen, und das Muster lautet lediglich `\((\d{2,4})\)`. Ein Wert, der zufällig eine zwei- bis vierstellige Zahl in Klammern enthält, würde ebenfalls entklammert. Wenden Sie dies nur auf eine Quelle an, die die HRI-Klammerkonvention verwendet, nicht auf Werte mit echten Klammern.

#### `ModifierRemoveSpaces`

Manche Scanner, Middleware-Systeme und Etikettendruckstrecken fügen in eine ansonsten wohlgeformte Elementkette überzählige Leerzeichen ein — um ein Feld fester Breite aufzufüllen, um lesbare Gruppen zu trennen oder um einen langen Wert umzubrechen. Die Tokenisierung behandelt jedes davon als Daten, verfälscht damit den Wert, in dem es steht, und verschiebt bei einem AI mit variabler Länge alles Nachfolgende.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Entfernt wird ausschließlich ASCII `0x20`. Anderer Leerraum bleibt erhalten — ein Tabulator etwa liegt außerhalb des GS1-codierbaren Zeichensatzes, sodass der Parser ihn als `GE-S008` meldet, statt ihn stillschweigend zu tilgen.

> **Einschränkung.** Das Leerzeichen (`0x20`) gehört zum invarianten Zeichensatz von GS1; eine Chargennummer oder eine Kundenartikelnummer darf also berechtigterweise eines enthalten. Der Modifikator kann ein überzähliges Leerzeichen nicht von einem echten unterscheiden; wenden Sie ihn nur auf eine Quelle an, von der Sie wissen, dass sie innerhalb ihrer AI-Werte keine Leerzeichen verwendet.

#### Präfixe werden übersprungen, nicht umgeschrieben

Modifikatoren laufen, bevor der Parser irgendetwas entfernt hat; die Roheingabe kann daher noch eine Korrelations-ID, eine AIM-Symbologiekennung und einen ECI-Indikator tragen. Beide eingebauten Modifikatoren ermitteln den Beginn der AI-Elementkette mit der parsereigenen Logik von `CorrelationIdParser` und `DataCarrierParser`, schreiben erst ab dieser Stelle um und setzen das Ergebnis wieder an das **unangetastete** Präfix an:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

EAN/UPC-Datenträger, deren Wert auf GTIN-14 aufgefüllt wird (`isRequiresGtinPadding()`), werden vollständig übersprungen: Ihre Nutzdaten sind ein rein numerischer Strichcodewert ohne AI-Struktur, in dem weder Klammern noch Leerzeichen eine Bedeutung haben können.

#### Reihenfolge: Leerzeichen vor Klammern

Werden beide eingesetzt, **registrieren Sie `ModifierRemoveSpaces` zuerst**. Die Klammererkennung ist positionsabhängig: Ein aufgefülltes `( 01 )` passt nicht auf `\((\d{2,4})\)`, sodass die Klammern bestehen bleiben und das von ihnen implizierte Trennzeichen nie wiederhergestellt wird.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Einen Modifikator schreiben

Schreiben Sie einen eigenen, wenn keiner der eingebauten passt — die Schnittstelle besteht aus einer Methode.

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

Überschreiben Sie stattdessen die zweiargumentige Überladung, wenn das Umschreiben von der Parser-Konfiguration abhängt:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Vertrag:

| Regel | Einzelheit |
|---|---|
| Zustandslos und threadsicher | Je Klasse wird eine Instanz zwischengespeichert und über alle Parse-Vorgänge geteilt. |
| Öffentlicher parameterloser Konstruktor | Nur erforderlich, wenn der Modifikator über den Klassennamen referenziert wird. |
| `null`- und Leereingabe behandeln | Der Parser filtert diese vor dem Lauf der Kette nicht heraus. |
| Rückgabe `null` bedeutet „keine Änderung“ | Der vorherige Wert wird weitergeführt. Geben Sie `input` unverändert zurück, wenn der Modifikator nicht greift. |
| Lieber unverändert zurückgeben als eine Ausnahme werfen | Ein Modifikator, der eine Ausnahme wirft, bricht das Parsen ab — siehe [Fehlerbehandlung](#fehlerbehandlung-bei-modifikatoren). |
| `getName()` | Überschreiben Sie sie, um den in `ModifierInfo` gemeldeten Namen zu steuern; standardmäßig ist es der einfache Klassenname. |

### Modifikatoren registrieren

Modifikatoren laufen in der Reihenfolge, in der sie hinzugefügt werden, und jeder erhält die Ausgabe des vorherigen. Registrieren Sie sie als Instanz, über den voll qualifizierten Klassennamen oder als Liste des einen oder anderen:

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

Die [eingebauten Modifikatoren](#eingebaute-modifikatoren) werden genauso benannt wie Ihre eigenen — **stets voll qualifiziert**. Es gibt für sie weder Kurznamen noch Alias-Auflösung; `ModifierRegistry` löst jeden Modifikator, mitgeliefert oder nicht, über den vollständigen Klassennamen auf.

Die Namen löst `ModifierRegistry` auf; sie instanziiert jede Klasse einmal über deren parameterlosen Konstruktor und legt die Instanz für jede spätere Konfiguration ab, die dieselbe Klasse nennt. Die Auflösung erfolgt **beim Bauen der Konfiguration**, sodass ein Name, der nicht gefunden wird, der `ModifierInterface` nicht implementiert oder der sich nicht instanziieren lässt, dort eine `IllegalArgumentException` wirft — und nicht stillschweigend erst zur Parse-Zeit. Ein Modifikator, der sich nicht reflektiv erzeugen lässt (etwa einer mit einer injizierten Abhängigkeit), kann vorab registriert werden, damit er weiterhin über seinen Namen ansprechbar bleibt:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Nachvollziehen, was ein Modifikator getan hat

Sind Modifikatoren konfiguriert, spiegelt `ParseResult.getPayload()` die **veränderte** Eingabe wider. Das Original bleibt in `ModifierInfo` erhalten:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` meldet das `getName()` jedes Modifikators; standardmäßig ist das der einfache Klassenname, den aber beide eingebauten Modifikatoren überschreiben — eine Kette aus beiden meldet daher die Anzeigenamen und nicht die Klassennamen:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` liefert `null`, wenn kein Modifikator konfiguriert war. Sind Modifikatoren gelaufen, gab aber jeder die Eingabe unverändert zurück, ist die Information vorhanden und `isModified()` ist `false` — in `getAppliedModifiers()` erscheinen nur Modifikatoren, die die Eingabe tatsächlich verändert haben.

### Fehlerbehandlung bei Modifikatoren

Ein Modifikator, der eine Ausnahme wirft, bricht das Parsen ab. Die Ausnahme wird in eine `GaiaModifierException` gehüllt, die den schuldigen Modifikator benennt, und das Ergebnis trägt einen internen Fehler `GE-I001`, dessen Meldung diesen Namen enthält; `getPayload()` meldet die unveränderte Eingabe. Das Parsen wird bewusst **nicht** mit einer halb umgeschriebenen Zeichenkette fortgesetzt: Ein Normalisierungsschritt, der stillschweigend scheitert, lieferte Ergebnisse, die gültig aussehen, aber aus der falschen Eingabe stammen.

---

## Parse-Modi

Jeder Modus benennt die tiefste [Stufe der Kette](#verarbeitungskette-des-parsers), die er ausführt; alle vorangehenden Stufen laufen ebenfalls.

| Modus | Läuft bis | Beantwortet |
|---|---|---|
| `DATA_CARRIER` | Stufe 1 (Eingabeweiterleitung) | Welche Symbologie hat das getragen? |
| `SYNTAX` | Stufe 2 (Syntax) | Sind die AI-Codes und Längen wohlgeformt? |
| `CONTENT` | Stufe 3 (Inhalt) | Sind die Werte gültige GS1-Daten? |
| `INTERPRETATION` | Stufe 4 (Interpretation) | Was bedeuten die Werte? |

### Modus DATA_CARRIER

Endet nach Stufe 1 — validiert die AIM-Symbologiekennung und bestimmt die Symbologie, betritt aber die AI-Parse-Kette nicht. Nützlich, um Symbologien zu bestimmen und Eingaben weiterzuleiten, ohne den Aufwand einer vollständigen Validierung.

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

**Einsetzen, wenn:** Ihre Anwendung den Strichcodetyp bestimmen muss, bevor sie entscheidet, wie die Nutzdaten zu verarbeiten sind — etwa zur Weiterleitung an verschiedene Handler für 1D- gegenüber 2D-Symbologien. Ziehen Sie für diese Weiterleitung den typisierten [`DataCarrierType`](#datacarrierentry-und-datacarriertype) (`getDataCarrier().getDataCarrierType()`) einem Zeichenkettenvergleich auf `getName()` vor.

---

### Modus SYNTAX

Endet nach Stufe 2. Nützlich für eine strukturelle Vorprüfung ohne den Aufwand der Inhaltsvalidierung.

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

**Einsetzen, wenn:** Sie prüfen wollen, ob AI-Codes und Datenlängen wohlgeformt sind, bevor Sie sich auf eine vollständige Validierung einlassen, oder wenn Sie große Mengen verarbeiten, bei denen Inhaltsfehler selten sind.

---

### Modus CONTENT

Endet nach Stufe 3.

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

> Die meisten AI dürfen nicht allein stehen: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) und `21`
> (SERIAL) *erfordern* jeweils einen Identifikationsschlüssel wie AI `01` in derselben
> Elementkette; ließe man die GTIN oben weg, scheiterte das bereits in Stufe 2 an `GE-S005`, ohne
> die Inhaltsvalidierung überhaupt zu erreichen. Setzen Sie `skipRequiresCheck(true)` in der
> `ParseConfig`, um Fragmente zu parsen, die ihre Begleit-AI bewusst weglassen.

**Einsetzen, wenn:** Sie wissen müssen, ob ein gescannter Wert vollständig GS1-konform ist, bevor Sie ihn in einem Geschäftsprozess verwenden — ohne den Aufwand der Interpretationsanreicherung.

---

### Modus INTERPRETATION (Standard)

Führt die vollständige Kette bis Stufe 4 aus. Standard beim Aufruf von `parse(String)` ohne Modusargument. Angereichert werden nur Elemente, die die Inhaltsvalidierung fehlerfrei bestanden haben.

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

**Beispielausgabe:**
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

**Beispiel für einen Geldbetrag (AI 3932 — Preis mit ISO-Währungscode):**
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

**Einsetzen, wenn:** Sie Anzeigeschichten, Werkzeuge zur Etikettenprüfung oder eine beliebige Oberfläche bauen, die eine menschenfreundliche Aufschlüsselung der AI-Werte benötigt.

---

## Korrelations-ID

Manche Arbeitsabläufe stellen der rohen GS1-Eingabe eine herstellerspezifische 8-stellige Korrelationskennung voran, damit sich Scan-Ereignisse einer Sitzung oder Transaktion zuordnen lassen. Das Format lautet:

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

Die Tilde (`~`) ist das Trennzeichen. Sie gehört **nicht** zum GS1-Inhalt — sie wird entfernt, bevor irgendein GS1-Parsen beginnt.

### Erkennungsregeln

Das Präfix wird erkannt, wenn die Eingabe mit genau 8 dezimalen ASCII-Ziffern (`0`–`9`) beginnt, denen unmittelbar ein `~` folgt. Ist das 9. Zeichen kein `~` oder ist eines der ersten 8 Zeichen keine Ziffer, gilt die Eingabe als gewöhnlicher GS1-Inhalt ohne Korrelationspräfix.

### Auf die Korrelations-ID zugreifen

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

### Kombination mit einer AIM-Symbologiekennung

Ein Korrelationspräfix kann vor einer AIM-Symbologiekennung stehen. Der Parser behandelt dies transparent:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Implementierungsklasse:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Ein **GS1 Digital Link** codiert einen oder mehrere AI-Werte unmittelbar in der Struktur einer HTTP(S)-URL und ermöglicht so im Web auflösbare Kennungen für physische Produkte. GAIA setzt den *GS1 Digital Link Standard: URI Syntax* (Release 1.7.0) für **unkomprimierte** URIs um.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` erkennt Digital-Link-URIs automatisch — jede Eingabe, die mit `http://` oder `https://` beginnt, wird an `GS1DLParser` geleitet, der dieselben Inhalts- und Interpretationsstufen ausführt wie die Kette für Elementketten.

### URI-Aufbau und AI-Rollen

Jedes AI in einem Digital-Link-URI übernimmt eine von drei Rollen, die an jedem `GS1AIObjectElement` über `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) verfügbar ist:

| Rolle | Ort | Beispiel |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Erstes `/ai/wert`-Paar des Pfads (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Folgende Pfadpaare, geordnet je Primärschlüssel (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Abfrageparameter mit rein numerischen Schlüsseln (§4.10) | `?17=271231` |

Durchgesetzte Strukturregeln (`DLPathRules`):
- Genau **ein** primärer Identifikationsschlüssel im Pfad; weitere Schlüssel müssen als Datenattribute in der Abfrage codiert werden.
- Schlüsselqualifizierer müssen vom Primärschlüssel zugelassen sein und in der vorgeschriebenen Reihenfolge erscheinen. Optionale Qualifizierer dürfen entfallen, doch alle, die *vorhanden* sind, müssen die feste Reihenfolge einhalten — siehe [Reihenfolge der Qualifizierer](#reihenfolge-der-qualifizierer).
- Vor dem Primärschlüssel dürfen beliebige eigene Pfadsegmente stehen (z. B. `/products/au/01/...`); abrufbar über `getDigitalLinkInfo().getCustomPathStem()`.
- Nicht numerische Abfrageschlüssel (`linkType`, `context`, Erweiterungsparameter wie `23P`) werden ignoriert; rein numerische Schlüssel müssen gültige, als `validAsDataAttribute` gekennzeichnete AI sein.
- Prozentcodierte Wertzeichen werden decodiert; die AI `(03)` und `(8014)` sind nicht zulässig.

Die Primärschlüssel und ihre zulässigen Qualifiziererfolgen sind **datengesteuert** aus den AI-Definitionen abgeleitet — über das Kennzeichen `gs1DigitalLinkPrimaryKey` und das Attribut `gs1DigitalLinkQualifiers` — statt fest verdrahtet zu sein.

Jeder Strukturverstoß und jede Eingabe, die keine URL ist, erzeugt einen strukturellen Digital-Link-Fehler (`GE-L001`–`GE-L014`, ein Code je Bedingung). Die zerlegten URL-Metadaten (`scheme`, `domain`, `path`, `customPathStem`, `query` sowie die `java.net.URL`) bleiben auch bei strukturellen Fehlern über `getDigitalLinkInfo()` verfügbar.

### Reihenfolge der Qualifizierer

Für jeden Primärschlüssel führt `gs1DigitalLinkQualifiers` eine oder mehrere **geordnete** Qualifiziererfolgen auf. Innerhalb einer Folge ist ein in eckigen Klammern stehendes AI **optional**, ein AI ohne Klammern **erforderlich** — analog zur Notation `[cpv-comp]` der ABNF aus §4.9. Die Folgen eines Primärschlüssels sind einander ausschließende Alternativen.

Die GTIN (`01`) etwa definiert zwei Folgen:

| Pfad | Folge | Bedeutung |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — jeweils optional, aber in dieser festen Reihenfolge |
| upui-path | `235` | TPX (erforderlich); GTIN + TPX = UPUI |

So ist `/01/09506000134352/10/LOT-ABC/21/SER` gültig (LOT vor SER, CPV weggelassen), `/01/.../21/SER/10/LOT-ABC` wird **abgelehnt** (falsche Reihenfolge), und `/01/09506000134352/235/2ABC456` ist der upui-path. Die Reihenfolgeprüfung ist ein reihenfolgeerhaltender Teilfolgenabgleich: Optionale AI dürfen also übersprungen, aber nie umgestellt werden.

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

**Implementierungsklasse:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Mit Ergebnissen arbeiten

### ParseResult

Das oberste Ergebnis, das `GaiaParser.parse()` zurückgibt.

| Methode | Liefert | Beschreibung |
|---|---|---|
| `isValid()` | `boolean` | `true`, wenn auf keiner Stufe Fehler vorliegen. Warnungen berühren die Gültigkeit nicht. Immer `true`, wenn `getAiObject()` `null` ist. |
| `getPayload()` | `String` | Die Eingabezeichenkette nach dem Entfernen des Korrelationspräfixes — und nachdem etwaige [Eingabemodifikatoren](#eingabemodifikatoren) sie umgeschrieben haben. |
| `getPayloadContent()` | `String` | Die Nutzdaten ohne AIM-Symbologiekennung und ECI-Präfix. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (ein als nicht GS1-fähig abgelehnter Datenträger, z. B. ein Code-39-Träger `]A0`) oder `UNABLE_TO_DETERMINE_CONTENT` (wenn `aiObject` `null` ist, z. B. im Modus `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Die konfigurierte Kettentiefe (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Die tiefste Stufe, die das Parsen tatsächlich erreicht hat — siehe unten. |
| `isParseComplete()` | `boolean` | `true`, wenn das Parsen die angeforderte Tiefe erreicht hat (`achieved == requested`). Unabhängig von `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Alle aufgelösten AI. `null` im Modus `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Alle Fehler jenseits von WARNING (auf Objekt- und auf allen Elementebenen). |
| `getWarnings()` | `List<GaiaError>` | Alle Hinweise der Stufe WARNING (auf Objekt- und auf allen Elementebenen). |
| `hasWarnings()` | `boolean` | `true`, wenn Hinweise der Stufe WARNING ausgelöst wurden. |
| `getIssues()` | `List<GaiaError>` | Fehler und Warnungen zusammen. |
| `hasDataCarrier()` | `boolean` | `true`, wenn eine AIM-Symbologiekennung erkannt wurde. |
| `getDataCarrier()` | `DataCarrierEntry` | Symbologie-Metadaten, oder `null`, wenn kein Datenträger bestimmt wurde. |
| `hasEci()` | `boolean` | `true`, wenn ein ECI-Indikator aus den Nutzdaten entfernt wurde. |
| `getEci()` | `EciEntry` | ECI-Codierungsmetadaten, oder `null`. |
| `hasCorrelationId()` | `boolean` | `true`, wenn in der ursprünglichen Eingabe ein Korrelationspräfix `DDDDDDDD~` vorhanden war. |
| `getCorrelationInfo()` | `CorrelationInfo` | Die entnommene Korrelations-ID, oder `null`, wenn keine vorhanden war. |
| `isInputModified()` | `boolean` | `true`, wenn ein [Eingabemodifikator](#eingabemodifikatoren) die Eingabe verändert hat. |
| `getModifierInfo()` | `ModifierInfo` | Was die Modifikatorkette getan hat — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null`, wenn kein Modifikator konfiguriert war. |
| `getTiming()` | `ProcessingTiming` | Laufzeitmessung des Parse-Vorgangs — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null`, wenn nicht von `GaiaParser` erzeugt. |
| `getVersion()` | `String` | Die Bibliotheksversion, die das Ergebnis erzeugt hat. |

#### Angeforderter gegenüber erreichtem Parse-Modus

Die Kette durchläuft die Leiter **SYNTAX → CONTENT → INTERPRETATION** und bricht bei Fehlern vorzeitig ab; der tatsächlich *erreichte* Modus kann daher flacher sein als der *angeforderte*. `getAchievedParseMode()` gibt an, wie weit sie gekommen ist:

| Angefordert | Was geschieht | Erreicht | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | ein **Syntax- oder Strukturfehler** stoppt das Parsen nach der Tokenisierung | `SYNTAX` | `false` |
| `INTERPRETATION` | ein **Inhaltsfehler** (falsches Format / falsche Prüfziffer) verhindert die Anreicherung | `CONTENT` | `false` |
| `CONTENT` | die Inhaltsstufe läuft stets vollständig durch (Fehler werden vermerkt, sind aber nicht fatal) | `CONTENT` | `true` |
| beliebig (fehlerfreie Eingabe) | die Kette erreicht die angeforderte Tiefe | = angefordert | `true` |
| `DATA_CARRIER` | Datenträger validiert; kein AI-Inhalt geparst | `DATA_CARRIER` | `true` |
| beliebig | der Datenträger wird vor dem AI-Parsen abgelehnt (z. B. ein nicht GS1-fähiger Träger `]A0`) | `SYNTAX` | `false` |

`isParseComplete()` ist unabhängig von `isValid()`: Ein `CONTENT`-Parse einer GTIN mit falscher Prüfziffer ist **vollständig** (die Inhaltsstufe ist gelaufen) und zugleich **ungültig** (die Prüfziffer schlug fehl). Fragen Sie mit `isParseComplete()` „Ist die Kette so tief gelaufen, wie ich es verlangt habe?“ und mit `isValid()` „Sind die Daten wohlgeformt?“.

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

Die Sammlung der aufgelösten AI-Elemente.

| Methode | Beschreibung |
|---|---|
| `getAis()` | Alle `GS1AIObjectElement`-Instanzen in der Reihenfolge der Eingabe. |
| `get(String aiCode)` | Erstes Element, das dem angegebenen AI-Code entspricht, oder `null`. |
| `contains(String aiCode)` | `true`, wenn ein AI mit diesem Code vorhanden ist. |
| `size()` | Anzahl der aufgelösten AI. |
| `isValid()` | `true`, wenn es keine Fehler auf Objektebene gibt und kein Element Fehler trägt. |
| `toHriString()` | HRI-Zeichenkette, z. B. `(01)09506000134352 (17)261231`. |
| `toElementString()` | Rohe Elementkette — ohne Klammern, mit FNC1 nach jedem Element variabler Länge — z. B. `010950600013435210LOT-ABC<GS>17271231`. Liefert `null`, wenn `isValid()` `false` ist. |
| `getContentType()` | `GS1_DIGITAL_LINK`, wenn `hasDigitalLink()` wahr ist, sonst `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true`, wenn die Eingabe ein GS1-Digital-Link-URI mit primärem Identifikationsschlüssel war. Eine wohlgeformte URL ohne Primärschlüssel stellt zwar `getDigitalLinkInfo()` bereit, liefert hier aber `false`. |
| `getCanonicalDigitalLink()` | Der kanonische GS1-Digital-Link-URI (§4.12) auf `https://id.gs1.org` — Primärschlüssel und Qualifizierer als Pfadsegmente, Datenattribute als nach AI-Schlüssel sortierte Abfrageparameter — oder `null`, wenn kein Primärschlüssel vorhanden ist. |
| `getDigitalLinkInfo()` | Metadaten der URI-Zerlegung (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), oder `null`, wenn es kein Digital Link ist. |
| `getAllErrors()` | Fehler auf Objektebene + alle Elementfehler (jenseits von WARNING). |
| `getAllWarnings()` | Warnungen auf Objektebene + alle Elementwarnungen. |
| `getAllIssues()` | Alles zusammen. |

---

### GS1AIObjectElement

Eine einzelne aufgelöste AI-Instanz.

| Methode | Beschreibung |
|---|---|
| `getAi()` | AI-Code, z. B. `"01"`, `"3102"`. |
| `getTitle()` | GS1-Datentitel, z. B. `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Vollständige GS1-Beschreibung des AI, **in die Parse-Sprache lokalisiert** (z. B. `"Global Trade Item Number (GTIN)"` auf Englisch). Fällt auf den englischen Text der AI-Definition zurück, wenn keine Übersetzung vorliegt. |
| `getFormatString()` | Formatbeschreibung für das AI *und* seine Daten, z. B. `"N2+N14"` für AI `01`, `"N2+X..20"` für AI `10`, `"N4+N3+N..15"` für AI `3932`. |
| `getValue()` | Roher Datenwert, aus der Elementkette entnommen. |
| `isFixedLength()` | `true`, wenn das AI eine feste Datenlänge hat. |
| `getPosition()` | Nullbasierter Zeichenversatz in der ursprünglichen Eingabe. |
| `getGS1ComponentValues()` | Wertabschnitte je Komponente (bei AI mit mehreren Komponenten). |
| `getErrors()` | Fehler auf Elementebene jenseits von WARNING. |
| `getWarnings()` | Hinweise der Stufe WARNING auf Elementebene. |
| `getIssues()` | Fehler und Warnungen des Elements zusammen. |
| `hasErrors()` | `true`, wenn Fehler jenseits von WARNING angehängt sind. |
| `hasWarnings()` | `true`, wenn Hinweise der Stufe WARNING angehängt sind. |
| `getInterpretations()` | `GS1AIInterpretation`-Einträge (im Modus INTERPRETATION befüllt). |
| `getInterpretation(String type)` | Erste Interpretation, die dem angegebenen Typschlüssel aus `GS1Constants_Enricher` entspricht, oder `null`. |
| `getDigitalLinkAIType()` | Die Digital-Link-Rolle des Elements (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), oder `null` bei Eingaben als Elementkette. |
| `hasDigitalLinkAIType()` | `true`, wenn eine Digital-Link-Rolle zugewiesen wurde. |

---

### GaiaError

Ein unveränderlicher Validierungsfehler oder Hinweis.

| Methode | Beschreibung |
|---|---|
| `getId()` | Katalogkennung, z. B. `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` oder `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` oder `INTERNAL`. |
| `getCode()` | Maschinenlesbarer Kurzcode. |
| `getAi()` | AI-Code, der den Fehler ausgelöst hat, oder `null` bei Fehlern auf Objektebene. |
| `getMessage()` | Menschenlesbare Meldung mit eingesetzten Werten. |
| `getPosition()` | Nullbasierter Zeichenversatz in der ursprünglichen Eingabe. |

---

### GS1AIInterpretation

Ein einzelnes beschriftetes Interpretationsfragment, im Modus `INTERPRETATION` an ein `GS1AIObjectElement` angehängt.

| Methode | Beschreibung |
|---|---|
| `getType()` | Maschinenlesbarer Typschlüssel, z. B. `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Über Sprachen hinweg stabil. |
| `getLabel()` | Menschenlesbare Beschriftung, **in die Parse-Sprache lokalisiert** (z. B. `"Date"` / `"GS1 company prefix"` auf Englisch). |
| `getValue()` | Entnommener bzw. angereicherter Wert, z. B. `"31/12/2026"`, `"9506000"`. Wird nicht lokalisiert. |

---

### DataCarrierEntry und DataCarrierType

Trägt die Eingabe eine AIM-Symbologiekennung, liefert `ParseResult.getDataCarrier()` einen `DataCarrierEntry`, der das Symbol beschreibt, das die Daten getragen hat. Der Eintrag ist der konkrete Registereintrag zur erkannten AIM-Symbologiekennung; `DataCarrierType` ist das zur Übersetzungszeit bekannte Enum, zu dem er gehört.

#### DataCarrierEntry

Die Metadaten einer erkannten AIM-Symbologiekennung (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Methode | Beschreibung |
|---|---|
| `getAimCodeId()` | Die erkannte AIM-Symbologiekennung, z. B. `"]C1"`. |
| `getName()` | Menschenlesbarer Name des konkreten Symbols, z. B. `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Ausführlichere Beschreibung des Datenträgers. |
| `getType()` | Der strukturelle Typ des Datenträgers als Zeichenkette (spiegelt `getDataCarrierType().getCategory()`). |
| `getStandard()` | Der Symbologiestandard, sofern erfasst. |
| `getDataCarrierType()` | Der typisierte `DataCarrierType` dieses Eintrags — für die programmatische Weiterleitung vorzuziehen. |
| `isGs1Capable()` | `true`, wenn der Datenträger GS1-Daten aufnehmen kann (AI-Elementketten und/oder Digital Link). |
| `isGs1AICapable()` | `true`, wenn der Datenträger GS1-AI-Elementketten aufnehmen kann. |
| `isGs1DigitalLinkCapable()` | `true`, wenn der Datenträger einen GS1-Digital-Link-URI aufnehmen kann. |
| `isEciCapable()` | `true`, wenn der Datenträger einen ECI-Indikator unterstützt. |
| `isRequiresGtinPadding()` | `true` für EAN/UPC/ITF-Datenträger, deren numerischer Wert vor dem AI-Parsen auf GTIN-14 aufgefüllt wird. |

#### DataCarrierType

Ein zur Übersetzungszeit bekanntes Enum der Datenträgertypen, indiziert über die in ISO/IEC 15424 vergebene AIM-Symbologiekennung (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Das Zeichen nach `]` (das *Codezeichen*) wählt die Familie; die meisten Familien werden auf eine einzige Konstante abgebildet, die sämtliche Modifikatoren abdeckt (`ITF` deckt `]I0`–`]I2` ab; `EAN_UPC` deckt EAN-13, UPC-A, UPC-E und EAN-8 ab). Wo GS1 einen Modifikator für AI-Daten reserviert, bildet diese Variante eine eigene Konstante — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) —, getrennt von ihrem gewöhnlichen Gegenstück. Fehlt eine AIM-Symbologiekennung oder benennt sie einen unbekannten Datenträger, lautet der Typ `UNKNOWN`.

| Methode | Beschreibung |
|---|---|
| `getCategory()` | Die grobe `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` oder `OTHER`. |
| `getCodeChar()` | Das AIM-Codezeichen, das die Familie kennzeichnet, z. B. `"Q"` für QR Code; `null` bei `UNKNOWN`. |
| `getDisplayName()` | Menschenlesbarer Name des *Typs* (kann weiter gefasst sein als `DataCarrierEntry.getName()` — z. B. `"EAN-13 / UPC-A / UPC-E / EAN-8"` gegenüber `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` für Konstanten, die stets GS1-AI-Daten bezeichnen: die vier von GS1 reservierten Varianten (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) sowie `GS1_DATABAR`, das von Natur aus GS1 ist, da jeder `]e`-Modifikator ein GS1 DataBar bezeichnet. Enger gefasst als `DataCarrierEntry.isGs1AICapable()` — auch ein gewöhnlicher `QR_CODE` kann GS1-AI-Daten tragen. |
| `static forAimCodeId(String)` | Löst einen Typ unmittelbar aus einer AIM-Symbologiekennung auf (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); liefert `UNKNOWN` bei fehlender, fehlerhafter oder unbekannter Kennung. |

Weiterleitung nach Typ statt nach Name — etwa um lineare Symbole (Code 128) von 2D-Symbolen (QR / Data Matrix) zu trennen:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` umfasst nur die Matrix- und Punktsymbole; die gestapelt-linearen Datenträger (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) sind `STACKED_LINEAR`, obwohl sie gemeinhin
„2D“-Strichcodes genannt werden. Um beide als eine Gruppe zu behandeln — etwa um zu entscheiden, ob ein
Imager statt eines Laserscanners nötig ist —, prüfen Sie auf eine der beiden Kategorien.

> Die Typauflösung setzt voraus, dass die AIM-Symbologiekennung im Scan enthalten ist; ohne sie ist `getDataCarrier()` `null` und der Typ `UNKNOWN`. Konfigurieren Sie den Scanner so, dass er das AIM-Kennungspräfix überträgt.

---

## Fehlerreferenz

| Code | Stufe | Phase | Bedeutung |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Unbekanntes AI-Präfix — Datenlänge nicht bestimmbar |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Eingabe zu kurz, um einen vollständigen AI-Code zu lesen |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Abgeschnittener Wert — weniger Zeichen, als das AI verlangt |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Doppeltes Anwendungskennzeichen in der Elementkette |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Erforderliche AI-Abhängigkeit fehlt |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Ausgeschlossene AI-Kombination — zwei AI, die nicht gemeinsam auftreten dürfen |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Unerwarteter Fehler bei der Tokenisierung |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Zeichen außerhalb des GS1-codierbaren Zeichensatzes in der Elementkette |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Erforderliches FNC1-Trennzeichen fehlt nach einem AI variabler Länge |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Überzählige Daten jenseits aller Komponentenmaxima |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | FNC1-Trennzeichen nach einem AI fester Länge in mittlerer Position |
| `GE-W002` | WARNING | SYNTAX | FNC1 am Ende der Elementkette (nur Hinweis) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Strukturverstöße im Digital-Link-URI — ein Code je Bedingung (fehlerhafter URI, Schema, Host, Qualifiziererreihenfolge, verbotenes AI, kein Primärschlüssel (`GE-L013`), mehrere Primärschlüssel (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Wert erfüllt den regulären Ausdruck des AI nicht |
| `GE-C003` | DATA_ERROR | CONTENT | Prüfziffernvalidierung fehlgeschlagen |
| `GE-C004` | DATA_ERROR | CONTENT | Validierung des Prüfzeichenpaars fehlgeschlagen |
| `GE-C005` | FORMAT_ERROR | CONTENT | Komponentenwert enthält ein Zeichen außerhalb des erlaubten Zeichensatzes |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Fehler im Komponentenformat — ein Code je Validierungsbedingung (siehe `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Fehler der eigenen semantischen Validierung — ein Code je Validierungsbedingung (siehe `content/validator/`). **Ausnahmen:** Die 14 unten aufgeführten Prüfungen des GS1-Basisnummernpräfixes tragen die Stufe `WARNING`, und `GE-C168` (unbekannter numerischer Ländercode nach ISO 3166-1) trägt `FORMAT_ERROR`. |
| Prüfungen des GS1-Basisnummernpräfixes | WARNING | CONTENT | Der Schlüssel beginnt nicht mit einem bekannten GS1-Basisnummernpräfix, bei den AI mit GS1-Schlüssel — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Nur Hinweis — ohne Einfluss auf die Gültigkeit. |
| `GE-C169` | DATA_ERROR | CONTENT | IMEI-Prüfziffer (Luhn) fehlgeschlagen bei AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | EID-Prüfziffer (Luhn) fehlgeschlagen bei AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Unbekannte AIM-Symbologiekennung |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Datenträger erkannt, unterstützt aber weder GS1-AI-Elementketten noch Digital-Link-URIs |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Unerwarteter interner Fehler |

> **Bekannter Mangel bei der Meldungsdarstellung.** Die Katalogvorlagen setzen eingefügte
> Werte in doppelte Apostrophe nach Art von MessageFormat (`''{value}''`), doch
> `ErrorRegistry` fügt mit einem einfachen `String.replace` ein, sodass die Verdopplung bis in
> `getMessage()` überlebt — Sie sehen derzeit `value ''09506000134351''`, wo die in diesem
> Handbuch zitierten Meldungstexte `value '09506000134351'` zeigen. Betroffen ist jede
> werteinschließende Meldung in allen 35 Sprachkatalogen. Werten Sie Fehlermeldungen nicht aus;
> vergleichen Sie auf `getId()` / `getCode()`.

---

## Thread-Sicherheit

`GaiaParser` ist nach der Konstruktion threadsicher. Eine einzige Instanz darf über Threads hinweg geteilt und nebenläufig verwendet werden. Das empfohlene Muster ist, beim Anwendungsstart eine Instanz zu erzeugen und wiederzuverwenden:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` ist unveränderlich und ebenso gefahrlos zu teilen. Die einzige Thread-Sicherheitspflicht, die die Bibliothek Ihnen nicht abnehmen kann, betrifft die [Eingabemodifikatoren](#eingabemodifikatoren): Von jedem Modifikator wird eine einzige Instanz zwischengespeichert und über alle nebenläufigen Parse-Vorgänge geteilt, weshalb Implementierungen zustandslos sein müssen.

---

## Anhang A — AI-Zeichenkettenkonstanten

`GS1Constants_AICodes` (im Paket `tools.pantheum.gaia.gs1.constants`) deklariert für jedes von GAIA erkannte Anwendungskennzeichen eine `String`-Konstante. Verwenden Sie diese Konstanten, statt AI-Codes als Zeichenketten fest zu verdrahten:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Jede Konstante enthält die Zeichenkettenform des AI-Codes (z. B. `AI_01_GTIN = "01"`).

### Identifikation und Serialisierung

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Nummer der Versandeinheit (SSCC). |
| `01` | `AI_01_GTIN` | Globale Artikelidentnummer (GTIN). |
| `02` | `AI_02_CONTENT` | Globale Artikelidentnummer (GTIN) der enthaltenen Handelseinheiten. |
| `03` | `AI_03_MTO_GTIN` | Kennzeichnung einer auftragsbezogen gefertigten Handelseinheit (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Chargen- oder Losnummer. |
| `20` | `AI_20_VARIANT` | Interne Produktvariante. |
| `21` | `AI_21_SERIAL` | Seriennummer. |
| `22` | `AI_22_CPV` | Variante des Konsumgüterprodukts. |
| `235` | `AI_235_TPX` | Von Dritten verwaltete, serialisierte Erweiterung der Globalen Artikelidentnummer (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Zusätzliche, vom Hersteller vergebene Produktkennzeichnung. |
| `241` | `AI_241_CUST_PART_NO` | Kunden-Teilenummer. |
| `242` | `AI_242_MTO_VARIANT` | Variantennummer der auftragsbezogenen Fertigung. |
| `243` | `AI_243_PCN` | Verpackungskomponentennummer. |
| `250` | `AI_250_SECONDARY_SERIAL` | Sekundäre Seriennummer. |
| `251` | `AI_251_REF_TO_SOURCE` | Verweis auf die Quellinstanz. |
| `253` | `AI_253_GDTI` | Globale Dokumenttyp-Kennung (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Erweiterungskomponente der Globalen Lokationsnummer (GLN). |
| `255` | `AI_255_GCN` | Globale Couponnummer (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variable Stückzahl (Handelseinheit mit variabler Menge). |
| `37` | `AI_37_COUNT` | Anzahl der Handelseinheiten oder Handelseinheitenstücke in einer logistischen Einheit. |

### Datum und Uhrzeit

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Produktionsdatum (JJMMTT). |
| `12` | `AI_12_DUE_DATE` | Fälligkeitsdatum (JJMMTT). |
| `13` | `AI_13_PACK_DATE` | Verpackungsdatum (JJMMTT). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Mindesthaltbarkeitsdatum (JJMMTT). |
| `16` | `AI_16_SELL_BY` | Verkaufsdatum, letztes (JJMMTT). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Verfallsdatum (JJMMTT). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Lieferdatum und -uhrzeit, nicht früher als (JJMMTThhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Lieferdatum und -uhrzeit, nicht später als (JJMMTThhmm). |
| `4326` | `AI_4326_REL_DATE` | Freigabedatum (JJMMTT). |
| `7003` | `AI_7003_EXPIRY_TIME` | Verfallsdatum und -uhrzeit (JJMMTThhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Datum des Ersteinfrierens (JJMMTT). |
| `7007` | `AI_7007_HARVEST_DATE` | Erntedatum (JJMMTT[JJMMTT]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Testdatum, spätestens (JJMMTT[hhmm]). |

### Menge und Maß — variables Maß (metrisch)

Die vierstelligen AI-Familien `310n`–`369n` codieren Mengen mit variablem Maß. Die dritte Ziffer wählt die Maßart; die **vierte Ziffer** (`n`, 0–5) ist die Anzahl der implizierten Nachkommastellen — `AI_3102_NET_WEIGHT_KG` bedeutet also Nettogewicht in kg mit 2 Nachkommastellen.

| Familie | Konstantenmuster (`n` = Nachkommastellen-Ziffer) | Beschreibung |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Nettogewicht, Kilogramm (Handelseinheit mit variabler Menge). |
| `311n` | `AI_311n_LENGTH_M` | Länge oder erste Abmessung, Meter (Handelseinheit mit variabler Menge). |
| `312n` | `AI_312n_WIDTH_M` | Breite, Durchmesser oder zweite Abmessung, Meter (Handelseinheit mit variabler Menge). |
| `313n` | `AI_313n_HEIGHT_M` | Tiefe, Dicke, Höhe oder dritte Abmessung, Meter (Handelseinheit mit variabler Menge). |
| `314n` | `AI_314n_AREA_M` | Fläche, Quadratmeter (Handelseinheit mit variabler Menge). |
| `315n` | `AI_315n_NET_VOLUME_L` | Nettovolumen, Liter (Handelseinheit mit variabler Menge). |
| `316n` | `AI_316n_NET_VOLUME_M` | Nettovolumen, Kubikmeter (Handelseinheit mit variabler Menge). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Logistikgewicht, Kilogramm. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Länge oder erste Abmessung, Meter. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Breite, Durchmesser oder zweite Abmessung, Meter. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Tiefe, Dicke, Höhe oder dritte Abmessung, Meter. |
| `334n` | `AI_334n_AREA_M_LOG` | Fläche, Quadratmeter. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Logistikvolumen, Liter. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Logistikvolumen, Kubikmeter. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramm pro Quadratmeter. |

### Menge und Maß — variables Maß (imperial / USA)

| Familie | Konstantenmuster (`n` = Nachkommastellen-Ziffer) | Beschreibung |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Nettogewicht, Pfund (Handelseinheit mit variabler Menge). |
| `321n` | `AI_321n_LENGTH_IN` | Länge oder erste Abmessung, Zoll (Handelseinheit mit variabler Menge). |
| `322n` | `AI_322n_LENGTH_FT` | Länge oder erste Abmessung, Fuß (Handelseinheit mit variabler Menge). |
| `323n` | `AI_323n_LENGTH_YD` | Länge oder erste Abmessung, Yard (Handelseinheit mit variabler Menge). |
| `324n` | `AI_324n_WIDTH_IN` | Breite, Durchmesser oder zweite Abmessung, Zoll (Handelseinheit mit variabler Menge). |
| `325n` | `AI_325n_WIDTH_FT` | Breite, Durchmesser oder zweite Abmessung, Fuß (Handelseinheit mit variabler Menge). |
| `326n` | `AI_326n_WIDTH_YD` | Breite, Durchmesser oder zweite Abmessung, Yard (Handelseinheit mit variabler Menge). |
| `327n` | `AI_327n_HEIGHT_IN` | Tiefe, Dicke, Höhe oder dritte Abmessung, Zoll (Handelseinheit mit variabler Menge). |
| `328n` | `AI_328n_HEIGHT_FT` | Tiefe, Dicke, Höhe oder dritte Abmessung, Fuß (Handelseinheit mit variabler Menge). |
| `329n` | `AI_329n_HEIGHT_YD` | Tiefe, Dicke, Höhe oder dritte Abmessung, Yard (Handelseinheit mit variabler Menge). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Logistikgewicht, Pfund. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Länge oder erste Abmessung, Zoll. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Länge oder erste Abmessung, Fuß. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Länge oder erste Abmessung, Yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Breite, Durchmesser oder zweite Abmessung, Zoll. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Breite, Durchmesser oder zweite Abmessung, Fuß. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Breite, Durchmesser oder zweite Abmessung, Yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Tiefe, Dicke, Höhe oder dritte Abmessung, Zoll. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Tiefe, Dicke, Höhe oder dritte Abmessung, Fuß. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Tiefe, Dicke, Höhe oder dritte Abmessung, Yard. |
| `350n` | `AI_350n_AREA_IN` | Fläche, Quadratzoll (Handelseinheit mit variabler Menge). |
| `351n` | `AI_351n_AREA_FT` | Fläche, Quadratfuß (Handelseinheit mit variabler Menge). |
| `352n` | `AI_352n_AREA_YD` | Fläche, Quadratyard (Handelseinheit mit variabler Menge). |
| `353n` | `AI_353n_AREA_IN_LOG` | Fläche, Quadratzoll. |
| `354n` | `AI_354n_AREA_FT_LOG` | Fläche, Quadratfuß. |
| `355n` | `AI_355n_AREA_YD_LOG` | Fläche, Quadratyard. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Nettogewicht, Feinunzen (Handelseinheit mit variabler Menge). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Nettogewicht (oder -volumen), Unzen (Handelseinheit mit variabler Menge). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Nettovolumen, Quart (Handelseinheit mit variabler Menge). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Nettovolumen, US-Gallonen (Handelseinheit mit variabler Menge). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Logistikvolumen, Quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Logistikvolumen, US-Gallonen. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Nettovolumen, Kubikzoll (Handelseinheit mit variabler Menge). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Nettovolumen, Kubikfuß (Handelseinheit mit variabler Menge). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Nettovolumen, Kubikyard (Handelseinheit mit variabler Menge). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Logistikvolumen, Kubikzoll. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Logistikvolumen, Kubikfuß. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Logistikvolumen, Kubikyard. |

### Preise und Geldbeträge

Die vierte Ziffer (`n`) codiert die Anzahl der implizierten Nachkommastellen. Ihr zulässiger Bereich
unterscheidet sich je Familie — siehe Spalte `n`.

| Familie | Konstantenmuster (`n` = Nachkommastellen-Ziffer) | `n` | Beschreibung |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Zu zahlender Betrag oder Couponwert, Landeswährung. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Zu zahlender Betrag mit ISO-Währungscode. |
| `392n` | `AI_392n_PRICE` | 0–9 | Zu zahlender Betrag, einheitlicher Währungsraum (Handelseinheit mit variabler Menge). |
| `393n` | `AI_393n_PRICE` | 0–9 | Zu zahlender Betrag mit ISO-Währungscode (Handelseinheit mit variabler Menge). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Prozentualer Rabatt eines Coupons. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Zu zahlender Betrag je Maßeinheit, einheitlicher Währungsraum (Handelseinheit mit variabler Menge). |

### Ort und Versand

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Bestellnummer des Kunden. |
| `401` | `AI_401_GINC` | Globale Sendungsidentifikationsnummer (GINC). |
| `402` | `AI_402_GSIN` | Globale Versandidentifikationsnummer (GSIN). |
| `403` | `AI_403_ROUTE` | Routing-Code. |
| `410` | `AI_410_SHIP_TO_LOC` | Globale Lokationsnummer (GLN) der Lieferadresse. |
| `411` | `AI_411_BILL_TO` | Globale Lokationsnummer (GLN) des Rechnungsempfängers. |
| `412` | `AI_412_PURCHASE_FROM` | Globale Lokationsnummer (GLN) des Einkaufslieferanten. |
| `413` | `AI_413_SHIP_FOR_LOC` | Globale Lokationsnummer (GLN) für die Weiterleitung. |
| `414` | `AI_414_LOC_NO` | Kennzeichnung eines physischen Standorts - Globale Lokationsnummer (GLN). |
| `415` | `AI_415_PAY_TO` | Globale Lokationsnummer (GLN) des Rechnungsstellers. |
| `416` | `AI_416_PROD_SERV_LOC` | Globale Lokationsnummer (GLN) des Produktions- oder Dienstleistungsstandorts. |
| `417` | `AI_417_PARTY` | Globale Lokationsnummer (GLN) der Partei. |
| `420` | `AI_420_SHIP_TO_POST` | Postleitzahl der Lieferadresse innerhalb einer einzigen Postbehörde. |
| `421` | `AI_421_SHIP_TO_POST` | Postleitzahl der Lieferadresse mit ISO-Ländercode. |
| `422` | `AI_422_ORIGIN` | Ursprungsland einer Handelseinheit. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Land der Erstverarbeitung. |
| `424` | `AI_424_COUNTRY_PROCESS` | Land der Verarbeitung. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Land der Demontage. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Land, das die gesamte Verarbeitungskette abdeckt. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Landesteil des Ursprungslands. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Firmenname der Lieferadresse. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Ansprechpartner der Lieferadresse. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Lieferadresse, Zeile 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Lieferadresse, Zeile 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Stadtteil der Lieferadresse. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Ort der Lieferadresse. |
| `4306` | `AI_4306_SHIP_TO_REG` | Region der Lieferadresse. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Ländercode der Lieferadresse. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Telefonnummer der Lieferadresse. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Geoposition der Lieferadresse. |
| `4310` | `AI_4310_RTN_TO_COMP` | Firmenname der Rücksendeadresse. |
| `4311` | `AI_4311_RTN_TO_NAME` | Ansprechpartner der Rücksendeadresse. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Rücksendeadresse, Zeile 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Rücksendeadresse, Zeile 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Stadtteil der Rücksendeadresse. |
| `4315` | `AI_4315_RTN_TO_LOC` | Ort der Rücksendeadresse. |
| `4316` | `AI_4316_RTN_TO_REG` | Region der Rücksendeadresse. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Ländercode der Rücksendeadresse. |
| `4318` | `AI_4318_RTN_TO_POST` | Postleitzahl der Rücksendeadresse. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Telefonnummer der Rücksendeadresse. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Beschreibung des Dienstleistungscodes. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Kennzeichen für Gefahrgut. |
| `4322` | `AI_4322_AUTH_LEAVE` | Zustellungserlaubnis ohne Unterschrift. |
| `4323` | `AI_4323_SIG_REQUIRED` | Kennzeichen für Unterschriftspflicht. |
| `4330` | `AI_4330_MAX_TEMP_F` | Maximaltemperatur in Fahrenheit (angegeben in Hundertstelgrad). |
| `4331` | `AI_4331_MAX_TEMP_C` | Maximaltemperatur in Celsius (angegeben in Hundertstelgrad). |
| `4332` | `AI_4332_MIN_TEMP_F` | Minimaltemperatur in Fahrenheit (angegeben in Hundertstelgrad). |
| `4333` | `AI_4333_MIN_TEMP_C` | Minimaltemperatur in Celsius (angegeben in Hundertstelgrad). |

### Produktmerkmale und Rückverfolgbarkeit

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO-Versorgungsnummer (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE-Klassifikation von Schlachtkörpern und Fleischteilstücken. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktive Wirkstärke. |
| `7005` | `AI_7005_CATCH_AREA` | Fanggebiet. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Art zu Fischereizwecken. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Art des Fanggeräts. |
| `7010` | `AI_7010_PROD_METHOD` | Produktionsmethode. |
| `7020` | `AI_7020_REFURB_LOT` | Los-Kennung der Aufarbeitung. |
| `7021` | `AI_7021_FUNC_STAT` | Funktionsstatus. |
| `7022` | `AI_7022_REV_STAT` | Revisionsstatus. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Globale individuelle Betriebsmittelkennung (GIAI) einer Baugruppe. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Nummer des Verarbeiters, mit dreistelligem ISO-Ländercode (10 Plätze). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC mit Erweiterung 1 und Importeur-Index. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT-Frachteinheitstyp. |

### Nationale Erstattungsnummern im Gesundheitswesen (NHRN)

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Deutschland PZN. |
| `711` | `AI_711_NHRN_CIP` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Frankreich CIP. |
| `712` | `AI_712_NHRN_CN` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Spanien CN. |
| `713` | `AI_713_NHRN_DRN` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Brasilien DRN. |
| `714` | `AI_714_NHRN_AIM` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Vereinigte Staaten von Amerika NDC. |
| `716` | `AI_716_NHRN_AIC` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Italien AIC. |
| `717` | `AI_717_NHRN_SRN` | Nationale Erstattungsnummer für Gesundheitsleistungen (NHRN) - Costa Rica, Gesundheitsregisternummer. |

### Gesundheitswesen, GMN, HIDRI, CPID, Personendaten

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Zertifizierungsreferenz (10 Plätze). |
| `7240` | `AI_7240_PROTOCOL` | Protokollkennung. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC-Datenträgertyp. |
| `7242` | `AI_7242_VCN` | Versionskontrollnummer (VCN). |
| `7250` | `AI_7250_DOB` | Geburtsdatum (JJJJMMTT). |
| `7251` | `AI_7251_DOB_TIME` | Geburtsdatum und -uhrzeit (JJJJMMTThhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biologisches Geschlecht. |
| `7253` | `AI_7253_FAMILY_NAME` | Familienname der Person. |
| `7254` | `AI_7254_GIVEN_NAME` | Vorname der Person. |
| `7255` | `AI_7255_SUFFIX` | Namenszusatz der Person. |
| `7256` | `AI_7256_FULL_NAME` | Vollständiger Name der Person. |
| `7257` | `AI_7257_PERSON_ADDR` | Adresse der Person. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Geburtsreihenfolge (Mehrlingsgeburt). |
| `7259` | `AI_7259_BABY` | Familienname des Neugeborenen. |
| `8001` | `AI_8001_DIMENSIONS` | Rollenware (Breite, Länge, Kerndurchmesser, Wickelrichtung, Spleiße). |
| `8002` | `AI_8002_CMT_NO` | Kennung des Mobiltelefons. |
| `8003` | `AI_8003_GRAI` | Globale Mehrwegtransportgut-Kennung (GRAI). |
| `8004` | `AI_8004_GIAI` | Globale individuelle Betriebsmittelkennung (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Preis je Maßeinheit. |
| `8006` | `AI_8006_ITIP` | Kennzeichnung eines einzelnen Handelseinheitenstücks (ITIP). |
| `8007` | `AI_8007_IBAN` | Internationale Bankkontonummer (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Produktionsdatum und -uhrzeit (JJMMTThh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optisch lesbarer Sensorindikator. |
| `8010` | `AI_8010_CPID` | Bauteil-/Komponentenkennung (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Seriennummer der Bauteil-/Komponentenkennung (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Softwareversion. |
| `8013` | `AI_8013_GMN` | Globale Modellnummer (GMN). |
| `8014` | `AI_8014_MUDI` | Registrierungskennung für hochindividualisierte Medizinprodukte (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Globale Dienstleistungsbeziehungsnummer (GSRN) zur Identifikation der Beziehung zwischen einer Dienstleistungen anbietenden Organisation und dem Dienstleistungserbringer. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Globale Dienstleistungsbeziehungsnummer (GSRN) zur Identifikation der Beziehung zwischen einer Dienstleistungen anbietenden Organisation und dem Dienstleistungsempfänger. |
| `8019` | `AI_8019_SRIN` | Instanznummer der Dienstleistungsbeziehung (SRIN). |
| `8020` | `AI_8020_REF_NO` | Referenznummer des Zahlscheins. |
| `8026` | `AI_8026_ITIP_CONTENT` | Kennzeichnung der in einer logistischen Einheit enthaltenen Handelseinheitenstücke (ITIP). |
| `8030` | `AI_8030_DIGSIG` | Digitale Signatur (DigSig). |
| `8040` | `AI_8040_IMEI` | Internationale Mobilfunkgerätekennung (IMEI). |
| `8041` | `AI_8041_IMEI2` | Internationale Mobilfunkgerätekennung 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nummer der eingebetteten SIM. |
| `8043` | `AI_8043_PSIM` | Nummer der physischen SIM. |
| `8110` | `AI_8110` | Coupon-Code-Kennung zur Verwendung in Nordamerika. |
| `8111` | `AI_8111_POINTS` | Bonuspunkte eines Coupons. |
| `8112` | `AI_8112` | Coupon-Code-Kennung der Positive-Offer-File-Datei zur Verwendung in Nordamerika. |
| `8200` | `AI_8200_PRODUCT_URL` | URL für erweiterte Verpackungsinformationen. |

### Interne / betriebliche Verwendung

| AI | Konstante | Beschreibung |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Zwischen Handelspartnern gegenseitig vereinbarte Information. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Betriebsinterne Information (9 Plätze). |

---

## Anhang B — Konstanten der Interpretationsschlüssel

Wird `GaiaParser.parse()` mit `ParseMode.INTERPRETATION` aufgerufen, kann jedes `GS1AIObjectElement` eine Liste von `GS1AIInterpretation`-Objekten tragen, die von fachspezifischen Anreicherern erzeugt wurden. Verwenden Sie die Konstanten aus `GS1Constants_Enricher` (im Paket `tools.pantheum.gaia.gs1.constants`) als Schlüssel, um bestimmte Interpretationswerte nachzuschlagen:

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

Anzeigebeschriftungen sind **keine** Konstanten — sie liegen in den lokalisierten Katalogen unter `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indiziert über die Typkonstante. `GS1AIInterpretation.getLabel()` liefert die Beschriftung für die Parse-Sprache (siehe [Lokalisierte Meldungen und Beschriftungen](#lokalisierte-meldungen-und-beschriftungen)) und fällt auf das Englische zurück, wenn ein Katalog den Schlüssel auslässt. Die Spalte „Anzeigebeschriftung“ unten führt den deutschen Text so auf, wie er im Katalog ausgeliefert wird; die Typschlüssel selbst sind über Sprachen hinweg stabil — vergleichen Sie daher stets auf den Schlüssel, nie auf die Beschriftung.

### Datum und Uhrzeit

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `DATE_VALUE` | Datum | Datums-AI (11–17, 7003, 7006, 7011 usw.) |
| `DATE_FORMAT` | Datumsformat | Datums-AI |
| `TIME_VALUE` | Uhrzeit | AI mit Uhrzeitanteil (7003, 7011, 8008 usw.) |
| `TIME_FORMAT` | Zeitformat | AI mit Uhrzeitanteil |
| `DATETIME_VALUE` | Datum und Uhrzeit | Datums- und Zeit-AI |
| `DATETIME_FORMAT` | Datums- und Zeitformat | Datums- und Zeit-AI |

### Erntedatum

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Erntebeginndatum | AI 7007 |
| `HARVEST_END_DATE` | Ernteenddatum | AI 7007 (optionales Bereichsende) |
| `HARVEST_DATE_RANGE` | Erntedatumsbereich | AI 7007 |

### GS1-Basisnummer

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1-Basisnummer | GTIN- / GLN- / SSCC-AI |
| `GS1_MEMBER_CODE` | GS1-Mitgliedscode | GTIN- / GLN- / SSCC-AI |
| `GS1_MEMBER_NAME` | GS1-Mitgliedsorganisation | GTIN- / GLN- / SSCC-AI |

### GTIN

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN-Typ | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Verpackungsebene | AI 01 |
| `GTIN_CHECK_DIGIT` | Prüfziffer | AI 01, 02 |

### SSCC

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Erweiterungsziffer | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Serienreferenz | AI 00 |
| `SSCC_CHECK_DIGIT` | Prüfziffer | AI 00 |

### Land (ISO 3166)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Ländercode (numerisch) | Einzelland-AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Ländercode (Alpha-2) | Alpha-2-Länder-AI |
| `COUNTRY_NAME` | Ländername | Einzelland-AI |
| `COUNTRY_LIST` | Länder | AI 423 — alle Namen zusammengefügt, z. B. `Australia, New Zealand` |

AI 423 (Land der ersten Verarbeitung) kann bis zu fünf Länder tragen und gibt daher je Land
ein **nummeriertes Paar** aus — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … —, gefolgt von der einzelnen Zusammenfassung
`COUNTRY_LIST`. Bilden Sie diese Schlüssel aus den Konstanten `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` und dem 1-basierten Index, oder durchlaufen Sie einfach `getInterpretations()`; die
Schlüssel `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` ohne Suffix werden für AI 423 **nicht** ausgegeben.

### Währung (ISO 4217)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Währungscode | Betrags-AI mit Währung (391n, 393n) |
| `CURRENCY_ALPHA` | Alphabetischer Währungscode | Betrags-AI mit Währung |
| `CURRENCY_NAME` | Währungsname | Betrags-AI mit Währung |

### Temperatur

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatur | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Temperatureinheit | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatur (formatiert) | AI 4330–4333 |

### Geschlecht (ISO 5218)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `SEX_CODE` | Geschlechtscode | AI 7252 |
| `SEX_DESCRIPTION` | Geschlechtsbeschreibung | AI 7252 |

### Wasserlebewesen (FAO)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Artcode | AI 7008 |
| `SPECIES_SCIENTIFIC` | Wissenschaftlicher Name | AI 7008 |
| `SPECIES_ENGLISH` | Gebräuchlicher Name | AI 7008 |
| `SPECIES_FAMILY` | Familie | AI 7008 |
| `SPECIES_ORDER` | Ordnung | AI 7008 |

### NATO-Versorgungsnummer (NSN)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `NSN_FSG` | Versorgungsgruppe | AI 7001 |
| `NSN_FSG_NAME` | Name der Versorgungsgruppe | AI 7001 |
| `NSN_FSCG` | Versorgungsklasse | AI 7001 |
| `NSN_FSCG_NAME` | Name der Versorgungsklasse | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Ländercode | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Land | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO-Ländercode | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS-Kategorie | AI 7001 |
| `NSN_NIIN` | Nationale Artikelnummer | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Rollenware

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Rollenbreite (mm) | AI 8001 |
| `ROLL_LENGTH` | Rollenlänge (m) | AI 8001 |
| `CORE_DIAMETER` | Kerndurchmesser (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Wickelrichtungscode | AI 8001 |
| `WINDING_DIRECTION` | Wickelrichtung | AI 8001 |
| `SPLICES` | Spleiße | AI 8001 |

### IBAN

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Ländercode | AI 8007 |
| `IBAN_COUNTRY_NAME` | Land | AI 8007 |
| `IBAN_CHECK_DIGITS` | Prüfziffern | AI 8007 |
| `IBAN_CHECK_VALID` | Prüfung | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Seriennummer | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Prüfziffer | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Vergabestelle | AI 8040, 8041 |

Die 15 Ziffern gliedern sich als `[ TAC (8) ][ Seriennummer (6) ][ Luhn-Prüfziffer (1) ]`, wobei die
RBI die führenden 2 Ziffern des TAC sind — `IMEI_RBI` ist also ein Präfix von `IMEI_TAC` und kein
eigener Abschnitt. `IMEI_FORMATTED` stellt die übliche GSMA-Anzeigegruppierung
`AA-BBBBBB-CCCCCC-D` dar (z. B. `49-015420-323751-8`), die den TAC an der RBI-Grenze
teilt; die alte Gruppierung `6-2-6-1`, die dort schnitt, wo der eingestellte Final Assembly
Code begann, wird nicht ausgegeben.

`IMEI_RBI_NAME` löst die RBI über `ImeiRbiData` in den Namen der vergebenden Stelle auf und wird
**zuletzt und nur dann angehängt, wenn der Code dort verzeichnet ist**. Diese Tabelle umfasst drei Gruppen:

- **Derzeit vergebend** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF sowie `99`
  Global Hexadecimal Administrator und `98` (reserviert).
- **Testbereiche** — `00` und `02`–`09`, die Test-IMEI statt einer echten Vergabe kennzeichnen.
  Abfragbar mit `ImeiRbiData.isTestCode(code)`.
- **Nicht mehr vergebend** — historische Stellen wie `49` (BZT/BAPT, Deutschland), `44`
  (BABT, Vereinigtes Königreich) oder `91` (MSAI, Indien). Abfragbar mit `ImeiRbiData.isNoLongerAllocating(code)`.
  Geräte mit diesen Codes sind gewöhnlich und bleiben im Einsatz; eingestellt ist lediglich die Neuvergabe.
  Es handelt sich also um eine Berichtsinformation, nie um ein Gültigkeitssignal.

Ein fehlendes `IMEI_RBI_NAME` bedeutet „diese RBI steht nicht in unserer Tabelle“, **nicht** „ungültige IMEI“:
Die Tabelle ist aus einer veröffentlichten RBI-Liste zusammengestellt und nicht unmittelbar von der GSMA, sie
kann neu benannten Stellen daher hinterherhinken. Leiten Sie aus ihrem Fehlen kein Validierungsergebnis ab;
die RBI ist kein Prüfzeichen. Code, der die Interpretationsliste durchläuft, muss ihr Fehlen ebenfalls
vertragen, statt nach Position zu indizieren.

### SIM-Kennungen (EID / ICCID)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Branchenkategorie | AI 8042 |
| `EID_BODY` | EID-Hauptteil | AI 8042 |
| `EID_CHECK_DIGIT` | Prüfziffer | AI 8042 |
| `ICCID_BODY` | ICCID-Hauptteil | AI 8043 |
| `ICCID_EXTENSION` | Erweiterung | AI 8043 |

`SIM_MII` trägt die führenden **zwei** Ziffern (`89`), das Paar, das ITU-T E.118 dem
Fernmeldewesen zuweist. ISO/IEC 7812 selbst definiert die MII als **nur die erste Ziffer**, weshalb
`SIM_MII_NAME` die Kategorie über `Iso7812Data` aus dieser führenden `8` auflöst — was
„Healthcare, telecommunications and other future industry assignments“ ergibt. Für eine wohlgeformte
EID ist dieser Wert daher konstant; er wird zur Nachvollziehbarkeit gegenüber dem Standard gemeldet, nicht als
Unterscheidungsmerkmal. `Iso7812Data.nameForCode(digit)` nimmt eine einzelne Ziffer,
`nameForIdentifier(prefix)` akzeptiert ein längeres Präfix und liest dessen führende Ziffer.

`SIM_MII_NAME` wird ausschließlich von `EidEnricher` (AI 8042) ausgegeben. `IccidEnricher` (AI 8043)
stellt `SIM_MII` ohne die Kategorie bereit.

### Zertifizierungsreferenz

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Sequenznummer | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Code des Zertifizierungsschemas | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Zertifizierungsschema | AI 7230–7239 |
| `CERT_REFERENCE` | Zertifizierungsreferenz | AI 7230–7239 |

### GS1 UIC

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC-Code | AI 7040 |
| `UIC_EXTENSION_1` | Erweiterung 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Importeur-Index | AI 7040 |

### Geburtenreihenfolge des Neugeborenen

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Geburtsposition | AI 7258 |
| `BIRTH_TOTAL` | Geburten insgesamt | AI 7258 |
| `BIRTH_SEQUENCE` | Geburtenfolge | AI 7258 |

### Globale Modellnummer (GMN)

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Modellreferenz | AI 8013 |
| `GMN_CHECK_PAIR` | Prüfzeichenpaar | AI 8013 |

### HIDRI

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Gerätereferenz | AI 8014 |
| `HIDRI_CHECK_PAIR` | Prüfzeichenpaar | AI 8014 |

### CPID

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Komponenten- und Teilereferenz | AI 8010–8011 |

### Dezimal- und Maßwerte

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Dezimalwert | Numerische AI mit implizierten Nachkommastellen (31xx–36xx) |
| `DECIMAL_AMOUNT` | Betrag | Preis-AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | Prozentsatz | AI 394n |
| `DECIMAL_PLACES` | Dezimalstellen | Zusammen mit `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Prozentformat | AI 394n |
| `ISO_UNIT_CODE` | ISO-Einheitencode | Maß-AI |
| `ISO_UNIT_NAME` | ISO-Einheitenname | Maß-AI |
| `MONETARY_AMOUNT` | Geldbetrag | Preis-AI |
| `MONETARY_AMOUNT_DISPLAY` | Geldbetrag (formatiert) | Preis-AI |

### Geokoordinaten

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `LATITUDE` | Breitengrad | AI 4309 |
| `LONGITUDE` | Längengrad | AI 4309 |
| `GEO_COORDINATES` | Geokoordinaten | AI 4309 |
| `LATITUDE_DMS` | Breitengrad (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Längengrad (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Geokoordinaten (DMS) | AI 4309 |

### Produktionsmethode

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Produktionsmethodencode | AI 7010 |
| `PRODUCTION_METHOD` | Produktionsmethode | AI 7010 |

### AIDC-Medientyp

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC-Medientypcode | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC-Medientyp | AI 7241 |

### Stück von Gesamtzahl

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Stücknummer | AI 8006 |
| `PIECE_TOTAL` | Gesamtstückzahl | AI 8006 |
| `PIECE_OF_TOTAL` | Stück von Gesamt | AI 8006 |

### Komponentenzerlegungen

Schlüssel, die von den deklarativen Komponentenzerlegungen in `content/ai-content.json` ausgegeben werden statt
von einem Java-Anreicherer — sie legen die benannten Teile eines zusammengesetzten AI-Werts offen. Anders als alle
übrigen Schlüssel dieses Anhangs haben sie **keine Konstante in `GS1Constants_Enricher`**: Vergleichen Sie
die Zeichenkette wörtlich oder lesen Sie den Typ über `GS1AIInterpretation.getType()`.

| Typschlüssel | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Prüfziffer | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Seriennummer | AI 253, 255, 8003 |
| `POSTAL_CODE` | Postleitzahl | AI 421 |
| `PROCESSOR_ID` | Verarbeiter-Kennung | AI 7030–7039 |

Beachten Sie, dass `CHECK_DIGIT` hier der allgemeine Schlüssel der Komponentenzerlegung ist, verschieden von den
anreichererspezifischen Schlüsseln `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` und
`EID_CHECK_DIGIT` weiter oben.

### Verschiedenes

| Schlüsselkonstante | Anzeigebeschriftung | Erzeugt von |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Wert | Boolesche AI / Kennzeichen-AI (4321–4323) |
| `DECODED_TEXT` | Dekodierter Text | Freitext-AI |
