package tools.pantheum.gaia.result;

import java.time.Duration;
import java.time.Instant;

/**
 * A running timer returned by {@link ProcessingTiming#start()}. Call {@link #stop()} when the
 * timed operation completes to obtain an immutable {@link ProcessingTiming}.
 *
 * <pre>{@code
 * Started timer = ProcessingTiming.start();
 * ... do work ...
 * ProcessingTiming timing = timer.stop();
 * }</pre>
 */
public final class Started {

    private final Instant startTime;
    private final long    startNanos;

    /** Package-private: instances are created only by {@link ProcessingTiming#start()}. */
    Started(Instant startTime, long startNanos) {
        this.startTime  = startTime;
        this.startNanos = startNanos;
    }

    /** Stops the timer, returning an immutable {@link ProcessingTiming}. */
    public ProcessingTiming stop() {
        return new ProcessingTiming(startTime, Duration.ofNanos(System.nanoTime() - startNanos));
    }
}
