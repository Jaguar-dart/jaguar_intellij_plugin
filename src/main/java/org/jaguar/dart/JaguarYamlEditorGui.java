package org.jaguar.dart;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jaguar.dart.editor.JaguarYaml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class JaguarYamlEditorGui {


  private JList<String> apiLst;
  private JPanel contentPanel;
  private JList<String> serializersLst;
  private JButton removeSerializerButton;
  private JButton addSerializerBtn;
  private JButton editSerializerBtn;
  private JList<String> validatorsLst;
  private JList<String> beansLst;
  private JButton addApiBtn;
  private JButton removeApiBtn;
  private JButton editApiBtn;
  private JButton addValidatorBtn;
  private JButton removeValidatorBtn;
  private JButton editValidatorBtn;
  private JButton addBeansBtn;
  private JButton removeBeansBtn;
  private JButton editBeansBtn;
  private JScrollPane rootPanel;

  public JaguarYamlEditorGui(@NotNull Module module) {
    this.module = module;

    removeApiBtn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
          return;
        }

        List<String> selected = apiLst.getSelectedValuesList();

        final JaguarYaml yaml = new JaguarYaml();
        Application app = ApplicationManager.getApplication();
        app.runReadAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            yaml.copy(JaguarYaml.read(file.getInputStream()));
          } catch (Exception ex) {
            //Do nothing
          }
        });
        yaml.removeApis(selected);
        app.runWriteAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            JaguarYaml.write(yaml, file.getOutputStream(this));
          } catch (Exception ex) {
            //Do nothing
          }
        });
      }
    });

    removeSerializerButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
          return;
        }

        List<String> selected = serializersLst.getSelectedValuesList();

        final JaguarYaml yaml = new JaguarYaml();
        Application app = ApplicationManager.getApplication();
        app.runReadAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            yaml.copy(JaguarYaml.read(file.getInputStream()));
          } catch (Exception ex) {
            //Do nothing
          }
        });
        yaml.removeSerializers(selected);
        app.runWriteAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            JaguarYaml.write(yaml, file.getOutputStream(this));
          } catch (Exception ex) {
            //Do nothing
          }
        });
      }
    });

    removeValidatorBtn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
          return;
        }

        List<String> selected = validatorsLst.getSelectedValuesList();

        final JaguarYaml yaml = new JaguarYaml();
        Application app = ApplicationManager.getApplication();
        app.runReadAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            yaml.copy(JaguarYaml.read(file.getInputStream()));
          } catch (Exception ex) {
            //Do nothing
          }
        });
        yaml.removeValidators(selected);
        app.runWriteAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            JaguarYaml.write(yaml, file.getOutputStream(this));
          } catch (Exception ex) {
            //Do nothing
          }
        });
      }
    });

    removeBeansBtn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
          return;
        }

        List<String> selected = beansLst.getSelectedValuesList();

        final JaguarYaml yaml = new JaguarYaml();
        Application app = ApplicationManager.getApplication();
        app.runReadAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            yaml.copy(JaguarYaml.read(file.getInputStream()));
          } catch (Exception ex) {
            //Do nothing
          }
        });
        yaml.removeBeans(selected);
        app.runWriteAction(() -> {
          VirtualFile file = getJaguarYamlPath();
          if (file == null) return;

          try {
            JaguarYaml.write(yaml, file.getOutputStream(this));
          } catch (Exception ex) {
            //Do nothing
          }
        });
      }
    });
  }

  @NotNull
  private final Module module;

  @Nullable
  private VirtualFile getJaguarYamlPath() {
    if (module.getModuleFile() == null) return null;
    String basePath = module.getModuleFile().getParent().getParent().getPath();
    Path path = Paths.get(basePath, ActionJaguarBuild.JAGUAR_YAML);

    VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(path.toString());

    if (file == null) {
      return null;
    }

    if (!file.exists()) {
      return null;
    }

    if (file.isDirectory()) {
      return null;
    }

    return file;
  }

  JComponent getRootPanel() {
    return rootPanel;
  }

  public void setApis(@NotNull HashSet<String> apis) {
    apiLst.removeAll();
    apiLst.setListData(apis.toArray(new String[apis.size()]));
  }

  public void setSerializers(@NotNull HashSet<String> serializers) {
    serializersLst.removeAll();
    serializersLst.setListData(serializers.toArray(new String[serializers.size()]));
  }

  public void setValidators(@NotNull HashSet<String> validators) {
    validatorsLst.removeAll();
    validatorsLst.setListData(validators.toArray(new String[validators.size()]));
  }

  public void setBeans(@NotNull HashSet<String> beans) {
    beansLst.removeAll();
    beansLst.setListData(beans.toArray(new String[beans.size()]));
  }

  void dispose() {
    //TODO dispose
  }

  JComponent getPreferredFocusedComponent() {
    return apiLst;
  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    rootPanel = new JScrollPane();
    contentPanel = new JPanel();
    contentPanel.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
    rootPanel.setViewportView(contentPanel);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
    contentPanel.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setFont(new Font(label1.getFont().getName(), label1.getFont().getStyle(), 18));
    label1.setText("Apis and RouteGroups");
    panel1.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    addApiBtn = new JButton();
    addApiBtn.setText("Add");
    panel1.add(addApiBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    removeApiBtn = new JButton();
    removeApiBtn.setText("Remove");
    panel1.add(removeApiBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    editApiBtn = new JButton();
    editApiBtn.setText("Edit");
    panel1.add(editApiBtn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel1.add(spacer1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(scrollPane1, new GridConstraints(1, 1, 3, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 100), new Dimension(-1, 100), 0, false));
    scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
    apiLst = new JList();
    scrollPane1.setViewportView(apiLst);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
    contentPanel.add(panel2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    removeSerializerButton = new JButton();
    removeSerializerButton.setText("Remove");
    panel2.add(removeSerializerButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    addSerializerBtn = new JButton();
    addSerializerBtn.setText("Add");
    panel2.add(addSerializerBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setFont(new Font(label2.getFont().getName(), label2.getFont().getStyle(), 16));
    label2.setText("Serializers");
    panel2.add(label2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    editSerializerBtn = new JButton();
    editSerializerBtn.setText("Edit");
    panel2.add(editSerializerBtn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel2.add(spacer2, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel2.add(scrollPane2, new GridConstraints(1, 1, 3, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 100), new Dimension(-1, 100), 0, false));
    scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
    serializersLst = new JList();
    scrollPane2.setViewportView(serializersLst);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
    contentPanel.add(panel3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label3 = new JLabel();
    label3.setFont(new Font(label3.getFont().getName(), label3.getFont().getStyle(), 16));
    label3.setText("Validators");
    panel3.add(label3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    addValidatorBtn = new JButton();
    addValidatorBtn.setText("Add");
    panel3.add(addValidatorBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    removeValidatorBtn = new JButton();
    removeValidatorBtn.setText("Remove");
    panel3.add(removeValidatorBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel3.add(spacer3, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JScrollPane scrollPane3 = new JScrollPane();
    panel3.add(scrollPane3, new GridConstraints(1, 1, 3, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 100), new Dimension(-1, 100), 0, false));
    scrollPane3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
    validatorsLst = new JList();
    scrollPane3.setViewportView(validatorsLst);
    editValidatorBtn = new JButton();
    editValidatorBtn.setText("Edit");
    panel3.add(editValidatorBtn, new GridConstraints(3, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
    contentPanel.add(panel4, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label4 = new JLabel();
    label4.setFont(new Font(label4.getFont().getName(), label4.getFont().getStyle(), 16));
    label4.setText("Beans");
    panel4.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer4 = new Spacer();
    panel4.add(spacer4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final Spacer spacer5 = new Spacer();
    panel4.add(spacer5, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    addBeansBtn = new JButton();
    addBeansBtn.setText("Add");
    panel4.add(addBeansBtn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    removeBeansBtn = new JButton();
    removeBeansBtn.setText("Remove");
    panel4.add(removeBeansBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    editBeansBtn = new JButton();
    editBeansBtn.setText("Edit");
    panel4.add(editBeansBtn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane4 = new JScrollPane();
    panel4.add(scrollPane4, new GridConstraints(1, 1, 3, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 100), new Dimension(-1, 100), 0, false));
    scrollPane4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null));
    beansLst = new JList();
    scrollPane4.setViewportView(beansLst);
    final Spacer spacer6 = new Spacer();
    contentPanel.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(13, 11), null, 0, false));
    final Spacer spacer7 = new Spacer();
    contentPanel.add(spacer7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(13, 5), null, 0, false));
    final Spacer spacer8 = new Spacer();
    contentPanel.add(spacer8, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, new Dimension(5, -1), null, 0, false));
    final Spacer spacer9 = new Spacer();
    contentPanel.add(spacer9, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return rootPanel;
  }
}
