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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.dto.Action;
import org.qubership.atp.ei.ntt.dto.ActionParameter;
import org.qubership.atp.ei.ntt.dto.ComplexActionParameter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VariablesCompiler {

    private static final String STRING_PARAM_REGEXP = "\\$\\{([a-zA-Z0-9_.]*)\\}";
    private static final String ARRAY_REGEXP = "\\(([a-zA-Z0-9_${}'\"\\s,]*)\\)";
    private static final String MAP_REGEXP = "\\(([a-zA-Z0-9_${}='\"\\s,]*)\\)";

    private static final Pattern STRING_PARAM_PATTERN = Pattern.compile(STRING_PARAM_REGEXP);
    private static final Pattern ARRAY_PATTERN = Pattern.compile(ARRAY_REGEXP);
    private static final Pattern MAP_PATTERN = Pattern.compile(MAP_REGEXP);

    private static final String FILE_NAME_REGEX = "\\/attachment\\/\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4"
            + "}-\\b[0-9a-f]{12}\\b(\\/)?";

    private static final String UUID_REGEX = "(\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12"
            + "}\\b)";
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(FILE_NAME_REGEX);
    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

    // need for action with name "Set default UI type to "[GWT | ROE | TUI | CBC | CBTUI | LR]""
    private static final String PIPE = "\\|";
    private static final String OPEN_SQUARE_BRACKET = "\\[";
    private static final String CLOSE_SQUARE_BRACKET = "\\]";
    private static final String DOLLAR_SIGN = "$";
    private static final String SCREENED_DOLLAR_SIGN = "\\$";
    private static final String SCREENED_PIPE = "\\\\|";
    private static final String SCREENED_OPEN_SQUARE_BRACKET = "\\\\[";
    private static final String SCREENED_CLOSE_SQUARE_BRACKET = "\\\\]";

    // need for action with name "Automatic choosing UI type "True\\False""
    private static final String DOUBLE_BACKSLASH = "\\\\\\\\";
    private static final String SCREENED_DOUBLE_BACKSLASH = "\\\\\\\\\\\\\\\\";

    /**
     * Check name by pattern.
     */
    public static boolean isFile(String name) {
        Matcher stringMatcher = FILE_NAME_PATTERN.matcher(name);
        return stringMatcher.matches();
    }

    /**
     * Get fileId by pattern.
     */
    public static String getFileId(String name) {
        Matcher stringMatcher = UUID_PATTERN.matcher(name);
        if (stringMatcher.find()) {
            return stringMatcher.group(1);
        }
        return name;
    }

    /**
     * Precompile ActionEntity name:
     * Put action parameters into replacers by Rules,
     * Simple parameters: "param", 'param', [param],
     * Complex parameters:
     * Map parameter: ("name"='value')
     * Array parameter: ("param").
     *
     * @param actionEntity some action.
     */
    public Action precompileStringParameters(Action actionEntity) {
        String name = actionEntity.getName();
        Matcher arrayMatcher = ARRAY_PATTERN.matcher(name);
        Matcher mapMatcher = MAP_PATTERN.matcher(name);
        actionEntity.getParameters().forEach(param -> {
            if (param.getComplexParam() == null) {
                precompileParameterValue(actionEntity, param);
            } else {
                precompileComplexParameterValue(actionEntity, arrayMatcher, param);
                precompileComplexParameterValue(actionEntity, mapMatcher, param);
            }
        });
        return actionEntity;
    }

    private void precompileParameterValue(Action actionEntity,
                                          ActionParameter actionParameter) {
        String screenedActionParameter = actionParameter.getName()
                .replaceAll(PIPE, SCREENED_PIPE)
                .replaceAll(OPEN_SQUARE_BRACKET, SCREENED_OPEN_SQUARE_BRACKET)
                .replaceAll(CLOSE_SQUARE_BRACKET, SCREENED_CLOSE_SQUARE_BRACKET)
                .replaceAll(DOUBLE_BACKSLASH, SCREENED_DOUBLE_BACKSLASH)
                .replace(DOLLAR_SIGN, SCREENED_DOLLAR_SIGN);
        String stringInQuotes = "[\\\"|\\'](" + screenedActionParameter + ")[|\\\"|\\']";
        String stringInSquareBrackets = "[\\[](" + screenedActionParameter + ")[\\]]";
        String stringInTriangularBrackets = "[\\<](" + screenedActionParameter + ")[\\>]";
        Pattern patternInQuotes = Pattern.compile(stringInQuotes);
        Pattern patternInSquareBrackets = Pattern.compile(stringInSquareBrackets);
        Pattern patternInTriangularBrackets = Pattern.compile(stringInTriangularBrackets);
        if (actionEntity != null) {
            replaceByMatcherForPattern(actionEntity, actionParameter, patternInQuotes);
            replaceByMatcherForPattern(actionEntity, actionParameter, patternInSquareBrackets);
            replaceByMatcherForPattern(actionEntity, actionParameter, patternInTriangularBrackets);
        }
    }

    private void replaceByMatcherForPattern(Action actionEntity, ActionParameter actionParameter,
                                            Pattern pattern) {
        Matcher matcher = pattern.matcher(actionEntity.getName());
        if (matcher.find()) {
            String value = StringUtils.defaultIfEmpty(actionParameter.getValue(), "");
            actionEntity.setName(actionEntity.getName().replace(matcher.group(0),
                    matcher.group(0).replace(matcher.group(1), value)));
        }
    }

    private void precompileComplexParameterValue(Action actionEntity, Matcher matcher,
                                                 ActionParameter actionParameter) {
        if (actionParameter.getComplexParam().getType().equals(ComplexActionParameter.Type.ARRAY)
                && matcher.find()) {
            ComplexActionParameter complexParam = actionParameter.getComplexParam();
            List<String> values = complexParam.getArrayParams().stream().map(complexActionParam -> {
                        String value = complexActionParam.getValue();
                        return "\"" + value + "\"";
                    }
            ).collect(Collectors.toList());
            joinComplexValue(actionEntity, matcher, values);
        }
        if (actionParameter.getComplexParam().getType().equals(ComplexActionParameter.Type.MAP)
                && matcher.find()) {
            ComplexActionParameter complexParam = actionParameter.getComplexParam();
            List<String> values = complexParam.getMapParams().stream().map(complexActionParam -> {
                        String key = complexActionParam.getParamKey().getValue();
                        String value = complexActionParam.getParamValue().getValue();
                        return "\"" + key + "\"='" + value + "'";
                    }
            ).collect(Collectors.toList());
            joinComplexValue(actionEntity, matcher, values);
        }
    }

    private void joinComplexValue(Action actionEntity, Matcher matcher, List<String> values) {
        String joinedValues = String.join(",", values);
        String replacement = ("(" + joinedValues + ")")
                .replaceAll("\\$", "\\\\\\$")
                .replaceAll("\\\\n", "\\\\\\\\n");
        String regex = matcher.group(0).replaceAll("\\(", "\\\\\\(")
                .replaceAll("\\)", "\\\\\\)");
        actionEntity.setName(actionEntity.getName().replaceFirst(regex, replacement));
    }

    /**
     * Insert context value into replacers ${param}.
     *
     * @param expression some string via parameters,
     * @param variables  context,
     * @return compiled string.
     */
    public String precompileVariables(String expression, Map<String, Object> variables) {
        log.debug("precompileVariables (expression = {}, variables = {})", expression, variables);
        Matcher stringMatcher = STRING_PARAM_PATTERN.matcher(expression);
        while (stringMatcher.find()) {
            String group = stringMatcher.group(1);
            Object replacement = variables.get(group);
            expression = replacement == null ? expression : expression.replace(stringMatcher.group(0),
                    String.valueOf(replacement));
        }

        log.debug("result expression {}", expression);
        return expression;
    }

}
