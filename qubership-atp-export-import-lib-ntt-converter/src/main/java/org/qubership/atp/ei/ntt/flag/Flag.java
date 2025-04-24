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

package org.qubership.atp.ei.ntt.flag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flag class.
 * <p>
 * The flags are applicable for model items. They allow auxiliary functionality for test case execution control.
 * </p>
 *
 * @version 1.4.3
 */
@SuppressWarnings("serial")
public class Flag implements Serializable {

    public static final String REMOTE = "Remote execution";
    public static final String SKIP_IF_ANY_PREV_EXCEPTION = "Skip if an exception has occurred in any previous item";
    public static final String STOP_ON_EXCEPTION = "Stop if an exception has occurred";
    public static final String STORE_RESULT_TO_CONTEXT = "Store result to context";
    public static final String SKIP_IF_STORED_FAIL = "Skip if the stored result was 'Failed' or 'Skipped'";
    public static final String STOP_IF_EXCEPTION_ON_ITEM = "Stop if an exception has occurred on this item";
    public static final String WARN_IF_FAIL = "Warn if fail";
    public static final String NEED_TO_REMOVE = "Need to remove this after complete";

    private static List<String> customFlags = Collections.synchronizedList(new ArrayList<String>());

    private String name;
    private Map<String, Option<?>> options = new HashMap<>();
    private boolean enabled;

    /**
     * Adds a custom flag in set of available flags.
     *
     * @param flag Custom flag name.
     */
    public static void addCustomFlag(String flag) {

        synchronized (customFlags) {
            if (!customFlags.contains(flag)) {
                customFlags.add(flag);
            }
        }
    }

    /**
     * Returns list of available custom flags.
     *
     * @return List of custom flags.
     */
    public static List<String> getCustomFlags() {

        synchronized (customFlags) {
            return customFlags;
        }
    }

    /**
     * Removes specified flag from custom flags set.
     *
     * @param flag Flag for removing.
     */
    public static void removeCustomFlag(String flag) {

        synchronized (customFlags) {
            if (customFlags.contains(flag)) {
                customFlags.remove(flag);
            }
        }
    }

    /**
     * Removes all available custom flags.
     */
    public static void clearCustomFlags() {

        synchronized (customFlags) {
            customFlags.clear();
        }
    }

    /**
     * Returns list of all flags (both predefined and custom).
     *
     * @return List of all available flags.
     */
    public static List<String> getFlagsScope() {

        List<String> allFlags = new ArrayList<>(Arrays.asList(REMOTE,
                SKIP_IF_ANY_PREV_EXCEPTION,
                STOP_IF_EXCEPTION_ON_ITEM,
                STOP_ON_EXCEPTION,
                STORE_RESULT_TO_CONTEXT,
                SKIP_IF_STORED_FAIL));
        synchronized (customFlags) {
            allFlags.addAll(customFlags);
        }
        return allFlags;
    }

    /**
     * Constructor of the flag object.
     *
     * @param name Name of the flag.
     */
    public Flag(String name) {

        this.name = name;
        enabled = true;
    }

    /**
     * Returns {@code true} if the flag is enabled.
     *
     * @return True if enabled.
     */
    public boolean isEnabled() {

        return enabled;
    }

    /**
     * Sets state of the flag (enabled or desabled).
     *
     * @param enabled State flag of the flag.
     */
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    /**
     * Returns true if the flag contains additional option.
     *
     * @param option The option name.
     * @return True if it contains option.
     */
    public boolean hasOption(String option) {

        return options.containsKey(option);
    }

    /**
     * Returns option value of the flag (if it contains the option).
     *
     * @param option Option name.
     * @param <T>    Type of the value.
     * @return Value of the option.
     */
    @SuppressWarnings("unchecked")
    public <T> T getOptionValue(String option) {

        if (hasOption(option)) {
            return (T) getOption(option).getValue();
        } else {
            return null;
        }
    }

    /**
     * Returns an option object.
     *
     * @param option Option name.
     * @param <T>    Type of option.
     * @return Option object.
     */
    @SuppressWarnings("unchecked")
    public <T> Option<T> getOption(String option) {

        return (Option<T>) options.get(option);
    }

    /**
     * Sets an option value.
     *
     * @param option Option name.
     * @param value  Value of generic type.
     * @param <T>    Generic type of option.
     */
    public <T> void setOptionValue(String option, T value) {

        if (hasOption(option)) {
            getOption(option).setValue(value);
        } else {
            Option<T> optionO = new Option<>(option, value);
            options.put(option, optionO);
        }
    }

    /**
     * Returns name of the flag.
     *
     * @return Name of the flag.
     */
    public String getName() {

        return name;
    }

    /**
     * Returns all available options of the flag.
     *
     * @return Options of the flag.
     */
    public Option<?>[] getOptions() {

        Option<?>[] arr = new Option[options.size()];
        return options.values().toArray(arr);
    }

    @Override
    public String toString() {

        if (options.isEmpty()) {
            return name;
        } else {
            return String.format("%s %s", name, Arrays.toString(getOptions()));
        }
    }
}
