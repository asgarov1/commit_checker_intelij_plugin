package com.javidasgarov.commit_checker.checker;

import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChangeChecker {

    public static Optional<String> fileIsStaged(Collection<Change> changes, List<String> filenames) {
        return getAddedChanges(changes).stream()
                .filter(change -> change.getVirtualFile() != null)
                .map(change -> change.getVirtualFile().getName())
                .filter(filenames::contains)
                .findFirst();
    }

    private static Set<Change> getAddedChanges(Collection<Change> changes) {
        return changes.stream()
                .filter(change -> change.getType() == Change.Type.MODIFICATION || change.getType() == Change.Type.NEW)
                .collect(Collectors.toSet());
    }

    public static Optional<String> containsKeyword(Collection<Change> changes, List<String> keywords) {
        return getAddedChanges(changes).stream()
                .map(change -> ChangeChecker.findMatch(change, keywords))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private static Optional<String> findMatch(Change change, List<String> keywords) {
        ContentRevision beforeRevision = change.getBeforeRevision();
        ContentRevision afterRevision = change.getAfterRevision();

        if (beforeRevision == null || afterRevision == null) {
            return Optional.empty();
        }

        Set<String> foundInAfterRevision = keywords.stream()
                .filter(keyword -> contains(afterRevision, keyword))
                .collect(Collectors.toSet());

        if (foundInAfterRevision.isEmpty()) {
            return Optional.empty();
        }

        return foundInAfterRevision.stream()
                .filter(keyword -> doesNotContain(beforeRevision, keyword)).findFirst();
    }

    @SneakyThrows
    private static boolean contains(ContentRevision revision, String keyword) {
        return revision.getContent() != null && revision.getContent().contains(keyword);
    }

    @SneakyThrows
    private static boolean doesNotContain(ContentRevision revision, String keyword) {
        return revision.getContent() != null && !revision.getContent().contains(keyword);
    }
}
