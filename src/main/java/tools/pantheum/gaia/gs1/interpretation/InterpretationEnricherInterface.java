package tools.pantheum.gaia.gs1.interpretation;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.List;

/**
 * Enriches a base value extracted from a GS1 AI element with additional
 * {@link GS1AIInterpretation} objects derived via a registry lookup or other logic.
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Implementations must be <strong>stateless and thread-safe</strong>.</li>
 *   <li>The returned list must never be {@code null}; return an empty list when
 *       no enrichment is possible (e.g. lookup found no match).</li>
 *   <li>Enricher output is <strong>always additive</strong> — it is appended to
 *       any base {@link GS1AIInterpretation} already produced by the definition's
 *       {@code type}/{@code label}. Enrichers must not attempt to replace or
 *       remove existing interpretations.</li>
 * </ul>
 *
 * <h2>Registration</h2>
 * Enricher implementations live in the
 * {@code tools.pantheum.gaia.gs1.interpretation.enricher} sub-package and
 * are referenced by their simple class name in {@code ai-content.json}.
 * They are instantiated once via reflection by {@link InterpretationRegistry}
 * and cached for the lifetime of the registry.
 */
public interface InterpretationEnricherInterface {

    /**
     * Produces additional interpretations from {@code baseValue}.
     *
     * @param baseValue    the extracted segment value (never {@code null} or empty)
     * @param aiDefinition the full AI definition for the element being interpreted
     * @param element      the parent element, providing access to all component values
     *                     and any interpretations already added in this pass
     * @return additional interpretations to append; never {@code null}
     */
    List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element);

    /**
     * Produces additional interpretations from {@code baseValue}, applying
     * caller-supplied formatting preferences from {@code config}.
     *
     * <p>Enrichers that do not use {@code config} inherit this default, which
     * delegates to {@link #enrich(String, AiDefinition, GS1AIObjectElement)}.
     * Enrichers that format dates (e.g. {@code DateEnricher},
     * {@code ProductionTimeEnricher}) override this method.
     *
     * @param baseValue    the extracted segment value (never {@code null} or empty)
     * @param aiDefinition the full AI definition for the element being interpreted
     * @param element      the parent element
     * @param config       caller-supplied parse configuration; never {@code null}
     * @return additional interpretations to append; never {@code null}
     */
    default List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition,
                                             GS1AIObjectElement element, ParseConfig config) {
        return enrich(baseValue, aiDefinition, element);
    }
}
