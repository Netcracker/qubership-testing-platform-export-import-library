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

import org.qubership.atp.ei.ntt.Constants;

/**
 * TODO Make javadoc documentation for this method.
 */
public interface IFile extends Constants {

    void setPathToFile(String pathToFile);

    File getFile();

    String getFileName();

    /**
     * Returns the absolute pathname string of this file.
     */
    String getAbsolutePathToFile();

    /**
     * Returns the related pathname string of this file.
     */
    String getRelatedPathToFile();

    String getDirectoryPath();

    String getPathToSave();
}
