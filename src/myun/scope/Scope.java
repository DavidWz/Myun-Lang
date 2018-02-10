package myun.scope;

import myun.AST.ASTFuncType;
import myun.AST.ASTType;
import myun.AST.ASTVariable;

import java.util.*;

/**
 * Represents the scope of a code block.
 */
public class Scope {
    private Scope parent;
    private Set<ASTVariable> declaredVariables;
    private Map<String, Set<ASTFuncType>> declaredFunctions;

    public Scope(Scope parent) {
        this.parent = parent;
        this.declaredVariables = new HashSet<>();
        this.declaredFunctions = new HashMap<>();
    }

    /**
     * Declares the variable in the current scope.
     * @param variable the variable
     * @param astType the type of the variable
     */
    public void declareVariable(ASTVariable variable, ASTType astType) {
        variable.setType(astType);
        declaredVariables.add(variable);
    }

    /**
     * Checks if a variable is defined in this scope.
     * @param var the variable
     * @return true iff the variable has been defined
     */
    public boolean containsVariable(ASTVariable var) {
        if (declaredVariables.contains(var)) {
            return true;
        }
        else if (parent != null) {
            return parent.containsVariable(var);
        }
        else {
            return false;
        }
    }

    /**
     * Determines the type of a variable.
     * @param var the variable
     * @return the type of the variable or empty if not found
     */
    public Optional<ASTType> getVariableType(ASTVariable var) {
        // search for the variable in this scope
        Optional<ASTType> varType = Optional.empty();
        for (ASTVariable declaredVar : declaredVariables) {
            if (declaredVar.equals(var)) {
                varType = declaredVar.getType();
                break;
            }
        }
        if (varType.isPresent()) {
            return varType;
        }
        else if (parent != null) {
            // search for it in the parent scope
            return parent.getVariableType(var);
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns the variable which was first encountered for this identifier.
     * @param var the variable
     * @return the first declared ast variable
     */
    public Optional<ASTVariable> getFirstDeclaredVariable(ASTVariable var) {
        // search for the variable in this scope
        for (ASTVariable declaredVar : declaredVariables) {
            if (declaredVar.equals(var)) {
                return Optional.of(declaredVar);
            }
        }

        if (parent != null) {
            // search for it in the parent scope
            return parent.getFirstDeclaredVariable(var);
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Declares a function in the current scope.
     * @param name the name of the function
     * @param funcType the type of the function
     */
    public void declareFunction(String name, ASTFuncType funcType) {
        if (declaredFunctions.containsKey(name)) {
            // there already are previous entry, so we need to overload
            declaredFunctions.get(name).add(funcType);
        }
        else {
            // create a new entry for this function
            Set<ASTFuncType> funcTypes = new HashSet<>();
            funcTypes.add(funcType);
            declaredFunctions.put(name, funcTypes);
        }
    }

    /**
     * Determines the return type of a function given the parameter types.
     * @param name the name of the function
     * @param paramTypes the parameter types
     * @return the return type or empty if not defined
     */
    public Optional<ASTType> getReturnType(String name, List<ASTType> paramTypes) {
        if (declaredFunctions.containsKey(name)) {
            // search for a definition in the current scope
            Set<ASTFuncType> funcTypes = declaredFunctions.get(name);
            for (ASTFuncType funcType : funcTypes) {
                if (funcType.getParameterTypes().equals(paramTypes)) {
                    return Optional.of(funcType.getReturnType());
                }
            }
        }

        // search for a definition in the parent scope
        if (parent != null) {
            return parent.getReturnType(name, paramTypes);
        }
        else {
            return Optional.empty();
        }
    }
}
