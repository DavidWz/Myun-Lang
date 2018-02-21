package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Tests logical operators.
 */
public class CircuitsTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler/circuits");
        resPath = resources.getAbsolutePath() + '/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    @Test
    public void logicalAndWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "logicalAnd.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"1", "0", "0", "0"};
        assertThat("There should be 4 output lines.", 4, is(lines.length));
        assertArrayEquals("Output should be true, false, false, false.", expected, lines);
    }

    @Test
    public void logicalOrWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "logicalOr.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"1", "1", "1", "0"};
        assertThat("There should be 4 output lines.", 4, is(lines.length));
        assertArrayEquals("Output should be true, true, true, false.", expected, lines);
    }

    @Test
    public void logicalNotWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "logicalNot.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"0", "1"};
        assertThat("There should be 2 output lines.", 2, is(lines.length));
        assertArrayEquals("Output should be false, true.", expected, lines);
    }

    @Test
    public void logicalIsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "logicalIs.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"1", "0", "0", "1"};
        assertThat("There should be 4 output lines.", 4, is(lines.length));
        assertArrayEquals("Output should be true, false, false, true.", expected, lines);
    }
}
