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
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param variable           The declared variable
     * @param expr               The expression the variable is assigned to
     */
    public ASTDeclaration(int lineNumber, int charPositionInLine, ASTVariable variable, ASTExpression expr) {
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
