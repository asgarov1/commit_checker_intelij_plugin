package com.javidasgarov.commit_checker.util;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.psi.search.GlobalSearchScope.projectScope;

@UtilityClass
public class FileUtil {

    public static final String FILE_NAME = "commit_checker.txt";

    public static List<String> loadKeywords(CheckinProjectPanel panel) {
        return FileTypeIndex.getFiles(PlainTextFileType.INSTANCE, projectScope(panel.getProject()))
                .stream()
                .filter(file -> file.getName().equals(FILE_NAME))
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
