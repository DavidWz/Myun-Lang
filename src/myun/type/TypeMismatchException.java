package myun.type;

import myun.AST.ASTNode;
import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

/**
 * Exception thrown when two types mismatch.
 */
class TypeMismatchException extends MyunException {
    private final ASTType actual;
    private final ASTType expected;
    private final ASTNode reason;
    private final MyunPrettyPrinter prettyPrinter;

    TypeMismatchException(ASTType actual, ASTType expected, ASTNode reason) {
        this.actual = actual;
        this.expected = expected;
        this.reason = reason;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Type Error: Types Mismatch.\n" +
                "Expected " + expected.accept(prettyPrinter) +
                " but got " + actual.accept(prettyPrinter) +
                " on line " + reason.getLine() + " at " + reason.getCharPositionInLine();
    }
}
