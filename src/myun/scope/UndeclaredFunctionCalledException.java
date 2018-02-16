package myun.scope;

import myun.AST.ASTNode;
import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;
import myun.MyunException;

import java.util.List;

/**
 * Thrown when an unknown function is called.
 */
class UndeclaredFunctionCalledException extends MyunException {
    private final ASTNode source;
    private final List<ASTType> paramTypes;
    private final MyunPrettyPrinter prettyPrinter;

    UndeclaredFunctionCalledException(ASTNode source, List<ASTType> paramTypes) {
        super();
        this.source = source;
        this.paramTypes = paramTypes;
        prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error: Undeclared function of type ");
        errorMsg.append("(");
        for (int i = 0; i < paramTypes.size(); i++) {
            errorMsg.append(paramTypes.get(0).accept(prettyPrinter));
            if (i < (paramTypes.size() - 1)) {
                errorMsg.append(", ");
            }
        }
        errorMsg.append(")");
        errorMsg.append(" called on line ").append(source.getLine());
        errorMsg.append(" at ").append(source.getCharPositionInLine());
        return errorMsg.toString();
    }
}
