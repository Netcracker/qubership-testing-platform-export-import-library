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

package org.qubership.atp.ei.ntt.settings.model.dal.settings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.Options;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;

import com.google.common.base.Strings;

/**
 * TODO Make summary for this class.
 */
public class ServersSettingsDal extends DaoFactory.AbstractElementDal {

    @Option(key = "serversSettings.currentServerAlias")
    private String currentServerAlias;

    @Options(key = "serversSettings.server", parent = "serversSettings", listClass = ServerDal.class)
    private List<ServerDal> serverList = new ArrayList<>();

    public String getCurrentServerAlias() {

        return currentServerAlias != null
                ? currentServerAlias
                : serverList.size() > 0 ? serverList.get(0).getAlias() : StringUtils.EMPTY;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nonnull
    public String getCurrentServerUrl() {

        for (ServerDal srv : serverList) {
            if (srv.getAlias().equals(currentServerAlias)) {
                return Strings.nullToEmpty(srv.getUrl());
            }
        }
        return Strings.nullToEmpty(serverList.get(0).getUrl());
    }

    public void setCurrentServerAlias(String currentServerAlias) {

        this.currentServerAlias = Strings.nullToEmpty(currentServerAlias);
    }

    @Nullable
    public List<ServerDal> getServerList() {

        return serverList;
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    @Nullable
    public ServerDal getServerByAlias(String alias) {

        for (ServerDal server : serverList) {
            if (server.getAlias().equalsIgnoreCase(alias)) {
                return server;
            }
        }
        return null;
    }
}
