# GaiaParser — Panduan Cepat

Ubah muatan barcode GS1 menjadi data terstruktur, tervalidasi, dan mudah dibaca manusia
dalam kira-kira sepuluh menit. Ini jalur pintasnya; **[Panduan Pengembang
GaiaParser](GaiaParser-Indonesian.md)** adalah rujukan lengkapnya, dan
**[GaiaBuilder](GaiaBuilder-Indonesian.md)** membahas arah sebaliknya (membangun string elemen dan
URI Digital Link).

## Daftar Isi

1. [Tambahkan Gaia ke proyek Anda](#1-tambahkan-gaia-ke-proyek-anda)
2. [Urai sesuatu](#2-urai-sesuatu)
3. [Baca hasilnya](#3-baca-hasilnya)
4. [Tangani penguraian yang gagal](#4-tangani-penguraian-yang-gagal)
5. [Dua hal yang akan menjegal Anda](#5-dua-hal-yang-akan-menjegal-anda)
6. [Awalan pemindai dan Digital Link langsung berjalan](#6-awalan-pemindai-dan-digital-link-langsung-berjalan)
7. [Kerjakan lebih sedikit: mode penguraian](#7-kerjakan-lebih-sedikit-mode-penguraian)
8. [Ubah bahasa dan format tanggal](#8-ubah-bahasa-dan-format-tanggal)
9. [Bersihkan masukan yang berantakan](#9-bersihkan-masukan-yang-berantakan)
10. [Ke mana selanjutnya](#10-ke-mana-selanjutnya)

---

## 1. Tambahkan Gaia ke proyek Anda

Gaia tidak diterbitkan di Maven Central, jadi bangun intinya sekali dan pasang ke repositori
lokal Anda:

```bash
cd gaia && mvn install
```

Lalu bergantunglah padanya:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Itu seluruh dependensi yang perlu Anda tulis. Jar-nya ramping: satu-satunya dependensi
lingkup-kompilasi milik Gaia — `com.fasterxml.jackson.core:jackson-databind` — ikut secara
transitif; dan bila build Anda sudah menetapkan versi Jackson tertentu, penetapan itulah
yang menang dan itu pula yang dipakai Gaia. Gaia menyasar **Java 11**, dan jar yang sama
berjalan tanpa perubahan pada setiap rilis JVM berikutnya.

> Melewati rangkaian uji inti (`mvn install -DskipTests`) mengubah beberapa menit menjadi
> beberapa detik selagi Anda baru memulai.

---

## 2. Urai sesuatu

Satu kelas, tanpa konfigurasi:

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

`parse(String)` menjalankan alur **penuh**: sintaks, validasi konten, interpretasi. Itulah
bawaan yang benar — persempit belakangan bila Anda sudah mengukur alasan untuk itu.

---

## 3. Baca hasilnya

`ParseResult.getAiObject()` memuat AI yang berhasil diurai. Ambil AI tertentu berdasarkan
kodenya, bukan posisinya:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Setiap elemen membawa daftar **interpretasi** — makna yang sudah dibongkar di balik
digit-digit mentahnya, dihasilkan oleh tahap interpretasi:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` sudah terlokalkan dan dimaksudkan untuk ditampilkan. Untuk *membaca* sebuah
nilai di dalam kode, carilah lewat kunci tipenya yang tetap:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI yang berbeda menghasilkan kunci yang berbeda — GTIN memberikan prefiks perusahaannya,
tipe GTIN, dan digit pemeriksanya; harga memberikan mata uang dan jumlah desimalnya.
Daftar lengkapnya ada di
[Lampiran B](GaiaParser-Indonesian.md#lampiran-b--konstanta-kunci-interpretasi), dan konstantanya ada
di `GS1Constants_Enricher`. Tidak semua AI punya interpretasi: nomor batch/lot berupa teks
bebas yang tidak ada apa pun untuk disimpulkan, jadi daftarnya kosong.

---

## 4. Tangani penguraian yang gagal

Muatan yang tidak sahih adalah hasil biasa, bukan kekecualian — `parse` tidak pernah
melempar karena data GS1 yang buruk:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Bercabanglah pada `getId()`, jangan pernah pada pesannya.** Pesan itu terlokalkan dan
kata-katanya bukan kontrak — pesan itu juga saat ini membawa cacat kutipan yang diketahui
(penggandaan `''` di atas), yang dicatat di
[Rujukan Galat](GaiaParser-Indonesian.md#rujukan-galat).

Dua pertanyaan berbeda, dua metode berbeda:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Penguraian berhenti turun begitu sebuah tahap gagal, jadi digit pemeriksa yang salah berarti
Anda mendapat galat validasi tetapi tanpa interpretasi.

### Peringatan tidak membuat hasil menjadi tidak sahih

Sebagian pemeriksaan bersifat imbauan. Prefiks perusahaan GS1 yang tak dikenali akan
dilaporkan, tetapi muatannya tetap baik bentuknya:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Pakai `getIssues()` bila Anda menginginkan keduanya sekaligus. Bila alur kerja Anda memang
harus menolak prefiks yang tak dikenali, periksalah `getWarnings()` secara eksplisit —
`isValid()` tidak akan melakukannya untuk Anda.

---

## 5. Dua hal yang akan menjegal Anda

### Pemisah GS, dan mengapa melewatkannya lebih buruk daripada sebuah galat

AI panjang variabel berjalan sampai **karakter GS** (ASCII `0x1D`, disebut FNC1 dalam
simbologi barcode) atau sampai akhir string. Bila ada AI lain yang mengikutinya, pemisah itu
wajib:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Melewatkannya **tidak** menghasilkan galat — melainkan jawaban keliru yang penuh percaya
diri:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` adalah `X..20`, jadi menelan `LOT-ABC21SN-98765` sepenuhnya masuk akal, dan pengurai
tidak punya cara untuk tahu bahwa itu bukan yang Anda maksud. Tidak ada apa pun di hilir yang
dapat memulihkan ini, jadi benarkan pemisahnya di sumbernya: baca bita dari pemindai sebagai
**ISO-8859-1** agar `0x1D` selamat, dan tulis `""` dalam literal string Java. AI panjang
tetap (`01`, `17`, `3103`) tidak memerlukan pemisah — pengurai tahu panjangnya.

### Sebagian besar AI tidak berdiri sendiri

Batch/lot, nomor seri, tanggal kedaluwarsa, dan sejenisnya adalah *atribut*: GS1 General
Specifications mewajibkan semuanya ditemani sebuah kunci identifikasi, dan Gaia
menegakkannya.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Tambahkan GTIN, maka ia lolos. Bila Anda memang perlu mengurai sebuah fragmen — uji unit,
pindaian sebagian — matikan pemeriksaannya:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Awalan pemindai dan Digital Link langsung berjalan

Anda tidak perlu memberi tahu Gaia bentuk masukannya — ia mendeteksi keempat bentuknya.
Berikan saja apa pun yang diberikan pemindai Anda.

**Awalan Pengidentifikasi Simbologi AIM** menetapkan simbologinya dan dilepas secara
otomatis:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**URI GS1 Digital Link** menempuh validasi dan pemerkayaan yang sama:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Karena kedua bentuk itu bermuara pada `GS1AIObject` yang sama, kode yang mengonsumsi hasil
pindaian tidak perlu peduli mana yang datang — dan `toElementString()` /
`getCanonicalDigitalLink()` mengubah yang satu menjadi yang lain.

**Awalan korelasi 8 digit** (`12345678~…`) juga dilepas dan disimpan di
`getCorrelationInfo()`, bila alur Anda memakainya.

---

## 7. Kerjakan lebih sedikit: mode penguraian

Bawaannya melakukan segalanya. Mintalah lebih sedikit bila Anda hanya perlu sebagian
jawabannya:

| Mode | Menjawab | Biaya |
|---|---|---|
| `DATA_CARRIER` | Simbologi apa ini? | Termurah — sama sekali tanpa penguraian AI, `getAiObject()` bernilai `null` |
| `SYNTAX` | Apakah kode dan panjang AI-nya baik bentuknya? | Tanpa digit pemeriksa, tanpa interpretasi |
| `CONTENT` | Apakah ini data GS1 yang sahih? | Validasi penuh, tanpa interpretasi |
| `INTERPRETATION` | Apa maknanya? | **Bawaan** — segalanya |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Pilih `CONTENT` bila Anda memvalidasi dalam volume besar dan tidak pernah menampilkan
uraiannya, dan `DATA_CARRIER` bila Anda hanya perlu mengarahkan pindaian ke penangan yang
tepat.

---

## 8. Ubah bahasa dan format tanggal

Pesan galat, label interpretasi, dan deskripsi AI diterjemahkan ke dalam **35 bahasa**;
tanggal dapat ditampilkan sesuka Anda. Semuanya tercakup dalam satu `ParseConfig` yang tak
dapat diubah:

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

Nilai tidak pernah dilokalkan — hanya label, deskripsi, dan pesannya — jadi `"2026-12-31"`
dan `"09506000134352"` bermakna sama dalam bahasa apa pun. Bangun konfigurasinya sekali saat
aplikasi mulai lalu pakai bersama; ia tak dapat diubah.

---

## 9. Bersihkan masukan yang berantakan

Bila sumber Anda mengeluarkan tanda kurung HRI tercetak atau spasi yang berkeliaran, inti
pustaka punya dua **pemodifikasi masukan** yang membereskan muatan sebelum penguraian:

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

Tidak ada yang aktif secara bawaan, dan keduanya punya catatan penting — spasi dan tanda
kurung sama-sama karakter data GS1 yang sah, jadi terapkan hanya pada sumber yang Anda
kenali. Lihat [Pemodifikasi bawaan](GaiaParser-Indonesian.md#pemodifikasi-bawaan), yang juga menjelaskan
mengapa melepas tanda kurung harus memulihkan pemisah yang tersirat olehnya.

---

## 10. Ke mana selanjutnya

- **[Panduan Pengembang GaiaParser](GaiaParser-Indonesian.md)** — alur secara terperinci, model hasil
  yang lengkap, setiap kode galat, dan lampiran AI serta kunci interpretasi.
- **[Panduan Pengembang GaiaBuilder](GaiaBuilder-Indonesian.md)** — bangun string elemen dan URI Digital
  Link dari pasangan AI/nilai.
- **[Rujukan HTTP Gaia API](../../gaia-api-reference.md)** — mesin yang sama lewat HTTP, bila Anda
  lebih suka tidak menyematkan pustakanya.
- **[ai-codes.txt](../../ai-codes.txt)** — daftar mendatar `(AI) TITLE` untuk pencarian cepat.

### Versi lima baris

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
