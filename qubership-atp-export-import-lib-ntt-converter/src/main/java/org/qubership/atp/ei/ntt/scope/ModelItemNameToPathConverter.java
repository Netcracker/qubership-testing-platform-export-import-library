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

package org.qubership.atp.ei.ntt.scope;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.ModelItem;

/**
 * The type Model item name to path converter.
 *
 * @since 27.05.2015
 */
public class ModelItemNameToPathConverter {

    /**
     * Extract test suite string.
     *
     * @param modelItem the model item
     * @return the string
     */
    public static String extractTestSuite(ModelItem modelItem) {

        switch (modelItem.getModelItemType()) {
            case SUITE:
                return modelItem.getName();
            case CASE:
                return modelItem.getParent().getName();
            case STEP:
                return modelItem.getParent().getParent().getName();
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * Extract test case string.
     *
     * @param modelItem the model item
     * @return the string
     */
    public static String extractTestCase(ModelItem modelItem) {

        switch (modelItem.getModelItemType()) {

            case CASE:
                return modelItem.getName();
            case STEP:
                return modelItem.getParent().getName();
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * Extract test step string.
     *
     * @param modelItem the model item
     * @return the string
     */
    public static String extractTestStep(ModelItem modelItem) {

        switch (modelItem.getModelItemType()) {

            case STEP:
                return modelItem.getName();
            default:
                return StringUtils.EMPTY;
        }
    }
}
