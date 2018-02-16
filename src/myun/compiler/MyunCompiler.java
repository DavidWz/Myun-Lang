package myun.compiler;

import myun.AST.*;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import myun.type.TypeInferrer;

import java.io.*;

/**
 * Compiles a myun source file to LLVM code.
 * @noinspection UseOfSystemOutOrSystemErr
 */
public class MyunCompiler {

    /**
     * Compiles myun code from a file and writes the resulting llvm ir code to an output file.
     *
     * @param inputFile the path to the input file
     * @throws IOException thrown when the file could not be loaded or written to
     */
    public void compileFromFile(String inputFile) throws IOException {
        // make sure the input file is myun source code
        if (!inputFile.endsWith(".myun")) {
            throw new RuntimeException("Input file is not a myun source file.");
        }

        // generate the ast
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(inputFile);

        // init the scopes
        new ScopeInitializer(program, MyunCoreScope.getInstance());

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
