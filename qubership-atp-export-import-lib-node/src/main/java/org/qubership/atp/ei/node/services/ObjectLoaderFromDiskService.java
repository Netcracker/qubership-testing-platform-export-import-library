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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ObjectLoaderFromDiskService {

    protected final ObjectMapper objectMapper;
    private Map<Class, KeyDeserializer> additionalKeyDeserializer = Maps.newHashMap();

    private Map<Class, KeyDeserializer> getAdditionalKeyDeserializer() {
        return additionalKeyDeserializer;
    }

    public void setAdditionalKeyDeserializer(Map<Class, KeyDeserializer> additionalKeyDeserializer) {
        this.additionalKeyDeserializer = additionalKeyDeserializer;
    }

    /**
     * Instantiates a new Object loader from disk service.
     */
    public ObjectLoaderFromDiskService() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Load object t.
     *
     * @param <T>     the type parameter
     * @param id      the id
     * @param clazz   the clazz
     * @param workDir the work dir
     * @return the t
     */
    public <T extends Object> T loadObject(UUID id, Class<T> clazz, Path workDir) {
        log.debug("start loadObject(id: {}, clazz: {})", id, clazz);
        Path dirWithObjects = workDir.resolve(clazz.getSimpleName());
        Path res = findFileOnDisk(dirWithObjects, id);
        return loadFileAsObject(res, clazz);
    }

    /**
     * Find object on disk t.
     *
     * @param <T>     the type parameter
     * @param workDir the work dir
     * @param id      the id
     * @param clazz   the clazz
     * @return the t
     */
    public <T extends Object> T findObjectOnDisk(Path workDir, UUID id, Class<T> clazz) {
        log.debug("start findFileOnDisk(workDir: {}, id: {}, clazz: {})", workDir, id, clazz);
        Path dirWithObjects = workDir.resolve(clazz.getSimpleName());
        Path path = findFileOnDisk(dirWithObjects, id);
        return loadFileAsObject(path, clazz);
    }

    /**
     * Find file on disk path.
     *
     * @param workDir the work dir
     * @param id      the id
     * @return the path
     */
    public Path findFileOnDisk(Path workDir, UUID id) {
        log.debug("start findFileOnDisk(workDir: {}, id: {})", workDir, id);
        String fileName = id.toString();
        Path res = null;
        try (Stream<Path> result = Files.find(workDir, 10,
                (path, basicFileAttributes) -> path.getFileName().toString().contains(fileName))) {
            Optional<Path> file = result.findFirst();
            if (!file.isPresent()) {
                log.info("Cannot find file with id {}", fileName);
            } else {
                res = file.get();
            }
        } catch (IOException e) {
            log.error("Cannot read directory {}", workDir, e);
        }

        log.debug("end findFileOnDisk(.., ..): {}", res);
        return res;
    }

    /**
     * Load file as object t.
     *
     * @param <T>   the type parameter
     * @param file  the file
     * @param clazz the clazz
     * @return the t
     */
    public <T> T loadFileAsObject(Path file, Class<T> clazz) {
        return loadFileAsObjectWithReplacementMap(file, clazz, new HashMap<>(), true, false);
    }

    /**
     * Load file as object t.
     *
     * @param <T>            the type parameter
     * @param file           the file
     * @param clazz          the clazz
     * @param replacementMap the replacement map
     * @return the t
     */
    public <T> T loadFileAsObjectWithReplacementMap(Path file, Class<T> clazz, Map<UUID, UUID> replacementMap) {
        return loadFileAsObjectWithReplacementMap(file, clazz, replacementMap, false, false);
    }

    /**
     * Load file as object with replacement map t.
     *
     * @param <T>              the type parameter
     * @param file             the file
     * @param clazz            the clazz
     * @param replacementMap   the replacement map
     * @param checkStringForId the check string for id
     * @return the t
     */
    public <T> T loadFileAsObjectWithReplacementMap(Path file, Class<T> clazz, Map<UUID, UUID> replacementMap,
                                                    boolean checkStringForId) {
        return loadFileAsObjectWithReplacementMap(file, clazz, replacementMap, false, checkStringForId);
    }

    /**
     * Load file as object t.
     *
     * @param <T>               the type parameter
     * @param file              the file
     * @param clazz             the clazz
     * @param replacementMap    the replacement map
     * @param saveOriginalValue the save original value
     * @return the t
     */
    public <T> T loadFileAsObjectWithReplacementMap(Path file, Class<T> clazz, Map<UUID, UUID> replacementMap,
                                                    boolean saveOriginalValue, boolean checkStringForId) {
        try {
            return loadFileAsObjectWithReplacementMap(file, clazz, replacementMap, saveOriginalValue, checkStringForId,
                    false);
        } catch (Exception e) {
            log.error("Cannot read file {}. class {}", file, clazz, e);
            return null;
        }
    }

    /**
     * Load file as object t.
     *
     * @param <T>               the type parameter
     * @param file              the file
     * @param clazz             the clazz
     * @param replacementMap    the replacement map
     * @param saveOriginalValue the save original value
     * @return the t
     */
    public <T> T loadFileAsObjectWithReplacementMap(Path file, Class<T> clazz, Map<UUID, UUID> replacementMap,
                                                    boolean saveOriginalValue, boolean checkStringForId,
                                                    boolean throwException)
            throws Exception {
        log.debug("start loadFileAsObject(id: {}, clazz: {})", file, clazz);

        ObjectMapper localObjectMapper = objectMapper;
        if (MapUtils.isNotEmpty(replacementMap)) {
            localObjectMapper = new ObjectMapperWithReplacementMap(objectMapper, replacementMap, saveOriginalValue,
                    checkStringForId, additionalKeyDeserializer);
        }

        T result = null;
        try (InputStream in = Files.newInputStream(file)) {
            result = localObjectMapper.readValue(in, clazz);
        } catch (Exception e) {
            log.error("Cannot read file {}. class {}", file, clazz, e);
            if (throwException) {
                throw e;
            }
        }
        log.debug("end (loadFileAsObject: {}, clazz: {})", file, clazz);
        return result;
    }

    /**
     * Load file as object with replacement map t.
     *
     * @param <T>              the type parameter
     * @param file             the file
     * @param clazz            the clazz
     * @param replacementMap   the replacement map
     * @param checkStringForId the check string for id
     * @return the t
     * @throws Exception The exception is sent back to EI-service.
     */
    public <T> T loadFileAsObjectWithReplacementMapThrowException(Path file, Class<T> clazz,
                                                                  Map<UUID, UUID> replacementMap,
                                                                  boolean checkStringForId) throws Exception {
        return loadFileAsObjectWithReplacementMap(file, clazz, replacementMap, false, checkStringForId,
                true);
    }

    private static class ObjectMapperWithReplacementMap extends ObjectMapper {
        public ObjectMapperWithReplacementMap(ObjectMapper src, Map<UUID, UUID> replacementMap,
                                              boolean saveOriginalValue, boolean checkStringForId,
                                              Map<Class, KeyDeserializer> additionalKeyDeserializer) {
            super(src);
            SimpleModule module = new SimpleModule() {
                @Override
                public void setupModule(SetupContext context) {
                    super.setupModule(context);
                    context.addBeanDeserializerModifier(new CustomizedBeanDeserializerModifier());
                }
            };
            if (!additionalKeyDeserializer.isEmpty()) {
                for (Class clazz : additionalKeyDeserializer.keySet()) {
                    module.addKeyDeserializer(clazz, additionalKeyDeserializer.get(clazz));
                }
            }

            UUIDDeserializer uuidDeserializer = new UUIDDeserializer() {
                @Override
                public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    UUID id = super.deserialize(p, ctxt);
                    return replacementMap.getOrDefault(id, saveOriginalValue ? id : null);
                }
            };
            module.addDeserializer(UUID.class, uuidDeserializer);

            if (checkStringForId) {
                module.addDeserializer(String.class, new StringDeserializer() {
                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        Object id;
                        if (p.getValueAsString().length() == 36) {
                            try {
                                id = uuidDeserializer.deserialize(p, ctxt);
                            } catch (Exception e) {
                                id = super.deserialize(p, ctxt);
                            }
                        } else {
                            id = super.deserialize(p, ctxt);
                        }


                        return id == null ? null : id.toString();
                    }
                });
            }

            registerModule(module);
        }
    }

    // thanks to https://stackoverflow.com/questions/33553553/
    // jackson-2-6-3-deserialization-avoid-literal-null-added-to-collection
    private static class CustomizedCollectionDeserializer extends CollectionDeserializer {

        public CustomizedCollectionDeserializer(CollectionDeserializer src) {
            super(src);
        }

        private static final long serialVersionUID = 1L;

        @Override
        public Collection<Object> deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            Collection<Object> col = super.deserialize(jp, ctxt);
            if (CollectionUtils.isNotEmpty(col)) {
                col.removeIf(Objects::isNull);
            }

            return col;
        }

        @Override
        public CollectionDeserializer createContextual(DeserializationContext ctxt,
                                                       BeanProperty property) throws JsonMappingException {
            return new CustomizedCollectionDeserializer(super.createContextual(ctxt, property));
        }
    }

    private static class CustomizedBeanDeserializerModifier extends BeanDeserializerModifier {
        @Override
        public JsonDeserializer<?> modifyCollectionDeserializer(
                DeserializationConfig config, CollectionType type,
                BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            if (deserializer instanceof CollectionDeserializer) {
                return new CustomizedCollectionDeserializer((CollectionDeserializer) deserializer);
            } else {
                return super.modifyCollectionDeserializer(config, type, beanDesc, deserializer);
            }
        }
    }

    /**
     * Gets list of objects by path and class.
     *
     * @param workDir the work dir
     * @param clazz   the clazz
     * @return Map of UUID, Path pairs.
     */
    public Map<UUID, Path> getListOfObjects(Path workDir, Class clazz) {
        return getListOfObjects(workDir, clazz, (UUID) null);
    }

    /**
     * Gets list of objects by path, class and parent id.
     *
     * @param workDir the work dir
     * @param clazz   the clazz
     * @return Map of UUID, Path pairs.
     */
    public Map<UUID, Path> getListOfObjects(Path workDir, Class clazz, UUID parentId) {
        return getListOfObjects(workDir, clazz.getSimpleName(), parentId);
    }

    /**
     * Gets list of objects by path and folder name.
     *
     * @param workDir    the work dir
     * @param folderName the folder name
     * @return Map of UUID, Path pairs.
     */
    public Map<UUID, Path> getListOfObjects(Path workDir, String folderName) {
        return getListOfObjects(workDir, folderName, null);
    }

    /**
     * Gets list of objects by path, folder name and parent id.
     *
     * @param workDir    the work dir
     * @param folderName the folder name
     * @return Map of UUID, Path pairs.
     */
    public Map<UUID, Path> getListOfObjects(Path workDir, String folderName, UUID parentId) {
        Path dirWithObjects = workDir.resolve(folderName);
        if (parentId != null) {
            dirWithObjects = dirWithObjects.resolve(parentId.toString());
        }
        return getListOfObjectIdByFolder(dirWithObjects);
    }

    /**
     * Gets list of objects by path, class and list of parent id.
     *
     * @param workDir   the work dir
     * @param clazz     the clazz
     * @param parentIds the parent ids
     * @return Map of UUID, Path pairs.
     */
    public Map<UUID, Path> getListOfObjects(Path workDir, Class clazz, List<UUID> parentIds) {
        log.debug("start getListOfObjects(workDir: {}, clazz: {}, parentIds: {})", workDir, clazz, parentIds);
        Map<UUID, Path> result = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(parentIds)) {
            result.putAll(parentIds
                    .stream()
                    .flatMap(
                            parentId -> getListOfObjects(workDir, clazz, parentId)
                                    .entrySet()
                                    .stream()
                    )
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        } else {
            result.putAll(getListOfObjects(workDir, clazz));
        }
        log.debug("end getListOfObjects(workDir: {}, clazz: {}, parentIds: {}) return {}", workDir, clazz, parentIds,
                result);
        return result;
    }

    private Map<UUID, Path> getListOfObjectIdByFolder(Path dirWithObjects) {
        log.debug("start getListOfObjectIdByFolder(dirWithObjects: {})", dirWithObjects);
        Map<UUID, Path> res = new LinkedHashMap<>();
        try (Stream<Path> result = Files.find(dirWithObjects, 5,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())) {
            result.forEach(pathToFile -> {
                log.debug("Path to file: {}", pathToFile);
                UUID objectId;
                try {
                    objectId = UUID.fromString(pathToFile.getFileName().toString().split("\\.")[0]);
                } catch (IllegalArgumentException e) {
                    log.warn("Can't get uuid from filename.", e);
                    return;
                }
                res.put(objectId, pathToFile);
            });
        } catch (Exception e) {
            log.error("Cannot find dir {}", dirWithObjects, e);
        }
        log.debug("end getListOfObjectIdByFolder(): {}", res);
        return res;
    }
}
