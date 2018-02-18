package myun.AST.constraints;

import myun.AST.*;

/**
 * Checks whether each program flow in a function has a return statement.
 */
class FunctionHasReturnConstraint implements Constraint, ASTVisitor<Boolean> {

    @Override
    public void check(ASTCompileUnit compileUnit) {
        compileUnit.accept(this);
    }

    @Override
    public Boolean visit(ASTAssignment node) {
        return false;
    }

    @Override
    public Boolean visit(ASTBlock node) {
        // if the block has a return statement at the end
        // we do not need to check the rest of the block any more
        if (node.getFuncReturn().isPresent()) {
            return true;
        }
        // if there is no return statement at the end
        // we need to traverse the statements from end to start and check for return statements
        else {
            for (int i = node.getStatements().size() - 1; i >= 0; i--) {
                if (node.getStatements().get(i).accept(this)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Boolean visit(ASTBranch node) {
        return node.hasElse() && node.getBlocks().stream().allMatch(block -> block.accept(this));
    }

    @Override
    public Boolean visit(ASTCompileUnit node) {
        return node.getFuncDefs().stream().allMatch(funcDef -> funcDef.accept(this));
    }

    @Override
    public <CT> Boolean visit(ASTConstant<CT> node) {
        return false;
    }

    @Override
    public Boolean visit(ASTDeclaration node) {
        return false;
    }

    @Override
    public Boolean visit(ASTForLoop node) {
        return false;
    }

    @Override
    public Boolean visit(ASTFuncCall node) {
        return false;
    }

    @Override
    public Boolean visit(ASTFuncDef node) {
        if (!node.getBlock().accept(this)) {
            throw new ReturnMissingException(node);
        }
        return true;
    }

    @Override
    public Boolean visit(ASTFuncReturn node) {
        return true;
    }

    @Override
    public Boolean visit(ASTLoopBreak node) {
        return false;
    }

    @Override
    public Boolean visit(ASTScript node) {
        return false;
    }

    @Override
    public Boolean visit(ASTProcCall node) {
        return false;
    }

    @Override
    public Boolean visit(ASTVariable node) {
        return false;
    }

    @Override
    public Boolean visit(ASTWhileLoop node) {
        return false;
    }
}
