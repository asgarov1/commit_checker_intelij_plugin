package com.javidasgarov.commit_checker.checker;

import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.javidasgarov.commit_checker.dto.*;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.intellij.openapi.vcs.changes.Change.Type.MODIFICATION;
import static com.intellij.openapi.vcs.changes.Change.Type.NEW;
import static com.javidasgarov.commit_checker.checker.RegexChecker.findRegexMatch;
import static com.javidasgarov.commit_checker.checker.RegexChecker.isRegex;

public class ChangeChecker {

    public static Optional<Matchable> fileIsStaged(Collection<Change> changes, List<String> readFilenames) {
        // abcd
        return changes.stream()
                .filter(change -> List.of(MODIFICATION, NEW).contains(change.getType()))
                .filter(change -> change.getVirtualFile() != null)
                .map(change -> filenameMatches(change, readFilenames))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public static Optional<Matchable> containsKeyword(Collection<Change> changes, List<String> keywords) {
        return changes.stream()
                .filter(change -> List.of(MODIFICATION, NEW).contains(change.getType()))
                .map(change -> ChangeChecker.findMatch(change, keywords))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    /**
     * This method only calls for a match if afterRevision file does include the changed
     * AND beforeRevision file does NOT.
     *
     * @param change   Change object
     * @param keywords keywords to look for
     * @return whether there is a match
     */
    private static Optional<Matchable> findMatch(Change change, List<String> keywords) {
        ContentRevision beforeRevision = change.getBeforeRevision();
        ContentRevision afterRevision = change.getAfterRevision();
        if (afterRevision == null) {
            return Optional.empty();
        }

        Set<String> foundInAfterRevision = keywords
                .stream()
                .filter(keyword -> contains(afterRevision, keyword))
                .collect(Collectors.toSet());

        if (foundInAfterRevision.isEmpty()) {
            return Optional.empty();
        }

        String stagedFileName = Optional.ofNullable(change.getVirtualFile()).map(VirtualFile::getName).orElse("Unknown");
        return foundInAfterRevision
                .stream()
                .filter(keyword -> !contains(beforeRevision, keyword))
                .findFirst()
                .map(match -> isRegex(match) ?
                        new RegexKeywordMatch(stagedFileName, match) :
                        new PlainKeywordMatch(stagedFileName, match));
    }

    @SneakyThrows
    private static boolean contains(ContentRevision revision, String keyword) {
        if (revision == null || revision.getContent() == null) {
            return false;
        }

        if (isRegex(keyword)) {
            return RegexChecker.matches(revision.getContent(), keyword);
        }
        return revision.getContent().contains(keyword);
    }

    private static Optional<Matchable> filenameMatches(Change change, List<String> filenames) {
        String stagedFilename = Optional.ofNullable(change.getVirtualFile()).map(VirtualFile::getName).orElse("Unknown");

        if (filenames.contains(stagedFilename)) {
            return Optional.of(new PlainFilenameMatch(stagedFilename));
        }

        return findRegexMatch(stagedFilename, filenames)
                .map(matchedRegex -> new RegexFilenameMatch(stagedFilename, matchedRegex));

    }
}
