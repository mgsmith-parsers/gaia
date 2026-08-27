# GAIA (GS1 Application Identifiers Analyser) — ਡਿਵੈਲਪਰ ਗਾਈਡ

## ਤਤਕਰਾ

1. [ਸੰਖੇਪ ਜਾਣ-ਪਛਾਣ](#ਸਖਪ-ਜਣ-ਪਛਣ)
2. [GS1 ਅਤੇ General Specifications ਬਾਰੇ](#gs1-ਅਤ-general-specifications-ਬਰ)
3. [GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ](#gs1-ਐਪਲਕਸਨ-ਆਈਡਟਫਇਰ)
4. [ਤੁਰੰਤ ਸ਼ੁਰੂਆਤ](#ਤਰਤ-ਸਰਆਤ)
5. [ਪਾਰਸਿੰਗ ਪਾਈਪਲਾਈਨ](#ਪਰਸਗ-ਪਈਪਲਈਨ)
   - [ਮੁੱਢਲਾ ਪੜਾਅ — ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਮਢਲ-ਪੜਅ--ਇਨਪਟ-ਮਡਫਇਰ)
   - [ਪੜਾਅ 0 — ਸਹਿ-ਸੰਬੰਧ ID](#ਪੜਅ-0--ਸਹ-ਸਬਧ-id)
   - [ਪੜਾਅ 1 — ਇਨਪੁਟ ਦੀ ਦਿਸ਼ਾ](#ਪੜਅ-1--ਇਨਪਟ-ਦ-ਦਸ)
   - [ਪੜਾਅ 2 — ਵਾਕ-ਬਣਤਰ](#ਪੜਅ-2--ਵਕ-ਬਣਤਰ)
   - [ਪੜਾਅ 3 — ਸਮੱਗਰੀ](#ਪੜਅ-3--ਸਮਗਰ)
   - [ਪੜਾਅ 4 — ਵਿਆਖਿਆ](#ਪੜਅ-4--ਵਆਖਆ)
6. [ਪਾਰਸ ਸੈਟਿੰਗ (`ParseConfig`)](#ਪਰਸ-ਸਟਗ-parseconfig)
   - [ਵਿਕਲਪ](#ਵਕਲਪ)
   - [ਸਥਾਨਕ ਭਾਸ਼ਾ ਵਿੱਚ ਸੁਨੇਹੇ ਤੇ ਲੇਬਲ](#ਸਥਨਕ-ਭਸ-ਵਚ-ਸਨਹ-ਤ-ਲਬਲ)
   - [ਮਿਤੀ ਦਾ ਫਾਰਮੈਟ](#ਮਤ-ਦ-ਫਰਮਟ)
7. [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਇਨਪਟ-ਮਡਫਇਰ)
   - [ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ](#ਅਦਰਨ-ਮਡਫਇਰ)
   - [ਮੋਡੀਫਾਇਰ ਲਿਖਣਾ](#ਮਡਫਇਰ-ਲਖਣ)
   - [ਮੋਡੀਫਾਇਰ ਦਰਜ ਕਰਨਾ](#ਮਡਫਇਰ-ਦਰਜ-ਕਰਨ)
   - [ਵੇਖਣਾ ਕਿ ਮੋਡੀਫਾਇਰ ਨੇ ਕੀ ਕੀਤਾ](#ਵਖਣ-ਕ-ਮਡਫਇਰ-ਨ-ਕ-ਕਤ)
   - [ਮੋਡੀਫਾਇਰ ਦੀ ਨਾਕਾਮੀ ਦੀ ਸੰਭਾਲ](#ਮਡਫਇਰ-ਦ-ਨਕਮ-ਦ-ਸਭਲ)
8. [ਪਾਰਸ ਢੰਗ](#ਪਰਸ-ਢਗ)
   - [DATA_CARRIER ਢੰਗ](#data_carrier-ਢਗ)
   - [SYNTAX ਢੰਗ](#syntax-ਢਗ)
   - [CONTENT ਢੰਗ](#content-ਢਗ)
   - [INTERPRETATION ਢੰਗ (ਮੂਲ)](#interpretation-ਢਗ-ਮਲ)
9. [ਸਹਿ-ਸੰਬੰਧ ID](#ਸਹ-ਸਬਧ-id)
10. [GS1 ਡਿਜੀਟਲ ਲਿੰਕ](#gs1-ਡਜਟਲ-ਲਕ)
11. [ਨਤੀਜਿਆਂ ਨਾਲ ਕੰਮ ਕਰਨਾ](#ਨਤਜਆ-ਨਲ-ਕਮ-ਕਰਨ)
    - [ParseResult](#parseresult)
    - [GS1AIObject](#gs1aiobject)
    - [GS1AIObjectElement](#gs1aiobjectelement)
    - [GaiaError](#gaiaerror)
    - [GS1AIInterpretation](#gs1aiinterpretation)
    - [DataCarrierEntry ਤੇ DataCarrierType](#datacarrierentry-ਤ-datacarriertype)
12. [ਗ਼ਲਤੀਆਂ ਦਾ ਹਵਾਲਾ](#ਗਲਤਆ-ਦ-ਹਵਲ)
13. [ਥ੍ਰੈੱਡ ਸੁਰੱਖਿਆ](#ਥਰਡ-ਸਰਖਆ)
14. [ਅੰਤਿਕਾ A — AI ਸਟ੍ਰਿੰਗ ਸਥਿਰਾਂਕ](#ਅਤਕ-a--ai-ਸਟਰਗ-ਸਥਰਕ)
    - [ਪਛਾਣ ਤੇ ਲੜੀਬੱਧਤਾ](#ਪਛਣ-ਤ-ਲੜਬਧਤ)
    - [ਮਿਤੀਆਂ ਤੇ ਸਮੇਂ](#ਮਤਆ-ਤ-ਸਮ)
    - [ਮਾਤਰਾ ਤੇ ਮਾਪ — ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ (ਮੀਟ੍ਰਿਕ)](#ਮਤਰ-ਤ-ਮਪ--ਪਰਵਰਤਨਸਲ-ਮਪ-ਮਟਰਕ)
    - [ਮਾਤਰਾ ਤੇ ਮਾਪ — ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ (ਇੰਪੀਰੀਅਲ / ਅਮਰੀਕੀ)](#ਮਤਰ-ਤ-ਮਪ--ਪਰਵਰਤਨਸਲ-ਮਪ-ਇਪਰਅਲ--ਅਮਰਕ)
    - [ਕੀਮਤ ਤੇ ਮੁਦਰਾ ਰਕਮਾਂ](#ਕਮਤ-ਤ-ਮਦਰ-ਰਕਮ)
    - [ਥਾਂ ਤੇ ਭੇਜਣਾ](#ਥ-ਤ-ਭਜਣ)
    - [ਵਸਤੂ ਦੇ ਗੁਣ ਤੇ ਪੈੜ-ਨਿਸ਼ਾਨੀ](#ਵਸਤ-ਦ-ਗਣ-ਤ-ਪੜ-ਨਸਨ)
    - [ਕੌਮੀ ਸਿਹਤ-ਸੰਭਾਲ ਮੁੜ-ਭੁਗਤਾਨ ਨੰਬਰ (NHRN)](#ਕਮ-ਸਹਤ-ਸਭਲ-ਮੜ-ਭਗਤਨ-ਨਬਰ-nhrn)
    - [ਸਿਹਤ-ਸੰਭਾਲ, GMN, HIDRI, CPID, ਵਿਅਕਤੀ ਦਾ ਡਾਟਾ](#ਸਹਤ-ਸਭਲ-gmn-hidri-cpid-ਵਅਕਤ-ਦ-ਡਟ)
    - [ਅੰਦਰੂਨੀ / ਕੰਪਨੀ ਦੀ ਵਰਤੋਂ](#ਅਦਰਨ--ਕਪਨ-ਦ-ਵਰਤ)
15. [ਅੰਤਿਕਾ B — ਵਿਆਖਿਆ ਕੁੰਜੀ ਸਥਿਰਾਂਕ](#ਅਤਕ-b--ਵਆਖਆ-ਕਜ-ਸਥਰਕ)
    - [ਮਿਤੀ ਤੇ ਸਮਾਂ](#ਮਤ-ਤ-ਸਮ)
    - [ਵਾਢੀ ਦੀ ਮਿਤੀ](#ਵਢ-ਦ-ਮਤ)
    - [GS1 ਕੰਪਨੀ ਅਗੇਤਰ](#gs1-ਕਪਨ-ਅਗਤਰ)
    - [GTIN](#gtin)
    - [SSCC](#sscc)
    - [ਦੇਸ਼ (ISO 3166)](#ਦਸ-iso-3166)
    - [ਮੁਦਰਾ (ISO 4217)](#ਮਦਰ-iso-4217)
    - [ਤਾਪਮਾਨ](#ਤਪਮਨ)
    - [ਲਿੰਗ (ISO 5218)](#ਲਗ-iso-5218)
    - [ਜਲ-ਜੀਵ ਜਾਤੀਆਂ (FAO)](#ਜਲ-ਜਵ-ਜਤਆ-fao)
    - [NATO ਸਟਾਕ ਨੰਬਰ (NSN)](#nato-ਸਟਕ-ਨਬਰ-nsn)
    - [ਰੋਲ ਵਾਲੀਆਂ ਵਸਤਾਂ](#ਰਲ-ਵਲਆ-ਵਸਤ)
    - [IBAN](#iban)
    - [IMEI](#imei)
    - [SIM ਪਛਾਣਕਾਰ (EID / ICCID)](#sim-ਪਛਣਕਰ-eid--iccid)
    - [ਪ੍ਰਮਾਣੀਕਰਨ ਹਵਾਲਾ](#ਪਰਮਣਕਰਨ-ਹਵਲ)
    - [GS1 UIC](#gs1-uic)
    - [ਬੱਚੇ ਦੇ ਜਨਮ ਦੀ ਤਰਤੀਬ](#ਬਚ-ਦ-ਜਨਮ-ਦ-ਤਰਤਬ)
    - [ਗਲੋਬਲ ਮਾਡਲ ਨੰਬਰ (GMN)](#ਗਲਬਲ-ਮਡਲ-ਨਬਰ-gmn)
    - [HIDRI](#hidri)
    - [CPID](#cpid)
    - [ਦਸ਼ਮਲਵ ਤੇ ਮਾਪ ਵਾਲੇ ਮੁੱਲ](#ਦਸਮਲਵ-ਤ-ਮਪ-ਵਲ-ਮਲ)
    - [ਭੂਗੋਲਿਕ ਧੁਰੇ](#ਭਗਲਕ-ਧਰ)
    - [ਉਤਪਾਦਨ ਦਾ ਢੰਗ](#ਉਤਪਦਨ-ਦ-ਢਗ)
    - [AIDC ਮਾਧਿਅਮ ਦੀ ਕਿਸਮ](#aidc-ਮਧਅਮ-ਦ-ਕਸਮ)
    - [ਕੁੱਲ ਵਿੱਚੋਂ ਟੁਕੜਾ](#ਕਲ-ਵਚ-ਟਕੜ)
    - [ਹਿੱਸਿਆਂ ਦੀ ਵੰਡ](#ਹਸਆ-ਦ-ਵਡ)
    - [ਫੁਟਕਲ](#ਫਟਕਲ)

---

## ਸੰਖੇਪ ਜਾਣ-ਪਛਾਣ

`GaiaParser` GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ (AI) ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਨੂੰ ਪਾਰਸ ਕਰਨ ਦਾ ਦਾਖ਼ਲਾ-ਬਿੰਦੂ ਹੈ। ਇਹ ਸਕੈਨਰ ਦਾ ਕੱਚਾ ਆਉਟਪੁੱਟ ਹੇਠ ਲਿਖੇ ਕਿਸੇ ਵੀ ਰੂਪ ਵਿੱਚ ਸਵੀਕਾਰ ਕਰਦਾ ਹੈ ਅਤੇ ਇੱਕ ਢਾਂਚਾਗਤ `ParseResult` ਮੋੜਦਾ ਹੈ, ਜਿਸ ਵਿੱਚ ਹੱਲ ਕੀਤੇ ਸਾਰੇ AI, ਤਸਦੀਕ ਦੀਆਂ ਗ਼ਲਤੀਆਂ, ਅਤੇ ਜੇ ਚਾਹੋ ਤਾਂ ਬੰਦੇ ਦੇ ਪੜ੍ਹਨ ਯੋਗ ਵਿਆਖਿਆਵਾਂ ਵੀ ਹੁੰਦੀਆਂ ਹਨ:

- ਸਾਦੀ AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ: `0109506000134352`
- AIM ਸਿੰਬੋਲੋਜੀ ਆਈਡੈਂਟੀਫਾਇਰ ਦੇ ਅਗੇਤਰ ਵਾਲੀ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ: `]C10109506000134352`
- GS1 Digital Link URI: `https://example.com/01/09506000134352`
- ਇਹਨਾਂ ਵਿੱਚੋਂ ਕੋਈ ਵੀ, ਮਰਜ਼ੀ ਨਾਲ 8-ਅੰਕੀ ਸਹਿ-ਸੰਬੰਧ ID ਦੇ ਅਗੇਤਰ ਸਮੇਤ: `12345678~0109506000134352`

**ਦਾਖ਼ਲਾ-ਬਿੰਦੂ ਕਲਾਸ:** `tools.pantheum.gaia.GaiaParser`

> **Gaia ਨਾਲ ਨਵੇਂ ਹੋ?** **[GaiaParser ਤੁਰੰਤ ਸ਼ੁਰੂਆਤ](GaiaParser-QuickStart-Punjabi.md)** ਤੋਂ ਸ਼ੁਰੂ ਕਰੋ — ਦਸ ਮਿੰਟਾਂ ਵਿੱਚ ਨਿਰਭਰਤਾਵਾਂ, ਪਹਿਲੀ ਪਾਰਸਿੰਗ, ਅਤੇ ਅਕਸਰ ਠੇਡਾ ਦੇਣ ਵਾਲੀਆਂ ਕੁਝ ਗੱਲਾਂ। ਇਹ ਗਾਈਡ ਪੂਰਾ ਹਵਾਲਾ-ਦਸਤਾਵੇਜ਼ ਹੈ।

> ਇਸ ਦਾ ਉਲਟਾ ਪਾਸਾ — AI/ਮੁੱਲ ਜੋੜਿਆਂ ਤੋਂ ਜਾਇਜ਼ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਅਤੇ Digital Link URI *ਬਣਾਉਣਾ* — **[GaiaBuilder — ਡਿਵੈਲਪਰ ਗਾਈਡ](GaiaBuilder-Punjabi.md)** ਵਿੱਚ ਹੈ।

---

## GS1 ਅਤੇ General Specifications ਬਾਰੇ

**GS1** ਇੱਕ ਸੰਸਾਰ-ਪੱਧਰੀ ਗ਼ੈਰ-ਮੁਨਾਫ਼ਾ ਸੰਸਥਾ ਹੈ ਜੋ ਸਪਲਾਈ-ਲੜੀ ਦੀ ਪਛਾਣ ਅਤੇ ਡਾਟੇ ਦੇ ਵਟਾਂਦਰੇ ਲਈ ਖੁੱਲ੍ਹੇ ਮਿਆਰ ਬਣਾਉਂਦੀ ਤੇ ਸੰਭਾਲਦੀ ਹੈ। ਇਸ ਦੇ ਮਿਆਰ ਪਰਚੂਨ, ਸਿਹਤ-ਸੰਭਾਲ, ਢੋਆ-ਢੁਆਈ, ਖਾਣ-ਪੀਣ ਸੇਵਾਵਾਂ ਅਤੇ ਹੋਰ ਕਈ ਸਨਅਤਾਂ ਵਿੱਚ ਵਰਤੇ ਜਾਂਦੇ ਹਨ; ਖਪਤਕਾਰ ਪੈਕਿੰਗ ਉੱਤੇ ਲੱਗੇ ਉਤਪਾਦ ਬਾਰਕੋਡ ਤੋਂ ਲੈ ਕੇ ਦਵਾਈਆਂ ਦੀਆਂ ਖ਼ੁਰਾਕਾਂ ਦੀ ਸੀਰੀਅਲ-ਆਧਾਰਿਤ ਪੈੜ ਤੱਕ ਸਭ ਕੁਝ ਇਹਨਾਂ ਵਿੱਚ ਆਉਂਦਾ ਹੈ।

ਇਹ ਪਾਰਸਰ ਜੋ ਕੁਝ ਵੀ ਲਾਗੂ ਕਰਦਾ ਹੈ, ਉਸ ਦਾ ਪ੍ਰਮਾਣਿਕ ਹਵਾਲਾ **GS1 General Specifications** ਹੈ — ਇਹੀ ਇੱਕੋ ਦਸਤਾਵੇਜ਼ ਹੇਠ ਲਿਖੀਆਂ ਗੱਲਾਂ ਤੈਅ ਕਰਦਾ ਹੈ:

- ਸਾਰੇ ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ (AI) ਕੋਡ, ਉਹਨਾਂ ਦੇ ਡਾਟਾ ਸਿਰਲੇਖ, ਫਾਰਮੈਟ ਅਤੇ ਤਸਦੀਕ ਦੇ ਨਿਯਮ
- AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ ਬਣਾਉਣ ਅਤੇ ਏਨਕੋਡ ਕਰਨ ਦੇ ਵਾਕ-ਬਣਤਰ ਨਿਯਮ
- ਬਾਰਕੋਡ ਸਿੰਬੋਲੋਜੀ ਦੀਆਂ ਲੋੜਾਂ ਅਤੇ AIM ਕੋਡ ID ਦੀ ਵੰਡ
- ਜਾਂਚ ਅੰਕ ਤੇ ਜਾਂਚ ਅੱਖਰ ਦੇ ਐਲਗੋਰਿਦਮ
- ਦੋ-ਅੰਕੀ ਸਾਲ ਦਾ ਨਿਰਧਾਰਨ (ਸਰਕਦੀ ਖਿੜਕੀ ਦਾ ਨਿਯਮ)
- Data Matrix, QR Code, GS1-128, GS1 DataBar ਅਤੇ ਹੋਰ ਵਾਹਕਾਂ ਦੇ ਵੇਰਵੇ

GS1 General Specifications ਹਰ ਸਾਲ ਨਵੇਂ ਕੀਤੇ ਜਾਂਦੇ ਹਨ। ਮੌਜੂਦਾ ਛਾਪ ਅਤੇ ਸਹਾਇਕ ਸਮੱਗਰੀ ਇੱਥੇ ਮਿਲਦੀ ਹੈ:

> **[https://www.gs1.org/standards/gs1-general-specifications](https://www.gs1.org/standards/gs1-general-specifications)**

GAIA, GS1 General Specifications ਦਾ **ਰੀਲੀਜ਼ 26.0 (ਪ੍ਰਵਾਨਿਤ, ਜਨਵਰੀ 2026)** ਲਾਗੂ ਕਰਦਾ ਹੈ।

GS1 Digital Link URI ਇੱਕ ਸਾਥੀ ਮਿਆਰ, **GS1 Digital Link: URI Syntax**, ਦੇ ਅਧੀਨ ਹਨ। ਮੁੱਢਲੀਆਂ ਪਛਾਣ ਕੁੰਜੀਆਂ, ਕੁੰਜੀ-ਵਿਸ਼ੇਸ਼ਕਾਂ ਦੀ ਤਰਤੀਬ, ਅਤੇ ਡਾਟਾ-ਗੁਣਾਂ ਦੀ ਏਨਕੋਡਿੰਗ ਉਹੀ ਤੈਅ ਕਰਦਾ ਹੈ — ਪਾਰਸਰ Digital Link ਇਨਪੁਟ ਉੱਤੇ ਇਹੀ ਲਾਗੂ ਕਰਦਾ ਹੈ:

> **[https://ref.gs1.org/standards/digital-link/uri-syntax/](https://ref.gs1.org/standards/digital-link/uri-syntax/)**

GAIA, GS1 Digital Link: URI Syntax ਮਿਆਰ ਦਾ **ਰੀਲੀਜ਼ 1.7.0 (ਪ੍ਰਵਾਨਿਤ, ਅਗਸਤ 2026)** ਲਾਗੂ ਕਰਦਾ ਹੈ।

ਇਸ ਦਸਤਾਵੇਜ਼ ਵਿੱਚ ਭਾਗਾਂ ਦੇ ਹਵਾਲੇ GS1 General Specifications ਵੱਲ ਇਸ਼ਾਰਾ ਕਰਦੇ ਹਨ (ਜਿਵੇਂ "Table 7-5", "section 7.12"), ਸਿਰਫ਼ Digital Link ਦੇ ਭਾਗ-ਨੰਬਰ (ਜਿਵੇਂ "§4.9", "§4.12") ਛੋਟ ਹਨ; ਉਹ GS1 Digital Link: URI Syntax ਮਿਆਰ ਵੱਲ ਇਸ਼ਾਰਾ ਕਰਦੇ ਹਨ।

---

## GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ

**GS1 ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ (AI)** ਦੋ ਤੋਂ ਚਾਰ ਅੰਕਾਂ ਦਾ ਇੱਕ ਨਿੱਕਾ ਸੰਖਿਅਕ ਅਗੇਤਰ ਹੈ, ਜੋ ਆਪਣੇ ਤੁਰੰਤ ਬਾਅਦ ਆਉਣ ਵਾਲੇ ਡਾਟੇ ਦਾ ਅਰਥ ਤੇ ਫਾਰਮੈਟ ਤੈਅ ਕਰਦਾ ਹੈ। AI, GS1 General Specifications ਵਿੱਚ ਪਰਿਭਾਸ਼ਿਤ ਹਨ ਅਤੇ ਸਪਲਾਈ-ਲੜੀ ਦੇ ਡਾਟੇ ਦਾ ਵੱਡਾ ਘੇਰਾ ਢਕਦੇ ਹਨ: ਉਤਪਾਦ ਪਛਾਣਕਾਰ, ਮਿਤੀਆਂ, ਮਾਤਰਾਵਾਂ, ਲਾਟ ਨੰਬਰ, ਸੀਰੀਅਲ ਨੰਬਰ, ਮਾਪ, URL, ਅਤੇ ਹੋਰ ਵੀ ਬਹੁਤ ਕੁਝ।

### AI ਐਲੀਮੈਂਟ ਦੀ ਬਣਤਰ

ਹਰ AI ਐਲੀਮੈਂਟ ਦੋ ਹਿੱਸਿਆਂ ਦਾ ਬਣਿਆ ਹੁੰਦਾ ਹੈ:

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

AI ਕੋਡ ਹਮੇਸ਼ਾ ਸੰਖਿਅਕ ਹੁੰਦਾ ਹੈ। ਡਾਟਾ ਮੁੱਲ ਉਸ ਦੇ ਤੁਰੰਤ ਬਾਅਦ ਆਉਂਦਾ ਹੈ, ਅਤੇ ਕੋਡ ਤੇ ਮੁੱਲ ਵਿਚਕਾਰ ਕੋਈ ਵਿਭਾਜਕ ਨਹੀਂ ਹੁੰਦਾ।

### ਸਥਿਰ-ਲੰਬਾਈ ਬਨਾਮ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ AI

AI ਦੋ ਕਿਸਮਾਂ ਵਿੱਚ ਵੰਡੇ ਜਾਂਦੇ ਹਨ:

| ਕਿਸਮ | ਵਿਹਾਰ | ਮਿਸਾਲ |
|---|---|---|
| **ਸਥਿਰ-ਲੰਬਾਈ** | ਅੱਖਰਾਂ ਦੀ ਪੱਕੀ ਗਿਣਤੀ, ਹਮੇਸ਼ਾ ਪੂਰੀ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ | AI `01` (GTIN) — ਹਮੇਸ਼ਾ 14 ਅੰਕ |
| **ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ** | 1 ਤੋਂ ਵੱਧ ਤੋਂ ਵੱਧ ਗਿਣਤੀ ਤੱਕ; GS ਵਿਭਾਜਕ ਉੱਤੇ ਜਾਂ ਇਨਪੁਟ ਦੇ ਅੰਤ ਉੱਤੇ ਮੁੱਕਦੀ ਹੈ | AI `10` (ਬੈਚ/ਲਾਟ) — 1 ਤੋਂ 20 ਅੱਖਰ-ਅੰਕੀ ਅੱਖਰ |

ਕੋਈ AI ਸਥਿਰ ਹੈ ਜਾਂ ਪਰਿਵਰਤਨਸ਼ੀਲ, ਇਹ ਸਿਰਫ਼ GS1 ਵੇਰਵੇ ਵਿੱਚ ਦਿੱਤੀ ਉਸ ਦੀ ਪਰਿਭਾਸ਼ਾ ਤੋਂ ਤੈਅ ਹੁੰਦਾ ਹੈ — ਪਾਰਸਰ ਕਦੇ ਅੰਦਾਜ਼ਾ ਨਹੀਂ ਲਾਉਂਦਾ।

### ਕਈ AI ਵਾਲੀਆਂ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗਾਂ

ਕਈ AI ਨੂੰ ਇੱਕੋ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ ਜੋੜਿਆ ਜਾ ਸਕਦਾ ਹੈ। ਸਥਿਰ-ਲੰਬਾਈ ਵਾਲੇ AI ਸਿੱਧੇ ਜੋੜੇ ਜਾ ਸਕਦੇ ਹਨ, ਕਿਉਂਕਿ ਪਾਰਸਰ ਨੂੰ ਹਮੇਸ਼ਾ ਪੱਕਾ ਪਤਾ ਹੁੰਦਾ ਹੈ ਕਿ ਕਿੰਨੇ ਅੱਖਰ ਪੜ੍ਹਨੇ ਹਨ। ਪਰ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI ਤੋਂ ਬਾਅਦ ਜਦੋਂ ਵੀ ਕੋਈ ਹੋਰ AI ਆਵੇ, ਤਾਂ ਉਸ ਨੂੰ **GS ਅੱਖਰ** (ASCII `0x1D`, ਬਾਰਕੋਡ ਸਿੰਬੋਲੋਜੀਆਂ ਵਿੱਚ ਇਸ ਨੂੰ FNC1 ਵੀ ਕਹਿੰਦੇ ਹਨ) ਨਾਲ ਮੁਕਾਉਣਾ ਲਾਜ਼ਮੀ ਹੈ, ਤਾਂ ਜੋ ਪਾਰਸਰ ਨੂੰ ਪਤਾ ਲੱਗੇ ਕਿ ਇੱਕ ਮੁੱਲ ਕਿੱਥੇ ਮੁੱਕਦਾ ਹੈ ਤੇ ਅਗਲਾ AI ਕੋਡ ਕਿੱਥੋਂ ਸ਼ੁਰੂ ਹੁੰਦਾ ਹੈ।

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

Java ਸਟ੍ਰਿੰਗ ਲਿਟਰਲ ਵਿੱਚ GS ਅੱਖਰ ਨੂੰ ਯੂਨੀਕੋਡ ਐਸਕੇਪ `""` ਨਾਲ ਲਿਖੋ।

### ਆਮ ਵਰਤੇ ਜਾਣ ਵਾਲੇ AI

| AI | ਡਾਟਾ ਸਿਰਲੇਖ | ਫਾਰਮੈਟ | ਮੁੱਲ ਦੀ ਮਿਸਾਲ |
|---|---|---|---|
| `00` | SSCC | N18 | `006141411234567890` |
| `01` | GTIN | N14 | `09506000134352` |
| `10` | BATCH/LOT | X..20 | `LOT-ABC` |
| `11` | PROD DATE | N6 (YYMMDD) | `261231` |
| `17` | USE BY or EXPIRY | N6 (YYMMDD) | `261231` |
| `21` | SERIAL | X..20 | `SN-98765` |
| `37` | COUNT | N..8 | `100` |
| `3103` | NET WEIGHT (kg) | N6 | `001500` (= 1.500 kg) |
| `3922` | PRICE | N..15 | `91234` (= 912.34, ਇਕਹਿਰਾ ਮੁਦਰਾ ਖੇਤਰ) |
| `710` | NHRN PZN | X..20 | `12345678` |

> 4-ਅੰਕੀ ਮਾਪ ਜਾਂ ਕੀਮਤ AI ਦਾ **ਚੌਥਾ ਅੰਕ** ਲੁਕਵੇਂ ਦਸ਼ਮਲਵ ਸਥਾਨਾਂ ਦੀ ਗਿਣਤੀ ਏਨਕੋਡ ਕਰਦਾ ਹੈ — `3103` ਦਾ ਮਤਲਬ 3 ਦਸ਼ਮਲਵਾਂ ਨਾਲ ਕਿਲੋਗ੍ਰਾਮ ਵਿੱਚ ਸ਼ੁੱਧ ਭਾਰ (`001500` = 1.500 kg), ਜਦਕਿ `3102` ਉਹੀ ਅੰਕ 15.00 kg ਪੜ੍ਹੇਗਾ। ਉੱਪਰਲਾ `ਫਾਰਮੈਟ` ਕਾਲਮ *ਡਾਟੇ* ਦਾ ਫਾਰਮੈਟ ਵਿਖਾਉਂਦਾ ਹੈ; ਹਰ AI ਦਾ ਪੂਰਾ `getFormatString()` ਖ਼ੁਦ AI ਨੂੰ ਵੀ ਸ਼ਾਮਲ ਕਰਦਾ ਹੈ (ਜਿਵੇਂ `3103` ਲਈ `N4+N6`)।

### ਬੰਦੇ ਦੇ ਪੜ੍ਹਨ ਯੋਗ ਵਿਆਖਿਆ (HRI)

ਰਵਾਇਤੀ ਪੜ੍ਹਨਯੋਗ ਰੂਪ ਹਰ AI ਕੋਡ ਨੂੰ ਉਸ ਦੇ ਮੁੱਲ ਤੋਂ ਐਨ ਪਹਿਲਾਂ ਬਰੈਕਟਾਂ ਵਿੱਚ ਲਪੇਟਦਾ ਹੈ, ਤੇ ਐਲੀਮੈਂਟਾਂ ਵਿਚਕਾਰ ਇੱਕ ਥਾਂ ਛੱਡਦਾ ਹੈ:

```
(01)09506000134352 (17)261231 (10)LOT-001
```

GS ਵਿਭਾਜਕ HRI ਵਿੱਚ ਨਹੀਂ ਦਿਖਦਾ। ਇਹ ਫਾਰਮੈਟ `GS1AIObject.toHriString()` ਬਣਾਉਂਦਾ ਹੈ।

### ਚਾਰ ਅੰਕਾਂ ਵਾਲੇ AI ਕੋਡ

ਕੁਝ AI ਦੋ ਦੀ ਥਾਂ ਚਾਰ ਅੰਕ ਵਰਤਦੇ ਹਨ। ਪਹਿਲੇ ਦੋ ਅੰਕ AI ਪਰਿਵਾਰ ਦੱਸਦੇ ਹਨ; ਤੀਜਾ ਤੇ/ਜਾਂ ਚੌਥਾ ਅੰਕ ਵਾਧੂ ਅਰਥ ਚੁੱਕਦਾ ਹੈ (ਜਿਵੇਂ ਮਾਪ AI ਵਿੱਚ ਲੁਕਵੇਂ ਦਸ਼ਮਲਵ ਬਿੰਦੂ ਦੀ ਥਾਂ)। ਪਾਰਸਰ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚੋਂ ਪੂਰਾ AI ਕੋਡ ਆਪੇ ਹੱਲ ਕਰ ਲੈਂਦਾ ਹੈ — ਸੱਦਣ ਵਾਲਾ ਹਮੇਸ਼ਾ ਪੂਰੇ ਕੋਡ ਨਾਲ ਹੀ ਕੰਮ ਕਰਦਾ ਹੈ (ਜਿਵੇਂ `"3102"`, ਸਿਰਫ਼ `"31"` ਨਹੀਂ)।

---

## ਤੁਰੰਤ ਸ਼ੁਰੂਆਤ

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

> **GS ਵਿਭਾਜਕ:** ਕਈ AI ਵਾਲੀ ਸਟ੍ਰਿੰਗ ਦੇ ਅੰਦਰ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI ਨੂੰ GS ਅੱਖਰ (ASCII `0x1D`) ਨਾਲ ਵੱਖ ਕਰਨਾ ਲਾਜ਼ਮੀ ਹੈ। Java ਸਟ੍ਰਿੰਗ ਲਿਟਰਲ ਵਿੱਚ `""` ਵਰਤੋ।

---

## ਪਾਰਸਿੰਗ ਪਾਈਪਲਾਈਨ

### ਮੁੱਢਲਾ ਪੜਾਅ — ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ

ਜੇ `ParseConfig` ਕੋਈ **ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ** ਚੁੱਕੀ ਬੈਠਾ ਹੋਵੇ, ਤਾਂ ਉਹ ਹਰ ਕਿਸੇ ਤੋਂ ਪਹਿਲਾਂ ਚੱਲਦੇ ਹਨ — ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ ਲਾਹੁਣ ਤੋਂ ਪਹਿਲਾਂ, ਵਾਹਕ ਪਛਾਣਨ ਤੋਂ ਪਹਿਲਾਂ, GS1 ਪਾਈਪਲਾਈਨ ਵਿੱਚ ਵੜਨ ਤੋਂ ਪਹਿਲਾਂ। ਹਰ ਮੋਡੀਫਾਇਰ ਅਗਲੇ ਲਈ ਕੱਚੇ ਇਨਪੁਟ ਨੂੰ ਮੁੜ ਲਿਖਦਾ ਹੈ, ਅਤੇ ਹੇਠਲੇ ਸਾਰੇ ਪੜਾਅ ਇਸੇ ਲੜੀ ਦੇ ਨਤੀਜੇ ਉੱਤੇ ਕੰਮ ਕਰਦੇ ਹਨ।

ਮੂਲ ਰੂਪ ਵਿੱਚ ਕੋਈ ਮੋਡੀਫਾਇਰ ਸੈੱਟ ਨਹੀਂ ਹੁੰਦਾ, ਸੋ ਜਦ ਤੱਕ ਤੁਸੀਂ ਆਪ ਨਾ ਚੁਣੋ, ਇਹ ਮੁੱਢਲਾ ਪੜਾਅ ਕੁਝ ਨਹੀਂ ਕਰਦਾ। ਵੇਖੋ [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਇਨਪਟ-ਮਡਫਇਰ)।

---

### ਪੜਾਅ 0 — ਸਹਿ-ਸੰਬੰਧ ID

ਕਿਸੇ ਵੀ GS1 ਕਾਰਵਾਈ ਤੋਂ ਪਹਿਲਾਂ, `GaiaParser` ਵੇਖਦਾ ਹੈ ਕਿ ਇਨਪੁਟ ਕਿਸੇ ਮਰਜ਼ੀ ਦੇ **ਸਹਿ-ਸੰਬੰਧ ID ਅਗੇਤਰ** ਨਾਲ ਸ਼ੁਰੂ ਹੁੰਦਾ ਹੈ ਜਾਂ ਨਹੀਂ: ਠੀਕ 8 ASCII ਦਸ਼ਮਲਵ ਅੰਕ, ਤੇ ਉਸ ਤੋਂ ਬਾਅਦ ਇੱਕ ਟਿਲਡ (`~`), ਜਿਵੇਂ `12345678~`।

ਜੇ ਅਗੇਤਰ ਹੋਵੇ ਤਾਂ ਉਹ ਲਾਹ ਕੇ ਮੁੜਦੇ `ParseResult` ਉੱਤੇ `CorrelationInfo` ਵਜੋਂ ਸਾਂਭ ਲਿਆ ਜਾਂਦਾ ਹੈ। ਅਗਲੇ ਸਾਰੇ ਪੜਾਅ ਲਾਹੇ ਹੋਏ ਪੇਲੋਡ ਉੱਤੇ ਕੰਮ ਕਰਦੇ ਹਨ। ਜੇ ਕੋਈ ਅਗੇਤਰ ਨਾ ਹੋਵੇ ਤਾਂ ਇਨਪੁਟ ਜਿਉਂ ਦਾ ਤਿਉਂ ਲੰਘ ਜਾਂਦਾ ਹੈ।

ਵੇਰਵੇ ਲਈ ਵੇਖੋ [ਸਹਿ-ਸੰਬੰਧ ID](#ਸਹ-ਸਬਧ-id)।

---

### ਪੜਾਅ 1 — ਇਨਪੁਟ ਦੀ ਦਿਸ਼ਾ

ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ ਲਾਹੁਣ ਮਗਰੋਂ, `GaiaParser` ਵੇਖਦਾ ਹੈ ਕਿ (ਲਾਹਿਆ ਹੋਇਆ) ਇਨਪੁਟ ਕਿਸੇ **AIM ਕੋਡ ID** ਨਾਲ ਸ਼ੁਰੂ ਹੁੰਦਾ ਹੈ ਜਾਂ ਨਹੀਂ: `]` + ASCII ਅੱਖਰ + ASCII ਅੰਕ ਦੀ ਸ਼ਕਲ ਵਾਲਾ ਤਿੰਨ-ਅੱਖਰੀ ਅਗੇਤਰ (ਜਿਵੇਂ GS1-128 ਲਈ `]C1`, GS1 DataMatrix ਲਈ `]d2`, GS1 DataBar / GS1 Composite ਲਈ `]e0`)।

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

ਜੇ ਵਾਹਕ GS1 AI ਚੁੱਕਣ ਦੇ ਯੋਗ ਨਾ ਹੋਵੇ (ਜਿਵੇਂ ਕੋਈ ਡਾਕ ਬਾਰਕੋਡ), ਤਾਂ ਪਾਰਸਿੰਗ ਓਸੇ ਵੇਲੇ `GE-D002` ਗ਼ਲਤੀ ਨਾਲ ਰੁਕ ਜਾਂਦੀ ਹੈ।

---

### ਪੜਾਅ 2 — ਵਾਕ-ਬਣਤਰ

ਇਹ ਹਮੇਸ਼ਾ ਚੱਲਦਾ ਹੈ। ਇਸ ਦੇ ਦੋ ਉਪ-ਕਦਮ ਹਨ:

**2ੳ. ਟੋਕਨ ਬਣਾਉਣਾ (`AISyntaxParser`)**
- GS1 ਅਗੇਤਰ ਸਾਰਣੀ (GS1 General Specifications Table 7-5) ਦੀ ਮਦਦ ਨਾਲ ਪਹਿਲੇ ਦੋ ਅੱਖਰਾਂ ਤੋਂ AI ਕੋਡ ਦੀ ਲੰਬਾਈ ਪੜ੍ਹਦਾ ਹੈ।
- ਸਥਿਰ-ਲੰਬਾਈ ਵਾਲੇ AI ਇਨਪੁਟ ਵਿੱਚੋਂ ਪੱਕੀ ਗਿਣਤੀ ਦੇ ਬਾਈਟ ਪੜ੍ਹਦੇ ਹਨ।
- ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI GS ਅੱਖਰ ਜਾਂ ਇਨਪੁਟ ਦੇ ਅੰਤ ਤੱਕ ਪੜ੍ਹੇ ਜਾਂਦੇ ਹਨ।
- ਕਈ ਹਿੱਸਿਆਂ ਵਾਲੇ AI ਦੇ ਮੁੱਲ-ਪੁੰਜ ਨੂੰ ਹਰ ਹਿੱਸੇ ਮੁਤਾਬਕ ਟੋਟਿਆਂ ਵਿੱਚ ਕੱਟਿਆ ਜਾਂਦਾ ਹੈ।

**2ਅ. ਬਣਤਰੀ ਤਸਦੀਕ (`SyntaxValidator`)**
- ਦੁਹਰਾਏ ਗਏ AI ਲੱਭਦਾ ਹੈ (`GE-S004`)।
- ਲਾਜ਼ਮੀ AI ਨਿਰਭਰਤਾਵਾਂ ਜਾਂਚਦਾ ਹੈ; ਜਿਵੇਂ AI `02` ਨੂੰ AI `37` ਚਾਹੀਦਾ ਹੈ (`GE-S005`)।
- ਵਰਜਿਤ AI ਜੋੜੇ ਜਾਂਚਦਾ ਹੈ (`GE-S006`)।

ਇਸ ਪੜਾਅ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਦਾ ਪੱਧਰ `SYNTAX_ERROR` (ਟੋਕਨਾਈਜ਼ਰ) ਜਾਂ `INTEGRITY_ERROR` (ਬਣਤਰੀ) ਹੁੰਦਾ ਹੈ। ਜੇ **ਕੋਈ ਵੀ** ਗ਼ਲਤੀ ਹੋਵੇ — ਟੋਕਨਾਈਜ਼ਰ ਦੀ ਹੋਵੇ ਜਾਂ ਬਣਤਰੀ — ਤਾਂ ਪਾਈਪਲਾਈਨ ਰੁਕ ਜਾਂਦੀ ਹੈ ਤੇ ਸਮੱਗਰੀ ਅਤੇ ਵਿਆਖਿਆ ਵਾਲੇ ਪੜਾਅ ਛੱਡ ਦਿੱਤੇ ਜਾਂਦੇ ਹਨ।

---

### ਪੜਾਅ 3 — ਸਮੱਗਰੀ

ਇਹ ਓਦੋਂ ਹੀ ਚੱਲਦਾ ਹੈ ਜਦੋਂ ਪੜਾਅ 2 ਨੇ ਕੋਈ ਗ਼ਲਤੀ ਨਾ ਦਿੱਤੀ ਹੋਵੇ (ਨਾ ਟੋਕਨਾਈਜ਼ਰ ਦੀ, ਨਾ ਬਣਤਰੀ)। ਹਰ ਐਲੀਮੈਂਟ ਲਈ ਪਾਈਪਲਾਈਨ ਇਉਂ ਹੈ (ਹਰ ਕਦਮ ਓਦੋਂ ਹੀ ਚੱਲਦਾ ਹੈ ਜਦੋਂ ਪਿਛਲੇ ਵਿੱਚ ਗ਼ਲਤੀ ਨਾ ਹੋਈ ਹੋਵੇ):

| ਕਦਮ | ਤਸਦੀਕਕਾਰ | ਗ਼ਲਤੀ ਕੋਡ |
|---|---|---|
| ਰੈਗੈਕਸ ਜਾਂਚ | `RegexValidator` | `GE-C001` |
| ਹਿੱਸੇ ਦਾ ਅੱਖਰ-ਸਮੂਹ + ਫਾਰਮੈਟ | `ComponentValidator` | `GE-C005` + ਹਰ ਸ਼ਰਤ ਲਈ ਫਾਰਮੈਟ ਕੋਡ (`GE-C054`–`GE-C115`) |
| ਜਾਂਚ ਅੰਕ / ਜਾਂਚ ਅੱਖਰ | `CheckDigitCharacterValidator` | `GE-C003`, `GE-C004` |
| ਖ਼ਾਸ ਅਰਥ-ਤਸਦੀਕ | `ContentValidatorRegistry` | ਹਰ ਸ਼ਰਤ ਲਈ ਸਮੱਗਰੀ ਕੋਡ (`GE-C116`–`GE-C170`) |

ਇਸ ਪੜਾਅ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਦਾ ਪੱਧਰ `FORMAT_ERROR` ਜਾਂ `DATA_ERROR` ਹੁੰਦਾ ਹੈ, ਇੱਕ ਛੋਟ ਨਾਲ: GS1-ਕੁੰਜੀ
ਵਾਲੇ AI ਉੱਤੇ GS1 ਕੰਪਨੀ ਅਗੇਤਰ ਦੀਆਂ ਜਾਂਚਾਂ ਸਿਰਫ਼ ਸਲਾਹ ਹਨ ਤੇ ਉਹਨਾਂ ਦਾ ਪੱਧਰ `WARNING` ਹੈ (ਵੇਖੋ
[ਗ਼ਲਤੀਆਂ ਦਾ ਹਵਾਲਾ](#ਗਲਤਆ-ਦ-ਹਵਲ)); ਸੋ ਕੋਈ ਅਣਪਛਾਤਾ ਕੰਪਨੀ ਅਗੇਤਰ ਆਪਣੇ ਆਪ ਨਤੀਜੇ ਨੂੰ
ਨਾਜਾਇਜ਼ ਨਹੀਂ ਕਰਦਾ।

---

### ਪੜਾਅ 4 — ਵਿਆਖਿਆ

ਇਹ ਸਿਰਫ਼ `INTERPRETATION` ਢੰਗ ਵਿੱਚ, ਅਤੇ ਓਦੋਂ ਹੀ ਚੱਲਦਾ ਹੈ ਜਦੋਂ ਕਿਸੇ ਐਲੀਮੈਂਟ ਉੱਤੇ ਪਿਛਲੇ ਕਿਸੇ ਪੜਾਅ ਦੀ ਗ਼ਲਤੀ ਨਾ ਹੋਵੇ। `InterpretationEngine` ਹਰ ਐਲੀਮੈਂਟ ਨੂੰ ਲੇਬਲ ਵਾਲੇ ਮੈਟਾਡਾਟੇ ਨਾਲ ਭਰਪੂਰ ਕਰਦਾ ਹੈ:

- `dd/mm/yyyy` ਵਜੋਂ ਮੁੜ-ਫਾਰਮੈਟ ਕੀਤੀਆਂ ਮਿਤੀਆਂ
- GTIN ਜਾਂਚ ਅੰਕ ਦਾ ਨਿਖੇੜਾ ਅਤੇ GS1 ਕੰਪਨੀ ਅਗੇਤਰ ਦੀ ਭਾਲ
- ISO 3166 ਦੇਸ਼ਾਂ ਦੇ ਨਾਮ
- ISO 4217 ਮੁਦਰਾਵਾਂ ਦੇ ਨਾਮ ਤੇ ਨਿਸ਼ਾਨ
- ਡੀਕੋਡ ਕੀਤੀਆਂ ਦਸ਼ਮਲਵ ਰਕਮਾਂ
- HRI (ਬੰਦੇ ਦੇ ਪੜ੍ਹਨ ਯੋਗ ਵਿਆਖਿਆ) ਦੇ ਟੁਕੜੇ

ਨਤੀਜੇ ਹਰ `GS1AIObjectElement` ਉੱਤੇ `GS1AIInterpretation` ਇੰਦਰਾਜਾਂ ਵਜੋਂ ਜੋੜੇ ਜਾਂਦੇ ਹਨ।

---

## ਪਾਰਸ ਸੈਟਿੰਗ (`ParseConfig`)

`GaiaParser` ਠੀਕ ਦੋ ਦਾਖ਼ਲਾ-ਬਿੰਦੂ ਦਿੰਦਾ ਹੈ:

```java
ParseResult parse(String input);                  // uses ParseConfig.defaultConfig()
ParseResult parse(String input, ParseConfig config);
```

`parse(String)` **ਮੂਲ ਸੈਟਿੰਗ** ਨਾਲ ਚੱਲਦਾ ਹੈ: `INTERPRETATION` ਢੰਗ, `/` ਵਿਭਾਜਕ ਤੇ ਚਾਰ-ਅੰਕੀ ਸਾਲ ਵਾਲੀਆਂ ਛੋਟੇ-ਸਿਰੇ ਵਾਲੀਆਂ ਮਿਤੀਆਂ (`dd/mm/yyyy`), ਅਤੇ **ਅੰਗਰੇਜ਼ੀ** ਗ਼ਲਤੀ ਸੁਨੇਹੇ। ਇਹਨਾਂ ਵਿੱਚੋਂ ਕੁਝ ਵੀ ਬਦਲਣ ਲਈ — ਪਾਰਸ ਢੰਗ ਸਮੇਤ — ਇਸ ਦੇ ਵਗਦੇ ਬਿਲਡਰ ਨਾਲ `ParseConfig` ਬਣਾਓ ਤੇ ਦੋ-ਦਲੀਲਾਂ ਵਾਲਾ ਰੂਪ ਵਰਤੋ।

```java
ParseConfig config = ParseConfig.builder()
        .requestedParseMode(ParseMode.CONTENT)
        .language(Language.FRENCH)
        .build();

ParseResult response = parser.parse("0109506000134351", config);
```

ਸਾਰੇ ਵਿਕਲਪਾਂ ਦੇ enum `GaiaConstants` ਵਿੱਚ ਹਨ।

### ਵਿਕਲਪ

| ਬਿਲਡਰ ਵਿਧੀ | Enum (`GaiaConstants`) | ਮੂਲ | ਅਸਰ |
|---|---|---|---|
| `requestedParseMode(...)`     | `ParseMode`     | `INTERPRETATION` | ਪਾਈਪਲਾਈਨ ਦੀ ਡੂੰਘਾਈ — ਵੇਖੋ [ਪਾਰਸ ਢੰਗ](#ਪਰਸ-ਢਗ)। |
| `language(...)`      | `Language`      | `ENGLISH`        | ਗ਼ਲਤੀ ਸੁਨੇਹਿਆਂ, ਵਿਆਖਿਆ ਲੇਬਲਾਂ **ਅਤੇ** AI ਵਰਣਨਾਂ ਦੀ ਭਾਸ਼ਾ। |
| `dateEndian(...)`    | `DateEndian`    | `LITTLE`         | ਮਿਤੀ ਦੇ ਹਿੱਸਿਆਂ ਦੀ ਤਰਤੀਬ: `LITTLE` (`dd/mm/yyyy`), `MIDDLE` (`mm/dd/yyyy`), `BIG` (`yyyy/mm/dd`)। |
| `dateSeparator(...)` | `DateSeparator` | `SLASH`          | ਮਿਤੀ ਦੇ ਹਿੱਸਿਆਂ ਵਿਚਲਾ ਅੱਖਰ: `SLASH` (`/`), `HYPHEN` (`-`), `PERIOD` (`.`)। |
| `monthFormat(...)`   | `MonthFormat`   | `TWO_DIGIT`      | `TWO_DIGIT` (`12`) ਜਾਂ `THREE_LETTER` (`DEC`)। |
| `yearFormat(...)`    | `YearFormat`    | `FOUR_DIGIT`     | `FOUR_DIGIT` (`2026`) ਜਾਂ `TWO_DIGIT` (`26`)। |
| `skipRequiresCheck(...)` | `boolean`   | `false`          | ਬਣਤਰੀ "ਲੋੜੀਂਦਾ" ਜਾਂਚ (`GE-S005`) ਛੱਡ ਦਿੰਦਾ ਹੈ। |
| `skipExcludesCheck(...)` | `boolean`   | `false`          | ਬਣਤਰੀ "ਵਰਜਿਤ" ਜਾਂਚ (`GE-S006`) ਛੱਡ ਦਿੰਦਾ ਹੈ। |
| `modifier(...)` / `modifierClass(...)` | `ModifierInterface` / ਕਲਾਸ ਦਾ ਨਾਮ | ਕੋਈ ਨਹੀਂ | ਉਹ ਕੋਡ ਜੋ ਪਾਰਸਿੰਗ ਤੋਂ ਪਹਿਲਾਂ ਕੱਚਾ ਇਨਪੁਟ ਮੁੜ ਲਿਖਦਾ ਹੈ — ਦੋ [ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ](#ਅਦਰਨ-ਮਡਫਇਰ) ਅਤੇ ਤੁਹਾਡੇ ਲਿਖੇ ਹੋਏ। ਵੇਖੋ [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਇਨਪਟ-ਮਡਫਇਰ)। |

ਮਿਤੀ ਵਾਲੇ ਇਹ ਚਾਰੇ ਵਿਕਲਪ ਸਿਰਫ਼ ਉਹਨਾਂ ਫਾਰਮੈਟ ਕੀਤੀਆਂ ਮਿਤੀ-ਸਟ੍ਰਿੰਗਾਂ ਉੱਤੇ ਅਸਰ ਪਾਉਂਦੇ ਹਨ ਜੋ ਵਿਆਖਿਆ-ਭਰਪੂਰਕ `INTERPRETATION` ਢੰਗ ਵਿੱਚ ਬਣਾਉਂਦੇ ਹਨ; ਇਹ ਤਸਦੀਕ ਨਹੀਂ ਬਦਲਦੇ। ਬਿਲਡਰ ਦੇ ਮੁੱਲ ਛੱਡੇ ਜਾ ਸਕਦੇ ਹਨ — ਜੋ ਵਿਕਲਪ ਸੈੱਟ ਨਾ ਕੀਤਾ ਜਾਵੇ (ਜਾਂ ਜਿਸ ਨੂੰ `null` ਦਿੱਤਾ ਜਾਵੇ) ਉਹ ਆਪਣਾ ਮੂਲ ਮੁੱਲ ਰੱਖਦਾ ਹੈ।

### ਸਥਾਨਕ ਭਾਸ਼ਾ ਵਿੱਚ ਸੁਨੇਹੇ ਤੇ ਲੇਬਲ

`language(...)` ਬੰਦੇ ਦੇ ਪੜ੍ਹਨ ਯੋਗ **ਤਿੰਨ** ਤਰ੍ਹਾਂ ਦੇ ਪਾਠ ਦੀ ਭਾਸ਼ਾ ਚੁਣਦਾ ਹੈ: ਗ਼ਲਤੀ ਸੁਨੇਹੇ, ਵਿਆਖਿਆ ਲੇਬਲ (ਹਰ `GS1AIInterpretation` ਦਾ `getLabel()`), ਅਤੇ AI ਵਰਣਨ (ਹਰ `GS1AIObjectElement` ਦਾ `getDescription()`)।

`GaiaConstants.Language` ਵਿੱਚ **35 ਭਾਸ਼ਾਵਾਂ** ਪਰਿਭਾਸ਼ਿਤ ਹਨ, ਜੋ ਦੁਨੀਆ ਦੀਆਂ ਸਭ ਤੋਂ ਵੱਧ ਬੋਲੀਆਂ ਜਾਣ ਵਾਲੀਆਂ ਭਾਸ਼ਾਵਾਂ ਢਕਦੀਆਂ ਹਨ: ਅੰਗਰੇਜ਼ੀ, ਫਰਾਂਸੀਸੀ, ਸਪੇਨੀ, ਜਰਮਨ, ਇਤਾਲਵੀ, ਪੁਰਤਗਾਲੀ, ਡੱਚ, ਪੋਲਿਸ਼, ਰੂਸੀ, ਯੂਕਰੇਨੀ, ਚੈੱਕ, ਸਵੀਡਿਸ਼, ਚੀਨੀ, ਜਾਪਾਨੀ, ਕੋਰੀਆਈ, ਅਰਬੀ, ਇੰਡੋਨੇਸ਼ੀਆਈ, ਹਿੰਦੀ, ਤੁਰਕੀ, ਬੰਗਾਲੀ, ਉਰਦੂ, ਵੀਅਤਨਾਮੀ, ਨਾਈਜੀਰੀਅਨ ਪਿਜਿਨ, ਮਿਸਰੀ ਅਰਬੀ, ਮਰਾਠੀ, ਤੇਲਗੂ, ਤਮਿਲ, ਕੈਂਟਨੀ, ਵੂ ਚੀਨੀ, ਤਾਗਾਲੋਗ, ਫ਼ਾਰਸੀ, ਹਾਉਸਾ, ਪੰਜਾਬੀ, ਜਾਵਾਨੀ ਅਤੇ ਸਵਾਹਿਲੀ।

ਅਨੁਵਾਦ ਦੀ ਹਾਲਤ (ਜਿਵੇਂ ਭੇਜੀ ਜਾਂਦੀ ਹੈ):
- **ਵਿਆਖਿਆ ਲੇਬਲ** — ਸਾਰੀਆਂ ਭਾਸ਼ਾਵਾਂ ਲਈ ਅਨੁਵਾਦ ਕੀਤੇ ਹੋਏ।
- **ਗ਼ਲਤੀ ਸੁਨੇਹੇ** — ਸਾਰੀਆਂ ਭਾਸ਼ਾਵਾਂ ਲਈ ਅਨੁਵਾਦ ਕੀਤੇ ਹੋਏ।
- **AI ਵਰਣਨ** — ਅੰਗਰੇਜ਼ੀ ਤੋਂ ਬਿਨਾਂ ਸਾਰੀਆਂ ਭਾਸ਼ਾਵਾਂ ਲਈ ਅਨੁਵਾਦ ਕੀਤੇ ਹੋਏ। ਅੰਗਰੇਜ਼ੀ ਦੀ ਵੱਖਰੀ ਸੂਚੀ ਨਹੀਂ: ਉਹ ਸਿੱਧੀ `gs1-application-identifiers.jsonld` ਵਿੱਚ ਉਸ AI ਦੇ ਇੰਦਰਾਜ ਦੇ `description` ਖੇਤਰ ਤੋਂ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ, ਤੇ ਅਖ਼ੀਰ ਹਰ AI ਵਰਣਨ ਇਸੇ ਉੱਤੇ ਮੁੜਦਾ ਹੈ।

ਨਾਈਜੀਰੀਅਨ ਪਿਜਿਨ (`NIGERIAN_PIDGIN`), ਜੋ ਅੰਗਰੇਜ਼ੀ-ਆਧਾਰਿਤ ਕ੍ਰਿਓਲ ਹੈ, ਵਿਆਖਿਆ ਲੇਬਲਾਂ ਤੇ ਗ਼ਲਤੀ ਸੁਨੇਹਿਆਂ ਲਈ ਜਾਣ-ਬੁੱਝ ਕੇ ਅੰਗਰੇਜ਼ੀ ਪਾਠ ਹੀ ਮੁੜ ਵਰਤਦੀ ਹੈ। AI ਵਰਣਨ ਇਸੇ ਛੋਟ ਦੀ ਛੋਟ ਹਨ: ਅੰਗਰੇਜ਼ੀ ਮੁੜ ਵਰਤਣ ਦੀ ਥਾਂ ਉਹ ਸੱਚੀ ਪਿਜਿਨ ਸ਼ੈਲੀ ਵਿੱਚ ਅਨੁਵਾਦ ਕੀਤੇ ਗਏ ਹਨ, ਕਿਉਂਕਿ AI-ਵਰਣਨ ਦੀਆਂ ਸੂਚੀਆਂ ਲੇਬਲ/ਸੁਨੇਹਾ ਸੂਚੀਆਂ ਤੋਂ ਵੱਖਰੇ ਤੌਰ ਉੱਤੇ ਬਣਾਈਆਂ ਗਈਆਂ ਸਨ। ਮਸ਼ੀਨੀ ਅਨੁਵਾਦਾਂ ਉੱਤੇ ਅਮਲੀ ਮਾਹੌਲ ਵਿੱਚ ਭਰੋਸਾ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ ਮਾਂ-ਬੋਲੀ ਵਾਲਿਆਂ ਤੋਂ ਪੜਤਾਲ ਕਰਵਾ ਲੈਣੀ ਚਾਹੀਦੀ ਹੈ।

ਕਿਸੇ ਭਾਸ਼ਾ ਦੀ ਸੂਚੀ ਵਿੱਚ ਨਾ ਮਿਲਣ ਵਾਲਾ ਕੋਈ ਵੀ ਸੁਨੇਹਾ, ਲੇਬਲ ਜਾਂ ਵਰਣਨ ਅੰਗਰੇਜ਼ੀ ਉੱਤੇ ਮੁੜ ਜਾਂਦਾ ਹੈ। ਸੱਜਿਓਂ-ਖੱਬੇ ਲਿਖੀਆਂ ਜਾਣ ਵਾਲੀਆਂ ਭਾਸ਼ਾਵਾਂ (ਅਰਬੀ, ਉਰਦੂ, ਮਿਸਰੀ ਅਰਬੀ, ਫ਼ਾਰਸੀ) ਸਟ੍ਰਿੰਗਾਂ ਵਜੋਂ ਠੀਕ ਸਾਂਭੀਆਂ ਹੋਈਆਂ ਹਨ; ਉਹਨਾਂ ਨੂੰ ਸੱਜਿਓਂ-ਖੱਬੇ ਵਿਖਾਉਣਾ ਵਿਖਾਵੇ ਵਾਲੀ ਪਰਤ ਦਾ ਕੰਮ ਹੈ।

```java
ParseResult en = parser.parse("0109506000134351");                       // default — English
ParseResult fr = parser.parse("0109506000134351",
        ParseConfig.builder().language(Language.FRENCH).build());

// Same GTIN check-digit failure (GE-C003), localized:
//   en → "Check digit validation failed for AI (01) value ..."
//   fr → "La validation du chiffre de contrôle a échoué pour la valeur ..."
```

ਵਿਆਖਿਆ ਲੇਬਲ ਵੀ ਇਸੇ ਤਰ੍ਹਾਂ ਸਥਾਨਕ ਭਾਸ਼ਾ ਵਿੱਚ ਢਲਦੇ ਹਨ (ਮੁੱਲ ਨਹੀਂ ਬਦਲਦੇ — ਸਿਰਫ਼ ਲੇਬਲ):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
// GTIN_TYPE interpretation:  label "Type de GTIN"  (English: "GTIN type"), value "GTIN-13"
```

AI ਵਰਣਨ ਵੀ ਇਸੇ ਤਰ੍ਹਾਂ ਢਲਦੇ ਹਨ (ਸਿਰਫ਼ `getTitle()`, ਜਿਵੇਂ `"GTIN"`, ਨਹੀਂ ਢਲਦਾ):

```java
ParseResult fr = parser.parse("0109506000134352",
        ParseConfig.builder().language(Language.FRENCH).build());
fr.getAiObject().get("01").getDescription();
// "Numéro international d'article commercial (GTIN)"  (English: "Global Trade Item Number (GTIN)")
```

### ਮਿਤੀ ਦਾ ਫਾਰਮੈਟ

```java
ParseConfig iso = ParseConfig.builder()
        .dateEndian(DateEndian.BIG)
        .dateSeparator(DateSeparator.HYPHEN)
        .build();

// AI (17) USE BY / EXPIRY 271231 → formatted date interpretation reads "2027-12-31"
ParseResult r = parser.parse("17271231", iso);
```

---

## ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ

**ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ** ਉਹ ਕੋਡ ਹੈ ਜੋ Gaia ਦੇ ਪਾਰਸ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ ਕੱਚੀ ਇਨਪੁਟ ਸਟ੍ਰਿੰਗ ਮੁੜ ਲਿਖਦਾ ਹੈ। ਮੋਡੀਫਾਇਰ ਉਸ ਇਨਪੁਟ ਲਈ ਹਨ ਜੋ ਪਹਿਲਾਂ ਹੀ ਵਿਗੜਿਆ ਹੋਇਆ ਪਹੁੰਚਦਾ ਹੈ — ਕੋਈ ਸਕੈਨਰ ਜੋ GS ਵਿਭਾਜਕ ਦੀ ਥਾਂ ਛਾਪਣਯੋਗ ਅੱਖਰ ਰੱਖ ਦਿੰਦਾ ਹੈ, ਕੋਈ ਮਿਡਲਵੇਅਰ ਜੋ ਪੇਲੋਡ ਨੂੰ ਵਿਕਰੇਤਾ ਦੇ ਖ਼ਾਸ ਅਗੇਤਰ ਵਿੱਚ ਲਪੇਟ ਦਿੰਦਾ ਹੈ, ਕੋਈ ਮੇਜ਼ਬਾਨ ਸਿਸਟਮ ਜੋ ਸਭ ਕੁਝ ਵੱਡੇ ਅੱਖਰਾਂ ਵਿੱਚ ਬਦਲ ਦਿੰਦਾ ਹੈ। ਹਰ ਸੱਦਣ ਵਾਲੀ ਥਾਂ ਉੱਤੇ ਹਰ ਸਟ੍ਰਿੰਗ ਨੂੰ ਪਹਿਲਾਂ ਸੰਵਾਰਨ ਦੀ ਥਾਂ (ਤੇ ਇਉਂ ਕਰਦਿਆਂ ਕਿਸੇ ਇੱਕ ਥਾਂ ਬਾਰੀਕ ਗ਼ਲਤੀ ਕਰ ਬੈਠਣ ਦੀ ਥਾਂ), ਸਧਾਰਨੀਕਰਨ ਇੱਕੋ ਵਾਰ `ParseConfig` ਉੱਤੇ ਦਰਜ ਕਰੋ ਤੇ ਇਸ ਨੂੰ ਲਾਗੂ ਕਰਨ ਦਾ ਕੰਮ ਪਾਰਸਰ ਉੱਤੇ ਛੱਡ ਦਿਓ।

ਮੋਡੀਫਾਇਰ `GaiaParser.parse(...)` ਦੇ ਬਿਲਕੁਲ ਸ਼ੁਰੂ ਵਿੱਚ ਚੱਲਦੇ ਹਨ — ਸਹਿ-ਸੰਬੰਧ ID ਲਾਹੁਣ ਤੋਂ ਪਹਿਲਾਂ, AIM ਕੋਡ ID ਪਛਾਣਨ ਤੋਂ ਪਹਿਲਾਂ, GS1 ਪਾਈਪਲਾਈਨ ਤੋਂ ਪਹਿਲਾਂ। ਇਸ ਤੋਂ ਅੱਗੇ ਹਰ ਚੀਜ਼ ਨੂੰ ਸਿਰਫ਼ ਮੁੜ-ਲਿਖੀ ਸਟ੍ਰਿੰਗ ਹੀ ਦਿਸਦੀ ਹੈ। ਦੋਹਾਂ [ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰਾਂ](#ਅਦਰਨ-ਮਡਫਇਰ) ਸਮੇਤ **ਮੂਲ ਰੂਪ ਵਿੱਚ ਕੁਝ ਵੀ ਸੈੱਟ ਨਹੀਂ ਹੁੰਦਾ** — ਹਰ `ParseConfig` ਲਈ ਤੁਸੀਂ ਆਪ ਚੁਣਦੇ ਹੋ।

**ਇੰਟਰਫੇਸ:** `tools.pantheum.gaia.modifier.ModifierInterface`

### ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ

ਕੋਰ jar ਵਿੱਚ `tools.pantheum.gaia.modifier.custom` ਹੇਠ ਦੋ ਮੋਡੀਫਾਇਰ ਆਉਂਦੇ ਹਨ। GS1 ਪੇਲੋਡ ਸਭ ਤੋਂ ਵੱਧ ਜਿਹਨਾਂ ਦੋ ਤਰੀਕਿਆਂ ਨਾਲ ਵਿਗੜ ਕੇ ਆਉਂਦਾ ਹੈ, ਇਹ ਉਹੀ ਸੰਭਾਲਦੇ ਹਨ — ਡਾਟਾ ਸਮਝ ਲਏ ਗਏ ਛਪੇ ਹੋਏ HRI ਬਰੈਕਟ, ਅਤੇ ਬੇਲੋੜੀਆਂ ਥਾਂਵਾਂ — ਸੋ ਆਮ ਹਾਲਤਾਂ ਲਈ ਕੋਈ ਖ਼ਾਸ ਕਲਾਸ ਲਿਖਣ ਦੀ ਲੋੜ ਨਹੀਂ:

| ਕਲਾਸ | `getName()` | ਕੀ ਕਰਦਾ ਹੈ |
|---|---|---|
| `ModifierRemoveAIBrackets` | `Remove Brackets Around AI` | ਹਰ AI ਦੁਆਲੇ ਲੱਗੇ HRI ਬਰੈਕਟ (`(01)…(10)…`) ਲਾਹ ਦਿੰਦਾ ਹੈ ਤੇ ਉਹ FNC1 ਵਿਭਾਜਕ ਮੁੜ ਲਿਆਉਂਦਾ ਹੈ ਜਿਸ ਵੱਲ ਉਹ ਇਸ਼ਾਰਾ ਕਰਦੇ ਸਨ। |
| `ModifierRemoveSpaces` | `Remove Space Characters` | AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚੋਂ ਹਰ ਥਾਂ (`0x20`) ਹਟਾ ਦਿੰਦਾ ਹੈ। |

ਇਹ ਦੋਵੇਂ ਕਿਸੇ ਖ਼ਾਸ ਦਰਜੇ ਤੋਂ ਬਿਨਾਂ ਸਧਾਰਨ `ModifierInterface` ਅਮਲ ਹਨ — ਤੁਹਾਡੇ ਆਪਣੇ ਲਿਖਿਆਂ ਵਾਂਗ ਹੀ ਇਹ ਦਰਜ ਹੁੰਦੇ, ਤਰਤੀਬ ਪਾਉਂਦੇ, ਰਿਪੋਰਟ ਹੁੰਦੇ ਤੇ ਨਾਕਾਮ ਹੁੰਦੇ ਹਨ:

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

ਦੋਵੇਂ ਹਾਲਤ-ਰਹਿਤ ਤੇ ਥ੍ਰੈੱਡ-ਸੁਰੱਖਿਅਤ ਹਨ, ਸੋ ਇੱਕੋ ਨਮੂਨਾ ਸਾਰੇ ਸਾਂਝਾ ਕਰ ਸਕਦੇ ਹਨ; ਅਤੇ ਸੈਟਿੰਗ-ਫ਼ਾਈਲ ਆਧਾਰਿਤ ਪ੍ਰਬੰਧਾਂ ਲਈ ਦੋਵਾਂ ਨੂੰ ਪੂਰੇ ਕਲਾਸ ਨਾਮ ਨਾਲ ਸੱਦਿਆ ਜਾ ਸਕਦਾ ਹੈ (ਵੇਖੋ [ਮੋਡੀਫਾਇਰ ਦਰਜ ਕਰਨਾ](#ਮਡਫਇਰ-ਦਰਜ-ਕਰਨ))।

#### `ModifierRemoveAIBrackets`

GS1 ਦੀ ਬੰਦੇ ਦੇ ਪੜ੍ਹਨ ਯੋਗ ਵਿਆਖਿਆ ਹਰ AI ਨੂੰ ਬਰੈਕਟਾਂ ਵਿੱਚ ਛਾਪਦੀ ਹੈ — `(01)09521234543213(10)ABC123` — ਇਹ ਨਿਰੋਲ ਛਪਾਈ ਦੀ ਰੀਤ ਹੈ। HRI ਭੇਜਣ ਲਈ ਸੈੱਟ ਕੀਤਾ ਕੋਈ ਸਕੈਨਰ ਜਾਂ ਮਿਡਲਵੇਅਰ ਉਹਨਾਂ ਬਰੈਕਟਾਂ ਨੂੰ ਡਾਟੇ ਵਾਂਗ ਹੀ ਅੱਗੇ ਭੇਜ ਦਿੰਦਾ ਹੈ, ਤੇ ਟੋਕਨਾਈਜ਼ਰ ਨੂੰ ਪਤਾ ਨਹੀਂ ਹੁੰਦਾ ਕਿ ਉਹਨਾਂ ਦਾ ਕੀ ਕਰੇ।

ਬਰੈਕਟ ਲਾਹੁਣੇ ਸਿਰਫ਼ ਅੱਧਾ ਕੰਮ ਹੈ। HRI ਵਿੱਚ *ਅਗਲੇ* AI ਦਾ ਖੁੱਲ੍ਹਣ ਵਾਲਾ `(` ਹੀ ਪਿਛਲੇ ਮੁੱਲ ਦਾ ਅੰਤ ਦੱਸਦਾ ਹੈ, ਸੋ ਬਰੈਕਟਾਂ ਵਾਲੀ ਸ਼ਕਲ ਵਿੱਚ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI ਨੂੰ FNC1 ਦੀ ਲੋੜ ਨਹੀਂ ਪੈਂਦੀ। ਬਰੈਕਟ ਬਿਨਾਂ ਸੋਚੇ ਲਾਹ ਦਿਓ ਤੇ ਉਹ ਹੱਦ ਹੀ ਗੁੰਮ ਹੋ ਜਾਂਦੀ ਹੈ:

```
(400)1234A1234567899(90)DD123
  ↓  naive strip
4001234A123456789990DD123      ← AI (400) is X..30 and swallows the (90) payload
```

ਇਸੇ ਲਈ ਇਹ ਮੋਡੀਫਾਇਰ **ਹਰ ਉਸ ਹੱਦ ਉੱਤੇ ਮੁੜ FNC1 ਪਾ ਦਿੰਦਾ ਹੈ ਜਿੱਥੇ ਪਹਿਲਾ AI ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲਾ ਹੋਵੇ**, ਤੇ ਠੀਕ ਉਹੀ ਮੋੜ ਲਿਆਉਂਦਾ ਹੈ ਜੋ ਬਰੈਕਟ ਏਨਕੋਡ ਕਰ ਰਹੇ ਸਨ:

```java
ModifierInterface m = new ModifierRemoveAIBrackets();

m.modify("(400)1234A1234567899(90)DD123");
// 4001234A1234567899<GS>90DD123          — (400) is variable-length, so a separator is restored

m.modify("(01)09521234543213(3103)000123(10)LOT1");
// 0109521234543213310300012310LOT1       — (01) and (3103) are fixed-length; no separator added

m.modify("(01)09521234543213(10)ABC123");
// 010952123454321310ABC123               — the trailing value ends at end-of-string, so no separator
```

ਲੰਬਾਈ ਪਾਰਸਰ ਦੀ ਆਪਣੀ `AiDefinitionRegistry` ਵਿੱਚੋਂ ਲੱਭੀ ਜਾਂਦੀ ਹੈ, ਸੋ ਕੋਡ ਵਿੱਚ ਪੱਕੀ ਲਿਖੀ ਸੂਚੀ ਦੀ ਥਾਂ ਹਰ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲਾ AI ਸੰਭਾਲਿਆ ਜਾਂਦਾ ਹੈ। ਤਿੰਨ ਹਾਲਤਾਂ ਜਾਣ-ਬੁੱਝ ਕੇ ਬਿਨਾਂ ਛੇੜੇ ਛੱਡ ਦਿੱਤੀਆਂ ਜਾਂਦੀਆਂ ਹਨ: ਉਹ ਮੁੱਲ ਜੋ ਪਹਿਲਾਂ ਹੀ FNC1 ਉੱਤੇ ਮੁੱਕਦਾ ਹੈ (ਦੋਵੇਂ ਰੀਤਾਂ ਭੇਜਣ ਵਾਲੇ ਸਰੋਤ ਨੂੰ ਦੂਜਾ ਵਿਭਾਜਕ ਨਹੀਂ ਮਿਲਦਾ), ਉਹ ਬਰੈਕਟਾਂ ਵਾਲਾ ਕੋਡ ਜੋ ਕੋਈ ਜਾਣਿਆ-ਪਛਾਣਿਆ AI ਨਹੀਂ (ਅਣਜਾਣ AI ਆਪਣੀ ਲੰਬਾਈ ਬਾਰੇ ਕੁਝ ਨਹੀਂ ਦੱਸਦਾ), ਅਤੇ ਸਟ੍ਰਿੰਗ ਦਾ ਆਖ਼ਰੀ AI।

ਇਹ ਮੁੜ-ਲਿਖਾਈ **ਸਮ-ਸ਼ਕਤ** ਹੈ — ਇਸ ਨੂੰ ਇਸੇ ਦੇ ਆਪਣੇ ਨਤੀਜੇ ਉੱਤੇ ਚਲਾਓ ਤਾਂ ਕੁਝ ਨਹੀਂ ਬਦਲਦਾ — ਸੋ ਇਹ ਉਸ ਰਲਵੇਂ ਵਹਾਅ ਉੱਤੇ ਵੀ ਸੁਰੱਖਿਅਤ ਹੈ ਜਿਸ ਵਿੱਚ ਸਿਰਫ਼ ਕੁਝ ਇਨਪੁਟ ਬਰੈਕਟਾਂ ਵਾਲੇ ਹੋਣ।

> **ਹੱਦ।** `(` ਤੇ `)` ਆਪ ਜਾਇਜ਼ GS1 ਡਾਟਾ ਅੱਖਰ ਹਨ, ਤੇ ਇੱਥੇ ਵਰਤਿਆ ਨਮੂਨਾ ਸਿਰਫ਼ `\((\d{2,4})\)` ਹੈ। ਜੇ ਕਿਸੇ ਮੁੱਲ ਵਿੱਚ ਇਤਫ਼ਾਕਨ ਬਰੈਕਟਾਂ ਵਿੱਚ ਦੋ-ਤੋਂ-ਚਾਰ ਅੰਕਾਂ ਵਾਲਾ ਕੋਈ ਨੰਬਰ ਹੋਵੇ, ਤਾਂ ਉਸ ਦੇ ਬਰੈਕਟ ਵੀ ਲਹਿ ਜਾਣਗੇ। ਇਸ ਨੂੰ ਸਿਰਫ਼ ਉਸ ਸਰੋਤ ਉੱਤੇ ਲਾਓ ਜੋ HRI ਬਰੈਕਟ ਰੀਤ ਵਰਤਦਾ ਹੋਵੇ, ਉਸ ਉੱਤੇ ਨਹੀਂ ਜਿਸ ਵਿੱਚ ਸੱਚਮੁੱਚ ਬਰੈਕਟਾਂ ਵਾਲੇ ਮੁੱਲ ਹੋਣ।

#### `ModifierRemoveSpaces`

ਕੁਝ ਸਕੈਨਰ, ਮਿਡਲਵੇਅਰ ਤੇ ਲੇਬਲ-ਛਪਾਈ ਪ੍ਰਬੰਧ ਨਹੀਂ ਤਾਂ ਠੀਕ-ਠਾਕ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ ਬੇਲੋੜੀਆਂ ਥਾਂਵਾਂ ਪਾ ਦਿੰਦੇ ਹਨ — ਕਿਸੇ ਪੱਕੀ-ਚੌੜਾਈ ਵਾਲੇ ਖੇਤਰ ਨੂੰ ਭਰਨ ਲਈ, ਪੜ੍ਹਨਯੋਗ ਟੋਲੀਆਂ ਵੱਖ ਕਰਨ ਲਈ, ਜਾਂ ਲੰਮਾ ਮੁੱਲ ਲਪੇਟਣ ਲਈ। ਟੋਕਨਾਈਜ਼ਰ ਹਰ ਥਾਂ ਨੂੰ ਡਾਟਾ ਹੀ ਮੰਨਦਾ ਹੈ, ਜਿਸ ਨਾਲ ਜਿਸ ਮੁੱਲ ਵਿੱਚ ਉਹ ਬੈਠੀ ਹੈ ਉਹ ਵਿਗੜ ਜਾਂਦਾ ਹੈ, ਤੇ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI ਲਈ ਉਸ ਤੋਂ ਅੱਗੇ ਦਾ ਸਭ ਕੁਝ ਖਿਸਕ ਜਾਂਦਾ ਹੈ।

```java
new ModifierRemoveSpaces().modify("0109506000134352 21 SER 123");
// 010950600013435221SER123
```

ਸਿਰਫ਼ ASCII `0x20` ਹਟਾਇਆ ਜਾਂਦਾ ਹੈ। ਬਾਕੀ ਚਿੱਟੀਆਂ ਥਾਂਵਾਂ ਆਪਣੀ ਥਾਂ ਰਹਿੰਦੀਆਂ ਹਨ — ਜਿਵੇਂ ਟੈਬ GS1 ਦੇ ਏਨਕੋਡ-ਯੋਗ ਸਮੂਹ ਤੋਂ ਬਾਹਰ ਹੈ, ਸੋ ਪਾਰਸਰ ਉਸ ਨੂੰ ਚੁੱਪ-ਚਾਪ ਵਾਰ ਦੇਣ ਦੀ ਥਾਂ `GE-S008` ਵਜੋਂ ਦੱਸਦਾ ਹੈ।

> **ਹੱਦ।** ਥਾਂ (`0x20`) GS1 ਦੇ ਅਟੱਲ ਅੱਖਰ-ਸਮੂਹ ਦਾ ਹਿੱਸਾ ਹੈ, ਸੋ ਕਿਸੇ ਬੈਚ/ਲਾਟ ਜਾਂ ਗਾਹਕ ਦੇ ਪੁਰਜ਼ਾ-ਨੰਬਰ ਵਿੱਚ ਜਾਇਜ਼ ਤੌਰ ਉੱਤੇ ਵੀ ਥਾਂ ਹੋ ਸਕਦੀ ਹੈ। ਮੋਡੀਫਾਇਰ ਬੇਲੋੜੀ ਥਾਂ ਤੇ ਅਸਲੀ ਥਾਂ ਵਿੱਚ ਫ਼ਰਕ ਨਹੀਂ ਕਰ ਸਕਦਾ; ਇਸ ਨੂੰ ਸਿਰਫ਼ ਉਸ ਸਰੋਤ ਉੱਤੇ ਲਾਓ ਜਿਸ ਬਾਰੇ ਪਤਾ ਹੋਵੇ ਕਿ ਉਹ ਆਪਣੇ AI ਮੁੱਲਾਂ ਵਿੱਚ ਥਾਂਵਾਂ ਨਹੀਂ ਵਰਤਦਾ।

#### ਅਗੇਤਰ ਛੱਡੇ ਜਾਂਦੇ ਹਨ, ਮੁੜ ਨਹੀਂ ਲਿਖੇ ਜਾਂਦੇ

ਮੋਡੀਫਾਇਰ ਓਦੋਂ ਚੱਲਦੇ ਹਨ ਜਦੋਂ ਪਾਰਸਰ ਨੇ ਅਜੇ ਕੁਝ ਨਹੀਂ ਲਾਹਿਆ ਹੁੰਦਾ, ਸੋ ਕੱਚੇ ਇਨਪੁਟ ਵਿੱਚ ਅਜੇ ਵੀ ਸਹਿ-ਸੰਬੰਧ ID, AIM ਕੋਡ ID ਤੇ ECI ਸੰਕੇਤਕ ਹੋ ਸਕਦੇ ਹਨ। ਦੋਵੇਂ ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ ਪਾਰਸਰ ਦੇ ਆਪਣੇ `CorrelationIdParser` ਤੇ `DataCarrierParser` ਤਰਕ ਨਾਲ ਹੀ AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਦਾ ਮੁੱਢ ਲੱਭਦੇ ਹਨ, ਓਥੋਂ ਹੀ ਅੱਗੇ ਮੁੜ ਲਿਖਦੇ ਹਨ, ਤੇ ਨਤੀਜੇ ਨੂੰ **ਬਿਨਾਂ ਛੂਹੇ** ਅਗੇਤਰ ਨਾਲ ਮੁੜ ਜੋੜ ਦਿੰਦੇ ਹਨ:

```java
new ModifierRemoveAIBrackets().modify("12345678~]d2\\000004(01)09521234543213(10)ABC");
// 12345678~]d2\000004010952123454321310ABC
//  ^^^^^^^^^^^^^^^^^^^ preserved verbatim
```

ਜਿਹਨਾਂ EAN/UPC ਵਾਹਕਾਂ ਦਾ ਮੁੱਲ GTIN-14 ਤੱਕ ਭਰਿਆ ਜਾਂਦਾ ਹੈ (`isRequiresGtinPadding()`), ਉਹ ਪੂਰੇ ਦੇ ਪੂਰੇ ਛੱਡ ਦਿੱਤੇ ਜਾਂਦੇ ਹਨ — ਉਹਨਾਂ ਦਾ ਪੇਲੋਡ ਬਿਨਾਂ ਕਿਸੇ AI ਬਣਤਰ ਦਾ ਕੱਚਾ ਸੰਖਿਅਕ ਬਾਰਕੋਡ ਮੁੱਲ ਹੁੰਦਾ ਹੈ, ਸੋ ਓਥੇ ਨਾ ਬਰੈਕਟ ਕੋਈ ਅਰਥ ਰੱਖ ਸਕਦੇ ਹਨ ਨਾ ਥਾਂਵਾਂ।

#### ਤਰਤੀਬ: ਬਰੈਕਟਾਂ ਤੋਂ ਪਹਿਲਾਂ ਥਾਂਵਾਂ

ਜਦੋਂ ਦੋਵੇਂ ਵਰਤੋ, ਤਾਂ **ਪਹਿਲਾਂ `ModifierRemoveSpaces` ਦਰਜ ਕਰੋ**। ਬਰੈਕਟਾਂ ਦਾ ਮੇਲ ਥਾਂ ਉੱਤੇ ਨਿਰਭਰ ਹੈ: ਥਾਂਵਾਂ ਨਾਲ ਖਿੱਲਰਿਆ `( 01 )` `\((\d{2,4})\)` ਨਾਲ ਨਹੀਂ ਮਿਲਦਾ, ਸੋ ਬਰੈਕਟ ਬਚੇ ਰਹਿੰਦੇ ਹਨ ਤੇ ਜਿਸ ਵਿਭਾਜਕ ਵੱਲ ਉਹ ਇਸ਼ਾਰਾ ਕਰਦੇ ਸਨ ਉਹ ਕਦੇ ਵਾਪਸ ਨਹੀਂ ਆਉਂਦਾ।

```java
// Correct — spaces, then brackets
new ModifierRemoveAIBrackets().modify(new ModifierRemoveSpaces().modify("( 01 )09521234543213( 10 )ABC"));
// 010952123454321310ABC

// Wrong way round — the brackets never match, and nothing useful happens
new ModifierRemoveSpaces().modify(new ModifierRemoveAIBrackets().modify("( 01 )09521234543213( 10 )ABC"));
// (01)09521234543213(10)ABC
```

### ਮੋਡੀਫਾਇਰ ਲਿਖਣਾ

ਜਦੋਂ ਦੋਹਾਂ ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰਾਂ ਵਿੱਚੋਂ ਕੋਈ ਵੀ ਢੁਕਵਾਂ ਨਾ ਹੋਵੇ ਤਾਂ ਆਪਣਾ ਲਿਖੋ — ਇੰਟਰਫੇਸ ਵਿੱਚ ਸਿਰਫ਼ ਇੱਕੋ ਵਿਧੀ ਹੈ।

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

ਜੇ ਮੁੜ-ਲਿਖਾਈ ਪਾਰਸ ਸੈਟਿੰਗ ਉੱਤੇ ਨਿਰਭਰ ਹੋਵੇ, ਤਾਂ ਇਸ ਦੀ ਥਾਂ ਦੋ-ਦਲੀਲਾਂ ਵਾਲਾ ਰੂਪ ਓਵਰਰਾਈਡ ਕਰੋ:

```java
@Override
public String modify(String input, ParseConfig config) {
    return config.getLanguage() == Language.ENGLISH ? input : normalise(input);
}
```

ਇਕਰਾਰ:

| ਨਿਯਮ | ਵੇਰਵਾ |
|---|---|
| ਹਾਲਤ-ਰਹਿਤ ਤੇ ਥ੍ਰੈੱਡ-ਸੁਰੱਖਿਅਤ | ਹਰ ਕਲਾਸ ਦਾ ਇੱਕੋ ਨਮੂਨਾ ਸਾਂਭਿਆ ਜਾਂਦਾ ਹੈ ਤੇ ਹਰ ਪਾਰਸ ਵਿੱਚ ਸਾਂਝਾ ਹੁੰਦਾ ਹੈ। |
| ਬਿਨਾਂ ਦਲੀਲ ਵਾਲਾ ਜਨਤਕ ਕੰਸਟ੍ਰਕਟਰ | ਸਿਰਫ਼ ਓਦੋਂ ਲੋੜੀਂਦਾ ਜਦੋਂ ਮੋਡੀਫਾਇਰ ਨੂੰ ਕਲਾਸ ਦੇ ਨਾਮ ਨਾਲ ਸੱਦਿਆ ਜਾਵੇ। |
| `null` ਤੇ ਖ਼ਾਲੀ ਇਨਪੁਟ ਸੰਭਾਲੋ | ਲੜੀ ਚੱਲਣ ਤੋਂ ਪਹਿਲਾਂ ਪਾਰਸਰ ਇਹਨਾਂ ਨੂੰ ਛਾਣਦਾ ਨਹੀਂ। |
| `null` ਮੋੜਨ ਦਾ ਮਤਲਬ "ਕੋਈ ਬਦਲਾਅ ਨਹੀਂ" | ਪਿਛਲਾ ਮੁੱਲ ਹੀ ਅੱਗੇ ਤੁਰਦਾ ਹੈ। ਜਦੋਂ ਮੋਡੀਫਾਇਰ ਲਾਗੂ ਨਾ ਹੋਵੇ ਤਾਂ `input` ਜਿਉਂ ਦਾ ਤਿਉਂ ਮੋੜੋ। |
| ਅਪਵਾਦ ਸੁੱਟਣ ਨਾਲੋਂ ਜਿਉਂ ਦਾ ਤਿਉਂ ਮੋੜਨਾ ਬਿਹਤਰ | ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲਾ ਮੋਡੀਫਾਇਰ ਪਾਰਸਿੰਗ ਰੱਦ ਕਰ ਦਿੰਦਾ ਹੈ — ਵੇਖੋ [ਨਾਕਾਮੀ ਦੀ ਸੰਭਾਲ](#ਮਡਫਇਰ-ਦ-ਨਕਮ-ਦ-ਸਭਲ)। |
| `getName()` | `ModifierInfo` ਉੱਤੇ ਦੱਸੇ ਜਾਣ ਵਾਲੇ ਨਾਮ ਨੂੰ ਕਾਬੂ ਕਰਨ ਲਈ ਓਵਰਰਾਈਡ ਕਰੋ; ਮੂਲ ਹੈ ਸਾਦਾ ਕਲਾਸ ਨਾਮ। |

### ਮੋਡੀਫਾਇਰ ਦਰਜ ਕਰਨਾ

ਮੋਡੀਫਾਇਰ ਓਸੇ ਤਰਤੀਬ ਵਿੱਚ ਚੱਲਦੇ ਹਨ ਜਿਸ ਵਿੱਚ ਤੁਸੀਂ ਜੋੜੋ, ਤੇ ਹਰ ਇੱਕ ਨੂੰ ਪਿਛਲੇ ਦਾ ਨਤੀਜਾ ਮਿਲਦਾ ਹੈ। ਇਹਨਾਂ ਨੂੰ ਨਮੂਨੇ ਰਾਹੀਂ, ਪੂਰੇ ਕਲਾਸ ਨਾਮ ਰਾਹੀਂ, ਜਾਂ ਇਹਨਾਂ ਵਿੱਚੋਂ ਕਿਸੇ ਦੀ ਸੂਚੀ ਰਾਹੀਂ ਦਰਜ ਕਰੋ:

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

[ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰਾਂ](#ਅਦਰਨ-ਮਡਫਇਰ) ਨੂੰ ਵੀ ਤੁਹਾਡੇ ਆਪਣਿਆਂ ਵਾਂਗ ਹੀ ਨਾਮ ਦਿੱਤਾ ਜਾਂਦਾ ਹੈ — **ਹਮੇਸ਼ਾ ਪੂਰੇ ਨਾਮ ਨਾਲ**। ਉਹਨਾਂ ਲਈ ਕੋਈ ਛੋਟਾ ਨਾਮ ਜਾਂ ਉਪਨਾਮ ਖੋਜ ਨਹੀਂ; `ModifierRegistry` ਹਰ ਮੋਡੀਫਾਇਰ ਨੂੰ, ਭਾਵੇਂ ਉਹ ਨਾਲ ਆਇਆ ਹੋਵੇ ਜਾਂ ਨਾ, ਪੂਰੇ ਕਲਾਸ ਨਾਮ ਨਾਲ ਹੀ ਹੱਲ ਕਰਦਾ ਹੈ।

ਨਾਮ `ModifierRegistry` ਹੱਲ ਕਰਦਾ ਹੈ; ਹਰ ਕਲਾਸ ਦਾ ਇੱਕ ਨਮੂਨਾ ਉਸ ਦੇ ਬਿਨਾਂ-ਦਲੀਲ ਕੰਸਟ੍ਰਕਟਰ ਨਾਲ ਇੱਕੋ ਵਾਰ ਬਣਾਉਂਦਾ ਹੈ ਤੇ ਓਸੇ ਕਲਾਸ ਦਾ ਨਾਮ ਲੈਣ ਵਾਲੀ ਹਰ ਅਗਲੀ ਸੈਟਿੰਗ ਲਈ ਉਹੀ ਸਾਂਭ ਰੱਖਦਾ ਹੈ। ਇਹ ਹੱਲ **ਸੈਟਿੰਗ ਬਣਨ ਵੇਲੇ** ਹੁੰਦਾ ਹੈ, ਸੋ ਜੋ ਨਾਮ ਨਾ ਲੱਭੇ, ਜੋ `ModifierInterface` ਲਾਗੂ ਨਾ ਕਰਦਾ ਹੋਵੇ, ਜਾਂ ਜਿਸ ਦਾ ਨਮੂਨਾ ਨਾ ਬਣ ਸਕੇ, ਉਹ ਓਥੇ ਹੀ `IllegalArgumentException` ਸੁੱਟਦਾ ਹੈ — ਪਾਰਸ ਵੇਲੇ ਚੁੱਪ-ਚਾਪ ਨਹੀਂ। ਜਿਹੜਾ ਮੋਡੀਫਾਇਰ ਪਰਾਵਰਤਨ ਨਾਲ ਨਾ ਬਣ ਸਕਦਾ ਹੋਵੇ (ਜਿਵੇਂ ਕੋਈ ਟੀਕਾ ਲਗਾਈ ਨਿਰਭਰਤਾ ਚੁੱਕੀ ਬੈਠਾ), ਉਸ ਨੂੰ ਪਹਿਲਾਂ ਹੀ ਦਰਜ ਕਰ ਕੇ ਨਾਮ ਨਾਲ ਸੱਦਣਯੋਗ ਰੱਖਿਆ ਜਾ ਸਕਦਾ ਹੈ:

```java
ModifierRegistry.INSTANCE.register(new LookupModifier(myDependency));
```

### ਵੇਖਣਾ ਕਿ ਮੋਡੀਫਾਇਰ ਨੇ ਕੀ ਕੀਤਾ

ਜਦੋਂ ਮੋਡੀਫਾਇਰ ਸੈੱਟ ਹੋਣ, ਤਾਂ `ParseResult.getPayload()` **ਬਦਲਿਆ ਹੋਇਆ** ਇਨਪੁਟ ਵਿਖਾਉਂਦਾ ਹੈ। ਅਸਲੀ `ModifierInfo` ਉੱਤੇ ਸਾਂਭਿਆ ਰਹਿੰਦਾ ਹੈ:

```java
ParseResult r = parser.parse("SCAN:0109506000134352", config);

r.isInputModified();                              // true
r.getPayload();                                   // "0109506000134352"  — what was parsed
r.getModifierInfo().getOriginalInput();           // "SCAN:0109506000134352"  — what was submitted
r.getModifierInfo().getAppliedModifiers();        // ["StripVendorWrapperModifier"]
```

`getAppliedModifiers()` ਹਰ ਮੋਡੀਫਾਇਰ ਦਾ `getName()` ਦੱਸਦਾ ਹੈ, ਜਿਸ ਦਾ ਮੂਲ ਸਾਦਾ ਕਲਾਸ ਨਾਮ ਹੈ ਪਰ ਦੋਵੇਂ ਅੰਦਰੂਨੀ ਮੋਡੀਫਾਇਰ ਉਸ ਨੂੰ ਓਵਰਰਾਈਡ ਕਰਦੇ ਹਨ — ਸੋ ਇਹਨਾਂ ਦੋਹਾਂ ਦੀ ਲੜੀ ਕਲਾਸ ਨਾਮਾਂ ਦੀ ਥਾਂ ਵਿਖਾਵੇ ਵਾਲੇ ਨਾਮ ਦੱਸਦੀ ਹੈ:

```java
ParseResult r = parser.parse("( 01 ) 09521234543213 ( 10 ) ABC123", config);
r.getModifierInfo().getAppliedModifiers();
// ["Remove Space Characters", "Remove Brackets Around AI"]
```

ਜਦੋਂ ਕੋਈ ਮੋਡੀਫਾਇਰ ਸੈੱਟ ਨਾ ਹੋਵੇ ਤਾਂ `getModifierInfo()` `null` ਮੋੜਦਾ ਹੈ। ਜੇ ਮੋਡੀਫਾਇਰ ਚੱਲੇ ਪਰ ਹਰ ਇੱਕ ਨੇ ਇਨਪੁਟ ਜਿਉਂ ਦਾ ਤਿਉਂ ਮੋੜ ਦਿੱਤਾ, ਤਾਂ ਜਾਣਕਾਰੀ ਮੌਜੂਦ ਰਹਿੰਦੀ ਹੈ ਪਰ `isModified()` `false` ਹੁੰਦਾ ਹੈ — `getAppliedModifiers()` ਵਿੱਚ ਸਿਰਫ਼ ਉਹੀ ਮੋਡੀਫਾਇਰ ਆਉਂਦੇ ਹਨ ਜਿਹਨਾਂ ਨੇ ਸੱਚਮੁੱਚ ਇਨਪੁਟ ਬਦਲਿਆ ਹੋਵੇ।

### ਮੋਡੀਫਾਇਰ ਦੀ ਨਾਕਾਮੀ ਦੀ ਸੰਭਾਲ

ਅਪਵਾਦ ਸੁੱਟਣ ਵਾਲਾ ਮੋਡੀਫਾਇਰ ਪਾਰਸਿੰਗ ਰੱਦ ਕਰ ਦਿੰਦਾ ਹੈ। ਉਹ ਅਪਵਾਦ ਇੱਕ `GaiaModifierException` ਵਿੱਚ ਲਪੇਟਿਆ ਜਾਂਦਾ ਹੈ ਜੋ ਦੋਸ਼ੀ ਮੋਡੀਫਾਇਰ ਦਾ ਨਾਮ ਦੱਸਦਾ ਹੈ, ਤੇ ਨਤੀਜਾ `GE-I001` ਅੰਦਰੂਨੀ ਗ਼ਲਤੀ ਚੁੱਕਦਾ ਹੈ ਜਿਸ ਦੇ ਸੁਨੇਹੇ ਵਿੱਚ ਉਹੀ ਨਾਮ ਹੁੰਦਾ ਹੈ; `getPayload()` ਬਿਨਾਂ ਬਦਲਿਆ ਇਨਪੁਟ ਦੱਸਦਾ ਹੈ। ਅੱਧੀ ਮੁੜ-ਲਿਖੀ ਸਟ੍ਰਿੰਗ ਨਾਲ ਪਾਰਸਿੰਗ ਜਾਣ-ਬੁੱਝ ਕੇ **ਅੱਗੇ ਨਹੀਂ ਵਧਦੀ** — ਚੁੱਪ-ਚਾਪ ਨਾਕਾਮ ਹੋਇਆ ਸਧਾਰਨੀਕਰਨ ਦਾ ਕਦਮ ਅਜਿਹੇ ਨਤੀਜੇ ਦਿੰਦਾ ਜੋ ਵੇਖਣ ਨੂੰ ਜਾਇਜ਼ ਲੱਗਦੇ ਪਰ ਗ਼ਲਤ ਇਨਪੁਟ ਤੋਂ ਪਾਰਸ ਕੀਤੇ ਹੋਏ ਹੁੰਦੇ।

---

## ਪਾਰਸ ਢੰਗ

ਹਰ ਢੰਗ ਦਾ ਨਾਮ ਉਸ ਸਭ ਤੋਂ ਡੂੰਘੇ [ਪਾਈਪਲਾਈਨ ਪੜਾਅ](#ਪਰਸਗ-ਪਈਪਲਈਨ) ਉੱਤੇ ਹੈ ਜੋ ਉਹ ਚਲਾਉਂਦਾ ਹੈ; ਉਸ ਤੋਂ ਪਹਿਲਾਂ ਦਾ ਹਰ ਪੜਾਅ ਫਿਰ ਵੀ ਚੱਲਦਾ ਹੈ।

| ਢੰਗ | ਕਿੱਥੋਂ ਤੱਕ ਚੱਲਦਾ ਹੈ | ਕਿਸ ਦਾ ਜਵਾਬ ਦਿੰਦਾ ਹੈ |
|---|---|---|
| `DATA_CARRIER` | ਪੜਾਅ 1 (ਇਨਪੁਟ ਦੀ ਦਿਸ਼ਾ) | ਇਸ ਨੂੰ ਕਿਹੜੀ ਸਿੰਬੋਲੋਜੀ ਚੁੱਕ ਕੇ ਲਿਆਈ? |
| `SYNTAX` | ਪੜਾਅ 2 (ਵਾਕ-ਬਣਤਰ) | AI ਕੋਡ ਤੇ ਲੰਬਾਈਆਂ ਠੀਕ ਬਣੀਆਂ ਹਨ? |
| `CONTENT` | ਪੜਾਅ 3 (ਸਮੱਗਰੀ) | ਮੁੱਲ ਜਾਇਜ਼ GS1 ਡਾਟਾ ਹਨ? |
| `INTERPRETATION` | ਪੜਾਅ 4 (ਵਿਆਖਿਆ) | ਮੁੱਲਾਂ ਦਾ ਮਤਲਬ ਕੀ ਹੈ? |

### DATA_CARRIER ਢੰਗ

ਪੜਾਅ 1 ਮਗਰੋਂ ਰੁਕ ਜਾਂਦਾ ਹੈ — AIM ਕੋਡ ID ਤਸਦੀਕ ਕਰਦਾ ਹੈ ਤੇ ਸਿੰਬੋਲੋਜੀ ਪਛਾਣਦਾ ਹੈ, ਪਰ AI ਪਾਰਸਿੰਗ ਪਾਈਪਲਾਈਨ ਵਿੱਚ ਨਹੀਂ ਵੜਦਾ। ਪੂਰੀ ਤਸਦੀਕ ਦੇ ਭਾਰ ਤੋਂ ਬਿਨਾਂ ਸਿੰਬੋਲੋਜੀ ਪਛਾਣਨ ਤੇ ਦਿਸ਼ਾ ਦੇਣ ਲਈ ਲਾਭਦਾਇਕ।

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

**ਕਦੋਂ ਵਰਤੋ:** ਜਦੋਂ ਪੇਲੋਡ ਕਿਵੇਂ ਸੰਭਾਲਣਾ ਹੈ ਇਹ ਤੈਅ ਕਰਨ ਤੋਂ ਪਹਿਲਾਂ ਤੁਹਾਡੀ ਐਪਲੀਕੇਸ਼ਨ ਨੂੰ ਬਾਰਕੋਡ ਦੀ ਕਿਸਮ ਜਾਣਨੀ ਪਵੇ — ਜਿਵੇਂ 1D ਤੇ 2D ਸਿੰਬੋਲੋਜੀਆਂ ਨੂੰ ਵੱਖ-ਵੱਖ ਸੰਭਾਲਣ ਵਾਲਿਆਂ ਕੋਲ ਭੇਜਣਾ। ਉਸ ਦਿਸ਼ਾ ਲਈ `getName()` ਉੱਤੇ ਸਟ੍ਰਿੰਗ ਮਿਲਾਉਣ ਦੀ ਥਾਂ ਕਿਸਮ ਵਾਲਾ [`DataCarrierType`](#datacarrierentry-ਤ-datacarriertype) (`getDataCarrier().getDataCarrierType()`) ਵਰਤੋ।

---

### SYNTAX ਢੰਗ

ਪੜਾਅ 2 ਮਗਰੋਂ ਰੁਕ ਜਾਂਦਾ ਹੈ। ਸਮੱਗਰੀ ਤਸਦੀਕ ਦੇ ਖ਼ਰਚੇ ਤੋਂ ਬਿਨਾਂ ਬਣਤਰੀ ਛਾਣ-ਬੀਣ ਲਈ ਲਾਭਦਾਇਕ।

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

**ਕਦੋਂ ਵਰਤੋ:** ਜਦੋਂ ਪੂਰੀ ਤਸਦੀਕ ਵਿੱਚ ਪੈਣ ਤੋਂ ਪਹਿਲਾਂ ਵੇਖਣਾ ਹੋਵੇ ਕਿ AI ਕੋਡ ਤੇ ਡਾਟੇ ਦੀਆਂ ਲੰਬਾਈਆਂ ਠੀਕ ਹਨ, ਜਾਂ ਜਦੋਂ ਵੱਡੀ ਗਿਣਤੀ ਵਿੱਚ ਸਕੈਨ ਕਰ ਰਹੇ ਹੋਵੋ ਜਿੱਥੇ ਸਮੱਗਰੀ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਘੱਟ ਹੀ ਹੁੰਦੀਆਂ ਹਨ।

---

### CONTENT ਢੰਗ

ਪੜਾਅ 3 ਮਗਰੋਂ ਰੁਕ ਜਾਂਦਾ ਹੈ।

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

> ਬਹੁਤੇ AI ਇਕੱਲੇ ਖੜ੍ਹੇ ਨਹੀਂ ਹੋ ਸਕਦੇ: AI `10` (BATCH/LOT), `17` (USE BY or EXPIRY) ਅਤੇ
> `21` (SERIAL) — ਹਰ ਇੱਕ ਨੂੰ ਓਸੇ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ AI `01` ਵਰਗੀ ਪਛਾਣ ਕੁੰਜੀ *ਚਾਹੀਦੀ ਹੈ*;
> ਸੋ ਉੱਪਰਲੀ ਮਿਸਾਲ ਵਿੱਚੋਂ GTIN ਕੱਢ ਦਿਓ ਤਾਂ ਸਮੱਗਰੀ ਤਸਦੀਕ ਤੱਕ ਪੁੱਜਣ ਤੋਂ ਪਹਿਲਾਂ ਹੀ ਪੜਾਅ 2
> ਵਿੱਚ `GE-S005` ਨਾਲ ਨਾਕਾਮੀ ਹੋਵੇਗੀ। ਜੋ ਟੋਟੇ ਜਾਣ-ਬੁੱਝ ਕੇ ਆਪਣੇ ਸਾਥੀ AI ਤੋਂ ਬਿਨਾਂ ਹੋਣ,
> ਉਹਨਾਂ ਨੂੰ ਪਾਰਸ ਕਰਨ ਲਈ `ParseConfig` ਉੱਤੇ `skipRequiresCheck(true)` ਸੈੱਟ ਕਰੋ।

**ਕਦੋਂ ਵਰਤੋ:** ਜਦੋਂ ਸਕੈਨ ਕੀਤੇ ਮੁੱਲ ਨੂੰ ਕਾਰੋਬਾਰੀ ਅਮਲ ਵਿੱਚ ਵਰਤਣ ਤੋਂ ਪਹਿਲਾਂ ਜਾਣਨਾ ਹੋਵੇ ਕਿ ਉਹ ਪੂਰੀ ਤਰ੍ਹਾਂ GS1-ਅਨੁਸਾਰੀ ਹੈ ਜਾਂ ਨਹੀਂ, ਪਰ ਵਿਆਖਿਆ-ਭਰਪੂਰਤਾ ਦਾ ਭਾਰ ਨਾ ਚਾਹੀਦਾ ਹੋਵੇ।

---

### INTERPRETATION ਢੰਗ (ਮੂਲ)

ਪੜਾਅ 4 ਤੱਕ ਪੂਰੀ ਪਾਈਪਲਾਈਨ ਚਲਾਉਂਦਾ ਹੈ। ਢੰਗ ਵਾਲੀ ਦਲੀਲ ਤੋਂ ਬਿਨਾਂ `parse(String)` ਸੱਦਣ ਉੱਤੇ ਇਹੀ ਮੂਲ ਹੈ। ਇਹ ਸਿਰਫ਼ ਉਹਨਾਂ ਐਲੀਮੈਂਟਾਂ ਨੂੰ ਭਰਪੂਰ ਕਰਦਾ ਹੈ ਜੋ ਸਮੱਗਰੀ ਤਸਦੀਕ ਸਾਫ਼-ਸੁਥਰੀ ਲੰਘੇ ਹੋਣ।

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

**ਮਿਸਾਲੀ ਨਤੀਜਾ:**
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

**ਮੁਦਰਾ ਰਕਮ ਦੀ ਮਿਸਾਲ (AI 3932 — ISO ਮੁਦਰਾ ਕੋਡ ਸਮੇਤ ਕੀਮਤ):**
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

**ਕਦੋਂ ਵਰਤੋ:** ਵਿਖਾਵੇ ਵਾਲੀਆਂ ਪਰਤਾਂ, ਲੇਬਲ ਪਰਖਣ ਵਾਲੇ ਸੰਦ, ਜਾਂ ਕੋਈ ਵੀ ਅਜਿਹਾ UI ਬਣਾਉਂਦਿਆਂ ਜਿਸ ਨੂੰ AI ਮੁੱਲਾਂ ਦਾ ਬੰਦੇ-ਪੱਖੀ ਨਿਖੇੜਾ ਚਾਹੀਦਾ ਹੋਵੇ।

---

## ਸਹਿ-ਸੰਬੰਧ ID

ਕੁਝ ਕੰਮ-ਵਹਾਅ ਕੱਚੀ GS1 ਇਨਪੁਟ ਤੋਂ ਪਹਿਲਾਂ ਆਪਣਾ ਖ਼ਾਸ 8-ਅੰਕੀ ਸਹਿ-ਸੰਬੰਧ ਪਛਾਣਕਾਰ ਲਾ ਦਿੰਦੇ ਹਨ ਤਾਂ ਜੋ ਸਕੈਨ ਦੀਆਂ ਘਟਨਾਵਾਂ ਨੂੰ ਕਿਸੇ ਸੈਸ਼ਨ ਜਾਂ ਲੈਣ-ਦੇਣ ਨਾਲ ਜੋੜਿਆ ਜਾ ਸਕੇ। ਰੂਪ ਇਹ ਹੈ:

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

`~` (ਟਿਲਡਾ) ਵਿਭਾਜਕ ਹੈ। ਇਹ GS1 ਸਮੱਗਰੀ ਦਾ ਹਿੱਸਾ **ਨਹੀਂ** — GS1 ਦੀ ਕੋਈ ਵੀ ਪਾਰਸਿੰਗ ਸ਼ੁਰੂ ਹੋਣ ਤੋਂ ਪਹਿਲਾਂ ਹੀ ਇਹ ਲਾਹ ਦਿੱਤਾ ਜਾਂਦਾ ਹੈ।

### ਪਛਾਣ ਦੇ ਨਿਯਮ

ਅਗੇਤਰ ਓਦੋਂ ਪਛਾਣਿਆ ਜਾਂਦਾ ਹੈ ਜਦੋਂ ਇਨਪੁਟ ਠੀਕ 8 ASCII ਦਸ਼ਮਲਵ ਅੰਕਾਂ (`0`–`9`) ਨਾਲ ਸ਼ੁਰੂ ਹੋਵੇ ਤੇ ਉਸ ਤੋਂ ਤੁਰੰਤ ਬਾਅਦ `~` ਹੋਵੇ। ਜੇ 9ਵਾਂ ਅੱਖਰ `~` ਨਾ ਹੋਵੇ, ਜਾਂ ਪਹਿਲੇ 8 ਅੱਖਰਾਂ ਵਿੱਚੋਂ ਕੋਈ ਵੀ ਅੰਕ ਨਾ ਹੋਵੇ, ਤਾਂ ਇਨਪੁਟ ਨੂੰ ਬਿਨਾਂ ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ ਵਾਲੀ ਸਾਦੀ GS1 ਸਮੱਗਰੀ ਮੰਨਿਆ ਜਾਂਦਾ ਹੈ।

### ਸਹਿ-ਸੰਬੰਧ ID ਤੱਕ ਪਹੁੰਚ

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

### AIM ਕੋਡ ID ਨਾਲ ਮਿਲਾ ਕੇ

ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ AIM ਕੋਡ ID ਤੋਂ ਪਹਿਲਾਂ ਆ ਸਕਦਾ ਹੈ। ਪਾਰਸਰ ਇਸ ਨੂੰ ਬਿਨਾਂ ਕਿਸੇ ਵਾਧੂ ਜਤਨ ਦੇ ਸੰਭਾਲ ਲੈਂਦਾ ਹੈ:

```java
// Correlation prefix + GS1-128 AIM Code ID
ParseResult response = parser.parse("12345678~]C10109506000134352");

System.out.println(response.hasCorrelationId());              // true
System.out.println(response.getCorrelationInfo().getId());    // "12345678"
System.out.println(response.hasDataCarrier());                // true
System.out.println(response.getDataCarrier().getAimCodeId()); // "]C1"
```

**ਅਮਲ ਕਰਨ ਵਾਲੀ ਕਲਾਸ:** `tools.pantheum.gaia.correlation.CorrelationIdParser`

---

## GS1 ਡਿਜੀਟਲ ਲਿੰਕ

**GS1 ਡਿਜੀਟਲ ਲਿੰਕ** ਇੱਕ ਜਾਂ ਵੱਧ AI ਮੁੱਲਾਂ ਨੂੰ ਸਿੱਧਾ HTTP(S) URL ਦੀ ਬਣਤਰ ਵਿੱਚ ਏਨਕੋਡ ਕਰਦਾ ਹੈ, ਜਿਸ ਨਾਲ ਭੌਤਿਕ ਵਸਤਾਂ ਲਈ ਵੈੱਬ-ਹੱਲ-ਯੋਗ ਪਛਾਣਕਾਰ ਮਿਲਦੇ ਹਨ। GAIA **ਅਣ-ਨਪੀੜੇ** URI ਲਈ *GS1 Digital Link Standard: URI Syntax* (ਰਿਲੀਜ਼ 1.7.0) ਲਾਗੂ ਕਰਦਾ ਹੈ।

```
https://example.com/01/09506000134352/10/ABC?17=271231
                    ^^  ^^^^^^^^^^^^^^  ^^ ^^^  ^^^^^^^^
                    AI  GTIN value      AI  │   AI 17 value (query)
                                        10  │
                                            batch/lot value
```

`GaiaParser` ਡਿਜੀਟਲ ਲਿੰਕ URI ਆਪ ਹੀ ਪਛਾਣ ਲੈਂਦਾ ਹੈ — `http://` ਜਾਂ `https://` ਨਾਲ ਸ਼ੁਰੂ ਹੋਣ ਵਾਲੀ ਹਰ ਇਨਪੁਟ `GS1DLParser` ਵੱਲ ਭੇਜੀ ਜਾਂਦੀ ਹੈ, ਜੋ ਓਹੀ ਸਮੱਗਰੀ ਤੇ ਵਿਆਖਿਆ ਵਾਲੇ ਪੜਾਅ ਚਲਾਉਂਦਾ ਹੈ ਜੋ ਐਲੀਮੈਂਟ-ਸਟ੍ਰਿੰਗ ਪਾਈਪਲਾਈਨ ਚਲਾਉਂਦੀ ਹੈ।

### URI ਦੀ ਬਣਤਰ ਤੇ AI ਦੀਆਂ ਭੂਮਿਕਾਵਾਂ

ਡਿਜੀਟਲ ਲਿੰਕ URI ਵਿੱਚ ਹਰ AI ਤਿੰਨ ਭੂਮਿਕਾਵਾਂ ਵਿੱਚੋਂ ਇੱਕ ਨਿਭਾਉਂਦਾ ਹੈ, ਜੋ ਹਰ `GS1AIObjectElement` ਉੱਤੇ `getDigitalLinkAIType()` (`GS1Constants.DigitalLinkAIType`) ਰਾਹੀਂ ਮਿਲਦੀ ਹੈ:

| ਭੂਮਿਕਾ | ਥਾਂ | ਮਿਸਾਲ |
|---|---|---|
| `PRIMARY_IDENTIFICATION_KEY` | ਰਾਹ ਦਾ ਪਹਿਲਾ `/ai/value` ਜੋੜਾ (§4.3) | `/01/09506000134352` |
| `KEY_QUALIFIER` | ਅਗਲੇ ਰਾਹ-ਜੋੜੇ, ਮੁੱਖ ਕੁੰਜੀ ਮੁਤਾਬਕ ਤਰਤੀਬ ਵਿੱਚ (§4.9) | `/10/ABC`, `/21/SER` |
| `DATA_ATTRIBUTE` | ਨਿਰੋਲ ਸੰਖਿਅਕ ਕੁੰਜੀਆਂ ਵਾਲੇ ਸਵਾਲ-ਪੈਰਾਮੀਟਰ (§4.10) | `?17=271231` |

ਲਾਗੂ ਹੋਣ ਵਾਲੇ ਬਣਤਰੀ ਨਿਯਮ (`DLPathRules`):
- ਰਾਹ ਵਿੱਚ ਠੀਕ **ਇੱਕੋ** ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀ; ਵਾਧੂ ਕੁੰਜੀਆਂ ਸਵਾਲ ਵਾਲੇ ਡਾਟਾ ਗੁਣਾਂ ਵਜੋਂ ਏਨਕੋਡ ਹੋਣੀਆਂ ਚਾਹੀਦੀਆਂ ਹਨ।
- ਕੁੰਜੀ ਯੋਗਤਾ-ਸੂਚਕ ਮੁੱਖ ਕੁੰਜੀ ਵੱਲੋਂ ਮੰਨਜ਼ੂਰ ਹੋਣੇ ਚਾਹੀਦੇ ਹਨ ਤੇ ਦੱਸੀ ਤਰਤੀਬ ਵਿੱਚ ਆਉਣੇ ਚਾਹੀਦੇ ਹਨ। ਮਰਜ਼ੀ ਵਾਲੇ ਯੋਗਤਾ-ਸੂਚਕ ਛੱਡੇ ਜਾ ਸਕਦੇ ਹਨ, ਪਰ ਜੋ *ਮੌਜੂਦ* ਹੋਣ ਉਹਨਾਂ ਨੂੰ ਪੱਕੀ ਤਰਤੀਬ ਮੰਨਣੀ ਪਵੇਗੀ — ਵੇਖੋ [ਯੋਗਤਾ-ਸੂਚਕਾਂ ਦੀ ਤਰਤੀਬ](#ਯਗਤ-ਸਚਕ-ਦ-ਤਰਤਬ)।
- ਮੁੱਖ ਕੁੰਜੀ ਤੋਂ ਪਹਿਲਾਂ ਮਨਮਰਜ਼ੀ ਦੇ ਖ਼ਾਸ ਰਾਹ-ਖੰਡ ਆ ਸਕਦੇ ਹਨ (ਜਿਵੇਂ `/products/au/01/...`); ਇਹ `getDigitalLinkInfo().getCustomPathStem()` ਰਾਹੀਂ ਲਵੋ।
- ਜੋ ਸਵਾਲ-ਕੁੰਜੀਆਂ ਸੰਖਿਅਕ ਨਹੀਂ (`linkType`, `context`, ਤੇ `23P` ਵਰਗੇ ਵਧਾਊ ਪੈਰਾਮੀਟਰ) ਉਹ ਅਣਗੌਲੀਆਂ ਕਰ ਦਿੱਤੀਆਂ ਜਾਂਦੀਆਂ ਹਨ; ਨਿਰੋਲ ਸੰਖਿਅਕ ਕੁੰਜੀਆਂ ਜਾਇਜ਼ AI ਹੋਣੀਆਂ ਚਾਹੀਦੀਆਂ ਹਨ ਜਿਹਨਾਂ ਉੱਤੇ `validAsDataAttribute` ਲੱਗਾ ਹੋਵੇ।
- ਪ੍ਰਤੀਸ਼ਤ-ਏਨਕੋਡ ਕੀਤੇ ਮੁੱਲ-ਅੱਖਰ ਡੀਕੋਡ ਕੀਤੇ ਜਾਂਦੇ ਹਨ; AI `(03)` ਤੇ `(8014)` ਦੀ ਇਜਾਜ਼ਤ ਨਹੀਂ।

ਮੁੱਖ ਕੁੰਜੀਆਂ ਤੇ ਉਹਨਾਂ ਦੀਆਂ ਮੰਨਜ਼ੂਰ ਯੋਗਤਾ-ਸੂਚਕ ਲੜੀਆਂ ਕੋਡ ਵਿੱਚ ਪੱਕੀਆਂ ਲਿਖੀਆਂ ਹੋਣ ਦੀ ਥਾਂ AI ਪਰਿਭਾਸ਼ਾਵਾਂ ਵਿੱਚੋਂ **ਡਾਟੇ ਤੋਂ ਚੱਲਦੀਆਂ** ਹਨ — `gs1DigitalLinkPrimaryKey` ਝੰਡੀ ਤੇ `gs1DigitalLinkQualifiers` ਗੁਣ ਰਾਹੀਂ।

ਕੋਈ ਵੀ ਬਣਤਰੀ ਉਲੰਘਣਾ, ਜਾਂ ਗ਼ੈਰ-URL ਇਨਪੁਟ, ਡਿਜੀਟਲ ਲਿੰਕ ਬਣਤਰੀ ਗ਼ਲਤੀ ਪੈਦਾ ਕਰਦੀ ਹੈ (`GE-L001`–`GE-L014`, ਹਰ ਹਾਲਤ ਲਈ ਇੱਕ ਕੋਡ)। ਵੱਖ ਕੀਤਾ URL ਵੇਰਵਾ (`scheme`, `domain`, `path`, `customPathStem`, `query`, ਤੇ `java.net.URL`) ਬਣਤਰੀ ਗ਼ਲਤੀਆਂ ਹੋਣ ਦੇ ਬਾਵਜੂਦ ਵੀ `getDigitalLinkInfo()` ਰਾਹੀਂ ਮਿਲਦਾ ਹੈ।

### ਯੋਗਤਾ-ਸੂਚਕਾਂ ਦੀ ਤਰਤੀਬ

ਹਰ ਮੁੱਖ ਕੁੰਜੀ ਲਈ `gs1DigitalLinkQualifiers` ਇੱਕ ਜਾਂ ਵੱਧ **ਤਰਤੀਬਬੱਧ** ਯੋਗਤਾ-ਸੂਚਕ ਲੜੀਆਂ ਦੱਸਦਾ ਹੈ। ਕਿਸੇ ਲੜੀ ਅੰਦਰ ਵਰਗਾਕਾਰ ਬਰੈਕਟਾਂ ਵਿੱਚ ਲਪੇਟਿਆ AI **ਮਰਜ਼ੀ ਵਾਲਾ** ਹੈ, ਬਿਨਾਂ ਬਰੈਕਟ ਵਾਲਾ AI **ਲਾਜ਼ਮੀ** — ਇਹ §4.9 ਦੇ ABNF ਦੇ `[cpv-comp]` ਚਿੰਨ੍ਹ-ਢੰਗ ਵਾਂਗ ਹੀ ਹੈ। ਇੱਕੋ ਮੁੱਖ ਕੁੰਜੀ ਦੀਆਂ ਲੜੀਆਂ ਆਪਸ ਵਿੱਚ ਵੱਖਰੇ, ਇੱਕ-ਦੂਜੇ ਨੂੰ ਰੱਦ ਕਰਨ ਵਾਲੇ ਬਦਲ ਹਨ।

ਮਿਸਾਲ ਵਜੋਂ GTIN (`01`) ਦੋ ਲੜੀਆਂ ਦੱਸਦਾ ਹੈ:

| ਰਾਹ | ਲੜੀ | ਮਤਲਬ |
|---|---|---|
| gtin-path | `[22]` → `[10]` → `[21]` | CPV, LOT, SER — ਹਰ ਇੱਕ ਮਰਜ਼ੀ ਵਾਲਾ, ਪਰ ਤਰਤੀਬ ਇਹੀ ਪੱਕੀ |
| upui-path | `235` | TPX (ਲਾਜ਼ਮੀ); GTIN + TPX = UPUI |

ਸੋ `/01/09506000134352/10/LOT-ABC/21/SER` ਜਾਇਜ਼ ਹੈ (LOT, SER ਤੋਂ ਪਹਿਲਾਂ; CPV ਛੱਡਿਆ), `/01/.../21/SER/10/LOT-ABC` **ਰੱਦ** ਹੁੰਦਾ ਹੈ (ਤਰਤੀਬ ਵਿਗੜੀ), ਤੇ `/01/09506000134352/235/2ABC456` upui-path ਹੈ। ਤਰਤੀਬ ਦੀ ਪਰਖ ਤਰਤੀਬ ਸਾਂਭਣ ਵਾਲਾ ਉਪ-ਲੜੀ ਮੇਲ ਹੈ, ਸੋ ਮਰਜ਼ੀ ਵਾਲੇ AI ਛੱਡੇ ਤਾਂ ਜਾ ਸਕਦੇ ਹਨ ਪਰ ਅੱਗੇ-ਪਿੱਛੇ ਕਦੇ ਨਹੀਂ ਕੀਤੇ ਜਾ ਸਕਦੇ।

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

**ਅਮਲ ਕਰਨ ਵਾਲੀ ਕਲਾਸ:** `tools.pantheum.gaia.gs1.GS1DLParser`

---

## ਨਤੀਜਿਆਂ ਨਾਲ ਕੰਮ ਕਰਨਾ

### ParseResult

`GaiaParser.parse()` ਵੱਲੋਂ ਮੋੜਿਆ ਸਭ ਤੋਂ ਉੱਪਰਲਾ ਨਤੀਜਾ।

| ਵਿਧੀ | ਕੀ ਮੋੜਦੀ ਹੈ | ਵੇਰਵਾ |
|---|---|---|
| `isValid()` | `boolean` | `true` ਜੇ ਕਿਸੇ ਵੀ ਦਰਜੇ ਉੱਤੇ ਕੋਈ ਗ਼ਲਤੀ ਨਾ ਹੋਵੇ। ਚੇਤਾਵਨੀਆਂ ਜਾਇਜ਼ਪੁਣੇ ਉੱਤੇ ਅਸਰ ਨਹੀਂ ਪਾਉਂਦੀਆਂ। ਜਦੋਂ `getAiObject()` `null` ਹੋਵੇ ਤਾਂ ਹਮੇਸ਼ਾ `true`। |
| `getPayload()` | `String` | ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ ਲਾਹੁਣ ਮਗਰੋਂ — ਤੇ ਕਿਸੇ ਵੀ [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਇਨਪਟ-ਮਡਫਇਰ) ਵੱਲੋਂ ਮੁੜ ਲਿਖੇ ਜਾਣ ਮਗਰੋਂ — ਦੀ ਇਨਪੁਟ ਸਟ੍ਰਿੰਗ। |
| `getPayloadContent()` | `String` | AIM ਕੋਡ ID ਤੇ ECI ਅਗੇਤਰ ਲਾਹਿਆ ਹੋਇਆ ਪੇਲੋਡ। |
| `getContentType()` | `GS1ContentType` | `GS1_APPLICATION_IDENTIFIERS`, `GS1_DIGITAL_LINK`, `DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL` (ਗ਼ੈਰ-GS1 ਵਜੋਂ ਰੱਦ ਕੀਤਾ ਡਾਟਾ ਵਾਹਕ, ਜਿਵੇਂ Code 39 ਦਾ `]A0` ਵਾਹਕ), ਜਾਂ `UNABLE_TO_DETERMINE_CONTENT` (ਜਦੋਂ `aiObject` `null` ਹੋਵੇ, ਜਿਵੇਂ `DATA_CARRIER` ਢੰਗ ਵਿੱਚ)। |
| `getRequestedParseMode()` | `ParseMode` | ਸੈੱਟ ਕੀਤੀ ਪਾਈਪਲਾਈਨ ਡੂੰਘਾਈ (`ParseConfig.getRequestedParseMode()`)। |
| `getAchievedParseMode()` | `ParseMode` | ਪਾਰਸ ਸੱਚਮੁੱਚ ਜਿੱਥੋਂ ਤੱਕ ਪੁੱਜਾ, ਉਹ ਸਭ ਤੋਂ ਡੂੰਘਾ ਪੜਾਅ — ਹੇਠਾਂ ਵੇਖੋ। |
| `isParseComplete()` | `boolean` | `true` ਜੇ ਪਾਰਸ ਮੰਗੀ ਡੂੰਘਾਈ ਤੱਕ ਪੁੱਜ ਗਿਆ (`achieved == requested`)। `isValid()` ਤੋਂ ਸੁਤੰਤਰ। |
| `getAiObject()` | `GS1AIObject` | ਸਾਰੇ ਹੱਲ ਕੀਤੇ AI। `DATA_CARRIER` ਢੰਗ ਵਿੱਚ `null`। |
| `getErrors()` | `List<GaiaError>` | ਸਾਰੀਆਂ ਗ਼ੈਰ-WARNING ਗ਼ਲਤੀਆਂ (ਵਸਤੂ-ਦਰਜਾ + ਸਾਰੇ ਐਲੀਮੈਂਟ-ਦਰਜਾ)। |
| `getWarnings()` | `List<GaiaError>` | ਸਾਰੀਆਂ WARNING ਸਲਾਹਾਂ (ਵਸਤੂ-ਦਰਜਾ + ਸਾਰੇ ਐਲੀਮੈਂਟ-ਦਰਜਾ)। |
| `hasWarnings()` | `boolean` | `true` ਜੇ ਕੋਈ WARNING ਸਲਾਹ ਉੱਠੀ ਹੋਵੇ। |
| `getIssues()` | `List<GaiaError>` | ਗ਼ਲਤੀਆਂ ਤੇ ਚੇਤਾਵਨੀਆਂ ਰਲਾ ਕੇ। |
| `hasDataCarrier()` | `boolean` | `true` ਜੇ ਕੋਈ AIM ਕੋਡ ID ਪਛਾਣਿਆ ਗਿਆ ਹੋਵੇ। |
| `getDataCarrier()` | `DataCarrierEntry` | ਸਿੰਬੋਲੋਜੀ ਦਾ ਵੇਰਵਾ, ਜਾਂ ਕੋਈ ਵਾਹਕ ਨਾ ਪਛਾਣੇ ਜਾਣ ਉੱਤੇ `null`। |
| `hasEci()` | `boolean` | `true` ਜੇ ਪੇਲੋਡ ਵਿੱਚੋਂ ਕੋਈ ECI ਸੰਕੇਤਕ ਲਾਹਿਆ ਗਿਆ ਹੋਵੇ। |
| `getEci()` | `EciEntry` | ECI ਏਨਕੋਡਿੰਗ ਦਾ ਵੇਰਵਾ, ਜਾਂ `null`। |
| `hasCorrelationId()` | `boolean` | `true` ਜੇ ਅਸਲੀ ਇਨਪੁਟ ਵਿੱਚ `DDDDDDDD~` ਸਹਿ-ਸੰਬੰਧ ਅਗੇਤਰ ਮੌਜੂਦ ਸੀ। |
| `getCorrelationInfo()` | `CorrelationInfo` | ਕੱਢਿਆ ਹੋਇਆ ਸਹਿ-ਸੰਬੰਧ ID, ਜਾਂ ਕੋਈ ਨਾ ਹੋਣ ਉੱਤੇ `null`। |
| `isInputModified()` | `boolean` | `true` ਜੇ ਕਿਸੇ [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰ](#ਇਨਪਟ-ਮਡਫਇਰ) ਨੇ ਇਨਪੁਟ ਬਦਲੀ ਹੋਵੇ। |
| `getModifierInfo()` | `ModifierInfo` | ਮੋਡੀਫਾਇਰ ਲੜੀ ਨੇ ਕੀ ਕੀਤਾ — `getOriginalInput()`, `getModifiedInput()`, `getAppliedModifiers()`। ਜੇ ਕੋਈ ਮੋਡੀਫਾਇਰ ਸੈੱਟ ਨਾ ਹੋਵੇ ਤਾਂ `null`। |
| `getTiming()` | `ProcessingTiming` | ਪਾਰਸ ਦਾ ਘੜੀ-ਸਮਾਂ — `getStartTime()` (`Instant`), `getProcessingTime()` (`Duration`), `getProcessingTimeMillis()` (`long`), `getCompletionTime()`। ਜੇ `GaiaParser` ਨੇ ਨਾ ਬਣਾਇਆ ਹੋਵੇ ਤਾਂ `null`। |
| `getVersion()` | `String` | ਉਹ ਲਾਇਬ੍ਰੇਰੀ ਵਰਜਨ ਜਿਸ ਨੇ ਨਤੀਜਾ ਬਣਾਇਆ। |

#### ਮੰਗਿਆ ਬਨਾਮ ਪ੍ਰਾਪਤ ਪਾਰਸ ਢੰਗ

ਪਾਈਪਲਾਈਨ **SYNTAX → CONTENT → INTERPRETATION** ਦੀ ਪੌੜੀ ਚੜ੍ਹਦੀ ਹੈ ਤੇ ਗ਼ਲਤੀ ਉੱਤੇ ਪਹਿਲਾਂ ਹੀ ਰੁਕ ਜਾਂਦੀ ਹੈ, ਸੋ ਜੋ ਢੰਗ ਸੱਚਮੁੱਚ *ਪ੍ਰਾਪਤ* ਹੋਇਆ ਉਹ *ਮੰਗੇ* ਢੰਗ ਨਾਲੋਂ ਘੱਟ ਡੂੰਘਾ ਹੋ ਸਕਦਾ ਹੈ। `getAchievedParseMode()` ਦੱਸਦਾ ਹੈ ਕਿ ਉਹ ਕਿੱਥੋਂ ਤੱਕ ਪੁੱਜੀ:

| ਮੰਗਿਆ | ਕੀ ਹੁੰਦਾ ਹੈ | ਪ੍ਰਾਪਤ | `isParseComplete()` |
|---|---|---|---|
| `CONTENT` / `INTERPRETATION` | ਟੋਕਨਾਈਜ਼ੇਸ਼ਨ ਮਗਰੋਂ ਕੋਈ **ਵਾਕ-ਬਣਤਰੀ / ਬਣਤਰੀ** ਗ਼ਲਤੀ ਪਾਰਸ ਰੋਕ ਦਿੰਦੀ ਹੈ | `SYNTAX` | `false` |
| `INTERPRETATION` | ਕੋਈ **ਸਮੱਗਰੀ** ਗ਼ਲਤੀ (ਗ਼ਲਤ ਰੂਪ/ਜਾਂਚ ਅੰਕ) ਭਰਪੂਰਤਾ ਰੋਕ ਦਿੰਦੀ ਹੈ | `CONTENT` | `false` |
| `CONTENT` | ਸਮੱਗਰੀ ਵਾਲਾ ਪੜਾਅ ਹਮੇਸ਼ਾ ਪੂਰਾ ਚੱਲਦਾ ਹੈ (ਗ਼ਲਤੀਆਂ ਦਰਜ ਹੁੰਦੀਆਂ ਹਨ, ਘਾਤਕ ਨਹੀਂ) | `CONTENT` | `true` |
| ਕੋਈ ਵੀ (ਸਾਫ਼ ਇਨਪੁਟ) | ਪਾਈਪਲਾਈਨ ਮੰਗੀ ਡੂੰਘਾਈ ਤੱਕ ਪੁੱਜ ਜਾਂਦੀ ਹੈ | = ਮੰਗਿਆ | `true` |
| `DATA_CARRIER` | ਵਾਹਕ ਤਸਦੀਕ ਹੋਇਆ; ਕੋਈ AI ਸਮੱਗਰੀ ਪਾਰਸ ਨਹੀਂ ਹੋਈ | `DATA_CARRIER` | `true` |
| ਕੋਈ ਵੀ | AI ਪਾਰਸਿੰਗ ਤੋਂ ਪਹਿਲਾਂ ਹੀ ਡਾਟਾ ਵਾਹਕ ਰੱਦ ਹੋ ਜਾਂਦਾ ਹੈ (ਜਿਵੇਂ ਗ਼ੈਰ-GS1 `]A0` ਵਾਹਕ) | `SYNTAX` | `false` |

`isParseComplete()` `isValid()` ਤੋਂ ਸੁਤੰਤਰ ਹੈ: ਗ਼ਲਤ ਜਾਂਚ ਅੰਕ ਵਾਲੇ GTIN ਦਾ `CONTENT` ਪਾਰਸ **ਪੂਰਾ** ਹੁੰਦਾ ਹੈ (ਸਮੱਗਰੀ ਵਾਲਾ ਪੜਾਅ ਚੱਲ ਗਿਆ) ਪਰ **ਨਾਜਾਇਜ਼** (ਜਾਂਚ ਅੰਕ ਨਾਕਾਮ ਰਿਹਾ)। "ਕੀ ਪਾਈਪਲਾਈਨ ਓਨੀ ਡੂੰਘੀ ਚੱਲੀ ਜਿੰਨੀ ਮੈਂ ਮੰਗੀ ਸੀ?" ਪੁੱਛਣ ਲਈ `isParseComplete()` ਵਰਤੋ, ਤੇ "ਕੀ ਡਾਟਾ ਠੀਕ ਬਣਿਆ ਹੈ?" ਪੁੱਛਣ ਲਈ `isValid()`।

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

ਹੱਲ ਕੀਤੇ AI ਐਲੀਮੈਂਟਾਂ ਦਾ ਸੰਗ੍ਰਹਿ।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getAis()` | ਇਨਪੁਟ ਦੀ ਤਰਤੀਬ ਵਿੱਚ ਸਾਰੇ `GS1AIObjectElement` ਨਮੂਨੇ। |
| `get(String aiCode)` | ਦਿੱਤੇ AI ਕੋਡ ਨਾਲ ਮਿਲਦਾ ਪਹਿਲਾ ਐਲੀਮੈਂਟ, ਜਾਂ `null`। |
| `contains(String aiCode)` | `true` ਜੇ ਉਸ ਕੋਡ ਵਾਲਾ AI ਮੌਜੂਦ ਹੋਵੇ। |
| `size()` | ਹੱਲ ਕੀਤੇ AI ਦੀ ਗਿਣਤੀ। |
| `isValid()` | `true` ਜੇ ਕੋਈ ਵਸਤੂ-ਦਰਜੇ ਦੀ ਗ਼ਲਤੀ ਨਾ ਹੋਵੇ ਤੇ ਕਿਸੇ ਐਲੀਮੈਂਟ ਉੱਤੇ ਗ਼ਲਤੀ ਨਾ ਹੋਵੇ। |
| `toHriString()` | HRI ਸਟ੍ਰਿੰਗ, ਜਿਵੇਂ `(01)09506000134352 (17)261231`। |
| `toElementString()` | ਕੱਚੀ ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ — ਬਿਨਾਂ ਬਰੈਕਟ, ਹਰ ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਐਲੀਮੈਂਟ ਮਗਰੋਂ FNC1 — ਜਿਵੇਂ `010950600013435210LOT-ABC<GS>17271231`। ਜੇ `isValid()` `false` ਹੋਵੇ ਤਾਂ `null` ਮੋੜਦੀ ਹੈ। |
| `getContentType()` | ਜਦੋਂ `hasDigitalLink()` ਸਹੀ ਹੋਵੇ ਤਾਂ `GS1_DIGITAL_LINK`, ਨਹੀਂ ਤਾਂ `GS1_APPLICATION_IDENTIFIERS`। |
| `hasDigitalLink()` | `true` ਜੇ ਇਨਪੁਟ ਮੁੱਖ ਪਛਾਣ ਕੁੰਜੀ ਵਾਲਾ GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI ਸੀ। ਜਿਸ ਠੀਕ-ਬਣੇ URL ਵਿੱਚ ਮੁੱਖ ਕੁੰਜੀ ਨਾ ਹੋਵੇ, ਉਹ `getDigitalLinkInfo()` ਤਾਂ ਦਿੰਦਾ ਹੈ ਪਰ ਇੱਥੇ `false` ਮੋੜਦਾ ਹੈ। |
| `getCanonicalDigitalLink()` | `https://id.gs1.org` ਉੱਤੇ ਮਿਆਰੀ GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI (§4.12) — ਮੁੱਖ ਕੁੰਜੀ ਤੇ ਯੋਗਤਾ-ਸੂਚਕ ਰਾਹ-ਖੰਡਾਂ ਵਜੋਂ, ਡਾਟਾ ਗੁਣ AI ਕੁੰਜੀ ਮੁਤਾਬਕ ਤਰਤੀਬੇ ਸਵਾਲ-ਪੈਰਾਮੀਟਰਾਂ ਵਜੋਂ — ਜਾਂ ਮੁੱਖ ਕੁੰਜੀ ਨਾ ਹੋਣ ਉੱਤੇ `null`। |
| `getDigitalLinkInfo()` | URI ਦੇ ਟੁਕੜਿਆਂ ਦਾ ਵੇਰਵਾ (`getUri()`, `getUrl()`, `scheme`, `domain`, `path`, `getCustomPathStem()`, `query`), ਜਾਂ ਡਿਜੀਟਲ ਲਿੰਕ ਨਾ ਹੋਣ ਉੱਤੇ `null`। |
| `getAllErrors()` | ਵਸਤੂ-ਦਰਜਾ + ਸਾਰੀਆਂ ਐਲੀਮੈਂਟ ਗ਼ਲਤੀਆਂ (ਗ਼ੈਰ-WARNING)। |
| `getAllWarnings()` | ਵਸਤੂ-ਦਰਜਾ + ਸਾਰੀਆਂ ਐਲੀਮੈਂਟ ਚੇਤਾਵਨੀਆਂ। |
| `getAllIssues()` | ਸਭ ਕੁਝ ਰਲਾ ਕੇ। |

---

### GS1AIObjectElement

ਇੱਕੋ ਹੱਲ ਕੀਤਾ AI ਨਮੂਨਾ।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getAi()` | AI ਕੋਡ, ਜਿਵੇਂ `"01"`, `"3102"`। |
| `getTitle()` | GS1 ਡਾਟਾ ਸਿਰਲੇਖ, ਜਿਵੇਂ `"GTIN"`, `"BATCH/LOT"`। |
| `getDescription()` | AI ਦਾ ਪੂਰਾ GS1 ਵੇਰਵਾ, **ਪਾਰਸ ਭਾਸ਼ਾ ਵਿੱਚ ਸਥਾਨਕ ਕੀਤਾ** (ਜਿਵੇਂ ਅੰਗਰੇਜ਼ੀ ਵਿੱਚ `"Global Trade Item Number (GTIN)"`)। ਅਨੁਵਾਦ ਨਾ ਹੋਣ ਉੱਤੇ AI ਪਰਿਭਾਸ਼ਾ ਵਾਲੇ ਅੰਗਰੇਜ਼ੀ ਪਾਠ ਉੱਤੇ ਮੁੜ ਆਉਂਦੀ ਹੈ। |
| `getFormatString()` | AI *ਤੇ* ਉਸ ਦੇ ਡਾਟੇ, ਦੋਹਾਂ ਨੂੰ ਢਕਣ ਵਾਲਾ ਰੂਪ-ਵਰਣਨ, ਜਿਵੇਂ AI `01` ਲਈ `"N2+N14"`, AI `10` ਲਈ `"N2+X..20"`, AI `3932` ਲਈ `"N4+N3+N..15"`। |
| `getValue()` | ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚੋਂ ਕੱਢਿਆ ਕੱਚਾ ਡਾਟਾ ਮੁੱਲ। |
| `isFixedLength()` | `true` ਜੇ AI ਦੀ ਡਾਟਾ ਲੰਬਾਈ ਸਥਿਰ ਹੋਵੇ। |
| `getPosition()` | ਅਸਲੀ ਇਨਪੁਟ ਵਿੱਚ ਸਿਫ਼ਰ-ਤੋਂ-ਸ਼ੁਰੂ ਹੋਣ ਵਾਲੀ ਅੱਖਰੀ ਥਾਂ। |
| `getGS1ComponentValues()` | ਹਰ ਹਿੱਸੇ ਦੇ ਮੁੱਲ ਦੇ ਟੋਟੇ (ਕਈ-ਹਿੱਸਿਆਂ ਵਾਲੇ AI ਲਈ)। |
| `getErrors()` | ਐਲੀਮੈਂਟ-ਦਰਜੇ ਦੀਆਂ ਗ਼ੈਰ-WARNING ਗ਼ਲਤੀਆਂ। |
| `getWarnings()` | ਐਲੀਮੈਂਟ-ਦਰਜੇ ਦੀਆਂ WARNING ਸਲਾਹਾਂ। |
| `getIssues()` | ਐਲੀਮੈਂਟ-ਦਰਜੇ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਤੇ ਚੇਤਾਵਨੀਆਂ ਰਲਾ ਕੇ। |
| `hasErrors()` | `true` ਜੇ ਕੋਈ ਗ਼ੈਰ-WARNING ਗ਼ਲਤੀ ਜੁੜੀ ਹੋਵੇ। |
| `hasWarnings()` | `true` ਜੇ ਕੋਈ WARNING ਸਲਾਹ ਜੁੜੀ ਹੋਵੇ। |
| `getInterpretations()` | `GS1AIInterpretation` ਦਰਜੇ (INTERPRETATION ਢੰਗ ਵਿੱਚ ਭਰੇ ਜਾਂਦੇ ਹਨ)। |
| `getInterpretation(String type)` | ਦਿੱਤੀ `GS1Constants_Enricher` ਕਿਸਮ-ਕੁੰਜੀ ਨਾਲ ਮਿਲਦੀ ਪਹਿਲੀ ਵਿਆਖਿਆ, ਜਾਂ `null`। |
| `getDigitalLinkAIType()` | ਐਲੀਮੈਂਟ ਦੀ ਡਿਜੀਟਲ ਲਿੰਕ ਭੂਮਿਕਾ (`PRIMARY_IDENTIFICATION_KEY`, `KEY_QUALIFIER`, `DATA_ATTRIBUTE`), ਜਾਂ ਐਲੀਮੈਂਟ-ਸਟ੍ਰਿੰਗ ਇਨਪੁਟ ਲਈ `null`। |
| `hasDigitalLinkAIType()` | `true` ਜੇ ਡਿਜੀਟਲ ਲਿੰਕ ਭੂਮਿਕਾ ਦਿੱਤੀ ਗਈ ਹੋਵੇ। |

---

### GaiaError

ਅਟੱਲ ਤਸਦੀਕ ਗ਼ਲਤੀ ਜਾਂ ਸਲਾਹ।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getId()` | ਸੂਚੀ-ਪਛਾਣਕਾਰ, ਜਿਵੇਂ `"GE-C003"`। |
| `getLevel()` | `SYNTAX_ERROR`, `INTEGRITY_ERROR`, `FORMAT_ERROR`, `DATA_ERROR`, ਜਾਂ `WARNING`। |
| `getStage()` | `DATA_CARRIER`, `DIGITAL_LINK`, `SYNTAX`, `CONTENT`, ਜਾਂ `INTERNAL`। |
| `getCode()` | ਮਸ਼ੀਨ-ਪੜ੍ਹਨਯੋਗ ਛੋਟਾ ਕੋਡ। |
| `getAi()` | ਗ਼ਲਤੀ ਦਾ ਕਾਰਨ ਬਣਿਆ AI ਕੋਡ, ਜਾਂ ਵਸਤੂ-ਦਰਜੇ ਦੀਆਂ ਗ਼ਲਤੀਆਂ ਲਈ `null`। |
| `getMessage()` | ਬੰਦੇ-ਪੜ੍ਹਨਯੋਗ, ਮੁੱਲ ਭਰਿਆ ਸੁਨੇਹਾ। |
| `getPosition()` | ਅਸਲੀ ਇਨਪੁਟ ਵਿੱਚ ਸਿਫ਼ਰ-ਤੋਂ-ਸ਼ੁਰੂ ਹੋਣ ਵਾਲੀ ਅੱਖਰੀ ਥਾਂ। |

---

### GS1AIInterpretation

ਇੱਕੋ ਲੇਬਲ ਵਾਲਾ ਵਿਆਖਿਆ ਟੁਕੜਾ, ਜੋ `INTERPRETATION` ਢੰਗ ਵਿੱਚ `GS1AIObjectElement` ਨਾਲ ਜੁੜਦਾ ਹੈ।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getType()` | ਮਸ਼ੀਨ-ਪੜ੍ਹਨਯੋਗ ਕਿਸਮ-ਕੁੰਜੀ, ਜਿਵੇਂ `"DATE_VALUE"`, `"GS1_COMPANY_PREFIX"`। ਹਰ ਭਾਸ਼ਾ ਵਿੱਚ ਇੱਕੋ ਜਿਹੀ ਰਹਿੰਦੀ ਹੈ। |
| `getLabel()` | ਬੰਦੇ-ਪੜ੍ਹਨਯੋਗ ਲੇਬਲ, **ਪਾਰਸ ਭਾਸ਼ਾ ਵਿੱਚ ਸਥਾਨਕ ਕੀਤਾ** (ਜਿਵੇਂ ਅੰਗਰੇਜ਼ੀ ਵਿੱਚ `"Date"` / `"GS1 company prefix"`)। |
| `getValue()` | ਕੱਢਿਆ/ਭਰਪੂਰ ਕੀਤਾ ਮੁੱਲ, ਜਿਵੇਂ `"31/12/2026"`, `"9506000"`। ਸਥਾਨਕ ਨਹੀਂ ਕੀਤਾ ਜਾਂਦਾ। |

---

### DataCarrierEntry ਤੇ DataCarrierType

ਜਦੋਂ ਇਨਪੁਟ ਨਾਲ AIM ਕੋਡ ID ਆਵੇ, ਤਾਂ `ParseResult.getDataCarrier()` ਇੱਕ `DataCarrierEntry` ਮੋੜਦਾ ਹੈ ਜੋ ਉਸ ਚਿੰਨ੍ਹ ਦਾ ਵਰਣਨ ਕਰਦਾ ਹੈ ਜਿਸ ਨੇ ਡਾਟਾ ਚੁੱਕਿਆ ਸੀ। ਇਹ ਦਰਜ, ਮਿਲੇ ਹੋਏ AIM ਕੋਡ ID ਦਾ ਖ਼ਾਸ ਰਜਿਸਟਰੀ ਰਿਕਾਰਡ ਹੈ; `DataCarrierType` ਉਹ ਕੰਪਾਈਲ-ਵੇਲੇ ਵਾਲਾ enum ਹੈ ਜਿਸ ਨਾਲ ਉਹ ਸੰਬੰਧਿਤ ਹੈ।

#### DataCarrierEntry

ਇੱਕ ਪਛਾਣੇ ਗਏ AIM ਕੋਡ ID ਦਾ ਵੇਰਵਾ (`tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry`)।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getAimCodeId()` | ਜੋ AIM ਕੋਡ ID ਮਿਲਿਆ, ਜਿਵੇਂ `"]C1"`। |
| `getName()` | ਉਸ ਖ਼ਾਸ ਚਿੰਨ੍ਹ ਦਾ ਬੰਦੇ-ਪੜ੍ਹਨਯੋਗ ਨਾਮ, ਜਿਵੇਂ `"GS1-128 / ISBT 128"`, `"EAN-8"`। |
| `getDescription()` | ਵਾਹਕ ਦਾ ਲੰਮਾ ਵੇਰਵਾ। |
| `getType()` | ਵਾਹਕ ਦੀ ਬਣਤਰੀ ਕਿਸਮ, ਸਟ੍ਰਿੰਗ ਵਜੋਂ (`getDataCarrierType().getCategory()` ਵਾਂਗ ਹੀ)। |
| `getStandard()` | ਸਿੰਬੋਲੋਜੀ ਦਾ ਮਿਆਰ, ਜਿੱਥੇ ਦਰਜ ਹੋਵੇ। |
| `getDataCarrierType()` | ਇਸ ਦਰਜ ਲਈ ਕਿਸਮ ਵਾਲਾ `DataCarrierType` — ਪ੍ਰੋਗਰਾਮੀ ਦਿਸ਼ਾ ਦੇਣ ਲਈ ਇਹੀ ਚੁਣੋ। |
| `isGs1Capable()` | `true` ਜੇ ਵਾਹਕ GS1 ਡਾਟਾ ਚੁੱਕ ਸਕਦਾ ਹੋਵੇ (AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਅਤੇ/ਜਾਂ ਡਿਜੀਟਲ ਲਿੰਕ)। |
| `isGs1AICapable()` | `true` ਜੇ ਵਾਹਕ GS1 AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਚੁੱਕ ਸਕਦਾ ਹੋਵੇ। |
| `isGs1DigitalLinkCapable()` | `true` ਜੇ ਵਾਹਕ GS1 ਡਿਜੀਟਲ ਲਿੰਕ URI ਚੁੱਕ ਸਕਦਾ ਹੋਵੇ। |
| `isEciCapable()` | `true` ਜੇ ਵਾਹਕ ECI ਸੰਕੇਤਕ ਦਾ ਸਾਥ ਦਿੰਦਾ ਹੋਵੇ। |
| `isRequiresGtinPadding()` | ਉਹਨਾਂ EAN/UPC/ITF ਵਾਹਕਾਂ ਲਈ `true` ਜਿਹਨਾਂ ਦਾ ਸੰਖਿਅਕ ਮੁੱਲ AI ਪਾਰਸਿੰਗ ਤੋਂ ਪਹਿਲਾਂ GTIN-14 ਤੱਕ ਭਰਿਆ ਜਾਂਦਾ ਹੈ। |

#### DataCarrierType

ISO/IEC 15424 ਵਿੱਚ ਦਿੱਤੇ AIM ਕੋਡ ID ਮੁਤਾਬਕ ਕੁੰਜੀਬੱਧ, ਡਾਟਾ-ਵਾਹਕ ਕਿਸਮਾਂ ਦਾ ਕੰਪਾਈਲ-ਵੇਲੇ ਵਾਲਾ enum (`tools.pantheum.gaia.datacarrier.registry.DataCarrierType`)। `]` ਮਗਰੋਂ ਆਉਣ ਵਾਲਾ ਅੱਖਰ (*ਕੋਡ ਅੱਖਰ*) ਪਰਿਵਾਰ ਚੁਣਦਾ ਹੈ; ਬਹੁਤੇ ਪਰਿਵਾਰ ਇੱਕੋ ਸਥਿਰਾਂਕ ਨਾਲ ਮਿਲਦੇ ਹਨ ਜੋ ਹਰ ਸੋਧਕ ਨੂੰ ਢਕ ਲੈਂਦਾ ਹੈ (`ITF` `]I0`–`]I2` ਨੂੰ ਢਕਦਾ ਹੈ; `EAN_UPC` EAN-13, UPC-A, UPC-E ਤੇ EAN-8 ਨੂੰ)। ਜਿੱਥੇ GS1 ਨੇ AI ਡਾਟੇ ਲਈ ਕੋਈ ਸੋਧਕ ਰਾਖਵਾਂ ਰੱਖਿਆ ਹੈ, ਓਥੇ ਉਹ ਰੂਪ ਆਪਣਾ ਵੱਖਰਾ ਸਥਿਰਾਂਕ ਹੈ — `GS1_128` (`]C1`), `GS1_DATA_MATRIX` (`]d2`), `GS1_QR_CODE` (`]Q3`), `GS1_DOT_CODE` (`]J1`) — ਜੋ ਆਪਣੇ ਸਾਦੇ ਸਾਥੀਆਂ ਤੋਂ ਵੱਖਰੇ ਹਨ। ਜਦੋਂ ਕੋਈ AIM ਕੋਡ ID ਮੌਜੂਦ ਨਾ ਹੋਵੇ, ਜਾਂ ਉਹ ਕਿਸੇ ਅਣਜਾਣ ਵਾਹਕ ਦਾ ਨਾਮ ਲਵੇ, ਤਾਂ ਕਿਸਮ `UNKNOWN` ਹੁੰਦੀ ਹੈ।

| ਵਿਧੀ | ਵੇਰਵਾ |
|---|---|
| `getCategory()` | ਮੋਟੀ `GaiaConstants.DataCarrierTypeCategory`: `LINEAR`, `STACKED_LINEAR`, `TWO_D`, `POSTAL`, `OCR`, ਜਾਂ `OTHER`। |
| `getCodeChar()` | ਪਰਿਵਾਰ ਦੱਸਣ ਵਾਲਾ AIM ਕੋਡ ਅੱਖਰ, ਜਿਵੇਂ QR ਕੋਡ ਲਈ `"Q"`; `UNKNOWN` ਲਈ `null`। |
| `getDisplayName()` | *ਕਿਸਮ* ਦਾ ਬੰਦੇ-ਪੜ੍ਹਨਯੋਗ ਨਾਮ (ਇਹ `DataCarrierEntry.getName()` ਨਾਲੋਂ ਵੱਧ ਚੌੜਾ ਹੋ ਸਕਦਾ ਹੈ — ਜਿਵੇਂ `"EAN-13 / UPC-A / UPC-E / EAN-8"` ਬਨਾਮ `"EAN-8"`)। |
| `isGs1DataCarrier()` | ਉਹਨਾਂ ਸਥਿਰਾਂਕਾਂ ਲਈ `true` ਜੋ ਹਮੇਸ਼ਾ GS1 AI ਡਾਟਾ ਹੀ ਦਰਸਾਉਂਦੇ ਹਨ: GS1-ਰਾਖਵੇਂ ਚਾਰ ਰੂਪ (`GS1_128`, `GS1_DATA_MATRIX`, `GS1_QR_CODE`, `GS1_DOT_CODE`) ਤੇ ਨਾਲ `GS1_DATABAR`, ਜੋ ਆਪਣੇ ਸੁਭਾਅ ਤੋਂ ਹੀ GS1 ਹੈ ਕਿਉਂਕਿ ਹਰ `]e` ਸੋਧਕ GS1 DataBar ਹੀ ਹੁੰਦਾ ਹੈ। ਇਹ `DataCarrierEntry.isGs1AICapable()` ਨਾਲੋਂ ਭੀੜਾ ਹੈ — ਸਾਦਾ `QR_CODE` ਵੀ GS1 AI ਡਾਟਾ ਚੁੱਕ ਸਕਦਾ ਹੈ। |
| `static forAimCodeId(String)` | ਕਿਸਮ ਸਿੱਧੀ AIM ਕੋਡ ID ਤੋਂ ਹੱਲ ਕਰਦੀ ਹੈ (`"]Q3"` → `GS1_QR_CODE`; `"]Q9"` → `QR_CODE`); ਗ਼ੈਰ-ਹਾਜ਼ਰ, ਵਿਗੜੇ ਜਾਂ ਅਣਪਛਾਤੇ ID ਲਈ `UNKNOWN` ਮੋੜਦੀ ਹੈ। |

ਨਾਮ ਦੀ ਥਾਂ ਕਿਸਮ ਮੁਤਾਬਕ ਦਿਸ਼ਾ ਦੇਣਾ — ਜਿਵੇਂ ਰੇਖੀ (Code-128) ਨੂੰ 2D (QR / ਡਾਟਾ ਮੈਟ੍ਰਿਕਸ) ਚਿੰਨ੍ਹਾਂ ਤੋਂ ਵੱਖ ਕਰਨਾ:

```java
ParseResult resp = parser.parse(scan,
        ParseConfig.builder().requestedParseMode(ParseMode.DATA_CARRIER).build());

DataCarrierType type = resp.hasDataCarrier()
        ? resp.getDataCarrier().getDataCarrierType()
        : DataCarrierType.UNKNOWN;

boolean linear = type == DataCarrierType.CODE_128 || type == DataCarrierType.GS1_128;
boolean matrix = type.getCategory() == DataCarrierTypeCategory.TWO_D;  // QR, Data Matrix, their GS1 variants
```

`TWO_D` ਸਿਰਫ਼ ਮੈਟ੍ਰਿਕਸ ਤੇ ਬਿੰਦੂ ਵਾਲੇ ਚਿੰਨ੍ਹ ਢਕਦਾ ਹੈ; ਥੱਪੇ ਹੋਏ ਰੇਖੀ ਵਾਹਕ (`PDF417`,
`CODE_16K`, `CODABLOCK`, `CODE_49`) `STACKED_LINEAR` ਹਨ, ਭਾਵੇਂ ਇਹਨਾਂ ਨੂੰ ਆਮ ਕਰ ਕੇ "2D"
ਬਾਰਕੋਡ ਕਿਹਾ ਜਾਂਦਾ ਹੈ। ਦੋਹਾਂ ਨੂੰ ਇੱਕੋ ਟੋਲੀ ਵਾਂਗ ਸੰਭਾਲਣ ਲਈ — ਜਿਵੇਂ ਇਹ ਤੈਅ ਕਰਨ ਲਈ ਕਿ ਲੇਜ਼ਰ
ਸਕੈਨਰ ਦੀ ਥਾਂ ਇਮੇਜਰ ਚਾਹੀਦਾ ਹੈ ਜਾਂ ਨਹੀਂ — ਦੋਹਾਂ ਵਿੱਚੋਂ ਕਿਸੇ ਵੀ ਸ਼੍ਰੇਣੀ ਦੀ ਪਰਖ ਕਰੋ।

> ਕਿਸਮ ਹੱਲ ਕਰਨ ਲਈ ਸਕੈਨ ਵਿੱਚ AIM ਕੋਡ ID ਦਾ ਹੋਣਾ ਲਾਜ਼ਮੀ ਹੈ; ਉਸ ਤੋਂ ਬਿਨਾਂ `getDataCarrier()` `null` ਹੁੰਦਾ ਹੈ ਤੇ ਕਿਸਮ `UNKNOWN`। ਸਕੈਨਰ ਨੂੰ AIM ਕੋਡ ID ਅਗੇਤਰ ਭੇਜਣ ਲਈ ਸੈੱਟ ਕਰੋ।

---

## ਗ਼ਲਤੀਆਂ ਦਾ ਹਵਾਲਾ

| ਕੋਡ | ਦਰਜਾ | ਪੜਾਅ | ਮਤਲਬ |
|---|---|---|---|
| `GE-S001` | SYNTAX_ERROR | SYNTAX | ਅਣਜਾਣ AI ਅਗੇਤਰ — ਡਾਟੇ ਦੀ ਲੰਬਾਈ ਤੈਅ ਨਹੀਂ ਹੋ ਸਕਦੀ |
| `GE-S002` | SYNTAX_ERROR | SYNTAX | ਪੂਰਾ AI ਕੋਡ ਪੜ੍ਹਨ ਲਈ ਇਨਪੁਟ ਬਹੁਤ ਛੋਟੀ |
| `GE-S003` | SYNTAX_ERROR | SYNTAX | ਕੱਟਿਆ ਹੋਇਆ ਮੁੱਲ — AI ਦੀ ਲੋੜ ਨਾਲੋਂ ਘੱਟ ਅੱਖਰ |
| `GE-S004` | INTEGRITY_ERROR | SYNTAX | ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ ਦੁਹਰਾਇਆ ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ |
| `GE-S005` | INTEGRITY_ERROR | SYNTAX | ਲੋੜੀਂਦੀ AI ਨਿਰਭਰਤਾ ਗ਼ੈਰ-ਹਾਜ਼ਰ |
| `GE-S006` | INTEGRITY_ERROR | SYNTAX | ਵਰਜਿਤ AI ਜੋੜਾ — ਦੋ AI ਜੋ ਇਕੱਠੇ ਨਹੀਂ ਆ ਸਕਦੇ |
| `GE-S007` | SYNTAX_ERROR | SYNTAX | ਅਣਕਿਆਸੀ ਟੋਕਨਾਈਜ਼ੇਸ਼ਨ ਨਾਕਾਮੀ |
| `GE-S008` | SYNTAX_ERROR | SYNTAX | ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਵਿੱਚ GS1 ਦੇ ਏਨਕੋਡ-ਯੋਗ ਸਮੂਹ ਤੋਂ ਬਾਹਰਲਾ ਅੱਖਰ |
| `GE-S009` | SYNTAX_ERROR | SYNTAX | ਪਰਿਵਰਤਨਸ਼ੀਲ-ਲੰਬਾਈ ਵਾਲੇ AI ਮਗਰੋਂ ਲੋੜੀਂਦਾ FNC1 ਵਿਭਾਜਕ ਗ਼ੈਰ-ਹਾਜ਼ਰ |
| `GE-S010` | SYNTAX_ERROR | SYNTAX | ਸਾਰੇ ਹਿੱਸਿਆਂ ਦੀ ਵੱਧ ਤੋਂ ਵੱਧ ਹੱਦ ਤੋਂ ਅੱਗੇ ਬਚਿਆ ਡਾਟਾ |
| `GE-S011` | SYNTAX_ERROR | SYNTAX | ਸਟ੍ਰਿੰਗ ਦੇ ਵਿਚਕਾਰ ਸਥਿਰ-ਲੰਬਾਈ ਵਾਲੇ AI ਮਗਰੋਂ FNC1 ਵਿਭਾਜਕ |
| `GE-W002` | WARNING | SYNTAX | ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਦੇ ਅੰਤ ਉੱਤੇ ਬਚਿਆ FNC1 (ਸਿਰਫ਼ ਸਲਾਹ) |
| `GE-L001`–`GE-L014` | SYNTAX_ERROR | DIGITAL_LINK | ਡਿਜੀਟਲ ਲਿੰਕ URI ਦੀਆਂ ਬਣਤਰੀ ਉਲੰਘਣਾਵਾਂ — ਹਰ ਹਾਲਤ ਲਈ ਇੱਕ ਕੋਡ (ਵਿਗੜਿਆ URI, ਸਕੀਮ, ਮੇਜ਼ਬਾਨ, ਯੋਗਤਾ-ਸੂਚਕਾਂ ਦੀ ਤਰਤੀਬ, ਵਰਜਿਤ AI, ਕੋਈ ਮੁੱਖ ਕੁੰਜੀ ਨਹੀਂ (`GE-L013`), ਕਈ ਮੁੱਖ ਕੁੰਜੀਆਂ (`GE-L014`), …) |
| `GE-C001` | FORMAT_ERROR | CONTENT | ਮੁੱਲ AI ਦੇ regex ਨਮੂਨੇ ਉੱਤੇ ਪੂਰਾ ਨਹੀਂ ਉੱਤਰਦਾ |
| `GE-C003` | DATA_ERROR | CONTENT | ਜਾਂਚ ਅੰਕ ਦੀ ਤਸਦੀਕ ਨਾਕਾਮ |
| `GE-C004` | DATA_ERROR | CONTENT | ਜਾਂਚ ਅੱਖਰ ਜੋੜੇ ਦੀ ਤਸਦੀਕ ਨਾਕਾਮ |
| `GE-C005` | FORMAT_ERROR | CONTENT | ਹਿੱਸੇ ਦੇ ਮੁੱਲ ਵਿੱਚ ਮੰਨਜ਼ੂਰ ਅੱਖਰ-ਸਮੂਹ ਤੋਂ ਬਾਹਰਲਾ ਅੱਖਰ |
| `GE-C054`–`GE-C115` | FORMAT_ERROR | CONTENT | ਹਿੱਸੇ ਦੇ ਰੂਪ ਦੀਆਂ ਨਾਕਾਮੀਆਂ — ਹਰ ਤਸਦੀਕ-ਸ਼ਰਤ ਲਈ ਇੱਕ ਕੋਡ (ਵੇਖੋ `componentformat/`) |
| `GE-C116`–`GE-C170` | DATA_ERROR | CONTENT | ਖ਼ਾਸ ਅਰਥ-ਤਸਦੀਕ ਦੀਆਂ ਨਾਕਾਮੀਆਂ — ਹਰ ਤਸਦੀਕ-ਸ਼ਰਤ ਲਈ ਇੱਕ ਕੋਡ (ਵੇਖੋ `content/validator/`)। **ਅਪਵਾਦ:** ਹੇਠਾਂ ਗਿਣੀਆਂ 14 GS1 ਕੰਪਨੀ-ਅਗੇਤਰ ਪਰਖਾਂ ਦਾ ਦਰਜਾ `WARNING` ਹੈ, ਤੇ `GE-C168` (ਅਣਪਛਾਤਾ ISO 3166-1 ਸੰਖਿਅਕ ਦੇਸ਼ ਕੋਡ) ਦਾ ਦਰਜਾ `FORMAT_ERROR` ਹੈ। |
| GS1 ਕੰਪਨੀ-ਅਗੇਤਰ ਪਰਖਾਂ | WARNING | CONTENT | GS1-ਕੁੰਜੀ ਵਾਲੇ AI ਉੱਤੇ ਕੁੰਜੀ ਕਿਸੇ ਪਛਾਣੇ GS1 ਕੰਪਨੀ ਅਗੇਤਰ ਨਾਲ ਸ਼ੁਰੂ ਨਹੀਂ ਹੁੰਦੀ — `GE-C122` (CPID), `GE-C129` (GCN), `GE-C131` (GDTI), `GE-C132` (GIAI), `GE-C133` (GINC), `GE-C135` (GLN), `GE-C137` (GMN), `GE-C140` (GRAI), `GE-C142` (GSIN), `GE-C144` (GSRN), `GE-C146` (GTIN), `GE-C148` (HIDRI), `GE-C153` (ITIP), `GE-C165` (SSCC)। ਸਿਰਫ਼ ਸਲਾਹ — ਜਾਇਜ਼ਪੁਣੇ ਉੱਤੇ ਅਸਰ ਨਹੀਂ ਪਾਉਂਦੀ। |
| `GE-C169` | DATA_ERROR | CONTENT | AI 8040 (IMEI) / 8041 (IMEI2) ਉੱਤੇ IMEI ਜਾਂਚ ਅੰਕ (Luhn) ਨਾਕਾਮ |
| `GE-C170` | DATA_ERROR | CONTENT | AI 8042 (ESIM) ਉੱਤੇ EID ਜਾਂਚ ਅੰਕ (Luhn) ਨਾਕਾਮ |
| `GE-D001` | SYNTAX_ERROR | DATA_CARRIER | ਅਣਪਛਾਤਾ AIM ਕੋਡ ID |
| `GE-D002` | SYNTAX_ERROR | DATA_CARRIER | ਵਾਹਕ ਪਛਾਣਿਆ ਗਿਆ ਪਰ ਉਹ ਨਾ GS1 AI ਐਲੀਮੈਂਟ ਸਟ੍ਰਿੰਗ ਦਾ ਸਾਥ ਦਿੰਦਾ ਹੈ ਨਾ ਡਿਜੀਟਲ ਲਿੰਕ URI ਦਾ |
| `GE-I001` | SYNTAX_ERROR | INTERNAL | ਅਣਕਿਆਸੀ ਅੰਦਰੂਨੀ ਗ਼ਲਤੀ |

> **ਸੁਨੇਹਾ ਵਿਖਾਉਣ ਵਿੱਚ ਜਾਣੀ-ਪਛਾਣੀ ਖ਼ਰਾਬੀ।** ਸੂਚੀ ਦੇ ਨਮੂਨੇ ਭਰੇ ਹੋਏ ਮੁੱਲਾਂ ਨੂੰ
> MessageFormat ਵਾਲੇ ਦੂਹਰੇ ਉਲਟੇ ਕਾਮਿਆਂ (`''{value}''`) ਵਿੱਚ ਲਿਖਦੇ ਹਨ, ਪਰ
> `ErrorRegistry` ਸਾਦੇ `String.replace` ਨਾਲ ਮੁੱਲ ਭਰਦਾ ਹੈ, ਸੋ ਇਹ ਦੂਹਰਾਪਣ
> `getMessage()` ਤੱਕ ਬਚਿਆ ਰਹਿੰਦਾ ਹੈ — ਜਿੱਥੇ ਇਸ ਗਾਈਡ ਵਿੱਚ ਦਿੱਤੇ ਸੁਨੇਹੇ
> `value '09506000134351'` ਵਿਖਾਉਂਦੇ ਹਨ, ਓਥੇ ਤੁਹਾਨੂੰ ਇਸ ਵੇਲੇ
> `value ''09506000134351''` ਦਿਸੇਗਾ। ਇਹ ਸਾਰੀਆਂ 35 ਭਾਸ਼ਾ ਸੂਚੀਆਂ ਵਿੱਚ ਹਰ
> ਮੁੱਲ-ਲਿਖਣ ਵਾਲੇ ਸੁਨੇਹੇ ਉੱਤੇ ਅਸਰ ਪਾਉਂਦੀ ਹੈ। ਗ਼ਲਤੀ ਸੁਨੇਹੇ ਪਾਰਸ ਨਾ ਕਰੋ;
> `getId()` / `getCode()` ਉੱਤੇ ਮੇਲ ਕਰੋ।

---

## ਥ੍ਰੈੱਡ ਸੁਰੱਖਿਆ

ਬਣ ਜਾਣ ਮਗਰੋਂ `GaiaParser` ਥ੍ਰੈੱਡ-ਸੁਰੱਖਿਅਤ ਹੈ। ਇੱਕੋ ਨਮੂਨਾ ਕਈ ਥ੍ਰੈੱਡਾਂ ਵਿੱਚ ਸਾਂਝਾ ਕੀਤਾ ਤੇ ਨਾਲੋ-ਨਾਲ ਵਰਤਿਆ ਜਾ ਸਕਦਾ ਹੈ। ਸੁਝਾਇਆ ਨਮੂਨਾ ਇਹ ਹੈ ਕਿ ਐਪਲੀਕੇਸ਼ਨ ਦੇ ਸ਼ੁਰੂ ਵਿੱਚ ਇੱਕੋ ਨਮੂਨਾ ਬਣਾਓ ਤੇ ਉਸੇ ਨੂੰ ਮੁੜ-ਮੁੜ ਵਰਤੋ:

```java
// Application startup
private static final GaiaParser PARSER = new GaiaParser();

// Per-request usage (safe to call concurrently)
public ParseResult scan(String rawInput) {
    return PARSER.parse(rawInput);   // default config = INTERPRETATION mode
}
```

`ParseConfig` ਅਟੱਲ ਹੈ ਤੇ ਓਨਾ ਹੀ ਸਾਂਝਾ ਕਰਨ-ਯੋਗ ਸੁਰੱਖਿਅਤ। ਥ੍ਰੈੱਡ-ਸੁਰੱਖਿਆ ਦੀ ਇੱਕੋ ਜ਼ਿੰਮੇਵਾਰੀ ਜੋ ਲਾਇਬ੍ਰੇਰੀ ਤੁਹਾਡੇ ਲਈ ਪੱਕੀ ਨਹੀਂ ਕਰ ਸਕਦੀ ਉਹ [ਇਨਪੁਟ ਮੋਡੀਫਾਇਰਾਂ](#ਇਨਪਟ-ਮਡਫਇਰ) ਉੱਤੇ ਹੈ: ਹਰ ਮੋਡੀਫਾਇਰ ਦਾ ਇੱਕੋ ਨਮੂਨਾ ਸਾਂਭ ਕੇ ਹਰ ਨਾਲੋ-ਨਾਲ ਚੱਲਦੇ ਪਾਰਸ ਵਿੱਚ ਸਾਂਝਾ ਕੀਤਾ ਜਾਂਦਾ ਹੈ, ਸੋ ਅਮਲਾਂ ਦਾ ਹਾਲਤ-ਰਹਿਤ ਹੋਣਾ ਲਾਜ਼ਮੀ ਹੈ।

---

## ਅੰਤਿਕਾ A — AI ਸਟ੍ਰਿੰਗ ਸਥਿਰਾਂਕ

`GS1Constants_AICodes` (ਪੈਕੇਜ `tools.pantheum.gaia.gs1.constants` ਵਿੱਚ) GAIA ਵੱਲੋਂ ਪਛਾਣੇ ਜਾਂਦੇ ਹਰ ਐਪਲੀਕੇਸ਼ਨ ਆਈਡੈਂਟੀਫਾਇਰ ਲਈ ਇੱਕ `String` ਸਥਿਰਾਂਕ ਐਲਾਨਦੀ ਹੈ। ਕੱਚੀਆਂ AI ਕੋਡ ਸਟ੍ਰਿੰਗਾਂ ਕੋਡ ਵਿੱਚ ਪੱਕੀਆਂ ਲਿਖਣ ਦੀ ਥਾਂ ਇਹ ਸਥਿਰਾਂਕ ਵਰਤੋ:

```java
// Retrieve a parsed element by AI code
GS1AIObjectElement gtinEl = aiObject.get(GS1Constants_AICodes.AI_01_GTIN);

// Test whether a specific AI is present
boolean hasExpiry = aiObject.contains(GS1Constants_AICodes.AI_17_USE_BY_OR_EXPIRY);
```

ਹਰ ਸਥਿਰਾਂਕ AI ਕੋਡ ਦਾ ਸਟ੍ਰਿੰਗ ਰੂਪ ਰੱਖਦਾ ਹੈ (ਜਿਵੇਂ `AI_01_GTIN = "01"`)।

### ਪਛਾਣ ਤੇ ਲੜੀਬੱਧਤਾ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `00` | `AI_00_SSCC` | ਸੀਰੀਅਲ ਸ਼ਿਪਿੰਗ ਕੰਟੇਨਰ ਕੋਡ (SSCC). |
| `01` | `AI_01_GTIN` | ਗਲੋਬਲ ਵਪਾਰ ਵਸਤੂ ਨੰਬਰ (GTIN). |
| `02` | `AI_02_CONTENT` | ਅੰਦਰ ਸ਼ਾਮਲ ਵਪਾਰ ਵਸਤੂਆਂ ਦਾ ਗਲੋਬਲ ਵਪਾਰ ਵਸਤੂ ਨੰਬਰ (GTIN). |
| `03` | `AI_03_MTO_GTIN` | ਮੇਡ-ਟੂ-ਆਰਡਰ (MtO) ਵਪਾਰ ਵਸਤੂ ਦੀ ਪਛਾਣ (GTIN). |
| `10` | `AI_10_BATCH_LOT` | ਬੈਚ ਜਾਂ ਲਾਟ ਨੰਬਰ. |
| `20` | `AI_20_VARIANT` | ਅੰਦਰੂਨੀ ਉਤਪਾਦ ਰੂਪ. |
| `21` | `AI_21_SERIAL` | ਸੀਰੀਅਲ ਨੰਬਰ. |
| `22` | `AI_22_CPV` | ਖਪਤਕਾਰ ਉਤਪਾਦ ਰੂਪ. |
| `235` | `AI_235_TPX` | ਥਰਡ ਪਾਰਟੀ ਕੰਟਰੋਲਡ, ਸੀਰੀਅਲਾਈਜ਼ਡ ਐਕਸਟੈਂਸ਼ਨ ਆਫ਼ ਗਲੋਬਲ ਵਪਾਰ ਵਸਤੂ ਨੰਬਰ (GTIN) (TPX). |
| `240` | `AI_240_ADDITIONAL_ID` | ਨਿਰਮਾਤਾ ਦੁਆਰਾ ਦਿੱਤੀ ਗਈ ਵਾਧੂ ਉਤਪਾਦ ਪਛਾਣ. |
| `241` | `AI_241_CUST_PART_NO` | ਗਾਹਕ ਪੁਰਜ਼ਾ ਨੰਬਰ. |
| `242` | `AI_242_MTO_VARIANT` | ਮੇਡ-ਟੂ-ਆਰਡਰ ਬਦਲਾਅ ਨੰਬਰ. |
| `243` | `AI_243_PCN` | ਪੈਕੇਜਿੰਗ ਹਿੱਸਾ ਨੰਬਰ. |
| `250` | `AI_250_SECONDARY_SERIAL` | ਸੈਕੰਡਰੀ ਸੀਰੀਅਲ ਨੰਬਰ. |
| `251` | `AI_251_REF_TO_SOURCE` | ਸਰੋਤ ਇਕਾਈ ਦਾ ਹਵਾਲਾ. |
| `253` | `AI_253_GDTI` | ਗਲੋਬਲ ਦਸਤਾਵੇਜ਼ ਕਿਸਮ ਪਛਾਣਕਰਤਾ (GDTI). |
| `254` | `AI_254_GLN_EXTENSION_COMPONENT` | ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN) ਐਕਸਟੈਂਸ਼ਨ ਹਿੱਸਾ. |
| `255` | `AI_255_GCN` | ਗਲੋਬਲ ਕੂਪਨ ਨੰਬਰ (GCN). |
| `30` | `AI_30_VAR_COUNT` | ਵਸਤੂਆਂ ਦੀ ਪਰਿਵਰਤਨਸ਼ੀਲ ਗਿਣਤੀ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `37` | `AI_37_COUNT` | ਲੌਜਿਸਟਿਕ ਯੂਨਿਟ ਵਿੱਚ ਸ਼ਾਮਲ ਵਪਾਰ ਵਸਤੂਆਂ ਜਾਂ ਵਸਤੂ ਦੇ ਟੁਕੜਿਆਂ ਦੀ ਗਿਣਤੀ. |

### ਮਿਤੀਆਂ ਤੇ ਸਮੇਂ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `11` | `AI_11_PROD_DATE` | ਉਤਪਾਦਨ ਮਿਤੀ (YYMMDD). |
| `12` | `AI_12_DUE_DATE` | ਦੇਣ ਦੀ ਮਿਤੀ (YYMMDD). |
| `13` | `AI_13_PACK_DATE` | ਪੈਕੇਜਿੰਗ ਮਿਤੀ (YYMMDD). |
| `15` | `AI_15_BEST_BEFORE_OR_BEST_BY` | ਬੈਸਟ ਬਿਫੋਰ ਮਿਤੀ (YYMMDD). |
| `16` | `AI_16_SELL_BY` | ਸੇਲ ਬਾਈ ਮਿਤੀ (YYMMDD). |
| `17` | `AI_17_USE_BY_OR_EXPIRY` | ਮਿਆਦ ਪੁੱਗਣ ਦੀ ਮਿਤੀ (YYMMDD). |
| `4324` | `AI_4324_NBEF_DEL_DT` | ਡਿਲੀਵਰੀ ਤੋਂ ਪਹਿਲਾਂ ਨਾ ਹੋਣ ਵਾਲੀ ਮਿਤੀ ਸਮਾਂ (YYMMDDhhmm). |
| `4325` | `AI_4325_NAFT_DEL_DT` | ਡਿਲੀਵਰੀ ਤੋਂ ਬਾਅਦ ਨਾ ਹੋਣ ਵਾਲੀ ਮਿਤੀ ਸਮਾਂ (YYMMDDhhmm). |
| `4326` | `AI_4326_REL_DATE` | ਰਿਲੀਜ਼ ਮਿਤੀ (YYMMDD). |
| `7003` | `AI_7003_EXPIRY_TIME` | ਮਿਆਦ ਪੁੱਗਣ ਦੀ ਮਿਤੀ ਅਤੇ ਸਮਾਂ (YYMMDDhhmm). |
| `7006` | `AI_7006_FIRST_FREEZE_DATE` | ਪਹਿਲੀ ਫ੍ਰੀਜ਼ ਮਿਤੀ (YYMMDD). |
| `7007` | `AI_7007_HARVEST_DATE` | ਵਾਢੀ ਦੀ ਮਿਤੀ (YYMMDD[YYMMDD]). |
| `7011` | `AI_7011_TEST_BY_DATE` | ਟੈਸਟ ਬਾਈ ਮਿਤੀ (YYMMDD[hhmm]). |

### ਮਾਤਰਾ ਤੇ ਮਾਪ — ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ (ਮੀਟ੍ਰਿਕ)

4-ਅੰਕੀ AI ਪਰਿਵਾਰ `310n`–`369n` ਪਰਿਵਰਤਨਸ਼ੀਲ-ਮਾਪ ਵਾਲੀਆਂ ਮਾਤਰਾਵਾਂ ਏਨਕੋਡ ਕਰਦੇ ਹਨ। ਤੀਜਾ ਅੰਕ ਮਾਪ ਦੀ ਕਿਸਮ ਚੁਣਦਾ ਹੈ; **ਚੌਥਾ ਅੰਕ** (`n`, 0–5) ਲੁਕਵੇਂ ਦਸ਼ਮਲਵ ਸਥਾਨਾਂ ਦੀ ਗਿਣਤੀ ਹੈ — ਜਿਵੇਂ `AI_3102_NET_WEIGHT_KG` ਦਾ ਮਤਲਬ ਹੈ 2 ਦਸ਼ਮਲਵ ਸਥਾਨਾਂ ਸਮੇਤ ਕਿਲੋਗ੍ਰਾਮ ਵਿੱਚ ਸ਼ੁੱਧ ਭਾਰ।

| ਪਰਿਵਾਰ | ਸਥਿਰਾਂਕ ਨਮੂਨਾ (`n` = ਦਸ਼ਮਲਵ-ਸਥਾਨ ਅੰਕ) | ਵੇਰਵਾ |
|--------|-----------------|-------------|
| `310n` | `AI_310n_NET_WEIGHT_KG` | ਸ਼ੁੱਧ ਭਾਰ, ਕਿਲੋਗ੍ਰਾਮ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `311n` | `AI_311n_LENGTH_M` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਮੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `312n` | `AI_312n_WIDTH_M` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਮੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `313n` | `AI_313n_HEIGHT_M` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਮੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `314n` | `AI_314n_AREA_M` | ਖੇਤਰਫਲ, ਵਰਗ ਮੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `315n` | `AI_315n_NET_VOLUME_L` | ਸ਼ੁੱਧ ਆਇਤਨ, ਲੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `316n` | `AI_316n_NET_VOLUME_M` | ਸ਼ੁੱਧ ਆਇਤਨ, ਘਣ ਮੀਟਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `330n` | `AI_330n_GROSS_WEIGHT_KG` | ਲੌਜਿਸਟਿਕ ਭਾਰ, ਕਿਲੋਗ੍ਰਾਮ. |
| `331n` | `AI_331n_LENGTH_M_LOG` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਮੀਟਰ. |
| `332n` | `AI_332n_WIDTH_M_LOG` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਮੀਟਰ. |
| `333n` | `AI_333n_HEIGHT_M_LOG` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਮੀਟਰ. |
| `334n` | `AI_334n_AREA_M_LOG` | ਖੇਤਰਫਲ, ਵਰਗ ਮੀਟਰ. |
| `335n` | `AI_335n_VOLUME_L_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਲੀਟਰ. |
| `336n` | `AI_336n_VOLUME_M_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਘਣ ਮੀਟਰ. |
| `337n` | `AI_337n_KG_PER_M` | ਕਿਲੋਗ੍ਰਾਮ ਪ੍ਰਤੀ ਵਰਗ ਮੀਟਰ. |

### ਮਾਤਰਾ ਤੇ ਮਾਪ — ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ (ਇੰਪੀਰੀਅਲ / ਅਮਰੀਕੀ)

| ਪਰਿਵਾਰ | ਸਥਿਰਾਂਕ ਨਮੂਨਾ (`n` = ਦਸ਼ਮਲਵ-ਸਥਾਨ ਅੰਕ) | ਵੇਰਵਾ |
|--------|-----------------|-------------|
| `320n` | `AI_320n_NET_WEIGHT_LB` | ਸ਼ੁੱਧ ਭਾਰ, ਪੌਂਡ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `321n` | `AI_321n_LENGTH_IN` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਇੰਚ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `322n` | `AI_322n_LENGTH_FT` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਫੁੱਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `323n` | `AI_323n_LENGTH_YD` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਗਜ਼ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `324n` | `AI_324n_WIDTH_IN` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਇੰਚ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `325n` | `AI_325n_WIDTH_FT` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਫੁੱਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `326n` | `AI_326n_WIDTH_YD` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਗਜ਼ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `327n` | `AI_327n_HEIGHT_IN` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਇੰਚ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `328n` | `AI_328n_HEIGHT_FT` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਫੁੱਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `329n` | `AI_329n_HEIGHT_YD` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਗਜ਼ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `340n` | `AI_340n_GROSS_WEIGHT_LB` | ਲੌਜਿਸਟਿਕ ਭਾਰ, ਪੌਂਡ. |
| `341n` | `AI_341n_LENGTH_IN_LOG` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਇੰਚ. |
| `342n` | `AI_342n_LENGTH_FT_LOG` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਫੁੱਟ. |
| `343n` | `AI_343n_LENGTH_YD_LOG` | ਲੰਬਾਈ ਜਾਂ ਪਹਿਲਾ ਮਾਪ, ਗਜ਼. |
| `344n` | `AI_344n_WIDTH_IN_LOG` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਇੰਚ. |
| `345n` | `AI_345n_WIDTH_FT_LOG` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਫੁੱਟ. |
| `346n` | `AI_346n_WIDTH_YD_LOG` | ਚੌੜਾਈ, ਵਿਆਸ, ਜਾਂ ਦੂਜਾ ਮਾਪ, ਗਜ਼. |
| `347n` | `AI_347n_HEIGHT_IN_LOG` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਇੰਚ. |
| `348n` | `AI_348n_HEIGHT_FT_LOG` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਫੁੱਟ. |
| `349n` | `AI_349n_HEIGHT_YD_LOG` | ਡੂੰਘਾਈ, ਮੋਟਾਈ, ਉਚਾਈ, ਜਾਂ ਤੀਜਾ ਮਾਪ, ਗਜ਼. |
| `350n` | `AI_350n_AREA_IN` | ਖੇਤਰਫਲ, ਵਰਗ ਇੰਚ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `351n` | `AI_351n_AREA_FT` | ਖੇਤਰਫਲ, ਵਰਗ ਫੁੱਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `352n` | `AI_352n_AREA_YD` | ਖੇਤਰਫਲ, ਵਰਗ ਗਜ਼ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `353n` | `AI_353n_AREA_IN_LOG` | ਖੇਤਰਫਲ, ਵਰਗ ਇੰਚ. |
| `354n` | `AI_354n_AREA_FT_LOG` | ਖੇਤਰਫਲ, ਵਰਗ ਫੁੱਟ. |
| `355n` | `AI_355n_AREA_YD_LOG` | ਖੇਤਰਫਲ, ਵਰਗ ਗਜ਼. |
| `356n` | `AI_356n_NET_WEIGHT_TROY_OZ` | ਸ਼ੁੱਧ ਭਾਰ, ਟਰੌਏ ਔਂਸ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `357n` | `AI_357n_NET_VOLUME_OZ` | ਸ਼ੁੱਧ ਭਾਰ (ਜਾਂ ਆਇਤਨ), ਔਂਸ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `360n` | `AI_360n_NET_VOLUME_QT` | ਸ਼ੁੱਧ ਆਇਤਨ, ਕੁਆਰਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `361n` | `AI_361n_NET_VOLUME_GAL` | ਸ਼ੁੱਧ ਆਇਤਨ, ਅਮਰੀਕੀ ਗੈਲਨ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `362n` | `AI_362n_VOLUME_QT_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਕੁਆਰਟ. |
| `363n` | `AI_363n_VOLUME_GAL_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਅਮਰੀਕੀ ਗੈਲਨ. |
| `364n` | `AI_364n_NET_VOLUME_IN` | ਸ਼ੁੱਧ ਆਇਤਨ, ਘਣ ਇੰਚ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `365n` | `AI_365n_NET_VOLUME_FT` | ਸ਼ੁੱਧ ਆਇਤਨ, ਘਣ ਫੁੱਟ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `366n` | `AI_366n_NET_VOLUME_YD` | ਸ਼ੁੱਧ ਆਇਤਨ, ਘਣ ਗਜ਼ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `367n` | `AI_367n_VOLUME_IN_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਘਣ ਇੰਚ. |
| `368n` | `AI_368n_VOLUME_FT_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਘਣ ਫੁੱਟ. |
| `369n` | `AI_369n_VOLUME_YD_LOG` | ਲੌਜਿਸਟਿਕ ਆਇਤਨ, ਘਣ ਗਜ਼. |

### ਕੀਮਤ ਤੇ ਮੁਦਰਾ ਰਕਮਾਂ

ਚੌਥਾ ਅੰਕ (`n`) ਲੁਕਵੇਂ ਦਸ਼ਮਲਵ ਸਥਾਨਾਂ ਦੀ ਗਿਣਤੀ ਏਨਕੋਡ ਕਰਦਾ ਹੈ। ਇਸ ਦੀ ਮੰਨਜ਼ੂਰ ਹੱਦ ਹਰ ਪਰਿਵਾਰ
ਲਈ ਵੱਖਰੀ ਹੈ — `n` ਕਾਲਮ ਵੇਖੋ।

| ਪਰਿਵਾਰ | ਸਥਿਰਾਂਕ ਨਮੂਨਾ (`n` = ਦਸ਼ਮਲਵ-ਸਥਾਨ ਅੰਕ) | `n` | ਵੇਰਵਾ |
|--------|-----------------|-----|-------------|
| `390n` | `AI_390n_AMOUNT` | 0–9 | ਲਾਗੂ ਅਦਾਇਗੀਯੋਗ ਰਕਮ ਜਾਂ ਕੂਪਨ ਦੀ ਕੀਮਤ, ਸਥਾਨਕ ਮੁਦਰਾ. |
| `391n` | `AI_391n_AMOUNT` | 0–9 | ISO ਮੁਦਰਾ ਕੋਡ ਨਾਲ ਲਾਗੂ ਅਦਾਇਗੀਯੋਗ ਰਕਮ. |
| `392n` | `AI_392n_PRICE` | 0–9 | ਲਾਗੂ ਅਦਾਇਗੀਯੋਗ ਰਕਮ, ਇੱਕ ਮੁਦਰਾ ਖੇਤਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `393n` | `AI_393n_PRICE` | 0–9 | ISO ਮੁਦਰਾ ਕੋਡ ਨਾਲ ਲਾਗੂ ਅਦਾਇਗੀਯੋਗ ਰਕਮ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |
| `394n` | `AI_394n_PRCNT_OFF` | 0–3 | ਕੂਪਨ ਦੀ ਪ੍ਰਤੀਸ਼ਤ ਛੋਟ. |
| `395n` | `AI_395n_PRICE_UOM` | 0–5 | ਪ੍ਰਤੀ ਯੂਨਿਟ ਮਾਪ ਅਦਾਇਗੀਯੋਗ ਰਕਮ, ਇੱਕ ਮੁਦਰਾ ਖੇਤਰ (ਪਰਿਵਰਤਨਸ਼ੀਲ ਮਾਪ ਵਾਲੀ ਵਪਾਰ ਵਸਤੂ). |

### ਥਾਂ ਤੇ ਭੇਜਣਾ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `400` | `AI_400_ORDER_NUMBER` | ਗਾਹਕ ਖਰੀਦ ਆਰਡਰ ਨੰਬਰ. |
| `401` | `AI_401_GINC` | ਖੇਪ ਲਈ ਗਲੋਬਲ ਪਛਾਣ ਨੰਬਰ (GINC). |
| `402` | `AI_402_GSIN` | ਗਲੋਬਲ ਸ਼ਿਪਮੈਂਟ ਪਛਾਣ ਨੰਬਰ (GSIN). |
| `403` | `AI_403_ROUTE` | ਰੂਟਿੰਗ ਕੋਡ. |
| `410` | `AI_410_SHIP_TO_LOC` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `411` | `AI_411_BILL_TO` | ਬਿੱਲ ਭੇਜਣ / ਇਨਵਾਇਸ ਭੇਜਣ ਲਈ ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `412` | `AI_412_PURCHASE_FROM` | ਜਿੱਥੋਂ ਖਰੀਦਿਆ ਗਿਆ ਉਸਦਾ ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `413` | `AI_413_SHIP_FOR_LOC` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ - ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN) ਵੱਲ ਅੱਗੇ ਭੇਜੋ. |
| `414` | `AI_414_LOC_NO` | ਭੌਤਿਕ ਟਿਕਾਣੇ ਦੀ ਪਛਾਣ - ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `415` | `AI_415_PAY_TO` | ਇਨਵਾਇਸ ਧਿਰ ਦਾ ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `416` | `AI_416_PROD_SERV_LOC` | ਉਤਪਾਦਨ ਜਾਂ ਸੇਵਾ ਸਥਾਨ ਦਾ ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `417` | `AI_417_PARTY` | ਧਿਰ ਦਾ ਗਲੋਬਲ ਟਿਕਾਣਾ ਨੰਬਰ (GLN). |
| `420` | `AI_420_SHIP_TO_POST` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਪੋਸਟਲ ਕੋਡ ਇੱਕ ਸਿੰਗਲ ਪੋਸਟਲ ਅਥਾਰਟੀ ਦੇ ਅੰਦਰ. |
| `421` | `AI_421_SHIP_TO_POST` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਪੋਸਟਲ ਕੋਡ ਦੇਸ਼ ਦੇ ISO ਕੋਡ ਸਮੇਤ. |
| `422` | `AI_422_ORIGIN` | ਵਪਾਰ ਵਸਤੂ ਦੇ ਮੂਲ ਦਾ ਦੇਸ਼. |
| `423` | `AI_423_COUNTRY_INITIAL_PROCESS` | ਸ਼ੁਰੂਆਤੀ ਪ੍ਰੋਸੈਸਿੰਗ ਦਾ ਦੇਸ਼. |
| `424` | `AI_424_COUNTRY_PROCESS` | ਪ੍ਰੋਸੈਸਿੰਗ ਦਾ ਦੇਸ਼. |
| `425` | `AI_425_COUNTRY_DISASSEMBLY` | ਵੱਖ ਕਰਨ ਵਾਲਾ ਦੇਸ਼. |
| `426` | `AI_426_COUNTRY_FULL_PROCESS` | ਪੂਰੀ ਪ੍ਰਕਿਰਿਆ ਲੜੀ ਨੂੰ ਕਵਰ ਕਰਨ ਵਾਲਾ ਦੇਸ਼. |
| `427` | `AI_427_ORIGIN_SUBDIVISION` | ਮੂਲ ਦੇਸ਼ ਦੀ ਉਪ-ਵੰਡ. |
| `4300` | `AI_4300_SHIP_TO_COMP` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਕੰਪਨੀ ਦਾ ਨਾਮ. |
| `4301` | `AI_4301_SHIP_TO_NAME` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਸੰਪਰਕ. |
| `4302` | `AI_4302_SHIP_TO_ADD1` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਪਤਾ ਲਾਈਨ 1. |
| `4303` | `AI_4303_SHIP_TO_ADD2` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਪਤਾ ਲਾਈਨ 2. |
| `4304` | `AI_4304_SHIP_TO_SUB` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਉਪਨਗਰ. |
| `4305` | `AI_4305_SHIP_TO_LOC` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਇਲਾਕਾ. |
| `4306` | `AI_4306_SHIP_TO_REG` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਖੇਤਰ. |
| `4307` | `AI_4307_SHIP_TO_COUNTRY` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਦੇਸ਼ ਕੋਡ. |
| `4308` | `AI_4308_SHIP_TO_PHONE` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – ਟੈਲੀਫੋਨ ਨੰਬਰ. |
| `4309` | `AI_4309_SHIP_TO_GEO` | ਭੇਜਣ ਲਈ / ਡਿਲੀਵਰ ਕਰਨ ਲਈ – GEO ਟਿਕਾਣਾ. |
| `4310` | `AI_4310_RTN_TO_COMP` | ਵਾਪਸੀ – ਕੰਪਨੀ ਦਾ ਨਾਮ. |
| `4311` | `AI_4311_RTN_TO_NAME` | ਵਾਪਸੀ – ਸੰਪਰਕ. |
| `4312` | `AI_4312_RTN_TO_ADD1` | ਵਾਪਸੀ – ਪਤਾ ਲਾਈਨ 1. |
| `4313` | `AI_4313_RTN_TO_ADD2` | ਵਾਪਸੀ – ਪਤਾ ਲਾਈਨ 2. |
| `4314` | `AI_4314_RTN_TO_SUB` | ਵਾਪਸੀ – ਉਪਨਗਰ. |
| `4315` | `AI_4315_RTN_TO_LOC` | ਵਾਪਸੀ – ਇਲਾਕਾ. |
| `4316` | `AI_4316_RTN_TO_REG` | ਵਾਪਸੀ – ਖੇਤਰ. |
| `4317` | `AI_4317_RTN_TO_COUNTRY` | ਵਾਪਸੀ – ਦੇਸ਼ ਕੋਡ. |
| `4318` | `AI_4318_RTN_TO_POST` | ਵਾਪਸੀ – ਪੋਸਟਲ ਕੋਡ. |
| `4319` | `AI_4319_RTN_TO_PHONE` | ਵਾਪਸੀ – ਟੈਲੀਫੋਨ ਨੰਬਰ. |
| `4320` | `AI_4320_SRV_DESCRIPTION` | ਸੇਵਾ ਕੋਡ ਵੇਰਵਾ. |
| `4321` | `AI_4321_DANGEROUS_GOODS` | ਖਤਰਨਾਕ ਸਮਾਨ ਫਲੈਗ. |
| `4322` | `AI_4322_AUTH_LEAVE` | ਸਮਾਨ ਬਿਨਾਂ ਦਸਤਖ਼ਤ ਛੱਡਣ ਦੀ ਇਜਾਜ਼ਤ (Authority to Leave). |
| `4323` | `AI_4323_SIG_REQUIRED` | ਦਸਤਖ਼ਤ ਲੋੜੀਂਦਾ ਫਲੈਗ. |
| `4330` | `AI_4330_MAX_TEMP_F` | ਵੱਧ ਤੋਂ ਵੱਧ ਤਾਪਮਾਨ ਫਾਰਨਹੀਟ ਵਿੱਚ (ਡਿਗਰੀ ਦੇ ਸੌਵੇਂ ਹਿੱਸੇ ਵਿੱਚ ਦਰਸਾਇਆ ਗਿਆ). |
| `4331` | `AI_4331_MAX_TEMP_C` | ਵੱਧ ਤੋਂ ਵੱਧ ਤਾਪਮਾਨ ਸੈਲਸੀਅਸ ਵਿੱਚ (ਡਿਗਰੀ ਦੇ ਸੌਵੇਂ ਹਿੱਸੇ ਵਿੱਚ ਦਰਸਾਇਆ ਗਿਆ). |
| `4332` | `AI_4332_MIN_TEMP_F` | ਘੱਟ ਤੋਂ ਘੱਟ ਤਾਪਮਾਨ ਫਾਰਨਹੀਟ ਵਿੱਚ (ਡਿਗਰੀ ਦੇ ਸੌਵੇਂ ਹਿੱਸੇ ਵਿੱਚ ਦਰਸਾਇਆ ਗਿਆ). |
| `4333` | `AI_4333_MIN_TEMP_C` | ਘੱਟ ਤੋਂ ਘੱਟ ਤਾਪਮਾਨ ਸੈਲਸੀਅਸ ਵਿੱਚ (ਡਿਗਰੀ ਦੇ ਸੌਵੇਂ ਹਿੱਸੇ ਵਿੱਚ ਦਰਸਾਇਆ ਗਿਆ). |

### ਵਸਤੂ ਦੇ ਗੁਣ ਤੇ ਪੈੜ-ਨਿਸ਼ਾਨੀ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `7001` | `AI_7001_NSN` | ਨਾਟੋ ਸਟਾਕ ਨੰਬਰ (NSN). |
| `7002` | `AI_7002_MEAT_CUT` | UN/ECE ਮੀਟ ਕਾਰਕਾਸ ਅਤੇ ਕੱਟ ਵਰਗੀਕਰਨ. |
| `7004` | `AI_7004_ACTIVE_POTENCY` | ਸਰਗਰਮ ਪੋਟੈਂਸੀ. |
| `7005` | `AI_7005_CATCH_AREA` | ਫੜਨ ਦਾ ਖੇਤਰ. |
| `7008` | `AI_7008_AQUATIC_SPECIES` | ਮੱਛੀ ਪਾਲਣ ਦੇ ਉਦੇਸ਼ਾਂ ਲਈ ਪ੍ਰਜਾਤੀ. |
| `7009` | `AI_7009_FISHING_GEAR_TYPE` | ਮੱਛੀ ਫੜਨ ਦੇ ਸਾਜ਼ੋ-ਸਾਮਾਨ ਦੀ ਕਿਸਮ. |
| `7010` | `AI_7010_PROD_METHOD` | ਉਤਪਾਦਨ ਵਿਧੀ. |
| `7020` | `AI_7020_REFURB_LOT` | ਮੁਰੰਮਤ ਲਾਟ ID. |
| `7021` | `AI_7021_FUNC_STAT` | ਕਾਰਜਸ਼ੀਲ ਸਥਿਤੀ. |
| `7022` | `AI_7022_REV_STAT` | ਸੋਧ ਸਥਿਤੀ. |
| `7023` | `AI_7023_GIAI_ASSEMBLY` | ਇੱਕ ਅਸੈਂਬਲੀ ਦਾ ਗਲੋਬਲ ਵਿਅਕਤੀਗਤ ਸੰਪਤੀ ਪਛਾਣਕਰਤਾ (GIAI). |
| `7030`–`7039` | `AI_7030_PROCESSOR_0` – `AI_7039_PROCESSOR_9` | ਤਿੰਨ-ਅੰਕੀ ISO ਦੇਸ਼ ਕੋਡ ਸਮੇਤ ਸੋਧਕ ਦਾ ਨੰਬਰ (10 ਥਾਂਵਾਂ)।. |
| `7040` | `AI_7040_UIC_EXT` | GS1 UIC ਐਕਸਟੈਂਸ਼ਨ 1 ਅਤੇ ਦਰਾਮਦਕਾਰ ਸੂਚਕਾਂਕ ਨਾਲ. |
| `7041` | `AI_7041_UFRGT_UNIT_TYPE` | UN/CEFACT ਫਰੇਟ ਯੂਨਿਟ ਕਿਸਮ. |

### ਕੌਮੀ ਸਿਹਤ-ਸੰਭਾਲ ਮੁੜ-ਭੁਗਤਾਨ ਨੰਬਰ (NHRN)

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `710` | `AI_710_NHRN_PZN` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਜਰਮਨੀ PZN. |
| `711` | `AI_711_NHRN_CIP` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਫਰਾਂਸ CIP. |
| `712` | `AI_712_NHRN_CN` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਸਪੇਨ CN. |
| `713` | `AI_713_NHRN_DRN` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਬ੍ਰਾਜ਼ੀਲ DRN. |
| `714` | `AI_714_NHRN_AIM` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਪੁਰਤਗਾਲ AIM. |
| `715` | `AI_715_NHRN_NDC` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਸੰਯੁਕਤ ਰਾਜ ਅਮਰੀਕਾ NDC. |
| `716` | `AI_716_NHRN_AIC` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਇਟਲੀ AIC. |
| `717` | `AI_717_NHRN_SRN` | ਰਾਸ਼ਟਰੀ ਸਿਹਤ ਸੰਭਾਲ ਮੁੜ-ਅਦਾਇਗੀ ਨੰਬਰ (NHRN) - ਕੋਸਟਾ ਰੀਕਾ ਸੈਨੇਟਰੀ ਰਜਿਸਟਰ ਨੰਬਰ. |

### ਸਿਹਤ-ਸੰਭਾਲ, GMN, HIDRI, CPID, ਵਿਅਕਤੀ ਦਾ ਡਾਟਾ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `7230`–`7239` | `AI_7230_CERT_0` – `AI_7239_CERT_9` | ਪ੍ਰਮਾਣੀਕਰਨ ਹਵਾਲਾ (10 ਥਾਂਵਾਂ)।. |
| `7240` | `AI_7240_PROTOCOL` | ਪ੍ਰੋਟੋਕੋਲ ID. |
| `7241` | `AI_7241_AIDC_MEDIA_TYPE` | AIDC ਮੀਡੀਆ ਦੀ ਕਿਸਮ. |
| `7242` | `AI_7242_VCN` | ਵਰਜਨ ਕੰਟਰੋਲ ਨੰਬਰ (VCN). |
| `7250` | `AI_7250_DOB` | ਜਨਮ ਮਿਤੀ (YYYYMMDD). |
| `7251` | `AI_7251_DOB_TIME` | ਜਨਮ ਦੀ ਮਿਤੀ ਅਤੇ ਸਮਾਂ (YYYYMMDDhhmm). |
| `7252` | `AI_7252_BIO_SEX` | ਜੈਵਿਕ ਲਿੰਗ. |
| `7253` | `AI_7253_FAMILY_NAME` | ਵਿਅਕਤੀ ਦਾ ਪਰਿਵਾਰਕ ਨਾਮ. |
| `7254` | `AI_7254_GIVEN_NAME` | ਵਿਅਕਤੀ ਦਾ ਪਹਿਲਾ ਨਾਮ. |
| `7255` | `AI_7255_SUFFIX` | ਵਿਅਕਤੀ ਦੇ ਨਾਮ ਦਾ ਪਿਛੇਤਰ. |
| `7256` | `AI_7256_FULL_NAME` | ਵਿਅਕਤੀ ਦਾ ਪੂਰਾ ਨਾਮ. |
| `7257` | `AI_7257_PERSON_ADDR` | ਵਿਅਕਤੀ ਦਾ ਪਤਾ. |
| `7258` | `AI_7258_BIRTH_SEQUENCE` | ਬੱਚੇ ਦੇ ਜਨਮ ਦਾ ਕ੍ਰਮ. |
| `7259` | `AI_7259_BABY` | ਬੱਚੇ ਦਾ ਪਰਿਵਾਰਕ ਨਾਮ. |
| `8001` | `AI_8001_DIMENSIONS` | ਰੋਲ ਉਤਪਾਦ (ਚੌੜਾਈ, ਲੰਬਾਈ, ਕੋਰ ਵਿਆਸ, ਦਿਸ਼ਾ, ਸਪਲਾਈਸ). |
| `8002` | `AI_8002_CMT_NO` | ਸੈਲੂਲਰ ਮੋਬਾਈਲ ਟੈਲੀਫੋਨ ਪਛਾਣਕਰਤਾ. |
| `8003` | `AI_8003_GRAI` | ਗਲੋਬਲ ਵਾਪਸੀਯੋਗ ਸੰਪਤੀ ਪਛਾਣਕਰਤਾ (GRAI). |
| `8004` | `AI_8004_GIAI` | ਗਲੋਬਲ ਵਿਅਕਤੀਗਤ ਸੰਪਤੀ ਪਛਾਣਕਰਤਾ (GIAI). |
| `8005` | `AI_8005_PRICE_PER_UNIT` | ਪ੍ਰਤੀ ਯੂਨਿਟ ਮਾਪ ਕੀਮਤ. |
| `8006` | `AI_8006_ITIP` | ਵਿਅਕਤੀਗਤ ਵਪਾਰ ਵਸਤੂ ਟੁਕੜੇ ਦੀ ਪਛਾਣ (ITIP). |
| `8007` | `AI_8007_IBAN` | ਅੰਤਰਰਾਸ਼ਟਰੀ ਬੈਂਕ ਖਾਤਾ ਨੰਬਰ (IBAN). |
| `8008` | `AI_8008_PROD_TIME` | ਉਤਪਾਦਨ ਦੀ ਮਿਤੀ ਅਤੇ ਸਮਾਂ (YYMMDDhh[mm[ss]]). |
| `8009` | `AI_8009_OPTSEN` | ਆਪਟੀਕਲੀ ਰੀਡੇਬਲ ਸੈਂਸਰ ਸੂਚਕ. |
| `8010` | `AI_8010_CPID` | ਹਿੱਸਾ/ਪੁਰਜ਼ਾ ਪਛਾਣਕਰਤਾ (CPID). |
| `8011` | `AI_8011_CPID_SERIAL` | ਹਿੱਸਾ/ਪੁਰਜ਼ਾ ਪਛਾਣਕਰਤਾ ਸੀਰੀਅਲ ਨੰਬਰ (CPID SERIAL). |
| `8012` | `AI_8012_VERSION` | ਸਾਫਟਵੇਅਰ ਵਰਜਨ. |
| `8013` | `AI_8013_GMN` | ਗਲੋਬਲ ਮਾਡਲ ਨੰਬਰ (GMN). |
| `8014` | `AI_8014_MUDI` | ਬਹੁਤ ਹੀ ਵਿਅਕਤੀਗਤ ਯੰਤਰ ਰਜਿਸਟ੍ਰੇਸ਼ਨ ਪਛਾਣਕਰਤਾ (HIDRI). |
| `8017` | `AI_8017_GSRN_PROVIDER` | ਸੇਵਾ ਪੇਸ਼ਕਸ਼ ਕਰਨ ਵਾਲੀ ਸੰਸਥਾ ਅਤੇ ਸੇਵਾ ਪ੍ਰਦਾਤਾ ਵਿਚਕਾਰ ਸਬੰਧ ਦੀ ਪਛਾਣ ਲਈ ਗਲੋਬਲ ਸੇਵਾ ਸਬੰਧ ਨੰਬਰ (GSRN). |
| `8018` | `AI_8018_GSRN_RECIPIENT` | ਸੇਵਾ ਪੇਸ਼ਕਸ਼ ਕਰਨ ਵਾਲੀ ਸੰਸਥਾ ਅਤੇ ਸੇਵਾ ਪ੍ਰਾਪਤਕਰਤਾ ਵਿਚਕਾਰ ਸਬੰਧ ਦੀ ਪਛਾਣ ਲਈ ਗਲੋਬਲ ਸੇਵਾ ਸਬੰਧ ਨੰਬਰ (GSRN). |
| `8019` | `AI_8019_SRIN` | ਸੇਵਾ ਸਬੰਧ ਇੰਸਟੈਂਸ ਨੰਬਰ (SRIN). |
| `8020` | `AI_8020_REF_NO` | ਭੁਗਤਾਨ ਸਲਿੱਪ ਹਵਾਲਾ ਨੰਬਰ. |
| `8026` | `AI_8026_ITIP_CONTENT` | ਲੌਜਿਸਟਿਕ ਯੂਨਿਟ ਵਿੱਚ ਸ਼ਾਮਲ ਵਪਾਰ ਵਸਤੂ ਦੇ ਟੁਕੜਿਆਂ (ITIP) ਦੀ ਪਛਾਣ. |
| `8030` | `AI_8030_DIGSIG` | ਡਿਜੀਟਲ ਦਸਤਖ਼ਤ (DigSig). |
| `8040` | `AI_8040_IMEI` | ਅੰਤਰਰਾਸ਼ਟਰੀ ਮੋਬਾਈਲ ਉਪਕਰਨ ਪਛਾਣ (IMEI). |
| `8041` | `AI_8041_IMEI2` | ਅੰਤਰਰਾਸ਼ਟਰੀ ਮੋਬਾਈਲ ਉਪਕਰਨ ਪਛਾਣ 2 (IMEI2). |
| `8042` | `AI_8042_ESIM` | ਏਮਬੈਡਡ SIM ਨੰਬਰ. |
| `8043` | `AI_8043_PSIM` | ਭੌਤਿਕ SIM ਨੰਬਰ. |
| `8110` | `AI_8110` | ਉੱਤਰੀ ਅਮਰੀਕਾ ਵਿੱਚ ਵਰਤੋਂ ਲਈ ਕੂਪਨ ਕੋਡ ਪਛਾਣ. |
| `8111` | `AI_8111_POINTS` | ਕੂਪਨ ਦੇ ਵਫ਼ਾਦਾਰੀ ਪੁਆਇੰਟ. |
| `8112` | `AI_8112` | ਉੱਤਰੀ ਅਮਰੀਕਾ ਵਿੱਚ ਵਰਤੋਂ ਲਈ ਪਾਜ਼ੇਟਿਵ ਆਫ਼ਰ ਫਾਈਲ ਕੂਪਨ ਕੋਡ ਪਛਾਣ. |
| `8200` | `AI_8200_PRODUCT_URL` | ਵਿਸਤ੍ਰਿਤ ਪੈਕੇਜਿੰਗ URL. |

### ਅੰਦਰੂਨੀ / ਕੰਪਨੀ ਦੀ ਵਰਤੋਂ

| AI | ਸਥਿਰਾਂਕ | ਵੇਰਵਾ |
|----|----------|-------------|
| `90` | `AI_90_INTERNAL` | ਵਪਾਰਕ ਭਾਈਵਾਲਾਂ ਵਿਚਕਾਰ ਆਪਸੀ ਸਹਿਮਤੀ ਵਾਲੀ ਜਾਣਕਾਰੀ. |
| `91`–`99` | `AI_91_INTERNAL` – `AI_99_INTERNAL` | ਕੰਪਨੀ ਦੀ ਅੰਦਰੂਨੀ ਜਾਣਕਾਰੀ (9 ਥਾਂਵਾਂ)।. |

---

## ਅੰਤਿਕਾ B — ਵਿਆਖਿਆ ਕੁੰਜੀ ਸਥਿਰਾਂਕ

ਜਦੋਂ `GaiaParser.parse()` ਨੂੰ `ParseMode.INTERPRETATION` ਨਾਲ ਸੱਦਿਆ ਜਾਵੇ, ਤਾਂ ਹਰ `GS1AIObjectElement` ਨਾਲ ਖੇਤਰ-ਵਿਸ਼ੇਸ਼ ਭਰਪੂਰਕਾਂ ਵੱਲੋਂ ਬਣਾਈਆਂ `GS1AIInterpretation` ਵਸਤਾਂ ਦੀ ਸੂਚੀ ਹੋ ਸਕਦੀ ਹੈ। ਖ਼ਾਸ ਵਿਆਖਿਆ ਮੁੱਲ ਲੱਭਣ ਲਈ ਕੁੰਜੀਆਂ ਵਜੋਂ `GS1Constants_Enricher` (ਪੈਕੇਜ `tools.pantheum.gaia.gs1.constants` ਵਿੱਚ) ਦੇ ਸਥਿਰਾਂਕ ਵਰਤੋ:

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

ਵਿਖਾਵੇ ਵਾਲੇ ਲੇਬਲ ਸਥਿਰਾਂਕ **ਨਹੀਂ** ਹਨ — ਉਹ `gaia/src/main/resources/localization/<LANG>/interpretation_labels_<LANG>.json` ਹੇਠਲੀਆਂ ਸਥਾਨਕ ਸੂਚੀਆਂ ਵਿੱਚ, ਕਿਸਮ-ਸਥਿਰਾਂਕ ਨਾਲ ਕੁੰਜੀਬੱਧ ਹੋ ਕੇ ਰਹਿੰਦੇ ਹਨ। `GS1AIInterpretation.getLabel()` ਪਾਰਸ ਭਾਸ਼ਾ ਦਾ ਲੇਬਲ ਮੋੜਦਾ ਹੈ (ਵੇਖੋ [ਸਥਾਨਕ ਸੁਨੇਹੇ ਤੇ ਲੇਬਲ](#ਸਥਨਕ-ਭਸ-ਵਚ-ਸਨਹ-ਤ-ਲਬਲ)), ਤੇ ਜਦੋਂ ਕੋਈ ਸੂਚੀ ਉਹ ਕੁੰਜੀ ਛੱਡ ਦੇਵੇ ਤਾਂ ਅੰਗਰੇਜ਼ੀ ਉੱਤੇ ਮੁੜ ਆਉਂਦਾ ਹੈ। ਹੇਠਲਾ "ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ" ਕਾਲਮ ਪੰਜਾਬੀ ਪਾਠ ਦੱਸਦਾ ਹੈ; ਕਿਸਮ-ਕੁੰਜੀਆਂ ਆਪ ਹਰ ਭਾਸ਼ਾ ਵਿੱਚ ਇੱਕੋ ਜਿਹੀਆਂ ਰਹਿੰਦੀਆਂ ਹਨ, ਸੋ ਕੁੰਜੀ ਉੱਤੇ ਮੇਲ ਕਰੋ, ਲੇਬਲ ਉੱਤੇ ਕਦੇ ਨਹੀਂ।

### ਮਿਤੀ ਤੇ ਸਮਾਂ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `DATE_VALUE` | ਮਿਤੀ | ਮਿਤੀ ਵਾਲੇ AI (11–17, 7003, 7006, 7011, ਆਦਿ) |
| `DATE_FORMAT` | ਮਿਤੀ ਫਾਰਮੈਟ | ਮਿਤੀ ਵਾਲੇ AI |
| `TIME_VALUE` | ਸਮਾਂ | ਸਮਾਂ ਚੁੱਕਣ ਵਾਲੇ AI (7003, 7011, 8008, ਆਦਿ) |
| `TIME_FORMAT` | ਸਮਾਂ ਫਾਰਮੈਟ | ਸਮਾਂ ਚੁੱਕਣ ਵਾਲੇ AI |
| `DATETIME_VALUE` | ਮਿਤੀ ਤੇ ਸਮਾਂ | ਮਿਤੀ+ਸਮਾਂ ਵਾਲੇ AI |
| `DATETIME_FORMAT` | ਮਿਤੀ ਤੇ ਸਮਾਂ ਫਾਰਮੈਟ | ਮਿਤੀ+ਸਮਾਂ ਵਾਲੇ AI |

### ਵਾਢੀ ਦੀ ਮਿਤੀ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `HARVEST_START_DATE` | ਵਾਢੀ ਸ਼ੁਰੂ ਮਿਤੀ | AI 7007 |
| `HARVEST_END_DATE` | ਵਾਢੀ ਸਮਾਪਤੀ ਮਿਤੀ | AI 7007 (ਮਰਜ਼ੀ ਵਾਲਾ ਹੱਦ-ਅੰਤ) |
| `HARVEST_DATE_RANGE` | ਵਾਢੀ ਮਿਤੀ ਸੀਮਾ | AI 7007 |

### GS1 ਕੰਪਨੀ ਅਗੇਤਰ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `GS1_COMPANY_PREFIX` | GS1 ਕੰਪਨੀ ਅਗੇਤਰ | GTIN / GLN / SSCC ਵਾਲੇ AI |
| `GS1_MEMBER_CODE` | GS1 ਮੈਂਬਰ ਕੋਡ | GTIN / GLN / SSCC ਵਾਲੇ AI |
| `GS1_MEMBER_NAME` | GS1 ਮੈਂਬਰ ਸੰਸਥਾ | GTIN / GLN / SSCC ਵਾਲੇ AI |

### GTIN

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `GTIN_TYPE` | GTIN ਕਿਸਮ | AI 01, 02 |
| `GTIN_NATIVE` | GTIN | AI 01, 02 |
| `PACKAGING_LEVEL` | ਪੈਕੇਜਿੰਗ ਪੱਧਰ | AI 01 |
| `GTIN_CHECK_DIGIT` | ਜਾਂਚ ਅੰਕ | AI 01, 02 |

### SSCC

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `SSCC_EXTENSION_DIGIT` | ਵਿਸਤਾਰ ਅੰਕ | AI 00 |
| `SSCC_SERIAL_REFERENCE` | ਸੀਰੀਅਲ ਹਵਾਲਾ | AI 00 |
| `SSCC_CHECK_DIGIT` | ਜਾਂਚ ਅੰਕ | AI 00 |

### ਦੇਸ਼ (ISO 3166)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `COUNTRY_CODE_NUMERIC` | ਦੇਸ਼ ਕੋਡ (ਸੰਖਿਆਤਮਕ) | ਇੱਕ-ਦੇਸ਼ ਵਾਲੇ AI (422, 424–426, 4307, 4317, 421, 7030–7039) |
| `COUNTRY_CODE_ALPHA2` | ਦੇਸ਼ ਕੋਡ (ਅਲਫ਼ਾ-2) | ਅਲਫ਼ਾ-2 ਦੇਸ਼ ਵਾਲੇ AI |
| `COUNTRY_NAME` | ਦੇਸ਼ ਦਾ ਨਾਮ | ਇੱਕ-ਦੇਸ਼ ਵਾਲੇ AI |
| `COUNTRY_LIST` | ਦੇਸ਼ | AI 423 — ਸਾਰੇ ਨਾਮ ਜੋੜ ਕੇ, ਜਿਵੇਂ `Australia, New Zealand` |

AI 423 (ਪਹਿਲੀ ਸੋਧ ਦਾ ਦੇਸ਼) ਪੰਜ ਦੇਸ਼ਾਂ ਤੱਕ ਚੁੱਕ ਸਕਦਾ ਹੈ, ਸੋ ਇਹ **ਹਰ ਦੇਸ਼ ਲਈ ਇੱਕ ਨੰਬਰ ਵਾਲਾ
ਜੋੜਾ** ਕੱਢਦਾ ਹੈ — `COUNTRY_CODE_NUMERIC_1`, `COUNTRY_NAME_1`,
`COUNTRY_CODE_NUMERIC_2`, `COUNTRY_NAME_2`, … — ਤੇ ਉਸ ਮਗਰੋਂ ਇੱਕੋ `COUNTRY_LIST`
ਸਾਰ। ਇਹ ਕੁੰਜੀਆਂ `COUNTRY_CODE_NUMERIC_PREFIX` / `COUNTRY_NAME_PREFIX` ਸਥਿਰਾਂਕਾਂ ਨਾਲ
1-ਤੋਂ-ਸ਼ੁਰੂ ਹੋਣ ਵਾਲਾ ਸੂਚਕ ਜੋੜ ਕੇ ਬਣਾਓ, ਜਾਂ ਸਿੱਧਾ `getInterpretations()` ਉੱਤੇ ਗੇੜਾ ਲਾਓ; ਬਿਨਾਂ
ਪਿਛੇਤਰ ਵਾਲੀਆਂ `COUNTRY_CODE_NUMERIC` / `COUNTRY_NAME` ਕੁੰਜੀਆਂ AI 423 ਲਈ **ਨਹੀਂ** ਨਿਕਲਦੀਆਂ।

### ਮੁਦਰਾ (ISO 4217)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `CURRENCY_CODE` | ਮੁਦਰਾ ਕੋਡ | ਮੁਦਰਾ ਸਮੇਤ ਰਕਮ ਵਾਲੇ AI (391n, 393n) |
| `CURRENCY_ALPHA` | ਮੁਦਰਾ ਅੱਖਰੀ ਕੋਡ | ਮੁਦਰਾ ਸਮੇਤ ਰਕਮ ਵਾਲੇ AI |
| `CURRENCY_NAME` | ਮੁਦਰਾ ਦਾ ਨਾਮ | ਮੁਦਰਾ ਸਮੇਤ ਰਕਮ ਵਾਲੇ AI |

### ਤਾਪਮਾਨ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `TEMPERATURE` | ਤਾਪਮਾਨ | AI 4330–4333 |
| `TEMPERATURE_UNIT` | ਤਾਪਮਾਨ ਇਕਾਈ | AI 4330–4333 |
| `TEMPERATURE_FORMATTED` | ਤਾਪਮਾਨ (ਫਾਰਮੈਟ ਕੀਤਾ) | AI 4330–4333 |

### ਲਿੰਗ (ISO 5218)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `SEX_CODE` | ਲਿੰਗ ਕੋਡ | AI 7252 |
| `SEX_DESCRIPTION` | ਲਿੰਗ ਵੇਰਵਾ | AI 7252 |

### ਜਲ-ਜੀਵ ਜਾਤੀਆਂ (FAO)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `SPECIES_CODE` | ਪ੍ਰਜਾਤੀ ਕੋਡ | AI 7008 |
| `SPECIES_SCIENTIFIC` | ਵਿਗਿਆਨਕ ਨਾਮ | AI 7008 |
| `SPECIES_ENGLISH` | ਆਮ ਨਾਮ | AI 7008 |
| `SPECIES_FAMILY` | ਪਰਿਵਾਰ | AI 7008 |
| `SPECIES_ORDER` | ਗਣ | AI 7008 |

### NATO ਸਟਾਕ ਨੰਬਰ (NSN)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `NSN_FSG` | ਸਪਲਾਈ ਸਮੂਹ | AI 7001 |
| `NSN_FSG_NAME` | ਸਪਲਾਈ ਸਮੂਹ ਦਾ ਨਾਮ | AI 7001 |
| `NSN_FSCG` | ਸਪਲਾਈ ਸ਼੍ਰੇਣੀ | AI 7001 |
| `NSN_FSCG_NAME` | ਸਪਲਾਈ ਸ਼੍ਰੇਣੀ ਦਾ ਨਾਮ | AI 7001 |
| `NSN_NCB_COUNTRY_CODE` | ਦੇਸ਼ ਕੋਡ | AI 7001 |
| `NSN_NCB_COUNTRY_NAME` | ਦੇਸ਼ | AI 7001 |
| `NSN_NCB_COUNTRY_CTR` | ISO ਦੇਸ਼ ਕੋਡ | AI 7001 |
| `NSN_NCB_COUNTRY_CAT` | NCS ਸ਼੍ਰੇਣੀ | AI 7001 |
| `NSN_NIIN` | ਰਾਸ਼ਟਰੀ ਆਈਟਮ ਨੰਬਰ | AI 7001 |
| `NSN_FORMATTED` | NSN | AI 7001 |

### ਰੋਲ ਵਾਲੀਆਂ ਵਸਤਾਂ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `ROLL_WIDTH` | ਰੋਲ ਚੌੜਾਈ (mm) | AI 8001 |
| `ROLL_LENGTH` | ਰੋਲ ਲੰਬਾਈ (m) | AI 8001 |
| `CORE_DIAMETER` | ਕੋਰ ਵਿਆਸ (mm) | AI 8001 |
| `WINDING_DIRECTION_CODE` | ਵਲੇਟਣ ਦਿਸ਼ਾ ਕੋਡ | AI 8001 |
| `WINDING_DIRECTION` | ਵਲੇਟਣ ਦਿਸ਼ਾ | AI 8001 |
| `SPLICES` | ਜੋੜ | AI 8001 |

### IBAN

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `IBAN_COUNTRY_CODE` | ਦੇਸ਼ ਕੋਡ | AI 8007 |
| `IBAN_COUNTRY_NAME` | ਦੇਸ਼ | AI 8007 |
| `IBAN_CHECK_DIGITS` | ਜਾਂਚ ਅੰਕ | AI 8007 |
| `IBAN_CHECK_VALID` | ਜਾਂਚ | AI 8007 |
| `IBAN_BBAN` | BBAN | AI 8007 |

### IMEI

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `IMEI_RBI` | Reporting Body Identifier (RBI) | AI 8040, 8041 |
| `IMEI_TAC` | Type Allocation Code (TAC) | AI 8040, 8041 |
| `IMEI_SERIAL` | ਸੀਰੀਅਲ ਨੰਬਰ | AI 8040, 8041 |
| `IMEI_CHECK_DIGIT` | ਜਾਂਚ ਅੰਕ | AI 8040, 8041 |
| `IMEI_FORMATTED` | IMEI | AI 8040, 8041 |
| `IMEI_RBI_NAME` | ਜਾਰੀ ਕਰਨ ਵਾਲੀ ਸੰਸਥਾ | AI 8040, 8041 |

15 ਅੰਕ ਇਉਂ ਵੰਡੇ ਜਾਂਦੇ ਹਨ: `[ TAC (8) ][ ਸੀਰੀਅਲ (6) ][ Luhn ਜਾਂਚ ਅੰਕ (1) ]`, ਜਿੱਥੇ
RBI, TAC ਦੇ ਪਹਿਲੇ 2 ਅੰਕ ਹੁੰਦਾ ਹੈ — ਸੋ `IMEI_RBI` `IMEI_TAC` ਦਾ ਅਗੇਤਰ ਹੈ, ਕੋਈ ਵੱਖਰਾ
ਟੋਟਾ ਨਹੀਂ। `IMEI_FORMATTED` ਮਿਆਰੀ GSMA ਵਿਖਾਵੇ ਵਾਲੀ ਵੰਡ `AA-BBBBBB-CCCCCC-D` ਵਿਖਾਉਂਦਾ ਹੈ
(ਜਿਵੇਂ `49-015420-323751-8`), ਜੋ TAC ਨੂੰ RBI ਵਾਲੀ ਹੱਦ ਉੱਤੇ ਵੰਡਦੀ ਹੈ; ਪੁਰਾਣੀ `6-2-6-1`
ਵੰਡ, ਜੋ ਓਥੋਂ ਕੱਟਦੀ ਹੈ ਜਿੱਥੋਂ ਬੰਦ ਕੀਤਾ ਜਾ ਚੁੱਕਾ Final Assembly Code ਸ਼ੁਰੂ ਹੁੰਦਾ ਸੀ, ਨਹੀਂ ਕੱਢੀ ਜਾਂਦੀ।

`IMEI_RBI_NAME` `ImeiRbiData` ਰਾਹੀਂ RBI ਨੂੰ ਵੰਡ ਕਰਨ ਵਾਲੀ ਸੰਸਥਾ ਦੇ ਨਾਮ ਤੱਕ ਹੱਲ ਕਰਦਾ ਹੈ, ਤੇ ਇਹ
**ਸਭ ਤੋਂ ਅਖ਼ੀਰ ਵਿੱਚ ਤੇ ਸਿਰਫ਼ ਓਦੋਂ ਜੋੜਿਆ ਜਾਂਦਾ ਹੈ ਜਦੋਂ ਕੋਡ ਓਥੇ ਦਰਜ ਹੋਵੇ**। ਉਹ ਸਾਰਣੀ ਤਿੰਨ ਟੋਲੀਆਂ ਢਕਦੀ ਹੈ:

- **ਇਸ ਵੇਲੇ ਵੰਡ ਕਰਨ ਵਾਲੀਆਂ** — `01` CTIA/PTCRB, `35` TÜV SÜD BABT, `86` TAF, ਤੇ ਨਾਲ `99`
  Global Hexadecimal Administrator ਤੇ `98` (ਰਾਖਵਾਂ)।
- **ਪਰਖ ਵਾਲੀਆਂ ਹੱਦਾਂ** — `00` ਤੇ `02`–`09`, ਜੋ ਅਸਲੀ ਵੰਡ ਦੀ ਥਾਂ ਪਰਖ ਵਾਲੇ IMEI ਦੱਸਦੀਆਂ ਹਨ।
  `ImeiRbiData.isTestCode(code)` ਨਾਲ ਪੁੱਛੋ।
- **ਹੁਣ ਵੰਡ ਨਾ ਕਰਨ ਵਾਲੀਆਂ** — ਇਤਿਹਾਸਕ ਸੰਸਥਾਵਾਂ ਜਿਵੇਂ `49` (BZT/BAPT, ਜਰਮਨੀ), `44`
  (BABT, ਯੂਕੇ) ਜਾਂ `91` (MSAI, ਭਾਰਤ)। `ImeiRbiData.isNoLongerAllocating(code)` ਨਾਲ ਪੁੱਛੋ।
  ਇਹ ਕੋਡ ਚੁੱਕਣ ਵਾਲੇ ਯੰਤਰ ਆਮ ਹਨ ਤੇ ਵਰਤੋਂ ਵਿੱਚ ਬਣੇ ਹੋਏ ਹਨ; ਸਿਰਫ਼ ਨਵੀਂ ਵੰਡ ਰੁਕੀ ਹੈ, ਸੋ ਇਹ
  ਦੱਸਣ ਵਾਲੀ ਜਾਣਕਾਰੀ ਹੈ, ਜਾਇਜ਼ਪੁਣੇ ਦਾ ਸੰਕੇਤ ਕਦੇ ਨਹੀਂ।

`IMEI_RBI_NAME` ਦੀ ਗ਼ੈਰ-ਹਾਜ਼ਰੀ ਦਾ ਮਤਲਬ ਹੈ "ਇਹ RBI ਸਾਡੀ ਸਾਰਣੀ ਵਿੱਚ ਨਹੀਂ", **ਨਾ ਕਿ** "ਨਾਜਾਇਜ਼ IMEI":
ਇਹ ਸਾਰਣੀ ਸਿੱਧੀ GSMA ਦੀ ਥਾਂ ਕਿਸੇ ਛਪੀ ਹੋਈ RBI ਸੂਚੀ ਤੋਂ ਬਣੀ ਹੈ, ਸੋ ਇਹ ਨਵੀਂ ਥਾਪੀ ਸੰਸਥਾ ਤੋਂ
ਪਿੱਛੇ ਰਹਿ ਸਕਦੀ ਹੈ। ਇਸ ਦੀ ਗ਼ੈਰ-ਹਾਜ਼ਰੀ ਤੋਂ ਕੋਈ ਤਸਦੀਕ ਨਤੀਜਾ ਨਾ ਕੱਢੋ; RBI ਕੋਈ ਜਾਂਚ ਅੱਖਰ ਨਹੀਂ।
ਜੋ ਕੋਡ ਵਿਆਖਿਆ ਸੂਚੀ ਉੱਤੇ ਗੇੜਾ ਲਾਉਂਦਾ ਹੈ, ਉਸ ਨੂੰ ਥਾਂ ਦੇ ਸੂਚਕ ਨਾਲ ਪਹੁੰਚ ਕਰਨ ਦੀ ਥਾਂ ਇਸ ਦੀ ਗ਼ੈਰ-ਹਾਜ਼ਰੀ ਸਹਿਣੀ ਚਾਹੀਦੀ ਹੈ।

### SIM ਪਛਾਣਕਾਰ (EID / ICCID)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `SIM_MII` | Major Industry Identifier (MII) | AI 8042, 8043 |
| `SIM_MII_NAME` | ਉਦਯੋਗ ਸ਼੍ਰੇਣੀ | AI 8042 |
| `EID_BODY` | EID ਮੁੱਖ ਭਾਗ | AI 8042 |
| `EID_CHECK_DIGIT` | ਜਾਂਚ ਅੰਕ | AI 8042 |
| `ICCID_BODY` | ICCID ਮੁੱਖ ਭਾਗ | AI 8043 |
| `ICCID_EXTENSION` | ਵਿਸਤਾਰ | AI 8043 |

`SIM_MII` ਪਹਿਲੇ **ਦੋ** ਅੰਕ (`89`) ਚੁੱਕਦਾ ਹੈ, ਉਹ ਜੋੜਾ ਜੋ ITU-T E.118 ਦੂਰ-ਸੰਚਾਰ ਨੂੰ ਦਿੰਦਾ ਹੈ।
ISO/IEC 7812 ਆਪ MII ਨੂੰ **ਸਿਰਫ਼ ਪਹਿਲਾ ਅੰਕ** ਦੱਸਦਾ ਹੈ, ਸੋ `SIM_MII_NAME` ਉਸ ਪਹਿਲੇ `8` ਤੋਂ
`Iso7812Data` ਰਾਹੀਂ ਸ਼੍ਰੇਣੀ ਹੱਲ ਕਰਦਾ ਹੈ — ਜਿਸ ਤੋਂ ਮਿਲਦਾ ਹੈ "Healthcare, telecommunications
and other future industry assignments"। ਸੋ ਠੀਕ-ਬਣੇ EID ਲਈ ਇਹ ਹਮੇਸ਼ਾ ਇੱਕੋ ਜਿਹਾ ਰਹਿੰਦਾ ਹੈ; ਇਹ
ਮਿਆਰ ਤੱਕ ਪੈੜ ਲੱਭਣ ਲਈ ਦੱਸਿਆ ਜਾਂਦਾ ਹੈ, ਨਿਖੇੜਾ ਕਰਨ ਵਾਲੇ ਵਜੋਂ ਨਹੀਂ। `Iso7812Data.nameForCode(digit)`
ਸਾਦਾ ਅੰਕ ਲੈਂਦਾ ਹੈ, `nameForIdentifier(prefix)` ਲੰਮਾ ਅਗੇਤਰ ਲੈ ਕੇ ਉਸ ਦਾ ਪਹਿਲਾ ਅੰਕ ਪੜ੍ਹਦਾ ਹੈ।

`SIM_MII_NAME` ਸਿਰਫ਼ `EidEnricher` (AI 8042) ਕੱਢਦਾ ਹੈ। `IccidEnricher` (AI 8043)
`SIM_MII` ਤਾਂ ਦਿੰਦਾ ਹੈ ਪਰ ਸ਼੍ਰੇਣੀ ਤੋਂ ਬਿਨਾਂ।

### ਪ੍ਰਮਾਣੀਕਰਨ ਹਵਾਲਾ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `CERT_SEQUENCE` | ਕ੍ਰਮ ਸੰਖਿਆ | AI 7230–7239 |
| `CERT_SCHEME_CODE` | ਪ੍ਰਮਾਣੀਕਰਨ ਸਕੀਮ ਕੋਡ | AI 7230–7239 |
| `CERT_SCHEME_NAME` | ਪ੍ਰਮਾਣੀਕਰਨ ਸਕੀਮ | AI 7230–7239 |
| `CERT_REFERENCE` | ਪ੍ਰਮਾਣੀਕਰਨ ਹਵਾਲਾ | AI 7230–7239 |

### GS1 UIC

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `UIC_CODE` | UIC ਕੋਡ | AI 7040 |
| `UIC_EXTENSION_1` | ਵਿਸਤਾਰ 1 | AI 7040 |
| `UIC_IMPORTER_INDEX` | ਆਯਾਤਕ ਸੂਚਕਾਂਕ | AI 7040 |

### ਬੱਚੇ ਦੇ ਜਨਮ ਦੀ ਤਰਤੀਬ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `BIRTH_POSITION` | ਜਨਮ ਸਥਿਤੀ | AI 7258 |
| `BIRTH_TOTAL` | ਕੁੱਲ ਜਨਮ | AI 7258 |
| `BIRTH_SEQUENCE` | ਜਨਮ ਕ੍ਰਮ | AI 7258 |

### ਗਲੋਬਲ ਮਾਡਲ ਨੰਬਰ (GMN)

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `GMN_MODEL_REFERENCE` | ਮਾਡਲ ਹਵਾਲਾ | AI 8013 |
| `GMN_CHECK_PAIR` | ਜਾਂਚ ਜੋੜਾ | AI 8013 |

### HIDRI

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `HIDRI_DEVICE_REFERENCE` | ਡਿਵਾਈਸ ਹਵਾਲਾ | AI 8014 |
| `HIDRI_CHECK_PAIR` | ਜਾਂਚ ਜੋੜਾ | AI 8014 |

### CPID

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `CPID_PART_REFERENCE` | ਹਿੱਸਾ ਤੇ ਪੁਰਜ਼ਾ ਹਵਾਲਾ | AI 8010–8011 |

### ਦਸ਼ਮਲਵ ਤੇ ਮਾਪ ਵਾਲੇ ਮੁੱਲ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `DECIMAL_VALUE` | ਦਸ਼ਮਲਵ ਮੁੱਲ | ਲੁਕਵੇਂ ਦਸ਼ਮਲਵ ਸਥਾਨਾਂ ਵਾਲੇ ਸੰਖਿਅਕ AI (31xx–36xx) |
| `DECIMAL_AMOUNT` | ਰਕਮ | ਕੀਮਤ ਵਾਲੇ AI (390n–395n) |
| `DECIMAL_PERCENTAGE` | ਪ੍ਰਤੀਸ਼ਤ | AI 394n |
| `DECIMAL_PLACES` | ਦਸ਼ਮਲਵ ਸਥਾਨ | `DECIMAL_VALUE` / `DECIMAL_AMOUNT` / `DECIMAL_PERCENTAGE` ਦੇ ਨਾਲ |
| `PERCENTAGE_FORMAT` | ਪ੍ਰਤੀਸ਼ਤ ਫਾਰਮੈਟ | AI 394n |
| `ISO_UNIT_CODE` | ISO ਇਕਾਈ ਕੋਡ | ਮਾਪ ਵਾਲੇ AI |
| `ISO_UNIT_NAME` | ISO ਇਕਾਈ ਨਾਮ | ਮਾਪ ਵਾਲੇ AI |
| `MONETARY_AMOUNT` | ਮੁਦਰਾ ਰਕਮ | ਕੀਮਤ ਵਾਲੇ AI |
| `MONETARY_AMOUNT_DISPLAY` | ਮੁਦਰਾ ਰਕਮ (ਫਾਰਮੈਟ ਕੀਤੀ) | ਕੀਮਤ ਵਾਲੇ AI |

### ਭੂਗੋਲਿਕ ਧੁਰੇ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `LATITUDE` | ਅਕਸ਼ਾਂਸ਼ | AI 4309 |
| `LONGITUDE` | ਦੇਸ਼ਾਂਤਰ | AI 4309 |
| `GEO_COORDINATES` | ਭੂਗੋਲਿਕ ਧੁਰੇ | AI 4309 |
| `LATITUDE_DMS` | ਅਕਸ਼ਾਂਸ਼ (DMS) | AI 4309 |
| `LONGITUDE_DMS` | ਦੇਸ਼ਾਂਤਰ (DMS) | AI 4309 |
| `GEO_COORDINATES_DMS` | ਭੂਗੋਲਿਕ ਧੁਰੇ (DMS) | AI 4309 |

### ਉਤਪਾਦਨ ਦਾ ਢੰਗ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `PRODUCTION_METHOD_CODE` | ਉਤਪਾਦਨ ਵਿਧੀ ਕੋਡ | AI 7010 |
| `PRODUCTION_METHOD` | ਉਤਪਾਦਨ ਵਿਧੀ | AI 7010 |

### AIDC ਮਾਧਿਅਮ ਦੀ ਕਿਸਮ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `MEDIA_TYPE_CODE` | AIDC ਮੀਡੀਆ ਕਿਸਮ ਕੋਡ | AI 7241 |
| `MEDIA_TYPE_NAME` | AIDC ਮੀਡੀਆ ਕਿਸਮ | AI 7241 |

### ਕੁੱਲ ਵਿੱਚੋਂ ਟੁਕੜਾ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `PIECE_NUMBER` | ਟੁਕੜਾ ਨੰਬਰ | AI 8006 |
| `PIECE_TOTAL` | ਕੁੱਲ ਟੁਕੜੇ | AI 8006 |
| `PIECE_OF_TOTAL` | ਕੁੱਲ ਵਿੱਚੋਂ ਟੁਕੜਾ | AI 8006 |

### ਹਿੱਸਿਆਂ ਦੀ ਵੰਡ

ਇਹ ਕੁੰਜੀਆਂ ਕਿਸੇ ਜਾਵਾ ਭਰਪੂਰਕ ਦੀ ਥਾਂ `content/ai-content.json` ਵਿੱਚਲੀਆਂ ਐਲਾਨੀਆ ਹਿੱਸਾ-ਵੰਡਾਂ
ਵੱਲੋਂ ਕੱਢੀਆਂ ਜਾਂਦੀਆਂ ਹਨ — ਇਹ ਕਿਸੇ ਸਾਂਝੇ AI ਮੁੱਲ ਦੇ ਨਾਮ ਵਾਲੇ ਹਿੱਸੇ ਸਾਹਮਣੇ ਲਿਆਉਂਦੀਆਂ ਹਨ। ਇਸ
ਅੰਤਿਕਾ ਦੀ ਹਰ ਦੂਜੀ ਕੁੰਜੀ ਤੋਂ ਉਲਟ, ਇਹਨਾਂ ਦਾ **`GS1Constants_Enricher` ਵਿੱਚ ਕੋਈ ਸਥਿਰਾਂਕ ਨਹੀਂ**:
ਅੱਖਰੋ-ਅੱਖਰ ਸਟ੍ਰਿੰਗ ਉੱਤੇ ਮੇਲ ਕਰੋ, ਜਾਂ ਕਿਸਮ `GS1AIInterpretation.getType()` ਤੋਂ ਪੜ੍ਹੋ।

| ਕਿਸਮ ਕੁੰਜੀ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `CHECK_DIGIT` | ਜਾਂਚ ਅੰਕ | AI 253, 255, 402, 410–417, 8003, 8017, 8018 |
| `SERIAL_NUMBER` | ਸੀਰੀਅਲ ਨੰਬਰ | AI 253, 255, 8003 |
| `POSTAL_CODE` | ਡਾਕ ਕੋਡ | AI 421 |
| `PROCESSOR_ID` | ਪ੍ਰੋਸੈਸਰ ਪਛਾਣਕਰਤਾ | AI 7030–7039 |

ਧਿਆਨ ਰੱਖੋ ਕਿ ਇੱਥੇ `CHECK_DIGIT` ਆਮ ਹਿੱਸਾ-ਵੰਡ ਵਾਲੀ ਕੁੰਜੀ ਹੈ, ਜੋ ਉੱਪਰ ਗਿਣੀਆਂ ਭਰਪੂਰਕ-ਵਿਸ਼ੇਸ਼
`GTIN_CHECK_DIGIT`, `SSCC_CHECK_DIGIT`, `IMEI_CHECK_DIGIT` ਤੇ `EID_CHECK_DIGIT`
ਕੁੰਜੀਆਂ ਤੋਂ ਵੱਖਰੀ ਹੈ।

### ਫੁਟਕਲ

| ਕੁੰਜੀ ਸਥਿਰਾਂਕ | ਵਿਖਾਵੇ ਵਾਲਾ ਲੇਬਲ | ਕਿਸ ਤੋਂ ਬਣਦਾ ਹੈ |
|--------------|---------------|-------------|
| `FLAG_VALUE` | ਮੁੱਲ | ਬੂਲੀਅਨ / ਝੰਡੀ ਵਾਲੇ AI (4321–4323) |
| `DECODED_TEXT` | ਡੀਕੋਡ ਕੀਤਾ ਟੈਕਸਟ | ਖੁੱਲ੍ਹੇ-ਪਾਠ ਵਾਲੇ AI |
