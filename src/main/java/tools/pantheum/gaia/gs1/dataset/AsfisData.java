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
 * Reference data for FAO ASFIS (Aquatic Sciences and Fisheries Information
 * System) species codes, loaded from the bundled {@code asfis-species.json}
 * resource at class-initialisation time.
 *
 * <p>The dataset is sourced from the FAO ASFIS List of Species for Fishery
 * Statistics Purposes (2025 edition):
 * <a href="https://www.fao.org/fishery/collection/asfis/en">
 * https://www.fao.org/fishery/collection/asfis/en</a>.
 *
 * <p>Each entry is keyed by the 3-letter Alpha-3 code used in GS1 AI 7008
 * (AQUATIC SPECIES). The lookup is case-sensitive; codes are uppercase.
 *
 * <p>Use {@link #entryFor(String)} for lookups. The map is immutable. If the
 * resource cannot be loaded the map is empty and a warning is printed to
 * stderr; validators then skip the lookup and fail open.
 *
 * @see AsfisEntry
 */
public final class AsfisData {

    /** Maps 3-letter Alpha-3 species codes (e.g. {@code "COD"}) to species entries. */
    public static final Map<String, AsfisEntry> SPECIES;

    static {
        Map<String, AsfisEntry> species = new HashMap<>();

        try (InputStream is = AsfisData.class.getResourceAsStream(GS1Constants_System.ASFIS_SPECIES)) {
            if (is == null) {
                ResourceLoadLogger.resourceNotFound("AsfisData", GS1Constants_System.ASFIS_SPECIES, "ASFIS species lookup disabled");
            } else {
                JsonNode root = new ObjectMapper().readTree(is);
                root.properties().forEach(e -> {
                    String   code  = e.getKey();
                    JsonNode node  = e.getValue();
                    String   sci   = node.path("scientificName").asText(null);
                    String   eng   = node.path("englishName").asText(null);
                    String   fam   = node.path("family").asText(null);
                    String   ord   = node.path("order").asText(null);
                    if (sci != null) {
                        species.put(code, new AsfisEntry(code, sci, eng, fam, ord));
                    }
                });
            }
        } catch (IOException e) {
            ResourceLoadLogger.loadFailed("AsfisData", GS1Constants_System.ASFIS_SPECIES, e.getMessage());
        }

        SPECIES = Collections.unmodifiableMap(species);
    }

    private AsfisData() {}

    /**
     * Returns the {@link AsfisEntry} for the given Alpha-3 species code, or
     * {@link Optional#empty()} if the code is not recognised or data is unavailable.
     *
     * @param alpha3Code 1–3 character uppercase code, e.g. {@code "COD"}
     */
    public static Optional<AsfisEntry> entryFor(String alpha3Code) {
        return Optional.ofNullable(SPECIES.get(alpha3Code));
    }

}
