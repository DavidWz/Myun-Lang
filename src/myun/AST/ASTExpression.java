package myun.AST;

import java.util.Optional;

/**
 * Represents a general expression.
 */
public abstract class ASTExpression extends ASTNode {
    protected ASTType type;

    /**
     * Creates a new AST expression (without any type information)
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTExpression(int lineNumber, int charPositionInLine) {
        super(lineNumber, charPositionInLine);
    }

    public Optional<ASTType> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(ASTType type) {
        this.type = type;
    }
}
