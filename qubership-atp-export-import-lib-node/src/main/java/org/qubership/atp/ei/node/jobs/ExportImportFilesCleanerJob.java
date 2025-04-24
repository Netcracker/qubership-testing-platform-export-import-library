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

package org.qubership.atp.ei.node.jobs;

import java.nio.file.Paths;
import java.util.List;

import org.qubership.atp.common.lock.LockManager;
import org.qubership.atp.ei.node.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "atp.ei.file.cleanup.job.enable", matchIfMissing = true)
@EnableScheduling
@ComponentScan(basePackages = "org.qubership.atp.common.lock")
@Slf4j
public class ExportImportFilesCleanerJob {

    @Value("${atp.export.workdir:exportimport/node}")
    private String workDir;

    @Value("${atp.ei.file.delete.after.ms:172800000}")
    private Long expirationTimeMillis;

    private final LockManager lockManager;
    private final FileService fileService;

    public ExportImportFilesCleanerJob(LockManager lockManager, FileService fileService) {
        this.lockManager = lockManager;
        this.fileService = fileService;
    }

    /**
     * Job to delete old files.
     */
    @Scheduled(fixedRateString = "${atp.ei.file.cleanup.job.fixedRate:86400000}")
    public void scheduledFileRemoving() {
        log.info("Start cleaning old export-import files in folder {}", workDir);
        lockManager.executeWithLock("Cleaning up old export-import files", () -> {
            List<String> deletedPaths = fileService.removeAllOutdatedFilesAndFolders(
                    Paths.get(workDir), expirationTimeMillis);
            log.info("Cleaning old export-import files. Removed files and directories: {}", deletedPaths);
        });
        log.info("End cleaning old export-import files in folder {}", workDir);
    }
}
