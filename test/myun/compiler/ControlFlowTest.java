package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests control flow structures in Myun.
 */
public class ControlFlowTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler/controlflow");
        resPath = resources.getAbsolutePath() + '/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    @Test
    public void testOnlyIf() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "onlyIf.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"11", "12", "13",
                "21", "23"};
        assertArrayEquals("Correct branch paths taken for simple if branches.", expected, lines);
    }

    @Test
    public void testIfElse() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "ifElse.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"31", "32", "34",
                "41", "43", "44"};
        assertArrayEquals("Correct branch paths taken for if-else branches.", expected, lines);
    }

    @Test
    public void testIfElseifElses() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "elseIf.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"51", "54", "58",
                "61", "63", "64"};
        assertArrayEquals("Correct branch paths taken for if-elseif-else branches.", expected, lines);
    }

    @Test
    public void testNestedBranches() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "nestedBranches.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"0", "2", "23", "25", "4"};
        assertArrayEquals("Correct nested branch paths taken.", expected, lines);
    }

    @Test
    public void testWhileLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "whileLoops.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"1", "3", "3"};
        assertArrayEquals("While body correct amount of times executed.", expected, lines);
    }

    @Test
    public void testNestedWhileLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "nestedWhile.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"55"};
        assertArrayEquals("While body correct amount of times executed.", expected, lines);
    }

    @Test
    public void testSimpleForLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "simpleForLoop.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"66", "7", "8"};
        assertArrayEquals("For body correct amount of times executed.", expected, lines);
    }

    @Test
    public void testForLoopSameItVar() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "forSameItVar.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"15", "7", "12"};
        assertArrayEquals("For loops with same it vars in different scopes should compile.", expected, lines);
    }

    @Test
    public void testNestedFor() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "nestedFor.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"218"};
        assertArrayEquals("Nested for loops should compile.", expected, lines);
    }

    @Test
    public void testSimpleBreaks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "simpleBreaks.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"1", "3", "2", "5", "6", "7", "8"};
        assertArrayEquals("Breaks should work.", expected, lines);
    }

    @Test
    public void testNestedBreaks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "nestedBreaks.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"7", "6", "5", "10", "5", "9",
            "1", "2", "1", "3", "1", "2", "4", "1", "2", "3", "5", "1", "2", "3", "4",
            "1", "2", "3"};
        assertArrayEquals("Breaks in nested loops should work.", expected, lines);
    }
}
