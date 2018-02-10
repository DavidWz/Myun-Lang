package myun.scope;

import myun.AST.ASTNode;
import myun.AST.MyunPrettyPrinter;

/**
 * Thrown when a variable or function is illegally redefined.
 */
public class IllegalRedefineException extends RuntimeException {
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
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error: Illegal redefinition of ").append(name);
        errorMsg.append(" on line ").append(redefinedNode.getLine()).append(" at ").append(redefinedNode.getCharPositionInLine());
        errorMsg.append(" first defined on line ").append(originalNode.getLine());
        errorMsg.append(" at ").append(originalNode.getCharPositionInLine());
        return errorMsg.toString();
    }
}
