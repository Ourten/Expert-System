package fr.expertsystem;

import fr.expertsystem.data.Rule;
import fr.expertsystem.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class Main
{
    private static final char COMMENT_CHAR = '#';
    private static final char INIT_CHAR    = '?';
    private static final char QUERY_CHAR   = '=';

    public static void main(String... args) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(args[0])).stream().filter((line) ->
        {
            return !line.isEmpty() && line.charAt(0) != COMMENT_CHAR;
        }).collect(Collectors.toList());

        List<String> rawRules = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c != INIT_CHAR && c != QUERY_CHAR;
        }).collect(Collectors.toList());

        List<Rule> rules = Parser.parseRules(rawRules);

        for (Rule rule : rules)
        {
            System.out.println(rule);
        }
    }
}
