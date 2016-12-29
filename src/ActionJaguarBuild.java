import com.intellij.execution.ExecutionException;
import com.intellij.execution.actions.StopProcessAction;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.UrlFilter;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CloseActiveTabAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.UIBundle;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.MessageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class ActionJaguarBuild extends AnAction {

    private static final Key<WindowContentInfo> WINDOW_CONTENT_INFO_KEY = Key.create("WINDOW_CONTENT_INFO_KEY");

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Pair<Module, VirtualFile> moduleAndPubspecYamlFile = getModuleAndPubspecYamlFile(e);
        if (moduleAndPubspecYamlFile == null) return;

        final Module module = moduleAndPubspecYamlFile.first;
        final VirtualFile pubspec = moduleAndPubspecYamlFile.second;

        final GeneralCommandLine command = buildCommand(pubspec.getParent());

        execute(module, pubspec.getParent(), command, "Jaguar build");  //TODO fix action title
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        final boolean visible = getModuleAndPubspecYamlFile(e) != null;
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible && !isInProgress());
    }

    @Nullable
    private static Pair<Module, VirtualFile> getModuleAndPubspecYamlFile(final AnActionEvent e) {
        final Module module = LangDataKeys.MODULE.getData(e.getDataContext());
        final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());

        System.out.print(module.getModuleFilePath());

        if (module != null && psiFile != null && psiFile.getName().equalsIgnoreCase(JAGUAR_YAML)) {
            final VirtualFile file = psiFile.getOriginalFile().getVirtualFile();
            return file != null ? Pair.create(module, file) : null;
        }
        return null;
    }

    private static final String JAGUAR_YAML = "jaguar.yaml";

    private static final AtomicBoolean ourInProgress = new AtomicBoolean(false);

    private static boolean isInProgress() {
        return ourInProgress.get();
    }

    private GeneralCommandLine buildCommand(VirtualFile path) {
        final GeneralCommandLine command = new GeneralCommandLine().withWorkDirectory(path.getPath());

        command.setExePath("/home/teja/.pub-cache/bin/jaguar");
        command.addParameter("build");

        return command;
    }

    private static void execute(@NotNull final Module module,
                                @NotNull final VirtualFile workingDir,
                                @NotNull final GeneralCommandLine command,
                                @NotNull final String actionTitle) {
        try {
            final OSProcessHandler processHandler = new OSProcessHandler(command);

            processHandler.addProcessListener(new ProcessAdapter() {
                @Override
                public void processTerminated(final ProcessEvent event) {
                    ourInProgress.set(false);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!module.isDisposed()) {
                            // refresh later than exclude, otherwise IDE may start indexing excluded folders
                            VfsUtil.markDirtyAndRefresh(true, true, true, workingDir);
                        }
                    });
                }
            });

            showOutput(module, command, processHandler, workingDir, actionTitle);
        } catch (ExecutionException e) {
            e.printStackTrace();

            ourInProgress.set(false);

            /* TODO
            // may be better show it in Messages tool window console?
            Notifications.Bus.notify(
                    new Notification(GROUP_DISPLAY_ID, actionTitle, DartBundle.message("dart.pub.exception", e.getMessage()), NotificationType.ERROR));
            */
        }
        //TODO
    }

    private static void showOutput(@NotNull final Module module,
                                   @NotNull final GeneralCommandLine command,
                                   @NotNull final OSProcessHandler processHandler,
                                   @NotNull final VirtualFile workingDir,
                                   @NotNull final String actionTitle) {
        final ConsoleView console;
        WindowContentInfo info = findExistingInfoForCommand(module.getProject(), command);

        if (info != null) {
            // rerunning the same pub command in the same tool window tab (corresponding tool window action invoked)
            console = info.console;
            console.clear();
        } else {
            console = createConsole(module.getProject(), workingDir);
            info = new WindowContentInfo(module, workingDir, command, actionTitle, console);

            final ActionToolbar actionToolbar = createToolWindowActionsBar(info);

            final SimpleToolWindowPanel toolWindowPanel = new SimpleToolWindowPanel(false, true);
            toolWindowPanel.setContent(console.getComponent());
            toolWindowPanel.setToolbar(actionToolbar.getComponent());

            final Content content = ContentFactory.SERVICE.getInstance().createContent(toolWindowPanel.getComponent(), actionTitle, true);
            content.putUserData(WINDOW_CONTENT_INFO_KEY, info);
            Disposer.register(content, console);

            final ContentManager contentManager = MessageView.SERVICE.getInstance(module.getProject()).getContentManager();
            removeOldTabs(contentManager);
            contentManager.addContent(content);
            contentManager.setSelectedContent(content);

            final ToolWindow toolWindow = ToolWindowManager.getInstance(module.getProject()).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
            toolWindow.activate(null, true);
        }

        info.rerunPubCommandAction.setProcessHandler(processHandler);
        info.stopProcessAction.setProcessHandler(processHandler);

        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(final ProcessEvent event) {
                console.print(IdeBundle.message("finished.with.exit.code.text.message", event.getExitCode()), ConsoleViewContentType.SYSTEM_OUTPUT);
            }
        });

        console.print(FileUtil.toSystemDependentName(workingDir.getPath()) + "\n",
                ConsoleViewContentType.SYSTEM_OUTPUT);
        console.attachToProcess(processHandler);
        processHandler.startNotify();
    }

    @Nullable
    private static WindowContentInfo findExistingInfoForCommand(final Project project, @NotNull final GeneralCommandLine command) {
        for (Content content : MessageView.SERVICE.getInstance(project).getContentManager().getContents()) {
            final WindowContentInfo info = content.getUserData(WINDOW_CONTENT_INFO_KEY);
            if (info != null && info.command == command) {
                return info;
            }
        }
        return null;
    }

    @NotNull
    private static ConsoleView createConsole(@NotNull final Project project, @NotNull final VirtualFile workingDir) {
        final TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        consoleBuilder.setViewer(true);
        //TODO consoleBuilder.addFilter(new DartConsoleFilter(project, workingDir));
        //TODO consoleBuilder.addFilter(new DartRelativePathsConsoleFilter(project, workingDir.getPath()));
        consoleBuilder.addFilter(new UrlFilter());
        return consoleBuilder.getConsole();
    }

    @NotNull
    private static ActionToolbar createToolWindowActionsBar(@NotNull final WindowContentInfo info) {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();

        final RerunCommandAction rerunPubCommandAction = new RerunCommandAction(info);
        info.rerunPubCommandAction = rerunPubCommandAction;
        actionGroup.addAction(rerunPubCommandAction);

        final StopProcessAction stopProcessAction = new StopProcessAction("Stop",
                "Stop 'jaguar build'",
                null);
        info.stopProcessAction = stopProcessAction;
        actionGroup.addAction(stopProcessAction);

        actionGroup.add(ActionManager.getInstance().getAction(IdeActions.ACTION_PIN_ACTIVE_TAB));

        final AnAction closeContentAction = new CloseActiveTabAction();
        closeContentAction.getTemplatePresentation().setIcon(AllIcons.Actions.Cancel);
        closeContentAction.getTemplatePresentation().setText(UIBundle.message("tabbed.pane.close.tab.action.name"));
        actionGroup.add(closeContentAction);

        final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, false);
        toolbar.setTargetComponent(info.console.getComponent());
        return toolbar;
    }


    private static void removeOldTabs(@NotNull final ContentManager contentManager) {
        for (Content content : contentManager.getContents()) {
            if (!content.isPinned() && content.isCloseable() && content.getUserData(WINDOW_CONTENT_INFO_KEY) != null) {
                contentManager.removeContent(content, false);
            }
        }
    }

    private static class WindowContentInfo {
        private
        @NotNull
        final Module module;
        private
        @NotNull
        final VirtualFile pubspecFile;
        private
        @NotNull
        final GeneralCommandLine command;
        private
        @NotNull
        final String actionTitle;
        private
        @NotNull
        final ConsoleView console;
        private RerunCommandAction rerunPubCommandAction;
        private StopProcessAction stopProcessAction;

        private WindowContentInfo(@NotNull final Module module,
                                 @NotNull final VirtualFile pubspecFile,
                                 @NotNull final GeneralCommandLine command,
                                 @NotNull final String actionTitle,
                                 @NotNull final ConsoleView console) {
            this.module = module;
            this.pubspecFile = pubspecFile;
            this.command = command;
            this.actionTitle = actionTitle;
            this.console = console;
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof WindowContentInfo && command == ((WindowContentInfo) o).command;
        }

        @Override
        public int hashCode() {
            return command.hashCode();
        }
    }

    private static class RerunCommandAction extends DumbAwareAction {
        @NotNull
        private final WindowContentInfo myInfo;

        private OSProcessHandler myProcessHandler;

        private RerunCommandAction(@NotNull final WindowContentInfo info) {
            super("Rerun 'jaguar build'",
                    "Reruns 'jaguar build'",
                    AllIcons.Actions.Execute);
            myInfo = info;

            registerCustomShortcutSet(CommonShortcuts.getRerun(), info.console.getComponent());
        }

        private void setProcessHandler(@NotNull final OSProcessHandler processHandler) {
            myProcessHandler = processHandler;
        }

        @Override
        public void update(@NotNull final AnActionEvent e) {
            e.getPresentation().setEnabled(!isInProgress() && myProcessHandler != null && myProcessHandler.isProcessTerminated());
        }

        @Override
        public void actionPerformed(@NotNull final AnActionEvent e) {
            execute(myInfo.module, myInfo.pubspecFile, myInfo.command, myInfo.actionTitle);
        }
    }
}
