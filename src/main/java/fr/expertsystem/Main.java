package fr.expertsystem;

import fr.expertsystem.data.*;
import fr.expertsystem.data.graph.FactSolver;
import fr.expertsystem.data.graph.Graph;
import fr.expertsystem.gui.Visualiser;
import fr.expertsystem.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main
{
    public static void main(String... args)
    {
        List<String> argsList = Arrays.asList(args);
        if (argsList.isEmpty())
        {
            System.err.println("Usage: file [-g] [-v]");
            return;
        }
        List<String> lines;
        try
        {
            Path path = Paths.get(args[0]);
            if (!path.toFile().exists())
            {
                System.err.println("File not found");
                return;
            }
            lines = Files.readAllLines(path);
        }
        catch (InvalidPathException ignored)
        {
            System.err.println("Error: path is invalid!");
            return;
        }
        catch (IOException e)
        {
            System.err.println("Error: an I/O exception occurred!");
            return;
        }

        Parser.Result result = Parser.parseLines(lines);
        if (!result.isOk())
        {
            System.err.printf("Error: %s\n", result.getError());
            return;
        }

        if (argsList.contains("-v"))
            result.getRules().forEach(System.out::println);

        List<Rule> rules = RuleExpander.expandRules(new ArrayList<>(result.getRules()));

        if (argsList.contains("-v"))
        {
            System.out.println("Expanding rules...");
            rules.forEach(System.out::println);
        }
        try
        {
            GlobalState state = runSolver(result.getInitialFacts(), result.getQueryFacts(), rules);
            if (argsList.contains("-g"))
                Visualiser.start(result, state, rules);
        } catch (RuntimeException ex)
        {
            System.err.printf("Error: %s\n", ex.getMessage());
        }
    }

    public static GlobalState runSolver(List<Fact> initialFacts, List<Fact> queryFacts, List<Rule> rules) throws RuntimeException
    {
        Graph graph = new Graph();
        rules.forEach(graph::addRule);

        GlobalState state = new GlobalState();
        for (Fact initialFact : initialFacts)
            state.setFactState(initialFact, FactState.TRUE);

        for (Fact queryFact : queryFacts)
        {
            FactSolver.query(queryFact, state, graph);
            System.out.println("Solving: " + queryFact + " = " + state.getFactState(queryFact));
        }
        System.out.println("Solving done.");

        return state;
    }
}
