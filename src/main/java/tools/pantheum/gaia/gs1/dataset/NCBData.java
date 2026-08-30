package tools.pantheum.gaia.gs1.dataset;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reference data for NATO Stock Number (NSN) cataloguing nation codes — the
 * National Codification Bureau (NCB) codes assigned under the NATO
 * Codification System (NCS) — loaded from the bundled
 * {@code ncb-countries.json} resource at class-initialisation time.
 *
 * <p>Provides a single shared dataset used by both the NSN content validator
 * and the NSN interpretation enricher, ensuring the two components remain in
 * sync.
 *
 * <p>Use {@link #entryFor(String)} for the full record, or
 * {@link #nameForCode(String)} when only the country name is needed.
 * {@link #COUNTRY_CODES} remains available as a code → country-name view.
 *
 * <p>Unlike the fail-open enrichment datasets, this one is required for
 * correct validation: {@code NSNValidator} rejects any code absent from the
 * map, so an empty map would reject every NSN. A load failure therefore
 * throws {@link IllegalStateException} rather than logging a warning.
 *
 * <h2>Coverage</h2>
 * Only codes assigned in the NSPA list are present, so any other code is
 * rejected by the validator. Unassigned: {@code 10}, {@code 11}, {@code 67},
 * {@code 69} and {@code 85}–{@code 97}. Code {@code 44} is not a nation — it
 * is held by the United Nations.
 *
 * <p>Source: NSPA AC/135 e-Portal, NCS country code list
 * ({@code https://eportal.nspa.nato.int/ac135/ncs/countries/}, backed by
 * {@code /ac135/data/countries2.xml}), retrieved 3 August 2026.
 *
 * @see NCBEntry
 */
public final class NCBData {

    /** Maps 2-digit NCB cataloguing nation codes to their full entries. */
    public static final Map<String, NCBEntry> ENTRIES;

    /**
     * Maps 2-digit NCB cataloguing nation codes to their country names.
     * A convenience view over {@link #ENTRIES}.
     */
    public static final Map<String, String> COUNTRY_CODES;

    static {
        Map<String, NCBEntry> entries = new LinkedHashMap<>();
        Map<String, String>   names   = new LinkedHashMap<>();

        try (InputStream is = NCBData.class.getResourceAsStream(GS1Constants_System.NCB_COUNTRIES)) {
            if (is == null) {
                throw new IllegalStateException(
                        "NCB country codes not found on classpath: " + GS1Constants_System.NCB_COUNTRIES);
            }
            JsonNode root = new ObjectMapper().readTree(is);
            root.properties().forEach(e -> {
                String   code    = e.getKey();
                JsonNode node    = e.getValue();
                String   country = node.path("country").asText(null);
                String   ctr     = node.path("ctr").asText(null);
                String   cat     = node.path("cat").asText(null);
                if (country != null) {
                    entries.put(code, new NCBEntry(code, country, ctr, cat));
                    names.put(code, country);
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load NCB country codes: " + GS1Constants_System.NCB_COUNTRIES, e);
        }

        ENTRIES       = Collections.unmodifiableMap(entries);
        COUNTRY_CODES = Collections.unmodifiableMap(names);
    }

    private NCBData() {}

    /**
     * Returns the full {@link NCBEntry} for the given 2-digit NCB cataloguing
     * nation code, or {@link Optional#empty()} if the code is not assigned.
     *
     * @param code two-digit string, e.g. {@code "66"} for Australia
     * @return a new {@code Optional<NCBEntry>}
     */
    public static Optional<NCBEntry> entryFor(String code) {
        return Optional.ofNullable(ENTRIES.get(code));
    }

    /**
     * Returns the country name for the given 2-digit NCB cataloguing nation
     * code, or {@link Optional#empty()} if the code is not assigned.
     *
     * @param code two-digit string, e.g. {@code "00"} for United States
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> nameForCode(String code) {
        return Optional.ofNullable(COUNTRY_CODES.get(code));
    }
}
