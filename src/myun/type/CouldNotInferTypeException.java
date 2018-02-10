package myun.type;

import myun.AST.ASTExpression;
import myun.AST.MyunPrettyPrinter;

/**
 * Thrown when the type of an expression could not be inferred.
 */
public class CouldNotInferTypeException extends RuntimeException {
    private ASTExpression expression;
    private MyunPrettyPrinter prettyPrinter;

    public CouldNotInferTypeException(ASTExpression expr) {
        this.expression = expr;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Type Error: Could not infer type of expression \n");
        errorMsg.append(expression.accept(prettyPrinter));
        errorMsg.append(" on line ").append(expression.getLine());
        errorMsg.append(" at ").append(expression.getCharPositionInLine());
        return errorMsg.toString();
    }
}
