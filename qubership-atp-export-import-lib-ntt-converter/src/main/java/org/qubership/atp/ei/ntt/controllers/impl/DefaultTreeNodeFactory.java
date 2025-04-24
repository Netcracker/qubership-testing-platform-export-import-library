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

package org.qubership.atp.ei.ntt.controllers.impl;

import java.util.HashMap;
import java.util.Map;

import org.qubership.atp.ei.ntt.controllers.TreeNodeFactory;
import org.qubership.atp.ei.ntt.model.GenericItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TemplateProject;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.impl.ProjectModel;
import org.qubership.atp.ei.ntt.model.impl.ReferenceModelItem;
import org.qubership.atp.ei.ntt.model.impl.TemplateModel;
import org.qubership.atp.ei.ntt.model.impl.TemplateProjectModel;
import org.qubership.atp.ei.ntt.model.impl.TestActionModel;
import org.qubership.atp.ei.ntt.model.impl.TestCaseModel;
import org.qubership.atp.ei.ntt.model.impl.TestStepModel;
import org.qubership.atp.ei.ntt.model.impl.TestSuiteModel;
import org.qubership.atp.ei.ntt.settings.ReflectionUtils;

public class DefaultTreeNodeFactory implements TreeNodeFactory {

    public static final DefaultTreeNodeFactory INSTANCE = new DefaultTreeNodeFactory();

    protected DefaultTreeNodeFactory() {
    }

    private Map<Class<? extends GenericItem>, Class<? extends TreeNode>> implClasses = new HashMap<>();

    {
        implClasses.put(Project.class, ProjectModel.class);
        implClasses.put(TemplateProject.class, TemplateProjectModel.class);

        implClasses.put(TestSuite.class, TestSuiteModel.class);
        implClasses.put(NttTestCase.class, TestCaseModel.class);
        implClasses.put(TestStep.class, TestStepModel.class);
        implClasses.put(TestAction.class, TestActionModel.class);

        implClasses.put(Reference.class, ReferenceModelItem.class);
        implClasses.put(Template.class, TemplateModel.class);
    }

    @Override
    public TreeNode create(String name, Object... helpArgs) {
        return null;
    }

    @Override
    public TreeNode create(Class clazz, String name, Object... helpArgs) {
        Class<? extends TreeNode> implClazz = getImplClazz(clazz);
        TreeNode instance = ReflectionUtils.getInstance(implClazz);
        instance.setName(name);
        return instance;
    }

    @Override
    public TreeNode create(TreeNode parent, String name, Object... helpArgs) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends TreeNode> getImplClazz(Class<TreeNode> clazz) {
        Class<? extends TreeNode> implClazz = implClasses.get(clazz);
        return implClazz == null ? clazz : implClazz;
    }
}
