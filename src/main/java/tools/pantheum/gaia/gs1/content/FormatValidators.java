package tools.pantheum.gaia.gs1.content;

import tools.pantheum.gaia.gs1.content.componentformat.HhmmValidator;
import tools.pantheum.gaia.gs1.content.componentformat.IbanValidator;
import tools.pantheum.gaia.gs1.content.componentformat.ImporterIdxValidator;
import tools.pantheum.gaia.gs1.content.componentformat.Iso3166Alpha2Validator;
import tools.pantheum.gaia.gs1.content.componentformat.Iso3166ListValidator;
import tools.pantheum.gaia.gs1.content.componentformat.Iso3166Validator;
import tools.pantheum.gaia.gs1.content.componentformat.Iso4217Validator;
import tools.pantheum.gaia.gs1.content.componentformat.Iso5218Validator;
import tools.pantheum.gaia.gs1.content.componentformat.LatLongValidator;
import tools.pantheum.gaia.gs1.content.componentformat.MmoptssValidator;
import tools.pantheum.gaia.gs1.content.componentformat.NonzeroValidator;
import tools.pantheum.gaia.gs1.content.componentformat.NozeroPrefixValidator;
import tools.pantheum.gaia.gs1.content.componentformat.PcencValidator;
import tools.pantheum.gaia.gs1.content.componentformat.PieceOfTotalValidator;
import tools.pantheum.gaia.gs1.content.componentformat.PosInSeqSlashValidator;
import tools.pantheum.gaia.gs1.content.componentformat.WindingValidator;
import tools.pantheum.gaia.gs1.content.componentformat.YesNoValidator;
import tools.pantheum.gaia.gs1.content.componentformat.Yymmd0Validator;
import tools.pantheum.gaia.gs1.content.componentformat.YymmddHhValidator;
import tools.pantheum.gaia.gs1.content.componentformat.YymmddValidator;
import tools.pantheum.gaia.gs1.content.componentformat.YyyymmddValidator;
import tools.pantheum.gaia.gs1.content.componentformat.ZeroValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maps GS1 component {@code format} names to their
 * {@link ComponentFormatInterface} implementations.
 *
 * <p>Each validator lives in its own class under
 * {@code tools.pantheum.gaia.gs1.content.componentformat} and is registered here as a singleton.
 * Look up a validator with {@link #forFormat(String)}.
 *
 * <h2>Registered formats</h2>
 * <table border="1">
 *   <caption>Component format names and their validators</caption>
 *   <tr><th>Format</th><th>Validator</th></tr>
 *   <tr><td>hhmm</td>         <td>{@link HhmmValidator}</td></tr>
 *   <tr><td>iban</td>         <td>{@link IbanValidator}</td></tr>
 *   <tr><td>importeridx</td>  <td>{@link ImporterIdxValidator}</td></tr>
 *   <tr><td>iso3166</td>      <td>{@link Iso3166Validator}</td></tr>
 *   <tr><td>iso3166alpha2</td><td>{@link Iso3166Alpha2Validator}</td></tr>
 *   <tr><td>iso3166list</td>  <td>{@link Iso3166ListValidator}</td></tr>
 *   <tr><td>iso4217</td>      <td>{@link Iso4217Validator}</td></tr>
 *   <tr><td>iso5218</td>      <td>{@link Iso5218Validator}</td></tr>
 *   <tr><td>latlong</td>      <td>{@link LatLongValidator}</td></tr>
 *   <tr><td>mmoptss</td>      <td>{@link MmoptssValidator}</td></tr>
 *   <tr><td>nonzero</td>      <td>{@link NonzeroValidator}</td></tr>
 *   <tr><td>nozeroprefix</td> <td>{@link NozeroPrefixValidator}</td></tr>
 *   <tr><td>pcenc</td>        <td>{@link PcencValidator}</td></tr>
 *   <tr><td>pieceoftotal</td> <td>{@link PieceOfTotalValidator}</td></tr>
 *   <tr><td>posinseqslash</td><td>{@link PosInSeqSlashValidator}</td></tr>
 *   <tr><td>winding</td>      <td>{@link WindingValidator}</td></tr>
 *   <tr><td>yesno</td>        <td>{@link YesNoValidator}</td></tr>
 *   <tr><td>yymmd0</td>       <td>{@link Yymmd0Validator}</td></tr>
 *   <tr><td>yymmdd</td>       <td>{@link YymmddValidator}</td></tr>
 *   <tr><td>yymmddhh</td>     <td>{@link YymmddHhValidator}</td></tr>
 *   <tr><td>yyyymmdd</td>     <td>{@link YyyymmddValidator}</td></tr>
 *   <tr><td>zero</td>         <td>{@link ZeroValidator}</td></tr>
 * </table>
 */
public final class FormatValidators {

    private static final Map<String, ComponentFormatInterface> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put("hhmm",          HhmmValidator.INSTANCE);
        REGISTRY.put("iban",          IbanValidator.INSTANCE);
        REGISTRY.put("importeridx",   ImporterIdxValidator.INSTANCE);
        REGISTRY.put("iso3166",       Iso3166Validator.INSTANCE);
        REGISTRY.put("iso3166alpha2", Iso3166Alpha2Validator.INSTANCE);
        REGISTRY.put("iso3166list",   Iso3166ListValidator.INSTANCE);
        REGISTRY.put("iso4217",       Iso4217Validator.INSTANCE);
        REGISTRY.put("iso5218",       Iso5218Validator.INSTANCE);
        REGISTRY.put("latlong",       LatLongValidator.INSTANCE);
        REGISTRY.put("mmoptss",       MmoptssValidator.INSTANCE);
        REGISTRY.put("nonzero",       NonzeroValidator.INSTANCE);
        REGISTRY.put("nozeroprefix",  NozeroPrefixValidator.INSTANCE);
        REGISTRY.put("pcenc",         PcencValidator.INSTANCE);
        REGISTRY.put("pieceoftotal",  PieceOfTotalValidator.INSTANCE);
        REGISTRY.put("posinseqslash", PosInSeqSlashValidator.INSTANCE);
        REGISTRY.put("winding",       WindingValidator.INSTANCE);
        REGISTRY.put("yesno",         YesNoValidator.INSTANCE);
        REGISTRY.put("yymmd0",        Yymmd0Validator.INSTANCE);
        REGISTRY.put("yymmdd",        YymmddValidator.INSTANCE);
        REGISTRY.put("yymmddhh",      YymmddHhValidator.INSTANCE);
        REGISTRY.put("yyyymmdd",      YyyymmddValidator.INSTANCE);
        REGISTRY.put("zero",          ZeroValidator.INSTANCE);
    }

    private FormatValidators() {}

    /**
     * Returns the {@link ComponentFormatInterface} for the given format name.
     *
     * @param format the {@code format} field from an
     *               {@link tools.pantheum.gaia.gs1.registry.AiComponent}
     * @return the matching validator, or {@code null} if the format is unknown or {@code null}
     */
    public static ComponentFormatInterface forFormat(String format) {
        return format == null ? null : REGISTRY.get(format);
    }
}
