package tools.pantheum.gaia.modifier.registry;

import tools.pantheum.gaia.modifier.ModifierInterface;
import tools.pantheum.gaia.modifier.TestModifiers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ModifierRegistry} — reflective resolution, caching, and fail-fast rejection. */
@DisplayName("ModifierRegistry")
class ModifierRegistryTest {

    private static final String GS_PLACEHOLDER = TestModifiers.GsPlaceholder.class.getName();

    @Test
    @DisplayName("resolves a fully-qualified class name to a working instance")
    void resolvesByName() {
        ModifierInterface modifier = ModifierRegistry.INSTANCE.resolve(GS_PLACEHOLDER);

        assertNotNull(modifier);
        assertInstanceOf(TestModifiers.GsPlaceholder.class, modifier);
        assertEquals("10A" + TestModifiers.GS, modifier.modify("10A{GS}"));
    }

    @Test
    @DisplayName("returns the same cached instance for repeated resolutions")
    void cachesInstance() {
        assertSame(ModifierRegistry.INSTANCE.resolve(GS_PLACEHOLDER),
                   ModifierRegistry.INSTANCE.resolve(GS_PLACEHOLDER));
        assertTrue(ModifierRegistry.INSTANCE.isCached(GS_PLACEHOLDER));
    }

    @Test
    @DisplayName("surrounding whitespace in the class name is trimmed")
    void trimsName() {
        assertSame(ModifierRegistry.INSTANCE.resolve(GS_PLACEHOLDER),
                   ModifierRegistry.INSTANCE.resolve("  " + GS_PLACEHOLDER + "  "));
    }

    @Test
    @DisplayName("unknown class name is rejected")
    void unknownClass() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ModifierRegistry.INSTANCE.resolve("com.example.NoSuchModifier"));
        assertTrue(ex.getMessage().contains("not found"), ex.getMessage());
    }

    @Test
    @DisplayName("class that does not implement ModifierInterface is rejected")
    void wrongType() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ModifierRegistry.INSTANCE.resolve(TestModifiers.NotAModifier.class.getName()));
        assertTrue(ex.getMessage().contains("does not implement"), ex.getMessage());
    }

    @Test
    @DisplayName("class without a no-argument constructor is rejected")
    void noDefaultConstructor() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ModifierRegistry.INSTANCE.resolve(TestModifiers.NoDefaultConstructor.class.getName()));
        assertTrue(ex.getMessage().contains("no-argument constructor"), ex.getMessage());
    }

    @Test
    @DisplayName("null and blank class names are rejected")
    void nullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> ModifierRegistry.INSTANCE.resolve(null));
        assertThrows(IllegalArgumentException.class, () -> ModifierRegistry.INSTANCE.resolve("   "));
    }

    @Test
    @DisplayName("register() makes a pre-built instance resolvable by its class name")
    void registerInstance() {
        ModifierInterface instance = new TestModifiers.StripScanPrefix();
        ModifierRegistry.INSTANCE.register(instance);

        assertSame(instance, ModifierRegistry.INSTANCE.resolve(TestModifiers.StripScanPrefix.class.getName()));
    }
}
