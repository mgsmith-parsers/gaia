package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GS1AIObject} — the parse result container. */
@DisplayName("GS1AIObject")
class GS1AIObjectTest {

    private GS1AIObjectElement element(String ai, String value) {
        return new GS1AIObjectElement(
                AiDefinitionRegistry.getInstance().find(ai).orElseThrow(),
                value, 0, Collections.emptyList());
    }

    private GaiaError error() {
        return ErrorRegistry.INSTANCE.create("GE-S004", "01", 0, Map.of("ai", "01"));
    }

    private GaiaError warning() {
        return ErrorRegistry.INSTANCE.create("GE-W002", null, 0, Map.of());
    }

    @Test
    @DisplayName("add/get/contains/size work together")
    void addAndLookup() {
        GS1AIObject obj = new GS1AIObject(Collections.emptyList(), Collections.emptyList());
        obj.add(element("01", "09506000134352"));
        assertEquals(1, obj.size());
        assertTrue(obj.contains("01"));
        assertFalse(obj.contains("10"));
        assertEquals("09506000134352", obj.get("01").getValue());
        assertNull(obj.get("10"));
    }

    @Test
    @DisplayName("add(null) is rejected")
    void addNullRejected() {
        GS1AIObject obj = new GS1AIObject(Collections.emptyList(), Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> obj.add(null));
    }

    @Test
    @DisplayName("getErrors excludes warnings; getWarnings excludes errors")
    void errorWarningSeparation() {
        GS1AIObject obj = new GS1AIObject(Collections.emptyList(),
                List.of(error(), warning()));
        assertEquals(1, obj.getErrors().size());
        assertEquals(1, obj.getWarnings().size());
        assertEquals(2, obj.getAllIssues().size());
    }

    @Test
    @DisplayName("warnings do not affect validity; errors do")
    void validity() {
        assertTrue(new GS1AIObject(Collections.emptyList(),
                List.of(warning())).isValid());
        assertFalse(new GS1AIObject(Collections.emptyList(),
                List.of(error())).isValid());
    }

    @Test
    @DisplayName("toHriString renders (ai)value pairs")
    void hriString() {
        GS1AIObject obj = new GS1AIObject(
                List.of(element("01", "09506000134352"), element("10", "LOT1")),
                Collections.emptyList());
        assertEquals("(01)09506000134352 (10)LOT1", obj.toHriString());
    }

    @Test
    @DisplayName("toElementString renders raw AI/value pairs with FNC1 after variable-length elements")
    void elementString() {
        GS1AIObject obj = new GS1AIObject(
                List.of(element("01", "09506000134352"),   // fixed-length — no separator
                        element("10", "LOT-ABC"),          // variable-length — FNC1 follows
                        element("17", "271231")),          // fixed-length, last
                Collections.emptyList());
        assertEquals("0109506000134352" + "10LOT-ABC" + GS1Constants.FNC1_GS + "17271231",
                obj.toElementString());
    }

    @Test
    @DisplayName("toElementString omits the FNC1 after a trailing variable-length element")
    void elementStringNoTrailingFnc1() {
        GS1AIObject obj = new GS1AIObject(
                List.of(element("01", "09506000134352"), element("10", "LOT-ABC")),
                Collections.emptyList());
        assertEquals("0109506000134352" + "10LOT-ABC", obj.toElementString());
    }

    @Test
    @DisplayName("toElementString round-trips through the parser")
    void elementStringRoundTrip() {
        String input = "0109506000134352" + "10LOT-ABC" + GS1Constants.FNC1_GS + "17271231";
        GS1AIObject parsed = new tools.pantheum.gaia.gs1.GS1Parser().parse(input);
        assertTrue(parsed.isValid());
        assertEquals(input, parsed.toElementString());
    }

    @Test
    @DisplayName("toElementString returns null when the object carries errors")
    void elementStringNullOnErrors() {
        GS1AIObject obj = new GS1AIObject(
                List.of(element("01", "09506000134352")), List.of(error()));
        assertNull(obj.toElementString());
    }

    @Test
    @DisplayName("content type reflects Digital Link presence")
    void contentType() {
        GS1AIObject plain = new GS1AIObject(Collections.emptyList(), Collections.emptyList());
        assertEquals(GS1Constants.GS1ContentType.GS1_APPLICATION_IDENTIFIERS, plain.getContentType());
        assertFalse(plain.hasDigitalLink());

        GS1DigitalLinkInfo dl = new GS1DigitalLinkInfo(
                java.net.URI.create("https://example.com/01/09506000134352"));
        GS1AIObjectElement primaryKey = element("01", "09506000134352");
        primaryKey.setDigitalLinkAIType(GS1Constants.DigitalLinkAIType.PRIMARY_IDENTIFICATION_KEY);
        GS1AIObject linked = new GS1AIObject(List.of(primaryKey), Collections.emptyList(), dl);
        assertTrue(linked.hasDigitalLink());
        assertEquals(GS1Constants.GS1ContentType.GS1_DIGITAL_LINK, linked.getContentType());
        assertSame(dl, linked.getDigitalLinkInfo());

        // URL metadata present but no primary key — not a usable Digital Link.
        GS1AIObject noKey = new GS1AIObject(Collections.emptyList(), Collections.emptyList(), dl);
        assertFalse(noKey.hasDigitalLink());
        assertSame(dl, noKey.getDigitalLinkInfo());
        assertEquals(GS1Constants.GS1ContentType.GS1_APPLICATION_IDENTIFIERS, noKey.getContentType());
    }
}
