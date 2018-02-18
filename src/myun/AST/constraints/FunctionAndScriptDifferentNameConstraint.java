package myun.AST.constraints;

import myun.AST.*;

/**
 * Checks whether functions and scripts have different names.
 */
class FunctionAndScriptDifferentNameConstraint implements Constraint, ASTVisitor<Boolean>{
    private String scriptName;
    private SourcePosition funcDefPos;
    
    FunctionAndScriptDifferentNameConstraint() {
        scriptName = "";
        funcDefPos = new SourcePosition();
    }

    @Override
    public void check(ASTCompileUnit compileUnit) {
        if (!compileUnit.accept(this)) {
            throw new FunctionAndScriptSameNameException(funcDefPos);
        }
    }

    @Override
    public Boolean visit(ASTAssignment node) {
        return true;
    }

    @Override
    public Boolean visit(ASTBlock node) {
        return true;
    }

    @Override
    public Boolean visit(ASTBranch node) {
        return true;
    }

    @Override
    public Boolean visit(ASTCompileUnit node) {
        scriptName = node.getScript().getName();
        return node.getFuncDefs().stream().allMatch(def -> def.accept(this));
    }

    @Override
    public <CT> Boolean visit(ASTConstant<CT> node) {
        return true;
    }

    @Override
    public Boolean visit(ASTDeclaration node) {
        return true;
    }

    @Override
    public Boolean visit(ASTForLoop node) {
        return true;
    }

    @Override
    public Boolean visit(ASTFuncCall node) {
        return true;
    }

    @Override
    public Boolean visit(ASTFuncDef node) {
        if (scriptName.equals(node.getName())) {
            funcDefPos = node.getSourcePosition();
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public Boolean visit(ASTFuncReturn node) {
        return true;
    }

    @Override
    public Boolean visit(ASTLoopBreak node) {
        return true;
    }

    @Override
    public Boolean visit(ASTScript node) {
        return true;
    }

    @Override
    public Boolean visit(ASTProcCall node) {
        return true;
    }

    @Override
    public Boolean visit(ASTVariable node) {
        return true;
    }

    @Override
    public Boolean visit(ASTWhileLoop node) {
        return true;
    }
}
