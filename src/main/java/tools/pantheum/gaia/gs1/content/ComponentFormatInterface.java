package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;

import java.util.List;

/**
 * Strategy for validating a single component's value against a specific GS1 format constraint.
 *
 * <p>Implementations are registered in {@link FormatValidators} keyed by the {@code format}
 * field of an {@link tools.pantheum.gaia.gs1.registry.AiComponent}, and are invoked by
 * {@link ComponentValidator}.
 *
 * <p>Each implementation builds its localized {@link GaiaError}s itself (via
 * {@code ErrorRegistry.INSTANCE.create(...)} under a condition-specific catalogue code),
 * supplying only data values — the message text comes from the per-language catalogue. The
 * {@code ai}, {@code component}, {@code format}, and {@code value} context tokens are always
 * available to the message template.
 *
 * <p>Implementations are referenced statically from {@link FormatValidators}, so the
 * convention here is a private constructor plus a {@code public static final INSTANCE}
 * singleton — unlike {@link ContentInterface} implementations, which must expose a public
 * no-argument constructor because the content-validator registry instantiates them
 * reflectively by class name.
 */
@FunctionalInterface
public interface ComponentFormatInterface {

    /**
     * Validates {@code value} against this format's rules and returns the resulting
     * errors, or an empty list when valid (one {@link GaiaError} per failing condition).
     *
     * @param value     the component slice to validate (charset already verified)
     * @param ai        the Application Identifier of the enclosing element
     * @param position  the character position of the element in the input
     * @param component the component's data title
     * @param format    the component's format key
     * @param language  the language in which to produce the error messages
     * @return the validate.
     */
    List<GaiaError> validate(String value, String ai, int position, String component,
                             String format, GaiaConstants.Language language);
}
