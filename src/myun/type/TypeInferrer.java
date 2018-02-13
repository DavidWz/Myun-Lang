package myun.type;

import myun.AST.*;
import myun.scope.IllegalRedefineException;
import myun.scope.UndeclaredFunctionCalledException;
import myun.scope.UndeclaredVariableUsedException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Infers types of expressions and functions.
 */
public class TypeInferrer implements ASTVisitor<Void> {

    public TypeInferrer() {

    }

    public void inferTypes(ASTNode node) throws TypeMismatchException {
        node.accept(this);
    }


    @Override
    public Void visit(ASTAssignment node) {
        // determine the type of the expression
        node.getExpr().accept(this);
        ASTType exprType = node.getExpr().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getExpr()));

        // we need to differentiate between variable declaration and assignment
        if (node.getScope().containsVariable(node.getVariable())) {
            // this variable already exists, so make sure the types match
            ASTType varType = node.getScope().getVariableType(node.getVariable()).orElseThrow(() -> new
                    CouldNotInferTypeException(node.getVariable()));
            if (varType.equals(exprType)) {
                node.getVariable().setType(varType);
            } else {
                throw new TypeMismatchException(exprType, varType, node.getExpr());
            }
        } else {
            // this variable does not exist yet, so create it
            node.getScope().declareVariable(node.getVariable(), exprType);
            node.getVariable().setType(exprType);
        }

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
        // make sure the conditions are of type boolean
        node.getConditions().forEach(cond -> {
            cond.accept(this);
            ASTBasicType boolType = new ASTBasicType(cond.getLine(), cond.getCharPositionInLine(), PrimitiveTypes
                    .MYUN_BOOL);
            ASTType condType = cond.getType().orElseThrow(() -> new CouldNotInferTypeException(cond));
            if (!boolType.equals(condType)) {
                throw new TypeMismatchException(condType, boolType, cond);
            }
        });

        // then infer the types in the blocks
        node.getBlocks().forEach(b -> b.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTCompileUnit node) {
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));
        node.getScript().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTConstant node) {
        String typeName;
        if (node.getValue() instanceof Integer) {
            typeName = PrimitiveTypes.MYUN_INT;
        } else if (node.getValue() instanceof Float) {
            typeName = PrimitiveTypes.MYUN_FLOAT;
        } else if (node.getValue() instanceof Boolean) {
            typeName = PrimitiveTypes.MYUN_BOOL;
        } else {
            throw new RuntimeException("Unknown constant type " + node.getValue());
        }

        ASTBasicType type = new ASTBasicType(node.getLine(), node.getCharPositionInLine(), typeName);
        node.setType(type);
        return null;
    }

    @Override
    public Void visit(ASTForLoop node) {
        // make sure the variable is not defined yet
        if (node.getVariable().getScope().containsVariable(node.getVariable())) {
            ASTVariable originalVar = node.getVariable().getScope().getFirstDeclaredVariable(node.getVariable()).
                    orElseThrow(() -> new RuntimeException("Scope returned empty optional even though " +
                            "containsVariable method returned true."));
            throw new IllegalRedefineException(node.getVariable().getName(), originalVar, node.getVariable());
        }

        // define that variable in the scope
        ASTBasicType varIntType = new ASTBasicType(node.getVariable().getLine(), node.getVariable()
                .getCharPositionInLine(), PrimitiveTypes.MYUN_INT);
        node.getVariable().setType(varIntType);
        node.getVariable().getScope().declareVariable(node.getVariable(), varIntType);

        // make sure from and to expressions are of type int
        node.getFrom().accept(this);
        node.getTo().accept(this);
        ASTType fromType = node.getFrom().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getFrom()));
        ASTType toType = node.getTo().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getTo()));

        ASTBasicType fromIntType = new ASTBasicType(node.getFrom().getLine(), node.getFrom().getCharPositionInLine(),
                PrimitiveTypes.MYUN_INT);
        ASTBasicType toIntType = new ASTBasicType(node.getTo().getLine(), node.getTo().getCharPositionInLine(),
                PrimitiveTypes.MYUN_INT);

        if (!fromIntType.equals(fromType)) {
            throw new TypeMismatchException(fromType, fromIntType, node.getFrom());
        }
        if (!toIntType.equals(toType)) {
            throw new TypeMismatchException(toType, toIntType, node.getTo());
        }

        // finally, infer the types in the block
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTFuncCall node) {
        node.getArgs().forEach(arg -> arg.accept(this));

        // ask the scope for the type
        List<ASTType> paramTypes = node.getArgs().stream().
                map(arg -> arg.getType().orElseThrow(() -> new CouldNotInferTypeException(arg))).
                collect(Collectors.toList());
        ASTType returnType = node.getScope().getReturnType(node.getFunction().getName(), paramTypes).
                orElseThrow(() -> new UndeclaredFunctionCalledException(node, paramTypes));
        node.setType(returnType);

        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        // make the parameter types known to the scope
        node.getParameters().forEach(param -> param.getScope().declareVariable(param, param.getType().orElseThrow(()
                -> new RuntimeException("Type inference is not supported yet!"))));

        // make the function type known to the scope
        if (!node.getReturnType().isPresent()) {
            throw new RuntimeException("Type inference is not supported yet!");
        }
        ASTFuncType funcType = new ASTFuncType(node.getLine(), node.getCharPositionInLine(),
                node.getParameters().stream().map(param -> param.getType().orElseThrow(() -> new RuntimeException
                        ("Type inference is not supported yet!"))).collect(Collectors.toList()),
                node.getReturnType().orElseThrow(() -> new RuntimeException("Type inference is not supported yet!")));

        // we set without check for previously defined function because we want to support redefinition of functions
        node.getScope().declareFunction(node.getName(), funcType);

        // do type inference in the function body
        node.getBlock().accept(this);

        // check if all the return expression types match the actual return type
        ReturnTypeChecker returnTypeChecker = new ReturnTypeChecker();
        returnTypeChecker.checkReturnType(node);

        return null;
    }

    @Override
    public Void visit(ASTFuncReturn node) {
        node.getExpr().accept(this);
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
        node.getBlock().accept(this);
        return null;
    }

    @Override
    public Void visit(ASTVariable node) {
        // ask the scope for the type
        node.setType(node.getScope().getVariableType(node).orElseThrow(() -> new UndeclaredVariableUsedException
                (node)));
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        node.getCondition().accept(this);
        node.getBlock().accept(this);
        return null;
    }
}
