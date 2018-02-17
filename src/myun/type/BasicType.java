package myun.type;

/**
 * Represents a basic/primitive type in Myun.
 */
public class BasicType implements MyunType {
    private final String name;

    /**
     * Creates a new basic myun type.
     *
     * @param name The name of this type
     */
    public BasicType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        BasicType that = (BasicType) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean isFullyKnown() {
        return true;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
