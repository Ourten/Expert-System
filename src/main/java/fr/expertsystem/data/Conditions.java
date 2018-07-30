package fr.expertsystem.data;

import java.util.Optional;

public enum Conditions
{
    AND('+'),
    OR('|'),
    XOR('^');

    private final char conChar;

    Conditions(char condChar)
    {
        this.conChar = condChar;
    }

    @Override
    public String toString()
    {
        return String.format("%c", getChar());
    }

    public char getChar()
    {
        return conChar;
    }

    public static Optional<Conditions> fromChar(char condChar)
    {
        for (Conditions cond : values())
        {
            if (cond.getChar() == condChar)
                return Optional.of(cond);
        }
        return Optional.empty();
    }
}
