package myun.compiler;

/**
 * Thrown when the compiler meets a node that he cannot compile because preprocessing (scopes, types, etc)
 * was not sufficient for the task.
 */
class InsufficientPreprocessingException extends RuntimeException {
    InsufficientPreprocessingException(String msg) {
        super(msg);
    }
}
