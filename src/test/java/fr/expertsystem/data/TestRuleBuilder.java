package fr.expertsystem.data;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRuleBuilder
{
    @Test
    void simpleRule()
    {
        Rule rule = Rule.build().fact("A").cond(Conditions.AND).fact("B").imply().fact("C").create();
        MutableFact A = new MutableFact("A", false);
        MutableFact B = new MutableFact("B", false);
        MutableFact C = new MutableFact("C", false);

        assertThat(rule.getDependencies()).contains(A, B);
        assertThat(rule.getDependents()).containsOnly(C);

        assertThat(rule.toString()).isEqualToIgnoringWhitespace("A + B => C");
    }
}
