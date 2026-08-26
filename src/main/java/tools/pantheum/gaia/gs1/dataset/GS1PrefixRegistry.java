package tools.pantheum.gaia.gs1.dataset;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads the GS1 company prefix range table from
 * {@code src/main/resources/content/gs1_company_prefix_registry.json} and
 * provides prefix validation for GS1 keys (GLN, GTIN, SSCC, etc.).
 *
 * <h2>How prefix matching works</h2>
 * <p>Each entry in the registry defines a range of prefixes at a specific
 * length (e.g. {@code "300"}–{@code "379"} for GS1 France, or
 * {@code "0000001"}–{@code "0000099"} for an unused block). To test a value,
 * every range is tried in turn: the first {@code range.start.length()} digits
 * of the value are extracted and compared against {@code [start, end]}
 * (lexicographic, safe because all strings within a range are the same
 * length). A match against <em>any</em> entry means the prefix is recognised.
 *
 * <p>The registry is initialised once at class-load time and is thread-safe
 * for concurrent reads.
 */
public final class GS1PrefixRegistry {

    public static final GS1PrefixRegistry INSTANCE = new GS1PrefixRegistry();

    private static final String RESOURCE_PATH = GS1Constants_System.GS1_COMPANY_PREFIX_REGISTRY;

    /** Flat list of [start, end] pairs — mixed lengths, checked linearly. */
    private final List<String[]> ranges;

    private GS1PrefixRegistry() {
        ranges = load();
    }

    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the leading digits of {@code value} match a
     * recognised GS1 prefix range.
     *
     * @param value the GS1 key value to test (e.g. a 13-digit GLN or 14-digit
     *              GTIN); must not be {@code null}
     * @return {@code true} if a known prefix range covers the start of {@code value}
     */
    public boolean isKnownPrefix(String value) {
        for (String[] range : ranges) {
            int len = range[0].length();
            if (value.length() < len) continue;
            String prefix = value.substring(0, len);
            if (prefix.compareTo(range[0]) >= 0 && prefix.compareTo(range[1]) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a two-element array {@code {matchedPrefix, description}} for the
     * first prefix range that covers the start of {@code value}, or {@code null}
     * if no range matches.
     *
     * <p>Used by
     * {@link tools.pantheum.gaia.gs1.interpretation.enricher.GS1PrefixEnricher}
     * to produce both the company prefix string and the MO description in one
     * lookup.
     *
     * @param value the GS1 key value to look up
     * @return {@code {prefix, description}} or {@code null}
     */
    public String[] prefixMatchFor(String value) {
        for (String[] range : ranges) {
            int len = range[0].length();
            if (value.length() < len) continue;
            String prefix = value.substring(0, len);
            if (prefix.compareTo(range[0]) >= 0 && prefix.compareTo(range[1]) <= 0) {
                // A 3-digit range whose start ends in "0" and end ends in "9" spans the
                // full final digit, so only the leading 2 digits are the significant
                // GS1 prefix (e.g. range 300–379 → prefix "30").
                if (len == 3 && range[0].endsWith("0") && range[1].endsWith("9")) {
                    prefix = prefix.substring(0, 2);
                }
                return new String[]{prefix, range[2]};
            }
        }
        return null;
    }

    /**
     * Returns the description for the GS1 member organisation that owns the
     * prefix of {@code value}, or {@code null} if the prefix is not recognised.
     *
     * @param value the GS1 key value to look up
     */
    public String descriptionFor(String value) {
        for (String[] range : ranges) {
            int len = range[0].length();
            if (value.length() < len) continue;
            String prefix = value.substring(0, len);
            if (prefix.compareTo(range[0]) >= 0 && prefix.compareTo(range[1]) <= 0) {
                return range[2];
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------

    private static List<String[]> load() {
        InputStream in = GS1PrefixRegistry.class.getResourceAsStream(RESOURCE_PATH);
        if (in == null) {
            // Without the range table, isKnownPrefix(...) would reject every value,
            // silently failing all prefix-validated AIs — fail fast instead.
            throw new IllegalStateException("GS1 company prefix registry not found on classpath: " + RESOURCE_PATH);
        }

        List<String[]> list = new ArrayList<>();
        try {
            JsonNode root = new ObjectMapper().readTree(in);
            for (JsonNode entry : root.path("gs1CompanyPrefixes")) {
                JsonNode range = entry.path("range");
                String start       = range.path("start").asText(null);
                String end         = range.path("end").asText(null);
                String description = entry.path("description").asText("");
                if (start == null || end == null) continue;
                list.add(new String[]{start, end, description});
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load GS1 company prefix registry: " + RESOURCE_PATH, e);
        }

        return Collections.unmodifiableList(list);
    }
}
