package myun.AST;

/**
 * Represents a while loop.
 */
public class ASTWhileLoop extends ASTStatement {
    private final ASTExpression condition;
    private final ASTBlock block;

    /**
     * Creates a new AST while loop.
     *
     * @param sourcePos the position of this node in the source code
     * @param condition          The condition for this loop
     * @param block              The loop body
     */
    public ASTWhileLoop(SourcePosition sourcePos, ASTExpression condition, ASTBlock block) {
        super(sourcePos);
        this.condition = condition;
        this.block = block;
    }

    public ASTExpression getCondition() {
        return condition;
    }

    public ASTBlock getBlock() {
        return block;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
