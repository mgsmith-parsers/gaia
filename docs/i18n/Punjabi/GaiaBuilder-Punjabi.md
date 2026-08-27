# GaiaBuilder — ਡਿਵੈਲਪਰ ਗਾਈਡ

## ਤਤਕਰਾ

1. [ਸੰਖੇਪ ਜਾਣ-ਪਛਾਣ](#ਸਖਪ-ਜਣ-ਪਛਣ)
2. [GS1 ਤੇ General Specifications ਬਾਰੇ](#gs1-ਤ-general-specifications-ਬਰ)
3. [ਝਟਪਟ ਸ਼ੁਰੂਆਤ](#ਝਟਪਟ-ਸਰਆਤ)
4. [ਇਹ ਕਿਵੇਂ ਕੰਮ ਕਰਦਾ ਹੈ](#ਇਹ-ਕਵ-ਕਮ-ਕਰਦ-ਹ)
5. [ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਬਣਾਉਣਾ](#ਐਲਮਟ-ਸਟਰਗ-ਬਣਉਣ)
   - [ਗੁਣ ਵਾਲੇ AI ਨੂੰ ਆਪਣੀ ਪਛਾਣ ਕੁੰਜੀ ਚਾਹੀਦੀ ਹੈ](#ਗਣ-ਵਲ-ai-ਨ-ਆਪਣ-ਪਛਣ-ਕਜ-ਚਹਦ-ਹ)
6. [ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਉਣਾ](#ਡਜਟਲ-ਲਕ-uri-ਬਣਉਣ)
7. [BuilderDigitalLinkConfig](#builderdigitallinkconfig)
8. [ਤਸਦੀਕ ਤੇ ਗ਼ਲਤੀਆਂ](#ਤਸਦਕ-ਤ-ਗਲਤਆ)
   - [ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲੀਆਂ ਬਿਲਡ ਵਿਧੀਆਂ](#ਅਪਵਦ-ਸਟਣ-ਵਲਆ-ਬਲਡ-ਵਧਆ)
   - [ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲੀਆਂ tryBuild\* ਵਿਧੀਆਂ](#ਅਪਵਦ-ਨ-ਸਟਣ-ਵਲਆ-trybuild-ਵਧਆ)
   - [ਗ਼ਲਤੀ ਸੁਨੇਹਿਆਂ ਦੀ ਭਾਸ਼ਾ](#ਗਲਤ-ਸਨਹਆ-ਦ-ਭਸ)
   - [BuildResult](#buildresult)
9. [ਜਾਂਚ ਅੰਕ](#ਜਚ-ਅਕ)
10. [ਥ੍ਰੈੱਡ ਸੁਰੱਖਿਆ](#ਥਰਡ-ਸਰਖਆ)
11. [API ਹਵਾਲਾ](#api-ਹਵਲ)

---

## ਸੰਖੇਪ ਜਾਣ-ਪਛਾਣ

`GaiaBuilder` [`GaiaParser`](GaiaParser-Punjabi.md) ਦਾ ਉਲਟ ਹੈ: ਇਹ ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ (AI) / ਮੁੱਲ ਜੋੜਿਆਂ ਦੇ ਸਮੂਹ ਨੂੰ ਠੀਕ-ਬਣੀ GS1 **ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ** ਜਾਂ **GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI** ਵਿੱਚ ਬਦਲ ਦਿੰਦਾ ਹੈ। ਤੁਸੀਂ AI ਤੇ ਉਹਨਾਂ ਦੇ ਪੂਰੇ ਡਾਟਾ ਮੁੱਲ ਦਿੰਦੇ ਹੋ; ਬਿਲਡਰ ਉਹਨਾਂ ਨੂੰ ਜੋੜਦਾ ਹੈ, ਨਤੀਜੇ ਨੂੰ ਓਸੇ ਇੰਜਣ ਨਾਲ ਤਸਦੀਕ ਕਰਦਾ ਹੈ ਜੋ `GaiaParser` ਵਰਤਦਾ ਹੈ, ਤੇ ਫਿਰ ਨਤੀਜਾ ਵਿਖਾਉਂਦਾ ਹੈ।

ਕਿਉਂਕਿ ਬਿਲਡਰ *ਆਪਣੇ ਹੀ ਸੰਭਾਵੀ ਨਤੀਜੇ ਨੂੰ ਪਾਰਸ ਕਰ ਕੇ* ਤਸਦੀਕ ਕਰਦਾ ਹੈ, ਇਸ ਲਈ ਜੋ ਕੁਝ ਵੀ ਇਹ ਮੋੜਦਾ ਹੈ ਉਹ `GaiaParser` ਰਾਹੀਂ ਸਾਫ਼-ਸੁਥਰਾ ਵਾਪਸ ਪਾਰਸ ਹੋਣ ਦੀ ਗਾਰੰਟੀ ਰੱਖਦਾ ਹੈ — ਇਹ ਦੋਵੇਂ ਕਦੇ ਵੀ ਇਸ ਗੱਲ ਉੱਤੇ ਵੱਖ ਨਹੀਂ ਹੋ ਸਕਦੇ ਕਿ ਠੀਕ-ਬਣਿਆ ਕੀ ਹੈ।

**ਦਾਖ਼ਲਾ ਕਲਾਸ:** `tools.pantheum.gaia.GaiaBuilder`

---

## GS1 ਤੇ General Specifications ਬਾਰੇ

**GS1** ਇੱਕ ਸੰਸਾਰ-ਪੱਧਰੀ ਗ਼ੈਰ-ਮੁਨਾਫ਼ਾ ਸੰਸਥਾ ਹੈ ਜੋ ਸਪਲਾਈ-ਲੜੀ ਦੀ ਪਛਾਣ ਤੇ ਡਾਟਾ ਵਟਾਂਦਰੇ ਲਈ ਖੁੱਲ੍ਹੇ ਮਿਆਰ ਬਣਾਉਂਦੀ ਤੇ ਸੰਭਾਲਦੀ ਹੈ। ਇਸ ਦੇ ਮਿਆਰ ਪਰਚੂਨ, ਸਿਹਤ-ਸੰਭਾਲ, ਲੌਜਿਸਟਿਕਸ, ਖਾਣ-ਪੀਣ ਸੇਵਾਵਾਂ ਤੇ ਹੋਰ ਕਈ ਸਨਅਤਾਂ ਵਿੱਚ ਵਰਤੇ ਜਾਂਦੇ ਹਨ, ਜੋ ਖ਼ਪਤਕਾਰ ਪੈਕਿੰਗ ਉੱਤੇ ਲੱਗੇ ਵਸਤੂ ਬਾਰਕੋਡ ਤੋਂ ਲੈ ਕੇ ਦਵਾਈ ਦੀਆਂ ਖ਼ੁਰਾਕਾਂ ਦੀ ਸੀਰੀਅਲ-ਵਾਰ ਪੈੜ-ਨਿਸ਼ਾਨੀ ਤੱਕ ਸਭ ਕੁਝ ਢਕਦੇ ਹਨ।

ਇਹ ਬਿਲਡਰ ਜੋ ਕੁਝ ਵੀ ਲਾਗੂ ਕਰਦਾ ਹੈ, ਉਸ ਸਭ ਲਈ ਅਧਿਕਾਰਤ ਹਵਾਲਾ **GS1 General Specifications** ਹੈ — ਇੱਕੋ ਦਸਤਾਵੇਜ਼ ਜੋ ਇਹ ਸਭ ਪਰਿਭਾਸ਼ਿਤ ਕਰਦਾ ਹੈ:

- ਸਾਰੇ ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ (AI) ਕੋਡ, ਉਹਨਾਂ ਦੇ ਡਾਟਾ ਸਿਰਲੇਖ, ਰੂਪ ਤੇ ਤਸਦੀਕ ਨਿਯਮ
- AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਬਣਾਉਣ ਤੇ ਏਨਕੋਡ ਕਰਨ ਦੇ ਵਾਕ-ਬਣਤਰੀ ਨਿਯਮ
- ਬਾਰਕੋਡ ਸਿੰਬੋਲੋਜੀ ਦੀਆਂ ਲੋੜਾਂ ਤੇ AIM ਕੋਡ ID ਦੀਆਂ ਵੰਡਾਂ
- ਜਾਂਚ ਅੰਕ ਤੇ ਜਾਂਚ ਅੱਖਰ ਦੇ ਗਣਿਤ-ਢੰਗ
- ਦੋ-ਅੰਕੀ ਸਾਲ ਦਾ ਨਿਬੇੜਾ (ਸਰਕਦੀ-ਖਿੜਕੀ ਵਾਲਾ ਨਿਯਮ)
- ਡਾਟਾ ਮੈਟ੍ਰਿਕਸ, QR ਕੋਡ, GS1-128, GS1 DataBar ਤੇ ਹੋਰ ਵਾਹਕਾਂ ਦੇ ਵੇਰਵੇ

GS1 General Specifications ਹਰ ਸਾਲ ਸੋਧੇ ਜਾਂਦੇ ਹਨ। ਮੌਜੂਦਾ ਐਡੀਸ਼ਨ ਤੇ ਸਹਾਇਕ ਸਮੱਗਰੀ ਇੱਥੇ ਮਿਲਦੀ ਹੈ:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA GS1 General Specifications ਦਾ **ਰਿਲੀਜ਼ 26.0 (ਪ੍ਰਵਾਨਿਤ, ਜਨਵਰੀ 2026)** ਲਾਗੂ ਕਰਦਾ ਹੈ।

GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI ਇੱਕ ਸਾਥੀ ਮਿਆਰ, **GS1 Digital Link: URI Syntax**, ਹੇਠ ਆਉਂਦੇ ਹਨ, ਜੋ ਉਹ ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀਆਂ, ਕੁੰਜੀ-ਯੋਗਤਾ-ਸੂਚਕਾਂ ਦੀ ਤਰਤੀਬ, ਤੇ ਡਾਟਾ-ਗੁਣ ਏਨਕੋਡਿੰਗ ਪਰਿਭਾਸ਼ਿਤ ਕਰਦਾ ਹੈ ਜੋ ਬਿਲਡਰ ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਉਂਦੇ ਵੇਲੇ ਲਾਗੂ ਕਰਦਾ ਹੈ:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA GS1 Digital Link: URI Syntax ਮਿਆਰ ਦਾ **ਰਿਲੀਜ਼ 1.7.0 (ਪ੍ਰਵਾਨਿਤ, ਅਗਸਤ 2026)** ਲਾਗੂ ਕਰਦਾ ਹੈ।

ਇਸ ਦਸਤਾਵੇਜ਼ ਵਿੱਚ ਸਾਰੇ ਭਾਗ-ਹਵਾਲੇ GS1 General Specifications ਵੱਲ ਇਸ਼ਾਰਾ ਕਰਦੇ ਹਨ (ਜਿਵੇਂ "Table 7-5", "section 7.12"), ਸਿਵਾਏ ਡਿਜੀਟਲ ਲਿੰਕ ਭਾਗ-ਨੰਬਰਾਂ ਦੇ (ਜਿਵੇਂ "§4.9", "§4.12"), ਜੋ GS1 Digital Link: URI Syntax ਮਿਆਰ ਵੱਲ ਇਸ਼ਾਰਾ ਕਰਦੇ ਹਨ।

---

## ਝਟਪਟ ਸ਼ੁਰੂਆਤ

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

ਕੱਚੀਆਂ AI ਸਟ੍ਰਿੰਗਾਂ ਦੀ ਥਾਂ `GS1Constants_AICodes` ਸਥਿਰਾਂਕ ਵਰਤਣੇ ਬਿਹਤਰ ਹਨ (ਵੇਖੋ [ਪਾਰਸਰ ਗਾਈਡ ਦੀ ਅੰਤਿਕਾ A](GaiaParser-Punjabi.md#ਅਤਕ-a--ai-ਸਟਰਗ-ਸਥਰਕ)):

```java
import static tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes.*;

String es = GaiaBuilder.create()
        .ai(AI_01_GTIN, "09506000134352")
        .ai(AI_10_BATCH_LOT, "LOT-ABC")
        .buildElementString();
```

---

## ਇਹ ਕਿਵੇਂ ਕੰਮ ਕਰਦਾ ਹੈ

ਹਰ ਬਿਲਡ ਇੱਕੋ ਰਾਹ ਤੁਰਦਾ ਹੈ:

1. **ਜੋੜਨਾ** — AI/ਮੁੱਲ ਜੋੜੇ ਇੱਕ ਸੰਭਾਵੀ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ ਨਾਲੋ-ਨਾਲ ਜੋੜ ਦਿੱਤੇ ਜਾਂਦੇ ਹਨ। ਹਰ ਉਸ AI ਮਗਰੋਂ FNC1 ਟੋਲੀ-ਵਿਭਾਜਕ (`0x1D`) ਪਾਇਆ ਜਾਂਦਾ ਹੈ ਜਿਸ ਨੂੰ *ਵਿਭਾਜਕ ਦੀ ਲੋੜ ਹੋਵੇ* ਤੇ ਜੋ ਆਖ਼ਰੀ ਐਲੀਮੈਂਟ ਨਾ ਹੋਵੇ। ਪਹਿਲਾਂ ਤੋਂ ਤੈਅ ਲੰਬਾਈ ਵਾਲੇ AI (GTIN, ਮਿਤੀਆਂ, ਸਥਿਰ-ਲੰਬਾਈ ਵਾਲੇ ਮਾਪ) ਵਿਭਾਜਕ ਨਹੀਂ ਲੈਂਦੇ; ਬਾਕੀ ਸਾਰੇ ਲੈਂਦੇ ਹਨ। (ਅਣਪਛਾਤੇ AI ਇਸ ਕਦਮ ਤੱਕ ਪੁੱਜਦੇ ਹੀ ਨਹੀਂ — `ai(...)` ਉਹਨਾਂ ਨੂੰ ਓਥੇ ਹੀ ਰੱਦ ਕਰ ਦਿੰਦਾ ਹੈ; ਵੇਖੋ [ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਬਣਾਉਣਾ](#ਐਲਮਟ-ਸਟਰਗ-ਬਣਉਣ)।)
2. **ਤਸਦੀਕ** — ਸੰਭਾਵੀ ਸਟ੍ਰਿੰਗ `GaiaParser` ਰਾਹੀਂ `CONTENT` ਢੰਗ ਵਿੱਚ ਪਾਰਸ ਕੀਤੀ ਜਾਂਦੀ ਹੈ। ਹਰ ਮੁੱਲ ਆਪਣੇ AI ਦੇ ਰੂਪ ਤੇ ਜਾਂਚ ਅੰਕ ਉੱਤੇ ਪਰਖਿਆ ਜਾਂਦਾ ਹੈ, ਤੇ ਬਣਤਰੀ ਨਿਯਮ (ਲੋੜੀਂਦੇ/ਵਰਜਿਤ AI ਜੋੜੇ) ਲਾਗੂ ਕੀਤੇ ਜਾਂਦੇ ਹਨ। ਜੇ ਪਾਰਸ ਜਾਇਜ਼ ਨਾ ਹੋਵੇ, ਤਾਂ ਬਿਲਡ ਨਾਕਾਮ ਹੁੰਦਾ ਹੈ।
3. **ਵਿਖਾਉਣਾ** —
   - ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਲਈ, ਤਸਦੀਕ ਹੋਈ ਵਸਤੂ ਦਾ `toElementString()` ਮੋੜਿਆ ਜਾਂਦਾ ਹੈ।
   - ਡਿਜੀਟਲ ਲਿੰਕ ਲਈ, ਹਰ ਐਲੀਮੈਂਟ ਨੂੰ ਉਸ ਦੀ DL ਭੂਮਿਕਾ ਦਿੱਤੀ ਜਾਂਦੀ ਹੈ (ਮੁੱਖ ਕੁੰਜੀ, ਕੁੰਜੀ ਯੋਗਤਾ-ਸੂਚਕ, ਜਾਂ ਡਾਟਾ ਗੁਣ), ਕੁੰਜੀ-ਯੋਗਤਾ-ਸੂਚਕਾਂ ਦੀ ਲੜੀ ਤਸਦੀਕ ਹੁੰਦੀ ਹੈ, URI ਕੱਢਿਆ ਜਾਂਦਾ ਹੈ, ਤੇ ਕੱਢੇ ਹੋਏ URI ਨੂੰ **ਮੁੜ ਪਾਰਸ ਕਰ ਕੇ ਪੱਕਾ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਕਿ ਉਹ ਜਾਇਜ਼ ਡਿਜੀਟਲ ਲਿੰਕ ਵਜੋਂ ਗੇੜਾ ਪੂਰਾ ਕਰਦਾ ਹੈ** — ਇਹ ਸਟ੍ਰਿੰਗ ਜੋੜਨ ਤੇ ਪ੍ਰਤੀਸ਼ਤ-ਏਨਕੋਡਿੰਗ ਵਾਲੇ ਕਦਮ ਉੱਤੇ ਬਚਾਅ ਵਾਲੀ ਪਰਖ ਹੈ। ਜੇ ਗੇੜਾ ਪੂਰਾ ਨਾ ਹੋਵੇ, ਤਾਂ `GaiaBuilderException` ਸੁੱਟਿਆ ਜਾਂਦਾ ਹੈ।

ਇਹ `DLSyntaxParser` ਵਾਲੇ ਮੁੜ-ਉਸਾਰੀ ਤਰਕ ਵਾਂਗ ਹੀ ਹੈ, ਸੋ ਵਿਭਾਜਕ ਦੀ ਥਾਂ ਤੇ ਤਸਦੀਕ, ਦੋਵੇਂ ਓਹੀ ਹਨ ਜਿਹਨਾਂ ਦੀ ਪਾਰਸਰ ਆਸ ਰੱਖਦਾ ਹੈ।

---

## ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਬਣਾਉਣਾ

```java
String es = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .buildElementString();
// 0109506000134352
```

- **AI** ਓਥੇ ਹੀ ਤਸਦੀਕ ਹੁੰਦਾ ਹੈ: ਜੇ ਉਹ ਕੋਈ ਪਛਾਣਿਆ GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ ਨਾ ਹੋਵੇ ਤਾਂ `ai(...)` `IllegalArgumentException` ਸੁੱਟਦਾ ਹੈ। (ਬਿਲਡਰ ਪਾਰਸ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ AI ਤੇ ਮੁੱਲ ਜੋੜ ਦਿੰਦਾ ਹੈ, ਸੋ `"99999"` ਵਰਗਾ ਅਣਪਛਾਤਾ ਜਾਂ ਵਾਧੂ ਲੰਮਾ AI ਇੱਥੇ ਹੀ ਫੜਨਾ ਪਵੇਗਾ — ਨਹੀਂ ਤਾਂ ਉਹ ਚੁੱਪ-ਚਾਪ ਕਿਸੇ ਹੋਰ AI ਵਜੋਂ ਮੁੜ-ਟੋਕਨਾਈਜ਼ ਹੋ ਜਾਵੇਗਾ।) **ਮੁੱਲ** ਬਾਅਦ ਵਿੱਚ, ਬਿਲਡ ਵੇਲੇ ਤਸਦੀਕ ਹੁੰਦਾ ਹੈ।
- ਮੁੱਲ **ਪੂਰੇ** ਹੋਣੇ ਚਾਹੀਦੇ ਹਨ, ਹਰ ਜਾਂਚ ਅੰਕ ਸਮੇਤ। ਬਿਲਡਰ ਤੁਹਾਡੇ ਲਈ ਜਾਂਚ ਅੰਕ ਨਾ ਗਿਣਦਾ ਹੈ ਨਾ ਜੋੜਦਾ ਹੈ — ਵੇਖੋ [ਜਾਂਚ ਅੰਕ](#ਜਚ-ਅਕ)।
- AI ਉਸੇ ਤਰਤੀਬ ਵਿੱਚ ਕੱਢੇ ਜਾਂਦੇ ਹਨ ਜਿਸ ਵਿੱਚ ਤੁਸੀਂ ਜੋੜਦੇ ਹੋ। ਜਿੱਥੇ GS1 ਵਾਕ-ਬਣਤਰ ਨੂੰ ਲੋੜ ਹੋਵੇ ਓਥੇ FNC1 ਵਿਭਾਜਕ ਬਿਲਡਰ ਆਪ ਪਾ ਦਿੰਦਾ ਹੈ; ਤੁਸੀਂ ਆਪ ਵਿਭਾਜਕ ਨਾ ਪਾਓ।
- **ਬਿਲਕੁਲ ਕੋਈ AI ਦਿੱਤੇ ਬਿਨਾਂ** ਬਿਲਡ ਕਰਨ ਉੱਤੇ ਖ਼ਾਲੀ `getErrors()` ਸੂਚੀ ਸਮੇਤ `GaiaBuilderException("No AIs supplied")` ਸੁੱਟਿਆ ਜਾਂਦਾ ਹੈ — ਇਹੀ ਇੱਕੋ ਨਾਕਾਮੀ ਹੈ ਜਿਸ ਨਾਲ ਕੋਈ `GaiaError` ਨਹੀਂ ਆਉਂਦੀ।
- ਜਿਸ AI ਦਾ ਮੁੱਲ ਆਪਣੇ ਰੂਪ ਜਾਂ ਜਾਂਚ-ਅੰਕ ਨਿਯਮ ਉੱਤੇ ਪੂਰਾ ਨਾ ਉੱਤਰੇ, ਉਹ ਬਿਲਡ ਨਾਕਾਮ ਕਰ ਦਿੰਦਾ ਹੈ।

### ਗੁਣ ਵਾਲੇ AI ਨੂੰ ਆਪਣੀ ਪਛਾਣ ਕੁੰਜੀ ਚਾਹੀਦੀ ਹੈ

ਬਹੁਤੇ AI *ਗੁਣ* ਹਨ ਜਿਹਨਾਂ ਦੇ ਨਾਲ GS1 General Specifications ਕਿਸੇ ਪਛਾਣ ਕੁੰਜੀ ਦਾ ਹੋਣਾ ਲਾਜ਼ਮੀ ਕਰਦੇ ਹਨ, ਤੇ ਬਿਲਡਰ ਇਹ ਲਾਗੂ ਕਰਦਾ ਹੈ — ਇਹ ਪੂਰੇ ਵਾਕ-ਬਣਤਰੀ ਪੜਾਅ ਰਾਹੀਂ ਤਸਦੀਕ ਕਰਦਾ ਹੈ, ਤੇ ਇਸ ਤੋਂ ਬਾਹਰ ਨਿਕਲਣ ਦਾ ਕੋਈ ਰਾਹ ਨਹੀਂ। ਇਕੱਲਾ ਬੈਚ/ਲਾਟ ਜਾਂ ਸੀਰੀਅਲ ਜਾਇਜ਼ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ **ਨਹੀਂ**:

```java
GaiaBuilder.create().ai("10", "LOT-ABC").buildElementString();
// GaiaBuilderException: Cannot build a well-formed element string: 1 validation error(s)
//   [GE-S005] AI (10) requires 01 OR 02 OR 03 OR 8006 OR 8026 …

GaiaBuilder.create().ai("01", "09506000134352").ai("10", "LOT-ABC").buildElementString();
// 010950600013435210LOT-ABC        — the GTIN satisfies the requirement
```

ਪਛਾਣ ਕੁੰਜੀਆਂ (GTIN `01`, SSCC `00`, GLN `414`, …) ਤੇ ਕੰਪਨੀ ਦੇ ਅੰਦਰੂਨੀ AI (`90`–`99`) ਪੂਰੇ ਹੱਕ ਨਾਲ ਇਕੱਲੇ ਖੜ੍ਹ ਸਕਦੇ ਹਨ। ਬਾਕੀ ਹਰ ਇੱਕ ਨੂੰ ਆਪਣਾ ਸਾਥੀ ਚਾਹੀਦਾ ਹੈ।

> `GaiaParser` ਨੂੰ `ParseConfig.skipRequiresCheck(true)` ਨਾਲ ਇਹ ਪਰਖ ਛੱਡਣ ਲਈ ਕਿਹਾ ਜਾ ਸਕਦਾ ਹੈ; `GaiaBuilder` ਜਾਣ-ਬੁੱਝ ਕੇ ਇਸ ਦਾ ਕੋਈ ਬਰਾਬਰ ਦਾ ਰਾਹ ਨਹੀਂ ਦਿੰਦਾ — ਇਹ ਮਿਆਰ-ਅਨੁਸਾਰੀ ਨਤੀਜਾ ਦੇਣ ਲਈ ਹੀ ਬਣਿਆ ਹੈ। ਜਾਣ-ਬੁੱਝ ਕੇ ਅਧੂਰੀ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਬਣਾਉਣ ਲਈ ਉਸ ਨੂੰ ਆਪ ਜੋੜੋ ਤੇ ਇਹ ਪਰਖ ਬੰਦ ਕਰ ਕੇ ਪਾਰਸ ਕਰੋ।

---

## ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਉਣਾ

```java
// Canonical form (https://id.gs1.org)
String dl = GaiaBuilder.create()
        .ai("01", "09506000134352")
        .ai("21", "SERIAL123")
        .buildDigitalLinkUri();
// https://id.gs1.org/01/09506000134352/21/SERIAL123
```

ਜਾਇਜ਼ ਡਿਜੀਟਲ ਲਿੰਕ ਲਈ ਠੀਕ ਇੱਕੋ **ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀ** ਚਾਹੀਦੀ ਹੈ (ਜਿਵੇਂ GTIN `01`, GLN `414`, SSCC `00`)। ਬਿਲਡਰ ਹਰ ਦਿੱਤੇ AI ਦੀ ਸ਼੍ਰੇਣੀ ਤੈਅ ਕਰਦਾ ਹੈ:

| ਭੂਮਿਕਾ | ਕਿਵੇਂ ਵਿਖਾਇਆ ਜਾਂਦਾ ਹੈ | ਮਿਸਾਲ |
|------|-------------|---------|
| ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀ | ਡੋਮੇਨ/ਅਗੇਤਰ ਮਗਰੋਂ ਰਾਹ-ਖੰਡ | `/01/09506000134352` |
| ਕੁੰਜੀ ਯੋਗਤਾ-ਸੂਚਕ (CPV `22`, ਬੈਚ `10`, ਸੀਰੀਅਲ `21`, …) | ਅਗਲੇ ਰਾਹ-ਖੰਡ, **ਮਿਆਰੀ §4.9 ਤਰਤੀਬ ਵਿੱਚ** (ਉਸ ਤਰਤੀਬ ਵਿੱਚ ਨਹੀਂ ਜਿਸ ਵਿੱਚ ਤੁਸੀਂ ਜੋੜੇ) | `/10/LOT-ABC` |
| ਡਾਟਾ ਗੁਣ (ਬਾਕੀ ਸਭ ਕੁਝ) | ਸਵਾਲ-ਪੈਰਾਮੀਟਰ, **AI ਕੁੰਜੀ ਮੁਤਾਬਕ ਸ਼ਬਦ-ਕ੍ਰਮ ਵਿੱਚ ਤਰਤੀਬੇ** (§4.12) | `?17=271231` |

ਕਿਉਂਕਿ ਯੋਗਤਾ-ਸੂਚਕ ਕੱਢਣ ਵੇਲੇ ਮੁੜ ਤਰਤੀਬੇ ਜਾਂਦੇ ਹਨ, ਉਹਨਾਂ ਨੂੰ ਬੇ-ਤਰਤੀਬ ਦੇਣਾ ਕੋਈ ਦਿੱਕਤ ਨਹੀਂ — `ai("10", …)` ਤੋਂ ਪਹਿਲਾਂ `ai("21", …)` ਦੇਣ ਉੱਤੇ ਵੀ `/10/LOT/21/SER` ਹੀ ਬਣਦਾ ਹੈ। ਸਿਰਫ਼ *ਸਮੂਹ* ਦਾ ਉਸ ਮੁੱਖ ਕੁੰਜੀ ਲਈ ਮੰਨਜ਼ੂਰ ਹੋਣਾ ਲਾਜ਼ਮੀ ਹੈ।

ਰਾਹ ਤੇ ਸਵਾਲ, ਦੋਹਾਂ ਵਿੱਚਲੇ ਮੁੱਲ ਪ੍ਰਤੀਸ਼ਤ-ਏਨਕੋਡ ਕੀਤੇ ਜਾਂਦੇ ਹਨ।

ਬਿਲਡ ਇਹਨਾਂ ਹਾਲਤਾਂ ਵਿੱਚ **ਨਾਕਾਮ** ਹੁੰਦਾ ਹੈ (`GaiaBuilderException` ਸੁੱਟਦਾ ਹੈ, ਜਾਂ ਨਾਕਾਮ `BuildResult` ਮੋੜਦਾ ਹੈ):

- AI ਵਿੱਚ ਕੋਈ ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀ **ਹੈ ਹੀ ਨਹੀਂ**;
- **ਇੱਕ ਤੋਂ ਵੱਧ** ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀਆਂ ਹਨ;
- ਕੋਈ AI ਡਿਜੀਟਲ ਲਿੰਕ ਵਿੱਚ **ਵਰਜਿਤ** ਹੈ (`03`, `8014`);
- ਚੁਣੀ ਮੁੱਖ ਕੁੰਜੀ ਲਈ **ਕੁੰਜੀ-ਯੋਗਤਾ-ਸੂਚਕ ਲੜੀ** ਨਾਜਾਇਜ਼ ਹੈ (ਜਿਵੇਂ ਕੋਈ ਅਜਿਹਾ ਯੋਗਤਾ-ਸੂਚਕ ਜੋ ਉਸ ਕੁੰਜੀ ਨਾਲ ਸੰਬੰਧਿਤ ਨਹੀਂ, ਜਾਂ ਯੋਗਤਾ-ਸੂਚਕ ਆਪਣੀ ਮੰਨਜ਼ੂਰ ਤਰਤੀਬ ਤੋਂ ਬਾਹਰ)।

---

## BuilderDigitalLinkConfig

ਸਕੀਮ, ਡੋਮੇਨ, ਰਾਹ-ਅਗੇਤਰ, ਵਾਧੂ ਸਵਾਲ-ਪੈਰਾਮੀਟਰ ਤੇ ਟੁਕੜਾ ਕਾਬੂ ਕਰਨ ਲਈ `BuilderDigitalLinkConfig` ਦਿਓ:

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

| ਬਿਲਡਰ ਵਿਧੀ | ਮਕਸਦ | ਮੂਲ |
|----------------|---------|---------|
| `scheme(String)` | URI ਸਕੀਮ; `http` ਜਾਂ `https` ਹੋਣੀ ਲਾਜ਼ਮੀ | `https` |
| `domain(String)` | ਅਥਾਰਟੀ — ਮੇਜ਼ਬਾਨ ਜਾਂ `host:port` | `id.gs1.org` |
| `pathPrefix(String)` | ਪਹਿਲੀ ਮੁੱਖ ਕੁੰਜੀ ਤੋਂ ਪਹਿਲਾਂ ਦੇ ਰਾਹ-ਖੰਡ; ਅੱਗੇ/ਪਿੱਛੇ ਵਾਲੀਆਂ ਸਲੈਸ਼ਾਂ ਸਧਾਰਨ ਕੀਤੀਆਂ ਜਾਂਦੀਆਂ ਹਨ | *(ਕੋਈ ਨਹੀਂ)* |
| `baseUrl(String)` | ਸੌਖ ਲਈ, ਜੋ URL ਨੂੰ `scheme` + `domain` + `pathPrefix` ਵਿੱਚ ਵੰਡ ਦਿੰਦੀ ਹੈ | — |
| `addQueryParam(String, String)` | ਵਾਧੂ ਸਵਾਲ-ਪੈਰਾਮੀਟਰ, ਜੋ AI ਡਾਟਾ ਗੁਣਾਂ ਤੋਂ **ਬਾਅਦ**, ਜੋੜਨ ਦੀ ਤਰਤੀਬ ਵਿੱਚ ਲੱਗਦਾ ਹੈ; ਪ੍ਰਤੀਸ਼ਤ-ਏਨਕੋਡ ਕੀਤਾ | — |
| `fragment(String)` | URL ਟੁਕੜਾ (ਅੱਗੇ ਲੱਗਣ ਵਾਲੇ `#` ਤੋਂ ਬਿਨਾਂ); ਪ੍ਰਤੀਸ਼ਤ-ਏਨਕੋਡ ਕੀਤਾ | *(ਕੋਈ ਨਹੀਂ)* |

`build()` ਸੈਟਿੰਗ ਓਥੇ ਹੀ ਤਸਦੀਕ ਕਰਦਾ ਹੈ: ਗ਼ੈਰ-`http(s)` ਸਕੀਮ ਜਾਂ ਖ਼ਾਲੀ ਡੋਮੇਨ `IllegalArgumentException` ਸੁੱਟਦੀ ਹੈ।

- `BuilderDigitalLinkConfig.canonical()` (ਉਪਨਾਮ `defaultConfig()`) ਬਿਨਾਂ ਕਿਸੇ ਵਾਧੂ ਚੀਜ਼ ਦੇ `https://id.gs1.org` ਵਾਲਾ ਮੂਲ ਹੈ — ਠੀਕ ਓਹੀ ਜੋ ਬਿਨਾਂ ਦਲੀਲ ਵਾਲਾ `buildDigitalLinkUri()` ਵਰਤਦਾ ਹੈ, ਤੇ ਜੋ `GS1AIObject.getCanonicalDigitalLink()` ਬਣਾਉਂਦਾ ਹੈ।
- `baseUrl("http://id.example.org:8080/r")` → ਸਕੀਮ `http`, ਡੋਮੇਨ `id.example.org:8080`, ਰਾਹ-ਅਗੇਤਰ `/r`।
- ਵਾਧੂ ਸਵਾਲ-ਪੈਰਾਮੀਟਰ ਹਮੇਸ਼ਾ AI ਤੋਂ ਬਣੇ ਗੁਣਾਂ ਮਗਰੋਂ ਆਉਂਦੇ ਹਨ, ਸੋ ਮਿਆਰੀ AI ਤਰਤੀਬ (§4.12) ਬਚੀ ਰਹਿੰਦੀ ਹੈ।

`BuilderDigitalLinkConfig` ਅਟੱਲ ਹੈ; ਇੱਕੋ ਨਮੂਨਾ ਬੇਝਿਜਕ ਮੁੜ-ਮੁੜ ਵਰਤੋ।

---

## ਤਸਦੀਕ ਤੇ ਗ਼ਲਤੀਆਂ

### ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲੀਆਂ ਬਿਲਡ ਵਿਧੀਆਂ

ਜਦੋਂ AI ਤੋਂ ਠੀਕ-ਬਣਿਆ ਨਤੀਜਾ ਨਾ ਬਣ ਸਕੇ ਤਾਂ `buildElementString()`, `buildDigitalLinkUri()`, ਤੇ `buildDigitalLinkUri(BuilderDigitalLinkConfig)` **`GaiaBuilderException`** (ਇੱਕ ਅਣ-ਜਾਂਚਿਆ `RuntimeException`) ਸੁੱਟਦੀਆਂ ਹਨ:

```java
try {
    String es = GaiaBuilder.create().ai("01", "09506000134350").buildElementString();
} catch (GaiaBuilderException ex) {
    System.err.println(ex.getMessage());     // human-readable summary
    ex.getErrors().forEach(System.err::println);  // underlying GaiaError list
}
```

- **ਸਮੱਗਰੀ** ਵਾਲੀਆਂ ਨਾਕਾਮੀਆਂ (ਗ਼ਲਤ ਜਾਂਚ ਅੰਕ, ਰੂਪ ਦਾ ਨਾ ਮਿਲਣਾ, ਗ਼ੈਰ-ਹਾਜ਼ਰ/ਵਰਜਿਤ AI) ਲਈ `getErrors()` ਪਾਰਸਰ ਦੀਆਂ `GaiaError` ਲੈ ਕੇ ਆਉਂਦਾ ਹੈ — ਓਹੀ ਵਸਤਾਂ ਜੋ [ਪਾਰਸਰ ਗਾਈਡ ਵਿੱਚ ਦਰਜ ਹਨ](GaiaParser-Punjabi.md#gaiaerror)।
- **ਡਿਜੀਟਲ ਲਿੰਕ ਬਣਤਰੀ** ਨਾਕਾਮੀਆਂ (ਕੋਈ ਮੁੱਖ ਕੁੰਜੀ ਨਹੀਂ, ਇੱਕ ਤੋਂ ਵੱਧ ਮੁੱਖ ਕੁੰਜੀਆਂ, ਵਰਜਿਤ AI, ਨਾਜਾਇਜ਼ ਕੁੰਜੀ-ਯੋਗਤਾ-ਸੂਚਕ ਲੜੀ) ਲਈ `getErrors()` ਬਿਲਡਰ ਦੀ ਭਾਸ਼ਾ ਵਿੱਚ ਸਥਾਨਕ ਕੀਤੀ ਇੱਕੋ `GaiaError` ਲੈ ਕੇ ਆਉਂਦਾ ਹੈ (ਕੋਡ `GE-L008`, `GE-L012`, `GE-L013`, ਜਾਂ `GE-L014`)।

### ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲੀਆਂ tryBuild\* ਵਿਧੀਆਂ

ਜਦੋਂ ਇਨਪੁਟ ਵਰਤੋਂਕਾਰ ਵੱਲੋਂ ਆਵੇ ਤੇ ਨਾਕਾਮੀ ਇੱਕ ਆਸ ਕੀਤਾ, ਸੰਭਾਲਣ-ਯੋਗ ਨਤੀਜਾ ਹੋਵੇ, ਤਾਂ ਅਪਵਾਦਾਂ ਨਾਲ ਵਹਾਅ ਕਾਬੂ ਕਰਨ ਦੀ ਥਾਂ `tryBuild*` ਰੂਪ ਵਰਤੋ। ਇਹ ਅਪਵਾਦ ਸੁੱਟਣ ਦੀ ਥਾਂ [`BuildResult`](#buildresult) ਮੋੜਦੀਆਂ ਹਨ:

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

| ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲੀ | ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲੀ |
|----------|--------------|
| `buildElementString()` | `tryBuildElementString()` |
| `buildDigitalLinkUri()` | `tryBuildDigitalLinkUri()` |
| `buildDigitalLinkUri(BuilderDigitalLinkConfig)` | `tryBuildDigitalLinkUri(BuilderDigitalLinkConfig)` |

ਹਰ `tryBuild*` ਵਿਧੀ ਦਾ ਤਸਦੀਕ ਵਾਲਾ ਧੁਰਾ ਆਪਣੀ ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲੀ ਜੌੜੀ ਵਾਲਾ ਹੀ ਹੈ; ਫ਼ਰਕ ਸਿਰਫ਼ ਨਾਕਾਮੀ ਦੀ ਹੱਦ ਦਾ ਹੈ।

### ਗ਼ਲਤੀ ਸੁਨੇਹਿਆਂ ਦੀ ਭਾਸ਼ਾ

ਸਮੱਗਰੀ-ਤਸਦੀਕ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਸਥਾਨਕ ਕੀਤੀ ਗ਼ਲਤੀ ਸੂਚੀ ਵਿੱਚੋਂ ਲਈਆਂ ਜਾਂਦੀਆਂ ਹਨ। `GaiaBuilderException.getErrors()` / `BuildResult.getErrors()` ਨਾਲ ਆਉਣ ਵਾਲੇ `GaiaError` ਸੁਨੇਹਿਆਂ ਦੀ ਭਾਸ਼ਾ ਚੁਣਨ ਲਈ `language(...)` ਸੱਦੋ; ਮੂਲ ਭਾਸ਼ਾ ਅੰਗਰੇਜ਼ੀ ਹੈ:

```java
BuildResult r = GaiaBuilder.create()
        .language(GaiaConstants.Language.FRENCH)
        .ai("01", userValue)
        .tryBuildElementString();
// r.getErrors() messages are in French
```

ਇਹ ਓਹੀ `GaiaConstants.Language` ਸੈਟਿੰਗ ਹੈ ਜੋ `GaiaParser` `ParseConfig` ਰਾਹੀਂ ਲੈਂਦਾ ਹੈ, ਸੋ ਬਿਲਡਰ ਤੇ ਪਾਰਸਰ ਇੱਕੋ ਜਿਹਾ ਸਥਾਨਕ ਕਰਦੇ ਹਨ।

**ਸਮੱਗਰੀ** ਵਾਲੇ `GaiaError` ਸੁਨੇਹੇ ਤੇ **ਡਿਜੀਟਲ ਲਿੰਕ ਬਣਤਰੀ** ਨਾਕਾਮੀਆਂ (ਕੋਈ ਮੁੱਖ ਕੁੰਜੀ ਨਹੀਂ, ਇੱਕ ਤੋਂ ਵੱਧ ਮੁੱਖ ਕੁੰਜੀਆਂ, ਵਰਜਿਤ AI, ਨਾਜਾਇਜ਼ ਕੁੰਜੀ-ਯੋਗਤਾ-ਸੂਚਕ ਲੜੀ), ਦੋਵੇਂ ਸਾਂਝੀ ਗ਼ਲਤੀ ਸੂਚੀ ਰਾਹੀਂ ਸਥਾਨਕ ਕੀਤੇ ਜਾਂਦੇ ਹਨ — ਪਿਛਲੀਆਂ ਲਈ ਕੋਡ `GE-L008`, `GE-L012`, `GE-L013`, ਤੇ `GE-L014` ਵਰਤੇ ਜਾਂਦੇ ਹਨ।

### BuildResult

`BuildResult` (ਪੈਕੇਜ `tools.pantheum.gaia.result` ਵਿੱਚ) ਇੱਕ ਅਟੱਲ ਮੁੱਲ-ਕਿਸਮ ਹੈ ਜੋ `tryBuild*` ਸੱਦੇ ਦਾ ਨਤੀਜਾ ਦੱਸਦੀ ਹੈ:

| ਵਿਧੀ | ਕਾਮਯਾਬੀ ਉੱਤੇ | ਨਾਕਾਮੀ ਉੱਤੇ |
|--------|------------|------------|
| `isSuccess()` | `true` | `false` |
| `getValue()` | ਬਣਾਈ ਹੋਈ ਸਟ੍ਰਿੰਗ | `null` |
| `getMessage()` | `null` | ਨਾਕਾਮੀ ਦਾ ਵੇਰਵਾ |
| `getErrors()` | ਖ਼ਾਲੀ ਸੂਚੀ | ਤਸਦੀਕ ਗ਼ਲਤੀਆਂ (`GaiaBuilderException.getErrors()` ਵਾਂਗ ਹੀ) |

---

## ਜਾਂਚ ਅੰਕ

ਬਿਲਡਰ ਜਾਂਚ ਅੰਕ ਤਸਦੀਕ ਤਾਂ ਕਰਦਾ ਹੈ ਪਰ ਉਹਨਾਂ ਨੂੰ ਗਿਣਦਾ **ਨਹੀਂ** — ਮੁੱਲਾਂ ਵਿੱਚ ਆਪਣਾ ਜਾਂਚ ਅੰਕ ਪਹਿਲਾਂ ਹੀ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ। ਇੱਕ ਗਿਣਨ ਲਈ `GS1Utils.calculateCheckDigit` ਵਰਤੋ:

```java
import tools.pantheum.gaia.gs1.util.GS1Utils;

int cd = GS1Utils.calculateCheckDigit("0950600013435");  // → 2
String gtin = "0950600013435" + cd;                      // 09506000134352
String es = GaiaBuilder.create().ai("01", gtin).buildElementString();
```

`calculateCheckDigit(String)` ਦਿੱਤੇ ਅੰਕਾਂ ਉੱਤੇ ਮਿਆਰੀ GS1 ਮਾਡਿਊਲੋ-10 ਗਣਿਤ-ਢੰਗ ਲਾਉਂਦਾ ਹੈ ਤੇ `0–9` ਵਿੱਚੋਂ ਜਾਂਚ ਅੰਕ ਮੋੜਦਾ ਹੈ, ਜਾਂ ਜੇ ਇਨਪੁਟ null, ਖ਼ਾਲੀ ਜਾਂ ਗ਼ੈਰ-ਸੰਖਿਅਕ ਹੋਵੇ ਤਾਂ `-1`।

---

## ਥ੍ਰੈੱਡ ਸੁਰੱਖਿਆ

`GaiaBuilder` ਥ੍ਰੈੱਡ-ਸੁਰੱਖਿਅਤ **ਨਹੀਂ** ਹੈ ਤੇ ਇੱਕੋ ਵਾਰ ਵਰਤਣ ਲਈ ਹੈ: `create()` ਸੱਦੋ, AI ਜੋੜੋ, ਇੱਕੋ ਵਾਰ ਬਿਲਡ ਕਰੋ। ਹਰ ਨਤੀਜੇ ਲਈ ਨਵਾਂ ਬਿਲਡਰ ਬਣਾਓ; ਇੱਕੋ ਨੂੰ ਕਈ ਥ੍ਰੈੱਡਾਂ ਵਿੱਚ ਸਾਂਝਾ ਨਾ ਕਰੋ।

`BuilderDigitalLinkConfig` (ਤੇ ਇਸ ਦੇ `BuildResult` ਨਤੀਜੇ) ਅਟੱਲ ਹਨ ਤੇ ਬੇਝਿਜਕ ਸਾਂਝੇ ਕੀਤੇ ਜਾ ਸਕਦੇ ਹਨ — ਸ਼ੁਰੂ ਵਿੱਚ ਇੱਕੋ ਵਾਰ ਸੈਟਿੰਗ ਬਣਾਓ ਤੇ ਕਈ ਬਿਲਡਰਾਂ ਵਿੱਚ ਮੁੜ-ਮੁੜ ਵਰਤੋ।

---

## API ਹਵਾਲਾ

### `GaiaBuilder`

| ਵਿਧੀ | ਵੇਰਵਾ |
|--------|-------------|
| `static GaiaBuilder create()` | ਨਵਾਂ, ਖ਼ਾਲੀ ਬਿਲਡਰ ਸ਼ੁਰੂ ਕਰਦੀ ਹੈ। |
| `GaiaBuilder ai(String ai, String value)` | ਇੱਕ AI ਤੇ ਉਸ ਦਾ ਪੂਰਾ ਮੁੱਲ ਜੋੜਦੀ ਹੈ। ਜੇ ਕੋਈ ਵੀ `null` ਹੋਵੇ, ਜਾਂ `ai` ਕੋਈ ਪਛਾਣਿਆ GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ ਨਾ ਹੋਵੇ, ਤਾਂ `IllegalArgumentException` ਸੁੱਟਦੀ ਹੈ। |
| `GaiaBuilder language(GaiaConstants.Language language)` | ਸਮੱਗਰੀ-ਤਸਦੀਕ ਗ਼ਲਤੀ ਸੁਨੇਹਿਆਂ ਦੀ ਭਾਸ਼ਾ ਸੈੱਟ ਕਰਦੀ ਹੈ (ਮੂਲ ਅੰਗਰੇਜ਼ੀ)। `null` ਅਣਗੌਲਿਆ ਜਾਂਦਾ ਹੈ। |
| `String buildElementString()` | GS1 ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਬਣਾਉਂਦੀ ਹੈ। ਨਾਕਾਮੀ ਉੱਤੇ `GaiaBuilderException` ਸੁੱਟਦੀ ਹੈ। |
| `String buildDigitalLinkUri()` | ਮਿਆਰੀ ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਉਂਦੀ ਹੈ। ਨਾਕਾਮੀ ਉੱਤੇ `GaiaBuilderException` ਸੁੱਟਦੀ ਹੈ। |
| `String buildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` ਹੇਠ ਡਿਜੀਟਲ ਲਿੰਕ URI ਬਣਾਉਂਦੀ ਹੈ। ਨਾਕਾਮੀ ਉੱਤੇ `GaiaBuilderException` ਸੁੱਟਦੀ ਹੈ। |
| `BuildResult tryBuildElementString()` | ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲਾ ਐਲੀਮੈਂਟ-ਸਟ੍ਰਿੰਗ ਬਿਲਡ। |
| `BuildResult tryBuildDigitalLinkUri()` | ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲਾ ਮਿਆਰੀ ਡਿਜੀਟਲ ਲਿੰਕ ਬਿਲਡ। |
| `BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config)` | `config` ਹੇਠ ਅਪਵਾਦ ਨਾ ਸੁੱਟਣ ਵਾਲਾ ਡਿਜੀਟਲ ਲਿੰਕ ਬਿਲਡ। |

### `BuilderDigitalLinkConfig`

| ਮੈਂਬਰ | ਵੇਰਵਾ |
|--------|-------------|
| `static BuilderDigitalLinkConfig canonical()` / `defaultConfig()` | `https://id.gs1.org` ਵਾਲਾ ਮੂਲ। |
| `static Builder builder()` | ਨਵਾਂ ਸੈਟਿੰਗ ਬਿਲਡਰ। |
| `getScheme()` / `getDomain()` / `getPathPrefix()` | ਹੱਲ ਕੀਤੀ ਸਕੀਮ, ਅਥਾਰਟੀ, ਤੇ ਰਾਹ-ਅਗੇਤਰ। |
| `getExtraQueryParams()` | ਵਾਧੂ ਸਵਾਲ-ਪੈਰਾਮੀਟਰ, ਜੋੜਨ ਦੀ ਤਰਤੀਬ ਵਿੱਚ। |
| `getFragment()` | ਟੁਕੜਾ, ਜਾਂ `null`। |

### `GaiaBuilderException`

| ਮੈਂਬਰ | ਵੇਰਵਾ |
|--------|-------------|
| `getErrors()` | ਉਹ `GaiaError` ਜਿਹਨਾਂ ਕਰ ਕੇ ਨਾਕਾਮੀ ਹੋਈ — ਸਮੱਗਰੀ ਨਾਕਾਮੀ ਲਈ ਪਾਰਸਰ ਦੀਆਂ ਗ਼ਲਤੀਆਂ, ਜਾਂ ਇੱਕੋ ਡਿਜੀਟਲ ਲਿੰਕ ਬਣਤਰੀ ਗ਼ਲਤੀ (`GE-L008`/`GE-L012`/`GE-L013`/`GE-L014`)। ਕਦੇ `null` ਨਹੀਂ। |

### `BuildResult`

| ਮੈਂਬਰ | ਵੇਰਵਾ |
|--------|-------------|
| `isSuccess()` | ਬਿਲਡ ਕਾਮਯਾਬ ਹੋਇਆ ਜਾਂ ਨਹੀਂ। |
| `getValue()` | ਕਾਮਯਾਬੀ ਉੱਤੇ ਬਣਾਇਆ ਨਤੀਜਾ; ਨਾਕਾਮੀ ਉੱਤੇ `null`। |
| `getMessage()` | ਨਾਕਾਮੀ ਉੱਤੇ ਨਾਕਾਮੀ ਦਾ ਵੇਰਵਾ; ਕਾਮਯਾਬੀ ਉੱਤੇ `null`। |
| `getErrors()` | ਨਾਕਾਮੀ ਉੱਤੇ ਤਸਦੀਕ ਗ਼ਲਤੀਆਂ; ਕਾਮਯਾਬੀ ਉੱਤੇ ਖ਼ਾਲੀ। ਕਦੇ `null` ਨਹੀਂ। |
| `getTiming()` | ਬਿਲਡ ਦਾ `ProcessingTiming` (ਸ਼ੁਰੂ ਹੋਣ ਦਾ ਸਮਾਂ, ਕਾਰਵਾਈ ਦੀ ਮਿਆਦ), ਜਾਂ `null`। |

---

ਇਹ ਵੀ ਵੇਖੋ: ਪਾਰਸਿੰਗ ਵਾਲੇ ਪਾਸੇ, AI ਐਲੀਮੈਂਟ ਮਾਡਲ, ਗ਼ਲਤੀਆਂ ਦੇ ਹਵਾਲੇ, ਤੇ AI/ਵਿਆਖਿਆ ਸਥਿਰਾਂਕਾਂ ਦੀਆਂ ਅੰਤਿਕਾਵਾਂ ਲਈ **[GaiaParser — ਡਿਵੈਲਪਰ ਗਾਈਡ](GaiaParser-Punjabi.md)**।
