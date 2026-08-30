package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiComponent;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.GS1Utils;

import java.util.Map;

/**
 * Validates check digits and check character pairs embedded in AI data values.
 *
 * <p>This validator walks each component of an AI and applies the algorithm the
 * component declares (both implemented in {@link tools.pantheum.gaia.gs1.util.GS1Utils}):
 * <ol>
 *   <li>{@code checkDigit} — <b>standard modulo-10</b> (GS1 spec §7.9.1,
 *       {@link tools.pantheum.gaia.gs1.util.GS1Utils#verifyModulo10}); fails with
 *       {@code GE-C003}. Applies to GTIN, SSCC, GLN, GSIN, GDTI, GIAI, GRAI, GSRN, GCN, etc.</li>
 *   <li>{@code checkCharacters} — <b>MOD 1021,32 check character pair</b> (GS1 spec §7.9.5,
 *       {@link tools.pantheum.gaia.gs1.util.GS1Utils#verifyMod102132}); fails with
 *       {@code GE-C004}. Applies to the Global Model Number (GMN, AI 8013) and the
 *       Highly Individualised Device Registration Identifier (HIDRI, AI 8014).</li>
 * </ol>
 *
 * For multi-component AIs (e.g. ITIP/8006: GTIN-14 + piece + total), the check
 * applies only to the sub-string covered by the component that carries the flag.
 */
public final class CheckDigitCharacterValidator {

    /** The singleton instance. */
    public static final CheckDigitCharacterValidator INSTANCE = new CheckDigitCharacterValidator();

    private CheckDigitCharacterValidator() {}

    /**
     * Validate.
     *
     * @param element the element
     * @param def the def
     * @param config the config
     */
    public void validate(GS1AIObjectElement element, AiDefinition def, ParseConfig config) {
        String value = element.getValue();
        int offset = 0;

        for (AiComponent component : def.getComponents()) {
            if (!component.isCheckDigit() && !component.isCheckCharacters()) {
                offset += component.isFixedLength() ? component.getLength() : value.length() - offset;
                continue;
            }

            // Extract the substring belonging to this component
            int componentEnd = component.isFixedLength()
                    ? offset + component.getLength()
                    : value.length();

            if (componentEnd > value.length()) break; // truncation already caught by parser

            String componentValue = value.substring(offset, componentEnd);

            if (component.isCheckDigit()) {
                // Standard modulo-10 check digit
                if (!GS1Utils.verifyModulo10(componentValue)) {
                    element.addError(ErrorRegistry.INSTANCE.create("GE-C003", element.getAi(), element.getPosition(),
                            Map.of("ai", element.getAi(), "value", value), config.getLanguage()));
                }
            } else {
                // MOD 1021,32 alphanumeric check character pair
                if (!GS1Utils.verifyMod102132(componentValue)) {
                    element.addError(ErrorRegistry.INSTANCE.create("GE-C004", element.getAi(), element.getPosition(),
                            Map.of("ai", element.getAi(), "value", value), config.getLanguage()));
                }
            }

            offset = componentEnd;
        }
    }
}
