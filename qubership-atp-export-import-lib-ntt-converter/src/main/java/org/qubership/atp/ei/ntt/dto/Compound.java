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

package org.qubership.atp.ei.ntt.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Compound extends AbstractTestScenario implements Cloneable {

    private List<ActionParameter> parameters;
    private boolean deprecated;
    private String comment;
    private UUID projectUuid;
    private UUID qaDslLibraryId;

    @Override
    public Compound clone() {
        Compound compound;
        try {
            compound = (Compound) super.clone();
        } catch (CloneNotSupportedException e) {
            compound = new Compound();
            compound.setName(this.getName());
            compound.setDeprecated(this.isDeprecated());
            compound.setComment(this.getComment());
        }
        compound.setUuid(UUID.randomUUID());
        compound.setProjectUuid(this.getProjectUuid());
        compound.setParameters(this.getParameters());
        compound.setQaDslLibraryId(this.getQaDslLibraryId());
        compound.setMetainfo(this.getMetainfo());
        compound.setType(this.getType());

        return compound;
    }
}
