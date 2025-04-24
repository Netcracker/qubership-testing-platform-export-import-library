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

package org.qubership.atp.ei.ntt.converter;

import javax.annotation.Nonnull;

/**
 * The type Read only text js mapping.
 *
 * @author Boris Kuznetsov
 * @since 29.07.2016
 */
public class ReadOnlyTextJsMapping {

    private StringBuilder sb;
    private JsRanges ranges;
    private boolean wasReadOnly;
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    ReadOnlyTextJsMapping() {

        this.sb = new StringBuilder();
        this.ranges = new JsRanges();
        startRow = 0;
        startCol = 0;
        endRow = 0;
        endCol = 0;
        wasReadOnly = false;
    }

    public ReadOnlyTextJsMapping append(@Nonnull String text) {
        return append(text, false);
    }

    /**
     * Append read only text js mapping.
     *
     * @param text       the text
     * @param isReadOnly the is read only
     * @return the read only text js mapping
     */
    public ReadOnlyTextJsMapping append(@Nonnull String text, boolean isReadOnly) {

        if (text.isEmpty()) {
            return this;
        }

        sb.append(text);
        if (isReadOnly) {
            endCol += text.length();
        } else {
            if (wasReadOnly) {
                ranges.addRange(startRow, startCol, endRow, endCol);
            }
            endCol += text.length();
            startCol = endCol;
            startRow = endRow;
        }
        wasReadOnly = isReadOnly;
        return this;
    }

    /**
     * Append new line read only text js mapping.
     *
     * @return the read only text js mapping
     */
    public ReadOnlyTextJsMapping appendNewLine() {

        sb.append(ConverterConstants.ENDL);
        endRow++;
        endCol = 0;
        if (!wasReadOnly) {
            startRow = endRow;
            startCol = endCol;
        }

        return this;
    }

    /**
     * Gets read only ranges.
     *
     * @return the read only ranges
     */
    public String getReadOnlyRanges() {

        if (wasReadOnly) {
            ranges.addRange(startRow, startCol, endRow, endCol);
            startCol = endCol;
            startRow = endRow;
            wasReadOnly = false;
        }
        return ranges.toString();
    }

    public String getText() {

        return sb.toString();
    }
}
