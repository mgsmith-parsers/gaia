package tools.pantheum.gaia.gs1.syntax;

import tools.pantheum.gaia.gs1.syntax.ai.AISyntaxParser;

import tools.pantheum.gaia.config.ParseConfig;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.registry.AiDefinitionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link SyntaxValidator} — duplicates, requires, excludes (GE-S004/5/6). */
@DisplayName("SyntaxValidator")
class SyntaxValidatorTest {

    private final AiDefinitionRegistry registry = AiDefinitionRegistry.getInstance();
    private final AISyntaxParser parser = new AISyntaxParser(registry);
    private final SyntaxValidator validator = new SyntaxValidator(registry);
    private final ParseConfig config = ParseConfig.defaultConfig();

    private List<GS1AIObjectElement> elements(String input) {
        return parser.parse(input, config).getElements();
    }

    @Test
    @DisplayName("duplicate AIs attach GE-S004 to the repeated element")
    void duplicateAi() {
        List<GS1AIObjectElement> els = elements("01095060001343520109506000134352");
        validator.validate(els, config);
        assertTrue(els.get(1).getErrors().stream().anyMatch(e -> "GE-S004".equals(e.getId())));
    }

    @Test
    @DisplayName("missing required AIs attach GE-S005")
    void missingRequired() {
        List<GS1AIObjectElement> els = elements("8111" + "0001"); // requires (255)
        validator.validate(els, config);
        assertTrue(els.get(0).getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId())));
    }

    @Test
    @DisplayName("excluded pairings attach GE-S006")
    void excludedPairing() {
        // AI (01) excludes AI (02)
        List<GS1AIObjectElement> els = elements("01095060001343520209506000134352");
        validator.validate(els, config);
        assertTrue(els.stream().anyMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId()))));
    }

    @Test
    @DisplayName("a clean element set passes without errors")
    void cleanSetPasses() {
        List<GS1AIObjectElement> els = elements("0109506000134352" + "10LOT1");
        validator.validate(els, config);
        assertTrue(els.stream().noneMatch(GS1AIObjectElement::hasErrors));
    }

    @Test
    @DisplayName("skipRequiresCheck suppresses GE-S005 but keeps GE-S006")
    void skipRequiresOnly() {
        ParseConfig cfg = ParseConfig.builder().skipRequiresCheck(true).build();

        // requires check would fire GE-S005; must be absent when skipped
        List<GS1AIObjectElement> requiresEls = elements("8111" + "0001"); // requires (255)
        validator.validate(requiresEls, cfg);
        assertTrue(requiresEls.stream().noneMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId()))));

        // excludes check must still fire GE-S006
        List<GS1AIObjectElement> excludesEls = elements("01095060001343520209506000134352");
        validator.validate(excludesEls, cfg);
        assertTrue(excludesEls.stream().anyMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId()))));
    }

    @Test
    @DisplayName("skipExcludesCheck suppresses GE-S006 but keeps GE-S005")
    void skipExcludesOnly() {
        ParseConfig cfg = ParseConfig.builder().skipExcludesCheck(true).build();

        // excludes check would fire GE-S006; must be absent when skipped
        List<GS1AIObjectElement> excludesEls = elements("01095060001343520209506000134352");
        validator.validate(excludesEls, cfg);
        assertTrue(excludesEls.stream().noneMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId()))));

        // requires check must still fire GE-S005
        List<GS1AIObjectElement> requiresEls = elements("8111" + "0001"); // requires (255)
        validator.validate(requiresEls, cfg);
        assertTrue(requiresEls.stream().anyMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId()))));
    }

    @Test
    @DisplayName("skipping both suppresses GE-S005 and GE-S006 while GE-S004 still fires")
    void skipBoth() {
        ParseConfig cfg = ParseConfig.builder()
                .skipRequiresCheck(true)
                .skipExcludesCheck(true)
                .build();

        List<GS1AIObjectElement> requiresEls = elements("8111" + "0001");
        validator.validate(requiresEls, cfg);
        assertTrue(requiresEls.stream().noneMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S005".equals(e.getId()))));

        List<GS1AIObjectElement> excludesEls = elements("01095060001343520209506000134352");
        validator.validate(excludesEls, cfg);
        assertTrue(excludesEls.stream().noneMatch(
                el -> el.getErrors().stream().anyMatch(e -> "GE-S006".equals(e.getId()))));

        // duplicate-AI check (GE-S004) is unaffected by the skip flags
        List<GS1AIObjectElement> dupEls = elements("01095060001343520109506000134352");
        validator.validate(dupEls, cfg);
        assertTrue(dupEls.get(1).getErrors().stream().anyMatch(e -> "GE-S004".equals(e.getId())));
    }
}
