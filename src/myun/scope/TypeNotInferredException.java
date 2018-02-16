package myun.scope;

import myun.AST.ASTNode;
import myun.MyunException;

/**
 * Thrown when a type was expected, but not inferred yet.
 */
public class TypeNotInferredException extends MyunException {
    private final ASTNode node;

    public TypeNotInferredException(ASTNode node) {
        super();
        this.node = node;
    }

    @Override
    public String getMessage() {
        return "Error: Type expected but not inferred yet for expression used on line " +
                node.getLine() + " at " + node.getCharPositionInLine();
    }
}
