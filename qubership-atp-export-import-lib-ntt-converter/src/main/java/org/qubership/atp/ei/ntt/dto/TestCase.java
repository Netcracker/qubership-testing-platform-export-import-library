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
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.qubership.atp.ei.ntt.dto.enums.AtpPriority;
import org.qubership.atp.ei.ntt.dto.enums.AtpTestCaseStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public class TestCase extends AbstractEntity {

    private UUID projectUuid;
    private UUID testPlanUuid;
    private UUID testScenarioUuid;
    private UUID groupId;
    private UUID datasetUuid;
    private UUID datasetStorageUuid;
    private UUID lastRun;
    private String lastRunStatus;

    private String ticketUrl;
    private AtpPriority priority;
    private AtpTestCaseStatus status;
    private UserInfo assignee;

    private Long createDate;
    private UserInfo createdBy;
    private Long lastModifyDate;
    private UserInfo lastModifiedBy;

    private List<TestCaseDependency> dependsOn;
    private List<TestCaseFlags> flags;
    private List<UUID> labelIds;
    private List<TestCaseOrder> order;

    public List<UUID> getLabelIds() {
        return isNull(labelIds) ? new ArrayList<>() : labelIds;
    }

    public List<TestCaseFlags> getFlags() {
        return isNull(flags) ? new ArrayList<>() : flags;
    }

    public List<TestCaseOrder> getOrder() {
        return isNull(order) ? new ArrayList<>() : order;
    }
}
