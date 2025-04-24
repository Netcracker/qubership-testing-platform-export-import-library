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

import org.qubership.atp.ei.ntt.model.TreeNode;

public interface TreeNodeFactoryDetector {

    TreeNodeFactory detect(Class<? extends TreeNode> clazz, Object... helpArgs);

    TreeNodeFactory detect(String by, Object... helpArgs);

    TreeNodeFactory detect(Class<? extends TreeNode> clazz, String by, Object... helpArgs);

    TreeNodeFactory detect(TreeNode parent, String by, Object... helpArgs);
}