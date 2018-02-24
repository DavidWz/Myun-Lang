package myun.type.inference;

import myun.type.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Offers functionality to unify two myun types.
 */
class TypeUnifier {
    /**
     * Unifies two Myun types.
     *
     * @param first the first type
     * @param second the second type
     * @return the unified type or an empty optional if unification was not possible
     */
    static Optional<MyunType> unify(MyunType first, MyunType second) {
        // Forgive me.
        if (first instanceof UnknownType) {
            return Optional.of(second);
        } else if (second instanceof UnknownType) {
            return Optional.of(first);
        } else if (first instanceof VariantType && second instanceof VariantType) {
            return unifyTwoVariants((VariantType) first, (VariantType) second);
        } else if (first instanceof VariantType) {
            return unifyTwoVariants((VariantType) first, new VariantType(second));
        } else if (second instanceof VariantType) {
            return unifyTwoVariants(new VariantType(first), (VariantType) second);
        } else if (first instanceof BasicType && second instanceof BasicType) {
            return unify((BasicType) first, (BasicType) second);
        } else if (first instanceof FuncType && second instanceof FuncType) {
            return unify((FuncType) first, (FuncType) second);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Unifies two lists of types in order.
     * @param first the first list
     * @param second the second list
     * @return a list of unified types or empty if there was one type that could not be unified
     */
    static Optional<List<MyunType>> unify(List<MyunType> first, List<MyunType> second) {
        if (first.size() != second.size()) {
            return Optional.empty();
        }

        List<MyunType> unifiedTypes = new ArrayList<>();
        for (int i = 0; i < first.size(); i++) {
            Optional<MyunType> tmp = unify(first.get(i), second.get(i));
            if (tmp.isPresent()) {
                unifiedTypes.add(tmp.get());
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(unifiedTypes);
    }

    private static Optional<MyunType> unifyTwoVariants(VariantType first, VariantType second) {
        List<MyunType> unifiedVariants = new ArrayList<>();

        // build the intersection of the two variants
        for (int i = 0; i < first.getVariants().size(); i++) {
            MyunType t1 = first.getVariants().get(i);
            for (int j = 0; j < second.getVariants().size(); j++) {
                MyunType t2 = second.getVariants().get(j);
                unify(t1, t2).ifPresent(unifiedVariants::add);
            }
        }

        // if nothing is left, we cannot unify
        if (unifiedVariants.size() == 0) {
            return Optional.empty();
        }
        else {
            return Optional.of(new VariantType(unifiedVariants));
        }
    }


    private static Optional<MyunType> unify(BasicType first, BasicType second) {
        // they must be equal
        if (first.equals(second)) {
            return Optional.of(first);
        }
        else {
            return Optional.empty();
        }
    }

    private static Optional<MyunType> unify(FuncType first, FuncType second) {
        // all parameters and the return type must be unifiable
        if (first.getParameterTypes().size() != second.getParameterTypes().size()) {
            return Optional.empty();
        }

        // unify return type
        Optional<MyunType> tmp = unify(first.getReturnType(), second.getReturnType());
        if (!tmp.isPresent()) {
            return Optional.empty();
        }
        MyunType unifiedReturn = tmp.get();

        // unify param types
        List<MyunType> unifiedParams = new ArrayList<>();
        for (int i = 0; i < first.getParameterTypes().size(); i++) {
            tmp = unify(first.getParameterTypes().get(i), second.getParameterTypes().get(i));
            if (tmp.isPresent()) {
                unifiedParams.add(tmp.get());
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(new FuncType(unifiedParams, unifiedReturn));
    }
}
