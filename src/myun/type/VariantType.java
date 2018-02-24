package myun.type;

import java.util.Arrays;
import java.util.List;

/**
 * A type representing multiple possible types.
 */
public class VariantType implements MyunType {
    private final List<MyunType> variants;

    public VariantType(List<MyunType> variants) {
        this.variants = variants;
    }

    public VariantType(MyunType... variants) {
        this.variants = Arrays.asList(variants);
    }

    public List<MyunType> getVariants() {
        return variants;
    }

    @Override
    public boolean isFullyKnown() {
        return variants.stream().allMatch(MyunType::isFullyKnown);
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantType that = (VariantType) o;

        return getVariants().equals(that.getVariants());

    }

    @Override
    public int hashCode() {
        return getVariants().hashCode();
    }
}
