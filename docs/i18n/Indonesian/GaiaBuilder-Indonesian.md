# GaiaBuilder — Panduan Pengembang

## Daftar Isi

1. [Ikhtisar](#ikhtisar)
2. [Tentang GS1 dan General Specifications](#tentang-gs1-dan-general-specifications)
3. [Panduan Cepat](#panduan-cepat)
4. [Cara kerjanya](#cara-kerjanya)
5. [Membangun string elemen](#membangun-string-elemen)
   - [AI atribut memerlukan kunci identifikasinya](#ai-atribut-memerlukan-kunci-identifikasinya)
6. [Membangun URI Digital Link](#membangun-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validasi dan galat](#validasi-dan-galat)
   - [Metode pembangunan yang melempar](#metode-pembangunan-yang-melempar)
   - [Metode tryBuild\* yang tidak melempar](#metode-trybuild-yang-tidak-melempar)
   - [Bahasa pesan galat](#bahasa-pesan-galat)
   - [BuildResult](#buildresult)
9. [Digit pemeriksa](#digit-pemeriksa)
10. [Keamanan Thread](#keamanan-thread)
11. [Rujukan API](#rujukan-api)

---

## Ikhtisar

`GaiaBuilder` adalah pasangan terbalik dari [`GaiaParser`](GaiaParser-Indonesian.md): ia mengubah sekumpulan pasangan Pengidentifikasi Aplikasi (AI) dan nilainya menjadi **string elemen** GS1 yang baik bentuknya atau sebuah **URI GS1 Digital Link**. Anda memberikan AI beserta nilai datanya yang lengkap; pembangun merangkainya, memvalidasi hasilnya dengan mesin yang sama dengan `GaiaParser`, lalu menampilkan keluarannya.

Karena pembangun memvalidasi dengan *mengurai sendiri calon keluarannya*, semua yang dikembalikannya dijamin terurai bersih lewat `GaiaParser` — keduanya tidak mungkin berselisih tentang apa yang baik bentuknya.

**Kelas titik masuk:** `tools.pantheum.gaia.GaiaBuilder`

---

## Tentang GS1 dan General Specifications

**GS1** adalah organisasi nirlaba global yang mengembangkan dan memelihara standar terbuka untuk identifikasi rantai pasok dan pertukaran data. Standarnya dipakai di ritel, layanan kesehatan, logistik, jasa boga, dan banyak industri lain, mencakup segala hal mulai dari barcode produk pada kemasan konsumen hingga pelacakan berseri dosis farmasi.

Rujukan otoritatif untuk segala yang diimplementasikan pembangun ini adalah **GS1 General Specifications** — satu dokumen yang mendefinisikan:

- Seluruh kode Pengidentifikasi Aplikasi (AI), judul datanya, formatnya, dan aturan validasinya
- Aturan sintaks untuk menyusun dan menyandikan string elemen AI
- Persyaratan simbologi barcode dan penetapan Pengidentifikasi Simbologi AIM
- Algoritma digit pemeriksa dan karakter pemeriksa
- Penafsiran tahun dua digit (aturan jendela geser)
- Spesifikasi Data Matrix, QR Code, GS1-128, GS1 DataBar, dan pembawa data lainnya

GS1 General Specifications diperbarui setiap tahun. Edisi terkini dan sumber daya pendukungnya tersedia di:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA mengimplementasikan **Release 26.0 (Disahkan, Jan 2026)** dari GS1 General Specifications.

URI GS1 Digital Link diatur oleh standar pendamping, **GS1 Digital Link: URI Syntax**, yang mendefinisikan kunci identifikasi utama, urutan kualifikasi kunci, dan penyandian atribut data yang diterapkan pembangun saat menampilkan URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA mengimplementasikan **Release 1.7.0 (Disahkan, Agu 2026)** dari standar GS1 Digital Link: URI Syntax.

Rujukan bagian di sepanjang dokumen ini mengacu pada GS1 General Specifications (mis. "Table 7-5", "section 7.12"), kecuali nomor bagian Digital Link (mis. "§4.9", "§4.12") yang mengacu pada standar GS1 Digital Link: URI Syntax.

---

## Panduan Cepat

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

Pakai konstanta `GS1Constants_AICodes` alih-alih literal AI mentah (lihat [Lampiran A pada panduan pengurai](GaiaParser-Indonesian.md#lampiran-a--konstanta-string-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Cara kerjanya

Setiap pembangunan menempuh jalur yang sama:

1. **Perangkaian** — pasangan AI/nilai dirangkai menjadi sebuah calon string elemen. Pemisah grup FNC1 (`0x1D`) disisipkan setelah setiap AI yang *memerlukan pemisah* dan bukan elemen terakhir. AI yang panjangnya sudah ditentukan sebelumnya (GTIN, tanggal, ukuran panjang tetap) tidak mendapat pemisah; selain itu mendapat. (AI yang tak dikenal tidak pernah sampai ke langkah ini — `ai(...)` menolaknya seketika; lihat [Membangun string elemen](#membangun-string-elemen).)
2. **Validasi** — calon itu diurai dalam mode `CONTENT` oleh `GaiaParser`. Setiap nilai diperiksa terhadap format AI-nya dan digit pemeriksanya, dan aturan struktural (pasangan AI yang diwajibkan dan yang meniadakan) ditegakkan. Bila penguraiannya tidak sahih, pembangunan gagal.
3. **Penampilan** —
   - Untuk string elemen, `toElementString()` dari objek yang tervalidasi itulah yang dikembalikan.
   - Untuk Digital Link, setiap elemen diberi peran DL-nya (kunci utama, kualifikasi kunci, atau atribut data), rangkaian kualifikasi kuncinya divalidasi, URI-nya dikeluarkan, lalu **URI keluaran itu diurai kembali untuk memastikan ia kembali sebagai Digital Link yang sahih** — sebuah pengaman untuk langkah perangkaian string dan penyandian-persen. Bila tidak, `GaiaBuilderException` dilemparkan.

Ini mencerminkan logika rekonstruksi di `DLSyntaxParser`, sehingga penempatan pemisah dan validasinya sama persis dengan yang diharapkan pengurai.

---

## Membangun string elemen

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** divalidasi seketika: `ai(...)` melempar `IllegalArgumentException` bila AI-nya bukan Pengidentifikasi Aplikasi GS1 yang dikenal. (Pembangun menyambungkan AI dengan nilainya sebelum penguraian, jadi AI yang tak dikenal atau kepanjangan seperti `"99999"` harus tertangkap di sini — jika tidak, ia akan diam-diam ditokenisasi ulang menjadi AI yang berbeda.) **Nilainya** divalidasi belakangan, saat pembangunan.
- Nilai harus **lengkap**, termasuk digit pemeriksanya. Pembangun tidak menghitung ataupun menambahkan digit pemeriksa untuk Anda — lihat [Digit pemeriksa](#digit-pemeriksa).
- AI dikeluarkan sesuai urutan penambahannya. Pembangun menyisipkan pemisah FNC1 di tempat yang disyaratkan struktur GS1; jangan menambahkannya sendiri.
- Membangun **tanpa AI sama sekali** melempar `GaiaBuilderException("No AIs supplied")` dengan `getErrors()` kosong — satu-satunya kegagalan yang tidak membawa `GaiaError` apa pun.
- AI yang nilainya gagal pada aturan format atau digit pemeriksanya membuat pembangunan gagal.

### AI atribut memerlukan kunci identifikasinya

Sebagian besar AI adalah *atribut* yang oleh GS1 General Specifications diwajibkan ditemani sebuah kunci identifikasi, dan pembangun menegakkannya — ia memvalidasi lewat tahap sintaks yang penuh, tanpa jalan pintas. Sebuah nomor batch/lot atau nomor seri yang berdiri sendiri **bukan** string elemen yang sahih:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Kunci identifikasi (GTIN `01`, SSCC `00`, GLN `414`, …) dan AI internal perusahaan (`90`–`99`) sepenuhnya sah berdiri sendiri. Selain itu, semuanya perlu pendamping.

> `GaiaParser` dapat diminta melewati pemeriksaan ini lewat `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` sengaja tidak menyediakan padanannya — tujuannya adalah menghasilkan keluaran yang patuh standar. Untuk merangkai string elemen yang sengaja tidak lengkap, rangkailah sendiri lalu urai dengan pemeriksaan itu dimatikan.

---

## Membangun URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Digital Link yang sahih memerlukan tepat satu **kunci identifikasi utama** (mis. GTIN `01`, GLN `414`, SSCC `00`). Pembangun mengelompokkan setiap AI yang Anda berikan:

| Peran | Cara ditampilkan | Contoh |
|------|-------------|---------|
| Kunci identifikasi utama | Segmen path setelah domain/awalan | `/01/09506000134352` |
| Kualifikasi kunci (CPV `22`, batch `10`, serial `21`, …) | Segmen path berikutnya, **dalam urutan kanonis §4.9** (bukan urutan penambahan Anda) | `/10/LOT-ABC` |
| Atribut data (semua sisanya) | Parameter kueri, **terurut secara leksikal menurut kunci AI** (§4.12) | `?17=271231` |

Karena kualifikasi diurutkan ulang saat ditampilkan, memberikannya di luar urutan tidak jadi soal — `ai("21", …)` sebelum `ai("10", …)` tetap ditampilkan sebagai `/10/LOT/21/SER`. Yang harus dapat diterima oleh kunci utama hanyalah *himpunannya*.

Nilai pada path maupun kueri disandikan-persen.

Pembangunan gagal (melempar `GaiaBuilderException`, atau mengembalikan `BuildResult` yang gagal) bila:

- **tidak ada** kunci identifikasi utama di antara AI-nya;
- ada **lebih dari satu** kunci identifikasi utama;
- salah satu AI-nya **terlarang** dalam Digital Link (`03`, `8014`);
- **rangkaian kualifikasi kunci** tidak sahih bagi kunci utama yang dipilih (kualifikasi yang tidak mengikuti kunci itu, atau kualifikasi di luar urutan yang diizinkan).

---

## BuilderDigitalLinkConfig

Berikan `BuilderDigitalLinkConfig` untuk mengendalikan skema, domain, awalan path, parameter kueri tambahan, dan fragmen:

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

| Metode pembangun | Kegunaan | Bawaan |
|----------------|---------|---------|
| `scheme(String)` | Skema URI; harus `http` atau `https` | `https` |
| `domain(String)` | Otoritas resolver — host atau `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Segmen path sebelum kunci utama pertama; garis miring di kedua ujungnya dinormalkan | *(tidak ada)* |
| `baseUrl(String)` | Kemudahan yang memecah sebuah URL menjadi `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Parameter kueri tambahan, dilampirkan **setelah** atribut data AI, sesuai urutan penyisipan; disandikan-persen | — |
| `fragment(String)` | Fragmen URI (tanpa `#` di depan); disandikan-persen | *(tidak ada)* |

`build()` memvalidasi konfigurasinya seketika: skema yang bukan `http(s)` atau domain yang kosong akan melempar `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) adalah bawaan `https://id.gs1.org` tanpa tambahan apa pun — persis yang dipakai `buildDigitalLinkUri()` tanpa argumen, dan yang dihasilkan `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → skema `http`, domain `id.example.org:8080`, awalan path `/r`.
- Parameter kueri tambahan selalu datang setelah atribut turunan AI, sehingga urutan AI yang kanonis (§4.12) tetap terjaga.

`BuilderDigitalLinkConfig` tak dapat diubah; pakai ulang satu instans sesuka Anda.

---

## Validasi dan galat

### Metode pembangunan yang melempar

`buildElementString()`, `buildDigitalLinkUri()`, dan `buildDigitalLinkUri(BuilderDigitalLinkConfig)` melempar **`GaiaBuilderException`** (sebuah `RuntimeException` yang tak terperiksa) bila AI-AI tersebut tidak dapat membentuk keluaran yang baik bentuknya:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Untuk kegagalan **konten** (digit pemeriksa salah, format tak cocok, AI hilang/terlarang), `getErrors()` membawa objek `GaiaError` milik pengurai — objek yang sama yang [didokumentasikan dalam panduan pengurai](GaiaParser-Indonesian.md#gaiaerror).
- Untuk kegagalan **struktur Digital Link** (kunci utama tidak ada, kunci utama lebih dari satu, AI terlarang, rangkaian kualifikasi kunci tak sahih), `getErrors()` membawa satu `GaiaError` (kode `GE-L008`, `GE-L012`, `GE-L013`, atau `GE-L014`) yang terlokalkan ke bahasa pembangun.

### Metode tryBuild\* yang tidak melempar

Bila masukan berasal dari pengguna dan kegagalan adalah hasil yang wajar dan dapat ditangani, pakailah varian `tryBuild*` alih-alih mengendalikan alur lewat kekecualian. Semuanya mengembalikan [`BuildResult`](#buildresult) alih-alih melempar:

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

| Melempar | Tidak melempar |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Setiap metode `tryBuild*` berbagi inti validasi yang sama dengan kembarannya yang melempar; hanya batas kegagalannya yang berbeda.

### Bahasa pesan galat

Galat validasi konten diambil dari katalog galat terlokalkan. Panggil `language(...)` untuk memilih bahasa pesan `GaiaError` yang dibawa `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; bawaannya adalah bahasa Inggris:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Ini adalah setelan `GaiaConstants.Language` yang sama yang diterima `GaiaParser` lewat `ParseConfig`, sehingga pembangun dan pengurai melokalkan dengan cara yang sama.

Pesan `GaiaError` untuk kegagalan **konten** maupun **struktur Digital Link** (kunci utama tidak ada, kunci utama lebih dari satu, AI terlarang, rangkaian kualifikasi kunci tak sahih) sama-sama dilokalkan lewat katalog galat bersama — yang terakhir memakai kode `GE-L008`, `GE-L012`, `GE-L013`, dan `GE-L014`.

### BuildResult

`BuildResult` (paket `tools.pantheum.gaia.result`) adalah tipe nilai tak dapat diubah yang menggambarkan hasil pemanggilan `tryBuild*`:

| Metode | Saat berhasil | Saat gagal |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | String yang ditampilkan | `null` |
| `getMessage()` | `null` | Deskripsi kegagalan |
| `getErrors()` | Daftar kosong | Galat validasi (sama seperti pada `GaiaBuilderException.getErrors()`) |

---

## Digit pemeriksa

Pembangun memvalidasi digit pemeriksa tetapi **tidak** menghitungnya — nilai Anda harus sudah menyertakan digit pemeriksanya. Untuk menghitungnya, pakai `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` menerapkan algoritma modulo-10 baku GS1 pada digit-digit yang diberikan dan mengembalikan digit pemeriksa `0`–`9`, atau `-1` bila masukannya `null`, kosong, atau bukan numerik.

---

## Keamanan Thread

`GaiaBuilder` **tidak** aman untuk banyak thread dan dimaksudkan untuk sekali pakai: panggil `create()`, tambahkan AI-nya, bangun sekali. Buatlah pembangun baru untuk tiap keluaran; jangan memakai satu pembangun bersama lintas thread.

`BuilderDigitalLinkConfig` (dan keluaran `BuildResult`-nya) tak dapat diubah dan boleh dipakai bersama sesuka hati — bangun satu konfigurasi saat aplikasi mulai dan pakai ulang lintas banyak pembangun.

---

## Rujukan API

### `GaiaBuilder`

| Metode | Deskripsi |
|--------|-------------|
| `static GaiaBuilder create()` | Memulai pembangun baru yang kosong. |
| `GaiaBuilder ai(String ai, String value)` | Menambahkan sebuah AI beserta nilai lengkapnya. Melempar `IllegalArgumentException` bila salah satunya `null`, atau bila `ai` bukan Pengidentifikasi Aplikasi GS1 yang dikenal. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Menyetel bahasa pesan galat validasi konten (bawaannya Inggris). `null` diabaikan. |
| `String buildElementString()` | Menampilkan string elemen GS1. Melempar `GaiaBuilderException` saat gagal. |
| `String buildDigitalLinkUri()` | Menampilkan URI Digital Link kanonis. Melempar `GaiaBuilderException` saat gagal. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Menampilkan URI Digital Link sesuai `config`. Melempar `GaiaBuilderException` saat gagal. |
| `BuildResult tryBuildElementString()` | Pembangunan string elemen yang tidak melempar. |
| `BuildResult tryBuildDigitalLinkUri()` | Pembangunan Digital Link kanonis yang tidak melempar. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Pembangunan Digital Link sesuai `config` yang tidak melempar. |

### `BuilderDigitalLinkConfig`

| Anggota | Deskripsi |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Bawaan `https://id.gs1.org`. |
| `static Builder builder()` | Pembangun konfigurasi yang baru. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Skema, otoritas resolver, dan awalan path setelah ditentukan. |
| `getExtraQueryParams()` | Parameter kueri tambahan, sesuai urutan penyisipan. |
| `getFragment()` | Fragmen, atau `null`. |

### `GaiaBuilderException`

| Anggota | Deskripsi |
|--------|-------------|
| `getErrors()` | Objek `GaiaError` yang menyebabkan kegagalan — galat pengurai untuk kegagalan konten, atau satu galat struktural Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Tidak pernah `null`. |

### `BuildResult`

| Anggota | Deskripsi |
|--------|-------------|
| `isSuccess()` | Apakah pembangunannya berhasil. |
| `getValue()` | Keluaran yang ditampilkan saat berhasil; `null` saat gagal. |
| `getMessage()` | Deskripsi kegagalan saat gagal; `null` saat berhasil. |
| `getErrors()` | Galat validasi saat gagal; kosong saat berhasil. Tidak pernah `null`. |
| `getTiming()` | `ProcessingTiming` untuk operasi pembangunan (waktu mulai, durasi pemrosesan), atau `null`. |

---

Lihat juga: **[GaiaParser — Panduan Pengembang](GaiaParser-Indonesian.md)** untuk sisi penguraian, model elemen AI, rujukan galat, serta lampiran konstanta AI dan interpretasi.
