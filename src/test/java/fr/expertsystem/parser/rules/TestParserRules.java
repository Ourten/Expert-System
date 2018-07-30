package fr.expertsystem.parser.rules;

import fr.expertsystem.data.Rule;
import fr.expertsystem.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestParserRules
{
    private static final char COMMENT_CHAR = '#';
    private static final char INIT_CHAR    = '?';
    private static final char QUERY_CHAR   = '=';

    List<String> fileToRawRules(String testFileName) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(String.format("src/test/java/fr/expertsystem/parser/%s.txt"
                , testFileName))).stream().filter((line) ->
        {
            return !line.isEmpty() && line.charAt(0) != COMMENT_CHAR;
        }).collect(Collectors.toList());

        return lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c != INIT_CHAR && c != QUERY_CHAR;
        }).collect(Collectors.toList());
    }

    List<String> recreateRules(String testFileName) throws IOException
    {
        return Parser.parseRules(fileToRawRules(testFileName)).stream().map(Rule::toString).collect(Collectors.toList());
    }

    void testParserOutput(String testFileName) throws IOException
    {
        Function<String, String> spaceCleaner = (str) ->
                str.replaceAll("\\s+", " ").trim();
        List<String> rules = fileToRawRules(testFileName).stream().map(spaceCleaner).collect(Collectors.toList());
        List<String> recreatedRules = recreateRules(testFileName).stream().map(String::trim).collect(Collectors.toList());;
        assertThat(rules).containsAll(recreatedRules);
    }

    @Test
    void basicOrConditions() throws IOException
    {
        testParserOutput("basic_or_conditions");
    }

    @Test
    void basicXorConditions() throws IOException
    {
        testParserOutput("basic_xor_conditions");
    }

    @Test
    void basicNotConditions() throws IOException
    {
        testParserOutput("basic_not_conditions");
    }

    @Test
    void sameConclusion() throws IOException
    {
        testParserOutput("same_conclusion");
    }

    @Test
    void andConditionsConclusions() throws IOException
    {
        testParserOutput("and_conditions_conclusions");
    }
}
