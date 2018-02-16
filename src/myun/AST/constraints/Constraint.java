package myun.AST.constraints;

import myun.AST.ASTCompileUnit;
import myun.MyunException;

/**
 * Interface for context-sensitive constraints on AST nodes.
 */
interface Constraint {
    void check(ASTCompileUnit compileUnit) throws MyunException;
}
