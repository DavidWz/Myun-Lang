package myun.AST;

/**
 * A statement in the program. (one line of code)
 */
public abstract class ASTStatement extends ASTNode {
    /**
     * Creates a new AST node.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTStatement(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
    }
}
