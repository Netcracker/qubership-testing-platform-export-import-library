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

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;

public final class ConverterConstants {

    public static final String COLON_SPACE = ": ";

    public static final String TEMPLATE_ = "Template_";

    public static final char ENDL = '\n';

    public static final String SPACE = StringUtils.EMPTY;

    public static final String COMMA = ",";

    public static final String R_BRACKET = ")";

    public static final String QUOTE = "\"";

    public static final String OLD_COMMENT = "!--";

    public static final String COMMENT = "//";

    public static final String REFDELIMETER = "\\";

    public static final Pattern FLAGS = Pattern.compile("(.+?)\\s*;\\s*flags\\s*=(.*)");

    public static final String[] PREFIXES = new String[]{

            ModelItemType.PROJECT.getName() + COLON_SPACE,
            ModelItemType.SUITE.getName() + COLON_SPACE,
            ModelItemType.CASE.getName() + COLON_SPACE,
            ModelItemType.STEP.getName() + COLON_SPACE,
            StringUtils.EMPTY};

}
