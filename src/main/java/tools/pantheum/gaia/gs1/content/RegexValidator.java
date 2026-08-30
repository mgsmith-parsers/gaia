package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates the data value of each parsed element against the regex pattern
 * defined in the AI definition (JSON-LD {@code regex} field).
 *
 * The pattern is compiled once at registry load time and stored on
 * {@link AiDefinition}; this validator simply applies the pre-compiled
 * {@link Pattern} to the element value.
 * A FORMAT_ERROR (GE-C001) is raised when the value does not conform.
 */
public final class RegexValidator {

    /** The singleton instance. */
    public static final RegexValidator INSTANCE = new RegexValidator();

    private RegexValidator() {}

    /**
     * Validate.
     *
     * @param element the element
     * @param def the def
     * @param config the config
     */
    public void validate(GS1AIObjectElement element, AiDefinition def, ParseConfig config) {
        Pattern pattern = def.getCompiledRegex();
        if (pattern == null) return;

        if (!pattern.matcher(element.getValue()).matches()) {
            element.addError(ErrorRegistry.INSTANCE.create("GE-C001", element.getAi(), element.getPosition(),
                    Map.of(
                            "ai",     element.getAi(),
                            "value",  element.getValue(),
                            "format", def.getFormatString()
                    ), config.getLanguage()));
        }
    }
}
