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

package org.qubership.atp.ei.node.services.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.qubership.atp.ei.node.clients.ExportImportFeignClient;
import org.qubership.atp.ei.node.constants.Constant;
import org.qubership.atp.ei.node.dto.ExportImportReportRequest;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.dto.ValidationResult;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotifyService {

    private final ExportImportFeignClient exportImportFeignClient;

    @Autowired
    public NotifyService(ExportImportFeignClient exportImportFeignClient) {
        this.exportImportFeignClient = exportImportFeignClient;
    }

    // COMMON

    private void notifyFlow(Exception e, RunNodeRequest runNodeRequest, String exportImportType) {
        notifyFlow(e, runNodeRequest, exportImportType, null);
    }

    private void notifyFlow(Exception e, RunNodeRequest runNodeRequest, String exportImportType,
                            String fileIdInGridFs) {
        ExportImportReportRequest request = new ExportImportReportRequest();
        if (fileIdInGridFs != null) {
            request.setFileId(fileIdInGridFs);
        }
        if (e != null) {
            request.setStatus("ERROR");
            request.setError(ExceptionUtils.getMessage(e));
        } else {
            request.setStatus("COMPLETED");
        }
        notifyFlow(runNodeRequest, exportImportType, request);
    }

    private void notifyFlow(RunNodeRequest runNodeRequest, String exportImportType,
                            ExportImportReportRequest request) {
        log.info("Notify export service, project {}, process {}, task {}",
                runNodeRequest.getProjectId(), runNodeRequest.getProcessId(), runNodeRequest.getTaskId());
        try {
            exportImportFeignClient.report(exportImportType, runNodeRequest.getProjectId(),
                    runNodeRequest.getProcessId(), runNodeRequest.getTaskId(), request);
        } catch (RestClientException | FeignException e) {
            log.error("Cannot notify export service about completeness runRequest {}, report {}", runNodeRequest,
                    request, e);
            ExportException.throwException("Cannot notify export service about completeness runRequest {}, report {}",
                    runNodeRequest, request, e);
        }
    }

    // EXPORT

    public void notifyExportFlow(RunNodeRequest request, String gridFsFileId) {
        notifyFlow(null, request, Constant.EXPORT, gridFsFileId);
    }

    public void notifyExportFlow(Exception e, RunNodeRequest request) {
        notifyFlow(e, request, Constant.EXPORT);
    }

    // IMPORT

    /**
     * Notify after validation.
     *
     * @param request          the request
     * @param validationResult the validation result
     */
    public void notifyAfterValidation(RunNodeRequest request, ValidationResult validationResult) {
        String validationStatus = validationResult.isValid() ? Constant.VALIDATION_PASSED : Constant.VALIDATION_FAILED;
        ExportImportReportRequest reportRequest = new ExportImportReportRequest();
        reportRequest.setStatus(validationStatus);
        reportRequest.setMessages(validationResult.getMessages());
        reportRequest.setDetails(validationResult.getDetails());
        reportRequest.setReplacementMap(validationResult.getReplacementMap());

        notifyFlow(request, Constant.IMPORT, reportRequest);
    }

    public void notifyImportFlow(RunNodeRequest request) {
        notifyFlow(null, request, Constant.IMPORT);
    }

    public void notifyImportFlow(Exception e, RunNodeRequest request) {
        notifyFlow(e, request, Constant.IMPORT);
    }
}
