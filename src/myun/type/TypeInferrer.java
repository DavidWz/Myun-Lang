package myun.type;

import myun.AST.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infers types of expressions and functions.
 */
public class TypeInferrer /*implements ASTVisitor<Boolean>*/ {
    // storing the two types that mismatch (in case there is a mismatch)
    private ASTType mismatchFirst;
    private ASTType mismatchSecond;

    public TypeInferrer() {
        this.mismatchFirst = null;
        this.mismatchSecond = null;
    }

    /**
     * Tries to infer the types in the given AST structure.
     * Throws an exception if it finds type errors.
     *
     * @param node the ast node
     * @throws TypeMismatchException thrown when two types mismatch
     */
    public void inferTypes(ASTNode node) throws TypeMismatchException {
        this.mismatchFirst = null;
        this.mismatchSecond = null;

        /*if(!node.accept(this)) {
            throw new TypeMismatchException(mismatchFirst, mismatchSecond);
        }*/
    }

    /**
     * Tries to set the given new type for to the given expression.
     * If the expression does not have a type yet, it will simply be set.
     * If it does have a type already, it will only be set when the types match.
     * Otherwise and exception is thrown.
     *
     * @param expr the expression
     * @param type the new type
     * @return true iff the update was successful
     */
    private boolean tryUpdateType(ASTExpression expr, ASTType type)  {
        if (expr.getType().isPresent()) {
            // since we do not have inheritance, we can simply check for equality
            if (expr.getType().get().equals(type)) {
                return true;
            }
            else {
                this.mismatchFirst = expr.getType().get();
                this.mismatchSecond = type;
                return false;
            }
        }
        else {
            expr.setType(type);
            return true;
        }
    }

    /*@Override
    public Boolean visit(ASTAssignment node) {
        // first infer the type of the expression
        if(!node.getExpr().accept(this)) {
            return false;
        }
        ASTType exprType = node.getExpr().getType().get();

        // if this is a new variable, register it at the current scope
        if (!node.getVariable().getType().isPresent()) {
            node.getScope().setType(node.getVariable(), exprType);
            tryUpdateType(node.getVariable(), exprType);
        }
        // variable already exists, the value will be overwritten
        else {

        }


        // then try to update the type of the assigned variable
        if (exprType.isPresent() && tryUpdateType(node.getVariable(), exprType.get())) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Boolean visit(ASTBasicType node) {
        return true;
    }

    @Override
    public Boolean visit(ASTBlock node) {
        return node.getStatements().stream().allMatch(stmt -> stmt.accept(this));
    }

    @Override
    public Boolean visit(ASTBoolean node) {
        ASTBasicType type = new ASTBasicType(node.getLine(), node.getCharPositionInLine(), PrimitiveTypes.BOOL);
        return tryUpdateType(node, type);
    }

    @Override
    public Boolean visit(ASTBranch node) {
        // infer types of expressions and check if they match with the Bool type
        boolean areConditionsBool = node.getConditions().stream().allMatch(condition -> {
           if (condition.accept(this)) {
               return tryUpdateType(condition,
                       new ASTBasicType(condition.getLine(), condition.getCharPositionInLine(), PrimitiveTypes.BOOL));
           }
           else {
               return false;
           }
        });

        if (!areConditionsBool) {
            return false;
        }

        // next, let the inferrer work on the blocks
        return node.getBlocks().stream().allMatch(block -> block.accept(this));
    }

    @Override
    public Boolean visit(ASTCompileUnit node) {
        if(node.getFuncDefs().stream().allMatch(funcDef -> funcDef.accept(this))) {
            return node.getScript().accept(this);
        }
        else {
            return false;
        }
    }

    @Override
    public Boolean visit(ASTFloat node) {
        ASTBasicType type = new ASTBasicType(node.getLine(), node.getCharPositionInLine(), PrimitiveTypes.FLOAT);
        return tryUpdateType(node, type);
    }

    @Override
    public Boolean visit(ASTForLoop node) {
        // infer types of the from and to expressions
        if (!node.getFrom().accept(this) || !node.getTo().accept(this)) {
            return false;
        }

        // match them with the integer type
        if (!tryUpdateType(node.getFrom(), new ASTBasicType(node.getFrom().getLine(), node.getFrom().getCharPositionInLine(), PrimitiveTypes.INT))) {
            return false;
        }

        if (!tryUpdateType(node.getTo(), new ASTBasicType(node.getTo().getLine(), node.getTo().getCharPositionInLine(), PrimitiveTypes.INT))) {
            return false;
        }

        // match the variable with the integer type
        ASTVariable var = node.getVariable();
        if (!var.accept(this)) {
            return false;
        }
        if (!tryUpdateType(var, new ASTBasicType(var.getLine(), var.getCharPositionInLine(), PrimitiveTypes.INT))) {
            return false;
        }

        // infer the types in the block
        return node.getBlock().accept(this);
    }

    @Override
    public Boolean visit(ASTFuncCall node) {
        // infer the types of the arguments
        if (node.getArgs().stream().anyMatch(arg -> !arg.accept(this))) {
            return false;
        }

        // try to find this function and determine the result type
        List<ASTType> argTypes = node.getArgs().stream().map(arg -> arg.getType().get()).collect(Collectors.toList());
        Optional<ASTType> resultType = node.getScope().getType(node.getFunction(), argTypes);
        return (resultType.isPresent() && tryUpdateType(node, resultType.get()));
    }

    @Override
    public Boolean visit(ASTFuncDef node) {
        // TODO
        return null;
    }

    @Override
    public Boolean visit(ASTFuncReturn node) {
        return node.getExpr().accept(this);
    }

    @Override
    public Boolean visit(ASTFuncType node) {
        return true;
    }

    @Override
    public Boolean visit(ASTInteger node) {
        ASTBasicType type = new ASTBasicType(node.getLine(), node.getCharPositionInLine(), PrimitiveTypes.INT);
        return tryUpdateType(node, type);
    }

    @Override
    public Boolean visit(ASTLoopBreak node) {
        return true;
    }

    @Override
    public Boolean visit(ASTScript node) {
        return node.getBlock().accept(this);
    }

    @Override
    public Boolean visit(ASTVariable node) {
        Optional<ASTType> type = node.getScope().getType(node);
        return (type.isPresent() && tryUpdateType(node, type.get()));
    }

    @Override
    public Boolean visit(ASTWhileLoop node) {
        ASTExpression cond = node.getCondition();

        // infer the type of the expression
        if (!cond.accept(this)) {
            return false;
        }

        // and make sure it is a boolean
        if (!tryUpdateType(cond, new ASTBasicType(cond.getLine(), cond.getCharPositionInLine(), PrimitiveTypes.BOOL))) {
            return false;
        }

        // infer the types in the block
        return node.getBlock().accept(this);
    }*/
}
