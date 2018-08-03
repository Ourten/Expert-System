package fr.expertsystem;

import fr.expertsystem.data.*;
import fr.expertsystem.data.graph.FactSolver;
import fr.expertsystem.data.graph.Graph;
import fr.expertsystem.gui.Visualiser;
import fr.expertsystem.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main
{

    public static void main(String... args) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        Parser.Result result = Parser.parseLines(lines);
        if (!result.isOk())
        {
            System.err.println(String.format("Error: %s", result.getError()));
            return;
        }

        result.getRules().forEach(System.out::println);
        List<Rule> rules = RuleExpander.expandRules(new ArrayList<>(result.getRules()));

        System.out.println("Expanding rules...");
        rules.forEach(System.out::println);

        Graph graph = new Graph();
        rules.forEach(graph::addRule);

        GlobalState state = new GlobalState();
        for (Fact initialFact : result.getInitialFacts())
            state.setFactState(initialFact, FactState.TRUE);

        for (Fact queryFact : result.getQueryFacts())
            solveFact(queryFact, state, graph);

        if (Arrays.asList(args).contains("-g"))
            Visualiser.start(result, graph, state);
    }

    private static void solveFact(Fact fact, GlobalState state, Graph graph)
    {
        // FIXME: set resulting Factr state in GlobalState?
        System.out.println("Solving: " + fact + " = " + FactSolver.query(fact, state, graph));
    }
}
