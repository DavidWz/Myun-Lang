package myun.scope;

import myun.AST.ASTVariable;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Thrown when a variable is used which has not been declared.
 */
class UndeclaredVariableUsedException extends MyunException {
    private final ASTVariable variable;
    private final MyunPrettyPrinter prettyPrinter;

    UndeclaredVariableUsedException(ASTVariable variable) {
        super(variable.getSourcePosition());
        this.variable = variable;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Error: Undeclared variable " + variable.accept(prettyPrinter) +
                " used on " + sourcePosition;
    }
}