package org.jaguar.dart;

import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JaguarIconProvider extends IconProvider {
  @Nullable
  @Override
  public Icon getIcon(@NotNull PsiElement psiElement, @Iconable.IconFlags int i) {
    if (psiElement instanceof PsiFile && !((PsiFile) psiElement).isDirectory()) {
      final VirtualFile file = ((PsiFile) psiElement).getVirtualFile();

      if (file.getName().equals(ActionJaguarBuild.JAGUAR_YAML)) {
        return IconLoader.getIcon("/icons/jaguar.png");
      }
    }

    return null;
  }
}
