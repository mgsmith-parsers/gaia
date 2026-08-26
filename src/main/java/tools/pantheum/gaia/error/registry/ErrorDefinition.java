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

    private String id;
    private String stage;
    private String level;
    private String code;
    private String message;

    public String getId()      { return id; }
    public String getStage()   { return stage; }
    public String getLevel()   { return level; }
    public String getCode()    { return code; }
    public String getMessage() { return message; }

    public void setId(String id)           { this.id = id; }
    public void setStage(String stage)     { this.stage = stage; }
    public void setLevel(String level)     { this.level = level; }
    public void setCode(String code)       { this.code = code; }
    public void setMessage(String message) { this.message = message; }
}
