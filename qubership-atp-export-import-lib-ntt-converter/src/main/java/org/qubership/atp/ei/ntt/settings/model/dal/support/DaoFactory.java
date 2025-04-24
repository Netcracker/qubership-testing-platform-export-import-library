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

package org.qubership.atp.ei.ntt.settings.model.dal.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.qubership.atp.ei.ntt.settings.ReflectionUtils;
import org.qubership.atp.ei.ntt.settings.model.FileSettings;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.dal.Associable;
import org.qubership.atp.ei.ntt.settings.model.dal.FiledStorer;
import org.qubership.atp.ei.ntt.settings.model.dal.Resource;
import org.qubership.atp.ei.ntt.settings.model.dal.jdom.JDomStorer;


/**
 * TODO Make summary for this class.
 */
public abstract class DaoFactory {

    private static DaoFactory instance;

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nonnull
    public static DaoFactory getInstance() {

        if (instance == null) {
            instance = new XmlDaoFactory();
        }
        return instance;
    }

    public abstract static class XmlResource implements Resource {

        @Override
        public DaoFactory getFactory() {

            return DaoFactory.getInstance();
        }

        protected List<ElementDal> children = new ArrayList<>();
        private FileSettings fileSettings;

        /**
         * TODO Make javadoc documentation for this method.
         */
        public XmlResource() {

            super();
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public XmlResource(String filePath) {

            this();
            File tmpFile = new File(filePath);
            filePath = tmpFile.getAbsolutePath();
            setFileSettings(filePath);
        }

        public abstract Associable getAssociable();

        public abstract FiledStorer getStorer();

        public FileSettings getFileSettings() {

            return fileSettings;
        }

        public void setFileSettings(String filePath) {

            this.fileSettings = new FileSettings(filePath);
        }

        public void setFileSettings(File file) {

            this.fileSettings = new FileSettings(file);
        }

        public abstract String getRootElementName();

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ElementDal> List<T> getChildren() {

            return (List<T>) children;
        }

        @Override
        public <T extends ElementDal> void setChild(T obj) {

            boolean find = false;
            for (ElementDal element : children) {
                if (element.getClass().equals(obj.getClass())) {
                    int index = children.indexOf(element);
                    children.set(index, obj);
                    find = true;
                    break;
                }
            }
            if (!find) {
                children.add(obj);
            }
            obj.setResource(this);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ElementDal> T getChild(Class<T> clazz) {

            for (ElementDal element : children) {
                if (element.getClass().equals(clazz)) {
                    return (T) element;
                }
            }
            T element = ReflectionUtils.getInstance(clazz);
            children.add(element);
            return element;
        }
    }

    public abstract static class DomXmlResource extends XmlResource {

        public DomXmlResource() {

            super();
        }

        public DomXmlResource(String filePath) {

            super(filePath);
        }

        private JDomStorer domStorer = new JDomStorer(this);

        @Override
        public Associable getAssociable() {

            return domStorer;
        }

        @Override
        public FiledStorer getStorer() {

            return domStorer;
        }
    }

    public interface ElementDal {

        Resource getResource();

        void setResource(Resource resource);
    }

    public abstract static class AbstractElementDal implements ElementDal {

        public AbstractElementDal() {

        }

        private Resource resource;

        public Resource getResource() {

            return resource;
        }

        public void setResource(Resource resource) {

            this.resource = resource;
        }
    }

    public interface NamedDal {

        String getName();

        void setName(String name);

        String getDescription();

        void setDescription(String description);
    }

    public interface Versioned {

        String getCermVersion();

        void setCermVersion(String cermVersion);
    }

    public abstract static class NamedElementDal extends AbstractElementDal implements NamedDal {

        @Option(key = "./@name")
        private String name;
        @Option(key = "./@description")
        private String description;

        @Override
        public String getName() {

            return name;
        }

        @Override
        public void setName(String name) {

            this.name = name;
        }

        @Override
        public String getDescription() {

            return description;
        }

        @Override
        public void setDescription(String description) {

            this.description = description;
        }
    }


    public abstract <T extends Resource> T load(Class<T> clazz);

    public abstract <T extends ElementDal> T load(Resource resource);

    public abstract <T extends XmlResource> T loadResource(T resource);

    public abstract <T extends ElementDal> T get(XmlResource parent);

    @Nullable
    public abstract <T extends ElementDal> T get(XmlResource parent, Class<T> clazz);

    public abstract <T extends ElementDal> T set(T element);

    public abstract <T extends Resource> T set(T resource);

    public abstract <T extends XmlResource> boolean save(T resource);

    public abstract <T extends ElementDal> boolean save(T element);

}
