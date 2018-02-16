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
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param name               The name of the script
     * @param block              The code of this script
     */
    public ASTScript(int lineNumber, int charPositionInLine, String name, ASTBlock block) {
        super(lineNumber, charPositionInLine);
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
}
