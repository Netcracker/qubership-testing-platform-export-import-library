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

package org.qubership.atp.ei.ntt.settings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * TODO Make summary for this class.
 */
public class ReflectionUtils {

    private static Logger log = Logger.getLogger(ReflectionUtils.class);

    /**
     * TODO Make javadoc documentation for this method.
     */
    public static <T> T getInstance(Class<T> clazz) {
        T result = null;
        try {
            result = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public static <T> T getInstance(Class<T> clazz, String value) {
        T resource = null;
        try {
            resource = clazz.getConstructor(new Class[]{String.class}).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            log.error(e.getMessage(), e);
        }
        return resource;
    }

    /**
     * Returns all declared fields for class and its parent classes.
     * @param clazz tagret class to collect fields
     * @return list of Fields
     */
    public static List<Field> getAllDeclaredFields(Class clazz) {

        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllDeclaredFields(clazz.getSuperclass()));
        result.addAll(Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
        return result;
    }
}
