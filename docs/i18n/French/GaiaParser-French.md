# GAIA (GS1 Application Identifiers Analyser) — Guide du développeur

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [À propos de GS1 et des spécifications générales](#à-propos-de-gs1-et-des-spécifications-générales)
3. [Identifiants de données GS1](#identifiants-de-données-gs1)
4. [Démarrage rapide](#démarrage-rapide)
5. [Chaîne de traitement de l'analyse](#chaîne-de-traitement-de-lanalyse)
   - [Étape préalable — modificateurs d'entrée](#étape-préalable--modificateurs-dentrée)
   - [Étape 0 — identifiant de corrélation](#étape-0--identifiant-de-corrélation)
   - [Étape 1 — aiguillage de l'entrée](#étape-1--aiguillage-de-lentrée)
   - [Étape 2 — syntaxe](#étape-2--syntaxe)
   - [Étape 3 — contenu](#étape-3--contenu)
   - [Étape 4 — interprétation](#étape-4--interprétation)
6. [Configuration de l'analyse (`ParseConfig`)](#configuration-de-lanalyse-parseconfig)
   - [Options](#options)
   - [Messages et étiquettes localisés](#messages-et-étiquettes-localisés)
   - [Formatage des dates](#formatage-des-dates)
7. [Modificateurs d'entrée](#modificateurs-dentrée)
   - [Modificateurs intégrés](#modificateurs-intégrés)
   - [Écrire un modificateur](#écrire-un-modificateur)
   - [Déclarer des modificateurs](#déclarer-des-modificateurs)
   - [Examiner l'action d'un modificateur](#examiner-laction-dun-modificateur)
   - [Gestion des échecs d'un modificateur](#gestion-des-échecs-dun-modificateur)
8. [Modes d'analyse](#modes-danalyse)
   - [Mode DATA_CARRIER](#mode-data_carrier)
   - [Mode SYNTAX](#mode-syntax)
   - [Mode CONTENT](#mode-content)
   - [Mode INTERPRETATION (par défaut)](#mode-interpretation-par-défaut)
9. [Identifiant de corrélation](#identifiant-de-corrélation)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Exploiter les résultats](#exploiter-les-résultats)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry et DataCarrierType](#datacarrierentry-et-datacarriertype)
12. [Référence des erreurs](#référence-des-erreurs)
13. [Sûreté vis-à-vis des threads](#sûreté-vis-à-vis-des-threads)
14. [Annexe A — constantes de chaînes d'AI](#annexe-a--constantes-de-chaînes-dai)
    - [Identification et sérialisation](#identification-et-sérialisation)
    - [Dates et heures](#dates-et-heures)
    - [Quantité et mesure — mesure variable (métrique)](#quantité-et-mesure--mesure-variable-métrique)
    - [Quantité et mesure — mesure variable (impérial / États-Unis)](#quantité-et-mesure--mesure-variable-impérial--états-unis)
    - [Prix et montants monétaires](#prix-et-montants-monétaires)
    - [Lieux et expédition](#lieux-et-expédition)
    - [Attributs produit et traçabilité](#attributs-produit-et-traçabilité)
    - [Numéros nationaux de remboursement de santé (NHRN)](#numéros-nationaux-de-remboursement-de-santé-nhrn)
    - [Santé, GMN, HIDRI, CPID, données personnelles](#santé-gmn-hidri-cpid-données-personnelles)
    - [Usage interne / entreprise](#usage-interne--entreprise)
15. [Annexe B — constantes de clés d'interprétation](#annexe-b--constantes-de-clés-dinterprétation)
    - [Date et heure](#date-et-heure)
    - [Date de récolte](#date-de-récolte)
    - [Préfixe d'entreprise GS1](#préfixe-dentreprise-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [Pays (ISO 3166)](#pays-iso-3166)
    - [Devise (ISO 4217)](#devise-iso-4217)
    - [Température](#température)
    - [Sexe (ISO 5218)](#sexe-iso-5218)
    - [Espèces aquatiques (FAO)](#espèces-aquatiques-fao)
    - [Numéro de nomenclature OTAN (NSN)](#numéro-de-nomenclature-otan-nsn)
    - [Produits en rouleau](#produits-en-rouleau)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identifiants de carte SIM (EID / ICCID)](#identifiants-de-carte-sim-eid--iccid)
    - [Référence de certification](#référence-de-certification)
    - [GS1 UIC](#gs1-uic)
    - [Rang de naissance du nouveau-né](#rang-de-naissance-du-nouveau-né)
    - [Numéro de modèle mondial (GMN)](#numéro-de-modèle-mondial-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Valeurs décimales et de mesure](#valeurs-décimales-et-de-mesure)
    - [Coordonnées géographiques](#coordonnées-géographiques)
    - [Méthode de production](#méthode-de-production)
    - [Type de support AIDC](#type-de-support-aidc)
    - [Pièce sur total](#pièce-sur-total)
    - [Découpages en composants](#découpages-en-composants)
    - [Divers](#divers)

---

## Vue d'ensemble

`GaiaParser` est le point d'entrée pour l'analyse des chaînes d'éléments à identifiants de données GS1 (AI, *Application Identifier*). Il accepte la sortie brute d'un lecteur sous l'une des formes suivantes et renvoie un `ParseResult` structuré contenant tous les AI résolus, les erreurs de validation et, en option, les interprétations lisibles par l'humain :

- Chaîne d'éléments AI simple : `0109506000134352`
- Chaîne d'éléments précédée d'un identifiant de symbologie AIM : `]C10109506000134352`
- URI GS1 Digital Link : `https://example.com/01/09506000134352`
- N'importe laquelle des formes ci-dessus, éventuellement précédée d'un identifiant de corrélation à 8 chiffres : `12345678~0109506000134352`

**Classe du point d'entrée :** `tools.pantheum.gaia.GaiaParser`

> **Vous découvrez Gaia ?** Commencez par le **[démarrage rapide de GaiaParser](GaiaParser-QuickStart-French.md)** — la dépendance, une première analyse et les quelques pièges classiques, en une dizaine de minutes. Le présent guide est la référence complète.

> Pour l'opération inverse — la *construction* de chaînes d'éléments et d'URI Digital Link bien formées à partir de couples AI/valeur — voir le **[GaiaBuilder — Guide du développeur](GaiaBuilder-French.md)**.

---

## À propos de GS1 et des spécifications générales

**GS1** est une organisation mondiale à but non lucratif qui élabore et maintient des normes ouvertes pour l'identification et l'échange de données dans les chaînes d'approvisionnement. Ses normes sont utilisées dans la distribution, la santé, la logistique, la restauration et de nombreux autres secteurs, du code-barres produit sur un emballage grand public au suivi sérialisé des doses pharmaceutiques.

La référence faisant autorité pour tout ce que cet analyseur met en œuvre est le document **GS1 General Specifications** — un document unique qui définit :

- Tous les codes d'identifiants de données (AI), leurs titres de données, leurs formats et leurs règles de validation
- Les règles de syntaxe pour construire et encoder les chaînes d'éléments AI
- Les exigences de symbologie des codes-barres et l'attribution des identifiants de symbologie AIM
- Les algorithmes de chiffre de contrôle et de caractère de contrôle
- La résolution des années à deux chiffres (règle de la fenêtre glissante)
- Les spécifications Data Matrix, QR Code, GS1-128, GS1 DataBar et autres supports de données

Les GS1 General Specifications sont mises à jour chaque année. L'édition en vigueur et les ressources associées sont disponibles à l'adresse :

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA met en œuvre la **version 26.0 (ratifiée en janvier 2026)** des GS1 General Specifications.

Les URI GS1 Digital Link sont régis par une norme complémentaire, **GS1 Digital Link: URI Syntax**, qui définit les clés d'identification primaires, l'ordre des qualificatifs de clé et l'encodage des attributs de données que l'analyseur applique aux entrées Digital Link :

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA met en œuvre la **version 1.7.0 (ratifiée en août 2026)** de la norme GS1 Digital Link: URI Syntax.

Dans tout ce document, les références de section renvoient aux GS1 General Specifications (par exemple « Table 7-5 », « section 7.12 »), à l'exception des numéros de section Digital Link (par exemple « §4.9 », « §4.12 »), qui renvoient à la norme GS1 Digital Link: URI Syntax.

---

## Identifiants de données GS1

Un **identifiant de données GS1 (AI, *Application Identifier*)** est un court préfixe numérique — de deux à quatre chiffres — qui identifie la signification et le format de la donnée qui le suit immédiatement. Les AI sont définis dans les GS1 General Specifications et couvrent un large éventail de données de chaîne d'approvisionnement : identifiants de produits, dates, quantités, numéros de lot, numéros de série, mesures, URL, et bien d'autres.

### Structure d'un élément AI

Chaque élément AI comporte deux parties :

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

Le code AI est toujours numérique. La valeur de donnée suit immédiatement, sans aucun délimiteur entre le code et la valeur.

### AI de longueur fixe et de longueur variable

Les AI se répartissent en deux catégories :

| Type | Comportement | Exemple |
|---|---|---|
| **Longueur fixe** | Nombre exact de caractères, toujours consommé intégralement | AI `01` (GTIN) — toujours 14 chiffres |
| **Longueur variable** | De 1 caractère jusqu'à un maximum ; terminé par un séparateur GS ou par la fin de l'entrée | AI `10` (lot) — de 1 à 20 caractères alphanumériques |

Le caractère fixe ou variable d'un AI découle uniquement de sa définition dans la spécification GS1 — l'analyseur ne devine jamais.

### Chaînes d'éléments multi-AI

Plusieurs AI peuvent être concaténés dans une seule chaîne d'éléments. Les AI de longueur fixe peuvent être concaténés directement, car l'analyseur sait toujours exactement combien de caractères consommer. Les AI de longueur variable doivent être terminés par le **caractère GS** (ASCII `0x1D`, également appelé FNC1 dans les symbologies de codes-barres) dès qu'un autre AI les suit, afin que l'analyseur sache où se termine une valeur et où commence le code AI suivant.

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

Dans les littéraux de chaîne Java, écrivez le caractère GS avec la séquence d'échappement Unicode `"\u001D"`.

### AI courants

| AI | Titre de données | Format | Exemple de valeur |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (AAMMJJ) | `261231` |
| `17` | USE BY or EXPIRY | N6 (AAMMJJ) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, zone monétaire unique) |
| `710` | NHRN PZN | X..20 | `12345678` |

> Le **quatrième chiffre** d'un AI de mesure ou de prix à 4 chiffres encode le nombre de décimales implicites — `3103` désigne un poids net en kg avec 3 décimales (`001500` = 1,500 kg), tandis que `3102` lirait les mêmes chiffres comme 15,00 kg. La colonne `Format` ci-dessus indique le format de la *donnée* ; le `getFormatString()` complet de chaque AI inclut l'AI lui-même (par exemple `N4+N6` pour `3103`).

### Interprétation lisible par l'humain (HRI)

La forme lisible conventionnelle place chaque code AI entre parenthèses, immédiatement avant sa valeur, avec un espace entre les éléments :

```
(01)09506000134352 (17)261231 (10)LOT-001
```

Le séparateur GS n'apparaît pas dans la HRI. `GS1AIObject.toHriString()` produit ce format.

### Codes AI à quatre chiffres

Certains AI utilisent quatre chiffres plutôt que deux. Les deux premiers chiffres identifient la famille d'AI ; le troisième et/ou le quatrième portent une sémantique supplémentaire (comme la position de la virgule implicite pour les AI de mesure). L'analyseur résout automatiquement le code AI complet à partir de la chaîne d'éléments — l'appelant travaille toujours avec le code complet (par exemple `"3102"`, et non simplement `"31"`).

---

## Démarrage rapide

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

> **Séparateur GS :** dans une chaîne multi-AI, les AI de longueur variable doivent être délimités par le caractère GS (ASCII `0x1D`). Utilisez `"\u001D"` dans les littéraux de chaîne Java.

---

## Chaîne de traitement de l'analyse

### Étape préalable — modificateurs d'entrée

Si le `ParseConfig` comporte des **modificateurs d'entrée**, ceux-ci s'exécutent avant tout le reste — avant le retrait de l'identifiant de corrélation, avant la détection du support de données, avant même l'entrée dans la chaîne GS1. Chaque modificateur réécrit l'entrée brute pour le suivant, et toutes les étapes ci-dessous opèrent sur la sortie de la chaîne.

Aucun modificateur n'est configuré par défaut : cette étape préalable ne fait donc rien tant que vous ne l'activez pas explicitement. Voir [Modificateurs d'entrée](#modificateurs-dentrée).

---

### Étape 0 — identifiant de corrélation

Avant tout traitement GS1, `GaiaParser` vérifie si l'entrée commence par un **préfixe d'identifiant de corrélation** optionnel : exactement 8 chiffres décimaux ASCII suivis d'un tilde (`~`), par exemple `12345678~`.

Si le préfixe est présent, il est retiré et conservé sous forme de `CorrelationInfo` dans le `ParseResult` renvoyé. Toutes les étapes suivantes opèrent sur la charge utile ainsi dépouillée. En l'absence de préfixe, l'entrée est transmise telle quelle.

Voir [Identifiant de corrélation](#identifiant-de-corrélation) pour les détails.

---

### Étape 1 — aiguillage de l'entrée

Après le retrait de la corrélation, `GaiaParser` vérifie si l'entrée (dépouillée) commence par un **identifiant de symbologie AIM** : un préfixe de trois caractères de la forme `]` + lettre ASCII + chiffre ASCII (par exemple `]C1` pour GS1-128, `]d2` pour GS1 DataMatrix, `]e0` pour GS1 DataBar / GS1 Composite).

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

Si le support de données n'est pas compatible avec les AI GS1 (par exemple un code-barres postal), l'analyse s'arrête immédiatement sur une erreur `GE-D002`.

---

### Étape 2 — syntaxe

S'exécute systématiquement. Elle se compose de deux sous-étapes :

**2a. Découpage en jetons (`AISyntaxParser`)**
- Lit la longueur du code AI à partir des deux premiers caractères, au moyen de la table de préfixes GS1 (GS1 General Specifications, table 7-5).
- Les AI de longueur fixe consomment un nombre d'octets exact dans l'entrée.
- Les AI de longueur variable sont lus jusqu'à un caractère GS ou jusqu'à la fin de l'entrée.
- Pour les AI multi-composants, le bloc de valeur est découpé en segments, un par composant.

**2b. Validation structurelle (`SyntaxValidator`)**
- Détecte les AI en double (`GE-S004`).
- Vérifie les dépendances d'AI requises, par exemple l'AI `02` qui exige l'AI `37` (`GE-S005`).
- Vérifie les associations d'AI interdites (`GE-S006`).

Les erreurs de cette étape ont le niveau `SYNTAX_ERROR` (découpage) ou `INTEGRITY_ERROR` (structure). Si **une seule** erreur est présente — de découpage ou de structure — la chaîne s'arrête et les étapes de contenu et d'interprétation sont ignorées.

---

### Étape 3 — contenu

Ne s'exécute que si l'étape 2 n'a produit aucune erreur (ni de découpage, ni de structure). Chaîne appliquée à chaque élément (chaque étape ne s'exécute que si la précédente n'a produit aucune erreur) :

| Étape | Validateur | Codes d'erreur |
|---|---|---|
| Vérification par expression régulière | `RegexValidator` | `GE-C001` |
| Jeu de caractères et format des composants | `ComponentValidator` | `GE-C005` + codes de format par condition (`GE-C054`–`GE-C115`) |
| Chiffre de contrôle / caractère de contrôle | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validation sémantique personnalisée | `ContentValidatorRegistry` | codes de contenu par condition (`GE-C116`–`GE-C170`) |

Les erreurs de cette étape ont le niveau `FORMAT_ERROR` ou `DATA_ERROR`, à une exception près : les
contrôles du préfixe d'entreprise GS1 sur les AI porteurs d'une clé GS1 sont indicatifs et portent le niveau `WARNING` (voir la
[Référence des erreurs](#référence-des-erreurs)) ; un préfixe d'entreprise non reconnu ne rend donc pas à lui seul
le résultat invalide.

---

### Étape 4 — interprétation

Ne s'exécute qu'en mode `INTERPRETATION`, et uniquement si aucun élément ne porte d'erreur issue d'une étape antérieure. Le moteur `InterpretationEngine` enrichit chaque élément de métadonnées étiquetées :

- Dates reformatées en `jj/mm/aaaa`
- Décomposition du chiffre de contrôle du GTIN et recherche du préfixe d'entreprise GS1
- Noms de pays ISO 3166
- Noms et symboles de devises ISO 4217
- Montants décimaux décodés
- Fragments de HRI (interprétation lisible par l'humain)

Les résultats sont attachés sous forme d'entrées `GS1AIInterpretation` sur chaque `GS1AIObjectElement`.

---

## Configuration de l'analyse (`ParseConfig`)

`GaiaParser` expose exactement deux points d'entrée :

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` s'exécute avec la **configuration par défaut** : mode `INTERPRETATION`, dates petit-boutistes (`jj/mm/aaaa`) avec un séparateur `/` et une année à quatre chiffres, et messages d'erreur en **anglais**. Pour modifier l'un de ces réglages — y compris le mode d'analyse — construisez un `ParseConfig` avec son constructeur fluide et utilisez la surcharge à deux arguments.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Les énumérations d'options se trouvent toutes dans `GaiaConstants`.

### Options

| Méthode du constructeur | Énumération (`GaiaConstants`) | Défaut | Effet |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Profondeur de la chaîne — voir [Modes d'analyse](#modes-danalyse). |
| `language(...)`      | `Language`      | `ENGLISH`        | Langue des messages d'erreur, des étiquettes d'interprétation **et** des descriptions d'AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Ordre des composants de date : `LITTLE` (`jj/mm/aaaa`), `MIDDLE` (`mm/jj/aaaa`), `BIG` (`aaaa/mm/jj`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Caractère séparant les composants de date : `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) ou `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) ou `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Ignore le contrôle structurel « requiert » (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Ignore le contrôle structurel « exclut » (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nom de classe | aucun | Code qui réécrit l'entrée brute avant l'analyse — deux [modificateurs intégrés](#modificateurs-intégrés) auxquels s'ajoute tout ce que vous écrivez. Voir [Modificateurs d'entrée](#modificateurs-dentrée). |

Les quatre options de date n'agissent que sur les chaînes de dates formatées produites par les enrichisseurs d'interprétation (en mode `INTERPRETATION`) ; elles ne modifient pas la validation. Les valeurs du constructeur peuvent être omises — toute option non renseignée (ou à laquelle on passe `null`) conserve sa valeur par défaut.

### Messages et étiquettes localisés

`language(...)` sélectionne la langue de **trois** catégories de texte lisible par l'humain : les messages d'erreur, les étiquettes d'interprétation (le `getLabel()` de chaque `GS1AIInterpretation`) et les descriptions d'AI (le `getDescription()` de chaque `GS1AIObjectElement`).

**35 langues** sont définies par `GaiaConstants.Language`, couvrant les langues les plus parlées au monde : anglais, français, espagnol, allemand, italien, portugais, néerlandais, polonais, russe, ukrainien, tchèque, suédois, chinois, japonais, coréen, arabe, indonésien, hindi, turc, bengali, ourdou, vietnamien, pidgin nigérian, arabe égyptien, marathi, télougou, tamoul, cantonais, wu, tagalog, persan, haoussa, pendjabi, javanais et swahili.

État des traductions (telles que livrées) :
- **Étiquettes d'interprétation** — traduites dans toutes les langues.
- **Messages d'erreur** — traduits dans toutes les langues.
- **Descriptions d'AI** — traduites dans toutes les langues sauf l'anglais. L'anglais ne constitue pas un catalogue distinct : il est lu directement dans le champ `description` de l'entrée de l'AI dans `gs1-application-identifiers.jsonld`, qui sert de recours ultime à toute description d'AI.

Le pidgin nigérian (`NIGERIAN_PIDGIN`), un créole à base anglaise, réutilise volontairement le texte anglais pour les étiquettes d'interprétation et les messages d'erreur. Les descriptions d'AI font exception à cette exception : elles sont traduites en pidgin authentique plutôt que de reprendre l'anglais, car les catalogues de descriptions d'AI ont été produits indépendamment des catalogues d'étiquettes et de messages. Les traductions automatiques doivent être relues par des locuteurs natifs avant d'être utilisées en production.

Tout message, étiquette ou description absent du catalogue d'une langue est remplacé par sa version anglaise. Les langues s'écrivant de droite à gauche (arabe, ourdou, arabe égyptien, persan) sont stockées correctement sous forme de chaînes ; leur rendu de droite à gauche relève de la couche d'affichage.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Les étiquettes d'interprétation se localisent de la même façon (les valeurs sont inchangées — seules les étiquettes le sont) :

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Les descriptions d'AI se localisent de la même façon (seul `getTitle()`, par exemple `"GTIN"`, n'est pas localisé) :

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formatage des dates

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Modificateurs d'entrée

Un **modificateur d'entrée** est du code qui réécrit la chaîne d'entrée brute avant que Gaia ne l'analyse. Les modificateurs existent pour les entrées qui arrivent déjà déformées — un lecteur qui remplace le séparateur GS par un caractère imprimable, un intergiciel qui enveloppe la charge utile dans un préfixe propriétaire, un système hôte qui met tout en majuscules. Plutôt que de prétraiter chaque chaîne à chaque appel (et de se tromper subtilement dans l'un d'eux), déclarez la normalisation une seule fois sur le `ParseConfig` et laissez l'analyseur l'appliquer.

Les modificateurs s'exécutent tout au début de `GaiaParser.parse(...)` — avant le retrait de l'identifiant de corrélation, avant la détection de l'identifiant de symbologie AIM, avant la chaîne GS1. Tout ce qui suit ne voit que la chaîne réécrite. **Rien n'est configuré par défaut**, pas même les deux [modificateurs intégrés](#modificateurs-intégrés) — vous les activez explicitement dans chaque `ParseConfig`.

**Interface :** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modificateurs intégrés

Deux modificateurs sont livrés dans le jar principal, dans `tools.pantheum.gaia.modifier.custom`. Ils couvrent les deux façons dont une charge utile GS1 arrive le plus souvent déformée — des parenthèses de HRI imprimées puis traitées comme des données, et des espaces parasites — de sorte que les cas courants ne nécessitent aucune classe personnalisée :

| Classe | `getName()` | Rôle |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Retire les parenthèses de HRI autour de chaque AI (`(01)…(10)…`) et rétablit le séparateur FNC1 qu'elles impliquaient. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Supprime toutes les espaces (`0x20`) de la chaîne d'éléments AI. |

Ce sont des implémentations ordinaires de `ModifierInterface`, sans statut particulier — elles sont déclarées, ordonnées, signalées et mises en échec exactement comme les vôtres :

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

Les deux sont sans état et compatibles avec les accès concurrents : une instance unique peut donc être partagée, et toutes deux sont désignables par leur nom de classe pleinement qualifié pour une configuration externalisée (voir [Déclarer des modificateurs](#déclarer-des-modificateurs)).

#### `ModifierRemoveAIBrackets`

L'interprétation lisible par l'humain de GS1 imprime chaque AI entre parenthèses — `(01)09521234543213(10)ABC123` — par pure convention typographique. Un lecteur ou un intergiciel configuré pour émettre la HRI transmet ces parenthèses comme des données, et le module de découpage ne sait absolument pas qu'en faire.

Retirer les parenthèses ne fait que la moitié du travail. En HRI, c'est la parenthèse ouvrante de l'AI *suivant* qui marque la fin de la valeur précédente : sous forme parenthésée, un AI de longueur variable n'a donc besoin d'aucun FNC1. Retirez naïvement les parenthèses et cette frontière disparaît :

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Le modificateur **réinsère donc un FNC1 à chaque frontière dont l'AI précédent est de longueur variable**, rétablissant exactement ce que les parenthèses encodaient :

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

La longueur est recherchée dans le propre `AiDefinitionRegistry` de l'analyseur : tous les AI de longueur variable sont donc traités, sans liste codée en dur. Trois cas sont délibérément laissés intacts : une valeur qui se termine déjà par un FNC1 (une source qui émet les deux conventions ne reçoit pas de second séparateur), un code entre parenthèses qui n'est pas un AI connu (un AI inconnu ne dit rien de sa propre longueur), et le dernier AI de la chaîne.

La réécriture est **idempotente** — la réappliquer à son propre résultat ne change rien — elle est donc sans danger sur un flux mixte où seules certaines entrées sont parenthésées.

> **Limite.** `(` et `)` sont eux-mêmes des caractères de données GS1 valides, et le motif se réduit à `\((\d{2,4})\)`. Une valeur contenant par hasard un nombre de deux à quatre chiffres entre parenthèses se verrait elle aussi dépouillée. N'appliquez ceci qu'à une source utilisant la convention des parenthèses de HRI, et non à des valeurs comportant de véritables parenthèses.

#### `ModifierRemoveSpaces`

Certains lecteurs, intergiciels et chaînes d'impression d'étiquettes insèrent des espaces parasites dans une chaîne d'éléments par ailleurs bien formée — pour remplir un champ de largeur fixe, séparer des groupes lisibles ou couper une valeur longue. Le module de découpage traite chacune d'elles comme une donnée, corrompant la valeur qui la contient et, pour un AI de longueur variable, décalant tout ce qui suit.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Seul l'ASCII `0x20` est supprimé. Les autres caractères d'espacement sont laissés en place — une tabulation, par exemple, n'appartient pas au jeu de caractères encodables GS1, et l'analyseur la signale donc par `GE-S008` au lieu de la faire disparaître silencieusement.

> **Limite.** L'espace (`0x20`) fait partie du jeu de caractères invariants GS1 : un numéro de lot ou une référence article client peut donc légitimement en contenir une. Le modificateur ne sait pas distinguer une espace parasite d'une espace authentique ; ne l'appliquez qu'à une source dont vous savez qu'elle n'utilise pas d'espaces à l'intérieur de ses valeurs d'AI.

#### Les préfixes sont ignorés, non réécrits

Les modificateurs s'exécutent avant que l'analyseur n'ait retiré quoi que ce soit : l'entrée brute peut donc encore porter un identifiant de corrélation, un identifiant de symbologie AIM et un indicateur ECI. Les deux modificateurs intégrés localisent le début de la chaîne d'éléments AI au moyen de la logique de `CorrelationIdParser` et de `DataCarrierParser` de l'analyseur lui-même, ne réécrivent qu'à partir de ce point, et raccordent le résultat au préfixe resté **intact** :

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Les supports EAN/UPC dont la valeur est complétée jusqu'au GTIN-14 (`isRequiresGtinPadding()`) sont entièrement ignorés : leur charge utile est une valeur de code-barres purement numérique, sans structure d'AI, où ni parenthèses ni espaces ne sauraient avoir de sens.

#### Ordre : les espaces avant les parenthèses

Lorsque les deux sont utilisés, **déclarez `ModifierRemoveSpaces` en premier**. La reconnaissance des parenthèses dépend de la position : un `( 01 )` espacé ne correspond pas à `\((\d{2,4})\)`, si bien que les parenthèses subsistent et que le séparateur qu'elles impliquaient n'est jamais rétabli.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Écrire un modificateur

Écrivez le vôtre lorsqu'aucun des modificateurs intégrés ne convient — l'interface se réduit à une méthode.

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

Redéfinissez plutôt la surcharge à deux arguments lorsque la réécriture dépend de la configuration d'analyse :

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contrat :

| Règle | Détail |
|---|---|
| Sans état et compatible avec les accès concurrents | Une seule instance est mise en cache par classe et partagée par toutes les analyses. |
| Constructeur public sans argument | Requis uniquement lorsque le modificateur est désigné par son nom de classe. |
| Gérer l'entrée `null` et l'entrée vide | L'analyseur ne les filtre pas avant l'exécution de la chaîne. |
| Un retour `null` signifie « aucun changement » | La valeur précédente est conservée. Renvoyez `input` tel quel lorsque le modificateur ne s'applique pas. |
| Mieux vaut renvoyer l'entrée inchangée que lever une exception | Un modificateur qui lève une exception interrompt l'analyse — voir [Gestion des échecs](#gestion-des-échecs-dun-modificateur). |
| `getName()` | Redéfinissez-la pour contrôler le nom rapporté dans `ModifierInfo` ; par défaut, il s'agit du nom simple de la classe. |

### Déclarer des modificateurs

Les modificateurs s'exécutent dans leur ordre d'ajout, chacun recevant la sortie du précédent. Déclarez-les par instance, par nom de classe pleinement qualifié, ou sous forme de liste de l'un ou l'autre :

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

Les [modificateurs intégrés](#modificateurs-intégrés) se désignent exactement comme les vôtres — **toujours en nom pleinement qualifié**. Il n'existe pour eux ni nom court ni alias ; `ModifierRegistry` résout tout modificateur, livré ou non, par son nom de classe complet.

Les noms sont résolus par `ModifierRegistry`, qui instancie chaque classe une seule fois via son constructeur sans argument et met l'instance en cache pour toute configuration ultérieure désignant la même classe. La résolution a lieu **au moment de la construction de la configuration** : un nom introuvable, qui n'implémente pas `ModifierInterface` ou qui ne peut pas être instancié lève donc un `IllegalArgumentException` à cet endroit — et non silencieusement au moment de l'analyse. Un modificateur qui ne peut pas être construit par réflexion (parce qu'il détient une dépendance injectée, par exemple) peut être pré-enregistré pour rester désignable par son nom :

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Examiner l'action d'un modificateur

Lorsque des modificateurs sont configurés, `ParseResult.getPayload()` reflète l'entrée **modifiée**. L'original est conservé dans `ModifierInfo` :

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` rapporte le `getName()` de chaque modificateur, qui vaut par défaut le nom simple de la classe mais que les deux modificateurs intégrés redéfinissent — une chaîne composée des deux rapporte donc les noms d'affichage, et non les noms de classe :

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` renvoie `null` lorsqu'aucun modificateur n'était configuré. Lorsque des modificateurs se sont exécutés mais que tous ont renvoyé l'entrée inchangée, l'information est présente et `isModified()` vaut `false` — seuls les modificateurs ayant réellement modifié l'entrée figurent dans `getAppliedModifiers()`.

### Gestion des échecs d'un modificateur

Un modificateur qui lève une exception interrompt l'analyse. L'exception est encapsulée dans une `GaiaModifierException` nommant le modificateur fautif, et le résultat porte une erreur interne `GE-I001` dont le message reprend ce nom ; `getPayload()` rapporte l'entrée non modifiée. L'analyse ne se poursuit délibérément **pas** avec une chaîne à demi réécrite : une étape de normalisation qui échouerait silencieusement produirait des résultats d'apparence valide mais issus d'une entrée erronée.

---

## Modes d'analyse

Chaque mode désigne l'[étape de la chaîne](#chaîne-de-traitement-de-lanalyse) la plus profonde qu'il exécute ; toutes les étapes antérieures s'exécutent également.

| Mode | Va jusqu'à | Répond à la question |
|---|---|---|
| `DATA_CARRIER` | Étape 1 (aiguillage de l'entrée) | Quelle symbologie a porté ces données ? |
| `SYNTAX` | Étape 2 (syntaxe) | Les codes AI et les longueurs sont-ils bien formés ? |
| `CONTENT` | Étape 3 (contenu) | Les valeurs sont-elles des données GS1 valides ? |
| `INTERPRETATION` | Étape 4 (interprétation) | Que signifient les valeurs ? |

### Mode DATA_CARRIER

S'arrête après l'étape 1 — valide l'identifiant de symbologie AIM et identifie la symbologie, mais n'entre pas dans la chaîne d'analyse des AI. Utile pour identifier une symbologie et aiguiller le traitement sans supporter le coût d'une validation complète.

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

**À utiliser quand :** votre application doit identifier le type de code-barres avant de décider comment traiter la charge utile — par exemple pour aiguiller vers des gestionnaires différents selon qu'il s'agit d'une symbologie 1D ou 2D. Pour cet aiguillage, préférez le type [`DataCarrierType`](#datacarrierentry-et-datacarriertype) (`getDataCarrier().getDataCarrierType()`) plutôt qu'une comparaison de chaînes sur `getName()`.

---

### Mode SYNTAX

S'arrête après l'étape 2. Utile pour un présélection structurelle sans le coût de la validation du contenu.

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

**À utiliser quand :** vous voulez vérifier que les codes AI et les longueurs de données sont bien formés avant de vous engager dans une validation complète, ou lorsque vous traitez de gros volumes où les erreurs de contenu sont rares.

---

### Mode CONTENT

S'arrête après l'étape 3.

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

> La plupart des AI ne peuvent pas figurer seuls : les AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) et `21`
> (SERIAL) *exigent* chacun la présence d'une clé d'identification telle que l'AI `01` dans la même chaîne
> d'éléments ; omettre le GTIN ci-dessus échouerait donc dès l'étape 2 sur `GE-S005`, sans jamais
> atteindre la validation du contenu. Positionnez `skipRequiresCheck(true)` sur le
> `ParseConfig` pour analyser des fragments qui omettent délibérément leurs AI compagnons.

**À utiliser quand :** vous devez savoir si une valeur lue est pleinement conforme à GS1 avant de l'utiliser dans un processus métier, sans le surcoût de l'enrichissement par interprétation.

---

### Mode INTERPRETATION (par défaut)

Exécute toute la chaîne jusqu'à l'étape 4. C'est le mode par défaut lorsqu'on appelle `parse(String)` sans argument de mode. Seuls les éléments ayant passé la validation du contenu sans erreur sont enrichis.

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

**Exemple de sortie :**
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

**Exemple de montant monétaire (AI 3932 — prix avec code devise ISO) :**
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

**À utiliser quand :** vous construisez des couches d'affichage, des outils de vérification d'étiquettes, ou toute interface nécessitant une décomposition lisible des valeurs d'AI.

---

## Identifiant de corrélation

Certains flux de travail font précéder l'entrée GS1 brute d'un identifiant de corrélation propriétaire à 8 chiffres, afin de rattacher les événements de lecture à une session ou à une transaction. Le format est le suivant :

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

Le `~` (tilde) est le séparateur. Il ne fait **pas** partie du contenu GS1 — il est retiré avant tout début d'analyse GS1.

### Règles de détection

Le préfixe est détecté lorsque l'entrée commence par exactement 8 chiffres décimaux ASCII (`0`–`9`) immédiatement suivis d'un `~`. Si le 9ᵉ caractère n'est pas un `~`, ou si l'un des 8 premiers caractères n'est pas un chiffre, l'entrée est traitée comme un contenu GS1 ordinaire, sans préfixe de corrélation.

### Accéder à l'identifiant de corrélation

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

### Combinaison avec un identifiant de symbologie AIM

Un préfixe de corrélation peut précéder un identifiant de symbologie AIM. L'analyseur gère ce cas de façon transparente :

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Classe d'implémentation :** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Un **GS1 Digital Link** encode une ou plusieurs valeurs d'AI directement dans la structure d'une URL HTTP(S), permettant des identifiants de produits physiques résolvables sur le web. GAIA met en œuvre la norme *GS1 Digital Link Standard: URI Syntax* (version 1.7.0) pour les URI **non compressés**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` reconnaît automatiquement les URI Digital Link — toute entrée commençant par `http://` ou `https://` est dirigée vers `GS1DLParser`, qui exécute les mêmes étapes de contenu et d'interprétation que la chaîne des chaînes d'éléments.

### Structure de l'URI et rôles des AI

Chaque AI d'un URI Digital Link joue l'un de trois rôles, exposé sur chaque `GS1AIObjectElement` par `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) :

| Rôle | Emplacement | Exemple |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Premier couple `/ai/valeur` du chemin (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Couples de chemin suivants, ordonnés selon la clé primaire (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Paramètres de requête à clé entièrement numérique (§4.10) | `?17=271231` |

Règles structurelles appliquées (`DLPathRules`) :
- Exactement **une** clé d'identification primaire dans le chemin ; les clés supplémentaires doivent être encodées comme attributs de données dans la requête.
- Les qualificatifs de clé doivent être admis par la clé primaire et apparaître dans l'ordre prescrit. Les qualificatifs optionnels peuvent être omis, mais ceux qui *sont* présents doivent respecter l'ordre fixé — voir [Ordre des qualificatifs](#ordre-des-qualificatifs).
- Des segments de chemin personnalisés quelconques peuvent précéder la clé primaire (par exemple `/products/au/01/...`) ; récupérez-les via `getDigitalLinkInfo().getCustomPathStem()`.
- Les clés de requête non numériques (`linkType`, `context`, paramètres d'extension tels que `23P`) sont ignorées ; les clés entièrement numériques doivent être des AI valides marqués `validAsDataAttribute`.
- Les caractères de valeur encodés en pourcentage sont décodés ; les AI `(03)` et `(8014)` ne sont pas autorisés.

Les clés primaires et leurs séquences de qualificatifs admissibles sont **pilotées par les données** issues des définitions d'AI — l'indicateur `gs1DigitalLinkPrimaryKey` et l'attribut `gs1DigitalLinkQualifiers` — plutôt que codées en dur.

Toute violation structurelle, ou une entrée qui n'est pas une URL, produit une erreur structurelle Digital Link (`GE-L001`–`GE-L014`, un code par condition). Les métadonnées décomposées de l'URL (`scheme`, `domain`, `path`, `customPathStem`, `query` et l'objet `java.net.URL`) restent accessibles via `getDigitalLinkInfo()`, même en présence d'erreurs structurelles.

### Ordre des qualificatifs

Pour chaque clé primaire, `gs1DigitalLinkQualifiers` énumère une ou plusieurs séquences **ordonnées** de qualificatifs. Au sein d'une séquence, un AI entre crochets est **optionnel**, un AI sans crochets est **obligatoire** — à l'image de la notation `[cpv-comp]` de l'ABNF du §4.9. Les séquences d'une même clé primaire sont des alternatives mutuellement exclusives.

Le GTIN (`01`), par exemple, définit deux séquences :

| Chemin | Séquence | Signification |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — chacun optionnel, mais dans cet ordre imposé |
| upui-path | `235` | TPX (obligatoire) ; GTIN + TPX = UPUI |

Ainsi `/01/09506000134352/10/LOT-ABC/21/SER` est valide (LOT avant SER, CPV omis), `/01/.../21/SER/10/LOT-ABC` est **rejeté** (ordre incorrect), et `/01/09506000134352/235/2ABC456` relève de l'upui-path. Le contrôle d'ordre est une correspondance de sous-séquence préservant l'ordre : les AI optionnels peuvent donc être omis, mais jamais réordonnés.

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

**Classe d'implémentation :** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Exploiter les résultats

### ParseResult

Le résultat de premier niveau renvoyé par `GaiaParser.parse()`.

| Méthode | Renvoie | Description |
|---|---|---|
| `isValid()` | `boolean` | `true` en l'absence d'erreur, quel qu'en soit le niveau. Les avertissements n'affectent pas la validité. Toujours `true` lorsque `getAiObject()` vaut `null`. |
| `getPayload()` | `String` | La chaîne d'entrée après retrait du préfixe de corrélation — et après réécriture éventuelle par les [modificateurs d'entrée](#modificateurs-dentrée). |
| `getPayloadContent()` | `String` | La charge utile privée de l'identifiant de symbologie AIM et du préfixe ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (un support de données rejeté comme non GS1, par exemple un support Code 39 `]A0`), ou `UNABLE_TO_DETERMINE_CONTENT` (lorsque `aiObject` vaut `null`, par exemple en mode `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | La profondeur de chaîne configurée (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | L'étape la plus profonde réellement atteinte par l'analyse — voir ci-dessous. |
| `isParseComplete()` | `boolean` | `true` si l'analyse a atteint la profondeur demandée (`achieved == requested`). Indépendant de `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Tous les AI résolus. `null` en mode `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Toutes les erreurs de niveau autre que WARNING (au niveau de l'objet et de tous les éléments). |
| `getWarnings()` | `List<GaiaError>` | Tous les avis de niveau WARNING (au niveau de l'objet et de tous les éléments). |
| `hasWarnings()` | `boolean` | `true` si des avis de niveau WARNING ont été émis. |
| `getIssues()` | `List<GaiaError>` | Erreurs et avertissements réunis. |
| `hasDataCarrier()` | `boolean` | `true` si un identifiant de symbologie AIM a été reconnu. |
| `getDataCarrier()` | `DataCarrierEntry` | Métadonnées de symbologie, ou `null` si aucun support n'a été identifié. |
| `hasEci()` | `boolean` | `true` si un indicateur ECI a été retiré de la charge utile. |
| `getEci()` | `EciEntry` | Métadonnées d'encodage ECI, ou `null`. |
| `hasCorrelationId()` | `boolean` | `true` si un préfixe de corrélation `DDDDDDDD~` était présent dans l'entrée d'origine. |
| `getCorrelationInfo()` | `CorrelationInfo` | L'identifiant de corrélation extrait, ou `null` en son absence. |
| `isInputModified()` | `boolean` | `true` si un [modificateur d'entrée](#modificateurs-dentrée) a modifié l'entrée. |
| `getModifierInfo()` | `ModifierInfo` | L'action de la chaîne de modificateurs — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` si aucun modificateur n'était configuré. |
| `getTiming()` | `ProcessingTiming` | Chronométrage de l'analyse en temps réel — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` si le résultat n'a pas été produit par `GaiaParser`. |
| `getVersion()` | `String` | La version de la bibliothèque qui a produit le résultat. |

#### Mode d'analyse demandé et mode atteint

La chaîne parcourt l'échelle **SYNTAX → CONTENT → INTERPRETATION** et s'arrête prématurément en cas d'erreur : le mode réellement *atteint* peut donc être moins profond que le mode *demandé*. `getAchievedParseMode()` indique jusqu'où elle est allée :

| Demandé | Ce qui se produit | Atteint | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | une erreur de **syntaxe ou de structure** interrompt l'analyse après le découpage | `SYNTAX` | `false` |
| `INTERPRETATION` | une erreur de **contenu** (format ou chiffre de contrôle incorrect) bloque l'enrichissement | `CONTENT` | `false` |
| `CONTENT` | l'étape de contenu va toujours à son terme (les erreurs sont annotées, pas fatales) | `CONTENT` | `true` |
| n'importe lequel (entrée sans erreur) | la chaîne atteint la profondeur demandée | = demandé | `true` |
| `DATA_CARRIER` | support validé ; aucun contenu d'AI analysé | `DATA_CARRIER` | `true` |
| n'importe lequel | le support de données est rejeté avant l'analyse des AI (par exemple un support non GS1 `]A0`) | `SYNTAX` | `false` |

`isParseComplete()` est indépendant d'`isValid()` : une analyse `CONTENT` d'un GTIN au chiffre de contrôle incorrect est **complète** (l'étape de contenu a bien été exécutée) tout en étant **invalide** (le chiffre de contrôle a échoué). Utilisez `isParseComplete()` pour demander « la chaîne est-elle allée aussi loin que je l'ai demandé ? » et `isValid()` pour demander « les données sont-elles bien formées ? ».

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

La collection des éléments AI résolus.

| Méthode | Description |
|---|---|
| `getAis()` | Toutes les instances de `GS1AIObjectElement`, dans l'ordre de l'entrée. |
| `get(String aiCode)` | Premier élément correspondant au code AI donné, ou `null`. |
| `contains(String aiCode)` | `true` si un AI portant ce code est présent. |
| `size()` | Nombre d'AI résolus. |
| `isValid()` | `true` en l'absence d'erreur au niveau de l'objet et si aucun élément ne porte d'erreur. |
| `toHriString()` | Chaîne HRI, par exemple `(01)09506000134352 (17)261231`. |
| `toElementString()` | Chaîne d'éléments brute — sans parenthèses, avec un FNC1 après chaque élément de longueur variable — par exemple `010950600013435210LOT-ABC<GS>17271231`. Renvoie `null` si `isValid()` vaut `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` lorsque `hasDigitalLink()` vaut vrai, sinon `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` si l'entrée était un URI GS1 Digital Link portant une clé d'identification primaire. Une URL bien formée sans clé primaire expose tout de même `getDigitalLinkInfo()` mais renvoie `false` ici. |
| `getCanonicalDigitalLink()` | L'URI GS1 Digital Link canonique (§4.12) sur `https://id.gs1.org` — clé primaire et qualificatifs en segments de chemin, attributs de données en paramètres de requête triés par clé d'AI — ou `null` en l'absence de clé primaire. |
| `getDigitalLinkInfo()` | Métadonnées de décomposition de l'URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), ou `null` s'il ne s'agit pas d'un Digital Link. |
| `getAllErrors()` | Erreurs au niveau de l'objet + toutes les erreurs d'éléments (hors WARNING). |
| `getAllWarnings()` | Avertissements au niveau de l'objet + tous les avertissements d'éléments. |
| `getAllIssues()` | L'ensemble réuni. |

---

### GS1AIObjectElement

Une instance d'AI résolue.

| Méthode | Description |
|---|---|
| `getAi()` | Code AI, par exemple `"01"`, `"3102"`. |
| `getTitle()` | Titre de données GS1, par exemple `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Description GS1 complète de l'AI, **localisée dans la langue d'analyse** (par exemple `"Global Trade Item Number (GTIN)"` en anglais). Retombe sur le texte anglais de la définition de l'AI si aucune traduction n'existe. |
| `getFormatString()` | Descripteur de format couvrant l'AI *et* sa donnée, par exemple `"N2+N14"` pour l'AI `01`, `"N2+X..20"` pour l'AI `10`, `"N4+N3+N..15"` pour l'AI `3932`. |
| `getValue()` | Valeur de donnée brute extraite de la chaîne d'éléments. |
| `isFixedLength()` | `true` si l'AI a une longueur de donnée fixe. |
| `getPosition()` | Décalage de caractère (base zéro) dans l'entrée d'origine. |
| `getGS1ComponentValues()` | Découpage de la valeur par composant (pour les AI multi-composants). |
| `getErrors()` | Erreurs de niveau élément, hors WARNING. |
| `getWarnings()` | Avis de niveau WARNING sur l'élément. |
| `getIssues()` | Erreurs et avertissements de l'élément réunis. |
| `hasErrors()` | `true` si des erreurs hors WARNING sont attachées. |
| `hasWarnings()` | `true` si des avis de niveau WARNING sont attachés. |
| `getInterpretations()` | Entrées `GS1AIInterpretation` (renseignées en mode INTERPRETATION). |
| `getInterpretation(String type)` | Première interprétation correspondant à la clé de type `GS1Constants_Enricher` donnée, ou `null`. |
| `getDigitalLinkAIType()` | Le rôle Digital Link de l'élément (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), ou `null` pour une entrée de type chaîne d'éléments. |
| `hasDigitalLinkAIType()` | `true` si un rôle Digital Link a été attribué. |

---

### GaiaError

Une erreur de validation ou un avis, immuable.

| Méthode | Description |
|---|---|
| `getId()` | Identifiant de catalogue, par exemple `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` ou `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` ou `INTERNAL`. |
| `getCode()` | Code court exploitable par un programme. |
| `getAi()` | Code AI à l'origine de l'erreur, ou `null` pour une erreur au niveau de l'objet. |
| `getMessage()` | Message lisible, avec interpolation des valeurs. |
| `getPosition()` | Décalage de caractère (base zéro) dans l'entrée d'origine. |

---

### GS1AIInterpretation

Un fragment d'interprétation étiqueté, attaché à un `GS1AIObjectElement` en mode `INTERPRETATION`.

| Méthode | Description |
|---|---|
| `getType()` | Clé de type exploitable par un programme, par exemple `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Stable d'une langue à l'autre. |
| `getLabel()` | Étiquette lisible, **localisée dans la langue d'analyse** (par exemple `"Date"` / `"GS1 company prefix"` en anglais). |
| `getValue()` | Valeur extraite ou enrichie, par exemple `"31/12/2026"`, `"9506000"`. Non localisée. |

---

### DataCarrierEntry et DataCarrierType

Lorsque l'entrée porte un identifiant de symbologie AIM, `ParseResult.getDataCarrier()` renvoie un `DataCarrierEntry` décrivant le symbole qui a transporté les données. Cette entrée est l'enregistrement précis du registre correspondant à l'identifiant AIM reconnu ; `DataCarrierType` est l'énumération, connue à la compilation, à laquelle elle appartient.

#### DataCarrierEntry

Les métadonnées d'un identifiant de symbologie AIM reconnu (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Méthode | Description |
|---|---|
| `getAimCodeId()` | L'identifiant de symbologie AIM reconnu, par exemple `"]C1"`. |
| `getName()` | Nom lisible du symbole précis, par exemple `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Description plus détaillée du support. |
| `getType()` | Le type structurel du support sous forme de chaîne (reflète `getDataCarrierType().getCategory()`). |
| `getStandard()` | La norme de symbologie, lorsqu'elle est renseignée. |
| `getDataCarrierType()` | Le `DataCarrierType` typé correspondant à cette entrée — à préférer pour tout aiguillage programmatique. |
| `isGs1Capable()` | `true` si le support peut contenir des données GS1 (chaînes d'éléments AI et/ou Digital Link). |
| `isGs1AICapable()` | `true` si le support peut contenir des chaînes d'éléments AI GS1. |
| `isGs1DigitalLinkCapable()` | `true` si le support peut contenir un URI GS1 Digital Link. |
| `isEciCapable()` | `true` si le support prend en charge un indicateur ECI. |
| `isRequiresGtinPadding()` | `true` pour les supports EAN/UPC/ITF dont la valeur numérique est complétée jusqu'au GTIN-14 avant l'analyse des AI. |

#### DataCarrierType

Une énumération, connue à la compilation, des types de supports de données, indexée par l'identifiant de symbologie AIM attribué dans l'ISO/CEI 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). Le caractère qui suit `]` (le *caractère de code*) sélectionne la famille ; la plupart des familles correspondent à une seule constante couvrant tous les modificateurs (`ITF` couvre `]I0`–`]I2` ; `EAN_UPC` couvre EAN-13, UPC-A, UPC-E et EAN-8). Lorsque GS1 réserve un modificateur aux données d'AI, cette variante constitue sa propre constante — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — distincte de son homologue ordinaire. En l'absence d'identifiant AIM, ou lorsqu'il désigne un support inconnu, le type est `UNKNOWN`.

| Méthode | Description |
|---|---|
| `getCategory()` | La catégorie générale `GaiaConstants.DataCarrierTypeCategory` : `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` ou `OTHER`. |
| `getCodeChar()` | Le caractère de code AIM identifiant la famille, par exemple `"Q"` pour QR Code ; `null` pour `UNKNOWN`. |
| `getDisplayName()` | Nom lisible du *type* (parfois plus large que `DataCarrierEntry.getName()` — par exemple `"EAN-13 / UPC-A / UPC-E / EAN-8"` contre `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` pour les constantes qui désignent toujours des données d'AI GS1 : les quatre variantes réservées par GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) ainsi que `GS1_DATABAR`, intrinsèquement GS1 puisque tout modificateur `]e` désigne un GS1 DataBar. Plus restrictif que `DataCarrierEntry.isGs1AICapable()` — un `QR_CODE` ordinaire peut lui aussi porter des données d'AI GS1. |
| `static forAimCodeId(String)` | Résout un type directement à partir d'un identifiant AIM (`"]Q3"` → `GS1_QR_CODE` ; `"]Q9"` → `QR_CODE`) ; renvoie `UNKNOWN` pour un identifiant absent, mal formé ou non reconnu. |

Aiguiller par type plutôt que par nom — par exemple pour séparer les symboles linéaires (Code 128) des symboles 2D (QR / Data Matrix) :

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` ne couvre que les symboles matriciels et à points ; les supports linéaires empilés (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) relèvent de `STACKED_LINEAR`, bien qu'on les appelle
couramment des codes-barres « 2D ». Pour traiter les deux comme un seul groupe — par exemple pour décider
si un imageur est nécessaire plutôt qu'un lecteur laser — testez l'appartenance à l'une ou l'autre catégorie.

> La résolution du type suppose que l'identifiant de symbologie AIM soit présent dans la lecture ; sans lui, `getDataCarrier()` vaut `null` et le type est `UNKNOWN`. Configurez le lecteur pour qu'il transmette le préfixe d'identifiant AIM.

---

## Référence des erreurs

| Code | Niveau | Étape | Signification |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Préfixe d'AI inconnu — impossible de déterminer la longueur des données |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Entrée trop courte pour lire un code AI complet |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Valeur tronquée — moins de caractères que l'AI n'en exige |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Identifiant de données en double dans la chaîne d'éléments |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Dépendance d'AI requise absente |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Association d'AI interdite — deux AI qui ne peuvent pas coexister |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Échec inattendu du découpage en jetons |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Caractère hors du jeu encodable GS1 dans la chaîne d'éléments |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Séparateur FNC1 requis absent après un AI de longueur variable |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Données résiduelles au-delà du maximum de tous les composants |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Séparateur FNC1 après un AI de longueur fixe en position intermédiaire |
| `GE-W002` | WARNING | SYNTAX | FNC1 en fin de chaîne d'éléments (simple avis) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Violations structurelles d'un URI Digital Link — un code par condition (URI mal formé, schéma, hôte, ordre des qualificatifs, AI interdit, absence de clé primaire (`GE-L013`), clés primaires multiples (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | La valeur ne satisfait pas l'expression régulière de l'AI |
| `GE-C003` | DATA_ERROR | CONTENT | Échec de la validation du chiffre de contrôle |
| `GE-C004` | DATA_ERROR | CONTENT | Échec de la validation de la paire de caractères de contrôle |
| `GE-C005` | FORMAT_ERROR | CONTENT | La valeur d'un composant contient un caractère hors du jeu autorisé |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Échecs de format de composant — un code par condition de validation (voir `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Échecs de validation sémantique personnalisée — un code par condition de validation (voir `content/validator/`). **Exceptions :** les 14 contrôles de préfixe d'entreprise GS1 énumérés ci-dessous portent le niveau `WARNING`, et `GE-C168` (code pays numérique ISO 3166-1 non reconnu) porte `FORMAT_ERROR`. |
| Contrôles du préfixe d'entreprise GS1 | WARNING | CONTENT | La clé ne commence pas par un préfixe d'entreprise GS1 reconnu, sur les AI porteurs d'une clé GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Simple avis — sans effet sur la validité. |
| `GE-C169` | DATA_ERROR | CONTENT | Échec du chiffre de contrôle IMEI (Luhn) sur l'AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Échec du chiffre de contrôle EID (Luhn) sur l'AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Identifiant de symbologie AIM non reconnu |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Support identifié mais ne prenant en charge ni les chaînes d'éléments AI GS1 ni les URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Erreur interne inattendue |

> **Défaut connu dans le rendu des messages.** Les modèles du catalogue encadrent les
> valeurs interpolées d'apostrophes doublées à la manière de MessageFormat (`''{value}''`), mais
> `ErrorRegistry` interpole avec un simple `String.replace` : le doublement subsiste donc dans
> `getMessage()` — vous verrez actuellement `value ''09506000134351''` là où les
> messages cités dans ce guide affichent `value '09506000134351'`. Cela touche tous les
> messages citant une valeur, dans les 35 catalogues de langues. N'analysez pas les messages d'erreur ;
> comparez sur `getId()` / `getCode()`.

---

## Sûreté vis-à-vis des threads

`GaiaParser` est sûr vis-à-vis des threads une fois construit. Une instance unique peut être partagée entre plusieurs threads et utilisée simultanément. Le schéma recommandé consiste à construire une instance au démarrage de l'application et à la réutiliser :

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` est immuable et tout aussi sûr à partager. La seule obligation de sûreté vis-à-vis des threads que la bibliothèque ne peut pas garantir à votre place porte sur les [modificateurs d'entrée](#modificateurs-dentrée) : une instance unique de chaque modificateur est mise en cache et partagée par toutes les analyses concurrentes, les implémentations doivent donc être sans état.

---

## Annexe A — constantes de chaînes d'AI

`GS1Constants_AICodes` (dans le paquet `tools.pantheum.gaia.gs1.constants`) déclare une constante `String` pour chaque identifiant de données reconnu par GAIA. Utilisez ces constantes plutôt que d'écrire en dur les chaînes de codes AI :

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Chaque constante contient la forme textuelle du code AI (par exemple `AI_01_GTIN = "01"`).

### Identification et sérialisation

| AI | Constante | Description |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Code d'identification unitaire d'expédition (SSCC). |
| `01` | `AI_01_GTIN` | Numéro international d'article commercial (GTIN). |
| `02` | `AI_02_CONTENT` | Numéro international d'article commercial (GTIN) des unités commerciales contenues. |
| `03` | `AI_03_MTO_GTIN` | Identification d'une unité commerciale fabriquée sur commande (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Numéro de lot. |
| `20` | `AI_20_VARIANT` | Variante de produit interne. |
| `21` | `AI_21_SERIAL` | Numéro de série. |
| `22` | `AI_22_CPV` | Variante du produit de consommation. |
| `235` | `AI_235_TPX` | Extension sérialisée contrôlée par un tiers du numéro international d'article commercial (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identification supplémentaire du produit attribuée par le fabricant. |
| `241` | `AI_241_CUST_PART_NO` | Numéro de pièce client. |
| `242` | `AI_242_MTO_VARIANT` | Numéro de variante fabriquée sur commande. |
| `243` | `AI_243_PCN` | Numéro de composant d'emballage. |
| `250` | `AI_250_SECONDARY_SERIAL` | Numéro de série secondaire. |
| `251` | `AI_251_REF_TO_SOURCE` | Référence à l'entité source. |
| `253` | `AI_253_GDTI` | Identifiant international du type de document (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Composant d'extension du numéro international de localisation (GLN). |
| `255` | `AI_255_GCN` | Numéro international du coupon (GCN). |
| `30` | `AI_30_VAR_COUNT` | Nombre variable d'articles (article à mesure variable). |
| `37` | `AI_37_COUNT` | Nombre d'unités commerciales ou de pièces d'unité commerciale contenues dans une unité logistique. |

### Dates et heures

| AI | Constante | Description |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Date de production (AAMMJJ). |
| `12` | `AI_12_DUE_DATE` | Date d'échéance (AAMMJJ). |
| `13` | `AI_13_PACK_DATE` | Date d'emballage (AAMMJJ). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Date de durabilité minimale (AAMMJJ). |
| `16` | `AI_16_SELL_BY` | Date limite de vente (AAMMJJ). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Date limite d'utilisation (AAMMJJ). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Date et heure de livraison au plus tôt (AAMMJJhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Date et heure limite de livraison (AAMMJJhhmm). |
| `4326` | `AI_4326_REL_DATE` | Date de mise en circulation (AAMMJJ). |
| `7003` | `AI_7003_EXPIRY_TIME` | Date et heure limite d'utilisation (AAMMJJhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Date de première congélation (AAMMJJ). |
| `7007` | `AI_7007_HARVEST_DATE` | Date de récolte (AAMMJJ[AAMMJJ]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Date limite de test (AAMMJJ[hhmm]). |

### Quantité et mesure — mesure variable (métrique)

Les familles d'AI à 4 chiffres `310n`–`369n` encodent des quantités à mesure variable. Le troisième chiffre sélectionne le type de mesure ; le **quatrième chiffre** (`n`, 0–5) donne le nombre de décimales implicites — par exemple `AI_3102_NET_WEIGHT_KG` désigne un poids net en kg avec 2 décimales.

| Famille | Motif de constante (`n` = chiffre de décimales) | Description |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Poids net, kilogrammes (article à mesure variable). |
| `311n` | `AI_311n_LENGTH_M` | Longueur ou première dimension, mètres (article à mesure variable). |
| `312n` | `AI_312n_WIDTH_M` | Largeur, diamètre ou deuxième dimension, mètres (article à mesure variable). |
| `313n` | `AI_313n_HEIGHT_M` | Profondeur, épaisseur, hauteur ou troisième dimension, mètres (article à mesure variable). |
| `314n` | `AI_314n_AREA_M` | Surface, mètres carrés (article à mesure variable). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volume net, litres (article à mesure variable). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volume net, mètres cubes (article à mesure variable). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Poids logistique, kilogrammes. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Longueur ou première dimension, mètres. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Largeur, diamètre ou deuxième dimension, mètres. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Profondeur, épaisseur, hauteur ou troisième dimension, mètres. |
| `334n` | `AI_334n_AREA_M_LOG` | Surface, mètres carrés. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volume logistique, litres. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volume logistique, mètres cubes. |
| `337n` | `AI_337n_KG_PER_M` | Kilogrammes par mètre carré. |

### Quantité et mesure — mesure variable (impérial / États-Unis)

| Famille | Motif de constante (`n` = chiffre de décimales) | Description |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Poids net, livres (article à mesure variable). |
| `321n` | `AI_321n_LENGTH_IN` | Longueur ou première dimension, pouces (article à mesure variable). |
| `322n` | `AI_322n_LENGTH_FT` | Longueur ou première dimension, pieds (article à mesure variable). |
| `323n` | `AI_323n_LENGTH_YD` | Longueur ou première dimension, yards (article à mesure variable). |
| `324n` | `AI_324n_WIDTH_IN` | Largeur, diamètre ou deuxième dimension, pouces (article à mesure variable). |
| `325n` | `AI_325n_WIDTH_FT` | Largeur, diamètre ou deuxième dimension, pieds (article à mesure variable). |
| `326n` | `AI_326n_WIDTH_YD` | Largeur, diamètre ou deuxième dimension, yards (article à mesure variable). |
| `327n` | `AI_327n_HEIGHT_IN` | Profondeur, épaisseur, hauteur ou troisième dimension, pouces (article à mesure variable). |
| `328n` | `AI_328n_HEIGHT_FT` | Profondeur, épaisseur, hauteur ou troisième dimension, pieds (article à mesure variable). |
| `329n` | `AI_329n_HEIGHT_YD` | Profondeur, épaisseur, hauteur ou troisième dimension, yards (article à mesure variable). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Poids logistique, livres. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Longueur ou première dimension, pouces. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Longueur ou première dimension, pieds. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Longueur ou première dimension, yards. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Largeur, diamètre ou deuxième dimension, pouces. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Largeur, diamètre ou deuxième dimension, pieds. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Largeur, diamètre ou deuxième dimension, yard. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Profondeur, épaisseur, hauteur ou troisième dimension, pouces. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Profondeur, épaisseur, hauteur ou troisième dimension, pieds. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Profondeur, épaisseur, hauteur ou troisième dimension, yards. |
| `350n` | `AI_350n_AREA_IN` | Surface, pouces carrés (article à mesure variable). |
| `351n` | `AI_351n_AREA_FT` | Surface, pieds carrés (article à mesure variable). |
| `352n` | `AI_352n_AREA_YD` | Surface, yards carrés (article à mesure variable). |
| `353n` | `AI_353n_AREA_IN_LOG` | Surface, pouces carrés. |
| `354n` | `AI_354n_AREA_FT_LOG` | Surface, pieds carrés. |
| `355n` | `AI_355n_AREA_YD_LOG` | Surface, yards carrés. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Poids net, onces troy (article à mesure variable). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Poids net (ou volume), onces (article à mesure variable). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volume net, quarts (article à mesure variable). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volume net, gallons US (article à mesure variable). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volume logistique, quarts. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volume logistique, gallons US. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volume net, pouces cubes (article à mesure variable). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volume net, pieds cubes (article à mesure variable). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volume net, yards cubes (article à mesure variable). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volume logistique, pouces cubes. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volume logistique, pieds cubes. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volume logistique, yards cubes. |

### Prix et montants monétaires

Le quatrième chiffre (`n`) encode le nombre de décimales implicites. Sa plage autorisée
diffère selon la famille — voir la colonne `n`.

| Famille | Motif de constante (`n` = chiffre de décimales) | `n` | Description |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Montant à payer applicable ou valeur du coupon, devise locale. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Montant à payer applicable avec code devise ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Montant à payer applicable, zone monétaire unique (article à mesure variable). |
| `393n` | `AI_393n_PRICE` | 0–9 | Montant à payer applicable avec code devise ISO (article à mesure variable). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Pourcentage de remise d'un coupon. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Montant à payer par unité de mesure, zone monétaire unique (article à mesure variable). |

### Lieux et expédition

| AI | Constante | Description |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Numéro de bon de commande du client. |
| `401` | `AI_401_GINC` | Numéro international d'identification d'envoi (GINC). |
| `402` | `AI_402_GSIN` | Numéro international d'identification d'expédition (GSIN). |
| `403` | `AI_403_ROUTE` | Code d'acheminement. |
| `410` | `AI_410_SHIP_TO_LOC` | Numéro international de localisation (GLN) du lieu de livraison. |
| `411` | `AI_411_BILL_TO` | Numéro international de localisation (GLN) du facturé. |
| `412` | `AI_412_PURCHASE_FROM` | Numéro international de localisation (GLN) du fournisseur. |
| `413` | `AI_413_SHIP_FOR_LOC` | Numéro international de localisation (GLN) de réexpédition. |
| `414` | `AI_414_LOC_NO` | Identification d'un emplacement physique - Numéro international de localisation (GLN). |
| `415` | `AI_415_PAY_TO` | Numéro international de localisation (GLN) de l'entité facturante. |
| `416` | `AI_416_PROD_SERV_LOC` | Numéro international de localisation (GLN) du lieu de production ou de service. |
| `417` | `AI_417_PARTY` | Numéro international de localisation (GLN) de la partie. |
| `420` | `AI_420_SHIP_TO_POST` | Code postal du lieu de livraison au sein d'une même autorité postale. |
| `421` | `AI_421_SHIP_TO_POST` | Code postal du lieu de livraison avec code pays ISO. |
| `422` | `AI_422_ORIGIN` | Pays d'origine d'une unité commerciale. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | Pays de première transformation. |
| `424` | `AI_424_COUNTRY_PROCESS` | Pays de transformation. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | Pays de désassemblage. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | Pays couvrant l'ensemble de la chaîne de transformation. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivision du pays d'origine. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Nom de l'entreprise du lieu de livraison. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Contact du lieu de livraison. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Adresse de livraison, ligne 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Adresse de livraison, ligne 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Quartier du lieu de livraison. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Localité du lieu de livraison. |
| `4306` | `AI_4306_SHIP_TO_REG` | Région du lieu de livraison. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Code pays du lieu de livraison. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Numéro de téléphone du lieu de livraison. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Géolocalisation du lieu de livraison. |
| `4310` | `AI_4310_RTN_TO_COMP` | Nom de l'entreprise de retour. |
| `4311` | `AI_4311_RTN_TO_NAME` | Contact pour le retour. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Adresse de retour, ligne 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Adresse de retour, ligne 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Quartier de retour. |
| `4315` | `AI_4315_RTN_TO_LOC` | Localité de retour. |
| `4316` | `AI_4316_RTN_TO_REG` | Région de retour. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Code pays de retour. |
| `4318` | `AI_4318_RTN_TO_POST` | Code postal de retour. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Numéro de téléphone de retour. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Description du code de service. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Indicateur de marchandises dangereuses. |
| `4322` | `AI_4322_AUTH_LEAVE` | Autorisation de dépôt sans signature. |
| `4323` | `AI_4323_SIG_REQUIRED` | Indicateur de signature requise. |
| `4330` | `AI_4330_MAX_TEMP_F` | Température maximale en Fahrenheit (exprimée en centièmes de degré). |
| `4331` | `AI_4331_MAX_TEMP_C` | Température maximale en Celsius (exprimée en centièmes de degré). |
| `4332` | `AI_4332_MIN_TEMP_F` | Température minimale en Fahrenheit (exprimée en centièmes de degré). |
| `4333` | `AI_4333_MIN_TEMP_C` | Température minimale en Celsius (exprimée en centièmes de degré). |

### Attributs produit et traçabilité

| AI | Constante | Description |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Numéro de nomenclature OTAN (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Classification UN/CEE des carcasses et découpes de viande. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Puissance active. |
| `7005` | `AI_7005_CATCH_AREA` | Zone de capture. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Espèce à des fins halieutiques. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Type d'engin de pêche. |
| `7010` | `AI_7010_PROD_METHOD` | Méthode de production. |
| `7020` | `AI_7020_REFURB_LOT` | Identifiant de lot de remise à neuf. |
| `7021` | `AI_7021_FUNC_STAT` | État fonctionnel. |
| `7022` | `AI_7022_REV_STAT` | État de révision. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Identifiant international d'actif individuel (GIAI) d'un assemblage. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Numéro de l'unité de transformation, avec code pays ISO à trois chiffres (10 emplacements). |
| `7040` | `AI_7040_UIC_EXT` | UIC GS1 avec extension 1 et indice de l'importateur. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Type d'unité de fret UN/CEFACT. |

### Numéros nationaux de remboursement de santé (NHRN)

| AI | Constante | Description |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Numéro national de remboursement des soins de santé (NHRN) - Allemagne PZN. |
| `711` | `AI_711_NHRN_CIP` | Numéro national de remboursement des soins de santé (NHRN) - France CIP. |
| `712` | `AI_712_NHRN_CN` | Numéro national de remboursement des soins de santé (NHRN) - Espagne CN. |
| `713` | `AI_713_NHRN_DRN` | Numéro national de remboursement des soins de santé (NHRN) - Brésil DRN. |
| `714` | `AI_714_NHRN_AIM` | Numéro national de remboursement des soins de santé (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Numéro national de remboursement des soins de santé (NHRN) - États-Unis d'Amérique NDC. |
| `716` | `AI_716_NHRN_AIC` | Numéro national de remboursement des soins de santé (NHRN) - Italie AIC. |
| `717` | `AI_717_NHRN_SRN` | Numéro national de remboursement des soins de santé (NHRN) - Costa Rica, numéro d'enregistrement sanitaire. |

### Santé, GMN, HIDRI, CPID, données personnelles

| AI | Constante | Description |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Référence de certification (10 emplacements). |
| `7240` | `AI_7240_PROTOCOL` | Identifiant de protocole. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Type de support AIDC. |
| `7242` | `AI_7242_VCN` | Numéro de contrôle de version (VCN). |
| `7250` | `AI_7250_DOB` | Date de naissance (AAAAMMJJ). |
| `7251` | `AI_7251_DOB_TIME` | Date et heure de naissance (AAAAMMJJhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Sexe biologique. |
| `7253` | `AI_7253_FAMILY_NAME` | Nom de famille de la personne. |
| `7254` | `AI_7254_GIVEN_NAME` | Prénom de la personne. |
| `7255` | `AI_7255_SUFFIX` | Suffixe du nom de la personne. |
| `7256` | `AI_7256_FULL_NAME` | Nom complet de la personne. |
| `7257` | `AI_7257_PERSON_ADDR` | Adresse de la personne. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Ordre de naissance (naissances multiples). |
| `7259` | `AI_7259_BABY` | Nom de famille du nouveau-né. |
| `8001` | `AI_8001_DIMENSIONS` | Produits en rouleau (largeur, longueur, diamètre du mandrin, sens, raccords). |
| `8002` | `AI_8002_CMT_NO` | Identifiant de téléphone mobile cellulaire. |
| `8003` | `AI_8003_GRAI` | Identifiant international d'actif réutilisable (GRAI). |
| `8004` | `AI_8004_GIAI` | Identifiant international d'actif individuel (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Prix par unité de mesure. |
| `8006` | `AI_8006_ITIP` | Identification d'une pièce individuelle d'unité commerciale (ITIP). |
| `8007` | `AI_8007_IBAN` | Numéro de compte bancaire international (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Date et heure de production (AAMMJJhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indicateur de capteur à lecture optique. |
| `8010` | `AI_8010_CPID` | Identifiant de composant/pièce (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Numéro de série de l'identifiant de composant/pièce (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Version du logiciel. |
| `8013` | `AI_8013_GMN` | Numéro international de modèle (GMN). |
| `8014` | `AI_8014_MUDI` | Identifiant d'enregistrement de dispositif hautement individualisé (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Numéro international de relation de service (GSRN) identifiant la relation entre une organisation offrant des services et le prestataire de services. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Numéro international de relation de service (GSRN) identifiant la relation entre une organisation offrant des services et le bénéficiaire des services. |
| `8019` | `AI_8019_SRIN` | Numéro d'instance de relation de service (SRIN). |
| `8020` | `AI_8020_REF_NO` | Numéro de référence du bulletin de versement. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identification des pièces d'une unité commerciale (ITIP) contenues dans une unité logistique. |
| `8030` | `AI_8030_DIGSIG` | Signature numérique (DigSig). |
| `8040` | `AI_8040_IMEI` | Identité internationale d'équipement mobile (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identité internationale d'équipement mobile 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Numéro de carte SIM intégrée. |
| `8043` | `AI_8043_PSIM` | Numéro de carte SIM physique. |
| `8110` | `AI_8110` | Identification du code coupon pour utilisation en Amérique du Nord. |
| `8111` | `AI_8111_POINTS` | Points de fidélité d'un coupon. |
| `8112` | `AI_8112` | Identification du code coupon du fichier d'offres positives pour utilisation en Amérique du Nord. |
| `8200` | `AI_8200_PRODUCT_URL` | URL d'emballage étendu. |

### Usage interne / entreprise

| AI | Constante | Description |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Information convenue mutuellement entre partenaires commerciaux. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Information interne à l'entreprise (9 emplacements). |

---

## Annexe B — constantes de clés d'interprétation

Lorsque `GaiaParser.parse()` est appelé avec `ParseMode.INTERPRETATION`, chaque `GS1AIObjectElement` peut porter une liste d'objets `GS1AIInterpretation` produits par des enrichisseurs spécialisés. Utilisez les constantes de `GS1Constants_Enricher` (dans le paquet `tools.pantheum.gaia.gs1.constants`) comme clés pour retrouver une valeur d'interprétation précise :

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

Les étiquettes d'affichage ne sont **pas** des constantes — elles résident dans les catalogues localisés sous `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indexées par la constante de type. `GS1AIInterpretation.getLabel()` renvoie l'étiquette correspondant à la langue d'analyse (voir [Messages et étiquettes localisés](#messages-et-étiquettes-localisés)), avec repli sur l'anglais lorsqu'un catalogue omet la clé. La colonne « Étiquette d'affichage » ci-dessous reprend le texte français tel que livré dans le catalogue ; les clés de type, elles, sont stables d'une langue à l'autre : comparez toujours sur la clé, jamais sur l'étiquette.

### Date et heure

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `DATE_VALUE` | Date | AI de date (11–17, 7003, 7006, 7011, etc.) |
| `DATE_FORMAT` | Format de date | AI de date |
| `TIME_VALUE` | Heure | AI porteurs d'une heure (7003, 7011, 8008, etc.) |
| `TIME_FORMAT` | Format d'heure | AI porteurs d'une heure |
| `DATETIME_VALUE` | Date et heure | AI de date et heure |
| `DATETIME_FORMAT` | Format de date et heure | AI de date et heure |

### Date de récolte

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Date de début de récolte | AI 7007 |
| `HARVEST_END_DATE` | Date de fin de récolte | AI 7007 (fin de plage optionnelle) |
| `HARVEST_DATE_RANGE` | Plage de dates de récolte | AI 7007 |

### Préfixe d'entreprise GS1

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Préfixe d'entreprise GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Code du membre GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organisation membre GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Type de GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Niveau d'emballage | AI 01 |
| `GTIN_CHECK_DIGIT` | Chiffre de contrôle | AI 01, 02 |

### SSCC

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Chiffre d'extension | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Référence de série | AI 00 |
| `SSCC_CHECK_DIGIT` | Chiffre de contrôle | AI 00 |

### Pays (ISO 3166)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Code pays (numérique) | AI à pays unique (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Code pays (alpha-2) | AI de pays alpha-2 |
| `COUNTRY_NAME` | Nom du pays | AI à pays unique |
| `COUNTRY_LIST` | Pays | AI 423 — tous les noms réunis, par exemple `Australia, New Zealand` |

L'AI 423 (pays de première transformation) peut porter jusqu'à cinq pays : il émet donc un
**couple numéroté par pays** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — suivi de l'unique récapitulatif
`COUNTRY_LIST`. Construisez ces clés à partir des constantes `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` et de l'indice à base 1, ou parcourez simplement `getInterpretations()` ; les
clés `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` sans suffixe ne sont **pas** émises pour l'AI 423.

### Devise (ISO 4217)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Code de devise | AI de montant avec devise (391n, 393n) |
| `CURRENCY_ALPHA` | Code alphabétique de devise | AI de montant avec devise |
| `CURRENCY_NAME` | Nom de la devise | AI de montant avec devise |

### Température

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `TEMPERATURE` | Température | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Unité de température | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Température (formatée) | AI 4330–4333 |

### Sexe (ISO 5218)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `SEX_CODE` | Code de sexe | AI 7252 |
| `SEX_DESCRIPTION` | Description du sexe | AI 7252 |

### Espèces aquatiques (FAO)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Code d'espèce | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nom scientifique | AI 7008 |
| `SPECIES_ENGLISH` | Nom commun | AI 7008 |
| `SPECIES_FAMILY` | Famille | AI 7008 |
| `SPECIES_ORDER` | Ordre | AI 7008 |

### Numéro de nomenclature OTAN (NSN)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `NSN_FSG` | Groupe d'approvisionnement | AI 7001 |
| `NSN_FSG_NAME` | Nom du groupe d'approvisionnement | AI 7001 |
| `NSN_FSCG` | Classe d'approvisionnement | AI 7001 |
| `NSN_FSCG_NAME` | Nom de la classe d'approvisionnement | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Code pays | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | Pays | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Code pays ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Catégorie NCS | AI 7001 |
| `NSN_NIIN` | Numéro national d'article | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Produits en rouleau

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Largeur du rouleau (mm) | AI 8001 |
| `ROLL_LENGTH` | Longueur du rouleau (m) | AI 8001 |
| `CORE_DIAMETER` | Diamètre du noyau (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Code de sens d'enroulement | AI 8001 |
| `WINDING_DIRECTION` | Sens d'enroulement | AI 8001 |
| `SPLICES` | Épissures | AI 8001 |

### IBAN

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Code pays | AI 8007 |
| `IBAN_COUNTRY_NAME` | Pays | AI 8007 |
| `IBAN_CHECK_DIGITS` | Chiffres de contrôle | AI 8007 |
| `IBAN_CHECK_VALID` | Vérification | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Numéro de série | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Chiffre de contrôle | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Organisme émetteur | AI 8040, 8041 |

Les 15 chiffres se décomposent en `[ TAC (8) ][ numéro de série (6) ][ chiffre de contrôle de Luhn (1) ]`, le
RBI correspondant aux 2 premiers chiffres du TAC — `IMEI_RBI` est donc un préfixe d'`IMEI_TAC`, et non
une plage distincte. `IMEI_FORMATTED` restitue le groupement d'affichage GSMA standard
`AA-BBBBBB-CCCCCC-D` (par exemple `49-015420-323751-8`), qui coupe le TAC à la frontière
du RBI ; l'ancien groupement `6-2-6-1`, qui coupait là où commençait l'ancien Final Assembly
Code aujourd'hui abandonné, n'est pas émis.

`IMEI_RBI_NAME` résout le RBI en nom de l'organisme d'attribution via `ImeiRbiData`, et est
**ajouté en dernier, et uniquement lorsque le code y figure**. Cette table couvre trois groupes :

- **Attribution en cours** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, ainsi que `99`
  Global Hexadecimal Administrator et `98` (réservé).
- **Plages de test** — `00` et `02`–`09`, qui signalent des IMEI de test plutôt qu'une attribution réelle.
  Interrogez-les avec `ImeiRbiData.isTestCode(code)`.
- **Attribution close** — organismes historiques tels que `49` (BZT/BAPT, Allemagne), `44`
  (BABT, Royaume-Uni) ou `91` (MSAI, Inde). Interrogez-les avec `ImeiRbiData.isNoLongerAllocating(code)`.
  Les appareils portant ces codes sont ordinaires et restent en service ; seule l'attribution de nouveaux codes
  a cessé : il s'agit donc d'une information de reporting, jamais d'un signal de validité.

L'absence d'`IMEI_RBI_NAME` signifie « ce RBI n'est pas dans notre table », et **non** « IMEI invalide » :
la table est compilée à partir d'une liste de RBI publiée, et non directement auprès de la GSMA ; elle
peut donc accuser un retard sur les organismes récemment désignés. N'en tirez aucune conclusion de validation ;
le RBI n'est pas un caractère de contrôle. Le code qui parcourt la liste d'interprétations doit lui aussi
tolérer son absence plutôt que d'indexer par position.

### Identifiants de carte SIM (EID / ICCID)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Catégorie d'activité | AI 8042 |
| `EID_BODY` | Corps de l'EID | AI 8042 |
| `EID_CHECK_DIGIT` | Chiffre de contrôle | AI 8042 |
| `ICCID_BODY` | Corps de l'ICCID | AI 8043 |
| `ICCID_EXTENSION` | Extension | AI 8043 |

`SIM_MII` porte les **deux** premiers chiffres (`89`), la paire que l'UIT-T E.118 attribue aux
télécommunications. L'ISO/CEI 7812 définit quant à elle le MII comme le **premier chiffre seulement** :
`SIM_MII_NAME` résout donc la catégorie à partir de ce `8` initial via `Iso7812Data` — ce qui donne
« Healthcare, telecommunications and other future industry assignments ». Pour un EID bien formé,
cette valeur est donc constante ; elle est rapportée pour la traçabilité vis-à-vis de la norme, non comme
critère de discrimination. `Iso7812Data.nameForCode(digit)` prend un chiffre isolé,
`nameForIdentifier(prefix)` accepte un préfixe plus long et en lit le premier chiffre.

`SIM_MII_NAME` n'est émis que par `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
expose `SIM_MII` sans la catégorie.

### Référence de certification

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Numéro de séquence | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Code du schéma de certification | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Schéma de certification | AI 7230–7239 |
| `CERT_REFERENCE` | Référence de certification | AI 7230–7239 |

### GS1 UIC

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `UIC_CODE` | Code UIC | AI 7040 |
| `UIC_EXTENSION_1` | Extension 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Indice d'importateur | AI 7040 |

### Rang de naissance du nouveau-né

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Position de naissance | AI 7258 |
| `BIRTH_TOTAL` | Nombre total de naissances | AI 7258 |
| `BIRTH_SEQUENCE` | Séquence de naissance | AI 7258 |

### Numéro de modèle mondial (GMN)

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Référence du modèle | AI 8013 |
| `GMN_CHECK_PAIR` | Paire de contrôle | AI 8013 |

### HIDRI

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Référence du dispositif | AI 8014 |
| `HIDRI_CHECK_PAIR` | Paire de contrôle | AI 8014 |

### CPID

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Référence de composant et pièce | AI 8010–8011 |

### Valeurs décimales et de mesure

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Valeur décimale | AI numériques à décimales implicites (31xx–36xx) |
| `DECIMAL_AMOUNT` | Montant | AI de prix (390n–395n) |
| `DECIMAL_PERCENTAGE` | Pourcentage | AI 394n |
| `DECIMAL_PLACES` | Décimales | Aux côtés de `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Format de pourcentage | AI 394n |
| `ISO_UNIT_CODE` | Code d'unité ISO | AI de mesure |
| `ISO_UNIT_NAME` | Nom d'unité ISO | AI de mesure |
| `MONETARY_AMOUNT` | Montant monétaire | AI de prix |
| `MONETARY_AMOUNT_DISPLAY` | Montant monétaire (formaté) | AI de prix |

### Coordonnées géographiques

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `LATITUDE` | Latitude | AI 4309 |
| `LONGITUDE` | Longitude | AI 4309 |
| `GEO_COORDINATES` | Coordonnées géographiques | AI 4309 |
| `LATITUDE_DMS` | Latitude (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitude (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Coordonnées géographiques (DMS) | AI 4309 |

### Méthode de production

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Code de méthode de production | AI 7010 |
| `PRODUCTION_METHOD` | Méthode de production | AI 7010 |

### Type de support AIDC

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Code de type de média AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Type de média AIDC | AI 7241 |

### Pièce sur total

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Numéro de pièce | AI 8006 |
| `PIECE_TOTAL` | Nombre total de pièces | AI 8006 |
| `PIECE_OF_TOTAL` | Pièce sur total | AI 8006 |

### Découpages en composants

Clés émises par les découpages en composants déclaratifs de `content/ai-content.json` plutôt que
par un enrichisseur Java — elles font apparaître les parties nommées d'une valeur d'AI composite. Contrairement à toutes
les autres clés de cette annexe, celles-ci n'ont **aucune constante dans `GS1Constants_Enricher`** : comparez
la chaîne littérale, ou lisez le type via `GS1AIInterpretation.getType()`.

| Clé de type | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Chiffre de contrôle | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Numéro de série | AI 253, 255, 8003 |
| `POSTAL_CODE` | Code postal | AI 421 |
| `PROCESSOR_ID` | Identifiant du transformateur | AI 7030–7039 |

Notez que `CHECK_DIGIT` est ici la clé générique de découpage en composants, distincte des clés
spécifiques aux enrichisseurs `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` et
`EID_CHECK_DIGIT` énumérées plus haut.

### Divers

| Constante de clé | Étiquette d'affichage | Produite par |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Valeur | AI booléens / indicateurs (4321–4323) |
| `DECODED_TEXT` | Texte décodé | AI de texte libre |
