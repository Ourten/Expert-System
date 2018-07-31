package fr.expertsystem.parser;

import fr.expertsystem.data.Conditions;
import fr.expertsystem.data.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Parser
{
    static class ParserElement
    {
        static enum Type
        {
            XOR('^', true),
            OR('|', true),
            AND('+', true),
            NOT('!', false),
            OPEN_PARENTHESIS('(', false),
            CLOSE_PARENTHESIS(')', false),
            IMPLY,
            FACT,
            BREAK('#', false);

            private final char    token;
            private final boolean isOperation;

            Type(char token, boolean isOperation)
            {
                this.token = token;
                this.isOperation = isOperation;
            }

            Type()
            {
                this('\0', false);
            }

            public static Type fromToken(char token)
            {
                for (Type type : values())
                    if (type.token == token && type.token != '\0')
                        return type;
                return null;
            }

            @Override
            public String toString()
            {
                return String.format("%c", this.token);
            }

            public char getToken()
            {
                return token;
            }

            public boolean isOperation()
            {
                return isOperation;
            }
        }

        private final Type   type;
        private final String factID;

        public ParserElement(Type type, String factID)
        {
            this.type = type;
            this.factID = factID;
        }

        public ParserElement(Type type)
        {
            this(type, null);
        }

        public Type getType()
        {
            return type;
        }

        public String getFactID()
        {
            return factID;
        }
    }

    private static ParserElement parseToken(ParserElement prevElem, char token)
    {
        ParserElement.Type type = ParserElement.Type.fromToken(token);
        if (type == null)
        {
            if (Character.isAlphabetic(token))
                return new ParserElement(ParserElement.Type.FACT, String.valueOf(token));
            throw new RuntimeException(String.format("Unknown token \"%c\"!", token));
        }
        return new ParserElement(type);
    }

    private static void parseStringToken(List<ParserElement> elements, String token)
    {
        for (int i = 0; i < token.length(); i++)
        {
            char c = token.charAt(i);
            ParserElement prevElem = elements.size() > 0 ? elements.get(elements.size() - 1) : null;

            ParserElement newElem;

            //  2 char token
            if (token.substring(i).startsWith("=>"))
            {
                newElem = new ParserElement(ParserElement.Type.IMPLY);
                // skip already parsed char
                i++;
            }
            else
            {
                newElem = parseToken(prevElem, c);
                if (newElem.getType() == ParserElement.Type.BREAK)
                    break;
            }
            elements.add(newElem);
        }
    }

    private static Rule parseRule(String rawRule)
    {
        List<ParserElement> elements = new ArrayList<>();
        // Clean up the rule
        rawRule = rawRule.replaceAll("\\s+", " ").trim();
        StringTokenizer tokenizer = new StringTokenizer(rawRule, " ");

        // Collect every elements
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            parseStringToken(elements, token);
        }

        /*ParserElement prevElement = null;

        // must be set when encounter +|^ or ! (unset by imply or when a fact is encounter)
        boolean requireFact = false;

        // set when in operation mode
        boolean operationProvided = false;

        // set when encounter a fact and requireFact == false
        boolean requireOperation = false;

        Stack<Boolean> parenthesisStack = new Stack<>();

        int parenthesisOpenCount = 0;

        for (int i = 0; i < elements.size(); i++)
        {
            ParserElement element = elements.get(i);
            ParserElement.Type type = element.getType();
            if (type == ParserElement.Type.OPEN_PARENTHESIS)
            {
                parenthesisStack.push(requireFact);
                parenthesisStack.push(operationProvided);
                parenthesisStack.push(requireOperation);
                requireFact = false;
                operationProvided = false;
                requireOperation = false;
                parenthesisOpenCount++;
            }
            else if (type == ParserElement.Type.CLOSE_PARENTHESIS)
            {
                parenthesisOpenCount--;
                if (parenthesisOpenCount < 0 || prevElement == null || prevElement.getType() == ParserElement.Type
                .OPEN_PARENTHESIS)
                    throw new RuntimeException("Invalid close parenthesis");
                if (!parenthesisStack.empty())
                {
                    requireFact = parenthesisStack.pop();
                    operationProvided = parenthesisStack.pop();
                    requireOperation = parenthesisStack.pop();

                    requireFact = false;

                }
            }
            else if (type == ParserElement.Type.NOT)
            {
                requireFact = true;
            }
            else if (type == ParserElement.Type.IMPLY)
            {
                if (requireFact && !operationProvided)
                    throw new RuntimeException("Invalid usage of imply!");
                requireFact = false;
                requireOperation = false;
            }
            else if (type == ParserElement.Type.FACT)
            {
                if (requireOperation)
                    throw new RuntimeException("Fact not intended here!");
                if (requireFact)
                {
                    if (!operationProvided)
                        requireFact = false;
                    else
                        operationProvided = false;
                }
                else
                    requireOperation = true;
            }
            else if (type.isOperation())
            {
                if (requireOperation)
                {
                    operationProvided = true;
                    requireFact = true;
                    requireOperation = false;
                }
                else
                    throw new RuntimeException("Missing left operand!");
            }
            prevElement = element;
        }*/

        Rule.Builder.IPartBuilder partBuilder = Rule.build();
        for (ParserElement element : elements)
        {
            switch (element.getType())
            {
                case OPEN_PARENTHESIS:
                    partBuilder.cond(Conditions.OPEN_PARENTHESIS);
                    break;
                case CLOSE_PARENTHESIS:
                    partBuilder.cond(Conditions.CLOSE_PARENTHESIS);
                    break;
                case NOT:
                    partBuilder.cond(Conditions.NOT);
                    break;
                case AND:
                    partBuilder.cond(Conditions.AND);
                    break;
                case OR:
                    partBuilder.cond(Conditions.OR);
                    break;
                case XOR:
                    partBuilder.cond(Conditions.XOR);
                    break;
                case IMPLY:
                    partBuilder = partBuilder.imply();
                    break;
                case FACT:
                    partBuilder.fact(element.getFactID());
                    break;
            }
        }
        return partBuilder.create();
    }

    public static List<Rule> parseRules(List<String> rawRules)
    {
        return rawRules.stream().map(Parser::parseRule).collect(Collectors.toList());
    }
}
