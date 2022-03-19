package com.javidasgarov.commit_checker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.javidasgarov.commit_checker.dto.CommitCheckerDto;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class YmlUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @SneakyThrows
    public static CommitCheckerDto readFromFile(VirtualFile virtualFile) {
        return objectMapper.readValue(virtualFile.contentsToByteArray(), CommitCheckerDto.class);
    }

}
