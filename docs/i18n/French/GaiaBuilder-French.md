# GaiaBuilder — Guide du développeur

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [À propos de GS1 et des spécifications générales](#à-propos-de-gs1-et-des-spécifications-générales)
3. [Démarrage rapide](#démarrage-rapide)
4. [Fonctionnement](#fonctionnement)
5. [Construire des chaînes d'éléments](#construire-des-chaînes-déléments)
   - [Les AI d'attribut exigent leur clé d'identification](#les-ai-dattribut-exigent-leur-clé-didentification)
6. [Construire des URI Digital Link](#construire-des-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validation et erreurs](#validation-et-erreurs)
   - [Méthodes de construction levant des exceptions](#méthodes-de-construction-levant-des-exceptions)
   - [Méthodes tryBuild\* sans exception](#méthodes-trybuild-sans-exception)
   - [Langue des messages d'erreur](#langue-des-messages-derreur)
   - [BuildResult](#buildresult)
9. [Chiffres de contrôle](#chiffres-de-contrôle)
10. [Sûreté vis-à-vis des threads](#sûreté-vis-à-vis-des-threads)
11. [Référence de l'API](#référence-de-lapi)

---

## Vue d'ensemble

`GaiaBuilder` est l'inverse de [`GaiaParser`](GaiaParser-French.md) : il transforme un ensemble de couples identifiant de données (AI) / valeur en une **chaîne d'éléments** GS1 ou en un **URI GS1 Digital Link** bien formé. Vous fournissez les AI et leurs valeurs de données complètes ; le constructeur les assemble, valide le résultat au moyen du moteur qu'utilise `GaiaParser`, puis produit la sortie.

Comme le constructeur valide en *analysant sa propre sortie candidate*, tout ce qu'il renvoie est garanti d'être analysé sans erreur par `GaiaParser` — les deux ne peuvent jamais diverger sur ce qui est bien formé.

**Classe du point d'entrée :** `tools.pantheum.gaia.GaiaBuilder`

---

## À propos de GS1 et des spécifications générales

**GS1** est une organisation mondiale à but non lucratif qui élabore et maintient des normes ouvertes pour l'identification et l'échange de données dans les chaînes d'approvisionnement. Ses normes sont utilisées dans la distribution, la santé, la logistique, la restauration et de nombreux autres secteurs, du code-barres produit sur un emballage grand public au suivi sérialisé des doses pharmaceutiques.

La référence faisant autorité pour tout ce que ce constructeur met en œuvre est le document **GS1 General Specifications** — un document unique qui définit :

- Tous les codes d'identifiants de données (AI), leurs titres de données, leurs formats et leurs règles de validation
- Les règles de syntaxe pour construire et encoder les chaînes d'éléments AI
- Les exigences de symbologie des codes-barres et l'attribution des identifiants de symbologie AIM
- Les algorithmes de chiffre de contrôle et de caractère de contrôle
- La résolution des années à deux chiffres (règle de la fenêtre glissante)
- Les spécifications Data Matrix, QR Code, GS1-128, GS1 DataBar et autres supports de données

Les GS1 General Specifications sont mises à jour chaque année. L'édition en vigueur et les ressources associées sont disponibles à l'adresse :

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA met en œuvre la **version 26.0 (ratifiée en janvier 2026)** des GS1 General Specifications.

Les URI GS1 Digital Link sont régis par une norme complémentaire, **GS1 Digital Link: URI Syntax**, qui définit les clés d'identification primaires, l'ordre des qualificatifs de clé et l'encodage des attributs de données que le constructeur applique lors du rendu des URI Digital Link :

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA met en œuvre la **version 1.7.0 (ratifiée en août 2026)** de la norme GS1 Digital Link: URI Syntax.

Dans tout ce document, les références de section renvoient aux GS1 General Specifications (par exemple « Table 7-5 », « section 7.12 »), à l'exception des numéros de section Digital Link (par exemple « §4.9 », « §4.12 »), qui renvoient à la norme GS1 Digital Link: URI Syntax.

---

## Démarrage rapide

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

Préférez les constantes `GS1Constants_AICodes` aux chaînes d'AI brutes (voir l'[annexe A du guide de l'analyseur](GaiaParser-French.md#annexe-a--constantes-de-chaînes-dai)) :

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Fonctionnement

Toute construction suit le même parcours :

1. **Assemblage** — les couples AI/valeur sont concaténés en une chaîne d'éléments candidate. Un séparateur de groupe FNC1 (`0x1D`) est inséré après chaque AI qui *requiert un séparateur* et qui n'est pas le dernier élément. Les AI de longueur prédéfinie (GTIN, dates, mesures de longueur fixe) ne prennent pas de séparateur ; tous les autres en prennent un. (Les AI non reconnus n'atteignent jamais cette étape — `ai(...)` les rejette immédiatement ; voir [Construire des chaînes d'éléments](#construire-des-chaînes-déléments).)
2. **Validation** — la chaîne candidate est analysée en mode `CONTENT` par `GaiaParser`. Chaque valeur est confrontée au format et au chiffre de contrôle de son AI, et les règles structurelles (associations d'AI requises ou interdites) sont appliquées. Si l'analyse n'est pas valide, la construction échoue.
3. **Rendu** —
   - Pour une chaîne d'éléments, le `toElementString()` de l'objet validé est renvoyé.
   - Pour un Digital Link, chaque élément se voit attribuer son rôle DL (clé primaire, qualificatif de clé ou attribut de données), la séquence de qualificatifs de clé est validée, l'URI est émis, puis **réanalysé pour confirmer qu'il fait un aller-retour valide en tant que Digital Link** — un contrôle défensif sur l'assemblage de la chaîne et l'encodage en pourcentage. Si l'aller-retour échoue, une `GaiaBuilderException` est levée.

Cette démarche reproduit la logique de reconstruction de `DLSyntaxParser` : le placement des séparateurs et la validation sont donc identiques à ce qu'attend l'analyseur.

---

## Construire des chaînes d'éléments

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- L'**AI** est validé immédiatement : `ai(...)` lève une `IllegalArgumentException` s'il ne s'agit pas d'un identifiant de données GS1 reconnu. (Le constructeur concatène l'AI et la valeur avant l'analyse ; un AI non reconnu ou trop long tel que `"99999"` doit donc être intercepté ici — sans quoi il serait silencieusement redécoupé en un AI différent.) La **valeur**, elle, est validée plus tard, au moment de la construction.
- Les valeurs doivent être **complètes**, chiffre de contrôle compris. Le constructeur ne calcule ni n'ajoute les chiffres de contrôle à votre place — voir [Chiffres de contrôle](#chiffres-de-contrôle).
- Les AI sont émis dans l'ordre où vous les ajoutez. Le constructeur insère les séparateurs FNC1 là où la syntaxe GS1 l'exige ; vous n'avez pas à les ajouter vous-même.
- Une construction **sans aucun AI** lève `GaiaBuilderException("No AIs supplied")` avec une liste `getErrors()` vide — le seul échec qui ne porte aucun `GaiaError`.
- Un AI dont la valeur enfreint sa règle de format ou de chiffre de contrôle fait échouer la construction.

### Les AI d'attribut exigent leur clé d'identification

La plupart des AI sont des *attributs* que les GS1 General Specifications exigent d'accompagner d'une clé d'identification, et le constructeur applique cette règle — il valide via l'étape de syntaxe complète, sans possibilité de s'y soustraire. Un numéro de lot ou un numéro de série isolé n'est **pas** une chaîne d'éléments valide :

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Les clés d'identification (GTIN `01`, SSCC `00`, GLN `414`, …) et les AI à usage interne d'entreprise (`90`–`99`) peuvent tout à fait figurer seuls. Tout le reste a besoin de son AI compagnon.

> On peut demander à `GaiaParser` d'ignorer ce contrôle avec `ParseConfig.skipRequiresCheck(true)` ; `GaiaBuilder` n'expose délibérément aucun équivalent — il est fait pour produire une sortie conforme aux normes. Pour assembler une chaîne d'éléments volontairement partielle, concaténez-la vous-même et analysez-la avec le contrôle désactivé.

---

## Construire des URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Un Digital Link valide exige exactement une **clé d'identification primaire** (par exemple GTIN `01`, GLN `414`, SSCC `00`). Le constructeur classe chaque AI fourni :

| Rôle | Rendu sous forme de | Exemple |
|------|-------------|---------|
| Clé d'identification primaire | Segment de chemin après le domaine ou le préfixe | `/01/09506000134352` |
| Qualificatif de clé (CPV `22`, lot `10`, numéro de série `21`, …) | Segments de chemin suivants, dans l'**ordre canonique du §4.9** (et non dans l'ordre d'ajout) | `/10/LOT-ABC` |
| Attribut de données (tout le reste) | Paramètres de requête, **triés lexicalement par clé d'AI** (§4.12) | `?17=271231` |

Comme les qualificatifs sont réordonnés à l'émission, les fournir dans le désordre ne pose aucun problème — `ai("21", …)` avant `ai("10", …)` produit tout de même `/10/LOT/21/SER`. Seul l'*ensemble* doit être admissible pour la clé primaire.

Les valeurs, tant dans le chemin que dans la requête, sont encodées en pourcentage.

La construction **échoue** (elle lève une `GaiaBuilderException`, ou renvoie un `BuildResult` en échec) lorsque :

- il n'y a **aucune** clé d'identification primaire parmi les AI ;
- il y a **plus d'une** clé d'identification primaire ;
- un AI est **interdit** dans les Digital Links (`03`, `8014`) ;
- la **séquence de qualificatifs de clé** est invalide pour la clé primaire choisie (par exemple un qualificatif qui n'appartient pas à cette clé, ou des qualificatifs hors de l'ordre autorisé).

---

## BuilderDigitalLinkConfig

Passez un `BuilderDigitalLinkConfig` pour contrôler le schéma, le domaine, le préfixe de chemin, les paramètres de requête supplémentaires et le fragment :

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

| Méthode du constructeur | Rôle | Défaut |
|----------------|---------|---------|
| `scheme(String)` | Schéma d'URI ; doit valoir `http` ou `https` | `https` |
| `domain(String)` | Autorité — hôte ou `hôte:port` | `id.gs1.org` |
| `pathPrefix(String)` | Segments de chemin précédant la première clé primaire ; les barres obliques de début et de fin sont normalisées | *(aucun)* |
| `baseUrl(String)` | Raccourci qui décompose une URL en `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Paramètre de requête supplémentaire, ajouté **après** les attributs de données d'AI, dans l'ordre d'insertion ; encodé en pourcentage | — |
| `fragment(String)` | Fragment d'URL (sans le `#` initial) ; encodé en pourcentage | *(aucun)* |

`build()` valide la configuration immédiatement : un schéma autre que `http(s)` ou un domaine vide lève une `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) correspond au réglage par défaut `https://id.gs1.org` sans supplément — exactement ce qu'utilise `buildDigitalLinkUri()` sans argument, et ce que produit `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → schéma `http`, domaine `id.example.org:8080`, préfixe de chemin `/r`.
- Les paramètres de requête supplémentaires suivent toujours les attributs issus des AI, ce qui préserve l'ordre canonique des AI (§4.12).

`BuilderDigitalLinkConfig` est immuable ; réutilisez une même instance sans réserve.

---

## Validation et erreurs

### Méthodes de construction levant des exceptions

`buildElementString()`, `buildDigitalLinkUri()` et `buildDigitalLinkUri(BuilderDigitalLinkConfig)` lèvent une **`GaiaBuilderException`** (une `RuntimeException` non contrôlée) lorsque les AI ne peuvent pas former une sortie bien formée :

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- En cas d'échec de **contenu** (chiffre de contrôle incorrect, format non conforme, AI manquant ou interdit), `getErrors()` porte les `GaiaError` de l'analyseur — les objets mêmes [décrits dans le guide de l'analyseur](GaiaParser-French.md#gaiaerror).
- En cas d'échec **structurel Digital Link** (aucune clé primaire, plusieurs clés primaires, AI interdit, séquence de qualificatifs de clé invalide), `getErrors()` porte un unique `GaiaError` (code `GE-L008`, `GE-L012`, `GE-L013` ou `GE-L014`), localisé dans la langue du constructeur.

### Méthodes tryBuild\* sans exception

Lorsque l'entrée provient de l'utilisateur et que l'échec est un résultat attendu et récupérable, utilisez les variantes `tryBuild*` plutôt qu'un flux de contrôle par exceptions. Elles renvoient un [`BuildResult`](#buildresult) au lieu de lever une exception :

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

| Avec exception | Sans exception |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Chaque méthode `tryBuild*` partage le même noyau de validation que sa jumelle à exception ; seule la frontière d'échec diffère.

### Langue des messages d'erreur

Les erreurs de validation de contenu proviennent du catalogue d'erreurs localisé. Appelez `language(...)` pour choisir la langue des messages des `GaiaError` portés par `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` ; la valeur par défaut est l'anglais :

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Il s'agit du même réglage `GaiaConstants.Language` que `GaiaParser` accepte via `ParseConfig` : le constructeur et l'analyseur se localisent donc à l'identique.

Les messages des `GaiaError` de **contenu** comme les échecs **structurels Digital Link** (aucune clé primaire, plusieurs clés primaires, AI interdit, séquence de qualificatifs de clé invalide) sont localisés au moyen du catalogue d'erreurs partagé — ces derniers via les codes `GE-L008`, `GE-L012`, `GE-L013` et `GE-L014`.

### BuildResult

`BuildResult` (dans le paquet `tools.pantheum.gaia.result`) est un type valeur immuable décrivant l'issue d'un appel `tryBuild*` :

| Méthode | En cas de succès | En cas d'échec |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | la chaîne produite | `null` |
| `getMessage()` | `null` | description de l'échec |
| `getErrors()` | liste vide | les erreurs de validation (les mêmes que `GaiaBuilderException.getErrors()`) |

---

## Chiffres de contrôle

Le constructeur valide les chiffres de contrôle mais ne les calcule **pas** — les valeurs doivent déjà inclure le leur. Pour en calculer un, utilisez `GS1Utils.calculateCheckDigit` :

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` applique l'algorithme GS1 standard modulo 10 aux chiffres fournis et renvoie le chiffre de contrôle `0–9`, ou `-1` si l'entrée est nulle, vide ou non numérique.

---

## Sûreté vis-à-vis des threads

`GaiaBuilder` n'est **pas** sûr vis-à-vis des threads et est prévu pour un usage unique : appelez `create()`, ajoutez les AI, construisez une fois. Créez un nouveau constructeur par sortie ; n'en partagez pas un entre plusieurs threads.

`BuilderDigitalLinkConfig` (et les `BuildResult` qu'il produit) sont immuables et peuvent être partagés sans réserve — construisez une configuration une fois au démarrage et réutilisez-la pour de nombreux constructeurs.

---

## Référence de l'API

### `GaiaBuilder`

| Méthode | Description |
|--------|-------------|
| `static GaiaBuilder create()` | Démarre un nouveau constructeur, vide. |
| `GaiaBuilder ai(String ai, String value)` | Ajoute un AI et sa valeur complète. Lève une `IllegalArgumentException` si l'un des deux est `null`, ou si `ai` n'est pas un identifiant de données GS1 reconnu. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Définit la langue des messages d'erreur de validation du contenu (anglais par défaut). `null` est ignoré. |
| `String buildElementString()` | Produit une chaîne d'éléments GS1. Lève une `GaiaBuilderException` en cas d'échec. |
| `String buildDigitalLinkUri()` | Produit un URI Digital Link canonique. Lève une `GaiaBuilderException` en cas d'échec. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Produit un URI Digital Link selon `config`. Lève une `GaiaBuilderException` en cas d'échec. |
| `BuildResult tryBuildElementString()` | Construction d'une chaîne d'éléments sans exception. |
| `BuildResult tryBuildDigitalLinkUri()` | Construction d'un Digital Link canonique sans exception. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Construction d'un Digital Link selon `config`, sans exception. |

### `BuilderDigitalLinkConfig`

| Membre | Description |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | Le réglage par défaut `https://id.gs1.org`. |
| `static Builder builder()` | Un nouveau constructeur de configuration. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Schéma, autorité et préfixe de chemin résolus. |
| `getExtraQueryParams()` | Paramètres de requête supplémentaires, dans l'ordre d'insertion. |
| `getFragment()` | Fragment, ou `null`. |

### `GaiaBuilderException`

| Membre | Description |
|--------|-------------|
| `getErrors()` | Les `GaiaError` à l'origine de l'échec — les erreurs de l'analyseur pour un échec de contenu, ou une unique erreur structurelle Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Jamais `null`. |

### `BuildResult`

| Membre | Description |
|--------|-------------|
| `isSuccess()` | Indique si la construction a réussi. |
| `getValue()` | La sortie produite en cas de succès ; `null` en cas d'échec. |
| `getMessage()` | La description de l'échec en cas d'échec ; `null` en cas de succès. |
| `getErrors()` | Les erreurs de validation en cas d'échec ; liste vide en cas de succès. Jamais `null`. |
| `getTiming()` | Le `ProcessingTiming` de la construction (heure de début, durée de traitement), ou `null`. |

---

Voir également : **[GaiaParser — Guide du développeur](GaiaParser-French.md)** pour le versant analyse, le modèle d'élément AI, la référence des erreurs et les annexes de constantes d'AI et d'interprétation.
