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
import java.nio.file.Path;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.util.Strings;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ObjectSaverToDiskService {

    private static final String regexInvalidCharInFileName = "[\\\\/:*?\"<>|]";

    private final ObjectWriter writer;
    private final FileService fileService;

    /**
     * Instantiates a new Object saver to disk service.
     *
     * @param fileService the file service
     */
    public ObjectSaverToDiskService(FileService fileService,
                                    @Value("${atp.export.pretty-print:false}") boolean isPrettyPrint) {
        if (isPrettyPrint) {
            this.writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        } else {
            this.writer = new ObjectMapper().writer();
        }
        this.fileService = fileService;
    }

    public void exportAtpEntity(UUID id, Object object, Path dir) throws ExportException {
        exportAtpEntity(id, object, null, dir);
    }

    /**
     * Export atp entity.
     *
     * @param id       the id
     * @param object   the object
     * @param parentId the parent id
     * @param dir      the dir
     * @throws ExportException the export exception
     */
    public void exportAtpEntity(UUID id, Object object, UUID parentId,
                                Path dir) throws ExportException {
        Assert.notNull(id, "Id cannot be null");
        Assert.notNull(object, "Object cannot be null");
        Path testScopesDirPath = fileService.createDirectory(object.getClass().getSimpleName(), dir);
        writeAtpEntityToFile(id, object, parentId, testScopesDirPath);
    }

    /**
     * Write atp entity to file.
     *
     * @param id     the id
     * @param object the object
     * @param dir    the dir
     * @throws ExportException the export exception
     */
    public void writeAtpEntityToFile(UUID id, Object object, Path dir) throws ExportException {
        writeAtpEntityToFile(id, object, null, dir);
    }

    /**
     * Write atp entity to file.
     *
     * @param id       the id
     * @param object   the object
     * @param parentId the parent id
     * @param dir      the dir
     * @throws ExportException the export exception
     */
    public void writeAtpEntityToFile(UUID id, Object object, UUID parentId,
                                     Path dir) throws ExportException {

        writeAtpEntityToFile(id.toString(), object, parentId == null ? null : parentId.toString(), dir, false);

    }

    /**
     * Export atp entity to file with check file name to invalid characters.
     *
     * @param fileName         the file name
     * @param object           the object
     * @param parentName       the parent id
     * @param dir              the dir
     * @param validateFileName validate file name to invalid characters
     * @throws ExportException the export exception
     */
    public void writeAtpEntityToFile(@NotNull String fileName,
                                     @NotNull Object object,
                                     String parentName,
                                     @NotNull Path dir,
                                     boolean validateFileName) throws ExportException {
        fileName = validateFileName ? fileName.replaceAll(regexInvalidCharInFileName, "") : fileName;
        Path filePath = dir;
        if (Strings.isNotBlank(parentName)) {
            filePath = fileService.createDirectory(parentName, filePath);
        }
        filePath = this.fileService.createFile(fileName + ".json", filePath);
        writeObject(filePath, object);
    }

    private void writeObject(Path filePath, Object object) throws ExportException {
        try {
            this.writer.writeValue(filePath.toFile(), object);
        } catch (IOException ex) {
            log.error("Cannot write object {} in file {}", new Object[]{object, filePath.toString(), ex});
            ExportException.throwException("Cannot write object {} in file ",
                    new Object[]{object, filePath.toString(), ex});
        }
    }
}
