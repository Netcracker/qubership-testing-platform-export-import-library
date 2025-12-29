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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

public class DependenceTransformer {

    /**
     * Transform map.
     *
     * @param sourceGraph the source graph
     * @return the map
     */
    public Map<UUID, UUID> transform(Map<UUID, HashSet<UUID>> sourceGraph) {
        Map<UUID, UUID> resultMap = new HashMap<>();
        for (UUID key : sourceGraph.keySet()) {
            HashSet<UUID> values = sourceGraph.get(key);
            if (CollectionUtils.isNotEmpty(values)) {
                for (UUID value : values) {
                    updateMap(resultMap, key, value);
                }
            }
        }
        return resultMap;
    }

    private void updateMap(Map<UUID, UUID> resultMap, UUID key, UUID value) {
        if (key.equals(value)) {
            return;
        }
        if (resultMap.containsKey(key)) {
            UUID value2 = resultMap.get(key);
            updateMap(resultMap, value, value2);
        }
        resultMap.put(key, value);
    }

}
