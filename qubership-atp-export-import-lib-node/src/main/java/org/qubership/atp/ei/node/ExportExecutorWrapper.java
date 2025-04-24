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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.dto.ExportFormat;
import org.qubership.atp.ei.node.dto.ExportImportData;
import org.qubership.atp.ei.node.dto.ExportNodeInfo;
import org.qubership.atp.ei.node.dto.ExportScope;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.qubership.atp.ei.node.services.FileService;
import org.qubership.atp.ei.node.services.MetricsExportImportService;
import org.qubership.atp.ei.node.services.impl.NotifyService;
import org.qubership.atp.integration.configuration.mdc.MdcUtils;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ExportExecutorWrapper implements CancellableExportImportTask, ExportExecutor {

    private final ExportExecutor exportExecutor;
    private final ExportNodeInfo exportNodeInfo;
    private final NotifyService notifyService;
    private final ObjectMapper objectMapper;
    private final UUID projectId;
    private final ExportFormat format;
    private final Path workDir;
    private final RunNodeRequest request;
    private final ExportScope exportScope;
    private final ExportImportFilesService exportImportFilesService;
    private final SecurityContext securityContext;
    private final FileService fileService;
    private boolean isCancelled;
    private Map<String, String> mdcContext;
    private final MetricsExportImportService metricsExportImportService;

    /**
     * Instantiates a new Export executor wrapper.
     *
     * @param request        the request
     * @param defaultWorkDir the default work dir
     * @param exportExecutor the export executor
     * @param exportNodeInfo the export node info
     * @param notifyService  the notify service to orchestrator
     * @param objectMapper   the object mapper
     */
    public ExportExecutorWrapper(RunNodeRequest request, Path defaultWorkDir,
                                 ExportExecutor exportExecutor, ExportNodeInfo exportNodeInfo,
                                 NotifyService notifyService,
                                 ObjectMapper objectMapper, ExportImportFilesService exportImportFilesService,
                                 FileService fileService, MetricsExportImportService metricsExportImportService) {
        this.request = request;
        this.projectId = request.getProjectId();
        this.exportScope = request.getExportScope();
        this.exportExecutor = exportExecutor;
        this.notifyService = notifyService;
        this.exportNodeInfo = exportNodeInfo;
        this.objectMapper = objectMapper;
        this.format = request.getExportFormat();
        this.exportImportFilesService = exportImportFilesService;
        this.securityContext = SecurityContextHolder.getContext();
        this.fileService = fileService;
        this.workDir = createExportWorkDir(defaultWorkDir, fileService);
        this.mdcContext = MDC.getCopyOfContextMap();
        this.metricsExportImportService = metricsExportImportService;
    }

    @SneakyThrows
    private Path createExportWorkDir(Path defaultWorkDir, FileService fileService) {
        if (Files.exists(defaultWorkDir)) {
            fileService.deletePath(defaultWorkDir);
        }

        return fileService.createDirectory(defaultWorkDir);
    }

    @Override
    public Object call() throws Exception {
        log.debug("Export wrapper, mdcContext = {}", mdcContext);
        final Stopwatch timer = Stopwatch.createStarted();
        metricsExportImportService.registerProcess(request.getProjectId().toString(), request.getProcessId(),
                metricsExportImportService.ACTIVE_EXPORT_PROCESS_COUNT);

        MdcUtils.setContextMap(mdcContext);
        SecurityContextHolder.setContext(securityContext);
        Path dir;
        String fileId;
        try {
            exportToFolder(new ExportImportData(projectId, exportScope, format), workDir);
            dir = fileService.packDirectory(workDir);
            fileId = exportImportFilesService.storeInGridFs(dir, request.getProcessId());
        } catch (Exception e) {
            log.error("Error occurred while exporting", e);
            if (!isCancelled()) {
                notifyService.notifyExportFlow(e, request);
            }
            throw e;
        } finally {
            fileService.deletePath(workDir);
            try {
                metricsExportImportService.addTimeMetric(request.getProjectId(), timer,
                        metricsExportImportService.TOTAL_TIME_OF_EXPORT_PROCESSES);
                metricsExportImportService.unregisterProcess(UUID.fromString(request.getProcessId()));
            } catch (Throwable e) {
                log.error("Unable unregister metric for processId {}", request.getProcessId(), e);
                throw new ExportException("Unable unregister metric for processId " + request.getProcessId());
            }
        }
        notifyService.notifyExportFlow(request, fileId);

        return dir;
    }

    @SneakyThrows
    private void putExportNodeInfoInDir(Path workDir) {
        Path exportNodeInfoFile = workDir.resolve(getExportImplementationName() + ".json");
        objectMapper.writeValue(exportNodeInfoFile.toFile(), exportNodeInfo);
    }

    @Override
    public void exportToFolder(ExportImportData exportData, Path workDir) throws Exception {
        log.info("Export project {} with objects {} started. format {} ", projectId, exportScope, format);

        Path dirForExport = workDir;
        if (ExportFormat.ATP == format) {
            dirForExport = fileService.createDirectory(getExportImplementationName(), workDir);
            putExportNodeInfoInDir(workDir);
        }

        exportExecutor.exportToFolder(exportData, dirForExport);

        if (ExportFormat.ATP == format) {
            if (fileService.isDirNotEmpty(dirForExport)) {
                fileService.packDirectory(dirForExport);
            }
            fileService.deletePath(dirForExport);
        }

        log.info("Export project {} with objects {} finished", projectId, exportScope);
    }

    @Override
    public String getExportImplementationName() {
        return exportExecutor.getExportImplementationName();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled() {
        this.isCancelled = true;
    }
}
