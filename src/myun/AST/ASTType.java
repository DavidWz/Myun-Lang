package myun.AST;

/**
 * Abstract super class for Myun types.
 */
public abstract class ASTType extends ASTNode {
    /**
     * Creates a new AST type.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTType(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
    }
}
