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

import static org.qubership.atp.ei.ntt.Constants.EXPORT_FILES_FOLDER_NAME;
import static org.qubership.atp.ei.ntt.Constants.EXPORT_PROJECTS_FOLDER_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.qubership.atp.ei.ntt.Constants;
import org.qubership.atp.ei.ntt.controllers.DataSetController;
import org.qubership.atp.ei.ntt.controllers.ModelItemController;
import org.qubership.atp.ei.ntt.converter.ModelItemTextConverter;
import org.qubership.atp.ei.ntt.converter.TextType;
import org.qubership.atp.ei.ntt.model.ContextVariable;
import org.qubership.atp.ei.ntt.model.DataSet;
import org.qubership.atp.ei.ntt.model.ModelItem;
import org.qubership.atp.ei.ntt.model.Project;
import org.qubership.atp.ei.ntt.model.Reference;
import org.qubership.atp.ei.ntt.model.Scope;
import org.qubership.atp.ei.ntt.model.Template;
import org.qubership.atp.ei.ntt.model.TemplateProject;
import org.qubership.atp.ei.ntt.model.TestAction;
import org.qubership.atp.ei.ntt.model.TreeNode;
import org.qubership.atp.ei.ntt.model.Workspace;
import org.qubership.atp.ei.ntt.model.enums.ModelItemType;
import org.qubership.atp.ei.ntt.model.impl.ProjectModel;
import org.qubership.atp.ei.ntt.model.impl.TemplateProjectModel;
import org.qubership.atp.ei.ntt.model.impl.WorkspaceModel;
import org.qubership.atp.ei.ntt.scope.ScopeDao;
import org.qubership.atp.ei.ntt.scope.ScopeDaoFactory;
import org.qubership.atp.ei.ntt.settings.model.dal.SettingsResource;
import org.qubership.atp.ei.ntt.settings.model.dal.support.DaoFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NttModelLoader {

    private static NttModelLoader instance;
    private static final ModelItemController modelItemController = ModelItemController.getInstance();
    private static final int MAX_SIZE = 5242880;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized NttModelLoader getInstance() {
        if (instance == null) {
            instance = new NttModelLoader();
        }
        return instance;
    }

    private NttModelLoader() {
        super();
    }

    /**
     * Add node.
     *
     * @param parent   the parent
     * @param child    the child
     * @param helpArgs the help args
     */
    public void addNode(TreeNode parent, TreeNode[] child, Object... helpArgs) {
        if (parent == null || child == null) {
            return;
        }
        modelItemController.add(parent, child, helpArgs);
    }

    /**
     * Clear references.
     */
    public void clearReferences() {
        for (Project proj : getProjects(false)) {
            clearReferences(proj);
        }
    }

    private void clearReferences(TreeNode treeNode) {
        for (TreeNode node : treeNode.getChildren()) {
            if (node instanceof Reference) {
                ((Reference) node).setTemplate(null);
            }
            clearReferences(node);
        }
    }

    /**
     * Gets projects.
     *
     * @param isTemplate the is template
     * @return the projects
     */
    public List<Project> getProjects(boolean isTemplate) {
        if (isTemplate) {
            return getWorkspace().getTemplateProjects();
        } else {
            return getWorkspace().getProjects();
        }
    }

    public Workspace getWorkspace() {
        return WorkspaceModel.getInstance();
    }

    public Scope getScope() {
        return getWorkspace().getScope();
    }

    /**
     * Create reference.
     *
     * @param name             the name
     * @param miEnum           the mi enum
     * @param templateProjName the template proj name
     * @param templateName     the template name
     * @return the reference
     */
    public Reference createReference(String name, ModelItemType miEnum, String templateProjName, String templateName) {
        Reference referenceModel = modelItemController.createSomeThing(Reference.class, name);
        referenceModel.setTemplate(miEnum, templateProjName, templateName);
        referenceModel.setReferenceId(CommonUtils.generateId());
        return referenceModel;
    }

    /**
     * Create test action.
     *
     * @param name        the name
     * @param description the description
     * @return the test action
     */
    public TestAction createTestAction(String name, String description) {
        TestAction action = modelItemController.createSomeThing(TestAction.class, name);
        action.setActionId(CommonUtils.generateId());
        action.setDescription(description);
        return action;
    }

    /**
     * Create template.
     *
     * @param modelForTemplate the model for template
     * @return the template
     */
    public Template createTemplate(ModelItem modelForTemplate) {
        Template template = createTemplateCore(modelForTemplate);
        modelForTemplate.setParent(template);
        return template;
    }

    private Template createTemplateCore(ModelItem modelForTemplate) {
        Template template = modelItemController.createSomeThing(Template.class, StringUtils.EMPTY);
        template.setModelItem(modelForTemplate);
        template.setTemplateId(CommonUtils.generateId());
        return template;
    }

    /**
     * Load project.
     *
     * @param file the file
     * @return the project
     */
    public Project loadProject(File file) {
        Project project = new ProjectModel();
        project.setName(FilenameUtils.removeExtension(file.getName()));
        ModelItemTextConverter.getInstance().setChildrenFromText(project, file, false);
        String dsFilename = FilenameUtils.removeExtension(file.getPath()) + Constants.TXT_DATASET_EXTENTION;
        DataSetController.getInstance().load(project, new ArrayList<>(Collections.singletonList(dsFilename)));
        List<Project> projects = NttModelLoader.getInstance().getProjects(false);
        projects.add(project);
        log.debug("Loaded NTT projects: {}", projects);

        return project;
    }


    /**
     * Load project template project.
     *
     * @param file the file
     * @return the project
     */
    public Project loadProjectTemplate(File file) {
        Project project = new TemplateProjectModel();
        project.setName(FilenameUtils.removeExtension(file.getName()));
        ModelItemTextConverter.getInstance().setChildrenFromText(project, file, true);
        String dsFilename = FilenameUtils.removeExtension(file.getPath())
                + Constants.TXT_TEMPLATE_DATASET_EXTENTION;
        DataSetController.getInstance().load(project, new ArrayList<>(Collections.singletonList(dsFilename)));
        NttModelLoader.getInstance().getProjects(true).add(project);
        return project;
    }

    /**
     * Save project.
     *
     * @param project       the project
     * @param directoryPath the directory path
     * @throws IOException the io exception
     */
    public void saveProject(Project project, Path directoryPath) throws IOException {
        if (project == null) {
            return;
        }
        boolean isTemplate = project instanceof TemplateProject;
        String fileName = CommonUtils.getSafeFilename(project.getName());
        Path directory = createFolder(directoryPath.resolve(EXPORT_PROJECTS_FOLDER_NAME));

        String projectFileText;

        if (isTemplate) {
            projectFileText =
                    ModelItemTextConverter.getInstance().convertTemplateToText(project, TextType.FILE).getText();
        } else {
            projectFileText = ModelItemTextConverter.getInstance().convertMiToText(project, TextType.FILE).getText();
        }

        log.debug("result: {}", projectFileText);

        if (projectFileText != null) {
            if (projectFileText.length() >= MAX_SIZE) {
                throw new IOException("Maximum file size exceeded: " + MAX_SIZE + "!");
            }
            String projectFileExtension = isTemplate
                    ? Constants.TXT_TEMPLATE_PROJECT_EXTENTION
                    : Constants.TXT_PROJECT_EXTENTION;
            Path projectFile = directory.resolve(fileName + projectFileExtension);

            Files.write(projectFile, projectFileText.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Save data set.
     *
     * @param project       the project
     * @param directoryPath the directory path
     * @throws IOException the io exception
     */
    public void saveDataSet(Project project, Path directoryPath) throws IOException {
        if (project == null) {
            return;
        }
        String fileName = CommonUtils.getSafeFilename(project.getName());
        Path directory = createFolder(directoryPath.resolve(EXPORT_PROJECTS_FOLDER_NAME));

        String dataSetFileExtension = project instanceof TemplateProject
                ? Constants.TXT_TEMPLATE_DATASET_EXTENTION
                : Constants.TXT_DATASET_EXTENTION;
        Path dataSetFile = directory.resolve(fileName + dataSetFileExtension);

        StringBuilder buf = new StringBuilder();
        for (DataSet dataSet : project.getDataSets()) {
            buf.append("dsName=").append(dataSet.getName()).append(StringUtils.LF).append(StringUtils.LF);
            for (ContextVariable variable : dataSet.getVariables()) {
                buf.append("name=").append(variable.getName()).append("; description=")
                        .append(variable.getDescription()).append("; value=").append(variable.getValue())
                        .append(StringUtils.LF);
            }
            buf.append(StringUtils.LF);
        }

        String dataSetFileText = buf.toString();

        log.debug("dataSetFileText: {}", dataSetFileText);

        Files.write(dataSetFile, dataSetFileText.getBytes(StandardCharsets.UTF_8));

        Path filesDirectory = createFolder(directoryPath.resolve(EXPORT_FILES_FOLDER_NAME));

        Map<Path, Path> files = project.getFiles();
        for (Map.Entry<Path, Path> entry : files.entrySet()) {
            Path atpFileContainer = entry.getValue();
            Path newFileFullPath = filesDirectory.resolve(entry.getKey());
            createFolder(newFileFullPath.getParent());
            Files.copy(atpFileContainer, newFileFullPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Path createFolder(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        return path;
    }

    /**
     * Save scope.
     *
     * @param scope         the scope
     * @param directoryPath the directory path
     */
    public void saveScope(Scope scope, Path directoryPath) throws IOException {
        Path directory = createFolder(directoryPath.resolve(EXPORT_PROJECTS_FOLDER_NAME));
        String scopeName = CommonUtils.getSafeFilename(scope.getName());
        Path scopeFile = directory.resolve(scopeName + Constants.DEFAULT_FILE_EXT);

        ScopeDaoFactory scopeDaoFactory = ScopeDaoFactory.getInstance();
        ScopeDao scopeDao = scopeDaoFactory.getScopeDao(scope);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ScopeDao.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(scopeDao, scopeFile.toFile());
        } catch (JAXBException e) {
            log.info("Error save scope {}", scopeName, e);
        }
    }

    /**
     * Save environment settings.
     *
     * @param settingResource the setting resource
     * @param workDir         the work dir
     * @throws IOException the io exception
     */
    public void saveEnvironmentSettings(SettingsResource settingResource, Path workDir) throws IOException {
        Path directory = createFolder(workDir.resolve("config"));
        Path settingFile = Files.createFile(directory.resolve("new_settings.xml"));

        DaoFactory factory = DaoFactory.getInstance();
        settingResource.setFileSettings(settingFile.toFile());
        factory.save(settingResource);
    }
}