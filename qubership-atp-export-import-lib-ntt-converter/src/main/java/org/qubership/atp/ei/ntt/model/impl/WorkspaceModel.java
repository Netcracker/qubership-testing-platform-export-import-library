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

package org.qubership.atp.ei.ntt.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.model.Workspace;

public class WorkspaceModel extends AbstractGenericItem implements Workspace {

    private static final ThreadLocal<WorkspaceModel> WORKSPACE_MODEL = new ThreadLocal<>();

    private List<Project> projects = new ArrayList<>();
    private List<Project> templateProjects = new ArrayList<>();
    private Scope scope = new ScopeModel();


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static WorkspaceModel getInstance() {
        WorkspaceModel workspaceModel = WORKSPACE_MODEL.get();
        if (workspaceModel == null) {
            workspaceModel = new WorkspaceModel();
            WORKSPACE_MODEL.set(workspaceModel);
        }
        return workspaceModel;
    }

    public static void reset() {
        WORKSPACE_MODEL.remove();
    }


    private WorkspaceModel() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<Project> getProjects() {
        return projects;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setProjects(@Nonnull List<Project> projects) {
        this.projects = projects;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<Project> getTemplateProjects() {
        return templateProjects;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateProjects(@Nonnull List<Project> templateProjects) {
        this.templateProjects = templateProjects;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Scope getScope() {
        return scope;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }
}