package tools.pantheum.gaia.gs1.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unit-of-measure constants for measurement AIs, split out of
 * {@link tools.pantheum.gaia.gs1.interpretation.enricher.ISOUnitEnricher}.
 *
 * <p>Unit codes follow UN/ECE Recommendation 20 (also aligned with ISO 80000).
 * Used by AIs in the 31nn, 32nn, 33nn, 34nn, 35nn and 36nn ranges, where the
 * first three digits of the AI identify the physical quantity and its unit.
 */
public final class GS1Constants_UOM {

    private GS1Constants_UOM() {}

    // -------------------------------------------------------------------------
    // UN/ECE Recommendation 20 unit constants  { code, name }
    // -------------------------------------------------------------------------

    private static final String[] KILOGRAM              = { "KGM", "Kilogram" };
    private static final String[] METRE                 = { "MTR", "Metre" };
    private static final String[] SQUARE_METRE          = { "MTK", "Square metre" };
    private static final String[] LITRE                 = { "LTR", "Litre" };
    private static final String[] CUBIC_METRE           = { "MTQ", "Cubic metre" };
    private static final String[] KG_PER_SQUARE_METRE   = { "28",  "Kilogram per square metre" };
    private static final String[] POUND                 = { "LBR", "Pound" };
    private static final String[] INCH                  = { "INH", "Inch" };
    private static final String[] FOOT                  = { "FOT", "Foot" };
    private static final String[] YARD                  = { "YRD", "Yard" };
    private static final String[] SQUARE_INCH           = { "INK", "Square inch" };
    private static final String[] SQUARE_FOOT           = { "FTK", "Square foot" };
    private static final String[] SQUARE_YARD           = { "YDK", "Square yard" };
    private static final String[] TROY_OUNCE            = { "APZ", "Troy ounce" };
    private static final String[] FLUID_OUNCE_US        = { "OZI", "Fluid ounce (US)" };
    private static final String[] QUART_US              = { "QTI", "Quart (US liquid)" };
    private static final String[] GALLON_US             = { "GLL", "Gallon (US)" };
    private static final String[] CUBIC_INCH            = { "INQ", "Cubic inch" };
    private static final String[] CUBIC_FOOT            = { "FTQ", "Cubic foot" };
    private static final String[] CUBIC_YARD            = { "YDQ", "Cubic yard" };

    // -------------------------------------------------------------------------
    // Unit map — first 3 digits of AI → unit constant
    // -------------------------------------------------------------------------

    /**
     * Maps the first 3 digits of the AI code to a unit constant.
     * Source: UN/ECE Recommendation 20 / GS1 General Specifications.
     */
    public static final Map<String, String[]> UNIT_MAP;

    static {
        Map<String, String[]> m = new LinkedHashMap<>();

        // ---- 31nn — metric weights and measures ----
        m.put("310", KILOGRAM);            // NET WEIGHT (kg)
        m.put("311", METRE);               // LENGTH (m)
        m.put("312", METRE);               // WIDTH (m)
        m.put("313", METRE);               // HEIGHT (m)
        m.put("314", SQUARE_METRE);        // AREA (m²)
        m.put("315", LITRE);               // NET VOLUME (l)
        m.put("316", CUBIC_METRE);         // NET VOLUME (m³)

        // ---- 32nn — imperial/US lengths and weight ----
        m.put("320", POUND);               // NET WEIGHT (lb)
        m.put("321", INCH);                // LENGTH (in)
        m.put("322", FOOT);                // LENGTH (ft)
        m.put("323", YARD);                // LENGTH (yd)
        m.put("324", INCH);                // WIDTH (in)
        m.put("325", FOOT);                // WIDTH (ft)
        m.put("326", YARD);                // WIDTH (yd)
        m.put("327", INCH);                // HEIGHT (in)
        m.put("328", FOOT);                // HEIGHT (ft)
        m.put("329", YARD);                // HEIGHT (yd)

        // ---- 33nn — metric gross weights, log measures, kg/m² ----
        m.put("330", KILOGRAM);            // GROSS WEIGHT (kg)
        m.put("331", METRE);               // LENGTH (m), log
        m.put("332", METRE);               // WIDTH (m), log
        m.put("333", METRE);               // HEIGHT (m), log
        m.put("334", SQUARE_METRE);        // AREA (m²), log
        m.put("335", LITRE);               // VOLUME (l), log
        m.put("336", CUBIC_METRE);         // VOLUME (m³), log
        m.put("337", KG_PER_SQUARE_METRE); // KG PER m²

        // ---- 34nn — imperial/US gross weights and log measures ----
        m.put("340", POUND);               // GROSS WEIGHT (lb)
        m.put("341", INCH);                // LENGTH (in), log
        m.put("342", FOOT);                // LENGTH (ft), log
        m.put("343", YARD);                // LENGTH (yd), log
        m.put("344", INCH);                // WIDTH (in), log
        m.put("345", FOOT);                // WIDTH (ft), log
        m.put("346", YARD);                // WIDTH (yd), log
        m.put("347", INCH);                // HEIGHT (in), log
        m.put("348", FOOT);                // HEIGHT (ft), log
        m.put("349", YARD);                // HEIGHT (yd), log

        // ---- 35nn — imperial areas, troy oz, fluid oz ----
        m.put("350", SQUARE_INCH);         // AREA (in²)
        m.put("351", SQUARE_FOOT);         // AREA (ft²)
        m.put("352", SQUARE_YARD);         // AREA (yd²)
        m.put("353", SQUARE_INCH);         // AREA (in²), log
        m.put("354", SQUARE_FOOT);         // AREA (ft²), log
        m.put("355", SQUARE_YARD);         // AREA (yd²), log
        m.put("356", TROY_OUNCE);          // NET WEIGHT (troy oz)
        m.put("357", FLUID_OUNCE_US);      // NET VOLUME (oz)

        // ---- 36nn — US liquid volumes ----
        m.put("360", QUART_US);            // NET VOLUME (qt)
        m.put("361", GALLON_US);           // NET VOLUME (gal.)
        m.put("362", QUART_US);            // VOLUME (qt), log
        m.put("363", GALLON_US);           // VOLUME (gal.), log
        m.put("364", CUBIC_INCH);          // NET VOLUME (in³)
        m.put("365", CUBIC_FOOT);          // NET VOLUME (ft³)
        m.put("366", CUBIC_YARD);          // NET VOLUME (yd³)
        m.put("367", CUBIC_INCH);          // VOLUME (in³), log
        m.put("368", CUBIC_FOOT);          // VOLUME (ft³), log
        m.put("369", CUBIC_YARD);          // VOLUME (yd³), log

        UNIT_MAP = Collections.unmodifiableMap(m);
    }
}
