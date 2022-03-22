package com.javidasgarov.commit_checker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

import static com.javidasgarov.commit_checker.dto.Constants.FORBIDDEN_KEYWORD_IS_FOUND;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegexKeywordMatch implements Matchable {

    public static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD = "Changes staged for commit include file '%s' " +
            "that contains regex-match for '%s'";

    private String stagedFileName;
    private String matchedRegex;

    @Override
    public String getPopupTitle() {
        return FORBIDDEN_KEYWORD_IS_FOUND;
    }

    @Override
    public String getPopupMessage() {
        return String.format(CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD, stagedFileName, matchedRegex);
    }
}
