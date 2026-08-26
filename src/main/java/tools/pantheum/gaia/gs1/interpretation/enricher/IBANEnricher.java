package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.dataset.Iso3166Data;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;
import tools.pantheum.gaia.gs1.util.IBANUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches AI 8007 (IBAN — International Bank Account Number) by decomposing
 * the value into its constituent fields and verifying the MOD 97 check digits.
 *
 * <p>An IBAN is structured as:
 * <pre>
 *   [ Country code (2) ][ Check digits (2) ][ BBAN (4–30) ]
 *   e.g.  GB29NWBK60161331926819
 * </pre>
 *
 * <h2>Check digit verification (ISO 7064 MOD 97-10)</h2>
 * <ol>
 *   <li>Move the first 4 characters (country + check digits) to the end.</li>
 *   <li>Replace each letter with its numeric equivalent: A=10, B=11, …, Z=35.</li>
 *   <li>Interpret the result as an integer and compute {@code mod 97}.</li>
 *   <li>A valid IBAN yields a remainder of {@code 1}.</li>
 * </ol>
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code IBAN_COUNTRY_CODE} — 2-letter ISO 3166-1 alpha-2 country code,
 *       e.g. {@code "GB"}</li>
 *   <li>{@code IBAN_COUNTRY_NAME} — country name if recognised,
 *       e.g. {@code "United Kingdom"} (omitted if unknown)</li>
 *   <li>{@code IBAN_CHECK_DIGITS} — 2-digit MOD 97 check value, e.g. {@code "29"}</li>
 *   <li>{@code IBAN_CHECK_VALID}  — {@code "Valid"} or {@code "Invalid"} based on
 *       the MOD 97-10 check</li>
 *   <li>{@code IBAN_BBAN}         — Basic Bank Account Number (everything after
 *       the first 4 characters), e.g. {@code "NWBK60161331926819"}</li>
 * </ul>
 * Returns an empty list if the value is null, shorter than 5 characters, or
 * the country code portion is not 2 ASCII letters.
 */
public final class IBANEnricher implements InterpretationEnricherInterface {

    public IBANEnricher() {}

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() < 5) return Collections.emptyList();

        String countryCode  = baseValue.substring(0, 2).toUpperCase();
        String checkDigits  = baseValue.substring(2, 4);
        String bban         = baseValue.substring(4);

        // Country code must be two ASCII letters
        if (!Character.isLetter(countryCode.charAt(0)) || !Character.isLetter(countryCode.charAt(1))) {
            return Collections.emptyList();
        }

        List<GS1AIInterpretation> results = new ArrayList<>(5);
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IBAN_COUNTRY_CODE,  null,  countryCode));

        Iso3166Data.nameForAlpha2(countryCode).ifPresent(countryName ->
                results.add(new GS1AIInterpretation(GS1Constants_Enricher.IBAN_COUNTRY_NAME, null, countryName)));

        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IBAN_CHECK_DIGITS, null, checkDigits));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IBAN_CHECK_VALID,  null, IBANUtils.verifyIbanMod97(baseValue) ? "Valid" : "Invalid"));
        results.add(new GS1AIInterpretation(GS1Constants_Enricher.IBAN_BBAN,         null,         bban));

        return results;
    }
}
