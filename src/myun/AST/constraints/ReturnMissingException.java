package myun.AST.constraints;

import myun.AST.ASTFuncDef;
import myun.MyunException;

/**
 * Thrown when a return statement is missing.
 */
class ReturnMissingException extends MyunException {
    private final ASTFuncDef reason;

    ReturnMissingException(ASTFuncDef reason) {
        super();
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Function " + reason.getName() + " missing a return statement in each execution path " +
                "on line " + reason.getLine() + " at " + reason.getCharPositionInLine();
    }
}
