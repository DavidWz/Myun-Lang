package myun.scope;

import myun.AST.ASTNode;
import myun.MyunException;

/**
 * Thrown when a variable or function is illegally redefined.
 */
public class IllegalRedefineException extends MyunException {
    private String name;
    private ASTNode originalNode;
    private ASTNode redefinedNode;

    public IllegalRedefineException(String name, ASTNode originalNode, ASTNode redefinedNode) {
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
