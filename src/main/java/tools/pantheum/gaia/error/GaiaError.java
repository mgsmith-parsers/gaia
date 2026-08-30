package tools.pantheum.gaia.error;

import tools.pantheum.gaia.GaiaConstants;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * An immutable validation error or advisory warning produced during GS1 element string parsing.
 *
 * <p>Each error carries a catalogue identifier ({@link #getId()}), a severity
 * {@link GaiaConstants.ErrorLevel}, a pipeline {@link GaiaConstants.ErrorStage}, a
 * machine-readable {@link #getCode()}, the Application Identifier that caused it
 * ({@link #getAi()}, may be {@code null}), a human-readable interpolated
 * {@link #getMessage()}, and the zero-based character {@link #getPosition()} within
 * the original input.
 *
 * <p>Instances are created by {@link tools.pantheum.gaia.error.registry.ErrorRegistry},
 * which interpolates the localized message from the catalogue. The constructor is public
 * only because the registry lives in a sub-package; prefer
 * {@code ErrorRegistry.INSTANCE.create(...)} over constructing directly, or the error
 * will bypass the catalogue and carry unlocalized text.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GaiaError {

    private final String id;
    private final GaiaConstants.ErrorLevel level;
    private final GaiaConstants.ErrorStage stage;
    private final String code;
    private final String ai;
    private final String message;
    private final int position;

    /**
     * Creates a new {@link GaiaError}.
     *
     * @param id the id
     * @param level the level
     * @param stage the stage
     * @param code the code
     * @param ai the AI
     * @param message the message
     * @param position the position
     */
    public GaiaError(String id, GaiaConstants.ErrorLevel level, GaiaConstants.ErrorStage stage,
                     String code, String ai, String message, int position) {
        this.id = id;
        this.level = level;
        this.stage = stage;
        this.code = code;
        this.ai = ai;
        this.message = message;
        this.position = position;
    }

    /**
     * Returns the id.
     *
     * @return the id.
     */
    public String getId()                      { return id; }
    /**
     * Returns the level.
     *
     * @return the level.
     */
    public GaiaConstants.ErrorLevel getLevel() { return level; }
    /**
     * Returns the stage.
     *
     * @return the stage.
     */
    public GaiaConstants.ErrorStage getStage() { return stage; }
    /**
     * Returns the code.
     *
     * @return the code.
     */
    public String getCode()                    { return code; }
    /**
     * Returns the AI.
     *
     * @return the AI.
     */
    public String getAi()                      { return ai; }
    /**
     * Returns the message.
     *
     * @return the message.
     */
    public String getMessage()                 { return message; }
    /**
     * Returns the position.
     *
     * @return the position.
     */
    public int getPosition()                   { return position; }

    @Override
    public String toString() {
        return "[" + id + "] " + level + " (" + stage + ")" +
               (ai != null ? " AI(" + ai + ")" : "") +
               " pos=" + position + ": " + message;
    }
}
