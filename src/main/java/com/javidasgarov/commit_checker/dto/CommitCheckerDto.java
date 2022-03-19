package com.javidasgarov.commit_checker.dto;

import java.util.ArrayList;
import java.util.List;

public class CommitCheckerDto {
    private List<String> keywords = new ArrayList<>();
    private List<String> files = new ArrayList<>();

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
