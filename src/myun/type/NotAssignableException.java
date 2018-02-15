package myun.type;

import myun.AST.ASTAssignment;

/**
 * Thrown when an assignment is made even though the assigned variable is not assignable.
 */
class NotAssignableException extends RuntimeException {
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
