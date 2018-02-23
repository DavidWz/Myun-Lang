package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * Tests comparison operators.
 */
public class ComparisonTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler/comparisons");
        resPath = resources.getAbsolutePath() + '/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    @Test
    public void lessThanWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "lessThan.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"11", "22", "32",
                "41", "52", "62"};
        assertArrayEquals("Less than comparisons should be correct.", expected, lines);
    }

    @Test
    public void lessThanEqualsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "lessEqual.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"11", "22", "31",
                "41", "52", "61"};
        assertArrayEquals("Less than or equals comparisons should be correct.", expected, lines);
    }

    @Test
    public void greaterThanWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "greaterThan.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"12", "21", "32",
                "42", "51", "62"};
        assertArrayEquals("Greater than or equals comparisons should be correct.", expected, lines);
    }

    @Test
    public void greaterThanEqualsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "greaterEqual.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"12", "21", "31",
                "42", "51", "61"};
        assertArrayEquals("Greater than or equals comparisons should be correct.", expected, lines);
    }

    @Test
    public void equalsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "equals.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"11", "22", "32", "41"};
        assertArrayEquals("Equals comparisons should be correct.", expected, lines);
    }
}
