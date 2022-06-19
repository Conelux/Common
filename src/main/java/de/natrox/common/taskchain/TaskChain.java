package de.natrox.common.taskchain;

import de.natrox.common.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * Represents a chain of tasks
 */
public sealed interface TaskChain permits TaskChainImpl {

    /**
     * Execute the task on the main thread
     *
     * @param task the task to execute
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskChain sync(@NotNull Task task);

    /**
     * Execute the task on a extra thread
     *
     * @param task the task to execute
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskChain async(@NotNull Task task);

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration the duration of the delay before next task
     * @param timeUnit the {@link TimeUnit} in which the duration is specified
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskChain delay(@Range(from = 0, to = Long.MAX_VALUE) long duration, @NotNull TimeUnit timeUnit);

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration the {@link Duration} of the delay before next task
     * @return this TaskBatch, for chaining
     */
    default @NotNull TaskChain delay(@NotNull Duration duration) {
        Check.notNull(duration, "duration");
        return this.delay(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration     the duration of the delay before next task
     * @param temporalUnit the {@link TemporalUnit} in which the duration is specified
     * @return this TaskBatch, for chaining
     */
    default @NotNull TaskChain delay(@Range(from = 0, to = Long.MAX_VALUE) long duration, @NotNull TemporalUnit temporalUnit) {
        Check.notNull(temporalUnit, "temporalUnit");
        return this.delay(Duration.of(duration, temporalUnit));
    }

    /**
     * Finished adding tasks, begins executing them with a done notifier
     *
     * @param callback the {@link Runnable} to handle when the batch has finished completion
     */
    void run(@Nullable Runnable callback);

    /**
     * Finished adding tasks, begins executing them.
     */
    default void run() {
        this.run(null);
    }

}
