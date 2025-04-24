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

package org.qubership.atp.ei.node.services;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.qubership.atp.ei.node.dto.ExportFileDescriptor;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.qubership.atp.ei.node.repo.GridFsRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExportImportFilesService {

    private final GridFsRepository gridFsRepository;
    private final FileService fileService;

    public ExportImportFilesService(GridFsRepository gridFsRepository, FileService fileService) {
        this.gridFsRepository = gridFsRepository;
        this.fileService = fileService;
    }

    /**
     * Store in grid fs string.
     *
     * @param dir       the dir
     * @param processId the process id
     * @return the string
     * @throws IOException the io exception
     */
    public String storeInGridFs(Path dir, String processId) throws IOException {
        return gridFsRepository.store(Files.newInputStream(dir), dir.getFileName().toString(),
                "application/zip", processId
        ).toString();
    }

    /**
     * Download file in dir path.
     *
     * @param fileDescriptor the file descriptor
     * @param defaultWorkDir the default work dir
     * @return the path
     */
    public Path downloadFileInDir(ExportFileDescriptor fileDescriptor, Path defaultWorkDir) {
        InputStreamResource resource = getResourceById(fileDescriptor.getFileId());
        Path archive = createExportFile(defaultWorkDir, UUID.randomUUID().toString());
        try (OutputStream os = Files.newOutputStream(archive, StandardOpenOption.APPEND)) {
            StreamUtils.copy(resource.getInputStream(), os);
        } catch (IOException e) {
            log.error("Failed to load file {} from DB", fileDescriptor, e);
            throw new ExportException("Failed to load file from DB", e);
        }
        return archive;
    }

    public InputStreamResource getResourceById(String objectId) {
        return gridFsRepository.getResourceById(new ObjectId(objectId));
    }

    private Path createExportFile(Path defaultWorkDir, String fileName) throws ExportException {
        String archiveName = fileName + ".zip";
        Path archive = defaultWorkDir.resolve(archiveName);
        fileService.createFile(archive);
        return archive;
    }
}