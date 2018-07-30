package fr.expertsystem.data;

import java.util.Objects;

public class MutableFact extends Fact
{
    private boolean negated;

    public MutableFact(String ID, boolean negated)
    {
        super(ID);

        this.negated = negated;
    }

    public MutableFact(Fact fact, boolean negated)
    {
        this(fact.getID(), negated);
    }

    public boolean isNegated()
    {
        return negated;
    }

    public void setNegated(boolean negated)
    {
        this.negated = negated;
    }

    public void setID(String ID)
    {
        this.ID = ID;
    }

    @Override
    public String toString()
    {
        return (negated ? "!" : "") + this.getID();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MutableFact that = (MutableFact) o;
        return isNegated() == that.isNegated();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), isNegated());
    }
}
