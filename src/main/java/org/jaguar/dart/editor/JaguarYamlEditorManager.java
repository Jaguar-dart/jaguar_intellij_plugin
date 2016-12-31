package org.jaguar.dart.editor;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.Alarm;
import org.jaguar.dart.ActionJaguarBuild;
import org.jaguar.dart.JaguarYamlEditorGui;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class JaguarYamlEditorManager {
  @NotNull private final JaguarYamlEditorGui gui;

  @NotNull private final Module module;

  @NotNull private final Project project;

  private boolean myInsideChange = false;

  @NotNull private final VirtualFile file;

  private final Document myDocument;

  @NotNull private final DocumentAdapter myDocumentListener;

  @NotNull private JaguarYaml yaml = new JaguarYaml();

  public JaguarYamlEditorManager(@NotNull JaguarYamlEditorGui gui, @NotNull Module module, @NotNull Project project, @NotNull VirtualFile file) {
    this.gui = gui;
    this.module = module;
    this.project = project;
    this.file = file;

    // We need to synchronize GUI editor with the document
    final Alarm alarm = new Alarm();
    myDocumentListener = new DocumentAdapter() {
      public void documentChanged(final DocumentEvent e) {
        if (!myInsideChange) {
          alarm.cancelAllRequests();
          alarm.addRequest(new MySynchronizeRequest(),
              100);
        }
      }
    };

    // Prepare document
    myDocument = FileDocumentManager.getInstance().getDocument(file);
    if(myDocument != null) {
      myDocument.addDocumentListener(myDocumentListener);
    }

    // Read form from file
    readFromFile();
  }

  /// Disposes all the resources
  public void dispose() {
    myDocument.removeDocumentListener(myDocumentListener);
  }

  private void readFromFile() {
    final String text = myDocument.getText();

    yaml = JaguarYaml.read(text);

    refreshView();
  }

  private void refreshView() {
    gui.setApis(yaml.getApis());
    gui.setSerializers(yaml.getSerializers());
    gui.setValidators(yaml.getValidators());
    gui.setBeans(yaml.getBeans());

    //TODO
  }

  private class MySynchronizeRequest implements Runnable {
    MySynchronizeRequest() {
    }

    public void run() {
      if (module.isDisposed()) {
        return;
      }

      if (project.isDisposed()) {
        return;
      }

      System.out.println("Synchronizing GUI editor " + file.getName() + " to document");
      if(myDocument != null) {
        PsiDocumentManager.getInstance(project).commitDocument(myDocument);
      }

      readFromFile();
    }
  }
}
