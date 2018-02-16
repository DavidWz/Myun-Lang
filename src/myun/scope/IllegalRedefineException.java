package myun.scope;

import myun.AST.ASTNode;
import myun.MyunException;

/**
 * Thrown when a variable or function is illegally redefined.
 */
public class IllegalRedefineException extends MyunException {
    private final String name;
    private final ASTNode originalNode;
    private final ASTNode redefinedNode;

    public IllegalRedefineException(String name, ASTNode originalNode, ASTNode redefinedNode) {
        super();
        this.name = name;
        this.originalNode = originalNode;
        this.redefinedNode = redefinedNode;
    }

    @Override
    public String getMessage() {
        return "Error: Illegal redefinition of " + name +
                " on line " + redefinedNode.getLine() + " at " + redefinedNode.getCharPositionInLine() +
                " first defined on line " + originalNode.getLine() +
                " at " + originalNode.getCharPositionInLine();
    }
}
