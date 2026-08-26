package tools.pantheum.gaia.gs1.localization;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.result.ParseResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for interpretation-value localization via {@link ValueRegistry}. */
@DisplayName("ValueRegistry")
class ValueRegistryTest {

    private static final GaiaParser PARSER = new GaiaParser();

    /** The language-suffix codes (ISO 639-1, or 639-3 where no 639-1 exists) of every shipped value catalogue. */
    private static final String[] LANGS =
            {"EN","FR","ES","DE","IT","PT","NL","PL","RU","UK","CS","SV","ZH","JA","KO","AR","ID","HI","TR","BN","UR","VI","PCM","ARZ","MR","TE","TA","YUE","WUU","TL","FA","HA","PA","JV","SW"};

    private String valueOf(ParseResult r, String ai, String type) {
        return r.getAiObject().get(ai).getInterpretations().stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getValue)
                .findFirst().orElse(null);
    }

    @Test
    @DisplayName("English values come from the catalogue")
    void englishValues() {
        assertEquals("Yes", ValueRegistry.INSTANCE.valueFor("FLAG_VALUE", "Yes", GaiaConstants.Language.ENGLISH));
        assertEquals("No",  ValueRegistry.INSTANCE.valueFor("FLAG_VALUE", "No",  GaiaConstants.Language.ENGLISH));
    }

    @Test
    @DisplayName("an uncatalogued (type, value) pair returns null (producer value is kept)")
    void unknownPairNull() {
        assertNull(ValueRegistry.INSTANCE.valueFor("FLAG_VALUE", "Maybe", GaiaConstants.Language.ENGLISH));
        assertNull(ValueRegistry.INSTANCE.valueFor("NO_SUCH_TYPE", "Yes", GaiaConstants.Language.ENGLISH));
    }

    @Test
    @DisplayName("a parse in French uses the shipped French flag values")
    void frenchFlagValues() {
        ParseResult r = PARSER.parse("0009506000134352111343211",
                ParseConfig.builder().language(GaiaConstants.Language.FRENCH).build());
        assertEquals("Oui", valueOf(r, "4321", "FLAG_VALUE"));
    }

    @Test
    @DisplayName("default (English) parse keeps the English flag value")
    void englishParseUnchanged() {
        ParseResult r = PARSER.parse("0009506000134352111343211");
        assertEquals("Yes", valueOf(r, "4321", "FLAG_VALUE"));
    }

    @Test
    @DisplayName("a parse in French uses the shipped French sex description")
    void frenchSexDescription() {
        ParseResult r = PARSER.parse("8018950600012345678907\u001D72521",
                ParseConfig.builder().language(GaiaConstants.Language.FRENCH).build());
        assertEquals("Homme", valueOf(r, "7252", "SEX_DESCRIPTION"));
    }

    @Test
    @DisplayName("a parse in Russian uses the shipped Russian temperature unit")
    void russianTemperatureUnit() {
        ParseResult r = PARSER.parse("000950600013435211134331111111",
                ParseConfig.builder().language(GaiaConstants.Language.RUSSIAN).build());
        assertEquals("Цельсий (°C)", valueOf(r, "4331", "TEMPERATURE_UNIT"));
    }

    @Test
    @DisplayName("a parse in French uses the shipped French ISO unit name")
    void frenchIsoUnitName() {
        ParseResult r = PARSER.parse("01095060001343523100111111",
                ParseConfig.builder().language(GaiaConstants.Language.FRENCH).build());
        assertEquals("Kilogramme", valueOf(r, "3100", "ISO_UNIT_NAME"));
    }

    @Test
    @DisplayName("every language catalogue defines exactly the English key set, per translatable type")
    void everyCatalogueCoversEnglishKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> en = load(mapper, "EN");
        assertFalse(en.isEmpty());
        for (String lang : LANGS) {
            Map<String, Map<String, String>> catalogue = load(mapper, lang);
            assertEquals(en.keySet(), catalogue.keySet(),
                    "catalogue " + lang + " must define exactly the same types as EN");
            for (String type : en.keySet()) {
                assertEquals(en.get(type).keySet(), catalogue.get(type).keySet(),
                        "catalogue " + lang + " must define exactly the same values for " + type + " as EN");
            }
        }
    }

    private Map<String, Map<String, String>> load(ObjectMapper mapper, String lang) throws Exception {
        String path = "/localization/" + lang + "/interpretation_values_" + lang + ".json";
        try (InputStream in = ValueRegistryTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "missing catalogue: " + path);
            return mapper.readValue(in, new TypeReference<Map<String, Map<String, String>>>() {});
        }
    }
}
