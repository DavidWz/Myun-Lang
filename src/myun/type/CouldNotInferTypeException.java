package myun.type;

import myun.AST.ASTExpression;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Thrown when the type of an expression could not be inferred.
 */
class CouldNotInferTypeException extends MyunException {
    private ASTExpression expression;
    private MyunPrettyPrinter prettyPrinter;

    CouldNotInferTypeException(ASTExpression expr) {
        this.expression = expr;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Type Error: Could not infer type of expression \n" +
                expression.accept(prettyPrinter) +
                " on line " + expression.getLine() +
                " at " + expression.getCharPositionInLine();
    }
}
