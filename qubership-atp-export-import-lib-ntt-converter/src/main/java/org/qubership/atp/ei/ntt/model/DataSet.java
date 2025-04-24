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

package org.qubership.atp.ei.ntt.model;

import java.util.List;

/**
 * The interface Data set.
 *
 * @author Denis Arychkov
 * @since 20.11.2013
 */
public interface DataSet extends ContainerTreeNode {

    List<ContextVariable> getVariables();

    List<String> getVariableNames();

    ContextVariable getVariableByName(String name);

    void setVariables(List<ContextVariable> variables);

    String getId();

    void setId(String uuid);

    Project getProject();

    void setProject(Project project);

}
