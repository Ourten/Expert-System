package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestParenthesisRuleSolver
{
    @Test
    void andOrParenthesis()
    {
        GlobalState state = new GlobalState();
        state.setFactState(new Fact("A"), FactState.TRUE);
        state.setFactState(new Fact("B"), FactState.TRUE);

        Rule rule = Rule.build().fact("A").cond(Conditions.AND)
                .cond(Conditions.OPEN_PARENTHESIS).fact("B").cond(Conditions.OR).fact("C").imply().fact("D").create();

        assertThat(FactSolver.parseRule(null, rule, state)).isEqualTo(FactState.TRUE);
    }
}
