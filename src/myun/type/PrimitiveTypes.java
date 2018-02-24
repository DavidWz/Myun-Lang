package myun.type;

/**
 * Library of primitive types.
 */
public final class PrimitiveTypes {
    private PrimitiveTypes() {
    }

    public static final String MYUN_BOOL_NAME = "Bool";
    public static final String MYUN_INT_NAME = "Int";
    public static final String MYUN_FLOAT_NAME = "Float";
    public static final BasicType MYUN_BOOL = new BasicType(MYUN_BOOL_NAME);
    public static final BasicType MYUN_INT = new BasicType(MYUN_INT_NAME);
    public static final BasicType MYUN_FLOAT = new BasicType(MYUN_FLOAT_NAME);

    // we assume 64-bit architecture
    public static final String LLVM_BOOL = "i1";
    public static final String LLVM_INT = "i64";
    public static final String LLVM_FLOAT = "double";
}
