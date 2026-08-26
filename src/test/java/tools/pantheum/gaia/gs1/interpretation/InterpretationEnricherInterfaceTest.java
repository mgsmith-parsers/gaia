package tools.pantheum.gaia.gs1.interpretation;

import tools.pantheum.gaia.gs1.interpretation.registry.InterpretationRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests for {@link InterpretationEnricherInterface} — enrichers are
 * resolved by simple class name through {@link InterpretationRegistry}.
 */
@DisplayName("InterpretationEnricherInterface contract")
class InterpretationEnricherInterfaceTest {

    @Test
    @DisplayName("a registered enricher resolves and implements the interface")
    void resolvesRegisteredEnricher() {
        InterpretationEnricherInterface enricher =
                InterpretationRegistry.INSTANCE.enricherFor("SSCCEnricher").orElse(null);
        assertNotNull(enricher, "SSCCEnricher must be resolvable");
    }

    @Test
    @DisplayName("an unknown enricher name resolves to empty")
    void unknownEnricherEmpty() {
        assertTrue(InterpretationRegistry.INSTANCE.enricherFor("NoSuchEnricher").isEmpty());
    }
}
