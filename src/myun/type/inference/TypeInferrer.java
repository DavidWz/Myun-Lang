package myun.type.inference;

import myun.AST.*;
import myun.NotImplementedException;
import myun.scope.IllegalRedefineException;
import myun.scope.UndeclaredVariableUsedException;
import myun.scope.VariableInfo;
import myun.type.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Infers types of expressions and functions.
 */
public class TypeInferrer implements ASTVisitor<Void> {
    /**
     * Infers and sets the types in the given AST node.
     *
     * @param node the AST node
     */
    public void inferTypes(ASTNode node) {
        node.accept(this);
    }

    /**
     * Converts the name of a java type to the name of the corresponding myun type.
     * @param javaName the name of the java type
     * @return the name of the myun type
     */
    private static String javaTypeToMyunType(String javaName) {
        switch (javaName) {
            case "Integer":
                return PrimitiveTypes.MYUN_INT;
            case "Float":
                return PrimitiveTypes.MYUN_FLOAT;
            case "Boolean":
                return PrimitiveTypes.MYUN_BOOL;
            default:
                throw new ParserException("Type \"" + javaName + "\" unknown", new SourcePosition());
        }
    }

    /**
     * Checks if all return statements in a function have the same type as the return type of the function.
     * @param funcDef the function definition to be checked
     */
    private static void checkReturnTypes(ASTFuncDef funcDef) {
        funcDef.accept(new ReturnTypeChecker(funcDef));
    }

    /**
     * Ensures that the type of the given expression is fully known.
     * Throws an exception otherwise.
     *
     * @param expr the expression
     */
    private static void ensureFullyKnownType(ASTExpression expr) {
        if (!expr.getType().isFullyKnown()) {
            throw new CouldNotInferTypeException(expr);
        }
    }

    @Override
    public Void visit(ASTAssignment node) {
        // determine the type of the expression
        node.getExpr().accept(this);
        ensureFullyKnownType(node.getExpr());
        MyunType exprType = node.getExpr().getType();
        ASTVariable var = node.getVariable();

        // check if this variable has been declared already
        if (node.getScope().isDeclared(var)) {
            // make sure it is actually assignable
            if (!node.getScope().getVarInfo(var).isAssignable()) {
                throw new NotAssignableException(node.getSourcePosition());
            }

            // make sure the types match
            MyunType varType = node.getScope().getVarInfo(var).getType();
            if (varType.equals(exprType)) {
                var.setType(varType);
            } else {
                throw new TypeMismatchException(exprType, varType, node.getExpr().getSourcePosition());
            }
        } else {
            throw new UndeclaredVariableUsedException(var);
        }

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
        // make sure the conditions are of type boolean
        node.getConditions().forEach(cond -> {
            cond.accept(this);
            ensureFullyKnownType(cond);
            BasicType boolType = new BasicType(PrimitiveTypes.MYUN_BOOL);
            MyunType condType = cond.getType();
            if (!boolType.equals(condType)) {
                throw new TypeMismatchException(condType, boolType, cond.getSourcePosition());
            }
        });

        // then infer the types in the blocks
        node.getBlocks().forEach(b -> b.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        // first declare every function, so that we can call functions that will be declared after the call
        node.getFuncDefs().forEach(this::declareFunction);

        // then handle the actual definition with body
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));

        // finally, handle the main script
        node.getScript().accept(this);
        return null;
    }

    @Override
    public <CT> Void visit(ASTConstant<CT> node) {
        // convert java class types to Myun type names
        String typeName = javaTypeToMyunType(node.getValue().getClass().getSimpleName());
        node.setType(new BasicType(typeName));
        return null;
    }

    @Override
    public Void visit(ASTDeclaration node) {
        // determine the type of the expression
        node.getExpr().accept(this);
        ensureFullyKnownType(node.getExpr());
        ASTVariable var = node.getVariable();

        // check if this variable has already been declared
        if (var.getScope().isDeclared(var)) {
            // this variable already exists
            throw new IllegalRedefineException(node.getVariable().getName(),
                    node.getScope().getVarInfo(var).getDeclaration().getSourcePosition(),
                    node.getSourcePosition());
        }
        // this variable does not exist yet, so declare it
        node.getScope().declareVariable(node); // it is assignable by default

        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        // make sure the variable is not defined yet
        ASTVariable itVar = node.getVariable();
        if (itVar.getScope().isDeclared(itVar)) {
            throw new IllegalRedefineException(itVar.getName(),
                    itVar.getScope().getVarInfo(itVar).getDeclaration().getSourcePosition(),
                    itVar.getSourcePosition());
        }

        // define that variable in the scope
        BasicType intType = new BasicType(PrimitiveTypes.MYUN_INT);
        itVar.getScope().declareVariable(itVar, new VariableInfo(intType, false, node));

        // make sure from and to expressions are of type int
        node.getFrom().accept(this);
        node.getTo().accept(this);
        ensureFullyKnownType(node.getFrom());
        ensureFullyKnownType(node.getTo());
        MyunType fromType = node.getFrom().getType();
        MyunType toType = node.getTo().getType();

        if (!intType.equals(fromType)) {
            throw new TypeMismatchException(fromType, intType, node.getFrom().getSourcePosition());
        }
        if (!intType.equals(toType)) {
            throw new TypeMismatchException(toType, intType, node.getTo().getSourcePosition());
        }

        // finally, infer the types in the block
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTFuncCall node) {
        node.getArgs().forEach(arg -> arg.accept(this));

        // ask the scope for the type
        List<MyunType> paramTypes = node.getArgs().stream().map(ASTExpression::getType).collect(Collectors.toList());
        FuncHeader funcHeader = new FuncHeader(node.getFunction(), paramTypes);
        MyunType returnType =  node.getScope().getFunctionInfo(funcHeader, node.getSourcePosition()).getType().getReturnType();
        node.setType(returnType);

        return null;
    }

    /**
     * Declares the function and makes its type known to the global scope.
     * @param node the function definition
     */
    private void declareFunction(ASTFuncDef node) {
        // we cannot infer the return type of the function yet
        if (!node.getReturnType().isFullyKnown()) {
            throw new NotImplementedException("return type inference", node.getSourcePosition());
        }

        // make the parameters known to the scope with their annotated type
        node.getParameters().forEach(param -> {
            ensureFullyKnownType(param);
            VariableInfo varInfo = new VariableInfo(param.getType(), false, param);
            param.getScope().declareVariable(param, varInfo);
        });

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
    public Void visit(ASTFuncDef node) {
        // the function header has been defined already in the compilation unit
        // so that we can support out-of-order function definitions

        // do type inference in the function body
        node.getBlock().accept(this);

        // check if all the return expression types match the actual return type
        checkReturnTypes(node);

        return null;
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        node.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTLoopBreak node) {
        return null;
    }

    @Override
    public Void visit(ASTScript node) {
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTProcCall node) {
        node.getFuncCall().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTVariable node) {
        // ask the scope for the type
        node.setType(node.getScope().getVarInfo(node).getType());
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        node.getCondition().accept(this);
        node.getBlock().accept(this);
        return null;
    }
}
