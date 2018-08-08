package fr.expertsystem.data;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestRuleExpander
{
    @Test
    void extractParenthesis()
    {
        // A + (B | C) => D
        Rule rule = Rule.build().fact("A").cond(Conditions.AND)
                .cond(Conditions.OPEN_PARENTHESIS).fact("B").cond(Conditions.OR).fact("C").cond(Conditions.CLOSE_PARENTHESIS)
                .imply().fact("D").create();

        List<Rule> expanded = RuleExpander.expandRules(Collections.singletonList(rule), new ExpandedRuleMap());

        assertThat(expanded).hasSize(2);

        // B | C => $0
        assertThat(expanded.get(0)).isEqualTo(Rule.build().fact("B").cond(Conditions.OR).fact("C").imply().fact("$0").create());
        // A | $0 => D
        assertThat(expanded.get(1)).isEqualTo(Rule.build().fact("A").cond(Conditions.AND).fact("$0").imply().fact("D").create());
    }

    @Test
    void extractDeepParenthesis()
    {
        // !(A + (B | C)) => D
        Rule rule = Rule.build().cond(Conditions.NOT).cond(Conditions.OPEN_PARENTHESIS).fact("A").cond(Conditions.AND)
                .cond(Conditions.OPEN_PARENTHESIS).fact("B").cond(Conditions.OR).fact("C").cond(Conditions.CLOSE_PARENTHESIS)
                .cond(Conditions.CLOSE_PARENTHESIS)
                .imply().fact("D").create();

        List<Rule> expanded = RuleExpander.expandRules(Collections.singletonList(rule), new ExpandedRuleMap());
        assertThat(expanded).hasSize(3);

        // B | C => $1
        assertThat(expanded.get(0)).isEqualTo(Rule.build().fact("B").cond(Conditions.OR).fact("C").imply().fact("$1").create());
        // A + $1 => $0
        assertThat(expanded.get(1)).isEqualTo(Rule.build()
                .fact("A").cond(Conditions.AND).fact("$1").imply().fact("$0").create());
        // !$0 => D
        assertThat(expanded.get(2)).isEqualTo(Rule.build().cond(Conditions.NOT).fact("$0").imply().fact("D").create());
    }

    @Test
    void extractSuccessiveParenthesis()
    {
        // (A | B) + (B ^ C) => D
        Rule rule = Rule.build().cond(Conditions.OPEN_PARENTHESIS).fact("A").cond(Conditions.OR).fact("B")
                .cond(Conditions.CLOSE_PARENTHESIS).cond(Conditions.AND).cond(Conditions.OPEN_PARENTHESIS)
                .fact("B").cond(Conditions.XOR).fact("C").cond(Conditions.CLOSE_PARENTHESIS).imply().fact("D").create();

        List<Rule> expanded = RuleExpander.expandRules(Collections.singletonList(rule), new ExpandedRuleMap());
        assertThat(expanded).hasSize(3);

        // A | B => $0
        assertThat(expanded.get(0)).isEqualTo(Rule.build().fact("A").cond(Conditions.OR).fact("B").imply().fact("$0").create());
        // B ^ C => $1
        assertThat(expanded.get(1)).isEqualTo(Rule.build()
                .fact("B").cond(Conditions.XOR).fact("C").imply().fact("$1").create());
        // $0 + $1 => D
        assertThat(expanded.get(2)).isEqualTo(Rule.build().fact("$0").cond(Conditions.AND).fact("$1")
                .imply().fact("D").create());
    }
}
