package myun.AST.constraints;

import myun.AST.ASTFuncDef;
import myun.AST.ParserException;

/**
 * Thrown when a return statement is missing.
 */
class ReturnMissingException extends ParserException {
    ReturnMissingException(ASTFuncDef reason) {
        super("Function " + reason.getName() + " missing a return statement in each execution path ",
                reason.getSourcePosition());
    }
}
