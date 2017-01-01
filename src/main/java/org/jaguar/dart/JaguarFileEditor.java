package org.jaguar.dart;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jaguar.dart.editor.JaguarYamlEditorManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class JaguarFileEditor extends UserDataHolderBase implements FileEditor, PossiblyDumbAware {

  private final VirtualFile myFile;

  private final JaguarYamlEditorGui gui;

  @NotNull
  private final JaguarYamlEditorManager manager;

  JaguarFileEditor(@NotNull final Project project, @NotNull final VirtualFile file) {
    final VirtualFile vf = file instanceof LightVirtualFile ? ((LightVirtualFile) file).getOriginalFile() : file;
    final Module module = ModuleUtilCore.findModuleForFile(vf, project);
    if (module == null) {
      throw new IllegalArgumentException("No module for file " + file + " in project " + project);
    }
    myFile = file;
    gui = new JaguarYamlEditorGui(module);

    manager = new JaguarYamlEditorManager(gui, module, project, file);
  }

  @NotNull
  public JComponent getComponent() {
    return gui.getRootPanel();
  }

  public void dispose() {
    gui.dispose();
    manager.dispose();
  }

  public JComponent getPreferredFocusedComponent() {
    return gui.getPreferredFocusedComponent();
  }

  @NotNull
  public String getName() {
    return "jaguar.yaml Editor";
  }

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public boolean isValid() {
    return
        FileDocumentManager.getInstance().getDocument(myFile) != null &&
            myFile.getName().equals(ActionJaguarBuild.JAGUAR_YAML);
  }

  @Override
  public void selectNotify() {
  }

  @Override
  public void deselectNotify() {
  }

  @Override
  public void addPropertyChangeListener(@NotNull final PropertyChangeListener listener) {
    //NOTE do nothing?
  }

  @Override
  public void removePropertyChangeListener(@NotNull final PropertyChangeListener listener) {
    //NOTE do nothing?
  }

  public BackgroundEditorHighlighter getBackgroundHighlighter() {
    return null;
  }

  public FileEditorLocation getCurrentLocation() {
    return null;
  }

  public void setState(@NotNull final FileEditorState state) {
    //TODO
  }

  @Override
  public boolean isDumbAware() {
    return false;
  }
}
