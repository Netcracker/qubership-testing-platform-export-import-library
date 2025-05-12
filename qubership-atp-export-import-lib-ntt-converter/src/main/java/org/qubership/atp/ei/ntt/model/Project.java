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

package org.qubership.atp.ei.ntt.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Project interface.
 * <p>
 * Describes a project behaviour.
 * </p>
 *
 * @version 1.4.3
 */
public interface Project extends ModelItem, ContainerTreeNode {

    /**
     * Returns list of test suites which it contains.
     *
     * @return List of test suites.
     */
    List<TestSuite> getTestSuites();

    /**
     * Sets list of test suites to the project.
     *
     * @param testSuites List of test suites for containing in the project.
     */
    void setTestSuites(List<TestSuite> testSuites);

    /**
     * Returns all data sets of the project.
     *
     * @return List of data sets.
     */
    List<DataSet> getDataSets();

    /**
     * Sets specified data sets to the project.
     *
     * @param dataSets List of data sets.
     */
    void setDataSets(List<DataSet> dataSets);

    /**
     * Returns a current data set of the project.
     *
     * @return The current data set.
     */
    DataSet getCurrentDataSet();

    /**
     * Sets a current data set to the project.
     *
     * @param currentDataSet DataSet object for setting.
     */
    void setCurrentDataSet(DataSet currentDataSet);

    /**
     * Sets project type.
     * <p>
     * Project types are XML, TXT, SQL, story.
     * </p>
     *
     * @param projectType Type of the project.
     */
    void setProjectType(String projectType);

    /**
     * Returns a type of the project.
     *
     * @return Type of the project.
     */
    String getProjectType();

    Map<Path, Path> getFiles();
}
