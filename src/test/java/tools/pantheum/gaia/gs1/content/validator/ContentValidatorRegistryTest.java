package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ContentValidatorRegistry}.
 *
 * <p>Verifies that the registry correctly loads and maps AIs to their
 * registered custom validator instances, and returns empty for unregistered AIs.
 */
@DisplayName("ContentValidatorRegistry")
class ContentValidatorRegistryTest {

    // -------------------------------------------------------------------------
    // Known-registered AIs
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("AI 00 (SSCC) maps to SSCCValidator")
    void ai00MapsToSsccValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_00_SSCC);
        assertTrue(v.isPresent());
        assertInstanceOf(SSCCValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 01 (GTIN) maps to GTINValidator")
    void ai01MapsToGtinValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_01_GTIN);
        assertTrue(v.isPresent());
        assertInstanceOf(GTINValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 02 (CONTENT) maps to GTINValidator")
    void ai02MapsToGtinValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_02_CONTENT);
        assertTrue(v.isPresent());
        assertInstanceOf(GTINValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 03 (MTO GTIN) maps to GTINValidator")
    void ai03MapsToGtinValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_03_MTO_GTIN);
        assertTrue(v.isPresent());
        assertInstanceOf(GTINValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 253 (GDTI) maps to GDTIValidator")
    void ai253MapsToGdtiValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_253_GDTI);
        assertTrue(v.isPresent());
        assertInstanceOf(GDTIValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 255 (GCN) maps to GCNValidator")
    void ai255MapsToGcnValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_255_GCN);
        assertTrue(v.isPresent());
        assertInstanceOf(GCNValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 401 (GINC) maps to GINCValidator")
    void ai401MapsToGincValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_401_GINC);
        assertTrue(v.isPresent());
        assertInstanceOf(GINCValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 402 (GSIN) maps to GSINValidator")
    void ai402MapsToGsinValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_402_GSIN);
        assertTrue(v.isPresent());
        assertInstanceOf(GSINValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 410 (SHIP TO LOC) maps to GLNValidator")
    void ai410MapsToGlnValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_410_SHIP_TO_LOC);
        assertTrue(v.isPresent());
        assertInstanceOf(GLNValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 417 (PARTY) maps to GLNValidator")
    void ai417MapsToGlnValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_417_PARTY);
        assertTrue(v.isPresent());
        assertInstanceOf(GLNValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 7230 maps to CertificationReferenceValidator")
    void ai7230MapsToCertValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_7230_CERT_0);
        assertTrue(v.isPresent());
        assertInstanceOf(CertificationReferenceValidator.class, v.get());
    }

    @Test
    @DisplayName("AI 7239 maps to CertificationReferenceValidator")
    void ai7239MapsToCertValidator() {
        Optional<ContentInterface> v = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_7239_CERT_9);
        assertTrue(v.isPresent());
        assertInstanceOf(CertificationReferenceValidator.class, v.get());
    }

    // -------------------------------------------------------------------------
    // AIs with no registered validator
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("AI 10 (BATCH/LOT) has no registered validator")
    void ai10HasNoValidator() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_10_BATCH_LOT).isEmpty());
    }

    @Test
    @DisplayName("AI 21 (SERIAL) has no registered validator")
    void ai21HasNoValidator() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_21_SERIAL).isEmpty());
    }

    @Test
    @DisplayName("Unknown AI code returns empty")
    void unknownAiReturnsEmpty() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("9999").isEmpty());
    }

    @Test
    @DisplayName("Null-like empty string returns empty")
    void emptyStringReturnsEmpty() {
        assertTrue(ContentValidatorRegistry.INSTANCE.find("").isEmpty());
    }

    // -------------------------------------------------------------------------
    // Singleton identity
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("INSTANCE is a singleton — same object on repeated calls")
    void singletonIdentity() {
        assertSame(ContentValidatorRegistry.INSTANCE, ContentValidatorRegistry.INSTANCE);
    }

    @Test
    @DisplayName("Same validator instance returned on repeated find() calls")
    void sameValidatorInstanceReturned() {
        ContentInterface v1 = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_01_GTIN).orElseThrow();
        ContentInterface v2 = ContentValidatorRegistry.INSTANCE.find(GS1Constants_AICodes.AI_01_GTIN).orElseThrow();
        assertSame(v1, v2);
    }
}
