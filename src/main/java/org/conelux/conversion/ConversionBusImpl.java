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

package org.conelux.conversion;

import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.conelux.common.validate.Check;
import org.conelux.conversion.converter.ConditionalConverter;
import org.conelux.conversion.converter.Converter;
import org.conelux.conversion.converter.ConverterCondition;
import org.conelux.conversion.converter.ConverterFactory;
import org.conelux.conversion.exception.ConversionException;
import org.conelux.conversion.exception.ConverterNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked"})
sealed class ConversionBusImpl implements ConversionBus permits DefaultConversionBus {

    private static final ConditionalConverter<Object, Object> NO_OP_CONVERTER = new NoOpConverter();

    private final Set<ConditionalConverter<Object, Object>> converters;
    private final Map<Key, ConditionalConverter<Object, Object>> cache = new ConcurrentHashMap<>(64);

    ConversionBusImpl() {
        this.converters = new HashSet<>();
    }

    @Override
    public <U, V> void register(Class<? extends U> source, Class<V> target, Converter<U, V> converter) {
        this.converters.add(new ConverterAdapter(converter, source, target));
    }

    @Override
    public void register(ConditionalConverter<?, ?> converter) {
        this.converters.add((ConditionalConverter<Object, Object>) converter);
    }

    @Override
    public <U, V> void register(Class<? extends U> source, Class<V> target, ConverterFactory<?, ?> converterFactory) {
        this.converters.add(new ConverterFactoryAdapter(converterFactory, source, target));
    }

    @Override
    public boolean canConvert(@NotNull Type sourceType, @NotNull Type targetType) {
        Check.notNull(sourceType, "sourceType");
        Check.notNull(targetType, "targetType");
        return this.getConverter(GenericTypeReflector.box(sourceType), GenericTypeReflector.box(targetType)) != null;
    }

    @Override
    public @NotNull Object convert(@NotNull Object source, @NotNull Type sourceType, @NotNull Type targetType)
        throws ConversionException {
        Check.notNull(source, "source");
        Check.notNull(targetType, "targetType");

        targetType = GenericTypeReflector.box(targetType);
        Converter<Object, Object> converter = this.getConverter(sourceType, targetType);

        if (converter == null) {
            // No Converter found
            throw new ConverterNotFoundException(sourceType, targetType);
        }

        return converter.convert(source, sourceType, targetType);
    }

    private @Nullable Converter<Object, Object> getConverter(@NotNull Type sourceType, @NotNull Type targetType) {
        return this.cache.computeIfAbsent(new Key(sourceType, targetType), param -> {
            for (ConditionalConverter<Object, Object> conv : this.converters) {
                if (conv.matches(param.sourceType(), param.targetType())) {
                    return conv;
                }
            }

            if (GenericTypeReflector.isSuperType(sourceType, targetType)) {
                return NO_OP_CONVERTER;
            }

            return null;
        });
    }

    private static final class ConverterAdapter implements ConditionalConverter<Object, Object> {

        private final Converter<Object, Object> converter;
        private final Type sourceType;
        private final Type targetType;

        public ConverterAdapter(Converter<?, ?> converter, Type sourceType, Type targetType) {
            this.converter = (Converter<Object, Object>) converter;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public @NotNull Object convert(@NotNull Object obj, @NotNull Type sourceType,
            @NotNull Type targetType) throws ConversionException {
            return this.converter.convert(obj, sourceType, targetType);
        }

        @Override
        public boolean matches(Type sourceType, Type targetType) {
            if (this.targetType != targetType) {
                return false;
            }

            return GenericTypeReflector.isSuperType(this.sourceType, sourceType);
        }
    }

    private static final class ConverterFactoryAdapter implements ConditionalConverter<Object, Object> {

        private final ConverterFactory<Object, Object> converterFactory;
        private final Class<?> sourceType;
        private final Class<?> targetType;

        public ConverterFactoryAdapter(ConverterFactory<?, ?> converterFactory, Class<?> sourceType,
            Class<?> targetType) {
            this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public @NotNull Object convert(@NotNull Object obj, @NotNull Type sourceType,
            @NotNull Type targetType) throws ConversionException {
            return this.converterFactory.create(GenericTypeReflector.erase(targetType))
                .convert(obj, sourceType, targetType);
        }

        @Override
        public boolean matches(Type sourceType, Type targetType) {
            if (this.converterFactory instanceof ConverterCondition condition && condition.matches(sourceType,
                targetType)) {
                Converter<?, ?> converter = this.converterFactory.create(GenericTypeReflector.erase(targetType));
                if (converter instanceof ConverterCondition converterCondition) {
                    return converterCondition.matches(sourceType, targetType);
                }
            }

            if (!GenericTypeReflector.isSuperType(this.targetType, targetType)) {
                return false;
            }

            return GenericTypeReflector.isSuperType(this.sourceType, sourceType);
        }
    }

    record Key(Type sourceType, Type targetType) {

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Key otherKey)) {
                return false;
            }
            return (this.sourceType.equals(otherKey.sourceType)) && this.targetType.equals(otherKey.targetType);
        }
    }

    private static class NoOpConverter implements ConditionalConverter<Object, Object> {

        @Override
        public @NotNull Object convert(@NotNull Object source, @NotNull Type sourceType, @NotNull Type targetType) {
            return source;
        }

        @Override
        public boolean matches(Type sourceType, Type targetType) {
            return true;
        }
    }
}
