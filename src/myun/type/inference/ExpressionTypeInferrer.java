package myun.type.inference;

import myun.AST.*;
import myun.type.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Infers the types of Myun expressions.
 */
public class ExpressionTypeInferrer implements ASTExpressionVisitor<MyunType> {
    @Override
    public <CT> MyunType visit(ASTConstant<CT> node) {
        String javaName = node.getValue().getClass().getSimpleName();
        MyunType type;
        switch (javaName) {
            case "Integer":
                type = PrimitiveTypes.MYUN_INT;
                break;
            case "Float":
                type = PrimitiveTypes.MYUN_FLOAT;
                break;
            case "Boolean":
                type = PrimitiveTypes.MYUN_BOOL;
                break;
            default:
                throw new ParserException("Type \"" + javaName + "\" unknown", new SourcePosition());
        }
        node.setType(type);
        return type;
    }

    @Override
    public MyunType visit(ASTFuncCall node) {
        // retrieve all known types for that function
        Collection<FuncType> declaredTypes = node.getScope().getDeclaredFunctionTypes(node.getFunction());

        // collect the parameter types
        List<MyunType> paramTypes = node.getArgs().stream().map(arg -> arg.accept(this)).collect(Collectors.toList());

        // check which functions could be referenced by this call
        List<MyunType> possibleReturnTypes = new ArrayList<>();
        for (FuncType declaredType : declaredTypes) {
            Optional<List<MyunType>> tmp = TypeUnifier.unify(declaredType.getParameterTypes(), paramTypes);
            if (tmp.isPresent()) {
                possibleReturnTypes.add(declaredType.getReturnType());
            }
        }

        // determine the return type
        if (possibleReturnTypes.size() == 0) {
            throw new CouldNotInferTypeException(node);
        }

        MyunType resultType;
        if (possibleReturnTypes.size() == 1) {
            resultType = possibleReturnTypes.get(0);
        }
        else {
            resultType = new VariantType(possibleReturnTypes);
        }

        node.setType(resultType);
        return resultType;
    }

    @Override
    public MyunType visit(ASTVariable node) {
        return node.getType();
    }
}
