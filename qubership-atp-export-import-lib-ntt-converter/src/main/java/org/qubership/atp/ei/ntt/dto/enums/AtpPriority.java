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

import java.util.Arrays;

public enum AtpPriority implements DictionaryItem {
    BLOCKER("Blocker"),
    CRITICAL("Critical"),
    MAJOR("Major"),
    NORMAL("Normal"),
    LOW("Low");

    private final String caption;

    AtpPriority(String caption) {
        this.caption = caption;
    }

    /**
     * Get {@link AtpPriority}.
     *
     * @param caption for searching
     * @return {@link AtpPriority}
     */
    public static AtpPriority getByCaption(String caption) {
        return Arrays.stream(values())
                .filter(priority -> priority.getCaption().equals(caption))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No such priority exists with this caption:" + caption)
                );
    }

    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getCaption() {
        return caption;
    }
}
