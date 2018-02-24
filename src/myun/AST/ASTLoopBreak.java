package myun.AST;

/**
 * Represtens a loop break.
 */
public class ASTLoopBreak extends ASTStatement {
    /**
     * Creates a new AST node.
     *
     * @param sourcePos the position of this node in the source code
     */
    public ASTLoopBreak(SourcePosition sourcePos) {
        super(sourcePos);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(ASTNonExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
