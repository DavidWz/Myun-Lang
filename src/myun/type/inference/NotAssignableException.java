package myun.type.inference;

import myun.AST.SourcePosition;
import myun.MyunException;

/**
 * Thrown when an assignment is made even though the assigned variable is not assignable.
 */
class NotAssignableException extends MyunException {
    NotAssignableException(SourcePosition assignmentPos) {
        super(assignmentPos);
    }

    @Override
    public String getMessage() {
        return "Type Error: Assignment made to a not assignable variable\n" +
                " on " + sourcePosition;
    }
}
