package myun.AST.constraints;

import myun.AST.ASTVariable;
import myun.MyunException;

class DuplicateParametersException extends MyunException {
    private final ASTVariable reason;

    DuplicateParametersException(ASTVariable reason) {
        super();
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Duplicate parameter name \"" + reason.getName() + "\" found in function definition " +
                "on line " + reason.getLine() + " at " + reason.getCharPositionInLine();
    }
}
