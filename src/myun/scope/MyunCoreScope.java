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

    private final Map<FuncHeader, LLVMInstruction> llvmInstructions;

    private MyunCoreScope() {
        super(null);
        llvmInstructions = new HashMap<>();
        declareOperators();
        declareIO();
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

    private void declareAndSetLLVM(String name, FuncType type, LLVMInstruction instr) {
        FuncHeader funcHeader = new FuncHeader(name, type.getParameterTypes());
        declareFunction(funcHeader, type, new SourcePosition());
        llvmInstructions.put(funcHeader, instr);
    }

    private void declareAndSetLLVMOperator(String name, FuncType type, String opCall) {
        LLVMInstruction instr = new LLVMInstruction(opCall, false, false);
        declareAndSetLLVM(name, type, instr);
    }

    private void declareOperators() {
        declareAndSetLLVMOperator("and",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "and " + PrimitiveTypes.LLVM_BOOL);
        declareAndSetLLVMOperator("or",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "or " + PrimitiveTypes.LLVM_BOOL);
        declareAndSetLLVMOperator("is",
                binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "icmp eq " + PrimitiveTypes.LLVM_BOOL);

        declareAndSetLLVMOperator("is",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp eq " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("isLess",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp slt " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("isLessEq",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sle " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("isGreater",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sgt " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("isGreaterEq",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL),
                "icmp sge " + PrimitiveTypes.LLVM_INT);

        declareAndSetLLVMOperator("plus",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "add " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("minus",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "sub " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("mult",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "mul " + PrimitiveTypes.LLVM_INT);
        declareAndSetLLVMOperator("div",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "sdiv " + PrimitiveTypes.LLVM_INT);
        // TODO: exp function
        /*declareAndSetLLVMOperator("exp",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "???"); */
        declareAndSetLLVMOperator("mod",
                binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "srem " + PrimitiveTypes.LLVM_INT);

        declareAndSetLLVMOperator("is",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp oeq " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("isLess",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp olt " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("isLessEq",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp ole " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("isGreater",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp ogt " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("isGreaterEq",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL),
                "fcmp oge " + PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVMOperator("plus",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fadd " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("minus",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fsub " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("mult",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fmul " + PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVMOperator("div",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fdiv " + PrimitiveTypes.LLVM_FLOAT);
        // TODO: exp function
        /*declareAndSetLLVMOperator("exp",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "???"); */
        declareAndSetLLVMOperator("mod",
                binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "frem " + PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVMOperator("not",
                unaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL),
                "xor i1 0, ");

        declareAndSetLLVMOperator("negate",
                unaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                "mul " + PrimitiveTypes.LLVM_INT + " -1, ");
        declareAndSetLLVMOperator("negate",
                unaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                "fmul " + PrimitiveTypes.LLVM_INT + " -1.0, ");
    }

    private void declareIO() {
        // TODO: should get the %.str constant from somewhere dynamically
        String printfInt = "call i32 (i8*, ...) @printf(i8* getelementptr inbounds " +
                "([6 x i8], [6 x i8]* @.str, i32 0, i32 0),";
        declareAndSetLLVM("print",
                unaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT),
                new LLVMInstruction(printfInt, true, true));

        String printfFloat = "call i32 (i8*, ...) @printf(i8* getelementptr inbounds " +
                "([4 x i8], [4 x i8]* @.str.1, i32 0, i32 0),";
        declareAndSetLLVM("print",
                unaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT),
                new LLVMInstruction(printfFloat, true, true));
    }

    /**
     * Returns the native LLVM operation for a given function call with certain argument types.
     *
     * @param name     the name of the function
     * @param argTypes the types of the arguments
     * @return the native LLVM instruction for that call or empty if none found
     */
    public static Optional<LLVMInstruction> getLLVMInstruction(String name, List<MyunType> argTypes) {
        FuncHeader header = new FuncHeader(name, argTypes);
        if (instance.llvmInstructions.containsKey(header)) {
            return Optional.of(instance.llvmInstructions.get(header));
        } else {
            return Optional.empty();
        }
    }
}
