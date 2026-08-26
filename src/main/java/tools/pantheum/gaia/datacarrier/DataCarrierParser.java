package tools.pantheum.gaia.datacarrier;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierRegistry;
import tools.pantheum.gaia.datacarrier.registry.EciEntry;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.constants.GS1Constants_AICodes;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.gs1.GS1Parser;
import tools.pantheum.gaia.result.ParseResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Parses a GS1 element string that is prefixed with an AIM Code ID.
 *
 * <h2>Processing pipeline</h2>
 * <ol>
 *   <li>Extract the three-character AIM Code ID prefix
 *       ({@code ']'} + code character + modifier digit).</li>
 *   <li>Look up the data carrier in {@link DataCarrierRegistry}. If not found,
 *       return a {@code GE-D001} error immediately.</li>
 *   <li>Check {@link DataCarrierEntry#isGs1AICapable()} or
 *       {@link DataCarrierEntry#isRequiresGtinPadding()}. If neither is
 *       {@code true}, return a {@code GE-D002} error immediately without
 *       entering the AI parsing pipeline.</li>
 *   <li>If the carrier is {@link DataCarrierEntry#isEciCapable()}, inspect the
 *       start of the payload for an ECI indicator ({@code '\'} followed by six
 *       decimal digits). If found, the indicator is resolved via
 *       {@link DataCarrierRegistry#eciForIndicator(String)} and stripped from
 *       the payload before AI parsing.</li>
 *   <li>If {@link DataCarrierEntry#isRequiresGtinPadding()} is {@code true}
 *       (EAN/UPC symbologies), the raw numeric payload is left-padded to 14
 *       digits and prefixed with AI {@code 01} to form a GTIN element string.</li>
 *   <li>If the parse mode is {@link tools.pantheum.gaia.GaiaConstants.ParseMode#DATA_CARRIER
 *       DATA_CARRIER}, carrier-level checks are complete and the response is
 *       returned without entering the AI parsing pipeline.</li>
 *   <li>Delegate the (ECI-stripped, optionally GTIN-prefixed) payload to
 *       {@link GS1Parser}.</li>
 * </ol>
 *
 * <h2>Thread safety</h2>
 * {@code DataCarrierParser} is thread-safe once constructed.
 */
public class DataCarrierParser {

    /** Length of the AIM Code ID prefix: {@code ']'} + code char + modifier digit. */
    public static final int AIM_CODE_ID_LENGTH = 3;

    /** AI prefix prepended to EAN/UPC payloads to form a GTIN element string. */
    private static final String GTIN_AI_PREFIX = GS1Constants_AICodes.AI_01_GTIN;

    /** First character of every AIM Code ID. */
    private static final char AIM_CODE_ID_LEAD = ']';

    /** First character of an ECI indicator sequence. */
    private static final char ECI_INDICATOR_LEAD = '\\';

    /**
     * Length of an ECI indicator: a backslash ({@code '\'}) followed by
     * exactly six decimal digits, e.g. {@code \000026}.
     */
    public static final int ECI_INDICATOR_LENGTH = 7;

    private final GS1Parser  gs1Parser;
    public DataCarrierParser() {
        this.gs1Parser = new GS1Parser();
    }

    /**
     * Parses a data-carrier-prefixed input string using the supplied {@link ParseConfig}.
     *
     * <p>The caller is responsible for confirming that {@code input} begins with
     * a valid AIM Code ID (use {@link #startsWithDataCarrier(String)}).
     * The pipeline depth is taken from {@link ParseConfig#getRequestedParseMode()}.
     *
     * @param input  raw scanner output including the AIM Code ID prefix;
     *               must not be {@code null} and must be at least three characters long
     * @param config parse configuration (mode, date formatting, language); use
     *               {@link ParseConfig#defaultConfig()} for standard behaviour
     * @return a {@link ParseResult} containing the resolved AIs and/or errors,
     *         the identified {@link DataCarrierEntry}, and any resolved
     *         {@link EciEntry}; never {@code null}
     * @throws IllegalArgumentException if {@code input} is {@code null}
     */
    public ParseResult parse(String input, ParseConfig config) {
        if (input == null) throw new IllegalArgumentException("input must not be null");

        String aimCodeId = input.substring(0, AIM_CODE_ID_LENGTH);
        String payload   = input.substring(AIM_CODE_ID_LENGTH);

        Optional<DataCarrierEntry> entryOpt = DataCarrierRegistry.forAimCodeId(aimCodeId);

        if (!entryOpt.isPresent()) {
            GaiaError error = ErrorRegistry.INSTANCE.create("GE-D001", null, 0,
                    Map.of("aimCodeId", aimCodeId), config.getLanguage());
            return errorResponse(input, error, null, null);
        }

        DataCarrierEntry entry = entryOpt.get();

        if (!entry.isGs1AICapable() && !entry.isRequiresGtinPadding()) {
            GaiaError error = ErrorRegistry.INSTANCE.create("GE-D002", null, 0,
                    Map.of("aimCodeId", aimCodeId,
                           "name",      entry.getName() != null ? entry.getName() : aimCodeId),
                    config.getLanguage());
            return errorResponse(input, error, entry, null);
        }

        // --- ECI detection and stripping ---
        EciEntry eci = null;
        if (entry.isEciCapable()) {
            eci = detectEci(payload);
            if (eci != null) {
                payload = payload.substring(ECI_INDICATOR_LENGTH);
            }
        }

        // --- GTIN padding (EAN-13, UPC-A, UPC-E, EAN-8) ---
        // The raw payload contains only the numeric barcode value with no AI prefix.
        // Left-pad to 14 digits and prepend AI (01) to form a valid GS1 element string.
        if (entry.isRequiresGtinPadding()) {
            payload = buildGtinPayload(payload);
        }

        // DATA_CARRIER mode: carrier checks are complete — do not enter the AI parsing pipeline.
        if (config.getRequestedParseMode() == GaiaConstants.ParseMode.DATA_CARRIER) {
            return new ParseResult(input, null, entry, eci);
        }

        // Delegate (ECI-stripped, optionally GTIN-prefixed) payload to the GS1 AI parser.
        GS1AIObject aiObject = gs1Parser.parse(payload, config.toGs1ParseMode(), config);
        return new ParseResult(input, aiObject, entry, eci);
    }

    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if {@code input} begins with an AIM Code ID:
     * a {@code ']'} character, followed by an ASCII letter, followed by an ASCII digit.
     *
     * @param input the raw scanner output; may be {@code null}
     */
    public static boolean startsWithDataCarrier(String input) {
        return input != null
                && input.length() >= AIM_CODE_ID_LENGTH
                && input.charAt(0) == AIM_CODE_ID_LEAD
                && Character.isLetter(input.charAt(1))
                && Character.isDigit(input.charAt(2));
    }

    // -------------------------------------------------------------------------

    /**
     * Checks whether {@code payload} begins with an ECI indicator
     * ({@code '\'} followed by exactly six decimal digits) and, if so,
     * looks it up in the registry.
     *
     * @param payload the post-AIM-Code-ID portion of the scanner output
     * @return the resolved {@link EciEntry}, or {@code null} if no ECI indicator
     *         is present or the indicator is not in the registry
     */
    private static EciEntry detectEci(String payload) {
        if (payload.length() < ECI_INDICATOR_LENGTH) {
            return null;
        }
        if (payload.charAt(0) != ECI_INDICATOR_LEAD) {
            return null;
        }
        for (int i = 1; i < ECI_INDICATOR_LENGTH; i++) {
            if (!Character.isDigit(payload.charAt(i))) {
                return null;
            }
        }
        String indicator = payload.substring(0, ECI_INDICATOR_LENGTH);
        return DataCarrierRegistry.eciForIndicator(indicator).orElse(null);
    }

    /**
     * Constructs a GS1 AI element string from a raw EAN/UPC numeric payload.
     *
     * <p>The payload is left-padded with zeros to 14 digits, then prefixed with
     * AI {@code 01}, producing a string of the form {@code 01NNNNNNNNNNNNNN} that
     * the GS1 AI parser can process as a GTIN element string.
     *
     * @param digits the raw numeric barcode payload (7–13 digits for EAN/UPC)
     * @return a GS1 element string beginning with {@code "01"} followed by
     *         {@code digits} left-padded to 14 characters (16 characters total
     *         for standard 7–13 digit EAN/UPC payloads)
     */
    private static String buildGtinPayload(String digits) {
        int padLen = 14 - digits.length();
        String padded = padLen > 0 ? "0".repeat(padLen) + digits : digits;
        return GTIN_AI_PREFIX + padded;
    }

    private static ParseResult errorResponse(String input, GaiaError error, DataCarrierEntry entry, EciEntry eci) {
        GS1AIObject obj = new GS1AIObject(Collections.emptyList(), List.of(error));
        return new ParseResult(input, obj, entry, eci);
    }
}
