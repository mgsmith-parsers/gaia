package tools.pantheum.gaia.error.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link ErrorDefinition} — the error catalogue bean. */
@DisplayName("ErrorDefinition")
class ErrorDefinitionTest {

    @Test
    @DisplayName("getters mirror setters")
    void gettersAndSetters() {
        ErrorDefinition def = new ErrorDefinition();
        def.setId("GE-X999");
        def.setStage("SYNTAX");
        def.setLevel("ERROR");
        def.setCode("TEST");
        def.setMessage("test message {param}");
        assertEquals("GE-X999", def.getId());
        assertEquals("SYNTAX", def.getStage());
        assertEquals("ERROR", def.getLevel());
        assertEquals("TEST", def.getCode());
        assertEquals("test message {param}", def.getMessage());
    }
}
