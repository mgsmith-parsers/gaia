package tools.pantheum.gaia.gs1.content.validator;

import tools.pantheum.gaia.gs1.content.ContentInterface;
import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.error.GaiaError;
import tools.pantheum.gaia.error.registry.ErrorRegistry;

import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIComponentValue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Custom semantic validator for AI 8200 — Extended Packaging URL (PRODUCT URL).
 *
 * <p>The standard pipeline verifies that the value is 1–70 characters drawn
 * from the GS1 AI 82 character set. This validator applies the following
 * additional semantic rules:
 *
 * <ul>
 *   <li>The value must be a syntactically well-formed URI.</li>
 *   <li>The URI must have an explicit scheme.</li>
 *   <li>The scheme must be {@code http} or {@code https}.</li>
 *   <li>The URI must have a non-empty host.</li>
 * </ul>
 *
 * <p>No network access is performed; only structural validity is checked.
 */
public final class ProductUrlValidator implements ContentInterface {

    public static final ProductUrlValidator INSTANCE = new ProductUrlValidator();

    public ProductUrlValidator() {}

    static final String ERR_CODE_INVALID_URI = "GE-C159";
    static final String ERR_CODE_NO_SCHEME   = "GE-C160";
    static final String ERR_CODE_BAD_SCHEME  = "GE-C161";
    static final String ERR_CODE_NO_HOST     = "GE-C162";

    @Override
    public List<GaiaError> validate(GS1AIObjectElement element, GaiaConstants.Language language) {
        List<GS1AIComponentValue> components = element.getGS1ComponentValues();
        if (components.isEmpty()) return List.of();

        String value = components.get(0).getValue();
        if (value == null || value.isEmpty()) return List.of();

        URI uri;
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_INVALID_URI, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NO_SCHEME, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_BAD_SCHEME, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            return List.of(ErrorRegistry.INSTANCE.create(ERR_CODE_NO_HOST, element.getAi(), element.getPosition(), Map.of("ai", element.getAi(), "value", element.getValue()), language));
        }

        return List.of();
    }
}
