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

package org.qubership.atp.ei.ntt.model.enums;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;

import com.google.common.base.Preconditions;

/**
 * The enum Model item type.
 *
 * @author A.Kolosov
 * @author B.Kuznetsov
 * @since 20.11.2015
 */
public enum ModelItemType {

    PROJECT(Project.class, "Project"),
    SUITE(TestSuite.class, "TestSuite"),
    CASE(NttTestCase.class, "TestCase"),
    STEP(TestStep.class, "TestStep"),
    ACTION(TestAction.class, "TestAction");

    private Class<? extends ModelItem> clazz;
    private String name;

    ModelItemType(Class<? extends ModelItem> clazz, String name) {

        this.clazz = clazz;
        this.name = name;
    }

    public Class<? extends ModelItem> getInterface() {

        return clazz;
    }

    public String getName() {

        return name;
    }

    public ModelItemType next() {

        return ordinal() + 1 >= values().length ? values()[ordinal()] : values()[ordinal() + 1];
    }

    public ModelItemType previous() {

        return ordinal() - 1 < 0 ? values()[0] : values()[ordinal() - 1];
    }

    /**
     * Get model item type.
     *
     * @param name the name
     * @return the model item type
     */
    @Nullable
    public static ModelItemType get(@Nonnull String name) {

        for (ModelItemType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    public boolean isLeaf() {

        return ordinal() == values().length - 1;
    }

    public boolean isRoot() {

        return ordinal() == 0;
    }

    /**
     * looking for a parent instance of the passed ModelItem.
     * !do not use it to navigate through templates.
     *
     * @param <T>      should match the interface (see{@link #getInterface()}) of current enum
     * @param instance - some modelItem
     * @return parent instance of current type
     * @throws IllegalArgumentException if you has mixed up the inheritance (trying to get a suite from a step for ex.)
     * @throws ClassCastException       if you specify the wrong generic type <b>T</b>
     */
    @Nonnull
    public <T extends ModelItem> T from(@Nonnull ModelItem instance) {
        ModelItemType instanceType = instance.getModelItemType();
        Preconditions.checkArgument(instanceType.ordinal() >= this.ordinal(),
                "[%s] could not be a parent of [%s]{%s}", this, instanceType, instance);
        int advance = instanceType.ordinal() - this.ordinal();
        for (int i = 0; i < advance; i++) {
            instance = (ModelItem) instance.getParent();
        }
        return (T) clazz.cast(instance);
    }
}
