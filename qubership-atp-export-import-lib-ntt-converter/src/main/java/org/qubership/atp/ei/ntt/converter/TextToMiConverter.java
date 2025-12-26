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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.controllers.ModelItemController;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.ScopeItem;
import org.qubership.atp.ei.ntt.model.TemplateProject;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;
import org.qubership.atp.ei.ntt.utils.NttModelLoader;

/**
 * The type Text to Model Item converter.
 *
 * @author Boris Kuznetsov
 * @since 29.07.2016
 */
public class TextToMiConverter {

    private static final ModelItemController MIC = ModelItemController.getInstance();
    private static final NttModelLoader WSC = NttModelLoader.getInstance();

    private ModelItem parent;
    private int sizeDiff;
    private ModelItem[] structure;
    private List<ModelItem> children;
    private MiAppendMethod appendMethod;
    private boolean mayContainReference = false;
    private ModelItemTextConverter converter;

    /**
     * Instantiates a new Text to mi converter.
     *
     * @param converter       the converter
     * @param parent          NonLeaf
     * @param isTemplatesTree the is templates tree
     */
    TextToMiConverter(ModelItemTextConverter converter, ModelItem parent, boolean isTemplatesTree) {
        this.converter = converter;
        sizeDiff = parent.getModelItemType().ordinal() + 1;
        //difference between length of ModelItemType.values() and structure
        structure = new ModelItem[ModelItemType.values().length - sizeDiff];//if parent = suite,
        // we have structure.length=3 to contain case, step and action
        children = new ArrayList<>();
        this.parent = parent;
        if (!isTemplatesTree && !(parent instanceof Reference || parent.isTemplate())) {
            mayContainReference = true;
        }
        appendMethod = parent instanceof TemplateProject ? appendToTemplatesProj : regularTree;
    }

    /**
     * Append mi.
     *
     * @param line the line
     */
    public void appendMi(String line) {
        //line = Template_TestStep: Template_del\TestStep.1 "TestStep.1"
        boolean isEnabled = true;
        //get rid of comments
        if (line.startsWith(ConverterConstants.COMMENT)) {
            isEnabled = false;
            line = line.substring(ConverterConstants.COMMENT.length()).trim();
        } else if (line.startsWith(ConverterConstants.OLD_COMMENT)) {
            isEnabled = false;
            line = line.substring(ConverterConstants.OLD_COMMENT.length()).trim();
        }
        if (line.isEmpty()) {
            return;
        }
        //get rid of flags
        String[] str = getFlags(line.trim());
        line = str[0];
        String flags = str[1];
        appendMethod.append(line, flags, isEnabled);
    }

    /**
     * Flush.
     */
    public void flush() {
        if (structure[0] != null) {
            children.add(structure[0]);
        }
        MIC.clear(parent);
        if (!children.isEmpty()) {
            WSC.addNode(parent, children.toArray(new TreeNode[0]));
            createScopeChanges();
        }
        if (appendMethod == appendToTemplatesProj) {
            //templates are changed, need to update references
            WSC.clearReferences();
        }
    }

    private void createScopeChanges() {
        int firstChildTypeOrdinal = children.get(0).getModelItemType().ordinal();
        ModelItemTextConverter.getChangesMapForScope().keySet()
                .removeIf(treeNode -> ((ModelItem) treeNode).getModelItemType().ordinal() >= firstChildTypeOrdinal);
        for (ScopeItem item : WSC.getScope().getScopeItems()) {
            TreeNode parent = item.getModelItem();
            if (parent == null) {
                continue;
            }
            if (((ModelItem) parent).getModelItemType().ordinal() < firstChildTypeOrdinal) {
                return;
            }
            List<String> path = new ArrayList<>();
            do {
                path.add(parent.getName());
                parent = parent.getParent();
            } while (parent != null && ((ModelItem) parent).getModelItemType().ordinal() >= firstChildTypeOrdinal);

            TreeNode found = getTnByPath(path, new ArrayList<>(children));
            if (found != null) {
                ModelItemTextConverter.getChangesMapForScope().put(item.getModelItem(), found);
            }
        }
    }

    private TreeNode getTnByPath(List<String> path, List<TreeNode> where) {
        for (TreeNode current : where) {
            if (current.getName().equals(path.get(path.size() - 1))) {
                path.remove(path.size() - 1);
                return path.isEmpty()
                        ? current
                        : getTnByPath(path, current.getChildren());
            }
        }
        return null;
    }

    private String[] getFlags(String line) {
        Matcher m = ConverterConstants.FLAGS.matcher(line);
        String flags = StringUtils.EMPTY;
        if (m.matches()) {
            flags = m.group(2);
            line = m.group(1);
        }
        return new String[]{line, flags.trim()};
    }

    private ModelItemType getMiEnum(String lineWithPrefix) {
        for (int i = 0; i < ConverterConstants.PREFIXES.length; i++) {
            if (lineWithPrefix.startsWith(ConverterConstants.PREFIXES[i])) {
                return ModelItemType.values()[i];
            }
        }
        return ModelItemType.ACTION;
    }

    private void setFlags(ModelItem item, String flags) {
        if (flags.isEmpty()) {
            return;
        }
        for (String s : flags.split(ConverterConstants.COMMA)) {
            s = s.trim();
            if (!s.isEmpty()) {
                item.setFlag(s, true);
            }
        }
    }

    private ModelItem createReference(ModelItemType modelItemType, String templProjName,
                                      String templName, String name, String flags, boolean isEnabled) {
        Reference ref = WSC.createReference(name, modelItemType, templProjName, templName);
        ref.setEnabled(isEnabled);
        setFlags(ref, flags);
        appendMethod.addNode(modelItemType, ref, false, true);
        return ref;
    }

    private MiAppendMethod appendToTemplatesProj = new MiAppendMethod() {

        @Override
        public void append(String line, String flags, boolean isEnabled) {
            boolean isTemplate = false;
            ModelItemType modelItemType = getMiEnum(line);
            if (line.startsWith(ConverterConstants.TEMPLATE_)) {
                ModelItemType tempMiEnum = getMiEnum(line.substring(ConverterConstants.TEMPLATE_.length()));
                if (!tempMiEnum.isLeaf()) {
                    line = line.substring(ConverterConstants.TEMPLATE_.length());
                    modelItemType = tempMiEnum;
                    isTemplate = true;
                }
            }
            line = line.substring(converter.getPrefix(modelItemType).length());
            if (parent.getModelItemType().ordinal() >= modelItemType.ordinal()) {
                //we don't need Project in TemplatesProject
                modelItemType = parent.getModelItemType().next();
            }
            if (!isTemplate && (structure[0] == null
                    || structure[0].getModelItemType().ordinal() >= modelItemType.ordinal())) {
                //can not put nonTemplate to TemplateProject, sorry.
                if (!modelItemType.isLeaf()) {
                    isTemplate = true;
                } else {
                    createMi(modelItemType.previous(),
                            modelItemType.previous().getName() + ".auto", StringUtils.EMPTY,
                            true, true);
                }
            }
            createMi(modelItemType, line, flags, isTemplate, isEnabled);
        }

        @Override
        public void addNode(ModelItemType modelItemType, ModelItem modelItem, boolean isTemplate, boolean isReference) {
            int index = isTemplate ? 0 : modelItemType.ordinal() - structure[0].getModelItemType().ordinal();
            TextToMiConverter.this.addNode(modelItemType, modelItem, index);
        }
    };

    private void addNode(ModelItemType modelItemType, ModelItem modelItem, int index) {
        if (index == 0) {
            //this is root of structure
            //will replace structure[0], need to save it
            if (structure[0] != null) {
                children.add(structure[0]);
            }
        } else {
            int parentIndex = index - 1;
            //need to add to the parent in structure
            if (structure[parentIndex] == null) {
                createMi(modelItemType.previous(),
                        modelItemType.previous().getName() + ".auto", StringUtils.EMPTY,
                        false, true); //there are action in suite, will fix it
            }
            structure[parentIndex].getChildren().add(modelItem);
            modelItem.setParent(structure[parentIndex]);
        }
        structure[index] = modelItem;
        for (int i = index + 1; i < structure.length; i++) {
            //if we added new parent there, his children must be null now
            structure[i] = null;
        }
    }

    private MiAppendMethod regularTree = new MiAppendMethod() {

        private ModelItemType currentReference = null;

        @Override
        public void append(String line, String flags, boolean isEnabled) {
            ModelItemType modelItemType = getMiEnum(line);
            if (!modelItemType.isLeaf()) {
                line = line.substring(converter.getPrefix(modelItemType).length());
                if (parent.getModelItemType().ordinal() >= modelItemType.ordinal()) {
                    //we don't need testCase in testCase
                    modelItemType = parent.getModelItemType().next();
                }
                if (mayContainReference && line.startsWith(ConverterConstants.TEMPLATE_)
                        && canItBeReference(modelItemType)) {
                    //try to find name of reference
                    String refLine = line.substring(ConverterConstants.TEMPLATE_.length());
                    String name = StringUtils.EMPTY;
                    int nameLastIndex = refLine.lastIndexOf(ConverterConstants.QUOTE);
                    if (nameLastIndex != -1) {
                        int nameBeginIndex = refLine.substring(0, nameLastIndex).lastIndexOf(ConverterConstants.QUOTE);
                        if (nameBeginIndex != -1) {
                            name = refLine.substring(nameBeginIndex + 1, nameLastIndex);
                            refLine = refLine.substring(0, nameBeginIndex);
                        }
                    }
                    int delimIndex = refLine.indexOf(ConverterConstants.REFDELIMETER);
                    if (delimIndex != -1) {
                        //its totally reference
                        String templProjName = refLine.substring(0, delimIndex);
                        String templName = refLine.substring(templProjName.length() + 1);
                        createReference(modelItemType, templProjName, templName.trim(), name, flags, isEnabled);
                        return;
                    }
                }
            }
            if (currentReference == null || modelItemType.ordinal() <= currentReference.ordinal()) {
                createMi(modelItemType, line, flags, false, isEnabled);
            }
            //else it will be placed in Reference, but all children of reference going from its template
        }

        @Override
        public void addNode(ModelItemType modelItemType, ModelItem modelItem, boolean isTemplate, boolean isReference) {
            int index = modelItemType.ordinal() - sizeDiff; //its index in structure
            TextToMiConverter.this.addNode(modelItemType, modelItem, index);
            if (isReference) {
                currentReference = modelItemType;
            } else if (currentReference != null && modelItemType.ordinal() <= currentReference.ordinal()) {
                //reference no more in the structure
                currentReference = null;
            }
        }

        private boolean canItBeReference(ModelItemType miEnum) {
            if (currentReference != null) {
                return miEnum.ordinal() <= currentReference.ordinal(); //will be added outside currentReference
            }
            return true;
        }
    };

    private ModelItem createMi(ModelItemType modelItemType, String name, String flags,
                               boolean isTemplate, boolean isEnabled) {
        ModelItem modelItem;
        if (modelItemType.isLeaf()) {
            modelItem = WSC.createTestAction(name, name);
        } else {
            modelItem = MIC.createSomeThing(modelItemType.getInterface(), name);
            if (isTemplate) {
                modelItem = WSC.createTemplate(modelItem);
            }
        }
        modelItem.setEnabled(isEnabled);
        setFlags(modelItem, flags);
        appendMethod.addNode(modelItemType, modelItem, isTemplate, false);
        return modelItem;
    }

}
