package myun.AST;

/**
 * Visitor pattern for the AST.
 */
public interface ASTVisitor<T> {
    T visit(ASTAssignment node);
    T visit(ASTBlock node);
    T visit(ASTBoolean node);
    T visit(ASTBranch node);
    T visit(ASTCompileUnit node);
    T visit(ASTFloat node);
    T visit(ASTForLoop node);
    T visit(ASTFuncCall node);
    T visit(ASTFuncDef node);
    T visit(ASTFuncReturn node);
    T visit(ASTInteger node);
    T visit(ASTLoopBreak node);
    T visit(ASTScript node);
    T visit(ASTVariable node);
    T visit(ASTWhileLoop node);
}
