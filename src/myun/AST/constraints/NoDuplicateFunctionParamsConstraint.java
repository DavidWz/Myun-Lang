package myun.AST.constraints;

import myun.AST.*;

import java.util.Optional;

/**
 * Makes sure that in a function declaration there are no two parameters with the same variable name.
 */
class NoDuplicateFunctionParamsConstraint implements Constraint, ASTVisitor<Optional<ASTVariable>> {
    @Override
    public void check(ASTCompileUnit compileUnit) {
        Optional<ASTVariable> duplicate = compileUnit.accept(this);

        if (duplicate.isPresent()) {
            throw new DuplicateParametersException(duplicate.get());
        }
    }

    @Override
    public Optional<ASTVariable> visit(ASTAssignment node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTBlock node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTBranch node) {
        return Optional.empty();
    }

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
    public <CT> Optional<ASTVariable> visit(ASTConstant<CT> node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTDeclaration node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTForLoop node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTFuncCall node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTFuncDef node) {
        for (int i = 0; i < node.getParameters().size(); i++) {
            for (int j = i + 1; j < node.getParameters().size(); j++) {
                if (node.getParameters().get(i).getName().equals(node.getParameters().get(j).getName())) {
                    return Optional.of(node.getParameters().get(j));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTFuncReturn node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTLoopBreak node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTScript node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTProcCall node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTVariable node) {
        return Optional.empty();
    }

    @Override
    public Optional<ASTVariable> visit(ASTWhileLoop node) {
        return Optional.empty();
    }
}
