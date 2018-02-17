package myun.AST;

import myun.MyunException;

/**
 * Thrown when the parser finds an error.
 */
public class ParserException extends MyunException {
    private final String cause;

    public ParserException(String cause, SourcePosition sourcePosition) {
        super(sourcePosition);
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "Parse Error: " + cause + " on " + sourcePosition;
    }
}
