package myun.AST.constraints;

/**
 * Exception for violating context-sensitive constraints on the AST structure.
 */
public class ViolatedConstraintException extends Exception {
    private int lineNumber;
    private int charPositionInLine;

    public ViolatedConstraintException(String message, int lineNumber, int charPositionInLine) {
        super(message);
        this.lineNumber = lineNumber;
        this.charPositionInLine = charPositionInLine;
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error on line ").append(lineNumber);
        errorMsg.append(" at ").append(charPositionInLine).append(": ");
        errorMsg.append(super.getMessage());
        return errorMsg.toString();
    }
}
