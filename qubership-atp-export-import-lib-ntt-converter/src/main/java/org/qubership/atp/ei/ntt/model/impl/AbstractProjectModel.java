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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * Describes a model of project.
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public abstract class AbstractProjectModel extends AbstractModelItem implements Project {

    private List<DataSet> dataSets = new LinkedList<>();
    private Map<Path, Path> files = new HashMap<>();

    private DataSet currentDataSet = null;
    private String projectType = Constants.TXT_PROJECT;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataSet> getDataSets() {
        return dataSets;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataSets(List<DataSet> dataSets) {
        this.dataSets = dataSets;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet getCurrentDataSet() {
        return currentDataSet;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentDataSet(DataSet currentDataSet) {
        this.currentDataSet = currentDataSet;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getProjectType() {
        return projectType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ModelItemType getModelItemType() {
        return ModelItemType.PROJECT;
    }

    /**
     * This method is used for export proposes from ATP to NTT project.
     */
    @Override
    public Map<Path, Path> getFiles() {
        return files;
    }

}
