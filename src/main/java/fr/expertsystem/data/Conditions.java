package fr.expertsystem.data;

import java.util.Optional;

public enum Conditions implements IRuleElement
{
    XOR('^'),
    OR('|'),
    AND('+'),
    NOT('!'),
    OPEN_PARENTHESIS('('),
    CLOSE_PARENTHESIS(')');

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

    public int getPrecedence()
    {
        return this.ordinal();
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
