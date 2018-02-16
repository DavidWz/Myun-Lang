package myun.compiler;

import myun.MyunException;

/**
 * Thrown when the LLVM compilation process was unsuccessful.
 */
class UnsuccessfulCompilationException extends MyunException {
    UnsuccessfulCompilationException(String msg) {
        super(msg);
    }
}
