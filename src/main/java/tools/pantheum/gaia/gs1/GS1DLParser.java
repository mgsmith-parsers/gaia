package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import tools.pantheum.gaia.gs1.syntax.SyntaxValidator;
import tools.pantheum.gaia.gs1.syntax.digitallink.DLSyntaxParser;

/**
 * GS1 Digital Link URI parser.
 *
 * <p>Runs the shared {@link GS1PipelineParser} pipeline with {@link DLSyntaxParser}
 * as the Stage-1 tokeniser:
 * <ol>
 *   <li>{@link DLSyntaxParser}: converts the input into a URL and extracts the
 *       primary key, key qualifiers, and query data attributes into
 *       {@link GS1AIObjectElement} objects (tagged with their
 *       {@link GS1Constants.DigitalLinkAIType} role), detecting structural
 *       Digital Link errors (GE-L001–GE-L012) and tokeniser SYNTAX_ERRORs.</li>
 *   <li>{@link SyntaxValidator}: checks duplicate AIs, required dependencies and
 *       excluded pairings, raising INTEGRITY_ERRORs.</li>
 *   <li>Content validation, then interpretation enrichment in
 *       {@link GS1Constants.ParseMode#INTERPRETATION} mode — see {@link GS1PipelineParser}.</li>
 * </ol>
 *
 * <p>The returned {@link tools.pantheum.gaia.gs1.model.GS1AIObject} also carries the
 * decomposed {@link tools.pantheum.gaia.gs1.model.GS1DigitalLinkInfo}. Only
 * <em>uncompressed</em> GS1 Digital Link URIs are supported; compressed URIs
 * (GS1 Digital Link: Compression standard) are not recognised.
 *
 * <h2>Thread safety</h2>
 * {@code GS1DLParser} is thread-safe once constructed.
 *
 * @see GS1PipelineParser
 * @see tools.pantheum.gaia.gs1.syntax.digitallink.DLSyntaxParser
 * @see tools.pantheum.gaia.gs1.syntax.digitallink.DLPathRules
 */
public class GS1DLParser extends GS1PipelineParser {

    private final DLSyntaxParser dlSyntaxParser;

    public GS1DLParser() {
        super(AiDefinitionRegistry.getInstance());
        this.dlSyntaxParser = new DLSyntaxParser(registry);
    }

    @Override
    protected SyntaxParseResult tokenise(String input, ParseConfig config) {
        return dlSyntaxParser.parse(input, config);
    }
}
