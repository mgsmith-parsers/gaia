# GAIA (GS1 Application Identifiers Analyser) — Jagora ga Mai Haɓakawa

## Jerin Abubuwan Ciki

1. [Bayani Gaba Ɗaya](#bayani-gaba-ɗaya)
2. [Game da GS1 da General Specifications](#game-da-gs1-da-general-specifications)
3. [GS1 Application Identifier](#gs1-application-identifier)
4. [Gabatarwa Mai Sauri](#gabatarwa-mai-sauri)
5. [Layin Tantancewa](#layin-tantancewa)
   - [Matakin farko — Input Modifier](#matakin-farko--input-modifier)
   - [Mataki na 0 — Correlation ID](#mataki-na-0--correlation-id)
   - [Mataki na 1 — Tura Shigarwa](#mataki-na-1--tura-shigarwa)
   - [Mataki na 2 — Nahawu](#mataki-na-2--nahawu)
   - [Mataki na 3 — Abin Ciki](#mataki-na-3--abin-ciki)
   - [Mataki na 4 — Fassara](#mataki-na-4--fassara)
6. [Saitin Tantancewa (`ParseConfig`)](#saitin-tantancewa-parseconfig)
   - [Zaɓuɓɓuka](#zaɓuɓɓuka)
   - [Saƙonni da lakabi da aka mayar wa yare](#saƙonni-da-lakabi-da-aka-mayar-wa-yare)
   - [Tsara kwanan wata](#tsara-kwanan-wata)
7. [Input Modifier](#input-modifier)
   - [Modifier na ciki](#modifier-na-ciki)
   - [Rubuta modifier](#rubuta-modifier)
   - [Yin rijistar modifier](#yin-rijistar-modifier)
   - [Duba abin da modifier ya yi](#duba-abin-da-modifier-ya-yi)
   - [Magance kasawar modifier](#magance-kasawar-modifier)
8. [Yanayin Tantancewa](#yanayin-tantancewa)
   - [Yanayin DATA_CARRIER](#yanayin-data_carrier)
   - [Yanayin SYNTAX](#yanayin-syntax)
   - [Yanayin CONTENT](#yanayin-content)
   - [Yanayin INTERPRETATION (tsoho)](#yanayin-interpretation-tsoho)
9. [Correlation ID](#correlation-id)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Yin Aiki da Sakamako](#yin-aiki-da-sakamako)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry da DataCarrierType](#datacarrierentry-da-datacarriertype)
12. [Maganar Kuskure](#maganar-kuskure)
13. [Aminci ga Thread](#aminci-ga-thread)
14. [Ƙarin Bayani A — Constant ɗin String na AI](#ƙarin-bayani-a--constant-ɗin-string-na-ai)
    - [Ganewa da serialisation](#ganewa-da-serialisation)
    - [Kwanan wata da lokaci](#kwanan-wata-da-lokaci)
    - [Adadi da awo — awo mai canzawa (metric)](#adadi-da-awo--awo-mai-canzawa-metric)
    - [Adadi da awo — awo mai canzawa (imperial / US)](#adadi-da-awo--awo-mai-canzawa-imperial--us)
    - [Farashi da adadin kuɗi](#farashi-da-adadin-kuɗi)
    - [Wuri da jigilar kaya](#wuri-da-jigilar-kaya)
    - [Halayen kaya da iya bibiya](#halayen-kaya-da-iya-bibiya)
    - [Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN)](#lambar-biyan-kuɗin-kiwon-lafiya-ta-ƙasa-nhrn)
    - [Kiwon lafiya, GMN, HIDRI, CPID, bayanan mutum](#kiwon-lafiya-gmn-hidri-cpid-bayanan-mutum)
    - [Amfanin ciki / na kamfani](#amfanin-ciki--na-kamfani)
15. [Ƙarin Bayani B — Constant ɗin Maɓallin Fassara](#ƙarin-bayani-b--constant-ɗin-maɓallin-fassara)
    - [Kwanan wata da lokaci](#kwanan-wata-da-lokaci)
    - [Ranar girbi](#ranar-girbi)
    - [Prefix na Kamfanin GS1](#prefix-na-kamfanin-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Ƙasa (ISO 3166)](#ƙasa-iso-3166)
    - [Kuɗi (ISO 4217)](#kuɗi-iso-4217)
    - [Zafi](#zafi)
    - [Jinsi (ISO 5218)](#jinsi-iso-5218)
    - [Halittun ruwa (FAO)](#halittun-ruwa-fao)
    - [Lambar Kayan NATO (NSN)](#lambar-kayan-nato-nsn)
    - [Kayan da aka naɗe](#kayan-da-aka-naɗe)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Alamun ganewar SIM (EID / ICCID)](#alamun-ganewar-sim-eid--iccid)
    - [Maganar takardar shaida](#maganar-takardar-shaida)
    - [GS1 UIC](#gs1-uic)
    - [Jerin haihuwar jariri](#jerin-haihuwar-jariri)
    - [Lambar Samfurin Duniya (GMN)](#lambar-samfurin-duniya-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Ƙimomin ma'ushi da na awo](#ƙimomin-maushi-da-na-awo)
    - [Wurin da yake a doron ƙasa](#wurin-da-yake-a-doron-ƙasa)
    - [Hanyar samarwa](#hanyar-samarwa)
    - [Nau'in kafofin AIDC](#nauin-kafofin-aidc)
    - [Guda daga cikin jimla](#guda-daga-cikin-jimla)
    - [Rarrabuwar ɓangarori](#rarrabuwar-ɓangarori)
    - [Sauran abubuwa](#sauran-abubuwa)

---

## Bayani Gaba Ɗaya

`GaiaParser` shi ne ƙofar shiga don tantance element string ɗin GS1 Application Identifier (AI). Yana karɓar ɗanyen fitarwar na'urar daukar hoto a kowane ɗaya daga cikin waɗannan surori, kuma yana mayar da `ParseResult` wanda aka tsara, mai ɗauke da dukan AI da aka gano, kurakuran tabbatarwa, da (idan an nema) fassarorin da mutum zai iya karantawa:

- Element string ɗin AI kawai: `0109506000134352`
- Element string mai prefix ɗin AIM Code ID: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- Kowane daga cikin waɗannan, wanda za a iya sa masa prefix ɗin correlation ID mai lamba 8: `12345678~0109506000134352`

**Aji ɗin ƙofar shiga:** `tools.pantheum.gaia.GaiaParser`

> **Sabo ne ga Gaia?** Fara da **[Gabatarwa Mai Sauri ta GaiaParser](GaiaParser-QuickStart-Hausa.md)** — dependency, tantancewa ta farko, da ƴan abubuwan da sukan tuntuɓe mutane, cikin kimanin minti goma. Wannan jagora kuwa ita ce cikakkiyar magana.

> Don aikin akasin haka — *gina* element string da Digital Link URI masu inganci daga nau'ikan AI da ƙima — duba **[GaiaBuilder — Jagora ga Mai Haɓakawa](GaiaBuilder-Hausa.md)**.

---

## Game da GS1 da General Specifications

**GS1** ƙungiya ce ta duniya mai zaman kanta wadda ba ta neman riba, wadda take ƙirƙira da kula da ƙa'idojin buɗaɗɗu don ganewar sarkar samar da kayayyaki da musayar bayanai. Ana amfani da ƙa'idojinta a kasuwanci, kiwon lafiya, sufuri, hidimar abinci, da sauran masana'antu da yawa, wanda ya ƙunshi komai daga barcode ɗin kaya a kan marufin mabukaci har zuwa bibiyar allurar magani ta lambar serial.

Tushen hukuma na duk abin da wannan parser ke aiwatarwa shi ne **GS1 General Specifications** — takarda guda ɗaya wadda take bayyana:

- Dukan lambobin Application Identifier (AI), sunayen bayanansu, tsare-tsarensu, da ƙa'idojin tabbatarwa
- Ƙa'idojin nahawu don gina da yin encoding ga element string ɗin AI
- Buƙatun symbology na barcode da rabon AIM Code ID
- Algorithm ɗin lambar bincike da harafin bincike
- Warware shekara mai lamba biyu (ƙa'idar taga mai zamewa)
- Ƙayyadaddun bayanai na Data Matrix, QR Code, GS1-128, GS1 DataBar, da sauran masu ɗaukar bayanai

Ana sabunta GS1 General Specifications kowace shekara. Ana samun bugu na yanzu da kayan taimako a:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA yana aiwatar da **Release 26.0 (An Amince, Janairu 2026)** na GS1 General Specifications.

Ƙa'ida abokiyar aiki mai suna **GS1 Digital Link: URI Syntax** ce ke tafiyar da GS1 Digital Link URI, kuma ita ce take bayyana maɓallan ganewa na farko, tsarin jerin key qualifier, da encoding ɗin data attribute waɗanda parser ke amfani da su ga shigarwar Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA yana aiwatar da **Release 1.7.0 (An Amince, Agusta 2026)** na ƙa'idar GS1 Digital Link: URI Syntax.

Duk maganganun sashe a cikin wannan takarda suna nufin GS1 General Specifications (misali, "Table 7-5", "section 7.12"), sai dai lambobin sashe na Digital Link (misali, "§4.9", "§4.12"), waɗanda suke nufin ƙa'idar GS1 Digital Link: URI Syntax.

---

## GS1 Application Identifier

**GS1 Application Identifier (AI)** gajeren prefix ne na lambobi — lamba biyu zuwa huɗu — wanda yake nuna ma'ana da tsarin bayanan da suke bi masa nan take. An bayyana AI a cikin GS1 General Specifications kuma suna ƙunshe da faffaɗan bayanan sarkar samar da kayayyaki: alamun ganewar kaya, kwanan wata, adadi, lambar lot, lambar serial, awo, URL, da sauransu.

### Tsarin wani element ɗin AI

Kowane element ɗin AI ya ƙunshi sassa biyu:

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

Lambar AI kullum lambobi ce. Ƙimar bayanai tana biyo bayanta nan take, kuma babu wani delimiter tsakanin lambar da ƙimar.

### Tsayayyen tsawo da tsawo mai canzawa

AI sun kasu kashi biyu:

| Nau'i | Hali | Misali |
|---|---|---|
| **Tsayayyen tsawo** | Adadin haruffa daidai ne, kuma kullum ana karanta shi gaba ɗaya | AI `01` (GTIN) — kullum lambobi 14 |
| **Tsawo mai canzawa** | Daga 1 zuwa iyakar adadin haruffa; yana ƙarewa da separator ɗin GS ko ƙarshen shigarwa | AI `10` (Kundi/Lot) — haruffa 1 zuwa 20 na lambobi da baƙaƙe |

Ma'aunin da ke ƙayyade ko AI tsayayye ne ko mai canzawa shi ne kawai bayaninsa a cikin ƙayyadaddun bayanan GS1 — parser ba ya yin zato.

### Element string masu AI da yawa

Ana iya haɗa AI da yawa cikin element string guda ɗaya. Ana iya haɗa AI masu tsayayyen tsawo kai tsaye domin parser kullum yana sane da adadin haruffan da zai karanta. Amma dole ne a ƙare AI masu tsawo mai canzawa da **harafin GS** (ASCII `0x1D`, wanda kuma aka fi sani da FNC1 a cikin symbology na barcode) duk lokacin da wani AI ke biyo bayansu, don parser ya san inda wata ƙima take ƙarewa da inda lambar AI ta gaba take farawa.

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

A cikin literal ɗin string na Java, rubuta harafin GS da Unicode escape ɗin `""`.

### AI da aka fi amfani da su

| AI | Sunan bayanai | Tsari | Misalin ƙima |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, yanki ɗaya na kuɗi) |
| `710` | NHRN PZN | X..20 | `12345678` |

> **Lamba ta huɗu** ta AI mai lamba 4 na awo ko farashi tana yin encoding ga adadin wuraren ma'ushi da ake nufi — `3103` shi ne nauyi na gaske a kilogiram da ma'ushi 3 (`001500` = 1.500 kg), yayin da `3102` zai karanta waɗannan lambobin a matsayin 15.00 kg. Ginshiƙin `Tsari` a sama yana nuna tsarin *bayanai*; cikakken `getFormatString()` na kowane AI kuwa ya haɗa da AI ɗin kansa (misali, `N4+N6` don `3103`).

### Fassarar da Mutum Zai Iya Karantawa (HRI)

Sura da aka saba amfani da ita ga mutane tana sanya kowace lambar AI cikin baka nan take gabanin ƙimarta, da fili guda tsakanin element:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Ba a nuna separator ɗin GS a cikin HRI. `GS1AIObject.toHriString()` yana samar da wannan tsari.

### Lambobin AI masu lamba huɗu

Wasu AI suna amfani da lambobi huɗu maimakon biyu. Lambobi biyu na farko suna nuna iyalin AI; lamba ta uku da/ko ta huɗu kuwa suna ɗauke da ƙarin ma'ana (kamar wurin ma'ushi da ake nufi ga AI na awo). Parser yana gano cikakkiyar lambar AI daga element string ta atomatik — masu kira kullum suna aiki da cikakkiyar lamba (misali, `"3102"`, ba `"31"` kawai ba).

---

## Gabatarwa Mai Sauri

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

> **Separator ɗin GS:** Dole ne a raba AI masu tsawo mai canzawa da ke cikin string mai AI da yawa ta hanyar harafin GS (ASCII `0x1D`). Yi amfani da `""` a cikin literal ɗin string na Java.

---

## Layin Tantancewa

### Matakin farko — Input Modifier

Idan `ParseConfig` yana ɗauke da wani **input modifier**, sukan gudu kafin komai — kafin cire correlation, kafin gano mai ɗaukar bayanai, kafin shiga layin GS1. Kowane modifier yana sake rubuta ɗanyen shigarwa don na gaba, kuma fitarwar sarkar ce kowane mataki na ƙasa ke aiki a kanta.

Ba a saita wani modifier a matsayin tsoho, don haka wannan matakin farko ba ya yin komai sai dai idan kai kanka ka zaɓa. Duba [Input Modifier](#input-modifier).

---

### Mataki na 0 — Correlation ID

Kafin kowane sarrafawar GS1, `GaiaParser` yana duba ko shigarwar tana farawa da **prefix ɗin correlation ID** na zaɓi: lambobin ASCII goma-goma guda 8 daidai, waɗanda tilde (`~`) ke biyo bayansu, misali `12345678~`.

Idan prefix ɗin yana nan, ana cire shi kuma a adana shi a matsayin `CorrelationInfo` a kan `ParseResult` da aka mayar. Dukan matakan da suke biyo baya suna aiki a kan payload ɗin da aka cire prefix. Idan babu prefix, shigarwar tana wucewa ba tare da canji ba.

Duba [Correlation ID](#correlation-id) don ƙarin bayani.

---

### Mataki na 1 — Tura Shigarwa

Bayan an cire correlation, `GaiaParser` yana duba ko shigarwar (wadda aka cire wa prefix) tana farawa da **AIM Code ID**: prefix mai haruffa uku a surar `]` + harafin ASCII + lambar ASCII (misali, `]C1` don GS1-128, `]d2` don GS1 DataMatrix, `]e0` don GS1 DataBar / GS1 Composite).

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

Idan mai ɗaukar bayanan ba ya iya ɗaukar GS1 AI (misali, barcode ɗin gidan waya), tantancewa tana tsayawa nan take da kuskuren `GE-D002`.

---

### Mataki na 2 — Nahawu

Yana gudu ba tare da wani sharaɗi ba. Ya ƙunshi ƙananan matakai biyu:

**2a. Rarraba token (`AISyntaxParser`)**
- Yana karanta tsawon lambar AI daga haruffa biyu na farko ta amfani da teburin prefix ɗin GS1 (GS1 General Specifications Table 7-5).
- AI masu tsayayyen tsawo suna karanta adadin byte daidai daga shigarwa.
- AI masu tsawo mai canzawa ana karanta su har zuwa harafin GS ko ƙarshen shigarwa.
- AI masu sassa da yawa ana yanka ƙimarsu zuwa sassa bisa kowane ɓangare.

**2b. Tabbatar da tsari (`SyntaxValidator`)**
- Yana duba AI da aka maimaita (`GE-S004`).
- Yana duba dogaron AI da ake buƙata, misali, AI `02` yana buƙatar AI `37` (`GE-S005`).
- Yana duba nau'ikan AI da aka haramta haɗuwarsu (`GE-S006`).

Kurakuran wannan mataki suna da matsayin `SYNTAX_ERROR` (tokeniser) ko `INTEGRITY_ERROR` (tsari). Idan akwai **kowane** kuskure — na tokeniser ko na tsari — layin yana tsayawa kuma ana tsallake matakan abin ciki da fassara.

---

### Mataki na 3 — Abin Ciki

Yana gudu ne kawai lokacin da Mataki na 2 bai samar da wani kuskure ba (ba na tokeniser ba, ba na tsari ba). Layin kowane element kamar haka (kowane mataki yana gudu ne kawai idan na baya bai samar da kuskure ba):

| Mataki | Mai tabbatarwa | Lambobin kuskure |
|---|---|---|
| Duba regex | `RegexValidator` | `GE-C001` |
| Charset da tsarin ɓangare | `ComponentValidator` | `GE-C005` + lambobin tsari bisa sharaɗi (`GE-C054`–`GE-C115`) |
| Lambar bincike / harafin bincike | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Tabbatarwar ma'ana ta musamman | `ContentValidatorRegistry` | lambobin abin ciki bisa sharaɗi (`GE-C116`–`GE-C170`) |

Kurakuran wannan mataki suna da matsayin `FORMAT_ERROR` ko `DATA_ERROR`, sai dai abu ɗaya: dubawar
prefix na kamfanin GS1 a kan AI masu maɓallin GS1 shawara ce kawai kuma tana da matsayin
`WARNING` (duba [Maganar Kuskure](#maganar-kuskure)), don haka prefix na kamfani da ba a gane
shi ba, shi kaɗai, ba ya sanya sakamakon ya zama mara inganci.

---

### Mataki na 4 — Fassara

Yana gudu ne kawai a yanayin `INTERPRETATION` kuma sai idan babu wani element ɗauke da kuskure daga kowane mataki na baya. `InterpretationEngine` yana wadatar da kowane element da metadata mai lakabi:

- Kwanan wata da aka sake tsara su a matsayin `dd/mm/yyyy`
- Rarraba lambar bincike ta GTIN da neman prefix ɗin kamfanin GS1
- Sunayen ƙasashe bisa ISO 3166
- Sunaye da alamomin kuɗi bisa ISO 4217
- Ƙimomin ma'ushi da aka yi decoding
- Sassan HRI (Fassarar da Mutum Zai Iya Karantawa)

Ana haɗa sakamakon a matsayin shigarwar `GS1AIInterpretation` a kan kowane `GS1AIObjectElement`.

---

## Saitin Tantancewa (`ParseConfig`)

`GaiaParser` yana bayyana ƙofofin shiga biyu kacal:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` yana gudu da **saitin tsoho**: yanayin `INTERPRETATION`, kwanan wata na little-endian (`dd/mm/yyyy`) da separator ɗin `/` da shekara mai lamba huɗu, da saƙonnin kuskure na **Turanci**. Don canza kowane ɗaya daga cikin waɗannan — har da yanayin tantancewa — gina `ParseConfig` da builder ɗinsa mai gudana sannan ka yi amfani da overload mai gardama biyu.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Dukan enum ɗin zaɓuɓɓuka suna zaune a cikin `GaiaConstants`.

### Zaɓuɓɓuka

| Hanyar builder | Enum (`GaiaConstants`) | Tsoho | Tasiri |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Zurfin layi — duba [Yanayin Tantancewa](#yanayin-tantancewa). |
| `language(...)`      | `Language`      | `ENGLISH`        | Yaren saƙonnin kuskure, lakabin fassara, **da kuma** bayanan AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Jerin sassan kwanan wata: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Harafi tsakanin sassan kwanan wata: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) ko `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) ko `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Yana tsallake dubawar tsari ta "requires" (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Yana tsallake dubawar tsari ta "excludes" (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / sunan aji | babu | Code ɗin da yake sake rubuta ɗanyen shigarwa kafin tantancewa — [modifier guda biyu na ciki](#modifier-na-ciki) da duk abin da ka rubuta. Duba [Input Modifier](#input-modifier). |

Zaɓuɓɓukan kwanan wata guda huɗu suna shafar kawai string ɗin kwanan wata da aka tsara waɗanda masu wadatar fassara ke samarwa (a yanayin `INTERPRETATION`); ba sa canza tabbatarwa. Ana iya barin ƙimomin builder — duk zaɓin da ba a saita ba (ko aka ba shi `null`) yana riƙe tsohonsa.

### Saƙonni da lakabi da aka mayar wa yare

`language(...)` yana zaɓar yare don **nau'i uku** na rubutun da mutum zai iya karantawa: saƙonnin kuskure, lakabin fassara (`getLabel()` na kowane `GS1AIInterpretation`), da bayanan AI (`getDescription()` na kowane `GS1AIObjectElement`).

`GaiaConstants.Language` yana bayyana **yare 35**, waɗanda suka ƙunshi yarukan da aka fi magana da su a duniya: Turanci, Faransanci, Sifaniyanci, Jamusanci, Italiyanci, Fotugisanci, Holanci, Yaren Poland, Rashanci, Yaren Ukraine, Yaren Czech, Yaren Sweden, Sinanci, Jafananci, Koreyanci, Larabci, Yaren Indonesiya, Hindi, Turkanci, Bengali, Urdu, Yaren Vietnam, Nigerian Pidgin, Larabcin Masar, Marathi, Telugu, Tamil, Cantonese, Wu Chinese, Tagalog, Farisanci, Hausa, Punjabi, Javanese, da Swahili.

Halin fassara (kamar yadda aka aika shi):
- **Lakabin fassara** — an fassara su ga dukan yaruka.
- **Saƙonnin kuskure** — an fassara su ga dukan yaruka.
- **Bayanan AI** — an fassara su ga dukan yaruka sai Turanci. Turanci ba katalogi ne na dabam ba: ana karanta shi kai tsaye daga filin `description` na shigarwar AI a cikin `gs1-application-identifiers.jsonld`, wanda kowane bayanin AI a ƙarshe yake komawa gare shi.

Nigerian Pidgin (`NIGERIAN_PIDGIN`), wanda yare ne na creole da ya dogara da Turanci, da gangan yake sake amfani da rubutun Turanci ga lakabin fassara da saƙonnin kuskure. Bayanan AI su ne banda a cikin wannan banda: an fassara su zuwa ainihin maganar Pidgin maimakon sake amfani da Turanci, domin an samar da katalogin bayanan AI daban da katalogin lakabi da saƙonni. Ya kamata masu yaren gida su duba fassarorin inji kafin a dogara da su a wurin samarwa.

Kowane saƙo, lakabi, ko bayani da ba ya cikin katalogin wani yare yana komawa Turanci. Yarukan da ake rubutawa daga dama zuwa hagu (Larabci, Urdu, Larabcin Masar, Farisanci) an adana su daidai a matsayin string; nuna su a matsayin RTL kuwa aikin ɓangaren nunawa ne.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Haka nan ake mayar da lakabin fassara wa yare (ƙimomin ba sa canzawa — lakabin kawai):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Haka nan ake mayar da bayanan AI wa yare (`getTitle()` kaɗai, misali `"GTIN"`, ba a mayar da shi wa yare ba):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Tsara kwanan wata

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

**Input modifier** code ne da yake sake rubuta ɗanyen string ɗin shigarwa kafin Gaia ya tantance shi. Modifier na nufin shigarwar da take zuwa a lalace tun farko — na'urar daukar hoto da take musanya separator ɗin GS da harafi mai bayyana, middleware da yake naɗe payload cikin prefix na mai sayarwa, tsarin host da yake mayar da komai babban harafi. Maimakon a tsaftace kowane string a kowane wurin kira (kuma a yin haka a yi ƙaramin kuskure a wuri ɗaya), a yi rijistar daidaitawa sau ɗaya a kan `ParseConfig` a bar wa parser aikin amfani da shi.

Modifier suna gudu a farkon farkon `GaiaParser.parse(...)` — kafin a cire correlation ID, kafin a gano AIM Code ID, kafin a shiga layin GS1. Komai da ke bayan haka yana ganin string ɗin da aka sake rubutawa kaɗai. Har da [modifier guda biyu na ciki](#modifier-na-ciki), **ba a saita komai a matsayin tsoho** — kai ne ka zaɓa ga kowane `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modifier na ciki

Core jar yana zuwa da modifier guda biyu a ƙarƙashin `tools.pantheum.gaia.modifier.custom`. Suna magance hanyoyi biyu da suka fi yawa da payload ɗin GS1 ke lalacewa — bakunan HRI da aka buga waɗanda aka ɗauka a matsayin bayanai, da filaye masu yawa — don haka babu buƙatar rubuta aji na musamman ga yanayin da aka fi samu:

| Aji | `getName()` | Abin da yake yi |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Yana cire bakunan HRI da ke kewaye da kowane AI (`(01)…(10)…`) kuma yana mayar da separator ɗin FNC1 da suke nunawa. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Yana cire kowane fili (`0x20`) daga element string ɗin AI. |

Dukansu biyu aiwatarwa ce kawai ta `ModifierInterface` ba tare da wani matsayi na musamman ba — ana yin rijistarsu, jerinsu, bayar da rahotonsu, da kasawarsu daidai kamar naka:

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

Dukansu biyu ba su da hali kuma suna da aminci ga thread, don haka kowa yana iya raba instance ɗaya; kuma don tura aiki bisa fayil ɗin saiti, ana iya kiran su biyu da cikakken sunan aji (duba [Yin rijistar modifier](#yin-rijistar-modifier)).

#### `ModifierRemoveAIBrackets`

Fassarar GS1 da mutum zai iya karantawa tana buga kowane AI cikin baka — `(01)09521234543213(10)ABC123` — al'adar bugu ce kawai. Kowace na'urar daukar hoto ko middleware da aka saita don aika HRI tana wuce da waɗannan bakunan kamar bayanai, kuma tokeniser ba shi da masaniyar abin da zai yi da su.

Cire bakunan rabin aiki ne kawai. A cikin HRI, bakan buɗewa `(` na AI na *gaba* shi kaɗai ne alamar ƙarshen ƙimar da ta gabata, don haka a surar baka, AI mai tsawo mai canzawa ba ya buƙatar FNC1. Ka cire bakunan kawai kuma wannan iyaka ta ɓace:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Shi ya sa wannan modifier yake **sake sanya FNC1 a kowace iyaka inda AI na baya yake mai tsawo mai canzawa**, yana mayar da ainihin rabuwar da bakunan suke yin encoding gare ta:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

Ana neman tsawon daga `AiDefinitionRegistry` na parser kansa, don haka yana magance kowane AI mai tsawo mai canzawa maimakon jerin da aka rubuta a code. Akwai yanayi uku da aka bar su da gangan: ƙimar da ta riga ta ƙare da FNC1 (tushen da yake aika al'adun biyu ba ya samun separator na biyu), lambar da ke cikin baka wadda ba wani AI da aka sani ba (AI da ba a san shi ba ba ya faɗin tsawonsa), da AI na ƙarshe a cikin string.

Wannan sake rubutu **idempotent** ne — ka gudanar da shi a kan fitarwar kansa kuma babu abin da zai canza — don haka yana da aminci ko a kan layin gauraye inda wasu shigarwa kawai suke da baka.

> **Iyaka.** `(` da `)` haruffan bayanai ne masu inganci na GS1 da kansu, kuma pattern ɗin da aka yi amfani da shi anan `\((\d{2,4})\)` ne kawai. Idan ƙima tana da lamba mai lamba biyu-zuwa-huɗu cikin baka bisa haɗari, za a cire bakunanta ita ma. Ka yi amfani da shi kawai ga tushen da yake amfani da al'adar bakunan HRI, ba ga wanda gaske yake da ƙimomi masu baka ba.

#### `ModifierRemoveSpaces`

Wasu na'urorin daukar hoto, middleware, da tsarin buga lakabi suna saka filaye masu yawa cikin element string da ba shi da wata matsala — don cika filin da yake da tsayayyen faɗi, don raba ƙungiyoyi masu sauƙin karantawa, ko don naɗe doguwar ƙima. Tokeniser yana ɗaukar kowane fili a matsayin bayanai, wanda hakan yake lalata ƙimar da yake ciki, kuma ga AI mai tsawo mai canzawa, komai da ke bayansa yana motsawa.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Ana cire ASCII `0x20` kaɗai. Sauran farin fili yana nan a wurinsa — misali, tab ba ya cikin saitin haruffan da GS1 ke iya yin encoding, don haka parser yana ba da rahotonsa a matsayin `GE-S008` maimakon ya haɗiye shi shiru.

> **Iyaka.** Fili (`0x20`) yana cikin saitin haruffan GS1 da ba sa canzawa, don haka kundi/lot ko lambar sashen abokin ciniki tana iya samun fili bisa inganci. Modifier ba zai iya bambanta fili mai yawa da na gaske ba; ka yi amfani da shi kawai ga tushen da ka sani ba ya amfani da filaye a ƙimomin AI ɗinsa.

#### Ana tsallake prefix, ba a sake rubuta su ba

Modifier suna gudu tun kafin parser ya cire komai, don haka ɗanyen shigarwa tana iya kasancewa har yanzu tana ɗauke da correlation ID, AIM Code ID, da alamar ECI. Modifier guda biyu na ciki suna amfani da ainihin dabarun parser na `CorrelationIdParser` da `DataCarrierParser` don gano farkon element string ɗin AI, suna fara sake rubutu daga nan, sannan suna sake haɗa sakamakon da prefix ɗin **da ba a taɓa ba**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Ana tsallake gaba ɗaya masu ɗaukar bayanai na EAN/UPC waɗanda ake cika ƙimarsu zuwa GTIN-14 (`isRequiresGtinPadding()`) — payload ɗinsu ɗanyar ƙimar barcode ce ta lambobi ba tare da wani tsarin AI ba, don haka ba baka ba fili ba za su iya samun ma'ana a can.

#### Jeri: filaye kafin baka

Idan kana amfani da su biyu, **ka fara yin rijistar `ModifierRemoveSpaces`**. Daidaita bakunan ya dogara da wuri: `( 01 )` mai filaye ba ya daidaita da `\((\d{2,4})\)`, don haka bakunan suna nan kuma separator ɗin da suke nunawa ba ya taɓa dawowa.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Rubuta modifier

Idan babu ɗaya daga cikin modifier na ciki guda biyu da ya dace, ka rubuta naka — hanya guda ɗaya kaɗai ce a cikin interface.

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

Idan sake rubutun ya dogara da saitin tantancewa, sai ka yi override ga surar mai gardama biyu:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Yarjejeniyar:

| Ƙa'ida | Bayani |
|---|---|
| Ba shi da hali kuma yana da aminci ga thread | Ana adana instance guda ɗaya na kowane aji kuma ana raba shi a kowace tantancewa. |
| Constructor na jama'a ba tare da gardama ba | Ana buƙatarsa ne kawai idan aka kira modifier ta sunan aji. |
| Ka magance `null` da shigarwa mara komai | Parser ba ya tace su kafin sarkar ta gudu. |
| Mayar da `null` yana nufin "babu canji" | Ana wuce da ƙimar da ta gabata. Ka mayar da `input` ba tare da canji ba idan modifier ɗin bai dace ba. |
| Gara a mayar ba tare da canji ba fiye da a jefa exception | Modifier da yake jefa exception yana dakatar da tantancewa — duba [Magance kasawa](#magance-kasawar-modifier). |
| `getName()` | Ka yi override ɗinsa don ka sarrafa sunan da ake bayar da rahoto a kan `ModifierInfo`; tsohonsa shi ne sunan aji mai sauƙi. |

### Yin rijistar modifier

Modifier suna gudu bisa jerin da ka ƙara su, kuma kowanne yana karɓar fitarwar wanda ya gabace shi. Ka yi rijistarsu ta instance, ta cikakken sunan aji, ko ta jerin kowane ɗaya daga cikinsu:

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

Ana kiran [modifier na ciki](#modifier-na-ciki) daidai kamar naka — **kullum da cikakken suna**. Ba su da gajeren suna ko neman alias; `ModifierRegistry` yana warware kowane modifier, ko na ciki ne ko a'a, ta cikakken sunan aji.

`ModifierRegistry` shi ne yake warware sunaye; yana ƙirƙirar instance ɗaya na kowane aji sau ɗaya ta constructor ɗinsa mara gardama, sannan yana adana shi ga kowane saiti na gaba da yake kiran wannan aji. Wannan warwarewar tana faruwa ne **lokacin ƙirƙirar saiti**, don haka suna da ba a samu ba, aji da bai aiwatar da `ModifierInterface` ba, ko wanda ba za a iya ƙirƙirar instance ɗinsa ba, yana jefa `IllegalArgumentException` nan take a wurin — ba shiru a lokacin tantancewa ba. Modifier da ba za a iya gina shi ta reflection ba (misali, saboda yana ɗauke da dependency da aka saka) ana iya yin rijistarsa tun da farko don ya ci gaba da kasancewa mai yiwuwa a kira shi ta suna:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Duba abin da modifier ya yi

Idan an saita modifier, `ParseResult.getPayload()` yana nuna shigarwar **da aka canza**. Ta asali tana nan a kan `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` yana bayar da rahoton `getName()` na kowane modifier, wanda tsohonsa shi ne sunan aji mai sauƙi amma modifier guda biyu na ciki suna yin override ɗinsa — don haka sarkar da waɗannan biyun suka yi tana nuna sunayen nunawa maimakon sunayen aji:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

Idan ba a saita wani modifier ba, `getModifierInfo()` yana mayar da `null`. Idan modifier sun gudu amma kowanne ya mayar da shigarwar ba tare da canji ba, bayanin yana nan amma `isModified()` yana `false` — modifier waɗanda gaske suka canza shigarwar kaɗai suke bayyana a `getAppliedModifiers()`.

### Magance kasawar modifier

Modifier da yake jefa exception yana dakatar da tantancewa. Ana naɗe wannan exception cikin `GaiaModifierException` wanda yake kiran sunan modifier ɗin da ya yi laifi, kuma sakamakon yana ɗauke da kuskuren ciki `GE-I001` mai wannan suna a saƙonsa; `getPayload()` yana nuna shigarwar da ba a canza ba. Da gangan tantancewa **ba ta ci gaba** da string ɗin da aka rubuta rabinsa — matakin daidaitawa da ya kasa shiru yana samar da sakamako da yake kama da inganci amma an tantance shi daga shigarwa mara kyau.

---

## Yanayin Tantancewa

An sanya wa kowane yanayi suna bisa [matakin layi](#layin-tantancewa) mafi zurfi da yake gudanarwa; duk da haka kowane mataki na baya yana gudu.

| Yanayi | Inda yake tsayawa | Abin da yake amsawa |
|---|---|---|
| `DATA_CARRIER` | Mataki na 1 (turawar shigarwa) | Wane symbology ne ya kawo wannan? |
| `SYNTAX` | Mataki na 2 (nahawu) | Lambobin AI da tsawonsu suna da kyakkyawan tsari? |
| `CONTENT` | Mataki na 3 (abin ciki) | Ƙimomin bayanan GS1 ne masu inganci? |
| `INTERPRETATION` | Mataki na 4 (fassara) | Menene ma'anar ƙimomin? |

### Yanayin DATA_CARRIER

Yana tsayawa bayan Mataki na 1 — yana tabbatar da AIM Code ID kuma yana gano symbology, amma ba ya shiga layin tantance AI. Yana da amfani wajen gano symbology da turawa ba tare da nauyin cikakkiyar tabbatarwa ba.

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

**Yaushe za a yi amfani da shi:** Idan aikace-aikacenka yana buƙatar sanin nau'in barcode kafin ya yanke shawarar yadda zai magance payload — misali, wajen turawar symbology na 1D da 2D zuwa masu magance daban. Don wannan turawar, ka yi amfani da [`DataCarrierType`](#datacarrierentry-da-datacarriertype) mai nau'i (`getDataCarrier().getDataCarrierType()`) maimakon daidaita string a kan `getName()`.

---

### Yanayin SYNTAX

Yana tsayawa bayan Mataki na 2. Yana da amfani wajen tacewa bisa tsari ba tare da tsadar tabbatar da abin ciki ba.

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

**Yaushe za a yi amfani da shi:** Idan kana son tabbatar da cewa lambobin AI da tsawon bayanai daidai ne kafin ka shiga cikakkiyar tabbatarwa, ko idan kana daukar hoto da yawa inda kurakuran abin ciki ba su cika faruwa ba.

---

### Yanayin CONTENT

Yana tsayawa bayan Mataki na 3.

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

> Yawancin AI ba za su iya tsayawa su kaɗai ba: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY)
> da `21` (SERIAL) — kowanne *yana buƙatar* maɓallin ganewa kamar AI `01` a cikin element
> string ɗin guda; don haka ka cire GTIN daga misalin da ke sama kuma zai kasa a Mataki
> na 2 da `GE-S005` tun kafin ma a isa tabbatar da abin ciki. Don tantance sassa da gangan
> ba tare da AI abokan tafiyarsu ba, ka saita `skipRequiresCheck(true)` a kan `ParseConfig`.

**Yaushe za a yi amfani da shi:** Idan kana buƙatar sanin cewa ƙimar da aka ɗauka tana bin GS1 gaba ɗaya kafin ka yi amfani da ita a wani tsarin kasuwanci, amma ba ka son nauyin wadatar fassara.

---

### Yanayin INTERPRETATION (tsoho)

Yana gudanar da cikakken layi har zuwa Mataki na 4. Wannan shi ne tsoho idan aka kira `parse(String)` ba tare da gardamar yanayi ba. Yana wadatar da element ɗin da suka wuce tabbatar da abin ciki lafiya kaɗai.

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

**Misalin fitarwa:**
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

**Misalin adadin kuɗi (AI 3932 — farashi da lambar kuɗi ta ISO):**
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

**Yaushe za a yi amfani da shi:** Wajen gina ɓangaren nunawa, kayan duba lakabi, ko kowane UI da yake buƙatar rarrabuwar ƙimomin AI ta hanyar da mutum zai fahimta.

---

## Correlation ID

Wasu tsarin aiki suna sanya alamar ganewa ta correlation mai lamba 8 ta kansu a gaban ɗanyen shigarwar GS1 don a iya haɗa abubuwan da suka faru na daukar hoto da wani zama ko ma'amala. Tsarin shi ne:

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

`~` (tilde) shi ne separator. **Ba** ɓangaren abin cikin GS1 ba ne — ana cire shi kafin a fara kowace tantancewar GS1.

### Ƙa'idojin ganowa

Ana gano prefix ɗin idan shigarwar ta fara da lambobin ASCII goma-goma guda 8 daidai (`0`–`9`) waɗanda `~` ke biyo bayansu nan take. Idan harafi na 9 ba `~` ba ne, ko kuma idan wani daga cikin haruffa 8 na farko ba lamba ba ne, ana ɗaukar shigarwar a matsayin abin cikin GS1 kawai ba tare da prefix ɗin correlation ba.

### Samun correlation ID

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

### Haɗawa da AIM Code ID

Prefix ɗin correlation yana iya bayyana gabanin AIM Code ID. Parser yana magance wannan a fili:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Aji ɗin aiwatarwa:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

**GS1 Digital Link** yana yin encoding ga ƙima ɗaya ko fiye na AI kai tsaye a cikin tsarin URL ɗin HTTP(S), wanda hakan yake ba da alamun ganewa da za a iya warware su ta yanar gizo ga kayayyakin zahiri. GAIA yana aiwatar da *GS1 Digital Link Standard: URI Syntax* (release 1.7.0) don URI **da ba a matse ba**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` yana gane Digital Link URI ta atomatik — ana turawa kowace shigarwa da ta fara da `http://` ko `https://` zuwa `GS1DLParser`, wanda yake gudanar da matakan abin ciki da fassara guda ɗaya kamar layin element string.

### Tsarin URI da ayyukan AI

Kowane AI a cikin Digital Link URI yana da ɗaya daga cikin ayyuka uku, wanda ake samu a kan kowane `GS1AIObjectElement` ta hanyar `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Aiki | Wuri | Misali |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Nau'in `/ai/value` na farko a hanya (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Nau'ikan hanya masu biyowa, bisa tsarin maɓallin farko (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Query parameter masu maɓallan lambobi kaɗai (§4.10) | `?17=271231` |

Ƙa'idojin tsari da ake aiwatarwa (`DLPathRules`):
- Maɓallin ganewa na farko **guda ɗaya** daidai a cikin hanya; dole a yi encoding ga ƙarin maɓallai a matsayin query data attribute.
- Dole ne maɓallin farko ya karɓi key qualifier kuma su bayyana bisa tsarin da aka ƙayyade. Ana iya barin qualifier na zaɓi, amma waɗanda *suke nan* dole su bi tsarin da aka ƙayyade — duba [Tsarin qualifier](#tsarin-qualifier).
- Sassan hanya na musamman za su iya zuwa gaban maɓallin farko (misali, `/products/au/01/...`); ka same su ta hanyar `getDigitalLinkInfo().getCustomPathStem()`.
- Ana yin watsi da maɓallan query da ba lambobi ba (`linkType`, `context`, da extension parameter kamar `23P`); maɓallan lambobi kaɗai dole ne su zama AI masu inganci da aka yi wa alamar `validAsDataAttribute`.
- Ana yin decoding ga haruffan ƙima da aka yi wa percent-encoding; ba a yarda da AI `(03)` da `(8014)` ba.

Maɓallan farko da jerin qualifier da suke karɓa **suna fitowa ne daga bayanai** cikin bayanan AI — ta hanyar tutar `gs1DigitalLinkPrimaryKey` da halayen `gs1DigitalLinkQualifiers` — maimakon a rubuta su a code.

Kowace keta tsari, ko shigarwa da ba URL ba, tana samar da kuskuren tsari na Digital Link (`GE-L001`–`GE-L014`, lamba ɗaya ga kowane yanayi). Ana samun metadata ɗin URL da aka rarraba (`scheme`, `domain`, `path`, `customPathStem`, `query`, da `java.net.URL`) ta hanyar `getDigitalLinkInfo()` ko da akwai kurakuran tsari.

### Tsarin qualifier

Ga kowane maɓallin farko, `gs1DigitalLinkQualifiers` yana lissafa jeri **mai tsari** guda ɗaya ko fiye na qualifier. A cikin jeri, AI da aka naɗe cikin baka murabba'i yana da **zaɓi**, AI mara baka kuwa **wajibi** ne — kamar yadda alamar `[cpv-comp]` ta ABNF ta §4.9 take. Jerin maɓallin farko guda ɗaya madadin ne da suke ware juna.

GTIN (`01`), alal misali, yana bayyana jeri biyu:

| Hanya | Jeri | Ma'ana |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — kowanne na zaɓi ne, amma tsarin nan tsayayye ne |
| upui-path | `235` | TPX (wajibi); GTIN + TPX = UPUI |

Don haka `/01/09506000134352/10/LOT-ABC/21/SER` yana da inganci (LOT gabanin SER, an bar CPV), `/01/.../21/SER/10/LOT-ABC` kuwa ana **ƙin sa** (tsarin ya ɓaci), kuma `/01/09506000134352/235/2ABC456` shi ne upui-path. Dubawar tsarin daidaita subsequence ce da take kiyaye tsari, don haka ana iya tsallake AI na zaɓi amma ba a taɓa canza tsarinsu ba.

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

**Aji ɗin aiwatarwa:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Yin Aiki da Sakamako

### ParseResult

Sakamako na sama da `GaiaParser.parse()` yake mayarwa.

| Hanya | Yana mayarwa | Bayani |
|---|---|---|
| `isValid()` | `boolean` | `true` idan babu kuskure a kowane matsayi. Gargaɗi ba ya shafar inganci. Kullum `true` idan `getAiObject()` yana `null`. |
| `getPayload()` | `String` | String ɗin shigarwa bayan an cire prefix ɗin correlation — kuma bayan wani [input modifier](#input-modifier) ya sake rubuta shi. |
| `getPayloadContent()` | `String` | Payload ɗin da aka cire wa AIM Code ID da prefix ɗin ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (mai ɗaukar bayanai da aka ƙi saboda ba na GS1 ba ne, misali mai ɗaukar bayanai `]A0` na Code 39), ko `UNABLE_TO_DETERMINE_CONTENT` (idan `aiObject` yana `null`, misali a yanayin `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | Zurfin layin da aka saita (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | Mataki mafi zurfi da tantancewar ta isa gaske — duba ƙasa. |
| `isParseComplete()` | `boolean` | `true` idan tantancewar ta isa zurfin da aka nema (`achieved == requested`). Ba ta da alaƙa da `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Dukan AI da aka gano. `null` a yanayin `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Dukan kurakuran da ba WARNING ba (matsayin object + dukan matsayin element). |
| `getWarnings()` | `List<GaiaError>` | Dukan shawarwarin WARNING (matsayin object + dukan matsayin element). |
| `hasWarnings()` | `boolean` | `true` idan an tayar da wata shawarar WARNING. |
| `getIssues()` | `List<GaiaError>` | Kurakurai da gargaɗi tare. |
| `hasDataCarrier()` | `boolean` | `true` idan an gane AIM Code ID. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadata ɗin symbology, ko `null` idan ba a gano mai ɗaukar bayanai ba. |
| `hasEci()` | `boolean` | `true` idan an cire alamar ECI daga payload. |
| `getEci()` | `EciEntry` | Metadata ɗin encoding na ECI, ko `null`. |
| `hasCorrelationId()` | `boolean` | `true` idan prefix ɗin correlation `DDDDDDDD~` yana nan a ainihin shigarwar. |
| `getCorrelationInfo()` | `CorrelationInfo` | Correlation ID da aka ciro, ko `null` idan babu. |
| `isInputModified()` | `boolean` | `true` idan wani [input modifier](#input-modifier) ya canza shigarwar. |
| `getModifierInfo()` | `ModifierInfo` | Abin da sarkar modifier ta yi — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` idan ba a saita modifier ba. |
| `getTiming()` | `ProcessingTiming` | Lokacin agogo na tantancewar — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` idan ba `GaiaParser` ya samar da shi ba. |
| `getVersion()` | `String` | Sigar library ɗin da ya samar da sakamakon. |

#### Yanayin da aka nema da wanda aka cimma

Layin yana gudanar da matakalar **SYNTAX → CONTENT → INTERPRETATION** kuma yana tsayawa da wuri idan an sami kurakurai, don haka yanayin da aka *cimma* gaske yana iya zama ƙasa da zurfin yanayin da aka *nema*. `getAchievedParseMode()` yana bayar da rahoton inda ya kai:

| Wanda aka nema | Abin da ke faruwa | Wanda aka cimma | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | kuskuren **nahawu / tsari** yana dakatar da tantancewa bayan rarraba token | `SYNTAX` | `false` |
| `INTERPRETATION` | kuskuren **abin ciki** (tsari/lambar bincike mara kyau) yana toshe wadatarwa | `CONTENT` | `false` |
| `CONTENT` | abin ciki kullum yana gudu har ƙarshe (ana rubuta kurakurai, ba sa kashewa) | `CONTENT` | `true` |
| kowanne (shigarwa mai tsafta) | layin ya isa zurfin da aka nema | = wanda aka nema | `true` |
| `DATA_CARRIER` | an tabbatar da mai ɗaukar bayanai; ba a tantance abin cikin AI ba | `DATA_CARRIER` | `true` |
| kowanne | an ƙi mai ɗaukar bayanai kafin tantance AI (misali, mai ɗaukar bayanai `]A0` da ba na GS1 ba) | `SYNTAX` | `false` |

`isParseComplete()` ba shi da alaƙa da `isValid()`: tantancewar `CONTENT` ta GTIN mai lambar bincike mara kyau **cikakkiya** ce (ta gudanar da matakin abin ciki) amma **mara inganci** (lambar bincike ta kasa). Ka yi amfani da `isParseComplete()` don ka tambaya "shin layin ya gudu har zurfin da na nema?" kuma `isValid()` don "shin bayanan suna da kyakkyawan tsari?".

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

Tarin element ɗin AI da aka gano.

| Hanya | Bayani |
|---|---|
| `getAis()` | Dukan instance ɗin `GS1AIObjectElement` bisa jerin shigarwa. |
| `get(String aiCode)` | Element na farko da ya dace da lambar AI da aka bayar, ko `null`. |
| `contains(String aiCode)` | `true` idan akwai AI mai wannan lamba. |
| `size()` | Adadin AI da aka gano. |
| `isValid()` | `true` idan babu kuskure a matsayin object kuma babu element mai kuskure. |
| `toHriString()` | String ɗin HRI, misali `(01)09506000134352 (17)261231`. |
| `toElementString()` | Ɗanyen element string — babu baka, akwai FNC1 bayan kowane element mai tsawo mai canzawa — misali `010950600013435210LOT-ABC<GS>17271231`. Yana mayar da `null` idan `isValid()` yana `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` idan `hasDigitalLink()` gaskiya ne, in ba haka ba `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` idan shigarwar GS1 Digital Link URI ce mai ɗauke da maɓallin ganewa na farko. URL mai kyakkyawan tsari amma babu maɓallin farko har yanzu yana bayyana `getDigitalLinkInfo()` amma yana mayar da `false` anan. |
| `getCanonicalDigitalLink()` | GS1 Digital Link URI na asali (§4.12) a kan `https://id.gs1.org` — maɓallin farko da qualifier a matsayin sassan hanya, data attribute a matsayin query parameter da aka jera bisa maɓallin AI — ko `null` idan babu maɓallin farko. |
| `getDigitalLinkInfo()` | Metadata ɗin rarraba URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), ko `null` idan ba Digital Link ba ne. |
| `getAllErrors()` | Matsayin object + dukan kurakuran element (ba WARNING ba). |
| `getAllWarnings()` | Matsayin object + dukan gargaɗin element. |
| `getAllIssues()` | Komai tare. |

---

### GS1AIObjectElement

Instance guda ɗaya na AI da aka gano.

| Hanya | Bayani |
|---|---|
| `getAi()` | Lambar AI, misali `"01"`, `"3102"`. |
| `getTitle()` | Sunan bayanan GS1, misali `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Cikakken bayanin GS1 na AI, **an mayar da shi wa yaren tantancewa** (misali, `"Global Trade Item Number (GTIN)"` da Turanci). Yana komawa rubutun Turanci daga bayanin AI idan ba a fassara shi ba. |
| `getFormatString()` | Bayanin tsari da ya ƙunshi AI *da kuma* bayanansa, misali `"N2+N14"` don AI `01`, `"N2+X..20"` don AI `10`, `"N4+N3+N..15"` don AI `3932`. |
| `getValue()` | Ɗanyar ƙimar bayanai da aka ciro daga element string. |
| `isFixedLength()` | `true` idan AI yana da tsayayyen tsawon bayanai. |
| `getPosition()` | Wurin harafi da yake farawa daga sifili a ainihin shigarwar. |
| `getGS1ComponentValues()` | Yankan ƙima bisa kowane ɓangare (ga AI masu sassa da yawa). |
| `getErrors()` | Kurakuran matsayin element da ba WARNING ba. |
| `getWarnings()` | Shawarwarin WARNING na matsayin element. |
| `getIssues()` | Kurakurai da gargaɗin matsayin element tare. |
| `hasErrors()` | `true` idan an haɗa wani kuskure da ba WARNING ba. |
| `hasWarnings()` | `true` idan an haɗa wata shawarar WARNING. |
| `getInterpretations()` | Shigarwar `GS1AIInterpretation` (ana cika su a yanayin INTERPRETATION). |
| `getInterpretation(String type)` | Fassara ta farko da ta dace da maɓallin nau'i na `GS1Constants_Enricher` da aka bayar, ko `null`. |
| `getDigitalLinkAIType()` | Aikin element a Digital Link (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), ko `null` ga shigarwar element string. |
| `hasDigitalLinkAIType()` | `true` idan an sanya aikin Digital Link. |

---

### GaiaError

Kuskuren tabbatarwa ko shawara wanda ba a canzawa.

| Hanya | Bayani |
|---|---|
| `getId()` | Alamar katalogi, misali `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, ko `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, ko `INTERNAL`. |
| `getCode()` | Gajeriyar lamba da inji zai iya karantawa. |
| `getAi()` | Lambar AI da ta haddasa kuskuren, ko `null` ga kurakuran matsayin object. |
| `getMessage()` | Saƙo da mutum zai iya karantawa wanda aka saka masa ƙimomi. |
| `getPosition()` | Wurin harafi da yake farawa daga sifili a ainihin shigarwar. |

---

### GS1AIInterpretation

Ɓangaren fassara guda ɗaya mai lakabi, wanda aka haɗa da `GS1AIObjectElement` a yanayin `INTERPRETATION`.

| Hanya | Bayani |
|---|---|
| `getType()` | Maɓallin nau'i da inji zai iya karantawa, misali `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Ba ya canzawa a kowane yare. |
| `getLabel()` | Lakabi da mutum zai iya karantawa, **an mayar da shi wa yaren tantancewa** (misali, `"Date"` / `"GS1 company prefix"` da Turanci). |
| `getValue()` | Ƙimar da aka ciro/wadatar, misali `"31/12/2026"`, `"9506000"`. Ba a mayar da ita wa yare ba. |

---

### DataCarrierEntry da DataCarrierType

Idan shigarwar tana ɗauke da AIM Code ID, `ParseResult.getDataCarrier()` yana mayar da `DataCarrierEntry` mai bayyana alamar da ta ɗauko bayanan. Wannan shigarwar ita ce takamaimiyar bayanan rajista ga AIM Code ID da ya dace; `DataCarrierType` kuwa shi ne enum na lokacin compile da take ciki.

#### DataCarrierEntry

Metadata na AIM Code ID guda ɗaya da aka gane (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Hanya | Bayani |
|---|---|
| `getAimCodeId()` | AIM Code ID da ya dace, misali `"]C1"`. |
| `getName()` | Sunan takamaimiyar alamar da mutum zai iya karantawa, misali `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Doguwar bayanin mai ɗaukar bayanai. |
| `getType()` | Nau'in tsari na mai ɗaukar bayanai a matsayin string (daidai da `getDataCarrierType().getCategory()`). |
| `getStandard()` | Ƙa'idar symbology, idan an rubuta ta. |
| `getDataCarrierType()` | `DataCarrierType` mai nau'i ga wannan shigarwar — wannan ne za a fi so a yi amfani da shi wajen turawa a code. |
| `isGs1Capable()` | `true` idan mai ɗaukar bayanan yana iya ɗaukar bayanan GS1 (element string ɗin AI da/ko Digital Link). |
| `isGs1AICapable()` | `true` idan mai ɗaukar bayanan yana iya ɗaukar element string ɗin GS1 AI. |
| `isGs1DigitalLinkCapable()` | `true` idan mai ɗaukar bayanan yana iya ɗaukar GS1 Digital Link URI. |
| `isEciCapable()` | `true` idan mai ɗaukar bayanan yana goyon bayan alamar ECI. |
| `isRequiresGtinPadding()` | `true` ga masu ɗaukar bayanai na EAN/UPC/ITF waɗanda ake cika ƙimarsu ta lambobi zuwa GTIN-14 kafin tantance AI. |

#### DataCarrierType

Enum na lokacin compile na nau'ikan mai ɗaukar bayanai, bisa maɓallin AIM Code ID da aka bayar a ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Harafin da ke bayan `]` (*harafin lamba*) shi ne yake zaɓar iyali; yawancin iyalai suna daidai da constant guda ɗaya da ya ƙunshi kowane modifier (`ITF` ya ƙunshi `]I0`–`]I2`; `EAN_UPC` ya ƙunshi EAN-13, UPC-A, UPC-E da EAN-8). Inda GS1 ya ajiye modifier don bayanan AI, wannan bambancin yana da constant nasa — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — daban da takwarorinsu na yau da kullum. Idan babu AIM Code ID, ko kuwa yana kiran mai ɗaukar bayanai da ba a sani ba, nau'in shi ne `UNKNOWN`.

| Hanya | Bayani |
|---|---|
| `getCategory()` | Faffaɗan `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, ko `OTHER`. |
| `getCodeChar()` | Harafin lambar AIM da yake nuna iyalin, misali `"Q"` don QR Code; `null` don `UNKNOWN`. |
| `getDisplayName()` | Sunan *nau'in* da mutum zai iya karantawa (yana iya zama mafi faɗi fiye da `DataCarrierEntry.getName()` — misali, `"EAN-13 / UPC-A / UPC-E / EAN-8"` da `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` ga constant waɗanda kullum suke nufin bayanan GS1 AI: bambance-bambance huɗu da GS1 ya ajiye (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) tare da `GS1_DATABAR`, wanda a asalinsa na GS1 ne domin kowane modifier ɗin `]e` GS1 DataBar ne. Ya fi `DataCarrierEntry.isGs1AICapable()` ƙunci — `QR_CODE` na yau da kullum ma yana iya ɗaukar bayanan GS1 AI. |
| `static forAimCodeId(String)` | Yana warware nau'i kai tsaye daga AIM Code ID (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); yana mayar da `UNKNOWN` ga ID da babu, mai mummunan tsari, ko wanda ba a gane ba. |

Turawa bisa nau'i maimakon bisa suna — misali, wajen raba alamomin linear (Code-128) da na 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` yana ƙunshe da alamomin matrix da dot kaɗai; masu ɗaukar bayanai na stacked-linear
(`PDF417`, `CODE_16K`, `CODABLOCK`, `CODE_49`) `STACKED_LINEAR` ne, ko da yake ana yawan
kiran su barcode na "2D". Don a ɗauki dukansu a matsayin ƙungiya ɗaya — misali, don a yanke
shawarar ko ana buƙatar imager maimakon na'urar daukar hoto ta laser — ka duba kowace
daga cikin nau'ikan biyu.

> Warware nau'i yana buƙatar AIM Code ID ya kasance a cikin abin da aka ɗauka; ba tare da shi ba, `getDataCarrier()` yana `null` kuma nau'in shi ne `UNKNOWN`. Ka saita na'urar daukar hoto ta aika prefix ɗin AIM Code ID.

---

## Maganar Kuskure

| Lamba | Matsayi | Mataki | Ma'ana |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Prefix ɗin AI da ba a sani ba — ba a iya gano tsawon bayanai |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Shigarwa ta yi gajarta don karanta cikakkiyar lambar AI |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Ƙimar da aka datse — haruffa sun yi ƙasa da abin da AI yake buƙata |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Application Identifier da aka maimaita a cikin element string |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Dogaron AI da ake buƙata ya ɓace |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Haɗin AI da aka haramta — AI biyu da ba za su iya haɗuwa ba |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Kasawar rarraba token da ba a zata ba |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Harafi da ke wajen saitin haruffan da GS1 ke iya yin encoding a cikin element string |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Separator ɗin FNC1 da ake buƙata ya ɓace bayan AI mai tsawo mai canzawa |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Bayanan da suka rage bayan iyakar dukan sassa |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separator ɗin FNC1 bayan AI mai tsayayyen tsawo a tsakiyar string |
| `GE-W002` | WARNING | SYNTAX | FNC1 da ya rage a ƙarshen element string (shawara kawai) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Ketawar tsarin Digital Link URI — lamba ɗaya ga kowane yanayi (URI mai mummunan tsari, scheme, host, tsarin qualifier, AI da aka haramta, babu maɓallin farko (`GE-L013`), maɓallan farko da yawa (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | Ƙima ba ta cika pattern ɗin regex na AI ba |
| `GE-C003` | DATA_ERROR | CONTENT | Tabbatar da lambar bincike ya kasa |
| `GE-C004` | DATA_ERROR | CONTENT | Tabbatar da nau'in harafin bincike ya kasa |
| `GE-C005` | FORMAT_ERROR | CONTENT | Ƙimar ɓangare tana ɗauke da harafi wajen saitin haruffan da aka yarda da su |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Kasawar tsarin ɓangare — lamba ɗaya ga kowane sharaɗin mai tabbatarwa (duba `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Kasawar tabbatarwar ma'ana ta musamman — lamba ɗaya ga kowane sharaɗin mai tabbatarwa (duba `content/validator/`). **Banda:** dubawar prefix na kamfanin GS1 guda 14 da aka lissafa a ƙasa suna da matsayin `WARNING`, kuma `GE-C168` (lambar ƙasa ta lambobi bisa ISO 3166-1 da ba a gane ba) tana da matsayin `FORMAT_ERROR`. |
| Dubawar prefix na kamfanin GS1 | WARNING | CONTENT | Maɓallin bai fara da prefix na kamfanin GS1 da aka sani ba, a kan AI masu maɓallin GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Shawara kawai — ba ta shafar inganci. |
| `GE-C169` | DATA_ERROR | CONTENT | Lambar bincike ta IMEI (Luhn) ta kasa a AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Lambar bincike ta EID (Luhn) ta kasa a AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | AIM Code ID da ba a gane ba |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | An gano mai ɗaukar bayanai amma ba ya goyon bayan element string ɗin GS1 AI ko Digital Link URI |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Kuskuren ciki da ba a zata ba |

> **Aibi da aka sani a nuna saƙo.** Template ɗin katalogi suna sanya ƙimomin da aka saka
> cikin ninkakkun alamun magana irin na MessageFormat (`''{value}''`), amma `ErrorRegistry`
> yana saka su da `String.replace` kawai, don haka ninkin yana rayuwa har zuwa
> `getMessage()` — a yanzu za ka ga `value ''09506000134351''` inda rubutun saƙon da aka
> kawo a wannan jagora yake nuna `value '09506000134351'`. Yana shafar kowane saƙo mai
> kawo ƙima a dukan katalogin yare 35. Kada ka tantance saƙonnin kuskure; ka daidaita
> a kan `getId()` / `getCode()`.

---

## Aminci ga Thread

`GaiaParser` yana da aminci ga thread da zarar an gina shi. Ana iya raba instance guda ɗaya tsakanin thread da dama kuma a yi amfani da shi lokaci guda. Hanyar da aka ba da shawara ita ce a gina instance ɗaya a farkon aikace-aikacen sannan a sake amfani da shi:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` ba a canzawa kuma yana da aminci a raba shi haka nan. Alhakin amincin thread guda ɗaya da library ba zai iya tabbatar maka da shi ba yana kan [input modifier](#input-modifier): ana adana instance guda ɗaya na kowane modifier kuma ana raba shi a kowace tantancewa da take gudana lokaci guda, don haka dole ne aiwatarwar ta kasance ba tare da hali ba.

---

## Ƙarin Bayani A — Constant ɗin String na AI

`GS1Constants_AICodes` (a cikin package ɗin `tools.pantheum.gaia.gs1.constants`) yana bayyana constant ɗin `String` ga kowane Application Identifier da GAIA yake ganewa. Ka yi amfani da waɗannan constant maimakon rubuta ɗanyen string ɗin lambar AI a code:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Kowane constant yana riƙe da surar string na lambar AI (misali, `AI_01_GTIN = "01"`).

### Ganewa da serialisation

| AI | Constant | Bayani |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Lambar Serial ta Akwatin Jigilar Kaya (SSCC). |
| `01` | `AI_01_GTIN` | Lambar Ƙasashen Duniya ta Kayan Kasuwanci (GTIN). |
| `02` | `AI_02_CONTENT` | Lambar Ƙasashen Duniya ta Kayan Kasuwanci (GTIN) na kayayyakin da ke ciki. |
| `03` | `AI_03_MTO_GTIN` | Ganewar kayan kasuwanci na Made-to-Order (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Lambar kundi ko lot. |
| `20` | `AI_20_VARIANT` | Nau'in samfur na cikin gida. |
| `21` | `AI_21_SERIAL` | Lambar serial. |
| `22` | `AI_22_CPV` | Nau'in samfur na mabukaci. |
| `235` | `AI_235_TPX` | Ƙarin Extension mai Lambar Serial da Ɓangare na Uku ke Sarrafawa na Lambar Ƙasashen Duniya ta Kayan Kasuwanci (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Ƙarin ma'anar samfur da masana'anta ta ba shi. |
| `241` | `AI_241_CUST_PART_NO` | Lambar kayan abokin ciniki. |
| `242` | `AI_242_MTO_VARIANT` | Lambar bambancin Made-to-Order. |
| `243` | `AI_243_PCN` | Lambar sashin marufi. |
| `250` | `AI_250_SECONDARY_SERIAL` | Lambar serial ta biyu. |
| `251` | `AI_251_REF_TO_SOURCE` | Bayanin ƙungiyar tushe. |
| `253` | `AI_253_GDTI` | Ma'anar Nau'in Takarda ta Ƙasashen Duniya (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Sashin ƙari na Lambar Wuri ta Ƙasashen Duniya (GLN). |
| `255` | `AI_255_GCN` | Lambar Kupon ta Ƙasashen Duniya (GCN). |
| `30` | `AI_30_VAR_COUNT` | Adadin kayayyaki mai canzawa (kayan kasuwanci mai canjin awo). |
| `37` | `AI_37_COUNT` | Adadin kayayyakin kasuwanci ko sassansu da ke cikin naúrar sufuri (logistic unit). |

### Kwanan wata da lokaci

| AI | Constant | Bayani |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Ranar samarwa (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Ranar ƙarshe (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Ranar marufi (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Ranar "mafi kyau kafin" (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Ranar sayarwa kafin (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Ranar ƙarewa (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Ranar da lokacin isarwa marar wuri (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Ranar da lokacin isarwa marar wucewa (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Ranar sakin kaya (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Ranar da lokacin ƙarewa (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Ranar farko ta daskarewa (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Ranar girbi (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Ranar gwaji kafin (YYMMDD[hhmm]). |

### Adadi da awo — awo mai canzawa (metric)

Iyalan AI masu lamba 4 `310n`–`369n` suna yin encoding ga adadi masu awo mai canzawa. Lamba ta uku tana zaɓar nau'in awo; **lamba ta huɗu** (`n`, 0–5) ita ce adadin wuraren ma'ushi da ake nufi — misali, `AI_3102_NET_WEIGHT_KG` yana nufin nauyi na gaske a kilogiram da wuraren ma'ushi 2.

| Iyali | Pattern ɗin constant (`n` = lambar wurin ma'ushi) | Bayani |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Nauyi na gaske, kilogiram (kayan kasuwanci mai canjin awo). |
| `311n` | `AI_311n_LENGTH_M` | Tsawo ko girman na farko, mita (kayan kasuwanci mai canjin awo). |
| `312n` | `AI_312n_WIDTH_M` | Fadi, diamita, ko girman na biyu, mita (kayan kasuwanci mai canjin awo). |
| `313n` | `AI_313n_HEIGHT_M` | Zurfi, kauri, tsayi, ko girman na uku, mita (kayan kasuwanci mai canjin awo). |
| `314n` | `AI_314n_AREA_M` | Fadin fili, mita murabba'i (kayan kasuwanci mai canjin awo). |
| `315n` | `AI_315n_NET_VOLUME_L` | Girma na gaske, lita (kayan kasuwanci mai canjin awo). |
| `316n` | `AI_316n_NET_VOLUME_M` | Girma na gaske, mita kubik (kayan kasuwanci mai canjin awo). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Nauyin kayan sufuri, kilogiram. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Tsawo ko girman na farko, mita. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Fadi, diamita, ko girman na biyu, mita. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Zurfi, kauri, tsayi, ko girman na uku, mita. |
| `334n` | `AI_334n_AREA_M_LOG` | Fadin fili, mita murabba'i. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Girman kayan sufuri, lita. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Girman kayan sufuri, mita kubik. |
| `337n` | `AI_337n_KG_PER_M` | Kilogiram a kowace mita murabba'i. |

### Adadi da awo — awo mai canzawa (imperial / US)

| Iyali | Pattern ɗin constant (`n` = lambar wurin ma'ushi) | Bayani |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Nauyi na gaske, fam (kayan kasuwanci mai canjin awo). |
| `321n` | `AI_321n_LENGTH_IN` | Tsawo ko girman na farko, inci (kayan kasuwanci mai canjin awo). |
| `322n` | `AI_322n_LENGTH_FT` | Tsawo ko girman na farko, ƙafa (kayan kasuwanci mai canjin awo). |
| `323n` | `AI_323n_LENGTH_YD` | Tsawo ko girman na farko, yadi (kayan kasuwanci mai canjin awo). |
| `324n` | `AI_324n_WIDTH_IN` | Fadi, diamita, ko girman na biyu, inci (kayan kasuwanci mai canjin awo). |
| `325n` | `AI_325n_WIDTH_FT` | Fadi, diamita, ko girman na biyu, ƙafa (kayan kasuwanci mai canjin awo). |
| `326n` | `AI_326n_WIDTH_YD` | Fadi, diamita, ko girman na biyu, yadi (kayan kasuwanci mai canjin awo). |
| `327n` | `AI_327n_HEIGHT_IN` | Zurfi, kauri, tsayi, ko girman na uku, inci (kayan kasuwanci mai canjin awo). |
| `328n` | `AI_328n_HEIGHT_FT` | Zurfi, kauri, tsayi, ko girman na uku, ƙafa (kayan kasuwanci mai canjin awo). |
| `329n` | `AI_329n_HEIGHT_YD` | Zurfi, kauri, tsayi, ko girman na uku, yadi (kayan kasuwanci mai canjin awo). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Nauyin kayan sufuri, fam. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Tsawo ko girman na farko, inci. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Tsawo ko girman na farko, ƙafa. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Tsawo ko girman na farko, yadi. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Fadi, diamita, ko girman na biyu, inci. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Fadi, diamita, ko girman na biyu, ƙafa. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Fadi, diamita, ko girman na biyu, yadi. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Zurfi, kauri, tsayi, ko girman na uku, inci. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Zurfi, kauri, tsayi, ko girman na uku, ƙafa. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Zurfi, kauri, tsayi, ko girman na uku, yadi. |
| `350n` | `AI_350n_AREA_IN` | Fadin fili, inci murabba'i (kayan kasuwanci mai canjin awo). |
| `351n` | `AI_351n_AREA_FT` | Fadin fili, ƙafa murabba'i (kayan kasuwanci mai canjin awo). |
| `352n` | `AI_352n_AREA_YD` | Fadin fili, yadi murabba'i (kayan kasuwanci mai canjin awo). |
| `353n` | `AI_353n_AREA_IN_LOG` | Fadin fili, inci murabba'i. |
| `354n` | `AI_354n_AREA_FT_LOG` | Fadin fili, ƙafa murabba'i. |
| `355n` | `AI_355n_AREA_YD_LOG` | Fadin fili, yadi murabba'i. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Nauyi na gaske, auns na troy (kayan kasuwanci mai canjin awo). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Nauyi na gaske (ko girma), auns (kayan kasuwanci mai canjin awo). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Girma na gaske, kwart (kayan kasuwanci mai canjin awo). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Girma na gaske, galan na Amurka (kayan kasuwanci mai canjin awo). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Girman kayan sufuri, kwart. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Girman kayan sufuri, galan na Amurka. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Girma na gaske, inci kubik (kayan kasuwanci mai canjin awo). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Girma na gaske, ƙafa kubik (kayan kasuwanci mai canjin awo). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Girma na gaske, yadi kubik (kayan kasuwanci mai canjin awo). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Girman kayan sufuri, inci kubik. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Girman kayan sufuri, ƙafa kubik. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Girman kayan sufuri, yadi kubik. |

### Farashi da adadin kuɗi

Lamba ta huɗu (`n`) tana yin encoding ga adadin wuraren ma'ushi da ake nufi. Zangonta da aka
yarda da shi ya bambanta ga kowane iyali — duba ginshiƙin `n`.

| Iyali | Pattern ɗin constant (`n` = lambar wurin ma'ushi) | `n` | Bayani |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Adadin da ya dace da za a biya ko ƙimar kupon, kuɗin gida. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Adadin da ya dace da za a biya tare da lambar kuɗin ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Adadin da ya dace da za a biya, yanki ɗaya na kuɗi (kayan kasuwanci mai canjin awo). |
| `393n` | `AI_393n_PRICE` | 0–9 | Adadin da ya dace da za a biya tare da lambar kuɗin ISO (kayan kasuwanci mai canjin awo). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Kason rangwame na kupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Adadin da za a biya kan kowace naúrar awo, yanki ɗaya na kuɗi (kayan kasuwanci mai canjin awo). |

### Wuri da jigilar kaya

| AI | Constant | Bayani |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Lambar odar sayayya ta abokin ciniki. |
| `401` | `AI_401_GINC` | Lambar Ƙasashen Duniya ta Ganewar Kaya (GINC). |
| `402` | `AI_402_GSIN` | Lambar Ƙasashen Duniya ta Ganewar Kaya da Aka Aika (GSIN). |
| `403` | `AI_403_ROUTE` | Lambar hanya (routing). |
| `410` | `AI_410_SHIP_TO_LOC` | Aikawa zuwa / Kai zuwa – Lambar Wuri ta Ƙasashen Duniya (GLN). |
| `411` | `AI_411_BILL_TO` | Lambar Wuri ta Ƙasashen Duniya (GLN) na wanda za a caje / aika invoice. |
| `412` | `AI_412_PURCHASE_FROM` | Lambar Wuri ta Ƙasashen Duniya (GLN) na wurin sayan kaya. |
| `413` | `AI_413_SHIP_FOR_LOC` | Aikawa domin / Kai domin - Turawa zuwa Lambar Wuri ta Ƙasashen Duniya (GLN). |
| `414` | `AI_414_LOC_NO` | Ganewar wurin zahiri - Lambar Wuri ta Ƙasashen Duniya (GLN). |
| `415` | `AI_415_PAY_TO` | Lambar Wuri ta Ƙasashen Duniya (GLN) na wanda ke bayar da invoice. |
| `416` | `AI_416_PROD_SERV_LOC` | Lambar Wuri ta Ƙasashen Duniya (GLN) na wurin samarwa ko sabis. |
| `417` | `AI_417_PARTY` | Lambar Wuri ta Ƙasashen Duniya (GLN) na ɓangare. |
| `420` | `AI_420_SHIP_TO_POST` | Aikawa zuwa / Kai zuwa – lambar gidan waya a cikin hukumar gidan waya guda ɗaya. |
| `421` | `AI_421_SHIP_TO_POST` | Aikawa zuwa / Kai zuwa – lambar gidan waya tare da lambar ƙasa ta ISO. |
| `422` | `AI_422_ORIGIN` | Ƙasar samuwar kayan kasuwanci. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Ƙasar sarrafa farko. |
| `424` | `AI_424_COUNTRY_PROCESS` | Ƙasar sarrafawa. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Ƙasar da aka warware kayan a ciki. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Ƙasar da ta ƙunshi cikakken jerin sarrafawa. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Yankin ƙasa na samuwar kaya. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Aikawa zuwa / Kai zuwa – sunan kamfani. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Aikawa zuwa / Kai zuwa – mai lamba. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Aikawa zuwa / Kai zuwa – layin adireshi 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Aikawa zuwa / Kai zuwa – layin adireshi 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Aikawa zuwa / Kai zuwa – kewayen gari. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Aikawa zuwa / Kai zuwa – unguwa. |
| `4306` | `AI_4306_SHIP_TO_REG` | Aikawa zuwa / Kai zuwa – yanki. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Aikawa zuwa / Kai zuwa – lambar ƙasa. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Aikawa zuwa / Kai zuwa – lambar waya. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Aikawa zuwa / Kai zuwa – Wurin GEO. |
| `4310` | `AI_4310_RTN_TO_COMP` | Mayar da zuwa – sunan kamfani. |
| `4311` | `AI_4311_RTN_TO_NAME` | Mayar da zuwa – mai lamba. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Mayar da zuwa – layin adireshi 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Mayar da zuwa – layin adireshi 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Mayar da zuwa – kewayen gari. |
| `4315` | `AI_4315_RTN_TO_LOC` | Mayar da zuwa – unguwa. |
| `4316` | `AI_4316_RTN_TO_REG` | Mayar da zuwa – yanki. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Mayar da zuwa – lambar ƙasa. |
| `4318` | `AI_4318_RTN_TO_POST` | Mayar da zuwa – lambar gidan waya. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Mayar da zuwa – lambar waya. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Bayanin lambar sabis. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Alamar kayan haɗari. |
| `4322` | `AI_4322_AUTH_LEAVE` | Izinin barin kaya ba tare da sa hannu ba (Authority to Leave). |
| `4323` | `AI_4323_SIG_REQUIRED` | Alamar buƙatar sa hannu. |
| `4330` | `AI_4330_MAX_TEMP_F` | Mafi girman zafi a Fahrenheit (an bayyana shi cikin kashi ɗari na digiri). |
| `4331` | `AI_4331_MAX_TEMP_C` | Mafi girman zafi a Celsius (an bayyana shi cikin kashi ɗari na digiri). |
| `4332` | `AI_4332_MIN_TEMP_F` | Mafi ƙarancin zafi a Fahrenheit (an bayyana shi cikin kashi ɗari na digiri). |
| `4333` | `AI_4333_MIN_TEMP_C` | Mafi ƙarancin zafi a Celsius (an bayyana shi cikin kashi ɗari na digiri). |

### Halayen kaya da iya bibiya

| AI | Constant | Bayani |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Lambar Kayayyakin NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Rarrabuwar UN/ECE na gawar dabba da yankakken nama. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Ƙarfin sinadari mai aiki. |
| `7005` | `AI_7005_CATCH_AREA` | Yankin kama kifi. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Nau'in kifi don manufofin kamun kifi. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Nau'in kayan aikin kamun kifi. |
| `7010` | `AI_7010_PROD_METHOD` | Hanyar samarwa. |
| `7020` | `AI_7020_REFURB_LOT` | Lambar lot na gyaran kaya. |
| `7021` | `AI_7021_FUNC_STAT` | Matsayin aiki. |
| `7022` | `AI_7022_REV_STAT` | Matsayin gyara. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Ma'anar Ƙasashen Duniya ta Kayan Dukiya Guda ɗaya (GIAI) na wani assembly. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Lambar mai sarrafawa tare da lambar ƙasa ta ISO mai lamba uku (wurare 10). |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC tare da Extension 1 da alamar mai shigo da kaya. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Nau'in naúrar kaya ta UN/CEFACT. |

### Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN)

| AI | Constant | Bayani |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Jamus PZN. |
| `711` | `AI_711_NHRN_CIP` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Faransa CIP. |
| `712` | `AI_712_NHRN_CN` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Spain CN. |
| `713` | `AI_713_NHRN_DRN` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Brazil DRN. |
| `714` | `AI_714_NHRN_AIM` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Amurka NDC. |
| `716` | `AI_716_NHRN_AIC` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Italiya AIC. |
| `717` | `AI_717_NHRN_SRN` | Lambar Biyan Kuɗin Kiwon Lafiya ta Ƙasa (NHRN) - Lambar Rijistar Tsafta ta Costa Rica. |

### Kiwon lafiya, GMN, HIDRI, CPID, bayanan mutum

| AI | Constant | Bayani |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Maganar Takardar Shaida (wurare 10). |
| `7240` | `AI_7240_PROTOCOL` | Lambar Protocol. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Nau'in kafofin watsa labarai na AIDC. |
| `7242` | `AI_7242_VCN` | Lambar Kula da Sigar (VCN). |
| `7250` | `AI_7250_DOB` | Ranar haihuwa (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Ranar da lokacin haihuwa (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Jinsi na halitta. |
| `7253` | `AI_7253_FAMILY_NAME` | Sunan iyali na mutum. |
| `7254` | `AI_7254_GIVEN_NAME` | Sunan farko na mutum. |
| `7255` | `AI_7255_SUFFIX` | Ƙarin sunan mutum (suffix). |
| `7256` | `AI_7256_FULL_NAME` | Cikakken sunan mutum. |
| `7257` | `AI_7257_PERSON_ADDR` | Adireshin mutum. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Jerin haihuwar jariri. |
| `7259` | `AI_7259_BABY` | Sunan iyalin jariri. |
| `8001` | `AI_8001_DIMENSIONS` | Kayayyakin roll (fadi, tsawo, diamitar tsakiya, alkibla, haɗe-haɗe). |
| `8002` | `AI_8002_CMT_NO` | Ma'anar wayar salula. |
| `8003` | `AI_8003_GRAI` | Ma'anar Ƙasashen Duniya ta Kayan Dukiya Mai Dawowa (GRAI). |
| `8004` | `AI_8004_GIAI` | Ma'anar Ƙasashen Duniya ta Kayan Dukiya Guda ɗaya (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Farashi kan kowace naúrar awo. |
| `8006` | `AI_8006_ITIP` | Ganewar sashi guda ɗaya na kayan kasuwanci (ITIP). |
| `8007` | `AI_8007_IBAN` | Lambar Ƙasashen Duniya ta Asusun Banki (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Ranar da lokacin samarwa (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Alamar Na'urar Firikwensin da Ake Karantawa ta Gani. |
| `8010` | `AI_8010_CPID` | Ma'anar Kayan Aiki/Sashi (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Lambar serial na Ma'anar Kayan Aiki/Sashi (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Sigar software. |
| `8013` | `AI_8013_GMN` | Lambar Ƙirar Ƙasashen Duniya (GMN). |
| `8014` | `AI_8014_MUDI` | Ma'anar Rijistar Na'urar Musamman Ƙwarai (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Lambar Ƙasashen Duniya ta Alaƙar Sabis (GSRN) don gano alaƙar da ke tsakanin ƙungiyar da ke bayar da sabis da mai bayar da sabis. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Lambar Ƙasashen Duniya ta Alaƙar Sabis (GSRN) don gano alaƙar da ke tsakanin ƙungiyar da ke bayar da sabis da mai karɓar sabis. |
| `8019` | `AI_8019_SRIN` | Lambar Misalin Alaƙar Sabis (SRIN). |
| `8020` | `AI_8020_REF_NO` | Lambar bayanin takardar biyan kuɗi. |
| `8026` | `AI_8026_ITIP_CONTENT` | Ganewar sassan kayan kasuwanci (ITIP) da ke cikin naúrar sufuri. |
| `8030` | `AI_8030_DIGSIG` | Sa Hannu na Dijital (DigSig). |
| `8040` | `AI_8040_IMEI` | Ma'anar Ƙasashen Duniya ta Kayan Wayar Salula (IMEI). |
| `8041` | `AI_8041_IMEI2` | Ma'anar Ƙasashen Duniya ta Kayan Wayar Salula 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Lambar SIM da aka saka a ciki (eSIM). |
| `8043` | `AI_8043_PSIM` | Lambar SIM ta zahiri. |
| `8110` | `AI_8110` | Ma'anar lambar kupon don amfani a Arewacin Amurka. |
| `8111` | `AI_8111_POINTS` | Maki na aminci na kupon. |
| `8112` | `AI_8112` | Ma'anar lambar kupon ta positive offer file don amfani a Arewacin Amurka. |
| `8200` | `AI_8200_PRODUCT_URL` | URL na Ƙarin Bayanin Marufi. |

### Amfanin ciki / na kamfani

| AI | Constant | Bayani |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Bayanin da ma'abota kasuwanci suka amince da shi tare. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Bayanin cikin kamfani (wurare 9). |

---

## Ƙarin Bayani B — Constant ɗin Maɓallin Fassara

Idan aka kira `GaiaParser.parse()` da `ParseMode.INTERPRETATION`, kowane `GS1AIObjectElement` yana iya ɗauke da jerin object ɗin `GS1AIInterpretation` da masu wadatarwa na fannoni daban-daban suka samar. Ka yi amfani da constant daga `GS1Constants_Enricher` (a cikin package ɗin `tools.pantheum.gaia.gs1.constants`) a matsayin maɓallai don neman takamaiman ƙimomin fassara:

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

Lakabin nunawa **ba** constant ba ne — suna zaune a cikin katalogin da aka mayar wa yare a ƙarƙashin `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, bisa maɓallin constant na nau'i. `GS1AIInterpretation.getLabel()` yana mayar da lakabi ga yaren tantancewa (duba [Saƙonni da lakabi da aka mayar wa yare](#saƙonni-da-lakabi-da-aka-mayar-wa-yare)), yana komawa Turanci idan katalogi bai da wannan maɓallin. Ginshiƙin "Lakabin nunawa" a ƙasa yana lissafa rubutun Hausa; maɓallan nau'i da kansu ba sa canzawa a kowane yare, don haka ka daidaita a kan maɓalli, kada ka taɓa yin haka a kan lakabi.

### Kwanan wata da lokaci

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `DATE_VALUE` | Kwanan wata | AI na kwanan wata (11–17, 7003, 7006, 7011, da sauransu) |
| `DATE_FORMAT` | Tsarin kwanan wata | AI na kwanan wata |
| `TIME_VALUE` | Lokaci | AI masu ɗauke da lokaci (7003, 7011, 8008, da sauransu) |
| `TIME_FORMAT` | Tsarin lokaci | AI masu ɗauke da lokaci |
| `DATETIME_VALUE` | Kwanan wata da lokaci | AI na kwanan wata da lokaci |
| `DATETIME_FORMAT` | Tsarin kwanan wata da lokaci | AI na kwanan wata da lokaci |

### Ranar girbi

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Kwanan farkon girbi | AI 7007 |
| `HARVEST_END_DATE` | Kwanan ƙarshen girbi | AI 7007 (ƙarshen zangon na zaɓi) |
| `HARVEST_DATE_RANGE` | Zangon kwanan girbi | AI 7007 |

### Prefix na Kamfanin GS1

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefix na kamfanin GS1 | AI na GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Lambar memba na GS1 | AI na GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Ƙungiyar memba na GS1 | AI na GTIN / GLN / SSCC |

### GTIN

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Nau'in GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Matakin marufi | AI 01 |
| `GTIN_CHECK_DIGIT` | Lambar bincike | AI 01, 02 |

### SSCC

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Lambar faɗaɗawa | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Bayanin jeri | AI 00 |
| `SSCC_CHECK_DIGIT` | Lambar bincike | AI 00 |

### Ƙasa (ISO 3166)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Lambar ƙasa (lamba) | AI na ƙasa ɗaya (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Lambar ƙasa (alpha-2) | AI na ƙasa na Alpha-2 |
| `COUNTRY_NAME` | Sunan ƙasa | AI na ƙasa ɗaya |
| `COUNTRY_LIST` | Ƙasashe | AI 423 — dukan sunaye a haɗe, misali `Australia, New Zealand` |

AI 423 (ƙasar sarrafawa ta farko) yana iya ɗaukar ƙasashe har biyar, don haka yana fitar da
**nau'i mai lamba guda ga kowace ƙasa** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — sannan taƙaitaccen `COUNTRY_LIST` guda ɗaya
ke biyo baya. Ka gina waɗannan maɓallan daga constant ɗin `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` tare da lamba mai farawa daga 1, ko kuma kawai ka zagaya
`getInterpretations()`; maɓallan `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` marasa ƙari **ba a**
fitar da su ga AI 423.

### Kuɗi (ISO 4217)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Lambar kuɗi | AI na adadi tare da kuɗi (391n, 393n) |
| `CURRENCY_ALPHA` | Lambar haruffa na kuɗi | AI na adadi tare da kuɗi |
| `CURRENCY_NAME` | Sunan kuɗi | AI na adadi tare da kuɗi |

### Zafi

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `TEMPERATURE` | Zafin jiki | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Sashin zafin jiki | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Zafin jiki (an tsara) | AI 4330–4333 |

### Jinsi (ISO 5218)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `SEX_CODE` | Lambar jinsi | AI 7252 |
| `SEX_DESCRIPTION` | Bayanin jinsi | AI 7252 |

### Halittun ruwa (FAO)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Lambar nau'i | AI 7008 |
| `SPECIES_SCIENTIFIC` | Sunan kimiyya | AI 7008 |
| `SPECIES_ENGLISH` | Sunan gama-gari | AI 7008 |
| `SPECIES_FAMILY` | Iyali | AI 7008 |
| `SPECIES_ORDER` | Tsari | AI 7008 |

### Lambar Kayan NATO (NSN)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `NSN_FSG` | Rukunin kayayyaki | AI 7001 |
| `NSN_FSG_NAME` | Sunan rukunin kayayyaki | AI 7001 |
| `NSN_FSCG` | Ajin samarwa | AI 7001 |
| `NSN_FSCG_NAME` | Sunan ajin kayayyaki | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Lambar ƙasa | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Ƙasa | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Lambar ƙasa ta ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Rukunin NCS | AI 7001 |
| `NSN_NIIN` | Lambar kayan ƙasa | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Kayan da aka naɗe

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Faɗin nadi (mm) | AI 8001 |
| `ROLL_LENGTH` | Tsawon nadi (m) | AI 8001 |
| `CORE_DIAMETER` | Diamita na tsakiya (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Lambar hanyar naɗi | AI 8001 |
| `WINDING_DIRECTION` | Hanyar naɗi | AI 8001 |
| `SPLICES` | Haɗe-haɗe | AI 8001 |

### IBAN

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Lambar ƙasa | AI 8007 |
| `IBAN_COUNTRY_NAME` | Ƙasa | AI 8007 |
| `IBAN_CHECK_DIGITS` | Lambobin bincike | AI 8007 |
| `IBAN_CHECK_VALID` | Bincike | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Lambar jerin | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Lambar bincike | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Hukumar bayarwa | AI 8040, 8041 |

Lambobi 15 sun rabu kamar haka: `[ TAC (8) ][ serial (6) ][ lambar bincike ta Luhn (1) ]`, kuma
RBI shi ne lambobi 2 na farko na TAC — don haka `IMEI_RBI` prefix ne na `IMEI_TAC`, ba wani
sashe na dabam ba. `IMEI_FORMATTED` yana nuna ƙungiyar nunawa ta GSMA da aka saba
`AA-BBBBBB-CCCCCC-D` (misali, `49-015420-323751-8`), wadda take yanke TAC a iyakar RBI;
ƙungiyar tsohuwa ta `6-2-6-1`, wadda take yankewa inda Final Assembly Code da aka daina yake
farawa, ba a fitar da ita.

`IMEI_RBI_NAME` yana warware RBI zuwa sunan hukumar da ta ba da shi ta hanyar `ImeiRbiData`,
kuma ana **ƙara shi a ƙarshe kuma sai idan lambar tana cikin wannan teburi**. Wannan teburi
ya ƙunshi ƙungiyoyi uku:

- **Waɗanda suke bayarwa a yanzu** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, tare da `99`
  Global Hexadecimal Administrator da `98` (an ajiye shi).
- **Zangon gwaji** — `00` da `02`–`09`, waɗanda suke nuna IMEI na gwaji maimakon ainihin bayarwa.
  Ka tambaya da `ImeiRbiData.isTestCode(code)`.
- **Waɗanda ba sa bayarwa kuma** — hukumomin tarihi kamar `49` (BZT/BAPT, Jamus), `44`
  (BABT, Ingila) ko `91` (MSAI, Indiya). Ka tambaya da `ImeiRbiData.isNoLongerAllocating(code)`.
  Na'urorin da ke ɗauke da waɗannan lambobin na yau da kullum ne kuma har yanzu suna aiki; bayarwa
  sabuwa kaɗai ta tsaya, don haka wannan bayani ne na rahoto, ba alamar inganci ba ko kaɗan.

Rashin `IMEI_RBI_NAME` yana nufin "wannan RBI ba ya cikin teburinmu", **ba** "IMEI mara inganci"
ba: an tattara teburin daga jerin RBI da aka buga maimakon daga GSMA kai tsaye, don haka yana iya
yin baya ga hukumomin da aka naɗa kwanan nan. Kada ka fitar da wani sakamakon tabbatarwa daga
rashinsa; RBI ba harafin bincike ba ne. Code ɗin da yake zagaya jerin fassara dole ya jure
rashinsa maimakon ya dogara da wuri.

### Alamun ganewar SIM (EID / ICCID)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Rukunin masana'antu | AI 8042 |
| `EID_BODY` | Jikin EID | AI 8042 |
| `EID_CHECK_DIGIT` | Lambar bincike | AI 8042 |
| `ICCID_BODY` | Jikin ICCID | AI 8043 |
| `ICCID_EXTENSION` | Faɗaɗawa | AI 8043 |

`SIM_MII` yana ɗauke da lambobi **biyu** na farko (`89`), nau'in da ITU-T E.118 ya bai wa sadarwa.
ISO/IEC 7812 da kansa yana bayyana MII a matsayin **lamba ta farko kaɗai**, don haka
`SIM_MII_NAME` yana warware nau'in daga wannan `8` na farko ta hanyar `Iso7812Data` — wanda yake
bayar da "Healthcare, telecommunications and other future industry assignments". Ga EID mai
kyakkyawan tsari, saboda haka, wannan ba ya canzawa; ana bayar da rahotonsa don a iya bibiyar
ƙa'idar, ba a matsayin abin bambantawa ba. `Iso7812Data.nameForCode(digit)` yana karɓar lamba
guda, `nameForIdentifier(prefix)` kuwa yana karɓar prefix mai tsawo kuma yana karanta lambarsa
ta farko.

`EidEnricher` (AI 8042) kaɗai yake fitar da `SIM_MII_NAME`. `IccidEnricher` (AI 8043) yana
bayyana `SIM_MII` ba tare da nau'in ba.

### Maganar takardar shaida

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Lambar jeri | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Lambar tsarin takardar shaida | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Tsarin takardar shaida | AI 7230–7239 |
| `CERT_REFERENCE` | Bayanin takardar shaida | AI 7230–7239 |

### GS1 UIC

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `UIC_CODE` | Lambar UIC | AI 7040 |
| `UIC_EXTENSION_1` | Faɗaɗawa 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Ma'aunin mai shigo da kaya | AI 7040 |

### Jerin haihuwar jariri

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Matsayin haihuwa | AI 7258 |
| `BIRTH_TOTAL` | Jimlar haihuwa | AI 7258 |
| `BIRTH_SEQUENCE` | Jerin haihuwa | AI 7258 |

### Lambar Samfurin Duniya (GMN)

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Bayanin samfuri | AI 8013 |
| `GMN_CHECK_PAIR` | Ma'auratan bincike | AI 8013 |

### HIDRI

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Bayanin na'ura | AI 8014 |
| `HIDRI_CHECK_PAIR` | Ma'auratan bincike | AI 8014 |

### CPID

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Bayanin kayan aiki da sashi | AI 8010–8011 |

### Ƙimomin ma'ushi da na awo

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Ƙimar ma'ushi | AI na lambobi masu wuraren ma'ushi da ake nufi (31xx–36xx) |
| `DECIMAL_AMOUNT` | Adadi | AI na farashi (390n–395n) |
| `DECIMAL_PERCENTAGE` | Kashi dari | AI 394n |
| `DECIMAL_PLACES` | Wuraren ma'ushi | Tare da `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Tsarin kashi dari | AI 394n |
| `ISO_UNIT_CODE` | Lambar sashin ISO | AI na awo |
| `ISO_UNIT_NAME` | Sunan sashin ISO | AI na awo |
| `MONETARY_AMOUNT` | Adadin kuɗi | AI na farashi |
| `MONETARY_AMOUNT_DISPLAY` | Adadin kuɗi (an tsara) | AI na farashi |

### Wurin da yake a doron ƙasa

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `LATITUDE` | Latitude | AI 4309 |
| `LONGITUDE` | Longitude | AI 4309 |
| `GEO_COORDINATES` | Masu daidaita labarin ƙasa | AI 4309 |
| `LATITUDE_DMS` | Latitude (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitude (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Masu daidaita labarin ƙasa (DMS) | AI 4309 |

### Hanyar samarwa

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Lambar hanyar samarwa | AI 7010 |
| `PRODUCTION_METHOD` | Hanyar samarwa | AI 7010 |

### Nau'in kafofin AIDC

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Lambar nau'in kafofin AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Nau'in kafofin AIDC | AI 7241 |

### Guda daga cikin jimla

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Lambar guda | AI 8006 |
| `PIECE_TOTAL` | Jimlar guda | AI 8006 |
| `PIECE_OF_TOTAL` | Guda daga jimla | AI 8006 |

### Rarrabuwar ɓangarori

Maɓallan da rarrabuwar ɓangarori na bayyanawa a cikin `content/ai-content.json` suke fitarwa
maimakon wani mai wadatarwa na Java — suna bayyana sassan da aka ba wa suna na wata ƙimar AI
haɗaɗɗiya. Ba kamar kowane maɓalli a wannan ƙarin bayani ba, **ba su da constant a cikin
`GS1Constants_Enricher`**: ka daidaita string ɗin kai tsaye, ko ka karanta nau'in daga
`GS1AIInterpretation.getType()`.

| Maɓallin nau'i | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Lambar bincike | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Lambar jerin | AI 253, 255, 8003 |
| `POSTAL_CODE` | Lambar gidan waya | AI 421 |
| `PROCESSOR_ID` | Mai gano na'ura mai sarrafawa | AI 7030–7039 |

Ka lura cewa `CHECK_DIGIT` anan shi ne maɓallin rarrabuwar ɓangare na gaba ɗaya, daban da
maɓallan `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` da `EID_CHECK_DIGIT` na
takamaiman masu wadatarwa da aka lissafa a sama.

### Sauran abubuwa

| Constant ɗin maɓalli | Lakabin nunawa | Wanda yake samar da shi |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Ƙima | AI na boolean / tuta (4321–4323) |
| `DECODED_TEXT` | Rubutu da aka warware | AI na rubutu maras tsari |
