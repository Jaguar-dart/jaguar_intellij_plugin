package org.jaguar.dart.projectWizard;

import org.jetbrains.annotations.NotNull;

public class JaguarProjectWizardData {
  @NotNull
  public final String templateUrl;

  JaguarProjectWizardData(@NotNull final String templateUrl) {
    this.templateUrl = templateUrl;
  }
}
