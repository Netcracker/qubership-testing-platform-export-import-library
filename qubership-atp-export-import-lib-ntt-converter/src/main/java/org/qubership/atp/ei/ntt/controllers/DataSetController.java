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

package org.qubership.atp.ei.ntt.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.impl.ContextVariableImpl;
import org.qubership.atp.ei.ntt.model.impl.DataSetModel;
import org.qubership.atp.ei.ntt.utils.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSetController {

    private static DataSetController instance = null;

    protected DataSetController() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized DataSetController getInstance() {
        if (instance == null) {
            instance = new DataSetController();
        }
        return instance;
    }

    /**
     * Load.
     *
     * @param project     the project
     * @param dataSetPath the data set path
     */
    public void load(Project project, List<String> dataSetPath) {
        List<DataSet> dataSets = new ArrayList<>();
        switch (project.getProjectType().toLowerCase()) {
            case Constants.TXT_PROJECT:
                try {
                    //check and create dataset if not exist
                    File dsFile = new File(dataSetPath.iterator().next());
                    if (!dsFile.exists()) {
                        Writer wr = new FileWriter(dsFile);
                        wr.write("dsName=DataSet1\n\nname=var1; value=1");
                        wr.flush();
                        wr.close();
                    }

                    CSVFormat csvFormat = CSVFormat.newFormat(';').withCommentMarker('#').withQuote('\"');
                    CSVParser dsParser = new CSVParser(CommonUtils.getReaderForFile(dsFile), csvFormat);

                    boolean dsFound = false;
                    List<ContextVariable> contVar = new LinkedList<>();

                    DataSet dataSet = null;
                    for (CSVRecord ss : dsParser.getRecords()) {
                        if (ss.size() == 1 && ss.get(0).startsWith("dsName")) {

                            if (dataSet != null && contVar.size() > 0) {
                                dataSet.setVariables(contVar);
                                dataSets.add(dataSet);
                            }

                            dataSet = createEmptyDataSet(project);
                            dataSet.setName(ss.get(0).substring(ss.get(0).indexOf('=') + 1));

                            contVar = new LinkedList<>();
                            dsFound = true;

                        }
                        if (dsFound && ss.size() >= 2) {
                            ContextVariable var = new ContextVariableImpl();
                            for (String s : ss) {
                                if (!s.contains("=")) {
                                    continue;
                                }
                                String[] dsVals = s.trim().split("=");
                                String val1 = dsVals.length >= 1 ? dsVals[0] : "";
                                String val2 = dsVals.length > 1 ? dsVals[1] : "";
                                switch (val1) {
                                    case "name":
                                        var.setName(val2);
                                        break;
                                    case "description":
                                        var.setDescription(val2);
                                        break;
                                    case "value":
                                        var.setValue(val2);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            contVar.add(var);
                        }
                    }
                    if (dataSet != null) {
                        if (contVar.size() > 0) {
                            dataSet.setVariables(contVar);

                        }
                        dataSets.add(dataSet);
                    }
                    if (dataSets.size() > 0) {
                        project.setCurrentDataSet(dataSets.get(0));
                    }
                } catch (Exception e) {
                    log.error("Load TXT ATPDataSet", e);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Create empty data set.
     *
     * @param project  the project
     * @param helpArgs the help args
     * @return the data set
     */
    public DataSet createEmptyDataSet(Project project, Object... helpArgs) {
        DataSet dataSet = new DataSetModel(project, getDefaultName(project));
        ModelItemController.getInstance().calculateNewNodeName(dataSet, project);
        addDataSet(project, dataSet);
        return dataSet;
    }

    public String getDefaultName(ModelItem project, Object... helpArgs) {
        return "Data Set";
    }

    private void addDataSet(Project project, DataSet dataSet) {
        dataSet.setProject(project);
        project.getDataSets().add(dataSet);
    }
}
