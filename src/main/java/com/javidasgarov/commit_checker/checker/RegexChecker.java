package com.javidasgarov.commit_checker.checker;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RegexChecker {

    public static boolean isRegex(String input) {
        return input != null && input.length() > 2 && input.startsWith("/") && input.endsWith("/");
    }

    @SneakyThrows
    public static boolean matches(String content, String regex) {
        if(isRegex(regex)){
            regex = regex.substring(1, regex.length()-1);
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    public static Optional<String> findRegexMatch(String fileName, List<String> regexes) {
        return regexes.stream()
                .filter(regex -> matches(fileName, regex))
                .findFirst();
    }
}
