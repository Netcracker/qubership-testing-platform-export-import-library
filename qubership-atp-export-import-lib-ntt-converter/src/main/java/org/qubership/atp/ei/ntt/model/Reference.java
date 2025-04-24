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

import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * Reference interface.
 * <p>
 * Describes behaviour of a reference model item.
 * </p>
 *
 * @version 1.4.3
 * @see Template
 */
public interface Reference extends ModelItem {

    /**
     * Returns template which owns the reference.
     *
     * @return Template object.
     * @see Template
     */
    Template getTemplate();

    /**
     * Sets specified template which owns the reference.
     *
     * @param reference Template object.
     */
    void setTemplate(Template reference);

    /**
     * Sets specified template which owns the reference.
     *
     * @param miEnum        Type of the model item.
     * @param templProjName Name of template project.
     * @param templName     Name of template.
     * @see ModelItemType
     * @see Template
     */
    void setTemplate(ModelItemType miEnum, String templProjName, String templName);

    /**
     * Returns ID of the reference.
     *
     * @return ID of the reference as string.
     */
    String getReferenceId();

    /**
     * Sets specified ID to the reference.
     *
     * @param id ID for setting to the reference.
     */
    void setReferenceId(String id);

    /**
     * Returns name of the reference item.
     *
     * @return Name of the reference item.
     */
    String getRefName();

    /**
     * Returns name of template project.
     *
     * @return Name of template project.
     */
    String getTemplProjName();

    /**
     * Returns template name of the reference.
     *
     * @return Name of template.
     */
    String getTemplName();

}
