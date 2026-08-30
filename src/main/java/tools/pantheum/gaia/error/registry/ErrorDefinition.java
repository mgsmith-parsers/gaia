package tools.pantheum.gaia.error.registry;

/**
 * Deserialisation target for a single entry in the error message catalogue
 * ({@code errors/error_messages_EN.json}, {@code errors/error_messages_FR.json}, etc.).
 *
 * <p>Each definition carries the catalogue {@code id} (e.g. {@code "GE-S001"}),
 * the pipeline {@code stage} (e.g. {@code "SYNTAX"}), the {@code level} (e.g.
 * {@code "SYNTAX_ERROR"}), a machine-readable {@code code} token, and a
 * {@code message} template that may contain {@code {placeholders}} substituted
 * at error-creation time by {@link ErrorRegistry}.
 *
 * <p>Instances are created by Jackson during registry load; they are not
 * constructed directly by application code.
 */
public class ErrorDefinition {

    /** Creates a new {@link ErrorDefinition}. */
    public ErrorDefinition() {}

    private String id;
    private String stage;
    private String level;
    private String code;
    private String message;

    /**
     * Returns the id.
     *
     * @return the id.
     */
    public String getId()      { return id; }
    /**
     * Returns the stage.
     *
     * @return the stage.
     */
    public String getStage()   { return stage; }
    /**
     * Returns the level.
     *
     * @return the level.
     */
    public String getLevel()   { return level; }
    /**
     * Returns the code.
     *
     * @return the code.
     */
    public String getCode()    { return code; }
    /**
     * Returns the message.
     *
     * @return the message.
     */
    public String getMessage() { return message; }

    /**
     * Set id.
     *
     * @param id the id
     */
    public void setId(String id)           { this.id = id; }
    /**
     * Set stage.
     *
     * @param stage the stage
     */
    public void setStage(String stage)     { this.stage = stage; }
    /**
     * Set level.
     *
     * @param level the level
     */
    public void setLevel(String level)     { this.level = level; }
    /**
     * Set code.
     *
     * @param code the code
     */
    public void setCode(String code)       { this.code = code; }
    /**
     * Set message.
     *
     * @param message the message
     */
    public void setMessage(String message) { this.message = message; }
}
