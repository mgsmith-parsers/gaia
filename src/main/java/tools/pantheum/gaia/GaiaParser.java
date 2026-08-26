package tools.pantheum.gaia;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.correlation.CorrelationIdParser;
import tools.pantheum.gaia.correlation.CorrelationParseResult;
import tools.pantheum.gaia.datacarrier.DataCarrierParser;
import tools.pantheum.gaia.gs1.GS1Parser;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.modifier.ModifierChain;
import tools.pantheum.gaia.modifier.ModifierInfo;
import tools.pantheum.gaia.result.ParseResult;
import tools.pantheum.gaia.result.ProcessingTiming;
import tools.pantheum.gaia.result.Started;

/**
 * Entry point for the Gaia GS1 element string parser.
 *
 * <h2>Routing</h2>
 * <ol>
 *   <li><b>Input modifiers</b> — if the {@link ParseConfig} carries any
 *       {@link tools.pantheum.gaia.modifier.ModifierInterface} implementations, they run
 *       first, in order, each rewriting the raw input for the next. Every stage below
 *       operates on the chain's output, and what the chain did is recorded on the result as
 *       {@link tools.pantheum.gaia.result.ParseResult#getModifierInfo() ModifierInfo}.
 *       No modifiers are configured by default.</li>
 *   <li><b>Correlation ID</b> — if the input begins with an 8-digit numeric prefix
 *       followed by {@code ~} (e.g. {@code 12345678~}), the prefix is stripped by
 *       {@link tools.pantheum.gaia.correlation.CorrelationIdParser} and the ID is
 *       attached to the resulting {@link tools.pantheum.gaia.result.ParseResult}.
 *       All subsequent stages operate on the stripped payload.</li>
 *   <li><b>Data carrier</b> — if the (stripped) input begins with an AIM Code ID prefix
 *       ({@code ']'} + ASCII letter + ASCII digit, e.g. {@code "]C1"}), it is
 *       forwarded to {@link DataCarrierParser}, which validates the carrier and —
 *       if it is GS1 AI capable — strips the prefix and delegates the payload to
 *       the underlying {@link GS1Parser}.</li>
 *   <li><b>GS1 content</b> — inputs without an AIM Code ID prefix are forwarded
 *       directly to {@link GS1Parser}, unless
 *       {@link GaiaConstants.ParseMode#DATA_CARRIER DATA_CARRIER} mode is active —
 *       in that case there is no carrier to validate and no AI payload to parse,
 *       so an empty response is returned immediately.</li>
 * </ol>
 */
public class GaiaParser {

    private final CorrelationIdParser correlationIdParser;
    private final GS1Parser           gs1Parser;
    private final DataCarrierParser   dataCarrierParser;

    public GaiaParser() {
        this.correlationIdParser = new CorrelationIdParser();
        this.gs1Parser           = new GS1Parser();
        this.dataCarrierParser   = new DataCarrierParser();
    }

    /**
     * Parses GS1 content using {@link ParseConfig#defaultConfig()}
     * ({@link GaiaConstants.ParseMode#INTERPRETATION INTERPRETATION} mode,
     * LITTLE-endian dates, English error messages).
     *
     * @param input optionally prefixed with an 8-digit correlation ID and {@code ~}
     *              (e.g. {@code 12345678~}), then a GS1 Digital Link URI
     *              ({@code http://} or {@code https://}), a raw element string
     *              (use ASCII 0x1D as the GS separator character), or either
     *              prefixed with an AIM Code ID (e.g. {@code "]C1"})
     * @return a {@link ParseResult}; never {@code null}
     */
    public ParseResult parse(String input) {
        return parse(input, ParseConfig.defaultConfig());
    }

    /**
     * Parses GS1 content with the given {@link ParseConfig}.
     *
     * <p>Any {@link tools.pantheum.gaia.modifier.ModifierInterface} configured on
     * {@code config} runs before anything else, rewriting {@code input} for every stage
     * that follows.</p>
     *
     * <p>The pipeline depth is taken from {@link ParseConfig#getRequestedParseMode()}.
     * If {@code input} starts with an AIM Code ID, the call is routed through
     * {@link DataCarrierParser}. If the mode is
     * {@link GaiaConstants.ParseMode#DATA_CARRIER DATA_CARRIER} and the input
     * has no AIM Code ID prefix, an empty response is returned immediately.
     * Otherwise the input is passed directly to {@link GS1Parser}, which further
     * routes {@code http://} / {@code https://} inputs to the Digital Link parser.</p>
     *
     * @param input  optionally prefixed with an 8-digit correlation ID and {@code ~}
     *               (e.g. {@code 12345678~}), then a GS1 Digital Link URI
     *               ({@code http://} or {@code https://}), a raw element string
     *               (use ASCII 0x1D as the GS separator character), or either
     *               prefixed with an AIM Code ID (e.g. {@code "]C1"})
     * @param config parse configuration (mode, date formatting, language);
     *               use {@link ParseConfig#defaultConfig()} for standard behaviour
     * @return a {@link ParseResult}; never {@code null}
     */
    public ParseResult parse(String input, ParseConfig config) {
        Started timer = ProcessingTiming.start();
        ModifierInfo modifierInfo = null;
        try {
            modifierInfo = ModifierChain.apply(input, config);
            String modifiedInput = modifierInfo != null ? modifierInfo.getModifiedInput() : input;

            CorrelationParseResult correlation = correlationIdParser.parse(modifiedInput);
            String payload = correlation.getStrippedPayload();

            ParseResult response;
            if (DataCarrierParser.startsWithDataCarrier(payload)) {
                response = dataCarrierParser.parse(payload, config);
            } else if (config.getRequestedParseMode() == GaiaConstants.ParseMode.DATA_CARRIER) {
                // DATA_CARRIER mode on a plain input — no carrier to validate, no AI to parse.
                response = new ParseResult(payload, null);
            } else {
                response = new ParseResult(payload,
                        gs1Parser.parse(payload, config.toGs1ParseMode(), config));
            }

            response = withParseModes(response, config);
            if (correlation.hasCorrelationId()) {
                response = response.withCorrelationInfo(correlation.getCorrelationInfo());
            }
            return response.withModifierInfo(modifierInfo).withTiming(timer.stop());
        } catch (RuntimeException e) {
            return withParseModes(new ParseResult(input, GS1Parser.internalError(e, config, "GaiaParser")), config)
                    .withModifierInfo(modifierInfo)
                    .withTiming(timer.stop());
        }
    }

    /**
     * Records the requested mode (from {@code config}) and the deepest mode the pipeline
     * actually reached on {@code response}.
     *
     * <ul>
     *   <li>No AI object — no AI parse was attempted (e.g. {@code DATA_CARRIER} mode):
     *       the requested depth was satisfied, so achieved equals requested.</li>
     *   <li>An AI object carrying a pipeline rung: that rung is the achieved mode.</li>
     *   <li>An AI object with no rung — a hand-built error object that never entered the
     *       pipeline (e.g. a rejected data carrier): the parse did not reach the requested
     *       depth, so achieved is the shallowest mode ({@code SYNTAX}).</li>
     * </ul>
     */
    private static ParseResult withParseModes(ParseResult response, ParseConfig config) {
        GaiaConstants.ParseMode requested = config.getRequestedParseMode();
        GS1AIObject aiObject = response.getAiObject();
        GaiaConstants.ParseMode achieved;
        if (aiObject == null) {
            achieved = requested;                                  // no AI parse attempted
        } else if (aiObject.getAchievedParseMode() != null) {
            achieved = toPublicMode(aiObject.getAchievedParseMode());
        } else {
            achieved = GaiaConstants.ParseMode.SYNTAX;             // error object, pipeline not run
        }
        return response.withParseModes(requested, achieved);
    }

    /** Maps the internal pipeline rung to the public parse mode. */
    private static GaiaConstants.ParseMode toPublicMode(GS1Constants.ParseMode rung) {
        switch (rung) {
            case SYNTAX:         return GaiaConstants.ParseMode.SYNTAX;
            case CONTENT:        return GaiaConstants.ParseMode.CONTENT;
            case INTERPRETATION: return GaiaConstants.ParseMode.INTERPRETATION;
            default: throw new IllegalStateException("Unhandled pipeline mode: " + rung);
        }
    }

}
