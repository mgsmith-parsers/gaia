package tools.pantheum.gaia.gs1.util;

/**
 * Shared stderr warning format for classpath resources that fail to load at
 * class-initialisation time, either because the resource is missing or because
 * reading/parsing it failed.
 *
 * <p>Only for loaders that intentionally fail open (the enrichment datasets:
 * {@code Iso3166Data}, {@code Iso4217Data}, {@code AsfisData}). Structural
 * registries whose data is required for correct parsing must instead throw
 * {@code IllegalStateException} on load failure.
 */
public final class ResourceLoadLogger {

    private ResourceLoadLogger() {}

    /**
     * Logs that {@code resourcePath} was not found on the classpath.
     *
     * @param tag          the reporting class's simple name, e.g. {@code "Iso3166Data"}
     * @param resourcePath the classpath resource path that was looked up
     * @param consequence  what happens as a result, e.g. {@code "ISO 3166 lookup disabled"}
     */
    public static void resourceNotFound(String tag, String resourcePath, String consequence) {
        System.err.println("[" + tag + "] WARNING: " + resourcePath + " not found on classpath — " + consequence);
    }

    /**
     * Logs that {@code resourcePath} was found but failed to load or parse.
     *
     * @param tag          the reporting class's simple name, e.g. {@code "Iso3166Data"}
     * @param resourcePath the classpath resource path that failed to load
     * @param reason       the underlying failure message, e.g. {@code IOException#getMessage()}
     */
    public static void loadFailed(String tag, String resourcePath, String reason) {
        System.err.println("[" + tag + "] WARNING: Failed to load " + resourcePath + ": " + reason);
    }
}
