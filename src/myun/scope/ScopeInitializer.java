package myun.scope;

import myun.AST.*;
import myun.type.PrimitiveTypes;
import myun.type.UnknownType;

/**
 * Creates and initializes all scopes for the program.
 * Also replaces all variable objects that refer to the same variable of a scope with a single unique object.
 */
public final class ScopeInitializer implements ASTVisitor<Void> {
    private Scope currentScope;

    private ScopeInitializer(Scope parentScope) {
        currentScope = parentScope;
    }

    /**
     * Initializes the scopes of the given program.
     *
     * @param program the program
     * @param parentScope the top-most scope
     */
    public static void initScopes(ASTCompileUnit program, Scope parentScope) {
        program.accept(new ScopeInitializer(parentScope));
    }

    @Override
    public Void visit(ASTAssignment node) {
        node.setScope(currentScope);

        // replace the rhs variable by its scoped original variable, if needed
        if (node.getExpr() instanceof ASTVariable) {
            node.setExpr(node.getScope().getActualVariable((ASTVariable) node.getExpr()));
        }
        else {
            node.getExpr().accept(this);
        }

        // replace the lhs variable by its scoped, original variable
        node.setVariable(node.getScope().getActualVariable(node.getVariable()));


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
    public Void visit(ASTBranch node) {
        node.setScope(currentScope);

        for (int i = 0; i < node.getConditions().size(); i++) {
            ASTExpression c = node.getConditions().get(i);

            // if the condition is simply a variable, replace it by its original
            if (c instanceof ASTVariable) {
                node.setCondition(i, node.getScope().getActualVariable((ASTVariable) c));
            }
            else {
                c.accept(this);
            }
        }

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
    public <CT> Void visit(ASTConstant<CT> node) {
        node.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(ASTDeclaration node) {
        node.setScope(currentScope);

        // replace the rhs variable by its scoped original variable, if needed
        if (node.getExpr() instanceof ASTVariable) {
            node.setExpr(node.getScope().getActualVariable((ASTVariable) node.getExpr()));
        }
        else {
            node.getExpr().accept(this);
        }

        // declare the new variable in the scope
        node.getVariable().accept(this);
        node.getVariable().setType(new UnknownType());
        node.getVariable().setAssignable(true);
        node.getScope().declareVariable(node.getVariable());

        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        node.setScope(currentScope);
        Scope parentScope = currentScope;

        // for loops open their own scope because they declare the iteration variable
        currentScope = new Scope(parentScope);

        // declare the new iteration variable in the scope
        node.getVariable().accept(this);
        node.getVariable().setType(PrimitiveTypes.MYUN_INT);
        node.getVariable().setAssignable(false);
        node.getVariable().getScope().declareVariable(node.getVariable());

        // replace from and to by the original variable, if needed
        if (node.getFrom() instanceof ASTVariable) {
            node.setFrom(node.getScope().getActualVariable((ASTVariable) node.getFrom()));
        }
        else {
            node.getFrom().accept(this);
        }
        if (node.getTo() instanceof ASTVariable) {
            node.setTo(node.getScope().getActualVariable((ASTVariable) node.getTo()));
        }
        else {
            node.getTo().accept(this);
        }

        node.getBlock().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTFuncCall node) {
        node.setScope(currentScope);

        for (int i = 0; i < node.getArgs().size(); i++) {
            // replace the variables by original variables, if needed
            ASTExpression arg = node.getArgs().get(i);
            if (arg instanceof ASTVariable) {
                node.setArg(i, node.getScope().getActualVariable((ASTVariable) arg));
            }
            else {
                arg.accept(this);
            }
        }

        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        node.setScope(currentScope);
        Scope parentScope = currentScope;

        // function definitions have their own scope
        // so that parameter variables do not interfere with other function parameters
        currentScope = new Scope(parentScope);

        // make the parameters known to the scope with their annotated type
        node.getParameters().forEach(param -> {
            param.accept(this);
            param.setAssignable(false);
            param.getScope().declareVariable(param);
        });
        node.getBlock().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        node.setScope(currentScope);

        if (node.getExpr() instanceof ASTVariable) {
            node.setExpr(node.getScope().getActualVariable((ASTVariable) node.getExpr()));
        }
        else {
            node.getExpr().accept(this);
        }
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
        Scope parentScope = currentScope;

        // similarly to functions, scripts have their own scope too
        currentScope = new Scope(parentScope);
        node.getBlock().accept(this);

        currentScope = parentScope;
        return null;
    }

    @Override
    public Void visit(ASTProcCall node) {
        node.setScope(currentScope);
        node.getFuncCall().accept(this);
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

        if (node.getCondition() instanceof ASTVariable) {
            node.setCondition(node.getScope().getActualVariable((ASTVariable) node.getCondition()));
        }
        else {
            node.getCondition().accept(this);
        }

        node.getBlock().accept(this);
        return null;
    }
}
