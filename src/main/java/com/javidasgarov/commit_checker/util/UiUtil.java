package com.javidasgarov.commit_checker.util;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.ui.UIUtil;
import com.javidasgarov.commit_checker.dto.Matchable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class UiUtil {

    public static final String CONFIRM_OPTION = "Yes, I Know What I'm Doing";
    public static final String NO = "No";
    private static final String CHECK_ADDED_CHANGES_CHECKBOX = "CHECK_ADDED_CHANGES_CHECKBOX";
    public static final String CHECKBOX_LABEL = "Check added changes for files/keywords specified in check_commit.yml (.txt)";

    public static boolean isCheckboxChecked() {
        return PropertiesComponent.getInstance().getBoolean(CHECK_ADDED_CHANGES_CHECKBOX, true);
    }

    public static int getConfirmCommit(Matchable foundMatch) {
        return Messages.showDialog(
                UiUtil.getMessageBody(foundMatch),
                foundMatch.getPopupTitle(),
                new String[]{CONFIRM_OPTION, NO},
                1,
                UIUtil.getErrorIcon()
        );
    }

    private static String getMessageBody(Matchable foundMatch) {
        return "<html>" +
                "<body>"
                + foundMatch.getPopupMessage()
                + "<br>"
                + "<br>"
                + "Are you sure you want to commit?"
                + "</body>" +
                "</html>";
    }

    @NotNull
    public static RefreshableOnComponent getRefreshableOnComponent() {
        final JCheckBox checkBox = new JCheckBox(CHECKBOX_LABEL);

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
                checkBox.setSelected(isCheckboxChecked());
            }
        };
    }

}
