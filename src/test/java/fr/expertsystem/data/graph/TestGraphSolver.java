package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestGraphSolver
{
    @Test
    void expandedRule()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        // A + (B | C) => D
        Rule rule = Rule.build().fact("A").cond(Conditions.AND)
                .cond(Conditions.OPEN_PARENTHESIS).fact("B").cond(Conditions.OR).fact("C").cond(Conditions.CLOSE_PARENTHESIS).imply().fact("D").create();

        Graph graph = new Graph();
        RuleExpander.expandRules(Collections.singletonList(rule), new ExpandedRuleMap()).forEach(graph::addRule);

        assertThat(FactSolver.query(new Fact("D"), state.copy(), graph)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("B"), FactState.FALSE);
        assertThat(FactSolver.query(new Fact("D"), state.copy(), graph)).isEqualTo(FactState.FALSE);
    }

    @Test
    void andConclusion()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        // A + B = C + D
        Rule rule =
                Rule.build().fact("A").cond(Conditions.AND).fact("B").imply().fact("C").cond(Conditions.AND).fact("D").create();

        Graph graph = new Graph();
        graph.addRule(rule);

        assertThat(FactSolver.query(new Fact("C"), state.copy(), graph)).isEqualTo(FactState.TRUE);
        assertThat(FactSolver.query(new Fact("D"), state.copy(), graph)).isEqualTo(FactState.TRUE);
    }

    @Test
    void firstDegreeCircularDep()
    {
        GlobalState state = new GlobalState();

        Rule firstRule = Rule.build().fact("A").imply().fact("B").create();
        Rule secondRule = Rule.build().fact("B").imply().fact("A").create();

        Graph graph = new Graph();
        graph.addRule(firstRule);
        graph.addRule(secondRule);

        assertThrows(RuntimeException.class, () -> FactSolver.query(new Fact("B"), state, graph));
        assertThrows(RuntimeException.class, () -> FactSolver.query(new Fact("A"), state, graph));
    }

    @Test
    void secondDegreeCircularDep()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);

        Rule firstRule = Rule.build().fact("A").cond(Conditions.AND).fact("B").imply().fact("C").create();
        Rule secondRule = Rule.build().fact("C").imply().fact("B").create();

        Graph graph = new Graph();
        graph.addRule(firstRule);
        graph.addRule(secondRule);

        assertThrows(RuntimeException.class, () -> FactSolver.query(new Fact("B"), state, graph));
        assertThrows(RuntimeException.class, () -> FactSolver.query(new Fact("C"), state, graph));
    }

    @Test
    void sameConclusions()
    {
        GlobalState state = new GlobalState();

        Rule firstRule = Rule.build().fact("B").imply().fact("A").create();
        Rule secondRule = Rule.build().fact("C").imply().fact("A").create();

        Graph graph = new Graph();
        graph.addRule(firstRule);
        graph.addRule(secondRule);

        assertThat(FactSolver.query(new Fact("A"), state.copy(), graph)).isEqualTo(FactState.FALSE);

        state.setFactState(new Fact("B"), FactState.TRUE);
        assertThat(FactSolver.query(new Fact("A"), state.copy(), graph)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("B"), FactState.FALSE);
        state.setFactState(new Fact("C"), FactState.TRUE);
        assertThat(FactSolver.query(new Fact("A"), state.copy(), graph)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("B"), FactState.TRUE);
        assertThat(FactSolver.query(new Fact("A"), state.copy(), graph)).isEqualTo(FactState.TRUE);
    }

    @Test
    void negatedConclusions()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);

        // A => !B
        Rule rule = Rule.build().fact("A").imply().cond(Conditions.NOT).fact("B").create();

        Graph graph = new Graph();
        graph.addRule(rule);

        assertThat(FactSolver.query(new Fact("B"), state.copy(), graph)).isEqualTo(FactState.FALSE);

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.query(new Fact("B"), state.copy(), graph)).isEqualTo(FactState.FALSE);
    }
}
