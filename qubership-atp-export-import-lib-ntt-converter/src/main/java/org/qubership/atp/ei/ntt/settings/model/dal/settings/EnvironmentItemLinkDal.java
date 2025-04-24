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

import java.io.Serializable;

import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;

import com.google.common.base.Strings;

/**
 * TODO Make summary for this class.
 */
@SuppressWarnings("Serial")
public class EnvironmentItemLinkDal extends DaoFactory.AbstractElementDal implements Serializable,
        Cloneable<EnvironmentItemLinkDal> {

    @Option(key = "[@environment]")
    private String environment;

    @Option(key = "[@server]")
    private String server;

    @Option(key = "[@type]")
    private String type;

    @Override
    public EnvironmentItemLinkDal clone() {

        EnvironmentItemLinkDal item = new EnvironmentItemLinkDal();
        item.setEnvironment(this.getEnvironment());
        item.setServer(this.getServer());
        item.setType(this.getType());
        return item;
    }

    public String getEnvironment() {

        return Strings.nullToEmpty(environment);
    }

    public void setEnvironment(String environment) {

        this.environment = Strings.nullToEmpty(environment);
    }

    public String getServer() {

        return Strings.nullToEmpty(server);
    }

    public void setServer(String server) {

        this.server = server;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getType() {

        return Strings.nullToEmpty(type);
    }
}
