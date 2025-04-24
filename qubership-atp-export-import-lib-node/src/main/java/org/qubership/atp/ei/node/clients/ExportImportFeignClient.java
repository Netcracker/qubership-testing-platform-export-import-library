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

package org.qubership.atp.ei.node.clients;

import java.util.UUID;

import org.qubership.atp.auth.springbootstarter.config.FeignConfiguration;
import org.qubership.atp.ei.node.dto.ExportImportReportRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "${feign.atp.ei.name}", url = "${feign.atp.ei.url}",
        configuration = FeignConfiguration.class)
public interface ExportImportFeignClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "${feign.atp.ei.route}/ei/api/v1/flow/{exportImportType}"
                    + "/{projectId}/processes/{processId}/tasks/{taskId}/report")
    void report(@PathVariable("exportImportType") String exportImportType,
                @PathVariable("projectId") UUID projectId,
                @PathVariable("processId") String processId,
                @PathVariable("taskId") String taskId,
                @RequestBody ExportImportReportRequest exportImportReportRequest);
}