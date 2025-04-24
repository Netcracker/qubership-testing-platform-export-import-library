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

package org.qubership.atp.ei.node;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.dto.ExportImportData;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.dto.ValidationResult;
import org.qubership.atp.ei.node.dto.validation.ValidationType;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.qubership.atp.ei.node.services.FileService;
import org.qubership.atp.ei.node.services.MetricsExportImportService;
import org.qubership.atp.ei.node.services.impl.NotifyService;
import org.qubership.atp.integration.configuration.mdc.MdcUtils;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportExecutorWrapper implements CancellableExportImportTask, ImportExecutor {

    private final ValidationType validationType;
    private final RunNodeRequest request;
    private final Path defaultWorkDir;
    private final ImportExecutor executor;
    private final NotifyService notifyService;
    private final SecurityContext securityContext;
    private boolean isCancelled;
    private final ExportImportFilesService exportImportFilesService;
    private final FileService fileService;
    private Map<String, String> mdcContext;
    private final MetricsExportImportService metricsExportImportService;

    /**
     * Instantiates a new Import executor wrapper.
     *
     * @param request        the request
     * @param defaultWorkDir the default work dir
     * @param validationType the is validation
     * @param importExecutor the import executor
     * @param notifyService  the notify service
     */
    public ImportExecutorWrapper(RunNodeRequest request, Path defaultWorkDir, ValidationType validationType,
                                 ImportExecutor importExecutor, NotifyService notifyService,
                                 ExportImportFilesService exportImportFilesService,
                                 FileService fileService,
                                 MetricsExportImportService metricsExportImportService) {
        this.validationType = validationType;
        this.request = request;
        this.defaultWorkDir = defaultWorkDir;
        this.executor = importExecutor;
        this.notifyService = notifyService;
        this.securityContext = SecurityContextHolder.getContext();
        this.exportImportFilesService = exportImportFilesService;
        this.fileService = fileService;
        this.mdcContext = MDC.getCopyOfContextMap();
        this.metricsExportImportService = metricsExportImportService;
    }

    @Override
    public void importData(ExportImportData importData, Path workDir) throws Exception {
        log.info("Start import data for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            executor.importData(importData, workDir);
            fileService.deletePath(workDir);
        } catch (Exception e) {
            log.error("Error occurred while importing data", e);
            throw e;
        }
        log.info("Finish import data for project {} and task {}", request.getProjectId(), request.getTaskId());
    }

    @Override
    public ValidationResult preValidateData(ExportImportData importData, Path workDir) throws Exception {
        log.info("Start validate data for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            ValidationResult result = executor.preValidateData(importData, workDir);
            log.info("Finish Validate data for project {} and task {}", request.getProjectId(), request.getTaskId());
            return result;
        } catch (Exception e) {
            log.error("Error occurred while validate data", e);
            throw e;
        }
    }

    @Override
    public ValidationResult validateData(ExportImportData importData, Path workDir) throws Exception {
        log.info("Start validate data for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            ValidationResult result = executor.validateData(importData, workDir);
            log.info("Finish Validate data for project {} and task {}", request.getProjectId(), request.getTaskId());
            return result;
        } catch (Exception e) {
            log.error("Error occurred while validate data", e);
            throw e;
        }
    }

    @Override
    public Object call() throws Exception {
        log.debug("Export wrapper, mdcContext = {}", mdcContext);
        final Stopwatch timer = Stopwatch.createStarted();
        metricsExportImportService.registerProcess(request.getProjectId().toString(), request.getProcessId(),
                metricsExportImportService.ACTIVE_IMPORT_PROCESS_COUNT);
        MdcUtils.setContextMap(mdcContext);
        SecurityContextHolder.setContext(securityContext);
        try {
            Path pathToImportArchive =
                    exportImportFilesService.downloadFileInDir(request.getFileDescriptor(), defaultWorkDir);
            fileService.unpackZipFile(pathToImportArchive, defaultWorkDir);
            fileService.deletePath(pathToImportArchive);

            if (validationType != null) {
                ExportImportData exportImportData = request.toExportImportData();
                ValidationResult validationResult;
                if (validationType == ValidationType.PRE_VALIDATE) {
                    validationResult = preValidateData(exportImportData, defaultWorkDir);
                } else {
                    validationResult = validateData(exportImportData, defaultWorkDir);
                }
                if (validationResult == null) {
                    validationResult = new ValidationResult();
                }
                if (validationResult.getReplacementMap() == null) {
                    validationResult.setReplacementMap(exportImportData.getReplacementMap());
                } else {
                    validationResult.getReplacementMap().putAll(exportImportData.getReplacementMap());
                }

                log.info("Validation result {}", validationResult);
                notifyService.notifyAfterValidation(request, validationResult);
            } else {
                importData(request.toExportImportData(false), defaultWorkDir);
                notifyService.notifyImportFlow(request);
            }
        } catch (Exception e) {
            log.info("Exception during import", e);
            if (!isCancelled()) {
                notifyService.notifyImportFlow(e, request);
            }
        } finally {
            try {
                metricsExportImportService.addTimeMetric(request.getProjectId(), timer,
                        metricsExportImportService.TOTAL_TIME_OF_IMPORT_PROCESSES);
                metricsExportImportService.unregisterProcess(UUID.fromString(request.getProcessId()));
            } catch (Throwable e) {
                log.error("Unable unregister metric for processId {}", request.getProcessId(), e);
                throw new ExportException("Unable unregister metric for processId " + request.getProcessId());
            }
        }
        return null;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled() {
        this.isCancelled = true;
    }
}
