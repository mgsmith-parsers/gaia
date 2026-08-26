package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;

import java.util.List;

/**
 * Pluggable semantic validator for a specific GS1 Application Identifier.
 *
 * <p>Implementations are registered in
 * {@code src/main/resources/content/ai-content.json} and are invoked by
 * {@link tools.pantheum.gaia.gs1.content.ContentValidator} at the end of
 * the content-validation pipeline, <em>only</em> when all prior checks
 * (regex, charset, format, check digit) have passed with no errors.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Return an empty list when the element value is valid; otherwise one
 *       {@link GaiaError} per failing condition, built by the validator itself
 *       (typically via {@code ErrorRegistry.INSTANCE.create(...)} under a
 *       condition-specific catalogue code).</li>
 *   <li>The error message text is sourced from the per-language catalogue — the
 *       validator supplies the code and data values, not the wording.</li>
 *   <li>Implementations must be <em>stateless</em> and <em>thread-safe</em>;
 *       a single instance is shared across all parse calls.</li>
 *   <li>Implementations must provide a public no-argument constructor so the
 *       registry can instantiate them via reflection.</li>
 * </ul>
 */
public interface ContentInterface {

    /**
     * Validates the data value of {@code element} for semantic correctness and
     * returns the resulting localized errors, or an empty list when valid.
     *
     * @param element  the fully-parsed AI element; never {@code null};
     *                 {@link GS1AIObjectElement#getValue()} is guaranteed non-null
     *                 and already free of standard errors when this method is called
     * @param language the language in which to produce the error messages
     * @return one {@link GaiaError} per failing condition, in order; empty if valid
     */
    List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language);
}
