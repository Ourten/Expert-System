package fr.expertsystem.data;

public enum Conditions
{
    AND,
    OR,
    XOR;

    @Override
    public String toString()
    {
        switch (this)
        {
            case AND:
                return "+";
            case OR:
                return "|";
            case XOR:
                return "^";
            default:
                return "???";
        }
    }
}
