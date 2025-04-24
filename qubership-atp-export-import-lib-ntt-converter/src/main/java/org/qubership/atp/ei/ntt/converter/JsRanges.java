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

import java.util.ArrayList;

/**
 * The type Js ranges.
 *
 * @author Boris Kuznetsov
 * @since 29.07.2016
 */
final class JsRanges {

    private ArrayList<int[]> ranges;

    public JsRanges() {

        ranges = new ArrayList<>();
    }

    public void addRange(int startRow, int startCol, int endRow, int endCol) {

        ranges.add(new int[]{startRow, startCol, endRow, endCol});
    }

    public String toString() {

        StringBuilder rangesSb = new StringBuilder("new Array(");
        for (int i = 0; i < ranges.size(); i++) {
            if (i != 0) {
                rangesSb.append(ConverterConstants.COMMA);
            }
            int[] range = ranges.get(i);
            rangesSb.append("new Range(");
            for (int j = 0; j < range.length; j++) {
                if (j != 0) {
                    rangesSb.append(ConverterConstants.COMMA);
                }
                rangesSb.append(range[j] == -1 ? "Infinity" : range[j]);
            }
            rangesSb.append(ConverterConstants.R_BRACKET);
        }
        rangesSb.append(ConverterConstants.R_BRACKET);

        return rangesSb.toString();
    }
}
