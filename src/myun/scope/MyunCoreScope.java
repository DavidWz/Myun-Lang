package myun.scope;

import myun.AST.ASTBasicType;
import myun.AST.ASTFuncType;
import myun.AST.ASTType;
import myun.type.PrimitiveTypes;

import java.util.*;

/**
 * Offers the predefined scope with all predefined methods.
 * For example, it contains arithmetic methods, such as plus, minus, etc.
 */
public class MyunCoreScope extends Scope {
    private static final MyunCoreScope instance = new MyunCoreScope();

    private Map<FuncHeader, String> llvmInstructions;
    
    private MyunCoreScope() {
        super(null);
        llvmInstructions = new HashMap<>();
        declarePredefinedFunctions();
    }

    public static MyunCoreScope getInstance() {
        return instance;
    }

    private ASTFuncType binaryFunction(String param1, String param2, String result) {
        ASTBasicType type1 = new ASTBasicType(-1, -1, param1);
        ASTBasicType type2 = new ASTBasicType(-1, -1, param2);
        ASTBasicType resultType = new ASTBasicType(-1, -1, result);
        List<ASTType> params = new ArrayList<>();
        params.add(type1);
        params.add(type2);
        return new ASTFuncType(-1, -1, params, resultType);
    }

    private ASTFuncType unaryFunction(String param, String result) {
        ASTBasicType type = new ASTBasicType(-1, -1, param);
        ASTBasicType resultType = new ASTBasicType(-1, -1, result);
        List<ASTType> params = new ArrayList<>();
        params.add(type);
        return new ASTFuncType(-1, -1, params, resultType);
    }
    
    private void declareAndSetLLVM(String name, ASTFuncType type, String llvm) {
        declareFunction(name, type);
        llvmInstructions.put(new FuncHeader(name, type), llvm);
    }
    
    private void declarePredefinedFunctions() {
        declareAndSetLLVM("and", binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL), "and "+PrimitiveTypes.LLVM_BOOL);
        declareAndSetLLVM("or", binaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL), "or "+PrimitiveTypes.LLVM_BOOL);

        declareAndSetLLVM("is", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL), "icmp eq "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isLess", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL), "icmp slt "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isLessEq", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL), "icmp sle "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isGreater", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL), "icmp sgt "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("isGreaterEq", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_BOOL), "icmp sge "+PrimitiveTypes.LLVM_INT);

        declareAndSetLLVM("plus", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "add "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("minus", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "sub "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("mult", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "mul "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("div", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "sdiv "+PrimitiveTypes.LLVM_INT);
        declareAndSetLLVM("exp", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "???"); // TODO
        declareAndSetLLVM("mod", binaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "srem "+PrimitiveTypes.LLVM_INT);

        declareAndSetLLVM("is", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL), "fcmp oeq "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isLess", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL), "fcmp oslt "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isLessEq", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL), "fcmp osle "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isGreater", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL), "fcmp osgt "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("isGreaterEq", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_BOOL), "fcmp osge "+PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVM("plus", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "fadd "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("minus", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "fsub "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("mult", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "fmul "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("div", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "fdiv "+PrimitiveTypes.LLVM_FLOAT);
        declareAndSetLLVM("exp", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "???"); // TODO
        declareAndSetLLVM("mod", binaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "frem "+PrimitiveTypes.LLVM_FLOAT);

        declareAndSetLLVM("not", unaryFunction(PrimitiveTypes.MYUN_BOOL, PrimitiveTypes.MYUN_BOOL), "xor i1 0, ");

        declareAndSetLLVM("negate", unaryFunction(PrimitiveTypes.MYUN_INT, PrimitiveTypes.MYUN_INT), "mul "+PrimitiveTypes.LLVM_INT+" -1, ");
        declareAndSetLLVM("negate", unaryFunction(PrimitiveTypes.MYUN_FLOAT, PrimitiveTypes.MYUN_FLOAT), "fmul "+PrimitiveTypes.LLVM_INT+" -1.0, ");
    }

    /**
     * Returns the native LLVM operation for a given function call with certain argument types.
     * @param name the name of the function
     * @param argTypes the types of the arguments
     * @return the native LLVM instruction for that call or empty if none found
     */
    public static Optional<String> getLLVMOperation(String name, List<ASTType> argTypes, ASTType retType) {
        FuncHeader header = new FuncHeader(name, new ASTFuncType(-1, -1, argTypes, retType));
        if (instance.llvmInstructions.containsKey(header)) {
            return Optional.of(instance.llvmInstructions.get(header));
        }
        else {
            return Optional.empty();
        }
    }
}
