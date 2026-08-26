package tools.pantheum.gaia.gs1.dataset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link NCBData} — NATO cataloguing nation codes. */
@DisplayName("NCBData")
class NCBDataTest {

    @Test
    @DisplayName("code 00 resolves to United States")
    void knownCode() {
        assertEquals("United States", NCBData.nameForCode("00").orElseThrow());
    }

    @Test
    @DisplayName("unknown codes resolve to empty")
    void unknownCode() {
        assertTrue(NCBData.nameForCode("XX").isEmpty());
    }

    @Test
    @DisplayName("NCB codes match their assigned nations")
    void assignedNations() {
        assertEquals("Germany",        NCBData.nameForCode("12").orElseThrow());
        assertEquals("Belgium",        NCBData.nameForCode("13").orElseThrow());
        assertEquals("Canada",         NCBData.nameForCode("21").orElseThrow());
        assertEquals("Iraq",           NCBData.nameForCode("62").orElseThrow());
        assertEquals("Australia",      NCBData.nameForCode("66").orElseThrow());
        assertEquals("Ireland",        NCBData.nameForCode("84").orElseThrow());
        assertEquals("New Zealand",    NCBData.nameForCode("98").orElseThrow());
        assertEquals("United Kingdom", NCBData.nameForCode("99").orElseThrow());
    }

    @Test
    @DisplayName("the United States holds the whole 00-09 block")
    void unitedStatesBlock() {
        for (int i = 0; i <= 9; i++) {
            String code = String.format("%02d", i);
            assertEquals("United States", NCBData.nameForCode(code).orElseThrow(),
                    "Code " + code + " must resolve to the United States");
        }
    }

    @Test
    @DisplayName("code 44 is held by the United Nations rather than a nation")
    void unitedNationsCode() {
        assertEquals("United Nations", NCBData.nameForCode("44").orElseThrow());
    }

    @Test
    @DisplayName("unassigned codes are absent so the validator rejects them")
    void unassignedCodes() {
        for (String code : new String[]{"10", "11", "67", "69", "85", "90", "97"}) {
            assertTrue(NCBData.nameForCode(code).isEmpty(), "Code " + code + " must be unassigned");
        }
    }

    @Test
    @DisplayName("every key is a two-digit code")
    void keyFormat() {
        for (String code : NCBData.COUNTRY_CODES.keySet()) {
            assertTrue(code.matches("\\d{2}"), "Not a two-digit code: " + code);
        }
    }

    @Test
    @DisplayName("entries carry the CTR alpha-3 code and NCS category")
    void entryFields() {
        NCBEntry au = NCBData.entryFor("66").orElseThrow();
        assertEquals("66",        au.getNcbCode());
        assertEquals("Australia", au.getCountry());
        assertEquals("AUS",       au.getCtr());
        assertEquals("TIER2",     au.getCat());

        NCBEntry gb = NCBData.entryFor("99").orElseThrow();
        assertEquals("GBR",  gb.getCtr());
        assertEquals("NATO", gb.getCat());

        assertTrue(NCBData.entryFor("11").isEmpty(), "Code 11 must be unassigned");
    }

    @Test
    @DisplayName("every entry has a known category and a well-formed CTR where one exists")
    void entryInvariants() {
        for (NCBEntry entry : NCBData.ENTRIES.values()) {
            assertTrue(entry.getCtr() == null || entry.getCtr().matches("[A-Z]{3}"),
                    "Bad CTR for NCB " + entry.getNcbCode() + ": " + entry.getCtr());
            assertTrue(java.util.List.of("NATO", "TIER1", "TIER2", "OTHER", "NSPA").contains(entry.getCat()),
                    "Bad CAT for NCB " + entry.getNcbCode() + ": " + entry.getCat());
        }
    }

    @Test
    @DisplayName("the United Nations entry has no ISO country code")
    void unitedNationsHasNoCtr() {
        NCBEntry un = NCBData.entryFor("44").orElseThrow();
        assertEquals("United Nations", un.getCountry());
        assertNull(un.getCtr(), "NCB 44 is not a nation and carries no alpha-3 code");
        assertEquals("OTHER", un.getCat());
    }

    @Test
    @DisplayName("the two views stay in step")
    void viewsAgree() {
        assertEquals(NCBData.ENTRIES.keySet(), NCBData.COUNTRY_CODES.keySet());
        NCBData.ENTRIES.forEach((code, entry) ->
                assertEquals(entry.getCountry(), NCBData.COUNTRY_CODES.get(code)));
    }
}
