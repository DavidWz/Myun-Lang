package myun.AST;

/**
 * Represents a variable declaration.
 */
public class ASTDeclaration extends ASTStatement {
    private final ASTVariable variable;
    private final ASTExpression expr;

    /**
     * Creates a new AST declaration.
     *
     * @param sourcePos the position of this node in the source code
     * @param variable           The declared variable
     * @param expr               The expression the variable is assigned to
     */
    public ASTDeclaration(SourcePosition sourcePos, ASTVariable variable, ASTExpression expr) {
        super(sourcePos);
        this.variable = variable;
        this.expr = expr;
    }

    public ASTVariable getVariable() {
        return variable;
    }

    public ASTExpression getExpr() {
        return expr;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
