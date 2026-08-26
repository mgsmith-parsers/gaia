package tools.pantheum.gaia.gs1.model;

import tools.pantheum.gaia.gs1.syntax.digitallink.DLPathRules;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Metadata extracted from a GS1 Digital Link URI.
 *
 * <p>A GS1 Digital Link URI encodes one or more GS1 Application Identifiers
 * directly in the structure of an HTTP(S) URL, for example:
 *
 * <pre>{@code
 * https://example.com/01/09506000134352/10/ABC?17=271231
 * }</pre>
 *
 * <p>The URL is composed of:
 * <ul>
 *   <li><b>scheme</b> — {@code http} or {@code https}</li>
 *   <li><b>domain</b> — the resolver domain (e.g. {@code example.com})</li>
 *   <li><b>path</b> — AI/value pairs encoded as path segments, where the first
 *       AI is the <em>primary key</em> (e.g. {@code /01/09506000134352}),
 *       optionally preceded by a custom path stem (§6.1.1) such as
 *       {@code /products} — see {@link #getCustomPathStem()}</li>
 *   <li><b>query</b> — additional AI/value pairs as query parameters
 *       (e.g. {@code 17=271231})</li>
 * </ul>
 *
 * <p>Instances are immutable. Construct via the
 * {@link #GS1DigitalLinkInfo(URI)} constructor with the input URI — the URL
 * and its scheme, domain, path, and query parts are derived from it.
 *
 * @see <a href="https://www.gs1.org/standards/gs1-digital-link">GS1 Digital Link standard</a>
 */
public final class GS1DigitalLinkInfo {

    private final URI uri;
    private final URL url;

    /**
     * Constructs a {@link GS1DigitalLinkInfo} from the input URI.
     *
     * @param uri the GS1 Digital Link URI as received; must not be {@code null}
     *            and must be convertible to an absolute {@link java.net.URL}
     * @throws IllegalArgumentException if {@code uri} is {@code null} or not a valid URL
     */
    public GS1DigitalLinkInfo(URI uri) {
        // TODO - Check if URI is compressed, if necessary decompress
        // TODO - Add indicators for compression and original compressed URI
        if (uri == null) throw new IllegalArgumentException("uri must not be null");
        this.uri = uri;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("uri is not a valid URL: " + uri, e);
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * The input URI exactly as received by the parser.
     * Example: {@code "https://example.com/01/09506000134352/10/ABC?17=271231"}
     */
    public URI getUri() { return uri; }

    /** The parsed GS1 Digital Link as a {@link java.net.URL}. Never {@code null}. */
    public URL getUrl() { return url; }

    /**
     * The URL scheme (protocol) — either {@code "http"} or {@code "https"}.
     */
    public String getScheme() { return url.getProtocol(); }

    /**
     * The resolver domain (host portion) of the URL, without the trailing slash.
     * Example: {@code "example.com"}
     */
    public String getDomain() { return url.getHost(); }

    /**
     * The path portion of the URL, including the leading slash. Contains the
     * AI/value path segments, optionally preceded by a custom path stem
     * (see {@link #getCustomPathStem()}).
     * Example: {@code "/01/09506000134352/10/ABC"}
     */
    public String getPath() { return url.getPath(); }

    /**
     * The custom path stem: the portion of the path between the domain and the
     * first GS1 Application Identifier (the primary identification key). A GS1
     * Digital Link URI may carry arbitrary custom path segments before the GS1
     * path (GS1 Digital Link: URI Syntax §6.1.1) — for example the
     * {@code "/products"} in {@code https://example.com/products/01/09506000134352}.
     *
     * <p>Returns {@code ""} (empty string) when the primary key is the first path
     * segment (no custom stem), and the whole path when no primary key is present.
     * The returned value includes a leading slash but no trailing slash.
     * Example: {@code "/products"}
     */
    public String getCustomPathStem() {
        // url.getPath() returns the raw (undecoded) path, matching the parser's segmentation.
        List<String> segments = DLPathRules.pathSegments(url.getPath());
        StringBuilder stem = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            // The primary key is the first AI followed by a value (mirrors DLSyntaxParser).
            if (i + 1 < segments.size() && DLPathRules.isPrimaryKey(segments.get(i))) {
                return stem.toString();
            }
            stem.append('/').append(segments.get(i));
        }
        return stem.toString();  // no primary key found — the whole path is the stem
    }

    /**
     * The raw query string of the URL, <em>excluding</em> the leading {@code '?'},
     * or {@code null} if the URL has no query component.
     * Example: {@code "17=271231&22=XYZ"}
     */
    public String getQuery() { return url.getQuery(); }

    /** Returns {@code true} if the URL contains a query component. */
    public boolean hasQuery() { return url.getQuery() != null && !url.getQuery().isEmpty(); }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "GS1DigitalLinkInfo{uri=\"" + uri + "\"}";
    }

}
