package tools.pantheum.gaia.config;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.modifier.ModifierInterface;
import tools.pantheum.gaia.modifier.TestModifiers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ParseConfig} — defaults and builder overrides. */
@DisplayName("ParseConfig")
class ParseConfigTest {

    @Test
    @DisplayName("defaultConfig uses INTERPRETATION mode and English")
    void defaults() {
        ParseConfig cfg = ParseConfig.defaultConfig();
        assertEquals(GaiaConstants.ParseMode.INTERPRETATION, cfg.getRequestedParseMode());
        assertEquals(GaiaConstants.Language.ENGLISH, cfg.getLanguage());
        assertNotNull(cfg.getDateEndian());
        assertNotNull(cfg.getDateSeparator());
    }

    @Test
    @DisplayName("builder overrides parse mode and language")
    void builderOverrides() {
        ParseConfig cfg = ParseConfig.builder()
                .requestedParseMode(GaiaConstants.ParseMode.CONTENT)
                .language(GaiaConstants.Language.FRENCH)
                .build();
        assertEquals(GaiaConstants.ParseMode.CONTENT, cfg.getRequestedParseMode());
        assertEquals(GaiaConstants.Language.FRENCH, cfg.getLanguage());
    }

    // -------------------------------------------------------------------------
    // Modifiers
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("no modifiers by default")
    void noModifiersByDefault() {
        ParseConfig cfg = ParseConfig.defaultConfig();
        assertFalse(cfg.hasModifiers());
        assertTrue(cfg.getModifiers().isEmpty());
    }

    @Test
    @DisplayName("modifiers are kept in the order they were added")
    void modifierOrder() {
        ModifierInterface first  = new TestModifiers.StripScanPrefix();
        ModifierInterface second = new TestModifiers.GsPlaceholder();

        ParseConfig cfg = ParseConfig.builder().modifier(first).modifier(second).build();

        assertTrue(cfg.hasModifiers());
        assertEquals(List.of(first, second), cfg.getModifiers());
    }

    @Test
    @DisplayName("modifierClass resolves a fully-qualified class name")
    void modifierByClassName() {
        ParseConfig cfg = ParseConfig.builder()
                .modifierClass(TestModifiers.GsPlaceholder.class.getName())
                .build();

        assertEquals(1, cfg.getModifiers().size());
        assertInstanceOf(TestModifiers.GsPlaceholder.class, cfg.getModifiers().get(0));
    }

    @Test
    @DisplayName("modifierClasses resolves a list, preserving order")
    void modifierClassList() {
        ParseConfig cfg = ParseConfig.builder()
                .modifierClasses(List.of(TestModifiers.StripScanPrefix.class.getName(),
                                         TestModifiers.GsPlaceholder.class.getName()))
                .build();

        assertEquals(2, cfg.getModifiers().size());
        assertInstanceOf(TestModifiers.StripScanPrefix.class, cfg.getModifiers().get(0));
        assertInstanceOf(TestModifiers.GsPlaceholder.class, cfg.getModifiers().get(1));
    }

    @Test
    @DisplayName("an unresolvable modifier class name fails when the config is built")
    void unresolvableModifierClass() {
        assertThrows(IllegalArgumentException.class,
                () -> ParseConfig.builder().modifierClass("com.example.NoSuchModifier").build());
    }

    @Test
    @DisplayName("null and blank modifier entries are ignored")
    void nullEntriesIgnored() {
        ParseConfig cfg = ParseConfig.builder()
                .modifier(null)
                .modifierClass(null)
                .modifierClass("   ")
                .modifiers(null)
                .modifierClasses(null)
                .build();

        assertFalse(cfg.hasModifiers());
    }

    @Test
    @DisplayName("getModifiers is unmodifiable and unaffected by later builder use")
    void modifiersImmutable() {
        ParseConfig.Builder builder = ParseConfig.builder().modifier(new TestModifiers.NoOp());
        ParseConfig cfg = builder.build();

        builder.modifier(new TestModifiers.GsPlaceholder());   // must not leak into the built config

        assertEquals(1, cfg.getModifiers().size());
        assertThrows(UnsupportedOperationException.class,
                () -> cfg.getModifiers().add(new TestModifiers.NoOp()));
    }
}
