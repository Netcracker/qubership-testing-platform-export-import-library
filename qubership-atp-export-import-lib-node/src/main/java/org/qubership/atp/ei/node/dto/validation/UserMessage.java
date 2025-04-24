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

package org.qubership.atp.ei.node.dto.validation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "CONFIRMATION", value = UserMessage.class),
        @JsonSubTypes.Type(name = "CHOOSE_ANOTHER_OBJECT", value = ChoseAnotherObjectUserMessage.class)
})
@AllArgsConstructor
public class UserMessage {
    private String message;
    private MessageType messageType;
    private String taskId;

    public UserMessage() {
        this.messageType = MessageType.CONFIRMATION;
    }

    public UserMessage(String message) {
        this.message = message;
        this.messageType = MessageType.CONFIRMATION;
    }

    protected UserMessage(String message, MessageType messageType) {
        this.message = message;
        this.messageType = messageType;
    }
}
