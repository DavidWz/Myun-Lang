package myun.AST.constraints;

import myun.AST.ParserException;
import myun.AST.SourcePosition;

/**
 * Thrown when a break is found outside of a loop.
 */
class BreakOutsideLoopException extends ParserException {
    BreakOutsideLoopException(SourcePosition sourcePosition) {
        super("Loop break found outside of a loop.", sourcePosition);
    }
}
