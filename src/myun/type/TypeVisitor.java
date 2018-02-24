package myun.type;

/**
 * Visitor for myun types.
 * @noinspection UnusedParameters, SameReturnValue
 */
public interface TypeVisitor<T> {
    T visit(BasicType type);
    T visit(FuncType type);
    T visit(UnknownType type);
    T visit(VariantType type);
}
