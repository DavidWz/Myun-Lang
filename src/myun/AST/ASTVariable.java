package myun.AST;

/**
 * Represents a variable.
 */
public class ASTVariable extends ASTExpression {
    private String name;

    /**
     * Creates a new AST variable.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param name               The name of this variable
     */
    public ASTVariable(int lineNumber, int charPositionInLine, String name) {
        super(lineNumber, charPositionInLine);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
