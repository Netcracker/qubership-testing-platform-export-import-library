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

package org.qubership.atp.ei.ntt.settings.store.file.apache;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.qubership.atp.ei.ntt.settings.model.AdditionalOption;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.Options;
import org.qubership.atp.ei.ntt.settings.store.option.AbstractOptionProcessor;
import org.qubership.atp.ei.ntt.settings.store.option.OptionProcessException;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class ApacheOptionProcessor extends AbstractOptionProcessor<HierarchicalConfiguration> {

    private static final List<Class<?>> primitives = Lists.newArrayList(String.class, boolean.class,
            Boolean.class, int.class, Integer.class, double.class,
            Double.class, float.class, Float.class, long.class, Long.class);
    private final Logger log = Logger.getLogger(ApacheOptionProcessor.class);

    @Override
    public Object loadOption(final Field field, final HierarchicalConfiguration source) throws OptionProcessException {
        String key = field.getAnnotation(Option.class).key();
        Class<?> type = field.getType();
        return getProperty(type, key, source);
    }

    private Object getProperty(final Class type,
                               final String key,
                               final HierarchicalConfiguration source) throws OptionProcessException {
        String shortCanonicalName = ClassUtils.getShortCanonicalName(type);
        try {
            Method method = source.getClass()
                                  .getMethod(String.format("get%s", StringUtils.capitalize(shortCanonicalName)),
                                             String.class);
            return method.invoke(source, key);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new OptionProcessException(e instanceof InvocationTargetException
                                             ? ((InvocationTargetException) e).getTargetException()
                                             : e);
        }
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public Object loadUnclassifiedParameters(final Field field,
                                             final HierarchicalConfiguration source) throws OptionProcessException {
        Map<String, String> unclassMap = new HashMap<>();
        AdditionalOption additionalOption = field.getAnnotation(AdditionalOption.class);
        if (additionalOption != null) {
            String additionalKey = additionalOption.key();
            Iterator<String> keys = source.getKeys(additionalKey);
            Class<?> type = String.class;
            while (keys.hasNext()) {
                String key = keys.next();
                unclassMap.put(key, (String) getProperty(type, key, source));
            }
        }
        return unclassMap;
    }

    @Override
    public Object loadOptions(final Field field, final HierarchicalConfiguration source) throws OptionProcessException {
        String key = field.getAnnotation(Options.class).key();
        Type fieldGenericType = field.getGenericType();
        if (fieldGenericType instanceof ParameterizedType) {
            Class<?> fieldClass = (Class<?>) ((ParameterizedType) fieldGenericType).getRawType();
            Type fieldTypeParameter = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
            if (Collection.class.isAssignableFrom(fieldClass)) {
                List<Object> values = Lists.newArrayListWithCapacity(source.getMaxIndex(key) + 5);
                Class<?> collectionClass;
                if (fieldTypeParameter instanceof ParameterizedType) {
                    collectionClass = (Class<?>) ((ParameterizedType) fieldTypeParameter).getRawType();
                } else {
                    collectionClass = (Class<?>) fieldTypeParameter;
                }
                for (int i = 0; i < source.getMaxIndex(key) + 1; i++) {
                    String newKey = String.format("%s(%s)", key, i);
                    HierarchicalConfiguration configuration = source.configurationAt(newKey);
                    if (primitives.contains(collectionClass)) {
                        values.add(getProperty(collectionClass, newKey, source));
                    } else {
                        try {
                            values.add(load(collectionClass.newInstance(), configuration));
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new OptionProcessException(e);
                        }
                    }
                }
                return values;
            } else {
                throw new OptionProcessException(String.format("Unsupported raw type: %s", fieldClass));
            }
        } else {
            throw new OptionProcessException(String.format("Unknown field type: %s", fieldGenericType));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Y> Y load(final Y object, final HierarchicalConfiguration source) {
        Class<?> daoClass = object.getClass();
        while (!daoClass.getSuperclass().equals(Object.class)) {
            for (Field field : daoClass.getDeclaredFields()) {
                try {
                    Object value;
                    if (field.getAnnotation(Option.class) != null) {
                        value = loadOption(field, source);
                        setFieldValue(object, field, value);
                    } else if (field.getAnnotation(Options.class) != null) {
                        value = loadOptions(field, source);
                        setFieldValue(object, field, value);
                    } else if (field.getAnnotation(AdditionalOption.class) != null) {
                        value = loadUnclassifiedParameters(field, source);
                        setFieldValue(object, field, value);
                    }
                } catch (OptionProcessException e) {
                    log.warn("Error loading option", e);
                }
            }
            daoClass = daoClass.getSuperclass();
        }
        return object;
    }

    private void setFieldValue(final Object object, final Field field, final Object value) {
        Object val = value;
        try {
            field.setAccessible(true);
            if (value != null) {
                val = field.getType().getSimpleName().equals("String") ? ((String) value).trim() : value;
            }
            field.set(object, val);
        } catch (IllegalAccessException e) {
            log.error(e);
        } finally {
            field.setAccessible(false);
        }
    }

    @Override
    public void saveOption(final Object object, final Field field, final HierarchicalConfiguration source) {
        setProperty(field.getAnnotation(Option.class).key(), getFieldValue(object, field), source);
    }

    private void setProperty(final String key, final Object value, final HierarchicalConfiguration source) {
        source.setProperty(key, value);
    }

    private void generateTreeByKey(final String key, final HierarchicalConfiguration configuration) {
        ConfigurationNode rootNode = configuration.getRootNode();
        Iterable<String> split = Splitter.on('.').split(key);
        for (String nodeName : split) {
            int index = 0;
            Matcher matcher = Pattern.compile("\\((\\d+)\\)").matcher(nodeName);
            if (matcher.find()) {
                index = Integer.parseInt(matcher.group(1));
            }
            if (rootNode.getChildren(nodeName).size() <= index) {
                rootNode.addChild(new HierarchicalConfiguration.Node(
                        nodeName.replaceAll("\\(\\d+\\)", StringUtils.EMPTY)));
            }
            rootNode = rootNode.getChildren(nodeName.replaceAll("\\(\\d+\\)", StringUtils.EMPTY)).get(index);
        }
    }

    @Override
    protected void iterate(final String key,
                           final HierarchicalConfiguration source,
                           final List<?> toIterate,
                           final Class<?> collectionClass) {
        for (int i = 0; i < toIterate.size(); i++) {
            if (source.getMaxIndex(key) < i) {
                if (key.lastIndexOf('.') > 0) {
                    source.addNodes(
                            key.substring(0, key.lastIndexOf('.')),
                            Lists.newArrayList(
                                    new HierarchicalConfiguration.Node(
                                            key.substring(key.lastIndexOf('.') + 1))));
                } else {
                    source.getRootNode().addChild(new HierarchicalConfiguration.Node(key));
                }
            }
            String newKey = String.format("%s(%s)", key, i);
            try {
                HierarchicalConfiguration configuration = source.configurationAt(newKey);
                if (primitives.contains(collectionClass)) {
                    setProperty(newKey, toIterate.get(i), source);
                } else {
                    save(toIterate.get(i), configuration);
                }
            } catch (Exception e) {
                log.error(StringUtils.EMPTY, e);
            }
        }
        //yeah, clear garbage
        while (source.getMaxIndex(key) >= toIterate.size()) {
            String newKey = String.format("%s(%s)", key, source.getMaxIndex(key));
            ConfigurationNode rootNode = source.configurationAt(newKey).getRootNode();
            cyclicRemoveNode(source.getRootNode(), rootNode);
        }
    }

    /**
     * Get Logger.
     *
     * @return Logger object.
     */
    protected Logger getLog() {
        return log;
    }

    private void cyclicRemoveNode(final ConfigurationNode root, final ConfigurationNode toRemove) {
        if (root.getChildren().contains(toRemove)) {
            root.removeChild(toRemove);
        } else {
            for (ConfigurationNode configurationNode : root.getChildren()) {
                cyclicRemoveNode(configurationNode, toRemove);
            }
        }
    }
}
