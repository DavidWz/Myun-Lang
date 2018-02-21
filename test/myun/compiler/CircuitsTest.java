package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

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
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void logicalOrWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void logicalNotWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void logicalIsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }
}
