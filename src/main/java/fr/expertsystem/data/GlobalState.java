package fr.expertsystem.data;

import java.util.HashMap;
import java.util.Map;

public class GlobalState
{
    private Map<Fact, FactState> factsMap;

    public GlobalState()
    {
        this.factsMap = new HashMap<>();
    }

    public FactState getFactState(Fact fact)
    {
        return this.factsMap.getOrDefault(fact, FactState.FALSE);
    }

    public void setFactState(Fact fact, FactState state)
    {
        this.factsMap.put(fact, state);
    }

    public GlobalState copy()
    {
        GlobalState copy = new GlobalState();
        copy.factsMap.putAll(factsMap);

        return copy;
    }
}
