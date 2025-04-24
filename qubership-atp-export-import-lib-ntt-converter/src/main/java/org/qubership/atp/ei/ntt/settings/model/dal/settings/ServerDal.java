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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.qubership.atp.ei.ntt.model.Cloneable;
import org.qubership.atp.ei.ntt.settings.model.AdditionalOption;
import org.qubership.atp.ei.ntt.settings.model.Option;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;

import com.google.common.base.Strings;

/**
 * TODO Make summary for this class.
 */
@SuppressWarnings("serial")
public class ServerDal extends DaoFactory.AbstractElementDal implements Serializable, Cloneable<ServerDal> {

    @Option(key = "[@alias]")
    private String alias;

    @Option(key = "[@url]")
    private String url;

    @Option(key = "externalUrl")
    private String externalUrl;

    @Option(key = "[@instance]")
    private String instance;

    @Option(key = "type")
    private String type;

    @Option(key = "dbTns")
    private String tnsName;

    @Option(key = "dbServer")
    private String dbServer;

    @Option(key = "dbPort")
    private String dbPort;

    @Option(key = "dbSid")
    private String dbSid;

    @Option(key = "dbUser")
    private String dbUser;

    @Option(key = "dbPassword")
    private String dbPassword;

    @Option(key = "dbType")
    private String dbType;

    @Option(key = "dbConnectionUrl")
    private String dbConnectionUrl;

    @Option(key = "sshHost")
    private String sshHost;

    @Option(key = "sshUser")
    private String sshUser;

    @Option(key = "sshPassword")
    private String sshPassword;

    @Option(key = "sshWorkingDir")
    private String sshWorkingDir;

    @Option(key = "sshKey")
    private String sshKey;

    @Option(key = "serverVersionEnabled")
    private String serverVersionEnabled = "true";

    @AdditionalOption(key = "additional")
    private Map<String, String> unclassifiedServerParameters;

    public Map<String, String> getUnclassifiedServerParameters() {
        return unclassifiedServerParameters;
    }

    public void setUnclassifiedServerParameters(Map<String, String> unclassifiedServerParameters) {
        this.unclassifiedServerParameters = unclassifiedServerParameters;
    }

    public String getUnclassifiedServerParameter(String key) {
        return this.unclassifiedServerParameters.get(key.toLowerCase());
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    public void putUnclassifiedServerParameter(String key, String value) {
        if (this.unclassifiedServerParameters == null) {
            this.unclassifiedServerParameters = new HashMap<>();
        }
        unclassifiedServerParameters.put(key.toLowerCase(), value);
    }

    @Nonnull
    public String getAlias() {
        return Strings.nullToEmpty(alias);
    }

    public void setAlias(String alias) {
        this.alias = Strings.nullToEmpty(alias);
    }

    @Nonnull
    public String getUrl() {
        return Strings.nullToEmpty(url);
    }

    public void setUrl(String url) {
        this.url = Strings.nullToEmpty(url);
    }

    @Nonnull
    public String getExternalUrl() {
        return Strings.nullToEmpty(externalUrl);
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = Strings.nullToEmpty(externalUrl);
    }

    @Nonnull
    public String getInstance() {
        return Strings.nullToEmpty(instance);
    }

    public void setInstance(String instance) {
        this.instance = Strings.nullToEmpty(instance);
    }

    @Nonnull
    public String getDbServer() {
        return Strings.nullToEmpty(dbServer);
    }

    public void setDbServer(String dbServer) {
        this.dbServer = Strings.nullToEmpty(dbServer);
    }

    @Nonnull
    public String getDbPort() {
        return Strings.nullToEmpty(dbPort);
    }

    public void setDbPort(String dbPort) {
        this.dbPort = Strings.nullToEmpty(dbPort);
    }

    @Nonnull
    public String getDbSid() {
        return Strings.nullToEmpty(dbSid);
    }

    public void setDbSid(String dbSid) {
        this.dbSid = Strings.nullToEmpty(dbSid);
    }

    @Nonnull
    public String getDbUser() {
        return Strings.nullToEmpty(dbUser);
    }

    public void setDbUser(String dbUser) {
        this.dbUser = Strings.nullToEmpty(dbUser);
    }

    @Nonnull
    public String getDbPassword() {
        return Strings.nullToEmpty(dbPassword);
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = Strings.nullToEmpty(dbPassword);
    }

    @Nonnull
    public String getTnsName() {
        return Strings.nullToEmpty(tnsName);
    }

    public void setTnsName(String tnsName) {
        this.tnsName = Strings.nullToEmpty(tnsName);
    }

    public void setDbConnectionUrl(String dbConnectionUrl) {
        this.dbConnectionUrl = Strings.nullToEmpty(dbConnectionUrl);
    }

    @Nonnull
    public String getDbConnectionUrl() {
        return Strings.nullToEmpty(dbConnectionUrl);
    }

    @Nonnull
    public String getDbType() {
        return Strings.nullToEmpty(dbType);
    }

    public void setDbType(String dbType) {
        this.dbType = Strings.nullToEmpty(dbType);
    }

    @Nonnull
    public String getType() {
        return Strings.nullToEmpty(type);
    }

    public void setType(String type) {
        this.type = Strings.nullToEmpty(type);
    }

    @Nonnull
    public String getSshHost() {
        return Strings.nullToEmpty(sshHost);
    }

    public void setSshHost(String sshHost) {
        this.sshHost = Strings.nullToEmpty(sshHost);
    }

    @Nonnull
    public String getSshUser() {
        return Strings.nullToEmpty(sshUser);
    }

    public void setSshUser(String sshUser) {
        this.sshUser = Strings.nullToEmpty(sshUser);
    }

    @Nonnull
    public String getSshPassword() {
        return Strings.nullToEmpty(sshPassword);
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = Strings.nullToEmpty(sshPassword);
    }

    @Nonnull
    public String getSshWorkingDir() {
        return Strings.nullToEmpty(sshWorkingDir);
    }

    public void setSshWorkingDir(String sshWorkingDir) {
        this.sshWorkingDir = Strings.nullToEmpty(sshWorkingDir);
    }

    @Nonnull
    public String getSshKey() {
        return Strings.nullToEmpty(sshKey);
    }

    public void setSshKey(String sshKey) {
        this.sshKey = Strings.nullToEmpty(sshKey);
    }

    @Nonnull
    public String getServerVersionEnabled() {
        return Strings.nullToEmpty(serverVersionEnabled).equals("") ? "true" : this.serverVersionEnabled;
    }

    public void setServerVersionEnabled(String serverVersionEnabled) {

        this.serverVersionEnabled = serverVersionEnabled;
    }

    @Override
    public ServerDal clone() {

        ServerDal serverDal = new ServerDal();

        serverDal.setAlias(this.getAlias());
        serverDal.setUrl(this.getUrl());
        serverDal.setExternalUrl(this.getExternalUrl());
        serverDal.setInstance(this.getInstance());
        serverDal.setType(this.getType());
        serverDal.setDbSid(this.getDbSid());
        serverDal.setDbServer(this.getDbServer());
        serverDal.setDbPort(this.getDbPort());
        serverDal.setTnsName(this.getTnsName());
        serverDal.setDbUser(this.getDbUser());
        serverDal.setDbPassword(this.getDbPassword());
        serverDal.setDbType(this.getDbType());
        serverDal.setDbConnectionUrl(this.getDbConnectionUrl());
        serverDal.setSshHost(this.getSshHost());
        serverDal.setSshUser(this.getSshUser());
        serverDal.setSshPassword(this.getSshPassword());
        serverDal.setSshWorkingDir(this.getSshWorkingDir());
        serverDal.setSshKey(this.getSshKey());
        serverDal.setServerVersionEnabled(this.getServerVersionEnabled());

        return serverDal;
    }
}
