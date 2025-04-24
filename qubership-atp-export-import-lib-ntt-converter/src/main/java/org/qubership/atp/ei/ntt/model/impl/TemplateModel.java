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

package org.qubership.atp.ei.ntt.model.impl;

import java.util.List;

import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * Template implementation class.
 * <p>
 * Describes behaviour and defines a model of templates.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class TemplateModel extends AbstractModelItem implements Template {

    private ModelItem item;
    private String templateId;


    /**
     * Default constructor of the template object.
     */
    public TemplateModel() {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItem getModelItem() {

        return item;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setModelItem(ModelItem item) {

        this.item = item;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends TreeNode> List<T> getChildren() {

        return item.getChildren();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateId() {

        return templateId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateId(String id) {

        this.templateId = id;
    }


    @Override
    public String toString() {

        return String.format("%s : %s", getPrefix(), item.getName());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {

        if (item == null) {
            return null;
        }
        return item.getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {

        if (item == null) {
            return;
        }
        String prefix = String.format("%s : ", getPrefix());
        if (name.startsWith(prefix)) {
            name = name.split("^" + prefix)[1];
        }
        item.setName(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {

        return item.getDescription();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {

        item.setDescription(description);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTemplate() {

        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {

        return getModelItem().getModelItemType();
    }


    private static final int TESTLENGTH = "Test".length();


    private String getPrefix() {

        String prefix = "UNKNOWN";
        if (getModelItemType() != null) {
            prefix = getModelItemType().getName().substring(TESTLENGTH).toUpperCase();
        }
        return prefix;
    }
}
