package org.jaguar.dart;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;

public class JaguarFileEditorProvider implements FileEditorProvider, DumbAware {
    public boolean accept(@NotNull final Project project, @NotNull final VirtualFile file) {
        System.out.println(file.getName());
        return
                file.getName().equals(ActionJaguarBuild.JAGUAR_YAML) &&
                        (ModuleUtil.findModuleForFile(file, project) != null || file instanceof LightVirtualFile);
    }

    @NotNull
    public FileEditor createEditor(@NotNull final Project project, @NotNull final VirtualFile file) {
        return new JaguarFileEditor(project, file);
    }

    /* TODO
    @NotNull
    public FileEditorState readState(@NotNull final Element element, @NotNull final Project project, @NotNull final VirtualFile file) {
        //TODO[anton,vova] implement
        return new MyEditorState(-1, ArrayUtil.EMPTY_STRING_ARRAY);
    }
    */

    @NotNull
    public String getEditorTypeId() {
        return "jaguar.yaml";
    }

    @NotNull
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
