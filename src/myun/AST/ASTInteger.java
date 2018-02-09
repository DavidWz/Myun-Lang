package myun.AST;

/**
 * Represents an integer.
 */
public class ASTInteger extends ASTExpression {
    private int value;

    /**
     * Creates a new AST integer
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param value              The value of this integer
     */
    public ASTInteger(int lineNumber, int charPositionInLine, int value) {
        super(lineNumber, charPositionInLine);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
