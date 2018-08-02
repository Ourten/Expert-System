package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSimpleRuleSolver
{
    @Test
    void simpleAnd()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.AND).fact("B").imply().fact("C").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();

        state.setFactState(new Fact("B"), FactState.FALSE);

        assertThat(FactSolver.parseRule(null, rule, state)).isFalse();
    }

    @Test
    void simpleOr()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.OR).fact("B").imply().fact("C").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();

        state.setFactState(new Fact("B"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isFalse();
    }

    @Test
    void simpleXor()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.XOR).fact("B").imply().fact("C").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isFalse();

        state.setFactState(new Fact("B"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isFalse();

        state.setFactState(new Fact("B"), FactState.TRUE);
        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();
    }

    @Test
    void simpleNot()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);

        Rule rule = Rule.build().cond(Conditions.NOT).fact("A").imply().fact("B").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isFalse();

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isTrue();
    }
}
