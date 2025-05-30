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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.qubership.atp.ei.node.dto.validation.UserMessage;

import lombok.Data;

@Data
public class ExportImportReportRequest {
    private String status;
    private String fileId;
    private String error;
    private List<String> messages;
    private List<UserMessage> details;
    private Map<UUID, UUID> replacementMap;
}
