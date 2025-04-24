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

import static java.util.Objects.isNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.qubership.atp.ei.node.config.ExportImportNodeConfig;
import org.qubership.atp.ei.node.exceptions.ExportException;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {
    private static final String ATTRIBUTE_LAST_MOD_NAME = "lastModifiedTime";

    /**
     * Delete path.
     *
     * @param dir the dir
     * @throws ExportException the export exception
     */
    public void deletePath(Path dir) throws ExportException {
        if (dir == null) {
            return;
        }
        log.debug("delete path {}", dir);
        try {
            FileSystemUtils.deleteRecursively(dir);
        } catch (IOException e) {
            log.error("Cannot delete dir {} ", dir, e);
        }
    }

    /**
     * Delete specific root folder for import or export.
     */
    public void deleteFolder(UUID projectId, String taskId, String folderName) {
        Path folder = getFolderPath(projectId, taskId, folderName);
        deletePath(folder);
    }

    /**
     * Copy path.
     *
     * @param dir  the dir
     * @param dest the dest
     * @throws ExportException the export exception
     */
    public void copyPath(Path dir, Path dest) throws ExportException {
        Assert.notNull(dir, "Argument dir is null");
        Assert.notNull(dest, "Argument dest is null");
        log.debug("copy path {} to {}", dir.toString(), dest.toString());
        try {
            FileSystemUtils.copyRecursively(dir, dest);
        } catch (IOException e) {
            log.error("Cannot copy dir {} to {}", dir, dest, e);
            ExportException.throwException("Cannot copy dir {} to ", dir, dest, e);
        }
    }

    /**
     * Create directory path.
     *
     * @param name   the name
     * @param parent the parent
     * @return the path
     * @throws ExportException the export exception
     */
    public Path createDirectory(String name, Path parent) throws ExportException {
        Assert.notNull(parent, "Argument parent is null");
        Path result = parent.resolve(name);
        return createDirectory(result);
    }

    /**
     * Create directory path.
     *
     * @param dir the dir
     * @return the path
     * @throws ExportException the export exception
     */
    public Path createDirectory(Path dir) throws ExportException {
        Assert.notNull(dir, "Argument dir is null");
        log.debug("create dir {}", dir.toString());
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            log.error("Cannot create dir {} ", dir.toString(), e);
            ExportException.throwException("Cannot create dir {} ", dir.toString(), e);
        }
        return dir;
    }

    /**
     * Create file path.
     *
     * @param name   the name
     * @param parent the parent
     * @return the path
     * @throws ExportException the export exception
     */
    public Path createFile(String name, Path parent) throws ExportException {
        Assert.notNull(parent, "Argument parent is null");
        Path file = parent.resolve(name);
        return createFile(file);
    }

    /**
     * Create file path.
     *
     * @param file the file
     * @return the path
     * @throws ExportException the export exception
     */
    public Path createFile(Path file) throws ExportException {
        Assert.notNull(file, "Argument file is null");
        log.debug("create file {}", file.toString());
        try {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            log.error("Cannot create dir {} ", file.toString(), e);
            ExportException.throwException("Cannot create dir {} ", file.toString(), e);
        }
        return file;
    }

    /**
     * Pack directory path.
     *
     * @param dirToZip the dir to zip
     * @return the path
     * @throws ExportException the export exception
     */
    public Path packDirectory(Path dirToZip) throws ExportException {
        Assert.notNull(dirToZip, "Argument dirToZip is null");
        log.debug("pack dir {}", dirToZip.toString());
        Path parentDir = dirToZip.getParent();
        String archiveFileName = dirToZip.getFileName().toString();

        Path archiveFile = Paths.get(parentDir.toAbsolutePath().toString(), archiveFileName + ".zip");
        deletePath(archiveFile);

        try (OutputStream fos = Files.newOutputStream(archiveFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            Files.walkFileTree(dirToZip, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dirToZip.equals(dir)) {
                        zipOut.putNextEntry(new ZipEntry(dirToZip.relativize(dir).toString() + "/"));
                        zipOut.closeEntry();
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zipOut.putNextEntry(new ZipEntry(dirToZip.relativize(file).toString()));
                    Files.copy(file, zipOut);
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            log.error("Cannot zip dir {}", dirToZip, e);
            ExportException.throwException("Cannot zip dir {}", dirToZip, e);
        }

        return archiveFile;
    }

    /**
     * Check that the directory is not empty.
     *
     * @param directory the directory
     * @return the boolean
     */
    public boolean isDirNotEmpty(Path directory) {
        boolean isNotEmpty = false;
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            isNotEmpty = dirStream.iterator().hasNext();
        } catch (Exception e) {
            log.error("Cannot read directory {}", directory, e);
        }

        return isNotEmpty;
    }

    /**
     * Unpack zip file path.
     *
     * @param zipFile the zip file
     * @param dest    the dest
     * @return the path
     * @throws ExportException the export exception
     */
    public Path unpackZipFile(Path zipFile, Path dest) throws ExportException {
        Assert.notNull(zipFile, "Argument zipFile is null");
        Assert.notNull(dest, "Argument dest is null");
        log.debug("unpack zip {} in {}", zipFile.toString(), dest.toString());
        if (!Files.exists(dest)) {
            createDirectory(dest);
        }
        byte[] buffer = new byte[1024];
        try (InputStream in = Files.newInputStream(zipFile); ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newFile = dest.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    createDirectory(newFile);
                } else {
                    createDirectory(newFile.getParent());
                    createFile(newFile);
                    try (OutputStream fos = Files.newOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error("Cannot unzip archive {} in dir {}", zipFile, dest, e);
            ExportException.throwException("Cannot unzip archive {} in dir {}", zipFile, dest, e);
        }
        return dest;
    }

    /**
     * Get path to root folder for export or import.
     */
    public Path getFolderPath(UUID projectId, String taskId, String exportOrImportFolderName) {
        return Paths.get(ExportImportNodeConfig.DEFAULT_WORK_DIR)
                .resolve(projectId.toString())
                .resolve(exportOrImportFolderName)
                .resolve(taskId);
    }

    /**
     * Get path to specific zip file from export or import root folder.
     */
    public Path getFilePath(UUID projectId, String taskId, String exportOrImportFolderName) {
        Path workDir = getFolderPath(projectId, taskId, exportOrImportFolderName);
        return workDir.getParent().resolve(taskId + ".zip");
    }

    /**
     * Get path to specific zip file from export or import root folder.
     */
    public Path getFilePathIfExists(UUID projectId, String taskId, String exportOrImportFolderName) {
        Path archive = getFilePath(projectId, taskId, exportOrImportFolderName);
        if (Files.exists(archive)) {
            return archive;
        }
        return null;
    }

    /**
     * Save input stream source in file.
     */
    public void saveInputStreamSourceInFile(InputStreamSource inputStreamSource,
                                            Path file) throws ExportException {
        try (InputStream in = inputStreamSource.getInputStream(); OutputStream out = Files.newOutputStream(file)) {
            StreamUtils.copy(in, out);
        } catch (IOException e) {
            log.error("Can not save input stream in file {}", file, e);
            ExportException.throwException("Can not save input stream in file {}", file, e);
        }
    }

    /**
     * Find all archives list.
     *
     * @param workDir the work dir
     * @return the list
     */
    public List<Path> findAllArchives(Path workDir) {
        List<Path> archives = new ArrayList<>();
        try (Stream<Path> files = Files.find(workDir, 1,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile()
                        && path.getFileName().toString().contains(".zip"))) {
            archives.addAll(files.collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("Cannot find export files in dir {}", workDir, e);
            ExportException.throwException("Cannot find export files in dir {}", workDir, e);
        }
        return archives;
    }

    /**
     * Delete all outdated files and folders.
     * Along with the folder, all internal folders and files are deleted,
     * regardless of the time they were last modified.
     * @param workDir root directory
     * @param expirationTimeInMilliseconds period of time
     *                                     in which files are not considered out of date (in milliseconds).
     * @return list of deleted paths.
     */
    public List<String> removeAllOutdatedFilesAndFolders(Path workDir, Long expirationTimeInMilliseconds) {
        final Long expirationDate = System.currentTimeMillis() - expirationTimeInMilliseconds;
        List<String> deletedPaths = new ArrayList<>();
        if (isNull(workDir) || !Files.exists(workDir)) {
            log.warn("Can not delete old files in {} directory, because it doesn't exist", workDir);
            return deletedPaths;
        }
        try {
            Files.walkFileTree(workDir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.lastModifiedTime().toMillis() < expirationDate) {
                        deletedPaths.add(file.toString());
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        log.error("An error occurred while deleting old files in folder {}", dir, exc);
                    }
                    if (!isDirNotEmpty(dir)) {
                        deletedPaths.add(dir.toString());
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Can not delete files older than {} millis in {} dir", expirationTimeInMilliseconds, workDir, e);
            ExportException.throwException("Can not delete files older than {} millis in {} dir",
                    expirationTimeInMilliseconds, workDir, e);
        }
        return deletedPaths;
    }
}
