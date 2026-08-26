package tools.pantheum.gaia.gs1.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link AiDefinition} — the per-AI definition bean. */
@DisplayName("AiDefinition")
class AiDefinitionTest {

    @Test
    @DisplayName("AI (01) definition matches the GS1 spec")
    void gtinDefinition() {
        AiDefinition def = AiDefinitionRegistry.getInstance().find("01").orElseThrow();
        assertEquals("01", def.getApplicationIdentifier());
        assertEquals("N2+N14", def.getFormatString());
        assertEquals("GTIN", def.getTitle());
        assertFalse(def.isSeparatorRequired(), "AI (01) is a predefined fixed-length AI");
        assertEquals(1, def.getComponentCount());
        assertNotNull(def.getRegex());
        assertNotNull(def.getDescription());
    }

    @Test
    @DisplayName("AI (10) is variable length and separator-required")
    void batchLotDefinition() {
        AiDefinition def = AiDefinitionRegistry.getInstance().find("10").orElseThrow();
        assertTrue(def.isSeparatorRequired());
    }

    @Test
    @DisplayName("gs1DigitalLinkPrimaryKey flag is parsed from the AI definitions")
    void digitalLinkPrimaryKeyFlag() {
        AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
        assertTrue(registry.find("01").orElseThrow().isDigitalLinkPrimaryKey(),  // GTIN
                "AI (01) is a Digital Link primary key");
        assertTrue(registry.find("00").orElseThrow().isDigitalLinkPrimaryKey()); // SSCC
        assertFalse(registry.find("10").orElseThrow().isDigitalLinkPrimaryKey(), // batch/lot — qualifier
                "AI (10) is a key qualifier, not a primary key");
        assertFalse(registry.find("17").orElseThrow().isDigitalLinkPrimaryKey());
    }

    @Test
    @DisplayName("gs1DigitalLinkQualifiers sequences are parsed from the AI definitions")
    void digitalLinkQualifiers() {
        AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
        // GTIN admits the optional CPV/LOT/SER sequence and the required TPX sequence (§4.9).
        assertEquals(List.of(List.of("[22]", "[10]", "[21]"), List.of("235")),
                registry.find("01").orElseThrow().getDigitalLinkQualifiers());
        // A non-primary-key AI has no qualifier sequences.
        assertTrue(registry.find("10").orElseThrow().getDigitalLinkQualifiers().isEmpty());
    }
}
