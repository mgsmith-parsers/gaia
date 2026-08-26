# GaiaParser — démarrage rapide

Analysez une charge utile de code-barres GS1 en données structurées, validées et lisibles par l'humain
en une dizaine de minutes. Voici la voie courte ; le **[guide du développeur GaiaParser](GaiaParser-French.md)** est la
référence complète, et **[GaiaBuilder](GaiaBuilder-French.md)** couvre le sens inverse
(la construction de chaînes d'éléments et d'URI Digital Link).

## Sommaire

1. [Ajouter Gaia à votre projet](#1-ajouter-gaia-à-votre-projet)
2. [Analyser quelque chose](#2-analyser-quelque-chose)
3. [Lire le résultat](#3-lire-le-résultat)
4. [Traiter une analyse en échec](#4-traiter-une-analyse-en-échec)
5. [Deux pièges qui vous attendent](#5-deux-pièges-qui-vous-attendent)
6. [Les préfixes de lecteur et les Digital Links fonctionnent d'emblée](#6-les-préfixes-de-lecteur-et-les-digital-links-fonctionnent-demblée)
7. [En faire moins : les modes d'analyse](#7-en-faire-moins--les-modes-danalyse)
8. [Changer la langue et le format de date](#8-changer-la-langue-et-le-format-de-date)
9. [Nettoyer une entrée mal formée](#9-nettoyer-une-entrée-mal-formée)
10. [Pour aller plus loin](#10-pour-aller-plus-loin)

---

## 1. Ajouter Gaia à votre projet

Gaia n'est pas publié sur Maven Central : construisez donc le module principal une fois et installez-le dans votre
dépôt local :

```bash
cd gaia && mvn install
```

Puis déclarez-en la dépendance :

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

C'est toute la liste de dépendances que vous avez à écrire. Le jar est léger : l'unique
dépendance de portée compilation de Gaia — `com.fasterxml.jackson.core:jackson-databind` — arrive
de façon transitive ; si votre build fixe déjà une version de Jackson, c'est la vôtre qui l'emporte et que Gaia utilise.
Gaia vise **Java 11**, et le même jar fonctionne tel quel sur toute JVM ultérieure.

> Ignorer la suite de tests du module principal (`mvn install -DskipTests`) fait passer quelques minutes à quelques
> secondes le temps de vos premiers pas.

---

## 2. Analyser quelque chose

Une seule classe, aucune configuration :

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

`parse(String)` exécute la chaîne **complète** : syntaxe, validation du contenu et interprétation.
C'est le bon réglage par défaut — restreignez-le plus tard si vos mesures vous en donnent une raison.

---

## 3. Lire le résultat

`ParseResult.getAiObject()` contient les AI résolus. Accédez à l'un d'eux par son code plutôt
que par sa position :

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Chaque élément porte une liste d'**interprétations** — le sens décodé derrière les chiffres bruts,
produit par l'étape d'interprétation :

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` est localisé et destiné à l'affichage. Pour *lire* une valeur dans le code, retrouvez-la plutôt
par sa clé de type, qui est stable :

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Chaque AI produit des clés différentes — un GTIN donne son préfixe d'entreprise, son type de GTIN et son chiffre
de contrôle ; un prix donne la devise et le montant décimal. La liste complète se trouve en
[annexe B](GaiaParser-French.md#annexe-b--constantes-de-clés-dinterprétation), et les constantes résident
dans `GS1Constants_Enricher`. Tous les AI n'ont pas d'interprétations : un numéro de lot en texte libre n'a
rien à en déduire, sa liste est donc vide.

---

## 4. Traiter une analyse en échec

Une charge utile invalide est un résultat normal, non une exception — `parse` ne lève jamais d'exception pour de
mauvaises données GS1 :

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Branchez sur `getId()`, jamais sur le message.** Les messages sont localisés et leur formulation
n'est pas un contrat — et ils portent actuellement un défaut de guillemets connu (le `''` doublé ci-dessus),
signalé dans la [référence des erreurs](GaiaParser-French.md#référence-des-erreurs).

Deux questions différentes, deux méthodes différentes :

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

L'analyse cesse de descendre dès qu'une étape échoue : un chiffre de contrôle incorrect vous donne donc des
erreurs de validation, mais aucune interprétation.

### Les avertissements ne rendent pas un résultat invalide

Certains contrôles sont indicatifs. Un préfixe d'entreprise GS1 non reconnu est signalé, mais la charge utile
reste structurellement saine :

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Utilisez `getIssues()` lorsque vous voulez les deux. Si votre processus doit rejeter les préfixes inconnus, examinez
explicitement `getWarnings()` — `isValid()` ne le fera pas à votre place.

---

## 5. Deux pièges qui vous attendent

### Le séparateur GS, et pourquoi l'omettre est pire qu'une erreur

Un AI de longueur variable court jusqu'à un **caractère GS** (ASCII `0x1D`, appelé FNC1 dans les
symbologies de codes-barres) ou jusqu'à la fin de la chaîne. Lorsqu'un autre AI le suit, ce séparateur est
obligatoire :

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Omettez-le et vous n'obtenez **pas** une erreur — vous obtenez une réponse fausse, donnée avec aplomb :

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

L'AI `10` est en `X..20` : il avale donc légitimement `LOT-ABC21SN-98765`, et l'analyseur n'a aucun
moyen de savoir que ce n'était pas voulu. Rien en aval ne peut rattraper cela : réglez donc le séparateur
correctement à la source — lisez les octets du lecteur en **ISO-8859-1** pour que `0x1D` survive, et écrivez
`"\u001D"` dans les littéraux de chaîne Java. Les AI de longueur fixe (`01`, `17`, `3103`) n'ont besoin d'aucun séparateur —
l'analyseur connaît leur longueur.

### La plupart des AI ne peuvent pas figurer seuls

Numéro de lot, numéro de série, date de péremption et consorts sont des *attributs* : les GS1 General Specifications
exigent qu'ils voyagent avec une clé d'identification, et Gaia applique cette règle.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Ajoutez le GTIN et l'analyse passe. Si vous avez réellement besoin d'analyser un fragment — un test unitaire, une
lecture partielle — désactivez le contrôle :

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Les préfixes de lecteur et les Digital Links fonctionnent d'emblée

Vous n'avez pas à indiquer à Gaia la forme de l'entrée — il détecte les quatre formes. Donnez-lui
ce que le lecteur vous a transmis, tel quel.

**Un préfixe d'identifiant de symbologie AIM** identifie la symbologie et est retiré automatiquement :

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Un URI GS1 Digital Link** passe par la même validation et le même enrichissement :

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Comme les deux formes aboutissent au même `GS1AIObject`, le code qui consomme une lecture n'a pas
à se soucier de celle qui est arrivée — et `toElementString()` / `getCanonicalDigitalLink()`
permettent de passer de l'une à l'autre.

Un **préfixe de corrélation à 8 chiffres** (`12345678~…`) est lui aussi retiré et conservé dans
`getCorrelationInfo()`, si votre chaîne de traitement en utilise un.

---

## 7. En faire moins : les modes d'analyse

Le mode par défaut fait tout. Demandez-en moins lorsque vous n'avez besoin que d'une partie de la réponse :

| Mode | Répond à la question | Coût |
|---|---|---|
| `DATA_CARRIER` | De quelle symbologie s'agit-il ? | Le moins cher — aucune analyse d'AI, `getAiObject()` vaut `null` |
| `SYNTAX` | Les codes AI et les longueurs sont-ils bien formés ? | Pas de chiffres de contrôle, pas d'interprétations |
| `CONTENT` | S'agit-il de données GS1 valides ? | Validation complète, pas d'interprétations |
| `INTERPRETATION` | Que signifient-elles ? | **Par défaut** — tout |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Choisissez `CONTENT` lorsque vous validez en volume sans jamais afficher le détail, et
`DATA_CARRIER` lorsque vous devez seulement aiguiller une lecture vers le bon gestionnaire.

---

## 8. Changer la langue et le format de date

Les messages d'erreur, les étiquettes d'interprétation et les descriptions d'AI sont traduits en **35
langues** ; les dates s'affichent comme vous le souhaitez. Le tout tient dans un unique `ParseConfig` immuable :

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

Les valeurs ne sont jamais localisées — seuls le sont les étiquettes, les descriptions et les messages — si bien que `"2026-12-31"` et
`"09506000134352"` signifient la même chose dans toutes les langues. Construisez la configuration une fois au démarrage
et partagez-la ; elle est immuable.

---

## 9. Nettoyer une entrée mal formée

Si votre source émet des parenthèses de HRI imprimées ou des espaces parasites, deux **modificateurs d'entrée** livrés
dans le module principal réparent la charge utile avant l'analyse :

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

Rien n'est activé par défaut, et les deux comportent des réserves — l'espace et les parenthèses sont des caractères
de données GS1 licites : ne les appliquez donc qu'à une source que vous connaissez. Voir
[Modificateurs intégrés](GaiaParser-French.md#modificateurs-intégrés), qui explique aussi pourquoi le retrait des parenthèses
doit rétablir le séparateur qu'elles impliquaient.

---

## 10. Pour aller plus loin

- **[Guide du développeur GaiaParser](GaiaParser-French.md)** — la chaîne de traitement en détail, le modèle de résultat
  complet, tous les codes d'erreur, et les annexes des AI et des clés d'interprétation.
- **[Guide du développeur GaiaBuilder](GaiaBuilder-French.md)** — construire des chaînes d'éléments et des URI Digital Link
  à partir de couples AI/valeur.
- **[Référence HTTP de l'API Gaia](../../gaia-api-reference.md)** — le même moteur exposé en HTTP, si vous
  préférez ne pas embarquer la bibliothèque.
- **[ai-codes.txt](../../ai-codes.txt)** — une liste à plat `(AI) TITRE` pour une consultation rapide.

### La version en cinq lignes

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
