# GAIA (GS1 Application Identifiers Analyser) — Gabay para sa Developer

## Talaan ng Nilalaman

1. [Pangkalahatang-tanaw](#pangkalahatang-tanaw)
2. [Tungkol sa GS1 at sa General Specifications](#tungkol-sa-gs1-at-sa-general-specifications)
3. [Mga GS1 Application Identifier](#mga-gs1-application-identifier)
4. [Mabilisang Panimula](#mabilisang-panimula)
5. [Ang Daloy ng Pag-parse](#ang-daloy-ng-pag-parse)
   - [Paunang yugto — Mga Input Modifier](#paunang-yugto--mga-input-modifier)
   - [Yugto 0 — Correlation ID](#yugto-0--correlation-id)
   - [Yugto 1 — Pagruruta ng Input](#yugto-1--pagruruta-ng-input)
   - [Yugto 2 — Sintaks](#yugto-2--sintaks)
   - [Yugto 3 — Nilalaman](#yugto-3--nilalaman)
   - [Yugto 4 — Interpretasyon](#yugto-4--interpretasyon)
6. [Pagsasaayos ng Pag-parse (`ParseConfig`)](#pagsasaayos-ng-pag-parse-parseconfig)
   - [Mga opsyon](#mga-opsyon)
   - [Mga naisalokal na mensahe at label](#mga-naisalokal-na-mensahe-at-label)
   - [Pag-format ng petsa](#pag-format-ng-petsa)
7. [Mga Input Modifier](#mga-input-modifier)
   - [Mga nakapaloob na modifier](#mga-nakapaloob-na-modifier)
   - [Pagsulat ng isang modifier](#pagsulat-ng-isang-modifier)
   - [Pagrerehistro ng mga modifier](#pagrerehistro-ng-mga-modifier)
   - [Pagsusuri sa ginawa ng modifier](#pagsusuri-sa-ginawa-ng-modifier)
   - [Paghawak sa pagkabigo ng modifier](#paghawak-sa-pagkabigo-ng-modifier)
8. [Mga Modo ng Pag-parse](#mga-modo-ng-pag-parse)
   - [Modong DATA_CARRIER](#modong-data_carrier)
   - [Modong SYNTAX](#modong-syntax)
   - [Modong CONTENT](#modong-content)
   - [Modong INTERPRETATION (default)](#modong-interpretation-default)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Pagtatrabaho sa mga Resulta](#pagtatrabaho-sa-mga-resulta)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry at DataCarrierType](#datacarrierentry-at-datacarriertype)
12. [Sanggunian sa Error](#sanggunian-sa-error)
13. [Kaligtasan sa Thread](#kaligtasan-sa-thread)
14. [Apendiks A — Mga Konstant na String ng AI](#apendiks-a--mga-konstant-na-string-ng-ai)
    - [Pagkilala at serialisasyon](#pagkilala-at-serialisasyon)
    - [Mga petsa at oras](#mga-petsa-at-oras)
    - [Dami at sukat — variable na sukat (metriko)](#dami-at-sukat--variable-na-sukat-metriko)
    - [Dami at sukat — variable na sukat (imperial / US)](#dami-at-sukat--variable-na-sukat-imperial--us)
    - [Presyo at halagang pera](#presyo-at-halagang-pera)
    - [Lokasyon at pagpapadala](#lokasyon-at-pagpapadala)
    - [Mga katangian ng produkto at kakayahang masubaybayan](#mga-katangian-ng-produkto-at-kakayahang-masubaybayan)
    - [Mga Numero ng Pambansang Reimbursement sa Healthcare (NHRN)](#mga-numero-ng-pambansang-reimbursement-sa-healthcare-nhrn)
    - [Healthcare, GMN, HIDRI, CPID, datos ng tao](#healthcare-gmn-hidri-cpid-datos-ng-tao)
    - [Panloob / gamit ng kumpanya](#panloob--gamit-ng-kumpanya)
15. [Apendiks B — Mga Konstant na Susi ng Interpretasyon](#apendiks-b--mga-konstant-na-susi-ng-interpretasyon)
    - [Petsa at oras](#petsa-at-oras)
    - [Petsa ng ani](#petsa-ng-ani)
    - [Prefix ng Kumpanya ng GS1](#prefix-ng-kumpanya-ng-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Bansa (ISO 3166)](#bansa-iso-3166)
    - [Pera (ISO 4217)](#pera-iso-4217)
    - [Temperatura](#temperatura)
    - [Kasarian (ISO 5218)](#kasarian-iso-5218)
    - [Mga espesye sa tubig (FAO)](#mga-espesye-sa-tubig-fao)
    - [Numero ng Stock ng NATO (NSN)](#numero-ng-stock-ng-nato-nsn)
    - [Mga produktong rolyo](#mga-produktong-rolyo)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Mga tagapagkilala ng SIM (EID / ICCID)](#mga-tagapagkilala-ng-sim-eid--iccid)
    - [Sanggunian ng sertipikasyon](#sanggunian-ng-sertipikasyon)
    - [GS1 UIC](#gs1-uic)
    - [Pagkakasunod ng kapanganakan ng sanggol](#pagkakasunod-ng-kapanganakan-ng-sanggol)
    - [Pandaigdigang Numero ng Modelo (GMN)](#pandaigdigang-numero-ng-modelo-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Mga halagang desimal at panukat](#mga-halagang-desimal-at-panukat)
    - [Mga koordinadang heograpiko](#mga-koordinadang-heograpiko)
    - [Paraan ng produksyon](#paraan-ng-produksyon)
    - [Uri ng midya ng AIDC](#uri-ng-midya-ng-aidc)
    - [Piraso mula sa kabuuan](#piraso-mula-sa-kabuuan)
    - [Mga hati ng bahagi](#mga-hati-ng-bahagi)
    - [Iba pa](#iba-pa)

---

## Pangkalahatang-tanaw

Ang `GaiaParser` ang pasukan para sa pag-parse ng mga element string ng GS1 Application Identifier (AI). Tinatanggap nito ang hilaw na output ng scanner sa alinman sa mga sumusunod na anyo, at nagbabalik ito ng nakabalangkas na `ParseResult` na naglalaman ng lahat ng natukoy na AI, ng mga error sa pagpapatunay, at (kung kailangan) ng mga interpretasyong mababasa ng tao:

- Payak na element string ng AI: `0109506000134352`
- Element string na may prefix na AIM Code ID: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Alinman sa mga nabanggit, na maaaring unahan ng 8-digit na correlation ID: `12345678~0109506000134352`

**Klase ng pasukan:** `tools.pantheum.gaia.GaiaParser`

> **Bago pa lang sa Gaia?** Magsimula sa **[Mabilisang Panimula sa GaiaParser](GaiaParser-QuickStart-Tagalog.md)** — ang dependency, ang unang pag-parse, at ang ilang bagay na madalas ikatalisod ng marami, sa loob ng humigit-kumulang sampung minuto. Ang gabay na ito naman ang buong sanggunian.

> Para sa kabaligtarang gawain — ang *pagbuo* ng mga wastong element string at Digital Link URI mula sa mga pares ng AI at halaga — tingnan ang **[GaiaBuilder — Gabay para sa Developer](GaiaBuilder-Tagalog.md)**.

---

## Tungkol sa GS1 at sa General Specifications

Ang **GS1** ay isang pandaigdigang organisasyong di-pangkalakal na bumubuo at nagpapanatili ng mga bukás na pamantayan para sa pagkilala sa supply chain at sa palitan ng datos. Ginagamit ang mga pamantayan nito sa retail, healthcare, logistics, foodservice, at sa marami pang ibang industriya, mula sa mga barcode ng produkto sa pakete ng mamimili hanggang sa serialisadong pagsubaybay sa dosis ng gamot.

Ang makapangyarihang sanggunian para sa lahat ng ipinatutupad ng parser na ito ay ang **GS1 General Specifications** — isang dokumento na naglalarawan ng:

- Lahat ng code ng Application Identifier (AI), ang kanilang pamagat ng datos, format, at panuntunan sa pagpapatunay
- Ang mga panuntunan sa sintaks para sa pagbuo at pag-encode ng mga element string ng AI
- Ang mga kinakailangan sa symbology ng barcode at ang pagtatalaga ng AIM Code ID
- Ang mga algoritmo para sa check digit at check character
- Ang paglutas sa dalawang-digit na taon (ang panuntunang sliding-window)
- Ang mga espesipikasyon ng Data Matrix, QR Code, GS1-128, GS1 DataBar, at iba pang carrier

Taun-taon inaayos ang GS1 General Specifications. Ang kasalukuyang edisyon at ang mga kaugnay na sanggunian ay makukuha sa:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

Ipinatutupad ng GAIA ang **Release 26.0 (Pinagtibay, Enero 2026)** ng GS1 General Specifications.

Ang mga GS1 Digital Link URI ay pinamamahalaan ng katuwang na pamantayang **GS1 Digital Link: URI Syntax**, na naglalarawan ng mga pangunahing susi sa pagkilala, ng pagkakasunod-sunod ng key qualifier, at ng pag-encode ng data attribute na ipinapatupad ng parser sa mga input na Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

Ipinatutupad ng GAIA ang **Release 1.7.0 (Pinagtibay, Agosto 2026)** ng pamantayang GS1 Digital Link: URI Syntax.

Ang mga sanggunian sa seksyon sa buong dokumentong ito ay tumutukoy sa GS1 General Specifications (halimbawa, "Table 7-5", "section 7.12"), maliban sa mga numero ng seksyon ng Digital Link (halimbawa, "§4.9", "§4.12") na tumutukoy naman sa pamantayang GS1 Digital Link: URI Syntax.

---

## Mga GS1 Application Identifier

Ang **GS1 Application Identifier (AI)** ay isang maikling numerikong prefix — dalawa hanggang apat na digit — na nagsasabi kung ano ang kahulugan at ang format ng datos na kaagad sumusunod dito. Nakalarawan ang mga AI sa GS1 General Specifications at sumasaklaw sila sa malawak na hanay ng datos sa supply chain: mga pagkakakilanlan ng produkto, petsa, dami, numero ng lote, serial number, sukat, URL, at iba pa.

### Ang balangkas ng isang elemento ng AI

Binubuo ang bawat elemento ng AI ng dalawang bahagi:

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

Laging numeriko ang code ng AI. Kaagad sumusunod dito ang halaga ng datos, at walang anumang delimiter sa pagitan ng code at ng halaga.

### Nakapirming haba laban sa variable ang haba

Nahahati sa dalawang uri ang mga AI:

| Uri | Gawi | Halimbawa |
|---|---|---|
| **Nakapirmi ang haba** | Tiyak ang bilang ng karakter, at laging binabasa nang buo | AI `01` (GTIN) — laging 14 na digit |
| **Variable ang haba** | Mula 1 hanggang sa pinakamataas na bilang ng karakter; nagwawakas sa isang separator na GS o sa dulo ng input | AI `10` (Batch/Lote) — 1 hanggang 20 alphanumeric na karakter |

Ang pagiging nakapirmi o variable ng isang AI ay natutukoy lamang mula sa depinisyon nito sa espesipikasyon ng GS1 — hindi nanghuhula ang parser.

### Mga element string na maraming AI

Maaaring pagdugtungin ang ilang AI sa iisang element string. Puwedeng dugtungan nang tuwiran ang mga AI na nakapirmi ang haba dahil laging alam ng parser kung ilang karakter ang babasahin. Ang mga AI namang variable ang haba ay dapat wakasan ng **karakter na GS** (ASCII `0x1D`, na kilala rin bilang FNC1 sa mga symbology ng barcode) tuwing may sumusunod pang AI, upang malaman ng parser kung saan nagtatapos ang isang halaga at kung saan nagsisimula ang susunod na code ng AI.

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

Sa mga literal na string sa Java, isulat ang karakter na GS gamit ang Unicode escape na `""`.

### Mga karaniwang AI

| AI | Pamagat ng datos | Format | Halimbawang halaga |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, iisang monetary area) |
| `710` | NHRN PZN | X..20 | `12345678` |

> Ang **ikaapat na digit** ng isang 4-digit na AI ng sukat o presyo ang nag-eencode ng bilang ng ipinahihiwatig na decimal place — ang `3103` ay netong timbang sa kilogramo na may 3 decimal (`001500` = 1.500 kg), samantalang babasahin naman ng `3102` ang gayunding mga digit bilang 15.00 kg. Ang haligi ng `Format` sa itaas ay nagpapakita ng format ng *datos*; ang buong `getFormatString()` ng bawat AI ay kasama na ang AI mismo (halimbawa, `N4+N6` para sa `3103`).

### Interpretasyong Mababasa ng Tao (HRI)

Sa kinaugaliang anyong mababasa ng tao, nakapaloob sa panaklong ang bawat code ng AI kaagad bago ang halaga nito, at may isang espasyo sa pagitan ng mga elemento:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Hindi ipinapakita sa HRI ang separator na GS. Ang `GS1AIObject.toHriString()` ang gumagawa ng format na ito.

### Mga code ng AI na apat na digit

May ilang AI na apat na digit imbes na dalawa. Ang unang dalawang digit ang nagsasabi ng pamilya ng AI; ang ikatlo at/o ikaapat na digit ang may dalang karagdagang kahulugan (gaya ng puwesto ng ipinahihiwatig na decimal point sa mga AI ng sukat). Kusang tinutukoy ng parser ang buong code ng AI mula sa element string — laging buong code ang ginagamit ng tumatawag (halimbawa, `"3102"`, hindi `"31"` lamang).

---

## Mabilisang Panimula

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

> **Separator na GS:** Ang mga AI na variable ang haba sa loob ng isang string na maraming AI ay dapat paghiwalayin ng karakter na GS (ASCII `0x1D`). Gamitin ang `""` sa mga literal na string sa Java.

---

## Ang Daloy ng Pag-parse

### Paunang yugto — Mga Input Modifier

Kung may dalang anumang **input modifier** ang `ParseConfig`, tumatakbo sila bago ang lahat — bago ang pag-alis ng correlation, bago ang pagtukoy ng carrier, bago pumasok sa daloy ng GS1. Muling isinusulat ng bawat modifier ang hilaw na input para sa susunod, at ang output ng tanikala ang pinagtatrabahuhan ng lahat ng yugto sa ibaba.

Walang modifier na nakatakda bilang default, kaya walang ginagawa ang paunang yugtong ito maliban kung ikaw mismo ang pumili. Tingnan ang [Mga Input Modifier](#mga-input-modifier).

---

### Yugto 0 — Correlation ID

Bago ang anumang pagproseso ng GS1, sinusuri ng `GaiaParser` kung nagsisimula ang input sa isang opsyonal na **prefix na correlation ID**: eksaktong 8 ASCII decimal digit na sinusundan ng tilde (`~`), halimbawa `12345678~`.

Kung naroon ang prefix, inaalis ito at itinatabi bilang isang `CorrelationInfo` sa ibinabalik na `ParseResult`. Ang lahat ng sumusunod na yugto ay gumagana sa payload nang wala na ang prefix. Kung walang prefix, dumadaan ang input nang walang pagbabago.

Tingnan ang [Correlation ID](#correlation-id) para sa mga detalye.

---

### Yugto 1 — Pagruruta ng Input

Pagkatapos alisin ang correlation, sinusuri ng `GaiaParser` kung nagsisimula ang (naalisang) input sa isang **AIM Code ID**: isang tatlong-karakter na prefix na may anyong `]` + letrang ASCII + digit na ASCII (halimbawa, `]C1` para sa GS1-128, `]d2` para sa GS1 DataMatrix, `]e0` para sa GS1 DataBar / GS1 Composite).

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

Kung hindi kayang magdala ng GS1 AI ang carrier (halimbawa, isang postal barcode), kaagad huminto ang pag-parse at may lumalabas na error na `GE-D002`.

---

### Yugto 2 — Sintaks

Laging tumatakbo. Binubuo ito ng dalawang hakbang:

**2a. Pagti-token (`AISyntaxParser`)**
- Binabasa ang haba ng code ng AI mula sa unang dalawang karakter gamit ang talaan ng prefix ng GS1 (GS1 General Specifications Table 7-5).
- Ang mga AI na nakapirmi ang haba ay bumabasa ng tiyak na bilang ng byte mula sa input.
- Ang mga AI namang variable ang haba ay binabasa hanggang sa isang karakter na GS o sa dulo ng input.
- Ang mga AI na maraming bahagi ay hinahati ang kanilang halaga sa mga segment ayon sa bawat bahagi.

**2b. Pagpapatunay ng balangkas (`SyntaxValidator`)**
- Sinusuri kung may nadobleng AI (`GE-S004`).
- Sinusuri ang mga kinakailangang pagdepende ng AI, halimbawa, kailangan ng AI `02` ang AI `37` (`GE-S005`).
- Sinusuri ang mga ipinagbabawal na pares ng AI (`GE-S006`).

Ang mga error sa yugtong ito ay may antas na `SYNTAX_ERROR` (tokeniser) o `INTEGRITY_ERROR` (balangkas). Kung may **anumang** error — tokeniser man o balangkas — humihinto ang daloy at nilalaktawan ang mga yugto ng nilalaman at interpretasyon.

---

### Yugto 3 — Nilalaman

Tumatakbo lamang kapag walang error na nabuo sa Yugto 2 (tokeniser man o balangkas). Ganito ang daloy sa bawat elemento (tumatakbo lamang ang bawat hakbang kung walang error ang naunang hakbang):

| Hakbang | Tagapagpatunay | Mga code ng error |
|---|---|---|
| Pagsusuri sa regex | `RegexValidator` | `GE-C001` |
| Charset at format ng bahagi | `ComponentValidator` | `GE-C005` + mga code ng format ayon sa kondisyon (`GE-C054`–`GE-C115`) |
| Check digit / check character | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Pasadyang pagpapatunay ng kahulugan | `ContentValidatorRegistry` | mga code ng nilalaman ayon sa kondisyon (`GE-C116`–`GE-C170`) |

Ang mga error sa yugtong ito ay may antas na `FORMAT_ERROR` o `DATA_ERROR`, maliban sa isa: ang mga
pagsusuri sa prefix ng kumpanya ng GS1 sa mga AI na susi ng GS1 ay pahiwatig lamang at may antas
na `WARNING` (tingnan ang [Sanggunian sa Error](#sanggunian-sa-error)), kaya ang isang hindi nakikilalang
prefix ng kumpanya ay hindi nag-iisang dahilan upang maging di-wasto ang resulta.

---

### Yugto 4 — Interpretasyon

Tumatakbo lamang sa modong `INTERPRETATION` at kapag walang elementong may dalang error mula sa alinmang naunang yugto. Pinayayaman ng `InterpretationEngine` ang bawat elemento ng may-label na metadata:

- Mga petsang muling isinaayos bilang `dd/mm/yyyy`
- Paghati sa check digit ng GTIN at paghanap sa prefix ng kumpanya ng GS1
- Mga pangalan ng bansa ayon sa ISO 3166
- Mga pangalan at simbolo ng pera ayon sa ISO 4217
- Mga nadecode na halagang desimal
- Mga bahagi ng HRI (Interpretasyong Mababasa ng Tao)

Ikinakabit ang mga resulta bilang mga entry na `GS1AIInterpretation` sa bawat `GS1AIObjectElement`.

---

## Pagsasaayos ng Pag-parse (`ParseConfig`)

Dalawang pasukan lamang ang inilalantad ng `GaiaParser`:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

Tumatakbo ang `parse(String)` sa **default na pagsasaayos**: modong `INTERPRETATION`, mga petsang little-endian (`dd/mm/yyyy`) na may separator na `/` at apat na digit na taon, at mga mensahe ng error sa **Ingles**. Upang baguhin ang alinman sa mga ito — pati na ang modo ng pag-parse — bumuo ng isang `ParseConfig` sa pamamagitan ng matatas nitong builder at gamitin ang overload na may dalawang argumento.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Nasa `GaiaConstants` ang lahat ng enum ng mga opsyon.

### Mga opsyon

| Method ng builder | Enum (`GaiaConstants`) | Default | Epekto |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Lalim ng daloy — tingnan ang [Mga Modo ng Pag-parse](#mga-modo-ng-pag-parse). |
| `language(...)`      | `Language`      | `ENGLISH`        | Wika ng mga mensahe ng error, ng mga label ng interpretasyon, **at** ng mga paglalarawan ng AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Pagkakasunod ng bahagi ng petsa: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Karakter sa pagitan ng mga bahagi ng petsa: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) o `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) o `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Nilalaktawan ang pagsusuring pambalangkas na "requires" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Nilalaktawan ang pagsusuring pambalangkas na "excludes" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / pangalan ng klase | wala | Code na muling sumusulat sa hilaw na input bago mag-parse — dalawang [nakapaloob na modifier](#mga-nakapaloob-na-modifier) at kung ano pa ang isusulat mo. Tingnan ang [Mga Input Modifier](#mga-input-modifier). |

Ang apat na opsyon sa petsa ay nakaaapekto lamang sa mga naka-format na string ng petsa na ginagawa ng mga tagapagpayaman ng interpretasyon (sa modong `INTERPRETATION`); hindi nila binabago ang pagpapatunay. Maaaring laktawan ang mga halaga sa builder — ang anumang opsyong hindi itinakda (o binigyan ng `null`) ay mananatili sa default nito.

### Mga naisalokal na mensahe at label

Pinipili ng `language(...)` ang wika para sa **tatlong** uri ng tekstong mababasa ng tao: ang mga mensahe ng error, ang mga label ng interpretasyon (ang `getLabel()` ng bawat `GS1AIInterpretation`), at ang mga paglalarawan ng AI (ang `getDescription()` ng bawat `GS1AIObjectElement`).

**35 wika** ang tinutukoy ng `GaiaConstants.Language`, na sumasaklaw sa mga wikang pinakamaraming nagsasalita sa mundo: Ingles, Pranses, Espanyol, Aleman, Italyano, Portuges, Olandes, Polako, Ruso, Ukranyano, Tseko, Suweko, Tsino, Hapon, Koreano, Arabe, Indonesyano, Hindi, Turko, Bengali, Urdu, Byetnames, Nigerian Pidgin, Arabeng Ehipsiyo, Marathi, Telugu, Tamil, Cantonese, Wu Chinese, Tagalog, Persiyano, Hausa, Punjabi, Javanese, at Swahili.

Kalagayan ng pagsasalin (sa inilabas na bersyon):
- **Mga label ng interpretasyon** — naisalin sa lahat ng wika.
- **Mga mensahe ng error** — naisalin sa lahat ng wika.
- **Mga paglalarawan ng AI** — naisalin sa lahat ng wika maliban sa Ingles. Hindi hiwalay na katalogo ang Ingles: tuwirang binabasa ito mula sa field na `description` ng entry ng AI sa `gs1-application-identifiers.jsonld`, na siyang huling babalikan ng bawat paglalarawan ng AI.

Ang Nigerian Pidgin (`NIGERIAN_PIDGIN`), isang creole na nakabatay sa Ingles, ay sinasadyang gumagamit muli ng tekstong Ingles para sa mga label ng interpretasyon at mga mensahe ng error. Ang mga paglalarawan ng AI ang naiibang bahagi: isinalin ang mga ito sa tunay na pananalitang Pidgin sa halip na gamitin muli ang Ingles, dahil hiwalay na ginawa ang mga katalogo ng paglalarawan ng AI sa mga katalogo ng label at mensahe. Dapat suriin ng mga katutubong tagapagsalita ang mga saling-makina bago sila pagkatiwalaan sa produksyon.

Ang anumang mensahe, label, o paglalarawan na wala sa katalogo ng isang wika ay babalik sa Ingles. Ang mga wikang mula-kanan-pakaliwa (Arabe, Urdu, Arabeng Ehipsiyo, Persiyano) ay wastong nakaimbak bilang string; ang pagpapakita ng mga ito nang RTL ay tungkulin ng display layer.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Gayundin ang paraan ng pagsasalokal sa mga label ng interpretasyon (hindi nagbabago ang mga halaga — ang mga label lamang):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Gayundin ang paraan ng pagsasalokal sa mga paglalarawan ng AI (tanging ang `getTitle()`, halimbawa `"GTIN"`, ang hindi isinasalokal):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Pag-format ng petsa

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Mga Input Modifier

Ang **input modifier** ay code na muling sumusulat sa hilaw na input string bago mag-parse ang Gaia. Para sa input na dumarating nang sira na ang mga modifier — isang scanner na pinapalitan ng nakikitang karakter ang separator na GS, isang middleware na binabalot ang payload sa isang prefix na pag-aari ng vendor, isang host system na ginagawang malaking titik ang lahat. Sa halip na linisin ang bawat string sa bawat lugar na tumatawag (at sa gayon ay may isang lugar na magkakamali nang bahagya), irehistro nang minsanan ang pagsasa-normal sa `ParseConfig` at ipaubaya na sa parser ang pagsasagawa nito.

Tumatakbo ang mga modifier sa pinakasimula ng `GaiaParser.parse(...)` — bago alisin ang correlation ID, bago tukuyin ang AIM Code ID, bago pumasok sa daloy ng GS1. Ang muling naisulat na string na lamang ang nakikita ng lahat ng kasunod. Kasama na ang dalawang [nakapaloob na modifier](#mga-nakapaloob-na-modifier), **walang anumang nakatakda bilang default** — ikaw ang pipili sa bawat `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Mga nakapaloob na modifier

May dalawang modifier na kasama sa core jar sa ilalim ng `tools.pantheum.gaia.modifier.custom`. Hinahawakan nila ang dalawang pinakakaraniwang paraan ng pagkasira ng payload ng GS1 — ang mga nakalimbag na panaklong ng HRI na napagkamalang datos, at ang mga sobrang espasyo — kaya hindi na kailangang magsulat ng sariling klase para sa karaniwang pagkakataon:

| Klase | `getName()` | Ang ginagawa nito |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Inaalis ang mga panaklong ng HRI sa paligid ng bawat AI (`(01)…(10)…`) at ibinabalik ang separator na FNC1 na ipinahihiwatig ng mga ito. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Inaalis ang bawat espasyo (`0x20`) mula sa element string ng AI. |

Pawang payak na pagpapatupad ng `ModifierInterface` ang dalawa at walang natatanging katayuan — pareho silang nairerehistro, naisusunod, naiuulat, at nabibigo tulad ng sarili mong isusulat:

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

Pareho silang walang kalagayan at ligtas sa thread, kaya maaaring pagsaluhan ng lahat ang iisang instance; at para sa mga deployment na nakabatay sa configuration file, maaari silang pangalanan sa pamamagitan ng buong pangalan ng klase (tingnan ang [Pagrerehistro ng mga modifier](#pagrerehistro-ng-mga-modifier)).

#### `ModifierRemoveAIBrackets`

Nililimbag ng interpretasyong mababasa ng tao ng GS1 ang bawat AI sa loob ng panaklong — `(01)09521234543213(10)ABC123` — isang kaugalian lamang sa paglilimbag. Ang anumang scanner o middleware na nakatakdang magpadala ng HRI ay ipinapasa ang mga panaklong na iyon na parang datos, at hindi alam ng tokeniser kung ano ang gagawin sa kanila.

Kalahati lamang ng trabaho ang pag-alis ng mga panaklong. Sa HRI, ang pambukas na `(` ng *susunod* na AI ang nag-iisang tanda ng pagwawakas ng naunang halaga, kaya sa anyong may panaklong ay hindi na kailangan ng FNC1 ng isang AI na variable ang haba. Basta-basta mong alisin ang mga panaklong at mawawala ang hangganang iyon:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Kaya nga **muling naglalagay ng FNC1 ang modifier na ito sa bawat hangganan kung saan variable ang haba ng naunang AI**, at tumpak na ibinabalik ang mismong hatian na ini-encode ng mga panaklong:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Hinahanap ang haba mula sa sariling `AiDefinitionRegistry` ng parser, kaya nahahawakan nito ang bawat AI na variable ang haba sa halip na isang listahang nakasulat na sa code. May tatlong sitwasyong sinasadyang hindi ginagalaw: ang halagang nagtatapos na sa FNC1 (hindi nakakakuha ng pangalawang separator ang pinagmulang nagpapadala ng dalawang kaugalian), ang naka-panaklong na code na hindi kilalang AI (hindi nagsasabi ng haba nito ang isang di-kilalang AI), at ang huling AI sa string.

**Idempotent** ang muling pagsusulat na ito — patakbuhin ito sa sarili nitong output at walang magbabago — kaya ligtas ito kahit sa halong daloy kung saan may panaklong lamang ang ilang input.

> **Hangganan.** Ang `(` at `)` ay wastong mga karakter ng datos ng GS1, at ang pattern na ginagamit dito ay `\((\d{2,4})\)` lamang. Kung nagkataong may nakapanaklong na dalawa-hanggang-apat na digit na bilang ang isang halaga, maaalis din ang mga panaklong nito. Ipatupad ito sa pinagmulang gumagamit ng kaugaliang panaklong ng HRI lamang, hindi sa isang tunay na may mga halagang may panaklong.

#### `ModifierRemoveSpaces`

May ilang scanner, middleware, at sistema ng paglilimbag ng label na nagdaragdag ng sobrang espasyo sa isang element string na maayos naman sana — upang punuin ang isang field na nakapirmi ang lapad, upang paghiwalayin ang mga pangkat na madaling basahin, o upang balutin ang isang mahabang halaga. Itinuturing ng tokeniser na datos ang bawat espasyo, kaya nasisira ang halagang kinaroroonan nito, at para sa isang AI na variable ang haba ay naiuusod ang lahat ng kasunod.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Ang ASCII `0x20` lamang ang inaalis. Nananatili sa kinalalagyan ang ibang whitespace — halimbawa, ang tab ay wala sa naiene-encode na set ng GS1, kaya iniuulat ito ng parser bilang `GE-S008` sa halip na tahimik itong lunukin.

> **Hangganan.** Bahagi ng di-nagbabagong set ng karakter ng GS1 ang espasyo (`0x20`), kaya maaaring may wastong espasyo ang isang batch/lote o ang numero ng piyesa ng kostumer. Hindi kayang tukuyin ng modifier ang pagkakaiba ng sobrang espasyo at ng tunay na espasyo; ipatupad ito sa pinagmulan lamang na alam mong hindi gumagamit ng espasyo sa mga halaga ng AI nito.

#### Nilalaktawan ang mga prefix, hindi muling isinusulat

Tumatakbo ang mga modifier habang wala pang anumang inaalis ang parser, kaya maaaring may dala pa ring correlation ID, AIM Code ID, at tagapagpahiwatig ng ECI ang hilaw na input. Ang dalawang nakapaloob na modifier ay gumagamit ng mismong lohika ng parser na `CorrelationIdParser` at `DataCarrierParser` upang hanapin ang simula ng element string ng AI, doon nagsisimulang muling sumulat, at pinagdurugtong muli ang resulta at ang prefix na **hindi nagalaw**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Ang mga carrier na EAN/UPC na pinupunan ang halaga hanggang GTIN-14 (`isRequiresGtinPadding()`) ay buong nilalaktawan — ang payload nila ay isang hilaw na numerikong halaga ng barcode na walang balangkas ng AI, kaya walang saysay doon ang mga panaklong at ang mga espasyo.

#### Pagkakasunod: espasyo muna, saka panaklong

Kapag ginagamit ang dalawa, **irehistro muna ang `ModifierRemoveSpaces`**. Sensitibo sa puwesto ang pagtutugma ng panaklong: hindi tumutugma sa `\((\d{2,4})\)` ang isang `( 01 )` na may espasyo, kaya nananatili ang mga panaklong at hindi na kailanman bumabalik ang separator na ipinahihiwatig nila.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Pagsulat ng isang modifier

Kung walang bagay sa dalawang nakapaloob na modifier, sumulat ng sarili mo — isang method lamang ang nasa interface.

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

Kung nakadepende sa pagsasaayos ng pag-parse ang muling pagsusulat, i-override ang anyong may dalawang argumento:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Ang kasunduan:

| Panuntunan | Detalye |
|---|---|
| Walang kalagayan at ligtas sa thread | Iisang instance ng bawat klase ang iniimbak at pinagsasaluhan ng lahat ng pag-parse. |
| Pampublikong constructor na walang argumento | Kailangan lamang kapag pinangalanan ang modifier sa pamamagitan ng pangalan ng klase. |
| Hawakan ang `null` at ang walang lamang input | Hindi sinasala ng parser ang mga ito bago tumakbo ang tanikala. |
| Ang pagbabalik ng `null` ay nangangahulugang "walang binago" | Ipinapasa ang naunang halaga. Ibalik ang `input` nang buo kapag hindi angkop ang modifier. |
| Mas mabuti ang ibalik nang buo kaysa maghagis ng exception | Pinipigil ng modifier na naghahagis ng exception ang buong pag-parse — tingnan ang [Paghawak sa pagkabigo](#paghawak-sa-pagkabigo-ng-modifier). |
| `getName()` | I-override ito upang piliin ang pangalang iuulat sa `ModifierInfo`; ang default ay ang payak na pangalan ng klase. |

### Pagrerehistro ng mga modifier

Tumatakbo ang mga modifier sa pagkakasunod na idinagdag mo sila, at natatanggap ng bawat isa ang output ng nauna. Irehistro sila sa pamamagitan ng instance, ng buong pangalan ng klase, o ng isang listahan ng alinman sa dalawa:

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

Pinapangalanan din ang mga [nakapaloob na modifier](#mga-nakapaloob-na-modifier) tulad ng sa sarili mo — **laging sa buong pangalan**. Walang maikling pangalan o paghahanap ng alias para sa kanila; nilulutas ng `ModifierRegistry` ang bawat modifier, kasama man ito sa core o hindi, sa pamamagitan ng buong pangalan ng klase.

Ang `ModifierRegistry` ang lumulutas sa mga pangalan; minsan lamang nitong ginagawa ang isang instance ng bawat klase sa pamamagitan ng constructor nitong walang argumento, at iniimbak iyon para sa bawat kasunod na pagsasaayos na pumapangalan sa gayunding klase. Nangyayari ang paglutas na ito **sa paggawa ng pagsasaayos**, kaya ang pangalang hindi matagpuan, ang hindi nagpapatupad ng `ModifierInterface`, o ang hindi mabuong instance ay naghahagis kaagad ng `IllegalArgumentException` doon mismo — hindi tahimik sa oras ng pag-parse. Ang modifier na hindi mabubuo sa pamamagitan ng reflection (halimbawa, dahil may dala itong dependency na ininiksyon) ay maaaring irehistro nang maaga upang manatiling mapapangalanan:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Pagsusuri sa ginawa ng modifier

Kapag may nakatakdang mga modifier, ang **binagong** input ang ipinapakita ng `ParseResult.getPayload()`. Nananatili sa `ModifierInfo` ang orihinal:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

Iniuulat ng `getAppliedModifiers()` ang `getName()` ng bawat modifier, na ang default ay ang payak na pangalan ng klase ngunit ino-override ito ng dalawang nakapaloob na modifier — kaya ang tanikalang binubuo ng dalawang ito ay nagpapakita ng mga pangalang pandisplay sa halip na ng pangalan ng klase:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

Kapag walang nakatakdang modifier, `null` ang ibinabalik ng `getModifierInfo()`. Kung tumakbo ang mga modifier ngunit ibinalik ng bawat isa ang input nang buo, mananatili ang impormasyon ngunit `false` ang `isModified()` — ang mga modifier lamang na tunay na nagbago ng input ang lumilitaw sa `getAppliedModifiers()`.

### Paghawak sa pagkabigo ng modifier

Pinipigil ng modifier na naghahagis ng exception ang buong pag-parse. Ibinabalot ang exception na iyon sa isang `GaiaModifierException` na pumapangalan sa modifier na may kasalanan, at may dalang panloob na error na `GE-I001` ang resulta na may gayunding pangalan sa mensahe nito; ipinapakita ng `getPayload()` ang input na hindi nabago. Sinasadyang **hindi nagpapatuloy** ang pag-parse gamit ang isang string na kalahati ang naisulat — ang isang hakbang ng pagsasa-normal na tahimik na nabigo ay nagdudulot ng mga resultang mukhang wasto ngunit na-parse mula sa maling input.

---

## Mga Modo ng Pag-parse

Ipinangalan ang bawat modo sa pinakamalalim na [yugto ng daloy](#ang-daloy-ng-pag-parse) na pinatatakbo nito; tumatakbo pa rin ang bawat naunang yugto.

| Modo | Hanggang saan tumatakbo | Ano ang sinasagot |
|---|---|---|
| `DATA_CARRIER` | Yugto 1 (pagruruta ng input) | Anong symbology ang nagdala nito? |
| `SYNTAX` | Yugto 2 (sintaks) | Wasto ba ang anyo ng mga code at haba ng AI? |
| `CONTENT` | Yugto 3 (nilalaman) | Wastong datos ba ng GS1 ang mga halaga? |
| `INTERPRETATION` | Yugto 4 (interpretasyon) | Ano ang ibig sabihin ng mga halaga? |

### Modong DATA_CARRIER

Humihinto pagkatapos ng Yugto 1 — pinapatunayan nito ang AIM Code ID at tinutukoy ang symbology, ngunit hindi ito pumapasok sa daloy ng pag-parse ng AI. Kapaki-pakinabang sa pagtukoy ng symbology at sa pagruruta nang walang bigat ng buong pagpapatunay.

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

**Kailan gagamitin:** Kapag kailangang malaman ng iyong aplikasyon ang uri ng barcode bago magpasya kung paano hahawakan ang payload — halimbawa, sa pagruruta ng 1D at 2D na symbology sa magkaibang tagahawak. Para sa pagrurutang iyon, gamitin ang may-uring [`DataCarrierType`](#datacarrierentry-at-datacarriertype) (`getDataCarrier().getDataCarrierType()`) sa halip na magtugma ng string sa `getName()`.

---

### Modong SYNTAX

Humihinto pagkatapos ng Yugto 2. Kapaki-pakinabang sa pagsala ayon sa balangkas nang walang gastos ng pagpapatunay ng nilalaman.

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

**Kailan gagamitin:** Kapag gusto mong tiyakin na tama ang mga code ng AI at ang haba ng datos bago pumasok sa buong pagpapatunay, o kapag nag-i-scan ka nang maramihan kung saan bihira ang mga error sa nilalaman.

---

### Modong CONTENT

Humihinto pagkatapos ng Yugto 3.

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

> Karamihan sa mga AI ay hindi makatatayo nang mag-isa: ang AI `10` (BATCH/LOT), `17`
> (USE BY or EXPIRY) at `21` (SERIAL) — *kailangan* ng bawat isa ng susi sa pagkilala
> gaya ng AI `01` sa gayunding element string; kaya alisin mo ang GTIN sa halimbawa sa
> itaas at mabibigo na ito sa Yugto 2 nang may `GE-S005` bago pa man marating ang
> pagpapatunay ng nilalaman. Upang mag-parse ng mga bahaging sinasadyang walang
> katuwang na AI, itakda ang `skipRequiresCheck(true)` sa `ParseConfig`.

**Kailan gagamitin:** Kapag kailangan mong malaman na ganap na sumusunod sa GS1 ang isang na-scan na halaga bago ito gamitin sa isang proseso ng negosyo, ngunit ayaw mo ng bigat ng pagpapayaman ng interpretasyon.

---

### Modong INTERPRETATION (default)

Pinatatakbo ang buong daloy hanggang Yugto 4. Ito ang default kapag tinawag ang `parse(String)` nang walang argumentong modo. Pinapayaman lamang nito ang mga elementong malinis na nakalusot sa pagpapatunay ng nilalaman.

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

**Halimbawang output:**
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

**Halimbawa ng halagang pera (AI 3932 — presyong may code ng pera ayon sa ISO):**
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

**Kailan gagamitin:** Sa paggawa ng display layer, ng kasangkapan sa pagsuri ng label, o ng anumang UI na nangangailangan ng paghahati ng mga halaga ng AI nang madaling maunawaan ng tao.

---

## Correlation ID

May ilang daloy ng trabaho na naglalagay ng pag-aaring 8-digit na tagapagkilala ng correlation sa unahan ng hilaw na input ng GS1 upang maiugnay ang mga pangyayaring pag-scan sa isang session o transaksyon. Ganito ang anyo:

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

Ang `~` (tilde) ang separator. **Hindi** ito bahagi ng nilalaman ng GS1 — inaalis ito bago magsimula ang anumang pag-parse ng GS1.

### Mga panuntunan sa pagtukoy

Natutukoy ang prefix kapag nagsisimula ang input sa eksaktong 8 ASCII decimal digit (`0`–`9`) na kaagad sinusundan ng `~`. Kung hindi `~` ang ikasiyam na karakter, o kung may alinman sa unang 8 karakter na hindi digit, ituturing ang input na payak na nilalaman ng GS1 na walang prefix ng correlation.

### Pagkuha sa correlation ID

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

### Pagsasama sa AIM Code ID

Maaaring lumitaw ang prefix ng correlation bago ang isang AIM Code ID. Malinaw na hinahawakan ito ng parser:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Klase ng pagpapatupad:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Ang **GS1 Digital Link** ay tuwirang nag-eencode ng isa o higit pang halaga ng AI sa balangkas ng isang HTTP(S) URL, na nagbibigay ng mga tagapagkilalang malulutas sa web para sa pisikal na produkto. Ipinapatupad ng GAIA ang *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) para sa mga URI na **hindi siniksik**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

Kusang nakikilala ng `GaiaParser` ang mga Digital Link URI — ang anumang input na nagsisimula sa `http://` o `https://` ay iniruruta sa `GS1DLParser`, na nagpapatakbo ng gayunding yugto ng nilalaman at interpretasyon gaya ng daloy ng element string.

### Ang balangkas ng URI at ang mga papel ng AI

Bawat AI sa isang Digital Link URI ay may isa sa tatlong papel, na makukuha sa bawat `GS1AIObjectElement` sa pamamagitan ng `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Papel | Lokasyon | Halimbawa |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Unang pares na `/ai/value` ng path (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Mga sumusunod na pares sa path, nakasunod ayon sa pangunahing susi (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Mga query parameter na pawang numeriko ang susi (§4.10) | `?17=271231` |

Mga panuntunang pambalangkas na ipinatutupad (`DLPathRules`):
- Eksaktong **isang** pangunahing susi sa pagkilala sa path; dapat i-encode bilang query data attribute ang mga karagdagang susi.
- Dapat tanggapin ng pangunahing susi ang mga key qualifier at dapat silang lumitaw sa itinakdang pagkakasunod. Maaaring laktawan ang mga opsyonal na qualifier, ngunit ang mga *naroroon* ay dapat pa ring sumunod sa nakapirming pagkakasunod — tingnan ang [Pagkakasunod ng qualifier](#pagkakasunod-ng-qualifier).
- Maaaring maunahan ng anumang pasadyang segment ng path ang pangunahing susi (halimbawa, `/products/au/01/...`); kunin sila sa pamamagitan ng `getDigitalLinkInfo().getCustomPathStem()`.
- Binabalewala ang mga query key na hindi numeriko (`linkType`, `context`, at mga extension parameter gaya ng `23P`); ang mga susing pawang numeriko ay dapat wastong AI na may markang `validAsDataAttribute`.
- Dine-decode ang mga karakter ng halagang naka-percent-encode; hindi pinapayagan ang AI `(03)` at `(8014)`.

Ang mga pangunahing susi at ang tinatanggap nilang pagkakasunod ng qualifier ay **hango sa datos** mula sa mga depinisyon ng AI — ang bandilang `gs1DigitalLinkPrimaryKey` at ang katangiang `gs1DigitalLinkQualifiers` — sa halip na nakasulat na sa code.

Ang anumang paglabag sa balangkas, o ang input na hindi URL, ay nagdudulot ng error na pambalangkas ng Digital Link (`GE-L001`–`GE-L014`, isang code sa bawat kondisyon). Ang nahating metadata ng URL (`scheme`, `domain`, `path`, `customPathStem`, `query`, at ang `java.net.URL`) ay makukuha pa rin sa pamamagitan ng `getDigitalLinkInfo()` kahit may mga error sa balangkas.

### Pagkakasunod ng qualifier

Para sa bawat pangunahing susi, naglilista ang `gs1DigitalLinkQualifiers` ng isa o higit pang **nakasunod** na serye ng qualifier. Sa loob ng isang serye, **opsyonal** ang isang AI na nakabalot sa kuwadradong panaklong, at **kailangan** naman ang AI na walang panaklong — katulad ng notasyong `[cpv-comp]` ng ABNF sa §4.9. Ang mga serye para sa isang pangunahing susi ay magkakabukod na alternatibo.

Ang GTIN (`01`), halimbawa, ay may dalawang serye:

| Path | Serye | Kahulugan |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — pawang opsyonal, ngunit nakapirmi sa pagkakasunod na ito |
| upui-path | `235` | TPX (kailangan); GTIN + TPX = UPUI |

Kaya wasto ang `/01/09506000134352/10/LOT-ABC/21/SER` (LOT bago ang SER, nilaktawan ang CPV), **tinatanggihan** naman ang `/01/.../21/SER/10/LOT-ABC` (mali ang pagkakasunod), at ang `/01/09506000134352/235/2ABC456` ang upui-path. Ang pagsusuri sa pagkakasunod ay isang pagtutugma ng subsequence na iniingatan ang pagkakasunod, kaya maaaring laktawan ang mga opsyonal na AI ngunit hindi kailanman baguhin ang pagkakasunod nila.

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

**Klase ng pagpapatupad:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Pagtatrabaho sa mga Resulta

### ParseResult

Ang pinakamataas na resultang ibinabalik ng `GaiaParser.parse()`.

| Method | Ibinabalik | Paglalarawan |
|---|---|---|
| `isValid()` | `boolean` | `true` kung walang error sa anumang antas. Hindi nakaaapekto ang mga babala sa pagiging wasto. Laging `true` kapag `null` ang `getAiObject()`. |
| `getPayload()` | `String` | Ang input string matapos alisin ang prefix ng correlation — at matapos itong isulat muli ng anumang [input modifier](#mga-input-modifier). |
| `getPayloadContent()` | `String` | Ang payload na inalisan ng AIM Code ID at ng prefix ng ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (isang data carrier na tinanggihan dahil hindi GS1, halimbawa ang carrier na `]A0` ng Code 39), o `UNABLE_TO_DETERMINE_CONTENT` (kapag `null` ang `aiObject`, halimbawa sa modong `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Ang itinakdang lalim ng daloy (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Ang pinakamalalim na yugtong tunay na narating ng pag-parse — tingnan sa ibaba. |
| `isParseComplete()` | `boolean` | `true` kung narating ng pag-parse ang hiniling na lalim (`achieved == requested`). Hiwalay sa `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Lahat ng natukoy na AI. `null` sa modong `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Lahat ng error na hindi WARNING (antas ng object + lahat ng antas ng elemento). |
| `getWarnings()` | `List<GaiaError>` | Lahat ng pahiwatig na WARNING (antas ng object + lahat ng antas ng elemento). |
| `hasWarnings()` | `boolean` | `true` kung may anumang pahiwatig na WARNING na lumitaw. |
| `getIssues()` | `List<GaiaError>` | Pinagsamang mga error at babala. |
| `hasDataCarrier()` | `boolean` | `true` kung may nakilalang AIM Code ID. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata ng symbology, o `null` kung walang natukoy na carrier. |
| `hasEci()` | `boolean` | `true` kung may inalis na tagapagpahiwatig ng ECI mula sa payload. |
| `getEci()` | `EciEntry` | Metadata ng pag-encode ng ECI, o `null`. |
| `hasCorrelationId()` | `boolean` | `true` kung may prefix na correlation na `DDDDDDDD~` sa orihinal na input. |
| `getCorrelationInfo()` | `CorrelationInfo` | Ang nakuhang correlation ID, o `null` kung wala. |
| `isInputModified()` | `boolean` | `true` kung may [input modifier](#mga-input-modifier) na nagbago sa input. |
| `getModifierInfo()` | `ModifierInfo` | Ang ginawa ng tanikala ng modifier — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` kung walang nakatakdang modifier. |
| `getTiming()` | `ProcessingTiming` | Ang tunay na tagal ng pag-parse — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` kung hindi ito ginawa ng `GaiaParser`. |
| `getVersion()` | `String` | Ang bersyon ng library na gumawa ng resulta. |

#### Hiniling laban sa naabot na modo ng pag-parse

Tumatakbo ang daloy sa hagdanang **SYNTAX → CONTENT → INTERPRETATION** at maagang humihinto kapag may error, kaya maaaring mas mababaw ang modong tunay na *naabot* kaysa sa modong *hiniling*. Iniuulat ng `getAchievedParseMode()` kung hanggang saan ito nakarating:

| Hiniling | Ano ang nangyayari | Naabot | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | isang error sa **sintaks / balangkas** ang pumipigil sa pag-parse matapos ang pagti-token | `SYNTAX` | `false` |
| `INTERPRETATION` | isang error sa **nilalaman** (maling format/check digit) ang humaharang sa pagpapayaman | `CONTENT` | `false` |
| `CONTENT` | laging natatapos ang nilalaman (naitatala ang mga error, hindi nakamamatay) | `CONTENT` | `true` |
| alinman (malinis na input) | narating ng daloy ang hiniling na lalim | = hiniling | `true` |
| `DATA_CARRIER` | napatunayan ang carrier; walang na-parse na nilalaman ng AI | `DATA_CARRIER` | `true` |
| alinman | tinanggihan ang data carrier bago ang pag-parse ng AI (halimbawa, isang carrier na `]A0` na hindi GS1) | `SYNTAX` | `false` |

Hiwalay ang `isParseComplete()` sa `isValid()`: ang pag-parse na `CONTENT` ng isang GTIN na maling check digit ay **kumpleto** (natakbo nito ang yugto ng nilalaman) ngunit **di-wasto** (nabigo ang check digit). Gamitin ang `isParseComplete()` upang itanong ang "narating ba ng daloy ang lalim na hiniling ko?" at ang `isValid()` naman para sa "maayos ba ang anyo ng datos?".

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

Ang koleksyon ng mga natukoy na elemento ng AI.

| Method | Paglalarawan |
|---|---|
| `getAis()` | Lahat ng instance ng `GS1AIObjectElement` ayon sa pagkakasunod sa input. |
| `get(String aiCode)` | Ang unang elementong tumutugma sa ibinigay na code ng AI, o `null`. |
| `contains(String aiCode)` | `true` kung naroroon ang isang AI na may code na iyon. |
| `size()` | Bilang ng mga natukoy na AI. |
| `isValid()` | `true` kung walang error sa antas ng object at walang elementong may error. |
| `toHriString()` | String na HRI, halimbawa `(01)09506000134352 (17)261231`. |
| `toElementString()` | Hilaw na element string — walang panaklong, may FNC1 pagkatapos ng bawat elementong variable ang haba — halimbawa `010950600013435210LOT-ABC<GS>17271231`. Nagbabalik ng `null` kung `false` ang `isValid()`. |
| `getContentType()` | `GS1_DIGITAL_LINK` kapag totoo ang `hasDigitalLink()`, kung hindi ay `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` kung ang input ay isang GS1 Digital Link URI na may dalang pangunahing susi sa pagkilala. Ang wastong URL na walang pangunahing susi ay may `getDigitalLinkInfo()` pa rin ngunit `false` ang ibinabalik dito. |
| `getCanonicalDigitalLink()` | Ang kanonikong GS1 Digital Link URI (§4.12) sa `https://id.gs1.org` — pangunahing susi at mga qualifier bilang segment ng path, mga data attribute bilang query parameter na isinaayos ayon sa susi ng AI — o `null` kung walang pangunahing susi. |
| `getDigitalLinkInfo()` | Metadata ng paghahati ng URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), o `null` kung hindi ito Digital Link. |
| `getAllErrors()` | Antas ng object + lahat ng error ng elemento (hindi WARNING). |
| `getAllWarnings()` | Antas ng object + lahat ng babala ng elemento. |
| `getAllIssues()` | Lahat, pinagsama. |

---

### GS1AIObjectElement

Isang natukoy na instance ng AI.

| Method | Paglalarawan |
|---|---|
| `getAi()` | Code ng AI, halimbawa `"01"`, `"3102"`. |
| `getTitle()` | Pamagat ng datos ng GS1, halimbawa `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Ang buong paglalarawan ng GS1 sa AI, **naisalokal sa wika ng pag-parse** (halimbawa, `"Global Trade Item Number (GTIN)"` sa Ingles). Babalik sa tekstong Ingles mula sa depinisyon ng AI kung hindi pa naisasalin. |
| `getFormatString()` | Tagalarawan ng format na sumasaklaw sa AI *at* sa datos nito, halimbawa `"N2+N14"` para sa AI `01`, `"N2+X..20"` para sa AI `10`, `"N4+N3+N..15"` para sa AI `3932`. |
| `getValue()` | Ang hilaw na halaga ng datos na nakuha mula sa element string. |
| `isFixedLength()` | `true` kung nakapirmi ang haba ng datos ng AI. |
| `getPosition()` | Ang offset ng karakter na nagsisimula sa zero sa orihinal na input. |
| `getGS1ComponentValues()` | Mga hati ng halaga ayon sa bahagi (para sa mga AI na maraming bahagi). |
| `getErrors()` | Mga error sa antas ng elemento na hindi WARNING. |
| `getWarnings()` | Mga pahiwatig na WARNING sa antas ng elemento. |
| `getIssues()` | Pinagsamang mga error at babala sa antas ng elemento. |
| `hasErrors()` | `true` kung may nakakabit na anumang error na hindi WARNING. |
| `hasWarnings()` | `true` kung may nakakabit na anumang pahiwatig na WARNING. |
| `getInterpretations()` | Mga entry na `GS1AIInterpretation` (napupuno sa modong INTERPRETATION). |
| `getInterpretation(String type)` | Ang unang interpretasyong tumutugma sa ibinigay na susi ng uri sa `GS1Constants_Enricher`, o `null`. |
| `getDigitalLinkAIType()` | Ang papel ng elemento sa Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), o `null` para sa input na element string. |
| `hasDigitalLinkAIType()` | `true` kung may naitalagang papel sa Digital Link. |

---

### GaiaError

Isang di-nababagong error sa pagpapatunay o pahiwatig.

| Method | Paglalarawan |
|---|---|
| `getId()` | Tagapagkilala sa katalogo, halimbawa `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, o `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, o `INTERNAL`. |
| `getCode()` | Maikling code na mababasa ng makina. |
| `getAi()` | Ang code ng AI na nagdulot ng error, o `null` para sa mga error sa antas ng object. |
| `getMessage()` | Mensaheng mababasa ng tao na may nakapasok nang mga halaga. |
| `getPosition()` | Ang offset ng karakter na nagsisimula sa zero sa orihinal na input. |

---

### GS1AIInterpretation

Isang may-label na bahagi ng interpretasyon, na ikinakabit sa isang `GS1AIObjectElement` sa modong `INTERPRETATION`.

| Method | Paglalarawan |
|---|---|
| `getType()` | Susi ng uri na mababasa ng makina, halimbawa `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Hindi nagbabago sa lahat ng wika. |
| `getLabel()` | Label na mababasa ng tao, **naisalokal sa wika ng pag-parse** (halimbawa, `"Date"` / `"GS1 company prefix"` sa Ingles). |
| `getValue()` | Ang nakuha o pinayamang halaga, halimbawa `"31/12/2026"`, `"9506000"`. Hindi isinasalokal. |

---

### DataCarrierEntry at DataCarrierType

Kapag may dalang AIM Code ID ang input, nagbabalik ang `ParseResult.getDataCarrier()` ng isang `DataCarrierEntry` na naglalarawan sa simbolong nagdala ng datos. Ang entry ay ang tiyak na tala sa rehistro para sa tumugmang AIM Code ID; ang `DataCarrierType` naman ang enum sa oras ng compile na kinabibilangan nito.

#### DataCarrierEntry

Ang metadata ng isang nakilalang AIM Code ID (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Method | Paglalarawan |
|---|---|
| `getAimCodeId()` | Ang AIM Code ID na tumugma, halimbawa `"]C1"`. |
| `getName()` | Ang pangalang mababasa ng tao ng tiyak na simbolo, halimbawa `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Mas mahabang paglalarawan ng carrier. |
| `getType()` | Ang uring pambalangkas ng carrier bilang string (katumbas ng `getDataCarrierType().getCategory()`). |
| `getStandard()` | Ang pamantayan ng symbology, kung naitala. |
| `getDataCarrierType()` | Ang may-uring `DataCarrierType` para sa entry na ito — ito ang gamitin sa pagruruta sa code. |
| `isGs1Capable()` | `true` kung kayang magdala ng datos ng GS1 ang carrier (mga element string ng AI at/o Digital Link). |
| `isGs1AICapable()` | `true` kung kayang magdala ng mga element string ng GS1 AI ang carrier. |
| `isGs1DigitalLinkCapable()` | `true` kung kayang magdala ng GS1 Digital Link URI ang carrier. |
| `isEciCapable()` | `true` kung sinusuportahan ng carrier ang tagapagpahiwatig ng ECI. |
| `isRequiresGtinPadding()` | `true` para sa mga carrier na EAN/UPC/ITF na pinupunan ang numerikong halaga hanggang GTIN-14 bago ang pag-parse ng AI. |

#### DataCarrierType

Isang enum sa oras ng compile ng mga uri ng data carrier, na nakasusi sa AIM Code ID na itinalaga sa ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Ang karakter matapos ang `]` (ang *karakter ng code*) ang pumipili ng pamilya; karamihan sa mga pamilya ay tumutugma sa isang konstant na sumasaklaw sa bawat modifier (saklaw ng `ITF` ang `]I0`–`]I2`; saklaw ng `EAN_UPC` ang EAN-13, UPC-A, UPC-E at EAN-8). Kung saan may inilaang modifier ang GS1 para sa datos ng AI, ang bariyanteng iyon ay may sariling konstant — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — na kaiba sa payak nilang katapat. Kapag walang AIM Code ID, o kapag pumapangalan ito sa di-kilalang carrier, `UNKNOWN` ang uri.

| Method | Paglalarawan |
|---|---|
| `getCategory()` | Ang malawak na `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, o `OTHER`. |
| `getCodeChar()` | Ang karakter ng AIM code na nagsasabi ng pamilya, halimbawa `"Q"` para sa QR Code; `null` para sa `UNKNOWN`. |
| `getDisplayName()` | Ang pangalang mababasa ng tao ng *uri* (maaaring mas malawak kaysa sa `DataCarrierEntry.getName()` — halimbawa, `"EAN-13 / UPC-A / UPC-E / EAN-8"` laban sa `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` para sa mga konstant na laging nangangahulugang datos ng GS1 AI: ang apat na bariyanteng inilaan ng GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) kasama ang `GS1_DATABAR`, na likas na GS1 dahil bawat modifier na `]e` ay GS1 DataBar. Mas makitid ito kaysa sa `DataCarrierEntry.isGs1AICapable()` — kayang magdala ng datos ng GS1 AI ng isang payak na `QR_CODE`. |
| `static forAimCodeId(String)` | Tuwirang nilulutas ang uri mula sa isang AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); nagbabalik ng `UNKNOWN` para sa walang laman, maling anyo, o di-kilalang ID. |

Ang pagruruta ayon sa uri sa halip na ayon sa pangalan — halimbawa, sa paghihiwalay ng linear (Code-128) sa 2D (QR / Data Matrix) na mga simbolo:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

Saklaw lamang ng `TWO_D` ang mga simbolong matrix at dot; ang mga carrier na stacked-linear (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) ay `STACKED_LINEAR`, kahit karaniwan silang tinatawag
na mga barcode na "2D". Upang ituring silang iisang pangkat — halimbawa, sa pagpapasya kung
imager sa halip na laser scanner ang kailangan — suriin ang alinman sa dalawang kategorya.

> Kailangang naroroon ang AIM Code ID sa scan upang malutas ang uri; kung wala ito, `null` ang `getDataCarrier()` at `UNKNOWN` ang uri. Itakda ang scanner na magpadala ng prefix na AIM Code ID.

---

## Sanggunian sa Error

| Code | Antas | Yugto | Kahulugan |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Di-kilalang prefix ng AI — hindi matukoy ang haba ng datos |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Masyadong maikli ang input para makabasa ng buong code ng AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Naputol na halaga — mas kaunti ang karakter kaysa sa kailangan ng AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Nadobleng Application Identifier sa element string |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Nawawala ang kinakailangang dependency ng AI |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Ipinagbabawal na pares ng AI — dalawang AI na hindi maaaring magkasama |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Di-inaasahang pagkabigo sa pagti-token |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Karakter na wala sa naiene-encode na set ng GS1 sa element string |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Nawawala ang kinakailangang separator na FNC1 matapos ang isang AI na variable ang haba |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | May natirang datos lampas sa pinakamataas na hangganan ng lahat ng bahagi |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separator na FNC1 matapos ang isang AI na nakapirmi ang haba sa gitna ng string |
| `GE-W002` | WARNING | SYNTAX | Natirang FNC1 sa dulo ng element string (pahiwatig lamang) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Mga paglabag sa balangkas ng Digital Link URI — isang code sa bawat kondisyon (maling anyo ng URI, scheme, host, pagkakasunod ng qualifier, ipinagbabawal na AI, walang pangunahing susi (`GE-L013`), maraming pangunahing susi (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Hindi pumapasa ang halaga sa pattern ng regex ng AI |
| `GE-C003` | DATA_ERROR | CONTENT | Nabigo ang pagpapatunay ng check digit |
| `GE-C004` | DATA_ERROR | CONTENT | Nabigo ang pagpapatunay ng pares ng check character |
| `GE-C005` | FORMAT_ERROR | CONTENT | May karakter ang halaga ng bahagi na wala sa pinapayagang set ng karakter |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Mga pagkabigo sa format ng bahagi — isang code sa bawat kondisyon ng tagapagpatunay (tingnan ang `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Mga pagkabigo sa pasadyang pagpapatunay ng kahulugan — isang code sa bawat kondisyon ng tagapagpatunay (tingnan ang `content/validator/`). **Mga pagbubukod:** ang 14 na pagsusuri sa prefix ng kumpanya ng GS1 na nakalista sa ibaba ay may antas na `WARNING`, at ang `GE-C168` (di-kilalang numerikong code ng bansa ayon sa ISO 3166-1) ay may antas na `FORMAT_ERROR`. |
| Mga pagsusuri sa prefix ng kumpanya ng GS1 | WARNING | CONTENT | Hindi nagsisimula ang susi sa isang kilalang prefix ng kumpanya ng GS1, sa mga AI na susi ng GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Pahiwatig lamang — hindi nakaaapekto sa pagiging wasto. |
| `GE-C169` | DATA_ERROR | CONTENT | Nabigo ang check digit ng IMEI (Luhn) sa AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Nabigo ang check digit ng EID (Luhn) sa AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Di-kilalang AIM Code ID |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Natukoy ang carrier ngunit hindi nito sinusuportahan ang mga element string ng GS1 AI ni ang mga Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Di-inaasahang panloob na error |

> **Kilalang depekto sa paglalabas ng mensahe.** Isinasalungguhit ng mga template sa katalogo
> ang mga ipinapasok na halaga gamit ang dinobleng kudlit na istilong MessageFormat
> (`''{value}''`), ngunit `String.replace` lamang ang ginagamit ng `ErrorRegistry` sa
> pagpasok, kaya nananatili ang pagdodoble hanggang sa `getMessage()` — makikita mo sa
> ngayon ang `value ''09506000134351''` kung saan `value '09506000134351'` ang ipinapakita
> ng mga tekstong nakasipi sa gabay na ito. Naaapektuhan nito ang bawat mensaheng
> sumisipi ng halaga sa lahat ng 35 katalogo ng wika. Huwag i-parse ang mga mensahe ng
> error; magtugma sa `getId()` / `getCode()`.

---

## Kaligtasan sa Thread

Ligtas sa thread ang `GaiaParser` kapag nabuo na. Maaaring pagsaluhan at gamitin nang sabay-sabay ang iisang instance sa maraming thread. Ang inirerekomendang paraan ay bumuo ng isang instance sa pagsisimula ng aplikasyon at gamitin itong muli:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

Di-nababago ang `ParseConfig` at kasing-ligtas ding pagsaluhan. Ang tanging tungkulin sa kaligtasan sa thread na hindi kayang ipatupad ng library para sa iyo ay nasa [mga input modifier](#mga-input-modifier): iisang instance ng bawat modifier ang iniimbak at pinagsasaluhan ng lahat ng sabay-sabay na pag-parse, kaya dapat walang kalagayan ang mga pagpapatupad.

---

## Apendiks A — Mga Konstant na String ng AI

Nagdedeklara ang `GS1Constants_AICodes` (sa pakete na `tools.pantheum.gaia.gs1.constants`) ng isang konstant na `String` para sa bawat Application Identifier na nakikilala ng GAIA. Gamitin ang mga konstant na ito sa halip na isulat na sa code ang hilaw na string ng code ng AI:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Ang bawat konstant ay may hawak na anyong string ng code ng AI (halimbawa, `AI_01_GTIN = "01"`).

### Pagkilala at serialisasyon

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Serial na Code ng Shipping Container (SSCC). |
| `01` | `AI_01_GTIN` | Pandaigdigang Numero ng Item sa Kalakalan (GTIN). |
| `02` | `AI_02_CONTENT` | Global Trade Item Number (GTIN) ng mga nilalamang item sa kalakalan. |
| `03` | `AI_03_MTO_GTIN` | Identipikasyon ng Made-to-Order (MtO) na item sa kalakalan (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Numero ng batch o lote. |
| `20` | `AI_20_VARIANT` | Panloob na variant ng produkto. |
| `21` | `AI_21_SERIAL` | Serial number. |
| `22` | `AI_22_CPV` | Variant ng produkto para sa konsyumer. |
| `235` | `AI_235_TPX` | Third Party Controlled, Serialized Extension ng Global Trade Item Number (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Karagdagang identipikasyon ng produkto na itinalaga ng gumawa. |
| `241` | `AI_241_CUST_PART_NO` | Numero ng parte ng kostumer. |
| `242` | `AI_242_MTO_VARIANT` | Numero ng variation para sa Made-to-Order. |
| `243` | `AI_243_PCN` | Numero ng bahagi ng packaging. |
| `250` | `AI_250_SECONDARY_SERIAL` | Pangalawang serial number. |
| `251` | `AI_251_REF_TO_SOURCE` | Sanggunian sa entity na pinagmulan. |
| `253` | `AI_253_GDTI` | Pandaigdigang Identipikasyon ng Uri ng Dokumento (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Extension component ng Global Location Number (GLN). |
| `255` | `AI_255_GCN` | Pandaigdigang Numero ng Kupon (GCN). |
| `30` | `AI_30_VAR_COUNT` | Variable na bilang ng mga item (item sa kalakalang variable ang sukat). |
| `37` | `AI_37_COUNT` | Bilang ng mga item sa kalakalan o piyesa nito na nilalaman ng logistic unit. |

### Mga petsa at oras

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Petsa ng produksyon (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Petsa ng due date (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Petsa ng pagpapaketa (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Petsa ng best before (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Petsa ng sell by (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Petsa ng pag-expire (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Petsa at oras na hindi mauuna para sa delivery (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Petsa at oras na hindi lalampas para sa delivery (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Petsa ng paglabas (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Petsa at oras ng pag-expire (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Unang petsa ng pagyeyelo (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Petsa ng ani (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Petsa ng test by (YYMMDD[hhmm]). |

### Dami at sukat — variable na sukat (metriko)

Ini-encode ng mga 4-digit na pamilya ng AI na `310n`–`369n` ang mga daming variable ang sukat. Ang ikatlong digit ang pumipili ng uri ng sukat; ang **ikaapat na digit** (`n`, 0–5) ang bilang ng ipinahihiwatig na decimal place — halimbawa, ang `AI_3102_NET_WEIGHT_KG` ay netong timbang sa kilogramo na may 2 decimal place.

| Pamilya | Pattern ng konstant (`n` = digit ng decimal place) | Paglalarawan |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Netong timbang, kilogramo (item sa kalakalang variable ang sukat). |
| `311n` | `AI_311n_LENGTH_M` | Haba o unang sukat, metro (item sa kalakalang variable ang sukat). |
| `312n` | `AI_312n_WIDTH_M` | Lapad, diameter, o ikalawang sukat, metro (item sa kalakalang variable ang sukat). |
| `313n` | `AI_313n_HEIGHT_M` | Lalim, kapal, taas, o ikatlong sukat, metro (item sa kalakalang variable ang sukat). |
| `314n` | `AI_314n_AREA_M` | Sukat na lawak, metro kuwadrado (item sa kalakalang variable ang sukat). |
| `315n` | `AI_315n_NET_VOLUME_L` | Netong dami, litro (item sa kalakalang variable ang sukat). |
| `316n` | `AI_316n_NET_VOLUME_M` | Netong dami, metro kubiko (item sa kalakalang variable ang sukat). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Timbang sa logistics, kilogramo. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Haba o unang sukat, metro. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Lapad, diameter, o ikalawang sukat, metro. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Lalim, kapal, taas, o ikatlong sukat, metro. |
| `334n` | `AI_334n_AREA_M_LOG` | Sukat na lawak, metro kuwadrado. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Dami sa logistics, litro. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Dami sa logistics, metro kubiko. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramo kada metro kuwadrado. |

### Dami at sukat — variable na sukat (imperial / US)

| Pamilya | Pattern ng konstant (`n` = digit ng decimal place) | Paglalarawan |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Netong timbang, libra (pounds) (item sa kalakalang variable ang sukat). |
| `321n` | `AI_321n_LENGTH_IN` | Haba o unang sukat, inches (item sa kalakalang variable ang sukat). |
| `322n` | `AI_322n_LENGTH_FT` | Haba o unang sukat, feet (item sa kalakalang variable ang sukat). |
| `323n` | `AI_323n_LENGTH_YD` | Haba o unang sukat, yards (item sa kalakalang variable ang sukat). |
| `324n` | `AI_324n_WIDTH_IN` | Lapad, diameter, o ikalawang sukat, inches (item sa kalakalang variable ang sukat). |
| `325n` | `AI_325n_WIDTH_FT` | Lapad, diameter, o ikalawang sukat, feet (item sa kalakalang variable ang sukat). |
| `326n` | `AI_326n_WIDTH_YD` | Lapad, diameter, o ikalawang sukat, yards (item sa kalakalang variable ang sukat). |
| `327n` | `AI_327n_HEIGHT_IN` | Lalim, kapal, taas, o ikatlong sukat, inches (item sa kalakalang variable ang sukat). |
| `328n` | `AI_328n_HEIGHT_FT` | Lalim, kapal, taas, o ikatlong sukat, feet (item sa kalakalang variable ang sukat). |
| `329n` | `AI_329n_HEIGHT_YD` | Lalim, kapal, taas, o ikatlong sukat, yards (item sa kalakalang variable ang sukat). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Timbang sa logistics, libra (pounds). |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Haba o unang sukat, inches. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Haba o unang sukat, feet. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Haba o unang sukat, yards. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Lapad, diameter, o ikalawang sukat, inches. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Lapad, diameter, o ikalawang sukat, feet. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Lapad, diameter, o ikalawang sukat, yarda. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Lalim, kapal, taas, o ikatlong sukat, inches. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Lalim, kapal, taas, o ikatlong sukat, feet. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Lalim, kapal, taas, o ikatlong sukat, yards. |
| `350n` | `AI_350n_AREA_IN` | Sukat na lawak, pulgadang parisukat (item sa kalakalang variable ang sukat). |
| `351n` | `AI_351n_AREA_FT` | Sukat na lawak, talampakang parisukat (item sa kalakalang variable ang sukat). |
| `352n` | `AI_352n_AREA_YD` | Sukat na lawak, yardang parisukat (item sa kalakalang variable ang sukat). |
| `353n` | `AI_353n_AREA_IN_LOG` | Sukat na lawak, pulgadang parisukat. |
| `354n` | `AI_354n_AREA_FT_LOG` | Sukat na lawak, talampakang parisukat. |
| `355n` | `AI_355n_AREA_YD_LOG` | Sukat na lawak, yardang parisukat. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Netong timbang, onsang troy (item sa kalakalang variable ang sukat). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Netong timbang (o dami), onsa (item sa kalakalang variable ang sukat). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Netong dami, quart (item sa kalakalang variable ang sukat). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Netong dami, galonyang Amerikano (U.S.) (item sa kalakalang variable ang sukat). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Dami sa logistics, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Dami sa logistics, galonyang Amerikano (U.S.). |
| `364n` | `AI_364n_NET_VOLUME_IN` | Netong dami, pulgadang kubiko (item sa kalakalang variable ang sukat). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Netong dami, talampakang kubiko (item sa kalakalang variable ang sukat). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Netong dami, yardang kubiko (item sa kalakalang variable ang sukat). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Dami sa logistics, pulgadang kubiko. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Dami sa logistics, talampakang kubiko. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Dami sa logistics, yardang kubiko. |

### Presyo at halagang pera

Ini-encode ng ikaapat na digit (`n`) ang bilang ng ipinahihiwatig na decimal place. Magkaiba ang
pinapayagang saklaw nito sa bawat pamilya — tingnan ang haliging `n`.

| Pamilya | Pattern ng konstant (`n` = digit ng decimal place) | `n` | Paglalarawan |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Naaangkop na halagang babayaran o halaga ng kupon, lokal na currency. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Naaangkop na halagang babayaran gamit ang ISO currency code. |
| `392n` | `AI_392n_PRICE` | 0–9 | Naaangkop na halagang babayaran, iisang monetary area (item sa kalakalang variable ang sukat). |
| `393n` | `AI_393n_PRICE` | 0–9 | Naaangkop na halagang babayaran gamit ang ISO currency code (item sa kalakalang variable ang sukat). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Porsyentong diskwento ng kupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Halagang babayaran kada yunit ng sukat, iisang monetary area (item sa kalakalang variable ang sukat). |

### Lokasyon at pagpapadala

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Numero ng purchase order ng kostumer. |
| `401` | `AI_401_GINC` | Pandaigdigang Numero ng Identipikasyon para sa Consignment (GINC). |
| `402` | `AI_402_GSIN` | Pandaigdigang Numero ng Identipikasyon ng Padala (GSIN). |
| `403` | `AI_403_ROUTE` | Routing code. |
| `410` | `AI_410_SHIP_TO_LOC` | Padadalhan / Hahatiran – Global Location Number (GLN). |
| `411` | `AI_411_BILL_TO` | Global Location Number (GLN) ng bibilingan / pagpapadalhan ng invoice. |
| `412` | `AI_412_PURCHASE_FROM` | Global Location Number (GLN) kung saan binili. |
| `413` | `AI_413_SHIP_FOR_LOC` | Ipadala para kay / Ihatid para kay - Iforward sa Global Location Number (GLN). |
| `414` | `AI_414_LOC_NO` | Identipikasyon ng pisikal na lokasyon - Global Location Number (GLN). |
| `415` | `AI_415_PAY_TO` | Global Location Number (GLN) ng partidong nag-invoice. |
| `416` | `AI_416_PROD_SERV_LOC` | Global Location Number (GLN) ng lokasyon ng produksyon o serbisyo. |
| `417` | `AI_417_PARTY` | Global Location Number (GLN) ng partido. |
| `420` | `AI_420_SHIP_TO_POST` | Padadalhan / Hahatiran – postal code sa loob ng iisang postal authority. |
| `421` | `AI_421_SHIP_TO_POST` | Padadalhan / Hahatiran – postal code kasama ang ISO country code. |
| `422` | `AI_422_ORIGIN` | Bansang pinagmulan ng item sa kalakalan. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Bansa ng paunang pagproseso. |
| `424` | `AI_424_COUNTRY_PROCESS` | Bansa ng pagproseso. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Bansa ng pag-disassemble. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Bansang sumasaklaw sa buong proseso ng chain. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivisyon ng bansang pinagmulan. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Padadalhan / Hahatiran – pangalan ng kumpanya. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Padadalhan / Hahatiran – contact person. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Padadalhan / Hahatiran – linya 1 ng address. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Padadalhan / Hahatiran – linya 2 ng address. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Padadalhan / Hahatiran – suburb. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Padadalhan / Hahatiran – lokalidad. |
| `4306` | `AI_4306_SHIP_TO_REG` | Padadalhan / Hahatiran – rehiyon. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Padadalhan / Hahatiran – country code. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Padadalhan / Hahatiran – numero ng telepono. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Padadalhan / Hahatiran – GEO location. |
| `4310` | `AI_4310_RTN_TO_COMP` | Pagbabalik sa – pangalan ng kumpanya. |
| `4311` | `AI_4311_RTN_TO_NAME` | Pagbabalik sa – contact person. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Pagbabalik sa – linya 1 ng address. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Pagbabalik sa – linya 2 ng address. |
| `4314` | `AI_4314_RTN_TO_SUB` | Pagbabalik sa – suburb. |
| `4315` | `AI_4315_RTN_TO_LOC` | Pagbabalik sa – lokalidad. |
| `4316` | `AI_4316_RTN_TO_REG` | Pagbabalik sa – rehiyon. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Pagbabalik sa – country code. |
| `4318` | `AI_4318_RTN_TO_POST` | Pagbabalik sa – postal code. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Pagbabalik sa – numero ng telepono. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Deskripsyon ng service code. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Flag ng mapanganib na kalakal. |
| `4322` | `AI_4322_AUTH_LEAVE` | Pahintulot na iwan ang delivery (Authority to Leave). |
| `4323` | `AI_4323_SIG_REQUIRED` | Flag na kailangan ng lagda. |
| `4330` | `AI_4330_MAX_TEMP_F` | Pinakamataas na temperatura sa Fahrenheit (nasa ikasandaang bahagi ng degree). |
| `4331` | `AI_4331_MAX_TEMP_C` | Pinakamataas na temperatura sa Celsius (nasa ikasandaang bahagi ng degree). |
| `4332` | `AI_4332_MIN_TEMP_F` | Pinakamababang temperatura sa Fahrenheit (nasa ikasandaang bahagi ng degree). |
| `4333` | `AI_4333_MIN_TEMP_C` | Pinakamababang temperatura sa Celsius (nasa ikasandaang bahagi ng degree). |

### Mga katangian ng produkto at kakayahang masubaybayan

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO Stock Number (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Klasipikasyon ng UN/ECE para sa carcasses at hiwa ng karne. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktibong potency. |
| `7005` | `AI_7005_CATCH_AREA` | Lugar ng panghuhuli (catch area). |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Species para sa layuning pangisdaan. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Uri ng kagamitan sa pangingisda. |
| `7010` | `AI_7010_PROD_METHOD` | Paraan ng produksyon. |
| `7020` | `AI_7020_REFURB_LOT` | Lot ID ng refurbishment. |
| `7021` | `AI_7021_FUNC_STAT` | Katayuan sa paggana (functional status). |
| `7022` | `AI_7022_REV_STAT` | Katayuan ng rebisyon. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Pandaigdigang Identipikasyon ng Indibidwal na Ari-arian (GIAI) ng isang assembly. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Numero ng nagproseso na may tatlong-digit na code ng bansa ayon sa ISO (10 puwesto). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC na may Extension 1 at Importer index. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Uri ng freight unit ng UN/CEFACT. |

### Mga Numero ng Pambansang Reimbursement sa Healthcare (NHRN)

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Germany PZN. |
| `711` | `AI_711_NHRN_CIP` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - France CIP. |
| `712` | `AI_712_NHRN_CN` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Spain CN. |
| `713` | `AI_713_NHRN_DRN` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - United States of America NDC. |
| `716` | `AI_716_NHRN_AIC` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Italy AIC. |
| `717` | `AI_717_NHRN_SRN` | Numero ng Pambansang Reimbursement sa Healthcare (NHRN) - Numero ng Sanitary Register ng Costa Rica. |

### Healthcare, GMN, HIDRI, CPID, datos ng tao

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Sanggunian ng Sertipikasyon (10 puwesto). |
| `7240` | `AI_7240_PROTOCOL` | Protocol ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Uri ng AIDC media. |
| `7242` | `AI_7242_VCN` | Version Control Number (VCN). |
| `7250` | `AI_7250_DOB` | Petsa ng kapanganakan (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Petsa at oras ng kapanganakan (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Kasarian (biological sex). |
| `7253` | `AI_7253_FAMILY_NAME` | Apelyido ng tao. |
| `7254` | `AI_7254_GIVEN_NAME` | Unang pangalan ng tao. |
| `7255` | `AI_7255_SUFFIX` | Suffix ng pangalan ng tao. |
| `7256` | `AI_7256_FULL_NAME` | Buong pangalan ng tao. |
| `7257` | `AI_7257_PERSON_ADDR` | Address ng tao. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Pagkakasunod-sunod ng kapanganakan ng sanggol. |
| `7259` | `AI_7259_BABY` | Apelyido ng sanggol. |
| `8001` | `AI_8001_DIMENSIONS` | Mga produktong roll (lapad, haba, diameter ng core, direksyon, splices). |
| `8002` | `AI_8002_CMT_NO` | Identipikasyon ng cellular mobile telephone. |
| `8003` | `AI_8003_GRAI` | Pandaigdigang Identipikasyon ng Maisasauling Ari-arian (GRAI). |
| `8004` | `AI_8004_GIAI` | Pandaigdigang Identipikasyon ng Indibidwal na Ari-arian (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Presyo kada yunit ng sukat. |
| `8006` | `AI_8006_ITIP` | Identipikasyon ng indibidwal na piyesa ng item sa kalakalan (ITIP). |
| `8007` | `AI_8007_IBAN` | International Bank Account Number (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Petsa at oras ng produksyon (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optically Readable Sensor Indicator (senyales na nababasa nang optiko). |
| `8010` | `AI_8010_CPID` | Identipikasyon ng Bahagi/Piyesa (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Serial number ng Identipikasyon ng Bahagi/Piyesa (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Bersyon ng software. |
| `8013` | `AI_8013_GMN` | Pandaigdigang Numero ng Modelo (GMN). |
| `8014` | `AI_8014_MUDI` | Lubos na Indibidwalisadong Identipikasyon sa Pagpaparehistro ng Device (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Pandaigdigang Numero ng Relasyon sa Serbisyo (GSRN) para tukuyin ang relasyon sa pagitan ng organisasyong nag-aalok ng serbisyo at ng nagbibigay ng serbisyo. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Pandaigdigang Numero ng Relasyon sa Serbisyo (GSRN) para tukuyin ang relasyon sa pagitan ng organisasyong nag-aalok ng serbisyo at ng tatanggap ng serbisyo. |
| `8019` | `AI_8019_SRIN` | Service Relation Instance Number (SRIN). |
| `8020` | `AI_8020_REF_NO` | Reference number ng payment slip. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identipikasyon ng mga piyesa ng item sa kalakalan (ITIP) na nilalaman ng logistic unit. |
| `8030` | `AI_8030_DIGSIG` | Digital na Lagda (DigSig). |
| `8040` | `AI_8040_IMEI` | International Mobile Equipment Identity (IMEI). |
| `8041` | `AI_8041_IMEI2` | International Mobile Equipment Identity 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Numero ng embedded SIM. |
| `8043` | `AI_8043_PSIM` | Numero ng pisikal na SIM. |
| `8110` | `AI_8110` | Identipikasyon ng coupon code para sa paggamit sa North America. |
| `8111` | `AI_8111_POINTS` | Loyalty points ng kupon. |
| `8112` | `AI_8112` | Identipikasyon ng coupon code sa positive offer file para sa paggamit sa North America. |
| `8200` | `AI_8200_PRODUCT_URL` | URL ng Extended Packaging. |

### Panloob / gamit ng kumpanya

| AI | Konstant | Paglalarawan |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Impormasyong napagkasunduan ng mga trading partner. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Panloob na impormasyon ng kumpanya (9 puwesto). |

---

## Apendiks B — Mga Konstant na Susi ng Interpretasyon

Kapag tinawag ang `GaiaParser.parse()` gamit ang `ParseMode.INTERPRETATION`, maaaring may dalang listahan ng mga object na `GS1AIInterpretation` ang bawat `GS1AIObjectElement`, na ginawa ng mga tagapagpayamang tiyak sa larangan. Gamitin ang mga konstant mula sa `GS1Constants_Enricher` (sa pakete na `tools.pantheum.gaia.gs1.constants`) bilang susi sa paghahanap ng tiyak na halaga ng interpretasyon:

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

**Hindi** konstant ang mga label na ipinapakita — nakatira sila sa mga naisalokal na katalogo sa ilalim ng `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, na nakasusi sa konstant ng uri. Ibinabalik ng `GS1AIInterpretation.getLabel()` ang label para sa wika ng pag-parse (tingnan ang [Mga naisalokal na mensahe at label](#mga-naisalokal-na-mensahe-at-label)), at babalik sa Ingles kapag walang ganoong susi ang isang katalogo. Ang haliging "Label na ipinapakita" sa ibaba ay naglilista ng tekstong Tagalog; hindi nagbabago ang mismong mga susi ng uri sa lahat ng wika, kaya magtugma sa susi at hindi kailanman sa label.

### Petsa at oras

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `DATE_VALUE` | Petsa | Mga AI ng petsa (11–17, 7003, 7006, 7011, atbp.) |
| `DATE_FORMAT` | Format ng petsa | Mga AI ng petsa |
| `TIME_VALUE` | Oras | Mga AI na may dalang oras (7003, 7011, 8008, atbp.) |
| `TIME_FORMAT` | Format ng oras | Mga AI na may dalang oras |
| `DATETIME_VALUE` | Petsa at oras | Mga AI ng petsa+oras |
| `DATETIME_FORMAT` | Format ng petsa at oras | Mga AI ng petsa+oras |

### Petsa ng ani

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Petsa ng pagsisimula ng ani | AI 7007 |
| `HARVEST_END_DATE` | Petsa ng pagtatapos ng ani | AI 7007 (opsyonal na dulo ng saklaw) |
| `HARVEST_DATE_RANGE` | Saklaw ng petsa ng ani | AI 7007 |

### Prefix ng Kumpanya ng GS1

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefix ng kumpanya ng GS1 | Mga AI ng GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Kodigo ng miyembro ng GS1 | Mga AI ng GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organisasyong miyembro ng GS1 | Mga AI ng GTIN / GLN / SSCC |

### GTIN

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Uri ng GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Antas ng packaging | AI 01 |
| `GTIN_CHECK_DIGIT` | Digit ng pagsusuri | AI 01, 02 |

### SSCC

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Digit ng ekstensiyon | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Sanggunian ng serye | AI 00 |
| `SSCC_CHECK_DIGIT` | Digit ng pagsusuri | AI 00 |

### Bansa (ISO 3166)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Kodigo ng bansa (numeriko) | Mga AI na iisang bansa (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Kodigo ng bansa (alpha-2) | Mga AI ng bansang Alpha-2 |
| `COUNTRY_NAME` | Pangalan ng bansa | Mga AI na iisang bansa |
| `COUNTRY_LIST` | Mga bansa | AI 423 — lahat ng pangalan na pinagdugtong, halimbawa `Australia, New Zealand` |

Kayang magdala ng hanggang limang bansa ng AI 423 (bansa ng unang pagproseso), kaya naglalabas ito
ng **isang may-numerong pares sa bawat bansa** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — na sinusundan ng iisang buod na
`COUNTRY_LIST`. Buuin ang mga susing ito mula sa mga konstant na
`COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` kasama ang indeks na nagsisimula sa 1,
o umikot na lamang sa `getInterpretations()`; **hindi** inilalabas para sa AI 423 ang mga
susing `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` na walang panlapi.

### Pera (ISO 4217)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Kodigo ng pera | Mga AI ng halaga na may pera (391n, 393n) |
| `CURRENCY_ALPHA` | Alphabetic currency code | Mga AI ng halaga na may pera |
| `CURRENCY_NAME` | Pangalan ng pera | Mga AI ng halaga na may pera |

### Temperatura

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatura | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Unit ng temperatura | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatura (naka-format) | AI 4330–4333 |

### Kasarian (ISO 5218)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `SEX_CODE` | Kodigo ng kasarian | AI 7252 |
| `SEX_DESCRIPTION` | Deskripsyon ng kasarian | AI 7252 |

### Mga espesye sa tubig (FAO)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Kodigo ng espesye | AI 7008 |
| `SPECIES_SCIENTIFIC` | Siyentipikong pangalan | AI 7008 |
| `SPECIES_ENGLISH` | Karaniwang pangalan | AI 7008 |
| `SPECIES_FAMILY` | Pamilya | AI 7008 |
| `SPECIES_ORDER` | Orden | AI 7008 |

### Numero ng Stock ng NATO (NSN)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `NSN_FSG` | Pangkat ng suplay | AI 7001 |
| `NSN_FSG_NAME` | Pangalan ng pangkat ng suplay | AI 7001 |
| `NSN_FSCG` | Klase ng supply | AI 7001 |
| `NSN_FSCG_NAME` | Pangalan ng klase ng suplay | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Kodigo ng bansa | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Bansa | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO kodigo ng bansa | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategorya ng NCS | AI 7001 |
| `NSN_NIIN` | Pambansang numero ng aytem | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Mga produktong rolyo

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Lapad ng roll (mm) | AI 8001 |
| `ROLL_LENGTH` | Haba ng roll (m) | AI 8001 |
| `CORE_DIAMETER` | Diyametro ng core (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Code ng direksyon ng winding | AI 8001 |
| `WINDING_DIRECTION` | Direksyon ng winding | AI 8001 |
| `SPLICES` | Mga splice | AI 8001 |

### IBAN

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Kodigo ng bansa | AI 8007 |
| `IBAN_COUNTRY_NAME` | Bansa | AI 8007 |
| `IBAN_CHECK_DIGITS` | Mga digit ng pagsusuri | AI 8007 |
| `IBAN_CHECK_VALID` | Tseke | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Numero ng serye | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Digit ng pagsusuri | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Ahensyang naglalaan | AI 8040, 8041 |

Nahahati ang 15 digit bilang `[ TAC (8) ][ serial (6) ][ check digit na Luhn (1) ]`, at ang
RBI ang unang 2 digit ng TAC — kaya prefix ng `IMEI_TAC` ang `IMEI_RBI`, hindi isang
hiwalay na saklaw. Ipinapakita ng `IMEI_FORMATTED` ang karaniwang pagpapangkat na pandisplay
ng GSMA na `AA-BBBBBB-CCCCCC-D` (halimbawa, `49-015420-323751-8`), na humahati sa TAC sa
hangganan ng RBI; hindi inilalabas ang lumang pagpapangkat na `6-2-6-1`, na humahati kung
saan dating nagsisimula ang itinigil nang Final Assembly Code.

Nilulutas ng `IMEI_RBI_NAME` ang RBI tungo sa pangalan ng katawang naglalaan sa pamamagitan ng
`ImeiRbiData`, at **idinaragdag ito nang huli at kapag lamang nakalista roon ang code**.
Sumasaklaw ang talaang iyon sa tatlong pangkat:

- **Kasalukuyang naglalaan** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, kasama ang `99`
  Global Hexadecimal Administrator at `98` (nakalaan).
- **Mga saklaw na pansubok** — `00` at `02`–`09`, na nagtatanda ng mga IMEI na pansubok sa
  halip na tunay na paglalaan. Itanong sa pamamagitan ng `ImeiRbiData.isTestCode(code)`.
- **Hindi na naglalaan** — mga makasaysayang katawan gaya ng `49` (BZT/BAPT, Alemanya), `44`
  (BABT, UK) o `91` (MSAI, India). Itanong sa pamamagitan ng `ImeiRbiData.isNoLongerAllocating(code)`.
  Karaniwan lamang ang mga kagamitang may dalang ganitong code at gamit pa rin sila; ang bagong
  paglalaan lamang ang tumigil, kaya impormasyong pang-ulat ito at hindi kailanman hudyat ng
  pagiging wasto.

Ang kawalan ng `IMEI_RBI_NAME` ay nangangahulugang "wala sa aming talaan ang RBI na ito",
**hindi** "di-wastong IMEI": tinipon ang talaan mula sa isang nailathalang listahan ng RBI at
hindi tuwiran mula sa GSMA, kaya maaari itong mahuli sa mga bagong itinalagang katawan. Huwag
maghinuha ng anumang kalalabasan ng pagpapatunay mula sa kawalan nito; hindi check character
ang RBI. Dapat ding kayanin ng code na umiikot sa listahan ng interpretasyon ang kawalan nito
sa halip na umasa sa puwesto.

### Mga tagapagkilala ng SIM (EID / ICCID)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategorya ng industriya | AI 8042 |
| `EID_BODY` | Katawan ng EID | AI 8042 |
| `EID_CHECK_DIGIT` | Digit ng pagsusuri | AI 8042 |
| `ICCID_BODY` | Katawan ng ICCID | AI 8043 |
| `ICCID_EXTENSION` | Ekstensiyon | AI 8043 |

May dalang unang **dalawang** digit ang `SIM_MII` (`89`), ang pares na itinatalaga ng ITU-T
E.118 sa telekomunikasyon. Ang ISO/IEC 7812 mismo ay naglalarawan sa MII bilang **unang digit
lamang**, kaya nilulutas ng `SIM_MII_NAME` ang kategorya mula sa unang `8` na iyon sa
pamamagitan ng `Iso7812Data` — na nagbubunga ng "Healthcare, telecommunications and other
future industry assignments". Para sa isang wastong EID, samakatwid, hindi ito nagbabago;
iniuulat ito para sa kakayahang subaybayan pabalik sa pamantayan, hindi bilang pantukoy ng
pagkakaiba. Tumatanggap ang `Iso7812Data.nameForCode(digit)` ng payak na digit, samantalang
tumatanggap ang `nameForIdentifier(prefix)` ng mas mahabang prefix at binabasa ang unang digit nito.

Ang `EidEnricher` (AI 8042) lamang ang naglalabas ng `SIM_MII_NAME`. Inilalabas ng
`IccidEnricher` (AI 8043) ang `SIM_MII` nang walang kategorya.

### Sanggunian ng sertipikasyon

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Serial number | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Code ng scheme ng sertipikasyon | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Scheme ng sertipikasyon | AI 7230–7239 |
| `CERT_REFERENCE` | Sanggunian ng sertipikasyon | AI 7230–7239 |

### GS1 UIC

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `UIC_CODE` | Kodigo ng UIC | AI 7040 |
| `UIC_EXTENSION_1` | Ekstensiyon 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Index ng importer | AI 7040 |

### Pagkakasunod ng kapanganakan ng sanggol

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posisyon ng kapanganakan | AI 7258 |
| `BIRTH_TOTAL` | Kabuuang kapanganakan | AI 7258 |
| `BIRTH_SEQUENCE` | Pagkakasunod ng kapanganakan | AI 7258 |

### Pandaigdigang Numero ng Modelo (GMN)

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Sanggunian ng modelo | AI 8013 |
| `GMN_CHECK_PAIR` | Pares ng pagsusuri | AI 8013 |

### HIDRI

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Sanggunian ng device | AI 8014 |
| `HIDRI_CHECK_PAIR` | Pares ng pagsusuri | AI 8014 |

### CPID

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Sanggunian ng bahagi at pyesa | AI 8010–8011 |

### Mga halagang desimal at panukat

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Desimal na halaga | Mga numerikong AI na may ipinahihiwatig na decimal place (31xx–36xx) |
| `DECIMAL_AMOUNT` | Halaga | Mga AI ng presyo (390n–395n) |
| `DECIMAL_PERCENTAGE` | Porsyento | AI 394n |
| `DECIMAL_PLACES` | Mga desimal na lugar | Kasabay ng `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Format ng porsyento | AI 394n |
| `ISO_UNIT_CODE` | Kodigo ng yunit ng ISO | Mga AI ng sukat |
| `ISO_UNIT_NAME` | Pangalan ng yunit ng ISO | Mga AI ng sukat |
| `MONETARY_AMOUNT` | Halagang pera | Mga AI ng presyo |
| `MONETARY_AMOUNT_DISPLAY` | Halagang pera (naka-format) | Mga AI ng presyo |

### Mga koordinadang heograpiko

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `LATITUDE` | Latitud | AI 4309 |
| `LONGITUDE` | Longhitud | AI 4309 |
| `GEO_COORDINATES` | Mga geo coordinate | AI 4309 |
| `LATITUDE_DMS` | Latitud (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longhitud (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Mga geo coordinate (DMS) | AI 4309 |

### Paraan ng produksyon

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Code ng paraan ng produksyon | AI 7010 |
| `PRODUCTION_METHOD` | Paraan ng produksyon | AI 7010 |

### Uri ng midya ng AIDC

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Kodigo ng uri ng AIDC media | AI 7241 |
| `MEDIA_TYPE_NAME` | Uri ng AIDC media | AI 7241 |

### Piraso mula sa kabuuan

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Numero ng piraso | AI 8006 |
| `PIECE_TOTAL` | Kabuuang piraso | AI 8006 |
| `PIECE_OF_TOTAL` | Piraso ng kabuuan | AI 8006 |

### Mga hati ng bahagi

Mga susing inilalabas ng mga deklaratibong hati ng bahagi sa `content/ai-content.json` sa halip
na ng isang tagapagpayaman na Java — inilalantad nila ang mga pinangalanang bahagi ng isang
pinagsamang halaga ng AI. Hindi tulad ng bawat ibang susi sa apendiks na ito, **wala silang
konstant sa `GS1Constants_Enricher`**: magtugma sa mismong string, o basahin ang uri mula sa
`GS1AIInterpretation.getType()`.

| Susi ng uri | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Digit ng pagsusuri | Mga AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Numero ng serye | Mga AI 253, 255, 8003 |
| `POSTAL_CODE` | Kodigo postal | AI 421 |
| `PROCESSOR_ID` | Identifier ng processor | Mga AI 7030–7039 |

Tandaan na ang `CHECK_DIGIT` dito ay ang pangkalahatang susi ng hati ng bahagi, na kaiba sa mga
susing tiyak sa tagapagpayaman na `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` at
`EID_CHECK_DIGIT` na nakalista sa itaas.

### Iba pa

| Konstant na susi | Label na ipinapakita | Ginagawa ng |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Halaga | Mga AI na boolean / bandila (4321–4323) |
| `DECODED_TEXT` | Na-decode na teksto | Mga AI na malayang teksto |
