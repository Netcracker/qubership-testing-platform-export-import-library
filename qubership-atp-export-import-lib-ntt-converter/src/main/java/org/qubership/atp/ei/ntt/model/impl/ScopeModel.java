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

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.model.ScopeItem;
import org.qubership.atp.ei.ntt.model.enums.ScopeSectionType;


/**
 * Scope implementation.
 *
 * @author Denis Arychkov
 * @version 1.4.4
 * @since 10.01.2014
 */

@SuppressWarnings("serial")
public class ScopeModel extends AbstractTreeNode implements Scope {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScopeItem> getScopeItems() {
        return super.getChildren();
    }

    private List<ScopeItem> getScopeItems(ScopeSectionType type, List<ScopeItem> sourceScopeItemList) {

        List<ScopeItem> filteredScopeItems = new ArrayList<>();

        for (ScopeItem scopeItem : sourceScopeItemList) {

            if (scopeItem.getStage() == type) {
                filteredScopeItems.add(scopeItem);
            }
        }

        return filteredScopeItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScopeItem> getScopeItems(ScopeSectionType type) {

        return getScopeItems(type, getScopeItems());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScopeItems(List<ScopeItem> scopeItems) {
        super.setChildren(scopeItems);
    }


    private List<ScopeItem> resetNumbering(List<ScopeItem> scopeItems) {
        if (scopeItems == null) {
            return null;
        }

        List<ScopeItem> scopePrerequisites = new ArrayList<>();
        List<ScopeItem> scopeActions = new ArrayList<>();
        List<ScopeItem> scopeValidation = new ArrayList<>();

        separateScopeItemsByScopeSection(scopeItems, scopePrerequisites, scopeActions, scopeValidation);
        List<ScopeItem> renumberedScopeItems = resetNumbers(scopePrerequisites, scopeActions, scopeValidation);

        return renumberedScopeItems;
    }


    private List<ScopeItem> resetNumbers(List<ScopeItem> prerequisites,
                                         List<ScopeItem> actions, List<ScopeItem> validation) {

        if (prerequisites == null || actions == null || validation == null) {
            return null;
        }

        int lastNumber = 0;
        lastNumber = renumberItemsFromIndex(prerequisites, lastNumber);
        lastNumber = renumberItemsFromIndex(actions, lastNumber);
        renumberItemsFromIndex(validation, lastNumber);

        List<ScopeItem> allRenumberedScopeItems = new ArrayList<>();
        allRenumberedScopeItems.addAll(prerequisites);
        allRenumberedScopeItems.addAll(actions);
        allRenumberedScopeItems.addAll(validation);

        return allRenumberedScopeItems;
    }


    private int renumberItemsFromIndex(List<ScopeItem> scopeItems, Integer lastIndex) {

        for (int index = 0; index < scopeItems.size(); index++) {
            scopeItems.get(index).setNumber(++lastIndex);
        }

        return lastIndex;
    }


    private void separateScopeItemsByScopeSection(List<ScopeItem> allScopeItems,
                                                  List<ScopeItem> prerequisites, List<ScopeItem> actions,
                                                  List<ScopeItem> validation) {

        if (allScopeItems == null || prerequisites == null || actions == null || validation == null) {
            return;
        }

        prerequisites.clear();
        actions.clear();
        validation.clear();

        for (ScopeItem scopeItem : allScopeItems) {
            switch (scopeItem.getStage()) {

                case PREREQUISITES:
                    prerequisites.add(scopeItem);
                    continue;
                case ACTIONS:
                    actions.add(scopeItem);
                    continue;
                case VALIDATION:
                    validation.add(scopeItem);
                    continue;
                default:
                    break;
            }
        }
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public void addScopeItems(List<ScopeItem> scopeItems) {

        List<ScopeItem> toDelete = new ArrayList<>();

        // No need to add already existing items twice.
        for (ScopeItem item : scopeItems) {
            if (getScopeItems().contains(item)) {
                toDelete.add(item);
            }
        }
        scopeItems.removeAll(toDelete);

        // Add only not existing items.
        super.getChildren().addAll(scopeItems);
        calcScopeItemsList();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void calcScopeItemsList() {

        setScopeItems(resetNumbering(getScopeItems()));

        int size = getScopeItems().size();
        for (int i = 0; i < size; i++) {

            ScopeItem item = getScopeItems().get(i);
            String itemName = item.getName();
            String newName = String.format("%d_%s", item.getNumber(), itemName.substring(itemName.indexOf('_') + 1));
            item.setName(newName);
        }
    }


    @Override
    public ScopeItem getScopeItemByName(String name) {

        for (ScopeItem scopeItem : getScopeItems()) {
            if (scopeItem.getName().equals(name)) {
                return scopeItem;
            }
        }

        return null;
    }


    /**
     * Adds a single scope item into the scope.
     *
     * @param newItem Scope item for adding.
     * @see ScopeItem
     */
    public void addItemToScope(ScopeItem newItem) {

        if (newItem == null) {
            return;
        }

        List<ScopeItem> currentScopeItems = new ArrayList<>(getScopeItems());
        List<ScopeItem> tempScope = new ArrayList<>(currentScopeItems);
        String newItemName = newItem.getName().replaceAll("\\d*_", StringUtils.EMPTY);

        for (int i = 0; i < tempScope.size(); i++) {

            String itemName = tempScope.get(i).getName().replaceAll("\\d*_", StringUtils.EMPTY);

            //if currentScope contain newItem
            if (itemName.equals(newItemName)
                    && tempScope.get(i).getProject().equals(newItem.getProject())
                    && tempScope.get(i).getStage().equals(newItem.getStage())) {
                currentScopeItems.remove(i);
                break;
            }
        }
        currentScopeItems.add(0, newItem);
        super.setChildren(currentScopeItems);
    }
}
