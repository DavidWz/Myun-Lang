package myun.AST.constraints;

import myun.AST.ParserException;
import myun.AST.SourcePosition;

/**
 * Thrown when a function has the same name as a script.
 */
class FunctionAndScriptSameNameException extends ParserException {
    FunctionAndScriptSameNameException(SourcePosition sourcePosition) {
        super("Script has the same name as a function defined on ", sourcePosition);
    }
}
