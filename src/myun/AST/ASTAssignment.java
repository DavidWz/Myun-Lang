package myun.AST;

/**
 * Represents an assignment of an expression to a variable.
 */
public class ASTAssignment extends ASTStatement {
    private final ASTVariable variable;
    private final ASTExpression expr;

    /**
     * Creates a new AST assignment.
     *
     * @param sourcePos the position of this node in the source code
     * @param variable           The assigned variable
     * @param expr               The expression the variable is assigned to
     */
    public ASTAssignment(SourcePosition sourcePos, ASTVariable variable, ASTExpression expr) {
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
