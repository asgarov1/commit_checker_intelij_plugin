package com.javidasgarov.commit_checker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.javidasgarov.commit_checker.dto.Constants.FORBIDDEN_FILE_IS_FOUND;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlainFilenameMatch implements Matchable {

    public static final String CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE = "Changes staged for commit contain file '%s'";

    private String stagedFileName;

    @Override
    public String getPopupTitle() {
        return FORBIDDEN_FILE_IS_FOUND;
    }

    @Override
    public String getPopupMessage() {
        return String.format(CHANGES_STAGED_FOR_COMMIT_CONTAIN_FILE, stagedFileName);
    }

}
