/*
 * # Copyright 2024-2025 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.ei.ntt.settings.store.option;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.Options;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractOptionProcessor<T> implements OptionProcessor<T> {

    @Override
    public void save(Object object, T source) {
        Class<?> daoClass = object.getClass();
        while (!daoClass.getSuperclass().equals(Object.class)) {
            for (Field field : daoClass.getDeclaredFields()) {
                if (field.getAnnotation(Option.class) != null) {
                    try {
                        saveOption(object, field, source);
                    } catch (OptionProcessException e) {
                        getLog().error(e);
                    }
                } else if (field.getAnnotation(Options.class) != null) {
                    try {
                        saveOptions(object, field, source);
                    } catch (OptionProcessException e) {
                        getLog().error(e);
                    }
                }
            }
            daoClass = daoClass.getSuperclass();
        }
    }

    @Override
    public void saveOptions(Object object, Field field, T source) throws OptionProcessException {
        Type fieldGenericType = field.getGenericType();
        if (fieldGenericType instanceof ParameterizedType) {
            Class<?> fieldClass = (Class<?>) ((ParameterizedType) fieldGenericType).getRawType();
            Type fieldTypeParameter = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
            if (Collection.class.isAssignableFrom(fieldClass)) {
                Class<?> collectionClass;
                if (fieldTypeParameter instanceof ParameterizedType) {
                    collectionClass = (Class<?>) ((ParameterizedType) fieldTypeParameter).getRawType();
                } else {
                    collectionClass = (Class<?>) fieldTypeParameter;
                }
                Object fieldValue = getFieldValue(object, field);
                List<?> toIterate;
                if (fieldValue == null) {
                    toIterate = Lists.newArrayList();
                } else {
                    if (fieldValue instanceof List) {
                        toIterate = (List) fieldValue;
                    } else {
                        toIterate = Lists.newArrayListWithCapacity(Iterables.size((Iterable<?>) fieldValue));
                        toIterate.addAll((Collection) fieldValue);
                    }
                }
                String key = field.getAnnotation(Options.class).key();

                iterate(key, source, toIterate, collectionClass);
            } else {
                throw new OptionProcessException(String.format("Unsupported raw type: %s", fieldClass));
            }
        } else {
            throw new OptionProcessException(String.format("Unknown field type: %s", fieldGenericType));
        }
    }

    protected abstract void iterate(String xpath, T source, List<?> toIterate, Class<?> collectionClass);

    protected Object getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            getLog().error(e);
            return null;
        } finally {
            field.setAccessible(false);
        }
    }

    protected abstract Logger getLog();
}
