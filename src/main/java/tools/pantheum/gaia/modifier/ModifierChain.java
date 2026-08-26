package tools.pantheum.gaia.modifier;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaModifierException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Runs the {@link ModifierInterface} chain configured on a {@link ParseConfig} over the raw input.
 *
 * <p>Modifiers execute in registration order, each receiving the previous one's output. A
 * {@code null} return is treated as "no change" and the previous value is carried forward.
 * The names of the modifiers that actually changed the input are recorded on the returned
 * {@link ModifierInfo}.
 *
 * <p>Invoked once by {@link tools.pantheum.gaia.GaiaParser} at the start of every parse.
 */
public final class ModifierChain {

    private ModifierChain() {}

    /**
     * Applies {@code config}'s modifiers to {@code input}.
     *
     * @param input  the raw input as passed to the parser; may be {@code null} or empty
     * @param config the parse configuration supplying the modifier chain; never {@code null}
     * @return a {@link ModifierInfo} describing the run, or {@code null} if no modifiers
     *         are configured
     * @throws GaiaModifierException if a modifier throws — the parse must not continue with
     *                               a partially modified input
     */
    public static ModifierInfo apply(String input, ParseConfig config) {
        Objects.requireNonNull(config, "config");
        List<ModifierInterface> modifiers = config.getModifiers();
        if (modifiers.isEmpty()) return null;

        String       current = input;
        List<String> applied = new ArrayList<>();

        for (ModifierInterface modifier : modifiers) {
            String result;
            try {
                result = modifier.modify(current, config);
            } catch (RuntimeException e) {
                throw new GaiaModifierException(modifier.getName(), e);
            }
            if (result == null || result.equals(current)) continue;   // null → no change
            current = result;
            applied.add(modifier.getName());
        }

        return new ModifierInfo(input, current, applied);
    }
}
