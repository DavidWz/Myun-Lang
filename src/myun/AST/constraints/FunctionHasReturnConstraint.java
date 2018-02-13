package myun.AST.constraints;

import myun.AST.*;

/**
 * Checks whether each program flow in a function has a return statement.
 */
public class FunctionHasReturnConstraint implements Constraint, ASTVisitor<Boolean> {
    private final static String ERROR_MSG = "Each execution path in a function must have a return statement.";
    private ASTFuncDef funcWithoutReturn;

    public FunctionHasReturnConstraint() {
        this.funcWithoutReturn = null;
    }

    @Override
    public void check(ASTCompileUnit compileUnit) throws ViolatedConstraintException {
        funcWithoutReturn = null;

        if(!compileUnit.accept(this)) {
            throw new ViolatedConstraintException(ERROR_MSG, funcWithoutReturn.getLine(), funcWithoutReturn.getCharPositionInLine());
        }
    }

    @Override
    public Boolean visit(ASTAssignment node) {
        return false;
    }

    @Override
    public Boolean visit(ASTBasicType node) {
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
        // we need to traverse the statements from
        else {
            for (int i = node.getStatements().size()-1; i >= 0; i--) {
                if (node.getStatements().get(i).accept(this)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Boolean visit(ASTBranch node) {
        if (node.hasElse()) {
            return node.getBlocks().stream().allMatch(block -> block.accept(this));
        }
        else {
            return false;
        }
    }

    @Override
    public Boolean visit(ASTCompileUnit node) {
        return node.getFuncDefs().stream().allMatch(funcDef -> funcDef.accept(this));
    }

    @Override
    public Boolean visit(ASTConstant node) {
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
        boolean hasReturn = node.getBlock().accept(this);
        if (!hasReturn) {
            this.funcWithoutReturn = node;
        }
        return hasReturn;
    }

    @Override
    public Boolean visit(ASTFuncReturn node) {
        return true;
    }

    @Override
    public Boolean visit(ASTFuncType node) {
        return false;
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
    public Boolean visit(ASTVariable node) {
        return false;
    }

    @Override
    public Boolean visit(ASTWhileLoop node) {
        return false;
    }
}
