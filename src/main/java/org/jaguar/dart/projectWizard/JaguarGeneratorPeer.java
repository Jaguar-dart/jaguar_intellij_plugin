package org.jaguar.dart.projectWizard;

import com.intellij.execution.OutputListener;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.WebProjectGenerator;
import org.jaguar.dart.JaguarSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.HashSet;

public class JaguarGeneratorPeer implements WebProjectGenerator.GeneratorPeer<JaguarProjectWizardData> {
  @NotNull
  private final ProjectCreationStepGui gui;

  JaguarGeneratorPeer() {
    gui = new ProjectCreationStepGui();
    getTemplates();
    gui.setJagarExePath(JaguarSettingsManager.getJaguarExePath(), JaguarSettingsManager.isJaguarExePathValid());
  }

  @Override
  public void buildUI(final @NotNull SettingsStep settingsStep) {
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return gui.getRootPanel();
  }

  @NotNull
  @Override
  public JaguarProjectWizardData getSettings() {
    return new JaguarProjectWizardData(gui.getTemplateName());
  }

  @Nullable
  @Override
  public ValidationInfo validate() {
    if(!JaguarSettingsManager.isJaguarExePathValid()) {
      return new ValidationInfo("Invalid Jaguar.dart executable path!", gui.getJaguarExePathLbl());
    }

    {
      String template = gui.getTemplateName();
      if (template.length() == 0) {
        return new ValidationInfo("Template not selected!", gui.getTemplatesLst());
      }
    }

    return null;
  }

  public boolean validateInIntelliJ() {
    final ValidationInfo info = validate();

    if (info == null) {
      //TODO myErrorLabel.setVisible(false);
      return true;
    } else {
      /* TODO
      myErrorLabel.setVisible(true);
      myErrorLabel
          .setText(XmlStringUtil.wrapInHtml("<font color='#" + ColorUtil.toHex(JBColor.RED) + "'><left>" + info.message + "</left></font>"));

      if (!myIntellijLiveValidationEnabled) {
        myIntellijLiveValidationEnabled = true;
        enableIntellijLiveValidation();
      }
      */

      return false;
    }
  }

  @Override
  public void addSettingsStateListener(final @NotNull WebProjectGenerator.SettingsStateListener stateListener) {
    gui.setProjectCreationStepChangeListener(() -> {
      stateListener.stateChanged(validate() == null);
    });
  }

  @Override
  public boolean isBackgroundJobRunning() {
    return false;
  }

  private void getTemplates() {
    GeneralCommandLine command = makeListCommand();

    if (command == null) return;

    try {
      final OSProcessHandler processHandler = new OSProcessHandler(command);

      StringBuilder outContent = new StringBuilder();
      StringBuilder errContent = new StringBuilder();

      processHandler.addProcessListener(new ProcessAdapter() {
        @Override
        public void processTerminated(final ProcessEvent event) {
          final HashSet<String> ret = new HashSet<>();
          try {
            if (event.getExitCode() == 0) {
              BufferedReader rdr = new BufferedReader(new StringReader(outContent.toString()));
              for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                final String stripped = line.replaceAll("\\p{Cc}", "").trim();
                if (stripped.length() == 0 || stripped.equals("[0m")) continue;
                ret.add(stripped);
              }
              rdr.close();
            }
          } catch (Exception exp) {
            //Do nothing
          }
          gui.setTemplates(ret);
        }
      });


      processHandler.addProcessListener(new OutputListener(outContent, errContent));

      processHandler.startNotify();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static GeneralCommandLine makeListCommand() {
    final GeneralCommandLine command = new GeneralCommandLine();

    if (!JaguarSettingsManager.isJaguarExePathValid()) return null;

    String jaguarExePath = JaguarSettingsManager.getJaguarExePath();

    command.setExePath(jaguarExePath);
    command.addParameter("list::projects");

    return command;
  }
}
