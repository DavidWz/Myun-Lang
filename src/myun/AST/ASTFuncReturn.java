package myun.AST;

/**
 * Represents a function return statement.
 */
public class ASTFuncReturn extends ASTStatement {
    private final ASTExpression expr;

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

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
