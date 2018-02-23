package myun.AST.constraints;

import myun.AST.ParserException;
import myun.AST.SourcePosition;

/**
 * Thrown when a break is found outside of a loop.
 */
public class BreakOutsideLoopException extends ParserException {
    public BreakOutsideLoopException(SourcePosition sourcePosition) {
        super("Loop break found outside of a loop.", sourcePosition);
    }
}
