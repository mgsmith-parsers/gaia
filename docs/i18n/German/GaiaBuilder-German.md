# GaiaBuilder — Entwicklerhandbuch

## Inhaltsverzeichnis

1. [Überblick](#überblick)
2. [Über GS1 und die General Specifications](#über-gs1-und-die-general-specifications)
3. [Schnelleinstieg](#schnelleinstieg)
4. [Funktionsweise](#funktionsweise)
5. [Elementketten bauen](#elementketten-bauen)
   - [Attribut-AI benötigen ihren Identifikationsschlüssel](#attribut-ai-benötigen-ihren-identifikationsschlüssel)
6. [Digital-Link-URIs bauen](#digital-link-uris-bauen)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validierung und Fehler](#validierung-und-fehler)
   - [Bau-Methoden, die Ausnahmen werfen](#bau-methoden-die-ausnahmen-werfen)
   - [tryBuild\*-Methoden ohne Ausnahmen](#trybuild-methoden-ohne-ausnahmen)
   - [Sprache der Fehlermeldungen](#sprache-der-fehlermeldungen)
   - [BuildResult](#buildresult)
9. [Prüfziffern](#prüfziffern)
10. [Thread-Sicherheit](#thread-sicherheit)
11. [API-Referenz](#api-referenz)

---

## Überblick

`GaiaBuilder` ist das Gegenstück zu [`GaiaParser`](GaiaParser-German.md): Er macht aus einer Menge von Paaren aus Anwendungskennzeichen (AI) und Wert eine wohlgeformte GS1-**Elementkette** oder einen wohlgeformten **GS1-Digital-Link-URI**. Sie liefern die AI und ihre vollständigen Datenwerte; der Builder setzt sie zusammen, validiert das Ergebnis mit derselben Maschinerie, die auch `GaiaParser` verwendet, und gibt die Ausgabe aus.

Da der Builder validiert, indem er *seine eigene Ausgabe probeweise parst*, lässt sich alles, was er zurückgibt, garantiert fehlerfrei mit `GaiaParser` zurücklesen — beide können nie uneins darüber sein, was wohlgeformt ist.

**Einstiegsklasse:** `tools.pantheum.gaia.GaiaBuilder`

---

## Über GS1 und die General Specifications

**GS1** ist eine weltweit tätige gemeinnützige Organisation, die offene Standards für die Identifikation und den Datenaustausch in Lieferketten entwickelt und pflegt. Ihre Standards werden im Handel, im Gesundheitswesen, in der Logistik, in der Gastronomie und in vielen weiteren Branchen eingesetzt — von Produktstrichcodes auf Verbraucherverpackungen bis zur serialisierten Rückverfolgung pharmazeutischer Dosen.

Die maßgebliche Referenz für alles, was dieser Builder umsetzt, sind die **GS1 General Specifications** — ein einziges Dokument, das Folgendes festlegt:

- Alle Codes der Anwendungskennzeichen (AI), ihre Datentitel, Formate und Validierungsregeln
- Die Syntaxregeln zum Bilden und Codieren von AI-Elementketten
- Die Anforderungen an Strichcode-Symbologien und die Vergabe der AIM-Symbologiekennungen
- Die Algorithmen für Prüfziffern und Prüfzeichen
- Die Auflösung zweistelliger Jahreszahlen (die Schiebefensterregel)
- Die Spezifikationen für Data Matrix, QR Code, GS1-128, GS1 DataBar und weitere Datenträger

Die GS1 General Specifications werden jährlich aktualisiert. Die aktuelle Ausgabe und begleitende Materialien sind verfügbar unter:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA setzt **Release 26.0 (ratifiziert im Januar 2026)** der GS1 General Specifications um.

GS1-Digital-Link-URIs unterliegen einem begleitenden Standard, **GS1 Digital Link: URI Syntax**, der die primären Identifikationsschlüssel, die Reihenfolge der Schlüsselqualifizierer und die Codierung der Datenattribute festlegt, die der Builder beim Erzeugen von Digital-Link-URIs anwendet:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA setzt **Release 1.7.0 (ratifiziert im August 2026)** des Standards GS1 Digital Link: URI Syntax um.

Abschnittsverweise in diesem Dokument beziehen sich auf die GS1 General Specifications (z. B. „Table 7-5“, „section 7.12“), mit Ausnahme der Digital-Link-Abschnittsnummern (z. B. „§4.9“, „§4.12“), die sich auf den Standard GS1 Digital Link: URI Syntax beziehen.

---

## Schnelleinstieg

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

Ziehen Sie die Konstanten aus `GS1Constants_AICodes` rohen AI-Zeichenketten vor (siehe [Anhang A des Parser-Handbuchs](GaiaParser-German.md#anhang-a--ai-zeichenkettenkonstanten)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Funktionsweise

Jeder Bauvorgang folgt demselben Weg:

1. **Zusammensetzen** — die AI/Wert-Paare werden zu einer Elementketten-Kandidatin verkettet. Nach jedem AI, das *ein Trennzeichen erfordert* und nicht das letzte Element ist, wird ein FNC1-Gruppentrennzeichen (`0x1D`) eingefügt. AI mit vordefinierter Länge (GTIN, Datumsangaben, Maße fester Länge) erhalten kein Trennzeichen; alle anderen schon. (Unbekannte AI erreichen diesen Schritt nie — `ai(...)` weist sie sofort zurück; siehe [Elementketten bauen](#elementketten-bauen).)
2. **Validieren** — die Kandidatin wird im Modus `CONTENT` mit `GaiaParser` geparst. Jeder Wert wird gegen Format und Prüfziffer seines AI geprüft, und die Strukturregeln (erforderliche bzw. ausgeschlossene AI-Kombinationen) werden durchgesetzt. Ist das Parsen nicht gültig, scheitert der Bauvorgang.
3. **Ausgeben** —
   - Bei einer Elementkette wird das `toElementString()` des validierten Objekts zurückgegeben.
   - Bei einem Digital Link wird jedem Element seine DL-Rolle zugewiesen (Primärschlüssel, Schlüsselqualifizierer oder Datenattribut), die Qualifiziererfolge wird validiert, der URI ausgegeben und der ausgegebene URI **erneut geparst, um zu bestätigen, dass er als gültiger Digital Link zurückgelesen werden kann** — eine Absicherung des Zusammensetzens und der Prozentcodierung. Gelingt dieser Rundlauf nicht, wird eine `GaiaBuilderException` geworfen.

Dies bildet die Rekonstruktionslogik von `DLSyntaxParser` nach, sodass Trennzeichensetzung und Validierung genau dem entsprechen, was der Parser erwartet.

---

## Elementketten bauen

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- Das **AI** wird sofort validiert: `ai(...)` wirft eine `IllegalArgumentException`, wenn es kein bekanntes GS1-Anwendungskennzeichen ist. (Der Builder verkettet AI und Wert vor dem Parsen, weshalb ein unbekanntes oder zu langes AI wie `"99999"` hier abgefangen werden muss — sonst würde es stillschweigend zu einem anderen AI umtokenisiert.) Der **Wert** wird später validiert, beim Bauen.
- Werte müssen **vollständig** sein, einschließlich etwaiger Prüfziffer. Der Builder berechnet und ergänzt keine Prüfziffern für Sie — siehe [Prüfziffern](#prüfziffern).
- Die AI werden in der Reihenfolge ausgegeben, in der Sie sie hinzufügen. Der Builder fügt die FNC1-Trennzeichen dort ein, wo die GS1-Syntax sie verlangt; Sie fügen selbst keine ein.
- Ein Bauvorgang **ganz ohne AI** wirft `GaiaBuilderException("No AIs supplied")` mit einer leeren `getErrors()`-Liste — der einzige Fehlschlag ohne jeden `GaiaError`.
- Ein AI, dessen Wert seine Format- oder Prüfziffernregel verletzt, lässt den Bauvorgang scheitern.

### Attribut-AI benötigen ihren Identifikationsschlüssel

Die meisten AI sind *Attribute*, die laut GS1 General Specifications von einem Identifikationsschlüssel begleitet sein müssen, und der Builder setzt das durch — er validiert über die vollständige Syntaxstufe, ohne Möglichkeit zur Abwahl. Eine Charge oder eine Seriennummer allein ist **keine** gültige Elementkette:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Identifikationsschlüssel (GTIN `01`, SSCC `00`, GLN `414`, …) und die betriebsinternen AI (`90`–`99`) dürfen völlig zu Recht allein stehen. Alles andere braucht seinen Begleiter.

> `GaiaParser` lässt sich mit `ParseConfig.skipRequiresCheck(true)` anweisen, diese Prüfung zu überspringen; `GaiaBuilder` bietet bewusst kein Gegenstück — er soll standardkonforme Ausgaben erzeugen. Um eine absichtlich unvollständige Elementkette zusammenzusetzen, verketten Sie sie selbst und parsen sie mit deaktivierter Prüfung.

---

## Digital-Link-URIs bauen

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Ein gültiger Digital Link erfordert genau einen **primären Identifikationsschlüssel** (z. B. GTIN `01`, GLN `414`, SSCC `00`). Der Builder ordnet jedes gelieferte AI ein:

| Rolle | Ausgegeben als | Beispiel |
|------|-------------|---------|
| Primärer Identifikationsschlüssel | Pfadsegment nach Domain bzw. Präfix | `/01/09506000134352` |
| Schlüsselqualifizierer (CPV `22`, Charge `10`, Seriennummer `21`, …) | Folgende Pfadsegmente, in der **kanonischen Reihenfolge nach §4.9** (nicht in der Reihenfolge des Hinzufügens) | `/10/LOT-ABC` |
| Datenattribut (alles Übrige) | Abfrageparameter, **lexikalisch nach AI-Schlüssel sortiert** (§4.12) | `?17=271231` |

Da die Qualifizierer bei der Ausgabe umsortiert werden, ist es unproblematisch, sie in beliebiger Reihenfolge zu liefern — `ai("21", …)` vor `ai("10", …)` ergibt dennoch `/10/LOT/21/SER`. Nur die *Menge* muss für den Primärschlüssel zulässig sein.

Werte werden sowohl im Pfad als auch in der Abfrage prozentcodiert.

Der Bauvorgang **scheitert** (wirft `GaiaBuilderException` bzw. liefert ein fehlgeschlagenes `BuildResult`), wenn:

- unter den AI **kein** primärer Identifikationsschlüssel ist;
- **mehr als ein** primärer Identifikationsschlüssel vorhanden ist;
- ein AI in Digital Links **verboten** ist (`03`, `8014`);
- die **Folge der Schlüsselqualifizierer** für den gewählten Primärschlüssel ungültig ist (etwa ein Qualifizierer, der nicht zu diesem Schlüssel gehört, oder Qualifizierer außerhalb ihrer zulässigen Reihenfolge).

---

## BuilderDigitalLinkConfig

Übergeben Sie eine `BuilderDigitalLinkConfig`, um Schema, Domain, Pfadpräfix, zusätzliche Abfrageparameter und Fragment zu steuern:

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

| Builder-Methode | Zweck | Standard |
|----------------|---------|---------|
| `scheme(String)` | URI-Schema; muss `http` oder `https` sein | `https` |
| `domain(String)` | Autorität — Host oder `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Pfadsegmente vor dem ersten Primärschlüssel; führende und abschließende Schrägstriche werden normalisiert | *(keines)* |
| `baseUrl(String)` | Komfortmethode, die eine URL in `scheme` + `domain` + `pathPrefix` zerlegt | — |
| `addQueryParam(String, String)` | Zusätzlicher Abfrageparameter, **nach** den AI-Datenattributen angehängt, in Einfügereihenfolge; prozentcodiert | — |
| `fragment(String)` | URL-Fragment (ohne führendes `#`); prozentcodiert | *(keines)* |

`build()` validiert die Konfiguration sofort: Ein Schema, das nicht `http(s)` ist, oder eine leere Domain werfen `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (Alias `defaultConfig()`) ist der Standard `https://id.gs1.org` ohne Zusätze — genau das, was `buildDigitalLinkUri()` ohne Argument verwendet und was `GS1AIObject.getCanonicalDigitalLink()` erzeugt.
- `baseUrl("http://id.example.org:8080/r")` → Schema `http`, Domain `id.example.org:8080`, Pfadpräfix `/r`.
- Zusätzliche Abfrageparameter folgen stets den aus den AI abgeleiteten Attributen, sodass die kanonische AI-Reihenfolge (§4.12) erhalten bleibt.

`BuilderDigitalLinkConfig` ist unveränderlich; eine Instanz lässt sich beliebig wiederverwenden.

---

## Validierung und Fehler

### Bau-Methoden, die Ausnahmen werfen

`buildElementString()`, `buildDigitalLinkUri()` und `buildDigitalLinkUri(BuilderDigitalLinkConfig)` werfen eine **`GaiaBuilderException`** (eine ungeprüfte `RuntimeException`), wenn die AI keine wohlgeformte Ausgabe bilden können:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Bei **Inhaltsfehlern** (falsche Prüfziffer, Formatabweichung, fehlendes bzw. ausgeschlossenes AI) trägt `getErrors()` die `GaiaError` des Parsers — dieselben Objekte, die [im Parser-Handbuch beschrieben sind](GaiaParser-German.md#gaiaerror).
- Bei **strukturellen Digital-Link-Fehlern** (kein Primärschlüssel, mehr als ein Primärschlüssel, verbotenes AI, ungültige Qualifiziererfolge) trägt `getErrors()` einen einzelnen `GaiaError` (Code `GE-L008`, `GE-L012`, `GE-L013` oder `GE-L014`), lokalisiert in der Sprache des Builders.

### tryBuild\*-Methoden ohne Ausnahmen

Stammt die Eingabe von Nutzenden und ist ein Fehlschlag ein erwartetes, behebbares Ergebnis, verwenden Sie die `tryBuild*`-Varianten statt eines Ausnahmen-Kontrollflusses. Sie liefern ein [`BuildResult`](#buildresult), statt zu werfen:

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

| Mit Ausnahme | Ohne Ausnahme |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Jede `tryBuild*`-Methode teilt denselben Validierungskern wie ihr werfender Zwilling; nur die Fehlergrenze unterscheidet sich.

### Sprache der Fehlermeldungen

Fehler der Inhaltsvalidierung stammen aus dem lokalisierten Fehlerkatalog. Rufen Sie `language(...)` auf, um die Sprache der `GaiaError`-Meldungen zu wählen, die `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` tragen; Standard ist Englisch:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Es ist dieselbe `GaiaConstants.Language`-Einstellung, die `GaiaParser` über `ParseConfig` entgegennimmt; Builder und Parser lokalisieren also identisch.

Sowohl die **Inhalts**-`GaiaError`-Meldungen als auch die **strukturellen Digital-Link**-Fehlschläge (kein Primärschlüssel, mehr als ein Primärschlüssel, verbotenes AI, ungültige Qualifiziererfolge) werden über den gemeinsamen Fehlerkatalog lokalisiert — Letztere über die Codes `GE-L008`, `GE-L012`, `GE-L013` und `GE-L014`.

### BuildResult

`BuildResult` (im Paket `tools.pantheum.gaia.result`) ist ein unveränderlicher Werttyp, der den Ausgang eines `tryBuild*`-Aufrufs beschreibt:

| Methode | Bei Erfolg | Bei Fehlschlag |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | die erzeugte Zeichenkette | `null` |
| `getMessage()` | `null` | Beschreibung des Fehlschlags |
| `getErrors()` | leere Liste | die Validierungsfehler (dieselben wie bei `GaiaBuilderException.getErrors()`) |

---

## Prüfziffern

Der Builder validiert Prüfziffern, berechnet sie aber **nicht** — die Werte müssen ihre Prüfziffer bereits enthalten. Um eine zu berechnen, verwenden Sie `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` wendet den üblichen GS1-Modulo-10-Algorithmus auf die gelieferten Ziffern an und gibt die Prüfziffer `0–9` zurück, oder `-1`, wenn die Eingabe null, leer oder nicht numerisch ist.

---

## Thread-Sicherheit

`GaiaBuilder` ist **nicht** threadsicher und für den einmaligen Gebrauch gedacht: `create()` aufrufen, AI hinzufügen, einmal bauen. Erzeugen Sie je Ausgabe einen neuen Builder; teilen Sie keinen über Threads hinweg.

`BuilderDigitalLinkConfig` (und die davon erzeugten `BuildResult`) sind unveränderlich und dürfen beliebig geteilt werden — bauen Sie eine Konfiguration einmal beim Start und verwenden Sie sie für viele Builder wieder.

---

## API-Referenz

### `GaiaBuilder`

| Methode | Beschreibung |
|--------|-------------|
| `static GaiaBuilder create()` | Startet einen neuen, leeren Builder. |
| `GaiaBuilder ai(String ai, String value)` | Fügt ein AI und seinen vollständigen Wert an. Wirft `IllegalArgumentException`, wenn eines von beiden `null` ist oder `ai` kein bekanntes GS1-Anwendungskennzeichen ist. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Legt die Sprache der Fehlermeldungen der Inhaltsvalidierung fest (Standard Englisch). `null` wird ignoriert. |
| `String buildElementString()` | Erzeugt eine GS1-Elementkette. Wirft bei Fehlschlag `GaiaBuilderException`. |
| `String buildDigitalLinkUri()` | Erzeugt einen kanonischen Digital-Link-URI. Wirft bei Fehlschlag `GaiaBuilderException`. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Erzeugt einen Digital-Link-URI gemäß `config`. Wirft bei Fehlschlag `GaiaBuilderException`. |
| `BuildResult tryBuildElementString()` | Bau einer Elementkette ohne Ausnahmen. |
| `BuildResult tryBuildDigitalLinkUri()` | Bau eines kanonischen Digital Link ohne Ausnahmen. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Bau eines Digital Link gemäß `config`, ohne Ausnahmen. |

### `BuilderDigitalLinkConfig`

| Element | Beschreibung |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Der Standard `https://id.gs1.org`. |
| `static Builder builder()` | Ein neuer Konfigurations-Builder. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Aufgelöstes Schema, aufgelöste Autorität und aufgelöstes Pfadpräfix. |
| `getExtraQueryParams()` | Zusätzliche Abfrageparameter, in Einfügereihenfolge. |
| `getFragment()` | Fragment, oder `null`. |

### `GaiaBuilderException`

| Element | Beschreibung |
|--------|-------------|
| `getErrors()` | Die `GaiaError`, die den Fehlschlag verursacht haben — bei einem Inhaltsfehler die Fehler des Parsers, sonst ein einzelner struktureller Digital-Link-Fehler (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nie `null`. |

### `BuildResult`

| Element | Beschreibung |
|--------|-------------|
| `isSuccess()` | Ob der Bauvorgang erfolgreich war. |
| `getValue()` | Die erzeugte Ausgabe bei Erfolg; `null` bei Fehlschlag. |
| `getMessage()` | Die Beschreibung des Fehlschlags bei Fehlschlag; `null` bei Erfolg. |
| `getErrors()` | Die Validierungsfehler bei Fehlschlag; leer bei Erfolg. Nie `null`. |
| `getTiming()` | Das `ProcessingTiming` des Bauvorgangs (Startzeit, Verarbeitungsdauer), oder `null`. |

---

Siehe auch: **[GaiaParser — Entwicklerhandbuch](GaiaParser-German.md)** für die Parse-Seite, das AI-Elementmodell, die Fehlerreferenz und die Anhänge mit den AI- und Interpretationskonstanten.
