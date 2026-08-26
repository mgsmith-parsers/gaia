package tools.pantheum.gaia.config;

import tools.pantheum.gaia.GaiaConstants;
import tools.pantheum.gaia.gs1.constants.GS1Constants;
import tools.pantheum.gaia.modifier.ModifierInterface;
import tools.pantheum.gaia.modifier.registry.ModifierRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable per-call configuration for {@link tools.pantheum.gaia.GaiaParser#parse(String, ParseConfig)}.
 *
 * <p>Consolidates all parse-time options — pipeline depth, date formatting, and
 * error-message language — into a single object that is passed once and flows
 * through every stage of the pipeline.
 *
 * <h2>Parse mode</h2>
 * <ul>
 *   <li>{@link #getRequestedParseMode()} — controls how deeply the input is validated;
 *       defaults to {@link GaiaConstants.ParseMode#INTERPRETATION}.</li>
 * </ul>
 *
 * <h2>Date formatting</h2>
 * <ul>
 *   <li>{@link #getDateEndian()}    — controls the order of day, month and year components.</li>
 *   <li>{@link #getDateSeparator()} — the separator placed between date components;
 *       one of {@link GaiaConstants.DateSeparator#SLASH SLASH} ({@code /}),
 *       {@link GaiaConstants.DateSeparator#HYPHEN HYPHEN} ({@code -}), or
 *       {@link GaiaConstants.DateSeparator#PERIOD PERIOD} ({@code .}).</li>
 *   <li>{@link #getMonthFormat()}   — whether the month is a two-digit number
 *       ({@code 12}) or a three-letter abbreviation ({@code DEC}).</li>
 *   <li>{@link #getYearFormat()}    — whether the year is four digits
 *       ({@code 2026}) or two digits ({@code 26}).</li>
 * </ul>
 *
 * <h2>Language</h2>
 * <ul>
 *   <li>{@link #getLanguage()} — the language used for error messages;
 *       defaults to {@link GaiaConstants.Language#ENGLISH}.</li>
 * </ul>
 *
 * <h2>Modifiers</h2>
 * <ul>
 *   <li>{@link #getModifiers()} — caller-supplied {@link ModifierInterface} implementations
 *       that rewrite the raw input before any parsing takes place. Empty by default.
 *       Add them by instance ({@link Builder#modifier(ModifierInterface)}) or by
 *       fully-qualified class name ({@link Builder#modifierClass(String)}); either way
 *       they run in the order added.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Default config — INTERPRETATION mode, dd/mm/yyyy, English
 * ParseConfig config = ParseConfig.defaultConfig();
 *
 * // CONTENT-only parse (skip interpretation enrichers)
 * ParseConfig content = ParseConfig.builder()
 *         .requestedParseMode(GaiaConstants.ParseMode.CONTENT)
 *         .build();
 *
 * // Full interpretation with ISO 8601 dates and French error messages
 * ParseConfig custom = ParseConfig.builder()
 *         .dateEndian(GaiaConstants.DateEndian.BIG)
 *         .dateSeparator(GaiaConstants.DateSeparator.HYPHEN)
 *         .language(GaiaConstants.Language.FRENCH)
 *         .build();
 *
 * // Normalise the input before parsing — modifiers run first, in the order added
 * ParseConfig modified = ParseConfig.builder()
 *         .modifier(new GsPlaceholderModifier())
 *         .modifierClass("com.example.gaia.StripVendorWrapperModifier")
 *         .build();
 * }</pre>
 */
public final class ParseConfig {

    private static final ParseConfig DEFAULT = new ParseConfig(
            GaiaConstants.ParseMode.INTERPRETATION,
            GaiaConstants.DateEndian.LITTLE,
            GaiaConstants.DateSeparator.SLASH,
            GaiaConstants.MonthFormat.TWO_DIGIT,
            GaiaConstants.YearFormat.FOUR_DIGIT,
            GaiaConstants.Language.ENGLISH,
            false,
            false,
            Collections.emptyList());

    private final GaiaConstants.ParseMode     requestedParseMode;
    private final GaiaConstants.DateEndian    dateEndian;
    private final GaiaConstants.DateSeparator dateSeparator;
    private final GaiaConstants.MonthFormat   monthFormat;
    private final GaiaConstants.YearFormat    yearFormat;
    private final GaiaConstants.Language      language;
    private final boolean                     skipRequiresCheck;
    private final boolean                     skipExcludesCheck;
    private final List<ModifierInterface>     modifiers;

    private ParseConfig(GaiaConstants.ParseMode requestedParseMode,
                        GaiaConstants.DateEndian dateEndian,
                        GaiaConstants.DateSeparator dateSeparator,
                        GaiaConstants.MonthFormat monthFormat,
                        GaiaConstants.YearFormat yearFormat,
                        GaiaConstants.Language language,
                        boolean skipRequiresCheck,
                        boolean skipExcludesCheck,
                        List<ModifierInterface> modifiers) {
        this.requestedParseMode     = requestedParseMode     != null ? requestedParseMode     : GaiaConstants.ParseMode.INTERPRETATION;
        this.dateEndian    = dateEndian    != null ? dateEndian    : GaiaConstants.DateEndian.LITTLE;
        this.dateSeparator = dateSeparator != null ? dateSeparator : GaiaConstants.DateSeparator.SLASH;
        this.monthFormat   = monthFormat   != null ? monthFormat   : GaiaConstants.MonthFormat.TWO_DIGIT;
        this.yearFormat    = yearFormat    != null ? yearFormat    : GaiaConstants.YearFormat.FOUR_DIGIT;
        this.language      = language      != null ? language      : GaiaConstants.Language.ENGLISH;
        this.skipRequiresCheck = skipRequiresCheck;
        this.skipExcludesCheck = skipExcludesCheck;
        this.modifiers     = (modifiers == null || modifiers.isEmpty())
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(modifiers));
    }

    /**
     * Returns the default configuration:
     * {@link GaiaConstants.ParseMode#INTERPRETATION INTERPRETATION} mode,
     * LITTLE-endian ({@code dd/mm/yyyy}),
     * {@link GaiaConstants.DateSeparator#SLASH SLASH} separator,
     * {@link GaiaConstants.MonthFormat#TWO_DIGIT TWO_DIGIT} month,
     * {@link GaiaConstants.YearFormat#FOUR_DIGIT FOUR_DIGIT} year, and
     * {@link GaiaConstants.Language#ENGLISH ENGLISH} error messages.
     */
    public static ParseConfig defaultConfig() {
        return DEFAULT;
    }

    /** Returns a new {@link Builder} for constructing a custom {@code ParseConfig}. */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The pipeline depth applied during parsing.
     *
     * @see GaiaConstants.ParseMode
     */
    public GaiaConstants.ParseMode getRequestedParseMode() { return requestedParseMode; }

    /**
     * The date component order used when formatting date interpretations.
     *
     * @see GaiaConstants.DateEndian
     */
    public GaiaConstants.DateEndian getDateEndian() { return dateEndian; }

    /**
     * The separator placed between day, month, and year in a formatted date,
     * e.g. {@link GaiaConstants.DateSeparator#SLASH SLASH} → {@code "31/12/2026"},
     * {@link GaiaConstants.DateSeparator#HYPHEN HYPHEN} → {@code "2026-12-31"}.
     *
     * @see GaiaConstants.DateSeparator
     */
    public GaiaConstants.DateSeparator getDateSeparator() { return dateSeparator; }

    /**
     * Whether the month is rendered as a two-digit number ({@code 12}) or a
     * three-letter abbreviation ({@code DEC}) in a formatted date interpretation.
     *
     * @see GaiaConstants.MonthFormat
     */
    public GaiaConstants.MonthFormat getMonthFormat() { return monthFormat; }

    /**
     * Whether the year is rendered as four digits ({@code 2026}) or two digits
     * ({@code 26}) in a formatted date interpretation.
     *
     * @see GaiaConstants.YearFormat
     */
    public GaiaConstants.YearFormat getYearFormat() { return yearFormat; }

    /**
     * The language used for error messages produced during parsing.
     *
     * @see GaiaConstants.Language
     */
    public GaiaConstants.Language getLanguage() { return language; }

    /**
     * Whether the structural "requires" check (GE-S005 — missing required AI
     * dependencies) is skipped during syntax validation.
     *
     * <p>Defaults to {@code false} — the check runs unless explicitly disabled.
     */
    public boolean isSkipRequiresCheck() { return skipRequiresCheck; }

    /**
     * Whether the structural "excludes" check (GE-S006 — excluded AI pairings)
     * is skipped during syntax validation.
     *
     * <p>Defaults to {@code false} — the check runs unless explicitly disabled.
     */
    public boolean isSkipExcludesCheck() { return skipExcludesCheck; }

    /**
     * The input modifiers to run — in order — before any parsing takes place.
     *
     * <p>Empty by default. Each modifier receives the previous one's output; the first
     * receives the raw input exactly as passed to
     * {@link tools.pantheum.gaia.GaiaParser#parse(String, ParseConfig)}. What the chain
     * did is reported on the result via
     * {@link tools.pantheum.gaia.result.ParseResult#getModifierInfo()}.
     *
     * @return an unmodifiable list; never {@code null}
     * @see ModifierInterface
     */
    public List<ModifierInterface> getModifiers() { return modifiers; }

    /** Returns {@code true} if at least one input modifier is configured. */
    public boolean hasModifiers() { return !modifiers.isEmpty(); }

    /**
     * Maps this config's {@link #getRequestedParseMode()} to the equivalent
     * {@link GS1Constants.ParseMode} used by the internal GS1 AI parser.
     *
     * <ul>
     *   <li>{@link GaiaConstants.ParseMode#DATA_CARRIER}  → {@code null}
     *       (carrier-level checks are complete before AI parsing begins; the GS1 AI
     *       pipeline is not entered at all)</li>
     *   <li>{@link GaiaConstants.ParseMode#SYNTAX}        → {@link GS1Constants.ParseMode#SYNTAX}</li>
     *   <li>{@link GaiaConstants.ParseMode#CONTENT}       → {@link GS1Constants.ParseMode#CONTENT}</li>
     *   <li>{@link GaiaConstants.ParseMode#INTERPRETATION}→ {@link GS1Constants.ParseMode#INTERPRETATION}</li>
     * </ul>
     *
     * @return the {@link GS1Constants.ParseMode} that corresponds to this config's parse mode,
     *         or {@code null} when {@link GaiaConstants.ParseMode#DATA_CARRIER} — the GS1 AI
     *         pipeline is not entered for carrier-only parses
     */
    public GS1Constants.ParseMode toGs1ParseMode() {
        switch (requestedParseMode) {
            case DATA_CARRIER:   return null;
            case SYNTAX:         return GS1Constants.ParseMode.SYNTAX;
            case CONTENT:        return GS1Constants.ParseMode.CONTENT;
            case INTERPRETATION: return GS1Constants.ParseMode.INTERPRETATION;
            default: throw new IllegalStateException("Unhandled ParseMode: " + requestedParseMode);
        }
    }

    // -------------------------------------------------------------------------

    /** Builder for {@link ParseConfig}. */
    public static final class Builder {

        private GaiaConstants.ParseMode     requestedParseMode     = GaiaConstants.ParseMode.INTERPRETATION;
        private GaiaConstants.DateEndian    dateEndian    = GaiaConstants.DateEndian.LITTLE;
        private GaiaConstants.DateSeparator dateSeparator = GaiaConstants.DateSeparator.SLASH;
        private GaiaConstants.MonthFormat   monthFormat   = GaiaConstants.MonthFormat.TWO_DIGIT;
        private GaiaConstants.YearFormat    yearFormat    = GaiaConstants.YearFormat.FOUR_DIGIT;
        private GaiaConstants.Language      language      = GaiaConstants.Language.ENGLISH;
        private boolean                     skipRequiresCheck = false;
        private boolean                     skipExcludesCheck = false;
        private final List<ModifierInterface> modifiers       = new ArrayList<>();

        private Builder() {}

        /**
         * Sets the pipeline depth.
         * Defaults to {@link GaiaConstants.ParseMode#INTERPRETATION} if not called.
         */
        public Builder requestedParseMode(GaiaConstants.ParseMode requestedParseMode) {
            this.requestedParseMode = requestedParseMode;
            return this;
        }

        /**
         * Sets the date component order.
         * Defaults to {@link GaiaConstants.DateEndian#LITTLE} if not called.
         */
        public Builder dateEndian(GaiaConstants.DateEndian dateEndian) {
            this.dateEndian = dateEndian;
            return this;
        }

        /**
         * Sets the separator placed between date components.
         * Defaults to {@link GaiaConstants.DateSeparator#SLASH} if not called.
         */
        public Builder dateSeparator(GaiaConstants.DateSeparator dateSeparator) {
            this.dateSeparator = dateSeparator;
            return this;
        }

        /**
         * Sets whether the month is rendered as two digits or a three-letter abbreviation.
         * Defaults to {@link GaiaConstants.MonthFormat#TWO_DIGIT} if not called.
         */
        public Builder monthFormat(GaiaConstants.MonthFormat monthFormat) {
            this.monthFormat = monthFormat;
            return this;
        }

        /**
         * Sets whether the year is rendered as four digits or two digits.
         * Defaults to {@link GaiaConstants.YearFormat#FOUR_DIGIT} if not called.
         */
        public Builder yearFormat(GaiaConstants.YearFormat yearFormat) {
            this.yearFormat = yearFormat;
            return this;
        }

        /**
         * Sets the language used for error messages.
         * Defaults to {@link GaiaConstants.Language#ENGLISH} if not called.
         */
        public Builder language(GaiaConstants.Language language) {
            this.language = language;
            return this;
        }

        /**
         * Sets whether the structural "requires" check (GE-S005) is skipped.
         * Defaults to {@code false} (the check runs) if not called.
         */
        public Builder skipRequiresCheck(boolean skipRequiresCheck) {
            this.skipRequiresCheck = skipRequiresCheck;
            return this;
        }

        /**
         * Sets whether the structural "excludes" check (GE-S006) is skipped.
         * Defaults to {@code false} (the check runs) if not called.
         */
        public Builder skipExcludesCheck(boolean skipExcludesCheck) {
            this.skipExcludesCheck = skipExcludesCheck;
            return this;
        }

        /**
         * Appends an input modifier to the chain.
         *
         * <p>Modifiers run before any parsing, in the order they are added here. Passing
         * {@code null} is a no-op.
         *
         * @param modifier the modifier instance; must be stateless and thread-safe
         * @see ModifierInterface
         */
        public Builder modifier(ModifierInterface modifier) {
            if (modifier != null) this.modifiers.add(modifier);
            return this;
        }

        /**
         * Appends an input modifier to the chain by fully-qualified class name.
         *
         * <p>The class is resolved and instantiated immediately by
         * {@link ModifierRegistry} — a bad class name fails here, when the config is
         * built, rather than silently doing nothing at parse time. The instance is cached
         * and reused by every later config naming the same class. Passing {@code null} or
         * a blank string is a no-op.
         *
         * @param className fully-qualified name of a public class with a public no-argument
         *                  constructor that implements {@link ModifierInterface}
         * @throws IllegalArgumentException if the class cannot be found, does not implement
         *                                  {@link ModifierInterface}, or cannot be instantiated
         */
        public Builder modifierClass(String className) {
            if (className != null && !className.trim().isEmpty()) {
                this.modifiers.add(ModifierRegistry.INSTANCE.resolve(className));
            }
            return this;
        }

        /**
         * Appends every modifier in {@code modifiers} to the chain, preserving iteration order.
         * {@code null} entries and a {@code null} list are ignored.
         *
         * @see #modifier(ModifierInterface)
         */
        public Builder modifiers(List<ModifierInterface> modifiers) {
            if (modifiers != null) {
                for (ModifierInterface modifier : modifiers) modifier(modifier);
            }
            return this;
        }

        /**
         * Appends every named modifier class to the chain, preserving iteration order.
         * This is the list form used when the modifier chain comes from external
         * configuration. {@code null} and blank entries, and a {@code null} list, are ignored.
         *
         * @throws IllegalArgumentException if any class cannot be resolved — see
         *                                  {@link #modifierClass(String)}
         */
        public Builder modifierClasses(List<String> classNames) {
            if (classNames != null) {
                for (String className : classNames) modifierClass(className);
            }
            return this;
        }

        /** Builds and returns an immutable {@link ParseConfig}. */
        public ParseConfig build() {
            return new ParseConfig(requestedParseMode, dateEndian, dateSeparator, monthFormat, yearFormat, language,
                    skipRequiresCheck, skipExcludesCheck, modifiers);
        }
    }
}
