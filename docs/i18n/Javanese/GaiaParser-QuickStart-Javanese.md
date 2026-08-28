# GaiaParser — Wiwitan Cepet

Parsing-en payload barcode GS1 dadi data sing kasusun, disahake, lan bisa diwaca manungsa, ing
sajroning kira-kira sepuluh menit. Iki dalan cendhake; **[Pandhuan GaiaParser kanggo
Pangembang](GaiaParser-Javanese.md)** iku referensi sing jangkep, dene **[GaiaBuilder](GaiaBuilder-Javanese.md)**
nyakup arah sing kosok balen (mbangun string elemen lan Digital Link URI).

## Isi

1. [1. Tambahna Gaia ing proyekmu](#1-tambahna-gaia-ing-proyekmu)
2. [2. Parsing-a sawijining bab](#2-parsing-a-sawijining-bab)
3. [3. Wacanen asile](#3-wacanen-asile)
4. [4. Nangani parsing sing gagal](#4-nangani-parsing-sing-gagal)
5. [5. Rong bab sing bakal nyokot kowe](#5-rong-bab-sing-bakal-nyokot-kowe)
6. [6. Prefiks scanner lan Digital Link mlaku dhewe](#6-prefiks-scanner-lan-digital-link-mlaku-dhewe)
7. [7. Nyuda pakaryan: modhe parsing](#7-nyuda-pakaryan-modhe-parsing)
8. [8. Ngganti basa lan format tanggal](#8-ngganti-basa-lan-format-tanggal)
9. [9. Ngresiki input sing morat-marit](#9-ngresiki-input-sing-morat-marit)
10. [10. Sabanjure menyang ngendi](#10-sabanjure-menyang-ngendi)

---

## 1. Tambahna Gaia ing proyekmu

Gaia ora diterbitake ing Maven Central, mula bangunen core-e sepisan banjur pasangen ing
repositori lokalmu:

```bash
cd gaia && mvn install
```

Banjur gumantunga marang iku:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Iku wis kabeh dhaptar dependensi sing kudu koktulis. Jar-e tipis, mula siji-sijine dependensi
Gaia ing lingkup compile — `com.fasterxml.jackson.core:jackson-databind` — teka kanthi transitif;
yen build-mu wis nemtokake versi Jackson tinamtu, iku sing menang lan Gaia bakal nganggo iku.
Gaia nyasar **Java 11**, lan jar sing padha mlaku tanpa owah ing saben JVM sing luwih anyar.

> Ngliwati test suite duweke core (`mvn install -DskipTests`) ngowahi pirang-pirang menit dadi
> pirang-pirang detik nalika kowe lagi wiwit.

---

## 2. Parsing-a sawijining bab

Siji kelas, tanpa konfigurasi:

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

`parse(String)` nglakokake pipeline **sakabehe**: sintaksis, validasi isi, lan interpretasi.
Iku standar sing bener — ciyutna mengko yen kowe nemu alesan sing wis kokukur.

---

## 3. Wacanen asile

`ParseResult.getAiObject()` ngemot AI sing wis diweruhi. Njupuka salah siji sing tinamtu nganggo
kode, dudu nganggo posisi:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Saben elemen nggawa dhaptar **interpretasi** — teges sing wis didekode ing mburine angka mentah,
sing digawe dening tahap interpretasi:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` iku wis dilokalake lan dienggo kanggo tampilan. Kanggo *maca* sawijining nilai ing
kode, golekana nganggo kunci tipene sing tetep:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI sing beda ngasilake kunci sing beda — GTIN menehi prefiks perusahaane, tipe GTIN, lan angka
centhang; rega menehi mata uang lan jumlah desimal. Dhaptar sing jangkep ana ing
[Lampiran B](GaiaParser-Javanese.md#lampiran-b--konstanta-kunci-interpretasi), lan konstantane manggon ing
`GS1Constants_Enricher`. Ora saben AI duwe interpretasi: bets/lot sing wujud teks bebas ora ana
sing bisa dijupuk, mula dhaptare kothong.

---

## 4. Nangani parsing sing gagal

Payload sing ora sah iku asil lumrah, dudu exception — `parse` ora tau nguncalake amarga data GS1
sing ala:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Cabangna ing `getId()`, aja pisan-pisan ing pesene.** Pesene wis dilokalake lan tetembungane
dudu prajanjian — lan saiki pesene isih nggawa cacad tandha petik sing wis dingerteni (dhobel
`''` ing ndhuwur), sing kacathet ing [Referensi Kesalahan](GaiaParser-Javanese.md#referensi-kesalahan).

Rong pitakon sing beda, rong metode sing beda:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Parsing mandheg mudhun sawise sawijining tahap gagal, mula angka centhang sing kleru tegese kowe
oleh kesalahan validasi nanging ora oleh interpretasi.

### Pepeling ora ndadekake asil dadi ora sah

Ana sawetara pamriksan sing mung pituduh. Prefiks perusahaan GS1 sing ora diakoni dilapurake,
nanging payloade tetep bener susunane:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Nganggoa `getIssues()` yen kowe kepengin loro-lorone. Yen alur kerjamu kudu nolak prefiks sing
ora dikenal, priksanen `getWarnings()` kanthi cetha — `isValid()` ora bakal nindakake iku
kanggo kowe.

---

## 5. Rong bab sing bakal nyokot kowe

### Pamisah GS, lan kenapa ngliwati iku luwih ala tinimbang kesalahan

AI sing dawane variabel mlaku nganti tekan **aksara GS** (ASCII `0x1D`, sing diarani FNC1 ing
simbologi barcode) utawa pungkasaning string. Yen ana AI liya sing ngetutake, pamisah kasebut
iku wajib:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Lewatna lan kowe **ora** bakal oleh kesalahan — kowe bakal oleh wangsulan sing kleru nanging
diaturake kanthi mantep:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` iku `X..20`, mula kanthi sah iku nguntal `LOT-ABC21SN-98765` lan parser ora duwe cara
kanggo ngerti yen iku dudu sing dikarepake. Ora ana sing ing sabanjure bisa mulihake iki, mula
benerna pamisahe wiwit ing sumbere: wacanen byte saka scanner minangka **ISO-8859-1** supaya
`0x1D` lestari, lan tulisen `""` ing literal string Java. AI sing dawane tetep (`01`, `17`,
`3103`) ora butuh pamisah — parser wis ngerti dawane.

### Akeh-akehe AI ora bisa ngadeg dhewe

Bets/lot, serial, tanggal kadaluwarsa lan kanca-kancane iku *atribut*: GS1 General Specifications
mbutuhake kabeh mau lelungan bareng karo kunci identifikasi, lan Gaia ngetrapake iku.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Tambahna GTIN-e banjur lulus. Yen kowe pancen butuh mem-parsing sawijining cuwilan — sawijining
unit test, sawijining scan sing sebagean — patenana pamriksane:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Prefiks scanner lan Digital Link mlaku dhewe

Kowe ora perlu ngandhani Gaia bab wujude input — iku ndeteksi kabeh papat wujude. Wenehna wae apa
sing diwenehake scanner.

**Prefiks AIM Code ID** nuduhake simbologine lan dicopot kanthi otomatis:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** ngliwati validasi lan pangsugih sing padha:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Amarga loro wujude tumiba ing `GS1AIObject` sing padha, kode sing nampa asil scan ora perlu
mikirake endi sing teka — lan `toElementString()` / `getCanonicalDigitalLink()` sing ngowahi
saka siji menyang liyane.

**Prefiks correlation cacah 8 angka** (`12345678~…`) uga dicopot lan disimpen ing
`getCorrelationInfo()`, yen pipeline-mu nganggo iku.

---

## 7. Nyuda pakaryan: modhe parsing

Standare nindakake kabeh. Njaluka sing luwih sithik yen kowe mung butuh sebagean saka wangsulane:

| Modhe | Sing diwangsuli | Ragade |
|---|---|---|
| `DATA_CARRIER` | Iki simbologi apa? | Paling murah — babar pisan ora ana parsing AI, `getAiObject()` iku `null` |
| `SYNTAX` | Kode AI lan dawane wis kabentuk bener? | Tanpa angka centhang, tanpa interpretasi |
| `CONTENT` | Apa iki data GS1 sing sah? | Validasi jangkep, tanpa interpretasi |
| `INTERPRETATION` | Apa tegese? | **Standar** — kabeh |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Njupuka `CONTENT` yen kowe nyahake akeh-akehan lan ora tau nampilake panyigarane, lan
`DATA_CARRIER` yen kowe mung butuh ngarahake asil scan menyang panangan sing bener.

---

## 8. Ngganti basa lan format tanggal

Pesen kesalahan, label interpretasi, lan katrangan AI wis diterjemahake menyang **35 basa**;
tanggal ditampilake sakarepmu. Kabeh mau ana ing siji `ParseConfig` sing ora bisa diowahi:

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

Nilai ora tau dilokalake — mung label, katrangan lan pesen — mula `"2026-12-31"` lan
`"09506000134352"` tegese padha ing saben basa. Gaweya konfigurasine sepisan nalika wiwitan
banjur enggonen bareng; iku ora bisa diowahi.

---

## 9. Ngresiki input sing morat-marit

Yen sumbermu ngetokake kurung HRI sing dicithak utawa spasi sing kliwat, ana rong **input
modifier** sing kasedhiya ing core lan mbenerake payloade sadurunge parsing:

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

Ora ana sing diuripake minangka standar, lan loro-lorone nduweni pepeling — spasi lan kurung iku
aksara data GS1 sing sah, mula etrapna mung ing sumber sing kokngerteni. Deleng
[Modifier bawaan](GaiaParser-Javanese.md#modifier-bawaan), sing uga nerangake kenapa nyopot kurung
kudu mbalekake pamisah sing dimaksud dening kurung mau.

---

## 10. Sabanjure menyang ngendi

- **[Pandhuan GaiaParser kanggo Pangembang](GaiaParser-Javanese.md)** — pipeline kanthi rinci, model
  asil sing jangkep, saben kode kesalahan, lan lampiran AI lan kunci interpretasi.
- **[Pandhuan GaiaBuilder kanggo Pangembang](GaiaBuilder-Javanese.md)** — mbangun string elemen lan
  Digital Link URI saka pasangan AI lan nilai.
- **[Referensi HTTP Gaia API](../../gaia-api-reference.md)** — mesin sing padha liwat HTTP, yen kowe
  luwih seneng ora nyelehake library-ne ing njero.
- **[ai-codes.txt](../../ai-codes.txt)** — dhaptar `(AI) TITLE` sing prasaja kanggo panggolekan cepet.

### Versi limang baris

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
