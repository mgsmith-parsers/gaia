package tools.pantheum.gaia.modifier.registry;

import tools.pantheum.gaia.modifier.ModifierInterface;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves {@link ModifierInterface} class names to cached singleton instances.
 *
 * <p>Modifiers are caller-supplied code living in caller-owned packages, so they are
 * referenced by <strong>fully-qualified</strong> class name (unlike the interpretation
 * enrichers and content validators, which ship with the library and are referenced by
 * simple name against a known package). Each class is instantiated once via its public
 * no-argument constructor and cached for the lifetime of the JVM — modifiers are required
 * to be stateless and thread-safe, so one instance serves every parse.
 *
 * <h2>Unknown classes fail fast</h2>
 * {@link #resolve(String)} throws {@link IllegalArgumentException} rather than skipping a
 * class it cannot load. A modifier name comes from the caller's own {@link
 * tools.pantheum.gaia.config.ParseConfig}, so a miss is a configuration error the caller
 * needs to see — not a data-dependent outcome to absorb. Resolution happens when the config
 * is built, so the failure surfaces before any parsing is attempted.
 *
 * <p>This class is thread-safe.
 */
public final class ModifierRegistry {

    public static final ModifierRegistry INSTANCE = new ModifierRegistry();

    /** Maps fully-qualified class name → singleton modifier instance. */
    private final Map<String, ModifierInterface> modifiers = new ConcurrentHashMap<>();

    private ModifierRegistry() {}

    /**
     * Returns the cached modifier instance for the given fully-qualified class name,
     * instantiating it on first use.
     *
     * @param className fully-qualified name of a public class with a public no-argument
     *                  constructor that implements {@link ModifierInterface}
     * @return the shared instance for that class; never {@code null}
     * @throws IllegalArgumentException if {@code className} is {@code null} or blank, the class
     *                                  cannot be found, it does not implement
     *                                  {@link ModifierInterface}, or it cannot be instantiated
     */
    public ModifierInterface resolve(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Modifier class name must not be null or blank");
        }
        return modifiers.computeIfAbsent(className.trim(), ModifierRegistry::instantiate);
    }

    /** Returns {@code true} if {@code className} has already been resolved and cached. */
    public boolean isCached(String className) {
        return className != null && modifiers.containsKey(className.trim());
    }

    // -------------------------------------------------------------------------

    private static ModifierInterface instantiate(String fqn) {
        Class<?> cls;
        try {
            cls = Class.forName(fqn);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Modifier class not found: " + fqn, e);
        }
        if (!ModifierInterface.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(fqn + " does not implement "
                    + ModifierInterface.class.getName());
        }
        try {
            return (ModifierInterface) cls.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Could not instantiate modifier " + fqn
                    + " (a public no-argument constructor is required): " + e, e);
        }
    }

    /**
     * Registers an already-constructed instance under its own class name, so later
     * {@link #resolve(String)} calls for that name return this instance. Intended for
     * modifiers that cannot be built reflectively (e.g. ones holding an injected
     * dependency) but still need to be referenced by name from configuration.
     *
     * @param modifier the instance to register; must be stateless and thread-safe
     * @throws NullPointerException if {@code modifier} is {@code null}
     */
    public void register(ModifierInterface modifier) {
        Objects.requireNonNull(modifier, "modifier");
        modifiers.put(modifier.getClass().getName(), modifier);
    }
}
