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

import static org.qubership.atp.ei.ntt.Constants.EXPORT_PROJECTS_FOLDER_NAME;

import java.io.File;
import java.util.List;

import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.model.ScopeItem;
import org.qubership.atp.ei.ntt.utils.CommonUtils;

public class ScopeDaoFactory {
    private static ScopeDaoFactory ourInstance = new ScopeDaoFactory();

    public static ScopeDaoFactory getInstance() {
        return ourInstance;
    }

    private ScopeDaoFactory() {
    }


    /**
     * Gets scope dao.
     *
     * @param scope the scope
     * @return the scope dao
     */
    public ScopeDao getScopeDao(Scope scope) {
        ScopeDao result = new ScopeDao();
        result.setName(scope.getName());
        result.setDescription(scope.getDescription());

        List<ScopeItem> items = scope.getScopeItems();
        for (ScopeItem item : items) {
            ScopeItemDao scopeItem = new ScopeItemDao();
            scopeItem.setUuid(item.getId().toString());

            scopeItem.setModelItem("");

            if (item.getDataSet() != null) {
                scopeItem.setDataSet(item.getDataSet().getName());
            }

            if (item.getDependency() != null) {
                scopeItem.setBlockedBy(item.getDependency().getId().toString());
            }

            scopeItem.setStage(item.getStage().toString());
            scopeItem.setCountLimit(String.valueOf(item.getCountLimit()));
            scopeItem.setServer("");

            scopeItem.setTestSuite(ModelItemNameToPathConverter.extractTestSuite(item.getModelItem()));
            scopeItem.setTestCase(ModelItemNameToPathConverter.extractTestCase(item.getModelItem()));
            scopeItem.setTestStep(ModelItemNameToPathConverter.extractTestStep(item.getModelItem()));

            String projectFileName = CommonUtils.getSafeFilename(item.getProject().getName());
            scopeItem.setProject("." + File.separatorChar + EXPORT_PROJECTS_FOLDER_NAME
                    + File.separatorChar + projectFileName + Constants.TXT_PROJECT_EXTENTION);

            result.getScopeItems().add(scopeItem);
        }

        return result;
    }
}
