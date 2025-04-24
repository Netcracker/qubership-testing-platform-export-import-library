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

package org.qubership.atp.ei.node.controllers;

import java.util.UUID;

import org.qubership.atp.ei.node.dto.ExportNodeInfo;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.services.ExportNodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ei/api/v1/node/export")
@AllArgsConstructor
@Slf4j
public class ExportNodeController {

    private final ExportNodeService exportService;

    @GetMapping("/ping")
    public ExportNodeInfo ping() {
        return exportService.getExportNodeInfo(true);
    }

    /**
     * Run response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/run")
    public ResponseEntity run(@RequestBody RunNodeRequest request) {
        if (isRequestEmpty(request)) {
            log.info("Empty request is received");
            return ResponseEntity.badRequest().build();
        }
        exportService.runExport(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cancel")
    public ResponseEntity cancel(@RequestParam("projectId") UUID projectId,
                                 @RequestParam("taskId") String taskId,
                                 @RequestParam("processId") String processId) {
        exportService.cancelExport(projectId, taskId, processId);
        return ResponseEntity.ok().build();
    }

    private boolean isRequestEmpty(RunNodeRequest request) {
        return request.getProjectId() == null && request.getTaskId() == null;
    }

    @DeleteMapping("{projectId}/delete/{taskId}")
    public ResponseEntity delete(@PathVariable("projectId") UUID projectId, @PathVariable("taskId") String taskId) {
        exportService.deleteExportFile(projectId, taskId);
        return ResponseEntity.ok().build();
    }
}
