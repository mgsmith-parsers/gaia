# GaiaBuilder — Guida per sviluppatori

## Indice

1. [Panoramica](#panoramica)
2. [Informazioni su GS1 e sulle General Specifications](#informazioni-su-gs1-e-sulle-general-specifications)
3. [Avvio rapido](#avvio-rapido)
4. [Come funziona](#come-funziona)
5. [Costruire stringhe di elementi](#costruire-stringhe-di-elementi)
   - [Gli AI di attributo richiedono la propria chiave di identificazione](#gli-ai-di-attributo-richiedono-la-propria-chiave-di-identificazione)
6. [Costruire URI Digital Link](#costruire-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validazione ed errori](#validazione-ed-errori)
   - [Metodi di costruzione che sollevano eccezioni](#metodi-di-costruzione-che-sollevano-eccezioni)
   - [Metodi tryBuild\* senza eccezioni](#metodi-trybuild-senza-eccezioni)
   - [Lingua dei messaggi di errore](#lingua-dei-messaggi-di-errore)
   - [BuildResult](#buildresult)
9. [Cifre di controllo](#cifre-di-controllo)
10. [Sicurezza rispetto ai thread](#sicurezza-rispetto-ai-thread)
11. [Riferimento dell'API](#riferimento-dellapi)

---

## Panoramica

`GaiaBuilder` è l'inverso di [`GaiaParser`](GaiaParser-Italian.md): trasforma un insieme di coppie identificatore di applicazione (AI) / valore in una **stringa di elementi** GS1 o in un **URI GS1 Digital Link** ben formati. Voi fornite gli AI e i loro valori di dato completi; il builder li assembla, valida il risultato con lo stesso motore usato da `GaiaParser` e genera l'output.

Poiché il builder valida *analizzando il proprio output candidato*, tutto ciò che restituisce è garantito analizzabile senza errori da `GaiaParser`: i due non possono mai discordare su che cosa sia ben formato.

**Classe di ingresso:** `tools.pantheum.gaia.GaiaBuilder`

---

## Informazioni su GS1 e sulle General Specifications

**GS1** è un'organizzazione mondiale senza scopo di lucro che sviluppa e mantiene standard aperti per l'identificazione e lo scambio di dati nelle catene di fornitura. I suoi standard sono impiegati nella distribuzione, nella sanità, nella logistica, nella ristorazione e in molti altri settori, e coprono tutto: dai codici a barre sulle confezioni al consumo alla tracciabilità serializzata delle dosi farmaceutiche.

Il riferimento autorevole per tutto ciò che questo builder implementa è il documento **GS1 General Specifications** — un unico documento che definisce:

- Tutti i codici degli identificatori di applicazione (AI), i loro titoli di dato, i formati e le regole di validazione
- Le regole di sintassi per comporre e codificare le stringhe di elementi AI
- I requisiti di simbologia dei codici a barre e l'assegnazione degli identificatori di simbologia AIM
- Gli algoritmi della cifra di controllo e del carattere di controllo
- La risoluzione degli anni a due cifre (la regola della finestra scorrevole)
- Le specifiche di Data Matrix, QR Code, GS1-128, GS1 DataBar e degli altri supporti dati

Le GS1 General Specifications sono aggiornate ogni anno. L'edizione in vigore e le risorse collegate sono disponibili all'indirizzo:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementa la **release 26.0 (ratificata a gennaio 2026)** delle GS1 General Specifications.

Gli URI GS1 Digital Link sono regolati da uno standard complementare, **GS1 Digital Link: URI Syntax**, che definisce le chiavi di identificazione primarie, l'ordine dei qualificatori di chiave e la codifica degli attributi di dato che il builder applica nel generare gli URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementa la **release 1.7.0 (ratificata ad agosto 2026)** dello standard GS1 Digital Link: URI Syntax.

In tutto questo documento i riferimenti alle sezioni rimandano alle GS1 General Specifications (per esempio «Table 7-5», «section 7.12»), a eccezione dei numeri di sezione Digital Link (per esempio «§4.9», «§4.12»), che rimandano allo standard GS1 Digital Link: URI Syntax.

---

## Avvio rapido

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

Preferite le costanti `GS1Constants_AICodes` alle stringhe di AI grezze (si veda l'[appendice A della guida del parser](GaiaParser-Italian.md#appendice-a--costanti-di-stringa-degli-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Come funziona

Ogni costruzione segue lo stesso percorso:

1. **Assemblaggio** — le coppie AI/valore vengono concatenate in una stringa di elementi candidata. Dopo ogni AI che *richiede un separatore* e che non sia l'ultimo elemento viene inserito un separatore di gruppo FNC1 (`0x1D`). Gli AI a lunghezza predefinita (GTIN, date, misure a lunghezza fissa) non prendono separatore; tutti gli altri sì. (Gli AI non riconosciuti non arrivano mai a questo passo: `ai(...)` li respinge subito; si veda [Costruire stringhe di elementi](#costruire-stringhe-di-elementi).)
2. **Validazione** — la candidata viene analizzata in modalità `CONTENT` da `GaiaParser`. Ogni valore è confrontato con il formato e la cifra di controllo del proprio AI, e vengono applicate le regole strutturali (abbinamenti di AI obbligatori o esclusi). Se l'analisi non è valida, la costruzione fallisce.
3. **Generazione** —
   - Per una stringa di elementi viene restituito il `toElementString()` dell'oggetto validato.
   - Per un Digital Link, a ciascun elemento viene assegnato il proprio ruolo DL (chiave primaria, qualificatore di chiave o attributo di dato), la sequenza dei qualificatori di chiave viene validata, l'URI viene emesso e l'URI emesso viene **rianalizzato per confermare che compia un giro di andata e ritorno valido come Digital Link**: un controllo difensivo sull'assemblaggio della stringa e sulla codifica in percentuale. Se il giro di andata e ritorno non riesce, viene sollevata una `GaiaBuilderException`.

Ciò rispecchia la logica di ricostruzione di `DLSyntaxParser`, sicché il posizionamento dei separatori e la validazione sono identici a quanto il parser si attende.

---

## Costruire stringhe di elementi

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- L'**AI** viene validato subito: `ai(...)` solleva `IllegalArgumentException` se non è un identificatore di applicazione GS1 riconosciuto. (Il builder concatena AI e valore prima di analizzare, quindi un AI non riconosciuto o troppo lungo come `"99999"` va intercettato qui: altrimenti verrebbe silenziosamente ri-tokenizzato in un AI diverso.) Il **valore**, invece, è validato più tardi, al momento della costruzione.
- I valori devono essere **completi**, cifra di controllo compresa. Il builder non calcola né aggiunge le cifre di controllo al posto vostro — si veda [Cifre di controllo](#cifre-di-controllo).
- Gli AI sono emessi nell'ordine in cui li aggiungete. Il builder inserisce i separatori FNC1 dove la sintassi GS1 li richiede; non dovete aggiungerli voi.
- Una costruzione **priva di qualsiasi AI** solleva `GaiaBuilderException("No AIs supplied")` con un elenco `getErrors()` vuoto: l'unico fallimento che non porta alcun `GaiaError`.
- Un AI il cui valore violi la regola di formato o di cifra di controllo fa fallire la costruzione.

### Gli AI di attributo richiedono la propria chiave di identificazione

La maggior parte degli AI sono *attributi* che le GS1 General Specifications impongono di accompagnare a una chiave di identificazione, e il builder lo fa rispettare: valida attraverso l'intera fase di sintassi, senza alcuna possibilità di rinuncia. Un lotto o un numero di serie da soli **non** costituiscono una stringa di elementi valida:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Le chiavi di identificazione (GTIN `01`, SSCC `00`, GLN `414`, …) e gli AI a uso interno aziendale (`90`–`99`) possono comparire da soli a pieno titolo. Tutto il resto ha bisogno del proprio accompagnatore.

> A `GaiaParser` si può chiedere di saltare questo controllo con `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` non espone deliberatamente alcun equivalente, poiché è pensato per produrre output conforme agli standard. Per assemblare una stringa di elementi deliberatamente parziale, concatenatela voi stessi e analizzatela con il controllo disattivato.

---

## Costruire URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Un Digital Link valido richiede esattamente una **chiave di identificazione primaria** (per esempio GTIN `01`, GLN `414`, SSCC `00`). Il builder classifica ciascun AI fornito:

| Ruolo | Reso come | Esempio |
|------|-------------|---------|
| Chiave di identificazione primaria | Segmento di percorso dopo il dominio o il prefisso | `/01/09506000134352` |
| Qualificatore di chiave (CPV `22`, lotto `10`, seriale `21`, …) | Segmenti di percorso successivi, nell'**ordine canonico del §4.9** (non nell'ordine in cui li avete aggiunti) | `/10/LOT-ABC` |
| Attributo di dato (tutto il resto) | Parametri di query, **ordinati lessicalmente per chiave di AI** (§4.12) | `?17=271231` |

Poiché i qualificatori vengono riordinati in fase di emissione, fornirli fuori sequenza non è un problema: `ai("21", …)` prima di `ai("10", …)` produce comunque `/10/LOT/21/SER`. Solo l'*insieme* deve essere ammissibile per la chiave primaria.

I valori, sia nel percorso sia nella query, sono codificati in percentuale.

La costruzione **fallisce** (solleva `GaiaBuilderException`, oppure restituisce un `BuildResult` fallito) quando:

- fra gli AI **non** vi è alcuna chiave di identificazione primaria;
- vi è **più di una** chiave di identificazione primaria;
- un AI è **vietato** nei Digital Link (`03`, `8014`);
- la **sequenza dei qualificatori di chiave** non è valida per la chiave primaria scelta (per esempio un qualificatore che non appartiene a quella chiave, oppure qualificatori fuori dall'ordine consentito).

---

## BuilderDigitalLinkConfig

Passate una `BuilderDigitalLinkConfig` per controllare schema, dominio, prefisso di percorso, parametri di query aggiuntivi e frammento:

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

| Metodo del builder | Scopo | Predefinito |
|----------------|---------|---------|
| `scheme(String)` | Schema dell'URI; deve essere `http` o `https` | `https` |
| `domain(String)` | Autorità — host oppure `host:porta` | `id.gs1.org` |
| `pathPrefix(String)` | Segmenti di percorso precedenti la prima chiave primaria; le barre iniziali e finali sono normalizzate | *(nessuno)* |
| `baseUrl(String)` | Scorciatoia che scompone un URL in `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Parametro di query aggiuntivo, accodato **dopo** gli attributi di dato degli AI, nell'ordine di inserimento; codificato in percentuale | — |
| `fragment(String)` | Frammento dell'URL (senza il `#` iniziale); codificato in percentuale | *(nessuno)* |

`build()` valida subito la configurazione: uno schema diverso da `http(s)` o un dominio vuoto sollevano `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) è il valore predefinito `https://id.gs1.org` senza aggiunte: esattamente ciò che usa `buildDigitalLinkUri()` senza argomenti e ciò che produce `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → schema `http`, dominio `id.example.org:8080`, prefisso di percorso `/r`.
- I parametri di query aggiuntivi seguono sempre gli attributi ricavati dagli AI, così da preservare l'ordine canonico degli AI (§4.12).

`BuilderDigitalLinkConfig` è immutabile; riutilizzate liberamente una stessa istanza.

---

## Validazione ed errori

### Metodi di costruzione che sollevano eccezioni

`buildElementString()`, `buildDigitalLinkUri()` e `buildDigitalLinkUri(BuilderDigitalLinkConfig)` sollevano una **`GaiaBuilderException`** (una `RuntimeException` non controllata) quando gli AI non possono formare un output ben formato:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Per i fallimenti **di contenuto** (cifra di controllo errata, formato non conforme, AI mancante o escluso), `getErrors()` porta i `GaiaError` del parser — gli stessi oggetti [descritti nella guida del parser](GaiaParser-Italian.md#gaiaerror).
- Per i fallimenti **strutturali Digital Link** (nessuna chiave primaria, più di una chiave primaria, AI vietato, sequenza di qualificatori di chiave non valida), `getErrors()` porta un unico `GaiaError` (codice `GE-L008`, `GE-L012`, `GE-L013` o `GE-L014`) localizzato nella lingua del builder.

### Metodi tryBuild\* senza eccezioni

Quando l'input proviene dall'utente e il fallimento è un esito atteso e recuperabile, usate le varianti `tryBuild*` anziché un flusso di controllo basato sulle eccezioni. Restituiscono un [`BuildResult`](#buildresult) invece di sollevare:

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

| Con eccezione | Senza eccezione |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Ogni metodo `tryBuild*` condivide lo stesso nucleo di validazione del proprio gemello che solleva eccezioni; cambia soltanto il confine di fallimento.

### Lingua dei messaggi di errore

Gli errori di validazione del contenuto provengono dal catalogo degli errori localizzato. Chiamate `language(...)` per scegliere la lingua dei messaggi dei `GaiaError` portati da `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; il valore predefinito è l'inglese:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

È la stessa impostazione `GaiaConstants.Language` che `GaiaParser` accetta tramite `ParseConfig`: builder e parser si localizzano dunque in modo identico.

Sia i messaggi dei `GaiaError` **di contenuto** sia i fallimenti **strutturali Digital Link** (nessuna chiave primaria, più di una chiave primaria, AI vietato, sequenza di qualificatori di chiave non valida) sono localizzati tramite il catalogo degli errori condiviso — questi ultimi mediante i codici `GE-L008`, `GE-L012`, `GE-L013` e `GE-L014`.

### BuildResult

`BuildResult` (nel package `tools.pantheum.gaia.result`) è un tipo valore immutabile che descrive l'esito di una chiamata `tryBuild*`:

| Metodo | In caso di successo | In caso di fallimento |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | la stringa generata | `null` |
| `getMessage()` | `null` | descrizione del fallimento |
| `getErrors()` | elenco vuoto | gli errori di validazione (gli stessi di `GaiaBuilderException.getErrors()`) |

---

## Cifre di controllo

Il builder valida le cifre di controllo ma **non** le calcola: i valori devono già includere la propria. Per calcolarne una, usate `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` applica l'algoritmo GS1 standard modulo 10 alle cifre fornite e restituisce la cifra di controllo `0–9`, oppure `-1` se l'input è nullo, vuoto o non numerico.

---

## Sicurezza rispetto ai thread

`GaiaBuilder` **non** è sicuro rispetto ai thread ed è pensato per un uso singolo: chiamate `create()`, aggiungete gli AI, costruite una volta. Create un nuovo builder per ogni output; non condividetene uno fra più thread.

`BuilderDigitalLinkConfig` (e i `BuildResult` che produce) sono immutabili e possono essere condivisi liberamente: costruite una configurazione una sola volta all'avvio e riutilizzatela per molti builder.

---

## Riferimento dell'API

### `GaiaBuilder`

| Metodo | Descrizione |
|--------|-------------|
| `static GaiaBuilder create()` | Avvia un nuovo builder, vuoto. |
| `GaiaBuilder ai(String ai, String value)` | Aggiunge un AI e il suo valore completo. Solleva `IllegalArgumentException` se uno dei due è `null`, oppure se `ai` non è un identificatore di applicazione GS1 riconosciuto. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Imposta la lingua dei messaggi di errore della validazione del contenuto (inglese per impostazione predefinita). `null` viene ignorato. |
| `String buildElementString()` | Genera una stringa di elementi GS1. Solleva `GaiaBuilderException` in caso di fallimento. |
| `String buildDigitalLinkUri()` | Genera un URI Digital Link canonico. Solleva `GaiaBuilderException` in caso di fallimento. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Genera un URI Digital Link secondo `config`. Solleva `GaiaBuilderException` in caso di fallimento. |
| `BuildResult tryBuildElementString()` | Costruzione di una stringa di elementi senza eccezioni. |
| `BuildResult tryBuildDigitalLinkUri()` | Costruzione di un Digital Link canonico senza eccezioni. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Costruzione di un Digital Link secondo `config`, senza eccezioni. |

### `BuilderDigitalLinkConfig`

| Membro | Descrizione |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Il valore predefinito `https://id.gs1.org`. |
| `static Builder builder()` | Un nuovo builder di configurazione. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Schema, autorità e prefisso di percorso risolti. |
| `getExtraQueryParams()` | Parametri di query aggiuntivi, nell'ordine di inserimento. |
| `getFragment()` | Frammento, oppure `null`. |

### `GaiaBuilderException`

| Membro | Descrizione |
|--------|-------------|
| `getErrors()` | I `GaiaError` che hanno causato il fallimento — gli errori del parser per un fallimento di contenuto, oppure un unico errore strutturale Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Mai `null`. |

### `BuildResult`

| Membro | Descrizione |
|--------|-------------|
| `isSuccess()` | Se la costruzione è riuscita. |
| `getValue()` | L'output generato in caso di successo; `null` in caso di fallimento. |
| `getMessage()` | La descrizione del fallimento in caso di fallimento; `null` in caso di successo. |
| `getErrors()` | Gli errori di validazione in caso di fallimento; elenco vuoto in caso di successo. Mai `null`. |
| `getTiming()` | Il `ProcessingTiming` della costruzione (ora di inizio, durata dell'elaborazione), oppure `null`. |

---

Si veda anche: **[GaiaParser — Guida per sviluppatori](GaiaParser-Italian.md)** per il versante dell'analisi, il modello di elemento AI, il riferimento degli errori e le appendici delle costanti di AI e di interpretazione.
