package myun.compiler;

import myun.AST.*;
import myun.NotImplementedException;
import myun.scope.LLVMInstruction;
import myun.scope.MyunCoreScope;
import myun.scope.TypeNotInferredException;
import myun.type.*;

import java.util.*;
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
class MyunToLLVMTranslator implements ASTExpressionVisitor<String>, ASTNonExpressionVisitor, TypeVisitor<String> {
    // stores the index of the next free register
    private int nextRegister;

    // stores the index of the next free label ID
    private int nextLabelID;

    // stores the previous label
    private String prevLabel;

    // stores the actual llvm code
    private StringBuilder llvmCode;

    // stores a mapping of iteration variables to current registers
    private Map<String, String> itVarMap;

    // stores the label of the current loop exit
    private Stack<String> currentLoopExit;

    // stores a mapping of function headers to actual llvm functions
    private int nextFuncID;
    private Map<FuncHeader, String> funcNames;

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

        // declare IO functions
        llvmCode.append("@.str = private unnamed_addr constant [6 x i8] c\"%lld\\0A\\00\", align 1\n");
        llvmCode.append("@.str.1 = private unnamed_addr constant [7 x i8] c\"%.15e\\0A\\00\", align 1\n");
        // FIXME: add support for procedures that don't return anything and make print a procedure
        // TODO: import statements? so that this header does not get included every time
        llvmCode.append("declare i32 @printf(i8*, ...)\n\n");

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
        prevLabel = "entry"; // functions always start with an entry label
        itVarMap = new HashMap<>();
        currentLoopExit = new Stack<>();
        nextFuncID = 0;
        funcNames = new HashMap<>();
    }

    /**
     * @return returns the name of the next, free register
     */
    private String getNextRegister() {
        nextRegister++;
        // we begin temporary register names with an underscore, because myun does not allow variables to start with an
        // underscore, so there will never be any name collisions
        return "%_" + nextRegister;
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
     * @return the register or constant which holds the result
     */
    private String getConstantOrRegister(ASTExpression expr) {
        String val = expr.accept(this);

        if ((expr instanceof ASTFuncCall) ||
                ((expr instanceof ASTVariable) && expr.getScope().getActualVariable((ASTVariable) expr).isAssignable())) {
            String tmp = getNextRegister();
            llvmCode.append('\t').append(tmp).append(" = ").append(val).append('\n');
            val = tmp;
        }

        return val;
    }

    /**
     * Gets a new register name, and assigns the mapping of the given iteration variable to that register.
     * @param itVar the iteration variable (for-loops)
     * @return the corresponding register
     */
    private String getNewRegisterForItVar(ASTVariable itVar) {
        String newReg = getNextRegister();
        itVarMap.put(itVar.getName(), newReg);
        return newReg;
    }

    /**
     * Retrieves the name of the actual LLVM function for that specific function header.
     * If none has been assigned yet, a new one will be created.
     *
     * @param header the function header
     * @return a unique name
     */
    private String getNameForFunction(FuncHeader header) {
        if (funcNames.containsKey(header)) {
            return funcNames.get(header);
        }
        else {
            nextFuncID++;
            // we begin actual function names with an underscore, because myun does not allow
            // function names to start with an underscore, so there will never be any name collisions
            String realName = "_"+header.getName()+nextFuncID;
            funcNames.put(header, realName);
            return realName;
        }
    }

    @Override
    public void visit(ASTAssignment node) {
        // compute the expression value and type
        String exprVal = getConstantOrRegister(node.getExpr());
        String varName = node.getVariable().getName();
        String type = node.getVariable().getType().accept(this);

        // store the expression in the stack variable
        llvmCode.append("\tstore ").append(type).append(' ').append(exprVal).append(", ").
                append(type).append("* %").append(varName).append('\n');
    }

    @Override
    public String visit(BasicType node) {
        String name = node.getName();
        if (PrimitiveTypes.MYUN_BOOL_NAME.equals(name)) {
            return PrimitiveTypes.LLVM_BOOL;
        }
        if (PrimitiveTypes.MYUN_INT_NAME.equals(name)) {
            return PrimitiveTypes.LLVM_INT;
        }
        if (PrimitiveTypes.MYUN_FLOAT_NAME.equals(name)) {
            return PrimitiveTypes.LLVM_FLOAT;
        }

        throw new ParserException("Type \"" + node.getName() + "\" unknown", new SourcePosition());
    }

    @Override
    public void visit(ASTBlock node) {
        node.getStatements().forEach(stmt -> stmt.accept(this));
        node.getFuncReturn().ifPresent(funcRet -> funcRet.accept(this));
        node.getLoopBreak().ifPresent(loopBreak -> loopBreak.accept(this));
    }

    @Override
    public void visit(ASTBranch node) {
        // suffix for the labels unique for this branch
        int labelID = getNextLabelID();

        llvmCode.append("\tbr label %if").append(labelID).append("_0\n");
        for (int i = 0; i < node.getConditions().size(); i++) {
            String suffix = labelID + "_" + i;
            String nextSuffix = labelID + "_" + (i+1);

            // evaluate the if condition
            llvmCode.append("if").append(suffix).append(":\n");
            String ifCond = getConstantOrRegister(node.getConditions().get(i));

            // compare then jump
            llvmCode.append("\tbr i1 ").append(ifCond);
            llvmCode.append(", label %then").append(suffix);
            llvmCode.append(", label %if").append(nextSuffix).append('\n');

            // block of the current branch
            llvmCode.append("then").append(suffix).append(":\n");
            prevLabel = "then"+suffix;
            node.getBlocks().get(i).accept(this);
            llvmCode.append("\tbr label %ifCont").append(labelID).append('\n');
        }
        String elseSuffix = labelID + "_" + node.getConditions().size();

        // else-block
        llvmCode.append("if").append(elseSuffix).append(":\n");
        prevLabel = "if"+elseSuffix;
        node.getElseBlock().ifPresent(b -> b.accept(this));

        // go on with the control flow
        llvmCode.append("\tbr label %ifCont").append(labelID).append('\n');
        llvmCode.append("ifCont").append(labelID).append(":\n");
        prevLabel = "ifCont" + labelID;
    }

    @Override
    public void visit(ASTCompileUnit node) {
        node.getFuncDefs().forEach(funcDef -> funcDef.accept(this));
        node.getScript().accept(this);
    }

    @Override
    public <CT> String visit(ASTConstant<CT> node) {
        return node.getValue().toString();
    }

    @Override
    public void visit(ASTDeclaration node) {
        // compute the expression value and type
        String exprVal = getConstantOrRegister(node.getExpr());
        String varName = node.getVariable().getName();
        String type = node.getVariable().getType().accept(this);

        // allocate space for this variable on the stack if it is a declaration
        llvmCode.append("\t%").append(varName).append(" = alloca ").append(type).append('\n');

        // initialize the value
        llvmCode.append("\tstore ").append(type).append(' ').append(exprVal).append(", ").
                append(type).append("* %").append(varName).append('\n');
    }

    @Override
    public void visit(ASTForLoop node) {
        String nextIt = getNextRegister();
        String itVar = getNewRegisterForItVar(node.getVariable());
        String itType = node.getVariable().getType().accept(this);
        int labelID = getNextLabelID();

        // start the loop
        llvmCode.append("\tbr label %loop").append(labelID).append('\n');
        llvmCode.append("loop").append(labelID).append(":\n");

        // set var to from
        String fromVal = getConstantOrRegister(node.getFrom());

        // generate the phi instruction for our iteration variable
        llvmCode.append('\t').append(itVar).append(" = phi ").append(itType);
        llvmCode.append(" [").append(fromVal).append(", %").append(prevLabel).append("],");
        llvmCode.append(" [").append(nextIt).append(", %loopIncr").append(labelID).append("]\n");

        // check if i reached the to-value yet
        String toVal = getConstantOrRegister(node.getTo());
        String cmpResult = getNextRegister();
        llvmCode.append('\t').append(cmpResult).append(" = icmp sle ").append(PrimitiveTypes.LLVM_INT);
        llvmCode.append(' ').append(itVar).append(", ").append(toVal).append('\n');

        // jump out of loop when condition reached
        llvmCode.append("\tbr i1 ").append(cmpResult);
        llvmCode.append(", label %loopBody").append(labelID);
        llvmCode.append(", label %loopCont").append(labelID).append('\n');

        // loop body
        llvmCode.append("loopBody").append(labelID).append(":\n");
        prevLabel = "loopBody"+labelID;
        currentLoopExit.push("loopCont"+labelID);
        node.getBlock().accept(this);

        // increment the iteration variable
        llvmCode.append("\tbr label %loopIncr").append(labelID).append('\n');
        llvmCode.append("loopIncr").append(labelID).append(":\n");
        llvmCode.append('\t').append(nextIt).append(" = add ").append(PrimitiveTypes.LLVM_INT).append(" 1, ").append
                (itVar).append('\n');
        llvmCode.append("\tbr label %loop").append(labelID).append('\n');

        // loop end
        llvmCode.append("loopCont").append(labelID).append(":\n");
        prevLabel = "loopCont" + labelID;
        currentLoopExit.pop();
    }

    @Override
    public String visit(ASTFuncCall node) {
        // we need the return type and argument types of the function call
        List<MyunType> argTypes = node.getArgs().stream().map(ASTExpression::getType).collect(Collectors.toList());
        FuncHeader header = new FuncHeader(node.getFunction(), argTypes);
        MyunType retType = node.getScope().getFunctionInfo(header, node.getSourcePosition()).getType().getReturnType();

        // retrieve the appropriate llvm instruction
        StringBuilder callBuilder = new StringBuilder();
        Optional<LLVMInstruction> llvmInstruction = MyunCoreScope.getLLVMInstruction(node.getFunction(), argTypes);
        if (llvmInstruction.isPresent()) {
            callBuilder.append(llvmInstruction.get().getInstruction()).append(' ');
        } else {
            callBuilder.append("call ").append(retType.accept(this)).append(" @").append(getNameForFunction(header)).append('(');
        }

        // go through the function arguments and create code to evaluate the expressions
        for (int i = 0; i < node.getArgs().size(); i++) {
            ASTExpression arg = node.getArgs().get(i);

            // generate code for the argument evaluation if necessary
            String argVal = getConstantOrRegister(arg);

            // for native llvm instructions we do not need to annotate the type
            if (!llvmInstruction.isPresent() || llvmInstruction.get().isNeedsTypes()) {
                callBuilder.append(arg.getType().accept(this));
                callBuilder.append(' ');
            }

            // now actually add the argument to the call
            callBuilder.append(argVal);

            if (i < (node.getArgs().size() - 1)) {
                callBuilder.append(", ");
            }
        }
        if (!llvmInstruction.isPresent() || llvmInstruction.get().isNeedsClosingParenthesis()) {
            callBuilder.append(')');
        }

        // finally, we can return our call instruction
        return callBuilder.toString();
    }

    @Override
    public void visit(ASTFuncDef node) {
        FuncHeader header = new FuncHeader(node.getName(),
                node.getParameters().stream().map(ASTExpression::getType).collect(Collectors.toList()));

        // function definition
        MyunType returnType = node.getReturnType();
        llvmCode.append("define ").append(returnType.accept(this)).append(" @").append(getNameForFunction(header));

        // params
        llvmCode.append('(');
        for (int i = 0; i < node.getParameters().size(); i++) {
            ASTVariable paramVar = node.getParameters().get(i);
            MyunType paramType = paramVar.getType();

            llvmCode.append(paramType.accept(this)).append(' ').append(paramVar.accept(this));

            if (i < (node.getParameters().size() - 1)) {
                llvmCode.append(", ");
            }
        }
        llvmCode.append(") {\n");
        llvmCode.append("entry:\n");
        prevLabel = "entry";

        node.getBlock().accept(this);

        llvmCode.append("\tunreachable\n");
        llvmCode.append("}\n\n");
    }

    @Override
    public void visit(ASTFuncReturn node) {
        MyunType retType = node.getExpr().getType();
        String retVal = getConstantOrRegister(node.getExpr());
        llvmCode.append("\tret ").append(retType.accept(this)).append(' ').append(retVal).append('\n');
    }

    @Override
    public String visit(FuncType node) {
        throw new NotImplementedException("function types", new SourcePosition());
    }

    @Override
    public String visit(UnknownType type) {
        throw new TypeNotInferredException(new SourcePosition());
    }

    @Override
    public String visit(VariantType type) {
        throw new NotImplementedException("variant types", new SourcePosition());
    }

    @Override
    public void visit(ASTLoopBreak node) {
        llvmCode.append("\tbr label %").append(currentLoopExit.peek()).append('\n');
    }

    @Override
    public void visit(ASTScript node) {
        // the script is our main function and always returns 0
        llvmCode.append("define ").append(PrimitiveTypes.LLVM_INT).append(" @main() {\n");
        llvmCode.append("entry:\n");
        prevLabel = "entry";
        node.getBlock().accept(this);
        llvmCode.append("\tret ").append(PrimitiveTypes.LLVM_INT).append(" 0\n");
        llvmCode.append("}\n");
    }

    @Override
    public void visit(ASTProcCall node) {
        // get a dummy register
        String tmp = getNextRegister();
        String callVal = node.getFuncCall().accept(this);

        llvmCode.append('\t').append(tmp).append(" = ").append(callVal).append('\n');
    }

    @Override
    public String visit(ASTVariable node) {
        if (node.isAssignable()) {
            // for mutable variables we need to load the value from the stack
            String type = node.getType().accept(this);
            return "load " + type + ", " +  type + "* %" + node.getName();
        }
        else if (itVarMap.containsKey(node.getName())) {
            // for unmutable register we need to be careful about iteration variables
            // since they can be used several times in different for-loops
            // so we have to return the mapped name, if there is one
            return itVarMap.get(node.getName());
        }
        else {
            // for other unmutable variables we can simply take the register where they are stored
            return '%' +node.getName();
        }
    }

    @Override
    public void visit(ASTWhileLoop node) {
        int labelID = getNextLabelID();

        // start the loop
        llvmCode.append("\tbr label %loop").append(labelID).append('\n');
        llvmCode.append("loop").append(labelID).append(":\n");

        // check the condition
        String condVal = getConstantOrRegister(node.getCondition());
        String cmpResult = getNextRegister();

        llvmCode.append('\t').append(cmpResult).append(" = icmp eq ").append(PrimitiveTypes.LLVM_BOOL);
        llvmCode.append(" true, ").append(condVal).append('\n');

        // jump out of loop when condition reached
        llvmCode.append("\tbr i1 ").append(cmpResult);
        llvmCode.append(", label %loopBody").append(labelID);
        llvmCode.append(", label %loopCont").append(labelID).append('\n');

        // loop body
        llvmCode.append("loopBody").append(labelID).append(":\n");
        prevLabel = "loopBody"+labelID;
        currentLoopExit.push("loopCont"+labelID);
        node.getBlock().accept(this);
        llvmCode.append("\tbr label %loop").append(labelID).append('\n');

        // loop end
        llvmCode.append("loopCont").append(labelID).append(":\n");
        prevLabel = "loopCont" + labelID;
        currentLoopExit.pop();
    }
}
