package tools.pantheum.gaia.gs1.content.componentformat;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.util.IntUtils;

import tools.pantheum.gaia.gs1.content.ComponentFormatInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates {@code pieceoftotal} — a 4-digit value encoded as {@code PPTT}.
 *
 * <p>Rules:
 * <ul>
 *   <li>PP (piece number): 01–99</li>
 *   <li>TT (total):        01–99</li>
 *   <li>Piece must not exceed total (PP ≤ TT)</li>
 * </ul>
 * The piece and total ranges are independent, so both can fail at once.
 *
 * <p>Used in AI 8006 (ITIP) and AI 8026 (ITIP CONTENT).
 */
public final class PieceOfTotalValidator implements ComponentFormatInterface {

    public static final PieceOfTotalValidator INSTANCE = new PieceOfTotalValidator();

    private PieceOfTotalValidator() {}

    static final String ERR_CODE_LENGTH = "GE-C112";  // wrong length
    static final String ERR_CODE_PIECE  = "GE-C113";  // piece number out of range
    static final String ERR_CODE_TOTAL  = "GE-C114";  // total out of range
    static final String ERR_CODE_EXCEED = "GE-C115";  // piece exceeds total

    @Override
    public List<GaiaError> validate(String s, String ai, int position, String component,
                                    String format, GaiaConstants.Language language) {
        if (s.length() != 4)
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_LENGTH, ai, position,
                    Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        List<GaiaError> errors = new ArrayList<>();
        int piece = IntUtils.parseDigits(s, 0, 2);
        int total = IntUtils.parseDigits(s, 2, 4);
        if (piece < 1 || piece > 99) errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_PIECE, ai, position,
                Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (total < 1 || total > 99) errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_TOTAL, ai, position,
                Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        if (errors.isEmpty() && piece > total) errors.add(ErrorRegistry.INSTANCE.create(ERR_CODE_EXCEED, ai, position,
                Map.of("ai", ai, "component", component, "format", format, "value", s), language));
        return errors;
    }
}
