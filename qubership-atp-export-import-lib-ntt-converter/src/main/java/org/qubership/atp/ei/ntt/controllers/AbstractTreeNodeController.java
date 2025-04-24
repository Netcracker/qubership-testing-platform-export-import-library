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
import java.util.Vector;

import org.qubership.atp.ei.ntt.controllers.impl.DefaultTreeNodeFactory;
import org.qubership.atp.ei.ntt.controllers.impl.DefaultTreeNodeFactoryDetector;
import org.qubership.atp.ei.ntt.model.TreeNode;

public abstract class AbstractTreeNodeController {

    public static final TreeNodeFactory DEFAULT_FACTORY = DefaultTreeNodeFactory.INSTANCE;
    private TreeNodeFactoryDetector factoryDetector = new DefaultTreeNodeFactoryDetector();


    public void clear(TreeNode item, Object... helpArgs) {
        remove(item.getChildren().toArray(new TreeNode[item.getChildren().size()]), helpArgs);
    }

    /**
     * Remove.
     *
     * @param item     the item
     * @param helpArgs the help args
     */
    public void remove(TreeNode[] item, Object... helpArgs) {
        if (item.length > 0) {
            TreeNode parent = item[0].getParent();
            if (parent != null) {
                List<TreeNode> childs = parent.getChildren();
                Vector<Integer> itemsForRemove = new Vector<>(1, 1);
                for (int i = 0; i < item.length; i++) {
                    int index = childs.indexOf(item[i]);
                    if (index != -1) {
                        itemsForRemove.add(index);
                    }
                }
                int offset = 0;
                int[] removedItems = new int[itemsForRemove.size()];
                for (Integer index : itemsForRemove) {
                    childs.remove(index - offset);
                    removedItems[offset] = index;
                    offset++;
                }
            }
        }
    }

    public <T extends TreeNode> T create(Class<T> clazz, String name, Object... helpArgs) {
        return factoryDetect(clazz, name, helpArgs).create(clazz, name, helpArgs);
    }

    protected TreeNodeFactory factoryDetect(Class<? extends TreeNode> clazz, String by, Object... helpArgs) {
        TreeNodeFactory factory = getFactoryDetector().detect(clazz, by, helpArgs);
        if (factory == null) {
            factory = getDefaultFactory();
        }
        return factory;
    }

    public static TreeNodeFactory getDefaultFactory() {
        return DEFAULT_FACTORY;
    }

    public TreeNodeFactoryDetector getFactoryDetector() {
        return factoryDetector;
    }

    /**
     * Add.
     *
     * @param parent   the parent
     * @param child    the child
     * @param helpArgs the help args
     */
    public void add(TreeNode parent, TreeNode[] child, Object... helpArgs) {

        int offset;
        if (helpArgs != null && helpArgs.length > 0 && helpArgs[0] instanceof Integer) {
            offset = (Integer) helpArgs[0];
        } else {
            offset = parent.getChildren().size();
        }
        int[] indices = new int[child.length];
        for (int i = 0; i < child.length; i++) {
            int index = offset + i;
            parent.getChildren().add(index, child[i]);
            child[i].setParent(parent);
            indices[i] = index;
        }
    }
}
