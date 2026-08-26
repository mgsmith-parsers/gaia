package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import tools.pantheum.gaia.gs1.syntax.SyntaxValidator;
import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;

/**
 * GS1 Application Identifier element string parser.
 *
 * <p>Runs the shared {@link GS1PipelineParser} pipeline with {@link AISyntaxParser}
 * as the Stage-1 tokeniser:
 * <ol>
 *   <li>{@link AISyntaxParser}: tokenises the raw element string into
 *       {@link GS1AIObjectElement} objects, detecting SYNTAX_ERRORs.</li>
 *   <li>{@link SyntaxValidator}: checks duplicate AIs, required dependencies and
 *       excluded pairings, raising INTEGRITY_ERRORs.</li>
 *   <li>Content validation, then interpretation enrichment in
 *       {@link GS1Constants.ParseMode#INTERPRETATION} mode — see {@link GS1PipelineParser}.</li>
 * </ol>
 *
 * <h2>Thread safety</h2>
 * {@code GS1AIParser} is thread-safe once constructed.
 *
 * <h2>Separator character</h2>
 * The GS character (ASCII 0x1D / {@link GS1Constants#FNC1_GS}) is the
 * expected separator between variable-length element strings. Inputs should be
 * normalised to use this character before parsing; {@link tools.pantheum.gaia.GaiaParser}
 * is the recommended entry point for external callers.
 *
 * @see GS1PipelineParser
 */
public class GS1AIParser extends GS1PipelineParser {

    private final AISyntaxParser aiSyntaxParser;

    public GS1AIParser() {
        super(AiDefinitionRegistry.getInstance());
        this.aiSyntaxParser = new AISyntaxParser(registry);
    }

    @Override
    protected SyntaxParseResult tokenise(String input, ParseConfig config) {
        return aiSyntaxParser.parse(input, config);
    }
}
