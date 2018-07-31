package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.expertsystem.data.Conditions.*;

public class FactSolver
{
    public static FactState query(Fact query, GlobalState state, Graph graph)
    {
        // Start backward search
        if (graph.containsFact(query) && !graph.getByFact(query).getEdgesTo().isEmpty())
        {
            // Prevent loops
            state.setFactState(query, FactState.UNKNOWN);

            //     graph.getByFact(query).getEdgesTo().forEach(edge -> resolveEdge(edge, ));
        }
        else
            return state.getFactState(query);
        return FactState.FALSE;
    }

    private static FactState resolveEdge(Fact query, Edge edge, Graph graph, GlobalState state)
    {
        if (edge.getFrom().stream().allMatch(vertex -> vertex.getEdgesTo().isEmpty()))
        {
            return parseRule(query, edge.getRule(), state);
        }
        else
            return accumulate(edge.getFrom().stream().map(vertex -> query(vertex.getFact(), state, graph)).collect(Collectors.toList()));
    }

    public static FactState parseRule(Fact query, Rule rule, GlobalState state)
    {
        return cond(rule.getLeftPart().getElements()).test(state) ? FactState.TRUE : FactState.FALSE;
    }

    private static Predicate<GlobalState> cond(List<IRuleElement> part)
    {
        if (part.size() == 1)
        {
            Fact fact = (Fact) part.get(0);
            return state -> state.getFactState(new Fact(fact.getID())) == FactState.TRUE;
        }
        else
        {
            if (part.contains(XOR))
                return state -> cond(part.subList(0, part.indexOf(XOR))).test(state) ^
                        cond(part.subList(part.indexOf(XOR) + 1, part.size())).test(state);

            if (part.contains(OR))
                return cond(part.subList(0, part.indexOf(OR)))
                        .or(cond(part.subList(part.indexOf(OR) + 1, part.size())));

            if (part.contains(AND))
                return cond(part.subList(0, part.indexOf(AND)))
                        .and(cond(part.subList(part.indexOf(AND) + 1, part.size())));

            if (part.contains(NOT))
                return cond(part.subList(part.indexOf(NOT) + 1, part.size())).negate();
        }
        return state -> true;
    }

    private static FactState accumulate(List<FactState> collect)
    {
        for (FactState state : collect)
        {
            if (state == FactState.UNKNOWN)
                return FactState.UNKNOWN;
            if (state == FactState.TRUE)
                return FactState.TRUE;
        }
        return FactState.FALSE;
    }
}
