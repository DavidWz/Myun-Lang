package myun.AST.constraints;

import myun.AST.ASTVariable;
import myun.AST.ParserException;

class DuplicateParametersException extends ParserException {
    DuplicateParametersException(ASTVariable reason) {
        super("Duplicate parameter name \"" + reason.getName() + "\" found in function definition",
                reason.getSourcePosition());
    }
}
