package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7241 — AIDC Media Type.
 *
 * <p>The standard pipeline already verifies that the value is exactly 2 digits.
 * This validator checks that the code is one of the currently assigned AIDC
 * media type values defined by ICCBBA for use with the GS1 GSRN.
 *
 * <h2>AIDC media type code table (GS1 General Specifications, AI 7241)</h2>
 * <table border="1">
 *   <caption>AIDC media type codes for AI 7241</caption>
 *   <tr><th>Code</th><th>AIDC media type</th><th>Defined by</th></tr>
 *   <tr><td>{@code 00}</td><td>Not used</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 01}</td><td>Wristband</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 02}</td><td>Order form</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 03}</td><td>Sample tube</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 04}</td><td>Working list / lab list / form</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 05}</td><td>Test report</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 06}</td><td>Delivery note / issue documentation</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 07}</td><td>Intended recipient label (attached to container)</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 08}</td><td>Label affixed to product</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 09}</td><td>Identity card</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 10}</td><td>Clinical or progress notes</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 11}–{@code 29}</td><td>Reserved for ICCBBA future assignment</td><td>ICCBBA</td></tr>
 *   <tr><td>{@code 30}–{@code 59}</td><td>Reserved for GS1 future assignment</td><td>GS1</td></tr>
 *   <tr><td>{@code 60}–{@code 79}</td><td>Reserved for future capacity needs of ICCBBA or GS1</td><td>ICCBBA or GS1</td></tr>
 *   <tr><td>{@code 80}–{@code 99}</td><td>Reserved for local or national use</td><td>ICCBBA</td></tr>
 * </table>
 */
public final class AIDCMediaTypeValidator implements ContentInterface {

    /** The singleton instance. */
    public static final AIDCMediaTypeValidator INSTANCE = new AIDCMediaTypeValidator();

    /** Creates a new {@link AIDCMediaTypeValidator}. */
    public AIDCMediaTypeValidator() {}

    static final String ERR_CODE_NOT_USED        = "GE-C116";
    static final String ERR_CODE_ICCBBA_RESERVED = "GE-C117";
    static final String ERR_CODE_GS1_RESERVED    = "GE-C118";
    static final String ERR_CODE_FUTURE_CAPACITY = "GE-C119";
    static final String ERR_CODE_LOCAL_NATIONAL  = "GE-C120";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String code = components.get(0).getValue();

        if (GS1Constants.AIDC_ASSIGNED_CODES.containsKey(code)) {
            return List.of(); // valid assigned code
        }

        // Provide a specific reason based on the reserved range
        int value = Integer.parseInt(code);

        if (value == 0) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NOT_USED, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }
        if (value <= 29) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_ICCBBA_RESERVED, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }
        if (value <= 59) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_GS1_RESERVED, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }
        if (value <= 79) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_FUTURE_CAPACITY, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }
        // 80–99
        return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LOCAL_NATIONAL, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
    }
}
