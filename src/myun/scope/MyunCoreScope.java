package myun.scope;

import myun.AST.SourcePosition;
import myun.type.BasicType;
import myun.type.FuncType;
import myun.type.MyunType;
import myun.type.FuncHeader;
import myun.type.PrimitiveTypes;

import java.util.*;

/**
 * Offers the predefined scope with all predefined methods.
 * For example, it contains arithmetic methods, such as plus, minus, etc.
 */
public final class MyunCoreScope extends Scope {
    private static final MyunCoreScope instance = new MyunCoreScope();

    private final Map<FuncHeader, String> llvmInstructions;

    private MyunCoreScope() {
        super(null);
        llvmInstructions = new HashMap<>();
        declarePredefinedFunctions();
    }

    public static MyunCoreScope getInstance() {
        return instance;
    }

    private static FuncType binaryFunction(String param1, String param2, String result) {
        MyunType type1 = new BasicType(param1);
        MyunType type2 = new BasicType(param2);
        MyunType resultType = new BasicType(result);
        List<MyunType> params = new ArrayList<>();
        params.add(type1);
        params.add(type2);
        return new FuncType(params, resultType);
    }

    private static FuncType unaryFunction(String param, String result) {
        MyunType type = new BasicType(param);
        MyunType resultType = new BasicType(result);
        List<MyunType> params = new ArrayList<>();
        params.add(type);
        return new FuncType(params, resultType);
    }

    private void declareAndSetLLVM(String name, FuncType type, String llvm) {
        FuncHeader funcHeader = new FuncHeader(name, type.getParameterTypes());
        declareFunction(funcHeader, type, new SourcePosition());
        llvmInstructions.put(funcHeader, llvm);
    }

    private void declarePredefinedFunctions() {
        declareAndSetLLVM("and",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "and " + PrimitiveTypes.LLVM_BOOL);
        declareAndSetLLVM("or",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "or " + PrimitiveTypes.LLVM_BOOL);
        declareAndSetLLVM("is",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "icmp eq " + PrimitiveTypes.LLVM_BOOL);

        declareAndSetLLVM("is",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp eq " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isLess",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp slt " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isLessEq",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sle " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isGreater",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sgt " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isGreaterEq",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sge " +  PrimitiveTypes.LLVM_INT);

        declareAndSetLLVM("plus",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "add " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("minus",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "sub " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("mult",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "mul " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("div",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "sdiv " + PrimitiveTypes.LLVM_INT);
        // TODO: exp function
        /*declareAndSetLLVM("exp",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "???"); */
        declareAndSetLLVM("mod",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "srem " + PrimitiveTypes.LLVM_INT);

        declareAndSetLLVM("is",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp oeq " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isLess",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp olt " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isLessEq",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp ole " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isGreater",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp ogt " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isGreaterEq",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp oge " + PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVM("plus",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fadd " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("minus",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fsub " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("mult",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fmul " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("div",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fdiv " + PrimitiveTypes.LLVM_FLOAT);
        // TODO: exp function
        /*declareAndSetLLVM("exp",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "???"); */
        declareAndSetLLVM("mod",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "frem " + PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVM("not",
                unaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "xor i1 0, ");

        declareAndSetLLVM("negate",
                unaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "mul " + PrimitiveTypes.LLVM_INT + " -1, ");
        declareAndSetLLVM("negate",
                unaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fmul " + PrimitiveTypes.LLVM_INT + " -1.0, ");
    }

    /**
     * Returns the native LLVM operation for a given function call with certain argument types.
     *
     * @param name     the name of the function
     * @param argTypes the types of the arguments
     * @return the native LLVM instruction for that call or empty if none found
     */
    public static Optional<String> getLLVMOperation(String name, List<MyunType> argTypes) {
        FuncHeader header = new FuncHeader(name, argTypes);
        if (instance.llvmInstructions.containsKey(header)) {
            return Optional.of(instance.llvmInstructions.get(header));
        } else {
            return Optional.empty();
        }
    }
}
