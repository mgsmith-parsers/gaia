package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.gs1.content.validator.SSCCValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests for {@link ContentInterface} — custom AI validators registered
 * in {@code ai-content.json} and resolved via {@link ContentValidatorRegistry}.
 */
@DisplayName("ContentInterface contract")
class ContentInterfaceTest {

    @Test
    @DisplayName("AI (00) resolves to the SSCC implementation")
    void ssccRegistered() {
        ContentInterface v = ContentValidatorRegistry.INSTANCE.find("00").orElse(null);
        assertNotNull(v, "AI (00) must have a custom validator");
        assertTrue(v instanceof SSCCValidator);
    }

    @Test
    @DisplayName("an AI without a custom validator resolves to empty")
    void unregisteredAiEmpty() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("10").isEmpty(),
                "AI (10) has no custom content validator");
    }
}
