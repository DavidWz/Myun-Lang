package myun.AST.constraints;

import myun.AST.ASTNode;
import myun.MyunException;

/**
 * Exception for violating context-sensitive constraints on the AST structure.
 */
public class ViolatedConstraintException extends MyunException {
    private ASTNode reason;

    ViolatedConstraintException(String message, ASTNode reason) {
        super(message);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Error on line " + reason.getLine() +
                " at " + reason.getCharPositionInLine() + ": " +
                super.getMessage();
    }
}
