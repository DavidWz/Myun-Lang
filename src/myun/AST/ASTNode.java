package myun.AST;

import myun.scope.Scope;

/**
 * Most general AST node.
 */
public abstract class ASTNode {
    // position in source code
    private final SourcePosition sourcePosition;

    // scope to which this node belongs
    private Scope scope;

    /**
     * Creates a new AST node.
     * @param sourcePosition the position of this node in the source code
     */
    ASTNode(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
        scope = new Scope(null);
    }

    public SourcePosition getSourcePosition() {
        return sourcePosition;
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
