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

/**
 * TestAction interface.
 * <p>
 * Describes behaviour of the test action items.
 * </p>
 *
 * @version 1.4.3
 */
public interface TestAction extends ModelItem {

    /**
     * Returns an ID of the action.
     *
     * @return ID of the action.
     */
    String getActionId();

    /**
     * Sets the action ID.
     *
     * @param actionId Action ID.
     */
    void setActionId(String actionId);
}
