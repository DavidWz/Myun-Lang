package myun.type;

/**
 * Interface for Myun types.
 */
public interface MyunType {
    /**
     * @return true iff every component of this type is known
     */
    boolean isFullyKnown();

    /**
     * Visits a given AST visitor.
     * @param visitor the AST visitor
     */
    <T> T accept(TypeVisitor<T> visitor);
}
