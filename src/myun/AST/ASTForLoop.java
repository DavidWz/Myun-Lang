package myun.AST;

/**
 * Represents a for loop.
 */
public class ASTForLoop extends ASTStatement {
    private final ASTVariable variable;
    private ASTExpression from;
    private ASTExpression to;
    private final ASTBlock block;

    /**
     * Creates a new AST for loop.
     *
     * @param sourcePos the position of this node in the source code
     * @param variable           The iteration variable
     * @param from               The starting value
     * @param to                 The end value
     * @param block              The execution block
     */
    public ASTForLoop(SourcePosition sourcePos,
                      ASTVariable variable, ASTExpression from, ASTExpression to, ASTBlock block) {
        super(sourcePos);
        this.variable = variable;
        this.from = from;
        this.to = to;
        this.block = block;
    }

    public ASTVariable getVariable() {
        return variable;
    }

    public ASTExpression getFrom() {
        return from;
    }

    public ASTExpression getTo() {
        return to;
    }

    public ASTBlock getBlock() {
        return block;
    }

    public void setFrom(ASTExpression from) {
        this.from = from;
    }

    public void setTo(ASTExpression to) {
        this.to = to;
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
