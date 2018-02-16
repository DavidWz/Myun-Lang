package myun.scope;

import myun.AST.ASTVariable;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Thrown when a variable is used which has not been declared.
 */
public class UndeclaredVariableUsedException extends MyunException {
    private final ASTVariable variable;
    private final MyunPrettyPrinter prettyPrinter;

    public UndeclaredVariableUsedException(ASTVariable variable) {
        super();
        this.variable = variable;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Error: Undeclared variable " + variable.accept(prettyPrinter) +
                " used on line " + variable.getLine() + " at " + variable.getCharPositionInLine();
    }
}