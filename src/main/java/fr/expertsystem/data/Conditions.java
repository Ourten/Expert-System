package fr.expertsystem.data;

import java.util.Optional;

public enum Conditions implements IRuleElement
{
    AND('+', 1),
    OR('|', 0),
    XOR('^', 0),
    OPEN_PARENTHESIS('(', 99),
    CLOSE_PARENTHESIS(')', 99);

    private final char conChar;
    private final int  precedence;

    Conditions(char condChar, int precedence)
    {
        this.conChar = condChar;
        this.precedence = precedence;
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
        return precedence;
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
