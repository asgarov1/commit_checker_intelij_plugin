package com.javidasgarov.commit_checker.checker;

import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChangeChecker {

    public static Optional<String> findMatch(Change change, List<String> keywords) {
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
        return revision.getContent().contains(keyword);
    }

    @SneakyThrows
    private static boolean doesNotContain(ContentRevision revision, String keyword) {
        return !revision.getContent().contains(keyword);
    }
}
