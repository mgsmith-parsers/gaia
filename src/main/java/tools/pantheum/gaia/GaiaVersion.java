package tools.pantheum.gaia;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides the build version and timestamp of the Gaia library, sourced from
 * {@code gaia-version.properties} which is populated at build time by Maven
 * resource filtering.
 *
 * <p>Values are loaded once at class initialisation and cached. If the resource
 * cannot be found (e.g. running unpackaged from an IDE without first running
 * {@code mvn process-resources}), both fields fall back to {@code "unknown"}.
 */
public final class GaiaVersion {

    /** Library version, e.g. {@code "1.0.0-SNAPSHOT"} or {@code "1.2.3"}. */
    public static final String VERSION   = load("version");

    /** ISO-8601 build timestamp, e.g. {@code "2026-06-07T12:00:00Z"}. */
    public static final String TIMESTAMP = load("build.timestamp");

    private GaiaVersion() {}

    // -------------------------------------------------------------------------

    private static String load(String key) {
        try (InputStream is = GaiaVersion.class.getResourceAsStream("/gaia-version.properties")) {
            if (is == null) return "unknown";
            Properties props = new Properties();
            props.load(is);
            String value = props.getProperty(key, "unknown").trim();
            return value.isEmpty() ? "unknown" : value;
        } catch (IOException e) {
            return "unknown";
        }
    }
}
