package myun.type.inference;

import myun.AST.*;
import myun.scope.IllegalRedefineException;
import myun.type.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Infers types of expressions and functions.
 */
public class TypeInferrer implements ASTNonExpressionVisitor {
    private final ExpressionTypeInferrer expressionTypeInferrer;
    private final ReturnTypeInferrer returnTypeInferrer;

    public TypeInferrer() {
        expressionTypeInferrer = new ExpressionTypeInferrer();
        returnTypeInferrer = new ReturnTypeInferrer();
    }

    /**
     * Infers and sets the types in the given AST node.
     *
     * @param node the AST node
     */
    public void inferTypes(ASTCompileUnit node) {
        node.accept(this);
    }

    @Override
    public void visit(ASTAssignment node) {
        ASTVariable var = node.getVariable();

        // make sure the variable is actually assignable
        if (!var.isAssignable()) {
            throw new NotAssignableException(node.getSourcePosition());
        }

        // determine the type of the expression
        MyunType exprType = node.getExpr().accept(expressionTypeInferrer);
        MyunType unifiedType = TypeUnifier.unify(var.getType(), exprType).
                orElseThrow(() -> new TypeMismatchException(exprType, var.getType(), node.getExpr().getSourcePosition()));

        // update the types
        var.setType(unifiedType);
        node.getExpr().setType(unifiedType);
    }

    @Override
    public void visit(ASTBlock node) {
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(fR -> fR.accept(this));
    }

    @Override
    public void visit(ASTBranch node) {
        // make sure the conditions are of type boolean
        node.getConditions().forEach(cond -> {
            MyunType condType = cond.accept(expressionTypeInferrer);
            BasicType boolType = PrimitiveTypes.MYUN_BOOL;
            if (!boolType.equals(condType)) {
                throw new TypeMismatchException(condType, boolType, cond.getSourcePosition());
            }
        });

        // then infer the types in the blocks
        node.getBlocks().forEach(b -> b.accept(this));
    }

    @Override
    public void visit(ASTCompileUnit node) {
        // first declare every function, so that we can call functions that will be declared after the call
        node.getFuncDefs().forEach(this::declareFunction);

        // then handle the actual definition with body
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));

        // finally, handle the main script
        node.getScript().accept(this);
    }

    @Override
    public void visit(ASTDeclaration node) {
        // determine the type of the expression
        MyunType exprType = node.getExpr().accept(expressionTypeInferrer);

        // update the type of the variable
        node.getVariable().setType(exprType);
    }

    @Override
    public void visit(ASTForLoop node) {
        // make sure from and to expressions are of type int
        MyunType fromType = node.getFrom().accept(expressionTypeInferrer);
        MyunType toType = node.getTo().accept(expressionTypeInferrer);

        if (!PrimitiveTypes.MYUN_INT.equals(fromType)) {
            throw new TypeMismatchException(fromType, PrimitiveTypes.MYUN_INT, node.getFrom().getSourcePosition());
        }
        if (!PrimitiveTypes.MYUN_INT.equals(toType)) {
            throw new TypeMismatchException(toType, PrimitiveTypes.MYUN_INT, node.getTo().getSourcePosition());
        }

        // finally, infer the types in the block
        node.getBlock().accept(this);
    }

    /**
     * Declares the function and makes its type known to the global scope.
     * @param node the function definition
     */
    private void declareFunction(ASTFuncDef node) {
        // retrieve the type of this function
        List<MyunType> paramTypes = node.getParameters().stream().map(ASTExpression::getType).collect(Collectors.toList());
        FuncHeader funcHeader = new FuncHeader(node.getName(), paramTypes);

        // make sure this function has not been declared yet
        if (node.getScope().isDeclared(funcHeader)) {
            ASTFuncDef originalDef = node.getScope().getFunctionInfo(funcHeader, node.getSourcePosition()).getFuncDef();
            throw new IllegalRedefineException(node.getName(), originalDef.getSourcePosition(), node.getSourcePosition());
        }

        // declare this function
        node.getScope().declareFunction(funcHeader, node);
    }

    @Override
    public void visit(ASTFuncDef node) {
        // the function header has been defined already in the compilation unit
        // so that we can support out-of-order function definitions

        // do type inference in the function body
        node.getBlock().accept(this);

        // check if all the return expression types match the actual return type
        node.setReturnType(returnTypeInferrer.inferReturnType(node));
    }

    @Override
    public void visit(ASTFuncReturn node) {
        node.getExpr().accept(expressionTypeInferrer);
    }

    @Override
    public void visit(ASTLoopBreak node) {
    }

    @Override
    public void visit(ASTScript node) {
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ASTProcCall node) {
        node.getFuncCall().accept(expressionTypeInferrer);
    }

    @Override
    public void visit(ASTWhileLoop node) {
        MyunType condType = node.getCondition().accept(expressionTypeInferrer);

        // make sure the while condition is of type boolean
        BasicType boolType = PrimitiveTypes.MYUN_BOOL;
        if (!boolType.equals(condType)) {
            throw new TypeMismatchException(condType, boolType, node.getSourcePosition());
        }

        node.getBlock().accept(this);
    }
}
