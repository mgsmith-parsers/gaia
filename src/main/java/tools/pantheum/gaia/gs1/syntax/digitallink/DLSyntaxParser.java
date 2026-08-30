package tools.pantheum.gaia.gs1.syntax.digitallink;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1DigitalLinkInfo;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;
import tools.pantheum.gaia.gs1.util.PcEncUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Extracts GS1 Application Identifier elements from an uncompressed GS1 Digital
 * Link URI into a list of {@link GS1AIObjectElement} objects.
 *
 * <p>Parsing rules (GS1 Digital Link Standard: URI Syntax, release 1.7.0):
 * <ul>
 *   <li>The input must be an absolute HTTP(S) URL ({@code GE-L001}–{@code GE-L003} otherwise).</li>
 *   <li>The path contains exactly one <em>primary identification key</em> as an
 *       {@code /ai/value} pair ({@link DLPathRules §4.3}); arbitrary custom path
 *       segments may precede it (§6.1.1).</li>
 *   <li>Subsequent {@code /ai/value} path pairs are <em>key qualifiers</em>,
 *       admitted and ordered per primary key ({@link DLPathRules §4.9}).</li>
 *   <li>Query parameters with all-numeric keys are <em>data attribute</em> AIs
 *       (§4.10); non-numeric keys are extension parameters and are ignored,
 *       as are the reserved keywords {@code linkType} and {@code context}.</li>
 *   <li>Percent-encoded characters in values are decoded (§4.2).</li>
 * </ul>
 *
 * <p>Parsing is two-phase, mirroring {@link AISyntaxParser}:
 * <ol>
 *   <li><b>Phase 1 – Extract:</b> walk the URL path and query, collecting
 *       AI/value pairs and their {@link GS1Constants.DigitalLinkAIType} roles,
 *       raising a structural Digital Link error ({@code GE-L001}–{@code GE-L012}) per violation.</li>
 *   <li><b>Phase 2 – Tokenise:</b> render the pairs as a GS1 element string and
 *       delegate to {@link AISyntaxParser} so that elements carry the same
 *       component slices and value checks as element-string parsing; each
 *       element is then tagged with its Digital Link role.</li>
 * </ol>
 *
 * <p>All elements and errors collected before a structural error are still
 * returned so that callers can report as much context as possible.
 */
public class DLSyntaxParser {

    private final AiDefinitionRegistry registry;
    private final AISyntaxParser         syntaxParser;

    // Catalogue codes for the structural Digital Link failures (each localized in the catalogue).
    static final String ERR_CODE_MALFORMED_URI            = "GE-L001";
    static final String ERR_CODE_SCHEME                   = "GE-L002";
    static final String ERR_CODE_HOST                     = "GE-L003";
    static final String ERR_CODE_NO_PRIMARY_KEY           = "GE-L004";
    static final String ERR_CODE_PATH_NOT_PAIRED          = "GE-L005";
    static final String ERR_CODE_QUERY_NOT_DATA_ATTRIBUTE = "GE-L006";
    static final String ERR_CODE_QUERY_NO_VALUE           = "GE-L007";
    static final String ERR_CODE_BANNED_AI                = "GE-L008";
    static final String ERR_CODE_INVALID_PCENC            = "GE-L009";

    /**
     * Creates a new {@link DLSyntaxParser}.
     *
     * @param registry the registry
     */
    public DLSyntaxParser(AiDefinitionRegistry registry) {
        this.registry     = registry;
        this.syntaxParser = new AISyntaxParser(registry);
    }

    /**
     * Parse.
     *
     * @param input the input
     * @param config the config
     * @return the parse.
     */
    public SyntaxParseResult parse(String input, ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        List<GaiaError> errors = new ArrayList<>();
        
        // ---- 1. Convert the input into a URL ----
        URI uri;
        try {
            uri = new URI(input == null ? "" : input);
        } catch (URISyntaxException e) {
            return failure(input, ERR_CODE_MALFORMED_URI, Map.of(), lang);
        }
        String scheme = uri.getScheme();
        if (scheme == null
                || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            return failure(input, ERR_CODE_SCHEME, Map.of(), lang);
        }
        if (uri.getHost() == null || uri.getHost().isEmpty()) {
            return failure(input, ERR_CODE_HOST, Map.of(), lang);
        }

        GS1DigitalLinkInfo info = new GS1DigitalLinkInfo(uri);

        List<GS1AIObjectElement> elements = new ArrayList<>();
        try {
            // ---- 2. Walk the path: custom stem, primary key, key qualifiers ----
            List<DLPair> pairs = new ArrayList<>(extractPathPairs(uri, input, errors, lang));

            // ---- 3. Walk the query string: data attributes ----
            pairs.addAll(extractQueryPairs(uri, input, errors, lang));

            // ---- 4. Tokenise the extracted pairs as a GS1 element string ----
            if (!pairs.isEmpty()) {
                SyntaxParseResult tokenised = syntaxParser.parse(toElementString(pairs), config);
                elements.addAll(tokenised.getElements());
                errors.addAll(tokenised.getErrors());
                tagRoles(elements, pairs);
            }
        } catch (RuntimeException e) {
            System.err.println("[DLSyntaxParser] Unexpected error during extraction: " + e);
            errors.add(ErrorRegistry.INSTANCE.create("GE-S007", null, 0,
                    Map.of("message", e.getClass().getSimpleName() + ": " + e.getMessage()), lang));
        }
        return new SyntaxParseResult(elements, errors, info);
    }

    // -------------------------------------------------------------------------
    // Path extraction
    // -------------------------------------------------------------------------

    private List<DLPair> extractPathPairs(URI uri, String input,
                                        List<GaiaError> errors, GaiaConstants.Language lang) {
        List<DLPair> pairs = new ArrayList<>();
        List<String> segments = DLPathRules.pathSegments(uri.getRawPath());

        // Locate the primary identification key; arbitrary custom path segments
        // may precede it (spec §6.1.1).
        int keyIndex = -1;
        for (int i = 0; i + 1 < segments.size(); i++) {
            if (DLPathRules.isPrimaryKey(segments.get(i))) {
                keyIndex = i;
                break;
            }
        }
        if (keyIndex < 0) {
            errors.add(structuralError(input, ERR_CODE_NO_PRIMARY_KEY, Map.of(), lang));
            return pairs;
        }

        String primaryKey = segments.get(keyIndex);
        String primaryValue = decode(segments.get(keyIndex + 1), input, errors, lang);
        if (primaryValue == null) return pairs;
        pairs.add(new DLPair(primaryKey, primaryValue, GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY));

        // Remaining path segments must pair up as /ai/value key qualifiers.
        List<String> rest = segments.subList(keyIndex + 2, segments.size());
        if (rest.size() % 2 != 0) {
            errors.add(structuralError(input, ERR_CODE_PATH_NOT_PAIRED, Map.of(), lang));
            return pairs;
        }
        List<String> qualifierAis = new ArrayList<>();
        List<DLPair> qualifierPairs = new ArrayList<>();
        for (int i = 0; i < rest.size(); i += 2) {
            String ai = rest.get(i);
            String value = decode(rest.get(i + 1), input, errors, lang);
            if (value == null) return pairs;
            if (banned(ai, input, errors, lang)) return pairs;
            qualifierAis.add(ai);
            qualifierPairs.add(new DLPair(ai, value, GS1Constants.DigitalLinkAIType.KEY_QUALIFIER));
        }
        Map<String, String> qualifierError = DLPathRules.validateQualifierSequence(primaryKey, qualifierAis);
        if (qualifierError != null) {
            Map<String, String> extra = new HashMap<>(qualifierError);
            String code = extra.remove("code");
            errors.add(structuralError(input, code, extra, lang));
        }
        pairs.addAll(qualifierPairs);
        return pairs;
    }


    // -------------------------------------------------------------------------
    // Query extraction
    // -------------------------------------------------------------------------

    private List<DLPair> extractQueryPairs(URI uri, String input,
                                         List<GaiaError> errors, GaiaConstants.Language lang) {
        List<DLPair> pairs = new ArrayList<>();
        String rawQuery = uri.getRawQuery();
        if (rawQuery == null || rawQuery.isEmpty()) return pairs;

        for (String param : rawQuery.split("&")) {
            if (param.isEmpty()) continue;
            int eq = param.indexOf('=');
            String key = eq < 0 ? param : param.substring(0, eq);
            if (!key.chars().allMatch(Character::isDigit)) {
                continue; // extension parameter or reserved keyword (linkType, context) — ignored
            }
            // Only AIs flagged valid as a data attribute may appear in the query string (spec §4.10).
            Optional<AiDefinition> defOpt = registry.find(key);
            if (defOpt.isEmpty() || !defOpt.get().isValidAsDataAttribute()) {
                errors.add(structuralError(input, ERR_CODE_QUERY_NOT_DATA_ATTRIBUTE, Map.of("key", key), lang));
                continue;
            }
            if (eq < 0 || eq == param.length() - 1) {
                errors.add(structuralError(input, ERR_CODE_QUERY_NO_VALUE, Map.of("key", key), lang));
                continue;
            }
            String value = decode(param.substring(eq + 1), input, errors, lang);
            if (value == null) continue;
            pairs.add(new DLPair(key, value, GS1Constants.DigitalLinkAIType.DATA_ATTRIBUTE));
        }
        return pairs;
    }

    // -------------------------------------------------------------------------
    // Element string synthesis and role tagging
    // -------------------------------------------------------------------------

    /**
     * Renders the extracted pairs as a GS1 element string (FNC1 after each
     * variable-length element) so {@link AISyntaxParser} can tokenise them with
     * the standard component walk.
     */
    private String toElementString(List<DLPair> pairs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pairs.size(); i++) {
            DLPair pair = pairs.get(i);
            sb.append(pair.ai).append(pair.value);
            boolean last = i == pairs.size() - 1;
            boolean separatorRequired = registry.find(pair.ai)
                    .map(def -> def.isSeparatorRequired()).orElse(true);
            if (!last && separatorRequired) sb.append(GS1Constants.FNC1_GS);
        }
        return sb.toString();
    }

    /** Tags each tokenised element with the Digital Link role of its source pair. */
    private static void tagRoles(List<GS1AIObjectElement> elements, List<DLPair> pairs) {
        int pairIndex = 0;
        for (GS1AIObjectElement element : elements) {
            while (pairIndex < pairs.size()
                    && !pairs.get(pairIndex).ai.equals(element.getAi())) {
                pairIndex++;
            }
            if (pairIndex >= pairs.size()) break;
            element.setDigitalLinkAIType(pairs.get(pairIndex).role);
            pairIndex++;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean banned(String ai, String input,
                                  List<GaiaError> errors, GaiaConstants.Language lang) {
        if (DLPathRules.isBanned(ai)) {
            errors.add(structuralError(input, ERR_CODE_BANNED_AI, Map.of("ai", ai), lang));
            return true;
        }
        return false;
    }

    /**
     * Percent-decodes a path segment or query value via
     * {@link PcEncUtils#decode(String)} (UTF-8). {@link PcEncUtils#decode} is pure
     * RFC 3986, so a literal {@code '+'} is already preserved. Returns {@code null}
     * and records a {@code GE-L009} error on a malformed sequence.
     */
    private String decode(String s, String input,
                          List<GaiaError> errors, GaiaConstants.Language lang) {
        try {
            return PcEncUtils.decode(s);
        } catch (IllegalArgumentException e) {
            errors.add(structuralError(input, ERR_CODE_INVALID_PCENC, Map.of("value", s), lang));
            return null;
        }
    }

    /**
     * Builds a localized structural Digital Link error under {@code code}. The
     * {@code {uri}} token is always available; {@code extra} supplies any further
     * data values the message needs (e.g. {@code key}, {@code ai}, {@code value}).
     */
    private static GaiaError structuralError(String input, String code,
                                             Map<String, String> extra, GaiaConstants.Language lang) {
        Map<String, String> params = new HashMap<>(extra);
        params.put("uri", String.valueOf(input));
        return ErrorRegistry.INSTANCE.create(code, null, 0, params, lang);
    }

    private SyntaxParseResult failure(String input, String code,
                                      Map<String, String> extra, GaiaConstants.Language lang) {
        List<GaiaError> errors = new ArrayList<>();
        errors.add(structuralError(input, code, extra, lang));
        return new SyntaxParseResult(new ArrayList<>(), errors, null);
    }
}
