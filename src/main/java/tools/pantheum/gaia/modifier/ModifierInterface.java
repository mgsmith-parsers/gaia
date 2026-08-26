package tools.pantheum.gaia.modifier;

import tools.pantheum.gaia.config.ParseConfig;

/**
 * Rewrites the raw input string before any Gaia parsing takes place.
 *
 * <p>Modifiers are caller-supplied code that runs at the very start of
 * {@link tools.pantheum.gaia.GaiaParser#parse(String, ParseConfig)} — before
 * correlation-ID stripping, before data-carrier detection, and before the GS1
 * pipeline is entered. They exist to normalise input that a scanner, middleware,
 * or legacy system has mangled: substituting a printable placeholder back to the
 * GS separator ({@code 0x1D}), trimming a vendor-specific wrapper, upper-casing a
 * Digital Link host, and so on.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Implementations must be <strong>stateless and thread-safe</strong> — a single
 *       instance is cached per class and shared across every parse.</li>
 *   <li>Implementations must expose a <strong>public no-argument constructor</strong>
 *       when they are referenced by class name from
 *       {@link ParseConfig.Builder#modifierClass(String)}.</li>
 *   <li>{@code input} may be {@code null} or empty — the parser does not filter those
 *       out before the chain runs. Handle both.</li>
 *   <li>A {@code null} return is treated as "no change" and the previous value is kept.
 *       Return the unmodified {@code input} when the modifier does not apply.</li>
 *   <li>A modifier that throws aborts the parse; the result carries a {@code GE-I001}
 *       internal error naming the failing modifier. Prefer returning the input unchanged
 *       over throwing.</li>
 * </ul>
 *
 * <h2>Registration</h2>
 * Modifiers are registered per parse on {@link ParseConfig}, either by instance or by
 * fully-qualified class name (the latter is instantiated once by
 * {@link tools.pantheum.gaia.modifier.registry.ModifierRegistry} and cached):
 *
 * <pre>{@code
 * ParseConfig config = ParseConfig.builder()
 *         .modifier(new GsPlaceholderModifier())                              // by instance
 *         .modifierClass("com.example.gaia.StripVendorWrapperModifier")       // by class name
 *         .build();
 * }</pre>
 *
 * Modifiers run in the order they were added, each receiving the previous one's output.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * public final class GsPlaceholderModifier implements ModifierInterface {
 *     public String modify(String input) {
 *         return input == null ? null : input.replace("{GS}", "");
 *     }
 * }
 * }</pre>
 */
public interface ModifierInterface {

    /**
     * Returns the rewritten input.
     *
     * @param input the current input — the original string for the first modifier in the
     *              chain, otherwise the previous modifier's output; may be {@code null} or empty
     * @return the rewritten input, or {@code input} unchanged when the modifier does not
     *         apply; {@code null} is treated as "no change"
     */
    String modify(String input);

    /**
     * Returns the rewritten input, with access to the caller-supplied {@link ParseConfig}.
     *
     * <p>Modifiers that do not need the config inherit this default, which delegates to
     * {@link #modify(String)}. Modifiers whose behaviour depends on the parse mode or
     * language override this method instead.
     *
     * @param input  the current input; may be {@code null} or empty
     * @param config the configuration for this parse — the same instance the modifier was
     *               registered on; never {@code null}
     * @return the rewritten input; {@code null} is treated as "no change"
     */
    default String modify(String input, ParseConfig config) {
        return modify(input);
    }

    /**
     * The name reported in {@link ModifierInfo#getAppliedModifiers()} and in the error
     * message raised when this modifier throws. Defaults to the simple class name.
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
