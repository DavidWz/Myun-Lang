package myun.type;

import myun.AST.ASTNode;
import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;

/**
 * Exception thrown when two types mismatch.
 */
public class TypeMismatchException extends RuntimeException {
    private ASTType actual;
    private ASTType expected;
    private ASTNode reason;
    private MyunPrettyPrinter prettyPrinter;

    public TypeMismatchException(ASTType actual, ASTType expected, ASTNode reason) {
        super();
        this.actual = actual;
        this.expected = expected;
        this.reason = reason;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Type Error: Types Mismatch.\n");
        errorMsg.append("Expected ").append(expected.accept(prettyPrinter));
        errorMsg.append(" but got ").append(actual.accept(prettyPrinter));
        errorMsg.append(" on line ").append(reason.getLine()).append(" at ").append(reason.getCharPositionInLine());
        return errorMsg.toString();
    }
}
