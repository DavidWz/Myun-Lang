package myun.type;

import myun.AST.*;
import myun.AST.FuncHeader;
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

    /**
     * Infers and sets the types in the given ast node.
     *
     * @param node the ast node
     * @throws CouldNotInferTypeException thrown when a type could not be inferred
     * @throws TypeMismatchException      thrown when two types mismatch
     */
    public void inferTypes(ASTNode node) throws CouldNotInferTypeException, TypeMismatchException {
        node.accept(this);
    }

    @Override
    public Void visit(ASTAssignment node) {
        // determine the type of the expression
        node.getExpr().accept(this);
        ASTType exprType = node.getExpr().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getExpr()));
        ASTVariable var = node.getVariable();

        // we need to differentiate between variable declaration and assignment
        if (node.getScope().containsVariable(var)) {
            // this variable already exists

            // make sure it is actually assignable
            if (!node.getScope().isAssignable(var)) {
                throw new NotAssignableException(node);
            }

            // make sure the types match
            ASTType varType = node.getScope().getVariableType(var).orElseThrow(() -> new
                    CouldNotInferTypeException(var));
            if (varType.equals(exprType)) {
                var.setType(varType);
            } else {
                throw new TypeMismatchException(exprType, varType, node.getExpr());
            }
        } else {
            // this variable does not exist yet, so create it
            node.getScope().declareVariable(var, exprType, true);
            var.setType(exprType);
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
            ASTBasicType boolType = new ASTBasicType(cond.getLine(),
                    cond.getCharPositionInLine(),
                    PrimitiveTypes.MYUN_BOOL);
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
        // convert java class types to Myun type names
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
        ASTVariable itVar = node.getVariable();
        if (itVar.getScope().containsVariable(itVar)) {
            ASTVariable originalVar = itVar.getScope().getFirstDeclaredVariable(itVar).
                    orElseThrow(() -> new RuntimeException("Scope returned empty optional even though " +
                            "containsVariable method returned true."));
            throw new IllegalRedefineException(itVar.getName(), originalVar, itVar);
        }

        // define that variable in the scope
        ASTBasicType intType = new ASTBasicType(itVar.getLine(),
                itVar.getCharPositionInLine(),
                PrimitiveTypes.MYUN_INT);
        itVar.setType(intType);
        itVar.getScope().declareVariable(itVar, intType, false);

        // make sure from and to expressions are of type int
        node.getFrom().accept(this);
        node.getTo().accept(this);
        ASTType fromType = node.getFrom().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getFrom()));
        ASTType toType = node.getTo().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getTo()));

        ASTBasicType fromIntType = new ASTBasicType(node.getFrom().getLine(), node.getFrom().getCharPositionInLine(),
                PrimitiveTypes.MYUN_INT);
        if (!fromIntType.equals(fromType)) {
            throw new TypeMismatchException(fromType, fromIntType, node.getFrom());
        }

        ASTBasicType toIntType = new ASTBasicType(node.getTo().getLine(), node.getTo().getCharPositionInLine(),
                PrimitiveTypes.MYUN_INT);
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
        ASTType returnType = node.getScope().getReturnType(node.getFunction(), paramTypes).
                orElseThrow(() -> new UndeclaredFunctionCalledException(node, paramTypes));
        node.setType(returnType);

        return null;
    }

    @Override
    public Void visit(ASTFuncDef node) {
        // make the parameters known to the scope with their annotated type
        node.getParameters().forEach(param -> param.getScope().declareVariable(param,
                param.getType().orElseThrow(() -> new RuntimeException("Type inference is not supported yet!")),
                false));

        // retrieve the type of this function
        ASTFuncType funcType = new ASTFuncType(node.getLine(), node.getCharPositionInLine(),
                node.getParameters().stream().map(param -> param.getType().
                        orElseThrow(() -> new CouldNotInferTypeException(param))).collect(Collectors.toList()),
                node.getReturnType().orElseThrow(() -> new RuntimeException("Type inference is not supported yet!")));
        FuncHeader funcHeader = new FuncHeader(node.getName(), funcType);

        // make sure this function has not been declared yet
        if (node.getScope().containsFunction(funcHeader)) {
            throw new IllegalRedefineException(node.getName(), node.getScope().getFirstDeclaredFunction(funcHeader)
                    .orElseThrow(() -> new RuntimeException("Scope returned empty Optional even though " +
                            "containsFunction returned true.")), node);
        }

        // declare this function
        node.getScope().declareFunction(funcHeader, node);

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
        node.setType(node.getScope().getVariableType(node).
                orElseThrow(() -> new UndeclaredVariableUsedException(node)));
        return null;
    }

    @Override
    public Void visit(ASTWhileLoop node) {
        node.getCondition().accept(this);
        node.getBlock().accept(this);
        return null;
    }
}
