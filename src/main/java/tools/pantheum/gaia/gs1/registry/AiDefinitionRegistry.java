package tools.pantheum.gaia.gs1.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Loads GS1 Application Identifier definitions from the bundled JSON-LD resource
 * and provides fast lookup by AI code.
 *
 * <p>AI code length by first-two-digit prefix follows GS1 General Specifications
 * section 7.8.2, Table 7-5; see {@link #AI_LENGTH_BY_PREFIX} and
 * {@link #aiLengthForPrefix(String)}.
 */
public class AiDefinitionRegistry {

    private static final String RESOURCE_PATH = "/gs1-application-identifiers.jsonld";

    /** Maps the first two digits of any AI to the total length of that AI code. */
    public static final Map<String, Integer> AI_LENGTH_BY_PREFIX;

    static {
        Map<String, Integer> m = new HashMap<>();
        // 2-digit AIs
        for (String p : new String[]{"00","01","02","03","10","11","12","13","15","16","17",
                                     "20","21","22","30","37","90","91","92","93","94","95","96","97","98","99"}) {
            m.put(p, 2);
        }
        // 3-digit AIs
        for (String p : new String[]{"23","24","25","40","41","42","71"}) {
            m.put(p, 3);
        }
        // 4-digit AIs
        for (String p : new String[]{"31","32","33","34","35","36","39","43","70","72","80","81","82"}) {
            m.put(p, 4);
        }
        AI_LENGTH_BY_PREFIX = Collections.unmodifiableMap(m);
    }

    private final Map<String, AiDefinition> byCode;

    private static final AiDefinitionRegistry INSTANCE = new AiDefinitionRegistry();

    /**
     * Returns the instance.
     *
     * @return the instance.
     */
    public static AiDefinitionRegistry getInstance() {
        return INSTANCE;
    }

    private AiDefinitionRegistry() {
        byCode = new HashMap<>();
        load();
    }

    private void load() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = AiDefinitionRegistry.class.getResourceAsStream(RESOURCE_PATH)) {
            if (in == null) {
                throw new IllegalStateException("AI definitions not found on classpath: " + RESOURCE_PATH);
            }
            JsonNode root = mapper.readTree(in);
            JsonNode aiArray = root.get("applicationIdentifiers");
            if (aiArray == null || !aiArray.isArray()) {
                throw new IllegalStateException("applicationIdentifiers array not found in JSON-LD");
            }
            for (JsonNode node : aiArray) {
                // Skip vocabulary metadata entries (no short AI code)
                JsonNode aiCodeNode = node.get("applicationIdentifier");
                if (aiCodeNode == null || !aiCodeNode.isTextual()) continue;
                String aiCode = aiCodeNode.asText();
                // Skip if this looks like a full URI (vocabulary metadata)
                if (aiCode.startsWith("http")) continue;

                AiDefinition def = parseDefinition(aiCode, node);
                byCode.put(aiCode, def);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load AI definitions", e);
        }
    }

    private AiDefinition parseDefinition(String aiCode, JsonNode node) {
        String formatString   = textOrEmpty(node, "formatString");
        String description    = textOrEmpty(node, "description");
        String title          = textOrEmpty(node, "title");
        String regex          = textOrEmpty(node, "regex");
        boolean sepRequired   = booleanField(node, "separatorRequired", true);
        boolean validAsDataAttribute = booleanField(node, "validAsDataAttribute", false);
        boolean digitalLinkPrimaryKey = booleanField(node, "gs1DigitalLinkPrimaryKey", false);
        List<List<String>> digitalLinkQualifiers = parseDigitalLinkQualifiers(node.get("gs1DigitalLinkQualifiers"));

        List<AiComponent> components  = parseComponents(node.get("components"));
        List<AiRequiresEntry> requires = parseRequires(node.get("requires"));
        List<AiExcludesEntry> excludes = parseExcludes(node.get("excludes"));

        return new AiDefinition(aiCode, formatString, description, title, regex,
                                sepRequired, validAsDataAttribute, digitalLinkPrimaryKey,
                                digitalLinkQualifiers, components, requires, excludes);
    }

    private List<AiComponent> parseComponents(JsonNode node) {
        List<AiComponent> list = new ArrayList<>();
        if (node == null || !node.isArray()) return list;
        ObjectMapper mapper = new ObjectMapper();
        for (JsonNode c : node) {
            try {
                list.add(mapper.treeToValue(c, AiComponent.class));
            } catch (Exception e) {
                System.err.println("[AiDefinitionRegistry] Skipping malformed component in AI definition: " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Parses the requires field into List&lt;AiRequiresEntry&gt;.
     * Outer list = OR (at least one option must be satisfied).
     * Each AiRequiresEntry = AND (all AIs in that option must be present).
     *
     * JSON forms handled:
     *   ["01","02"]                              → [AiRequiresEntry("01"), AiRequiresEntry("02")]
     *   [["00","37"]]                            → [AiRequiresEntry(["00","37"])]
     *   [{"start":"3100","end":"3195"}]         → [AiRequiresEntry(["3100","3101",...,"3195"])]
     *   mixed array of strings and range objects → both
     */
    private List<AiRequiresEntry> parseRequires(JsonNode node) {
        List<AiRequiresEntry> result = new ArrayList<>();
        if (node == null || !node.isArray()) return result;
        for (JsonNode element : node) {
            if (element.isTextual()) {
                // String: single AI option
                result.add(AiRequiresEntry.ofSingle(element.asText()));
            } else if (element.isArray()) {
                // Array: group of AIs that must all be present
                List<String> group = new ArrayList<>();
                for (JsonNode ai : element) {
                    if (ai.isTextual()) group.add(ai.asText());
                }
                if (!group.isEmpty()) result.add(AiRequiresEntry.ofGroup(group));
            } else if (element.isObject()) {
                // Range object: store as a range entry
                JsonNode startNode = element.get("start");
                JsonNode endNode   = element.get("end");
                if (startNode != null && endNode != null) {
                    result.add(AiRequiresEntry.ofRange(startNode.asText(), endNode.asText()));
                }
            }
        }
        return result;
    }

    /**
     * Parses the excludes field.
     * JSON forms handled:
     *   ["02","03"]                              → exact AI matches
     *   [{"start":"3110","end":"3115"}]          → range matches
     *   mixed array of strings and range objects → both
     */
    private List<AiExcludesEntry> parseExcludes(JsonNode node) {
        List<AiExcludesEntry> result = new ArrayList<>();
        if (node == null || !node.isArray()) return result;
        for (JsonNode element : node) {
            if (element.isTextual()) {
                result.add(AiExcludesEntry.ofExact(element.asText()));
            } else if (element.isObject()) {
                JsonNode startNode = element.get("start");
                JsonNode endNode   = element.get("end");
                if (startNode != null && endNode != null) {
                    result.add(AiExcludesEntry.ofRange(
                            startNode.asText(), endNode.asText()));
                }
            }
        }
        return result;
    }

    /**
     * Parses the {@code gs1DigitalLinkQualifiers} array — an array of arrays of
     * AI code strings — into a list of ordered qualifier sequences. The tokens
     * are stored verbatim, including the optional-marking square brackets (e.g.
     * {@code "[10]"}); the bracket convention is interpreted downstream by
     * {@code DLPathRules}. See {@link AiDefinition#getDigitalLinkQualifiers()}.
     */
    private List<List<String>> parseDigitalLinkQualifiers(JsonNode node) {
        List<List<String>> result = new ArrayList<>();
        if (node == null || !node.isArray()) return result;
        for (JsonNode sequence : node) {
            if (!sequence.isArray()) continue;
            List<String> ais = new ArrayList<>();
            for (JsonNode ai : sequence) {
                if (ai.isTextual()) ais.add(ai.asText());
            }
            result.add(ais);
        }
        return result;
    }

    // -------------------------------------------------------------------------

    /**
     * Returns the {@link AiDefinition} for the given AI code, or
     * {@link Optional#empty()} if the AI is not in the registry.
     *
     * @param aiCode the full AI code, e.g. {@code "01"}, {@code "3102"}
     * @return the find.
     */
    public Optional<AiDefinition> find(String aiCode) {
        return Optional.ofNullable(byCode.get(aiCode));
    }

    /**
     * Returns {@code true} if the given AI code is present in the registry.
     *
     * @param aiCode the full AI code, e.g. {@code "01"}, {@code "3102"}
     * @return {@code true} if is known.
     */
    public boolean isKnown(String aiCode) {
        return byCode.containsKey(aiCode);
    }

    /**
     * Returns the total number of AI definitions loaded from the registry.
     *
     * @return the size.
     */
    public int size() {
        return byCode.size();
    }

    /**
     * Returns an unmodifiable view of all AI definitions in the registry.
     *
     * @return the all.
     */
    public Collection<AiDefinition> all() {
        return Collections.unmodifiableCollection(byCode.values());
    }

    /**
     * Resolves the length of the AI code itself (not its data field) given the
     * first two characters of the AI.  Returns -1 if the prefix is unknown.
     *
     * @param twoDigitPrefix the two digit prefix
     * @return a new {@code int}
     */
    public static int aiLengthForPrefix(String twoDigitPrefix) {
        return AI_LENGTH_BY_PREFIX.getOrDefault(twoDigitPrefix, -1);
    }

    // -------------------------------------------------------------------------

    private static String textOrEmpty(JsonNode node, String field) {
        JsonNode n = node.get(field);
        return (n != null && n.isTextual()) ? n.asText() : "";
    }

    private static boolean booleanField(JsonNode node, String field, boolean defaultValue) {
        JsonNode n = node.get(field);
        return (n != null && n.isBoolean()) ? n.asBoolean() : defaultValue;
    }
}
