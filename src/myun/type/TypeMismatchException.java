package myun.type;

import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;

/**
 * Exception thrown when two types mismatch.
 */
public class TypeMismatchException extends Exception {
    private ASTType first;
    private ASTType second;
    private MyunPrettyPrinter prettyPrinter;

    public TypeMismatchException(ASTType first, ASTType second) {
        super();
        this.first = first;
        this.second = second;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Type Error: Types Mismatch.\n");
        errorMsg.append(first.accept(prettyPrinter));
        errorMsg.append(" on line ").append(first.getLine()).append(" at ").append(first.getCharPositionInLine());
        errorMsg.append(" and ").append(second.accept(prettyPrinter));
        errorMsg.append(" on line ").append(second.getLine()).append(" at ").append(second.getCharPositionInLine());
        return errorMsg.toString();
    }
}
