package tools.pantheum.gaia;

import tools.pantheum.gaia.result.BuildResult;
import tools.pantheum.gaia.config.BuilderDigitalLinkConfig;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaBuilderException;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.digitallink.DLPathRules;
import tools.pantheum.gaia.gs1.util.PcEncUtils;
import tools.pantheum.gaia.result.ParseResult;
import tools.pantheum.gaia.result.ProcessingTiming;
import tools.pantheum.gaia.result.Started;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fluent builder for well-formed GS1 output — the inverse of {@link GaiaParser}.
 *
 * <p>Collect Application Identifier / value pairs, then render them as either a
 * GS1 element string or a GS1 Digital Link URI:
 *
 * <pre>{@code
 * String es = GaiaBuilder.create()
 *         .ai("01", "09506000134352")
 *         .ai("10", "LOT-ABC")
 *         .ai("17", "271231")
 *         .buildElementString();         // 0109506000134352…10LOT-ABC<GS>17271231
 *
 * String dl = GaiaBuilder.create()
 *         .ai("01", "09506000134352")
 *         .ai("10", "LOT-ABC")
 *         .ai("17", "271231")
 *         .buildDigitalLinkUri();        // https://id.gs1.org/01/09506000134352/10/LOT-ABC?17=271231
 * }</pre>
 *
 * <p>Both build methods validate via the same engine that {@link GaiaParser} uses:
 * each value is checked against the AI's format and check digit, and structural
 * rules (required/excluded AI pairings; for Digital Link, the primary key and
 * key-qualifier sequence) are enforced. A {@link GaiaBuilderException} is thrown
 * if the result would not be well-formed; the {@code tryBuild*} variants return a
 * {@link BuildResult} instead of throwing.
 *
 * <p>Values must be complete (including any check digit); see
 * {@link tools.pantheum.gaia.gs1.util.GS1Utils#calculateCheckDigit(String)} to
 * compute one. Not thread-safe; create one builder per output.
 */
public final class GaiaBuilder {

    private final List<String[]> pairs = new ArrayList<>();   // each entry: { ai, value }
    private final GaiaParser parser = new GaiaParser();
    private final AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
    private GaiaConstants.Language language = GaiaConstants.Language.ENGLISH;

    private GaiaBuilder() {}

    /** Starts a new, empty builder. */
    public static GaiaBuilder create() { return new GaiaBuilder(); }

    /**
     * Sets the language for build error messages — both the content-validation errors carried
     * by {@link GaiaBuilderException#getErrors()} and {@link BuildResult#getErrors()} when a
     * value fails its format or check-digit rule, and the Digital Link structural failures
     * (no primary key, more than one primary key, banned AI, invalid key-qualifier sequence).
     * Defaults to {@link GaiaConstants.Language#ENGLISH}.
     *
     * @param language the error-message language; {@code null} is ignored
     */
    public GaiaBuilder language(GaiaConstants.Language language) {
        if (language != null) {
            this.language = language;
        }
        return this;
    }

    /**
     * Appends an Application Identifier and its complete value.
     *
     * <p>The AI must be a recognised GS1 Application Identifier. This is checked
     * eagerly because the builder concatenates AI and value before validating: an
     * unrecognised or over-long AI (e.g. {@code "99999"}) would otherwise be
     * silently re-tokenised by the parser into a <em>different</em> AI rather than
     * rejected. The value itself is validated later, at build time.
     *
     * @param ai    the AI code, e.g. {@code "01"} or {@code GS1Constants_AICodes.AI_01_GTIN}
     * @param value the complete data value (including any check digit)
     * @throws IllegalArgumentException if {@code ai} or {@code value} is {@code null},
     *         or {@code ai} is not a recognised GS1 Application Identifier
     */
    public GaiaBuilder ai(String ai, String value) {
        if (ai == null || value == null) {
            throw new IllegalArgumentException("AI and value must not be null");
        }
        if (registry.find(ai).isEmpty()) {
            throw new IllegalArgumentException("Unrecognised Application Identifier: '" + ai + "'");
        }
        pairs.add(new String[]{ai, value});
        return this;
    }

    /**
     * Renders the collected AIs as a GS1 element string (FNC1 group separators
     * inserted after each AI that requires a separator, except the last).
     *
     * @throws GaiaBuilderException if the AIs do not form a well-formed element string
     */
    public String buildElementString() {
        return validatedObject().toElementString();
    }

    /** Renders the collected AIs as a canonical GS1 Digital Link URI ({@code https://id.gs1.org}). */
    public String buildDigitalLinkUri() {
        return buildDigitalLinkUri(BuilderDigitalLinkConfig.canonical());
    }

    /**
     * Non-throwing counterpart of {@link #buildElementString()}: on success the
     * {@link BuildResult} carries the element string; on failure it carries the
     * validation errors instead of raising {@link GaiaBuilderException}.
     */
    public BuildResult tryBuildElementString() {
        Started timer = ProcessingTiming.start();
        try {
            return BuildResult.success(buildElementString()).withTiming(timer.stop());
        } catch (GaiaBuilderException ex) {
            return BuildResult.failure(ex.getMessage(), ex.getErrors()).withTiming(timer.stop());
        }
    }

    /** Non-throwing counterpart of {@link #buildDigitalLinkUri()}. */
    public BuildResult tryBuildDigitalLinkUri() {
        return tryBuildDigitalLinkUri(BuilderDigitalLinkConfig.canonical());
    }

    /** Non-throwing counterpart of {@link #buildDigitalLinkUri(BuilderDigitalLinkConfig)}. */
    public BuildResult tryBuildDigitalLinkUri(BuilderDigitalLinkConfig config) {
        Started timer = ProcessingTiming.start();
        try {
            return BuildResult.success(buildDigitalLinkUri(config)).withTiming(timer.stop());
        } catch (GaiaBuilderException ex) {
            return BuildResult.failure(ex.getMessage(), ex.getErrors()).withTiming(timer.stop());
        }
    }

    /**
     * Renders the collected AIs as a GS1 Digital Link URI using {@code config}.
     *
     * @throws GaiaBuilderException if the AIs are not a well-formed element string,
     *         or cannot form a valid Digital Link (no primary key, more than one
     *         primary key, a banned AI, or an invalid key-qualifier sequence)
     */
    public String buildDigitalLinkUri(BuilderDigitalLinkConfig config) {
        GS1AIObject object = validatedObject();
        List<GS1AIObjectElement> elements = object.getAis();
        assignDigitalLinkRoles(elements);
        String uri = emit(elements, config);
        verifyDigitalLinkUri(uri);
        return uri;
    }

    // -------------------------------------------------------------------------

    /**
     * Defensive round-trip: re-parses the emitted URI to confirm it is a valid GS1
     * Digital Link. Content and structure are already validated before {@code emit};
     * this guards the string-assembly and percent-encoding step itself, so a bug
     * there surfaces here rather than in a consumer. Should never fire for valid input.
     */
    private void verifyDigitalLinkUri(String uri) {
        ParseResult resp = parser.parse(uri,
                ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).language(language).build());
        boolean ok = resp.isValid()
                && resp.getAiObject() != null
                && resp.getAiObject().hasDigitalLink();
        if (!ok) {
            List<GaiaError> errors = resp.getAiObject() != null
                    ? resp.getAiObject().getAllErrors() : resp.getErrors();
            throw new GaiaBuilderException(
                    "Emitted Digital Link URI did not round-trip as a valid Digital Link: " + uri, errors);
        }
    }

    /** Assembles the candidate element string and validates it through the parser. */
    private GS1AIObject validatedObject() {
        if (pairs.isEmpty()) {
            throw new GaiaBuilderException("No AIs supplied", List.of());
        }
        ParseResult resp = parser.parse(assembleElementString(),
                ParseConfig.builder().requestedParseMode(GaiaConstants.ParseMode.CONTENT).language(language).build());
        if (!resp.isValid() || resp.getAiObject() == null) {
            List<GaiaError> errors = resp.getAiObject() != null
                    ? resp.getAiObject().getAllErrors() : resp.getErrors();
            throw new GaiaBuilderException(
                    "Cannot build a well-formed element string: " + errors.size() + " validation error(s)", errors);
        }
        return resp.getAiObject();
    }

    /** Concatenates AI+value pairs, inserting an FNC1 after each AI that requires a separator and is not last. */
    private String assembleElementString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pairs.size(); i++) {
            String ai = pairs.get(i)[0];
            sb.append(ai).append(pairs.get(i)[1]);
            boolean last = (i == pairs.size() - 1);
            // Mirror DLSyntaxParser's reconstruction: separator required unless the AI has a
            // predefined data length; an unknown AI is assumed variable-length (separator on).
            boolean separatorRequired = registry.find(ai).map(AiDefinition::isSeparatorRequired).orElse(true);
            if (!last && separatorRequired) {
                sb.append(GS1Constants.FNC1_GS);
            }
        }
        return sb.toString();
    }

    /** Determines and assigns the Digital Link role of each element, or throws if the set is not DL-valid. */
    private void assignDigitalLinkRoles(List<GS1AIObjectElement> elements) {
        List<GS1AIObjectElement> primaries = elements.stream()
                .filter(e -> DLPathRules.isPrimaryKey(e.getAi()))
                .collect(Collectors.toList());
        if (primaries.isEmpty()) {
            throw dlError("GE-L013", Map.of());
        }
        if (primaries.size() > 1) {
            String keys = primaries.stream().map(GS1AIObjectElement::getAi).collect(Collectors.joining(", "));
            throw dlError("GE-L014", Map.of("keys", keys));
        }
        String primaryKey = primaries.get(0).getAi();

        for (GS1AIObjectElement e : elements) {
            if (DLPathRules.isBanned(e.getAi())) {
                throw dlError("GE-L008", Map.of("ai", e.getAi()));
            }
        }

        // Validate the qualifier set order-insensitively — the builder reorders them
        // into the canonical §4.9 sequence on emit, so the order they were supplied in
        // does not matter (only that the set is admissible for this primary key).
        List<String> qualifierAis = elements.stream()
                .map(GS1AIObjectElement::getAi)
                .filter(ai -> !ai.equals(primaryKey) && DLPathRules.isKeyQualifier(ai))
                .collect(Collectors.toList());
        if (DLPathRules.orderQualifiers(primaryKey, qualifierAis) == null) {
            throw dlError("GE-L012", Map.of("primaryKey", primaryKey));
        }

        for (GS1AIObjectElement e : elements) {
            if (e.getAi().equals(primaryKey)) {
                e.setDigitalLinkAIType(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY);
            } else if (DLPathRules.isKeyQualifier(e.getAi())) {
                e.setDigitalLinkAIType(GS1Constants.DigitalLinkAIType.KEY_QUALIFIER);
            } else {
                e.setDigitalLinkAIType(GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE);
            }
        }
    }

    /**
     * Builds a {@link GaiaBuilderException} for a Digital Link structural failure, with a
     * {@link GaiaError} whose message is localized to the builder's configured language.
     */
    private GaiaBuilderException dlError(String code, Map<String, String> params) {
        GaiaError err = ErrorRegistry.INSTANCE.create(code, null, 0, params, language);
        return new GaiaBuilderException(err.getMessage(), List.of(err));
    }

    /** Renders the role-assigned elements as a Digital Link URI under {@code config}. */
    private String emit(List<GS1AIObjectElement> elements, BuilderDigitalLinkConfig config) {
        GS1AIObjectElement primary = null;
        List<GS1AIObjectElement> qualifiers = new ArrayList<>();
        List<GS1AIObjectElement> dataAttributes = new ArrayList<>();
        for (GS1AIObjectElement e : elements) {
            switch (e.getDigitalLinkAIType()) {
                case PRIMARY_IDENTIFICATION_KEY: primary = e; break;
                case KEY_QUALIFIER:              qualifiers.add(e); break;
                default:                         dataAttributes.add(e);
            }
        }

        // Key qualifiers must appear in the canonical §4.9 order defined by the AI
        // definitions, not the order they were supplied to the builder.
        List<String> qualifierOrder = DLPathRules.orderQualifiers(primary.getAi(),
                qualifiers.stream().map(GS1AIObjectElement::getAi).collect(Collectors.toList()));
        if (qualifierOrder != null) {
            qualifiers.sort(Comparator.comparingInt(q -> qualifierOrder.indexOf(q.getAi())));
        }

        StringBuilder path = new StringBuilder();
        path.append('/').append(primary.getAi()).append('/').append(PcEncUtils.encode(primary.getValue()));
        for (GS1AIObjectElement q : qualifiers) {
            path.append('/').append(q.getAi()).append('/').append(PcEncUtils.encode(q.getValue()));
        }

        // AI data attributes first (sorted lexically by AI key, §4.12), then config extras.
        dataAttributes.sort(Comparator.comparing(GS1AIObjectElement::getAi));
        StringBuilder query = new StringBuilder();
        for (GS1AIObjectElement d : dataAttributes) {
            appendQuery(query, d.getAi(), PcEncUtils.encode(d.getValue()));
        }
        for (Map.Entry<String, String> p : config.getExtraQueryParams()) {
            appendQuery(query, PcEncUtils.encode(p.getKey()), PcEncUtils.encode(p.getValue()));
        }

        StringBuilder uri = new StringBuilder()
                .append(config.getScheme()).append("://").append(config.getDomain())
                .append(config.getPathPrefix()).append(path).append(query);
        if (config.getFragment() != null && !config.getFragment().isEmpty()) {
            uri.append('#').append(PcEncUtils.encode(config.getFragment()));
        }
        return uri.toString();
    }

    private static void appendQuery(StringBuilder query, String key, String value) {
        query.append(query.length() == 0 ? '?' : '&').append(key).append('=').append(value);
    }
}
