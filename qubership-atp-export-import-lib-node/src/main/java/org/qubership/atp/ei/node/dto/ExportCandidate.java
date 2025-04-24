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

package org.qubership.atp.ei.node.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(as = ExportCandidate.class)
public class ExportCandidate extends AbstractExportCandidate {
    private UUID id;
    private String name;
    private String groupId;

    private Boolean isLazy;

    private List<AbstractExportCandidate> children = new ArrayList<>();
    private String candidateName;

    /**
     * Instantiates a new Export candidate.
     *
     * @param id      the id
     * @param name    the name
     * @param groupId the group id
     */
    public ExportCandidate(UUID id, String name, Boolean isLazy, String groupId,
                           String candidateName) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.isLazy = isLazy;
        this.candidateName = candidateName;
    }

    /**
     * Instantiates a new Export candidate.
     *
     * @param id      the id
     * @param name    the name
     * @param groupId the group id
     */
    public ExportCandidate(UUID id, String name, Boolean isLazy, String groupId) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.isLazy = isLazy;
    }
}
