package com.javidasgarov.commit_checker.checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.javidasgarov.commit_checker.dto.CommitCheckerDto;
import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.javidasgarov.commit_checker.checker.RegexChecker.findRegexMatch;
import static com.javidasgarov.commit_checker.checker.RegexChecker.matches;

public class RegexCheckerTest extends TestCase {

    public static final String TEST_YML = "src/test/resources/test.yml";

    @ParameterizedTest
    @ValueSource(strings = {"/abc/", "/[A-z]*/"})
    public void testIsRegex_happyPath(String input) {
        Assertions.assertTrue(RegexChecker.isRegex(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "demo", "", "/asfs", "asfas/"})
    public void testIsRegex_ShouldReturnFalseForWrongInputs(String input) {
        Assertions.assertFalse(RegexChecker.isRegex(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/went/", "/[A-Z]{1}/", "/[a-z]+/", "/\\d/", "/\\w/"})
    public void testContains_happyPath(String regex) {
        String content = "I went to shop 55 times this year.";
        Assertions.assertTrue(matches(content, regex));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/abra/", "/[A-Z]{2}/"})
    public void testContains_shouldNotFindTheseRegexes(String regex) {
        String content = "I went to shop 55 times this year.";
        Assertions.assertFalse(matches(content, regex));
    }

    @Test
    public void testContains_shouldWorkCorrectlyWhenReadingFromYml() {
        var dto = getCommitCheckerDtoFromFile("src/test/resources/test.yml");
        List<String> keywords = dto.getKeywords();

        String content = "An apple a day keeps a doctor away. But if you do want an APPLE, or even2 apples, try calling 555-555.";

        keywords.forEach(regex -> Assertions.assertTrue(matches(content, regex)));
    }

    @Test
    public void testContains_shouldCorrectlyNotFindNotExistingWhenReadingFromYml() {
        var dto = getCommitCheckerDtoFromFile(TEST_YML);
        List<String> keywords = dto.getKeywords();

        String content = "There is no word here that matches.";

        keywords.forEach(regex -> Assertions.assertFalse(matches(content, regex)));
    }

    @SneakyThrows
    private CommitCheckerDto getCommitCheckerDtoFromFile(String path) {
        String fileContent = Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));

        var objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(fileContent.getBytes(), CommitCheckerDto.class);
    }


    @ParameterizedTest
    @ValueSource(strings = {"demo.html", "app.component.spec.ts"})
    public void isARegexMatch_shouldWorkCorrectlyWhenReadingFromYml(String stagedFileName) {
        var dto = getCommitCheckerDtoFromFile("src/test/resources/test.yml");
        List<String> regexes = dto.getFiles(); //checking if regex for filenames work as expected

        Assertions.assertTrue(findRegexMatch(stagedFileName, regexes).isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"demo.java", "style.css"})
    public void isARegexMatch_shouldWorkCorrectlyWhenReadingFromYml_WhenNoMatch(String stagedFileName) {
        var dto = getCommitCheckerDtoFromFile("src/test/resources/test.yml");
        List<String> regexes = dto.getFiles();

        Assertions.assertFalse(findRegexMatch(stagedFileName, regexes).isPresent());
    }

}