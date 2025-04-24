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

package org.qubership.atp.ei.ntt.scope;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ScopeItemDao {

    @XmlAttribute(name = "uuid")
    private String uuid;
    @XmlAttribute(name = "project")
    private String project;
    @XmlAttribute(name = "modelItem")
    private String modelItem;
    @XmlAttribute(name = "dataset")
    private String dataSet;
    @XmlAttribute(name = "blockedBy")
    private String blockedBy;
    @XmlAttribute(name = "stage")
    private String stage;
    @XmlAttribute(name = "countLimit")
    private String countLimit;
    @XmlAttribute(name = "server")
    private String server;
    @XmlAttribute(name = "suite")
    private String testSuite;
    @XmlAttribute(name = "case")
    private String testCase;
    @XmlAttribute(name = "step")
    private String testStep;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }


    public String getModelItem() {
        return modelItem;
    }

    public void setModelItem(String modelItem) {
        this.modelItem = modelItem;
    }


    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }


    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getCountLimit() {
        return countLimit;
    }

    public void setCountLimit(String countLimit) {
        this.countLimit = countLimit;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }
}
