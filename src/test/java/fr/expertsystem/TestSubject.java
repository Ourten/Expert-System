package fr.expertsystem;

import fr.expertsystem.data.Fact;
import fr.expertsystem.data.FactState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestSubject
{
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    String constructTestPath(String name)
    {
        return String.format("src/test/resources/fr/expertsystem/tests/subject/%s.txt"
                , name);
    }

    void setupOutputStream()
    {
        outputStream.reset();
        System.setOut(new PrintStream(outputStream));
    }

    Map<Fact, FactState> parseSystemOut()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();

        String output = outputStream.toString();
        System.err.print(output);

        Arrays.stream(output.split("\n")).forEach((line) -> parseSolveLine(factsMap, line));

        // make sure to clear the stream
        outputStream.reset();
        return factsMap;
    }

    void parseSolveLine(Map<Fact, FactState> factsMap, String line)
    {
        if (line.startsWith("Solving: "))
        {
            line = line.substring(9);
            String[] parts = line.split(" = ");
            if (parts.length == 2)
            {
                factsMap.put(new Fact(parts[0]), parts[1].trim().equals("TRUE") ? FactState.TRUE : FactState.FALSE);
            }
        }
    }

    void executeProgram(Map<Fact, FactState> intendedResult, String... args)
    {
        setupOutputStream();
        Main.main(args);
        Map<Fact, FactState> factStateMap = parseSystemOut();

        intendedResult.forEach((key, value) -> {
            FactState resultState = factStateMap.get(key);
            if (!value.equals(resultState))
                System.err.println("Failing fact: " + key);
            assertThat(resultState).isEqualTo(value);
        });
        outputStream.reset();
    }

    @Test
    void andTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        factsMap.put(new Fact("F"), FactState.TRUE);
        factsMap.put(new Fact("K"), FactState.TRUE);
        factsMap.put(new Fact("P"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("and/and_conditions_conclusions_1"));
    }

    @Test
    void andTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        factsMap.put(new Fact("F"), FactState.TRUE);
        factsMap.put(new Fact("K"), FactState.FALSE);
        factsMap.put(new Fact("P"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("and/and_conditions_conclusions_2"));
    }

    @Test
    void orTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("or/basic_or_conditions_1"));
    }

    @Test
    void orTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("or/basic_or_conditions_2"));
    }

    @Test
    void orTest3()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("or/basic_or_conditions_3"));
    }

    @Test
    void orTest4()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("or/basic_or_conditions_4"));
    }

    @Test
    void xorTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("xor/basic_xor_conditions_1"));
    }

    @Test
    void xorTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("xor/basic_xor_conditions_2"));
    }

    @Test
    void xorTest3()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("xor/basic_xor_conditions_3"));
    }

    @Test
    void xorTest4()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("xor/basic_xor_conditions_4"));
    }

    @Test
    void notTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("not/basic_not_conditions_1"));
    }

    @Test
    void notTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("not/basic_not_conditions_2"));
    }

    @Test
    void notTest3()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("not/basic_not_conditions_3"));
    }

    @Test
    void notTest4()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("not/basic_not_conditions_4"));
    }

    @Test
    void sameTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("other/same_conclusion_1"));
    }

    @Test
    void sameTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("other/same_conclusion_2"));
    }

    @Test
    void sameTest3()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("other/same_conclusion_3"));
    }

    @Test
    void sameTest4()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("A"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("other/same_conclusion_4"));
    }

    @Test
    void parenthesisTest1()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_1"));
    }

    @Test
    void parenthesisTest2()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_2"));
    }

    @Test
    void parenthesisTest3()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_3"));
    }

    @Test
    void parenthesisTest4()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_4"));
    }

    @Test
    void parenthesisTest5()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_5"));
    }

    @Test
    void parenthesisTest6()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_6"));
    }

    @Test
    void parenthesisTest7()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_7"));
    }

    @Test
    void parenthesisTest8()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_8"));
    }

    @Test
    void parenthesisTest9()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.FALSE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_9"));
    }

    @Test
    void parenthesisTest10()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_10"));
    }

    @Test
    void parenthesisTest11()
    {
        Map<Fact, FactState> factsMap = new HashMap<>();
        factsMap.put(new Fact("E"), FactState.TRUE);
        executeProgram(factsMap, constructTestPath("parenthesis/parenthesis_conditions_11"));
    }
}
