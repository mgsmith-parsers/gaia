package tools.pantheum.gaia.gs1;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.content.ContentValidator;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEngine;
import tools.pantheum.gaia.gs1.model.GS1AIObject;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1DigitalLinkInfo;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.syntax.SyntaxParseResult;
import tools.pantheum.gaia.gs1.syntax.SyntaxValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared three-stage parsing pipeline for GS1 content.
 *
 * <p>Both concrete parsers — {@link GS1AIParser} for plain element strings and
 * {@link GS1DLParser} for GS1 Digital Link URIs — share the same post-tokenisation
 * pipeline and differ <em>only</em> in how Stage 1 tokenises the input. This base
 * class owns the common stages and {@link #parse(String, GS1Constants.ParseMode, ParseConfig)}
 * template; subclasses supply the input-specific {@link #tokenise(String, ParseConfig)} step.
 *
 * <h2>Processing pipeline</h2>
 * <ol>
 *   <li><b>Syntax stage</b>
 *     <ol type="a">
 *       <li>{@link #tokenise(String, ParseConfig)}: tokenises the input into
 *           {@link GS1AIObjectElement} objects (raising SYNTAX_ERRORs, or the
 *           Digital Link structural errors GE-L001–GE-L012).</li>
 *       <li>{@link SyntaxValidator}: checks duplicate AIs, required dependencies
 *           and excluded pairings, raising INTEGRITY_ERRORs attached to elements.</li>
 *     </ol>
 *     If the syntax stage produced any error — a tokeniser SYNTAX_ERROR or a
 *     structural INTEGRITY_ERROR (detected together via
 *     {@code SyntaxParseResult#hasSyntaxErrors()}) — the content stage is skipped
 *     and the response is returned immediately.
 *   </li>
 *   <li><b>Content stage</b> — {@link ContentValidator}: validates each element's
 *       value against the regex format, semantic date rules, and check digit /
 *       check character pair, raising FORMAT_ERROR and DATA_ERROR as appropriate.</li>
 *   <li><b>Interpretation stage</b> (runs only in
 *       {@link GS1Constants.ParseMode#INTERPRETATION} mode and only when no element
 *       carries an error from any prior stage) — {@link InterpretationEngine}:
 *       decomposes each element's value into labelled, human-readable segments.</li>
 * </ol>
 *
 * <h2>Thread safety</h2>
 * A pipeline parser is thread-safe once constructed.
 */
abstract class GS1PipelineParser {

    /** Shared by subclasses to construct their Stage-1 tokeniser. */
    protected final AiDefinitionRegistry registry;

    private final SyntaxValidator      syntaxValidator;
    private final ContentValidator     contentValidator;
    private final InterpretationEngine interpretationEngine;

    protected GS1PipelineParser(AiDefinitionRegistry registry) {
        this.registry             = registry;
        this.syntaxValidator      = new SyntaxValidator(registry);
        this.contentValidator     = new ContentValidator(registry);
        this.interpretationEngine = new InterpretationEngine();
    }

    /**
     * Stage 1 — tokenises the raw input into elements. The only step that differs
     * between an element string and a Digital Link URI.
     *
     * @param input  the raw input to tokenise
     * @param config parse configuration applied by the tokeniser
     * @return the Stage-1 result; its {@code getDigitalLinkInfo()} is {@code null}
     *         for element-string inputs and populated for Digital Link URIs
     */
    protected abstract SyntaxParseResult tokenise(String input, ParseConfig config);

    /**
     * Runs the full pipeline to the depth requested by {@code mode}.
     *
     * @param input  the raw input
     * @param mode   controls the depth of validation applied:
     *               {@link GS1Constants.ParseMode#SYNTAX SYNTAX} stops after tokenisation and
     *               structural checks; {@link GS1Constants.ParseMode#CONTENT CONTENT} also runs
     *               format, check-digit, and custom semantic validation;
     *               {@link GS1Constants.ParseMode#INTERPRETATION INTERPRETATION} additionally
     *               enriches each element with human-readable interpretation data
     * @param config parse configuration applied by all pipeline stages
     * @return a {@link GS1AIObject}; never {@code null}
     */
    public final GS1AIObject parse(String input, GS1Constants.ParseMode mode, ParseConfig config) {
        try {
            // --- Stage 1a: Tokenisation ---
            SyntaxParseResult syntaxResult = tokenise(input, config);
            List<GS1AIObjectElement> elements = new ArrayList<>(syntaxResult.getElements());
            List<GaiaError>          errors   = new ArrayList<>(syntaxResult.getErrors());
            GS1DigitalLinkInfo       digitalLinkInfo = syntaxResult.getDigitalLinkInfo();

            // --- Stage 1b: Structural validation (errors attached directly to elements) ---
            syntaxValidator.validate(elements, config);

            if (syntaxResult.hasSyntaxErrors() || mode == GS1Constants.ParseMode.SYNTAX) {
                // Stopped after the syntax stage — either by request or because a
                // syntax/structural error blocked the content stage.
                return finished(elements, errors, digitalLinkInfo, GS1Constants.ParseMode.SYNTAX);
            }

            // --- Stage 2: Content validation (errors attached directly to elements) ---
            contentValidator.validate(elements, config);

            if (mode == GS1Constants.ParseMode.CONTENT) {
                return finished(elements, errors, digitalLinkInfo, GS1Constants.ParseMode.CONTENT);
            }

            // --- Stage 3: Interpretation (only when content is clean) ---
            boolean clean = elements.stream().noneMatch(GS1AIObjectElement::hasErrors);
            if (clean) {
                interpretationEngine.interpret(elements, config);
            }

            // Interpretation was requested; it is only reached when content is clean.
            // Otherwise the deepest stage actually achieved is CONTENT.
            return finished(elements, errors, digitalLinkInfo,
                    clean ? GS1Constants.ParseMode.INTERPRETATION : GS1Constants.ParseMode.CONTENT);

        } catch (RuntimeException e) {
            return GS1Parser.internalError(e, config, getClass().getSimpleName());
        }
    }

    /** Builds the result and records the deepest pipeline stage it actually reached. */
    private static GS1AIObject finished(List<GS1AIObjectElement> elements, List<GaiaError> errors,
                                        GS1DigitalLinkInfo digitalLinkInfo, GS1Constants.ParseMode achieved) {
        GS1AIObject object = new GS1AIObject(elements, errors, digitalLinkInfo);
        object.setAchievedParseMode(achieved);
        return object;
    }
}
