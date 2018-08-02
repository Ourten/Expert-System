package fr.expertsystem.data.graph;

import fr.expertsystem.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fr.expertsystem.data.Conditions.*;

public class FactSolver
{
    public static FactState query(Fact query, GlobalState state, Graph graph)
    {
        return internalQuery(query, state, graph, new ArrayList<>());
    }

    private static FactState internalQuery(Fact query, GlobalState state, Graph graph, List<Edge> openset)
    {
        // Start backward search
        if (graph.containsFact(query) && !graph.getByFact(query).getEdgesTo().isEmpty())
        {
            List<Optional<FactState>> accumulated = graph.getByFact(query).getEdgesTo().stream().map(edge ->
                    resolveEdge(query, edge, graph, state, openset)).filter(Optional::isPresent).distinct().collect(Collectors.toList());

            if (accumulated.size() > 1)
                throw new RuntimeException("Contradiction of facts! Multiples rules that are true contradict each " +
                        "other!");

            // Really this cannot happen. This was only useful in debug.
            if (!accumulated.isEmpty())
                return accumulated.get(0).get();
        }
        return state.getFactState(query);
    }

    private static Optional<FactState> resolveEdge(Fact query, Edge edge, Graph graph, GlobalState state,
                                                   List<Edge> openset)
    {
        // Early exit since rule has already been computed
        if (state.containsRule(edge.getRule()))
        {
            if (state.getRuleState(edge.getRule()))
            {
                state.setFactState(query, getFactFromRuleResult(query, edge.getRule()));
                return Optional.ofNullable(state.getFactState(query));
            }
            return Optional.empty();
        }

        if (openset.contains(edge))
            throw new RuntimeException("Circular dependency detected! Cannot solve " + query);
        openset.add(edge);

        if (edge.getFrom().stream().anyMatch(vertex -> !vertex.getEdgesTo().isEmpty()))
            edge.getFrom().forEach(vertex -> internalQuery(vertex.getFact(), state, graph, openset));

        boolean ruleResult = parseRule(query, edge.getRule(), state);

        state.setRuleState(edge.getRule(), ruleResult);
        if (ruleResult)
        {
            state.setFactState(query, getFactFromRuleResult(query, edge.getRule()));
            return Optional.ofNullable(state.getFactState(query));
        }
        return Optional.empty();
    }

    private static FactState getFactFromRuleResult(Fact fact, Rule rule)
    {
        List<IRuleElement> elements = rule.getRightPart().getElements();

        if (elements.indexOf(fact) != 0)
            return elements.get(elements.indexOf(fact) - 1).equals(NOT) ? FactState.FALSE : FactState.TRUE;

        return FactState.TRUE;
    }

    static boolean parseRule(Fact query, Rule rule, GlobalState state)
    {
        return cond(rule.getLeftPart().getElements()).test(state);
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
}
