package com.javidasgarov.commit_checker.handler;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.ui.UIUtil;
import com.javidasgarov.commit_checker.util.TextUtil;
import com.javidasgarov.commit_checker.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.javidasgarov.commit_checker.checker.ChangeChecker.containsKeyword;
import static com.javidasgarov.commit_checker.checker.ChangeChecker.fileIsStaged;

public class CommitHandler extends CheckinHandler {

    private static final String CHECK_ADDED_CHANGES_CHECKBOX = "CHECK_ADDED_CHANGES_CHECKBOX";
    public static final String CONFIRM_OPTION = "Yes, I Know What I'm Doing";
    public static final String NO = "No";

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

    @Override
    public ReturnResult beforeCheckin() {
        if (!isCheckChangesEnabled()) {
            return super.beforeCheckin(); //Check turned off
        }

        List<String> keywords = FileUtil.loadKeywords(panel);
        List<String> filenames = FileUtil.loadFilenames(panel);
        if (keywords.isEmpty() && filenames.isEmpty()) {
            return super.beforeCheckin(); //No keywords/files defined to look for
        }

        Optional<String> foundKeyword = containsKeyword(panel.getSelectedChanges(), keywords);
        Optional<String> foundFileName = fileIsStaged(panel.getSelectedChanges(), filenames);
        if (foundKeyword.isEmpty() && foundFileName.isEmpty()) {
            return super.beforeCheckin();
        }

        int forceCommit = Messages.showDialog(
                TextUtil.getMessageBody(foundFileName, foundKeyword),
                TextUtil.getCorrectTitle(foundFileName, foundKeyword),
                new String[]{CONFIRM_OPTION, NO},
                1,
                UIUtil.getErrorIcon()
        );

        if (forceCommit == Messages.YES) {
            return ReturnResult.COMMIT;
        }

        return ReturnResult.CANCEL;
    }

}
