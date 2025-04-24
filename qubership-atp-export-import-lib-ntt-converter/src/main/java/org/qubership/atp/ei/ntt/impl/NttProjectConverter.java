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

package org.qubership.atp.ei.ntt.impl;

import static org.qubership.atp.ei.ntt.Constants.NTT_ACTION_SWITCH_TO_SERVER;
import static org.qubership.atp.ei.ntt.Constants.PATH_TO_FILES;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.ExportConverter;
import org.qubership.atp.ei.ntt.controllers.DataSetController;
import org.qubership.atp.ei.ntt.controllers.ModelItemController;
import org.qubership.atp.ei.ntt.dto.Action;
import org.qubership.atp.ei.ntt.dto.ActionParameter;
import org.qubership.atp.ei.ntt.dto.Compound;
import org.qubership.atp.ei.ntt.dto.Connection;
import org.qubership.atp.ei.ntt.dto.DataSet;
import org.qubership.atp.ei.ntt.dto.DataSetAttribute;
import org.qubership.atp.ei.ntt.dto.DataSetAttributeKey;
import org.qubership.atp.ei.ntt.dto.DataSetList;
import org.qubership.atp.ei.ntt.dto.DataSetListValue;
import org.qubership.atp.ei.ntt.dto.DataSetParameter;
import org.qubership.atp.ei.ntt.dto.Environment;
import org.qubership.atp.ei.ntt.dto.FileData;
import org.qubership.atp.ei.ntt.dto.MetaInfo;
import org.qubership.atp.ei.ntt.dto.System;
import org.qubership.atp.ei.ntt.dto.TestCase;
import org.qubership.atp.ei.ntt.dto.TestCaseDependency;
import org.qubership.atp.ei.ntt.dto.TestCaseFlags;
import org.qubership.atp.ei.ntt.dto.TestScenario;
import org.qubership.atp.ei.ntt.dto.TestScope;
import org.qubership.atp.ei.ntt.dto.enums.DirectiveEnum;
import org.qubership.atp.ei.ntt.dto.enums.Flags;
import org.qubership.atp.ei.ntt.flag.Flag;
import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.NttTestCase;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.ScopeItem;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TestStep;
import org.qubership.atp.ei.ntt.model.TestSuite;
import org.qubership.atp.ei.ntt.model.enums.ScopeSectionType;
import org.qubership.atp.ei.ntt.model.impl.ContextVariableImpl;
import org.qubership.atp.ei.ntt.model.impl.ProjectModel;
import org.qubership.atp.ei.ntt.model.impl.ScopeItemModel;
import org.qubership.atp.ei.ntt.model.impl.ScopeModel;
import org.qubership.atp.ei.ntt.model.impl.WorkspaceModel;
import org.qubership.atp.ei.ntt.settings.model.dal.SettingsResource;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinkDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentItemLinksSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentListDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.EnvironmentListsSectionDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServerDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServerTypesDal;
import org.qubership.atp.ei.ntt.settings.model.dal.settings.ServersSettingsDal;
import org.qubership.atp.ei.ntt.utils.CommonUtils;
import org.qubership.atp.ei.ntt.utils.DependenceTransformer;
import org.qubership.atp.ei.ntt.utils.NttModelLoader;
import org.qubership.atp.ei.ntt.utils.VariablesCompiler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NttProjectConverter implements ExportConverter {

    private static final Map<String, String> atp2NttFlagMaps = new HashMap<>();
    private static final UUID SSH_CONNECTION_ID = UUID.fromString("24136d83-5ffb-487f-9bb4-e73be3a89aa2");
    private static final UUID DB_CONNECTION_ID = UUID.fromString("46ca25d6-058e-471a-9b5e-c13e4b481227");
    private static final UUID HTTP_CONNECTION_ID = UUID.fromString("2a0eab16-0fe7-4a12-8155-78c0c151abdf");

    private static Map<String, String> macroRegexReplacement;

    static {
        macroRegexReplacement = new HashMap<>();
        macroRegexReplacement.put("\\$RAND\\(\'([0-9]*)\'\\)", "%RANDOM#$1%");
        macroRegexReplacement.put("\\$DATE\\(\'(.*?)\'(, \'.*?\')?\\)", "%DATE#$1%");
        //action
        atp2NttFlagMaps.put(Constants.Flags.WARN_IF_FAIL_FLAG_NAME, Flag.WARN_IF_FAIL);
        //action/TR/ER
        atp2NttFlagMaps.put(Constants.Flags.SKIP_ON_FAIL_FLAG_NAME, Flag.SKIP_IF_ANY_PREV_EXCEPTION);
        atp2NttFlagMaps.put(Constants.Flags.STOP_ON_FAIL_FLAG_NAME, Flag.STOP_IF_EXCEPTION_ON_ITEM);
        atp2NttFlagMaps.put(Constants.Flags.TERMINATE_IF_FAIL_FLAG_NAME, Flag.STOP_IF_EXCEPTION_ON_ITEM);
        //TR/ER
        atp2NttFlagMaps.put(Constants.Flags.SKIP_IF_DEPENDENCY_FAILED_FLAG_NAME, Flag.SKIP_IF_ANY_PREV_EXCEPTION);
    }

    private final ObjectMapper objectMapper;

    private final ModelItemController modelItemController;
    private final NttModelLoader nttActionsCreator;
    private final WorkspaceModel nttWorkspaceModel;
    private final VariablesCompiler variablesCompiler;

    private NttExportScope nttScopeMaps;
    private Path modelLocationPath;
    private Path rootForFiles;

    /**
     * Instantiates a new Ntt project converter.
     */
    public NttProjectConverter(Path modelLocationPath) {
        WorkspaceModel.reset();
        this.nttWorkspaceModel = WorkspaceModel.getInstance();
        this.modelItemController = ModelItemController.getInstance();
        this.nttActionsCreator = NttModelLoader.getInstance();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.variablesCompiler = new VariablesCompiler();
        this.nttScopeMaps = new NttExportScope();
        this.modelLocationPath = modelLocationPath;
        this.rootForFiles = modelLocationPath.resolve("files");
    }

    private void createScopesForExistingScopes() {
        log.debug("start createScopesForExistingScopes()");
        List<UUID> scopeIds = nttScopeMaps.getNttTestSuite2atpTestScope()
                .values().stream().distinct().collect(Collectors.toList());

        scopeIds.forEach(atpScopeId -> {
                    log.trace("loop scopeIds: {}", atpScopeId);
                    TestScope atpScope = loadObject(atpScopeId, TestScope.class);

                    ScopeModel scope = new ScopeModel();
                    String scopeName = atpScope.getName();
                    scope.setName(getSafeProjectName(scopeName));
                    nttScopeMaps.getNttScopes().add(scope);

                    Map<UUID, ScopeItem> testCase2ScopeItems = new HashMap<>();
                    Map<UUID, HashSet<UUID>> dependenciesGraph = new HashMap<>();

                    List<TestSuite> testSuites = nttScopeMaps.getNttTestSuite2atpTestScope()
                            .entrySet().stream()
                            .filter(testSuiteIdEntry -> testSuiteIdEntry.getValue().equals(atpScopeId))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    List<NttTestCase> testCases0 = new ArrayList<>();
                    testSuites.forEach(testSuite1 -> testCases0.addAll(testSuite1.getTestCases()));

                    for (NttTestCase nttTestCase : testCases0) {
                        buildScopeItem(atpScope, nttTestCase, testCase2ScopeItems, dependenciesGraph);
                    }

                    // setting dependencies
                    Map<UUID, UUID> singleDependencyGraph = new DependenceTransformer().transform(dependenciesGraph);
                    for (Map.Entry<UUID, UUID> dependency : singleDependencyGraph.entrySet()) {
                        if (dependency.getValue() != null) {
                            ScopeItem scopeItem1 = testCase2ScopeItems.get(dependency.getKey());
                            ScopeItem scopeItem2 = testCase2ScopeItems.get(dependency.getValue());
                            scopeItem1.setDependency(scopeItem2);
                        }
                    }

                    scope.setScopeItems(new ArrayList<>(testCase2ScopeItems.values()));
                }
        );
        log.debug("end createScopesForExistingScopes()");
    }

    private void buildScopeItem(TestScope atpScope,
                                NttTestCase testCase,
                                Map<UUID, ScopeItem> testCase2ScopeItems,
                                Map<UUID, HashSet<UUID>> dependenciesGraph) {
        UUID atpTestCaseId = nttScopeMaps.getNttTestCase2atpTestCase().get(testCase);
        ScopeItem scopeItem = buildScopeItem(atpTestCaseId, testCase);
        ScopeSectionType scopeSectionType = ScopeSectionType.ACTIONS;
        if (atpScope.getPrerequisitesCases() != null && atpScope.getPrerequisitesCases().contains(atpTestCaseId)) {
            scopeSectionType = ScopeSectionType.PREREQUISITES;
        } else if (atpScope.getValidationCases() != null && atpScope.getValidationCases().contains(atpTestCaseId)) {
            scopeSectionType = ScopeSectionType.VALIDATION;
        }
        scopeItem.setStage(scopeSectionType);
        UUID testCaseId = atpTestCaseId;
        testCase2ScopeItems.put(testCaseId, scopeItem);

        HashSet<UUID> dependsOn = new HashSet<>();
        List<TestCaseDependency> atpDependsOn = loadObject(atpTestCaseId, TestCase.class).getDependsOn();
        if (atpDependsOn != null) {
            UUID atpScopeId = atpScope.getUuid();
            atpDependsOn.stream()
                    .filter(testCaseDependency -> testCaseDependency.getTestScopeId().equals(atpScopeId))
                    .forEach(testCaseDependency -> dependsOn.add(testCaseDependency.getTestCaseId()));
        }
        dependenciesGraph.put(testCaseId, new HashSet<>(dependsOn));
    }

    private ScopeItem buildScopeItem(UUID atpTestCaseId, NttTestCase testCase) {
        UUID dataSetId = loadObject(atpTestCaseId, TestCase.class).getDatasetUuid();
        org.qubership.atp.ei.ntt.model.DataSet dataSet = nttScopeMaps.getDataSet2NttDataSetMap().get(dataSetId);

        return new ScopeItemModel(testCase, dataSet);
    }

    private void extractFlagsForSuites() {
        log.debug("start extractFlagsForSuites()");
        nttScopeMaps.getNttTestSuite2atpTestScope().forEach((testSuite, atpScopeId) -> {
            List<String> flags = new ArrayList<>();
            List<Flags> atpFlags = loadObject(atpScopeId, TestScope.class).getFlags();
            if (atpFlags != null) {
                atpFlags.forEach(flag -> flags.add(flag.getName()));
            }
            for (String flag : flags) {
                String nttFlag = atp2NttFlagMaps.get(flag.toLowerCase());
                if (nttFlag != null) {
                    testSuite.setFlag(nttFlag);
                }
            }
        });
        log.debug("end extractFlagsForSuites()");
    }

    private void exportTestCases() {
        log.debug("start exportTestCases()");
        nttScopeMaps.getNttTestCase2atpTestCase()
                .forEach((testCase, testCaseId) -> exportTestCase(testCaseId, testCase));
        log.debug("end exportTestCases()");
    }

    private void extractFlagsForTestCases() throws Exception {
        log.debug("start extractFlagsForTestCases()");
        List<UUID> cases = getListOfObjects(TestCase.class);
        cases.forEach((testCaseId) -> {
            List<TestCaseFlags> atpFlags = loadObject(testCaseId, TestCase.class).getFlags();
            if (atpFlags != null) {
                atpFlags.stream().forEach(testCaseFlags -> {
                    UUID scopeId = testCaseFlags.getTestScopeId();
                    if (!nttScopeMaps.getNttTestSuite2atpTestScope().values().contains(scopeId)) {
                        return;
                    }

                    List<String> flags = new ArrayList<>();
                    if (testCaseFlags.getFlags() != null) {
                        testCaseFlags.getFlags().forEach(flag1 -> flags.add(flag1.getName()));
                    }

                    List<TestSuite> listOfSuites = new ArrayList<>();
                    nttScopeMaps.getNttTestSuite2atpTestScope()
                            .entrySet().stream()
                            .filter(testSuiteUUIDEntry -> scopeId.equals(testSuiteUUIDEntry.getValue()))
                            .forEach(testSuiteUUIDEntry -> listOfSuites.add(testSuiteUUIDEntry.getKey()));

                    NttTestCase nttTestCase = nttScopeMaps.getNttTestCase2atpTestCase().entrySet()
                            .stream()
                            .filter(testCaseUUIDEntry -> listOfSuites.contains(testCaseUUIDEntry.getKey().getParent()))
                            .findFirst().get().getKey();

                    for (String flag : flags) {
                        String nttFlag = atp2NttFlagMaps.get(flag.toLowerCase());
                        if (nttFlag != null) {
                            nttTestCase.setFlag(nttFlag);
                        }
                    }
                });
            }
        });
        log.debug("end extractFlagsForTestCases()");
    }

    private String replaceAtpMacrosToNtt(String step) {
        if (step == null) {
            return step;
        }
        String result = step;
        for (Map.Entry<String, String> entry : macroRegexReplacement.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private NttTestCase exportTestCase(UUID testCaseId, NttTestCase nttTestCase) {
        log.debug("start exportTestCases(testCaseId: {}, nttTestCase: {})", testCaseId, nttTestCase);
        UUID testScenarioId = loadObject(testCaseId, TestCase.class).getTestScenarioUuid();

        List<MetaInfo> steps = loadObject(testScenarioId, TestScenario.class).getMetainfo();
        if (steps == null) {
            return nttTestCase;
        }
        List<TestStep> nttTestSteps = nttTestCase.getTestSteps();

        List<String> flags = new ArrayList<>();
        List<TestAction> directiveActions = new ArrayList<>();

        for (MetaInfo step : steps) {
            TestStep nttTestStep = null;
            if (MetaInfo.Type.FLAG == step.getType()) {
                flags.add(getFlagText(step));
            } else if (MetaInfo.Type.DIRECTIVE == step.getType()) {
                TestAction directiveAction = handleDirectiveAsAction(step, flags);
                if (directiveAction != null) {
                    directiveActions.add(directiveAction);
                }
            } else if (MetaInfo.Type.ACTION == step.getType()) {
                String stepText = getActionText(step);
                nttTestStep = modelItemController.create(TestStep.class, stepText);

                String finalValue = replaceAtpMacrosToNtt(stepText);
                TestAction nttTestAction = nttActionsCreator.createTestAction(finalValue, null);

                List<TestAction> nttActions = nttTestStep.getActions();
                nttActions.addAll(directiveActions);
                directiveActions.clear();
                nttActions.add(nttTestAction);
            } else if (MetaInfo.Type.COMPOUND == step.getType()) {
                String stepText = getCompoundText(step);
                nttTestStep = modelItemController.create(TestStep.class, stepText);
                List<TestAction> compoundActions = compounds(step, 1);

                List<TestAction> nttActions = nttTestStep.getActions();
                nttActions.addAll(directiveActions);
                directiveActions.clear();
                nttActions.addAll(compoundActions);
            } else if (StringUtils.isNotEmpty(step.getOtherTextValue())) {
                nttTestStep = modelItemController.create(TestStep.class, step.getOtherTextValue());
            } else {
                continue;
            }

            if (MetaInfo.Type.ACTION == step.getType() || MetaInfo.Type.COMPOUND == step.getType()) {
                for (String flag : flags) {
                    nttTestStep.setFlag(flag);
                }
                flags.clear();
            }

            if (nttTestStep != null) { // whatever but not flag and directive
                nttTestSteps.add(nttTestStep);
            }
        }
        log.debug("end exportTestCases(..): {}", nttTestCase);
        return nttTestCase;
    }

    private TestAction getUseDirectiveAction(MetaInfo step) {
        ArrayList<String> useDirective = getDirectiveValues(step);
        String directiveStr = convertUseDirectiveToActionSwitch(useDirective);
        if (directiveStr == null) {
            directiveStr = "//@Use(" + useDirective + ")";
        }
        return nttActionsCreator.createTestAction(directiveStr, null);
    }

    private String getFlagByDirectiveName(String directiveName) {
        if (directiveName == null) {
            return null;
        }
        return atp2NttFlagMaps.get(directiveName.replace("@", "")
                .replace("_", " ").toLowerCase());
    }

    private String getActionText(MetaInfo step) {
        UUID actionId = step.getStepId();
        Action action = loadObject(actionId, Action.class);
        action.setParameters(step.getParameters());
        Action result = variablesCompiler.precompileStringParameters(action);
        return result.getName();
    }

    private String getCompoundText(MetaInfo step) {
        UUID actionId = step.getStepId();
        Compound compound = loadObject(actionId, Compound.class);
        Action action = new Action();
        action.setParameters(step.getParameters());
        action.setName(compound.getName());
        Action result = new VariablesCompiler().precompileStringParameters(action);
        return result.getName();
    }

    private ArrayList<String> getDirectiveValues(MetaInfo step) {
        List<ActionParameter> params = step.getParameters();
        ArrayList<String> values = new ArrayList<>();
        if (params == null || params.isEmpty()) {
            return values;
        }
        params.stream().filter(actionParameter -> "value".equals(actionParameter.getName()))
                .forEach(actionParameter -> values.add(actionParameter.getValue()));
        return values;
    }

    private String getFlagText(MetaInfo step) {
        return step.toString(); // todo implement me correctly
    }

    private boolean isUseDirective(MetaInfo step) {
        return DirectiveEnum.USE.getId().equals(step.getStepId());
    }

    private String getSafeProjectName(String name) {
        return CommonUtils.getSafeFilename(name);
    }

    private String convertUseDirectiveToActionSwitch(ArrayList<String> useDirectiveValues) {
        String actionText = null;
        if (useDirectiveValues != null && !useDirectiveValues.isEmpty()) {
            actionText = String.format(NTT_ACTION_SWITCH_TO_SERVER, useDirectiveValues.get(0));
        }
        return actionText;
    }

    private List<TestAction> compounds(MetaInfo step, int level) {
        List<TestAction> resultActions = new ArrayList<>();
        Compound compound = loadObject(step.getStepId(), Compound.class);
        Map<String, Object> context = getCompoundContext(step);

        List<MetaInfo> subTestSteps = compound.getMetainfo();
        for (MetaInfo subStep : subTestSteps) {
            if (MetaInfo.Type.FLAG == subStep.getType()) {
                String flag = getFlagText(subStep);
                TestAction subTestAction = nttActionsCreator.createTestAction("//" + flag, null);
                resultActions.add(subTestAction);
            } else if (MetaInfo.Type.DIRECTIVE == subStep.getType()) {
                TestAction directiveAction = handleDirectiveAsAction(subStep, null);
                if (directiveAction != null) {
                    resultActions.add(directiveAction);
                }
            } else if (MetaInfo.Type.ACTION == subStep.getType()) {
                String subStepFinalValue = getActionText(subStep);
                subStepFinalValue = variablesCompiler.precompileVariables(subStepFinalValue, context);
                String finalValue = replaceAtpMacrosToNtt(subStepFinalValue);
                TestAction subTestAction = nttActionsCreator.createTestAction(finalValue, null);
                resultActions.add(subTestAction);
            } else if (MetaInfo.Type.COMPOUND == subStep.getType()) {
                subStep.getParameters().addAll(step.getParameters());
                String subStepFinalValue = getCompoundText(subStep);
                String finalValue = replaceAtpMacrosToNtt(subStepFinalValue);
                finalValue = "//{ compound (level " + level + "): " + finalValue;
                TestAction subTestAction = nttActionsCreator.createTestAction(finalValue, null);
                resultActions.add(subTestAction);
                resultActions.addAll(compounds(subStep, level + 1));
                finalValue = "//} compound (level " + level + "): " + subStepFinalValue;
                subTestAction = nttActionsCreator.createTestAction(finalValue, null);
                resultActions.add(subTestAction);
            } else {
                TestAction subTestAction = nttActionsCreator.createTestAction(
                        StringUtils.defaultIfEmpty(subStep.getOtherTextValue(), ""), null);
                resultActions.add(subTestAction);
            }
        }
        return resultActions;
    }

    private Map<String, Object> getCompoundContext(MetaInfo step) {
        Map<String, Object> result = new HashMap<>();
        step.getParameters().forEach(parameter -> result.put(parameter.getName(), parameter.getValue()));
        return result;
    }

    private TestAction handleDirectiveAsAction(MetaInfo step, List<String> flags) {
        TestAction directiveAction;
        if (isUseDirective(step)) {
            directiveAction = getUseDirectiveAction(step);
        } else {
            String directiveName = DirectiveEnum.getNameById(step.getStepId());
            String directiveStr = "// " + directiveName;
            directiveAction = nttActionsCreator.createTestAction(directiveStr, null);
            if (flags != null) {
                String flag = getFlagByDirectiveName(directiveName);
                if (flag != null) {
                    flags.add(flag);
                }
            }
        }

        return directiveAction;
    }

    private void createSuitesAndTestCases() throws Exception {
        log.debug("start createProjects()");
        Map<UUID, List<UUID>> tastCaseToTestSuites = new HashMap<>();

        List<UUID> scopes = getListOfObjects(TestScope.class);

        scopes.forEach((testScopeId) -> {
            TestScope nttTestScope = loadObject(testScopeId, TestScope.class);
            List<UUID> executionCases = nttTestScope.getExecutionCases();
            if (executionCases != null) {
                executionCases.forEach(testCaseId ->
                        tastCaseToTestSuites.computeIfAbsent(testCaseId, t -> new ArrayList<>()).add(testScopeId)
                );
            }
            List<UUID> validationCases = nttTestScope.getValidationCases();
            if (validationCases != null) {
                validationCases.forEach(testCaseId ->
                        tastCaseToTestSuites.computeIfAbsent(testCaseId, t -> new ArrayList<>()).add(testScopeId)
                );
            }
            List<UUID> prerequisitesCases = nttTestScope.getPrerequisitesCases();
            if (prerequisitesCases != null) {
                prerequisitesCases.forEach(testCaseId ->
                        tastCaseToTestSuites.computeIfAbsent(testCaseId, t -> new ArrayList<>()).add(testScopeId)
                );
            }
        });

        List<UUID> cases = getListOfObjects(TestCase.class);
        cases.forEach((testCaseId) -> {
                    TestCase atpTestCase = loadObject(testCaseId, TestCase.class);
                    UUID atpDataSetListId = atpTestCase.getDatasetStorageUuid();

                    Project project = nttScopeMaps.getDataSetList2NttProjectMap().get(atpDataSetListId);
                    if (project == null) {
                        project = nttScopeMaps.getDataSetList2NttProjectMap().get(null);
                    }

                    List<TestSuite> nttTestSuites = project.getTestSuites();
                    // collect test suites
                    List<UUID> testSuiteIds = tastCaseToTestSuites.get(testCaseId);

                    Map<UUID, TestSuite> atp2NttSuitesMap = nttScopeMaps.getProject2NttSuitesMap()
                            .computeIfAbsent(atpDataSetListId, k -> new HashMap<>());

                    if (testSuiteIds == null || testSuiteIds.isEmpty()) {
                        TestSuite nttSuite = atp2NttSuitesMap.get(null);
                        if (nttSuite == null) {
                            nttSuite = modelItemController.create(TestSuite.class, "Single Test Cases");
                            nttTestSuites.add(nttSuite);
                            atp2NttSuitesMap.put(null, nttSuite); // for single test case key is null
                            nttSuite.setParent(project);
                        }

                        String name = atpTestCase.getName();
                        NttTestCase nttTestCase = modelItemController.create(NttTestCase.class, name);

                        List<NttTestCase> nttSuiteTestCases = nttSuite.getTestCases();
                        nttSuiteTestCases.add(nttTestCase);
                        nttTestCase.setParent(nttSuite);

                        nttScopeMaps.getNttTestCase2atpTestCase().put(nttTestCase, testCaseId);
                    } else {
                        for (UUID testSuiteId : testSuiteIds) {
                            TestSuite nttSuite = atp2NttSuitesMap.get(testSuiteId);

                            if (nttSuite == null) {
                                TestScope testSuite = loadObject(testSuiteId, TestScope.class);
                                nttSuite = modelItemController.create(TestSuite.class, testSuite.getName());
                                nttTestSuites.add(nttSuite);
                                atp2NttSuitesMap.put(testSuiteId, nttSuite);
                                nttSuite.setParent(project);
                                nttScopeMaps.getNttTestSuite2atpTestScope().put(nttSuite, testSuiteId);
                            }

                            String name = atpTestCase.getName();
                            NttTestCase nttTestCase = modelItemController.create(NttTestCase.class, name);

                            List<NttTestCase> nttSuiteTestCases = nttSuite.getTestCases();
                            nttSuiteTestCases.add(nttTestCase);
                            nttTestCase.setParent(nttSuite);

                            nttScopeMaps.getNttTestCase2atpTestCase().put(nttTestCase, testCaseId);
                        }
                    }
                }
        );
        log.debug("end createProjects()");
    }

    @SneakyThrows
    private void insertVariablesInNttDataSets() {
        List<UUID> parameters = getListOfObjects(DataSetParameter.class);
        List<DataSetParameter> dataSetReferenceParameters = new ArrayList<>();
        parameters.forEach(id -> {
            DataSetParameter parameter = loadObject(id, DataSetParameter.class);

            UUID dataSetId = parameter.getDataSet();
            org.qubership.atp.ei.ntt.model.DataSet nttDataSet = nttScopeMaps.getDataSet2NttDataSetMap().get(dataSetId);
            if (nttDataSet == null) {
                return;
            }
            UUID attrId = parameter.getAttribute();

            DataSetAttribute attribute = getAttribute(attrId);

            if (4 == attribute.getAttributeType()) { // DSL link attribute
                dataSetReferenceParameters.add(parameter);
                return;
            }

            addValueInNttDataSet(nttDataSet, attribute, parameter);
        });

        dataSetReferenceParameters.forEach(dataSetReferenceParameter -> {

            UUID dataSet = dataSetReferenceParameter.getDataSet();
            if (dataSet == null) {
                return;
            }
            org.qubership.atp.ei.ntt.model.DataSet nttDataSet =
                    nttScopeMaps.getDataSet2NttDataSetMap().get(dataSet);

            if (nttDataSet == null || dataSetReferenceParameter.getDataSetReferenceValue() == null) {
                return;
            }

            List<UUID> parameters2 = getListOfObjects(DataSetParameter.class,
                    dataSetReferenceParameter.getDataSetReferenceValue());

            UUID dataSetReferenceAttributeId = dataSetReferenceParameter.getAttribute();
            DataSetAttribute dataSetReferenceAttribute = getAttribute(dataSetReferenceAttributeId);

            parameters2.forEach(id0 -> {
                DataSetParameter parameter = loadObject(id0, DataSetParameter.class);
                UUID attrId = parameter.getAttribute();
                DataSetAttribute attribute = getAttribute(attrId);
                attribute.setName(dataSetReferenceAttribute.getName() + "." + attribute.getName());
                addValueInNttDataSet(nttDataSet, attribute, parameter);
            });
        });
    }

    private void addValueInNttDataSet(org.qubership.atp.ei.ntt.model.DataSet nttDataSet,
                                      DataSetAttribute attribute,
                                      DataSetParameter parameter) {
        ContextVariable variable = nttDataSet.getVariableByName(attribute.getName());
        if (variable == null) {
            variable = new ContextVariableImpl();
            variable.setName(attribute.getName());
            nttDataSet.getVariables().add(variable);
        } else {
            return; // do not replace value if exists
        }
        if (parameter.getStringValue() != null) {
            variable.setValue(replaceAtpMacrosToNtt(parameter.getStringValue()));
        } else if (parameter.getListValue() != null) {
            DataSetListValue listValue = loadObject(parameter.getListValue(), DataSetListValue.class);
            variable.setValue(listValue.getText());
        } else if (parameter.getFileValueId() != null) {
            //todo implement me
        } else if (parameter.getDataSetReferenceValue() != null) {
            //todo implement me
        } else {
            Path file = getFileForVariable(parameter.getId());
            if (file != null) {
                Path nttPath = file.subpath(rootForFiles.getNameCount(), file.getNameCount());
                nttDataSet.getProject().getFiles().put(nttPath, file);
                variable.setValue(Paths.get(PATH_TO_FILES).resolve(nttPath).toString());
            }
        }
    }

    private DataSetAttribute getAttribute(UUID attrId) {
        DataSetAttribute attribute = null;
        try {
            attribute = loadObject(attrId, DataSetAttribute.class);
        } catch (Exception e) {
            DataSetAttributeKey attributeKey = loadObject(attrId, DataSetAttributeKey.class);
            attribute = loadObject(attributeKey.getAttribute(), DataSetAttribute.class);

            StringBuilder attrKeyName = new StringBuilder();
            List<UUID> keys = getPath(attributeKey.getKey());

            keys.forEach(keyId -> {
                DataSetAttribute rootAttribute = loadObject(keyId, DataSetAttribute.class);
                attrKeyName.append(rootAttribute.getName());
                attrKeyName.append(".");
            });
            attribute.setName(attrKeyName.toString() + attribute.getName());
        }
        return attribute;
    }

    private List<UUID> getPath(String key) {
        List<UUID> result = new LinkedList<>();
        String[] splitResult = key.split("_");
        for (String splitPart : splitResult) {
            result.add(UUID.fromString(splitPart));
        }
        return result;
    }

    private <T extends Object> List<UUID> getListOfObjects(Class<T> clazz, UUID... parentIds) {
        String folderName = clazz.getSimpleName();
        Path dirWithObjects = getFolderWithObjects(folderName, parentIds);
        return getListOfObjectIdByFolder(dirWithObjects);
    }

    private List<UUID> getListOfObjectIdByFolder(Path dirWithObjects) {
        List<UUID> res = new ArrayList<>();
        if (dirWithObjects == null || !Files.exists(dirWithObjects)) {
            return res;
        }
        try (Stream<Path> result = Files.find(dirWithObjects, 5,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())) {
            result.forEach(path -> res.add(UUID.fromString(path.getFileName().toString().split("\\.")[0])));
        } catch (Exception e) {
            log.error("Error on collecting list of objects by files from folder {}", dirWithObjects, e);
            return res;
        }
        return res;
    }

    private Path getFolderWithObjects(String folderName, UUID... parentIds) {
        log.debug("start getFolderWithObjects(folderName: {}, additionalPaths: {})", folderName, parentIds);
        Path res = modelLocationPath.resolve(folderName);
        if (parentIds != null) {
            for (UUID parentId : parentIds) {
                res = res.resolve(parentId.toString());
            }
        }
        return res;
    }

    @SneakyThrows
    private Path getFileForVariable(UUID id) {
        Path fileDataPath;
        try (Stream<Path> stream = Files.find(rootForFiles, 5,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile()
                        && path.getFileName().toString().contains(id.toString() + ".json"))) {
            fileDataPath = stream.findFirst().orElse(null);
        }
        if (fileDataPath != null) {
            FileData fileData = loadFileAsObject(fileDataPath, FileData.class, id);
            return fileDataPath.getParent().resolve(
                    CommonUtils.getIdentifierForFilePath(fileData.getParameterUuid().toString(), id));
        }
        log.debug("File for attribute with id {} not found.", id);
        return null;
    }

    private void createProjects(Map<UUID, String> dataSetListIdNameMap, Map<UUID, String> dataSetIdNameMap,
                                Map<UUID, UUID> dataSetIdParentIdMap) {
        log.debug("start createProjects(dataSetListIdNameMap: {}, dataSetIdNameMap: {}, dataSetIdParentIdMap: {})",
                dataSetListIdNameMap, dataSetIdNameMap, dataSetIdParentIdMap);
        for (UUID dataSetListId : dataSetListIdNameMap.keySet()) {
            nttScopeMaps.getDataSetList2NttProjectMap()
                    .computeIfAbsent(dataSetListId,
                            id -> new ProjectModel(getSafeProjectName(dataSetListIdNameMap.get(dataSetListId))));
        }

        for (UUID atpDataSetId : dataSetIdParentIdMap.keySet()) {
            UUID dataSetListId = dataSetIdParentIdMap.get(atpDataSetId);

            Project dataSetsProject = nttScopeMaps.getDataSetList2NttProjectMap().get(dataSetListId);

            org.qubership.atp.ei.ntt.model.DataSet nttDataSet = DataSetController.getInstance()
                    .createEmptyDataSet(dataSetsProject);

            String atpDataSetName = dataSetIdNameMap.get(atpDataSetId);

            nttDataSet.setName(getSafeProjectName(atpDataSetName));
            nttScopeMaps.getDataSet2NttDataSetMap().put(atpDataSetId, nttDataSet);
        }

        Project defaultProject = new ProjectModel("Default Project");
        nttScopeMaps.getDataSetList2NttProjectMap().put(null, defaultProject); // default project with null key
        nttWorkspaceModel.getProjects().add(defaultProject);
        log.debug("end createProjects(.., .., ..)");
    }

    private void createProjects() throws Exception {
        List<UUID> dataSets = getListOfObjects(DataSet.class);

        for (UUID atpDataSetId : dataSets) {
            DataSet dataSet = loadObject(atpDataSetId, DataSet.class);
            UUID dataSetListId = dataSet.getDataSetList();

            DataSetList dataSetList = loadObject(dataSetListId, DataSetList.class);

            Project dataSetsProject = nttScopeMaps.getDataSetList2NttProjectMap()
                    .computeIfAbsent(dataSetListId,
                            id -> new ProjectModel(getSafeProjectName(dataSetList.getName())));

            org.qubership.atp.ei.ntt.model.DataSet nttDataSet = DataSetController.getInstance()
                    .createEmptyDataSet(dataSetsProject);

            String atpDataSetName =
                    loadObject(atpDataSetId,
                            DataSet.class).getName();

            nttDataSet.setName(getSafeProjectName(atpDataSetName));
            nttScopeMaps.getDataSet2NttDataSetMap().put(atpDataSetId, nttDataSet);
        }

        Project defaultProject = new ProjectModel("Default Project");
        nttScopeMaps.getDataSetList2NttProjectMap().put(null, defaultProject); // default project with null key
        nttWorkspaceModel.getProjects().add(defaultProject);
    }

    /**
     * Convert data set into ntt format.
     *
     * @return the ntt export converter result
     * @throws Exception the exception
     */
    public NttExportConverterResult convertDataSet() throws Exception {
        createProjects();
        insertVariablesInNttDataSets();

        return new NttExportConverterResult(
                new ArrayList<>(nttScopeMaps.getDataSetList2NttProjectMap().values()),
                null, null, false, modelLocationPath);
    }

    /**
     * Convert catalog data into ntt format.
     *
     * @param dataSetListIdNameMap the data set list id name map
     * @param dataSetIdNameMap     the data set id name map
     * @param dataSetIdParentIdMap the data set id parent id map
     * @return the ntt export converter result
     * @throws Exception the exception
     */
    public NttExportConverterResult convertCatalog(Map<UUID, String> dataSetListIdNameMap,
                                                   Map<UUID, String> dataSetIdNameMap,
                                                   Map<UUID, UUID> dataSetIdParentIdMap) throws Exception {
        log.debug("start convertCatalog(dataSetListIdNameMap: {}, dataSetIdNameMap: {}, dataSetIdParentIdMap: {})",
                dataSetListIdNameMap, dataSetIdNameMap, dataSetIdParentIdMap);

        createProjects(dataSetListIdNameMap, dataSetIdNameMap, dataSetIdParentIdMap);

        createSuitesAndTestCases();
        exportTestCases();
        extractFlagsForTestCases();
        extractFlagsForSuites();
        createScopesForExistingScopes();

        NttExportConverterResult result = new NttExportConverterResult(
                new ArrayList<>(nttScopeMaps.getDataSetList2NttProjectMap().values()),
                nttScopeMaps.getNttScopes(), null, true, modelLocationPath);

        log.debug("end convertCatalog(.., .., ..)");
        return result;
    }

    /**
     * Convert environment data into ntt format.
     *
     * @return the ntt export converter result
     */
    public NttExportConverterResult convertEnvironment(Map<UUID, String> categoryMap) throws Exception {
        SettingsResource settingResource = new SettingsResource();
        nttScopeMaps.setNttEnvironments(settingResource);

        ServerTypesDal serverTypes = settingResource.getChild(ServerTypesDal.class);
        ServersSettingsDal servers = settingResource.getChild(ServersSettingsDal.class);
        EnvironmentListsSectionDal envs = settingResource.getChild(EnvironmentListsSectionDal.class);
        EnvironmentItemLinksSectionDal envLinks = settingResource.getChild(EnvironmentItemLinksSectionDal.class);

        List<UUID> listId = getListOfObjects(Environment.class);
        for (UUID id : listId) {
            Environment atpEnv = loadObject(id, Environment.class);

            EnvironmentListDal env = new EnvironmentListDal();
            env.setName(atpEnv.getName());
            envs.getEnvironmentLists().add(env);
            List<System> systems = atpEnv.getSystems();
            if (systems == null) {
                continue;
            }

            for (System system : systems) {
                String serverType = categoryMap.get(system.getSystemCategoryId());
                if (!serverTypes.getTypes().contains(serverType)) {
                    serverTypes.getTypes().add(serverType);
                }

                ServerDal server = new ServerDal();
                server.setAlias(system.getName());
                server.setType(serverType);

                servers.getServerList().add(server);

                EnvironmentItemLinkDal envLink = new EnvironmentItemLinkDal();
                envLink.setEnvironment(env.getName());
                envLink.setServer(server.getAlias());
                envLink.setType(server.getType());
                envLinks.getEnvironmentItemLinks().add(envLink);

                List<Connection> connections = system.getConnections();
                if (connections == null) {
                    continue;
                }
                for (Connection connection : connections) {
                    if (connection.getParameters() == null || connection.getParameters().isEmpty()) {
                        continue;
                    }
                    Map<String, String> params = connection.getParameters();
                    if (SSH_CONNECTION_ID.equals(connection.getSourceTemplateId())) {
                        server.setSshHost(params.get("ssh_host"));
                        server.setSshKey(params.get("ssh_key"));
                        server.setSshUser(params.get("ssh_login"));
                        server.setDbPassword(params.get("ssh_password"));
                    } else if (DB_CONNECTION_ID.equals(connection.getSourceTemplateId())) {
                        server.setDbConnectionUrl(params.get("jdbc_url"));
                        server.setDbPassword(params.get("db_password"));
                        server.setDbPort(params.get("db_port"));
                        server.setDbServer(params.get("db_host"));
                        server.setDbSid(params.get("db_name"));
                        server.setDbType(params.get("db_type"));
                        server.setDbUser(params.get("db_login"));
                    } else if (HTTP_CONNECTION_ID.equals(connection.getSourceTemplateId())) {
                        server.setInstance(params.get("url"));
                        server.setUrl(params.get("url"));
                    }

                    server.setUnclassifiedServerParameters(connection.getParameters());
                }
            }
        }
        return new NttExportConverterResult(
                null, null, nttScopeMaps.getNttEnvironments(), false, modelLocationPath);
    }

    @SneakyThrows
    private <T extends Object> T loadObject(UUID id, Class<T> clazz) {
        log.debug("start loadObject(id: {}, clazz: {})", id, clazz);
        Path dirWithObjects = getFolderWithObjects(clazz.getSimpleName());
        Path res = findFileOnDisk(dirWithObjects, id);
        return loadFileAsObject(res, clazz, id);
    }

    @SneakyThrows
    private <T extends Object> T loadFileAsObject(Path file, Class<T> clazz, UUID id) {
        log.debug("start loadFileAsObject(id: {}, clazz: {})", file, clazz);
        T result;
        try (InputStream in = Files.newInputStream(file)) {
            result = objectMapper.readValue(in, clazz);
        } catch (Exception e) {
            log.error("Cannot read data from file {}, id {}, class {}", file, id, clazz, e);
            throw new RuntimeException(String.format("Cannot read data from file %s", file), e);
        }
        log.debug("end (loadFileAsObject: {}, clazz: {})", file, clazz);
        return result;
    }

    @SneakyThrows
    private Path findFileOnDisk(Path workDir, UUID id) {
        log.debug("start findFileOnDisk(workDir: {}, id: {})", workDir, id);
        String fileName = id.toString();
        Optional<Path> res;
        try (Stream<Path> result = Files.find(workDir, 10,
                (path, basicFileAttributes) -> path.getFileName().toString().contains(fileName))) {
            res = result.findFirst();
        }
        if (!res.isPresent()) {
            return null;
        }
        Path result = res.get();
        log.debug("end findFileOnDisk(.., ..): {}", result);
        return result;
    }
}