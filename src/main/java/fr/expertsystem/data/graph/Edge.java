package fr.expertsystem.data.graph;

import fr.expertsystem.data.Rule;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Edge
{
    private Rule        rule;
    private Set<Vertex> from;
    private Set<Vertex> to;

    public Edge(Rule rule)
    {
        this.rule = rule;

        this.from = new HashSet<>();
        this.to = new HashSet<>();
    }

    public Rule getRule()
    {
        return rule;
    }

    public Set<Vertex> getFrom()
    {
        return from;
    }

    public Set<Vertex> getTo()
    {
        return to;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(getRule(), edge.getRule()) &&
                Objects.equals(getFrom(), edge.getFrom()) &&
                Objects.equals(getTo(), edge.getTo());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getRule(), getFrom(), getTo());
    }
}
