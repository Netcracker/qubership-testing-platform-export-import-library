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

package org.qubership.atp.ei.ntt.model.enums;

/**
 * The enum Scope section type.
 *
 * @author Boris Kuznetsov
 * @version 1.4.4
 * @since 16.12.2015.
 */
public enum ScopeSectionType {

    PREREQUISITES("Prerequisites"),
    ACTIONS("Actions"),
    VALIDATION("Validation");

    private String typeName;

    ScopeSectionType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }

    /**
     * Value of ignore case scope section type.
     *
     * @param value the value
     * @return the scope section type
     */
    public static ScopeSectionType valueOfIgnoreCase(String value) {
        for (ScopeSectionType sectionType : ScopeSectionType.values()) {
            if (sectionType.toString().equalsIgnoreCase(value)) {
                return sectionType;
            }
        }
        return null;
    }

    public boolean isFirst() {
        return ordinal() == 0;
    }

    public boolean isLast() {
        return ordinal() == values().length - 1;
    }

    public ScopeSectionType previous() {
        return ordinal() - 1 < 0 ? values()[0] : values()[ordinal() - 1];
    }

    public ScopeSectionType next() {
        return ordinal() + 1 >= values().length ? values()[ordinal()] : values()[ordinal() + 1];
    }
}
