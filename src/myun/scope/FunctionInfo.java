package myun.scope;

import myun.AST.ASTFuncDef;

/**
 * Stores information about functions.
 */
public class FunctionInfo {
    // the function definition
    private ASTFuncDef originalDefinition;

    FunctionInfo(ASTFuncDef originalDefinition) {
        this.originalDefinition = originalDefinition;
    }

    public ASTFuncDef getFuncDef() {
        return originalDefinition;
    }
}
