package myun.AST;

/**
 * Represents a floating point number.
 */
public class ASTFloat extends ASTExpression {
    private float value;

    /**
     * Creates a new AST float.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param value              The value of this float
     */
    public ASTFloat(int lineNumber, int charPositionInLine, float value) {
        super(lineNumber, charPositionInLine);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
