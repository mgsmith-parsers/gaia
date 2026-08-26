package tools.pantheum.gaia.result;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.correlation.CorrelationInfo;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierRegistry;
import tools.pantheum.gaia.datacarrier.registry.EciEntry;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ParseResult} — the top-level parse result. */
@DisplayName("ParseResult")
class ParseResultTest {

    private static final GS1AIObject EMPTY =
            new GS1AIObject(Collections.emptyList(), Collections.emptyList());

    @Test
    @DisplayName("a full parse populates payload, validity, and version metadata")
    void fullParse() {
        ParseResult resp = new GaiaParser().parse("0109506000134352");
        assertTrue(resp.isValid());
        assertEquals("0109506000134352", resp.getPayload());
        assertNotNull(resp.getVersion());
        assertEquals(GS1Constants.GS1ContentType.GS1_APPLICATION_IDENTIFIERS,
                resp.getContentType());
    }

    @Test
    @DisplayName("isValid is true and error lists empty when aiObject is null")
    void nullAiObject() {
        ParseResult resp = new ParseResult("payload", null);
        assertTrue(resp.isValid());
        assertTrue(resp.getErrors().isEmpty());
        assertTrue(resp.getWarnings().isEmpty());
        assertTrue(resp.getIssues().isEmpty());
        assertEquals(GS1Constants.GS1ContentType.UNABLE_TO_DETERMINE_CONTENT, resp.getContentType());
    }

    @Test
    @DisplayName("a rejected non-GS1 carrier reports content type DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL but keeps its error")
    void rejectedCarrierDoesNotSupportAiDl() {
        ParseResult resp = new tools.pantheum.gaia.GaiaParser().parse("]A0ABC-123"); // Code 39, non-GS1
        assertEquals(GS1Constants.GS1ContentType.DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL, resp.getContentType());
        assertFalse(resp.isValid());
        assertFalse(resp.getErrors().isEmpty(), "the carrier-rejection error must still be surfaced");
    }

    @Test
    @DisplayName("getPayloadContent strips the AIM Code ID and ECI indicator")
    void payloadContentStripping() {
        DataCarrierEntry carrier = DataCarrierRegistry.forAimCodeId("]A0").orElseThrow();
        EciEntry eci = DataCarrierRegistry.eciForIndicator("\\000001").orElseThrow();

        ParseResult plain = new ParseResult("DATA", EMPTY, null, null);
        assertEquals("DATA", plain.getPayloadContent());

        ParseResult withCarrier = new ParseResult("]A0DATA", EMPTY, carrier, null);
        assertEquals("DATA", withCarrier.getPayloadContent());
        assertTrue(withCarrier.hasDataCarrier());

        ParseResult withEci = new ParseResult("]A0\\000001DATA", EMPTY, carrier, eci);
        assertEquals("DATA", withEci.getPayloadContent());
        assertTrue(withEci.hasEci());
    }

    @Test
    @DisplayName("withCorrelationInfo attaches the ID and preserves the rest")
    void withCorrelationInfo() {
        ParseResult base = new ParseResult("DATA", EMPTY);
        assertFalse(base.hasCorrelationId());
        ParseResult with = base.withCorrelationInfo(new CorrelationInfo("12345678"));
        assertTrue(with.hasCorrelationId());
        assertEquals("12345678", with.getCorrelationInfo().getId());
        assertEquals("DATA", with.getPayload());
        assertFalse(base.hasCorrelationId(), "The original must be unchanged");
    }

    @Test
    @DisplayName("toString renders a readable summary")
    void toStringSummary() {
        String s = new GaiaParser().parse("0109506000134352").toString();
        assertTrue(s.contains("valid=true"));
        assertTrue(s.contains("(01)"));
    }
}
