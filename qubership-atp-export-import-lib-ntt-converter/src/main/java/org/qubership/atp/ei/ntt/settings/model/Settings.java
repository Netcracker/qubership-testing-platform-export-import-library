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

package org.qubership.atp.ei.ntt.settings.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.settings.ReflectionUtils;
import org.qubership.atp.ei.ntt.settings.model.dal.SettingsResource;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinkDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinksSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentListDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentListsSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServerDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServerTypesDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServersSettingsDal;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;
import org.qubership.atp.ei.ntt.settings.model.environment.EnvironmentStorage;


public final class Settings {

    private static DaoFactory factory = DaoFactory.getInstance();
    private static SettingsResource settingsDAO;
    private static Map<Class<?>, Object> wrapper = new HashMap<>();

    static {
        settingsDAO = factory.loadResource(new SettingsResource());
    }

    /**
     * TODO Make javadoc documentation for this method.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {

        if (wrapper.containsKey(clazz)) {
            return (T) wrapper.get(clazz);
        }

        boolean isClassMatch = clazz != null && DaoFactory.ElementDal.class.isAssignableFrom(clazz);

        Object object = isClassMatch 
                ? factory.get(settingsDAO, (Class<? extends DaoFactory.ElementDal>) clazz)
                : ReflectionUtils.getInstance(clazz);
 
        wrapper.put(clazz, object);
        return (T) object;
    }

    public static void store() {

        factory.save(settingsDAO);
    }
    
    public static SettingsResource getSettingsResource() {

        return settingsDAO;
    }
    
    @Nonnull
    public static DaoFactory getDaoFactory() {

        return factory;
    }


    /**
     * Environment settings controller.
     * <p>
     */
    public static class EnvironmentSettings implements EnvironmentStorage {

        private List<EnvironmentListDal> envListDals = factory.get(
                                                        settingsDAO, EnvironmentListsSectionDal.class)
                                                        .getEnvironmentLists();
        private List<EnvironmentItemLinkDal> envItemLinkDals = factory.get(
                                                                settingsDAO, EnvironmentItemLinksSectionDal.class)
                                                                .getEnvironmentItemLinks();


        @Override
        public List<EnvironmentListDal> getEnvironmentListDals() {

            return envListDals;
        }

        @Override
        public List<EnvironmentItemLinkDal> getEnvironmentItemLinkDals() {

            return envItemLinkDals;
        }

        @Override
        public void save() {

            saveEnvironmentLists();
        }

        @Override
        public void addEnvironmentItemLinkDal(EnvironmentItemLinkDal envItemLinkDal) {

            if (envItemLinkDal == null || !envItemLinkDals.contains(envItemLinkDal)) {
                return;
            }

            envItemLinkDals.add(envItemLinkDal);
        }

        @Override
        public void addEnvironmentItemLinkDals(List<EnvironmentItemLinkDal> itemLinkDals) {

            if (itemLinkDals == null) {
                return;
            }

            envItemLinkDals.addAll(itemLinkDals);
        }

        @Override
        public void removeEnvironmentItemLinkDal(EnvironmentItemLinkDal envItemLinkDal) {

            if (envItemLinkDal == null || !envItemLinkDals.contains(envItemLinkDal)) {
                return;
            }

            envItemLinkDals.remove(envItemLinkDal);
        }

        @Override
        public void removeAllEnvironmentItemLinkDalByName(String environmentName) {

            List<EnvironmentItemLinkDal> delItems = getLinksByEnvironmentListName(environmentName);
            envItemLinkDals.removeAll(delItems);
        }

        @Override
        public void updateEnvironmentItemLinkDalsOwner(String oldEnvListOwner, String newEnvListName) {

            if (newEnvListName == null || newEnvListName.isEmpty()
                    || oldEnvListOwner == null || oldEnvListOwner.isEmpty()) {
                return;
            }

            List<EnvironmentItemLinkDal> foundedLinkDals = getLinksByEnvironmentListName(oldEnvListOwner);

            for (EnvironmentItemLinkDal item : foundedLinkDals) {
                item.setEnvironment(newEnvListName);
            }
        }

        /**
         * Returns all EnvironmentItemLinkDALs with specified environment name.
         * TODO add description for params and return
         */
        @Override
        public List<EnvironmentItemLinkDal> getLinksByEnvironmentListName(String envListName) {

            List<EnvironmentItemLinkDal> foundedItems = new ArrayList<>();
            for (EnvironmentItemLinkDal link : getEnvironmentItemLinkDals()) {
                if (link.getEnvironment().equals(envListName)) {
                    foundedItems.add(link);
                }
            }
            return foundedItems;
        }

        @Override
        @Nullable
        public EnvironmentListDal getEnvironmentListDalByName(String envListName) {

            if (envListName == null || envListName.isEmpty()) {

                return null;
            }

            for (EnvironmentListDal list : getEnvironmentListDals()) {

                if (list.getName().equals(envListName)) {
                    return list;
                }
            }
            
            return null;
        }

        @Override
        public void addEnvironmentListDal(@Nonnull EnvironmentListDal envListDal) {

            if (envListDal == null || envListDals.contains(envListDal)) {
                return;
            }

            envListDals.add(envListDal);
        }

        @Override
        public void removeEnvironmentListDal(@Nonnull EnvironmentListDal envListDal) {

            if (envListDal == null || !envListDals.contains(envListDal)) {
                return;
            }

            removeAllEnvironmentItemLinkDalByName(envListDal.getName());
            envListDals.remove(envListDal);
        }

        /**
         * Saves environment lists in settings file.
         */
        private void saveEnvironmentLists() {

            List<EnvironmentListDal> envListDals = factory.get(
                                                    settingsDAO, EnvironmentListsSectionDal.class)
                                                    .getEnvironmentLists();
            envListDals.clear();
            envListDals.addAll(getEnvironmentListDals());
        }


    }

    public static class ServerTypesSettings {

        private List<String> serverTypes = factory.get(settingsDAO, ServerTypesDal.class).getTypes();

        public List<String> getTypes() {

            return serverTypes;
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void add(@Nonnull String type) {

            if (!type.isEmpty() && !serverTypes.contains(type)) {
                serverTypes.add(type);
            }
        }

        public void delete(@Nonnull String type) {
            serverTypes.remove(type);
        }
    }


    public static class ServersSettings {

        private Map<String, ServerDal> serversMap = new HashMap<>();

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nonnull
        public String getCurrentServerAlias() {

            ServersSettingsDal serverSettings = factory.get(settingsDAO, ServersSettingsDal.class);
            return serverSettings != null ? serverSettings.getCurrentServerAlias() : StringUtils.EMPTY;
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void setCurrentServerAlias(String currentServerAlias) {

            ServersSettingsDal serverSettings = factory.get(settingsDAO, ServersSettingsDal.class);
            if (serverSettings != null) {
                serverSettings.setCurrentServerAlias(currentServerAlias);
            }
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nonnull
        public String getCurrentServerUrl() {

            ServersSettingsDal serverSettings = factory.get(settingsDAO, ServersSettingsDal.class);
            return serverSettings != null ? serverSettings.getCurrentServerUrl() : StringUtils.EMPTY;
        }

        private void loadServerMap() {

            ServersSettingsDal serverSettings = factory.get(settingsDAO, ServersSettingsDal.class);
            if (serverSettings == null || serverSettings.getServerList() == null) {

                return;
            }
            
            serversMap.clear();
            
            serverSettings.getServerList()
                .forEach(server -> serversMap.put(server.getAlias(), server));
        }

        private void storeServerMap() {

            ServersSettingsDal serverSettings = factory.get(settingsDAO, ServersSettingsDal.class);

            if (serverSettings == null || serverSettings.getServerList() == null) {
                return;
            }

            serverSettings.getServerList().clear();

            serversMap.entrySet()
                .forEach(entry -> serverSettings.getServerList().add(entry.getValue()));
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nonnull
        public Map<String, ServerDal> getServersMap() {

            if (serversMap.isEmpty()) {
                loadServerMap();
            }
            
            return serversMap;
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nonnull
        public ServerDal[] getServers() {

            Collection<ServerDal> servers = getServersMap().values();
            return servers.toArray(new ServerDal[servers.size()]);
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nullable
        public ServerDal getServer(int index) {

            return (index > getServers().length - 1) || (index < 0)
                    ? null
                    : getServers()[index];
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void addServer(@Nonnull ServerDal server) {

            getServersMap().put(server.getAlias(), server);
            storeServerMap();
            
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void updateServer(@Nonnull ServerDal oldServer, @Nonnull ServerDal newServer) {

            this.serversMap.remove(oldServer.getAlias());
            this.serversMap.put(newServer.getAlias(), newServer);
            storeServerMap();
            
       }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void deleteServer(int index) {

            ServerDal server = getServer(index);
            if (server == null) {
                return;
            }
            
            getServersMap().remove(server.getAlias());
            storeServerMap();
            
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        public void deleteServer(String alias) {

            ServerDal serverDal = getServersMap().get(alias);
            if (serverDal == null) {
                return;
            }
            
            getServersMap().remove(alias);
            storeServerMap();
            
        }

        /**
         * TODO Make javadoc documentation for this method.
         */
        @Nonnull
        public String[] getServerUrls() {

            ServerDal[] servers = getServers();
            String[] urls = new String[servers.length];
            
            int i = 0;
            for (ServerDal server : servers) {

                if (server == null) {
                    continue;
                }

                urls[i] = server.getUrl();
                i = i + 1;
            }
            return urls;
        }
    }
}
