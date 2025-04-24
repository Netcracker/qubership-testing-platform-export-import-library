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

import java.util.UUID;

import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.ScopeItem;
import org.qubership.atp.ei.ntt.model.enums.ScopeItemMode;
import org.qubership.atp.ei.ntt.model.enums.ScopeSectionType;
import org.qubership.atp.ei.ntt.model.enums.ValidationLevel;

public class ScopeItemModel extends AbstractTreeNode implements ScopeItem {

    private UUID uuid;
    private int number;
    private ModelItem modelItem;
    private DataSet dataSet;
    private ScopeItemMode mode;
    private ValidationLevel validationLevel;
    private ScopeItem dependency;
    private int curActionNum = 0;
    private int actionsNum = 0;
    private int counter = 0;
    private ScopeSectionType stage = ScopeSectionType.ACTIONS;
    private int countLimit = 1;

    /**
     * Instantiates a new Scope item model.
     *
     * @param modelItem the model item
     * @param dataSet   the data set
     */
    public ScopeItemModel(ModelItem modelItem, DataSet dataSet) {
        this.modelItem = modelItem;
        this.dataSet = dataSet;
        this.uuid = UUID.randomUUID();
        this.stage = ScopeSectionType.ACTIONS;
    }

    @Override
    public UUID getId() {
        return uuid;
    }


    @Override
    public void init(ModelItem model, DataSet dataSet) {

    }


    @Override
    public int getNumber() {
        return number;
    }


    @Override
    public void setNumber(int number) {
        this.number = number;
    }


    @Override
    public ModelItem getModelItem() {
        return modelItem;
    }


    @Override
    public void setModelItem(ModelItem modelItem) {
        this.modelItem = modelItem;
    }


    @Override
    public DataSet getDataSet() {
        return dataSet;
    }


    @Override
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }


    @Override
    public ScopeItemMode getMode() {
        return mode;
    }


    @Override
    public void setMode(ScopeItemMode mode) {
        this.mode = mode;
    }

    @Override
    public ValidationLevel getValidationLevel() {
        return validationLevel;
    }


    @Override
    public void setValidationLevel(ValidationLevel validationLevel) {

        this.validationLevel = validationLevel;
    }


    @Override
    public Project getProject() {
        return modelItem.getProject();
    }


    @Override
    public int getActionsNum() {
        return actionsNum;
    }


    @Override
    public void setActionsNum(int actionsNum) {
        this.actionsNum = actionsNum;
    }


    @Override
    public int getCurrentActionNum() {

        return curActionNum;
    }

    @Override
    public void setCurrentActionNum(int curActionNum) {

        this.curActionNum = curActionNum;
    }

    @Override
    public ScopeItem getDependency() {
        return dependency;
    }

    @Override
    public void setDependency(ScopeItem dependency) {
        this.dependency = dependency;
    }

    @Override
    public int getCounter() {

        return counter;
    }

    @Override
    public int getCountLimit() {

        return countLimit;
    }

    @Override
    public void setCounter(int count) {

        this.counter = count;
    }

    @Override
    public void setCountLimit(int limit) {

        countLimit = limit;
    }

    @Override
    public void setStage(ScopeSectionType stage) {
        this.stage = stage;
    }

    @Override
    public ScopeSectionType getStage() {

        return stage;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void refresh() {

    }
}
