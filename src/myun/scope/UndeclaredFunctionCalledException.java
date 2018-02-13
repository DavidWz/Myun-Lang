package myun.scope;

import myun.AST.ASTFuncCall;
import myun.AST.ASTType;
import myun.AST.MyunPrettyPrinter;

import java.util.List;

/**
 * Thrown when an unknown function is called.
 */
public class UndeclaredFunctionCalledException extends RuntimeException {
    private ASTFuncCall funcCall;
    private List<ASTType> paramTypes;
    private MyunPrettyPrinter prettyPrinter;

    public UndeclaredFunctionCalledException(ASTFuncCall funcCall, List<ASTType> paramTypes) {
        this.funcCall = funcCall;
        this.paramTypes = paramTypes;
        this.prettyPrinter = new MyunPrettyPrinter();
    }

    @Override
    public String getMessage() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Error: Undeclared function ").append(funcCall.getFunction().getName());
        errorMsg.append("(");
        for (int i = 0; i < paramTypes.size(); i++) {
            errorMsg.append(paramTypes.get(0).accept(prettyPrinter));
            if (i < paramTypes.size() - 1) {
                errorMsg.append(", ");
            }
        }
        errorMsg.append(")");
        errorMsg.append(" called on line ").append(funcCall.getLine()).append(" at ").append(funcCall
                .getCharPositionInLine());
        return errorMsg.toString();
    }
}
