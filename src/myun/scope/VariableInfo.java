package myun.scope;

import myun.AST.ASTNode;
import myun.type.MyunType;

/**
 * Stores information about variables.
 */
public class VariableInfo {
    // the type of this variable
    private final MyunType type;

    // whether it is possible to assign new values to that variable
    private final boolean isAssignable;

    // the node in which this variable was declared
    // e.g. ASTDeclaration, ASTForLoop, ASTFuncDef, ...
    private final ASTNode declaration;

    public VariableInfo(MyunType type, boolean isAssignable, ASTNode declaration) {
        this.type = type;
        this.isAssignable = isAssignable;
        this.declaration = declaration;
    }

    public MyunType getType() {
        return type;
    }

    public boolean isAssignable() {
        return isAssignable;
    }

    public ASTNode getDeclaration() {
        return declaration;
    }
}
