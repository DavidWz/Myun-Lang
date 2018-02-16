package myun.AST;

/**
 * Represents a function return statement.
 */
public class ASTFuncReturn extends ASTStatement {
    private final ASTExpression expr;

    /**
     * Creates a new AST function return.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param expr               The returned expression
     */
    public ASTFuncReturn(int lineNumber, int charPositionInLine, ASTExpression expr) {
        super(lineNumber, charPositionInLine);
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
