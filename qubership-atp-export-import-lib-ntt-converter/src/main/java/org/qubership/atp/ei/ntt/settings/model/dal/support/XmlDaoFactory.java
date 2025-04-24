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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.qubership.atp.ei.ntt.settings.ReflectionUtils;
import org.qubership.atp.ei.ntt.settings.model.dal.Resource;


/**
 * TODO Make summary for this class.
 */
public class XmlDaoFactory extends DaoFactory {

    protected static final Logger log = Logger.getLogger(XmlDaoFactory.class);

    @Override
    public <T extends Resource> T load(Class<T> clazz) {

        T element = (T) ReflectionUtils.getInstance(clazz);
        load(element);
        return element;
    }

    /**
     * Please use for loading simple structure: library.xml as example
     */
    @Override
    public <T extends ElementDal> T load(Resource resource) {

        resource = loadResource((XmlResource) resource);
        T element = get((XmlResource) resource);
        return element;
    }

    @Override
    public <T extends XmlResource> T loadResource(T resource) {

        log.debug("Try to load file by path:[" + resource.getFileSettings().getPathToSave() + "]");
        resource.getStorer().setFilePath(resource.getFileSettings().getPathToSave());
        resource.getStorer().read();
        log.debug("Successful load file by path:[" + resource.getFileSettings().getPathToSave() + "]");
        parse(resource);
        return resource;
    }

    private <T extends XmlResource> T parse(T resource) {

        for (ElementDal element : resource.getChildren()) {
            resource.getAssociable().initAssociations(element);
            element.setResource(resource);
        }
        return resource;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ElementDal> T get(XmlResource resource) {

        return (T) get(resource, resource.getChildren().get(0).getClass());
    }

    @Override
    @Nullable
    public <T extends ElementDal> T get(XmlResource resource, Class<T> clazz) {

        T result = null;
        if (clazz != null) {
            result = resource.getChild(clazz);
        }
        return result;
    }

    @Override
    public <T extends ElementDal> T set(T element) {

        element.getResource().setChild(element);
        set(element.getResource());
        return element;
    }

    @Override
    public <T extends Resource> T set(T resource) {

        for (ElementDal element : resource.getChildren()) {
            ((XmlResource) resource).getAssociable().setAssociations(element);
        }
        return resource;
    }

    @Override
    public <T extends XmlResource> boolean save(T resource) {

        final String tab = "\t";
        set(resource);

        log.debug("Try to save file by path:[" + resource.getFileSettings().getPathToSave() + "]");

        resource.getStorer().setFilePath(resource.getFileSettings().getPathToSave());
        boolean result = resource.getStorer().write();

        if (result) {

            try {
                BufferedReader reader = new BufferedReader(new FileReader(resource.getFileSettings().getPathToSave()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {

                    String testString = line.replace(tab, StringUtils.EMPTY)
                            .replace(StringUtils.SPACE, StringUtils.EMPTY);
                    if (!testString.isEmpty()) {
                        sb.append(line.concat(StringUtils.LF));
                    }
                }
                reader.close();

                PrintWriter out = new PrintWriter(resource.getFileSettings().getPathToSave());
                out.print(sb.toString());
                out.close();
            } catch (Exception ex) {

                log.error("Exception occurred while saving file: " + ex.getMessage(), ex);
                return true;
            }

            log.debug("Successful save file by path:[" + resource.getFileSettings().getPathToSave() + "]");
        }

        return result;
    }

    @Override
    public <T extends ElementDal> boolean save(T element) {

        return save((XmlResource) element.getResource());
    }
}