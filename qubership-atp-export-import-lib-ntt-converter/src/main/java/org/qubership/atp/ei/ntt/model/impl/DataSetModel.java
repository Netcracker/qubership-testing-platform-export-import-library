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

import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.Project;

/**
 * The type Data set model.
 *
 * @author Denis Arychkov
 * @since 20.11.2013
 */
@SuppressWarnings("serial")
public class DataSetModel extends AbstractTreeNode implements DataSet {

    private List<ContextVariable> variables = new ArrayList<>();
    private String id;
    private Project project;


    public DataSetModel() {

    }


    /**
     * Instantiates a new Data set model.
     *
     * @param project the project
     * @param name    the name
     */
    public DataSetModel(Project project, String name) {

        this.project = project;
        setName(name);
    }


    @Override
    public List<ContextVariable> getVariables() {

        return variables;
    }


    @Override
    public List<String> getVariableNames() {
        List<String> names = new ArrayList<>();
        for (ContextVariable variable : variables) {
            names.add(variable.getName());
        }
        return names;
    }


    @Override
    public void setVariables(List<ContextVariable> variables) {

        this.variables = variables;
    }


    @Override
    public String getId() {

        return id;
    }


    @Override
    public void setId(String id) {

        this.id = id;
    }


    @Override
    public Project getProject() {

        return project;
    }


    @Override
    public void setProject(Project project) {

        this.project = project;
    }


    @Override
    public String toString() {

        return getName();
    }


    @Override
    public ContextVariable getVariableByName(String name) {

        for (ContextVariable var : variables) {
            if (var.getName().equals(name)) {
                return var;
            }
        }
        return null;
    }
}
