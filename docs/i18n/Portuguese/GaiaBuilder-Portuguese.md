# GaiaBuilder — Guia do programador

## Índice

1. [Visão geral](#visão-geral)
2. [Acerca da GS1 e das General Specifications](#acerca-da-gs1-e-das-general-specifications)
3. [Início rápido](#início-rápido)
4. [Como funciona](#como-funciona)
5. [Construir cadeias de elementos](#construir-cadeias-de-elementos)
   - [Os AI de atributo exigem a sua chave de identificação](#os-ai-de-atributo-exigem-a-sua-chave-de-identificação)
6. [Construir URI Digital Link](#construir-uri-digital-link)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [Validação e erros](#validação-e-erros)
   - [Métodos de construção que lançam exceções](#métodos-de-construção-que-lançam-exceções)
   - [Métodos tryBuild\* sem exceções](#métodos-trybuild-sem-exceções)
   - [Idioma das mensagens de erro](#idioma-das-mensagens-de-erro)
   - [BuildResult](#buildresult)
9. [Dígitos de controlo](#dígitos-de-controlo)
10. [Segurança perante threads](#segurança-perante-threads)
11. [Referência da API](#referência-da-api)

---

## Visão geral

`GaiaBuilder` é o inverso do [`GaiaParser`](GaiaParser-Portuguese.md): transforma um conjunto de pares identificador de aplicação (AI) / valor numa **cadeia de elementos** GS1 ou num **URI GS1 Digital Link** bem formados. Você fornece os AI e os seus valores de dados completos; o builder monta-os, valida o resultado com o mesmo motor que o `GaiaParser` utiliza e produz a saída.

Uma vez que o builder valida *analisando a sua própria saída candidata*, tudo o que devolve tem garantia de ser analisado sem erros pelo `GaiaParser` — os dois nunca podem divergir quanto ao que está bem formado.

**Classe de entrada:** `tools.pantheum.gaia.GaiaBuilder`

---

## Acerca da GS1 e das General Specifications

A **GS1** é uma organização mundial sem fins lucrativos que desenvolve e mantém normas abertas para a identificação e o intercâmbio de dados nas cadeias de abastecimento. As suas normas são utilizadas na distribuição, na saúde, na logística, na restauração e em muitos outros setores, abrangendo desde os códigos de barras dos produtos de consumo até ao rastreio serializado de doses farmacêuticas.

A referência com autoridade para tudo o que este builder implementa são as **GS1 General Specifications** — um único documento que define:

- Todos os códigos de identificador de aplicação (AI), os seus títulos de dados, formatos e regras de validação
- As regras de sintaxe para compor e codificar cadeias de elementos AI
- Os requisitos de simbologia dos códigos de barras e a atribuição dos identificadores de simbologia AIM
- Os algoritmos do dígito de controlo e do carácter de controlo
- A resolução de anos com dois dígitos (a regra da janela deslizante)
- As especificações de Data Matrix, QR Code, GS1-128, GS1 DataBar e demais suportes de dados

As GS1 General Specifications são atualizadas anualmente. A edição em vigor e os recursos associados estão disponíveis em:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

O GAIA implementa a **versão 26.0 (ratificada em janeiro de 2026)** das GS1 General Specifications.

Os URI GS1 Digital Link regem-se por uma norma complementar, **GS1 Digital Link: URI Syntax**, que define as chaves de identificação primárias, a ordem dos qualificadores de chave e a codificação dos atributos de dados que o builder aplica ao gerar os URI Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

O GAIA implementa a **versão 1.7.0 (ratificada em agosto de 2026)** da norma GS1 Digital Link: URI Syntax.

Ao longo deste documento, as referências a secções remetem para as GS1 General Specifications (por exemplo, «Table 7-5», «section 7.12»), com exceção dos números de secção do Digital Link (por exemplo, «§4.9», «§4.12»), que remetem para a norma GS1 Digital Link: URI Syntax.

---

## Início rápido

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

Prefira as constantes `GS1Constants_AICodes` às cadeias de AI em bruto (consulte o [anexo A do guia do analisador](GaiaParser-Portuguese.md#anexo-a--constantes-de-cadeia-dos-ai)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## Como funciona

Cada construção segue o mesmo percurso:

1. **Montagem** — os pares AI/valor são concatenados numa cadeia de elementos candidata. É inserido um separador de grupo FNC1 (`0x1D`) depois de cada AI que *exija separador* e que não seja o último elemento. Os AI de comprimento predefinido (GTIN, datas, medidas de comprimento fixo) não levam separador; todos os outros levam. (Os AI não reconhecidos nunca chegam a este passo — `ai(...)` rejeita-os de imediato; consulte [Construir cadeias de elementos](#construir-cadeias-de-elementos).)
2. **Validação** — a candidata é analisada no modo `CONTENT` pelo `GaiaParser`. Cada valor é confrontado com o formato e o dígito de controlo do seu AI, e são aplicadas as regras estruturais (emparelhamentos de AI obrigatórios ou excluídos). Se a análise não for válida, a construção falha.
3. **Geração** —
   - Para uma cadeia de elementos, é devolvido o `toElementString()` do objeto validado.
   - Para um Digital Link, é atribuído a cada elemento o seu papel DL (chave primária, qualificador de chave ou atributo de dados), a sequência de qualificadores de chave é validada, o URI é emitido e o URI emitido é **novamente analisado para confirmar que faz uma ida e volta válida enquanto Digital Link** — uma verificação defensiva sobre a montagem da cadeia e a codificação com percentagem. Se a ida e volta falhar, é lançada uma `GaiaBuilderException`.

Isto reproduz a lógica de reconstrução do `DLSyntaxParser`, pelo que a colocação dos separadores e a validação são idênticas ao que o analisador espera.

---

## Construir cadeias de elementos

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- O **AI** é validado de imediato: `ai(...)` lança `IllegalArgumentException` se não for um identificador de aplicação GS1 reconhecido. (O builder concatena o AI e o valor antes de analisar, pelo que um AI não reconhecido ou demasiado longo como `"99999"` tem de ser detetado aqui — caso contrário seria silenciosamente redividido em tokens como um AI diferente.) O **valor**, esse, é validado mais tarde, no momento da construção.
- Os valores têm de estar **completos**, incluindo qualquer dígito de controlo. O builder não calcula nem acrescenta dígitos de controlo por si — consulte [Dígitos de controlo](#dígitos-de-controlo).
- Os AI são emitidos pela ordem em que os acrescenta. O builder insere os separadores FNC1 onde a sintaxe GS1 os exige; não deve acrescentá-los.
- Uma construção **sem qualquer AI** lança `GaiaBuilderException("No AIs supplied")` com uma lista `getErrors()` vazia — a única falha que não transporta qualquer `GaiaError`.
- Um AI cujo valor infrinja a sua regra de formato ou de dígito de controlo faz falhar a construção.

### Os AI de atributo exigem a sua chave de identificação

A maioria dos AI são *atributos* que as GS1 General Specifications exigem que sejam acompanhados de uma chave de identificação, e o builder fá-lo cumprir: valida através da fase de sintaxe completa, sem qualquer forma de o desativar. Um lote ou um número de série isolados **não** constituem uma cadeia de elementos válida:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

As chaves de identificação (GTIN `01`, SSCC `00`, GLN `414`, …) e os AI de uso interno da empresa (`90`–`99`) podem aparecer isolados com toda a legitimidade. Tudo o resto precisa do seu acompanhante.

> Ao `GaiaParser` pode dizer-se que omita esta verificação, com `ParseConfig.skipRequiresCheck(true)`; o `GaiaBuilder` não expõe deliberadamente qualquer equivalente — destina-se a produzir saída conforme com as normas. Para montar uma cadeia de elementos deliberadamente parcial, concatene-a você mesmo e analise-a com a verificação desativada.

---

## Construir URI Digital Link

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

Um Digital Link válido exige exatamente uma **chave de identificação primária** (por exemplo, GTIN `01`, GLN `414`, SSCC `00`). O builder classifica cada AI fornecido:

| Papel | Gerado como | Exemplo |
|------|-------------|---------|
| Chave de identificação primária | Segmento de caminho a seguir ao domínio ou ao prefixo | `/01/09506000134352` |
| Qualificador de chave (CPV `22`, lote `10`, série `21`, …) | Segmentos de caminho seguintes, pela **ordem canónica do §4.9** (e não pela ordem em que os acrescentou) | `/10/LOT-ABC` |
| Atributo de dados (tudo o resto) | Parâmetros de consulta, **ordenados lexicalmente por chave de AI** (§4.12) | `?17=271231` |

Como os qualificadores são reordenados na emissão, fornecê-los fora de sequência não é problema: `ai("21", …)` antes de `ai("10", …)` gera na mesma `/10/LOT/21/SER`. Só o *conjunto* tem de ser admissível para a chave primária.

Os valores, tanto no caminho como na consulta, são codificados com percentagem.

A construção **falha** (lança `GaiaBuilderException`, ou devolve um `BuildResult` falhado) quando:

- **não** existe qualquer chave de identificação primária entre os AI;
- existe **mais do que uma** chave de identificação primária;
- um AI é **proibido** nos Digital Link (`03`, `8014`);
- a **sequência de qualificadores de chave** não é válida para a chave primária escolhida (por exemplo, um qualificador que não pertence a essa chave, ou qualificadores fora da ordem permitida).

---

## BuilderDigitalLinkConfig

Passe uma `BuilderDigitalLinkConfig` para controlar o esquema, o domínio, o prefixo de caminho, os parâmetros de consulta adicionais e o fragmento:

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

| Método do construtor | Finalidade | Predefinição |
|----------------|---------|---------|
| `scheme(String)` | Esquema do URI; tem de ser `http` ou `https` | `https` |
| `domain(String)` | Autoridade — anfitrião ou `anfitrião:porta` | `id.gs1.org` |
| `pathPrefix(String)` | Segmentos de caminho anteriores à primeira chave primária; as barras iniciais e finais são normalizadas | *(nenhum)* |
| `baseUrl(String)` | Atalho que decompõe um URL em `scheme` + `domain` + `pathPrefix` | — |
| `addQueryParam(String, String)` | Parâmetro de consulta adicional, acrescentado **depois** dos atributos de dados dos AI, pela ordem de inserção; codificado com percentagem | — |
| `fragment(String)` | Fragmento do URL (sem o `#` inicial); codificado com percentagem | *(nenhum)* |

`build()` valida a configuração de imediato: um esquema que não seja `http(s)` ou um domínio vazio lançam `IllegalArgumentException`.

- `BuilderDigitalLinkConfig.canonical()` (alias `defaultConfig()`) é a predefinição `https://id.gs1.org` sem extras — exatamente o que `buildDigitalLinkUri()` sem argumentos utiliza e o que `GS1AIObject.getCanonicalDigitalLink()` produz.
- `baseUrl("http://id.example.org:8080/r")` → esquema `http`, domínio `id.example.org:8080`, prefixo de caminho `/r`.
- Os parâmetros de consulta adicionais seguem-se sempre aos atributos derivados dos AI, preservando assim a ordem canónica dos AI (§4.12).

`BuilderDigitalLinkConfig` é imutável; reutilize livremente a mesma instância.

---

## Validação e erros

### Métodos de construção que lançam exceções

`buildElementString()`, `buildDigitalLinkUri()` e `buildDigitalLinkUri(BuilderDigitalLinkConfig)` lançam uma **`GaiaBuilderException`** (uma `RuntimeException` não verificada) quando os AI não conseguem formar uma saída bem formada:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- Perante falhas **de conteúdo** (dígito de controlo errado, formato não conforme, AI em falta ou excluído), `getErrors()` transporta os `GaiaError` do analisador — os mesmos objetos [descritos no guia do analisador](GaiaParser-Portuguese.md#gaiaerror).
- Perante falhas **estruturais de Digital Link** (sem chave primária, mais do que uma chave primária, AI proibido, sequência de qualificadores de chave inválida), `getErrors()` transporta um único `GaiaError` (código `GE-L008`, `GE-L012`, `GE-L013` ou `GE-L014`) localizado no idioma do builder.

### Métodos tryBuild\* sem exceções

Quando a entrada provém do utilizador e a falha é um desfecho esperado e recuperável, use as variantes `tryBuild*` em vez de um fluxo de controlo assente em exceções. Devolvem um [`BuildResult`](#buildresult) em vez de lançarem:

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

| Com exceção | Sem exceção |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

Cada método `tryBuild*` partilha o mesmo núcleo de validação do seu gémeo que lança exceções; muda apenas a fronteira da falha.

### Idioma das mensagens de erro

Os erros de validação de conteúdo provêm do catálogo de erros localizado. Chame `language(...)` para escolher o idioma das mensagens dos `GaiaError` transportados por `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()`; a predefinição é o inglês:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

É a mesma definição `GaiaConstants.Language` que o `GaiaParser` aceita através de `ParseConfig`, pelo que o builder e o analisador se localizam de forma idêntica.

Tanto as mensagens dos `GaiaError` **de conteúdo** como as falhas **estruturais de Digital Link** (sem chave primária, mais do que uma chave primária, AI proibido, sequência de qualificadores de chave inválida) são localizadas através do catálogo de erros partilhado — estas últimas com os códigos `GE-L008`, `GE-L012`, `GE-L013` e `GE-L014`.

### BuildResult

`BuildResult` (no pacote `tools.pantheum.gaia.result`) é um tipo de valor imutável que descreve o desfecho de uma chamada `tryBuild*`:

| Método | Em caso de sucesso | Em caso de falha |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | a cadeia gerada | `null` |
| `getMessage()` | `null` | descrição da falha |
| `getErrors()` | lista vazia | os erros de validação (os mesmos de `GaiaBuilderException.getErrors()`) |

---

## Dígitos de controlo

O builder valida os dígitos de controlo, mas **não** os calcula: os valores já têm de incluir o seu. Para calcular um, use `GS1Utils.calculateCheckDigit`:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` aplica o algoritmo GS1 padrão módulo 10 aos dígitos fornecidos e devolve o dígito de controlo `0–9`, ou `-1` se a entrada for nula, vazia ou não numérica.

---

## Segurança perante threads

O `GaiaBuilder` **não** é seguro perante threads e destina-se a uma utilização única: chame `create()`, acrescente os AI, construa uma vez. Crie um novo builder por cada saída; não partilhe um entre threads.

`BuilderDigitalLinkConfig` (e os `BuildResult` que produz) são imutáveis e podem ser partilhados livremente — construa uma configuração uma só vez no arranque e reutilize-a em muitos builders.

---

## Referência da API

### `GaiaBuilder`

| Método | Descrição |
|--------|-------------|
| `static GaiaBuilder create()` | Inicia um novo builder, vazio. |
| `GaiaBuilder ai(String ai, String value)` | Acrescenta um AI e o seu valor completo. Lança `IllegalArgumentException` se algum dos dois for `null`, ou se `ai` não for um identificador de aplicação GS1 reconhecido. |
| `GaiaBuilder language(GaiaConstants.Language language)` | Define o idioma das mensagens de erro da validação de conteúdo (inglês por predefinição). `null` é ignorado. |
| `String buildElementString()` | Gera uma cadeia de elementos GS1. Lança `GaiaBuilderException` em caso de falha. |
| `String buildDigitalLinkUri()` | Gera um URI Digital Link canónico. Lança `GaiaBuilderException` em caso de falha. |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Gera um URI Digital Link segundo `config`. Lança `GaiaBuilderException` em caso de falha. |
| `BuildResult tryBuildElementString()` | Construção de uma cadeia de elementos sem exceções. |
| `BuildResult tryBuildDigitalLinkUri()` | Construção de um Digital Link canónico sem exceções. |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | Construção de um Digital Link segundo `config`, sem exceções. |

### `BuilderDigitalLinkConfig`

| Membro | Descrição |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | A predefinição `https://id.gs1.org`. |
| `static Builder builder()` | Um novo construtor de configuração. |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | Esquema, autoridade e prefixo de caminho resolvidos. |
| `getExtraQueryParams()` | Parâmetros de consulta adicionais, pela ordem de inserção. |
| `getFragment()` | Fragmento, ou `null`. |

### `GaiaBuilderException`

| Membro | Descrição |
|--------|-------------|
| `getErrors()` | Os `GaiaError` que causaram a falha — os erros do analisador numa falha de conteúdo, ou um único erro estrutural de Digital Link (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`). Nunca `null`. |

### `BuildResult`

| Membro | Descrição |
|--------|-------------|
| `isSuccess()` | Se a construção foi bem-sucedida. |
| `getValue()` | A saída gerada em caso de sucesso; `null` em caso de falha. |
| `getMessage()` | A descrição da falha em caso de falha; `null` em caso de sucesso. |
| `getErrors()` | Os erros de validação em caso de falha; lista vazia em caso de sucesso. Nunca `null`. |
| `getTiming()` | O `ProcessingTiming` da construção (hora de início, duração do processamento), ou `null`. |

---

Consulte também: **[GaiaParser — Guia do programador](GaiaParser-Portuguese.md)** para a vertente da análise, o modelo de elemento AI, a referência de erros e os anexos das constantes de AI e de interpretação.
