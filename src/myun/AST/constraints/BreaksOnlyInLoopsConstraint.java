package myun.AST.constraints;

import myun.AST.*;

/**
 * Checks whether breaks only appear in loops.
 * (because the grammar itself allows breaks to be at the end of any block)
 */
class BreaksOnlyInLoopsConstraint implements Constraint, ASTVisitor<Void> {
    private int loopDepth;
    
    BreaksOnlyInLoopsConstraint() {
        loopDepth = 0;
    }
    
    @Override
    public void check(ASTCompileUnit compileUnit) {
        loopDepth = 0;
        compileUnit.accept(this);
    }
    
    @Override
    public Void visit(ASTAssignment node) {
        return null;
    }

    @Override
    public Void visit(ASTBlock node) {
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getLoopBreak().ifPresent(lB -> lB.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTBranch node) {
        node.getBlocks().forEach(b -> b.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        node.getFuncDefs().forEach(fD -> fD.accept(this));
        node.getScript().accept(this);
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
        loopDepth++;
        node.getBlock().accept(this);
        loopDepth--;
        return null;
    }

    @Override
    public Void visit(ASTFuncCall node) {
        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        return null;
    }

    @Override
    public Void visit(ASTLoopBreak node) {
        if (loopDepth == 0) {
            throw new BreakOutsideLoopException(node.getSourcePosition());
        }
        return null;
    }

    @Override
    public Void visit(ASTScript node) {
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTProcCall node) {
        return null;
    }

    @Override
    public Void visit(ASTVariable node) {
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        loopDepth++;
        node.getBlock().accept(this);
        loopDepth--;
        return null;
    }
}
