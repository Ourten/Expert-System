package fr.expertsystem.data;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRuleBuilder
{
    @Test
    void simpleRule()
    {
        Rule rule = Rule.build().fact("A").cond(Conditions.AND).fact("B").imply().fact("C").create();
        Fact A = new Fact("A");
        Fact B = new Fact("B");
        Fact C = new Fact("C");

        assertThat(rule.getDependencies()).contains(A, B);
        assertThat(rule.getDependents()).containsOnly(C);

        assertThat(rule.toString()).isEqualToIgnoringWhitespace("A + B => C");
    }
}
