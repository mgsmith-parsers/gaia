package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.content.registry.ContentValidatorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;

import java.util.List;
import java.util.Optional;

/**
 * Orchestrates content-stage validators over the list of parsed elements.
 *
 * <p>Content validation is only invoked when the syntax stage has produced no errors
 * (the {@link tools.pantheum.gaia.gs1.GS1AIParser} is responsible for enforcing that gate).
 *
 * <h2>Validation pipeline (per element)</h2>
 * <ol>
 *   <li>{@link RegexValidator}      — {@code FORMAT_ERROR} (GE-C001) if value fails the AI's regex</li>
 *   <li>{@link ComponentValidator}  — per-component charset (GE-C005) and per-condition
 *       format checks; runs only when regex passes</li>
 *   <li>{@link CheckDigitCharacterValidator} — {@code DATA_ERROR} (GE-C003/GE-C004) for check digit /
 *       check character pair failures; runs only when component checks pass</li>
 *   <li>{@link ContentInterface}  — {@code DATA_ERROR} custom semantic validation under
 *       per-condition codes; runs only when all prior checks pass with no errors;
 *       implementations are registered in {@code src/main/resources/content/ai-content.json}</li>
 * </ol>
 */
public class ContentValidator {

    private final AiDefinitionRegistry      registry;
    private final ContentValidatorRegistry  aiValidatorRegistry;
    private final RegexValidator            regexValidator;
    private final ComponentValidator        componentValidator;
    private final CheckDigitCharacterValidator checkDigitCharacterValidator;

    public ContentValidator(AiDefinitionRegistry registry) {
        this.registry            = registry;
        this.aiValidatorRegistry = ContentValidatorRegistry.INSTANCE;
        this.regexValidator      = RegexValidator.INSTANCE;
        this.componentValidator  = ComponentValidator.INSTANCE;
        this.checkDigitCharacterValidator = CheckDigitCharacterValidator.INSTANCE;
    }

    /**
     * Validates every element in order:
     * regex → component (charset + format) → check digit → custom validator.
     * Each subsequent step runs only when the preceding step produced no errors.
     *
     * @param elements the parsed elements to validate
     * @param config   parse configuration — controls the language of error messages
     */
    public void validate(List<GS1AIObjectElement> elements, ParseConfig config) {
        for (GS1AIObjectElement element : elements) {
            Optional<AiDefinition> defOpt = registry.find(element.getAi());
            if (defOpt.isEmpty()) continue; // unknown AI already caught in syntax stage

            AiDefinition def = defOpt.get();

            regexValidator.validate(element, def, config);

            // Component charset + format checks only run when the regex passes.
            if (!element.hasErrors()) {
                componentValidator.validate(element, config);
            }

            // Check digit validation only runs when component checks pass — avoids a
            // spurious check-digit error (GE-C003/GE-C004) on a value that already has
            // a charset error (GE-C005).
            if (!element.hasErrors()) {
                checkDigitCharacterValidator.validate(element, def, config);
            }

            // Custom semantic validation runs last, only when all prior checks pass.
            if (!element.hasErrors()) {
                aiValidatorRegistry.find(element.getAi()).ifPresent(customValidator ->
                        customValidator.validate(element, config.getLanguage()).forEach(element::addError));
            }
        }
    }
}
