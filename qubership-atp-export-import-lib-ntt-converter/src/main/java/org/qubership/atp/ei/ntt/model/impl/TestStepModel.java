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

import java.util.ArrayList;
import java.util.List;

import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;


/**
 * TestStep item implementation.
 * <p>
 * Implements behaviour and defines a model of TestStep items.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class TestStepModel extends AbstractModelItem implements TestStep, Cloneable<TestStepModel> {

    /**
     * Default constructor an object of TestStep.
     */
    public TestStepModel() {
    }


    /**
     * Constructs an object of TestStep with setting of its name.
     *
     * @param name Name of the test step item.
     */
    public TestStepModel(String name) {
        setName(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestAction> getActions() {
        return getChildren();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setActions(List<TestAction> actions) {
        this.setChildren(actions);
    }


    @Override
    public TestStepModel clone() {
        try {
            TestStepModel newTestStepModel = (TestStepModel) super.clone();
            List<TestAction> newActions = new ArrayList<>(getActions().size());
            for (TestAction testAction : getActions()) {
                TestActionModel newTestActionModel = ((TestActionModel) testAction).clone();
                newTestActionModel.setParent(newTestStepModel);
                newActions.add(newTestActionModel);
            }
            newTestStepModel.setActions(newActions);
            return newTestStepModel;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone is not supported for TestStepModel super class", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {
        return ModelItemType.STEP;
    }

}
