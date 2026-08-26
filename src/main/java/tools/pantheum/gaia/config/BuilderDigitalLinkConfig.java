package tools.pantheum.gaia.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Options controlling how {@link tools.pantheum.gaia.GaiaBuilder#buildDigitalLinkUri(BuilderDigitalLinkConfig)}
 * renders a GS1 Digital Link URI.
 *
 * <p>Immutable; constructed via {@link #builder()}. {@link #canonical()} is the
 * default — {@code https://id.gs1.org} with no path prefix, extra query
 * parameters, or fragment, matching {@code GS1AIObject.getCanonicalDigitalLink()}.
 *
 * <pre>{@code
 * BuilderDigitalLinkConfig cfg = BuilderDigitalLinkConfig.builder()
 *         .baseUrl("https://example.com/resolver")   // sets scheme, domain, and path prefix
 *         .addQueryParam("context", "retail")        // appended after the AI data attributes
 *         .fragment("section-2")
 *         .build();
 * }</pre>
 */
public final class BuilderDigitalLinkConfig {

    private static final BuilderDigitalLinkConfig CANONICAL = builder().build();

    private final String scheme;
    private final String domain;
    private final String pathPrefix;
    private final List<Map.Entry<String, String>> extraQueryParams;
    private final String fragment;

    private BuilderDigitalLinkConfig(String scheme, String domain, String pathPrefix,
                              List<Map.Entry<String, String>> extraQueryParams, String fragment) {
        this.scheme           = scheme;
        this.domain           = domain;
        this.pathPrefix       = pathPrefix;
        this.extraQueryParams = Collections.unmodifiableList(extraQueryParams);
        this.fragment         = fragment;
    }

    /** The canonical configuration: {@code https://id.gs1.org}, no extras. */
    public static BuilderDigitalLinkConfig canonical()     { return CANONICAL; }

    /** Alias for {@link #canonical()}. */
    public static BuilderDigitalLinkConfig defaultConfig() { return CANONICAL; }

    public static Builder builder() { return new Builder(); }

    /** URI scheme, {@code "http"} or {@code "https"}. */
    public String getScheme()     { return scheme; }

    /** Authority (host, optionally {@code host:port}), e.g. {@code "id.gs1.org"}. */
    public String getDomain()     { return domain; }

    /** Path segments before the first primary key, e.g. {@code "/resolver"}; empty when none. */
    public String getPathPrefix() { return pathPrefix; }

    /** Additional query parameters appended after the AI data-attribute parameters, in insertion order. */
    public List<Map.Entry<String, String>> getExtraQueryParams() { return extraQueryParams; }

    /** URL fragment (without the leading {@code #}), or {@code null} if none. */
    public String getFragment()   { return fragment; }

    /** Mutable builder for {@link BuilderDigitalLinkConfig}. */
    public static final class Builder {
        private String scheme = "https";
        private String domain = "id.gs1.org";
        private String pathPrefix = "";
        private final List<Map.Entry<String, String>> extra = new ArrayList<>();
        private String fragment = null;

        /** Sets the scheme; must be {@code http} or {@code https} (validated at {@link #build()}). */
        public Builder scheme(String scheme)   { if (scheme != null) this.scheme = scheme; return this; }

        /** Sets the authority (host or {@code host:port}). */
        public Builder domain(String domain)   { if (domain != null) this.domain = domain; return this; }

        /** Sets path segments placed before the first primary key (leading/trailing slashes normalised). */
        public Builder pathPrefix(String path) { this.pathPrefix = normalizePrefix(path); return this; }

        /** Adds a query parameter appended after the AI data attributes. Values are percent-encoded by the builder. */
        public Builder addQueryParam(String key, String value) {
            extra.add(Map.entry(key, value == null ? "" : value));
            return this;
        }

        /** Sets the URL fragment (without {@code #}). */
        public Builder fragment(String fragment) { this.fragment = fragment; return this; }

        /**
         * Convenience that splits a base URL such as {@code "https://example.com/resolver"}
         * into its {@link #scheme(String)}, {@link #domain(String)}, and {@link #pathPrefix(String)}.
         */
        public Builder baseUrl(String baseUrl) {
            if (baseUrl == null) return this;
            URI u = URI.create(baseUrl.trim());
            if (u.getScheme() != null) this.scheme = u.getScheme();
            if (u.getAuthority() != null && !u.getAuthority().isEmpty()) this.domain = u.getAuthority();
            this.pathPrefix = normalizePrefix(u.getPath());
            return this;
        }

        public BuilderDigitalLinkConfig build() {
            String sc = scheme.toLowerCase(Locale.ROOT);
            if (!sc.equals("http") && !sc.equals("https")) {
                throw new IllegalArgumentException("Digital Link scheme must be http or https, was: " + scheme);
            }
            if (domain == null || domain.isBlank()) {
                throw new IllegalArgumentException("Digital Link domain must not be empty");
            }
            return new BuilderDigitalLinkConfig(sc, domain.trim(), pathPrefix, new ArrayList<>(extra), fragment);
        }

        private static String normalizePrefix(String p) {
            if (p == null) return "";
            String t = p.trim();
            if (t.isEmpty() || t.equals("/")) return "";
            if (!t.startsWith("/")) t = "/" + t;
            while (t.endsWith("/")) t = t.substring(0, t.length() - 1);
            return t;
        }
    }
}
