package tools.pantheum.gaia.modifier;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaModifierException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ModifierChain} — ordering, no-op handling, and failure propagation. */
@DisplayName("ModifierChain")
class ModifierChainTest {

    @Test
    @DisplayName("returns null when no modifiers are configured")
    void noModifiers() {
        assertNull(ModifierChain.apply("0109506000134352", ParseConfig.defaultConfig()));
    }

    @Test
    @DisplayName("single modifier rewrites the input and is reported as applied")
    void singleModifier() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.GsPlaceholder()).build();

        ModifierInfo info = ModifierChain.apply("10LOT-A{GS}17271231", config);

        assertNotNull(info);
        assertTrue(info.isModified());
        assertEquals("10LOT-A" + TestModifiers.GS + "17271231", info.getModifiedInput());
        assertEquals("10LOT-A{GS}17271231", info.getOriginalInput());
        assertEquals(java.util.List.of("GsPlaceholder"), info.getAppliedModifiers());
    }

    @Test
    @DisplayName("modifiers run in registration order, each fed the previous output")
    void chainsInOrder() {
        ParseConfig config = ParseConfig.builder()
                .modifier(new TestModifiers.StripScanPrefix())
                .modifier(new TestModifiers.GsPlaceholder())
                .build();

        ModifierInfo info = ModifierChain.apply("SCAN:10LOT-A{GS}17271231", config);

        assertEquals("10LOT-A" + TestModifiers.GS + "17271231", info.getModifiedInput());
        assertEquals(java.util.List.of("StripScanPrefix", "GsPlaceholder"), info.getAppliedModifiers());
    }

    @Test
    @DisplayName("a modifier that returns the input unchanged is not reported as applied")
    void noOpNotReported() {
        ParseConfig config = ParseConfig.builder()
                .modifier(new TestModifiers.NoOp())
                .modifier(new TestModifiers.GsPlaceholder())
                .build();

        ModifierInfo info = ModifierChain.apply("10A{GS}", config);

        assertEquals(java.util.List.of("GsPlaceholder"), info.getAppliedModifiers());
    }

    @Test
    @DisplayName("chain of only no-ops reports not modified but still returns info")
    void allNoOps() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.NoOp()).build();

        ModifierInfo info = ModifierChain.apply("0109506000134352", config);

        assertNotNull(info);
        assertFalse(info.isModified());
        assertEquals("0109506000134352", info.getModifiedInput());
        assertTrue(info.getAppliedModifiers().isEmpty());
    }

    @Test
    @DisplayName("a null return is treated as no change and the previous value is kept")
    void nullReturnKeepsPreviousValue() {
        ParseConfig config = ParseConfig.builder()
                .modifier(new TestModifiers.GsPlaceholder())
                .modifier(new TestModifiers.ReturnsNull())
                .build();

        ModifierInfo info = ModifierChain.apply("10A{GS}", config);

        assertEquals("10A" + TestModifiers.GS, info.getModifiedInput());
        assertEquals(java.util.List.of("GsPlaceholder"), info.getAppliedModifiers());
    }

    @Test
    @DisplayName("null input is passed through to the modifiers")
    void nullInput() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.GsPlaceholder()).build();

        ModifierInfo info = ModifierChain.apply(null, config);

        assertNotNull(info);
        assertNull(info.getModifiedInput());
        assertFalse(info.isModified());
    }

    @Test
    @DisplayName("getName() override is used in the applied list")
    void customName() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.CustomName()).build();

        ModifierInfo info = ModifierChain.apply("01", config);

        assertEquals(java.util.List.of("custom-name"), info.getAppliedModifiers());
    }

    @Test
    @DisplayName("config-aware overload receives the parse config")
    void configAwareOverload() {
        ParseConfig config = ParseConfig.builder()
                .language(GaiaConstants.Language.FRENCH)
                .modifier(new TestModifiers.ConfigAware())
                .build();

        assertEquals("01FRENCH", ModifierChain.apply("01", config).getModifiedInput());
    }

    @Test
    @DisplayName("a throwing modifier raises GaiaModifierException naming the modifier")
    void throwingModifier() {
        ParseConfig config = ParseConfig.builder().modifier(new TestModifiers.Throwing()).build();

        GaiaModifierException ex = assertThrows(GaiaModifierException.class,
                () -> ModifierChain.apply("01", config));

        assertEquals("Throwing", ex.getModifierName());
        assertTrue(ex.getMessage().contains("Throwing"), ex.getMessage());
        assertInstanceOf(IllegalStateException.class, ex.getCause());
    }
}
