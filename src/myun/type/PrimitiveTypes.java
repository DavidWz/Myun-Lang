package myun.type;

/**
 * Library of primitive types.
 */
public final class PrimitiveTypes {
    private PrimitiveTypes() {
    }

    public static final String MYUN_BOOL = "Bool";
    public static final String MYUN_INT = "Int";
    public static final String MYUN_FLOAT = "Float";

    // we assume 64-bit architecture
    public static final String LLVM_BOOL = "i1";
    public static final String LLVM_INT = "i64";
    public static final String LLVM_FLOAT = "double";
}
