package myun.scope;

import myun.AST.ASTNode;
import myun.AST.ASTType;

/**
 * Stores information about variables.
 */
public class VariableInfo {
    // the type of this variable
    private final ASTType type;

    // whether it is possible to assign new values to that variable
    private final boolean isAssignable;

    // the node in which this variable was declared
    // e.g. ASTDeclaration, ASTForLoop, ASTFuncDef, ...
    private final ASTNode declaration;

    public VariableInfo(ASTType type, boolean isAssignable, ASTNode declaration) {
        super();
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
