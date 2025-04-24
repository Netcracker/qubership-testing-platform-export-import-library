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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;
import org.qubership.atp.ei.ntt.utils.CommonUtils;
import org.qubership.atp.ei.ntt.utils.NttModelLoader;

/**
 * ReferenceModelItem implementation.
 * <p>
 * Defines a model of reference model items.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class ReferenceModelItem extends AbstractModelItem implements TestSuite,
        NttTestCase, TestStep, Reference, Cloneable<ReferenceModelItem> {

    private Template template;
    private String templProjName;
    private String templName;
    private String referenceId;
    private ModelItemType modelItemType;


    public ReferenceModelItem() {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Template getTemplate() {

        updateTemplate();
        return template;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplate(Template localTemplate) {

        this.template = localTemplate;
        if (template != null) {
            modelItemType = template.getModelItemType();
            templName = template.getName();
            templProjName = template.getProject().getName();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplate(ModelItemType miEnum, String templProjName, String templName) {

        modelItemType = miEnum;
        this.templProjName = templProjName;
        this.templName = templName;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getReferenceId() {

        return referenceId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setReferenceId(String id) {

        this.referenceId = id;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getRefName() {

        return super.getName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {

        updateTemplate();
        if (template == null) {
            return "Illegal state: Lost reference";
        } else if (!super.getName().equals(StringUtils.EMPTY)) {
            return super.getName();
        } else {
            return String.format("Reference to [%s - %s]", templProjName, template.toString());
        }
    }


    @Override
    public String toString() {

        return getName();
    }


    /**
     * Clone reference model item.
     *
     * @return the reference model item
     */
    public ReferenceModelItem clone() {
        ReferenceModelItem referenceModelItem = new ReferenceModelItem();
        referenceModelItem.setTemplate(modelItemType, templProjName, templName);
        referenceModelItem.setName(super.getName());
        referenceModelItem.setReferenceId(CommonUtils.generateId());
        referenceModelItem.setParent(getParent());
        referenceModelItem.setDescription(getDescription());
        return referenceModelItem;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends TreeNode> List<T> getChildren() {

        if (getTemplate() == null) {
            return Collections.emptyList();
        } else {
            return getTemplate().getChildren();
        }
    }


    /**
     * Deprecated method. Unused.
     *
     * @param children List of children. Unused.
     * @param <T>      Unused.
     */
    @Override
    public <T extends TreeNode> void setChildren(List<T> children) {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<NttTestCase> getTestCases() {

        return ((TestSuite) getTemplate().getModelItem()).getTestCases();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestCases(List<NttTestCase> cases) {

        ((TestSuite) getTemplate().getModelItem()).setTestCases(cases);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestStep> getTestSteps() {

        return ((NttTestCase) getTemplate().getModelItem()).getTestSteps();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestSteps(List<TestStep> steps) {

        ((NttTestCase) getTemplate().getModelItem()).setTestSteps(steps);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestAction> getActions() {
        return getTemplate().getModelItem().getActions();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActions(List<TestAction> actions) {
        ((TestStep) getTemplate().getModelItem()).setActions(actions);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {

        if (modelItemType != null) {
            return modelItemType;
        } else if (getParent() != null) {
            return ((ModelItem) getParent()).getModelItemType().next();
        } else {
            return null;
        }
    }


    private void updateTemplate() {

        for (Project templProject : NttModelLoader.getInstance().getProjects(true)) {
            if (templProject.getName().equals(templProjName)) {
                for (TreeNode template : templProject.getChildren()) {
                    if (template.getName().equals(templName)
                            && ((Template) template).getModelItemType() == getModelItemType()) {
                        this.template = (Template) template;
                        return;
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplProjName() {

        return templProjName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplName() {

        if (template != null) {
            templName = template.getName();
        }

        return templName;
    }

}
