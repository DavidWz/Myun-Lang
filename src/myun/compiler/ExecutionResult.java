package myun.compiler;

/**
 * Stores the result of a code execution (exit status + output)
 */
class ExecutionResult {
    private final int exitStatus;
    private final String errors;
    private final String output;

    ExecutionResult(int exitStatus, String errors, String output) {
        this.exitStatus = exitStatus;
        this.errors = errors;
        this.output = output;

    }

    int getExitStatus() {
        return exitStatus;
    }

    String getErrors() {
        return errors;
    }

    String getOutput() {
        return output;
    }
}
