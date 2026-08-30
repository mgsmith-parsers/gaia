package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 7010 — Production Method.
 *
 * <p>The standard pipeline already verifies that the value is 1–2 characters
 * from the GS1 AI encodable character set. This validator checks that the code
 * is one of the GS1-defined production method values for fishery and aquaculture
 * products, aligned with EU Regulation 1379/2013 Article 38 and Annex II.
 *
 * <h2>Allowed production method codes (GS1 General Specifications, AI 7010)</h2>
 * <ul>
 *   <li>{@code "0"} — Caught at sea (wild marine fisheries)</li>
 *   <li>{@code "1"} — Caught in freshwater (wild freshwater fisheries)</li>
 *   <li>{@code "2"} — Farmed (aquaculture, general)</li>
 *   <li>{@code "3"} — Organically farmed (certified organic aquaculture)</li>
 *   <li>{@code "4"} — Caught in freshwater, organically farmed (combined)</li>
 * </ul>
 *
 * <p>Note: this AI is restricted to the fisheries/aquaculture sector and is
 * not multi-sectoral. Its use is limited to applications aligned with EU
 * fisheries labelling regulations.
 */
public final class ProductionMethodValidator implements ContentInterface {

    /** The singleton instance. */
    public static final ProductionMethodValidator INSTANCE = new ProductionMethodValidator();

    /**
     * GS1-defined production method codes for AI 7010, mapped to descriptions.
     * Source: GS1 General Specifications, AI 7010 / EU Regulation 1379/2013.
     */
    public static final Map<String, String> ALLOWED_CODES;

    static {
        Map<String, String> codes = new LinkedHashMap<>();
        // Single-character codes
        codes.put("0",  "Caught at sea (wild marine fisheries)");
        codes.put("1",  "Caught in freshwater (wild freshwater fisheries)");
        codes.put("2",  "Farmed (aquaculture, general)");
        codes.put("3",  "Organically farmed (certified organic aquaculture)");
        codes.put("4",  "Caught in freshwater, organically farmed");
        ALLOWED_CODES = Collections.unmodifiableMap(codes);
    }

    /** Creates a new {@link ProductionMethodValidator}. */
    public ProductionMethodValidator() {}

    static final String ERR_CODE_UNKNOWN = "GE-C163";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String code = components.get(0).getValue();

        if (!ALLOWED_CODES.containsKey(code)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_UNKNOWN, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
