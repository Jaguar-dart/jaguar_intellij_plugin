import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return !gui.getJaguarExePath().equals(PropertiesComponent.getInstance().getValue(kJaguarExePath));
    }

    @Override
    public void apply() throws ConfigurationException {
        PropertiesComponent.getInstance().setValue(kJaguarExePath, gui.getJaguarExePath());
    }

    @Override
    public void reset() {
        gui.setJaguarExePath(PropertiesComponent.getInstance().getValue(kJaguarExePath));
    }

    @Override
    public void disposeUIResources() {
        gui = null;
    }
}
