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

package org.qubership.atp.ei.ntt.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TemplateProject;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.TreeNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract item of workspace model structure.
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
@Slf4j
public abstract class AbstractModelItem extends AbstractTreeNode implements ModelItem {

    private boolean isEnable = true;
    private boolean isWanted = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public Project getProject() {

        if (this instanceof Project) {
            return (Project) this;
        }

        TreeNode item = getParent();
        for (; !(item instanceof Project) && item != null; item = item.getParent()) {
        }
        return (Project) item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTemplate() {

        TreeNode tmp = this;
        while (tmp != null) {
            if (tmp instanceof Template) {
                return true;
            }
            tmp = tmp.getParent();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    //FIXME: templates have links to Template Project only and not to Reference object
    @Override
    public boolean isReference() {

        TreeNode tmp = this;
        while (tmp != null) {
            if (tmp instanceof Reference) {
                return true;
            }
            tmp = tmp.getParent();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Template getTemplate() {

        TreeNode tmp = this;
        while (tmp != null) {
            if (tmp instanceof Template) {
                return (Template) tmp;
            }
            if (tmp instanceof ReferenceModelItem) {
                return ((ReferenceModelItem) tmp).getTemplate();
            }

            tmp = tmp.getParent();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {

        return isEnable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnable) {

        this.isEnable = isEnable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWanted() {

        return isWanted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWanted(boolean isWanted) {

        this.isWanted = isWanted;
    }

    /**
     * Model item helper.
     * <p>
     * The Helper provides model item checking for assigning as a child to some parent model item.
     * </p>
     */
    public static class Helper {

        private static final Multimap<Class<? extends ModelItem>, Class<? extends ModelItem>>
                parentAcceptsChild = ArrayListMultimap.create(10, 5);

        static {
            parentAcceptsChild.put(TestStep.class, TestAction.class);
            parentAcceptsChild.put(NttTestCase.class, TestStep.class);
            parentAcceptsChild.put(TestSuite.class, NttTestCase.class);
            parentAcceptsChild.put(Project.class, TestSuite.class);
            parentAcceptsChild.put(TemplateProject.class, Template.class);
            parentAcceptsChild.put(NttTestCase.class, Reference.class);
            parentAcceptsChild.put(TestSuite.class, Reference.class);
            parentAcceptsChild.put(Project.class, Reference.class);
        }

        /**
         * Returns {@code true} if specified model item can be assigned as child to some parent model item.
         *
         * @param child          Child model item.
         * @param possibleParent Parent model item.
         * @return Result. True if operation allowed.
         */
        public static boolean childCanBeAccepted(ModelItem child, ModelItem possibleParent) {

            if (child == null) {
                return false;
            }
            Class<? extends ModelItem> possibleParentClass = possibleParent.getClass() == Reference.class
                    ? possibleParent.getClass()
                    : possibleParent.getClass();

            Class<? extends ModelItem> parClass = null;
            for (Class<? extends ModelItem> parentClass : parentAcceptsChild.keySet()) {
                if (parentClass.isAssignableFrom(possibleParentClass)) {
                    boolean toBeBroken = false;
                    for (Class<? extends ModelItem> superParentClass : parentAcceptsChild.keySet()) {
                        if (superParentClass.isAssignableFrom(parentClass) && !superParentClass.equals(parentClass)) {
                            toBeBroken = true;
                            break;
                        }
                    }
                    parClass = parentClass;
                    if (toBeBroken) {
                        break;
                    }
                }
            }
            Class childClass = child.getClass() == ReferenceModelItem.class
                    ? child.getTemplate().getModelItem().getClass()
                    : child.getClass();
            for (Class<? extends ModelItem> assClass : parentAcceptsChild.get(parClass)) {
                if (assClass.isAssignableFrom(childClass)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.error("Sleep has been interrupted", e);
                    }
                    return true;
                }
            }
            return false;
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends TestAction> getActions() {

        return getAllActionsFromModelItem(this);
    }


    private List<TestAction> getAllActionsFromModelItem(ModelItem item) {

        List<TestAction> results = new ArrayList<>();
        for (TreeNode node : item.getChildren()) {
            if (node instanceof TestAction) {
                results.add((TestActionModel) node);
            } else {
                results.addAll(getAllActionsFromModelItem((ModelItem) node));
            }
        }
        return results;
    }


}
