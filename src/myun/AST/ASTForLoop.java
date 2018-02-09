package myun.AST;

/**
 * Represents a for loop.
 */
public class ASTForLoop extends ASTStatement {
    private ASTVariable variable;
    private ASTExpression from;
    private ASTExpression to;
    private ASTBlock block;

    /**
     * Creates a new AST for loop.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param variable           The iteration variable
     * @param from               The starting value
     * @param to                 The end value
     * @param block              The execution block
     */
    public ASTForLoop(int lineNumber, int charPositionInLine, ASTVariable variable, ASTExpression from, ASTExpression to, ASTBlock block) {
        super(lineNumber, charPositionInLine);
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

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
