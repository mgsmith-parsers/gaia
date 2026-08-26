package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CertificationReferenceValidator} via AIs 7230–7239.
 *
 * <p>Structure: [Certification scheme code (2 chars)][Certification reference (1–28 chars)].
 * The fourth digit of the AI (0–9) is the sequence number for multiple occurrences.
 *
 * <p>Currently allowed scheme codes: {@code "EM"} (European Marine Equipment Directive).
 */
@DisplayName("CertificationReferenceValidator — AIs 7230–7239")
class CertificationReferenceValidatorTest {

    static GaiaParser parser;

    /**
     * AIs 7230–7239 require AI (01) or AI (8004) to be present.
     * Prepend a valid fixed-length GTIN so the structural gate passes and the
     * CertificationReferenceValidator actually runs.
     * "09506000134352" is a GTIN-14 with a valid check digit.
     */
    private static final String WITH_GTIN = "0109506000134352";

    @BeforeAll
    static void setup() { parser = new GaiaParser(); }

    // -------------------------------------------------------------------------
    // Valid — known scheme code
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid: scheme EM (European Marine Equipment Directive), sequence 0")
    void validEmSequence0() {
        assertTrue(valid(WITH_GTIN + "7230" + "EMCERTIFICATE01"));
    }

    @Test
    @DisplayName("Valid: scheme EM, sequence 1 (AI 7231)")
    void validEmSequence1() {
        assertTrue(valid(WITH_GTIN + "7231" + "EMREF"));
    }

    @Test
    @DisplayName("Valid: scheme EM, sequence 9 (AI 7239, maximum sequence number)")
    void validEmSequence9() {
        assertTrue(valid(WITH_GTIN + "7239" + "EMCERTIFICATE09"));
    }

    @Test
    @DisplayName("Valid: scheme EM, reference at maximum length (28 chars)")
    void validEmMaxReference() {
        // Total value = "EM" (2) + 28-char reference = 30 chars (maximum)
        assertTrue(valid(WITH_GTIN + "7230" + "EM" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ12"));
    }

    @Test
    @DisplayName("Valid: scheme EM, minimum reference length (1 char)")
    void validEmMinReference() {
        // Total value = "EM" (2) + "A" (1) = 3 chars (minimum)
        assertTrue(valid(WITH_GTIN + "7230" + "EMA"));
    }

    // -------------------------------------------------------------------------
    // Invalid — unrecognised scheme code
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Unknown scheme code XX: fails scheme-code check")
    void unknownSchemeCodeXX() {
        ParseResult resp = parser.parse(WITH_GTIN + "7230" + "XXCERTIFICATE01");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "unrecognised certification scheme code");
    }

    @Test
    @DisplayName("Unknown scheme code AA: fails scheme-code check")
    void unknownSchemeCodeAA() {
        ParseResult resp = parser.parse(WITH_GTIN + "7232" + "AACERTREF");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "unrecognised certification scheme code");
    }

    @Test
    @DisplayName("Unknown scheme code 01 (numeric): fails scheme-code check")
    void unknownSchemeCodeNumeric() {
        ParseResult resp = parser.parse(WITH_GTIN + "7230" + "01CERTREF001");
        assertFalse(resp.isValid());
        assertErrorContains(resp, "unrecognised certification scheme code");
    }

    // -------------------------------------------------------------------------
    // Error message includes sequence number
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("AI 7235 (sequence 5) with unknown scheme is rejected with GE-C123")
    void unknownSchemeAtSequenceFive() {
        // AI 7235 → sequence number 5; scheme "XX" is not recognised
        ParseResult resp = parser.parse(WITH_GTIN + "7235" + "XXCERTREF");
        assertFalse(resp.isValid());
        assertTrue(resp.getErrors().stream().anyMatch(e -> "GE-C123".equals(e.getId())),
                "unknown scheme must raise GE-C123");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean valid(String element) {
        ParseResult resp = parser.parse(element);
        assertTrue(resp.isValid(), () -> "Expected valid but got errors: " + resp.getErrors());
        return true;
    }

    private void assertErrorContains(ParseResult resp, String fragment) {
        assertTrue(resp.getErrors().stream()
                        .anyMatch(e -> e.getMessage().contains(fragment)),
                () -> "Expected error containing '" + fragment + "' but got: " + resp.getErrors());
    }
}
