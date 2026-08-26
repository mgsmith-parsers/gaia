package tools.pantheum.gaia.error;

/**
 * Thrown when a caller-supplied {@link tools.pantheum.gaia.modifier.ModifierInterface}
 * throws while rewriting the input.
 *
 * <p>Raised by {@link tools.pantheum.gaia.modifier.ModifierChain} so the failing
 * modifier is named in the message. {@link tools.pantheum.gaia.GaiaParser} catches it
 * with every other runtime exception and returns a {@code GE-I001} internal-error result;
 * the parse does not continue with a partially modified input.
 */
public class GaiaModifierException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String modifierName;

    /**
     * @param modifierName the {@link tools.pantheum.gaia.modifier.ModifierInterface#getName() name}
     *                     of the modifier that failed
     * @param cause        the exception the modifier threw
     */
    public GaiaModifierException(String modifierName, Throwable cause) {
        super("Modifier '" + modifierName + "' failed: " + cause, cause);
        this.modifierName = modifierName;
    }

    /** The name of the modifier that failed. */
    public String getModifierName() { return modifierName; }
}
