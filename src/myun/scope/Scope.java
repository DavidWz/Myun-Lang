package myun.scope;

import myun.AST.*;
import myun.type.FuncHeader;
import myun.type.FuncType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the scope of a code block.
 * TODO: Why difference between variables and functions? functions are variables too...
 */
public class Scope {
    private final Scope parent;
    private final Set<ASTVariable> declaredVariables;
    private final Map<FuncHeader, FunctionInfo> declaredFunctions;

    /**
     * Creates a new scope with a given parent.
     *
     * @param parent a parent or null if this is a root scope
     */
    public Scope(Scope parent) {
        this.parent = parent;
        declaredVariables = new HashSet<>();
        declaredFunctions = new HashMap<>();
    }

    /**
     * Checks if a variable has already been declared in this scope or any parent scope.
     *
     * @param var the variable
     * @return true iff the variable has been declared
     */
    private boolean isDeclared(ASTVariable var) {
        return declaredVariables.contains(var) || ((parent != null) && parent.isDeclared(var));
    }

    /**
     * Declares the variable in the current scope.
     * Can only be called when the variable has not been declared already.
     *
     * @param variable the variable information
     * @throws IllegalRedefineException thrown when the variable has been declared in this scope already
     */
    void declareVariable(ASTVariable variable) {
        // check for illegal redefinition of the variable
        if (isDeclared(variable)) {
            throw new IllegalRedefineException(variable.getName(),
                    getActualVariable(variable).getSourcePosition(),
                    variable.getSourcePosition());
        }
        else {
            // add variable to declarations and update type of lhs
            declaredVariables.add(variable);
        }
    }

    /**
     * Retrieves the actual variable object refered to by the name of the given variable object.
     * The variable must be declared in this scope.
     *
     * @param copyVar the variable object that holds the name of the actual variable
     * @return the variable object
     */
    public ASTVariable getActualVariable(ASTVariable copyVar) {
        for (ASTVariable var : declaredVariables) {
            if (var.equals(copyVar)) {
                return var;
            }
        }

        if (parent != null) {
            return parent.getActualVariable(copyVar);
        }
        else {
            throw new UndeclaredVariableUsedException(copyVar);
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

    /**
     * Returns all known declared types for the function with the given name.
     * @param function the name of the function
     * @return all known types
     */
    public Collection<FuncType> getDeclaredFunctionTypes(String function) {
        Collection<FuncType> knownTypes = declaredFunctions.keySet().stream().
                filter(header -> function.equals(header.getName())).
                map(header -> declaredFunctions.get(header).getType()).
                collect(Collectors.toCollection(HashSet::new));

        if (parent != null) {
            knownTypes.addAll(parent.getDeclaredFunctionTypes(function));
        }

        return knownTypes;
    }
}
