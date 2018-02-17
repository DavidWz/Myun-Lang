package myun.AST;

/**
 * Wraps the position of some AST node in the source code.
 */
public class SourcePosition {
    private final int line;
    private final int position;

    /**
     * Declares an unknown position.
     */
    public SourcePosition() {
        line = -1;
        position = -1;
    }

    /**
     * Declares a known position in the source code.
     * @param line the line number of code
     * @param position the character position in that line
     */
    public SourcePosition(int line, int position) {
        this.line = line;
        this.position = position;
    }

    private boolean isKnownPosition() {
        return (line > 0) && (position > 0);
    }

    @Override
    public String toString() {
        if (isKnownPosition()) {
            return "line " + line + " at " + position;
        }
        else {
            return "unknown position";
        }
    }
}
