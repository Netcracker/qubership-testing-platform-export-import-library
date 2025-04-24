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

package org.qubership.atp.ei.node.repo;

import java.io.InputStream;
import java.util.UUID;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.qubership.atp.ei.node.config.GridFsConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.client.gridfs.model.GridFSFile;

@Repository
public class GridFsRepository {

    private final GridFsTemplate eiGridFsTemplate;

    public GridFsRepository(GridFsConfiguration.GridFsProvider eiGridFsTemplate) {
        this.eiGridFsTemplate = eiGridFsTemplate.getGridFsTemplate();
    }

    /**
     * Store object id.
     *
     * @param fileInputStream the file input stream
     * @param fileName        the file name
     * @param contentType     the content type
     * @param processId       the process id
     * @return the object id
     */
    public ObjectId store(InputStream fileInputStream, String fileName, String contentType, String processId) {
        Document document = new Document();
        document.append("processId", UUID.fromString(processId));
        document.append("originalName", fileName);

        return eiGridFsTemplate.store(fileInputStream, processId + "_" + fileName, contentType, document);
    }

    public GridFSFile findOne(ObjectId objectId) {
        return eiGridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
    }

    public InputStreamResource getResourceById(ObjectId objectId) {
        return eiGridFsTemplate.getResource(findOne(objectId));
    }
}