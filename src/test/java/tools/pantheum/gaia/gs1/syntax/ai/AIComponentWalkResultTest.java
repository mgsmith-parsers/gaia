package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AIComponentWalkResult} — the component-walk outcome used by
 * {@link AISyntaxParser} to attach {@link GS1AIComponentValue} slices to elements.
 * The class is package-private plumbing, so it is exercised through the parser.
 */
@DisplayName("AIComponentWalkResult (via AISyntaxParser)")
class ComponentWalkResultTest {

    private final AISyntaxParser parser = new AISyntaxParser(AiDefinitionRegistry.getInstance());

    @Test
    @DisplayName("the component walk slices a multi-component value")
    void componentValuesAttached() {
        GS1AIObjectElement el = parser
                .parse("8006095060001343520101", ParseConfig.defaultConfig())
                .getElements().get(0);
        List<GS1AIComponentValue> comps = el.getGS1ComponentValues();
        assertEquals(2, comps.size(), "ITIP walks into N14 + N4 (piece/total)");
        assertEquals("09506000134352", comps.get(0).getValue());
    }

    @Test
    @DisplayName("an omitted optional component yields no slice")
    void optionalComponentOmitted() {
        GS1AIObjectElement el = parser
                .parse("2539506000134352", ParseConfig.defaultConfig())
                .getElements().get(0);
        assertEquals(1, el.getGS1ComponentValues().size(),
                "GDTI without the optional serial walks into a single component");
    }
}
