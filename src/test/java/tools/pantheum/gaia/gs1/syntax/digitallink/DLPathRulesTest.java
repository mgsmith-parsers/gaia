package tools.pantheum.gaia.gs1.syntax.digitallink;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link DLPathRules} — Digital Link URI structural tables (spec §4.3/4.4/4.9/4.10). */
@DisplayName("DLPathRules")
class DLPathRulesTest {

    @Test
    @DisplayName("primary key membership follows spec §4.3")
    void primaryKeys() {
        assertTrue(DLPathRules.isPrimaryKey("01"));
        assertTrue(DLPathRules.isPrimaryKey("00"));
        assertTrue(DLPathRules.isPrimaryKey("8004"));
        assertFalse(DLPathRules.isPrimaryKey("10"), "Batch/lot is a qualifier, not a primary key");
        assertFalse(DLPathRules.isPrimaryKey("17"));
    }

    @Test
    @DisplayName("key qualifier membership follows spec §4.4")
    void keyQualifiers() {
        assertTrue(DLPathRules.isKeyQualifier("10"));
        assertTrue(DLPathRules.isKeyQualifier("21"));
        assertTrue(DLPathRules.isKeyQualifier("7040"));
        assertFalse(DLPathRules.isKeyQualifier("01"));
        assertFalse(DLPathRules.isKeyQualifier("17"));
    }

    @Test
    @DisplayName("AIs (03) and (8014) are banned (spec §4.10)")
    void bannedAis() {
        assertTrue(DLPathRules.isBanned("03"));
        assertTrue(DLPathRules.isBanned("8014"));
        assertFalse(DLPathRules.isBanned("01"));
    }

    @Test
    @DisplayName("GTIN admits CPV, LOT, SER in order — any subsequence")
    void gtinQualifierSubsequences() {
        assertNull(DLPathRules.validateQualifierSequence("01", List.of()));
        assertNull(DLPathRules.validateQualifierSequence("01", List.of("10")));
        assertNull(DLPathRules.validateQualifierSequence("01", List.of("22", "10", "21")));
        assertNull(DLPathRules.validateQualifierSequence("01", List.of("10", "21")));
        assertNull(DLPathRules.validateQualifierSequence("01", List.of("21")));
    }

    @Test
    @DisplayName("qualifier order violations are rejected (spec §4.9)")
    void orderViolationRejected() {
        assertNotNull(DLPathRules.validateQualifierSequence("01", List.of("21", "10")),
                "SER before LOT violates the gtin-path ordering");
        assertNotNull(DLPathRules.validateQualifierSequence("01", List.of("10", "22")));
    }

    @Test
    @DisplayName("a qualifier not admitted by the primary key is rejected")
    void wrongQualifierRejected() {
        assertNotNull(DLPathRules.validateQualifierSequence("00", List.of("10")),
                "SSCC admits no key qualifiers");
        assertNotNull(DLPathRules.validateQualifierSequence("01", List.of("254")),
                "GLN extension is not a GTIN qualifier");
    }

    @Test
    @DisplayName("a second primary key in the path is rejected with the one-primary-key code")
    void secondPrimaryKeyRejected() {
        Map<String, String> error = DLPathRules.validateQualifierSequence("01", List.of("414"));
        assertNotNull(error);
        assertEquals("GE-L010", error.get("code"), "a second primary key must raise GE-L010");
    }

    @Test
    @DisplayName("payTo (415) requires the payment reference qualifier (8020)")
    void payToRequiresRefno() {
        assertNull(DLPathRules.validateQualifierSequence("415", List.of("8020")));
        assertNotNull(DLPathRules.validateQualifierSequence("415", List.of()),
                "payTo-path = payTo-comp refNo-comp — (8020) is not optional");
    }

    @Test
    @DisplayName("GTIN + TPX (235) is the upui-path — exclusive of other qualifiers")
    void upuiPath() {
        assertNull(DLPathRules.validateQualifierSequence("01", List.of("235")));
        assertNotNull(DLPathRules.validateQualifierSequence("01", List.of("235", "21")),
                "upui-path admits no further qualifiers");
    }

    @Test
    @DisplayName("GLN (414) admits either the GLN extension or the UIC extension, not both")
    void glnAlternatives() {
        assertNull(DLPathRules.validateQualifierSequence("414", List.of()));
        assertNull(DLPathRules.validateQualifierSequence("414", List.of("254")));
        assertNull(DLPathRules.validateQualifierSequence("414", List.of("7040")));
        assertNotNull(DLPathRules.validateQualifierSequence("414", List.of("254", "7040")));
    }
}
