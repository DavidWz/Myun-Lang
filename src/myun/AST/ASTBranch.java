package myun.AST;

import java.util.List;
import java.util.Optional;

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
     * @param sourcePos the position of this node in the source code
     * @param conditions         A list of each if/elseif condition
     * @param blocks             A list of each if/elseif/else block
     */
    public ASTBranch(SourcePosition sourcePos, List<ASTExpression> conditions, List<ASTBlock> blocks) {
        super(sourcePos);
        this.conditions = conditions;
        this.blocks = blocks;
    }

    public Optional<ASTBlock> getElseBlock() {
        if (conditions.size() != blocks.size()) {
            return Optional.of(blocks.get(blocks.size() - 1));
        }
        else {
            return Optional.empty();
        }
    }

    public List<ASTExpression> getConditions() {
        return conditions;
    }

    public void setCondition(int i, ASTExpression condition) {
        conditions.set(i, condition);
    }

    public List<ASTBlock> getBlocks() {
        return blocks;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(ASTNonExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
