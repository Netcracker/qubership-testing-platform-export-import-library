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

import javax.xml.bind.Unmarshaller;

import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;


/**
 * TestCase item class.
 * <p>
 * Implements behaviour and defines a model of TestCase items.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class TestCaseModel extends AbstractModelItem implements NttTestCase, Cloneable<TestCaseModel> {

    /**
     * Default constructor of the test case items.
     */
    public TestCaseModel() {
    }


    /**
     * Constructor of the test case item with specifying of its name.
     *
     * @param name Name of creating TestCase object.
     */
    public TestCaseModel(String name) {
        setName(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestStep> getTestSteps() {
        return getChildren();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestSteps(List<TestStep> steps) {
        super.setChildren(steps);
    }


    /**
     * Deprecated method. Saved for backward compatibility.
     *
     * @param u      Unused.
     * @param parent Unused.
     */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent instanceof TestSuite) {
            super.setParent((ModelItem) parent);
        }
    }


    @Override
    public String toString() {
        return getName();
    }


    @Override
    public TestCaseModel clone() {
        try {
            TestCaseModel newTestCaseModel = (TestCaseModel) super.clone();
            List<ModelItem> newChildren = new ArrayList<>(getChildren().size());
            for (TestStep testStep : this.getTestSteps()) {
                TestStepModel newTestStepModel = ((TestStepModel) testStep).clone();
                newTestStepModel.setParent(newTestCaseModel);
                newChildren.add(newTestStepModel);
            }
            newTestCaseModel.setChildren(newChildren);
            return newTestCaseModel;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone is not supported for TestCaseModel super class", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {
        return ModelItemType.CASE;
    }
}
