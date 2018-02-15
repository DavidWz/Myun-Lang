package myun.scope;

import myun.AST.*;

import java.util.*;

/**
 * Represents the scope of a code block.
 */
public class Scope {
    /**
     * Stores information about variables.
     */
    private class VariableInfo {
        boolean isAssignable;

        VariableInfo(boolean isAssignable) {
            this.isAssignable = isAssignable;
        }
    }

    /**
     * Stores information about functions.
     */
    private class FunctionInfo {
        ASTFuncDef originalDefinition;

        FunctionInfo(ASTFuncDef originalDefinition) {
            this.originalDefinition = originalDefinition;
        }
    }

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
     * Checks if a variable has already been declared in this scope.
     *
     * @param var the variable
     * @return true iff the variable has been declared
     */
    public boolean containsVariable(ASTVariable var) {
        return declaredVariables.containsKey(var) || (parent != null && parent.containsVariable(var));
    }

    /**
     * Declares the variable in the current scope.
     * Can only be called when the variable has not been declared already.
     *
     * @param variable the variable
     * @param astType  the type of the variable
     * @param isAssignable whether the variable is assignable
     * @throws IllegalRedefineException thrown when the variable has already been declared
     */
    public void declareVariable(ASTVariable variable, ASTType astType, boolean isAssignable) throws IllegalRedefineException {
        // check for illegal redefinition of the variable
        if (containsVariable(variable)) {
            ASTVariable originalVar = getFirstDeclaredVariable(variable).
                    orElseThrow(() -> new RuntimeException("Scope returned empty optional even though " +
                            "containsVariable method returned true."));
            throw new IllegalRedefineException(variable.getName(), originalVar, variable);
        }
        else {
            variable.setType(astType);
            declaredVariables.put(variable, new VariableInfo(isAssignable));
        }
    }

    private Optional<VariableInfo> getVarInfo(ASTVariable var) {
        // search for the variable info in this scope
        if (declaredVariables.containsKey(var)) {
            return Optional.of(declaredVariables.get(var));
        }
        else if (parent != null) {
            // search for it in the parent scope
            return parent.getVarInfo(var);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the variable which was first encountered for this identifier.
     *
     * @param var the variable
     * @return the first declared ast variable
     */
    public Optional<ASTVariable> getFirstDeclaredVariable(ASTVariable var) {
        // search for the variable in this scope
        for (ASTVariable declaredVar : declaredVariables.keySet()) {
            if (declaredVar.equals(var)) {
                return Optional.of(declaredVar);
            }
        }

        if (parent != null) {
            // search for it in the parent scope
            return parent.getFirstDeclaredVariable(var);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Determines the type of a variable.
     *
     * @param var the variable
     * @return the type of the variable or empty if not found
     */
    public Optional<ASTType> getVariableType(ASTVariable var) {
        Optional<ASTVariable> firstVar = getFirstDeclaredVariable(var);
        if (firstVar.isPresent()) {
            return firstVar.get().getType();
        }
        else {
            return Optional.empty();
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
            return Optional.of(declaredFunctions.get(funcHeader).originalDefinition);
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

    /**
     * Checks whether the given variable is assignable.
     *
     * @param var the variable
     * @return true iff it is assignable
     */
    public boolean isAssignable(ASTVariable var) {
        return getVarInfo(var).orElseThrow(() -> new UndeclaredVariableUsedException(var)).isAssignable;
    }
}
