package myun.scope;

import myun.AST.*;

/**
 * Creates and initializes all scopes for the program.
 */
public class ScopeInitializer implements ASTVisitor<Void> {
    private Scope currentScope;

    /**
     * Initializes the scope of the given program.
     *
     * @param program the program
     * @param parentScope the parent scope
     */
    public ScopeInitializer(ASTCompileUnit program, Scope parentScope) {
        super();
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
        Scope parentScope = currentScope;

        // blocks always open their own scope
        currentScope = new Scope(parentScope);
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(fR -> fR.accept(this));
        node.getLoopBreak().ifPresent(lB -> lB.accept(this));

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTBranch node) {
        node.setScope(currentScope);
        node.getConditions().forEach(c -> c.accept(this));
        node.getBlocks().forEach(b -> b.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        node.setScope(currentScope);
        Scope parentScope = currentScope;

        // compile units have their own scope
        currentScope = new Scope(currentScope);
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));
        node.getScript().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTConstant node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTDeclaration node) {
        node.setScope(currentScope);
        node.getVariable().accept(this);
        node.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        node.setScope(currentScope);
        Scope parentScope = currentScope;

        // for loops open their own scope because they declare the iteration variable
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
        node.getArgs().forEach(arg -> arg.accept(this));
        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        node.setScope(currentScope);
        Scope parentScope = currentScope;

        // function definitions have their own scope
        // so that parameter variables do not interfere with other function parameters
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
        node.getCondition().accept(this);
        node.getBlock().accept(this);
        return null;
    }
}
