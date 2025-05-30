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

package org.qubership.atp.ei.ntt.settings.model.environment;

import java.util.List;

import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinkDal;


/**
 * Manages DALs of environment item links.
 *
 * @author Boris Kuznetsov
 * @since 29.11.2015.
 */
public interface EnvironmentItemLinkDalHolder {


    void addEnvironmentItemLinkDal(EnvironmentItemLinkDal envItemLinkDal);

    void addEnvironmentItemLinkDals(List<EnvironmentItemLinkDal> itemLinkDals);

    void removeEnvironmentItemLinkDal(EnvironmentItemLinkDal envItemLinkDal);

    void removeAllEnvironmentItemLinkDalByName(String environmentName);

    void updateEnvironmentItemLinkDalsOwner(String oldEnvListName, String newEnvListName);

    List<EnvironmentItemLinkDal> getLinksByEnvironmentListName(String envListName);
}
