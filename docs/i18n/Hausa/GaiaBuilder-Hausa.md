# GaiaBuilder — Jagora ga Mai Haɓakawa

## Jerin Abubuwan Ciki

1. [Bayani Gaba Ɗaya](#bayani-gaba-ɗaya)
2. [Game da GS1 da General Specifications](#game-da-gs1-da-general-specifications)
3. [Gabatarwa Mai Sauri](#gabatarwa-mai-sauri)
4. [Yadda Yake Aiki](#yadda-yake-aiki)
5. [Gina Element String](#gina-element-string)
   - [AI na halaye suna buƙatar maɓallin ganewarsu](#ai-na-halaye-suna-buƙatar-maɓallin-ganewarsu)
6. [Gina Digital Link URI](#gina-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Tabbatarwa da Kurakurai](#tabbatarwa-da-kurakurai)
   - [Hanyoyin ginawa masu jefa exception](#hanyoyin-ginawa-masu-jefa-exception)
   - [Hanyoyin tryBuild\* waɗanda ba sa jefa exception](#hanyoyin-trybuild-waɗanda-ba-sa-jefa-exception)
   - [Yaren saƙon kuskure](#yaren-saƙon-kuskure)
   - [BuildResult](#buildresult)
9. [Lambobin Bincike](#lambobin-bincike)
10. [Aminci ga Thread](#aminci-ga-thread)
11. [Maganar API](#maganar-api)

---

## Bayani Gaba Ɗaya

`GaiaBuilder` shi ne akasin [`GaiaParser`](GaiaParser-Hausa.md): yana mayar da rukunin nau'ikan Application Identifier (AI) da ƙima zuwa **element string** na GS1 mai kyakkyawan tsari ko **GS1 Digital Link URI**. Kai kake bayar da AI da cikakkun ƙimomin bayanansu; builder yana haɗa su, yana tabbatar da sakamakon ta injin guda da `GaiaParser` ke amfani da shi, sannan yana fitar da abin da aka samar.

Tunda builder yana tabbatarwa ta hanyar *tantance fitarwarsa da kansa*, duk abin da yake mayarwa an tabbatar zai sake tantancewa lafiya ta `GaiaParser` — waɗannan biyun ba za su taɓa saɓa wa juna ba a kan abin da yake da kyakkyawan tsari.

**Aji ɗin ƙofar shiga:** `tools.pantheum.gaia.GaiaBuilder`

---

## Game da GS1 da General Specifications

**GS1** ƙungiya ce ta duniya mai zaman kanta wadda ba ta neman riba, wadda take ƙirƙira da kula da ƙa'idojin buɗaɗɗu don ganewar sarkar samar da kayayyaki da musayar bayanai. Ana amfani da ƙa'idojinta a kasuwanci, kiwon lafiya, sufuri, hidimar abinci, da sauran masana'antu da yawa, wanda ya ƙunshi komai daga barcode ɗin kaya a kan marufin mabukaci har zuwa bibiyar allurar magani ta lambar serial.

Tushen hukuma na duk abin da wannan builder ke aiwatarwa shi ne **GS1 General Specifications** — takarda guda ɗaya wadda take bayyana:

- Dukan lambobin Application Identifier (AI), sunayen bayanansu, tsare-tsarensu, da ƙa'idojin tabbatarwa
- Ƙa'idojin nahawu don gina da yin encoding ga element string ɗin AI
- Buƙatun symbology na barcode da rabon AIM Code ID
- Algorithm ɗin lambar bincike da harafin bincike
- Warware shekara mai lamba biyu (ƙa'idar taga mai zamewa)
- Ƙayyadaddun bayanai na Data Matrix, QR Code, GS1-128, GS1 DataBar, da sauran masu ɗaukar bayanai

Ana sabunta GS1 General Specifications kowace shekara. Ana samun bugu na yanzu da kayan taimako a:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA yana aiwatar da **Release 26.0 (An Amince, Janairu 2026)** na GS1 General Specifications.

Ƙa'ida abokiyar aiki mai suna **GS1 Digital Link: URI Syntax** ce ke tafiyar da GS1 Digital Link URI, kuma ita ce take bayyana maɓallan ganewa na farko, tsarin jerin key qualifier, da encoding ɗin data attribute waɗanda builder ke amfani da su lokacin fitar da Digital Link URI:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA yana aiwatar da **Release 1.7.0 (An Amince, Agusta 2026)** na ƙa'idar GS1 Digital Link: URI Syntax.

Duk maganganun sashe a cikin wannan takarda suna nufin GS1 General Specifications (misali, "Table 7-5", "section 7.12"), sai dai lambobin sashe na Digital Link (misali, "§4.9", "§4.12"), waɗanda suke nufin ƙa'idar GS1 Digital Link: URI Syntax.

---

## Gabatarwa Mai Sauri

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

Gara a yi amfani da constant ɗin `GS1Constants_AICodes` maimakon ɗanyen string ɗin AI (duba [Ƙarin Bayani A na jagorar parser](GaiaParser-Hausa.md#ƙarin-bayani-a--constant-ɗin-string-na-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Yadda Yake Aiki

Kowace gina tana bin hanya guda:

1. **Haɗawa** — ana haɗa nau'ikan AI da ƙima cikin element string ɗin da ake nema. Ana saka separator ɗin ƙungiya na FNC1 (`0x1D`) bayan kowane AI da *yake buƙatar separator* kuma ba shi ne element na ƙarshe ba. AI masu tsawon da aka riga aka ƙayyade (GTIN, kwanan wata, awo masu tsayayyen tsawo) ba sa ɗaukar separator; sauran duka suna ɗauka. (AI da ba a sani ba ba sa isa wannan matakin — `ai(...)` yana ƙin su nan take; duba [Gina Element String](#gina-element-string).)
2. **Tabbatarwa** — ana tantance abin da ake nema a yanayin `CONTENT` ta hanyar `GaiaParser`. Ana duba kowace ƙima bisa tsarin AI ɗinta da lambar binciken ta, kuma ana aiwatar da ƙa'idojin tsari (haɗin AI da ake buƙata/da aka haramta). Idan tantancewar ba ta da inginci, ginawar ta kasa.
3. **Fitarwa** —
   - Ga element string, ana mayar da `toElementString()` na object ɗin da aka tabbatar.
   - Ga Digital Link, ana sanya wa kowane element aikinsa na DL (maɓallin farko, key qualifier, ko data attribute), ana tabbatar da jerin key qualifier, ana fitar da URI, sannan ana **sake tantance URI ɗin da aka fitar don a tabbatar ya dawo a matsayin Digital Link mai inganci** — dubawar tsaro a kan matakin haɗa string da percent-encoding. Idan bai dawo ba, ana jefa `GaiaBuilderException`.

Wannan yana kama da dabarun sake ginawa a cikin `DLSyntaxParser`, don haka wurin sanya separator da tabbatarwa daidai suke da abin da parser yake tsammani.

---

## Gina Element String

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- Ana tabbatar da **AI** nan take: `ai(...)` yana jefa `IllegalArgumentException` idan ba GS1 Application Identifier da aka sani ba ne. (Builder yana haɗa AI da ƙima kafin tantancewa, don haka dole a kama AI da ba a sani ba ko mai tsawo fiye da kima kamar `"99999"` a nan — in ba haka ba za a sake rarraba shi shiru zuwa wani AI dabam.) Ana tabbatar da **ƙimar** daga baya, a lokacin ginawa.
- Dole ƙimomin su kasance **cikakku**, har da kowace lambar bincike. Builder ba ya lissafa ko ƙara lambobin bincike a madadinka — duba [Lambobin Bincike](#lambobin-bincike).
- Ana fitar da AI bisa jerin da ka ƙara su. Builder yana saka separator ɗin FNC1 inda nahawun GS1 ke buƙata; ba kai ke saka separator ba.
- Ginawa **ba tare da wani AI ko kaɗan ba** tana jefa `GaiaBuilderException("No AIs supplied")` da jerin `getErrors()` mara komai — ita ce kaɗai kasawar da ba ta ɗauke da `GaiaError`.
- AI wanda ƙimarsa ta kasa bin ƙa'idar tsarinsa ko ta lambar binciken sa yana sa ginawar ta kasa.

### AI na halaye suna buƙatar maɓallin ganewarsu

Yawancin AI *halaye* ne waɗanda GS1 General Specifications ke buƙatar su zo tare da maɓallin ganewa, kuma builder yana aiwatar da hakan — yana tabbatarwa ta cikakken matakin nahawu, kuma babu hanyar ficewa. Kundi/lot ko serial shi kaɗai **ba** element string mai inganci ba ne:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Maɓallan ganewa (GTIN `01`, SSCC `00`, GLN `414`, …) da AI na cikin kamfani (`90`–`99`) suna iya tsayawa su kaɗai bisa inganci sosai. Sauran duka suna buƙatar abokin tafiyarsu.

> Ana iya gaya wa `GaiaParser` ya tsallake wannan dubawar da `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` kuwa da gangan bai bayyana wani makamancin haka ba — an yi shi ne don ya fitar da abin da yake bin ƙa'ida. Don haɗa element string da gangan ba cikakke ba, ka haɗa shi da kanka sannan ka tantance shi da dubawar a kashe.

---

## Gina Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Digital Link mai inganci yana buƙatar **maɓallin ganewa na farko** guda ɗaya daidai (misali, GTIN `01`, GLN `414`, SSCC `00`). Builder yana rarraba kowane AI da aka bayar:

| Aiki | Yadda ake fitar da shi | Misali |
|------|-------------|---------|
| Maɓallin ganewa na farko | Sashen hanya bayan domain/prefix | `/01/09506000134352` |
| Key qualifier (CPV `22`, kundi `10`, serial `21`, …) | Sassan hanya masu biyowa, bisa **tsarin asali na §4.9** (ba bisa jerin da ka ƙara su ba) | `/10/LOT-ABC` |
| Data attribute (sauran duka) | Query parameter, **an jera su bisa haruffan maɓallin AI** (§4.12) | `?17=271231` |

Tunda ana sake tsara qualifier a lokacin fitarwa, babu matsala idan ba ka bayar da su bisa tsari ba — `ai("21", …)` gabanin `ai("10", …)` yana ci gaba da fitar da `/10/LOT/21/SER`. *Rukunin* kaɗai ne dole maɓallin farko ya karɓa.

Ana yin percent-encoding ga ƙimomin da ke cikin hanya da na query.

Ginawar tana **kasawa** (tana jefa `GaiaBuilderException`, ko tana mayar da `BuildResult` da ya kasa) idan:

- **babu** maɓallin ganewa na farko a cikin AI;
- akwai maɓallin ganewa na farko **fiye da ɗaya**;
- an **haramta** wani AI a cikin Digital Link (`03`, `8014`);
- **jerin key qualifier** ba shi da inganci ga maɓallin farko da aka zaɓa (misali, qualifier da ba na wannan maɓallin ba, ko qualifier waɗanda ba sa cikin tsarin da aka yarda da shi).

---

## BuilderDigitalLinkConfig

Ka wuce da `BuilderDigitalLinkConfig` don ka sarrafa scheme, domain, prefix ɗin hanya, ƙarin query parameter, da fragment:

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

| Hanyar builder | Manufa | Tsoho |
|----------------|---------|---------|
| `scheme(String)` | Scheme ɗin URI; dole ya zama `http` ko `https` | `https` |
| `domain(String)` | Hukuma — host ko `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Sassan hanya gabanin maɓallin farko na farko; ana daidaita slash na gaba/na baya | *(babu)* |
| `baseUrl(String)` | Hanya mai sauƙi da take raba URL zuwa `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Ƙarin query parameter, ana ƙara shi **bayan** data attribute ɗin AI, bisa jerin sakawa; an yi masa percent-encoding | — |
| `fragment(String)` | Fragment ɗin URL (ba tare da `#` na gaba ba); an yi masa percent-encoding | *(babu)* |

`build()` yana tabbatar da saitin nan take: scheme da ba `http(s)` ba ko domain mara komai yana jefa `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias ɗin `defaultConfig()`) shi ne tsohon `https://id.gs1.org` ba tare da wani ƙari ba — daidai abin da `buildDigitalLinkUri()` mara gardama yake amfani da shi, kuma abin da `GS1AIObject.getCanonicalDigitalLink()` yake samarwa.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, prefix ɗin hanya `/r`.
- Ƙarin query parameter kullum suna biyo bayan halayen da suka fito daga AI, don haka ana kiyaye tsarin asali na AI (§4.12).

`BuilderDigitalLinkConfig` ba a canzawa; ka sake amfani da instance ɗaya cikin walwala.

---

## Tabbatarwa da Kurakurai

### Hanyoyin ginawa masu jefa exception

`buildElementString()`, `buildDigitalLinkUri()`, da `buildDigitalLinkUri(BuilderDigitalLinkConfig)` suna jefa **`GaiaBuilderException`** (`RuntimeException` da ba a duba ba) idan AI ba za su iya samar da fitarwa mai kyakkyawan tsari ba:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Ga kasawar **abin ciki** (lambar bincike mara kyau, tsari da bai dace ba, AI da ya ɓace/da aka haramta), `getErrors()` yana ɗauke da `GaiaError` na parser — ainihin object ɗin da [aka rubuta a jagorar parser](GaiaParser-Hausa.md#gaiaerror).
- Ga kasawar **tsarin Digital Link** (babu maɓallin farko, maɓallin farko fiye da ɗaya, AI da aka haramta, jerin key qualifier mara inganci), `getErrors()` yana ɗauke da `GaiaError` guda ɗaya (lamba `GE-L008`, `GE-L012`, `GE-L013`, ko `GE-L014`) da aka mayar wa yaren builder.

### Hanyoyin tryBuild\* waɗanda ba sa jefa exception

Idan shigarwa daga mai amfani take kuma kasawa sakamako ne da ake tsammani kuma ana iya farfaɗowa daga gare shi, ka yi amfani da nau'ikan `tryBuild*` maimakon sarrafa gudanawa ta hanyar exception. Suna mayar da [`BuildResult`](#buildresult) maimakon jefawa:

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

| Mai jefawa | Mara jefawa |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Kowace hanyar `tryBuild*` tana raba ainihin tushen tabbatarwa da tagwayenta mai jefawa; iyakar kasawa kaɗai ce ta bambanta.

### Yaren saƙon kuskure

Ana ɗauko kurakuran tabbatar da abin ciki daga katalogin kuskure da aka mayar wa yare. Ka kira `language(...)` don ka zaɓi yaren saƙonnin `GaiaError` da `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` ke ɗauka; tsohonsa Turanci ne:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Wannan shi ne ainihin saitin `GaiaConstants.Language` da `GaiaParser` yake karɓa ta hanyar `ParseConfig`, don haka builder da parser suna mayar da yare daidai wa daida.

Duka saƙonnin `GaiaError` na **abin ciki** da kasawar **tsarin Digital Link** (babu maɓallin farko, maɓallin farko fiye da ɗaya, AI da aka haramta, jerin key qualifier mara inganci) ana mayar da su wa yare ta katalogin kuskure da ake rabawa — na ƙarshen suna amfani da lambobin `GE-L008`, `GE-L012`, `GE-L013`, da `GE-L014`.

### BuildResult

`BuildResult` (a cikin package ɗin `tools.pantheum.gaia.result`) nau'in ƙima ne da ba a canzawa wanda yake bayyana sakamakon kiran `tryBuild*`:

| Hanya | Idan ya yi nasara | Idan ya kasa |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | string ɗin da aka fitar | `null` |
| `getMessage()` | `null` | bayanin kasawa |
| `getErrors()` | jeri mara komai | kurakuran tabbatarwa (daidai da `GaiaBuilderException.getErrors()`) |

---

## Lambobin Bincike

Builder yana tabbatar da lambobin bincike amma **ba ya** lissafa su — dole ƙimomin su riga sun haɗa da lambar binciken su. Don ka lissafa ɗaya, ka yi amfani da `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` yana amfani da algorithm ɗin modulo-10 na GS1 da aka saba a kan lambobin da aka bayar kuma yana mayar da lambar bincike `0–9`, ko `-1` idan shigarwar null ce, mara komai, ko ba ta lambobi ba.

---

## Aminci ga Thread

`GaiaBuilder` **ba shi da** aminci ga thread kuma an yi shi don amfani sau ɗaya: ka kira `create()`, ka ƙara AI, ka gina sau ɗaya. Ka ƙirƙiri sabon builder ga kowace fitarwa; kada ka raba ɗaya tsakanin thread.

`BuilderDigitalLinkConfig` (da fitarwarsa ta `BuildResult`) ba a canzawa kuma ana iya raba su cikin walwala — ka gina saiti sau ɗaya a farkon aiki sannan ka sake amfani da shi a builder da yawa.

---

## Maganar API

### `GaiaBuilder`

| Hanya | Bayani |
|--------|-------------|
| `static GaiaBuilder create()` | Yana fara sabon builder mara komai. |
| `GaiaBuilder ai(String ai, String value)` | Yana ƙara AI da cikakkiyar ƙimarsa. Yana jefa `IllegalArgumentException` idan ɗayansu yana `null`, ko idan `ai` ba GS1 Application Identifier da aka sani ba ne. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Yana saita yaren saƙonnin kuskure na tabbatar da abin ciki (tsoho Turanci ne). Ana yin watsi da `null`. |
| `String buildElementString()` | Yana fitar da element string ɗin GS1. Yana jefa `GaiaBuilderException` idan ya kasa. |
| `String buildDigitalLinkUri()` | Yana fitar da Digital Link URI na asali. Yana jefa `GaiaBuilderException` idan ya kasa. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Yana fitar da Digital Link URI bisa `config`. Yana jefa `GaiaBuilderException` idan ya kasa. |
| `BuildResult tryBuildElementString()` | Ginawar element string ba tare da jefa exception ba. |
| `BuildResult tryBuildDigitalLinkUri()` | Ginawar Digital Link na asali ba tare da jefa exception ba. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Ginawar Digital Link bisa `config` ba tare da jefa exception ba. |

### `BuilderDigitalLinkConfig`

| Memba | Bayani |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Tsohon `https://id.gs1.org`. |
| `static Builder builder()` | Sabon builder ɗin saiti. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Scheme, hukuma, da prefix ɗin hanya da aka warware. |
| `getExtraQueryParams()` | Ƙarin query parameter, bisa jerin sakawa. |
| `getFragment()` | Fragment, ko `null`. |

### `GaiaBuilderException`

| Memba | Bayani |
|--------|-------------|
| `getErrors()` | `GaiaError` da suka haddasa kasawar — kurakuran parser ga kasawar abin ciki, ko kuskuren tsarin Digital Link guda ɗaya (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Ba ya taɓa zama `null`. |

### `BuildResult`

| Memba | Bayani |
|--------|-------------|
| `isSuccess()` | Ko ginawar ta yi nasara. |
| `getValue()` | Fitarwar da aka samar idan ya yi nasara; `null` idan ya kasa. |
| `getMessage()` | Bayanin kasawa idan ya kasa; `null` idan ya yi nasara. |
| `getErrors()` | Kurakuran tabbatarwa idan ya kasa; mara komai idan ya yi nasara. Ba ya taɓa zama `null`. |
| `getTiming()` | `ProcessingTiming` na ginawar (lokacin farawa, tsawon sarrafawa), ko `null`. |

---

Duba kuma: **[GaiaParser — Jagora ga Mai Haɓakawa](GaiaParser-Hausa.md)** don ɓangaren tantancewa, samfurin element ɗin AI, maganar kuskure, da ƙarin bayanan constant na AI da na fassara.
