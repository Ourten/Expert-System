package fr.expertsystem.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleExpander
{
    private static Integer expandedIndex;

    public static List<Rule> expandRules(List<Rule> original, ExpandedRuleMap ruleMap)
    {
        List<Rule> rules = new ArrayList<>();

        expandedIndex = 0;
        for (Rule rule : original)
        {
            List<Rule> expanded = expandRule(rule.copy());

            expanded.stream().filter(candidate -> candidate.getRightPart().equals(rule.getRightPart())).findFirst()
                    .ifPresent(subRule -> ruleMap.addExpanded(rule, subRule));
            rules.addAll(expanded);
        }
        return rules;
    }

    private static List<Rule> expandRule(Rule rule)
    {
        if (rule.getLeftPart().getElements().contains(Conditions.OPEN_PARENTHESIS))
        {
            List<IRuleElement> leftElements = rule.getLeftPart().getElements();

            int startParenthesis = leftElements.indexOf(Conditions.OPEN_PARENTHESIS);
            int endParenthesis = getNextParenthesis(leftElements);
            List<IRuleElement> subList = leftElements.subList(startParenthesis, endParenthesis + 1);

            Rule.RulePart leftPart = new Rule.RulePart(new ArrayList<>(subList.subList(1, subList.size() - 1)));
            Rule.RulePart rightPart = new Rule.RulePart(Collections.singletonList(new Fact("$" + expandedIndex)));

            Rule subRule = new Rule(leftPart, rightPart);

            subList.clear();
            leftElements.add(startParenthesis, new Fact("$" + expandedIndex));
            expandedIndex++;

            List<Rule> rules = new ArrayList<>(expandRule(subRule));
            rules.addAll(expandRule(rule));
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
