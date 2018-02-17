package myun.scope;

import myun.AST.SourcePosition;
import myun.MyunException;

/**
 * Thrown when a variable or function is illegally redefined.
 */
public class IllegalRedefineException extends MyunException {
    private final String name;
    private final SourcePosition originalPos;

    public IllegalRedefineException(String name, SourcePosition originalPos, SourcePosition redefinedPos) {
        super(redefinedPos);
        this.name = name;
        this.originalPos = originalPos;
    }

    @Override
    public String getMessage() {
        return "Error: Illegal redefinition of " + name + " on " + sourcePosition + " first defined on " + originalPos;
    }
}
