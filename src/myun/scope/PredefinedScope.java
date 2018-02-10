package myun.scope;

import myun.AST.ASTBasicType;
import myun.AST.ASTFuncType;
import myun.AST.ASTType;
import myun.type.PrimitiveTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Offers the predefined scope with all predefined methods.
 * For example, it contains arithmetic methods, such as plus, minus, etc.
 */
public class PredefinedScope {
    private static ASTFuncType binaryFunction(String param1, String param2, String result) {
        ASTBasicType type1 = new ASTBasicType(-1, -1, param1);
        ASTBasicType type2 = new ASTBasicType(-1, -1, param2);
        ASTBasicType resultType = new ASTBasicType(-1, -1, result);
        List<ASTType> params = new ArrayList<>();
        params.add(type1);
        params.add(type2);
        return new ASTFuncType(-1, -1, params, resultType);
    }

    private static ASTFuncType unaryFunction(String param, String result) {
        ASTBasicType type = new ASTBasicType(-1, -1, param);
        ASTBasicType resultType = new ASTBasicType(-1, -1, result);
        List<ASTType> params = new ArrayList<>();
        params.add(type);
        return new ASTFuncType(-1, -1, params, resultType);
    }
    
    public static Scope getPredefinedScope() {
        Scope core = new Scope(null);

        core.declareFunction("and", binaryFunction(PrimitiveTypes.BOOL, PrimitiveTypes.BOOL, PrimitiveTypes.BOOL));
        core.declareFunction("or", binaryFunction(PrimitiveTypes.BOOL, PrimitiveTypes.BOOL, PrimitiveTypes.BOOL));

        core.declareFunction("is", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.BOOL));
        core.declareFunction("isLess", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.BOOL));
        core.declareFunction("isLessEq", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.BOOL));
        core.declareFunction("isGreater", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.BOOL));
        core.declareFunction("isGreaterEq", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.BOOL));

        core.declareFunction("plus", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("minus", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("mult", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("div", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("exp", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("mod", binaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT, PrimitiveTypes.INT));

        core.declareFunction("is", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.BOOL));
        core.declareFunction("isLess", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.BOOL));
        core.declareFunction("isLessEq", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.BOOL));
        core.declareFunction("isGreater", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.BOOL));
        core.declareFunction("isGreaterEq", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.BOOL));

        core.declareFunction("plus", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));
        core.declareFunction("minus", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));
        core.declareFunction("mult", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));
        core.declareFunction("div", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));
        core.declareFunction("exp", binaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));

        core.declareFunction("not", unaryFunction(PrimitiveTypes.BOOL, PrimitiveTypes.BOOL));

        core.declareFunction("negate", unaryFunction(PrimitiveTypes.INT, PrimitiveTypes.INT));
        core.declareFunction("negate", unaryFunction(PrimitiveTypes.FLOAT, PrimitiveTypes.FLOAT));

        return core;
    }
}
