package myun.AST;

/**
 * Represents an assignment of an expression to a variable.
 */
public class ASTAssignment extends ASTStatement {
    private ASTVariable variable;
    private ASTExpression expr;

    /**
     * Creates a new AST assignment.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param variable The assigned variable
     * @param expr The expression the variable is assigned to
     */
    public ASTAssignment(int lineNumber, int charPositionInLine, ASTVariable variable, ASTExpression expr) {
        super(lineNumber, charPositionInLine);
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
