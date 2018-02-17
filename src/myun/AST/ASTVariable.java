package myun.AST;

/**
 * Represents a variable.
 */
public class ASTVariable extends ASTExpression {
    private final String name;

    /**
     * Creates a new AST variable.
     *
     * @param sourcePos the position of this node in the source code
     * @param name               The name of this variable
     */
    public ASTVariable(SourcePosition sourcePos, String name) {
        super(sourcePos);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        ASTVariable that = (ASTVariable) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
