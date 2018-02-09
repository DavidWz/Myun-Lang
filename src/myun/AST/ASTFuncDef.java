package myun.AST;

import java.util.List;

/**
 * Represents a function definition.
 */
public class ASTFuncDef extends ASTNode {
    private String name;
    private List<ASTVariable> parameters;
    private ASTBlock block;

    /**
     * Creates a new AST function definition.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param name               The name of the defined function
     * @param params             The parameters of the function
     * @param block              The function body
     */
    public ASTFuncDef(int lineNumber, int charPositionInLine, String name, List<ASTVariable> params, ASTBlock block) {
        super(lineNumber, charPositionInLine);
        this.name = name;
        this.parameters = params;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public List<ASTVariable> getParameters() {
        return parameters;
    }

    public ASTBlock getBlock() {
        return block;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}