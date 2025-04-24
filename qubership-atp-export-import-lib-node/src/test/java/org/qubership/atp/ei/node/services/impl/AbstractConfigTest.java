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
import java.util.UUID;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.qubership.atp.ei.node.config.ExportImportNodeConfig;
import org.qubership.atp.ei.node.dto.ExportFileDescriptor;
import org.qubership.atp.ei.node.dto.ExportFormat;
import org.qubership.atp.ei.node.dto.RunNodeRequest;
import org.qubership.atp.ei.node.repo.GridFsRepository;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.qubership.atp.ei.node.services.FileService;
import org.qubership.atp.ei.node.services.MetricsExportImportService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract public class AbstractConfigTest {
    @Mock
    protected NotifyService notifyService;
    @Mock
    protected GridFsRepository gridFsRepository;
    @Mock
    protected MetricsExportImportService metricsExportImportService;
    protected ExportImportFilesService exportImportFilesService;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected final String implName = "Impl_Name";
    protected final String projectName = "Project";
    protected final String fileExt = ".json";

    protected ThreadPoolTaskExecutor threadPoolTaskExecutor;
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected TasksService tasksService;
    protected Path defaultWorkDir;

    protected UUID projectId;
    protected String processId;
    protected String taskId;
    protected RunNodeRequest request;

    protected FileService fileService = new FileService();

    public void setUp() throws Exception {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.initialize();
        threadPoolTaskExecutor = executor;

        defaultWorkDir = folder.newFolder("test_folder_" + System.currentTimeMillis()).toPath();
        fileService.createDirectory(defaultWorkDir);

        tasksService = new TasksService();

        ReflectionTestUtils.setField(ExportImportNodeConfig.class, "DEFAULT_WORK_DIR",
                defaultWorkDir.toAbsolutePath().toString());

        projectId = UUID.randomUUID();
        processId = UUID.randomUUID().toString();
        taskId = UUID.randomUUID().toString();

        request = new RunNodeRequest();
        request.setProjectId(projectId);
        request.setProcessId(processId);
        request.setTaskId(taskId);
        request.setExportFormat(ExportFormat.ATP);
        request.setFileDescriptor(new ExportFileDescriptor());
        request.getFileDescriptor().setFileId("6167e6b746faf45620686d97");

        exportImportFilesService = new ExportImportFilesService(gridFsRepository, fileService);
    }
}
