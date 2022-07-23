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

package de.natrox.serialize.parse;

import de.natrox.common.consumer.ThrowableConsumer;
import de.natrox.serialize.ParserCollection;
import de.natrox.serialize.exception.SerializeException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

final class ListParserImpl<T> extends AbstractListChildParser<List<T>> implements ListParser<T> {

    ListParserImpl(Type type, ParserCollection collection) {
        super(type, collection);
    }

    protected Type elementType(Type containerType) throws SerializeException {
        if (!(containerType instanceof ParameterizedType)) {
            throw new SerializeException(containerType, "Raw types are not supported for collections");
        }
        return ((ParameterizedType) containerType).getActualTypeArguments()[0];
    }

    @Override
    protected List<T> createNew(int length, Type elementType) {
        return new ArrayList<>(length);
    }

    @Override
    protected void forEachElement(List<T> collection, ThrowableConsumer<Object, SerializeException> action) throws SerializeException {
        for (Object el : collection) {
            action.accept(el);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void deserializeSingle(int index, List<T> collection, Object deserialized) {
        ((List) collection).add(deserialized);
    }
}
