package myun.AST;

import myun.scope.Scope;

/**
 * Most general AST node.
 */
public abstract class ASTNode {
    // position in source code
    protected int lineNumber;
    protected int charPositionInLine;

    // scope to which this node belongs
    protected Scope scope;

    /**
     * Creates a new AST node.
     * @param lineNumber The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     */
    public ASTNode(int lineNumber, int charPositionInLine) {
        this.lineNumber = lineNumber;
        this.charPositionInLine = charPositionInLine;
        this.scope = null;
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

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /**
     * Visits a given AST visitor.
     * @param visitor the ast visitor
     */
    public abstract <T> T accept(ASTVisitor<T> visitor);
}
