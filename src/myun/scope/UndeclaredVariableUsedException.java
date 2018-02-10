package myun.scope;

import myun.AST.ASTVariable;
import myun.AST.MyunPrettyPrinter;

/**
 * Thrown when a variable is used which has not been declared.
 */
public class UndeclaredVariableUsedException extends RuntimeException {
    private ASTVariable variable;
    private MyunPrettyPrinter prettyPrinter;

    public UndeclaredVariableUsedException(ASTVariable variable) {
        this.variable = variable;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error: Undeclared variable ").append(variable.accept(prettyPrinter));
        errorMsg.append(" used on line ").append(variable.getLine()).append(" at ").append(variable.getCharPositionInLine());
        return errorMsg.toString();
    }
}