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
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * TestSuite item implementation.
 * <p>
 * Describes behaviour and defines a model of test suite items.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class TestSuiteModel extends AbstractModelItem implements TestSuite, Cloneable<TestSuiteModel> {

    /**
     * Default constructor of test suite items.
     */
    public TestSuiteModel() {
    }


    /**
     * Constructor of test suite object.
     *
     * @param name Name of the test suite item.
     */
    public TestSuiteModel(String name) {
        setName(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Project getProject() {
        return (Project) super.getProject();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<NttTestCase> getTestCases() {
        return super.getChildren();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestCases(List<NttTestCase> cases) {
        super.setChildren(cases);
    }


    @Override
    public TestSuiteModel clone() {
        try {
            TestSuiteModel newTestSuiteModel = (TestSuiteModel) super.clone();
            List<ModelItem> newChildren = new ArrayList<>(getChildren().size());
            for (NttTestCase testCase : this.getTestCases()) {
                TestCaseModel newTestCaseModel = ((TestCaseModel) testCase).clone();
                newChildren.add(newTestCaseModel);
                newTestCaseModel.setParent(newTestSuiteModel);
            }
            newTestSuiteModel.setChildren(newChildren);
            return newTestSuiteModel;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone is not supported for TestCaseModel super class", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {
        return ModelItemType.SUITE;
    }

}
