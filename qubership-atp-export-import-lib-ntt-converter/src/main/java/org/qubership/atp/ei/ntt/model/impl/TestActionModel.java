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

import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;
import org.qubership.atp.ei.ntt.utils.CommonUtils;

/**
 * TestAction item class.
 * <p>
 * Implements behaviour and defines a model of TestAction items.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class TestActionModel extends AbstractModelItem implements TestAction, Cloneable<TestActionModel> {

    private String actionId;


    /**
     * Default constructor of test action.
     */
    public TestActionModel() {
    }


    /**
     * Constructs the test action item object with setting of name.
     *
     * @param name Name of the TestAction item.
     */
    public TestActionModel(String name) {
        setName(name);
    }


    /**
     * Constructor of the test action item object which creates an object based on another test action object.
     *
     * @param name  Name of the TestAction item.
     * @param model Test action model for getting data from it.
     */
    public TestActionModel(String name, TestActionModel model) {
        this.setName(name);
        this.actionId = CommonUtils.generateId();
        this.setParent(model.getParent());
        for (String flag : model.getFlags()) {
            this.setFlag(flag, true);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends TreeNode> List<T> getChildren() {
        return Collections.emptyList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends TreeNode> void setChildren(List<T> children) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionId() {
        return actionId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }


    /**
     * {@inheritDoc}
     */
    //FIXME: move to ModelItemController method=copy
    @SuppressWarnings("unchecked")
    @Override
    public TestActionModel clone() {
        try {
            TestActionModel newTestActionModel = (TestActionModel) super.clone();
            newTestActionModel.setActionId(CommonUtils.generateId());
            return newTestActionModel;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone is not supported for TestActionModel super class", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {
        return ModelItemType.ACTION;
    }
}
