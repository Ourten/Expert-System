package fr.expertsystem.parser;

import fr.expertsystem.data.Conditions;
import fr.expertsystem.data.Fact;
import fr.expertsystem.data.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Parser {
    private static final char COMMENT_CHAR = '#';
    private static final char QUERY_CHAR = '?';
    private static final char INIT_CHAR = '=';

    public static class Result {

        private String error;
        private List<Rule> rules;
        private List<Fact> initialFacts;
        private List<Fact> queryFacts;

        private Result(List<Rule> rules, List<Fact> initialFacts, List<Fact> queryFacts) {
            this.rules = rules;
            this.initialFacts = initialFacts;
            this.queryFacts = queryFacts;
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

        public static Result Ok(List<Rule> rules, List<Fact> initialFacts, List<Fact> queryFacts) {
            return new Result(rules, initialFacts, queryFacts);
        }

        public static Result Error(String error) {
            return new Result(error);
        }

        public List<Fact> getInitialFacts() {
            return initialFacts;
        }

        public List<Fact> getQueryFacts() {
            return queryFacts;
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
            if (token >= 'A' && token <= 'Z')
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

        int openParenthesis = 0;
        boolean afterImply = false;
        Element prevElem = null;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Element nextElement = (i + 1) < elements.size() ? elements.get(i + 1) : null;

            switch (element.getType()) {
                case IMPLY:
                    if (afterImply || openParenthesis != 0)
                        throw new RuntimeException("Invalid usage of imply found");
                    afterImply = true;
                    if (nextElement == null || prevElem == null)
                        throw new RuntimeException("Cannot imply nothing!");
                    break;
                case OPEN_PARENTHESIS:
                    openParenthesis++;
                    break;
                case CLOSE_PARENTHESIS:
                    openParenthesis--;
                    break;
                case OR:
                case XOR:
                case AND:
                    if (prevElem != null && prevElem.getType().isOperation())
                        throw new RuntimeException("Fact expected!");
                    if (nextElement == null || (nextElement.getType() != Element.Type.FACT && nextElement.getType() != Element.Type.OPEN_PARENTHESIS && nextElement.getType() != Element.Type.NOT))
                        throw new RuntimeException(String.format("Invalid %s usage", element.getType()));
                    break;
                case NOT:
                    if (nextElement == null || (nextElement.getType() != Element.Type.FACT && nextElement.getType() != Element.Type.OPEN_PARENTHESIS))
                        throw new RuntimeException("Invalid NOT usage");
                    if (prevElem != null && prevElem.getType() == Element.Type.NOT)
                        throw new RuntimeException("Cannot use a NOT after a NOT");
                    break;
                case FACT:
                    if (!afterImply) {
                        if (nextElement == null)
                            throw new RuntimeException("Missing imply and right part");
                        else if (!nextElement.getType().isOperation() && nextElement.getType() != Element.Type.IMPLY && nextElement.getType() != Element.Type.CLOSE_PARENTHESIS) {
                            throw new RuntimeException("Invalid token after FACT");
                        }
                    } else {
                        if (nextElement != null && !nextElement.getType().isOperation() && nextElement.getType() != Element.Type.CLOSE_PARENTHESIS) {
                            throw new RuntimeException("Invalid token after FACT");
                        }
                    }
                    break;
                default:
                    break;
            }
            prevElem = element;
        }

        if (openParenthesis != 0)
            throw new RuntimeException("Invalid parenthesis block found");


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

    public static Result parseLines(List<String> lines) {
        lines = lines.stream().filter((line) ->
                !line.trim().isEmpty() && line.charAt(0) != COMMENT_CHAR).collect(Collectors.toList());

        List<String> rawRules = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c != INIT_CHAR && c != QUERY_CHAR;
        }).collect(Collectors.toList());

        List<String> initFactsList = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c == INIT_CHAR;
        }).map(String::trim).collect(Collectors.toList());

        if (initFactsList.isEmpty())
            return Result.Error("Initial facts are missing");
        else if (initFactsList.size() != 1)
            return Result.Error("Cannot have more than one initial facts declaration");

        List<String> queryFactsList = lines.stream().filter((line) ->
        {
            char c = line.trim().charAt(0);
            return c == QUERY_CHAR;
        }).map(String::trim).collect(Collectors.toList());

        if (queryFactsList.isEmpty())
            return Result.Error("Query facts are missing");
        else if (queryFactsList.size() != 1)
            return Result.Error("Cannot have more than one query facts declaration");

        try {
            List<Rule> rules = Parser.parseRules(rawRules);
            List<Fact> initialFacts = parseFacts(initFactsList.get(0));
            List<Fact> queryFacts = parseFacts(queryFactsList.get(0));
            return Result.Ok(rules, initialFacts, queryFacts);
        } catch (RuntimeException ex) {
            // TODO: use custom exception
            return Result.Error(ex.getMessage());
        }

    }

    private static List<Fact> parseFacts(String rawFacts) {
        // ignore initial char
        rawFacts = rawFacts.substring(1);
        List<Fact> facts = new ArrayList<>();
        for (int i = 0; i < rawFacts.length(); i++) {
            char c = rawFacts.charAt(i);
            if (c >= 'A' && c <= 'Z')
                facts.add(new Fact(String.valueOf(c)));
            else
                throw new RuntimeException(String.format("Invalid initial fact \"%c\"", c));
        }
        return facts;
    }
}
