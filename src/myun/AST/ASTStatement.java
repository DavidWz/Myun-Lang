package myun.AST;

/**
 * A statement in the program. (one line of code)
 */
public abstract class ASTStatement extends ASTNode {
    /**
     * Creates a new AST statement.
     *
     * @param sourcePosition the position of this node in the source code
     */
    ASTStatement(SourcePosition sourcePosition) {
        super(sourcePosition);
    }

    /**
     * Visits a given AST non-expression visitor.
     * @param visitor the AST visitor
     */
    public abstract void accept(ASTNonExpressionVisitor visitor);
}
