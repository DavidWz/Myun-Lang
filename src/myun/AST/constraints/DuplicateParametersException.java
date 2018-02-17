package myun.AST.constraints;

import myun.AST.ASTVariable;
import myun.MyunException;

class DuplicateParametersException extends MyunException {
    private final ASTVariable reason;

    DuplicateParametersException(ASTVariable reason) {
        super(reason.getSourcePosition());
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return "Duplicate parameter name \"" + reason.getName() + "\" found in function definition " +
                "on " + sourcePosition;
    }
}
