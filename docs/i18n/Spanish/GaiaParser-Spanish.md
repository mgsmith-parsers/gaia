# GAIA (GS1 Application Identifiers Analyser) — Guía del desarrollador

## Índice

1. [Visión general](#visión-general)
2. [Acerca de GS1 y las especificaciones generales](#acerca-de-gs1-y-las-especificaciones-generales)
3. [Identificadores de aplicación de GS1](#identificadores-de-aplicación-de-gs1)
4. [Inicio rápido](#inicio-rápido)
5. [Cadena de procesamiento del análisis](#cadena-de-procesamiento-del-análisis)
   - [Etapa previa — modificadores de entrada](#etapa-previa--modificadores-de-entrada)
   - [Etapa 0 — identificador de correlación](#etapa-0--identificador-de-correlación)
   - [Etapa 1 — encaminamiento de la entrada](#etapa-1--encaminamiento-de-la-entrada)
   - [Etapa 2 — sintaxis](#etapa-2--sintaxis)
   - [Etapa 3 — contenido](#etapa-3--contenido)
   - [Etapa 4 — interpretación](#etapa-4--interpretación)
6. [Configuración del análisis (`ParseConfig`)](#configuración-del-análisis-parseconfig)
   - [Opciones](#opciones)
   - [Mensajes y etiquetas localizados](#mensajes-y-etiquetas-localizados)
   - [Formato de fechas](#formato-de-fechas)
7. [Modificadores de entrada](#modificadores-de-entrada)
   - [Modificadores integrados](#modificadores-integrados)
   - [Escribir un modificador](#escribir-un-modificador)
   - [Declarar modificadores](#declarar-modificadores)
   - [Examinar qué hizo un modificador](#examinar-qué-hizo-un-modificador)
   - [Gestión de fallos de un modificador](#gestión-de-fallos-de-un-modificador)
8. [Modos de análisis](#modos-de-análisis)
   - [Modo DATA_CARRIER](#modo-data_carrier)
   - [Modo SYNTAX](#modo-syntax)
   - [Modo CONTENT](#modo-content)
   - [Modo INTERPRETATION (predeterminado)](#modo-interpretation-predeterminado)
9. [Identificador de correlación](#identificador-de-correlación)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Trabajar con los resultados](#trabajar-con-los-resultados)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry y DataCarrierType](#datacarrierentry-y-datacarriertype)
12. [Referencia de errores](#referencia-de-errores)
13. [Seguridad frente a hilos](#seguridad-frente-a-hilos)
14. [Apéndice A — constantes de cadena de AI](#apéndice-a--constantes-de-cadena-de-ai)
    - [Identificación y serialización](#identificación-y-serialización)
    - [Fechas y horas](#fechas-y-horas)
    - [Cantidad y medida — medida variable (métrico)](#cantidad-y-medida--medida-variable-métrico)
    - [Cantidad y medida — medida variable (imperial / EE. UU.)](#cantidad-y-medida--medida-variable-imperial--ee-uu)
    - [Precios e importes monetarios](#precios-e-importes-monetarios)
    - [Ubicación y expedición](#ubicación-y-expedición)
    - [Atributos de producto y trazabilidad](#atributos-de-producto-y-trazabilidad)
    - [Números nacionales de reembolso sanitario (NHRN)](#números-nacionales-de-reembolso-sanitario-nhrn)
    - [Sanidad, GMN, HIDRI, CPID, datos personales](#sanidad-gmn-hidri-cpid-datos-personales)
    - [Uso interno / de empresa](#uso-interno--de-empresa)
15. [Apéndice B — constantes de clave de interpretación](#apéndice-b--constantes-de-clave-de-interpretación)
    - [Fecha y hora](#fecha-y-hora)
    - [Fecha de cosecha](#fecha-de-cosecha)
    - [Prefijo de empresa GS1](#prefijo-de-empresa-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [País (ISO 3166)](#país-iso-3166)
    - [Moneda (ISO 4217)](#moneda-iso-4217)
    - [Temperatura](#temperatura)
    - [Sexo (ISO 5218)](#sexo-iso-5218)
    - [Especies acuáticas (FAO)](#especies-acuáticas-fao)
    - [Número de catálogo OTAN (NSN)](#número-de-catálogo-otan-nsn)
    - [Productos en rollo](#productos-en-rollo)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identificadores de SIM (EID / ICCID)](#identificadores-de-sim-eid--iccid)
    - [Referencia de certificación](#referencia-de-certificación)
    - [GS1 UIC](#gs1-uic)
    - [Orden de nacimiento del recién nacido](#orden-de-nacimiento-del-recién-nacido)
    - [Número mundial de modelo (GMN)](#número-mundial-de-modelo-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Valores decimales y de medida](#valores-decimales-y-de-medida)
    - [Coordenadas geográficas](#coordenadas-geográficas)
    - [Método de producción](#método-de-producción)
    - [Tipo de soporte AIDC](#tipo-de-soporte-aidc)
    - [Pieza del total](#pieza-del-total)
    - [Divisiones en componentes](#divisiones-en-componentes)
    - [Varios](#varios)

---

## Visión general

`GaiaParser` es el punto de entrada para analizar cadenas de elementos con identificadores de aplicación (AI) de GS1. Acepta la salida en bruto de un lector en cualquiera de las formas siguientes y devuelve un `ParseResult` estructurado que contiene todos los AI resueltos, los errores de validación y, opcionalmente, las interpretaciones legibles por personas:

- Cadena de elementos AI simple: `0109506000134352`
- Cadena de elementos con prefijo de identificador de simbología AIM: `]C10109506000134352`
- URI de GS1 Digital Link: `https://example.com/01/09506000134352`
- Cualquiera de las anteriores, precedida opcionalmente por un identificador de correlación de 8 dígitos: `12345678~0109506000134352`

**Clase del punto de entrada:** `tools.pantheum.gaia.GaiaParser`

> **¿Empieza con Gaia?** Comience por la **[guía de inicio rápido de GaiaParser](GaiaParser-QuickStart-Spanish.md)** — la dependencia, un primer análisis y los pocos escollos habituales, en unos diez minutos. Esta guía es la referencia completa.

> Para la operación inversa — la *construcción* de cadenas de elementos y URI de Digital Link bien formados a partir de pares AI/valor — consulte la **[guía del desarrollador de GaiaBuilder](GaiaBuilder-Spanish.md)**.

---

## Acerca de GS1 y las especificaciones generales

**GS1** es una organización mundial sin ánimo de lucro que desarrolla y mantiene estándares abiertos para la identificación y el intercambio de datos en la cadena de suministro. Sus estándares se utilizan en distribución, sanidad, logística, restauración y muchos otros sectores, y abarcan desde los códigos de barras de los productos de consumo hasta el seguimiento serializado de dosis farmacéuticas.

La referencia autorizada para todo lo que implementa este analizador es el documento **GS1 General Specifications**, un único documento que define:

- Todos los códigos de identificador de aplicación (AI), sus títulos de datos, formatos y reglas de validación
- Las reglas de sintaxis para construir y codificar cadenas de elementos AI
- Los requisitos de simbología de códigos de barras y la asignación de identificadores de simbología AIM
- Los algoritmos de dígito de control y de carácter de control
- La resolución de años de dos dígitos (la regla de la ventana deslizante)
- Las especificaciones de Data Matrix, QR Code, GS1-128, GS1 DataBar y otros portadores de datos

Las GS1 General Specifications se actualizan anualmente. La edición vigente y los recursos complementarios están disponibles en:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementa la **versión 26.0 (ratificada en enero de 2026)** de las GS1 General Specifications.

Los URI de GS1 Digital Link se rigen por un estándar complementario, **GS1 Digital Link: URI Syntax**, que define las claves de identificación primarias, el orden de los calificadores de clave y la codificación de los atributos de datos que el analizador aplica a las entradas de tipo Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementa la **versión 1.7.0 (ratificada en agosto de 2026)** del estándar GS1 Digital Link: URI Syntax.

En todo este documento, las referencias a secciones remiten a las GS1 General Specifications (por ejemplo, «Table 7-5», «section 7.12»), salvo los números de sección de Digital Link (por ejemplo, «§4.9», «§4.12»), que remiten al estándar GS1 Digital Link: URI Syntax.

---

## Identificadores de aplicación de GS1

Un **identificador de aplicación (AI) de GS1** es un prefijo numérico corto —de dos a cuatro dígitos— que identifica el significado y el formato del dato que le sigue inmediatamente. Los AI están definidos en las GS1 General Specifications y abarcan una amplia gama de datos de la cadena de suministro: identificadores de producto, fechas, cantidades, números de lote, números de serie, medidas, URL y mucho más.

### Estructura de un elemento AI

Cada elemento AI consta de dos partes:

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

El código AI es siempre numérico. El valor de datos le sigue inmediatamente, sin ningún delimitador entre el código y el valor.

### AI de longitud fija y de longitud variable

Los AI se dividen en dos categorías:

| Tipo | Comportamiento | Ejemplo |
|---|---|---|
| **Longitud fija** | Número exacto de caracteres, siempre consumido por completo | AI `01` (GTIN) — siempre 14 dígitos |
| **Longitud variable** | Desde 1 carácter hasta un máximo; termina con un separador GS o con el fin de la entrada | AI `10` (lote) — de 1 a 20 caracteres alfanuméricos |

Que un AI sea fijo o variable depende únicamente de su definición en la especificación de GS1: el analizador nunca lo supone.

### Cadenas de elementos con varios AI

Se pueden concatenar varios AI en una sola cadena de elementos. Los AI de longitud fija pueden concatenarse directamente, porque el analizador siempre sabe exactamente cuántos caracteres debe consumir. Los AI de longitud variable deben terminar con el **carácter GS** (ASCII `0x1D`, también llamado FNC1 en las simbologías de códigos de barras) siempre que les siga otro AI, para que el analizador sepa dónde termina un valor y dónde empieza el siguiente código AI.

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

En los literales de cadena de Java, escriba el carácter GS con la secuencia de escape Unicode `"\u001D"`.

### AI habituales

| AI | Título de datos | Formato | Valor de ejemplo |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (AAMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (AAMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, área monetaria única) |
| `710` | NHRN PZN | X..20 | `12345678` |

> El **cuarto dígito** de un AI de medida o de precio de 4 dígitos codifica el número de decimales implícitos: `3103` es el peso neto en kg con 3 decimales (`001500` = 1,500 kg), mientras que `3102` leería los mismos dígitos como 15,00 kg. La columna `Formato` anterior muestra el formato del *dato*; el `getFormatString()` completo de cada AI incluye el propio AI (por ejemplo, `N4+N6` para `3103`).

### Interpretación legible por personas (HRI)

La forma legible convencional encierra cada código AI entre paréntesis, inmediatamente antes de su valor, con un espacio entre elementos:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

El separador GS no aparece en la HRI. `GS1AIObject.toHriString()` produce este formato.

### Códigos AI de cuatro dígitos

Algunos AI utilizan cuatro dígitos en lugar de dos. Los dos primeros identifican la familia del AI; el tercero o el cuarto aportan semántica adicional (como la posición de la coma decimal implícita en los AI de medida). El analizador resuelve automáticamente el código AI completo a partir de la cadena de elementos: quien lo invoca trabaja siempre con el código completo (por ejemplo, `"3102"`, no solo `"31"`).

---

## Inicio rápido

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

> **Separador GS:** dentro de una cadena con varios AI, los AI de longitud variable deben delimitarse con el carácter GS (ASCII `0x1D`). Utilice `"\u001D"` en los literales de cadena de Java.

---

## Cadena de procesamiento del análisis

### Etapa previa — modificadores de entrada

Si el `ParseConfig` incluye **modificadores de entrada**, estos se ejecutan antes que todo lo demás: antes de retirar el identificador de correlación, antes de detectar el portador de datos y antes de entrar en la cadena de GS1. Cada modificador reescribe la entrada en bruto para el siguiente, y todas las etapas descritas más abajo operan sobre la salida de la cadena.

No hay ningún modificador configurado de forma predeterminada, de modo que esta etapa previa no hace nada mientras usted no la active explícitamente. Véase [Modificadores de entrada](#modificadores-de-entrada).

---

### Etapa 0 — identificador de correlación

Antes de cualquier procesamiento GS1, `GaiaParser` comprueba si la entrada empieza por un **prefijo de identificador de correlación** opcional: exactamente 8 dígitos decimales ASCII seguidos de una virgulilla (`~`), por ejemplo `12345678~`.

Si el prefijo está presente, se retira y se conserva como un `CorrelationInfo` en el `ParseResult` devuelto. Todas las etapas posteriores operan sobre la carga útil ya despojada. Si no hay prefijo, la entrada pasa sin cambios.

Véase [Identificador de correlación](#identificador-de-correlación) para más detalles.

---

### Etapa 1 — encaminamiento de la entrada

Tras retirar la correlación, `GaiaParser` comprueba si la entrada (ya despojada) empieza por un **identificador de simbología AIM**: un prefijo de tres caracteres con la forma `]` + letra ASCII + dígito ASCII (por ejemplo, `]C1` para GS1-128, `]d2` para GS1 DataMatrix, `]e0` para GS1 DataBar / GS1 Composite).

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

Si el portador no admite AI de GS1 (por ejemplo, un código de barras postal), el análisis se detiene de inmediato con un error `GE-D002`.

---

### Etapa 2 — sintaxis

Se ejecuta siempre. Consta de dos subetapas:

**2a. Tokenización (`AISyntaxParser`)**
- Lee la longitud del código AI a partir de los dos primeros caracteres, mediante la tabla de prefijos de GS1 (GS1 General Specifications, tabla 7-5).
- Los AI de longitud fija consumen un número exacto de bytes de la entrada.
- Los AI de longitud variable se leen hasta encontrar un carácter GS o el fin de la entrada.
- En los AI de varios componentes, el bloque de valor se divide en segmentos, uno por componente.

**2b. Validación estructural (`SyntaxValidator`)**
- Detecta AI duplicados (`GE-S004`).
- Comprueba las dependencias de AI obligatorias; por ejemplo, el AI `02` exige el AI `37` (`GE-S005`).
- Comprueba las combinaciones de AI excluidas (`GE-S006`).

Los errores de esta etapa tienen nivel `SYNTAX_ERROR` (tokenización) o `INTEGRITY_ERROR` (estructura). Si hay **algún** error —de tokenización o de estructura—, la cadena se detiene y se omiten las etapas de contenido e interpretación.

---

### Etapa 3 — contenido

Solo se ejecuta si la etapa 2 no produjo ningún error (ni de tokenización ni de estructura). Cadena aplicada a cada elemento (cada paso solo se ejecuta si el anterior no produjo errores):

| Paso | Validador | Códigos de error |
|---|---|---|
| Comprobación por expresión regular | `RegexValidator` | `GE-C001` |
| Juego de caracteres y formato de los componentes | `ComponentValidator` | `GE-C005` + códigos de formato por condición (`GE-C054`–`GE-C115`) |
| Dígito de control / carácter de control | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validación semántica personalizada | `ContentValidatorRegistry` | códigos de contenido por condición (`GE-C116`–`GE-C170`) |

Los errores de esta etapa tienen nivel `FORMAT_ERROR` o `DATA_ERROR`, con una excepción: las
comprobaciones del prefijo de empresa GS1 en los AI portadores de clave GS1 son orientativas y llevan nivel `WARNING` (véase la
[Referencia de errores](#referencia-de-errores)), de modo que un prefijo de empresa no reconocido no invalida
por sí solo el resultado.

---

### Etapa 4 — interpretación

Solo se ejecuta en modo `INTERPRETATION` y únicamente cuando ningún elemento arrastra un error de una etapa anterior. El `InterpretationEngine` enriquece cada elemento con metadatos etiquetados:

- Fechas reformateadas como `dd/mm/aaaa`
- Descomposición del dígito de control del GTIN y consulta del prefijo de empresa GS1
- Nombres de país ISO 3166
- Nombres y símbolos de moneda ISO 4217
- Importes decimales descodificados
- Fragmentos de HRI (interpretación legible por personas)

Los resultados se adjuntan como entradas `GS1AIInterpretation` en cada `GS1AIObjectElement`.

---

## Configuración del análisis (`ParseConfig`)

`GaiaParser` expone exactamente dos puntos de entrada:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` se ejecuta con la **configuración predeterminada**: modo `INTERPRETATION`, fechas en orden ascendente (`dd/mm/aaaa`) con separador `/` y año de cuatro dígitos, y mensajes de error en **inglés**. Para cambiar cualquiera de estos aspectos —incluido el modo de análisis—, construya un `ParseConfig` con su constructor fluido y utilice la sobrecarga de dos argumentos.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Todas las enumeraciones de opciones residen en `GaiaConstants`.

### Opciones

| Método del constructor | Enumeración (`GaiaConstants`) | Predeterminado | Efecto |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Profundidad de la cadena — véase [Modos de análisis](#modos-de-análisis). |
| `language(...)`      | `Language`      | `ENGLISH`        | Idioma de los mensajes de error, de las etiquetas de interpretación **y** de las descripciones de AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Orden de los componentes de la fecha: `LITTLE` (`dd/mm/aaaa`), `MIDDLE` (`mm/dd/aaaa`), `BIG` (`aaaa/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Carácter entre los componentes de la fecha: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) o `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) o `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Omite la comprobación estructural de «requiere» (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Omite la comprobación estructural de «excluye» (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nombre de clase | ninguno | Código que reescribe la entrada en bruto antes del análisis: dos [modificadores integrados](#modificadores-integrados) más los que usted escriba. Véase [Modificadores de entrada](#modificadores-de-entrada). |

Las cuatro opciones de fecha solo afectan a las cadenas de fecha formateadas que producen los enriquecedores de interpretación (en modo `INTERPRETATION`); no alteran la validación. Los valores del constructor pueden omitirse: cualquier opción que no se establezca (o a la que se pase `null`) conserva su valor predeterminado.

### Mensajes y etiquetas localizados

`language(...)` selecciona el idioma de **tres** clases de texto legible por personas: los mensajes de error, las etiquetas de interpretación (el `getLabel()` de cada `GS1AIInterpretation`) y las descripciones de AI (el `getDescription()` de cada `GS1AIObjectElement`).

`GaiaConstants.Language` define **35 idiomas**, que cubren las lenguas más habladas del mundo: inglés, francés, español, alemán, italiano, portugués, neerlandés, polaco, ruso, ucraniano, checo, sueco, chino, japonés, coreano, árabe, indonesio, hindi, turco, bengalí, urdu, vietnamita, pidgin nigeriano, árabe egipcio, maratí, telugu, tamil, cantonés, wu, tagalo, persa, hausa, panyabí, javanés y suajili.

Estado de las traducciones (tal como se entregan):
- **Etiquetas de interpretación** — traducidas a todos los idiomas.
- **Mensajes de error** — traducidos a todos los idiomas.
- **Descripciones de AI** — traducidas a todos los idiomas salvo el inglés. El inglés no constituye un catálogo aparte: se lee directamente del campo `description` de la entrada del AI en `gs1-application-identifiers.jsonld`, que es el recurso final al que recurre cualquier descripción de AI.

El pidgin nigeriano (`NIGERIAN_PIDGIN`), un criollo de base inglesa, reutiliza deliberadamente el texto inglés para las etiquetas de interpretación y los mensajes de error. Las descripciones de AI son la excepción a esa excepción: están traducidas a pidgin auténtico en lugar de reutilizar el inglés, porque los catálogos de descripciones de AI se elaboraron con independencia de los de etiquetas y mensajes. Conviene que hablantes nativos revisen las traducciones automáticas antes de confiar en ellas en producción.

Todo mensaje, etiqueta o descripción que falte en el catálogo de un idioma se sustituye por su versión inglesa. Los idiomas de escritura de derecha a izquierda (árabe, urdu, árabe egipcio, persa) se almacenan correctamente como cadenas; su representación de derecha a izquierda corresponde a la capa de presentación.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

Las etiquetas de interpretación se localizan igual (los valores no cambian; solo las etiquetas):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

Las descripciones de AI se localizan igual (solo `getTitle()`, por ejemplo `"GTIN"`, no se localiza):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formato de fechas

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## Modificadores de entrada

Un **modificador de entrada** es código que reescribe la cadena de entrada en bruto antes de que Gaia la analice. Los modificadores existen para las entradas que llegan ya deformadas: un lector que sustituye el separador GS por un marcador imprimible, un middleware que envuelve la carga útil en un prefijo propietario, un sistema anfitrión que lo pasa todo a mayúsculas. En lugar de preprocesar cada cadena en cada punto de llamada (y equivocarse sutilmente en alguno), declare la normalización una sola vez en el `ParseConfig` y deje que el analizador la aplique.

Los modificadores se ejecutan justo al principio de `GaiaParser.parse(...)`: antes de retirar el identificador de correlación, antes de detectar el identificador de simbología AIM y antes de la cadena de GS1. Todo lo que viene después solo ve la cadena reescrita. **No hay nada configurado de forma predeterminada**, ni siquiera los dos [modificadores integrados](#modificadores-integrados): usted los activa explícitamente en cada `ParseConfig`.

**Interfaz:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modificadores integrados

El jar principal incluye dos modificadores, en `tools.pantheum.gaia.modifier.custom`. Cubren las dos formas en que una carga útil GS1 llega deformada con más frecuencia —paréntesis de HRI impresos y tratados como datos, y espacios espurios—, de modo que los casos habituales no requieren ninguna clase propia:

| Clase | `getName()` | Qué hace |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Elimina los paréntesis de HRI que rodean cada AI (`(01)…(10)…`) y restituye el separador FNC1 que estos implicaban. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Elimina todos los espacios (`0x20`) de la cadena de elementos AI. |

Son implementaciones corrientes de `ModifierInterface`, sin ningún estatus especial: se declaran, se ordenan, se informan y fallan exactamente igual que los suyos:

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

Ambos carecen de estado y son seguros frente a la concurrencia, por lo que puede compartirse una única instancia, y ambos son direccionables por su nombre de clase completo para configuraciones externalizadas (véase [Declarar modificadores](#declarar-modificadores)).

#### `ModifierRemoveAIBrackets`

La interpretación legible por personas de GS1 imprime cada AI entre paréntesis —`(01)09521234543213(10)ABC123`— por pura convención tipográfica. Un lector o un middleware configurado para emitir la HRI transmite esos paréntesis como datos, y el tokenizador no tiene ni idea de qué hacer con ellos.

Eliminar los paréntesis es solo la mitad del trabajo. En la HRI, el paréntesis de apertura del AI *siguiente* es lo que marca el final del valor anterior, de modo que en forma con paréntesis un AI de longitud variable no necesita FNC1. Elimine los paréntesis sin más y esa frontera desaparece:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Por eso el modificador **reinserta un FNC1 en cada frontera cuyo AI precedente sea de longitud variable**, restituyendo exactamente lo que codificaban los paréntesis:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

La longitud se consulta en el propio `AiDefinitionRegistry` del analizador, de modo que se tratan todos los AI de longitud variable en lugar de una lista codificada a mano. Hay tres casos que se dejan intactos deliberadamente: un valor que ya termina en FNC1 (una fuente que emite ambas convenciones no recibe un segundo separador), un código entre paréntesis que no es un AI conocido (un AI desconocido no dice nada sobre su propia longitud) y el último AI de la cadena.

La reescritura es **idempotente** —volver a aplicarla sobre su propia salida no cambia nada—, por lo que es segura en un flujo mixto en el que solo algunas entradas llevan paréntesis.

> **Limitación.** `(` y `)` son a su vez caracteres de datos GS1 válidos, y el patrón se reduce a `\((\d{2,4})\)`. Un valor que casualmente contenga un número de dos a cuatro dígitos entre paréntesis también quedaría desenvuelto. Aplique esto únicamente a una fuente que use la convención de paréntesis de la HRI, no a valores con paréntesis genuinos.

#### `ModifierRemoveSpaces`

Algunos lectores, middlewares y cadenas de impresión de etiquetas insertan espacios espurios en una cadena de elementos por lo demás bien formada: para rellenar un campo de ancho fijo, para separar grupos legibles o para partir un valor largo. El tokenizador trata cada uno de ellos como un dato, lo que corrompe el valor que lo contiene y, en un AI de longitud variable, desplaza todo lo que viene después.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Solo se elimina el ASCII `0x20`. El resto de los caracteres de espacio se conservan: una tabulación, por ejemplo, queda fuera del juego de caracteres codificable de GS1, de modo que el analizador la señala como `GE-S008` en lugar de barrerla en silencio.

> **Limitación.** El espacio (`0x20`) forma parte del juego de caracteres invariante de GS1, por lo que un número de lote o una referencia de artículo de cliente pueden contener uno legítimamente. El modificador no sabe distinguir un espacio espurio de uno auténtico; aplíquelo únicamente a una fuente de la que sepa que no utiliza espacios dentro de sus valores de AI.

#### Los prefijos se omiten, no se reescriben

Los modificadores se ejecutan antes de que el analizador haya retirado nada, por lo que la entrada en bruto puede llevar todavía un identificador de correlación, un identificador de simbología AIM y un indicador ECI. Los dos modificadores integrados localizan el inicio de la cadena de elementos AI mediante la lógica de `CorrelationIdParser` y `DataCarrierParser` del propio analizador, reescriben solo a partir de ese punto y vuelven a unir el resultado al prefijo, que queda **intacto**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Los portadores EAN/UPC cuyo valor se rellena hasta GTIN-14 (`isRequiresGtinPadding()`) se omiten por completo: su carga útil es un valor de código de barras puramente numérico, sin estructura de AI, donde ni los paréntesis ni los espacios pueden significar nada.

#### Orden: los espacios antes que los paréntesis

Cuando se usan ambos, **declare `ModifierRemoveSpaces` en primer lugar**. El reconocimiento de los paréntesis depende de la posición: un `( 01 )` con espacios no coincide con `\((\d{2,4})\)`, de modo que los paréntesis sobreviven y el separador que implicaban no se restituye nunca.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Escribir un modificador

Escriba el suyo cuando ninguno de los integrados le sirva: la interfaz se reduce a un método.

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

Redefina en su lugar la sobrecarga de dos argumentos cuando la reescritura dependa de la configuración del análisis:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contrato:

| Regla | Detalle |
|---|---|
| Sin estado y seguro frente a la concurrencia | Se guarda en caché una única instancia por clase y se comparte entre todos los análisis. |
| Constructor público sin argumentos | Obligatorio solo cuando el modificador se referencia por su nombre de clase. |
| Tratar la entrada `null` y la vacía | El analizador no las filtra antes de ejecutar la cadena. |
| Devolver `null` significa «sin cambios» | Se conserva el valor anterior. Devuelva `input` sin modificar cuando el modificador no sea aplicable. |
| Es preferible devolver la entrada sin cambios que lanzar una excepción | Un modificador que lanza una excepción aborta el análisis — véase [Gestión de fallos](#gestión-de-fallos-de-un-modificador). |
| `getName()` | Redefínalo para controlar el nombre que se informa en `ModifierInfo`; de forma predeterminada es el nombre simple de la clase. |

### Declarar modificadores

Los modificadores se ejecutan en el orden en que se añaden, y cada uno recibe la salida del anterior. Decláre­los por instancia, por nombre de clase completo o como una lista de cualquiera de las dos formas:

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

Los [modificadores integrados](#modificadores-integrados) se nombran igual que los suyos: **siempre con el nombre completo**. No existe para ellos ninguna búsqueda por nombre corto ni por alias; `ModifierRegistry` resuelve todos los modificadores, propios o de la biblioteca, por su nombre de clase completo.

Los nombres los resuelve `ModifierRegistry`, que instancia cada clase una sola vez mediante su constructor sin argumentos y guarda la instancia en caché para cualquier configuración posterior que nombre la misma clase. La resolución ocurre **al construir la configuración**, de modo que un nombre que no se encuentra, que no implementa `ModifierInterface` o que no se puede instanciar lanza `IllegalArgumentException` en ese momento, y no en silencio durante el análisis. Un modificador que no puede construirse por reflexión (por ejemplo, uno que contiene una dependencia inyectada) puede registrarse previamente para seguir siendo direccionable por su nombre:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Examinar qué hizo un modificador

Cuando hay modificadores configurados, `ParseResult.getPayload()` refleja la entrada **modificada**. La original se conserva en `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` informa del `getName()` de cada modificador, que de forma predeterminada es el nombre simple de la clase pero que ambos modificadores integrados redefinen, de modo que una cadena formada por los dos informa de los nombres de presentación y no de los nombres de clase:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` devuelve `null` cuando no se configuró ningún modificador. Cuando se ejecutaron modificadores pero todos devolvieron la entrada sin cambios, la información está presente e `isModified()` vale `false`: solo aparecen en `getAppliedModifiers()` los modificadores que realmente cambiaron la entrada.

### Gestión de fallos de un modificador

Un modificador que lanza una excepción aborta el análisis. La excepción se envuelve en una `GaiaModifierException` que nombra al modificador culpable, y el resultado lleva un error interno `GE-I001` cuyo mensaje incluye ese nombre; `getPayload()` informa de la entrada sin modificar. El análisis deliberadamente **no** continúa con una cadena reescrita a medias: un paso de normalización que fallase en silencio produciría resultados de apariencia válida pero obtenidos a partir de una entrada equivocada.

---

## Modos de análisis

Cada modo designa la [etapa de la cadena](#cadena-de-procesamiento-del-análisis) más profunda que ejecuta; todas las etapas anteriores se ejecutan igualmente.

| Modo | Llega hasta | Responde a |
|---|---|---|
| `DATA_CARRIER` | Etapa 1 (encaminamiento de la entrada) | ¿Qué simbología transportó esto? |
| `SYNTAX` | Etapa 2 (sintaxis) | ¿Están bien formados los códigos AI y las longitudes? |
| `CONTENT` | Etapa 3 (contenido) | ¿Son los valores datos GS1 válidos? |
| `INTERPRETATION` | Etapa 4 (interpretación) | ¿Qué significan los valores? |

### Modo DATA_CARRIER

Se detiene tras la etapa 1: valida el identificador de simbología AIM e identifica la simbología, pero no entra en la cadena de análisis de AI. Resulta útil para identificar la simbología y encaminar el tratamiento sin asumir el coste de una validación completa.

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

**Úselo cuando:** su aplicación necesite identificar el tipo de código de barras antes de decidir cómo procesar la carga útil; por ejemplo, para encaminar hacia manejadores distintos según se trate de simbologías 1D o 2D. Para ese encaminamiento, prefiera el tipo [`DataCarrierType`](#datacarrierentry-y-datacarriertype) (`getDataCarrier().getDataCarrierType()`) en lugar de comparar cadenas con `getName()`.

---

### Modo SYNTAX

Se detiene tras la etapa 2. Resulta útil para un cribado estructural previo sin el coste de la validación del contenido.

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

**Úselo cuando:** quiera comprobar que los códigos AI y las longitudes de datos están bien formados antes de comprometerse con una validación completa, o cuando procese grandes volúmenes en los que los errores de contenido son infrecuentes.

---

### Modo CONTENT

Se detiene tras la etapa 3.

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

> La mayoría de los AI no pueden aparecer solos: los AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) y `21`
> (SERIAL) *exigen* cada uno una clave de identificación, como el AI `01`, en la misma cadena de
> elementos; omitir el GTIN del ejemplo anterior fallaría ya en la etapa 2 con `GE-S005`, sin llegar
> siquiera a la validación del contenido. Establezca `skipRequiresCheck(true)` en el
> `ParseConfig` para analizar fragmentos que omiten deliberadamente sus AI acompañantes.

**Úselo cuando:** necesite saber si un valor leído cumple plenamente con GS1 antes de utilizarlo en un proceso de negocio, sin el sobrecoste del enriquecimiento por interpretación.

---

### Modo INTERPRETATION (predeterminado)

Ejecuta la cadena completa hasta la etapa 4. Es el modo predeterminado al llamar a `parse(String)` sin argumento de modo. Solo enriquece los elementos que han superado la validación de contenido sin errores.

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

**Ejemplo de salida:**
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

**Ejemplo de importe monetario (AI 3932 — precio con código de moneda ISO):**
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

**Úselo cuando:** construya capas de presentación, herramientas de verificación de etiquetas o cualquier interfaz que necesite un desglose legible de los valores de AI.

---

## Identificador de correlación

Algunos flujos de trabajo anteponen un identificador de correlación propietario de 8 dígitos a la entrada GS1 en bruto, para poder vincular los eventos de lectura con una sesión o una transacción. El formato es:

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

La virgulilla (`~`) es el separador. **No** forma parte del contenido GS1: se retira antes de que comience cualquier análisis GS1.

### Reglas de detección

El prefijo se detecta cuando la entrada empieza por exactamente 8 dígitos decimales ASCII (`0`–`9`) seguidos inmediatamente de `~`. Si el noveno carácter no es `~`, o si alguno de los 8 primeros no es un dígito, la entrada se trata como contenido GS1 corriente, sin prefijo de correlación.

### Acceder al identificador de correlación

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

### Combinación con un identificador de simbología AIM

Un prefijo de correlación puede preceder a un identificador de simbología AIM. El analizador lo gestiona de forma transparente:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Clase de implementación:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Un **GS1 Digital Link** codifica uno o varios valores de AI directamente en la estructura de una URL HTTP(S), lo que permite identificadores de productos físicos resolubles en la web. GAIA implementa el estándar *GS1 Digital Link Standard: URI Syntax* (versión 1.7.0) para URI **sin comprimir**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` reconoce automáticamente los URI de Digital Link: toda entrada que empiece por `http://` o `https://` se dirige a `GS1DLParser`, que ejecuta las mismas etapas de contenido e interpretación que la cadena de las cadenas de elementos.

### Estructura del URI y funciones de los AI

Cada AI de un URI de Digital Link desempeña una de tres funciones, expuesta en cada `GS1AIObjectElement` mediante `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Función | Ubicación | Ejemplo |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Primer par `/ai/valor` de la ruta (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Pares de ruta siguientes, ordenados según la clave primaria (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parámetros de consulta con claves totalmente numéricas (§4.10) | `?17=271231` |

Reglas estructurales que se aplican (`DLPathRules`):
- Exactamente **una** clave de identificación primaria en la ruta; las claves adicionales deben codificarse como atributos de datos en la consulta.
- Los calificadores de clave deben estar admitidos por la clave primaria y aparecer en el orden prescrito. Los calificadores opcionales pueden omitirse, pero los que *sí* estén presentes deben respetar el orden fijado — véase [Orden de los calificadores](#orden-de-los-calificadores).
- Pueden preceder a la clave primaria segmentos de ruta personalizados arbitrarios (por ejemplo, `/products/au/01/...`); recupérelos mediante `getDigitalLinkInfo().getCustomPathStem()`.
- Las claves de consulta no numéricas (`linkType`, `context`, parámetros de extensión como `23P`) se ignoran; las claves totalmente numéricas deben ser AI válidos marcados con `validAsDataAttribute`.
- Los caracteres de valor codificados con porcentaje se descodifican; los AI `(03)` y `(8014)` no están permitidos.

Las claves primarias y sus secuencias admisibles de calificadores están **dirigidas por datos** a partir de las definiciones de AI —el indicador `gs1DigitalLinkPrimaryKey` y el atributo `gs1DigitalLinkQualifiers`— en lugar de estar codificadas a mano.

Cualquier infracción estructural, o una entrada que no sea una URL, produce un error estructural de Digital Link (`GE-L001`–`GE-L014`, un código por condición). Los metadatos descompuestos de la URL (`scheme`, `domain`, `path`, `customPathStem`, `query` y el objeto `java.net.URL`) siguen estando disponibles mediante `getDigitalLinkInfo()` incluso cuando hay errores estructurales.

### Orden de los calificadores

Para cada clave primaria, `gs1DigitalLinkQualifiers` enumera una o varias secuencias **ordenadas** de calificadores. Dentro de una secuencia, un AI entre corchetes es **opcional** y un AI sin corchetes es **obligatorio**, reflejando la notación `[cpv-comp]` de la ABNF del §4.9. Las secuencias de una misma clave primaria son alternativas mutuamente excluyentes.

El GTIN (`01`), por ejemplo, define dos secuencias:

| Ruta | Secuencia | Significado |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — cada uno opcional, pero en este orden fijo |
| upui-path | `235` | TPX (obligatorio); GTIN + TPX = UPUI |

Así, `/01/09506000134352/10/LOT-ABC/21/SER` es válido (LOT antes que SER, CPV omitido), `/01/.../21/SER/10/LOT-ABC` se **rechaza** (fuera de orden) y `/01/09506000134352/235/2ABC456` corresponde a la upui-path. La comprobación de orden es una coincidencia de subsecuencia que preserva el orden, de modo que los AI opcionales pueden omitirse, pero nunca reordenarse.

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

**Clase de implementación:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Trabajar con los resultados

### ParseResult

El resultado de nivel superior que devuelve `GaiaParser.parse()`.

| Método | Devuelve | Descripción |
|---|---|---|
| `isValid()` | `boolean` | `true` si no hay errores de ningún nivel. Las advertencias no afectan a la validez. Siempre `true` cuando `getAiObject()` es `null`. |
| `getPayload()` | `String` | La cadena de entrada tras retirar el prefijo de correlación — y después de que cualquier [modificador de entrada](#modificadores-de-entrada) la haya reescrito. |
| `getPayloadContent()` | `String` | La carga útil sin el identificador de simbología AIM ni el prefijo ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (un portador de datos rechazado por no ser GS1, por ejemplo un portador Code 39 `]A0`) o `UNABLE_TO_DETERMINE_CONTENT` (cuando `aiObject` es `null`, por ejemplo en modo `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | La profundidad de cadena configurada (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | La etapa más profunda que el análisis alcanzó realmente — véase más abajo. |
| `isParseComplete()` | `boolean` | `true` si el análisis alcanzó la profundidad solicitada (`achieved == requested`). Independiente de `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Todos los AI resueltos. `null` en modo `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Todos los errores de nivel distinto de WARNING (de objeto y de todos los elementos). |
| `getWarnings()` | `List<GaiaError>` | Todos los avisos de nivel WARNING (de objeto y de todos los elementos). |
| `hasWarnings()` | `boolean` | `true` si se emitió algún aviso de nivel WARNING. |
| `getIssues()` | `List<GaiaError>` | Errores y advertencias juntos. |
| `hasDataCarrier()` | `boolean` | `true` si se reconoció un identificador de simbología AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadatos de simbología, o `null` si no se identificó ningún portador. |
| `hasEci()` | `boolean` | `true` si se retiró un indicador ECI de la carga útil. |
| `getEci()` | `EciEntry` | Metadatos de codificación ECI, o `null`. |
| `hasCorrelationId()` | `boolean` | `true` si en la entrada original había un prefijo de correlación `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | El identificador de correlación extraído, o `null` si no había ninguno. |
| `isInputModified()` | `boolean` | `true` si un [modificador de entrada](#modificadores-de-entrada) cambió la entrada. |
| `getModifierInfo()` | `ModifierInfo` | Qué hizo la cadena de modificadores — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` si no se configuró ningún modificador. |
| `getTiming()` | `ProcessingTiming` | Cronometraje real del análisis — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` si no lo produjo `GaiaParser`. |
| `getVersion()` | `String` | La versión de la biblioteca que produjo el resultado. |

#### Modo de análisis solicitado frente al alcanzado

La cadena recorre la escala **SYNTAX → CONTENT → INTERPRETATION** y se detiene antes de tiempo ante un error, de modo que el modo realmente *alcanzado* puede ser menos profundo que el *solicitado*. `getAchievedParseMode()` indica hasta dónde llegó:

| Solicitado | Qué ocurre | Alcanzado | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | un error **de sintaxis o de estructura** detiene el análisis tras la tokenización | `SYNTAX` | `false` |
| `INTERPRETATION` | un error **de contenido** (formato o dígito de control incorrecto) impide el enriquecimiento | `CONTENT` | `false` |
| `CONTENT` | la etapa de contenido siempre se ejecuta por completo (los errores se anotan, no son fatales) | `CONTENT` | `true` |
| cualquiera (entrada sin errores) | la cadena alcanza la profundidad solicitada | = solicitado | `true` |
| `DATA_CARRIER` | portador validado; no se analiza contenido de AI | `DATA_CARRIER` | `true` |
| cualquiera | el portador de datos se rechaza antes del análisis de AI (por ejemplo, un portador `]A0` no GS1) | `SYNTAX` | `false` |

`isParseComplete()` es independiente de `isValid()`: un análisis `CONTENT` de un GTIN con un dígito de control incorrecto está **completo** (ejecutó la etapa de contenido) y a la vez es **inválido** (el dígito de control falló). Use `isParseComplete()` para preguntar «¿llegó la cadena tan lejos como pedí?» e `isValid()` para preguntar «¿están los datos bien formados?».

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

La colección de elementos AI resueltos.

| Método | Descripción |
|---|---|
| `getAis()` | Todas las instancias de `GS1AIObjectElement`, en el orden de la entrada. |
| `get(String aiCode)` | Primer elemento que coincide con el código AI dado, o `null`. |
| `contains(String aiCode)` | `true` si hay un AI con ese código. |
| `size()` | Número de AI resueltos. |
| `isValid()` | `true` si no hay errores de objeto y ningún elemento tiene errores. |
| `toHriString()` | Cadena HRI, por ejemplo `(01)09506000134352 (17)261231`. |
| `toElementString()` | Cadena de elementos en bruto: sin paréntesis y con un FNC1 tras cada elemento de longitud variable; por ejemplo, `010950600013435210LOT-ABC<GS>17271231`. Devuelve `null` si `isValid()` es `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` cuando `hasDigitalLink()` es verdadero; en caso contrario, `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` si la entrada era un URI de GS1 Digital Link con una clave de identificación primaria. Una URL bien formada sin clave primaria expone igualmente `getDigitalLinkInfo()`, pero aquí devuelve `false`. |
| `getCanonicalDigitalLink()` | El URI canónico de GS1 Digital Link (§4.12) en `https://id.gs1.org` —clave primaria y calificadores como segmentos de ruta, atributos de datos como parámetros de consulta ordenados por clave de AI—, o `null` si no hay clave primaria. |
| `getDigitalLinkInfo()` | Metadatos de descomposición del URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), o `null` si no es un Digital Link. |
| `getAllErrors()` | Errores de objeto + todos los errores de elementos (distintos de WARNING). |
| `getAllWarnings()` | Advertencias de objeto + todas las advertencias de elementos. |
| `getAllIssues()` | Todo junto. |

---

### GS1AIObjectElement

Una única instancia de AI resuelta.

| Método | Descripción |
|---|---|
| `getAi()` | Código AI, por ejemplo `"01"`, `"3102"`. |
| `getTitle()` | Título de datos de GS1, por ejemplo `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Descripción GS1 completa del AI, **localizada al idioma del análisis** (por ejemplo, `"Global Trade Item Number (GTIN)"` en inglés). Recurre al texto inglés de la definición del AI si no está traducida. |
| `getFormatString()` | Descriptor de formato que abarca el AI *y* su dato, por ejemplo `"N2+N14"` para el AI `01`, `"N2+X..20"` para el AI `10`, `"N4+N3+N..15"` para el AI `3932`. |
| `getValue()` | Valor de datos en bruto extraído de la cadena de elementos. |
| `isFixedLength()` | `true` si el AI tiene una longitud de datos fija. |
| `getPosition()` | Desplazamiento de carácter (base cero) en la entrada original. |
| `getGS1ComponentValues()` | Segmentos de valor por componente (para AI de varios componentes). |
| `getErrors()` | Errores de nivel elemento distintos de WARNING. |
| `getWarnings()` | Avisos de nivel WARNING del elemento. |
| `getIssues()` | Errores y advertencias del elemento juntos. |
| `hasErrors()` | `true` si hay adjunto algún error distinto de WARNING. |
| `hasWarnings()` | `true` si hay adjunto algún aviso de nivel WARNING. |
| `getInterpretations()` | Entradas `GS1AIInterpretation` (rellenadas en modo INTERPRETATION). |
| `getInterpretation(String type)` | Primera interpretación que coincide con la clave de tipo de `GS1Constants_Enricher` dada, o `null`. |
| `getDigitalLinkAIType()` | La función Digital Link del elemento (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), o `null` para entradas de tipo cadena de elementos. |
| `hasDigitalLinkAIType()` | `true` si se ha asignado una función Digital Link. |

---

### GaiaError

Un error de validación o un aviso, inmutable.

| Método | Descripción |
|---|---|
| `getId()` | Identificador de catálogo, por ejemplo `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` o `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` o `INTERNAL`. |
| `getCode()` | Código corto legible por máquina. |
| `getAi()` | Código AI que provocó el error, o `null` para errores de objeto. |
| `getMessage()` | Mensaje legible, con los valores interpolados. |
| `getPosition()` | Desplazamiento de carácter (base cero) en la entrada original. |

---

### GS1AIInterpretation

Un fragmento de interpretación etiquetado, adjunto a un `GS1AIObjectElement` en modo `INTERPRETATION`.

| Método | Descripción |
|---|---|
| `getType()` | Clave de tipo legible por máquina, por ejemplo `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Estable entre idiomas. |
| `getLabel()` | Etiqueta legible por personas, **localizada al idioma del análisis** (por ejemplo, `"Date"` / `"GS1 company prefix"` en inglés). |
| `getValue()` | Valor extraído o enriquecido, por ejemplo `"31/12/2026"`, `"9506000"`. No se localiza. |

---

### DataCarrierEntry y DataCarrierType

Cuando la entrada lleva un identificador de simbología AIM, `ParseResult.getDataCarrier()` devuelve un `DataCarrierEntry` que describe el símbolo que transportó los datos. Esa entrada es el registro concreto del catálogo correspondiente al identificador AIM reconocido; `DataCarrierType` es la enumeración, conocida en tiempo de compilación, a la que pertenece.

#### DataCarrierEntry

Los metadatos de un identificador de simbología AIM reconocido (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Método | Descripción |
|---|---|
| `getAimCodeId()` | El identificador de simbología AIM reconocido, por ejemplo `"]C1"`. |
| `getName()` | Nombre legible del símbolo concreto, por ejemplo `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Descripción más extensa del portador. |
| `getType()` | El tipo estructural del portador como cadena (refleja `getDataCarrierType().getCategory()`). |
| `getStandard()` | El estándar de simbología, cuando está registrado. |
| `getDataCarrierType()` | El `DataCarrierType` tipado de esta entrada; es preferible para el encaminamiento programático. |
| `isGs1Capable()` | `true` si el portador puede contener datos GS1 (cadenas de elementos AI o Digital Link). |
| `isGs1AICapable()` | `true` si el portador puede contener cadenas de elementos AI de GS1. |
| `isGs1DigitalLinkCapable()` | `true` si el portador puede contener un URI de GS1 Digital Link. |
| `isEciCapable()` | `true` si el portador admite un indicador ECI. |
| `isRequiresGtinPadding()` | `true` para los portadores EAN/UPC/ITF cuyo valor numérico se rellena hasta GTIN-14 antes del análisis de AI. |

#### DataCarrierType

Una enumeración, conocida en tiempo de compilación, de tipos de portador de datos, indexada por el identificador de simbología AIM asignado en ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). El carácter que sigue a `]` (el *carácter de código*) selecciona la familia; la mayoría de las familias se corresponden con una única constante que cubre todos los modificadores (`ITF` cubre `]I0`–`]I2`; `EAN_UPC` cubre EAN-13, UPC-A, UPC-E y EAN-8). Cuando GS1 reserva un modificador para datos de AI, esa variante constituye su propia constante —`GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`)—, distinta de su homóloga corriente. Cuando no hay identificador AIM, o cuando este designa un portador desconocido, el tipo es `UNKNOWN`.

| Método | Descripción |
|---|---|
| `getCategory()` | La categoría general `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` u `OTHER`. |
| `getCodeChar()` | El carácter de código AIM que identifica la familia, por ejemplo `"Q"` para QR Code; `null` para `UNKNOWN`. |
| `getDisplayName()` | Nombre legible del *tipo* (puede ser más amplio que `DataCarrierEntry.getName()`; por ejemplo, `"EAN-13 / UPC-A / UPC-E / EAN-8"` frente a `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` para las constantes que siempre denotan datos de AI de GS1: las cuatro variantes reservadas por GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) más `GS1_DATABAR`, que es intrínsecamente GS1 porque todo modificador `]e` designa un GS1 DataBar. Más restrictivo que `DataCarrierEntry.isGs1AICapable()`: un `QR_CODE` corriente también puede transportar datos de AI de GS1. |
| `static forAimCodeId(String)` | Resuelve un tipo directamente a partir de un identificador AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); devuelve `UNKNOWN` para un identificador ausente, mal formado o no reconocido. |

Encaminar por tipo en lugar de por nombre; por ejemplo, para separar los símbolos lineales (Code 128) de los 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` cubre únicamente los símbolos matriciales y de puntos; los portadores lineales apilados (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) son `STACKED_LINEAR`, aunque se les llame habitualmente
códigos de barras «2D». Para tratar ambos como un solo grupo —por ejemplo, para decidir si hace falta
un lector de imagen en lugar de un lector láser—, compruebe si pertenecen a cualquiera de las dos categorías.

> La resolución del tipo exige que el identificador de simbología AIM esté presente en la lectura; sin él, `getDataCarrier()` es `null` y el tipo es `UNKNOWN`. Configure el lector para que transmita el prefijo del identificador AIM.

---

## Referencia de errores

| Código | Nivel | Etapa | Significado |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Prefijo de AI desconocido: no se puede determinar la longitud de los datos |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Entrada demasiado corta para leer un código AI completo |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Valor truncado: menos caracteres de los que exige el AI |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Identificador de aplicación duplicado en la cadena de elementos |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Falta una dependencia de AI obligatoria |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Combinación de AI excluida: dos AI que no pueden coexistir |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Fallo inesperado de la tokenización |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Carácter fuera del juego codificable de GS1 en la cadena de elementos |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Falta el separador FNC1 obligatorio tras un AI de longitud variable |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Datos sobrantes más allá del máximo de todos los componentes |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separador FNC1 tras un AI de longitud fija en posición intermedia |
| `GE-W002` | WARNING | SYNTAX | FNC1 al final de la cadena de elementos (solo orientativo) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Infracciones estructurales de un URI de Digital Link: un código por condición (URI mal formado, esquema, host, orden de calificadores, AI prohibido, sin clave primaria (`GE-L013`), varias claves primarias (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | El valor no supera la expresión regular del AI |
| `GE-C003` | DATA_ERROR | CONTENT | Fallo de validación del dígito de control |
| `GE-C004` | DATA_ERROR | CONTENT | Fallo de validación del par de caracteres de control |
| `GE-C005` | FORMAT_ERROR | CONTENT | El valor de un componente contiene un carácter fuera del juego permitido |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Fallos de formato de componente: un código por condición de validación (véase `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Fallos de validación semántica personalizada: un código por condición de validación (véase `content/validator/`). **Excepciones:** las 14 comprobaciones del prefijo de empresa GS1 que se enumeran más abajo llevan nivel `WARNING`, y `GE-C168` (código numérico de país ISO 3166-1 no reconocido) lleva `FORMAT_ERROR`. |
| Comprobaciones del prefijo de empresa GS1 | WARNING | CONTENT | La clave no empieza por un prefijo de empresa GS1 reconocido, en los AI portadores de clave GS1: `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Solo orientativo: no afecta a la validez. |
| `GE-C169` | DATA_ERROR | CONTENT | Fallo del dígito de control IMEI (Luhn) en el AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Fallo del dígito de control EID (Luhn) en el AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Identificador de simbología AIM no reconocido |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Portador identificado, pero no admite ni cadenas de elementos AI de GS1 ni URI de Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Error interno inesperado |

> **Defecto conocido en la representación de los mensajes.** Las plantillas del catálogo entrecomillan los
> valores interpolados con apóstrofos duplicados al estilo de MessageFormat (`''{value}''`), pero
> `ErrorRegistry` interpola con un simple `String.replace`, de modo que la duplicación sobrevive hasta
> `getMessage()`: actualmente verá `value ''09506000134351''` donde los textos de mensaje
> citados en esta guía muestran `value '09506000134351'`. Afecta a todos los mensajes que
> entrecomillan un valor, en los 35 catálogos de idiomas. No analice los mensajes de error;
> compare con `getId()` / `getCode()`.

---

## Seguridad frente a hilos

`GaiaParser` es seguro frente a hilos una vez construido. Una única instancia puede compartirse entre hilos y utilizarse de forma concurrente. El patrón recomendado es construir una instancia al arrancar la aplicación y reutilizarla:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` es inmutable e igual de seguro de compartir. La única obligación de seguridad frente a hilos que la biblioteca no puede garantizar por usted recae en los [modificadores de entrada](#modificadores-de-entrada): se guarda en caché una única instancia de cada modificador y se comparte entre todos los análisis concurrentes, de modo que las implementaciones deben carecer de estado.

---

## Apéndice A — constantes de cadena de AI

`GS1Constants_AICodes` (en el paquete `tools.pantheum.gaia.gs1.constants`) declara una constante `String` para cada identificador de aplicación que GAIA reconoce. Utilice estas constantes en lugar de escribir a mano las cadenas de códigos AI:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Cada constante contiene la forma textual del código AI (por ejemplo, `AI_01_GTIN = "01"`).

### Identificación y serialización

| AI | Constante | Descripción |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Código Seriado de la Unidad de Envío (SSCC). |
| `01` | `AI_01_GTIN` | Número Global de Artículo Comercial (GTIN). |
| `02` | `AI_02_CONTENT` | Número Global de Artículo Comercial (GTIN) de las unidades de expedición contenidas. |
| `03` | `AI_03_MTO_GTIN` | Identificación de una unidad de expedición fabricada por encargo (MtO) (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Número de lote. |
| `20` | `AI_20_VARIANT` | Variante interna de producto. |
| `21` | `AI_21_SERIAL` | Número de serie. |
| `22` | `AI_22_CPV` | Variante del producto de consumo. |
| `235` | `AI_235_TPX` | Extensión Serializada Controlada por Terceros del Número Global de Artículo Comercial (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identificación adicional del producto asignada por el fabricante. |
| `241` | `AI_241_CUST_PART_NO` | Número de pieza del cliente. |
| `242` | `AI_242_MTO_VARIANT` | Número de variante de fabricación por encargo. |
| `243` | `AI_243_PCN` | Número de componente de embalaje. |
| `250` | `AI_250_SECONDARY_SERIAL` | Número de serie secundario. |
| `251` | `AI_251_REF_TO_SOURCE` | Referencia a la entidad de origen. |
| `253` | `AI_253_GDTI` | Identificador Global de Tipo de Documento (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Componente de extensión del Número Global de Localización (GLN). |
| `255` | `AI_255_GCN` | Número Global de Cupón (GCN). |
| `30` | `AI_30_VAR_COUNT` | Número variable de artículos (artículo de medida variable). |
| `37` | `AI_37_COUNT` | Número de unidades de expedición o piezas de unidad de expedición contenidas en una unidad logística. |

### Fechas y horas

| AI | Constante | Descripción |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Fecha de producción (AAMMDD). |
| `12` | `AI_12_DUE_DATE` | Fecha de vencimiento (AAMMDD). |
| `13` | `AI_13_PACK_DATE` | Fecha de envasado (AAMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Fecha de consumo preferente (AAMMDD). |
| `16` | `AI_16_SELL_BY` | Fecha límite de venta (AAMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Fecha de caducidad (AAMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Fecha y hora de entrega no antes de (AAMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Fecha y hora límite de entrega (AAMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Fecha de liberación (AAMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Fecha y hora de caducidad (AAMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Fecha de primera congelación (AAMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Fecha de recolección (AAMMDD[AAMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Fecha límite de prueba (AAMMDD[hhmm]). |

### Cantidad y medida — medida variable (métrico)

Las familias de AI de 4 dígitos `310n`–`369n` codifican cantidades de medida variable. El tercer dígito selecciona el tipo de medida; el **cuarto dígito** (`n`, 0–5) es el número de decimales implícitos; por ejemplo, `AI_3102_NET_WEIGHT_KG` significa peso neto en kg con 2 decimales.

| Familia | Patrón de constante (`n` = dígito de decimales) | Descripción |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Peso neto, kilogramos (artículo de medida variable). |
| `311n` | `AI_311n_LENGTH_M` | Longitud o primera dimensión, metros (artículo de medida variable). |
| `312n` | `AI_312n_WIDTH_M` | Anchura, diámetro o segunda dimensión, metros (artículo de medida variable). |
| `313n` | `AI_313n_HEIGHT_M` | Profundidad, espesor, altura o tercera dimensión, metros (artículo de medida variable). |
| `314n` | `AI_314n_AREA_M` | Superficie, metros cuadrados (artículo de medida variable). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volumen neto, litros (artículo de medida variable). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volumen neto, metros cúbicos (artículo de medida variable). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Peso logístico, kilogramos. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Longitud o primera dimensión, metros. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Anchura, diámetro o segunda dimensión, metros. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Profundidad, espesor, altura o tercera dimensión, metros. |
| `334n` | `AI_334n_AREA_M_LOG` | Superficie, metros cuadrados. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volumen logístico, litros. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volumen logístico, metros cúbicos. |
| `337n` | `AI_337n_KG_PER_M` | Kilogramos por metro cuadrado. |

### Cantidad y medida — medida variable (imperial / EE. UU.)

| Familia | Patrón de constante (`n` = dígito de decimales) | Descripción |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Peso neto, libras (artículo de medida variable). |
| `321n` | `AI_321n_LENGTH_IN` | Longitud o primera dimensión, pulgadas (artículo de medida variable). |
| `322n` | `AI_322n_LENGTH_FT` | Longitud o primera dimensión, pies (artículo de medida variable). |
| `323n` | `AI_323n_LENGTH_YD` | Longitud o primera dimensión, yardas (artículo de medida variable). |
| `324n` | `AI_324n_WIDTH_IN` | Anchura, diámetro o segunda dimensión, pulgadas (artículo de medida variable). |
| `325n` | `AI_325n_WIDTH_FT` | Anchura, diámetro o segunda dimensión, pies (artículo de medida variable). |
| `326n` | `AI_326n_WIDTH_YD` | Anchura, diámetro o segunda dimensión, yardas (artículo de medida variable). |
| `327n` | `AI_327n_HEIGHT_IN` | Profundidad, espesor, altura o tercera dimensión, pulgadas (artículo de medida variable). |
| `328n` | `AI_328n_HEIGHT_FT` | Profundidad, espesor, altura o tercera dimensión, pies (artículo de medida variable). |
| `329n` | `AI_329n_HEIGHT_YD` | Profundidad, espesor, altura o tercera dimensión, yardas (artículo de medida variable). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Peso logístico, libras. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Longitud o primera dimensión, pulgadas. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Longitud o primera dimensión, pies. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Longitud o primera dimensión, yardas. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Anchura, diámetro o segunda dimensión, pulgadas. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Anchura, diámetro o segunda dimensión, pies. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Anchura, diámetro o segunda dimensión, yarda. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Profundidad, espesor, altura o tercera dimensión, pulgadas. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Profundidad, espesor, altura o tercera dimensión, pies. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Profundidad, espesor, altura o tercera dimensión, yardas. |
| `350n` | `AI_350n_AREA_IN` | Superficie, pulgadas cuadradas (artículo de medida variable). |
| `351n` | `AI_351n_AREA_FT` | Superficie, pies cuadrados (artículo de medida variable). |
| `352n` | `AI_352n_AREA_YD` | Superficie, yardas cuadradas (artículo de medida variable). |
| `353n` | `AI_353n_AREA_IN_LOG` | Superficie, pulgadas cuadradas. |
| `354n` | `AI_354n_AREA_FT_LOG` | Superficie, pies cuadrados. |
| `355n` | `AI_355n_AREA_YD_LOG` | Superficie, yardas cuadradas. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Peso neto, onzas troy (artículo de medida variable). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Peso neto (o volumen), onzas (artículo de medida variable). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volumen neto, cuartos de galón (artículo de medida variable). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volumen neto, galones EE. UU. (artículo de medida variable). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volumen logístico, cuartos de galón. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volumen logístico, galones EE. UU. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volumen neto, pulgadas cúbicas (artículo de medida variable). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volumen neto, pies cúbicos (artículo de medida variable). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volumen neto, yardas cúbicas (artículo de medida variable). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volumen logístico, pulgadas cúbicas. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volumen logístico, pies cúbicos. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volumen logístico, yardas cúbicas. |

### Precios e importes monetarios

El cuarto dígito (`n`) codifica el número de decimales implícitos. Su rango permitido
varía según la familia — véase la columna `n`.

| Familia | Patrón de constante (`n` = dígito de decimales) | `n` | Descripción |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Importe a pagar aplicable o valor del cupón, moneda local. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Importe a pagar aplicable con código de moneda ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Importe a pagar aplicable, zona monetaria única (artículo de medida variable). |
| `393n` | `AI_393n_PRICE` | 0–9 | Importe a pagar aplicable con código de moneda ISO (artículo de medida variable). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Porcentaje de descuento de un cupón. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Importe a pagar por unidad de medida, zona monetaria única (artículo de medida variable). |

### Ubicación y expedición

| AI | Constante | Descripción |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Número de pedido de compra del cliente. |
| `401` | `AI_401_GINC` | Número Global de Identificación de Envío (GINC). |
| `402` | `AI_402_GSIN` | Número Global de Identificación de Envío (GSIN). |
| `403` | `AI_403_ROUTE` | Código de enrutamiento. |
| `410` | `AI_410_SHIP_TO_LOC` | Número Global de Localización (GLN) del lugar de entrega. |
| `411` | `AI_411_BILL_TO` | Número Global de Localización (GLN) del facturado. |
| `412` | `AI_412_PURCHASE_FROM` | Número Global de Localización (GLN) del proveedor de compra. |
| `413` | `AI_413_SHIP_FOR_LOC` | Número Global de Localización (GLN) de reenvío. |
| `414` | `AI_414_LOC_NO` | Identificación de una ubicación física - Número Global de Localización (GLN). |
| `415` | `AI_415_PAY_TO` | Número Global de Localización (GLN) de la entidad facturadora. |
| `416` | `AI_416_PROD_SERV_LOC` | Número Global de Localización (GLN) del lugar de producción o servicio. |
| `417` | `AI_417_PARTY` | Número Global de Localización (GLN) de la parte. |
| `420` | `AI_420_SHIP_TO_POST` | Código postal del lugar de entrega dentro de una única autoridad postal. |
| `421` | `AI_421_SHIP_TO_POST` | Código postal del lugar de entrega con código de país ISO. |
| `422` | `AI_422_ORIGIN` | País de origen de una unidad de expedición. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | País de procesamiento inicial. |
| `424` | `AI_424_COUNTRY_PROCESS` | País de procesamiento. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | País de desmontaje. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | País que cubre toda la cadena de procesos. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivisión de país de origen. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Nombre de la empresa del lugar de entrega. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Contacto del lugar de entrega. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Dirección de entrega, línea 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Dirección de entrega, línea 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Barrio del lugar de entrega. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Localidad del lugar de entrega. |
| `4306` | `AI_4306_SHIP_TO_REG` | Región del lugar de entrega. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Código de país del lugar de entrega. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Número de teléfono del lugar de entrega. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Geolocalización del lugar de entrega. |
| `4310` | `AI_4310_RTN_TO_COMP` | Nombre de la empresa de devolución. |
| `4311` | `AI_4311_RTN_TO_NAME` | Contacto de devolución. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Dirección de devolución, línea 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Dirección de devolución, línea 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | Barrio de devolución. |
| `4315` | `AI_4315_RTN_TO_LOC` | Localidad de devolución. |
| `4316` | `AI_4316_RTN_TO_REG` | Región de devolución. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Código de país de devolución. |
| `4318` | `AI_4318_RTN_TO_POST` | Código postal de devolución. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Número de teléfono de devolución. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Descripción del código de servicio. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Indicador de mercancías peligrosas. |
| `4322` | `AI_4322_AUTH_LEAVE` | Autorización de entrega sin firma. |
| `4323` | `AI_4323_SIG_REQUIRED` | Indicador de firma requerida. |
| `4330` | `AI_4330_MAX_TEMP_F` | Temperatura máxima en Fahrenheit (expresada en centésimas de grado). |
| `4331` | `AI_4331_MAX_TEMP_C` | Temperatura máxima en Celsius (expresada en centésimas de grado). |
| `4332` | `AI_4332_MIN_TEMP_F` | Temperatura mínima en Fahrenheit (expresada en centésimas de grado). |
| `4333` | `AI_4333_MIN_TEMP_C` | Temperatura mínima en Celsius (expresada en centésimas de grado). |

### Atributos de producto y trazabilidad

| AI | Constante | Descripción |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Número de Catálogo OTAN (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Clasificación UN/CEPE de canales y cortes de carne. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Potencia activa. |
| `7005` | `AI_7005_CATCH_AREA` | Zona de captura. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Especie con fines pesqueros. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Tipo de arte de pesca. |
| `7010` | `AI_7010_PROD_METHOD` | Método de producción. |
| `7020` | `AI_7020_REFURB_LOT` | Identificador de lote de reacondicionamiento. |
| `7021` | `AI_7021_FUNC_STAT` | Estado funcional. |
| `7022` | `AI_7022_REV_STAT` | Estado de revisión. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Identificador Global de Activo Individual (GIAI) de un conjunto. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Número de la unidad transformadora, con código de país ISO de tres dígitos (10 posiciones). |
| `7040` | `AI_7040_UIC_EXT` | UIC de GS1 con extensión 1 e índice del importador. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Tipo de unidad de carga UN/CEFACT. |

### Números nacionales de reembolso sanitario (NHRN)

| AI | Constante | Descripción |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Número Nacional de Reembolso Sanitario (NHRN) - Alemania PZN. |
| `711` | `AI_711_NHRN_CIP` | Número Nacional de Reembolso Sanitario (NHRN) - Francia CIP. |
| `712` | `AI_712_NHRN_CN` | Número Nacional de Reembolso Sanitario (NHRN) - España CN. |
| `713` | `AI_713_NHRN_DRN` | Número Nacional de Reembolso Sanitario (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Número Nacional de Reembolso Sanitario (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Número Nacional de Reembolso Sanitario (NHRN) - Estados Unidos de América NDC. |
| `716` | `AI_716_NHRN_AIC` | Número Nacional de Reembolso Sanitario (NHRN) - Italia AIC. |
| `717` | `AI_717_NHRN_SRN` | Número Nacional de Reembolso Sanitario (NHRN) - Costa Rica, Número de Registro Sanitario. |

### Sanidad, GMN, HIDRI, CPID, datos personales

| AI | Constante | Descripción |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Referencia de certificación (10 posiciones). |
| `7240` | `AI_7240_PROTOCOL` | Identificador de protocolo. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Tipo de soporte AIDC. |
| `7242` | `AI_7242_VCN` | Número de Control de Versión (VCN). |
| `7250` | `AI_7250_DOB` | Fecha de nacimiento (AAAAMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Fecha y hora de nacimiento (AAAAMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Sexo biológico. |
| `7253` | `AI_7253_FAMILY_NAME` | Apellido de la persona. |
| `7254` | `AI_7254_GIVEN_NAME` | Nombre de pila de la persona. |
| `7255` | `AI_7255_SUFFIX` | Sufijo del nombre de la persona. |
| `7256` | `AI_7256_FULL_NAME` | Nombre completo de la persona. |
| `7257` | `AI_7257_PERSON_ADDR` | Dirección de la persona. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Orden de nacimiento (partos múltiples). |
| `7259` | `AI_7259_BABY` | Apellido del recién nacido. |
| `8001` | `AI_8001_DIMENSIONS` | Productos en rollo (anchura, longitud, diámetro del núcleo, dirección, empalmes). |
| `8002` | `AI_8002_CMT_NO` | Identificador de teléfono móvil celular. |
| `8003` | `AI_8003_GRAI` | Identificador Global de Activo Retornable (GRAI). |
| `8004` | `AI_8004_GIAI` | Identificador Global de Activo Individual (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Precio por unidad de medida. |
| `8006` | `AI_8006_ITIP` | Identificación de una pieza individual de unidad de expedición (ITIP). |
| `8007` | `AI_8007_IBAN` | Número de Cuenta Bancaria Internacional (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Fecha y hora de producción (AAMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indicador de sensor de lectura óptica. |
| `8010` | `AI_8010_CPID` | Identificador de componente/pieza (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Número de serie del identificador de componente/pieza (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Versión del software. |
| `8013` | `AI_8013_GMN` | Número Global de Modelo (GMN). |
| `8014` | `AI_8014_MUDI` | Identificador de Registro de Dispositivo Altamente Individualizado (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Número Global de Relación de Servicio (GSRN) para identificar la relación entre una organización que ofrece servicios y el prestador del servicio. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Número Global de Relación de Servicio (GSRN) para identificar la relación entre una organización que ofrece servicios y el destinatario del servicio. |
| `8019` | `AI_8019_SRIN` | Número de Instancia de Relación de Servicio (SRIN). |
| `8020` | `AI_8020_REF_NO` | Número de referencia del comprobante de pago. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identificación de piezas de una unidad de expedición (ITIP) contenidas en una unidad logística. |
| `8030` | `AI_8030_DIGSIG` | Firma digital (DigSig). |
| `8040` | `AI_8040_IMEI` | Identidad Internacional de Equipo Móvil (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identidad Internacional de Equipo Móvil 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Número de SIM integrada. |
| `8043` | `AI_8043_PSIM` | Número de SIM física. |
| `8110` | `AI_8110` | Identificación del código de cupón para uso en América del Norte. |
| `8111` | `AI_8111_POINTS` | Puntos de fidelidad de un cupón. |
| `8112` | `AI_8112` | Identificación del código de cupón del archivo de ofertas positivas para uso en América del Norte. |
| `8200` | `AI_8200_PRODUCT_URL` | URL de embalaje extendido. |

### Uso interno / de empresa

| AI | Constante | Descripción |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Información acordada mutuamente entre socios comerciales. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Información interna de la empresa (9 posiciones). |

---

## Apéndice B — constantes de clave de interpretación

Cuando se llama a `GaiaParser.parse()` con `ParseMode.INTERPRETATION`, cada `GS1AIObjectElement` puede llevar una lista de objetos `GS1AIInterpretation` producidos por enriquecedores especializados. Utilice las constantes de `GS1Constants_Enricher` (en el paquete `tools.pantheum.gaia.gs1.constants`) como claves para localizar valores de interpretación concretos:

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

Las etiquetas de presentación **no** son constantes: residen en los catálogos localizados bajo `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indexadas por la constante de tipo. `GS1AIInterpretation.getLabel()` devuelve la etiqueta correspondiente al idioma del análisis (véase [Mensajes y etiquetas localizados](#mensajes-y-etiquetas-localizados)), con recurso al inglés cuando un catálogo omite la clave. La columna «Etiqueta de presentación» que sigue recoge el texto en español tal como se entrega en el catálogo; las claves de tipo, en cambio, son estables entre idiomas: compare siempre con la clave, nunca con la etiqueta.

### Fecha y hora

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `DATE_VALUE` | Fecha | AI de fecha (11–17, 7003, 7006, 7011, etc.) |
| `DATE_FORMAT` | Formato de fecha | AI de fecha |
| `TIME_VALUE` | Hora | AI que llevan hora (7003, 7011, 8008, etc.) |
| `TIME_FORMAT` | Formato de hora | AI que llevan hora |
| `DATETIME_VALUE` | Fecha y hora | AI de fecha y hora |
| `DATETIME_FORMAT` | Formato de fecha y hora | AI de fecha y hora |

### Fecha de cosecha

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Fecha de inicio de cosecha | AI 7007 |
| `HARVEST_END_DATE` | Fecha de fin de cosecha | AI 7007 (fin de rango opcional) |
| `HARVEST_DATE_RANGE` | Rango de fechas de cosecha | AI 7007 |

### Prefijo de empresa GS1

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefijo de empresa GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Código de miembro GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organización miembro de GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Tipo de GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Nivel de embalaje | AI 01 |
| `GTIN_CHECK_DIGIT` | Dígito de control | AI 01, 02 |

### SSCC

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Dígito de extensión | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Referencia de serie | AI 00 |
| `SSCC_CHECK_DIGIT` | Dígito de control | AI 00 |

### País (ISO 3166)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Código de país (numérico) | AI de país único (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Código de país (alfa-2) | AI de país alfa-2 |
| `COUNTRY_NAME` | Nombre del país | AI de país único |
| `COUNTRY_LIST` | Países | AI 423 — todos los nombres unidos, por ejemplo `Australia, New Zealand` |

El AI 423 (país de primera transformación) puede llevar hasta cinco países, por lo que emite un
**par numerado por país** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — seguido del único resumen
`COUNTRY_LIST`. Construya estas claves a partir de las constantes `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` y el índice de base 1, o simplemente recorra `getInterpretations()`; las
claves `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` sin sufijo **no** se emiten para el AI 423.

### Moneda (ISO 4217)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Código de moneda | AI de importe con moneda (391n, 393n) |
| `CURRENCY_ALPHA` | Código alfabético de moneda | AI de importe con moneda |
| `CURRENCY_NAME` | Nombre de la moneda | AI de importe con moneda |

### Temperatura

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatura | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Unidad de temperatura | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatura (formateada) | AI 4330–4333 |

### Sexo (ISO 5218)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `SEX_CODE` | Código de sexo | AI 7252 |
| `SEX_DESCRIPTION` | Descripción del sexo | AI 7252 |

### Especies acuáticas (FAO)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Código de especie | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nombre científico | AI 7008 |
| `SPECIES_ENGLISH` | Nombre común | AI 7008 |
| `SPECIES_FAMILY` | Familia | AI 7008 |
| `SPECIES_ORDER` | Orden | AI 7008 |

### Número de catálogo OTAN (NSN)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `NSN_FSG` | Grupo de suministro | AI 7001 |
| `NSN_FSG_NAME` | Nombre del grupo de suministro | AI 7001 |
| `NSN_FSCG` | Clase de suministro | AI 7001 |
| `NSN_FSCG_NAME` | Nombre de la clase de suministro | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Código de país | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | País | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Código de país ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Categoría NCS | AI 7001 |
| `NSN_NIIN` | Número nacional de artículo | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Productos en rollo

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Ancho del rollo (mm) | AI 8001 |
| `ROLL_LENGTH` | Longitud del rollo (m) | AI 8001 |
| `CORE_DIAMETER` | Diámetro del núcleo (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Código de dirección de bobinado | AI 8001 |
| `WINDING_DIRECTION` | Dirección de bobinado | AI 8001 |
| `SPLICES` | Empalmes | AI 8001 |

### IBAN

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Código de país | AI 8007 |
| `IBAN_COUNTRY_NAME` | País | AI 8007 |
| `IBAN_CHECK_DIGITS` | Dígitos de control | AI 8007 |
| `IBAN_CHECK_VALID` | Verificación | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Número de serie | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Dígito de control | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Organismo emisor | AI 8040, 8041 |

Los 15 dígitos se descomponen en `[ TAC (8) ][ número de serie (6) ][ dígito de control de Luhn (1) ]`, siendo el
RBI los 2 primeros dígitos del TAC: `IMEI_RBI` es, por tanto, un prefijo de `IMEI_TAC`, y no
un tramo aparte. `IMEI_FORMATTED` representa la agrupación de presentación estándar de la GSMA
`AA-BBBBBB-CCCCCC-D` (por ejemplo, `49-015420-323751-8`), que parte el TAC en la frontera
del RBI; la antigua agrupación `6-2-6-1`, que cortaba donde empezaba el ya descontinuado Final Assembly
Code, no se emite.

`IMEI_RBI_NAME` resuelve el RBI al nombre del organismo asignador mediante `ImeiRbiData`, y se
**añade en último lugar y solo cuando el código figura allí**. Esa tabla cubre tres grupos:

- **Con asignación activa** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, además de `99`
  Global Hexadecimal Administrator y `98` (reservado).
- **Rangos de prueba** — `00` y `02`–`09`, que señalan IMEI de prueba en lugar de una asignación real.
  Consúltelos con `ImeiRbiData.isTestCode(code)`.
- **Sin asignación** — organismos históricos como `49` (BZT/BAPT, Alemania), `44`
  (BABT, Reino Unido) o `91` (MSAI, India). Consúltelos con `ImeiRbiData.isNoLongerAllocating(code)`.
  Los dispositivos con estos códigos son corrientes y siguen en servicio; solo ha cesado la asignación
  de códigos nuevos, de modo que es información informativa, nunca una señal de validez.

Que falte `IMEI_RBI_NAME` significa «este RBI no está en nuestra tabla», **no** «IMEI inválido»:
la tabla se compila a partir de un listado publicado de RBI y no directamente de la GSMA, por lo que
puede ir por detrás de los organismos designados recientemente. No deduzca ningún resultado de validación de su ausencia;
el RBI no es un carácter de control. El código que recorra la lista de interpretaciones también debe
tolerar su ausencia en lugar de indexar por posición.

### Identificadores de SIM (EID / ICCID)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Categoría de industria | AI 8042 |
| `EID_BODY` | Cuerpo del EID | AI 8042 |
| `EID_CHECK_DIGIT` | Dígito de control | AI 8042 |
| `ICCID_BODY` | Cuerpo del ICCID | AI 8043 |
| `ICCID_EXTENSION` | Extensión | AI 8043 |

`SIM_MII` lleva los **dos** primeros dígitos (`89`), el par que la UIT-T E.118 asigna a las
telecomunicaciones. La propia ISO/IEC 7812 define el MII como el **primer dígito únicamente**, de modo que
`SIM_MII_NAME` resuelve la categoría a partir de ese `8` inicial mediante `Iso7812Data`, lo que da
«Healthcare, telecommunications and other future industry assignments». Para un EID bien formado
es, por tanto, un valor constante; se informa por trazabilidad respecto al estándar, no como
criterio discriminante. `Iso7812Data.nameForCode(digit)` toma un dígito suelto y
`nameForIdentifier(prefix)` acepta un prefijo más largo y lee su primer dígito.

`SIM_MII_NAME` lo emite únicamente `EidEnricher` (AI 8042). `IccidEnricher` (AI 8043)
expone `SIM_MII` sin la categoría.

### Referencia de certificación

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Número de secuencia | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Código del esquema de certificación | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Esquema de certificación | AI 7230–7239 |
| `CERT_REFERENCE` | Referencia de certificación | AI 7230–7239 |

### GS1 UIC

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `UIC_CODE` | Código UIC | AI 7040 |
| `UIC_EXTENSION_1` | Extensión 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Índice de importador | AI 7040 |

### Orden de nacimiento del recién nacido

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posición de nacimiento | AI 7258 |
| `BIRTH_TOTAL` | Total de nacimientos | AI 7258 |
| `BIRTH_SEQUENCE` | Secuencia de nacimiento | AI 7258 |

### Número mundial de modelo (GMN)

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Referencia del modelo | AI 8013 |
| `GMN_CHECK_PAIR` | Par de control | AI 8013 |

### HIDRI

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Referencia del dispositivo | AI 8014 |
| `HIDRI_CHECK_PAIR` | Par de control | AI 8014 |

### CPID

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Referencia de componente y pieza | AI 8010–8011 |

### Valores decimales y de medida

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Valor decimal | AI numéricos con decimales implícitos (31xx–36xx) |
| `DECIMAL_AMOUNT` | Importe | AI de precio (390n–395n) |
| `DECIMAL_PERCENTAGE` | Porcentaje | AI 394n |
| `DECIMAL_PLACES` | Decimales | Junto a `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Formato de porcentaje | AI 394n |
| `ISO_UNIT_CODE` | Código de unidad ISO | AI de medida |
| `ISO_UNIT_NAME` | Nombre de unidad ISO | AI de medida |
| `MONETARY_AMOUNT` | Importe monetario | AI de precio |
| `MONETARY_AMOUNT_DISPLAY` | Importe monetario (formateado) | AI de precio |

### Coordenadas geográficas

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `LATITUDE` | Latitud | AI 4309 |
| `LONGITUDE` | Longitud | AI 4309 |
| `GEO_COORDINATES` | Coordenadas geográficas | AI 4309 |
| `LATITUDE_DMS` | Latitud (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitud (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Coordenadas geográficas (DMS) | AI 4309 |

### Método de producción

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Código de método de producción | AI 7010 |
| `PRODUCTION_METHOD` | Método de producción | AI 7010 |

### Tipo de soporte AIDC

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Código de tipo de medio AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Tipo de medio AIDC | AI 7241 |

### Pieza del total

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Número de pieza | AI 8006 |
| `PIECE_TOTAL` | Total de piezas | AI 8006 |
| `PIECE_OF_TOTAL` | Pieza del total | AI 8006 |

### Divisiones en componentes

Claves emitidas por las divisiones en componentes declarativas de `content/ai-content.json` y no
por un enriquecedor Java: exponen las partes con nombre del valor de un AI compuesto. A diferencia de todas
las demás claves de este apéndice, estas **no tienen constante en `GS1Constants_Enricher`**: compare
la cadena literal, o lea el tipo con `GS1AIInterpretation.getType()`.

| Clave de tipo | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Dígito de control | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Número de serie | AI 253, 255, 8003 |
| `POSTAL_CODE` | Código postal | AI 421 |
| `PROCESSOR_ID` | Identificador del procesador | AI 7030–7039 |

Tenga en cuenta que aquí `CHECK_DIGIT` es la clave genérica de división en componentes, distinta de las claves
específicas de los enriquecedores `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` y
`EID_CHECK_DIGIT` enumeradas más arriba.

### Varios

| Constante de clave | Etiqueta de presentación | Producida por |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Valor | AI booleanos / de indicador (4321–4323) |
| `DECODED_TEXT` | Texto decodificado | AI de texto libre |
