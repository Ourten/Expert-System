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

public class TestParserRules {
    private static final char COMMENT_CHAR = '#';
    private static final char INIT_CHAR = '?';
    private static final char QUERY_CHAR = '=';

    List<String> fileToString(String testFileName) throws IOException {
        return Files.readAllLines(Paths.get(String.format("src/test/java/fr/expertsystem/parser/%s.txt"
                , testFileName)));
    }

    List<String> recreateRules(String testFileName) throws IOException {
        Parser.Result result = Parser.parseLines(fileToString(testFileName));
        if (!result.isOk())
            throw new RuntimeException(result.getError());
        return result.getRules().stream().map(Rule::toString).collect(Collectors.toList());
    }

    void testParserOutput(String testFileName) throws IOException {
        Function<String, String> spaceCleaner = (str) ->
                str.replaceAll("\\s+", " ").trim();
        List<String> rules = fileToString(testFileName).stream().map(spaceCleaner).collect(Collectors.toList());
        List<String> recreatedRules = recreateRules(testFileName).stream().map(String::trim).collect(Collectors.toList());
        ;
        assertThat(rules).containsAll(recreatedRules);
    }

    @Test
    void basicOrConditions() throws IOException {
        testParserOutput("basic_or_conditions");
    }

    @Test
    void basicXorConditions() throws IOException {
        testParserOutput("basic_xor_conditions");
    }

    @Test
    void basicNotConditions() throws IOException {
        testParserOutput("basic_not_conditions");
    }

    @Test
    void sameConclusion() throws IOException {
        testParserOutput("same_conclusion");
    }

    @Test
    void andConditionsConclusions() throws IOException {
        testParserOutput("and_conditions_conclusions");
    }
}
