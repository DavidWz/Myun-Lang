package myun.AST.constraints;

import myun.AST.ASTCompileUnit;

/**
 * Interface for context-sensitive constraints on AST nodes.
 */
public interface Constraint {
    void check(ASTCompileUnit compileUnit) throws ViolatedConstraintException;
}
