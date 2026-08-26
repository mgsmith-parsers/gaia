package tools.pantheum.gaia.datacarrier.registry;

import tools.pantheum.gaia.GaiaConstants.DataCarrierTypeCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataCarrierTypeTest {

    @Test
    @DisplayName("resolves the common data carriers from their AIM Code IDs")
    void resolvesCommonTypes() {
        assertEquals(DataCarrierType.QR_CODE,     DataCarrierType.forAimCodeId("]Q1"));
        assertEquals(DataCarrierType.DATA_MATRIX, DataCarrierType.forAimCodeId("]d1"));
        assertEquals(DataCarrierType.ITF,         DataCarrierType.forAimCodeId("]I0"));
        assertEquals(DataCarrierType.CODE_128,    DataCarrierType.forAimCodeId("]C0"));
        assertEquals(DataCarrierType.PDF417,      DataCarrierType.forAimCodeId("]L0"));
        assertEquals(DataCarrierType.AZTEC_CODE,  DataCarrierType.forAimCodeId("]z0"));
    }

    @Test
    @DisplayName("GS1-reserved modifiers resolve to their own constants")
    void gs1VariantsAreDistinct() {
        assertEquals(DataCarrierType.GS1_128,         DataCarrierType.forAimCodeId("]C1"));
        assertEquals(DataCarrierType.GS1_DATA_MATRIX, DataCarrierType.forAimCodeId("]d2"));
        assertEquals(DataCarrierType.GS1_QR_CODE,     DataCarrierType.forAimCodeId("]Q3"));
        assertEquals(DataCarrierType.GS1_DOT_CODE,    DataCarrierType.forAimCodeId("]J1"));

        assertNotEquals(DataCarrierType.QR_CODE, DataCarrierType.forAimCodeId("]Q3"));
        assertTrue(DataCarrierType.GS1_QR_CODE.isGs1DataCarrier());
        assertFalse(DataCarrierType.QR_CODE.isGs1DataCarrier());
    }

    @Test
    @DisplayName("ignoring GS1 variants resolves them to their plain families")
    void gs1VariantsCanBeIgnored() {
        assertEquals(DataCarrierType.CODE_128,    DataCarrierType.forAimCodeId("]C1", true));
        assertEquals(DataCarrierType.DATA_MATRIX, DataCarrierType.forAimCodeId("]d2", true));
        assertEquals(DataCarrierType.QR_CODE,     DataCarrierType.forAimCodeId("]Q3", true));
        assertEquals(DataCarrierType.DOT_CODE,    DataCarrierType.forAimCodeId("]J1", true));

        for (String id : new String[]{"]C1", "]d2", "]Q3", "]J1"}) {
            assertFalse(DataCarrierType.forAimCodeId(id, true).isGs1DataCarrier(),
                    id + " must not resolve to a GS1-reserved constant when they are ignored");
        }
    }

    @Test
    @DisplayName("the flag changes nothing for non-GS1-reserved AIM Code IDs")
    void ignoringGs1VariantsLeavesEverythingElseAlone() {
        for (String id : new String[]{"]Q1", "]d1", "]I0", "]C0", "]L0", "]z0", "]E4", "]Q9", "]Y0", "]QQ", ""}) {
            assertEquals(DataCarrierType.forAimCodeId(id), DataCarrierType.forAimCodeId(id, true),
                    "resolution of " + id + " must not depend on the flag");
        }
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId(null, true));
    }

    @Test
    @DisplayName("GS1 DataBar is a family, not a reserved variant, so the flag does not affect it")
    void gs1DataBarIsUnaffected() {
        // ]e* has no plain counterpart — GS1_DATABAR is the family itself.
        assertEquals(DataCarrierType.GS1_DATABAR, DataCarrierType.forAimCodeId("]e0"));
        assertEquals(DataCarrierType.GS1_DATABAR, DataCarrierType.forAimCodeId("]e0", true));
    }

    @Test
    @DisplayName("isGs1DataCarrier covers the four reserved variants and GS1 DataBar")
    void isGs1DataCarrierCoversDataBar() {
        for (DataCarrierType t : new DataCarrierType[]{
                DataCarrierType.GS1_128, DataCarrierType.GS1_DATA_MATRIX,
                DataCarrierType.GS1_QR_CODE, DataCarrierType.GS1_DOT_CODE,
                DataCarrierType.GS1_DATABAR}) {
            assertTrue(t.isGs1DataCarrier(), t + " always denotes GS1 AI data");
        }

        // GS1 DataBar qualifies even though it is not a reserved variant.
        assertTrue(DataCarrierType.GS1_DATABAR.isGs1DataCarrier());
        assertTrue(DataCarrierType.forAimCodeId("]e0").isGs1DataCarrier());
        assertTrue(DataCarrierType.forAimCodeId("]e0", true).isGs1DataCarrier(),
                "GS1 DataBar cannot be collapsed, so it stays a GS1 data carrier under the flag");
    }

    @Test
    @DisplayName("isGs1DataCarrier is false for carriers that merely may hold GS1 data")
    void isGs1DataCarrierExcludesPlainFamilies() {
        for (DataCarrierType t : new DataCarrierType[]{
                DataCarrierType.QR_CODE, DataCarrierType.DATA_MATRIX, DataCarrierType.CODE_128,
                DataCarrierType.DOT_CODE, DataCarrierType.EAN_UPC, DataCarrierType.ITF,
                DataCarrierType.PDF417, DataCarrierType.UNKNOWN}) {
            assertFalse(t.isGs1DataCarrier(), t + " does not always denote GS1 AI data");
        }
    }

    @Test
    @DisplayName("exactly five constants are GS1 data carriers")
    void exactlyFiveGs1DataCarriers() {
        long count = java.util.Arrays.stream(DataCarrierType.values())
                .filter(DataCarrierType::isGs1DataCarrier)
                .count();
        assertEquals(5, count, "Four reserved variants plus GS1 DataBar");
    }

    @Test
    @DisplayName("the single-argument method keeps its original behaviour")
    void singleArgumentOverloadUnchanged() {
        assertEquals(DataCarrierType.GS1_128,         DataCarrierType.forAimCodeId("]C1"));
        assertEquals(DataCarrierType.GS1_DATA_MATRIX, DataCarrierType.forAimCodeId("]d2"));
        assertEquals(DataCarrierType.GS1_QR_CODE,     DataCarrierType.forAimCodeId("]Q3"));
        assertEquals(DataCarrierType.GS1_DOT_CODE,    DataCarrierType.forAimCodeId("]J1"));

        for (String id : new String[]{"]C1", "]d2", "]Q3", "]J1", "]Q1", "]I0", "]Y0"}) {
            assertEquals(DataCarrierType.forAimCodeId(id, false), DataCarrierType.forAimCodeId(id),
                    "the one-argument form must equal the two-argument form with false");
        }
    }

    @Test
    @DisplayName("code character selects the family for unlisted modifiers")
    void unlistedModifiersFallBackToFamily() {
        assertEquals(DataCarrierType.QR_CODE, DataCarrierType.forAimCodeId("]Q9"));
        assertEquals(DataCarrierType.ITF,     DataCarrierType.forAimCodeId("]I7"));
    }

    @Test
    @DisplayName("absent, malformed or unknown AIM Code IDs give UNKNOWN")
    void unknownInputsGiveUnknown() {
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId(null));
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId(""));
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("]"));
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("]Y0"));
    }

    @Test
    @DisplayName("input that is not a well-formed AIM Code ID gives UNKNOWN, not a guess")
    void malformedIdsAreNotGuessed() {
        // Right code character, but no ']' lead — must not resolve.
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("XQ1"));
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("[d2"));
        // Missing the modifier digit.
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("]Q"));
        // Modifier present but not a digit.
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("]QQ"));
        // Code character present but not a letter.
        assertEquals(DataCarrierType.UNKNOWN, DataCarrierType.forAimCodeId("]11"));
    }

    @Test
    @DisplayName("case of the code character is significant")
    void codeCharacterIsCaseSensitive() {
        assertEquals(DataCarrierType.CODE_ONE,    DataCarrierType.forAimCodeId("]D0"));
        assertEquals(DataCarrierType.DATA_MATRIX, DataCarrierType.forAimCodeId("]d0"));
    }

    @Test
    @DisplayName("every registry entry resolves to a known data carrier type")
    void everyRegistryEntryIsCovered() {
        for (Map.Entry<String, DataCarrierEntry> e : DataCarrierRegistry.BY_AIM_CODE_ID.entrySet()) {
            assertNotEquals(DataCarrierType.UNKNOWN, e.getValue().getDataCarrierType(),
                    "no DataCarrierType constant covers AIM Code ID " + e.getKey());
        }
    }

    @Test
    @DisplayName("category agrees with the registry type field")
    void categoryMatchesRegistryType() {
        for (Map.Entry<String, DataCarrierEntry> e : DataCarrierRegistry.BY_AIM_CODE_ID.entrySet()) {
            DataCarrierEntry entry = e.getValue();
            DataCarrierTypeCategory expected;
            switch (entry.getType()) {
                case "linear":         expected = DataCarrierTypeCategory.LINEAR;         break;
                case "stacked_linear": expected = DataCarrierTypeCategory.STACKED_LINEAR; break;
                case "2d":             expected = DataCarrierTypeCategory.TWO_D;          break;
                case "postal":         expected = DataCarrierTypeCategory.POSTAL;         break;
                case "ocr":            expected = DataCarrierTypeCategory.OCR;            break;
                default:               expected = DataCarrierTypeCategory.OTHER;          break;
            }
            assertEquals(expected, entry.getDataCarrierType().getCategory(),
                    "category mismatch for AIM Code ID " + e.getKey());
        }
    }

    @Test
    @DisplayName("DataCarrierEntry exposes the type of the looked-up entry")
    void entryExposesDataCarrierType() {
        DataCarrierEntry qr = DataCarrierRegistry.forAimCodeId("]Q3").orElseThrow(AssertionError::new);
        assertEquals(DataCarrierType.GS1_QR_CODE, qr.getDataCarrierType());
        assertEquals(DataCarrierTypeCategory.TWO_D, qr.getDataCarrierType().getCategory());

        DataCarrierEntry itf = DataCarrierRegistry.forAimCodeId("]I1").orElseThrow(AssertionError::new);
        assertEquals(DataCarrierType.ITF, itf.getDataCarrierType());
        assertEquals(DataCarrierTypeCategory.LINEAR, itf.getDataCarrierType().getCategory());
    }

    @Test
    @DisplayName("TWO_D excludes the stacked-linear carriers")
    void twoDExcludesStackedLinear() {
        assertEquals(DataCarrierTypeCategory.TWO_D, DataCarrierType.DATA_MATRIX.getCategory());
        assertEquals(DataCarrierTypeCategory.TWO_D, DataCarrierType.AZTEC_CODE.getCategory());

        assertEquals(DataCarrierTypeCategory.STACKED_LINEAR, DataCarrierType.PDF417.getCategory());
        assertEquals(DataCarrierTypeCategory.STACKED_LINEAR, DataCarrierType.CODE_16K.getCategory());
    }

    @Test
    @DisplayName("the type's display name may be broader than the entry's own name")
    void displayNameMayBeBroaderThanEntryName() {
        DataCarrierEntry ean8 = DataCarrierRegistry.forAimCodeId("]E4").orElseThrow(AssertionError::new);
        assertEquals("EAN-8", ean8.getName());
        assertEquals(DataCarrierType.EAN_UPC, ean8.getDataCarrierType());
        assertEquals("EAN-13 / UPC-A / UPC-E / EAN-8",
                ean8.getDataCarrierType().getDisplayName());
    }
}
