# GaiaParser — Gabatarwa Mai Sauri

Ka tantance payload ɗin barcode na GS1 zuwa bayanai da aka tsara, aka tabbatar, kuma mutum zai
iya karantawa, cikin kimanin minti goma. Wannan ita ce gajeriyar hanya; **[Jagorar GaiaParser
ga Mai Haɓakawa](GaiaParser-Hausa.md)** ita ce cikakkiyar magana, kuma **[GaiaBuilder](GaiaBuilder-Hausa.md)**
kuwa yana ƙunshe da hanyar akasi (gina element string da Digital Link URI).

## Abubuwan Ciki

1. [Ƙara Gaia a cikin aikinka](#1-ƙara-gaia-a-cikin-aikinka)
2. [Tantance wani abu](#2-tantance-wani-abu)
3. [Karanta sakamakon](#3-karanta-sakamakon)
4. [Magance tantancewar da ta kasa](#4-magance-tantancewar-da-ta-kasa)
5. [Abubuwa biyu da za su cije ka](#5-abubuwa-biyu-da-za-su-cije-ka)
6. [Prefix ɗin na'urar daukar hoto da Digital Link suna aiki kai tsaye](#6-prefix-ɗin-naurar-daukar-hoto-da-digital-link-suna-aiki-kai-tsaye)
7. [Yi aiki kaɗan: yanayin tantancewa](#7-yi-aiki-kaɗan-yanayin-tantancewa)
8. [Canza yare da tsarin kwanan wata](#8-canza-yare-da-tsarin-kwanan-wata)
9. [Tsaftace shigarwa mai rikici](#9-tsaftace-shigarwa-mai-rikici)
10. [Inda za a je gaba](#10-inda-za-a-je-gaba)

---

## 1. Ƙara Gaia a cikin aikinka

Ba a buga Gaia a Maven Central ba, don haka ka gina core sau ɗaya sannan ka shigar da shi cikin
ma'ajiyarka ta gida:

```bash
cd gaia && mvn install
```

Sannan ka dogara da shi:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Wannan shi ne cikakken jerin dependency da za ka rubuta. Jar ɗin bakin ciki ne, don haka
dependency guda ɗaya na Gaia a matakin compile — `com.fasterxml.jackson.core:jackson-databind`
— yana zuwa ta hanyar wucewa; idan ginanka ya riga ya ƙayyade wata sigar Jackson, wannan
ƙayyadewar ce ke da rinjaye kuma Gaia zai yi amfani da ita. Gaia yana nufin **Java 11**, kuma
jar ɗin guda yana gudu ba tare da canji ba a kan kowace JVM ta baya.

> Tsallake test suite ɗin core (`mvn install -DskipTests`) yana mayar da mintuna kaɗan zuwa
> daƙiƙai kaɗan yayin da kake farawa.

---

## 2. Tantance wani abu

Aji guda ɗaya, babu saiti:

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

`parse(String)` yana gudanar da **cikakken** layi: nahawu, tabbatar da abin ciki, da fassara.
Wannan shi ne tsoho mai kyau — sai ka rage shi daga baya idan ka sami dalilin da ka auna.

---

## 3. Karanta sakamakon

`ParseResult.getAiObject()` yana riƙe da AI da aka gano. Ka nemi wani takamaiman AI ta lamba
maimakon ta wuri:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Kowane element yana ɗauke da jerin **fassara** — ma'anar da aka warware daga bayan ɗanyen
lambobin, wadda matakin fassara ya samar:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

An mayar da `getLabel()` wa yare kuma an yi shi don nunawa. Don ka *karanta* wata ƙima a cikin
code, sai ka neme ta ta maɓallin nau'inta mai tsayayye:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI daban-daban suna samar da maɓallai daban-daban — GTIN yana bayar da prefix ɗin kamfaninsa,
nau'in GTIN, da lambar bincike; farashi kuwa yana bayar da kuɗi da adadin ma'ushi. Cikakken
jerin yana a [Ƙarin Bayani B](GaiaParser-Hausa.md#ƙarin-bayani-b--constant-ɗin-maɓallin-fassara), kuma
constant ɗin suna zaune a `GS1Constants_Enricher`. Ba kowane AI ne yake da fassara ba: kundi/lot
na rubutu maras tsari ba shi da abin da za a ciro, don haka jerinsa mara komai ne.

---

## 4. Magance tantancewar da ta kasa

Payload mara inganci sakamako ne na yau da kullum, ba exception ba — `parse` ba ya taɓa jefawa
saboda mummunan bayanan GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Ka rarraba a kan `getId()`, kada ka taɓa yin haka a kan saƙon.** An mayar da saƙonnin wa yare
kuma kalmominsu ba yarjejeniya ba ne — kuma a yanzu suna ɗauke da aibin alamar magana da aka
sani (ninkakken `''` a sama), wanda aka ambata a [Maganar Kuskure](GaiaParser-Hausa.md#maganar-kuskure).

Tambayoyi biyu daban, hanyoyi biyu daban:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Tantancewa tana tsayawa daga sauka da zarar wani mataki ya kasa, don haka lambar bincike mara
kyau tana nufin za ka sami kurakuran tabbatarwa amma ba fassara ba.

### Gargaɗi ba ya sanya sakamako ya zama mara inganci

Wasu dubawa shawara ce kawai. Ana bayar da rahoton prefix na kamfanin GS1 da ba a gane ba, amma
payload ɗin har yanzu yana da kyakkyawan tsari:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Ka yi amfani da `getIssues()` idan kana son duka biyun. Idan tsarin aikinka dole ya ƙi prefix
da ba a sani ba, sai ka duba `getWarnings()` a fili — `isValid()` ba zai yi maka hakan ba.

---

## 5. Abubuwa biyu da za su cije ka

### Separator ɗin GS, da dalilin da ya sa barinsa ya fi kuskure muni

AI mai tsawo mai canzawa yana gudu har sai ya sami **harafin GS** (ASCII `0x1D`, wanda ake kira
FNC1 a symbology na barcode) ko ƙarshen string. Idan wani AI yana biyo bayansa, wannan separator
wajibi ne:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Ka bar shi kuma **ba** za ka sami kuskure ba — za ka sami amsa mara kyau amma da cikakken tabbaci:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` yana `X..20`, don haka bisa inganci yana haɗiye `LOT-ABC21SN-98765` kuma parser ba shi da
hanyar sanin cewa ba haka aka nufa ba. Babu abin da ke ƙasa da zai iya gyara wannan, don haka ka
gyara separator a tushe: ka karanta byte ɗin na'urar daukar hoto a matsayin **ISO-8859-1** don
`0x1D` ya rayu, kuma ka rubuta `""` a literal ɗin string na Java. AI masu tsayayyen tsawo
(`01`, `17`, `3103`) ba sa buƙatar separator — parser ya san tsawonsu.

### Yawancin AI ba za su iya tsayawa su kaɗai ba

Kundi/lot, serial, ranar ƙarewa da makamantansu *halaye* ne: GS1 General Specifications suna
buƙatar su yi tafiya tare da maɓallin ganewa, kuma Gaia yana aiwatar da hakan.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Ka ƙara GTIN sai ya wuce. Idan gaske kana buƙatar tantance wani sashe — gwajin unit, ko daukar
hoto na wani ɓangare — sai ka kashe dubawar:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Prefix ɗin na'urar daukar hoto da Digital Link suna aiki kai tsaye

Ba sai ka gaya wa Gaia surar shigarwar ba — yana gano dukan surori huɗu. Ka ba shi duk abin da
na'urar daukar hoto ta ba ka.

**Prefix ɗin AIM Code ID** yana nuna symbology kuma ana cire shi ta atomatik:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 Digital Link URI** yana bi ta tabbatarwa da wadatarwa guda:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Tunda surori biyun suna sauka a cikin `GS1AIObject` guda, code ɗin da yake amfani da abin da aka
ɗauka ba sai ya damu da wace ce ta zo ba — kuma `toElementString()` /
`getCanonicalDigitalLink()` suna juyawa tsakaninsu.

Ana kuma cire **prefix ɗin correlation mai lamba 8** (`12345678~…`) kuma ana ajiye shi a
`getCorrelationInfo()`, idan layinka yana amfani da shi.

---

## 7. Yi aiki kaɗan: yanayin tantancewa

Tsohon yana yin komai. Ka nemi ƙasa da haka idan kana buƙatar sashe ɗaya kaɗai na amsar:

| Yanayi | Yana amsawa | Tsada |
|---|---|---|
| `DATA_CARRIER` | Wane symbology ne wannan? | Mafi arha — babu tantance AI ko kaɗan, `getAiObject()` yana `null` |
| `SYNTAX` | Lambobin AI da tsawonsu suna da kyakkyawan tsari? | Babu lambar bincike, babu fassara |
| `CONTENT` | Wannan bayanan GS1 ne masu inganci? | Cikakkiyar tabbatarwa, babu fassara |
| `INTERPRETATION` | Menene ma'anarsa? | **Tsoho** — komai |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Ka nemi `CONTENT` idan kana tabbatarwa da yawa kuma ba ka taɓa nuna rarrabuwar, kuma
`DATA_CARRIER` idan kana buƙatar turawar abin da aka ɗauka zuwa mai magancewa da ya dace kaɗai.

---

## 8. Canza yare da tsarin kwanan wata

An fassara saƙonnin kuskure, lakabin fassara, da bayanan AI zuwa **yare 35**; kwanan wata kuwa
ana nuna su yadda kake so. Duk waɗannan suna cikin `ParseConfig` guda ɗaya da ba a canzawa:

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

Ba a taɓa mayar da ƙimomi wa yare ba — lakabi, bayanai da saƙonni kaɗai — don haka
`"2026-12-31"` da `"09506000134352"` ma'ana ɗaya suke da ita a kowane yare. Ka gina saitin sau
ɗaya a farkon aiki sannan ka raba shi; ba a canza shi.

---

## 9. Tsaftace shigarwa mai rikici

Idan tushenka yana fitar da bakunan HRI da aka buga ko filaye marasa amfani, akwai **input
modifier** guda biyu a cikin core waɗanda suke gyara payload kafin tantancewa:

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

Ba a kunna komai a matsayin tsoho, kuma duka biyun suna da gargaɗi — fili da baka haruffan
bayanai ne masu inganci na GS1, don haka ka yi amfani da su kawai ga tushen da ka sani. Duba
[Modifier na ciki](GaiaParser-Hausa.md#modifier-na-ciki), wanda kuma yake bayyana dalilin da ya sa
cire baka dole ya mayar da separator ɗin da bakunan suke nunawa.

---

## 10. Inda za a je gaba

- **[Jagorar GaiaParser ga Mai Haɓakawa](GaiaParser-Hausa.md)** — layin dalla-dalla, cikakken
  samfurin sakamako, kowace lambar kuskure, da ƙarin bayanan AI da na maɓallin fassara.
- **[Jagorar GaiaBuilder ga Mai Haɓakawa](GaiaBuilder-Hausa.md)** — ka gina element string da
  Digital Link URI daga nau'ikan AI da ƙima.
- **[Maganar HTTP ta Gaia API](../../gaia-api-reference.md)** — injin guda ta hanyar HTTP, idan ba ka
  son saka library ɗin a ciki.
- **[ai-codes.txt](../../ai-codes.txt)** — jeri mai sauƙi na `(AI) TITLE` don nema da sauri.

### Sigar layi biyar

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
