package myun.AST.constraints;

import myun.AST.ASTCompileUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks context-sensitive constraints on the AST structure.
 */
public class ConstraintChecker {
    private List<Constraint> constraints;

    public ConstraintChecker() {
        constraints = new ArrayList<>();
        constraints.add(new NoDuplicateFunctionParamsConstraint());
        constraints.add(new FunctionHasReturnConstraint());
    }

    public void check(ASTCompileUnit compileUnit) throws ViolatedConstraintException {
        for (Constraint constraint : constraints) {
            constraint.check(compileUnit);
        }
    }
}
