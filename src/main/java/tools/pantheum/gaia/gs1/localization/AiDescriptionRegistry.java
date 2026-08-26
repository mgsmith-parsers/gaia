package tools.pantheum.gaia.gs1.localization;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.constants.GS1Constants_System;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Localized full descriptions for GS1 Application Identifiers, keyed by AI code
 * (e.g. {@code "01"}, {@code "3102"}).
 *
 * <p>Unlike {@link LabelRegistry} and {@link ValueRegistry}, English is not catalogued
 * here: it is sourced directly from the {@code description} field in
 * {@code gs1-application-identifiers.jsonld} via
 * {@link tools.pantheum.gaia.gs1.registry.AiDefinition#getDescription()}, which is the
 * canonical GS1 text. Each other supported language ships a JSON catalogue under
 * {@code /localization/<LANG>/} mapping AI code to a machine-translated description. The
 * loader is fail-open: a catalogue file that is absent or malformed yields an empty map,
 * so the English description still applies.
 *
 * <p>Loaded lazily per language and cached; thread-safe.
 */
public final class AiDescriptionRegistry {

    public static final AiDescriptionRegistry INSTANCE = new AiDescriptionRegistry();

    /** Maps each non-English {@link GaiaConstants.Language} to its classpath resource path. */
    private static final Map<GaiaConstants.Language, String> RESOURCES = Map.ofEntries(
            Map.entry(GaiaConstants.Language.FRENCH,     GS1Constants_System.AI_DESCRIPTIONS_FR),
            Map.entry(GaiaConstants.Language.SPANISH,    GS1Constants_System.AI_DESCRIPTIONS_ES),
            Map.entry(GaiaConstants.Language.GERMAN,     GS1Constants_System.AI_DESCRIPTIONS_DE),
            Map.entry(GaiaConstants.Language.ITALIAN,    GS1Constants_System.AI_DESCRIPTIONS_IT),
            Map.entry(GaiaConstants.Language.PORTUGUESE, GS1Constants_System.AI_DESCRIPTIONS_PT),
            Map.entry(GaiaConstants.Language.DUTCH,      GS1Constants_System.AI_DESCRIPTIONS_NL),
            Map.entry(GaiaConstants.Language.POLISH,     GS1Constants_System.AI_DESCRIPTIONS_PL),
            Map.entry(GaiaConstants.Language.RUSSIAN,    GS1Constants_System.AI_DESCRIPTIONS_RU),
            Map.entry(GaiaConstants.Language.UKRAINIAN,  GS1Constants_System.AI_DESCRIPTIONS_UK),
            Map.entry(GaiaConstants.Language.CZECH,      GS1Constants_System.AI_DESCRIPTIONS_CS),
            Map.entry(GaiaConstants.Language.SWEDISH,    GS1Constants_System.AI_DESCRIPTIONS_SV),
            Map.entry(GaiaConstants.Language.CHINESE,    GS1Constants_System.AI_DESCRIPTIONS_ZH),
            Map.entry(GaiaConstants.Language.JAPANESE,   GS1Constants_System.AI_DESCRIPTIONS_JA),
            Map.entry(GaiaConstants.Language.KOREAN,     GS1Constants_System.AI_DESCRIPTIONS_KO),
            Map.entry(GaiaConstants.Language.ARABIC,     GS1Constants_System.AI_DESCRIPTIONS_AR),
            Map.entry(GaiaConstants.Language.INDONESIAN, GS1Constants_System.AI_DESCRIPTIONS_ID),
            Map.entry(GaiaConstants.Language.HINDI,      GS1Constants_System.AI_DESCRIPTIONS_HI),
            Map.entry(GaiaConstants.Language.TURKISH,    GS1Constants_System.AI_DESCRIPTIONS_TR),
            Map.entry(GaiaConstants.Language.BENGALI,    GS1Constants_System.AI_DESCRIPTIONS_BN),
            Map.entry(GaiaConstants.Language.URDU,       GS1Constants_System.AI_DESCRIPTIONS_UR),
            Map.entry(GaiaConstants.Language.VIETNAMESE, GS1Constants_System.AI_DESCRIPTIONS_VI),
            Map.entry(GaiaConstants.Language.NIGERIAN_PIDGIN, GS1Constants_System.AI_DESCRIPTIONS_PCM),
            Map.entry(GaiaConstants.Language.EGYPTIAN_ARABIC, GS1Constants_System.AI_DESCRIPTIONS_ARZ),
            Map.entry(GaiaConstants.Language.MARATHI,    GS1Constants_System.AI_DESCRIPTIONS_MR),
            Map.entry(GaiaConstants.Language.TELUGU,     GS1Constants_System.AI_DESCRIPTIONS_TE),
            Map.entry(GaiaConstants.Language.TAMIL,      GS1Constants_System.AI_DESCRIPTIONS_TA),
            Map.entry(GaiaConstants.Language.CANTONESE,  GS1Constants_System.AI_DESCRIPTIONS_YUE),
            Map.entry(GaiaConstants.Language.WU_CHINESE, GS1Constants_System.AI_DESCRIPTIONS_WUU),
            Map.entry(GaiaConstants.Language.TAGALOG,    GS1Constants_System.AI_DESCRIPTIONS_TL),
            Map.entry(GaiaConstants.Language.PERSIAN,    GS1Constants_System.AI_DESCRIPTIONS_FA),
            Map.entry(GaiaConstants.Language.HAUSA,      GS1Constants_System.AI_DESCRIPTIONS_HA),
            Map.entry(GaiaConstants.Language.PUNJABI,    GS1Constants_System.AI_DESCRIPTIONS_PA),
            Map.entry(GaiaConstants.Language.JAVANESE,   GS1Constants_System.AI_DESCRIPTIONS_JV),
            Map.entry(GaiaConstants.Language.SWAHILI,    GS1Constants_System.AI_DESCRIPTIONS_SW)
    );

    /** Lazily populated cache: language → (AI code → translated description). */
    private final ConcurrentHashMap<GaiaConstants.Language, Map<String, String>> cache
            = new ConcurrentHashMap<>();

    private AiDescriptionRegistry() {}

    /**
     * Returns the localized description for the given AI code, or {@code null} if the
     * language is English, has no catalogue, or the AI code is not catalogued in it.
     * Callers should treat {@code null} as "keep the English description from the AI
     * definition".
     *
     * @param aiCode   the AI code, e.g. {@code "01"}
     * @param language the desired language
     */
    public String descriptionFor(String aiCode, GaiaConstants.Language language) {
        if (aiCode == null || language == null || language == GaiaConstants.Language.ENGLISH) return null;
        return catalogue(language).get(aiCode);
    }

    private Map<String, String> catalogue(GaiaConstants.Language language) {
        if (!RESOURCES.containsKey(language)) return Collections.emptyMap();
        return cache.computeIfAbsent(language, AiDescriptionRegistry::loadCatalogue);
    }

    /** Loads a language catalogue; returns an empty map (fail-open) if the file is absent or malformed. */
    private static Map<String, String> loadCatalogue(GaiaConstants.Language language) {
        String path = RESOURCES.get(language);
        try (InputStream in = AiDescriptionRegistry.class.getResourceAsStream(path)) {
            if (in == null) {
                return Collections.emptyMap();   // no catalogue file (defensive) — English fallback applies
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> descriptions = mapper.readValue(in, new TypeReference<Map<String, String>>() {});
            return Collections.unmodifiableMap(descriptions);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
