package fr.expertsystem.data;

import java.util.HashMap;
import java.util.Map;

public class ExpandedRuleMap
{
    private Map<Rule, Rule> rules;

    public ExpandedRuleMap()
    {
        rules = new HashMap<>();
    }

    public void addExpanded(Rule original, Rule subRule)
    {
        this.rules.put(original, subRule);
    }

    public Map<Rule, Rule> getRules()
    {
        return rules;
    }
}
