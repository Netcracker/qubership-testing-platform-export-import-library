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

import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * Interface of an item in model structure of workspace.
 *
 * @version 1.4.3
 */
public interface ModelItem extends TreeNode {

    /**
     * Returns owner project of the item.
     *
     * @return Owner project.
     */
    Project getProject();

    /**
     * Returns {@code true} if the item is template item.
     *
     * @return True if it is template.
     */
    boolean isTemplate();

    /**
     * Returns {@code true} if the item is reference item.
     *
     * @return True if it is reference.
     */
    boolean isReference();

    /**
     * Returns a template of the item if it is template model.
     *
     * @return {@code Template} object
     * @see Template
     */
    Template getTemplate();

    /**
     * Returns state (enabled/disabled) of the item.
     *
     * @return State of the item.
     */
    boolean isEnabled();

    /**
     * Sets state (enabled/disabled) of the item.
     *
     * @param isEnable State of the item.
     */
    void setEnabled(boolean isEnable);

    /**
     * Deprecated functionality. Saved for backward compatibility.
     *
     * @return Unused.
     */
    boolean isWanted();

    /**
     * Deprecated functionality. Saved for backward compatibility.
     *
     * @param isWanted Unused.
     */
    void setWanted(boolean isWanted);

    /**
     * Returns list of action items which the model item contains.
     *
     * @param <T> Generic type of TestAction item.
     * @return List of TestAction.
     * @see TestAction
     */
    <T extends TestAction> List<T> getActions();

    /**
     * Returns type of the model item without using of {@code instanceof} operator.
     *
     * @return Model item types enumeration.
     * @see ModelItemType
     */
    ModelItemType getModelItemType();
}
