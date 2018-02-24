package myun.AST;

/**
 * Represents a constant.
 */
public class ASTConstant<CT> extends ASTExpression {
    private final CT value;

    /**
     * Creates a new AST constant.
     *
     * @param sourcePos the position of this node in the source code
     * @param value              The value of this constant.
     */
    public ASTConstant(SourcePosition sourcePos, CT value) {
        super(sourcePos);
        this.value = value;
    }

    public CT getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(ASTExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
