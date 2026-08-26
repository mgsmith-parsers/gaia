# GaiaBuilder — Geliştirici Kılavuzu

## İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [GS1 ve General Specifications hakkında](#gs1-ve-general-specifications-hakkında)
3. [Hızlı Başlangıç](#hızlı-başlangıç)
4. [Nasıl çalışır](#nasıl-çalışır)
5. [Eleman dizileri oluşturma](#eleman-dizileri-oluşturma)
   - [Öznitelik AI'ları kendi tanımlama anahtarını gerektirir](#öznitelik-aiları-kendi-tanımlama-anahtarını-gerektirir)
6. [Digital Link URI'leri oluşturma](#digital-link-urileri-oluşturma)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Doğrulama ve hatalar](#doğrulama-ve-hatalar)
   - [Fırlatan oluşturma yöntemleri](#fırlatan-oluşturma-yöntemleri)
   - [Fırlatmayan tryBuild\* yöntemleri](#fırlatmayan-trybuild-yöntemleri)
   - [Hata iletisi dili](#hata-iletisi-dili)
   - [BuildResult](#buildresult)
9. [Kontrol haneleri](#kontrol-haneleri)
10. [İş Parçacığı Güvenliği](#iş-parçacığı-güvenliği)
11. [API Başvurusu](#api-başvurusu)

---

## Genel Bakış

`GaiaBuilder`, [`GaiaParser`](GaiaParser-Turkish.md)'ın ters yöndeki karşılığıdır: bir Uygulama Tanımlayıcı (AI) ve değer çiftleri derlemini düzgün biçimlenmiş bir GS1 **eleman dizisine** ya da bir **GS1 Digital Link URI'sine** dönüştürür. Siz AI'ları ve tam veri değerlerini verirsiniz; oluşturucu bunları birleştirir, sonucu `GaiaParser`'ın kullandığı motorun aynısıyla doğrular ve çıktıyı sunar.

Oluşturucu, doğrulamayı *kendi aday çıktısını ayrıştırarak* yaptığı için döndürdüğü her şeyin `GaiaParser` ile temiz biçimde ayrıştırılacağı güvencelidir — ikisi neyin düzgün biçimlendiği konusunda asla ayrışamaz.

**Giriş noktası sınıfı:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 ve General Specifications hakkında

**GS1**, tedarik zinciri tanımlaması ve veri değişimi için açık standartlar geliştiren ve sürdüren küresel, kâr amacı gütmeyen bir kuruluştur. Standartları perakende, sağlık hizmetleri, lojistik, yiyecek-içecek hizmetleri ve daha birçok sektörde kullanılır; tüketici ambalajlarındaki ürün barkodlarından ilaç dozlarının seri numarayla izlenmesine dek her şeyi kapsar.

Bu oluşturucunun uyguladığı her şeyin yetkili kaynağı **GS1 General Specifications**'tır — şunları tanımlayan tek bir belge:

- Tüm Uygulama Tanımlayıcı (AI) kodları, veri başlıkları, biçimleri ve doğrulama kuralları
- AI eleman dizilerinin oluşturulması ve kodlanmasına ilişkin sözdizimi kuralları
- Barkod semboloji gereksinimleri ve AIM Kod Kimliği atamaları
- Kontrol hanesi ve kontrol karakteri algoritmaları
- İki haneli yıl çözümlemesi (kayan pencere kuralı)
- Data Matrix, QR Code, GS1-128, GS1 DataBar ve diğer taşıyıcı belirtimleri

GS1 General Specifications her yıl güncellenir. Güncel baskı ve destekleyici kaynaklar şu adreste bulunabilir:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications'ın **Sürüm 26.0 (Onaylandı, Oca 2026)** düzeyini uygular.

GS1 Digital Link URI'leri, oluşturucunun Digital Link URI'lerini sunarken uyguladığı birincil tanımlama anahtarlarını, anahtar niteleyici sırasını ve veri özniteliği kodlamasını tanımlayan tamamlayıcı bir standarda, **GS1 Digital Link: URI Syntax**'a tabidir:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax standardının **Sürüm 1.7.0 (Onaylandı, Ağu 2026)** düzeyini uygular.

Bu belge boyunca bölüm göndermeleri GS1 General Specifications'a işaret eder (ör. "Table 7-5", "section 7.12"); yalnızca Digital Link bölüm numaraları (ör. "§4.9", "§4.12") bunun dışındadır ve GS1 Digital Link: URI Syntax standardına işaret eder.

---

## Hızlı Başlangıç

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

Ham AI sabitleri yerine `GS1Constants_AICodes` sabitlerini yeğleyin (bkz. [ayrıştırıcı kılavuzundaki Ek A](GaiaParser-Turkish.md#ek-a--ai-dizi-sabitleri)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Nasıl çalışır

Her oluşturma aynı yolu izler:

1. **Birleştirme** — AI/değer çiftleri bir aday eleman dizisinde birleştirilir. FNC1 grup ayırıcısı (`0x1D`), *ayırıcı gerektiren* ve son eleman olmayan her AI'dan sonra eklenir. Uzunluğu önceden belirlenmiş AI'lar (GTIN, tarihler, sabit uzunluklu ölçüler) ayırıcı almaz; diğerleri alır. (Bilinmeyen AI'lar bu adıma hiç ulaşmaz — `ai(...)` onları anında reddeder; bkz. [Eleman dizileri oluşturma](#eleman-dizileri-oluşturma).)
2. **Doğrulama** — aday, `GaiaParser` tarafından `CONTENT` kipinde ayrıştırılır. Her değer kendi AI'ının biçimine ve kontrol hanesine karşı denetlenir; yapısal kurallar (zorunlu ve dışlanan AI eşleşmeleri) uygulanır. Ayrıştırma geçerli değilse oluşturma başarısız olur.
3. **Sunum** —
   - Eleman dizisi için, doğrulanmış nesnenin `toElementString()` değeri döndürülür.
   - Digital Link için, her elemana DL rolü atanır (birincil anahtar, anahtar niteleyici ya da veri özniteliği), anahtar niteleyici dizisi doğrulanır, URI üretilir ve ardından **üretilen URI, geçerli bir Digital Link olarak geri döndüğünden emin olmak için yeniden ayrıştırılır** — bu, dizi birleştirme ve yüzde kodlama adımı için bir güvenlik denetimidir. Geri dönmezse `GaiaBuilderException` fırlatılır.

Bu, `DLSyntaxParser`'daki yeniden kurma mantığını yansıtır; böylece ayırıcı yerleşimi ve doğrulama tam olarak ayrıştırıcının beklediği gibi olur.

---

## Eleman dizileri oluşturma

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** anında doğrulanır: bilinen bir GS1 Uygulama Tanımlayıcı değilse `ai(...)` bir `IllegalArgumentException` fırlatır. (Oluşturucu, AI'ı ayrıştırmadan önce değerle birleştirir; bu yüzden `"99999"` gibi bilinmeyen ya da fazla uzun bir AI'ın burada yakalanması gerekir — aksi hâlde sessizce farklı bir AI olarak yeniden belirteçlenirdi.) **Değer** ise daha sonra, oluşturma sırasında doğrulanır.
- Değerler, varsa kontrol hanesi dahil **eksiksiz** olmalıdır. Oluşturucu sizin için kontrol hanesi hesaplamaz ve eklemez — bkz. [Kontrol haneleri](#kontrol-haneleri).
- AI'lar eklediğiniz sırayla üretilir. Oluşturucu, GS1'in yapısının gerektirdiği yerlere FNC1 ayırıcıları koyar; onları kendiniz eklemeyin.
- **Hiç AI olmadan** oluşturmak, boş bir `getErrors()` listesiyle `GaiaBuilderException("No AIs supplied")` fırlatır — hiçbir `GaiaError` taşımayan tek başarısızlık budur.
- Değeri kendi biçim ya da kontrol hanesi kuralında başarısız olan bir AI, oluşturmayı başarısız kılar.

### Öznitelik AI'ları kendi tanımlama anahtarını gerektirir

AI'ların çoğu, GS1 General Specifications'ın bir tanımlama anahtarıyla birlikte taşınmasını istediği *özniteliklerdir* ve oluşturucu bunu uygular — doğrulamayı sözdizimi aşamasının tamamından geçirir ve bunun bir kaçış yolu yoktur. Tek başına bir parti/lot ya da seri numarası geçerli bir eleman dizisi **değildir**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Tanımlama anahtarları (GTIN `01`, SSCC `00`, GLN `414`, …) ve firma içi AI'lar (`90`–`99`) tümüyle meşru biçimde tek başlarına durabilir. Geri kalan her şeyin bir eşlikçiye gereksinimi vardır.

> `GaiaParser`'a bu denetimi `ParseConfig.skipRequiresCheck(true)` ile atlaması söylenebilir; `GaiaBuilder` bilinçli olarak bunun bir karşılığını sunmaz — amacı standarda uyumlu çıktı üretmektir. Bilinçli olarak eksik bir eleman dizisi birleştirmek için onu kendiniz birleştirin ve denetim kapalıyken ayrıştırın.

---

## Digital Link URI'leri oluşturma

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Geçerli bir Digital Link, tam olarak bir **birincil tanımlama anahtarı** gerektirir (ör. GTIN `01`, GLN `414`, SSCC `00`). Oluşturucu, verdiğiniz her AI'ı sınıflandırır:

| Rol | Nasıl sunulur | Örnek |
|------|-------------|---------|
| Birincil tanımlama anahtarı | Etki alanı/önek sonrasındaki yol parçası | `/01/09506000134352` |
| Anahtar niteleyici (CPV `22`, parti `10`, seri `21`, …) | Sonraki yol parçaları, **§4.9 kanonik sırasında** (sizin ekleme sıranızda değil) | `/10/LOT-ABC` |
| Veri özniteliği (geri kalan her şey) | Sorgu parametreleri, **AI anahtarına göre sözlük sırasında** (§4.12) | `?17=271231` |

Niteleyiciler sunum sırasında yeniden sıralandığı için onları sırasız vermenin bir sakıncası yoktur — `ai("10", …)`'dan önce gelen `ai("21", …)` yine `/10/LOT/21/SER` olarak sunulur. Birincil anahtar için kabul edilebilir olması gereken yalnızca onların *kümesidir*.

Hem yoldaki hem sorgudaki değerler yüzde kodlanır.

Oluşturma şu durumlarda başarısız olur (`GaiaBuilderException` fırlatır ya da başarısız bir `BuildResult` döndürür):

- AI'lar arasında **hiçbir** birincil tanımlama anahtarı yoksa;
- **birden çok** birincil tanımlama anahtarı varsa;
- bir AI Digital Link'te **yasaklıysa** (`03`, `8014`);
- **anahtar niteleyici dizisi** seçilen birincil anahtar için geçersizse (o anahtarı izlemeyen bir niteleyici ya da izin verilen sırasının dışındaki niteleyiciler).

---

## BuilderDigitalLinkConfig

Şemayı, etki alanını, yol önekini, ek sorgu parametrelerini ve parçayı denetlemek için bir `BuilderDigitalLinkConfig` verin:

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

| Oluşturucu yöntemi | Amacı | Öntanımlı |
|----------------|---------|---------|
| `scheme(String)` | URI şeması; `http` ya da `https` olmalıdır | `https` |
| `domain(String)` | Çözümleyici yetkilisi — ana bilgisayar ya da `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | İlk birincil anahtardan önceki yol parçaları; baştaki ve sondaki eğik çizgiler normalleştirilir | *(yok)* |
| `baseUrl(String)` | Bir URL'yi `scheme` + `domain` + `pathPrefix` olarak bölen kolaylık | — |
| `addQueryParam(String, String)` | Ek sorgu parametresi; AI veri özniteliklerinden **sonra**, ekleme sırasında eklenir; yüzde kodlanır | — |
| `fragment(String)` | URI parçası (baştaki `#` olmadan); yüzde kodlanır | *(yok)* |

`build()` yapılandırmayı anında doğrular: `http(s)` olmayan bir şema ya da boş bir etki alanı `IllegalArgumentException` fırlatır.

- `BuilderDigitalLinkConfig.canonical()` (takma adı `defaultConfig()`), hiçbir ek olmaksızın öntanımlı `https://id.gs1.org`'dur — bağımsız değişkensiz `buildDigitalLinkUri()`'nin kullandığı ve `GS1AIObject.getCanonicalDigitalLink()`'in ürettiği şeyin tam olarak aynısı.
- `baseUrl("http://id.example.org:8080/r")` → şema `http`, etki alanı `id.example.org:8080`, yol öneki `/r`.
- Ek sorgu parametreleri her zaman AI kaynaklı özniteliklerden sonra gelir; böylece kanonik AI sırası (§4.12) korunur.

`BuilderDigitalLinkConfig` değiştirilemezdir; tek bir örneği gönül rahatlığıyla yeniden kullanın.

---

## Doğrulama ve hatalar

### Fırlatan oluşturma yöntemleri

`buildElementString()`, `buildDigitalLinkUri()` ve `buildDigitalLinkUri(BuilderDigitalLinkConfig)`, AI'lar düzgün biçimlenmiş bir çıktı oluşturamadığında **`GaiaBuilderException`** (denetlenmeyen bir `RuntimeException`) fırlatır:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **İçerik** başarısızlıklarında (hatalı kontrol hanesi, biçim uyuşmazlığı, eksik/yasaklı AI) `getErrors()` ayrıştırıcının `GaiaError` nesnelerini taşır — [ayrıştırıcı kılavuzunda belgelenen](GaiaParser-Turkish.md#gaiaerror) nesnelerin aynısı.
- **Digital Link yapısı** başarısızlıklarında (birincil anahtar yok, birden çok birincil anahtar, yasaklı AI, geçersiz anahtar niteleyici dizisi) `getErrors()`, oluşturucunun diline yerelleştirilmiş tek bir `GaiaError` taşır (kod `GE-L008`, `GE-L012`, `GE-L013` ya da `GE-L014`).

### Fırlatmayan tryBuild\* yöntemleri

Girdi kullanıcıdan geldiğinde ve başarısızlık beklenen, ele alınabilir bir sonuç olduğunda, denetim akışını özel durumlarla yönetmek yerine `tryBuild*` biçimlerini kullanın. Bunlar fırlatmak yerine bir [`BuildResult`](#buildresult) döndürür:

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

| Fırlatan | Fırlatmayan |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Her `tryBuild*` yöntemi, fırlatan ikiziyle aynı doğrulama çekirdeğini paylaşır; yalnızca başarısızlık sınırı farklıdır.

### Hata iletisi dili

İçerik doğrulama hataları, yerelleştirilmiş hata kataloğundan alınır. `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` tarafından taşınan `GaiaError` iletilerinin dilini seçmek için `language(...)` çağırın; öntanımlı dil İngilizcedir:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Bu, `GaiaParser`'ın `ParseConfig` aracılığıyla kabul ettiği `GaiaConstants.Language` ayarının aynısıdır; böylece oluşturucu ile ayrıştırıcı aynı biçimde yerelleştirir.

Hem **içerik** hem de **Digital Link yapısı** başarısızlıklarının (birincil anahtar yok, birden çok birincil anahtar, yasaklı AI, geçersiz anahtar niteleyici dizisi) `GaiaError` iletileri ortak hata kataloğu aracılığıyla yerelleştirilir — ikincisi `GE-L008`, `GE-L012`, `GE-L013` ve `GE-L014` kodlarını kullanır.

### BuildResult

`BuildResult` (paket `tools.pantheum.gaia.result`), bir `tryBuild*` çağrısının sonucunu betimleyen değiştirilemez bir değer türüdür:

| Yöntem | Başarıda | Başarısızlıkta |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | Sunulan dizi | `null` |
| `getMessage()` | `null` | Başarısızlığın açıklaması |
| `getErrors()` | Boş liste | Doğrulama hataları (`GaiaBuilderException.getErrors()` ile aynı) |

---

## Kontrol haneleri

Oluşturucu kontrol hanelerini doğrular ama **hesaplamaz** — değerleriniz kontrol hanesini zaten içermelidir. Bir tanesini hesaplamak için `GS1Utils.calculateCheckDigit` kullanın:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)`, verilen hanelere standart GS1 modulo-10 algoritmasını uygular ve `0`–`9` arasında bir kontrol hanesi döndürür; girdi `null`, boş ya da sayısal değilse `-1` döndürür.

---

## İş Parçacığı Güvenliği

`GaiaBuilder` iş parçacığı güvenli **değildir** ve tek kullanımlık olacak biçimde tasarlanmıştır: `create()` çağırın, AI'ları ekleyin, bir kez oluşturun. Her çıktı için yeni bir oluşturucu kurun; tek bir oluşturucuyu iş parçacıkları arasında paylaşmayın.

`BuilderDigitalLinkConfig` (ve onun `BuildResult` çıktıları) değiştirilemezdir ve gönül rahatlığıyla paylaşılabilir — başlangıçta tek bir yapılandırma kurun ve birçok oluşturucuda yeniden kullanın.

---

## API Başvurusu

### `GaiaBuilder`

| Yöntem | Açıklama |
|--------|-------------|
| `static GaiaBuilder create()` | Yeni, boş bir oluşturucu başlatır. |
| `GaiaBuilder ai(String ai, String value)` | Bir AI'ı ve tam değerini ekler. İkisinden biri `null` ise ya da `ai` bilinen bir GS1 Uygulama Tanımlayıcı değilse `IllegalArgumentException` fırlatır. |
| `GaiaBuilder language(GaiaConstants.Language language)` | İçerik doğrulama hata iletilerinin dilini ayarlar (öntanımlı İngilizce). `null` yok sayılır. |
| `String buildElementString()` | Bir GS1 eleman dizisi sunar. Başarısızlıkta `GaiaBuilderException` fırlatır. |
| `String buildDigitalLinkUri()` | Kanonik bir Digital Link URI'si sunar. Başarısızlıkta `GaiaBuilderException` fırlatır. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config`'e göre bir Digital Link URI'si sunar. Başarısızlıkta `GaiaBuilderException` fırlatır. |
| `BuildResult tryBuildElementString()` | Fırlatmayan eleman dizisi oluşturma. |
| `BuildResult tryBuildDigitalLinkUri()` | Fırlatmayan kanonik Digital Link oluşturma. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config`'e göre fırlatmayan Digital Link oluşturma. |

### `BuilderDigitalLinkConfig`

| Üye | Açıklama |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Öntanımlı `https://id.gs1.org`. |
| `static Builder builder()` | Yeni bir yapılandırma oluşturucusu. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Çözümlenmiş şema, çözümleyici yetkilisi ve yol öneki. |
| `getExtraQueryParams()` | Ek sorgu parametreleri, ekleme sırasında. |
| `getFragment()` | Parça ya da `null`. |

### `GaiaBuilderException`

| Üye | Açıklama |
|--------|-------------|
| `getErrors()` | Başarısızlığa yol açan `GaiaError` nesneleri — içerik başarısızlığında ayrıştırıcı hataları, ya da tek bir Digital Link yapısal hatası (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Asla `null` olmaz. |

### `BuildResult`

| Üye | Açıklama |
|--------|-------------|
| `isSuccess()` | Oluşturmanın başarılı olup olmadığı. |
| `getValue()` | Başarıda sunulan çıktı; başarısızlıkta `null`. |
| `getMessage()` | Başarısızlıkta başarısızlık açıklaması; başarıda `null`. |
| `getErrors()` | Başarısızlıkta doğrulama hataları; başarıda boş. Asla `null` olmaz. |
| `getTiming()` | Oluşturma işleminin `ProcessingTiming` değeri (başlangıç zamanı, işlem süresi) ya da `null`. |

---

Ayrıca bkz.: ayrıştırma tarafı, AI eleman modeli, hata başvurusu ve AI ile yorum sabitlerine ilişkin ekler için **[GaiaParser — Geliştirici Kılavuzu](GaiaParser-Turkish.md)**.
