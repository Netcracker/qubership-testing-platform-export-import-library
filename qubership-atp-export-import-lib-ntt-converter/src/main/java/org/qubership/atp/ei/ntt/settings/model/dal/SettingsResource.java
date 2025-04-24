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

package org.qubership.atp.ei.ntt.settings.model.dal;

import org.apache.log4j.Logger;
import org.qubership.atp.ei.ntt.settings.CermFileSystem;
import org.qubership.atp.ei.ntt.settings.model.ApacheXMLConfig;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinksSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentListsSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServerTypesDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServersSettingsDal;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;


/**
 * TODO Make summary for this class.
 */
public class SettingsResource extends DaoFactory.XmlResource {

    private static Logger log = Logger.getLogger(SettingsResource.class);

    private ApacheXMLConfig config = new ApacheXMLConfig(this);

    @Override
    public Associable getAssociable() {

        return config;
    }

    @Override
    public FiledStorer getStorer() {

        return config;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public SettingsResource() {

        super(CermFileSystem.EXEC_SETTINGS_FILE_NAME);
        defaultInitRootChildrenClasses();
    }

    @Override
    public String getRootElementName() {

        return "settings-configuration";
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public void defaultInitRootChildrenClasses() {
        setChild(new ServersSettingsDal());
        setChild(new EnvironmentListsSectionDal());
        setChild(new EnvironmentItemLinksSectionDal());
        setChild(new ServerTypesDal());
    }

}
