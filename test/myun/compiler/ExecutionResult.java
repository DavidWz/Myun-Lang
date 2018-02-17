package myun.compiler;

/**
 * Stores the result of a code execution (exit status + output)
 */
class ExecutionResult {
    private final int exitStatus;
    private final String errors;

    ExecutionResult(int exitStatus, String errors) {
        this.exitStatus = exitStatus;
        this.errors = errors;
    }

    int getExitStatus() {
        return exitStatus;
    }

    String getErrors() {
        return errors;
    }
}
