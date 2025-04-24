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

package org.qubership.atp.ei.ntt.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.settings.model.dal.SettingsResource;

import lombok.Data;

@Data
public class NttExportScope {
    List<Scope> nttScopes = new ArrayList<>();
    SettingsResource nttEnvironments;
    Map<UUID, Project> dataSetList2NttProjectMap = new HashMap<>();
    Map<UUID, DataSet> dataSet2NttDataSetMap = new HashMap<>();
    HashMap<UUID, Map<UUID, TestSuite>> project2NttSuitesMap = new HashMap<>();
    Map<NttTestCase, UUID> nttTestCase2atpTestCase = new HashMap<>();
    Map<TestSuite, UUID> nttTestSuite2atpTestScope = new HashMap<>();
}
