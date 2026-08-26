package tools.pantheum.gaia.datacarrier.registry;

import tools.pantheum.gaia.GaiaConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of all AIM data-carrier symbology variants and ECI entries,
 * loaded from the bundled {@code datacarriers.json} classpath resource at
 * class-initialisation time.
 *
 * <h2>Lookup maps</h2>
 * <ul>
 *   <li>{@link #BY_AIM_CODE_ID} — keyed by the three-character AIM Code ID
 *       (e.g. {@code "]C1"})</li>
 *   <li>{@link #ECI_BY_INDICATOR} — keyed by ECI indicator string (e.g. {@code "\\000026"})</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * All public state is immutable after class loading; safe for concurrent use.
 */
public final class DataCarrierRegistry {

    /**
     * All known symbology variants, keyed by AIM Code ID (e.g. {@code "]C1"}).
     * Unmodifiable; never {@code null}. If the bundled resource is missing or
     * unreadable, class initialisation fails with {@link IllegalStateException}.
     */
    public static final Map<String, DataCarrierEntry> BY_AIM_CODE_ID;

    /**
     * All ECI entries, keyed by indicator string (e.g. {@code "\\000026"}).
     * Unmodifiable; never {@code null}. If the bundled resource is missing or
     * unreadable, class initialisation fails with {@link IllegalStateException}.
     */
    public static final Map<String, EciEntry> ECI_BY_INDICATOR;

    static {
        Map<String, DataCarrierEntry> byAimCodeId    = new LinkedHashMap<>();
        Map<String, EciEntry>       eciByIndicator = new LinkedHashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = DataCarrierRegistry.class.getResourceAsStream(GaiaConstants.DATA_CARRIERS_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Data carrier registry not found on classpath: " + GaiaConstants.DATA_CARRIERS_RESOURCE);
            }
            JsonNode root = mapper.readTree(in);

            // --- ECIs ---
            for (JsonNode eci : root.path("ecis")) {
                String indicator = eci.path("indicator").asText("");
                int    number    = eci.path("number").asInt();
                String charset   = eci.path("charset").asText("");
                eciByIndicator.put(indicator, new EciEntry(indicator, number, charset));
            }

            // --- Data carriers ---
            // Every codeCharacters group lists its carriers under "datacarriers".
            for (JsonNode group : root.path("codeCharacters")) {
                for (JsonNode node : group.path("datacarriers")) {
                    DataCarrierEntry entry = parseDataCarrierEntry(node);
                    if (entry.getAimCodeId() != null) {
                        byAimCodeId.put(entry.getAimCodeId(), entry);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load data carrier registry: " + GaiaConstants.DATA_CARRIERS_RESOURCE, e);
        }

        BY_AIM_CODE_ID   = Collections.unmodifiableMap(byAimCodeId);
        ECI_BY_INDICATOR = Collections.unmodifiableMap(eciByIndicator);
    }

    private DataCarrierRegistry() {}

    // -------------------------------------------------------------------------

    /**
     * Looks up a symbology by its AIM Code ID.
     *
     * @param aimCodeId the three-character AIM Code ID, e.g. {@code "]C1"}
     * @return the matching {@link DataCarrierEntry}, or {@link Optional#empty()} if unknown
     */
    public static Optional<DataCarrierEntry> forAimCodeId(String aimCodeId) {
        return Optional.ofNullable(BY_AIM_CODE_ID.get(aimCodeId));
    }

    /**
     * Looks up an ECI entry by its indicator string (e.g. {@code "\\000026"}).
     *
     * @param indicator the ECI indicator string
     * @return the matching {@link EciEntry}, or {@link Optional#empty()} if not defined
     */
    public static Optional<EciEntry> eciForIndicator(String indicator) {
        return Optional.ofNullable(ECI_BY_INDICATOR.get(indicator));
    }

    // -------------------------------------------------------------------------

    private static DataCarrierEntry parseDataCarrierEntry(JsonNode node) {
        return new DataCarrierEntry(
                nullableText(node, "aimCodeId"),
                nullableText(node, "codeChar"),
                nullableText(node, "modifier"),
                nullableText(node, "name"),
                nullableText(node, "description"),
                nullableText(node, "class"),
                nullableText(node, "regex"),
                nullableText(node, "type"),
                nullableText(node, "standard"),
                nullableText(node, "note"),
                node.path("eciCapable").asBoolean(false),
                node.path("eciActive").asBoolean(false),
                node.path("gs1Capable").asBoolean(false),
                node.path("gs1AICapable").asBoolean(false),
                node.path("gs1DigitalLinkCapable").asBoolean(false),
                node.path("requiresGtinPadding").asBoolean(false)
        );
    }

    /** Returns the text value of a node field, or {@code null} if the field is absent or null. */
    private static String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        return field.isNull() || field.isMissingNode() ? null : field.asText();
    }
}
