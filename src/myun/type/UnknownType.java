package myun.type;

/**
 * Represents an unknown type which is yet to be determined.
 */
public class UnknownType implements MyunType {
    @Override
    public boolean isFullyKnown() {
        return false;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
