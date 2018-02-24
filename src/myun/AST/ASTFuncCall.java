package myun.AST;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a function call.
 */
public class ASTFuncCall extends ASTExpression {
    private final String function;
    private final List<ASTExpression> args;

    /**
     * Creates a new AST function call
     *
     * @param sourcePos the position of this node in the source code
     * @param function           The called function
     * @param args               A list of arguments.
     */
    public ASTFuncCall(SourcePosition sourcePos, String function, List<ASTExpression> args) {
        super(sourcePos);
        this.function = function;
        this.args = args;
    }

    /**
     * Creates a new AST function call
     *
     * @param sourcePos the position of this node in the source code
     * @param function           The called function
     * @param args               A list of arguments.
     */
    public ASTFuncCall(SourcePosition sourcePos, String function, ASTExpression... args) {
        super(sourcePos);
        this.function = function;
        this.args = Arrays.asList(args);
    }

    public String getFunction() {
        return function;
    }

    public List<ASTExpression> getArgs() {
        return args;
    }

    public void setArg(int i, ASTVariable actualVariable) {
        args.set(i, actualVariable);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(ASTExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
