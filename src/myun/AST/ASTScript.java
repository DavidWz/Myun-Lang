package myun.AST;

/**
 * Represents the script part of a compilation unit.
 */
public class ASTScript extends ASTNode {
    private final String name;
    private final ASTBlock block;

    /**
     * Creates a new AST node.
     *
     * @param sourcePos the position of this node in the source code
     * @param name               The name of the script
     * @param block              The code of this script
     */
    public ASTScript(SourcePosition sourcePos, String name, ASTBlock block) {
        super(sourcePos);
        this.name = name;
        this.block = block;
    }

    public String getName() {
        return name;
    }

    public ASTBlock getBlock() {
        return block;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void accept(ASTNonExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
