package myun.type;

import myun.AST.ASTAssignment;
import myun.MyunException;

/**
 * Thrown when an assignment is made even though the assigned variable is not assignable.
 */
class NotAssignableException extends MyunException {
    private ASTAssignment assignment;

    public NotAssignableException(ASTAssignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public String getMessage() {
        return "Type Error: Assignment made to a not assignable variable\n" +
                " on line " + assignment.getLine() + " at " + assignment.getCharPositionInLine();
    }
}
