package myun.AST;

/**
 * Represents a function return statement.
 */
public class ASTFuncReturn extends ASTStatement {
    private ASTExpression expr;

    /**
     * Creates a new AST function return.
     *
     * @param sourcePos the position of this node in the source code
     * @param expr               The returned expression
     */
    public ASTFuncReturn(SourcePosition sourcePos, ASTExpression expr) {
        super(sourcePos);
        this.expr = expr;
    }

    public ASTExpression getExpr() {
        return expr;
    }

    public void setExpr(ASTVariable expr) {
        this.expr = expr;
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
