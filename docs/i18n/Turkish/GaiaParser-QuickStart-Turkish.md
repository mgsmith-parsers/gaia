# GaiaParser — Hızlı Başlangıç

Bir GS1 barkod yükünü on dakika kadar bir sürede yapılandırılmış, doğrulanmış, insan
tarafından okunabilir veriye dönüştürün. Bu kısa yoldur; **[GaiaParser Geliştirici
Kılavuzu](GaiaParser-Turkish.md)** eksiksiz başvuru belgesidir ve
**[GaiaBuilder](GaiaBuilder-Turkish.md)** ters yönü (eleman dizileri ve Digital Link URI'leri
oluşturmayı) ele alır.

## İçindekiler

1. [Gaia'yı projenize ekleyin](#1-gaiayı-projenize-ekleyin)
2. [Bir şey ayrıştırın](#2-bir-şey-ayrıştırın)
3. [Sonucu okuyun](#3-sonucu-okuyun)
4. [Başarısız bir ayrıştırmayı ele alın](#4-başarısız-bir-ayrıştırmayı-ele-alın)
5. [Sizi tökezletecek iki şey](#5-sizi-tökezletecek-iki-şey)
6. [Tarayıcı önekleri ve Digital Link kendiliğinden çalışır](#6-tarayıcı-önekleri-ve-digital-link-kendiliğinden-çalışır)
7. [Daha az iş yapın: ayrıştırma kipleri](#7-daha-az-iş-yapın-ayrıştırma-kipleri)
8. [Dili ve tarih biçimini değiştirin](#8-dili-ve-tarih-biçimini-değiştirin)
9. [Dağınık girdiyi temizleyin](#9-dağınık-girdiyi-temizleyin)
10. [Buradan nereye](#10-buradan-nereye)

---

## 1. Gaia'yı projenize ekleyin

Gaia, Maven Central'da yayımlanmıyor; bu yüzden çekirdeği bir kez derleyip yerel deponuza
kurun:

```bash
cd gaia && mvn install
```

Sonra ona bağımlılık verin:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Yazmanız gereken bağımlılığın tamamı bu. Jar incedir: Gaia'nın tek derleme kapsamlı
bağımlılığı — `com.fasterxml.jackson.core:jackson-databind` — geçişli olarak gelir; derleme
düzeneğiniz zaten bir Jackson sürümünü sabitliyorsa o sabitleme geçerli olur ve Gaia onu
kullanır. Gaia **Java 11**'i hedefler ve aynı jar sonraki her JVM sürümünde değişmeden
çalışır.

> Çekirdek sınama takımını atlamak (`mvn install -DskipTests`), işe yeni başlarken birkaç
> dakikayı birkaç saniyeye indirir.

---

## 2. Bir şey ayrıştırın

Tek sınıf, yapılandırma yok:

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

`parse(String)`, **tüm** işlem hattını çalıştırır: sözdizimi, içerik doğrulaması, yorum. Doğru
öntanımlı budur — bunun için ölçülmüş bir gerekçeniz olduğunda daraltın.

---

## 3. Sonucu okuyun

`ParseResult.getAiObject()` çözümlenmiş AI'ları tutar. Belirli bir AI'ı konumuna göre değil,
koduna göre alın:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Her eleman bir **yorum** listesi taşır — ham hanelerin ardındaki, yorum aşamasının ürettiği
açılmış anlam:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` yerelleştirilmiştir ve görüntülemek içindir. Kod içinde bir değeri *okumak* için
onu bunun yerine değişmez tür anahtarıyla arayın:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Farklı AI'lar farklı anahtarlar üretir — GTIN kendi firma önekini, GTIN türünü ve kontrol
hanesini verir; fiyat ise para birimini ve ondalık tutarı verir. Tam liste
[Ek B](GaiaParser-Turkish.md#ek-b--yorum-anahtarı-sabitleri)'dedir, sabitler ise
`GS1Constants_Enricher` içindedir. Her AI'ın yorumu olmaz: bir parti/lot numarası, kendisinden
türetilecek hiçbir şey bulunmayan serbest metindir; bu yüzden listesi boştur.

---

## 4. Başarısız bir ayrıştırmayı ele alın

Geçersiz bir yük olağan bir sonuçtur, bir özel durum değil — `parse` bozuk GS1 verisi için
asla fırlatmaz:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` üzerinden dallanın, asla ileti üzerinden değil.** İletiler yerelleştirilmiştir ve
sözcük seçimleri bir sözleşme değildir — ayrıca şu anda bilinen bir tırnaklama kusuru
taşırlar (yukarıdaki `''` ikilemesi); bu kusur
[Hata Başvurusu](GaiaParser-Turkish.md#hata-başvurusu)'nda belgelenmiştir.

İki farklı soru, iki farklı yöntem:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Bir aşama başarısız olur olmaz ayrıştırma derine inmeyi bırakır; bu yüzden hatalı bir kontrol
hanesi, doğrulama hataları alacağınız ama hiç yorum almayacağınız anlamına gelir.

### Uyarılar sonucu geçersiz kılmaz

Bazı denetimler bilgilendirme amaçlıdır. Tanınmayan bir GS1 firma öneki raporlanır, ancak yük
yine de düzgün biçimlenmiştir:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

İkisini birden istediğinizde `getIssues()` kullanın. İş akışınız tanınmayan önekleri
reddetmeyi gerektiriyorsa `getWarnings()`'i açıkça denetleyin — `isValid()` bunu sizin için
yapmayacaktır.

---

## 5. Sizi tökezletecek iki şey

### GS ayırıcısı ve onu atlamanın neden bir hatadan beter olduğu

Değişken uzunluklu bir AI, bir **GS karakterine** (ASCII `0x1D`; barkod sembolojilerinde
FNC1 denir) ya da dizinin sonuna dek uzanır. Ardından başka bir AI geldiğinde bu ayırıcı
zorunludur:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Onu atlarsanız bir hata **almazsınız** — kendinden emin biçimde yanlış bir yanıt alırsınız:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` bir `X..20`'dir; dolayısıyla `LOT-ABC21SN-98765`'in tamamını yutması gayet
mantıklıdır ve ayrıştırıcının bunun kastınız olmadığını bilmesinin hiçbir yolu yoktur. Bunu
sonradan hiçbir şey geri getiremez; bu yüzden ayırıcıyı kaynağında doğru yapın: `0x1D`'nin
korunması için tarayıcı baytlarını **ISO-8859-1** olarak okuyun ve Java dizi sabitlerinde
`""` yazın. Sabit uzunluklu AI'ların (`01`, `17`, `3103`) ayırıcıya gereksinimi yoktur —
ayrıştırıcı uzunluklarını bilir.

### AI'ların çoğu tek başına durmaz

Parti/lot, seri numarası, son kullanma tarihi ve benzerleri *özniteliklerdir*: GS1 General
Specifications bunların bir tanımlama anahtarıyla birlikte taşınmasını gerektirir ve Gaia
bunu uygular.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Bir GTIN ekleyin, geçer. Gerçekten bir parçayı ayrıştırmanız gerekiyorsa — bir birim sınaması,
kısmi bir tarama — denetimi kapatın:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Tarayıcı önekleri ve Digital Link kendiliğinden çalışır

Gaia'ya girdinin hangi biçimde olduğunu söylemenize gerek yok — dört biçimin hepsini algılar.
Tarayıcınız size ne verdiyse onu geçirin.

**AIM Semboloji Tanımlayıcı öneki** sembolojiyi belirler ve kendiliğinden ayıklanır:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI'si** aynı doğrulama ve zenginleştirmeden geçer:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Her iki biçim de aynı `GS1AIObject`'e vardığı için taramayı tüketen kodun hangisinin geldiğini
umursamasına gerek kalmaz — `toElementString()` / `getCanonicalDigitalLink()` de birini
diğerine dönüştürür.

**8 haneli ilişkilendirme öneki** (`12345678~…`) de, akışınız onu kullanıyorsa, ayıklanır ve
`getCorrelationInfo()` içinde korunur.

---

## 7. Daha az iş yapın: ayrıştırma kipleri

Öntanımlı olan her şeyi yapar. Yanıtın yalnızca bir bölümüne gereksiniminiz varsa daha azını
isteyin:

| Kip | Neyi yanıtlar | Maliyet |
|---|---|---|
| `DATA_CARRIER` | Bu hangi semboloji? | En ucuzu — hiç AI ayrıştırması yok, `getAiObject()` `null` |
| `SYNTAX` | AI kodları ve uzunlukları düzgün biçimlenmiş mi? | Kontrol hanesi yok, yorum yok |
| `CONTENT` | Bu geçerli GS1 verisi mi? | Tam doğrulama, yorum yok |
| `INTERPRETATION` | Bu ne anlama geliyor? | **Öntanımlı** — her şey |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Yüksek hacimde doğrulama yapıp dökümü hiç göstermiyorsanız `CONTENT`'i, yalnızca taramayı
doğru işleyiciye yönlendirmeniz gerekiyorsa `DATA_CARRIER`'ı seçin.

---

## 8. Dili ve tarih biçimini değiştirin

Hata iletileri, yorum etiketleri ve AI açıklamaları **35 dile** çevrilmiştir; tarihler
dilediğiniz gibi gösterilebilir. Bunların hepsi tek bir değiştirilemez `ParseConfig` içinde
toplanır:

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

Değerler asla yerelleştirilmez — yalnızca etiketler, açıklamalar ve iletiler — bu yüzden
`"2026-12-31"` ve `"09506000134352"` her dilde aynı anlama gelir. Yapılandırmayı başlangıçta
bir kez kurun ve paylaşın; değiştirilemezdir.

---

## 9. Dağınık girdiyi temizleyin

Kaynağınız yazdırılmış HRI parantezleri ya da başıboş boşluklar üretiyorsa, çekirdekte
ayrıştırmadan önce yükü düzelten iki **girdi değiştiricisi** vardır:

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

Öntanımlı olarak hiçbiri etkin değildir ve ikisinin de uyarıları vardır — boşluklar da
parantezler de geçerli GS1 veri karakterleridir; bu yüzden yalnızca tanıdığınız bir kaynağa
uygulayın. Bkz. [Yerleşik değiştiriciler](GaiaParser-Turkish.md#yerleşik-değiştiriciler); orada,
parantezleri ayıklamanın neden onların ima ettiği ayırıcıyı geri koyması gerektiği de
açıklanır.

---

## 10. Buradan nereye

- **[GaiaParser Geliştirici Kılavuzu](GaiaParser-Turkish.md)** — işlem hattının ayrıntıları, eksiksiz
  sonuç modeli, her hata kodu ve AI ile yorum anahtarlarına ilişkin ekler.
- **[GaiaBuilder Geliştirici Kılavuzu](GaiaBuilder-Turkish.md)** — AI/değer çiftlerinden eleman
  dizileri ve Digital Link URI'leri oluşturun.
- **[Gaia API HTTP Başvurusu](../../gaia-api-reference.md)** — kitaplığı gömmeyi yeğlemiyorsanız
  aynı motor HTTP üzerinden.
- **[ai-codes.txt](../../ai-codes.txt)** — hızlı arama için düz bir `(AI) TITLE` listesi.

### Beş satırlık sürüm

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
