package myun.compiler;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import myun.type.TypeInferrer;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Helper class which offers functionality to run Myun code and see the result.
 */
class CodeRunner {
    /**
     * Compiles a string of myun code and returns a string of llvm code.
     *
     * @param code the myun code
     * @return the llvm code
     */
    static String compileStringToString(String code) {
        // generate the ast
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseString(code);

        // init the scopes
        new ScopeInitializer(program, MyunCoreScope.getInstance());

        // infer the types
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        // compile the code
        MyunToLLVMTranslator llvmTranslator = new MyunToLLVMTranslator();
        return llvmTranslator.translateToLLVM(program);
    }

    /**
     * Compiles the given Myun code to a temporary file, executes it, and returns the output.
     *
     * @param myunCode the llvm code
     * @return the result of the execution
     * @throws IOException thrown when a file could not be written
     * @throws InterruptedException thrown when a process is interrupted
     */
    ExecutionResult runMyunCode(String myunCode) throws IOException, InterruptedException {
        // compile myun to llvm code
        String llvmCode = compileStringToString(myunCode);

        // write the given code to a temporary file
        String outputFile = "/tmp/myunllvmtest";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile+".ll"), "utf-8"));
        writer.write(llvmCode);
        writer.close();

        Runtime rt = Runtime.getRuntime();


        // compile the code
        String[] execCodes = {"llc -O0 "+outputFile+".ll",
                "gcc -c "+outputFile+".s -o "+outputFile+".o",
                "gcc "+outputFile+".o -o "+outputFile+".out"};
        for (String execCode : execCodes) {
            Process compileProcess = rt.exec(execCode);
            int compileRet = compileProcess.waitFor();
            if (0 != compileRet) {
                String error = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                throw new UnsuccessfulCompilationException(error);
            }
        }

        // run the code
        Process runProcess = rt.exec("./tmp/"+outputFile+".out");
        int exitStatus = runProcess.waitFor();
        String errors = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()))
                .lines().collect(Collectors.joining("\n"));
        return new ExecutionResult(exitStatus, errors);
    }
}
