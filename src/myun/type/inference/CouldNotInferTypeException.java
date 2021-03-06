package myun.type.inference;

import myun.AST.ASTExpression;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Thrown when the type of an expression could not be inferred.
 */
class CouldNotInferTypeException extends MyunException {
    private final ASTExpression expression;
    private final MyunPrettyPrinter prettyPrinter;

    CouldNotInferTypeException(ASTExpression expr) {
        super(expr.getSourcePosition());
        expression = expr;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Type Error: Could not infer type of expression \n" +
                expression.accept(prettyPrinter) +
                " on " + sourcePosition;
    }
}
