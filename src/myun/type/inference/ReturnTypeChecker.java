package myun.type.inference;

import myun.AST.*;
import myun.type.MyunType;

/**
 * Checks if all expressions in function return statements are of the same type as the declared function return type.
 */
final class ReturnTypeChecker implements ASTVisitor<Void> {
    private final MyunType targetType;

    ReturnTypeChecker(ASTFuncDef node) {
        targetType = node.getReturnType();
    }

    @Override
    public Void visit(ASTAssignment node) {
        return null;
    }

    @Override
    public Void visit(ASTBlock node) {
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(fR -> fR.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTBranch node) {
        node.getBlocks().forEach(block -> block.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        return null;
    }

    @Override
    public <CT> Void visit(ASTConstant<CT> node) {
        return null;
    }

    @Override
    public Void visit(ASTDeclaration node) {
        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        return node.getBlock().accept(this);
    }

    @Override
    public Void visit(ASTFuncCall node) {
        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        return node.getBlock().accept(this);
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        MyunType returnType = node.getExpr().getType();
        if (!returnType.equals(targetType)) {
            throw new TypeMismatchException(returnType, targetType, node.getExpr().getSourcePosition());
        }
        return null;
    }

    @Override
    public Void visit(ASTLoopBreak node) {
        return null;
    }

    @Override
    public Void visit(ASTScript node) {
        return null;
    }

    @Override
    public Void visit(ASTVariable node) {
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        return node.getBlock().accept(this);
    }
}
