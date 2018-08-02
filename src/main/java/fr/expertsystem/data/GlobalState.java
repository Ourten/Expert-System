package fr.expertsystem.data;

import java.util.HashMap;
import java.util.Map;

public class GlobalState
{
    private Map<Fact, FactState> factsMap;
    private Map<Rule, Boolean>   ruleMap;

    public GlobalState()
    {
        this.factsMap = new HashMap<>();
        this.ruleMap = new HashMap<>();
    }

    public FactState getFactState(Fact fact)
    {
        return this.factsMap.getOrDefault(fact, FactState.FALSE);
    }

    public void setFactState(Fact fact, FactState state)
    {
        this.factsMap.put(fact, state);
    }

    public boolean containsRule(Rule rule)
    {
        return this.ruleMap.containsKey(rule);
    }

    public boolean getRuleState(Rule rule)
    {
        return this.ruleMap.getOrDefault(rule, Boolean.FALSE);
    }

    public void setRuleState(Rule rule, boolean state)
    {
        this.ruleMap.put(rule, state);
    }

    public GlobalState copy()
    {
        GlobalState copy = new GlobalState();
        copy.factsMap.putAll(factsMap);
        copy.ruleMap.putAll(ruleMap);

        return copy;
    }
}
