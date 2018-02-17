package myun;

import myun.AST.SourcePosition;

/**
 * Thrown when a feature is not implemented yet.
 */
public class NotImplementedException extends MyunException {
    private final String featureName;

    public NotImplementedException(String featureName, SourcePosition sourcePosition) {
        super(sourcePosition);
        this.featureName = featureName;
    }

    @Override
    public String getMessage() {
        return "Not implemented feature " + featureName + " used on " + sourcePosition;
    }
}
