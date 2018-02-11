package myun.scope;

import myun.AST.*;

/**
 * Creates and initializes all scopes for the program.
 */
public class ScopeInitializer implements ASTVisitor<Void> {
    private Scope currentScope;

    public ScopeInitializer() {
        currentScope = null;
    }

    public void initScope(ASTCompileUnit program, Scope parentScope) {
        currentScope = parentScope;
        program.accept(this);
    }

    @Override
    public Void visit(ASTAssignment node) {
        node.setScope(currentScope);
        node.getVariable().accept(this);
        node.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTBasicType node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTBlock node) {
        node.setScope(currentScope);
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(fR -> fR.accept(this));
        node.getLoopBreak().ifPresent(lB -> lB.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTBoolean node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTBranch node) {
        node.setScope(currentScope);
        node.getConditions().forEach(c -> c.accept(this));

        Scope parentScope = currentScope;
        for (ASTBlock block : node.getBlocks()) {
            currentScope = new Scope(parentScope);
            block.accept(this);
            currentScope = parentScope;
        }

        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        node.setScope(currentScope);
        currentScope = new Scope(currentScope);
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));
        node.getScript().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTFloat node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        node.setScope(currentScope);

        Scope parentScope = currentScope;
        currentScope = new Scope(parentScope);

        node.getVariable().accept(this);
        node.getFrom().accept(this);
        node.getTo().accept(this);
        node.getBlock().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTFuncCall node) {
        node.setScope(currentScope);
        node.getFunction().accept(this);
        node.getArgs().forEach(arg -> arg.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        node.setScope(currentScope);

        Scope parentScope = currentScope;
        currentScope = new Scope(parentScope);

        node.getParameters().forEach(param -> {
            param.accept(this);
            param.getType().ifPresent(t -> t.accept(this));
        });
        node.getReturnType().ifPresent(t -> t.accept(this));
        node.getBlock().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        node.setScope(currentScope);
        node.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTFuncType node) {
        node.setScope(currentScope);
        node.getParameterTypes().forEach(p -> p.accept(this));
        node.getReturnType().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTInteger node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTLoopBreak node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTScript node) {
        node.setScope(currentScope);
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTVariable node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        node.setScope(currentScope);

        Scope parentScope = currentScope;
        currentScope = new Scope(parentScope);

        node.getCondition().accept(this);
        node.getBlock().accept(this);

        currentScope = parentScope;

        return null;
    }
}
