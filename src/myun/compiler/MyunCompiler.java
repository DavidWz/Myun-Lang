package myun.compiler;

import myun.AST.*;
import myun.AST.constraints.ViolatedConstraintException;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import myun.type.TypeInferrer;

import java.io.*;

/**
 * Compiles a myun source file to LLVM code.
 */
public class MyunCompiler {
    public MyunCompiler() {
    }

    public void compileFromFile(String inputFile) throws IOException, ViolatedConstraintException {
        // make sure the input file is myun source code
        if (!inputFile.endsWith(".myun")) {
            throw new RuntimeException("Input file is not a myun source file.");
        }

        // generate the ast
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(inputFile);

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
        System.out.println("### Compiled to: ### \n");
        MyunToLLVMTranslator llvmTranslator = new MyunToLLVMTranslator();
        String llvmCode = llvmTranslator.translateToLLVM(program);
        System.out.println(llvmCode);

        // write it to the output file
        String outputFile = inputFile.substring(0, inputFile.length()-1-"myun".length())+".ll";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
        writer.write(llvmCode);
        writer.close();
    }
}
