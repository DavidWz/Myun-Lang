package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void testWhileLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }

    @Test
    public void testForLoops() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "???.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        // TODO
        fail();
    }
}
