package myun.scope;

import myun.AST.ASTFuncDef;
import myun.type.FuncType;
import myun.type.MyunType;

/**
 * Stores information about functions.
 */
public class FunctionInfo {
    // the type of this function
    private final FuncType type;

    // the function definition
    private final ASTFuncDef originalDefinition;

    FunctionInfo(FuncType type, ASTFuncDef originalDefinition) {
        this.type = type;
        this.originalDefinition = originalDefinition;
    }

    public FuncType getType() {
        return type;
    }

    public ASTFuncDef getFuncDef() {
        return originalDefinition;
    }

    public void setReturnType(MyunType returnType) {
        type.setReturnType(returnType);
    }
}
