package myun.scope;

import myun.AST.*;
import myun.type.FuncHeader;
import myun.type.FuncType;
import myun.type.MyunType;

import java.util.*;

/**
 * Represents the scope of a code block.
 */
public class Scope {
    private final Scope parent;
    private final Map<ASTVariable, VariableInfo> declaredVariables;
    private final Map<FuncHeader, FunctionInfo> declaredFunctions;

    /**
     * Creates a new scope with a given parent.
     *
     * @param parent a parent or null if this is a root scope
     */
    public Scope(Scope parent) {
        this.parent = parent;
        declaredVariables = new HashMap<>();
        declaredFunctions = new HashMap<>();
    }

    /**
     * Checks if a variable has already been declared in this scope or any parent scope.
     *
     * @param var the variable
     * @return true iff the variable has been declared
     */
    public boolean isDeclared(ASTVariable var) {
        return declaredVariables.containsKey(var) || ((parent != null) && parent.isDeclared(var));
    }

    /**
     * Declares the variable in the current scope.
     * Additionally, sets the type of the variable to the type of the expression.
     * Can only be called when the variable has not been declared already.
     *
     * @param varInfo the variable information
     * @throws IllegalRedefineException thrown when the variable has been declared in this scope already
     */
    public void declareVariable(ASTVariable variable, VariableInfo varInfo) {
        // check for illegal redefinition of the variable
        if (isDeclared(variable)) {
            throw new IllegalRedefineException(variable.getName(),
                    getVarInfo(variable).getDeclaration().getSourcePosition(),
                    varInfo.getDeclaration().getSourcePosition());
        }
        else if (!varInfo.getType().isFullyKnown()) {
            throw new TypeNotInferredException(variable.getSourcePosition());
        }
        else {
            // add variable to declarations and update type of lhs
            declaredVariables.put(variable, varInfo);
            variable.setType(varInfo.getType());
        }
    }

    /**
     * Declares the variable in the current scope.
     * Additionally, sets the type of the variable to the type of the expression.
     * Can only be called when the variable has not been declared already.
     * Furthermore, the type of the right-hand-side expression needs to have been inferred already.
     *
     * @param declaration the variable declaration
     * @throws IllegalRedefineException thrown when the variable has been declared in this scope already
     * @throws TypeNotInferredException thrown when the type of the expression has not been inferred yet
     */
    public void declareVariable(ASTDeclaration declaration) {
        MyunType type = declaration.getExpr().getType();
        if (!type.isFullyKnown()) {
            throw new TypeNotInferredException(declaration.getExpr().getSourcePosition());
        }

        // user made declarations are always assignable
        VariableInfo varInfo = new VariableInfo(type, true, declaration);
        declareVariable(declaration.getVariable(), varInfo);
    }

    /**
     * Retrieves the variable info for the variable.
     * The variable must be declared in this scope.
     *
     * @param var the variable
     * @return the variable info
     * @throws UndeclaredVariableUsedException thrown when var is not declared in this scope
     */
    public VariableInfo getVarInfo(ASTVariable var) {
        // search for the variable info in this scope
        if (declaredVariables.containsKey(var)) {
            return declaredVariables.get(var);
        }
        else if (parent != null) {
            // search for it in the parent scope
            return parent.getVarInfo(var);
        } else {
            throw new UndeclaredVariableUsedException(var);
        }
    }

    /**
     * Checks if the function has already been declared in this scope.
     *
     * @param funcHeader the function header
     * @return true iff it already has been declared
     */
    public boolean isDeclared(FuncHeader funcHeader) {
        return declaredFunctions.containsKey(funcHeader) || ((parent != null) && parent.isDeclared
                (funcHeader));
    }

    /**
     * Declares a function in the current scope.
     * Can only be called when the function has not been declared already.
     * However, functions with the same name can be re-declared if the parameter types are different.
     * Furthermore, the type of the function needs to have been inferred already.
     *
     * @param funcHeader the function header
     * @param funcDef the function definition
     * @throws IllegalRedefineException thrown when the function has already been declared
     */
    public void declareFunction(FuncHeader funcHeader, ASTFuncDef funcDef) {
        if (isDeclared(funcHeader)) {
            throw new IllegalRedefineException(funcHeader.getName(),
                    getFunctionInfo(funcHeader, funcDef.getSourcePosition()).getFuncDef().getSourcePosition(),
                    funcDef.getSourcePosition());
        }
        else if (!funcDef.getType().isFullyKnown()) {
            throw new TypeNotInferredException(funcDef.getSourcePosition());
        }
        else {
            declaredFunctions.put(funcHeader, new FunctionInfo(funcDef.getType(), funcDef));
        }
    }

    /**
     * Declares an implicitly defined function.
     *
     * @param funcHeader the function header
     * @param funcType the function type
     * @param sourcePos the position where this function was declared
     */
    void declareFunction(FuncHeader funcHeader, FuncType funcType, SourcePosition sourcePos) {
        if (isDeclared(funcHeader)) {
            throw new IllegalRedefineException(funcHeader.getName(),
                    getFunctionInfo(funcHeader, sourcePos).getFuncDef().getSourcePosition(), sourcePos);
        }
        else {
            declaredFunctions.put(funcHeader, new FunctionInfo(funcType, null)); // FIXME: null value for optional
        }
    }

    /**
     * Returns the function information for the given function header.
     * The function must have been defined already.
     *
     * @param funcHeader the function header
     * @return the function information
     * @param sourcePos the position where this function was declared
     * @throws UndeclaredFunctionCalledException thrown when the function has not been declared
     */
    public FunctionInfo getFunctionInfo(FuncHeader funcHeader, SourcePosition sourcePos) {
        if (declaredFunctions.containsKey(funcHeader)) {
            return declaredFunctions.get(funcHeader);
        }
        else if (parent != null) {
            return parent.getFunctionInfo(funcHeader, sourcePos);
        }
        else {
            throw new UndeclaredFunctionCalledException(funcHeader, sourcePos);
        }
    }
}
