package myun.AST.constraints;

import myun.AST.ASTCompileUnit;
import myun.MyunException;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks context-sensitive constraints on the AST structure.
 */
public class ConstraintChecker {
    private final List<Constraint> constraints;

    public ConstraintChecker() {
        super();
        constraints = new ArrayList<>();
        constraints.add(new NoDuplicateFunctionParamsConstraint());
        constraints.add(new FunctionHasReturnConstraint());
    }

    public void check(ASTCompileUnit compileUnit) throws MyunException {
        for (Constraint constraint : constraints) {
            constraint.check(compileUnit);
        }
    }
}
