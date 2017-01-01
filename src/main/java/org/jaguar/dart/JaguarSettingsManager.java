package org.jaguar.dart;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class JaguarSettingsManager implements SearchableConfigurable {
  public static final String kJaguarExePath = "JaguarExePath";

  private JaguarSettingsGui gui;

  public JaguarSettingsManager() {
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Jaguar.dart";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @NotNull
  @Override
  public String getId() {
    return "preferences.jaguar.dart";
  }

  @Nullable
  @Override
  public Runnable enableSearch(String s) {
    return null;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    gui = new JaguarSettingsGui();
    return gui.getRootPanel();
  }

  @Override
  public boolean isModified() {
    return !gui.getJaguarExePath().equals(getJaguarExePath());
  }

  @Override
  public void apply() throws ConfigurationException {
    setJaguarExePath(gui.getJaguarExePath());
  }

  @Override
  public void reset() {
    gui.setJaguarExePath(getJaguarExePath());
  }

  @Override
  public void disposeUIResources() {
    gui = null;
  }

  public static String getJaguarExePath() {
    return PropertiesComponent.getInstance().getValue(JaguarSettingsManager.kJaguarExePath);
  }

  public static void setJaguarExePath(@NotNull String value) {
    PropertiesComponent.getInstance().setValue(kJaguarExePath, value);
  }

  public static boolean isJaguarExePathValid() {
    String path = getJaguarExePath();

    if (path == null) return false;

    File exeFile = new File(path);

    if (!exeFile.exists()) return false;
    if (!exeFile.isFile()) return false;
    if (!exeFile.canExecute()) return false;

    //TODO check version?

    return true;
  }
}
