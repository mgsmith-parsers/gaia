package tools.pantheum.gaia;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the requested vs achieved parse mode reported by {@link ParseResult}. */
@DisplayName("ParseResult parse modes")
class ParseResultModeTest {

    private static final GaiaParser PARSER = new GaiaParser();

    private ParseResult parse(String input, GaiaConstants.ParseMode mode) {
        return PARSER.parse(input, ParseConfig.builder().requestedParseMode(mode).build());
    }

    @Test
    @DisplayName("INTERPRETATION requested + valid input → achieved INTERPRETATION, complete")
    void interpretationReached() {
        ParseResult r = parse("0109506000134352", GaiaConstants.ParseMode.INTERPRETATION);
        assertEquals(GaiaConstants.ParseMode.INTERPRETATION, r.getRequestedParseMode());
        assertEquals(GaiaConstants.ParseMode.INTERPRETATION, r.getAchievedParseMode());
        assertTrue(r.isParseComplete());
        assertTrue(r.isValid());
    }

    @Test
    @DisplayName("INTERPRETATION requested + content error → downgraded to CONTENT, not complete")
    void contentErrorStopsAtContent() {
        ParseResult r = parse("0109506000134350", GaiaConstants.ParseMode.INTERPRETATION); // bad check digit
        assertEquals(GaiaConstants.ParseMode.INTERPRETATION, r.getRequestedParseMode());
        assertEquals(GaiaConstants.ParseMode.CONTENT, r.getAchievedParseMode());
        assertFalse(r.isParseComplete());
        assertFalse(r.isValid());
    }

    @Test
    @DisplayName("CONTENT requested + content error → complete (content ran) yet invalid")
    void completeButInvalid() {
        ParseResult r = parse("0109506000134350", GaiaConstants.ParseMode.CONTENT);
        assertEquals(GaiaConstants.ParseMode.CONTENT, r.getAchievedParseMode());
        assertTrue(r.isParseComplete(), "content stage ran to the requested depth");
        assertFalse(r.isValid(), "but the value is invalid");
    }

    @Test
    @DisplayName("CONTENT requested + syntax error → downgraded to SYNTAX, not complete")
    void syntaxErrorStopsAtSyntax() {
        ParseResult r = parse("ZZ123", GaiaConstants.ParseMode.CONTENT); // unrecognised AI = syntax error
        assertEquals(GaiaConstants.ParseMode.CONTENT, r.getRequestedParseMode());
        assertEquals(GaiaConstants.ParseMode.SYNTAX, r.getAchievedParseMode());
        assertFalse(r.isParseComplete());
    }

    @Test
    @DisplayName("SYNTAX requested + valid input → achieved SYNTAX, complete")
    void syntaxRequestedReached() {
        ParseResult r = parse("0109506000134352", GaiaConstants.ParseMode.SYNTAX);
        assertEquals(GaiaConstants.ParseMode.SYNTAX, r.getAchievedParseMode());
        assertTrue(r.isParseComplete());
    }

    @Test
    @DisplayName("DATA_CARRIER requested on a plain input → achieved DATA_CARRIER, complete")
    void dataCarrierMode() {
        ParseResult r = parse("0109506000134352", GaiaConstants.ParseMode.DATA_CARRIER);
        assertEquals(GaiaConstants.ParseMode.DATA_CARRIER, r.getRequestedParseMode());
        assertEquals(GaiaConstants.ParseMode.DATA_CARRIER, r.getAchievedParseMode());
        assertTrue(r.isParseComplete());
    }

    @Test
    @DisplayName("Rejected non-GS1 carrier (no AI parse) → not complete, achieved SYNTAX")
    void rejectedCarrierNotComplete() {
        // ]A0 is a non-GS1 carrier (Code 39): rejected before any AI parsing
        ParseResult r = parse("]A0ABC-123", GaiaConstants.ParseMode.INTERPRETATION);
        assertFalse(r.isValid());
        assertEquals(GaiaConstants.ParseMode.SYNTAX, r.getAchievedParseMode());
        assertFalse(r.isParseComplete(), "the AI pipeline never ran, so the parse is not complete");
    }
}
