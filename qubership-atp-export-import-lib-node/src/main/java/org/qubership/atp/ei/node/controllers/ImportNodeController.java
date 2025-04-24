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

import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.services.ImportNodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ei/api/v1/node/import")
@AllArgsConstructor
@Slf4j
public class ImportNodeController {

    private final ImportNodeService importService;

    /**
     * Run response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping(value = "/run", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity run(@RequestBody RunNodeRequest request) {
        importService.runImport(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Validate import file.
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity validate(@RequestBody RunNodeRequest request) {
        importService.validateImport(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/prevalidate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity prevalidate(@RequestBody RunNodeRequest request) {
        importService.preValidateImport(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cancel")
    public ResponseEntity cancel(@RequestParam("projectId") UUID projectId,
                                 @RequestParam("taskId") String taskId,
                                 @RequestParam("processId") String processId) {
        importService.cancel(projectId, taskId, processId);
        return ResponseEntity.ok().build();
    }

}
