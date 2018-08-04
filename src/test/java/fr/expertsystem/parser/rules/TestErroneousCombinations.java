package fr.expertsystem.parser.rules;

import fr.expertsystem.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestErroneousCombinations {

    private void testRule(String rule) {
        testRule(rule, false);
    }

    private void testRule(String rule, boolean verbose) {
        Parser.Result res = Parser.Result.Ok(null, null, null);
        try {
            Parser.parseRule(rule.trim());
        } catch (RuntimeException ex) {
            // TODO: use custom exception
            res = Parser.Result.Error(ex.getMessage());
        }

        if (res.isOk())
            throw new RuntimeException(String.format("Parser doesn't error'd out on \"%s\"", rule));
        else if (verbose)
            System.err.printf("\"%s\": %s\n", rule, res.getError());
    }

    @Test
    void andConditions() throws IOException {
        testRule("A + => B");
        testRule("A + ! => B");
        testRule(" + B => A");
        testRule("! + B => B");
        testRule("B + | => A");
        testRule("^ + B => A");
        testRule("=> + B => A");
        testRule("A + + => A");
        testRule("A + B => A +");
        testRule("A + B => A + +");
        testRule("A + B => A + C + !");
        testRule("A + B => A + C + #");
    }

    @Test
    void orConditions() throws IOException {
        testRule("A | => B");
        testRule("A | ! => B");
        testRule(" | B => A");
        testRule("! | B => B");
        testRule("B | + => A");
        testRule("^ | B => A");
        testRule("=> | B => A");
        testRule("A | | => A");
        testRule("A | B => A |");
        testRule("A | B => A | |");
        testRule("A | B => A | C | !");
        testRule("A | B => A | C | #");
        testRule("A + B => C | D");
    }

    @Test
    void xOrConditions() throws IOException {
        testRule("A ^ => B");
        testRule("A ^ ! => B");
        testRule(" ^ B => A");
        testRule("! ^ B => B");
        testRule("B ^ + => A");
        testRule("^ ^ B => A");
        testRule("=> ^ B => A");
        testRule("A ^ ^ => A");
        testRule("A ^ B => A ^");
        testRule("A ^ B => A ^ ^");
        testRule("A ^ B => A ^ C ^ !");
        testRule("A ^ B => A ^ C ^ #");
        testRule("A + B => C ^ D");

    }

    @Test
    void notModifier() throws IOException {
        testRule("!!A => B");
        testRule("A => !!B");
        testRule("A ! + B => C");
        testRule("A + B ! => C");
        testRule("(A + B !) => C");
    }

    @Test
    void parenthesisModifier() throws IOException {
        testRule("(=>)");
        testRule("() => ()");
        testRule("() => V");
        testRule("V => ()");
        testRule("(V + D => E");
        testRule("(V + D => E)");
        testRule("V + D => E)");
        testRule("((V ^ D) => E)");
    }
}
