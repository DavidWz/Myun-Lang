package myun.type;

import myun.AST.ASTNode;
import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Exception thrown when two types mismatch.
 */
public class TypeMismatchException extends MyunException {
    private ASTType actual;
    private ASTType expected;
    private ASTNode reason;
    private MyunPrettyPrinter prettyPrinter;

    TypeMismatchException(ASTType actual, ASTType expected, ASTNode reason) {
        super();
        this.actual = actual;
        this.expected = expected;
        this.reason = reason;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Type Error: Types Mismatch.\n" +
                "Expected " + expected.accept(prettyPrinter) +
                " but got " + actual.accept(prettyPrinter) +
                " on line " + reason.getLine() + " at " + reason.getCharPositionInLine();
    }
}
