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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.dto.validation.ValidationType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportImportData {
    private final UUID projectId;
    private final ExportScope exportScope;
    private final ExportFormat format;
    private final boolean createNewProject;
    private final boolean interProjectImport;
    private final UUID importedProjectId;
    private final Map<UUID, UUID> replacementMap;
    private final Map<UUID, String> newObjectNamesMap;
    private final ValidationType validationType;
    private boolean importFirstTime;

    /**
     * Instantiates a new Export import data.
     *
     * @param projectId   the project id
     * @param exportScope the export scope
     * @param format      the format
     */
    public ExportImportData(UUID projectId, ExportScope exportScope, ExportFormat format) {
        this.projectId = projectId;
        this.exportScope = exportScope;
        this.format = format;
        this.interProjectImport = false;
        this.createNewProject = false;
        this.importedProjectId = null;
        this.validationType = null;
        this.replacementMap = new HashMap<>();
        this.newObjectNamesMap = new HashMap<>();
        this.importFirstTime = false;
    }
}
