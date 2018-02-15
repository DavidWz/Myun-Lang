package myun.scope;

import myun.AST.*;

import java.util.*;

/**
 * Represents the scope of a code block.
 */
public class Scope {
    private Scope parent;
    private Map<ASTVariable, VariableInfo> declaredVariables;
    private Map<FuncHeader, FunctionInfo> declaredFunctions;

    /**
     * Creates a new scope with a given parent.
     *
     * @param parent a parent or null if this is a root scope
     */
    Scope(Scope parent) {
        this.parent = parent;
        this.declaredVariables = new HashMap<>();
        this.declaredFunctions = new HashMap<>();
    }

    /**
     * Checks if a variable has already been declared in this scope or any parent scope.
     *
     * @param var the variable
     * @return true iff the variable has been declared
     */
    public boolean isDeclared(ASTVariable var) {
        return declaredVariables.containsKey(var) || (parent != null && parent.isDeclared(var));
    }

    /**
     * Declares the variable in the current scope.
     * Additionally, sets the type of the variable to the type of the expression.
     * Can only be called when the variable has not been declared already.
     *
     * @param varInfo the variable information
     * @throws IllegalRedefineException thrown when the variable has been declared in this scope already
     */
    public void declareVariable(ASTVariable variable, VariableInfo varInfo) throws IllegalRedefineException {
        // check for illegal redefinition of the variable
        if (isDeclared(variable)) {
            throw new IllegalRedefineException(variable.getName(),
                    getVarInfo(variable).getDeclaration(),
                    varInfo.getDeclaration());
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
     * @param isAssignable whether the variable is assignable
     * @throws IllegalRedefineException thrown when the variable has been declared in this scope already
     * @throws TypeNotInferredException thrown when the type of the expression has not been inferred yet
     */
    public void declareVariable(ASTDeclaration declaration, boolean isAssignable) throws IllegalRedefineException, TypeNotInferredException {
        ASTType type = declaration.getExpr().getType().orElseThrow(() ->
                new TypeNotInferredException(declaration.getExpr()));
        VariableInfo varInfo = new VariableInfo(type, isAssignable, declaration);
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
    public VariableInfo getVarInfo(ASTVariable var) throws UndeclaredVariableUsedException {
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
    public boolean containsFunction(FuncHeader funcHeader) {
        for (FuncHeader header : declaredFunctions.keySet()) {
            if (header.hasSameNameAndParamTypes(funcHeader)) {
                return true;
            }
        }

        return parent != null && parent.containsFunction(funcHeader);
    }

    /**
     * Declares a function in the current scope.
     * Can only be called when the function has not been declared already.
     * However, functions with the same name can be re-declared if the parameter types are different.
     *
     * @param funcHeader the function header
     * @param funcDef the function definition
     * @throws IllegalRedefineException thrown when the function has already been declared
     */
    public void declareFunction(FuncHeader funcHeader, ASTFuncDef funcDef) throws IllegalRedefineException {
        if (containsFunction(funcHeader)) {
            throw new IllegalRedefineException(funcHeader.getName(),
                    getFirstDeclaredFunction(funcHeader).orElseThrow(() -> new RuntimeException("Optional was empty even though containsFunction returned true.")),
                    funcDef);
        }
        else {
            declaredFunctions.put(funcHeader, new FunctionInfo(funcDef));
        }
    }

    /**
     * Returns the function definition that was first encountered for this function header.
     *
     * @param funcHeader the function header
     * @return the first encountered function definition or an empty optional if not found
     */
    public Optional<ASTFuncDef> getFirstDeclaredFunction(FuncHeader funcHeader) {
        if (declaredFunctions.containsKey(funcHeader)) {
            return Optional.of(declaredFunctions.get(funcHeader).getFuncDef());
        }
        else if (parent != null) {
            return parent.getFirstDeclaredFunction(funcHeader);
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Determines the return type of a function given the parameter types.
     *
     * @param name the name of the function
     * @param paramTypes the types of the parameters
     * @return the return type or empty if not defined
     */
    public Optional<ASTType> getReturnType(String name, List<ASTType> paramTypes) {
        for (FuncHeader header : declaredFunctions.keySet()) {
            if (header.getName().equals(name) && header.getType().getParameterTypes().equals(paramTypes)) {
                return Optional.of(header.getType().getReturnType());
            }
        }

        if (parent != null) {
            return parent.getReturnType(name, paramTypes);
        }
        else {
            return Optional.empty();
        }
    }
}
