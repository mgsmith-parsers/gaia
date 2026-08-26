package tools.pantheum.gaia.gs1.syntax;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.gs1.registry.AiExcludesEntry;
import tools.pantheum.gaia.gs1.registry.AiRequiresEntry;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Performs structural validation over the full set of parsed elements.
 *
 * Checks (all produce INTEGRITY_ERROR):
 * <ol>
 *   <li>Duplicate Application Identifiers — GE-S004</li>
 *   <li>Missing required AI dependencies — GE-S005</li>
 *   <li>Excluded AI pairings — GE-S006</li>
 * </ol>
 *
 * The requires (GE-S005) and excludes (GE-S006) checks can each be disabled
 * independently via {@link ParseConfig#isSkipRequiresCheck()} and
 * {@link ParseConfig#isSkipExcludesCheck()}; both run by default. The
 * duplicate-AI check (GE-S004) always runs.
 *
 * These checks operate on the full token list produced by
 * {@link tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser} — the complete set of AI codes is
 * needed for the dependency and exclusion checks. Errors are attached directly to the
 * offending elements, so {@code SyntaxParseResult#hasSyntaxErrors()} reports them and the
 * caller skips the content stage when any are present.
 */
public class SyntaxValidator {

    private final AiDefinitionRegistry registry;

    public SyntaxValidator(AiDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * Validates the structural integrity of the parsed element list, attaching
     * any errors directly to the element that caused them via
     * {@link GS1AIObjectElement#addError(tools.pantheum.gaia.error.GaiaError)}.
     *
     * @param elements the tokenised elements to validate
     * @param config   parse configuration — controls the language of error messages
     */
    public void validate(List<GS1AIObjectElement> elements, ParseConfig config) {
        GaiaConstants.Language lang = config.getLanguage();
        Set<String> presentAis = new LinkedHashSet<>();

        // ---- 1. Duplicate AI check ----
        for (GS1AIObjectElement element : elements) {
            String ai = element.getAi();
            if (!presentAis.add(ai)) {
                element.addError(ErrorRegistry.INSTANCE.create("GE-S004", ai, element.getPosition(),
                        Map.of("ai", ai), lang));
            }
        }

        // ---- 2 & 3. Requires / Excludes checks ----
        boolean checkRequires = !config.isSkipRequiresCheck();
        boolean checkExcludes = !config.isSkipExcludesCheck();
        if (!checkRequires && !checkExcludes) {
            return; // both structural dependency checks disabled by config
        }

        for (GS1AIObjectElement element : elements) {
            String ai = element.getAi();
            Optional<AiDefinition> defOpt = registry.find(ai);
            if (defOpt.isEmpty()) continue; // already caught by parser

            AiDefinition def = defOpt.get();

            // Requires check
            if (checkRequires) {
                List<AiRequiresEntry> requires = def.getRequires();
                if (!requires.isEmpty()) {
                    boolean satisfied = requires.stream()
                            .anyMatch(entry -> entry.isSatisfiedBy(presentAis));
                    if (!satisfied) {
                        String required = requires.stream()
                                .map(AiRequiresEntry::toString)
                                .collect(Collectors.joining(" OR "));
                        element.addError(ErrorRegistry.INSTANCE.create("GE-S005", ai, element.getPosition(),
                                Map.of("ai", ai, "required", required), lang));
                    }
                }
            }

            // Excludes check
            if (checkExcludes) {
                for (AiExcludesEntry excluded : def.getExcludes()) {
                    for (String presentAi : presentAis) {
                        if (!presentAi.equals(ai) && excluded.matches(presentAi)) {
                            element.addError(ErrorRegistry.INSTANCE.create("GE-S006", ai, element.getPosition(),
                                    Map.of("ai", ai, "other", presentAi), lang));
                        }
                    }
                }
            }
        }
    }
}
