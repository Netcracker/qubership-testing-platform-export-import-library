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

package org.qubership.atp.ei.ntt.dto.enums;

import static org.qubership.atp.ei.ntt.dto.enums.FlagEntity.Type.COLLECTION;
import static org.qubership.atp.ei.ntt.dto.enums.FlagEntity.Type.EXECUTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Flags {

    INVERT_RESULT(UUID.fromString("5e482fa3-1f9b-42eb-52b8-789591342c78"), "Invert Result", EXECUTION),
    STOP_ON_FAIL(UUID.fromString("3e482af5-19fb-4e2b-82b5-879519342c68"), "Stop on fail", EXECUTION),
    SKIP_IF_DEPENDENCIES_FAIL(UUID.fromString("8596d3da-0226-4df8-9877-0a05f7784586"),
            "Skip if dependencies fail", EXECUTION),
    SKIP(UUID.fromString("5d9b8af2-9c21-4750-afda-b605f52cacec"), "Skip", EXECUTION),
    TERMINATE_IF_FAIL(UUID.fromString("547c84b1-4111-457d-bc7d-76e3a2a9d157"),
            "Terminate if fail", EXECUTION),
    COLLECT_LOGS(UUID.fromString("b7025ffb-fa42-4c70-997e-715bc8324946"), "Collect logs", COLLECTION),
    COLLECT_LOGS_ON_BLOCKED(UUID.fromString("6be63860-70b0-4643-b55b-af46c2d0295d"),
            "Collect logs on blocked", COLLECTION),
    COLLECT_LOGS_ON_FAIL(UUID.fromString("c9b450db-1799-4911-b804-25f4e808ef89"),
            "Collect logs on fail", COLLECTION),
    COLLECT_LOGS_ON_SKIPPED(UUID.fromString("adf75cc8-0dc6-4776-803f-8ab314c17fce"),
            "Collect logs on skipped", COLLECTION),
    COLLECT_LOGS_ON_WARNING(UUID.fromString("8a9db7a6-6ba6-4501-b4d3-9f09f8719afd"),
            "Collect logs on warning", COLLECTION);

    private static final Map<UUID, Flags>
            byIdIndex = Maps.newHashMapWithExpectedSize(Flags.values().length);

    static {
        for (Flags flag : Flags.values()) {
            byIdIndex.put(flag.getId(), flag);
        }
    }

    private FlagEntity flagEntity;

    Flags(UUID id, String name, FlagEntity.Type type) {
        this.flagEntity = new FlagEntity(id, name, type);
    }

    /**
     * Return all {@link FlagEntity}.
     *
     * @return list of entities
     */
    public static List<FlagEntity> getAll() {
        List<FlagEntity> flagEntityList = new ArrayList<>();
        for (Flags flags : values()) {
            flagEntityList.add(flags.getFlagEntity());
        }
        return flagEntityList;
    }

    /**
     * Get flag by id.
     */
    public static Flags getById(UUID id) {
        Preconditions.checkNotNull(byIdIndex.get(id), "Flag with ID %id does not exist", id);
        return byIdIndex.get(id);
    }

    /**
     * Get flags by ids.
     */
    public static List<Flags> getByIds(List<UUID> ids) {
        if (Objects.isNull(ids) || ids.size() == 0) {
            return null;
        }
        List<Flags> flags = new ArrayList<>();
        ids.forEach(id -> {
            Preconditions.checkNotNull(byIdIndex.get(id), "Flag with ID %id does not exist", id);
            flags.add(byIdIndex.get(id));
        });
        return flags;
    }

    /**
     * Get flag ids by flags value.
     *
     * @param flags input flags
     * @return list of ids
     */
    public static List<UUID> getIdByValue(List<Flags> flags) {
        if (Objects.isNull(flags) || flags.size() == 0) {
            return null;
        }
        return flags.stream()
                .flatMap(flag -> getKeysByValue(byIdIndex, flag)
                        .stream())
                .collect(Collectors.toList());
    }

    private static <V1, V2> Set<V1> getKeysByValue(Map<V1, V2> map, V2 value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Find entities by type.
     *
     * @param type for find
     * @return found entities
     */
    public static List<Flags> getByType(FlagEntity.Type type) {
        List<Flags> flagsList = new ArrayList<>();
        for (Flags flags : Flags.values()) {
            if (flags.getFlagEntity().getFlagType().equals(type)) {
                flagsList.add(flags);
            }
        }
        return flagsList;
    }

    public FlagEntity getFlagEntity() {
        return flagEntity;
    }

    public String getName() {
        return flagEntity.getName();
    }

    public UUID getId() {
        return flagEntity.getId();
    }

    @Override
    public String toString() {
        return "Flags{"
                + "flagEntity=" + flagEntity
                + '}';
    }
}
