package myun.AST;

/**
 * Visitor pattern for AST statements, function definitions, etc.
 * (Everything that is not an expression).
 * @noinspection UnusedParameters
 */
public interface ASTNonExpressionVisitor {
    void visit(ASTAssignment node);
    void visit(ASTBlock node);
    void visit(ASTBranch node);
    void visit(ASTCompileUnit node);
    void visit(ASTDeclaration node);
    void visit(ASTForLoop node);
    void visit(ASTFuncDef node);
    void visit(ASTFuncReturn node);
    void visit(ASTLoopBreak node);
    void visit(ASTProcCall node);
    void visit(ASTScript node);
    void visit(ASTWhileLoop node);
}
