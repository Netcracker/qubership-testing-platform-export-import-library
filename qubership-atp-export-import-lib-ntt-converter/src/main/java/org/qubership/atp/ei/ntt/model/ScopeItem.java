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

import java.util.UUID;

import org.qubership.atp.ei.ntt.model.enums.ScopeItemMode;
import org.qubership.atp.ei.ntt.model.enums.ScopeSectionType;
import org.qubership.atp.ei.ntt.model.enums.ValidationLevel;

/**
 * The interface Scope item.
 *
 * @author Denis Arychkov
 * @version 1.4.4
 * @since 10.01.2014
 */
public interface ScopeItem extends TreeNode {

    UUID getId();

    void init(ModelItem model, DataSet dataSet);

    int getNumber();

    void setNumber(int number);

    ModelItem getModelItem();

    void setModelItem(ModelItem modelItem);

    DataSet getDataSet();

    void setDataSet(DataSet dataSet);

    ScopeItemMode getMode();

    void setMode(ScopeItemMode mode);

    ValidationLevel getValidationLevel();

    void setValidationLevel(ValidationLevel validationLevel);

    Project getProject();

    int getActionsNum();

    void setActionsNum(int actionsNum);

    int getCurrentActionNum();

    void setCurrentActionNum(int curActionNum);

    ScopeItem getDependency();

    void setDependency(ScopeItem dependency);

    int getCounter();

    void setCounter(int count);

    int getCountLimit();

    void setCountLimit(int limit);

    void setStage(ScopeSectionType stage);

    ScopeSectionType getStage();

    void refresh();
}
