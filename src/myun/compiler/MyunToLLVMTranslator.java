package myun.compiler;

import myun.AST.*;
import myun.scope.MyunCoreScope;
import myun.type.PrimitiveTypes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Translates Myun code to LLVM code.
 * <p>
 * All visit methods write their translated code to the llvmCode string builder.
 * Visit methods for statements return the last computed value.
 * Visit methods for expressions return the line of code that yields the result value when evaluated in LLVM.
 * Visit methods for types simply return their LLVM type name.
 * </p>
 */
class MyunToLLVMTranslator implements ASTVisitor<String> {
    // stores the index of the next free register
    private int nextRegister;

    // stores the index of the next free label ID
    private int nextLabelID;

    // stores the previous label
    private String prevLabel;

    // stores the actual llvm code
    private StringBuilder llvmCode;

    /**
     * Creates a new Myun to LLVM translator.
     */
    MyunToLLVMTranslator() {
        init();
    }

    /**
     * Translates a given myun program to llvm IR code.
     *
     * @param myunProgram the myun program
     * @return llvm IR code as a string
     */
    String translateToLLVM(ASTCompileUnit myunProgram) {
        init();
        myunProgram.accept(this);
        return llvmCode.toString();
    }

    /**
     * Initializes the state of this translator for a new translation process.
     */
    private void init() {
        llvmCode = new StringBuilder();
        nextRegister = 0;
        nextLabelID = 0;
        prevLabel = null;
    }

    /**
     * @return returns the name of the next, free register
     */
    private String getNextRegister() {
        nextRegister++;
        return "%tmp" + nextRegister;
    }

    /**
     * @return the ID of the next free label
     */
    private int getNextLabelID() {
        nextLabelID++;
        return nextLabelID;
    }

    /**
     * Generates code for the given expression and packs it into a new register if necessary.
     *
     * @param expr the expression
     * @return the variable or constant which holds the result
     */
    private String getConstantOrVariableValue(ASTExpression expr) {
        String val = expr.accept(this);

        // since the phi instruction only takes vars or constants, we have to use another register for calls
        if (expr instanceof ASTFuncCall) {
            String tmp = getNextRegister();
            llvmCode.append("\t").append(tmp).append(" = ").append(val).append("\n");
            val = tmp;
        }

        return val;
    }

    @Override
    public String visit(ASTAssignment node) {
        // we assume the variable was allocated already
        // and the ptr to that variable is stored in %var
        String exprVal = getConstantOrVariableValue(node.getExpr());
        String exprType = node.getExpr().getType().
                orElseThrow(() -> new InsufficientPreprocessingException("Expression type unknown.")).
                accept(this);
        String targetVar = node.getVariable().getName();

        // create a store instruction that stores the expression value to the variable ptr on the stack
        llvmCode.append("\tstore ").append(exprType).append(" ").append(exprVal).append(", ");
        llvmCode.append(exprType).append("* %").append(targetVar).append("\n");

        throw new RuntimeException("Not supported yet.");
    }

    @Override
    public String visit(ASTBasicType node) {
        String name = node.getName();
        if (PrimitiveTypes.MYUN_BOOL.equals(name)) {
            return PrimitiveTypes.LLVM_BOOL;
        } else if (PrimitiveTypes.MYUN_INT.equals(name)) {
            return PrimitiveTypes.LLVM_INT;
        } else if (PrimitiveTypes.MYUN_FLOAT.equals(name)) {
            return PrimitiveTypes.LLVM_FLOAT;
        } else {
            throw new RuntimeException("Type unknown.");
        }
    }

    @Override
    public String visit(ASTBlock node) {
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(funcRet -> funcRet.accept(this));
        node.getLoopBreak().ifPresent(loopBreak -> loopBreak.accept(this));

        return null;
    }

    @Override
    public String visit(ASTBranch node) {
        if (node.getConditions().size() != 1) {
            throw new RuntimeException("Elseif blocks not implemented yet.");
        }

        // suffix for the labels unique for this branch
        int labelID = getNextLabelID();

        // evaluate the if condition
        String ifCond = getConstantOrVariableValue(node.getConditions().get(0));

        // compare then jump
        llvmCode.append("\tbr i1 ").append(ifCond);
        llvmCode.append(", label %then").append(labelID);
        llvmCode.append(", label %else").append(labelID).append("\n");

        // if-block
        llvmCode.append("then").append(labelID).append(":\n");
        node.getBlocks().get(0).accept(this);
        llvmCode.append("\tbr label %ifCont").append(labelID).append("\n");

        // else-block
        llvmCode.append("else").append(labelID).append(":\n");
        if (node.hasElse()) {
            node.getElseBlock().accept(this);
        }

        // go on with the control flow
        llvmCode.append("ifCont").append(labelID).append(":\n");
        prevLabel = "ifCont" + labelID;

        return null;
    }

    @Override
    public String visit(ASTCompileUnit node) {
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));
        node.getScript().accept(this);

        return null;
    }

    @Override
    public String visit(ASTConstant node) {
        return node.getValue().toString();
    }

    @Override
    public String visit(ASTForLoop node) {
        String nextIt = getNextRegister();
        String itVar = node.getVariable().accept(this);
        String itType = node.getVariable().getType().orElseThrow(() -> new InsufficientPreprocessingException
                ("Variable type unknown.")).accept(this);
        int labelID = getNextLabelID();

        // loop start
        llvmCode.append("loop").append(labelID).append(":\n");

        // set var to from
        String fromVal = getConstantOrVariableValue(node.getFrom());

        // generate the phi instruction for our iteration variable
        llvmCode.append("\t").append(itVar).append(" = phi ").append(itType);
        llvmCode.append(" [").append(fromVal).append(", %").append(prevLabel).append("],");
        llvmCode.append(" [").append(nextIt).append(", %loopBody").append(labelID).append("]\n");

        // check if i reached the to-value yet
        String toVal = getConstantOrVariableValue(node.getTo());
        String cmpResult = getNextRegister();
        llvmCode.append("\t").append(cmpResult).append(" = icmp sle ").append(PrimitiveTypes.LLVM_INT);
        llvmCode.append(" ").append(itVar).append(", ").append(toVal).append("\n");

        // jump out of loop when condition reached
        llvmCode.append("\tbr i1 ").append(cmpResult);
        llvmCode.append(", label %loopBody").append(labelID);
        llvmCode.append(", label %loopCont").append(labelID).append("\n");

        // loop body
        llvmCode.append("loopBody").append(labelID).append(":\n");
        node.getBlock().accept(this);
        // increment the iteration variable
        llvmCode.append("\t").append(nextIt).append(" = add ").append(PrimitiveTypes.LLVM_INT).append(" 1, ").append
                (itVar).append("\n");
        llvmCode.append("\tbr label %loop").append(labelID).append("\n");

        // loop end
        llvmCode.append("loopCont").append(labelID).append(":\n");
        prevLabel = "loopCont" + labelID;

        return null;
    }

    @Override
    public String visit(ASTFuncCall node) {
        // we need the return type and argument types of the function call
        List<ASTType> argTypes = node.getArgs().stream().
                map(arg -> arg.getType().orElseThrow(() -> new InsufficientPreprocessingException("Argument types not" +
                        " determined."))).
                collect(Collectors.toList());
        ASTType retType = node.getScope().getReturnType(node.getFunction().getName(), argTypes).orElseThrow(() -> new
                InsufficientPreprocessingException("Function return type unknown."));

        // retrieve the appropriate llvm instruction
        StringBuilder callBuilder = new StringBuilder();
        Optional<String> llvmInstruction = MyunCoreScope.getLLVMOperation(node.getFunction().getName(), argTypes,
                retType);
        if (llvmInstruction.isPresent()) {
            callBuilder.append(llvmInstruction.get()).append(" ");
        } else {
            callBuilder.append("call ").append(retType.accept(this)).append(" @").append(node.getFunction().getName()
            ).append("(");
        }

        // go through the function arguments and create code to evaluate the expressions
        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTExpression arg = node.getArgs().get(i);

            // generate code for the argument evaluation if necessary
            String argVal = getConstantOrVariableValue(arg);

            // for native llvm instructions we do not need to annotate the type
            if (!llvmInstruction.isPresent()) {
                callBuilder.append(arg.getType().orElseThrow(() -> new InsufficientPreprocessingException("Argument " +
                        "types not determined.")).accept(this));
                callBuilder.append(" ");
            }

            // now actually add the argument to the call
            callBuilder.append(argVal);

            if (i < node.getArgs().size() - 1) {
                callBuilder.append(", ");
            }
        }
        if (!llvmInstruction.isPresent()) {
            callBuilder.append(")");
        }

        // finally, we can return our call instruction
        return callBuilder.toString();
    }

    @Override
    public String visit(ASTFuncDef node) {
        // function definition
        ASTType returnType = node.getReturnType().orElseThrow(() -> new InsufficientPreprocessingException("Return " +
                "type not determined."));
        llvmCode.append("define ").append(returnType.accept(this)).append(" @").append(node.getName());

        // params
        llvmCode.append("(");
        for (int i = 0; i < node.getParameters().size(); i++) {
            ASTVariable paramVar = node.getParameters().get(i);
            ASTType paramType = paramVar.getScope().getVariableType(paramVar).orElseThrow(() -> new
                    InsufficientPreprocessingException("Parameter type not determined."));

            llvmCode.append(paramType.accept(this)).append(" ").append(paramVar.accept(this));

            if (i < node.getParameters().size() - 1) {
                llvmCode.append(", ");
            }
        }
        llvmCode.append(") {\n");
        llvmCode.append("entry:\n");
        prevLabel = "entry";

        // TODO: allocate space for mutable variables
        node.getBlock().accept(this);

        // FIXME: default values for non-basic data types
        llvmCode.append("\tret ").append(returnType.accept(this)).append(" 0\n");
        llvmCode.append("}\n");

        return null;
    }

    @Override
    public String visit(ASTFuncReturn node) {
        ASTType retType = node.getExpr().getType().orElseThrow(() -> new InsufficientPreprocessingException("Return " +
                "type not determined."));
        String retVal = getConstantOrVariableValue(node.getExpr());
        llvmCode.append("\tret ").append(retType.accept(this)).append(" ").append(retVal).append("\n");
        return null;
    }

    @Override
    public String visit(ASTFuncType node) {
        throw new RuntimeException("Function types not implemented yet.");
    }

    @Override
    public String visit(ASTLoopBreak node) {
        throw new RuntimeException("Loop breaks not implemented yet.");
    }

    @Override
    public String visit(ASTScript node) {
        // scripts are main functions in Myun
        llvmCode.append("define ").append(PrimitiveTypes.LLVM_INT).append(" @main() {\n");

        // generate llvm code for the script content
        node.getBlock().accept(this);

        // return the last evaluated statement
        llvmCode.append("\tret ").append(PrimitiveTypes.LLVM_INT).append(" 0\n");

        // done
        llvmCode.append("}\n");
        return null;
    }

    @Override
    public String visit(ASTVariable node) {
        return "%" + node.getName();
    }

    @Override
    public String visit(ASTWhileLoop node) {
        throw new RuntimeException("While loops not implemented yet.");
    }
}
