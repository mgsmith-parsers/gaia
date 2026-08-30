package tools.pantheum.gaia.modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records what the {@link ModifierInterface} chain did to the raw input before parsing began.
 *
 * <p>Attached to the {@link tools.pantheum.gaia.result.ParseResult} whenever at least one
 * modifier was configured, so callers can tell the string they supplied apart from the string
 * the parser actually saw. {@link tools.pantheum.gaia.result.ParseResult#getPayload()}
 * reflects the <em>modified</em> input; {@link #getOriginalInput()} preserves what was passed in.
 */
public final class ModifierInfo {

    private final String       originalInput;
    private final String       modifiedInput;
    private final List<String> appliedModifiers;

    /**
     * Creates a new {@link ModifierInfo}.
     *
     * @param originalInput    the string passed to the parser, before any modifier ran
     * @param modifiedInput    the string the chain produced — what the pipeline parsed
     * @param appliedModifiers names of the modifiers that actually changed the input,
     *                         in execution order; may be empty
     */
    public ModifierInfo(String originalInput, String modifiedInput, List<String> appliedModifiers) {
        this.originalInput    = originalInput;
        this.modifiedInput    = modifiedInput;
        this.appliedModifiers = (appliedModifiers == null || appliedModifiers.isEmpty())
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(appliedModifiers));
    }

    /**
     * The input exactly as passed to {@code GaiaParser.parse(...)}. May be {@code null}.
     *
     * @return the original input.
     */
    public String getOriginalInput() { return originalInput; }

    /**
     * The input after the whole chain ran — the string the pipeline parsed. May be {@code null}.
     *
     * @return the modified input.
     */
    public String getModifiedInput() { return modifiedInput; }

    /**
     * The names ({@link ModifierInterface#getName()}) of the modifiers that changed the input,
     * in the order they ran. Modifiers that ran but returned the input unchanged are not listed.
     * Never {@code null}; empty when the chain was a no-op.
     *
     * @return the applied modifiers.
     */
    public List<String> getAppliedModifiers() { return appliedModifiers; }

    /**
     * Returns {@code true} if the chain changed the input.
     *
     * @return {@code true} if this element is modified.
     */
    public boolean isModified() {
        return !appliedModifiers.isEmpty();
    }

    @Override
    public String toString() {
        return "ModifierInfo{modified=" + isModified()
             + ", appliedModifiers=" + appliedModifiers
             + ", originalInput=\"" + originalInput + "\""
             + ", modifiedInput=\"" + modifiedInput + "\"}";
    }
}
