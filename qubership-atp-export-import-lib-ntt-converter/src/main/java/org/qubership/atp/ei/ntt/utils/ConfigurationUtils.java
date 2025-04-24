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

package org.qubership.atp.ei.ntt.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.DataSet;


public class ConfigurationUtils {

    /**
     * Write text.
     *
     * @param writer the writer
     * @param text   the text
     * @throws IOException the io exception
     */
    public static void writeText(@Nonnull Writer writer, @Nonnull String text) throws IOException {
        writer.write(text);
        writer.flush();
        writer.close();
    }

    /**
     * Write data sets.
     *
     * @param writer   the writer
     * @param dataSets the data sets
     * @throws IOException the io exception
     */
    public static void writeDataSets(@Nonnull Writer writer, @Nonnull List<DataSet> dataSets) throws IOException {
        for (DataSet dataSet : dataSets) {
            writer.write("dsName=" + dataSet.getName() + StringUtils.LF + StringUtils.LF);
            for (ContextVariable variable : dataSet.getVariables()) {
                writer.write("name=" + variable.getName() + "; description="
                        + variable.getDescription() + "; value=" + variable.getValue() + StringUtils.LF);
            }
            writer.write(StringUtils.LF);
        }

        writer.flush();
    }

}
