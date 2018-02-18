package myun.scope;

/**
 * Small wrapper for LLVM instructions of
 */
public class LLVMInstruction {
    private final String instruction;
    private final boolean needsTypes;
    private final boolean needsClosingParenthesis;

    LLVMInstruction(String instruction, boolean needsTypes, boolean needsClosingParenthesis) {
        this.instruction = instruction;
        this.needsTypes = needsTypes;
        this.needsClosingParenthesis = needsClosingParenthesis;
    }

    public String getInstruction() {
        return instruction;
    }

    public boolean isNeedsTypes() {
        return needsTypes;
    }

    public boolean isNeedsClosingParenthesis() {
        return needsClosingParenthesis;
    }
}
