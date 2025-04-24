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

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

/**
 * The type Text appender.
 *
 * @author Boris Kuznetsov
 * @since 29.07.2016
 */
class TextAppender {

    private TextAppendMethod textAppendMethod;
    private ModelItemType startingModelItemType;
    private ModelItemType currentModelItemType;
    private String modelItemIndent = StringUtils.EMPTY;
    private ReadOnlyTextJsMapping mapping;
    private ModelItemTextConverter converter;

    TextAppender(ModelItemTextConverter converter, TextType type, ModelItemType modelItemType,
                 ReadOnlyTextJsMapping mapping) {

        this.converter = converter;
        this.startingModelItemType = modelItemType;
        setCurrentModelItemType(modelItemType);
        setType(type);
        this.mapping = mapping;
    }

    public void setType(TextType type) {
        switch (type) {
            case EDITOR:
                textAppendMethod = appendToEditor;
                return;
            case FILE:
                textAppendMethod = appendToFile;
                return;
            default:
                break;
        }
    }

    public void clearIndent() {
        this.startingModelItemType = this.currentModelItemType;
        generateModelItemIdent();
    }

    public void setCurrentModelItemType(ModelItemType modelItemsEnum) {
        this.currentModelItemType = modelItemsEnum;
        generateModelItemIdent();
    }

    public void appendModelItem(ModelItem modelItem, boolean isTemplatesTree) {
        if (isTemplatesTree) {
            textAppendMethod.append(modelItem, false);
        } else {
            boolean isReference = false;
            if (modelItem.isTemplate()) {
                isReference = true;
            }
            textAppendMethod.append(modelItem, isReference);
        }
    }

    private void inToHierarchy() {
        currentModelItemType = currentModelItemType.next();
        modelItemIndent = modelItemIndent + Constants.CASE_INDENT;
    }

    private void outFromHierarchy() {
        currentModelItemType = currentModelItemType.previous();
        modelItemIndent = modelItemIndent.substring(Constants.CASE_INDENT.length());
    }

    private void generateModelItemIdent() {
        modelItemIndent = StringUtils.EMPTY;
        int iter = currentModelItemType.ordinal() - startingModelItemType.ordinal();
        for (int i = 0; i < iter; i++) {
            modelItemIndent = modelItemIndent + Constants.CASE_INDENT;
        }
    }

    private String getFlagString(ModelItem item) {
        final String[] flags = item.getFlags();
        if (flags.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder result = new StringBuilder("; flags=");
        for (String flag : flags) {
            if (result.length() > 8) { // cuz "; flags=".length()
                result.append(ConverterConstants.COMMA);
            }
            result.append(flag);
        }
        return result.toString();
    }

    private String getModelState(ModelItem item) {
        if (!item.isEnabled()) {
            return ConverterConstants.COMMENT;
        }
        return StringUtils.EMPTY;
    }

    private TextAppendMethod appendToEditor = new TextAppendMethod() {

        @Override
        public void append(ModelItem modelItem, boolean isReference) {

            boolean instanceOfTempl = modelItem instanceof Template;
            boolean instanceOfRef = modelItem instanceof Reference;
            if (!isReference) {
                isReference = instanceOfRef;
            }

            boolean isReadOnly = false;
            mapping.append(modelItemIndent, isReadOnly).append(getModelState(modelItem), isReadOnly);
            // indent + modelState +...
            String tempModelItemName = modelItem.getName();
            boolean isLeaf = currentModelItemType.isLeaf();
            if (!isLeaf) {
                String tempModelItem = converter.getPrefix(currentModelItemType);
                if (instanceOfTempl) {
                    tempModelItem = ConverterConstants.TEMPLATE_ + tempModelItem;
                } else if (instanceOfRef) {
                    tempModelItemName = ConverterConstants.TEMPLATE_ + getRefName((Reference) modelItem);
                }
                mapping.append(tempModelItem, isReadOnly); // ...+ ModelItemTypeName + ...
            }
            mapping.append(tempModelItemName, isReadOnly).append(getFlagString(modelItem), isReadOnly);
            // ...+ ModelItemName + FlagString
            if (!isLeaf) {
                inToHierarchy(); //going for childs
                for (TreeNode child : modelItem.getChildren()) {
                    mapping.appendNewLine();
                    textAppendMethod.append((ModelItem) child, isReference);
                }
                outFromHierarchy();
            }
        }
    };

    private TextAppendMethod appendToFile = new TextAppendMethod() {

        @Override
        public void append(ModelItem modelItem, boolean isReference) {

            mapping.append(modelItemIndent).append(getModelState(modelItem)); // indent + modelState +...
            String tempModelItemName = modelItem.getName();
            boolean isLeaf = currentModelItemType.isLeaf();
            if (!isLeaf) {
                String tempModelItem = converter.getPrefix(currentModelItemType);
                if (modelItem instanceof Template) {
                    tempModelItem = ConverterConstants.TEMPLATE_ + tempModelItem;

                } else if (modelItem instanceof Reference) {
                    tempModelItemName = ConverterConstants.TEMPLATE_ + getRefName((Reference) modelItem);
                    isReference = true; //not needed to save childs of reference in file;
                }
                mapping.append(tempModelItem); // ...+ ModelItemTypeName + ...
            }
            mapping.append(tempModelItemName).append(getFlagString(modelItem)); // ...+ ModelItemName + FlagString
            if (!isLeaf) {
                inToHierarchy(); //going for childs
                if (!isReference) {
                    //not needed to save childs of reference in file;
                    for (TreeNode child : modelItem.getChildren()) {
                        mapping.appendNewLine();
                        textAppendMethod.append((ModelItem) child, false);
                    }
                }
                outFromHierarchy();
            }
        }
    };

    private String getRefName(Reference ref) {
        String name = ref.getTemplProjName() + ConverterConstants.REFDELIMETER + ref.getTemplName();
        String refName = ref.getRefName();
        if (!refName.equals(StringUtils.EMPTY) && !Objects.equals(refName, name)) {
            name = name + (ConverterConstants.SPACE + ConverterConstants.QUOTE + refName + ConverterConstants.QUOTE);
        }
        return name;
    }
}
