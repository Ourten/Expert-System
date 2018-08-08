package fr.expertsystem.parser.rules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ParserUtils {
    static List<String> fileToString(String testFileName) throws IOException
    {
        return Files.readAllLines(Paths.get(String.format("src/test/resources/fr/expertsystem/tests/parser/rules/%s.txt"
                , testFileName)));
    }
}
