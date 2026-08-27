# GaiaBuilder — Gabay para sa Developer

## Talaan ng Nilalaman

1. [Pangkalahatang-tanaw](#pangkalahatang-tanaw)
2. [Tungkol sa GS1 at sa General Specifications](#tungkol-sa-gs1-at-sa-general-specifications)
3. [Mabilisang Panimula](#mabilisang-panimula)
4. [Paano Ito Gumagana](#paano-ito-gumagana)
5. [Pagbuo ng mga Element String](#pagbuo-ng-mga-element-string)
   - [Kailangan ng mga AI na katangian ang kanilang susi sa pagkilala](#kailangan-ng-mga-ai-na-katangian-ang-kanilang-susi-sa-pagkilala)
6. [Pagbuo ng mga Digital Link URI](#pagbuo-ng-mga-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Pagpapatunay at mga Error](#pagpapatunay-at-mga-error)
   - [Mga method ng pagbuo na naghahagis](#mga-method-ng-pagbuo-na-naghahagis)
   - [Mga method na tryBuild\* na hindi naghahagis](#mga-method-na-trybuild-na-hindi-naghahagis)
   - [Wika ng mensahe ng error](#wika-ng-mensahe-ng-error)
   - [BuildResult](#buildresult)
9. [Mga Check Digit](#mga-check-digit)
10. [Kaligtasan sa Thread](#kaligtasan-sa-thread)
11. [Sanggunian sa API](#sanggunian-sa-api)

---

## Pangkalahatang-tanaw

Ang `GaiaBuilder` ang kabaligtaran ng [`GaiaParser`](GaiaParser-Tagalog.md): ginagawa nitong wastong **element string** ng GS1 o **GS1 Digital Link URI** ang isang hanay ng mga pares ng Application Identifier (AI) at halaga. Ikaw ang nagbibigay ng mga AI at ng buo nilang halaga ng datos; pinagsasama-sama sila ng builder, pinapatunayan ang resulta sa pamamagitan ng gayunding makina na ginagamit ng `GaiaParser`, at inilalabas ang output.

Dahil nagpapatunay ang builder sa pamamagitan ng *pag-parse sa sarili nitong kandidatong output*, tiyak na malinis na mababalik sa `GaiaParser` ang anumang ibinabalik nito — hindi kailanman magkakasalungat ang dalawa tungkol sa kung ano ang wasto.

**Klase ng pasukan:** `tools.pantheum.gaia.GaiaBuilder`

---

## Tungkol sa GS1 at sa General Specifications

Ang **GS1** ay isang pandaigdigang organisasyong di-pangkalakal na bumubuo at nagpapanatili ng mga bukás na pamantayan para sa pagkilala sa supply chain at sa palitan ng datos. Ginagamit ang mga pamantayan nito sa retail, healthcare, logistics, foodservice, at sa marami pang ibang industriya, mula sa mga barcode ng produkto sa pakete ng mamimili hanggang sa serialisadong pagsubaybay sa dosis ng gamot.

Ang makapangyarihang sanggunian para sa lahat ng ipinatutupad ng builder na ito ay ang **GS1 General Specifications** — isang dokumento na naglalarawan ng:

- Lahat ng code ng Application Identifier (AI), ang kanilang pamagat ng datos, format, at panuntunan sa pagpapatunay
- Ang mga panuntunan sa sintaks para sa pagbuo at pag-encode ng mga element string ng AI
- Ang mga kinakailangan sa symbology ng barcode at ang pagtatalaga ng AIM Code ID
- Ang mga algoritmo para sa check digit at check character
- Ang paglutas sa dalawang-digit na taon (ang panuntunang sliding-window)
- Ang mga espesipikasyon ng Data Matrix, QR Code, GS1-128, GS1 DataBar, at iba pang carrier

Taun-taon inaayos ang GS1 General Specifications. Ang kasalukuyang edisyon at ang mga kaugnay na sanggunian ay makukuha sa:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

Ipinatutupad ng GAIA ang **Release 26.0 (Pinagtibay, Enero 2026)** ng GS1 General Specifications.

Ang mga GS1 Digital Link URI ay pinamamahalaan ng katuwang na pamantayang **GS1 Digital Link: URI Syntax**, na naglalarawan ng mga pangunahing susi sa pagkilala, ng pagkakasunod-sunod ng key qualifier, at ng pag-encode ng data attribute na ipinapatupad ng builder sa paglalabas ng mga Digital Link URI:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

Ipinatutupad ng GAIA ang **Release 1.7.0 (Pinagtibay, Agosto 2026)** ng pamantayang GS1 Digital Link: URI Syntax.

Ang mga sanggunian sa seksyon sa buong dokumentong ito ay tumutukoy sa GS1 General Specifications (halimbawa, "Table 7-5", "section 7.12"), maliban sa mga numero ng seksyon ng Digital Link (halimbawa, "§4.9", "§4.12") na tumutukoy naman sa pamantayang GS1 Digital Link: URI Syntax.

---

## Mabilisang Panimula

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

Mas mabuting gamitin ang mga konstant na `GS1Constants_AICodes` kaysa sa hilaw na string ng AI (tingnan ang [Apendiks A ng gabay sa parser](GaiaParser-Tagalog.md#apendiks-a--mga-konstant-na-string-ng-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Paano Ito Gumagana

Iisang landas ang tinatahak ng bawat pagbuo:

1. **Pagsasama-sama** — pinagdudugtong ang mga pares ng AI at halaga tungo sa isang kandidatong element string. May ipinapasok na separator ng pangkat na FNC1 (`0x1D`) pagkatapos ng bawat AI na *nangangailangan ng separator* at hindi ang huling elemento. Ang mga AI na paunang natakda ang haba (GTIN, mga petsa, mga sukat na nakapirmi ang haba) ay hindi kumukuha ng separator; ang lahat ng iba ay kumukuha. (Hindi umaabot dito ang mga di-kilalang AI — kaagad silang tinatanggihan ng `ai(...)`; tingnan ang [Pagbuo ng mga Element String](#pagbuo-ng-mga-element-string).)
2. **Pagpapatunay** — pinapa-parse ang kandidato sa modong `CONTENT` sa pamamagitan ng `GaiaParser`. Sinusuri ang bawat halaga laban sa format at check digit ng AI nito, at ipinatutupad ang mga panuntunang pambalangkas (mga kinakailangan at ipinagbabawal na pares ng AI). Kung hindi wasto ang pag-parse, nabibigo ang pagbuo.
3. **Paglalabas** —
   - Para sa element string, ibinabalik ang `toElementString()` ng napatunayang object.
   - Para sa Digital Link, itinatalaga sa bawat elemento ang papel nito sa DL (pangunahing susi, key qualifier, o data attribute), pinapatunayan ang serye ng key qualifier, inilalabas ang URI, at **muling pina-parse ang inilabas na URI upang tiyaking bumabalik ito bilang wastong Digital Link** — isang pagsusuring pangkaligtasan sa hakbang ng pagbubuo ng string at ng percent-encoding. Kung hindi ito bumalik, naghahagis ng `GaiaBuilderException`.

Katulad ito ng lohika ng muling pagbuo sa `DLSyntaxParser`, kaya magkatugma ang paglalagay ng separator at ang pagpapatunay sa inaasahan ng parser.

---

## Pagbuo ng mga Element String

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- Kaagad pinapatunayan ang **AI**: naghahagis ang `ai(...)` ng `IllegalArgumentException` kung hindi ito kilalang GS1 Application Identifier. (Pinagdudugtong ng builder ang AI at ang halaga bago mag-parse, kaya dapat mahuli rito ang di-kilala o masyadong mahabang AI gaya ng `"99999"` — kung hindi, tahimik itong magiging ibang AI matapos ang muling pagti-token.) Ang **halaga** ay pinapatunayan sa bandang huli, sa oras ng pagbuo.
- Dapat **buo** ang mga halaga, kasama na ang anumang check digit. Hindi kinakalkula o idinaragdag ng builder ang mga check digit para sa iyo — tingnan ang [Mga Check Digit](#mga-check-digit).
- Inilalabas ang mga AI sa pagkakasunod na idinagdag mo sila. Naglalagay ang builder ng mga separator na FNC1 kung saan kailangan ng sintaks ng GS1; hindi ikaw ang naglalagay ng mga separator.
- Ang pagbuo nang **wala kahit isang AI** ay naghahagis ng `GaiaBuilderException("No AIs supplied")` na may walang lamang listahang `getErrors()` — ito lamang ang pagkabigong walang dalang `GaiaError`.
- Ang isang AI na ang halaga ay hindi pumapasa sa panuntunan ng format o ng check digit nito ay nagpapabigo sa pagbuo.

### Kailangan ng mga AI na katangian ang kanilang susi sa pagkilala

Karamihan sa mga AI ay *katangian* na hinihiling ng GS1 General Specifications na samahan ng isang susi sa pagkilala, at ipinatutupad iyon ng builder — nagpapatunay ito sa buong yugto ng sintaks, at walang paraan upang umiwas. **Hindi** wastong element string ang isang batch/lote o serial nang mag-isa:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Ang mga susi sa pagkilala (GTIN `01`, SSCC `00`, GLN `414`, …) at ang mga AI na panloob sa kumpanya (`90`–`99`) ay lubos na wastong makatatayo nang mag-isa. Ang lahat ng iba ay nangangailangan ng katuwang nila.

> Maaaring sabihan ang `GaiaParser` na laktawan ang pagsusuring ito sa pamamagitan ng `ParseConfig.skipRequiresCheck(true)`; sinasadyang walang inilalantad na katumbas ang `GaiaBuilder` — layunin nitong maglabas ng output na sumusunod sa pamantayan. Upang bumuo ng element string na sinasadyang hindi kumpleto, pagdugtungin ito nang mag-isa at i-parse ito nang nakapatay ang pagsusuri.

---

## Pagbuo ng mga Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Nangangailangan ang wastong Digital Link ng eksaktong isang **pangunahing susi sa pagkilala** (halimbawa, GTIN `01`, GLN `414`, SSCC `00`). Inuuri ng builder ang bawat AI na ibinigay:

| Papel | Paano inilalabas | Halimbawa |
|------|-------------|---------|
| Pangunahing susi sa pagkilala | Segment ng path matapos ang domain/prefix | `/01/09506000134352` |
| Key qualifier (CPV `22`, batch `10`, serial `21`, …) | Mga sumusunod na segment ng path, sa **kanonikong pagkakasunod ng §4.9** (hindi sa pagkakasunod ng pagdaragdag mo) | `/10/LOT-ABC` |
| Data attribute (lahat ng iba) | Mga query parameter, **isinaayos ayon sa titik ng susi ng AI** (§4.12) | `?17=271231` |

Dahil muling isinasaayos ang mga qualifier sa paglalabas, walang problema kung hindi mo sila sunod-sunod na ibinigay — kahit nauna ang `ai("21", …)` sa `ai("10", …)`, `/10/LOT/21/SER` pa rin ang inilalabas. Ang *hanay* lamang ang kailangang tanggapin ng pangunahing susi.

Naka-percent-encode ang mga halaga sa path at sa query.

**Nabibigo** ang pagbuo (naghahagis ng `GaiaBuilderException`, o nagbabalik ng bigong `BuildResult`) kapag:

- **walang** pangunahing susi sa pagkilala sa mga AI;
- may **higit sa isang** pangunahing susi sa pagkilala;
- may AI na **ipinagbabawal** sa mga Digital Link (`03`, `8014`);
- di-wasto ang **serye ng key qualifier** para sa napiling pangunahing susi (halimbawa, isang qualifier na hindi kabilang sa susing iyon, o mga qualifier na wala sa pinapayagang pagkakasunod).

---

## BuilderDigitalLinkConfig

Magpasa ng `BuilderDigitalLinkConfig` upang piliin ang scheme, domain, prefix ng path, karagdagang query parameter, at fragment:

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

| Method ng builder | Layunin | Default |
|----------------|---------|---------|
| `scheme(String)` | Scheme ng URI; dapat `http` o `https` | `https` |
| `domain(String)` | Awtoridad — host o `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Mga segment ng path bago ang unang pangunahing susi; isinasa-normal ang mga slash sa unahan/hulihan | *(wala)* |
| `baseUrl(String)` | Pampadali na humahati ng isang URL tungo sa `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Karagdagang query parameter, idinaragdag **matapos** ang mga data attribute ng AI, ayon sa pagkakapasok; naka-percent-encode | — |
| `fragment(String)` | Fragment ng URL (walang paunang `#`); naka-percent-encode | *(wala)* |

Kaagad pinapatunayan ng `build()` ang pagsasaayos: naghahagis ng `IllegalArgumentException` ang isang scheme na hindi `http(s)` o ang walang lamang domain.

- Ang `BuilderDigitalLinkConfig.canonical()` (alyas na `defaultConfig()`) ang default na `https://id.gs1.org` na walang anumang dagdag — ito mismo ang ginagamit ng `buildDigitalLinkUri()` na walang argumento, at ito rin ang ginagawa ng `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, prefix ng path `/r`.
- Laging kasunod ng mga katangiang hango sa AI ang karagdagang query parameter, kaya napananatili ang kanonikong pagkakasunod ng AI (§4.12).

Di-nababago ang `BuilderDigitalLinkConfig`; malaya mong magagamit muli ang iisang instance.

---

## Pagpapatunay at mga Error

### Mga method ng pagbuo na naghahagis

Naghahagis ang `buildElementString()`, `buildDigitalLinkUri()`, at `buildDigitalLinkUri(BuilderDigitalLinkConfig)` ng **`GaiaBuilderException`** (isang `RuntimeException` na hindi sinusuri) kapag hindi makabuo ng wastong output ang mga AI:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Para sa mga pagkabigo sa **nilalaman** (maling check digit, hindi tugmang format, nawawala/ipinagbabawal na AI), may dalang mga `GaiaError` ng parser ang `getErrors()` — ang mismong mga object na [nakadokumento sa gabay sa parser](GaiaParser-Tagalog.md#gaiaerror).
- Para sa mga pagkabigo sa **balangkas ng Digital Link** (walang pangunahing susi, higit sa isang pangunahing susi, ipinagbabawal na AI, di-wastong serye ng key qualifier), may dalang isang `GaiaError` ang `getErrors()` (code na `GE-L008`, `GE-L012`, `GE-L013`, o `GE-L014`) na naisalokal sa wika ng builder.

### Mga method na tryBuild\* na hindi naghahagis

Kapag mula sa gumagamit ang input at inaasahang kalalabasan ang pagkabigo, gamitin ang mga bariyanteng `tryBuild*` sa halip na kontrolin ang daloy sa pamamagitan ng exception. Nagbabalik sila ng [`BuildResult`](#buildresult) sa halip na maghagis:

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

| Naghahagis | Hindi naghahagis |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Iisa ang ubod ng pagpapatunay ng bawat method na `tryBuild*` at ng kakambal nitong naghahagis; ang hangganan lamang ng pagkabigo ang nagkakaiba.

### Wika ng mensahe ng error

Hango sa naisalokal na katalogo ng error ang mga error sa pagpapatunay ng nilalaman. Tawagin ang `language(...)` upang piliin ang wika ng mga mensaheng `GaiaError` na dala ng `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; Ingles ang default:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Ito rin ang mismong setting na `GaiaConstants.Language` na tinatanggap ng `GaiaParser` sa pamamagitan ng `ParseConfig`, kaya magkatulad ang pagsasalokal ng builder at ng parser.

Parehong naisasalokal sa pamamagitan ng pinagsasaluhang katalogo ng error ang mga mensaheng `GaiaError` ng **nilalaman** at ang mga pagkabigo sa **balangkas ng Digital Link** (walang pangunahing susi, higit sa isang pangunahing susi, ipinagbabawal na AI, di-wastong serye ng key qualifier) — ang huli ay gumagamit ng mga code na `GE-L008`, `GE-L012`, `GE-L013`, at `GE-L014`.

### BuildResult

Ang `BuildResult` (sa pakete na `tools.pantheum.gaia.result`) ay isang di-nababagong uri ng halaga na naglalarawan sa kalalabasan ng isang tawag na `tryBuild*`:

| Method | Kapag nagtagumpay | Kapag nabigo |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | ang inilabas na string | `null` |
| `getMessage()` | `null` | paglalarawan ng pagkabigo |
| `getErrors()` | walang lamang listahan | ang mga error sa pagpapatunay (katulad ng `GaiaBuilderException.getErrors()`) |

---

## Mga Check Digit

Pinapatunayan ng builder ang mga check digit ngunit **hindi** nito kinakalkula ang mga ito — dapat kasama na sa mga halaga ang check digit nila. Upang kalkulahin ang isa, gamitin ang `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

Ipinapatupad ng `calculateCheckDigit(String)` ang karaniwang algoritmong modulo-10 ng GS1 sa mga ibinigay na digit at ibinabalik ang check digit na `0–9`, o `-1` kung ang input ay null, walang laman, o hindi numeriko.

---

## Kaligtasan sa Thread

**Hindi** ligtas sa thread ang `GaiaBuilder` at inilaan ito sa minsanang paggamit: tawagin ang `create()`, magdagdag ng mga AI, bumuo nang minsan. Gumawa ng bagong builder sa bawat output; huwag pagsaluhan ang iisa sa maraming thread.

Di-nababago ang `BuilderDigitalLinkConfig` (at ang mga output nitong `BuildResult`) at malayang mapagsasaluhan — bumuo ng isang config sa pagsisimula at gamitin itong muli sa maraming builder.

---

## Sanggunian sa API

### `GaiaBuilder`

| Method | Paglalarawan |
|--------|-------------|
| `static GaiaBuilder create()` | Nagsisimula ng bago at walang lamang builder. |
| `GaiaBuilder ai(String ai, String value)` | Nagdaragdag ng isang AI at ng buong halaga nito. Naghahagis ng `IllegalArgumentException` kung `null` ang alinman sa dalawa, o kung hindi kilalang GS1 Application Identifier ang `ai`. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Itinatakda ang wika ng mga mensahe ng error sa pagpapatunay ng nilalaman (default ay Ingles). Binabalewala ang `null`. |
| `String buildElementString()` | Naglalabas ng isang element string ng GS1. Naghahagis ng `GaiaBuilderException` kapag nabigo. |
| `String buildDigitalLinkUri()` | Naglalabas ng isang kanonikong Digital Link URI. Naghahagis ng `GaiaBuilderException` kapag nabigo. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Naglalabas ng isang Digital Link URI ayon sa `config`. Naghahagis ng `GaiaBuilderException` kapag nabigo. |
| `BuildResult tryBuildElementString()` | Pagbuo ng element string na hindi naghahagis. |
| `BuildResult tryBuildDigitalLinkUri()` | Kanonikong pagbuo ng Digital Link na hindi naghahagis. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Pagbuo ng Digital Link ayon sa `config` na hindi naghahagis. |

### `BuilderDigitalLinkConfig`

| Miyembro | Paglalarawan |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Ang default na `https://id.gs1.org`. |
| `static Builder builder()` | Isang bagong builder ng config. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Ang nalutas na scheme, awtoridad, at prefix ng path. |
| `getExtraQueryParams()` | Mga karagdagang query parameter, ayon sa pagkakapasok. |
| `getFragment()` | Fragment, o `null`. |

### `GaiaBuilderException`

| Miyembro | Paglalarawan |
|--------|-------------|
| `getErrors()` | Ang mga `GaiaError` na nagdulot ng pagkabigo — ang mga error ng parser para sa pagkabigo sa nilalaman, o isang error sa balangkas ng Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Hindi kailanman `null`. |

### `BuildResult`

| Miyembro | Paglalarawan |
|--------|-------------|
| `isSuccess()` | Kung nagtagumpay ba ang pagbuo. |
| `getValue()` | Ang inilabas na output kapag nagtagumpay; `null` kapag nabigo. |
| `getMessage()` | Paglalarawan ng pagkabigo kapag nabigo; `null` kapag nagtagumpay. |
| `getErrors()` | Mga error sa pagpapatunay kapag nabigo; walang laman kapag nagtagumpay. Hindi kailanman `null`. |
| `getTiming()` | Ang `ProcessingTiming` ng pagbuo (oras ng simula, tagal ng pagproseso), o `null`. |

---

Tingnan din: **[GaiaParser — Gabay para sa Developer](GaiaParser-Tagalog.md)** para sa panig ng pag-parse, sa modelo ng elemento ng AI, sa sanggunian ng error, at sa mga apendiks ng konstant ng AI at ng interpretasyon.
