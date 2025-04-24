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

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChoseAnotherObjectUserMessage extends UserMessage {

    private String objectType;
    private List<ObjectIdentifier> objects;
    private List<ObjectIdentifier> suggestedValues;

    /**
     * Instantiates a new Chose another object user message.
     *
     * @param message         the message
     * @param objectType      the object type
     * @param objects         the objects
     * @param suggestedValues the suggested values
     */
    public ChoseAnotherObjectUserMessage(String message, String objectType,
                                         List<ObjectIdentifier> objects,
                                         List<ObjectIdentifier> suggestedValues) {
        super(message, MessageType.CHOOSE_ANOTHER_OBJECT);
        this.objectType = objectType;
        this.objects = objects;
        this.suggestedValues = suggestedValues;
    }

    /**
     * Instantiates a new Chose another object user message.
     */
    public ChoseAnotherObjectUserMessage() {
        super(null, MessageType.CHOOSE_ANOTHER_OBJECT);
    }
}
