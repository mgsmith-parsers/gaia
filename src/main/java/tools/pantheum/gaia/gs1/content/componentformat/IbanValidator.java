package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.gs1.util.CharUtils;
import tools.pantheum.gaia.gs1.util.IBANUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import java.util.List;
import java.util.Map;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;

/**
 * Validates {@code iban} — International Bank Account Number.
 *
 * <p>Rules:
 * <ul>
 *   <li>Length 5–34</li>
 *   <li>Positions 1–2: ISO 3166-1 alpha-2 country code (two uppercase letters)</li>
 *   <li>Positions 3–4: 2-digit check number</li>
 *   <li>Positions 5–end: BBAN — uppercase letters and digits only</li>
 *   <li>MOD-97 check digit verification (result must equal 1)</li>
 * </ul>
 */
public final class IbanValidator implements ComponentFormatInterface {

    /** The singleton instance. */
    public static final IbanValidator INSTANCE = new IbanValidator();

    private IbanValidator() {}

    static final String ERR_CODE_LENGTH          = "GE-C060";
    static final String ERR_CODE_COUNTRY_LETTERS = "GE-C061";
    static final String ERR_CODE_COUNTRY_CODE    = "GE-C062";
    static final String ERR_CODE_CHECK_DIGITS    = "GE-C063";
    static final String ERR_CODE_BBAN_CHAR       = "GE-C064";
    static final String ERR_CODE_MOD97           = "GE-C065";

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() < 5 || s.length() > 34)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));

        char c0 = s.charAt(0), c1 = s.charAt(1);
        if (!CharUtils.isUpperAlpha(c0) || !CharUtils.isUpperAlpha(c1))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_COUNTRY_LETTERS, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));

        String countryCode = s.substring(0, 2);
        if (!Iso3166Data.ALPHA2_TO_NAME.isEmpty() && !Iso3166Data.ALPHA2_TO_NAME.containsKey(countryCode))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_COUNTRY_CODE, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));

        char d0 = s.charAt(2), d1 = s.charAt(3);
        if (!CharUtils.isDigit(d0) || !CharUtils.isDigit(d1))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_CHECK_DIGITS, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));

        for (int i = 4; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!CharUtils.isDigit(c) && !CharUtils.isUpperAlpha(c))
                return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_BBAN_CHAR, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        }

        if (!IBANUtils.verifyIbanMod97(s))
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_MOD97, ai, position, Map.of("ai", ai, "component", component, "format", format, "value", s), language));

        return List.of();
    }
}
