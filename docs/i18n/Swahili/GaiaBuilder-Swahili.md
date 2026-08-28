# GaiaBuilder — Mwongozo wa Msanidi

## Yaliyomo

1. [Muhtasari](#muhtasari)
2. [Kuhusu GS1 na General Specifications](#kuhusu-gs1-na-general-specifications)
3. [Mwanzo wa Haraka](#mwanzo-wa-haraka)
4. [Jinsi Inavyofanya Kazi](#jinsi-inavyofanya-kazi)
5. [Kujenga Mifuatano ya Vipengele](#kujenga-mifuatano-ya-vipengele)
   - [AI za sifa zinahitaji ufunguo wao wa utambulisho](#ai-za-sifa-zinahitaji-ufunguo-wao-wa-utambulisho)
6. [Kujenga Digital Link URI](#kujenga-digital-link-uri)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Uthibitishaji na Hitilafu](#uthibitishaji-na-hitilafu)
   - [Mbinu za ujenzi zinazotupa exception](#mbinu-za-ujenzi-zinazotupa-exception)
   - [Mbinu za tryBuild\* zisizotupa exception](#mbinu-za-trybuild-zisizotupa-exception)
   - [Lugha ya ujumbe wa hitilafu](#lugha-ya-ujumbe-wa-hitilafu)
   - [BuildResult](#buildresult)
9. [Tarakimu za Ukaguzi](#tarakimu-za-ukaguzi)
10. [Usalama wa Thread](#usalama-wa-thread)
11. [Marejeleo ya API](#marejeleo-ya-api)

---

## Muhtasari

`GaiaBuilder` ni kinyume cha [`GaiaParser`](GaiaParser-Swahili.md): hugeuza seti ya jozi za Application Identifier (AI) na thamani kuwa **mfuatano wa kipengele** wa GS1 ulioundwa vizuri au **GS1 Digital Link URI**. Wewe hutoa AI na thamani zao kamili za data; builder huziunganisha, huthibitisha matokeo kupitia injini ileile inayotumiwa na `GaiaParser`, kisha hutoa matokeo.

Kwa kuwa builder huthibitisha kwa *kuchanganua matokeo yake mwenyewe yanayopendekezwa*, chochote anachorudisha kimehakikishwa kitachanganuliwa tena bila hitilafu na `GaiaParser` — hivi viwili haviwezi kamwe kutofautiana kuhusu kilichoundwa vizuri.

**Klasi ya lango:** `tools.pantheum.gaia.GaiaBuilder`

---

## Kuhusu GS1 na General Specifications

**GS1** ni shirika la kimataifa lisilo la faida linaloandaa na kudumisha viwango huria vya utambulisho wa mnyororo wa ugavi na ubadilishanaji wa data. Viwango vyake hutumika katika rejareja, afya, lojistiki, huduma za chakula, na sekta nyingine nyingi, vikijumuisha kila kitu kuanzia misimbo pau ya bidhaa kwenye vifungashio vya walaji hadi ufuatiliaji wa mfululizo wa dozi za dawa.

Chanzo rasmi cha kila kitu ambacho builder hii inatekeleza ni **GS1 General Specifications** — hati moja inayofafanua:

- Misimbo yote ya Application Identifier (AI), majina ya data zao, maumbizo, na kanuni za uthibitishaji
- Kanuni za sintaksia za kujenga na kusimba mifuatano ya vipengele vya AI
- Mahitaji ya simbolojia ya msimbo pau na ugawaji wa AIM Code ID
- Algoriti za tarakimu ya ukaguzi na herufi ya ukaguzi
- Utatuzi wa mwaka wa tarakimu mbili (kanuni ya dirisha linalotelezea)
- Vipimo vya Data Matrix, QR Code, GS1-128, GS1 DataBar, na vibeba data vingine

GS1 General Specifications husasishwa kila mwaka. Toleo la sasa na rasilimali za usaidizi zinapatikana katika:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA inatekeleza **Release 26.0 (Iliyoidhinishwa, Januari 2026)** ya GS1 General Specifications.

GS1 Digital Link URI zinaongozwa na kiwango shirika, **GS1 Digital Link: URI Syntax**, kinachofafanua funguo kuu za utambulisho, mpangilio wa key qualifier, na usimbaji wa data attribute ambavyo builder huvitumia inapotoa Digital Link URI:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA inatekeleza **Release 1.7.0 (Iliyoidhinishwa, Agosti 2026)** ya kiwango cha GS1 Digital Link: URI Syntax.

Marejeleo yote ya sehemu katika hati hii yanarejelea GS1 General Specifications (mfano, "Table 7-5", "section 7.12"), isipokuwa nambari za sehemu za Digital Link (mfano, "§4.9", "§4.12"), zinazorejelea kiwango cha GS1 Digital Link: URI Syntax.

---

## Mwanzo wa Haraka

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

Ni bora kutumia konstanti za `GS1Constants_AICodes` kuliko mifuatano ghafi ya AI (angalia [Kiambatisho A cha mwongozo wa parser](GaiaParser-Swahili.md#kiambatisho-a--konstanti-za-mfuatano-wa-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Jinsi Inavyofanya Kazi

Kila ujenzi hufuata njia ileile:

1. **Kuunganisha** — jozi za AI na thamani huunganishwa kuwa mfuatano wa kipengele unaopendekezwa. Kitenganishi cha kundi cha FNC1 (`0x1D`) huingizwa baada ya kila AI *inayohitaji kitenganishi* na isiyo kipengele cha mwisho. AI zenye urefu uliobainishwa mapema (GTIN, tarehe, vipimo vyenye urefu usiobadilika) hazichukui kitenganishi; nyingine zote huchukua. (AI zisizotambuliwa hazifiki hatua hii — `ai(...)` huzikataa mara moja; angalia [Kujenga Mifuatano ya Vipengele](#kujenga-mifuatano-ya-vipengele).)
2. **Uthibitishaji** — inayopendekezwa huchanganuliwa katika hali ya `CONTENT` kupitia `GaiaParser`. Kila thamani hukaguliwa dhidi ya umbizo la AI yake na tarakimu ya ukaguzi, na kanuni za muundo (jozi za AI zinazohitajika/zilizozuiliwa) hutekelezwa. Ikiwa uchanganuzi si halali, ujenzi hushindwa.
3. **Kutoa** —
   - Kwa mfuatano wa kipengele, `toElementString()` ya objekti iliyothibitishwa hurudishwa.
   - Kwa Digital Link, kila kipengele hukabidhiwa jukumu lake la DL (ufunguo mkuu, key qualifier, au data attribute), mfululizo wa key qualifier huthibitishwa, URI hutolewa, kisha URI iliyotolewa **huchanganuliwa tena ili kuthibitisha kuwa inarudi kama Digital Link halali** — ukaguzi wa kinga kwa hatua ya kuunganisha mfuatano na percent-encoding. Ikiwa hairudi, `GaiaBuilderException` hutupwa.

Hii huakisi mantiki ya ujenzi upya katika `DLSyntaxParser`, hivyo uwekaji wa kitenganishi na uthibitishaji ni sawa kabisa na kile parser inachotarajia.

---

## Kujenga Mifuatano ya Vipengele

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** huthibitishwa mara moja: `ai(...)` hutupa `IllegalArgumentException` iwapo si GS1 Application Identifier inayotambuliwa. (Builder huunganisha AI na thamani kabla ya kuchanganua, hivyo AI isiyotambuliwa au ndefu mno kama `"99999"` lazima ikamatwe hapa — vinginevyo ingegawanywa upya kimya kimya kuwa AI tofauti.) **Thamani** huthibitishwa baadaye, wakati wa ujenzi.
- Thamani lazima ziwe **kamili**, ikijumuisha tarakimu yoyote ya ukaguzi. Builder haihesabu wala kuongeza tarakimu za ukaguzi kwa niaba yako — angalia [Tarakimu za Ukaguzi](#tarakimu-za-ukaguzi).
- AI hutolewa kwa mpangilio uliozizidisha. Builder huingiza vitenganishi vya FNC1 pale sintaksia ya GS1 inapohitaji; wewe huongezi vitenganishi mwenyewe.
- Kujenga **bila AI yoyote kabisa** hutupa `GaiaBuilderException("No AIs supplied")` yenye orodha tupu ya `getErrors()` — hiki ndicho kushindwa pekee kusikobeba `GaiaError`.
- AI ambayo thamani yake haipiti kanuni ya umbizo au ya tarakimu ya ukaguzi husababisha ujenzi ushindwe.

### AI za sifa zinahitaji ufunguo wao wa utambulisho

AI nyingi ni *sifa* ambazo GS1 General Specifications huhitaji ziambatane na ufunguo wa utambulisho, na builder hutekeleza hilo — huthibitisha kupitia hatua kamili ya sintaksia, bila njia ya kujitoa. Kundi/lot au mfululizo peke yake **si** mfuatano halali wa kipengele:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Funguo za utambulisho (GTIN `01`, SSCC `00`, GLN `414`, …) na AI za ndani za kampuni (`90`–`99`) zinaweza kusimama peke yake kihalali kabisa. Nyingine zote zinahitaji mwenzao.

> `GaiaParser` inaweza kuambiwa iruke ukaguzi huu kwa `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` kwa makusudi haifunui kitu kinacholingana — imekusudiwa kutoa matokeo yanayotii viwango. Ili kuunganisha mfuatano wa kipengele usio kamili kwa makusudi, uunganishe mwenyewe kisha uuchanganue ukaguzi ukiwa umezimwa.

---

## Kujenga Digital Link URI

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Digital Link halali huhitaji ufunguo mmoja kamili wa **utambulisho mkuu** (mfano, GTIN `01`, GLN `414`, SSCC `00`). Builder huainisha kila AI iliyotolewa:

| Jukumu | Jinsi inavyotolewa | Mfano |
|------|-------------|---------|
| Ufunguo mkuu wa utambulisho | Sehemu ya njia baada ya domain/kiambishi awali | `/01/09506000134352` |
| Key qualifier (CPV `22`, kundi `10`, mfululizo `21`, …) | Sehemu za njia zinazofuata, katika **mpangilio wa kikanoni wa §4.9** (si mpangilio uliozizidisha) | `/10/LOT-ABC` |
| Data attribute (nyingine zote) | Vigezo vya query, **vilivyopangwa kwa herufi za ufunguo wa AI** (§4.12) | `?17=271231` |

Kwa kuwa qualifier hupangwa upya wakati wa kutoa, kuzitoa nje ya mpangilio si tatizo — `ai("21", …)` kabla ya `ai("10", …)` bado hutoa `/10/LOT/21/SER`. Ni *seti* pekee inayopaswa kukubaliwa na ufunguo mkuu.

Thamani zilizo katika njia na katika query zote husimbwa kwa asilimia.

Ujenzi **hushindwa** (hutupa `GaiaBuilderException`, au hurudisha `BuildResult` iliyoshindwa) pale:

- **hakuna** ufunguo mkuu wa utambulisho miongoni mwa AI;
- kuna ufunguo mkuu wa utambulisho **zaidi ya mmoja**;
- AI **imezuiliwa** katika Digital Link (`03`, `8014`);
- **mfululizo wa key qualifier** si halali kwa ufunguo mkuu uliochaguliwa (mfano, qualifier isiyo ya ufunguo huo, au qualifier zilizo nje ya mpangilio unaoruhusiwa).

---

## BuilderDigitalLinkConfig

Pitisha `BuilderDigitalLinkConfig` ili kudhibiti scheme, domain, kiambishi awali cha njia, vigezo vya ziada vya query, na fragment:

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

| Mbinu ya builder | Madhumuni | Chaguo-msingi |
|----------------|---------|---------|
| `scheme(String)` | Scheme ya URI; lazima iwe `http` au `https` | `https` |
| `domain(String)` | Mamlaka — host au `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | Sehemu za njia kabla ya ufunguo mkuu wa kwanza; mistari ya mbele/nyuma hurekebishwa | *(hakuna)* |
| `baseUrl(String)` | Njia rahisi inayogawa URL kuwa `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Kigezo cha ziada cha query, huongezwa **baada ya** data attribute za AI, kwa mpangilio wa kuingiza; husimbwa kwa asilimia | — |
| `fragment(String)` | Fragment ya URL (bila `#` ya mbele); husimbwa kwa asilimia | *(hakuna)* |

`build()` huthibitisha usanidi mara moja: scheme isiyo `http(s)` au domain tupu hutupa `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (lakabu `defaultConfig()`) ndicho chaguo-msingi cha `https://id.gs1.org` bila nyongeza yoyote — ndicho hasa `buildDigitalLinkUri()` isiyo na hoja hutumia, na ndicho `GS1AIObject.getCanonicalDigitalLink()` huzalisha.
- `baseUrl("http://id.example.org:8080/r")` → scheme `http`, domain `id.example.org:8080`, kiambishi awali cha njia `/r`.
- Vigezo vya ziada vya query daima hufuata sifa zilizotokana na AI, hivyo mpangilio wa kikanoni wa AI (§4.12) huhifadhiwa.

`BuilderDigitalLinkConfig` haibadiliki; tumia instance moja tena kwa uhuru.

---

## Uthibitishaji na Hitilafu

### Mbinu za ujenzi zinazotupa exception

`buildElementString()`, `buildDigitalLinkUri()`, na `buildDigitalLinkUri(BuilderDigitalLinkConfig)` hutupa **`GaiaBuilderException`** (`RuntimeException` isiyokaguliwa) pale AI haziwezi kuunda matokeo yaliyoundwa vizuri:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Kwa kushindwa kwa **maudhui** (tarakimu ya ukaguzi mbovu, umbizo lisilolingana, AI isiyopo/iliyozuiliwa), `getErrors()` hubeba `GaiaError` za parser — objekti zilezile [zilizoandikwa katika mwongozo wa parser](GaiaParser-Swahili.md#gaiaerror).
- Kwa kushindwa kwa **muundo wa Digital Link** (hakuna ufunguo mkuu, ufunguo mkuu zaidi ya mmoja, AI iliyozuiliwa, mfululizo wa key qualifier usio halali), `getErrors()` hubeba `GaiaError` moja (msimbo `GE-L008`, `GE-L012`, `GE-L013`, au `GE-L014`) iliyotafsiriwa kwa lugha ya builder.

### Mbinu za tryBuild\* zisizotupa exception

Pale ingizo linatoka kwa mtumiaji na kushindwa ni matokeo yanayotarajiwa na yanayoweza kurekebishwa, tumia matoleo ya `tryBuild*` badala ya kudhibiti mtiririko kwa exception. Hurudisha [`BuildResult`](#buildresult) badala ya kutupa:

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

| Inayotupa | Isiyotupa |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Kila mbinu ya `tryBuild*` hushiriki kiini kilekile cha uthibitishaji na pacha wake anayetupa; ni mpaka wa kushindwa pekee unaotofautiana.

### Lugha ya ujumbe wa hitilafu

Hitilafu za uthibitishaji wa maudhui hutolewa kutoka katalogi ya hitilafu iliyotafsiriwa. Ita `language(...)` kuchagua lugha ya ujumbe wa `GaiaError` unaobebwa na `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; chaguo-msingi ni Kiingereza:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Hii ni mpangilio uleule wa `GaiaConstants.Language` ambao `GaiaParser` hukubali kupitia `ParseConfig`, hivyo builder na parser hutafsiri kwa namna ileile kabisa.

Ujumbe wa `GaiaError` wa **maudhui** na kushindwa kwa **muundo wa Digital Link** (hakuna ufunguo mkuu, ufunguo mkuu zaidi ya mmoja, AI iliyozuiliwa, mfululizo wa key qualifier usio halali) vyote hutafsiriwa kupitia katalogi ya pamoja ya hitilafu — vya mwisho vikitumia misimbo `GE-L008`, `GE-L012`, `GE-L013`, na `GE-L014`.

### BuildResult

`BuildResult` (katika pakiti `tools.pantheum.gaia.result`) ni aina ya thamani isiyobadilika inayoeleza matokeo ya wito wa `tryBuild*`:

| Mbinu | Ikifaulu | Ikishindwa |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | mfuatano uliotolewa | `null` |
| `getMessage()` | `null` | maelezo ya kushindwa |
| `getErrors()` | orodha tupu | hitilafu za uthibitishaji (sawa na `GaiaBuilderException.getErrors()`) |

---

## Tarakimu za Ukaguzi

Builder huthibitisha tarakimu za ukaguzi lakini **haizihesabu** — thamani lazima ziwe tayari zimejumuisha tarakimu yao ya ukaguzi. Ili kuhesabu moja, tumia `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` hutumia algoriti ya kawaida ya GS1 ya modulo-10 kwa tarakimu zilizotolewa na hurudisha tarakimu ya ukaguzi `0–9`, au `-1` iwapo ingizo ni null, tupu, au si la tarakimu.

---

## Usalama wa Thread

`GaiaBuilder` **si** salama kwa thread na imekusudiwa kutumika mara moja: ita `create()`, ongeza AI, jenga mara moja. Unda builder mpya kwa kila matokeo; usishiriki moja kati ya thread nyingi.

`BuilderDigitalLinkConfig` (na matokeo yake ya `BuildResult`) hazibadiliki na zinaweza kushirikiwa kwa uhuru — jenga usanidi mara moja wakati wa kuanza kisha uutumie tena katika builder nyingi.

---

## Marejeleo ya API

### `GaiaBuilder`

| Mbinu | Maelezo |
|--------|-------------|
| `static GaiaBuilder create()` | Huanzisha builder mpya, tupu. |
| `GaiaBuilder ai(String ai, String value)` | Huongeza AI na thamani yake kamili. Hutupa `IllegalArgumentException` iwapo mojawapo ni `null`, au iwapo `ai` si GS1 Application Identifier inayotambuliwa. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Huweka lugha ya ujumbe wa hitilafu za uthibitishaji wa maudhui (chaguo-msingi ni Kiingereza). `null` hupuuzwa. |
| `String buildElementString()` | Hutoa mfuatano wa kipengele wa GS1. Hutupa `GaiaBuilderException` ikishindwa. |
| `String buildDigitalLinkUri()` | Hutoa Digital Link URI ya kikanoni. Hutupa `GaiaBuilderException` ikishindwa. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Hutoa Digital Link URI kwa mujibu wa `config`. Hutupa `GaiaBuilderException` ikishindwa. |
| `BuildResult tryBuildElementString()` | Ujenzi wa mfuatano wa kipengele usiotupa exception. |
| `BuildResult tryBuildDigitalLinkUri()` | Ujenzi wa Digital Link ya kikanoni usiotupa exception. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Ujenzi wa Digital Link kwa mujibu wa `config` usiotupa exception. |

### `BuilderDigitalLinkConfig`

| Kipengele | Maelezo |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Chaguo-msingi cha `https://id.gs1.org`. |
| `static Builder builder()` | Builder mpya ya usanidi. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Scheme, mamlaka, na kiambishi awali cha njia vilivyotatuliwa. |
| `getExtraQueryParams()` | Vigezo vya ziada vya query, kwa mpangilio wa kuingiza. |
| `getFragment()` | Fragment, au `null`. |

### `GaiaBuilderException`

| Kipengele | Maelezo |
|--------|-------------|
| `getErrors()` | `GaiaError` zilizosababisha kushindwa — hitilafu za parser kwa kushindwa kwa maudhui, au hitilafu moja ya muundo wa Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Kamwe si `null`. |

### `BuildResult`

| Kipengele | Maelezo |
|--------|-------------|
| `isSuccess()` | Iwapo ujenzi ulifaulu. |
| `getValue()` | Matokeo yaliyotolewa ikifaulu; `null` ikishindwa. |
| `getMessage()` | Maelezo ya kushindwa ikishindwa; `null` ikifaulu. |
| `getErrors()` | Hitilafu za uthibitishaji ikishindwa; tupu ikifaulu. Kamwe si `null`. |
| `getTiming()` | `ProcessingTiming` ya ujenzi (muda wa kuanza, muda wa usindikaji), au `null`. |

---

Angalia pia: **[GaiaParser — Mwongozo wa Msanidi](GaiaParser-Swahili.md)** kwa upande wa uchanganuzi, muundo wa kipengele cha AI, marejeleo ya hitilafu, na viambatisho vya konstanti za AI na za tafsiri.
