package myun.compiler;

import myun.scope.IllegalRedefineException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests whether script and functions can have the same names, and if overloading gets parsed.
 */
public class FunctionOverloadTest {
    @Test
    public void funcOverloadSucceeds() throws IOException, InterruptedException {
        String code = "fib(n::Int)::Int\n" +
                "    return 4"+
                "end\n" +
                '\n' +
                "fib(n::Float)::Float\n" +
                "    return 4.0"+
                "end\n script bar end";
        ExecutionResult result = CodeRunner.runMyunCode(code);
        assertThat("Exit status should be 0.", 0, is(result.getExitStatus()));
        assertThat("There should be no error.", "", is(result.getErrors()));
    }

    @Test(expected = IllegalRedefineException.class)
    public void funcRedefinitionFails() throws IOException, InterruptedException {
        String code = "fib(n::Int)::Int\n" +
                "    return 4"+
                "end\n" +
                '\n' +
                "fib(n::Int)::Int\n" +
                "    return 5"+
                "end\n script bar end";
        CodeRunner.compileStringToString(code);
    }

    @Test(expected = IllegalRedefineException.class)
    public void onlyReturnTypeDifferentFails() throws IOException, InterruptedException {
        String code = "fib(n::Int)::Int\n" +
                "    return 4"+
                "end\n" +
                '\n' +
                "fib(n::Int)::Float\n" +
                "    return 5.0"+
                "end\n script bar end";
        CodeRunner.compileStringToString(code);
    }

    @Test
    public void funcAndScriptSameNameDifferentParamsSucceeds() throws IOException, InterruptedException {
        String code = "foo(x::Int, y::Int)::Int\n" +
                "    return y+2\n" +
                "end script foo end";
        ExecutionResult result = CodeRunner.runMyunCode(code);
        assertThat("Exit status should be 0.", 0, is(result.getExitStatus()));
        assertThat("There should be no error.", "", is(result.getErrors()));
    }

    @Test
    public void funcAndScriptSameNameNoParamsSucceeds() throws IOException, InterruptedException {
        String code = "bar()::Int\n" +
                "    return 42\n" +
                "end script bar end";
        ExecutionResult result = CodeRunner.runMyunCode(code);
        assertThat("Exit status should be 0.", 0, is(result.getExitStatus()));
        assertThat("There should be no error.", "", is(result.getErrors()));
    }
}
