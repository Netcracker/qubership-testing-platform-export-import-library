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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.qubership.atp.ei.node.ExportExecutor;
import org.qubership.atp.ei.node.constants.Constant;
import org.qubership.atp.ei.node.dto.ExportImportData;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.services.ExportImportFilesService;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class ExportNodeServiceImplTest extends AbstractConfigTest {

    private ExportExecutor exportExecutor;
    private ExportNodeServiceImpl exportNodeService;
    private ExportImportFilesService exportImportFilesService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        exportExecutor = new ExportExecutor() {
            @Override
            public void exportToFolder(ExportImportData exportData, Path workDir) throws Exception {
                Path projectDir = workDir.resolve(projectName);
                fileService.createDirectory(projectDir);
                String filename = exportData.getProjectId().toString() + fileExt;
                Path file = fileService.createFile(filename, projectDir);
                Files.write(file, "Hello world!".getBytes());
                Thread.sleep(5 * 1000);
            }

            @Override
            public String getExportImplementationName() {
                return implName;
            }
        };

        exportImportFilesService = new ExportImportFilesService(null, fileService) {

            public String storeInGridFs(Path dir, String processId) throws IOException {
                return "randomObjectIdString";
            }
        };

        exportNodeService = new ExportNodeServiceImpl(threadPoolTaskExecutor, notifyService,
                objectMapper, exportExecutor, tasksService, exportImportFilesService, fileService, metricsExportImportService );

        ReflectionTestUtils.setField(exportNodeService, "applicationName", "Application_Name");
    }

    @Test
    public void runExport() throws ExecutionException, InterruptedException, ExportException {

        exportNodeService.runExport(request);
        Future<Object> task = tasksService.getTaskById(taskId);

        Assert.assertNotNull(task);
        Path result = (Path) task.get();
        Assert.assertTrue(Files.exists(result));

        Thread.sleep(10 * 1000);
        task = tasksService.getTaskById(taskId);
        Assert.assertNull(task);

        Path exportFolder = fileService.getFolderPath(projectId, processId, Constant.EXPORT);

        Path workDir = exportFolder.resolve(implName);
        Assert.assertFalse(Files.exists(workDir));

        Path unpDir = exportFolder.resolve("unzip_" + result.getFileName().toString());
        fileService.unpackZipFile(result, unpDir);

        Path result2 = unpDir.resolve(implName + ".zip");
        Assert.assertTrue(Files.exists(result2));

        Path result3 = unpDir.resolve(implName + ".json");
        Assert.assertTrue(Files.exists(result3));

        Path unpDir2 = unpDir.resolve("unzip_" + result2.getFileName().toString());
        fileService.unpackZipFile(result2, unpDir2);

        Path exportFileFromArchive = unpDir2.resolve(projectName).resolve(projectId.toString() + fileExt);
        Assert.assertTrue(Files.exists(exportFileFromArchive));
    }

    @Test
    public void cancelExport_onCancelTask_doNotCallNotifyExportFlow() throws InterruptedException, ExportException {
        exportNodeService.runExport(request);
        tasksService.cancelTask(taskId);
        Thread.sleep(10 * 1000);

        verify(notifyService, times(0)).notifyExportFlow((Exception) any(), any());
    }

    @Test
    public void getExportPath() {
        UUID projectId = UUID.randomUUID();
        Path exportPath = fileService.getFolderPath(projectId, processId, Constant.EXPORT);
        Assert.assertTrue(exportPath.toAbsolutePath().toString().lastIndexOf(processId) > 0);
        Assert.assertTrue(exportPath.toAbsolutePath().toString().lastIndexOf(projectId.toString()) > 0);
    }
}
