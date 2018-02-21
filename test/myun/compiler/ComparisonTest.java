package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void lessThanEqualsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void greaterThanWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void greaterThanEqualsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void equalsWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }
}
