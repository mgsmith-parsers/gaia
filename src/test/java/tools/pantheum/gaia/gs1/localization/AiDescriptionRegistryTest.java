package tools.pantheum.gaia.gs1.localization;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import tools.pantheum.gaia.result.ParseResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for AI-description localization via {@link AiDescriptionRegistry}. */
@DisplayName("AiDescriptionRegistry")
class AiDescriptionRegistryTest {

    private static final GaiaParser PARSER = new GaiaParser();

    /** The language-suffix codes (ISO 639-1, or 639-3 where no 639-1 exists) of every shipped description catalogue. */
    private static final String[] LANGS =
            {"FR","ES","DE","IT","PT","NL","PL","RU","UK","CS","SV","ZH","JA","KO","AR","ID","HI","TR","BN","UR","VI","PCM","ARZ","MR","TE","TA","YUE","WUU","TL","FA","HA","PA","JV","SW"};

    @Test
    @DisplayName("English is not catalogued — sourced from the AI definition (jsonld) instead")
    void englishNotCatalogued() {
        assertNull(AiDescriptionRegistry.INSTANCE.descriptionFor("01", GaiaConstants.Language.ENGLISH));
        assertEquals("Global Trade Item Number (GTIN)",
                AiDefinitionRegistry.getInstance().find("01").orElseThrow().getDescription());
    }

    @Test
    @DisplayName("an uncatalogued AI code returns null (English description is kept)")
    void unknownAiCodeNull() {
        assertNull(AiDescriptionRegistry.INSTANCE.descriptionFor("NO_SUCH_AI", GaiaConstants.Language.FRENCH));
    }

    @Test
    @DisplayName("French descriptions come from the shipped French catalogue")
    void frenchDescriptions() {
        assertEquals("Numéro international d'article commercial (GTIN)",
                AiDescriptionRegistry.INSTANCE.descriptionFor("01", GaiaConstants.Language.FRENCH));
    }

    @Test
    @DisplayName("a parse in French uses the shipped French description")
    void frenchParse() {
        ParseResult r = PARSER.parse("0109506000134352",
                ParseConfig.builder().language(GaiaConstants.Language.FRENCH).build());
        assertEquals("Numéro international d'article commercial (GTIN)",
                r.getAiObject().get("01").getDescription());
    }

    @Test
    @DisplayName("default (English) parse keeps the English description from the AI definition")
    void englishParseUnchanged() {
        ParseResult r = PARSER.parse("0109506000134352");
        assertEquals("Global Trade Item Number (GTIN)", r.getAiObject().get("01").getDescription());
    }

    @Test
    @DisplayName("every language catalogue defines exactly the AI-code key set")
    void everyCatalogueCoversAiCodeKeySet() throws Exception {
        Set<String> aiCodes = AiDefinitionRegistry.getInstance().all().stream()
                .map(tools.pantheum.gaia.gs1.registry.AiDefinition::getApplicationIdentifier)
                .collect(java.util.stream.Collectors.toSet());
        assertFalse(aiCodes.isEmpty());

        ObjectMapper mapper = new ObjectMapper();
        for (String lang : LANGS) {
            Set<String> keys = load(mapper, lang).keySet();
            assertEquals(aiCodes, keys,
                    "catalogue " + lang + " must define exactly the AI-definition key set");
        }
    }

    private Map<String, String> load(ObjectMapper mapper, String lang) throws Exception {
        String path = "/localization/" + lang + "/ai_descriptions_" + lang + ".json";
        try (InputStream in = AiDescriptionRegistryTest.class.getResourceAsStream(path)) {
            assertNotNull(in, "missing catalogue: " + path);
            return mapper.readValue(in, new TypeReference<Map<String, String>>() {});
        }
    }
}
