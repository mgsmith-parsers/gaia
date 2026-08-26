package tools.pantheum.gaia.error;

import java.util.Collections;
import java.util.List;

/**
 * Thrown by {@link tools.pantheum.gaia.GaiaBuilder} when the supplied AIs cannot
 * be assembled into a well-formed GS1 element string or Digital Link URI.
 *
 * <p>For content-validation failures (bad check digit, format mismatch, missing
 * required AI, …) {@link #getErrors()} carries the underlying {@link GaiaError}s
 * from the parser. For Digital Link structural failures (no primary key, invalid
 * key-qualifier sequence, banned AI) the message describes the problem and
 * {@link #getErrors()} may be empty.
 */
public class GaiaBuilderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient List<GaiaError> errors;

    public GaiaBuilderException(String message, List<GaiaError> errors) {
        super(message);
        this.errors = (errors == null) ? Collections.emptyList()
                                       : Collections.unmodifiableList(errors);
    }

    /** The validation errors that caused the build to fail; never {@code null}, possibly empty. */
    public List<GaiaError> getErrors() { return errors; }
}
