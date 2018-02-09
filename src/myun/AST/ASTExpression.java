package myun.AST;

/**
 * Represents a general expression.
 */
public abstract class ASTExpression extends ASTNode {
    /**
     * Creates a new AST node.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTExpression(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
    }
}
