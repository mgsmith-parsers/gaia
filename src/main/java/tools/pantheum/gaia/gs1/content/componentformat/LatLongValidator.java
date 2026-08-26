package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates {@code latlong} — WGS-84 latitude/longitude encoded as N20 (AI 4309).
 *
 * <p>Encoding (GS1 spec):
 * <ul>
 *   <li>Digits 1–10: latitude offset = (latitude + 90) × 10⁶ → range 0000000000–1800000000</li>
 *   <li>Digits 11–20: longitude offset = (longitude + 180) × 10⁶ → range 0000000000–3600000000</li>
 * </ul>
 * Latitude and longitude are independent halves, so both can fail at once.
 */
public final class LatLongValidator implements ComponentFormatInterface {

    public static final LatLongValidator INSTANCE = new LatLongValidator();

    private LatLongValidator() {}

    static final String ERR_CODE_LENGTH    = "GE-C109";  // wrong length
    static final String ERR_CODE_LATITUDE  = "GE-C110";  // latitude out of range
    static final String ERR_CODE_LONGITUDE = "GE-C111";  // longitude out of range

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 20)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        List<GaiaError> errors = new ArrayList<>();
        long latEnc = Long.parseLong(s.substring(0,  10));
        long lonEnc = Long.parseLong(s.substring(10, 20));
        if (latEnc > 1_800_000_000L) errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_LATITUDE, ai, position,
                Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (lonEnc > 3_600_000_000L) errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_LONGITUDE, ai, position,
                Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return errors;
    }
}
