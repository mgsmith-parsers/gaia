package tools.pantheum.gaia.gs1.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Percent-encoding (RFC 3986) decoding helpers for GS1 {@code pcenc} text fields
 * and GS1 Digital Link URI values.
 *
 * <p>GS1 uses percent-encoding to allow non-ASCII characters in element strings.
 * Multi-byte UTF-8 characters are encoded as consecutive {@code %XX} sequences,
 * e.g. the Euro sign {@code €} becomes {@code %E2%82%AC} and {@code é} becomes
 * {@code %C3%A9}.
 *
 * <p>{@link #decode(String)} throws {@link IllegalArgumentException}
 * on a malformed {@code %XX} sequence — used by Digital Link URI parsing, where a
 * bad sequence is a structural error. Callers that want lenient decoding (returning
 * the raw value on malformed input) wrap it in a try/catch. Decoding is pure
 * RFC 3986: a literal {@code '+'} is left unchanged (it is <em>not</em> form-encoding,
 * so {@code '+'} is not a space). Callers that layer the GS1 healthcare
 * {@code +}-as-space convention on top apply it themselves after decoding.
 */
public final class PcEncUtils {

    private PcEncUtils() {}

    /**
     * Percent-decodes {@code value} as UTF-8 per RFC 3986, throwing on a malformed
     * sequence. Only {@code %XX} octets are decoded; every other character —
     * including a literal {@code '+'} — is passed through unchanged.
     *
     * @param value the raw pcenc value
     * @return the decoded string
     * @throws IllegalArgumentException if {@code value} contains an incomplete or
     *         non-hexadecimal {@code %XX} sequence
     */
    public static String decode(String value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '%') {
                if (i + 2 >= value.length()
                        || !CharUtils.isHexDigit(value.charAt(i + 1))
                        || !CharUtils.isHexDigit(value.charAt(i + 2)))
                    throw new IllegalArgumentException("Malformed percent-encoding: " + value);
                out.write(Integer.parseInt(value, i + 1, i + 3, 16));
                i += 2;
            } else {
                for (byte b : String.valueOf(c).getBytes(StandardCharsets.UTF_8)) out.write(b);
            }
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    /**
     * Percent-encodes {@code value} for inclusion in a GS1 Digital Link URI
     * (GS1 Digital Link Standard: URI Syntax §4.2): RFC 3986 unreserved characters
     * ({@code A-Z a-z 0-9 - . _ ~}) are kept literal; every other character is
     * percent-encoded as its UTF-8 octets.
     *
     * @param value the raw value to encode
     * @return the percent-encoded value
     */
    public static String encode(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                    || c == '-' || c == '.' || c == '_' || c == '~') {
                sb.append(c);
            } else {
                for (byte b : String.valueOf(c).getBytes(StandardCharsets.UTF_8)) {
                    sb.append('%').append(String.format("%02X", b & 0xFF));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Returns {@code true} if {@code value} contains at least one well-formed
     * percent-encoded octet — a {@code '%'} followed by two hexadecimal digits.
     * A bare {@code '%'} that is not followed by two hex digits does not count.
     *
     * @param value the string to test; may be {@code null}
     * @return a new {@code boolean}
     */
    public static boolean hasPercentEncoding(String value) {
        if (value == null) return false;
        for (int i = 0; i + 2 < value.length(); i++) {
            if (value.charAt(i) == '%'
                    && CharUtils.isHexDigit(value.charAt(i + 1))
                    && CharUtils.isHexDigit(value.charAt(i + 2))) {
                return true;
            }
        }
        return false;
    }
}
