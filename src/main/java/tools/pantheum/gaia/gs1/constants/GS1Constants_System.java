package tools.pantheum.gaia.gs1.constants;

/**
 * Classpath locations of the static data resources GAIA loads at startup.
 *
 * <p>Centralises the resource paths used by the content datasets, the AI-content
 * registries, and the localized error-message catalogues, so the on-disk layout of
 * {@code src/main/resources} is described in one place.
 */
public final class GS1Constants_System {

    private GS1Constants_System() {}

    /** Package (with trailing dot) of the content-validator classes instantiated reflectively by name. */
    public static final String CONTENT_VALIDATOR_PACKAGE = "tools.pantheum.gaia.gs1.content.validator.";
    /** Package (with trailing dot) of the interpretation-enricher classes instantiated reflectively by name. */
    public static final String ENRICHER_PACKAGE          = "tools.pantheum.gaia.gs1.interpretation.enricher.";

    // --- Content datasets ---

    /** AI content definitions (used by both the content validators and the interpretation enrichers). */
    public static final String AI_CONTENT                  = "/content/ai-content.json";
    /** GS1 Company Prefix registry. */
    public static final String GS1_COMPANY_PREFIX_REGISTRY = "/content/gs1_company_prefix_registry.json";
    /** ISO 4217 currency codes. */
    public static final String ISO_4217                    = "/content/iso_4217.json";
    /** ISO 3166 country codes. */
    public static final String ISO_3166                    = "/content/iso_3166.json";
    /** FAO ASFIS aquatic species codes. */
    public static final String ASFIS_SPECIES               = "/content/asfis-species.json";
    /** NATO Codification System NCB cataloguing nation codes. */
    public static final String NCB_COUNTRIES               = "/content/ncb-countries.json";
    /** Federal Supply Group (FSG) codes and titles. */
    public static final String FSG_GROUPS                  = "/content/fsg-groups.json";
    /** Federal Supply Class (FSC) codes and titles. */
    public static final String FSC_CLASSES                 = "/content/fsc-classes.json";

    // --- Error-message catalogues (one per supported language) ---

    /** Error-message catalogue — English. */
    public static final String ERROR_MESSAGES_EN = "/localization/EN/error_messages_EN.json";
    /** Error-message catalogue — French. */
    public static final String ERROR_MESSAGES_FR = "/localization/FR/error_messages_FR.json";
    /** Error-message catalogue — Spanish. */
    public static final String ERROR_MESSAGES_ES = "/localization/ES/error_messages_ES.json";
    /** Error-message catalogue — German. */
    public static final String ERROR_MESSAGES_DE = "/localization/DE/error_messages_DE.json";
    /** Error-message catalogue — Italian. */
    public static final String ERROR_MESSAGES_IT = "/localization/IT/error_messages_IT.json";
    /** Error-message catalogue — Portuguese. */
    public static final String ERROR_MESSAGES_PT = "/localization/PT/error_messages_PT.json";
    /** Error-message catalogue — Dutch. */
    public static final String ERROR_MESSAGES_NL = "/localization/NL/error_messages_NL.json";
    /** Error-message catalogue — Polish. */
    public static final String ERROR_MESSAGES_PL = "/localization/PL/error_messages_PL.json";
    /** Error-message catalogue — Russian. */
    public static final String ERROR_MESSAGES_RU = "/localization/RU/error_messages_RU.json";
    /** Error-message catalogue — Ukrainian. */
    public static final String ERROR_MESSAGES_UK = "/localization/UK/error_messages_UK.json";
    /** Error-message catalogue — Czech. */
    public static final String ERROR_MESSAGES_CS = "/localization/CS/error_messages_CS.json";
    /** Error-message catalogue — Swedish. */
    public static final String ERROR_MESSAGES_SV = "/localization/SV/error_messages_SV.json";
    /** Error-message catalogue — Chinese (Simplified). */
    public static final String ERROR_MESSAGES_ZH = "/localization/ZH/error_messages_ZH.json";
    /** Error-message catalogue — Japanese. */
    public static final String ERROR_MESSAGES_JA = "/localization/JA/error_messages_JA.json";
    /** Error-message catalogue — Korean. */
    public static final String ERROR_MESSAGES_KO = "/localization/KO/error_messages_KO.json";
    /** Error-message catalogue — Arabic. */
    public static final String ERROR_MESSAGES_AR = "/localization/AR/error_messages_AR.json";
    /** Error-message catalogue — Indonesian. */
    public static final String ERROR_MESSAGES_ID = "/localization/ID/error_messages_ID.json";
    /** Error-message catalogue — Hindi. */
    public static final String ERROR_MESSAGES_HI = "/localization/HI/error_messages_HI.json";
    /** Error-message catalogue — Turkish. */
    public static final String ERROR_MESSAGES_TR = "/localization/TR/error_messages_TR.json";
    /** Error-message catalogue — Bengali. */
    public static final String ERROR_MESSAGES_BN = "/localization/BN/error_messages_BN.json";
    /** Error-message catalogue — Urdu. */
    public static final String ERROR_MESSAGES_UR = "/localization/UR/error_messages_UR.json";
    /** Error-message catalogue — Vietnamese. */
    public static final String ERROR_MESSAGES_VI = "/localization/VI/error_messages_VI.json";
    /** Error-message catalogue — Nigerian Pidgin. */
    public static final String ERROR_MESSAGES_PCM = "/localization/PCM/error_messages_PCM.json";
    /** Error-message catalogue — Egyptian Arabic. */
    public static final String ERROR_MESSAGES_ARZ = "/localization/ARZ/error_messages_ARZ.json";
    /** Error-message catalogue — Marathi. */
    public static final String ERROR_MESSAGES_MR  = "/localization/MR/error_messages_MR.json";
    /** Error-message catalogue — Telugu. */
    public static final String ERROR_MESSAGES_TE  = "/localization/TE/error_messages_TE.json";
    /** Error-message catalogue — Tamil. */
    public static final String ERROR_MESSAGES_TA  = "/localization/TA/error_messages_TA.json";
    /** Error-message catalogue — Cantonese. */
    public static final String ERROR_MESSAGES_YUE = "/localization/YUE/error_messages_YUE.json";
    /** Error-message catalogue — Wu Chinese. */
    public static final String ERROR_MESSAGES_WUU = "/localization/WUU/error_messages_WUU.json";
    /** Error-message catalogue — Tagalog. */
    public static final String ERROR_MESSAGES_TL  = "/localization/TL/error_messages_TL.json";
    /** Error-message catalogue — Persian. */
    public static final String ERROR_MESSAGES_FA  = "/localization/FA/error_messages_FA.json";
    /** Error-message catalogue — Hausa. */
    public static final String ERROR_MESSAGES_HA  = "/localization/HA/error_messages_HA.json";
    /** Error-message catalogue — Punjabi. */
    public static final String ERROR_MESSAGES_PA  = "/localization/PA/error_messages_PA.json";
    /** Error-message catalogue — Javanese. */
    public static final String ERROR_MESSAGES_JV  = "/localization/JV/error_messages_JV.json";
    /** Error-message catalogue — Swahili. */
    public static final String ERROR_MESSAGES_SW  = "/localization/SW/error_messages_SW.json";

    // --- Interpretation-label catalogues (one per supported language) ---
    // A missing key in any catalogue falls back to the English label the enricher emitted.

    /** Interpretation-label catalogue — English. */
    public static final String INTERPRETATION_LABELS_EN = "/localization/EN/interpretation_labels_EN.json";
    /** Interpretation-label catalogue — French. */
    public static final String INTERPRETATION_LABELS_FR = "/localization/FR/interpretation_labels_FR.json";
    /** Interpretation-label catalogue — Spanish. */
    public static final String INTERPRETATION_LABELS_ES = "/localization/ES/interpretation_labels_ES.json";
    /** Interpretation-label catalogue — German. */
    public static final String INTERPRETATION_LABELS_DE = "/localization/DE/interpretation_labels_DE.json";
    /** Interpretation-label catalogue — Italian. */
    public static final String INTERPRETATION_LABELS_IT = "/localization/IT/interpretation_labels_IT.json";
    /** Interpretation-label catalogue — Portuguese. */
    public static final String INTERPRETATION_LABELS_PT = "/localization/PT/interpretation_labels_PT.json";
    /** Interpretation-label catalogue — Dutch. */
    public static final String INTERPRETATION_LABELS_NL = "/localization/NL/interpretation_labels_NL.json";
    /** Interpretation-label catalogue — Polish. */
    public static final String INTERPRETATION_LABELS_PL = "/localization/PL/interpretation_labels_PL.json";
    /** Interpretation-label catalogue — Russian. */
    public static final String INTERPRETATION_LABELS_RU = "/localization/RU/interpretation_labels_RU.json";
    /** Interpretation-label catalogue — Ukrainian. */
    public static final String INTERPRETATION_LABELS_UK = "/localization/UK/interpretation_labels_UK.json";
    /** Interpretation-label catalogue — Czech. */
    public static final String INTERPRETATION_LABELS_CS = "/localization/CS/interpretation_labels_CS.json";
    /** Interpretation-label catalogue — Swedish. */
    public static final String INTERPRETATION_LABELS_SV = "/localization/SV/interpretation_labels_SV.json";
    /** Interpretation-label catalogue — Chinese (Simplified). */
    public static final String INTERPRETATION_LABELS_ZH = "/localization/ZH/interpretation_labels_ZH.json";
    /** Interpretation-label catalogue — Japanese. */
    public static final String INTERPRETATION_LABELS_JA = "/localization/JA/interpretation_labels_JA.json";
    /** Interpretation-label catalogue — Korean. */
    public static final String INTERPRETATION_LABELS_KO = "/localization/KO/interpretation_labels_KO.json";
    /** Interpretation-label catalogue — Arabic. */
    public static final String INTERPRETATION_LABELS_AR = "/localization/AR/interpretation_labels_AR.json";
    /** Interpretation-label catalogue — Indonesian. */
    public static final String INTERPRETATION_LABELS_ID = "/localization/ID/interpretation_labels_ID.json";
    /** Interpretation-label catalogue — Hindi. */
    public static final String INTERPRETATION_LABELS_HI = "/localization/HI/interpretation_labels_HI.json";
    /** Interpretation-label catalogue — Turkish. */
    public static final String INTERPRETATION_LABELS_TR = "/localization/TR/interpretation_labels_TR.json";
    /** Interpretation-label catalogue — Bengali. */
    public static final String INTERPRETATION_LABELS_BN = "/localization/BN/interpretation_labels_BN.json";
    /** Interpretation-label catalogue — Urdu. */
    public static final String INTERPRETATION_LABELS_UR = "/localization/UR/interpretation_labels_UR.json";
    /** Interpretation-label catalogue — Vietnamese. */
    public static final String INTERPRETATION_LABELS_VI = "/localization/VI/interpretation_labels_VI.json";
    /** Interpretation-label catalogue — Nigerian Pidgin. */
    public static final String INTERPRETATION_LABELS_PCM = "/localization/PCM/interpretation_labels_PCM.json";
    /** Interpretation-label catalogue — Egyptian Arabic. */
    public static final String INTERPRETATION_LABELS_ARZ = "/localization/ARZ/interpretation_labels_ARZ.json";
    /** Interpretation-label catalogue — Marathi. */
    public static final String INTERPRETATION_LABELS_MR  = "/localization/MR/interpretation_labels_MR.json";
    /** Interpretation-label catalogue — Telugu. */
    public static final String INTERPRETATION_LABELS_TE  = "/localization/TE/interpretation_labels_TE.json";
    /** Interpretation-label catalogue — Tamil. */
    public static final String INTERPRETATION_LABELS_TA  = "/localization/TA/interpretation_labels_TA.json";
    /** Interpretation-label catalogue — Cantonese. */
    public static final String INTERPRETATION_LABELS_YUE = "/localization/YUE/interpretation_labels_YUE.json";
    /** Interpretation-label catalogue — Wu Chinese. */
    public static final String INTERPRETATION_LABELS_WUU = "/localization/WUU/interpretation_labels_WUU.json";
    /** Interpretation-label catalogue — Tagalog. */
    public static final String INTERPRETATION_LABELS_TL  = "/localization/TL/interpretation_labels_TL.json";
    /** Interpretation-label catalogue — Persian. */
    public static final String INTERPRETATION_LABELS_FA  = "/localization/FA/interpretation_labels_FA.json";
    /** Interpretation-label catalogue — Hausa. */
    public static final String INTERPRETATION_LABELS_HA  = "/localization/HA/interpretation_labels_HA.json";
    /** Interpretation-label catalogue — Punjabi. */
    public static final String INTERPRETATION_LABELS_PA  = "/localization/PA/interpretation_labels_PA.json";
    /** Interpretation-label catalogue — Javanese. */
    public static final String INTERPRETATION_LABELS_JV  = "/localization/JV/interpretation_labels_JV.json";
    /** Interpretation-label catalogue — Swahili. */
    public static final String INTERPRETATION_LABELS_SW  = "/localization/SW/interpretation_labels_SW.json";

    // --- Interpretation-value catalogues (one per supported language) ---
    // Only interpretation types listed in an InterpretationDefinition's
    // translatableValueTypes are looked up here; a missing key falls back to
    // the English value the enricher emitted.

    /** Interpretation-value catalogue — English. */
    public static final String INTERPRETATION_VALUES_EN = "/localization/EN/interpretation_values_EN.json";
    /** Interpretation-value catalogue — French. */
    public static final String INTERPRETATION_VALUES_FR = "/localization/FR/interpretation_values_FR.json";
    /** Interpretation-value catalogue — Spanish. */
    public static final String INTERPRETATION_VALUES_ES = "/localization/ES/interpretation_values_ES.json";
    /** Interpretation-value catalogue — German. */
    public static final String INTERPRETATION_VALUES_DE = "/localization/DE/interpretation_values_DE.json";
    /** Interpretation-value catalogue — Italian. */
    public static final String INTERPRETATION_VALUES_IT = "/localization/IT/interpretation_values_IT.json";
    /** Interpretation-value catalogue — Portuguese. */
    public static final String INTERPRETATION_VALUES_PT = "/localization/PT/interpretation_values_PT.json";
    /** Interpretation-value catalogue — Dutch. */
    public static final String INTERPRETATION_VALUES_NL = "/localization/NL/interpretation_values_NL.json";
    /** Interpretation-value catalogue — Polish. */
    public static final String INTERPRETATION_VALUES_PL = "/localization/PL/interpretation_values_PL.json";
    /** Interpretation-value catalogue — Russian. */
    public static final String INTERPRETATION_VALUES_RU = "/localization/RU/interpretation_values_RU.json";
    /** Interpretation-value catalogue — Ukrainian. */
    public static final String INTERPRETATION_VALUES_UK = "/localization/UK/interpretation_values_UK.json";
    /** Interpretation-value catalogue — Czech. */
    public static final String INTERPRETATION_VALUES_CS = "/localization/CS/interpretation_values_CS.json";
    /** Interpretation-value catalogue — Swedish. */
    public static final String INTERPRETATION_VALUES_SV = "/localization/SV/interpretation_values_SV.json";
    /** Interpretation-value catalogue — Chinese (Simplified). */
    public static final String INTERPRETATION_VALUES_ZH = "/localization/ZH/interpretation_values_ZH.json";
    /** Interpretation-value catalogue — Japanese. */
    public static final String INTERPRETATION_VALUES_JA = "/localization/JA/interpretation_values_JA.json";
    /** Interpretation-value catalogue — Korean. */
    public static final String INTERPRETATION_VALUES_KO = "/localization/KO/interpretation_values_KO.json";
    /** Interpretation-value catalogue — Arabic. */
    public static final String INTERPRETATION_VALUES_AR = "/localization/AR/interpretation_values_AR.json";
    /** Interpretation-value catalogue — Indonesian. */
    public static final String INTERPRETATION_VALUES_ID = "/localization/ID/interpretation_values_ID.json";
    /** Interpretation-value catalogue — Hindi. */
    public static final String INTERPRETATION_VALUES_HI = "/localization/HI/interpretation_values_HI.json";
    /** Interpretation-value catalogue — Turkish. */
    public static final String INTERPRETATION_VALUES_TR = "/localization/TR/interpretation_values_TR.json";
    /** Interpretation-value catalogue — Bengali. */
    public static final String INTERPRETATION_VALUES_BN = "/localization/BN/interpretation_values_BN.json";
    /** Interpretation-value catalogue — Urdu. */
    public static final String INTERPRETATION_VALUES_UR = "/localization/UR/interpretation_values_UR.json";
    /** Interpretation-value catalogue — Vietnamese. */
    public static final String INTERPRETATION_VALUES_VI = "/localization/VI/interpretation_values_VI.json";
    /** Interpretation-value catalogue — Nigerian Pidgin. */
    public static final String INTERPRETATION_VALUES_PCM = "/localization/PCM/interpretation_values_PCM.json";
    /** Interpretation-value catalogue — Egyptian Arabic. */
    public static final String INTERPRETATION_VALUES_ARZ = "/localization/ARZ/interpretation_values_ARZ.json";
    /** Interpretation-value catalogue — Marathi. */
    public static final String INTERPRETATION_VALUES_MR  = "/localization/MR/interpretation_values_MR.json";
    /** Interpretation-value catalogue — Telugu. */
    public static final String INTERPRETATION_VALUES_TE  = "/localization/TE/interpretation_values_TE.json";
    /** Interpretation-value catalogue — Tamil. */
    public static final String INTERPRETATION_VALUES_TA  = "/localization/TA/interpretation_values_TA.json";
    /** Interpretation-value catalogue — Cantonese. */
    public static final String INTERPRETATION_VALUES_YUE = "/localization/YUE/interpretation_values_YUE.json";
    /** Interpretation-value catalogue — Wu Chinese. */
    public static final String INTERPRETATION_VALUES_WUU = "/localization/WUU/interpretation_values_WUU.json";
    /** Interpretation-value catalogue — Tagalog. */
    public static final String INTERPRETATION_VALUES_TL  = "/localization/TL/interpretation_values_TL.json";
    /** Interpretation-value catalogue — Persian. */
    public static final String INTERPRETATION_VALUES_FA  = "/localization/FA/interpretation_values_FA.json";
    /** Interpretation-value catalogue — Hausa. */
    public static final String INTERPRETATION_VALUES_HA  = "/localization/HA/interpretation_values_HA.json";
    /** Interpretation-value catalogue — Punjabi. */
    public static final String INTERPRETATION_VALUES_PA  = "/localization/PA/interpretation_values_PA.json";
    /** Interpretation-value catalogue — Javanese. */
    public static final String INTERPRETATION_VALUES_JV  = "/localization/JV/interpretation_values_JV.json";
    /** Interpretation-value catalogue — Swahili. */
    public static final String INTERPRETATION_VALUES_SW  = "/localization/SW/interpretation_values_SW.json";

    // --- AI-description catalogues (one per supported non-English language) ---
    // Keyed by AI code (e.g. "01", "3102"). English is not catalogued here: it is
    // sourced directly from the "description" field in gs1-application-identifiers.jsonld
    // (see AiDefinition#getDescription()). A missing key falls back to that English text.

    /** AI-description catalogue — French. */
    public static final String AI_DESCRIPTIONS_FR  = "/localization/FR/ai_descriptions_FR.json";
    /** AI-description catalogue — Spanish. */
    public static final String AI_DESCRIPTIONS_ES  = "/localization/ES/ai_descriptions_ES.json";
    /** AI-description catalogue — German. */
    public static final String AI_DESCRIPTIONS_DE  = "/localization/DE/ai_descriptions_DE.json";
    /** AI-description catalogue — Italian. */
    public static final String AI_DESCRIPTIONS_IT  = "/localization/IT/ai_descriptions_IT.json";
    /** AI-description catalogue — Portuguese. */
    public static final String AI_DESCRIPTIONS_PT  = "/localization/PT/ai_descriptions_PT.json";
    /** AI-description catalogue — Dutch. */
    public static final String AI_DESCRIPTIONS_NL  = "/localization/NL/ai_descriptions_NL.json";
    /** AI-description catalogue — Polish. */
    public static final String AI_DESCRIPTIONS_PL  = "/localization/PL/ai_descriptions_PL.json";
    /** AI-description catalogue — Russian. */
    public static final String AI_DESCRIPTIONS_RU  = "/localization/RU/ai_descriptions_RU.json";
    /** AI-description catalogue — Ukrainian. */
    public static final String AI_DESCRIPTIONS_UK  = "/localization/UK/ai_descriptions_UK.json";
    /** AI-description catalogue — Czech. */
    public static final String AI_DESCRIPTIONS_CS  = "/localization/CS/ai_descriptions_CS.json";
    /** AI-description catalogue — Swedish. */
    public static final String AI_DESCRIPTIONS_SV  = "/localization/SV/ai_descriptions_SV.json";
    /** AI-description catalogue — Chinese (Simplified). */
    public static final String AI_DESCRIPTIONS_ZH  = "/localization/ZH/ai_descriptions_ZH.json";
    /** AI-description catalogue — Japanese. */
    public static final String AI_DESCRIPTIONS_JA  = "/localization/JA/ai_descriptions_JA.json";
    /** AI-description catalogue — Korean. */
    public static final String AI_DESCRIPTIONS_KO  = "/localization/KO/ai_descriptions_KO.json";
    /** AI-description catalogue — Arabic. */
    public static final String AI_DESCRIPTIONS_AR  = "/localization/AR/ai_descriptions_AR.json";
    /** AI-description catalogue — Indonesian. */
    public static final String AI_DESCRIPTIONS_ID  = "/localization/ID/ai_descriptions_ID.json";
    /** AI-description catalogue — Hindi. */
    public static final String AI_DESCRIPTIONS_HI  = "/localization/HI/ai_descriptions_HI.json";
    /** AI-description catalogue — Turkish. */
    public static final String AI_DESCRIPTIONS_TR  = "/localization/TR/ai_descriptions_TR.json";
    /** AI-description catalogue — Bengali. */
    public static final String AI_DESCRIPTIONS_BN  = "/localization/BN/ai_descriptions_BN.json";
    /** AI-description catalogue — Urdu. */
    public static final String AI_DESCRIPTIONS_UR  = "/localization/UR/ai_descriptions_UR.json";
    /** AI-description catalogue — Vietnamese. */
    public static final String AI_DESCRIPTIONS_VI  = "/localization/VI/ai_descriptions_VI.json";
    /** AI-description catalogue — Nigerian Pidgin. */
    public static final String AI_DESCRIPTIONS_PCM = "/localization/PCM/ai_descriptions_PCM.json";
    /** AI-description catalogue — Egyptian Arabic. */
    public static final String AI_DESCRIPTIONS_ARZ = "/localization/ARZ/ai_descriptions_ARZ.json";
    /** AI-description catalogue — Marathi. */
    public static final String AI_DESCRIPTIONS_MR  = "/localization/MR/ai_descriptions_MR.json";
    /** AI-description catalogue — Telugu. */
    public static final String AI_DESCRIPTIONS_TE  = "/localization/TE/ai_descriptions_TE.json";
    /** AI-description catalogue — Tamil. */
    public static final String AI_DESCRIPTIONS_TA  = "/localization/TA/ai_descriptions_TA.json";
    /** AI-description catalogue — Cantonese. */
    public static final String AI_DESCRIPTIONS_YUE = "/localization/YUE/ai_descriptions_YUE.json";
    /** AI-description catalogue — Wu Chinese. */
    public static final String AI_DESCRIPTIONS_WUU = "/localization/WUU/ai_descriptions_WUU.json";
    /** AI-description catalogue — Tagalog. */
    public static final String AI_DESCRIPTIONS_TL  = "/localization/TL/ai_descriptions_TL.json";
    /** AI-description catalogue — Persian. */
    public static final String AI_DESCRIPTIONS_FA  = "/localization/FA/ai_descriptions_FA.json";
    /** AI-description catalogue — Hausa. */
    public static final String AI_DESCRIPTIONS_HA  = "/localization/HA/ai_descriptions_HA.json";
    /** AI-description catalogue — Punjabi. */
    public static final String AI_DESCRIPTIONS_PA  = "/localization/PA/ai_descriptions_PA.json";
    /** AI-description catalogue — Javanese. */
    public static final String AI_DESCRIPTIONS_JV  = "/localization/JV/ai_descriptions_JV.json";
    /** AI-description catalogue — Swahili. */
    public static final String AI_DESCRIPTIONS_SW  = "/localization/SW/ai_descriptions_SW.json";
}
