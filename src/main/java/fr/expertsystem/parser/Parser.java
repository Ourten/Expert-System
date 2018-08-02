package fr.expertsystem.parser;

import fr.expertsystem.data.Conditions;
import fr.expertsystem.data.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Parser {
    private static final char COMMENT_CHAR = '#';
    private static final char INIT_CHAR    = '?';
    private static final char QUERY_CHAR   = '=';

    public static class Result {

        private String error;
        private List<Rule> rules;

        private Result(List<Rule> rules) {
            this.rules = rules;
        }

        private Result(String error) {
            this.error = error;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public String getError() {
            return error;
        }

        public boolean isOk() {
            return getError() == null;
        }

        public static Result Ok(List<Rule> rules) {
            return new Result(rules);
        }

        public static Result Error(String error) {
            return new Result(error);
        }
    }

    static class Element {
        enum Type {
            XOR('^', true),
            OR('|', true),
            AND('+', true),
            NOT('!', false),
            OPEN_PARENTHESIS('(', false),
            CLOSE_PARENTHESIS(')', false),
            IMPLY,
            FACT,
            BREAK('#', false);

            private final char token;
            private final boolean isOperation;

            Type(char token, boolean isOperation) {
                this.token = token;
                this.isOperation = isOperation;
            }

            Type() {
                this('\0', false);
            }

            public static Type fromToken(char token) {
                for (Type type : values())
                    if (type.token == token && type.token != '\0')
                        return type;
                return null;
            }

            @Override
            public String toString() {
                return String.format("%c", this.token);
            }

            public char getToken() {
                return token;
            }

            public boolean isOperation() {
                return isOperation;
            }
        }

        private final Type type;
        private final String factID;

        public Element(Type type, String factID) {
            this.type = type;
            this.factID = factID;
        }

        public Element(Type type) {
            this(type, null);
        }

        public Type getType() {
            return type;
        }

        public String getFactID() {
            return factID;
        }
    }

    private static Element parseToken(char token) {
        Element.Type type = Element.Type.fromToken(token);
        if (type == null) {
            if (Character.isAlphabetic(token))
                return new Element(Element.Type.FACT, String.valueOf(token));
            throw new RuntimeException(String.format("Unknown token \"%c\"!", token));
        }
        return new Element(type);
    }

    private static void parseStringToken(List<Element> elements, String token) {
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);

            Element newElem;

            //  2 char token
            if (token.substring(i).startsWith("=>")) {
                newElem = new Element(Element.Type.IMPLY);
                // skip already parsed char
                i++;
            } else {
                newElem = parseToken(c);
                if (newElem.getType() == Element.Type.BREAK)
                    break;
            }
            elements.add(newElem);
        }
    }

    private static Rule parseRule(String rawRule) {
        List<Element> elements = new ArrayList<>();
        // Clean up the rule
        rawRule = rawRule.replaceAll("\\s+", " ").trim();
        StringTokenizer tokenizer = new StringTokenizer(rawRule, " ");

        // Collect every elements
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            parseStringToken(elements, token);
        }

        /*Element prevElement = null;

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
            Element element = elements.get(i);
            Element.Type type = element.getType();
            if (type == Element.Type.OPEN_PARENTHESIS)
            {
                parenthesisStack.push(requireFact);
                parenthesisStack.push(operationProvided);
                parenthesisStack.push(requireOperation);
                requireFact = false;
                operationProvided = false;
                requireOperation = false;
                parenthesisOpenCount++;
            }
            else if (type == Element.Type.CLOSE_PARENTHESIS)
            {
                parenthesisOpenCount--;
                if (parenthesisOpenCount < 0 || prevElement == null || prevElement.getType() == Element.Type
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
            else if (type == Element.Type.NOT)
            {
                requireFact = true;
            }
            else if (type == Element.Type.IMPLY)
            {
                if (requireFact && !operationProvided)
                    throw new RuntimeException("Invalid usage of imply!");
                requireFact = false;
                requireOperation = false;
            }
            else if (type == Element.Type.FACT)
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
        for (Element element : elements) {
            switch (element.getType()) {
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

    private static List<Rule> parseRules(List<String> rawRules) {
        return rawRules.stream().map(Parser::parseRule).collect(Collectors.toList());
    }

    public static Result parseLines(List<String> lines)
    {
        lines = lines.stream().filter((line) ->
                !line.trim().isEmpty() && line.charAt(0) != COMMENT_CHAR).collect(Collectors.toList());
        List<String> rawRules = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c != INIT_CHAR && c != QUERY_CHAR;
        }).collect(Collectors.toList());

        List<String> initAndQuery = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c == INIT_CHAR || c == QUERY_CHAR;
        }).collect(Collectors.toList());

        if (initAndQuery.size() != 2)
            return Result.Error("Initial facts or query is missing");

        try {
            List<Rule> rules = Parser.parseRules(rawRules);
            return Result.Ok(rules);
        } catch (RuntimeException ex)
        {
            // TODO: use custom exception
            return Result.Error(ex.getMessage());
        }

    }
}
