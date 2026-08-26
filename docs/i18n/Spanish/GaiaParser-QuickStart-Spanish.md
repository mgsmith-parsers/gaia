# GaiaParser — inicio rápido

Convierta la carga útil de un código de barras GS1 en datos estructurados, validados y legibles por personas
en unos diez minutos. Este es el camino corto; la **[guía del desarrollador de GaiaParser](GaiaParser-Spanish.md)** es la
referencia completa, y **[GaiaBuilder](GaiaBuilder-Spanish.md)** cubre el sentido inverso
(construir cadenas de elementos y URI de Digital Link).

## Contenido

1. [Añadir Gaia a su proyecto](#1-añadir-gaia-a-su-proyecto)
2. [Analizar algo](#2-analizar-algo)
3. [Leer el resultado](#3-leer-el-resultado)
4. [Tratar un análisis fallido](#4-tratar-un-análisis-fallido)
5. [Dos cosas que le harán tropezar](#5-dos-cosas-que-le-harán-tropezar)
6. [Los prefijos de lector y los Digital Link funcionan sin más](#6-los-prefijos-de-lector-y-los-digital-link-funcionan-sin-más)
7. [Hacer menos trabajo: los modos de análisis](#7-hacer-menos-trabajo-los-modos-de-análisis)
8. [Cambiar el idioma y el formato de fecha](#8-cambiar-el-idioma-y-el-formato-de-fecha)
9. [Limpiar entradas mal formadas](#9-limpiar-entradas-mal-formadas)
10. [Adónde ir después](#10-adónde-ir-después)

---

## 1. Añadir Gaia a su proyecto

Gaia no se publica en Maven Central, así que compile el módulo principal una vez e instálelo en su
repositorio local:

```bash
cd gaia && mvn install
```

Después, declare la dependencia:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

Esa es toda la lista de dependencias que tiene que escribir. El jar es ligero, de modo que la única
dependencia de ámbito de compilación de Gaia —`com.fasterxml.jackson.core:jackson-databind`— llega
de forma transitiva; si su compilación ya fija una versión de Jackson, esa fijación gana y Gaia la usa.
Gaia tiene como objetivo **Java 11**, y el mismo jar funciona sin cambios en cualquier JVM posterior.

> Omitir la batería de pruebas del módulo principal (`mvn install -DskipTests`) convierte unos minutos en unos pocos
> segundos mientras da sus primeros pasos.

---

## 2. Analizar algo

Una sola clase, sin configuración:

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

`parse(String)` ejecuta la cadena **completa**: sintaxis, validación de contenido e interpretación.
Ese es el valor predeterminado correcto; redúzcalo más adelante si mide alguna razón para hacerlo.

---

## 3. Leer el resultado

`ParseResult.getAiObject()` contiene los AI resueltos. Acceda a uno concreto por su código y no
por su posición:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Cada elemento lleva una lista de **interpretaciones**: el significado descodificado tras los dígitos en bruto,
producido por la etapa de interpretación:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` está localizado y está pensado para presentación. Para *leer* un valor en código, búsquelo
por su clave de tipo, que es estable:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

Cada AI produce claves distintas: un GTIN da su prefijo de empresa, su tipo de GTIN y su dígito de
control; un precio da la moneda y el importe decimal. La lista completa está en el
[apéndice B](GaiaParser-Spanish.md#apéndice-b--constantes-de-clave-de-interpretación), y las constantes residen
en `GS1Constants_Enricher`. No todos los AI tienen interpretaciones: un lote de texto libre no tiene
nada que deducir, así que su lista está vacía.

---

## 4. Tratar un análisis fallido

Una carga útil no válida es un resultado normal, no una excepción: `parse` nunca lanza excepciones por datos
GS1 incorrectos:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Bifurque según `getId()`, nunca según el mensaje.** Los mensajes están localizados y su redacción
no es un contrato, y además arrastran un defecto conocido de entrecomillado (el `''` duplicado anterior),
señalado en la [referencia de errores](GaiaParser-Spanish.md#referencia-de-errores).

Dos preguntas distintas, dos métodos distintos:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Un análisis deja de descender en cuanto falla una etapa, de modo que un dígito de control incorrecto le da
errores de validación pero ninguna interpretación.

### Las advertencias no invalidan un resultado

Algunas comprobaciones son orientativas. Un prefijo de empresa GS1 no reconocido se informa, pero la carga útil
sigue siendo estructuralmente correcta:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Use `getIssues()` cuando quiera ambos. Si su flujo de trabajo debe rechazar los prefijos desconocidos, consulte
`getWarnings()` explícitamente: `isValid()` no lo hará por usted.

---

## 5. Dos cosas que le harán tropezar

### El separador GS, y por qué omitirlo es peor que un error

Un AI de longitud variable se extiende hasta un **carácter GS** (ASCII `0x1D`, llamado FNC1 en las
simbologías de códigos de barras) o hasta el final de la cadena. Cuando le sigue otro AI, ese separador es
obligatorio:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Omítalo y **no** obtendrá un error: obtendrá una respuesta equivocada dada con toda seguridad:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

El AI `10` es `X..20`, así que se traga legítimamente `LOT-ABC21SN-98765` y el analizador no tiene
forma de saber que eso no era lo que se pretendía. Nada aguas abajo puede recuperarlo, de modo que ponga bien el
separador en el origen: lea los bytes del lector como **ISO-8859-1** para que `0x1D` sobreviva, y escriba
`""` en los literales de cadena de Java. Los AI de longitud fija (`01`, `17`, `3103`) no necesitan separador:
el analizador conoce su longitud.

### La mayoría de los AI no pueden ir solos

El lote, el número de serie, la caducidad y sus compañeros son *atributos*: las GS1 General Specifications
exigen que viajen con una clave de identificación, y Gaia lo hace cumplir.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Añada el GTIN y pasará. Si realmente necesita analizar un fragmento —una prueba unitaria, una
lectura parcial—, desactive la comprobación:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Los prefijos de lector y los Digital Link funcionan sin más

No tiene que indicarle a Gaia qué forma tiene la entrada: detecta las cuatro. Deles lo que le haya
dado el lector, tal cual.

**Un prefijo de identificador de simbología AIM** identifica la simbología y se retira automáticamente:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Un URI de GS1 Digital Link** pasa por la misma validación y el mismo enriquecimiento:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Como ambas formas acaban en el mismo `GS1AIObject`, el código que consume una lectura no necesita
preocuparse por cuál llegó, y `toElementString()` / `getCanonicalDigitalLink()`
convierten de una a otra.

Un **prefijo de correlación de 8 dígitos** (`12345678~…`) también se retira y se conserva en
`getCorrelationInfo()`, si su cadena de procesamiento utiliza uno.

---

## 7. Hacer menos trabajo: los modos de análisis

El modo predeterminado lo hace todo. Pida menos cuando solo necesite parte de la respuesta:

| Modo | Responde a | Coste |
|---|---|---|
| `DATA_CARRIER` | ¿De qué simbología se trata? | El más barato: sin análisis de AI, `getAiObject()` es `null` |
| `SYNTAX` | ¿Están bien formados los códigos AI y las longitudes? | Sin dígitos de control ni interpretaciones |
| `CONTENT` | ¿Son datos GS1 válidos? | Validación completa, sin interpretaciones |
| `INTERPRETATION` | ¿Qué significan? | **Predeterminado**: todo |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Recurra a `CONTENT` cuando valide en volumen y nunca muestre el desglose, y a
`DATA_CARRIER` cuando solo necesite encaminar una lectura hacia el manejador adecuado.

---

## 8. Cambiar el idioma y el formato de fecha

Los mensajes de error, las etiquetas de interpretación y las descripciones de AI están traducidos a **35
idiomas**; las fechas se representan como usted quiera. Todo ello cabe en un único `ParseConfig` inmutable:

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

Los valores nunca se localizan —solo las etiquetas, las descripciones y los mensajes—, de modo que `"2026-12-31"` y
`"09506000134352"` significan lo mismo en todos los idiomas. Construya la configuración una vez al arrancar
y compártala; es inmutable.

---

## 9. Limpiar entradas mal formadas

Si su fuente emite paréntesis de HRI impresos o espacios sueltos, el módulo principal incluye dos
**modificadores de entrada** que reparan la carga útil antes del análisis:

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

No hay nada activado de forma predeterminada, y ambos tienen salvedades: el espacio y los paréntesis son caracteres
de datos GS1 lícitos, así que aplíquelos únicamente a una fuente que conozca. Véase
[Modificadores integrados](GaiaParser-Spanish.md#modificadores-integrados), que explica además por qué la eliminación de
paréntesis debe restituir el separador que estos implicaban.

---

## 10. Adónde ir después

- **[Guía del desarrollador de GaiaParser](GaiaParser-Spanish.md)** — la cadena de procesamiento en detalle, el modelo de
  resultado completo, todos los códigos de error y los apéndices de AI y de claves de interpretación.
- **[Guía del desarrollador de GaiaBuilder](GaiaBuilder-Spanish.md)** — construir cadenas de elementos y URI de Digital
  Link a partir de pares AI/valor.
- **[Referencia HTTP de la API de Gaia](../../gaia-api-reference.md)** — el mismo motor sobre HTTP, si prefiere
  no incrustar la biblioteca.
- **[ai-codes.txt](../../ai-codes.txt)** — un listado plano `(AI) TÍTULO` para consultas rápidas.

### La versión en cinco líneas

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
