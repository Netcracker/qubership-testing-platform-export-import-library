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

import org.qubership.atp.ei.ntt.model.enums.ScopeSectionType;

/**
 * Scope interface.
 *
 * @author Denis Arychkov
 * @version 1.4.4
 * @since 10.01.2014
 */
public interface Scope extends TreeNode, ContainerTreeNode {

    /**
     * Returns all scope items.
     *
     * @return List of scope items.
     * @see ScopeItem
     */
    List<ScopeItem> getScopeItems();

    /**
     * Returns scope items of specified type.
     *
     * @param type Type of scope items for filtering.
     * @return List of filtered scope items.
     * @see ScopeItem
     */
    List<ScopeItem> getScopeItems(ScopeSectionType type);

    /**
     * Sets scope items into scope.
     *
     * @param scopeItems List of scope items.
     * @see ScopeItem
     */
    void setScopeItems(List<ScopeItem> scopeItems);

    /**
     * Adds specified scope items to the scope with name checking.
     *
     * @param scopeItems List of scope items for addition.
     */
    void addScopeItems(List<ScopeItem> scopeItems);

    /**
     * Renumbers scope items in the scope.
     */
    void calcScopeItemsList();

    /**
     * Searches scope item by its name.
     *
     * @param name Name of searching scope item.
     * @return Scope item.
     */
    ScopeItem getScopeItemByName(String name);
}
