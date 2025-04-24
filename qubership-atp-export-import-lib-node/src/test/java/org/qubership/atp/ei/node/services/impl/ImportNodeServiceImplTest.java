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
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.qubership.atp.ei.node.ImportExecutor;
import org.qubership.atp.ei.node.constants.Constant;
import org.qubership.atp.ei.node.dto.ExportImportData;
import org.qubership.atp.ei.node.dto.ValidationResult;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class ImportNodeServiceImplTest extends AbstractConfigTest {

    private ImportExecutor importExecutor;
    private ImportNodeServiceImpl importNodeService;
    private List<TestData> testDataList;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        super.setUp();

        importExecutor = new ImportExecutor() {
            @Override
            public void importData(ExportImportData importData, Path workDir) throws Exception {
                log.info("Sleep at importData");
                Thread.sleep(5 * 1000);
                testDataList = new ArrayList<>();
                for (File file : workDir.toFile().listFiles()) {
                    TestData data = objectMapper.readValue(file, TestData.class);
                    testDataList.add(data);
                }
            }

            @Override
            public ValidationResult preValidateData(ExportImportData importData, Path workDir) throws Exception {
                return null;
            }

            @Override
            public ValidationResult validateData(ExportImportData importData, Path workDir) throws Exception {
                log.info("Sleep at validateData");
                Thread.sleep(5 * 1000);
                return new ValidationResult(Arrays.asList("Warning"));
            }
        };

        importNodeService =
                new ImportNodeServiceImpl(threadPoolTaskExecutor, importExecutor, tasksService, notifyService,
                        exportImportFilesService, fileService, metricsExportImportService);
    }

    @Test
    public void runValidation() throws ExecutionException, InterruptedException, ExportException, IOException {
        //
        // VALIDATION
        //
        String jsonForValidationName = "TestScenario.json";
        Resource zipForValidation = new ClassPathResource("TestScenario.zip");

        when(gridFsRepository.getResourceById(any(ObjectId.class)))
                .thenReturn(new InputStreamResource(FileUtils.openInputStream(zipForValidation.getFile())));

        importNodeService.validateImport(request);

        // task should exists
        Future<Object> task = tasksService.getTaskById(taskId);
        Assert.assertNotNull(task);

        // unzip json file should exists
        Thread.sleep(1 * 1000);
        Path importFolder = fileService.getFolderPath(projectId, processId, Constant.IMPORT);
        Path jsonFileForValidation = importFolder.resolve(jsonForValidationName);
        Assert.assertTrue(Files.exists(jsonFileForValidation));

        // saved zip file should not be exists
        Path importZipFile = fileService.getFilePath(projectId, taskId, Constant.IMPORT);
        Assert.assertFalse(Files.exists(importZipFile));

        // task should not be exists
        Thread.sleep(5 * 1000);
        task = tasksService.getTaskById(taskId);
        Assert.assertNull(task);

        //
        // IMPORT
        //

        when(gridFsRepository.getResourceById(any(ObjectId.class)))
                .thenReturn(new InputStreamResource(FileUtils.openInputStream(zipForValidation.getFile())));

        importNodeService.runImport(request);

        // import folder should be exists
        Assert.assertTrue(Files.exists(importFolder));

        Thread.sleep(10 * 1000);

        // import folder should not be exists
        Assert.assertFalse(Files.exists(importFolder));

        // list should be not empty
        Assert.assertEquals(testDataList, Collections.singletonList(new TestData(111, "test")));

    }

    @Test
    public void cancelValidation_onCancelTask_doNotCallNotifyImportFlow()
            throws InterruptedException, ExportException, IOException {
        Resource zipForValidation = new ClassPathResource("TestScenario.zip");

        when(gridFsRepository.getResourceById(any(ObjectId.class)))
                .thenReturn(new InputStreamResource(FileUtils.openInputStream(zipForValidation.getFile())));

        importNodeService.validateImport(request);
        Thread.sleep(1 * 1000);
        tasksService.cancelTask(taskId);
        Thread.sleep(10 * 1000);

        verify(notifyService, times(0)).notifyImportFlow(any(), any());
    }

    @Test
    public void cancelImport_onCancelTask_doNotCallNotifyImportFlow()
            throws InterruptedException, ExportException, IOException {
        Resource zipForValidation = new ClassPathResource("TestScenario.zip");

        when(gridFsRepository.getResourceById(any(ObjectId.class)))
                .thenReturn(new InputStreamResource(FileUtils.openInputStream(zipForValidation.getFile())));

        importNodeService.validateImport(request);
        Thread.sleep(7 * 1000);

        when(gridFsRepository.getResourceById(any(ObjectId.class)))
                .thenReturn(new InputStreamResource(FileUtils.openInputStream(zipForValidation.getFile())));
        importNodeService.runImport(request);

        Thread.sleep(1000);
        tasksService.cancelTask(taskId);
        Thread.sleep(10 * 1000);

        verify(notifyService, times(0)).notifyImportFlow(any(), any());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static private class TestData {
        private Integer id;
        private String name;
    }

}
