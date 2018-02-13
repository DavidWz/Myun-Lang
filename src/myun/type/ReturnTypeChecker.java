package myun.type;

import myun.AST.*;

/**
 * Checks if all expressions in function return statements are of the same type as the declared function return type.
 */
class ReturnTypeChecker implements ASTVisitor<Void> {
    private ASTType targetType;

    ReturnTypeChecker() {
        this.targetType = null;
    }

    void checkReturnType(ASTFuncDef node) {
        this.targetType = node.getReturnType().orElseThrow(() -> new RuntimeException("Type inference not supported " +
                "yet!"));
        node.accept(this);
    }

    @Override
    public Void visit(ASTAssignment node) {
        return null;
    }

    @Override
    public Void visit(ASTBasicType node) {
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
    public Void visit(ASTConstant node) {
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
        ASTType returnType = node.getExpr().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getExpr()));
        if (!returnType.equals(targetType)) {
            throw new TypeMismatchException(returnType, targetType, node.getExpr());
        }
        return null;
    }

    @Override
    public Void visit(ASTFuncType node) {
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
