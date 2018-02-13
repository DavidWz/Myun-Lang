package myun.compiler;

import myun.AST.*;
import myun.AST.constraints.ViolatedConstraintException;
import myun.scope.MyunCoreScope;
import myun.scope.Scope;
import myun.scope.ScopeInitializer;
import myun.type.TypeInferrer;

import java.io.*;

/**
 * Compiles a myun source file to LLVM code.
 */
public class MyunCompiler {
    public MyunCompiler() {
    }

    public void compileFromFile(String fileName) throws IOException, ViolatedConstraintException {
        // generate the ast
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(fileName);

        // init the scopes
        ScopeInitializer scopeInitializer = new ScopeInitializer();
        scopeInitializer.initScope(program, MyunCoreScope.getInstance());

        // infer the types
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        // print the source code
        MyunPrettyPrinter prettyPrinter = new MyunPrettyPrinter();
        System.out.println(prettyPrinter.toString(program));

        // compile the code
        System.out.println("### Compiling:");
        MyunToLLVMTranslator llvmTranslator = new MyunToLLVMTranslator();
        String llvmCode = llvmTranslator.translateToLLVM(program);
        System.out.println(llvmCode);

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+".ll"), "utf-8"));
        writer.write(llvmCode);
        writer.close();
    }
}
