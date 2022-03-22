package com.javidasgarov.commit_checker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javidasgarov.commit_checker.dto.Constants.FORBIDDEN_KEYWORD_IS_FOUND;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlainKeywordMatch implements Matchable {

    private static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD =
            "File staged for commit '%s' contains keyword '%s'";

    private String stagedFileName;
    private String foundKeyword;

    @Override
    public String getPopupTitle() {
        return FORBIDDEN_KEYWORD_IS_FOUND;
    }

    @Override
    public String getPopupMessage() {
        return String.format(CHANGES_STAGED_FOR_COMMIT_CONTAIN_KEYWORD, stagedFileName, foundKeyword);
    }

}
