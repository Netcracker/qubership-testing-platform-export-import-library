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

package org.qubership.atp.ei.ntt.settings.model;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.qubership.atp.ei.ntt.settings.FileUtils;

/**
 * TODO Make summary for this class.
 */
public class FileSettings implements IFile {

    private File file;

    public FileSettings(String relatedPath) {

        setPathToFile(relatedPath);
    }

    public FileSettings(File file) {

        setFile(file);
    }

    @Override
    public void setPathToFile(String pathToFile) {

        File tmpFile = new File(pathToFile);
        pathToFile = tmpFile.getAbsolutePath();

        if (FileUtils.isPathRelated(pathToFile)) {
            pathToFile = FileUtils.getAbsolutePath(pathToFile);
        }
        this.file = new File(pathToFile);
    }

    public void setFile(File file) {

        this.file = file;
    }

    @Override
    public File getFile() {

        return file;
    }

    @Override
    public String getFileName() {

        return file.getName();
    }

    public String getFileNameWithoutExt() {

        return FilenameUtils.removeExtension(file.getName());
    }

    @Override
    public String getAbsolutePathToFile() {

        return FileUtils.getPathToFile(file);
    }

    @Override
    public String getRelatedPathToFile() {

        return FileUtils.getRelatedPathToFile(file);
    }

    @Override
    public String getPathToSave() {

        if (FileUtils.isPathRelated(getRelatedPathToFile())) {
            return getRelatedPathToFile();
        } else {
            return getAbsolutePathToFile();
        }
    }

    @Override
    public String getDirectoryPath() {

        return FileUtils.getPathToFile(file.getParentFile());
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public boolean equals(FileSettings fileSettings) {

        if (fileSettings == null || fileSettings.getFile() == null) {

            return this.file == null;
        }

        if (this.file != null) {

            return getAbsolutePathToFile().equals(fileSettings.getAbsolutePathToFile());
        }

        return false;
    }
}
