package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests whether function definitions and calls work properly.
 */
public class FunctionTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler/functions");
        resPath = resources.getAbsolutePath() + '/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    @Test
    public void simpleFunctionCallsSucceed() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "funcCall.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int resInt = Integer.parseInt(lines[0]);
        double resFloat = Double.parseDouble(lines[1]);

        assertThat("There should be two output lines.", 2, is(lines.length));
        assertEquals("First output should be int 5876", 5876, resInt);
        assertEquals("Second output should be float -9003.0", -9003.0, resFloat, 1e-8);
    }

    @Test
    public void nestedFunctionCallsSucceed() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "nestedFuncCall.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        double res = Double.parseDouble(lines[0]);
        assertThat("There should be one output line.", 1, is(lines.length));
        assertEquals("Output should be float 121.0", 121.0, res, 1e-8);
    }

    @Test
    public void outOfOrderFunctionDefsCompile() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "outOfOrderFuncDefs.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int res = Integer.parseInt(lines[0]);
        assertThat("There should be one output line.", 1, is(lines.length));
        assertEquals("Output should be 3^3 = 27", 27, res);
    }

    @Test
    public void cyclicFunctionDefsCompile() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "cyclicFuncDefs.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        int res = Integer.parseInt(lines[0]);
        assertThat("There should be one output line.", 1, is(lines.length));
        assertEquals("Output should be 1", 1, res);
    }

    @Test
    public void recursionWorks() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath + "recursion.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        assertThat("There should be ten output lines.", 10, is(lines.length));
        int[] fibs = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55};
        for (int i = 0; i < 10; i++) {
            int res = Integer.parseInt(lines[i]);
            assertEquals((i+1) + "th fibonacci number should be " + fibs[i], fibs[i], res);
        }
    }

    @Test
    public void funcOverloadSucceeds() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath+"simpleOverloading.myun");
        String[] lines = CodeRunner.executeAndGetOutput(outputFile);

        double resFloat = Double.parseDouble(lines[0]);
        int resInt = Integer.parseInt(lines[1]);

        assertThat("There should be two output lines.", 2, is(lines.length));
        assertEquals("First output should be float 5050.0", 5050.0, resFloat, 1e-8);
        assertEquals("Second output should be int 5050", 5050, resInt);
    }
}
