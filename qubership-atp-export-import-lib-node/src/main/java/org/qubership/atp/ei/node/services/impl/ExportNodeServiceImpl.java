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

import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.ExportExecutor;
import org.qubership.atp.ei.node.ExportExecutorWrapper;
import org.qubership.atp.ei.node.config.ExportImportNodeConfig;
import org.qubership.atp.ei.node.constants.Constant;
import org.qubership.atp.ei.node.dto.ExportNodeInfo;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.qubership.atp.ei.node.services.ExportNodeService;
import org.qubership.atp.ei.node.services.FileService;
import org.qubership.atp.ei.node.services.MetricsExportImportService;
import org.qubership.atp.integration.configuration.mdc.MdcUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExportNodeServiceImpl implements ExportNodeService {

    @Value("${spring.application.name}")
    private String applicationName;

    private final NotifyService notifyService;
    private final ObjectMapper objectMapper;
    private final ExportExecutor exportExecutor;
    private final TasksService tasksService;
    private final ExportImportFilesService exportImportFilesService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final FileService fileService;
    private Map<String, String> mdcContext;
    private final MetricsExportImportService metricsExportImportService;

    /**
     * Instantiates a new Export node service.
     *
     * @param notifyService  the notify service to orchestrator
     * @param objectMapper   the object mapper
     * @param exportExecutor the export executor
     */
    @Autowired
    public ExportNodeServiceImpl(@Qualifier("atpExportThreadExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                 NotifyService notifyService,
                                 ObjectMapper objectMapper,
                                 ExportExecutor exportExecutor,
                                 TasksService tasksService,
                                 ExportImportFilesService exportImportFilesService,
                                 FileService fileService,
                                 MetricsExportImportService metricsExportImportService) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.notifyService = notifyService;
        this.objectMapper = objectMapper;
        this.exportExecutor = exportExecutor;
        this.tasksService = tasksService;
        this.exportImportFilesService = exportImportFilesService;
        this.fileService = fileService;
        this.mdcContext = MDC.getCopyOfContextMap();
        this.metricsExportImportService = metricsExportImportService;
    }

    @Override
    public void runExport(RunNodeRequest request) {
        log.info("Income  request {}", request.toString());
        if (request.getProjectId() == null || request.getTaskId() == null || request.getProcessId() == null) {
            log.error("The the following params should be filled for export to proceed: projectId, taskId, processId"
                            + "Received projectId {}. taskId {}, processId {}",
                    request.getProjectId(), request.getTaskId(), request.getProcessId());
            return;
        }
        runExport(request, exportExecutor);
    }

    private void runExport(RunNodeRequest request, ExportExecutor exportExecutor) {
        try {
            ExportExecutorWrapper executor = new ExportExecutorWrapper(request,
                    fileService.getFolderPath(request.getProjectId(), request.getProcessId(), Constant.EXPORT),
                    exportExecutor, getExportNodeInfo(false), notifyService, objectMapper,
                    exportImportFilesService, fileService, metricsExportImportService);

            tasksService.submitTask(request.getTaskId(), executor, threadPoolTaskExecutor);
        } catch (Exception e) {
            String msg = "Error while start export";
            log.error(msg, e);
            ExportException.throwException(msg, e);
        }
    }

    @Override
    public void cancelExport(UUID projectId, String taskId, String processId) {
        log.debug("Export wrapper, mdcContext = {}", mdcContext);
        MdcUtils.setContextMap(mdcContext);
        log.info("Cancel Export request for project {} and task {}", projectId, taskId);
        tasksService.cancelTask(taskId);
        deleteExportFile(projectId, processId);
    }

    @Override
    public void deleteExportFile(UUID projectId, String processId) {
        log.info("Delete Export File request for project {} and process {}", projectId, processId);
        fileService.deleteFolder(projectId, processId, Constant.EXPORT);
    }

    @Override
    public ExportNodeInfo getExportNodeInfo(boolean isDetailed) {
        ExportNodeInfo info = new ExportNodeInfo();

        info.setName(applicationName);
        info.setVersion("1"); // TODO write here version of application

        info.setExportImplementationClass(exportExecutor.getClass().getCanonicalName());
        info.setExportImplementationName(exportExecutor.getExportImplementationName());

        if (isDetailed) {
            info.setDefaultWorkDir(ExportImportNodeConfig.DEFAULT_WORK_DIR);
            info.setActiveExportCount(threadPoolTaskExecutor.getActiveCount());
        }

        return info;
    }
}
