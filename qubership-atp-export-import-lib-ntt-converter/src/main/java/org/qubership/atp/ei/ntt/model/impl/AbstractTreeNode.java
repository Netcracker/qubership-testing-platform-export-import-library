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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qubership.atp.ei.ntt.flag.Flag;
import org.qubership.atp.ei.ntt.flag.Option;
import org.qubership.atp.ei.ntt.model.TreeNode;

/**
 * TreeNode item abstraction.
 *
 * @version 1.4.3
 */
@SuppressWarnings({"serial"})
public abstract class AbstractTreeNode extends AbstractGenericItem implements TreeNode {

    private static final Option<?>[] NO_OPTIONS = {};
    protected TreeNode parent;
    protected List<? extends TreeNode> children = new ArrayList<>();
    private Map<String, Flag> flags = new HashMap<>();


    /**
     * {@inheritDoc}
     */
    @Override
    public TreeNode getParent() {

        return this.parent;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(TreeNode parent) {

        this.parent = parent;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAncestorFor(TreeNode node) {

        TreeNode item = getParent();
        for (; item != null; item = item.getParent()) {
            if (this == item) {
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends TreeNode> List<T> getChildren() {

        return (List<T>) children;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends TreeNode> void setChildren(List<T> children) {

        this.children = children;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlag(String flag) {

        setFlag(flag, true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlag(String flag, boolean enabled) {

        if (hasFlag(flag)) {
            flags.get(flag).setEnabled(enabled);
        } else if (enabled) {
            flags.put(flag, new Flag(flag));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFlag(String flag) {

        return flags.containsKey(flag) && flags.get(flag).isEnabled();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getFlags() {

        List<String> list = new ArrayList<>(flags.size());
        for (String flag : flags.keySet()) {
            if (getFlag(flag)) {
                list.add(flag);
            }
        }
        return list.toArray(new String[list.size()]);
    }


    private boolean hasFlag(String flag) {

        return flags.containsKey(flag);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void setFlagOption(String flag, String option, T optionValue) {

        if (hasFlag(flag)) {
            flags.get(flag).setOptionValue(option, optionValue);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getFlagOption(String flag, String option) {

        if (hasFlag(flag)) {
            return flags.get(flag).getOptionValue(option);
        } else {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlagOptionString(String flag, String option) {

        return getFlagOption(flag, option).toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Option<?>[] getFlagOptions(String flag) {

        if (hasFlag(flag)) {
            return flags.get(flag).getOptions();
        } else {
            return NO_OPTIONS;
        }
    }
}
