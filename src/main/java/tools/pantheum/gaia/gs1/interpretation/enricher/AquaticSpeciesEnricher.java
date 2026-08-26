package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.AsfisData;
import tools.pantheum.gaia.gs1.dataset.AsfisEntry;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Enriches AI 7008 (AQUATIC SPECIES) by resolving the FAO Alpha-3 species
 * code to its scientific name, English common name, and taxonomic family and
 * order from the ASFIS List of Species for Fishery Statistics Purposes.
 *
 * <p>Unrecognised codes (or those not present when the dataset failed to load)
 * produce only a {@code SPECIES_CODE} entry.
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code SPECIES_CODE}          — the raw Alpha-3 code, e.g. {@code "COD"}</li>
 *   <li>{@code SPECIES_SCIENTIFIC}    — scientific name, e.g. {@code "Gadus morhua"}</li>
 *   <li>{@code SPECIES_ENGLISH}       — English common name, e.g. {@code "Atlantic cod"}
 *       (omitted if not assigned)</li>
 *   <li>{@code SPECIES_FAMILY}        — taxonomic family, e.g. {@code "GADIDAE"}
 *       (omitted if not assigned)</li>
 *   <li>{@code SPECIES_ORDER}         — order or higher taxon, e.g. {@code "GADIFORMES"}
 *       (omitted if not assigned)</li>
 * </ul>
 *
 * @see AsfisData
 */
public final class AquaticSpeciesEnricher implements InterpretationEnricherInterface {

    public AquaticSpeciesEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(5);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPECIES_CODE, null, baseValue));

        Optional<AsfisEntry> opt = AsfisData.entryFor(baseValue);
        if (!opt.isPresent()) return results;

        AsfisEntry entry = opt.get();
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPECIES_SCIENTIFIC, null, entry.getScientificName()));

        if (entry.getEnglishName() != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPECIES_ENGLISH, null, entry.getEnglishName()));
        }
        if (entry.getFamily() != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPECIES_FAMILY, null, entry.getFamily()));
        }
        if (entry.getOrder() != null) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.SPECIES_ORDER, null, entry.getOrder()));
        }

        return results;
    }
}
