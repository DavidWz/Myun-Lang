package myun.AST;

import myun.type.MyunType;
import myun.type.UnknownType;

/**
 * Represents a general expression.
 */
public abstract class ASTExpression extends ASTNode {
    private MyunType type;

    /**
     * Creates a new AST expression (without any type information)
     *
     * @param sourcePosition the position of this node in the source code
     */
    ASTExpression(SourcePosition sourcePosition) {
        super(sourcePosition);
        type = new UnknownType();
    }

    public MyunType getType() {
        return type;
    }

    public void setType(MyunType type) {
        this.type = type;
    }
}
