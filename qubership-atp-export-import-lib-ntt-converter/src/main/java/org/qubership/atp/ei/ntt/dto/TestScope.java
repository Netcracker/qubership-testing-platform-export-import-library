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

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;
import java.util.UUID;

import org.qubership.atp.ei.ntt.dto.enums.Flags;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public class TestScope extends AbstractEntity {

    private UUID projectUuid;
    private UUID testPlanUuid;
    private String[] solutionBuild;
    private String[] systemUnderTestHost;
    private UUID environmentUuid;
    private int numberOfThreshold;
    private UUID taToolsUuid;
    private List<Flags> flags;
    private UUID groupUuid;

    private List<UUID> prerequisitesCases;
    private List<UUID> executionCases;
    private List<UUID> validationCases;
}
