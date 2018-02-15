package myun.AST;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a function call.
 */
public class ASTFuncCall extends ASTExpression {
    private String function;
    private List<ASTExpression> args;

    /**
     * Creates a new AST function call
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param function           The called function
     * @param args               A list of arguments.
     */
    public ASTFuncCall(int lineNumber, int charPositionInLine, String function, List<ASTExpression> args) {
        super(lineNumber, charPositionInLine);
        this.function = function;
        this.args = args;
    }

    /**
     * Creates a new AST function call
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param function           The called function
     * @param args               A list of arguments.
     */
    public ASTFuncCall(int lineNumber, int charPositionInLine,  String function, ASTExpression... args) {
        super(lineNumber, charPositionInLine);
        this.function = function;
        this.args = Arrays.asList(args);
    }

    public String getFunction() {
        return function;
    }

    public List<ASTExpression> getArgs() {
        return args;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
