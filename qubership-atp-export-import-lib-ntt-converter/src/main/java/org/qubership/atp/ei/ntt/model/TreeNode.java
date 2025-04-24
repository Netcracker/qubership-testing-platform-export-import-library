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

package org.qubership.atp.ei.ntt.model;

import java.util.List;

import org.qubership.atp.ei.ntt.flag.Option;

/**
 * TreeNode interface.
 *
 * @version 1.4.3
 */
public interface TreeNode extends GenericItem {

    /**
     * Returns a parent of the tree node.
     *
     * @return TreeNode object.
     */
    TreeNode getParent();

    /**
     * Sets specified parent for the tree node.
     *
     * @param parent Parent TreeNode.
     */
    void setParent(TreeNode parent);

    /**
     * Returns true if specified TreeNode is child of the current tree node.
     *
     * @param node Checking tree node.
     * @return True if argument node is child of the node.
     */
    boolean isAncestorFor(TreeNode node);

    /**
     * Returns list of children of the TreeNode.
     *
     * @param <T> Generic type of a child.
     * @return List of children.
     */
    <T extends TreeNode> List<T> getChildren();

    /**
     * Sets list of children of the TreeNode.
     *
     * @param children List of children.
     * @param <T>      Generic type of child.
     */
    <T extends TreeNode> void setChildren(List<T> children);

    /**
     * Sets a flag on the TreeNode.
     *
     * @param flag Type of flag.
     *
     */
    void setFlag(String flag);

    /**
     * Sets state of existing flag.
     *
     * @param flag    Name of flag.
     * @param enabled State: true - enabled, false - disabled.
     */
    void setFlag(String flag, boolean enabled);

    /**
     * Returns true if specified flag applied to the TreeNode.
     *
     * @param flag Name of flag.
     * @return True if flag applied.
     */
    boolean getFlag(String flag);

    /**
     * Returns an array with flags applied to the TreeNode.
     *
     * @return Array of flags.
     * @see
     */
    String[] getFlags();

    /**
     * Sets an option of a flag.
     *
     * @param flag        The flag.
     * @param option      Name of the option.
     * @param optionValue Value of the option.
     * @param <T>         Generic type of the option.
     * @see
     */
    <T> void setFlagOption(String flag, String option, T optionValue);

    /**
     * Returns option of flag.
     *
     * @param flag   The flag.
     * @param option Name of the option.
     * @param <T>    Generic type of the option.
     * @return Option object.
     */
    <T> T getFlagOption(String flag, String option);

    /**
     * Returns flag option as string.
     *
     * @param flag   The flag.
     * @param option The option name.
     * @return Flag option as string.
     */
    String getFlagOptionString(String flag, String option);

    /**
     * Returns all options of specified flag.
     *
     * @param flag The flag.
     * @return Array with options of the flag.
     */
    Option<?>[] getFlagOptions(String flag);

}
