package myun.scope;

import myun.type.FuncHeader;
import myun.AST.MyunPrettyPrinter;
import myun.AST.SourcePosition;
import myun.MyunException;

/**
 * Thrown when an unknown function is called.
 */
class UndeclaredFunctionCalledException extends MyunException {
    private final FuncHeader funcHeader;
    private final MyunPrettyPrinter prettyPrinter;

    UndeclaredFunctionCalledException(FuncHeader funcHeader, SourcePosition sourcePosition) {
        super(sourcePosition);
        this.funcHeader = funcHeader;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error: Undeclared function ").append(funcHeader.getName()).append(" of type ");
        errorMsg.append('(');
        for (int i = 0; i < funcHeader.getParameterTypes().size(); i++) {
            errorMsg.append(funcHeader.getParameterTypes().get(0).accept(prettyPrinter));
            if (i < (funcHeader.getParameterTypes().size() - 1)) {
                errorMsg.append(", ");
            }
        }
        errorMsg.append(')');
        errorMsg.append(" called on ").append(sourcePosition);
        return errorMsg.toString();
    }
}
