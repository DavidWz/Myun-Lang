package myun.AST;

/**
 * Visitor pattern only for the AST expressions.
 */
public interface ASTExpressionVisitor<T> {
    <CT> T visit(ASTConstant<CT> node);
    T visit(ASTFuncCall node);
    T visit(ASTVariable node);
}
