package myun.AST;

/**
 * Represents a constant.
 */
public class ASTConstant<CT> extends ASTExpression {
    private final CT value;

    /**
     * Creates a new AST constant.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param value              The value of this constant.
     */
    public ASTConstant(int lineNumber, int charPositionInLine, CT value) {
        super(lineNumber, charPositionInLine);
        this.value = value;
    }

    public CT getValue() {
        return value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
