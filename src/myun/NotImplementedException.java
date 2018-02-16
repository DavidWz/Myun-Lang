package myun;

import myun.AST.ASTNode;

/**
 * Thrown when a feature is not implemented yet.
 */
public class NotImplementedException extends MyunException {
    private final ASTNode reason;

    public NotImplementedException(String msg, ASTNode reason) {
        super(msg);
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " on line " + reason.getLine() + " at " + reason.getCharPositionInLine();
    }
}
