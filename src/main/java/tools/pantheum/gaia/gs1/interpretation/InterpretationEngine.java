package tools.pantheum.gaia.gs1.interpretation;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationDefinition;
import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import tools.pantheum.gaia.gs1.localization.LabelRegistry;
import tools.pantheum.gaia.gs1.localization.ValueRegistry;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the interpretation stage of the GS1 parsing pipeline.
 *
 * <p>For each {@link GS1AIObjectElement} in the parsed result, the engine:
 * <ol>
 *   <li>Looks up the AI's interpretation definitions in
 *       {@link InterpretationRegistry}.</li>
 *   <li>For each definition, extracts a base value using the following
 *       priority:
 *       <ul>
 *         <li>If {@link InterpretationDefinition#getComponent()} is non-null,
 *             the value of that component (by index) is used. If the component
 *             is absent or empty (optional component not present), the definition
 *             is silently skipped.</li>
 *         <li>Otherwise, {@link InterpretationDefinition#getStart()} and
 *             {@link InterpretationDefinition#getEnd()} are applied as
 *             substring offsets on {@link GS1AIObjectElement#getValue()}.</li>
 *       </ul>
 *   </li>
 *   <li>If {@link InterpretationDefinition#getType()} is non-null, a base
 *       {@link GS1AIInterpretation} is added to the element.</li>
 *   <li>If {@link InterpretationDefinition#getEnricher()} is non-null, the
 *       enricher is called and its results are appended (additive).</li>
 *   <li>Both the base interpretation and every enricher result have their label
 *       localized to the {@link ParseConfig#getLanguage() configured language} via
 *       {@link tools.pantheum.gaia.gs1.localization.LabelRegistry},
 *       keyed on the interpretation type. An uncatalogued type keeps the English
 *       label the producer supplied.</li>
 *   <li>Enricher results whose type is listed in
 *       {@link InterpretationDefinition#getTranslatableValueTypes()} additionally have
 *       their {@code value} localized via
 *       {@link tools.pantheum.gaia.gs1.localization.ValueRegistry}. This is
 *       opt-in per AI definition, since most enricher values are extracted data
 *       (GTINs, dates, check digits) rather than fixed-vocabulary text.</li>
 * </ol>
 *
 * <p>This class is thread-safe; the underlying registry is immutable after
 * initialisation.
 */
public final class InterpretationEngine {

    private final InterpretationRegistry registry;
    private final AiDefinitionRegistry   aiDefinitionRegistry;

    public InterpretationEngine() {
        this.registry             = InterpretationRegistry.INSTANCE;
        this.aiDefinitionRegistry = AiDefinitionRegistry.getInstance();
    }

    // -------------------------------------------------------------------------

    /**
     * Runs the interpretation stage over all elements using
     * {@link ParseConfig#defaultConfig()}.
     *
     * @param elements the parsed AI elements (modified in place)
     */
    public void interpret(List<GS1AIObjectElement> elements) {
        interpret(elements, ParseConfig.defaultConfig());
    }

    /**
     * Runs the interpretation stage over all elements, attaching
     * {@link GS1AIInterpretation} objects to each element that has definitions.
     *
     * @param elements the parsed AI elements (modified in place)
     * @param config   caller-supplied parse configuration applied by enrichers
     *                 that format their output (e.g. date formatting)
     */
    public void interpret(List<GS1AIObjectElement> elements, ParseConfig config) {
        for (GS1AIObjectElement element : elements) {
            List<InterpretationDefinition> defs = registry.find(element.getAi()).orElse(null);
            if (defs == null) continue;

            AiDefinition aiDefinition = aiDefinitionRegistry.find(element.getAi()).orElse(null);

            for (InterpretationDefinition def : defs) {
                String baseValue = extractBaseValue(element, def);
                if (baseValue == null) continue; // optional component absent — skip silently

                // Base interpretation (only when type is defined)
                if (def.getType() != null && def.getLabel() != null) {
                    element.addInterpretation(localize(
                            new GS1AIInterpretation(def.getType(), def.getLabel(), baseValue), config));
                }

                // Enricher output — always additive
                if (def.getEnricher() != null) {
                    registry.enricherFor(def.getEnricher()).ifPresent(enricher ->
                            element.addInterpretations(localize(
                                    enricher.enrich(baseValue, aiDefinition, element, config), config,
                                    def.getTranslatableValueTypes())));
                }
            }
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Replaces each interpretation's label with the {@link LabelRegistry} label for the
     * configured language, keyed on the interpretation type. Defaults to the English label
     * when the type is not catalogued in the requested language, and falls back to the label
     * the producer already supplied when the type is not catalogued in English either.
     *
     * <p>Additionally, for any interpretation whose type appears in {@code translatableValueTypes},
     * the {@code value} is looked up in {@link ValueRegistry} for the configured language.
     */
    private static List<GS1AIInterpretation> localize(List<GS1AIInterpretation> interpretations, ParseConfig config,
                                                       List<String> translatableValueTypes) {
        List<GS1AIInterpretation> localized = new ArrayList<>(interpretations.size());
        for (GS1AIInterpretation i : interpretations) {
            localized.add(localizeValue(localize(i, config), config, translatableValueTypes));
        }
        return localized;
    }

    private static GS1AIInterpretation localize(GS1AIInterpretation interpretation, ParseConfig config) {
        GaiaConstants.Language language = config.getLanguage();
        String label = LabelRegistry.INSTANCE.labelFor(interpretation.getType(), language);
        if (label == null && language != GaiaConstants.Language.ENGLISH) {
            // Uncatalogued in the requested language — default to the English label.
            label = LabelRegistry.INSTANCE.labelFor(interpretation.getType(), GaiaConstants.Language.ENGLISH);
        }
        if (label == null || label.equals(interpretation.getLabel())) {
            return interpretation;   // not catalogued even in English, or unchanged — keep as-is
        }
        return new GS1AIInterpretation(interpretation.getType(), label, interpretation.getValue());
    }

    private static GS1AIInterpretation localizeValue(GS1AIInterpretation interpretation, ParseConfig config,
                                                      List<String> translatableValueTypes) {
        if (translatableValueTypes == null || !translatableValueTypes.contains(interpretation.getType())) {
            return interpretation;   // not opted in — value is opaque extracted data, keep as-is
        }
        String value = ValueRegistry.INSTANCE.valueFor(
                interpretation.getType(), interpretation.getValue(), config.getLanguage());
        if (value == null || value.equals(interpretation.getValue())) {
            return interpretation;   // not catalogued, or unchanged — keep as-is
        }
        return new GS1AIInterpretation(interpretation.getType(), interpretation.getLabel(), value);
    }

    /**
     * Extracts the base value for a definition.
     * Component takes priority; start/end offsets are used only when component is absent.
     *
     * @return the extracted value, or {@code null} if extraction should be skipped
     */
    private String extractBaseValue(GS1AIObjectElement element, InterpretationDefinition def) {
        if (def.getComponent() != null) {
            // Component-based extraction
            List<GS1AIComponentValue> components = element.getGS1ComponentValues();
            int idx = def.getComponent();
            if (idx >= components.size()) return null; // component index out of range
            String cv = components.get(idx).getValue();
            if (cv == null || cv.isEmpty()) return null; // optional component not present
            return cv;
        }

        // Offset-based extraction on the element's overall value
        String value = element.getValue();
        int start = def.getStart() != null ? def.getStart() : 0;
        int end   = def.getEnd()   != null ? def.getEnd()   : value.length();

        if (start < 0 || end > value.length() || start >= end) return null;
        return value.substring(start, end);
    }
}
