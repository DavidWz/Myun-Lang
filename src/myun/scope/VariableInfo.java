package myun.scope;

import myun.AST.ASTNode;
import myun.AST.ASTType;

/**
 * Stores information about variables.
 */
public class VariableInfo {
    // the type of this variable
    private ASTType type;

    // whether it is possible to assign new values to that variable
    private boolean isAssignable;

    // the node in which this variable was declared
    // e.g. ASTDeclaration, ASTForLoop, ASTFuncDef, ...
    private ASTNode declaration;

    public VariableInfo(ASTType type, boolean isAssignable, ASTNode declaration) {
        this.type = type;
        this.isAssignable = isAssignable;
        this.declaration = declaration;
    }

    public ASTType getType() {
        return type;
    }

    public boolean isAssignable() {
        return isAssignable;
    }

    public ASTNode getDeclaration() {
        return declaration;
    }
}
