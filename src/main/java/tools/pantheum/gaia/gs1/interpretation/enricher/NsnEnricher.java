package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.FSGData;
import tools.pantheum.gaia.gs1.dataset.NCBData;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 7001 (NSN — NATO Stock Number) by decomposing the 13-digit value
 * into its constituent NATO-defined fields and formatting it in the standard
 * dash-separated display notation.
 *
 * <h2>NSN structure (13 digits)</h2>
 * <pre>
 *   Digits  0– 3 : Federal Supply Class (FSC)  — 4 digits
 *   Digits  4–12 : National Item Identification Number (NIIN) — 9 digits, of which
 *     Digits  4– 5 :   NCB cataloguing nation code — 2 digits
 *     Digits  6–12 :   item number                 — 7 digits
 * </pre>
 *
 * <p>The FSC itself splits in two: the first two digits are the Federal Supply
 * Group (FSG), identifying the broad scope of property, and the last two
 * narrow that group to a specific class.</p>
 *
 * <h2>Standard display format</h2>
 * {@code NNNN-CC-NNN-NNNN}, e.g. {@code "1005-00-123-4567"}
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code NSN_FSG}          — Federal Supply Group, the first two digits
 *       of the FSC, e.g. {@code "10"}</li>
 *   <li>{@code NSN_FSG_NAME}     — Federal Supply Group title if the group is
 *       recognised, e.g. {@code "Weapons"}; omitted otherwise</li>
 *   <li>{@code NSN_FSCG}          — Federal Supply Class, e.g. {@code "1005"}</li>
 *   <li>{@code NSN_FSCG_NAME}     — Federal Supply Class title if the class is
 *       recognised, e.g. {@code "Weapons (from 1 mm through 30 mm)"};
 *       omitted otherwise</li>
 *   <li>{@code NSN_NCB_COUNTRY_CODE} — NATO country code, e.g. {@code "00"}</li>
 *   <li>{@code NSN_NCB_COUNTRY_NAME} — NCB country name if the code is recognised,
 *       e.g. {@code "United States"}; omitted for unrecognised codes</li>
 *   <li>{@code NSN_NCB_COUNTRY_CTR}  — the country's ISO 3166-1 alpha-3 code,
 *       e.g. {@code "USA"}; omitted for unrecognised codes and for NCB 44
 *       (United Nations), which has no alpha-3 code</li>
 *   <li>{@code NSN_NCB_COUNTRY_CAT}  — the country's NCS participation category,
 *       one of {@code NATO}, {@code TIER1}, {@code TIER2}, {@code OTHER} or
 *       {@code NSPA}; omitted for unrecognised codes</li>
 *   <li>{@code NSN_NIIN}         — National Item Identification Number: the NCB
 *       cataloguing nation code followed by the 7-digit item number,
 *       e.g. {@code "001234567"}</li>
 *   <li>{@code NSN_FORMATTED}    — standard dash-formatted NSN,
 *       e.g. {@code "1005-00-123-4567"}</li>
 * </ul>
 * Returns an empty list if the value is null or not exactly 13 digits.
 */
public final class NsnEnricher implements InterpretationEnricherInterface {

    public NsnEnricher() {}


    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 13) return Collections.emptyList();

        String fsc         = baseValue.substring(0, 4);
        String countryCode = baseValue.substring(4, 6);
        String itemNumber  = baseValue.substring(6, 13);

        // The NIIN is the 9-digit tail: the NCB code followed by the item number.
        String niin = countryCode + itemNumber;

        // Standard NSN display: NNNN-CC-NNN-NNNN
        String formatted = fsc + "-" + countryCode + "-" + itemNumber.substring(0, 3) + "-" + itemNumber.substring(3);

        String fsg = fsc.substring(0, 2);

        List<GS1AIInterpretation> results = new ArrayList<>(9);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_FSG,           null,          fsg));

        FSGData.titleForGroup(fsg).ifPresent(groupName ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_FSG_NAME, null, groupName)));

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_FSCG,          null,          fsc));

        FSGData.titleForClass(fsc).ifPresent(className ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_FSCG_NAME, null, className)));

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_NCB_COUNTRY_CODE, null, countryCode));

        NCBData.entryFor(countryCode).ifPresent(entry -> {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_NCB_COUNTRY_NAME, null, entry.getCountry()));
            if (entry.getCtr() != null) {
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_NCB_COUNTRY_CTR, null, entry.getCtr()));
            }
            if (entry.getCat() != null) {
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_NCB_COUNTRY_CAT, null, entry.getCat()));
            }
        });

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_NIIN,      null,         niin));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.NSN_FORMATTED, null,    formatted));
        return results;
    }
}
