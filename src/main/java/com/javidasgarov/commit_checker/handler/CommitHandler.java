package com.javidasgarov.commit_checker.handler;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.javidasgarov.commit_checker.dto.Matchable;
import com.javidasgarov.commit_checker.util.FileUtil;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.intellij.openapi.ui.Messages.YES;
import static com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult.CANCEL;
import static com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult.COMMIT;
import static com.javidasgarov.commit_checker.checker.ChangeChecker.containsKeyword;
import static com.javidasgarov.commit_checker.checker.ChangeChecker.fileIsStaged;
import static com.javidasgarov.commit_checker.util.UiUtil.*;

public class CommitHandler extends CheckinHandler {

    private final CheckinProjectPanel panel;

    public CommitHandler(CheckinProjectPanel panel) {
        this.panel = panel;
    }

    @Override
    public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        return getRefreshableOnComponent();
    }

    @Override
    public ReturnResult beforeCheckin() {
        if (!isCheckboxChecked()) {
            return super.beforeCheckin();
        }

        GitRepositoryManager.getInstance(panel.getProject()).getRepositories().get(0).getCurrentBranch();

        List<String> readKeywords = FileUtil.loadKeywords(panel);
        List<String> readFilenames = FileUtil.loadFilenames(panel);
        if (noKeywordsOrFilesAreDefined(readKeywords, readFilenames)) {
            return super.beforeCheckin();
        }

        Optional<Matchable> foundKeywordMatch = containsKeyword(panel.getSelectedChanges(), readKeywords);
        Optional<Matchable> foundFileNameMatch = fileIsStaged(panel.getSelectedChanges(), readFilenames);
        if (foundKeywordMatch.isEmpty() && foundFileNameMatch.isEmpty()) {
            return super.beforeCheckin();
        }

        int confirmCommit = getConfirmCommit(foundFileNameMatch.orElse(foundKeywordMatch.get()));
        return confirmCommit == YES ? COMMIT : CANCEL;
    }

    private boolean noKeywordsOrFilesAreDefined(List<String> readKeywords, List<String> readFilenames) {
        return readKeywords.isEmpty() && readFilenames.isEmpty();
    }
}
