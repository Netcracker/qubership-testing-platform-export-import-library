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

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.ImportExecutor;
import org.qubership.atp.ei.node.ImportExecutorWrapper;
import org.qubership.atp.ei.node.constants.Constant;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.dto.validation.ValidationType;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.qubership.atp.ei.node.services.FileService;
import org.qubership.atp.ei.node.services.ImportNodeService;
import org.qubership.atp.ei.node.services.MetricsExportImportService;
import org.qubership.atp.integration.configuration.mdc.MdcUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImportNodeServiceImpl implements ImportNodeService {

    private final ImportExecutor importExecutor;
    private final TasksService tasksService;
    private final NotifyService notifyService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final ExportImportFilesService exportImportFilesService;
    private final FileService fileService;
    private Map<String, String> mdcContext;
    private final MetricsExportImportService metricsExportImportService;

    /**
     * Instantiates a new Import node service.
     *
     * @param importExecutor the import executor
     * @param tasksService   the tasks service
     * @param notifyService  the notify service
     */
    @Autowired
    public ImportNodeServiceImpl(@Qualifier("atpImportThreadExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                 ImportExecutor importExecutor,
                                 TasksService tasksService,
                                 NotifyService notifyService,
                                 ExportImportFilesService exportImportFilesService,
                                 FileService fileService,
                                 MetricsExportImportService metricsExportImportService) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.importExecutor = importExecutor;
        this.tasksService = tasksService;
        this.notifyService = notifyService;
        this.exportImportFilesService = exportImportFilesService;
        this.fileService = fileService;
        this.mdcContext = MDC.getCopyOfContextMap();
        this.metricsExportImportService = metricsExportImportService;
    }

    @Override
    public void validateImport(RunNodeRequest request) {
        log.info("Validate import request for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            Path importDirectory = preparePathForValidation(request);

            ImportExecutorWrapper executor = new ImportExecutorWrapper(
                    request, importDirectory, ValidationType.VALIDATE, importExecutor, notifyService,
                    exportImportFilesService, fileService, metricsExportImportService);

            tasksService.submitTask(request.getTaskId(), executor, threadPoolTaskExecutor);
        } catch (Exception e) {
            String msg = "Error while start validation";
            log.error(msg, e);
            ExportException.throwException(msg, e);
        }
    }

    @Override
    public void preValidateImport(RunNodeRequest request) {
        log.info("Validate import request for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            Path importDirectory = preparePathForValidation(request);

            ImportExecutorWrapper executor = new ImportExecutorWrapper(
                    request, importDirectory, ValidationType.PRE_VALIDATE, importExecutor, notifyService,
                    exportImportFilesService, fileService, metricsExportImportService);

            tasksService.submitTask(request.getTaskId(), executor, threadPoolTaskExecutor);
        } catch (Exception e) {
            String msg = "Error while start validation";
            log.error(msg, e);
            ExportException.throwException(msg, e);

        }
    }

    private Path preparePathForValidation(RunNodeRequest request) throws ExportException {
        Path importDirectory =
                fileService.getFolderPath(request.getProjectId(), request.getProcessId(), Constant.IMPORT);
        fileService.deletePath(importDirectory);
        fileService.createDirectory(importDirectory);
        return importDirectory;
    }

    @Override
    public void cancel(UUID projectId, String taskId, String processId) {
        log.debug("Export wrapper, mdcContext = {}", mdcContext);
        MdcUtils.setContextMap(mdcContext);
        log.info("Cancel Import request for project {} and task {}", projectId, taskId);
        tasksService.cancelTask(taskId);
        fileService.deleteFolder(projectId, processId, Constant.IMPORT);
    }

    @Override
    public void runImport(RunNodeRequest request) {
        log.info("Run import request for project {} and task {}", request.getProjectId(), request.getTaskId());
        try {
            Path importDirectory = preparePathForValidation(request);

            ImportExecutorWrapper executor = new ImportExecutorWrapper(
                    request, importDirectory, null, importExecutor, notifyService,
                    exportImportFilesService, fileService, metricsExportImportService);

            tasksService.submitTask(request.getTaskId(), executor, threadPoolTaskExecutor);

        } catch (Exception e) {
            String msg = "Error while start import";
            log.error(msg, e);
            ExportException.throwException(msg, e);
        }
    }
}
