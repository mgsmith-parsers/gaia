# GAIA (GS1 Application Identifiers Analyser) — Mwongozo wa Msanidi

## Yaliyomo

1. [Muhtasari](#muhtasari)
2. [Kuhusu GS1 na General Specifications](#kuhusu-gs1-na-general-specifications)
3. [GS1 Application Identifier](#gs1-application-identifier)
4. [Mwanzo wa Haraka](#mwanzo-wa-haraka)
5. [Mtiririko wa Uchanganuzi](#mtiririko-wa-uchanganuzi)
   - [Hatua ya awali — Input Modifier](#hatua-ya-awali--input-modifier)
   - [Hatua ya 0 — Correlation ID](#hatua-ya-0--correlation-id)
   - [Hatua ya 1 — Kuelekeza Ingizo](#hatua-ya-1--kuelekeza-ingizo)
   - [Hatua ya 2 — Sintaksia](#hatua-ya-2--sintaksia)
   - [Hatua ya 3 — Maudhui](#hatua-ya-3--maudhui)
   - [Hatua ya 4 — Tafsiri](#hatua-ya-4--tafsiri)
6. [Usanidi wa Uchanganuzi (`ParseConfig`)](#usanidi-wa-uchanganuzi-parseconfig)
   - [Machaguo](#machaguo)
   - [Ujumbe na lebo zilizotafsiriwa](#ujumbe-na-lebo-zilizotafsiriwa)
   - [Uumbizaji wa tarehe](#uumbizaji-wa-tarehe)
7. [Input Modifier](#input-modifier)
   - [Modifier za ndani](#modifier-za-ndani)
   - [Kuandika modifier](#kuandika-modifier)
   - [Kusajili modifier](#kusajili-modifier)
   - [Kuangalia modifier ilichofanya](#kuangalia-modifier-ilichofanya)
   - [Kushughulikia kushindwa kwa modifier](#kushughulikia-kushindwa-kwa-modifier)
8. [Hali za Uchanganuzi](#hali-za-uchanganuzi)
   - [Hali ya DATA_CARRIER](#hali-ya-data_carrier)
   - [Hali ya SYNTAX](#hali-ya-syntax)
   - [Hali ya CONTENT](#hali-ya-content)
   - [Hali ya INTERPRETATION (chaguo-msingi)](#hali-ya-interpretation-chaguo-msingi)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Kufanya Kazi na Matokeo](#kufanya-kazi-na-matokeo)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry na DataCarrierType](#datacarrierentry-na-datacarriertype)
12. [Marejeleo ya Hitilafu](#marejeleo-ya-hitilafu)
13. [Usalama wa Thread](#usalama-wa-thread)
14. [Kiambatisho A — Konstanti za Mfuatano wa AI](#kiambatisho-a--konstanti-za-mfuatano-wa-ai)
    - [Utambulisho na uwekaji mfululizo](#utambulisho-na-uwekaji-mfululizo)
    - [Tarehe na nyakati](#tarehe-na-nyakati)
    - [Idadi na kipimo — kipimo kinachobadilika (metriki)](#idadi-na-kipimo--kipimo-kinachobadilika-metriki)
    - [Idadi na kipimo — kipimo kinachobadilika (imperial / US)](#idadi-na-kipimo--kipimo-kinachobadilika-imperial--us)
    - [Bei na viasi vya fedha](#bei-na-viasi-vya-fedha)
    - [Mahali na usafirishaji](#mahali-na-usafirishaji)
    - [Sifa za bidhaa na ufuatiliaji](#sifa-za-bidhaa-na-ufuatiliaji)
    - [Nambari za Kitaifa za Urejeshaji Gharama za Afya (NHRN)](#nambari-za-kitaifa-za-urejeshaji-gharama-za-afya-nhrn)
    - [Afya, GMN, HIDRI, CPID, data za mtu](#afya-gmn-hidri-cpid-data-za-mtu)
    - [Matumizi ya ndani / ya kampuni](#matumizi-ya-ndani--ya-kampuni)
15. [Kiambatisho B — Konstanti za Funguo za Tafsiri](#kiambatisho-b--konstanti-za-funguo-za-tafsiri)
    - [Tarehe na muda](#tarehe-na-muda)
    - [Tarehe ya mavuno](#tarehe-ya-mavuno)
    - [Kiambishi Awali cha Kampuni GS1](#kiambishi-awali-cha-kampuni-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Nchi (ISO 3166)](#nchi-iso-3166)
    - [Sarafu (ISO 4217)](#sarafu-iso-4217)
    - [Halijoto](#halijoto)
    - [Jinsia (ISO 5218)](#jinsia-iso-5218)
    - [Viumbe vya majini (FAO)](#viumbe-vya-majini-fao)
    - [Nambari ya Hisa ya NATO (NSN)](#nambari-ya-hisa-ya-nato-nsn)
    - [Bidhaa za roli](#bidhaa-za-roli)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Vitambulisho vya SIM (EID / ICCID)](#vitambulisho-vya-sim-eid--iccid)
    - [Marejeleo ya uthibitisho](#marejeleo-ya-uthibitisho)
    - [GS1 UIC](#gs1-uic)
    - [Mfuatano wa kuzaliwa kwa mtoto](#mfuatano-wa-kuzaliwa-kwa-mtoto)
    - [Nambari ya Muundo ya Kimataifa (GMN)](#nambari-ya-muundo-ya-kimataifa-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Thamani za desimali na za vipimo](#thamani-za-desimali-na-za-vipimo)
    - [Viwianishi vya kijiografia](#viwianishi-vya-kijiografia)
    - [Njia ya uzalishaji](#njia-ya-uzalishaji)
    - [Aina ya media ya AIDC](#aina-ya-media-ya-aidc)
    - [Kipande cha jumla](#kipande-cha-jumla)
    - [Migawanyo ya vijenzi](#migawanyo-ya-vijenzi)
    - [Mengineyo](#mengineyo)

---

## Muhtasari

`GaiaParser` ndiyo lango la kuchanganua mifuatano ya vipengele vya GS1 Application Identifier (AI). Hupokea matokeo ghafi ya kichanganuzi katika mojawapo ya maumbo yafuatayo, na hurudisha `ParseResult` iliyopangwa yenye AI zote zilizotambuliwa, hitilafu za uthibitishaji, na (ikiombwa) tafsiri zinazosomeka na binadamu:

- Mfuatano wa kipengele cha AI pekee: `0109506000134352`
- Mfuatano wa kipengele wenye kiambishi awali cha AIM Code ID: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Lolote kati ya hayo, likiwa na kiambishi awali cha hiari cha correlation ID ya tarakimu 8: `12345678~0109506000134352`

**Klasi ya lango:** `tools.pantheum.gaia.GaiaParser`

> **Mgeni kwa Gaia?** Anza na **[Mwanzo wa Haraka wa GaiaParser](GaiaParser-QuickStart-Swahili.md)** — utegemezi, uchanganuzi wa kwanza, na mambo machache yanayowakwaza watu, kwa muda wa takriban dakika kumi. Mwongozo huu ndio marejeleo kamili.

> Kwa kazi ya kinyume — *kujenga* mifuatano ya vipengele na Digital Link URI zilizo sahihi kutoka kwa jozi za AI na thamani — angalia **[GaiaBuilder — Mwongozo wa Msanidi](GaiaBuilder-Swahili.md)**.

---


## Kuhusu GS1 na General Specifications

**GS1** ni shirika la kimataifa lisilo la faida linaloandaa na kudumisha viwango huria vya utambulisho wa mnyororo wa ugavi na ubadilishanaji wa data. Viwango vyake hutumika katika rejareja, afya, lojistiki, huduma za chakula, na sekta nyingine nyingi, vikijumuisha kila kitu kuanzia misimbo pau ya bidhaa kwenye vifungashio vya walaji hadi ufuatiliaji wa mfululizo wa dozi za dawa.

Chanzo rasmi cha kila kitu ambacho parser hii inatekeleza ni **GS1 General Specifications** — hati moja inayofafanua:

- Misimbo yote ya Application Identifier (AI), majina ya data zao, maumbizo, na kanuni za uthibitishaji
- Kanuni za sintaksia za kujenga na kusimba mifuatano ya vipengele vya AI
- Mahitaji ya simbolojia ya msimbo pau na ugawaji wa AIM Code ID
- Algoriti za tarakimu ya ukaguzi na herufi ya ukaguzi
- Utatuzi wa mwaka wa tarakimu mbili (kanuni ya dirisha linalotelezea)
- Vipimo vya Data Matrix, QR Code, GS1-128, GS1 DataBar, na vibeba data vingine

GS1 General Specifications husasishwa kila mwaka. Toleo la sasa na rasilimali za usaidizi zinapatikana katika:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA inatekeleza **Release 26.0 (Iliyoidhinishwa, Januari 2026)** ya GS1 General Specifications.

GS1 Digital Link URI zinaongozwa na kiwango shirika, **GS1 Digital Link: URI Syntax**, kinachofafanua funguo kuu za utambulisho, mpangilio wa key qualifier, na usimbaji wa data attribute ambavyo parser huvitumia kwa ingizo la Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA inatekeleza **Release 1.7.0 (Iliyoidhinishwa, Agosti 2026)** ya kiwango cha GS1 Digital Link: URI Syntax.

Marejeleo yote ya sehemu katika hati hii yanarejelea GS1 General Specifications (mfano, "Table 7-5", "section 7.12"), isipokuwa nambari za sehemu za Digital Link (mfano, "§4.9", "§4.12"), zinazorejelea kiwango cha GS1 Digital Link: URI Syntax.

---

## GS1 Application Identifier

**GS1 Application Identifier (AI)** ni kiambishi awali kifupi cha tarakimu — tarakimu mbili hadi nne — kinachoonyesha maana na umbizo la data inayokifuata mara moja. AI zimefafanuliwa katika GS1 General Specifications na zinajumuisha aina nyingi za data za mnyororo wa ugavi: vitambulisho vya bidhaa, tarehe, idadi, nambari za lot, nambari za mfululizo, vipimo, URL, na nyingine.

### Muundo wa kipengele cha AI

Kila kipengele cha AI kina sehemu mbili:

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

Msimbo wa AI daima ni tarakimu. Thamani ya data huufuata mara moja, bila kitenganishi chochote kati ya msimbo na thamani.

### Urefu usiobadilika dhidi ya urefu unaobadilika

AI zimegawanyika katika makundi mawili:

| Aina | Tabia | Mfano |
|---|---|---|
| **Urefu usiobadilika** | Idadi kamili ya herufi, husomwa yote kila wakati | AI `01` (GTIN) — daima tarakimu 14 |
| **Urefu unaobadilika** | Kuanzia 1 hadi idadi kubwa zaidi ya herufi; hukomeshwa na kitenganishi cha GS au mwisho wa ingizo | AI `10` (Kundi/Lot) — herufi 1 hadi 20 za tarakimu na herufi |

Iwapo AI ina urefu usiobadilika au unaobadilika hutegemea tu ufafanuzi wake katika vipimo vya GS1 — parser haibashiri kamwe.

### Mifuatano ya vipengele yenye AI nyingi

AI kadhaa zinaweza kuunganishwa kuwa mfuatano mmoja wa kipengele. AI zenye urefu usiobadilika zinaweza kuunganishwa moja kwa moja kwa sababu parser daima hujua ni herufi ngapi za kusoma. AI zenye urefu unaobadilika lazima zikomeshwe kwa **herufi ya GS** (ASCII `0x1D`, ijulikanayo pia kama FNC1 katika simbolojia za msimbo pau) kila AI nyingine inapozifuata, ili parser ijue thamani moja inapoishia na msimbo wa AI unaofuata unapoanzia.

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

Katika literali za string za Java, andika herufi ya GS kwa Unicode escape `""`.

### AI zinazotumika sana

| AI | Jina la data | Umbizo | Mfano wa thamani |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, eneo moja la fedha) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Tarakimu ya nne** ya AI ya kipimo au bei yenye tarakimu 4 husimba idadi ya nafasi za desimali zinazokusudiwa — `3103` ni uzito halisi kwa kilogramu wenye desimali 3 (`001500` = 1.500 kg), ilhali `3102` ingezisoma tarakimu zilezile kama 15.00 kg. Safu ya `Umbizo` hapo juu inaonyesha umbizo la *data*; `getFormatString()` kamili ya kila AI hujumuisha AI yenyewe (mfano, `N4+N6` kwa `3103`).

### Tafsiri Inayosomeka na Binadamu (HRI)

Umbo la kawaida linalosomeka na binadamu hufunga kila msimbo wa AI ndani ya mabano mara moja kabla ya thamani yake, na nafasi moja kati ya vipengele:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Kitenganishi cha GS hakionyeshwi katika HRI. `GS1AIObject.toHriString()` huzalisha umbizo hili.

### Misimbo ya AI ya tarakimu nne

Baadhi ya AI hutumia tarakimu nne badala ya mbili. Tarakimu mbili za mwanzo huonyesha familia ya AI; tarakimu ya tatu na/au ya nne hubeba maana ya ziada (kama vile nafasi ya nukta ya desimali inayokusudiwa kwa AI za kipimo). Parser hutatua msimbo kamili wa AI kutoka kwa mfuatano wa kipengele kiotomatiki — wanaoita daima hutumia msimbo kamili (mfano, `"3102"`, si `"31"` peke yake).

---

## Mwanzo wa Haraka

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

> **Kitenganishi cha GS:** AI zenye urefu unaobadilika ndani ya mfuatano wenye AI nyingi lazima zitenganishwe kwa herufi ya GS (ASCII `0x1D`). Tumia `""` katika literali za string za Java.

---

## Mtiririko wa Uchanganuzi

### Hatua ya awali — Input Modifier

Ikiwa `ParseConfig` ina **input modifier** zozote, hukimbia kabla ya kila kitu kingine — kabla ya kuondoa correlation, kabla ya kutambua kibeba data, kabla ya kuingia katika mtiririko wa GS1. Kila modifier huandika upya ingizo ghafi kwa ajili ya inayofuata, na kila hatua iliyo hapa chini hufanya kazi kwenye matokeo ya mnyororo.

Hakuna modifier iliyowekwa kama chaguo-msingi, hivyo hatua hii ya awali haifanyi lolote isipokuwa uchague mwenyewe. Angalia [Input Modifier](#input-modifier).

---

### Hatua ya 0 — Correlation ID

Kabla ya usindikaji wowote wa GS1, `GaiaParser` hukagua iwapo ingizo linaanza na **kiambishi awali cha correlation ID** cha hiari: tarakimu 8 kamili za desimali za ASCII zikifuatwa na tilde (`~`), mfano `12345678~`.

Iwapo kiambishi awali kipo, huondolewa na kuhifadhiwa kama `CorrelationInfo` katika `ParseResult` inayorudishwa. Hatua zote zinazofuata hufanya kazi kwenye payload iliyoondolewa kiambishi. Ikiwa hakuna kiambishi awali, ingizo hupita bila kubadilishwa.

Angalia [Correlation ID](#correlation-id) kwa maelezo zaidi.

---

### Hatua ya 1 — Kuelekeza Ingizo

Baada ya kuondoa correlation, `GaiaParser` hukagua iwapo ingizo (lililoondolewa kiambishi) linaanza na **AIM Code ID**: kiambishi awali cha herufi tatu chenye umbo `]` + herufi ya ASCII + tarakimu ya ASCII (mfano, `]C1` kwa GS1-128, `]d2` kwa GS1 DataMatrix, `]e0` kwa GS1 DataBar / GS1 Composite).

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

Ikiwa kibeba data hakiwezi kubeba GS1 AI (mfano, msimbo pau wa posta), uchanganuzi husimama mara moja na hitilafu ya `GE-D002`.

---

### Hatua ya 2 — Sintaksia

Hukimbia bila masharti. Ina hatua ndogo mbili:

**2a. Kugawa vipande (`AISyntaxParser`)**
- Husoma urefu wa msimbo wa AI kutoka herufi mbili za mwanzo kwa kutumia jedwali la viambishi awali vya GS1 (GS1 General Specifications Table 7-5).
- AI zenye urefu usiobadilika husoma idadi kamili ya baiti kutoka kwa ingizo.
- AI zenye urefu unaobadilika husomwa hadi herufi ya GS au mwisho wa ingizo.
- AI zenye vijenzi vingi hukatwa thamani yao kuwa sehemu za kila kijenzi.

**2b. Uthibitishaji wa muundo (`SyntaxValidator`)**
- Hukagua AI zilizorudiwa (`GE-S004`).
- Hukagua utegemezi wa AI unaohitajika, mfano, AI `02` inahitaji AI `37` (`GE-S005`).
- Hukagua jozi za AI zilizozuiliwa (`GE-S006`).

Hitilafu katika hatua hii zina kiwango cha `SYNTAX_ERROR` (kigawa vipande) au `INTEGRITY_ERROR` (muundo). Ikiwa kuna hitilafu **yoyote** — ya kigawa vipande au ya muundo — mtiririko husimama na hatua za maudhui na tafsiri huachwa.

---

### Hatua ya 3 — Maudhui

Hukimbia tu iwapo Hatua ya 2 haikuzalisha hitilafu yoyote (ya kigawa vipande wala ya muundo). Mtiririko wa kila kipengele ni hivi (kila hatua hukimbia tu iwapo iliyotangulia haikuzalisha hitilafu):

| Hatua | Kithibitishaji | Misimbo ya hitilafu |
|---|---|---|
| Ukaguzi wa regex | `RegexValidator` | `GE-C001` |
| Seti ya herufi na umbizo la kijenzi | `ComponentValidator` | `GE-C005` + misimbo ya umbizo kwa kila sharti (`GE-C054`–`GE-C115`) |
| Tarakimu ya ukaguzi / herufi ya ukaguzi | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Uthibitishaji maalum wa maana | `ContentValidatorRegistry` | misimbo ya maudhui kwa kila sharti (`GE-C116`–`GE-C170`) |

Hitilafu katika hatua hii zina kiwango cha `FORMAT_ERROR` au `DATA_ERROR`, isipokuwa moja: ukaguzi
wa kiambishi awali cha kampuni GS1 kwenye AI zenye funguo za GS1 ni ushauri tu na una kiwango cha
`WARNING` (angalia [Marejeleo ya Hitilafu](#marejeleo-ya-hitilafu)), hivyo kiambishi awali cha kampuni
kisichotambuliwa, chenyewe, hakifanyi matokeo yasiwe halali.

---

### Hatua ya 4 — Tafsiri

Hukimbia tu katika hali ya `INTERPRETATION` na tu iwapo hakuna kipengele chenye hitilafu kutoka hatua yoyote iliyotangulia. `InterpretationEngine` huboresha kila kipengele kwa metadata yenye lebo:

- Tarehe zilizoumbizwa upya kama `dd/mm/yyyy`
- Uchambuzi wa tarakimu ya ukaguzi ya GTIN na utafutaji wa kiambishi awali cha kampuni GS1
- Majina ya nchi kwa ISO 3166
- Majina na alama za sarafu kwa ISO 4217
- Viasi vya desimali vilivyofumbuliwa
- Vipande vya HRI (Tafsiri Inayosomeka na Binadamu)

Matokeo hubandikwa kama viingizo vya `GS1AIInterpretation` kwenye kila `GS1AIObjectElement`.

---

## Usanidi wa Uchanganuzi (`ParseConfig`)

`GaiaParser` hufunua malango mawili tu:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` hukimbia kwa **usanidi chaguo-msingi**: hali ya `INTERPRETATION`, tarehe za little-endian (`dd/mm/yyyy`) zenye kitenganishi `/` na mwaka wa tarakimu nne, na ujumbe wa hitilafu kwa **Kiingereza**. Ili kubadilisha lolote kati ya hayo — ikiwa ni pamoja na hali ya uchanganuzi — jenga `ParseConfig` kwa builder yake tiririfu kisha tumia overload yenye hoja mbili.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Enum zote za chaguo zinaishi katika `GaiaConstants`.

### Machaguo

| Mbinu ya builder | Enum (`GaiaConstants`) | Chaguo-msingi | Athari |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Kina cha mtiririko — angalia [Hali za Uchanganuzi](#hali-za-uchanganuzi). |
| `language(...)`      | `Language`      | `ENGLISH`        | Lugha ya ujumbe wa hitilafu, lebo za tafsiri, **na pia** maelezo ya AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Mpangilio wa vijenzi vya tarehe: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Herufi kati ya vijenzi vya tarehe: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) au `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) au `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Huruka ukaguzi wa muundo wa "requires" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Huruka ukaguzi wa muundo wa "excludes" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / jina la klasi | hakuna | Msimbo unaoandika upya ingizo ghafi kabla ya uchanganuzi — [modifier mbili za ndani](#modifier-za-ndani) pamoja na chochote utakachoandika. Angalia [Input Modifier](#input-modifier). |

Machaguo manne ya tarehe huathiri tu mifuatano ya tarehe iliyoumbizwa inayozalishwa na viboreshaji vya tafsiri (katika hali ya `INTERPRETATION`); hayabadilishi uthibitishaji. Thamani za builder zinaweza kuachwa — chaguo lolote lisilowekwa (au lililopewa `null`) hubaki na chaguo-msingi lake.

### Ujumbe na lebo zilizotafsiriwa

`language(...)` huchagua lugha kwa **aina tatu** za maandishi yanayosomeka na binadamu: ujumbe wa hitilafu, lebo za tafsiri (`getLabel()` ya kila `GS1AIInterpretation`), na maelezo ya AI (`getDescription()` ya kila `GS1AIObjectElement`).

**Lugha 35** zimefafanuliwa na `GaiaConstants.Language`, zikijumuisha lugha zinazozungumzwa zaidi duniani: Kiingereza, Kifaransa, Kihispania, Kijerumani, Kiitaliano, Kireno, Kiholanzi, Kipolandi, Kirusi, Kiukreni, Kicheki, Kiswidi, Kichina, Kijapani, Kikorea, Kiarabu, Kiindonesia, Kihindi, Kituruki, Kibengali, Kiurdu, Kivietinamu, Nigerian Pidgin, Kiarabu cha Misri, Kimarathi, Kitelugu, Kitamil, Kikantoni, Kiwu, Kitagalogi, Kiajemi, Kihausa, Kipunjabi, Kijava, na Kiswahili.

Hali ya tafsiri (kama ilivyosafirishwa):
- **Lebo za tafsiri** — zimetafsiriwa kwa lugha zote.
- **Ujumbe wa hitilafu** — umetafsiriwa kwa lugha zote.
- **Maelezo ya AI** — yametafsiriwa kwa lugha zote isipokuwa Kiingereza. Kiingereza si katalogi tofauti: husomwa moja kwa moja kutoka sehemu ya `description` ya kiingizo cha AI katika `gs1-application-identifiers.jsonld`, ambapo kila maelezo ya AI hurejea hatimaye.

Nigerian Pidgin (`NIGERIAN_PIDGIN`), lugha ya krioli yenye msingi wa Kiingereza, kwa makusudi hutumia tena maandishi ya Kiingereza kwa lebo za tafsiri na ujumbe wa hitilafu. Maelezo ya AI ndiyo kipekee ndani ya kipekee hicho: yametafsiriwa kwa maneno halisi ya Pidgin badala ya kutumia tena Kiingereza, kwa sababu katalogi za maelezo ya AI zilitayarishwa kando na katalogi za lebo na ujumbe. Tafsiri za mashine zinapaswa kukaguliwa na wazungumzaji asilia kabla ya kuzitegemea katika uzalishaji.

Ujumbe, lebo, au maelezo yoyote yasiyopatikana katika katalogi ya lugha fulani hurejea Kiingereza. Lugha zinazoandikwa kutoka kulia kwenda kushoto (Kiarabu, Kiurdu, Kiarabu cha Misri, Kiajemi) huhifadhiwa kwa usahihi kama mifuatano; kuzionyesha kwa mtindo wa RTL ni jukumu la safu ya uonyeshaji.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Lebo za tafsiri hutafsiriwa vivyo hivyo (thamani hazibadiliki — lebo pekee):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Maelezo ya AI hutafsiriwa vivyo hivyo (ni `getTitle()` pekee, mfano `"GTIN"`, isiyotafsiriwa):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Uumbizaji wa tarehe

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Input Modifier

**Input modifier** ni msimbo unaoandika upya mfuatano ghafi wa ingizo kabla Gaia haijauchanganua. Modifier zinakusudiwa kwa ingizo linalofika likiwa tayari limeharibika — kichanganuzi kinachobadilisha kitenganishi cha GS kuwa herufi inayoonekana, middleware inayofunga payload ndani ya kiambishi awali cha mchuuzi, mfumo pangishi unaogeuza kila kitu kuwa herufi kubwa. Badala ya kusafisha kila mfuatano katika kila mahali panapoita (na hivyo kukosea kidogo mahali pamoja), sajili urekebishaji mara moja kwenye `ParseConfig` kisha acha parser ndiyo iutumie.

Modifier hukimbia mwanzoni kabisa mwa `GaiaParser.parse(...)` — kabla ya kuondoa correlation ID, kabla ya kutambua AIM Code ID, kabla ya kuingia katika mtiririko wa GS1. Kila kitu kinachofuata huona mfuatano ulioandikwa upya pekee. Pamoja na [modifier mbili za ndani](#modifier-za-ndani), **hakuna kilichowekwa kama chaguo-msingi** — wewe ndiye unayechagua kwa kila `ParseConfig`.

**Kiolesura:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modifier za ndani

Core jar huja na modifier mbili chini ya `tools.pantheum.gaia.modifier.custom`. Zinashughulikia njia mbili zinazotokea zaidi za payload ya GS1 kuharibika — mabano ya HRI yaliyochapishwa yakichukuliwa kama data, na nafasi za ziada — hivyo hakuna haja ya kuandika klasi yako mwenyewe kwa hali ya kawaida:

| Klasi | `getName()` | Inachofanya |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Huondoa mabano ya HRI yaliyozunguka kila AI (`(01)…(10)…`) na kurudisha kitenganishi cha FNC1 ambacho mabano hayo yalikimaanisha. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Huondoa kila nafasi (`0x20`) kutoka kwa mfuatano wa kipengele cha AI. |

Zote mbili ni utekelezaji wa kawaida wa `ModifierInterface` bila hadhi maalum — husajiliwa, hupangwa, huripotiwa, na hushindwa vivyo hivyo kama zako mwenyewe:

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

Zote mbili hazina hali na ni salama kwa thread, hivyo instance moja inaweza kushirikiwa na wote; na kwa usambazaji unaotegemea faili la usanidi, zote mbini zinaweza kutajwa kwa jina kamili la klasi (angalia [Kusajili modifier](#kusajili-modifier)).

#### `ModifierRemoveAIBrackets`

Tafsiri ya GS1 inayosomeka na binadamu huchapisha kila AI ndani ya mabano — `(01)09521234543213(10)ABC123` — ni desturi ya uchapishaji tu. Kichanganuzi au middleware yoyote iliyowekwa kutuma HRI hupitisha mabano hayo kama data, na kigawa vipande hakijui cha kufanya nayo.

Kuondoa mabano ni nusu ya kazi tu. Katika HRI, bano la kufungua `(` la AI *inayofuata* ndiyo alama pekee ya mwisho wa thamani iliyotangulia, hivyo katika umbo la mabano, AI yenye urefu unaobadilika haihitaji FNC1. Ondoa mabano ovyo na mpaka huo hupotea:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Ndiyo maana modifier hii **huweka tena FNC1 katika kila mpaka ambapo AI iliyotangulia ina urefu unaobadilika**, ikirudisha hasa ule mgawanyiko ambao mabano yalikuwa yakiusimba:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Urefu hutafutwa kutoka `AiDefinitionRegistry` ya parser yenyewe, hivyo hushughulikia kila AI yenye urefu unaobadilika badala ya orodha iliyoandikwa moja kwa moja kwenye msimbo. Kuna hali tatu zinazoachwa kwa makusudi: thamani inayoishia tayari kwa FNC1 (chanzo kinachotuma desturi zote mbili hakipati kitenganishi cha pili), msimbo wa mabano usio AI inayojulikana (AI isiyojulikana haisemi urefu wake), na AI ya mwisho katika mfuatano.

Uandikaji huu upya ni **idempotenti** — ukiukimbiza kwenye matokeo yake mwenyewe hakuna kinachobadilika — hivyo ni salama hata kwenye mtiririko mchanganyiko ambapo ni baadhi tu ya ingizo lenye mabano.

> **Kikomo.** `(` na `)` ni herufi halali za data za GS1 zenyewe, na muundo unaotumiwa hapa ni `\((\d{2,4})\)` tu. Ikiwa thamani ina nambari ya tarakimu mbili-hadi-nne ndani ya mabano kwa bahati mbaya, mabano yake nayo yataondolewa. Itumie tu kwa chanzo kinachotumia desturi ya mabano ya HRI, si kwa kile chenye thamani halisi zenye mabano.

#### `ModifierRemoveSpaces`

Baadhi ya vichanganuzi, middleware, na mifumo ya kuchapisha lebo huingiza nafasi za ziada katika mfuatano wa kipengele ambao vinginevyo ungekuwa sahihi — ili kujaza sehemu yenye upana usiobadilika, kutenganisha makundi yanayosomeka kwa urahisi, au kukunja thamani ndefu. Kigawa vipande huchukulia kila nafasi kama data, hivyo huharibu thamani iliyomo, na kwa AI yenye urefu unaobadilika, kila kitu kinachofuata hujisogeza.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Ni ASCII `0x20` pekee inayoondolewa. Nafasi nyingine nyeupe hubaki mahali pake — mfano, tab haiko ndani ya seti ya herufi zinazoweza kusimbwa na GS1, hivyo parser huiripoti kama `GE-S008` badala ya kuimeza kimya kimya.

> **Kikomo.** Nafasi (`0x20`) ni sehemu ya seti ya herufi zisizobadilika za GS1, hivyo nambari ya kundi/lot au nambari ya sehemu ya mteja inaweza kuwa na nafasi kihalali. Modifier haiwezi kutofautisha nafasi ya ziada na ya kweli; itumie tu kwa chanzo unachojua hakitumii nafasi katika thamani zake za AI.

#### Viambishi awali hurukwa, haviandikwi upya

Modifier hukimbia wakati parser bado haijaondoa chochote, hivyo ingizo ghafi linaweza bado kubeba correlation ID, AIM Code ID, na kiashiria cha ECI. Modifier zote mbili za ndani hutumia mantiki ile ile ya parser ya `CorrelationIdParser` na `DataCarrierParser` kupata mwanzo wa mfuatano wa kipengele cha AI, huanza kuandika upya kutoka hapo, kisha huunganisha tena matokeo na kiambishi awali **kilichoachwa kama kilivyo**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Vibeba data vya EAN/UPC ambavyo thamani yake hujazwa hadi GTIN-14 (`isRequiresGtinPadding()`) hurukwa kabisa — payload yao ni thamani ghafi ya tarakimu ya msimbo pau bila muundo wowote wa AI, hivyo hapo mabano wala nafasi haziwezi kuwa na maana.

#### Mpangilio: nafasi kwanza, mabano baadaye

Unapotumia zote mbili, **sajili `ModifierRemoveSpaces` kwanza**. Ulinganishaji wa mabano hutegemea nafasi: `( 01 )` yenye nafasi hailingani na `\((\d{2,4})\)`, hivyo mabano hubaki na kitenganishi walichokimaanisha hakirudi kamwe.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```


### Kuandika modifier

Ikiwa hakuna kati ya modifier mbili za ndani inayofaa, andika yako mwenyewe — kuna mbinu moja tu katika kiolesura.

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

Ikiwa uandikaji upya unategemea usanidi wa uchanganuzi, badala yake fanya override kwa umbo lenye hoja mbili:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Mkataba:

| Kanuni | Maelezo |
|---|---|
| Bila hali na salama kwa thread | Instance moja ya kila klasi huhifadhiwa na kushirikiwa katika kila uchanganuzi. |
| Konstrakta ya umma isiyo na hoja | Inahitajika tu iwapo modifier inatajwa kwa jina la klasi. |
| Shughulikia `null` na ingizo tupu | Parser haiyachuji kabla mnyororo haujakimbia. |
| Kurudisha `null` kunamaanisha "hakuna badiliko" | Thamani iliyotangulia husongezwa mbele. Rudisha `input` bila kubadilika iwapo modifier haihusiki. |
| Ni afadhali kurudisha bila kubadilika kuliko kutupa exception | Modifier inayotupa exception hukatiza uchanganuzi — angalia [Kushughulikia kushindwa](#kushughulikia-kushindwa-kwa-modifier). |
| `getName()` | Ifanyie override ili kudhibiti jina linaloripotiwa kwenye `ModifierInfo`; chaguo-msingi ni jina rahisi la klasi. |

### Kusajili modifier

Modifier hukimbia kwa mpangilio uliozizidisha, na kila moja hupokea matokeo ya iliyotangulia. Zisajili kwa instance, kwa jina kamili la klasi, au kwa orodha ya lolote kati ya hayo:

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

[Modifier za ndani](#modifier-za-ndani) nazo hutajwa kama zako mwenyewe — **daima kwa jina kamili**. Hazina jina fupi wala utafutaji wa lakabu; `ModifierRegistry` hutatua kila modifier, ya ndani au la, kwa jina kamili la klasi.

`ModifierRegistry` ndiyo hutatua majina; huunda instance moja ya kila klasi mara moja kwa konstrakta yake isiyo na hoja kisha huihifadhi kwa kila usanidi unaofuata unaotaja klasi hiyo hiyo. Utatuzi huu hutokea **wakati usanidi unaundwa**, hivyo jina lisilopatikana, klasi isiyotekeleza `ModifierInterface`, au isiyoweza kuundwa instance, hutupa `IllegalArgumentException` hapo hapo — si kimya kimya wakati wa uchanganuzi. Modifier isiyoweza kujengwa kwa reflection (mfano, kwa sababu inabeba utegemezi uliodungwa) inaweza kusajiliwa mapema ili ibaki inaweza kutajwa kwa jina:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Kuangalia modifier ilichofanya

Modifier zikiwekwa, `ParseResult.getPayload()` huonyesha ingizo **lililobadilishwa**. La awali hubaki kwenye `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` huripoti `getName()` ya kila modifier, ambayo chaguo-msingi lake ni jina rahisi la klasi lakini modifier mbili za ndani huifanyia override — hivyo mnyororo wa hizo mbili huonyesha majina ya kuonyesha badala ya majina ya klasi:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

Ikiwa hakuna modifier iliyowekwa, `getModifierInfo()` hurudisha `null`. Ikiwa modifier zilikimbia lakini kila moja ilirudisha ingizo bila kubadilika, taarifa hubaki lakini `isModified()` ni `false` — ni modifier zilizobadilisha ingizo kweli pekee zinazoonekana katika `getAppliedModifiers()`.

### Kushughulikia kushindwa kwa modifier

Modifier inayotupa exception hukatiza uchanganuzi. Exception hiyo hufungwa ndani ya `GaiaModifierException` inayotaja modifier iliyokosea, na matokeo hubeba hitilafu ya ndani ya `GE-I001` yenye jina hilo hilo katika ujumbe wake; `getPayload()` huonyesha ingizo lisilobadilishwa. Kwa makusudi uchanganuzi **hauendelei** na mfuatano ulioandikwa upya nusu — hatua ya urekebishaji iliyoshindwa kimya kimya huzalisha matokeo yanayoonekana halali lakini yaliyochanganuliwa kutoka ingizo lisilo sahihi.

---

## Hali za Uchanganuzi

Kila hali imepewa jina la [hatua ya mtiririko](#mtiririko-wa-uchanganuzi) yenye kina zaidi inayoikimbiza; hata hivyo kila hatua iliyotangulia hukimbia.

| Hali | Hukimbia hadi wapi | Hujibu nini |
|---|---|---|
| `DATA_CARRIER` | Hatua ya 1 (kuelekeza ingizo) | Ni simbolojia gani iliyoleta hii? |
| `SYNTAX` | Hatua ya 2 (sintaksia) | Misimbo ya AI na urefu wake vimeundwa vizuri? |
| `CONTENT` | Hatua ya 3 (maudhui) | Thamani hizi ni data halali za GS1? |
| `INTERPRETATION` | Hatua ya 4 (tafsiri) | Thamani hizi zina maana gani? |

### Hali ya DATA_CARRIER

Husimama baada ya Hatua ya 1 — huthibitisha AIM Code ID na kutambua simbolojia, lakini haiingii katika mtiririko wa kuchanganua AI. Ni muhimu kwa kutambua simbolojia na kuelekeza bila uzito wa uthibitishaji kamili.

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

**Lini kuitumia:** Programu yako inapohitaji kujua aina ya msimbo pau kabla ya kuamua jinsi ya kushughulikia payload — mfano, kuelekeza simbolojia za 1D na 2D kwa washughulikiaji tofauti. Kwa uelekezaji huo, tumia [`DataCarrierType`](#datacarrierentry-na-datacarriertype) yenye aina (`getDataCarrier().getDataCarrierType()`) badala ya kulinganisha mfuatano kwenye `getName()`.

---

### Hali ya SYNTAX

Husimama baada ya Hatua ya 2. Ni muhimu kwa uchujaji wa kimuundo bila gharama ya uthibitishaji wa maudhui.

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

**Lini kuitumia:** Unapotaka kuhakikisha kuwa misimbo ya AI na urefu wa data ni sahihi kabla ya kuingia katika uthibitishaji kamili, au unapochanganua kwa wingi ambapo hitilafu za maudhui ni nadra.

---

### Hali ya CONTENT

Husimama baada ya Hatua ya 3.

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

> AI nyingi haziwezi kusimama peke yake: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) na
> `21` (SERIAL) — kila moja *inahitaji* ufunguo wa utambulisho kama AI `01` katika mfuatano
> uleule wa kipengele; hivyo ondoa GTIN kwenye mfano ulio hapo juu na itashindwa katika
> Hatua ya 2 kwa `GE-S005` kabla hata haijafika kwenye uthibitishaji wa maudhui. Ili
> kuchanganua vipande vilivyokusudiwa kuwa bila AI wenzao, weka `skipRequiresCheck(true)`
> kwenye `ParseConfig`.

**Lini kuitumia:** Unapohitaji kujua kuwa thamani iliyochanganuliwa inatii GS1 kikamilifu kabla ya kuitumia katika mchakato wa biashara, lakini hutaki uzito wa uboreshaji wa tafsiri.

---

### Hali ya INTERPRETATION (chaguo-msingi)

Hukimbiza mtiririko kamili hadi Hatua ya 4. Hii ndiyo chaguo-msingi `parse(String)` inapoitwa bila hoja ya hali. Huboresha tu vipengele vilivyopita uthibitishaji wa maudhui bila hitilafu.

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

**Mfano wa matokeo:**
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

**Mfano wa kiasi cha fedha (AI 3932 — bei yenye msimbo wa sarafu wa ISO):**
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

**Lini kuitumia:** Unapojenga safu ya uonyeshaji, zana ya kukagua lebo, au UI yoyote inayohitaji uchambuzi wa thamani za AI kwa namna inayoeleweka kwa binadamu.

---

## Correlation ID

Baadhi ya mitiririko ya kazi huweka kitambulisho chake cha correlation cha tarakimu 8 mbele ya ingizo ghafi la GS1 ili matukio ya kuchanganua yaweze kuunganishwa na kipindi au muamala. Umbizo ni:

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

`~` (tilde) ndiyo kitenganishi. **Si** sehemu ya maudhui ya GS1 — huondolewa kabla uchanganuzi wowote wa GS1 haujaanza.

### Kanuni za utambuzi

Kiambishi awali hutambuliwa ingizo linapoanza na tarakimu 8 kamili za desimali za ASCII (`0`–`9`) zikifuatwa mara moja na `~`. Ikiwa herufi ya 9 si `~`, au ikiwa mojawapo ya herufi 8 za mwanzo si tarakimu, ingizo huchukuliwa kama maudhui ya GS1 pekee bila kiambishi awali cha correlation.

### Kupata correlation ID

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

### Kuchanganya na AIM Code ID

Kiambishi awali cha correlation kinaweza kutokea kabla ya AIM Code ID. Parser hushughulikia hili kwa uwazi:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Klasi ya utekelezaji:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** husimba thamani moja au zaidi za AI moja kwa moja katika muundo wa URL ya HTTP(S), ikitoa vitambulisho vinavyoweza kutatuliwa kwenye wavuti kwa bidhaa halisi. GAIA inatekeleza *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) kwa URI **zisizobanwa**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` hutambua Digital Link URI kiotomatiki — ingizo lolote linaloanza na `http://` au `https://` huelekezwa kwa `GS1DLParser`, ambayo hukimbiza hatua zilezile za maudhui na tafsiri kama mtiririko wa mfuatano wa kipengele.

### Muundo wa URI na majukumu ya AI

Kila AI katika Digital Link URI ina mojawapo ya majukumu matatu, linalopatikana kwenye kila `GS1AIObjectElement` kupitia `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Jukumu | Mahali | Mfano |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Jozi ya kwanza ya `/ai/value` katika njia (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Jozi za njia zinazofuata, zikipangwa kwa ufunguo mkuu (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Vigezo vya query vyenye funguo za tarakimu pekee (§4.10) | `?17=271231` |

Kanuni za muundo zinazotekelezwa (`DLPathRules`):
- Ufunguo mkuu mmoja **kamili** wa utambulisho katika njia; funguo za ziada lazima zisimbwe kama data attribute za query.
- Key qualifier lazima zikubaliwe na ufunguo mkuu na zitokee kwa mpangilio ulioagizwa. Qualifier za hiari zinaweza kuachwa, lakini zile *zilizopo* lazima bado zifuate mpangilio uliowekwa — angalia [Mpangilio wa qualifier](#mpangilio-wa-qualifier).
- Sehemu maalum za njia zinaweza kutangulia ufunguo mkuu (mfano, `/products/au/01/...`); zipate kupitia `getDigitalLinkInfo().getCustomPathStem()`.
- Funguo za query zisizo za tarakimu (`linkType`, `context`, na vigezo vya kupanua kama `23P`) hupuuzwa; funguo za tarakimu pekee lazima ziwe AI halali zilizowekwa alama ya `validAsDataAttribute`.
- Herufi za thamani zilizosimbwa kwa asilimia hufumbuliwa; AI `(03)` na `(8014)` haziruhusiwi.

Funguo kuu na mfululizo wa qualifier wanaozikubali **hutoka kwenye data** ya ufafanuzi wa AI — kupitia bendera ya `gs1DigitalLinkPrimaryKey` na sifa ya `gs1DigitalLinkQualifiers` — badala ya kuandikwa moja kwa moja kwenye msimbo.

Ukiukaji wowote wa muundo, au ingizo lisilo URL, huzalisha hitilafu ya muundo wa Digital Link (`GE-L001`–`GE-L014`, msimbo mmoja kwa kila sharti). Metadata ya URL iliyochambuliwa (`scheme`, `domain`, `path`, `customPathStem`, `query`, na `java.net.URL`) inapatikana kupitia `getDigitalLinkInfo()` hata pale kuna hitilafu za muundo.

### Mpangilio wa qualifier

Kwa kila ufunguo mkuu, `gs1DigitalLinkQualifiers` huorodhesha mfululizo mmoja au zaidi wa qualifier **uliopangwa**. Ndani ya mfululizo, AI iliyofungwa katika mabano ya mraba ni ya **hiari**, AI isiyo na mabano ni ya **lazima** — sawa na notesheni ya `[cpv-comp]` ya ABNF ya §4.9. Mifululizo ya ufunguo mkuu mmoja ni njia mbadala zinazotenguana.

GTIN (`01`), kwa mfano, hufafanua mifululizo miwili:

| Njia | Mfululizo | Maana |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — kila moja ya hiari, lakini mpangilio huu hauwezi kubadilika |
| upui-path | `235` | TPX (ya lazima); GTIN + TPX = UPUI |

Hivyo `/01/09506000134352/10/LOT-ABC/21/SER` ni halali (LOT kabla ya SER, CPV imeachwa), `/01/.../21/SER/10/LOT-ABC` **hukataliwa** (mpangilio umevurugika), na `/01/09506000134352/235/2ABC456` ndiyo upui-path. Ukaguzi wa mpangilio ni ulinganishaji wa mfululizo mdogo unaohifadhi mpangilio, hivyo AI za hiari zinaweza kurukwa lakini kamwe hazipangwi upya.

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

**Klasi ya utekelezaji:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Kufanya Kazi na Matokeo

### ParseResult

Matokeo ya ngazi ya juu yanayorudishwa na `GaiaParser.parse()`.

| Mbinu | Hurudisha | Maelezo |
|---|---|---|
| `isValid()` | `boolean` | `true` iwapo hakuna hitilafu katika kiwango chochote. Maonyo hayaathiri uhalali. Daima `true` pale `getAiObject()` ni `null`. |
| `getPayload()` | `String` | Mfuatano wa ingizo baada ya kuondolewa kiambishi awali cha correlation — na baada ya [input modifier](#input-modifier) yoyote kuuandika upya. |
| `getPayloadContent()` | `String` | Payload iliyoondolewa AIM Code ID na kiambishi awali cha ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (kibeba data kilichokataliwa kwa kuwa si cha GS1, mfano kibeba data `]A0` cha Code 39), au `UNABLE_TO_DETERMINE_CONTENT` (pale `aiObject` ni `null`, mfano katika hali ya `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Kina cha mtiririko kilichowekwa (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Hatua yenye kina zaidi ambayo uchanganuzi ulifikia kweli — angalia hapa chini. |
| `isParseComplete()` | `boolean` | `true` iwapo uchanganuzi ulifikia kina kilichoombwa (`achieved == requested`). Haitegemei `isValid()`. |
| `getAiObject()` | `GS1AIObject` | AI zote zilizotambuliwa. `null` katika hali ya `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Hitilafu zote zisizo WARNING (kiwango cha object + viwango vyote vya kipengele). |
| `getWarnings()` | `List<GaiaError>` | Ushauri wote wa WARNING (kiwango cha object + viwango vyote vya kipengele). |
| `hasWarnings()` | `boolean` | `true` iwapo ushauri wowote wa WARNING ulitolewa. |
| `getIssues()` | `List<GaiaError>` | Hitilafu na maonyo pamoja. |
| `hasDataCarrier()` | `boolean` | `true` iwapo AIM Code ID ilitambuliwa. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata ya simbolojia, au `null` iwapo hakuna kibeba data kilichotambuliwa. |
| `hasEci()` | `boolean` | `true` iwapo kiashiria cha ECI kiliondolewa kwenye payload. |
| `getEci()` | `EciEntry` | Metadata ya usimbaji wa ECI, au `null`. |
| `hasCorrelationId()` | `boolean` | `true` iwapo kiambishi awali cha correlation `DDDDDDDD~` kilikuwepo katika ingizo asilia. |
| `getCorrelationInfo()` | `CorrelationInfo` | Correlation ID iliyotolewa, au `null` iwapo haikuwepo. |
| `isInputModified()` | `boolean` | `true` iwapo [input modifier](#input-modifier) ilibadilisha ingizo. |
| `getModifierInfo()` | `ModifierInfo` | Kile mnyororo wa modifier ulichofanya — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` iwapo hakuna modifier iliyowekwa. |
| `getTiming()` | `ProcessingTiming` | Muda halisi wa uchanganuzi — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` iwapo hakizalishwa na `GaiaParser`. |
| `getVersion()` | `String` | Toleo la maktaba lililozalisha matokeo. |

#### Hali iliyoombwa dhidi ya hali iliyofikiwa

Mtiririko hukimbia ngazi ya **SYNTAX → CONTENT → INTERPRETATION** na husimama mapema pale kuna hitilafu, hivyo hali *iliyofikiwa* kweli inaweza kuwa na kina kidogo kuliko hali *iliyoombwa*. `getAchievedParseMode()` huripoti ulifika wapi:

| Iliyoombwa | Kinachotokea | Iliyofikiwa | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | hitilafu ya **sintaksia / muundo** husimamisha uchanganuzi baada ya kugawa vipande | `SYNTAX` | `false` |
| `INTERPRETATION` | hitilafu ya **maudhui** (umbizo/tarakimu ya ukaguzi mbovu) huzuia uboreshaji | `CONTENT` | `false` |
| `CONTENT` | maudhui daima hukimbia hadi mwisho (hitilafu hurekodiwa, si za kuua) | `CONTENT` | `true` |
| yoyote (ingizo safi) | mtiririko hufikia kina kilichoombwa | = iliyoombwa | `true` |
| `DATA_CARRIER` | kibeba data kimethibitishwa; hakuna maudhui ya AI yaliyochanganuliwa | `DATA_CARRIER` | `true` |
| yoyote | kibeba data kimekataliwa kabla ya kuchanganua AI (mfano, kibeba data `]A0` kisicho cha GS1) | `SYNTAX` | `false` |

`isParseComplete()` haitegemei `isValid()`: uchanganuzi wa `CONTENT` wa GTIN yenye tarakimu ya ukaguzi mbovu **umekamilika** (ulikimbiza hatua ya maudhui) lakini **si halali** (tarakimu ya ukaguzi ilishindwa). Tumia `isParseComplete()` kuuliza "je, mtiririko ulikimbia hadi kina nilichoomba?" na `isValid()` kuuliza "je, data imeundwa vizuri?".

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

Mkusanyiko wa vipengele vya AI vilivyotambuliwa.

| Mbinu | Maelezo |
|---|---|
| `getAis()` | Instance zote za `GS1AIObjectElement` kwa mpangilio wa ingizo. |
| `get(String aiCode)` | Kipengele cha kwanza kinacholingana na msimbo wa AI uliotolewa, au `null`. |
| `contains(String aiCode)` | `true` iwapo AI yenye msimbo huo ipo. |
| `size()` | Idadi ya AI zilizotambuliwa. |
| `isValid()` | `true` iwapo hakuna hitilafu za kiwango cha object na hakuna kipengele chenye hitilafu. |
| `toHriString()` | Mfuatano wa HRI, mfano `(01)09506000134352 (17)261231`. |
| `toElementString()` | Mfuatano ghafi wa kipengele — bila mabano, na FNC1 baada ya kila kipengele chenye urefu unaobadilika — mfano `010950600013435210LOT-ABC<GS>17271231`. Hurudisha `null` iwapo `isValid()` ni `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` pale `hasDigitalLink()` ni kweli, vinginevyo `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` iwapo ingizo lilikuwa GS1 Digital Link URI yenye ufunguo mkuu wa utambulisho. URL iliyoundwa vizuri lakini isiyo na ufunguo mkuu bado hufunua `getDigitalLinkInfo()` lakini hurudisha `false` hapa. |
| `getCanonicalDigitalLink()` | GS1 Digital Link URI ya kikanoni (§4.12) kwenye `https://id.gs1.org` — ufunguo mkuu na qualifier kama sehemu za njia, data attribute kama vigezo vya query vilivyopangwa kwa ufunguo wa AI — au `null` iwapo hakuna ufunguo mkuu. |
| `getDigitalLinkInfo()` | Metadata ya uchambuzi wa URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), au `null` iwapo si Digital Link. |
| `getAllErrors()` | Kiwango cha object + hitilafu zote za vipengele (zisizo WARNING). |
| `getAllWarnings()` | Kiwango cha object + maonyo yote ya vipengele. |
| `getAllIssues()` | Vyote pamoja. |

---

### GS1AIObjectElement

Instance moja ya AI iliyotambuliwa.

| Mbinu | Maelezo |
|---|---|
| `getAi()` | Msimbo wa AI, mfano `"01"`, `"3102"`. |
| `getTitle()` | Jina la data la GS1, mfano `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Maelezo kamili ya GS1 ya AI, **yaliyotafsiriwa kwa lugha ya uchanganuzi** (mfano, `"Global Trade Item Number (GTIN)"` kwa Kiingereza). Hurejea maandishi ya Kiingereza kutoka ufafanuzi wa AI iwapo hayajatafsiriwa. |
| `getFormatString()` | Kielezi cha umbizo kinachojumuisha AI *pamoja na* data yake, mfano `"N2+N14"` kwa AI `01`, `"N2+X..20"` kwa AI `10`, `"N4+N3+N..15"` kwa AI `3932`. |
| `getValue()` | Thamani ghafi ya data iliyotolewa kutoka mfuatano wa kipengele. |
| `isFixedLength()` | `true` iwapo AI ina urefu wa data usiobadilika. |
| `getPosition()` | Nafasi ya herufi inayoanzia sifuri katika ingizo asilia. |
| `getGS1ComponentValues()` | Vipande vya thamani kwa kila kijenzi (kwa AI zenye vijenzi vingi). |
| `getErrors()` | Hitilafu za kiwango cha kipengele zisizo WARNING. |
| `getWarnings()` | Ushauri wa WARNING wa kiwango cha kipengele. |
| `getIssues()` | Hitilafu na maonyo ya kiwango cha kipengele pamoja. |
| `hasErrors()` | `true` iwapo kuna hitilafu zozote zisizo WARNING zilizoambatishwa. |
| `hasWarnings()` | `true` iwapo kuna ushauri wowote wa WARNING ulioambatishwa. |
| `getInterpretations()` | Viingizo vya `GS1AIInterpretation` (hujazwa katika hali ya INTERPRETATION). |
| `getInterpretation(String type)` | Tafsiri ya kwanza inayolingana na ufunguo wa aina wa `GS1Constants_Enricher` uliotolewa, au `null`. |
| `getDigitalLinkAIType()` | Jukumu la kipengele katika Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), au `null` kwa ingizo la mfuatano wa kipengele. |
| `hasDigitalLinkAIType()` | `true` iwapo jukumu la Digital Link limekabidhiwa. |

---

### GaiaError

Hitilafu ya uthibitishaji au ushauri isiyobadilika.

| Mbinu | Maelezo |
|---|---|
| `getId()` | Kitambulisho cha katalogi, mfano `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, au `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, au `INTERNAL`. |
| `getCode()` | Msimbo mfupi unaosomeka na mashine. |
| `getAi()` | Msimbo wa AI uliosababisha hitilafu, au `null` kwa hitilafu za kiwango cha object. |
| `getMessage()` | Ujumbe unaosomeka na binadamu wenye thamani zilizojazwa. |
| `getPosition()` | Nafasi ya herufi inayoanzia sifuri katika ingizo asilia. |

---

### GS1AIInterpretation

Kipande kimoja cha tafsiri chenye lebo, kinachoambatishwa kwenye `GS1AIObjectElement` katika hali ya `INTERPRETATION`.

| Mbinu | Maelezo |
|---|---|
| `getType()` | Ufunguo wa aina unaosomeka na mashine, mfano `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Haubadiliki katika lugha zote. |
| `getLabel()` | Lebo inayosomeka na binadamu, **iliyotafsiriwa kwa lugha ya uchanganuzi** (mfano, `"Date"` / `"GS1 company prefix"` kwa Kiingereza). |
| `getValue()` | Thamani iliyotolewa/iliyoboreshwa, mfano `"31/12/2026"`, `"9506000"`. Haitafsiriwi. |

---

### DataCarrierEntry na DataCarrierType

Ingizo linapobeba AIM Code ID, `ParseResult.getDataCarrier()` hurudisha `DataCarrierEntry` inayoeleza alama iliyobeba data. Kiingizo hiki ni rekodi mahususi ya rejista kwa AIM Code ID iliyolingana; `DataCarrierType` ni enum ya wakati wa ujumuishaji ambayo ni sehemu yake.

#### DataCarrierEntry

Metadata ya AIM Code ID moja iliyotambuliwa (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Mbinu | Maelezo |
|---|---|
| `getAimCodeId()` | AIM Code ID iliyolingana, mfano `"]C1"`. |
| `getName()` | Jina la alama mahususi linalosomeka na binadamu, mfano `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Maelezo marefu zaidi ya kibeba data. |
| `getType()` | Aina ya kimuundo ya kibeba data kama mfuatano (inalingana na `getDataCarrierType().getCategory()`). |
| `getStandard()` | Kiwango cha simbolojia, pale kilipoandikwa. |
| `getDataCarrierType()` | `DataCarrierType` yenye aina kwa kiingizo hiki — hii ndiyo ya kupendelewa kwa uelekezaji katika msimbo. |
| `isGs1Capable()` | `true` iwapo kibeba data kinaweza kubeba data za GS1 (mifuatano ya vipengele vya AI na/au Digital Link). |
| `isGs1AICapable()` | `true` iwapo kibeba data kinaweza kubeba mifuatano ya vipengele vya GS1 AI. |
| `isGs1DigitalLinkCapable()` | `true` iwapo kibeba data kinaweza kubeba GS1 Digital Link URI. |
| `isEciCapable()` | `true` iwapo kibeba data kinaunga mkono kiashiria cha ECI. |
| `isRequiresGtinPadding()` | `true` kwa vibeba data vya EAN/UPC/ITF ambavyo thamani yake ya tarakimu hujazwa hadi GTIN-14 kabla ya kuchanganua AI. |

#### DataCarrierType

Enum ya wakati wa ujumuishaji ya aina za vibeba data, ikiwa na ufunguo wa AIM Code ID iliyokabidhiwa katika ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Herufi baada ya `]` (yaani *herufi ya msimbo*) huchagua familia; familia nyingi hulingana na konstanti moja inayojumuisha kila modifier (`ITF` hujumuisha `]I0`–`]I2`; `EAN_UPC` hujumuisha EAN-13, UPC-A, UPC-E na EAN-8). Pale GS1 imehifadhi modifier kwa ajili ya data za AI, tofauti hiyo ina konstanti yake yenyewe — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — tofauti na wenzao wa kawaida. Pale hakuna AIM Code ID, au inapotaja kibeba data kisichojulikana, aina ni `UNKNOWN`.

| Mbinu | Maelezo |
|---|---|
| `getCategory()` | `GaiaConstants.DataCarrierTypeCategory` pana: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, au `OTHER`. |
| `getCodeChar()` | Herufi ya msimbo wa AIM inayotambulisha familia, mfano `"Q"` kwa QR Code; `null` kwa `UNKNOWN`. |
| `getDisplayName()` | Jina la *aina* linalosomeka na binadamu (linaweza kuwa pana kuliko `DataCarrierEntry.getName()` — mfano, `"EAN-13 / UPC-A / UPC-E / EAN-8"` dhidi ya `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` kwa konstanti zinazoashiria daima data za GS1 AI: tofauti nne zilizohifadhiwa na GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) pamoja na `GS1_DATABAR`, ambayo kwa asili yake ni ya GS1 kwa kuwa kila modifier ya `]e` ni GS1 DataBar. Ni finyu kuliko `DataCarrierEntry.isGs1AICapable()` — `QR_CODE` ya kawaida nayo inaweza kubeba data za GS1 AI. |
| `static forAimCodeId(String)` | Hutatua aina moja kwa moja kutoka AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); hurudisha `UNKNOWN` kwa ID isiyopo, iliyoharibika, au isiyotambuliwa. |

Kuelekeza kwa aina badala ya kwa jina — mfano, kutenganisha alama za linear (Code-128) na za 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` hujumuisha alama za matrix na dot pekee; vibeba data vya stacked-linear (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) ni `STACKED_LINEAR`, ingawa mara nyingi huitwa misimbo pau
ya "2D". Ili kuvichukulia vyote kama kundi moja — tuseme, kuamua iwapo imager inahitajika
badala ya kichanganuzi cha leza — kagua mojawapo ya makundi hayo mawili.

> Kutatua aina kunahitaji AIM Code ID iwepo katika kile kilichochanganuliwa; bila hiyo, `getDataCarrier()` ni `null` na aina ni `UNKNOWN`. Weka kichanganuzi kutuma kiambishi awali cha AIM Code ID.

---

## Marejeleo ya Hitilafu

| Msimbo | Kiwango | Hatua | Maana |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Kiambishi awali cha AI kisichojulikana — haiwezekani kubaini urefu wa data |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Ingizo ni fupi mno kusoma msimbo kamili wa AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Thamani iliyokatwa — herufi ni chache kuliko AI inavyohitaji |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Application Identifier iliyorudiwa katika mfuatano wa kipengele |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Utegemezi unaohitajika wa AI haupo |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Jozi ya AI iliyozuiliwa — AI mbili zisizoweza kutokea pamoja |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Kushindwa kwa kugawa vipande kusikotarajiwa |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Herufi iliyo nje ya seti ya herufi zinazoweza kusimbwa na GS1 katika mfuatano wa kipengele |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Kitenganishi cha FNC1 kinachohitajika hakipo baada ya AI yenye urefu unaobadilika |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Data iliyobaki zaidi ya kikomo cha juu cha vijenzi vyote |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Kitenganishi cha FNC1 baada ya AI yenye urefu usiobadilika katikati ya mfuatano |
| `GE-W002` | WARNING | SYNTAX | FNC1 iliyobaki mwishoni mwa mfuatano wa kipengele (ushauri tu) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Ukiukaji wa muundo wa Digital Link URI — msimbo mmoja kwa kila sharti (URI iliyoharibika, scheme, host, mpangilio wa qualifier, AI iliyozuiliwa, hakuna ufunguo mkuu (`GE-L013`), funguo kuu zaidi ya moja (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Thamani haiendani na muundo wa regex wa AI |
| `GE-C003` | DATA_ERROR | CONTENT | Uthibitishaji wa tarakimu ya ukaguzi umeshindwa |
| `GE-C004` | DATA_ERROR | CONTENT | Uthibitishaji wa jozi ya herufi ya ukaguzi umeshindwa |
| `GE-C005` | FORMAT_ERROR | CONTENT | Thamani ya kijenzi ina herufi iliyo nje ya seti ya herufi zinazoruhusiwa |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Kushindwa kwa umbizo la kijenzi — msimbo mmoja kwa kila sharti la kithibitishaji (angalia `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Kushindwa kwa uthibitishaji maalum wa maana — msimbo mmoja kwa kila sharti la kithibitishaji (angalia `content/validator/`). **Vighairi:** ukaguzi 14 wa kiambishi awali cha kampuni GS1 ulioorodheshwa hapa chini una kiwango cha `WARNING`, na `GE-C168` (msimbo wa nchi wa tarakimu wa ISO 3166-1 usiotambuliwa) una kiwango cha `FORMAT_ERROR`. |
| Ukaguzi wa kiambishi awali cha kampuni GS1 | WARNING | CONTENT | Ufunguo hauanzi na kiambishi awali cha kampuni GS1 kinachotambuliwa, kwenye AI zenye funguo za GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Ushauri tu — hauathiri uhalali. |
| `GE-C169` | DATA_ERROR | CONTENT | Tarakimu ya ukaguzi ya IMEI (Luhn) imeshindwa kwenye AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Tarakimu ya ukaguzi ya EID (Luhn) imeshindwa kwenye AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | AIM Code ID isiyotambuliwa |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Kibeba data kimetambuliwa lakini hakiungi mkono mifuatano ya vipengele vya GS1 AI wala Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Hitilafu ya ndani isiyotarajiwa |

> **Kasoro inayojulikana katika uonyeshaji wa ujumbe.** Violezo vya katalogi hufunga thamani
> zilizojazwa kwa alama za nukuu mbili za mtindo wa MessageFormat (`''{value}''`), lakini
> `ErrorRegistry` hujaza kwa `String.replace` ya kawaida, hivyo urudufu huo hubaki hadi
> kwenye `getMessage()` — kwa sasa utaona `value ''09506000134351''` mahali ambapo maandishi
> ya ujumbe yaliyonukuliwa katika mwongozo huu yanaonyesha `value '09506000134351'`.
> Huathiri kila ujumbe unaonukuu thamani katika katalogi zote 35 za lugha. Usichanganue
> ujumbe wa hitilafu; linganisha kwenye `getId()` / `getCode()`.

---

## Usalama wa Thread

`GaiaParser` ni salama kwa thread mara tu inapoundwa. Instance moja inaweza kushirikiwa kati ya thread nyingi na kutumiwa kwa wakati mmoja. Mtindo unaopendekezwa ni kuunda instance moja wakati programu inaanza kisha kuitumia tena:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` haibadiliki na ni salama vivyo hivyo kushirikiwa. Wajibu pekee wa usalama wa thread ambao maktaba haiwezi kuutekeleza kwa niaba yako uko kwenye [input modifier](#input-modifier): instance moja ya kila modifier huhifadhiwa na kushirikiwa katika kila uchanganuzi unaokimbia kwa wakati mmoja, hivyo utekelezaji lazima uwe bila hali.

---


## Kiambatisho A — Konstanti za Mfuatano wa AI

`GS1Constants_AICodes` (katika pakiti `tools.pantheum.gaia.gs1.constants`) hutangaza konstanti ya `String` kwa kila Application Identifier ambayo GAIA hutambua. Tumia konstanti hizi badala ya kuandika mifuatano ghafi ya misimbo ya AI moja kwa moja kwenye msimbo:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Kila konstanti hushikilia umbo la mfuatano la msimbo wa AI (mfano, `AI_01_GTIN = "01"`).

### Utambulisho na uwekaji mfululizo

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Msimbo wa Mfululizo wa Kontena la Usafirishaji (SSCC). |
| `01` | `AI_01_GTIN` | Nambari ya Kimataifa ya Bidhaa (GTIN). |
| `02` | `AI_02_CONTENT` | Nambari ya Kimataifa ya Bidhaa (GTIN) ya bidhaa zilizomo. |
| `03` | `AI_03_MTO_GTIN` | Kitambulisho cha bidhaa Inayotengenezwa kwa Oda (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Nambari ya kundi au lot. |
| `20` | `AI_20_VARIANT` | Aina tofauti ya bidhaa ya ndani. |
| `21` | `AI_21_SERIAL` | Nambari ya mfululizo. |
| `22` | `AI_22_CPV` | Aina tofauti ya bidhaa ya mtumiaji. |
| `235` | `AI_235_TPX` | Nyongeza ya Mfululizo Inayodhibitiwa na Mhusika wa Tatu ya Nambari ya Kimataifa ya Bidhaa (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Kitambulisho cha ziada cha bidhaa kilichotolewa na mtengenezaji. |
| `241` | `AI_241_CUST_PART_NO` | Nambari ya sehemu ya mteja. |
| `242` | `AI_242_MTO_VARIANT` | Nambari ya mchepuko wa Kutengenezwa kwa Oda. |
| `243` | `AI_243_PCN` | Nambari ya kipengele cha ufungashaji. |
| `250` | `AI_250_SECONDARY_SERIAL` | Nambari ya mfululizo ya pili. |
| `251` | `AI_251_REF_TO_SOURCE` | Rejea ya chombo chanzo. |
| `253` | `AI_253_GDTI` | Kitambulisho cha Aina ya Hati ya Kimataifa (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Kipengele cha nyongeza cha Nambari ya Mahali pa Kimataifa (GLN). |
| `255` | `AI_255_GCN` | Nambari ya Kuponi ya Kimataifa (GCN). |
| `30` | `AI_30_VAR_COUNT` | Idadi inayobadilika ya vitu (bidhaa yenye kipimo kinachobadilika). |
| `37` | `AI_37_COUNT` | Idadi ya bidhaa au vipande vya bidhaa vilivyomo kwenye kitengo cha lojistiki. |

### Tarehe na nyakati

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Tarehe ya uzalishaji (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Tarehe ya mwisho (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Tarehe ya ufungashaji (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Tarehe bora kabla ya (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Tarehe ya mwisho ya kuuza (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Tarehe ya mwisho wa matumizi (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Tarehe na saa ya uwasilishaji si kabla ya (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Tarehe na saa ya uwasilishaji si baada ya (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Tarehe ya kutolewa (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Tarehe na saa ya mwisho wa matumizi (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Tarehe ya kwanza ya kugandishwa (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Tarehe ya mavuno (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Tarehe ya mwisho ya kupima (YYMMDD[hhmm]). |

### Idadi na kipimo — kipimo kinachobadilika (metriki)

Familia za AI za tarakimu 4 `310n`–`369n` husimba idadi zenye kipimo kinachobadilika. Tarakimu ya tatu huchagua aina ya kipimo; **tarakimu ya nne** (`n`, 0–5) ni idadi ya nafasi za desimali zinazokusudiwa — mfano, `AI_3102_NET_WEIGHT_KG` inamaanisha uzito halisi kwa kilogramu wenye nafasi 2 za desimali.

| Familia | Muundo wa konstanti (`n` = tarakimu ya nafasi ya desimali) | Maelezo |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Uzito halisi, kilogramu (bidhaa yenye kipimo kinachobadilika). |
| `311n` | `AI_311n_LENGTH_M` | Urefu au kipimo cha kwanza, mita (bidhaa yenye kipimo kinachobadilika). |
| `312n` | `AI_312n_WIDTH_M` | Upana, kipenyo, au kipimo cha pili, mita (bidhaa yenye kipimo kinachobadilika). |
| `313n` | `AI_313n_HEIGHT_M` | Kina, unene, urefu wa wima, au kipimo cha tatu, mita (bidhaa yenye kipimo kinachobadilika). |
| `314n` | `AI_314n_AREA_M` | Eneo, mita za mraba (bidhaa yenye kipimo kinachobadilika). |
| `315n` | `AI_315n_NET_VOLUME_L` | Ujazo halisi, lita (bidhaa yenye kipimo kinachobadilika). |
| `316n` | `AI_316n_NET_VOLUME_M` | Ujazo halisi, mita za ujazo (bidhaa yenye kipimo kinachobadilika). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Uzito wa lojistiki, kilogramu. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Urefu au kipimo cha kwanza, mita. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Upana, kipenyo, au kipimo cha pili, mita. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Kina, unene, urefu wa wima, au kipimo cha tatu, mita. |
| `334n` | `AI_334n_AREA_M_LOG` | Eneo, mita za mraba. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Ujazo wa lojistiki, lita. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Ujazo wa lojistiki, mita za ujazo. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramu kwa kila mita ya mraba. |

### Idadi na kipimo — kipimo kinachobadilika (imperial / US)

| Familia | Muundo wa konstanti (`n` = tarakimu ya nafasi ya desimali) | Maelezo |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Uzito halisi, paundi (bidhaa yenye kipimo kinachobadilika). |
| `321n` | `AI_321n_LENGTH_IN` | Urefu au kipimo cha kwanza, inchi (bidhaa yenye kipimo kinachobadilika). |
| `322n` | `AI_322n_LENGTH_FT` | Urefu au kipimo cha kwanza, futi (bidhaa yenye kipimo kinachobadilika). |
| `323n` | `AI_323n_LENGTH_YD` | Urefu au kipimo cha kwanza, yadi (bidhaa yenye kipimo kinachobadilika). |
| `324n` | `AI_324n_WIDTH_IN` | Upana, kipenyo, au kipimo cha pili, inchi (bidhaa yenye kipimo kinachobadilika). |
| `325n` | `AI_325n_WIDTH_FT` | Upana, kipenyo, au kipimo cha pili, futi (bidhaa yenye kipimo kinachobadilika). |
| `326n` | `AI_326n_WIDTH_YD` | Upana, kipenyo, au kipimo cha pili, yadi (bidhaa yenye kipimo kinachobadilika). |
| `327n` | `AI_327n_HEIGHT_IN` | Kina, unene, urefu wa wima, au kipimo cha tatu, inchi (bidhaa yenye kipimo kinachobadilika). |
| `328n` | `AI_328n_HEIGHT_FT` | Kina, unene, urefu wa wima, au kipimo cha tatu, futi (bidhaa yenye kipimo kinachobadilika). |
| `329n` | `AI_329n_HEIGHT_YD` | Kina, unene, urefu wa wima, au kipimo cha tatu, yadi (bidhaa yenye kipimo kinachobadilika). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Uzito wa lojistiki, paundi. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Urefu au kipimo cha kwanza, inchi. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Urefu au kipimo cha kwanza, futi. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Urefu au kipimo cha kwanza, yadi. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Upana, kipenyo, au kipimo cha pili, inchi. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Upana, kipenyo, au kipimo cha pili, futi. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Upana, kipenyo, au kipimo cha pili, yadi. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Kina, unene, urefu wa wima, au kipimo cha tatu, inchi. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Kina, unene, urefu wa wima, au kipimo cha tatu, futi. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Kina, unene, urefu wa wima, au kipimo cha tatu, yadi. |
| `350n` | `AI_350n_AREA_IN` | Eneo, inchi za mraba (bidhaa yenye kipimo kinachobadilika). |
| `351n` | `AI_351n_AREA_FT` | Eneo, futi za mraba (bidhaa yenye kipimo kinachobadilika). |
| `352n` | `AI_352n_AREA_YD` | Eneo, yadi za mraba (bidhaa yenye kipimo kinachobadilika). |
| `353n` | `AI_353n_AREA_IN_LOG` | Eneo, inchi za mraba. |
| `354n` | `AI_354n_AREA_FT_LOG` | Eneo, futi za mraba. |
| `355n` | `AI_355n_AREA_YD_LOG` | Eneo, yadi za mraba. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Uzito halisi, aunzi troy (bidhaa yenye kipimo kinachobadilika). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Uzito halisi (au ujazo), aunzi (bidhaa yenye kipimo kinachobadilika). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Ujazo halisi, kwati (bidhaa yenye kipimo kinachobadilika). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Ujazo halisi, galoni za Marekani (bidhaa yenye kipimo kinachobadilika). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Ujazo wa lojistiki, kwati. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Ujazo wa lojistiki, galoni za Marekani. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Ujazo halisi, inchi za ujazo (bidhaa yenye kipimo kinachobadilika). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Ujazo halisi, futi za ujazo (bidhaa yenye kipimo kinachobadilika). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Ujazo halisi, yadi za ujazo (bidhaa yenye kipimo kinachobadilika). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Ujazo wa lojistiki, inchi za ujazo. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Ujazo wa lojistiki, futi za ujazo. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Ujazo wa lojistiki, yadi za ujazo. |

### Bei na viasi vya fedha

Tarakimu ya nne (`n`) husimba idadi ya nafasi za desimali zinazokusudiwa. Masafa yake
yanayoruhusiwa hutofautiana kwa kila familia — angalia safu ya `n`.

| Familia | Muundo wa konstanti (`n` = tarakimu ya nafasi ya desimali) | `n` | Maelezo |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Kiasi kinacholipwa au thamani ya kuponi, sarafu ya ndani. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Kiasi kinacholipwa pamoja na msimbo wa sarafu wa ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Kiasi kinacholipwa, eneo moja la fedha (bidhaa yenye kipimo kinachobadilika). |
| `393n` | `AI_393n_PRICE` | 0–9 | Kiasi kinacholipwa pamoja na msimbo wa sarafu wa ISO (bidhaa yenye kipimo kinachobadilika). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Asilimia ya punguzo la kuponi. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Kiasi kinacholipwa kwa kila kizio cha kipimo, eneo moja la fedha (bidhaa yenye kipimo kinachobadilika). |

### Mahali na usafirishaji

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Nambari ya oda ya ununuzi ya mteja. |
| `401` | `AI_401_GINC` | Nambari ya Kitambulisho cha Kimataifa cha Shehena (GINC). |
| `402` | `AI_402_GSIN` | Nambari ya Kitambulisho cha Kimataifa cha Usafirishaji (GSIN). |
| `403` | `AI_403_ROUTE` | Msimbo wa njia. |
| `410` | `AI_410_SHIP_TO_LOC` | Safirisha kwa / Peleka kwa Nambari ya Mahali pa Kimataifa (GLN). |
| `411` | `AI_411_BILL_TO` | Nambari ya Mahali pa Kimataifa (GLN) ya anayetozwa ankara. |
| `412` | `AI_412_PURCHASE_FROM` | Ilinunuliwa kutoka Nambari ya Mahali pa Kimataifa (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | Safirisha kwa ajili ya / Peleka kwa Nambari ya Mahali pa Kimataifa (GLN). |
| `414` | `AI_414_LOC_NO` | Kitambulisho cha eneo halisi - Nambari ya Mahali pa Kimataifa (GLN). |
| `415` | `AI_415_PAY_TO` | Nambari ya Mahali pa Kimataifa (GLN) ya upande unaotoza ankara. |
| `416` | `AI_416_PROD_SERV_LOC` | Nambari ya Mahali pa Kimataifa (GLN) ya eneo la uzalishaji au huduma. |
| `417` | `AI_417_PARTY` | Nambari ya Mahali pa Kimataifa (GLN) ya upande husika. |
| `420` | `AI_420_SHIP_TO_POST` | Msimbo wa posta wa kupeleka ndani ya mamlaka moja ya posta. |
| `421` | `AI_421_SHIP_TO_POST` | Msimbo wa posta wa kupeleka pamoja na msimbo wa nchi wa ISO. |
| `422` | `AI_422_ORIGIN` | Nchi asili ya bidhaa. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Nchi ya uchakataji wa awali. |
| `424` | `AI_424_COUNTRY_PROCESS` | Nchi ya uchakataji. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Nchi ya kubomolea. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Nchi inayohusika na mnyororo mzima wa uzalishaji. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Kitengo cha nchi cha asili. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Jina la kampuni ya kupeleka. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Mawasiliano ya kupeleka. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Mstari wa 1 wa anwani ya kupeleka. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Mstari wa 2 wa anwani ya kupeleka. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Kitongoji cha kupeleka. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Mtaa wa kupeleka. |
| `4306` | `AI_4306_SHIP_TO_REG` | Mkoa wa kupeleka. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Msimbo wa nchi wa kupeleka. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Nambari ya simu ya kupeleka. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Mahali pa kijiografia pa kupeleka. |
| `4310` | `AI_4310_RTN_TO_COMP` | Jina la kampuni ya kurejeshea. |
| `4311` | `AI_4311_RTN_TO_NAME` | Mawasiliano ya kurejeshea. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Mstari wa 1 wa anwani ya kurejeshea. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Mstari wa 2 wa anwani ya kurejeshea. |
| `4314` | `AI_4314_RTN_TO_SUB` | Kitongoji cha kurejeshea. |
| `4315` | `AI_4315_RTN_TO_LOC` | Mtaa wa kurejeshea. |
| `4316` | `AI_4316_RTN_TO_REG` | Mkoa wa kurejeshea. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Msimbo wa nchi wa kurejeshea. |
| `4318` | `AI_4318_RTN_TO_POST` | Msimbo wa posta wa kurejeshea. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Nambari ya simu ya kurejeshea. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Maelezo ya msimbo wa huduma. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Alama ya bidhaa hatari. |
| `4322` | `AI_4322_AUTH_LEAVE` | Idhini ya kuacha shehena. |
| `4323` | `AI_4323_SIG_REQUIRED` | Alama ya kuhitaji sahihi. |
| `4330` | `AI_4330_MAX_TEMP_F` | Halijoto ya juu zaidi kwa Fahrenheit (ikionyeshwa kwa sehemu za mia za nyuzi). |
| `4331` | `AI_4331_MAX_TEMP_C` | Halijoto ya juu zaidi kwa Selsiasi (ikionyeshwa kwa sehemu za mia za nyuzi). |
| `4332` | `AI_4332_MIN_TEMP_F` | Halijoto ya chini zaidi kwa Fahrenheit (ikionyeshwa kwa sehemu za mia za nyuzi). |
| `4333` | `AI_4333_MIN_TEMP_C` | Halijoto ya chini zaidi kwa Selsiasi (ikionyeshwa kwa sehemu za mia za nyuzi). |

### Sifa za bidhaa na ufuatiliaji

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Nambari ya Hisa ya NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Uainishaji wa mizoga na vipande vya nyama vya UN/ECE. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Ukolezo amilifu. |
| `7005` | `AI_7005_CATCH_AREA` | Eneo la uvuvi. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Spishi kwa madhumuni ya uvuvi. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Aina ya zana za uvuvi. |
| `7010` | `AI_7010_PROD_METHOD` | Njia ya uzalishaji. |
| `7020` | `AI_7020_REFURB_LOT` | Kitambulisho cha kundi la ukarabati. |
| `7021` | `AI_7021_FUNC_STAT` | Hali ya utendaji. |
| `7022` | `AI_7022_REV_STAT` | Hali ya marekebisho. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Kitambulisho cha Kimataifa cha Mali Binafsi (GIAI) cha mkusanyiko. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Nambari ya msindikaji yenye msimbo wa nchi wa ISO wa tarakimu tatu (nafasi 10). |
| `7040` | `AI_7040_UIC_EXT` | UIC ya GS1 pamoja na Nyongeza 1 na faharasa ya mwagizaji. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Aina ya kizio cha shehena cha UN/CEFACT. |

### Nambari za Kitaifa za Urejeshaji Gharama za Afya (NHRN)

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Ujerumani PZN. |
| `711` | `AI_711_NHRN_CIP` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Ufaransa CIP. |
| `712` | `AI_712_NHRN_CN` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Uhispania CN. |
| `713` | `AI_713_NHRN_DRN` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Brazili DRN. |
| `714` | `AI_714_NHRN_AIM` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Ureno AIM. |
| `715` | `AI_715_NHRN_NDC` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Marekani NDC. |
| `716` | `AI_716_NHRN_AIC` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Italia AIC. |
| `717` | `AI_717_NHRN_SRN` | Nambari ya Kitaifa ya Urejeshaji Gharama za Afya (NHRN) - Kostarika Nambari ya Usajili wa Afya. |

### Afya, GMN, HIDRI, CPID, data za mtu

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Marejeleo ya Uthibitisho (nafasi 10). |
| `7240` | `AI_7240_PROTOCOL` | Kitambulisho cha Itifaki. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Aina ya media ya AIDC. |
| `7242` | `AI_7242_VCN` | Nambari ya Udhibiti wa Toleo (VCN). |
| `7250` | `AI_7250_DOB` | Tarehe ya kuzaliwa (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Tarehe na saa ya kuzaliwa (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Jinsia ya kibiolojia. |
| `7253` | `AI_7253_FAMILY_NAME` | Jina la ukoo la mtu. |
| `7254` | `AI_7254_GIVEN_NAME` | Jina la kwanza la mtu. |
| `7255` | `AI_7255_SUFFIX` | Kiambishi tamati cha jina la mtu. |
| `7256` | `AI_7256_FULL_NAME` | Jina kamili la mtu. |
| `7257` | `AI_7257_PERSON_ADDR` | Anwani ya mtu. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Mfululizo wa kuzaliwa kwa mtoto. |
| `7259` | `AI_7259_BABY` | Mtoto wa jina la ukoo. |
| `8001` | `AI_8001_DIMENSIONS` | Bidhaa za roli (upana, urefu, kipenyo cha kiini, mwelekeo, viungio). |
| `8002` | `AI_8002_CMT_NO` | Kitambulisho cha simu ya mkononi. |
| `8003` | `AI_8003_GRAI` | Kitambulisho cha Kimataifa cha Mali Inayorejeshwa (GRAI). |
| `8004` | `AI_8004_GIAI` | Kitambulisho cha Kimataifa cha Mali Binafsi (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Bei kwa kila kizio cha kipimo. |
| `8006` | `AI_8006_ITIP` | Kitambulisho cha kipande kimoja cha bidhaa (ITIP). |
| `8007` | `AI_8007_IBAN` | Nambari ya Kimataifa ya Akaunti ya Benki (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Tarehe na saa ya uzalishaji (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Kiashiria cha Sensa Kinachosomeka kwa Macho. |
| `8010` | `AI_8010_CPID` | Kitambulisho cha Kipengele/Sehemu (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Nambari ya mfululizo ya Kitambulisho cha Kipengele/Sehemu (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Toleo la programu. |
| `8013` | `AI_8013_GMN` | Nambari ya Modeli ya Kimataifa (GMN). |
| `8014` | `AI_8014_MUDI` | Kitambulisho cha Usajili wa Kifaa cha Kibinafsi Zaidi (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Nambari ya Uhusiano wa Huduma ya Kimataifa (GSRN) ya kutambua uhusiano kati ya shirika linalotoa huduma na mtoa huduma. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Nambari ya Uhusiano wa Huduma ya Kimataifa (GSRN) ya kutambua uhusiano kati ya shirika linalotoa huduma na mpokeaji wa huduma. |
| `8019` | `AI_8019_SRIN` | Nambari ya Kisa cha Uhusiano wa Huduma (SRIN). |
| `8020` | `AI_8020_REF_NO` | Nambari ya rejea ya risiti ya malipo. |
| `8026` | `AI_8026_ITIP_CONTENT` | Kitambulisho cha vipande vya bidhaa (ITIP) vilivyomo kwenye kitengo cha lojistiki. |
| `8030` | `AI_8030_DIGSIG` | Sahihi ya Kidijitali (DigSig). |
| `8040` | `AI_8040_IMEI` | Kitambulisho cha Kimataifa cha Kifaa cha Simu (IMEI). |
| `8041` | `AI_8041_IMEI2` | Kitambulisho cha Kimataifa cha Kifaa cha Simu 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Nambari ya SIM iliyopachikwa. |
| `8043` | `AI_8043_PSIM` | Nambari ya SIM halisi. |
| `8110` | `AI_8110` | Kitambulisho cha msimbo wa kuponi kwa matumizi Amerika Kaskazini. |
| `8111` | `AI_8111_POINTS` | Alama za uaminifu za kuponi. |
| `8112` | `AI_8112` | Kitambulisho cha msimbo wa kuponi wa faili ya matoleo yaliyoidhinishwa kwa matumizi Amerika Kaskazini. |
| `8200` | `AI_8200_PRODUCT_URL` | URL ya Ufungashaji wa Ziada. |

### Matumizi ya ndani / ya kampuni

| AI | Konstanti | Maelezo |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Taarifa zilizokubaliwa pamoja kati ya washirika wa biashara. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Taarifa za ndani za kampuni (nafasi 9). |

---

## Kiambatisho B — Konstanti za Funguo za Tafsiri

`GaiaParser.parse()` inapoitwa kwa `ParseMode.INTERPRETATION`, kila `GS1AIObjectElement` inaweza kubeba orodha ya objekti za `GS1AIInterpretation` zilizozalishwa na viboreshaji mahususi vya nyanja. Tumia konstanti kutoka `GS1Constants_Enricher` (katika pakiti `tools.pantheum.gaia.gs1.constants`) kama funguo za kutafuta thamani mahususi za tafsiri:

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

Lebo za kuonyesha **si** konstanti — zinaishi katika katalogi zilizotafsiriwa chini ya `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, zikiwa na ufunguo wa konstanti ya aina. `GS1AIInterpretation.getLabel()` hurudisha lebo ya lugha ya uchanganuzi (angalia [Ujumbe na lebo zilizotafsiriwa](#ujumbe-na-lebo-zilizotafsiriwa)), ikirejea Kiingereza pale katalogi inapokosa ufunguo huo. Safu ya "Lebo ya kuonyesha" hapa chini inaorodhesha maandishi ya Kiswahili; funguo za aina zenyewe hazibadiliki katika lugha zote, hivyo linganisha kwenye ufunguo, kamwe si kwenye lebo.

### Tarehe na muda

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `DATE_VALUE` | Tarehe | AI za tarehe (11–17, 7003, 7006, 7011, n.k.) |
| `DATE_FORMAT` | Umbizo la tarehe | AI za tarehe |
| `TIME_VALUE` | Saa | AI zinazobeba muda (7003, 7011, 8008, n.k.) |
| `TIME_FORMAT` | Umbizo la saa | AI zinazobeba muda |
| `DATETIME_VALUE` | Tarehe na saa | AI za tarehe na muda |
| `DATETIME_FORMAT` | Umbizo la tarehe na saa | AI za tarehe na muda |

### Tarehe ya mavuno

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Tarehe ya kuanza mavuno | AI 7007 |
| `HARVEST_END_DATE` | Tarehe ya mwisho ya mavuno | AI 7007 (mwisho wa masafa wa hiari) |
| `HARVEST_DATE_RANGE` | Kipindi cha tarehe ya mavuno | AI 7007 |

### Kiambishi Awali cha Kampuni GS1

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Kiambishi awali cha kampuni GS1 | AI za GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Msimbo wa mwanachama GS1 | AI za GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Shirika mwanachama la GS1 | AI za GTIN / GLN / SSCC |

### GTIN

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Aina ya GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Kiwango cha ufungashaji | AI 01 |
| `GTIN_CHECK_DIGIT` | Tarakimu ya ukaguzi | AI 01, 02 |

### SSCC

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Tarakimu ya nyongeza | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Rejea ya mfululizo | AI 00 |
| `SSCC_CHECK_DIGIT` | Tarakimu ya ukaguzi | AI 00 |

### Nchi (ISO 3166)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Msimbo wa nchi (namba) | AI za nchi moja (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Msimbo wa nchi (alfa-2) | AI za nchi za Alpha-2 |
| `COUNTRY_NAME` | Jina la nchi | AI za nchi moja |
| `COUNTRY_LIST` | Nchi | AI 423 — majina yote yakiunganishwa, mfano `Australia, New Zealand` |

AI 423 (nchi ya usindikaji wa awali) inaweza kubeba hadi nchi tano, hivyo hutoa **jozi moja
yenye nambari kwa kila nchi** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — ikifuatwa na muhtasari mmoja wa
`COUNTRY_LIST`. Jenga funguo hizi kutoka konstanti za `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` pamoja na kielezo kinachoanzia 1, au pitia tu `getInterpretations()`;
funguo za `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` zisizo na kiambishi tamati **hazitolewi**
kwa AI 423.

### Sarafu (ISO 4217)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Msimbo wa sarafu | AI za kiasi zenye sarafu (391n, 393n) |
| `CURRENCY_ALPHA` | Msimbo wa herufi wa sarafu | AI za kiasi zenye sarafu |
| `CURRENCY_NAME` | Jina la sarafu | AI za kiasi zenye sarafu |

### Halijoto

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `TEMPERATURE` | Halijoto | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Kizio cha halijoto | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Halijoto (iliyoumbizwa) | AI 4330–4333 |

### Jinsia (ISO 5218)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `SEX_CODE` | Msimbo wa jinsia | AI 7252 |
| `SEX_DESCRIPTION` | Maelezo ya jinsia | AI 7252 |

### Viumbe vya majini (FAO)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Msimbo wa spishi | AI 7008 |
| `SPECIES_SCIENTIFIC` | Jina la kisayansi | AI 7008 |
| `SPECIES_ENGLISH` | Jina la kawaida | AI 7008 |
| `SPECIES_FAMILY` | Familia | AI 7008 |
| `SPECIES_ORDER` | Oda | AI 7008 |

### Nambari ya Hisa ya NATO (NSN)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `NSN_FSG` | Kundi la ugavi | AI 7001 |
| `NSN_FSG_NAME` | Jina la kundi la ugavi | AI 7001 |
| `NSN_FSCG` | Daraja la ugavi | AI 7001 |
| `NSN_FSCG_NAME` | Jina la daraja la ugavi | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Msimbo wa nchi | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Nchi | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Msimbo wa nchi wa ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Kategoria ya NCS | AI 7001 |
| `NSN_NIIN` | Nambari ya bidhaa ya kitaifa | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Bidhaa za roli

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Upana wa roli (mm) | AI 8001 |
| `ROLL_LENGTH` | Urefu wa roli (m) | AI 8001 |
| `CORE_DIAMETER` | Kipenyo cha kiini (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Msimbo wa mwelekeo wa kuzungusha | AI 8001 |
| `WINDING_DIRECTION` | Mwelekeo wa kuzungusha | AI 8001 |
| `SPLICES` | Viungio | AI 8001 |

### IBAN

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Msimbo wa nchi | AI 8007 |
| `IBAN_COUNTRY_NAME` | Nchi | AI 8007 |
| `IBAN_CHECK_DIGITS` | Tarakimu za ukaguzi | AI 8007 |
| `IBAN_CHECK_VALID` | Ukaguzi | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Nambari ya mfululizo | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Tarakimu ya ukaguzi | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Shirika la utoaji | AI 8040, 8041 |

Tarakimu 15 hugawanyika kama `[ TAC (8) ][ mfululizo (6) ][ tarakimu ya ukaguzi ya Luhn (1) ]`,
huku RBI ikiwa tarakimu 2 za mwanzo za TAC — hivyo `IMEI_RBI` ni kiambishi awali cha
`IMEI_TAC`, si masafa tofauti. `IMEI_FORMATTED` huonyesha mpangilio wa kawaida wa kuonyesha wa
GSMA `AA-BBBBBB-CCCCCC-D` (mfano, `49-015420-323751-8`), unaokata TAC kwenye mpaka wa RBI;
mpangilio wa zamani wa `6-2-6-1`, unaokata mahali ambapo Final Assembly Code iliyoachwa ilikuwa
ikianzia, hautolewi.

`IMEI_RBI_NAME` hutatua RBI kuwa jina la chombo kilichoitoa kupitia `ImeiRbiData`, na
**huongezwa mwisho kabisa na tu pale msimbo umeorodheshwa hapo**. Jedwali hilo hujumuisha
makundi matatu:

- **Vinavyotoa kwa sasa** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, pamoja na `99`
  Global Hexadecimal Administrator na `98` (imehifadhiwa).
- **Masafa ya majaribio** — `00` na `02`–`09`, yakiashiria IMEI za majaribio badala ya utoaji
  halisi. Uliza kwa `ImeiRbiData.isTestCode(code)`.
- **Visivyotoa tena** — vyombo vya kihistoria kama `49` (BZT/BAPT, Ujerumani), `44`
  (BABT, Uingereza) au `91` (MSAI, India). Uliza kwa `ImeiRbiData.isNoLongerAllocating(code)`.
  Vifaa vyenye misimbo hii ni vya kawaida na bado viko kazini; ni utoaji mpya pekee
  uliokoma, hivyo hii ni taarifa ya kuripoti, si kamwe ishara ya uhalali.

Kukosekana kwa `IMEI_RBI_NAME` kunamaanisha "RBI hii haiko katika jedwali letu", **si**
"IMEI si halali": jedwali limekusanywa kutoka orodha ya RBI iliyochapishwa badala ya moja kwa
moja kutoka GSMA, hivyo linaweza kuchelewa kufikia vyombo vilivyoteuliwa hivi karibuni.
Usitoe hitimisho lolote la uthibitishaji kutokana na kukosekana kwake; RBI si herufi ya
ukaguzi. Msimbo unaopitia orodha ya tafsiri nao lazima uvumilie kukosekana kwake badala ya
kutegemea nafasi.

### Vitambulisho vya SIM (EID / ICCID)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Kategoria ya sekta | AI 8042 |
| `EID_BODY` | Mwili wa EID | AI 8042 |
| `EID_CHECK_DIGIT` | Tarakimu ya ukaguzi | AI 8042 |
| `ICCID_BODY` | Mwili wa ICCID | AI 8043 |
| `ICCID_EXTENSION` | Nyongeza | AI 8043 |

`SIM_MII` hubeba tarakimu **mbili** za mwanzo (`89`), jozi ambayo ITU-T E.118 huikabidhi kwa
mawasiliano ya simu. ISO/IEC 7812 yenyewe hufafanua MII kama **tarakimu ya kwanza pekee**,
hivyo `SIM_MII_NAME` hutatua kundi kutoka `8` hiyo ya mwanzo kupitia `Iso7812Data` — ikitoa
"Healthcare, telecommunications and other future industry assignments". Kwa EID iliyoundwa
vizuri, kwa hiyo, hii haibadiliki; huripotiwa ili kuweza kufuatiliwa hadi kwenye kiwango, si
kama kitofautishi. `Iso7812Data.nameForCode(digit)` hupokea tarakimu moja tupu, ilhali
`nameForIdentifier(prefix)` hupokea kiambishi awali kirefu zaidi na kusoma tarakimu yake ya
kwanza.

`SIM_MII_NAME` hutolewa na `EidEnricher` (AI 8042) pekee. `IccidEnricher` (AI 8043) hufunua
`SIM_MII` bila kundi.

### Marejeleo ya uthibitisho

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Nambari ya mfululizo | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Msimbo wa mpango wa uthibitisho | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Mpango wa uthibitisho | AI 7230–7239 |
| `CERT_REFERENCE` | Rejea ya uthibitisho | AI 7230–7239 |

### GS1 UIC

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `UIC_CODE` | Msimbo wa UIC | AI 7040 |
| `UIC_EXTENSION_1` | Nyongeza 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Faharasa ya mwagizaji | AI 7040 |

### Mfuatano wa kuzaliwa kwa mtoto

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Nafasi ya kuzaliwa | AI 7258 |
| `BIRTH_TOTAL` | Jumla ya kuzaliwa | AI 7258 |
| `BIRTH_SEQUENCE` | Mfululizo wa kuzaliwa | AI 7258 |

### Nambari ya Muundo ya Kimataifa (GMN)

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Rejea ya modeli | AI 8013 |
| `GMN_CHECK_PAIR` | Jozi ya ukaguzi | AI 8013 |

### HIDRI

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Rejea ya kifaa | AI 8014 |
| `HIDRI_CHECK_PAIR` | Jozi ya ukaguzi | AI 8014 |

### CPID

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Rejea ya sehemu na kipande | AI 8010–8011 |

### Thamani za desimali na za vipimo

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Thamani ya desimali | AI za tarakimu zenye nafasi za desimali zinazokusudiwa (31xx–36xx) |
| `DECIMAL_AMOUNT` | Kiasi | AI za bei (390n–395n) |
| `DECIMAL_PERCENTAGE` | Asilimia | AI 394n |
| `DECIMAL_PLACES` | Nafasi za desimali | Pamoja na `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Umbizo la asilimia | AI 394n |
| `ISO_UNIT_CODE` | Msimbo wa kizio cha ISO | AI za vipimo |
| `ISO_UNIT_NAME` | Jina la kizio cha ISO | AI za vipimo |
| `MONETARY_AMOUNT` | Kiasi cha fedha | AI za bei |
| `MONETARY_AMOUNT_DISPLAY` | Kiasi cha fedha (kilichoumbizwa) | AI za bei |

### Viwianishi vya kijiografia

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `LATITUDE` | Latitudo | AI 4309 |
| `LONGITUDE` | Longitudo | AI 4309 |
| `GEO_COORDINATES` | Viwianishi vya kijiografia | AI 4309 |
| `LATITUDE_DMS` | Latitudo (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitudo (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Viwianishi vya kijiografia (DMS) | AI 4309 |

### Njia ya uzalishaji

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Msimbo wa njia ya uzalishaji | AI 7010 |
| `PRODUCTION_METHOD` | Njia ya uzalishaji | AI 7010 |

### Aina ya media ya AIDC

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Msimbo wa aina ya media ya AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Aina ya media ya AIDC | AI 7241 |

### Kipande cha jumla

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Nambari ya kipande | AI 8006 |
| `PIECE_TOTAL` | Jumla ya vipande | AI 8006 |
| `PIECE_OF_TOTAL` | Kipande cha jumla | AI 8006 |

### Migawanyo ya vijenzi

Funguo zinazotolewa na migawanyo tangazi ya vijenzi katika `content/ai-content.json` badala ya
na kiboreshaji cha Java — hufunua sehemu zilizopewa majina za thamani mchanganyiko ya AI.
Tofauti na kila ufunguo mwingine katika kiambatisho hiki, hizi **hazina konstanti katika
`GS1Constants_Enricher`**: linganisha mfuatano wenyewe, au soma aina kutoka
`GS1AIInterpretation.getType()`.

| Ufunguo wa aina | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Tarakimu ya ukaguzi | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Nambari ya mfululizo | AI 253, 255, 8003 |
| `POSTAL_CODE` | Msimbo wa posta | AI 421 |
| `PROCESSOR_ID` | Kitambulisho cha kichakataji | AI 7030–7039 |

Kumbuka kuwa `CHECK_DIGIT` hapa ni ufunguo wa jumla wa mgawanyo wa kijenzi, tofauti na funguo
mahususi za viboreshaji `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` na
`EID_CHECK_DIGIT` zilizoorodheshwa hapo juu.

### Mengineyo

| Konstanti ya ufunguo | Lebo ya kuonyesha | Huzalishwa na |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Thamani | AI za boolean / bendera (4321–4323) |
| `DECODED_TEXT` | Maandishi yaliyofumbuliwa | AI za maandishi huru |
