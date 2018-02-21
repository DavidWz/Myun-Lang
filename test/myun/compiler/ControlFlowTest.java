package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

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
    public void testBranches() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "branches.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"11", "12", "13",
                "21", "23",
                "31", "32", "33",
                "41", "43", "44",
                "51", "54", "58",
                "61", "63"};
        assertArrayEquals("Correct branch paths taken.", expected, lines);
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

        String[] expected = {"1", "3", "3", "55", "9", "8", "7", "6"};
        assertArrayEquals("While body correct amount of times executed.", expected, lines);
    }

    @Test
    public void testForLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "forLoops.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        String[] expected = {"66", "7", "8", "7", "12", "218"};
        assertArrayEquals("For body correct amount of times executed.", expected, lines);
    }
}
