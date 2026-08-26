package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.GaiaParser;
import tools.pantheum.gaia.result.ParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1AIComponentValue} — a value slice mapped to its component. */
@DisplayName("GS1AIComponentValue")
class GS1AIComponentValueTest {

    @Test
    @DisplayName("a parsed ITIP element decomposes into its components")
    void itipComponents() {
        ParseResult resp = new GaiaParser().parse(
                "0109506000134352" + "21A" + "\u001D" + "8006" + "095060001343520101");
        List<GS1AIComponentValue> comps = resp.getAiObject().get("8006").getGS1ComponentValues();
        assertEquals(2, comps.size(), "ITIP is N14 + N4 (piece/total)");
        assertEquals("09506000134352", comps.get(0).getValue());
        assertEquals("0101", comps.get(1).getValue());
        assertEquals("N", comps.get(0).getType());
        assertEquals(0, comps.get(0).getOffset());
        assertEquals(14, comps.get(1).getOffset());
        assertNotNull(comps.get(0).getComponent());
    }
}
