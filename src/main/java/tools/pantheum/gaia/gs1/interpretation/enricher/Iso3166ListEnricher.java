package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Enriches GS1 {@code iso3166list} fields containing one to five concatenated
 * ISO 3166-1 numeric country codes (3 digits each) by resolving each code to
 * its country name.
 *
 * <p>Used by:
 * <ul>
 *   <li>AI 423 — Country of initial processing</li>
 *   <li>AI 425 — Country of disassembly</li>
 * </ul>
 *
 * <h2>Value format</h2>
 * The value is a concatenation of 1–5 three-digit ISO 3166-1 numeric codes,
 * e.g. {@code "840"} (USA), {@code "840036"} (USA + Australia),
 * {@code "840036276"} (USA + Australia + Germany).
 *
 * <h2>Produced interpretations</h2>
 * For each country code at position <i>n</i> (1-based):
 * <ul>
 *   <li>{@code COUNTRY_CODE_n} — the 3-digit numeric code, e.g. {@code "036"}</li>
 *   <li>{@code COUNTRY_NAME_n} — the country name, e.g. {@code "Australia"};
 *       omitted if the code is not found in the ISO 3166 dataset</li>
 * </ul>
 * Plus a combined summary:
 * <ul>
 *   <li>{@code COUNTRY_LIST} — comma-separated country names for all resolved
 *       codes, e.g. {@code "United States of America, Australia"}</li>
 * </ul>
 * Returns an empty list if the value is null, empty, or not a multiple of 3 digits.
 */
public final class Iso3166ListEnricher implements InterpretationEnricherInterface {

    public Iso3166ListEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.isEmpty()) return Collections.emptyList();
        if (baseValue.length() % 3 != 0)             return Collections.emptyList();

        int count = baseValue.length() / 3;
        if (count < 1 || count > 5)                   return Collections.emptyList();

        List<GS1AIInterpretation> results = new ArrayList<>(count * 2 + 1);
        StringJoiner nameList = new StringJoiner(", ");

        for (int i = 0; i < count; i++) {
            String code = baseValue.substring(i * 3, i * 3 + 3);
            String n    = String.valueOf(i + 1);

            results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_CODE_NUMERIC_PREFIX + n, "Country " + n + " code (numeric)", code));

            Iso3166Data.nameForNumeric(code).ifPresent(name -> {
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_NAME_PREFIX + n, "Country " + n + " name", name));
                nameList.add(name);
            });
        }

        if (nameList.length() > 0) {
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.COUNTRY_LIST, null, nameList.toString()));
        }

        return results;
    }
}
