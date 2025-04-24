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

import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.TestSuite;

/**
 * Project model implementation.
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class ProjectModel extends AbstractProjectModel implements Project {

    public ProjectModel() {

    }


    public ProjectModel(String name) {

        this.setName(name);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestSuite> getTestSuites() {

        return super.getChildren();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestSuites(List<TestSuite> testSuites) {

        super.setChildren(testSuites);
    }
}
