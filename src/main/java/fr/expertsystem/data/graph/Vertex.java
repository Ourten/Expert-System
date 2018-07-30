package fr.expertsystem.data.graph;

import fr.expertsystem.data.Fact;

import java.util.HashSet;
import java.util.Set;

public class Vertex
{
    private Fact      fact;
    private Set<Edge> edgesTo;
    private Set<Edge> edgesFrom;

    public Vertex(Fact fact)
    {
        this.fact = fact;

        this.edgesTo = new HashSet<>();
        this.edgesFrom = new HashSet<>();
    }

    public Fact getFact()
    {
        return fact;
    }

    public Set<Edge> getEdgesTo()
    {
        return edgesTo;
    }

    public Set<Edge> getEdgesFrom()
    {
        return edgesFrom;
    }
}
