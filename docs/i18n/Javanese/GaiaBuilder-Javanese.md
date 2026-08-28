# GaiaBuilder — Pandhuan kanggo Pangembang

## Daftar Isi

1. [Gambaran Umum](#gambaran-umum)
2. [Bab GS1 lan General Specifications](#bab-gs1-lan-general-specifications)
3. [Wiwitan Cepet](#wiwitan-cepet)
4. [Kepiye Cara Kerjane](#kepiye-cara-kerjane)
5. [Mbangun String Elemen](#mbangun-string-elemen)
   - [AI atribut mbutuhake kunci identifikasine](#ai-atribut-mbutuhake-kunci-identifikasine)
6. [Mbangun Digital Link URI](#mbangun-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validasi lan Kesalahan](#validasi-lan-kesalahan)
   - [Metode pambangunan sing nguncalake exception](#metode-pambangunan-sing-nguncalake-exception)
   - [Metode tryBuild\* sing ora nguncalake exception](#metode-trybuild-sing-ora-nguncalake-exception)
   - [Basa pesen kesalahan](#basa-pesen-kesalahan)
   - [BuildResult](#buildresult)
9. [Angka Centhang](#angka-centhang)
10. [Aman kanggo Thread](#aman-kanggo-thread)
11. [Referensi API](#referensi-api)

---

## Gambaran Umum

`GaiaBuilder` iku kosok balene [`GaiaParser`](GaiaParser-Javanese.md): iku ngowahi sakumpulan pasangan Application Identifier (AI) lan nilai dadi **string elemen** GS1 sing bener utawa **GS1 Digital Link URI**. Kowe sing nyedhiyakake AI lan nilai datane sing jangkep; builder sing nyusun kabeh mau, nyahake asile liwat mesin sing padha karo sing dienggo `GaiaParser`, banjur ngetokake asile.

Amarga builder nyahake kanthi cara *mem-parsing outpute dhewe sing lagi dicalonake*, apa wae sing dibalekake wis dijamin bakal bisa di-parse maneh kanthi resik dening `GaiaParser` — loro-lorone ora bakal tau beda panemu bab apa sing kabentuk bener.

**Kelas lawang mlebu:** `tools.pantheum.gaia.GaiaBuilder`

---

## Bab GS1 lan General Specifications

**GS1** iku organisasi global nirlaba sing nyusun lan ngrumat standar terbuka kanggo identifikasi rantai pasokan lan pertukaran data. Standare dienggo ing ritel, kesehatan, logistik, layanan pangan, lan akeh industri liyane, nyakup kabeh wiwit barcode produk ing kemasan konsumen nganti pelacakan serial dosis obat.

Sumber resmi kanggo kabeh sing dileksanakake builder iki yaiku **GS1 General Specifications** — siji dokumen sing netepake:

- Kabeh kode Application Identifier (AI), judhul datane, formate, lan aturan validasine
- Aturan sintaksis kanggo mbangun lan ngode string elemen AI
- Syarat simbologi barcode lan wenehan AIM Code ID
- Algoritma angka centhang lan aksara centhang
- Rampungan taun rong angka (aturan jendhela geser)
- Spesifikasi Data Matrix, QR Code, GS1-128, GS1 DataBar, lan operator data liyane

GS1 General Specifications dianyarake saben taun. Edhisi saiki lan sumber panyengkuyunge kasedhiya ing:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA nglakokake **Release 26.0 (Disahake, Januari 2026)** saka GS1 General Specifications.

GS1 Digital Link URI diatur dening standar kanca yaiku **GS1 Digital Link: URI Syntax**, sing netepake kunci identifikasi utama, urutan key qualifier, lan pangodean data attribute sing dienggo builder nalika ngetokake Digital Link URI:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA nglakokake **Release 1.7.0 (Disahake, Agustus 2026)** saka standar GS1 Digital Link: URI Syntax.

Kabeh acuan bagean ing sajroning dokumen iki nuduhake GS1 General Specifications (tuladha, "Table 7-5", "section 7.12"), kajaba nomer bagean Digital Link (tuladha, "§4.9", "§4.12"), sing nuduhake standar GS1 Digital Link: URI Syntax.

---

## Wiwitan Cepet

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

Luwih becik nganggo konstanta `GS1Constants_AICodes` tinimbang string AI mentah (deleng [Lampiran A ing pandhuan parser](GaiaParser-Javanese.md#lampiran-a--konstanta-string-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Kepiye Cara Kerjane

Saben pambangunan ngliwati dalan sing padha:

1. **Nyusun** — pasangan AI lan nilai disambung dadi string elemen sing dicalonake. Pamisah kelompok FNC1 (`0x1D`) disisipake sawise saben AI sing *mbutuhake pamisah* lan dudu elemen pungkasan. AI sing dawane wis ditemtokake sadurunge (GTIN, tanggal, ukuran sing dawane tetep) ora njupuk pamisah; liyane kabeh njupuk. (AI sing ora dikenal ora bakal tekan langkah iki — `ai(...)` nolak kabeh mau sanalika; deleng [Mbangun String Elemen](#mbangun-string-elemen).)
2. **Validasi** — calone di-parse ing modhe `CONTENT` liwat `GaiaParser`. Saben nilai dipriksa marang format lan angka centhang duweke AI-ne, lan aturan susunan (pasangan AI sing dibutuhake/dilarang) ditrapake. Yen parsinge ora sah, pambangunan gagal.
3. **Ngetokake** —
   - Kanggo string elemen, `toElementString()` saka objek sing wis disahake dibalekake.
   - Kanggo Digital Link, saben elemen diwenehi peran DL-e (kunci utama, key qualifier, utawa data attribute), urutan key qualifier disahake, URI dietokake, banjur URI sing wis dietokake mau **di-parse maneh kanggo mesthekake yen bisa bali dadi Digital Link sing sah** — pamriksan pangreksa marang langkah panyusunan string lan percent-encoding. Yen ora bisa bali, `GaiaBuilderException` diuncalake.

Iki nyerminake logika panyusunan maneh ing `DLSyntaxParser`, mula panyelehan pamisah lan validasine padha persis karo sing diarep-arep parser.

---

## Mbangun String Elemen

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** disahake sanalika: `ai(...)` nguncalake `IllegalArgumentException` yen iku dudu GS1 Application Identifier sing diakoni. (Builder nyambung AI lan nilai sadurunge parsing, mula AI sing ora dikenal utawa kedawan kayata `"99999"` kudu dicekel ing kene — yen ora, iku bakal meneng-menengan diowahi dadi AI liya nalika tokenisasi.) **Nilai**-ne disahake mengko, nalika pambangunan.
- Nilai kudu **jangkep**, kalebu angka centhang apa wae. Builder ora ngetung utawa nambahake angka centhang kanggo kowe — deleng [Angka Centhang](#angka-centhang).
- AI dietokake miturut urutan sing kokwuwuhake. Builder sing nyisipake pamisah FNC1 ing ngendi sintaksis GS1 mbutuhake; kowe ora perlu nambahake pamisah dhewe.
- Mbangun **tanpa AI babar pisan** nguncalake `GaiaBuilderException("No AIs supplied")` kanthi dhaptar `getErrors()` sing kothong — iki siji-sijine kegagalan sing ora nggawa `GaiaError`.
- AI sing nilaine ora lulus aturan format utawa angka centhange njalari pambangunan gagal.

### AI atribut mbutuhake kunci identifikasine

Akeh-akehe AI iku *atribut* sing miturut GS1 General Specifications kudu dibarengi kunci identifikasi, lan builder ngetrapake iku — iku nyahake liwat tahap sintaksis sing jangkep, tanpa dalan kanggo metu. Bets/lot utawa serial sing dhewekan iku **dudu** string elemen sing sah:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Kunci identifikasi (GTIN `01`, SSCC `00`, GLN `414`, …) lan AI internal perusahaan (`90`–`99`) pancen bisa ngadeg dhewe kanthi sah. Liyane kabeh mbutuhake kancane.

> `GaiaParser` bisa dikon ngliwati pamriksan iki nganggo `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` kanthi sengaja ora nyedhiyakake sing padha — iku digawe supaya ngetokake asil sing manut standar. Kanggo nyusun string elemen sing pancen sengaja ora jangkep, sambungen dhewe banjur parsing-en kanthi pamriksane dipateni.

---

## Mbangun Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Digital Link sing sah mbutuhake persis siji **kunci identifikasi utama** (tuladha, GTIN `01`, GLN `414`, SSCC `00`). Builder nggolongake saben AI sing disedhiyakake:

| Peran | Kepiye dietokake | Tuladha |
|------|-------------|---------|
| Kunci identifikasi utama | Segmen path sawise domain/prefiks | `/01/09506000134352` |
| Key qualifier (CPV `22`, bets `10`, serial `21`, …) | Segmen path sabanjure, ing **urutan kanonik §4.9** (dudu urutan sing kokwuwuhake) | `/10/LOT-ABC` |
| Data attribute (liyane kabeh) | Parameter query, **diurutake miturut aksara kunci AI** (§4.12) | `?17=271231` |

Amarga qualifier diurutake maneh nalika dietokake, ora dadi masalah yen kowe menehake ora urut — `ai("21", …)` sadurunge `ai("10", …)` tetep ngetokake `/10/LOT/21/SER`. Mung *himpunane* sing kudu ditampa dening kunci utama.

Nilai ing path lan ing query padha-padha di-percent-encode.

Pambangunan **gagal** (nguncalake `GaiaBuilderException`, utawa mbalekake `BuildResult` sing gagal) yen:

- **ora ana** kunci identifikasi utama ing antaraning AI;
- ana kunci identifikasi utama **luwih saka siji**;
- ana AI sing **dilarang** ing Digital Link (`03`, `8014`);
- **urutan key qualifier**-e ora sah kanggo kunci utama sing dipilih (tuladha, qualifier sing dudu duweke kunci kasebut, utawa qualifier sing ora manut urutan sing diidini).

---

## BuilderDigitalLinkConfig

Lewatna `BuilderDigitalLinkConfig` kanggo ngatur scheme, domain, prefiks path, parameter query tambahan, lan fragmen:

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

| Metode builder | Gunane | Standar |
|----------------|---------|---------|
| `scheme(String)` | Scheme URI; kudu `http` utawa `https` | `https` |
| `domain(String)` | Otoritas — host utawa `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Segmen path sadurunge kunci utama sepisanan; garis miring ngarep/mburi dinormalake | *(ora ana)* |
| `baseUrl(String)` | Cara gampang sing misahake URL dadi `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Parameter query tambahan, disambung **sawise** data attribute AI, miturut urutan sisipan; di-percent-encode | — |
| `fragment(String)` | Fragmen URL (tanpa `#` ngarep); di-percent-encode | *(ora ana)* |

`build()` nyahake konfigurasine sanalika: scheme sing dudu `http(s)` utawa domain sing kothong nguncalake `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) iku standar `https://id.gs1.org` tanpa tambahan apa-apa — persis sing dienggo `buildDigitalLinkUri()` tanpa gardha, lan sing digawe `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, prefiks path `/r`.
- Parameter query tambahan tansah ngetutake atribut sing asale saka AI, mula urutan AI sing kanonik (§4.12) tetep kajaga.

`BuilderDigitalLinkConfig` ora bisa diowahi; enggonen maneh siji instance kanthi bebas.

---

## Validasi lan Kesalahan

### Metode pambangunan sing nguncalake exception

`buildElementString()`, `buildDigitalLinkUri()`, lan `buildDigitalLinkUri(BuilderDigitalLinkConfig)` nguncalake **`GaiaBuilderException`** (`RuntimeException` sing ora dipriksa) yen AI-ne ora bisa mbentuk output sing bener:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Kanggo kegagalan **isi** (angka centhang kleru, format ora cocog, AI sing ora ana/dilarang), `getErrors()` nggawa `GaiaError` duweke parser — objek sing padha karo sing [didokumentasekake ing pandhuan parser](GaiaParser-Javanese.md#gaiaerror).
- Kanggo kegagalan **susunan Digital Link** (ora ana kunci utama, kunci utama luwih saka siji, AI sing dilarang, urutan key qualifier sing ora sah), `getErrors()` nggawa siji `GaiaError` (kode `GE-L008`, `GE-L012`, `GE-L013`, utawa `GE-L014`) sing wis dilokalake miturut basa builder.

### Metode tryBuild\* sing ora nguncalake exception

Yen inpute saka pangguna lan kegagalan iku asil sing wis dikira-kira lan bisa dipulihake, nganggoa varian `tryBuild*` tinimbang ngatur alur nganggo exception. Kabeh mau mbalekake [`BuildResult`](#buildresult) tinimbang nguncalake:

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

| Nguncalake | Ora nguncalake |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Saben metode `tryBuild*` nduweni inti validasi sing padha karo kembarane sing nguncalake; mung wates kegagalane sing beda.

### Basa pesen kesalahan

Kesalahan validasi isi dijupuk saka katalog kesalahan sing wis dilokalake. Celuken `language(...)` kanggo milih basa pesen `GaiaError` sing digawa `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; standare basa Inggris:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Iki setelan `GaiaConstants.Language` sing padha karo sing ditampa `GaiaParser` liwat `ParseConfig`, mula builder lan parser nglokalake kanthi cara sing padha persis.

Pesen `GaiaError` **isi** lan kegagalan **susunan Digital Link** (ora ana kunci utama, kunci utama luwih saka siji, AI sing dilarang, urutan key qualifier sing ora sah) loro-lorone dilokalake liwat katalog kesalahan sing dienggo bareng — sing kapindho nganggo kode `GE-L008`, `GE-L012`, `GE-L013`, lan `GE-L014`.

### BuildResult

`BuildResult` (ing paket `tools.pantheum.gaia.result`) iku tipe nilai sing ora bisa diowahi sing nyritakake asile panyeluk `tryBuild*`:

| Metode | Yen kasil | Yen gagal |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | string sing dietokake | `null` |
| `getMessage()` | `null` | katrangan kegagalan |
| `getErrors()` | dhaptar kothong | kesalahan validasi (padha karo `GaiaBuilderException.getErrors()`) |

---

## Angka Centhang

Builder nyahake angka centhang nanging **ora** ngetung — nilai kudu wis kalebu angka centhange. Kanggo ngetung siji, nganggoa `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` ngetrapake algoritma modulo-10 GS1 sing lumrah marang angka sing diwenehake lan mbalekake angka centhang `0–9`, utawa `-1` yen inpute null, kothong, utawa dudu angka.

---

## Aman kanggo Thread

`GaiaBuilder` **ora** aman kanggo thread lan dienggo mung sepisan: celuken `create()`, tambahna AI, bangunen sepisan. Gaweya builder anyar kanggo saben output; aja nganggo siji bebarengan ing pirang-pirang thread.

`BuilderDigitalLinkConfig` (lan output `BuildResult`-e) ora bisa diowahi lan kena dienggo bareng kanthi bebas — gaweya konfigurasi sepisan nalika wiwitan banjur enggonen maneh ing akeh builder.

---

## Referensi API

### `GaiaBuilder`

| Metode | Katrangan |
|--------|-------------|
| `static GaiaBuilder create()` | Miwiti builder anyar sing kothong. |
| `GaiaBuilder ai(String ai, String value)` | Nambahake siji AI lan nilaine sing jangkep. Nguncalake `IllegalArgumentException` yen salah sijine `null`, utawa yen `ai` dudu GS1 Application Identifier sing diakoni. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Nyetel basa pesen kesalahan validasi isi (standare Inggris). `null` diabaikan. |
| `String buildElementString()` | Ngetokake string elemen GS1. Nguncalake `GaiaBuilderException` yen gagal. |
| `String buildDigitalLinkUri()` | Ngetokake Digital Link URI kanonik. Nguncalake `GaiaBuilderException` yen gagal. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Ngetokake Digital Link URI miturut `config`. Nguncalake `GaiaBuilderException` yen gagal. |
| `BuildResult tryBuildElementString()` | Pambangunan string elemen tanpa nguncalake exception. |
| `BuildResult tryBuildDigitalLinkUri()` | Pambangunan Digital Link kanonik tanpa nguncalake exception. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Pambangunan Digital Link miturut `config` tanpa nguncalake exception. |

### `BuilderDigitalLinkConfig`

| Anggota | Katrangan |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Standar `https://id.gs1.org`. |
| `static Builder builder()` | Builder konfigurasi sing anyar. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Scheme, otoritas, lan prefiks path sing wis dirampungake. |
| `getExtraQueryParams()` | Parameter query tambahan, miturut urutan sisipan. |
| `getFragment()` | Fragmen, utawa `null`. |

### `GaiaBuilderException`

| Anggota | Katrangan |
|--------|-------------|
| `getErrors()` | `GaiaError` sing njalari kegagalan — kesalahan parser kanggo kegagalan isi, utawa siji kesalahan susunan Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Ora tau `null`. |

### `BuildResult`

| Anggota | Katrangan |
|--------|-------------|
| `isSuccess()` | Apa pambangunane kasil. |
| `getValue()` | Output sing dietokake yen kasil; `null` yen gagal. |
| `getMessage()` | Katrangan kegagalan yen gagal; `null` yen kasil. |
| `getErrors()` | Kesalahan validasi yen gagal; kothong yen kasil. Ora tau `null`. |
| `getTiming()` | `ProcessingTiming` kanggo pambangunane (wektu wiwitan, suwene pangolahan), utawa `null`. |

---

Deleng uga: **[GaiaParser — Pandhuan kanggo Pangembang](GaiaParser-Javanese.md)** kanggo sisi parsing, model elemen AI, referensi kesalahan, lan lampiran konstanta AI lan interpretasi.
