/*
 * Copyright 2020-2022 NatroxMC
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

package de.natrox.common.counter;

import de.natrox.common.scheduler.Scheduler;
import de.natrox.common.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Represents a clocked Counter, for example a countdown
 */
public sealed interface Counter permits CounterImpl {

    Scheduler DEFAULT_SCHEDULER = Scheduler.create();

    /**
     * Create a {@link Builder} for a {@link Counter} scheduling the tasks with the default{@link Scheduler}
     *
     * @return the Builder
     */
    static Counter.@NotNull Builder builder() {
        return new CounterImpl.BuilderImpl(DEFAULT_SCHEDULER);
    }

    /**
     * Create a {@link Builder} for a {@link Counter} scheduling the tasks with the passed {@link Scheduler}
     *
     * @param scheduler the {@link Scheduler}
     * @return the Builder
     */
    static Counter.@NotNull Builder builder(@NotNull Scheduler scheduler) {
        return new CounterImpl.BuilderImpl(scheduler);
    }

    /**
     * Starts the {@link Counter} if its {@link CounterStatus} is IDLING
     * Sets the status to RUNNING
     */
    void start();

    /**
     * Pauses the {@link Counter} if its {@link CounterStatus} is RUNNING
     * Sets the status to PAUSED
     */
    void pause();

    /**
     * Resumes the {@link Counter} if its {@link CounterStatus} is PAUSED
     * Sets the status to RUNNING
     */
    void resume();

    /**
     * Stops the {@link Counter} if its {@link CounterStatus} not IDLING
     * Sets the status to IDLING
     */
    void stop();

    /**
     * @return true if, and only if the {@link Counter} its {@link CounterStatus} is PAUSED
     */
    boolean isPaused();

    /**
     * @return true if, and only if the {@link Counter} its {@link CounterStatus} is RUNNING
     */
    boolean isRunning();

    /**
     * @return the current {@link CounterStatus}
     */
    @NotNull CounterStatus status();

    /**
     * @return the number with witch the {@link Counter} starts
     */
    long startCount();

    /**
     * @return the number with witch the {@link Counter} stops
     */
    long stopCount();

    /**
     * @return the value of a single tick
     */
    long tickValue();

    /**
     * @return the amount of counted numbers after start
     */
    long tickedTime();

    /**
     * @return the amount of counted numbers after start
     */
    long currentCount();

    /**
     * Sets the current number the specified value
     *
     * @param currentCount the new current number
     */
    void currentCount(long currentCount);

    /**
     * @return the {@link TimeUnit} to multiply with the tickValue to get the delay between two ticks
     */
    @NotNull TimeUnit tickUnit();

    /**
     * Represents a Builder for a {@link Counter}
     */
    sealed interface Builder permits CounterImpl.BuilderImpl {

        /**
         * Sets the startCount to the specified value
         *
         * @param startCount the startCount
         * @return the {@link Builder}
         */
        @NotNull Builder startCount(long startCount);

        /**
         * Sets the stopCount to the specified value
         *
         * @param stopCount the stopCount
         * @return the {@link Builder}
         */
        @NotNull Builder stopCount(long stopCount);

        /**
         * Sets the delay between two ticks
         *
         * @param tick     the amount of tickUnits needed to the next tick
         * @param tickUnit the matching type to the tick parameter
         * @return the {@link Builder}
         */
        @NotNull Builder tick(@Range(from = 0, to = Long.MAX_VALUE) long tick, @NotNull TimeUnit tickUnit);

        /**
         * Sets the delay between two ticks
         *
         * @param tick     the amount of tickUnits needed to the next tick
         * @param tickUnit the matching type to the tick parameter
         * @return the {@link Builder}
         */
        default @NotNull Builder tick(@Range(from = 0, to = Long.MAX_VALUE) long tick, @NotNull ChronoUnit tickUnit) {
            Check.argCondition(tick <= 0, "tick must be positive");
            Check.notNull(tickUnit, "tickUnit");
            return tick(tick, TimeUnit.of(tickUnit));
        }

        /**
         * Sets the startHandler
         * Gets executed if the {@link Counter} is started
         *
         * @param startHandler a consumer consuming the counter
         * @return the {@link Builder}
         */
        @NotNull Builder startHandler(@Nullable Consumer<Counter> startHandler);

        /**
         * Sets the tickHandler
         * Gets executed if the {@link Counter} ticked
         *
         * @param tickHandler a consumer consuming the counter
         * @return the {@link Builder}
         */
        @NotNull Builder tickHandler(@Nullable Consumer<Counter> tickHandler);

        /**
         * Sets the finishHandler
         * Gets executed if the {@link Counter} finished counting
         *
         * @param finishHandler a consumer consuming the counter
         * @return the {@link Builder}
         */
        @NotNull Builder finishHandler(@Nullable Consumer<Counter> finishHandler);

        /**
         * Sets the cancelHandler
         * Gets executed if the {@link Counter} is canceled
         *
         * @param cancelHandler a consumer consuming the counter
         * @return the {@link Builder}
         */
        @NotNull Builder cancelHandler(@Nullable Consumer<Counter> cancelHandler);

        /**
         * Builds the {@link Counter}
         *
         * @return the Counter
         */
        @NotNull Counter build();

    }
}
