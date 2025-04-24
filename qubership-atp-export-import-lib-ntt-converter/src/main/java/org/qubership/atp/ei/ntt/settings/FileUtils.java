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

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;

/**
 * TODO Make summary for this class.
 */
public final class FileUtils {

    private static final Logger log = Logger.getLogger(FileUtils.class);

    private static void trace(@Nonnull final String message) {
        if (log.isTraceEnabled()) {
            log.trace(message);
        }
    }

    /**
     * Reads content of a plain text file.
     *
     * @param file Plain text file.
     * @return text Content of the file (plain text, UTF-8).
     */
    @Nonnull
    public static String readTextFile(@Nonnull final File file) {
        String path = file.getAbsolutePath();
        try {
            return IOUtils.toString(new FileInputStream(path), Charsets.UTF_8);
        } catch (FileNotFoundException ex) {
            log.error(String.format("Cannot find file [%s]", path), ex);
        } catch (IOException ex) {
            log.error(String.format("Cannot read file [%s]", path), ex);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Creates a new file and fills it with specified text.
     *
     * @param text     Text to store.
     * @param fileName Name of file.
     */
    public static void writeTextFile(@Nonnull final String text, @Nonnull final String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile(), Charsets.UTF_8.name());
            out.print(text);
            out.close();
        } catch (IOException e) {
            log.error(String.format("Cannot write text file [%s]", fileName), e);
        }
    }

    /**
     * Returns canonical path to the specified file.
     *
     * @param file Target file.
     * @return Canonical path.
     */
    @Nonnull
    public static String getPathToFile(@Nonnull final File file) {
        try {
            String canonicalPath = file.getCanonicalPath();
            return canonicalPath.replace(File.separator, "/");
        } catch (IOException e) {
            log.error(String.format("Can not get canonical path for the [%s]", file.getPath()), e);
            return file.getAbsolutePath();
        }
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public static String getRelatedPathToFile(@Nonnull final File file) {
        return getRelatedPathToFile(CermFileSystem.APP_DIR, file);
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nonnull
    public static String getRelatedPathToFile(@Nonnull final File appDir, @Nonnull final File file) {
        try {
            String filePath = file.getCanonicalPath();
            String appDirPath = appDir.getCanonicalPath();
            boolean isSubPath = (filePath + File.separator).contains(appDirPath + File.separator);
            return isSubPath
                    ? filePath.replace(appDirPath, ".").replace(File.separator, "/")
                    : filePath;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can not do [%s] path to relate to [%s]", file, appDir), e);
        }
    }

    /**
     * Deletes files or directories with name including the containsLine in the specified directory.
     * @param rootDirectory Directory in which the file is searched.
     * @param containsLine String that should be in the file name.
     */
    public static void deleteFileOrDirectoryWithSpecificName(String rootDirectory, String containsLine) {
        File directory = new File(rootDirectory);
        String[] files = directory.list((dir, name) -> name.contains(containsLine));
        if (files != null) {
            log.info("Start deleting files " + Arrays.toString(files));
            for (String file : files) {
                try {
                    deleteDirectory(new File(Paths.get(rootDirectory, file).toUri()));
                } catch (IOException e) {
                    log.error("Failed delete file: " + file);
                }
            }
        } else {
            log.error("File with name contains \"" + containsLine + "\" not found.");
        }
    }

    /**
     * Returns true if specified file is related.
     *
     * @param filePath Path for check.
     * @return True if the path for check is relative for app dir.
     */
    public static boolean isPathRelated(@Nonnull final String filePath) {
        try {
            File file = new File(filePath);
            return !file.isAbsolute() && file.getCanonicalPath().startsWith(CermFileSystem.APP_DIR.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(
                    String.format("Can not check if [%s] path relates to application directory", filePath), e);
        }
    }

    /**
     * Returns absolute path from relative path with application directory as base.
     *
     * @param relatedPath Relative path.
     * @return Absolute path.
     */
    @Nonnull
    public static String getAbsolutePath(@Nonnull final String relatedPath) {
        File concat = new File(CermFileSystem.APP_DIR, relatedPath);
        try {
            return concat.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can not get canonical path of [%s]", concat), e);
        }
    }

    /**
     * Returns parent folder path in canonical form.
     *
     * @param file Base file.
     * @return Parent folder path.
     */
    @Nonnull
    public static String getParentFolder(@Nonnull final File file) {
        try {
            return URLDecoder.decode(file.getParentFile().getCanonicalPath(), Charsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot get canonical path for [%s]", file), e);
        }
    }

    /**
     * Creates new file if it is not null and not exists yet.
     *
     * @param file File for creation.
     */
    public static void createIfNotExists(@Nonnull final File file) {
        if (file == null || file.exists()) {
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.error(String.format("Cannot create new file [%s]", file.getName()), e);
        }
    }

    /**
     * Reads lines from plain text file (UTF-8).
     *
     * @param file Target file.
     * @return List of lines.
     */
    @Nonnull
    public static List<String> readLines(@Nonnull final File file) {
        if (file == null || !file.exists()) {
            return Collections.emptyList();
        }
        try {
            return org.apache.commons.io.FileUtils.readLines(file, Charsets.UTF_8);
        } catch (IOException e) {
            log.error(String.format("Cannot read lines from file [%s]", file.getAbsolutePath()), e);
        }
        return Collections.emptyList();
    }

    private static void createFolders(@Nonnull final File file) {
        if (file == null) {
            return;
        }
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Writes Serializable object to file.
     *
     * @param obj      Object for serialization.
     * @param fileName Target filename.
     */
    public static void storeObject(@Nonnull final Serializable obj, @Nonnull final String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            createFolders(file);
        } else {
            file.delete();
        }
        trace(String.format("Try to write object [%s] to file [%s]", obj, file.getPath()));
        try (ObjectOutputStream oStream = new ObjectOutputStream(new FileOutputStream(file))) {
            oStream.writeObject(obj);
            oStream.flush();
            trace(String.format("Object [%s] was successfully write to file [%s]", obj, file.getPath()));
        } catch (FileNotFoundException e) {
            log.error(String.format("Cannot find file [%s]", file.getAbsolutePath()), e);
        } catch (IOException e) {
            log.error(String.format("Cannot store object in [%s]", file.getAbsolutePath()), e);
        }
    }

    /**
     * Loads object from specified filename.
     *
     * @param fileName Target file.
     * @return Serializable instance.
     */
    @Nullable
    public static Serializable loadObject(@Nonnull final String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.error(String.format("The file [%s] wasn't found. Please check the name.", file.getPath()));
            return null;
        }
        trace(String.format("Try to read object from file [%s]", file.getPath()));
        Object obj = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            obj = ois.readObject();
            trace(String.format("Object [%s] was successfully read from file [%s]", obj, file.getPath()));
        } catch (ClassNotFoundException e) {
            log.error("ClassNotFoundException", e);
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return (Serializable) obj;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nullable
    public static BufferedReader createReader(@Nonnull final File file) {
        try {
            InputStream inStream = org.apache.commons.io.FileUtils.openInputStream(file);
            return new BufferedReader(new InputStreamReader(inStream, Charsets.UTF_8));
        } catch (Exception e) {
            log.error(String.format("Cannot create reader stream for [%s]", file.getAbsolutePath()), e);
        }
        return null;
    }

    /**
     * Checks presence of target extension and creates file with it.
     *
     * @param absoluteFilePath Target file path (contains or not needed extension).
     * @param extension        Needed extension.
     * @return Created file.
     */
    @Nonnull
    public static File createFileWithExtension(@Nonnull final String absoluteFilePath,
                                               @Nonnull final String extension) {
        String projectAbsolutePath = absoluteFilePath;
        if (!StringUtils.endsWithIgnoreCase(projectAbsolutePath, extension)) {
            projectAbsolutePath = projectAbsolutePath.concat(extension);
        }
        File newFile = new File(projectAbsolutePath);
        createIfNotExists(newFile);
        return newFile;
    }
}
