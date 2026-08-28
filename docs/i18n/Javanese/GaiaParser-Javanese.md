# GAIA (GS1 Application Identifiers Analyser) — Pandhuan kanggo Pangembang

## Daftar Isi

1. [Gambaran Umum](#gambaran-umum)
2. [Bab GS1 lan General Specifications](#bab-gs1-lan-general-specifications)
3. [GS1 Application Identifier](#gs1-application-identifier)
4. [Wiwitan Cepet](#wiwitan-cepet)
5. [Pipeline Parsing](#pipeline-parsing)
   - [Tahap wiwitan — Input Modifier](#tahap-wiwitan--input-modifier)
   - [Tahap 0 — Correlation ID](#tahap-0--correlation-id)
   - [Tahap 1 — Ngarahake Input](#tahap-1--ngarahake-input)
   - [Tahap 2 — Sintaksis](#tahap-2--sintaksis)
   - [Tahap 3 — Isi](#tahap-3--isi)
   - [Tahap 4 — Interpretasi](#tahap-4--interpretasi)
6. [Konfigurasi Parsing (`ParseConfig`)](#konfigurasi-parsing-parseconfig)
   - [Pilihan](#pilihan)
   - [Pesen lan label sing dilokalake](#pesen-lan-label-sing-dilokalake)
   - [Pormating tanggal](#pormating-tanggal)
7. [Input Modifier](#input-modifier)
   - [Modifier bawaan](#modifier-bawaan)
   - [Nulis modifier](#nulis-modifier)
   - [Ndaftarake modifier](#ndaftarake-modifier)
   - [Ndeleng apa sing wis ditindakake modifier](#ndeleng-apa-sing-wis-ditindakake-modifier)
   - [Nangani kegagalan modifier](#nangani-kegagalan-modifier)
8. [Modhe Parsing](#modhe-parsing)
   - [Modhe DATA_CARRIER](#modhe-data_carrier)
   - [Modhe SYNTAX](#modhe-syntax)
   - [Modhe CONTENT](#modhe-content)
   - [Modhe INTERPRETATION (standar)](#modhe-interpretation-standar)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Nggarap Asil](#nggarap-asil)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry lan DataCarrierType](#datacarrierentry-lan-datacarriertype)
12. [Referensi Kesalahan](#referensi-kesalahan)
13. [Aman kanggo Thread](#aman-kanggo-thread)
14. [Lampiran A — Konstanta String AI](#lampiran-a--konstanta-string-ai)
    - [Identifikasi lan serialisasi](#identifikasi-lan-serialisasi)
    - [Tanggal lan wektu](#tanggal-lan-wektu)
    - [Cacah lan ukuran — ukuran variabel (metrik)](#cacah-lan-ukuran--ukuran-variabel-metrik)
    - [Cacah lan ukuran — ukuran variabel (imperial / US)](#cacah-lan-ukuran--ukuran-variabel-imperial--us)
    - [Rega lan jumlah moneter](#rega-lan-jumlah-moneter)
    - [Lokasi lan pangiriman](#lokasi-lan-pangiriman)
    - [Atribut produk lan katelusuran](#atribut-produk-lan-katelusuran)
    - [Nomer Penggantian Kesehatan Nasional (NHRN)](#nomer-penggantian-kesehatan-nasional-nhrn)
    - [Kesehatan, GMN, HIDRI, CPID, data pawongan](#kesehatan-gmn-hidri-cpid-data-pawongan)
    - [Panganggo internal / perusahaan](#panganggo-internal--perusahaan)
15. [Lampiran B — Konstanta Kunci Interpretasi](#lampiran-b--konstanta-kunci-interpretasi)
    - [Tanggal lan wektu](#tanggal-lan-wektu)
    - [Tanggal panen](#tanggal-panen)
    - [Prefiks Perusahaan GS1](#prefiks-perusahaan-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Negara (ISO 3166)](#negara-iso-3166)
    - [Mata uang (ISO 4217)](#mata-uang-iso-4217)
    - [Suhu](#suhu)
    - [Jinis kelamin (ISO 5218)](#jinis-kelamin-iso-5218)
    - [Spesies banyu (FAO)](#spesies-banyu-fao)
    - [Nomer Stok NATO (NSN)](#nomer-stok-nato-nsn)
    - [Produk gulungan](#produk-gulungan)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Panuduh SIM (EID / ICCID)](#panuduh-sim-eid--iccid)
    - [Acuan sertifikasi](#acuan-sertifikasi)
    - [GS1 UIC](#gs1-uic)
    - [Urutan lair bayi](#urutan-lair-bayi)
    - [Nomer Model Global (GMN)](#nomer-model-global-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Nilai desimal lan ukuran](#nilai-desimal-lan-ukuran)
    - [Koordinat geo](#koordinat-geo)
    - [Cara produksi](#cara-produksi)
    - [Tipe media AIDC](#tipe-media-aidc)
    - [Cuwilan saka gunggung](#cuwilan-saka-gunggung)
    - [Panyigaran komponen](#panyigaran-komponen)
    - [Liya-liyane](#liya-liyane)

---

## Gambaran Umum

`GaiaParser` iku lawang mlebu kanggo parsing string elemen GS1 Application Identifier (AI). Iki nampa output mentah saka scanner ing salah siji wujud ing ngisor iki, banjur ngasilake `ParseResult` sing kasusun, isine kabeh AI sing wis diweruhi, kesalahan validasi, lan (yen dijaluk) interpretasi sing bisa diwaca manungsa:

- String elemen AI lugu: `0109506000134352`
- String elemen kanthi prefiks AIM Code ID: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Salah siji saka kabeh mau, sing bisa didhisiki correlation ID cacah 8 angka: `12345678~0109506000134352`

**Kelas lawang mlebu:** `tools.pantheum.gaia.GaiaParser`

> **Lagi kenal Gaia?** Wiwitana saka **[Wiwitan Cepet GaiaParser](GaiaParser-QuickStart-Javanese.md)** — dependensi, parsing sepisanan, lan sawetara bab sing kerep nyandhung wong, kira-kira sepuluh menit. Pandhuan iki minangka referensi sing jangkep.

> Kanggo pakaryan sing kosok balen — *mbangun* string elemen lan Digital Link URI sing bener saka pasangan AI lan nilai — deleng **[GaiaBuilder — Pandhuan kanggo Pangembang](GaiaBuilder-Javanese.md)**.

---


## Bab GS1 lan General Specifications

**GS1** iku organisasi global nirlaba sing nyusun lan ngrumat standar terbuka kanggo identifikasi rantai pasokan lan pertukaran data. Standare dienggo ing ritel, kesehatan, logistik, layanan pangan, lan akeh industri liyane, nyakup kabeh wiwit barcode produk ing kemasan konsumen nganti pelacakan serial dosis obat.

Sumber resmi kanggo kabeh sing dileksanakake parser iki yaiku **GS1 General Specifications** — siji dokumen sing netepake:

- Kabeh kode Application Identifier (AI), judhul datane, formate, lan aturan validasine
- Aturan sintaksis kanggo mbangun lan ngode string elemen AI
- Syarat simbologi barcode lan wenehan AIM Code ID
- Algoritma angka centhang lan aksara centhang
- Rampungan taun rong angka (aturan jendhela geser)
- Spesifikasi Data Matrix, QR Code, GS1-128, GS1 DataBar, lan operator data liyane

GS1 General Specifications dianyarake saben taun. Edhisi saiki lan sumber panyengkuyunge kasedhiya ing:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA nglakokake **Release 26.0 (Disahake, Januari 2026)** saka GS1 General Specifications.

GS1 Digital Link URI diatur dening standar kanca yaiku **GS1 Digital Link: URI Syntax**, sing netepake kunci identifikasi utama, urutan key qualifier, lan pangodean data attribute sing dienggo parser kanggo input Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA nglakokake **Release 1.7.0 (Disahake, Agustus 2026)** saka standar GS1 Digital Link: URI Syntax.

Kabeh acuan bagean ing sajroning dokumen iki nuduhake GS1 General Specifications (tuladha, "Table 7-5", "section 7.12"), kajaba nomer bagean Digital Link (tuladha, "§4.9", "§4.12"), sing nuduhake standar GS1 Digital Link: URI Syntax.

---

## GS1 Application Identifier

**GS1 Application Identifier (AI)** iku prefiks angka sing cendhak — loro nganti papat angka — sing nuduhake teges lan format data sing langsung ngetutake. AI ditetepake ing GS1 General Specifications lan nyakup werna-werna data rantai pasokan: identitas produk, tanggal, cacah, nomer lot, nomer seri, ukuran, URL, lan liya-liyane.

### Susunan sawijining elemen AI

Saben elemen AI kasusun saka rong bagean:

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

Kode AI mesthi angka. Nilai data langsung ngetutake, tanpa ana delimiter ing antarane kode lan nilai.

### Dawa tetep lan dawa variabel

AI kepara dadi rong golongan:

| Tipe | Kelakuan | Tuladha |
|---|---|---|
| **Dawa tetep** | Cacah aksara sing pesthi, tansah diwaca kabeh | AI `01` (GTIN) — tansah 14 angka |
| **Dawa variabel** | Saka 1 nganti cacah aksara paling akeh; rampung ing pamisah GS utawa pungkasaning input | AI `10` (Bets/Lot) — 1 nganti 20 aksara alfanumerik |

Apa sawijining AI iku tetep utawa variabel, iku mung ditemtokake saka tetepane ing spesifikasi GS1 — parser ora tau nyoba nebak.

### String elemen kanthi AI akeh

AI pirang-pirang bisa disambung dadi siji string elemen. AI sing dawane tetep bisa disambung langsung amarga parser tansah ngerti pira aksara sing kudu diwaca. Nanging AI sing dawane variabel kudu dipungkasi nganggo **aksara GS** (ASCII `0x1D`, sing uga diarani FNC1 ing simbologi barcode) saben ana AI liya sing ngetutake, supaya parser ngerti ngendi nilai siji rampung lan ngendi kode AI sabanjure wiwit.

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

Ing literal string Java, tulisen aksara GS nganggo Unicode escape `""`.

### AI sing kerep dienggo

| AI | Judhul data | Format | Tuladha nilai |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, wilayah moneter tunggal) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Angka kapapat** saka AI ukuran utawa rega sing cacahe 4 angka iku ngode cacahing panggonan desimal sing dimaksud — `3103` iku bobot netto ing kilogram kanthi 3 desimal (`001500` = 1.500 kg), dene `3102` bakal maca angka sing padha dadi 15.00 kg. Kolom `Format` ing ndhuwur nuduhake format *data*; `getFormatString()` sing jangkep saka saben AI uga nyakup AI dhewe (tuladha, `N4+N6` kanggo `3103`).

### Interpretasi sing Bisa Diwaca Manungsa (HRI)

Wujud lumrah sing bisa diwaca manungsa iku mbungkus saben kode AI nganggo kurung persis sadurunge nilaine, kanthi spasi siji ing antaraning elemen:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Pamisah GS ora ditampilake ing HRI. `GS1AIObject.toHriString()` sing ngasilake format iki.

### Kode AI cacah papat angka

Ana sawetara AI sing nganggo papat angka, dudu loro. Rong angka ngarep nuduhake kulawarga AI; angka katelu lan/utawa kapapat nggawa teges tambahan (kayata panggonan titik desimal sing dimaksud kanggo AI ukuran). Parser ngrampungake kode AI sing jangkep saka string elemen kanthi otomatis — sing nyeluk tansah nganggo kode sing jangkep (tuladha, `"3102"`, dudu `"31"` thok).

---

## Wiwitan Cepet

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

> **Pamisah GS:** AI sing dawane variabel ing sajroning string kanthi AI akeh kudu dipisah nganggo aksara GS (ASCII `0x1D`). Nganggoa `""` ing literal string Java.

---

## Pipeline Parsing

### Tahap wiwitan — Input Modifier

Yen `ParseConfig` nggawa **input modifier**, kabeh mau mlaku sadurunge liyane — sadurunge nyopot correlation, sadurunge ndeteksi operator data, sadurunge mlebu pipeline GS1. Saben modifier nulis ulang input mentah kanggo modifier sabanjure, lan kabeh tahap ing ngisor iki nggarap output saka rantai kasebut.

Ora ana modifier sing disetel minangka standar, mula tahap wiwitan iki ora nglakoni apa-apa kajaba kowe dhewe milih. Deleng [Input Modifier](#input-modifier).

---

### Tahap 0 — Correlation ID

Sadurunge pangolahan GS1 apa wae, `GaiaParser` mriksa apa input wiwit kanthi **prefiks correlation ID** sing opsional: persis 8 angka desimal ASCII sing langsung diterusake tilde (`~`), tuladha `12345678~`.

Yen prefikse ana, iku dicopot lan disimpen minangka `CorrelationInfo` ing `ParseResult` sing dibalekake. Kabeh tahap sabanjure nggarap payload sing wis dicopot prefikse. Yen ora ana prefiks, input liwat tanpa owah-owahan.

Deleng [Correlation ID](#correlation-id) kanggo katrangan luwih jangkep.

---

### Tahap 1 — Ngarahake Input

Sawise correlation dicopot, `GaiaParser` mriksa apa input (sing wis dicopot) wiwit kanthi **AIM Code ID**: prefiks telung aksara kanthi wujud `]` + aksara ASCII + angka ASCII (tuladha, `]C1` kanggo GS1-128, `]d2` kanggo GS1 DataMatrix, `]e0` kanggo GS1 DataBar / GS1 Composite).

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

Yen operator datane ora bisa nggawa GS1 AI (tuladha, barcode pos), parsing mandheg sanalika kanthi kesalahan `GE-D002`.

---

### Tahap 2 — Sintaksis

Tansah mlaku tanpa syarat. Kasusun saka rong langkah cilik:

**2a. Tokenisasi (`AISyntaxParser`)**
- Maca dawaning kode AI saka rong aksara ngarep nganggo tabel prefiks GS1 (GS1 General Specifications Table 7-5).
- AI sing dawane tetep maca cacah byte sing pesthi saka input.
- AI sing dawane variabel diwaca nganti tekan aksara GS utawa pungkasaning input.
- AI sing komponene akeh, nilaine diiris dadi segmen saben komponen.

**2b. Validasi susunan (`SyntaxValidator`)**
- Mriksa AI sing dibaleni (`GE-S004`).
- Mriksa gumantungan AI sing dibutuhake, tuladha, AI `02` mbutuhake AI `37` (`GE-S005`).
- Mriksa pasangan AI sing dilarang (`GE-S006`).

Kesalahan ing tahap iki nduweni tataran `SYNTAX_ERROR` (tokeniser) utawa `INTEGRITY_ERROR` (susunan). Yen ana **kesalahan apa wae** — saka tokeniser utawa saka susunan — pipeline mandheg lan tahap isi lan interpretasi dilewati.

---

### Tahap 3 — Isi

Mung mlaku yen Tahap 2 ora ngasilake kesalahan apa wae (dudu saka tokeniser, dudu saka susunan). Pipeline saben elemen kaya mangkene (saben langkah mung mlaku yen langkah sadurunge ora ngasilake kesalahan):

| Langkah | Validator | Kode kesalahan |
|---|---|---|
| Pamriksan regex | `RegexValidator` | `GE-C001` |
| Charset lan format komponen | `ComponentValidator` | `GE-C005` + kode format saben kahanan (`GE-C054`–`GE-C115`) |
| Angka centhang / aksara centhang | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validasi teges khusus | `ContentValidatorRegistry` | kode isi saben kahanan (`GE-C116`–`GE-C170`) |

Kesalahan ing tahap iki nduweni tataran `FORMAT_ERROR` utawa `DATA_ERROR`, kajaba siji: pamriksan
prefiks perusahaan GS1 ing AI sing dadi kunci GS1 iku mung pituduh lan nduweni tataran `WARNING`
(deleng [Referensi Kesalahan](#referensi-kesalahan)), mula prefiks perusahaan sing ora diakoni,
dhewekan, ora ndadekake asile dadi ora sah.

---

### Tahap 4 — Interpretasi

Mung mlaku ing modhe `INTERPRETATION` lan mung yen ora ana elemen sing nggawa kesalahan saka tahap sadurunge. `InterpretationEngine` nyugihake saben elemen kanthi metadata sing dilabeli:

- Tanggal sing diformat ulang dadi `dd/mm/yyyy`
- Panyigaran angka centhang GTIN lan panggolekan prefiks perusahaan GS1
- Jeneng negara miturut ISO 3166
- Jeneng lan lambang mata uang miturut ISO 4217
- Jumlah desimal sing wis didekode
- Cuwilan HRI (Interpretasi sing Bisa Diwaca Manungsa)

Asile dicanthelake minangka entri `GS1AIInterpretation` ing saben `GS1AIObjectElement`.

---

## Konfigurasi Parsing (`ParseConfig`)

`GaiaParser` mbukak lawang mlebu mung loro:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` mlaku nganggo **konfigurasi standar**: modhe `INTERPRETATION`, tanggal little-endian (`dd/mm/yyyy`) kanthi pamisah `/` lan taun patang angka, sarta pesen kesalahan basa **Inggris**. Kanggo ngowahi salah sijine — kalebu modhe parsing — gaweya `ParseConfig` nganggo builder-e sing lancar banjur nganggoa overload sing gardhane loro.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Kabeh enum pilihan manggon ing `GaiaConstants`.

### Pilihan

| Metode builder | Enum (`GaiaConstants`) | Standar | Pengaruh |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Jerone pipeline — deleng [Modhe Parsing](#modhe-parsing). |
| `language(...)`      | `Language`      | `ENGLISH`        | Basa pesen kesalahan, label interpretasi, **lan uga** katrangan AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Urutan komponen tanggal: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Aksara ing antaraning komponen tanggal: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) utawa `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) utawa `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Ngliwati pamriksan susunan "requires" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Ngliwati pamriksan susunan "excludes" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / jeneng kelas | ora ana | Kode sing nulis ulang input mentah sadurunge parsing — [rong modifier bawaan](#modifier-bawaan) ditambah apa wae sing kokgawe. Deleng [Input Modifier](#input-modifier). |

Papat pilihan tanggal mau mung ngenani string tanggal sing diformat dening pangsugih interpretasi (ing modhe `INTERPRETATION`); ora ngowahi validasi. Nilai ing builder kena ora diisi — pilihan apa wae sing ora disetel (utawa diwenehi `null`) tetep nganggo standare.

### Pesen lan label sing dilokalake

`language(...)` milih basa kanggo **telung** jinis teks sing bisa diwaca manungsa: pesen kesalahan, label interpretasi (`getLabel()` saka saben `GS1AIInterpretation`), lan katrangan AI (`getDescription()` saka saben `GS1AIObjectElement`).

**35 basa** ditetepake dening `GaiaConstants.Language`, nyakup basa-basa sing paling akeh panuture ing donya: Inggris, Prancis, Spanyol, Jerman, Italia, Portugis, Walanda, Polandia, Rusia, Ukraina, Ceko, Swedia, Cina, Jepang, Korea, Arab, Indonesia, Hindi, Turki, Bengali, Urdu, Vietnam, Nigerian Pidgin, Arab Mesir, Marathi, Telugu, Tamil, Kanton, Wu, Tagalog, Persia, Hausa, Punjabi, Jawa, lan Swahili.

Kahanan terjemahan (kaya sing dikirim):
- **Label interpretasi** — wis diterjemahake kanggo kabeh basa.
- **Pesen kesalahan** — wis diterjemahake kanggo kabeh basa.
- **Katrangan AI** — wis diterjemahake kanggo kabeh basa kajaba Inggris. Inggris dudu katalog sing kapisah: iku diwaca langsung saka kolom `description` ing entri AI ing `gs1-application-identifiers.jsonld`, sing dadi papan bali kanggo saben katrangan AI.

Nigerian Pidgin (`NIGERIAN_PIDGIN`), yaiku basa kreol adhedhasar Inggris, kanthi sengaja nganggo maneh teks Inggris kanggo label interpretasi lan pesen kesalahan. Katrangan AI dadi kajaba ing sajroning kajaba iki: kabeh mau diterjemahake dadi tembung Pidgin sing tenanan tinimbang nganggo maneh Inggris, amarga katalog katrangan AI digawe kapisah saka katalog label lan pesen. Terjemahan mesin kudu ditliti panutur asli sadurunge diendelake ing produksi.

Pesen, label, utawa katrangan apa wae sing ora ana ing katalog sawijining basa bakal bali menyang Inggris. Basa sing ditulis saka tengen menyang kiwa (Arab, Urdu, Arab Mesir, Persia) disimpen kanthi bener minangka string; nampilake kanthi RTL iku tanggung jawabe lapisan tampilan.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Label interpretasi dilokalake kanthi cara sing padha (nilaine ora owah — mung labele):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Katrangan AI uga dilokalake kanthi cara sing padha (mung `getTitle()`, tuladha `"GTIN"`, sing ora dilokalake):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Pormating tanggal

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Input Modifier

**Input modifier** iku kode sing nulis ulang string input mentah sadurunge Gaia nglakoni parsing. Modifier iku kanggo input sing pancen wis rusak nalika teka — scanner sing ngganti pamisah GS nganggo aksara sing katon, middleware sing mbuntel payload ing prefiks duweke vendor, sistem host sing ngowahi kabeh dadi aksara gedhe. Tinimbang ngresiki saben string ing saben papan sing nyeluk (lan mula ana siji papan sing bakal kleru sathithik), daftarna normalisasi sepisan ing `ParseConfig` banjur pasrahna pakaryan ngetrapake marang parser.

Modifier mlaku ing wiwitan banget saka `GaiaParser.parse(...)` — sadurunge nyopot correlation ID, sadurunge ngenali AIM Code ID, sadurunge mlebu pipeline GS1. Kabeh sing ing sabanjure mung ndeleng string sing wis ditulis ulang. Kalebu [rong modifier bawaan](#modifier-bawaan), **ora ana apa-apa sing disetel minangka standar** — kowe sing milih kanggo saben `ParseConfig`.

**Antarmuka:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modifier bawaan

Core jar nggawa rong modifier ing sangisore `tools.pantheum.gaia.modifier.custom`. Loro-lorone nangani rong cara sing paling kerep ndadekake payload GS1 rusak — kurung HRI sing dicithak nanging dianggep data, lan spasi kliwat — mula ora perlu nulis kelas dhewe kanggo kahanan sing umum:

| Kelas | `getName()` | Apa sing ditindakake |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Nyopot kurung HRI sing ngubengi saben AI (`(01)…(10)…`) lan mbalekake pamisah FNC1 sing dimaksud dening kurung mau. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Nyopot saben spasi (`0x20`) saka string elemen AI. |

Loro-lorone mung implementasi `ModifierInterface` biasa tanpa status khusus — padha didaftarake, diurutake, dilapurake, lan gagal kaya sing kokgawe dhewe:

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

Loro-lorone tanpa status lan aman kanggo thread, mula siji instance bisa dienggo bareng-bareng; lan kanggo panyebaran adhedhasar file konfigurasi, loro-lorone bisa disebut nganggo jeneng kelas sing jangkep (deleng [Ndaftarake modifier](#ndaftarake-modifier)).

#### `ModifierRemoveAIBrackets`

Interpretasi GS1 sing bisa diwaca manungsa nyithak saben AI ing sajroning kurung — `(01)09521234543213(10)ABC123` — mung adat cithak wae. Scanner utawa middleware apa wae sing disetel ngirim HRI bakal nerusake kurung mau kaya data, lan tokeniser ora ngerti arep diapakake.

Nyopot kurung iku mung setengah pakaryan. Ing HRI, kurung mbukak `(` saka AI *sabanjure* iku siji-sijine tandha rampunge nilai sadurunge, mula ing wujud kurung, AI sing dawane variabel ora mbutuhake FNC1. Yen kurunge dicopot sakepenake, wates mau ilang:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Mula modifier iki **nyelehake maneh FNC1 ing saben wates sing AI sadurunge dawane variabel**, mbalekake persis pedhotan sing sabenere dikode dening kurung mau:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Dawane digoleki saka `AiDefinitionRegistry` duweke parser dhewe, mula bisa nangani saben AI sing dawane variabel tinimbang gumantung dhaptar sing ditulis mati ing kode. Ana telung kahanan sing kanthi sengaja ora diutik-utik: nilai sing wis rampung nganggo FNC1 (sumber sing ngirim rong adat pisan ora bakal oleh pamisah kaping pindho), kode ing kurung sing dudu AI sing dikenal (AI sing ora dikenal ora ngandhani dawane), lan AI pungkasan ing string.

Panulisan ulang iki **idempoten** — lakokna ing outpute dhewe lan ora ana sing owah — mula aman uga ing alur campuran sing mung sawetara inpute nganggo kurung.

> **Wates.** `(` lan `)` iku aksara data GS1 sing sah dhewe, lan pola sing dienggo ing kene mung `\((\d{2,4})\)`. Yen ana nilai sing kebeneran isi angka rong-nganti-papat ing sajroning kurung, kurunge uga bakal kecopot. Etrapna mung ing sumber sing nganggo adat kurung HRI, aja ing sumber sing pancen duwe nilai nganggo kurung.

#### `ModifierRemoveSpaces`

Ana sawetara scanner, middleware, lan sistem cithak label sing nyelehake spasi kliwat ing string elemen sing sejatine wis bener — kanggo ngebaki kolom sing ambane tetep, kanggo misahake kumpulan supaya gampang diwaca, utawa kanggo mbuntel nilai sing dawa. Tokeniser nganggep saben spasi minangka data, mula ngrusak nilai sing diampiri, lan kanggo AI sing dawane variabel, kabeh sing ing sabanjure geser.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Mung ASCII `0x20` sing dicopot. Spasi putih liyane tetep ing panggonane — tuladha, tab iku ora ana ing himpunan aksara sing bisa dikode GS1, mula parser nglapurake dadi `GE-S008` tinimbang nguntal meneng-menengan.

> **Wates.** Spasi (`0x20`) iku bagean saka himpunan aksara GS1 sing ora owah, mula nomer bets/lot utawa nomer suku cadang pelanggan bisa wae kanthi sah isi spasi. Modifier ora bisa mbedakake spasi kliwat lan spasi sing sanyatane; etrapna mung ing sumber sing kokngerteni ora nganggo spasi ing nilai AI-ne.

#### Prefiks dilewati, ora ditulis ulang

Modifier mlaku nalika parser durung nyopot apa-apa, mula input mentah bisa isih nggawa correlation ID, AIM Code ID, lan panuduh ECI. Loro modifier bawaan mau nganggo logika `CorrelationIdParser` lan `DataCarrierParser` duweke parser dhewe kanggo nemokake wiwitane string elemen AI, wiwit nulis ulang saka kono, banjur nyambung maneh asile karo prefiks sing **ora diutik-utik**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Operator data EAN/UPC sing nilaine diisi nganti dadi GTIN-14 (`isRequiresGtinPadding()`) dilewati kabeh — payloade iku nilai barcode angka mentah tanpa susunan AI apa wae, mula ing kono kurung lan spasi ora bisa duwe teges.

#### Urutan: spasi dhisik, kurung keri

Yen loro-lorone dienggo, **daftarna `ModifierRemoveSpaces` dhisik**. Panjodhoan kurung iku peka marang posisi: `( 01 )` sing ana spasine ora cocog karo `\((\d{2,4})\)`, mula kurunge kari lan pamisah sing dimaksud ora bakal bali.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```


### Nulis modifier

Yen ora ana siji-sijia saka rong modifier bawaan sing cocog, gaweya dhewe — mung ana siji metode ing antarmukane.

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

Yen panulisan ulange gumantung marang konfigurasi parsing, override-a wujud sing gardhane loro:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Prajanjiane:

| Aturan | Katrangan |
|---|---|
| Tanpa status lan aman kanggo thread | Siji instance saben kelas disimpen lan dienggo bareng ing saben parsing. |
| Konstruktor publik tanpa gardha | Mung dibutuhake yen modifier disebut nganggo jeneng kelas. |
| Tanganana `null` lan input kothong | Parser ora nyaring kabeh mau sadurunge rantaine mlaku. |
| Mbalekake `null` tegese "ora ana owah-owahan" | Nilai sadurunge diterusake. Balekna `input` tanpa owah yen modifiere ora cocog. |
| Luwih becik mbalekake tanpa owah tinimbang nguncalake exception | Modifier sing nguncalake exception mbatalake parsing — deleng [Nangani kegagalan](#nangani-kegagalan-modifier). |
| `getName()` | Override-a kanggo ngatur jeneng sing dilapurake ing `ModifierInfo`; standare jeneng kelas sing prasaja. |

### Ndaftarake modifier

Modifier mlaku miturut urutan sing kokwuwuhake, lan saben siji nampa output saka sing sadurunge. Daftarna kanthi instance, kanthi jeneng kelas sing jangkep, utawa kanthi dhaptar salah sijine:

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

[Modifier bawaan](#modifier-bawaan) uga disebut kaya duwekmu dhewe — **tansah nganggo jeneng sing jangkep**. Ora ana jeneng cendhak utawa panggolekan alias kanggo kabeh mau; `ModifierRegistry` ngrampungake saben modifier, bawaan utawa dudu, nganggo jeneng kelas sing jangkep.

`ModifierRegistry` sing ngrampungake jeneng; iku nggawe siji instance saben kelas sepisan nganggo konstruktor tanpa gardhane, banjur nyimpen kanggo saben konfigurasi sabanjure sing nyebut kelas sing padha. Panyerapan iki dumadi **nalika konfigurasi digawe**, mula jeneng sing ora ketemu, kelas sing ora nglakokake `ModifierInterface`, utawa sing ora bisa digawe instance-e, bakal nguncalake `IllegalArgumentException` sanalika ing kono — dudu meneng-menengan nalika parsing. Modifier sing ora bisa digawe liwat reflection (tuladha, amarga nggawa dependensi sing disuntikake) bisa didaftarake luwih dhisik supaya tetep bisa disebut nganggo jeneng:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Ndeleng apa sing wis ditindakake modifier

Yen ana modifier sing disetel, `ParseResult.getPayload()` nuduhake input sing **wis diowahi**. Sing asli tetep ana ing `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` nglapurake `getName()` saka saben modifier, sing standare jeneng kelas prasaja nanging rong modifier bawaan mau nge-override — mula rantai sing kasusun saka loro mau nuduhake jeneng tampilan tinimbang jeneng kelas:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

Yen ora ana modifier sing disetel, `getModifierInfo()` mbalekake `null`. Yen modifier wis mlaku nanging saben siji mbalekake input tanpa owah, informasine tetep ana nanging `isModified()` iku `false` — mung modifier sing pancen ngowahi input sing katon ing `getAppliedModifiers()`.

### Nangani kegagalan modifier

Modifier sing nguncalake exception mbatalake parsing. Exception mau dibuntel ing `GaiaModifierException` sing nyebut jeneng modifier sing salah, lan asile nggawa kesalahan internal `GE-I001` kanthi jeneng sing padha ing pesene; `getPayload()` nuduhake input sing ora diowahi. Kanthi sengaja, parsing **ora nerusake** nganggo string sing setengah ditulis ulang — langkah normalisasi sing gagal meneng-menengan bakal ngasilake asil sing katon sah nanging sejatine di-parse saka input sing salah.

---

## Modhe Parsing

Saben modhe dijenengi miturut [tahap pipeline](#pipeline-parsing) sing paling jero sing dilakokake; nanging saben tahap sadurunge iku tetep mlaku.

| Modhe | Mlaku nganti ngendi | Apa sing diwangsuli |
|---|---|---|
| `DATA_CARRIER` | Tahap 1 (ngarahake input) | Simbologi apa sing nggawa iki? |
| `SYNTAX` | Tahap 2 (sintaksis) | Kode AI lan dawane wis kabentuk bener? |
| `CONTENT` | Tahap 3 (isi) | Nilaine iku data GS1 sing sah? |
| `INTERPRETATION` | Tahap 4 (interpretasi) | Apa tegese nilai-nilai mau? |

### Modhe DATA_CARRIER

Mandheg sawise Tahap 1 — iku nyahake AIM Code ID lan ngenali simbologi, nanging ora mlebu pipeline parsing AI. Migunani kanggo ngenali simbologi lan ngarahake tanpa abote validasi jangkep.

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

**Kapan dienggo:** Nalika aplikasimu kudu ngerti jinis barcode-e sadurunge mutusake kepiye nangani payload — tuladha, ngarahake simbologi 1D lan 2D menyang panangan sing beda. Kanggo pangarahan mau, nganggoa [`DataCarrierType`](#datacarrierentry-lan-datacarriertype) sing wis nduweni tipe (`getDataCarrier().getDataCarrierType()`) tinimbang njodhokake string ing `getName()`.

---

### Modhe SYNTAX

Mandheg sawise Tahap 2. Migunani kanggo nyaring adhedhasar susunan tanpa ragade validasi isi.

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

**Kapan dienggo:** Nalika kowe kepengin mesthekake yen kode AI lan dawaning data wis bener sadurunge mlebu validasi jangkep, utawa nalika kowe nge-scan akeh-akehan sing kesalahan isine arang kedadeyan.

---

### Modhe CONTENT

Mandheg sawise Tahap 3.

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

> Akeh-akehe AI ora bisa ngadeg dhewe: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) lan
> `21` (SERIAL) — saben siji *mbutuhake* kunci identifikasi kayata AI `01` ing string elemen
> sing padha; mula yen GTIN ing tuladha ndhuwur dicopot, bakal gagal ing Tahap 2 kanthi
> `GE-S005` sadurunge tekan validasi isi. Kanggo nglakoni parsing marang cuwilan sing pancen
> sengaja tanpa AI kancane, setelen `skipRequiresCheck(true)` ing `ParseConfig`.

**Kapan dienggo:** Nalika kowe kudu ngerti yen nilai sing di-scan pancen manut GS1 sakabehe sadurunge dienggo ing proses bisnis, nanging kowe ora butuh abote pangsugih interpretasi.

---

### Modhe INTERPRETATION (standar)

Nglakokake pipeline sakabehe nganti Tahap 4. Iki standare yen `parse(String)` diceluk tanpa gardha modhe. Iki mung nyugihake elemen sing lulus validasi isi kanthi resik.

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

**Tuladha output:**
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

**Tuladha jumlah moneter (AI 3932 — rega kanthi kode mata uang ISO):**
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

**Kapan dienggo:** Nalika mbangun lapisan tampilan, piranti mriksa label, utawa UI apa wae sing butuh panyigaran nilai AI kanthi cara sing gampang dimangerteni manungsa.

---

## Correlation ID

Ana sawetara alur kerja sing nyelehake panuduh correlation cacah 8 angka duweke dhewe ing ngarepe input GS1 mentah supaya prastawa scan bisa disambungake maneh karo sawijining sesi utawa transaksi. Formate:

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

`~` (tilde) iku pamisahe. Iku **dudu** bagean saka isi GS1 — dicopot sadurunge parsing GS1 apa wae diwiwiti.

### Aturan pandeteksian

Prefikse dideteksi yen input wiwit kanthi persis 8 angka desimal ASCII (`0`–`9`) sing langsung diterusake `~`. Yen aksara kaping 9 dudu `~`, utawa yen salah siji saka 8 aksara ngarep dudu angka, input dianggep isi GS1 lugu tanpa prefiks correlation.

### Njupuk correlation ID

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

### Digabung karo AIM Code ID

Prefiks correlation bisa katon sadurunge AIM Code ID. Parser nangani iki kanthi cetha:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Kelas implementasi:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** ngode siji utawa luwih nilai AI langsung ing susunan URL HTTP(S), sing menehi panuduh identitas sing bisa dirampungake liwat web kanggo produk fisik. GAIA nglakokake *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) kanggo URI sing **ora dikompres**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` ngenali Digital Link URI kanthi otomatis — input apa wae sing wiwit kanthi `http://` utawa `https://` diarahake menyang `GS1DLParser`, sing nglakokake tahap isi lan interpretasi sing padha karo pipeline string elemen.

### Susunan URI lan peran AI

Saben AI ing sajroning Digital Link URI nduweni salah siji saka telung peran, sing bisa dijupuk ing saben `GS1AIObjectElement` liwat `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Peran | Panggonan | Tuladha |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Pasangan `/ai/value` sepisanan ing path (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Pasangan path sabanjure, diurutake miturut kunci utama (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parameter query sing kuncine kabeh angka (§4.10) | `?17=271231` |

Aturan susunan sing ditrapake (`DLPathRules`):
- Persis **siji** kunci identifikasi utama ing path; kunci tambahan kudu dikode minangka data attribute ing query.
- Key qualifier kudu ditampa dening kunci utama lan kudu katon miturut urutan sing ditemtokake. Qualifier sing opsional kena dilewati, nanging sing *pancen ana* tetep kudu manut urutan sing tetep — deleng [Urutan qualifier](#urutan-qualifier).
- Segmen path khusus apa wae kena ndhisiki kunci utama (tuladha, `/products/au/01/...`); jupuken liwat `getDigitalLinkInfo().getCustomPathStem()`.
- Kunci query sing dudu angka (`linkType`, `context`, lan parameter ekstensi kayata `23P`) diabaikan; kunci sing kabeh angka kudu AI sing sah lan dilabeli `validAsDataAttribute`.
- Aksara nilai sing di-percent-encode bakal didekode; AI `(03)` lan `(8014)` ora diidini.

Kunci utama lan urutan qualifier sing ditampa iku **saka data** ing tetepan AI — liwat bendera `gs1DigitalLinkPrimaryKey` lan atribut `gs1DigitalLinkQualifiers` — dudu ditulis mati ing kode.

Saben pelanggaran susunan, utawa input sing dudu URL, ngasilake kesalahan susunan Digital Link (`GE-L001`–`GE-L014`, siji kode saben kahanan). Metadata URL sing wis dipilah (`scheme`, `domain`, `path`, `customPathStem`, `query`, lan `java.net.URL`) tetep bisa dijupuk liwat `getDigitalLinkInfo()` senadyan ana kesalahan susunan.

### Urutan qualifier

Kanggo saben kunci utama, `gs1DigitalLinkQualifiers` ndhaptar siji utawa luwih urutan qualifier sing **wis diurutake**. Ing sajroning siji urutan, AI sing dibuntel ing kurung siku iku **opsional**, dene AI tanpa kurung iku **wajib** — padha karo notasi `[cpv-comp]` ing ABNF §4.9. Urutan-urutan kanggo siji kunci utama iku pilihan sing salah sijine wae.

GTIN (`01`), tuladhane, netepake rong urutan:

| Path | Urutan | Tegese |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — kabeh opsional, nanging urutane tetep kaya ngene |
| upui-path | `235` | TPX (wajib); GTIN + TPX = UPUI |

Mula `/01/09506000134352/10/LOT-ABC/21/SER` iku sah (LOT sadurunge SER, CPV dilewati), `/01/.../21/SER/10/LOT-ABC` iku **ditolak** (urutane kleru), lan `/01/09506000134352/235/2ABC456` iku upui-path. Pamriksan urutan iku panjodhoan subsequence sing njaga urutan, mula AI sing opsional kena dilewati nanging ora kena diowahi urutane.

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

**Kelas implementasi:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Nggarap Asil

### ParseResult

Asil paling dhuwur sing dibalekake `GaiaParser.parse()`.

| Metode | Ngasilake | Katrangan |
|---|---|---|
| `isValid()` | `boolean` | `true` yen ora ana kesalahan ing tataran apa wae. Pepeling ora ngenani kasahan. Tansah `true` yen `getAiObject()` iku `null`. |
| `getPayload()` | `String` | String input sawise prefiks correlation dicopot — lan sawise ditulis ulang dening [input modifier](#input-modifier) apa wae. |
| `getPayloadContent()` | `String` | Payload sing wis dicopot AIM Code ID lan prefiks ECI-ne. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (operator data sing ditolak amarga dudu GS1, tuladha operator data `]A0` saka Code 39), utawa `UNABLE_TO_DETERMINE_CONTENT` (yen `aiObject` iku `null`, tuladha ing modhe `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Jerone pipeline sing disetel (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Tahap paling jero sing pancen ditekani parsing — deleng ngisor. |
| `isParseComplete()` | `boolean` | `true` yen parsing tekan jero sing dijaluk (`achieved == requested`). Ora ana gandhengane karo `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Kabeh AI sing wis diweruhi. `null` ing modhe `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Kabeh kesalahan sing dudu WARNING (tataran objek + kabeh tataran elemen). |
| `getWarnings()` | `List<GaiaError>` | Kabeh pituduh WARNING (tataran objek + kabeh tataran elemen). |
| `hasWarnings()` | `boolean` | `true` yen ana pituduh WARNING sing diunggahake. |
| `getIssues()` | `List<GaiaError>` | Kesalahan lan pepeling digabung. |
| `hasDataCarrier()` | `boolean` | `true` yen ana AIM Code ID sing diakoni. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata simbologi, utawa `null` yen ora ana operator data sing kenali. |
| `hasEci()` | `boolean` | `true` yen ana panuduh ECI sing dicopot saka payload. |
| `getEci()` | `EciEntry` | Metadata pangodean ECI, utawa `null`. |
| `hasCorrelationId()` | `boolean` | `true` yen prefiks correlation `DDDDDDDD~` ana ing input asli. |
| `getCorrelationInfo()` | `CorrelationInfo` | Correlation ID sing dijupuk, utawa `null` yen ora ana. |
| `isInputModified()` | `boolean` | `true` yen ana [input modifier](#input-modifier) sing ngowahi input. |
| `getModifierInfo()` | `ModifierInfo` | Apa sing wis ditindakake rantai modifier — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` yen ora ana modifier sing disetel. |
| `getTiming()` | `ProcessingTiming` | Wektu jam saka parsing — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` yen ora digawe dening `GaiaParser`. |
| `getVersion()` | `String` | Versi library sing ngasilake asil kasebut. |

#### Modhe parsing sing dijaluk lan sing ditekani

Pipeline mlaku ing undhak-undhakan **SYNTAX → CONTENT → INTERPRETATION** lan mandheg luwih awal yen ana kesalahan, mula modhe sing pancen *ditekani* bisa luwih cethek tinimbang modhe sing *dijaluk*. `getAchievedParseMode()` nglapurake tekan ngendi:

| Sing dijaluk | Apa sing kedadeyan | Sing ditekani | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | kesalahan **sintaksis / susunan** mandhegake parsing sawise tokenisasi | `SYNTAX` | `false` |
| `INTERPRETATION` | kesalahan **isi** (format/angka centhang kleru) ngalangi pangsugih | `CONTENT` | `false` |
| `CONTENT` | isi tansah mlaku nganti rampung (kesalahan mung dicathet, ora mateni) | `CONTENT` | `true` |
| apa wae (input resik) | pipeline tekan jero sing dijaluk | = sing dijaluk | `true` |
| `DATA_CARRIER` | operator data wis disahake; isi AI ora di-parse | `DATA_CARRIER` | `true` |
| apa wae | operator data ditolak sadurunge parsing AI (tuladha, operator data `]A0` sing dudu GS1) | `SYNTAX` | `false` |

`isParseComplete()` ora ana gandhengane karo `isValid()`: parsing `CONTENT` marang GTIN sing angka centhange kleru iku **rampung** (tahap isine wis mlaku) nanging **ora sah** (angka centhange gagal). Nganggoa `isParseComplete()` kanggo takon "apa pipeline mlaku nganti jero sing dakjaluk?" lan `isValid()` kanggo "apa datane wis kabentuk bener?".

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

Kumpulan elemen AI sing wis diweruhi.

| Metode | Katrangan |
|---|---|
| `getAis()` | Kabeh instance `GS1AIObjectElement` miturut urutan ing input. |
| `get(String aiCode)` | Elemen sepisanan sing cocog karo kode AI sing diwenehake, utawa `null`. |
| `contains(String aiCode)` | `true` yen ana AI kanthi kode kasebut. |
| `size()` | Cacahing AI sing wis diweruhi. |
| `isValid()` | `true` yen ora ana kesalahan tataran objek lan ora ana elemen sing duwe kesalahan. |
| `toHriString()` | String HRI, tuladha `(01)09506000134352 (17)261231`. |
| `toElementString()` | String elemen mentah — tanpa kurung, ana FNC1 sawise saben elemen sing dawane variabel — tuladha `010950600013435210LOT-ABC<GS>17271231`. Mbalekake `null` yen `isValid()` iku `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` yen `hasDigitalLink()` bener, yen ora `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` yen inpute GS1 Digital Link URI sing nggawa kunci identifikasi utama. URL sing kabentuk bener nanging tanpa kunci utama isih menehi `getDigitalLinkInfo()` nanging mbalekake `false` ing kene. |
| `getCanonicalDigitalLink()` | GS1 Digital Link URI kanonik (§4.12) ing `https://id.gs1.org` — kunci utama lan qualifier minangka segmen path, data attribute minangka parameter query sing diurutake miturut kunci AI — utawa `null` yen ora ana kunci utama. |
| `getDigitalLinkInfo()` | Metadata pamilahan URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), utawa `null` yen dudu Digital Link. |
| `getAllErrors()` | Tataran objek + kabeh kesalahan elemen (dudu WARNING). |
| `getAllWarnings()` | Tataran objek + kabeh pepeling elemen. |
| `getAllIssues()` | Kabeh digabung. |

---

### GS1AIObjectElement

Siji instance AI sing wis diweruhi.

| Metode | Katrangan |
|---|---|
| `getAi()` | Kode AI, tuladha `"01"`, `"3102"`. |
| `getTitle()` | Judhul data GS1, tuladha `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Katrangan GS1 sing jangkep kanggo AI, **wis dilokalake miturut basa parsing** (tuladha, `"Global Trade Item Number (GTIN)"` ing basa Inggris). Bali menyang teks Inggris saka tetepan AI yen durung diterjemahake. |
| `getFormatString()` | Panuduh format sing nyakup AI *lan uga* datane, tuladha `"N2+N14"` kanggo AI `01`, `"N2+X..20"` kanggo AI `10`, `"N4+N3+N..15"` kanggo AI `3932`. |
| `getValue()` | Nilai data mentah sing dijupuk saka string elemen. |
| `isFixedLength()` | `true` yen AI-ne duwe dawa data sing tetep. |
| `getPosition()` | Offset aksara sing wiwit saka nol ing input asli. |
| `getGS1ComponentValues()` | Irisan nilai saben komponen (kanggo AI sing komponene akeh). |
| `getErrors()` | Kesalahan tataran elemen sing dudu WARNING. |
| `getWarnings()` | Pituduh WARNING ing tataran elemen. |
| `getIssues()` | Kesalahan lan pepeling tataran elemen digabung. |
| `hasErrors()` | `true` yen ana kesalahan sing dudu WARNING sing kacanthel. |
| `hasWarnings()` | `true` yen ana pituduh WARNING sing kacanthel. |
| `getInterpretations()` | Entri `GS1AIInterpretation` (diisi ing modhe INTERPRETATION). |
| `getInterpretation(String type)` | Interpretasi sepisanan sing cocog karo kunci tipe `GS1Constants_Enricher` sing diwenehake, utawa `null`. |
| `getDigitalLinkAIType()` | Peran elemen ing Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), utawa `null` kanggo input string elemen. |
| `hasDigitalLinkAIType()` | `true` yen peran Digital Link wis diwenehake. |

---

### GaiaError

Kesalahan validasi utawa pituduh sing ora bisa diowahi.

| Metode | Katrangan |
|---|---|
| `getId()` | Panuduh katalog, tuladha `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, utawa `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, utawa `INTERNAL`. |
| `getCode()` | Kode cendhak sing bisa diwaca mesin. |
| `getAi()` | Kode AI sing njalari kesalahan, utawa `null` kanggo kesalahan tataran objek. |
| `getMessage()` | Pesen sing bisa diwaca manungsa lan wis diisi nilaine. |
| `getPosition()` | Offset aksara sing wiwit saka nol ing input asli. |

---

### GS1AIInterpretation

Siji cuwilan interpretasi sing dilabeli, dicanthelake ing `GS1AIObjectElement` ing modhe `INTERPRETATION`.

| Metode | Katrangan |
|---|---|
| `getType()` | Kunci tipe sing bisa diwaca mesin, tuladha `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Ora owah ing kabeh basa. |
| `getLabel()` | Label sing bisa diwaca manungsa, **wis dilokalake miturut basa parsing** (tuladha, `"Date"` / `"GS1 company prefix"` ing basa Inggris). |
| `getValue()` | Nilai sing dijupuk/disugihake, tuladha `"31/12/2026"`, `"9506000"`. Ora dilokalake. |

---

### DataCarrierEntry lan DataCarrierType

Yen inpute nggawa AIM Code ID, `ParseResult.getDataCarrier()` mbalekake `DataCarrierEntry` sing nyritakake simbol sing nggawa datane. Entri kasebut iku cathetan registri sing gumathok kanggo AIM Code ID sing cocog; `DataCarrierType` iku enum wektu-kompilasi sing dienggoni.

#### DataCarrierEntry

Metadata kanggo siji AIM Code ID sing diakoni (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metode | Katrangan |
|---|---|
| `getAimCodeId()` | AIM Code ID sing cocog, tuladha `"]C1"`. |
| `getName()` | Jeneng simbol gumathok sing bisa diwaca manungsa, tuladha `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Katrangan operator data sing luwih dawa. |
| `getType()` | Tipe susunan operator data minangka string (padha karo `getDataCarrierType().getCategory()`). |
| `getStandard()` | Standar simbologi, yen kacathet. |
| `getDataCarrierType()` | `DataCarrierType` sing wis nduweni tipe kanggo entri iki — iki sing luwih becik dienggo kanggo ngarahake ing kode. |
| `isGs1Capable()` | `true` yen operator datane bisa nggawa data GS1 (string elemen AI lan/utawa Digital Link). |
| `isGs1AICapable()` | `true` yen operator datane bisa nggawa string elemen GS1 AI. |
| `isGs1DigitalLinkCapable()` | `true` yen operator datane bisa nggawa GS1 Digital Link URI. |
| `isEciCapable()` | `true` yen operator datane ndhukung panuduh ECI. |
| `isRequiresGtinPadding()` | `true` kanggo operator data EAN/UPC/ITF sing nilai angkane diisi nganti dadi GTIN-14 sadurunge parsing AI. |

#### DataCarrierType

Enum wektu-kompilasi kanggo tipe operator data, kanthi kunci AIM Code ID sing diwenehake ing ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Aksara sawise `]` (yaiku *aksara kode*) sing milih kulawargane; akeh-akehe kulawarga cocog karo siji konstanta sing nyakup saben modifier (`ITF` nyakup `]I0`–`]I2`; `EAN_UPC` nyakup EAN-13, UPC-A, UPC-E lan EAN-8). Ing ngendi GS1 nyawiskake modifier kanggo data AI, varian kasebut duwe konstanta dhewe — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — beda karo kanca-kancane sing biasa. Yen ora ana AIM Code ID, utawa yen nyebut operator data sing ora dikenal, tipene yaiku `UNKNOWN`.

| Metode | Katrangan |
|---|---|
| `getCategory()` | `GaiaConstants.DataCarrierTypeCategory` sing amba: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, utawa `OTHER`. |
| `getCodeChar()` | Aksara kode AIM sing nuduhake kulawargane, tuladha `"Q"` kanggo QR Code; `null` kanggo `UNKNOWN`. |
| `getDisplayName()` | Jeneng *tipe* sing bisa diwaca manungsa (bisa luwih amba tinimbang `DataCarrierEntry.getName()` — tuladha, `"EAN-13 / UPC-A / UPC-E / EAN-8"` lan `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` kanggo konstanta sing tansah ateges data GS1 AI: papat varian sing disawiskake GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) ditambah `GS1_DATABAR`, sing pancen GS1 saka asale amarga saben modifier `]e` iku GS1 DataBar. Luwih ciyut tinimbang `DataCarrierEntry.isGs1AICapable()` — `QR_CODE` biasa uga isih bisa nggawa data GS1 AI. |
| `static forAimCodeId(String)` | Ngrampungake tipe langsung saka AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); mbalekake `UNKNOWN` kanggo ID sing ora ana, salah wujud, utawa ora diakoni. |

Ngarahake miturut tipe, dudu miturut jeneng — tuladha, misahake simbol linear (Code-128) saka 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` mung nyakup simbol matriks lan titik; operator data sing tumpuk-linear (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) iku `STACKED_LINEAR`, senadyan lumrahe diarani barcode
"2D". Kanggo nganggep loro-lorone dadi siji kelompok — upamane, kanggo mutusake apa butuh
imager tinimbang scanner laser — priksanen salah siji saka rong kategori kasebut.

> Ngrampungake tipe iku mbutuhake AIM Code ID ana ing asil scan; tanpa iku, `getDataCarrier()` iku `null` lan tipene `UNKNOWN`. Setelen scanner supaya ngirim prefiks AIM Code ID.

---

## Referensi Kesalahan

| Kode | Tataran | Tahap | Tegese |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Prefiks AI sing ora dikenal — ora bisa netepake dawaning data |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Input kecendhaken kanggo maca kode AI sing jangkep |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Nilai kepotong — aksarane kurang saka sing dibutuhake AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Application Identifier sing dibaleni ing string elemen |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Gumantungan AI sing dibutuhake ora ana |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Pasangan AI sing dilarang — rong AI sing ora kena bebarengan |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Kegagalan tokenisasi sing ora dinyana |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Aksara ing sanjabane himpunan aksara sing bisa dikode GS1 ing string elemen |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Pamisah FNC1 sing dibutuhake ora ana sawise AI sing dawane variabel |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Ana data sing turah ngluwihi wates dhuwur kabeh komponen |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Pamisah FNC1 sawise AI sing dawane tetep ing tengahing string |
| `GE-W002` | WARNING | SYNTAX | FNC1 sing turah ing pungkasaning string elemen (mung pituduh) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Pelanggaran susunan Digital Link URI — siji kode saben kahanan (URI salah wujud, scheme, host, urutan qualifier, AI sing dilarang, ora ana kunci utama (`GE-L013`), kunci utama luwih saka siji (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Nilaine ora lulus pola regex duweke AI |
| `GE-C003` | DATA_ERROR | CONTENT | Validasi angka centhang gagal |
| `GE-C004` | DATA_ERROR | CONTENT | Validasi pasangan aksara centhang gagal |
| `GE-C005` | FORMAT_ERROR | CONTENT | Nilai komponen isi aksara ing sanjabane himpunan aksara sing diidini |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Kegagalan format komponen — siji kode saben kahanan validator (deleng `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Kegagalan validasi teges khusus — siji kode saben kahanan validator (deleng `content/validator/`). **Kajaba:** 14 pamriksan prefiks perusahaan GS1 sing didhaptar ing ngisor nduweni tataran `WARNING`, lan `GE-C168` (kode negara angka ISO 3166-1 sing ora diakoni) nduweni tataran `FORMAT_ERROR`. |
| Pamriksan prefiks perusahaan GS1 | WARNING | CONTENT | Kuncine ora wiwit kanthi prefiks perusahaan GS1 sing diakoni, ing AI sing dadi kunci GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Mung pituduh — ora ngenani kasahan. |
| `GE-C169` | DATA_ERROR | CONTENT | Angka centhang IMEI (Luhn) gagal ing AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Angka centhang EID (Luhn) gagal ing AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | AIM Code ID sing ora diakoni |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Operator data kenali nanging ora ndhukung string elemen GS1 AI utawa Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Kesalahan internal sing ora dinyana |

> **Cacad sing wis dingerteni ing panampilan pesen.** Cithakan katalog ngapit nilai sing
> disisipake nganggo tandha petik tunggal dobel gaya MessageFormat (`''{value}''`), nanging
> `ErrorRegistry` nyisipake nganggo `String.replace` biasa, mula pandhobelan mau lestari
> nganti tekan `getMessage()` — saiki kowe bakal weruh `value ''09506000134351''` ing papan
> sing teks pesene ing pandhuan iki nuduhake `value '09506000134351'`. Iki ngenani saben
> pesen sing ngapit nilai ing kabeh 35 katalog basa. Aja mem-parsing pesen kesalahan;
> jodhokna ing `getId()` / `getCode()`.

---

## Aman kanggo Thread

`GaiaParser` iku aman kanggo thread sawise digawe. Siji instance kena dienggo bareng ing pirang-pirang thread lan dienggo bebarengan. Cara sing disaranake yaiku nggawe siji instance nalika aplikasi wiwit banjur dienggo maneh:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` ora bisa diowahi lan padha amane kanggo dienggo bareng. Siji-sijine tanggung jawab keamanan thread sing ora bisa dijamin library kanggo kowe iku ana ing [input modifier](#input-modifier): siji instance saben modifier disimpen lan dienggo bareng ing saben parsing sing mlaku bebarengan, mula implementasine kudu tanpa status.

---


## Lampiran A — Konstanta String AI

`GS1Constants_AICodes` (ing paket `tools.pantheum.gaia.gs1.constants`) netepake siji konstanta `String` kanggo saben Application Identifier sing diakoni GAIA. Nganggoa konstanta iki tinimbang nulis mati string kode AI mentah:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Saben konstanta ngemot wujud string saka kode AI (tuladha, `AI_01_GTIN = "01"`).

### Identifikasi lan serialisasi

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Kode Kontainer Pengiriman Serial (SSCC). |
| `01` | `AI_01_GTIN` | Nomer Item Dagang Global (GTIN). |
| `02` | `AI_02_CONTENT` | Nomer Item Dagang Global (GTIN) saka item dagang sing kamot. |
| `03` | `AI_03_MTO_GTIN` | Identifikasi item dagang Digawe-miturut-Pesenan (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Nomer bets utawa lot. |
| `20` | `AI_20_VARIANT` | Varian produk internal. |
| `21` | `AI_21_SERIAL` | Nomer seri. |
| `22` | `AI_22_CPV` | Varian produk konsumen. |
| `235` | `AI_235_TPX` | Ekstensi Serial Nomer Item Dagang Global (GTIN) sing Dikontrol Pihak Katelu (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identifikasi produk tambahan sing diwenehake dening pabrikan. |
| `241` | `AI_241_CUST_PART_NO` | Nomer bagian pelanggan. |
| `242` | `AI_242_MTO_VARIANT` | Nomer variasi Digawe-miturut-Pesenan. |
| `243` | `AI_243_PCN` | Nomer komponen kemasan. |
| `250` | `AI_250_SECONDARY_SERIAL` | Nomer seri sekunder. |
| `251` | `AI_251_REF_TO_SOURCE` | Referensi menyang entitas sumber. |
| `253` | `AI_253_GDTI` | Pengenal Jinis Dokumen Global (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Komponen ekstensi Nomer Lokasi Global (GLN). |
| `255` | `AI_255_GCN` | Nomer Kupon Global (GCN). |
| `30` | `AI_30_VAR_COUNT` | Cacahing item variabel (item dagang ukuran variabel). |
| `37` | `AI_37_COUNT` | Cacahing item dagang utawa cuwilan item dagang ing sajroning unit logistik. |

### Tanggal lan wektu

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Tanggal produksi (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Tanggal jatuh tempo (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Tanggal kemasan (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Tanggal paling apik sadurunge (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Tanggal adol paling akhir (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Tanggal kadaluwarsa (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Tanggal wektu pangiriman ora sadurunge (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Tanggal wektu pangiriman ora luwih saka (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Tanggal rilis (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Tanggal lan wektu kadaluwarsa (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Tanggal beku pisanan (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Tanggal panen (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Tanggal tes paling akhir (YYMMDD[hhmm]). |

### Cacah lan ukuran — ukuran variabel (metrik)

Kulawarga AI patang angka `310n`–`369n` ngode cacah kanthi ukuran variabel. Angka katelu milih tipe ukurane; **angka kapapat** (`n`, 0–5) iku cacahing panggonan desimal sing dimaksud — tuladha, `AI_3102_NET_WEIGHT_KG` tegese bobot netto ing kilogram kanthi 2 panggonan desimal.

| Kulawarga | Pola konstanta (`n` = angka panggonan desimal) | Katrangan |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Bobot netto, kilogram (item dagang ukuran variabel). |
| `311n` | `AI_311n_LENGTH_M` | Dawa utawa dimensi kaping siji, meter (item dagang ukuran variabel). |
| `312n` | `AI_312n_WIDTH_M` | Ambane, diameter, utawa dimensi kaping loro, meter (item dagang ukuran variabel). |
| `313n` | `AI_313n_HEIGHT_M` | Jero, kandel, dhuwur, utawa dimensi kaping telu, meter (item dagang ukuran variabel). |
| `314n` | `AI_314n_AREA_M` | Area, meter pesagi (item dagang ukuran variabel). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volume netto, liter (item dagang ukuran variabel). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volume netto, meter kubik (item dagang ukuran variabel). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Bobot logistik, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Dawa utawa dimensi kaping siji, meter. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Ambane, diameter, utawa dimensi kaping loro, meter. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Jero, kandel, dhuwur, utawa dimensi kaping telu, meter. |
| `334n` | `AI_334n_AREA_M_LOG` | Area, meter pesagi. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volume logistik, liter. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volume logistik, meter kubik. |
| `337n` | `AI_337n_KG_PER_M` | Kilogram saben meter pesagi. |

### Cacah lan ukuran — ukuran variabel (imperial / US)

| Kulawarga | Pola konstanta (`n` = angka panggonan desimal) | Katrangan |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Bobot netto, pon (item dagang ukuran variabel). |
| `321n` | `AI_321n_LENGTH_IN` | Dawa utawa dimensi kaping siji, inci (item dagang ukuran variabel). |
| `322n` | `AI_322n_LENGTH_FT` | Dawa utawa dimensi kaping siji, kaki (item dagang ukuran variabel). |
| `323n` | `AI_323n_LENGTH_YD` | Dawa utawa dimensi kaping siji, yard (item dagang ukuran variabel). |
| `324n` | `AI_324n_WIDTH_IN` | Ambane, diameter, utawa dimensi kaping loro, inci (item dagang ukuran variabel). |
| `325n` | `AI_325n_WIDTH_FT` | Ambane, diameter, utawa dimensi kaping loro, kaki (item dagang ukuran variabel). |
| `326n` | `AI_326n_WIDTH_YD` | Ambane, diameter, utawa dimensi kaping loro, yard (item dagang ukuran variabel). |
| `327n` | `AI_327n_HEIGHT_IN` | Jero, kandel, dhuwur, utawa dimensi kaping telu, inci (item dagang ukuran variabel). |
| `328n` | `AI_328n_HEIGHT_FT` | Jero, kandel, dhuwur, utawa dimensi kaping telu, kaki (item dagang ukuran variabel). |
| `329n` | `AI_329n_HEIGHT_YD` | Jero, kandel, dhuwur, utawa dimensi kaping telu, yard (item dagang ukuran variabel). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Bobot logistik, pon. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Dawa utawa dimensi kaping siji, inci. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Dawa utawa dimensi kaping siji, kaki. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Dawa utawa dimensi kaping siji, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Ambane, diameter, utawa dimensi kaping loro, inci. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Ambane, diameter, utawa dimensi kaping loro, kaki. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Ambane, diameter, utawa dimensi kaping loro, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Jero, kandel, dhuwur, utawa dimensi kaping telu, inci. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Jero, kandel, dhuwur, utawa dimensi kaping telu, kaki. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Jero, kandel, dhuwur, utawa dimensi kaping telu, yard. |
| `350n` | `AI_350n_AREA_IN` | Area, inci pesagi (item dagang ukuran variabel). |
| `351n` | `AI_351n_AREA_FT` | Area, kaki pesagi (item dagang ukuran variabel). |
| `352n` | `AI_352n_AREA_YD` | Area, yard pesagi (item dagang ukuran variabel). |
| `353n` | `AI_353n_AREA_IN_LOG` | Area, inci pesagi. |
| `354n` | `AI_354n_AREA_FT_LOG` | Area, kaki pesagi. |
| `355n` | `AI_355n_AREA_YD_LOG` | Area, yard pesagi. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Bobot netto, ons troy (item dagang ukuran variabel). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Bobot netto (utawa volume), ons (item dagang ukuran variabel). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volume netto, quart (item dagang ukuran variabel). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volume netto, galon A.S. (item dagang ukuran variabel). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volume logistik, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volume logistik, galon A.S. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volume netto, inci kubik (item dagang ukuran variabel). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volume netto, kaki kubik (item dagang ukuran variabel). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volume netto, yard kubik (item dagang ukuran variabel). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volume logistik, inci kubik. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volume logistik, kaki kubik. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volume logistik, yard kubik. |

### Rega lan jumlah moneter

Angka kapapat (`n`) ngode cacahing panggonan desimal sing dimaksud. Rentang sing diidini beda-beda
saben kulawarga — deleng kolom `n`.

| Kulawarga | Pola konstanta (`n` = angka panggonan desimal) | `n` | Katrangan |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Jumlah sing kudu dibayar utawa nilai kupon, mata uang lokal. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Jumlah sing kudu dibayar kanthi kode mata uang ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Jumlah sing kudu dibayar, wilayah moneter tunggal (item dagang ukuran variabel). |
| `393n` | `AI_393n_PRICE` | 0–9 | Jumlah sing kudu dibayar kanthi kode mata uang ISO (item dagang ukuran variabel). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Persentase diskon kupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Jumlah sing kudu dibayar saben unit ukuran, wilayah moneter tunggal (item dagang ukuran variabel). |

### Lokasi lan pangiriman

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Nomer pesenan tuku pelanggan. |
| `401` | `AI_401_GINC` | Nomer Identifikasi Global kanggo Kiriman (GINC). |
| `402` | `AI_402_GSIN` | Nomer Identifikasi Pengiriman Global (GSIN). |
| `403` | `AI_403_ROUTE` | Kode rute. |
| `410` | `AI_410_SHIP_TO_LOC` | Kirim menyang Nomer Lokasi Global (GLN). |
| `411` | `AI_411_BILL_TO` | Nomer Lokasi Global (GLN) sing ditagih / difakturake. |
| `412` | `AI_412_PURCHASE_FROM` | Dituku saka Nomer Lokasi Global (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | Kirim kanggo / Terusake menyang Nomer Lokasi Global (GLN). |
| `414` | `AI_414_LOC_NO` | Identifikasi lokasi fisik - Nomer Lokasi Global (GLN). |
| `415` | `AI_415_PAY_TO` | Nomer Lokasi Global (GLN) pihak sing nagih. |
| `416` | `AI_416_PROD_SERV_LOC` | Nomer Lokasi Global (GLN) papan produksi utawa layanan. |
| `417` | `AI_417_PARTY` | Nomer Lokasi Global (GLN) pihak. |
| `420` | `AI_420_SHIP_TO_POST` | Kode pos kirim menyang ing sajroning siji otoritas pos. |
| `421` | `AI_421_SHIP_TO_POST` | Kode pos kirim menyang kanthi kode negara ISO. |
| `422` | `AI_422_ORIGIN` | Negara asal item dagang. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Negara pemrosesan awal. |
| `424` | `AI_424_COUNTRY_PROCESS` | Negara pemrosesan. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Negara papan dibongkar. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Negara sing nyakup kabeh rantai proses. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivisi negara asal. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Jeneng perusahaan kirim menyang. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Kontak kirim menyang. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Baris alamat 1 kirim menyang. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Baris alamat 2 kirim menyang. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Pinggiran kutha kirim menyang. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Dhaerah kirim menyang. |
| `4306` | `AI_4306_SHIP_TO_REG` | Wilayah kirim menyang. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Kode negara kirim menyang. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Nomer telpon kirim menyang. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Lokasi GEO kirim menyang. |
| `4310` | `AI_4310_RTN_TO_COMP` | Jeneng perusahaan mbalekake menyang. |
| `4311` | `AI_4311_RTN_TO_NAME` | Kontak mbalekake menyang. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Baris alamat 1 mbalekake menyang. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Baris alamat 2 mbalekake menyang. |
| `4314` | `AI_4314_RTN_TO_SUB` | Pinggiran kutha mbalekake menyang. |
| `4315` | `AI_4315_RTN_TO_LOC` | Dhaerah mbalekake menyang. |
| `4316` | `AI_4316_RTN_TO_REG` | Wilayah mbalekake menyang. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Kode negara mbalekake menyang. |
| `4318` | `AI_4318_RTN_TO_POST` | Kode pos mbalekake menyang. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Nomer telpon mbalekake menyang. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Deskripsi kode layanan. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Tandha barang mbebayani. |
| `4322` | `AI_4322_AUTH_LEAVE` | Wewenang ninggalake kiriman. |
| `4323` | `AI_4323_SIG_REQUIRED` | Tandha mbutuhake tandha tangan. |
| `4330` | `AI_4330_MAX_TEMP_F` | Suhu maksimum ing Fahrenheit (dituduhake ing satus-satusan derajat). |
| `4331` | `AI_4331_MAX_TEMP_C` | Suhu maksimum ing Celsius (dituduhake ing satus-satusan derajat). |
| `4332` | `AI_4332_MIN_TEMP_F` | Suhu minimum ing Fahrenheit (dituduhake ing satus-satusan derajat). |
| `4333` | `AI_4333_MIN_TEMP_C` | Suhu minimum ing Celsius (dituduhake ing satus-satusan derajat). |

### Atribut produk lan katelusuran

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Nomer Stok NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Klasifikasi bangke lan potongan daging UN/ECE. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Potensi aktif. |
| `7005` | `AI_7005_CATCH_AREA` | Area tangkapan. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Spesies kanggo kabutuhan perikanan. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Jinis piranti mancing. |
| `7010` | `AI_7010_PROD_METHOD` | Metode produksi. |
| `7020` | `AI_7020_REFURB_LOT` | ID lot rekondisi. |
| `7021` | `AI_7021_FUNC_STAT` | Status fungsional. |
| `7022` | `AI_7022_REV_STAT` | Status revisi. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Pengenal Aset Individu Global (GIAI) saka rakitan. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Nomer pangolah kanthi kode negara ISO telung angka (10 slot). |
| `7040` | `AI_7040_UIC_EXT` | UIC GS1 kanthi Ekstensi 1 lan indeks Importir. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Jinis unit kargo UN/CEFACT. |

### Nomer Penggantian Kesehatan Nasional (NHRN)

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nomer Penggantian Kesehatan Nasional (NHRN) - Jerman PZN. |
| `711` | `AI_711_NHRN_CIP` | Nomer Penggantian Kesehatan Nasional (NHRN) - Prancis CIP. |
| `712` | `AI_712_NHRN_CN` | Nomer Penggantian Kesehatan Nasional (NHRN) - Spanyol CN. |
| `713` | `AI_713_NHRN_DRN` | Nomer Penggantian Kesehatan Nasional (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Nomer Penggantian Kesehatan Nasional (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Nomer Penggantian Kesehatan Nasional (NHRN) - Amerika Serikat NDC. |
| `716` | `AI_716_NHRN_AIC` | Nomer Penggantian Kesehatan Nasional (NHRN) - Italia AIC. |
| `717` | `AI_717_NHRN_SRN` | Nomer Penggantian Kesehatan Nasional (NHRN) - Kosta Rika Nomer Registrasi Sanitasi. |

### Kesehatan, GMN, HIDRI, CPID, data pawongan

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Acuan Sertifikasi (10 slot). |
| `7240` | `AI_7240_PROTOCOL` | ID Protokol. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Tipe media AIDC. |
| `7242` | `AI_7242_VCN` | Nomer Kontrol Versi (VCN). |
| `7250` | `AI_7250_DOB` | Tanggal lair (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Tanggal lan wektu lair (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Jenis kelamin biologis. |
| `7253` | `AI_7253_FAMILY_NAME` | Jeneng kulawarga wong. |
| `7254` | `AI_7254_GIVEN_NAME` | Jeneng paringan wong. |
| `7255` | `AI_7255_SUFFIX` | Akhiran jeneng wong. |
| `7256` | `AI_7256_FULL_NAME` | Jeneng lengkap wong. |
| `7257` | `AI_7257_PERSON_ADDR` | Alamat wong. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Urutan lair bayi. |
| `7259` | `AI_7259_BABY` | Bayi saka jeneng kulawarga. |
| `8001` | `AI_8001_DIMENSIONS` | Produk gulungan (ambane, dawa, diameter inti, arah, sambungan). |
| `8002` | `AI_8002_CMT_NO` | Identifier telpon seluler. |
| `8003` | `AI_8003_GRAI` | Pengenal Aset Bisa Dibalèkaké Global (GRAI). |
| `8004` | `AI_8004_GIAI` | Pengenal Aset Individu Global (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Rega saben unit ukuran. |
| `8006` | `AI_8006_ITIP` | Identifikasi cuwilan item dagang individu (ITIP). |
| `8007` | `AI_8007_IBAN` | Nomer Akun Bank Internasional (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Tanggal lan wektu produksi (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indikator Sensor Sing Bisa Diwaca Optik. |
| `8010` | `AI_8010_CPID` | Pengenal Komponen/Bagian (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Nomer seri Pengenal Komponen/Bagian (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Versi software. |
| `8013` | `AI_8013_GMN` | Nomer Model Global (GMN). |
| `8014` | `AI_8014_MUDI` | Pengenal Registrasi Piranti Individual Dhuwur (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Nomer Hubungan Layanan Global (GSRN) kanggo ngenali hubungan antarane organisasi sing nawakake layanan lan panyedhiya layanan. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Nomer Hubungan Layanan Global (GSRN) kanggo ngenali hubungan antarane organisasi sing nawakake layanan lan panampa layanan. |
| `8019` | `AI_8019_SRIN` | Nomer Instansi Hubungan Layanan (SRIN). |
| `8020` | `AI_8020_REF_NO` | Nomer referensi slip pembayaran. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identifikasi cuwilan item dagang (ITIP) sing kamot ing unit logistik. |
| `8030` | `AI_8030_DIGSIG` | Tandha Tangan Digital (DigSig). |
| `8040` | `AI_8040_IMEI` | Identitas Peralatan Seluler Internasional (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identitas Peralatan Seluler Internasional 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nomer eSIM (SIM tertanam). |
| `8043` | `AI_8043_PSIM` | Nomer SIM fisik. |
| `8110` | `AI_8110` | Identifikasi kode kupon kanggo digunakake ing Amerika Utara. |
| `8111` | `AI_8111_POINTS` | Poin loyalitas kupon. |
| `8112` | `AI_8112` | Identifikasi kode kupon file tawaran positif kanggo digunakake ing Amerika Utara. |
| `8200` | `AI_8200_PRODUCT_URL` | URL Kemasan Ekstensi. |

### Panganggo internal / perusahaan

| AI | Konstanta | Katrangan |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informasi sing wis disepakati bebarengan antarane mitra dagang. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Informasi internal perusahaan (9 slot). |

---

## Lampiran B — Konstanta Kunci Interpretasi

Yen `GaiaParser.parse()` diceluk nganggo `ParseMode.INTERPRETATION`, saben `GS1AIObjectElement` bisa nggawa dhaptar objek `GS1AIInterpretation` sing digawe dening pangsugih khusus bidang. Nganggoa konstanta saka `GS1Constants_Enricher` (ing paket `tools.pantheum.gaia.gs1.constants`) minangka kunci kanggo nggoleki nilai interpretasi tinamtu:

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

Label tampilan iku **dudu** konstanta — kabeh mau manggon ing katalog sing dilokalake ing sangisore `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, kanthi kunci konstanta tipe. `GS1AIInterpretation.getLabel()` mbalekake label kanggo basa parsing (deleng [Pesen lan label sing dilokalake](#pesen-lan-label-sing-dilokalake)), lan bali menyang Inggris yen sawijining katalog ora ngemot kuncine. Kolom "Label tampilan" ing ngisor iki ndhaptar teks basa Jawa; kunci tipe dhewe ora owah ing kabeh basa, mula jodhokna ing kunci, aja pisan-pisan ing label.

### Tanggal lan wektu

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `DATE_VALUE` | Tanggal | AI tanggal (11–17, 7003, 7006, 7011, lsp.) |
| `DATE_FORMAT` | Format tanggal | AI tanggal |
| `TIME_VALUE` | Waktu | AI sing nggawa wektu (7003, 7011, 8008, lsp.) |
| `TIME_FORMAT` | Format waktu | AI sing nggawa wektu |
| `DATETIME_VALUE` | Tanggal lan wektu | AI tanggal lan wektu |
| `DATETIME_FORMAT` | Format tanggal lan wektu | AI tanggal lan wektu |

### Tanggal panen

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Tanggal mulai panen | AI 7007 |
| `HARVEST_END_DATE` | Tanggal akhir panen | AI 7007 (pungkasaning rentang sing opsional) |
| `HARVEST_DATE_RANGE` | Rentang tanggal panen | AI 7007 |

### Prefiks Perusahaan GS1

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefiks perusahaan GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Kode anggota GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organisasi anggota GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Tipe GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Tingkat kemasan | AI 01 |
| `GTIN_CHECK_DIGIT` | Angka centhang | AI 01, 02 |

### SSCC

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Angka ekstensi | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Referensi seri | AI 00 |
| `SSCC_CHECK_DIGIT` | Angka centhang | AI 00 |

### Negara (ISO 3166)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Kode negara (numerik) | AI negara tunggal (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Kode negara (alfa-2) | AI negara Alpha-2 |
| `COUNTRY_NAME` | Jeneng negara | AI negara tunggal |
| `COUNTRY_LIST` | Negara | AI 423 — kabeh jeneng disambung, tuladha `Australia, New Zealand` |

AI 423 (negara pangolahan sepisanan) bisa nggawa nganti limang negara, mula iku ngetokake
**siji pasangan kanthi nomer saben negara** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — banjur diterusake siji ringkesan
`COUNTRY_LIST`. Gaweya kunci-kunci iki saka konstanta `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` ditambah indeks sing wiwit saka 1, utawa cukup ubengana
`getInterpretations()`; kunci `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` tanpa akhiran **ora**
dietokake kanggo AI 423.

### Mata uang (ISO 4217)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Kode mata uang | AI jumlah kanthi mata uang (391n, 393n) |
| `CURRENCY_ALPHA` | Kode alfabet mata uang | AI jumlah kanthi mata uang |
| `CURRENCY_NAME` | Jeneng mata uang | AI jumlah kanthi mata uang |

### Suhu

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `TEMPERATURE` | Suhu | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Satuan suhu | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Suhu (terformat) | AI 4330–4333 |

### Jinis kelamin (ISO 5218)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `SEX_CODE` | Kode jenis kelamin | AI 7252 |
| `SEX_DESCRIPTION` | Deskripsi jenis kelamin | AI 7252 |

### Spesies banyu (FAO)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Kode spesies | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nama ilmiah | AI 7008 |
| `SPECIES_ENGLISH` | Nama umum | AI 7008 |
| `SPECIES_FAMILY` | Famili | AI 7008 |
| `SPECIES_ORDER` | Ordo | AI 7008 |

### Nomer Stok NATO (NSN)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `NSN_FSG` | Grup pasokan | AI 7001 |
| `NSN_FSG_NAME` | Jeneng grup pasokan | AI 7001 |
| `NSN_FSCG` | Kelas pasokan | AI 7001 |
| `NSN_FSCG_NAME` | Jeneng kelas pasokan | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Kode negara | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Negara | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Kode negara ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategori NCS | AI 7001 |
| `NSN_NIIN` | Nomor barang nasional | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Produk gulungan

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Lebar gulungan (mm) | AI 8001 |
| `ROLL_LENGTH` | Panjang gulungan (m) | AI 8001 |
| `CORE_DIAMETER` | Diameter inti (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Kode arah gulungan | AI 8001 |
| `WINDING_DIRECTION` | Arah gulungan | AI 8001 |
| `SPLICES` | Sambungan | AI 8001 |

### IBAN

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Kode negara | AI 8007 |
| `IBAN_COUNTRY_NAME` | Negara | AI 8007 |
| `IBAN_CHECK_DIGITS` | Angka centhang | AI 8007 |
| `IBAN_CHECK_VALID` | Centhang | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Nomor seri | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Angka centhang | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Badan penerbit | AI 8040, 8041 |

15 angka mau kapilah dadi `[ TAC (8) ][ serial (6) ][ angka centhang Luhn (1) ]`, dene
RBI iku 2 angka ngarep saka TAC — mula `IMEI_RBI` iku prefikse `IMEI_TAC`, dudu rentang
sing kapisah. `IMEI_FORMATTED` nampilake pangelompokan tampilan GSMA sing lumrah
`AA-BBBBBB-CCCCCC-D` (tuladha, `49-015420-323751-8`), sing motong TAC ing watese RBI;
pangelompokan lawas `6-2-6-1`, sing motong ing panggonan wiwitane Final Assembly Code sing
wis ora dienggo, ora dietokake.

`IMEI_RBI_NAME` ngrampungake RBI dadi jenenge badan sing menehi liwat `ImeiRbiData`, lan
**mung ditambahake ing pungkasan sarta mung yen kodene kadhaptar ing kono**. Tabel kasebut
nyakup telung kelompok:

- **Sing saiki isih menehi** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, ditambah `99`
  Global Hexadecimal Administrator lan `98` (disawiskake).
- **Rentang uji coba** — `00` lan `02`–`09`, sing nandhani IMEI uji coba dudu wenehan sanyatane.
  Takonana nganggo `ImeiRbiData.isTestCode(code)`.
- **Sing wis ora menehi maneh** — badan-badan lawas kayata `49` (BZT/BAPT, Jerman), `44`
  (BABT, Inggris) utawa `91` (MSAI, India). Takonana nganggo `ImeiRbiData.isNoLongerAllocating(code)`.
  Piranti sing nggawa kode iki lumrah wae lan isih dienggo; mung wenehan anyar sing mandheg,
  mula iki informasi laporan, ora pisan-pisan tandha kasahan.

Ora anane `IMEI_RBI_NAME` tegese "RBI iki ora ana ing tabel dhewe", **dudu** "IMEI ora sah":
tabele disusun saka dhaptar RBI sing wis diterbitake, dudu langsung saka GSMA, mula bisa
ketinggalan saka badan sing lagi wae ditunjuk. Aja narik kesimpulan validasi apa wae saka ora
anane; RBI iku dudu aksara centhang. Kode sing ngubengi dhaptar interpretasi uga kudu bisa
nampa yen iku ora ana, tinimbang gumantung marang posisi.

### Panuduh SIM (EID / ICCID)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategori industri | AI 8042 |
| `EID_BODY` | Badan EID | AI 8042 |
| `EID_CHECK_DIGIT` | Angka centhang | AI 8042 |
| `ICCID_BODY` | Badan ICCID | AI 8043 |
| `ICCID_EXTENSION` | Ekstensi | AI 8043 |

`SIM_MII` nggawa **rong** angka ngarep (`89`), pasangan sing diwenehake ITU-T E.118 kanggo
telekomunikasi. ISO/IEC 7812 dhewe netepake MII minangka **angka sepisanan thok**, mula
`SIM_MII_NAME` ngrampungake kategorine saka `8` ngarep kasebut liwat `Iso7812Data` — sing
ngasilake "Healthcare, telecommunications and other future industry assignments". Kanggo EID
sing kabentuk bener, mula, iki tetep ora owah; iku dilapurake supaya bisa dilacak balik
menyang standare, dudu minangka pambeda. `Iso7812Data.nameForCode(digit)` nampa siji angka
lugu, dene `nameForIdentifier(prefix)` nampa prefiks sing luwih dawa banjur maca angka
ngarepe.

`SIM_MII_NAME` mung dietokake dening `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
nampilake `SIM_MII` tanpa kategorine.

### Acuan sertifikasi

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Nomer urutan | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Kode skema sertifikasi | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Skema sertifikasi | AI 7230–7239 |
| `CERT_REFERENCE` | Referensi sertifikasi | AI 7230–7239 |

### GS1 UIC

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `UIC_CODE` | Kode UIC | AI 7040 |
| `UIC_EXTENSION_1` | Ekstensi 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Indeks importir | AI 7040 |

### Urutan lair bayi

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posisi lair | AI 7258 |
| `BIRTH_TOTAL` | Total lair | AI 7258 |
| `BIRTH_SEQUENCE` | Urutan lair | AI 7258 |

### Nomer Model Global (GMN)

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Referensi model | AI 8013 |
| `GMN_CHECK_PAIR` | Pasangan centhang | AI 8013 |

### HIDRI

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Referensi perangkat | AI 8014 |
| `HIDRI_CHECK_PAIR` | Pasangan centhang | AI 8014 |

### CPID

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Referensi komponen & bagian | AI 8010–8011 |

### Nilai desimal lan ukuran

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Nilai desimal | AI angka kanthi panggonan desimal sing dimaksud (31xx–36xx) |
| `DECIMAL_AMOUNT` | Jumlah | AI rega (390n–395n) |
| `DECIMAL_PERCENTAGE` | Persen | AI 394n |
| `DECIMAL_PLACES` | Panggonan desimal | Bebarengan karo `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Format persentase | AI 394n |
| `ISO_UNIT_CODE` | Kode satuan ISO | AI ukuran |
| `ISO_UNIT_NAME` | Nama satuan ISO | AI ukuran |
| `MONETARY_AMOUNT` | Jumlah moneter | AI rega |
| `MONETARY_AMOUNT_DISPLAY` | Jumlah moneter (terformat) | AI rega |

### Koordinat geo

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `LATITUDE` | Lintang | AI 4309 |
| `LONGITUDE` | Bujur | AI 4309 |
| `GEO_COORDINATES` | Koordinat geografis | AI 4309 |
| `LATITUDE_DMS` | Lintang (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Bujur (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Koordinat geografis (DMS) | AI 4309 |

### Cara produksi

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Kode metode produksi | AI 7010 |
| `PRODUCTION_METHOD` | Metode produksi | AI 7010 |

### Tipe media AIDC

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Kode tipe media AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Tipe media AIDC | AI 7241 |

### Cuwilan saka gunggung

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Nomor keping | AI 8006 |
| `PIECE_TOTAL` | Total keping | AI 8006 |
| `PIECE_OF_TOTAL` | Keping dari total | AI 8006 |

### Panyigaran komponen

Kunci sing dietokake dening panyigaran komponen deklaratif ing `content/ai-content.json`, dudu
dening pangsugih Java — kabeh mau nampilake bagean-bagean sing dijenengi saka sawijining nilai
AI gabungan. Beda karo saben kunci liyane ing lampiran iki, kabeh mau **ora duwe konstanta ing
`GS1Constants_Enricher`**: jodhokna string-e apa anane, utawa wacanen tipene saka
`GS1AIInterpretation.getType()`.

| Kunci tipe | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Angka centhang | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Nomor seri | AI 253, 255, 8003 |
| `POSTAL_CODE` | Kode pos | AI 421 |
| `PROCESSOR_ID` | Pengenal pemroses | AI 7030–7039 |

Elinga yen `CHECK_DIGIT` ing kene iku kunci panyigaran komponen sing umum, beda karo kunci
`GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` lan `EID_CHECK_DIGIT` sing khusus
kanggo pangsugih lan wis didhaptar ing ndhuwur.

### Liya-liyane

| Konstanta kunci | Label tampilan | Digawe dening |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Nilai | AI boolean / gendera (4321–4323) |
| `DECODED_TEXT` | Teks sing didekode | AI teks bebas |
