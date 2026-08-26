package tools.pantheum.gaia.gs1.content.registry;

import tools.pantheum.gaia.gs1.constants.GS1Constants_System;
import tools.pantheum.gaia.gs1.content.ContentInterface;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Loads the per-AI custom validator mapping from
 * {@code src/main/resources/content/ai-content.json} and provides
 * singleton lookup by AI code.
 *
 * <p>Entries in the JSON whose {@code "contentValidator"} field is {@code null} or
 * absent are silently skipped. Entries that name a class that cannot be
 * loaded or instantiated produce a warning on {@code System.err} at startup
 * and are omitted from the registry (fail-open).
 *
 * <p>The registry is initialised once at class-load time and is thread-safe
 * for concurrent reads thereafter.
 */
public final class ContentValidatorRegistry {

    public static final ContentValidatorRegistry INSTANCE = new ContentValidatorRegistry();

    private static final String RESOURCE_PATH = GS1Constants_System.AI_CONTENT;

    private final Map<String, ContentInterface> validators;

    private ContentValidatorRegistry() {
        validators = load();
    }

    /**
     * Returns the custom validator registered for the given AI code, or
     * {@link Optional#empty()} if none is configured.
     *
     * @param ai the Application Identifier code, e.g. {@code "01"}
     */
    public Optional<ContentInterface> find(String ai) {
        return Optional.ofNullable(validators.get(ai));
    }

    // -------------------------------------------------------------------------

    private static Map<String, ContentInterface> load() {
        InputStream in = ContentValidatorRegistry.class.getResourceAsStream(RESOURCE_PATH);
        if (in == null) {
            throw new IllegalStateException("Content validator definitions not found on classpath: " + RESOURCE_PATH);
        }

        Map<String, ContentInterface> map = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);

            for (JsonNode node : root) {
                JsonNode aiNode        = node.get("ai");
                JsonNode validatorNode = node.get("contentValidator");

                if (aiNode == null || aiNode.isNull()) continue;
                if (validatorNode == null || validatorNode.isNull()) continue;

                String ai            = aiNode.asText().trim();
                String validatorName = validatorNode.asText().trim();
                if (ai.isEmpty() || validatorName.isEmpty()) continue;

                ContentInterface instance = instantiate(validatorName);
                if (instance != null) {
                    map.put(ai, instance);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load content validator definitions: " + RESOURCE_PATH, e);
        }

        return Collections.unmodifiableMap(map);
    }

    private static ContentInterface instantiate(String simpleClassName) {
        String fqn = GS1Constants_System.CONTENT_VALIDATOR_PACKAGE + simpleClassName;
        try {
            Class<?> cls = Class.forName(fqn);
            Object obj = cls.getDeclaredConstructor().newInstance();
            if (!(obj instanceof ContentInterface)) {
                System.err.println("[ContentValidatorRegistry] " + fqn
                        + " does not implement ContentInterface — skipping");
                return null;
            }
            return (ContentInterface) obj;
        } catch (ClassNotFoundException e) {
            System.err.println("[ContentValidatorRegistry] Class not found: " + fqn + " — skipping");
        } catch (ReflectiveOperationException e) {
            System.err.println("[ContentValidatorRegistry] Could not instantiate " + fqn
                    + ": " + e.getMessage() + " — skipping");
        }
        return null;
    }
}
