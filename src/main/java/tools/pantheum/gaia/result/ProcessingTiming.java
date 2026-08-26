package tools.pantheum.gaia.result;

import java.time.Duration;
import java.time.Instant;

/**
 * Immutable record of how long a {@link tools.pantheum.gaia.GaiaParser} parse or
 * {@link tools.pantheum.gaia.GaiaBuilder} build took, and when it ran.
 *
 * <p>The wall-clock {@link #getStartTime() start} is captured with {@link Instant#now()};
 * the {@link #getProcessingTime() elapsed} duration is measured with the monotonic
 * {@link System#nanoTime()} clock (which, unlike differencing two {@link Instant}s, is
 * immune to system-clock adjustments and has nanosecond resolution). The
 * {@link #getCompletionTime() completion} timestamp is derived as {@code start + elapsed}.
 *
 * <p>Create instances with {@link #start()} at the beginning of the operation and
 * {@link Started#stop()} at the end:
 * <pre>{@code
 * Started timer = ProcessingTiming.start();
 * ... do work ...
 * ProcessingTiming timing = timer.stop();
 * }</pre>
 */
public final class ProcessingTiming {

    private final Instant  startTime;
    private final Duration processingTime;

    /** Package-private: instances are created via {@link #start()} / {@link Started#stop()}. */
    ProcessingTiming(Instant startTime, Duration processingTime) {
        this.startTime      = startTime;
        this.processingTime = processingTime;
    }

    /**
     * Begins timing an operation, capturing the wall-clock start and a monotonic
     * reference point. Call {@link Started#stop()} when the operation completes.
     */
    public static Started start() {
        return new Started(Instant.now(), System.nanoTime());
    }

    /** The wall-clock time at which the operation started. */
    public Instant getStartTime() { return startTime; }

    /** The elapsed processing time, measured on the monotonic clock. */
    public Duration getProcessingTime() { return processingTime; }

    /** The elapsed processing time in whole milliseconds. */
    public long getProcessingTimeMillis() { return processingTime.toMillis(); }

    /** The wall-clock time at which the operation completed ({@code start + elapsed}). */
    public Instant getCompletionTime() { return startTime.plus(processingTime); }

    @Override
    public String toString() {
        return "ProcessingTiming{start=" + startTime
                + ", completion=" + getCompletionTime()
                + ", processingTime=" + processingTime.toNanos() / 1_000_000.0 + "ms}";
    }
}
