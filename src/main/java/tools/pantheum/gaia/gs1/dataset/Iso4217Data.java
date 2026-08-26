package tools.pantheum.gaia.gs1.dataset;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;
import tools.pantheum.gaia.gs1.util.ResourceLoadLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reference data for ISO 4217 currency codes, loaded from the bundled
 * {@code iso_4217.json} resource at class-initialisation time.
 *
 * <p>Provides two maps keyed by code, each holding a full {@link CurrencyEntry}:
 * <ul>
 *   <li>{@link #BY_NUMERIC} — numeric code → entry (e.g. {@code "036" → AUD entry})</li>
 *   <li>{@link #BY_ALPHA}   — alpha code   → entry (e.g. {@code "AUD" → AUD entry})</li>
 * </ul>
 *
 * <p>Use {@link #forNumeric(String)} or {@link #forAlpha(String)} for lookups.
 * Both maps are immutable. If the resource cannot be loaded the maps are empty and a
 * warning is printed to stderr; validators then skip the lookup and fail open.
 */
public final class Iso4217Data {

    /** Maps 3-digit numeric codes (e.g. {@code "036"}) to their {@link CurrencyEntry}. */
    public static final Map<String, CurrencyEntry> BY_NUMERIC;

    /** Maps 3-letter alpha codes (e.g. {@code "AUD"}) to their {@link CurrencyEntry}. */
    public static final Map<String, CurrencyEntry> BY_ALPHA;

    static {
        Map<String, CurrencyEntry> byNumeric = new HashMap<>();
        Map<String, CurrencyEntry> byAlpha   = new HashMap<>();

        try (InputStream is = Iso4217Data.class.getResourceAsStream(GS1Constants_System.ISO_4217)) {
            if (is == null) {
                ResourceLoadLogger.resourceNotFound("Iso4217Data", GS1Constants_System.ISO_4217, "ISO 4217 lookup disabled");
            } else {
                JsonNode root = new ObjectMapper().readTree(is);
                for (JsonNode node : root.path("isoCurrencies")) {
                    String numeric            = node.path("numeric").asText(null);
                    String code               = node.path("code").asText(null);
                    String name               = node.path("name").asText(null);
                    String symbol             = node.path("symbol").asText(null);
                    String symbolPosition     = node.path("symbol_position").asText(null);
                    int    decimalPlaces      = node.path("decimal_places").asInt(2);
                    String thousandsSeparator = node.path("thousands_separator").asText(null);
                    String decimalCharacter   = node.path("decimal_character").asText("period");

                    if (numeric == null || code == null || name == null) continue;

                    CurrencyEntry entry = new CurrencyEntry(
                            numeric, code, name, symbol, symbolPosition,
                            decimalPlaces, thousandsSeparator, decimalCharacter);

                    byNumeric.put(numeric, entry);
                    byAlpha.put(code, entry);
                }
            }
        } catch (IOException e) {
            ResourceLoadLogger.loadFailed("Iso4217Data", GS1Constants_System.ISO_4217, e.getMessage());
        }

        BY_NUMERIC = Collections.unmodifiableMap(byNumeric);
        BY_ALPHA   = Collections.unmodifiableMap(byAlpha);
    }

    private Iso4217Data() {}

    /**
     * Returns the {@link CurrencyEntry} for the given ISO 4217 numeric code, or
     * {@link Optional#empty()} if the code is not recognised or the data is unavailable.
     *
     * @param numericCode zero-padded 3-digit string, e.g. {@code "036"}
     */
    public static Optional<CurrencyEntry> forNumeric(String numericCode) {
        return Optional.ofNullable(BY_NUMERIC.get(numericCode));
    }

    /**
     * Returns the {@link CurrencyEntry} for the given ISO 4217 alpha code, or
     * {@link Optional#empty()} if the code is not recognised or the data is unavailable.
     *
     * @param alphaCode 3-letter uppercase string, e.g. {@code "AUD"}
     */
    public static Optional<CurrencyEntry> forAlpha(String alphaCode) {
        return Optional.ofNullable(BY_ALPHA.get(alphaCode));
    }
}
