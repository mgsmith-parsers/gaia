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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for interpretation-label localization via {@link LabelRegistry}. */
@DisplayName("LabelRegistry")
class LabelRegistryTest {

    private static final GaiaParser PARSER = new GaiaParser();

    /** The language-suffix codes (ISO 639-1, or 639-3 where no 639-1 exists) of every shipped label catalogue. */
    private static final String[] LANGS =
            {"EN","FR","ES","DE","IT","PT","NL","PL","RU","UK","CS","SV","ZH","JA","KO","AR","ID","HI","TR","BN","UR","VI","PCM","ARZ","MR","TE","TA","YUE","WUU","TL","FA","HA","PA","JV","SW"};

    private String labelOf(ParseResult r, String type) {
        return r.getAiObject().get("01").getInterpretations().stream()
                .filter(i -> type.equals(i.getType()))
                .map(GS1AIInterpretation::getLabel)
                .findFirst().orElse(null);
    }

    @Test
    @DisplayName("English labels come from the catalogue")
    void englishLabels() {
        assertEquals("GTIN type",   LabelRegistry.INSTANCE.labelFor("GTIN_TYPE", GaiaConstants.Language.ENGLISH));
        assertEquals("Check digit", LabelRegistry.INSTANCE.labelFor("GTIN_CHECK_DIGIT", GaiaConstants.Language.ENGLISH));
    }

    @Test
    @DisplayName("an uncatalogued type returns null (producer label is kept)")
    void unknownTypeNull() {
        assertNull(LabelRegistry.INSTANCE.labelFor("NO_SUCH_TYPE", GaiaConstants.Language.ENGLISH));
    }

    @Test
    @DisplayName("a parse in French uses the shipped French labels")
    void frenchLabels() {
        ParseResult r = PARSER.parse("0109506000134352",
                ParseConfig.builder().language(GaiaConstants.Language.FRENCH).build());
        assertEquals("Type de GTIN",       labelOf(r, "GTIN_TYPE"));
        assertEquals("Chiffre de contrôle", labelOf(r, "GTIN_CHECK_DIGIT"));
        assertEquals("GTIN",               labelOf(r, "GTIN_NATIVE")); // acronym unchanged across languages
    }

    @Test
    @DisplayName("default (English) parse keeps English labels")
    void englishParseUnchanged() {
        ParseResult r = PARSER.parse("0109506000134352");
        assertEquals("GTIN type",   labelOf(r, "GTIN_TYPE"));
        assertEquals("Check digit", labelOf(r, "GTIN_CHECK_DIGIT"));
    }

    @Test
    @DisplayName("every language catalogue defines exactly the English key set")
    void everyCatalogueCoversEnglishKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> enKeys = load(mapper, "EN").keySet();
        assertFalse(enKeys.isEmpty());
        for (String lang : LANGS) {
            Set<String> keys = load(mapper, lang).keySet();
            assertEquals(enKeys, keys,
                    "catalogue " + lang + " must define exactly the same keys as EN");
        }
    }

    private Map<String, String> load(ObjectMapper mapper, String lang) throws Exception {
        String path = "/localization/" + lang + "/interpretation_labels_" + lang + ".json";
        try (InputStream in = LabelRegistryTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "missing catalogue: " + path);
            return mapper.readValue(in, new TypeReference<Map<String, String>>() {});
        }
    }
}
