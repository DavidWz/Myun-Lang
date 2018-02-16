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
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param condition          The condition for this loop
     * @param block              The loop body
     */
    public ASTWhileLoop(int lineNumber, int charPositionInLine, ASTExpression condition, ASTBlock block) {
        super(lineNumber, charPositionInLine);
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
