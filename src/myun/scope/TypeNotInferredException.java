package myun.scope;

import myun.AST.ASTExpression;

/**
 * Thrown when a type was expected, but not inferred yet.
 */
public class TypeNotInferredException extends RuntimeException {
    private ASTExpression node;

    public TypeNotInferredException(ASTExpression node) {
        this.node = node;
    }

    @Override
    public String getMessage() {
        return "Error: Type expected but not inferred yet for expression used on line " +
                node.getLine() + " at " + node.getCharPositionInLine();
    }
}
