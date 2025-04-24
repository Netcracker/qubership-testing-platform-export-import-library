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

package org.qubership.atp.ei.ntt.controllers;

import java.util.List;

import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.TreeNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelItemController extends AbstractTreeNodeController {

    private static ModelItemController instance = null;

    private ModelItemController() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized ModelItemController getInstance() {
        if (instance == null) {
            instance = new ModelItemController();
        }
        return instance;
    }

    public <T extends TreeNode> T createSomeThing(Class<T> clazz, String name, Object... helpArgs) {
        return create(clazz, name, helpArgs);
    }

    /**
     * Calculate new node name.
     *
     * @param node     the node
     * @param parent   the parent
     * @param helpArgs the help args
     */
    public void calculateNewNodeName(TreeNode node, TreeNode parent, Object... helpArgs) {
        if (parent == null) {
            return;
        }

        String prefix;
        List<? extends TreeNode> nodeList;
        if (node instanceof DataSet) {
            String name;
            if (parent.getName() == null) {
                name = "default";
            } else {
                name = parent.getName();
            }
            prefix = name + ".DataSet";
            nodeList = ((Project) parent).getDataSets();
        } else {
            prefix = getDefaultChildPrefix(parent);
            nodeList = parent.getChildren();
        }
        String newName;

        if (helpArgs != null && helpArgs.length > 0 && helpArgs[0] instanceof Integer) {
            newName = prefix + '.' + helpArgs[0];
        } else {
            boolean unique;
            int index = 1;
            do {
                newName = prefix + '.' + index;
                unique = true;
                for (TreeNode someNode : nodeList) {
                    if (newName.equals(someNode.getName())) {
                        index++;
                        unique = false;
                        break;
                    }
                }
            } while (!unique);
        }
        node.setName(newName);
    }

    /**
     * Gets default child prefix.
     *
     * @param parent the parent
     * @return the default child prefix
     */
    public String getDefaultChildPrefix(TreeNode parent) {
        if (parent instanceof ModelItem) {
            return ((ModelItem) parent).getModelItemType().next().getName();
        }
        return null;
    }
}
