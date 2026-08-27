# GaiaParser — Mabilisang Panimula

I-parse ang payload ng isang barcode ng GS1 tungo sa nakabalangkas, napatunayan, at mababasang
datos sa loob ng humigit-kumulang sampung minuto. Ito ang maikling daan; ang
**[Gabay sa GaiaParser para sa Developer](GaiaParser-Tagalog.md)** ang buong sanggunian, at ang
**[GaiaBuilder](GaiaBuilder-Tagalog.md)** naman ang sumasaklaw sa kabaligtarang direksyon
(ang pagbuo ng mga element string at Digital Link URI).

## Nilalaman

1. [Idagdag ang Gaia sa iyong proyekto](#1-idagdag-ang-gaia-sa-iyong-proyekto)
2. [Mag-parse ng kung ano](#2-mag-parse-ng-kung-ano)
3. [Basahin ang resulta](#3-basahin-ang-resulta)
4. [Hawakan ang bigong pag-parse](#4-hawakan-ang-bigong-pag-parse)
5. [Dalawang bagay na kakagatin ka](#5-dalawang-bagay-na-kakagatin-ka)
6. [Kusang gumagana ang mga prefix ng scanner at ang mga Digital Link](#6-kusang-gumagana-ang-mga-prefix-ng-scanner-at-ang-mga-digital-link)
7. [Bawasan ang trabaho: mga modo ng pag-parse](#7-bawasan-ang-trabaho-mga-modo-ng-pag-parse)
8. [Palitan ang wika at ang format ng petsa](#8-palitan-ang-wika-at-ang-format-ng-petsa)
9. [Linisin ang magulong input](#9-linisin-ang-magulong-input)
10. [Saan susunod](#10-saan-susunod)

---

## 1. Idagdag ang Gaia sa iyong proyekto

Hindi nailathala ang Gaia sa Maven Central, kaya buuin ang core nang minsanan at i-install
ito sa iyong lokal na repository:

```bash
cd gaia && mvn install
```

Pagkatapos ay idepende rito:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Iyan na ang buong listahan ng dependency na kailangan mong isulat. Manipis ang jar, kaya
ang nag-iisang dependency ng Gaia sa saklaw ng compile —
`com.fasterxml.jackson.core:jackson-databind` — ay dumarating nang palipat; kung may
nakatakda nang bersyon ng Jackson ang iyong build, iyon ang mananaig at iyon ang gagamitin ng
Gaia. Nakatuon ang Gaia sa **Java 11**, at tumatakbo ang gayunding jar nang walang pagbabago
sa bawat mas bagong JVM.

> Ang paglaktaw sa test suite ng core (`mvn install -DskipTests`) ay nagpapaikli ng ilang
> minuto tungo sa ilang segundo habang nagsisimula ka pa lamang.

---

## 2. Mag-parse ng kung ano

Isang klase, walang pagsasaayos:

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

Pinatatakbo ng `parse(String)` ang **buong** daloy: sintaks, pagpapatunay ng nilalaman, at
interpretasyon. Iyon ang tamang default — paliitin ito sa bandang huli kung may nasukat kang
dahilan.

---

## 3. Basahin ang resulta

Nasa `ParseResult.getAiObject()` ang mga natukoy na AI. Kunin ang isang tiyak na AI sa
pamamagitan ng code at hindi ng puwesto:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

May dalang listahan ng **interpretasyon** ang bawat elemento — ang nadecode na kahulugan sa
likod ng hilaw na mga digit, na ginawa ng yugto ng interpretasyon:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

Naisalokal ang `getLabel()` at inilaan ito sa pagpapakita. Upang *basahin* ang isang halaga sa
code, hanapin ito sa pamamagitan ng matatag nitong susi ng uri:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Magkakaibang susi ang ibinubunga ng magkakaibang AI — ang GTIN ay nagbibigay ng prefix ng
kumpanya nito, ng uri ng GTIN, at ng check digit; ang presyo naman ay nagbibigay ng pera at ng
halagang desimal. Nasa [Apendiks B](GaiaParser-Tagalog.md#apendiks-b--mga-konstant-na-susi-ng-interpretasyon)
ang buong listahan, at nakatira ang mga konstant sa `GS1Constants_Enricher`. Hindi lahat ng AI
ay may interpretasyon: walang mahuhugot sa isang batch/lote na malayang teksto, kaya walang
laman ang listahan nito.

---

## 4. Hawakan ang bigong pag-parse

Karaniwang kalalabasan ang isang di-wastong payload at hindi isang exception — hindi kailanman
naghahagis ang `parse` dahil sa masamang datos ng GS1:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Magsanga sa `getId()`, hindi kailanman sa mensahe.** Naisalokal ang mga mensahe at hindi
kasunduan ang pananalita nila — at may dala silang kilalang depekto sa panipi sa ngayon (ang
dinobleng `''` sa itaas), na nabanggit sa [Sanggunian sa Error](GaiaParser-Tagalog.md#sanggunian-sa-error).

Dalawang magkaibang tanong, dalawang magkaibang method:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Humihinto sa pagbaba ang isang pag-parse kapag nabigo ang isang yugto, kaya ang maling check
digit ay nangangahulugang makukuha mo ang mga error sa pagpapatunay ngunit walang interpretasyon.

### Hindi ginagawang di-wasto ng mga babala ang isang resulta

Pahiwatig lamang ang ilang pagsusuri. Iniuulat ang isang di-kilalang prefix ng kumpanya ng GS1,
ngunit matatag pa rin ang balangkas ng payload:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Gamitin ang `getIssues()` kapag gusto mo ang dalawa. Kung kailangang tanggihan ng iyong daloy
ang mga di-kilalang prefix, tiyakang suriin ang `getWarnings()` — hindi iyon gagawin ng
`isValid()` para sa iyo.

---

## 5. Dalawang bagay na kakagatin ka

### Ang separator na GS, at kung bakit mas masahol ang paglaktaw rito kaysa sa isang error

Tumatakbo ang isang AI na variable ang haba hanggang sa isang **karakter na GS** (ASCII `0x1D`,
tinatawag na FNC1 sa mga symbology ng barcode) o hanggang sa dulo ng string. Kapag may sumusunod
na ibang AI, sapilitan ang separator na iyon:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Laktawan mo ito at **hindi** ka makakakuha ng error — makakakuha ka ng sagot na mali ngunit
tiwalang-tiwala:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

`X..20` ang AI `10`, kaya lubos na wasto ang paglunok nito sa `LOT-ABC21SN-98765` at walang
paraan ang parser upang malaman na hindi iyon ang layunin. Walang makababawi nito sa dulo ng
daloy, kaya ayusin ang separator sa pinagmulan mismo: basahin ang mga byte ng scanner bilang
**ISO-8859-1** upang manatili ang `0x1D`, at isulat ang `""` sa mga literal na string sa Java.
Hindi nangangailangan ng separator ang mga AI na nakapirmi ang haba (`01`, `17`, `3103`) —
alam ng parser ang haba nila.

### Hindi makatatayo nang mag-isa ang karamihan sa mga AI

Ang batch/lote, serial, petsa ng pag-expire at ang mga kauri nila ay *katangian*: hinihiling ng
GS1 General Specifications na sumama sila sa isang susi sa pagkilala, at ipinatutupad iyon ng Gaia.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Idagdag ang GTIN at papasa na ito. Kung tunay mong kailangang mag-parse ng isang bahagi — isang
unit test, isang bahagyang scan — patayin ang pagsusuri:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Kusang gumagana ang mga prefix ng scanner at ang mga Digital Link

Hindi mo kailangang sabihin sa Gaia kung ano ang anyo ng input — natutukoy nito ang lahat ng
apat na anyo. Ipakain mo lamang ang kung anuman ang ibinigay ng scanner.

Tinutukoy ng isang **prefix na AIM Code ID** ang symbology at kusa itong inaalis:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

Dumadaan ang isang **GS1 Digital Link URI** sa gayunding pagpapatunay at pagpapayaman:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Dahil parehong napupunta sa gayunding `GS1AIObject` ang dalawang anyo, hindi kailangang alalahanin
ng code na kumukonsumo ng isang scan kung alin sa dalawa ang dumating — at ang
`toElementString()` / `getCanonicalDigitalLink()` ang naglilipat sa pagitan nila.

Inaalis din ang isang **8-digit na prefix ng correlation** (`12345678~…`) at itinatabi ito sa
`getCorrelationInfo()`, kung gumagamit nito ang iyong daloy.

---

## 7. Bawasan ang trabaho: mga modo ng pag-parse

Ginagawa ng default ang lahat. Humingi ng mas kaunti kapag bahagi lamang ng sagot ang kailangan mo:

| Modo | Sinasagot | Gastos |
|---|---|---|
| `DATA_CARRIER` | Anong symbology ito? | Pinakamura — walang anumang pag-parse ng AI, `null` ang `getAiObject()` |
| `SYNTAX` | Wasto ba ang anyo ng mga code at haba ng AI? | Walang check digit, walang interpretasyon |
| `CONTENT` | Wastong datos ba ito ng GS1? | Buong pagpapatunay, walang interpretasyon |
| `INTERPRETATION` | Ano ang ibig sabihin nito? | **Default** — lahat |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Piliin ang `CONTENT` kapag nagpapatunay ka nang maramihan at hindi mo kailanman ipinapakita ang
paghahati, at ang `DATA_CARRIER` naman kapag kailangan mo lamang irutang isang scan sa tamang
tagahawak.

---

## 8. Palitan ang wika at ang format ng petsa

Naisalin sa **35 wika** ang mga mensahe ng error, ang mga label ng interpretasyon, at ang mga
paglalarawan ng AI; at ipinapakita ang mga petsa ayon sa gusto mo. Nasa iisang di-nababagong
`ParseConfig` ang lahat ng ito:

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

Hindi kailanman naisasalokal ang mga halaga — ang mga label, paglalarawan at mensahe lamang —
kaya ang `"2026-12-31"` at ang `"09506000134352"` ay may gayunding kahulugan sa bawat wika.
Bumuo ng config nang minsanan sa pagsisimula at pagsaluhan ito; di-nababago ito.

---

## 9. Linisin ang magulong input

Kung naglalabas ang iyong pinagmulan ng mga nakalimbag na panaklong ng HRI o ng mga ligaw na
espasyo, may dalawang **input modifier** na kasama sa core at inaayos nila ang payload bago
mag-parse:

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

Walang naka-enable bilang default, at may babala ang dalawa — wastong mga karakter ng datos ng
GS1 ang espasyo at ang panaklong, kaya ipatupad lamang sila sa pinagmulang alam mo. Tingnan ang
[Mga nakapaloob na modifier](GaiaParser-Tagalog.md#mga-nakapaloob-na-modifier), na nagpapaliwanag din kung bakit
kailangang ibalik ng pag-alis ng panaklong ang separator na ipinahihiwatig ng mga ito.

---

## 10. Saan susunod

- **[Gabay sa GaiaParser para sa Developer](GaiaParser-Tagalog.md)** — ang daloy nang detalyado, ang
  buong modelo ng resulta, ang bawat code ng error, at ang mga apendiks ng AI at ng susi ng
  interpretasyon.
- **[Gabay sa GaiaBuilder para sa Developer](GaiaBuilder-Tagalog.md)** — bumuo ng mga element string at
  Digital Link URI mula sa mga pares ng AI at halaga.
- **[Sanggunian sa HTTP ng Gaia API](../../gaia-api-reference.md)** — ang gayunding makina sa
  pamamagitan ng HTTP, kung ayaw mong isama ang library sa loob.
- **[ai-codes.txt](../../ai-codes.txt)** — isang patag na listahang `(AI) TITLE` para sa mabilisang paghahanap.

### Ang bersyong limang linya

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
