package myun.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
}
