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

import javax.annotation.Nonnull;

/**
 * The interface Workspace.
 *
 * @author Denis Arychkov
 * @version 1.4.4
 * @since 20.11.2013
 */
public interface Workspace extends GenericItem {

    @Nonnull
    List<Project> getProjects();

    void setProjects(@Nonnull List<Project> projects);

    @Nonnull
    List<Project> getTemplateProjects();

    void setTemplateProjects(@Nonnull List<Project> templateProjects);

    Scope getScope();

    void setScope(Scope scope);

}
