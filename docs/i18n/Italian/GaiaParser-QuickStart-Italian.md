# GaiaParser — Guida rapida

Trasformate il payload di un codice a barre GS1 in dati strutturati, validati e leggibili da una persona
in una decina di minuti. Questa è la via breve; la **[guida per sviluppatori di GaiaParser](GaiaParser-Italian.md)** è il
riferimento completo, e **[GaiaBuilder](GaiaBuilder-Italian.md)** copre la direzione inversa
(la costruzione di stringhe di elementi e URI Digital Link).

## Contenuti

1. [Aggiungere Gaia al vostro progetto](#1-aggiungere-gaia-al-vostro-progetto)
2. [Analizzare qualcosa](#2-analizzare-qualcosa)
3. [Leggere il risultato](#3-leggere-il-risultato)
4. [Gestire un'analisi fallita](#4-gestire-unanalisi-fallita)
5. [Due cose che vi metteranno in difficoltà](#5-due-cose-che-vi-metteranno-in-difficoltà)
6. [I prefissi dei lettori e i Digital Link funzionano subito](#6-i-prefissi-dei-lettori-e-i-digital-link-funzionano-subito)
7. [Lavorare di meno: le modalità di analisi](#7-lavorare-di-meno-le-modalità-di-analisi)
8. [Cambiare la lingua e il formato della data](#8-cambiare-la-lingua-e-il-formato-della-data)
9. [Ripulire un input disordinato](#9-ripulire-un-input-disordinato)
10. [Dove proseguire](#10-dove-proseguire)

---

## 1. Aggiungere Gaia al vostro progetto

Gaia non è pubblicato su Maven Central: compilate quindi il modulo principale una volta e installatelo nel vostro
repository locale:

```bash
cd gaia && mvn install
```

Poi dichiaratene la dipendenza:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

È l'intero elenco di dipendenze che dovete scrivere. Il jar è leggero, sicché l'unica
dipendenza con scope di compilazione di Gaia — `com.fasterxml.jackson.core:jackson-databind` — arriva
in modo transitivo; se la vostra build fissa già una versione di Jackson, prevale quella e Gaia la usa.
Gaia ha come destinazione **Java 11**, e lo stesso jar funziona immutato su ogni JVM successiva.

> Saltare la suite di test del modulo principale (`mvn install -DskipTests`) trasforma qualche minuto in pochi
> secondi, mentre muovete i primi passi.

---

## 2. Analizzare qualcosa

Una sola classe, nessuna configurazione:

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

`parse(String)` esegue la catena **completa**: sintassi, validazione del contenuto e interpretazione.
È il valore predefinito giusto: restringetelo più avanti se le misurazioni vi danno un motivo per farlo.

---

## 3. Leggere il risultato

`ParseResult.getAiObject()` contiene gli AI risolti. Raggiungetene uno preciso tramite il suo codice anziché
tramite la posizione:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Ogni elemento porta un elenco di **interpretazioni**: il significato decodificato dietro le cifre grezze,
prodotto dalla fase di interpretazione:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` è localizzato ed è pensato per la visualizzazione. Per *leggere* un valore nel codice, cercatelo
invece tramite la sua chiave di tipo, che è stabile:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI diversi producono chiavi diverse: un GTIN restituisce il proprio prefisso aziendale, il tipo di GTIN e la cifra
di controllo; un prezzo restituisce la valuta e l'importo decimale. L'elenco completo è nell'
[appendice B](GaiaParser-Italian.md#appendice-b--costanti-delle-chiavi-di-interpretazione), e le costanti risiedono
in `GS1Constants_Enricher`. Non tutti gli AI hanno interpretazioni: un lotto a testo libero non ha
nulla da cui ricavarle, e il suo elenco resta quindi vuoto.

---

## 4. Gestire un'analisi fallita

Un payload non valido è un esito normale, non un'eccezione: `parse` non solleva mai eccezioni per dati
GS1 errati:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Ramificate su `getId()`, mai sul messaggio.** I messaggi sono localizzati e la loro formulazione
non è un contratto — e attualmente portano un difetto noto di virgolettatura (il `''` raddoppiato qui sopra),
segnalato nel [riferimento degli errori](GaiaParser-Italian.md#riferimento-degli-errori).

Due domande diverse, due metodi diversi:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Un'analisi smette di scendere non appena una fase fallisce: una cifra di controllo errata vi dà quindi
errori di validazione ma nessuna interpretazione.

### Gli avvisi non rendono non valido un risultato

Alcuni controlli sono meramente indicativi. Un prefisso aziendale GS1 non riconosciuto viene segnalato, ma il payload
resta strutturalmente corretto:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Usate `getIssues()` quando li volete entrambi. Se il vostro flusso deve rifiutare i prefissi sconosciuti, controllate
esplicitamente `getWarnings()`: `isValid()` non lo farà al posto vostro.

---

## 5. Due cose che vi metteranno in difficoltà

### Il separatore GS, e perché ometterlo è peggio di un errore

Un AI a lunghezza variabile si estende fino a un **carattere GS** (ASCII `0x1D`, detto FNC1 nelle
simbologie di codici a barre) oppure fino alla fine della stringa. Quando lo segue un altro AI, quel separatore è
obbligatorio:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Ometterlo **non** produce un errore: produce una risposta sbagliata data con piena sicurezza:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

L'AI `10` è `X..20`, e quindi inghiotte legittimamente `LOT-ABC21SN-98765`, e il parser non ha alcun
modo di sapere che non era ciò che si voleva. Nulla a valle può rimediare: mettete dunque a posto il separatore
già alla sorgente — leggete i byte del lettore come **ISO-8859-1** affinché `0x1D` sopravviva, e scrivete
`""` nei letterali stringa Java. Gli AI a lunghezza fissa (`01`, `17`, `3103`) non richiedono separatore:
il parser ne conosce la lunghezza.

### La maggior parte degli AI non può comparire da sola

Lotto, numero di serie, scadenza e affini sono *attributi*: le GS1 General Specifications
impongono che viaggino insieme a una chiave di identificazione, e Gaia lo fa rispettare.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Aggiungete il GTIN e l'analisi passa. Se avete davvero bisogno di analizzare un frammento — un test unitario, una
lettura parziale — disattivate il controllo:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. I prefissi dei lettori e i Digital Link funzionano subito

Non dovete dire a Gaia che forma abbia l'input: le riconosce tutte e quattro. Dategli
ciò che il lettore vi ha consegnato, così com'è.

**Un prefisso di identificatore di simbologia AIM** individua la simbologia e viene rimosso automaticamente:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Un URI GS1 Digital Link** attraversa la stessa validazione e lo stesso arricchimento:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Poiché entrambe le forme confluiscono nello stesso `GS1AIObject`, il codice che consuma una lettura non deve
preoccuparsi di quale sia arrivata — e `toElementString()` / `getCanonicalDigitalLink()`
convertono dall'una all'altra.

Anche un **prefisso di correlazione di 8 cifre** (`12345678~…`) viene rimosso e conservato in
`getCorrelationInfo()`, se la vostra catena di elaborazione ne usa uno.

---

## 7. Lavorare di meno: le modalità di analisi

L'impostazione predefinita fa tutto. Chiedete di meno quando vi serve solo una parte della risposta:

| Modalità | Risponde alla domanda | Costo |
|---|---|---|
| `DATA_CARRIER` | Di quale simbologia si tratta? | Il più basso — nessuna analisi degli AI, `getAiObject()` è `null` |
| `SYNTAX` | I codici AI e le lunghezze sono ben formati? | Niente cifre di controllo, niente interpretazioni |
| `CONTENT` | Sono dati GS1 validi? | Validazione completa, niente interpretazioni |
| `INTERPRETATION` | Che cosa significano? | **Predefinita** — tutto |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Ricorrete a `CONTENT` quando validate a volume e non mostrate mai la scomposizione, e a
`DATA_CARRIER` quando dovete soltanto instradare una lettura verso il gestore giusto.

---

## 8. Cambiare la lingua e il formato della data

I messaggi di errore, le etichette di interpretazione e le descrizioni degli AI sono tradotti in **35
lingue**; le date si presentano come preferite. Tutto questo sta in un'unica `ParseConfig` immutabile:

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

I valori non vengono mai localizzati — lo sono solo etichette, descrizioni e messaggi —, sicché `"2026-12-31"` e
`"09506000134352"` significano la stessa cosa in ogni lingua. Costruite la configurazione una volta all'avvio
e condividetela; è immutabile.

---

## 9. Ripulire un input disordinato

Se la vostra sorgente emette parentesi HRI stampate o spazi sparsi, il modulo principale include due
**modificatori di input** che riparano il payload prima dell'analisi:

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

Nulla è attivo per impostazione predefinita, ed entrambi hanno delle riserve: lo spazio e le parentesi sono
caratteri di dato GS1 leciti, applicateli quindi soltanto a una sorgente che conoscete. Si veda
[Modificatori integrati](GaiaParser-Italian.md#modificatori-integrati), che spiega anche perché la rimozione delle
parentesi debba ripristinare il separatore che esse implicavano.

---

## 10. Dove proseguire

- **[Guida per sviluppatori di GaiaParser](GaiaParser-Italian.md)** — la catena di elaborazione in dettaglio, il modello
  di risultato completo, tutti i codici di errore e le appendici degli AI e delle chiavi di interpretazione.
- **[Guida per sviluppatori di GaiaBuilder](GaiaBuilder-Italian.md)** — costruire stringhe di elementi e URI Digital
  Link a partire da coppie AI/valore.
- **[Riferimento HTTP dell'API Gaia](../../gaia-api-reference.md)** — lo stesso motore via HTTP, se preferite
  non incorporare la libreria.
- **[ai-codes.txt](../../ai-codes.txt)** — un elenco piatto `(AI) TITOLO` per consultazioni rapide.

### La versione in cinque righe

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
