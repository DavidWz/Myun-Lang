package myun.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests whether the right overloaded function gets called.
 */
public class FunctionOverloadTest {
    private MyunCompiler compiler;
    private String resPath;

    @Before
    public void setUp() {
        File resources = new File("testData/myun/compiler");
        resPath = resources.getAbsolutePath()+'/';
        compiler = MyunCompiler.getDefaultMyunCompiler();
    }

    @Test
    public void funcOverloadSucceeds() throws IOException, InterruptedException {
        String outputFile = compiler.compileFromFile(resPath+"simpleOverloading.myun");
        ExecutionResult result = MyunCompiler.runMyunFile(outputFile);
        assertThat("Exit status should be 0.", 0, is(result.getExitStatus()));
        assertThat("There should be no error.", "", is(result.getErrors()));

        String output = result.getOutput();
        String[] lines = output.split("\n");
        assertThat("There should be two output lines.", 2, is(lines.length));
        assertTrue("First output should be float 5050", lines[0].startsWith("5050.0"));
        assertEquals("Second output should be int 5050", "5050", lines[1]);
    }
}
