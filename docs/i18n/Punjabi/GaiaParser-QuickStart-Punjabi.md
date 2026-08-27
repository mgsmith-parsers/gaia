# GaiaParser — ਝਟਪਟ ਸ਼ੁਰੂਆਤ

GS1 ਬਾਰਕੋਡ ਪੇਲੋਡ ਨੂੰ ਲਗਭਗ ਦਸ ਮਿੰਟਾਂ ਵਿੱਚ ਬਣਤਰਬੱਧ, ਤਸਦੀਕ ਕੀਤੇ, ਬੰਦੇ-ਪੜ੍ਹਨਯੋਗ ਡਾਟੇ ਵਿੱਚ
ਪਾਰਸ ਕਰੋ। ਇਹ ਛੋਟਾ ਰਾਹ ਹੈ; ਪੂਰਾ ਹਵਾਲਾ **[GaiaParser ਡਿਵੈਲਪਰ ਗਾਈਡ](GaiaParser-Punjabi.md)** ਹੈ, ਤੇ
**[GaiaBuilder](GaiaBuilder-Punjabi.md)** ਉਲਟੀ ਦਿਸ਼ਾ ਢਕਦਾ ਹੈ (ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਤੇ ਡਿਜੀਟਲ ਲਿੰਕ
URI ਬਣਾਉਣਾ)।

## ਤਤਕਰਾ

1. [ਆਪਣੇ ਪ੍ਰੋਜੈਕਟ ਵਿੱਚ Gaia ਜੋੜੋ](#1-ਆਪਣ-ਪਰਜਕਟ-ਵਚ-gaia-ਜੜ)
2. [ਕੁਝ ਪਾਰਸ ਕਰੋ](#2-ਕਝ-ਪਰਸ-ਕਰ)
3. [ਨਤੀਜਾ ਪੜ੍ਹੋ](#3-ਨਤਜ-ਪੜਹ)
4. [ਨਾਕਾਮ ਪਾਰਸ ਸੰਭਾਲੋ](#4-ਨਕਮ-ਪਰਸ-ਸਭਲ)
5. [ਦੋ ਗੱਲਾਂ ਜੋ ਤੁਹਾਨੂੰ ਡੰਗ ਮਾਰਨਗੀਆਂ](#5-ਦ-ਗਲ-ਜ-ਤਹਨ-ਡਗ-ਮਰਨਗਆ)
6. [ਸਕੈਨਰ ਅਗੇਤਰ ਤੇ ਡਿਜੀਟਲ ਲਿੰਕ ਆਪੇ ਹੀ ਚੱਲਦੇ ਹਨ](#6-ਸਕਨਰ-ਅਗਤਰ-ਤ-ਡਜਟਲ-ਲਕ-ਆਪ-ਹ-ਚਲਦ-ਹਨ)
7. [ਘੱਟ ਕੰਮ ਕਰੋ: ਪਾਰਸ ਢੰਗ](#7-ਘਟ-ਕਮ-ਕਰ-ਪਰਸ-ਢਗ)
8. [ਭਾਸ਼ਾ ਤੇ ਮਿਤੀ ਦਾ ਰੂਪ ਬਦਲੋ](#8-ਭਸ-ਤ-ਮਤ-ਦ-ਰਪ-ਬਦਲ)
9. [ਵਿਗੜੀ ਇਨਪੁਟ ਸਾਫ਼ ਕਰੋ](#9-ਵਗੜ-ਇਨਪਟ-ਸਫ-ਕਰ)
10. [ਅੱਗੇ ਕਿੱਥੇ ਜਾਣਾ ਹੈ](#10-ਅਗ-ਕਥ-ਜਣ-ਹ)

---

## 1. ਆਪਣੇ ਪ੍ਰੋਜੈਕਟ ਵਿੱਚ Gaia ਜੋੜੋ

Gaia Maven Central ਉੱਤੇ ਨਹੀਂ ਛਪਿਆ, ਸੋ ਕੋਰ ਨੂੰ ਇੱਕੋ ਵਾਰ ਬਿਲਡ ਕਰ ਕੇ ਆਪਣੀ ਸਥਾਨਕ ਰਿਪੋਜ਼ਟਰੀ ਵਿੱਚ
ਇੰਸਟਾਲ ਕਰੋ:

```bash
cd gaia && mvn install
```

ਫਿਰ ਇਸ ਉੱਤੇ ਨਿਰਭਰਤਾ ਪਾਓ:

```xml
<dependency>
    <groupId>tools.pantheum</groupId>
    <artifactId>gaia</artifactId>
    <version>1.0.0</version>
</dependency>
```

ਬੱਸ ਇਹੀ ਪੂਰੀ ਨਿਰਭਰਤਾ ਸੂਚੀ ਹੈ ਜੋ ਤੁਹਾਨੂੰ ਲਿਖਣੀ ਪੈਂਦੀ ਹੈ। jar ਪਤਲਾ ਹੈ, ਸੋ Gaia ਦੀ ਇੱਕੋ
ਕੰਪਾਈਲ-ਦਾਇਰੇ ਵਾਲੀ ਨਿਰਭਰਤਾ — `com.fasterxml.jackson.core:jackson-databind` — ਆਪੇ ਨਾਲ
ਆ ਜਾਂਦੀ ਹੈ; ਜੇ ਤੁਹਾਡਾ ਬਿਲਡ ਪਹਿਲਾਂ ਹੀ ਕੋਈ Jackson ਵਰਜਨ ਪੱਕਾ ਕਰਦਾ ਹੈ, ਤਾਂ ਉਹੀ ਚੱਲੇਗਾ ਤੇ Gaia
ਉਹੀ ਵਰਤੇਗਾ। Gaia **ਜਾਵਾ 11** ਨੂੰ ਨਿਸ਼ਾਨਾ ਬਣਾਉਂਦਾ ਹੈ, ਤੇ ਓਹੀ jar ਹਰ ਬਾਅਦ ਵਾਲੇ JVM ਉੱਤੇ ਬਿਨਾਂ ਬਦਲੇ ਚੱਲਦਾ ਹੈ।

> ਸ਼ੁਰੂਆਤ ਕਰਦਿਆਂ ਕੋਰ ਦੀ ਪਰਖ-ਲੜੀ ਛੱਡਣ (`mvn install -DskipTests`) ਨਾਲ ਕੁਝ ਮਿੰਟਾਂ ਦਾ ਕੰਮ
> ਕੁਝ ਸਕਿੰਟਾਂ ਵਿੱਚ ਹੋ ਜਾਂਦਾ ਹੈ।

---

## 2. ਕੁਝ ਪਾਰਸ ਕਰੋ

ਇੱਕੋ ਕਲਾਸ, ਕੋਈ ਸੈਟਿੰਗ ਨਹੀਂ:

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

`parse(String)` **ਪੂਰੀ** ਪਾਈਪਲਾਈਨ ਚਲਾਉਂਦਾ ਹੈ: ਵਾਕ-ਬਣਤਰ, ਸਮੱਗਰੀ ਤਸਦੀਕ, ਤੇ ਵਿਆਖਿਆ।
ਇਹੀ ਠੀਕ ਮੂਲ ਹੈ — ਜੇ ਮਿਣ ਕੇ ਕੋਈ ਕਾਰਨ ਲੱਭੇ ਤਾਂ ਬਾਅਦ ਵਿੱਚ ਇਸ ਨੂੰ ਭੀੜਾ ਕਰ ਲਿਓ।

---

## 3. ਨਤੀਜਾ ਪੜ੍ਹੋ

ਹੱਲ ਕੀਤੇ AI `ParseResult.getAiObject()` ਵਿੱਚ ਰਹਿੰਦੇ ਹਨ। ਕਿਸੇ ਖ਼ਾਸ AI ਤੱਕ ਥਾਂ ਦੀ ਥਾਂ
ਕੋਡ ਨਾਲ ਪਹੁੰਚੋ:

```java
GS1AIObjectElement expiry = result.getAiObject().get("17");   // null if absent
boolean hasExpiry         = result.getAiObject().contains("17");
```

ਹਰ ਐਲੀਮੈਂਟ ਨਾਲ ਇੱਕ **ਵਿਆਖਿਆ** ਸੂਚੀ ਆਉਂਦੀ ਹੈ — ਕੱਚੇ ਅੰਕਾਂ ਪਿੱਛੇ ਲੁਕਿਆ ਡੀਕੋਡ ਕੀਤਾ ਮਤਲਬ,
ਜੋ ਵਿਆਖਿਆ ਵਾਲਾ ਪੜਾਅ ਬਣਾਉਂਦਾ ਹੈ:

```java
for (GS1AIInterpretation i : expiry.getInterpretations()) {
    System.out.printf("%-14s %s%n", i.getLabel(), i.getValue());
}
// Date           31/12/2026
// Date format    dd/mm/yyyy
```

`getLabel()` ਸਥਾਨਕ ਕੀਤਾ ਹੁੰਦਾ ਹੈ ਤੇ ਵਿਖਾਉਣ ਲਈ ਹੈ। ਕੋਡ ਵਿੱਚ ਕੋਈ ਮੁੱਲ *ਪੜ੍ਹਨ* ਲਈ ਇਸ ਦੀ ਥਾਂ
ਉਸ ਨੂੰ ਪੱਕੀ ਕਿਸਮ-ਕੁੰਜੀ ਨਾਲ ਲੱਭੋ:

```java
String date = expiry.getInterpretation("DATE_VALUE").getValue();   // "31/12/2026"
```

ਵੱਖ-ਵੱਖ AI ਵੱਖ-ਵੱਖ ਕੁੰਜੀਆਂ ਦਿੰਦੇ ਹਨ — GTIN ਆਪਣਾ ਕੰਪਨੀ ਅਗੇਤਰ, GTIN ਕਿਸਮ ਤੇ ਜਾਂਚ ਅੰਕ ਦਿੰਦਾ ਹੈ;
ਕੀਮਤ ਮੁਦਰਾ ਤੇ ਦਸ਼ਮਲਵ ਰਕਮ ਦਿੰਦੀ ਹੈ। ਪੂਰੀ ਸੂਚੀ
[ਅੰਤਿਕਾ B](GaiaParser-Punjabi.md#ਅਤਕ-b--ਵਆਖਆ-ਕਜ-ਸਥਰਕ) ਹੈ, ਤੇ ਸਥਿਰਾਂਕ
`GS1Constants_Enricher` ਵਿੱਚ ਰਹਿੰਦੇ ਹਨ। ਹਰ AI ਦੀਆਂ ਵਿਆਖਿਆਵਾਂ ਨਹੀਂ ਹੁੰਦੀਆਂ: ਖੁੱਲ੍ਹੇ-ਪਾਠ ਵਾਲੇ
ਬੈਚ/ਲਾਟ ਵਿੱਚੋਂ ਕੁਝ ਕੱਢਣ ਨੂੰ ਹੀ ਨਹੀਂ ਹੁੰਦਾ, ਸੋ ਉਸ ਦੀ ਸੂਚੀ ਖ਼ਾਲੀ ਰਹਿੰਦੀ ਹੈ।

---

## 4. ਨਾਕਾਮ ਪਾਰਸ ਸੰਭਾਲੋ

ਨਾਜਾਇਜ਼ ਪੇਲੋਡ ਆਮ ਨਤੀਜਾ ਹੈ, ਅਪਵਾਦ ਨਹੀਂ — ਖ਼ਰਾਬ GS1 ਡਾਟੇ ਲਈ `parse` ਕਦੇ ਅਪਵਾਦ ਨਹੀਂ ਸੁੱਟਦਾ:

```java
ParseResult bad = PARSER.parse("0109506000134350");   // last digit should be 2

bad.isValid();      // false
bad.getErrors();    // one GaiaError
```

```
[GE-C003] Check digit validation failed for AI (01) value ''09506000134350''   (AI 01, level DATA_ERROR)
```

**`getId()` ਉੱਤੇ ਸ਼ਾਖਾ ਬਣਾਓ, ਸੁਨੇਹੇ ਉੱਤੇ ਕਦੇ ਨਹੀਂ।** ਸੁਨੇਹੇ ਸਥਾਨਕ ਕੀਤੇ ਹੁੰਦੇ ਹਨ ਤੇ ਉਹਨਾਂ ਦੀ
ਸ਼ਬਦਾਵਲੀ ਕੋਈ ਇਕਰਾਰ ਨਹੀਂ — ਤੇ ਇਸ ਵੇਲੇ ਉਹਨਾਂ ਵਿੱਚ ਉਲਟੇ ਕਾਮਿਆਂ ਦੀ ਇੱਕ ਜਾਣੀ-ਪਛਾਣੀ ਖ਼ਰਾਬੀ ਹੈ
(ਉੱਪਰਲੇ ਦੂਹਰੇ `''`), ਜਿਸ ਦਾ ਜ਼ਿਕਰ [ਗ਼ਲਤੀਆਂ ਦੇ ਹਵਾਲੇ](GaiaParser-Punjabi.md#ਗਲਤਆ-ਦ-ਹਵਲ) ਵਿੱਚ ਹੈ।

ਦੋ ਵੱਖਰੇ ਸਵਾਲ, ਦੋ ਵੱਖਰੀਆਂ ਵਿਧੀਆਂ:

```java
bad.isValid();           // false — is the data well-formed?
bad.isParseComplete();   // false — did the pipeline run as deep as I asked?
bad.getAchievedParseMode();   // CONTENT — enrichment was skipped because content failed
```

ਕੋਈ ਪੜਾਅ ਨਾਕਾਮ ਹੁੰਦਿਆਂ ਹੀ ਪਾਰਸ ਹੇਠਾਂ ਉਤਰਨਾ ਬੰਦ ਕਰ ਦਿੰਦਾ ਹੈ, ਸੋ ਗ਼ਲਤ ਜਾਂਚ ਅੰਕ ਦਾ ਮਤਲਬ ਹੈ
ਕਿ ਤੁਹਾਨੂੰ ਤਸਦੀਕ ਗ਼ਲਤੀਆਂ ਤਾਂ ਮਿਲਣਗੀਆਂ ਪਰ ਵਿਆਖਿਆਵਾਂ ਨਹੀਂ।

### ਚੇਤਾਵਨੀਆਂ ਨਤੀਜੇ ਨੂੰ ਨਾਜਾਇਜ਼ ਨਹੀਂ ਬਣਾਉਂਦੀਆਂ

ਕੁਝ ਪਰਖਾਂ ਸਲਾਹ ਵਾਲੀਆਂ ਹਨ। ਅਣਪਛਾਤਾ GS1 ਕੰਪਨੀ ਅਗੇਤਰ ਦੱਸਿਆ ਤਾਂ ਜਾਂਦਾ ਹੈ, ਪਰ ਪੇਲੋਡ ਫਿਰ ਵੀ
ਬਣਤਰੀ ਪੱਖੋਂ ਠੀਕ ਰਹਿੰਦਾ ਹੈ:

```java
ParseResult w = PARSER.parse("0109888880000010");

w.isValid();        // true
w.getErrors();      // empty
w.getWarnings();    // [GE-C146] AI (01) value ''09888880000010'' does not contain a
                    //           recognised GS1 company prefix (GTIN)
```

ਜਦੋਂ ਦੋਵੇਂ ਚਾਹੀਦੇ ਹੋਣ ਤਾਂ `getIssues()` ਵਰਤੋ। ਜੇ ਤੁਹਾਡੇ ਕੰਮ-ਵਹਾਅ ਲਈ ਅਣਜਾਣ ਅਗੇਤਰ ਰੱਦ ਕਰਨੇ
ਲਾਜ਼ਮੀ ਹੋਣ, ਤਾਂ `getWarnings()` ਆਪ ਵੇਖੋ — `isValid()` ਇਹ ਤੁਹਾਡੇ ਲਈ ਨਹੀਂ ਕਰੇਗਾ।

---

## 5. ਦੋ ਗੱਲਾਂ ਜੋ ਤੁਹਾਨੂੰ ਡੰਗ ਮਾਰਨਗੀਆਂ

### GS ਵਿਭਾਜਕ, ਤੇ ਇਸ ਨੂੰ ਛੱਡਣਾ ਗ਼ਲਤੀ ਨਾਲੋਂ ਵੀ ਭੈੜਾ ਕਿਉਂ ਹੈ

ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲਾ AI ਕਿਸੇ **GS ਅੱਖਰ** (ASCII `0x1D`, ਜਿਸ ਨੂੰ ਬਾਰਕੋਡ ਸਿੰਬੋਲੋਜੀਆਂ ਵਿੱਚ
FNC1 ਕਹਿੰਦੇ ਹਨ) ਜਾਂ ਸਟ੍ਰਿੰਗ ਦੇ ਅੰਤ ਤੱਕ ਚੱਲਦਾ ਹੈ। ਜਦੋਂ ਉਸ ਮਗਰੋਂ ਕੋਈ ਹੋਰ AI ਆਵੇ, ਤਾਂ ਉਹ ਵਿਭਾਜਕ
ਲਾਜ਼ਮੀ ਹੈ:

```java
String input = "0109506000134352" + "10LOT-ABC" + "\u001D" + "21SN-98765";
ParseResult r = PARSER.parse(input);
// (01)09506000134352 (10)LOT-ABC (21)SN-98765
```

ਇਸ ਨੂੰ ਛੱਡ ਦਿਓ ਤਾਂ ਤੁਹਾਨੂੰ ਗ਼ਲਤੀ **ਨਹੀਂ** ਮਿਲਦੀ — ਪੂਰੇ ਭਰੋਸੇ ਨਾਲ ਦਿੱਤਾ ਗ਼ਲਤ ਜਵਾਬ ਮਿਲਦਾ ਹੈ:

```java
PARSER.parse("0109506000134352" + "10LOT-ABC" + "21SN-98765").getAiObject().toHriString();
// (01)09506000134352 (10)LOT-ABC21SN-98765      ← valid=true, and the serial is gone
```

AI `10` `X..20` ਹੈ, ਸੋ ਇਹ ਪੂਰੇ ਹੱਕ ਨਾਲ `LOT-ABC21SN-98765` ਨਿਗਲ ਜਾਂਦਾ ਹੈ ਤੇ ਪਾਰਸਰ ਕੋਲ ਇਹ
ਜਾਣਨ ਦਾ ਕੋਈ ਰਾਹ ਨਹੀਂ ਕਿ ਇਹ ਮਕਸਦ ਨਹੀਂ ਸੀ। ਅੱਗੇ ਕੋਈ ਵੀ ਇਸ ਨੂੰ ਸੰਭਾਲ ਨਹੀਂ ਸਕਦਾ, ਸੋ ਵਿਭਾਜਕ
ਸਰੋਤ ਉੱਤੇ ਹੀ ਠੀਕ ਰੱਖੋ: ਸਕੈਨਰ ਦੇ ਬਾਈਟ **ISO-8859-1** ਵਜੋਂ ਪੜ੍ਹੋ ਤਾਂ ਜੋ `0x1D` ਬਚਿਆ ਰਹੇ, ਤੇ
ਜਾਵਾ ਸਟ੍ਰਿੰਗ ਲਿਟਰਲ ਵਿੱਚ `"\u001D"` ਲਿਖੋ। ਸਥਿਰ-ਲੰਬਾਈ ਵਾਲੇ AI (`01`, `17`, `3103`) ਨੂੰ ਵਿਭਾਜਕ
ਨਹੀਂ ਚਾਹੀਦਾ — ਪਾਰਸਰ ਨੂੰ ਉਹਨਾਂ ਦੀ ਲੰਬਾਈ ਪਤਾ ਹੈ।

### ਬਹੁਤੇ AI ਇਕੱਲੇ ਖੜ੍ਹੇ ਨਹੀਂ ਹੋ ਸਕਦੇ

ਬੈਚ/ਲਾਟ, ਸੀਰੀਅਲ, ਮਿਆਦ ਤੇ ਇਹਨਾਂ ਵਰਗੇ ਬਾਕੀ *ਗੁਣ* ਹਨ: GS1 General Specifications ਲਾਜ਼ਮੀ
ਕਰਦੇ ਹਨ ਕਿ ਇਹ ਕਿਸੇ ਪਛਾਣ ਕੁੰਜੀ ਨਾਲ ਹੀ ਤੁਰਨ, ਤੇ Gaia ਇਹ ਲਾਗੂ ਕਰਦਾ ਹੈ।

```java
PARSER.parse("10LOT-ABC").isValid();   // false
// [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 but no valid
//           combination was found in the element string
```

GTIN ਜੋੜ ਦਿਓ ਤਾਂ ਇਹ ਲੰਘ ਜਾਂਦਾ ਹੈ। ਜੇ ਤੁਹਾਨੂੰ ਸੱਚਮੁੱਚ ਕੋਈ ਟੋਟਾ ਪਾਰਸ ਕਰਨਾ ਪਵੇ — ਕੋਈ ਯੂਨਿਟ
ਟੈਸਟ, ਕੋਈ ਅਧੂਰਾ ਸਕੈਨ — ਤਾਂ ਇਹ ਪਰਖ ਬੰਦ ਕਰ ਦਿਓ:

```java
ParseConfig fragment = ParseConfig.builder().skipRequiresCheck(true).build();
PARSER.parse("10LOT-ABC", fragment).isValid();   // true
```

---

## 6. ਸਕੈਨਰ ਅਗੇਤਰ ਤੇ ਡਿਜੀਟਲ ਲਿੰਕ ਆਪੇ ਹੀ ਚੱਲਦੇ ਹਨ

ਤੁਹਾਨੂੰ Gaia ਨੂੰ ਦੱਸਣ ਦੀ ਲੋੜ ਨਹੀਂ ਕਿ ਇਨਪੁਟ ਕਿਸ ਸ਼ਕਲ ਦੀ ਹੈ — ਇਹ ਚਾਰੇ ਰੂਪ ਆਪ ਪਛਾਣ ਲੈਂਦਾ ਹੈ।
ਸਕੈਨਰ ਨੇ ਜੋ ਦਿੱਤਾ, ਉਹੀ ਇਸ ਨੂੰ ਦੇ ਦਿਓ।

**AIM ਕੋਡ ID ਅਗੇਤਰ** ਸਿੰਬੋਲੋਜੀ ਦੱਸਦਾ ਹੈ ਤੇ ਆਪੇ ਲਾਹ ਦਿੱਤਾ ਜਾਂਦਾ ਹੈ:

```java
ParseResult scan = PARSER.parse("]C1010950600013435210LOT-ABC");

scan.isValid();                                  // true
scan.getDataCarrier().getName();                 // "GS1-128 / ISBT 128"
scan.getDataCarrier().getDataCarrierType();      // GS1_128  — switch on this, not the name
scan.getPayloadContent();                        // "010950600013435210LOT-ABC"
```

**GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI** ਓਸੇ ਤਸਦੀਕ ਤੇ ਭਰਪੂਰਤਾ ਵਿੱਚੋਂ ਲੰਘਦਾ ਹੈ:

```java
ParseResult dl = PARSER.parse("https://example.com/01/09506000134352/10/LOT-ABC?17=271231");

dl.getContentType();                             // GS1_DIGITAL_LINK
dl.getAiObject().toHriString();                  // (01)09506000134352 (10)LOT-ABC (17)271231
dl.getAiObject().getCanonicalDigitalLink();      // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
dl.getAiObject().toElementString();              // 010950600013435210LOT-ABC<GS>17271231
```

ਕਿਉਂਕਿ ਦੋਵੇਂ ਰੂਪ ਓਸੇ `GS1AIObject` ਵਿੱਚ ਪੁੱਜਦੇ ਹਨ, ਸਕੈਨ ਵਰਤਣ ਵਾਲੇ ਕੋਡ ਨੂੰ ਇਹ ਪਰਵਾਹ ਕਰਨ ਦੀ ਲੋੜ
ਨਹੀਂ ਕਿ ਕਿਹੜਾ ਆਇਆ ਸੀ — ਤੇ `toElementString()` / `getCanonicalDigitalLink()` ਇਹਨਾਂ ਵਿੱਚ
ਅਦਲਾ-ਬਦਲੀ ਕਰ ਦਿੰਦੇ ਹਨ।

**8-ਅੰਕੀ ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ** (`12345678~…`) ਵੀ ਲਾਹਿਆ ਜਾਂਦਾ ਹੈ ਤੇ `getCorrelationInfo()` ਉੱਤੇ
ਸਾਂਭਿਆ ਰਹਿੰਦਾ ਹੈ, ਜੇ ਤੁਹਾਡੀ ਪਾਈਪਲਾਈਨ ਇਹ ਵਰਤਦੀ ਹੋਵੇ।

---

## 7. ਘੱਟ ਕੰਮ ਕਰੋ: ਪਾਰਸ ਢੰਗ

ਮੂਲ ਢੰਗ ਸਭ ਕੁਝ ਕਰਦਾ ਹੈ। ਜਦੋਂ ਤੁਹਾਨੂੰ ਜਵਾਬ ਦਾ ਸਿਰਫ਼ ਇੱਕ ਹਿੱਸਾ ਚਾਹੀਦਾ ਹੋਵੇ ਤਾਂ ਘੱਟ ਮੰਗੋ:

| ਢੰਗ | ਕਿਸ ਦਾ ਜਵਾਬ ਦਿੰਦਾ ਹੈ | ਖ਼ਰਚਾ |
|---|---|---|
| `DATA_CARRIER` | ਇਹ ਕਿਹੜੀ ਸਿੰਬੋਲੋਜੀ ਹੈ? | ਸਭ ਤੋਂ ਸਸਤਾ — ਕੋਈ AI ਪਾਰਸਿੰਗ ਹੀ ਨਹੀਂ, `getAiObject()` `null` ਹੁੰਦਾ ਹੈ |
| `SYNTAX` | AI ਕੋਡ ਤੇ ਲੰਬਾਈਆਂ ਠੀਕ ਬਣੀਆਂ ਹਨ? | ਕੋਈ ਜਾਂਚ ਅੰਕ ਨਹੀਂ, ਕੋਈ ਵਿਆਖਿਆ ਨਹੀਂ |
| `CONTENT` | ਕੀ ਇਹ ਜਾਇਜ਼ GS1 ਡਾਟਾ ਹੈ? | ਪੂਰੀ ਤਸਦੀਕ, ਕੋਈ ਵਿਆਖਿਆ ਨਹੀਂ |
| `INTERPRETATION` | ਇਸ ਦਾ ਮਤਲਬ ਕੀ ਹੈ? | **ਮੂਲ** — ਸਭ ਕੁਝ |

```java
ParseConfig fast = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .build();

ParseResult r = PARSER.parse(input, fast);
```

ਜਦੋਂ ਤੁਸੀਂ ਵੱਡੀ ਗਿਣਤੀ ਵਿੱਚ ਤਸਦੀਕ ਕਰ ਰਹੇ ਹੋਵੋ ਤੇ ਨਿਖੇੜਾ ਕਦੇ ਨਾ ਵਿਖਾਉਂਦੇ ਹੋਵੋ ਤਾਂ `CONTENT`
ਚੁਣੋ, ਤੇ ਜਦੋਂ ਸਿਰਫ਼ ਸਕੈਨ ਨੂੰ ਠੀਕ ਸੰਭਾਲਣ ਵਾਲੇ ਵੱਲ ਭੇਜਣਾ ਹੋਵੇ ਤਾਂ `DATA_CARRIER`।

---

## 8. ਭਾਸ਼ਾ ਤੇ ਮਿਤੀ ਦਾ ਰੂਪ ਬਦਲੋ

ਗ਼ਲਤੀ ਸੁਨੇਹੇ, ਵਿਆਖਿਆ ਲੇਬਲ ਤੇ AI ਵੇਰਵੇ **35 ਭਾਸ਼ਾਵਾਂ** ਵਿੱਚ ਅਨੁਵਾਦ ਕੀਤੇ ਹੋਏ ਹਨ; ਮਿਤੀਆਂ ਜਿਵੇਂ
ਚਾਹੋ ਵਿਖਦੀਆਂ ਹਨ। ਇਹ ਸਭ ਇੱਕੋ ਅਟੱਲ `ParseConfig` ਵਿੱਚ ਹੈ:

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

ਮੁੱਲ ਕਦੇ ਸਥਾਨਕ ਨਹੀਂ ਕੀਤੇ ਜਾਂਦੇ — ਸਿਰਫ਼ ਲੇਬਲ, ਵੇਰਵੇ ਤੇ ਸੁਨੇਹੇ — ਸੋ `"2026-12-31"` ਤੇ
`"09506000134352"` ਦਾ ਮਤਲਬ ਹਰ ਭਾਸ਼ਾ ਵਿੱਚ ਇੱਕੋ ਹੀ ਹੈ। ਸ਼ੁਰੂ ਵਿੱਚ ਇੱਕੋ ਵਾਰ ਸੈਟਿੰਗ ਬਣਾਓ ਤੇ ਉਸ ਨੂੰ
ਸਾਂਝਾ ਕਰੋ; ਇਹ ਅਟੱਲ ਹੈ।

---

## 9. ਵਿਗੜੀ ਇਨਪੁਟ ਸਾਫ਼ ਕਰੋ

ਜੇ ਤੁਹਾਡਾ ਸਰੋਤ ਛਪੇ ਹੋਏ HRI ਬਰੈਕਟ ਜਾਂ ਬੇਲੋੜੀਆਂ ਥਾਂਵਾਂ ਕੱਢਦਾ ਹੋਵੇ, ਤਾਂ ਕੋਰ ਵਿੱਚ ਦੋ **ਇਨਪੁਟ
ਮੋਡੀਫਾਇਰ** ਆਉਂਦੇ ਹਨ ਜੋ ਪਾਰਸ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ ਪੇਲੋਡ ਸੰਵਾਰ ਦਿੰਦੇ ਹਨ:

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

ਮੂਲ ਰੂਪ ਵਿੱਚ ਕੁਝ ਵੀ ਸੈੱਟ ਨਹੀਂ ਹੁੰਦਾ, ਤੇ ਦੋਹਾਂ ਨਾਲ ਸ਼ਰਤਾਂ ਜੁੜੀਆਂ ਹਨ — ਥਾਂ ਤੇ ਬਰੈਕਟ ਜਾਇਜ਼ GS1
ਡਾਟਾ ਅੱਖਰ ਹਨ, ਸੋ ਇਹ ਸਿਰਫ਼ ਉਸ ਸਰੋਤ ਉੱਤੇ ਲਾਓ ਜਿਸ ਬਾਰੇ ਤੁਹਾਨੂੰ ਪਤਾ ਹੋਵੇ। ਵੇਖੋ
[ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ](GaiaParser-Punjabi.md#ਅਦਰਨ-ਮਡਫਇਰ), ਜੋ ਇਹ ਵੀ ਸਮਝਾਉਂਦਾ ਹੈ ਕਿ ਬਰੈਕਟ ਲਾਹੁਣ
ਵੇਲੇ ਉਹ ਵਿਭਾਜਕ ਮੁੜ ਕਿਉਂ ਪਾਉਣਾ ਪੈਂਦਾ ਹੈ ਜਿਸ ਵੱਲ ਬਰੈਕਟ ਇਸ਼ਾਰਾ ਕਰ ਰਹੇ ਸਨ।

---

## 10. ਅੱਗੇ ਕਿੱਥੇ ਜਾਣਾ ਹੈ

- **[GaiaParser ਡਿਵੈਲਪਰ ਗਾਈਡ](GaiaParser-Punjabi.md)** — ਪਾਈਪਲਾਈਨ ਦਾ ਵੇਰਵਾ, ਪੂਰਾ ਨਤੀਜਾ
  ਮਾਡਲ, ਹਰ ਗ਼ਲਤੀ ਕੋਡ, ਤੇ AI ਤੇ ਵਿਆਖਿਆ-ਕੁੰਜੀ ਵਾਲੀਆਂ ਅੰਤਿਕਾਵਾਂ।
- **[GaiaBuilder ਡਿਵੈਲਪਰ ਗਾਈਡ](GaiaBuilder-Punjabi.md)** — AI/ਮੁੱਲ ਜੋੜਿਆਂ ਤੋਂ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਤੇ
  ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਓ।
- **[Gaia API HTTP ਹਵਾਲਾ](../../gaia-api-reference.md)** — ਓਹੀ ਇੰਜਣ HTTP ਉੱਤੇ, ਜੇ ਤੁਸੀਂ
  ਲਾਇਬ੍ਰੇਰੀ ਆਪਣੇ ਵਿੱਚ ਨਾ ਪਾਉਣੀ ਚਾਹੋ।
- **[ai-codes.txt](../../ai-codes.txt)** — ਝਟਪਟ ਲੱਭਣ ਲਈ ਸਾਦੀ `(AI) TITLE` ਸੂਚੀ।

### ਪੰਜ-ਸਤਰੀ ਰੂਪ

```java
private static final GaiaParser PARSER = new GaiaParser();   // once, shared, thread-safe

ParseResult r = PARSER.parse(scannedString);                 // any form, auto-detected
if (r.isValid()) use(r.getAiObject());                       // get("01"), getInterpretation(...)
else             report(r.getErrors());                      // branch on getId()
```
