package tools.pantheum.gaia.modifier;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants;

/**
 * Reusable {@link ModifierInterface} fixtures for the modifier tests.
 *
 * <p>Each is a public static nested class with an implicit public no-argument constructor,
 * so it can be resolved by name through
 * {@link tools.pantheum.gaia.modifier.registry.ModifierRegistry} the same way a
 * caller-supplied modifier would be.
 */
public final class TestModifiers {

    private TestModifiers() {}

    /** The printable placeholder some middleware substitutes for the GS separator. */
    public static final String GS_PLACEHOLDER = "{GS}";

    /**
     * The real GS separator (ASCII 0x1D) as a one-character string. Derived from the
     * constant rather than written as a literal so no raw control character has to
     * survive in a test source file.
     */
    public static final String GS = String.valueOf(GS1Constants.FNC1_GS);

    /** Replaces the printable {@code {GS}} placeholder with the real GS character (0x1D). */
    public static class GsPlaceholder implements ModifierInterface {
        @Override
        public String modify(String input) {
            return input == null ? null : input.replace(GS_PLACEHOLDER, GS);
        }
    }

    /** Strips a leading {@code SCAN:} wrapper some scanners prepend. */
    public static class StripScanPrefix implements ModifierInterface {
        @Override
        public String modify(String input) {
            return (input != null && input.startsWith("SCAN:")) ? input.substring("SCAN:".length()) : input;
        }
    }

    /** Never changes the input — used to prove no-op modifiers are not reported as applied. */
    public static class NoOp implements ModifierInterface {
        @Override
        public String modify(String input) { return input; }
    }

    /** Always returns {@code null} — treated as "no change" by the chain. */
    public static class ReturnsNull implements ModifierInterface {
        @Override
        public String modify(String input) { return null; }
    }

    /** Always throws — used to prove the parse aborts with an internal error. */
    public static class Throwing implements ModifierInterface {
        @Override
        public String modify(String input) { throw new IllegalStateException("boom"); }
    }

    /** Overrides {@link ModifierInterface#getName()} to prove the custom name is reported. */
    public static class CustomName implements ModifierInterface {
        @Override
        public String modify(String input) { return input + "X"; }
        @Override
        public String getName() { return "custom-name"; }
    }

    /** Overrides the config-aware overload; appends the configured language. */
    public static class ConfigAware implements ModifierInterface {
        @Override
        public String modify(String input) { return input; }
        @Override
        public String modify(String input, ParseConfig config) { return input + config.getLanguage(); }
    }

    /** Does not implement {@link ModifierInterface} — rejected by the registry. */
    public static class NotAModifier {
    }

    /** Implements the interface but has no accessible no-argument constructor. */
    public static class NoDefaultConstructor implements ModifierInterface {
        public NoDefaultConstructor(String required) { }
        @Override
        public String modify(String input) { return input; }
    }
}
