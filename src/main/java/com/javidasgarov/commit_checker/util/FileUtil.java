package com.javidasgarov.commit_checker.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.javidasgarov.commit_checker.dto.CommitCheckerDto;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtil {

    public static final String YML_FILE_NAME = "commit_checker.yml";
    public static final String TXT_FILE_NAME = "commit_checker.txt";

    public static List<String> loadKeywords(CheckinProjectPanel panel) {
        return getYmlFile(panel.getProject())
                .map(FileUtil::loadKeywordsFromYml)
                .orElseGet(() -> loadKeywordsFromTxt(panel.getProject()));
    }

    public static List<String> loadFilenames(CheckinProjectPanel panel) {
        return getYmlFile(panel.getProject())
                .map(YmlUtil::readFromFile)
                .map(CommitCheckerDto::getFiles)
                .orElseGet(() -> loadKeywordsFromTxt(panel.getProject()));
    }

    private static Optional<VirtualFile> getYmlFile(Project project) {
        return FilenameIndex.getVirtualFilesByName(project, YML_FILE_NAME, GlobalSearchScope.projectScope(project))
                .stream()
                .findFirst();
    }

    private static List<String> loadKeywordsFromYml(VirtualFile ymlFile) {
        return YmlUtil.readFromFile(ymlFile).getKeywords();
    }

    private static List<String> loadKeywordsFromTxt(Project project) {
        return FilenameIndex.getVirtualFilesByName(project, TXT_FILE_NAME, GlobalSearchScope.projectScope(project))
                .stream()
                .map(FileUtil::getContent)
                .map(content -> content.split(","))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(word -> word.length() > 0)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private static String getContent(VirtualFile virtualFile) {
        return new String(virtualFile.contentsToByteArray());
    }
}
