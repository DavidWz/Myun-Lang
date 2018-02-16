package myun.AST;

import java.util.List;

/**
 * Represents a conditional branch.
 */
public class ASTBranch extends ASTStatement {
    private final List<ASTExpression> conditions;
    // there might be one more block than there are conditions
    // the last block then corresponds to the else-branch
    private final List<ASTBlock> blocks;

    /**
     * Creates a new AST branch.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param conditions         A list of each if/elseif condition
     * @param blocks             A list of each if/elseif/else block
     */
    public ASTBranch(int lineNumber, int charPositionInLine, List<ASTExpression> conditions, List<ASTBlock> blocks) {
        super(lineNumber, charPositionInLine);
        this.conditions = conditions;
        this.blocks = blocks;
    }

    public boolean hasElse() {
        return conditions.size() != blocks.size();
    }

    public ASTBlock getElseBlock() {
        return blocks.get(blocks.size()-1);
    }

    public List<ASTExpression> getConditions() {
        return conditions;
    }

    public List<ASTBlock> getBlocks() {
        return blocks;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
