package com.javidasgarov.commit_checker.util;

import java.util.Optional;

public class TextUtil {

    public static final String FORBIDDEN_FILE_IS_FOUND = "Forbidden File Is Found.";
    public static final String FORBIDDEN_KEYWORD_IS_FOUND = "Forbidden Keyword Is Found";
    public static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD = "Changes staged for commit contain keyword ";
    public static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE = "Changes staged for commit contain file ";

    public static String getCorrectMessage(Optional<String> foundFileName, Optional<String> foundKeyword) {
        return foundFileName.map(fileName -> CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE + "'" + fileName + "'.")
                .orElseGet(() -> CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD + "'" + foundKeyword.get() + "'.");
    }

    public static String getCorrectTitle(Optional<String> foundFileName, Optional<String> foundKeyword) {
        return foundFileName.map(fileName -> FORBIDDEN_FILE_IS_FOUND)
                .orElse(FORBIDDEN_KEYWORD_IS_FOUND);
    }

    public static String getMessageBody(Optional<String> foundFileName, Optional<String> foundKeyword) {
        return "<html>" +
                "<body>"
                + TextUtil.getCorrectMessage(foundFileName, foundKeyword)
                + "<br>"
                + "<br>"
                + "Are you sure you want to commit those?"
                + "</body>" +
                "</html>";
    }
}
