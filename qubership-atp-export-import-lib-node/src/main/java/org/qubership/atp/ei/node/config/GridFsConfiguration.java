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

package org.qubership.atp.ei.node.config;

import org.apache.commons.lang3.StringUtils;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
public class GridFsConfiguration {

    @Value("${ei.gridfs.host}")
    private String host;
    @Value("${ei.gridfs.port}")
    private String port;
    @Value("${ei.gridfs.database}")
    private String database;
    @Value("${ei.gridfs.user}")
    private String user;
    @Value("${ei.gridfs.password}")
    private String password;

    /**
     * To hide from Spring Mongo autoconfiguration GridFSTemplate.
     */
    @Bean
    public GridFsProvider eiGridFsTemplate() {
        if (StringUtils.isEmpty(host)) {
            return new GridFsProvider(null);
        }
        String mongoClientUri = "mongodb://" + user + ":" + password
                + "@" + host + ":" + Integer.parseInt(port) + "/?authSource=" + database;
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoClientUri))
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .build()
        );
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient, database);
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(SimpleTypeHolder.DEFAULT);
        mappingContext.afterPropertiesSet();
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        return new GridFsProvider(new GridFsTemplate(factory, mappingConverter));
    }

    @Getter
    @RequiredArgsConstructor
    public static class GridFsProvider {

        private final GridFsTemplate gridFsTemplate;

        public GridFsTemplate getTemplate() {
            return gridFsTemplate;
        }
    }
}
