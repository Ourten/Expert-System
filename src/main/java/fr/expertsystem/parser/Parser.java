package fr.expertsystem.parser;

import fr.expertsystem.data.Conditions;
import fr.expertsystem.data.Rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: custom exception
public class Parser
{
    private static Rule.Builder.LeftPartBuilder parseAndAddFactLeft(Rule.Builder.LeftPartBuilder leftPartBuilder,
                                                                    String rawFact)
    {
        boolean isNeg = rawFact.startsWith("!");
        int factIndex = isNeg ? 1 : 0;
        if (rawFact.length() != factIndex + 1)
            throw new RuntimeException("Invalid fact " + rawFact);
        char factID = rawFact.charAt(factIndex);
        if (!Character.isAlphabetic(factID))
            throw new RuntimeException("Invalid fact " + rawFact);
        return leftPartBuilder.fact(String.valueOf(factID), isNeg);
    }

    private static Rule.Builder.RightPartBuilder parseAndAddFactRight(Rule.Builder.RightPartBuilder rightPartBuilder,
                                                                      String rawFact)
    {
        boolean isNeg = rawFact.startsWith("!");
        int factIndex = isNeg ? 1 : 0;
        if (rawFact.length() != factIndex + 1)
            throw new RuntimeException("Invalid fact " + rawFact);
        char factID = rawFact.charAt(factIndex);
        if (!Character.isAlphabetic(factID))
            throw new RuntimeException("Invalid fact " + rawFact);
        return rightPartBuilder.fact(String.valueOf(factID), isNeg);
    }

    private static Rule parseRule(String rawRule)
    {
        String[] parts = rawRule.split("=>");
        if (parts.length != 2)
            throw new RuntimeException("Invalid rule " + rawRule);

        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        if (parts[0].isEmpty() || parts[1].isEmpty())
            throw new RuntimeException("Invalid rule " + rawRule);

        String[] rawLeftPart = parts[0].split("(?=[+|^])");
        Rule.Builder.LeftPartBuilder leftBuilder = Rule.build();

        // if the split fail, it must be a direct imply
        if (rawLeftPart.length != 2)
            parseAndAddFactLeft(leftBuilder, parts[0]);
        else
        {
            rawLeftPart[0] = rawLeftPart[0].trim();
            rawLeftPart[1] = rawLeftPart[1].replaceAll("\\s+", "");

            // <condChar><rightFact>
            if (rawLeftPart[1].length() < 2 || rawLeftPart[1].length() > 3)
                throw new RuntimeException("Invalid fact" + rawLeftPart[1]);

            char condChar = rawLeftPart[1].charAt(0);

            Optional<Conditions> optionalCondition = Conditions.fromChar(condChar);
            if (!optionalCondition.isPresent())
                throw new RuntimeException(String.format("Invalid condition \"%c\" for rule \"%s\"", condChar,
                        rawRule));

            String leftFact = rawLeftPart[0];
            String rightFact = rawLeftPart[1].substring(1);

            parseAndAddFactLeft(leftBuilder, leftFact);
            leftBuilder.cond(optionalCondition.get());
            parseAndAddFactLeft(leftBuilder, rightFact);
        }

        return parseAndAddFactRight(leftBuilder.imply(), parts[1]).create();
    }

    public static List<Rule> parseRules(List<String> rawRules)
    {
        return rawRules.stream().map(Parser::parseRule).collect(Collectors.toList());
    }
}
