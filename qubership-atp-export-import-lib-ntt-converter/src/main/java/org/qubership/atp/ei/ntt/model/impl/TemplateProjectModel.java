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

import org.qubership.atp.ei.ntt.model.TemplateProject;
import org.qubership.atp.ei.ntt.model.TestSuite;

/**
 * The type Template project model.
 *
 * @author Roman Aksenenko
 * @since 20.11.2013
 */
@SuppressWarnings("serial")
public class TemplateProjectModel extends AbstractProjectModel implements TemplateProject {


    public TemplateProjectModel() {
    }


    public TemplateProjectModel(String name) {
        this.setName(name);
    }


    @Override
    public List<TestSuite> getTestSuites() {
        return null;
    }


    @Override
    public void setTestSuites(List<TestSuite> testSuites) {
    }

}
