package fr.expertsystem.data.graph;

import fr.expertsystem.data.Fact;
import fr.expertsystem.data.Rule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph
{
    private Set<Edge>   edges;
    private Set<Vertex> vertices;

    public Graph()
    {
        this.edges = new HashSet<>();
        this.vertices = new HashSet<>();
    }

    public boolean containsFact(Fact fact)
    {
        return this.vertices.stream().anyMatch(vertex -> vertex.getFact().equals(fact));
    }

    public boolean containsVertex(Vertex vertex)
    {
        return this.containsFact(vertex.getFact());
    }

    public Vertex getByFact(Fact fact)
    {
        return this.vertices.stream().filter(vertex -> vertex.getFact().equals(fact)).findFirst()
                .orElseThrow(() -> new RuntimeException("No Vertex containing this Fact exist!"));
    }

    public Vertex getOrCreate(Fact fact)
    {
        if (!this.containsFact(fact))
            this.vertices.add(new Vertex(fact));
        return this.getByFact(fact);
    }

    public void addEdge(Edge edge, Set<Vertex> from, Set<Vertex> to)
    {
        edge.getFrom().addAll(from);
        edge.getTo().addAll(to);

        from.forEach(vertex -> vertex.getEdgesFrom().add(edge));
        to.forEach(vertex -> vertex.getEdgesTo().add(edge));
        this.edges.add(edge);
    }

    public void addRule(Rule rule)
    {
        Set<Vertex> from = rule.getDependencies().stream().map(this::getOrCreate).collect(Collectors.toSet());
        Set<Vertex> to = rule.getDependents().stream().map(this::getOrCreate).collect(Collectors.toSet());

        Edge edge = new Edge(rule);

        this.addEdge(edge, from, to);
    }

    public Set<Fact> getFactsDependent(Fact fact)
    {
        return this.getByFact(fact).getEdgesTo().stream()
                .flatMap(edge -> edge.getFrom().stream().map(Vertex::getFact))
                .collect(Collectors.toSet());
    }

    public Set<Fact> getFactsDepending(Fact fact)
    {
        return this.getByFact(fact).getEdgesFrom().stream()
                .flatMap(edge -> edge.getTo().stream().map(Vertex::getFact))
                .collect(Collectors.toSet());
    }
}
