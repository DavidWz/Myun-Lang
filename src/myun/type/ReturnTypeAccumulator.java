package myun.type;

import myun.AST.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Accumulates all types of return expressions inside a function definition.
 */
public class ReturnTypeAccumulator implements ASTVisitor<ASTType> {
    public ASTType getReturnExpressionType(ASTFuncDef funcDef) {
        return funcDef.accept(this);
    }

    private void ensureAllEquals(List<ASTType> types) {
        if (types.size() > 0) {
            for (ASTType type : types) {
                if (!type.equals(types.get(0))) {
                    throw new TypeMismatchException(types.get(0), type);
                }
            }
        }
    }

    @Override
    public ASTType visit(ASTAssignment node) {
        return null;
    }

    @Override
    public ASTType visit(ASTBasicType node) {
        return null;
    }

    @Override
    public ASTType visit(ASTBlock node) {
        List<ASTType> returnTypes = node.getStatements().stream().map(stmt -> stmt.accept(this)).collect(Collectors.toList());
        returnTypes.removeIf(type -> type == null);
        node.getFuncReturn().ifPresent(fR -> returnTypes.add(fR.accept(this)));

        // all return types must be equal
        if (returnTypes.size() > 0) {
            ensureAllEquals(returnTypes);
            return returnTypes.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public ASTType visit(ASTBoolean node) {
        return null;
    }

    @Override
    public ASTType visit(ASTBranch node) {
        List<ASTType> returnTypes = node.getBlocks().stream().map(block -> block.accept(this)).collect(Collectors.toList());
        returnTypes.removeIf(type -> type == null);
        if (returnTypes.size() > 0) {
            ensureAllEquals(returnTypes);
            return returnTypes.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public ASTType visit(ASTCompileUnit node) {
        return null;
    }

    @Override
    public ASTType visit(ASTFloat node) {
        return null;
    }

    @Override
    public ASTType visit(ASTForLoop node) {
        return node.getBlock().accept(this);
    }

    @Override
    public ASTType visit(ASTFuncCall node) {
        return null;
    }

    @Override
    public ASTType visit(ASTFuncDef node) {
        return node.getBlock().accept(this);
    }

    @Override
    public ASTType visit(ASTFuncReturn node) {
        return node.getExpr().getType().orElseThrow(() -> new CouldNotInferTypeException(node.getExpr()));
    }

    @Override
    public ASTType visit(ASTFuncType node) {
        return null;
    }

    @Override
    public ASTType visit(ASTInteger node) {
        return null;
    }

    @Override
    public ASTType visit(ASTLoopBreak node) {
        return null;
    }

    @Override
    public ASTType visit(ASTScript node) {
        return null;
    }

    @Override
    public ASTType visit(ASTVariable node) {
        return null;
    }

    @Override
    public ASTType visit(ASTWhileLoop node) {
        return node.getBlock().accept(this);
    }
}
