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

package org.qubership.atp.ei.ntt.settings.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.log4j.Logger;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;
import org.qubership.atp.ei.ntt.settings.store.file.apache.ApacheOptionProcessor;

/**
 * TODO Make summary for this class.
 */
@SuppressWarnings("serial")
public class ApacheXMLConfig extends ApacheXMLConfigurationProperties implements ApacheConfig {

    private static Logger log = Logger.getLogger(ApacheXMLConfig.class);

    private String filePath;
    private DaoFactory.XmlResource resource;
    private ApacheOptionProcessor processor = new ApacheOptionProcessor();

    /**
     * TODO Make javadoc documentation for this method.
     */
    public ApacheXMLConfig(DaoFactory.XmlResource resource) {

        this.resource = resource;
        super.setRootElementName(resource.getRootElementName());
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public void setFilePath(String filePath) {

        this.filePath = filePath;
        super.setFileName(filePath);
    }

    public String getFilePath() {

        return filePath;
    }

    @Override
    public boolean read() {

        try {
            super.clear();
            super.load(new File(filePath));
            this.fireEvent(EVENT_READ_PROPERTY, super.getRootNode().getName(), super.getRootNode(), false);
            return true;
        } catch (ConfigurationException e) {
            log.error(e);
            return false;
        }
    }

    @Override
    public boolean write() {

        try {
            super.save(new File(filePath).getAbsolutePath());
            return true;
        } catch (ConfigurationException e) {
            log.error(e);
            return false;
        }
    }

    @Override
    public boolean writeAs(String filePath) {

        try {
            super.save(filePath);
            return true;
        } catch (ConfigurationException e) {
            log.error(e);
            return false;
        }
    }

    public String getValue(SubnodeConfiguration node, String key) {

        return (String) node.getProperty(key);
    }

    @Override
    public void initAssociations(DaoFactory.ElementDal obj) {

        processor.load(obj, this);
    }

    @Override
    public void setAssociations(DaoFactory.ElementDal obj) {

        processor.save(obj, this);
    }

    public interface ConvertSupport<T> {

        T convert(String configValue);
    }

    protected static Map<Class<?>, ConvertSupport<?>> convertRule = new HashMap<Class<?>, ConvertSupport<?>>();

    private static void initConvertRule() {

        convertRule.put(String.class, new ConvertSupport<String>() {

            @Override
            public String convert(String configValue) {

                return configValue;
            }
        });
        convertRule.put(int.class, new ConvertSupport<Integer>() {

            @Override
            public Integer convert(String configValue) {

                return Integer.parseInt(configValue);
            }
        });
        convertRule.put(Integer.class, new ConvertSupport<Integer>() {

            @Override
            public Integer convert(String configValue) {

                return Integer.valueOf(configValue);
            }
        });
        convertRule.put(boolean.class, new ConvertSupport<Boolean>() {

            @Override
            public Boolean convert(String configValue) {

                return Boolean.parseBoolean(configValue);
            }
        });
        convertRule.put(Boolean.class, new ConvertSupport<Boolean>() {

            @Override
            public Boolean convert(String configValue) {

                return Boolean.valueOf(configValue);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertType(Class<T> type, String value) {

        if (convertRule.size() == 0) {
            initConvertRule();
        }
        return (T) convertRule.get(type).convert(value);
    }

    private static String convertType(Object value) {

        return value != null ? value.toString() : null;
    }

}
