package tools.pantheum.gaia.result;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.GaiaVersion;
import tools.pantheum.gaia.correlation.CorrelationInfo;
import tools.pantheum.gaia.datacarrier.DataCarrierParser;
import tools.pantheum.gaia.datacarrier.registry.DataCarrierEntry;
import tools.pantheum.gaia.datacarrier.registry.EciEntry;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.modifier.ModifierInfo;
import java.util.Collections;
import java.util.List;

/**
 * The top-level result returned by {@link tools.pantheum.gaia.GaiaParser}.
 *
 * <p>A response combines:
 * <ul>
 *   <li>The original input {@link #getPayload()} (after the correlation prefix, if any, has been stripped).</li>
 *   <li>The parsed {@link tools.pantheum.gaia.gs1.model.GS1AIObject} carrying all resolved
 *       AI elements, per-element errors, and per-element interpretations.</li>
 *   <li>Optional data-carrier metadata ({@link #getDataCarrier()}, {@link #getEci()})
 *       populated when the input starts with an AIM Code ID prefix.</li>
 *   <li>Optional correlation metadata ({@link #getCorrelationInfo()}) populated when
 *       the input started with an 8-digit correlation ID followed by {@code ~}.</li>
 *   <li>The requested and achieved parse modes ({@link #getRequestedParseMode()},
 *       {@link #getAchievedParseMode()}, {@link #isParseComplete()}) — the configured
 *       pipeline depth and the deepest depth actually reached.</li>
 * </ul>
 *
 * <p>Convenience accessors ({@link #isValid()}, {@link #getErrors()},
 * {@link #getWarnings()}, {@link #getIssues()}) aggregate errors across
 * both object-level and element-level slots so callers do not need to
 * navigate the object graph.
 */
public class ParseResult {

    private final String                payload;
    private final GS1AIObject           aiObject;
    private final DataCarrierEntry      dataCarrier;
    private final EciEntry              eci;
    private final CorrelationInfo       correlationInfo;
    private final GaiaConstants.ParseMode requestedParseMode;
    private final GaiaConstants.ParseMode achievedParseMode;
    private final ProcessingTiming      timing;
    private final ModifierInfo          modifierInfo;
    private final String                version;

    /**
     * Constructs a response for a plain (non-data-carrier) parse — no dataCarrier, ECI,
     * Digital Link, or correlation metadata.
     *
     * @param payload  the input string after correlation prefix stripping
     * @param aiObject the parsed AI object; must not be {@code null}
     */
    public ParseResult(String payload, GS1AIObject aiObject) {
        this(payload, aiObject, null, null, null);
    }

    /**
     * Constructs a response for a data-carrier parse, carrying the identified dataCarrier
     * and any resolved ECI entry.
     *
     * @param payload     the input string after correlation prefix stripping
     * @param aiObject    the parsed AI object; {@code null} when the parse mode is
     *                    {@code DATA_CARRIER} and AI parsing was not performed
     * @param dataCarrier the resolved data carrier, or {@code null} if not identified
     * @param eci         the resolved ECI entry, or {@code null} if no ECI indicator was present
     */
    public ParseResult(String payload, GS1AIObject aiObject, DataCarrierEntry dataCarrier, EciEntry eci) {
        this(payload, aiObject, dataCarrier, eci, null);
    }

    /**
     * Canonical constructor.
     *
     * @param payload         the input string after correlation prefix stripping
     * @param aiObject        the parsed AI object; may be {@code null}
     * @param dataCarrier     the resolved data carrier, or {@code null}
     * @param eci             the resolved ECI entry, or {@code null}
     * @param correlationInfo the extracted correlation ID, or {@code null} if none was present
     */
    public ParseResult(String payload, GS1AIObject aiObject, DataCarrierEntry dataCarrier,
                         EciEntry eci, CorrelationInfo correlationInfo) {
        this(payload, aiObject, dataCarrier, eci, correlationInfo, null, null, null, null);
    }

    /**
     * Full constructor, also carrying the requested/achieved parse modes, processing timing,
     * and input-modifier metadata.
     */
    private ParseResult(String payload, GS1AIObject aiObject, DataCarrierEntry dataCarrier,
                        EciEntry eci, CorrelationInfo correlationInfo,
                        GaiaConstants.ParseMode requestedParseMode, GaiaConstants.ParseMode achievedParseMode,
                        ProcessingTiming timing, ModifierInfo modifierInfo) {
        this.payload            = payload;
        this.aiObject           = aiObject;
        this.dataCarrier        = dataCarrier;
        this.eci                = eci;
        this.correlationInfo    = correlationInfo;
        this.requestedParseMode = requestedParseMode;
        this.achievedParseMode  = achievedParseMode;
        this.timing             = timing;
        this.modifierInfo       = modifierInfo;
        this.version            = GaiaVersion.VERSION;
    }

    /**
     * Returns a copy of this response with the given {@link CorrelationInfo} attached.
     * All other fields are unchanged.
     *
     * @param correlationInfo the correlation ID to attach; may be {@code null} to clear it
     * @return a new {@link ParseResult}; never {@code null}
     */
    public ParseResult withCorrelationInfo(CorrelationInfo correlationInfo) {
        return new ParseResult(payload, aiObject, dataCarrier, eci, correlationInfo,
                requestedParseMode, achievedParseMode, timing, modifierInfo);
    }

    /**
     * Returns a copy of this response recording the requested and achieved parse modes.
     * All other fields are unchanged.
     *
     * @param requested the {@link tools.pantheum.gaia.config.ParseConfig#getRequestedParseMode() requested} mode
     * @param achieved  the deepest mode the pipeline actually reached
     * @return a new {@link ParseResult}; never {@code null}
     */
    public ParseResult withParseModes(GaiaConstants.ParseMode requested, GaiaConstants.ParseMode achieved) {
        return new ParseResult(payload, aiObject, dataCarrier, eci, correlationInfo, requested, achieved,
                timing, modifierInfo);
    }

    /**
     * Returns a copy of this response with the given {@link ProcessingTiming} attached.
     * All other fields are unchanged.
     *
     * @param timing the processing timing to attach; may be {@code null} to clear it
     * @return a new {@link ParseResult}; never {@code null}
     */
    public ParseResult withTiming(ProcessingTiming timing) {
        return new ParseResult(payload, aiObject, dataCarrier, eci, correlationInfo,
                requestedParseMode, achievedParseMode, timing, modifierInfo);
    }

    /**
     * Returns a copy of this response with the given {@link ModifierInfo} attached.
     * All other fields are unchanged.
     *
     * @param modifierInfo the input-modifier metadata to attach; may be {@code null} to clear it
     * @return a new {@link ParseResult}; never {@code null}
     */
    public ParseResult withModifierInfo(ModifierInfo modifierInfo) {
        return new ParseResult(payload, aiObject, dataCarrier, eci, correlationInfo,
                requestedParseMode, achievedParseMode, timing, modifierInfo);
    }

    /**
     * The library version that produced this response, e.g. {@code "1.0.0"}.
     *
     * @return the version.
     */
    public String getVersion() { return version; }

    /**
     * The processing timing (start, completion, elapsed) for the parse that produced this
     * response, or {@code null} if this result was not produced by
     * {@link tools.pantheum.gaia.GaiaParser}.
     *
     * @return the timing.
     */
    public ProcessingTiming getTiming() { return timing; }

    /**
     * The original input string passed to the parser. May be {@code null}.
     *
     * @return the payload.
     */
    public String getPayload() { return payload; }

    /**
     * Returns the payload with the AIM Code ID prefix and ECI indicator stripped,
     * leaving only the raw AI element string content.
     *
     * <p>If no data carrier was identified the full payload is returned unchanged.
     * If a data carrier was identified, the three-character AIM Code ID is removed.
     * If an ECI indicator was also present, the seven-character indicator is removed
     * as well.
     *
     * <p>Returns {@code null} if {@link #getPayload()} is {@code null}.
     *
     * @return the payload content.
     */
    public String getPayloadContent() {
        if (payload == null) return null;
        int offset = 0;
        if (dataCarrier != null) {
            offset += DataCarrierParser.AIM_CODE_ID_LENGTH;
            if (eci != null) {
                offset += DataCarrierParser.ECI_INDICATOR_LENGTH;
            }
        }
        return offset == 0 ? payload : payload.substring(offset);
    }

    /**
     * All resolved AIs as a {@link GS1AIObject}, or {@code null} if the parse mode was
     * {@code DATA_CARRIER} and AI parsing was not performed.
     *
     * @return the AI object.
     */
    public GS1AIObject getAiObject() { return aiObject; }

    /**
     * All non-WARNING errors produced during parsing and validation, or an empty list if
     * {@link #getAiObject()} is {@code null}.
     *
     * <p>Advisory {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#WARNING} entries are excluded.
     * Use {@link #getWarnings()} to retrieve those, or {@link #getIssues()} for everything.
     *
     * @return the errors.
     */
    public List<GaiaError> getErrors() {
        return aiObject != null ? aiObject.getAllErrors() : Collections.emptyList();
    }

    /**
     * All {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#WARNING} advisories produced
     * during parsing, or an empty list if {@link #getAiObject()} is {@code null}.
     *
     * @return the warnings.
     */
    public List<GaiaError> getWarnings() {
        return aiObject != null ? aiObject.getAllWarnings() : Collections.emptyList();
    }

    /**
     * Returns {@code true} if there are any advisory warnings (does not affect validity).
     *
     * @return {@code true} if this element has warnings.
     */
    public boolean hasWarnings() {
        return !getWarnings().isEmpty();
    }

    /**
     * All issues regardless of level — errors and warnings combined.
     * Use this when you need the complete picture.
     *
     * @return the issues.
     */
    public List<GaiaError> getIssues() {
        return aiObject != null ? aiObject.getAllIssues() : Collections.emptyList();
    }

    /**
     * Returns {@code true} if there are no errors.
     * {@link tools.pantheum.gaia.GaiaConstants.ErrorLevel#WARNING} advisories do not affect validity.
     * Always {@code true} when {@link #getAiObject()} is {@code null}.
     *
     * @return {@code true} if this element is valid.
     */
    public boolean isValid() {
        return aiObject == null || aiObject.isValid();
    }

    /**
     * Returns the {@link GS1Constants.GS1ContentType} of the parsed input.
     *
     * <p>Delegates to {@link GS1AIObject#getContentType()} when an AI object is present.
     * Returns:
     * <ul>
     *   <li>{@link GS1Constants.GS1ContentType#UNABLE_TO_DETERMINE_CONTENT} when {@link #getAiObject()} is
     *       {@code null} (e.g. {@code DATA_CARRIER} mode with no AI parsing performed); or</li>
     *   <li>{@link GS1Constants.GS1ContentType#DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL} when a data carrier was
     *       identified but is not GS1 AI capable and is not an EAN/UPC carrier requiring GTIN
     *       padding (e.g. a Code 39 {@code ]A0} carrier) — the carrier supports neither GS1 AI
     *       element strings nor Digital Link URIs, even though an error-carrying AI object is present.</li>
     * </ul>
     *
     * @return the content type.
     */
    public GS1Constants.GS1ContentType getContentType() {
        if (aiObject == null) return GS1Constants.GS1ContentType.UNABLE_TO_DETERMINE_CONTENT;
        if (dataCarrier != null && !dataCarrier.isGs1AICapable() && !dataCarrier.isRequiresGtinPadding()) {
            return GS1Constants.GS1ContentType.DATA_CARRIER_DOES_NOT_SUPPORT_GS1_AI_DL;
        }
        return aiObject.getContentType();
    }

    /**
     * The parse mode that was <em>requested</em> — the
     * {@link tools.pantheum.gaia.config.ParseConfig#getRequestedParseMode() configured} pipeline depth.
     * {@code null} if this result was not produced by {@link tools.pantheum.gaia.GaiaParser}.
     *
     * @return the requested parse mode.
     */
    public GaiaConstants.ParseMode getRequestedParseMode() { return requestedParseMode; }

    /**
     * The parse mode that was actually <em>achieved</em> — the deepest pipeline stage the parse
     * reached. This is shallower than {@link #getRequestedParseMode()} when the pipeline stops
     * early: a syntax or structural error halts the parse after
     * {@link GaiaConstants.ParseMode#SYNTAX SYNTAX}, and a content error prevents
     * {@link GaiaConstants.ParseMode#INTERPRETATION INTERPRETATION} so the parse stops after
     * {@link GaiaConstants.ParseMode#CONTENT CONTENT}. Equal to the requested mode for a
     * {@link GaiaConstants.ParseMode#DATA_CARRIER DATA_CARRIER} parse. {@code null} if this
     * result was not produced by {@link tools.pantheum.gaia.GaiaParser}.
     *
     * @return the achieved parse mode.
     */
    public GaiaConstants.ParseMode getAchievedParseMode() { return achievedParseMode; }

    /**
     * Returns {@code true} if the parse reached the requested depth — i.e. the
     * {@link #getAchievedParseMode() achieved} mode equals the
     * {@link #getRequestedParseMode() requested} mode. Returns {@code false} when the pipeline
     * stopped early (and when either mode is unset). Independent of {@link #isValid()}: a parse
     * can be complete yet invalid (e.g. a {@code CONTENT} request whose value fails its check digit).
     *
     * @return {@code true} if this element is parse complete.
     */
    public boolean isParseComplete() {
        return requestedParseMode != null && requestedParseMode == achievedParseMode;
    }

    /**
     * The data carrier that was identified from the AIM Code ID prefix, or {@code null}
     * if the input was not prefixed with an AIM Code ID or the AIM Code ID was not recognised.
     *
     * @return the data carrier.
     */
    public DataCarrierEntry getDataCarrier() { return dataCarrier; }

    /**
     * Returns {@code true} if a data carrier was identified.
     *
     * @return {@code true} if this element has data carrier.
     */
    public boolean hasDataCarrier() { return dataCarrier != null; }

    /**
     * The ECI entry that was detected and stripped from the payload, or {@code null} if
     * no ECI indicator was present or the carrier is not ECI-capable.
     *
     * @return the ECI.
     */
    public EciEntry getEci() { return eci; }

    /**
     * Returns {@code true} if an ECI indicator was detected in the payload.
     *
     * @return {@code true} if this element has ECI.
     */
    public boolean hasEci() { return eci != null; }

    /**
     * The correlation ID that was stripped from the input before GS1 parsing, or
     * {@code null} if the input did not carry a correlation prefix.
     *
     * @return the correlation info.
     */
    public CorrelationInfo getCorrelationInfo() { return correlationInfo; }

    /**
     * Returns {@code true} if a correlation prefix was present in the original input.
     *
     * @return {@code true} if this element has correlation id.
     */
    public boolean hasCorrelationId() { return correlationInfo != null; }

    /**
     * What the configured {@link tools.pantheum.gaia.modifier.ModifierInterface} chain did
     * to the raw input before parsing, or {@code null} if no modifiers were configured.
     *
     * <p>When modifiers ran, {@link #getPayload()} reflects the <em>modified</em> input;
     * {@link ModifierInfo#getOriginalInput()} preserves the string the caller passed in.
     *
     * @see tools.pantheum.gaia.config.ParseConfig#getModifiers()
     *
     * @return the modifier info.
     */
    public ModifierInfo getModifierInfo() { return modifierInfo; }

    /**
     * Returns {@code true} if an input modifier actually changed the input.
     * {@code false} when no modifiers were configured, or when every configured modifier
     * returned the input unchanged.
     *
     * @return {@code true} if this element is input modified.
     */
    public boolean isInputModified() { return modifierInfo != null && modifierInfo.isModified(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParseResult{\n");
        sb.append("  version=").append(version).append('\n');
        sb.append("  valid=").append(isValid()).append('\n');
        if (correlationInfo != null) {
            sb.append("  correlationId=\"").append(correlationInfo.getId()).append("\"\n");
        }
        if (payload != null) {
            sb.append("  payload=\"").append(payload).append("\"\n");
        }
        if (isInputModified()) {
            sb.append("  originalInput=\"").append(modifierInfo.getOriginalInput()).append("\"\n");
            sb.append("  modifiedBy=").append(modifierInfo.getAppliedModifiers()).append('\n');
        }
        if (aiObject != null && aiObject.hasDigitalLink()) {
            sb.append("  digitalLink=\"").append(aiObject.getDigitalLinkInfo().getUri()).append("\"\n");
        }
        if (dataCarrier != null) {
            sb.append("  dataCarrier=\"").append(dataCarrier.getName())
              .append("\" (").append(dataCarrier.getAimCodeId()).append(")\n");
        }
        if (eci != null) {
            sb.append("  eci=").append(eci.getNumber())
              .append(" (").append(eci.getCharset()).append(")\n");
        }
        if (aiObject != null) {
            sb.append("  hri=\"").append(aiObject.toHriString()).append("\"\n");
            sb.append("  elements(").append(aiObject.size()).append("):\n");
            for (GS1AIObjectElement e : aiObject.getAis()) {
                sb.append("    (").append(e.getAi()).append(") ")
                  .append(e.getTitle()).append(" = \"").append(e.getValue()).append("\"\n");
            }
            List<GaiaError> allErrors = aiObject.getAllErrors();
            sb.append("  errors(").append(allErrors.size()).append("):\n");
            for (GaiaError err : allErrors) {
                sb.append("    [").append(err.getId()).append("] ")
                  .append(err.getLevel()).append(": ").append(err.getMessage()).append('\n');
            }
            List<GaiaError> allWarnings = aiObject.getAllWarnings();
            if (!allWarnings.isEmpty()) {
                sb.append("  warnings(").append(allWarnings.size()).append("):\n");
                for (GaiaError w : allWarnings) {
                    sb.append("    [").append(w.getId()).append("] ")
                      .append(w.getMessage()).append('\n');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
