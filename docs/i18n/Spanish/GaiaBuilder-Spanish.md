# GaiaBuilder — Guía del desarrollador

## Índice

1. [Visión general](#visión-general)
2. [Acerca de GS1 y las especificaciones generales](#acerca-de-gs1-y-las-especificaciones-generales)
3. [Inicio rápido](#inicio-rápido)
4. [Cómo funciona](#cómo-funciona)
5. [Construir cadenas de elementos](#construir-cadenas-de-elementos)
   - [Los AI de atributo necesitan su clave de identificación](#los-ai-de-atributo-necesitan-su-clave-de-identificación)
6. [Construir URI de Digital Link](#construir-uri-de-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validación y errores](#validación-y-errores)
   - [Métodos de construcción que lanzan excepciones](#métodos-de-construcción-que-lanzan-excepciones)
   - [Métodos tryBuild\* sin excepciones](#métodos-trybuild-sin-excepciones)
   - [Idioma de los mensajes de error](#idioma-de-los-mensajes-de-error)
   - [BuildResult](#buildresult)
9. [Dígitos de control](#dígitos-de-control)
10. [Seguridad frente a hilos](#seguridad-frente-a-hilos)
11. [Referencia de la API](#referencia-de-la-api)

---

## Visión general

`GaiaBuilder` es el inverso de [`GaiaParser`](GaiaParser-Spanish.md): convierte un conjunto de pares identificador de aplicación (AI) / valor en una **cadena de elementos** de GS1 o en un **URI de GS1 Digital Link** bien formados. Usted aporta los AI y sus valores de datos completos; el constructor los ensambla, valida el resultado con el mismo motor que utiliza `GaiaParser` y produce la salida.

Dado que el constructor valida *analizando su propia salida candidata*, todo lo que devuelve tiene garantizado un análisis limpio con `GaiaParser`: ambos nunca pueden discrepar sobre qué está bien formado.

**Clase del punto de entrada:** `tools.pantheum.gaia.GaiaBuilder`

---

## Acerca de GS1 y las especificaciones generales

**GS1** es una organización mundial sin ánimo de lucro que desarrolla y mantiene estándares abiertos para la identificación y el intercambio de datos en la cadena de suministro. Sus estándares se utilizan en distribución, sanidad, logística, restauración y muchos otros sectores, y abarcan desde los códigos de barras de los productos de consumo hasta el seguimiento serializado de dosis farmacéuticas.

La referencia autorizada para todo lo que implementa este constructor es el documento **GS1 General Specifications**, un único documento que define:

- Todos los códigos de identificador de aplicación (AI), sus títulos de datos, formatos y reglas de validación
- Las reglas de sintaxis para construir y codificar cadenas de elementos AI
- Los requisitos de simbología de códigos de barras y la asignación de identificadores de simbología AIM
- Los algoritmos de dígito de control y de carácter de control
- La resolución de años de dos dígitos (la regla de la ventana deslizante)
- Las especificaciones de Data Matrix, QR Code, GS1-128, GS1 DataBar y otros portadores de datos

Las GS1 General Specifications se actualizan anualmente. La edición vigente y los recursos complementarios están disponibles en:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA implementa la **versión 26.0 (ratificada en enero de 2026)** de las GS1 General Specifications.

Los URI de GS1 Digital Link se rigen por un estándar complementario, **GS1 Digital Link: URI Syntax**, que define las claves de identificación primarias, el orden de los calificadores de clave y la codificación de los atributos de datos que el constructor aplica al generar URI de Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA implementa la **versión 1.7.0 (ratificada en agosto de 2026)** del estándar GS1 Digital Link: URI Syntax.

En todo este documento, las referencias a secciones remiten a las GS1 General Specifications (por ejemplo, «Table 7-5», «section 7.12»), salvo los números de sección de Digital Link (por ejemplo, «§4.9», «§4.12»), que remiten al estándar GS1 Digital Link: URI Syntax.

---

## Inicio rápido

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

Prefiera las constantes `GS1Constants_AICodes` a las cadenas de AI en bruto (véase el [apéndice A de la guía del analizador](GaiaParser-Spanish.md#apéndice-a--constantes-de-cadena-de-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Cómo funciona

Toda construcción sigue el mismo recorrido:

1. **Ensamblado** — los pares AI/valor se concatenan en una cadena de elementos candidata. Se inserta un separador de grupo FNC1 (`0x1D`) después de cada AI que *requiere separador* y que no es el último elemento. Los AI de longitud predefinida (GTIN, fechas, medidas de longitud fija) no llevan separador; todos los demás sí. (Los AI no reconocidos nunca llegan a este paso: `ai(...)` los rechaza de inmediato; véase [Construir cadenas de elementos](#construir-cadenas-de-elementos).)
2. **Validación** — la candidata se analiza en modo `CONTENT` con `GaiaParser`. Cada valor se contrasta con el formato y el dígito de control de su AI, y se aplican las reglas estructurales (combinaciones de AI obligatorias o excluidas). Si el análisis no es válido, la construcción falla.
3. **Generación** —
   - Para una cadena de elementos, se devuelve el `toElementString()` del objeto validado.
   - Para un Digital Link, se asigna a cada elemento su función DL (clave primaria, calificador de clave o atributo de datos), se valida la secuencia de calificadores de clave, se emite el URI y este se **vuelve a analizar para confirmar que hace un viaje de ida y vuelta válido como Digital Link**: una comprobación defensiva sobre el ensamblado de la cadena y la codificación con porcentaje. Si el viaje de ida y vuelta falla, se lanza una `GaiaBuilderException`.

Esto refleja la lógica de reconstrucción de `DLSyntaxParser`, de modo que la colocación de los separadores y la validación son idénticas a lo que espera el analizador.

---

## Construir cadenas de elementos

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- El **AI** se valida de inmediato: `ai(...)` lanza `IllegalArgumentException` si no es un identificador de aplicación de GS1 reconocido. (El constructor concatena el AI y el valor antes de analizar, así que un AI no reconocido o demasiado largo como `"99999"` debe detectarse aquí; de lo contrario se volvería a tokenizar en silencio como un AI distinto.) El **valor** se valida más tarde, al construir.
- Los valores deben estar **completos**, incluido cualquier dígito de control. El constructor no calcula ni añade dígitos de control por usted — véase [Dígitos de control](#dígitos-de-control).
- Los AI se emiten en el orden en que se añaden. El constructor inserta los separadores FNC1 donde la sintaxis de GS1 los exige; usted no debe añadirlos.
- Construir **sin ningún AI** lanza `GaiaBuilderException("No AIs supplied")` con una lista `getErrors()` vacía: el único fallo que no lleva ningún `GaiaError`.
- Un AI cuyo valor incumple su regla de formato o de dígito de control hace fallar la construcción.

### Los AI de atributo necesitan su clave de identificación

La mayoría de los AI son *atributos* que las GS1 General Specifications exigen acompañar de una clave de identificación, y el constructor lo hace cumplir: valida a través de la etapa de sintaxis completa, sin forma de desactivarlo. Un lote o un número de serie por sí solos **no** son una cadena de elementos válida:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

Las claves de identificación (GTIN `01`, SSCC `00`, GLN `414`, …) y los AI de uso interno de la empresa (`90`–`99`) pueden aparecer solos con toda legitimidad. Todo lo demás necesita su acompañante.

> A `GaiaParser` se le puede indicar que omita esta comprobación con `ParseConfig.skipRequiresCheck(true)`; `GaiaBuilder` no expone deliberadamente ningún equivalente, porque está pensado para emitir una salida conforme a los estándares. Para ensamblar una cadena de elementos deliberadamente parcial, concaténela usted mismo y analícela con la comprobación desactivada.

---

## Construir URI de Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Un Digital Link válido exige exactamente una **clave de identificación primaria** (por ejemplo, GTIN `01`, GLN `414`, SSCC `00`). El constructor clasifica cada AI suministrado:

| Función | Se genera como | Ejemplo |
|------|-------------|---------|
| Clave de identificación primaria | Segmento de ruta tras el dominio o el prefijo | `/01/09506000134352` |
| Calificador de clave (CPV `22`, lote `10`, serie `21`, …) | Segmentos de ruta siguientes, en el **orden canónico del §4.9** (no en el orden en que los añadió) | `/10/LOT-ABC` |
| Atributo de datos (todo lo demás) | Parámetros de consulta, **ordenados lexicográficamente por clave de AI** (§4.12) | `?17=271231` |

Como los calificadores se reordenan al emitirlos, no hay problema en suministrarlos desordenados: `ai("21", …)` antes de `ai("10", …)` genera igualmente `/10/LOT/21/SER`. Solo el *conjunto* tiene que ser admisible para la clave primaria.

Los valores, tanto en la ruta como en la consulta, se codifican con porcentaje.

La construcción **falla** (lanza `GaiaBuilderException`, o devuelve un `BuildResult` fallido) cuando:

- **no** hay ninguna clave de identificación primaria entre los AI;
- hay **más de una** clave de identificación primaria;
- un AI está **prohibido** en los Digital Links (`03`, `8014`);
- la **secuencia de calificadores de clave** no es válida para la clave primaria elegida (por ejemplo, un calificador que no pertenece a esa clave, o calificadores fuera del orden permitido).

---

## BuilderDigitalLinkConfig

Pase un `BuilderDigitalLinkConfig` para controlar el esquema, el dominio, el prefijo de ruta, los parámetros de consulta adicionales y el fragmento:

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

| Método del constructor | Finalidad | Predeterminado |
|----------------|---------|---------|
| `scheme(String)` | Esquema del URI; debe ser `http` o `https` | `https` |
| `domain(String)` | Autoridad: host o `host:puerto` | `id.gs1.org` |
| `pathPrefix(String)` | Segmentos de ruta anteriores a la primera clave primaria; las barras inicial y final se normalizan | *(ninguno)* |
| `baseUrl(String)` | Atajo que descompone una URL en `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Parámetro de consulta adicional, añadido **después** de los atributos de datos de AI, en orden de inserción; codificado con porcentaje | — |
| `fragment(String)` | Fragmento de URL (sin el `#` inicial); codificado con porcentaje | *(ninguno)* |

`build()` valida la configuración de inmediato: un esquema que no sea `http(s)` o un dominio vacío lanzan `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) es el valor predeterminado `https://id.gs1.org` sin extras: exactamente lo que usa `buildDigitalLinkUri()` sin argumentos y lo que produce `GS1AIObject.getCanonicalDigitalLink()`.
- `baseUrl("http://id.example.org:8080/r")` → esquema `http`, dominio `id.example.org:8080`, prefijo de ruta `/r`.
- Los parámetros de consulta adicionales siguen siempre a los atributos derivados de los AI, de modo que se preserva el orden canónico de los AI (§4.12).

`BuilderDigitalLinkConfig` es inmutable; reutilice una misma instancia sin reservas.

---

## Validación y errores

### Métodos de construcción que lanzan excepciones

`buildElementString()`, `buildDigitalLinkUri()` y `buildDigitalLinkUri(BuilderDigitalLinkConfig)` lanzan **`GaiaBuilderException`** (una `RuntimeException` no comprobada) cuando los AI no pueden formar una salida bien formada:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Ante fallos de **contenido** (dígito de control incorrecto, formato no conforme, AI ausente o excluido), `getErrors()` lleva los `GaiaError` del analizador: los mismos objetos [descritos en la guía del analizador](GaiaParser-Spanish.md#gaiaerror).
- Ante fallos **estructurales de Digital Link** (sin clave primaria, más de una clave primaria, AI prohibido, secuencia de calificadores de clave no válida), `getErrors()` lleva un único `GaiaError` (código `GE-L008`, `GE-L012`, `GE-L013` o `GE-L014`) localizado al idioma del constructor.

### Métodos tryBuild\* sin excepciones

Cuando la entrada procede del usuario y el fallo es un resultado esperado y recuperable, use las variantes `tryBuild*` en lugar del control de flujo por excepciones. Devuelven un [`BuildResult`](#buildresult) en lugar de lanzar una excepción:

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

| Con excepción | Sin excepción |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Cada método `tryBuild*` comparte el mismo núcleo de validación que su gemelo con excepción; solo cambia la frontera del fallo.

### Idioma de los mensajes de error

Los errores de validación de contenido proceden del catálogo de errores localizado. Llame a `language(...)` para elegir el idioma de los mensajes de los `GaiaError` que llevan `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; el valor predeterminado es el inglés:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

Es el mismo ajuste `GaiaConstants.Language` que `GaiaParser` acepta mediante `ParseConfig`, de modo que el constructor y el analizador se localizan de forma idéntica.

Tanto los mensajes de los `GaiaError` de **contenido** como los fallos **estructurales de Digital Link** (sin clave primaria, más de una clave primaria, AI prohibido, secuencia de calificadores de clave no válida) se localizan mediante el catálogo de errores compartido; estos últimos, con los códigos `GE-L008`, `GE-L012`, `GE-L013` y `GE-L014`.

### BuildResult

`BuildResult` (en el paquete `tools.pantheum.gaia.result`) es un tipo de valor inmutable que describe el desenlace de una llamada `tryBuild*`:

| Método | Si tiene éxito | Si falla |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | la cadena generada | `null` |
| `getMessage()` | `null` | descripción del fallo |
| `getErrors()` | lista vacía | los errores de validación (los mismos que `GaiaBuilderException.getErrors()`) |

---

## Dígitos de control

El constructor valida los dígitos de control, pero **no** los calcula: los valores ya deben incluir el suyo. Para calcular uno, utilice `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` aplica el algoritmo estándar módulo 10 de GS1 a los dígitos suministrados y devuelve el dígito de control `0–9`, o `-1` si la entrada es nula, vacía o no numérica.

---

## Seguridad frente a hilos

`GaiaBuilder` **no** es seguro frente a hilos y está pensado para un solo uso: llame a `create()`, añada los AI y construya una vez. Cree un constructor nuevo por cada salida; no comparta uno entre hilos.

`BuilderDigitalLinkConfig` (y los `BuildResult` que produce) son inmutables y pueden compartirse sin reservas: construya una configuración una vez al arrancar y reutilícela en muchos constructores.

---

## Referencia de la API

### `GaiaBuilder`

| Método | Descripción |
|--------|-------------|
| `static GaiaBuilder create()` | Inicia un constructor nuevo y vacío. |
| `GaiaBuilder ai(String ai, String value)` | Añade un AI y su valor completo. Lanza `IllegalArgumentException` si alguno de los dos es `null`, o si `ai` no es un identificador de aplicación de GS1 reconocido. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Establece el idioma de los mensajes de error de validación de contenido (inglés de forma predeterminada). Se ignora `null`. |
| `String buildElementString()` | Genera una cadena de elementos de GS1. Lanza `GaiaBuilderException` si falla. |
| `String buildDigitalLinkUri()` | Genera un URI canónico de Digital Link. Lanza `GaiaBuilderException` si falla. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Genera un URI de Digital Link según `config`. Lanza `GaiaBuilderException` si falla. |
| `BuildResult tryBuildElementString()` | Construcción de cadena de elementos sin excepciones. |
| `BuildResult tryBuildDigitalLinkUri()` | Construcción de Digital Link canónico sin excepciones. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Construcción de Digital Link según `config`, sin excepciones. |

### `BuilderDigitalLinkConfig`

| Miembro | Descripción |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | El valor predeterminado `https://id.gs1.org`. |
| `static Builder builder()` | Un nuevo constructor de configuración. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Esquema, autoridad y prefijo de ruta resueltos. |
| `getExtraQueryParams()` | Parámetros de consulta adicionales, en orden de inserción. |
| `getFragment()` | Fragmento, o `null`. |

### `GaiaBuilderException`

| Miembro | Descripción |
|--------|-------------|
| `getErrors()` | Los `GaiaError` que provocaron el fallo: los errores del analizador ante un fallo de contenido, o un único error estructural de Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nunca `null`. |

### `BuildResult`

| Miembro | Descripción |
|--------|-------------|
| `isSuccess()` | Si la construcción tuvo éxito. |
| `getValue()` | La salida generada si tuvo éxito; `null` si falló. |
| `getMessage()` | La descripción del fallo si falló; `null` si tuvo éxito. |
| `getErrors()` | Los errores de validación si falló; lista vacía si tuvo éxito. Nunca `null`. |
| `getTiming()` | El `ProcessingTiming` de la construcción (hora de inicio, duración del procesamiento), o `null`. |

---

Véase también: **[GaiaParser — Guía del desarrollador](GaiaParser-Spanish.md)** para la vertiente de análisis, el modelo de elemento AI, la referencia de errores y los apéndices de constantes de AI y de interpretación.
