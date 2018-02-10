package myun.AST.constraints;

import myun.AST.*;

import java.util.Optional;

/**
 * Makes sure that in a function declaration there are no two parameters with the same variable name.
 */
public class NoDuplicateFunctionParamsConstraint implements Constraint, ASTVisitor<Optional<ASTVariable>> {
    private static final String ERROR_MSG = "Parameter names in function declarations must be mutually exclusive.";

    public NoDuplicateFunctionParamsConstraint() {
    }

    @Override
    public void check(ASTCompileUnit compileUnit) throws ViolatedConstraintException {
        Optional<ASTVariable> duplicate = compileUnit.accept(this);

        if (duplicate.isPresent()) {
            throw new ViolatedConstraintException(ERROR_MSG, duplicate.get().getLine(), duplicate.get().getCharPositionInLine());
        }
    }

    @Override
    public Optional<ASTVariable> visit(ASTAssignment node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTBasicType node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTBlock node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTBoolean node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTBranch node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTCompileUnit node) {
        for (ASTFuncDef funcDef : node.getFuncDefs()) {
            Optional<ASTVariable> duplicateVar = funcDef.accept(this);
            if (duplicateVar.isPresent()) {
                return duplicateVar;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTFloat node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTForLoop node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTFuncCall node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTFuncDef node) {
        for (int i = 0; i < node.getParameters().size(); i++) {
            for (int j = i+1; j < node.getParameters().size(); j++) {
                if (node.getParameters().get(i).getName().equals(node.getParameters().get(j).getName())) {
                    return Optional.of(node.getParameters().get(j));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTFuncReturn node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTFuncType node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTInteger node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTLoopBreak node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTScript node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTVariable node) { return Optional.empty(); }

    @Override
    public Optional<ASTVariable> visit(ASTWhileLoop node) { return Optional.empty(); }
}
