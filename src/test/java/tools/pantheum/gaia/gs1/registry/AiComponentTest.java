package tools.pantheum.gaia.gs1.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AiComponent} — a single component of an AI's format. */
@DisplayName("AiComponent")
class AiComponentTest {

    @Test
    @DisplayName("AI (01)'s single component is a 14-digit numeric key with check digit")
    void gtinComponent() {
        AiComponent comp = AiDefinitionRegistry.getInstance()
                .find("01").orElseThrow().getComponents().get(0);
        assertEquals("N", comp.getType());
        assertTrue(comp.isFixedLength());
        assertEquals(14, comp.getLength());
        assertTrue(comp.isCheckDigit());
        assertFalse(comp.isOptional());
    }

    @Test
    @DisplayName("AI (253)'s serial component is optional with format hint absent")
    void gdtiOptionalComponent() {
        AiComponent serial = AiDefinitionRegistry.getInstance()
                .find("253").orElseThrow().getComponents().get(1);
        assertTrue(serial.isOptional());
        assertFalse(serial.isFixedLength());
        assertEquals("X", serial.getType());
    }

    @Test
    @DisplayName("AI (11)'s component carries the yymmd0 format hint")
    void formatHint() {
        AiComponent date = AiDefinitionRegistry.getInstance()
                .find("11").orElseThrow().getComponents().get(0);
        assertEquals("yymmd0", date.getFormat());
    }
}
