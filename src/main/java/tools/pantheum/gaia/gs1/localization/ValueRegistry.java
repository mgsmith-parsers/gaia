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
 * Localized display values for interpretation values drawn from a fixed vocabulary
 * (e.g. {@code "Yes"}/{@code "No"}), keyed first by interpretation type
 * (e.g. {@code "FLAG_VALUE"}) and then by the English value the producer emitted.
 *
 * <p>Unlike {@link LabelRegistry}, which translates every interpretation's {@code label}
 * unconditionally, value translation is opt-in: only interpretation types listed in an
 * {@link tools.pantheum.gaia.gs1.interpretation.registry.InterpretationDefinition#getTranslatableValueTypes()}
 * are looked up here. This
 * keeps opaque extracted data (GTINs, dates, check digits, raw codes) from ever being
 * matched against catalogued words.
 *
 * <p>Each supported language ships a JSON catalogue under {@code /localization/<LANG>/} mapping
 * {@code type -> englishValue -> translatedValue}. English
 * ({@code interpretation_values_EN.json}) is the canonical, complete catalogue and the
 * fallback for any (type, value) pair missing from another language's catalogue. The
 * loader is fail-open: a catalogue file that is absent or malformed yields an empty map,
 * so the English fallback (and ultimately the value the producer emitted) still applies.
 *
 * <p>Loaded lazily per language and cached; thread-safe.
 *
 * @see tools.pantheum.gaia.gs1.interpretation.InterpretationEngine
 */
public final class ValueRegistry {

    /** The singleton instance. */
    public static final ValueRegistry INSTANCE = new ValueRegistry();

    /** Maps each {@link GaiaConstants.Language} to its classpath resource path. */
    private static final Map<GaiaConstants.Language, String> RESOURCES = Map.ofEntries(
            Map.entry(GaiaConstants.Language.ENGLISH,    GS1Constants_System.INTERPRETATION_VALUES_EN),
            Map.entry(GaiaConstants.Language.FRENCH,     GS1Constants_System.INTERPRETATION_VALUES_FR),
            Map.entry(GaiaConstants.Language.SPANISH,    GS1Constants_System.INTERPRETATION_VALUES_ES),
            Map.entry(GaiaConstants.Language.GERMAN,     GS1Constants_System.INTERPRETATION_VALUES_DE),
            Map.entry(GaiaConstants.Language.ITALIAN,    GS1Constants_System.INTERPRETATION_VALUES_IT),
            Map.entry(GaiaConstants.Language.PORTUGUESE, GS1Constants_System.INTERPRETATION_VALUES_PT),
            Map.entry(GaiaConstants.Language.DUTCH,      GS1Constants_System.INTERPRETATION_VALUES_NL),
            Map.entry(GaiaConstants.Language.POLISH,     GS1Constants_System.INTERPRETATION_VALUES_PL),
            Map.entry(GaiaConstants.Language.RUSSIAN,    GS1Constants_System.INTERPRETATION_VALUES_RU),
            Map.entry(GaiaConstants.Language.UKRAINIAN,  GS1Constants_System.INTERPRETATION_VALUES_UK),
            Map.entry(GaiaConstants.Language.CZECH,      GS1Constants_System.INTERPRETATION_VALUES_CS),
            Map.entry(GaiaConstants.Language.SWEDISH,    GS1Constants_System.INTERPRETATION_VALUES_SV),
            Map.entry(GaiaConstants.Language.CHINESE,    GS1Constants_System.INTERPRETATION_VALUES_ZH),
            Map.entry(GaiaConstants.Language.JAPANESE,   GS1Constants_System.INTERPRETATION_VALUES_JA),
            Map.entry(GaiaConstants.Language.KOREAN,     GS1Constants_System.INTERPRETATION_VALUES_KO),
            Map.entry(GaiaConstants.Language.ARABIC,     GS1Constants_System.INTERPRETATION_VALUES_AR),
            Map.entry(GaiaConstants.Language.INDONESIAN, GS1Constants_System.INTERPRETATION_VALUES_ID),
            Map.entry(GaiaConstants.Language.HINDI,      GS1Constants_System.INTERPRETATION_VALUES_HI),
            Map.entry(GaiaConstants.Language.TURKISH,    GS1Constants_System.INTERPRETATION_VALUES_TR),
            Map.entry(GaiaConstants.Language.BENGALI,    GS1Constants_System.INTERPRETATION_VALUES_BN),
            Map.entry(GaiaConstants.Language.URDU,       GS1Constants_System.INTERPRETATION_VALUES_UR),
            Map.entry(GaiaConstants.Language.VIETNAMESE, GS1Constants_System.INTERPRETATION_VALUES_VI),
            Map.entry(GaiaConstants.Language.NIGERIAN_PIDGIN, GS1Constants_System.INTERPRETATION_VALUES_PCM),
            Map.entry(GaiaConstants.Language.EGYPTIAN_ARABIC, GS1Constants_System.INTERPRETATION_VALUES_ARZ),
            Map.entry(GaiaConstants.Language.MARATHI,    GS1Constants_System.INTERPRETATION_VALUES_MR),
            Map.entry(GaiaConstants.Language.TELUGU,     GS1Constants_System.INTERPRETATION_VALUES_TE),
            Map.entry(GaiaConstants.Language.TAMIL,      GS1Constants_System.INTERPRETATION_VALUES_TA),
            Map.entry(GaiaConstants.Language.CANTONESE,  GS1Constants_System.INTERPRETATION_VALUES_YUE),
            Map.entry(GaiaConstants.Language.WU_CHINESE, GS1Constants_System.INTERPRETATION_VALUES_WUU),
            Map.entry(GaiaConstants.Language.TAGALOG,    GS1Constants_System.INTERPRETATION_VALUES_TL),
            Map.entry(GaiaConstants.Language.PERSIAN,    GS1Constants_System.INTERPRETATION_VALUES_FA),
            Map.entry(GaiaConstants.Language.HAUSA,      GS1Constants_System.INTERPRETATION_VALUES_HA),
            Map.entry(GaiaConstants.Language.PUNJABI,    GS1Constants_System.INTERPRETATION_VALUES_PA),
            Map.entry(GaiaConstants.Language.JAVANESE,   GS1Constants_System.INTERPRETATION_VALUES_JV),
            Map.entry(GaiaConstants.Language.SWAHILI,    GS1Constants_System.INTERPRETATION_VALUES_SW)
    );

    /** Lazily populated cache: language → (interpretation type → (English value → translated value)). */
    private final ConcurrentHashMap<GaiaConstants.Language, Map<String, Map<String, String>>> cache
            = new ConcurrentHashMap<>();

    private ValueRegistry() {}

    /**
     * Returns the localized value for the given interpretation type and English value, or
     * {@code null} if the pair is not catalogued in the requested language or in English.
     * Callers should treat {@code null} as "keep the value the producer already supplied".
     *
     * @param type     the interpretation type (its key), e.g. {@code "FLAG_VALUE"}
     * @param value    the English value emitted by the producer, e.g. {@code "Yes"}
     * @param language the desired language; falls back to English when the language has no
     *                 catalogue, or the (type, value) pair is missing from its catalogue
     *
     * @return the value for.
     */
    public String valueFor(String type, String value, GaiaConstants.Language language) {
        if (type == null || value == null) return null;
        String translated = lookup(catalogue(language), type, value);
        if (translated == null && language != GaiaConstants.Language.ENGLISH) {
            translated = lookup(catalogue(GaiaConstants.Language.ENGLISH), type, value);
        }
        return translated;
    }

    private static String lookup(Map<String, Map<String, String>> catalogue, String type, String value) {
        Map<String, String> forType = catalogue.get(type);
        return forType != null ? forType.get(value) : null;
    }

    private Map<String, Map<String, String>> catalogue(GaiaConstants.Language language) {
        GaiaConstants.Language resolved = RESOURCES.containsKey(language)
                ? language
                : GaiaConstants.Language.ENGLISH;
        return cache.computeIfAbsent(resolved, ValueRegistry::loadCatalogue);
    }

    /** Loads a language catalogue; returns an empty map (fail-open) if the file is absent or malformed. */
    private static Map<String, Map<String, String>> loadCatalogue(GaiaConstants.Language language) {
        String path = RESOURCES.get(language);
        try (InputStream in = ValueRegistry.class.getResourceAsStream(path)) {
            if (in == null) {
                return Collections.emptyMap();   // no catalogue file (defensive) — English fallback applies
            }
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> values =
                    mapper.readValue(in, new TypeReference<Map<String, Map<String, String>>>() {});
            return Collections.unmodifiableMap(values);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}
