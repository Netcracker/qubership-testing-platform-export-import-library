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

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractTestScenario extends AbstractEntity {

    private List<MetaInfo> metainfo;
    private Type type;

    /**
     * Get metainfo of test scenario. If null return empty list.
     */
    public List<MetaInfo> getMetainfo() {
        if (this.metainfo == null) {
            this.metainfo = new ArrayList<>();
        }
        return this.metainfo;
    }

    /**
     * Add metainfo to test scenario. If null creates empty list.
     */
    public void addMetaInfo(MetaInfo metaInfo) {
        if (this.metainfo == null) {
            this.metainfo = new ArrayList<>();
        }
        this.metainfo.add(metaInfo);
    }

    public enum Type {
        TESTSCENARIO, COMPOUND
    }
}
