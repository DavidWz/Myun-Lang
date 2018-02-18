package myun.AST;

/**
 * Represents a procedure call.
 */
public class ASTProcCall extends ASTStatement {
    private final ASTFuncCall funcCall;

    /**
     * Creates a new AST procedure call
     *
     * @param funcCall the function call
     */
    public ASTProcCall(ASTFuncCall funcCall) {
        super(funcCall.getSourcePosition());
        this.funcCall = funcCall;
    }

    public ASTFuncCall getFuncCall() {
        return funcCall;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
