package myun.AST.constraints;

import myun.AST.ASTCompileUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks context-sensitive constraints on the AST structure.
 */
public class ConstraintChecker {
    private final List<Constraint> constraints;

    public ConstraintChecker() {
        constraints = new ArrayList<>();
        constraints.add(new NoDuplicateFunctionParamsConstraint());
        constraints.add(new FunctionHasReturnConstraint());
        constraints.add(new FunctionAndScriptDifferentNameConstraint());
        constraints.add(new BreaksOnlyInLoopsConstraint());
    }

    public void check(ASTCompileUnit compileUnit) {
        for (Constraint constraint : constraints) {
            constraint.check(compileUnit);
        }
    }
}
