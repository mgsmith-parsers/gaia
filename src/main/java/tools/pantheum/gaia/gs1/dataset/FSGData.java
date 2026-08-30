package tools.pantheum.gaia.gs1.dataset;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;
import tools.pantheum.gaia.gs1.util.ResourceLoadLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reference data for Federal Supply Group (FSG) codes — the NATO Supply Group
 * (NSG) — loaded from the bundled {@code fsg-groups.json} resource at
 * class-initialisation time.
 *
 * <p>The 4-digit Federal Supply Class (FSC) that opens an NSN splits into two
 * parts:
 * <pre>
 *   [ Federal Supply Group (2 digits) ][ Class within the group (2 digits) ]
 * </pre>
 * The group identifies the broad scope of property — {@code 10} is Weapons,
 * {@code 30} is Mechanical Power Transmission Equipment — while the remaining
 * two digits narrow it to a specific class, e.g. {@code 1005} is
 * "Weapons (from 1 mm through 30 mm)". Both levels are covered:
 * {@link #GROUPS} keyed by the 2-digit group, {@link #CLASSES} by the full
 * 4-digit class.
 *
 * <p>Unlike {@link NCBData}, nothing validates against this map, so a load
 * failure is non-fatal: the map is left empty, a warning is printed to stderr
 * and the enricher simply omits the group name.
 *
 * <p>Source: Wikipedia, "List of NATO Supply Classification Groups", retrieved
 * 3 August 2026. The definitive list is the DLA H2 handbook (Federal Supply
 * Classification groups and classes).
 */
public final class FSGData {

    /** Maps 2-digit Federal Supply Group codes to their titles. */
    public static final Map<String, String> GROUPS;

    /** Maps 4-digit Federal Supply Class codes to their titles. */
    public static final Map<String, String> CLASSES;

    static {
        GROUPS  = load(GS1Constants_System.FSG_GROUPS,  "Federal Supply Group lookup disabled");
        CLASSES = load(GS1Constants_System.FSC_CLASSES, "Federal Supply Class lookup disabled");
    }

    private FSGData() {}

    private static Map<String, String> load(String resourcePath, String consequence) {
        Map<String, String> titles = new LinkedHashMap<>();

        try (InputStream is = FSGData.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                ResourceLoadLogger.resourceNotFound("FSGData", resourcePath, consequence);
            } else {
                JsonNode root = new ObjectMapper().readTree(is);
                root.properties().forEach(e -> {
                    String title = e.getValue().asText(null);
                    if (title != null && !title.isEmpty()) titles.put(e.getKey(), title);
                });
            }
        } catch (IOException e) {
            ResourceLoadLogger.loadFailed("FSGData", resourcePath, e.getMessage());
        }

        return Collections.unmodifiableMap(titles);
    }

    /**
     * Returns the group title for the given 2-digit Federal Supply Group code,
     * or {@link Optional#empty()} if the code is not recognised or the data is
     * unavailable.
     *
     * @param code two-digit string, e.g. {@code "10"} for Weapons
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> titleForGroup(String code) {
        return Optional.ofNullable(GROUPS.get(code));
    }

    /**
     * Returns the group title for the group embedded in a 4-digit Federal
     * Supply Class, or {@link Optional#empty()} if the FSC is malformed or the
     * group is not recognised.
     *
     * @param fsc four-digit Federal Supply Class, e.g. {@code "1005"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> titleForFsc(String fsc) {
        if (fsc == null || fsc.length() < 2) return Optional.empty();
        return titleForGroup(fsc.substring(0, 2));
    }

    /**
     * Returns the class title for the given 4-digit Federal Supply Class code,
     * or {@link Optional#empty()} if the code is not recognised or the data is
     * unavailable.
     *
     * @param fsc four-digit Federal Supply Class, e.g. {@code "1005"}
     * @return a new {@code Optional<String>}
     */
    public static Optional<String> titleForClass(String fsc) {
        return Optional.ofNullable(CLASSES.get(fsc));
    }
}
