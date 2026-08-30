package tools.pantheum.gaia.gs1.interpretation.enricher;

import tools.pantheum.gaia.gs1.constants.GS1Constants_Enricher;
import tools.pantheum.gaia.gs1.model.GS1AIObjectElement;
import tools.pantheum.gaia.gs1.model.GS1AIInterpretation;
import tools.pantheum.gaia.gs1.interpretation.InterpretationEnricherInterface;
import tools.pantheum.gaia.gs1.registry.AiDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enriches a GS1 {@code latlong} geographic coordinate value (AI 4309 — SHIP TO GEO)
 * by decoding the 20-digit encoded value into human-readable latitude, longitude
 * and combined coordinate interpretations in both decimal degrees (DD) and
 * degrees/minutes/seconds (DMS) formats.
 *
 * <h2>Encoding</h2>
 * The 20-digit fixed-length value is structured as two 10-digit fields:
 * <pre>
 *   Digits  0– 9 : encoded latitude  = (latitude  + 90)  × 10⁷
 *   Digits 10–19 : encoded longitude = (longitude + 180) × 10⁷
 * </pre>
 * Latitude range:  −90.0000000 to +90.0000000 degrees<br>
 * Longitude range: −180.0000000 to +180.0000000 degrees
 *
 * <h2>Produced interpretations</h2>
 * <ul>
 *   <li>{@code LATITUDE}          — decimal degrees, e.g. {@code "-33.8688197"}</li>
 *   <li>{@code LONGITUDE}         — decimal degrees, e.g. {@code "151.2092955"}</li>
 *   <li>{@code GEO_COORDINATES}   — combined decimal degrees,
 *       e.g. {@code "-33.8688197,151.2092955"}</li>
 *   <li>{@code LATITUDE_DMS}      — degrees/minutes/seconds with direction,
 *       e.g. {@code "33°52'7.75\"S"}</li>
 *   <li>{@code LONGITUDE_DMS}     — degrees/minutes/seconds with direction,
 *       e.g. {@code "151°12'33.46\"E"}</li>
 *   <li>{@code GEO_COORDINATES_DMS} — combined DMS,
 *       e.g. {@code "33°52'7.75\"S 151°12'33.46\"E"}</li>
 * </ul>
 * Returns an empty list if the value is null, not 20 digits, or cannot be parsed.
 */
public final class GeoCoordinateEnricher implements InterpretationEnricherInterface {

    /** Creates a new {@link GeoCoordinateEnricher}. */
    public GeoCoordinateEnricher() {}

    private static final long SCALE = 10_000_000L; // 10⁷

    @Override
    public List<GS1AIInterpretation> enrich(String baseValue, AiDefinition aiDefinition, GS1AIObjectElement element) {
        if (baseValue == null || baseValue.length() != 20) return Collections.emptyList();

        try {
            long encodedLat = Long.parseLong(baseValue.substring(0, 10));
            long encodedLon = Long.parseLong(baseValue.substring(10, 20));

            double latitude  = (encodedLat / (double) SCALE) - 90.0;
            double longitude = (encodedLon / (double) SCALE) - 180.0;

            String latDD  = formatDecimalDegrees(latitude);
            String lonDD  = formatDecimalDegrees(longitude);
            String latDMS = toDMS(latitude,  true);
            String lonDMS = toDMS(longitude, false);

            List<GS1AIInterpretation> results = new ArrayList<>(6);
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.LATITUDE,            null,            latDD));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.LONGITUDE,           null,           lonDD));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.GEO_COORDINATES,     null,     latDD + "," + lonDD));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.LATITUDE_DMS,        null,        latDMS));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.LONGITUDE_DMS,       null,       lonDMS));
            results.add(new GS1AIInterpretation(GS1Constants_Enricher.GEO_COORDINATES_DMS, null, latDMS + ", " + lonDMS));
            return results;

        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Formats a decimal degree value to 7 decimal places, stripping trailing
     * zeros but keeping at least one decimal place.
     * e.g. {@code -33.8688197}, {@code 151.2092955}, {@code -90.0}
     */
    static String formatDecimalDegrees(double value) {
        String s = String.format("%.7f", value);
        s = s.replaceAll("0+$", "");
        if (s.endsWith(".")) s += "0";
        return s;
    }

    /**
     * Converts a decimal degree value to DMS notation with a directional
     * indicator (N/S for latitude, E/W for longitude).
     *
     * <p>Format: {@code DD°MM'SS.SS"N}
     *
     * @param value     decimal degrees (negative = S or W)
     * @param isLatitude {@code true} for latitude (N/S), {@code false} for longitude (E/W)
     */
    static String toDMS(double value, boolean isLatitude) {
        String direction;
        if (isLatitude) {
            direction = value >= 0 ? "N" : "S";
        } else {
            direction = value >= 0 ? "E" : "W";
        }

        double abs     = Math.abs(value);
        int    degrees = (int) abs;
        double minFull = (abs - degrees) * 60.0;
        int    minutes = (int) minFull;
        double seconds = (minFull - minutes) * 60.0;

        // Format seconds to 2 decimal places, strip trailing zeros
        String secStr = String.format("%.2f", seconds);
        secStr = secStr.replaceAll("0+$", "");
        if (secStr.endsWith(".")) secStr += "0";

        return degrees + "°" + minutes + "'" + secStr + "\"" + direction;
    }
}
