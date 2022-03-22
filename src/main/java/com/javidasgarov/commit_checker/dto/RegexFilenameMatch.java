package com.javidasgarov.commit_checker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javidasgarov.commit_checker.dto.Constants.FORBIDDEN_FILE_IS_FOUND;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegexFilenameMatch implements Matchable {

    public static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE = "File staged for commit '%s' matches regex '%s'";

    private String stagedFileName;
    private String matchedRegex;

    @Override
    public String getPopupTitle() {
        return FORBIDDEN_FILE_IS_FOUND;
    }

    @Override
    public String getPopupMessage() {
        return String.format(CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE, stagedFileName, matchedRegex);
    }

}
