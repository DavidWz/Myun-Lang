package myun;

import myun.AST.SourcePosition;

/**
 * Abstract class for Myun exceptions.
 */
public abstract class MyunException extends RuntimeException {
    protected final SourcePosition sourcePosition;

    protected MyunException(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
    }
}
