package myun.AST.constraints;

/**
 * Exception for violating context-sensitive constraints on the AST structure.
 */
public class ViolatedConstraintException extends Exception {
    private int lineNumber;
    private int charPositionInLine;

    ViolatedConstraintException(String message, int lineNumber, int charPositionInLine) {
        super(message);
        this.lineNumber = lineNumber;
        this.charPositionInLine = charPositionInLine;
    }

    @Override
    public String getMessage() {
        return "Error on line " + lineNumber +
                " at " + charPositionInLine + ": " +
                super.getMessage();
    }
}
