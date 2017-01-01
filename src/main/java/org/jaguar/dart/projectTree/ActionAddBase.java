package org.jaguar.dart.projectTree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jaguar.dart.ActionJaguarBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract public class ActionAddBase extends AnAction {

  @Override
  public void update(AnActionEvent e) {
    super.update(e);

    VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
    final boolean visible = file != null && isDartFile(file);
    e.getPresentation().setVisible(visible);
    e.getPresentation().setEnabled(visible);
  }

  boolean isDartFile(@NotNull VirtualFile file) {
    String extension = file.getExtension();
    return extension != null && extension.equals("dart");
  }

  @NotNull
  static String relativePath(@NotNull Module module, @NotNull VirtualFile file) {
    if (module.getModuleFile() == null) return "";

    String path = file.getPath();

    String modulePath = module.getModuleFile().getParent().getParent().getPath();

    if (!path.startsWith(modulePath)) return "";

    String ret = path.substring(modulePath.length());

    if (ret.length() == 0) return "";

    if (ret.startsWith(File.separator)) {
      ret = ret.substring(1);
    }

    return ret;
  }

  @Nullable
  static Pair<Module, VirtualFile> getJaguarYamlFile(final AnActionEvent e) {
    final Module module = LangDataKeys.MODULE.getData(e.getDataContext());

    if (module == null || module.getModuleFile() == null) {
      return null;
    }

    String basePath = module.getModuleFile().getParent().getParent().getPath();
    Path path = Paths.get(basePath, ActionJaguarBuild.JAGUAR_YAML);

    VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(path.toString());

    if (file == null) return null;

    if (!file.exists()) return null;

    if (file.isDirectory()) return null;

    return Pair.create(module, file);
  }
}