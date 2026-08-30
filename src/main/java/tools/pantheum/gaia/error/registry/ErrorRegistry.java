package tools.pantheum.gaia.error.registry;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton registry that loads error catalogue definitions from the per-language
 * message files under {@code src/main/resources/localization/<CODE>/} and creates {@link GaiaError}
 * instances by interpolating message templates with call-site parameters.
 *
 * <p>Catalogues are loaded lazily — a language's JSON file is read from the classpath
 * the first time an error is created in that language, then cached for all subsequent
 * calls. Loading is thread-safe: {@link ConcurrentHashMap#computeIfAbsent} guarantees
 * each catalogue is loaded at most once even under concurrent access.
 *
 * <h2>Unknown ids fail fast</h2>
 * Unlike the input-keyed registries ({@code AiDefinitionRegistry},
 * {@code DataCarrierRegistry}) whose lookups return {@code Optional} because a miss
 * is a normal, data-dependent outcome, {@link #create} throws
 * {@link IllegalArgumentException} for an unknown id: error ids are compile-time
 * constants, so a miss always means the code and the catalogue have drifted out of
 * sync — a bug to surface, not a condition for callers to handle.
 *
 * <h2>Language files</h2>
 * One catalogue per {@link GaiaConstants.Language} value, at
 * {@code /localization/<CODE>/error_messages_<CODE>.json} (the code is ISO 639-1, or ISO 639-3 where
 * no 639-1 exists — e.g. {@code EN}, {@code FR}, {@code ZH}, {@code PCM}). English
 * ({@code error_messages_EN.json}) is the default and the fallback for any language whose
 * catalogue is not registered.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // English (default) — existing call sites unchanged
 * ErrorRegistry.INSTANCE.create("GE-S001", ai, position, params);
 *
 * // Language-specific — pass the language from ParseConfig
 * ErrorRegistry.INSTANCE.create("GE-S001", ai, position, params, GaiaConstants.Language.FRENCH);
 * }</pre>
 */
public class ErrorRegistry {

    /** The singleton instance. */
    public static final ErrorRegistry INSTANCE = new ErrorRegistry();

    /** Maps each {@link GaiaConstants.Language} to its classpath resource path. */
    private static final Map<GaiaConstants.Language, String> RESOURCES = Map.ofEntries(
            Map.entry(GaiaConstants.Language.ENGLISH,    GS1Constants_System.ERROR_MESSAGES_EN),
            Map.entry(GaiaConstants.Language.FRENCH,     GS1Constants_System.ERROR_MESSAGES_FR),
            Map.entry(GaiaConstants.Language.SPANISH,    GS1Constants_System.ERROR_MESSAGES_ES),
            Map.entry(GaiaConstants.Language.GERMAN,     GS1Constants_System.ERROR_MESSAGES_DE),
            Map.entry(GaiaConstants.Language.ITALIAN,    GS1Constants_System.ERROR_MESSAGES_IT),
            Map.entry(GaiaConstants.Language.PORTUGUESE, GS1Constants_System.ERROR_MESSAGES_PT),
            Map.entry(GaiaConstants.Language.DUTCH,      GS1Constants_System.ERROR_MESSAGES_NL),
            Map.entry(GaiaConstants.Language.POLISH,     GS1Constants_System.ERROR_MESSAGES_PL),
            Map.entry(GaiaConstants.Language.RUSSIAN,    GS1Constants_System.ERROR_MESSAGES_RU),
            Map.entry(GaiaConstants.Language.UKRAINIAN,  GS1Constants_System.ERROR_MESSAGES_UK),
            Map.entry(GaiaConstants.Language.CZECH,      GS1Constants_System.ERROR_MESSAGES_CS),
            Map.entry(GaiaConstants.Language.SWEDISH,    GS1Constants_System.ERROR_MESSAGES_SV),
            Map.entry(GaiaConstants.Language.CHINESE,    GS1Constants_System.ERROR_MESSAGES_ZH),
            Map.entry(GaiaConstants.Language.JAPANESE,   GS1Constants_System.ERROR_MESSAGES_JA),
            Map.entry(GaiaConstants.Language.KOREAN,     GS1Constants_System.ERROR_MESSAGES_KO),
            Map.entry(GaiaConstants.Language.ARABIC,     GS1Constants_System.ERROR_MESSAGES_AR),
            Map.entry(GaiaConstants.Language.INDONESIAN, GS1Constants_System.ERROR_MESSAGES_ID),
            Map.entry(GaiaConstants.Language.HINDI,      GS1Constants_System.ERROR_MESSAGES_HI),
            Map.entry(GaiaConstants.Language.TURKISH,    GS1Constants_System.ERROR_MESSAGES_TR),
            Map.entry(GaiaConstants.Language.BENGALI,    GS1Constants_System.ERROR_MESSAGES_BN),
            Map.entry(GaiaConstants.Language.URDU,       GS1Constants_System.ERROR_MESSAGES_UR),
            Map.entry(GaiaConstants.Language.VIETNAMESE, GS1Constants_System.ERROR_MESSAGES_VI),
            Map.entry(GaiaConstants.Language.NIGERIAN_PIDGIN, GS1Constants_System.ERROR_MESSAGES_PCM),
            Map.entry(GaiaConstants.Language.EGYPTIAN_ARABIC, GS1Constants_System.ERROR_MESSAGES_ARZ),
            Map.entry(GaiaConstants.Language.MARATHI,    GS1Constants_System.ERROR_MESSAGES_MR),
            Map.entry(GaiaConstants.Language.TELUGU,     GS1Constants_System.ERROR_MESSAGES_TE),
            Map.entry(GaiaConstants.Language.TAMIL,      GS1Constants_System.ERROR_MESSAGES_TA),
            Map.entry(GaiaConstants.Language.CANTONESE,  GS1Constants_System.ERROR_MESSAGES_YUE),
            Map.entry(GaiaConstants.Language.WU_CHINESE, GS1Constants_System.ERROR_MESSAGES_WUU),
            Map.entry(GaiaConstants.Language.TAGALOG,    GS1Constants_System.ERROR_MESSAGES_TL),
            Map.entry(GaiaConstants.Language.PERSIAN,    GS1Constants_System.ERROR_MESSAGES_FA),
            Map.entry(GaiaConstants.Language.HAUSA,      GS1Constants_System.ERROR_MESSAGES_HA),
            Map.entry(GaiaConstants.Language.PUNJABI,    GS1Constants_System.ERROR_MESSAGES_PA),
            Map.entry(GaiaConstants.Language.JAVANESE,   GS1Constants_System.ERROR_MESSAGES_JV),
            Map.entry(GaiaConstants.Language.SWAHILI,    GS1Constants_System.ERROR_MESSAGES_SW)
    );

    /** Lazily populated cache: language → (errorId → definition). */
    private final ConcurrentHashMap<GaiaConstants.Language, Map<String, ErrorDefinition>> cache
            = new ConcurrentHashMap<>();

    private ErrorRegistry() {}

    // -------------------------------------------------------------------------

    /**
     * Creates a {@link GaiaError} using the default language ({@link GaiaConstants.Language#ENGLISH}).
     *
     * <p>This overload keeps all existing call sites unchanged.
     *
     * @param id the id
     * @param ai the AI
     * @param position the position
     * @param params the params
     * @return a new {@code GaiaError}
     */
    public GaiaError create(String id, String ai, int position, Map<String, String> params) {
        return create(id, ai, position, params, GaiaConstants.Language.ENGLISH);
    }

    /**
     * Creates a {@link GaiaError} using the specified language.
     *
     * <p>The catalogue for {@code language} is loaded on the first call for that language
     * and cached for all subsequent calls.
     *
     * @param id       the catalogue entry id (e.g. {@code "GE-S001"})
     * @param ai       the Application Identifier string at the error site, or {@code null}
     * @param position the character position in the input where the error was detected
     * @param params   placeholder substitution map used to fill in the message template
     * @param language the language in which to produce the human-readable error message
     * @throws IllegalArgumentException if {@code id} is not found in the catalogue
     * @throws IllegalStateException    if the catalogue file cannot be loaded
     * @return a new {@code GaiaError}
     */
    public GaiaError create(String id, String ai, int position,
                            Map<String, String> params, GaiaConstants.Language language) {
        Map<String, ErrorDefinition> definitions = catalogue(language);

        ErrorDefinition def = definitions.get(id);
        if (def == null) {
            throw new IllegalArgumentException("Unknown error id: " + id);
        }
        String message = interpolate(def.getMessage(), params);
        return new GaiaError(
                id,
                GaiaConstants.ErrorLevel.valueOf(def.getLevel()),
                GaiaConstants.ErrorStage.valueOf(def.getStage()),
                def.getCode(),
                ai,
                message,
                position
        );
    }

    // -------------------------------------------------------------------------

    /**
     * Returns the catalogue for the given language, loading it on first access.
     * Falls back to English if no resource is registered for the requested language.
     */
    private Map<String, ErrorDefinition> catalogue(GaiaConstants.Language language) {
        GaiaConstants.Language resolved = RESOURCES.containsKey(language)
                ? language
                : GaiaConstants.Language.ENGLISH;
        return cache.computeIfAbsent(resolved, lang -> loadCatalogue(RESOURCES.get(lang)));
    }

    private static Map<String, ErrorDefinition> loadCatalogue(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = ErrorRegistry.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Error catalogue not found on classpath: " + resourcePath);
            }
            List<ErrorDefinition> list =
                    mapper.readValue(in, new TypeReference<List<ErrorDefinition>>() {});
            return Collections.unmodifiableMap(
                    list.stream().collect(Collectors.toMap(ErrorDefinition::getId, d -> d)));
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load error catalogue: " + resourcePath, e);
        }
    }

    private static String interpolate(String template, Map<String, String> params) {
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }
}
