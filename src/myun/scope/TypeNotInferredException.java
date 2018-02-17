package myun.scope;

import myun.AST.SourcePosition;
import myun.MyunException;

/**
 * Thrown when a type was expected, but not inferred yet.
 */
public class TypeNotInferredException extends MyunException {
    public TypeNotInferredException(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    @Override
    public String getMessage() {
        return "Error: Type expected but not inferred yet for expression used on " + sourcePosition;
    }
}
