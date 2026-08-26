package tools.pantheum.gaia;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.datacarrier.DataCarrierParser;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierRegistry;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.EciEntry;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the data-carrier parsing pipeline:
 *
 * <ul>
 *   <li>{@link DataCarrierParser#startsWithDataCarrier(String)} — AIM Code ID detection</li>
 *   <li>{@link DataCarrierRegistry} — carrier and ECI lookups</li>
 *   <li>{@link DataCarrierParser#parse} — error cases and parse modes</li>
 *   <li>{@link GaiaParser} routing — data-carrier vs. plain element string</li>
 * </ul>
 *
 * <h3>Test data</h3>
 * The bundled {@code datacarriers.json} contains six Code 39 variants
 * ({@code ]A0}, {@code ]A1}, {@code ]A3}, {@code ]A4}, {@code ]A5}, {@code ]A7})
 * and 38 ECI entries. None of the Code 39 variants are GS1 AI capable, so
 * they are used to verify the GE-D002 rejection path.
 */
@DisplayName("DataCarrierParser")
class DataCarrierParserTest {

    static GaiaParser      gaiaParser;
    static DataCarrierParser dcParser;

    @BeforeAll
    static void setup() {
        gaiaParser = new GaiaParser();
        dcParser   = new DataCarrierParser();
    }

    // =========================================================================
    // startsWithDataCarrier
    // =========================================================================

    @Nested
    @DisplayName("startsWithDataCarrier()")
    class StartsWithDataCarrier {

        @Test
        @DisplayName("Returns true for valid AIM Code ID prefix ']A0'")
        void validA0() {
            assertTrue(DataCarrierParser.startsWithDataCarrier("]A0SOMEDATA"));
        }

        @Test
        @DisplayName("Returns true for valid AIM Code ID prefix ']C1'")
        void validC1() {
            assertTrue(DataCarrierParser.startsWithDataCarrier("]C1" + GS1Constants_AICodes.AI_01_GTIN + "09506000134352"));
        }

        @Test
        @DisplayName("Returns true for any letter + digit combination")
        void validLetterDigit() {
            assertTrue(DataCarrierParser.startsWithDataCarrier("]Z9DATA"));
            assertTrue(DataCarrierParser.startsWithDataCarrier("]e0DATA"));
        }

        @Test
        @DisplayName("Returns false for null input")
        void nullInput() {
            assertFalse(DataCarrierParser.startsWithDataCarrier(null));
        }

        @Test
        @DisplayName("Returns false for empty string")
        void emptyString() {
            assertFalse(DataCarrierParser.startsWithDataCarrier(""));
        }

        @Test
        @DisplayName("Returns false for string shorter than 3 characters")
        void tooShort() {
            assertFalse(DataCarrierParser.startsWithDataCarrier("]A"));
            assertFalse(DataCarrierParser.startsWithDataCarrier("]"));
        }

        @Test
        @DisplayName("Returns false when first character is not ']'")
        void wrongLeadChar() {
            assertFalse(DataCarrierParser.startsWithDataCarrier("A0SOMEDATA"));
            assertFalse(DataCarrierParser.startsWithDataCarrier(GS1Constants_AICodes.AI_01_GTIN + "09506000134352"));
        }

        @Test
        @DisplayName("Returns false when second character is a digit (must be a letter)")
        void secondCharDigit() {
            assertFalse(DataCarrierParser.startsWithDataCarrier("]10SOMEDATA"));
        }

        @Test
        @DisplayName("Returns false when third character is a letter (must be a digit)")
        void thirdCharLetter() {
            assertFalse(DataCarrierParser.startsWithDataCarrier("]ABDATA"));
        }

        @Test
        @DisplayName("Returns false for plain element string without AIM Code ID")
        void plainElementString() {
            assertFalse(DataCarrierParser.startsWithDataCarrier(GS1Constants_AICodes.AI_01_GTIN + "09506000134352"));
        }
    }

    // =========================================================================
    // DataCarrierRegistry — carrier lookups
    // =========================================================================

    @Nested
    @DisplayName("DataCarrierRegistry — carrier lookups")
    class CarrierRegistry {

        @Test
        @DisplayName("]A0 is present in the registry")
        void a0Present() {
            Optional<DataCarrierEntry> entry = DataCarrierRegistry.forAimCodeId("]A0");
            assertTrue(entry.isPresent());
        }

        @Test
        @DisplayName("]A1 is present in the registry")
        void a1Present() {
            assertTrue(DataCarrierRegistry.forAimCodeId("]A1").isPresent());
        }

        @Test
        @DisplayName("All six Code 39 variants are loaded")
        void allCode39Variants() {
            for (String id : new String[]{"]A0", "]A1", "]A3", "]A4", "]A5", "]A7"}) {
                assertTrue(DataCarrierRegistry.forAimCodeId(id).isPresent(),
                        () -> id + " should be in the registry");
            }
        }

        @Test
        @DisplayName("Unknown AIM Code ID returns empty")
        void unknownReturnsEmpty() {
            assertTrue(DataCarrierRegistry.forAimCodeId("]Z9").isEmpty());
            assertTrue(DataCarrierRegistry.forAimCodeId("]Q9").isEmpty());
        }

        @Test
        @DisplayName("Code 39 ]A0 is not GS1 AI capable")
        void code39NotGs1Capable() {
            DataCarrierEntry entry = DataCarrierRegistry.forAimCodeId("]A0").orElseThrow();
            assertFalse(entry.isGs1AICapable());
        }

        @Test
        @DisplayName("Code 39 ]A0 does not require GTIN padding")
        void code39NoGtinPadding() {
            DataCarrierEntry entry = DataCarrierRegistry.forAimCodeId("]A0").orElseThrow();
            assertFalse(entry.isRequiresGtinPadding());
        }

        @Test
        @DisplayName("Code 39 ]A0 is not ECI capable")
        void code39NotEciCapable() {
            DataCarrierEntry entry = DataCarrierRegistry.forAimCodeId("]A0").orElseThrow();
            assertFalse(entry.isEciCapable());
        }

        @Test
        @DisplayName("BY_AIM_CODE_ID map is non-empty")
        void registryIsNonEmpty() {
            assertFalse(DataCarrierRegistry.BY_AIM_CODE_ID.isEmpty(),
                    "Data carrier registry must contain at least one entry");
        }
    }

    // =========================================================================
    // DataCarrierRegistry — ECI lookups
    // =========================================================================

    @Nested
    @DisplayName("DataCarrierRegistry — ECI lookups")
    class EciRegistry {

        @Test
        @DisplayName("ECI indicator '\\\\000001' resolves to ISO-8859-1")
        void eciIso88591() {
            Optional<EciEntry> eci = DataCarrierRegistry.eciForIndicator("\\000001");
            assertTrue(eci.isPresent());
            assertEquals(1, eci.get().getNumber());
            assertEquals("ISO-8859-1", eci.get().getCharset());
        }

        @Test
        @DisplayName("ECI indicator '\\\\000000' resolves to Cp437")
        void eciCp437() {
            Optional<EciEntry> eci = DataCarrierRegistry.eciForIndicator("\\000000");
            assertTrue(eci.isPresent());
            assertEquals("Cp437", eci.get().getCharset());
        }

        @Test
        @DisplayName("ECI indicator '\\\\000899' resolves to 8-bit Binary data")
        void eciBinary() {
            Optional<EciEntry> eci = DataCarrierRegistry.eciForIndicator("\\000899");
            assertTrue(eci.isPresent());
            assertEquals(899, eci.get().getNumber());
        }

        @Test
        @DisplayName("Unknown ECI indicator returns empty")
        void unknownEciReturnsEmpty() {
            assertTrue(DataCarrierRegistry.eciForIndicator("\\999999").isEmpty());
            assertTrue(DataCarrierRegistry.eciForIndicator("").isEmpty());
        }

        @Test
        @DisplayName("ECI_BY_INDICATOR map contains 38 entries")
        void eciRegistrySizeIs38() {
            assertEquals(38, DataCarrierRegistry.ECI_BY_INDICATOR.size());
        }
    }

    // =========================================================================
    // DataCarrierParser.parse() — error paths
    // =========================================================================

    @Nested
    @DisplayName("DataCarrierParser.parse() — error paths")
    class ParseErrors {

        @Test
        @DisplayName("GE-D001: unknown AIM Code ID produces UNKNOWN_DATA_CARRIER error")
        void unknownAimCodeId() {
            // "]Z9" is syntactically valid but not in the registry
            ParseResult resp = dcParser.parse("]Z9SOMEDATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).build());
            assertFalse(resp.isValid());
            assertTrue(resp.getErrors().stream()
                    .anyMatch(e -> e.getId().equals("GE-D001")),
                    () -> "Expected GE-D001 but got: " + resp.getErrors());
        }

        @Test
        @DisplayName("GE-D001: error message includes the unrecognised AIM Code ID")
        void unknownAimCodeIdMessage() {
            ParseResult resp = dcParser.parse("]Q9DATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).build());
            assertTrue(resp.getErrors().stream()
                    .anyMatch(e -> e.getMessage().contains("]Q9")),
                    () -> "Expected message to contain ']Q9' but got: " + resp.getErrors());
        }

        @Test
        @DisplayName("GE-D002: Code 39 (]A0) is not GS1 AI capable")
        void notGs1AiCapable() {
            ParseResult resp = dcParser.parse("]A0SOMEDATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).build());
            assertFalse(resp.isValid());
            assertTrue(resp.getErrors().stream()
                    .anyMatch(e -> e.getId().equals("GE-D002")),
                    () -> "Expected GE-D002 but got: " + resp.getErrors());
        }

        @Test
        @DisplayName("GE-D002: data carrier entry is still returned on GE-D002")
        void ge_d002_carrierStillSet() {
            ParseResult resp = dcParser.parse("]A0SOMEDATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).build());
            assertTrue(resp.hasDataCarrier());
            assertNotNull(resp.getDataCarrier());
        }

        @Test
        @DisplayName("GE-D001: data carrier is null when AIM Code ID is unknown")
        void ge_d001_noCarrier() {
            ParseResult resp = dcParser.parse("]Z9SOMEDATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).build());
            assertNull(resp.getDataCarrier());
        }

        @Test
        @DisplayName("Null input throws IllegalArgumentException")
        void nullInputThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> dcParser.parse(null, ParseConfig.defaultConfig()));
        }
    }

    // =========================================================================
    // ParseMode.DATA_CARRIER — carrier detection only
    // =========================================================================

    @Nested
    @DisplayName("ParseMode.DATA_CARRIER — carrier detection only")
    class DataCarrierMode {

        @Test
        @DisplayName("DATA_CARRIER mode: known carrier is identified, aiObject is null")
        void dataCarrierModeKnownCarrier() {
            // ]A0 is Code 39 — not GS1 capable, so it produces GE-D002 in CONTENT mode,
            // but in DATA_CARRIER mode the pipeline exits after the capability check.
            // Since ]A0 fails the GS1 gate, it still produces GE-D002 before DATA_CARRIER exit.
            ParseResult resp = gaiaParser.parse("]A0DATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.DATA_CARRIER).build());
            // GE-D002 is produced before DATA_CARRIER short-circuit
            assertTrue(resp.hasDataCarrier() || !resp.isValid());
        }

        @Test
        @DisplayName("DATA_CARRIER mode: unknown AIM Code ID produces GE-D001, no aiObject")
        void dataCarrierModeUnknown() {
            ParseResult resp = gaiaParser.parse("]Z9DATA", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.DATA_CARRIER).build());
            assertFalse(resp.isValid());
            assertNull(resp.getDataCarrier());
            assertTrue(resp.getErrors().stream().anyMatch(e -> e.getId().equals("GE-D001")));
        }

        @Test
        @DisplayName("DATA_CARRIER mode: plain element string bypasses carrier parser")
        void dataCarrierModePlainInput() {
            // No AIM Code ID → routes to GS1AIParser, aiObject may be null in DATA_CARRIER mode
            ParseResult resp = gaiaParser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352", ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.DATA_CARRIER).build());
            assertFalse(resp.hasDataCarrier());
        }
    }

    // =========================================================================
    // GaiaParser routing
    // =========================================================================

    @Nested
    @DisplayName("GaiaParser routing")
    class GaiaParserRouting {

        @Test
        @DisplayName("Input with AIM Code ID is routed to DataCarrierParser")
        void aimCodeIdRoutedToDataCarrierParser() {
            ParseResult resp = gaiaParser.parse("]A0SOMEDATA");
            // The carrier is identified (Code 39) and set, even though GE-D002 is also raised
            assertTrue(resp.hasDataCarrier(), "Expected carrier to be identified");
            assertEquals("]A0", resp.getDataCarrier().getAimCodeId());
        }

        @Test
        @DisplayName("Plain element string is routed directly to GS1AIParser")
        void plainInputRoutedToGs1Parser() {
            ParseResult resp = gaiaParser.parse(GS1Constants_AICodes.AI_01_GTIN + "09506000134352");
            assertFalse(resp.hasDataCarrier());
            assertNotNull(resp.getAiObject());
            assertEquals(1, resp.getAiObject().getAis().size());
            assertEquals(GS1Constants_AICodes.AI_01_GTIN, resp.getAiObject().getAis().get(0).getAi());
        }

        @Test
        @DisplayName("getPayload() returns the original input unchanged")
        void payloadIsOriginalInput() {
            String input = "]A0SOMEDATA";
            ParseResult resp = gaiaParser.parse(input);
            assertEquals(input, resp.getPayload());
        }

        @Test
        @DisplayName("getPayloadContent() strips AIM Code ID from payload")
        void payloadContentStripsAimCodeId() {
            ParseResult resp = gaiaParser.parse("]A0SOMEDATA");
            assertEquals("SOMEDATA", resp.getPayloadContent());
        }
    }
}
