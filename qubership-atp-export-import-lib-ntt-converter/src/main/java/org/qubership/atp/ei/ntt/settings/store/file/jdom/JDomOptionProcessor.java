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

package org.qubership.atp.ei.ntt.settings.store.file.jdom;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathFactory;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.Options;
import org.qubership.atp.ei.ntt.settings.store.option.AbstractOptionProcessor;
import org.qubership.atp.ei.ntt.settings.store.option.OptionProcessException;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * TODO Make summary for this class.
 */
public class JDomOptionProcessor extends AbstractOptionProcessor<Element> {

    private static final List<Class<?>> primitives = Lists.<Class<?>>newArrayList(String.class, boolean.class,
            Boolean.class, int.class, Integer.class,
            double.class, Double.class, float.class,
            Float.class, long.class, Long.class);
    private Logger log = Logger.getLogger(JDomOptionProcessor.class);

    private void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            log.error(e);
        } finally {
            field.setAccessible(false);
        }
    }

    private Object getTypedValue(String value, Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return value;
        }
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return Boolean.valueOf(value);
        }
        if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            return Integer.valueOf(value);
        }
        if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            return Double.valueOf(value);
        }
        if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            return Float.valueOf(value);
        }
        if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            return Long.valueOf(value);
        }
        return value;
    }

    @Override
    public Object loadOption(Field field, Element source) throws OptionProcessException {
        String xpath = field.getAnnotation(Option.class).key();
        if (Strings.isNullOrEmpty(xpath)) {
            return getTypedValue(source.getText(), field.getType());
        }
        Element node = XPathFactory.instance().compile(xpath, Filters.element()).evaluateFirst(source);
        if (node != null) {
            return getTypedValue(node.getText(), field.getType());
        } else {
            Attribute attribute = XPathFactory.instance().compile(xpath, Filters.attribute()).evaluateFirst(source);
            if (attribute != null) {
                return getTypedValue(attribute.getValue(), field.getType());
            } else {
                return null;
            }
        }
    }

    @Override
    public Object loadOptions(Field field, Element source) throws OptionProcessException {
        String xpath = field.getAnnotation(Options.class).key();
        Type fieldGenericType = field.getGenericType();
        if (fieldGenericType instanceof ParameterizedType) {
            Class<?> fieldClass = (Class<?>) ((ParameterizedType) fieldGenericType).getRawType();
            Type fieldTypeParameter = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
            if (Collection.class.isAssignableFrom(fieldClass)) {
                List<Element> possibleChilds = XPathFactory.instance()
                        .compile(xpath, Filters.element())
                        .evaluate(source);
                List<Object> values = Lists.newArrayListWithCapacity(possibleChilds.size() + 5);
                Class<?> collectionClass;
                if (fieldTypeParameter instanceof ParameterizedType) {
                    collectionClass = (Class<?>) ((ParameterizedType) fieldTypeParameter).getRawType();
                } else {
                    collectionClass = (Class<?>) fieldTypeParameter;
                }
                for (Element possibleChild : possibleChilds) {
                    if (primitives.contains(collectionClass)) {
                        values.add(getTypedValue(possibleChild.getText(), collectionClass));
                    } else {
                        try {
                            values.add(load(collectionClass.newInstance(), possibleChild));
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
    public <Y> Y load(Y object, Element source) {
        Class<?> daoClass = object.getClass();
        while (!daoClass.getSuperclass().equals(Object.class)) {
            for (Field field : daoClass.getDeclaredFields()) {
                if (field.getAnnotation(Option.class) != null) {
                    Object value;
                    try {
                        value = loadOption(field, source);
                        if (value != null) {
                            setFieldValue(object, field, value);
                        }
                    } catch (OptionProcessException e) {
                        log.warn("Error loading option", e);
                    }
                } else if (field.getAnnotation(Options.class) != null) {
                    Object value;
                    try {
                        value = loadOptions(field, source);
                        setFieldValue(object, field, value);
                    } catch (OptionProcessException e) {
                        log.warn("Error loading option", e);
                    }
                }
            }
            daoClass = daoClass.getSuperclass();
        }
        return object;
    }

    @Override
    public void save(Object object, Element source) {
        for (int i = 0, len = source.getAttributes().size(); i < len; i++) {
            source.removeAttribute(source.getAttributes().get(0));
        }
        super.save(object, source);
    }

    private void setValue(Object element, Object value, boolean cdata) {
        if (element instanceof Element) {
            if (cdata) {
                ((Element) element).setContent(new CDATA(Objects.toString(value, StringUtils.EMPTY)));
            } else {
                ((Element) element).setContent(new Text(Objects.toString(value, StringUtils.EMPTY)));
            }
        } else if (element instanceof Attribute) {
            ((Attribute) element).setValue(Objects.toString(value, StringUtils.EMPTY));
        }
    }

    private Object buildHierarchy(Element source, String xpath) {
        Iterable<String> split = Splitter.onPattern("(/|\\./)").omitEmptyStrings().split(xpath);
        Element element = source;
        Pattern pattern = Pattern.compile("\\[(\\d+)]");
        for (String name : split) {
            int index = 0;
            Matcher num = pattern.matcher(name);
            if (num.find()) {
                index = Integer.parseInt(num.group(1)) - 1;
            }
            name = name.replaceAll("(\\[\\d+\\])", StringUtils.EMPTY);
            if (name.startsWith("@")) {
                Attribute attribute = element.getAttribute(name = name.replace("@", StringUtils.EMPTY));
                if (attribute == null) {
                    attribute = new Attribute(name, StringUtils.EMPTY);
                    element.setAttribute(attribute);
                }
                return attribute;
            } else {
                List<Element> children = element.getChildren(name);
                if (children.size() <= index) {
                    Element child = new Element(name);
                    element.addContent(child);
                }
                element = element.getChildren(name).get(index);
            }
        }
        return element;
    }

    @Override
    public void saveOption(Object object, Field field, Element source) throws OptionProcessException {
        Object value = getFieldValue(object, field);
        if (value == null) {
            return;
        }
        String xpath = field.getAnnotation(Option.class).key();
        if (Strings.isNullOrEmpty(xpath)) {
            setValue(source, value, true);
            return;
        }
        Element node = XPathFactory.instance().compile(xpath, Filters.element()).evaluateFirst(source);
        boolean hasValues = !value.equals(StringUtils.EMPTY);
        if (node != null) {
            setValue(node, value, field.getAnnotation(Option.class).cdata()
                    ? field.getAnnotation(Option.class).cdata()
                    : hasValues);
        } else {
            Attribute attribute = XPathFactory.instance().compile(xpath, Filters.attribute()).evaluateFirst(source);
            if (attribute != null) {
                setValue(attribute, value, hasValues);
            } else {
                setValue(buildHierarchy(source, xpath), value, false);
            }
        }
    }

    @Override
    protected void iterate(String xpath, Element source, List<?> toIterate, Class<?> collectionClass) {
        List<Element> existingChildren = XPathFactory.instance()
                .compile(xpath, Filters.element())
                .evaluate(source);
        int i = 0;
        for (; i < toIterate.size(); i++) {
            if (existingChildren.size() < i + 1) {
                String newXpath = String.format("%s[%s]", xpath, i + 1);
                buildHierarchy(source, newXpath);
                existingChildren = XPathFactory.instance().compile(xpath, Filters.element()).evaluate(source);
            }
            if (primitives.contains(collectionClass)) {
                setValue(existingChildren.get(i), toIterate.get(i), false);
            } else {
                save(toIterate.get(i), existingChildren.get(i));
            }

            if (i < existingChildren.size()) {
                for (int j = i; j < existingChildren.size(); j++) {
                    existingChildren.get(j).getParentElement().removeContent(existingChildren.get(j));
                }
            }
        }
    }

    @Override
    protected Logger getLog() {
        return log;
    }
}
