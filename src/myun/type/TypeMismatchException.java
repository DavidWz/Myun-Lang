package myun.type;

import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;

/**
 * Exception thrown when two types mismatch.
 */
public class TypeMismatchException extends RuntimeException {
    private ASTType actual;
    private ASTType expected;
    private MyunPrettyPrinter prettyPrinter;

    public TypeMismatchException(ASTType actual, ASTType expected) {
        super();
        this.actual = actual;
        this.expected = expected;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Type Error: Types Mismatch.\n");
        errorMsg.append("Expected ").append(expected.accept(prettyPrinter));
        errorMsg.append(" but got ").append(actual.accept(prettyPrinter));
        errorMsg.append(" on line ").append(actual.getLine()).append(" at ").append(actual.getCharPositionInLine());
        return errorMsg.toString();
    }
}
