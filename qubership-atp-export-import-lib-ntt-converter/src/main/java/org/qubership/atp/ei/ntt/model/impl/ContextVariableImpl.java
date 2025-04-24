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

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.DataSet;

/**
 * The type Context variable.
 *
 * @author Roman Aksenenko
 * @since 07.05.2014
 */
public class ContextVariableImpl extends AbstractGenericItem implements ContextVariable {

    private String value = StringUtils.EMPTY;
    private DataSet parent;


    @Override
    public String getValue() {

        return value;
    }


    @Override
    public void setValue(String value) {

        this.value = value;
    }


    @Override
    public DataSet getParent() {

        return parent;
    }


    @Override
    public void setParent(DataSet dataSet) {

        this.parent = dataSet;
    }


    /**
     * Clone by context variable.
     *
     * @param originVariable the origin variable
     * @param customDs       the custom ds
     * @return the context variable
     */
    public static ContextVariable cloneBy(ContextVariable originVariable, DataSet customDs) {

        ContextVariable newVariable = new ContextVariableImpl();
        newVariable.setName(originVariable.getName());
        newVariable.setDescription(originVariable.getDescription());
        newVariable.setValue(originVariable.getValue());
        newVariable.setParent(customDs);
        return newVariable;
    }
}
