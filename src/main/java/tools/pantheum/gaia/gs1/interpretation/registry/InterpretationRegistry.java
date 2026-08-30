package tools.pantheum.gaia.gs1.interpretation.registry;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;

import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Loads interpretation definitions and enricher instances from
 * {@code /content/ai-content.json} at class-initialisation time.
 *
 * <p>Each AI entry in the JSON may carry an {@code interpretations} array.
 * Each element of that array is deserialised into an
 * {@link InterpretationDefinition}. Enrichers referenced by simple class name
 * (in the {@code tools.pantheum.gaia.gs1.interpretation.enricher} package)
 * are instantiated once via reflection and cached.
 *
 * <p>This class is thread-safe after initialisation (all state is immutable).
 */
public final class InterpretationRegistry {

    /** The singleton instance. */
    public static final InterpretationRegistry INSTANCE = new InterpretationRegistry();

    private static final String RESOURCE_PATH    = GS1Constants_System.AI_CONTENT;

    /** Maps AI code → ordered list of interpretation definitions. */
    private final Map<String, List<InterpretationDefinition>> definitions;

    /** Maps enricher simple class name → singleton enricher instance. */
    private final Map<String, InterpretationEnricherInterface> enrichers;

    private InterpretationRegistry() {
        Map<String, List<InterpretationDefinition>> defs      = new HashMap<>();
        Map<String, InterpretationEnricherInterface>       enrichMap = new HashMap<>();

        InputStream in = InterpretationRegistry.class.getResourceAsStream(RESOURCE_PATH);
        if (in == null) {
            throw new IllegalStateException("Interpretation definitions not found on classpath: " + RESOURCE_PATH);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(in);
            for (JsonNode entry : root) {
                JsonNode aiNode     = entry.get("ai");
                JsonNode interpNode = entry.get("interpretations");
                if (aiNode == null || aiNode.isNull() || interpNode == null || !interpNode.isArray()) continue;

                String ai = aiNode.asText().trim();
                List<InterpretationDefinition> list = new ArrayList<>();

                for (JsonNode defNode : interpNode) {
                    InterpretationDefinition def = mapper.treeToValue(defNode, InterpretationDefinition.class);
                    list.add(def);

                    // Pre-load enricher if referenced
                    String enricherName = def.getEnricher();
                    if (enricherName != null && !enrichMap.containsKey(enricherName)) {
                        InterpretationEnricherInterface enricher = instantiateEnricher(enricherName);
                        if (enricher != null) enrichMap.put(enricherName, enricher);
                    }
                }

                if (!list.isEmpty()) defs.put(ai, Collections.unmodifiableList(list));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load interpretation definitions: " + RESOURCE_PATH, e);
        }

        this.definitions = Collections.unmodifiableMap(defs);
        this.enrichers   = Collections.unmodifiableMap(enrichMap);
    }

    // -------------------------------------------------------------------------

    /**
     * Returns the ordered list of interpretation definitions for the given AI
     * code, or {@link Optional#empty()} if none are configured.
     *
     * @param ai the AI
     * @return the find.
     */
    public Optional<List<InterpretationDefinition>> find(String ai) {
        return Optional.ofNullable(definitions.get(ai));
    }

    /**
     * Returns the enricher registered under the given simple class name, or
     * {@link Optional#empty()} if it was not loaded.
     *
     * @param simpleClassName the simple class name
     * @return the enricher for.
     */
    public Optional<InterpretationEnricherInterface> enricherFor(String simpleClassName) {
        return Optional.ofNullable(enrichers.get(simpleClassName));
    }

    // -------------------------------------------------------------------------

    private static InterpretationEnricherInterface instantiateEnricher(String simpleClassName) {
        String fqn = GS1Constants_System.ENRICHER_PACKAGE + simpleClassName;
        try {
            Class<?> cls = Class.forName(fqn);
            Object   obj = cls.getDeclaredConstructor().newInstance();
            if (!(obj instanceof InterpretationEnricherInterface)) {
                System.err.println("[InterpretationRegistry] " + fqn
                        + " does not implement InterpretationEnricherInterface — skipping");
                return null;
            }
            return (InterpretationEnricherInterface) obj;
        } catch (ClassNotFoundException e) {
            System.err.println("[InterpretationRegistry] Enricher class not found: " + fqn + " — skipping");
        } catch (ReflectiveOperationException e) {
            System.err.println("[InterpretationRegistry] Could not instantiate " + fqn
                    + ": " + e.getMessage() + " — skipping");
        }
        return null;
    }
}
