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

package org.qubership.atp.ei.ntt.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.qubership.atp.ei.ntt.ExportConverterResult;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.settings.model.dal.SettingsResource;
import org.qubership.atp.ei.ntt.utils.NttModelLoader;
import org.springframework.util.FileSystemUtils;

public class NttExportConverterResult implements ExportConverterResult {

    final List<Project> projects;
    final SettingsResource environments;
    final List<Scope> scopes;
    final boolean isExportCatalog;
    final Path workDir;

    /**
     * Instantiates a new Ntt export converter result.
     *
     * @param projects        the projects
     * @param scopes          the scopes
     * @param environments    the environments
     * @param isExportCatalog the is export catalog
     * @param workDir         the work dir
     */
    public NttExportConverterResult(List<Project> projects,
                                    List<Scope> scopes, SettingsResource environments,
                                    boolean isExportCatalog, Path workDir) {
        this.projects = projects == null ? new ArrayList<>() : projects;
        this.environments = environments;
        this.scopes = scopes == null ? new ArrayList<>() : scopes;
        this.isExportCatalog = isExportCatalog;
        this.workDir = workDir;
    }

    @Override
    public void saveToFolder(Path saveToDir) throws IOException {
        boolean doPostDeleteSource = false;
        if (saveToDir.equals(workDir)) {
            doPostDeleteSource = true;
            saveToDir = Files.createDirectory(workDir.resolve("tmp"));
        }

        NttModelLoader instance = NttModelLoader.getInstance();

        for (Project project : projects) {
            if (isExportCatalog) {
                instance.saveProject(project, saveToDir);
            } else {
                instance.saveDataSet(project, saveToDir);
            }
        }

        for (Scope scope : scopes) {
            instance.saveScope(scope, saveToDir);
        }

        if (environments != null) {
            instance.saveEnvironmentSettings(environments, saveToDir);
        }

        if (doPostDeleteSource) {
            List<Path> dirs = Files.list(workDir).collect(Collectors.toList());
            for (Path dir : dirs) {
                if (dir.equals(saveToDir)) {
                    continue;
                }
                FileSystemUtils.deleteRecursively(dir);
            }
            FileSystemUtils.copyRecursively(saveToDir, workDir);
            FileSystemUtils.deleteRecursively(saveToDir);
        }
    }
}
