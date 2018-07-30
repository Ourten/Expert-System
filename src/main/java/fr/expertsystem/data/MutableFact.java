package fr.expertsystem.data;

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
}
