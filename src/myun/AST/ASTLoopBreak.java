package myun.AST;

/**
 * Represtens a loop break.
 */
public class ASTLoopBreak extends ASTStatement {
    /**
     * Creates a new AST node.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTLoopBreak(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
