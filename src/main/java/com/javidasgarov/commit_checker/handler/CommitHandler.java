package com.javidasgarov.commit_checker.handler;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.ui.UIUtil;
import com.javidasgarov.commit_checker.util.FileUtil;
import com.javidasgarov.commit_checker.checker.ChangeChecker;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CommitHandler extends CheckinHandler {

    private static final String CHECK_ADDED_CHANGES_CHECKBOX = "CHECK_ADDED_CHANGES_CHECKBOX";

    private final CheckinProjectPanel panel;

    public CommitHandler(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    public static boolean isCheckChangesEnabled() {
        return PropertiesComponent.getInstance().getBoolean(CHECK_ADDED_CHANGES_CHECKBOX, true);
    }

    @Override
    public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        final JCheckBox checkBox = new JCheckBox("Check added changes for keywords specified in check_commit.txt");

        return new RefreshableOnComponent() {
            @Override
            public JComponent getComponent() {
                final JPanel root = new JPanel(new BorderLayout());
                root.add(checkBox, "West");
                return root;
            }

            @Override
            public void refresh() {
            }

            @Override
            public void saveState() {
                PropertiesComponent.getInstance().setValue(CHECK_ADDED_CHANGES_CHECKBOX, checkBox.isSelected());
            }

            @Override
            public void restoreState() {
                checkBox.setSelected(isCheckChangesEnabled());
            }
        };
    }

    private Optional<String> containsKeyword(List<String> keywords) {
        Set<Change> selectedChanges = panel.getSelectedChanges().stream()
                .filter(change -> change.getType() == Change.Type.MODIFICATION || change.getType() == Change.Type.NEW)
                .collect(Collectors.toSet());

        return selectedChanges.stream()
                .map(change -> ChangeChecker.findMatch(change, keywords))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public ReturnResult beforeCheckin() {
        if (!isCheckChangesEnabled()) {
            return super.beforeCheckin();
        }

        List<String> keywords = FileUtil.loadKeywords(panel);
        if (keywords.isEmpty()) {
            return super.beforeCheckin();
        }

        Optional<String> found = containsKeyword(keywords);
        if (found.isEmpty()) {
            return super.beforeCheckin();
        }

        final String html =
                "<html><body>"
                        + "Files selected for commit include keyword '" + found.get() + "'."
                        + "<br>"
                        + "<br>"
                        + "Do you wish to continue ?"
                        + "</body></html>";

        int forceCommit = Messages.showYesNoDialog(html, "Forbidden Keyword Is Found", UIUtil.getWarningIcon());

        if (forceCommit == Messages.YES) {
            return ReturnResult.COMMIT;
        }

        return ReturnResult.CANCEL;
    }

}
