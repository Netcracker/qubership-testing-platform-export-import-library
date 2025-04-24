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

package org.qubership.atp.ei.ntt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

    public static final String ALLOWED_PATH_CHARS_REG = "^\\w+[ +.+\\w+]*$";
    public static final Pattern ALLOWED_PATH_CHARS_PATTERN = Pattern.compile(ALLOWED_PATH_CHARS_REG);

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets reader for file.
     *
     * @param file the file
     * @return the reader for file
     */
    public static BufferedReader getReaderForFile(File file) {
        try {
            InputStream is = org.apache.commons.io.FileUtils.openInputStream(file);
            return new BufferedReader(new InputStreamReader(is, "UTF-8"));

        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    /**
     * Gets safe filename.
     *
     * @param filename the filename
     * @return the safe filename
     */
    public static String getSafeFilename(String filename) {
        String result = filename;
        if (result != null) {
            result = result.replaceAll("[^ a-zA-Z0-9]+", "_").replaceAll("^-+", "");
        }
        return result;
    }

    /**
     * Checks if string is valid for file path (contains only latin chars, numbers, . and spaces).
     *
     * @param data - string for check
     * @return true or false
     */
    public static boolean isValidForFilePath(String data) {
        return ALLOWED_PATH_CHARS_PATTERN.matcher(data).find();
    }

    /**
     * Returns identifier for file path: name or uuid.
     *
     * @param name - object name
     * @param uuid - object id
     * @return if name is valid returns name, otherwise returns uuid.
     */
    public static String getIdentifierForFilePath(String name, UUID uuid) {
        return isValidForFilePath(name) ? name : uuid.toString();
    }
}
