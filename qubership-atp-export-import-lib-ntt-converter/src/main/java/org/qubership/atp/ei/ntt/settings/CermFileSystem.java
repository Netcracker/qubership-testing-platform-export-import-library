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

package org.qubership.atp.ei.ntt.settings;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;


public final class CermFileSystem {

    /* REROGANIZED */

    public static final String DIR_APPLICATION = getAppDir();

    @Nonnull
    public static final File APP_DIR = new File(DIR_APPLICATION);

    // Root folders
    public static final String DIR_ACTIONS = DIR_APPLICATION + "/actions/";
    public static final String DIR_CONFIG = DIR_APPLICATION + "/config/";
    public static final String DIR_DOCS = DIR_APPLICATION + "/docs/";
    public static final String DIR_PROJECTS = DIR_APPLICATION + "/projects/";
    public static final String DIR_RESOURCES = DIR_APPLICATION + "/resources/";

    // Actions folders
    public static final String DIR_ACTIONS_DEPENDENCIES = DIR_ACTIONS + "dependencies/";
    public static final String DIR_ACTIONS_LIBRARIES = DIR_ACTIONS + "libraries/";

    // Config files
    public static final String DESKTOP_SETTINGS_FILE_NAME = DIR_CONFIG + "desktop.xml";
    public static final String LOG4J_CONFIG_FILE_NAME = DIR_CONFIG + "log4j.xml";
    public static final String EXEC_SETTINGS_FILE_NAME = DIR_CONFIG + "settings.xml";
    public static final String DEFAULT_TEST_PROPS_FILE_NAME = DIR_CONFIG + "test.properties";

    // Docs folders
    public static final String DIR_DOC_CHANGELOG = DIR_DOCS + "changelog/";
    public static final String DIR_DOC_MANUAL = DIR_DOCS + "manual/";

    // Resources folders
    public static final String DIR_RES_COMPONENTS = DIR_RESOURCES + "components/";
    public static final String DIR_RES_DEPENDENCIES = DIR_RESOURCES + "dependencies/";
    public static final String DIR_RES_PYTHON = DIR_RESOURCES + "python/";
    public static final String DIR_EDITOR = DIR_RES_COMPONENTS + "editor/";
    public static final String DIR_PYTHON_SCRIPTS = DIR_RES_PYTHON + "scripts/";

    // Single files
    public static final String EDITOR_MAIN_FILE = DIR_EDITOR + "ntt/editor.html";
    public static final String MANO_FINDER_FILE = "config/finders/mano.element.finder.xml";
    public static final String GWT_FINDER_FILE = "config/finders/gwt.element.finder.xml";
    public static final String TUI_FINDER_FILE = "config/finders/tui.element.finder.xml";
    public static final String CBC_FINDER_FILE = "config/finders/cbc.element.finder.xml";

    /* !END OF REORGANIZED */

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nonnull
    public static String getAppDir() {
        try {
            Class<?> clazz = CermFileSystem.class;
            String pathToJar = clazz.getResource(clazz.getSimpleName() + ".class").getPath().split("!", 2)[0];
            File file = new File(new URI(new URL(pathToJar).toExternalForm())).getParentFile()
                    .getParentFile()
                    .getParentFile();
            return file.getAbsolutePath();
        } catch (Throwable e) {
            File probeFile = new File(".");
            String path;
            try {
                path = URLDecoder.decode(probeFile.getCanonicalPath(), Charsets.UTF_8.name());
            } catch (IOException ex) {
                path = probeFile.getAbsolutePath();
            }
            return path;
        }
    }
}
