# GAIA (GS1 Application Identifiers Analyser) — Guia do programador

## Índice

1. [Visão geral](#visão-geral)
2. [Acerca da GS1 e das General Specifications](#acerca-da-gs1-e-das-general-specifications)
3. [Identificadores de aplicação GS1](#identificadores-de-aplicação-gs1)
4. [Início rápido](#início-rápido)
5. [Cadeia de processamento da análise](#cadeia-de-processamento-da-análise)
   - [Fase preliminar — modificadores de entrada](#fase-preliminar--modificadores-de-entrada)
   - [Fase 0 — identificador de correlação](#fase-0--identificador-de-correlação)
   - [Fase 1 — encaminhamento da entrada](#fase-1--encaminhamento-da-entrada)
   - [Fase 2 — sintaxe](#fase-2--sintaxe)
   - [Fase 3 — conteúdo](#fase-3--conteúdo)
   - [Fase 4 — interpretação](#fase-4--interpretação)
6. [Configuração da análise (`ParseConfig`)](#configuração-da-análise-parseconfig)
   - [Opções](#opções)
   - [Mensagens e etiquetas localizadas](#mensagens-e-etiquetas-localizadas)
   - [Formatação de datas](#formatação-de-datas)
7. [Modificadores de entrada](#modificadores-de-entrada)
   - [Modificadores integrados](#modificadores-integrados)
   - [Escrever um modificador](#escrever-um-modificador)
   - [Declarar modificadores](#declarar-modificadores)
   - [Examinar o que um modificador fez](#examinar-o-que-um-modificador-fez)
   - [Tratamento de falhas de um modificador](#tratamento-de-falhas-de-um-modificador)
8. [Modos de análise](#modos-de-análise)
   - [Modo DATA_CARRIER](#modo-data_carrier)
   - [Modo SYNTAX](#modo-syntax)
   - [Modo CONTENT](#modo-content)
   - [Modo INTERPRETATION (predefinido)](#modo-interpretation-predefinido)
9. [Identificador de correlação](#identificador-de-correlação)
10. [GS1 Digital Link](#gs1-digital-link)
11. [Trabalhar com os resultados](#trabalhar-com-os-resultados)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry e DataCarrierType](#datacarrierentry-e-datacarriertype)
12. [Referência de erros](#referência-de-erros)
13. [Segurança perante threads](#segurança-perante-threads)
14. [Anexo A — constantes de cadeia dos AI](#anexo-a--constantes-de-cadeia-dos-ai)
    - [Identificação e serialização](#identificação-e-serialização)
    - [Datas e horas](#datas-e-horas)
    - [Quantidade e medida — medida variável (métrico)](#quantidade-e-medida--medida-variável-métrico)
    - [Quantidade e medida — medida variável (imperial / EUA)](#quantidade-e-medida--medida-variável-imperial--eua)
    - [Preços e montantes monetários](#preços-e-montantes-monetários)
    - [Localização e expedição](#localização-e-expedição)
    - [Atributos de produto e rastreabilidade](#atributos-de-produto-e-rastreabilidade)
    - [Números nacionais de comparticipação na saúde (NHRN)](#números-nacionais-de-comparticipação-na-saúde-nhrn)
    - [Saúde, GMN, HIDRI, CPID, dados pessoais](#saúde-gmn-hidri-cpid-dados-pessoais)
    - [Uso interno / da empresa](#uso-interno--da-empresa)
15. [Anexo B — constantes das chaves de interpretação](#anexo-b--constantes-das-chaves-de-interpretação)
    - [Data e hora](#data-e-hora)
    - [Data de colheita](#data-de-colheita)
    - [Prefixo de empresa GS1](#prefixo-de-empresa-gs1)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [País (ISO 3166)](#país-iso-3166)
    - [Moeda (ISO 4217)](#moeda-iso-4217)
    - [Temperatura](#temperatura)
    - [Sexo (ISO 5218)](#sexo-iso-5218)
    - [Espécies aquáticas (FAO)](#espécies-aquáticas-fao)
    - [Número de catálogo NATO (NSN)](#número-de-catálogo-nato-nsn)
    - [Produtos em rolo](#produtos-em-rolo)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [Identificadores de SIM (EID / ICCID)](#identificadores-de-sim-eid--iccid)
    - [Referência de certificação](#referência-de-certificação)
    - [GS1 UIC](#gs1-uic)
    - [Ordem de nascimento do recém-nascido](#ordem-de-nascimento-do-recém-nascido)
    - [Número global de modelo (GMN)](#número-global-de-modelo-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [Valores decimais e de medida](#valores-decimais-e-de-medida)
    - [Coordenadas geográficas](#coordenadas-geográficas)
    - [Método de produção](#método-de-produção)
    - [Tipo de suporte AIDC](#tipo-de-suporte-aidc)
    - [Peça do total](#peça-do-total)
    - [Divisões em componentes](#divisões-em-componentes)
    - [Diversos](#diversos)

---

## Visão geral

`GaiaParser` é o ponto de entrada para a análise de cadeias de elementos com identificadores de aplicação (AI) GS1. Aceita a saída em bruto de um leitor em qualquer uma das formas seguintes e devolve um `ParseResult` estruturado que contém todos os AI resolvidos, os erros de validação e, opcionalmente, as interpretações legíveis por pessoas:

- Cadeia de elementos AI simples: `0109506000134352`
- Cadeia de elementos precedida do identificador de simbologia AIM: `]C10109506000134352`
- URI GS1 Digital Link: `https://example.com/01/09506000134352`
- Qualquer uma das formas anteriores, opcionalmente precedida de um identificador de correlação de 8 dígitos: `12345678~0109506000134352`

**Classe de entrada:** `tools.pantheum.gaia.GaiaParser`

> **É a sua primeira vez com o Gaia?** Comece pelo **[guia de início rápido do GaiaParser](GaiaParser-QuickStart-Portuguese.md)** — a dependência, uma primeira análise e as poucas armadilhas habituais, em cerca de dez minutos. Este guia é a referência completa.

> Para a operação inversa — a *construção* de cadeias de elementos e URI Digital Link bem formados a partir de pares AI/valor — consulte o **[guia do programador do GaiaBuilder](GaiaBuilder-Portuguese.md)**.

---

## Acerca da GS1 e das General Specifications

A **GS1** é uma organização mundial sem fins lucrativos que desenvolve e mantém normas abertas para a identificação e o intercâmbio de dados nas cadeias de abastecimento. As suas normas são utilizadas na distribuição, na saúde, na logística, na restauração e em muitos outros setores, abrangendo desde os códigos de barras dos produtos de consumo até ao rastreio serializado de doses farmacêuticas.

A referência com autoridade para tudo o que este analisador implementa são as **GS1 General Specifications** — um único documento que define:

- Todos os códigos de identificador de aplicação (AI), os seus títulos de dados, formatos e regras de validação
- As regras de sintaxe para compor e codificar cadeias de elementos AI
- Os requisitos de simbologia dos códigos de barras e a atribuição dos identificadores de simbologia AIM
- Os algoritmos do dígito de controlo e do carácter de controlo
- A resolução de anos com dois dígitos (a regra da janela deslizante)
- As especificações de Data Matrix, QR Code, GS1-128, GS1 DataBar e demais suportes de dados

As GS1 General Specifications são atualizadas anualmente. A edição em vigor e os recursos associados estão disponíveis em:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

O GAIA implementa a **versão 26.0 (ratificada em janeiro de 2026)** das GS1 General Specifications.

Os URI GS1 Digital Link regem-se por uma norma complementar, **GS1 Digital Link: URI Syntax**, que define as chaves de identificação primárias, a ordem dos qualificadores de chave e a codificação dos atributos de dados que o analisador aplica às entradas do tipo Digital Link:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

O GAIA implementa a **versão 1.7.0 (ratificada em agosto de 2026)** da norma GS1 Digital Link: URI Syntax.

Ao longo deste documento, as referências a secções remetem para as GS1 General Specifications (por exemplo, «Table 7-5», «section 7.12»), com exceção dos números de secção do Digital Link (por exemplo, «§4.9», «§4.12»), que remetem para a norma GS1 Digital Link: URI Syntax.

---

## Identificadores de aplicação GS1

Um **identificador de aplicação (AI) GS1** é um prefixo numérico curto — de dois a quatro dígitos — que determina o significado e o formato dos dados que se lhe seguem imediatamente. Os AI estão definidos nas GS1 General Specifications e abrangem um vasto conjunto de dados da cadeia de abastecimento: identificadores de produto, datas, quantidades, números de lote, números de série, medidas, URL e muito mais.

### Estrutura de um elemento AI

Cada elemento AI é composto por duas partes:

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

O código AI é sempre numérico. O valor dos dados segue-se imediatamente, sem qualquer delimitador entre o código e o valor.

### AI de comprimento fixo e de comprimento variável

Os AI dividem-se em duas categorias:

| Tipo | Comportamento | Exemplo |
|---|---|---|
| **Comprimento fixo** | Número exato de carateres, sempre consumido na íntegra | AI `01` (GTIN) — sempre 14 dígitos |
| **Comprimento variável** | De 1 carácter até um máximo; termina com um separador GS ou com o fim da entrada | AI `10` (lote) — de 1 a 20 carateres alfanuméricos |

Que um AI seja de comprimento fixo ou variável decorre unicamente da sua definição na especificação GS1 — o analisador nunca adivinha.

### Cadeias de elementos com vários AI

É possível concatenar vários AI numa única cadeia de elementos. Os AI de comprimento fixo podem ser concatenados diretamente, porque o analisador sabe sempre exatamente quantos carateres consumir. Os AI de comprimento variável têm de ser terminados pelo **carácter GS** (ASCII `0x1D`, também designado FNC1 nas simbologias de códigos de barras) sempre que outro AI se lhes siga, para que o analisador saiba onde termina um valor e onde começa o código AI seguinte.

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

Nos literais de cadeia de Java, escreva o carácter GS com a sequência de escape Unicode `""`.

### AI de uso corrente

| AI | Título de dados | Formato | Valor de exemplo |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (AAMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (AAMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1,500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912,34, área monetária única) |
| `710` | NHRN PZN | X..20 | `12345678` |

> O **quarto dígito** de um AI de medida ou de preço com 4 dígitos codifica o número de casas decimais implícitas: `3103` é o peso líquido em kg com 3 casas decimais (`001500` = 1,500 kg), ao passo que `3102` leria os mesmos dígitos como 15,00 kg. A coluna `Formato` acima mostra o formato dos *dados*; o `getFormatString()` completo de cada AI inclui o próprio AI (por exemplo, `N4+N6` para `3103`).

### Interpretação legível por pessoas (HRI)

A forma legível convencional coloca cada código AI entre parênteses, imediatamente antes do seu valor, com um espaço entre elementos:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

O separador GS não aparece na HRI. `GS1AIObject.toHriString()` produz este formato.

### Códigos AI de quatro dígitos

Alguns AI utilizam quatro dígitos em vez de dois. Os dois primeiros identificam a família do AI; o terceiro e/ou o quarto transportam semântica adicional (como a posição da vírgula decimal implícita nos AI de medida). O analisador resolve automaticamente o código AI completo a partir da cadeia de elementos — quem o invoca trabalha sempre com o código completo (por exemplo, `"3102"`, e não apenas `"31"`).

---

## Início rápido

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

> **Separador GS:** dentro de uma cadeia com vários AI, os AI de comprimento variável têm de ser delimitados pelo carácter GS (ASCII `0x1D`). Nos literais de cadeia de Java, utilize `""`.

---

## Cadeia de processamento da análise

### Fase preliminar — modificadores de entrada

Se a `ParseConfig` incluir **modificadores de entrada**, estes são executados antes de tudo o resto: antes da remoção do identificador de correlação, antes da deteção do suporte de dados e antes de se entrar na cadeia GS1. Cada modificador reescreve a entrada em bruto para o seguinte, e todas as fases descritas adiante operam sobre a saída da cadeia.

Por predefinição não está configurado qualquer modificador, pelo que esta fase preliminar nada faz enquanto não a ativar explicitamente. Consulte [Modificadores de entrada](#modificadores-de-entrada).

---

### Fase 0 — identificador de correlação

Antes de qualquer processamento GS1, o `GaiaParser` verifica se a entrada começa por um **prefixo de identificador de correlação** opcional: exatamente 8 dígitos decimais ASCII seguidos de um til (`~`), por exemplo `12345678~`.

Se o prefixo estiver presente, é removido e guardado como `CorrelationInfo` no `ParseResult` devolvido. Todas as fases seguintes operam sobre o payload assim depurado. Na ausência do prefixo, a entrada passa inalterada.

Para os detalhes, consulte [Identificador de correlação](#identificador-de-correlação).

---

### Fase 1 — encaminhamento da entrada

Depois da remoção da correlação, o `GaiaParser` verifica se a entrada (depurada) começa por um **identificador de simbologia AIM**: um prefixo de três carateres na forma `]` + letra ASCII + dígito ASCII (por exemplo, `]C1` para GS1-128, `]d2` para GS1 DataMatrix, `]e0` para GS1 DataBar / GS1 Composite).

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

Se o suporte não for compatível com os AI GS1 (por exemplo, um código de barras postal), a análise para de imediato com um erro `GE-D002`.

---

### Fase 2 — sintaxe

É executada sempre. Compõe-se de duas subfases:

**2a. Divisão em tokens (`AISyntaxParser`)**
- Lê o comprimento do código AI a partir dos dois primeiros carateres, recorrendo à tabela de prefixos GS1 (GS1 General Specifications, tabela 7-5).
- Os AI de comprimento fixo consomem da entrada um número exato de bytes.
- Os AI de comprimento variável são lidos até um carácter GS ou até ao fim da entrada.
- Nos AI com vários componentes, o bloco de valor é dividido em segmentos, um por componente.

**2b. Validação estrutural (`SyntaxValidator`)**
- Deteta AI duplicados (`GE-S004`).
- Verifica as dependências obrigatórias entre AI, por exemplo o AI `02`, que exige o AI `37` (`GE-S005`).
- Verifica os emparelhamentos de AI excluídos (`GE-S006`).

Os erros desta fase têm nível `SYNTAX_ERROR` (divisão em tokens) ou `INTEGRITY_ERROR` (estrutura). Se existir **um único** erro que seja — de divisão ou de estrutura —, a cadeia para e as fases de conteúdo e de interpretação são omitidas.

---

### Fase 3 — conteúdo

Só é executada se a fase 2 não tiver produzido erros (nem de divisão nem de estrutura). Cadeia aplicada a cada elemento (cada passo só é executado se o anterior não tiver produzido erros):

| Passo | Validador | Códigos de erro |
|---|---|---|
| Verificação por expressão regular | `RegexValidator` | `GE-C001` |
| Conjunto de carateres e formato dos componentes | `ComponentValidator` | `GE-C005` + códigos de formato por condição (`GE-C054`–`GE-C115`) |
| Dígito de controlo / carácter de controlo | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| Validação semântica personalizada | `ContentValidatorRegistry` | códigos de conteúdo por condição (`GE-C116`–`GE-C170`) |

Os erros desta fase têm nível `FORMAT_ERROR` ou `DATA_ERROR`, com uma exceção: as
verificações do prefixo de empresa GS1 nos AI que transportam uma chave GS1 são meramente indicativas e têm nível `WARNING` (consulte a
[Referência de erros](#referência-de-erros)), pelo que um prefixo de empresa não reconhecido não torna,
por si só, o resultado inválido.

---

### Fase 4 — interpretação

Só é executada no modo `INTERPRETATION` e apenas quando nenhum elemento transporta um erro de uma fase anterior. O `InterpretationEngine` enriquece cada elemento com metadados etiquetados:

- Datas reformatadas como `dd/mm/aaaa`
- Decomposição do dígito de controlo do GTIN e consulta do prefixo de empresa GS1
- Nomes de país ISO 3166
- Nomes e símbolos de moeda ISO 4217
- Montantes decimais descodificados
- Fragmentos de HRI (interpretação legível por pessoas)

Os resultados são anexados como entradas `GS1AIInterpretation` a cada `GS1AIObjectElement`.

---

## Configuração da análise (`ParseConfig`)

O `GaiaParser` expõe exatamente dois pontos de entrada:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` é executado com a **configuração predefinida**: modo `INTERPRETATION`, datas por ordem crescente (`dd/mm/aaaa`) com separador `/` e ano com quatro dígitos, e mensagens de erro em **inglês**. Para alterar qualquer um destes aspetos — incluindo o modo de análise —, construa uma `ParseConfig` com o seu construtor fluente e utilize a sobrecarga de dois argumentos.

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

Todas as enumerações de opções residem em `GaiaConstants`.

### Opções

| Método do construtor | Enumeração (`GaiaConstants`) | Predefinição | Efeito |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | Profundidade da cadeia — consulte [Modos de análise](#modos-de-análise). |
| `language(...)`      | `Language`      | `ENGLISH`        | Idioma das mensagens de erro, das etiquetas de interpretação **e** das descrições dos AI. |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | Ordem dos componentes da data: `LITTLE` (`dd/mm/aaaa`), `MIDDLE` (`mm/dd/aaaa`), `BIG` (`aaaa/mm/dd`). |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | Carácter entre os componentes da data: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`). |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) ou `THREE_LETTER` (`DEC`). |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) ou `TWO_DIGIT` (`26`). |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | Omite a verificação estrutural «exige» (`GE-S005`). |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | Omite a verificação estrutural «exclui» (`GE-S006`). |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / nome de classe | nenhum | Código que reescreve a entrada em bruto antes da análise — dois [modificadores integrados](#modificadores-integrados) mais tudo o que escrever. Consulte [Modificadores de entrada](#modificadores-de-entrada). |

As quatro opções de data afetam apenas as cadeias de data formatadas produzidas pelos enriquecedores de interpretação (no modo `INTERPRETATION`); não alteram a validação. Os valores do construtor podem ser omitidos — qualquer opção não definida (ou à qual se passe `null`) mantém a sua predefinição.

### Mensagens e etiquetas localizadas

`language(...)` seleciona o idioma de **três** categorias de texto legível por pessoas: as mensagens de erro, as etiquetas de interpretação (o `getLabel()` de cada `GS1AIInterpretation`) e as descrições dos AI (o `getDescription()` de cada `GS1AIObjectElement`).

`GaiaConstants.Language` define **35 idiomas**, que abrangem as línguas mais faladas do mundo: inglês, francês, espanhol, alemão, italiano, português, neerlandês, polaco, russo, ucraniano, checo, sueco, chinês, japonês, coreano, árabe, indonésio, hindi, turco, bengali, urdu, vietnamita, pidgin nigeriano, árabe egípcio, marati, telugu, tâmil, cantonês, wu, tagalo, persa, hauçá, panjabi, javanês e suaíli.

Estado das traduções (tal como distribuídas):
- **Etiquetas de interpretação** — traduzidas para todos os idiomas.
- **Mensagens de erro** — traduzidas para todos os idiomas.
- **Descrições dos AI** — traduzidas para todos os idiomas exceto o inglês. O inglês não constitui um catálogo próprio: é lido diretamente do campo `description` da entrada do AI em `gs1-application-identifiers.jsonld`, para o qual qualquer descrição de AI acaba por recuar.

O pidgin nigeriano (`NIGERIAN_PIDGIN`), um crioulo de base inglesa, reutiliza deliberadamente o texto inglês nas etiquetas de interpretação e nas mensagens de erro. As descrições dos AI são a exceção a essa exceção: estão traduzidas em pidgin autêntico em vez de reaproveitarem o inglês, porque os catálogos de descrições de AI foram produzidos de forma independente dos catálogos de etiquetas e de mensagens. As traduções automáticas devem ser revistas por falantes nativos antes de se confiar nelas em produção.

Qualquer mensagem, etiqueta ou descrição em falta no catálogo de um idioma recua para o inglês. Os idiomas escritos da direita para a esquerda (árabe, urdu, árabe egípcio, persa) são armazenados corretamente como cadeias; a sua apresentação da direita para a esquerda cabe à camada de visualização.

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

As etiquetas de interpretação localizam-se do mesmo modo (os valores mantêm-se inalterados — só as etiquetas mudam):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

As descrições dos AI localizam-se do mesmo modo (só `getTitle()`, por exemplo `"GTIN"`, não é localizado):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### Formatação de datas

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

Um **modificador de entrada** é código que reescreve a cadeia de entrada em bruto antes de o Gaia a analisar. Os modificadores existem para as entradas que chegam já deformadas: um leitor que substitui o separador GS por um marcador imprimível, um middleware que envolve o payload num prefixo proprietário, um sistema anfitrião que converte tudo em maiúsculas. Em vez de pré-processar cada cadeia em cada ponto de chamada (e de o fazer subtilmente mal num deles), declare a normalização uma só vez na `ParseConfig` e deixe que seja o analisador a aplicá-la.

Os modificadores são executados logo no início de `GaiaParser.parse(...)`: antes da remoção do identificador de correlação, antes da deteção do identificador de simbologia AIM e antes da cadeia GS1. Tudo o que vem a jusante vê apenas a cadeia reescrita. **Por predefinição não está configurado nada**, nem sequer os dois [modificadores integrados](#modificadores-integrados) — é você que os ativa explicitamente em cada `ParseConfig`.

**Interface:** `tools.pantheum.gaia.modifier.ModifierInterface`

### Modificadores integrados

O jar principal inclui dois modificadores, em `tools.pantheum.gaia.modifier.custom`. Cobrem as duas formas mais frequentes de um payload GS1 chegar deformado — parênteses de HRI impressos e tratados como dados, e espaços espúrios —, pelo que os casos correntes dispensam qualquer classe própria:

| Classe | `getName()` | O que faz |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | Retira os parênteses de HRI em torno de cada AI (`(01)…(10)…`) e repõe o separador FNC1 que estes implicavam. |
| `ModifierRemoveSpaces` | `Remove Space Characters` | Remove todos os espaços (`0x20`) da cadeia de elementos AI. |

São implementações correntes de `ModifierInterface`, sem qualquer estatuto especial: são declaradas, ordenadas, reportadas e falham exatamente como as suas:

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

Ambos são desprovidos de estado e seguros perante threads, pelo que uma única instância pode ser partilhada, e ambos são endereçáveis pelo nome de classe completo em configurações externalizadas (consulte [Declarar modificadores](#declarar-modificadores)).

#### `ModifierRemoveAIBrackets`

A interpretação legível por pessoas da GS1 imprime cada AI entre parênteses — `(01)09521234543213(10)ABC123` — por pura convenção tipográfica. Um leitor ou um middleware configurado para emitir a HRI transmite esses parênteses como dados, e o divisor em tokens não sabe de todo o que fazer com eles.

Retirar os parênteses é apenas metade do trabalho. Na HRI, é o parêntese de abertura do AI *seguinte* que marca o fim do valor anterior, pelo que, na forma com parênteses, um AI de comprimento variável não precisa de qualquer FNC1. Retire os parênteses de forma ingénua e essa fronteira desaparece:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

Por isso o modificador **reinsere um FNC1 em cada fronteira cujo AI precedente seja de comprimento variável**, repondo exatamente aquilo que os parênteses codificavam:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

O comprimento é consultado no próprio `AiDefinitionRegistry` do analisador, pelo que são tratados todos os AI de comprimento variável, em vez de uma lista escrita à mão. Três casos ficam deliberadamente intactos: um valor que já termina em FNC1 (uma fonte que emita ambas as convenções não recebe um segundo separador), um código entre parênteses que não seja um AI conhecido (um AI desconhecido nada diz sobre o seu próprio comprimento) e o último AI da cadeia.

A reescrita é **idempotente** — voltar a aplicá-la ao seu próprio resultado nada altera —, sendo por isso segura num fluxo misto em que apenas algumas entradas trazem parênteses.

> **Limitação.** `(` e `)` são, eles próprios, carateres de dados GS1 válidos, e o padrão resume-se a `\((\d{2,4})\)`. Um valor que contenha por acaso um número de dois a quatro dígitos entre parênteses também ficaria sem eles. Aplique isto apenas a uma fonte que use a convenção dos parênteses da HRI, e não a valores com parênteses genuínos.

#### `ModifierRemoveSpaces`

Alguns leitores, middlewares e cadeias de impressão de etiquetas introduzem espaços espúrios numa cadeia de elementos de resto bem formada: para preencher um campo de largura fixa, para separar grupos legíveis ou para quebrar um valor longo. O divisor em tokens trata cada um deles como dados, corrompendo o valor em que se encontra e, num AI de comprimento variável, deslocando tudo o que se lhe segue.

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

Só é removido o ASCII `0x20`. Os restantes carateres de espaço mantêm-se: uma tabulação, por exemplo, fica fora do conjunto de carateres codificável da GS1, pelo que o analisador a assinala como `GE-S008` em vez de a varrer em silêncio.

> **Limitação.** O espaço (`0x20`) faz parte do conjunto de carateres invariante da GS1, pelo que um número de lote ou uma referência de artigo de cliente pode legitimamente conter um. O modificador não sabe distinguir um espaço espúrio de um autêntico; aplique-o apenas a uma fonte que saiba não usar espaços dentro dos seus valores de AI.

#### Os prefixos são ignorados, não reescritos

Os modificadores são executados antes de o analisador ter removido o que quer que seja, pelo que a entrada em bruto pode ainda trazer um identificador de correlação, um identificador de simbologia AIM e um indicador ECI. Ambos os modificadores integrados localizam o início da cadeia de elementos AI através da lógica de `CorrelationIdParser` e de `DataCarrierParser` do próprio analisador, reescrevem apenas a partir desse ponto e voltam a ligar o resultado ao prefixo, que fica **intacto**:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

Os suportes EAN/UPC cujo valor é preenchido até GTIN-14 (`isRequiresGtinPadding()`) são ignorados por completo: o seu payload é um valor de código de barras puramente numérico, sem estrutura de AI, onde nem parênteses nem espaços podem ter significado.

#### Ordem: primeiro os espaços, depois os parênteses

Quando ambos são usados, **declare `ModifierRemoveSpaces` em primeiro lugar**. O reconhecimento dos parênteses depende da posição: um `( 01 )` com espaços não corresponde a `\((\d{2,4})\)`, pelo que os parênteses sobrevivem e o separador que implicavam nunca é reposto.

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### Escrever um modificador

Escreva o seu quando nenhum dos integrados servir — a interface resume-se a um método.

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

Sobreponha antes a sobrecarga de dois argumentos quando a reescrita depender da configuração da análise:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

Contrato:

| Regra | Detalhe |
|---|---|
| Desprovido de estado e seguro perante threads | De cada classe é guardada em cache uma única instância, partilhada por todas as análises. |
| Construtor público sem argumentos | Necessário apenas quando o modificador é indicado pelo nome de classe. |
| Tratar entradas `null` e vazias | O analisador não as filtra antes de executar a cadeia. |
| Devolver `null` significa «sem alterações» | O valor anterior é mantido. Devolva `input` inalterado quando o modificador não se aplicar. |
| Preferir devolver a entrada inalterada a lançar uma exceção | Um modificador que lance uma exceção interrompe a análise — consulte [Tratamento de falhas](#tratamento-de-falhas-de-um-modificador). |
| `getName()` | Sobreponha-o para controlar o nome reportado em `ModifierInfo`; por predefinição é o nome simples da classe. |

### Declarar modificadores

Os modificadores são executados pela ordem em que são adicionados, recebendo cada um a saída do anterior. Declare-os por instância, pelo nome de classe completo ou como uma lista de qualquer um dos dois:

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

Os [modificadores integrados](#modificadores-integrados) indicam-se exatamente como os seus — **sempre com o nome completo**. Não existe para eles qualquer resolução por nome curto ou por alias; o `ModifierRegistry` resolve todos os modificadores, distribuídos ou não, pelo nome de classe completo.

Os nomes são resolvidos pelo `ModifierRegistry`, que instancia cada classe uma só vez através do seu construtor sem argumentos e guarda a instância em cache para qualquer configuração posterior que indique a mesma classe. A resolução ocorre **ao construir a configuração**, pelo que um nome que não seja encontrado, que não implemente `ModifierInterface` ou que não possa ser instanciado lança aí uma `IllegalArgumentException` — e não em silêncio no momento da análise. Um modificador que não possa ser construído por reflexão (por exemplo, um que contenha uma dependência injetada) pode ser registado previamente, para continuar endereçável pelo seu nome:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### Examinar o que um modificador fez

Quando há modificadores configurados, `ParseResult.getPayload()` reflete a entrada **modificada**. O original é conservado em `ModifierInfo`:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` reporta o `getName()` de cada modificador, que por predefinição é o nome simples da classe mas que ambos os modificadores integrados sobrepõem — uma cadeia composta pelos dois reporta, portanto, os nomes de apresentação, e não os nomes de classe:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

`getModifierInfo()` devolve `null` quando não foi configurado qualquer modificador. Quando foram executados modificadores mas todos devolveram a entrada inalterada, a informação está presente e `isModified()` vale `false` — em `getAppliedModifiers()` figuram apenas os modificadores que efetivamente alteraram a entrada.

### Tratamento de falhas de um modificador

Um modificador que lance uma exceção interrompe a análise. A exceção é encapsulada numa `GaiaModifierException` que nomeia o modificador responsável, e o resultado transporta um erro interno `GE-I001` cuja mensagem retoma esse nome; `getPayload()` reporta a entrada não modificada. A análise deliberadamente **não** prossegue com uma cadeia reescrita a meio: um passo de normalização que falhasse em silêncio produziria resultados de aparência válida mas obtidos a partir da entrada errada.

---

## Modos de análise

Cada modo designa a [fase da cadeia](#cadeia-de-processamento-da-análise) mais profunda que executa; todas as fases anteriores são igualmente executadas.

| Modo | Vai até | Responde à pergunta |
|---|---|---|
| `DATA_CARRIER` | Fase 1 (encaminhamento da entrada) | Que simbologia transportou isto? |
| `SYNTAX` | Fase 2 (sintaxe) | Os códigos AI e os comprimentos estão bem formados? |
| `CONTENT` | Fase 3 (conteúdo) | Os valores são dados GS1 válidos? |
| `INTERPRETATION` | Fase 4 (interpretação) | O que significam os valores? |

### Modo DATA_CARRIER

Para depois da fase 1: valida o identificador de simbologia AIM e determina a simbologia, mas não entra na cadeia de análise dos AI. Útil para identificar a simbologia e encaminhar o tratamento sem suportar o custo de uma validação completa.

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

**Use quando:** a sua aplicação precisar de identificar o tipo de código de barras antes de decidir como tratar o payload — por exemplo, para encaminhar para tratadores diferentes consoante se trate de simbologias 1D ou 2D. Para esse encaminhamento, prefira o tipo [`DataCarrierType`](#datacarrierentry-e-datacarriertype) (`getDataCarrier().getDataCarrierType()`) a uma comparação de cadeias sobre `getName()`.

---

### Modo SYNTAX

Para depois da fase 2. Útil para uma triagem estrutural prévia sem o custo da validação do conteúdo.

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

**Use quando:** quiser verificar se os códigos AI e os comprimentos dos dados estão bem formados antes de se comprometer com uma validação completa, ou quando processar grandes volumes em que os erros de conteúdo são raros.

---

### Modo CONTENT

Para depois da fase 3.

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

> A maioria dos AI não pode aparecer isolada: os AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) e `21`
> (SERIAL) *exigem* cada um uma chave de identificação, como o AI `01`, na mesma cadeia de
> elementos; omitir o GTIN acima falharia logo na fase 2 com `GE-S005`, sem sequer
> chegar à validação do conteúdo. Defina `skipRequiresCheck(true)` na
> `ParseConfig` para analisar fragmentos que omitam deliberadamente os seus AI acompanhantes.

**Use quando:** precisar de saber se um valor lido está plenamente conforme com a GS1 antes de o utilizar num processo de negócio, sem o custo adicional do enriquecimento por interpretação.

---

### Modo INTERPRETATION (predefinido)

Executa a cadeia completa até à fase 4. É o modo predefinido ao chamar `parse(String)` sem argumento de modo. Só são enriquecidos os elementos que passaram a validação de conteúdo sem erros.

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

**Exemplo de saída:**
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

**Exemplo de montante monetário (AI 3932 — preço com código de moeda ISO):**
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

**Use quando:** estiver a construir camadas de apresentação, ferramentas de verificação de etiquetas ou qualquer interface que precise de uma decomposição legível dos valores dos AI.

---

## Identificador de correlação

Alguns fluxos de trabalho antepõem à entrada GS1 em bruto um identificador de correlação proprietário de 8 dígitos, de modo a poder ligar os eventos de leitura a uma sessão ou a uma transação. O formato é o seguinte:

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

O til (`~`) é o separador. **Não** faz parte do conteúdo GS1 — é removido antes de começar qualquer análise GS1.

### Regras de deteção

O prefixo é detetado quando a entrada começa por exatamente 8 dígitos decimais ASCII (`0`–`9`) seguidos imediatamente de `~`. Se o nono carácter não for `~`, ou se algum dos 8 primeiros não for um dígito, a entrada é tratada como conteúdo GS1 corrente, sem prefixo de correlação.

### Aceder ao identificador de correlação

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

### Combinação com um identificador de simbologia AIM

Um prefixo de correlação pode preceder um identificador de simbologia AIM. O analisador trata este caso de forma transparente:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**Classe de implementação:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 Digital Link

Um **GS1 Digital Link** codifica um ou mais valores de AI diretamente na estrutura de um URL HTTP(S), permitindo identificadores de produtos físicos resolúveis na web. O GAIA implementa a norma *GS1 Digital Link Standard: URI Syntax* (versão 1.7.0) para URI **não comprimidos**.

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

O `GaiaParser` reconhece automaticamente os URI Digital Link: qualquer entrada que comece por `http://` ou `https://` é encaminhada para o `GS1DLParser`, que executa as mesmas fases de conteúdo e de interpretação da cadeia das cadeias de elementos.

### Estrutura do URI e papéis dos AI

Cada AI de um URI Digital Link desempenha um de três papéis, exposto em cada `GS1AIObjectElement` através de `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`):

| Papel | Localização | Exemplo |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | Primeiro par `/ai/valor` do caminho (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | Pares de caminho seguintes, ordenados segundo a chave primária (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | Parâmetros de consulta com chaves totalmente numéricas (§4.10) | `?17=271231` |

Regras estruturais aplicadas (`DLPathRules`):
- Exatamente **uma** chave de identificação primária no caminho; as chaves adicionais têm de ser codificadas como atributos de dados na consulta.
- Os qualificadores de chave têm de ser admitidos pela chave primária e surgir pela ordem prescrita. Os qualificadores opcionais podem ser omitidos, mas os que *estiverem* presentes têm de respeitar a ordem fixada — consulte [Ordem dos qualificadores](#ordem-dos-qualificadores).
- Podem preceder a chave primária segmentos de caminho personalizados arbitrários (por exemplo, `/products/au/01/...`); obtenha-os através de `getDigitalLinkInfo().getCustomPathStem()`.
- As chaves de consulta não numéricas (`linkType`, `context`, parâmetros de extensão como `23P`) são ignoradas; as chaves totalmente numéricas têm de ser AI válidos assinalados com `validAsDataAttribute`.
- Os carateres de valor codificados com percentagem são descodificados; os AI `(03)` e `(8014)` não são permitidos.

As chaves primárias e as suas sequências admissíveis de qualificadores são **orientadas por dados**, a partir das definições dos AI — o indicador `gs1DigitalLinkPrimaryKey` e o atributo `gs1DigitalLinkQualifiers` —, em vez de estarem escritas à mão no código.

Qualquer infração estrutural, ou uma entrada que não seja um URL, produz um erro estrutural de Digital Link (`GE-L001`–`GE-L014`, um código por condição). Os metadados decompostos do URL (`scheme`, `domain`, `path`, `customPathStem`, `query` e o objeto `java.net.URL`) continuam disponíveis através de `getDigitalLinkInfo()`, mesmo na presença de erros estruturais.

### Ordem dos qualificadores

Para cada chave primária, `gs1DigitalLinkQualifiers` enumera uma ou mais sequências **ordenadas** de qualificadores. Dentro de uma sequência, um AI entre parênteses retos é **opcional** e um AI sem parênteses é **obrigatório**, à imagem da notação `[cpv-comp]` da ABNF do §4.9. As sequências de uma mesma chave primária são alternativas mutuamente exclusivas.

O GTIN (`01`), por exemplo, define duas sequências:

| Caminho | Sequência | Significado |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — cada um opcional, mas nesta ordem fixa |
| upui-path | `235` | TPX (obrigatório); GTIN + TPX = UPUI |

Assim, `/01/09506000134352/10/LOT-ABC/21/SER` é válido (LOT antes de SER, CPV omitido), `/01/.../21/SER/10/LOT-ABC` é **rejeitado** (fora de ordem), e `/01/09506000134352/235/2ABC456` corresponde à upui-path. A verificação de ordem é uma correspondência de subsequência que preserva a ordem, pelo que os AI opcionais podem ser saltados, mas nunca reordenados.

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

**Classe de implementação:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## Trabalhar com os resultados

### ParseResult

O resultado de topo devolvido por `GaiaParser.parse()`.

| Método | Devolve | Descrição |
|---|---|---|
| `isValid()` | `boolean` | `true` se não houver erros de qualquer nível. Os avisos não afetam a validade. Sempre `true` quando `getAiObject()` é `null`. |
| `getPayload()` | `String` | A cadeia de entrada depois de removido o prefixo de correlação — e depois de eventuais [modificadores de entrada](#modificadores-de-entrada) a terem reescrito. |
| `getPayloadContent()` | `String` | O payload sem o identificador de simbologia AIM e sem o prefixo ECI. |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (um suporte de dados rejeitado por não ser GS1, por exemplo um suporte Code 39 `]A0`) ou `UNABLE_TO_DETERMINE_CONTENT` (quando `aiObject` é `null`, por exemplo no modo `DATA_CARRIER`). |
| `getRequestedParseMode()` | `ParseMode` | A profundidade de cadeia configurada (`ParseConfig.getRequestedParseMode()`). |
| `getAchievedParseMode()` | `ParseMode` | A fase mais profunda que a análise efetivamente alcançou — ver abaixo. |
| `isParseComplete()` | `boolean` | `true` se a análise atingiu a profundidade pedida (`achieved == requested`). Independente de `isValid()`. |
| `getAiObject()` | `GS1AIObject` | Todos os AI resolvidos. `null` no modo `DATA_CARRIER`. |
| `getErrors()` | `List<GaiaError>` | Todos os erros de nível diferente de WARNING (ao nível do objeto e de todos os elementos). |
| `getWarnings()` | `List<GaiaError>` | Todos os avisos de nível WARNING (ao nível do objeto e de todos os elementos). |
| `hasWarnings()` | `boolean` | `true` se foram emitidos avisos de nível WARNING. |
| `getIssues()` | `List<GaiaError>` | Erros e avisos em conjunto. |
| `hasDataCarrier()` | `boolean` | `true` se foi reconhecido um identificador de simbologia AIM. |
| `getDataCarrier()` | `DataCarrierEntry` | Metadados de simbologia, ou `null` se não foi identificado qualquer suporte. |
| `hasEci()` | `boolean` | `true` se foi removido do payload um indicador ECI. |
| `getEci()` | `EciEntry` | Metadados de codificação ECI, ou `null`. |
| `hasCorrelationId()` | `boolean` | `true` se na entrada original existia um prefixo de correlação `DDDDDDDD~`. |
| `getCorrelationInfo()` | `CorrelationInfo` | O identificador de correlação extraído, ou `null` se não existia nenhum. |
| `isInputModified()` | `boolean` | `true` se um [modificador de entrada](#modificadores-de-entrada) alterou a entrada. |
| `getModifierInfo()` | `ModifierInfo` | O que a cadeia de modificadores fez — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`. `null` se não foi configurado qualquer modificador. |
| `getTiming()` | `ProcessingTiming` | Cronometragem real da análise — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`. `null` se não foi produzido pelo `GaiaParser`. |
| `getVersion()` | `String` | A versão da biblioteca que produziu o resultado. |

#### Modo de análise pedido face ao alcançado

A cadeia percorre a escada **SYNTAX → CONTENT → INTERPRETATION** e para antecipadamente perante erros, pelo que o modo efetivamente *alcançado* pode ser menos profundo do que o *pedido*. `getAchievedParseMode()` indica até onde chegou:

| Pedido | O que acontece | Alcançado | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | um erro **de sintaxe ou de estrutura** interrompe a análise depois da divisão em tokens | `SYNTAX` | `false` |
| `INTERPRETATION` | um erro **de conteúdo** (formato ou dígito de controlo errados) impede o enriquecimento | `CONTENT` | `false` |
| `CONTENT` | a fase de conteúdo decorre sempre até ao fim (os erros são anotados, não são fatais) | `CONTENT` | `true` |
| qualquer (entrada sem erros) | a cadeia atinge a profundidade pedida | = pedido | `true` |
| `DATA_CARRIER` | suporte validado; nenhum conteúdo de AI analisado | `DATA_CARRIER` | `true` |
| qualquer | o suporte de dados é rejeitado antes da análise dos AI (por exemplo, um suporte `]A0` não GS1) | `SYNTAX` | `false` |

`isParseComplete()` é independente de `isValid()`: uma análise `CONTENT` de um GTIN com dígito de controlo errado está **completa** (a fase de conteúdo foi executada) e é ao mesmo tempo **inválida** (o dígito de controlo falhou). Use `isParseComplete()` para perguntar «a cadeia foi tão fundo quanto pedi?» e `isValid()` para perguntar «os dados estão bem formados?».

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

A coleção dos elementos AI resolvidos.

| Método | Descrição |
|---|---|
| `getAis()` | Todas as instâncias de `GS1AIObjectElement`, pela ordem da entrada. |
| `get(String aiCode)` | Primeiro elemento correspondente ao código AI indicado, ou `null`. |
| `contains(String aiCode)` | `true` se existir um AI com esse código. |
| `size()` | Número de AI resolvidos. |
| `isValid()` | `true` se não houver erros ao nível do objeto e nenhum elemento tiver erros. |
| `toHriString()` | Cadeia HRI, por exemplo `(01)09506000134352 (17)261231`. |
| `toElementString()` | Cadeia de elementos em bruto — sem parênteses, com um FNC1 após cada elemento de comprimento variável — por exemplo, `010950600013435210LOT-ABC<GS>17271231`. Devolve `null` se `isValid()` for `false`. |
| `getContentType()` | `GS1_DIGITAL_LINK` quando `hasDigitalLink()` é verdadeiro, caso contrário `GS1_APPLICATION_IDENTIFIERS`. |
| `hasDigitalLink()` | `true` se a entrada era um URI GS1 Digital Link com uma chave de identificação primária. Um URL bem formado sem chave primária expõe ainda assim `getDigitalLinkInfo()`, mas devolve `false` aqui. |
| `getCanonicalDigitalLink()` | O URI GS1 Digital Link canónico (§4.12) em `https://id.gs1.org` — chave primária e qualificadores como segmentos de caminho, atributos de dados como parâmetros de consulta ordenados por chave de AI — ou `null` se não houver chave primária. |
| `getDigitalLinkInfo()` | Metadados de decomposição do URI (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), ou `null` se não se tratar de um Digital Link. |
| `getAllErrors()` | Erros ao nível do objeto + todos os erros dos elementos (diferentes de WARNING). |
| `getAllWarnings()` | Avisos ao nível do objeto + todos os avisos dos elementos. |
| `getAllIssues()` | Tudo em conjunto. |

---

### GS1AIObjectElement

Uma única instância de AI resolvida.

| Método | Descrição |
|---|---|
| `getAi()` | Código AI, por exemplo `"01"`, `"3102"`. |
| `getTitle()` | Título de dados GS1, por exemplo `"GTIN"`, `"BATCH/LOT"`. |
| `getDescription()` | Descrição GS1 completa do AI, **localizada no idioma da análise** (por exemplo, `"Global Trade Item Number (GTIN)"` em inglês). Recua para o texto inglês da definição do AI se não estiver traduzida. |
| `getFormatString()` | Descritor de formato que abrange o AI *e* os seus dados, por exemplo `"N2+N14"` para o AI `01`, `"N2+X..20"` para o AI `10`, `"N4+N3+N..15"` para o AI `3932`. |
| `getValue()` | Valor de dados em bruto extraído da cadeia de elementos. |
| `isFixedLength()` | `true` se o AI tiver um comprimento de dados fixo. |
| `getPosition()` | Desvio de carácter (base zero) na entrada original. |
| `getGS1ComponentValues()` | Fatias de valor por componente (para os AI com vários componentes). |
| `getErrors()` | Erros ao nível do elemento diferentes de WARNING. |
| `getWarnings()` | Avisos de nível WARNING no elemento. |
| `getIssues()` | Erros e avisos do elemento em conjunto. |
| `hasErrors()` | `true` se estiverem anexados erros diferentes de WARNING. |
| `hasWarnings()` | `true` se estiverem anexados avisos de nível WARNING. |
| `getInterpretations()` | Entradas `GS1AIInterpretation` (preenchidas no modo INTERPRETATION). |
| `getInterpretation(String type)` | Primeira interpretação correspondente à chave de tipo de `GS1Constants_Enricher` indicada, ou `null`. |
| `getDigitalLinkAIType()` | O papel Digital Link do elemento (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), ou `null` para entradas do tipo cadeia de elementos. |
| `hasDigitalLinkAIType()` | `true` se foi atribuído um papel Digital Link. |

---

### GaiaError

Um erro de validação ou um aviso, imutável.

| Método | Descrição |
|---|---|
| `getId()` | Identificador de catálogo, por exemplo `"GE-C003"`. |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR` ou `WARNING`. |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT` ou `INTERNAL`. |
| `getCode()` | Código curto legível por máquina. |
| `getAi()` | Código AI que causou o erro, ou `null` para erros ao nível do objeto. |
| `getMessage()` | Mensagem legível, com os valores interpolados. |
| `getPosition()` | Desvio de carácter (base zero) na entrada original. |

---

### GS1AIInterpretation

Um único fragmento de interpretação etiquetado, anexado a um `GS1AIObjectElement` no modo `INTERPRETATION`.

| Método | Descrição |
|---|---|
| `getType()` | Chave de tipo legível por máquina, por exemplo `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`. Estável entre idiomas. |
| `getLabel()` | Etiqueta legível por pessoas, **localizada no idioma da análise** (por exemplo, `"Date"` / `"GS1 company prefix"` em inglês). |
| `getValue()` | Valor extraído ou enriquecido, por exemplo `"31/12/2026"`, `"9506000"`. Não é localizado. |

---

### DataCarrierEntry e DataCarrierType

Quando a entrada traz um identificador de simbologia AIM, `ParseResult.getDataCarrier()` devolve um `DataCarrierEntry` que descreve o símbolo que transportou os dados. A entrada é o registo concreto do catálogo correspondente ao identificador AIM reconhecido; `DataCarrierType` é a enumeração, conhecida em tempo de compilação, a que pertence.

#### DataCarrierEntry

Os metadados de um identificador de simbologia AIM reconhecido (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`).

| Método | Descrição |
|---|---|
| `getAimCodeId()` | O identificador de simbologia AIM reconhecido, por exemplo `"]C1"`. |
| `getName()` | Nome legível do símbolo concreto, por exemplo `"GS1-128 / ISBT 128"`, `"EAN-8"`. |
| `getDescription()` | Descrição mais extensa do suporte. |
| `getType()` | O tipo estrutural do suporte como cadeia (reflete `getDataCarrierType().getCategory()`). |
| `getStandard()` | A norma de simbologia, quando registada. |
| `getDataCarrierType()` | O `DataCarrierType` tipado desta entrada — preferível para o encaminhamento programático. |
| `isGs1Capable()` | `true` se o suporte puder conter dados GS1 (cadeias de elementos AI e/ou Digital Link). |
| `isGs1AICapable()` | `true` se o suporte puder conter cadeias de elementos AI GS1. |
| `isGs1DigitalLinkCapable()` | `true` se o suporte puder conter um URI GS1 Digital Link. |
| `isEciCapable()` | `true` se o suporte admitir um indicador ECI. |
| `isRequiresGtinPadding()` | `true` para os suportes EAN/UPC/ITF cujo valor numérico é preenchido até GTIN-14 antes da análise dos AI. |

#### DataCarrierType

Uma enumeração, conhecida em tempo de compilação, dos tipos de suporte de dados, indexada pelo identificador de simbologia AIM atribuído na ISO/IEC 15424 (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`). O carácter que se segue a `]` (o *carácter de código*) seleciona a família; a maioria das famílias corresponde a uma única constante que abrange todos os modificadores (`ITF` abrange `]I0`–`]I2`; `EAN_UPC` abrange EAN-13, UPC-A, UPC-E e EAN-8). Sempre que a GS1 reserva um modificador para dados de AI, essa variante constitui uma constante própria — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) —, distinta da sua congénere comum. Na ausência de identificador AIM, ou quando este designa um suporte desconhecido, o tipo é `UNKNOWN`.

| Método | Descrição |
|---|---|
| `getCategory()` | A categoria geral `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR` ou `OTHER`. |
| `getCodeChar()` | O carácter de código AIM que identifica a família, por exemplo `"Q"` para QR Code; `null` para `UNKNOWN`. |
| `getDisplayName()` | Nome legível do *tipo* (pode ser mais abrangente do que `DataCarrierEntry.getName()` — por exemplo, `"EAN-13 / UPC-A / UPC-E / EAN-8"` face a `"EAN-8"`). |
| `isGs1DataCarrier()` | `true` para as constantes que designam sempre dados de AI GS1: as quatro variantes reservadas pela GS1 (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) mais `GS1_DATABAR`, que é intrinsecamente GS1 por qualquer modificador `]e` designar um GS1 DataBar. Mais restritivo do que `DataCarrierEntry.isGs1AICapable()` — um `QR_CODE` comum também pode transportar dados de AI GS1. |
| `static forAimCodeId(String)` | Resolve um tipo diretamente a partir de um identificador AIM (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); devolve `UNKNOWN` para um identificador ausente, malformado ou não reconhecido. |

Encaminhar por tipo em vez de por nome — por exemplo, para separar os símbolos lineares (Code 128) dos 2D (QR / Data Matrix):

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` abrange apenas os símbolos de matriz e de pontos; os suportes lineares empilhados (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) são `STACKED_LINEAR`, ainda que sejam habitualmente
designados por códigos de barras «2D». Para tratar ambos como um só grupo — por exemplo, para decidir
se é necessário um leitor de imagem em vez de um leitor laser —, verifique a pertença a qualquer uma das duas categorias.

> A resolução do tipo exige que o identificador de simbologia AIM esteja presente na leitura; sem ele, `getDataCarrier()` é `null` e o tipo é `UNKNOWN`. Configure o leitor para transmitir o prefixo do identificador AIM.

---

## Referência de erros

| Código | Nível | Fase | Significado |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | Prefixo de AI desconhecido — não é possível determinar o comprimento dos dados |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | Entrada demasiado curta para ler um código AI completo |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | Valor truncado — menos carateres do que o AI exige |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | Identificador de aplicação duplicado na cadeia de elementos |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | Dependência de AI obrigatória em falta |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | Emparelhamento de AI excluído — dois AI que não podem coexistir |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | Falha inesperada na divisão em tokens |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | Carácter fora do conjunto codificável da GS1 na cadeia de elementos |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | Separador FNC1 obrigatório em falta após um AI de comprimento variável |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | Dados excedentes para além do máximo de todos os componentes |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | Separador FNC1 após um AI de comprimento fixo em posição intermédia |
| `GE-W002` | WARNING | SYNTAX | FNC1 no final da cadeia de elementos (apenas indicativo) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | Infrações estruturais de um URI Digital Link — um código por condição (URI malformado, esquema, anfitrião, ordem dos qualificadores, AI proibido, sem chave primária (`GE-L013`), várias chaves primárias (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | O valor não satisfaz a expressão regular do AI |
| `GE-C003` | DATA_ERROR | CONTENT | Falha na validação do dígito de controlo |
| `GE-C004` | DATA_ERROR | CONTENT | Falha na validação do par de carateres de controlo |
| `GE-C005` | FORMAT_ERROR | CONTENT | O valor de um componente contém um carácter fora do conjunto permitido |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | Falhas de formato dos componentes — um código por condição de validação (consulte `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | Falhas da validação semântica personalizada — um código por condição de validação (consulte `content/validator/`). **Exceções:** as 14 verificações do prefixo de empresa GS1 enumeradas abaixo têm nível `WARNING`, e `GE-C168` (código numérico de país ISO 3166-1 não reconhecido) tem `FORMAT_ERROR`. |
| Verificações do prefixo de empresa GS1 | WARNING | CONTENT | A chave não começa por um prefixo de empresa GS1 reconhecido, nos AI que transportam uma chave GS1 — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC). Apenas indicativo — não afeta a validade. |
| `GE-C169` | DATA_ERROR | CONTENT | Falha do dígito de controlo IMEI (Luhn) no AI 8040 (IMEI) / 8041 (IMEI2) |
| `GE-C170` | DATA_ERROR | CONTENT | Falha do dígito de controlo EID (Luhn) no AI 8042 (ESIM) |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | Identificador de simbologia AIM não reconhecido |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | Suporte identificado, mas que não admite cadeias de elementos AI GS1 nem URI Digital Link |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | Erro interno inesperado |

> **Defeito conhecido na apresentação das mensagens.** Os modelos do catálogo colocam os valores
> interpolados entre apóstrofos duplicados, à maneira do MessageFormat (`''{value}''`), mas o
> `ErrorRegistry` interpola com um simples `String.replace`, pelo que a duplicação sobrevive até
> `getMessage()` — atualmente verá `value ''09506000134351''` onde os textos das mensagens
> citados neste guia mostram `value '09506000134351'`. Afeta todas as mensagens que
> citam um valor, nos 35 catálogos de idiomas. Não analise as mensagens de erro;
> compare com `getId()` / `getCode()`.

---

## Segurança perante threads

O `GaiaParser` é seguro perante threads depois de construído. Uma única instância pode ser partilhada entre threads e usada em concorrência. O padrão recomendado é construir uma instância no arranque da aplicação e reutilizá-la:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

A `ParseConfig` é imutável e igualmente segura de partilhar. A única obrigação de segurança perante threads que a biblioteca não pode cumprir por si recai sobre os [modificadores de entrada](#modificadores-de-entrada): de cada modificador é guardada em cache uma única instância, partilhada por todas as análises concorrentes, pelo que as implementações têm de ser desprovidas de estado.

---

## Anexo A — constantes de cadeia dos AI

`GS1Constants_AICodes` (no pacote `tools.pantheum.gaia.gs1.constants`) declara uma constante `String` para cada identificador de aplicação reconhecido pelo GAIA. Use estas constantes em vez de escrever à mão as cadeias dos códigos AI:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

Cada constante contém a forma textual do código AI (por exemplo, `AI_01_GTIN = "01"`).

### Identificação e serialização

| AI | Constante | Descrição |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | Código Seriado de Unidade de Expedição (SSCC). |
| `01` | `AI_01_GTIN` | Número Global de Item Comercial (GTIN). |
| `02` | `AI_02_CONTENT` | Número Global de Item Comercial (GTIN) dos itens comerciais contidos. |
| `03` | `AI_03_MTO_GTIN` | Identificação de um item comercial fabricado por encomenda (GTIN). |
| `10` | `AI_10_BATCH_LOT` | Número de lote. |
| `20` | `AI_20_VARIANT` | Variante interna do produto. |
| `21` | `AI_21_SERIAL` | Número de série. |
| `22` | `AI_22_CPV` | Variante do produto de consumo. |
| `235` | `AI_235_TPX` | Extensão serializada do Número Global de Item Comercial (GTIN) controlada por terceiros (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | Identificação adicional do produto atribuída pelo fabricante. |
| `241` | `AI_241_CUST_PART_NO` | Número de peça do cliente. |
| `242` | `AI_242_MTO_VARIANT` | Número de variação de fabrico por encomenda. |
| `243` | `AI_243_PCN` | Número do componente de embalagem. |
| `250` | `AI_250_SECONDARY_SERIAL` | Número de série secundário. |
| `251` | `AI_251_REF_TO_SOURCE` | Referência à entidade de origem. |
| `253` | `AI_253_GDTI` | Identificador Global de Tipo de Documento (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | Componente de extensão do Número Global de Localização (GLN). |
| `255` | `AI_255_GCN` | Número Global de Cupão (GCN). |
| `30` | `AI_30_VAR_COUNT` | Contagem variável de itens (item comercial de medida variável). |
| `37` | `AI_37_COUNT` | Contagem de itens comerciais ou peças de itens comerciais contidos numa unidade logística. |

### Datas e horas

| AI | Constante | Descrição |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | Data de produção (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | Data de vencimento (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | Data de embalagem (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | Data de consumo preferencial (YYMMDD). |
| `16` | `AI_16_SELL_BY` | Data limite de venda (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | Data de validade (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | Data e hora de entrega não antes de (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | Data e hora de entrega não depois de (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | Data de lançamento (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | Data e hora de validade (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | Data de primeira congelação (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | Data de colheita (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | Data limite de teste (YYMMDD[hhmm]). |

### Quantidade e medida — medida variável (métrico)

As famílias de AI de 4 dígitos `310n`–`369n` codificam quantidades de medida variável. O terceiro dígito seleciona o tipo de medida; o **quarto dígito** (`n`, 0–5) é o número de casas decimais implícitas — por exemplo, `AI_3102_NET_WEIGHT_KG` significa peso líquido em kg com 2 casas decimais.

| Família | Padrão da constante (`n` = dígito das casas decimais) | Descrição |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | Peso líquido, quilogramas (item comercial de medida variável). |
| `311n` | `AI_311n_LENGTH_M` | Comprimento ou primeira dimensão, metros (item comercial de medida variável). |
| `312n` | `AI_312n_WIDTH_M` | Largura, diâmetro ou segunda dimensão, metros (item comercial de medida variável). |
| `313n` | `AI_313n_HEIGHT_M` | Profundidade, espessura, altura ou terceira dimensão, metros (item comercial de medida variável). |
| `314n` | `AI_314n_AREA_M` | Área, metros quadrados (item comercial de medida variável). |
| `315n` | `AI_315n_NET_VOLUME_L` | Volume líquido, litros (item comercial de medida variável). |
| `316n` | `AI_316n_NET_VOLUME_M` | Volume líquido, metros cúbicos (item comercial de medida variável). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | Peso logístico, quilogramas. |
| `331n` | `AI_331n_LENGTH_M_LOG` | Comprimento ou primeira dimensão, metros. |
| `332n` | `AI_332n_WIDTH_M_LOG` | Largura, diâmetro ou segunda dimensão, metros. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | Profundidade, espessura, altura ou terceira dimensão, metros. |
| `334n` | `AI_334n_AREA_M_LOG` | Área, metros quadrados. |
| `335n` | `AI_335n_VOLUME_L_LOG` | Volume logístico, litros. |
| `336n` | `AI_336n_VOLUME_M_LOG` | Volume logístico, metros cúbicos. |
| `337n` | `AI_337n_KG_PER_M` | Quilogramas por metro quadrado. |

### Quantidade e medida — medida variável (imperial / EUA)

| Família | Padrão da constante (`n` = dígito das casas decimais) | Descrição |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | Peso líquido, libras (item comercial de medida variável). |
| `321n` | `AI_321n_LENGTH_IN` | Comprimento ou primeira dimensão, polegadas (item comercial de medida variável). |
| `322n` | `AI_322n_LENGTH_FT` | Comprimento ou primeira dimensão, pés (item comercial de medida variável). |
| `323n` | `AI_323n_LENGTH_YD` | Comprimento ou primeira dimensão, jardas (item comercial de medida variável). |
| `324n` | `AI_324n_WIDTH_IN` | Largura, diâmetro ou segunda dimensão, polegadas (item comercial de medida variável). |
| `325n` | `AI_325n_WIDTH_FT` | Largura, diâmetro ou segunda dimensão, pés (item comercial de medida variável). |
| `326n` | `AI_326n_WIDTH_YD` | Largura, diâmetro ou segunda dimensão, jardas (item comercial de medida variável). |
| `327n` | `AI_327n_HEIGHT_IN` | Profundidade, espessura, altura ou terceira dimensão, polegadas (item comercial de medida variável). |
| `328n` | `AI_328n_HEIGHT_FT` | Profundidade, espessura, altura ou terceira dimensão, pés (item comercial de medida variável). |
| `329n` | `AI_329n_HEIGHT_YD` | Profundidade, espessura, altura ou terceira dimensão, jardas (item comercial de medida variável). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | Peso logístico, libras. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | Comprimento ou primeira dimensão, polegadas. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | Comprimento ou primeira dimensão, pés. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | Comprimento ou primeira dimensão, jardas. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | Largura, diâmetro ou segunda dimensão, polegadas. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | Largura, diâmetro ou segunda dimensão, pés. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | Largura, diâmetro ou segunda dimensão, jarda. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | Profundidade, espessura, altura ou terceira dimensão, polegadas. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | Profundidade, espessura, altura ou terceira dimensão, pés. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | Profundidade, espessura, altura ou terceira dimensão, jardas. |
| `350n` | `AI_350n_AREA_IN` | Área, polegadas quadradas (item comercial de medida variável). |
| `351n` | `AI_351n_AREA_FT` | Área, pés quadrados (item comercial de medida variável). |
| `352n` | `AI_352n_AREA_YD` | Área, jardas quadradas (item comercial de medida variável). |
| `353n` | `AI_353n_AREA_IN_LOG` | Área, polegadas quadradas. |
| `354n` | `AI_354n_AREA_FT_LOG` | Área, pés quadrados. |
| `355n` | `AI_355n_AREA_YD_LOG` | Área, jardas quadradas. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | Peso líquido, onças troy (item comercial de medida variável). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | Peso líquido (ou volume), onças (item comercial de medida variável). |
| `360n` | `AI_360n_NET_VOLUME_QT` | Volume líquido, quartos (item comercial de medida variável). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | Volume líquido, galões EUA (item comercial de medida variável). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | Volume logístico, quartos. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | Volume logístico, galões EUA. |
| `364n` | `AI_364n_NET_VOLUME_IN` | Volume líquido, polegadas cúbicas (item comercial de medida variável). |
| `365n` | `AI_365n_NET_VOLUME_FT` | Volume líquido, pés cúbicos (item comercial de medida variável). |
| `366n` | `AI_366n_NET_VOLUME_YD` | Volume líquido, jardas cúbicas (item comercial de medida variável). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | Volume logístico, polegadas cúbicas. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | Volume logístico, pés cúbicos. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | Volume logístico, jardas cúbicas. |

### Preços e montantes monetários

O quarto dígito (`n`) codifica o número de casas decimais implícitas. O seu intervalo permitido
varia consoante a família — consulte a coluna `n`.

| Família | Padrão da constante (`n` = dígito das casas decimais) | `n` | Descrição |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | Montante a pagar aplicável ou valor do cupão, moeda local. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | Montante a pagar aplicável com código de moeda ISO. |
| `392n` | `AI_392n_PRICE` | 0–9 | Montante a pagar aplicável, área monetária única (item comercial de medida variável). |
| `393n` | `AI_393n_PRICE` | 0–9 | Montante a pagar aplicável com código de moeda ISO (item comercial de medida variável). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | Percentagem de desconto de um cupão. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | Montante a pagar por unidade de medida, área monetária única (item comercial de medida variável). |

### Localização e expedição

| AI | Constante | Descrição |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | Número da encomenda de compra do cliente. |
| `401` | `AI_401_GINC` | Número Global de Identificação de Remessa (GINC). |
| `402` | `AI_402_GSIN` | Número Global de Identificação de Expedição (GSIN). |
| `403` | `AI_403_ROUTE` | Código de encaminhamento. |
| `410` | `AI_410_SHIP_TO_LOC` | Número Global de Localização (GLN) de expedir para / entregar a. |
| `411` | `AI_411_BILL_TO` | Número Global de Localização (GLN) de faturar a. |
| `412` | `AI_412_PURCHASE_FROM` | Número Global de Localização (GLN) do local de compra. |
| `413` | `AI_413_SHIP_FOR_LOC` | Número Global de Localização (GLN) de expedir para / entregar para - reencaminhar para. |
| `414` | `AI_414_LOC_NO` | Identificação de uma localização física - Número Global de Localização (GLN). |
| `415` | `AI_415_PAY_TO` | Número Global de Localização (GLN) da entidade emissora da fatura. |
| `416` | `AI_416_PROD_SERV_LOC` | Número Global de Localização (GLN) do local de produção ou prestação de serviço. |
| `417` | `AI_417_PARTY` | Número Global de Localização (GLN) da entidade. |
| `420` | `AI_420_SHIP_TO_POST` | Código postal de expedir para / entregar a, dentro de uma única autoridade postal. |
| `421` | `AI_421_SHIP_TO_POST` | Código postal de expedir para / entregar a com código de país ISO. |
| `422` | `AI_422_ORIGIN` | País de origem de um item comercial. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | País de processamento inicial. |
| `424` | `AI_424_COUNTRY_PROCESS` | País de processamento. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | País de desmontagem. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | País que abrange toda a cadeia de processamento. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | Subdivisão do país de origem. |
| `4300` | `AI_4300_SHIP_TO_COMP` | Nome da empresa de expedir para / entregar a. |
| `4301` | `AI_4301_SHIP_TO_NAME` | Contacto de expedir para / entregar a. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | Linha de endereço 1 de expedir para / entregar a. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | Linha de endereço 2 de expedir para / entregar a. |
| `4304` | `AI_4304_SHIP_TO_SUB` | Bairro de expedir para / entregar a. |
| `4305` | `AI_4305_SHIP_TO_LOC` | Localidade de expedir para / entregar a. |
| `4306` | `AI_4306_SHIP_TO_REG` | Região de expedir para / entregar a. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | Código do país de expedir para / entregar a. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | Número de telefone de expedir para / entregar a. |
| `4309` | `AI_4309_SHIP_TO_GEO` | Localização geográfica de expedir para / entregar a. |
| `4310` | `AI_4310_RTN_TO_COMP` | Nome da empresa de devolução. |
| `4311` | `AI_4311_RTN_TO_NAME` | Contacto de devolução. |
| `4312` | `AI_4312_RTN_TO_ADD1` | Linha de endereço 1 de devolução. |
| `4313` | `AI_4313_RTN_TO_ADD2` | Linha de endereço 2 de devolução. |
| `4314` | `AI_4314_RTN_TO_SUB` | Bairro de devolução. |
| `4315` | `AI_4315_RTN_TO_LOC` | Localidade de devolução. |
| `4316` | `AI_4316_RTN_TO_REG` | Região de devolução. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | Código do país de devolução. |
| `4318` | `AI_4318_RTN_TO_POST` | Código postal de devolução. |
| `4319` | `AI_4319_RTN_TO_PHONE` | Número de telefone de devolução. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | Descrição do código de serviço. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | Indicador de mercadorias perigosas. |
| `4322` | `AI_4322_AUTH_LEAVE` | Autorização para deixar a entrega. |
| `4323` | `AI_4323_SIG_REQUIRED` | Indicador de assinatura obrigatória. |
| `4330` | `AI_4330_MAX_TEMP_F` | Temperatura máxima em Fahrenheit (expressa em centésimos de grau). |
| `4331` | `AI_4331_MAX_TEMP_C` | Temperatura máxima em Celsius (expressa em centésimos de grau). |
| `4332` | `AI_4332_MIN_TEMP_F` | Temperatura mínima em Fahrenheit (expressa em centésimos de grau). |
| `4333` | `AI_4333_MIN_TEMP_C` | Temperatura mínima em Celsius (expressa em centésimos de grau). |

### Atributos de produto e rastreabilidade

| AI | Constante | Descrição |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | Número de Stock da NATO (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | Classificação UN/ECE de carcaças e cortes de carne. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | Potência ativa. |
| `7005` | `AI_7005_CATCH_AREA` | Zona de captura. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | Espécie para fins de pesca. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | Tipo de arte de pesca. |
| `7010` | `AI_7010_PROD_METHOD` | Método de produção. |
| `7020` | `AI_7020_REFURB_LOT` | Identificação do lote de recondicionamento. |
| `7021` | `AI_7021_FUNC_STAT` | Estado funcional. |
| `7022` | `AI_7022_REV_STAT` | Estado de revisão. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | Identificador Global de Ativo Individual (GIAI) de um conjunto. |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | Número do transformador, com código de país ISO de três dígitos (10 posições). |
| `7040` | `AI_7040_UIC_EXT` | UIC GS1 com Extensão 1 e índice do importador. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | Tipo de unidade de carga UN/CEFACT. |

### Números nacionais de comparticipação na saúde (NHRN)

| AI | Constante | Descrição |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | Número Nacional de Reembolso de Saúde (NHRN) - Alemanha PZN. |
| `711` | `AI_711_NHRN_CIP` | Número Nacional de Reembolso de Saúde (NHRN) - França CIP. |
| `712` | `AI_712_NHRN_CN` | Número Nacional de Reembolso de Saúde (NHRN) - Espanha CN. |
| `713` | `AI_713_NHRN_DRN` | Número Nacional de Reembolso de Saúde (NHRN) - Brasil DRN. |
| `714` | `AI_714_NHRN_AIM` | Número Nacional de Reembolso de Saúde (NHRN) - Portugal AIM. |
| `715` | `AI_715_NHRN_NDC` | Número Nacional de Reembolso de Saúde (NHRN) - Estados Unidos da América NDC. |
| `716` | `AI_716_NHRN_AIC` | Número Nacional de Reembolso de Saúde (NHRN) - Itália AIC. |
| `717` | `AI_717_NHRN_SRN` | Número Nacional de Reembolso de Saúde (NHRN) - Costa Rica, Número de Registo Sanitário. |

### Saúde, GMN, HIDRI, CPID, dados pessoais

| AI | Constante | Descrição |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | Referência de certificação (10 posições). |
| `7240` | `AI_7240_PROTOCOL` | Identificador de protocolo. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | Tipo de mídia AIDC. |
| `7242` | `AI_7242_VCN` | Número de Controlo de Versão (VCN). |
| `7250` | `AI_7250_DOB` | Data de nascimento (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | Data e hora de nascimento (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | Sexo biológico. |
| `7253` | `AI_7253_FAMILY_NAME` | Apelido da pessoa. |
| `7254` | `AI_7254_GIVEN_NAME` | Nome próprio da pessoa. |
| `7255` | `AI_7255_SUFFIX` | Sufixo do nome da pessoa. |
| `7256` | `AI_7256_FULL_NAME` | Nome completo da pessoa. |
| `7257` | `AI_7257_PERSON_ADDR` | Endereço da pessoa. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | Sequência de nascimento do bebé. |
| `7259` | `AI_7259_BABY` | Apelido de família do bebé. |
| `8001` | `AI_8001_DIMENSIONS` | Produtos em rolo (largura, comprimento, diâmetro do núcleo, direção, emendas). |
| `8002` | `AI_8002_CMT_NO` | Identificador de telefone móvel celular. |
| `8003` | `AI_8003_GRAI` | Identificador Global de Ativo Retornável (GRAI). |
| `8004` | `AI_8004_GIAI` | Identificador Global de Ativo Individual (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | Preço por unidade de medida. |
| `8006` | `AI_8006_ITIP` | Identificação de uma peça individual de item comercial (ITIP). |
| `8007` | `AI_8007_IBAN` | Número Internacional de Conta Bancária (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | Data e hora de produção (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | Indicador de sensor de leitura ótica. |
| `8010` | `AI_8010_CPID` | Identificador de Componente/Peça (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | Número de série do Identificador de Componente/Peça (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | Versão do software. |
| `8013` | `AI_8013_GMN` | Número Global de Modelo (GMN). |
| `8014` | `AI_8014_MUDI` | Identificador de Registo de Dispositivo Altamente Individualizado (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | Número Global de Relação de Serviço (GSRN) para identificar a relação entre uma organização que oferece serviços e o prestador de serviços. |
| `8018` | `AI_8018_GSRN_RECIPIENT` | Número Global de Relação de Serviço (GSRN) para identificar a relação entre uma organização que oferece serviços e o destinatário dos serviços. |
| `8019` | `AI_8019_SRIN` | Número de Instância de Relação de Serviço (SRIN). |
| `8020` | `AI_8020_REF_NO` | Número de referência do talão de pagamento. |
| `8026` | `AI_8026_ITIP_CONTENT` | Identificação de peças de um item comercial (ITIP) contidas numa unidade logística. |
| `8030` | `AI_8030_DIGSIG` | Assinatura Digital (DigSig). |
| `8040` | `AI_8040_IMEI` | Identidade Internacional de Equipamento Móvel (IMEI). |
| `8041` | `AI_8041_IMEI2` | Identidade Internacional de Equipamento Móvel 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | Número de SIM incorporado. |
| `8043` | `AI_8043_PSIM` | Número de SIM física. |
| `8110` | `AI_8110` | Identificação de código de cupão para uso na América do Norte. |
| `8111` | `AI_8111_POINTS` | Pontos de fidelidade de um cupão. |
| `8112` | `AI_8112` | Identificação de código de cupão de ficheiro de ofertas positivas para uso na América do Norte. |
| `8200` | `AI_8200_PRODUCT_URL` | URL de embalagem estendida. |

### Uso interno / da empresa

| AI | Constante | Descrição |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | Informação mutuamente acordada entre parceiros comerciais. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | Informação interna da empresa (9 posições). |

---

## Anexo B — constantes das chaves de interpretação

Quando `GaiaParser.parse()` é chamado com `ParseMode.INTERPRETATION`, cada `GS1AIObjectElement` pode transportar uma lista de objetos `GS1AIInterpretation` produzidos por enriquecedores especializados. Use as constantes de `GS1Constants_Enricher` (no pacote `tools.pantheum.gaia.gs1.constants`) como chaves para localizar valores de interpretação específicos:

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

As etiquetas de apresentação **não** são constantes: residem nos catálogos localizados em `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json`, indexadas pela constante de tipo. `GS1AIInterpretation.getLabel()` devolve a etiqueta correspondente ao idioma da análise (consulte [Mensagens e etiquetas localizadas](#mensagens-e-etiquetas-localizadas)), recuando para o inglês quando um catálogo omite a chave. A coluna «Etiqueta de apresentação» abaixo apresenta o texto em português tal como é distribuído no catálogo; já as chaves de tipo são estáveis entre idiomas — compare sempre pela chave, nunca pela etiqueta.

### Data e hora

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `DATE_VALUE` | Data | AI de data (11–17, 7003, 7006, 7011, etc.) |
| `DATE_FORMAT` | Formato de data | AI de data |
| `TIME_VALUE` | Hora | AI que transportam uma hora (7003, 7011, 8008, etc.) |
| `TIME_FORMAT` | Formato de hora | AI que transportam uma hora |
| `DATETIME_VALUE` | Data e hora | AI de data e hora |
| `DATETIME_FORMAT` | Formato de data e hora | AI de data e hora |

### Data de colheita

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | Data de início da colheita | AI 7007 |
| `HARVEST_END_DATE` | Data de fim da colheita | AI 7007 (fim de intervalo opcional) |
| `HARVEST_DATE_RANGE` | Intervalo de datas de colheita | AI 7007 |

### Prefixo de empresa GS1

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | Prefixo de empresa GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_CODE` | Código de membro GS1 | AI GTIN / GLN / SSCC |
| `GS1_MEMBER_NAME` | Organização membro GS1 | AI GTIN / GLN / SSCC |

### GTIN

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `GTIN_TYPE` | Tipo de GTIN | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | Nível de embalagem | AI 01 |
| `GTIN_CHECK_DIGIT` | Dígito de controlo | AI 01, 02 |

### SSCC

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | Dígito de extensão | AI 00 |
| `SSCC_SERIAL_REFERENCE` | Referência de série | AI 00 |
| `SSCC_CHECK_DIGIT` | Dígito de controlo | AI 00 |

### País (ISO 3166)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | Código de país (numérico) | AI de país único (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | Código de país (alfa-2) | AI de país alfa-2 |
| `COUNTRY_NAME` | Nome do país | AI de país único |
| `COUNTRY_LIST` | Países | AI 423 — todos os nomes reunidos, por exemplo `Australia, New Zealand` |

O AI 423 (país da primeira transformação) pode transportar até cinco países, pelo que emite um
**par numerado por país** — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — seguido do único resumo
`COUNTRY_LIST`. Componha estas chaves a partir das constantes `COUNTRY_CODE_NUMERIC_PREFIX` /
`COUNTRY_NAME_PREFIX` e do índice de base 1, ou limite-se a percorrer `getInterpretations()`; as
chaves `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` sem sufixo **não** são emitidas para o AI 423.

### Moeda (ISO 4217)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | Código da moeda | AI de montante com moeda (391n, 393n) |
| `CURRENCY_ALPHA` | Código alfabético da moeda | AI de montante com moeda |
| `CURRENCY_NAME` | Nome da moeda | AI de montante com moeda |

### Temperatura

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `TEMPERATURE` | Temperatura | AI 4330–4333 |
| `TEMPERATURE_UNIT` | Unidade de temperatura | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | Temperatura (formatada) | AI 4330–4333 |

### Sexo (ISO 5218)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `SEX_CODE` | Código de sexo | AI 7252 |
| `SEX_DESCRIPTION` | Descrição do sexo | AI 7252 |

### Espécies aquáticas (FAO)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `SPECIES_CODE` | Código de espécie | AI 7008 |
| `SPECIES_SCIENTIFIC` | Nome científico | AI 7008 |
| `SPECIES_ENGLISH` | Nome comum | AI 7008 |
| `SPECIES_FAMILY` | Família | AI 7008 |
| `SPECIES_ORDER` | Ordem | AI 7008 |

### Número de catálogo NATO (NSN)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `NSN_FSG` | Grupo de abastecimento | AI 7001 |
| `NSN_FSG_NAME` | Nome do grupo de abastecimento | AI 7001 |
| `NSN_FSCG` | Classe de fornecimento | AI 7001 |
| `NSN_FSCG_NAME` | Nome da classe de abastecimento | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | Código de país | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | País | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | Código de país ISO | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | Categoria NCS | AI 7001 |
| `NSN_NIIN` | Número nacional do artigo | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### Produtos em rolo

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | Largura do rolo (mm) | AI 8001 |
| `ROLL_LENGTH` | Comprimento do rolo (m) | AI 8001 |
| `CORE_DIAMETER` | Diâmetro do núcleo (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | Código de direção de enrolamento | AI 8001 |
| `WINDING_DIRECTION` | Direção de enrolamento | AI 8001 |
| `SPLICES` | Emendas | AI 8001 |

### IBAN

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | Código de país | AI 8007 |
| `IBAN_COUNTRY_NAME` | País | AI 8007 |
| `IBAN_CHECK_DIGITS` | Dígitos de controlo | AI 8007 |
| `IBAN_CHECK_VALID` | Verificação | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | Número de série | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | Dígito de controlo | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | Organismo emissor | AI 8040, 8041 |

Os 15 dígitos decompõem-se em `[ TAC (8) ][ número de série (6) ][ dígito de controlo de Luhn (1) ]`, sendo o
RBI os 2 primeiros dígitos do TAC: `IMEI_RBI` é, portanto, um prefixo de `IMEI_TAC`, e não
um troço distinto. `IMEI_FORMATTED` apresenta o agrupamento de visualização padrão da GSMA
`AA-BBBBBB-CCCCCC-D` (por exemplo, `49-015420-323751-8`), que divide o TAC na fronteira
do RBI; o antigo agrupamento `6-2-6-1`, que cortava onde começava o descontinuado Final Assembly
Code, não é emitido.

`IMEI_RBI_NAME` resolve o RBI no nome da entidade que o atribui, através de `ImeiRbiData`, e é
**acrescentado em último lugar e apenas quando o código aí consta**. Essa tabela abrange três grupos:

- **Com atribuição ativa** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, além de `99`
  Global Hexadecimal Administrator e `98` (reservado).
- **Intervalos de teste** — `00` e `02`–`09`, que assinalam IMEI de teste em vez de uma atribuição real.
  Consulte-os com `ImeiRbiData.isTestCode(code)`.
- **Sem atribuição** — entidades históricas como `49` (BZT/BAPT, Alemanha), `44`
  (BABT, Reino Unido) ou `91` (MSAI, Índia). Consulte-os com `ImeiRbiData.isNoLongerAllocating(code)`.
  Os dispositivos com estes códigos são correntes e continuam em serviço; cessou apenas a atribuição
  de códigos novos, pelo que se trata de informação de reporte, nunca de um sinal de validade.

A ausência de `IMEI_RBI_NAME` significa «este RBI não consta da nossa tabela», **não** «IMEI inválido»:
a tabela é compilada a partir de uma listagem publicada de RBI e não diretamente da GSMA, podendo
por isso ficar atrás das entidades designadas recentemente. Não retire da sua ausência qualquer conclusão de validação;
o RBI não é um carácter de controlo. O código que percorre a lista de interpretações também tem de
tolerar a sua ausência, em vez de indexar por posição.

### Identificadores de SIM (EID / ICCID)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | Categoria de indústria | AI 8042 |
| `EID_BODY` | Corpo do EID | AI 8042 |
| `EID_CHECK_DIGIT` | Dígito de controlo | AI 8042 |
| `ICCID_BODY` | Corpo do ICCID | AI 8043 |
| `ICCID_EXTENSION` | Extensão | AI 8043 |

`SIM_MII` transporta os **dois** dígitos iniciais (`89`), o par que a ITU-T E.118 atribui às
telecomunicações. A própria ISO/IEC 7812 define o MII como **apenas o primeiro dígito**, pelo que
`SIM_MII_NAME` resolve a categoria a partir desse `8` inicial através de `Iso7812Data` — o que dá
«Healthcare, telecommunications and other future industry assignments». Para um EID bem formado
esse valor é, por isso, constante; é reportado para rastreabilidade face à norma, e não como
critério de distinção. `Iso7812Data.nameForCode(digit)` recebe um dígito isolado e
`nameForIdentifier(prefix)` aceita um prefixo mais longo e lê-lhe o dígito inicial.

`SIM_MII_NAME` é emitido apenas pelo `EidEnricher` (AI 8042). O `IccidEnricher` (AI 8043)
expõe `SIM_MII` sem a categoria.

### Referência de certificação

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | Número de sequência | AI 7230–7239 |
| `CERT_SCHEME_CODE` | Código do esquema de certificação | AI 7230–7239 |
| `CERT_SCHEME_NAME` | Esquema de certificação | AI 7230–7239 |
| `CERT_REFERENCE` | Referência de certificação | AI 7230–7239 |

### GS1 UIC

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `UIC_CODE` | Código UIC | AI 7040 |
| `UIC_EXTENSION_1` | Extensão 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | Índice de importador | AI 7040 |

### Ordem de nascimento do recém-nascido

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | Posição de nascimento | AI 7258 |
| `BIRTH_TOTAL` | Total de nascimentos | AI 7258 |
| `BIRTH_SEQUENCE` | Sequência de nascimento | AI 7258 |

### Número global de modelo (GMN)

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | Referência do modelo | AI 8013 |
| `GMN_CHECK_PAIR` | Par de controlo | AI 8013 |

### HIDRI

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | Referência do dispositivo | AI 8014 |
| `HIDRI_CHECK_PAIR` | Par de controlo | AI 8014 |

### CPID

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | Referência de componente e peça | AI 8010–8011 |

### Valores decimais e de medida

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | Valor decimal | AI numéricos com casas decimais implícitas (31xx–36xx) |
| `DECIMAL_AMOUNT` | Montante | AI de preço (390n–395n) |
| `DECIMAL_PERCENTAGE` | Percentagem | AI 394n |
| `DECIMAL_PLACES` | Casas decimais | Em conjunto com `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` |
| `PERCENTAGE_FORMAT` | Formato de percentagem | AI 394n |
| `ISO_UNIT_CODE` | Código de unidade ISO | AI de medida |
| `ISO_UNIT_NAME` | Nome de unidade ISO | AI de medida |
| `MONETARY_AMOUNT` | Montante monetário | AI de preço |
| `MONETARY_AMOUNT_DISPLAY` | Montante monetário (formatado) | AI de preço |

### Coordenadas geográficas

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `LATITUDE` | Latitude | AI 4309 |
| `LONGITUDE` | Longitude | AI 4309 |
| `GEO_COORDINATES` | Coordenadas geográficas | AI 4309 |
| `LATITUDE_DMS` | Latitude (DMS) | AI 4309 |
| `LONGITUDE_DMS` | Longitude (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | Coordenadas geográficas (DMS) | AI 4309 |

### Método de produção

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | Código de método de produção | AI 7010 |
| `PRODUCTION_METHOD` | Método de produção | AI 7010 |

### Tipo de suporte AIDC

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | Código de tipo de mídia AIDC | AI 7241 |
| `MEDIA_TYPE_NAME` | Tipo de mídia AIDC | AI 7241 |

### Peça do total

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | Número da peça | AI 8006 |
| `PIECE_TOTAL` | Total de peças | AI 8006 |
| `PIECE_OF_TOTAL` | Peça do total | AI 8006 |

### Divisões em componentes

Chaves emitidas pelas divisões em componentes declarativas de `content/ai-content.json`, e não
por um enriquecedor Java: revelam as partes nomeadas do valor de um AI composto. Ao contrário de todas
as outras chaves deste anexo, estas **não têm constante em `GS1Constants_Enricher`**: compare
a cadeia literal, ou leia o tipo através de `GS1AIInterpretation.getType()`.

| Chave de tipo | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | Dígito de controlo | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | Número de série | AI 253, 255, 8003 |
| `POSTAL_CODE` | Código postal | AI 421 |
| `PROCESSOR_ID` | Identificador do processador | AI 7030–7039 |

Note que aqui `CHECK_DIGIT` é a chave genérica da divisão em componentes, distinta das chaves
específicas dos enriquecedores `GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` e
`EID_CHECK_DIGIT` enumeradas acima.

### Diversos

| Constante de chave | Etiqueta de apresentação | Produzida por |
|--------------|---------------|-------------|
| `FLAG_VALUE` | Valor | AI booleanos / de indicador (4321–4323) |
| `DECODED_TEXT` | Texto descodificado | AI de texto livre |
