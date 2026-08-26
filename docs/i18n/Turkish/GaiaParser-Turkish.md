# GAIA (GS1 Application Identifiers Analyser) — Geliştirici Kılavuzu

## İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [GS1 ve General Specifications hakkında](#gs1-ve-general-specifications-hakkında)
3. [GS1 Uygulama Tanımlayıcıları](#gs1-uygulama-tanımlayıcıları)
4. [Hızlı Başlangıç](#hızlı-başlangıç)
5. [Ayrıştırma İşlem Hattı](#ayrıştırma-işlem-hattı)
   - [Ön aşama — Girdi Değiştiricileri](#ön-aşama--girdi-değiştiricileri)
   - [Aşama 0 — İlişkilendirme Kimliği](#aşama-0--ilişkilendirme-kimliği)
   - [Aşama 1 — Girdi Yönlendirme](#aşama-1--girdi-yönlendirme)
   - [Aşama 2 — Sözdizimi](#aşama-2--sözdizimi)
   - [Aşama 3 — İçerik](#aşama-3--içerik)
   - [Aşama 4 — Yorum](#aşama-4--yorum)
6. [Ayrıştırma Yapılandırması (`ParseConfig`)](#ayrıştırma-yapılandırması-parseconfig)
   - [Seçenekler](#seçenekler)
   - [Yerelleştirilmiş iletiler ve etiketler](#yerelleştirilmiş-iletiler-ve-etiketler)
   - [Tarih biçimlendirme](#tarih-biçimlendirme)
7. [Girdi Değiştiricileri](#girdi-değiştiricileri)
   - [Yerleşik değiştiriciler](#yerleşik-değiştiriciler)
   - [Bir değiştirici yazmak](#bir-değiştirici-yazmak)
   - [Değiştiricileri kaydetme](#değiştiricileri-kaydetme)
   - [Bir değiştiricinin ne yaptığını incelemek](#bir-değiştiricinin-ne-yaptığını-incelemek)
   - [Değiştirici başarısızlığının işlenmesi](#değiştirici-başarısızlığının-işlenmesi)
8. [Ayrıştırma Kipleri](#ayrıştırma-kipleri)
   - [DATA_CARRIER kipi](#data_carrier-kipi)
   - [SYNTAX kipi](#syntax-kipi)
   - [CONTENT kipi](#content-kipi)
   - [INTERPRETATION kipi (öntanımlı)](#interpretation-kipi-öntanımlı)
9. [İlişkilendirme Kimliği](#ilişkilendirme-kimliği)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Sonuçlarla Çalışmak](#sonuçlarla-çalışmak)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry ve DataCarrierType](#datacarrierentry-ve-datacarriertype)
12. [Hata Başvurusu](#hata-başvurusu)
13. [İş Parçacığı Güvenliği](#iş-parçacığı-güvenliği)
14. [Ek A — AI Dizi Sabitleri](#ek-a--ai-dizi-sabitleri)
    - [Tanımlama ve Serileştirme](#tanımlama-ve-serileştirme)
    - [Tarihler ve Saatler](#tarihler-ve-saatler)
    - [Miktar ve Ölçü — Değişken Ölçü (Metrik)](#miktar-ve-ölçü--değişken-ölçü-metrik)
    - [Miktar ve Ölçü — Değişken Ölçü (İngiliz / ABD)](#miktar-ve-ölçü--değişken-ölçü-ingiliz--abd)
    - [Fiyatlandırma ve Parasal Tutarlar](#fiyatlandırma-ve-parasal-tutarlar)
    - [Konum ve Sevkiyat](#konum-ve-sevkiyat)
    - [Ürün Öznitelikleri ve İzlenebilirlik](#ürün-öznitelikleri-ve-izlenebilirlik)
    - [Ulusal Sağlık Geri Ödeme Numaraları (NHRN)](#ulusal-sağlık-geri-ödeme-numaraları-nhrn)
    - [Sağlık Hizmetleri, GMN, HIDRI, CPID ve Kişi Verileri](#sağlık-hizmetleri-gmn-hidri-cpid-ve-kişi-verileri)
    - [İç / Firma Kullanımı](#iç--firma-kullanımı)
15. [Ek B — Yorum Anahtarı Sabitleri](#ek-b--yorum-anahtarı-sabitleri)
    - [Tarih ve Saat](#tarih-ve-saat)
    - [Hasat Tarihi](#hasat-tarihi)
    - [GS1 Firma Öneki](#gs1-firma-öneki)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Ülke (ISO 3166)](#ülke-iso-3166)
    - [Para Birimi (ISO 4217)](#para-birimi-iso-4217)
    - [Sıcaklık](#sıcaklık)
    - [Cinsiyet (ISO 5218)](#cinsiyet-iso-5218)
    - [Su Ürünleri Türleri (FAO)](#su-ürünleri-türleri-fao)
    - [NATO Stok Numarası (NSN)](#nato-stok-numarası-nsn)
    - [Rulo Ürünler](#rulo-ürünler)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM Tanımlayıcıları (EID / ICCID)](#sim-tanımlayıcıları-eid--iccid)
    - [Sertifikasyon Referansı](#sertifikasyon-referansı)
    - [GS1 UIC](#gs1-uic)
    - [Bebek Doğum Sırası](#bebek-doğum-sırası)
    - [Global Model Numarası (GMN)](#global-model-numarası-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Ondalık ve Ölçü Değerleri](#ondalık-ve-ölçü-değerleri)
    - [Coğrafi Koordinatlar](#coğrafi-koordinatlar)
    - [Üretim Yöntemi](#üretim-yöntemi)
    - [AIDC Ortam Türü](#aidc-ortam-türü)
    - [Toplamdan Parça](#toplamdan-parça)
    - [Bileşen Ayrışmaları](#bileşen-ayrışmaları)
    - [Çeşitli](#çeşitli)

---

## Genel Bakış

`GaiaParser`, GS1 Uygulama Tanımlayıcı (AI) eleman dizilerini ayrıştırmanın giriş noktasıdır. Tarayıcının ham çıktısını aşağıdaki biçimlerin herhangi birinde kabul eder ve çözümlenmiş tüm AI'ları, doğrulama hatalarını ve isteğe bağlı olarak insan tarafından okunabilir yorumları içeren yapılandırılmış bir `ParseResult` döndürür:

- Yalın AI eleman dizisi: `0109506000134352`
- AIM Semboloji Tanımlayıcısı ile öneklenmiş eleman dizisi: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Yukarıdakilerden herhangi biri, isteğe bağlı olarak 8 haneli bir ilişkilendirme kimliğiyle öneklenmiş: `12345678~0109506000134352`

**Giriş noktası sınıfı:** `tools.pantheum.gaia.GaiaParser`

> **Gaia'yı yeni mi tanıyorsunuz?** **[GaiaParser Hızlı Başlangıç](GaiaParser-QuickStart-Turkish.md)** ile başlayın — bağımlılıklar, ilk ayrıştırma ve en sık karşılaşılan tuzaklar üzerinden geçen on dakikalık bir tur. Bu kılavuz ise eksiksiz başvuru belgesidir.

> Bunun tersi yön — AI/değer çiftlerinden geçerli eleman dizileri ve Digital Link URI'leri *oluşturmak* — **[GaiaBuilder — Geliştirici Kılavuzu](GaiaBuilder-Turkish.md)** belgesinde ele alınır.

---

## GS1 ve General Specifications hakkında

**GS1**, tedarik zinciri tanımlaması ve veri değişimi için açık standartlar geliştiren ve sürdüren küresel, kâr amacı gütmeyen bir kuruluştur. Standartları perakende, sağlık hizmetleri, lojistik, yiyecek-içecek hizmetleri ve daha birçok sektörde kullanılır; tüketici ambalajlarındaki ürün barkodlarından ilaç dozlarının seri numarayla izlenmesine dek her şeyi kapsar.

Bu ayrıştırıcının uyguladığı her şeyin yetkili kaynağı **GS1 General Specifications**'tır — şunları tanımlayan tek bir belge:

- Tüm Uygulama Tanımlayıcı (AI) kodları, veri başlıkları, biçimleri ve doğrulama kuralları
- AI eleman dizilerinin oluşturulması ve kodlanmasına ilişkin sözdizimi kuralları
- Barkod semboloji gereksinimleri ve AIM Kod Kimliği atamaları
- Kontrol hanesi ve kontrol karakteri algoritmaları
- İki haneli yıl çözümlemesi (kayan pencere kuralı)
- Data Matrix, QR Code, GS1-128, GS1 DataBar ve diğer taşıyıcı belirtimleri

GS1 General Specifications her yıl güncellenir. Güncel baskı ve destekleyici kaynaklar şu adreste bulunabilir:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications'ın **Sürüm 26.0 (Onaylandı, Oca 2026)** düzeyini uygular.

GS1 Digital Link URI'leri, ayrıştırıcının Digital Link girdilerine uyguladığı birincil tanımlama anahtarlarını, anahtar niteleyici sırasını ve veri özniteliği kodlamasını tanımlayan tamamlayıcı bir standarda, **GS1 Digital Link: URI Syntax**'a tabidir:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax standardının **Sürüm 1.7.0 (Onaylandı, Ağu 2026)** düzeyini uygular.

Bu belge boyunca bölüm göndermeleri GS1 General Specifications'a işaret eder (ör. "Table 7-5", "section 7.12"); yalnızca Digital Link bölüm numaraları (ör. "§4.9", "§4.12") bunun dışındadır ve GS1 Digital Link: URI Syntax standardına işaret eder.

---

## GS1 Uygulama Tanımlayıcıları

**GS1 Uygulama Tanımlayıcısı (AI)**, hemen ardından gelen verinin anlamını ve biçimini belirleyen kısa bir sayısal önektir — iki ila dört hane. AI'lar GS1 General Specifications'ta tanımlanır ve tedarik zinciri verilerinin geniş bir yelpazesini kapsar: ürün tanımlayıcıları, tarihler, miktarlar, lot numaraları, seri numaraları, ölçümler, URL'ler ve daha fazlası.

### Bir AI elemanının yapısı

Her AI elemanı iki bölümden oluşur:

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

AI kodu her zaman sayısaldır. Veri değeri hemen ardından gelir; kod ile değer arasında hiçbir sınırlandırıcı bulunmaz.

### Sabit uzunluklu ve değişken uzunluklu AI'lar

AI'lar iki kategoriye ayrılır:

| Tür | Davranış | Örnek |
|---|---|---|
| **Sabit uzunluklu** | Tam olarak belirli sayıda karakter; her zaman eksiksiz okunur | AI `01` (GTIN) — her zaman 14 hane |
| **Değişken uzunluklu** | 1 karakterden en fazla sayıya kadar; bir GS ayırıcısı ya da girdinin sonuyla biter | AI `10` (Parti/Lot) — 1 ila 20 alfasayısal karakter |

Bir AI'ın sabit mi yoksa değişken mi olduğu yalnızca GS1 belirtimindeki tanımıyla belirlenir — ayrıştırıcı asla tahminde bulunmaz.

### Çok AI'lı eleman dizileri

Birden çok AI tek bir eleman dizisinde birleştirilebilir. Sabit uzunluklu AI'lar doğrudan birleştirilebilir, çünkü ayrıştırıcı kaç karakter okuyacağını her zaman tam olarak bilir. Değişken uzunluklu AI'ların ardından başka bir AI geldiği her durumda, bu AI'ların **GS karakteriyle** (ASCII `0x1D`; barkod sembolojilerinde FNC1 olarak da bilinir) sonlandırılması zorunludur; ancak böylece ayrıştırıcı bir değerin nerede bitip bir sonraki AI kodunun nerede başladığını bilebilir.

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

Java dizi sabitlerinde GS karakterini `""` Unicode kaçışıyla yazın.

### Yaygın AI'lar

| AI | Veri başlığı | Biçim | Örnek değer |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34; tek para birimi alanı) |
| `710` | NHRN PZN | X..20 | `12345678` |

> 4 haneli bir ölçü ya da fiyat AI'ının **dördüncü hanesi**, örtük ondalık basamak sayısını kodlar — `3103`, 3 ondalıklı kilogram cinsinden net ağırlıktır (`001500` = 1,500 kg); `3102` ise aynı haneleri 15,00 kg olarak okur. Yukarıdaki `Biçim` sütunu *verinin* biçimini gösterir; her AI'ın tam `getFormatString()` değeri AI'ın kendisini de içerir (ör. `3103` için `N4+N6`).

### İnsan Tarafından Okunabilir Yorum (HRI)

Alışılagelmiş okunabilir biçim, her AI kodunu değerinin hemen öncesinde parantez içine alır ve elemanlar arasına bir boşluk koyar:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS ayırıcısı HRI'de gösterilmez. Bu biçimi `GS1AIObject.toHriString()` üretir.

### Dört haneli AI kodları

Bazı AI'lar iki yerine dört hane kullanır. İlk iki hane AI ailesini belirtir; üçüncü ve/veya dördüncü hane ek anlam taşır (ölçü AI'larındaki örtük ondalık ayırıcı konumu gibi). Ayrıştırıcı tam AI kodunu eleman dizisinden kendiliğinden çözer — çağıran taraf her zaman tam kodla çalışır (ör. yalnızca `"31"` değil, `"3102"`).

---

## Hızlı Başlangıç

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

> **GS ayırıcısı:** Çok AI'lı bir dizideki değişken uzunluklu AI'lar GS karakteriyle (ASCII `0x1D`) sınırlandırılmalıdır. Java dizi sabitlerinde `""` kullanın.

---

## Ayrıştırma İşlem Hattı

### Ön aşama — Girdi Değiştiricileri

`ParseConfig` herhangi bir **girdi değiştiricisi** taşıyorsa, bunlar her şeyden önce çalışır — ilişkilendirme öneki ayıklanmadan önce, taşıyıcı algılanmadan önce, GS1 işlem hattına girilmeden önce. Her değiştirici ham girdiyi bir sonraki için yeniden yazar ve aşağıdaki tüm aşamalar bu zincirin çıktısı üzerinde işlem yapar.

Öntanımlı olarak hiçbir değiştirici yapılandırılmamıştır; dolayısıyla siz açıkça seçmedikçe bu ön aşama hiçbir şey yapmaz. Bkz. [Girdi Değiştiricileri](#girdi-değiştiricileri).

---

### Aşama 0 — İlişkilendirme Kimliği

Herhangi bir GS1 işlemesinden önce `GaiaParser`, girdinin isteğe bağlı bir **ilişkilendirme kimliği öneki** ile başlayıp başlamadığını denetler: tam olarak 8 ASCII ondalık hane ve ardından bir tilde (`~`), ör. `12345678~`.

Önek varsa ayıklanır ve döndürülen `ParseResult` üzerinde bir `CorrelationInfo` olarak saklanır. Sonraki tüm aşamalar ayıklanmış yük üzerinde işlem yapar. Önek yoksa girdi olduğu gibi geçer.

Ayrıntılar için bkz. [İlişkilendirme Kimliği](#ilişkilendirme-kimliği).

---

### Aşama 1 — Girdi Yönlendirme

İlişkilendirme öneki ayıklandıktan sonra `GaiaParser`, (ayıklanmış) girdinin bir **AIM Kod Kimliği** ile başlayıp başlamadığını denetler: `]` + ASCII harf + ASCII hane biçiminde üç karakterlik bir önek (ör. GS1-128 için `]C1`, GS1 DataMatrix için `]d2`, GS1 DataBar / GS1 Composite için `]e0`).

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

Taşıyıcı GS1 AI taşıyamıyorsa (ör. bir posta barkodu), ayrıştırma anında bir `GE-D002` hatasıyla durur.

---

### Aşama 2 — Sözdizimi

Koşulsuz çalışır. İki alt adımdan oluşur:

**2a. Belirteçleme (`AISyntaxParser`)**
- GS1 önek tablosunu (GS1 General Specifications Table 7-5) kullanarak ilk iki karakterden AI kod uzunluğunu okur.
- Sabit uzunluklu AI'lar girdiden tam olarak belirli sayıda bayt okur.
- Değişken uzunluklu AI'lar bir GS karakterine ya da girdinin sonuna dek okunur.
- Çok bileşenli AI'ların değer bloğu bileşen başına parçalara ayrılır.

**2b. Yapısal doğrulama (`SyntaxValidator`)**
- Yinelenen AI'ları denetler (`GE-S004`).
- Zorunlu AI bağımlılıklarını denetler; ör. AI `02`, AI `37`'yi gerektirir (`GE-S005`).
- Birlikte bulunamayan AI eşleşmelerini denetler (`GE-S006`).

Bu aşamadaki hatalar `SYNTAX_ERROR` (belirteçleyici) ya da `INTEGRITY_ERROR` (yapısal) düzeyindedir. **Herhangi bir** hata varsa — belirteçleyici ya da yapısal — işlem hattı durur ve içerik ile yorum aşamaları atlanır.

---

### Aşama 3 — İçerik

Yalnızca Aşama 2 hiçbir hata üretmediyse çalışır (ne belirteçleyici ne de yapısal). Eleman başına işlem hattı (her adım yalnızca bir öncekinde hata çıkmadıysa çalışır):

| Adım | Doğrulayıcı | Hata Kodları |
|---|---|---|
| Düzenli ifade denetimi | `RegexValidator` | `GE-C001` |
| Bileşen karakter kümesi + biçim | `ComponentValidator` | `GE-C005` + koşul başına biçim kodları (`GE-C054`–`GE-C115`) |
| Kontrol hanesi / kontrol karakteri | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Özel anlamsal doğrulama | `ContentValidatorRegistry` | koşul başına içerik kodları (`GE-C116`–`GE-C170`) |

Bu aşamadaki hatalar `FORMAT_ERROR` ya da `DATA_ERROR` düzeyindedir; tek bir istisnayla: GS1
anahtarlı AI'lardaki GS1 firma öneki denetimleri bilgilendirme amaçlıdır ve `WARNING` düzeyi
taşır (bkz. [Hata Başvurusu](#hata-başvurusu)); dolayısıyla tanınmayan bir firma öneki tek
başına sonucu geçersiz kılmaz.

---

### Aşama 4 — Yorum

Yalnızca `INTERPRETATION` kipinde ve yalnızca hiçbir eleman önceki aşamalardan hata taşımıyorsa çalışır. `InterpretationEngine` her elemanı etiketli üstverilerle zenginleştirir:

- `dd/mm/yyyy` olarak yeniden biçimlendirilmiş tarihler
- GTIN kontrol hanesi ayrıştırması ve GS1 firma öneki araması
- ISO 3166 ülke adları
- ISO 4217 para birimi adları ve simgeleri
- Kodu çözülmüş ondalık tutarlar
- HRI (İnsan Tarafından Okunabilir Yorum) parçaları

Sonuçlar her `GS1AIObjectElement` üzerine `GS1AIInterpretation` girdileri olarak eklenir.

---

## Ayrıştırma Yapılandırması (`ParseConfig`)

`GaiaParser` tam olarak iki giriş noktası sunar:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)`, **öntanımlı yapılandırmayla** çalışır: `INTERPRETATION` kipi, `/` ayırıcılı ve dört haneli yıllı küçük-sonlu tarihler (`dd/mm/yyyy`) ve **İngilizce** hata iletileri. Bunlardan herhangi birini — ayrıştırma kipi dahil — değiştirmek için akıcı oluşturucusuyla bir `ParseConfig` kurun ve iki bağımsız değişkenli biçimi kullanın.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Seçenek numaralandırmalarının tümü `GaiaConstants` içindedir.

### Seçenekler

| Oluşturucu yöntemi | Numaralandırma (`GaiaConstants`) | Öntanımlı | Etkisi |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | İşlem hattı derinliği — bkz. [Ayrıştırma Kipleri](#ayrıştırma-kipleri). |
| `language(...)`      | `Language`      | `ENGLISH`        | Hata iletilerinin, yorum etiketlerinin **ve** AI açıklamalarının dili. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Tarih bileşeni sırası: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Tarih bileşenleri arasındaki karakter: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) ya da `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) ya da `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Yapısal "gerektirir" denetimini (`GE-S005`) atlar. |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Yapısal "dışlar" denetimini (`GE-S006`) atlar. |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / sınıf adı | yok | Ayrıştırmadan önce ham girdiyi yeniden yazan kod — iki [yerleşik değiştirici](#yerleşik-değiştiriciler) ve sizin yazdıklarınız. Bkz. [Girdi Değiştiricileri](#girdi-değiştiricileri). |

Dört tarih seçeneği yalnızca yorum zenginleştiricilerinin ürettiği biçimlendirilmiş tarih dizilerini etkiler (`INTERPRETATION` kipinde); doğrulamayı değiştirmezler. Oluşturucu değerleri atlanabilir — belirlenmemiş (ya da `null` verilmiş) her seçenek öntanımlı değerini korur.

### Yerelleştirilmiş iletiler ve etiketler

`language(...)`, insan tarafından okunabilir **üç** tür metnin dilini seçer: hata iletileri, yorum etiketleri (her `GS1AIInterpretation` için `getLabel()`) ve AI açıklamaları (her `GS1AIObjectElement` için `getDescription()`).

`GaiaConstants.Language` dünyanın en çok konuşulan dillerini kapsayan **35 dil** tanımlar: İngilizce, Fransızca, İspanyolca, Almanca, İtalyanca, Portekizce, Felemenkçe, Lehçe, Rusça, Ukraynaca, Çekçe, İsveççe, Çince, Japonca, Korece, Arapça, Endonezce, Hintçe, Türkçe, Bengalce, Urduca, Vietnamca, Nijerya Pidgin'i, Mısır Arapçası, Marathi, Telugu, Tamil, Kantonca, Wu Çincesi, Tagalogca, Farsça, Hausa, Pencapça, Cava dili ve Svahili.

Çeviri durumu (dağıtıldığı haliyle):
- **Yorum etiketleri** — tüm diller için çevrilmiştir.
- **Hata iletileri** — tüm diller için çevrilmiştir.
- **AI açıklamaları** — İngilizce dışındaki tüm diller için çevrilmiştir. İngilizce ayrı bir katalog değildir: doğrudan `gs1-application-identifiers.jsonld` içindeki AI girdisinin `description` alanından okunur ve her AI açıklaması nihayetinde buna geri döner.

İngilizce temelli bir kreol olan Nijerya Pidgin'i (`NIGERIAN_PIDGIN`), yorum etiketleri ve hata iletileri için bilinçli olarak İngilizce metni yeniden kullanır. AI açıklamaları bu istisnanın istisnasıdır: İngilizceyi yeniden kullanmak yerine gerçek Pidgin ifadelerine çevrilmişlerdir, çünkü AI açıklama katalogları etiket/ileti kataloglarından bağımsız olarak üretilmiştir. Makine çevirilerine üretim ortamında güvenmeden önce anadili konuşanlarca gözden geçirilmeleri yerinde olur.

Bir dilin kataloğunda bulunmayan herhangi bir ileti, etiket ya da açıklama İngilizceye geri döner. Sağdan sola yazılan diller (Arapça, Urduca, Mısır Arapçası, Farsça) dizi olarak doğru biçimde saklanır; bunları sağdan sola görüntülemek görüntüleme katmanının sorumluluğundadır.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Yorum etiketleri de aynı biçimde yerelleştirilir (değerler değişmez — yalnızca etiketler):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI açıklamaları da aynı biçimde yerelleştirilir (yalnızca `getTitle()`, ör. `"GTIN"`, yerelleştirilmez):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Tarih biçimlendirme

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Girdi Değiştiricileri

**Girdi değiştiricisi**, Gaia ayrıştırmadan önce ham girdi dizisini yeniden yazan koddur. Değiştiriciler, hâlihazırda bozulmuş olarak gelen girdiler için vardır — GS ayırıcısının yerine yazdırılabilir bir yer tutucu koyan bir tarayıcı, yükü satıcıya özgü bir önekle saran bir ara katman, her şeyi büyük harfe çeviren bir ana sistem. Her çağrı yerinde her diziyi tek tek ön işlemek (ve bunlardan birinde ince bir hata yapmak) yerine, normalleştirmeyi `ParseConfig` üzerinde bir kez kaydedin ve uygulamasını ayrıştırıcıya bırakın.

Değiştiriciler `GaiaParser.parse(...)` çağrısının en başında çalışır — ilişkilendirme kimliği ayıklanmadan önce, AIM Kod Kimliği algılanmadan önce, GS1 işlem hattından önce. Sonraki her şey yalnızca yeniden yazılmış diziyi görür. İki [yerleşik değiştirici](#yerleşik-değiştiriciler) dahil, **öntanımlı olarak hiçbir şey yapılandırılmamıştır** — her `ParseConfig` için ayrı ayrı siz seçersiniz.

**Arayüz:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Yerleşik değiştiriciler

Çekirdek jar'da, `tools.pantheum.gaia.modifier.custom` içinde iki değiştirici gelir. Bunlar bir GS1 yükünün en sık bozulduğu iki yolu kapsar — veri sanılan yazdırılmış HRI parantezleri ve yersiz boşluklar — böylece yaygın durumlar için özel bir sınıfa gerek kalmaz:

| Sınıf | `getName()` | Ne yapar |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Her AI'ın çevresindeki HRI parantezlerini (`(01)…(10)…`) ayıklar ve bunların ima ettiği FNC1 ayırıcısını geri koyar. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI eleman dizisindeki her boşluğu (`0x20`) kaldırır. |

Bunlar özel bir konumu olmayan sıradan `ModifierInterface` gerçeklemeleridir — tam olarak sizinkiler gibi kaydedilir, sıralanır, raporlanır ve başarısız olur:

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

İkisi de durumsuzdur ve iş parçacığı güvenlidir; bu yüzden tek bir örnek paylaşılabilir. Ayrıca ikisi de yapılandırma dosyasına dayalı kurulumlar için tam nitelikli sınıf adıyla adreslenebilir (bkz. [Değiştiricileri kaydetme](#değiştiricileri-kaydetme)).

#### `ModifierRemoveAIBrackets`

GS1'in insan tarafından okunabilir yorumu her AI'ı parantez içinde yazdırır — `(01)09521234543213(10)ABC123` — bu tümüyle bir yazdırma geleneğidir. HRI üretecek biçimde yapılandırılmış bir tarayıcı ya da ara katman bu parantezleri veri olarak geçirir ve belirteçleyicinin onlarla ne yapacağına dair hiçbir fikri yoktur.

Parantezleri ayıklamak işin yalnızca yarısıdır. HRI'de bir önceki değerin bittiğini işaretleyen şey *bir sonraki* AI'ın açılan `(` parantezidir; dolayısıyla parantezli biçimde değişken uzunluklu bir AI'ın FNC1'e gereksinimi yoktur. Parantezleri düşünmeden kaldırın, o sınır ortadan kalkar:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Bu yüzden değiştirici, **kendinden önceki AI değişken uzunluklu olan her sınıra yeniden bir FNC1 ekler** ve parantezlerin kodladığı şeyi tam olarak geri getirir:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Uzunluk, ayrıştırıcının kendi `AiDefinitionRegistry` kaydında aranır; böylece sabit kodlanmış bir liste yerine her değişken uzunluklu AI ele alınır. Üç durum bilinçli olarak elden geçirilmez: zaten FNC1 ile biten bir değer (her iki geleneği de üreten bir kaynak ikinci bir ayırıcı almaz), bilinen bir AI olmayan parantezli bir kod (bilinmeyen bir AI kendi uzunluğu hakkında hiçbir şey söylemez) ve dizideki son AI.

Bu yeniden yazma **etkisiz eleman özelliğine** sahiptir — kendi çıktısı üzerinde çalıştırıldığında hiçbir şeyi değiştirmez — bu yüzden yalnızca bazı girdilerin parantezli olduğu karışık bir akışta güvenlidir.

> **Sınırlama.** `(` ve `)` kendileri de geçerli GS1 veri karakterleridir ve kullanılan örüntü yalnızca `\((\d{2,4})\)` biçimindedir. İçinde rastlantı sonucu parantezli iki ila dört haneli bir sayı bulunan bir değerin de parantezleri açılır. Bunu yalnızca HRI parantez geleneğini kullanan bir kaynağa uygulayın; gerçekten parantezli değerler kullanan bir kaynağa değil.

#### `ModifierRemoveSpaces`

Bazı tarayıcılar, ara katmanlar ve etiket yazdırma hatları, aslında düzgün biçimlenmiş bir eleman dizisine yersiz boşluklar sokar — sabit genişlikli bir alanı doldurmak, okunabilir grupları ayırmak ya da uzun bir değeri sarmalamak için. Belirteçleyici bunların her birini veri sayar; böylece içinde bulunduğu değer bozulur ve değişken uzunluklu bir AI söz konusuysa ondan sonraki her şey kayar.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Yalnızca ASCII `0x20` kaldırılır. Diğer boşluk karakterleri yerinde bırakılır — örneğin sekme karakteri GS1'in kodlanabilir kümesinin dışındadır; bu yüzden ayrıştırıcı onu sessizce süpürmek yerine `GE-S008` olarak raporlar.

> **Sınırlama.** Boşluk (`0x20`), GS1'in değişmez karakter kümesinin bir parçasıdır; bu yüzden bir parti/lot ya da müşteri parça numarası meşru biçimde boşluk içerebilir. Değiştirici yersiz bir boşluğu gerçek olandan ayırt edemez; bunu yalnızca AI değerlerinin içinde boşluk kullanmadığı bilinen bir kaynağa uygulayın.

#### Önekler yeniden yazılmaz, atlanır

Değiştiriciler, ayrıştırıcı henüz hiçbir şey ayıklamadan çalışır; bu yüzden ham girdi hâlâ bir ilişkilendirme kimliği, bir AIM Kod Kimliği ve bir ECI göstergesi taşıyor olabilir. Her iki yerleşik değiştirici de AI eleman dizisinin başlangıcını ayrıştırıcının kendi `CorrelationIdParser` ve `DataCarrierParser` mantığıyla bulur, yalnızca oradan itibaren yeniden yazar ve sonucu **dokunulmamış** önekle yeniden birleştirir:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Değeri GTIN-14'e tamamlanan EAN/UPC taşıyıcıları (`isRequiresGtinPadding()`) tümüyle atlanır — yükleri hiçbir AI yapısı olmayan ham sayısal bir barkod değeridir; dolayısıyla orada ne parantezler ne de boşluklar anlamlı olabilir.

#### Sıra: parantezlerden önce boşluklar

İkisi birlikte kullanıldığında **önce `ModifierRemoveSpaces`'i kaydedin**. Parantez eşleştirme konuma duyarlıdır: boşluklarla açılmış bir `( 01 )`, `\((\d{2,4})\)` ile eşleşmez; böylece parantezler yerinde kalır ve ima ettikleri ayırıcı hiçbir zaman geri konmaz.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Bir değiştirici yazmak

Yerleşiklerin ikisi de uymuyorsa kendinizinkini yazın — arayüzde tek bir yöntem var.

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

Yeniden yazma ayrıştırma yapılandırmasına bağlıysa, bunun yerine iki bağımsız değişkenli biçimi geçersiz kılın:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Sözleşme:

| Kural | Ayrıntı |
|---|---|
| Durumsuz ve iş parçacığı güvenli | Sınıf başına tek bir örnek önbelleğe alınır ve her ayrıştırmada paylaşılır. |
| Bağımsız değişkensiz genel kurucu | Yalnızca değiştiriciye sınıf adıyla başvurulduğunda gereklidir. |
| `null` ve boş girdiyi ele alın | Zincir çalışmadan önce ayrıştırıcı bunları elemez. |
| `null` döndürmek "değişiklik yok" demektir | Önceki değer olduğu gibi taşınır. Değiştirici uygulanmıyorsa `input`'u değiştirmeden döndürün. |
| Özel durum fırlatmak yerine değiştirmeden döndürmeyi yeğleyin | Özel durum fırlatan bir değiştirici ayrıştırmayı iptal eder — bkz. [Başarısızlık işleme](#değiştirici-başarısızlığının-işlenmesi). |
| `getName()` | `ModifierInfo` üzerinde raporlanan adı denetlemek için geçersiz kılın; öntanımlı değer yalın sınıf adıdır. |

### Değiştiricileri kaydetme

Değiştiriciler eklendikleri sırayla çalışır ve her biri bir öncekinin çıktısını alır. Bunları örnekle, tam nitelikli sınıf adıyla ya da her ikisinin listesiyle kaydedin:

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

[Yerleşik değiştiriciler](#yerleşik-değiştiriciler) de sizinkilerle aynı biçimde adlandırılır — **her zaman tam nitelikli olarak**. Onlar için kısa ad ya da takma ad araması yoktur; `ModifierRegistry` her değiştiriciyi, ister birlikte gelsin ister gelmesin, tam sınıf adıyla çözer.

Adları `ModifierRegistry` çözer; her sınıfı bağımsız değişkensiz kurucusuyla bir kez örnekler ve aynı sınıfı adlandıran sonraki her yapılandırma için o örneği önbellekte tutar. Çözümleme **yapılandırma kurulurken** gerçekleşir; bu yüzden bulunamayan, `ModifierInterface`'i gerçeklemeyen ya da örneklenemeyen bir ad orada `IllegalArgumentException` fırlatır — ayrıştırma sırasında sessizce değil. Yansımayla kurulamayan bir değiştirici (diyelim ki enjekte edilmiş bir bağımlılık taşıyan biri) önceden kaydedilerek yine de adıyla adreslenebilir kılınabilir:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Bir değiştiricinin ne yaptığını incelemek

Değiştiriciler yapılandırıldığında `ParseResult.getPayload()` **değiştirilmiş** girdiyi yansıtır. Özgün girdi `ModifierInfo` üzerinde korunur:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` her değiştiricinin `getName()` değerini raporlar; bunun öntanımlısı yalın sınıf adıdır, ancak her iki yerleşik değiştirici de bunu geçersiz kılar — dolayısıyla bu ikisinden oluşan bir zincir sınıf adlarını değil, görünen adları raporlar:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

Hiçbir değiştirici yapılandırılmamışsa `getModifierInfo()` `null` döndürür. Değiştiriciler çalıştıysa ama hepsi girdiyi değiştirmeden döndürdüyse, bilgi mevcuttur ve `isModified()` `false`'tur — `getAppliedModifiers()` listesine yalnızca girdiyi gerçekten değiştiren değiştiriciler girer.

### Değiştirici başarısızlığının işlenmesi

Özel durum fırlatan bir değiştirici ayrıştırmayı iptal eder. Özel durum, sorunlu değiştiriciyi adlandıran bir `GaiaModifierException` içine sarılır ve sonuç, iletisinde o adı içeren bir `GE-I001` iç hatası taşır; `getPayload()` değiştirilmemiş girdiyi raporlar. Ayrıştırma bilinçli olarak yarı yeniden yazılmış bir diziyle **devam etmez** — sessizce başarısız olan bir normalleştirme adımı, geçerli görünen ama yanlış girdiden ayrıştırılmış sonuçlar üretirdi.

---

## Ayrıştırma Kipleri

Her kip, çalıştırdığı en derin [işlem hattı aşamasının](#ayrıştırma-işlem-hattı) adını taşır; ondan önceki her aşama yine de çalışır.

| Kip | Nereye kadar çalışır | Neyi yanıtlar |
|---|---|---|
| `DATA_CARRIER` | Aşama 1 (girdi yönlendirme) | Bunu hangi semboloji taşıdı? |
| `SYNTAX` | Aşama 2 (sözdizimi) | AI kodları ve uzunlukları düzgün biçimlenmiş mi? |
| `CONTENT` | Aşama 3 (içerik) | Değerler geçerli GS1 verisi mi? |
| `INTERPRETATION` | Aşama 4 (yorum) | Değerler ne anlama geliyor? |

### DATA_CARRIER kipi

Aşama 1'den sonra durur — AIM Kod Kimliğini doğrular ve sembolojiyi belirler, ancak AI ayrıştırma işlem hattına girmez. Tam doğrulamanın yükü olmadan semboloji belirleme ve yönlendirme için kullanışlıdır.

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

**Şu durumda kullanın:** Uygulamanızın yükü nasıl işleyeceğine karar vermeden önce barkod türünü belirlemesi gerekiyorsa — örneğin 1D ve 2D sembolojileri farklı işleyicilere yönlendirmek için. Bu yönlendirmede `getName()` üzerinde dizi eşleştirmek yerine türlenmiş [`DataCarrierType`](#datacarrierentry-ve-datacarriertype) değerini (`getDataCarrier().getDataCarrierType()`) yeğleyin.

---

### SYNTAX kipi

Aşama 2'den sonra durur. İçerik doğrulamasının maliyeti olmadan yapısal ön eleme için kullanışlıdır.

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

**Şu durumda kullanın:** Tam doğrulamaya girişmeden önce AI kodlarının ve veri uzunluklarının düzgün biçimlendiğinden emin olmak istediğinizde ya da içerik hatalarının ender olduğu yüksek hacimli taramalarda.

---

### CONTENT kipi

Aşama 3'ten sonra durur.

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

> AI'ların çoğu tek başına duramaz: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) ve
> `21` (SERIAL) için her biri, aynı eleman dizisinde AI `01` gibi bir tanımlama anahtarı
> *gerektirir*; dolayısıyla yukarıdaki GTIN'i çıkarmak, içerik doğrulamasına hiç
> ulaşmadan Aşama 2'de `GE-S005` ile başarısız olur. Eşlik eden AI'larını bilinçli
> olarak dışarıda bırakan parçaları ayrıştırmak için `ParseConfig` üzerinde
> `skipRequiresCheck(true)` ayarlayın.

**Şu durumda kullanın:** Taranmış bir değeri bir iş sürecinde kullanmadan önce tümüyle GS1 uyumlu olup olmadığını bilmeniz gerektiğinde, ancak yorum zenginleştirmesinin yükünü istemediğinizde.

---

### INTERPRETATION kipi (öntanımlı)

Tüm işlem hattını Aşama 4'e dek çalıştırır. `parse(String)` kip bağımsız değişkeni olmadan çağrıldığındaki öntanımlıdır. Yalnızca içerik doğrulamasını temiz biçimde geçen elemanları zenginleştirir.

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

**Örnek çıktı:**
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

**Parasal tutar örneği (AI 3932 — ISO para birimi kodlu fiyat):**
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

**Şu durumda kullanın:** Görüntüleme katmanları, etiket doğrulama araçları ya da AI değerlerinin insana dost bir dökümüne gereksinim duyan herhangi bir arayüz oluştururken.

---

## İlişkilendirme Kimliği

Bazı iş akışları, tarama olaylarının bir oturuma ya da işleme bağlanabilmesi için ham GS1 girdisinin önüne 8 haneli, kuruma özgü bir ilişkilendirme tanımlayıcısı ekler. Biçimi şöyledir:

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

`~` (tilde) ayırıcıdır. GS1 içeriğinin bir parçası **değildir** — herhangi bir GS1 ayrıştırması başlamadan önce ayıklanır.

### Algılama kuralları

Önek, girdi tam olarak 8 ASCII ondalık haneyle (`0`–`9`) başlayıp hemen ardından `~` geldiğinde algılanır. 9. karakter `~` değilse ya da ilk 8 karakterden biri hane değilse, girdi ilişkilendirme öneki olmayan düz GS1 içeriği olarak ele alınır.

### İlişkilendirme kimliğine erişim

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

### AIM Kod Kimliğiyle birlikte kullanım

Bir ilişkilendirme öneki, bir AIM Kod Kimliğinden önce gelebilir. Ayrıştırıcı bunu saydam biçimde ele alır:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Gerçekleme sınıfı:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Bir **GS1 Digital Link**, bir ya da daha çok AI değerini doğrudan bir HTTP(S) URL'sinin yapısında kodlar ve böylece fiziksel ürünler için web üzerinden çözümlenebilen tanımlayıcılar sağlar. GAIA, **sıkıştırılmamış** URI'ler için *GS1 Digital Link Standard: URI Syntax*'ı (sürüm 1.7.0) uygular.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser`, Digital Link URI'lerini kendiliğinden tanır — `http://` ya da `https://` ile başlayan her girdi `GS1DLParser`'a yönlendirilir; o da eleman dizisi işlem hattıyla aynı içerik ve yorum aşamalarını çalıştırır.

### URI yapısı ve AI rolleri

Bir Digital Link URI'sindeki her AI, üç rolden birini üstlenir; bu rol her `GS1AIObjectElement` üzerinde `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) ile sunulur:

| Rol | Konum | Örnek |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Yolun ilk `/ai/value` çifti (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Sonraki yol çiftleri, birincil anahtara göre sıralı (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Tümüyle sayısal anahtarlı sorgu parametreleri (§4.10) | `?17=271231` |

Uygulanan yapısal kurallar (`DLPathRules`):
- Yolda tam olarak **bir** birincil tanımlama anahtarı; ek anahtarlar sorgu veri öznitelikleri olarak kodlanmalıdır.
- Anahtar niteleyiciler birincil anahtarca kabul edilmeli ve öngörülen sırada görünmelidir. İsteğe bağlı niteleyiciler atlanabilir, ancak *bulunanların* yine de sabit sırayı izlemesi gerekir — bkz. [Niteleyici sıralaması](#niteleyici-sıralaması).
- Birincil anahtardan önce keyfi özel yol parçaları gelebilir (ör. `/products/au/01/...`); bunları `getDigitalLinkInfo().getCustomPathStem()` ile alın.
- Sayısal olmayan sorgu anahtarları (`linkType`, `context`, `23P` gibi uzantı parametreleri) yok sayılır; tümüyle sayısal anahtarlar `validAsDataAttribute` olarak işaretlenmiş geçerli AI'lar olmalıdır.
- Yüzde kodlanmış değer karakterlerinin kodu çözülür; AI `(03)` ve `(8014)` izinli değildir.

Birincil anahtarlar ve kabul ettikleri niteleyici dizileri sabit kodlanmış değil, AI tanımlarından **veri güdümlü** olarak gelir — `gs1DigitalLinkPrimaryKey` bayrağı ve `gs1DigitalLinkQualifiers` özniteliğinden.

Herhangi bir yapısal ihlal ya da URL olmayan bir girdi, bir Digital Link yapısal hatası üretir (`GE-L001`–`GE-L014`, koşul başına bir kod). Ayrıştırılmış URL üstverisi (`scheme`, `domain`, `path`, `customPathStem`, `query` ve `java.net.URL`), yapısal hatalar varken bile `getDigitalLinkInfo()` ile erişilebilir kalır.

### Niteleyici sıralaması

Her birincil anahtar için `gs1DigitalLinkQualifiers`, bir ya da daha çok **sıralı** niteleyici dizisi listeler. Bir dizi içinde köşeli parantezle sarılmış bir AI **isteğe bağlıdır**, parantezsiz bir AI ise **zorunludur** — bu, §4.9 ABNF'sindeki `[cpv-comp]` gösterimini birebir yansıtır. Tek bir birincil anahtara ait diziler birbirini dışlayan seçeneklerdir.

Örneğin GTIN (`01`) iki dizi tanımlar:

| Yol | Dizi | Anlamı |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — her biri isteğe bağlı, ancak bu sırada sabit |
| upui-path | `235` | TPX (zorunlu); GTIN + TPX = UPUI |

Dolayısıyla `/01/09506000134352/10/LOT-ABC/21/SER` geçerlidir (SER'den önce LOT, CPV atlanmış), `/01/.../21/SER/10/LOT-ABC` **reddedilir** (sıra dışı) ve `/01/09506000134352/235/2ABC456` upui-path'tir. Sıra denetimi, sırayı koruyan bir alt dizi eşleştirmesidir; bu yüzden isteğe bağlı AI'lar atlanabilir ama asla yeniden sıralanamaz.

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

**Gerçekleme sınıfı:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Sonuçlarla Çalışmak

### ParseResult

`GaiaParser.parse()` tarafından döndürülen üst düzey sonuç.

| Yöntem | Döndürdüğü | Açıklama |
|---|---|---|
| `isValid()` | `boolean` | Hiçbir düzeyde hata yoksa `true`. Uyarılar geçerliliği etkilemez. `getAiObject()` `null` olduğunda her zaman `true`. |
| `getPayload()` | `String` | İlişkilendirme öneki ayıklandıktan — ve varsa [girdi değiştiricileri](#girdi-değiştiricileri) onu yeniden yazdıktan — sonraki girdi dizisi. |
| `getPayloadContent()` | `String` | AIM Kod Kimliği ve ECI öneki ayıklanmış yük. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (GS1 olmadığı için reddedilen bir veri taşıyıcısı, ör. bir Code 39 `]A0` taşıyıcısı) ya da `UNABLE_TO_DETERMINE_CONTENT` (`aiObject` `null` olduğunda, ör. `DATA_CARRIER` kipinde). |
| `getRequestedParseMode()` | `ParseMode` | Yapılandırılmış işlem hattı derinliği (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Ayrıştırmanın gerçekten ulaştığı en derin aşama — aşağıya bakın. |
| `isParseComplete()` | `boolean` | Ayrıştırma istenen derinliğe ulaştıysa `true` (`achieved == requested`). `isValid()`'den bağımsızdır. |
| `getAiObject()` | `GS1AIObject` | Çözümlenmiş tüm AI'lar. `DATA_CARRIER` kipinde `null`. |
| `getErrors()` | `List<GaiaError>` | WARNING olmayan tüm hatalar (nesne düzeyi + tüm eleman düzeyi). |
| `getWarnings()` | `List<GaiaError>` | Tüm WARNING bildirimleri (nesne düzeyi + tüm eleman düzeyi). |
| `hasWarnings()` | `boolean` | Herhangi bir WARNING bildirimi oluştuysa `true`. |
| `getIssues()` | `List<GaiaError>` | Hatalar ve uyarılar birlikte. |
| `hasDataCarrier()` | `boolean` | Bir AIM Kod Kimliği tanındıysa `true`. |
| `getDataCarrier()` | `DataCarrierEntry` | Semboloji üstverisi ya da hiçbir taşıyıcı belirlenmediyse `null`. |
| `hasEci()` | `boolean` | Yükten bir ECI göstergesi ayıklandıysa `true`. |
| `getEci()` | `EciEntry` | ECI kodlama üstverisi ya da `null`. |
| `hasCorrelationId()` | `boolean` | Özgün girdide bir `DDDDDDDD~` ilişkilendirme öneki bulunduysa `true`. |
| `getCorrelationInfo()` | `CorrelationInfo` | Çıkarılan ilişkilendirme kimliği ya da yoksa `null`. |
| `isInputModified()` | `boolean` | Bir [girdi değiştiricisi](#girdi-değiştiricileri) girdiyi değiştirdiyse `true`. |
| `getModifierInfo()` | `ModifierInfo` | Değiştirici zincirinin ne yaptığı — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. Hiçbir değiştirici yapılandırılmamışsa `null`. |
| `getTiming()` | `ProcessingTiming` | Ayrıştırmanın duvar saati zamanlaması — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `GaiaParser` üretmediyse `null`. |
| `getVersion()` | `String` | Sonucu üreten kitaplık sürümü. |

#### İstenen ve ulaşılan ayrıştırma kipi

İşlem hattı **SYNTAX → CONTENT → INTERPRETATION** merdivenini tırmanır ve hatalarda erkenden durur; bu yüzden gerçekten *ulaşılan* kip, *istenen* kipten daha sığ olabilir. `getAchievedParseMode()` nereye kadar gidildiğini raporlar:

| İstenen | Ne olur | Ulaşılan | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | bir **sözdizimi / yapısal** hata, belirteçlemeden sonra ayrıştırmayı durdurur | `SYNTAX` | `false` |
| `INTERPRETATION` | bir **içerik** hatası (hatalı biçim/kontrol hanesi) zenginleştirmeyi engeller | `CONTENT` | `false` |
| `CONTENT` | içerik her zaman sonuna dek çalışır (hatalar not edilir, ölümcül değildir) | `CONTENT` | `true` |
| herhangi biri (temiz girdi) | işlem hattı istenen derinliğe ulaşır | = istenen | `true` |
| `DATA_CARRIER` | taşıyıcı doğrulanır; AI içeriği ayrıştırılmaz | `DATA_CARRIER` | `true` |
| herhangi biri | veri taşıyıcısı AI ayrıştırmasından önce reddedilir (ör. GS1 olmayan bir `]A0` taşıyıcısı) | `SYNTAX` | `false` |

`isParseComplete()`, `isValid()`'den bağımsızdır: hatalı kontrol haneli bir GTIN'in `CONTENT` ayrıştırması **tamamdır** (içerik aşaması çalıştı) ama **geçersizdir** (kontrol hanesi başarısız oldu). "İşlem hattı istediğim kadar derine indi mi?" diye sormak için `isParseComplete()`, "veri düzgün biçimlenmiş mi?" diye sormak için `isValid()` kullanın.

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

Çözümlenmiş AI elemanlarının derlemi.

| Yöntem | Açıklama |
|---|---|
| `getAis()` | Tüm `GS1AIObjectElement` örnekleri, girdi sırasında. |
| `get(String aiCode)` | Verilen AI koduyla eşleşen ilk eleman ya da `null`. |
| `contains(String aiCode)` | O koda sahip bir AI varsa `true`. |
| `size()` | Çözümlenmiş AI sayısı. |
| `isValid()` | Nesne düzeyinde hata yoksa ve hiçbir elemanda hata yoksa `true`. |
| `toHriString()` | HRI dizisi, ör. `(01)09506000134352 (17)261231`. |
| `toElementString()` | Ham eleman dizisi — parantezsiz, her değişken uzunluklu elemandan sonra FNC1 ile — ör. `010950600013435210LOT-ABC<GS>17271231`. `isValid()` `false` ise `null` döndürür. |
| `getContentType()` | `hasDigitalLink()` doğruysa `GS1_DIGITAL_LINK`, değilse `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | Girdi, birincil tanımlama anahtarı taşıyan bir GS1 Digital Link URI'siyse `true`. Düzgün biçimlenmiş ama birincil anahtarı olmayan bir URL yine de `getDigitalLinkInfo()` sunar, ancak burada `false` döndürür. |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` üzerindeki kanonik GS1 Digital Link URI'si (§4.12) — birincil anahtar ve niteleyiciler yol parçaları olarak, veri öznitelikleri AI anahtarına göre sıralı sorgu parametreleri olarak — ya da birincil anahtar yoksa `null`. |
| `getDigitalLinkInfo()` | URI ayrıştırma üstverisi (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`) ya da Digital Link değilse `null`. |
| `getAllErrors()` | Nesne düzeyi + tüm eleman hataları (WARNING olmayan). |
| `getAllWarnings()` | Nesne düzeyi + tüm eleman uyarıları. |
| `getAllIssues()` | Hepsi birlikte. |

---

### GS1AIObjectElement

Çözümlenmiş tek bir AI örneği.

| Yöntem | Açıklama |
|---|---|
| `getAi()` | AI kodu, ör. `"01"`, `"3102"`. |
| `getTitle()` | GS1 veri başlığı, ör. `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | AI'ın tam GS1 açıklaması, **ayrıştırma diline yerelleştirilmiş** (İngilizcede ör. `"Global Trade Item Number (GTIN)"`). Çevrilmemişse AI tanımındaki İngilizce metne geri döner. |
| `getFormatString()` | AI'ı *ve* verisini kapsayan biçim betimleyicisi, ör. AI `01` için `"N2+N14"`, AI `10` için `"N2+X..20"`, AI `3932` için `"N4+N3+N..15"`. |
| `getValue()` | Eleman dizisinden çıkarılan ham veri değeri. |
| `isFixedLength()` | AI'ın veri uzunluğu sabitse `true`. |
| `getPosition()` | Özgün girdideki sıfır tabanlı karakter konumu. |
| `getGS1ComponentValues()` | Bileşen başına değer dilimleri (çok bileşenli AI'lar için). |
| `getErrors()` | Eleman düzeyinde WARNING olmayan hatalar. |
| `getWarnings()` | Eleman düzeyinde WARNING bildirimleri. |
| `getIssues()` | Eleman düzeyindeki hatalar ve uyarılar birlikte. |
| `hasErrors()` | WARNING olmayan herhangi bir hata iliştirilmişse `true`. |
| `hasWarnings()` | Herhangi bir WARNING bildirimi iliştirilmişse `true`. |
| `getInterpretations()` | `GS1AIInterpretation` girdileri (INTERPRETATION kipinde doldurulur). |
| `getInterpretation(String type)` | Verilen `GS1Constants_Enricher` tür anahtarıyla eşleşen ilk yorum ya da `null`. |
| `getDigitalLinkAIType()` | Elemanın Digital Link rolü (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`) ya da eleman dizisi girdilerinde `null`. |
| `hasDigitalLinkAIType()` | Bir Digital Link rolü atanmışsa `true`. |

---

### GaiaError

Değiştirilemez bir doğrulama hatası ya da bildirimi.

| Yöntem | Açıklama |
|---|---|
| `getId()` | Katalog tanımlayıcısı, ör. `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` ya da `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` ya da `INTERNAL`. |
| `getCode()` | Makine tarafından okunabilir kısa kod. |
| `getAi()` | Hataya yol açan AI kodu ya da nesne düzeyi hatalarda `null`. |
| `getMessage()` | Değerleri yerleştirilmiş, insan tarafından okunabilir ileti. |
| `getPosition()` | Özgün girdideki sıfır tabanlı karakter konumu. |

---

### GS1AIInterpretation

`INTERPRETATION` kipinde bir `GS1AIObjectElement`'e iliştirilen, etiketli tek bir yorum parçası.

| Yöntem | Açıklama |
|---|---|
| `getType()` | Makine tarafından okunabilir tür anahtarı, ör. `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Diller arasında değişmez. |
| `getLabel()` | İnsan tarafından okunabilir etiket, **ayrıştırma diline yerelleştirilmiş** (İngilizcede ör. `"Date"` / `"GS1 company prefix"`). |
| `getValue()` | Çıkarılmış/zenginleştirilmiş değer, ör. `"31/12/2026"`, `"9506000"`. Yerelleştirilmez. |

---

### DataCarrierEntry ve DataCarrierType

Girdi bir AIM Kod Kimliği taşıdığında, `ParseResult.getDataCarrier()` veriyi taşıyan sembolü betimleyen bir `DataCarrierEntry` döndürür. Bu girdi, eşleşen AIM Kod Kimliğine ait belirli kayıt defteri kaydıdır; `DataCarrierType` ise onun ait olduğu derleme zamanı numaralandırmasıdır.

#### DataCarrierEntry

Tanınan tek bir AIM Kod Kimliğinin üstverisi (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Yöntem | Açıklama |
|---|---|
| `getAimCodeId()` | Eşleşen AIM Kod Kimliği, ör. `"]C1"`. |
| `getName()` | Belirli sembolün insan tarafından okunabilir adı, ör. `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Taşıyıcının daha uzun açıklaması. |
| `getType()` | Taşıyıcının yapısal türü, dizi olarak (`getDataCarrierType().getCategory()` değerini yansıtır). |
| `getStandard()` | Kayıtlıysa semboloji standardı. |
| `getDataCarrierType()` | Bu girdi için türlenmiş `DataCarrierType` — programatik yönlendirmede bunu yeğleyin. |
| `isGs1Capable()` | Taşıyıcı GS1 verisi tutabiliyorsa `true` (AI eleman dizileri ve/veya Digital Link). |
| `isGs1AICapable()` | Taşıyıcı GS1 AI eleman dizileri tutabiliyorsa `true`. |
| `isGs1DigitalLinkCapable()` | Taşıyıcı bir GS1 Digital Link URI'si tutabiliyorsa `true`. |
| `isEciCapable()` | Taşıyıcı bir ECI göstergesini destekliyorsa `true`. |
| `isRequiresGtinPadding()` | Sayısal değeri AI ayrıştırmasından önce GTIN-14'e tamamlanan EAN/UPC/ITF taşıyıcıları için `true`. |

#### DataCarrierType

Veri taşıyıcı türlerinin derleme zamanı numaralandırması; anahtarı ISO/IEC 15424'te atanmış AIM Kod Kimliğidir (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). `]` sonrasındaki karakter (*kod karakteri*) aileyi seçer; ailelerin çoğu her değiştiriciyi kapsayan tek bir sabite eşlenir (`ITF`, `]I0`–`]I2` aralığını kapsar; `EAN_UPC`, EAN-13, UPC-A, UPC-E ve EAN-8'i kapsar). GS1'in bir değiştiriciyi AI verisi için ayırdığı yerde, o çeşit kendi sabitidir — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — yalın karşılıklarından ayrı olarak. Hiçbir AIM Kod Kimliği yoksa ya da kimlik bilinmeyen bir taşıyıcıyı adlandırıyorsa, tür `UNKNOWN`'dır.

| Yöntem | Açıklama |
|---|---|
| `getCategory()` | Geniş `GaiaConstants.DataCarrierTypeCategory` kategorisi: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` ya da `OTHER`. |
| `getCodeChar()` | Aileyi belirleyen AIM kod karakteri, ör. QR Code için `"Q"`; `UNKNOWN` için `null`. |
| `getDisplayName()` | *Türün* insan tarafından okunabilir adı (`DataCarrierEntry.getName()`'den daha geniş olabilir — ör. `"EAN-13 / UPC-A / UPC-E / EAN-8"` karşısında `"EAN-8"`). |
| `isGs1DataCarrier()` | Her zaman GS1 AI verisi belirten sabitler için `true`: GS1'e ayrılmış dört çeşit (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) ve ayrıca `GS1_DATABAR`; sonuncusu doğası gereği GS1'dir, çünkü her `]e` değiştiricisi GS1 DataBar'dır. `DataCarrierEntry.isGs1AICapable()`'dan dardır — yalın bir `QR_CODE` de GS1 AI verisi taşıyabilir. |
| `static forAimCodeId(String)` | Türü doğrudan bir AIM Kod Kimliğinden çözer (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); bulunmayan, bozuk ya da tanınmayan bir kimlik için `UNKNOWN` döndürür. |

Ada göre değil türe göre yönlendirme — ör. doğrusal (Code-128) sembolleri 2D (QR / Data Matrix) sembollerden ayırmak:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` yalnızca matris ve nokta sembollerini kapsar; yığılmış doğrusal taşıyıcılar (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`), yaygın olarak "2D" barkod diye anılsalar da
`STACKED_LINEAR`'dır. İkisini tek bir grup olarak ele almak için — diyelim ki lazer tarayıcı
yerine görüntüleyici gerekip gerekmediğine karar vermek için — her iki kategoriyi de
sınayın.

> Tür çözümlemesi, taramada AIM Kod Kimliğinin bulunmasını gerektirir; o olmadan `getDataCarrier()` `null` ve tür `UNKNOWN` olur. Tarayıcıyı AIM Kod Kimliği önekini iletecek biçimde yapılandırın.

---

## Hata Başvurusu

| Kod | Düzey | Aşama | Anlamı |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Bilinmeyen AI öneki — veri uzunluğu belirlenemiyor |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Girdi, eksiksiz bir AI kodu okumak için fazla kısa |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Kesilmiş değer — AI'ın gerektirdiğinden az karakter |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Eleman dizisinde yinelenen Uygulama Tanımlayıcı |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Zorunlu AI bağımlılığı eksik |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Dışlanan AI eşleşmesi — birlikte bulunamayan iki AI |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Beklenmeyen belirteçleme başarısızlığı |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Eleman dizisinde GS1 kodlanabilir kümesi dışında karakter |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Değişken uzunluklu bir AI'dan sonra zorunlu FNC1 ayırıcısı eksik |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Tüm bileşen üst sınırlarının ötesinde artan veri |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Dizi ortasında, sabit uzunluklu bir AI'dan sonra FNC1 ayırıcısı |
| `GE-W002` | WARNING | SYNTAX | Eleman dizisinin sonunda artan FNC1 (yalnızca bildirim) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Digital Link URI yapısal ihlalleri — koşul başına bir kod (bozuk URI, şema, ana bilgisayar, niteleyici sırası, yasaklı AI, birincil anahtar yok (`GE-L013`), birden çok birincil anahtar (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Değer, AI'ın düzenli ifade örüntüsüne uymuyor |
| `GE-C003` | DATA_ERROR | CONTENT | Kontrol hanesi doğrulaması başarısız |
| `GE-C004` | DATA_ERROR | CONTENT | Kontrol karakteri çifti doğrulaması başarısız |
| `GE-C005` | FORMAT_ERROR | CONTENT | Bileşen değeri, izin verilen karakter kümesi dışında bir karakter içeriyor |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Bileşen biçimi başarısızlıkları — doğrulayıcı koşulu başına bir kod (bkz. `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Özel anlamsal doğrulama başarısızlıkları — doğrulayıcı koşulu başına bir kod (bkz. `content/validator/`). **İstisnalar:** aşağıda listelenen 14 GS1 firma öneki denetimi `WARNING` düzeyi taşır ve `GE-C168` (tanınmayan ISO 3166-1 sayısal ülke kodu) `FORMAT_ERROR` düzeyi taşır. |
| GS1 firma öneki denetimleri | WARNING | CONTENT | GS1 anahtarlı AI'larda anahtar, tanınan bir GS1 firma önekiyle başlamıyor — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Yalnızca bildirim — geçerliliği etkilemez. |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2) üzerinde IMEI kontrol hanesi (Luhn) başarısız |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM) üzerinde EID kontrol hanesi (Luhn) başarısız |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Tanınmayan AIM Kod Kimliği |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Taşıyıcı belirlendi ancak ne GS1 AI eleman dizilerini ne de Digital Link URI'lerini destekliyor |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Beklenmeyen iç hata |

> **İleti oluşturmada bilinen kusur.** Katalog şablonları, yerleştirilen değerleri
> MessageFormat biçemindeki ikili kesme imleriyle (`''{value}''`) tırnak içine alır, ancak
> `ErrorRegistry` değerleri yalın `String.replace` ile yerleştirir; bu yüzden ikileme
> `getMessage()`'a dek taşınır — bu kılavuzda alıntılanan ileti metinlerinin
> `value '09506000134351'` gösterdiği yerde şu anda `value ''09506000134351''`
> göreceksiniz. Bu, 35 dil kataloğunun tümündeki değer tırnaklayan her iletiyi etkiler.
> Hata iletilerini ayrıştırmayın; `getId()` / `getCode()` üzerinden eşleştirin.

---

## İş Parçacığı Güvenliği

`GaiaParser`, bir kez kurulduktan sonra iş parçacığı güvenlidir. Tek bir örnek iş parçacıkları arasında paylaşılabilir ve eşzamanlı olarak kullanılabilir. Önerilen kalıp, uygulama başlangıcında tek bir örnek kurup onu yeniden kullanmaktır:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` değiştirilemezdir ve paylaşılması aynı ölçüde güvenlidir. Kitaplığın sizin adınıza uygulayamadığı tek iş parçacığı güvenliği yükümlülüğü [girdi değiştiricilerinde](#girdi-değiştiricileri)dir: her değiştiricinin tek bir örneği önbelleğe alınıp eşzamanlı her ayrıştırmada paylaşılır; bu yüzden gerçeklemelerin durumsuz olması gerekir.

---

## Ek A — AI Dizi Sabitleri

`GS1Constants_AICodes` (paket `tools.pantheum.gaia.gs1.constants`), GAIA'nın tanıdığı her Uygulama Tanımlayıcı için bir `String` sabiti bildirir. Kodda AI kodu sabitlerini gömmek yerine bu sabitleri kullanın:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Her sabit, AI kodunun dizi biçimini taşır (ör. `AI_01_GTIN = "01"`).

### Tanımlama ve Serileştirme

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Seri Sevkiyat Konteyner Kodu (SSCC). |
| `01` | `AI_01_GTIN` | Global Ticari Ürün Numarası (GTIN). |
| `02` | `AI_02_CONTENT` | İçerilen ticari ürünlerin Global Ticari Ürün Numarası (GTIN). |
| `03` | `AI_03_MTO_GTIN` | Siparişe Göre Üretilen (MtO) ticari ürünün tanımlaması (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Parti veya lot numarası. |
| `20` | `AI_20_VARIANT` | Dahili ürün varyantı. |
| `21` | `AI_21_SERIAL` | Seri numarası. |
| `22` | `AI_22_CPV` | Tüketici ürün varyantı. |
| `235` | `AI_235_TPX` | Üçüncü Taraf Kontrollü, Global Ticari Ürün Numarasının (GTIN) Serileştirilmiş Uzantısı (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Üretici tarafından atanan ek ürün tanımlaması. |
| `241` | `AI_241_CUST_PART_NO` | Müşteri parça numarası. |
| `242` | `AI_242_MTO_VARIANT` | Siparişe göre üretim varyasyon numarası. |
| `243` | `AI_243_PCN` | Ambalaj bileşeni numarası. |
| `250` | `AI_250_SECONDARY_SERIAL` | İkincil seri numarası. |
| `251` | `AI_251_REF_TO_SOURCE` | Kaynak varlığa referans. |
| `253` | `AI_253_GDTI` | Global Doküman Türü Tanımlayıcısı (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Global Konum Numarası (GLN) uzantı bileşeni. |
| `255` | `AI_255_GCN` | Global Kupon Numarası (GCN). |
| `30` | `AI_30_VAR_COUNT` | Değişken ürün sayısı (değişken ölçülü ticari ürün). |
| `37` | `AI_37_COUNT` | Bir lojistik birim içindeki ticari ürün veya ticari ürün parçalarının sayısı. |

### Tarihler ve Saatler

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Üretim tarihi (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Vade tarihi (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Ambalajlama tarihi (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Son kullanma tarihi (tavsiye edilen) (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Son satış tarihi (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Son kullanma tarihi (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | En erken teslimat tarihi ve saati (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | En geç teslimat tarihi ve saati (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Yayın tarihi (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Son kullanma tarihi ve saati (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | İlk dondurma tarihi (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Hasat tarihi (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Test tarihi (YYMMDD[hhmm]). |

### Miktar ve Ölçü — Değişken Ölçü (Metrik)

`310n`–`369n` dört haneli AI aileleri değişken ölçülü miktarları kodlar. Üçüncü hane ölçü türünü seçer; **dördüncü hane** (`n`, 0–5) örtük ondalık basamak sayısıdır — yani `AI_3102_NET_WEIGHT_KG`, 2 ondalık basamaklı kilogram cinsinden net ağırlık demektir.

| Aile | Sabit örüntüsü (`n` = ondalık hane) | Açıklama |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Net ağırlık, kilogram (değişken ölçülü ticari ürün). |
| `311n` | `AI_311n_LENGTH_M` | Uzunluk veya birinci boyut, metre (değişken ölçülü ticari ürün). |
| `312n` | `AI_312n_WIDTH_M` | Genişlik, çap veya ikinci boyut, metre (değişken ölçülü ticari ürün). |
| `313n` | `AI_313n_HEIGHT_M` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, metre (değişken ölçülü ticari ürün). |
| `314n` | `AI_314n_AREA_M` | Alan, metrekare (değişken ölçülü ticari ürün). |
| `315n` | `AI_315n_NET_VOLUME_L` | Net hacim, litre (değişken ölçülü ticari ürün). |
| `316n` | `AI_316n_NET_VOLUME_M` | Net hacim, metreküp (değişken ölçülü ticari ürün). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Lojistik ağırlık, kilogram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Uzunluk veya birinci boyut, metre. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Genişlik, çap veya ikinci boyut, metre. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, metre. |
| `334n` | `AI_334n_AREA_M_LOG` | Alan, metrekare. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Lojistik hacim, litre. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Lojistik hacim, metreküp. |
| `337n` | `AI_337n_KG_PER_M` | Metrekare başına kilogram. |

### Miktar ve Ölçü — Değişken Ölçü (İngiliz / ABD)

| Aile | Sabit örüntüsü (`n` = ondalık hane) | Açıklama |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Net ağırlık, pound (değişken ölçülü ticari ürün). |
| `321n` | `AI_321n_LENGTH_IN` | Uzunluk veya birinci boyut, inç (değişken ölçülü ticari ürün). |
| `322n` | `AI_322n_LENGTH_FT` | Uzunluk veya birinci boyut, fit (değişken ölçülü ticari ürün). |
| `323n` | `AI_323n_LENGTH_YD` | Uzunluk veya birinci boyut, yarda (değişken ölçülü ticari ürün). |
| `324n` | `AI_324n_WIDTH_IN` | Genişlik, çap veya ikinci boyut, inç (değişken ölçülü ticari ürün). |
| `325n` | `AI_325n_WIDTH_FT` | Genişlik, çap veya ikinci boyut, fit (değişken ölçülü ticari ürün). |
| `326n` | `AI_326n_WIDTH_YD` | Genişlik, çap veya ikinci boyut, yarda (değişken ölçülü ticari ürün). |
| `327n` | `AI_327n_HEIGHT_IN` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, inç (değişken ölçülü ticari ürün). |
| `328n` | `AI_328n_HEIGHT_FT` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, fit (değişken ölçülü ticari ürün). |
| `329n` | `AI_329n_HEIGHT_YD` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, yarda (değişken ölçülü ticari ürün). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Lojistik ağırlık, pound. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Uzunluk veya birinci boyut, inç. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Uzunluk veya birinci boyut, fit. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Uzunluk veya birinci boyut, yarda. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Genişlik, çap veya ikinci boyut, inç. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Genişlik, çap veya ikinci boyut, fit. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Genişlik, çap veya ikinci boyut, yarda. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, inç. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, fit. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Derinlik, kalınlık, yükseklik veya üçüncü boyut, yarda. |
| `350n` | `AI_350n_AREA_IN` | Alan, kare inç (değişken ölçülü ticari ürün). |
| `351n` | `AI_351n_AREA_FT` | Alan, kare fit (değişken ölçülü ticari ürün). |
| `352n` | `AI_352n_AREA_YD` | Alan, kare yarda (değişken ölçülü ticari ürün). |
| `353n` | `AI_353n_AREA_IN_LOG` | Alan, kare inç. |
| `354n` | `AI_354n_AREA_FT_LOG` | Alan, kare fit. |
| `355n` | `AI_355n_AREA_YD_LOG` | Alan, kare yarda. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Net ağırlık, troy ons (değişken ölçülü ticari ürün). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Net ağırlık (veya hacim), ons (değişken ölçülü ticari ürün). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Net hacim, kuart (değişken ölçülü ticari ürün). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Net hacim, ABD galonu (değişken ölçülü ticari ürün). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Lojistik hacim, kuart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Lojistik hacim, ABD galonu. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Net hacim, kübik inç (değişken ölçülü ticari ürün). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Net hacim, kübik fit (değişken ölçülü ticari ürün). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Net hacim, kübik yarda (değişken ölçülü ticari ürün). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Lojistik hacim, kübik inç. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Lojistik hacim, kübik fit. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Lojistik hacim, kübik yarda. |

### Fiyatlandırma ve Parasal Tutarlar

Dördüncü hane (`n`) örtük ondalık basamak sayısını kodlar. İzin verilen aralık
aileye göre değişir — `n` sütununa bakın.

| Aile | Sabit örüntüsü (`n` = ondalık hane) | `n` | Açıklama |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Uygulanabilir ödenecek tutar veya Kupon değeri, yerel para birimi. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | ISO para birimi koduyla uygulanabilir ödenecek tutar. |
| `392n` | `AI_392n_PRICE` | 0–9 | Uygulanabilir ödenecek tutar, tek para birimi alanı (değişken ölçülü ticari ürün). |
| `393n` | `AI_393n_PRICE` | 0–9 | ISO para birimi koduyla uygulanabilir ödenecek tutar (değişken ölçülü ticari ürün). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Bir kuponun yüzde indirimi. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Ölçü birimi başına ödenecek tutar, tek para birimi alanı (değişken ölçülü ticari ürün). |

### Konum ve Sevkiyat

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Müşteri satın alma sipariş numarası. |
| `401` | `AI_401_GINC` | Sevkiyat için Global Tanımlama Numarası (GINC). |
| `402` | `AI_402_GSIN` | Global Sevkiyat Tanımlama Numarası (GSIN). |
| `403` | `AI_403_ROUTE` | Yönlendirme kodu. |
| `410` | `AI_410_SHIP_TO_LOC` | Sevk edilecek / Teslim edilecek Global Konum Numarası (GLN). |
| `411` | `AI_411_BILL_TO` | Fatura edilecek Global Konum Numarası (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | Satın alınan Global Konum Numarası (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | Sevk edilecek / Teslim edilecek - Yönlendirilecek Global Konum Numarası (GLN). |
| `414` | `AI_414_LOC_NO` | Fiziksel bir konumun tanımlaması - Global Konum Numarası (GLN). |
| `415` | `AI_415_PAY_TO` | Faturalandıran tarafın Global Konum Numarası (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | Üretim veya hizmet konumunun Global Konum Numarası (GLN). |
| `417` | `AI_417_PARTY` | Taraf Global Konum Numarası (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | Tek bir posta idaresi içinde sevk/teslim posta kodu. |
| `421` | `AI_421_SHIP_TO_POST` | ISO ülke koduyla sevk/teslim posta kodu. |
| `422` | `AI_422_ORIGIN` | Ticari ürünün menşe ülkesi. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | İlk işleme ülkesi. |
| `424` | `AI_424_COUNTRY_PROCESS` | İşleme ülkesi. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Sökme ülkesi. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Tüm işlem zincirini kapsayan ülke. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Menşe ülke alt bölgesi. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Sevk/Teslim edilecek şirket adı. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Sevk/Teslim iletişim bilgisi. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Sevk/Teslim adresi 1. satır. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Sevk/Teslim adresi 2. satır. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Sevk/Teslim edilecek semt. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Sevk/Teslim edilecek yerleşim yeri. |
| `4306` | `AI_4306_SHIP_TO_REG` | Sevk/Teslim edilecek bölge. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Sevk/Teslim ülke kodu. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Sevk/Teslim telefon numarası. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Sevk/Teslim edilecek coğrafi konum (GEO). |
| `4310` | `AI_4310_RTN_TO_COMP` | İade edilecek şirket adı. |
| `4311` | `AI_4311_RTN_TO_NAME` | İade iletişim bilgisi. |
| `4312` | `AI_4312_RTN_TO_ADD1` | İade adresi 1. satır. |
| `4313` | `AI_4313_RTN_TO_ADD2` | İade adresi 2. satır. |
| `4314` | `AI_4314_RTN_TO_SUB` | İade edilecek semt. |
| `4315` | `AI_4315_RTN_TO_LOC` | İade edilecek yerleşim yeri. |
| `4316` | `AI_4316_RTN_TO_REG` | İade edilecek bölge. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | İade ülke kodu. |
| `4318` | `AI_4318_RTN_TO_POST` | İade posta kodu. |
| `4319` | `AI_4319_RTN_TO_PHONE` | İade telefon numarası. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Hizmet kodu açıklaması. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Tehlikeli madde işareti. |
| `4322` | `AI_4322_AUTH_LEAVE` | Bırakma yetkisi. |
| `4323` | `AI_4323_SIG_REQUIRED` | İmza gerekli işareti. |
| `4330` | `AI_4330_MAX_TEMP_F` | Fahrenhayt cinsinden maksimum sıcaklık (derecenin yüzde biri olarak ifade edilir). |
| `4331` | `AI_4331_MAX_TEMP_C` | Santigrat cinsinden maksimum sıcaklık (derecenin yüzde biri olarak ifade edilir). |
| `4332` | `AI_4332_MIN_TEMP_F` | Fahrenhayt cinsinden minimum sıcaklık (derecenin yüzde biri olarak ifade edilir). |
| `4333` | `AI_4333_MIN_TEMP_C` | Santigrat cinsinden minimum sıcaklık (derecenin yüzde biri olarak ifade edilir). |

### Ürün Öznitelikleri ve İzlenebilirlik

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | NATO Stok Numarası (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE et karkas ve parça sınıflandırması. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Aktif potens. |
| `7005` | `AI_7005_CATCH_AREA` | Avlanma alanı. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Balıkçılık amaçlı türler. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Balıkçılık ekipmanı türü. |
| `7010` | `AI_7010_PROD_METHOD` | Üretim yöntemi. |
| `7020` | `AI_7020_REFURB_LOT` | Yenileme lot kimliği. |
| `7021` | `AI_7021_FUNC_STAT` | İşlevsel durum. |
| `7022` | `AI_7022_REV_STAT` | Revizyon durumu. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Bir montajın Global Bireysel Varlık Tanımlayıcısı (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Üç haneli ISO ülke kodlu işleyici numarası (10 yuva). |
| `7040` | `AI_7040_UIC_EXT` | Uzantı 1 ve İthalatçı endeksi ile GS1 UIC. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT navlun birimi türü. |

### Ulusal Sağlık Geri Ödeme Numaraları (NHRN)

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Almanya PZN. |
| `711` | `AI_711_NHRN_CIP` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Fransa CIP. |
| `712` | `AI_712_NHRN_CN` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - İspanya CN. |
| `713` | `AI_713_NHRN_DRN` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Brezilya DRN. |
| `714` | `AI_714_NHRN_AIM` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Portekiz AIM. |
| `715` | `AI_715_NHRN_NDC` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Amerika Birleşik Devletleri NDC. |
| `716` | `AI_716_NHRN_AIC` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - İtalya AIC. |
| `717` | `AI_717_NHRN_SRN` | Ulusal Sağlık Hizmeti Geri Ödeme Numarası (NHRN) - Kosta Rika Sağlık Sicil Numarası. |

### Sağlık Hizmetleri, GMN, HIDRI, CPID ve Kişi Verileri

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Sertifikasyon Referansı (10 yuva). |
| `7240` | `AI_7240_PROTOCOL` | Protokol Kimliği. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC ortam türü. |
| `7242` | `AI_7242_VCN` | Versiyon Kontrol Numarası (VCN). |
| `7250` | `AI_7250_DOB` | Doğum tarihi (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Doğum tarihi ve saati (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Biyolojik cinsiyet. |
| `7253` | `AI_7253_FAMILY_NAME` | Kişinin soyadı. |
| `7254` | `AI_7254_GIVEN_NAME` | Kişinin adı. |
| `7255` | `AI_7255_SUFFIX` | Kişinin ad soneki. |
| `7256` | `AI_7256_FULL_NAME` | Kişinin tam adı. |
| `7257` | `AI_7257_PERSON_ADDR` | Kişinin adresi. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Bebek doğum sırası. |
| `7259` | `AI_7259_BABY` | Aile soyadına göre bebek sırası. |
| `8001` | `AI_8001_DIMENSIONS` | Rulo ürünler (genişlik, uzunluk, göbek çapı, yön, ekler). |
| `8002` | `AI_8002_CMT_NO` | Cep telefonu tanımlayıcısı. |
| `8003` | `AI_8003_GRAI` | Global İade Edilebilir Varlık Tanımlayıcısı (GRAI). |
| `8004` | `AI_8004_GIAI` | Global Bireysel Varlık Tanımlayıcısı (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Ölçü birimi başına fiyat. |
| `8006` | `AI_8006_ITIP` | Bireysel bir ticari ürün parçasının tanımlaması (ITIP). |
| `8007` | `AI_8007_IBAN` | Uluslararası Banka Hesap Numarası (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Üretim tarihi ve saati (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Optik Olarak Okunabilir Sensör Göstergesi. |
| `8010` | `AI_8010_CPID` | Bileşen/Parça Tanımlayıcısı (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Bileşen/Parça Tanımlayıcısı seri numarası (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Yazılım sürümü. |
| `8013` | `AI_8013_GMN` | Global Model Numarası (GMN). |
| `8014` | `AI_8014_MUDI` | Yüksek Düzeyde Bireyselleştirilmiş Cihaz Kayıt Tanımlayıcısı (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Hizmet sunan kuruluş ile hizmet sağlayıcısı arasındaki ilişkiyi tanımlamak için Global Hizmet İlişki Numarası (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Hizmet sunan kuruluş ile hizmet alıcısı arasındaki ilişkiyi tanımlamak için Global Hizmet İlişki Numarası (GSRN). |
| `8019` | `AI_8019_SRIN` | Hizmet İlişkisi Örnek Numarası (SRIN). |
| `8020` | `AI_8020_REF_NO` | Ödeme fişi referans numarası. |
| `8026` | `AI_8026_ITIP_CONTENT` | Bir lojistik birim içinde bulunan ticari ürün parçalarının tanımlaması (ITIP). |
| `8030` | `AI_8030_DIGSIG` | Dijital İmza (DigSig). |
| `8040` | `AI_8040_IMEI` | Uluslararası Mobil Cihaz Kimliği (IMEI). |
| `8041` | `AI_8041_IMEI2` | Uluslararası Mobil Cihaz Kimliği 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Gömülü SIM numarası. |
| `8043` | `AI_8043_PSIM` | Fiziksel SIM numarası. |
| `8110` | `AI_8110` | Kuzey Amerika'da kullanım için kupon kodu tanımlaması. |
| `8111` | `AI_8111_POINTS` | Bir kuponun sadakat puanları. |
| `8112` | `AI_8112` | Kuzey Amerika'da kullanım için Positive Offer File kupon kodu tanımlaması. |
| `8200` | `AI_8200_PRODUCT_URL` | Genişletilmiş Paketleme URL'si. |

### İç / Firma Kullanımı

| AI | Sabit | Açıklama |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Ticaret ortakları arasında karşılıklı olarak kabul edilen bilgi. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Firma iç bilgisi (9 yuva). |

---

## Ek B — Yorum Anahtarı Sabitleri

`GaiaParser.parse()` `ParseMode.INTERPRETATION` ile çağrıldığında, her `GS1AIObjectElement` alana özgü zenginleştiricilerin ürettiği bir `GS1AIInterpretation` nesneleri listesi taşıyabilir. Belirli yorum değerlerini aramak için `GS1Constants_Enricher` sabitlerini (paket `tools.pantheum.gaia.gs1.constants`) anahtar olarak kullanın:

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

Görünen etiketler sabit **değildir** — hepsi `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` altındaki yerelleştirilmiş kataloglardadır ve anahtarları tür sabitidir. `GS1AIInterpretation.getLabel()` etiketi ayrıştırma dilinde döndürür (bkz. [Yerelleştirilmiş iletiler ve etiketler](#yerelleştirilmiş-iletiler-ve-etiketler)) ve bir katalogda anahtar eksikse İngilizceye geri döner. Aşağıdaki Görünen etiket sütunu Türkçe metni listeler; tür anahtarlarının kendileri diller arasında değişmez, bu yüzden her zaman anahtar üzerinden eşleştirin, asla etiket üzerinden değil.

### Tarih ve Saat

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `DATE_VALUE` | Tarih | Tarih AI'ları (11–17, 7003, 7006, 7011 vb.) |
| `DATE_FORMAT` | Tarih biçimi | Tarih AI'ları |
| `TIME_VALUE` | Saat | Saat taşıyan AI'lar (7003, 7011, 8008 vb.) |
| `TIME_FORMAT` | Saat biçimi | Saat taşıyan AI'lar |
| `DATETIME_VALUE` | Tarih ve saat | Tarih+saat AI'ları |
| `DATETIME_FORMAT` | Tarih ve saat biçimi | Tarih+saat AI'ları |

### Hasat Tarihi

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Hasat başlangıç tarihi | AI 7007 |
| `HARVEST_END_DATE` | Hasat bitiş tarihi | AI 7007 (isteğe bağlı aralık sonu) |
| `HARVEST_DATE_RANGE` | Hasat tarihi aralığı | AI 7007 |

### GS1 Firma Öneki

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 firma öneki | GTIN / GLN / SSCC AI'ları |
| `GS1_MEMBER_CODE` | GS1 üye kodu | GTIN / GLN / SSCC AI'ları |
| `GS1_MEMBER_NAME` | GS1 üye kuruluşu | GTIN / GLN / SSCC AI'ları |

### GTIN

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN türü | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Ambalaj düzeyi | AI 01 |
| `GTIN_CHECK_DIGIT` | Kontrol hanesi | AI 01, 02 |

### SSCC

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Uzantı hanesi | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Seri referansı | AI 00 |
| `SSCC_CHECK_DIGIT` | Kontrol hanesi | AI 00 |

### Ülke (ISO 3166)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Ülke kodu (sayısal) | Tek ülkeli AI'lar (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Ülke kodu (alfa-2) | Alfa-2 ülke AI'ları |
| `COUNTRY_NAME` | Ülke adı | Tek ülkeli AI'lar |
| `COUNTRY_LIST` | Ülkeler | AI 423 — tüm adlar birleştirilmiş, ör. `Australia, New Zealand` |

AI 423 (ilk işleme ülkesi) beşe kadar ülke taşıyabilir; bu yüzden **her ülke için
numaralandırılmış bir çift** üretir — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2` … — ardından tek bir `COUNTRY_LIST` özeti gelir.
Bu anahtarları `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` sabitlerinden 1'den
başlayan sıra numarasıyla oluşturun ya da yalnızca `getInterpretations()` üzerinde dolaşın;
sonek almayan `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` anahtarları AI 423 için
**üretilmez**.

### Para Birimi (ISO 4217)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Para birimi kodu | Para birimli tutar AI'ları (391n, 393n) |
| `CURRENCY_ALPHA` | Para birimi harf kodu | Para birimli tutar AI'ları |
| `CURRENCY_NAME` | Para birimi adı | Para birimli tutar AI'ları |

### Sıcaklık

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `TEMPERATURE` | Sıcaklık | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Sıcaklık birimi | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Sıcaklık (biçimlendirilmiş) | AI 4330–4333 |

### Cinsiyet (ISO 5218)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `SEX_CODE` | Cinsiyet kodu | AI 7252 |
| `SEX_DESCRIPTION` | Cinsiyet açıklaması | AI 7252 |

### Su Ürünleri Türleri (FAO)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Tür kodu | AI 7008 |
| `SPECIES_SCIENTIFIC` | Bilimsel ad | AI 7008 |
| `SPECIES_ENGLISH` | Yaygın ad | AI 7008 |
| `SPECIES_FAMILY` | Familya | AI 7008 |
| `SPECIES_ORDER` | Takım | AI 7008 |

### NATO Stok Numarası (NSN)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `NSN_FSG` | İkmal grubu | AI 7001 |
| `NSN_FSG_NAME` | İkmal grubu adı | AI 7001 |
| `NSN_FSCG` | Tedarik sınıfı | AI 7001 |
| `NSN_FSCG_NAME` | İkmal sınıfı adı | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Ülke kodu | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Ülke | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO ülke kodu | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS kategorisi | AI 7001 |
| `NSN_NIIN` | Ulusal kalem numarası | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Rulo Ürünler

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Rulo genişliği (mm) | AI 8001 |
| `ROLL_LENGTH` | Rulo uzunluğu (m) | AI 8001 |
| `CORE_DIAMETER` | Çekirdek çapı (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Sarım yönü kodu | AI 8001 |
| `WINDING_DIRECTION` | Sarım yönü | AI 8001 |
| `SPLICES` | Ekler | AI 8001 |

### IBAN

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Ülke kodu | AI 8007 |
| `IBAN_COUNTRY_NAME` | Ülke | AI 8007 |
| `IBAN_CHECK_DIGITS` | Kontrol haneleri | AI 8007 |
| `IBAN_CHECK_VALID` | Kontrol | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Seri numarası | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Kontrol hanesi | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Tahsis kuruluşu | AI 8040, 8041 |

On beş hane `[ TAC (8) ][ serial (6) ][ Luhn check digit (1) ]` biçiminde ayrışır; RBI ise
TAC'ın ilk iki hanesidir — yani `IMEI_RBI`, ayrı bir alan değil, `IMEI_TAC`'ın önekidir.
`IMEI_FORMATTED`, GSMA'nın standart görüntüleme gruplamasını `AA-BBBBBB-CCCCCC-D` gösterir
(ör. `49-015420-323751-8`); bu, TAC'ı RBI sınırında böler. Artık kaldırılmış olan son montaj
kodunun başladığı yerde bölen eski `6-2-6-1` gruplaması üretilmez.

`IMEI_RBI_NAME`, RBI'yi `ImeiRbiData` aracılığıyla tahsis eden kuruluşun adına çözer ve
**en sona, yalnızca kod orada listeleniyorsa** eklenir. O tablo üç grubu kapsar:

- **Hâlen tahsis eden kuruluşlar** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF ve ayrıca
  `99` Global Hexadecimal Administrator ile `98` (ayrılmış).
- **Test aralıkları** — `00` ve `02`–`09`; gerçek bir tahsisi değil, test IMEI'lerini
  belirtir. Bunları `ImeiRbiData.isTestCode(code)` ile sorgulayın.
- **Artık tahsis etmeyen kuruluşlar** — `49` (BZT/BAPT, Almanya), `44` (BABT, Birleşik
  Krallık) ve `91` (MSAI, Hindistan) gibi tarihsel kuruluşlar. Bunları
  `ImeiRbiData.isNoLongerAllocating(code)` ile sorgulayın. Bu kodları taşıyan cihazlar
  olağandır ve hâlâ hizmettedir; duran yalnızca yeni tahsistir, dolayısıyla bu raporlanacak
  bir bilgidir, kesinlikle bir geçerlilik göstergesi değildir.

`IMEI_RBI_NAME`'in yokluğu "bu RBI bizim tablomuzda yok" demektir, **"IMEI geçersiz"
demek değildir**: tablo doğrudan GSMA'dan değil, yayımlanmış bir RBI listesinden
derlenmiştir; bu yüzden yeni atanan kuruluşların gerisinde kalabilir. Yokluğundan hiçbir
doğrulama yargısı çıkarmayın; RBI bir kontrol karakteri değildir. Yorum listesi üzerinde
dolaşan kod, konuma göre dizinlemek yerine onun yokluğuna dayanıklı olmalıdır.

### SIM Tanımlayıcıları (EID / ICCID)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Sektör kategorisi | AI 8042 |
| `EID_BODY` | EID gövdesi | AI 8042 |
| `EID_CHECK_DIGIT` | Kontrol hanesi | AI 8042 |
| `ICCID_BODY` | ICCID gövdesi | AI 8043 |
| `ICCID_EXTENSION` | Uzantı | AI 8043 |

`SIM_MII` ilk **iki** haneyi (`89`) taşır; bu, ITU-T E.118'in telekomünikasyona ayırdığı
çifttir. ISO/IEC 7812'nin kendisi MII'yi **yalnızca ilk hane** olarak tanımlar; bu yüzden
`SIM_MII_NAME` kategoriyi baştaki `8` hanesinden `Iso7812Data` aracılığıyla çözer ve
"Healthcare, telecommunications and other future industry assignments" sonucunu verir.
Dolayısıyla düzgün biçimlenmiş her EID için aynı kalır; standarda izlenebilirlik için
raporlanır, bir ayırt edici olarak değil.
`Iso7812Data.nameForCode(digit)` tek bir hane alır; `nameForIdentifier(prefix)` ise daha uzun
bir önek kabul eder ve onun ilk hanesini okur.

`SIM_MII_NAME` yalnızca `EidEnricher` (AI 8042) tarafından üretilir. `IccidEnricher`
(AI 8043) `SIM_MII`'yi gösterir ama kategoriyi göstermez.

### Sertifikasyon Referansı

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Sıra numarası | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Sertifikasyon şeması kodu | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Sertifikasyon şeması | AI 7230–7239 |
| `CERT_REFERENCE` | Sertifikasyon referansı | AI 7230–7239 |

### GS1 UIC

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC kodu | AI 7040 |
| `UIC_EXTENSION_1` | Uzantı 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | İthalatçı dizini | AI 7040 |

### Bebek Doğum Sırası

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Doğum konumu | AI 7258 |
| `BIRTH_TOTAL` | Toplam doğum | AI 7258 |
| `BIRTH_SEQUENCE` | Doğum sırası | AI 7258 |

### Global Model Numarası (GMN)

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Model referansı | AI 8013 |
| `GMN_CHECK_PAIR` | Kontrol çifti | AI 8013 |

### HIDRI

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Cihaz referansı | AI 8014 |
| `HIDRI_CHECK_PAIR` | Kontrol çifti | AI 8014 |

### CPID

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Bileşen ve parça referansı | AI 8010–8011 |

### Ondalık ve Ölçü Değerleri

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Ondalık değer | Örtük ondalık basamaklı sayısal AI'lar (31xx–36xx) |
| `DECIMAL_AMOUNT` | Tutar | Fiyat AI'ları (390n–395n) |
| `DECIMAL_PERCENTAGE` | Yüzde | AI 394n |
| `DECIMAL_PLACES` | Ondalık basamak | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` ile birlikte |
| `PERCENTAGE_FORMAT` | Yüzde biçimi | AI 394n |
| `ISO_UNIT_CODE` | ISO birim kodu | Ölçü AI'ları |
| `ISO_UNIT_NAME` | ISO birim adı | Ölçü AI'ları |
| `MONETARY_AMOUNT` | Parasal tutar | Fiyat AI'ları |
| `MONETARY_AMOUNT_DISPLAY` | Parasal tutar (biçimlendirilmiş) | Fiyat AI'ları |

### Coğrafi Koordinatlar

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `LATITUDE` | Enlem | AI 4309 |
| `LONGITUDE` | Boylam | AI 4309 |
| `GEO_COORDINATES` | Coğrafi koordinatlar | AI 4309 |
| `LATITUDE_DMS` | Enlem (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Boylam (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Coğrafi koordinatlar (DMS) | AI 4309 |

### Üretim Yöntemi

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Üretim yöntemi kodu | AI 7010 |
| `PRODUCTION_METHOD` | Üretim yöntemi | AI 7010 |

### AIDC Ortam Türü

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC ortam türü kodu | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC ortam türü | AI 7241 |

### Toplamdan Parça

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Parça numarası | AI 8006 |
| `PIECE_TOTAL` | Toplam parça | AI 8006 |
| `PIECE_OF_TOTAL` | Toplamdan parça | AI 8006 |

### Bileşen Ayrışmaları

Java'da yazılmış bir zenginleştirici tarafından değil, `content/ai-content.json` içindeki
bildirimsel bileşen ayrışmaları tarafından üretilen anahtarlar — hepsi bileşik bir AI
değerinin adlandırılmış parçalarını gösterir. Bu ekteki diğer her anahtarın tersine,
bunların **`GS1Constants_Enricher` içinde sabiti yoktur**: dizi sabitiyle eşleştirin ya da
türü `GS1AIInterpretation.getType()` üzerinden okuyun.

| Tür anahtarı | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Kontrol hanesi | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Seri numarası | AI 253, 255, 8003 |
| `POSTAL_CODE` | Posta kodu | AI 421 |
| `PROCESSOR_ID` | İşleyici tanımlayıcısı | AI 7030–7039 |

Buradaki `CHECK_DIGIT`'in genel bileşen ayrışma anahtarı olduğuna dikkat edin; bu, yukarıdaki
zenginleştiriciye özgü `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` ve
`EID_CHECK_DIGIT` anahtarlarından ayrıdır.

### Çeşitli

| Anahtar sabiti | Görünen etiket | Üreten |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Değer | Mantıksal / bayrak AI'ları (4321–4323) |
| `DECODED_TEXT` | Çözülmüş metin | Serbest metin AI'ları |
