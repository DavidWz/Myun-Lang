package myun.AST;

/**
 * Most general AST node.
 */
public abstract class ASTNode {
    private int lineNumber;
    private int charPositionInLine;

    /**
     * Creates a new AST node.
     * @param lineNumber The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTNode(int lineNumber, int charPositionInLine) {
        this.lineNumber = lineNumber;
        this.charPositionInLine = charPositionInLine;
    }

    /**
     * @return the line in the source where this node starts
     */
    public int getLine() {
        return lineNumber;
    }

    /**
     * @return the character position of this node on its line
     */
    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    /**
     * Visits a given AST visitor.
     * @param visitor the ast visitor
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
