package tools.pantheum.gaia.gs1.constants;

/**
 * Application Identifier (AI) code constants, split out of {@link GS1Constants}.
 *
 * <p>Each constant holds the numeric AI code as a String so it can be used
 * directly in parser calls, assertions, and display logic without scattering
 * bare string literals through the codebase.
 */
public final class GS1Constants_AICodes {

    private GS1Constants_AICodes() {}

    // --- Identification — SSCC, GTIN, CONTENT, MTO ---

    /** AI (00) SSCC — Serial Shipping Container Code (SSCC). */
    public static final String AI_00_SSCC = "00";

    /** AI (01) GTIN — Global Trade Item Number (GTIN). */
    public static final String AI_01_GTIN = "01";

    /** AI (02) CONTENT — Global Trade Item Number (GTIN) of contained trade items. */
    public static final String AI_02_CONTENT = "02";

    /** AI (03) MTO GTIN — Identification of a Made-to-Order (MtO) trade item (GTIN). */
    public static final String AI_03_MTO_GTIN = "03";

    /** AI (10) BATCH/LOT — Batch or lot number. */
    public static final String AI_10_BATCH_LOT = "10";

    /** AI (11) PROD DATE — Production date (YYMMDD). */
    public static final String AI_11_PROD_DATE = "11";

    /** AI (12) DUE DATE — Due date (YYMMDD). */
    public static final String AI_12_DUE_DATE = "12";

    /** AI (13) PACK DATE — Packaging date (YYMMDD). */
    public static final String AI_13_PACK_DATE = "13";

    /** AI (15) BEST BEFORE or BEST BY — Best before date (YYMMDD). */
    public static final String AI_15_BEST_BEFORE_OR_BEST_BY = "15";

    /** AI (16) SELL BY — Sell by date (YYMMDD). */
    public static final String AI_16_SELL_BY = "16";

    /** AI (17) USE BY or EXPIRY — Expiration date (YYMMDD). */
    public static final String AI_17_USE_BY_OR_EXPIRY = "17";

    /** AI (20) VARIANT — Internal product variant. */
    public static final String AI_20_VARIANT = "20";

    /** AI (21) SERIAL — Serial number. */
    public static final String AI_21_SERIAL = "21";

    /** AI (22) CPV — Consumer product variant. */
    public static final String AI_22_CPV = "22";

    /** AI (235) TPX — Third Party Controlled, Serialised Extension of Global Trade Item Number (GTIN) (TPX). */
    public static final String AI_235_TPX = "235";

    /** AI (240) ADDITIONAL ID — Additional product identification assigned by the manufacturer. */
    public static final String AI_240_ADDITIONAL_ID = "240";

    /** AI (241) CUST. PART No. — Customer part number. */
    public static final String AI_241_CUST_PART_NO = "241";

    /** AI (242) MTO VARIANT — Made-to-Order variation number. */
    public static final String AI_242_MTO_VARIANT = "242";

    /** AI (243) PCN — Packaging component number. */
    public static final String AI_243_PCN = "243";

    /** AI (250) SECONDARY SERIAL — Secondary serial number. */
    public static final String AI_250_SECONDARY_SERIAL = "250";

    /** AI (251) REF. TO SOURCE — Reference to source entity. */
    public static final String AI_251_REF_TO_SOURCE = "251";

    /** AI (253) GDTI — Global Document Type Identifier (GDTI). */
    public static final String AI_253_GDTI = "253";

    /** AI (254) GLN EXTENSION COMPONENT — Global Location Number (GLN) extension component. */
    public static final String AI_254_GLN_EXTENSION_COMPONENT = "254";

    /** AI (255) GCN — Global Coupon Number (GCN). */
    public static final String AI_255_GCN = "255";

    /** AI (30) VAR. COUNT — Variable count of items (variable measure trade item). */
    public static final String AI_30_VAR_COUNT = "30";

    // --- Quantity / measure ---

    /** AI (3100) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3100_NET_WEIGHT_KG = "3100";

    /** AI (3101) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3101_NET_WEIGHT_KG = "3101";

    /** AI (3102) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3102_NET_WEIGHT_KG = "3102";

    /** AI (3103) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3103_NET_WEIGHT_KG = "3103";

    /** AI (3104) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3104_NET_WEIGHT_KG = "3104";

    /** AI (3105) NET WEIGHT (kg) — Net weight, kilograms (variable measure trade item). */
    public static final String AI_3105_NET_WEIGHT_KG = "3105";

    /** AI (3110) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3110_LENGTH_M = "3110";

    /** AI (3111) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3111_LENGTH_M = "3111";

    /** AI (3112) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3112_LENGTH_M = "3112";

    /** AI (3113) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3113_LENGTH_M = "3113";

    /** AI (3114) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3114_LENGTH_M = "3114";

    /** AI (3115) LENGTH (m) — Length or first dimension, metres (variable measure trade item). */
    public static final String AI_3115_LENGTH_M = "3115";

    /** AI (3120) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3120_WIDTH_M = "3120";

    /** AI (3121) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3121_WIDTH_M = "3121";

    /** AI (3122) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3122_WIDTH_M = "3122";

    /** AI (3123) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3123_WIDTH_M = "3123";

    /** AI (3124) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3124_WIDTH_M = "3124";

    /** AI (3125) WIDTH (m) — Width, diameter, or second dimension, metres (variable measure trade item). */
    public static final String AI_3125_WIDTH_M = "3125";

    /** AI (3130) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3130_HEIGHT_M = "3130";

    /** AI (3131) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3131_HEIGHT_M = "3131";

    /** AI (3132) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3132_HEIGHT_M = "3132";

    /** AI (3133) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3133_HEIGHT_M = "3133";

    /** AI (3134) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3134_HEIGHT_M = "3134";

    /** AI (3135) HEIGHT (m) — Depth, thickness, height, or third dimension, metres (variable measure trade item). */
    public static final String AI_3135_HEIGHT_M = "3135";

    /** AI (3140) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3140_AREA_M = "3140";

    /** AI (3141) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3141_AREA_M = "3141";

    /** AI (3142) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3142_AREA_M = "3142";

    /** AI (3143) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3143_AREA_M = "3143";

    /** AI (3144) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3144_AREA_M = "3144";

    /** AI (3145) AREA (m²) — Area, square metres (variable measure trade item). */
    public static final String AI_3145_AREA_M = "3145";

    /** AI (3150) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3150_NET_VOLUME_L = "3150";

    /** AI (3151) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3151_NET_VOLUME_L = "3151";

    /** AI (3152) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3152_NET_VOLUME_L = "3152";

    /** AI (3153) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3153_NET_VOLUME_L = "3153";

    /** AI (3154) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3154_NET_VOLUME_L = "3154";

    /** AI (3155) NET VOLUME (l) — Net volume, litres (variable measure trade item). */
    public static final String AI_3155_NET_VOLUME_L = "3155";

    /** AI (3160) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3160_NET_VOLUME_M = "3160";

    /** AI (3161) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3161_NET_VOLUME_M = "3161";

    /** AI (3162) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3162_NET_VOLUME_M = "3162";

    /** AI (3163) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3163_NET_VOLUME_M = "3163";

    /** AI (3164) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3164_NET_VOLUME_M = "3164";

    /** AI (3165) NET VOLUME (m³) — Net volume, cubic metres (variable measure trade item). */
    public static final String AI_3165_NET_VOLUME_M = "3165";

    /** AI (3200) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3200_NET_WEIGHT_LB = "3200";

    /** AI (3201) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3201_NET_WEIGHT_LB = "3201";

    /** AI (3202) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3202_NET_WEIGHT_LB = "3202";

    /** AI (3203) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3203_NET_WEIGHT_LB = "3203";

    /** AI (3204) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3204_NET_WEIGHT_LB = "3204";

    /** AI (3205) NET WEIGHT (lb) — Net weight, pounds (variable measure trade item). */
    public static final String AI_3205_NET_WEIGHT_LB = "3205";

    /** AI (3210) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3210_LENGTH_IN = "3210";

    /** AI (3211) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3211_LENGTH_IN = "3211";

    /** AI (3212) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3212_LENGTH_IN = "3212";

    /** AI (3213) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3213_LENGTH_IN = "3213";

    /** AI (3214) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3214_LENGTH_IN = "3214";

    /** AI (3215) LENGTH (in) — Length or first dimension, inches (variable measure trade item). */
    public static final String AI_3215_LENGTH_IN = "3215";

    /** AI (3220) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3220_LENGTH_FT = "3220";

    /** AI (3221) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3221_LENGTH_FT = "3221";

    /** AI (3222) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3222_LENGTH_FT = "3222";

    /** AI (3223) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3223_LENGTH_FT = "3223";

    /** AI (3224) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3224_LENGTH_FT = "3224";

    /** AI (3225) LENGTH (ft) — Length or first dimension, feet (variable measure trade item). */
    public static final String AI_3225_LENGTH_FT = "3225";

    /** AI (3230) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3230_LENGTH_YD = "3230";

    /** AI (3231) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3231_LENGTH_YD = "3231";

    /** AI (3232) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3232_LENGTH_YD = "3232";

    /** AI (3233) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3233_LENGTH_YD = "3233";

    /** AI (3234) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3234_LENGTH_YD = "3234";

    /** AI (3235) LENGTH (yd) — Length or first dimension, yards (variable measure trade item). */
    public static final String AI_3235_LENGTH_YD = "3235";

    /** AI (3240) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3240_WIDTH_IN = "3240";

    /** AI (3241) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3241_WIDTH_IN = "3241";

    /** AI (3242) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3242_WIDTH_IN = "3242";

    /** AI (3243) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3243_WIDTH_IN = "3243";

    /** AI (3244) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3244_WIDTH_IN = "3244";

    /** AI (3245) WIDTH (in) — Width, diameter, or second dimension, inches (variable measure trade item). */
    public static final String AI_3245_WIDTH_IN = "3245";

    /** AI (3250) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3250_WIDTH_FT = "3250";

    /** AI (3251) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3251_WIDTH_FT = "3251";

    /** AI (3252) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3252_WIDTH_FT = "3252";

    /** AI (3253) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3253_WIDTH_FT = "3253";

    /** AI (3254) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3254_WIDTH_FT = "3254";

    /** AI (3255) WIDTH (ft) — Width, diameter, or second dimension, feet (variable measure trade item). */
    public static final String AI_3255_WIDTH_FT = "3255";

    /** AI (3260) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3260_WIDTH_YD = "3260";

    /** AI (3261) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3261_WIDTH_YD = "3261";

    /** AI (3262) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3262_WIDTH_YD = "3262";

    /** AI (3263) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3263_WIDTH_YD = "3263";

    /** AI (3264) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3264_WIDTH_YD = "3264";

    /** AI (3265) WIDTH (yd) — Width, diameter, or second dimension, yards (variable measure trade item). */
    public static final String AI_3265_WIDTH_YD = "3265";

    /** AI (3270) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3270_HEIGHT_IN = "3270";

    /** AI (3271) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3271_HEIGHT_IN = "3271";

    /** AI (3272) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3272_HEIGHT_IN = "3272";

    /** AI (3273) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3273_HEIGHT_IN = "3273";

    /** AI (3274) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3274_HEIGHT_IN = "3274";

    /** AI (3275) HEIGHT (in) — Depth, thickness, height, or third dimension, inches (variable measure trade item). */
    public static final String AI_3275_HEIGHT_IN = "3275";

    /** AI (3280) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3280_HEIGHT_FT = "3280";

    /** AI (3281) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3281_HEIGHT_FT = "3281";

    /** AI (3282) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3282_HEIGHT_FT = "3282";

    /** AI (3283) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3283_HEIGHT_FT = "3283";

    /** AI (3284) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3284_HEIGHT_FT = "3284";

    /** AI (3285) HEIGHT (ft) — Depth, thickness, height, or third dimension, feet (variable measure trade item). */
    public static final String AI_3285_HEIGHT_FT = "3285";

    /** AI (3290) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3290_HEIGHT_YD = "3290";

    /** AI (3291) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3291_HEIGHT_YD = "3291";

    /** AI (3292) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3292_HEIGHT_YD = "3292";

    /** AI (3293) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3293_HEIGHT_YD = "3293";

    /** AI (3294) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3294_HEIGHT_YD = "3294";

    /** AI (3295) HEIGHT (yd) — Depth, thickness, height, or third dimension, yards (variable measure trade item). */
    public static final String AI_3295_HEIGHT_YD = "3295";

    /** AI (3300) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3300_GROSS_WEIGHT_KG = "3300";

    /** AI (3301) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3301_GROSS_WEIGHT_KG = "3301";

    /** AI (3302) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3302_GROSS_WEIGHT_KG = "3302";

    /** AI (3303) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3303_GROSS_WEIGHT_KG = "3303";

    /** AI (3304) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3304_GROSS_WEIGHT_KG = "3304";

    /** AI (3305) GROSS WEIGHT (kg) — Logistic weight, kilograms. */
    public static final String AI_3305_GROSS_WEIGHT_KG = "3305";

    /** AI (3310) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3310_LENGTH_M_LOG = "3310";

    /** AI (3311) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3311_LENGTH_M_LOG = "3311";

    /** AI (3312) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3312_LENGTH_M_LOG = "3312";

    /** AI (3313) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3313_LENGTH_M_LOG = "3313";

    /** AI (3314) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3314_LENGTH_M_LOG = "3314";

    /** AI (3315) LENGTH (m), log — Length or first dimension, metres. */
    public static final String AI_3315_LENGTH_M_LOG = "3315";

    /** AI (3320) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3320_WIDTH_M_LOG = "3320";

    /** AI (3321) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3321_WIDTH_M_LOG = "3321";

    /** AI (3322) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3322_WIDTH_M_LOG = "3322";

    /** AI (3323) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3323_WIDTH_M_LOG = "3323";

    /** AI (3324) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3324_WIDTH_M_LOG = "3324";

    /** AI (3325) WIDTH (m), log — Width, diameter, or second dimension, metres. */
    public static final String AI_3325_WIDTH_M_LOG = "3325";

    /** AI (3330) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3330_HEIGHT_M_LOG = "3330";

    /** AI (3331) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3331_HEIGHT_M_LOG = "3331";

    /** AI (3332) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3332_HEIGHT_M_LOG = "3332";

    /** AI (3333) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3333_HEIGHT_M_LOG = "3333";

    /** AI (3334) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3334_HEIGHT_M_LOG = "3334";

    /** AI (3335) HEIGHT (m), log — Depth, thickness, height, or third dimension, metres. */
    public static final String AI_3335_HEIGHT_M_LOG = "3335";

    /** AI (3340) AREA (m²), log — Area, square metres. */
    public static final String AI_3340_AREA_M_LOG = "3340";

    /** AI (3341) AREA (m²), log — Area, square metres. */
    public static final String AI_3341_AREA_M_LOG = "3341";

    /** AI (3342) AREA (m²), log — Area, square metres. */
    public static final String AI_3342_AREA_M_LOG = "3342";

    /** AI (3343) AREA (m²), log — Area, square metres. */
    public static final String AI_3343_AREA_M_LOG = "3343";

    /** AI (3344) AREA (m²), log — Area, square metres. */
    public static final String AI_3344_AREA_M_LOG = "3344";

    /** AI (3345) AREA (m²), log — Area, square metres. */
    public static final String AI_3345_AREA_M_LOG = "3345";

    /** AI (3350) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3350_VOLUME_L_LOG = "3350";

    /** AI (3351) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3351_VOLUME_L_LOG = "3351";

    /** AI (3352) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3352_VOLUME_L_LOG = "3352";

    /** AI (3353) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3353_VOLUME_L_LOG = "3353";

    /** AI (3354) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3354_VOLUME_L_LOG = "3354";

    /** AI (3355) VOLUME (l), log — Logistic volume, litres. */
    public static final String AI_3355_VOLUME_L_LOG = "3355";

    /** AI (3360) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3360_VOLUME_M_LOG = "3360";

    /** AI (3361) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3361_VOLUME_M_LOG = "3361";

    /** AI (3362) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3362_VOLUME_M_LOG = "3362";

    /** AI (3363) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3363_VOLUME_M_LOG = "3363";

    /** AI (3364) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3364_VOLUME_M_LOG = "3364";

    /** AI (3365) VOLUME (m³), log — Logistic volume, cubic metres. */
    public static final String AI_3365_VOLUME_M_LOG = "3365";

    /** AI (3370) KG PER m² — Kilograms per square metre. */
    public static final String AI_3370_KG_PER_M = "3370";

    /** AI (3371) KG PER m² — Kilograms per square metre. */
    public static final String AI_3371_KG_PER_M = "3371";

    /** AI (3372) KG PER m² — Kilograms per square metre. */
    public static final String AI_3372_KG_PER_M = "3372";

    /** AI (3373) KG PER m² — Kilograms per square metre. */
    public static final String AI_3373_KG_PER_M = "3373";

    /** AI (3374) KG PER m² — Kilograms per square metre. */
    public static final String AI_3374_KG_PER_M = "3374";

    /** AI (3375) KG PER m² — Kilograms per square metre. */
    public static final String AI_3375_KG_PER_M = "3375";

    /** AI (3400) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3400_GROSS_WEIGHT_LB = "3400";

    /** AI (3401) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3401_GROSS_WEIGHT_LB = "3401";

    /** AI (3402) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3402_GROSS_WEIGHT_LB = "3402";

    /** AI (3403) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3403_GROSS_WEIGHT_LB = "3403";

    /** AI (3404) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3404_GROSS_WEIGHT_LB = "3404";

    /** AI (3405) GROSS WEIGHT (lb) — Logistic weight, pounds. */
    public static final String AI_3405_GROSS_WEIGHT_LB = "3405";

    /** AI (3410) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3410_LENGTH_IN_LOG = "3410";

    /** AI (3411) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3411_LENGTH_IN_LOG = "3411";

    /** AI (3412) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3412_LENGTH_IN_LOG = "3412";

    /** AI (3413) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3413_LENGTH_IN_LOG = "3413";

    /** AI (3414) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3414_LENGTH_IN_LOG = "3414";

    /** AI (3415) LENGTH (in), log — Length or first dimension, inches. */
    public static final String AI_3415_LENGTH_IN_LOG = "3415";

    /** AI (3420) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3420_LENGTH_FT_LOG = "3420";

    /** AI (3421) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3421_LENGTH_FT_LOG = "3421";

    /** AI (3422) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3422_LENGTH_FT_LOG = "3422";

    /** AI (3423) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3423_LENGTH_FT_LOG = "3423";

    /** AI (3424) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3424_LENGTH_FT_LOG = "3424";

    /** AI (3425) LENGTH (ft), log — Length or first dimension, feet. */
    public static final String AI_3425_LENGTH_FT_LOG = "3425";

    /** AI (3430) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3430_LENGTH_YD_LOG = "3430";

    /** AI (3431) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3431_LENGTH_YD_LOG = "3431";

    /** AI (3432) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3432_LENGTH_YD_LOG = "3432";

    /** AI (3433) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3433_LENGTH_YD_LOG = "3433";

    /** AI (3434) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3434_LENGTH_YD_LOG = "3434";

    /** AI (3435) LENGTH (yd), log — Length or first dimension, yards. */
    public static final String AI_3435_LENGTH_YD_LOG = "3435";

    /** AI (3440) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3440_WIDTH_IN_LOG = "3440";

    /** AI (3441) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3441_WIDTH_IN_LOG = "3441";

    /** AI (3442) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3442_WIDTH_IN_LOG = "3442";

    /** AI (3443) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3443_WIDTH_IN_LOG = "3443";

    /** AI (3444) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3444_WIDTH_IN_LOG = "3444";

    /** AI (3445) WIDTH (in), log — Width, diameter, or second dimension, inches. */
    public static final String AI_3445_WIDTH_IN_LOG = "3445";

    /** AI (3450) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3450_WIDTH_FT_LOG = "3450";

    /** AI (3451) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3451_WIDTH_FT_LOG = "3451";

    /** AI (3452) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3452_WIDTH_FT_LOG = "3452";

    /** AI (3453) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3453_WIDTH_FT_LOG = "3453";

    /** AI (3454) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3454_WIDTH_FT_LOG = "3454";

    /** AI (3455) WIDTH (ft), log — Width, diameter, or second dimension, feet. */
    public static final String AI_3455_WIDTH_FT_LOG = "3455";

    /** AI (3460) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3460_WIDTH_YD_LOG = "3460";

    /** AI (3461) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3461_WIDTH_YD_LOG = "3461";

    /** AI (3462) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3462_WIDTH_YD_LOG = "3462";

    /** AI (3463) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3463_WIDTH_YD_LOG = "3463";

    /** AI (3464) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3464_WIDTH_YD_LOG = "3464";

    /** AI (3465) WIDTH (yd), log — Width, diameter, or second dimension, yard. */
    public static final String AI_3465_WIDTH_YD_LOG = "3465";

    /** AI (3470) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3470_HEIGHT_IN_LOG = "3470";

    /** AI (3471) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3471_HEIGHT_IN_LOG = "3471";

    /** AI (3472) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3472_HEIGHT_IN_LOG = "3472";

    /** AI (3473) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3473_HEIGHT_IN_LOG = "3473";

    /** AI (3474) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3474_HEIGHT_IN_LOG = "3474";

    /** AI (3475) HEIGHT (in), log — Depth, thickness, height, or third dimension, inches. */
    public static final String AI_3475_HEIGHT_IN_LOG = "3475";

    /** AI (3480) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3480_HEIGHT_FT_LOG = "3480";

    /** AI (3481) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3481_HEIGHT_FT_LOG = "3481";

    /** AI (3482) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3482_HEIGHT_FT_LOG = "3482";

    /** AI (3483) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3483_HEIGHT_FT_LOG = "3483";

    /** AI (3484) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3484_HEIGHT_FT_LOG = "3484";

    /** AI (3485) HEIGHT (ft), log — Depth, thickness, height, or third dimension, feet. */
    public static final String AI_3485_HEIGHT_FT_LOG = "3485";

    /** AI (3490) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3490_HEIGHT_YD_LOG = "3490";

    /** AI (3491) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3491_HEIGHT_YD_LOG = "3491";

    /** AI (3492) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3492_HEIGHT_YD_LOG = "3492";

    /** AI (3493) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3493_HEIGHT_YD_LOG = "3493";

    /** AI (3494) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3494_HEIGHT_YD_LOG = "3494";

    /** AI (3495) HEIGHT (yd), log — Depth, thickness, height, or third dimension, yards. */
    public static final String AI_3495_HEIGHT_YD_LOG = "3495";

    /** AI (3500) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3500_AREA_IN = "3500";

    /** AI (3501) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3501_AREA_IN = "3501";

    /** AI (3502) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3502_AREA_IN = "3502";

    /** AI (3503) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3503_AREA_IN = "3503";

    /** AI (3504) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3504_AREA_IN = "3504";

    /** AI (3505) AREA (in²) — Area, square inches (variable measure trade item). */
    public static final String AI_3505_AREA_IN = "3505";

    /** AI (3510) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3510_AREA_FT = "3510";

    /** AI (3511) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3511_AREA_FT = "3511";

    /** AI (3512) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3512_AREA_FT = "3512";

    /** AI (3513) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3513_AREA_FT = "3513";

    /** AI (3514) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3514_AREA_FT = "3514";

    /** AI (3515) AREA (ft²) — Area, square feet (variable measure trade item). */
    public static final String AI_3515_AREA_FT = "3515";

    /** AI (3520) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3520_AREA_YD = "3520";

    /** AI (3521) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3521_AREA_YD = "3521";

    /** AI (3522) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3522_AREA_YD = "3522";

    /** AI (3523) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3523_AREA_YD = "3523";

    /** AI (3524) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3524_AREA_YD = "3524";

    /** AI (3525) AREA (yd²) — Area, square yards (variable measure trade item). */
    public static final String AI_3525_AREA_YD = "3525";

    /** AI (3530) AREA (in²), log — Area, square inches. */
    public static final String AI_3530_AREA_IN_LOG = "3530";

    /** AI (3531) AREA (in²), log — Area, square inches. */
    public static final String AI_3531_AREA_IN_LOG = "3531";

    /** AI (3532) AREA (in²), log — Area, square inches. */
    public static final String AI_3532_AREA_IN_LOG = "3532";

    /** AI (3533) AREA (in²), log — Area, square inches. */
    public static final String AI_3533_AREA_IN_LOG = "3533";

    /** AI (3534) AREA (in²), log — Area, square inches. */
    public static final String AI_3534_AREA_IN_LOG = "3534";

    /** AI (3535) AREA (in²), log — Area, square inches. */
    public static final String AI_3535_AREA_IN_LOG = "3535";

    /** AI (3540) AREA (ft²), log — Area, square feet. */
    public static final String AI_3540_AREA_FT_LOG = "3540";

    /** AI (3541) AREA (ft²), log — Area, square feet. */
    public static final String AI_3541_AREA_FT_LOG = "3541";

    /** AI (3542) AREA (ft²), log — Area, square feet. */
    public static final String AI_3542_AREA_FT_LOG = "3542";

    /** AI (3543) AREA (ft²), log — Area, square feet. */
    public static final String AI_3543_AREA_FT_LOG = "3543";

    /** AI (3544) AREA (ft²), log — Area, square feet. */
    public static final String AI_3544_AREA_FT_LOG = "3544";

    /** AI (3545) AREA (ft²), log — Area, square feet. */
    public static final String AI_3545_AREA_FT_LOG = "3545";

    /** AI (3550) AREA (yd²), log — Area, square yards. */
    public static final String AI_3550_AREA_YD_LOG = "3550";

    /** AI (3551) AREA (yd²), log — Area, square yards. */
    public static final String AI_3551_AREA_YD_LOG = "3551";

    /** AI (3552) AREA (yd²), log — Area, square yards. */
    public static final String AI_3552_AREA_YD_LOG = "3552";

    /** AI (3553) AREA (yd²), log — Area, square yards. */
    public static final String AI_3553_AREA_YD_LOG = "3553";

    /** AI (3554) AREA (yd²), log — Area, square yards. */
    public static final String AI_3554_AREA_YD_LOG = "3554";

    /** AI (3555) AREA (yd²), log — Area, square yards. */
    public static final String AI_3555_AREA_YD_LOG = "3555";

    /** AI (3560) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3560_NET_WEIGHT_TROY_OZ = "3560";

    /** AI (3561) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3561_NET_WEIGHT_TROY_OZ = "3561";

    /** AI (3562) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3562_NET_WEIGHT_TROY_OZ = "3562";

    /** AI (3563) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3563_NET_WEIGHT_TROY_OZ = "3563";

    /** AI (3564) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3564_NET_WEIGHT_TROY_OZ = "3564";

    /** AI (3565) NET WEIGHT (troy oz) — Net weight, troy ounces (variable measure trade item). */
    public static final String AI_3565_NET_WEIGHT_TROY_OZ = "3565";

    /** AI (3570) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3570_NET_VOLUME_OZ = "3570";

    /** AI (3571) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3571_NET_VOLUME_OZ = "3571";

    /** AI (3572) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3572_NET_VOLUME_OZ = "3572";

    /** AI (3573) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3573_NET_VOLUME_OZ = "3573";

    /** AI (3574) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3574_NET_VOLUME_OZ = "3574";

    /** AI (3575) NET VOLUME (oz) — Net weight (or volume), ounces (variable measure trade item). */
    public static final String AI_3575_NET_VOLUME_OZ = "3575";

    /** AI (3600) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3600_NET_VOLUME_QT = "3600";

    /** AI (3601) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3601_NET_VOLUME_QT = "3601";

    /** AI (3602) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3602_NET_VOLUME_QT = "3602";

    /** AI (3603) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3603_NET_VOLUME_QT = "3603";

    /** AI (3604) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3604_NET_VOLUME_QT = "3604";

    /** AI (3605) NET VOLUME (qt) — Net volume, quarts (variable measure trade item). */
    public static final String AI_3605_NET_VOLUME_QT = "3605";

    /** AI (3610) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3610_NET_VOLUME_GAL = "3610";

    /** AI (3611) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3611_NET_VOLUME_GAL = "3611";

    /** AI (3612) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3612_NET_VOLUME_GAL = "3612";

    /** AI (3613) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3613_NET_VOLUME_GAL = "3613";

    /** AI (3614) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3614_NET_VOLUME_GAL = "3614";

    /** AI (3615) NET VOLUME (gal.) — Net volume, gallons U.S. (variable measure trade item). */
    public static final String AI_3615_NET_VOLUME_GAL = "3615";

    /** AI (3620) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3620_VOLUME_QT_LOG = "3620";

    /** AI (3621) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3621_VOLUME_QT_LOG = "3621";

    /** AI (3622) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3622_VOLUME_QT_LOG = "3622";

    /** AI (3623) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3623_VOLUME_QT_LOG = "3623";

    /** AI (3624) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3624_VOLUME_QT_LOG = "3624";

    /** AI (3625) VOLUME (qt), log — Logistic volume, quarts. */
    public static final String AI_3625_VOLUME_QT_LOG = "3625";

    /** AI (3630) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3630_VOLUME_GAL_LOG = "3630";

    /** AI (3631) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3631_VOLUME_GAL_LOG = "3631";

    /** AI (3632) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3632_VOLUME_GAL_LOG = "3632";

    /** AI (3633) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3633_VOLUME_GAL_LOG = "3633";

    /** AI (3634) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3634_VOLUME_GAL_LOG = "3634";

    /** AI (3635) VOLUME (gal.), log — Logistic volume, gallons U.S.. */
    public static final String AI_3635_VOLUME_GAL_LOG = "3635";

    /** AI (3640) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3640_NET_VOLUME_IN = "3640";

    /** AI (3641) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3641_NET_VOLUME_IN = "3641";

    /** AI (3642) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3642_NET_VOLUME_IN = "3642";

    /** AI (3643) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3643_NET_VOLUME_IN = "3643";

    /** AI (3644) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3644_NET_VOLUME_IN = "3644";

    /** AI (3645) NET VOLUME (in³) — Net volume, cubic inches (variable measure trade item). */
    public static final String AI_3645_NET_VOLUME_IN = "3645";

    /** AI (3650) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3650_NET_VOLUME_FT = "3650";

    /** AI (3651) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3651_NET_VOLUME_FT = "3651";

    /** AI (3652) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3652_NET_VOLUME_FT = "3652";

    /** AI (3653) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3653_NET_VOLUME_FT = "3653";

    /** AI (3654) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3654_NET_VOLUME_FT = "3654";

    /** AI (3655) NET VOLUME (ft³) — Net volume, cubic feet (variable measure trade item). */
    public static final String AI_3655_NET_VOLUME_FT = "3655";

    /** AI (3660) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3660_NET_VOLUME_YD = "3660";

    /** AI (3661) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3661_NET_VOLUME_YD = "3661";

    /** AI (3662) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3662_NET_VOLUME_YD = "3662";

    /** AI (3663) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3663_NET_VOLUME_YD = "3663";

    /** AI (3664) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3664_NET_VOLUME_YD = "3664";

    /** AI (3665) NET VOLUME (yd³) — Net volume, cubic yards (variable measure trade item). */
    public static final String AI_3665_NET_VOLUME_YD = "3665";

    /** AI (3670) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3670_VOLUME_IN_LOG = "3670";

    /** AI (3671) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3671_VOLUME_IN_LOG = "3671";

    /** AI (3672) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3672_VOLUME_IN_LOG = "3672";

    /** AI (3673) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3673_VOLUME_IN_LOG = "3673";

    /** AI (3674) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3674_VOLUME_IN_LOG = "3674";

    /** AI (3675) VOLUME (in³), log — Logistic volume, cubic inches. */
    public static final String AI_3675_VOLUME_IN_LOG = "3675";

    /** AI (3680) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3680_VOLUME_FT_LOG = "3680";

    /** AI (3681) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3681_VOLUME_FT_LOG = "3681";

    /** AI (3682) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3682_VOLUME_FT_LOG = "3682";

    /** AI (3683) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3683_VOLUME_FT_LOG = "3683";

    /** AI (3684) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3684_VOLUME_FT_LOG = "3684";

    /** AI (3685) VOLUME (ft³), log — Logistic volume, cubic feet. */
    public static final String AI_3685_VOLUME_FT_LOG = "3685";

    /** AI (3690) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3690_VOLUME_YD_LOG = "3690";

    /** AI (3691) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3691_VOLUME_YD_LOG = "3691";

    /** AI (3692) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3692_VOLUME_YD_LOG = "3692";

    /** AI (3693) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3693_VOLUME_YD_LOG = "3693";

    /** AI (3694) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3694_VOLUME_YD_LOG = "3694";

    /** AI (3695) VOLUME (yd³), log — Logistic volume, cubic yards. */
    public static final String AI_3695_VOLUME_YD_LOG = "3695";

    // --- Identification — SSCC, GTIN, CONTENT, MTO ---

    /** AI (37) COUNT — Count of trade items or trade item pieces contained in a logistic unit. */
    public static final String AI_37_COUNT = "37";

    // --- Quantity / measure ---

    /** AI (3900) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3900_AMOUNT = "3900";

    /** AI (3901) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3901_AMOUNT = "3901";

    /** AI (3902) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3902_AMOUNT = "3902";

    /** AI (3903) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3903_AMOUNT = "3903";

    /** AI (3904) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3904_AMOUNT = "3904";

    /** AI (3905) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3905_AMOUNT = "3905";

    /** AI (3906) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3906_AMOUNT = "3906";

    /** AI (3907) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3907_AMOUNT = "3907";

    /** AI (3908) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3908_AMOUNT = "3908";

    /** AI (3909) AMOUNT — Applicable amount payable or Coupon value, local currency. */
    public static final String AI_3909_AMOUNT = "3909";

    /** AI (3910) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3910_AMOUNT = "3910";

    /** AI (3911) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3911_AMOUNT = "3911";

    /** AI (3912) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3912_AMOUNT = "3912";

    /** AI (3913) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3913_AMOUNT = "3913";

    /** AI (3914) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3914_AMOUNT = "3914";

    /** AI (3915) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3915_AMOUNT = "3915";

    /** AI (3916) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3916_AMOUNT = "3916";

    /** AI (3917) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3917_AMOUNT = "3917";

    /** AI (3918) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3918_AMOUNT = "3918";

    /** AI (3919) AMOUNT — Applicable amount payable with ISO currency code. */
    public static final String AI_3919_AMOUNT = "3919";

    /** AI (3920) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3920_PRICE = "3920";

    /** AI (3921) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3921_PRICE = "3921";

    /** AI (3922) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3922_PRICE = "3922";

    /** AI (3923) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3923_PRICE = "3923";

    /** AI (3924) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3924_PRICE = "3924";

    /** AI (3925) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3925_PRICE = "3925";

    /** AI (3926) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3926_PRICE = "3926";

    /** AI (3927) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3927_PRICE = "3927";

    /** AI (3928) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3928_PRICE = "3928";

    /** AI (3929) PRICE — Applicable amount payable, single monetary area (variable measure trade item). */
    public static final String AI_3929_PRICE = "3929";

    /** AI (3930) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3930_PRICE = "3930";

    /** AI (3931) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3931_PRICE = "3931";

    /** AI (3932) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3932_PRICE = "3932";

    /** AI (3933) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3933_PRICE = "3933";

    /** AI (3934) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3934_PRICE = "3934";

    /** AI (3935) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3935_PRICE = "3935";

    /** AI (3936) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3936_PRICE = "3936";

    /** AI (3937) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3937_PRICE = "3937";

    /** AI (3938) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3938_PRICE = "3938";

    /** AI (3939) PRICE — Applicable amount payable with ISO currency code (variable measure trade item). */
    public static final String AI_3939_PRICE = "3939";

    /** AI (3940) PRCNT OFF — Percentage discount of a coupon. */
    public static final String AI_3940_PRCNT_OFF = "3940";

    /** AI (3941) PRCNT OFF — Percentage discount of a coupon. */
    public static final String AI_3941_PRCNT_OFF = "3941";

    /** AI (3942) PRCNT OFF — Percentage discount of a coupon. */
    public static final String AI_3942_PRCNT_OFF = "3942";

    /** AI (3943) PRCNT OFF — Percentage discount of a coupon. */
    public static final String AI_3943_PRCNT_OFF = "3943";

    /** AI (3950) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3950_PRICE_UOM = "3950";

    /** AI (3951) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3951_PRICE_UOM = "3951";

    /** AI (3952) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3952_PRICE_UOM = "3952";

    /** AI (3953) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3953_PRICE_UOM = "3953";

    /** AI (3954) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3954_PRICE_UOM = "3954";

    /** AI (3955) PRICE/UoM — Amount Payable per unit of measure single monetary area (variable measure trade item). */
    public static final String AI_3955_PRICE_UOM = "3955";

    // --- Identification — SSCC, GTIN, CONTENT, MTO ---

    /** AI (400) ORDER NUMBER — Customers purchase order number. */
    public static final String AI_400_ORDER_NUMBER = "400";

    /** AI (401) GINC — Global Identification Number for Consignment (GINC). */
    public static final String AI_401_GINC = "401";

    /** AI (402) GSIN — Global Shipment Identification Number (GSIN). */
    public static final String AI_402_GSIN = "402";

    /** AI (403) ROUTE — Routing code. */
    public static final String AI_403_ROUTE = "403";

    /** AI (410) SHIP TO LOC — Ship to / Deliver to Global Location Number (GLN). */
    public static final String AI_410_SHIP_TO_LOC = "410";

    /** AI (411) BILL TO — Bill to / Invoice to Global Location Number (GLN). */
    public static final String AI_411_BILL_TO = "411";

    /** AI (412) PURCHASE FROM — Purchased from Global Location Number (GLN). */
    public static final String AI_412_PURCHASE_FROM = "412";

    /** AI (413) SHIP FOR LOC — Ship for / Deliver for - Forward to Global Location Number (GLN). */
    public static final String AI_413_SHIP_FOR_LOC = "413";

    /** AI (414) LOC No. — Identification of a physical location - Global Location Number (GLN). */
    public static final String AI_414_LOC_NO = "414";

    /** AI (415) PAY TO — Global Location Number (GLN) of the invoicing party. */
    public static final String AI_415_PAY_TO = "415";

    /** AI (416) PROD/SERV LOC — Global Location Number (GLN) of the production or service location. */
    public static final String AI_416_PROD_SERV_LOC = "416";

    /** AI (417) PARTY — Party Global Location Number (GLN). */
    public static final String AI_417_PARTY = "417";

    /** AI (420) SHIP TO POST — Ship to / Deliver to postal code within a single postal authority. */
    public static final String AI_420_SHIP_TO_POST = "420";

    /** AI (421) SHIP TO POST — Ship to / Deliver to postal code with ISO country code. */
    public static final String AI_421_SHIP_TO_POST = "421";

    /** AI (422) ORIGIN — Country of origin of a trade item. */
    public static final String AI_422_ORIGIN = "422";

    /** AI (423) COUNTRY - INITIAL PROCESS — Country of initial processing. */
    public static final String AI_423_COUNTRY_INITIAL_PROCESS = "423";

    /** AI (424) COUNTRY - PROCESS — Country of processing. */
    public static final String AI_424_COUNTRY_PROCESS = "424";

    /** AI (425) COUNTRY - DISASSEMBLY — Country of disassembly. */
    public static final String AI_425_COUNTRY_DISASSEMBLY = "425";

    /** AI (426) COUNTRY - FULL PROCESS — Country covering full process chain. */
    public static final String AI_426_COUNTRY_FULL_PROCESS = "426";

    /** AI (427) ORIGIN SUBDIVISION — Country subdivision Of origin. */
    public static final String AI_427_ORIGIN_SUBDIVISION = "427";

    // --- Purchase order / shipment references ---

    /** AI (4300) SHIP TO COMP — Ship-to / Deliver-to company name. */
    public static final String AI_4300_SHIP_TO_COMP = "4300";

    /** AI (4301) SHIP TO NAME — Ship-to / Deliver-to contact. */
    public static final String AI_4301_SHIP_TO_NAME = "4301";

    /** AI (4302) SHIP TO ADD1 — Ship-to / Deliver-to address line 1. */
    public static final String AI_4302_SHIP_TO_ADD1 = "4302";

    /** AI (4303) SHIP TO ADD2 — Ship-to / Deliver-to address line 2. */
    public static final String AI_4303_SHIP_TO_ADD2 = "4303";

    /** AI (4304) SHIP TO SUB — Ship-to / Deliver-to suburb. */
    public static final String AI_4304_SHIP_TO_SUB = "4304";

    /** AI (4305) SHIP TO LOC — Ship-to / Deliver-to locality. */
    public static final String AI_4305_SHIP_TO_LOC = "4305";

    /** AI (4306) SHIP TO REG — Ship-to / Deliver-to region. */
    public static final String AI_4306_SHIP_TO_REG = "4306";

    /** AI (4307) SHIP TO COUNTRY — Ship-to / Deliver-to country code. */
    public static final String AI_4307_SHIP_TO_COUNTRY = "4307";

    /** AI (4308) SHIP TO PHONE — Ship-to / Deliver-to telephone number. */
    public static final String AI_4308_SHIP_TO_PHONE = "4308";

    /** AI (4309) SHIP TO GEO — Ship-to / Deliver-to GEO location. */
    public static final String AI_4309_SHIP_TO_GEO = "4309";

    /** AI (4310) RTN TO COMP — Return-to company name. */
    public static final String AI_4310_RTN_TO_COMP = "4310";

    /** AI (4311) RTN TO NAME — Return-to contact. */
    public static final String AI_4311_RTN_TO_NAME = "4311";

    /** AI (4312) RTN TO ADD1 — Return-to address line 1. */
    public static final String AI_4312_RTN_TO_ADD1 = "4312";

    /** AI (4313) RTN TO ADD2 — Return-to address line 2. */
    public static final String AI_4313_RTN_TO_ADD2 = "4313";

    /** AI (4314) RTN TO SUB — Return-to suburb. */
    public static final String AI_4314_RTN_TO_SUB = "4314";

    /** AI (4315) RTN TO LOC — Return-to locality. */
    public static final String AI_4315_RTN_TO_LOC = "4315";

    /** AI (4316) RTN TO REG — Return-to region. */
    public static final String AI_4316_RTN_TO_REG = "4316";

    /** AI (4317) RTN TO COUNTRY — Return-to country code. */
    public static final String AI_4317_RTN_TO_COUNTRY = "4317";

    /** AI (4318) RTN TO POST — Return-to postal code. */
    public static final String AI_4318_RTN_TO_POST = "4318";

    /** AI (4319) RTN TO PHONE — Return-to telephone number. */
    public static final String AI_4319_RTN_TO_PHONE = "4319";

    /** AI (4320) SRV DESCRIPTION — Service code description. */
    public static final String AI_4320_SRV_DESCRIPTION = "4320";

    /** AI (4321) DANGEROUS GOODS — Dangerous goods flag. */
    public static final String AI_4321_DANGEROUS_GOODS = "4321";

    /** AI (4322) AUTH LEAVE — Authority to leave. */
    public static final String AI_4322_AUTH_LEAVE = "4322";

    /** AI (4323) SIG REQUIRED — Signature required flag. */
    public static final String AI_4323_SIG_REQUIRED = "4323";

    /** AI (4324) NBEF DEL DT — Not before delivery date time (YYMMDDhhmm). */
    public static final String AI_4324_NBEF_DEL_DT = "4324";

    /** AI (4325) NAFT DEL DT — Not after delivery date time (YYMMDDhhmm). */
    public static final String AI_4325_NAFT_DEL_DT = "4325";

    /** AI (4326) REL DATE — Release date (YYMMDD). */
    public static final String AI_4326_REL_DATE = "4326";

    /** AI (4330) MAX TEMP F — Maximum temperature in Fahrenheit (expressed in hundredths of degrees). */
    public static final String AI_4330_MAX_TEMP_F = "4330";

    /** AI (4331) MAX TEMP C — Maximum temperature in Celsius (expressed in hundredths of degrees). */
    public static final String AI_4331_MAX_TEMP_C = "4331";

    /** AI (4332) MIN TEMP F — Minimum temperature in Fahrenheit (expressed in hundredths of degrees). */
    public static final String AI_4332_MIN_TEMP_F = "4332";

    /** AI (4333) MIN TEMP C — Minimum temperature in Celsius (expressed in hundredths of degrees). */
    public static final String AI_4333_MIN_TEMP_C = "4333";

    // --- GS1 UIC and tobacco traceability ---

    /** AI (7001) NSN — NATO Stock Number (NSN). */
    public static final String AI_7001_NSN = "7001";

    /** AI (7002) MEAT CUT — UN/ECE meat carcasses and cuts classification. */
    public static final String AI_7002_MEAT_CUT = "7002";

    /** AI (7003) EXPIRY TIME — Expiration date and time (YYMMDDhhmm). */
    public static final String AI_7003_EXPIRY_TIME = "7003";

    /** AI (7004) ACTIVE POTENCY. */
    public static final String AI_7004_ACTIVE_POTENCY = "7004";

    /** AI (7005) CATCH AREA. */
    public static final String AI_7005_CATCH_AREA = "7005";

    /** AI (7006) FIRST FREEZE DATE — First freeze date (YYMMDD). */
    public static final String AI_7006_FIRST_FREEZE_DATE = "7006";

    /** AI (7007) HARVEST DATE — Harvest date (YYMMDD[YYMMDD]). */
    public static final String AI_7007_HARVEST_DATE = "7007";

    /** AI (7008) AQUATIC SPECIES — Species for fishery purposes. */
    public static final String AI_7008_AQUATIC_SPECIES = "7008";

    /** AI (7009) FISHING GEAR TYPE. */
    public static final String AI_7009_FISHING_GEAR_TYPE = "7009";

    /** AI (7010) PROD METHOD — Production method. */
    public static final String AI_7010_PROD_METHOD = "7010";

    /** AI (7011) TEST BY DATE — Test by date (YYMMDD[hhmm]). */
    public static final String AI_7011_TEST_BY_DATE = "7011";

    /** AI (7020) REFURB LOT — Refurbishment lot ID. */
    public static final String AI_7020_REFURB_LOT = "7020";

    /** AI (7021) FUNC STAT — Functional status. */
    public static final String AI_7021_FUNC_STAT = "7021";

    /** AI (7022) REV STAT — Revision status. */
    public static final String AI_7022_REV_STAT = "7022";

    /** AI (7023) GIAI - ASSEMBLY — Global Individual Asset Identifier (GIAI) of an assembly. */
    public static final String AI_7023_GIAI_ASSEMBLY = "7023";

    /** AI (7030) PROCESSOR # 0 — Number of processor with three-digit ISO country code. */
    public static final String AI_7030_PROCESSOR_0 = "7030";

    /** AI (7031) PROCESSOR # 1 — Number of processor with three-digit ISO country code. */
    public static final String AI_7031_PROCESSOR_1 = "7031";

    /** AI (7032) PROCESSOR # 2 — Number of processor with three-digit ISO country code. */
    public static final String AI_7032_PROCESSOR_2 = "7032";

    /** AI (7033) PROCESSOR # 3 — Number of processor with three-digit ISO country code. */
    public static final String AI_7033_PROCESSOR_3 = "7033";

    /** AI (7034) PROCESSOR # 4 — Number of processor with three-digit ISO country code. */
    public static final String AI_7034_PROCESSOR_4 = "7034";

    /** AI (7035) PROCESSOR # 5 — Number of processor with three-digit ISO country code. */
    public static final String AI_7035_PROCESSOR_5 = "7035";

    /** AI (7036) PROCESSOR # 6 — Number of processor with three-digit ISO country code. */
    public static final String AI_7036_PROCESSOR_6 = "7036";

    /** AI (7037) PROCESSOR # 7 — Number of processor with three-digit ISO country code. */
    public static final String AI_7037_PROCESSOR_7 = "7037";

    /** AI (7038) PROCESSOR # 8 — Number of processor with three-digit ISO country code. */
    public static final String AI_7038_PROCESSOR_8 = "7038";

    /** AI (7039) PROCESSOR # 9 — Number of processor with three-digit ISO country code. */
    public static final String AI_7039_PROCESSOR_9 = "7039";

    /** AI (7040) UIC+EXT — GS1 UIC with Extension 1 and Importer index. */
    public static final String AI_7040_UIC_EXT = "7040";

    /** AI (7041) UFRGT UNIT TYPE — UN/CEFACT freight unit type. */
    public static final String AI_7041_UFRGT_UNIT_TYPE = "7041";

    // --- Identification — SSCC, GTIN, CONTENT, MTO ---

    /** AI (710) NHRN PZN — National Healthcare Reimbursement Number (NHRN) - Germany PZN. */
    public static final String AI_710_NHRN_PZN = "710";

    /** AI (711) NHRN CIP — National Healthcare Reimbursement Number (NHRN) - France CIP. */
    public static final String AI_711_NHRN_CIP = "711";

    /** AI (712) NHRN CN — National Healthcare Reimbursement Number (NHRN) - Spain CN. */
    public static final String AI_712_NHRN_CN = "712";

    /** AI (713) NHRN DRN — National Healthcare Reimbursement Number (NHRN) - Brasil DRN. */
    public static final String AI_713_NHRN_DRN = "713";

    /** AI (714) NHRN AIM — National Healthcare Reimbursement Number (NHRN) - Portugal AIM. */
    public static final String AI_714_NHRN_AIM = "714";

    /** AI (715) NHRN NDC — National Healthcare Reimbursement Number (NHRN) - United States of America NDC. */
    public static final String AI_715_NHRN_NDC = "715";

    /** AI (716) NHRN AIC — National Healthcare Reimbursement Number (NHRN) - Italy AIC. */
    public static final String AI_716_NHRN_AIC = "716";

    /** AI (717) NHRN SRN — National Healthcare Reimbursement Number (NHRN) - Costa Rica Sanitary Register Number. */
    public static final String AI_717_NHRN_SRN = "717";

    // --- Healthcare / GMN / HIDRI / CPID ---

    /** AI (7230) CERT # 0 — Certification Reference. */
    public static final String AI_7230_CERT_0 = "7230";

    /** AI (7231) CERT # 1 — Certification Reference. */
    public static final String AI_7231_CERT_1 = "7231";

    /** AI (7232) CERT # 2 — Certification Reference. */
    public static final String AI_7232_CERT_2 = "7232";

    /** AI (7233) CERT # 3 — Certification Reference. */
    public static final String AI_7233_CERT_3 = "7233";

    /** AI (7234) CERT # 4 — Certification Reference. */
    public static final String AI_7234_CERT_4 = "7234";

    /** AI (7235) CERT # 5 — Certification Reference. */
    public static final String AI_7235_CERT_5 = "7235";

    /** AI (7236) CERT # 6 — Certification Reference. */
    public static final String AI_7236_CERT_6 = "7236";

    /** AI (7237) CERT # 7 — Certification Reference. */
    public static final String AI_7237_CERT_7 = "7237";

    /** AI (7238) CERT # 8 — Certification Reference. */
    public static final String AI_7238_CERT_8 = "7238";

    /** AI (7239) CERT # 9 — Certification Reference. */
    public static final String AI_7239_CERT_9 = "7239";

    /** AI (7240) PROTOCOL — Protocol ID. */
    public static final String AI_7240_PROTOCOL = "7240";

    /** AI (7241) AIDC MEDIA TYPE. */
    public static final String AI_7241_AIDC_MEDIA_TYPE = "7241";

    /** AI (7242) VCN — Version Control Number (VCN). */
    public static final String AI_7242_VCN = "7242";

    /** AI (7250) DOB — Date of birth (YYYYMMDD). */
    public static final String AI_7250_DOB = "7250";

    /** AI (7251) DOB TIME — Date and time of birth (YYYYMMDDhhmm). */
    public static final String AI_7251_DOB_TIME = "7251";

    /** AI (7252) BIO SEX — Biological sex. */
    public static final String AI_7252_BIO_SEX = "7252";

    /** AI (7253) FAMILY NAME — Family name of person. */
    public static final String AI_7253_FAMILY_NAME = "7253";

    /** AI (7254) GIVEN NAME — Given name of person. */
    public static final String AI_7254_GIVEN_NAME = "7254";

    /** AI (7255) SUFFIX — Name suffix of person. */
    public static final String AI_7255_SUFFIX = "7255";

    /** AI (7256) FULL NAME — Full name of person. */
    public static final String AI_7256_FULL_NAME = "7256";

    /** AI (7257) PERSON ADDR — Address of person. */
    public static final String AI_7257_PERSON_ADDR = "7257";

    /** AI (7258) BIRTH SEQUENCE — Baby birth sequence. */
    public static final String AI_7258_BIRTH_SEQUENCE = "7258";

    /** AI (7259) BABY — Baby of family name. */
    public static final String AI_7259_BABY = "7259";

    // --- Financial / IBAN / pricing ---

    /** AI (8001) DIMENSIONS — Roll products (width, length, core diameter, direction, splices). */
    public static final String AI_8001_DIMENSIONS = "8001";

    /** AI (8002) CMT No. — Cellular mobile telephone identifier. */
    public static final String AI_8002_CMT_NO = "8002";

    /** AI (8003) GRAI — Global Returnable Asset Identifier (GRAI). */
    public static final String AI_8003_GRAI = "8003";

    /** AI (8004) GIAI — Global Individual Asset Identifier (GIAI). */
    public static final String AI_8004_GIAI = "8004";

    /** AI (8005) PRICE PER UNIT — Price per unit of measure. */
    public static final String AI_8005_PRICE_PER_UNIT = "8005";

    /** AI (8006) ITIP — Identification of an individual trade item piece (ITIP). */
    public static final String AI_8006_ITIP = "8006";

    /** AI (8007) IBAN — International Bank Account Number (IBAN). */
    public static final String AI_8007_IBAN = "8007";

    /** AI (8008) PROD TIME — Date and time of production (YYMMDDhh[mm[ss]]). */
    public static final String AI_8008_PROD_TIME = "8008";

    /** AI (8009) OPTSEN — Optically Readable Sensor Indicator. */
    public static final String AI_8009_OPTSEN = "8009";

    /** AI (8010) CPID — Component/Part Identifier (CPID). */
    public static final String AI_8010_CPID = "8010";

    /** AI (8011) CPID SERIAL — Component/Part Identifier serial number (CPID SERIAL). */
    public static final String AI_8011_CPID_SERIAL = "8011";

    /** AI (8012) VERSION — Software version. */
    public static final String AI_8012_VERSION = "8012";

    /** AI (8013) GMN — Global Model Number (GMN). */
    public static final String AI_8013_GMN = "8013";

    /** AI (8014) MUDI — Highly Individualised Device Registration Identifier (HIDRI). */
    public static final String AI_8014_MUDI = "8014";

    /** AI (8017) GSRN - PROVIDER — Global Service Relation Number (GSRN) to identify the relationship between an organisation offering services and the provider of services. */
    public static final String AI_8017_GSRN_PROVIDER = "8017";

    /** AI (8018) GSRN - RECIPIENT — Global Service Relation Number (GSRN) to identify the relationship between an organisation offering services and the recipient of services. */
    public static final String AI_8018_GSRN_RECIPIENT = "8018";

    /** AI (8019) SRIN — Service Relation Instance Number (SRIN). */
    public static final String AI_8019_SRIN = "8019";

    /** AI (8020) REF No. — Payment slip reference number. */
    public static final String AI_8020_REF_NO = "8020";

    /** AI (8026) ITIP CONTENT — Identification of pieces of a trade item (ITIP) contained in a logistic unit. */
    public static final String AI_8026_ITIP_CONTENT = "8026";

    /** AI (8030) DIGSIG — Digital Signature (DigSig). */
    public static final String AI_8030_DIGSIG = "8030";

    /** AI (8040) IMEI — Internatinal Mobile Equipment Identity (IMEI). */
    public static final String AI_8040_IMEI = "8040";

    /** AI (8041) IMEI2 — Internatinal Mobile Equipment Identity 2 (IMEI2). */
    public static final String AI_8041_IMEI2 = "8041";

    /** AI (8042) ESIM — Embedded SIM number. */
    public static final String AI_8042_ESIM = "8042";

    /** AI (8043) PSIM — Physical SIM number. */
    public static final String AI_8043_PSIM = "8043";

    /** AI (8110) — Coupon code identification for use in North America. */
    public static final String AI_8110 = "8110";

    /** AI (8111) POINTS — Loyalty points of a coupon. */
    public static final String AI_8111_POINTS = "8111";

    /** AI (8112) — Positive offer file coupon code identification for use in North America. */
    public static final String AI_8112 = "8112";

    /** AI (8200) PRODUCT URL — Extended Packaging URL. */
    public static final String AI_8200_PRODUCT_URL = "8200";

    // --- Identification — SSCC, GTIN, CONTENT, MTO ---

    /** AI (90) INTERNAL — Information mutually agreed between trading partners. */
    public static final String AI_90_INTERNAL = "90";

    /** AI (91) INTERNAL — Company internal information. */
    public static final String AI_91_INTERNAL = "91";

    /** AI (92) INTERNAL — Company internal information. */
    public static final String AI_92_INTERNAL = "92";

    /** AI (93) INTERNAL — Company internal information. */
    public static final String AI_93_INTERNAL = "93";

    /** AI (94) INTERNAL — Company internal information. */
    public static final String AI_94_INTERNAL = "94";

    /** AI (95) INTERNAL — Company internal information. */
    public static final String AI_95_INTERNAL = "95";

    /** AI (96) INTERNAL — Company internal information. */
    public static final String AI_96_INTERNAL = "96";

    /** AI (97) INTERNAL — Company internal information. */
    public static final String AI_97_INTERNAL = "97";

    /** AI (98) INTERNAL — Company internal information. */
    public static final String AI_98_INTERNAL = "98";

    /** AI (99) INTERNAL — Company internal information. */
    public static final String AI_99_INTERNAL = "99";
}
