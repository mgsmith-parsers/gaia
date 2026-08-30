package tools.pantheum.gaia.result;

import tools.pantheum.gaia.error.GaiaError;

import java.util.List;

/**
 * Outcome of a {@code GaiaBuilder.tryBuild*} call: either a success carrying the
 * rendered string, or a failure carrying the message and validation errors that a
 * throwing build would have raised via
 * {@link tools.pantheum.gaia.error.GaiaBuilderException}.
 *
 * <pre>{@code
 * BuildResult r = GaiaBuilder.create().ai("01", value).tryBuildElementString();
 * if (r.isSuccess()) {
 *     use(r.getValue());
 * } else {
 *     report(r.getMessage(), r.getErrors());
 * }
 * }</pre>
 *
 * <p>Immutable.
 */
public final class BuildResult {

    private final boolean success;
    private final String value;
    private final String message;
    private final List<GaiaError> errors;
    private final ProcessingTiming timing;

    private BuildResult(boolean success, String value, String message, List<GaiaError> errors,
                        ProcessingTiming timing) {
        this.success = success;
        this.value   = value;
        this.message = message;
        this.errors  = (errors == null) ? List.of() : List.copyOf(errors);
        this.timing  = timing;
    }

    /**
     * Creates a successful result carrying the rendered output.
     *
     * @param value the value
     * @return a new {@code BuildResult}
     */
    public static BuildResult success(String value) {
        return new BuildResult(true, value, null, List.of(), null);
    }

    /**
     * Creates a failed result carrying the failure description and validation errors.
     *
     * @param message the message
     * @param errors the errors
     * @return a new {@code BuildResult}
     */
    public static BuildResult failure(String message, List<GaiaError> errors) {
        return new BuildResult(false, null, message, errors, null);
    }

    /**
     * Returns a copy of this result with the given {@link ProcessingTiming} attached.
     * All other fields are unchanged.
     *
     * @param timing the processing timing to attach; may be {@code null} to clear it
     * @return a new {@link BuildResult}; never {@code null}
     */
    public BuildResult withTiming(ProcessingTiming timing) {
        return new BuildResult(success, value, message, errors, timing);
    }

    /**
     * Whether the build succeeded.
     *
     * @return {@code true} if this element is success.
     */
    public boolean isSuccess() { return success; }

    /**
     * The rendered element string or Digital Link URI on success; {@code null} on failure.
     *
     * @return the value.
     */
    public String getValue() { return value; }

    /**
     * The failure description on failure; {@code null} on success.
     *
     * @return the message.
     */
    public String getMessage() { return message; }

    /**
     * The validation errors on failure; never {@code null}, always empty on success.
     *
     * @return the errors.
     */
    public List<GaiaError> getErrors() { return errors; }

    /**
     * The processing timing (start, completion, elapsed) for the build that produced this
     * result, or {@code null} if no timing was recorded.
     *
     * @return the timing.
     */
    public ProcessingTiming getTiming() { return timing; }
}
