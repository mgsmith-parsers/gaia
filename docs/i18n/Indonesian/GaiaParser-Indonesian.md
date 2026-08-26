# GAIA (GS1 Application Identifiers Analyser) — Panduan Pengembang

## Daftar Isi

1. [Ikhtisar](#ikhtisar)
2. [Tentang GS1 dan General Specifications](#tentang-gs1-dan-general-specifications)
3. [Pengidentifikasi Aplikasi GS1](#pengidentifikasi-aplikasi-gs1)
4. [Panduan Cepat](#panduan-cepat)
5. [Alur Penguraian](#alur-penguraian)
   - [Pra-tahap — Pemodifikasi Masukan](#pra-tahap--pemodifikasi-masukan)
   - [Tahap 0 — ID Korelasi](#tahap-0--id-korelasi)
   - [Tahap 1 — Pengarahan Masukan](#tahap-1--pengarahan-masukan)
   - [Tahap 2 — Sintaks](#tahap-2--sintaks)
   - [Tahap 3 — Konten](#tahap-3--konten)
   - [Tahap 4 — Interpretasi](#tahap-4--interpretasi)
6. [Konfigurasi Penguraian (`ParseConfig`)](#konfigurasi-penguraian-parseconfig)
   - [Opsi](#opsi)
   - [Pesan dan label terlokalkan](#pesan-dan-label-terlokalkan)
   - [Pemformatan tanggal](#pemformatan-tanggal)
7. [Pemodifikasi Masukan](#pemodifikasi-masukan)
   - [Pemodifikasi bawaan](#pemodifikasi-bawaan)
   - [Menulis pemodifikasi](#menulis-pemodifikasi)
   - [Mendaftarkan pemodifikasi](#mendaftarkan-pemodifikasi)
   - [Memeriksa apa yang dilakukan pemodifikasi](#memeriksa-apa-yang-dilakukan-pemodifikasi)
   - [Penanganan kegagalan pemodifikasi](#penanganan-kegagalan-pemodifikasi)
8. [Mode Penguraian](#mode-penguraian)
   - [Mode DATA_CARRIER](#mode-data_carrier)
   - [Mode SYNTAX](#mode-syntax)
   - [Mode CONTENT](#mode-content)
   - [Mode INTERPRETATION (bawaan)](#mode-interpretation-bawaan)
9. [ID Korelasi](#id-korelasi)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Bekerja dengan Hasil](#bekerja-dengan-hasil)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry dan DataCarrierType](#datacarrierentry-dan-datacarriertype)
12. [Rujukan Galat](#rujukan-galat)
13. [Keamanan Thread](#keamanan-thread)
14. [Lampiran A — Konstanta String AI](#lampiran-a--konstanta-string-ai)
    - [Identifikasi dan Serialisasi](#identifikasi-dan-serialisasi)
    - [Tanggal dan Waktu](#tanggal-dan-waktu)
    - [Kuantitas dan Ukuran — Ukuran Variabel (Metrik)](#kuantitas-dan-ukuran--ukuran-variabel-metrik)
    - [Kuantitas dan Ukuran — Ukuran Variabel (Imperial / AS)](#kuantitas-dan-ukuran--ukuran-variabel-imperial--as)
    - [Penetapan Harga dan Jumlah Moneter](#penetapan-harga-dan-jumlah-moneter)
    - [Lokasi dan Pengiriman](#lokasi-dan-pengiriman)
    - [Atribut Produk dan Ketertelusuran](#atribut-produk-dan-ketertelusuran)
    - [Nomor Penggantian Biaya Kesehatan Nasional (NHRN)](#nomor-penggantian-biaya-kesehatan-nasional-nhrn)
    - [Layanan Kesehatan, GMN, HIDRI, CPID, dan Data Orang](#layanan-kesehatan-gmn-hidri-cpid-dan-data-orang)
    - [Penggunaan Internal / Perusahaan](#penggunaan-internal--perusahaan)
15. [Lampiran B — Konstanta Kunci Interpretasi](#lampiran-b--konstanta-kunci-interpretasi)
    - [Tanggal dan Waktu](#tanggal-dan-waktu)
    - [Tanggal Panen](#tanggal-panen)
    - [Prefiks Perusahaan GS1](#prefiks-perusahaan-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Negara (ISO 3166)](#negara-iso-3166)
    - [Mata Uang (ISO 4217)](#mata-uang-iso-4217)
    - [Suhu](#suhu)
    - [Jenis Kelamin (ISO 5218)](#jenis-kelamin-iso-5218)
    - [Spesies Akuatik (FAO)](#spesies-akuatik-fao)
    - [Nomor Stok NATO (NSN)](#nomor-stok-nato-nsn)
    - [Produk Gulungan](#produk-gulungan)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Pengenal SIM (EID / ICCID)](#pengenal-sim-eid--iccid)
    - [Rujukan Sertifikasi](#rujukan-sertifikasi)
    - [GS1 UIC](#gs1-uic)
    - [Urutan Kelahiran Bayi](#urutan-kelahiran-bayi)
    - [Nomor Model Global (GMN)](#nomor-model-global-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Nilai Desimal dan Ukuran](#nilai-desimal-dan-ukuran)
    - [Koordinat Geografis](#koordinat-geografis)
    - [Metode Produksi](#metode-produksi)
    - [Tipe Media AIDC](#tipe-media-aidc)
    - [Bagian dari Total](#bagian-dari-total)
    - [Pemisahan Komponen](#pemisahan-komponen)
    - [Lain-lain](#lain-lain)

---

## Ikhtisar

`GaiaParser` adalah titik masuk untuk mengurai string elemen Pengidentifikasi Aplikasi (AI) GS1. Ia menerima keluaran mentah pemindai dalam bentuk apa pun berikut dan mengembalikan `ParseResult` terstruktur berisi semua AI yang berhasil diurai, galat validasi, dan secara opsional interpretasi yang mudah dibaca manusia:

- String elemen AI sederhana: `0109506000134352`
- String elemen berawalan Pengidentifikasi Simbologi AIM: `]C10109506000134352`
- URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Salah satu bentuk di atas, secara opsional diawali ID korelasi 8 digit: `12345678~0109506000134352`

**Kelas titik masuk:** `tools.pantheum.gaia.GaiaParser`

> **Baru mengenal Gaia?** Mulailah dari **[Panduan Cepat GaiaParser](GaiaParser-QuickStart-Indonesian.md)** — sepuluh menit yang membawa Anda melewati dependensi, penguraian pertama, dan beberapa jebakan yang paling sering menjegal. Panduan ini adalah rujukan lengkapnya.

> Arah sebaliknya — *membangun* string elemen yang sahih dan URI Digital Link dari pasangan AI/nilai — dibahas di **[GaiaBuilder — Panduan Pengembang](GaiaBuilder-Indonesian.md)**.

---

## Tentang GS1 dan General Specifications

**GS1** adalah organisasi nirlaba global yang mengembangkan dan memelihara standar terbuka untuk identifikasi rantai pasok dan pertukaran data. Standarnya dipakai di ritel, layanan kesehatan, logistik, jasa boga, dan banyak industri lain, mencakup segala hal mulai dari barcode produk pada kemasan konsumen hingga pelacakan berseri dosis farmasi.

Rujukan otoritatif untuk segala yang diimplementasikan pengurai ini adalah **GS1 General Specifications** — satu dokumen yang mendefinisikan:

- Seluruh kode Pengidentifikasi Aplikasi (AI), judul datanya, formatnya, dan aturan validasinya
- Aturan sintaks untuk menyusun dan menyandikan string elemen AI
- Persyaratan simbologi barcode dan penetapan Pengidentifikasi Simbologi AIM
- Algoritma digit pemeriksa dan karakter pemeriksa
- Penafsiran tahun dua digit (aturan jendela geser)
- Spesifikasi Data Matrix, QR Code, GS1-128, GS1 DataBar, dan pembawa data lainnya

GS1 General Specifications diperbarui setiap tahun. Edisi terkini dan sumber daya pendukungnya tersedia di:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA mengimplementasikan **Release 26.0 (Disahkan, Jan 2026)** dari GS1 General Specifications.

URI GS1 Digital Link diatur oleh standar pendamping, **GS1 Digital Link: URI Syntax**, yang mendefinisikan kunci identifikasi utama, urutan kualifikasi kunci, dan penyandian atribut data yang diterapkan pengurai pada masukan Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA mengimplementasikan **Release 1.7.0 (Disahkan, Agu 2026)** dari standar GS1 Digital Link: URI Syntax.

Rujukan bagian di sepanjang dokumen ini mengacu pada GS1 General Specifications (mis. "Table 7-5", "section 7.12"), kecuali nomor bagian Digital Link (mis. "§4.9", "§4.12") yang mengacu pada standar GS1 Digital Link: URI Syntax.

---

## Pengidentifikasi Aplikasi GS1

**Pengidentifikasi Aplikasi (AI) GS1** adalah awalan numerik pendek — dua sampai empat digit — yang menentukan makna dan format data yang mengikutinya secara langsung. AI didefinisikan dalam GS1 General Specifications dan mencakup rentang luas data rantai pasok: pengidentifikasi produk, tanggal, kuantitas, nomor lot, nomor seri, hasil pengukuran, URL, dan lain-lain.

### Struktur sebuah elemen AI

Setiap elemen AI terdiri atas dua bagian:

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

Kode AI selalu numerik. Nilai data mengikutinya langsung, tanpa pembatas apa pun di antara kode dan nilai.

### AI panjang tetap vs panjang variabel

AI terbagi dalam dua kategori:

| Tipe | Perilaku | Contoh |
|---|---|---|
| **Panjang tetap** | Jumlah karakter yang persis, selalu diambil penuh | AI `01` (GTIN) — selalu 14 digit |
| **Panjang variabel** | 1 hingga jumlah karakter maksimum; diakhiri pemisah GS atau akhir masukan | AI `10` (Batch/Lot) — 1 sampai 20 karakter alfanumerik |

Tetap atau variabelnya sebuah AI ditentukan semata-mata oleh definisinya dalam spesifikasi GS1 — pengurai tidak pernah menebak.

### String elemen multi-AI

Beberapa AI dapat dirangkai menjadi satu string elemen. AI panjang tetap dapat dirangkai langsung karena pengurai selalu tahu persis berapa karakter yang harus diambil. AI panjang variabel harus diakhiri dengan **karakter GS** (ASCII `0x1D`, dikenal juga sebagai FNC1 dalam simbologi barcode) setiap kali ada AI lain yang mengikutinya, agar pengurai tahu di mana satu nilai berakhir dan kode AI berikutnya dimulai.

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

Dalam literal string Java, tulis karakter GS dengan escape Unicode `""`.

### AI yang umum dipakai

| AI | Judul data | Format | Contoh nilai |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, area moneter tunggal) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Digit keempat** pada AI ukuran atau harga 4 digit menyandikan jumlah tempat desimal tersirat — `3103` adalah berat bersih dalam kg dengan 3 desimal (`001500` = 1,500 kg), sedangkan `3102` akan membaca digit yang sama sebagai 15,00 kg. Kolom `Format` di atas menunjukkan format *data*; `getFormatString()` lengkap tiap AI menyertakan AI itu sendiri (mis. `N4+N6` untuk `3103`).

### Interpretasi yang Dapat Dibaca Manusia (HRI)

Bentuk konvensional yang mudah dibaca manusia membungkus setiap kode AI dalam tanda kurung tepat sebelum nilainya, dengan spasi di antara elemen:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Pemisah GS tidak ditampilkan dalam HRI. Format ini dihasilkan oleh `GS1AIObject.toHriString()`.

### Kode AI empat digit

Sebagian AI memakai empat digit, bukan dua. Dua digit pertama menandai keluarga AI; digit ketiga dan/atau keempat membawa makna tambahan (seperti posisi titik desimal tersirat pada AI pengukuran). Pengurai menentukan kode AI lengkap dari string elemen secara otomatis — pemanggil selalu bekerja dengan kode lengkap (mis. `"3102"`, bukan sekadar `"31"`).

---

## Panduan Cepat

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

> **Pemisah GS:** AI panjang variabel di dalam string multi-AI harus dipisahkan oleh karakter GS (ASCII `0x1D`). Gunakan `""` dalam literal string Java.

---

## Alur Penguraian

### Pra-tahap — Pemodifikasi Masukan

Jika `ParseConfig` membawa **pemodifikasi masukan**, semuanya berjalan sebelum apa pun yang lain — sebelum ID korelasi dilepas, sebelum pembawa data dideteksi, sebelum alur GS1 dimasuki. Setiap pemodifikasi menulis ulang masukan mentah untuk pemodifikasi berikutnya, dan seluruh tahap di bawah bekerja pada keluaran rantai tersebut.

Tidak ada pemodifikasi yang aktif secara bawaan, jadi pra-tahap ini tidak melakukan apa pun kecuali Anda mengaktifkannya. Lihat [Pemodifikasi Masukan](#pemodifikasi-masukan).

---

### Tahap 0 — ID Korelasi

Sebelum pemrosesan GS1 apa pun, `GaiaParser` memeriksa apakah masukan diawali **awalan ID korelasi** opsional: tepat 8 digit desimal ASCII diikuti tanda tilde (`~`), mis. `12345678~`.

Jika awalan itu ada, ia dilepas dan disimpan sebagai `CorrelationInfo` pada `ParseResult` yang dikembalikan. Semua tahap berikutnya bekerja pada muatan yang sudah dilepas awalannya. Jika tidak ada awalan, masukan diteruskan apa adanya.

Lihat [ID Korelasi](#id-korelasi) untuk rinciannya.

---

### Tahap 1 — Pengarahan Masukan

Setelah ID korelasi dilepas, `GaiaParser` memeriksa apakah masukan (yang sudah dilepas awalannya) dimulai dengan **Pengidentifikasi Simbologi AIM**: awalan tiga karakter berbentuk `]` + huruf ASCII + digit ASCII (mis. `]C1` untuk GS1-128, `]d2` untuk GS1 DataMatrix, `]e0` untuk GS1 DataBar / GS1 Composite).

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

Jika pembawa data tidak mampu membawa AI GS1 (mis. barcode pos), penguraian langsung berhenti dengan galat `GE-D002`.

---

### Tahap 2 — Sintaks

Selalu dijalankan. Terdiri atas dua sub-langkah:

**2a. Tokenisasi (`AISyntaxParser`)**
- Membaca panjang kode AI dari dua karakter pertama memakai tabel awalan GS1 (GS1 General Specifications Table 7-5).
- AI panjang tetap mengambil jumlah bita yang persis dari masukan.
- AI panjang variabel dibaca sampai karakter GS atau akhir masukan.
- AI multi-komponen dipotong gumpalan nilainya menjadi segmen per komponen.

**2b. Validasi struktural (`SyntaxValidator`)**
- Memeriksa AI ganda (`GE-S004`).
- Memeriksa dependensi AI yang diwajibkan, mis. AI `02` mensyaratkan AI `37` (`GE-S005`).
- Memeriksa pasangan AI yang saling meniadakan (`GE-S006`).

Galat pada tahap ini bertingkat `SYNTAX_ERROR` (tokeniser) atau `INTEGRITY_ERROR` (struktural). Jika ada **satu saja** galat — tokeniser maupun struktural — alur berhenti dan tahap konten serta interpretasi dilewati.

---

### Tahap 3 — Konten

Hanya berjalan bila Tahap 2 tidak menghasilkan galat (baik tokeniser maupun struktural). Alur per elemen (setiap langkah hanya berjalan bila langkah sebelumnya tidak menghasilkan galat):

| Langkah | Validator | Kode Galat |
|---|---|---|
| Pemeriksaan regex | `RegexValidator` | `GE-C001` |
| Himpunan karakter + format komponen | `ComponentValidator` | `GE-C005` + kode format per kondisi (`GE-C054`–`GE-C115`) |
| Digit pemeriksa / karakter pemeriksa | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validasi semantik khusus | `ContentValidatorRegistry` | kode konten per kondisi (`GE-C116`–`GE-C170`) |

Galat pada tahap ini bertingkat `FORMAT_ERROR` atau `DATA_ERROR`, dengan satu pengecualian: pemeriksaan
prefiks perusahaan GS1 pada AI berkunci GS1 bersifat imbauan dan bertingkat `WARNING` (lihat
[Rujukan Galat](#rujukan-galat)), sehingga prefiks perusahaan yang tak dikenali dengan sendirinya
tidak membuat hasil menjadi tidak sahih.

---

### Tahap 4 — Interpretasi

Hanya berjalan dalam mode `INTERPRETATION` dan hanya bila tidak ada elemen yang membawa galat dari tahap mana pun sebelumnya. `InterpretationEngine` memperkaya setiap elemen dengan metadata berlabel:

- Tanggal yang diformat ulang menjadi `dd/mm/yyyy`
- Penguraian digit pemeriksa GTIN dan pencarian prefiks perusahaan GS1
- Nama negara ISO 3166
- Nama dan simbol mata uang ISO 4217
- Jumlah desimal hasil pendekodean
- Fragmen HRI (Interpretasi yang Dapat Dibaca Manusia)

Hasilnya dilampirkan sebagai entri `GS1AIInterpretation` pada setiap `GS1AIObjectElement`.

---

## Konfigurasi Penguraian (`ParseConfig`)

`GaiaParser` menyediakan tepat dua titik masuk:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` berjalan dengan **konfigurasi bawaan**: mode `INTERPRETATION`, tanggal little-endian (`dd/mm/yyyy`) dengan pemisah `/` dan tahun empat digit, serta pesan galat **bahasa Inggris**. Untuk mengubah salah satunya — termasuk mode penguraian — bangunlah `ParseConfig` dengan pembangun berantainya dan pakai kelebihan-muatan berargumen dua.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Semua enum opsi berada di `GaiaConstants`.

### Opsi

| Metode pembangun | Enum (`GaiaConstants`) | Bawaan | Efek |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Kedalaman alur — lihat [Mode Penguraian](#mode-penguraian). |
| `language(...)`      | `Language`      | `ENGLISH`        | Bahasa pesan galat, label interpretasi, **dan** deskripsi AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Urutan komponen tanggal: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Karakter antar komponen tanggal: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) atau `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) atau `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Melewati pemeriksaan struktural "mensyaratkan" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Melewati pemeriksaan struktural "meniadakan" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nama kelas | tidak ada | Kode yang menulis ulang masukan mentah sebelum penguraian — dua [pemodifikasi bawaan](#pemodifikasi-bawaan) ditambah apa pun yang Anda tulis. Lihat [Pemodifikasi Masukan](#pemodifikasi-masukan). |

Keempat opsi tanggal hanya memengaruhi string tanggal terformat yang dihasilkan pemerkaya interpretasi (dalam mode `INTERPRETATION`); keduanya tidak mengubah validasi. Nilai pembangun boleh dihilangkan — opsi yang tidak diisi (atau diberi `null`) tetap memakai nilai bawaannya.

### Pesan dan label terlokalkan

`language(...)` memilih bahasa untuk **tiga** jenis teks yang dibaca manusia: pesan galat, label interpretasi (`getLabel()` pada setiap `GS1AIInterpretation`), dan deskripsi AI (`getDescription()` pada setiap `GS1AIObjectElement`).

**35 bahasa** didefinisikan oleh `GaiaConstants.Language`, mencakup bahasa-bahasa yang paling banyak dituturkan di dunia: Inggris, Prancis, Spanyol, Jerman, Italia, Portugis, Belanda, Polandia, Rusia, Ukraina, Ceko, Swedia, Mandarin, Jepang, Korea, Arab, Indonesia, Hindi, Turki, Bengali, Urdu, Vietnam, Pidgin Nigeria, Arab Mesir, Marathi, Telugu, Tamil, Kanton, Wu, Tagalog, Persia, Hausa, Punjabi, Jawa, dan Swahili.

Keadaan terjemahan (sebagaimana dirilis):
- **Label interpretasi** — diterjemahkan untuk semua bahasa.
- **Pesan galat** — diterjemahkan untuk semua bahasa.
- **Deskripsi AI** — diterjemahkan untuk semua bahasa kecuali Inggris. Inggris bukan katalog terpisah: ia dibaca langsung dari medan `description` pada entri AI di `gs1-application-identifiers.jsonld`, yang menjadi cadangan terakhir setiap deskripsi AI.

Pidgin Nigeria (`NIGERIAN_PIDGIN`), sebuah kreol berbasis Inggris, secara sengaja memakai ulang teks Inggris untuk label interpretasi dan pesan galat. Deskripsi AI adalah pengecualian dari pengecualian itu: ia diterjemahkan ke ungkapan Pidgin yang sesungguhnya alih-alih memakai ulang teks Inggris, sebab katalog deskripsi AI dibuat terpisah dari katalog label/pesan. Terjemahan mesin sebaiknya ditinjau penutur asli sebelum diandalkan di lingkungan produksi.

Pesan, label, atau deskripsi apa pun yang tidak ada dalam katalog suatu bahasa akan jatuh kembali ke Inggris. Bahasa yang ditulis dari kanan ke kiri (Arab, Urdu, Arab Mesir, Persia) tersimpan dengan benar sebagai string; menampilkannya dari kanan ke kiri adalah tanggung jawab lapisan tampilan.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Label interpretasi terlokalkan dengan cara yang sama (nilainya tidak berubah — hanya labelnya):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Deskripsi AI terlokalkan dengan cara yang sama (hanya `getTitle()`, mis. `"GTIN"`, yang tidak dilokalkan):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Pemformatan tanggal

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Pemodifikasi Masukan

**Pemodifikasi masukan** adalah kode yang menulis ulang string masukan mentah sebelum Gaia menguraikannya. Pemodifikasi ada untuk masukan yang tiba dalam keadaan sudah rusak — pemindai yang mengganti pemisah GS dengan penanda yang dapat dicetak, middleware yang membungkus muatan dalam awalan khas vendor, sistem host yang mengubah segalanya menjadi huruf besar. Alih-alih memproses setiap string di tiap tempat pemanggilan (dan salah secara halus di salah satunya), daftarkan normalisasinya sekali saja pada `ParseConfig` dan biarkan pengurai yang menerapkannya.

Pemodifikasi berjalan di awal sekali `GaiaParser.parse(...)` — sebelum ID korelasi dilepas, sebelum Kode ID AIM dideteksi, sebelum alur GS1. Segala sesuatu setelahnya hanya melihat string yang sudah ditulis ulang. **Tidak ada yang aktif secara bawaan**, termasuk kedua [pemodifikasi bawaan](#pemodifikasi-bawaan) — Anda mengaktifkannya per `ParseConfig`.

**Antarmuka:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Pemodifikasi bawaan

Dua pemodifikasi disertakan dalam jar inti, di `tools.pantheum.gaia.modifier.custom`. Keduanya menangani dua cara muatan GS1 paling sering tiba dalam keadaan rusak — tanda kurung HRI tercetak yang diperlakukan sebagai data, dan spasi yang tak semestinya — sehingga kasus umum tidak memerlukan kelas buatan sendiri:

| Kelas | `getName()` | Fungsinya |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Melepas tanda kurung HRI di sekitar setiap AI (`(01)…(10)…`) dan memulihkan pemisah FNC1 yang tersirat olehnya. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Menghapus setiap spasi (`0x20`) dari string elemen AI. |

Keduanya adalah implementasi `ModifierInterface` biasa tanpa status istimewa — didaftarkan, diurutkan, dilaporkan, dan gagal persis seperti buatan Anda sendiri:

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

Keduanya nirstatus dan aman untuk banyak thread, jadi satu instans dapat dipakai bersama, dan keduanya dapat dialamati lewat nama kelas berkualifikasi penuh untuk penyiapan berbasis berkas konfigurasi (lihat [Mendaftarkan pemodifikasi](#mendaftarkan-pemodifikasi)).

#### `ModifierRemoveAIBrackets`

Interpretasi GS1 yang dapat dibaca manusia mencetak setiap AI dalam tanda kurung — `(01)09521234543213(10)ABC123` — semata-mata sebagai konvensi pencetakan. Pemindai atau middleware yang diatur untuk mengeluarkan HRI meneruskan tanda kurung itu sebagai data, dan tokeniser tidak tahu harus berbuat apa dengannya.

Melepas tanda kurung baru separuh pekerjaan. Dalam HRI, kurung buka `(` milik AI *berikutnya*-lah yang menandai akhir nilai sebelumnya, sehingga AI panjang variabel tidak memerlukan FNC1 dalam bentuk berkurung. Lepaskan tanda kurung secara naif dan batas itu lenyap:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Karena itu pemodifikasi ini **menyisipkan kembali FNC1 di setiap batas yang AI sebelumnya berpanjang variabel**, memulihkan persis apa yang disandikan oleh tanda kurung tadi:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Panjangnya dicari di `AiDefinitionRegistry` milik pengurai sendiri, sehingga setiap AI panjang variabel tertangani, bukan hanya sebuah daftar yang dipatri di kode. Tiga kasus sengaja dibiarkan: nilai yang sudah diakhiri FNC1 (sumber yang mengeluarkan kedua konvensi tidak mendapat pemisah kedua), kode berkurung yang bukan AI yang dikenal (AI tak dikenal tidak berkata apa pun tentang panjangnya sendiri), dan AI terakhir dalam string.

Penulisan ulang ini **idempoten** — menjalankannya atas keluarannya sendiri tidak mengubah apa pun — sehingga aman untuk umpan campuran yang hanya sebagian masukannya berkurung.

> **Batasan.** `(` dan `)` sendiri adalah karakter data GS1 yang sah, dan polanya hanyalah `\((\d{2,4})\)`. Nilai yang kebetulan memuat bilangan dua sampai empat digit dalam tanda kurung juga akan ikut dibuka kurungnya. Terapkan ini hanya pada sumber yang memakai konvensi kurung HRI, bukan pada sumber dengan nilai berkurung yang sungguhan.

#### `ModifierRemoveSpaces`

Sebagian pemindai, middleware, dan alur cetak label menyisipkan spasi yang tak semestinya ke dalam string elemen yang sebenarnya sudah baik bentuknya — untuk mengisi medan berlebar tetap, memisahkan kelompok agar mudah dibaca, atau melipat nilai yang panjang. Tokeniser memperlakukan tiap spasi sebagai data, sehingga merusak nilai tempat spasi itu berada dan, untuk AI panjang variabel, menggeser segala yang mengikutinya.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Hanya ASCII `0x20` yang dihapus. Karakter spasi-putih lain dibiarkan di tempatnya — tab, misalnya, berada di luar himpunan karakter yang dapat disandikan GS1, sehingga pengurai melaporkannya sebagai `GE-S008` alih-alih menyapunya diam-diam.

> **Batasan.** Spasi (`0x20`) adalah bagian dari himpunan karakter invarian GS1, sehingga sebuah batch/lot atau nomor komponen pelanggan bisa saja memuat spasi secara sah. Pemodifikasi ini tidak dapat membedakan spasi yang tak semestinya dari yang sungguhan; terapkan hanya pada sumber yang diketahui tidak memakai spasi di dalam nilai AI-nya.

#### Awalan dilewati, bukan ditulis ulang

Pemodifikasi berjalan sebelum pengurai melepas apa pun, sehingga masukan mentah masih mungkin membawa ID korelasi, Kode ID AIM, dan penanda ECI. Kedua pemodifikasi bawaan menemukan awal string elemen AI memakai logika `CorrelationIdParser` dan `DataCarrierParser` milik pengurai sendiri, menulis ulang hanya dari titik itu ke belakang, lalu menyambungkan hasilnya kembali ke awalan yang **tidak tersentuh**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Pembawa EAN/UPC yang nilainya diisi hingga GTIN-14 (`isRequiresGtinPadding()`) dilewati sepenuhnya — muatannya adalah nilai barcode numerik mentah tanpa struktur AI, sehingga tanda kurung maupun spasi tidak mungkin bermakna di sana.

#### Urutan: spasi sebelum tanda kurung

Bila keduanya dipakai, **daftarkan `ModifierRemoveSpaces` lebih dulu**. Pencocokan tanda kurung peka posisi: `( 01 )` yang dilebarkan spasi tidak cocok dengan `\((\d{2,4})\)`, sehingga tanda kurungnya bertahan dan pemisah yang tersirat olehnya tidak pernah dipulihkan.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Menulis pemodifikasi

Tulis buatan Anda sendiri bila kedua pemodifikasi bawaan tidak cocok — antarmukanya hanya satu metode.

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

Timpa kelebihan-muatan berargumen dua sebagai gantinya bila penulisan ulangnya bergantung pada konfigurasi penguraian:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Kontraknya:

| Aturan | Rincian |
|---|---|
| Nirstatus dan aman untuk banyak thread | Satu instans disimpan per kelas dan dipakai bersama pada setiap penguraian. |
| Konstruktor publik tanpa argumen | Diperlukan hanya bila pemodifikasi dirujuk lewat nama kelas. |
| Tangani masukan `null` dan kosong | Pengurai tidak menyaring keduanya sebelum rantai berjalan. |
| Mengembalikan `null` berarti "tidak berubah" | Nilai sebelumnya diteruskan. Kembalikan `input` apa adanya bila pemodifikasi tidak berlaku. |
| Lebih baik mengembalikan apa adanya daripada melempar | Pemodifikasi yang melempar akan membatalkan penguraian — lihat [Penanganan kegagalan](#penanganan-kegagalan-pemodifikasi). |
| `getName()` | Timpa untuk menentukan nama yang dilaporkan pada `ModifierInfo`; bawaannya adalah nama kelas sederhana. |

### Mendaftarkan pemodifikasi

Pemodifikasi berjalan sesuai urutan penambahannya, masing-masing diberi keluaran pendahulunya. Daftarkan lewat instans, lewat nama kelas berkualifikasi penuh, atau sebagai daftar dari salah satunya:

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

[Pemodifikasi bawaan](#pemodifikasi-bawaan) dinamai dengan cara yang sama seperti buatan Anda — **selalu berkualifikasi penuh**. Tidak ada nama pendek atau pencarian alias untuk keduanya; `ModifierRegistry` menemukan setiap pemodifikasi, bawaan maupun bukan, lewat nama kelas lengkap.

Nama ditemukan oleh `ModifierRegistry`, yang membuat instans setiap kelas sekali lewat konstruktor tanpa argumennya dan menyimpannya untuk setiap konfigurasi berikutnya yang menyebut kelas yang sama. Pencarian terjadi **saat konfigurasi dibangun**, sehingga nama yang tidak ditemukan, yang tidak mengimplementasikan `ModifierInterface`, atau yang tidak dapat diinstansiasi akan melempar `IllegalArgumentException` di sana — bukan diam-diam saat penguraian. Pemodifikasi yang tidak dapat dibangun secara reflektif (misalnya yang menyimpan dependensi suntikan) dapat didaftarkan lebih dahulu agar tetap dapat dialamati lewat nama:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Memeriksa apa yang dilakukan pemodifikasi

Bila pemodifikasi aktif, `ParseResult.getPayload()` mencerminkan masukan yang **sudah dimodifikasi**. Aslinya tersimpan pada `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` melaporkan `getName()` tiap pemodifikasi, yang bawaannya adalah nama kelas sederhana tetapi ditimpa oleh kedua pemodifikasi bawaan — sehingga rantai keduanya melaporkan nama tampilan, bukan nama kelas:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` mengembalikan `null` bila tidak ada pemodifikasi yang diatur. Bila pemodifikasi berjalan tetapi semuanya mengembalikan masukan apa adanya, informasinya tetap ada dan `isModified()` bernilai `false` — hanya pemodifikasi yang benar-benar mengubah masukan yang tercantum di `getAppliedModifiers()`.

### Penanganan kegagalan pemodifikasi

Pemodifikasi yang melempar akan membatalkan penguraian. Kekecualiannya dibungkus dalam `GaiaModifierException` yang menyebut pemodifikasi bermasalah, dan hasilnya membawa galat internal `GE-I001` yang pesannya memuat nama tersebut; `getPayload()` melaporkan masukan yang belum dimodifikasi. Penguraian sengaja **tidak** dilanjutkan dengan string yang setengah ditulis ulang — langkah normalisasi yang gagal diam-diam akan menghasilkan keluaran yang tampak sahih padahal diurai dari masukan yang keliru.

---

## Mode Penguraian

Setiap mode dinamai menurut [tahap alur](#alur-penguraian) terdalam yang dijalankannya; semua tahap sebelumnya tetap berjalan.

| Mode | Berjalan sampai | Menjawab |
|---|---|---|
| `DATA_CARRIER` | Tahap 1 (pengarahan masukan) | Simbologi apa yang membawa ini? |
| `SYNTAX` | Tahap 2 (sintaks) | Apakah kode dan panjang AI-nya baik bentuknya? |
| `CONTENT` | Tahap 3 (konten) | Apakah nilainya data GS1 yang sahih? |
| `INTERPRETATION` | Tahap 4 (interpretasi) | Apa makna nilai-nilainya? |

### Mode DATA_CARRIER

Berhenti setelah Tahap 1 — memvalidasi Kode ID AIM dan mengenali simbologinya, tetapi tidak memasuki alur penguraian AI. Berguna untuk pengenalan simbologi dan pengarahan tanpa beban validasi penuh.

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

**Pakai bila:** aplikasi Anda perlu mengenali jenis barcode sebelum memutuskan cara memproses muatannya — misalnya mengarahkan simbologi 1D dan 2D ke penangan yang berbeda. Untuk pengarahan itu, pakailah [`DataCarrierType`](#datacarrierentry-dan-datacarriertype) bertipe (`getDataCarrier().getDataCarrierType()`) alih-alih mencocokkan `getName()` sebagai string.

---

### Mode SYNTAX

Berhenti setelah Tahap 2. Berguna untuk penyaringan struktural awal tanpa biaya validasi konten.

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

**Pakai bila:** Anda ingin memastikan kode AI dan panjang datanya baik bentuknya sebelum menempuh validasi penuh, atau saat memindai dalam volume besar yang galat kontennya jarang terjadi.

---

### Mode CONTENT

Berhenti setelah Tahap 3.

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

> Sebagian besar AI tidak boleh berdiri sendiri: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY)
> dan `21` (SERIAL) masing-masing *mensyaratkan* kunci identifikasi seperti AI `01` dalam
> string elemen yang sama, sehingga menghilangkan GTIN pada contoh di atas akan gagal di
> Tahap 2 dengan `GE-S005` alih-alih sampai ke validasi konten sama sekali. Setel
> `skipRequiresCheck(true)` pada `ParseConfig` untuk mengurai fragmen yang sengaja tidak
> menyertakan AI pendampingnya.

**Pakai bila:** Anda perlu tahu apakah nilai hasil pindaian sepenuhnya patuh GS1 sebelum memakainya dalam proses bisnis, tanpa beban pemerkayaan interpretasi.

---

### Mode INTERPRETATION (bawaan)

Menjalankan alur penuh hingga Tahap 4. Ini bawaan saat memanggil `parse(String)` tanpa argumen mode. Hanya memperkaya elemen yang lolos validasi konten tanpa cela.

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

**Contoh keluaran:**
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

**Contoh jumlah moneter (AI 3932 — harga dengan kode mata uang ISO):**
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

**Pakai bila:** membangun lapisan tampilan, alat verifikasi label, atau antarmuka apa pun yang perlu menguraikan nilai AI dengan cara yang ramah bagi manusia.

---

## ID Korelasi

Sebagian alur kerja menambahkan pengenal korelasi 8 digit milik sendiri di depan masukan GS1 mentah agar peristiwa pemindaian dapat ditautkan kembali ke sebuah sesi atau transaksi. Formatnya adalah:

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

Tanda `~` (tilde) adalah pemisahnya. Ia **bukan** bagian dari konten GS1 — ia dilepas sebelum penguraian GS1 apa pun dimulai.

### Aturan pendeteksian

Awalan terdeteksi bila masukan dimulai dengan tepat 8 digit desimal ASCII (`0`–`9`) yang langsung diikuti `~`. Jika karakter ke-9 bukan `~`, atau salah satu dari 8 karakter pertama bukan digit, masukan diperlakukan sebagai konten GS1 biasa tanpa awalan korelasi.

### Mengakses ID korelasi

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

### Dipadukan dengan Kode ID AIM

Awalan korelasi boleh muncul sebelum Kode ID AIM. Pengurai menanganinya secara transparan:

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

Sebuah **GS1 Digital Link** menyandikan satu atau lebih nilai AI langsung dalam struktur sebuah URL HTTP(S), sehingga produk fisik memiliki pengenal yang dapat diselesaikan lewat web. GAIA mengimplementasikan *GS1 Digital Link Standard: URI Syntax* (rilis 1.7.0) untuk URI **tak terkompresi**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` mengenali URI Digital Link secara otomatis — setiap masukan yang diawali `http://` atau `https://` diarahkan ke `GS1DLParser`, yang menjalankan tahap konten dan interpretasi yang sama dengan alur string elemen.

### Struktur URI dan peran AI

Setiap AI dalam URI Digital Link memainkan salah satu dari tiga peran, yang tersedia pada setiap `GS1AIObjectElement` lewat `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Peran | Letak | Contoh |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Pasangan `/ai/value` pertama pada path (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Pasangan path berikutnya, terurut menurut kunci utama (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parameter kueri dengan kunci serba-numerik (§4.10) | `?17=271231` |

Aturan struktural yang ditegakkan (`DLPathRules`):
- Tepat **satu** kunci identifikasi utama pada path; kunci tambahan harus disandikan sebagai atribut data pada kueri.
- Kualifikasi kunci harus diizinkan oleh kunci utama dan muncul dalam urutan yang ditentukan. Kualifikasi opsional boleh dihilangkan, tetapi yang *memang* ada tetap harus mengikuti urutan tetapnya — lihat [Urutan kualifikasi](#urutan-kualifikasi).
- Segmen path khusus sembarang boleh mendahului kunci utama (mis. `/products/au/01/...`); ambil lewat `getDigitalLinkInfo().getCustomPathStem()`.
- Kunci kueri yang non-numerik (`linkType`, `context`, parameter ekstensi seperti `23P`) diabaikan; kunci serba-numerik harus berupa AI sahih yang ditandai `validAsDataAttribute`.
- Karakter nilai bersandi-persen didekodekan; AI `(03)` dan `(8014)` tidak diperbolehkan.

Kunci utama dan rangkaian kualifikasi yang diterimanya **digerakkan oleh data** dari definisi AI — penanda `gs1DigitalLinkPrimaryKey` dan atribut `gs1DigitalLinkQualifiers` — bukan dipatri di kode.

Setiap pelanggaran struktural, atau masukan yang bukan URL, menghasilkan galat struktural Digital Link (`GE-L001`–`GE-L014`, satu kode per kondisi). Metadata URL yang terurai (`scheme`, `domain`, `path`, `customPathStem`, `query`, dan `java.net.URL`) tetap tersedia lewat `getDigitalLinkInfo()` sekalipun ada galat struktural.

### Urutan kualifikasi

Untuk setiap kunci utama, `gs1DigitalLinkQualifiers` mendaftarkan satu atau lebih rangkaian kualifikasi **terurut**. Di dalam sebuah rangkaian, AI yang dibungkus kurung siku bersifat **opsional**, AI tanpa kurung bersifat **wajib** — mencerminkan notasi `[cpv-comp]` pada ABNF §4.9. Rangkaian-rangkaian untuk satu kunci utama adalah alternatif yang saling meniadakan.

GTIN (`01`), misalnya, mendefinisikan dua rangkaian:

| Path | Rangkaian | Makna |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — masing-masing opsional, tetapi tetap dalam urutan ini |
| upui-path | `235` | TPX (wajib); GTIN + TPX = UPUI |

Jadi `/01/09506000134352/10/LOT-ABC/21/SER` sahih (LOT sebelum SER, CPV dihilangkan), `/01/.../21/SER/10/LOT-ABC` **ditolak** (salah urutan), dan `/01/09506000134352/235/2ABC456` adalah upui-path. Pemeriksaan urutan berupa pencocokan sub-barisan yang menjaga urutan, sehingga AI opsional boleh dilewati tetapi tidak pernah boleh ditukar urutannya.

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

## Bekerja dengan Hasil

### ParseResult

Hasil tingkat teratas yang dikembalikan `GaiaParser.parse()`.

| Metode | Mengembalikan | Deskripsi |
|---|---|---|
| `isValid()` | `boolean` | `true` bila tidak ada galat pada tingkat mana pun. Peringatan tidak memengaruhi kesahihan. Selalu `true` bila `getAiObject()` bernilai `null`. |
| `getPayload()` | `String` | String masukan setelah awalan korelasi dilepas — dan setelah [pemodifikasi masukan](#pemodifikasi-masukan) menulis ulangnya. |
| `getPayloadContent()` | `String` | Muatan setelah Kode ID AIM dan awalan ECI dilepas. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (pembawa data yang ditolak karena bukan GS1, mis. pembawa Code 39 `]A0`), atau `UNABLE_TO_DETERMINE_CONTENT` (bila `aiObject` bernilai `null`, mis. mode `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Kedalaman alur yang diatur (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Tahap terdalam yang benar-benar dicapai penguraian — lihat di bawah. |
| `isParseComplete()` | `boolean` | `true` bila penguraian mencapai kedalaman yang diminta (`achieved == requested`). Tidak bergantung pada `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Semua AI yang berhasil diurai. `null` dalam mode `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Semua galat non-WARNING (tingkat objek + semua tingkat elemen). |
| `getWarnings()` | `List<GaiaError>` | Semua imbauan WARNING (tingkat objek + semua tingkat elemen). |
| `hasWarnings()` | `boolean` | `true` bila ada imbauan WARNING yang muncul. |
| `getIssues()` | `List<GaiaError>` | Galat dan peringatan digabungkan. |
| `hasDataCarrier()` | `boolean` | `true` bila sebuah Kode ID AIM dikenali. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata simbologi, atau `null` bila tidak ada pembawa yang dikenali. |
| `hasEci()` | `boolean` | `true` bila penanda ECI dilepas dari muatan. |
| `getEci()` | `EciEntry` | Metadata penyandian ECI, atau `null`. |
| `hasCorrelationId()` | `boolean` | `true` bila awalan korelasi `DDDDDDDD~` ada pada masukan asli. |
| `getCorrelationInfo()` | `CorrelationInfo` | ID korelasi yang diekstraksi, atau `null` bila tidak ada. |
| `isInputModified()` | `boolean` | `true` bila [pemodifikasi masukan](#pemodifikasi-masukan) mengubah masukan. |
| `getModifierInfo()` | `ModifierInfo` | Apa yang dilakukan rantai pemodifikasi — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` bila tidak ada pemodifikasi yang diatur. |
| `getTiming()` | `ProcessingTiming` | Pewaktuan penguraian menurut jam dinding — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` bila tidak dihasilkan oleh `GaiaParser`. |
| `getVersion()` | `String` | Versi pustaka yang menghasilkan hasil ini. |

#### Mode penguraian diminta vs tercapai

Alur menaiki tangga **SYNTAX → CONTENT → INTERPRETATION** dan berhenti lebih awal saat ada galat, sehingga mode yang benar-benar *tercapai* bisa lebih dangkal daripada mode yang *diminta*. `getAchievedParseMode()` melaporkan sejauh mana ia sampai:

| Diminta | Yang terjadi | Tercapai | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | galat **sintaks / struktural** menghentikan penguraian setelah tokenisasi | `SYNTAX` | `false` |
| `INTERPRETATION` | galat **konten** (format/digit pemeriksa salah) menghalangi pemerkayaan | `CONTENT` | `false` |
| `CONTENT` | konten selalu berjalan sampai tuntas (galat dicatat, bukan fatal) | `CONTENT` | `true` |
| mana pun (masukan bersih) | alur mencapai kedalaman yang diminta | = yang diminta | `true` |
| `DATA_CARRIER` | pembawa divalidasi; konten AI tidak diurai | `DATA_CARRIER` | `true` |
| mana pun | pembawa data ditolak sebelum penguraian AI (mis. pembawa `]A0` non-GS1) | `SYNTAX` | `false` |

`isParseComplete()` tidak bergantung pada `isValid()`: penguraian `CONTENT` atas sebuah GTIN dengan digit pemeriksa salah adalah **lengkap** (tahap kontennya berjalan) namun **tidak sahih** (digit pemeriksanya gagal). Pakai `isParseComplete()` untuk bertanya "apakah alurnya berjalan sedalam yang saya minta?" dan `isValid()` untuk bertanya "apakah datanya baik bentuknya?".

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

Kumpulan elemen AI yang berhasil diurai.

| Metode | Deskripsi |
|---|---|
| `getAis()` | Semua instans `GS1AIObjectElement` sesuai urutan masukan. |
| `get(String aiCode)` | Elemen pertama yang cocok dengan kode AI yang diberikan, atau `null`. |
| `contains(String aiCode)` | `true` bila ada AI dengan kode tersebut. |
| `size()` | Jumlah AI yang berhasil diurai. |
| `isValid()` | `true` bila tidak ada galat tingkat objek dan tidak ada elemen yang bergalat. |
| `toHriString()` | String HRI, mis. `(01)09506000134352 (17)261231`. |
| `toElementString()` | String elemen mentah — tanpa tanda kurung, dengan FNC1 setelah setiap elemen panjang variabel — mis. `010950600013435210LOT-ABC<GS>17271231`. Mengembalikan `null` bila `isValid()` bernilai `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` bila `hasDigitalLink()` benar, selain itu `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` bila masukannya berupa URI GS1 Digital Link yang membawa kunci identifikasi utama. URL yang baik bentuknya namun tanpa kunci utama tetap menyediakan `getDigitalLinkInfo()` tetapi mengembalikan `false` di sini. |
| `getCanonicalDigitalLink()` | URI GS1 Digital Link kanonis (§4.12) pada `https://id.gs1.org` — kunci utama dan kualifikasi sebagai segmen path, atribut data sebagai parameter kueri yang terurut menurut kunci AI — atau `null` bila tidak ada kunci utama. |
| `getDigitalLinkInfo()` | Metadata penguraian URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), atau `null` bila bukan Digital Link. |
| `getAllErrors()` | Galat tingkat objek + semua galat elemen (non-WARNING). |
| `getAllWarnings()` | Peringatan tingkat objek + semua peringatan elemen. |
| `getAllIssues()` | Semuanya digabungkan. |

---

### GS1AIObjectElement

Satu instans AI yang berhasil diurai.

| Metode | Deskripsi |
|---|---|
| `getAi()` | Kode AI, mis. `"01"`, `"3102"`. |
| `getTitle()` | Judul data GS1, mis. `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Deskripsi GS1 lengkap untuk AI tersebut, **terlokalkan ke bahasa penguraian** (mis. `"Global Trade Item Number (GTIN)"` dalam bahasa Inggris). Jatuh kembali ke teks Inggris dari definisi AI bila belum diterjemahkan. |
| `getFormatString()` | Deskriptor format yang mencakup AI *beserta* datanya, mis. `"N2+N14"` untuk AI `01`, `"N2+X..20"` untuk AI `10`, `"N4+N3+N..15"` untuk AI `3932`. |
| `getValue()` | Nilai data mentah yang diekstraksi dari string elemen. |
| `isFixedLength()` | `true` bila AI berpanjang data tetap. |
| `getPosition()` | Ofset karakter berbasis nol pada masukan asli. |
| `getGS1ComponentValues()` | Potongan nilai per komponen (untuk AI multi-komponen). |
| `getErrors()` | Galat non-WARNING tingkat elemen. |
| `getWarnings()` | Imbauan WARNING tingkat elemen. |
| `getIssues()` | Galat dan peringatan tingkat elemen digabungkan. |
| `hasErrors()` | `true` bila ada galat non-WARNING yang terlampir. |
| `hasWarnings()` | `true` bila ada imbauan WARNING yang terlampir. |
| `getInterpretations()` | Entri `GS1AIInterpretation` (terisi dalam mode INTERPRETATION). |
| `getInterpretation(String type)` | Interpretasi pertama yang cocok dengan kunci tipe `GS1Constants_Enricher` yang diberikan, atau `null`. |
| `getDigitalLinkAIType()` | Peran Digital Link elemen tersebut (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), atau `null` untuk masukan string elemen. |
| `hasDigitalLinkAIType()` | `true` bila sebuah peran Digital Link telah ditetapkan. |

---

### GaiaError

Galat validasi atau imbauan yang tak dapat diubah.

| Metode | Deskripsi |
|---|---|
| `getId()` | Pengenal katalog, mis. `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, atau `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, atau `INTERNAL`. |
| `getCode()` | Kode pendek yang terbaca mesin. |
| `getAi()` | Kode AI yang menyebabkan galat, atau `null` untuk galat tingkat objek. |
| `getMessage()` | Pesan terbaca manusia yang sudah disisipi nilai. |
| `getPosition()` | Ofset karakter berbasis nol pada masukan asli. |

---

### GS1AIInterpretation

Satu fragmen interpretasi berlabel, terlampir pada `GS1AIObjectElement` dalam mode `INTERPRETATION`.

| Metode | Deskripsi |
|---|---|
| `getType()` | Kunci tipe yang terbaca mesin, mis. `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Tetap sama lintas bahasa. |
| `getLabel()` | Label terbaca manusia, **terlokalkan ke bahasa penguraian** (mis. `"Date"` / `"GS1 company prefix"` dalam bahasa Inggris). |
| `getValue()` | Nilai hasil ekstraksi/pemerkayaan, mis. `"31/12/2026"`, `"9506000"`. Tidak dilokalkan. |

---

### DataCarrierEntry dan DataCarrierType

Bila masukan membawa Kode ID AIM, `ParseResult.getDataCarrier()` mengembalikan sebuah `DataCarrierEntry` yang menggambarkan simbol pembawa datanya. Entri itu adalah catatan registri spesifik untuk Kode ID AIM yang cocok; `DataCarrierType` adalah enum waktu-kompilasi yang menaunginya.

#### DataCarrierEntry

Metadata untuk satu Kode ID AIM yang dikenali (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Metode | Deskripsi |
|---|---|
| `getAimCodeId()` | Kode ID AIM yang cocok, mis. `"]C1"`. |
| `getName()` | Nama terbaca manusia untuk simbol spesifiknya, mis. `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Deskripsi pembawa yang lebih panjang. |
| `getType()` | Tipe struktural pembawa sebagai string (mencerminkan `getDataCarrierType().getCategory()`). |
| `getStandard()` | Standar simbologinya, bila tercatat. |
| `getDataCarrierType()` | `DataCarrierType` bertipe untuk entri ini — pakai ini untuk pengarahan programatis. |
| `isGs1Capable()` | `true` bila pembawa dapat memuat data GS1 (string elemen AI dan/atau Digital Link). |
| `isGs1AICapable()` | `true` bila pembawa dapat memuat string elemen AI GS1. |
| `isGs1DigitalLinkCapable()` | `true` bila pembawa dapat memuat URI GS1 Digital Link. |
| `isEciCapable()` | `true` bila pembawa mendukung penanda ECI. |
| `isRequiresGtinPadding()` | `true` untuk pembawa EAN/UPC/ITF yang nilai numeriknya diisi hingga GTIN-14 sebelum penguraian AI. |

#### DataCarrierType

Enum waktu-kompilasi berisi tipe-tipe pembawa data, berkunci Kode ID AIM yang ditetapkan dalam ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Karakter setelah `]` (*karakter kode*) memilih keluarganya; sebagian besar keluarga dipetakan ke satu konstanta yang mencakup semua pengubah (`ITF` mencakup `]I0`–`]I2`; `EAN_UPC` mencakup EAN-13, UPC-A, UPC-E, dan EAN-8). Bila GS1 mencadangkan sebuah pengubah untuk data AI, varian itu menjadi konstantanya sendiri — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — berbeda dari padanan biasanya. Bila tidak ada Kode ID AIM, atau bila kodenya menunjuk pembawa yang tak dikenal, tipenya adalah `UNKNOWN`.

| Metode | Deskripsi |
|---|---|
| `getCategory()` | Kategori umum `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, atau `OTHER`. |
| `getCodeChar()` | Karakter kode AIM yang menandai keluarganya, mis. `"Q"` untuk QR Code; `null` untuk `UNKNOWN`. |
| `getDisplayName()` | Nama terbaca manusia untuk *tipe*-nya (bisa lebih luas daripada `DataCarrierEntry.getName()` — mis. `"EAN-13 / UPC-A / UPC-E / EAN-8"` vs `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` untuk konstanta yang selalu menandakan data AI GS1: empat varian yang dicadangkan GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) ditambah `GS1_DATABAR`, yang secara hakiki GS1 karena setiap pengubah `]e` adalah GS1 DataBar. Lebih sempit daripada `DataCarrierEntry.isGs1AICapable()` — sebuah `QR_CODE` biasa tetap dapat membawa data AI GS1. |
| `static forAimCodeId(String)` | Menentukan tipe langsung dari sebuah Kode ID AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); mengembalikan `UNKNOWN` untuk ID yang tidak ada, cacat, atau tak dikenali. |

Pengarahan berdasarkan tipe alih-alih nama — mis. memisahkan simbol linear (Code-128) dari 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` hanya mencakup simbol matriks dan titik; pembawa linear bertumpuk (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) tergolong `STACKED_LINEAR`, sekalipun lazim
disebut barcode "2D". Untuk memperlakukan keduanya sebagai satu kelompok — misalnya untuk
memutuskan apakah dibutuhkan pemindai citra alih-alih pemindai laser — periksalah kedua
kategori itu.

> Penentuan tipe memerlukan Kode ID AIM hadir dalam pindaian; tanpanya `getDataCarrier()` bernilai `null` dan tipenya `UNKNOWN`. Atur pemindai agar mengirimkan awalan Kode ID AIM.

---

## Rujukan Galat

| Kode | Tingkat | Tahap | Makna |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Awalan AI tak dikenal — panjang data tak dapat ditentukan |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Masukan terlalu pendek untuk membaca kode AI yang utuh |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Nilai terpotong — karakternya kurang dari yang disyaratkan AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Pengidentifikasi Aplikasi ganda dalam string elemen |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Dependensi AI yang diwajibkan tidak ada |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Pasangan AI yang meniadakan — dua AI yang tak boleh berdampingan |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Kegagalan tokenisasi yang tak terduga |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Karakter di luar himpunan yang dapat disandikan GS1 dalam string elemen |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Pemisah FNC1 yang diwajibkan tidak ada setelah AI panjang variabel |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Data sisa melampaui semua batas maksimum komponen |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Pemisah FNC1 setelah AI panjang tetap di posisi tengah string |
| `GE-W002` | WARNING | SYNTAX | FNC1 sisa di akhir string elemen (imbauan saja) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Pelanggaran struktural URI Digital Link — satu kode per kondisi (URI cacat, skema, host, urutan kualifikasi, AI terlarang, tanpa kunci utama (`GE-L013`), kunci utama lebih dari satu (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Nilai tidak lolos pola regex milik AI |
| `GE-C003` | DATA_ERROR | CONTENT | Kegagalan validasi digit pemeriksa |
| `GE-C004` | DATA_ERROR | CONTENT | Kegagalan validasi pasangan karakter pemeriksa |
| `GE-C005` | FORMAT_ERROR | CONTENT | Nilai komponen memuat karakter di luar himpunan karakter yang diizinkan |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Kegagalan format komponen — satu kode per kondisi validator (lihat `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Kegagalan validasi semantik khusus — satu kode per kondisi validator (lihat `content/validator/`). **Pengecualian:** 14 pemeriksaan prefiks perusahaan GS1 yang tercantum di bawah bertingkat `WARNING`, dan `GE-C168` (kode negara numerik ISO 3166-1 tak dikenali) bertingkat `FORMAT_ERROR`. |
| Pemeriksaan prefiks perusahaan GS1 | WARNING | CONTENT | Kunci tidak diawali prefiks perusahaan GS1 yang dikenali, pada AI berkunci GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Imbauan saja — tidak memengaruhi kesahihan. |
| `GE-C169` | DATA_ERROR | CONTENT | Kegagalan digit pemeriksa IMEI (Luhn) pada AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Kegagalan digit pemeriksa EID (Luhn) pada AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Kode ID AIM tak dikenali |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Pembawa dikenali tetapi tidak mendukung string elemen AI GS1 maupun URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Galat internal yang tak terduga |

> **Cacat yang diketahui dalam penampilan pesan.** Templat katalog mengapit nilai yang
> disisipkan dengan apostrof ganda bergaya MessageFormat (`''{value}''`), tetapi
> `ErrorRegistry` menyisipkannya memakai `String.replace` biasa, sehingga penggandaan itu
> terbawa sampai ke `getMessage()` — saat ini Anda akan melihat `value ''09506000134351''`
> di tempat teks pesan yang dikutip dalam panduan ini menampilkan `value '09506000134351'`.
> Hal ini berlaku pada setiap pesan yang mengapit nilai di seluruh 35 katalog bahasa.
> Jangan mengurai pesan galat; cocokkan pada `getId()` / `getCode()`.

---

## Keamanan Thread

`GaiaParser` aman untuk banyak thread begitu dibangun. Satu instans boleh dipakai bersama lintas thread dan digunakan secara bersamaan. Pola yang dianjurkan adalah membangun satu instans saat aplikasi mulai dan memakainya berulang:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` tak dapat diubah dan sama amannya untuk dipakai bersama. Satu-satunya kewajiban keamanan thread yang tak dapat ditegakkan pustaka ini untuk Anda ada pada [pemodifikasi masukan](#pemodifikasi-masukan): satu instans tiap pemodifikasi disimpan dan dipakai bersama pada setiap penguraian yang berjalan bersamaan, jadi implementasinya harus nirstatus.

---

## Lampiran A — Konstanta String AI

`GS1Constants_AICodes` (paket `tools.pantheum.gaia.gs1.constants`) mendeklarasikan sebuah konstanta `String` untuk setiap Pengidentifikasi Aplikasi yang dikenali GAIA. Pakai konstanta ini alih-alih memaku literal kode AI di dalam kode:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Setiap konstanta memuat bentuk string kode AI-nya (mis. `AI_01_GTIN = "01"`).

### Identifikasi dan Serialisasi

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Kode Kontainer Pengiriman Berseri (SSCC). |
| `01` | `AI_01_GTIN` | Nomor Item Dagang Global (GTIN). |
| `02` | `AI_02_CONTENT` | Nomor Item Dagang Global (GTIN) dari item dagang yang terkandung. |
| `03` | `AI_03_MTO_GTIN` | Identifikasi item dagang buatan pesanan (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Nomor batch atau lot. |
| `20` | `AI_20_VARIANT` | Varian produk internal. |
| `21` | `AI_21_SERIAL` | Nomor seri. |
| `22` | `AI_22_CPV` | Varian produk konsumen. |
| `235` | `AI_235_TPX` | Ekstensi Bersambung Terkendali Pihak Ketiga dari Nomor Item Dagang Global (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identifikasi produk tambahan yang ditetapkan oleh produsen. |
| `241` | `AI_241_CUST_PART_NO` | Nomor komponen pelanggan. |
| `242` | `AI_242_MTO_VARIANT` | Nomor variasi buatan pesanan. |
| `243` | `AI_243_PCN` | Nomor komponen kemasan. |
| `250` | `AI_250_SECONDARY_SERIAL` | Nomor seri sekunder. |
| `251` | `AI_251_REF_TO_SOURCE` | Referensi ke entitas sumber. |
| `253` | `AI_253_GDTI` | Pengidentifikasi Jenis Dokumen Global (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Komponen ekstensi Nomor Lokasi Global (GLN). |
| `255` | `AI_255_GCN` | Nomor Kupon Global (GCN). |
| `30` | `AI_30_VAR_COUNT` | Jumlah item variabel (item dagang dengan ukuran variabel). |
| `37` | `AI_37_COUNT` | Jumlah item dagang atau bagian item dagang yang terkandung dalam unit logistik. |

### Tanggal dan Waktu

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Tanggal produksi (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Tanggal jatuh tempo (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Tanggal pengemasan (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Tanggal baik digunakan sebelum (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Tanggal batas jual (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Tanggal kedaluwarsa (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Tanggal dan waktu pengiriman tidak sebelum (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Tanggal dan waktu pengiriman tidak setelah (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Tanggal rilis (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Tanggal dan waktu kedaluwarsa (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Tanggal pembekuan pertama (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Tanggal panen (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Tanggal batas uji (YYMMDD[hhmm]). |

### Kuantitas dan Ukuran — Ukuran Variabel (Metrik)

Keluarga AI empat digit `310n`–`369n` menyandikan kuantitas berukuran variabel. Digit ketiga memilih jenis ukurannya; **digit keempat** (`n`, 0–5) adalah jumlah tempat desimal tersirat — jadi `AI_3102_NET_WEIGHT_KG` berarti berat bersih dalam kilogram dengan 2 tempat desimal.

| Keluarga | Pola konstanta (`n` = digit desimal) | Deskripsi |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Berat bersih, kilogram (item dagang dengan ukuran variabel). |
| `311n` | `AI_311n_LENGTH_M` | Panjang atau dimensi pertama, meter (item dagang dengan ukuran variabel). |
| `312n` | `AI_312n_WIDTH_M` | Lebar, diameter, atau dimensi kedua, meter (item dagang dengan ukuran variabel). |
| `313n` | `AI_313n_HEIGHT_M` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, meter (item dagang dengan ukuran variabel). |
| `314n` | `AI_314n_AREA_M` | Luas, meter persegi (item dagang dengan ukuran variabel). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volume bersih, liter (item dagang dengan ukuran variabel). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volume bersih, meter kubik (item dagang dengan ukuran variabel). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Berat logistik, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Panjang atau dimensi pertama, meter. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Lebar, diameter, atau dimensi kedua, meter. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, meter. |
| `334n` | `AI_334n_AREA_M_LOG` | Luas, meter persegi. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volume logistik, liter. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volume logistik, meter kubik. |
| `337n` | `AI_337n_KG_PER_M` | Kilogram per meter persegi. |

### Kuantitas dan Ukuran — Ukuran Variabel (Imperial / AS)

| Keluarga | Pola konstanta (`n` = digit desimal) | Deskripsi |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Berat bersih, pon (item dagang dengan ukuran variabel). |
| `321n` | `AI_321n_LENGTH_IN` | Panjang atau dimensi pertama, inci (item dagang dengan ukuran variabel). |
| `322n` | `AI_322n_LENGTH_FT` | Panjang atau dimensi pertama, kaki (item dagang dengan ukuran variabel). |
| `323n` | `AI_323n_LENGTH_YD` | Panjang atau dimensi pertama, yard (item dagang dengan ukuran variabel). |
| `324n` | `AI_324n_WIDTH_IN` | Lebar, diameter, atau dimensi kedua, inci (item dagang dengan ukuran variabel). |
| `325n` | `AI_325n_WIDTH_FT` | Lebar, diameter, atau dimensi kedua, kaki (item dagang dengan ukuran variabel). |
| `326n` | `AI_326n_WIDTH_YD` | Lebar, diameter, atau dimensi kedua, yard (item dagang dengan ukuran variabel). |
| `327n` | `AI_327n_HEIGHT_IN` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, inci (item dagang dengan ukuran variabel). |
| `328n` | `AI_328n_HEIGHT_FT` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, kaki (item dagang dengan ukuran variabel). |
| `329n` | `AI_329n_HEIGHT_YD` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, yard (item dagang dengan ukuran variabel). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Berat logistik, pon. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Panjang atau dimensi pertama, inci. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Panjang atau dimensi pertama, kaki. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Panjang atau dimensi pertama, yard. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Lebar, diameter, atau dimensi kedua, inci. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Lebar, diameter, atau dimensi kedua, kaki. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Lebar, diameter, atau dimensi kedua, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, inci. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, kaki. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Kedalaman, ketebalan, tinggi, atau dimensi ketiga, yard. |
| `350n` | `AI_350n_AREA_IN` | Luas, inci persegi (item dagang dengan ukuran variabel). |
| `351n` | `AI_351n_AREA_FT` | Luas, kaki persegi (item dagang dengan ukuran variabel). |
| `352n` | `AI_352n_AREA_YD` | Luas, yard persegi (item dagang dengan ukuran variabel). |
| `353n` | `AI_353n_AREA_IN_LOG` | Luas, inci persegi. |
| `354n` | `AI_354n_AREA_FT_LOG` | Luas, kaki persegi. |
| `355n` | `AI_355n_AREA_YD_LOG` | Luas, yard persegi. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Berat bersih, troy ons (item dagang dengan ukuran variabel). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Berat bersih (atau volume), ons (item dagang dengan ukuran variabel). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volume bersih, quart (item dagang dengan ukuran variabel). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volume bersih, galon AS (item dagang dengan ukuran variabel). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volume logistik, quart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volume logistik, galon AS. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volume bersih, inci kubik (item dagang dengan ukuran variabel). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volume bersih, kaki kubik (item dagang dengan ukuran variabel). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volume bersih, yard kubik (item dagang dengan ukuran variabel). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volume logistik, inci kubik. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volume logistik, kaki kubik. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volume logistik, yard kubik. |

### Penetapan Harga dan Jumlah Moneter

Digit keempat (`n`) menyandikan jumlah tempat desimal tersirat. Rentang yang
diizinkan berbeda-beda menurut keluarganya — lihat kolom `n`.

| Keluarga | Pola konstanta (`n` = digit desimal) | `n` | Deskripsi |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Jumlah yang harus dibayar atau nilai kupon, mata uang lokal. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Jumlah yang harus dibayar dengan kode mata uang ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Jumlah yang harus dibayar, area moneter tunggal (item dagang dengan ukuran variabel). |
| `393n` | `AI_393n_PRICE` | 0–9 | Jumlah yang harus dibayar dengan kode mata uang ISO (item dagang dengan ukuran variabel). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Persentase diskon kupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Jumlah yang harus dibayar per satuan ukur, area moneter tunggal (item dagang dengan ukuran variabel). |

### Lokasi dan Pengiriman

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Nomor pesanan pembelian pelanggan. |
| `401` | `AI_401_GINC` | Nomor Identifikasi Global untuk Konsinyasi (GINC). |
| `402` | `AI_402_GSIN` | Nomor Identifikasi Pengiriman Global (GSIN). |
| `403` | `AI_403_ROUTE` | Kode perutean. |
| `410` | `AI_410_SHIP_TO_LOC` | Nomor Lokasi Global (GLN) kirim ke / antar ke. |
| `411` | `AI_411_BILL_TO` | Nomor Lokasi Global (GLN) tagihan ke / faktur ke. |
| `412` | `AI_412_PURCHASE_FROM` | Nomor Lokasi Global (GLN) dibeli dari. |
| `413` | `AI_413_SHIP_FOR_LOC` | Nomor Lokasi Global (GLN) kirim untuk / antar untuk - teruskan ke. |
| `414` | `AI_414_LOC_NO` | Identifikasi lokasi fisik - Nomor Lokasi Global (GLN). |
| `415` | `AI_415_PAY_TO` | Nomor Lokasi Global (GLN) pihak penagih. |
| `416` | `AI_416_PROD_SERV_LOC` | Nomor Lokasi Global (GLN) lokasi produksi atau layanan. |
| `417` | `AI_417_PARTY` | Nomor Lokasi Global (GLN) pihak. |
| `420` | `AI_420_SHIP_TO_POST` | Kode pos kirim ke / antar ke dalam satu otoritas pos tunggal. |
| `421` | `AI_421_SHIP_TO_POST` | Kode pos kirim ke / antar ke dengan kode negara ISO. |
| `422` | `AI_422_ORIGIN` | Negara asal item dagang. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Negara pemrosesan awal. |
| `424` | `AI_424_COUNTRY_PROCESS` | Negara pemrosesan. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Negara pembongkaran. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Negara yang mencakup seluruh rantai proses. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivisi negara asal. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Nama perusahaan kirim ke / antar ke. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Kontak kirim ke / antar ke. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Baris alamat 1 kirim ke / antar ke. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Baris alamat 2 kirim ke / antar ke. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Suburb kirim ke / antar ke. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Lokalitas kirim ke / antar ke. |
| `4306` | `AI_4306_SHIP_TO_REG` | Wilayah kirim ke / antar ke. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Kode negara kirim ke / antar ke. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Nomor telepon kirim ke / antar ke. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Lokasi GEO kirim ke / antar ke. |
| `4310` | `AI_4310_RTN_TO_COMP` | Nama perusahaan kembalikan ke. |
| `4311` | `AI_4311_RTN_TO_NAME` | Kontak kembalikan ke. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Baris alamat 1 kembalikan ke. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Baris alamat 2 kembalikan ke. |
| `4314` | `AI_4314_RTN_TO_SUB` | Suburb kembalikan ke. |
| `4315` | `AI_4315_RTN_TO_LOC` | Lokalitas kembalikan ke. |
| `4316` | `AI_4316_RTN_TO_REG` | Wilayah kembalikan ke. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Kode negara kembalikan ke. |
| `4318` | `AI_4318_RTN_TO_POST` | Kode pos kembalikan ke. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Nomor telepon kembalikan ke. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Deskripsi kode layanan. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Penanda barang berbahaya. |
| `4322` | `AI_4322_AUTH_LEAVE` | Izin meninggalkan barang tanpa penerima. |
| `4323` | `AI_4323_SIG_REQUIRED` | Penanda tanda tangan diperlukan. |
| `4330` | `AI_4330_MAX_TEMP_F` | Suhu maksimum dalam Fahrenheit (dinyatakan dalam per seratus derajat). |
| `4331` | `AI_4331_MAX_TEMP_C` | Suhu maksimum dalam Celsius (dinyatakan dalam per seratus derajat). |
| `4332` | `AI_4332_MIN_TEMP_F` | Suhu minimum dalam Fahrenheit (dinyatakan dalam per seratus derajat). |
| `4333` | `AI_4333_MIN_TEMP_C` | Suhu minimum dalam Celsius (dinyatakan dalam per seratus derajat). |

### Atribut Produk dan Ketertelusuran

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Nomor Stok NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Klasifikasi karkas dan potongan daging UN/ECE. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Potensi aktif. |
| `7005` | `AI_7005_CATCH_AREA` | Area penangkapan. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Spesies untuk tujuan perikanan. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Jenis alat tangkap. |
| `7010` | `AI_7010_PROD_METHOD` | Metode produksi. |
| `7020` | `AI_7020_REFURB_LOT` | ID lot perbaikan (refurbishment). |
| `7021` | `AI_7021_FUNC_STAT` | Status fungsional. |
| `7022` | `AI_7022_REV_STAT` | Status revisi. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Pengidentifikasi Aset Individu Global (GIAI) dari rakitan. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Nomor pengolah dengan kode negara ISO tiga digit (10 slot). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC dengan Ekstensi 1 dan indeks Importir. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Jenis unit kargo UN/CEFACT. |

### Nomor Penggantian Biaya Kesehatan Nasional (NHRN)

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Jerman PZN. |
| `711` | `AI_711_NHRN_CIP` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Prancis CIP. |
| `712` | `AI_712_NHRN_CN` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Spanyol CN. |
| `713` | `AI_713_NHRN_DRN` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Amerika Serikat NDC. |
| `716` | `AI_716_NHRN_AIC` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Italia AIC. |
| `717` | `AI_717_NHRN_SRN` | Nomor Reimbursement Layanan Kesehatan Nasional (NHRN) - Nomor Registrasi Sanitasi Kosta Rika. |

### Layanan Kesehatan, GMN, HIDRI, CPID, dan Data Orang

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Rujukan Sertifikasi (10 slot). |
| `7240` | `AI_7240_PROTOCOL` | ID protokol. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Jenis media AIDC. |
| `7242` | `AI_7242_VCN` | Nomor Kontrol Versi (VCN). |
| `7250` | `AI_7250_DOB` | Tanggal lahir (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Tanggal dan waktu lahir (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Jenis kelamin biologis. |
| `7253` | `AI_7253_FAMILY_NAME` | Nama keluarga orang. |
| `7254` | `AI_7254_GIVEN_NAME` | Nama pemberian orang. |
| `7255` | `AI_7255_SUFFIX` | Sufiks nama orang. |
| `7256` | `AI_7256_FULL_NAME` | Nama lengkap orang. |
| `7257` | `AI_7257_PERSON_ADDR` | Alamat orang. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Urutan kelahiran bayi. |
| `7259` | `AI_7259_BABY` | Bayi dari nama keluarga. |
| `8001` | `AI_8001_DIMENSIONS` | Produk gulungan (lebar, panjang, diameter inti, arah, jumlah sambungan). |
| `8002` | `AI_8002_CMT_NO` | Pengidentifikasi telepon seluler. |
| `8003` | `AI_8003_GRAI` | Pengidentifikasi Aset Dapat Dikembalikan Global (GRAI). |
| `8004` | `AI_8004_GIAI` | Pengidentifikasi Aset Individu Global (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Harga per satuan ukur. |
| `8006` | `AI_8006_ITIP` | Identifikasi bagian item dagang individu (ITIP). |
| `8007` | `AI_8007_IBAN` | Nomor Rekening Bank Internasional (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Tanggal dan waktu produksi (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indikator sensor yang dapat dibaca secara optik. |
| `8010` | `AI_8010_CPID` | Pengidentifikasi Komponen/Suku Cadang (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Nomor seri Pengidentifikasi Komponen/Suku Cadang (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Versi perangkat lunak. |
| `8013` | `AI_8013_GMN` | Nomor Model Global (GMN). |
| `8014` | `AI_8014_MUDI` | Pengidentifikasi Registrasi Perangkat Individual Tingkat Lanjut (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Nomor Hubungan Layanan Global (GSRN) untuk mengidentifikasi hubungan antara organisasi penyedia layanan dan pemberi layanan. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Nomor Hubungan Layanan Global (GSRN) untuk mengidentifikasi hubungan antara organisasi penyedia layanan dan penerima layanan. |
| `8019` | `AI_8019_SRIN` | Nomor Instans Hubungan Layanan (SRIN). |
| `8020` | `AI_8020_REF_NO` | Nomor referensi slip pembayaran. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identifikasi bagian item dagang (ITIP) yang terkandung dalam unit logistik. |
| `8030` | `AI_8030_DIGSIG` | Tanda tangan digital (DigSig). |
| `8040` | `AI_8040_IMEI` | Identitas Peralatan Bergerak Internasional (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identitas Peralatan Bergerak Internasional 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nomor SIM tertanam. |
| `8043` | `AI_8043_PSIM` | Nomor SIM fisik. |
| `8110` | `AI_8110` | Identifikasi kode kupon untuk digunakan di Amerika Utara. |
| `8111` | `AI_8111_POINTS` | Poin loyalitas kupon. |
| `8112` | `AI_8112` | Identifikasi kode kupon berkas penawaran positif untuk digunakan di Amerika Utara. |
| `8200` | `AI_8200_PRODUCT_URL` | URL Kemasan Diperluas. |

### Penggunaan Internal / Perusahaan

| AI | Konstanta | Deskripsi |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informasi yang disepakati bersama antara mitra dagang. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Informasi internal perusahaan (9 slot). |

---

## Lampiran B — Konstanta Kunci Interpretasi

Bila `GaiaParser.parse()` dipanggil dengan `ParseMode.INTERPRETATION`, setiap `GS1AIObjectElement` dapat membawa daftar objek `GS1AIInterpretation` yang dihasilkan oleh pemerkaya khusus domain. Pakai konstanta `GS1Constants_Enricher` (paket `tools.pantheum.gaia.gs1.constants`) sebagai kunci untuk mencari nilai interpretasi tertentu:

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

Label tampilan **bukan** konstanta — semuanya berada di katalog terlokalkan pada `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, berkunci konstanta tipe. `GS1AIInterpretation.getLabel()` mengembalikan label dalam bahasa penguraian (lihat [Pesan dan label terlokalkan](#pesan-dan-label-terlokalkan)), dan jatuh kembali ke bahasa Inggris bila sebuah katalog tidak memuat kuncinya. Kolom Label tampilan di bawah mencantumkan teks bahasa Indonesia; kunci tipenya sendiri tetap sama lintas bahasa, jadi cocokkan selalu pada kunci, tidak pernah pada label.

### Tanggal dan Waktu

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `DATE_VALUE` | Tanggal | AI tanggal (11–17, 7003, 7006, 7011, dll.) |
| `DATE_FORMAT` | Format tanggal | AI tanggal |
| `TIME_VALUE` | Waktu | AI yang membawa waktu (7003, 7011, 8008, dll.) |
| `TIME_FORMAT` | Format waktu | AI yang membawa waktu |
| `DATETIME_VALUE` | Tanggal dan waktu | AI tanggal+waktu |
| `DATETIME_FORMAT` | Format tanggal dan waktu | AI tanggal+waktu |

### Tanggal Panen

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Tanggal mulai panen | AI 7007 |
| `HARVEST_END_DATE` | Tanggal akhir panen | AI 7007 (akhir rentang opsional) |
| `HARVEST_DATE_RANGE` | Rentang tanggal panen | AI 7007 |

### Prefiks Perusahaan GS1

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefiks perusahaan GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Kode anggota GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organisasi anggota GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Tipe GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Tingkat kemasan | AI 01 |
| `GTIN_CHECK_DIGIT` | Digit pemeriksa | AI 01, 02 |

### SSCC

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Digit ekstensi | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Referensi seri | AI 00 |
| `SSCC_CHECK_DIGIT` | Digit pemeriksa | AI 00 |

### Negara (ISO 3166)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Kode negara (numerik) | AI negara tunggal (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Kode negara (alfa-2) | AI negara alfa-2 |
| `COUNTRY_NAME` | Nama negara | AI negara tunggal |
| `COUNTRY_LIST` | Negara | AI 423 — semua nama digabung, mis. `Australia, New Zealand` |

AI 423 (negara pemrosesan awal) dapat membawa hingga lima negara, sehingga ia
mengeluarkan **satu pasang bernomor untuk tiap negara** — `COUNTRY_CODE_NUMERIC_1`,
`COUNTRY_NAME_1`, `COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — diikuti satu
ringkasan `COUNTRY_LIST`. Bangunlah kunci-kunci ini dari konstanta
`COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` dengan urutan mulai dari 1, atau
cukup telusuri `getInterpretations()`; kunci `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME`
tanpa akhiran **tidak** dikeluarkan untuk AI 423.

### Mata Uang (ISO 4217)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Kode mata uang | AI jumlah dengan mata uang (391n, 393n) |
| `CURRENCY_ALPHA` | Kode alfabetis mata uang | AI jumlah dengan mata uang |
| `CURRENCY_NAME` | Nama mata uang | AI jumlah dengan mata uang |

### Suhu

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `TEMPERATURE` | Suhu | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Satuan suhu | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Suhu (terformat) | AI 4330–4333 |

### Jenis Kelamin (ISO 5218)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `SEX_CODE` | Kode jenis kelamin | AI 7252 |
| `SEX_DESCRIPTION` | Deskripsi jenis kelamin | AI 7252 |

### Spesies Akuatik (FAO)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Kode spesies | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nama ilmiah | AI 7008 |
| `SPECIES_ENGLISH` | Nama umum | AI 7008 |
| `SPECIES_FAMILY` | Famili | AI 7008 |
| `SPECIES_ORDER` | Ordo | AI 7008 |

### Nomor Stok NATO (NSN)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `NSN_FSG` | Grup pasokan | AI 7001 |
| `NSN_FSG_NAME` | Nama grup pasokan | AI 7001 |
| `NSN_FSCG` | Kelas pasokan | AI 7001 |
| `NSN_FSCG_NAME` | Nama kelas pasokan | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Kode negara | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Negara | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Kode negara ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategori NCS | AI 7001 |
| `NSN_NIIN` | Nomor barang nasional | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Produk Gulungan

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Lebar gulungan (mm) | AI 8001 |
| `ROLL_LENGTH` | Panjang gulungan (m) | AI 8001 |
| `CORE_DIAMETER` | Diameter inti (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Kode arah gulungan | AI 8001 |
| `WINDING_DIRECTION` | Arah gulungan | AI 8001 |
| `SPLICES` | Sambungan | AI 8001 |

### IBAN

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Kode negara | AI 8007 |
| `IBAN_COUNTRY_NAME` | Negara | AI 8007 |
| `IBAN_CHECK_DIGITS` | Digit pemeriksa | AI 8007 |
| `IBAN_CHECK_VALID` | Pemeriksaan | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Nomor seri | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Digit pemeriksa | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Badan penerbit | AI 8040, 8041 |

Ke-15 digitnya terurai menjadi `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]`, dengan
RBI berupa dua digit pertama TAC — jadi `IMEI_RBI` adalah awalan `IMEI_TAC`, bukan bidang
yang terpisah. `IMEI_FORMATTED` menampilkan pengelompokan tampilan baku GSMA
`AA-BBBBBB-CCCCCC-D` (mis. `49-015420-323751-8`), yang membelah TAC pada batas RBI;
pengelompokan lama `6-2-6-1`, yang membelah di tempat kode perakitan akhir yang kini
ditiadakan pernah dimulai, tidak dikeluarkan.

`IMEI_RBI_NAME` menerjemahkan RBI menjadi nama badan penetapnya lewat `ImeiRbiData`, dan
dilampirkan **terakhir serta hanya bila kodenya tercantum di sana**. Tabel itu mencakup tiga
kelompok:

- **Badan yang masih menetapkan** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, ditambah
  `99` Global Hexadecimal Administrator dan `98` (dicadangkan).
- **Rentang uji** — `00` dan `02`–`09`, menandakan nomor IMEI uji dan bukan penetapan
  sungguhan. Tanyakan lewat `ImeiRbiData.isTestCode(code)`.
- **Badan yang tak lagi menetapkan** — badan historis seperti `49` (BZT/BAPT, Jerman), `44`
  (BABT, Britania Raya), dan `91` (MSAI, India). Tanyakan lewat
  `ImeiRbiData.isNoLongerAllocating(code)`. Perangkat yang membawa kode ini normal dan masih
  beroperasi; hanya penetapan baru yang berhenti, jadi ini informasi untuk dilaporkan, bukan
  sama sekali pertanda kesahihan.

Tidak adanya `IMEI_RBI_NAME` berarti "RBI ini tidak ada dalam tabel kami", **bukan** "IMEI-nya
tidak sahih": tabelnya dihimpun dari daftar RBI yang diterbitkan, bukan langsung dari GSMA,
sehingga bisa tertinggal dari badan yang baru ditunjuk. Jangan menyimpulkan putusan validasi
apa pun dari ketidakhadirannya; RBI bukan karakter pemeriksa. Kode yang menelusuri daftar
interpretasi harus tahan bila ia tidak ada, alih-alih mengindeks berdasarkan posisi.

### Pengenal SIM (EID / ICCID)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategori industri | AI 8042 |
| `EID_BODY` | Badan EID | AI 8042 |
| `EID_CHECK_DIGIT` | Digit pemeriksa | AI 8042 |
| `ICCID_BODY` | Badan ICCID | AI 8043 |
| `ICCID_EXTENSION` | Ekstensi | AI 8043 |

`SIM_MII` memuat **dua** digit pertama (`89`), yaitu pasangan yang ditetapkan ITU-T E.118
untuk telekomunikasi. ISO/IEC 7812 sendiri mendefinisikan MII sebagai **digit pertama saja**,
jadi `SIM_MII_NAME` menentukan kategorinya dari digit `8` yang pertama lewat `Iso7812Data` —
menghasilkan "Healthcare, telecommunications and other future industry assignments". Karena
itu nilainya tetap sama untuk setiap EID yang baik bentuknya; ia dilaporkan demi
ketertelusuran ke standar, bukan sebagai pembeda.
`Iso7812Data.nameForCode(digit)` menerima satu digit tunggal, sedangkan
`nameForIdentifier(prefix)` menerima awalan yang lebih panjang dan membaca digit pertamanya.

`SIM_MII_NAME` hanya dikeluarkan oleh pemerkaya `EidEnricher` (AI 8042). `IccidEnricher`
(AI 8043) menampilkan `SIM_MII` tanpa kategorinya.

### Rujukan Sertifikasi

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Nomor urut | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Kode skema sertifikasi | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Skema sertifikasi | AI 7230–7239 |
| `CERT_REFERENCE` | Referensi sertifikasi | AI 7230–7239 |

### GS1 UIC

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `UIC_CODE` | Kode UIC | AI 7040 |
| `UIC_EXTENSION_1` | Ekstensi 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Indeks importir | AI 7040 |

### Urutan Kelahiran Bayi

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posisi kelahiran | AI 7258 |
| `BIRTH_TOTAL` | Total kelahiran | AI 7258 |
| `BIRTH_SEQUENCE` | Urutan kelahiran | AI 7258 |

### Nomor Model Global (GMN)

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Referensi model | AI 8013 |
| `GMN_CHECK_PAIR` | Pasangan pemeriksa | AI 8013 |

### HIDRI

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Referensi perangkat | AI 8014 |
| `HIDRI_CHECK_PAIR` | Pasangan pemeriksa | AI 8014 |

### CPID

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Referensi komponen & suku cadang | AI 8010–8011 |

### Nilai Desimal dan Ukuran

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Nilai desimal | AI numerik dengan tempat desimal tersirat (31xx–36xx) |
| `DECIMAL_AMOUNT` | Jumlah | AI penetapan harga (390n–395n) |
| `DECIMAL_PERCENTAGE` | Persentase | AI 394n |
| `DECIMAL_PLACES` | Tempat desimal | Bersama `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Format persentase | AI 394n |
| `ISO_UNIT_CODE` | Kode satuan ISO | AI pengukuran |
| `ISO_UNIT_NAME` | Nama satuan ISO | AI pengukuran |
| `MONETARY_AMOUNT` | Jumlah moneter | AI penetapan harga |
| `MONETARY_AMOUNT_DISPLAY` | Jumlah moneter (terformat) | AI penetapan harga |

### Koordinat Geografis

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `LATITUDE` | Lintang | AI 4309 |
| `LONGITUDE` | Bujur | AI 4309 |
| `GEO_COORDINATES` | Koordinat geografis | AI 4309 |
| `LATITUDE_DMS` | Lintang (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Bujur (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Koordinat geografis (DMS) | AI 4309 |

### Metode Produksi

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Kode metode produksi | AI 7010 |
| `PRODUCTION_METHOD` | Metode produksi | AI 7010 |

### Tipe Media AIDC

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Kode tipe media AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Tipe media AIDC | AI 7241 |

### Bagian dari Total

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Nomor bagian | AI 8006 |
| `PIECE_TOTAL` | Total bagian | AI 8006 |
| `PIECE_OF_TOTAL` | Bagian dari total | AI 8006 |

### Pemisahan Komponen

Kunci yang dikeluarkan oleh pemisahan komponen deklaratif di `content/ai-content.json`
alih-alih oleh pemerkaya yang ditulis dalam Java — semuanya menampilkan bagian bernama dari
sebuah nilai AI majemuk. Berbeda dari setiap kunci lain dalam lampiran ini, kunci-kunci ini
**tidak punya konstanta di `GS1Constants_Enricher`**: cocokkan literal stringnya, atau baca
tipenya dari `GS1AIInterpretation.getType()`.

| Kunci tipe | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Digit pemeriksa | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Nomor seri | AI 253, 255, 8003 |
| `POSTAL_CODE` | Kode pos | AI 421 |
| `PROCESSOR_ID` | Pengenal pemroses | AI 7030–7039 |

Perhatikan bahwa `CHECK_DIGIT` di sini adalah kunci pemisahan komponen yang umum, berbeda
dari kunci khusus pemerkaya `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT`, dan
`EID_CHECK_DIGIT` di atas.

### Lain-lain

| Konstanta kunci | Label tampilan | Dihasilkan oleh |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Nilai | AI boolean / penanda (4321–4323) |
| `DECODED_TEXT` | Teks terdekode | AI teks bebas |
