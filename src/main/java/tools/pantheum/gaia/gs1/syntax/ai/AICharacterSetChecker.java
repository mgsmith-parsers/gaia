package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.util.ASCIIRevealerUtils;
import tools.pantheum.gaia.gs1.charset.GS1CharacterSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates characters in GS1 element strings against GS1-defined character sets.
 *
 * <p>Used as a pre-flight check on the entire raw input before tokenising — see
 * {@link #checkInput(String, ParseConfig)} — and can also validate individual AI values
 * against a specific {@link GS1CharacterSet} via
 * {@link #check(String, GS1CharacterSet, int, ParseConfig)}.
 */
public final class AICharacterSetChecker {

    public AICharacterSetChecker() {}

    /**
     * Pre-flight check on the entire raw input string.
     *
     * <p>Every character must belong to at least one of {@link GS1CharacterSet#CSET39},
     * {@link GS1CharacterSet#CSET64}, or {@link GS1CharacterSet#CSET82}, or be the
     * FNC1 group separator (ASCII 0x1D). One {@code GE-S008} error is produced per
     * invalid character.
     *
     * @param input  the raw element string to validate
     * @param config parse configuration — controls the language of error messages
     * @return list of errors; empty when all characters are valid
     */
    public List<GaiaError> checkInput(String input, ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        List<GaiaError> errors = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == GS1Constants.FNC1_GS
                    || GS1CharacterSet.CSET39.contains(c)
                    || GS1CharacterSet.CSET64.contains(c)
                    || GS1CharacterSet.CSET82.contains(c)) continue;

            String display = (c <= 32) ? "{" + ASCIIRevealerUtils.nameOf(c) + "}" : String.valueOf(c);
            errors.add(ErrorRegistry.INSTANCE.create("GE-S008", null, i,
                    Map.of(
                            "character", display,
                            "position",  String.valueOf(i)
                    ), lang));
        }

        return errors;
    }

    /**
     * Validates every character in {@code value} against the given {@link GS1CharacterSet}.
     * The FNC1 group separator is always permitted. One {@code GE-S008} error is produced
     * per invalid character, with positions offset by {@code basePosition}.
     *
     * @param value        the string slice to validate
     * @param charSet      the character set to validate against
     * @param basePosition position of the first character in the original input (for error reporting)
     * @param config       parse configuration — controls the language of error messages
     * @return list of errors; empty when all characters are valid
     */
    public List<GaiaError> check(String value, GS1CharacterSet charSet, int basePosition,
                                 ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        List<GaiaError> errors = new ArrayList<>();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == GS1Constants.FNC1_GS || charSet.contains(c)) continue;

            String display = (c <= 32) ? "{" + ASCIIRevealerUtils.nameOf(c) + "}" : String.valueOf(c);
            int position = basePosition + i;
            errors.add(ErrorRegistry.INSTANCE.create("GE-S008", null, position,
                    Map.of(
                            "character", display,
                            "position",  String.valueOf(position)
                    ), lang));
        }

        return errors;
    }
}
