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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.ntt.dto.Directive;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public enum DirectiveEnum {

    USE(
            UUID.fromString("96c65818-d959-11e9-8a34-2a2ae2dbcce4"),
            "@Use()",
            "@Use(resource_system_name)"
    ),
    WAIT_FOR_TA_RESPONSE_TIMEOUT(
            UUID.fromString("bdf310c0-d959-11e9-8a34-2a2ae2dbcce4"),
            "@WaitForTAResponseTimeout()",
            "@WaitForTAResponseTimeout(digital_seconds)"
    ),
    SECTION(
            UUID.fromString("d98143b0-b7cf-42b0-804b-815e173724d9"),
            "@Section()",
            "@Section(section_name)"
    ),
    PARALLEL_EXECUTION(
            UUID.fromString("9341b417-ae2e-42bd-8410-a08878d418f9"),
            "@ParallelExecution()",
            "@ParallelExecution(number_of_threads)"
    ),
    CONDITION(
            UUID.fromString("da601fc8-ee84-4223-8589-7b17d2c50244"),
            "@Condition()",
            "@Condition(a>1, maxtime=2, attempts=3)"
    ),
    INVERT_RESULT(
            UUID.fromString("a3693ce4-7ac9-415d-82d6-12d1c54d08dc"),
            "@InvertResult",
            "@InvertResult"
    ),
    SKIP_ON_FAIL(
            UUID.fromString("3745c161-ebed-47cc-bcf8-f46abca26cc5"),
            "@skip_on_fail",
            "@skip_on_fail"
    ),
    STOP_ON_FAIL(
            UUID.fromString("30d0db7d-6440-49d1-9d8b-6602c11051ed"),
            "@stop_on_fail",
            "@stop_on_fail"
    ),
    WARN_IF_FAIL(
            UUID.fromString("493ac8e1-74f2-4821-aaa0-e6358ca120d4"),
            "@warn_if_fail",
            "@skip_on_fail"
    ),
    HIDDEN(
            UUID.fromString("c3b7f1ea-8932-11ea-bc55-0242ac130003"),
            "@Hidden",
            "@hide_step"
    );

    private Directive directive;

    private static final Map<UUID, Directive> byIdIndex =
            Maps.newHashMapWithExpectedSize(DirectiveEnum.values().length);
    private static final Map<String, DirectiveEnum> byNameIndex =
            Maps.newHashMapWithExpectedSize(DirectiveEnum.values().length);

    static {
        for (DirectiveEnum dir : DirectiveEnum
                .values()) {
            byIdIndex.put(dir.directive.getId(), dir.directive);
            byNameIndex.put(dir.directive.getName(), dir);
        }
    }

    /**
     * Get name of directive by name.
     */
    public static String getNameById(UUID id) {
        Preconditions.checkNotNull(byIdIndex.get(id), "Directive with ID %id does not exist", id);
        return byIdIndex.get(id).getName();
    }

    /**
     * Get by name.
     */
    public static DirectiveEnum findByKey(String name) {
        Preconditions.checkNotNull(byNameIndex.get(name),
                "Directive with ID %name does not exist", name);
        return byNameIndex.get(name);
    }

    DirectiveEnum(UUID id, String name, String description) {
        this.directive = new Directive(id, name, description);
    }

    public Directive getDirective() {
        return directive;
    }

    /**
     * Return all {@link Directive}.
     *
     * @return list of entities
     */
    public static List<Directive> getAll() {
        List<Directive> listDir = new ArrayList<>();
        for (DirectiveEnum directiveEnum : values()) {
            listDir.add(directiveEnum.getDirective());
        }
        return listDir;
    }

    public String getName() {
        return directive.getName();
    }

    public UUID getId() {
        return directive.getId();
    }
}

