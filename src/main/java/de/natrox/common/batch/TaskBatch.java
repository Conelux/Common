/*
 * Copyright 2020-2022 NatroxMC team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.natrox.common.batch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a chain of tasks
 */
public sealed interface TaskBatch permits SimpleTaskBatch {

    /**
     * Execute the task on the main thread
     *
     * @param runnable the task to execute
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskBatch sync(@NotNull Runnable runnable);

    /**
     * Execute the task on a extra thread
     *
     * @param runnable the task to execute
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskBatch async(@NotNull Runnable runnable);

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration the duration of the delay before next task
     * @param temporalUnit the {@link TemporalUnit} in which the duration is specified
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskBatch wait(long duration, @NotNull TemporalUnit temporalUnit);

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration the {@link Duration} of the delay before next task
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskBatch wait(Duration duration);

    /**
     * Adds a delay to the batch execution.
     *
     * @param duration the duration of the delay before next task
     * @param timeUnit the {@link TimeUnit} in which the duration is specified
     * @return this TaskBatch, for chaining
     */
    @NotNull TaskBatch wait(long duration, @NotNull TimeUnit timeUnit);

    /**
     * Finished adding tasks, begins executing them with a done notifier
     *
     * @param callback the {@link Runnable} to handle when the batch has finished completion
     */
    void execute(@Nullable Runnable callback);

    /**
     * Finished adding tasks, begins executing them.
     */
    default void execute() {
        execute(null);
    }

    /**
     * Aborts the batch and all its tasks and returns a list of the tasks
     * that were awaiting execution.
     *
     * @return a list of tasks that never commenced execution
     */
    @NotNull List<Runnable> interrupt();

    /**
     * Represents a factory for {@link TaskBatch}
     */
    interface Factory {

        /**
         * Creates a new {@link TaskBatch}.
         *
         * @return the created {@link TaskBatch}
         */
        TaskBatch createTaskBatch();

    }

}
