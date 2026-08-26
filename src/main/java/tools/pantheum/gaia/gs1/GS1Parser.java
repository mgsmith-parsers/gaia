package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObject;

import java.util.Collections;
import java.util.Map;

/**
 * Facade for GS1 element string parsing.
 *
 * <p>This class is the preferred entry point for all GS1 content parsing. It routes
 * inputs to the appropriate parser: GS1 Digital Link URIs ({@code http://} or
 * {@code https://}) are forwarded to {@link GS1DLParser}; all other inputs are treated
 * as plain element strings and forwarded to the {@link GS1AIParser} pipeline
 * (syntax tokenisation → structural validation → content validation → interpretation).
 *
 * <h2>Thread safety</h2>
 * {@code GS1Parser} is thread-safe once constructed.
 *
 * @see GS1AIParser
 * @see GS1DLParser
 */
public class GS1Parser {

    private final GS1AIParser aiParser;
    private final GS1DLParser dlParser;

    /**
     * Constructs a {@code GS1Parser} with default configuration.
     */
    public GS1Parser() {
        this.aiParser = new GS1AIParser();
        this.dlParser = new GS1DLParser();
    }

    /**
     * Parses GS1 content using {@link GS1Constants.ParseMode#CONTENT}.
     *
     * <p>Inputs beginning with {@code "http://"} or {@code "https://"} are routed to
     * {@link GS1DLParser}; all others are treated as plain element strings and routed
     * to {@link GS1AIParser}.
     *
     * @param input a GS1 Digital Link URI ({@code http://} or {@code https://}),
     *              or a raw element string (use ASCII 0x1D as the separator character)
     * @return a {@link GS1AIObject}; never {@code null}
     */
    public GS1AIObject parse(String input) {
        return parse(input,GS1Constants.ParseMode.CONTENT);
    }

    /**
     * Parses GS1 content with the given {@link GS1Constants.ParseMode}
     * and {@link ParseConfig#defaultConfig()}.
     *
     * <p>Inputs beginning with {@code "http://"} or {@code "https://"} are routed to
     * {@link GS1DLParser}; all others are treated as plain element strings and routed
     * to {@link GS1AIParser}.
     *
     * @param input a GS1 Digital Link URI ({@code http://} or {@code https://}),
     *              or a raw element string (use ASCII 0x1D as the separator character)
     * @param mode  controls the depth of validation applied
     * @return a {@link GS1AIObject}; never {@code null}
     */
    public GS1AIObject parse(String input, GS1Constants.ParseMode mode) {
        return parse(input, mode, ParseConfig.defaultConfig());
    }

    /**
     * Parses GS1 content with the given {@link GS1Constants.ParseMode} and {@link ParseConfig}.
     *
     * <p>If {@code input} begins with {@code "http://"} or {@code "https://"} it is treated
     * as a GS1 Digital Link URI and forwarded to {@link GS1DLParser}. All other inputs are
     * forwarded to {@link GS1AIParser} as plain element strings.
     *
     * @param input  a GS1 Digital Link URI or a raw element string (ASCII 0x1D as separator)
     * @param mode   controls the depth of validation applied
     * @param config parse configuration applied by all pipeline stages
     * @return a {@link GS1AIObject}; never {@code null}
     */
    public GS1AIObject parse(String input, GS1Constants.ParseMode mode, ParseConfig config) {
        if (input != null && (input.toLowerCase().startsWith("http://") || input.toLowerCase().startsWith("https://"))) {
            return dlParser.parse(input, mode, config);
        }
        return aiParser.parse(input, mode, config);
    }

    // -------------------------------------------------------------------------

    /**
     * Builds a {@link GS1AIObject} wrapping a single {@code GE-I001} internal error.
     *
     * <p>Called by both {@code GS1Parser} and {@link tools.pantheum.gaia.GaiaParser}
     * to handle unexpected {@link RuntimeException}s consistently: one log line to
     * {@code System.err} and one {@code GE-I001} error carrying the exception message.
     *
     * @param e      the unexpected exception
     * @param config the active parse configuration — used for the error-message language
     * @param tag    caller class name used as the log prefix, e.g. {@code "GS1Parser"}
     * @return a {@link GS1AIObject} with no elements and one {@code GE-I001} error
     */
    public static GS1AIObject internalError(RuntimeException e, ParseConfig config, String tag) {
        System.err.println("[" + tag + "] Unexpected error: " + e);
        GaiaError err = ErrorRegistry.INSTANCE.create("GE-I001", null, 0,
                Map.of("message", e.getClass().getSimpleName() + ": " + e.getMessage()),
                config.getLanguage());
        GS1AIObject object = new GS1AIObject(Collections.emptyList(), Collections.singletonList(err));
        object.setAchievedParseMode(GS1Constants.ParseMode.SYNTAX);
        return object;
    }
}
