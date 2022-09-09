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

package de.natrox.common.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result. Function might throw a checked
 * {@link Throwable}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <U> the type of the potentially thrown {@link Throwable}
 * @see Function
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, U extends Throwable> {

    /**
     * Returns a {@code ThrowableFunction} that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @param <U> the type of the potentially thrown {@link Throwable}
     * @return a function that always returns its input argument
     */
    static <T, U extends Throwable> ThrowableFunction<T, T, U> identity() {
        return t -> t;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the first function argument
     * @return the function result
     * @throws U the potentially thrown {@link Throwable}
     */
    R apply(T t) throws U;

    /**
     * Returns a composed {@code ThrowableFunction} that first applies the {@code before} function to its input, and
     * then applies this function to the result. If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V>    the type of input to the {@code before} function, and to the composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before} function and then applies this function
     * @throws NullPointerException if before is null
     * @see #andThen(Function)
     */
    default <V> ThrowableFunction<V, R, U> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed {@code ThrowableFunction} that first applies this function to its input, and then applies the
     * {@code after} function to the result. If evaluation of either function throws an exception, it is relayed to the
     * caller of the composed function.
     *
     * @param <V>   the type of output of the {@code after} function, and of the composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then applies the {@code after} function
     * @throws NullPointerException if after is null
     * @see #compose(Function)
     */
    default <V> ThrowableFunction<T, V, U> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }
}
