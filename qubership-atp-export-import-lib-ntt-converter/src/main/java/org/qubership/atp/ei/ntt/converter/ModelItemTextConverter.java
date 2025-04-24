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

package org.qubership.atp.ei.ntt.converter;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;

import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;
import org.qubership.atp.ei.ntt.utils.CommonUtils;

import com.google.common.collect.HashBiMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelItemTextConverter {

    private static ModelItemTextConverter instance;
    private static HashBiMap<TreeNode, TreeNode> changesMapForScopeItems;

    private ModelItemTextConverter() {
        changesMapForScopeItems = HashBiMap.create();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ModelItemTextConverter getInstance() {
        if (instance == null) {
            instance = new ModelItemTextConverter();
        }
        return instance;
    }

    public static HashBiMap<TreeNode, TreeNode> getChangesMapForScope() {
        return changesMapForScopeItems;
    }

    String getPrefix(ModelItemType miEnum) {
        return ConverterConstants.PREFIXES[miEnum.ordinal()];
    }

    /**
     * Sets children from text.
     *
     * @param parent          the parent
     * @param reader          the reader
     * @param isTemplatesTree the is templates tree
     */
    public void setChildrenFromText(ModelItem parent, BufferedReader reader, boolean isTemplatesTree) {
        if (parent == null || reader == null || parent.getModelItemType().isLeaf()) {
            return;
        }
        if (parent instanceof Reference) {
            parent = parent.getTemplate();
        }

        if (parent instanceof Template) {
            parent = ((Template) parent).getModelItem();
        }

        TextToMiConverter converter = new TextToMiConverter(this, parent, isTemplatesTree);
        try {
            String line = reader.readLine();
            while (line != null) {
                converter.appendMi(line.trim());
                line = reader.readLine();
            }
        } catch (Exception e) {
            log.error("Can not convert text to ModelItem", e);
            return;
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(reader);
        }
        converter.flush();//only if no exception occurred
    }

    public void setChildrenFromText(ModelItem parent, File file, boolean isTemplatesTree) {
        setChildrenFromText(parent, CommonUtils.getReaderForFile(file), isTemplatesTree);
    }

    /**
     * Convert mi to text read only text js mapping.
     *
     * @param modelItem the model item
     * @param type      the type
     * @return the read only text js mapping
     */
    public ReadOnlyTextJsMapping convertMiToText(ModelItem modelItem, TextType type) {

        ReadOnlyTextJsMapping mapping = new ReadOnlyTextJsMapping();
        ModelItemType miEnum = modelItem.getModelItemType();
        TextAppender ta = new TextAppender(this, type, miEnum.next(), mapping);
        List<TreeNode> children = modelItem.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (i != 0) {
                mapping.appendNewLine();
            }
            ta.appendModelItem((ModelItem) children.get(i), false);
        }
        return mapping;
    }

    /**
     * Convert template to text read only text js mapping.
     *
     * @param modelItem the model item
     * @param type      the type
     * @return the read only text js mapping
     */
    public ReadOnlyTextJsMapping convertTemplateToText(ModelItem modelItem, TextType type) {

        ReadOnlyTextJsMapping mapping = new ReadOnlyTextJsMapping();
        TextAppender ta = new TextAppender(this, type, ModelItemType.ACTION, mapping);
        List<TreeNode> children = modelItem.getChildren();
        for (int i = 0; i < children.size(); i++) {
            ModelItemType miEnum = ((ModelItem) children.get(i)).getModelItemType();
            ta.setCurrentModelItemType(miEnum);
            ta.clearIndent();
            if (i != 0) {
                mapping.appendNewLine();
            }
            ta.appendModelItem((ModelItem) children.get(i), true);
        }
        return mapping;
    }
}
