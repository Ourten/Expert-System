package fr.expertsystem.data;

import java.util.Objects;

public class Fact implements IRuleElement
{
    protected String ID;

    public Fact(String ID)
    {
        this.ID = ID;
    }

    public String getID()
    {
        return ID;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fact fact = (Fact) o;
        return Objects.equals(getID(), fact.getID());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getID());
    }
}
