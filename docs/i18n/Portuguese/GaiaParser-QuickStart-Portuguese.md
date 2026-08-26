# GaiaParser — Início rápido

Transforme o payload de um código de barras GS1 em dados estruturados, validados e legíveis por pessoas
em cerca de dez minutos. Este é o caminho curto; o **[guia do programador do GaiaParser](GaiaParser-Portuguese.md)** é a
referência completa, e o **[GaiaBuilder](GaiaBuilder-Portuguese.md)** cobre o sentido inverso
(a construção de cadeias de elementos e de URI Digital Link).

## Conteúdo

1. [Acrescentar o Gaia ao seu projeto](#1-acrescentar-o-gaia-ao-seu-projeto)
2. [Analisar algo](#2-analisar-algo)
3. [Ler o resultado](#3-ler-o-resultado)
4. [Tratar uma análise falhada](#4-tratar-uma-análise-falhada)
5. [Duas coisas que o vão apanhar](#5-duas-coisas-que-o-vão-apanhar)
6. [Os prefixos dos leitores e os Digital Link funcionam de imediato](#6-os-prefixos-dos-leitores-e-os-digital-link-funcionam-de-imediato)
7. [Fazer menos trabalho: os modos de análise](#7-fazer-menos-trabalho-os-modos-de-análise)
8. [Mudar o idioma e o formato da data](#8-mudar-o-idioma-e-o-formato-da-data)
9. [Limpar entradas desarrumadas](#9-limpar-entradas-desarrumadas)
10. [Para onde ir a seguir](#10-para-onde-ir-a-seguir)

---

## 1. Acrescentar o Gaia ao seu projeto

O Gaia não é publicado no Maven Central, pelo que deve compilar o módulo principal uma vez e instalá-lo no seu
repositório local:

```bash
cd gaia && mvn install
```

Depois, declare a dependência:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

É toda a lista de dependências que tem de escrever. O jar é leve, pelo que a única
dependência de âmbito de compilação do Gaia — `com.fasterxml.jackson.core:jackson-databind` — chega
de forma transitiva; se a sua compilação já fixar uma versão do Jackson, prevalece essa e é essa que o Gaia usa.
O Gaia tem como alvo o **Java 11**, e o mesmo jar funciona inalterado em qualquer JVM posterior.

> Omitir a bateria de testes do módulo principal (`mvn install -DskipTests`) transforma alguns minutos em poucos
> segundos, enquanto dá os primeiros passos.

---

## 2. Analisar algo

Uma única classe, sem configuração:

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

`parse(String)` executa a cadeia **completa**: sintaxe, validação de conteúdo e interpretação.
É a predefinição certa — restrinja-a mais tarde se as suas medições lhe derem um motivo para isso.

---

## 3. Ler o resultado

`ParseResult.getAiObject()` contém os AI resolvidos. Chegue a um em concreto pelo seu código, e não
pela posição:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

Cada elemento transporta uma lista de **interpretações**: o significado descodificado por detrás dos dígitos em bruto,
produzido pela fase de interpretação:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` é localizado e destina-se à apresentação. Para *ler* um valor em código, procure-o antes
pela sua chave de tipo, que é estável:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

AI diferentes produzem chaves diferentes: um GTIN dá o seu prefixo de empresa, o tipo de GTIN e o dígito
de controlo; um preço dá a moeda e o montante decimal. A lista completa está no
[anexo B](GaiaParser-Portuguese.md#anexo-b--constantes-das-chaves-de-interpretação), e as constantes residem
em `GS1Constants_Enricher`. Nem todos os AI têm interpretações: um lote em texto livre não tem
nada de que as derivar, pelo que a sua lista fica vazia.

---

## 4. Tratar uma análise falhada

Um payload inválido é um desfecho normal, não uma exceção — `parse` nunca lança exceções por dados
GS1 incorretos:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**Ramifique por `getId()`, nunca pela mensagem.** As mensagens são localizadas e a sua redação
não é um contrato — e transportam atualmente um defeito conhecido de aspas (o `''` duplicado acima),
assinalado na [referência de erros](GaiaParser-Portuguese.md#referência-de-erros).

Duas perguntas diferentes, dois métodos diferentes:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

Uma análise deixa de descer assim que uma fase falha, pelo que um dígito de controlo errado lhe dá
erros de validação mas nenhuma interpretação.

### Os avisos não tornam um resultado inválido

Algumas verificações são meramente indicativas. Um prefixo de empresa GS1 não reconhecido é assinalado, mas o payload
continua estruturalmente correto:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

Use `getIssues()` quando quiser ambos. Se o seu fluxo tiver de rejeitar prefixos desconhecidos, consulte
explicitamente `getWarnings()` — `isValid()` não o fará por si.

---

## 5. Duas coisas que o vão apanhar

### O separador GS, e porque omiti-lo é pior do que um erro

Um AI de comprimento variável estende-se até um **carácter GS** (ASCII `0x1D`, chamado FNC1 nas
simbologias de códigos de barras) ou até ao fim da cadeia. Quando lhe segue outro AI, esse separador é
obrigatório:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

Omita-o e **não** obtém um erro: obtém uma resposta errada dada com toda a confiança:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

O AI `10` é `X..20`, pelo que engole legitimamente `LOT-ABC21SN-98765`, e o analisador não tem
forma de saber que não era isso que se pretendia. Nada a jusante consegue recuperar isto, por isso acerte no
separador logo na origem: leia os bytes do leitor como **ISO-8859-1** para que `0x1D` sobreviva, e escreva
`""` nos literais de cadeia de Java. Os AI de comprimento fixo (`01`, `17`, `3103`) dispensam separador —
o analisador conhece o seu comprimento.

### A maioria dos AI não pode aparecer isolada

Lote, número de série, prazo de validade e afins são *atributos*: as GS1 General Specifications
exigem que viajem com uma chave de identificação, e o Gaia fá-lo cumprir.

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

Acrescente o GTIN e passa. Se realmente precisar de analisar um fragmento — um teste unitário, uma
leitura parcial —, desligue a verificação:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. Os prefixos dos leitores e os Digital Link funcionam de imediato

Não tem de dizer ao Gaia qual é a forma da entrada — ele deteta as quatro. Dê-lhe
aquilo que o leitor lhe entregou, tal como está.

**Um prefixo de identificador de simbologia AIM** determina a simbologia e é removido automaticamente:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**Um URI GS1 Digital Link** passa pela mesma validação e pelo mesmo enriquecimento:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

Como ambas as formas desembocam no mesmo `GS1AIObject`, o código que consome uma leitura não precisa de
se preocupar com qual delas chegou — e `toElementString()` / `getCanonicalDigitalLink()`
convertem entre uma e outra.

Um **prefixo de correlação de 8 dígitos** (`12345678~…`) é igualmente removido e conservado em
`getCorrelationInfo()`, caso a sua cadeia de processamento use um.

---

## 7. Fazer menos trabalho: os modos de análise

A predefinição faz tudo. Peça menos quando só precisar de parte da resposta:

| Modo | Responde à pergunta | Custo |
|---|---|---|
| `DATA_CARRIER` | De que simbologia se trata? | O mais baixo — nenhuma análise de AI, `getAiObject()` é `null` |
| `SYNTAX` | Os códigos AI e os comprimentos estão bem formados? | Sem dígitos de controlo nem interpretações |
| `CONTENT` | São dados GS1 válidos? | Validação completa, sem interpretações |
| `INTERPRETATION` | O que significam? | **Predefinido** — tudo |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

Recorra a `CONTENT` quando validar em volume e nunca mostrar a decomposição, e a
`DATA_CARRIER` quando apenas precisar de encaminhar uma leitura para o tratador certo.

---

## 8. Mudar o idioma e o formato da data

As mensagens de erro, as etiquetas de interpretação e as descrições dos AI estão traduzidas em **35
idiomas**; as datas apresentam-se como preferir. Tudo isso cabe numa única `ParseConfig` imutável:

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

Os valores nunca são localizados — apenas as etiquetas, as descrições e as mensagens —, pelo que `"2026-12-31"` e
`"09506000134352"` significam o mesmo em qualquer idioma. Construa a configuração uma vez no arranque
e partilhe-a; é imutável.

---

## 9. Limpar entradas desarrumadas

Se a sua fonte emitir parênteses de HRI impressos ou espaços soltos, o módulo principal inclui dois
**modificadores de entrada** que reparam o payload antes da análise:

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

Nada está ativo por predefinição, e ambos têm reservas: o espaço e os parênteses são carateres
de dados GS1 lícitos, pelo que só os deve aplicar a uma fonte que conheça. Consulte
[Modificadores integrados](GaiaParser-Portuguese.md#modificadores-integrados), que explica também porque é que a remoção dos
parênteses tem de repor o separador que estes implicavam.

---

## 10. Para onde ir a seguir

- **[Guia do programador do GaiaParser](GaiaParser-Portuguese.md)** — a cadeia de processamento em detalhe, o modelo
  de resultado completo, todos os códigos de erro e os anexos dos AI e das chaves de interpretação.
- **[Guia do programador do GaiaBuilder](GaiaBuilder-Portuguese.md)** — construir cadeias de elementos e URI Digital
  Link a partir de pares AI/valor.
- **[Referência HTTP da API Gaia](../../gaia-api-reference.md)** — o mesmo motor sobre HTTP, se preferir
  não incorporar a biblioteca.
- **[ai-codes.txt](../../ai-codes.txt)** — uma listagem simples `(AI) TÍTULO` para consulta rápida.

### A versão em cinco linhas

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
