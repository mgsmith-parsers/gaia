package tools.pantheum.gaia.gs1.syntax.ai;

import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.util.List;

/** Carries the output of {@link AISyntaxParser#walkComponents}: structural errors and sliced component values. */
class AIComponentWalkResult {

    final List<GaiaError>         errors;
    final List<GS1AIComponentValue> componentValues;

    AIComponentWalkResult(List<GaiaError> errors, List<GS1AIComponentValue> componentValues) {
        this.errors          = errors;
        this.componentValues = componentValues;
    }
}
