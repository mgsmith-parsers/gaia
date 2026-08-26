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
 * Localized display labels for interpretation segments, keyed by the interpretation
 * type (e.g. {@code "GTIN_TYPE"}, {@code "CHECK_DIGIT"}).
 *
 * <p>Each supported language ships a JSON catalogue under {@code /localization/<LANG>/} mapping the
 * interpretation type to its human-readable label. English
 * ({@code interpretation_labels_EN.json}) is the canonical, complete catalogue and the
 * fallback for any type missing from another language's catalogue. The loader is
 * fail-open: a catalogue file that is absent or malformed yields an empty map, so the
 * English fallback (and ultimately the label the producer emitted) still applies.
 *
 * <p>Loaded lazily per language and cached; thread-safe.
 *
 * @see tools.pantheum.gaia.gs1.interpretation.InterpretationEngine
 */
public final class LabelRegistry {

    public static final LabelRegistry INSTANCE = new LabelRegistry();

    /** Maps each {@link GaiaConstants.Language} to its classpath resource path. */
    private static final Map<GaiaConstants.Language, String> RESOURCES = Map.ofEntries(
            Map.entry(GaiaConstants.Language.ENGLISH,    GS1Constants_System.INTERPRETATION_LABELS_EN),
            Map.entry(GaiaConstants.Language.FRENCH,     GS1Constants_System.INTERPRETATION_LABELS_FR),
            Map.entry(GaiaConstants.Language.SPANISH,    GS1Constants_System.INTERPRETATION_LABELS_ES),
            Map.entry(GaiaConstants.Language.GERMAN,     GS1Constants_System.INTERPRETATION_LABELS_DE),
            Map.entry(GaiaConstants.Language.ITALIAN,    GS1Constants_System.INTERPRETATION_LABELS_IT),
            Map.entry(GaiaConstants.Language.PORTUGUESE, GS1Constants_System.INTERPRETATION_LABELS_PT),
            Map.entry(GaiaConstants.Language.DUTCH,      GS1Constants_System.INTERPRETATION_LABELS_NL),
            Map.entry(GaiaConstants.Language.POLISH,     GS1Constants_System.INTERPRETATION_LABELS_PL),
            Map.entry(GaiaConstants.Language.RUSSIAN,    GS1Constants_System.INTERPRETATION_LABELS_RU),
            Map.entry(GaiaConstants.Language.UKRAINIAN,  GS1Constants_System.INTERPRETATION_LABELS_UK),
            Map.entry(GaiaConstants.Language.CZECH,      GS1Constants_System.INTERPRETATION_LABELS_CS),
            Map.entry(GaiaConstants.Language.SWEDISH,    GS1Constants_System.INTERPRETATION_LABELS_SV),
            Map.entry(GaiaConstants.Language.CHINESE,    GS1Constants_System.INTERPRETATION_LABELS_ZH),
            Map.entry(GaiaConstants.Language.JAPANESE,   GS1Constants_System.INTERPRETATION_LABELS_JA),
            Map.entry(GaiaConstants.Language.KOREAN,     GS1Constants_System.INTERPRETATION_LABELS_KO),
            Map.entry(GaiaConstants.Language.ARABIC,     GS1Constants_System.INTERPRETATION_LABELS_AR),
            Map.entry(GaiaConstants.Language.INDONESIAN, GS1Constants_System.INTERPRETATION_LABELS_ID),
            Map.entry(GaiaConstants.Language.HINDI,      GS1Constants_System.INTERPRETATION_LABELS_HI),
            Map.entry(GaiaConstants.Language.TURKISH,    GS1Constants_System.INTERPRETATION_LABELS_TR),
            Map.entry(GaiaConstants.Language.BENGALI,    GS1Constants_System.INTERPRETATION_LABELS_BN),
            Map.entry(GaiaConstants.Language.URDU,       GS1Constants_System.INTERPRETATION_LABELS_UR),
            Map.entry(GaiaConstants.Language.VIETNAMESE, GS1Constants_System.INTERPRETATION_LABELS_VI),
            Map.entry(GaiaConstants.Language.NIGERIAN_PIDGIN, GS1Constants_System.INTERPRETATION_LABELS_PCM),
            Map.entry(GaiaConstants.Language.EGYPTIAN_ARABIC, GS1Constants_System.INTERPRETATION_LABELS_ARZ),
            Map.entry(GaiaConstants.Language.MARATHI,    GS1Constants_System.INTERPRETATION_LABELS_MR),
            Map.entry(GaiaConstants.Language.TELUGU,     GS1Constants_System.INTERPRETATION_LABELS_TE),
            Map.entry(GaiaConstants.Language.TAMIL,      GS1Constants_System.INTERPRETATION_LABELS_TA),
            Map.entry(GaiaConstants.Language.CANTONESE,  GS1Constants_System.INTERPRETATION_LABELS_YUE),
            Map.entry(GaiaConstants.Language.WU_CHINESE, GS1Constants_System.INTERPRETATION_LABELS_WUU),
            Map.entry(GaiaConstants.Language.TAGALOG,    GS1Constants_System.INTERPRETATION_LABELS_TL),
            Map.entry(GaiaConstants.Language.PERSIAN,    GS1Constants_System.INTERPRETATION_LABELS_FA),
            Map.entry(GaiaConstants.Language.HAUSA,      GS1Constants_System.INTERPRETATION_LABELS_HA),
            Map.entry(GaiaConstants.Language.PUNJABI,    GS1Constants_System.INTERPRETATION_LABELS_PA),
            Map.entry(GaiaConstants.Language.JAVANESE,   GS1Constants_System.INTERPRETATION_LABELS_JV),
            Map.entry(GaiaConstants.Language.SWAHILI,    GS1Constants_System.INTERPRETATION_LABELS_SW)
    );

    /** Lazily populated cache: language → (interpretation type → label). */
    private final ConcurrentHashMap<GaiaConstants.Language, Map<String, String>> cache
            = new ConcurrentHashMap<>();

    private LabelRegistry() {}

    /**
     * Returns the localized label for the given interpretation type, or {@code null}
     * if the type is not catalogued in the requested language or in English. Callers
     * should treat {@code null} as "keep the label the producer already supplied".
     *
     * @param type     the interpretation type (its key), e.g. {@code "GTIN_TYPE"}
     * @param language the desired language; falls back to English when the language
     *                 has no catalogue, or the type is missing from its catalogue
     */
    public String labelFor(String type, GaiaConstants.Language language) {
        if (type == null) return null;
        String label = catalogue(language).get(type);
        if (label == null && language != GaiaConstants.Language.ENGLISH) {
            label = catalogue(GaiaConstants.Language.ENGLISH).get(type);
        }
        return label;
    }

    private Map<String, String> catalogue(GaiaConstants.Language language) {
        GaiaConstants.Language resolved = RESOURCES.containsKey(language)
                ? language
                : GaiaConstants.Language.ENGLISH;
        return cache.computeIfAbsent(resolved, LabelRegistry::loadCatalogue);
    }

    /** Loads a language catalogue; returns an empty map (fail-open) if the file is absent or malformed. */
    private static Map<String, String> loadCatalogue(GaiaConstants.Language language) {
        String path = RESOURCES.get(language);
        try (InputStream in = LabelRegistry.class.getResourceAsStream(path)) {
            if (in == null) {
                return Collections.emptyMap();   // no catalogue file (defensive) — English fallback applies
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> labels = mapper.readValue(in, new TypeReference<Map<String, String>>() {});
            return Collections.unmodifiableMap(labels);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
