package fr.expertsystem.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleExpander
{
    public static List<Rule> expandRules(List<Rule> original)
    {
        List<Rule> rules = new ArrayList<>();

        Integer index = 0;
        for (Rule rule : original)
            rules.addAll(expandRule(rule, index));
        return rules;
    }

    public static List<Rule> expandRule(Rule rule, Integer currentIndex)
    {
        if (rule.getLeftPart().getElements().contains(Conditions.OPEN_PARENTHESIS))
        {
            List<IRuleElement> leftElements = rule.getLeftPart().getElements();

            int startParenthesis = leftElements.indexOf(Conditions.OPEN_PARENTHESIS);
            int endParenthesis = getNextParenthesis(leftElements);
            List<IRuleElement> subList = leftElements.subList(startParenthesis, endParenthesis + 1);

            Rule.RulePart leftPart = new Rule.RulePart(new ArrayList<>(subList.subList(1, subList.size() - 1)));
            Rule.RulePart rightPart = new Rule.RulePart(Collections.singletonList(new Fact("$" + currentIndex)));

            Rule subRule = new Rule(leftPart, rightPart);

            subList.clear();
            leftElements.add(startParenthesis, new Fact("$" + currentIndex));
            currentIndex++;

            List<Rule> rules = new ArrayList<>(expandRule(subRule, currentIndex));
            rules.addAll(expandRule(rule, currentIndex));
            return rules;
        }
        else
            return Collections.singletonList(rule);
    }

    private static int getNextParenthesis(List<IRuleElement> elements)
    {
        int count = 0;

        for (int i = elements.indexOf(Conditions.OPEN_PARENTHESIS), elementsSize = elements.size(); i < elementsSize; i++)
        {
            IRuleElement element = elements.get(i);
            if (element == Conditions.OPEN_PARENTHESIS)
                count++;
            if (element == Conditions.CLOSE_PARENTHESIS)
                count--;

            if (count == 0)
                return i;
        }
        return -1;
    }
}
