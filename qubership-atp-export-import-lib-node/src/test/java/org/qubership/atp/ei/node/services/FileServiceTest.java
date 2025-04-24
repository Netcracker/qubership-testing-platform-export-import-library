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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {
    
    @Rule
    public TemporaryFolder workDir = new TemporaryFolder();

    private FileService service;

    @Before
    public void init() {
        service = new FileService();
    }

    /**
     * -workdir
     *      -outdatedButNotEmptyFolder (not delete)
     *          -outdatedFileInOutdatedFolder.txt (delete)
     *          -fresh2LevelFolder (not delete)
     *              -fresh3LevelFile.zip (not delete)
     *      -fresh1LevelFolder (not delete)
     *          -fresh2LevelFile.txt (not delete)
     *          -outdated2LevelFile.zip (delete)
     *          -outdated2LevelFolder (delete)
     *              -outdated3LevelFile.zip (delete)
     *          -freshButEmptyFolder (delete)
     *      -fresh1LevelFile.txt (not delete)
     * @throws IOException
     */
    @Test
    public void removeAllOutdatedFiles() throws IOException {
        long expirationTime = 120 * 1000;
        File outdatedButNotEmptyFolder = workDir.newFolder("outdatedButNotEmptyFolder");
        File outdatedFileInOutdatedFolder = new File(outdatedButNotEmptyFolder, "outdatedFileInOutdatedFolder.txt");
        outdatedFileInOutdatedFolder.createNewFile();
        File fresh2LevelFolder = new File(outdatedButNotEmptyFolder, "fresh2LevelFolder");
        fresh2LevelFolder.mkdir();
        File fresh3LevelFile = new File(fresh2LevelFolder, "fresh3LevelFile.zip");
        fresh3LevelFile.createNewFile();
        File fresh1LevelFolder = workDir.newFolder("fresh1LevelFolder");
        File fresh2LevelFile = new File(fresh1LevelFolder, "fresh2LevelFile.txt");
        fresh2LevelFile.createNewFile();
        File outdated2LevelFile = new File(fresh1LevelFolder, "outdated2LevelFile.zip");
        outdated2LevelFile.createNewFile();
        File outdated2LevelFolder = new File(fresh1LevelFolder, "outdated2LevelFolder");
        outdated2LevelFolder.mkdir();
        File outdated3LevelFile = new File(outdated2LevelFolder, "outdated3LevelFile.zip");
        outdated3LevelFile.createNewFile();
        File freshButEmptyFolder = new File(fresh1LevelFolder, "freshButEmptyFolder");
        freshButEmptyFolder.mkdir();
        fresh1LevelFolder.mkdir();
        File fresh1LevelFile = workDir.newFile("fresh1LevelFile.txt");

        long lastModifiedTimeForOutdatedFiles = System.currentTimeMillis() - expirationTime * 4;
        outdatedButNotEmptyFolder.setLastModified(lastModifiedTimeForOutdatedFiles);
        outdatedFileInOutdatedFolder.setLastModified(lastModifiedTimeForOutdatedFiles);
        outdated2LevelFile.setLastModified(lastModifiedTimeForOutdatedFiles);
        outdated2LevelFolder.setLastModified(lastModifiedTimeForOutdatedFiles);
        outdated3LevelFile.setLastModified(lastModifiedTimeForOutdatedFiles);

        service.removeAllOutdatedFilesAndFolders(workDir.getRoot().toPath(), expirationTime);

        assertFalse(outdatedFileInOutdatedFolder.exists(),
                "Outdated folder " + outdatedFileInOutdatedFolder.getAbsolutePath() + " not deleted");
        assertFalse(outdated2LevelFile.exists(),
                "Outdated file " + outdated2LevelFile.getAbsolutePath() + " not deleted");
        assertFalse(outdated2LevelFolder.exists(),
                "Outdated folder " + outdated2LevelFolder.getAbsolutePath() + " not deleted");
        assertFalse(outdated3LevelFile.exists(),
                "Outdated file " + outdated3LevelFile.getAbsolutePath() + " not deleted");
        assertFalse(freshButEmptyFolder.exists(),
                "Empty folder " + freshButEmptyFolder.getAbsolutePath() + " not deleted");

        assertTrue(outdatedButNotEmptyFolder.exists(),
                "Not empty folder " + outdatedButNotEmptyFolder.getPath() + " deleted");
        assertTrue(fresh2LevelFolder.exists(), "Fresh folder " + fresh2LevelFolder.getPath() + " deleted");
        assertTrue(fresh3LevelFile.exists(), "Fresh file " + fresh3LevelFile.getPath() + " deleted");
        assertTrue(fresh1LevelFolder.exists(), "Fresh folder " + fresh1LevelFolder.getPath() + " deleted");
        assertTrue(fresh2LevelFile.exists(), "Fresh file " + fresh2LevelFile.getPath() + " deleted");
        assertTrue(fresh1LevelFile.exists(), "Fresh file " + fresh1LevelFile.getPath() + " deleted");
    }
}
