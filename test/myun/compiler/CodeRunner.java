package myun.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Executes Myun binaries.
 */
final class CodeRunner {
    private CodeRunner() {
    }

    /**
     * Runs the given Myun file.
     *
     * @param myunFile the llvm code
     * @return the result of the execution
     * @throws IOException thrown when a file could not be written
     * @throws InterruptedException thrown when a process is interrupted
     */
    static ExecutionResult runMyunFile(String myunFile) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process runProcess = rt.exec(myunFile);
        int exitStatus = runProcess.waitFor();
        String errors = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()))
                .lines().collect(Collectors.joining("\n"));
        String output = new BufferedReader(new InputStreamReader(runProcess.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        return new ExecutionResult(exitStatus, errors, output);
    }

    /**
     * Executes the given myun file, makes sure there are no errors, and returns all output lines.
     * @param fileName the myun file
     * @return an array of output lines
     */
    static String[] executeAndGetOutput(String fileName) throws IOException, InterruptedException {
        ExecutionResult result = CodeRunner.runMyunFile(fileName);
        assertThat("Exit status should be 0.", 0, is(result.getExitStatus()));
        assertThat("There should be no error.", "", is(result.getErrors()));
        String output = result.getOutput();
        return output.split("\n");
    }
}
