package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCompositeRuleSolver
{
    @Test
    void andWithOr()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.AND).fact("B").cond(Conditions.OR).fact("C")
                .imply().fact("D").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("B"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.FALSE);

        state.setFactState(new Fact("C"), FactState.TRUE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);
    }

    @Test
    void andWithNegate()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.AND).cond(Conditions.NOT).fact("B")
                .imply().fact("C").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.FALSE);

        state.setFactState(new Fact("B"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);
    }

    @Test
    void everythingMixedTogetherWhatHaveWeDone()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        // A + B ^ !C | D
        Rule rule = Rule.build().fact("A").cond(Conditions.AND).fact("B").cond(Conditions.XOR).cond(Conditions.NOT)
                .fact("C").cond(Conditions.OR).fact("D").imply().fact("E").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.FALSE);

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("C"), FactState.TRUE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);

        state.setFactState(new Fact("A"), FactState.FALSE);
        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.FALSE);
    }
}
