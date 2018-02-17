package myun.type.inference;

import myun.AST.MyunPrettyPrinter;
import myun.AST.SourcePosition;
import myun.MyunException;
import myun.type.MyunType;

/**
 * Exception thrown when two types mismatch.
 */
class TypeMismatchException extends MyunException {
    private final MyunType actual;
    private final MyunType expected;
    private final MyunPrettyPrinter prettyPrinter;

    TypeMismatchException(MyunType actual, MyunType expected, SourcePosition sourcePosition) {
        super(sourcePosition);
        this.actual = actual;
        this.expected = expected;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        return "Type Error: Types Mismatch.\n" +
                "Expected " + expected.accept(prettyPrinter) +
                " but got " + actual.accept(prettyPrinter) +
                " on " + sourcePosition;
    }
}
