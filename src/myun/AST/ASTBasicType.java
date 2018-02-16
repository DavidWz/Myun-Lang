package myun.AST;

/**
 * Represents a basic/primitive type in Myun.
 */
public class ASTBasicType extends ASTType {
    private final String name;

    /**
     * Creates a new AST type.
     *
     * @param lineNumber         The line in the source code where this node starts
     * @param charPositionInLine The character position of this node on its line
     * @param name               The name of this type

     */
    public ASTBasicType(int lineNumber, int charPositionInLine, String name) {
        super(lineNumber, charPositionInLine);
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
        if ((null == o) || (getClass() != o.getClass())) {
            return false;
        }

        ASTBasicType that = (ASTBasicType) o;

        return getName().equals(that.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
