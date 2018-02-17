package myun.compiler;

/**
 * Thrown when the LLVM compilation process was unsuccessful.
 */
class UnsuccessfulCompilationException extends RuntimeException {
    UnsuccessfulCompilationException(String msg) {
        super(msg);
    }
}
