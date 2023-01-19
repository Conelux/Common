/*
 * Copyright 2020-2023 AeroService
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

package org.aero.common.core.function;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuadFunctionTest {

    @Test
    void testApply() {
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> function = this::sum;
        assertEquals(10, function.apply(1, 2, 3, 4), "Function should return the input sum of 10.");
        assertEquals(14, function.apply(5, 4, 3, 2), "Function should return the input sum of 14.");
    }

    @Test
    void testNullApply() {
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> function = this::sum;
        assertThrows(NullPointerException.class, () ->
                function.apply(null, null, null, null),
            "Function should throw a NullPointerException if the arguments are null");
    }

    @Test
    void testAndThenApply() {
        final Function<Integer, Integer> andThenFunction = (a) -> (0);
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> operation = this::sum;
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> function = operation.andThen(andThenFunction);
        assertEquals(0, function.apply(1, 2, 3, 4), "Function should return zero");
        assertEquals(0, function.apply(5, 4, 3, 2), "Function should return zero");
    }

    @Test
    void testAndThenNull() {
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> function = this::sum;
        assertThrows(IllegalArgumentException.class, () ->
            function.andThen(null), "Function should throw a IllegalArgumentException if the andThen function is invalid");
    }

    @Test
    void testAndThenExecution() {
        final AtomicInteger indicator = new AtomicInteger();
        final Function<Integer, Integer> andThenFunction = (a) -> (indicator.incrementAndGet());
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> operation = (a, b, c, d) -> {
            throw new IllegalArgumentException();
        };
        final QuadFunction<Integer, Integer, Integer, Integer, Integer> function = operation.andThen(andThenFunction);
        assertThrows(IllegalArgumentException.class, () -> function.apply(1, 2, 3, 4), "The operation should fail");
        assertEquals(0, indicator.get(), "AndThenFunction should not have executed since the operation failed");
    }

    private int sum(final int a, final int b, final int c, final int d) {
        return a + b + c + d;
    }
}
