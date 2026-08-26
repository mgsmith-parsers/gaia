# GAIA (GS1 Application Identifiers Analyser) — Guida per sviluppatori

## Indice

1. [Panoramica](#panoramica)
2. [Informazioni su GS1 e sulle General Specifications](#informazioni-su-gs1-e-sulle-general-specifications)
3. [Identificatori di applicazione GS1](#identificatori-di-applicazione-gs1)
4. [Avvio rapido](#avvio-rapido)
5. [Catena di elaborazione dell'analisi](#catena-di-elaborazione-dellanalisi)
   - [Fase preliminare — modificatori di input](#fase-preliminare--modificatori-di-input)
   - [Fase 0 — identificatore di correlazione](#fase-0--identificatore-di-correlazione)
   - [Fase 1 — instradamento dell'input](#fase-1--instradamento-dellinput)
   - [Fase 2 — sintassi](#fase-2--sintassi)
   - [Fase 3 — contenuto](#fase-3--contenuto)
   - [Fase 4 — interpretazione](#fase-4--interpretazione)
6. [Configurazione dell'analisi (`ParseConfig`)](#configurazione-dellanalisi-parseconfig)
   - [Opzioni](#opzioni)
   - [Messaggi ed etichette localizzati](#messaggi-ed-etichette-localizzati)
   - [Formattazione delle date](#formattazione-delle-date)
7. [Modificatori di input](#modificatori-di-input)
   - [Modificatori integrati](#modificatori-integrati)
   - [Scrivere un modificatore](#scrivere-un-modificatore)
   - [Registrare i modificatori](#registrare-i-modificatori)
   - [Esaminare che cosa ha fatto un modificatore](#esaminare-che-cosa-ha-fatto-un-modificatore)
   - [Gestione degli errori di un modificatore](#gestione-degli-errori-di-un-modificatore)
8. [Modalità di analisi](#modalità-di-analisi)
   - [Modalità DATA_CARRIER](#modalità-data_carrier)
   - [Modalità SYNTAX](#modalità-syntax)
   - [Modalità CONTENT](#modalità-content)
   - [Modalità INTERPRETATION (predefinita)](#modalità-interpretation-predefinita)
9. [Identificatore di correlazione](#identificatore-di-correlazione)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Lavorare con i risultati](#lavorare-con-i-risultati)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry e DataCarrierType](#datacarrierentry-e-datacarriertype)
12. [Riferimento degli errori](#riferimento-degli-errori)
13. [Sicurezza rispetto ai thread](#sicurezza-rispetto-ai-thread)
14. [Appendice A — costanti di stringa degli AI](#appendice-a--costanti-di-stringa-degli-ai)
    - [Identificazione e serializzazione](#identificazione-e-serializzazione)
    - [Date e orari](#date-e-orari)
    - [Quantità e misura — misura variabile (metrico)](#quantità-e-misura--misura-variabile-metrico)
    - [Quantità e misura — misura variabile (imperiale / USA)](#quantità-e-misura--misura-variabile-imperiale--usa)
    - [Prezzi e importi monetari](#prezzi-e-importi-monetari)
    - [Luogo e spedizione](#luogo-e-spedizione)
    - [Attributi di prodotto e tracciabilità](#attributi-di-prodotto-e-tracciabilità)
    - [Numeri nazionali di rimborso sanitario (NHRN)](#numeri-nazionali-di-rimborso-sanitario-nhrn)
    - [Sanità, GMN, HIDRI, CPID, dati personali](#sanità-gmn-hidri-cpid-dati-personali)
    - [Uso interno / aziendale](#uso-interno--aziendale)
15. [Appendice B — costanti delle chiavi di interpretazione](#appendice-b--costanti-delle-chiavi-di-interpretazione)
    - [Data e ora](#data-e-ora)
    - [Data di raccolta](#data-di-raccolta)
    - [Prefisso aziendale GS1](#prefisso-aziendale-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Paese (ISO 3166)](#paese-iso-3166)
    - [Valuta (ISO 4217)](#valuta-iso-4217)
    - [Temperatura](#temperatura)
    - [Sesso (ISO 5218)](#sesso-iso-5218)
    - [Specie acquatiche (FAO)](#specie-acquatiche-fao)
    - [Numero di catalogo NATO (NSN)](#numero-di-catalogo-nato-nsn)
    - [Prodotti in rotolo](#prodotti-in-rotolo)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identificativi SIM (EID / ICCID)](#identificativi-sim-eid--iccid)
    - [Riferimento di certificazione](#riferimento-di-certificazione)
    - [GS1 UIC](#gs1-uic)
    - [Ordine di nascita del neonato](#ordine-di-nascita-del-neonato)
    - [Numero globale di modello (GMN)](#numero-globale-di-modello-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Valori decimali e di misura](#valori-decimali-e-di-misura)
    - [Coordinate geografiche](#coordinate-geografiche)
    - [Metodo di produzione](#metodo-di-produzione)
    - [Tipo di supporto AIDC](#tipo-di-supporto-aidc)
    - [Pezzo sul totale](#pezzo-sul-totale)
    - [Suddivisioni in componenti](#suddivisioni-in-componenti)
    - [Varie](#varie)

---

## Panoramica

`GaiaParser` è il punto di ingresso per l'analisi delle stringhe di elementi con identificatori di applicazione (AI) GS1. Accetta l'output grezzo di un lettore in una qualsiasi delle forme seguenti e restituisce un `ParseResult` strutturato contenente tutti gli AI risolti, gli errori di validazione e, facoltativamente, le interpretazioni leggibili da una persona:

- Stringa di elementi AI semplice: `0109506000134352`
- Stringa di elementi preceduta dall'identificatore di simbologia AIM: `]C10109506000134352`
- URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Una qualsiasi delle forme precedenti, facoltativamente preceduta da un identificatore di correlazione di 8 cifre: `12345678~0109506000134352`

**Classe di ingresso:** `tools.pantheum.gaia.GaiaParser`

> **Alle prime armi con Gaia?** Iniziate dalla **[guida rapida di GaiaParser](GaiaParser-QuickStart-Italian.md)** — la dipendenza, una prima analisi e le poche insidie ricorrenti, in una decina di minuti. Questa guida è il riferimento completo.

> Per l'operazione inversa — la *costruzione* di stringhe di elementi e URI Digital Link ben formati a partire da coppie AI/valore — si veda la **[guida per sviluppatori di GaiaBuilder](GaiaBuilder-Italian.md)**.

---

## Informazioni su GS1 e sulle General Specifications

**GS1** è un'organizzazione mondiale senza scopo di lucro che sviluppa e mantiene standard aperti per l'identificazione e lo scambio di dati nelle catene di fornitura. I suoi standard sono impiegati nella distribuzione, nella sanità, nella logistica, nella ristorazione e in molti altri settori, e coprono tutto: dai codici a barre sulle confezioni al consumo alla tracciabilità serializzata delle dosi farmaceutiche.

Il riferimento autorevole per tutto ciò che questo parser implementa è il documento **GS1 General Specifications** — un unico documento che definisce:

- Tutti i codici degli identificatori di applicazione (AI), i loro titoli di dato, i formati e le regole di validazione
- Le regole di sintassi per comporre e codificare le stringhe di elementi AI
- I requisiti di simbologia dei codici a barre e l'assegnazione degli identificatori di simbologia AIM
- Gli algoritmi della cifra di controllo e del carattere di controllo
- La risoluzione degli anni a due cifre (la regola della finestra scorrevole)
- Le specifiche di Data Matrix, QR Code, GS1-128, GS1 DataBar e degli altri supporti dati

Le GS1 General Specifications sono aggiornate ogni anno. L'edizione in vigore e le risorse collegate sono disponibili all'indirizzo:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementa la **release 26.0 (ratificata a gennaio 2026)** delle GS1 General Specifications.

Gli URI GS1 Digital Link sono regolati da uno standard complementare, **GS1 Digital Link: URI Syntax**, che definisce le chiavi di identificazione primarie, l'ordine dei qualificatori di chiave e la codifica degli attributi di dato che il parser applica agli input di tipo Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementa la **release 1.7.0 (ratificata ad agosto 2026)** dello standard GS1 Digital Link: URI Syntax.

In tutto questo documento i riferimenti alle sezioni rimandano alle GS1 General Specifications (per esempio «Table 7-5», «section 7.12»), a eccezione dei numeri di sezione Digital Link (per esempio «§4.9», «§4.12»), che rimandano allo standard GS1 Digital Link: URI Syntax.

---

## Identificatori di applicazione GS1

Un **identificatore di applicazione (AI) GS1** è un breve prefisso numerico — da due a quattro cifre — che stabilisce il significato e il formato del dato che lo segue immediatamente. Gli AI sono definiti nelle GS1 General Specifications e coprono un'ampia gamma di dati della catena di fornitura: identificativi di prodotto, date, quantità, numeri di lotto, numeri di serie, misure, URL e altro ancora.

### Struttura di un elemento AI

Ogni elemento AI si compone di due parti:

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

Il codice AI è sempre numerico. Il valore del dato segue immediatamente, senza alcun delimitatore fra codice e valore.

### AI a lunghezza fissa e a lunghezza variabile

Gli AI si dividono in due categorie:

| Tipo | Comportamento | Esempio |
|---|---|---|
| **Lunghezza fissa** | Numero esatto di caratteri, sempre consumato per intero | AI `01` (GTIN) — sempre 14 cifre |
| **Lunghezza variabile** | Da 1 carattere fino a un massimo; termina con un separatore GS o con la fine dell'input | AI `10` (lotto) — da 1 a 20 caratteri alfanumerici |

Che un AI sia a lunghezza fissa o variabile discende unicamente dalla sua definizione nella specifica GS1: il parser non tira mai a indovinare.

### Stringhe di elementi con più AI

Più AI possono essere concatenati in un'unica stringa di elementi. Gli AI a lunghezza fissa possono essere concatenati direttamente, perché il parser sa sempre esattamente quanti caratteri consumare. Gli AI a lunghezza variabile devono essere chiusi dal **carattere GS** (ASCII `0x1D`, detto anche FNC1 nelle simbologie di codici a barre) ogni volta che li segue un altro AI, affinché il parser sappia dove finisce un valore e dove comincia il codice AI successivo.

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

Nei letterali stringa Java, scrivete il carattere GS con la sequenza di escape Unicode `""`.

### AI di uso comune

| AI | Titolo di dato | Formato | Valore di esempio |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (AAMMGG) | `261231` |
| `17` | USE BY or EXPIRY | N6 (AAMMGG) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, area monetaria unica) |
| `710` | NHRN PZN | X..20 | `12345678` |

> La **quarta cifra** di un AI di misura o di prezzo a 4 cifre codifica il numero di decimali impliciti: `3103` è il peso netto in kg con 3 decimali (`001500` = 1,500 kg), mentre `3102` leggerebbe le stesse cifre come 15,00 kg. La colonna `Formato` qui sopra mostra il formato del *dato*; il `getFormatString()` completo di ciascun AI comprende l'AI stesso (per esempio `N4+N6` per `3103`).

### Interpretazione leggibile da una persona (HRI)

La forma leggibile convenzionale racchiude ciascun codice AI fra parentesi, immediatamente prima del suo valore, con uno spazio fra gli elementi:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Il separatore GS non compare nell'HRI. `GS1AIObject.toHriString()` produce questo formato.

### Codici AI a quattro cifre

Alcuni AI usano quattro cifre anziché due. Le prime due individuano la famiglia dell'AI; la terza e/o la quarta veicolano semantica aggiuntiva (per esempio la posizione della virgola decimale implicita negli AI di misura). Il parser risolve automaticamente il codice AI completo a partire dalla stringa di elementi: il chiamante lavora sempre con il codice completo (per esempio `"3102"`, non solo `"31"`).

---

## Avvio rapido

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

> **Separatore GS:** all'interno di una stringa con più AI, gli AI a lunghezza variabile devono essere delimitati dal carattere GS (ASCII `0x1D`). Nei letterali stringa Java usate `""`.

---

## Catena di elaborazione dell'analisi

### Fase preliminare — modificatori di input

Se la `ParseConfig` contiene dei **modificatori di input**, questi vengono eseguiti prima di tutto il resto: prima della rimozione dell'identificatore di correlazione, prima del riconoscimento del supporto dati, prima ancora di entrare nella catena GS1. Ogni modificatore riscrive l'input grezzo per quello successivo, e tutte le fasi descritte più avanti operano sull'output della catena.

Per impostazione predefinita non è configurato alcun modificatore: questa fase preliminare non fa dunque nulla finché non la attivate esplicitamente. Si veda [Modificatori di input](#modificatori-di-input).

---

### Fase 0 — identificatore di correlazione

Prima di qualsiasi elaborazione GS1, `GaiaParser` verifica se l'input inizia con un **prefisso di identificatore di correlazione** facoltativo: esattamente 8 cifre decimali ASCII seguite da una tilde (`~`), per esempio `12345678~`.

Se il prefisso è presente, viene rimosso e conservato come `CorrelationInfo` nel `ParseResult` restituito. Tutte le fasi successive operano sul payload così ripulito. In assenza del prefisso, l'input passa immutato.

Per i dettagli si veda [Identificatore di correlazione](#identificatore-di-correlazione).

---

### Fase 1 — instradamento dell'input

Dopo la rimozione della correlazione, `GaiaParser` verifica se l'input (ripulito) inizia con un **identificatore di simbologia AIM**: un prefisso di tre caratteri nella forma `]` + lettera ASCII + cifra ASCII (per esempio `]C1` per GS1-128, `]d2` per GS1 DataMatrix, `]e0` per GS1 DataBar / GS1 Composite).

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

Se il supporto dati non è compatibile con gli AI GS1 (per esempio un codice a barre postale), l'analisi si interrompe immediatamente con un errore `GE-D002`.

---

### Fase 2 — sintassi

Viene eseguita sempre. Si compone di due sotto-fasi:

**2a. Suddivisione in token (`AISyntaxParser`)**
- Legge la lunghezza del codice AI dai primi due caratteri, servendosi della tabella dei prefissi GS1 (GS1 General Specifications, tabella 7-5).
- Gli AI a lunghezza fissa consumano dall'input un numero esatto di byte.
- Gli AI a lunghezza variabile vengono letti fino a un carattere GS o fino alla fine dell'input.
- Negli AI a più componenti il blocco di valore viene suddiviso in segmenti, uno per componente.

**2b. Validazione strutturale (`SyntaxValidator`)**
- Rileva gli AI duplicati (`GE-S004`).
- Verifica le dipendenze fra AI obbligatorie, per esempio l'AI `02` che richiede l'AI `37` (`GE-S005`).
- Verifica gli abbinamenti di AI esclusi (`GE-S006`).

Gli errori di questa fase hanno livello `SYNTAX_ERROR` (suddivisione in token) o `INTEGRITY_ERROR` (struttura). Se è presente **anche un solo** errore — di suddivisione o di struttura — la catena si arresta e le fasi di contenuto e di interpretazione vengono saltate.

---

### Fase 3 — contenuto

Viene eseguita solo se la fase 2 non ha prodotto errori (né di suddivisione né di struttura). Catena applicata a ciascun elemento (ogni passo viene eseguito solo se il precedente non ha prodotto errori):

| Passo | Validatore | Codici di errore |
|---|---|---|
| Verifica tramite espressione regolare | `RegexValidator` | `GE-C001` |
| Set di caratteri e formato dei componenti | `ComponentValidator` | `GE-C005` + codici di formato per condizione (`GE-C054`–`GE-C115`) |
| Cifra di controllo / carattere di controllo | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validazione semantica personalizzata | `ContentValidatorRegistry` | codici di contenuto per condizione (`GE-C116`–`GE-C170`) |

Gli errori di questa fase hanno livello `FORMAT_ERROR` o `DATA_ERROR`, con un'eccezione: i
controlli sul prefisso aziendale GS1 negli AI che portano una chiave GS1 sono meramente indicativi e hanno livello `WARNING` (si veda il
[Riferimento degli errori](#riferimento-degli-errori)); un prefisso aziendale non riconosciuto non rende quindi
di per sé non valido il risultato.

---

### Fase 4 — interpretazione

Viene eseguita solo in modalità `INTERPRETATION` e soltanto quando nessun elemento porta un errore proveniente da una fase precedente. L'`InterpretationEngine` arricchisce ciascun elemento con metadati etichettati:

- Date riformattate come `gg/mm/aaaa`
- Scomposizione della cifra di controllo del GTIN e ricerca del prefisso aziendale GS1
- Nomi di Paese ISO 3166
- Nomi e simboli di valuta ISO 4217
- Importi decimali decodificati
- Frammenti di HRI (interpretazione leggibile da una persona)

I risultati vengono allegati come voci `GS1AIInterpretation` a ciascun `GS1AIObjectElement`.

---

## Configurazione dell'analisi (`ParseConfig`)

`GaiaParser` espone esattamente due punti di ingresso:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` viene eseguito con la **configurazione predefinita**: modalità `INTERPRETATION`, date in ordine crescente (`gg/mm/aaaa`) con separatore `/` e anno a quattro cifre, e messaggi di errore in **inglese**. Per modificare uno qualsiasi di questi aspetti — compresa la modalità di analisi — costruite una `ParseConfig` con il suo builder fluente e usate l'overload a due argomenti.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Tutte le enumerazioni delle opzioni risiedono in `GaiaConstants`.

### Opzioni

| Metodo del builder | Enumerazione (`GaiaConstants`) | Predefinito | Effetto |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Profondità della catena — si veda [Modalità di analisi](#modalità-di-analisi). |
| `language(...)`      | `Language`      | `ENGLISH`        | Lingua dei messaggi di errore, delle etichette di interpretazione **e** delle descrizioni degli AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Ordine dei componenti della data: `LITTLE` (`gg/mm/aaaa`), `MIDDLE` (`mm/gg/aaaa`), `BIG` (`aaaa/mm/gg`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Carattere fra i componenti della data: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) o `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) o `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Salta il controllo strutturale «richiede» (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Salta il controllo strutturale «esclude» (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nome di classe | nessuno | Codice che riscrive l'input grezzo prima dell'analisi — due [modificatori integrati](#modificatori-integrati) più tutto ciò che scrivete voi. Si veda [Modificatori di input](#modificatori-di-input). |

Le quattro opzioni relative alle date incidono soltanto sulle stringhe di data formattate prodotte dagli arricchitori di interpretazione (in modalità `INTERPRETATION`); non modificano la validazione. I valori del builder possono essere omessi: qualsiasi opzione non impostata (o a cui si passi `null`) conserva il proprio valore predefinito.

### Messaggi ed etichette localizzati

`language(...)` seleziona la lingua di **tre** categorie di testo leggibile da una persona: i messaggi di errore, le etichette di interpretazione (il `getLabel()` di ogni `GS1AIInterpretation`) e le descrizioni degli AI (il `getDescription()` di ogni `GS1AIObjectElement`).

`GaiaConstants.Language` definisce **35 lingue**, che coprono le lingue più parlate al mondo: inglese, francese, spagnolo, tedesco, italiano, portoghese, olandese, polacco, russo, ucraino, ceco, svedese, cinese, giapponese, coreano, arabo, indonesiano, hindi, turco, bengalese, urdu, vietnamita, pidgin nigeriano, arabo egiziano, marathi, telugu, tamil, cantonese, wu, tagalog, persiano, hausa, punjabi, giavanese e swahili.

Stato delle traduzioni (nella versione distribuita):
- **Etichette di interpretazione** — tradotte in tutte le lingue.
- **Messaggi di errore** — tradotti in tutte le lingue.
- **Descrizioni degli AI** — tradotte in tutte le lingue tranne l'inglese. L'inglese non costituisce un catalogo a sé: viene letto direttamente dal campo `description` della voce dell'AI in `gs1-application-identifiers.jsonld`, a cui ogni descrizione di AI fa in ultima istanza riferimento.

Il pidgin nigeriano (`NIGERIAN_PIDGIN`), un creolo a base inglese, riutilizza deliberatamente il testo inglese per le etichette di interpretazione e i messaggi di errore. Le descrizioni degli AI sono l'eccezione a questa eccezione: sono tradotte in pidgin autentico anziché riprendere l'inglese, poiché i cataloghi delle descrizioni degli AI sono stati prodotti indipendentemente da quelli delle etichette e dei messaggi. È opportuno che le traduzioni automatiche siano riviste da parlanti nativi prima di affidarvisi in produzione.

Qualsiasi messaggio, etichetta o descrizione mancante dal catalogo di una lingua ricade sull'inglese. Le lingue che si scrivono da destra a sinistra (arabo, urdu, arabo egiziano, persiano) sono memorizzate correttamente come stringhe; la loro resa da destra a sinistra spetta al livello di presentazione.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Le etichette di interpretazione si localizzano allo stesso modo (i valori restano invariati: cambiano solo le etichette):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Le descrizioni degli AI si localizzano allo stesso modo (solo `getTitle()`, per esempio `"GTIN"`, non viene localizzato):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formattazione delle date

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Modificatori di input

Un **modificatore di input** è codice che riscrive la stringa di input grezza prima che Gaia la analizzi. I modificatori esistono per gli input che arrivano già deformati: un lettore che sostituisce il separatore GS con un segnaposto stampabile, un middleware che avvolge il payload in un prefisso proprietario, un sistema host che converte tutto in maiuscolo. Anziché pre-elaborare ogni stringa in ogni punto di chiamata (sbagliando in modo sottile in uno di essi), dichiarate la normalizzazione una volta sola nella `ParseConfig` e lasciate che sia il parser ad applicarla.

I modificatori vengono eseguiti proprio all'inizio di `GaiaParser.parse(...)`: prima della rimozione dell'identificatore di correlazione, prima del riconoscimento dell'identificatore di simbologia AIM, prima della catena GS1. Tutto ciò che sta a valle vede soltanto la stringa riscritta. **Per impostazione predefinita non è configurato nulla**, nemmeno i due [modificatori integrati](#modificatori-integrati): li attivate esplicitamente in ciascuna `ParseConfig`.

**Interfaccia:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modificatori integrati

Il jar principale include due modificatori, in `tools.pantheum.gaia.modifier.custom`. Coprono i due modi in cui un payload GS1 arriva deformato più di frequente — parentesi HRI stampate e trattate come dati, e spazi spuri — cosicché i casi comuni non richiedono alcuna classe personalizzata:

| Classe | `getName()` | Che cosa fa |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Rimuove le parentesi HRI attorno a ciascun AI (`(01)…(10)…`) e ripristina il separatore FNC1 che esse implicavano. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Rimuove tutti gli spazi (`0x20`) dalla stringa di elementi AI. |

Sono normali implementazioni di `ModifierInterface`, senza alcuno statuto speciale: vengono registrate, ordinate, segnalate e mandate in errore esattamente come le vostre:

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

Entrambi sono privi di stato e sicuri rispetto ai thread, sicché una sola istanza può essere condivisa, ed entrambi sono indirizzabili tramite il nome di classe completo per configurazioni esternalizzate (si veda [Registrare i modificatori](#registrare-i-modificatori)).

#### `ModifierRemoveAIBrackets`

L'interpretazione leggibile da una persona di GS1 stampa ogni AI fra parentesi — `(01)09521234543213(10)ABC123` — per pura convenzione tipografica. Un lettore o un middleware configurato per emettere l'HRI trasmette quelle parentesi come dati, e il tokenizzatore non sa affatto che farsene.

Rimuovere le parentesi è solo metà del lavoro. Nell'HRI è la parentesi aperta dell'AI *successivo* a segnare la fine del valore precedente: in forma parentesizzata un AI a lunghezza variabile non ha quindi bisogno di alcun FNC1. Rimuovete ingenuamente le parentesi e quel confine sparisce:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Per questo il modificatore **reinserisce un FNC1 a ogni confine il cui AI precedente sia a lunghezza variabile**, ripristinando esattamente ciò che le parentesi codificavano:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

La lunghezza viene cercata nell'`AiDefinitionRegistry` del parser stesso, sicché vengono trattati tutti gli AI a lunghezza variabile anziché un elenco scritto a mano. Tre casi sono deliberatamente lasciati intatti: un valore che termina già con FNC1 (una sorgente che emette entrambe le convenzioni non riceve un secondo separatore), un codice fra parentesi che non è un AI noto (un AI sconosciuto non dice nulla sulla propria lunghezza) e l'ultimo AI della stringa.

La riscrittura è **idempotente** — riapplicarla al proprio risultato non cambia nulla —, ed è quindi sicura su un flusso misto in cui solo alcuni input sono parentesizzati.

> **Limite.** `(` e `)` sono a loro volta caratteri di dato GS1 validi, e il pattern si riduce a `\((\d{2,4})\)`. Anche un valore che contenga per caso un numero di due o quattro cifre fra parentesi verrebbe spogliato delle parentesi. Applicate questo modificatore soltanto a una sorgente che usi la convenzione delle parentesi HRI, non a valori con parentesi autentiche.

#### `ModifierRemoveSpaces`

Alcuni lettori, middleware e catene di stampa etichette inseriscono spazi spuri in una stringa di elementi per il resto ben formata: per riempire un campo a larghezza fissa, per separare gruppi leggibili o per mandare a capo un valore lungo. Il tokenizzatore tratta ciascuno di essi come un dato, corrompendo il valore in cui si trova e, per un AI a lunghezza variabile, spostando tutto ciò che segue.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Viene rimosso soltanto l'ASCII `0x20`. Gli altri caratteri di spaziatura restano al loro posto: una tabulazione, per esempio, è al di fuori del set di caratteri codificabile GS1, e il parser la segnala quindi come `GE-S008` anziché farla sparire in silenzio.

> **Limite.** Lo spazio (`0x20`) fa parte del set di caratteri invariante GS1: un numero di lotto o un codice articolo cliente può quindi legittimamente contenerne uno. Il modificatore non sa distinguere uno spazio spurio da uno autentico; applicatelo soltanto a una sorgente che sapete non usare spazi all'interno dei propri valori di AI.

#### I prefissi vengono saltati, non riscritti

I modificatori vengono eseguiti prima che il parser abbia rimosso alcunché: l'input grezzo può quindi portare ancora un identificatore di correlazione, un identificatore di simbologia AIM e un indicatore ECI. Entrambi i modificatori integrati individuano l'inizio della stringa di elementi AI servendosi della logica di `CorrelationIdParser` e `DataCarrierParser` del parser stesso, riscrivono soltanto a partire da quel punto e ricongiungono il risultato al prefisso rimasto **intatto**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

I supporti EAN/UPC il cui valore viene riempito fino a GTIN-14 (`isRequiresGtinPadding()`) vengono saltati per intero: il loro payload è un valore di codice a barre puramente numerico, privo di struttura AI, in cui né parentesi né spazi possono avere significato.

#### Ordine: prima gli spazi, poi le parentesi

Quando si usano entrambi, **registrate per primo `ModifierRemoveSpaces`**. Il riconoscimento delle parentesi dipende dalla posizione: un `( 01 )` spaziato non corrisponde a `\((\d{2,4})\)`, sicché le parentesi sopravvivono e il separatore che esse implicavano non viene mai ripristinato.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Scrivere un modificatore

Scrivetene uno vostro quando nessuno dei due integrati va bene: l'interfaccia si riduce a un solo metodo.

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

Sovrascrivete invece l'overload a due argomenti quando la riscrittura dipende dalla configurazione dell'analisi:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contratto:

| Regola | Dettaglio |
|---|---|
| Privo di stato e sicuro rispetto ai thread | Di ogni classe viene memorizzata in cache una sola istanza, condivisa da tutte le analisi. |
| Costruttore pubblico senza argomenti | Necessario solo quando il modificatore è indicato tramite il nome di classe. |
| Gestire input `null` e input vuoto | Il parser non li filtra prima di eseguire la catena. |
| Restituire `null` significa «nessuna modifica» | Il valore precedente viene mantenuto. Restituite `input` immutato quando il modificatore non si applica. |
| Meglio restituire l'input immutato che sollevare un'eccezione | Un modificatore che solleva un'eccezione interrompe l'analisi — si veda [Gestione degli errori](#gestione-degli-errori-di-un-modificatore). |
| `getName()` | Sovrascrivetelo per controllare il nome riportato in `ModifierInfo`; per impostazione predefinita è il nome semplice della classe. |

### Registrare i modificatori

I modificatori vengono eseguiti nell'ordine in cui sono aggiunti, e ciascuno riceve l'output del precedente. Registrateli per istanza, tramite il nome di classe completo o come elenco dell'uno o dell'altro tipo:

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

I [modificatori integrati](#modificatori-integrati) si indicano esattamente come i vostri — **sempre con il nome completo**. Per essi non esiste alcuna ricerca per nome breve o per alias; `ModifierRegistry` risolve ogni modificatore, distribuito o meno, tramite il nome di classe completo.

I nomi sono risolti da `ModifierRegistry`, che istanzia ciascuna classe una sola volta tramite il suo costruttore senza argomenti e ne conserva l'istanza in cache per ogni configurazione successiva che indichi la stessa classe. La risoluzione avviene **al momento della costruzione della configurazione**: un nome che non viene trovato, che non implementa `ModifierInterface` o che non può essere istanziato solleva quindi lì una `IllegalArgumentException`, e non in silenzio al momento dell'analisi. Un modificatore che non può essere costruito per riflessione (per esempio uno che contiene una dipendenza iniettata) può essere registrato in anticipo, così da restare indirizzabile tramite il proprio nome:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Esaminare che cosa ha fatto un modificatore

Quando sono configurati dei modificatori, `ParseResult.getPayload()` riflette l'input **modificato**. L'originale è conservato in `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` riporta il `getName()` di ciascun modificatore, che per impostazione predefinita è il nome semplice della classe ma che entrambi i modificatori integrati sovrascrivono: una catena composta dai due riporta quindi i nomi visualizzati, non quelli delle classi:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` restituisce `null` quando non è stato configurato alcun modificatore. Quando dei modificatori sono stati eseguiti ma ognuno ha restituito l'input immutato, l'informazione è presente e `isModified()` vale `false`: in `getAppliedModifiers()` compaiono soltanto i modificatori che hanno effettivamente cambiato l'input.

### Gestione degli errori di un modificatore

Un modificatore che solleva un'eccezione interrompe l'analisi. L'eccezione viene incapsulata in una `GaiaModifierException` che nomina il modificatore responsabile, e il risultato porta un errore interno `GE-I001` il cui messaggio riprende quel nome; `getPayload()` riporta l'input non modificato. L'analisi deliberatamente **non** prosegue con una stringa riscritta a metà: un passo di normalizzazione fallito in silenzio produrrebbe risultati dall'aspetto valido ma ricavati dall'input sbagliato.

---

## Modalità di analisi

Ogni modalità designa la [fase della catena](#catena-di-elaborazione-dellanalisi) più profonda che esegue; tutte le fasi precedenti vengono comunque eseguite.

| Modalità | Arriva fino a | Risponde alla domanda |
|---|---|---|
| `DATA_CARRIER` | Fase 1 (instradamento dell'input) | Quale simbologia ha trasportato questo dato? |
| `SYNTAX` | Fase 2 (sintassi) | I codici AI e le lunghezze sono ben formati? |
| `CONTENT` | Fase 3 (contenuto) | I valori sono dati GS1 validi? |
| `INTERPRETATION` | Fase 4 (interpretazione) | Che cosa significano i valori? |

### Modalità DATA_CARRIER

Si ferma dopo la fase 1: valida l'identificatore di simbologia AIM e individua la simbologia, ma non entra nella catena di analisi degli AI. Utile per identificare la simbologia e instradare il trattamento senza sostenere il costo di una validazione completa.

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

**Da usare quando:** la vostra applicazione deve individuare il tipo di codice a barre prima di decidere come trattare il payload — per esempio per instradare verso gestori diversi a seconda che si tratti di simbologie 1D o 2D. Per tale instradamento preferite il tipo [`DataCarrierType`](#datacarrierentry-e-datacarriertype) (`getDataCarrier().getDataCarrierType()`) anziché un confronto di stringhe su `getName()`.

---

### Modalità SYNTAX

Si ferma dopo la fase 2. Utile per un vaglio strutturale preliminare senza il costo della validazione del contenuto.

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

**Da usare quando:** volete verificare che i codici AI e le lunghezze dei dati siano ben formati prima di impegnarvi in una validazione completa, o quando trattate grandi volumi in cui gli errori di contenuto sono rari.

---

### Modalità CONTENT

Si ferma dopo la fase 3.

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

> La maggior parte degli AI non può comparire da sola: gli AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) e `21`
> (SERIAL) *richiedono* ciascuno una chiave di identificazione come l'AI `01` nella stessa stringa
> di elementi; omettere il GTIN qui sopra fallirebbe già alla fase 2 con `GE-S005`, senza
> arrivare affatto alla validazione del contenuto. Impostate `skipRequiresCheck(true)` nella
> `ParseConfig` per analizzare frammenti che omettono deliberatamente i propri AI di accompagnamento.

**Da usare quando:** dovete sapere se un valore letto è pienamente conforme a GS1 prima di impiegarlo in un processo di business, senza il costo aggiuntivo dell'arricchimento interpretativo.

---

### Modalità INTERPRETATION (predefinita)

Esegue l'intera catena fino alla fase 4. È la modalità predefinita quando si chiama `parse(String)` senza argomento di modalità. Vengono arricchiti soltanto gli elementi che hanno superato la validazione del contenuto senza errori.

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

**Esempio di output:**
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

**Esempio di importo monetario (AI 3932 — prezzo con codice valuta ISO):**
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

**Da usare quando:** state costruendo livelli di presentazione, strumenti di verifica delle etichette o una qualsiasi interfaccia che richieda una scomposizione leggibile dei valori degli AI.

---

## Identificatore di correlazione

Alcuni flussi di lavoro antepongono all'input GS1 grezzo un identificatore di correlazione proprietario di 8 cifre, in modo da poter ricollegare gli eventi di lettura a una sessione o a una transazione. Il formato è il seguente:

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

La tilde (`~`) è il separatore. **Non** fa parte del contenuto GS1: viene rimossa prima che inizi qualsiasi analisi GS1.

### Regole di riconoscimento

Il prefisso viene riconosciuto quando l'input inizia con esattamente 8 cifre decimali ASCII (`0`–`9`) seguite immediatamente da `~`. Se il nono carattere non è `~`, oppure se uno degli 8 caratteri iniziali non è una cifra, l'input è trattato come normale contenuto GS1, privo di prefisso di correlazione.

### Accedere all'identificatore di correlazione

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

### Combinazione con un identificatore di simbologia AIM

Un prefisso di correlazione può precedere un identificatore di simbologia AIM. Il parser gestisce il caso in modo trasparente:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Classe di implementazione:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Un **GS1 Digital Link** codifica uno o più valori di AI direttamente nella struttura di un URL HTTP(S), consentendo identificativi di prodotti fisici risolvibili sul web. GAIA implementa lo standard *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) per gli URI **non compressi**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` riconosce automaticamente gli URI Digital Link: ogni input che inizi con `http://` o `https://` viene indirizzato a `GS1DLParser`, che esegue le stesse fasi di contenuto e interpretazione della catena delle stringhe di elementi.

### Struttura dell'URI e ruoli degli AI

Ogni AI in un URI Digital Link riveste uno di tre ruoli, esposto su ciascun `GS1AIObjectElement` tramite `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Ruolo | Posizione | Esempio |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Prima coppia `/ai/valore` del percorso (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Coppie di percorso successive, ordinate secondo la chiave primaria (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parametri di query con chiavi interamente numeriche (§4.10) | `?17=271231` |

Regole strutturali applicate (`DLPathRules`):
- Esattamente **una** chiave di identificazione primaria nel percorso; le chiavi aggiuntive devono essere codificate come attributi di dato nella query.
- I qualificatori di chiave devono essere ammessi dalla chiave primaria e comparire nell'ordine prescritto. I qualificatori facoltativi possono essere omessi, ma quelli che *sono* presenti devono comunque rispettare l'ordine fissato — si veda [Ordine dei qualificatori](#ordine-dei-qualificatori).
- Segmenti di percorso personalizzati arbitrari possono precedere la chiave primaria (per esempio `/products/au/01/...`); si recuperano tramite `getDigitalLinkInfo().getCustomPathStem()`.
- Le chiavi di query non numeriche (`linkType`, `context`, parametri di estensione come `23P`) vengono ignorate; le chiavi interamente numeriche devono essere AI validi contrassegnati `validAsDataAttribute`.
- I caratteri di valore codificati in percentuale vengono decodificati; gli AI `(03)` e `(8014)` non sono ammessi.

Le chiavi primarie e le loro sequenze ammissibili di qualificatori sono **guidate dai dati** ricavati dalle definizioni degli AI — il flag `gs1DigitalLinkPrimaryKey` e l'attributo `gs1DigitalLinkQualifiers` — anziché scritte a mano nel codice.

Qualsiasi violazione strutturale, o un input che non sia un URL, produce un errore strutturale Digital Link (`GE-L001`–`GE-L014`, un codice per condizione). I metadati scomposti dell'URL (`scheme`, `domain`, `path`, `customPathStem`, `query` e l'oggetto `java.net.URL`) restano disponibili tramite `getDigitalLinkInfo()` anche in presenza di errori strutturali.

### Ordine dei qualificatori

Per ciascuna chiave primaria, `gs1DigitalLinkQualifiers` elenca una o più sequenze **ordinate** di qualificatori. All'interno di una sequenza un AI fra parentesi quadre è **facoltativo**, un AI senza parentesi è **obbligatorio**, sul modello della notazione `[cpv-comp]` dell'ABNF del §4.9. Le sequenze di una stessa chiave primaria sono alternative mutuamente esclusive.

Il GTIN (`01`), per esempio, definisce due sequenze:

| Percorso | Sequenza | Significato |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — ciascuno facoltativo, ma in questo ordine fisso |
| upui-path | `235` | TPX (obbligatorio); GTIN + TPX = UPUI |

Così `/01/09506000134352/10/LOT-ABC/21/SER` è valido (LOT prima di SER, CPV omesso), `/01/.../21/SER/10/LOT-ABC` viene **rifiutato** (ordine errato), e `/01/09506000134352/235/2ABC456` è l'upui-path. Il controllo dell'ordine è una corrispondenza di sottosequenza che preserva l'ordine: gli AI facoltativi possono quindi essere saltati, ma mai riordinati.

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

**Classe di implementazione:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Lavorare con i risultati

### ParseResult

Il risultato di primo livello restituito da `GaiaParser.parse()`.

| Metodo | Restituisce | Descrizione |
|---|---|---|
| `isValid()` | `boolean` | `true` se non vi sono errori di alcun livello. Gli avvisi non incidono sulla validità. Sempre `true` quando `getAiObject()` è `null`. |
| `getPayload()` | `String` | La stringa di input dopo la rimozione del prefisso di correlazione — e dopo l'eventuale riscrittura da parte dei [modificatori di input](#modificatori-di-input). |
| `getPayloadContent()` | `String` | Il payload privato dell'identificatore di simbologia AIM e del prefisso ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (un supporto dati rifiutato in quanto non GS1, per esempio un supporto Code 39 `]A0`) oppure `UNABLE_TO_DETERMINE_CONTENT` (quando `aiObject` è `null`, per esempio in modalità `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | La profondità di catena configurata (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | La fase più profonda effettivamente raggiunta dall'analisi — si veda più avanti. |
| `isParseComplete()` | `boolean` | `true` se l'analisi ha raggiunto la profondità richiesta (`achieved == requested`). Indipendente da `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Tutti gli AI risolti. `null` in modalità `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Tutti gli errori di livello diverso da WARNING (a livello di oggetto e di tutti gli elementi). |
| `getWarnings()` | `List<GaiaError>` | Tutti gli avvisi di livello WARNING (a livello di oggetto e di tutti gli elementi). |
| `hasWarnings()` | `boolean` | `true` se sono stati emessi avvisi di livello WARNING. |
| `getIssues()` | `List<GaiaError>` | Errori e avvisi insieme. |
| `hasDataCarrier()` | `boolean` | `true` se è stato riconosciuto un identificatore di simbologia AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadati di simbologia, oppure `null` se non è stato individuato alcun supporto. |
| `hasEci()` | `boolean` | `true` se dal payload è stato rimosso un indicatore ECI. |
| `getEci()` | `EciEntry` | Metadati di codifica ECI, oppure `null`. |
| `hasCorrelationId()` | `boolean` | `true` se nell'input originale era presente un prefisso di correlazione `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | L'identificatore di correlazione estratto, oppure `null` se non ve n'era alcuno. |
| `isInputModified()` | `boolean` | `true` se un [modificatore di input](#modificatori-di-input) ha cambiato l'input. |
| `getModifierInfo()` | `ModifierInfo` | Che cosa ha fatto la catena di modificatori — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` se non era configurato alcun modificatore. |
| `getTiming()` | `ProcessingTiming` | Cronometraggio effettivo dell'analisi — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` se non prodotto da `GaiaParser`. |
| `getVersion()` | `String` | La versione della libreria che ha prodotto il risultato. |

#### Modalità di analisi richiesta e modalità raggiunta

La catena percorre la scala **SYNTAX → CONTENT → INTERPRETATION** e si arresta anzitempo in presenza di errori: la modalità effettivamente *raggiunta* può quindi essere meno profonda di quella *richiesta*. `getAchievedParseMode()` indica fin dove è arrivata:

| Richiesta | Che cosa accade | Raggiunta | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | un errore **di sintassi o di struttura** arresta l'analisi dopo la suddivisione in token | `SYNTAX` | `false` |
| `INTERPRETATION` | un errore **di contenuto** (formato o cifra di controllo errati) impedisce l'arricchimento | `CONTENT` | `false` |
| `CONTENT` | la fase di contenuto arriva sempre a termine (gli errori sono annotati, non fatali) | `CONTENT` | `true` |
| qualsiasi (input senza errori) | la catena raggiunge la profondità richiesta | = richiesta | `true` |
| `DATA_CARRIER` | supporto validato; nessun contenuto di AI analizzato | `DATA_CARRIER` | `true` |
| qualsiasi | il supporto dati viene rifiutato prima dell'analisi degli AI (per esempio un supporto `]A0` non GS1) | `SYNTAX` | `false` |

`isParseComplete()` è indipendente da `isValid()`: un'analisi `CONTENT` di un GTIN con cifra di controllo errata è **completa** (la fase di contenuto è stata eseguita) e insieme **non valida** (la cifra di controllo non torna). Usate `isParseComplete()` per chiedere «la catena è arrivata in profondità quanto ho chiesto?» e `isValid()` per chiedere «i dati sono ben formati?».

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

La raccolta degli elementi AI risolti.

| Metodo | Descrizione |
|---|---|
| `getAis()` | Tutte le istanze di `GS1AIObjectElement`, nell'ordine dell'input. |
| `get(String aiCode)` | Primo elemento corrispondente al codice AI indicato, oppure `null`. |
| `contains(String aiCode)` | `true` se è presente un AI con quel codice. |
| `size()` | Numero di AI risolti. |
| `isValid()` | `true` se non vi sono errori a livello di oggetto e nessun elemento porta errori. |
| `toHriString()` | Stringa HRI, per esempio `(01)09506000134352 (17)261231`. |
| `toElementString()` | Stringa di elementi grezza — senza parentesi, con un FNC1 dopo ciascun elemento a lunghezza variabile — per esempio `010950600013435210LOT-ABC<GS>17271231`. Restituisce `null` se `isValid()` è `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` quando `hasDigitalLink()` è vero, altrimenti `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` se l'input era un URI GS1 Digital Link con una chiave di identificazione primaria. Un URL ben formato privo di chiave primaria espone comunque `getDigitalLinkInfo()`, ma qui restituisce `false`. |
| `getCanonicalDigitalLink()` | L'URI GS1 Digital Link canonico (§4.12) su `https://id.gs1.org` — chiave primaria e qualificatori come segmenti di percorso, attributi di dato come parametri di query ordinati per chiave di AI — oppure `null` in assenza di chiave primaria. |
| `getDigitalLinkInfo()` | Metadati di scomposizione dell'URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), oppure `null` se non si tratta di un Digital Link. |
| `getAllErrors()` | Errori a livello di oggetto + tutti gli errori degli elementi (diversi da WARNING). |
| `getAllWarnings()` | Avvisi a livello di oggetto + tutti gli avvisi degli elementi. |
| `getAllIssues()` | Tutto insieme. |

---

### GS1AIObjectElement

Una singola istanza di AI risolta.

| Metodo | Descrizione |
|---|---|
| `getAi()` | Codice AI, per esempio `"01"`, `"3102"`. |
| `getTitle()` | Titolo di dato GS1, per esempio `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Descrizione GS1 completa dell'AI, **localizzata nella lingua dell'analisi** (per esempio `"Global Trade Item Number (GTIN)"` in inglese). Ricade sul testo inglese della definizione dell'AI se non tradotta. |
| `getFormatString()` | Descrittore di formato che copre l'AI *e* il suo dato, per esempio `"N2+N14"` per l'AI `01`, `"N2+X..20"` per l'AI `10`, `"N4+N3+N..15"` per l'AI `3932`. |
| `getValue()` | Valore di dato grezzo estratto dalla stringa di elementi. |
| `isFixedLength()` | `true` se l'AI ha una lunghezza di dato fissa. |
| `getPosition()` | Scostamento di carattere (base zero) nell'input originale. |
| `getGS1ComponentValues()` | Porzioni di valore per componente (per gli AI a più componenti). |
| `getErrors()` | Errori a livello di elemento diversi da WARNING. |
| `getWarnings()` | Avvisi di livello WARNING sull'elemento. |
| `getIssues()` | Errori e avvisi dell'elemento insieme. |
| `hasErrors()` | `true` se sono allegati errori diversi da WARNING. |
| `hasWarnings()` | `true` se sono allegati avvisi di livello WARNING. |
| `getInterpretations()` | Voci `GS1AIInterpretation` (popolate in modalità INTERPRETATION). |
| `getInterpretation(String type)` | Prima interpretazione corrispondente alla chiave di tipo di `GS1Constants_Enricher` indicata, oppure `null`. |
| `getDigitalLinkAIType()` | Il ruolo Digital Link dell'elemento (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), oppure `null` per input di tipo stringa di elementi. |
| `hasDigitalLinkAIType()` | `true` se è stato assegnato un ruolo Digital Link. |

---

### GaiaError

Un errore di validazione o un avviso, immutabile.

| Metodo | Descrizione |
|---|---|
| `getId()` | Identificativo di catalogo, per esempio `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` oppure `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` oppure `INTERNAL`. |
| `getCode()` | Codice breve leggibile da una macchina. |
| `getAi()` | Codice AI che ha causato l'errore, oppure `null` per errori a livello di oggetto. |
| `getMessage()` | Messaggio leggibile, con i valori interpolati. |
| `getPosition()` | Scostamento di carattere (base zero) nell'input originale. |

---

### GS1AIInterpretation

Un singolo frammento di interpretazione etichettato, allegato a un `GS1AIObjectElement` in modalità `INTERPRETATION`.

| Metodo | Descrizione |
|---|---|
| `getType()` | Chiave di tipo leggibile da una macchina, per esempio `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Stabile fra le lingue. |
| `getLabel()` | Etichetta leggibile da una persona, **localizzata nella lingua dell'analisi** (per esempio `"Date"` / `"GS1 company prefix"` in inglese). |
| `getValue()` | Valore estratto o arricchito, per esempio `"31/12/2026"`, `"9506000"`. Non viene localizzato. |

---

### DataCarrierEntry e DataCarrierType

Quando l'input porta un identificatore di simbologia AIM, `ParseResult.getDataCarrier()` restituisce un `DataCarrierEntry` che descrive il simbolo che ha trasportato i dati. La voce è il record specifico del registro corrispondente all'identificatore AIM riconosciuto; `DataCarrierType` è l'enumerazione, nota al momento della compilazione, a cui essa appartiene.

#### DataCarrierEntry

I metadati di un identificatore di simbologia AIM riconosciuto (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metodo | Descrizione |
|---|---|
| `getAimCodeId()` | L'identificatore di simbologia AIM riconosciuto, per esempio `"]C1"`. |
| `getName()` | Nome leggibile del simbolo specifico, per esempio `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Descrizione più estesa del supporto. |
| `getType()` | Il tipo strutturale del supporto come stringa (rispecchia `getDataCarrierType().getCategory()`). |
| `getStandard()` | Lo standard di simbologia, ove registrato. |
| `getDataCarrierType()` | Il `DataCarrierType` tipizzato di questa voce — da preferire per l'instradamento programmatico. |
| `isGs1Capable()` | `true` se il supporto può contenere dati GS1 (stringhe di elementi AI e/o Digital Link). |
| `isGs1AICapable()` | `true` se il supporto può contenere stringhe di elementi AI GS1. |
| `isGs1DigitalLinkCapable()` | `true` se il supporto può contenere un URI GS1 Digital Link. |
| `isEciCapable()` | `true` se il supporto ammette un indicatore ECI. |
| `isRequiresGtinPadding()` | `true` per i supporti EAN/UPC/ITF il cui valore numerico viene riempito fino a GTIN-14 prima dell'analisi degli AI. |

#### DataCarrierType

Un'enumerazione, nota al momento della compilazione, dei tipi di supporto dati, indicizzata tramite l'identificatore di simbologia AIM assegnato in ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Il carattere che segue `]` (il *carattere di codice*) seleziona la famiglia; la maggior parte delle famiglie corrisponde a un'unica costante che copre tutti i modificatori (`ITF` copre `]I0`–`]I2`; `EAN_UPC` copre EAN-13, UPC-A, UPC-E ed EAN-8). Laddove GS1 riservi un modificatore ai dati AI, quella variante costituisce una costante a sé — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — distinta dalla controparte ordinaria. In assenza di identificatore AIM, o quando questo indichi un supporto sconosciuto, il tipo è `UNKNOWN`.

| Metodo | Descrizione |
|---|---|
| `getCategory()` | La categoria generale `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` oppure `OTHER`. |
| `getCodeChar()` | Il carattere di codice AIM che individua la famiglia, per esempio `"Q"` per QR Code; `null` per `UNKNOWN`. |
| `getDisplayName()` | Nome leggibile del *tipo* (può essere più ampio di `DataCarrierEntry.getName()` — per esempio `"EAN-13 / UPC-A / UPC-E / EAN-8"` contro `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` per le costanti che indicano sempre dati AI GS1: le quattro varianti riservate da GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) più `GS1_DATABAR`, intrinsecamente GS1 poiché ogni modificatore `]e` indica un GS1 DataBar. Più restrittivo di `DataCarrierEntry.isGs1AICapable()`: anche un `QR_CODE` ordinario può trasportare dati AI GS1. |
| `static forAimCodeId(String)` | Risolve un tipo direttamente da un identificatore AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); restituisce `UNKNOWN` per un identificatore assente, malformato o non riconosciuto. |

Instradare per tipo anziché per nome — per esempio per separare i simboli lineari (Code 128) da quelli 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` copre soltanto i simboli a matrice e a punti; i supporti lineari impilati (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) sono `STACKED_LINEAR`, benché siano comunemente
chiamati codici a barre «2D». Per trattare gli uni e gli altri come un unico gruppo — per esempio per decidere
se occorra un imager anziché un lettore laser — verificate l'appartenenza a una delle due categorie.

> La risoluzione del tipo presuppone che l'identificatore di simbologia AIM sia presente nella lettura; senza di esso `getDataCarrier()` è `null` e il tipo è `UNKNOWN`. Configurate il lettore affinché trasmetta il prefisso dell'identificatore AIM.

---

## Riferimento degli errori

| Codice | Livello | Fase | Significato |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Prefisso di AI sconosciuto — impossibile determinare la lunghezza dei dati |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Input troppo breve per leggere un codice AI completo |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Valore troncato — meno caratteri di quelli richiesti dall'AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Identificatore di applicazione duplicato nella stringa di elementi |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Dipendenza fra AI obbligatoria mancante |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Abbinamento di AI escluso — due AI che non possono coesistere |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Errore inatteso nella suddivisione in token |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Carattere al di fuori del set codificabile GS1 nella stringa di elementi |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Separatore FNC1 obbligatorio mancante dopo un AI a lunghezza variabile |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Dati residui oltre il massimo di tutti i componenti |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separatore FNC1 dopo un AI a lunghezza fissa in posizione intermedia |
| `GE-W002` | WARNING | SYNTAX | FNC1 in coda alla stringa di elementi (solo avviso) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Violazioni strutturali di un URI Digital Link — un codice per condizione (URI malformato, schema, host, ordine dei qualificatori, AI vietato, assenza di chiave primaria (`GE-L013`), più chiavi primarie (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Il valore non soddisfa l'espressione regolare dell'AI |
| `GE-C003` | DATA_ERROR | CONTENT | Validazione della cifra di controllo fallita |
| `GE-C004` | DATA_ERROR | CONTENT | Validazione della coppia di caratteri di controllo fallita |
| `GE-C005` | FORMAT_ERROR | CONTENT | Il valore di un componente contiene un carattere al di fuori del set consentito |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Errori di formato dei componenti — un codice per condizione di validazione (si veda `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Errori della validazione semantica personalizzata — un codice per condizione di validazione (si veda `content/validator/`). **Eccezioni:** i 14 controlli sul prefisso aziendale GS1 elencati qui sotto hanno livello `WARNING`, e `GE-C168` (codice numerico di Paese ISO 3166-1 non riconosciuto) ha `FORMAT_ERROR`. |
| Controlli sul prefisso aziendale GS1 | WARNING | CONTENT | La chiave non inizia con un prefisso aziendale GS1 riconosciuto, negli AI che portano una chiave GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Solo avviso — non incide sulla validità. |
| `GE-C169` | DATA_ERROR | CONTENT | Cifra di controllo IMEI (Luhn) fallita sull'AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Cifra di controllo EID (Luhn) fallita sull'AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Identificatore di simbologia AIM non riconosciuto |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Supporto individuato ma che non ammette né stringhe di elementi AI GS1 né URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Errore interno inatteso |

> **Difetto noto nella resa dei messaggi.** I modelli del catalogo racchiudono i valori
> interpolati fra apostrofi raddoppiati alla maniera di MessageFormat (`''{value}''`), ma
> `ErrorRegistry` interpola con un semplice `String.replace`: il raddoppio sopravvive quindi fino a
> `getMessage()` — attualmente vedrete `value ''09506000134351''` là dove i testi dei
> messaggi citati in questa guida mostrano `value '09506000134351'`. Riguarda ogni messaggio
> che racchiude un valore, in tutti i 35 cataloghi di lingua. Non analizzate i messaggi di errore;
> confrontate su `getId()` / `getCode()`.

---

## Sicurezza rispetto ai thread

`GaiaParser` è sicuro rispetto ai thread una volta costruito. Una sola istanza può essere condivisa fra più thread e usata in concorrenza. Lo schema consigliato è costruire un'istanza all'avvio dell'applicazione e riutilizzarla:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` è immutabile e altrettanto sicuro da condividere. L'unico obbligo di sicurezza rispetto ai thread che la libreria non può assolvere al posto vostro riguarda i [modificatori di input](#modificatori-di-input): di ciascun modificatore viene memorizzata in cache una sola istanza, condivisa da tutte le analisi concorrenti, e le implementazioni devono pertanto essere prive di stato.

---

## Appendice A — costanti di stringa degli AI

`GS1Constants_AICodes` (nel package `tools.pantheum.gaia.gs1.constants`) dichiara una costante `String` per ogni identificatore di applicazione riconosciuto da GAIA. Usate queste costanti anziché scrivere a mano le stringhe dei codici AI:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Ogni costante contiene la forma testuale del codice AI (per esempio `AI_01_GTIN = "01"`).

### Identificazione e serializzazione

| AI | Costante | Descrizione |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Codice Seriale del Container di Spedizione (SSCC). |
| `01` | `AI_01_GTIN` | Numero Globale dell'Articolo Commerciale (GTIN). |
| `02` | `AI_02_CONTENT` | Numero Globale dell'Articolo Commerciale (GTIN) delle unità di vendita contenute. |
| `03` | `AI_03_MTO_GTIN` | Identificazione di un'unità di vendita realizzata su ordinazione (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Numero di lotto. |
| `20` | `AI_20_VARIANT` | Variante interna del prodotto. |
| `21` | `AI_21_SERIAL` | Numero di serie. |
| `22` | `AI_22_CPV` | Variante del prodotto di consumo. |
| `235` | `AI_235_TPX` | Estensione Serializzata Controllata da Terzi del Numero Globale dell'Articolo Commerciale (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identificazione aggiuntiva del prodotto assegnata dal produttore. |
| `241` | `AI_241_CUST_PART_NO` | Numero di parte del cliente. |
| `242` | `AI_242_MTO_VARIANT` | Numero di variante realizzata su ordinazione. |
| `243` | `AI_243_PCN` | Numero del componente di imballaggio. |
| `250` | `AI_250_SECONDARY_SERIAL` | Numero di serie secondario. |
| `251` | `AI_251_REF_TO_SOURCE` | Riferimento all'entità di origine. |
| `253` | `AI_253_GDTI` | Identificativo Globale del Tipo di Documento (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Componente di estensione del Numero di Localizzazione Globale (GLN). |
| `255` | `AI_255_GCN` | Numero Globale del Buono (GCN). |
| `30` | `AI_30_VAR_COUNT` | Numero variabile di articoli (articolo a misura variabile). |
| `37` | `AI_37_COUNT` | Numero di unità di vendita o pezzi di unità di vendita contenuti in un'unità logistica. |

### Date e orari

| AI | Costante | Descrizione |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Data di produzione (AAMMGG). |
| `12` | `AI_12_DUE_DATE` | Data di scadenza prevista (AAMMGG). |
| `13` | `AI_13_PACK_DATE` | Data di imballaggio (AAMMGG). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Termine minimo di conservazione (AAMMGG). |
| `16` | `AI_16_SELL_BY` | Data limite di vendita (AAMMGG). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Data di scadenza (AAMMGG). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Data e ora di consegna non prima di (AAMMGGhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Data e ora limite di consegna (AAMMGGhhmm). |
| `4326` | `AI_4326_REL_DATE` | Data di rilascio (AAMMGG). |
| `7003` | `AI_7003_EXPIRY_TIME` | Data e ora di scadenza (AAMMGGhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Data del primo congelamento (AAMMGG). |
| `7007` | `AI_7007_HARVEST_DATE` | Data di raccolta (AAMMGG[AAMMGG]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Data limite di verifica (AAMMGG[hhmm]). |

### Quantità e misura — misura variabile (metrico)

Le famiglie di AI a 4 cifre `310n`–`369n` codificano quantità a misura variabile. La terza cifra seleziona il tipo di misura; la **quarta cifra** (`n`, 0–5) è il numero di decimali impliciti — per esempio `AI_3102_NET_WEIGHT_KG` indica il peso netto in kg con 2 decimali.

| Famiglia | Schema della costante (`n` = cifra dei decimali) | Descrizione |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Peso netto, chilogrammi (articolo a misura variabile). |
| `311n` | `AI_311n_LENGTH_M` | Lunghezza o prima dimensione, metri (articolo a misura variabile). |
| `312n` | `AI_312n_WIDTH_M` | Larghezza, diametro o seconda dimensione, metri (articolo a misura variabile). |
| `313n` | `AI_313n_HEIGHT_M` | Profondità, spessore, altezza o terza dimensione, metri (articolo a misura variabile). |
| `314n` | `AI_314n_AREA_M` | Superficie, metri quadrati (articolo a misura variabile). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volume netto, litri (articolo a misura variabile). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volume netto, metri cubi (articolo a misura variabile). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Peso logistico, chilogrammi. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Lunghezza o prima dimensione, metri. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Larghezza, diametro o seconda dimensione, metri. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Profondità, spessore, altezza o terza dimensione, metri. |
| `334n` | `AI_334n_AREA_M_LOG` | Superficie, metri quadrati. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volume logistico, litri. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volume logistico, metri cubi. |
| `337n` | `AI_337n_KG_PER_M` | Chilogrammi per metro quadrato. |

### Quantità e misura — misura variabile (imperiale / USA)

| Famiglia | Schema della costante (`n` = cifra dei decimali) | Descrizione |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Peso netto, libbre (articolo a misura variabile). |
| `321n` | `AI_321n_LENGTH_IN` | Lunghezza o prima dimensione, pollici (articolo a misura variabile). |
| `322n` | `AI_322n_LENGTH_FT` | Lunghezza o prima dimensione, piedi (articolo a misura variabile). |
| `323n` | `AI_323n_LENGTH_YD` | Lunghezza o prima dimensione, iarde (articolo a misura variabile). |
| `324n` | `AI_324n_WIDTH_IN` | Larghezza, diametro o seconda dimensione, pollici (articolo a misura variabile). |
| `325n` | `AI_325n_WIDTH_FT` | Larghezza, diametro o seconda dimensione, piedi (articolo a misura variabile). |
| `326n` | `AI_326n_WIDTH_YD` | Larghezza, diametro o seconda dimensione, iarde (articolo a misura variabile). |
| `327n` | `AI_327n_HEIGHT_IN` | Profondità, spessore, altezza o terza dimensione, pollici (articolo a misura variabile). |
| `328n` | `AI_328n_HEIGHT_FT` | Profondità, spessore, altezza o terza dimensione, piedi (articolo a misura variabile). |
| `329n` | `AI_329n_HEIGHT_YD` | Profondità, spessore, altezza o terza dimensione, iarde (articolo a misura variabile). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Peso logistico, libbre. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Lunghezza o prima dimensione, pollici. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Lunghezza o prima dimensione, piedi. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Lunghezza o prima dimensione, iarde. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Larghezza, diametro o seconda dimensione, pollici. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Larghezza, diametro o seconda dimensione, piedi. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Larghezza, diametro o seconda dimensione, iarda. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Profondità, spessore, altezza o terza dimensione, pollici. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Profondità, spessore, altezza o terza dimensione, piedi. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Profondità, spessore, altezza o terza dimensione, iarde. |
| `350n` | `AI_350n_AREA_IN` | Superficie, pollici quadrati (articolo a misura variabile). |
| `351n` | `AI_351n_AREA_FT` | Superficie, piedi quadrati (articolo a misura variabile). |
| `352n` | `AI_352n_AREA_YD` | Superficie, iarde quadrate (articolo a misura variabile). |
| `353n` | `AI_353n_AREA_IN_LOG` | Superficie, pollici quadrati. |
| `354n` | `AI_354n_AREA_FT_LOG` | Superficie, piedi quadrati. |
| `355n` | `AI_355n_AREA_YD_LOG` | Superficie, iarde quadrate. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Peso netto, once troy (articolo a misura variabile). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Peso netto (o volume), once (articolo a misura variabile). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volume netto, quarti (articolo a misura variabile). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volume netto, galloni USA (articolo a misura variabile). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volume logistico, quarti. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volume logistico, galloni USA. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volume netto, pollici cubi (articolo a misura variabile). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volume netto, piedi cubi (articolo a misura variabile). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volume netto, iarde cubiche (articolo a misura variabile). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volume logistico, pollici cubi. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volume logistico, piedi cubi. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volume logistico, iarde cubiche. |

### Prezzi e importi monetari

La quarta cifra (`n`) codifica il numero di decimali impliciti. L'intervallo consentito
varia a seconda della famiglia — si veda la colonna `n`.

| Famiglia | Schema della costante (`n` = cifra dei decimali) | `n` | Descrizione |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Importo da pagare applicabile o valore del buono, valuta locale. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Importo da pagare applicabile con codice valuta ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Importo da pagare applicabile, area monetaria unica (articolo a misura variabile). |
| `393n` | `AI_393n_PRICE` | 0–9 | Importo da pagare applicabile con codice valuta ISO (articolo a misura variabile). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Percentuale di sconto di un buono. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Importo da pagare per unità di misura, area monetaria unica (articolo a misura variabile). |

### Luogo e spedizione

| AI | Costante | Descrizione |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Numero d'ordine di acquisto del cliente. |
| `401` | `AI_401_GINC` | Numero Globale di Identificazione della Spedizione (GINC). |
| `402` | `AI_402_GSIN` | Numero Globale di Identificazione della Spedizione (GSIN). |
| `403` | `AI_403_ROUTE` | Codice di instradamento. |
| `410` | `AI_410_SHIP_TO_LOC` | Numero di Localizzazione Globale (GLN) del luogo di consegna. |
| `411` | `AI_411_BILL_TO` | Numero di Localizzazione Globale (GLN) del fatturato. |
| `412` | `AI_412_PURCHASE_FROM` | Numero di Localizzazione Globale (GLN) del fornitore di acquisto. |
| `413` | `AI_413_SHIP_FOR_LOC` | Numero di Localizzazione Globale (GLN) per l'inoltro. |
| `414` | `AI_414_LOC_NO` | Identificazione di un'ubicazione fisica - Numero di Localizzazione Globale (GLN). |
| `415` | `AI_415_PAY_TO` | Numero di Localizzazione Globale (GLN) del soggetto fatturante. |
| `416` | `AI_416_PROD_SERV_LOC` | Numero di Localizzazione Globale (GLN) del luogo di produzione o del servizio. |
| `417` | `AI_417_PARTY` | Numero di Localizzazione Globale (GLN) della parte. |
| `420` | `AI_420_SHIP_TO_POST` | Codice postale del luogo di consegna nell'ambito di un'unica autorità postale. |
| `421` | `AI_421_SHIP_TO_POST` | Codice postale del luogo di consegna con codice paese ISO. |
| `422` | `AI_422_ORIGIN` | Paese di origine di un'unità di vendita. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Paese di prima lavorazione. |
| `424` | `AI_424_COUNTRY_PROCESS` | Paese di lavorazione. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Paese di smontaggio. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Paese che copre l'intera catena di lavorazione. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Suddivisione del paese di origine. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Ragione sociale del luogo di consegna. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Contatto del luogo di consegna. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Indirizzo di consegna, riga 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Indirizzo di consegna, riga 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Quartiere del luogo di consegna. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Località del luogo di consegna. |
| `4306` | `AI_4306_SHIP_TO_REG` | Regione del luogo di consegna. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Codice paese del luogo di consegna. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Numero di telefono del luogo di consegna. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Geolocalizzazione del luogo di consegna. |
| `4310` | `AI_4310_RTN_TO_COMP` | Ragione sociale del reso. |
| `4311` | `AI_4311_RTN_TO_NAME` | Contatto per il reso. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Indirizzo di reso, riga 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Indirizzo di reso, riga 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Quartiere di reso. |
| `4315` | `AI_4315_RTN_TO_LOC` | Località di reso. |
| `4316` | `AI_4316_RTN_TO_REG` | Regione di reso. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Codice paese di reso. |
| `4318` | `AI_4318_RTN_TO_POST` | Codice postale di reso. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Numero di telefono di reso. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Descrizione del codice di servizio. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Indicatore di merci pericolose. |
| `4322` | `AI_4322_AUTH_LEAVE` | Autorizzazione alla consegna senza firma. |
| `4323` | `AI_4323_SIG_REQUIRED` | Indicatore di firma richiesta. |
| `4330` | `AI_4330_MAX_TEMP_F` | Temperatura massima in gradi Fahrenheit (espressa in centesimi di grado). |
| `4331` | `AI_4331_MAX_TEMP_C` | Temperatura massima in gradi Celsius (espressa in centesimi di grado). |
| `4332` | `AI_4332_MIN_TEMP_F` | Temperatura minima in gradi Fahrenheit (espressa in centesimi di grado). |
| `4333` | `AI_4333_MIN_TEMP_C` | Temperatura minima in gradi Celsius (espressa in centesimi di grado). |

### Attributi di prodotto e tracciabilità

| AI | Costante | Descrizione |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Numero di Nomenclatura NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Classificazione UN/ECE delle carcasse e dei tagli di carne. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Potenza attiva. |
| `7005` | `AI_7005_CATCH_AREA` | Zona di cattura. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Specie per finalità di pesca. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Tipo di attrezzo da pesca. |
| `7010` | `AI_7010_PROD_METHOD` | Metodo di produzione. |
| `7020` | `AI_7020_REFURB_LOT` | Identificativo del lotto di ricondizionamento. |
| `7021` | `AI_7021_FUNC_STAT` | Stato funzionale. |
| `7022` | `AI_7022_REV_STAT` | Stato di revisione. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Identificativo Globale del Bene Individuale (GIAI) di un assemblaggio. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Numero del trasformatore, con codice Paese ISO a tre cifre (10 posizioni). |
| `7040` | `AI_7040_UIC_EXT` | UIC GS1 con estensione 1 e indice dell'importatore. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Tipo di unità di carico UN/CEFACT. |

### Numeri nazionali di rimborso sanitario (NHRN)

| AI | Costante | Descrizione |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Numero Nazionale di Rimborso Sanitario (NHRN) - Germania PZN. |
| `711` | `AI_711_NHRN_CIP` | Numero Nazionale di Rimborso Sanitario (NHRN) - Francia CIP. |
| `712` | `AI_712_NHRN_CN` | Numero Nazionale di Rimborso Sanitario (NHRN) - Spagna CN. |
| `713` | `AI_713_NHRN_DRN` | Numero Nazionale di Rimborso Sanitario (NHRN) - Brasile DRN. |
| `714` | `AI_714_NHRN_AIM` | Numero Nazionale di Rimborso Sanitario (NHRN) - Portogallo AIM. |
| `715` | `AI_715_NHRN_NDC` | Numero Nazionale di Rimborso Sanitario (NHRN) - Stati Uniti d'America NDC. |
| `716` | `AI_716_NHRN_AIC` | Numero Nazionale di Rimborso Sanitario (NHRN) - Italia AIC. |
| `717` | `AI_717_NHRN_SRN` | Numero Nazionale di Rimborso Sanitario (NHRN) - Costa Rica, Numero di Registro Sanitario. |

### Sanità, GMN, HIDRI, CPID, dati personali

| AI | Costante | Descrizione |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Riferimento di certificazione (10 posizioni). |
| `7240` | `AI_7240_PROTOCOL` | Identificativo del protocollo. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Tipo di supporto AIDC. |
| `7242` | `AI_7242_VCN` | Numero di Controllo della Versione (VCN). |
| `7250` | `AI_7250_DOB` | Data di nascita (AAAAMMGG). |
| `7251` | `AI_7251_DOB_TIME` | Data e ora di nascita (AAAAMMGGhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Sesso biologico. |
| `7253` | `AI_7253_FAMILY_NAME` | Cognome della persona. |
| `7254` | `AI_7254_GIVEN_NAME` | Nome di battesimo della persona. |
| `7255` | `AI_7255_SUFFIX` | Suffisso del nome della persona. |
| `7256` | `AI_7256_FULL_NAME` | Nome completo della persona. |
| `7257` | `AI_7257_PERSON_ADDR` | Indirizzo della persona. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Ordine di nascita (parto plurimo). |
| `7259` | `AI_7259_BABY` | Cognome del neonato. |
| `8001` | `AI_8001_DIMENSIONS` | Prodotti in rotolo (larghezza, lunghezza, diametro dell'anima, direzione, giunzioni). |
| `8002` | `AI_8002_CMT_NO` | Identificativo del telefono cellulare. |
| `8003` | `AI_8003_GRAI` | Identificativo Globale del Bene a Rendere (GRAI). |
| `8004` | `AI_8004_GIAI` | Identificativo Globale del Bene Individuale (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Prezzo per unità di misura. |
| `8006` | `AI_8006_ITIP` | Identificazione di un singolo pezzo di unità di vendita (ITIP). |
| `8007` | `AI_8007_IBAN` | Numero di Conto Bancario Internazionale (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Data e ora di produzione (AAMMGGhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indicatore del sensore a lettura ottica. |
| `8010` | `AI_8010_CPID` | Identificativo di componente/parte (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Numero di serie dell'identificativo di componente/parte (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Versione del software. |
| `8013` | `AI_8013_GMN` | Numero Globale del Modello (GMN). |
| `8014` | `AI_8014_MUDI` | Identificativo di Registrazione del Dispositivo Altamente Individualizzato (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Numero Globale di Relazione di Servizio (GSRN) per identificare la relazione tra un'organizzazione che offre servizi e il fornitore del servizio. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Numero Globale di Relazione di Servizio (GSRN) per identificare la relazione tra un'organizzazione che offre servizi e il destinatario del servizio. |
| `8019` | `AI_8019_SRIN` | Numero di Istanza della Relazione di Servizio (SRIN). |
| `8020` | `AI_8020_REF_NO` | Numero di riferimento della distinta di pagamento. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identificazione dei pezzi di un'unità di vendita (ITIP) contenuti in un'unità logistica. |
| `8030` | `AI_8030_DIGSIG` | Firma digitale (DigSig). |
| `8040` | `AI_8040_IMEI` | Identità Internazionale dell'Apparecchiatura Mobile (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identità Internazionale dell'Apparecchiatura Mobile 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Numero della SIM integrata. |
| `8043` | `AI_8043_PSIM` | Numero della SIM fisica. |
| `8110` | `AI_8110` | Identificazione del codice buono per l'uso in Nord America. |
| `8111` | `AI_8111_POINTS` | Punti fedeltà di un buono. |
| `8112` | `AI_8112` | Identificazione del codice buono del file di offerte positive per l'uso in Nord America. |
| `8200` | `AI_8200_PRODUCT_URL` | URL di packaging esteso. |

### Uso interno / aziendale

| AI | Costante | Descrizione |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informazione concordata reciprocamente tra i partner commerciali. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Informazione interna all'azienda (9 posizioni). |

---

## Appendice B — costanti delle chiavi di interpretazione

Quando `GaiaParser.parse()` viene chiamato con `ParseMode.INTERPRETATION`, ciascun `GS1AIObjectElement` può portare un elenco di oggetti `GS1AIInterpretation` prodotti da arricchitori specializzati. Usate le costanti di `GS1Constants_Enricher` (nel package `tools.pantheum.gaia.gs1.constants`) come chiavi per reperire valori di interpretazione specifici:

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

Le etichette di visualizzazione **non** sono costanti: risiedono nei cataloghi localizzati sotto `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indicizzate dalla costante di tipo. `GS1AIInterpretation.getLabel()` restituisce l'etichetta corrispondente alla lingua dell'analisi (si veda [Messaggi ed etichette localizzati](#messaggi-ed-etichette-localizzati)), ricadendo sull'inglese quando un catalogo omette la chiave. La colonna «Etichetta di visualizzazione» qui sotto riporta il testo italiano così com'è distribuito nel catalogo; le chiavi di tipo, invece, sono stabili fra le lingue: confrontate sempre sulla chiave, mai sull'etichetta.

### Data e ora

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `DATE_VALUE` | Data | AI di data (11–17, 7003, 7006, 7011, ecc.) |
| `DATE_FORMAT` | Formato data | AI di data |
| `TIME_VALUE` | Ora | AI che portano un orario (7003, 7011, 8008, ecc.) |
| `TIME_FORMAT` | Formato ora | AI che portano un orario |
| `DATETIME_VALUE` | Data e ora | AI di data e ora |
| `DATETIME_FORMAT` | Formato data e ora | AI di data e ora |

### Data di raccolta

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Data di inizio raccolta | AI 7007 |
| `HARVEST_END_DATE` | Data di fine raccolta | AI 7007 (fine intervallo facoltativa) |
| `HARVEST_DATE_RANGE` | Intervallo date di raccolta | AI 7007 |

### Prefisso aziendale GS1

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefisso aziendale GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Codice membro GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organizzazione membro GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Tipo di GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Livello di imballaggio | AI 01 |
| `GTIN_CHECK_DIGIT` | Cifra di controllo | AI 01, 02 |

### SSCC

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Cifra di estensione | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Riferimento di serie | AI 00 |
| `SSCC_CHECK_DIGIT` | Cifra di controllo | AI 00 |

### Paese (ISO 3166)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Codice paese (numerico) | AI a Paese singolo (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Codice paese (alfa-2) | AI di Paese alfa-2 |
| `COUNTRY_NAME` | Nome del paese | AI a Paese singolo |
| `COUNTRY_LIST` | Paesi | AI 423 — tutti i nomi uniti, per esempio `Australia, New Zealand` |

L'AI 423 (Paese di prima trasformazione) può portare fino a cinque Paesi, e produce quindi una
**coppia numerata per ciascun Paese** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — seguita dall'unico riepilogo
`COUNTRY_LIST`. Componete queste chiavi a partire dalle costanti `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` e dall'indice a base 1, oppure limitatevi a scorrere `getInterpretations()`; le
chiavi `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` prive di suffisso **non** vengono emesse per l'AI 423.

### Valuta (ISO 4217)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Codice valuta | AI di importo con valuta (391n, 393n) |
| `CURRENCY_ALPHA` | Codice alfabetico della valuta | AI di importo con valuta |
| `CURRENCY_NAME` | Nome della valuta | AI di importo con valuta |

### Temperatura

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatura | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Unità di temperatura | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatura (formattata) | AI 4330–4333 |

### Sesso (ISO 5218)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `SEX_CODE` | Codice del sesso | AI 7252 |
| `SEX_DESCRIPTION` | Descrizione del sesso | AI 7252 |

### Specie acquatiche (FAO)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Codice specie | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nome scientifico | AI 7008 |
| `SPECIES_ENGLISH` | Nome comune | AI 7008 |
| `SPECIES_FAMILY` | Famiglia | AI 7008 |
| `SPECIES_ORDER` | Ordine | AI 7008 |

### Numero di catalogo NATO (NSN)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `NSN_FSG` | Gruppo di approvvigionamento | AI 7001 |
| `NSN_FSG_NAME` | Nome del gruppo di approvvigionamento | AI 7001 |
| `NSN_FSCG` | Classe di approvvigionamento | AI 7001 |
| `NSN_FSCG_NAME` | Nome della classe di approvvigionamento | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Codice paese | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Paese | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Codice paese ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Categoria NCS | AI 7001 |
| `NSN_NIIN` | Numero nazionale dell'articolo | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Prodotti in rotolo

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Larghezza del rotolo (mm) | AI 8001 |
| `ROLL_LENGTH` | Lunghezza del rotolo (m) | AI 8001 |
| `CORE_DIAMETER` | Diametro dell'anima (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Codice direzione di avvolgimento | AI 8001 |
| `WINDING_DIRECTION` | Direzione di avvolgimento | AI 8001 |
| `SPLICES` | Giunzioni | AI 8001 |

### IBAN

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Codice paese | AI 8007 |
| `IBAN_COUNTRY_NAME` | Paese | AI 8007 |
| `IBAN_CHECK_DIGITS` | Cifre di controllo | AI 8007 |
| `IBAN_CHECK_VALID` | Verifica | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Numero di serie | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Cifra di controllo | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Organismo emittente | AI 8040, 8041 |

Le 15 cifre si scompongono in `[ TAC (8) ][ numero di serie (6) ][ cifra di controllo di Luhn (1) ]`, dove il
RBI corrisponde alle prime 2 cifre del TAC: `IMEI_RBI` è dunque un prefisso di `IMEI_TAC`, non
un tratto distinto. `IMEI_FORMATTED` rende il raggruppamento di visualizzazione standard GSMA
`AA-BBBBBB-CCCCCC-D` (per esempio `49-015420-323751-8`), che divide il TAC al confine
del RBI; il vecchio raggruppamento `6-2-6-1`, che tagliava là dove iniziava il dismesso Final Assembly
Code, non viene emesso.

`IMEI_RBI_NAME` risolve il RBI nel nome dell'ente assegnatario tramite `ImeiRbiData`, ed è
**aggiunto per ultimo e soltanto quando il codice vi figura**. Quella tabella copre tre gruppi:

- **Assegnazione in corso** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, oltre a `99`
  Global Hexadecimal Administrator e `98` (riservato).
- **Intervalli di prova** — `00` e `02`–`09`, che segnalano IMEI di prova anziché un'assegnazione reale.
  Si interrogano con `ImeiRbiData.isTestCode(code)`.
- **Assegnazione cessata** — enti storici quali `49` (BZT/BAPT, Germania), `44`
  (BABT, Regno Unito) o `91` (MSAI, India). Si interrogano con `ImeiRbiData.isNoLongerAllocating(code)`.
  I dispositivi che portano questi codici sono normali e restano in servizio; è cessata soltanto
  l'assegnazione di codici nuovi: si tratta quindi di un'informazione di rendicontazione, mai di un segnale di validità.

L'assenza di `IMEI_RBI_NAME` significa «questo RBI non è nella nostra tabella», **non** «IMEI non valido»:
la tabella è compilata a partire da un elenco pubblicato di RBI e non direttamente dalla GSMA, e può
quindi essere in ritardo rispetto agli enti designati di recente. Non traetene alcun esito di validazione;
il RBI non è un carattere di controllo. Anche il codice che scorre l'elenco delle interpretazioni deve
tollerarne l'assenza anziché indicizzare per posizione.

### Identificativi SIM (EID / ICCID)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Categoria di settore | AI 8042 |
| `EID_BODY` | Corpo dell'EID | AI 8042 |
| `EID_CHECK_DIGIT` | Cifra di controllo | AI 8042 |
| `ICCID_BODY` | Corpo dell'ICCID | AI 8043 |
| `ICCID_EXTENSION` | Estensione | AI 8043 |

`SIM_MII` porta le **due** cifre iniziali (`89`), la coppia che l'ITU-T E.118 assegna alle
telecomunicazioni. La ISO/IEC 7812 definisce a sua volta il MII come la **sola prima cifra**:
`SIM_MII_NAME` risolve quindi la categoria a partire da quell'`8` iniziale tramite `Iso7812Data`, ottenendo
«Healthcare, telecommunications and other future industry assignments». Per un EID ben formato
tale valore è dunque costante; viene riportato per tracciabilità rispetto allo standard, non come
criterio discriminante. `Iso7812Data.nameForCode(digit)` accetta una singola cifra,
`nameForIdentifier(prefix)` accetta un prefisso più lungo e ne legge la cifra iniziale.

`SIM_MII_NAME` è emesso soltanto da `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
espone `SIM_MII` senza la categoria.

### Riferimento di certificazione

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Numero di sequenza | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Codice dello schema di certificazione | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Schema di certificazione | AI 7230–7239 |
| `CERT_REFERENCE` | Riferimento di certificazione | AI 7230–7239 |

### GS1 UIC

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `UIC_CODE` | Codice UIC | AI 7040 |
| `UIC_EXTENSION_1` | Estensione 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Indice importatore | AI 7040 |

### Ordine di nascita del neonato

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posizione di nascita | AI 7258 |
| `BIRTH_TOTAL` | Totale nascite | AI 7258 |
| `BIRTH_SEQUENCE` | Sequenza di nascita | AI 7258 |

### Numero globale di modello (GMN)

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Riferimento del modello | AI 8013 |
| `GMN_CHECK_PAIR` | Coppia di controllo | AI 8013 |

### HIDRI

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Riferimento del dispositivo | AI 8014 |
| `HIDRI_CHECK_PAIR` | Coppia di controllo | AI 8014 |

### CPID

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Riferimento componente e parte | AI 8010–8011 |

### Valori decimali e di misura

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Valore decimale | AI numerici con decimali impliciti (31xx–36xx) |
| `DECIMAL_AMOUNT` | Importo | AI di prezzo (390n–395n) |
| `DECIMAL_PERCENTAGE` | Percentuale | AI 394n |
| `DECIMAL_PLACES` | Cifre decimali | Insieme a `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Formato percentuale | AI 394n |
| `ISO_UNIT_CODE` | Codice unità ISO | AI di misura |
| `ISO_UNIT_NAME` | Nome unità ISO | AI di misura |
| `MONETARY_AMOUNT` | Importo monetario | AI di prezzo |
| `MONETARY_AMOUNT_DISPLAY` | Importo monetario (formattato) | AI di prezzo |

### Coordinate geografiche

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `LATITUDE` | Latitudine | AI 4309 |
| `LONGITUDE` | Longitudine | AI 4309 |
| `GEO_COORDINATES` | Coordinate geografiche | AI 4309 |
| `LATITUDE_DMS` | Latitudine (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitudine (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Coordinate geografiche (DMS) | AI 4309 |

### Metodo di produzione

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Codice metodo di produzione | AI 7010 |
| `PRODUCTION_METHOD` | Metodo di produzione | AI 7010 |

### Tipo di supporto AIDC

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Codice tipo di supporto AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Tipo di supporto AIDC | AI 7241 |

### Pezzo sul totale

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Numero del pezzo | AI 8006 |
| `PIECE_TOTAL` | Totale pezzi | AI 8006 |
| `PIECE_OF_TOTAL` | Pezzo sul totale | AI 8006 |

### Suddivisioni in componenti

Chiavi emesse dalle suddivisioni in componenti dichiarative di `content/ai-content.json` anziché
da un arricchitore Java: mettono in evidenza le parti denominate del valore di un AI composito. A differenza di tutte
le altre chiavi di questa appendice, esse **non hanno alcuna costante in `GS1Constants_Enricher`**: confrontate
la stringa letterale, oppure leggete il tipo tramite `GS1AIInterpretation.getType()`.

| Chiave di tipo | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Cifra di controllo | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Numero di serie | AI 253, 255, 8003 |
| `POSTAL_CODE` | Codice postale | AI 421 |
| `PROCESSOR_ID` | Identificativo del trasformatore | AI 7030–7039 |

Si noti che qui `CHECK_DIGIT` è la chiave generica della suddivisione in componenti, distinta dalle chiavi
specifiche degli arricchitori `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` ed
`EID_CHECK_DIGIT` elencate più sopra.

### Varie

| Costante di chiave | Etichetta di visualizzazione | Prodotta da |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Valore | AI booleani / indicatori (4321–4323) |
| `DECODED_TEXT` | Testo decodificato | AI a testo libero |
