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

package org.qubership.atp.ei.ntt;

import org.qubership.atp.ei.ntt.settings.CermFileSystem;

public interface Constants {
    String EXPORT_PROJECTS_FOLDER_NAME = "AT_configuration";
    String EXPORT_FILES_FOLDER_NAME = "files";

    String NTT_ACTION_SWITCH_TO_SERVER = "Switch to \"%s\" server";

    String PATH_TO_FILES = "./" + Constants.EXPORT_FILES_FOLDER_NAME + "/";
    String NTT_ACTION_UPLOAD_FILE = "Upload file \"" + PATH_TO_FILES + "%s\"";
    String NTT_ACTION_UPLOAD_SQL_FILE = "Execute SQL \"" + PATH_TO_FILES + "%s\"";

    String TXT_DATASET_EXTENTION = ".cds";
    String TXT_PROJECT_EXTENTION = ".cpr";
    String TXT_TEMPLATE_PROJECT_EXTENTION = ".ctpr";
    String TXT_TEMPLATE_DATASET_EXTENTION = ".ctds";

    String DEFAULT_FILE_EXT = ".xml";
    String TXT_PROJECT = "txt";

    /* Indent */
    String CASE_INDENT = "    ";
    String DIR_APPLICATION = CermFileSystem.DIR_APPLICATION;

    interface Flags {
        String STOP_ON_FAIL_FLAG_NAME = "stop on fail";
        String TERMINATE_IF_FAIL_FLAG_NAME = "terminate if fail";
        String SKIP_ON_FAIL_FLAG_NAME = "skip on fail";
        String SKIP_IF_DEPENDENCY_FAILED_FLAG_NAME = "skip if dependencies fail";
        String INVERT_RESULT_FLAG_NAME = "Invert Result";
        String WARN_IF_FAIL_FLAG_NAME = "warn if fail";
    }
}
