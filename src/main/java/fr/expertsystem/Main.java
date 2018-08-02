package fr.expertsystem;

import fr.expertsystem.data.*;
import fr.expertsystem.data.graph.FactSolver;
import fr.expertsystem.data.graph.Graph;
import fr.expertsystem.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Main {

    public static void main(String... args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        Parser.Result result = Parser.parseLines(lines);
        if (!result.isOk()) {
            System.err.println(String.format("Error: %s", result.getError()));
            return;
        }

        List<Rule> rules = result.getRules();

        rules.forEach(System.out::println);
        rules = RuleExpander.expandRules(rules);

        System.out.println("Expanding rules...");
        rules.forEach(System.out::println);

        Graph graph = new Graph();
        rules.forEach(graph::addRule);

        GlobalState state = new GlobalState();
        for (Fact initialFact : result.getInitialFacts())
            state.setFactState(initialFact, FactState.TRUE);

        for (Fact queryFact : result.getQueryFacts())
            solveFact(queryFact, state, graph);
    }

    private static void solveFact(Fact fact, GlobalState state, Graph graph) {
        // FIXME: set resulting Factr state in GlobalState?
        System.out.println("Solving: " + fact + " = " + FactSolver.query(fact, state, graph));
    }
}
