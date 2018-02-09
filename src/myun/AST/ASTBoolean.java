package myun.AST;

/**
 * Represents a boolean.
 */
public class ASTBoolean extends ASTExpression {
    private boolean value;

    /**
     * Creates a new AST boolean.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param value The value of this boolean
     */
    public ASTBoolean(int lineNumber, int charPositionInLine, boolean value) {
        super(lineNumber, charPositionInLine);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
