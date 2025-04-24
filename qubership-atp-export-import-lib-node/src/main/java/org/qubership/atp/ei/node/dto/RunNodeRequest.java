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

package org.qubership.atp.ei.node.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.dto.validation.ValidationType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunNodeRequest {
    private UUID projectId;
    private String processId;
    private String taskId;
    private ExportFormat exportFormat;
    private ExportScope exportScope;
    private boolean createNewProject;
    private boolean interProjectImport;
    private boolean importFirstTime;
    private UUID importedProjectId;
    private Map<UUID, UUID> replacementMap;
    private Map<UUID, String> newObjectNamesMap;
    private ExportFileDescriptor fileDescriptor;
    private ValidationType validationType;

    /**
     * To export import data export import data.
     *
     * @return the export import data
     */
    public ExportImportData toExportImportData(boolean unmodifiableMap) {
        if (replacementMap == null) {
            replacementMap = new HashMap<>();
        }
        if (interProjectImport && createNewProject && !replacementMap.containsKey(importedProjectId)) {
            replacementMap.put(importedProjectId, UUID.randomUUID());
        } else if (interProjectImport && !replacementMap.containsKey(importedProjectId)) {
            replacementMap.put(importedProjectId, projectId);
        } else if (!replacementMap.containsKey(importedProjectId)) {
            replacementMap.put(projectId, projectId);
        }
        if (newObjectNamesMap == null) {
            newObjectNamesMap = new HashMap<>();
        }
        return new ExportImportData(projectId, exportScope, exportFormat,
                createNewProject, interProjectImport, importedProjectId,
                unmodifiableMap ? Collections.unmodifiableMap(replacementMap) : replacementMap,
                unmodifiableMap ? Collections.unmodifiableMap(newObjectNamesMap) : newObjectNamesMap,
                validationType, importFirstTime);
    }

    public ExportImportData toExportImportData() {
        return toExportImportData(true);
    }
}
