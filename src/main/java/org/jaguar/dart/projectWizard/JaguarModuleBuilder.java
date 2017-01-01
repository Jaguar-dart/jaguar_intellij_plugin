package org.jaguar.dart.projectWizard;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.internal.statistic.UsageTrigger;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.module.WebModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jaguar.dart.JaguarSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class JaguarModuleBuilder extends ModuleBuilder {
  private JaguarProjectWizardData myWizardData;

  @Override
  public String getName() {
    return "Jaguar.dart";
  }

  @Override
  public String getPresentableName() {
    return "Jaguar.dart";
  }

  @Override
  public String getDescription() {
    return "Creates Jaguar.dart project";
  }

  @Override
  public Icon getBigIcon() {
    return IconLoader.getIcon("/icons/jaguar.png");
  }

  @Override
  public Icon getNodeIcon() {
    return IconLoader.getIcon("/icons/jaguar.png");
  }

  @Override
  public ModuleType getModuleType() {
    return WebModuleType.getInstance();
  }

  @Override
  public String getParentGroup() {
    return WebModuleBuilder.GROUP_NAME;
  }


  @Nullable
  @Override
  public ModuleWizardStep getCustomOptionsStep(final WizardContext context, final Disposable parentDisposable) {
    final JaguarModuleWizardStep step = new JaguarModuleWizardStep(context);
    Disposer.register(parentDisposable, step);
    return step;
  }

  void setWizardData(final JaguarProjectWizardData wizardData) {
    myWizardData = wizardData;
  }

  @Override
  public void setupRootModel(final ModifiableRootModel modifiableRootModel) throws ConfigurationException {
    final ContentEntry contentEntry = doAddContentEntry(modifiableRootModel);
    final VirtualFile baseDir = contentEntry == null ? null : contentEntry.getFile();
    if (baseDir != null) {
      setupProject(modifiableRootModel, baseDir, myWizardData);
    }
  }

  static private void setupProject(@NotNull final ModifiableRootModel modifiableRootModel,
                                   @NotNull final VirtualFile baseDir,
                                   @NotNull final JaguarProjectWizardData wizardData) {
    final String templateName = wizardData.templateUrl.isEmpty() ? "boilerplate" : wizardData.templateUrl;
    UsageTrigger.trigger("JaguarProjectWizard." + templateName);

    createModule(templateName, baseDir);
  }

  static private void createModule(String repoName, VirtualFile moduleDir) {
    GeneralCommandLine command = makeCreateCommand(repoName, moduleDir.getPath());

    if (command == null) return;

    try {
      final OSProcessHandler processHandler = new OSProcessHandler(command);

      processHandler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(final ProcessEvent event) {
          ApplicationManager.getApplication().invokeLater(() -> {
            // refresh later than exclude, otherwise IDE may start indexing excluded folders
            VfsUtil.markDirtyAndRefresh(true, true, true, moduleDir);
          });
        }
      });

      // StringBuilder outContent = new StringBuilder();
      // StringBuilder errContent = new StringBuilder();
      // processHandler.addProcessListener(new OutputListener(outContent, errContent));

      processHandler.startNotify();
      processHandler.waitFor();
      //TODO int exitCode = processHandler.getProcess().exitValue();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  @Nullable
  static private GeneralCommandLine makeCreateCommand(String repoName, String modulePath) {
    final GeneralCommandLine command = new GeneralCommandLine().withWorkDirectory(modulePath);

    if (!JaguarSettingsManager.isJaguarExePathValid()) return null;

    String jaguarExePath = JaguarSettingsManager.getJaguarExePath();

    command.setExePath(jaguarExePath);
    command.addParameter("create::project");
    command.addParameter("-r");
    command.addParameter(repoName);
    command.addParameter("-n");
    command.addParameter(".");

    return command;
  }
}
