package org.jaguar.dart.projectTree;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jaguar.dart.editor.JaguarYaml;

public class ActionAddBean extends ActionAddBase {

  @Override
  public void actionPerformed(AnActionEvent e) {
    VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

    if (file == null || !isDartFile(file)) return;

    Pair<Module, VirtualFile> pair = getJaguarYamlFile(e);

    if (pair == null) return;

    Module module = pair.first;
    VirtualFile jaguarYaml = pair.second;

    final JaguarYaml yaml = new JaguarYaml();
    Application app = ApplicationManager.getApplication();
    app.runReadAction(() -> {
      try {
        yaml.copy(JaguarYaml.read(jaguarYaml.getInputStream()));
      } catch (Exception ex) {
        //Do nothing
      }
    });
    yaml.addBean(relativePath(module, file));
    app.runWriteAction(() -> {
      try {
        JaguarYaml.write(yaml, jaguarYaml.getOutputStream(this));
      } catch (Exception ex) {
        //Do nothing
      }
    });
  }
}