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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaInfo {
    private Type type;
    private UUID stepId;
    private List<ActionParameter> parameters;
    private String otherTextValue;

    /**
     * Add param to ActionParameters.
     *
     * @param param for adding
     */
    public void addParameter(ActionParameter param) {
        if (Objects.isNull(parameters)) {
            parameters = new ArrayList<>();
        }
        this.parameters.add(param);
    }

    public enum Type {
        COMPOUND,
        ACTION,
        FLAG,
        DIRECTIVE,
        OTHER
    }

    /**
     * Just getter.
     */
    public List<ActionParameter> getParameters() {
        if (Objects.isNull(parameters)) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }
}
