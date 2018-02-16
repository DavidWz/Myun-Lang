package myun.scope;

import myun.AST.ASTFuncDef;
import myun.AST.ASTFuncType;

/**
 * Stores information about functions.
 */
public class FunctionInfo {
    // the type of this function
    private final ASTFuncType type;

    // the function definition
    private final ASTFuncDef originalDefinition;

    FunctionInfo(ASTFuncType type, ASTFuncDef originalDefinition) {
        super();
        this.type = type;
        this.originalDefinition = originalDefinition;
    }

    public ASTFuncType getType() {
        return type;
    }

    public ASTFuncDef getFuncDef() {
        return originalDefinition;
    }
}
